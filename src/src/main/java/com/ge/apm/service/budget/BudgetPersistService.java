package com.ge.apm.service.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.BudgetApplicationMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.budget.BudgetConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * LYJ
 * 2023.10.18
 */
@Service
public class BudgetPersistService {
    private static Logger logger = LoggerFactory.getLogger(BudgetPersistService.class);

    @Autowired
    private BudgetApplicationRepository budgetApplicationDao;
    @Autowired
    private BudgetAssetInfoRepository budgetAssetInfoDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private MultiLevelApprovalRepository approvalDao;
    @Autowired
    private MultiLevelApprovalService approvalService;
    @Autowired
    private BudgetApplicationMapper budgetApplicationMapper;
    @Autowired
    private BlobFileService blobService;
    @Autowired
    private OrgInfoRepository orgDao;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Transactional(rollbackFor = Exception.class)
    public boolean saveApplication(BudgetApplication application,List<V2_BlobObject> attachList,List<String> removeFileBlobIds) {
        if (!Optional.ofNullable(application.getId()).isPresent()) { // 新增  数据状态和步骤为空
            // 保存以获得ID
            this.genApplicationNum(application,application.getOrderType());
        }
        BudgetApplicationAsset budgetApplicationAsset = application.getBudgetAssets().iterator().next();
        budgetApplicationAsset.setApplicationNum(application.getApplicationNum());
        budgetApplicationAsset.setApplicationDeptId(application.getApplicationDeptId());
        application = budgetApplicationDao.save(application);
        BudgetApplicationAsset budgetApplicationAssetPersistent = application.getBudgetAssets().iterator().next();
        //明细关联的主键id 没有带出 需要放入 或者在字段上设置 不可以更新
        budgetApplicationAssetPersistent.setApplicationId(application.getId());
        if(CollectionUtils.isNotEmpty(attachList)){
            attachList.stream().forEach(att -> att.setBoUuid(budgetApplicationAssetPersistent.getId()));
        }
        //去掉删除的数据
        if(CollectionUtils.isNotEmpty(removeFileBlobIds)){
            removeFileBlobIds.stream().forEach(blobId ->{
                blobService.deleteBlobFileById(blobId);
            });
        }
        blobService.saveBlobObject(attachList);
        return true;
    }


    public void handleCommonField(BudgetApplication budgetApplication,
                                               Integer applicationStatus,String applicationStep,UserAccount account,String crudActionName,BudgetAssetInfo budgetAssetInfo){
        OrgInfo curDept = budgetApplication.getApplicationDept();
        budgetApplication.setLastModifiedBy(account.getLoginName());
        budgetApplication.setLastModifiedDate(new Date());
        budgetApplication.setApplicationDept(curDept);
        budgetApplication.setApplicationDeptId(curDept.getId());
        budgetApplication.setTenantUID(curDept.getTenantUID());
        budgetApplication.setInstitutionUID(curDept.getInstitutionUID());
        budgetApplication.setHospitalUID(curDept.getHospitalUID());
        budgetApplication.setSiteUID(curDept.getSiteUID());
        budgetApplication.setApplicationStatus(applicationStatus);
        budgetApplication.setApplicationStep(applicationStep);

        BudgetApplicationAsset budgetApplicationAsset = budgetApplication.getBudgetAssets().iterator().next();
        //设备信息
        if(budgetApplicationAsset.getCreatedDate()== null){
            budgetApplicationAsset.setCreatedDate(new Date());
            budgetApplicationAsset.setCreatedBy(account.getLoginName());
        }
        budgetApplicationAsset.setLastModifiedBy(account.getLoginName());
        budgetApplicationAsset.setLastModifiedDate(new Date());
        budgetApplicationAsset.setTenantUID(curDept.getTenantUID());
        budgetApplicationAsset.setInstitutionUID(curDept.getInstitutionUID());
        budgetApplicationAsset.setHospitalUID(curDept.getHospitalUID());
        budgetApplicationAsset.setSiteUID(curDept.getSiteUID());
        budgetApplicationAsset.setIsDeleted(0);
        if("Create".equals(crudActionName)){
            budgetApplicationAsset.setApplicationAssetName(budgetAssetInfo.getName());
        }
    }

