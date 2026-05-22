package com.ge.apm.view.sysutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.web.cboard.CBoardController;
import com.ge.healthcare.cn.common.cons.*;
import com.ge.healthcare.cn.common.utils.menu.MenuAnalysis;
import com.google.common.base.Strings;
import javaslang.control.Option;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.B64Code;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import webapp.framework.context.LocaleHolder;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "userContextService")
@SessionScoped
public class UserContextService implements Serializable {
    public static  final Logger logger = LoggerFactory.getLogger(UserContextService.class);

    private static final long serialVersionUID = 1L;
    public static final String ADMIN = "SuperAdmin,ITAdmin";

    private TenantInfo tenant = null;
    private UserAccount userAccount = null;
    private List<CustomRole> listCustomRole=null;
    private List<SysRole> listSysRole=null;
    private Set<String> apmFunctionList = null;
    private Set<ApmSystemRole> apmSystemRoles = null;
    private Set<String> apmFeatures = null;
    private Map<String, Boolean> adminActive = null; //单独激活项
    private Boolean enableMaterialsInterfaceFlag = null;
    private String selectedSiteUid;
    private Boolean flagIsUltraSoundTenant = null;
    private Boolean flagIsAgentTenant = null;
    private Boolean flagIsHospitalUser = null;
    private Boolean enableAssetGroupAssetTypeGroupAssociation = null;// 开启资产分类是否关联设备分类
    private Boolean enableAuditGroupConfiguration = null;// 开启核心组审核
    private Boolean synchronizeWorkOrder = null;// 开启MVS工单同步
    //选定的院区
    private OrgInfo selectSite;
    private OrgInfo selectSiteSchedule;
    private List<OrgInfo> child = new ArrayList<>();
    private Map<String, Boolean> enableBtnByKey = new HashMap<>();

    private Map<String,Integer> pageSizeCache = new HashMap<>();
    public static void setLoginUser(UserAccount user) {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        userContextService.userAccount = user;

        // reset previous cached data
        userContextService.isDispatcher = null;
        userContextService.isPmDispatcher = null;
        userContextService.isMeasuringDispatcher = null;
        userContextService.isAssetApprover = null;
        userContextService.needAssetApproval = null;
        userContextService.needInstorageCheck = null;

        userContextService.flagIsAgentTenant = null;
        userContextService.flagIsUltraSoundTenant = null;
        userContextService.flagIsHospitalUser = null;

        userContextService.tenant = null;

        userContextService.apmFeatures = null;
        userContextService.apmSystemRoles = null;
        userContextService.apmFunctionList = null;
        userContextService.listSysRole = null;
        userContextService.listCustomRole = null;

        userContextService.getTenantInfo();
        userContextService.getListCustomRole(user.getId());
        userContextService.getListSysRole(user.getId());
        if (StringUtils.isNotEmpty(userContextService.tenant.getFeatureList())) {
            userContextService.getFunctionListByFeatureAndRoles();
            userContextService.getFeatureList();
            userContextService.getApmSysRole();
            CustomPermissionEvaluator.apmFeatures = userContextService.apmFeatures;
            CustomPermissionEvaluator.apmFunctionList = userContextService.apmFunctionList;
            CustomPermissionEvaluator.apmSystemRoles = userContextService.apmSystemRoles;
        }
    }

    public void processAfterLogin() {
        getLoginUser();
    }
    public static void removeLoginUser(){
        UserContextService contextService = WebUtil.getBean(UserContextService.class);
        contextService.userAccount = null;
    }

    public boolean isLoggedIn() {
        return !isAnonymousUser();
    }

    public String getUserLoginName() {
        if (userAccount == null) return "";
        else return userAccount.getLoginName();
    }

    public String getUserName() {
        if (userAccount == null) {
            return "";
        } else {
            return userAccount.getName();
        }
    }

    public boolean isAnonymousUser() {
        return UserContext.ANONYMOUS_USER.equalsIgnoreCase(getUserLoginName());
    }

    public static List<String> getRoles() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount user = userContextService.getLoginUser();

