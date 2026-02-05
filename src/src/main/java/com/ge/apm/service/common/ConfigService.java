/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;

import java.util.*;
import java.util.stream.Collectors;

import com.ge.apm.service.pm.PmOrderAggregateService;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author 212579464
 */
@Component
public class ConfigService {
    Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private WorkflowConfigRepository configDao;

    @Autowired
    private ConfigCacheService cacheService;

    public WorkflowConfig findConfigBySite(String siteUid) {
        OrgInfo org = cacheService.findOrgByUid(siteUid);
        return cacheService.findConfigBySite(siteUid, isInsulateTenant(org.getTenantUID()), org);
    }

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategories(WorkflowConfig config) {
        List<WorkflowConfig.AttachmentCategory> result = new ArrayList<>();
        if(config!=null) {
            String categories = config.getCustomizedAssetAttachmentCategory();
            if(null !=categories){
                result = JSON.parseObject(categories, new TypeReference<List<WorkflowConfig.AttachmentCategory>>(){
                });
            }
        }
        return result;
    }

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategoriesByTenantUid(String tenantUid) {
        List<WorkflowConfig.AttachmentCategory> result = new ArrayList<>();
        List<WorkflowConfig> configList = configDao.findByTenantUID(tenantUid);
        for(WorkflowConfig config : configList) {
            result.addAll(getAttachmentCategories(config));
        }
        result = result.stream().distinct().collect(Collectors.toList());
        return result;
    }

    public boolean isNeedAssetApprover(String siteUid) {
        WorkflowConfig config = findConfigBySite(siteUid);
        return Optional.ofNullable(config).map(i->null!=i.getAssetApprover()).orElse(false);
    }

    public boolean isAssetNeedAudit(String siteUid) {
        WorkflowConfig config = findConfigBySite(siteUid);
        return isAssetNeedAudit(config);
    }

    private boolean isAssetNeedAudit(WorkflowConfig workflowConfig) {
        if (null == workflowConfig) {
            return false;
        }

        Boolean needInstorageCheck = workflowConfig.getNeedInstorageCheck();
        if (needInstorageCheck != null && needInstorageCheck.booleanValue()) {
            return false;
        }

        String approver = workflowConfig.getAssetApprover();
        if (!StringUtils.isEmpty(approver)) {
            return true;
        }

        return false;
    }

    public boolean isInsulateTenant(String tenantUid){
        TenantInfo tenant = cacheService.getTenantByUid(tenantUid);
        return Optional.ofNullable(tenant).map(TenantInfo::getInsulate).orElse(false);
    }

    public Optional<WorkflowConfig> findInheritableWorkflowConfigByCurrentUser() {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        return Optional.ofNullable(findConfigBySite(currentUserAccount.getSiteUID()));
    }

    //是否待报废权限控制
    public boolean needScrappedControl(){

        Optional<WorkflowConfig> optWorkflowConfig = findInheritableWorkflowConfigByCurrentUser();
        if(optWorkflowConfig.isPresent()){
            WorkflowConfig workflowConfig = optWorkflowConfig.get();
            return workflowConfig.getEnableScrappedControl();
        }
        return false;
    }

    //风险等级是否沿用高中低
    public boolean useNewRiskGrade(){
        Optional<WorkflowConfig> optWorkflowConfig = findInheritableWorkflowConfigByCurrentUser();
        if(optWorkflowConfig.isPresent()){
            WorkflowConfig workflowConfig = optWorkflowConfig.get();
            return workflowConfig.getUseNewRiskGrade()==null ? false : workflowConfig.getUseNewRiskGrade();
        }
        return false;
    }
}
