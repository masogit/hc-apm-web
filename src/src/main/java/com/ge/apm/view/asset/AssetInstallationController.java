/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.approval.WxMessageService;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.component.AssetTypeGroupTreeComponentController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.pm.RelRuleAssetController;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.llm.EquipmentNameplate;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import javaslang.Tuple2;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInstallationController extends GenericCRUDUUIDController<ArrivalList> {

    @Getter
    @Setter
    private ArrivalListItem arrivalListItem;
    private ArrivalListItemRepository itemDao;
    private WorkflowConfigRepository workflowDao;
    private ArrivalListRepository arrivalListDao;
    private TableDefinitionRepository tableDefinitionRepository;
    private AssetInstallationService aiService;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private QrCodeService qrCodeService;
    private AssetInfoMgrService assetMgrService;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private CustomConfigService ccService;
    private MedicalInstrumentsClassificationCompCtrl medicalInstrumentsClassificationCompCtrl;
    private AssetTypeGroupTreeComponentController assetTypeGroupTreeComponentController;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private ConfigItemRepository configItemRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private UserContextService userContextService;
    private AssetFileImportSupportingService supportingService;
    private List<V2_BlobObject> attachList;
    private ContractInfoItemRepository contractItemDao;

    @Getter
    @Setter
    private List<I18nMessage> code68List;
    private I18nMessageRepository i18nDao;
    private GbCodeService gbCodeService;
    private GbCodeRepository gbDao;

    @Getter
    @Setter
    private TreeNode selectedNode;
    @Getter
    @Setter
    private TreeNode selectedGbNode;

    @Getter
    @Setter
    private String gbCodeName;

    @Getter
    @Setter
    private GbCode selectedGb;
    @Getter
    @Setter
    private TreeNode gbTree;
    private String src = "";
    private UserAccount owner1;
    private UserAccount owner2;
    private UserAccount owner3;
    @Getter
    @Setter
    private UserAccount useOwner;
    private AutoCompleteService autoCompleteService;
    @Getter @Setter
    private Map<String, Object> selectedObjectMap = new HashMap<>();

    private ConfigService configService;

    private AssetInstallationReportService assetInstallationReportService;

    private StreamedContent exportFile;

    @Getter
    @Setter
    private String exportQrCodeTemplateId;

    @Getter
    @Setter
    private String deptName;
    private List<UserAccount> ownerList;
    private List<UserAccount> clinicalOwnerList;
    private List<OrgInfo> deptInfoList;
    private UaaService uaaService;
    private WorkflowConfig config;
    private boolean needInstorageCheck;
    private List<AssetInfo> parentAssetList;

    @Getter
    @Setter
    private AssetInfo parentAssetInfo;

    private String oldQrCode;

    @Getter
    private CustomConfigObject customConfig;

    private SupplierService supplierService;
    private OrgInfoRepository orgInfoRepository;

    private ArrivalList arrivalList;

    @Getter
    @Setter
    private String assetTypeGroup;

    private WxMessageService wxMessageService;

    private UserAccount loginUser;
    private TenantInfoRepository tenantInfoDao;

    private SystemTemplateService systemTemplateService;
    private MicroServiceUtil msService;
    private MasterDataService masterDataService;

    @Getter @Setter
    private Map<String, Boolean> arrivalRequireField = new HashMap<>();

    @Getter
    @Setter
    private SelectItemDto selectItemDto;

    @Getter @Setter
    private EquipmentNameplate equipmentNameplate = new EquipmentNameplate();

    @Getter @Setter
    private Map<String,Boolean> useValue = new HashMap<>();

    @Setter@Getter
    private String selectedFileItemId;

    public void nameplatePhotoPreview(String itemId){
        selectedFileItemId = itemId;
    }
    @Override
    protected GenericRepositoryUUID<ArrivalList> getDAO() {
        return arrivalListDao;
    }

    private RestTemplate restTemplate;

    @Override
    @PostConstruct
    protected void init() {
        aiService = WebUtil.getBean(AssetInstallationService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        assetMgrService = WebUtil.getBean(AssetInfoMgrService.class);
        configService = WebUtil.getBean(ConfigService.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        workflowDao = WebUtil.getBean(WorkflowConfigRepository.class);
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        tableDefinitionRepository = WebUtil.getBean(TableDefinitionRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        assetInstallationReportService = WebUtil.getBean(AssetInstallationReportService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        medicalInstrumentsClassificationCompCtrl = WebUtil.getBean(MedicalInstrumentsClassificationCompCtrl.class);
        medicalInstrumentsClassificationCompCtrl.registerController(this);
        userContextService=WebUtil.getBean(UserContextService.class);
        supportingService = WebUtil.getBean(AssetFileImportSupportingService.class);
        contractItemDao = WebUtil.getBean(ContractInfoItemRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String itemId = (String) UrlEncryptController.getValueFromMap(encodeStr, "itemId");
        src = (String) UrlEncryptController.getValueFromMap(encodeStr, "src");
        arrivalListItem = itemDao.findById(itemId);
        medicalInstrumentsClassificationCompCtrl.onServerEvent(MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, arrivalListItem.getMedicalApparatusClassification());
        arrivalListItem.setIsPm(arrivalListItem.getIsPm() == null || arrivalListItem.getIsPm());
        arrivalListItem.setIsChecked(arrivalListItem.getIsChecked() == null || arrivalListItem.getIsChecked());
        oldQrCode = arrivalListItem.getQrCode();
        assetTypeGroupTreeComponentController = WebUtil.getBean(AssetTypeGroupTreeComponentController.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        assetTypeGroupTreeComponentController.registerController(this);
        assetTypeGroupTreeComponentController.onServerEvent(AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE, arrivalListItem.getEncoded() + "#" + arrivalListItem.getArrivalList().getTenantUID());
        configItemRepository = WebUtil.getBean(ConfigItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        wxMessageService = WebUtil.getBean(WxMessageService.class);
        loginUser = UserContextService.getCurrentUserAccount();
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
        msService = WebUtil.getBean(MicroServiceUtil.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);
        restTemplate = WebUtil.getBean(RestTemplate.class);
        String assetTypeGroup = arrivalListItem.getEncoded();
        if (assetTypeGroup!=null){
            AssetTypeGroup selectedAssetTypeGroup = assetTypeGroupRepository.findByEncodedAndTenantUid(assetTypeGroup, arrivalListItem.getTenantUid());
            if (null != selectedAssetTypeGroup) {
                selectedObjectMap.put("selectedAssetTypeGroup", selectedAssetTypeGroup);
            }
        }

        if (null == arrivalListItem.getInstallDate()) {
            arrivalListItem.setInstallDate(new Date());
        }
        config = configService.findConfigBySite(arrivalListItem.getArrivalList().getSiteUID());
        customConfig = ccService.getCurrentCustomConfig(arrivalListItem.getArrivalList().getSiteUID());
        if (StringUtils.isNotEmpty(assetTypeGroup)) {
            customConfig.getAssetFields().forEach((k, v) -> requiredInput(assetTypeGroup, v));
        }
        initOwner();
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            OrgInfo org = aiService.getOrgInfoByUid(arrivalListItem.getClinicalDeptUID());
            deptName = org == null ? null : org.getName();
        }
        getOwnerList();
        onAssetOwnerNameChange();
        getClinicalOwnerList();
        changeFunctionGroup();
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);

        arrivalList = arrivalListItem.getArrivalList();
        deptInfoList = uaaService.getClinicalDeptListBySiteUID(arrivalList.getSiteUID());
        String contractNum = arrivalList.getContractNum();
        supplierService = WebUtil.getBean(SupplierService.class);
        if(activeSupplier() && contractNum != null){
            Set<String> ids = new HashSet<>();
            ids.add(arrivalList.getId());
            ids.add(contractNum);
            attachList = aiService.getPartPictures(ids);
        }else{
            attachList = aiService.getPicList(arrivalList.getId());
        }
        attachList.addAll(aiService.getPicList(arrivalListItem.getId()));
        initGbTree();
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        needInstorageCheck = userContextService.needInstorageCheck();
        if (arrivalListItem.getParentAssetId() != null) {
            parentAssetInfo = assetDao.findById(arrivalListItem.getParentAssetId());
        }
        getParentAssetList();
        selectedObjectMap.put("selectedOrg", orgInfoRepository.findByUid(arrivalList.getSiteUID()));
        if(arrivalListItem.getAssetClassification32()!=null){
            selectItemDto=new SelectItemDto(arrivalListItem.getAssetClassification32(),null);
        }
    }

    public void requiredInput(String encoded, CustomConfigAssetField customConfigAssetField) {
        if (customConfigAssetField.isArrivalRequired() && BooleanUtils.isTrue(arrivalRequireField.get(customConfigAssetField.getKey()))) {
            return;
        }
        if (StringUtils.isEmpty(encoded) || MapUtils.isEmpty(customConfigAssetField.getAssetTypeRequired())) {
            return;
        }
        if (arrivalRequireField.get(customConfigAssetField.getKey()) == null) {
            arrivalRequireField.put(customConfigAssetField.getKey(), customConfigAssetField.isArrivalRequired());
        }
        customConfigAssetField.setArrivalRequired(BooleanUtils.isTrue(customConfigAssetField.getAssetTypeRequired().get(encoded)));
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        selectedObjectMap.put("selectedOrg", orgInfo);
        arrivalList.setTenantUID(orgInfo.getTenantUID());
        arrivalList.setInstitutionUID(orgInfo.getInstitutionUID());
        arrivalList.setHospitalUID(orgInfo.getHospitalUID());
        arrivalList.setSiteUID(orgInfo.getSiteUID());
        deptInfoList = uaaService.getClinicalDeptListBySiteUID(orgInfo.getUid());
        clearClinicalDept();
    }

    private void initOwner() {
        String owner1UID = arrivalListItem.getAssetOwnerUID();
        String owenr2UID = arrivalListItem.getAssetOwnerUID2();
        String owner3UID = arrivalListItem.getAssetOwnerUID3();
        String useOwnerUID = arrivalListItem.getClinicalOwnerUid();
        if (Strings.isNullOrEmpty(owner1UID)) {
            if (UserContextService.checkRole("AssetStaff")) { //3363 
                arrivalListItem.setAssetOwnerUID(UserContextService.getCurrentUserAccount().getUid());
                owner1 = aiService.getUserAccountByUid(arrivalListItem.getAssetOwnerUID());
            }
        } else {
            owner1 = aiService.getUserAccountByUid(owner1UID);
        }
        if (StringUtils.isNotEmpty(owenr2UID)) {
            owner2 = aiService.getUserAccountByUid(owenr2UID);
        } else {
            owner2 = new UserAccount();
        }
        if (StringUtils.isNotEmpty(owner3UID)) {
            owner3 = aiService.getUserAccountByUid(owner3UID);
        } else {
            owner3 = new UserAccount();
        }
        if (StringUtils.isNotEmpty(useOwnerUID)) {
            useOwner = aiService.getUserAccountByUid(useOwnerUID);
        } else {
            useOwner = new UserAccount();
        }
    }

    public String rejectInstall() {
        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            if (Boolean.TRUE.equals(aiService.submitInstallItem(arrivalListItem.getId(), false, null))) {
                WebUtil.addSuccessMessage("提交成功");
                return returnAction();
            } else {
                WebUtil.addErrorMessage("提交失败");
            }
        }
        return "";
    }
    public boolean isChangeDept(){
        String stockDeptUid = Optional.ofNullable(configItemValueRepository.findFirstByKeyAndSiteUid("setAssetOutStockDept", loginUser.getSiteUID())).map(ConfigItemValue::getValue).orElse("");
        return StringUtils.isNotEmpty(stockDeptUid);
    }
    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
    }

    private Boolean isAvailableQrcode(String qrcode) {
        String qrCodeStatus = qrCodeService.checkAvailableBeforeSave(qrcode, arrivalListItem.getArrivalList().getSiteUID());
        if ("Normal".equals(qrCodeStatus)) {
            return true;
        } else {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage(qrCodeStatus));
            return false;
        }
    }

    public String finishInstall() {
        arrivalListDao.save(arrivalListItem.getArrivalList());//由于更新了院区，所以要重新保存一下，供定时任务获取
        boolean isValidate = true;
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                isValidate = false;
            }
            arrivalListItem.setNationalStandardCode(selectedGb.getCode());
            //更新资产国标码
            assetDao.updateStandardCode(selectedGb.getCode(), arrivalListItem.getAssetUID());
        }
        if (Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("ClinicalDeptName", true);
            isValidate = false;
        }
        if (arrivalListItem.getWorkHours() == null) {
            WebUtil.addErrorMessage("安装工时" + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("workHours", true);
            isValidate = false;
        }
        if (arrivalListItem.getArrivalList().getProcessType().equals(ArrivalList.ProcessType.Normal.toString())) {
            if (!arrivalListItem.getBusinessAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
                WebUtil.addErrorMessage("商务验收未通过");
                isValidate = false;
            }
            if (!arrivalListItem.getClinicalAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
                WebUtil.addErrorMessage("临床验收未通过");
                isValidate = false;
            }
            if (!arrivalListItem.getTechnicalAckStatus().equals(ArrivalListItem.ACK_STATUS_PASS)) {
                WebUtil.addErrorMessage("技术验收未通过");
                isValidate = false;
            }
        }
        if (needInstorageCheck && StringUtils.isEmpty(arrivalListItem.getDepartNum())) {
            WebUtil.addErrorMessage("设备编号（院方）不能为空");
            isValidate = false;
        }
        if (needInstorageCheck && StringUtils.isEmpty(arrivalListItem.getFinancingNum())) {
            WebUtil.addErrorMessage("资产编号不能为空");
            isValidate = false;
        }
        if (needInstorageCheck && StringUtils.isEmpty(arrivalListItem.getQrCode())) {
            WebUtil.addErrorMessage("二维码不能为空");
            isValidate = false;
        }

        if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
            if (Boolean.FALSE.equals(isAvailableQrcode(arrivalListItem.getQrCode()))) {
                arrivalListItem.setQrCode(null);
                isValidate = false;
            }
        }

        Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
        if (Boolean.FALSE.equals(isvalidDeptNum._1)) {
            isValidate = false;
            WebUtil.addErrorMessage(isvalidDeptNum._2);
        }

        AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup)selectedObjectMap.get("selectedAssetTypeGroup");
        if (null != selectedAssetTypeGroup){
            arrivalListItem.setEncoded(selectedAssetTypeGroup.getEncoded());
        }

        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(arrivalListItem.getEncoded())) {
            WebUtil.addErrorMessage("当前租户已开启资产分类,必填");
            isValidate = false;
        }

        // 序列号重复校验
        if (StringUtils.isNotEmpty(arrivalListItem.getSerialNum())) {
            boolean serialNumDuplicate = supportingService.isAssetFieldDuplicate("serialNum", arrivalListItem.getSerialNum(), arrivalListItem.getSiteUid(), null);
            if (serialNumDuplicate) {
                WebUtil.addErrorMessage("制造商序列号已存在！");
            }
        }

        if (!isValidate) {
            return "";
        }

        /*if (arrivalListItem.getWarrantyYear() != null) {
            Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
            Calendar calender = Calendar.getInstance();
            calender.setTime(arrivalListItem.getInstallDate() == null ? new Date() : arrivalListItem.getInstallDate());
            calender.add(Calendar.MONTH, monthLong);
            arrivalListItem.setFactoryWarrantyDate(calender.getTime());
        }*/
        //configService.isNeedAssetApprover(arrivalListItem.getArrivalList().getSiteUID())
        boolean approved = isApproved();
        if (approved) {
            //待审核
            arrivalListItem.setUsageStatus(2);
        } else {
            arrivalListItem.setUsageStatus(3);
        }

        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            arrivalListItem = itemDao.findById(arrivalListItem.getId());
            if (Boolean.TRUE.equals(aiService.submitInstallItem(arrivalListItem.getId(), true, arrivalListItem.getIsGeneratePmOrder() != null && arrivalListItem.getIsGeneratePmOrder()))) {
                WebUtil.addSuccessMessage("提交成功");
            } else {
                WebUtil.addErrorMessage("提交失败");
            }

        }
        String arrivalListId = arrivalListItem.getArrivalListId();
        if(approved){
            extracted(arrivalListItem);
        }
        if(arrivalListId != null ){
            //获取到arrival_list实体类对象
            ArrivalList arrivalList1 = arrivalListDao.findById(arrivalListId);
            //获取到院区编号
            String siteId = arrivalList1.getSiteUID();
            //获取到医院编号
            String hospitalId = arrivalList1.getHospitalUID();
            //获取到集团编号
            String institutionId = arrivalList1.getInstitutionUID();
            //获取到租户编号
            String tenantId = arrivalList1.getTenantUID();
            //获取到订单号（同一个合同号会有多个订单号）
            String orderNumber = arrivalList1.getName();
            List<WorkflowConfig> workflowConfigItems = workflowDao.findBySiteUID(siteId);
            WorkflowConfig workflowConfigItem =null;
            if(!workflowConfigItems.isEmpty()){
                workflowConfigItem = workflowConfigItems.get(0);
            }else{
                workflowConfigItems = workflowDao.findByHospitalUID(hospitalId);
                if(!workflowConfigItems.isEmpty()){
                    workflowConfigItem = workflowConfigItems.get(0);
                }else{
                    workflowConfigItems = workflowDao.findByInstitutionUID(institutionId);
                    if(!workflowConfigItems.isEmpty()){
                        workflowConfigItem = workflowConfigItems.get(0);
                    }else{
                        workflowConfigItems = workflowDao.findByTenantUID(tenantId);
                        if(!workflowConfigItems.isEmpty()){

                            workflowConfigItem = workflowConfigItems.get(0);
                        }
                    }
                }
            }
            if(workflowConfigItem != null){
                //获取到workflow中是否售后服务
                Boolean anBool = workflowConfigItem.getAiNeedMaintenance();
                if(Boolean.TRUE.equals(anBool)){
                    ArrivalList arrivalListToStatus = arrivalListDao.findByName(orderNumber);
                    if(arrivalListToStatus.getStatus()!=3){
                        return returnAction();
                    }
                    List<TableDefinition> tableDefinitions = tableDefinitionRepository.getBySiteUID(siteId);
                    if(!tableDefinitions.isEmpty()){
                        //需要判断设备分类是否相同而跳转到I产规则管理
                        //获取到安装设备的分类编号
                        Integer assetGroupNum1 = arrivalList1.getAssetGroup();
                        //获取到多个保养报告模板，循环每一个获取到设备分类的编号和安装设备编号对比
                        //有相同编号的跳转到资产规则管理，没有跳转到保养报告模板
                        for(TableDefinition tableDefinition : tableDefinitions){
                            Integer assetGroupId = tableDefinition.getAssetGroup();
                            if(assetGroupNum1.equals(assetGroupId)){
                                //获取到assetinfo表中设备想对应的uid值
                                //通过assetinfo中的uid值，获取到id值保存到集合
                                RelRuleAssetController.assetInfoUids= itemDao.getAssetUid(arrivalListId);
                                return "/portal/asset/arrived/ItemEdit2?faces-redirect=true";
                            }
                        }
                        return "/portal/reporter/schemaList?faces-redirect=true";
                    }else{
                        return "/portal/reporter/schemaList?faces-redirect=true";
                    }
                }else{
                    return returnAction();
                }
            }
        }
        return returnAction();
    }
    public String finishInstallBatch() {
        String contractNum = arrivalListItem.getArrivalList().getContractNum();
        String clinicalDeptUID = arrivalListItem.getClinicalDeptUID();
        ArrivalList arrivalList =arrivalListItem.getArrivalList();
        if(arrivalList.getVendor() == null || arrivalList.getSupplierUserId() == null){
            WebUtil.addErrorMessage("请致到货清单中录入供应商或供应商责任人！");
            return null;
        }
        if(StringUtils.isBlank(contractNum)){
            WebUtil.addErrorMessage("请致到货清单中录入合同编号");
            return null;
        }
        if(StringUtils.isBlank(clinicalDeptUID)){
            WebUtil.addErrorMessage("所属科室不能为空");
            return null;
        }

        saveItem();
        List<ArrivalListItem> itemList = itemDao.getArrivalListItem(contractNum,clinicalDeptUID,"new");
        if(itemList.isEmpty()){
            WebUtil.addErrorMessage(String.format("没有找到合同编号[%s]以及所属科室的到货单记录 .", contractNum));
            return null;
        }
        StringBuilder errorMessage = new StringBuilder();
        for(ArrivalListItem arrivalListItem : itemList){
            String message = "";
            if (null != gbCodeName) {
                GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
                if (null == gbCode) {
                    message = WebUtil.getMessage("无效的国标码");
                }
                arrivalListItem.setNationalStandardCode(selectedGb.getCode());
                //更新资产国标码
                assetDao.updateStandardCode(selectedGb.getCode(), arrivalListItem.getAssetUID());
            }
            if (Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
                message = message +"、"+ WebUtil.getMessage("clinicalDeptName") + WebUtil.getMessage("ValidationRequire");
                RequestContext.getCurrentInstance().addCallbackParam("ClinicalDeptName", true);
            }
            if (arrivalListItem.getWorkHours() == null) {
                message = message + "、安装工时" + WebUtil.getMessage("ValidationRequire");
                RequestContext.getCurrentInstance().addCallbackParam("workHours", true);
            }
            if (needInstorageCheck && StringUtils.isEmpty(arrivalListItem.getDepartNum())) {
                message = message + "、设备编号（院方）不能为空";
            }
            if (needInstorageCheck && StringUtils.isEmpty(arrivalListItem.getFinancingNum())) {
                message = message + "、资产编号不能为空";
            }
            String qrCode = arrivalListItem.getQrCode();
            if (needInstorageCheck && StringUtils.isEmpty(qrCode)) {
                message = message + "、二维码不能为空";
            }
            if(StringUtils.isNotBlank(qrCode)){
                String qrCodeStatus = qrCodeService.checkAvailableBeforeSaveJiuYuan(qrCode, arrivalListItem.getArrivalList().getSiteUID());
                if (!"Normal".equals(qrCodeStatus)) {
                    message = message + "、" + WebUtil.getMessage(qrCodeStatus);
                }
            }

            AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup)selectedObjectMap.get("selectedAssetTypeGroup");
            if (null != selectedAssetTypeGroup){
                arrivalListItem.setEncoded(selectedAssetTypeGroup.getEncoded());
            }

            if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(arrivalListItem.getEncoded())) {
                message = message + "、当前租户已开启资产分类,必填";
            }
            arrivalListItem.setBusinessAckStatus(ArrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setBusinessAckDate(new Date());
            arrivalListItem.setTechnicalAckStatus(ArrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setTechnicalAckDate(new Date());
            arrivalListItem.setClinicalAckStatus(ArrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setClinicalAckDate(new Date());
            arrivalListItem.setFixassetAckStatus(ArrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setFixassetAckDate(new Date());

            Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
            if (Boolean.FALSE.equals(isvalidDeptNum._1)) {
                message = message + "、" + isvalidDeptNum._2;
            }

            if (StringUtils.isNotBlank(message)) {
                String name = arrivalListItem.getArrivalList().getName();
                String financingNumName = WebUtil.getMessage("financingNum");
                String financingNum = arrivalListItem.getFinancingNum()==null?"不可为空！":arrivalListItem.getFinancingNum();
                message = message.replaceFirst("、","");
                if(errorMessage.toString().contains("到货单名称/编号："+name)){
                    errorMessage.append("、").append(financingNumName).append("：").append(financingNum).append("---->").append(message);
                }else{
                    message = "到货单名称/编号：" + name +","+ financingNumName +"："+ financingNum +"---->" + message;
                    errorMessage.append(message);
                }
            }

            /*if (arrivalListItem.getWarrantyYear() != null) {
                Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
                Calendar calender = Calendar.getInstance();
                calender.setTime(arrivalListItem.getInstallDate() == null ? new Date() : arrivalListItem.getInstallDate());
                calender.add(Calendar.MONTH, monthLong);
                arrivalListItem.setFactoryWarrantyDate(calender.getTime());
            }*/
            boolean approved = isApproved();
            if (approved) {
                //待审核
                arrivalListItem.setUsageStatus(2);
            } else {
                arrivalListItem.setUsageStatus(3);
            }
            //保存品牌
            if(StringUtils.isBlank(arrivalListItem.getBrand())){
                arrivalListItem.setBrand(arrivalListItem.getArrivalList().getBrand());
            }
        }

        if(StringUtils.isNotBlank(errorMessage.toString())){
            WebUtil.addErrorMessage(errorMessage.toString());
            return "";
        }

        arrivalListDao.save(arrivalListItem.getArrivalList());
        finishInstallHandle(itemList);
        if(isApproved()){
            for (ArrivalListItem listItem : itemList) {
                extracted(listItem);
            }
        }
        return returnAction();
    }

    private void extracted(ArrivalListItem listItem) {
        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setSiteUID(listItem.getArrivalList().getSiteUID());
        assetInfo.setCreatedDate(new Date());
        assetInfo.setClinicalDeptUID(listItem.getClinicalDeptUID());
        assetInfo.setTenantUID(listItem.getArrivalList().getTenantUID());
        assetInfo.setHospitalUID(listItem.getArrivalList().getHospitalUID());
        assetInfo.setInstitutionUID(listItem.getArrivalList().getInstitutionUID());
        OrgInfo clinicalDept = orgInfoRepository.findByUid(listItem.getClinicalDeptUID());
        assetInfo.setName(listItem.getArrivalList().getAssetName());
        assetInfo.setClinicalDeptName(clinicalDept.getName());
        assetInfo.setDepartNum(listItem.getDepartNum());
        assetInfo.setFinancingNum(listItem.getFinancingNum());
        assetInfo.setFunctionType(listItem.getFunctionType());
        wxMessageService.submitAssetApprovalSendMessage(assetInfo, "待审核");
    }

    public void finishInstallHandle(List<ArrivalListItem> itemList) {
        StringBuilder message = new StringBuilder();
        for(ArrivalListItem arrivalListItem : itemList){
            if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
                // 序列号重复校验
                if (StringUtils.isNotEmpty(arrivalListItem.getSerialNum())) {
                    List<AssetInfo> existList = assetDao.findBySerialNumAndTenantUID(
                            arrivalListItem.getSerialNum(), arrivalListItem.getArrivalList().getTenantUID());
                    if (!existList.isEmpty()) {
                        String name = arrivalListItem.getArrivalList().getName();
                        String errMsg = "制造商序列号[" + arrivalListItem.getSerialNum() + "]已存在，不允许重复建档";
                        if(message.toString().contains("到货单名称/编号："+name)){
                            message.append("、").append(errMsg);
                        }else{
                            message.append("到货单名称/编号：").append(name).append(",").append(errMsg);
                        } 
                        continue;
                    }
                }
                arrivalListItem.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
                aiService.saveOrUpdateItem(arrivalListItem);
                arrivalListItem = itemDao.findById(arrivalListItem.getId());
                if (Boolean.FALSE.equals(aiService.submitInstallItem(arrivalListItem.getId(), true, arrivalListItem.getIsGeneratePmOrder() != null && arrivalListItem.getIsGeneratePmOrder()))) {
                    String name = arrivalListItem.getArrivalList().getName();
                    if(message.toString().contains("到货单名称/编号："+name)){
                        message.append("、").append(WebUtil.getMessage("financingNum")).append("：").append(arrivalListItem.getFinancingNum()).append("---->当前记录提交失败");
                    }else{
                        message.append("到货单名称/编号：").append(name).append(",").append(WebUtil.getMessage("financingNum")).append("：").append(arrivalListItem.getFinancingNum()).append("---->当前记录提交失败");
                    }
                }

            }
        }
        if(StringUtils.isEmpty(message.toString())){
            WebUtil.addSuccessMessage("入库安装成功");
        }else{
            WebUtil.addErrorMessage(message.toString());
        }
    }
    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public List<V2_BlobObject> getAttachList(String type) {
        return attachList == null ? null : attachList.stream().filter(item -> type.equals(item.getBoSubType()))
                .collect(Collectors.toList());
    }

    public void reName(String blobId, String name) {

        attachList.forEach(v -> {
            if (v.getId().equals(blobId)) {
                v.setObjectName(name);
                blobService.saveBlobObject(v);
            }
        });
    }

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategories() {
        return configService.getAttachmentCategories(config);
    }

    public String returnAction() {
        if ("List".equals(src)) {
            return "arrivalItemsMain?faces-redirect=true";
        } else {
            return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Edit&listId=" + arrivalListItem.getArrivalListId());
        }
    }

    public void updateItem() {
        arrivalListDao.save(arrivalListItem.getArrivalList());
        if (saveItem()) {
            WebUtil.addSuccessMessage("修改成功");
        }
    }

    private boolean saveItem() {
        boolean valid = true;
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return false;
            }
            arrivalListItem.setNationalStandardCode(gbCodeName.split("/")[0]);
        }