        return user.getRoleNames();
    }

    public boolean isOnlyAssetStaff() {
        List<String> roles = getRoles();
        if (roles.size() == 1) {
            return roles.get(0).equalsIgnoreCase(ApmSystemRole.AssetStaff.name());
        } else {
            return false;
        }
    }

    public boolean hasRole(String role) {
        return UserContextService.checkRole(role);
    }

    public boolean hasAnyRole(String roleList) {
        List<String> roles = Arrays.asList(roleList.split(","));
        if (CollectionUtils.isEmpty(roles)) return false;
        UserAccount user = getLoginUser();
        List<String> userRoles = user.getRoleNames();
        if (userRoles.stream().anyMatch(roles::contains)) return true;
        //增加自定义角色判断
        List<CustomRole> customRoles = getListCustomRole(user.getId());
        List<String> names = customRoles.stream().map(CustomRole::getGroups).collect(Collectors.toList());
        return names.stream().anyMatch(roles::contains);
    }

    /**
     * 判断当前登录是否科室级用户
     * @return boolean
     */
    public static boolean isDeparmentUser() {
        OrgInfo org = WebUtil.getBean(OrgInfoRepository.class).getByUid(getCurrentUserAccount().getOrgInfoUID());
        return  null == org.getOrgType() ? Boolean.FALSE : 4 == org.getOrgType();
    }
    /**
     * 判断当前登录是
     * 该租户已开启了无人值守服务
     * 该租户已开启了小型物联网设备(急救类)服务
     * @return boolean
     */
    public boolean accessContain() {
        UserAccount currentUserAccount = getCurrentUserAccount();
        WorkflowConfig config = WebUtil.getBean(ConfigService.class).findConfigBySite(currentUserAccount.getSiteUID());
        if (null != config) {
            String whetherToEnableUnattended = WebUtil.getBean(ConfigItemValueRepository.class).findByKeyAndSiteForUseParent(currentUserAccount.getSiteUID(), currentUserAccount.getHospitalUID(), currentUserAccount.getInstitutionUID(), currentUserAccount.getTenantUID(), "whetherToEnableUnattended");
            return Boolean.TRUE.toString().equalsIgnoreCase(whetherToEnableUnattended) && BooleanUtils.isTrue(config.getIsiotenabled());
        }
        return false;
    }

    public boolean enableSiteToSiteAllocation() {
        WorkflowConfig config = WebUtil.getBean(ConfigService.class).findConfigBySite(getCurrentUserAccount().getSiteUID());
        if  (null == config ) {
            return false;
        }
        return config.getEnableSiteToSiteAllocation() != null && config.getEnableSiteToSiteAllocation();
    }
    public boolean enableAssetPreview() {
        WorkflowConfig config = WebUtil.getBean(ConfigService.class).findConfigBySite(getCurrentUserAccount().getSiteUID());
        if  (null == config ) {
            return false;
        }
        return config.getEnableAssetPreview() != null && config.getEnableAssetPreview();
    }
    public boolean enableSendRepair() {
        WorkflowConfig config = WebUtil.getBean(ConfigService.class).findConfigBySite(getCurrentUserAccount().getSiteUID());
        if (null == config ) {
            return false;
        }
        return config.getEnableSendRepair() != null && config.getEnableSendRepair();
    }

    public boolean hasNotAnyRole(String roleList) {
        return !hasAnyRole(roleList);
    }

    public TenantInfo getTenantInfo() {
        if (tenant != null) return tenant;
        TenantInfoRepository tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        tenant = tenantInfoRepository.findByuid(getLoginUser().getTenantUID());
        if (tenant.getCpmURL() == null) {
            String configStr = tenant.getTenantConfig();
            if (StringUtils.isNotEmpty(configStr)) {
                try {
                    String cpmURL = JSON.parseObject(configStr).getString("cpmURL");
                    if (StringUtils.isNotEmpty(cpmURL)) {
                        tenant.setCpmURL(cpmURL);
                    }
                } catch (Exception e) {
                    tenant.setCpmURL("");
                }
            }
        }
        return tenant;
    }

    public boolean hasLocationEn() {
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getLocationEn() != null;
    }

    public List<CustomRole> getListCustomRole(Integer userId){
    	if(listCustomRole==null){
    		CustomRoleRepository customRoleRepository=WebUtil.getBean(CustomRoleRepository.class);
    		listCustomRole=customRoleRepository.findCustomRolesByUserId(userId);
    	}
    	return listCustomRole;
    }
    public List<SysRole> getListSysRole(Integer userId){
    	if(listSysRole==null){
    		SysRoleRepository customRoleRepository=WebUtil.getBean(SysRoleRepository.class);
    		listSysRole=customRoleRepository.getSysRoles(userId);
    	}
    	return listSysRole;
    }

    public boolean accessContains(String menuName){
    	return authContains(menuName)&&versionContains(menuName);
    }

    public List<String> menuOrder() {
        List<String> defaultMenuOrder = Arrays.stream(ApmFeatureDefaultOrder.values()).map(ApmFeatureDefaultOrder::name).collect(Collectors.toList());
        if (getTenantInfo().getEnablesMenuOrder()) {
            //
            List<String> featureList = JSONArray.parseArray(getTenantInfo().getFeatureList()).toJavaList(String.class);
            /*defaultMenuOrder = JSONArray.parseArray(getTenantInfo().getFeatureList()).toJavaList(String.class)
                    .stream().filter(defaultMenuOrder::contains)
                    .collect(Collectors.toList());*/
            HashMap<String, String> defaultOrderMap = MenuAnalysis.defaultOrderToMap();
            defaultMenuOrder = featureList.stream().filter(t -> defaultOrderMap.containsKey(t)).map(t -> defaultOrderMap.get(t)).distinct().collect(Collectors.toList());
        }
        return defaultMenuOrder;
    }



    public boolean accessContainsFunctionMenu(String functionName) {
        Set<String> functions = getFunctionListByFeatureAndRoles();
        functions = functions.stream().filter(function -> function.startsWith(functionName)).collect(Collectors.toSet());
        return CollectionUtils.isNotEmpty(functions);
    }

    public boolean accessContainsFunctionMenu2(String functionName) {
        Set<String> functions = getFunctionListByFeatureAndRoles();
        functions = functions.stream().filter(function -> function.contains(functionName)).collect(Collectors.toSet());
        return CollectionUtils.isNotEmpty(functions);
    }

    public String queryDavinciUrl() {
        ConfigItemValueRepository configItemValueRepository=WebUtil.getBean(ConfigItemValueRepository.class);
        UserAccount currentUser = getCurrentUserAccount();
        return configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "ibReportUrl");
    }

    public boolean accessContainsApmFeature(String featureName) {
        Set<String> apmFeatures = getFeatureList();
        apmFeatures = apmFeatures.stream().filter(apmFeature -> apmFeature.startsWith(featureName)).collect(Collectors.toSet());
        return CollectionUtils.isNotEmpty(apmFeatures);
    }

    public boolean hasAnyRoleByApmRoles(ApmSystemRole ... roleList) {
        List<ApmSystemRole> roles = Optional.ofNullable(Arrays.asList(roleList)).orElse(new ArrayList<>());
        if (CollectionUtils.isEmpty(roles)) return false;
        return getApmSysRole().stream().anyMatch(roles::contains);
    }

    public boolean hasRoleByApmRoles(ApmSystemRole ... roleList) {
        List<ApmSystemRole> roles = Optional.ofNullable(Arrays.asList(roleList)).orElse(new ArrayList<>());
        if (CollectionUtils.isEmpty(roles)) return false;
        return getApmSysRole().stream().allMatch(roles::contains);
    }

    public boolean accessContainsOfJiangNing() {
        TenantInfoRepository tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        List<TenantInfo> tenantInfos = tenantInfoRepository.findByCustomTenant("2");
        if (CollectionUtils.isEmpty(tenantInfos)) {
            return false;
        }
        TenantInfo tf = tenantInfos.get(0);
        return tf.getUid().equals(userAccount.getTenantUID());
    }

    public boolean getUserAccountApprove(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccountRepository userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        userAccount = userContextService.getLoginUser();
        AuditGroupConfigurationRepository auditGroupConfigurationRepository=WebUtil.getBean(AuditGroupConfigurationRepository.class);
        AuditGroupConfiguration auditGroupConfiguration = auditGroupConfigurationRepository.findByHierOrgUidAndGroupName(userAccount.getHospitalUID(), WorkOrderConstant.CORE_GROUP_MEMBERS);
        if(auditGroupConfiguration!=null){
            String groupMemberItem = auditGroupConfiguration.getGroupMemberItem();
            List<AuditGroupConfigurationNode> auditGroupConfigurationNodes = JSON.parseObject(groupMemberItem, new TypeReference<List<AuditGroupConfigurationNode>>() {});
            if (auditGroupConfigurationNodes != null && !auditGroupConfigurationNodes.isEmpty()) {
                for (AuditGroupConfigurationNode auditGroupConfigurationNode : auditGroupConfigurationNodes) {
                    List<String> userUIds = auditGroupConfigurationNode.getUserUids();
                    UserAccount user = userAccountRepository.getByUid(userUIds.get(0));
                    if (user != null && userAccount.getUid().equals(user.getUid())) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public boolean isSuperAdminOrSiteAdminOrGeOp(UserAccount userAccount) {
        return userAccount.getIsSuperAdmin() || userAccount.getIsSiteAdmin() || userAccount.getIsGeOp();
    }

    public boolean authContains(String menuName) {
        if (isSuperAdminOrSiteAdminOrGeOp(userAccount)) {
            return true;
        }
        List<CustomRole> customRoles = getListCustomRole(userAccount.getId());
        List<SysRole> sysRoles = getListSysRole(userAccount.getId());
        boolean result = Boolean.FALSE;
        if (CollectionUtils.isNotEmpty(sysRoles)) {
            result = sysRoles.stream()
                    .filter(customRole -> customRole.getMenus() != null)
                    .flatMap(customRole -> Arrays.stream(customRole.getMenus().split(",")))
                    .map(String::trim)
                    .anyMatch(menu -> menu.equals(menuName));
        }
        if (CollectionUtils.isNotEmpty(customRoles) && BooleanUtils.isFalse(result)) {
            result = customRoles.stream()
                    .filter(customRole -> customRole.getMenus() != null)
                    .flatMap(customRole -> Arrays.stream(customRole.getMenus().split(",")))
                    .map(String::trim)
                    .anyMatch(menu -> menu.equals(menuName));
        }
        return result;
    }

    public boolean versionContains(String menuName) {
        TenantInfo tenantInfo = getTenantInfo();
        String configStr = tenantInfo.getTenantConfig();
        if (StringUtils.isNotEmpty(configStr)) {
            try {
                JSONArray jsonArray = JSON.parseObject(configStr).getJSONArray("menuList");
                menuName = menuName.trim();
                for (int i = 0; i < jsonArray.size(); ++i) {
                    if (jsonArray.getString(i).equals(menuName)) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean checkRole(String role) {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount user = userContextService.getLoginUser();

        if (user.getIsSuperAdmin()) {
            return true;
        } else if ("SuperAdmin".equals(role)) {
            return false;
        }

        if ("SiteAdmin".equals(role)) {
            return user.getIsSiteAdmin();
        }

        if ("GeOp".equals(role)) {
            return user.getIsGeOp();
        }

        if (user.getIsLocalAdmin()) {
            return true;
        } else if ("LocalAdmin".equals(role)) {
            return false;
        }
        return user.getRoleNames().contains(role);
    }

    public UserAccount getLoginUser() {
        if (userAccount == null) {
            String userName = getUserLoginName();
            if ("".equals(userName)) {
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                final Object attribute = request.getSession().getAttribute(CBoardController.LOGIN_NAME_KEY);
                userName = Optional.ofNullable(UserContext.getUsername(request)).orElse(String.valueOf(attribute));
                if(UserContext.ANONYMOUS_USER.equals(userName))
                    userName = null;
            }
            if (userName == null) {
                return null; // user is not logined
            }

            UserAccountRepository dao = WebUtil.getBean(UserAccountRepository.class);
            userAccount = dao.getByLoginName(userName);
        }
        return userAccount;
    }

    public void setAssetTypeGroup() {
        if (apmFeatures.contains("CONFIG_ASSET_TYPE_GROUP")) {
            userAccount.setEnableAssetTypeGroup(true);
        }
        AssetTypeGroupRepository assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        List<AssetTypeGroup> assetTypeGroupList = assetTypeGroupRepository.listAssetTypeGroupInHierarchyByUser(userAccount.getUid());
        Map<String, AssetTypeGroup> assetTypeGroupMap = assetTypeGroupList.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, t -> t));
        userAccount.setAssetTypeGroupList(assetTypeGroupList);
        userAccount.setAssetTypeGroupMap(assetTypeGroupMap);
    }

    public boolean isIBTenant() {
        UserAccount currentUser = getLoginUser();
        int siteId = currentUser.getSiteId();
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        if (siteId == 46 && (userContextService.hasRole("AssetStaff") || userContextService.hasRole("AssetHead"))) {
            return false;
        }
        return siteId == 46;
    }


    public boolean isAgentTenant() {
        if(flagIsAgentTenant!=null) return flagIsAgentTenant;
        tenant = getTenantInfo();
        flagIsAgentTenant = tenant.getType() != null && tenant.getType() == 1;

        return flagIsAgentTenant;
    }

    public boolean isUltraSoundTenant() {
        if (flagIsUltraSoundTenant != null) return flagIsUltraSoundTenant;
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getTenantConfig() != null && tenantInfo.getTenantConfig().contains("\"edition\":\"ultrasound\"");
    }

    public boolean isHospitalUser(){
        if(flagIsHospitalUser!=null) return flagIsHospitalUser;

        UserAccount loginUser = getLoginUser();
        //判断医院级别是否有调剂中心
        List<WorkflowConfig> list = WebUtil.getBean(WorkflowConfigRepository.class).findHospitalLevel(loginUser.getSiteUID());
        if(CollectionUtils.isNotEmpty(list)) {
            flagIsHospitalUser = Boolean.TRUE;
        } else {
            flagIsHospitalUser = Boolean.FALSE;
        }

        return flagIsHospitalUser;
    }

    public int getLoginUserId() {
        return Option.of(getLoginUser()).map(UserAccount::getId).getOrElse(-1);
    }

    public String getUserTenantType(){
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getType() != null ? tenantInfo.getType().toString() : "";
    }

    public String getLoginUserUid() {
        return Option.of(getLoginUser()).map(UserAccount::getUid).getOrElse("");
    }

    public String getLoginUserName() {
        return Option.of(getLoginUser()).map(UserAccount::getLoginName).getOrElse("");
    }

    public int getLoginUserSiteId() {
        return Option.of(getLoginUser()).map(UserAccount::getSiteId).getOrElse(-1);
    }

    public String getLoginUserSiteName() {
        return Option.of(getTenantInfo().getName()).getOrElse("");
    }

    public int getLoginUserHospitalId() {
        return Option.of(getLoginUser()).map(UserAccount::getHospitalId).getOrElse(-1);
    }

    public int getLoginUserOrgInfoId() {
        return Option.of(getLoginUser()).map(UserAccount::getOrgInfoId).getOrElse(-1);
    }

    public OrgInfo getLoginUserOrgInfo() {
        return Option.of(getLoginUser()).map(u -> WebUtil.getBean(OrgInfoRepository.class).findById(u.getOrgInfoId())).getOrElseThrow(() -> new IllegalStateException(String.format("unable to get orgInfo for user.orgInfoId = %s", userAccount.getOrgInfoId())));
    }

    public boolean getIsSuperAdmin() {
        return UserContextService.isSuperAdmin();
    }

    public boolean getIsTenantAdmin() {
        return UserContextService.isSiteAdmin();
    }

    public boolean getIsLocalAdmin() {
        return UserContextService.isLocalAdmin();
    }

    public boolean getIsGeOp() {
        return UserContextService.isGeOp();
    }

    public boolean getIsMVSAdmin(){
    	return UserContextService.isMVSAdmin();
    }

    public boolean getIsMVSRole(){
        return UserContextService.isMVSRole();
    }

    public static UserAccount getCurrentUserAccount() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.getLoginUser();
    }

    public static Set<String> getCurrentUserFunction() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.getFunctionListByFeatureAndRoles();
    }

    public static List<CustomRole> getCustomRole(Integer userId){
    	UserContextService userContextService = WebUtil.getBean(UserContextService.class);
    	return userContextService.getListCustomRole(userId);
    }

    public static List<SysRole> getSysRole(Integer userId){
    	UserContextService userContextService = WebUtil.getBean(UserContextService.class);
    	return userContextService.getListSysRole(userId);
    }

    public Set<String> getFeatureList() {
        if (CollectionUtils.isEmpty(apmFeatures)) {
            apmFeatures = ((List<String>)JSON.parse(getTenantInfo().getFeatureList())).stream().collect(Collectors.toSet());
            AdminFeatureActiveRepository featureActiveRepository = WebUtil.getBean(AdminFeatureActiveRepository.class);
            adminActive = new HashMap<>();
            UserAccount userAccount = Optional.ofNullable(getLoginUser()).orElse(new UserAccount());
            if (userAccount.getOrgLevel() >= 2) {
                if (!ObjectUtils.isEmpty(featureActiveRepository)) {
                    // 查找单独激活菜单
                    List<AdminFeatureActive> featureActives = featureActiveRepository.findByHospitalUID(userAccount.getHospitalUID());
                    featureActives.forEach(v -> adminActive.put(v.getFeatureKey(), Boolean.TRUE));
                }
            } else {
                Arrays.stream(ApmAdminActiveFeature.values()).filter(v -> apmFeatures.contains(v.name())).forEach(v -> adminActive.put(v.name(), Boolean.TRUE));
                adminActive.put(ApmAdminActiveFeature.DIGITAL_MEDICAL_ENGINEERING.name(), Boolean.TRUE);
            }
            CustomPermissionEvaluator.apmFeatures = apmFeatures;
        }
        // 设置资产分类
        setAssetTypeGroup();
        return apmFeatures;
    }

    public String isActive(String featureName) {
        if (null == adminActive) {
            return "(未激活)";
        }
        return adminActive.get(featureName) == null?"(未激活)":"";
    }

    public boolean canRender(String featureName) {
        if (null == adminActive) {
            return false;
        }
        return adminActive.get(featureName) == null?false:true;
    }

    public Set<ApmSystemRole> getApmSysRole() {
        if (CollectionUtils.isEmpty(apmSystemRoles)) {
            UserAccount user = getLoginUser();
            Set<String> roles = getSysRole(getUserID()).stream().map(SysRole::getName).collect(Collectors.toSet());
            apmSystemRoles = Arrays.stream(ApmSystemRole.values()).filter(r -> roles.contains(r.name())).collect(Collectors.toSet());
            addRoleIfTrue(user.getIsSuperAdmin(), ApmSystemRole.SuperAdmin);
            addRoleIfTrue(user.getIsSiteAdmin(), ApmSystemRole.SiteAdmin);
            addRoleIfTrue(BooleanUtils.isTrue(user.getIsGeOp()), ApmSystemRole.GeOp);
            addRoleIfTrue(user.getIsLocalAdmin(), ApmSystemRole.LocalAdmin);
            if (user.getMvsRoleType() != null) {
                addRoleIfTrue(user.getMvsRoleType() == 1 || user.getMvsRoleType() == 3, ApmSystemRole.MVSAdmin);
                addRoleIfTrue(user.getMvsRoleType() == 2, ApmSystemRole.MVSFE);
            }
            CustomPermissionEvaluator.apmSystemRoles = apmSystemRoles;
        }
        return apmSystemRoles;
    }

    private void addRoleIfTrue(boolean condition, ApmSystemRole role) {
        if (condition) {
            apmSystemRoles.add(role);
        }
    }

    public Set<String> getFunctionListByFeatureAndRoles() {
        if (CollectionUtils.isEmpty(apmFunctionList)) {
            apmFunctionList = new HashSet<>();
            List<CustomRole> customRoles = getCustomRole(getUserID());
            Set<ApmSystemRole> sysRoles = getApmSysRole();
            customRoles.forEach(customRole -> {
                if (StringUtils.isNotEmpty(customRole.getFunctionList())) {
                    apmFunctionList.addAll((List<String>)JSON.parse(customRole.getFunctionList()));
                }
            });
            Set<String> featureList = getFeatureList();
            List<ApmFeature> features = Arrays.asList(ApmFeature.values()).stream().filter(feature -> featureList.contains(feature.name())).collect(Collectors.toList());
            features.forEach(feature -> apmFunctionList.addAll(feature.getFunctionList().stream()
                    .filter(function -> sysRoles.stream().anyMatch(function.getRoleList()::contains) || CollectionUtils.isEmpty(function.getRoleList()))
                    .map(ApmFunction::name).collect(Collectors.toSet())));
            if (BooleanUtils.isTrue(getUserAccountApprove())) {
                apmFunctionList.add(ApmFunction.REPAIR_APPROVE_PROCESS_VIEW.name());
            }
            //租户额外特殊处理
            functionSpecial();

            CustomPermissionEvaluator.apmFunctionList = apmFunctionList;
        }
        return apmFunctionList;
    }

    private void functionSpecial() {
        if (needScrappedControl()) {
            List<CustomRole> customRoles = getCustomRole(getUserID());
            Set<String> functions = new HashSet<>();
            customRoles.forEach(customRole -> {
                if (StringUtils.isNotEmpty(customRole.getFunctionList())) {
                    functions.addAll((List<String>)JSON.parse(customRole.getFunctionList()));
                }
            });
            if (!functions.contains(ApmFunction.ASSET_SCRAP_VIEW.name())) {
                apmFunctionList.remove(ApmFunction.ASSET_SCRAP_VIEW.name());
            }
            if (!functions.contains(ApmFunction.ASSET_SCRAPED_VIEW.name())) {
                apmFunctionList.remove(ApmFunction.ASSET_SCRAPED_VIEW.name());
            }
        }
    }

    public static UserAuth getCustomRoleAuth(Integer userId, String authKey) {
        UserAuth customRoleAuth = new UserAuth();
        customRoleAuth.setAuthKey(authKey);

        List<CustomRole> listCustomRole = getCustomRole(userId);
        List<SysRole> listSysRole = getSysRole(userId);

        if (CollectionUtils.isNotEmpty(listSysRole)) {
            customRoleAuth.setAllAuth(authKey, true);
        } else if (StringUtils.isNotEmpty(authKey) && CollectionUtils.isNotEmpty(listCustomRole)) {
            Optional<JSONObject> jsonObject = listCustomRole.stream()
                    .map(CustomRole::getWebAuthorities)
                    .filter(StringUtils::isNotEmpty)
                    .map(JSON::parseArray)
                    .flatMap(Collection::stream)
                    .map(JSONObject.class::cast)
                    .filter(obj -> authKey.equals(obj.getString("k")))
                    .findFirst();

            jsonObject.ifPresent(jsonObj -> {
                customRoleAuth.setAuthCreate(jsonObj.getBooleanValue("a"));
                customRoleAuth.setAuthDelete(jsonObj.getBooleanValue("d"));
                customRoleAuth.setAuthModify(jsonObj.getBooleanValue("m"));
                customRoleAuth.setAuthSearch(jsonObj.getBooleanValue("s"));
                customRoleAuth.setAuthExport(jsonObj.getBooleanValue("o"));
                customRoleAuth.setAuthImport(jsonObj.getBooleanValue("i"));
            });

        } else {
            customRoleAuth.setAllAuth(authKey, true);
        }

        return customRoleAuth;
    }

    public static boolean isSuperAdmin() {
        UserAccount userAccount = getCurrentUserAccount();
        return userAccount.getIsSuperAdmin();
    }

    public static boolean isSiteAdmin() {
        UserAccount userAccount = getCurrentUserAccount();
        return userAccount.getIsSiteAdmin();
    }

    public static boolean isLocalAdmin() {
        UserAccount userAccount = getCurrentUserAccount();
        return userAccount.getIsLocalAdmin();
    }

    public static boolean isGeOp() {
        UserAccount userAccount = getCurrentUserAccount();
        return userAccount.getIsGeOp();
    }

    public static boolean isMVSAdmin() {
        UserAccount userAccount = getCurrentUserAccount();
        Integer mvsRoleType = userAccount.getMvsRoleType();
        return mvsRoleType != null && (mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN_2);
    }

    public static boolean isMVSRole(){
        UserAccount userAccount = getCurrentUserAccount();
        Integer mvsRoleType = userAccount.getMvsRoleType();
        return mvsRoleType != null && (mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_FE || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN_2);
    }

    public static int getUserID() {
        UserAccount userAccount = getCurrentUserAccount();
        if (userAccount != null) {
            return userAccount.getId();
        }

        return -1;
    }

    public static int getSiteId() {
        UserAccount userAccount = getCurrentUserAccount();
        if (userAccount != null) {
            return userAccount.getSiteId();
        }

        return -1;
    }

    public static int getHospitalId() {
        UserAccount userAccount = getCurrentUserAccount();
        if (userAccount != null) {
            return userAccount.getHospitalId();
        }

        return 0;
    }

    public static void setSiteFilter(List<SearchFilter> filters) {
        filters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getSiteId()));
    }

    public static void setHospitalFilter(List<SearchFilter> filters) {
        filters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
    }

    public String getUserDefaultHomePage() {
        this.getLoginUser();
        if (userAccount == null) {
            return "/login.xhtml";
        } else {
            if(isPartnerAccount())
                return "/admin/uaa/siteInfo/List.xhtml";

            if(isIBorUltraSoundIBEndUser())
                return "portal/profile.xhtml";

            UaaService uaaService = WebUtil.getBean(UaaService.class);
            return uaaService.getUserDefaultHomePage(userAccount);
        }
    }
    public static String getAccessToken() {
        return getAccessToken(null);
    }

    public static String getAccessToken(HttpServletRequest request) {
        String token = null;
        if (request == null) {
            try {
                request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            } catch (Exception ex) {
                logger.error("method getAccessToken get current request fail");
            }
        }
        if (request != null) {
            token = (String) request.getSession().getAttribute("ACCESS_TOKEN");
        }
        return token;
    }

    public String getSystemInMaintainConfig() {
        ApmConfigInfo info = WebUtil.getBean(ApmConfigInfoRepository.class).findByItem("system_maintain");
        if (info != null && info.getExpireDate() != null && (new Date()).after(info.getExpireDate())) {
            info = null;
        }
        if (info != null && "1".equals(info.getValue())) {
            try {
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect("/wx/mrr?message=" + URLEncoder.encode(info.getMessage(), "UTF-8"));
            } catch (IOException e) {
                logger.error("method getSystemInMaintainConfig is error message:{}", e.getMessage());
            }
        }
        return "0";
    }

    public String getSystemInMaintainConfigMessage() {
        ApmConfigInfo info = WebUtil.getBean(ApmConfigInfoRepository.class).findByItem("system_maintain");
        if (info == null || info.getExpireDate() != null && (new Date()).after(info.getExpireDate())) {
            return null;
        } else {
            return info.getMessage();
        }
    }

    private Boolean isDispatcher = null;
    private Boolean isPmDispatcher = null;
    private Boolean isMeasuringDispatcher = null;
    private Boolean isAssetApprover = null;
    private Boolean needAssetApproval = null;
    private Boolean needInstorageCheck = null;

    public Boolean needInstorageCheck(){
        if (needInstorageCheck == null) {
            getUserPrivilege();
        }
        return needInstorageCheck;
    }

    public Boolean needAssetApproval(){
        if (needAssetApproval == null) {
            getUserPrivilege();
        }
        return needAssetApproval;
    }

    public Boolean isDispatcher() {
        if (isDispatcher == null) {
            getUserPrivilege();
        }
        return isDispatcher;
    }


    public Boolean isPmDispatcher(){
        if (isPmDispatcher == null) {
            getUserPrivilege();
        }
        return isPmDispatcher;

    }
    public Boolean isMeasuringDispatcher(){
        if (isMeasuringDispatcher == null) {
            getUserPrivilege();
        }
        return isMeasuringDispatcher;

    }

    public Boolean isAssetApprover() {
        if (isAssetApprover == null) {
            getUserPrivilege();
        }
        return isAssetApprover;
    }

    private void getUserPrivilege() {
        List<WorkflowConfig> wcList = getSiteConfigurations();
        isDispatcher = wcList.stream().anyMatch(item -> Optional.ofNullable(item. getDispatchUserId()).orElse(0).equals(userAccount.getId())
                || Optional.ofNullable(item.getDispatchUserId2()).orElse(0).equals(userAccount.getId()));

        isPmDispatcher = wcList.stream().anyMatch(item -> Optional.ofNullable(item.getPmDispatchUserId()).orElse(0).equals(userAccount.getId())
                || Optional.ofNullable(item.getPmDispatchUserId2()).orElse(0).equals(userAccount.getId()));

        isMeasuringDispatcher = wcList.stream().anyMatch(item -> Optional.ofNullable(item.getMeasureDispatchUserId()).orElse(0).equals(userAccount.getId())
                || Optional.ofNullable(item.getMeasureDispatchUserId2()).orElse(0).equals(userAccount.getId()));

        isAssetApprover = wcList.stream().anyMatch(item -> Optional.ofNullable(item.getAssetApprover()).orElse("").equals(userAccount.getUid()));

        needAssetApproval = wcList.stream().anyMatch(item -> !Strings.isNullOrEmpty(item.getAssetApprover()));

        needInstorageCheck = wcList.stream().anyMatch(item -> null!=item.getNeedInstorageCheck() && item.getNeedInstorageCheck());
    }

    private List<WorkflowConfig> getSiteConfigurations() {
        long l = System.currentTimeMillis();
        logger.info("getSiteConfigurations ：start {}", l);
        List<String> orgList = new ArrayList<>();
        orgList.add(userAccount.getTenantUID());
        orgList.add(userAccount.getInstitutionUID());
        orgList.add(userAccount.getHospitalUID());
        orgList.add(userAccount.getSiteUID());
        List<WorkflowConfig> bySiteUIDList1 = getWWorkflowConfig(orgList, userAccount);
        logger.info("getSiteConfigurations ：end {}", System.currentTimeMillis() - l);
        return bySiteUIDList1;
    }

    private List<WorkflowConfig> getWWorkflowConfig(List<String> orgList,UserAccount user){
        WorkflowConfigRepository wcDao = WebUtil.getBean(WorkflowConfigRepository.class);
        List<WorkflowConfig> bySiteUIDList = wcDao.getBySiteUIDList(orgList);
        if(!org.springframework.util.CollectionUtils.isEmpty(bySiteUIDList)){
            return bySiteUIDList;
        }
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        List<OrgInfo> byUser = orgDao.getByUser(user.getUid());
        if(org.springframework.util.CollectionUtils.isEmpty(byUser)){
            return new ArrayList<>();
        }
        List<String> collect = byUser.stream().filter(item -> item != null && item.getOrgType() != 4).map(OrgInfo::getUid).collect(Collectors.toList());
        if(!org.springframework.util.CollectionUtils.isEmpty(collect)){
            return wcDao.findWorkflowConfig(collect);
        }
        List<String> collectType4 = byUser.stream().filter(item -> item != null && item.getOrgType() == 4).map(OrgInfo::getUid).collect(Collectors.toList());
        if(!org.springframework.util.CollectionUtils.isEmpty(collectType4)){
            return  wcDao.findWorkflowConfig(collectType4);
        }
        return new ArrayList<>();
    }
    public String isActive(){
        UserAccount currentUser = getLoginUser();
        if((!currentUser.getIsActive() && currentUser.getId() != 0)){
            return "1";//need active user
        } else {
            return "0";
        }
    }

    public String isActiveNew(){
        UserAccount currentUser = getLoginUser();
        // 忽略智康宝
        if (isZkbAccount()) {
            return "0";
        }
        if(currentUser.getOrgLevel() != null && currentUser.getOrgLevel() >= 2 && !canRender(ApmAdminActiveFeature.DIGITAL_MEDICAL_ENGINEERING.name())) {
            return "1";//need active DIGITAL_MEDICAL_ENGINEERING
        } else {
            return "0";
        }
    }

    public void afterActiveDigital() {
        adminActive.put(ApmAdminActiveFeature.DIGITAL_MEDICAL_ENGINEERING.name(), Boolean.TRUE);
    }

    /* 
     Full access for SuperAdmin, GE user(has GE SSO number), GE Ops.
     SiteAdmin: only External WorkOrder and SR Report submission
     Hopital end user's access to modules by different Versions:
        Account Version: Full function except for External Work Order
        Dealer version:  AssetInfo + WorkOrder ( Internal & External )
        UltraSound IB versoin: AssetInfo + External WorkOrder
        IB version: Only External WorkOrder
    */
    public boolean isGeSsoUser(){
        UserAccount currentUser = getLoginUser();
        return currentUser.getGeSso()!=null;
    }

    public boolean isGeOpsUser(){
        UserAccount currentUser = getLoginUser();
        return Boolean.TRUE.equals(currentUser.getIsGeOp());
    }

    public boolean isOpsOrSiteAdminUser(){
        UserAccount currentUser = getLoginUser();
        return Boolean.TRUE.equals(currentUser.getIsGeOp()) || currentUser.getIsSiteAdmin();
    }

    public boolean isIBEndUser(){
        if(isOpsOrSiteAdminUser()) return false;

        return isIBTenant();
    }

    public boolean isUltraSoundIBEndUser(){
        if(isOpsOrSiteAdminUser()) return false;

        return isUltraSoundTenant();
    }

    public boolean isIBorUltraSoundIBEndUser(){
        return isIBEndUser() || isUltraSoundIBEndUser();
    }

    public boolean isDealerEndUser(){
        if(isOpsOrSiteAdminUser()) return false;

        return isAgentTenant();
    }

    public boolean isAccountEndUser() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount currentUser = getLoginUser();
        int siteId = currentUser.getSiteId();
        /* 如果ib是46的话，工程师AssetStaff和医工主任AssetHead可以看到3个tab，其它人不能看到内部保养*/
        if (siteId == 46 && (userContextService.hasRole("AssetStaff") || userContextService.hasRole("AssetHead"))) {
            return false;
        }
        if (isOpsOrSiteAdminUser() || isDealerEndUser() || isIBEndUser()) {
            return false;
        }
        return !isUltraSoundIBEndUser();
    }

    public boolean isAccessMyTrain() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount currentUser = getLoginUser();
        TenantInfo currentTenantInfo = userContextService.getCurrentUserTenantInfo();
        List<String> userName = WebUtil.getBean(UserAccountRepository.class).findUsname(getCurrentUserAccount().getId());
        for (String s : userName) {
            if (s.equals("OpWorker")) {
                return false;
            }
        }
        int siteId = currentUser.getSiteId();
        return siteId == 46 || BooleanUtils.isTrue(currentTenantInfo.getHaveIbDevice());
    }


    public boolean isHaveIbDeviceUser() {
        return tenant.getHaveIbDevice() == null ? Boolean.FALSE : tenant.getHaveIbDevice();
    }
    /**
     * this api is to check whether login user is for lixiang company.
     * @return
     */
    public boolean isPartnerAccount() {
        UserAccount user = getLoginUser();
        if (null == user.getType()) {
            return false;
        }
        return ApmConstant.USER_ACCOUNT_TYPE_PARTNER == user.getType() && user.getIsSuperAdmin();

    }
    public boolean isPartnerAccount46() {
        UserAccount user = getLoginUser();
        if (null == user.getSiteId()) {
            return false;
        }
        return ApmConstant.IB_TENANT_ID == user.getSiteId();
    }

    public boolean isOpenNewMenuRoles() {
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getEnablesNewMenu() && StringUtils.isNotEmpty(tenantInfo.getFeatureList());
    }

    public boolean isZkbAccount() {
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getType() != null && tenantInfo.getType() == 3;
    }

    public boolean isGeAdmin() {
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfig = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class).findByLoginNameAndConfigType(getLoginUser().getLoginName(), "ClosePending");
        return CollectionUtils.isNotEmpty(zkbPartOrderUserConfig);
    }

    public boolean isEditCourseScheduleAdmin() {
        List<ZkbCourseTrainUserConfig> zkbCourseTrainUserConfigList = WebUtil.getBean(ZkbCourseTrainUserConfigRepository.class).findByLoginName(getLoginUser().getLoginName());
        return CollectionUtils.isNotEmpty(zkbCourseTrainUserConfigList);
    }

    public boolean isZkbFeUserConfig(String configType) {
        List<ZkbFePartOrderUserConfig> zkbFePartOrderUserConfig = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class).findByLoginNameAndConfigType(getLoginUser().getLoginName(), configType);
        return CollectionUtils.isNotEmpty(zkbFePartOrderUserConfig);
    }

    public boolean isZkbConfigAdmin() {
        UserAccount user = getLoginUser();
        if (StringUtils.isBlank(user.getLoginName()))
            return false;
        return user.getLoginName().contains("zkbConfigAdmin");
    }

    public boolean isZtbTenant() {
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getType() != null && tenantInfo.getType() == 4;
    }

    public boolean isMvsTenant() {
        TenantInfo tenantInfo = getTenantInfo();
        return ApmConstant.MVS_TENANT_NAME.equals(tenantInfo.getName());
    }

    public boolean isSupportMultiLanguage(){
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getIsSupportMultiLanguage() != null && tenantInfo.getIsSupportMultiLanguage() == Boolean.TRUE;
    }

    public boolean isYiMaiYangGuangTenant() {
        return accessContainsApmFeature("MVS_WORK_ORDER");
    }

