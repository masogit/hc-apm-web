package com.ge.apm.service.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.BudgetApplicationMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.budget.BudgetConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
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
    //主表字段
    private final String[] BUDGET_HEADER_FIELD = {"applicationYear","applicationDeptName","wardCode","applicantLoginName","applicationDate"};  //主表字段名
    //设备明细字段
    private final String[] ASSET_HEADER_FIELD = {"applicationAssetName","applicationAssetCategory","recommendedBrand","standards","model","applicationQuantity","unit",
            "applicationPrice","applicationAmount","sourceFunding","similarDevicesNumber","similarDevicesUse","importedOr","majorFunction","deviceConfiguration",
            "technicalParameter","equipmentUtilization","benefitAnalysis","productComparison","serviceLife","chargingStandards","yearIncome","usage",
            "placeSite","usedOperatingRoom","signatureNurse","systemDocking","connectNetwork","description","offlineApprovalComplete","offlineApprovalDate","offlineApprovalOpinions","offlineApprovalConclusion",
            "offlineApprovalRecorder","offlineApplicationQuantity","offlineApplicationPrice", "offlineApplicationAmount","offlineApprovalRemark"};  //明细字段名
    private final String[] ASSET_HEADER_FIELD_BIGDECIMAL_TYPE = {"applicationPrice", "applicationAmount","chargingStandards","yearIncome","offlineApplicationPrice","offlineApplicationAmount"}; //bigdecima数字类型字段
    private final String[] ASSET_HEADER_FIELD_INTEGER_TYPE = {"applicationQuantity","similarDevicesNumber","offlineApplicationQuantity"}; //integer类型字段
    //日期类型
    private final String[] HEADER_FIELD_DATE_TYPE = {"applicationDate","offlineApprovalDate"}; //日期类型字段
    //下拉字段名  ，需value  和中文转换
    private final String[] ASSET_HEADER_FIELD_DROP_INDEX = {"sourceFunding","importedOr","placeSite","usedOperatingRoom","signatureNurse","systemDocking","connectNetwork","offlineApprovalComplete"}; //下拉字段
    //主表必填字段
    private final String[][] BUDGET_FIELD_REQUIRE = {{"applicationNum","applicationDeptName","wardCode"},
                                                     {"申请编号"       ,"申请科室"             ,"病区代码"  }}; //主表必填字段名称，和中文名
    //明细必填字段
    private final String[][] ASSET_FIELD_REQUIRE = {{"applicationAssetName","applicationAssetCategory","applicationQuantity","unit","applicationPrice","usedOperatingRoom","offlineApprovalComplete"},
                                                    {"设备名称"              ,"设备分类"                  ,"数量"                ,"单位" ,"预算单价"         ,"是否在手术室使用"    ,"线下审批是否完成"  }}; //明细必填字段

    //过会信息必填字段
    private final String[][] OFFLINE_APPROVAL_REQUIRE = {{"offlineApprovalDate","offlineApprovalOpinions","offlineApprovalConclusion","offlineApprovalRecorder","offlineApplicationQuantity","offlineApplicationPrice","offlineApprovalRemark"},
                                                        {"审批时间(YYYY/MM/DD)" ,"审批意见"                ,"审批结论"                  ,"记录人"                   ,"审批数量"                   ,"审批单价"                 ,"备注"                  }}; //明细必填字段

    private final int ASSET_BEGIN = 5;

    @Autowired
    private UserAccountRepository userAccountRepository;

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
    @Autowired
    private PurchaseBudgetConfigRepository purchaseBudgetConfigRepository;
    @Autowired
    private I18nMessageRepository i18nMessageRepository;

    @Autowired
    private ConfigCacheService configCacheService;


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
            if(budgetAssetInfo != null){
                budgetApplicationAsset.setApplicationAssetName(budgetAssetInfo.getName());
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                List<PurchaseBudgetConfig>  purchaseBudgetConfigList = purchaseBudgetConfigRepository.queryEffiveConfig(account.getTenantUID(),
                        account.getSiteUID(),sdf.parse(sdf.format(budgetApplication.getApplicationDate())));
                if(!purchaseBudgetConfigList.isEmpty()){
                    budgetApplication.setApplicationYear(purchaseBudgetConfigList.get(0).getYear());
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

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
        application.setDataSource("SYSTEM");
        application.setOrderType(orderType);
        OrgInfo orgInfo = orgDao.findByUid(account.getOrgInfoUID());
        if(Objects.equals(orgInfo.getOrgType(),4)){
            application.setApplicationDeptName(orgInfo.getName());
            application.setApplicationDept(orgInfo);
        }
        OrgInfo orgArea = getCourtyardAreaOrg(orgInfo);
        application.setCourtyardArea(orgArea.getName());
        application.setCourtyardAreaId(orgArea.getId());
    }

    public OrgInfo getCourtyardAreaOrg(OrgInfo orgInfo){
        if(orgInfo!=null&& Objects.equals(orgInfo.getOrgLevel(),orgInfo.getOrgType())){
            return orgInfo;
        }
        return getCourtyardAreaOrg(orgInfo.getParentOrg());
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
                sendMsg(application, approvalConfig, approval);
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

        multiLevelApproval.setApplicationDepartment(org==null?null:Objects.equals(org.getOrgType(),4)?org.getName():null);
        multiLevelApproval.setApplicationDepartmentUid(org==null?null:Objects.equals(org.getOrgType(),4)?org.getUid():null);

    }
    private void sendMsg(BudgetApplication application, MultiLevelApprovalConfig config,  MultiLevelApproval approval) {
        Optional.ofNullable(config.getApprovalItem()).flatMap(item ->
                Optional.ofNullable(JSON.parseObject(item, new TypeReference<List<ApprovalNode>>() {
                })).flatMap(nodes -> Optional.ofNullable(nodes.get(0)))).ifPresent(node -> {
//            List<WechatParam> paramList = ListUtil.newArrayList();
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
        BudgetApplication budgetApplicationExist = budgetApplicationDao.findByApplicationNum(applicationNum);
        if(budgetApplicationExist != null){
                applicationNum = applicationNum+"_1";
        }
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
            budgetApplicationMap.put("申请编号",budgetApplication.getApplicationNum());
            budgetApplicationMap.put("年份",budgetApplication.getApplicationYear());
            budgetApplicationMap.put("院区",budgetApplication.getCourtyardArea());
            budgetApplicationMap.put("病区代码",budgetApplication.getWardCode());
            budgetApplicationMap.put("申请科室",budgetApplication.getApplicationDept()==null?"":budgetApplication.getApplicationDept().getName());
            budgetApplicationMap.put("设备名称",budgetApplicationAsset.getApplicationAssetName());
            budgetApplicationMap.put("设备分类",StringUtils.isBlank(budgetApplicationAsset.getApplicationAssetCategory())?"":WebUtil.getFieldValueMessage("assetGroup", budgetApplicationAsset.getApplicationAssetCategory()));
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
            budgetApplicationMap.put("步骤", stepCodeToName(budgetApplication.getApplicationStep()));
            budgeApplicationDataList.add(budgetApplicationMap);
        }
        return budgeApplicationDataList;
    }

    public String stepCodeToName(String v){
        if(StrUtil.isEmpty(v)){
            return "";
        }
        switch (v){
            case "ToSubmitted":
                return "待提交";
            case "PendingApproval":
                return "待审核";
            case "PendingComplete":
                return "待完成";
            case "Complete":
                return "已完成";
            default:
                return "";
        }
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> budgetApplicationData,String orderType) {
        Integer keyRow_budget = 2;
        Integer dataRow_budget = 3;
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

    public List<BudgetApplication> handleExcel(UploadedFile file, UserAccount user,OrgInfo selectOrgInfo,String orderType) throws IOException, NoSuchFieldException, IllegalAccessException {
        List<BudgetApplication> tempBudgetList = new ArrayList<BudgetApplication>();
        Workbook workbook = this.createExcel(file);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        for(int j = 2 ; j < sheet.getLastRowNum(); j ++){
            Row row = sheet.getRow(j+1);
            if(row==null){
                continue;
            }
            String errorInfo = "";
            BudgetApplication budgetApplication = new BudgetApplication();
            Set<BudgetApplicationAsset> budgetApplicationAssetSet = new HashSet<BudgetApplicationAsset>();
            BudgetApplicationAsset budgetApplicationAsset = new BudgetApplicationAsset();
            budgetApplicationAssetSet.add(budgetApplicationAsset);
            budgetApplication.setBudgetAssets(budgetApplicationAssetSet);
            budgetApplication = initCreateBudgetForExcel(user,selectOrgInfo,orderType);
            budgetApplicationAsset = budgetApplication.getBudgetAssets().iterator().next();
            for(int i = 0 ; i< BUDGET_HEADER_FIELD.length ; i++){
                String mtName = "set"+BUDGET_HEADER_FIELD[i].substring(0, 1).toUpperCase() + BUDGET_HEADER_FIELD[i].substring(1);
                if(row.getCell(i) != null && StringUtils.isNotBlank(CellType.NUMERIC.equals(row.getCell(i).getCellTypeEnum()) ?
                        new DecimalFormat("##############.#####").format(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue())){
                    String opt = CellType.NUMERIC.equals(row.getCell(i).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue();
                    try {
                        if(Arrays.asList(HEADER_FIELD_DATE_TYPE).contains(BUDGET_HEADER_FIELD[i])){
                            Method method = BudgetApplication.class.getMethod(mtName,Date.class);
                            if(HSSFDateUtil.isCellDateFormatted(row.getCell(i))){
                                Date optDate = row.getCell(i).getDateCellValue();
                                method.invoke(budgetApplication, optDate);
                            }
                        }else{
                            Method method = BudgetApplication.class.getMethod(mtName,String.class);
                            method.invoke(budgetApplication, opt);
                        }
                    } catch (Exception e) {
                        logger.debug("handleExcel error"+e.getMessage());
                    }
                }
            }

            for(int i = 0 ; i< ASSET_HEADER_FIELD.length ; i++){
                String mtName = "set"+ASSET_HEADER_FIELD[i].substring(0, 1).toUpperCase() + ASSET_HEADER_FIELD[i].substring(1);
                if(row.getCell(i+ASSET_BEGIN) != null && StringUtils.isNotBlank(CellType.NUMERIC.equals(row.getCell(i+ASSET_BEGIN).getCellTypeEnum()) ?
                        new DecimalFormat("##############.#####").format(row.getCell(i+ASSET_BEGIN).getNumericCellValue()) : row.getCell(i+ASSET_BEGIN).getStringCellValue())){
                    String opt = CellType.NUMERIC.equals(row.getCell(i+ASSET_BEGIN).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(i+ASSET_BEGIN).getNumericCellValue()) : row.getCell(i+ASSET_BEGIN).getStringCellValue();
                    opt = handIndexBox(ASSET_HEADER_FIELD[i],opt);
                    try {
                        if(Arrays.asList(ASSET_HEADER_FIELD_BIGDECIMAL_TYPE).contains(ASSET_HEADER_FIELD[i])){
                            Method method = BudgetApplicationAsset.class.getMethod(mtName, BigDecimal.class);
                            method.invoke(budgetApplicationAsset, new BigDecimal(opt));
                        }else if(Arrays.asList(ASSET_HEADER_FIELD_INTEGER_TYPE).contains(ASSET_HEADER_FIELD[i])){
                            Method method = BudgetApplicationAsset.class.getMethod(mtName,Integer.class);
                            method.invoke(budgetApplicationAsset,  Integer.parseInt(opt));
                        }else if(Arrays.asList(HEADER_FIELD_DATE_TYPE).contains(ASSET_HEADER_FIELD[i])){
                            Method method = BudgetApplicationAsset.class.getMethod(mtName,Date.class);
                            if(HSSFDateUtil.isCellDateFormatted(row.getCell(i+ASSET_BEGIN))){
                                Date optDate = row.getCell(i+ASSET_BEGIN).getDateCellValue();
                                method.invoke(budgetApplicationAsset,  optDate);
                            }
                        } else{
                            Method method = BudgetApplicationAsset.class.getMethod(mtName,String.class);
                            method.invoke(budgetApplicationAsset, opt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.debug("handleExcel error"+e.getMessage());
                    }
                }
            }
            errorInfo = checkRequiredAndDataVerification(budgetApplication,selectOrgInfo,errorInfo);
            budgetApplication.setExcelIdex(j + 2);
            if (StringUtils.isNotBlank(errorInfo)) {
                budgetApplication.setErrorInfo(errorInfo);
                tempBudgetList.add(budgetApplication);
                continue;
            }
            budgetApplicationAsset.setApplicationNum(budgetApplication.getApplicationNum());
            BudgetApplicationAsset budgetApplicationAssetCur = budgetApplication.getBudgetAssets().iterator().next();
            if(budgetApplicationAssetCur.getApplicationAmount() == null || budgetApplicationAssetCur.getApplicationAmount().compareTo(BigDecimal.ZERO) == 0){
                if(budgetApplicationAssetCur.getApplicationPrice()!= null && budgetApplicationAssetCur.getApplicationQuantity()!= null){
                    budgetApplicationAssetCur.setApplicationAmount(budgetApplicationAssetCur.getApplicationPrice()
                            .multiply(new BigDecimal(budgetApplicationAssetCur.getApplicationQuantity()))
                            .setScale(2, RoundingMode.HALF_UP));
                }
            }
            if(budgetApplicationAssetCur.getOfflineApplicationAmount() == null || budgetApplicationAssetCur.getOfflineApplicationAmount().compareTo(BigDecimal.ZERO) == 0){
                if(budgetApplicationAssetCur.getOfflineApplicationPrice()!= null && budgetApplicationAssetCur.getOfflineApplicationQuantity()!= null){
                    budgetApplicationAssetCur.setOfflineApplicationAmount(budgetApplicationAssetCur.getOfflineApplicationPrice()
                            .multiply(new BigDecimal(budgetApplicationAssetCur.getOfflineApplicationQuantity()))
                            .setScale(2, RoundingMode.HALF_UP));
                    //budgetApplicationAssetCur.setConfirmPrice(budgetApplicationAssetCur.getOfflineApplicationPrice());
                    //budgetApplicationAssetCur.setConfirmQuantity(budgetApplicationAssetCur.getOfflineApplicationQuantity());
                }
            }

            budgetApplicationDao.save(budgetApplication);
        }
        return tempBudgetList;
    }

    public BudgetApplication initCreateBudgetForExcel(UserAccount user,OrgInfo selectOrgInfo,String orderType){
        BudgetApplication budgetApplication = new BudgetApplication();
        Set<BudgetApplicationAsset> budgetApplicationAssetSet = new HashSet<BudgetApplicationAsset>();
        BudgetApplicationAsset budgetApplicationAsset = new BudgetApplicationAsset();
        budgetApplicationAssetSet.add(budgetApplicationAsset);
        budgetApplication.setBudgetAssets(budgetApplicationAssetSet);
        budgetApplication.setApplicationStep(BudgetApplication.APPLICATIONSTEP.Complete.toString());
        //budgetApplication.setApplicationDate(new Date());
        budgetApplication.setApplicationStatus(6);
        budgetApplication.setBudgetApprovals(new HashSet<>());
        budgetApplication.setCreatedBy(user.getLoginName());
        budgetApplication.setCreatedDate(new Date());
        budgetApplication.setLastModifiedBy(user.getLoginName());
        budgetApplication.setLastModifiedDate(new Date());
        budgetApplication.setOrderType(orderType);
        budgetApplication.setCourtyardArea(selectOrgInfo.getName());
        budgetApplication.setCourtyardAreaId(selectOrgInfo.getId());
        budgetApplication.setTenantUID(selectOrgInfo.getTenantUID());
        budgetApplication.setInstitutionUID(selectOrgInfo.getInstitutionUID());
        budgetApplication.setHospitalUID(selectOrgInfo.getHospitalUID());
        budgetApplication.setSiteUID(selectOrgInfo.getSiteUID());
        budgetApplication.setDataSource("EXCEL_IMPORT");
        budgetApplication.setApprover(user);
        budgetApplication.setApproverId(user.getId());
        budgetApplication.setApprovalDate(new Date());
        budgetApplicationAsset.setCreatedDate(new Date());
        budgetApplicationAsset.setCreatedBy(user.getLoginName());
        budgetApplicationAsset.setLastModifiedBy(user.getLoginName());
        budgetApplicationAsset.setLastModifiedDate(new Date());
        budgetApplicationAsset.setTenantUID(selectOrgInfo.getTenantUID());
        budgetApplicationAsset.setInstitutionUID(selectOrgInfo.getInstitutionUID());
        budgetApplicationAsset.setHospitalUID(selectOrgInfo.getHospitalUID());
        budgetApplicationAsset.setSiteUID(selectOrgInfo.getSiteUID());
        this.genApplicationNum(budgetApplication,budgetApplication.getOrderType());
        return budgetApplication;
    }



    public String checkRequiredAndDataVerification(BudgetApplication budgetApplication,OrgInfo selectOrgInfo,String errorInfo) throws NoSuchFieldException, IllegalAccessException {
        //BudgetApplication budgetApplicationExist = budgetApplicationDao.findByApplicationNum(budgetApplication.getApplicationNum());
        //if(budgetApplicationExist != null ){
            //为空  只有这个是等于
        //    errorInfo = "[数据已存在，请检查后重试]";
        //}else{
            for (int i = 0; i < BUDGET_FIELD_REQUIRE[0].length; i++) {
                String fieldName = BUDGET_FIELD_REQUIRE[0][i];
                Field field = budgetApplication.getClass().getDeclaredField(fieldName);
                if (isFieldEmpty(field, budgetApplication)) {
                    System.out.println("字段 " + fieldName + " 为空");
                    errorInfo += "[请输入"+BUDGET_FIELD_REQUIRE[1][i]+"]";
                }
            }
            BudgetApplicationAsset budgetApplicationAsset = budgetApplication.getBudgetAssets().iterator().next();
            for (int i = 0; i < ASSET_FIELD_REQUIRE[0].length; i++) {
                String fieldName = ASSET_FIELD_REQUIRE[0][i];
                Field field = budgetApplicationAsset.getClass().getDeclaredField(fieldName);
                if (isFieldEmpty(field, budgetApplicationAsset)) {
                    errorInfo += "[请输入"+ASSET_FIELD_REQUIRE[1][i]+"]";
                }
            }
            if("T".equals(budgetApplicationAsset.getOfflineApprovalComplete())){
                for (int i = 0; i < OFFLINE_APPROVAL_REQUIRE[0].length; i++) {
                    String fieldName = OFFLINE_APPROVAL_REQUIRE[0][i];
                    Field field = budgetApplicationAsset.getClass().getDeclaredField(fieldName);
                    if (isFieldEmpty(field, budgetApplicationAsset)) {
                        errorInfo += "[请输入"+OFFLINE_APPROVAL_REQUIRE[1][i]+"]";
                    }
                }
            }else{
                budgetApplication.setApplicationStep(BudgetApplication.APPLICATIONSTEP.PendingComplete.toString());
                budgetApplication.setApplicationStatus(3);
            }
            //手术室设备必填项校验
            if(StringUtils.isNotBlank(budgetApplicationAsset.getUsedOperatingRoom()) && "T".equals(budgetApplicationAsset.getUsedOperatingRoom())) {
                if (StringUtils.isBlank(budgetApplicationAsset.getSignatureNurse()) || "F".equals(budgetApplicationAsset.getSignatureNurse())) {
                    errorInfo += "[手术室使用的设备，护士长是否已签名需为是！]";
                }
            }
            //申请人
            if(StringUtils.isNotBlank(budgetApplication.getApplicantLoginName())){
                UserAccount userAccount = userAccountRepository.getByLoginName(budgetApplication.getApplicantLoginName());
                if(userAccount==null){
                    errorInfo += "[申请人登录账号有误，请检查后重试！]";
                }else{
                    budgetApplication.setApplicant(userAccount);
                    budgetApplication.setApplicantId(userAccount.getId());
                }
            }
            OrgInfo orgInfoDept = orgDao.getDepartmentByName(budgetApplication.getApplicationDeptName(),selectOrgInfo.getUid());
            if(orgInfoDept==null){
                errorInfo += "[申请科室有误，请检查后重试！]";
            }else{
                budgetApplication.setApplicationDeptName(orgInfoDept.getName());
                budgetApplication.setApplicationDeptId(orgInfoDept.getId());
                budgetApplication.setApplicationDept(orgInfoDept);
                budgetApplicationAsset.setApplicationDeptId(orgInfoDept.getId());
            }
           /* OrgInfo orgInfoSite = orgDao.getSiteByName(budgetApplication.getCourtyardArea(),selectOrgInfo.getHospitalUID());
            if(orgInfoSite==null){
                errorInfo += "[院区有误，请检查后重试！]";
            }else{
                budgetApplication.setCourtyardArea(orgInfoSite.getName());
                budgetApplication.setCourtyardAreaId(orgInfoSite.getId());
            }*/

            if(StrUtil.isNotEmpty(budgetApplicationAsset.getApplicationAssetCategory())){
                I18nMessage i = i18nMessageRepository.findFirstByMsgTypeAndValueZh("assetGroup", budgetApplicationAsset.getApplicationAssetCategory());
                if(i == null){
                    errorInfo += "[设备分类不存在，请检查后重试！]";
                }else{
                    budgetApplicationAsset.setApplicationAssetCategory(i.getMsgKey());
                }
            }
        //}
        return errorInfo;
    }

    private static boolean isFieldEmpty(Field field, Object obj) throws IllegalAccessException {
        field.setAccessible(true); // 允许访问私有字段
        Object value = field.get(obj);
        return value == null; // 如果值为null，则字段为空
    }

    public String handIndexBox(String strFiled,String value){
        if(Arrays.asList(ASSET_HEADER_FIELD_DROP_INDEX).contains(strFiled)){
            if(StringUtils.isNotBlank(value)){
                if("是".equals(value)){
                    value = "T";
                }else if("否".equals(value)){
                    value = "F";
                }else if("横向专项经费(科研)".equals(value)){
                    value = "HorizontalSpecial";
                }else if("拨款资金".equals(value)){
                    value = "Appropriation";
                }else if("自筹资金".equals(value)){
                    value = "SelfRaised";
                }
            }
        }
        return value;
    }


    public Workbook createExcel(UploadedFile file) throws IOException {
        Workbook workbook = null;
        if (ExcelDocument.ExcelType.XLS.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (ExcelDocument.ExcelType.XLSX.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        return workbook;
    }

   /* public void exportAssetExcelTemplate() throws IOException {

        CustomConfigObject customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        //field name using english
        List<String> exportFields = ccService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        //field name using chinese
//        List<String> exportColumnFields = Arrays.asList(AssetFileDynamicTemplate.columnTitles).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) ||
//                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());

        List<OrgInfo> orgList = orgDao.findBySiteUIDAndOrgType(UserContextService.getCurrentUserAccount().getSiteUID(), 4);
        List<I18nMessage> tcm = FieldValueMessageController.getFieldValueList("spare01");

        AssetFileDynamicTemplate template = new AssetFileDynamicTemplate(false);
        template.exportSampleAsset(orgList, exportFields);
        template.exportSampleAsset(orgList, exportFields,tcm);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if(userContextService.isLocale()){
            assetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "APM_Asset_Template.xlsx");
        }else{
            assetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "APM_Asset_Template_en.xlsx");
        }
        logger.info(" export asset excel template successful. ");
    }*/
}