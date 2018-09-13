package com.ge.apm.view.sysutil;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.uaa.UaaService;
import com.google.common.base.Strings;

import javaslang.control.Option;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

@ManagedBean(name = "userContextService")
@SessionScoped
public class UserContextService implements Serializable {

    private static final long serialVersionUID = 1L;

    private UserAccount userAccount = null;

    public static void setLoginUser(UserAccount user) {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        userContextService.userAccount = user;
        userContextService.isDispatcher = null;
        userContextService.isPmDispatcher = null;
        userContextService.isAssetApprover = null;
        userContextService.needAssetApproval = null;
        userContextService.needInstorageCheck = null;
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

    public boolean hasRole(String role) {
        return UserContextService.checkRole(role);
    }

    public boolean hasAnyRole(String roleList) {
        UserAccount user = getLoginUser();
        List<String> userRoles = user.getRoleNames();

        String[] roles = roleList.split(",");
        for (String role : roles) {
            role = role.trim();
            boolean roleAllowed = userRoles.contains(role);
            if (roleAllowed) {
                return true;
            }
        }

        return false;
    }

    public boolean hasNotAnyRole(String roleList) {
        UserAccount user = getLoginUser();
        List<String> userRoles = user.getRoleNames();

        String[] roles = roleList.split(",");
        for (String role : roles) {
            role = role.trim();
            boolean roleAllowed = userRoles.contains(role);
            if (roleAllowed) {
                return false;
            }
        }

        return true;
    }
    
    public boolean versionContains(String menuNameList){
    	UserAccount user = getLoginUser();
    	TenantInfoRepository tenantInfoRepository=WebUtil.getBean(TenantInfoRepository.class);
    	TenantInfo tenantInfo=tenantInfoRepository.findByuid(user.getTenantUID());
    	String configStr=tenantInfo.getTenantConfig();
    	if(StringUtils.isNotEmpty(configStr)){
    		try {
				JSONObject jsonObj=JSONObject.parseObject(configStr);
				JSONArray jsonArray=jsonObj.getJSONArray("menuList");
				String[] menus=menuNameList.split(",");
				for(String menuName:menus){
					menuName=menuName.trim();
					for(int i=0;i<jsonArray.size();++i){
						if(jsonArray.getString(i).equals(menuName)){
							return true;
						}
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
/*
        else {
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
    
    private Boolean flagIsAgentTenant = null;
    public boolean isAgentTenant() {
        if(flagIsAgentTenant!=null) return flagIsAgentTenant;
            
        UserAccount user = getLoginUser();
        TenantInfoRepository tenantDao = WebUtil.getBean(TenantInfoRepository.class);
        TenantInfo tenant = tenantDao.findByuid(user.getTenantUID());
        if(tenant.getType() != null && tenant.getType() == 1) {
                flagIsAgentTenant = true;
        }
        else
            flagIsAgentTenant = false;
        
        return flagIsAgentTenant;
    }

    WorkflowConfigRepository wfCfigDao = WebUtil.getBean(WorkflowConfigRepository.class);

    private Boolean flagIsHospitalUser = null;
    public boolean isHospitalUser(){
        if(flagIsHospitalUser!=null) return flagIsHospitalUser;

        flagIsHospitalUser = true;
        UserAccount loginUser = getLoginUser();
        if (loginUser.getSiteUID().equals(loginUser.getHospitalUID())){
            //判断医院级别是否有调剂中心
            List<WorkflowConfig> list = wfCfigDao.findHospitalLevel(loginUser.getHospitalUID());
            if(list.size() == 0) flagIsHospitalUser = false;
        }
        
        return flagIsHospitalUser;
    }

    public int getLoginUserId() {
        return Option.of(getLoginUser()).map(UserAccount::getId).getOrElse(-1);
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
        return Option.of(getLoginUser()).map(u -> WebUtil.getBean(TenantInfoRepository.class).findById(getLoginUser().getSiteId()).getName()).getOrElse("");
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

    public static UserAccount getCurrentUserAccount() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.getLoginUser();
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

    private Boolean isDispatcher = null;
    private Boolean isPmDispatcher = null;

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

        isAssetApprover = wcList.stream().anyMatch(item -> Optional.ofNullable(item.getAssetApprover()).orElse("").equals(userAccount.getUid()));
        
        needAssetApproval = wcList.stream().anyMatch(item -> !Strings.isNullOrEmpty(item.getAssetApprover()));
        
        needInstorageCheck = wcList.stream().anyMatch(item -> null!=item.getNeedInstorageCheck() && item.getNeedInstorageCheck());
    }
    
    private List<WorkflowConfig> getSiteConfigurations(){
        UaaService uuaService = WebUtil.getBean(UaaService.class);
        List<String> orgList = uuaService.getUserAccessibleSiteList(userAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        orgList.add(userAccount.getTenantUID());
        orgList.add(userAccount.getInstitutionUID());
        orgList.add(userAccount.getHospitalUID());
        orgList.add(userAccount.getSiteUID());
        WorkflowConfigRepository wcDao = WebUtil.getBean(WorkflowConfigRepository.class);
        return wcDao.getBySiteUIDList(orgList.stream().distinct().collect(Collectors.toList()));
    }

    public String isActive(){
        UserAccount currentUser = getLoginUser();
        if(!currentUser.getIsActive() && currentUser.getId() != 0){
            return "1";//need active
        }else {
            return "0";
        }
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
        UserAccount user = userContextService.getLoginUser();
        TenantInfoRepository tenantDao = WebUtil.getBean(TenantInfoRepository.class);
        TenantInfo tenant = tenantDao.findByuid(user.getTenantUID());
        if(tenant.getInsulate() != null && tenant.getInsulate()) {
            return true;
        }
        return false;
    }
}
