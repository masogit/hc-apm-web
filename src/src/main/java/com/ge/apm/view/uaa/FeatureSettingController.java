package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.AdminFeatureActive;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.*;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private UserCommonService userService;

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

    @Getter
    @Setter
    private Map<String, Boolean> selectHospitalMap = new HashMap<>();

    @Getter
    @Setter
    private OrgInfo selectHospital;

    @Getter
    @Setter
    private List<AdminFeatureActive> adminFeatureActives;

    @Getter
    @Setter
    private List<AdminFeatureActive> filterAdminFeatureActives = new ArrayList<>();

    @Getter
    @Setter
    private Integer tabIndex = 0;

    @Getter
    @Setter
    private String link;

    @Getter
    @Setter
    private String hospitalStr;

    @Getter
    @Setter
    private List<String> orderedItems = new ArrayList<>();

    @Override
    protected void init() {

        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        featureActiveRepository = WebUtil.getBean(AdminFeatureActiveRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        dao = WebUtil.getBean(TenantInfoRepository.class);
        userService = WebUtil.getBean(UserCommonService.class);

        String tenantUid = "";
        String encodeStr = WebUtil.getRequestParameter("str");
        if (encodeStr != null) {
            tenantUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "tenantUid");
        }
        tenantInfo = dao.findByUid(tenantUid);

        JSONObject tenantConfig = JSONObject.parseObject(tenantInfo.getTenantConfig());

        edition = tenantConfig.getString("edition");
        cpmURL = tenantConfig.getString("cpmURL");
        selectedFeature = "ALL";
        hospitalList = orgDao.findByTenantUIDAndOrgType(tenantInfo.getUid(), 2);
        buildActiveMap(tenantInfo.getFeatureList());

    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }

    public void changeTab(TabChangeEvent event) {
        if (tabIndex == 2) {
            List<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class);
            List<String> defaultOrder = Arrays.stream(ApmFeatureDefaultOrder.values()).map(Enum::name).collect(Collectors.toList());
            if (tenantInfo.getEnablesMenuOrder()) {
                orderedItems = featureList.stream()
                        .filter(defaultOrder::contains)  // 移除不在 defaultOrder 中的字符串
                        .collect(Collectors.toList());
            } else {
                orderedItems = featureList.stream()
                        .sorted(Comparator.comparingInt(defaultOrder::indexOf))  // 根据 defaultOrder 的顺序排序
                        .filter(defaultOrder::contains)  // 移除不在 defaultOrder 中的字符串
                        .collect(Collectors.toList());
            }
        } else {
            orderedItems = new ArrayList<>();
        }
    }

    public void changeListener() {
        Optional<ApmFeatureVersion> features = Arrays.stream(ApmFeatureVersion.values()).filter(f -> f.name().equals(edition)).collect(Collectors.toSet()).stream().findFirst();
        buildActiveMap(JSON.toJSONString(features.get().getFeatureList()));
    }

    public void changeActiveListener() {
        if (StringUtils.hasText(selectedFeature) && selectedFeature.equals("ALL")) {
            filterAdminFeatureActives = adminFeatureActives;
        } else {
            filterAdminFeatureActives = adminFeatureActives.stream().filter(v -> v.getFeatureKey().equals(selectedFeature)).collect(Collectors.toList());
            List<String> activeHospital = filterAdminFeatureActives.stream().map(AdminFeatureActive::getHospitalUID).collect(Collectors.toList());
            hospitalList.stream().filter(hospital -> !activeHospital.contains(hospital.getUid())).forEach(hospital -> {
                AdminFeatureActive featureActive = new AdminFeatureActive();
                featureActive.setFeatureKey(selectedFeature);
                featureActive.setHospitalUID(hospital.getUid());
                featureActive.setTenantUID(hospital.getTenantUID());
                filterAdminFeatureActives.add(featureActive);
            });
        }
        if (StringUtils.hasText(hospitalStr)) {
            filterAdminFeatureActives = filterAdminFeatureActives.stream().filter(v -> getHospitalName(v.getHospitalUID()).contains(hospitalStr)).collect(Collectors.toList());
        }
    }

    private void buildActiveMap(String features) {
        adminFeatureActives = featureActiveRepository.findByTenantUID(tenantInfo.getUid());
        filterAdminFeatureActives = adminFeatureActives;
        List<String> featureList = JSONArray.parseArray(features).toJavaList(String.class);
        tenantFeatureMap = Arrays.asList(ApmFeature.values()).stream()
                .collect(Collectors.toMap(ApmFeature::name, i-> featureList.contains(i.name())));
        if(!tenantFeatureMap.get("ADVERSE_MANAGE")){
            if(tenantFeatureMap.get("ASSET_ADVERSE_EVENT") || tenantFeatureMap.get("REPAIR_ADVERSE")){
                tenantFeatureMap.put("ADVERSE_MANAGE", true);
            }else{
                tenantFeatureMap.put("ADVERSE_MANAGE", false);
            }
        }
        if (StringUtils.hasText(selectedFeature) && !selectedFeature.equals("ALL")) {
            changeActiveListener();
        }
    }

    public List<ApmAdminActiveFeature> findAdminActiveFeatures() {
        List<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class);
        List<ApmAdminActiveFeature> features = Arrays.stream(ApmAdminActiveFeature.values()).filter(v -> featureList.contains(v.name())).collect(Collectors.toList());
        features.add(ApmAdminActiveFeature.DIGITAL_MEDICAL_ENGINEERING);
        return features;
    }

    public void hospitalChange() {
        if (StringUtils.isEmpty(selectedFeature)) {
            selectHospitalMap.clear();
        }
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
            case "INSTALL_OUT_STOCK":
                if (!tenantFeatureMap.get("INSTALL")) {
                    tenantFeatureMap.put("INSTALL_OUT_STOCK", false);
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
            case "PARTS":
                if (tenantFeatureMap.get("PARTS")) {
                    tenantFeatureMap.put("PARTS_APPLY", false);
                }
                break;
            case "PARTS_APPLY":
                if (tenantFeatureMap.get("PARTS_APPLY")) {
                    tenantFeatureMap.put("PARTS", false);
                }
                break;
            case "ASSET_ADVERSE_EVENT":
            case "REPAIR_ADVERSE":
                if(tenantFeatureMap.get("ASSET_ADVERSE_EVENT") || tenantFeatureMap.get("REPAIR_ADVERSE")){
                    tenantFeatureMap.put("ADVERSE_MANAGE", true);
                }else{
                    tenantFeatureMap.put("ADVERSE_MANAGE", false);
                }
                break;
        }
    }

    public void addHospital(AdminFeatureActive adminFeatureActive) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        adminFeatureActive.setActiveUser(userAccount.getUid());
        adminFeatureActive.setActiveRole(userAccount.getRoleNames().stream().collect(Collectors.joining(",")));
        adminFeatureActive.setCreateDate(new Date());
        adminFeatureActive.setActiveDate(new Date());
        featureActiveRepository.save(adminFeatureActive);
        buildActiveMap(tenantInfo.getFeatureList());
        MessageUtil.outPutReturnFacesMessage("激活成功", FacesMessage.SEVERITY_INFO);
    }

    public void saveFeatures(){
        try {
            JSONObject tenantConfig = JSONObject.parseObject(tenantInfo.getTenantConfig());
            tenantConfig.put("edition", edition);
            tenantConfig.put("cpmURL", cpmURL);
            JSONArray featureArray = new JSONArray();
            tenantFeatureMap.forEach((k,v) -> {
                if (v) {
                    featureArray.add(k);
                }
            });
            tenantInfo.setFeatureList(featureArray.toJSONString());
            tenantInfo.setLastModifiedDate(new Date());
            tenantInfo.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            dao.save(tenantInfo);
            configCacheService.cleanTenantCacheByUid(tenantInfo.getUid());
            MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void saveMenuOrder(){
        try {
            List<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class);
            orderedItems.addAll(featureList.stream()
                    .filter(s -> !orderedItems.contains(s))
                    .collect(Collectors.toList()));
            tenantInfo.setFeatureList(JSON.toJSONString(orderedItems));
            tenantInfo.setLastModifiedDate(new Date());
            tenantInfo.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            dao.save(tenantInfo);
            changeTab(null);
            MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public String getUserName(String userUid) {
        if (StringUtils.hasText(userUid)) {
            UserAccount user = userService.getUserByUid(userUid);
            return null == user ? "" : user.getName();
        }
        return "";
    }

    public String getRoleName(String role) {
        if (StringUtils.hasText(role)) {
            Map<String, String> roles = Arrays.stream(ApmSystemRole.values()).collect(Collectors.toMap(ApmSystemRole::name, ApmSystemRole::getRoleName));
            return Arrays.stream(role.split(",")).map(v -> Optional.ofNullable(roles.get(v)).orElse(ApmSystemRole.existSysRole.getRoleName())).collect(Collectors.joining(","));
        }
        return "";
    }

    public String getHospitalName(String uid) {
        if (StringUtils.hasText(uid)) {
            return hospitalList.stream().filter(v -> v.getUid().equals(uid)).findFirst().orElse(new OrgInfo()).getName();
        }
        return "";
    }

    public String getFeatureName(String featureName) {
        if (StringUtils.hasText(featureName)) {
            return ApmAdminActiveFeature.valueOf(featureName).getFeatureName();
        }
        return "";
    }

    public String getOrderFeatureName(String featureName) {
        if (StringUtils.hasText(featureName)) {
            return ApmFeatureDefaultOrder.valueOf(featureName).getFeatureName();
        }
        return "";
    }

    public Boolean isActive(String userUid) {
        return StringUtils.hasText(userUid);
    }

    public void generateActiveLink(String hospitalUID) throws IOException {
        if (StringUtils.isEmpty(hospitalUID)) {
            link = "";
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        // 获取 HttpServletRequest 对象
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        // 获取当前项目的请求地址
        String requestURL = request.getRequestURL().toString().replace("/admin/uaa/siteInfo/FeatureSetting.xhtml", "");

        link = requestURL + "/portal/activeFeature.xhtml?str=" + UrlParamUtil.encodeUrlQueryString("hospitalUID=" + hospitalUID);

    }

}