    public boolean checkMultiLevelApproval (BudgetApplication application){
        boolean result = true;
        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(application, false);
        MultiLevelApproval approval = generateApproval(application, approvalConfig);
        if (approval == null) {
            result = false;
            logger.info("budget message 001: new budget application, but no approval configuration is available");
        }
        return result;
    }

    public void initCreateObject(BudgetApplication application,BudgetApplicationAsset budgetApplicationAsset,UserAccount account,String orderType){
        Set<BudgetApplicationAsset> budgetApplicationAssetSet = new HashSet<BudgetApplicationAsset>();
        budgetApplicationAssetSet.add(budgetApplicationAsset);
        application.setBudgetAssets(budgetApplicationAssetSet);
        application.setApplicationStep(BudgetApplication.APPLICATIONSTEP.ToSubmitted.toString());
        application.setApplicant(account);
        application.setApplicantId(account.getId());
        application.setApplicationDate(new Date());
        application.setApplicationStatus(BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING);
        application.setBudgetApprovals(new HashSet<>());
        application.setIsDeleted(0);
        application.setCreatedBy(account.getLoginName());
        application.setCreatedDate(new Date());
        application.setLastModifiedBy(account.getLoginName());
        application.setLastModifiedDate(new Date());
        application.setOrderType(orderType);
        OrgInfo orgInfo = orgDao.findByUid(account.getOrgInfoUID());
        if(orgInfo.getOrgType()==4){
            application.setApplicationDeptName(orgInfo.getName());
            application.setApplicationDept(orgInfo);
        }
        application.setCourtyardArea(getCourtyardArea(orgInfo));
    }

