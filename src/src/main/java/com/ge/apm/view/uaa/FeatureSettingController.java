package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.*;
import com.ge.healthcare.cn.common.utils.menu.MenuAnalysis;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
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
    private AdminCustomActiveRepository customActiveRepository;
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
    private String selectedCustomFeature;
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
    private Map<String, List<String>> moduleFeatureMap = new LinkedHashMap<>();
    @Getter
    @Setter
    private Map<String, Boolean> tenantModuleMap = new LinkedHashMap<>();
    @Getter
    @Setter
    private List<ModuleItem> moduleItems = new ArrayList<>();
    @Getter
    @Setter
    private OrgInfo selectHospital;
    @Getter
    @Setter
    private List<AdminFeatureActive> adminFeatureActives;
    @Getter
    @Setter
    private List<AdminCustomActive> adminCustomFeatureActives;
    @Getter
    @Setter
    private List<AdminFeatureActive> filterAdminFeatureActives = new ArrayList<>();
    @Getter
    @Setter
    private List<AdminCustomActive> filterAdminCustomFeatureActives = new ArrayList<>();
    @Getter
    @Setter
    private Integer tabIndex = 0;
    @Getter
    @Setter
    private String link;
    @Getter
    @Setter
    private String customFunctionName;
    @Getter
    @Setter
    private String customFunctionComment;
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
        customActiveRepository = WebUtil.getBean(AdminCustomActiveRepository.class);
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
        buildCustomActive();
        initModuleSetting();
    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }

    public void changeTab(TabChangeEvent event) {
        if (tabIndex == 3) {
            List<String> featureList = JSONArray.parseArray(getTenantInfo().getFeatureList()).toJavaList(String.class);
            List<ApmFeatureDefaultOrder> apmFeatureDefaultOrderList =  Arrays.stream(ApmFeatureDefaultOrder.values()).collect(Collectors.toList());
            List<String> defaultOrder = Arrays.stream(ApmFeatureDefaultOrder.values()).map(Enum::name).collect(Collectors.toList());
            if (tenantInfo.getEnablesMenuOrder()) {
                HashMap<String, String> defaultOrderMap = MenuAnalysis.defaultOrderToMap();
                orderedItems = featureList.stream().filter(t -> defaultOrderMap.containsKey(t)).map(t -> defaultOrderMap.get(t)).distinct().collect(Collectors.toList());
            } else {
                orderedItems =  apmFeatureDefaultOrderList.stream()
                        .filter(t -> MenuAnalysis.existOrderByFeature(t, featureList))
                        .map(ApmFeatureDefaultOrder::name).collect(Collectors.toList());
            }
        } else {
            orderedItems = new ArrayList<>();
        }
    }

    public void changeListener() {
        Optional<ApmFeatureVersion> features = Arrays.stream(ApmFeatureVersion.values()).filter(f -> f.name().equals(edition)).collect(Collectors.toSet()).stream().findFirst();
        buildActiveMap(JSON.toJSONString(features.get().getFeatureList()));
        refreshModuleChecked();
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

    public void changeCustomActiveListener() {
        if (StringUtils.hasText(selectedCustomFeature) && selectedCustomFeature.equals("ALL")) {
            filterAdminCustomFeatureActives = adminCustomFeatureActives.stream().filter(v -> v.getHospitalUID() != null).collect(Collectors.toList());
        } else {
            filterAdminCustomFeatureActives = adminCustomFeatureActives.stream().filter(v -> v.getFeatureKey().equals(selectedCustomFeature)).collect(Collectors.toList());
            List<String> activeHospital = filterAdminCustomFeatureActives.stream().filter(v -> v.getHospitalUID() != null).map(AdminCustomActive::getHospitalUID).collect(Collectors.toList());
            hospitalList.stream().filter(hospital -> !activeHospital.contains(hospital.getUid())).forEach(hospital -> {
                AdminCustomActive featureActive = new AdminCustomActive();
                featureActive.setFeatureKey(selectedCustomFeature);
                featureActive.setHospitalUID(hospital.getUid());
                featureActive.setTenantUID(hospital.getTenantUID());
                filterAdminCustomFeatureActives.add(featureActive);
            });
            filterAdminCustomFeatureActives = filterAdminCustomFeatureActives.stream().filter(v -> v.getHospitalUID() != null).collect(Collectors.toList());
        }
        if (StringUtils.hasText(hospitalStr)) {
            filterAdminCustomFeatureActives = filterAdminCustomFeatureActives.stream().filter(v -> getHospitalName(v.getHospitalUID()).contains(hospitalStr)).collect(Collectors.toList());
        }
    }

    private void buildActiveMap(String features) {
        adminFeatureActives = featureActiveRepository.findByTenantUID(tenantInfo.getUid());
        filterAdminFeatureActives = adminFeatureActives;
        List<String> featureList = JSONArray.parseArray(features).toJavaList(String.class);
        tenantFeatureMap = Arrays.asList(ApmFeature.values()).stream()
                .collect(Collectors.toMap(ApmFeature::name, i-> featureList.contains(i.name())));
        if (StringUtils.hasText(selectedFeature) && !selectedFeature.equals("ALL")) {
            changeActiveListener();
        }
        // 若模块映射已初始化，则同步模块勾选状态
        if (moduleFeatureMap != null && !moduleFeatureMap.isEmpty()) {
            refreshModuleChecked();
        }
    }

    private void buildCustomActive() {
        adminCustomFeatureActives = customActiveRepository.findByTenantUID(tenantInfo.getUid());
        filterAdminCustomFeatureActives = adminCustomFeatureActives.stream().filter(v -> v.getHospitalUID() != null).collect(Collectors.toList());
        if (StringUtils.hasText(selectedCustomFeature) && !selectedCustomFeature.equals("ALL")) {
            changeCustomActiveListener();
        }
    }

    private void initModuleSetting() {
        moduleFeatureMap = new LinkedHashMap<>();
        tenantModuleMap = new LinkedHashMap<>();
        moduleItems = new ArrayList<>();
        for (ApmFeatureGroup g : ApmFeatureGroup.values()) {
            List<String> featureKeys = g.getFeatureList().stream().map(Enum::name).collect(Collectors.toList());
            moduleFeatureMap.put(g.name(), featureKeys);
            if (g.getVersionName().equals(ApmFeatureGroup.basicVersion.getVersionName())) {
                moduleItems.add(new ModuleItem(g.name(), g.getVersionName(), true));
            } else {
                moduleItems.add(new ModuleItem(g.name(), g.getVersionName(), false));
            }
        }
        refreshModuleChecked();
    }

    public void moduleChange(String moduleKey) {
        if (Strings.isNullOrEmpty(moduleKey)) {
            return;
        }
        Boolean checked = tenantModuleMap.get(moduleKey);
        List<String> featureKeys = moduleFeatureMap.getOrDefault(moduleKey, Collections.emptyList());

        if (Boolean.TRUE.equals(checked)) {
            featureKeys.forEach(k -> tenantFeatureMap.put(k, true));
        } else {
            for (String featureKey : featureKeys) {
                if (isFeatureUsedByOtherCheckedModules(featureKey, moduleKey)) {
                    continue;
                }
                tenantFeatureMap.put(featureKey, false);
            }
        }
        refreshModuleChecked();
    }

    private boolean isFeatureUsedByOtherCheckedModules(String featureKey, String excludedModuleKey) {
        if (Strings.isNullOrEmpty(featureKey)) {
            return false;
        }
        for (Map.Entry<String, Boolean> e : tenantModuleMap.entrySet()) {
            String moduleKey = e.getKey();
            Boolean moduleChecked = e.getValue();
            if (!Boolean.TRUE.equals(moduleChecked)) {
                continue;
            }
            if (Objects.equals(moduleKey, excludedModuleKey)) {
                continue;
            }
            List<String> keys = moduleFeatureMap.getOrDefault(moduleKey, Collections.emptyList());
            if (keys.contains(featureKey)) {
                return true;
            }
        }
        return false;
    }

    private void refreshModuleChecked() {
        if (tenantModuleMap == null) {
            tenantModuleMap = new LinkedHashMap<>();
        }
        if (moduleFeatureMap == null) {
            moduleFeatureMap = new LinkedHashMap<>();
        }
        moduleFeatureMap.forEach((moduleKey, features) -> {
            boolean anySelected = features.stream().anyMatch(f -> Boolean.TRUE.equals(tenantFeatureMap.get(f)));
            tenantModuleMap.put(moduleKey, anySelected);
        });
    }

    @Getter
    @Setter
    public static class ModuleItem {
        private String key;
        private String name;
        private boolean disabled;

        public ModuleItem(String key, String name, boolean disabled) {
            this.key = key;
            this.name = name;
            this.disabled = disabled;
        }
    }

    public List<ApmAdminActiveFeature> findAdminActiveFeatures() {
        List<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class);
        List<ApmAdminActiveFeature> features = Arrays.stream(ApmAdminActiveFeature.values()).filter(v -> featureList.contains(v.name())).collect(Collectors.toList());
        features.add(ApmAdminActiveFeature.DIGITAL_MEDICAL_ENGINEERING);
        features.add(ApmAdminActiveFeature.DAVINCI_REPORT_CUSTOMIZED);
        return features;
    }

    public Set<String> findAdminCustomActiveFeatures() {
        List<AdminCustomActive> adminCustomActives = customActiveRepository.findByTenantUID(tenantInfo.getUid());
        if (CollectionUtils.isEmpty(adminCustomActives)) {
            return new HashSet<>();
        }
        return adminCustomActives.stream().map(AdminCustomActive::getFeatureKey).collect(Collectors.toSet());
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
        }

        refreshModuleChecked();
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

    public void addCustomHospital(AdminCustomActive adminCustomActive) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        adminCustomActive.setActiveUser(userAccount.getUid());
        adminCustomActive.setActiveRole(userAccount.getRoleNames().stream().collect(Collectors.joining(",")));
        adminCustomActive.setCreateDate(new Date());
        adminCustomActive.setActiveDate(new Date());
        adminCustomActive.setActiveComment(adminCustomActive.getActiveComment());
        customActiveRepository.save(adminCustomActive);
        buildCustomActive();
        MessageUtil.outPutReturnFacesMessage("激活成功", FacesMessage.SEVERITY_INFO);
    }

    public void addCustomActive() {
        AdminCustomActive adminCustomActive = new AdminCustomActive();
        adminCustomActive.setCreateDate(new Date());
        adminCustomActive.setFeatureKey(customFunctionName);
        adminCustomActive.setActiveComment(customFunctionComment);
        adminCustomActive.setTenantUID(tenantInfo.getUid());
        customActiveRepository.save(adminCustomActive);
        buildCustomActive();
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
            // 为了不影响原来的feature排序 将新保存的和后面的进行比对添加和移除
            JSONArray oldFeatureArray = JSONArray.parseArray(tenantInfo.getFeatureList());
            List<String> result = new ArrayList<>();
            // 1. 先保留旧 feature 中仍然存在的（顺序不变）
            for (int i = 0; i < oldFeatureArray.size(); i++) {
                String feature = oldFeatureArray.getString(i);
                if (featureArray.contains(feature)) {
                    result.add(feature);
                }
            }
            // 2. 新增的 feature 追加到末尾
            for (int i = 0; i < featureArray.size(); i++) {
                String feature = featureArray.getString(i);
                if (!result.contains(feature)) {
                    result.add(feature);
                }
            }
            tenantInfo.setFeatureList(JSON.toJSONString(result));
            tenantInfo.setLastModifiedDate(new Date());
            tenantInfo.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            dao.save(tenantInfo);
            configCacheService.cleanTenantCacheByUid(tenantInfo.getUid());
            MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
        } catch (Exception e) {
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void saveMenuOrder() {
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
