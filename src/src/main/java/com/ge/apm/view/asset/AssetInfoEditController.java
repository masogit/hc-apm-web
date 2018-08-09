package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import com.ge.apm.service.asset.CustomConfigService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoEditController extends JpaCRUDController<AssetInfo> {
    
    private CustomConfigService ccService;
    private AssetInfoRepository dao;
    private UserCommonService ucService;
    private BlobFileService blobService;
    private QrCodeService qrCodeService;
    private AssetInfoMgrService aimService;
    private AssetCreateService acService;
    private GbCodeService gbCodeService;
    private UaaService uaaService;
    private AssetDepreciationService assetDepreciationService;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgDao;
    private GbCodeRepository gbDao;
    private AssetInfoService assetInfoService;
    private SupplierController supplierController;
    private UserAccountRepository userDao;
    private SupplierRepository supplierDao;
    private AutoCompleteService autoCompleteService;
    
    private Boolean terminate = false;
    private CustomConfigObject customConfig;
    private List<AssetInfo> parentAssetList;
    private List<OrgInfo> clinicalDeptList;
    private List<UserAccount> ownerList;
    private List<I18nMessage> code68List;
    private List<Supplier> supplierList = Collections.emptyList();
    private List<UserAccount> clinicalOwnerList;
    private List<V2_BlobObject> attachements;
    private ConfigService configService;
    private boolean resultStatus;
    private WorkflowConfig config;
    private String qrCode;
    private String gbCodeName;
    private GbCode selectedGb;
    private TreeNode gbTree;
    private TreeNode orgTree;
    
    private Map<String, Object> selectedObjectMap = new HashMap();
    private List<String> readableFieldsList;
    private List<String> editableFieldsList;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        configService = WebUtil.getBean(ConfigService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetDepreciationService = WebUtil.getBean(AssetDepreciationService.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID()));
                changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(Integer.parseInt(selectedId));
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            terminate = terminateStr == null ? false : Boolean.parseBoolean(terminateStr);
            prepareEdit();
            beforeEdit();
        }
        ownerList = ucService.getWorkerList(selected.getId(),"");
        config = configService.findConfigBySite(selected.getSiteUID());
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        editableFieldsList = ccService.getEditAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        supplierList = aimService.getSuplierListBySiteUid(selected.getTenantUID());
        clinicalDeptList = assetInfoService.getClinicalDeptList(selected.getSiteUID());
        initGbTree();
        initOrgTree();
    }
    
    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }
    
    @Override
    public void onBeforeNewObject(AssetInfo object) {
        object.setIsValid(true);
        object.setStatus(1);
        object.setDeviceStatus(0);
        object.setIsDeleted(false);
    }
    
    private void beforeEdit() {
        if (!StringUtils.isEmpty(selected.getNationalStandardCode())) {
            selectedGb = gbDao.getGbByCode(selected.getNationalStandardCode());
            if (null != selectedGb) {
                gbCodeName = selectedGb.getCode() + "/" + selectedGb.getName();
            }
        }
        selectedObjectMap.put("assetOwner", selected.getAssetOwnerId() == null ? null : userDao.findById(selected.getAssetOwnerId()));
        selectedObjectMap.put("assetOwner2", selected.getAssetOwnerId2() == null ? null : userDao.findById(selected.getAssetOwnerId2()));
        selectedObjectMap.put("assetOwner3", selected.getAssetOwnerId3() == null ? null : userDao.findById(selected.getAssetOwnerId3()));
        selectedObjectMap.put("clinicalOwner", selected.getClinicalOwnerId() == null ? null : userDao.findById(selected.getClinicalOwnerId()));
        selectedObjectMap.put("clinicalDept", selected.getClinicalDeptId() == null ? null : orgDao.findById(selected.getClinicalDeptId()));
        selectedObjectMap.put("supplier", selected.getSupplierId() == null ? null : supplierDao.findById(selected.getSupplierId()));
        selectedObjectMap.put("maintainer", selected.getMaitananceId() == null ? null : supplierDao.findById(Integer.valueOf(selected.getMaitananceId())));
        selectedObjectMap.put("manufacture", selected.getManufactureId() == null ? null : supplierDao.findById(Integer.valueOf(selected.getManufactureId())));
        selectedObjectMap.put("parentAsset", selected.getParentAssetId()== null ? null : dao.findById(selected.getParentAssetId()));
        selectedObjectMap.put("selectedOrg", selected.getSiteUID() == null ? null : orgDao.findByUid(selected.getSiteUID()));
        qrCode = selected.getQrCode();
        parentAssetList = aimService.getParentsAssetsList(this.selected);
        getClinicalOwnerList();
        
        if (selected.getSupplierId() != null) {
            Supplier supplier = supplierDao.findById(selected.getSupplierId());
            if (supplier != null) {
                selected.setVendorTel(supplier.getTel());
            }
        }
        
    }
    
    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }
    
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        changeAssetOrg(orgInfo);
        selectedObjectMap.put("selectedOrg", orgInfo);
    }
    
    public List<UserAccount> completeOwnerList(String query) {
        
        if (ownerList == null) {
            return Collections.emptyList();
        }
        
        List<UserAccount> res = this.ownerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    private void changeAssetOrg(OrgInfo orgInfo) {
        selected.setSiteId(orgInfo.getSiteId());
        selected.setHospitalId(orgInfo.getId());
        
        selected.setTenantUID(orgInfo.getTenantUID());
        selected.setHospitalUID(orgInfo.getHospitalUID());
        selected.setInstitutionUID(orgInfo.getInstitutionUID());
        selected.setSiteUID(orgInfo.getSiteUID());
        config = configService.findConfigBySite(selected.getSiteUID());
        clinicalDeptList = assetInfoService.getClinicalDeptList(selected.getSiteUID());
        selectedObjectMap.put("clinicalDept", null);
        onClinicalDeptChange();
        parentAssetList.clear();
    }
    
    public void onParentAssetChange() {
        AssetInfo selectedAsset = (AssetInfo) selectedObjectMap.get("parentAsset");
        selected.setParentAssetId(null == selectedAsset ? null : selectedAsset.getId());
    }
    
    public void onOwnerChange(String idx) {
        UserAccount selectedUser = (UserAccount) selectedObjectMap.get(idx);
        switch (idx) {
            case "assetOwner":
                selected.setAssetOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "assetOwner2":
                selected.setAssetOwnerId2(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName2(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel2(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "assetOwner3":
                selected.setAssetOwnerId3(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName3(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel3(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "clinicalOwner":
                selected.setClinicalOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setClinicalOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setClinicalOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
        }
    }
    
    public void onSupplierChange(String idx) {
        Supplier supplier = (Supplier) selectedObjectMap.get(idx);
        switch (idx) {
            case "supplier":
                selected.setSupplierId(supplier == null ? null : supplier.getId());
                selected.setVendor(supplier == null ? null : supplier.getName());
                selected.setVendorTel(supplier == null ? null : supplier.getTel());
                break;
            case "maintainer":
                selected.setMaitananceId(supplier == null ? null : supplier.getId().toString());
                selected.setMaitanance(supplier == null ? null : supplier.getName());
                selected.setMaitananceTel(supplier == null ? null : supplier.getTel());
                break;
            case "manufacture":
                selected.setManufactureId(supplier == null ? null : supplier.getId().toString());
                selected.setManufacture(supplier == null ? null : supplier.getName());
                break;
        }
    }
    
    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(assetuid);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }
    
    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            attachements.forEach(item -> {
                if (item.getBoId() == null) {
                    item.setBoId(object.getId());
                    item.setBoUuid(object.getUid());
                    blobService.saveBlobObject(item);
                }
            });
        }
    }
    
    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
            attachements.add(blobObject);
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
    
    @Transactional
    public String applyChange() {
        if (selected.getUsageStatus() == null) {
            selected.setUsageStatus(3);
        }
        if (Strings.isNullOrEmpty(qrCode) || "".equals(qrCode.trim())) {
            selected.setQrCode(null);
        } else {
            if (!qrCode.equals(selected.getQrCode())) {
                if ("DFQ".equals(getAssetCodeConfig())) {
                    QrCodeLib qrCodeLib = qrCodeService.findByQrCode(qrCode, selected.getSiteUID());
                    if (qrCodeLib == null) {
                        if (!qrCodeService.createSpecialQrCode(qrCode, selected.getSiteUID())) {
                            WebUtil.addErrorMessage("二维码不正确");
                            return "";
                        };
                    }
                    qrCodeService.forceRemoveQrcode(selected.getQrCode(), selected.getSiteUID());
                    selected.setQrCode(null);
                }
                if (!updateQrCode()) {
                    qrCode = "";
                    return "";
                }
            }
        }
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return "";
            }
            
            selected.setNationalStandardCode(selectedGb.getCode());
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
        return "AssetList?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }

    //三码合一设置
    public String getAssetCodeConfig() {
        return config == null ? null : config.getAssetCodeConfig();
    }
    
    private boolean updateQrCode() {
        QrCodeLib qrCodeLib = qrCodeService.findByQrCode(qrCode, selected.getSiteUID());
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
        
        QrCodeLib oldQRCodeLib = qrCodeService.findByQrCode(selected.getQrCode(), selected.getSiteUID());
        if (null != oldQRCodeLib) {
            qrCodeService.discardQrCode(qrCode, selected.getSiteUID());
        }
        qrCodeService.updateQrCodeStatus(qrCode, selected.getSiteUID(), 3);
        selected.setQrCode(qrCode);
        return true;
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
        changeFunctionGroup();
    }
    
    @Override
    public void onBeforeSave(AssetInfo object) {
        if (null != object.getClinicalDeptId()) {
            OrgInfo org = orgDao.findById(object.getClinicalDeptId());
            if (org != null) {
                object.setClinicalDeptName(org.getName());
                object.setClinicalDeptUID(org.getUid());
            }
            
        } else {
            //clear uid and its name
            object.setClinicalDeptUID(null);
            object.setClinicalDeptName(null);
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
        Date now = new Date();
        if (object.getId() == null) {
            object.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setCreatedDate(now);
            object.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setLastModifiedDate(now);
        } else {
            object.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setLastModifiedDate(now);
        }
    }
    
    public String setValidButton() {
        if (terminate) {
            this.selected.setIsValid(true);
            this.selected.setTerminateDate(null);
        } else {
            int workOrdersNum = dao.fetchCurrentWorkOrders(selected.getId());
            if (workOrdersNum > 0) {
                WebUtil.addErrorMessage("当前设备存在未完成工单，不能报废！");
                return null;
            } else {
                this.selected.setIsValid(false);
                this.selected.setTerminateDate(new Date());
            }
        }
        update();
        return "AssetList?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }
    
    public Boolean needAssetApproval(String siteUid) {
        if (null != config && config.getNeedInstorageCheck() != null && config.getNeedInstorageCheck()) {
            return false;
        }
        return acService.needAssetApproval(siteUid);
    }
    
    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(selected.getTenantUID(), selected.getSiteUID(), 1);
        qrCode = qrcodeLib.getQrCode();
        onDepartNumChange("qrCode");
    }
    
    public void onDepartNumChange(String fieldName) {
        if (!Strings.isNullOrEmpty(getAssetCodeConfig())) {
            String num = null;
            switch (getAssetCodeConfig()) {
                case "DFQ":
                    switch (fieldName) {
                        case "financingNum":
                            num = selected.getFinancingNum();
                            break;
                        case "departNum":
                            num = selected.getDepartNum();
                            break;
                        case "qrCode":
                            num = qrCode;
                            break;
                        default:
                    }
                    if (!Strings.isNullOrEmpty(num) && num.length() > 16) {
                        qrCode = num.substring(0, 16);
                        selected.setFinancingNum(qrCode);
                        selected.setDepartNum(qrCode);
                        WebUtil.addErrorMessage("二维码超长,最大16位");
                    } else {
                        qrCode = num;
                        selected.setFinancingNum(num);
                        selected.setDepartNum(num);
                    }
                    break;
                case "DF":
                    switch (fieldName) {
                        case "financingNum":
                            num = selected.getFinancingNum();
                            selected.setFinancingNum(num);
                            selected.setDepartNum(num);
                            break;
                        case "departNum":
                            num = selected.getDepartNum();
                            selected.setFinancingNum(num);
                            selected.setDepartNum(num);
                            break;
                    }
                    break;
                default:
            }
        }
    }
    
    public void generateFinancialNum() {
        String code = this.selectedGb == null ? null : this.selectedGb.getCode();
        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                this.selected.getId(),
                code,
                this.selected.getSiteUID(),
                this.selected.getParentAssetId(),
                this.selected.getFinancingNum()
        );
        if (financingNum != null) {
            if (financingNum.startsWith("error:")) {
                WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            } else {
                this.selected.setFinancingNum(financingNum);
                onDepartNumChange("financingNum");
            }
        }
    }
    
    public void updateFinancingNumByNationalStandardCode() {
        if (null != selected && null != selectedGb) {
            String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    this.selected.getId(),
                    this.selectedGb.getCode(),
                    this.selected.getSiteUID(),
                    null,
                    null
            );
            if (financingNum != null && StringUtils.isEmpty(this.selected.getFinancingNum())) {
                this.selected.setFinancingNum(financingNum);
            }
        }
    }
    
    public List<String> completeGbName(String prefix) {
        List<String> names = null;
        if (!StringUtils.isEmpty(prefix)) {
            names = gbDao.findGbCodeByPrefix(prefix);
        }
        return names;
    }
    
    private void updateSelectedOrgGb(GbCode gbCode) {
        if (gbCode == null || gbCode.getId() == -1) {
            selectedGb = null;
        } else {
            selectedGb = gbCode;
            gbCodeName = gbCode.getCode() + "/" + gbCode.getName();
        }
    }
    
    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
    }
    
    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
    }
    
    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    public List<AssetInfo> completeParentAssetId(String query) {
        if (null == parentAssetList || parentAssetList.isEmpty()) {
            return Collections.emptyList();
        }
        return parentAssetList.stream().filter(item -> item.getName().contains(query) || item.getAliasName() != null && item.getAliasName().contains(query)
                || item.getDepartNum() != null && item.getDepartNum().contains(query) || item.getFinancingNum() != null && item.getFinancingNum().contains(query)
                || item.getQrCode() != null && item.getQrCode().contains(query) || item.getNamePyCode() != null && item.getNamePyCode().contains(query)).collect(Collectors.toList());
    }
    
    public List<Supplier> autoCompleteSupplier(String query) {
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }
    
    public List<Supplier> autoCompleteManufacture(String query) {
        return autoCompleteService.autoCompleteManufacture(query, supplierList);
    }
    
    public List<Supplier> autoCompleteMaintainer(String query) {
        return autoCompleteService.autoCompleteMaintainer(query, supplierList);
    }
    
    public List<String> completeAssetName(String prefix) {
        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
        List<String> names = null;
        if (StringUtils.isEmpty(prefix)) {
            names = dao.findDistinctAssetNames(tenantUID);
        } else {
            names = dao.findDistinctAssetNamesByPrefix(tenantUID, prefix);
        }
        names.sort(new PinYinComparator());
        return names;
    }
    
    public List<UserAccount> completeClinicalOwnerList(String query) {
        if (clinicalOwnerList == null) {
            return Collections.emptyList();
        };
        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    
    public void onClinicalDeptChange() {
        OrgInfo clinicalDept = (OrgInfo) selectedObjectMap.get("clinicalDept");
        selected.setClinicalDeptId(clinicalDept == null ? null : clinicalDept.getId());
        selected.setClinicalDeptName(clinicalDept == null ? null : clinicalDept.getName());
        selected.setClinicalDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
        
        selectedObjectMap.put("clinicalOwner", null);
        onOwnerChange("clinicalOwner");
        parentAssetList = aimService.getParentsAssetsList(this.selected);
        getClinicalOwnerList();
    }
    
    public void getClinicalOwnerList() {
        if (null != selected.getClinicalDeptUID()) {
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptUID());
        } else {
            clinicalOwnerList = Collections.emptyList();
        }
    }
    
    public String submitApproval() {
        selected.setUsageStatus(2);
        return applyChange();
    }
    
    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }
    
    public boolean needShow(String itemKey, String fieldKey) {
        return itemKey.equals(fieldKey) && readableFieldsList.contains(fieldKey);
    }
    
    public boolean needEdit(String fieldKey) {
        return editableFieldsList.contains(fieldKey);
    }

//getter and setter
    public Boolean getTerminate() {
        return terminate;
    }
    
    public CustomConfigObject getCustomConfig() {
        return customConfig;
    }
    
    public List<I18nMessage> getCode68List() {
        return code68List;
    }
    
    public List<AssetInfo> getParentAssetList() {
        return parentAssetList;
    }
    
    public void setParentAssetList(List<AssetInfo> parentAssetList) {
        this.parentAssetList = parentAssetList;
    }
    
    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }
    
    public void setAttachements(List<V2_BlobObject> attachements) {
        this.attachements = attachements;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public String getGbCodeName() {
        return gbCodeName;
    }
    
    public void setGbCodeName(String gbCodeName) {
        this.gbCodeName = gbCodeName;
    }
    
    public GbCode getSelectedGb() {
        return selectedGb;
    }
    
    public void setSelectedGb(GbCode selectedGb) {
        this.selectedGb = selectedGb;
    }
    
    public TreeNode getOrgTree() {
        return orgTree;
    }
    
    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }
    
    public TreeNode getGbTree() {
        return gbTree;
    }
    
    public void setGbTree(TreeNode gbTree) {
        this.gbTree = gbTree;
    }
    
    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }
    
    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }
    
}
