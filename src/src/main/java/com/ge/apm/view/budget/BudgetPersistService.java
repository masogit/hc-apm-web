package com.ge.apm.view.budget;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.ApprovalNode;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.domain.MultiLevelApprovalConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Yangfan, Liu
 * @date 4/9/2022 16:51
 */
@Service
public class BudgetPersistService {
    private static Logger logger = LoggerFactory.getLogger(BudgetPersistService.class);

    @Autowired
    private BudgetApplicationRepository budgetApplicationDao;
    @Autowired
    private BudgetApplicationAssetRepository budgetApplicationAssetDao;
    @Autowired
    private BudgetAssetInfoRepository budgetAssetInfoDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private MultiLevelApprovalRepository approvalDao;
    @Autowired
    private MultiLevelApprovalService approvalService;

    @Transactional(rollbackFor = Exception.class)
    public boolean saveApplication(BudgetApplication application, List<BudgetApplicationAsset> deletedBudgetAssets) {
        boolean isEditAction = Optional.ofNullable(application.getId()).isPresent();
        application.getBudgetAssets().forEach(a -> Optional.of(a.isNew()).filter(b -> b).ifPresent(b -> a.setId(null)));

        // 新增
        if (!isEditAction) {
            // 保存以获得ID
            application = budgetApplicationDao.save(application);
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
        } else {
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
            // 删除逻辑
            List<BudgetApplicationAsset> deletedAssets = deletedBudgetAssets.stream().filter(a -> StringUtils.isNotEmpty(a.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deletedAssets)) {
                budgetApplicationAssetDao.delete(deletedAssets);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApplication(BudgetApplication application) {
        approvalDao.delete(application.getBudgetApprovals());
        application.setIsDeleted(1);
        budgetApplicationDao.save(application);
        return true;
    }

    private MultiLevelApproval generateApproval(BudgetApplication application, MultiLevelApprovalConfig approvalConfig) {
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
            approvalService.sendWxMsg(paramList, message, "待审批");
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveAsset(BudgetAssetInfo assetInfo) {
        budgetAssetInfoDao.save(assetInfo);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAsset(BudgetAssetInfo assetInfo) {
        assetInfo.setIsDeleted(1);
        budgetAssetInfoDao.save(assetInfo);
        return true;
    }
}