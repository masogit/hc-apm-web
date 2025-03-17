package com.ge.apm.service.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service("mcApplyPurchaseService")
public class MaintenanceContractApplyPurchaseService {

    private static Logger logger = LoggerFactory.getLogger(MaintenanceContractApplyPurchaseService.class);


    public final static String CREATE_ACTION = "Create";
    public final static String EDIT_ACTION = "Edit";

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

    public boolean checkYearStart(UserAccount account) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<PurchaseBudgetConfig> purchaseBudgetConfigList = purchaseBudgetConfigRepository.queryEffiveConfig(account.getTenantUID(),
                account.getSiteUID(),sdf.parse(sdf.format(new Date())));
        if(CollectionUtils.isEmpty(purchaseBudgetConfigList)){
            return false;
        }
        return true;
    }


    public List<AssetInfo> queryAssetByCondition(UserAccount userAccount, BudgetAssetSearchForm assetSearchForm){
        List<AssetInfo> assetInfos = new ArrayList<>();
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));
        if(StrUtil.isNotEmpty(assetSearchForm.getApplicationDeptId())){
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, assetSearchForm.getApplicationDeptId()));
        }
        if(StrUtil.isNotEmpty(assetSearchForm.getAssetName())){
            filters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        if(StrUtil.isNotEmpty(assetSearchForm.getAssetBrand())){
            filters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if(StrUtil.isNotEmpty(assetSearchForm.getAssetModel())){
            filters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if(StrUtil.isNotEmpty(assetSearchForm.getSerialNum())){
            filters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, assetSearchForm.getSerialNum()));
        }
        if(StrUtil.isNotEmpty(assetSearchForm.getFinancingNum())){
            filters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, assetSearchForm.getFinancingNum()));
        }
        assetInfos = assetInfoRepository.findBySearchFilter(filters);
        return assetInfos;
    }

    public void saveSelectedAsset(AssetInfo assetInfo, List<MaintenanceContractApplyPurchaseAsset> subscribeAssetList){
        MaintenanceContractApplyPurchaseAsset maintenanceContractSubscribeAsset = new MaintenanceContractApplyPurchaseAsset();
        maintenanceContractSubscribeAsset.setAssetUid(assetInfo.getUid());
        maintenanceContractSubscribeAsset.setAssetInfo(assetInfo);
        maintenanceContractSubscribeAsset.setId(UUID.randomUUID().toString());
        subscribeAssetList.add(maintenanceContractSubscribeAsset);
    }

    public void initCreateObject(MaintenanceContractApplyPurchase applyPurchase, UserAccount account){
        OrgInfo orgInfo = orgDao.findByUid(account.getOrgInfoUID());
        if(orgInfo.getOrgType()==4){
            applyPurchase.setApplicationDeptName(orgInfo.getName());
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

    public OrgInfo getCourtyardAreaOrg(OrgInfo orgInfo){
        if(orgInfo!=null&& orgInfo.getOrgLevel()==orgInfo.getOrgType()){
            return orgInfo;
        }
        return getCourtyardAreaOrg(orgInfo.getParentOrg());
    }

    @Transactional
    public void saveData(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList, List<V2_BlobObject> blobObjects, List<String> removeBoIds, UserAccount userAccount,String actionName){

        if(StrUtil.isEmpty(applyPurchase.getId())){
            applyPurchase.setCreatedBy(userAccount.getName());
            applyPurchase.setCreatedByUid(userAccount.getUid());
            applyPurchase.setCreatedDate(new Date());
            OrgInfo curDept = applyPurchase.getApplicationDept();
            applyPurchase.setTenantUID(curDept.getTenantUID());
            applyPurchase.setInstitutionUID(curDept.getInstitutionUID());
            applyPurchase.setHospitalUID(curDept.getHospitalUID());
            applyPurchase.setSiteUID(curDept.getSiteUID());
            applyPurchase.setApplicationNum(genApplicationNum());
        }
        applyPurchase.setLastModifiedBy(userAccount.getName());
        applyPurchase.setLastModifiedDate(new Date());
        applyPurchase.setIsDeleted(0);
        maintenanceContractApplyPurchaseRepository.save(applyPurchase);

        if(EDIT_ACTION.equals(actionName)){
            maintenanceContractApplyPurchaseAssetRepository.deleteByApplicationId(applyPurchase.getId());
        }

        for(MaintenanceContractApplyPurchaseAsset ma : applyPurchaseAssetList){
            ma.setApplicationId(applyPurchase.getId());
        }
        maintenanceContractApplyPurchaseAssetRepository.save(applyPurchaseAssetList);

        if(blobObjects != null){
            for(V2_BlobObject b : blobObjects){
                b.setBoUuid(applyPurchase.getId());
            }
            blobObjectRepository.save(blobObjects);
        }


        if(CollectionUtils.isNotEmpty(removeBoIds)){
            for(String id : removeBoIds){
                blobFileService.deleteBlobFileById(id);
            }
        }
    }

    /**
     * 生成ApplicationNum
     * @return
     */
    private String genApplicationNum() {
        String prefix = "BAW" + DateUtil.format(new Date(), "yyyyMMdd");
        String o = maintenanceContractApplyPurchaseRepository.getMaxNum(prefix );
        int index = 1;
        if(StrUtil.isNotEmpty(o)){
            o = o.replace(prefix, "");
            index = Integer.valueOf(o);
            index++;
        }

        String applicationNum = prefix + String.format("%04d", index);
        return applicationNum;
    }

    /**
     * 提交审批
     * @param applyPurchase
     * @param applyPurchaseAssetList
     * @param blobObjectList
     * @param removeBoIds
     * @param userAccount
     */
    public void submitAudit(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList, List<V2_BlobObject> blobObjectList, List<String> removeBoIds, UserAccount userAccount, String actionName){
        applyPurchase.setApplicationStep(MaintenanceContractApplyPurchase.ApplicationStepEnum.ApprovalPending.name());
        applyPurchase.setApplicationStatus(MaintenanceContractApplyPurchase.StatusEnum.Requested.name());
        saveData(applyPurchase, applyPurchaseAssetList, blobObjectList, removeBoIds, userAccount, actionName);

        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(applyPurchase, false);
        MultiLevelApproval approval = generateApproval(applyPurchase, approvalConfig,userAccount);
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

    public boolean checkMultiLevelApproval (MaintenanceContractApplyPurchase applyPurchase, UserAccount account){
        boolean result = true;
        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(applyPurchase, false);
        MultiLevelApproval approval = generateApproval(applyPurchase, approvalConfig, account);
        if (approval == null) {
            result = false;
            logger.info("MaintenanceContractApplyPurchase MultiLevelApproval Failure");
        }
        return result;
    }

    public boolean checkRequiredField(MaintenanceContractApplyPurchase applyPurchase, List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList){
        boolean flag = true;
        if(applyPurchase.getApplicationDeptId() == null){
            WebUtil.addErrorMessage("申请科室 不能为空！");
            flag = false;
        }
        if(StrUtil.isEmpty(applyPurchase.getWardCode())){
            WebUtil.addErrorMessage("病区代码 不能为空！");
            flag = false;
        }
        if(CollectionUtils.isEmpty(applyPurchaseAssetList)){
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
                approval.get().setCreatedDate(new Date());
                approval.get().setCreatedBy(account.getLoginName());

                approval.get().setTenantUID(account.getTenantUID());
                approval.get().setHierOrgUid(account.getSiteUID());
            });
        });
        return approval.get();
    }

    private void sendMsg(MaintenanceContractApplyPurchase application, MultiLevelApprovalConfig config,  MultiLevelApproval approval) {
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
            approvalService.sendWxMsg(sendUserUids, message, "待审批","", MSGEvent.MULTI_LEVEL_APPROVAL_TO_BE_APPROVED,approval);
        });
    }

    public List<MaintenanceContractApplyPurchaseAsset> queryPurchaseAssetById(String applicationId){
        return maintenanceContractApplyPurchaseAssetRepository.findByApplicationId(applicationId);
    }

    public void fieldRender(MaintenanceContractApplyPurchaseStatusVo applyPurchaseStatusVo, MaintenanceContractApplyPurchase applyPurchase){
        if(MaintenanceContractApplyPurchase.ApplicationStepEnum.MeetingReasoningPending.name().equals(applyPurchase.getApplicationStep())){
            applyPurchaseStatusVo.setShowTech(true);
        }else if(MaintenanceContractApplyPurchase.ApplicationStepEnum.PurchaseConfirmationPending.name().equals(applyPurchase.getApplicationStep())){
            applyPurchaseStatusVo.setShowTech(true);
            applyPurchaseStatusVo.setShowMeetReasoning(true);
        }else if(MaintenanceContractApplyPurchase.ApplicationStepEnum.Complete.name().equals(applyPurchase.getApplicationStep())){
            if(MaintenanceContractApplyPurchase.StatusEnum.ApprovalReject.name().equals(applyPurchase.getApplicationStatus())){
                applyPurchaseStatusVo.setShowTech(true);
            }else if(MaintenanceContractApplyPurchase.StatusEnum.MeetingReasoningReject.name().equals(applyPurchase.getApplicationStatus())){
                applyPurchaseStatusVo.setShowTech(true);
                applyPurchaseStatusVo.setShowMeetReasoning(true);
            }else{
                applyPurchaseStatusVo.setShowTech(true);
                applyPurchaseStatusVo.setShowMeetReasoning(true);
                applyPurchaseStatusVo.setShowPurchaseConfirm(true);
            }
        }
    }
}
