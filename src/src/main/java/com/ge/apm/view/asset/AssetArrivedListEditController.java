package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AssetClassification32Enum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private String qrCodePrefix;

    @Getter
    @Setter
    private Supplier newSupplier;

    @Getter
    @Setter
    private String newSupplierType;

    private UserContextService userContextService;
    private TenantInfoRepository tenantInfoRepository;
    private List<OrgInfo> deptInfoList;
    private OrgInfoRepository orgDao;

    private  QrCodeLibRepository qrCodeLibRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private MasterDataService masterDataService;
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
        deptInfoList = uaaService.getClinicalDeptListBySiteUID(currentUser.getSiteUID());
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        qrCodeLibRepository = WebUtil.getBean(QrCodeLibRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                String copyFrom = (String) UrlEncryptController.getValueFromMap(encodeStr, "copyFrom");
                prepareCreate();
                // 添加默认科室
                String stockDeptUid = Optional.ofNullable(configItemValueRepository.findFirstByKeyAndSiteUid("setAssetOutStockDept", currentUser.getSiteUID())).map(ConfigItemValue::getValue).orElse("");
                if (ObjectUtil.isNotEmpty(stockDeptUid)) {
                    String deptName = deptInfoList.stream().filter(item -> item.getUid().equals(stockDeptUid)).findFirst().map(OrgInfo::getName).orElse(null);
                    selected.setClinicalDeptUID(stockDeptUid);
                    selected.setClinicalDeptName(deptName);
                }
                if (!Strings.isNullOrEmpty(copyFrom)) {
                    copyFiledsValueById(copyFrom);
                    setCopyFrom(copyFrom);
                }
                contractList = contractService.getContractListByUser(currentUser);
                //移除标记为已完成的设备合同
                List<ContractInfo> completeEquipmentContracts = contractList.stream()
                        .filter(item -> (ContractInfo.ContractType.Equipment.toString().equals(item.getContractType()) &&
                                ContractInfo.ContractStatus.COMPLETE.toString().equals(item.getContractStatus())) ||
                                //移除网页版供应商新建或退回的合同
                                ListUtil.newArrayList(1, 3).contains(item.getStatus()))
                        .collect(Collectors.toList());
                contractList.removeAll(completeEquipmentContracts);

            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetArrivedListEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String listId = (String) UrlEncryptController.getValueFromMap(encodeStr, "listId");
            this.selected = dao.findById(listId);
            if(selected.getAssetClassification32()!=null){
                selectItemDto=new SelectItemDto(selected.getAssetClassification32(),null);
            }
            arrivalItemList = itemDao.findByArrivalListId(selected.getId());
            selectedOrg = aiService.getOrgInfoByUid(selected.getSiteUID());
            deptInfoList = uaaService.getClinicalDeptListBySiteUID(selected.getSiteUID());

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
        if(Boolean.TRUE.equals(selected.getEnableGenerateQrcode())  &&  StringUtils.isNotEmpty(qrCodePrefix) && qrCodePrefix.length()>3  ){
            WebUtil.addErrorMessage("二维码前缀过长!");
            return;
        }

        if (!Strings.isNullOrEmpty(selected.getContractItemId())) {
            ContractInfoItem contractInfoItem = contractService.getContractItem(selected.getContractItemId());
            if (selected.getQuantity() > contractInfoItem.getContractAssetQuantity()) {
                WebUtil.addErrorMessage("到货数量多于合同数量!");
                return;
            }
        }
        WorkflowConfig config = configService.findConfigBySite(selectedOrg.getSiteUID());
        if(config==null){
            WebUtil.addErrorMessage("找不到对应配置!");
            return;
        }
        saveArrivalList();
        if(Boolean.TRUE.equals(selected.getEnableGenerateQrcode()) &&  !StringUtils.isEmpty(config.getAssetCodeConfig()) && "DFQ".equals(config.getAssetCodeConfig())){
            String financingNum = assetFinacingNumGenerateService.batchAutoGenerateFinacingNum(null,
                    null, selectedOrg.getSiteUID() , null, qrCodePrefix,selected.getId(),selected.getQuantity());
            if(null == financingNum ){
                return;
            }
            if (financingNum.startsWith("error:")) {
                WebUtil.addErrorMessage(financingNum.replace("error:", ""));
                return;
            }
        }
        if (aiService.submitArrivalList(selected.getId())) {
            WebUtil.addSuccessMessage("到货清单已经提交");
        } else {
            WebUtil.addErrorMessage("到货清单提交失败");
            return;
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
        // 安装时添加
        /*if (!Strings.isNullOrEmpty(selected.getContractItemId())) {
            contractService.contractItemArrival(arrivalItemList);
        }*/
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
            deptInfoList = uaaService.getClinicalDeptListBySiteUID(selectedOrg.getUid());
        }

        clearClinicalDept();
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
            return aiService.getClinicalOwnerList(ListUtil.newArrayList(easyInstallItem.getClinicalDeptUID()));
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
        if (enableArrivalListRequired()) {
            List<V2_BlobObject> asset4 = attachList.stream().filter(t -> "asset4".equals(t.getBoSubType())).collect(Collectors.toList());
            if (ObjectUtil.isEmpty(asset4)) {
                WebUtil.addSuccessMessage("验收及其他材料必填");
                return;
            }
        }
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

    public void reName(String blobId, String name) {

        attachList.forEach(v -> {
            if (v.getId().equals(blobId)) {
                v.setObjectName(name);
                blobService.saveBlobObject(v);
            }
        });
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
            if(activeSupplier() && StringUtils.isNotEmpty(selected.getContractNum())){//已激活的供应商：使用合同号作为BoUuid
                blobObject.setBoUuid(selected.getContractNum());
            }
            blobObject.setBoSubType(type);
            blobService.saveBlobObject(blobObject);
            try {
                blobService.sendImageCompressMQ(blobObject);
//                if(blobObject.getObjectName().endsWith(".pdf")){
//                    V2_BlobObject v2BlobObject = blobService.compressByObjectId(blobObject.getObjectStorageId());
//                    if(v2BlobObject!=null){
//                        blobObject.setCompressObjectId(v2BlobObject.getObjectStorageId());
//                        blobService.saveBlobObject(blobObject);
//                    }
//                }else{
//                    blobService.sendImageCompressMQ(blobObject);
//                }
            }catch (Exception e){
                logger.error("到货安装，图片生成缩略图MQ发生失败：", e);
            }
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

    public Boolean isReadOnly() {
        Boolean result = false;
        if(selected!=null) {
            if(getInstalledCount() > 0 && selected.getContractItemId()!=null) { //至少有一个安装项目已经开始安装，并且绑定了合同的，不能编辑该字段
                result = true;
            }
        }
        return result;
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
                    .filter(item -> {
                                if(item.getContractAssetQuantity()!=null) {
                                    Integer installedSum = dao.sumQuantityByContractItemId(item.getId());
                                    if(installedSum == null) installedSum = 0;
                                    item.setNoInstallQuntity(item.getContractAssetQuantity()-installedSum > 0 ? item.getContractAssetQuantity()-installedSum : 0);
                                } else {
                                    item.setNoInstallQuntity(0);
                                }
                                return ContractInfoItem.ContractInfoItemType.Purchase.toString().equals(item.getItemType()) && item.getNoInstallQuntity() > 0;
                      }
                      ).collect(Collectors.toList());
            //TODO 去除到货数量等于订购数量的条目
        } else {
            contractItemList = new ArrayList();
            selectedContractItemId = null;
        }
    }

    public void onQuantityChange() {
        if(StringUtils.isNotEmpty(selectedContractItemId)) {
            ContractInfoItem selectedContractItem = contractItemList.stream().filter(item -> item.getId().equals(selectedContractItemId)).findAny().orElse(null);
            if(selected.getQuantity() > selectedContractItem.getNoInstallQuntity()) {
                WebUtil.addErrorMessage("填写数量超过合同中待安装数量" + selectedContractItem.getNoInstallQuntity() + ",请考虑重新填写");
                return;
            }
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
            this.selected.setQuantity(selectedContractItem.getNoInstallQuntity());
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

    public void onBlur() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        Supplier supplier = supplierDao.getBySiteIdAndName(loginUser.getSiteId(), this.newSupplier.getName());
        if (!ObjectUtils.isEmpty(supplier)) {
            this.newSupplier = supplier;
        } else {
            String name = newSupplier.getName();
            boolean ManufacturerT = newSupplier.getManufacturerT();
            boolean SupplierT = newSupplier.getSupplierT();
            boolean MaintainerT = newSupplier.getMaintainerT();
            prepareCreateSupplier("manufacturer");
            newSupplier.setName(name);
            newSupplier.setSupplierT(SupplierT);
            newSupplier.setMaintainerT(MaintainerT);
            newSupplier.setManufacturerT(ManufacturerT);
        }
    }

    public void saveNewSupplier() {
        if (null == newSupplier.getId()) {
            UserAccount loginUser = UserContextService.getCurrentUserAccount();
            this.newSupplier.setSiteId(loginUser.getSiteId());
            this.newSupplier.setHospitalId(loginUser.getHospitalId());
            this.newSupplier.setTenantUID(loginUser.getTenantUID());
            this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
            this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
            this.newSupplier.setSiteUID(loginUser.getSiteUID());
            this.newSupplier.setCreatedDate(new Date());
            this.newSupplier.setCreatedBy(loginUser.getName());
        }
        if (newSupplier.getManufacturerT()) {
            if (manueList.stream().filter(v -> v.getName().equals(newSupplier.getName())).collect(Collectors.toList()).size() < 1) {
                manueList.add(newSupplier);
            }
        }
        if (newSupplier.getSupplierT()) {
            if (suppliersList.stream().filter(v -> v.getName().equals(newSupplier.getName())).collect(Collectors.toList()).size() < 1) {
                suppliersList.add(newSupplier);
            }
        }
        if (newSupplier.getMaintainerT()) {
            if (maintainerList.stream().filter(v -> v.getName().equals(newSupplier.getName())).collect(Collectors.toList()).size() < 1) {
                maintainerList.add(newSupplier);
            }
        }
        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();
    }
    @Getter
    @Setter
    public String createTpyeName=null;
    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        newSupplierType = type;
        if("manufacturer".equals(type)){
            newSupplier.setManufacturerT(true);
            createTpyeName="制造商信息";
        }
        if("supplier".equals(type)){
            newSupplier.setSupplierT(true);
            createTpyeName="供应商信息";
        }
        if("maintainer".equals(type)){
            newSupplier.setMaintainerT(true);
            createTpyeName="维修商信息";
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

        if(org.apache.commons.lang.StringUtils.isEmpty(this.selected.getAssetName())
                || org.apache.commons.lang.StringUtils.isEmpty(this.selected.getFunctionType())){
            return;
        }

        ArrivalListItem lastItem = itemDao.getLastItemByNameAndModel(this.selected.getAssetName(),
                this.selected.getFunctionType(), this.selected.getTenantUID(), this.selected.getId());

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
            medicalInstrumentsClassificationCompCtrl.onServerEvent(MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, lastItem.getMedicalApparatusClassification());
        }
    }


    public List<String> autoCompleteDept(String query) {
        return deptInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).map(OrgInfo::getName).sorted(new PinYinComparator()).collect(Collectors.toList());
    }



    public void onClinicalDeptChange(SelectEvent event) {
        OrgInfo dept = deptInfoList.stream().filter(item -> item.getName().equals(selected.getClinicalDeptName())).findFirst().orElse(null);
        selected.setClinicalDeptUID(dept.getUid());

    }
    private StreamedContent zipFile;

    public StreamedContent getZipFile() {
        return zipFile;
    }

    public void setZipFile(StreamedContent zipFile) {
        this.zipFile = zipFile;
    }

    private String exportQrCodeTemplateId;

    public String getExportQrCodeTemplateId() {
        return exportQrCodeTemplateId;
    }

    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
    }

    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class);
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }
    public void exportQrCode() throws Exception {
//        Map<String, String> orgNamesMap = orgDao.findByUids(arrivalItemList.stream().filter(item->item.getSiteUid()!=null).map(ArrivalListItem::getSiteUid).distinct().collect(Collectors.toSet()))
//                .stream().collect(Collectors.toMap(OrgInfo::getUid, OrgInfo::getName));
        OrgInfo orgInfo = orgDao.findByUid(selected.getSiteUID());
        Map<String, String> orgNamesMap = new HashMap<>(2);
        orgNamesMap.put(orgInfo.getUid(),orgInfo.getName());

        List<String> list = arrivalItemList.stream().filter(item -> item.getQrCode() != null && !item.getQrCode().isEmpty()).map(ArrivalListItem::getQrCode).collect(Collectors.toList());

        List<SearchFilter> filterFilter =new ArrayList<>();
        filterFilter.add(new SearchFilter("qrCode", SearchFilter.Operator.IN, list));


        List<QrCodeLib> bySearchFilter = qrCodeLibRepository.findBySearchFilter(filterFilter);
        if(CollectionUtils.isEmpty(bySearchFilter)){
            return;
        }
        List<AssetInfo> assetInfos = new ArrayList<AssetInfo>(bySearchFilter.size());
        Map<String, List<ArrivalListItem>> listMap = arrivalItemList.stream().filter(item -> item.getQrCode() != null).collect(Collectors.groupingBy(ArrivalListItem::getQrCode));
        Set<String> clinicalDeptUIDs = arrivalItemList.stream().filter(item -> item.getClinicalDeptUID() != null).map(ArrivalListItem::getClinicalDeptUID).collect(Collectors.toSet());

        List<OrgInfo> orgInfos = orgDao.findInUIds(clinicalDeptUIDs);

        Map<String, String> orgMap = orgInfos.stream().collect(Collectors.toMap(OrgInfo::getUid, o -> o.getName()));
        for (QrCodeLib qrCodeLib : bySearchFilter) {
            AssetInfo assetInfo = new AssetInfo();
            List<ArrivalListItem> arrivalListItems = listMap.get(qrCodeLib.getQrCode());
            if(!CollectionUtils.isEmpty(arrivalListItems)){
                ArrivalListItem arrivalListItem = arrivalListItems.get(0);
                BeanUtils.copyProperties(arrivalListItem,assetInfo);
                assetInfo.setSerialNum(arrivalListItem.getSerialNum());
                assetInfo.setFinancingNum(arrivalListItem.getFinancingNum());
                assetInfo.setDepartNum(arrivalListItem.getDepartNum());
                assetInfo.setName(selected.getAssetName());
                assetInfo.setClinicalDeptName(orgMap.get(arrivalListItem.getClinicalDeptUID()));
                assetInfo.setClinicalDeptUID(arrivalListItem.getClinicalDeptUID());
                assetInfo.setAssetGroup(selected.getAssetGroup());
            }
            assetInfo.setSiteUID(qrCodeLib.getSiteUID());
            assetInfo.setQrCode(qrCodeLib.getQrCode());
            assetInfos.add(assetInfo);
        }
        QrCodeExportStream exportStream = new QrCodeExportStream(assetInfos.stream().filter(item -> item.getQrCode() != null && !item.getQrCode().isEmpty()).collect(Collectors.toList()), exportQrCodeTemplateId,orgNamesMap);
        zipFile = new DefaultStreamedContent(exportStream, "application/x-zip-compressed", "qrCodes.zip");
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        selected.setTenantUID(orgInfo.getTenantUID());
        selected.setInstitutionUID(orgInfo.getInstitutionUID());
        selected.setHospitalUID(orgInfo.getHospitalUID());
        selected.setSiteUID(orgInfo.getSiteUID());
        deptInfoList = uaaService.getClinicalDeptListBySiteUID(orgInfo.getUid());
        clearClinicalDept();
    }

    public void clearClinicalDept() {
        selected.setClinicalDeptName(null);
        selected.setClinicalDeptUID(null);
    }


    public String getQrCodePrefix() {
        return qrCodePrefix;
    }

    public void setQrCodePrefix(String qrCodePrefix) {
        this.qrCodePrefix = qrCodePrefix;
    }

    public String getInstallUrl(String itemId,String srcPage){
        if (enableArrivalListRequired()) {
            List<V2_BlobObject> asset4 = attachList.stream().filter(t -> "asset4".equals(t.getBoSubType())).collect(Collectors.toList());
            if (ObjectUtil.isEmpty(asset4)) {
                return "";
            }
        }
        return UrlEncryptController.encodeUrlParam("ItemEdit?faces-redirect=true&src=".concat(srcPage).concat("&itemId=").concat(itemId));
    }

    public boolean enableArrivalListRequired() {
        return Optional.ofNullable(configItemValueRepository.findFirstByKeyAndSiteUid("enableArrivalListRequired", currentUser.getSiteUID())).
                map(ConfigItemValue::getValue).map(Boolean::parseBoolean).orElse(false);
    }
    private SelectItemDto selectItemDto;


    public  List<SelectItemDto>  autoAssetClassification32() {

        ArrayList<SelectItemDto> selectItemDtos = new ArrayList<>();
        AssetClassificationModel assetClassificationModel=new AssetClassificationModel();
        assetClassificationModel.setBrand(selected.getMakerShortName());
        assetClassificationModel.setPattern(selected.getFunctionType());
        assetClassificationModel.setSourceName(selected.getName());
        assetClassificationModel.setFactory(selected.getManufacture());
        Map<String, Object> resMapByAsset = masterDataService.getResMapByAsset(assetClassificationModel);
        if(resMapByAsset!=null){
            String cat1 = (String) resMapByAsset.get("category");
            String cat2 = (String) resMapByAsset.get("category2");
            String cat3 = (String) resMapByAsset.get("category3");
            if (org.apache.commons.lang.StringUtils.isNotBlank(cat1)) {
                selectItemDtos.add(new SelectItemDto(cat1,toRoundedDouble(resMapByAsset.get("Prob")).toString()));
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(cat2)) {
                selectItemDtos.add(new SelectItemDto(cat2,toRoundedDouble(resMapByAsset.get("Prob2")).toString()));
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(cat3)) {
                selectItemDtos.add(new SelectItemDto(cat3,toRoundedDouble(resMapByAsset.get("Prob3")).toString()));
            }
        }
        List<String> allDisplayNameList = AssetClassification32Enum.getAllDisplayNameList();
        for (String s : allDisplayNameList) {
            selectItemDtos.add(new SelectItemDto(s,null));

        }
        return selectItemDtos;

    }
    private Double toRoundedDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        try {
            double d;
            if (value instanceof Number) {
                d = ((Number) value).doubleValue();
            } else {
                d = Double.parseDouble(value.toString());
            }
            return new BigDecimal(d)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public void assetClassification32Change() {
        if(selectItemDto!=null){
            selected.setAssetClassification32(selectItemDto.getLabel());
        }
    }
    public SelectItemDto getSelectItemDto() {
        return selectItemDto;
    }

    public void setSelectItemDto(SelectItemDto selectItemDto) {
        this.selectItemDto = selectItemDto;
    }
}
