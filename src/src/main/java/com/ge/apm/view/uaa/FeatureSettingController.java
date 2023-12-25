package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.AdminFeatureActive;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class FeatureSettingController extends JpaCRUDController<TenantInfo> {
    private static final Logger log = LoggerFactory.getLogger(FeatureSettingController.class);

    TenantInfoRepository dao;

    private ConfigCacheService configCacheService;
    private AdminFeatureActiveRepository featureActiveRepository;
    private OrgInfoRepository orgDao;

    @Getter
    @Setter
    private TenantInfo tenantInfo;

    @Getter
    @Setter
    private String edition;

    @Getter
    @Setter
    private String cpmURL;

    @Getter
    @Setter
    private Map<String, Boolean> tenantFeatureMap = new HashMap<>();

    @Getter
    @Setter
    private JSONObject featureSettings;

    @Getter
    @Setter
    private String selectedFeature;

    @Getter
    @Setter
    private List<OrgInfo> hospitalList;

    @Getter
    @Setter
    private List<String> selectHospitalUidList;

    @Override
    protected void init() {

        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        featureActiveRepository = WebUtil.getBean(AdminFeatureActiveRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        dao = WebUtil.getBean(TenantInfoRepository.class);


        String tenantUid = "";
        String encodeStr = WebUtil.getRequestParameter("str");
        if (encodeStr != null) {
            tenantUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "tenantUid");
        }
        tenantInfo = configCacheService.getTenantByUid(tenantUid);

        JSONObject tenantConfig = JSONObject.parseObject(tenantInfo.getTenantConfig());

        edition = tenantConfig.getString("edition");
        cpmURL = tenantConfig.getString("cpmURL");


        hospitalList = orgDao.findByTenantUIDAndOrgType(tenantInfo.getUid(), 2);
        buildActiveMap();

    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }



    private void buildActiveMap() {
        List<AdminFeatureActive> adminFeatureActives = featureActiveRepository.findByTenantUID(tenantInfo.getUid());
        List<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class);
        tenantFeatureMap = Arrays.asList(ApmFeature.values()).stream()
                .collect(Collectors.toMap(ApmFeature::getFeatureName, i-> featureList.contains(i.getFeatureName())));
    }

    public void featureChange(String feature) {

        if (Strings.isNullOrEmpty(feature)) {
            return;
        }

        switch (feature) {
            case "INSTALL":
            case "INSTALL_CHECK":
                if (!tenantFeatureMap.get("INSTALL")) {
                    tenantFeatureMap.put("INSTALL_CHECK", false);
                }
                break;
            case "NURSESITE_RECORD":
                if (tenantFeatureMap.get("NURSESITE_RECORD")) {
                    tenantFeatureMap.put("NURSESITE_USING", false);
                }
                break;
            case "NURSESITE_USING":
                if (tenantFeatureMap.get("NURSESITE_USING")) {
                    tenantFeatureMap.put("NURSESITE_RECORD", false);
                }
                break;
        }


    }


    public void addHospital() {

        List<AdminFeatureActive> addActives = selectHospitalUidList.stream().map(i -> {
            AdminFeatureActive adminFeatureActive = new AdminFeatureActive();
            adminFeatureActive.setFeatureKey(selectedFeature);
            adminFeatureActive.setHospitalUID(i);
            adminFeatureActive.setTenantUID(tenantInfo.getUid());
            adminFeatureActive.setCreateDate(new Date());
            return adminFeatureActive;
        }).collect(Collectors.toList());
        featureActiveRepository.save(addActives);
        buildActiveMap();
    }

    public void saveFeatures(){
        JSONObject tenantConfig = JSONObject.parseObject(tenantInfo.getTenantConfig());
        tenantConfig.put("edition",edition);
        tenantConfig.put("cpmURL",cpmURL);

        JSONArray featureArray = new JSONArray();
        featureArray.addAll(tenantFeatureMap.keySet());
        tenantInfo.setFeatureList(featureArray.toJSONString());
        dao.save(tenantInfo);
    }

}
