package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetDepreciationService;
import com.ge.apm.service.asset.AssetFinacingNumGenerateService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AssetInfoController extends JpaCRUDController<AssetInfo> {

    private static final long serialVersionUID = -1L;

    AssetInfoRepository dao = null;

    OrgInfoRepository orgInfoRepository;

    private UserAccountRepository userDao;

    private QrCodeLibRepository qrcodeDao;

    private BrandRepository brandDao;

    private boolean resultStatus;

    private UserAccount clinicalOwner;

    private Supplier supplier;

    private Brand brand;

    private StreamedContent zipFile;

    private UserAccount owner;
    private UserAccount owner2;
    private UserAccount owner3;
    //    private List<UserAccount> ownerList;
    private List<OrgInfo> ownerOrgList;

    private List<V2_BlobObject> attachements;

    private BlobFileService blobService;

    private UaaService uuaService;

    private OrgInfoRepository orgDao;

    private BlobObjectRepository blobDao;

    private String operation;

    private AssetDepreciationService assetDepreciationService;

    private QrCodeService qrCodeService;
    private UserCommonService ucService;

    private String oneSearchStr;
    private String qrCode;
    Boolean terminate;
    private Integer assetId;

    private List<AssetInfo> parentAssetList;
    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;

    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    private String selectedSiteUID;

    protected void setSelectedByUrlParam(String encodeUrl, String paramName) {
        setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeUrl, paramName)));

        owner = selected.getAssetOwnerId() == null ? null : userDao.findById(selected.getAssetOwnerId());
        owner2 = selected.getAssetOwnerId2() == null ? null : userDao.findById(selected.getAssetOwnerId2());
        owner3 = selected.getAssetOwnerId3() == null ? null : userDao.findById(selected.getAssetOwnerId3());

        if (null != selected.getClinicalOwnerId()) {
            clinicalOwner = userDao.findById(selected.getClinicalOwnerId());
        }

        if (null != selected.getSupplierId()) {
            SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);
            supplier = supplierDao.findById(selected.getSupplierId());
        }

        qrCode = selected.getQrCode();

        selectedOrg = orgInfoRepository.getByUid(selected.getSiteUID());

        setSelectedSiteUID(selected.getSiteUID());
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        qrcodeDao = WebUtil.getBean(QrCodeLibRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        brandDao = WebUtil.getBean(BrandRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetDepreciationService = WebUtil.getBean(AssetDepreciationService.class);
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);

        this.filterBySite = true;

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        // String actionName = WebUtil.getRequestParameter("actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("View".equalsIgnoreCase(actionName)) {
            // setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            if (terminateStr != null) {
                terminate = Boolean.parseBoolean(terminateStr);
            }
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareView();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            if (terminateStr != null) {
                terminate = Boolean.parseBoolean(terminateStr);
            }
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareEdit();
        } else if ("Delete".equalsIgnoreCase(actionName)) {
            //setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
            setSelectedByUrlParam(encodeStr, "selectedid");
            prepareDelete();
        }

        ownerOrgList = uuaService.getOrgListByHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

        if (this.selected != null) {
            initOrgTree();
        }


    }

    public Boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    private void setUserSearchFilters() {
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        if (terminate) {
            this.searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, false));
        } else {
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }

        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
            this.removeFilterOnField("serialNum");
            this.removeFilterOnField("manufacture");
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        selected = null;

        if (assetId == null) {
            setUserSearchFilters();
        }
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public void exportQrCode() throws Exception {
        List<AssetInfo> exportList = dao.findBySearchFilter(searchFilters);
        List<String> qrCodeList = exportList.stream().filter(item -> item.getQrCode() != null && !item.getQrCode().isEmpty()).map(AssetInfo::getQrCode).collect(Collectors.toList());
        zipFile = qrCodeService.exportQrCodeList(qrCodeList);
    }

    public void onSelectAsset() throws IOException {
        String url = "actionName=View&selectedid="
                + selected.getId().toString()
                + "&asset_name=" + selected.getName()
                + "&terminate=" + this.terminate;
        url = "Detail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        //FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String getViewPage(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
        return operation;
    }

    public String getDetailPage() {
        operation = "Detail.xhtml?actionName=View&selectedid=" + selected.getId()
                + "&asset_name=" + selected.getName();
        return operation;
    }

    public String getDetailPage(String assetId, String assetName) {
        operation = "Detail.xhtml?actionName=View&selectedid=" + assetId
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public String getEditPage(String assetId, String assetName) {
        operation = "Create.xhtml?actionName=Edit&selectedid=" + assetId
                + "&asset_name=" + assetName
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void assembleMaintainData() {
        operation = "/portal/sr/srList?faces-redirect=true";
    }

    public void assembleMaintainList() {
        operation = "/portal/sr/srList?faces-redirect=true";
    }

    public String getContractListLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId();
    }

    public String getContractAddLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId() + "&actionName=Create";
    }

    @Override
    public void onBeforeNewObject(AssetInfo object) {
        object.setSiteId(UserContextService.getSiteId());
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
        object.setIsValid(true);
        object.setStatus(1);
        object.setDeviceStatus(0);
        object.setIsDeleted(false);

        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
    }

    @Override
    public void onBeforeSave(AssetInfo object) {
        if (null != object.getClinicalDeptId()) {
            OrgInfo org = orgDao.findById(object.getClinicalDeptId());
            object.setClinicalDeptName(org.getName());

            object.setTenantUID(org.getTenantUID());
            object.setHospitalUID(org.getHospitalUID());
            object.setInstitutionUID(org.getInstitutionUID());
            object.setSiteUID(org.getSiteUID());
            object.setClinicalDeptUID(org.getUid());
        }
        DateTime temp;
        if (null != object.getManufactDate()) {
            temp = new DateTime(object.getManufactDate());
            object.setManufactDate(temp.plusHours(12).toDate());
        }
        if (null != object.getPurchaseDate()) {
            temp = new DateTime(object.getPurchaseDate());
            object.setPurchaseDate(temp.plusHours(12).toDate());
        }
        if (null != object.getArriveDate()) {
            temp = new DateTime(object.getArriveDate());
            object.setArriveDate(temp.plusHours(12).toDate());
        }
        if (null != object.getInstallDate()) {
            temp = new DateTime(object.getInstallDate());
            object.setInstallDate(temp.plusHours(12).toDate());
        }
        if (null != object.getWarrantyDate()) {
            temp = new DateTime(object.getWarrantyDate());
            object.setWarrantyDate(temp.plusHours(12).toDate());
        }
        if (null != object.getTerminateDate()) {
            temp = new DateTime(object.getTerminateDate());
            object.setTerminateDate(temp.plusHours(12).toDate());
        }
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if(isOK){
            attachements.forEach(item->{
                if(item.getBoId()==null){
                    item.setBoId(object.getId());
                    item.setBoUuid(object.getUid());
                    blobDao.save(item);
                }
            });
        }
    }

    @Override
    public void onAfterDeleteObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    public String removeOne() {
        this.delete();
        if (resultStatus) {
            return "List?faces-redirect=true";
        } else {
            return "";
        }
    }

    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(selected.getTenantUID(), selected.getSiteUID(), 1);
        qrCode = qrcodeLib.getQrCode();
    }

    private boolean updateQrCode() {
        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrCode);
        if (qrCodeLib == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("InvalidQRCode"));
            return false;
        }
        if (!qrCodeLib.getSiteUID().equals(selected.getSiteUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            WebUtil.addErrorMessage(WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        QrCodeLib oldQRCodeLib = qrcodeDao.findByQrCode(selected.getQrCode());
        if (null != oldQRCodeLib) {
            qrCodeService.discardQrCode(qrCode);
        }
        qrCodeLib.setStatus(3);
        qrcodeDao.save(qrCodeLib);
        selected.setQrCode(qrCode);
        return true;
    }

    @Transactional
    public String applyChange() {
        if (!isTimeValidate()) {
            return "";
        }
        if (!qrCode.equals(selected.getQrCode()) && !qrCode.isEmpty()) {
            if (!updateQrCode()) {
                return "";
            }
        }
        if (qrCode != null && "".equals(qrCode.trim())) {
            selected.setQrCode(null);
        }

        this.save();
        assetDepreciationService.saveAssetDerpeciation(selected);
        if (resultStatus) {
            return getListPageLink();
        } else {
            return "";
        }
    }

    public String getListPageLink() {
        return "List?faces-redirect=true" + (this.isTerminate() ? "&terminate=true" : "");
    }

    public String setValidButton() {
        if (terminate) {
            this.selected.setIsValid(true);
            this.selected.setTerminateDate(null);
        } else {
            Integer workOrdersNum = dao.fetchCurrentWorkOrders(selected.getId());
            if (workOrdersNum > 0) {
                WebUtil.addErrorMessage("当前设备存在未完成工单，不能报废！");
                return null;
            } else {
                this.selected.setIsValid(false);
                this.selected.setTerminateDate(new Date());
            }
        }
        update();
        return "List?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }

    public UserAccount getOwner() {
        return owner;
    }

    public List<OrgInfo> getOwnerOrgList() {
        return ownerOrgList;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public void onOwnerChange() {
        if (null != owner) {
            selected.setAssetOwnerId(owner.getId());
            selected.setAssetOwnerName(owner.getName());
            selected.setAssetOwnerTel(owner.getTelephone());
        } else {
            selected.setAssetOwnerId(null);
            selected.setAssetOwnerName(null);
            selected.setAssetOwnerTel(null);
        }

        if (null != owner2) {
            selected.setAssetOwnerId2(owner2.getId());
            selected.setAssetOwnerName2(owner2.getName());
            selected.setAssetOwnerTel2(owner2.getTelephone());
        } else {
            selected.setAssetOwnerId2(null);
            selected.setAssetOwnerName2(null);
            selected.setAssetOwnerTel2(null);
        }

        if (null != owner3) {
            selected.setAssetOwnerId3(owner3.getId());
            selected.setAssetOwnerName3(owner3.getName());
            selected.setAssetOwnerTel3(owner3.getTelephone());
        } else {
            selected.setAssetOwnerId3(null);
            selected.setAssetOwnerName3(null);
            selected.setAssetOwnerTel3(null);
        }

    }

    public void onClinicalDeptChange() {
        this.clinicalOwner = null;
        selected.setClinicalOwnerId(null);
        selected.setClinicalOwnerName(null);
        selected.setClinicalOwnerTel(null);
    }

    public void onClinicalOwnerChange() {
        if (null != clinicalOwner) {
            selected.setClinicalOwnerId(clinicalOwner.getId());
            selected.setClinicalOwnerName(clinicalOwner.getName());
            selected.setClinicalOwnerTel(clinicalOwner.getTelephone());
        } else {
            selected.setClinicalOwnerId(null);
            selected.setClinicalOwnerName(null);
            selected.setClinicalOwnerTel(null);
        }
    }

    public void onSupplierChange() {
        if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());
        }
    }

    public void onBrandChange() {
        if (null != brand) {
            selected.setBrand(brandDao.findById(brand.getId()));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
//            AssetFileAttachment attach = new AssetFileAttachment();
//            attach.setName(blobObject.getObjectName());
//            attach.setFileType(type);
//            attach.setSiteId(UserContextService.getCurrentUserAccount().getSiteId());
//            attach.setAssetId(selected.getId());
//            attach.setHospitalId(selected.getHospitalId());
//            attach.setFileUrl(blobObject.getObjectStorageId());
            attachements.add(blobObject);
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getUid());
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            //sfs.addAll(this.searchFilters);
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, assetuid));
            attachements = blobDao.findBySearchFilter(sfs);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }


    public void removeAttachment(String objectId) {

        blobService.deleteBlobFileByStorageId(objectId);
        attachements = attachements.stream().filter(item -> !item.getObjectStorageId().equals(objectId)).collect(Collectors.toList());

    }

    public String getHospitalName(String siteUid) {
        OrgInfo org = orgDao.getByUid(siteUid);
        return org == null ? "" : org.getName();
    }

    public void onHospitalChange() {
        OrgInfo org = orgDao.getByUid(selected.getSiteUID());
        selected.setInstitutionUID(org.getInstitutionUID());
        selected.setHospitalUID(org.getHospitalUID());
        this.owner = null;
    }

    public List<UserAccount> getOwnerList() {
       List<UserAccount> accountList = getEmptyAccountList();
       accountList.addAll(ucService.getWorkerList(selected.getId()));
       return accountList;
    }
    
    private List<UserAccount> getEmptyAccountList(){
        List<UserAccount> accountList = new ArrayList();
        UserAccount emptyValue = new UserAccount();
        emptyValue.setName(WebUtil.getMessage("selectOneMessage"));
        accountList.add(emptyValue);
        return accountList;
    }

    public List<UserAccount> getClinicalOwnerList() {
        List<UserAccount> accountList = getEmptyAccountList();
        if (null != selected.getClinicalDeptId()) {
            accountList.addAll(userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptId()));
        } 
        return accountList;
    }

    public String getQrCodeImageBase64() {
        return qrCodeService.getQrCodeImageBase64(this.selected.getQrCode());
    }

    public boolean isTimeValidate() {
        String message = WebUtil.getMessage("shouldEarly"); //"{0} must before {1}!";
        AssetInfo input = this.selected;
//        Date todaydate = new Date();
        boolean isError = false;

        if (null != input.getManufactDate() && null != input.getPurchaseDate() && input.getManufactDate().after(input.getPurchaseDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("manufactDate"), WebUtil.getMessage("purchaseDate")));
        }

        if (null != input.getArriveDate() && null != input.getPurchaseDate() && input.getPurchaseDate().after(input.getArriveDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("purchaseDate"), WebUtil.getMessage("arriveDate")));
        }

        if (null != input.getArriveDate() && null != input.getInstallDate() && input.getArriveDate().after(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("arriveDate"), WebUtil.getMessage("installDate")));
        }
        if (null != input.getWarrantyDate() && null != input.getInstallDate() && input.getWarrantyDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("warrantyDate")));
        }
        if (null != input.getTerminateDate() && null != input.getInstallDate() && input.getTerminateDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("terminateDate")));
        }
        if (null != input.getLastPmDate() && null != input.getInstallDate() && input.getLastPmDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastPmDate")));
        }
        if (null != input.getLastMeteringDate() && null != input.getInstallDate() && input.getLastMeteringDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastMeteringDate")));
        }
        if (null != input.getLastQaDate() && null != input.getInstallDate() && input.getLastQaDate().before(input.getInstallDate())) {
            isError = true;
            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("installDate"), WebUtil.getMessage("lastQaDate")));
        }