//    public boolean isYiMaiYangGuangTenant() {
//        if(synchronizeWorkOrder == null){
//            synchronizeWorkOrder = Boolean.FALSE;
//            String value = getConfigItemValue( "synchronizeWorkOrder");
//            synchronizeWorkOrder=Boolean.TRUE.toString().equalsIgnoreCase(value);
//        }
//        return synchronizeWorkOrder;
//    }

    public static boolean isYiMaiYangGuangTenantForServer() {
        return WebUtil.getBean(UserContextService.class).isYiMaiYangGuangTenant();
    }

    public static boolean isSuperAdminWithPartnerAccount(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.isPartnerAccount();
    }

    public static boolean isOnlyLocalITAdmin() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount user = userContextService.getLoginUser();
        List<String> userRoles = user.getRoleNames();
        if (userRoles.contains(ApmSystemRole.LocalAdmin.name())) {
            return !(userRoles.contains(ApmSystemRole.ITAdmin.name()) || isSuperAdmin());
        } else {
            return false;
        }
    }

    public static boolean isInsulate() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        TenantInfo tenantInfo = userContextService.getTenantInfo();
        return tenantInfo.getInsulate() != null && tenantInfo.getInsulate() && !userContextService.hasAnyRole(ADMIN);
    }

    public static String decryptData(String data){
        try {
            String [] secondDecryptionArray = data.split("");
            String [] tempSecondDecryptionArray = new String[secondDecryptionArray.length];
            for (int i = 0 ; i < secondDecryptionArray.length ; i++) {
                if(i % 2 == 0){
                    tempSecondDecryptionArray[i] = secondDecryptionArray[i + 1];
                }else{
                    tempSecondDecryptionArray[i] = secondDecryptionArray[i - 1];
                }
            }
            StringBuilder secondDecryptionArrayStringBuffer = new StringBuilder();
            for (String tempData : tempSecondDecryptionArray) {
                secondDecryptionArrayStringBuffer.append(tempData);
            }
            String firstDecryptionData = B64Code.decode(secondDecryptionArrayStringBuffer.toString(), "utf-8");

            String [] firstDecryptionArray = firstDecryptionData.split("");
            String tempFistData = firstDecryptionArray[firstDecryptionArray.length - 1];
            firstDecryptionArray[firstDecryptionArray.length - 1] = firstDecryptionArray[0];
            firstDecryptionArray[0] = tempFistData;
            String [] tempFirstDecryptionArray = new String[firstDecryptionArray.length];
            for (int i = 0 ; i < firstDecryptionArray.length ; i++) {
                if(i % 2 == 0){
                    tempFirstDecryptionArray[i] = firstDecryptionArray[i + 1];
                }else{
                    tempFirstDecryptionArray[i] = firstDecryptionArray[i - 1];
                }
            }
            StringBuilder firstDecryptionArrayStringBuffer = new StringBuilder();
            for (String tempData : tempFirstDecryptionArray) {
                firstDecryptionArrayStringBuffer.append(tempData);
            }
            return B64Code.decode(firstDecryptionArrayStringBuffer.toString(), "utf-8");
        }catch (Exception ex){
            return null;
        }
    }

    public TenantInfo getCurrentUserTenantInfo(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.getTenantInfo();
    }

    //返回当前用户所在时区
    public String getTimeZone(){
        Double currentTimeZone = getCurrentUserTenantInfo().getTimeZone();
        Integer timeZone = currentTimeZone == null ? 8 : currentTimeZone.intValue();
        if(timeZone >= 0){
            return "GMT+"+timeZone;
        }else{
            return "GMT"+timeZone;
        }
    }


    /***
     *  是否是中国 维修工作台在非中文状态下不显示外部工单和外部远程工单
     */
    public boolean isLocale(){
        Locale locale = LocaleHolder.getLocale();
        return "zh".equalsIgnoreCase(locale.getLanguage());
    }

    public String timeFormat(){
        if(!isLocale()){
            return "MM/dd/yyyy";
        }else{
            return "yyyy/MM/dd";
        }
    }

    public String timeFormat8(){
        if(!isLocale()){
            return "MM-dd-yyyy";
        }else{
            return "yyyy-MM-dd";
        }
    }

    public String timeFormat12(){
        if(!isLocale()){
            return "MM/dd/yyyy HH:mm";
        }else{
            return "yyyy/MM/dd HH:mm";
        }
    }

    public String timeFormat14(){
        if(!isLocale()){
            return "MM/dd/yyyy HH:mm:ss";
        }else{
            return "yyyy/MM/dd HH:mm:ss";
        }
    }

    public String getCalendarLocale(){
        return Locale.CHINESE.getLanguage();
    }

    public boolean isZkbUser(){
        TenantInfo currentTenantInfo = this.getCurrentUserTenantInfo();

        if(currentTenantInfo.getType() == null || currentTenantInfo.getTestStatus() == null){
            return Boolean.FALSE;
        }

        if(currentTenantInfo.getType().equals(3) && !currentTenantInfo.getTestStatus().equals(-1)){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public boolean isZkbAdmin(){
        TenantInfo currentTenantInfo = this.getCurrentUserTenantInfo();

        if(currentTenantInfo.getType() == null || currentTenantInfo.getTestStatus() == null){
            return Boolean.FALSE;
        }

        if(currentTenantInfo.getType().equals(3) && currentTenantInfo.getTestStatus().equals(-1)){
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public boolean isNinth(){
        TenantInfo currentTenantInfo = this.getCurrentUserTenantInfo();
        UserAccount user = getLoginUser();
        List<String> userRoles = user.getRoleNames();
        if(currentTenantInfo.getName().contains("九") || currentTenantInfo.getId() == 112){
            if (ninthHospitalNineJudgment(userRoles)) return Boolean.TRUE;
            return Boolean.FALSE;
        }

        return Boolean.FALSE;
    }

    private boolean ninthHospitalNineJudgment(List<String> userRoles) {
        for (int i = 0; i < userRoles.size(); i++) {
            if(CustomConfigConstant.ninthHospitalNineAuthority().contains(userRoles.get(i))){
                return true;
            }
        }
        return false;
    }

    public static boolean isPartsAdmin(){
        UserAccount userAccount = getCurrentUserAccount();
        List<String> roleNames = userAccount.getRoleNames();
        return roleNames.contains(SimplePartsConstant.PARTS_ADMIN_MANAGER);
    }

    public boolean hasScrappedMenuAuthority() {
        UserRoleRepository userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        CustomRoleRepository customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        List<UserRole> userRoleList = userRoleDao.findByUserIdAndRoleType(UserContextService.getCurrentUserAccount().getId(), ApmConstant.CUSTOME_ROLE_TYPE);
        for (UserRole userRole : userRoleList) {
            String menus = customRoleRepository.findById(userRole.getRoleId()).getMenus();
            if(null != menus && menus.contains("asset.scrap")){
                return true;
            }
        }
        return false;
    }

    public boolean needScrappedControl(){
        ConfigService cfgService = WebUtil.getBean(ConfigService.class);
        return cfgService.needScrappedControl();
    }

    public boolean useNewRiskGrade(){
        ConfigService cfgService = WebUtil.getBean(ConfigService.class);
        return cfgService.useNewRiskGrade();
    }

    public boolean hasScrappedAuthority(){
        AssetCreateService acService = WebUtil.getBean(AssetCreateService.class);
        return acService.hasScrappedAuthority();
    }

    public static boolean isAllowAssetBatchUpdate() {
        ConfigCacheService configCacheService = WebUtil.getBean(ConfigCacheService.class);
        TenantOptionConfig tenantOptionConfig = configCacheService.getTenantOptionConfigByUid(UserContextService.getCurrentUserAccount().getTenantUID());
        return tenantOptionConfig.getAllowAssetBatchUpdate();

    }

    /**
     * 验证租户名称
     * @param tenantName 非必填，如不填将验证方法中指定的租户是否为当前登录用户所属租户
     * @return
     */
    public static Boolean hasCurrentTenantName(String tenantName){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        TenantInfo currentTenantInfo = userContextService.getTenantInfo();
        if(currentTenantInfo == null){
            return Boolean.FALSE;
        }
        String name = currentTenantInfo.getName().trim();
        if(StringUtils.isBlank(tenantName)){
            if("云南省阜外心血管病医院".equals(name)){
                return Boolean.TRUE;
            }
        }else{
            if(tenantName.equals(name)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 判断租户是否有激活供应商的权限
     * @return
     */
    public Boolean isActiveSupplier(){
        TenantOptionConfig tenantOptionConfigOptional = WebUtil.getBean(ConfigCacheService.class).getTenantOptionConfigByUid(getCurrentUserTenantInfo().getUid());
        return tenantOptionConfigOptional.getActiveSupplier();
    }

    /**
     * 判断租户是否启用物资接口
     * @return
     */
    public boolean enableMaterialsInterface(){
        if(enableMaterialsInterfaceFlag == null){
            enableMaterialsInterfaceFlag = Boolean.FALSE;
            UserAccount user = getCurrentUserAccount();
            String enableMaterialsInterface = WebUtil.getBean(ConfigItemValueRepository.class).findByKeyAndSiteForUseParent(user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID(), "enableMaterialsInterface");
            enableMaterialsInterfaceFlag=Boolean.TRUE.toString().equalsIgnoreCase(enableMaterialsInterface);
        }

        return enableMaterialsInterfaceFlag;
    }

    /**
     *  资产分类是否关联设备分类
     * @return
     */
    public boolean enableAssetGroupAssetTypeGroupAssociation(){
        if(enableAssetGroupAssetTypeGroupAssociation == null){
            enableAssetGroupAssetTypeGroupAssociation = Boolean.FALSE;
            String enableAssetGroup = getConfigItemValue( "enableAssetGroupAssetTypeGroupAssociation");
            enableAssetGroupAssetTypeGroupAssociation=Boolean.TRUE.toString().equalsIgnoreCase(enableAssetGroup);
        }

        return enableAssetGroupAssetTypeGroupAssociation;
    }
    /**
     *  核心组审核
     * @return
     */
    public boolean enableAuditGroupConfiguration() {
        if (enableAuditGroupConfiguration == null) {
            enableAuditGroupConfiguration = Boolean.FALSE;
            String enableAssetGroup = getConfigItemValue("enableAuditGroupConfiguration");
            enableAuditGroupConfiguration = Boolean.TRUE.toString().equalsIgnoreCase(enableAssetGroup);
        }

        return enableAuditGroupConfiguration;
    }
    public boolean enableDisplayExternalWorkOrder(){
        String displayExternalWorkOrder = getConfigItemValue("displayExternalWorkOrder");
        return Boolean.TRUE.toString().equalsIgnoreCase(displayExternalWorkOrder);
    }
    public boolean enableDisplayRemoteWorkOrder(){
        String displayRemoteWorkOrder = getConfigItemValue("displayRemoteWorkOrder");
        return Boolean.TRUE.toString().equalsIgnoreCase(displayRemoteWorkOrder);
    }

    public boolean enableDisplayByKey(String key){
        String display = getConfigItemValue(key);
        return Boolean.TRUE.toString().equalsIgnoreCase(display);
    }
        /**
         *  通过key获取指定的配置
         * @param key
         * @return
         */
    public String getConfigItemValue(String key){
            UserAccount user = getCurrentUserAccount();
            String value = WebUtil.getBean(ConfigItemValueRepository.class).findByKeyAndSiteForUseParent(user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID(), key);
            return value;
    }
    public boolean activeSupplier(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        Set<String> feature = userContextService.getFeatureList();
        if (feature.contains(ApmFeature.ASSET_SUPPLIER_APPLICATION.name())) {
            return userContextService.canRender("ASSET_SUPPLIER_APPLICATION");
        }
        return false;
    }
    public List<RoleMsgRel> getRoleMsgInfos(Integer userId) {
        RoleMsgRelRepository roleMsgRelRepository = WebUtil.getBean(RoleMsgRelRepository.class);
        return roleMsgRelRepository.getRoleMsgRels(userId);
    }

    public List<RoleMsgRel> getRoleMsgInfosByRoleName(String roleName) {
        RoleMsgRelRepository roleMsgRelRepository = WebUtil.getBean(RoleMsgRelRepository.class);
        return roleMsgRelRepository.getRoleMsgByRoleName(roleName);
    }

    public I18nMessage getByMsgTypeAndMsgKey(String msgType, String msgKey) {
        I18nMessageRepository msgRepo = WebUtil.getBean(I18nMessageRepository.class);
        return msgRepo.getByMsgTypeAndMsgKey(msgType, msgKey);
    }

    public boolean isCountyArea(){
        TenantInfo tenantInfo = getTenantInfo();
        return tenantInfo.getType() != null && tenantInfo.getType() == 6;
    }

    public void updateSiteUid(){
        if(userAccount == null)return;
        if(StringUtils.isEmpty(selectedSiteUid)){
            userAccount.setSiteUID(userAccount.getHospitalUID());
        }else{
            userAccount.setSiteUID(selectedSiteUid);
        }
    }
    public String getSelectedSiteUid() {
        return selectedSiteUid;
    }

    public void setSelectedSiteUid(String selectedSiteUid) {
        this.selectedSiteUid = selectedSiteUid;
    }

    public List<OrgInfo> getSiteOrgInfoChild() {
        if (CollectionUtils.isNotEmpty(child)) return child;
        String hospitalUID = userAccount.getHospitalUID();
        if (userAccount.getOrgLevel() == 2 && hospitalUID.equals(userAccount.getSiteUID())) {
            WorkflowConfigRepository workflowConfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
            OrgInfoRepository orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
            child = orgInfoRepository.getHospitalByTenantUIDAndType(hospitalUID);
            if (CollectionUtils.isNotEmpty(child)) {
                child = filterChildOrgInfo(child, workflowConfigDao);
                if (CollectionUtils.isNotEmpty(child)) {
                    selectSite = getSelectSite(hospitalUID, workflowConfigDao, orgInfoRepository, child);
                }
            }
        }
        return child;
    }

    private List<OrgInfo> filterChildOrgInfo(List<OrgInfo> orgInfoList, WorkflowConfigRepository workflowConfigDao) {
        return orgInfoList.stream().filter(item -> {
            if (item == null) return false;
            Integer orgLevel = item.getOrgLevel();
            Integer orgType = item.getOrgType();
            return orgLevel != null && orgType != null && orgLevel == 3 && orgType == 3;
        }).filter(item -> {
            List<WorkflowConfig> siteWorkflowConfigList = workflowConfigDao.findBySiteUID(item.getUid());
            if (CollectionUtils.isEmpty(siteWorkflowConfigList)) {
                return false;
            }
            WorkflowConfig workflowConfig = siteWorkflowConfigList.get(0);
            return workflowConfig != null && workflowConfig.getEnableHospitalAdjustment() != null && workflowConfig.getEnableHospitalAdjustment();
        }).collect(Collectors.toList());
    }

    private OrgInfo getSelectSite(String hospitalUID, WorkflowConfigRepository workflowConfigDao, OrgInfoRepository orgInfoRepository, List<OrgInfo> orgInfoList) {
        OrgInfo info;
        List<WorkflowConfig> siteWorkflowConfigList = workflowConfigDao.findBySiteUID(hospitalUID);
        if (CollectionUtils.isNotEmpty(siteWorkflowConfigList) && StringUtils.isNotEmpty(siteWorkflowConfigList.get(0).getDeptForSharedAsset())) {
            OrgInfo orgInfo = orgInfoRepository.getByUid(hospitalUID);
            info = orgInfo;
            orgInfoList.add(orgInfo);
        } else {
            info = orgInfoList.get(0);
        }
        return info;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }
    public TreeNode getHospitalTree(){
        List<OrgInfo> orgList = getSiteOrgInfoChild();
        if(org.springframework.util.CollectionUtils.isEmpty(orgList)){
            return null;
        }
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
        expandedByOrgType(rootNode, 2);
        return rootNode;
    }
    private void expandedByOrgType(TreeNode tree, Integer type) {
        if (tree.getData() != null) {
            OrgInfo node = (OrgInfo) tree.getData();
            if (node.getOrgType() > type) {
                return;
            }
        }
        tree.setExpanded(true);
        for (TreeNode children : tree.getChildren()) {
            expandedByOrgType(children, type);
        }

    }
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }
        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }
    }
    public List<SelectItem> getOrgInfoNode(){
        List<SelectItem> selectItems=new ArrayList<>();
        if(selectSite != null){
            List<OrgInfo> orgInfos = WebUtil.getBean(OrgInfoRepository.class).getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(selectSite.getUid());
            if(!org.springframework.util.CollectionUtils.isEmpty(orgInfos)){
                selectItems = orgInfos.stream().map(item -> new SelectItem(item.getUid(), item.getName(), "", item.getOrgType() != 4)).collect(Collectors.toList());
            }
        }
        return selectItems;
    }
    // 当前用户只有指定角色的情况
    public boolean onlyHasRole(String roles){
        UserAccount user = getLoginUser();
        List<String> userRoles = user.getRoleNames();
        String[] roleList = roles.split(",");

        if(roleList.length < userRoles.size()){
            return false;
        }

        int count = 0;
        for (String role : roleList) {
            role = role.trim();
            boolean roleAllowed = userRoles.contains(role);
            if (roleAllowed) {
                count++;
            }
        }
        // 指定的角色比拥有的角色多，则判断用户的所有角色是否都在指定角色列表中
        return (roleList.length == userRoles.size() && count == roleList.length) || (roleList.length > userRoles.size() && count == userRoles.size());
    }

    public static Set<ApmFeature> getInitFeature() {
        return Arrays.stream(ApmFeature.values()).filter(ApmFeature::isNecessary).collect(Collectors.toSet());
    }

    public static Set<ApmFunction> getInitFunction() {
        Set<ApmFunction> apmFunctions = new HashSet<>();
        getInitFeature().forEach(f -> apmFunctions.addAll(f.getFunctionList()));
        return apmFunctions;
    }

    public boolean isEnableBtnByKey(String key) {
        Boolean result = enableBtnByKey.get(key);
        if (enableBtnByKey.get(key) == null) {
            ConfigItemService configItemService = WebUtil.getBean(ConfigItemService.class);
            result = BooleanUtils.isFalse(configItemService.getConfigItemByKey(key, getLoginUser()));
            enableBtnByKey.put(key, result);
        }
        return result;
    }

    public OrgInfo getSelectSiteSchedule() {
        return selectSiteSchedule;
    }

    public void setSelectSiteSchedule(OrgInfo selectSiteSchedule) {
        this.selectSiteSchedule = selectSiteSchedule;
    }

    public int getPageSizeCache(String key) {
        return pageSizeCache.computeIfAbsent(key,key1->20);
    }

    public static void setPageSizeCache(String key,Integer pageSize){
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        bean.pageSizeCache.put(key,pageSize);
    }




}
