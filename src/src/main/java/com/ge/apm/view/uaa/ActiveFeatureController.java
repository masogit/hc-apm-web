package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ge.apm.dao.AdminFeatureActiveRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.AdminFeatureActive;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmAdminActiveFeature;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ActiveFeatureController extends GenericCRUDUUIDController<AdminFeatureActive> {
    private static final Logger log = LoggerFactory.getLogger(ActiveFeatureController.class);

    private TenantInfoRepository tenantInfoRepository;
    private ConfigCacheService configCacheService;
    private AdminFeatureActiveRepository dao;
    private OrgInfoRepository orgDao;
    private UserCommonService userService;

    @Getter
    @Setter
    private TenantInfo tenantInfo;

    @Getter
    @Setter
    private List<OrgInfo> hospitalList;

    @Getter
    @Setter
    private List<String> selectHospitalUidList;

    @Getter
    @Setter
    private Map<String, Boolean> selectFeatureMap = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Boolean> oldFeatureMap = new HashMap<>();

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
    private boolean confirm = false;

    @Override
    protected void init() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        String hospitalUID = "";
        String encodeStr = WebUtil.getRequestParameter("str");
        if (encodeStr != null) {
            hospitalUID = (String) UrlEncryptController.getValueFromMap(encodeStr, "hospitalUID");
        }
        if (userContextService.getLoginUser() == null || !StringUtils.hasText(hospitalUID)) {
            return;
        }
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        userService = WebUtil.getBean(UserCommonService.class);
        dao = WebUtil.getBean(AdminFeatureActiveRepository.class);
        selectHospital = orgDao.findByUid(hospitalUID);
        if (ObjectUtils.isEmpty(selectHospital)) {
            return;
        }
        tenantInfo = tenantInfoRepository.findByUid(selectHospital.getTenantUID());
        if (ObjectUtils.isEmpty(tenantInfo) || !selectHospital.getTenantUID().equals(userContextService.getLoginUser().getTenantUID())) {
            WebUtil.redirectTo("/accessdeniedzh.xhtml");
            return;
        }
        buildActiveMap(tenantInfo.getFeatureList());

    }

    @Override
    protected AdminFeatureActiveRepository getDAO() {
        return dao;
    }

    private void buildActiveMap(String features) {
        adminFeatureActives = dao.findByHospitalUID(selectHospital.getUid());
        List<String> hasFeature = adminFeatureActives.stream().map(AdminFeatureActive::getFeatureKey).collect(Collectors.toList());
        List<String> featureList = JSONArray.parseArray(features).toJavaList(String.class);
        List<ApmAdminActiveFeature> waitActiveList = Arrays.stream(ApmAdminActiveFeature.values()).filter(v -> featureList.contains(v.name())).collect(Collectors.toList());
        waitActiveList.forEach(v -> {
            if (hasFeature.contains(v.name())) {
                selectFeatureMap.put(v.name(), true);
                oldFeatureMap.put(v.name(), true);
            }
        });
    }

    public List<ApmAdminActiveFeature> findAdminActiveFeatures() {
        List<String> featureList = JSONArray.parseArray(tenantInfo.getFeatureList()).toJavaList(String.class);
        return Arrays.stream(ApmAdminActiveFeature.values()).filter(v -> featureList.contains(v.name())).collect(Collectors.toList());
    }

    public void featureChange(String feature) {
        if (Strings.isNullOrEmpty(feature)) {
            return;
        }
    }

    public void addHospital() {
        selectFeatureMap = selectFeatureMap.entrySet().stream().filter(entry -> entry.getValue() && null == oldFeatureMap.get(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (CollectionUtils.isEmpty(selectFeatureMap)) {
            buildActiveMap(tenantInfo.getFeatureList());
            MessageUtil.outPutReturnFacesMessage("请勾选需要激活的功能", FacesMessage.SEVERITY_INFO);
            return;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        AdminFeatureActive adminFeatureActive = new AdminFeatureActive();
        adminFeatureActive.setHospitalUID(selectHospital.getUid());
        adminFeatureActive.setTenantUID(tenantInfo.getUid());
        adminFeatureActive.setActiveUser(userAccount.getUid());
        adminFeatureActive.setActiveRole(userAccount.getRoleNames().stream().collect(Collectors.joining(",")));
        adminFeatureActive.setCreateDate(new Date());
        adminFeatureActive.setActiveDate(new Date());
        selectFeatureMap.forEach((k, v) -> {
            adminFeatureActive.setId(null);
            adminFeatureActive.setFeatureKey(k);
            dao.save(adminFeatureActive);
        });
        buildActiveMap(tenantInfo.getFeatureList());
        MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
    }

    public String getFeatureName(String feature) {
        return ApmAdminActiveFeature.valueOf(feature).name();
    }
}
