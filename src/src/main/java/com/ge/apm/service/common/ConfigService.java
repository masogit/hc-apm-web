/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.WorkflowConfig;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class ConfigService {

    @Autowired
    private WorkflowConfigRepository configDao;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private TenantInfoRepository tenantDao;

    public WorkflowConfig findConfigBySite(String siteUid) {
        OrgInfo org = orgDao.findByUid(siteUid);
        TenantInfo tenant = tenantDao.findByuid(org.getTenantUID());
        List<WorkflowConfig> configList = configDao.findBySiteUID(siteUid);
        if (tenant.getInsulate()!=null && tenant.getInsulate()) {
            if (null != configList && !configList.isEmpty()) {
                return configList.get(0);
            }else{
                return null;
            }
        }
        
        if (null != configList && !configList.isEmpty()) {
            return configList.get(0);
        }
        configList = configDao.findBySiteUID(org.getHospitalUID());
        if (null != configList && !configList.isEmpty()) {
            return configList.get(0);
        }
        configList = configDao.findBySiteUID(org.getInstitutionUID());
        if (null != configList && !configList.isEmpty()) {
            return configList.get(0);
        }
        configList = configDao.findBySiteUID(org.getTenantUID());
        if (null != configList && !configList.isEmpty()) {
            return configList.get(0);
        }
        return null;
    }

    public boolean isNeedAssetApprover(String siteUid) {
        OrgInfo org = orgDao.findByUid(siteUid);
        TenantInfo tenant = tenantDao.findByuid(org.getTenantUID());
        List<WorkflowConfig> configList = configDao.findBySiteUID(siteUid);
        if (tenant.getInsulate()!=null && tenant.getInsulate()) {
            if (null != configList && !configList.isEmpty() && configList.get(0).getAssetApprover() != null) {
                return true;
            }else{
                return false;
            }
        }

        if (null != configList && !configList.isEmpty() && configList.get(0).getAssetApprover() != null) {
            return true;
        }
        configList = configDao.findBySiteUID(org.getHospitalUID());
        if (null != configList && !configList.isEmpty() && configList.get(0).getAssetApprover() != null) {
            return true;
        }
        configList = configDao.findBySiteUID(org.getInstitutionUID());
        if (null != configList && !configList.isEmpty() && configList.get(0).getAssetApprover() != null) {
            return true;
        }
        configList = configDao.findBySiteUID(org.getTenantUID());
        if (null != configList && !configList.isEmpty() && configList.get(0).getAssetApprover() != null) {
            return true;
        }

        return false;
    }
}
