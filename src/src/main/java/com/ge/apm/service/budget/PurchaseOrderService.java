package com.ge.apm.service.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * LYJ
 * 2023.10.18
 */
@Service
public class PurchaseOrderService {
    private static Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    private BudgetApplicationAssetRepository budgetApplicationAssetRepository;
    @Autowired
    private PurchaseOrderInfoRepository purchaseOrderInfoRepository;
    @Autowired
    private BudgetApplicationRepository budgetApplicationDao;
    @Autowired
    private PurchaseTemplateRepository purchaseTemplateRepository;
    @Autowired
    private BudgetApplicationExportService budgetApplicationExportService;
    @Value("#{systemEnvironment['DOMAIN_URL'] ?: urlProperties.domainUrl}")
    private String baseURL;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private MultiLevelApprovalRepository approvalDao;
    @Autowired
    private PurchaseVenderRepository purchaseVenderRepository;
    @Autowired
    private MultiLevelApprovalService approvalService;

    @Autowired
    private ConfigCacheService configCacheService;

    public String getBaseURL(){
        return baseURL;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean savePurchase(PurchaseOrderInfo purchaseOrderInfo,List<BudgetApplicationAsset> budgetApplicationAssetList,
                                List<String> templateIds,UserAccount userAccount) {
        this.handleCommonField(purchaseOrderInfo,userAccount);
        //先去掉之前的关联数据
        if (!Optional.ofNullable(purchaseOrderInfo.getId()).isPresent()) { // 新增  数据状态和步骤为空
            this.generatePurchaseNum(purchaseOrderInfo);
        }else{
            //编辑才需要处理之前数据
            List<BudgetApplication> lastBudgetApplications = budgetApplicationDao.findByPurchaseIdAndIsDeletedAndOrderTypeIsNotNull(purchaseOrderInfo.getId(),0);
            if(CollectionUtils.isNotEmpty(lastBudgetApplications)){
                lastBudgetApplications.stream().forEach(budget -> {
                    setPurchaseInfoNull(budget);
                });
            }
            budgetApplicationDao.save(lastBudgetApplications);
        }

        //保存采购单数据
        purchaseOrderInfo.setApplicationAssetName(null);
        if(CollectionUtils.isNotEmpty(budgetApplicationAssetList)){
            purchaseOrderInfo.setApplicationAssetName(budgetApplicationAssetList.get(0).getApplicationAssetName());
        }
        PurchaseOrderInfo purchaseOrderInfoPersistent = purchaseOrderInfoRepository.save(purchaseOrderInfo);

       //处理申购单数据
        List<String> budgetApplicationIds = budgetApplicationAssetList.stream().
                map(BudgetApplicationAsset::getApplicationId).collect(Collectors.toList());
        List<BudgetApplication> curBudgetApplications =  budgetApplicationDao.findByIdIn(budgetApplicationIds);
        if(CollectionUtils.isNotEmpty(curBudgetApplications)){
            curBudgetApplications.stream().forEach(budget -> {
                budget.setPurchaseId(purchaseOrderInfoPersistent.getId());
                budget.setProcureStatus(BudgetApplication.PROCURESTATUS.Purchased.toString());
            });
        }
        budgetApplicationDao.save(curBudgetApplications);

        //处理关联模板信息附件信息保存
        List<PurchaseTemplate> purchaseTemplateList = purchaseTemplateRepository.findByPurchaseId(purchaseOrderInfoPersistent.getId());
        purchaseTemplateRepository.delete(purchaseTemplateList);
        if(CollectionUtils.isNotEmpty(templateIds)){
            templateIds.stream().forEach(tempId -> {
                PurchaseTemplate purchaseTemplate = new PurchaseTemplate();
                purchaseTemplate.setTemplateId(tempId);
                purchaseTemplate.setPurchaseId(purchaseOrderInfoPersistent.getId());
                purchaseTemplateRepository.save(purchaseTemplate);
            });
        }
        return true;
    }

    public List<SearchFilter> initSearchFilter(List<SearchFilter> searchFilters) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }
        return searchFilters;
    }

    public void handleCommonField(PurchaseOrderInfo purchaseOrderInfo, UserAccount account){
        purchaseOrderInfo.setLastModifiedBy(account.getLoginName());
        purchaseOrderInfo.setLastModifiedDate(new Date());

        purchaseOrderInfo.setTenantUID(account.getTenantUID());
        purchaseOrderInfo.setInstitutionUID(account.getInstitutionUID());
        purchaseOrderInfo.setHospitalUID(account.getHospitalUID());
        purchaseOrderInfo.setSiteUID(account.getSiteUID());

    }

    public void initCreateObject(PurchaseOrderInfo purchaseOrderInfo,UserAccount account){
        purchaseOrderInfo.setOperator(account);
        purchaseOrderInfo.setOperatorId(account.getId());
        purchaseOrderInfo.setPurchaseQuantity(0);
        purchaseOrderInfo.setTotalPrice(new BigDecimal(0));
        purchaseOrderInfo.setCreatedBy(account.getLoginName());
        purchaseOrderInfo.setCreatedDate(new Date());
        purchaseOrderInfo.setLastModifiedBy(account.getLoginName());
        purchaseOrderInfo.setLastModifiedDate(new Date());
        purchaseOrderInfo.setStatus("NotEnabled");
    }

    /**
     * 给申请单添加单号和索引
     *
     * @param
     * @return
     */
    private PurchaseOrderInfo generatePurchaseNum(PurchaseOrderInfo purchaseOrderInfo) {
        Object o = purchaseOrderInfoRepository.getMaxIndex();
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String purchaseNum = "PUR" + simpleDateFormat.format(new Date()) + String.format("%04d", index);
        purchaseOrderInfo.setPurchaseNum(purchaseNum);
        purchaseOrderInfo.setPurchaseNumIndex(index);
        return purchaseOrderInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApplication(PurchaseOrderInfo purchaseOrderInfo) {
        List<PurchaseVender> purchaseVenderList = purchaseVenderRepository.findByPurchaseId(purchaseOrderInfo.getId());
        if(CollectionUtils.isNotEmpty(purchaseVenderList)){
            purchaseVenderRepository.delete(purchaseVenderList);
        }
        List<BudgetApplication> budgetApplicationList =  budgetApplicationDao.findByPurchaseIdAndIsDeletedAndOrderTypeIsNotNull(purchaseOrderInfo.getId(),0);
        budgetApplicationList.stream().forEach(budget -> {
            setPurchaseInfoNull(budget);
        });
        budgetApplicationDao.save(budgetApplicationList);
        purchaseOrderInfoRepository.delete(purchaseOrderInfo);
        return true;
    }

    public void setPurchaseInfoNull(BudgetApplication budgetApplication){
        budgetApplication.setPurchaseId(null);
        budgetApplication.setProcureStatus(null);
    }

    /**
     * 回退列表
     *
     * @param
     * @return
     */
    public String backList() {
        return "List.xhtml?faces-redirect=true";
    }

    public boolean checkDistinct(List<BudgetApplicationAsset> curApplicationBudgetAssets,List<BudgetApplicationAsset> selectApplicationBudgetAssets){
        boolean arg = false;
        List<String> assetName = new ArrayList<String>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(curApplicationBudgetAssets)){
            List<String>  assetNameFirest = curApplicationBudgetAssets.stream().map(BudgetApplicationAsset::getApplicationAssetName).collect(Collectors.toList());
            assetName.addAll(assetNameFirest);
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(selectApplicationBudgetAssets)){
            List<String>  assetNameSec = selectApplicationBudgetAssets.stream().map(BudgetApplicationAsset::getApplicationAssetName).collect(Collectors.toList());
            assetName.addAll(assetNameSec);
        }
        long num = assetName.stream().distinct().count();
        if (num != 1) {
            arg = true;
        }
        return arg;
    }

    public StreamedContent exportPurchaseZip(PurchaseOrderInfo purchaseOrderInfo) throws IOException, ParseException {
        String base = "采购单" + purchaseOrderInfo.getPurchaseNum();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);

        //budgetApplicationExportService.generatePurchaseFile(zout, purchaseOrderInfo);
        budgetApplicationExportService.generateBudgetFiles(zout, purchaseOrderInfo);
        budgetApplicationExportService.generateSupplierFiles(zout, purchaseOrderInfo);

        zout.close();
        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(bout.toByteArray())), "application/zip", base+".zip");
        return streamedContent;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean submitPurchaseApplication(PurchaseOrderInfo purchaseOrderInfo) {
        boolean isEditAction = Optional.ofNullable(purchaseOrderInfo.getId()).isPresent();
        /* if (true) {*/
        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(purchaseOrderInfo, false);
        MultiLevelApproval approval = generateApproval(purchaseOrderInfo, approvalConfig);
        if (approval == null) {
            // 无可用的审批流
            logger.info("budget message 001: new budget application, but no approval configuration is available");
        } else {
            // 正常
            logger.info("budget message 002: new budget application");
            purchaseOrderInfo.setApprovalConfigId(approval.getWorkflowConfigApproveUid());
            approvalDao.save(approval);
            purchaseOrderInfoRepository.save(purchaseOrderInfo);
            sendMsg(purchaseOrderInfo, approvalConfig,approval);
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


    public MultiLevelApproval generateApproval(PurchaseOrderInfo application, MultiLevelApprovalConfig approvalConfig) {
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
            });
        });
        return approval.get();
    }


    private void setApplicationDepartment( UserAccount userAccount, MultiLevelApproval multiLevelApproval) {
        OrgInfo  org= configCacheService.findOrgByUid(userAccount.getOrgInfoUID());

        multiLevelApproval.setApplicationDepartment(org==null?null:org.getOrgType()==4?org.getName():null);
        multiLevelApproval.setApplicationDepartmentUid(org==null?null:org.getOrgType()==4?org.getUid():null);

    }
    private void sendMsg(PurchaseOrderInfo purchaseOrderInfo, MultiLevelApprovalConfig config,  MultiLevelApproval approval) {
        Optional.ofNullable(config.getApprovalItem()).flatMap(item ->
                Optional.ofNullable(JSON.parseObject(item, new TypeReference<List<ApprovalNode>>() {
                })).flatMap(nodes -> Optional.ofNullable(nodes.get(0)))).ifPresent(node -> {
//            List<WechatParam> paramList = ListUtil.newArrayList();
            List<String> sendUserUids = ListUtil.newArrayList();
            if (node.getUsingRole()) {
                // 角色审批, 目前ACCT没有角色审批
                Optional.ofNullable(node.getRoleUids()).flatMap(list -> Optional.ofNullable(userDao.getApprovalPersonByRoleId(list)))
                        .ifPresent(userList -> userList.stream().filter(user -> ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getWeChatId(), user.getAppAlias()))
                                .forEach(user -> sendUserUids.add(user.getUid())));
            } else {
                // 用户审批
                Optional.ofNullable(node.getUserUids()).flatMap(list -> Optional.ofNullable(userDao.findByUidIn(list)))
                        .ifPresent(userList -> userList.stream().filter(user -> ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getWeChatId(), user.getAppAlias()))
                                .forEach(user -> sendUserUids.add(user.getUid())));
            }
            // 发送消息
            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2
                    + purchaseOrderInfo.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
            approvalService.sendWxMsg(sendUserUids, message, "待审批","", MSGEvent.MULTI_LEVEL_APPROVAL_TO_BE_APPROVED,approval);
        });
    }
    public boolean checkMultiLevelApproval (PurchaseOrderInfo purchaseOrderInfo){
        boolean result = true;
        MultiLevelApprovalConfig approvalConfig = approvalService.getFirstApprovalList(purchaseOrderInfo, false);
        MultiLevelApproval approval = generateApproval(purchaseOrderInfo, approvalConfig);
        if (approval == null) {
            result = false;
            logger.info("purchase message 001: confirm purchase application, but no approval configuration is available");
        }
        return result;
    }
    @Transactional(rollbackFor = Exception.class)
    public void delBudgetApplication(List<BudgetApplicationAsset> curApplicationBudgetAssets,BudgetApplicationAsset selectApplicationAssetBudget,
                                     PurchaseOrderInfo purchaseOrderInfoSelect,UserAccount account,List<String> templateIds) {

        if(selectApplicationAssetBudget.getBudgetApplication() !=null &&
                StringUtils.isNotBlank(selectApplicationAssetBudget.getBudgetApplication().getProcureStatus()) &&
                StringUtils.isNotBlank(selectApplicationAssetBudget.getBudgetApplication().getPurchaseId())){
            this.setPurchaseInfoNull(selectApplicationAssetBudget.getBudgetApplication());
            budgetApplicationDao.save(selectApplicationAssetBudget.getBudgetApplication());
        }
        this.handleCommonField(purchaseOrderInfoSelect,account);
        this.savePurchase(purchaseOrderInfoSelect,curApplicationBudgetAssets,templateIds,account);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveConfirmInfoBudgetApp(String budgetAssetId,BudgetApplicationAsset budgetApplicationAssetConfirm,PurchaseOrderInfo purchaseOrderInfoCur) {
        BudgetApplicationAsset budgetApplicationAsset = budgetApplicationAssetRepository.findById(budgetAssetId);
        budgetApplicationAsset.setConfirmQuantity(budgetApplicationAssetConfirm.getConfirmQuantity());
        budgetApplicationAsset.setConfirmPrice(budgetApplicationAssetConfirm.getConfirmPrice());
        budgetApplicationAssetRepository.save(budgetApplicationAsset);
        //UPDATE 采购单数量
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ, purchaseOrderInfoCur.getId()));
        List<BudgetApplicationAsset> budgetApplicationAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets,new Sort(Sort.Direction.DESC, "createdDate"));
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        int applicationQuantitySumResult = 0;
        for(BudgetApplicationAsset curApplicationBudget:budgetApplicationAssets){
            totalAmount.set(totalAmount.get().add(curApplicationBudget.getConfirmAmount()==null?
                    new BigDecimal(0):curApplicationBudget.getConfirmAmount()));
            applicationQuantitySumResult += curApplicationBudget.getConfirmQuantity()==null?
                    0:curApplicationBudget.getConfirmQuantity();
        }
        purchaseOrderInfoCur.setTotalPrice(totalAmount.get());
        purchaseOrderInfoCur.setPurchaseQuantity(applicationQuantitySumResult);
        purchaseOrderInfoRepository.save(purchaseOrderInfoCur);
    }

}