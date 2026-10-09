package com.ge.apm.service.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service("mcApplyPurchaseService")
public class MaintenanceContractApplyPurchaseService {

    private static Logger logger = LoggerFactory.getLogger(MaintenanceContractApplyPurchaseService.class);


    public final static String CREATE_ACTION = "Create";
    public final static String EDIT_ACTION = "Edit";

    public static final String FILE_NAME_ROOT_MC = "医疗设备维保项目申请表";

    public static final String ATTACHMENT = "附件";

    @Autowired
    PurchaseBudgetConfigRepository purchaseBudgetConfigRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private MaintenanceContractApplyPurchaseRepository maintenanceContractApplyPurchaseRepository;
    @Autowired
    private MaintenanceContractApplyPurchaseAssetRepository maintenanceContractApplyPurchaseAssetRepository;
    @Autowired
    private BlobObjectRepository blobObjectRepository;
    @Autowired
    private BlobFileService blobFileService;
    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private MultiLevelApprovalService approvalService;
    @Autowired
    private MultiLevelApprovalRepository approvalDao;
    @Autowired
    private UserAccountRepository userDao;

    private MicroServiceUtil msService;

    private FileDownloadService fileDownloadService;
    @Autowired
    private ConfigCacheService configCacheService;
    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
    }

    public boolean checkYearStart(UserAccount account) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<PurchaseBudgetConfig> purchaseBudgetConfigList = purchaseBudgetConfigRepository.queryEffiveConfig(account.getTenantUID(),
                account.getSiteUID(), sdf.parse(sdf.format(new Date())));
        if (CollectionUtils.isEmpty(purchaseBudgetConfigList)) {
            return false;
        }
        return true;
    }


    public List<AssetInfo> queryAssetByCondition(UserAccount userAccount, BudgetAssetSearchForm assetSearchForm) {
        List<AssetInfo> assetInfos = new ArrayList<>();
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));
        if (StrUtil.isNotEmpty(assetSearchForm.getApplicationDeptId())) {
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, assetSearchForm.getApplicationDeptId()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetName())) {
            filters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetBrand())) {
            filters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetModel())) {
            filters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getSerialNum())) {
            filters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, assetSearchForm.getSerialNum()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getFinancingNum())) {
            filters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, assetSearchForm.getFinancingNum()));
        }
        assetInfos = assetInfoRepository.findBySearchFilter(filters);
        return assetInfos;
    }

    public void saveSelectedAsset(Map<String, Boolean> selectedMap, List<MaintenanceContractApplyPurchaseAsset> subscribeAssetList) {
        List<AssetInfo> assetInfoList = new ArrayList<>();
        for (String key : selectedMap.keySet()){
            boolean v = selectedMap.get(key);
            if(v){
                AssetInfo assetInfo = assetInfoRepository.findByUid(key);
                assetInfoList.add(assetInfo);

            }
        }

        List<AssetInfo> addAsset = assetInfoList.stream().filter(t -> {
            boolean r = true;
            for (MaintenanceContractApplyPurchaseAsset pa : subscribeAssetList){
                if(pa.getAssetInfo().getUid().equals(t.getUid())){
                    r = false;
                    break;
                }
            }
            return r;
        }).collect(Collectors.toList());

        for(AssetInfo assetInfo : addAsset){
            MaintenanceContractApplyPurchaseAsset maintenanceContractSubscribeAsset = new MaintenanceContractApplyPurchaseAsset();
            maintenanceContractSubscribeAsset.setAssetUid(assetInfo.getUid());
            maintenanceContractSubscribeAsset.setAssetInfo(assetInfo);
            maintenanceContractSubscribeAsset.setId(UUID.randomUUID().toString());
            subscribeAssetList.add(maintenanceContractSubscribeAsset);
        }
    }

    public void initCreateObject(MaintenanceContractApplyPurchase applyPurchase, UserAccount account) {
        OrgInfo orgInfo = orgDao.findByUid(account.getOrgInfoUID());
        if (Objects.equals(orgInfo.getOrgType(), 4)) {
            applyPurchase.setApplicationDeptName(orgInfo.getName());
            applyPurchase.setApplicationDeptId(orgInfo.getId());
            applyPurchase.setApplicationDept(orgInfo);
        }
        OrgInfo orgArea = getCourtyardAreaOrg(orgInfo);
        applyPurchase.setCourtyardArea(orgArea.getName());
        applyPurchase.setCourtyardAreaId(orgArea.getId());
        applyPurchase.setApplicantId(account.getId());
        applyPurchase.setApplicantName(account.getName());
        applyPurchase.setApplicationDate(new Date());
        applyPurchase.setApplicationStep(MaintenanceContractApplyPurchase.ApplicationStepEnum.SubmitPending.name());
        applyPurchase.setApplicationStatus(MaintenanceContractApplyPurchase.StatusEnum.TempSave.name());
    }

    public OrgInfo getCourtyardAreaOrg(OrgInfo orgInfo) {
        if (orgInfo != null && Objects.equals(orgInfo.getOrgLevel(), orgInfo.getOrgType())) {
            return orgInfo;
        }
        return getCourtyardAreaOrg(orgInfo.getParentOrg());
    }

    @Transactional
    public void saveData(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList, List<V2_BlobObject> blobObjects, List<String> removeBoIds, UserAccount userAccount, String actionName) {

        if (StrUtil.isEmpty(applyPurchase.getId())) {
            applyPurchase.setCreatedBy(userAccount.getName());
            applyPurchase.setCreatedByUid(userAccount.getUid());
            applyPurchase.setCreatedDate(new Date());
            OrgInfo curDept = applyPurchase.getApplicationDept();
            applyPurchase.setTenantUID(curDept.getTenantUID());
            applyPurchase.setInstitutionUID(curDept.getInstitutionUID());
            applyPurchase.setHospitalUID(curDept.getHospitalUID());
            applyPurchase.setSiteUID(curDept.getSiteUID());
            applyPurchase.setOrgUid(curDept.getUid());
            applyPurchase.setApplicationNum(genApplicationNum());
        }
        applyPurchase.setLastModifiedBy(userAccount.getName());
        applyPurchase.setLastModifiedDate(new Date());
        applyPurchase.setIsDeleted(0);
        maintenanceContractApplyPurchaseRepository.save(applyPurchase);

        if (EDIT_ACTION.equals(actionName)) {
            maintenanceContractApplyPurchaseAssetRepository.deleteByApplicationId(applyPurchase.getId());
        }

        for (MaintenanceContractApplyPurchaseAsset ma : applyPurchaseAssetList) {
            ma.setApplicationId(applyPurchase.getId());
        }
        maintenanceContractApplyPurchaseAssetRepository.save(applyPurchaseAssetList);

        if (blobObjects != null) {
            for (V2_BlobObject b : blobObjects) {
                b.setBoUuid(applyPurchase.getId());
            }
            blobObjectRepository.save(blobObjects);
        }


        if (CollectionUtils.isNotEmpty(removeBoIds)) {
            for (String id : removeBoIds) {
                blobFileService.deleteBlobFileById(id);
            }
        }
    }

    /**
     * 生成ApplicationNum
     *
     * @return
     */
    private String genApplicationNum() {
        String prefix = "BAW" + DateUtil.format(new Date(), "yyyyMMdd");
        String o = maintenanceContractApplyPurchaseRepository.getMaxNum(prefix + "%");
        int index = 1;
        if (StrUtil.isNotEmpty(o)) {
            o = o.replace(prefix, "");
            index = Integer.valueOf(o);
            index++;
        }

        String applicationNum = prefix + String.format("%04d", index);
        return applicationNum;
    }

    /**
     * 提交审批
     *
     * @param applyPurchase
     * @param applyPurchaseAssetList
     * @param blobObjectList
     * @param removeBoIds
     * @param userAccount
     */
    public void submitAudit(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList, List<V2_BlobObject> blobObjectList, List<String> removeBoIds, UserAccount userAccount, String actionName) {
        applyPurchase.setApplicationStep(MaintenanceContractApplyPurchase.ApplicationStepEnum.ApprovalPending.name());
        applyPurchase.setApplicationStatus(MaintenanceContractApplyPurchase.StatusEnum.Requested.name());
        saveData(applyPurchase, applyPurchaseAssetList, blobObjectList, removeBoIds, userAccount, actionName);

        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(applyPurchase, false);
        MultiLevelApproval approval = generateApproval(applyPurchase, approvalConfig, userAccount);
        if (approval == null) {
            // 无可用的审批流
            logger.info("budget message 001: new budget application, but no approval configuration is available");
        } else {
            // 正常
            logger.info("budget message 002: new budget application");
            applyPurchase.setApprovalConfigId(approval.getWorkflowConfigApproveUid());
            approvalDao.save(approval);
            maintenanceContractApplyPurchaseRepository.save(applyPurchase);
            sendMsg(applyPurchase, approvalConfig, approval);
        }
    }

    public String checkMultiLevelApproval(MaintenanceContractApplyPurchase applyPurchase, UserAccount account) {
        String result = "";
        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(applyPurchase, false);
        MultiLevelApproval approval = generateApproval(applyPurchase, approvalConfig, account);
        if (approval == null) {
            result = "失败:无可用的审批流,请检查后重试！";
            logger.info("MaintenanceContractApplyPurchase MultiLevelApproval Failure");
        }else{
            List<ApprovalNode> approvalNodes = JSON.parseObject(approvalConfig.getApprovalItem(), new TypeReference<List<ApprovalNode>>() {});
            if(approvalNodes.size() != 3){
                result = "失败:多级审批节点设置有误，请重新设置！";
            }
        }

        return result;
    }

    public boolean checkRequiredField(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList) {
        boolean flag = true;
        if (applyPurchase.getApplicationDeptId() == null) {
            WebUtil.addErrorMessage("申请科室 不能为空！");
            flag = false;
        }
        if (StrUtil.isEmpty(applyPurchase.getWardCode())) {
            WebUtil.addErrorMessage("病区代码 不能为空！");
            flag = false;
        }
        if (CollectionUtils.isEmpty(applyPurchaseAssetList)) {
            WebUtil.addErrorMessage("保养设备 不能为空！");
            flag = false;
        }
        return flag;
    }

    public MultiLevelApproval generateApproval(MaintenanceContractApplyPurchase application, MultiLevelApprovalConfig approvalConfig, UserAccount account) {
        AtomicReference<MultiLevelApproval> approval = new AtomicReference<>(null);
        Optional.ofNullable(approvalConfig).flatMap(config -> Optional.ofNullable(config.getApprovalItem())).ifPresent(item -> {
            List<ApprovalNode> approvalNodes = JSON.parseObject(item, new TypeReference<List<ApprovalNode>>() {
            });
            Optional.ofNullable(approvalNodes).flatMap(nodes -> Optional.ofNullable(nodes.get(0))).ifPresent(node -> {
                approval.set(new MultiLevelApproval());
                if (node.getUsingRole()) {
                    // 角色审批, 目前ACCT没有角色审批
                    Optional.ofNullable(node.getRoleUids()).ifPresent(list -> {
                        approval.get().setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                        approval.get().setApprovalUid(StringUtils.join(list.toArray(), ","));
                    });
                } else {
                    // 用户审批
                    Optional.ofNullable(node.getUserUids()).ifPresent(list -> {
                        approval.get().setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                        approval.get().setApprovalUid(StringUtils.join(list.toArray(), ","));
                    });
                }
                approval.get().setBusinessNumber(application.getBusinessNumber());
                approval.get().setBusinessType(application.getBusinessType());
                approval.get().setBusinessUid(application.getBusinessUid());
                approval.get().setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                approval.get().setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                approval.get().setApprovalLevel(1);
                approval.get().setApprovalNumber(approvalService.autoGenerateFinacingNum());
                approval.get().setApprovalIng(false);
                approval.get().setApprovalLevelNow(1);
                approval.get().setWorkflowConfigApproveUid(approvalConfig.getId());
                approval.get().setInitiateApproverUid(account.getUid());
                approval.get().setApplicationUid(account.getUid());
                approval.get().setApplicationName(account.getName());
                approval.get().setCreatedDate(new Date());
                approval.get().setCreatedBy(account.getLoginName());
                setApplicationDepartment(account,approval.get());
                approval.get().setTenantUID(account.getTenantUID());
                approval.get().setHierOrgUid(account.getSiteUID());
                approval.get().setConfigOrgUid(approvalConfig.getHierOrgUid());
            });
        });
        return approval.get();
    }
    private void setApplicationDepartment( UserAccount userAccount, MultiLevelApproval multiLevelApproval) {
        OrgInfo  org= configCacheService.findOrgByUid(userAccount.getOrgInfoUID());

        multiLevelApproval.setApplicationDepartment(org==null?null:Objects.equals(org.getOrgType(), 4)?org.getName():null);
        multiLevelApproval.setApplicationDepartmentUid(org==null?null:Objects.equals(org.getOrgType(), 4)?org.getUid():null);

    }
    private void sendMsg(MaintenanceContractApplyPurchase application, MultiLevelApprovalConfig config, MultiLevelApproval approval) {
        Optional.ofNullable(config.getApprovalItem()).flatMap(item ->
                Optional.ofNullable(JSON.parseObject(item, new TypeReference<List<ApprovalNode>>() {
                })).flatMap(nodes -> Optional.ofNullable(nodes.get(0)))).ifPresent(node -> {
            List<String> sendUserUids = ListUtil.newArrayList();
            if (node.getUsingRole()) {
                // 角色审批, 目前ACCT没有角色审批
                Optional.ofNullable(node.getRoleUids()).flatMap(list -> Optional.ofNullable(userDao.getApprovalPersonByRoleId(list)))
                        .ifPresent(userList -> userList.stream().filter(user -> ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getUid()))
                                .forEach(user -> sendUserUids.add(user.getUid())));
            } else {
                // 用户审批
                Optional.ofNullable(node.getUserUids()).flatMap(list -> Optional.ofNullable(userDao.findByUidIn(list)))
                        .ifPresent(userList -> userList.stream().filter(user -> ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getWeChatId(), user.getAppAlias()))
                                .forEach(user -> sendUserUids.add(user.getUid())));
            }
            // 发送消息
            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2
                    + application.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
            approvalService.sendWxMsg(sendUserUids, message, "待审批", "", MSGEvent.MULTI_LEVEL_APPROVAL_TO_BE_APPROVED, approval);
        });
    }

    public List<MaintenanceContractApplyPurchaseAsset> queryPurchaseAssetById(String applicationId) {
        return maintenanceContractApplyPurchaseAssetRepository.findByApplicationId(applicationId);
    }

    public void fieldRender(MaintenanceContractApplyPurchaseStatusVo applyPurchaseStatusVo, MaintenanceContractApplyPurchase applyPurchase) {
        if (MaintenanceContractApplyPurchase.ApplicationStepEnum.MeetingReasoningPending.name().equals(applyPurchase.getApplicationStep())) {
            applyPurchaseStatusVo.setShowTech(true);
        } else if (MaintenanceContractApplyPurchase.ApplicationStepEnum.PurchaseConfirmationPending.name().equals(applyPurchase.getApplicationStep())) {
            applyPurchaseStatusVo.setShowTech(true);
            applyPurchaseStatusVo.setShowMeetReasoning(true);
        } else if (MaintenanceContractApplyPurchase.ApplicationStepEnum.Complete.name().equals(applyPurchase.getApplicationStep())) {
            if (MaintenanceContractApplyPurchase.StatusEnum.ApprovalReject.name().equals(applyPurchase.getApplicationStatus())) {
                applyPurchaseStatusVo.setShowTech(true);
            } else if (MaintenanceContractApplyPurchase.StatusEnum.MeetingReasoningReject.name().equals(applyPurchase.getApplicationStatus())) {
                applyPurchaseStatusVo.setShowTech(true);
                applyPurchaseStatusVo.setShowMeetReasoning(true);
            } else {
                applyPurchaseStatusVo.setShowTech(true);
                applyPurchaseStatusVo.setShowMeetReasoning(true);
                applyPurchaseStatusVo.setShowPurchaseConfirm(true);
            }
        }
    }


    public StreamedContent exportZip(String id) throws IOException {
        MaintenanceContractApplyPurchase applyPurchase = maintenanceContractApplyPurchaseRepository.findById(id);
        List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList = maintenanceContractApplyPurchaseAssetRepository.findByApplicationId(id);
        List<V2_BlobObject> blobObjects = blobObjectRepository.findByBoUuid(id);
        StreamedContent streamedContent = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ZipOutputStream zout = new ZipOutputStream(bout)) {
            generateSingleApply(zout, applyPurchase, applyPurchaseAssetList, blobObjects);
        }
        String name = this.buildFileName();
        streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(bout.toByteArray())), "application/zip", name+".zip");
        return streamedContent;
    }

    public void generateSingleApply(ZipOutputStream zout, MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList, List<V2_BlobObject> blobObjects) throws IOException {
        Map<String, Object> data = createReportData(applyPurchase, applyPurchaseAssetList, blobObjects);
        byte[] templateBytes = fileDownloadService.getTemplateFileByte(SystemTemplateEnum.BUDGET_MAINTENANCE_CONTRACT_APPLY_PURCHASE_DOCX.getCode(), SystemTemplateEnum.BUDGET_MAINTENANCE_CONTRACT_APPLY_PURCHASE_DOCX.getFileType());
        byte[] bytes = this.renderDocx(data, templateBytes);

        String fileName = applyPurchase.getApplicationNum() + ".docx";
        zout.putNextEntry(new ZipEntry( FILE_NAME_ROOT_MC+ "/" + fileName));
        IOUtils.write(bytes, zout);
        zout.closeEntry();
        HashMap<String,Integer> nameMap = new HashMap<>();
        if(blobObjects != null && blobObjects.size() > 0) {
            getFileBufferedInputStream(zout, blobObjects);
        }
    }

    public Map<String, Object> createReportData(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList, List<V2_BlobObject> blobObjects) {
        Map<String, Object> mm = new HashMap<>();
        mm.put("courtyardArea", applyPurchase.getCourtyardArea());
        mm.put("deptName", applyPurchase.getApplicationDeptName());
        mm.put("equipmentDescription", applyPurchase.getEquipmentDescription());
        mm.put("remark", applyPurchase.getRemark());
        mm.put("techAnalysisDiscussion", applyPurchase.getTechAnalysisDiscussion());
        mm.put("meetAuditComment", applyPurchase.getMeetReasoningResult());

        String assetNames = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null && StrUtil.isNotEmpty(t.getAssetInfo().getName())).map(pa -> pa.getAssetInfo().getName()).collect(Collectors.joining("、"));
        String makerShortNames = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null && StrUtil.isNotEmpty(t.getAssetInfo().getMakerShortName())).map(pa -> pa.getAssetInfo().getMakerShortName()).collect(Collectors.joining("、"));
        String functionTypes = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null && StrUtil.isNotEmpty(t.getAssetInfo().getFunctionType())).map(pa -> pa.getAssetInfo().getFunctionType()).collect(Collectors.joining("、"));
        String serialNums = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null && StrUtil.isNotEmpty(t.getAssetInfo().getSerialNum())).map(pa -> pa.getAssetInfo().getSerialNum()).collect(Collectors.joining("、"));
        String financingNums = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null && StrUtil.isNotEmpty(t.getAssetInfo().getFinancingNum())).map(pa -> pa.getAssetInfo().getFinancingNum()).collect(Collectors.joining("、"));
        String installDates = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null &&t.getAssetInfo().getInstallDate() != null).map(pa -> DateUtil.format(pa.getAssetInfo().getInstallDate(), "yyyy-MM-dd")).collect(Collectors.joining("、"));
        String purchasePrices = applyPurchaseAssetList.stream().filter(t -> t.getAssetInfo() != null && t.getAssetInfo().getPurchasePrice() != null).map(pa -> pa.getAssetInfo().getPurchasePrice().toString()).collect(Collectors.joining("、"));

        mm.put("assetNames", assetNames);
        mm.put("makerShortNames", makerShortNames);
        mm.put("functionTypes", functionTypes);
        mm.put("serialNums", serialNums);
        mm.put("financingNums", financingNums);
        mm.put("installDates", installDates);
        mm.put("purchasePrices", purchasePrices);

        return mm;
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {
        Map<String, RenderPolicy> bindings = new HashMap<>();
        return  WordExportServiceUtil.generate(data, templateBytes, bindings);
    }

    private String buildFileName(){
        String reportTypeTitle = FILE_NAME_ROOT_MC;
        return reportTypeTitle + "-" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
    }

    private String encoding(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return fileName;
        }
    }

    public void getFileBufferedInputStream(ZipOutputStream zout, List<V2_BlobObject> blobObjects) throws IOException{
        HashMap<String,Integer> nameMap = new HashMap<>();
        for (V2_BlobObject v : blobObjects) {
            String fn = v.getObjectName();
            String newFileName = handleRepeatFileName(nameMap, fn);
            StreamedContent sf = null;
            try{
                sf = msService.downloadSingleFile(v.getObjectStorageId());
            }catch (Exception e){
                e.printStackTrace();
                logger.error("file download fail", e);
                continue;
            }
            zout.putNextEntry(new ZipEntry(ATTACHMENT + "/" + newFileName));
            byte[] bys = new byte[1024 * 5];
            try( BufferedInputStream bufferStream = new BufferedInputStream(sf.getStream())){
                int rd = 0;
                while ((rd = bufferStream.read(bys)) != -1) {
                    zout.write(bys, 0, rd);
                }
                zout.closeEntry();
            }
        }
    }

    public String handleRepeatFileName(HashMap<String,Integer> nameMap, String fileName){
        String newFileName = fileName;
        if(nameMap.containsKey(fileName)){
            Integer next = nameMap.get(fileName) + 1;
            int lo = fileName.lastIndexOf(".");
            if(lo >-1){
                String pre = fileName.substring(0,lo);
                pre += "(" + next +")";
                newFileName = pre + fileName.substring(lo);
                //递归检查新的fileName是否重复
                newFileName = handleRepeatFileName(nameMap, newFileName);
            }
            nameMap.put(fileName, next);
        }else{
            nameMap.put(fileName, 0);
        }
        return newFileName;
    }

    @Transactional
    public void deleteApply(String id){
        maintenanceContractApplyPurchaseAssetRepository.deleteByApplicationId(id);
        maintenanceContractApplyPurchaseRepository.delete(id);
        List<V2_BlobObject> blobObjects = blobObjectRepository.findByBoUuid(id);
        for(V2_BlobObject b : blobObjects){
            blobFileService.deleteBlobFile(b);
        }
    }
}