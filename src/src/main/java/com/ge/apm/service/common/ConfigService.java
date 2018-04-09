/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
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


    public WorkflowConfig findConfigBySite(String siteUid) {
        OrgInfo org = orgDao.findByUid(siteUid);
        List<WorkflowConfig> configList = configDao.findBySiteUID(siteUid);
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
}
