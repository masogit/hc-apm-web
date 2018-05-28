/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.GbCodeRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.GbCode;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javaslang.Tuple2;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class AssetInstallationService {

    @Autowired
    AssetInstallationApiService installApiService;

    @Autowired
    BlobObjectRepository blobDao;

    @Autowired
    SupplierRepository supplierDao;

    @Autowired
    OrgInfoRepository orgDao;

    @Autowired
    UserAccountRepository userDao;

    @Autowired
    private UserCommonService ucService;

    @Autowired
    private ArrivalListItemRepository itemDao;

    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private AssetDepreciationService assetDepreciationService;
    @Autowired
    private V2_PmOrderService pmService;

    public Boolean submitArrivalList(String srrivalListId) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.submitArrivalList(token, srrivalListId);
        return res.contains("successfully");
    }

    public List<ArrivalListItem> getListItem(String arrivalListId) {
        return itemDao.findByArrivalListId(arrivalListId);
    }
    
    public void deleteListItem(String arrivalListId,String siteUid){
    	List<ArrivalListItem> listItem=getListItem(arrivalListId);
    	if(listItem!=null){
    		for(ArrivalListItem item: listItem){
    			if(StringUtils.isNotEmpty(item.getQrCode())){
    				deleteQrCode(item.getQrCode(), siteUid);
    			}
    		}
    		itemDao.delete(listItem);
    	}
    }
    public void deleteBlobObject(String arrivalListId){
    	List<V2_BlobObject> listObj=blobDao.findByBoUuid(arrivalListId);
    	if(listObj!=null){
    		blobDao.delete(listObj);
    	}
    }
    private void deleteQrCode(String qrCode,String siteUid){
    	qrCodeService.deleteQrcode(qrCode, siteUid);
    }

    public Boolean submitInstallItem(String itemId, Boolean isFinish, Boolean isGeneratePmOrder) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.submitArrivalListItem(token, itemId, isFinish);

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if (res.contains("successfully")) {
            if (isFinish) {
                ArrivalListItem temp = itemDao.findById(itemId);
                AssetInfo newAsset = assetDao.getByUid(temp.getAssetUID());
                if (!Strings.isNullOrEmpty(newAsset.getQrCode())) {
                    qrCodeService.updateQrCodeStatus(newAsset.getQrCode(),newAsset.getSiteUID(), 3);
                }
                assetDepreciationService.saveAssetDerpeciation(newAsset);
                if (isGeneratePmOrder) {
                    pmService.createPMrequest(newAsset.getUid(), null);
                }
                List<V2_BlobObject> assetPicList = Optional.ofNullable(getPicList(itemId)).orElse(new ArrayList<V2_BlobObject>())
                        .stream().filter(item -> item.getBoSubType().contains("asset")).collect(Collectors.toList());
                assetPicList.addAll(getPicList(temp.getArrivalListId()));
                assetPicList.forEach(item -> {
                    item.setId(null);
                    item.setBoUuid(newAsset.getUid());
                    item.setBoId(newAsset.getId());
                    switch (item.getBoSubType()) {
                        case "asset":
                            item.setBoSubType("1");
                            break;
                        case "asset2":
                            item.setBoSubType("2");
                            break;
                        case "asset3":
                            item.setBoSubType("3");
                            break;
                        default:
                    }

                    item.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                });
                blobDao.save(assetPicList);
                if (!userContextService.needInstorageCheck()) {
                    temp.setInstallStatus(ArrivalListItem.INSTALL_STATUS_INSTORAGE);
                    itemDao.save(temp);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void saveOrUpdateItem(ArrivalListItem arrivalListItem) {
        arrivalListItem.setLastModifiedDate(new Date());
        itemDao.save(arrivalListItem);
        if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
            qrCodeService.updateQrCodeStatus(arrivalListItem.getQrCode(),arrivalListItem.getArrivalList().getSiteUID(), 2);
        }

    }

    public Boolean reAssignWorkOrder(ArrivalListItem selectedItem) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.reAssignOrderOwner(token, selectedItem.getId(), selectedItem.getCurrentPersonUID());
        return res.contains("successfully");
    }

    public List<V2_BlobObject> getPicList(String itemId) {
        return blobDao.findByBoUuid(itemId);
    }

    public OrgInfo getOrgInfoByUid(String siteUID) {
        return orgDao.getByUid(siteUID);
    }

    public UserAccount getUserAccountByUid(String userUid) {
        return userDao.getByUid(userUid);
    }

    public UserAccount getUserAccountById(Integer userId) {
        return userDao.getById(userId);
    }

    public List<UserAccount> getWorkerList() {
        return ucService.getWorkerList(0);
    }

    public List<UserAccount> getClinicalOwnerList(String clinicalDeptUid) {
        if (Strings.isNullOrEmpty(clinicalDeptUid)) {
            return null;
        }
        List<UserAccount> userlist = userDao.findByOrgInfoUID(clinicalDeptUid);
        return userlist.stream().filter(item -> item.getUserRoleList() != null && !item.getUserRoleList().stream()
                .anyMatch(roleItem -> roleItem.getRole().getName().equals("Guest"))).collect(Collectors.toList());
    }

    public Supplier getSupplierById(Integer supplierId) {
        return Optional.ofNullable(supplierDao.findById(supplierId)).orElse(null);
    }

    public QrCodeLib getNewQrcode(String tenantUid, String siteUid) {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(tenantUid, siteUid, 1);
        return qrcodeLib;
    }

    public Tuple2<Boolean, String> isValidDeptNum(ArrivalListItem item) {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if (!userContextService.needInstorageCheck()) {
            return new Tuple2(true, "");
        }

        if (Strings.isNullOrEmpty(item.getDepartNum())) {
            return new Tuple2(false, "设备编号不可为空");
        }
        List<SearchFilter> departNumFilters = new ArrayList<>();
        departNumFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, item.getDepartNum()));
        departNumFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, item.getArrivalList().getSiteUID()));
        List<AssetInfo> assetList = assetDao.findBySearchFilter(departNumFilters);
        if (null != assetList && assetList.size() > 0) {
            return new Tuple2(false, "设备编号已经使用");
        }
        departNumFilters.clear();
        departNumFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, item.getDepartNum()));
        departNumFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.EQ, item.getArrivalList().getSiteUID()));
        List<ArrivalListItem> itemList = itemDao.findBySearchFilter(departNumFilters);
        if (null == itemList || itemList.isEmpty()) {
            return new Tuple2(true, "");
        }

        if (itemList.size() > 1) {
            return new Tuple2(false, "设备编号已被分配");
        }
        if (itemList.get(0).getId().equals(item.getId())) {
            return new Tuple2(true, "");
        }
        return new Tuple2(false, "设备编号已被分配");
    }

    public List<Supplier> getSupplierListBySiteUid(String siteUID) {
        return supplierDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
//        if(Strings.isNullOrEmpty(siteUID)){
//            return supplierDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
//        }else{
//            return supplierDao.getBySiteUID(siteUID);
//        }
    }

}