    public String getCourtyardArea(OrgInfo orgInfo){
        String orgName = new String();
        if(orgInfo!=null&& orgInfo.getOrgLevel()==orgInfo.getOrgType()){
            orgName = orgInfo.getName();
            return orgName;
        }
        return getCourtyardArea(orgInfo.getParentOrg());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean submitApplication(BudgetApplication application,List<V2_BlobObject> attachList) {
        this.saveApplication(application,attachList,null);
        boolean isEditAction = Optional.ofNullable(application.getId()).isPresent();
       /* if (true) {*/
            MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(application, false);
            MultiLevelApproval approval = generateApproval(application, approvalConfig);
            if (approval == null) {
                // 无可用的审批流
                logger.info("budget message 001: new budget application, but no approval configuration is available");
            } else {
                // 正常
                logger.info("budget message 002: new budget application");
                application.setApprovalConfigId(approval.getWorkflowConfigApproveUid());
                approvalDao.save(approval);
                budgetApplicationDao.save(application);
                sendMsg(application, approvalConfig);
            }
       /* } else {
            // 编辑
            Set<MultiLevelApproval> budgetApprovals = application.getBudgetApprovals();
            MultiLevelApprovalConfig newApprovalConfig = approvalService.getFirstApprovalList(application, false);
            if (newApprovalConfig == null) {
                // 无可用的审批流
                logger.info("budget message 003: edit budget application, but no approval configuration is available");
                if (CollectionUtils.isNotEmpty(budgetApprovals)) {
                    // 删除, 之前发送过消息
                    approvalDao.delete(budgetApprovals);
                }
                application.setApprovalConfigId(null);
            } else if (newApprovalConfig.getId().equals(application.getApprovalConfigId())) {
                // 总金额变化没有导致审批流变化
                logger.info("budget message 004: edit budget application, no need to update approval configuration");
            } else {
                // 总金额变化导致审批流变化
                logger.info("budget message 005: edit budget application, need to update approval configuration");
                application.setApprovalConfigId(newApprovalConfig.getId());
                // 更新审批单, 最多只会有一个审批单
                MultiLevelApproval newApproval = generateApproval(application, newApprovalConfig);
                if (newApproval != null) {
                    if (CollectionUtils.isNotEmpty(budgetApprovals)) {
                        budgetApprovals.forEach(target -> {
                            // 由于set方法的逻辑需要先去掉第一个和最后一个逗号
                            String s = newApproval.getApprovalUid();
                            target.setApprovalUid(StringUtils.isNotEmpty(s) ? s.substring(1, s.length() - 1) : s);
                            target.setWorkflowConfigApproveUid(newApproval.getWorkflowConfigApproveUid());
                            target.setInitiateApproverUid(newApproval.getInitiateApproverUid());
                            target.setCreatedDate(newApproval.getCreatedDate());
                            target.setCreatedBy(newApproval.getCreatedBy());

                            target.setTenantUID(newApproval.getTenantUID());
                            target.setHierOrgUid(newApproval.getHierOrgUid());
                        });
                    } else {
                        budgetApprovals.add(newApproval);
                    }
                    approvalDao.save(budgetApprovals);
                    sendMsg(application, newApprovalConfig);
                }
            }
            budgetApplicationDao.save(application);

        }*/
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApplication(BudgetApplication application) {
        if(CollectionUtils.isNotEmpty(application.getBudgetApprovals())){
            approvalDao.delete(application.getBudgetApprovals());
        }
        budgetApplicationDao.delete(application);
        return true;
    }

    public MultiLevelApproval generateApproval(BudgetApplication application, MultiLevelApprovalConfig approvalConfig) {
        UserAccount account = UserContextService.getCurrentUserAccount();
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
                approval.get().setCreatedDate(new Date());
                approval.get().setCreatedBy(account.getLoginName());

                approval.get().setTenantUID(account.getTenantUID());
                approval.get().setHierOrgUid(account.getSiteUID());
            });
        });
        return approval.get();
    }
    private void sendMsg(BudgetApplication application, MultiLevelApprovalConfig config) {
        Optional.ofNullable(config.getApprovalItem()).flatMap(item ->
                Optional.ofNullable(JSON.parseObject(item, new TypeReference<List<ApprovalNode>>() {
                })).flatMap(nodes -> Optional.ofNullable(nodes.get(0)))).ifPresent(node -> {
            List<WechatParam> paramList = ListUtil.newArrayList();
            if (node.getUsingRole()) {
                // 角色审批, 目前ACCT没有角色审批
                Optional.ofNullable(node.getRoleUids()).flatMap(list -> Optional.ofNullable(userDao.getApprovalPersonByRoleId(list)))
                        .ifPresent(userList -> userList.stream().filter(user -> ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getWeChatId(), user.getAppAlias()))
                                .forEach(user -> paramList.add(new WechatParam(user.getWeChatId(), user.getAppAlias()))));
            } else {
                // 用户审批
                Optional.ofNullable(node.getUserUids()).flatMap(list -> Optional.ofNullable(userDao.findByUidIn(list)))
                        .ifPresent(userList -> userList.stream().filter(user -> ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getWeChatId(), user.getAppAlias()))
                                .forEach(user -> paramList.add(new WechatParam(user.getWeChatId(), user.getAppAlias()))));
            }
            // 发送消息
            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2
                    + application.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
            approvalService.sendWxMsg(paramList, message, "待审批","");
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveAsset(BudgetAssetInfo assetInfo) {
        budgetAssetInfoDao.save(assetInfo);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAsset(BudgetAssetInfo assetInfo) {
        budgetAssetInfoDao.delete(assetInfo);
        return true;
    }
    /**
     * 给申请单添加单号和索引
     *
     * @param
     * @return
     */
    private BudgetApplication genApplicationNum(BudgetApplication budgetApplication, String orderType) {
        Object o = budgetApplicationDao.getMaxIndex(orderType );
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String applicationNum = orderType + simpleDateFormat.format(new Date()) + String.format("%04d", index);
        budgetApplication.setApplicationNum(applicationNum);
        budgetApplication.setApplicationNumIndex(index);
        return budgetApplication;
    }

    public List<BudgetApplication> loadDataList(List<SearchFilter> searchFilters){
        return budgetApplicationMapper.loadDataList(searchFilters);
    }


    public List<Map<String, Object>> createBudgeApplicationData(List<BudgetApplication> budgetApplicationList,String orderType) {
        List<Map<String, Object>> budgeApplicationDataList = new ArrayList<>();
        for (BudgetApplication budgetApplication : budgetApplicationList) {
            budgetApplication = budgetApplicationDao.findById(budgetApplication.getId());
            BudgetApplicationAsset budgetApplicationAsset = budgetApplication.getBudgetAssets().iterator().next();
            Map<String, Object> budgetApplicationMap = new HashMap<>();
            budgetApplicationMap.put("编号",budgetApplication.getApplicationNum());
            budgetApplicationMap.put("院区",budgetApplication.getCourtyardArea());
            budgetApplicationMap.put("病区代码",budgetApplication.getWardCode());
            budgetApplicationMap.put("申请科室",budgetApplication.getApplicationDept()==null?"":budgetApplication.getApplicationDept().getName());
            budgetApplicationMap.put("设备名称",budgetApplicationAsset.getApplicationAssetName());
            budgetApplicationMap.put("设备品类",budgetApplicationAsset.getApplicationAssetCategory());
            budgetApplicationMap.put("推荐品牌",budgetApplicationAsset.getRecommendedBrand());
            budgetApplicationMap.put("规格",budgetApplicationAsset.getStandards());
            budgetApplicationMap.put("型号",budgetApplicationAsset.getModel());
            budgetApplicationMap.put("数量",budgetApplicationAsset.getApplicationQuantity()==null?0:budgetApplicationAsset.getApplicationQuantity());
            budgetApplicationMap.put("单位",budgetApplicationAsset.getUnit());
            budgetApplicationMap.put("预算单价",budgetApplicationAsset.getApplicationPrice()==null?0:budgetApplicationAsset.getApplicationPrice().toString());
            budgetApplicationMap.put("预算总价",budgetApplicationAsset.getApplicationAmount()==null?0:budgetApplicationAsset.getApplicationAmount().toString());
            budgetApplicationMap.put("经费来源",StringUtils.isBlank(budgetApplicationAsset.getSourceFunding())?"":WebUtil.getFieldValueMessage("sourceFunding", budgetApplicationAsset.getSourceFunding()));
            if(BudgetApplication.OrderType.BAY.toString().equals(orderType)){
                budgetApplicationMap.put("申请人",budgetApplication.getApplicant()==null?"":budgetApplication.getApplicant().getName());
                budgetApplicationMap.put("申请理由", budgetApplicationAsset.getApplicationReason());
                budgetApplicationMap.put("拟购设备的用途", budgetApplicationAsset.getEquipmentUsage());
                budgetApplicationMap.put("需求说明", budgetApplicationAsset.getDescription());
            }else {
                budgetApplicationMap.put("科室现有同类设备数量", budgetApplicationAsset.getSimilarDevicesNumber()==null?0:budgetApplicationAsset.getSimilarDevicesNumber());
                budgetApplicationMap.put("科室现有同类设备利用情况", budgetApplicationAsset.getSimilarDevicesUse());
                budgetApplicationMap.put("是否采购进口产品",StringUtils.isBlank(budgetApplicationAsset.getImportedOr())?"": WebUtil.getFieldValueMessage("budgetApplicationT_F", budgetApplicationAsset.getImportedOr()));
                budgetApplicationMap.put("申购仪器设备的主要功能",  budgetApplicationAsset.getMajorFunction());
                budgetApplicationMap.put("申购仪器设备的配置",  budgetApplicationAsset.getDeviceConfiguration());
                budgetApplicationMap.put("申购仪器设备的主要技术参数及要求",  budgetApplicationAsset.getTechnicalParameter());
                budgetApplicationMap.put("申购仪器设备在本单位医疗、教学、科研工作中的利用",  budgetApplicationAsset.getEquipmentUtilization());
                budgetApplicationMap.put("社会、经济效益分析",  budgetApplicationAsset.getBenefitAnalysis());
                budgetApplicationMap.put("同类产品比较",  budgetApplicationAsset.getProductComparison());
                budgetApplicationMap.put("使用年限",  budgetApplicationAsset.getServiceLife());
                budgetApplicationMap.put("收费标准",  budgetApplicationAsset.getChargingStandards()==null?0:budgetApplicationAsset.getChargingStandards().toString());
                budgetApplicationMap.put("年预估收入",  budgetApplicationAsset.getYearIncome()==null?0:budgetApplicationAsset.getYearIncome().toString());
                budgetApplicationMap.put("所需耗材及使用量",  budgetApplicationAsset.getUsage());
                budgetApplicationMap.put("设备是否有放置场地", StringUtils.isBlank(budgetApplicationAsset.getPlaceSite())?"": WebUtil.getFieldValueMessage("budgetApplicationT_F", budgetApplicationAsset.getPlaceSite()));
                budgetApplicationMap.put("是否在手术室使用",StringUtils.isBlank(budgetApplicationAsset.getUsedOperatingRoom())?"":  WebUtil.getFieldValueMessage("budgetApplicationT_F", budgetApplicationAsset.getUsedOperatingRoom()));
                budgetApplicationMap.put("手术室护士长是否已签名",StringUtils.isBlank(budgetApplicationAsset.getSignatureNurse())?"":  WebUtil.getFieldValueMessage("budgetApplicationT_F", budgetApplicationAsset.getSignatureNurse()));
                budgetApplicationMap.put("是否需要同院内信息系统对接",StringUtils.isBlank(budgetApplicationAsset.getSystemDocking())?"":   WebUtil.getFieldValueMessage("budgetApplicationT_F", budgetApplicationAsset.getSystemDocking()));
                budgetApplicationMap.put("是否需要连接外网",StringUtils.isBlank(budgetApplicationAsset.getConnectNetwork())?"":  WebUtil.getFieldValueMessage("budgetApplicationT_F", budgetApplicationAsset.getConnectNetwork()));
                budgetApplicationMap.put("情况说明",  budgetApplicationAsset.getDescription());
            }
            budgetApplicationMap.put("审批时间", budgetApplicationAsset.getOfflineApprovalDate()==null?null:DateFormatUtils.format(budgetApplicationAsset.getOfflineApprovalDate(),"yyyy-MM-dd"));
            budgetApplicationMap.put("审批意见", budgetApplicationAsset.getOfflineApprovalOpinions());
            budgetApplicationMap.put("审批结论", budgetApplicationAsset.getOfflineApprovalConclusion());
            budgetApplicationMap.put("备注", budgetApplicationAsset.getOfflineApprovalRemark());
            budgetApplicationMap.put("记录人", budgetApplicationAsset.getOfflineApprovalRecorder());
            budgeApplicationDataList.add(budgetApplicationMap);
        }
        return budgeApplicationDataList;
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> budgetApplicationData,String orderType) {
        Integer keyRow_budget = 1;
        Integer dataRow_budget = 2;
        if(BudgetApplication.OrderType.BAY.toString().equals(orderType)){
            for (Map<String, Object> item : budgetApplicationData) {
                doc.writeRowData("临时采购申请", keyRow_budget, dataRow_budget++, item);
            }
        }else{
            for (Map<String, Object> item : budgetApplicationData) {
                doc.writeRowData("年度采购申请", keyRow_budget, dataRow_budget++, item);
            }
        }
    }
}