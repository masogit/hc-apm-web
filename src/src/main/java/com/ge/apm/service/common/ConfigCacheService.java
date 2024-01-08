package com.ge.apm.service.common;


import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.TenantOptionConfigRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.TenantOptionConfig;
import com.ge.apm.domain.WorkflowConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author 212579464
 */
@Component
public class ConfigCacheService {

    @Autowired
    private WorkflowConfigRepository configDao;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private TenantInfoRepository tenantDao;

    @Autowired
    private TenantOptionConfigRepository tenantOptionConfigRepository;


    @Cacheable(cacheNames = "springCache", key = "'OrgInfo_Uid.'+#uid")
    public OrgInfo findOrgByUid(String uid){
        return orgDao.findByUid(uid);
    }

    @Cacheable(cacheNames = "springCache", key = "'WorkflowConfig_siteUid.'+#siteUid")
    public WorkflowConfig findConfigBySite(String siteUid, boolean isInsulateTenant, OrgInfo org) {

        List<WorkflowConfig> configList = configDao.findByTenantUID(org.getTenantUID());
        if (isInsulateTenant) {
            return configList.stream().filter(i-> Objects.equals(siteUid,i.getSiteUID())).findAny().orElse(null);
        }

        return configList.stream().filter(i-> Objects.equals(siteUid,i.getSiteUID())).findAny()
                .orElse(configList.stream().filter(i-> Objects.equals(org.getHospitalUID(),i.getSiteUID())).findAny()
                        .orElse(configList.stream().filter(i-> Objects.equals(org.getInstitutionUID(),i.getSiteUID())).findAny()
                                .orElse(configList.stream().filter(i-> Objects.equals(org.getTenantUID(),i.getSiteUID())).findAny()
                                        .orElse(null))));
    }


    @Cacheable(cacheNames = "springCache", key = "'TenantOptionConfig_Uid.'+#uid")
    public TenantOptionConfig getTenantOptionConfigByUid(String uid){

        TenantOptionConfig tenantOptionConfig = new TenantOptionConfig();

        Optional<TenantOptionConfig> res = tenantOptionConfigRepository.findByTenantUid(uid);
        tenantOptionConfig.setTenantUid(uid);
        tenantOptionConfig.setOrderByCreateTimeByDefault(res.map(i->i.getOrderByCreateTimeByDefault()).orElse(false));
        tenantOptionConfig.setShowIsPm(res.map(i->i.getShowIsPm()).orElse(false));
        tenantOptionConfig.setActiveSupplier(res.map(i->i.getActiveSupplier()).orElse(false));
        tenantOptionConfig.setAllowAssetBatchUpdate(res.map(i->i.getAllowAssetBatchUpdate()).orElse(false));
        tenantOptionConfig.setSynchronizeWorkOrder(res.map(i->i.getSynchronizeWorkOrder()).orElse(false));

        return tenantOptionConfig;
    }

    public TenantInfo getTenantByUid(String uid){
        return tenantDao.findByuid(uid);
    }


    @CacheEvict(cacheNames = "springCache", key = "'WorkflowConfig_siteUid.'+#siteUid")
    public void  cleanWorkflowConfigCache (String siteUid){
    }

    @CacheEvict(cacheNames = "springCache", key = "'OrgInfo_Uid.'+#uid")
    public void  cleanOrgInfoCacheByUid (String uid){
    }

    @CacheEvict(cacheNames = "springCache", key = "'TenantOptionConfig_Uid.'+#uid")
    public void  cleanTenantOptionConfigByUid (String uid){
    }

}