//        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(selected.getAssetType())) {
//            WebUtil.addErrorMessage("当前租户已开启资产分类,必填");
//            return false;
//        }

        if (Boolean.FALSE.equals(isAvailableQrcode(arrivalListItem.getQrCode()))) {
            valid = false;
            arrivalListItem.setQrCode(null);
        }
        if (!valid) {
            return false;
        }

        AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup)selectedObjectMap.get("selectedAssetTypeGroup");
        if (null != selectedAssetTypeGroup){
            arrivalListItem.setEncoded(selectedAssetTypeGroup.getEncoded());
        }
        if(StrUtil.isNotEmpty(arrivalListItem.getDepartNum())){
            Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
            if (Boolean.FALSE.equals(isvalidDeptNum._1)) {
                WebUtil.addErrorMessage(isvalidDeptNum._2);
                return false;
            }
        }

        // 序列号重复校验
        if (StringUtils.isNotEmpty(arrivalListItem.getSerialNum())) {
            List<AssetInfo> existList = assetDao.findBySerialNumAndTenantUID(
                    arrivalListItem.getSerialNum(), arrivalListItem.getArrivalList().getTenantUID());
            if (!existList.isEmpty()) {
                WebUtil.addErrorMessage("制造商序列号已存在，不允许重复建档");
                return false;
            }
        }

        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            boolean res = aiService.saveOrUpdateItem(arrivalListItem);
            arrivalListItem = itemDao.findById(arrivalListItem.getId());
            return res;
        } else {
            WebUtil.addErrorMessage("不可修改");
            return false;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        String typeName = event.getComponent().getClientId();
        typeName = typeName.substring(typeName.indexOf("type") + 4);
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ArrivalListItem);
            blobObject.setBoUuid(arrivalListItem.getId());
            blobObject.setBoSubType(typeName);
            blobService.saveBlobObject(blobObject);
            try {
                blobService.sendImageCompressMQ(blobObject);
            }catch (Exception e){
                logger.error("安装入库，图片生成缩略图MQ发生失败：", e);
            }
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.arrivalListItem.setAssetCode68(null);

        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.arrivalListItem.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    public List<UserAccount> getClinicalOwnerList() {
        clinicalOwnerList = aiService.getClinicalOwnerList(ListUtil.newArrayList(arrivalListItem.getClinicalDeptUID()));
        return clinicalOwnerList;
    }

    public List<AssetInfo> getParentAssetList() {
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            parentAssetList = assetMgrService.getParentsAssetsList(null, arrivalListItem.getClinicalDeptUID(), arrivalListItem.getArrivalList().getSiteUID());
        } else {
            parentAssetList = Collections.emptyList();
        }
        return parentAssetList;
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
        //联动更新
        if (null != gbCodeName) {
            this.arrivalListItem.setNationalStandardCode(gbCodeName);
        }
    }

    private void updateSelectedOrgGb(GbCode gbCode) {
        if (gbCode == null || gbCode.getId() == -1) {
            selectedGb = null;
        } else {
            selectedGb = gbCode;
            gbCodeName = gbCode.getCode() + "/" + gbCode.getName();
        }

    }

    public void renewQrcode() {
        arrivalListItem.setQrCode(aiService.getNewQrcode(arrivalListItem.getArrivalList().getTenantUID(), arrivalListItem.getArrivalList().getSiteUID()).getQrCode());
        onDepartNumChange("qrCode");
    }

    public List<String> completeGbName(String prefix) {
        List<String> names = null;
        if (!StringUtils.isEmpty(prefix)) {
            names = gbDao.findGbCodeByPrefix(prefix);
        }
        return names;

    }

    public void gennerateFinancingNum() {
        String prifix = arrivalListItem.getFinancingNum() == null ? "" : arrivalListItem.getFinancingNum();
        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(null,
                arrivalListItem.getNationalStandardCode() == null ? null : arrivalListItem.getNationalStandardCode().split("/")[0],
                Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID()) ? arrivalListItem.getArrivalList().getSiteUID() : arrivalListItem.getClinicalDeptUID(),
                arrivalListItem.getParentAssetId(), prifix);
        if (null != financingNum && financingNum.startsWith("error:")) {
            WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            financingNum = "";
        }
        if (financingNum != null && (StringUtils.isEmpty(this.arrivalListItem.getFinancingNum()) || this.arrivalListItem.getFinancingNum().length() <= 4)) {
            this.arrivalListItem.setFinancingNum(financingNum);
        }
        onDepartNumChange("financingNum");

        //save
        saveItem();
    }

    public String getQrCodeImageForAsset() {
        AssetInfo asset = new AssetInfo();
        asset.setQrCode(arrivalListItem.getQrCode());
        asset.setName(arrivalListItem.getArrivalList().getAssetName());
        asset.setAssetGroup(arrivalListItem.getArrivalList().getAssetGroup());
        asset.setSiteUID(arrivalListItem.getArrivalList().getSiteUID());
        asset.setFunctionType(arrivalListItem.getArrivalList().getFunctionType());
        asset.setClinicalDeptUID(arrivalListItem.getClinicalDeptUID());
        asset.setSerialNum(arrivalListItem.getSerialNum());
        asset.setDepartNum(arrivalListItem.getDepartNum());
        asset.setFinancingNum(arrivalListItem.getFinancingNum());
        asset.setModalityId(arrivalListItem.getModalityId());
        asset.setSystemId(arrivalListItem.getSystemId());
        asset.setMakerShortName(arrivalListItem.getArrivalList().getMakerShortName());
        String tempSiteUid = arrivalListItem.getArrivalList().getSiteUID();
        //check to delete
        String tempQrCode = asset.getQrCode();
        if (StringUtils.isNotEmpty(tempQrCode)) {
            QrCodeLib tempQrCodeLib = qrCodeService.findByQrCode(tempQrCode, tempSiteUid);
            if (tempQrCodeLib == null) {
                qrCodeService.createSpecialQrCodeWithStatus(tempQrCode, tempSiteUid, 5);
            }
        }
        //delete
        return qrCodeService.getQrCodeImageBase64WithTemplate(asset, exportQrCodeTemplateId, tempSiteUid);
    }

    public void onAssetOwnerNameChange() {
        arrivalListItem.setAssetOwnerUID(owner1 == null ? null : owner1.getUid());
        if (owner1 == null) {
            return;
        }
        List<Object[]> ownerComposition = assetDao.findAssetOwnerComposition(owner1.getId(), arrivalListItem.getArrivalList().getSiteUID());
        if (ownerComposition != null && !ownerComposition.isEmpty()) {
            Object[] composition = ownerComposition.get(0);
            Integer assetOwnerId2 = (Integer) composition[2];
            if (assetOwnerId2 != null) {
                owner2 = aiService.getUserAccountById(assetOwnerId2);
                arrivalListItem.setAssetOwnerUID2(owner2 == null ? null : owner2.getUid());
            }
            Integer assetOwnerId3 = (Integer) composition[3];
            if (assetOwnerId3 != null) {
                owner3 = aiService.getUserAccountById(assetOwnerId3);
                arrivalListItem.setAssetOwnerUID3(owner3 == null ? null : owner3.getUid());
            }
        }
    }

    public void onAssetOwnerName2Change() {
        arrivalListItem.setAssetOwnerUID2(owner2 == null ? null : owner2.getUid());
    }

    public void onAssetOwnerName3Change() {
        arrivalListItem.setAssetOwnerUID3(owner3 == null ? null : owner3.getUid());
    }

    public void onUseOwnerNameChange() {
        arrivalListItem.setClinicalOwnerUid(useOwner == null ? null : useOwner.getUid());
    }

    public void onQrCodeChange() {
        if (!Strings.isNullOrEmpty(oldQrCode)) {
            //release old qrCode
            qrCodeService.releaseQrCode(oldQrCode, arrivalListItem.getArrivalList().getSiteUID());
            //delete old qrCode
            qrCodeService.deleteQrcode(oldQrCode, arrivalListItem.getArrivalList().getSiteUID());
        }
        if (!Strings.isNullOrEmpty(arrivalListItem.getQrCode())) {
            String availableQrcode = qrCodeService.getAvailableQrcode(arrivalListItem.getQrCode(), arrivalListItem.getArrivalList().getSiteUID());
            if (Strings.isNullOrEmpty(availableQrcode)) {
                arrivalListItem.setQrCode("");
            } else {
                oldQrCode = arrivalListItem.getQrCode();
            }
        } else {
            oldQrCode = "";
        }
        onDepartNumChange("qrCode");
    }

    public void checkDepartNumChange(){
        Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
        if (Boolean.FALSE.equals(isvalidDeptNum._1)) {
            WebUtil.addErrorMessage(isvalidDeptNum._2);
            arrivalListItem.setDepartNum("");
        }
    }

    public void onDepartNumChange(String fieldName) {
        if (config == null) {
            return;
        }
        if (!Strings.isNullOrEmpty(config.getAssetCodeConfig())) {
            String num = null;
            switch (config.getAssetCodeConfig()) {
                case "DFQ":
                    switch (fieldName) {
                        case "financingNum":
                            arrivalListItem.setQrCode(arrivalListItem.getFinancingNum());
                            onQrCodeChange();
                            num = arrivalListItem.getQrCode();
                            break;
                        case "departNum":
                            arrivalListItem.setQrCode(arrivalListItem.getDepartNum());
                            onQrCodeChange();
                            num = arrivalListItem.getQrCode();
                            break;
                        case "qrCode":
                            num = arrivalListItem.getQrCode();
                            break;
                        default:
                    }

                    if (!Strings.isNullOrEmpty(num) && num.length() > 64) {
                        num = num.substring(0, 64);
                        if (null != arrivalListItem.getQrCode()) {
                            arrivalListItem.setQrCode(num);
                            WebUtil.addErrorMessage("二维码超长,最大64位");
                        }
                    }
                    arrivalListItem.setFinancingNum(num);
                    arrivalListItem.setDepartNum(num);

                    break;
                case "DF":
                    switch (fieldName) {
                        case "financingNum":
                            num = arrivalListItem.getFinancingNum();
                            arrivalListItem.setFinancingNum(num);
                            arrivalListItem.setDepartNum(num);
                            break;
                        case "departNum":
                            num = arrivalListItem.getDepartNum();
                            arrivalListItem.setFinancingNum(num);
                            arrivalListItem.setDepartNum(num);
                            break;
                    }
                    break;
                default:
            }
        }
    }

    public void onAckChange(String ackType) {
        switch (ackType) {
            case "technical":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getTechnicalAckStatus())) {
                    arrivalListItem.setTechnicalAckDate(new Date());
                } else {
                    arrivalListItem.setTechnicalAckDate(null);
                }
                break;
            case "clinical":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getClinicalAckStatus())) {
                    arrivalListItem.setClinicalAckDate(new Date());
                } else {
                    arrivalListItem.setClinicalAckDate(null);
                }
                break;
            case "business":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getBusinessAckStatus())) {
                    arrivalListItem.setBusinessAckDate(new Date());
                } else {
                    arrivalListItem.setBusinessAckDate(null);
                }
                break;
            case "fixasset":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getFixassetAckStatus())) {
                    arrivalListItem.setFixassetAckDate(new Date());
                } else {
                    arrivalListItem.setFixassetAckDate(null);
                }
                break;
            default:
                break;
        }
    }

    public List<String> autoCompleteDept(String query) {
        return deptInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).map(OrgInfo::getName).sorted(new PinYinComparator()).collect(Collectors.toList());
    }

    public List<String> autoCompleteLocation(String query) {
        return deptInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase()))).map(OrgInfo::getName).sorted(new PinYinComparator()).collect(Collectors.toList());
    }

    public void onLocationSelect(SelectEvent event) {
        // dropdown选择时触发AJAX，确保值在immediate保存前已更新到model
    }

    public void onClinicalDeptChange(SelectEvent event) {
        OrgInfo dept = deptInfoList.stream().filter(item -> item.getName().equals(deptName)).findFirst().orElse(null);
        if (dept != null) {
            arrivalListItem.setClinicalDeptUID(dept.getUid());
            getClinicalOwnerList();
            parentAssetList = getParentAssetList();
        }
    }

    public void clearClinicalDept() {
        deptName = null;
        arrivalListItem.setClinicalDeptUID(null);
    }

    public void onParentAssetInfoChange() {
        if (this.parentAssetInfo != null) {
            this.arrivalListItem.setParentAssetId(this.parentAssetInfo.getId());
        } else {
            this.arrivalListItem.setParentAssetId(null);
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        if (eventName.equals(AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE)) {
            AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup) eventObject;
            selectedObjectMap.put("selectedAssetTypeGroup", selectedAssetTypeGroup);
            assetTypeGroup = selectedAssetTypeGroup.getEncoded();
            if(userContextService.enableAssetGroupAssetTypeGroupAssociation()){
                selected.setAssetGroup(selectedAssetTypeGroup.getAssetGroup());
            }
        }
        customConfig.getAssetFields().forEach((k, v) -> {
            requiredInput(assetTypeGroup, v);
        });
    }

    public List<UserAccount> completeOwnerList(String query) {
        return autoCompleteService.completeOwnerList(query, ownerList);
    }

    public List<UserAccount> completeClinicalOwnerList(String query) {
        return autoCompleteService.completeClinicalOwnerList(query, clinicalOwnerList);
    }

    public List<AssetInfo> completeParentAssetInfoList(String query) {
        return autoCompleteService.completeAssetInfoList(query, parentAssetList);
    }

    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class
        );
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }

    public List<UserAccount> getOwnerList() {
        if (ownerList == null) {
            ownerList = aiService.getWorkerList();
        }
        return ownerList;
    }

