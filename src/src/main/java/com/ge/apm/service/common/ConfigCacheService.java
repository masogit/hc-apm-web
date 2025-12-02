package com.ge.apm.service.common;


import com.alibaba.fastjson.JSONArray;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
public class ConfigCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCacheService.class);

    @Autowired
    private WorkflowConfigRepository configDao;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private TenantInfoRepository tenantDao;

    @Autowired
    private TenantOptionConfigRepository tenantOptionConfigRepository;

    @Autowired
    private SharedAssetService remoteService;

    @Autowired
    private AdminFeatureActiveRepository adminFeatureActiveRepository;

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

    @Cacheable(cacheNames = "springCache", key = "'TenantInfo_Uid.'+#uid")
    public TenantInfo getTenantByUid(String uid){
        return tenantDao.findByuid(uid);
    }


    @CacheEvict(cacheNames = "springCache", key = "'WorkflowConfig_siteUid.'+#siteUid")
    public void  cleanWorkflowConfigCache (String siteUid){
    }

    @CacheEvict(cacheNames = "springCache", key = "'TenantInfo_Uid.'+#uid")
    public void  cleanTenantCacheByUid (String uid){
        remoteService.asynRemoteCall(GatewayURL.CLEAN_TENANT_CACHE, HttpMethod.POST,new HashMap<>(),(url)->url.replace("{uid}",uid));
    }

    @CacheEvict(cacheNames = "springCache", key = "'OrgInfo_Uid.'+#uid")
    public void  cleanOrgInfoCacheByUid (String uid){
        remoteService.asynRemoteCall(GatewayURL.CLEAN_ORG_CACHE, HttpMethod.POST,new HashMap<>(),(url)->url.replace("{uid}",uid));
    }

    @CacheEvict(cacheNames = "springCache", key = "'TenantOptionConfig_Uid.'+#uid")
    public void  cleanTenantOptionConfigByUid (String uid){
    }

    @CacheEvict(cacheNames = "springCache", allEntries = true)
    public void cleanCacheNames(){
        remoteService.asynRemoteCall(GatewayURL.CLEAN_CACHE_NAMES, HttpMethod.POST,new HashMap<>(),(url)->url);
    }

    @PostConstruct
    public void initTenantInfo(){
        List<SearchFilter> searchFilterList = new ArrayList<>();
        List<TenantInfo> tenantInfos = tenantDao.findBySearchFilter(searchFilterList);
        LOGGER.info("同步 tenant feature list start");
        for (TenantInfo tenantInfo : tenantInfos) {
            String featureList = tenantInfo.getFeatureList();
            if(StringUtils.isEmpty(featureList)) continue;
            if (featureList.contains(ApmFeature.MOBILE_REPAIR.name())) continue;
            //["LOG_AUDIT","ASSET_ACCOUNT_DASHBOARD","ASSET_RISK_EVALUATE","ASSET_INVENTORY","ASSET_INFO","PERFORMANCE","KNOWLEDGE","COMPANY_FILES","TRAINING","CONFIG","REPAIR","MAINTAIN_TEMPLATE","ASSET_BATCH_IMPORT","REPAIR_TEMPLATE","ASSET_PARENTS_INFO","MULTILEVEL_APPROVE","MAINTAIN","ASSET_SCRAP","PLATFORM_CONFIG","MAINTAIN_ELECTRICDATAIMPORT","ASSET_LOCATION"]
            JSONArray featureArray = JSONArray.parseArray(featureList);
            featureArray.add(ApmFeature.MOBILE_REPAIR.name());
            featureArray.add(ApmFeature.MOBILE_REPAIR_MANAGEMENT.name());
            featureArray.add(ApmFeature.MOBILE_MAINTENANCE_MANAGEMENT.name());
            featureArray.add(ApmFeature.MOBILE_MAINTENANCE_SPARE_PARTS_APPROVAL.name());
            featureArray.add(ApmFeature.MOBILE_REPAIR_MY_WORK_ORDERS.name());
            featureArray.add(ApmFeature.MOBILE_MAINTENANCE_MY_WORK_ORDERS.name());
            featureArray.add(ApmFeature.MOBILE_DEPARTMENTAL_REPAIR_REPORT.name());
            String jsonString = featureArray.toJSONString();
            tenantInfo.setFeatureList(jsonString);
            LOGGER.info("同步 tenant feature list tenant:{},json:{}",tenantInfo.getName(),jsonString);
        }
        LOGGER.info("同步 tenant feature list end size:{}",tenantInfos.size());
        tenantDao.save(tenantInfos);
    }

    private void initialActiveFeature(){
        LOGGER.info("同步 tenant feature active start");
        String featureKey = ApmFeature.HISPERFORMANCE.name();
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("featureKey", SearchFilter.Operator.EQ,featureKey));
        List<AdminFeatureActive> adminFeatureActives = adminFeatureActiveRepository.findBySearchFilter(searchFilterList);
        if(!CollectionUtils.isEmpty(adminFeatureActives)) return;
        searchFilterList.clear();
        searchFilterList.add(new SearchFilter("featureList", SearchFilter.Operator.LIKE,featureKey));
        List<TenantInfo> tenantInfos = tenantDao.findBySearchFilter(searchFilterList);
        if(CollectionUtils.isEmpty(tenantInfos)) return;
        for (TenantInfo tenantInfo : tenantInfos) {
            createAdminFeatureActive(tenantInfo,featureKey);
        }
        LOGGER.info("同步 tenant feature active end");
    }
    private void createAdminFeatureActive(TenantInfo tenantInfo,String featureKey){
        List<OrgInfo> orgInfos = orgDao.findByTenantUIDAndOrgType(tenantInfo.getUid(),2);
        List<AdminFeatureActive> adminFeatureActives = orgInfos.stream().map(item -> {
            AdminFeatureActive adminFeatureActive = new AdminFeatureActive();
            adminFeatureActive.setFeatureKey(featureKey);
            adminFeatureActive.setActiveRole("SuperAdmin");
            adminFeatureActive.setTenantUID(item.getTenantUID());
            adminFeatureActive.setHospitalUID(item.getHospitalUID());
            adminFeatureActive.setCreateDate(new Date());
            adminFeatureActive.setActiveDate(new Date());
            LOGGER.info("同步 tenant feature active end orgName:{}", item.getName());
            return adminFeatureActive;
        }).collect(Collectors.toList());
        adminFeatureActiveRepository.save(adminFeatureActives);
    }
}
