package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetArrivedListEditController extends GenericCRUDUUIDController<ArrivalList> {

    private ArrivalListRepository dao = null;
    private ArrivalListItemRepository itemDao = null;

    private OrgInfo selectedOrg;

    private List<ArrivalListItem> arrivalItemList;

    private AssetInstallationService aiService;
    private ContractInfoService contractService;
    private UaaService uaaService;
    private TreeNode orgTree;

    private Supplier supplier;
    private Supplier maitanance;
    private Supplier manufacture;
    private String copyFrom = "";
    private TreeNode selectedNode;

    private ArrivalListItem easyInstallItem;

    private CustomConfigService ccService;

    private List<I18nMessage> code68List;

    private I18nMessageRepository i18nDao;
    private Integer installCount;
    private Map<String, Boolean> generateMap;
    private Boolean generatePmorder;

    private String nationalStandardCode;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private ConfigService configService;
    private GbCodeService gbCodeService;
    private TreeNode gbTree;
    private SupplierController supplierController;
    private MedicalInstrumentsClassificationCompCtrl medicalInstrumentsClassificationCompCtrl;
    private ServiceRequestApiService serviceRequestApiService;

    UserAccount currentUser;

    private List<V2_BlobObject> attachList;

    private BlobFileService blobService;

    SupplierRepository supplierDao = null;
    List<Supplier> suppliersList;
    List<Supplier> manueList;
    List<Supplier> maintainerList;
    private boolean needInstorageCheck;
    private AutoCompleteService autoCompleteService;

    private String selectedContractItemId;
    private String selectedContractId;
    private List<ContractInfo> contractList;
    private List<ContractInfoItem> contractItemList = new ArrayList<>();

    private SupplierService supplierService;
    private List<UserAccount> supplierUserList;

    @Getter
    private CustomConfigObject customConfig;

    private String imageZoom;
    private String imageName;


    @Getter
    @Setter
    private Supplier newSupplier;

    @Getter
    @Setter
    private String newSupplierType;

    private UserContextService userContextService;
    private TenantInfoRepository tenantInfoRepository;

    @Override
    protected void init() {

        dao = WebUtil.getBean(ArrivalListRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        contractService = WebUtil.getBean(ContractInfoService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        TreeNode subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(UserContextService.getCurrentUserAccount().getSiteUID(), UserContextService.getCurrentUserAccount().getUid());
        orgTree = new DefaultTreeNode("org", null, null);
        orgTree.getChildren().add(subTree);
        attachList = new ArrayList<>();
        currentUser=UserContextService.getCurrentUserAccount();
        configService = WebUtil.getBean(ConfigService.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        serviceRequestApiService = WebUtil.getBean(ServiceRequestApiService.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        medicalInstrumentsClassificationCompCtrl = WebUtil.getBean(MedicalInstrumentsClassificationCompCtrl.class);
        medicalInstrumentsClassificationCompCtrl.registerController(this);

        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                String copyFrom = (String) UrlEncryptController.getValueFromMap(encodeStr, "copyFrom");
                prepareCreate();
                if (!Strings.isNullOrEmpty(copyFrom)) {
                    copyFiledsValueById(copyFrom);
                    setCopyFrom(copyFrom);
                }
                contractList = contractService.getContractListByUser(currentUser);
                //移除标记为已完成的设备合同
                List<ContractInfo> completeEquipmentContracts = contractList.stream()
                        .filter(item -> (ContractInfo.ContractType.Equipment.toString().equals(item.getContractType()) &&
                                ContractInfo.ContractStatus.COMPLETE.toString().equals(item.getContractStatus())))
                        .collect(Collectors.toList());
                contractList.removeAll(completeEquipmentContracts);

            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetArrivedListEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String listId = (String) UrlEncryptController.getValueFromMap(encodeStr, "listId");
            this.selected = dao.findById(listId);
            arrivalItemList = itemDao.findByArrivalListId(selected.getId());
            selectedOrg = aiService.getOrgInfoByUid(selected.getSiteUID());
            if (selected.getSupplierId() != null) {
                supplier = aiService.getSupplierById(selected.getSupplierId());
                this.supplierUserList = supplierService.getApprovedUserList(selected.getSupplierId());
            }
            if (selected.getManufactureId() != null) {
                manufacture = aiService.getSupplierById(Integer.parseInt(selected.getManufactureId()));
            }
            initEasyInstallItem();
            this.changeFunctionGroup();
            if(activeSupplier() && selected.getContractNum() != null){
                Set<String> ids = new HashSet<>();
                ids.add(selected.getId());
                ids.add(selected.getContractNum());
                attachList = aiService.getPartPictures(ids);
            }else{
                attachList = aiService.getPicList(selected.getId());
            }
            if(null == selected.getBoxInstallationDate()) {
                selected.setBoxInstallationDate(selected.getArrivalDate());
            }
            medicalInstrumentsClassificationCompCtrl.onServerEvent(MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, easyInstallItem.getMedicalApparatusClassification());
            prepareEdit();
        }

        customConfig = ccService.getCurrentCustomConfig(currentUser.getSiteUID());
        suppliersList = aiService.getSupplierListBySiteUid(currentUser.getSiteUID());
        maintainerList = aiService.getOnlyMaintainListBySiteUid(UserContextService.getCurrentUserAccount().getTenantUID());
        manueList=aiService.getOnlyManuListBySiteUid(UserContextService.getCurrentUserAccount().getTenantUID());

        needInstorageCheck = userContextService.needInstorageCheck();
    }

    @Override
    protected ArrivalListRepository getDAO() {
        return dao;
    }

    @Override
    public void onBeforeNewObject(ArrivalList object) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        object.setCreatorUID(user.getUid());
        object.setStatus(1);
        //remove default value
//        object.setProcessType(ArrivalList.ProcessType.Normal.toString());
        object.setAgent(user.getName());
        object.setArrivalDate(new Date());
        if(activeSupplier() && null == object.getBoxInstallationDate()) {
            object.setBoxInstallationDate(object.getArrivalDate());
        }
        selectedOrg = aiService.getOrgInfoByUid(user.getSiteUID());
        object.setTenantUID(user.getTenantUID());

    }

    public void applyChange() {
        setCopyFrom("");
        onSupplierChange();
        this.save();
    }

    @Override
    public void onBeforeSave(ArrivalList object) {
        object.setTenantUID(selectedOrg.getTenantUID());
        object.setInstitutionUID(selectedOrg.getInstitutionUID());
        object.setHospitalUID(selectedOrg.getHospitalUID());
        object.setSiteUID(selectedOrg.getSiteUID());
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void delete() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            WebUtil.addErrorMessage("到货清单已经提交,不能删除");
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.arrivalItemList = null;
    }

    public void submitArrivalList() {
        if (!Strings.isNullOrEmpty(selected.getContractItemId())) {
            ContractInfoItem contractInfoItem = contractService.getContractItem(selected.getContractItemId());
            if (selected.getQuantity() > contractInfoItem.getContractAssetQuantity()) {
                WebUtil.addErrorMessage("到货数量多于合同数量!");
                return;
            }
        }
        saveArrivalList();
        if (aiService.submitArrivalList(selected.getId())) {
            WebUtil.addSuccessMessage("到货清单已经提交");
        } else {
            WebUtil.addErrorMessage("到货清单提交失败");
        };

        // YHAAO-16616 到货清单提交后，展开的信息要根据上次记录带入
        LinkedHashMap<String, Object> configMap = serviceRequestApiService.getConfigByKeyAndSite("arrivalListAutoFill", selected.getSiteUID());
        LinkedHashMap<String, Object> customerConfig = (LinkedHashMap<String, Object>) configMap.get("customerConfig");

        if(null != customerConfig && "true".equals(customerConfig.get("actualValue"))){
            autoFillInfo();
        }

        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        initEasyInstallItem();
        selected = dao.findById(selected.getId());
        if (!Strings.isNullOrEmpty(selected.getContractItemId())) {
            contractService.contractItemArrival(arrivalItemList);
        }
        if(userContextService.activeSupplier() && selected.getContractNum() != null){
            Set<String> ids = new HashSet<>();
            ids.add(selected.getId());
            ids.add(selected.getContractNum());
            attachList = aiService.getPartPictures(ids);
        }
        setCopyFrom("");
    }

    public void closecopyForm() {
        setCopyFrom("");
    }

    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
        if (null != selectedOrg) {
            customConfig = ccService.getCurrentCustomConfig(selectedOrg.getSiteUID());
        }
    }

    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public void prepareEasyInstall() {
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        initEasyInstallItem();
        installCount = 1;
        generatePmorder = null;

        if (generateMap == null) {
            generateMap = new HashMap();
        } else {
            generateMap.clear();
        }
        saveEasyItem();

    }

    public String gennerateFinancingNum(ArrivalListItem item, String prifix) {
        if (prifix != null) {
            if (prifix.length() > 3) {
                prifix = prifix.substring(0, 3);
            }
        } else {
            prifix = "";
        }

        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(null,
                item.getNationalStandardCode().split("/")[0], item.getClinicalDeptUID() == null ? item.getArrivalList().getSiteUID() : item.getClinicalDeptUID(), item.getParentAssetId(), prifix);
        return financingNum;
    }

    public void generateFinacialNum() {
        String financingNum = gennerateFinancingNum(easyInstallItem, easyInstallItem.getFinancingNum());
        if (null != financingNum && financingNum.startsWith("error:")) {
            WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            financingNum = "";
        }
        easyInstallItem.setFinancingNum(financingNum);
    }

    public void renewQrcode() {
        if (null != easyInstallItem) {
            easyInstallItem.setQrCode(aiService.getNewQrcode(easyInstallItem.getArrivalList().getTenantUID(), easyInstallItem.getArrivalList().getSiteUID()).getQrCode());
        }
    }

    public void cancelSaveEasyInstall() {
        initEasyInstallItem();
    }

    private void initEasyInstallItem() {
        easyInstallItem = getNewInstallAssetList().stream().findAny().orElse(new ArrivalListItem());
        String maitananceName = easyInstallItem.getMaitanance();
        if (maitananceName != null) {
            maitanance = new Supplier();
            maitanance.setName(maitananceName);
        }
        nationalStandardCode = easyInstallItem.getNationalStandardCode();
    }

    public List<UserAccount> getOwnerList() {
        return aiService.getWorkerList();
    }

    public List<UserAccount> getClinicalOwnerList() {
        if (null != easyInstallItem) {
            return aiService.getClinicalOwnerList(easyInstallItem.getClinicalDeptUID());
        }
        return null;
    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.easyInstallItem.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.easyInstallItem.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    public void saveEasyItem() {
        easyInstallItem.setNationalStandardCode(nationalStandardCode == null ? "" : nationalStandardCode.split("/")[0]);
        List<ArrivalListItem> saveList = getNewInstallAssetList();
        saveList.forEach(item -> {
            item.setNationalStandardCode(easyInstallItem.getNationalStandardCode());
            item.setFunctionGroup(easyInstallItem.getFunctionGroup());
            item.setAssetCode68(easyInstallItem.getAssetCode68());
            item.setRegistrationNo(easyInstallItem.getRegistrationNo());
            item.setMaitananceId(easyInstallItem.getMaitananceId());
            item.setMaitanance(easyInstallItem.getMaitanance());
            item.setPurchaseDate(easyInstallItem.getPurchaseDate());
            item.setWarrantyDate(easyInstallItem.getWarrantyDate());
            item.setPurchasePrice(easyInstallItem.getPurchasePrice());
            item.setAssetUsage(easyInstallItem.getAssetUsage());
            item.setCapitalSource(easyInstallItem.getCapitalSource());
            item.setMeasureGrade(easyInstallItem.getMeasureGrade());
            item.setRiskGrade(easyInstallItem.getRiskGrade());
            item.setEmergencyEquipment(easyInstallItem.getEmergencyEquipment());
            item.setImportEquipment(easyInstallItem.getImportEquipment());
            item.setMeasuringItemCategory(easyInstallItem.getMeasuringItemCategory());
            item.setAccountingCategory(easyInstallItem.getAccountingCategory());
            item.setSalvageValue(easyInstallItem.getSalvageValue());
            item.setLifecycleInYear(easyInstallItem.getLifecycleInYear());
            item.setDepreciationMethod(easyInstallItem.getDepreciationMethod());
            item.setIsPm(easyInstallItem.getIsPm());
            item.setIsChecked(easyInstallItem.getIsChecked());
            item.setWarrantyYear(easyInstallItem.getWarrantyYear());
            item.setMedicalApparatusClassification(easyInstallItem.getMedicalApparatusClassification());
            if(item.getFixassetAckStatus() == null){
                item.setFixassetAckStatus(ArrivalListItem.ACK_STATUS_NONE);
            }
        });
        itemDao.save(saveList);
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        initEasyInstallItem();
        WebUtil.addSuccessMessage("已保存");
    }

    public List<ArrivalListItem> getNewInstallAssetList() {
        if (null != arrivalItemList) {
            return arrivalItemList.stream().filter(item -> item.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)).collect(Collectors.toList());
        } else {
            return new ArrayList();
        }
    }

    public void takeWorkOrder(String itemId) {
        ArrivalListItem item = itemDao.findById(itemId);
        item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
        aiService.reAssignWorkOrder(item);
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
    }

    public void takeWorkOrderBatch(String itemId) {
        ArrivalListItem item = itemDao.findById(itemId);
        String contractNum = item.getArrivalList().getContractNum();
        String clinicalDeptUID = item.getClinicalDeptUID();
        String errorMessage = "";
        if(StringUtils.isEmpty(contractNum) || StringUtils.isEmpty(clinicalDeptUID)){
            item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
            Boolean responseStatus = aiService.reAssignWorkOrder(item);
            if(!responseStatus){
                errorMessage = "记录提交失败";
            }
        }else{
            List<ArrivalListItem> itemList = itemDao.getArrivalListItem(contractNum,clinicalDeptUID,"new");
            for(ArrivalListItem arrivalListItem:itemList){
                arrivalListItem.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
                Boolean responseStatus = aiService.reAssignWorkOrder(arrivalListItem);
                if(!responseStatus){
                    String name = arrivalListItem.getArrivalList().getName();
                    if(errorMessage.contains("到货单名称/编号："+name)){
                        errorMessage = errorMessage + "、"+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->记录提交失败";
                    }else{
                        errorMessage = errorMessage + "到货单名称/编号：" + name +","+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->记录提交失败";
                    }
                }
            }
        }
        if(StringUtils.isEmpty(errorMessage)){
            WebUtil.addSuccessMessage("接单成功");
        }else{
            WebUtil.addErrorMessage(errorMessage);
        }
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        if (gbCode == null || gbCode.getId() == -1) {
            nationalStandardCode = "";
        } else {
            nationalStandardCode = gbCode.getCode() + "/" + gbCode.getName();
        }
    }

    private void copyFiledsValueById(String copyFrom) {
        ArrivalList srcList = dao.findById(copyFrom);
        if (null != srcList) {
            selected.setHospitalUID(srcList.getHospitalUID());
            selected.setAssetName(srcList.getAssetName());
            selected.setAssetGroup(srcList.getAssetGroup());
            selected.setFunctionType(srcList.getFunctionType());
            //制造商
            selected.setManufacture(srcList.getManufacture());
            selected.setOrigin(srcList.getOrigin());
            //供应商
            selected.setVendor(srcList.getVendor());
            selected.setTenantUID(srcList.getTenantUID());
            selected.setInstitutionUID(srcList.getInstitutionUID());
            selected.setHospitalUID(srcList.getHospitalUID());
            selected.setSiteUID(srcList.getSiteUID());
            //设置邵逸夫医院对哪些字段不可见 ContractNum  合同号   contractPrice  合同价格  agent 经办人  ArrivalDate 到货日期
            //制造商、品牌/制造商简称、供应商
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            String tenantUid = currentUserAccount.getTenantUID();
            List<TenantInfo> tanentIfos = tenantInfoRepository.findByCustomTenant("3");
            String tanentInfoUid = tanentIfos.isEmpty() ? null : tanentIfos.get(0).getUid();
            if(!tenantUid.equals(tanentInfoUid)){
                selected.setContractNum(srcList.getContractNum());
                selected.setContractPrice(srcList.getContractPrice());
                selected.setAgent(srcList.getAgent());
                selected.setArrivalDate(srcList.getArrivalDate());
                selected.setInvoiceNum(srcList.getInvoiceNum());
            }else{
                selected.setContractNum("");
                selected.setContractPrice(null);
                selected.setAgent("");
                selected.setArrivalDate(null);
                selected.setInvoiceNum("");
                selected.setProcessType(srcList.getProcessType());
                selected.setMakerShortName(srcList.getMakerShortName());
                String manufactureUid=srcList.getManufactureId();
                if(StringUtils.isNotEmpty(manufactureUid)){
                    Supplier manufacture=aiService.getSupplierById(Integer.valueOf(manufactureUid));
                    if(manufacture!=null){
                        this.selected.setManufacture(manufacture.getName());
                        this.selected.setManufactureId(manufacture.getId().toString());
                        this.manufacture=manufacture;
                    }
                }
                Integer supplierId = srcList.getSupplierId();
                if(supplierId != null ){
                    Supplier supplier=aiService.getSupplierById(supplierId);
                    if(supplier!=null){
                        this.selected.setVendor(supplier.getName());
                        this.selected.setSupplierId(supplier.getId());
                        this.supplier=supplier;
                    }
                }
            }
        }

    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void removeAttachmentByIdJiuYuan(String blobId) {
        String msg = isEditFileJiuYuan();
        if(msg != null){
            WebUtil.addErrorMessage(msg);
            return;
        }
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategories() {
        if(selectedOrg==null) Collections.emptyList();
        return configService.getAttachmentCategories(configService.findConfigBySite(selectedOrg.getSiteUID()));
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        String type = event.getComponent().getId();
        type = type.substring(type.indexOf("type") + 4);
        if (null != blobObject) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ArrivalList);
            blobObject.setBoUuid(selected.getId());
            if(activeSupplier()){//已激活的供应商：使用合同号作为BoUuid
                blobObject.setBoUuid(selected.getContractNum());
            }
            blobObject.setBoSubType(type);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadJiuYuan(FileUploadEvent event) {
        String msg = isEditFileJiuYuan();
        if(msg != null){
            WebUtil.addErrorMessage(msg);
            return;
        }
        handleFileUpload(event);
    }

    public List<V2_BlobObject> getAttachList(String type) {
        return attachList == null ? null : attachList.stream().filter(item -> item.getBoSubType() == null || item.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public List<String> completeGbName(String prefix) {
        return gbCodeService.completeGbName(prefix);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return aiService.getOrgInfoByUid(orgUid);
    }

    public Integer getInstalledCount() {
        if (arrivalItemList != null) {
            return arrivalItemList.stream().filter(item -> ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getInstallStatus())
                    || ArrivalListItem.INSTALL_STATUS_INSTORAGE.equals(item.getInstallStatus())).collect(Collectors.toList()).size();
        } else {
            return 0;
        }
    }

    public void saveArrivalList() {
        this.save();

        saveRepairerAndRepairerTel();
    }

    public void saveRepairerAndRepairerTel(){
        if(activeSupplier() && this.selected.getStatus() > 1) {
            String arrivalListId = this.selected.getId();
            List<ArrivalListItem> arrivalListItems = itemDao.findByArrivalListId(arrivalListId);
            if(arrivalListItems.size() > 0){
                String repairerInfo = selected.getRepairer();
                if(StringUtils.isNotEmpty(repairerInfo) && StringUtils.isNotEmpty(selected.getRepairerTel())){
                    repairerInfo = repairerInfo +"-"+ selected.getRepairerTel();
                    String comments = arrivalListItems.get(0).getComments() == null ? "" : arrivalListItems.get(0).getComments();
                    String[] commentsSplit = comments.split("\n");//保存规则：维修人-电话'\n'其他评论信息
                    if(commentsSplit[0].contains("-")){
                        comments = !repairerInfo.equals(commentsSplit[0]) ? comments.replaceFirst(commentsSplit[0],repairerInfo) : comments;
                    }else{
                        comments = repairerInfo +"\n"+ comments;
                    }
                    itemDao.updateComments(comments, arrivalListId);
                    arrivalItemList = itemDao.findByArrivalListId(arrivalListId);
                }
            }
        }
    }

    public List<Supplier> autoCompleteVendor(String query) {
        return autoCompleteService.autoCompleteVendor(query, suppliersList);
    }

    public List<Supplier> autoCompleteMaintainer(String query) {
        return autoCompleteService.autoCompleteMaintainer(query, maintainerList);
    }

    public List<Supplier> autoCompleteManufacture(String query) {
        return autoCompleteService.autoCompleteManufacture(query, manueList);
    }

    public void onSupplierChange() {
        if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());

            this.supplierUserList = supplierService.getApprovedUserList(supplier.getId());
        } else {
            selected.setSupplierId(null);
            selected.setVendor(null);
        }
    }

    public void onSupplierChange2() {
        if (null != supplier) {
            this.supplierUserList = supplierService.getApprovedUserList(supplier.getId());
        }
    }

    public void onManufactureChange() {
        if (null != manufacture) {
            selected.setManufactureId(manufacture.getId().toString());
            selected.setManufacture(manufacture.getName());
        } else {
            selected.setManufactureId(null);
            selected.setManufacture(null);
        }
    }

    public void onMaintainerChange() {
        if (null != maitanance) {
            this.easyInstallItem.setMaitanance(maitanance.getName());
            this.easyInstallItem.setMaitananceId(maitanance.getId() + "");
            this.easyInstallItem.setMaitananceTel(maitanance.getTel());
        } else {
            this.easyInstallItem.setMaitanance(null);
            this.easyInstallItem.setMaitananceId(null);
            this.easyInstallItem.setMaitananceTel(null);
        }
    }

    public void onContractChange() {
        if (!Strings.isNullOrEmpty(selectedContractId)) {
            contractItemList = contractService.getContractItems(selectedContractId).stream()
                    .filter(item -> ContractInfoItem.ContractInfoItemType.Purchase.toString().equals(item.getItemType())).collect(Collectors.toList());
            //TODO 去除到货数量等于订购数量的条目
        } else {
            contractItemList = new ArrayList();
            selectedContractItemId = null;
        }
    }

    public void finishChoseContractItem() {
//        if(copyFrom.equals("")){
            ContractInfoItem selectedContractItem = contractItemList.stream().filter(item -> item.getId().equals(selectedContractItemId)).findAny().orElse(null);
            this.selected.setContractItemId(selectedContractItem.getId());
            this.selected.setAssetName(selectedContractItem.getContractAssetName());
            this.selected.setContractNum(selectedContractItem.getContractInfo().getContractNum());
            if(selectedContractItem.getContractAssetQuantity() != null && selectedContractItem.getContractAssetPrice() != null){
                this.selected.setContractPrice(selectedContractItem.getContractAssetQuantity() * selectedContractItem.getContractAssetPrice());
            }else {
                this.selected.setContractPrice(0d);
            }
//        this.selected.setContractPrice(selectedContractItem.getContractInfo().getAmount());
            this.selected.setQuantity(selectedContractItem.getContractAssetQuantity());
            this.selected.setSiteUID(selectedContractItem.getContractInfo().getSiteUID());
            this.selected.setHospitalUID(selectedContractItem.getContractInfo().getHospitalUID());
            this.selected.setInstitutionUID(selectedContractItem.getContractInfo().getInstitutionUID());
            this.selected.setTenantUID(selectedContractItem.getContractInfo().getTenantUID());
            this.selected.setFunctionType(selectedContractItem.getPattern());
            String manufactureUid=selectedContractItem.getManufacturer();
            if(StringUtils.isNotEmpty(manufactureUid)){
                Supplier manufacture=aiService.getSupplierByUid(manufactureUid);
                if(manufacture!=null){
                    this.selected.setManufacture(manufacture.getName());
                    this.selected.setManufactureId(manufacture.getId().toString());
                    this.manufacture=manufacture;
                }
            }
            this.selected.setQuantity(selectedContractItem.getContractAssetQuantity());
            this.selected.setContractNum(selectedContractItem.getContractInfo().getContractNum());
            this.selected.setOrigin(selectedContractItem.getProductionPlace());
            String supplierUid=selectedContractItem.getContractInfo().getSupplierUid();
            if(StringUtils.isNotEmpty(supplierUid)){
                Supplier supplier=aiService.getSupplierByUid(supplierUid);
                if(supplier!=null){
                    this.selected.setVendor(supplier.getName());
                    this.selected.setSupplierId(supplier.getId());
                    this.supplier=supplier;

                    this.supplierUserList = supplierService.getApprovedUserList(selected.getSupplierId());
                }
            }
            selectedOrg = aiService.getOrgInfoByUid(selected.getSiteUID());
//        }else{
//            ContractInfoItem selectedContractItem = contractItemList.stream().filter(item -> item.getId().equals(selectedContractItemId)).findAny().orElse(null);
//            this.selected.setContractItemId(selectedContractItem.getId());
//            this.selected.setAssetName(selectedContractItem.getContractAssetName());
//            this.selected.setContractNum(selectedContractItem.getContractInfo().getContractNum());
//            //ContractNum  合同号   contractPrice  合同价格  agent 经办人  ArrivalDate 到货日期
//            //Manufacture  制造商   Vendor  供应商    品牌/制造商简称  MakerShortName
//            //如果是邵逸夫医院，将制造商添加进去
//            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
//            String tenantUid = currentUserAccount.getTenantUID();
//            List<TenantInfo> tanentIfos = tenantInfoRepository.findByCustomTenant("3");
//            String tanentInfoUid = tanentIfos.get(0).getUid();
//            if(!tenantUid.equals(tanentInfoUid)){
//                this.selected.setContractNum(selectedContractItem.getContractInfo().getContractNum());
//                if(selectedContractItem.getContractAssetQuantity() != null && selectedContractItem.getContractAssetPrice() != null){
//                    this.selected.setContractPrice(selectedContractItem.getContractAssetQuantity() * selectedContractItem.getContractAssetPrice());
//                }else {
//                    this.selected.setContractPrice(0d);
//                }
//            }
//
////        this.selected.setContractPrice(selectedContractItem.getContractInfo().getAmount());
//            this.selected.setQuantity(selectedContractItem.getContractAssetQuantity());
//            this.selected.setSiteUID(selectedContractItem.getContractInfo().getSiteUID());
//            this.selected.setHospitalUID(selectedContractItem.getContractInfo().getHospitalUID());
//            this.selected.setInstitutionUID(selectedContractItem.getContractInfo().getInstitutionUID());
//            this.selected.setTenantUID(selectedContractItem.getContractInfo().getTenantUID());
//            this.selected.setFunctionType(selectedContractItem.getPattern());
//            String manufactureUid=selectedContractItem.getManufacturer();
//            if(StringUtils.isNotEmpty(manufactureUid)){
//                Supplier manufacture=aiService.getSupplierByUid(manufactureUid);
//                if(manufacture!=null){
//                    this.selected.setManufacture(manufacture.getName());
//                    this.selected.setManufactureId(manufacture.getId().toString());
//                    this.manufacture=manufacture;
//                }
//            }
//            this.selected.setQuantity(selectedContractItem.getContractAssetQuantity());
//            this.selected.setOrigin(selectedContractItem.getProductionPlace());
//            String supplierUid=selectedContractItem.getContractInfo().getSupplierUid();
//            if(StringUtils.isNotEmpty(supplierUid)){
//                Supplier supplier=aiService.getSupplierByUid(supplierUid);
//                if(supplier!=null){
//                    this.selected.setVendor(supplier.getName());
//                    this.selected.setSupplierId(supplier.getId());
//                    this.supplier=supplier;
//                    //如果是邵逸夫医院，将制造商添加进去
//                    if(tenantUid.equals(tanentInfoUid)){
//                        this.selected.setManufacture(supplier.getSupplierCode());
//                        this.selected.setMakerShortName(supplier.getSupplierAbbreviation());
//                    }
//
//                    this.supplierUserList = supplierService.getApprovedUserList(selected.getSupplierId());
//                }
//            }
//            selectedOrg = aiService.getOrgInfoByUid(selected.getSiteUID());
//        }
    }

    public void showImageZoom(String objectStorageId, String objctName){
        this.imageZoom = objectStorageId;
        this.imageName = objctName;
    }

    public boolean activeSupplier(){
        return supplierService.activeSupplier();
    }

    //geter and setter
    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<ArrivalListItem> getArrivalItemList() {
        return arrivalItemList;
    }

    public void setArrivalItemList(List<ArrivalListItem> arrivalItemList) {
        this.arrivalItemList = arrivalItemList;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Supplier getMaitanance() {
        return maitanance;
    }

    public void setMaitanance(Supplier maitanance) {
        this.maitanance = maitanance;
    }

    public ArrivalListItem getEasyInstallItem() {
        return easyInstallItem;
    }

    public void setEasyInstallItem(ArrivalListItem easyInstallItem) {
        this.easyInstallItem = easyInstallItem;
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public void setCode68List(List<I18nMessage> code68List) {
        this.code68List = code68List;
    }

    public Integer getInstallCount() {
        return installCount;
    }

    public void setInstallCount(Integer installCount) {
        this.installCount = installCount;
    }

    public Map<String, Boolean> getGenerateMap() {
        return generateMap;
    }

    public void setGenerateMap(Map<String, Boolean> generateMap) {
        this.generateMap = generateMap;
    }

    public String getNationalStandardCode() {
        return nationalStandardCode;
    }

    public void setNationalStandardCode(String nationalStandardCode) {
        this.nationalStandardCode = nationalStandardCode;
    }

    public TreeNode getGbTree() {
        if (gbTree == null) {
            TreeNode treeNode = gbCodeService.buildGbTree();
            gbTree = new DefaultTreeNode("root", null, null);
            gbTree.getChildren().add(treeNode);
            treeNode.setExpanded(true);
        }
        return gbTree;
    }

    public Boolean getGeneratePmorder() {
        return generatePmorder;
    }

    public void setGeneratePmorder(Boolean generatePmorder) {
        this.generatePmorder = generatePmorder;
    }

    public boolean isNeedInstorageCheck() {
        return needInstorageCheck;
    }

    public void setNeedInstorageCheck(boolean needInstorageCheck) {
        this.needInstorageCheck = needInstorageCheck;
    }

    public String getSelectedContractItemId() {
        return selectedContractItemId;
    }

    public void setSelectedContractItemId(String selectedContractItemId) {
        this.selectedContractItemId = selectedContractItemId;
    }

    public String getSelectedContractId() {
        return selectedContractId;
    }

    public void setSelectedContractId(String selectedContractId) {
        this.selectedContractId = selectedContractId;
    }

    public List<ContractInfo> getContractList() {
        return contractList;
    }

    public void setContractList(List<ContractInfo> contractList) {
        this.contractList = contractList;
    }

    public List<ContractInfoItem> getContractItemList() {
        return contractItemList;
    }

    public void setContractItemList(List<ContractInfoItem> contractItemList) {
        this.contractItemList = contractItemList;
    }

    public Supplier getManufacture() {
        return manufacture;
    }

    public void setManufacture(Supplier manufacture) {
        this.manufacture = manufacture;
    }

    public List<UserAccount> getSupplierUserList() {
        return supplierUserList;
    }

    public void setSupplierUserList(List<UserAccount> supplierUserList) {
        this.supplierUserList = supplierUserList;
    }

    public CustomConfigObject getCustomConfig() {
        return customConfig;
    }

    public String getImageZoom() {
        return imageZoom;
    }

    public void setImageZoom(String imageZoom) {
        this.imageZoom = imageZoom;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void updateInvoiceNum(){
        if(selected != null && selected.getId() != null){
            if(selected.getInvoiceNum() == null){
                WebUtil.addErrorMessage("请输入发票号码");
                return;
            }
            dao.updateInvoiceNum(selected.getInvoiceNum(),selected.getId());
            arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        }
    }

    public void updateSupplierUser(){
        if(selected != null && selected.getId() != null){
            if(selected.getSupplierUserId() == null){
                WebUtil.addErrorMessage("请选择供应商责任人");
                return;
            }
            dao.updateSupplierUser(selected.getVendor(),selected.getSupplierId(),selected.getSupplierUserId(),selected.getId(),3);
            arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        }
    }

    public void updateIsEdit(){
        dao.updateIsEdit(selected.getIsEdit(),selected.getId());
    }

    public void saveNewSupplier() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        this.newSupplier.setSiteId(loginUser.getSiteId());
        this.newSupplier.setHospitalId(loginUser.getHospitalId());
        this.newSupplier.setTenantUID(loginUser.getTenantUID());
        this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
        this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
        this.newSupplier.setSiteUID(loginUser.getSiteUID());
        this.newSupplier.setCreatedDate(new Date());
        this.newSupplier.setCreatedBy(loginUser.getName());
        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();

/*        if("manufacturer".equals(newSupplierType)){
            manueList.add(newSupplier);
        }else if("supplier".equals(newSupplierType)){
            suppliersList.add(newSupplier);
        }else if("maintainer".equals(newSupplierType)){
            maintainerList.add(newSupplier);
        }*/
        if(newSupplier.getManufacturerT()){
            manueList.add(newSupplier);
        }
        if(newSupplier.getSupplierT()){
            suppliersList.add(newSupplier);
        }
        if(newSupplier.getMaintainerT()){
            maintainerList.add(newSupplier);
        }
//        supplierList.add(newSupplier);
//        if (org.apache.commons.lang.StringUtils.isNotBlank(newSupplier.getUid()) && !supplierList.contains(newSupplier)) {
//            supplierList.add(newSupplier);
//        }
//        changeSelect(createTpye);

    }

    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        newSupplierType = type;
        if("manufacturer".equals(type)){
            newSupplier.setManufacturerT(true);
        }
        if("supplier".equals(type)){
            newSupplier.setSupplierT(true);
        }
        if("maintainer".equals(type)){
            newSupplier.setMaintainerT(true);
        }

    }

    public boolean verifyFileSuffixName(String fileName,String fileSuffixName){
        String[] fileSuffixNames = fileSuffixName.toUpperCase().split("\\|");
        for(int i=0;i<fileSuffixNames.length;i++){
            if(fileName.toUpperCase().endsWith(fileSuffixNames[i])){
                return true;
            }
        }
        return false;
    }

    public void saveDistributeSupplier(Map<String, Boolean> selectedMap){
        List<String> selectedIds = new ArrayList<>();
        selectedMap.keySet().stream().forEach(id -> { if(selectedMap.get(id)) selectedIds.add(id); });
        dao.updateBatchSupplier(supplier.getName(),supplier.getId(),installCount,selectedIds);
    }

    /**
     * 验证九院文件是否可以编辑包括：（上传）
     * 注意条件：九院同院区下并且同合同号的所有到货单已完成安装才可以编辑文件。
     * @return
     */
    public String isEditFileJiuYuan(){
        String msg = null;
        if(activeSupplier()){
            List<ArrivalList> arrivalLists = dao.findByContractNumAndSiteUID(selected.getContractNum(),selected.getSiteUID());
            for(ArrivalList arrivalList:arrivalLists){
                if(arrivalList.getStatus() != 3){
                    msg = "该合同号下有未安装完成的到货单："+ arrivalList.getName() +"，请全部完成后再上传！";
                }
            }
        }
        return msg;
    }

    public String getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        if (MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE.equals(eventName)) {
            SysAuthGroup selectedAuthGroup = (SysAuthGroup) eventObject;
            easyInstallItem.setMedicalApparatusClassification(selectedAuthGroup.getClassificationCode());
        }
    }

    private void autoFillInfo(){

        ArrivalListItem lastItem = itemDao.getLastItemByNameAndModel(this.selected.getAssetName(),
                StringUtils.isNotEmpty(this.selected.getFunctionType()) ? this.selected.getFunctionType() : "", this.selected.getTenantUID(), this.selected.getId());

        if(lastItem != null){
            List<ArrivalListItem> list = itemDao.findByArrivalListId(selected.getId());
            for (ArrivalListItem item : list) {
                item.setNationalStandardCode(lastItem.getNationalStandardCode());
                item.setMedicalApparatusClassification(lastItem.getMedicalApparatusClassification());
                item.setRegistrationNo(lastItem.getRegistrationNo());
                item.setMaitananceId(lastItem.getMaitananceId());
                item.setMaitanance(lastItem.getMaitanance());
                item.setWarrantyYear(lastItem.getWarrantyYear());
                item.setWarrantyDate(lastItem.getWarrantyDate());
                item.setAssetUsage(lastItem.getAssetUsage());
                item.setCapitalSource(lastItem.getCapitalSource());
                item.setMeasureGrade(lastItem.getMeasureGrade());
                item.setRiskGrade(lastItem.getRiskGrade());
                item.setEmergencyEquipment(lastItem.getEmergencyEquipment());
                item.setImportEquipment(lastItem.getImportEquipment());
                item.setMeasuringItemCategory(lastItem.getMeasuringItemCategory());
                item.setAccountingCategory(lastItem.getAccountingCategory());
                item.setSalvageValue(lastItem.getSalvageValue());
                item.setLifecycleInYear(lastItem.getLifecycleInYear());
                item.setDepreciationMethod(lastItem.getDepreciationMethod());
                item.setIsPm(lastItem.getIsPm());
                item.setIsChecked(lastItem.getIsChecked());
                itemDao.save(item);
            }
        }
    }
}
