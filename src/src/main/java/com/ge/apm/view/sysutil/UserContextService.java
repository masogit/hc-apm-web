package com.ge.apm.view.sysutil;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ApmConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.uaa.UaaService;
import com.google.common.base.Strings;

import javaslang.control.Option;
import org.eclipse.jetty.util.B64Code;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

@ManagedBean(name = "userContextService")
@SessionScoped
public class UserContextService implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String ADMIN = "SuperAdmin,ITAdmin";
//    public static final String MVS_ADMIN="MVSAdmin";
//    private static final String MVS_FE="MVSFE";

    private TenantInfo tenant = null;
    private UserAccount userAccount = null;
    private List<CustomRole> listCustomRole=null;
    private List<SysRole> listSysRole=null;

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
        if(user.getIsSuperAdmin()){
            return roleList.contains("SuperAdmin");
        }
        if(user.getIsSiteAdmin()){
            return roleList.contains("SiteAdmin");
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

    public boolean isIBTenant(){
        UserAccount currentUser = getLoginUser();
        int siteId= currentUser.getSiteId();
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
        UaaService uuaService = WebUtil.getBean(UaaService.class);
        List<String> orgList = uuaService.getUserAccessibleSiteList(userAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        orgList.add(userAccount.getTenantUID());
        orgList.add(userAccount.getInstitutionUID());
        orgList.add(userAccount.getHospitalUID());
        orgList.add(userAccount.getSiteUID());
        WorkflowConfigRepository wcDao = WebUtil.getBean(WorkflowConfigRepository.class);
        List<String> collect = orgList.stream().distinct().collect(Collectors.toList());
        collect.removeIf(Objects::isNull);
        return wcDao.getBySiteUIDList(collect);
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
    
    public boolean isAccountEndUser(){
        if(isOpsOrSiteAdminUser()) return false;
        if(isDealerEndUser()) return false;
        if(isIBEndUser()) return false;
        return !isUltraSoundIBEndUser();
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
}