//        if (null!=input.getLastPmDate() && input.getLastPmDate().after(todaydate)) {
//            isError = true;
//            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastPmDate"), WebUtil.getMessage("todayDate")));
//        }
//        if (null!=input.getLastMeteringDate() && input.getLastMeteringDate().after(todaydate)) {
//            isError = true;
//            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastMeteringDate"), WebUtil.getMessage("todayDate")));
//        }
//        if (null!=input.getLastQaDate() && input.getLastQaDate().after(todaydate)) {
//            isError = true;
//            WebUtil.addErrorMessage(MessageFormat.format(message, WebUtil.getMessage("lastQaDate"), WebUtil.getMessage("todayDate")));
//        }
        return !isError;
    }

    private String filterAssetStatus = null;

    public String getFilterAssetStatus() {
        return filterAssetStatus;
    }

    public void setFilterAssetStatus(String filterAssetStatus) {
        this.filterAssetStatus = filterAssetStatus;
    }

    public void setAssetStatusFilter() {
        if (filterAssetStatus == null && filterWarrantyDate == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterAssetStatus != null) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, filterAssetStatus));
        }
        if (assetId != null) {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, assetId));
        }
        /*if (filterWarrantyDate != null && "1".equals(filterWarrantyDate)) {
            Date warrantyDate = new Date();
            //2个月内，则是保修到期时间在2个月后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(warrantyDate);
            c.add(Calendar.MONTH, 2);
            varWarrantyDateTo = c.getTime();
            searchFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.LTE, varWarrantyDateTo));
            filterWarrantyDate = null;
        }*/
    }

    public void deleteAsset(AssetInfo asset) {
        if (asset.getQrCode() != null) {
            qrCodeService.updateQrCodeStatus(asset.getQrCode(), 1);
        }
        asset.setIsDeleted(true);
        asset.setIsValid(false);
        asset.setQrCode(null);
        dao.save(asset);
    }

    private String filterWarrantyDate = null;

    public String getFilterWarrantyDate() {
        return filterWarrantyDate;
    }

    public void setFilterWarrantyDate(String filterWarrantyDate) {
        this.filterWarrantyDate = filterWarrantyDate;
    }

    private Date varWarrantyDateTo = null;
    private String warrantyFormatTime = null;

    public Date getVarWarrantyDateTo() {
        return varWarrantyDateTo;
    }

    public void setVarWarrantyDateTo(Date varWarrantyDateTo) {
        this.varWarrantyDateTo = varWarrantyDateTo;
    }

    public String getWarrantyFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varWarrantyDateTo == null) {
            return warrantyFormatTime;
        } else {
            return "~" + sdf.format(varWarrantyDateTo);
        }
    }

    public void setWarrantyFormatTime(String warrantyFormatTime) {
        this.warrantyFormatTime = warrantyFormatTime;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Boolean isTerminate() {
        if (null != selected) {
            return !selected.getIsValid();
        }
        return terminate;
    }

    public UserAccount getClinicalOwner() {
        return clinicalOwner;
    }

    public void setClinicalOwner(UserAccount clinicalOwner) {
        this.clinicalOwner = clinicalOwner;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public UserAccount getOwner2() {
        return owner2;
    }

    public void setOwner2(UserAccount owner2) {
        this.owner2 = owner2;
    }

    public UserAccount getOwner3() {
        return owner3;
    }

    public void setOwner3(UserAccount owner3) {
        this.owner3 = owner3;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getOrgName(Integer orgId) {
        OrgInfo org = orgDao.findById(orgId);
        if (org != null) {
            return org.getName();
        }
        return null;
    }
    
    public String getSiteOrgName(String siteUID) {
        OrgInfo org = orgDao.getByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public String getAssetDetailTitle() {
        return terminate ? WebUtil.getMessage("TerminateAssetInfoDetail") : WebUtil.getMessage("AssetInfoDetail");
    }

    public String getAssetEditTitle() {
        return terminate ? WebUtil.getMessage("TerminateAssetInfo") : WebUtil.getMessage("AssetInfo");
    }

    public String getAssetListTitle() {
        return terminate ? WebUtil.getMessage("TerminateAssetInfo") : WebUtil.getMessage("AssetInfo");
    }

    public String getButtonName() {
        return terminate ? WebUtil.getMessage("ActiveAsset") : WebUtil.getMessage("InactiveAsset");
    }

    public void onAssetGroupChange() {

        List<SearchFilter> assetFilters = new ArrayList<>();
        if (this.selected.getAssetGroup() != null) {
            assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, this.selected.getSiteId()));
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.selected.getAssetGroup()));

            parentAssetList = dao.findBySearchFilter(assetFilters);
        } else {
            parentAssetList = new ArrayList<>();
        }

    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.selected.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.selected.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();

        onAssetGroupChange();

        changeFunctionGroup();
    }

    public List<AssetInfo> getParentAssetList() {
        return parentAssetList;
    }

    public void setParentAssetList(List<AssetInfo> parentAssetList) {
        this.parentAssetList = parentAssetList;
    }

    public String getParentAssetName() {
        if (this.selected.getParentAssetId() != null) {
            AssetInfo assetInfo = this.dao.getById(this.selected.getParentAssetId());
            if (assetInfo != null) {
                return assetInfo.getName();
            }
        }
        return "";
    }
    
    public String getParentAssetNameByAssetId(int assetId) {
    	AssetInfo ai = dao.findById(assetId);
        if (ai.getParentAssetId() != null) {
            AssetInfo assetInfo = dao.getById(ai.getParentAssetId());
            if (assetInfo != null) {
                return assetInfo.getName();
            }
        }
        return "";
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public void setCode68List(List<I18nMessage> code68List) {
        this.code68List = code68List;
    }

    public StreamedContent getZipFile() {
        return zipFile;
    }

    public void setZipFile(StreamedContent zipFile) {
        this.zipFile = zipFile;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if(oneSearchStr.isEmpty()){
            this.removeFilterOnField("name");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
            this.removeFilterOnField("serialNum");
            this.removeFilterOnField("manufacture");
        }
    }

    public List<Brand> getBrandList() {
        return brandDao.find();
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    private TreeNode orgTree;

    private TreeNode selectedNode;

    private OrgInfo selectedOrg;

    private UaaService uaaService = WebUtil.getBean(UaaService.class);

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;

            selected.setTenantUID(null);
            selected.setInstitutionUID(null);
            selected.setHospitalUID(null);
            selected.setSiteUID(null);

            setSelectedSiteUID(null);
        } else {
            selectedOrg = (OrgInfo) node.getData();

            selected.setTenantUID(selectedOrg.getTenantUID());
            selected.setInstitutionUID(selectedOrg.getInstitutionUID());
            selected.setHospitalUID(selectedOrg.getHospitalUID());
            selected.setSiteUID(selectedOrg.getSiteUID());

            setSelectedSiteUID(selectedOrg.getUid());
        }

    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
//        orgTree = new DefaultTreeNode("root",null,null);
//        orgTree.getChildren().add(treeNode);
//        orgTree.setExpanded(true);

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root",null,null);
        TreeNode rootNode = new DefaultTreeNode("org",rootOrg,orgTree);
        rootNode.getChildren().add(treeNode);
        rootNode.setExpanded(true);

    }

    public void updateFinancingNumByNationalStandardCode() {
        String financingNum = assetFinacingNumGenerateService.generateFinancingNum(
                this.selected.getId(),
                this.selected.getNationalStandardCode(),
                this.selected.getSiteUID()
        );
        if (financingNum != null && StringUtils.isEmpty(this.selected.getFinancingNum())) {
            this.selected.setFinancingNum(financingNum);
        }
    }

    public String getSelectedSiteUID() {
        return selectedSiteUID;
    }

    public void setSelectedSiteUID(String selectedSiteUID) {
        this.selectedSiteUID = selectedSiteUID;
    }
}
