package com.ge.apm.view.sysutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import javaslang.control.Option;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.B64Code;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.context.LocaleHolder;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.faces.application.ConfigurableNavigationHandler;
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



    private static final long serialVersionUID = 1L;
    public static final String ADMIN = "SuperAdmin,ITAdmin";
    public static final Integer INTERNATIONAL_VERSION = 1;
    public static List<String> internationalList= Lists.newArrayList("asset","repair","maintain","config");


    private TenantInfo tenant = null;
    private UserAccount userAccount = null;
    private List<CustomRole> listCustomRole=null;
    private List<SysRole> listSysRole=null;
    private Boolean activeSupplierFlag = null;
    private String selectedSiteUid;
    //选定的院区
    private OrgInfo selectSite;
    private List<OrgInfo> child = new ArrayList<>();
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
        userContextService.getTenantInfo();
        userContextService.getListCustomRole(user.getId());
        userContextService.getListSysRole(user.getId());
    }

    public void processAfterLogin() {
        getLoginUser();
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

    private boolean isFirstRequestAfterLogin = true;

    public boolean isIsFirstRequestAfterLogin() {
        return isFirstRequestAfterLogin;
    }

    public void checkIsSuperAdminAndFirstRequest() {
        if (!getLoginUser().getIsSuperAdmin()) {
            return;
        }

        if (!isFirstRequestAfterLogin) {
            return;
        }
        isFirstRequestAfterLogin = false;

        //navigate to super admin's home page
        FacesContext context = FacesContext.getCurrentInstance();
        ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        handler.performNavigation("admin/adminHome.xhtml?faces-redirect=true");
    }

    public static List<String> getRoles() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount user = userContextService.getLoginUser();

        return user.getRoleNames();
    }

    public boolean isOnlyAssetStaff(){
        List<String> roles = getRoles();
        if(roles.size()==1){
            if(roles.get(0).toString().equalsIgnoreCase("AssetStaff")){
                return true;
            }else{
                return false;
            }

        }else{
            return false;
        }

    }

    public boolean hasRole(String role) {
        return UserContextService.checkRole(role);
    }

    //add MVSAdmin,MVSFE
    public boolean hasAnyRole(String roleList) {
        UserAccount user = getLoginUser();
        Integer userId = user.getId();
        if(roleList.contains("SuperAdmin") && user.getIsSuperAdmin()){
            return true;
        }
        if(roleList.contains("SiteAdmin") && user.getIsSiteAdmin()){
            return true;
        }
        List<String> userRoles = user.getRoleNames();
        String[] roles = roleList.split(",");
        for (String role : roles) {
            role = role.trim();
            boolean roleAllowed = userRoles.contains(role);
            if (roleAllowed) {
                return true;
            }
        }
        //增加自定义角色判断
        CustomRoleRepository customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        List<CustomRole> customRoles = customRoleRepository.findCustomRolesByUserId(userId);
        List<String> names = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(customRoles)){
            for (CustomRole customRole : customRoles) {
                if(StringUtils.isNotEmpty(customRole.getGroups())){
                    names.addAll(Arrays.asList(customRole.getGroups().split(",")));
                }
            }
            names = names.stream().distinct().collect(Collectors.toList());
            for (String role : roles) {
                if(names.contains(role.trim())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前登录是否科室级用户
     * @return boolean
     */
    public static boolean isDeparmentUser() {
        OrgInfo org = WebUtil.getBean(OrgInfoRepository.class).getByUid(getCurrentUserAccount().getOrgInfoUID());
        return  null == org.getOrgType() ? false : 4 == org.getOrgType();
    }
    /**
     * 判断当前登录是
     * 该租户已开启了无人值守服务
     * 该租户已开启了小型物联网设备(急救类)服务
     * @return boolean
     */
    public boolean accessContain(){
        List<String> usname = WebUtil.getBean(UserAccountRepository.class).findUsname(getCurrentUserAccount().getId());
//            for (String s : usname) {
//                if (s.equals("OpWorker")) {
//                    return false;
//                }
//            }
        List<WorkflowConfig> bySiteUID = WebUtil.getBean(WorkflowConfigRepository.class).findBySiteUID(getCurrentUserAccount().getSiteUID());
        if (bySiteUID!=null&&bySiteUID.size()!=0){
            if (bySiteUID.get(0).getWhethertoenableunattended()!=null && bySiteUID.get(0).getIsiotenabled()!=null){
                if (bySiteUID.get(0).getWhethertoenableunattended()==true && bySiteUID.get(0).getIsiotenabled()==true){
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;

    }

    public boolean enableSiteToSiteAllocation() {
        List<WorkflowConfig> bySiteUID = WebUtil.getBean(WorkflowConfigRepository.class).findBySiteUID(getCurrentUserAccount().getSiteUID());
        if (bySiteUID == null || bySiteUID.isEmpty()) {
            return false;
        }
        WorkflowConfig workflowConfig = bySiteUID.get(0);
        return workflowConfig.getEnableSiteToSiteAllocation() != null && workflowConfig.getEnableSiteToSiteAllocation();
    }
    public boolean enableAssetPreview() {
        List<WorkflowConfig> bySiteUID = WebUtil.getBean(WorkflowConfigRepository.class).findBySiteUID(getCurrentUserAccount().getSiteUID());
        if (bySiteUID == null || bySiteUID.isEmpty()) {
            return false;
        }
        WorkflowConfig workflowConfig = bySiteUID.get(0);
        return workflowConfig.getEnableAssetPreview() != null && workflowConfig.getEnableAssetPreview();
    }

    public boolean byOpWorker(){
        List<String> usname = WebUtil.getBean(UserAccountRepository.class).findUsname(getCurrentUserAccount().getId());
            for (String s : usname) {
                if (s.equals("OpWorker")) {
                    return false;
                }
            }
        return true;
    }
    //add MVSAdmin,MVSFE
    public boolean hasNotAnyRole(String roleList) {
        return !hasAnyRole(roleList);
    }
    public TenantInfo getTenantInfo(){
        if(tenant!=null) return tenant;
    	TenantInfoRepository tenantInfoRepository=WebUtil.getBean(TenantInfoRepository.class);
    	tenant = tenantInfoRepository.findByuid(getLoginUser().getTenantUID());
    	if(tenant.getCpmURL()==null){
    		String configStr=tenant.getTenantConfig();
        	if(StringUtils.isNotEmpty(configStr)){
        		try {
    				JSONObject jsonObj=JSONObject.parseObject(configStr);
    				String cpmURL=jsonObj.getString("cpmURL");
    				if(StringUtils.isEmpty(cpmURL)){
    				}
    				else{
    					tenant.setCpmURL(cpmURL);
    				}
    			} catch (Exception e) {
    				// TODO: handle exception
    				tenant.setCpmURL("");
    			}
        	}
    	}
        return tenant;
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

    public boolean accessContainsOfJiangNing(){
        TenantInfoRepository tenantInfoRepository=WebUtil.getBean(TenantInfoRepository.class);
        List<TenantInfo> tenantIfos = tenantInfoRepository.findByCustomTenant("2");
        if(tenantIfos == null || tenantIfos.size() == 0){
            return false;
        }
        TenantInfo tf = tenantIfos.get(0);
        String jiangNingUid = tf.getUid();
        String currentTenantUid = userAccount.getTenantUID();
        if(jiangNingUid.equals(currentTenantUid)){
            return true;
        }else{
            return false;
        }
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
                for (int i = 0; i < auditGroupConfigurationNodes.size(); i++) {
                    AuditGroupConfigurationNode auditGroupConfigurationNode = auditGroupConfigurationNodes.get(i);
                    List<String> userUids = auditGroupConfigurationNode.getUserUids();
                    UserAccount user = userAccountRepository.getByUid(userUids.get(0));
                    if (user!=null && userAccount.getUid().equals(user.getUid())){
                        return true;
                    }
                }

            }
        }
        return false;
    }
    public boolean authContains(String menuName){
    	if(userAccount.getIsSuperAdmin()||userAccount.getIsSiteAdmin()||userAccount.getIsGeOp()){
    		return true;
    	}
    	List<CustomRole> listCustomRole=getListCustomRole(userAccount.getId());
    	List<SysRole> listSysRole=getListSysRole(userAccount.getId());
    	if(listCustomRole!=null && listCustomRole.size()>0){
    		for(int i=0;i<listCustomRole.size();++i){
    			CustomRole tempRole=listCustomRole.get(i);
    			String menus=tempRole.getMenus();
    			if(StringUtils.isNotEmpty(menus)){
    				String [] menuArr=menus.split(",");
    				for(int j=0;j<menuArr.length;++j){
    					String tempStr=menuArr[j].trim();
    					if(tempStr.equals(menuName)){
    						return true;
    					}
    				}
    			}
    		}
    	}
    	if(listSysRole!=null && listSysRole.size()>0){
    		for(int i=0;i<listSysRole.size();++i){
    			SysRole tempRole=listSysRole.get(i);
    			String menus=tempRole.getMenus();
    			if(StringUtils.isNotEmpty(menus)){
    				String [] menuArr=menus.split(",");
    				for(int j=0;j<menuArr.length;++j){
    					String tempStr=menuArr[j].trim();
    					if(tempStr.equals(menuName)){
    						return true;
    					}
    				}
    			}
    		}
    	}
    	return false;
    }

    public boolean versionContains(String menuName){
    	TenantInfo tenantInfo=getTenantInfo();
    	String configStr=tenantInfo.getTenantConfig();
    	if(StringUtils.isNotEmpty(configStr)){
    		try {
				JSONObject jsonObj=JSONObject.parseObject(configStr);
				JSONArray jsonArray=jsonObj.getJSONArray("menuList");
				menuName=menuName.trim();
				for(int i=0;i<jsonArray.size();++i){
					if(jsonArray.getString(i).equals(menuName)){
						return true;
					}
				}
				return false;
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
    	}
    	else{
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

//        if (UserContextService.isSiteAdmin()) return true;
//        else if ("SiteAdmin".equals(role)) return false;
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
                userName = UserContext.getUsername(request);
                if(UserContext.ANONYMOUS_USER.equals(userName))
                    userName = null;
            }
            if (userName == null) {
                return null; // user is not logined
            }

            UserAccountRepository dao = WebUtil.getBean(UserAccountRepository.class);
            userAccount = dao.getByLoginName(userName);
        }

    /* else {
            // check if user has switch account by WeChat Client
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String userName = UserContext.getUsername(request);

            if (!userAccount.getLoginName().equals(userName)) {
                userAccount = WebUtil.getUserAccountFromRequest();
            }
        }
     */

        return userAccount;
    }

    public boolean isIBTenant(){
        UserAccount currentUser = getLoginUser();
        int siteId= currentUser.getSiteId();

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        if(siteId==46){
            if(userContextService.hasRole("AssetStaff")|| userContextService.hasRole("AssetHead")){
                return false;
            }
        }
        return siteId==46;
    }

    private Boolean flagIsAgentTenant = null;
    public boolean isAgentTenant() {
        if(flagIsAgentTenant!=null) return flagIsAgentTenant;

        UserAccount user = getLoginUser();
        tenant = getTenantInfo();
        flagIsAgentTenant = tenant.getType() != null && tenant.getType() == 1;

        return flagIsAgentTenant;
    }

    private Boolean flagIsUltraSoundTenant = null;
    public boolean isUltraSoundTenant() {
        if(flagIsUltraSoundTenant!=null) return flagIsUltraSoundTenant;

        UserAccount user = getLoginUser();
        TenantInfo tenant = getTenantInfo();
        flagIsUltraSoundTenant = tenant.getTenantConfig() != null && tenant.getTenantConfig().contains("\"edition\":\"ultrasound\"");

        return flagIsUltraSoundTenant;
    }

    WorkflowConfigRepository wfCfigDao = WebUtil.getBean(WorkflowConfigRepository.class);

    /**
     * 获取智康宝用户
     */
    ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);

    ZkbCourseTrainUserConfigRepository zkbCourseTrainUserConfigRepository = WebUtil.getBean(ZkbCourseTrainUserConfigRepository.class);

    /**
     * 获取 fe part order user config
     */
    ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);

    private Boolean flagIsHospitalUser = null;
    public boolean isHospitalUser(){
        if(flagIsHospitalUser!=null) return flagIsHospitalUser;

        UserAccount loginUser = getLoginUser();
        //判断医院级别是否有调剂中心
        List<WorkflowConfig> list = wfCfigDao.findHospitalLevel(loginUser.getSiteUID());
        if(list.size() > 0) {
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

    public static List<CustomRole> getCustomRole(Integer userId){
    	UserContextService userContextService = WebUtil.getBean(UserContextService.class);
    	return userContextService.getListCustomRole(userId);
    }

    public static List<SysRole> getSysRole(Integer userId){
    	UserContextService userContextService = WebUtil.getBean(UserContextService.class);
    	return userContextService.getListSysRole(userId);
    }

    public static UserAuth getCustomRoleAuth(Integer userId,String authKey){
    	UserAuth customRoleAuth=new UserAuth();
    	customRoleAuth.setAuthKey(authKey);
    	List<CustomRole> listCustomRole=getCustomRole(userId);
    	List<SysRole> listSysRole=getSysRole(userId);
    	if(listSysRole!=null && listSysRole.size()>0){
    		customRoleAuth.setAllAuth(authKey,true);
    	}
    	else if(StringUtils.isNotEmpty(authKey) && listCustomRole!=null && listCustomRole.size()>0){
			for(int i=0;i<listCustomRole.size();++i){
				CustomRole customRole=listCustomRole.get(i);
				String jsonStr=customRole.getWebAuthorities();
				if(StringUtils.isNotEmpty(jsonStr)){
					JSONArray jsonArr=JSONArray.parseArray(jsonStr);
					for(int j=0;j<jsonArr.size();++j){
						JSONObject jsonObj=jsonArr.getJSONObject(j);
						String key=jsonObj.getString("k");
						if(authKey.equals(key)){
							customRoleAuth.setAuthCreate(jsonObj.getBooleanValue("a"));
							customRoleAuth.setAuthDelete(jsonObj.getBooleanValue("d"));
							customRoleAuth.setAuthModify(jsonObj.getBooleanValue("m"));
							customRoleAuth.setAuthSearch(jsonObj.getBooleanValue("s"));
							customRoleAuth.setAuthExport(jsonObj.getBooleanValue("o"));
							customRoleAuth.setAuthImport(jsonObj.getBooleanValue("i"));
							return customRoleAuth;
						}
					}
				}

			}
		}
		else{
			customRoleAuth.setAllAuth(authKey,true);
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

    public static boolean isMVSAdmin(){
    	UserAccount userAccount=getCurrentUserAccount();
    	Integer mvsRoleType=userAccount.getMvsRoleType();
    	if(mvsRoleType!=null && (mvsRoleType== ApmConstant.MVS_ROLE_TYPE_ADMIN || mvsRoleType== ApmConstant.MVS_ROLE_TYPE_ADMIN_2)){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    public static boolean isMVSRole(){
        UserAccount userAccount=getCurrentUserAccount();
        Integer mvsRoleType=userAccount.getMvsRoleType();
        if(mvsRoleType != null && (mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_FE
                                    || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN_2)){
            return true;
        }
        else{
            return false;
        }
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
//    	return "/portal/profile.xhtml";
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
            }
        }
        if (request != null) {
            token = (String) request.getSession().getAttribute("ACCESS_TOKEN");
        }
        return token;
    }

    public String getSystemInMaintainConfig() {
        ApmConfigInfoRepository repository = WebUtil.getBean(ApmConfigInfoRepository.class);
        ApmConfigInfo info = null;
        try {
            info = repository.findByItem("system_maintain");
            if (info != null && info.getExpireDate() != null && (new Date()).after(info.getExpireDate())) {
                info = null;
            }
        } catch (Exception e) {
        }
        if (info != null && "1".equals(info.getValue())) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {
                ec.redirect("/wx/mrr?message=" + URLEncoder.encode(info.getMessage(), "UTF-8"));
            } catch (IOException e) {
            }
        }
        return "0";
    }

    public String getSystemInMaintainConfigMessage() {
        ApmConfigInfoRepository repository = WebUtil.getBean(ApmConfigInfoRepository.class);
        try {
            ApmConfigInfo info = repository.findByItem("system_maintain");
            if (info == null || info.getExpireDate() != null && (new Date()).after(info.getExpireDate())) {
                return null;
            } else {
                return info.getMessage();
            }
        } catch (Exception e) {
        }
        return null;
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

    private List<WorkflowConfig> getSiteConfigurations(){
        /*UaaService uuaService = WebUtil.getBean(UaaService.class);
        List<String> orgList = uuaService.getUserAccessibleSiteList(userAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        orgList.add(userAccount.getTenantUID());
        orgList.add(userAccount.getInstitutionUID());
        orgList.add(userAccount.getHospitalUID());
        orgList.add(userAccount.getSiteUID());
        WorkflowConfigRepository wcDao = WebUtil.getBean(WorkflowConfigRepository.class);
        List<String> collect = orgList.stream().distinct().collect(Collectors.toList());
        collect.removeIf(Objects::isNull);
        return wcDao.getBySiteUIDList(collect);*/
        List<String> orgList = new ArrayList<String>();
        orgList.add(userAccount.getTenantUID());
        orgList.add(userAccount.getInstitutionUID());
        orgList.add(userAccount.getHospitalUID());
        orgList.add(userAccount.getSiteUID());
        WorkflowConfigRepository wcDao = WebUtil.getBean(WorkflowConfigRepository.class);
        return wcDao.getBySiteUIDList1(orgList,userAccount.getUid());
    }

    public String isActive(){
        UserAccount currentUser = getLoginUser();
        if(!currentUser.getIsActive() && currentUser.getId() != 0){
            return "1";//need active
        }else {
            return "0";
        }
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

    public boolean isIBorUltraSoundIBorDealerEndUser(){
        return isIBEndUser() || isUltraSoundIBEndUser() || isDealerEndUser();
    }

    public boolean isAccountEndUser() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount currentUser = getLoginUser();
        int siteId = currentUser.getSiteId();
        /* 如果ib是46的话，工程师AssetStaff和医工主任AssetHead可以看到3个tab，其它人不能看到内部保养*/
        if (siteId == 46) {
            if (userContextService.hasRole("AssetStaff") || userContextService.hasRole("AssetHead")) {
                return false;
            }
        }

        if (isOpsOrSiteAdminUser()) {
            return false;
        }
        if (isDealerEndUser()) {
            return false;
        }
        if (isIBEndUser()) {
            return false;
        }
        return !isUltraSoundIBEndUser();
    }

    public boolean isAccessMyTrain(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount currentUser = getLoginUser();
        TenantInfo currentTenantInfo = userContextService.getCurrentUserTenantInfo();
        List<String> usname = WebUtil.getBean(UserAccountRepository.class).findUsname(getCurrentUserAccount().getId());
            for (String s : usname) {
                if (s.equals("OpWorker")) {
                    return false;
                }
            }
        int siteId = currentUser.getSiteId();
        if (siteId == 46 || BooleanUtils.isTrue(currentTenantInfo.getHaveIbDevice())) {
            return true;
        }
        return false;
    }


    public boolean isHaveIbDeviceUser() {
        return tenant.getHaveIbDevice() == null ? false : tenant.getHaveIbDevice();
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
        if (ApmConstant.USER_ACCOUNT_TYPE_PARTNER == user.getType() && user.getIsSuperAdmin()) {
            return true;
        }
        return false;
    }
    public boolean isPartnerAccount46() {
        UserAccount user = getLoginUser();
        if (null == user.getSiteId()) {
            return false;
        }
        if (ApmConstant.IB_TENANT_ID == user.getSiteId()) {
            return true;
        }
        return false;
    }

    public boolean isZkbAccount() {
        TenantInfo tenantInfo = getTenantInfo();
        if(tenantInfo.getType() != null && tenantInfo.getType() == 3){
            return true;
        }
        return false;
    }

    public boolean isGeAdmin() {
        UserAccount user = getLoginUser();
        if(StringUtils.isBlank(user.getLoginName()))
            return false;
        List<ZkbPartOrderUserConfig>  zkbPartOrderUserConfig= this.zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(user.getLoginName(),"ClosePending");
        if(CollectionUtils.isNotEmpty(zkbPartOrderUserConfig))
            return true;
        return false;
    }

    public boolean isEditCourseScheduleAdmin() {
        UserAccount user = getLoginUser();
        if(StringUtils.isBlank(user.getLoginName()))
            return false;
        List<ZkbCourseTrainUserConfig>  zkbCourseTrainUserConfigList = this.zkbCourseTrainUserConfigRepository.findByLoginName(user.getLoginName());
        if(CollectionUtils.isNotEmpty(zkbCourseTrainUserConfigList))
            return true;
        return false;
    }

    public boolean isZkbFeUserConfig(String configType)
    {
        UserAccount user = getLoginUser();
        if(StringUtils.isBlank(user.getLoginName()))
            return false;
        List<ZkbFePartOrderUserConfig>  zkbFePartOrderUserConfig= this.zkbFePartOrderUserConfigRepository.findByLoginNameAndConfigType(user.getLoginName(),configType);
        if(CollectionUtils.isNotEmpty(zkbFePartOrderUserConfig))
            return true;
        return false;
    }

    public boolean isZkbConfigAdmin()
    {
        UserAccount user = getLoginUser();
        if(StringUtils.isBlank(user.getLoginName()))
            return false;
        if(user.getLoginName().contains("zkbConfigAdmin"))
            return true;
            return false;
    }

    public boolean isZtbTenant() {
        TenantInfo tenantInfo = getTenantInfo();
        if(tenantInfo.getType() != null && tenantInfo.getType() == 4){
            return true;
        }
        return false;
    }

    public boolean isMvsTenant() {
        TenantInfo tenantInfo = getTenantInfo();
        return ApmConstant.MVS_TENANT_NAME.equals(tenantInfo.getName());
    }

    public boolean isSupportMultiLanguage(){
        TenantInfo tenantInfo = getTenantInfo();
        if(tenantInfo.getIsSupportMultiLanguage() != null && tenantInfo.getIsSupportMultiLanguage() == true){
            return true;
        }
        return false;
    }

    public boolean isYiMaiYangGuangTenant() {
        Optional<TenantOptionConfig> tenantOptionConfigOptional = WebUtil.getBean(TenantOptionConfigRepository.class).findByTenantUid(WebUtil.getBean(UserContextService.class).getTenantInfo().getUid());
        if (tenantOptionConfigOptional.isPresent()) {
            return tenantOptionConfigOptional.get().getSynchronizeWorkOrder()!=null? tenantOptionConfigOptional.get().getSynchronizeWorkOrder():false;
        }
        return false;
    }

    public static boolean isSuperAdminWithPartnerAccount(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.isPartnerAccount();
    }

    public static boolean isOnlyLocalITAdmin() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        UserAccount user = userContextService.getLoginUser();
        List<String> userRoles = user.getRoleNames();

        if(userRoles.contains("LocalITAdmin")){
            if(userRoles.contains("ITAdmin") || isSuperAdmin()){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    public static boolean isInsulate() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        TenantInfo tenantInfo = userContextService.getTenantInfo();
        if(tenantInfo.getInsulate() != null && tenantInfo.getInsulate() && !userContextService.hasAnyRole(ADMIN)) {
            return true;
        }
        return false;
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
            StringBuffer secondDecryptionArrayStringBuffer = new StringBuffer();
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
            StringBuffer firstDecryptionArrayStringBuffer = new StringBuffer();
            for (String tempData : tempFirstDecryptionArray) {
                firstDecryptionArrayStringBuffer.append(tempData);
            }
            String decryptionData = B64Code.decode(firstDecryptionArrayStringBuffer.toString(), "utf-8");

            return decryptionData;
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
        //System.out.println("locale  is "+locale);
        if(locale.getLanguage().equalsIgnoreCase("zh")){
            return true;
        } else {
            return false;
        }
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
//        if(!isLocale()){
//            return Locale.US.toString();
//        }else{
            return Locale.CHINESE.getLanguage();
//        }
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
        if (roleNames.contains(SimplePartsConstant.PARTS_ADMIN_MANAGER)) {
            return true;
        } else {
            return false;
        }
    }

    //判断是否有报废资产菜单权限
    public boolean hasScrappedMenuAuthority() {

        UserRoleRepository userRoleDao = WebUtil.getBean(UserRoleRepository.class);
        CustomRoleRepository customeRoleDao = WebUtil.getBean(CustomRoleRepository.class);
        List<UserRole> userRoleList = userRoleDao.findByUserIdAndRoleType(UserContextService.getCurrentUserAccount().getId(), ApmConstant.CUSTOME_ROLE_TYPE);
        for (UserRole userRole : userRoleList) {
            String menus = customeRoleDao.findById(userRole.getRoleId()).getMenus();
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
        TenantOptionConfigRepository tenantOptionConfigRepository = WebUtil.getBean(TenantOptionConfigRepository.class);
        Optional<TenantOptionConfig> tenantOptionConfigOptional = tenantOptionConfigRepository.findByTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());

        if (!tenantOptionConfigOptional.isPresent()) {
            return false;
        }

        TenantOptionConfig tenantOptionConfig = tenantOptionConfigOptional.get();

        if (null == tenantOptionConfig.getAllowAssetBatchUpdate()) {
            return false;
        }

        return tenantOptionConfig.getAllowAssetBatchUpdate();

    }

    /***
     *  判断是否是云南阜外医院
     * @param tenantName
     * @return
     */
    public  Boolean isYunNanFuWai(String tenantName){
        if(this.getTenantInfo().getName().equals("云南省阜外心血管病医院")){
            return true;
        }else {
            return false;
        }
    }
    /***
     *  判断是否是北京积水潭医院
     * @param tenantName
     * @return
     */
    public static Boolean isHospitalName(String tenantName){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        TenantInfo currentTenantInfo = userContextService.getTenantInfo();
        if(currentTenantInfo == null){
            return Boolean.FALSE;
        }
        if(StringUtils.isBlank(tenantName)){
            //tenantName为空则验证下面租户
            if(WorkOrderConstant.DESIGNATED_USER.equals(currentTenantInfo.getName().trim())){
                return Boolean.TRUE;
            }
        }else{
            if(tenantName.equals(currentTenantInfo.getName().trim())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
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
            //tenantName为空则验证下面租户
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
        Boolean flag = Boolean.FALSE;
        Optional<TenantOptionConfig> tenantOptionConfigOptional = WebUtil.getBean(TenantOptionConfigRepository.class).findByTenantUid(getCurrentUserTenantInfo().getUid());
        if (tenantOptionConfigOptional.isPresent()) {
            TenantOptionConfig tenantOptionConfig = tenantOptionConfigOptional.get();
            flag = tenantOptionConfig.getActiveSupplier();
        }
        return flag;
    }

    /**
     * 判断租户是否已激活供应商
     * @return
     */
    public boolean activeSupplier(){
        if(activeSupplierFlag == null){
            activeSupplierFlag = false;
            UserAccount user = getCurrentUserAccount();
            WorkflowConfigRepository workflowConfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
            List<WorkflowConfig> workflowConfigList = workflowConfigDao.findBySiteUID(user.getSiteUID());
            if(workflowConfigList.isEmpty()){
                workflowConfigList = workflowConfigDao.findBySiteUID(user.getHospitalUID());
            }
            if(workflowConfigList.isEmpty()){
                workflowConfigList = workflowConfigDao.findBySiteUID(user.getInstitutionUID());
            }
            if(workflowConfigList.isEmpty()){
                workflowConfigList = workflowConfigDao.findBySiteUID(user.getTenantUID());
            }

            if(!workflowConfigList.isEmpty()){
                if (workflowConfigList.get(0).getActiveSupplier() != null && workflowConfigList.get(0).getActiveSupplier()){
                    activeSupplierFlag = true;
                }
            }
        }
        return activeSupplierFlag;
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

    /**
     * 是否是县域租户
     * @return
     */
    public boolean isCountyArea(){
        TenantInfo tenantInfo = getTenantInfo();
        if(tenantInfo.getType() != null && tenantInfo.getType() == 6){
            return true;
        }
        return false;
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

    public List<OrgInfo> getSiteOrgInfoChild(){
            if(CollectionUtils.isNotEmpty(child)) return child;
            String hospitalUID = userAccount.getHospitalUID();
            if(userAccount.getOrgLevel() == 2 && hospitalUID.equals(userAccount.getSiteUID())){
                WorkflowConfigRepository workflowConfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
                OrgInfoRepository orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
                child= orgInfoRepository.getHospitalByTenantUIDAndType(hospitalUID);
                if(child.size() > 0){
                    child=child.stream().filter(item->{
                        if(item == null)return false;
                        Integer orgLevel = item.getOrgLevel();
                        Integer orgType = item.getOrgType();
                        return orgLevel !=null && orgType !=null && orgLevel == 3 && orgType==3;
                    }).filter(item->{
                        List<WorkflowConfig> siteWorkflowConfigList = workflowConfigDao.findBySiteUID(item.getUid());
                        if(CollectionUtils.isEmpty(siteWorkflowConfigList)){
                            return false;
                        }
                        WorkflowConfig workflowConfig = siteWorkflowConfigList.get(0);
                        return workflowConfig !=null && workflowConfig.getEnableHospitalAdjustment() != null && workflowConfig.getEnableHospitalAdjustment();
                    }).collect(Collectors.toList());
                    if(child.size() > 0){
                        List<WorkflowConfig> siteWorkflowConfigList = workflowConfigDao.findBySiteUID(hospitalUID);
                        if (CollectionUtils.isNotEmpty(siteWorkflowConfigList) && siteWorkflowConfigList.get(0).getEnableHospitalAdjustment()) {
                            OrgInfo orgInfo = orgInfoRepository.getByUid(hospitalUID);
                            selectSite=orgInfo;
                            child.add(orgInfo);
                        }else{
                            selectSite=child.get(0);
                        }
                    }
                }
            }
        return  child;
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
        for (TreeNode child : tree.getChildren()) {
            expandedByOrgType(child, type);
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
                selectItems = orgInfos.stream().map(item -> {
                    return new SelectItem(item.getUid(), item.getName(), "", item.getOrgType() != 4);
                }).collect(Collectors.toList());
            }
        }
        return selectItems;
    }
}
