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

    public List<ArrivalListItem> getListItem(String arrivalListId)
    {
    	return itemDao.findByArrivalListId(arrivalListId);
    }
    
    public Boolean submitInstallItem(String itemId, Boolean isFinish, Boolean isGeneratePmOrder) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.submitArrivalListItem(token, itemId, isFinish);

        if (res.contains("successfully")) {
            if (isFinish) {
                ArrivalListItem temp = itemDao.findById(itemId);
                AssetInfo newAsset = assetDao.getByUid(temp.getAssetUID());
                if (!Strings.isNullOrEmpty(newAsset.getQrCode())) {
                    qrCodeService.updateQrCodeStatus(newAsset.getQrCode(), 3);
                }
                assetDepreciationService.saveAssetDerpeciation(newAsset);
                if (isGeneratePmOrder) {
                    pmService.createPMrequest(newAsset.getUid(), null);
                }
                List<V2_BlobObject> assetPicList = Optional.ofNullable(getPicList(itemId)).orElse(new ArrayList<V2_BlobObject>())
                        .stream().filter(item -> "asset".equals(item.getBoSubType())).collect(Collectors.toList());
                assetPicList.forEach(item -> {
                    item.setId(null);
                    item.setBoUuid(newAsset.getUid());
                    item.setBoId(newAsset.getId());
                    item.setBoSubType("1");
                    item.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                });
                blobDao.save(assetPicList);
            }
            return true;
        } else {
            return false;
        }
    }

    public void saveOrUpdateItem(ArrivalListItem arrivalListItem) {
        arrivalListItem.setLastModifiedDate(new Date());
        arrivalListItem.setUsageStatus(3);
        itemDao.save(arrivalListItem);
        if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
            qrCodeService.updateQrCodeStatus(arrivalListItem.getQrCode(), 2);
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

}
