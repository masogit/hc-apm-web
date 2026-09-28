/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetTypeController extends GenericCRUDUUIDController<SharedAssetType>  {

    private UaaService uuaService;
    private BlobFileService blobFileService;
    private AssetInfoMgrService assetInfoMgrService;
    SharedAssetTypeRepository dao = null;
    AssetInfoRepository assetInfoDao = null;
    OrgInfoRepository orgInfoDao = null;
    SharedAssetReqDetailRepository sharedAssetReqDetailRepository = null;
    private String operation;
    private String createDateFormatTime = null;
    private ConfigItemValueRepository configItemValueRepository;
    private String filterUnitPrice;
    private Double filterUnitPriceFrom;
    private Double filterUnitPriceTo;
    @Setter@Getter
    private List<V2_BlobObject> attachements;
    //如果无图片 就显示请上传图片
    private boolean uploadFlag;
    //是否启用无人值守 true：启动
    private Boolean whethertoenableunattended;
    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        assetInfoMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        dao = WebUtil.getBean(SharedAssetTypeRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        sharedAssetReqDetailRepository = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(SharedAssetTypeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {

            setSelected(dao.findById((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }

    @Override
    protected SharedAssetTypeRepository getDAO() {
        return dao;
    }

    public String getViewPage(String id, String actionName) {
        operation = "Edit.xhtml?actionName=Edit&selectedid=" + "1";
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void getViewPageA(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
    }

    public String getViewPageN(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void onSelectSwapType() throws IOException {
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    @Override
    protected Page<SharedAssetType> loadData(PageRequest pageRequest) {
        setUserSearchFilters();

        this.removeFilterOnField("unitPrice");

        if (StringUtils.isNotEmpty(this.getFilterUnitPrice())) {
            String[] split = this.getFilterUnitPrice().split("~");
            searchFilters.add(new SearchFilter("unitPrice", SearchFilter.Operator.GTE, split[0]));
            if (split.length == 2) {
                if (!split[1].equals(""))
                    searchFilters.add(new SearchFilter("unitPrice", SearchFilter.Operator.LTE, split[1]));
            }
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    public List<V2_BlobObject> getAttachList(String id) {
        if (attachements == null) {
            attachements = blobFileService.findBlobObjectsByBoUid(id);
        }
        uploadFlag = attachements.size() == 0;
        return attachements;
    }

    public void removeAttachmentById(String blobId) {
        blobFileService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }
    public boolean getUploadFlag() {
        return uploadFlag;
    }
    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobFileService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ADJUST_EQUIPMENT);
            blobObject.setBoUuid(selected.getId());
           blobFileService.saveBlobObject(blobObject);
           WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public SharedAssetType getAccessoriesById(String uid) {
        return dao.findById(uid);
    }

    @Override
    public List<SharedAssetType> getItemList() {
        return dao.find();
    }

    @Override
    public void onBeforeSave(SharedAssetType object) {
        object.setAccessoryList(object.getAccessoryList());
    }

    @Override
    public void onBeforeNewObject(SharedAssetType object) {
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setCreateBy(UserContextService.getCurrentUserAccount().getName());
        object.setCreateDate(new Date());
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        if(selectSite != null){
            object.setSiteUID(selectSite.getUid());
        }
    }

    @Override
    public void delete() {
        List<String> assetIds = dao.findAssetIsExist(selected.getId());
        if (assetIds.isEmpty()) {  //无设备绑定，可直接删除
            dao.delete(selected);
            WebUtil.addSuccessMessage("已成功删除: " + selected.getSwapName());
            selected = null;
        } else {
            List<AssetInfo> assetInfos = assetInfoDao.findBySharedAssetTypeUid(assetIds);
            AtomicBoolean isBorrowing = new AtomicBoolean(false);
            assetInfos.forEach(assetInfo -> { if(assetInfoMgrService.isInDispenceCenter(assetInfo)) isBorrowing.set(true); });
            if(isBorrowing.get()) {
                WebUtil.addErrorMessage("该调剂类型下有设备正在借出,无法删除！");
                return;
            } else {
                RequestContext.getCurrentInstance().addCallbackParam("showDeleteDlg", true);
            }
        }
    }

    public void deleteAgain() {
        assetInfoDao.clearShareAssetTypeByIds(dao.findAssetIsExist(selected.getId()));
        dao.delete(selected);
        RequestContext.getCurrentInstance().addCallbackParam("showDeleteDlg", false);
        WebUtil.addSuccessMessage("已成功删除: " + selected.getSwapName());
        selected = null;
    }

    //有设备绑定且无在借设备时候，隐藏confirm
    public Boolean hideConfirmBeforeDelete() {
        if(selected==null) return false;
        List<String> assetIds = dao.findAssetIsExist(selected.getId());
        if(!assetIds.isEmpty()) {
            List<AssetInfo> assetInfos = assetInfoDao.findBySharedAssetTypeUid(assetIds);
            AtomicBoolean isBorrowing = new AtomicBoolean(false);
            assetInfos.forEach(assetInfo -> { if(assetInfoMgrService.isInDispenceCenter(assetInfo)) isBorrowing.set(true); });

            if(!isBorrowing.get()) return true;
        }
        return false;
    }


    //根据登录人信息配置权限
    private void setUserSearchFilters() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        /* JIRA#3445
         * Since access control for shared_asset_type is in site_uid level, so retrieve site_uid from current user for access check
         * List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
         */
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        List<String> orgUidList = new ArrayList<>();
        if(selectSite != null){
            orgUidList= orgInfoDao.getOrgInfoUidsByparentUid(selectSite.getUid());
        }else{
            orgUidList.add(user.getSiteUID());
        }
        updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
    }

    public List<SharedAssetType> getSwapNameList() {
        List<SharedAssetType> sharedAssetTypes = dao.findbySiteUid(UserContextService.getCurrentUserAccount().getSiteUID());
        return sharedAssetTypes == null ? new ArrayList<>() : sharedAssetTypes;
    }

    public String getSharedAssetType(String id) {
        if (id != null) {
            SharedAssetType object = dao.findById(id);
            if (object != null) {
                return object.getSwapName();
            } else {
                return null;
            }
        }
        return null;
    }

    public List<SharedAssetType> getSwapNameReturnList() {
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);

        List<SharedAssetType> objectList;
        if (Objects.equals(userOrg.getOrgType(), 4)) {
            objectList = dao.findbyClinicalDeptUid(UserContextService.getCurrentUserAccount().getSiteUID(), userOrg.getUid());
        } else {
            objectList = dao.findbySiteUid(UserContextService.getCurrentUserAccount().getSiteUID());
        }
        return objectList == null ? new ArrayList<>() : objectList;
    }

    public int getSwapQty(String Uid, String clinicalDeptUid) {
        if (null == Uid || Uid.equalsIgnoreCase("")) {
            return 0;
        } else {
            List<AssetInfo> objectList = dao.findbyBorrowQty(clinicalDeptUid, Uid);
            return objectList.size();
        }
    }


    public void onChanged() {
        SharedAssetTypeController swapTypeController = WebUtil.getBean(SharedAssetTypeController.class);
        if (swapTypeController.getSelected() != null) {

        }

    }

    private SharedAssetTypeAccesssory selectedAccessory;

    public SharedAssetTypeAccesssory getSelectedAccessory() {
        return selectedAccessory;
    }

    public void setSelectedAccessory(SharedAssetTypeAccesssory selectedAccessory) {
        this.selectedAccessory = selectedAccessory;
    }

    public void saveAccessory() {
        List<SharedAssetTypeAccesssory> accessoryList = selected.getAccessoryList();
        if (accessoryList.size() > 0) {
            for (int i = 0; i < accessoryList.size(); i++) {
                SharedAssetTypeAccesssory sata = accessoryList.get(i);
                if (sata.getName().equals(selectedAccessory.getName())) {
                    accessoryList.remove(sata);
                    i--;
                }
            }
        }
        selected.getAccessoryList().add(selectedAccessory);
    }

    public void prepareCreateAccessory() {
        crudActionName = "Create";
        selectedAccessory = new SharedAssetTypeAccesssory();
    }

    public void prepareEditAccessory() {
        crudActionName = "Edit";
    }

    public void deleteAccessory() {
        if (selectedAccessory != null)
            selected.getAccessoryList().remove(selectedAccessory);
    }

    public String getCreateDateFormatTime() {
        return createDateFormatTime;
    }

    public void setCreateDateFormatTime(String createDateFormatTime) {
        this.createDateFormatTime = createDateFormatTime;
    }

    public String getFilterUnitPrice() {
        double from = filterUnitPriceFrom != null ? filterUnitPriceFrom : 0;
        String s = (filterUnitPriceTo != null && filterUnitPriceTo != 0) ? filterUnitPriceTo + "" : "";
        if (filterUnitPriceFrom == null && filterUnitPriceTo == null)
            return null;
        return from + "~" + s;
    }

    /**
     * @return true if chargingType is day or time.
     */
    public boolean disableStartingFields() {
        String chargingType = selected.getChargingType();
        return StringUtils.equals("1", chargingType) || StringUtils.equals("3", chargingType);
    }

    /**
     * Reset value in starting fields, if charging type is day or time
     */
    public void onChargingTypeChange() {
        if (disableStartingFields()) {
            selected.setStartingPrice(null);
            selected.setStartingDuration(null);
        }
    }

    public void setFilterUnitPrice(String filterUnitPrice) {
        this.filterUnitPrice = filterUnitPrice;
    }

    public Double getFilterUnitPriceFrom() {
        return filterUnitPriceFrom;
    }

    public void setFilterUnitPriceFrom(Double filterUnitPriceFrom) {
        this.filterUnitPriceFrom = filterUnitPriceFrom;
    }

    public Double getFilterUnitPriceTo() {
        return filterUnitPriceTo;
    }

    public void setFilterUnitPriceTo(Double filterUnitPriceTo) {
        this.filterUnitPriceTo = filterUnitPriceTo;
    }

    public boolean  isWhetherToEnableUnattendedAndType(){
        String chargingType = selected.getChargingType();
        if(isWhetherToEnableUnattended() &&  !("1".equals(chargingType) || "3".equals(chargingType))){
            return true;
        }
        return false;
    }

    public Boolean  isWhetherToEnableUnattended(){
        if(whethertoenableunattended == null){
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            OrgInfo selectSite = userContextService.getSelectSite();
            List<WorkflowConfig> bySiteUID=null;
            if(selectSite != null){
                whethertoenableunattended=checkWhethertoenableunattended(selectSite.getSiteUID(),selectSite.getHospitalUID(),selectSite.getInstitutionUID(),selectSite.getTenantUID());
            }else{
                UserAccount loginUser = userContextService.getLoginUser();
                whethertoenableunattended=checkWhethertoenableunattended(loginUser.getSiteUID(),loginUser.getHospitalUID(),loginUser.getInstitutionUID(),loginUser.getTenantUID());

            }
        }

        return whethertoenableunattended;
    }
    private boolean checkWhethertoenableunattended(String siteUid,String hospitalUID,String InstitutionUID,String tenantUID){
        String key="whetherToEnableUnattended";
        String byKeyAndSiteForUseParent = configItemValueRepository.findByKeyAndSiteForUseParent(siteUid,
                hospitalUID,
                InstitutionUID,
                tenantUID, key);
        return Boolean.TRUE.toString().equalsIgnoreCase(byKeyAndSiteForUseParent);
    }
}
