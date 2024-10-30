/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.view.sysutil.UserContextService;

import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.google.common.base.Strings;

import java.util.*;
import java.util.stream.Collectors;
import javaslang.Tuple2;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import com.ge.apm.service.common.WechatSendMsgService;

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
    protected BlobFileService blobService;

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
    @Autowired
    private ContractInfoItemRepository contractItemDao;
    @Autowired
    private WechatSendMsgService wechatSendMsgService;
    @Autowired
    private TenantInfoRepository tenantInfoDao;


    public List<Supplier> getOnlySuplierListBySiteUid(String tenantUid) {
        return supplierDao.findSupplierByTenantUID(tenantUid);
    }
    public List<Supplier> getOnlyMaintainListBySiteUid(String tenantUid) {
        return supplierDao.findMaintainByTenantUID(tenantUid);
    }
    public List<Supplier> getOnlyManuListBySiteUid(String tenantUid) {
        return supplierDao.findManuByTenantUID(tenantUid);
    }

    public Boolean submitArrivalList(String srrivalListId) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.submitArrivalList(token, srrivalListId);
        return res.contains("successfully");
    }

    public List<ArrivalListItem> getListItem(String arrivalListId) {
        return itemDao.findByArrivalListId(arrivalListId);
    }

    public boolean assetIsInvalid(String assetUid) {
        AssetInfo assetInfo = assetDao.findByUid(assetUid);
        if (assetInfo == null) {
            return true;
        }
        if (assetInfo != null) {
            if (assetInfo.getStatus().equals(4)) {
                return true;
            }
            if (assetInfo.getIsDeleted()) {
                return true;
            }
        }
        return false;
    }

    public void deleteListItem(String arrivalListId, String siteUid) {
        List<ArrivalListItem> listItem = getListItem(arrivalListId);
        if (listItem != null) {
            for (ArrivalListItem item : listItem) {
                if (StringUtils.isNotEmpty(item.getQrCode())) {
                    deleteQrCode(item.getQrCode(), siteUid);
                }
                ContractInfoItem contractItem = contractItemDao.getByArrivalItemId(item.getId());
                if (null != contractItem) {
                    contractItemDao.delete(contractItem);
                }
            }
            itemDao.delete(listItem);
        }
    }

    public List<AssetInfo> deleteListItemUnLimited(String arrivalListId, String siteUid) {
        List<AssetInfo> validList = new ArrayList<>();
        List<ArrivalListItem> listItem = getListItem(arrivalListId);
        if (listItem != null) {
            for (ArrivalListItem item : listItem) {
                if (StringUtils.isNotEmpty(item.getQrCode())) {
                    List<AssetInfo> result = deleteQrCodeAndAsset(item.getQrCode(), siteUid);
                    if (result.size() > 0) {
                        result.forEach(val -> validList.add(val));
                        continue;
                    }
                }
            }
            if (validList.size() > 0) {
                return validList;
            }
            itemDao.delete(listItem);
        }
        return new ArrayList<>();
    }

    public void deleteBlobObject(String arrivalListId) {
        List<V2_BlobObject> listObj = blobDao.findByBoUuid(arrivalListId);
        if (listObj != null) {
            for (V2_BlobObject obj : listObj) {
                blobService.deleteBlobFile(obj);
            }
        }
    }

    private void deleteQrCode(String qrCode, String siteUid) {
        qrCodeService.deleteQrcode(qrCode, siteUid);
    }

    private List<AssetInfo> deleteQrCodeAndAsset(String qrCode, String siteUid) {
        return qrCodeService.deleteQrcodeAndAsset(qrCode, siteUid);
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
                    qrCodeService.updateQrCodeStatus(newAsset.getQrCode(), newAsset.getSiteUID(), 3);
                }
                assetDepreciationService.saveAssetDerpeciation(newAsset);
                if (isGeneratePmOrder) {
                    pmService.createPMrequest(newAsset.getUid(), null);
                }
                List<V2_BlobObject> assetPicList = Optional.ofNullable(getPicList(itemId)).orElse(new ArrayList<V2_BlobObject>())
                        .stream().filter(item -> item.getBoSubType().contains("asset") || item.getBoSubType().contains("business") || item.getBoSubType().contains("clinical") || item.getBoSubType().contains("technical") || item.getBoSubType().contains("fixasset") || item.getBoSubType().contains("CustomizedAssetAttachmentCategory")).collect(Collectors.toList());
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
                        case "asset4":
                        case "business":
                        case "clinical":
                        case "technical":
                        case "fixasset":
                            item.setBoSubType("4");
                            break;
                        default:
                    }

