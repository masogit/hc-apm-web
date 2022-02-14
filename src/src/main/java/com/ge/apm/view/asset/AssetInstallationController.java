/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.view.pm.RelRuleAssetController;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;

import javaslang.Tuple2;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInstallationController {

    @Getter
    @Setter
    private ArrivalListItem arrivalListItem;
    private ArrivalListItemRepository itemDao;
    private WorkflowConfigRepository workflowDao;
    private UserAccountRepository userAccountDao;
    private ArrivalListRepository arrivalListDao;
    private RelRuleAssetRepository relRuleAssetDao;
    private AssetInfoRepository assetInfoRepository;
    private TableDefinitionRepository tableDefinitionRepository;
    private AssetInstallationService aiService;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private QrCodeService qrCodeService;
    private AssetInfoMgrService assetMgrService;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private CustomConfigService ccService;

    private List<V2_BlobObject> attachList;
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
    @Getter
    @Setter
    private UserAccount owner1;
    @Getter
    @Setter
    private UserAccount owner2;
    @Getter
    @Setter
    private UserAccount owner3;
    @Getter
    @Setter
    private UserAccount useOwner;
    private AutoCompleteService autoCompleteService;
    @Getter @Setter
    private Map<String, Object> selectedObjectMap = new HashMap();
    @Getter
    private TreeNode orgTree;
//    private String currentQrCode;

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
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        relRuleAssetDao = WebUtil.getBean(RelRuleAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tableDefinitionRepository = WebUtil.getBean(TableDefinitionRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        assetInstallationReportService = WebUtil.getBean(AssetInstallationReportService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String itemId = (String) UrlEncryptController.getValueFromMap(encodeStr, "itemId");
        src = (String) UrlEncryptController.getValueFromMap(encodeStr, "src");
//        String itemId = WebUtil.getRequestParameter("itemId");
        arrivalListItem = itemDao.findById(itemId);
        arrivalListItem.setIsPm(arrivalListItem.getIsPm() == null ? true : arrivalListItem.getIsPm());
        arrivalListItem.setIsChecked(arrivalListItem.getIsChecked() == null ? true : arrivalListItem.getIsChecked());
        oldQrCode = arrivalListItem.getQrCode();

        if (null == arrivalListItem.getInstallDate()) {
            arrivalListItem.setInstallDate(new Date());
        }
        config = configService.findConfigBySite(arrivalListItem.getArrivalList().getSiteUID());
        customConfig = ccService.getCurrentCustomConfig(arrivalListItem.getArrivalList().getSiteUID());
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
            if (aiService.submitInstallItem(arrivalListItem.getId(), false, null)) {
                WebUtil.addSuccessMessage("提交成功");
                return returnAction();
            } else {
                WebUtil.addErrorMessage("提交失败");
            }
        }
        return "";
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
        arrivalListDao.save(arrivalList);//由于更新了院区，所以要重新保存一下，供定时任务获取
        Boolean isValidate = true;
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
//        if (null == arrivalListItem.getIsGeneratePmOrder()) {
//            WebUtil.addErrorMessage("同时生成保养工单" + WebUtil.getMessage("ValidationRequire"));
//            RequestContext.getCurrentInstance().addCallbackParam("IsGeneratePmOrder", true);
//            isValidate = false;
//        }
        if (arrivalListItem.getWorkHours() == null) {
            WebUtil.addErrorMessage("安装工时" + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("workHours", true);
            isValidate = false;
        }
//        if (Strings.isNullOrEmpty(arrivalListItem.getSerialNum()) && customConfig.assetFields.get("serialNum").isArrivalRequired()) {
//            WebUtil.addErrorMessage(WebUtil.getMessage("serialNum") + WebUtil.getMessage("ValidationRequire"));
//            RequestContext.getCurrentInstance().addCallbackParam("serialNum", true);
//            isValidate = false;
//        }
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
            if (!isAvailableQrcode(arrivalListItem.getQrCode())) {
                arrivalListItem.setQrCode(null);
                isValidate = false;
            }
        }

        Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
        if (!isvalidDeptNum._1) {
            isValidate = false;
            WebUtil.addErrorMessage(isvalidDeptNum._2);
        }

        if (!isValidate) {
            return "";
        }

        if (arrivalListItem.getWarrantyYear() != null) {
            Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
            Calendar calender = Calendar.getInstance();
            calender.setTime(arrivalListItem.getInstallDate() == null ? new Date() : arrivalListItem.getInstallDate());
            calender.add(Calendar.MONTH, monthLong);
            arrivalListItem.setFactoryWarrantyDate(calender.getTime());
        }

        if (configService.isNeedAssetApprover(arrivalListItem.getArrivalList().getSiteUID())) {
            //待审核
            arrivalListItem.setUsageStatus(2);
        } else {
            arrivalListItem.setUsageStatus(3);
        }

        if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
            aiService.saveOrUpdateItem(arrivalListItem);
            arrivalListItem = itemDao.findById(arrivalListItem.getId());
            if (aiService.submitInstallItem(arrivalListItem.getId(), true, arrivalListItem.getIsGeneratePmOrder() == null ? false : arrivalListItem.getIsGeneratePmOrder())) {
                WebUtil.addSuccessMessage("提交成功");
            } else {
                WebUtil.addErrorMessage("提交失败");
            }

        }
        String arrivalListId = arrivalListItem.getArrivalListId();

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
            if(workflowConfigItems.size()>0){
                workflowConfigItem = workflowConfigItems.get(0);
            }else{
                workflowConfigItems = workflowDao.findByHospitalUID(hospitalId);
                if(workflowConfigItems.size()>0){
                    workflowConfigItem = workflowConfigItems.get(0);
                }else{
                    workflowConfigItems = workflowDao.findByInstitutionUID(institutionId);
                    if(workflowConfigItems.size()>0){
                        workflowConfigItem = workflowConfigItems.get(0);
                    }else{
                        workflowConfigItems = workflowDao.findByTenantUID(tenantId);
                        if(workflowConfigItems.size()>0){

                            workflowConfigItem = workflowConfigItems.get(0);
                        }
                    }
                }
            }
            if(workflowConfigItem != null){
                //获取到workflow中是否售后服务
                Boolean anBool = workflowConfigItem.getAiNeedMaintenance();
                if(anBool != null && anBool  == true){
                    ArrivalList arrivalListToStatus = arrivalListDao.findByName(orderNumber);
                    if(arrivalListToStatus.getStatus()!=3){
                        return returnAction();
                    }
                    List<TableDefinition> tableDefinitions = tableDefinitionRepository.getBySiteUID(siteId);
                    if(tableDefinitions.size()>0){
                        //需要判断设备分类是否相同而跳转到I产规则管理
                        //获取到安装设备的分类编号
                        Integer assetGroupNum1 = arrivalList1.getAssetGroup();
                        //获取到多个保养报告模板，循环每一个获取到设备分类的编号和安装设备编号对比
                        //有相同编号的跳转到资产规则管理，没有跳转到保养报告模板
                        for(TableDefinition tableDefinition : tableDefinitions){
                            Integer assetGroupId = tableDefinition.getAssetGroup();
                            if(assetGroupNum1.equals(assetGroupId)){
                                //获取到assetinfo表中设备想对应的uid值
                                List<String> assetIds = itemDao.getAssetUid(arrivalListId);
                                //通过assetinfo中的uid值，获取到id值保存到集合
                                RelRuleAssetController.assetInfoUids=assetIds;
                                return "/portal/asset/arrived/ItemEdit2?faces-redirect=true";
                            }else{
                                return "/portal/reporter/schemaList?faces-redirect=true";
                            }
                        }
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
        String errorMessage = "";
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
            //if(arrivalListItem.getBusinessAckStatus().equals(arrivalListItem.ACK_STATUS_NONE)){
            arrivalListItem.setBusinessAckStatus(arrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setBusinessAckDate(new Date());
            //}
//            if(arrivalListItem.getTechnicalAckStatus().equals(arrivalListItem.ACK_STATUS_NONE)){
            arrivalListItem.setTechnicalAckStatus(arrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setTechnicalAckDate(new Date());
//            }
//            if(arrivalListItem.getClinicalAckStatus().equals(arrivalListItem.ACK_STATUS_NONE)){
            arrivalListItem.setClinicalAckStatus(arrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setClinicalAckDate(new Date());
//            }
//            if(arrivalListItem.getFixassetAckStatus().equals(arrivalListItem.ACK_STATUS_NONE)){
            arrivalListItem.setFixassetAckStatus(arrivalListItem.ACK_STATUS_PASS);
            arrivalListItem.setFixassetAckDate(new Date());
//            }

            Tuple2<Boolean, String> isvalidDeptNum = aiService.isValidDeptNum(arrivalListItem);
            if (!isvalidDeptNum._1) {
                message = message + "、" + isvalidDeptNum._2;
            }

            if (StringUtils.isNotBlank(message)) {
                String name = arrivalListItem.getArrivalList().getName();
                String financingNumName = WebUtil.getMessage("financingNum");
                String financingNum = arrivalListItem.getFinancingNum()==null?"不可为空！":arrivalListItem.getFinancingNum();
                message = message.replaceFirst("、","");
                if(errorMessage.contains("到货单名称/编号："+name)){
                    errorMessage =  errorMessage + "、"+ financingNumName +"："+ financingNum +"---->" + message;
                }else{
                    message = "到货单名称/编号：" + name +","+ financingNumName +"："+ financingNum +"---->" + message;
                    errorMessage = errorMessage + message ;
                }
            }

            if (arrivalListItem.getWarrantyYear() != null) {
                Integer monthLong = Double.valueOf(arrivalListItem.getWarrantyYear() * 12).intValue();
                Calendar calender = Calendar.getInstance();
                calender.setTime(arrivalListItem.getInstallDate() == null ? new Date() : arrivalListItem.getInstallDate());
                calender.add(Calendar.MONTH, monthLong);
                arrivalListItem.setFactoryWarrantyDate(calender.getTime());
            }

            if (configService.isNeedAssetApprover(arrivalListItem.getArrivalList().getSiteUID())) {
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

        if(StringUtils.isNotBlank(errorMessage)){
            WebUtil.addErrorMessage(errorMessage);
            return "";
        }

        finishInstallHandle(itemList);
        return returnAction();
    }
    public void finishInstallHandle(List<ArrivalListItem> itemList) {
        String message = "";
        for(ArrivalListItem arrivalListItem : itemList){
            if (arrivalListItem.getInstallStatus().equals(ArrivalListItem.INSTALL_STATUS_NEW)) {
                arrivalListItem.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
                aiService.saveOrUpdateItem(arrivalListItem);
                arrivalListItem = itemDao.findById(arrivalListItem.getId());
                if (!aiService.submitInstallItem(arrivalListItem.getId(), true, arrivalListItem.getIsGeneratePmOrder() == null ? false : arrivalListItem.getIsGeneratePmOrder())) {
                    String name = arrivalListItem.getArrivalList().getName();
                    if(message.contains("到货单名称/编号："+name)){
                        message =  message +"、"+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->当前记录提交失败";
                    }else{
                        message = message + "到货单名称/编号：" + name +","+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->当前记录提交失败";
                    }
                }

            }
        }
        if(StringUtils.isEmpty(message)){
            WebUtil.addSuccessMessage("入库安装成功");
        }else{
            WebUtil.addErrorMessage(message);
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

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategories() {
        return configService.getAttachmentCategories(config);
    }

    public String returnAction() {
        if ("List".equals(src)) {
            return "arrivalItemsList?faces-redirect=true";
        } else {
            return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Edit&listId=" + arrivalListItem.getArrivalListId());
        }
    }

    public void updateItem() {
        if (saveItem()) {
            WebUtil.addSuccessMessage("修改成功");
        }
    }

    private boolean saveItem() {
        Boolean valid = true;
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return false;
            }
            if (null != gbCodeName) {
                arrivalListItem.setNationalStandardCode(gbCodeName.split("/")[0]);
            }
        }

        if (!isAvailableQrcode(arrivalListItem.getQrCode())) {
            valid = false;
            arrivalListItem.setQrCode(null);
        }
        if (!valid) {
            return false;
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
        clinicalOwnerList = aiService.getClinicalOwnerList(arrivalListItem.getClinicalDeptUID());
        return clinicalOwnerList;
    }

    public List<AssetInfo> getParentAssetList() {
        if (!Strings.isNullOrEmpty(arrivalListItem.getClinicalDeptUID())) {
            parentAssetList = assetMgrService.getParentsAssetsList(null, arrivalListItem.getClinicalDeptUID(), arrivalListItem.getArrivalList().getSiteUID());
        } else {
            parentAssetList = Collections.EMPTY_LIST;
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
//        owner1 = aiService.getUserAccountByUid(arrivalListItem.getAssetOwnerUID());
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
//                WebUtil.addErrorMessage("二维码不正确");
                arrivalListItem.setQrCode("");
            } else {
                oldQrCode = arrivalListItem.getQrCode();
            }
        } else {
            oldQrCode = "";
        }
        onDepartNumChange("qrCode");
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
//                            arrivalListItem.setQrCode(num);
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
            case "clinical":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getClinicalAckStatus())) {
                    arrivalListItem.setClinicalAckDate(new Date());
                } else {
                    arrivalListItem.setClinicalAckDate(null);
                }
            case "business":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getBusinessAckStatus())) {
                    arrivalListItem.setBusinessAckDate(new Date());
                } else {
                    arrivalListItem.setBusinessAckDate(null);
                }
            case "fixasset":
                if (ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getFixassetAckStatus())) {
                    arrivalListItem.setFixassetAckDate(new Date());
                } else {
                    arrivalListItem.setFixassetAckDate(null);
                }
        }
    }

    public List<String> autoCompleteDept(String query) {
        List<String> res = deptInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).map(OrgInfo::getName).collect(Collectors.toList());
        res.sort(new PinYinComparator());
        return res;
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

    public void exportReport(String reportType) {
        //List<ArrivalListItem> tempList = new ArrayList();
        //tempList.add(this.selected);
        try {
            Map<String, Object> data = assetInstallationReportService.createReportData(arrivalListItem);
            //List<Map<String, Object>> data = assetInstallationReportService.createReportDataList(tempList);
            byte[] templateBytes = assetInstallationReportService.buildReportTemplate(reportType);

            if (templateBytes != null) {
                exportFile = assetInstallationReportService.exportDocx(data, templateBytes, reportType);
                //exportFile = assetInstallationReportService.exportZip(data, templateBytes, reportType);
            }
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
//        saveItem();
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

    //    public ArrivalListItem getArrivalListItem() {
//        return arrivalListItem;
//    }
//
//    public void setArrivalListItem(ArrivalListItem arrivalListItem) {
//        this.arrivalListItem = arrivalListItem;
//    }
//    public List<I18nMessage> getCode68List() {
//        return code68List;
//    }
//
//    public void setCode68List(List<I18nMessage> code68List) {
//        this.code68List = code68List;
//    }
//    public String getGbCodeName() {
//        return gbCodeName;
//    }
//
//    public void setGbCodeName(String gbCodeName) {
//        this.gbCodeName = gbCodeName;
//    }
//    public GbCode getSelectedGb() {
//        return selectedGb;
//    }
//
//    public void setSelectedGb(GbCode selectedGb) {
//        this.selectedGb = selectedGb;
//    }
//    public TreeNode getSelectedNode() {
//        return selectedNode;
//    }
//
//    public void setSelectedNode(TreeNode selectedNode) {
//        this.selectedNode = selectedNode;
//    }
//    public TreeNode getSelectedGbNode() {
//        return selectedGbNode;
//    }
//
//    public void setSelectedGbNode(TreeNode selectedGbNode) {
//        this.selectedGbNode = selectedGbNode;
//    }
//    public TreeNode getGbTree() {
//        return gbTree;
//    }
//
//    public void setGbTree(TreeNode gbTree) {
//        this.gbTree = gbTree;
//    }
//    public String getExportQrCodeTemplateId() {
//        return exportQrCodeTemplateId;
//    }
//
//    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
//        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
//    }
//    public String getDeptName() {
//        return deptName;
//    }
//
//    public void setDeptName(String deptName) {
//        this.deptName = deptName;
//    }
    public boolean isNeedInstorageCheck() {
        return needInstorageCheck;
    }

    public void setNeedInstorageCheck(boolean needInstorageCheck) {
        this.needInstorageCheck = needInstorageCheck;
    }

//    public UserAccount getOwner1() {
//        return owner1;
//    }
//
//    public void setOwner1(UserAccount owner1) {
//        this.owner1 = owner1;
//    }
//    public UserAccount getOwner2() {
//        return owner2;
//    }
//
//    public void setOwner2(UserAccount owner2) {
//        this.owner2 = owner2;
//    }
//    public UserAccount getOwner3() {
//        return owner3;
//    }
//
//    public void setOwner3(UserAccount owner3) {
//        this.owner3 = owner3;
//    }
//    public UserAccount getUseOwner() {
//        return useOwner;
//    }
//
//    public void setUseOwner(UserAccount useOwner) {
//        this.useOwner = useOwner;
//    }
//
//	public AssetInfo getParentAssetInfo() {
//		return parentAssetInfo;
//	}
//
//	public void setParentAssetInfo(AssetInfo parentAssetInfo) {
//		this.parentAssetInfo = parentAssetInfo;
//	}

    public boolean activeSupplier(){
        return supplierService.activeSupplier();
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
}