//    public void exportReport(String reportType) {
//        try {
//            Map<String, Object> data = assetInstallationReportService.createReportData(arrivalListItem);
//            byte[] templateBytes = assetInstallationReportService.buildReportTemplate(reportType);
//            if (templateBytes != null) {
//                exportFile = assetInstallationReportService.exportDocx(data, templateBytes, reportType);
//            }
//        } catch (IOException e) {
//            Logger.getLogger(AssetInstallationController.class.getName()).log(Level.SEVERE, null, e);
//            WebUtil.addErrorMessage("导出失败");
//        }
//    }

    public void exportReport(SystemTemplate systemTemplate) {
        try {
             exportFile = assetInstallationReportService.exportReport(systemTemplate, arrivalListItem);
        } catch (IOException e) {
            Logger.getLogger(AssetInstallationController.class.getName()).log(Level.SEVERE, null, e);
            WebUtil.addErrorMessage("导出失败");
        }
    }

    public void exportReportBatch(String reportType) {
        String contractNum = arrivalListItem.getArrivalList().getContractNum();
        String clinicalDeptUID = arrivalListItem.getClinicalDeptUID();
        if(StringUtils.isBlank(contractNum)){
            WebUtil.addErrorMessage("合同号不能为空");
            return;
        }
        if(StringUtils.isBlank(clinicalDeptUID)){
            WebUtil.addErrorMessage("所属科室不能为空");
            return;
        }
        List<ArrivalListItem> itemList = itemDao.getArrivalListItem(contractNum,clinicalDeptUID,"new");
        if(itemList == null || itemList.isEmpty()){
            WebUtil.addErrorMessage("没有需要导出的验收报告");
            return;
        }
        try {
            Map<String, Object> data = assetInstallationReportService.createReportDataBatch(itemList);
            byte[] templateBytes = assetInstallationReportService.buildReportTemplate(reportType);

            if (templateBytes != null) {
                exportFile = assetInstallationReportService.exportDocxBatch(data, templateBytes, reportType);
            }
        } catch (IOException e) {
            Logger.getLogger(AssetInstallationController.class.getName()).log(Level.SEVERE, null, e);
            WebUtil.addErrorMessage("导出失败");
        }
    }

    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public boolean isNeedInstorageCheck() {
        return needInstorageCheck;
    }

    public void setNeedInstorageCheck(boolean needInstorageCheck) {
        this.needInstorageCheck = needInstorageCheck;
    }

    public boolean activeSupplier(){
        return supplierService.activeSupplier();
    }

    public boolean verifyFileSuffixName(String fileName,String fileSuffixName){
        String[] fileSuffixNames = fileSuffixName.toUpperCase().split("\\|");
        for (String suffixName : fileSuffixNames) {
            if (fileName.toUpperCase().endsWith(suffixName)) {
                return true;
            }
        }
        return false;
    }
    private boolean  isApproved(){
        /**
         * --  YHAAO-19736  远东宏信-完成安装验收后，进入资产审核流程
         * insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 3, 'false', '安装入库完成是否审批', 'AssetCreationApprover', '安装入库完成是否审批', 'f', 't', 3 where not exists (select * from config_item where key = 'AssetCreationApprover');
          */
        // TODO
        boolean flag=false;
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        TenantInfo tenantInfo = userContextService.getTenantInfo();
        String featureList = tenantInfo.getFeatureList();
        if (StringUtils.isNotBlank(featureList)) {
             flag=featureList.contains("ASSET_APPROVE");
             if(flag){
                flag=getConfigItemAssetCreationApprover(userContextService.getLoginUser());
             }
        }
        return flag;
    }
    private boolean getConfigItemAssetCreationApprover(UserAccount loginUser ){
        Boolean flag=null;
        String key="assetInstallApprove";
        ConfigItem byDefaultValueForKey = configItemRepository.findByDefaultValueForKey(key);
        if(byDefaultValueForKey == null ) return Boolean.FALSE;
        List<SearchFilter> searchFilters=new ArrayList<>();
        searchFilters.add(new SearchFilter("key", SearchFilter.Operator.EQ,key));
        String siteUID = loginUser.getSiteUID();
        String hospitalUID = loginUser.getHospitalUID();
        String institutionUID = loginUser.getInstitutionUID();
        String tenantUID = loginUser.getTenantUID();
        List<String> value = Arrays.asList(siteUID, hospitalUID, institutionUID, tenantUID);
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, value));
        List<ConfigItemValue> configItemValues = configItemValueRepository.findBySearchFilter(searchFilters);
        if(!CollectionUtils.isEmpty(configItemValues)){
            Map<String, Boolean> collect = configItemValues.stream().collect(Collectors.toMap(ConfigItemValue::getSiteUid, item ->Boolean.valueOf(item.getValue())));
            if((flag=collect.get(siteUID)) == null
                    && (flag=collect.get(hospitalUID)) == null
                    && (flag=collect.get(institutionUID)) == null
                    && (flag=collect.get(tenantUID)) == null){
                flag = Boolean.valueOf(byDefaultValueForKey.getDefaultValue());
            }
        }else{
            flag=Boolean.valueOf(byDefaultValueForKey.getDefaultValue());
        }
        return flag;
    }

    public void checkSerialNumExists () {
        if (supportingService.isAssetFieldDuplicate("serialNum", arrivalListItem.getSerialNum(), arrivalListItem.getSiteUid(), arrivalListItem.getAssetUID())) {
            WebUtil.addStickyErrorMessage("制造商序列号已存在！");
        }
    }

    private Date factoryWarrantyDate;

    public void installDateChange(ValueChangeEvent event) {
        if (ObjectUtil.isNotEmpty(event)) {
            Date installDate = (Date) event.getNewValue();
            arrivalListItem.setInstallDate(installDate);
        }
        if (ObjectUtil.isAllNotEmpty(arrivalListItem.getWarrantyYear(), arrivalListItem.getInstallDate())) {
            Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
            Calendar calender = Calendar.getInstance();
            calender.setTime(arrivalListItem.getInstallDate() == null ? new Date() : arrivalListItem.getInstallDate());
            calender.add(Calendar.MONTH, monthLong);
            arrivalListItem.setFactoryWarrantyDate(calender.getTime());
            factoryWarrantyDate = calender.getTime();
        }
    }

    public Date getFactoryWarrantyDate() {
        if (ObjectUtil.isNotEmpty(factoryWarrantyDate)) {
            return factoryWarrantyDate;
        }
        if (ObjectUtil.isEmpty(arrivalListItem.getFactoryWarrantyDate())) {
            installDateChange(null);
        }
        return arrivalListItem.getFactoryWarrantyDate();
    }

    public void setFactoryWarrantyDate(Date factoryWarrantyDate) {
        this.factoryWarrantyDate = factoryWarrantyDate;
        this.arrivalListItem.setFactoryWarrantyDate(factoryWarrantyDate);
    }

    public UserAccount getOwner1() {
        return owner1;
    }

    public void setOwner1(UserAccount owner1) {
        this.owner1 = owner1;
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

    public boolean isSyf(){
        String currentTenantUid = UserContextService.getCurrentUserAccount().getTenantUID();
        TenantInfo tanentIfo = tenantInfoDao.findByUid(currentTenantUid);
        return "3".equals(tanentIfo.getCustomTenant());
    }



    public List<SystemTemplate> getArrivalAcceptanceReportList() {
        return systemTemplateService.findByKeyAndSiteForUseParent(SystemTemplateEnum.ARRIVAL_ACCEPTANCE_REPORT.getCode(),loginUser);
    }
    public void assetClassification32Change() {
        if(selectItemDto!=null){
            arrivalListItem.setAssetClassification32(selectItemDto.getLabel());
        }
    }
    public  List<SelectItemDto>  autoAssetClassification32() {

        AssetClassificationModel assetClassificationModel=new AssetClassificationModel();
        assetClassificationModel.setId(arrivalListItem.getId());
        assetClassificationModel.setBrand(arrivalListItem.getBrand());
        assetClassificationModel.setPattern(arrivalListItem.getFunctionType());
        assetClassificationModel.setSourceName(arrivalListItem.getArrivalList().getAssetName());
        assetClassificationModel.setFactory(null);
        // ONNX FULL 模型扩展字段
        assetClassificationModel.setDepartment(
            arrivalListItem.getArrivalList() != null ? arrivalListItem.getArrivalList().getClinicalDeptName() : null);
        assetClassificationModel.setRegistrationNo(arrivalListItem.getRegistrationNo());
        assetClassificationModel.setIsEmergencyDevice(
                arrivalListItem.getEmergencyEquipment() != null ? FieldValueMessageController.fieldValue("emergencyEquipment", String.valueOf(arrivalListItem.getEmergencyEquipment())) : null);
        assetClassificationModel.setPurchasePrice(
            arrivalListItem.getPurchasePrice() != null ? String.valueOf(arrivalListItem.getPurchasePrice()) : null);

        return    masterDataService.autoAssetClassification32(assetClassificationModel);

    }

    public void  plateRecognition(String objectId){
        try {
            String token = UserContextService.getAccessToken();
            String gatewayURL = msService.getGatewayURL(GatewayURL.PLATE_RECOGNITION);
            gatewayURL = gatewayURL.replace("{objectId}", objectId).concat("?assetUid=");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token);
            HttpEntity<Map> requestEntity = new HttpEntity<Map>(headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(gatewayURL, HttpMethod.GET, requestEntity, JSONObject.class);
            if(response.getStatusCode().is2xxSuccessful()){
                JSONObject body = response.getBody();
                JSONObject data = body.getJSONObject("data");
                equipmentNameplate = data.toJavaObject(EquipmentNameplate.class);
                WebUtil.addSuccessMessage(WebUtil.getMessage("nameplateRecognition")+"完成");
            }
        } catch (Exception e) {
            // 识别失败，请重新上传图片之类的
            equipmentNameplate = new EquipmentNameplate();
            logger.info(e.getLocalizedMessage(),e);
            WebUtil.addErrorMessage(WebUtil.getMessage("nameplateRecognition")+"识别失败，请重新上传图片");
        }
    }
    public List<Supplier> autoCompleteManufacture(String query) {
        List<Supplier> manuList = assetMgrService.getOnlyManuListBySiteUid(arrivalList.getTenantUID());
        return autoCompleteService.autoCompleteManufacture(query, manuList);
    }
    public void applyResult(){
//        3.点击应用按钮后，名称、规格型号、制造商、注册证号、使用年限，保存到设备信息里的同时更新到到货清单中。
//        4.点击应用按钮后，制造商序列号和出厂日期保存到设备信息里。
        ArrivalListItem arrivalListItem = this.getArrivalListItem();
        String deviceName = equipmentNameplate.getDevice_name();
        if(StringUtils.isNotEmpty(deviceName) && useValue.get("device_name")){
            arrivalListItem.getArrivalList().setAssetName(deviceName);
            arrivalListItem.setAliasName(deviceName);
        }

        String financingNum = equipmentNameplate.getPattern();
        if(StringUtils.isNotEmpty(financingNum) && useValue.get("pattern")){
            arrivalListItem.getArrivalList().setFunctionType(financingNum);
            arrivalListItem.setFunctionType(financingNum);
        }

        String serialNum = equipmentNameplate.getSerial_num();
        if(StringUtils.isNotEmpty(serialNum) && useValue.get("serial_num")){
              arrivalListItem.setSerialNum(serialNum);
        }

        String manufactory = equipmentNameplate.getManufactory();
        if(StringUtils.isNotEmpty(manufactory) && useValue.get("manufactory")){
            List<Supplier> suppliers = autoCompleteManufacture("");
            Optional<Supplier> first = suppliers.stream().filter(item -> manufactory.equalsIgnoreCase(item.getName())).findFirst();
            Supplier supplier = null;
            if(first.isPresent()){
                supplier = first.get();
            }else{
                Supplier newSupplier = new Supplier();
                newSupplier.setName(manufactory);
                newSupplier.setManufacturerT(Boolean.TRUE);
                supplierService.saveSupplier(newSupplier);
                supplier =  newSupplier;
                WebUtil.addSuccessMessage("制造商创建成功");
            }
            arrivalListItem.getArrivalList().setManufacture(supplier.getName());
            arrivalListItem.getArrivalList().setManufactureId(supplier.getId().toString());
        }

        String registryNum = equipmentNameplate.getRegistry_num();
        if(StringUtils.isNotEmpty(registryNum) && useValue.get("registry_num")){
            arrivalListItem.setRegistrationNo(registryNum);
        }

        String usingYear = equipmentNameplate.getUsing_year();
        if(StringUtils.isNotEmpty(usingYear) && useValue.get("using_year")){
            try {
                Pattern pattern = Pattern.compile("(?:\\d+\\.?\\d*|\\.\\d+)");
                Matcher matcher = pattern.matcher(usingYear);
                StringBuffer buffer = new StringBuffer();
                while (matcher.find()){
                    buffer.append(matcher.group());
                }
                arrivalListItem.setLifecycleInYear(Double.valueOf(buffer.toString()));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        Date factoryDate = equipmentNameplate.getFactory_date();
        if(factoryDate != null && useValue.get("factory_date")){
            arrivalListItem.setManufactDate(factoryDate);
        }
        equipmentNameplate = null;

    }


    public String contractUrl(String contractItemId){
        if(StrUtil.isEmpty(contractItemId)){
            return "";
        }
        ContractInfoItem contractInfoItem = contractItemDao.findById(contractItemId);
        if(contractInfoItem == null || StrUtil.isEmpty(contractInfoItem.getContractId())){
            return "";
        }
        String str = "action=Edit&selectedid=".concat(contractInfoItem.getContractId());
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
        // 返回绝对路径
        String url = contextPath + "/portal/asset/contract/ContractView.xhtml?str="+ UrlParamUtil.encodeUrlQueryString(str);
        System.out.println(url);
        return url;
    }
}