/*                    // See: YHAAO-2346: ignore verification & other document.
                    if ("4".equals(item.getBoSubType()) && V2_BlobObject.BO_TYPE_ArrivalList.equals(item.getBoType())) {
                        return;
                    }*/

                    item.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                });
                blobDao.save(assetPicList);
                if (!userContextService.needInstorageCheck()) {
                    temp.setInstallStatus(ArrivalListItem.INSTALL_STATUS_INSTORAGE);
                    itemDao.save(temp);
                }

                ContractInfoItem contractItem = contractItemDao.getByArrivalItemId(temp.getId());
                if (null != contractItem) {
                    contractItem.setAssetUid(temp.getAssetUID());
                    ContractInfoItem tempContractItem = contractItemDao.save(contractItem);
                    this.updateAssetParent(tempContractItem, newAsset);
                }
            } else {
                //after reject;
                ArrivalListItem temp = itemDao.findById(itemId);
                if (!Strings.isNullOrEmpty(temp.getQrCode())) {
                    qrCodeService.releaseQrCode(temp.getQrCode(), temp.getArrivalList().getSiteUID());
                }
                temp.setQrCode(null);
                itemDao.save(temp);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean saveOrUpdateItem(ArrivalListItem arrivalListItem) {
//        arrivalListItem.setLastModifiedDate(new Date());
        try {
            itemDao.save(arrivalListItem);
            if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
                qrCodeService.updateQrCodeStatus(arrivalListItem.getQrCode(), arrivalListItem.getArrivalList().getSiteUID(), 5);
            }
            return true;
        } catch (ObjectOptimisticLockingFailureException e) {
            WebUtil.addErrorMessage("保存失败,数据已经被修改!");
        }
        return false;

    }

    public Boolean reAssignWorkOrder(ArrivalListItem selectedItem) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.reAssignOrderOwner(token, selectedItem.getId(), selectedItem.getCurrentPersonUID());
        return res.contains("successfully");
    }

    public List<V2_BlobObject> getPicList(String itemId) {
        return blobDao.findByBoUuid(itemId);
    }

    public List<V2_BlobObject> getPartPictures(Set<String> ids){
        return blobDao.getPartPictures(ids);
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
        return ucService.getWorkerList(0, "");
    }

    public List<UserAccount> getClinicalOwnerList(Collection<String> clinicalDeptUidList) {
        clinicalDeptUidList = clinicalDeptUidList.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(clinicalDeptUidList)) {
            return null;
        }
        List<UserAccount> userlist = userDao.findByOrgInfoUIDIn(clinicalDeptUidList);
        return userlist.stream().filter(item -> item.getUserRoleList() != null && !item.getUserRoleList().stream()
                .anyMatch(roleItem -> roleItem.getRole().getName().equals("Guest"))).collect(Collectors.toList());
    }

    public Supplier getSupplierById(Integer supplierId) {
        return Optional.ofNullable(supplierDao.findById(supplierId)).orElse(null);
    }

    public Supplier getSupplierByUid(String supplierUid){
        return supplierDao.getByUid(supplierUid);
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
        departNumFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, item.getArrivalList().getTenantUID()));
        List<AssetInfo> assetList = assetDao.findBySearchFilter(departNumFilters);
        if (null != assetList && assetList.size() > 0) {
            return new Tuple2(false, "设备编号已经使用");
        }
        departNumFilters.clear();
        departNumFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, item.getDepartNum()));
        departNumFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.EQ, item.getArrivalList().getTenantUID()));
        List<ArrivalListItem> itemList = itemDao.findBySearchFilter(departNumFilters);
        if (null == itemList || itemList.isEmpty()) {
            return new Tuple2(true, "");
        }
        //处理指定的租户对设备编号不验证
        String currentTenantUid = UserContextService.getCurrentUserAccount().getTenantUID();
        List<TenantInfo> tanentIfos = tenantInfoDao.findByCustomTenant("3");
        String tanentInfoUid = tanentIfos.get(0).getUid();
        if(tanentInfoUid.equals(currentTenantUid)){
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
        return supplierDao.findSupplierByTenantUIDAndStatus(UserContextService.getCurrentUserAccount().getTenantUID());
        //return supplierDao.findBySupplierTAndTenantUID(Boolean.TRUE, UserContextService.getCurrentUserAccount().getTenantUID());
        //return supplierDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
//        if(Strings.isNullOrEmpty(siteUID)){
//            return supplierDao.getByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
//        }else{
//            return supplierDao.getBySiteUID(siteUID);
//        }
    }

    public Map<String, Object> getArrivalExportData(String arrivalListId){

        Map<String, Object> arrivalMap = new HashMap<>();

        List<ArrivalListItem> arrivalListItemList = itemDao.findByArrivalListId(arrivalListId);
        for (ArrivalListItem arrivalListItem : arrivalListItemList) {
            arrivalMap.put("开箱安装日期", arrivalListItem.getArrivalList().getBoxInstallationDate());
            arrivalMap.put("使用日期", arrivalListItem.getArrivalList().getUsingDate());
            arrivalMap.put("使用人名称", arrivalListItem.getArrivalList().getUsingPerson());
            arrivalMap.put("使用人电话", arrivalListItem.getArrivalList().getUsingPersonTel());
            arrivalMap.put("联系人", arrivalListItem.getArrivalList().getContactPerson());
            arrivalMap.put("联系人电话", arrivalListItem.getArrivalList().getContactPersonTel());
            arrivalMap.put("维修联系人", arrivalListItem.getArrivalList().getRepairer());
            arrivalMap.put("维修联系人电话", arrivalListItem.getArrivalList().getRepairerTel());
            arrivalMap.put("培训资料", arrivalListItem.getArrivalList().getTrainingInfo());

            arrivalMap.put("设备名称", arrivalListItem.getArrivalList().getAssetName());
            arrivalMap.put("设备分类", arrivalListItem.getArrivalList().getAssetGroup());
            arrivalMap.put("规格型号", arrivalListItem.getArrivalList().getFunctionType());
            arrivalMap.put("设备编号", arrivalListItem.getDepartNum());
            arrivalMap.put("安置地点", arrivalListItem.getLocationName());
            arrivalMap.put("制造商序列号", arrivalListItem.getSerialNum());
            arrivalMap.put("安装工时", arrivalListItem.getWorkHours());
        }

        return arrivalMap;
    }

    public Boolean sendArrivalSubmitMsg(String arrivalListId){
        String token = UserContextService.getAccessToken();
        String appPath = "pages/index/index?arrivalListId=" + arrivalListId;
        String appName = "hc-apm-supplier";
        String template = "un_take_task_template_id";

        Map<String, Object> map = new HashMap<>();
        map.put("appPath", appPath);
        map.put("appName", appName);
        map.put("template", template);

        return wechatSendMsgService.sendArrivalSubmitMsg(token, map);
    }

    //处理合同中设置了主设备关系的业务逻辑
    public void updateAssetParent(ContractInfoItem tempContractItem, AssetInfo currentAsset){

        if(StringUtils.isEmpty(tempContractItem.getParentId())){
            List<ContractInfoItem> tempSourceContractInfoItemList = contractItemDao.findByParentIdAndContractId(tempContractItem.getPurchaseItemId(), tempContractItem.getContractId());
            for (ContractInfoItem sourceContractInfoItem : tempSourceContractInfoItemList) {
                List<ContractInfoItem> tempContractInfoItemList = contractItemDao.findByPurchaseItemIdAndContractId(sourceContractInfoItem.getId(), tempContractItem.getContractId());
                for (ContractInfoItem contractInfoItem : tempContractInfoItemList) {
                    if(StringUtils.isNotEmpty(contractInfoItem.getAssetUid())){
                        AssetInfo tempAsset = assetDao.findByUid(contractInfoItem.getAssetUid());
                        tempAsset.setParentAssetId(currentAsset.getId());
                        assetDao.save(tempAsset);
                    }
                }
            }
        }else {
            List<ContractInfoItem> tempParentContractInfoItemList = contractItemDao.findByPurchaseItemIdAndContractId(tempContractItem.getParentId(), tempContractItem.getContractId());
            if(!tempParentContractInfoItemList.isEmpty() && tempParentContractInfoItemList.size() == 1){
                ContractInfoItem tempParentContractInfoItem = tempParentContractInfoItemList.get(0);
                AssetInfo tempParentAsset = assetDao.findByUid(tempParentContractInfoItem.getAssetUid());
                if(tempParentAsset != null){
                    currentAsset.setParentAssetId(tempParentAsset.getId());
                    assetDao.save(currentAsset);
                }
            }
        }

    }

    public void updateAssetInfo(ArrivalListItem item){
        AssetInfo assetInfo = assetDao.getByUid(item.getAssetUID());
        assetInfo.setName(item.getArrivalList().getAssetName());
        assetInfo.setFunctionType(item.getFunctionType());
        assetInfo.setImportEquipment(item.getImportEquipment());
        assetInfo.setWarrantyMonth(item.getWarrantyMonth());
        assetInfo.setAnnualMaintenanceTimes(item.getAnnualMaintenanceTimes());
        assetInfo.setSerialNum(item.getSerialNum());
        assetInfo.setLocationName(item.getLocationName());
        assetDao.save(assetInfo);
    }
}
