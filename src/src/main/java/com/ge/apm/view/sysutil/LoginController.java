package com.ge.apm.view.sysutil;

import com.ge.apm.dao.LoginRecordRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.LoginRecord;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.web.cboard.CBoardController;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.SessionCache;
import webapp.framework.util.StringUtil;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.LoginService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@RequestScoped
public class LoginController extends LoginService {

    public static final int ACTIVATION_ROLE = 10;

	private Boolean isSuccess;

    private String plainPassword;

    private Boolean agree = false;

    private List<String> items;

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
    public void sendTheVerificationCode() {
        if(StringUtils.isEmpty(telephone)) return;
        try {
            UserAccountService userAccountService = WebUtil.getBean(UserAccountService.class);
            UserAccount user = userAccountService.getUserByTelephone(telephone);
            if(user != null) {
                TenantInfo tenantInfo = tenantInfoDao.findByuid(user.getTenantUID());
                if(tenantInfo != null && tenantInfo.getType() != null && tenantInfo.getType() != 3){
                    WebUtil.addErrorMessage("此手机号非智康保用户!");
                    return;
                }
                //发送手机验证码
                ResponseEntity<LinkedHashMap> response = this.sendVerificationCode(telephone, user.getEmail());
                if(response.getBody() != null && bizStatusCode.equals(response.getBody().get("bizStatusCode"))){
                    readonlyFlag = true;
                    WebUtil.addErrorMessage("验证码已发送请注意查收");
                    return;
                }
            }else {
                WebUtil.addErrorMessage("此手机号没找到对应的用户");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendMail() {
        if(StringUtils.isEmpty(verificationCode)){
            WebUtil.addErrorMessage("验证码不能为空");
            return;
        }
        if(!StringUtils.isEmpty(verificationCode)){
            ResponseEntity<LinkedHashMap> response = this.sendEmail(verificationCode, telephone);
            if(response.getBody() != null && bizStatusCode.equals(response.getBody().get("bizStatusCode"))){
                WebUtil.addErrorMessage("新密码已发送到邮件注意查收");
                return;
            }
        }
    }
    public void validatePassword(){
        UserAccount user = UserContextService.getCurrentUserAccount();
        if(user==null){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        
        try {
            byte[] salt = Digests.decodeHex(user.getPwdSalt());
            byte[] hashPassword;

            hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
            String saltedPassword = Digests.encodeHex(hashPassword);

            if(!user.getPassword().equals(saltedPassword)){
                WebUtil.addErrorMessageKey("PasswordNotMatch");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            }

            plainPassword = "";
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DecoderException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String onPasswordEncrypted(String loginName, String plainPassword){
        UserAccountRepository userDao = WebUtil.getBean(UserAccountRepository.class);
        UserAccount user = userDao.getByLoginName(loginName);
        if(user!=null) {
            try {
                byte[] salt = Digests.decodeHex(user.getPwdSalt());
                byte[] hashPassword;
                
                hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
                String saltedPassword = Digests.encodeHex(hashPassword);
                
                return saltedPassword;
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DecoderException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return plainPassword;        
    }

    private boolean checkPassword(String plainPassword){
        String saltedPassword = null;
        try {
            byte[] salt = Digests.decodeHex(user.getPwdSalt());
            byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, UserAccount.HASH_INTERATIONS);
            saltedPassword = Digests.encodeHex(hashPassword);
        } catch (Exception ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return user.getPassword().equals(saltedPassword);
    }
    
    @Override
    protected boolean beforeLogin(String plainPassword) {
        if(!StringUtils.isEmpty(PasswordUtil.checkPwd(plainPassword)) || isFirstTimeLogin(user)){
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            addErrorMessage(response, 205, "Resetpassword");
            return false;
        }
        return true;
    }
    
    @Override
    protected void afterLogin(UserAccount originUser,String userAgent) {
        // called after user logined.
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        userContextService.processAfterLogin();
        isSuccess = true;
        UserAccount user = userContextService.getLoginUser();
        if(user!=null){
        	user.setGeSSO(originUser.isGeSSO());
            user.setPlainPassword(originUser.getPlainPassword());
        }
        else{
        	UserContextService.setLoginUser(originUser);
        	user=originUser;
        }
        //in order to get token for deactive user
        saveSession(user,userAgent);

        String saltedPassword = null;
        try {
            byte[] salt = Digests.decodeHex(user.getPwdSalt());
            byte[] hashPassword = Digests.sha1(PasswordUtil.DEFAULT_PWD.getBytes(), salt, UserAccount.HASH_INTERATIONS);
            saltedPassword = Digests.encodeHex(hashPassword);
        } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DecoderException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user.getPassword().equals(saltedPassword)) {
            WebUtil.redirectTo("/resetPassword2.xhtml");
        } else {

            boolean factorValidLogin = isPhoneValidLogin(user);
            if(!factorValidLogin ){
                skipTelephoneValidlogin();
                String str = userContextService.getUserDefaultHomePage();
                if(StringUtils.isEmpty(redirectUrl)){
                    WebUtil.redirectTo(str);
                }
            }else if(initialLogin == 0){
                initialLogin = 3;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("twoFactorValid", initialLogin);
                telephone = user.getTelephone();
                encryTelephone = StringUtil.mobileEncrypt(telephone);
            }else if(initialLogin == 2){
                skipTelephoneValidlogin();
                String str = userContextService.getUserDefaultHomePage();
                WebUtil.redirectTo(str);
            }else if(initialLogin == 4){
                String str="/portal/login/phoneValid.xhtml";
                WebUtil.redirectTo(str);
            }
        }
    }
    private void skipTelephoneValidlogin() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("PHONE_SMSCODE_VALID",true);
    }
    @Transactional
    public void saveSession(UserAccount user,String userAgent){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        MicroServiceUtil msUtil = WebUtil.getBean(MicroServiceUtil.class);
        String loginName = user.getLoginName();
        UserAccountRepository dao = WebUtil.getBean(UserAccountRepository.class);
        if(user.getIsActive()){
            try{
                String accessToken = null;
                if(user.isGeSSO() || initialLogin == 1){
                	accessToken=msUtil.getAuthenticateByUserPasswordEncrypted(loginName, user.getPassword(),userAgent);
                }
                else{
                	accessToken=msUtil.getAuthenticateByUserPassword(loginName, user.getPlainPassword(),userAgent);
                }
                request.getSession().setAttribute("ACCESS_TOKEN", accessToken);
            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            user.setIsActive(true);
            dao.save(user);
            try{
            	String accessToken = null;
                if(user.isGeSSO() || initialLogin == 1){
                	accessToken=msUtil.getAuthenticateByUserPasswordEncrypted(loginName, user.getPassword(),userAgent);
                }
                else{
                	accessToken=msUtil.getAuthenticateByUserPassword(loginName, user.getPlainPassword(),userAgent);
                }
                request.getSession().setAttribute("ACCESS_TOKEN", accessToken);
            }catch(Exception e){
                e.printStackTrace();
            }

            user.setIsActive(false);
            dao.save(user);
        }
        request.getSession().setAttribute(CBoardController.LOGIN_NAME_KEY, loginName);
        SessionCache.putSession(request.getSession());

        //登录信息记录
        saveLoginRecord(ctx,user);
    }

    private void saveLoginRecord(ExternalContext ctx, UserAccount user) {
        try {
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            String loginName = user.getLoginName();
            String addr = request.getRemoteAddr();
            String origin = ctx.getRequestHeaderMap().get("origin");
            LoginRecordRepository loginRecordRepository = WebUtil.getBean(LoginRecordRepository.class);
            LoginRecord loginRecord =  new LoginRecord();
            loginRecord.setLoginName(loginName);
            loginRecord.setLoginUrl(origin);
            loginRecord.setLoginTime(new Date());
            loginRecord.setLoginIp(addr);
            loginRecord.setIsDeleted(false);
            loginRecordRepository.save(loginRecord);
        }catch (Exception ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getHomePage() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.getUserDefaultHomePage();
    }

/*    public void login(){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
        WebApplicationContext contextForWeb = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        UserAccountRepository userAccountRepository = contextForWeb.getBean(UserAccountRepository.class);

        String userName = "";
        String password = "";
        Map<String, String[]> extraParams = new TreeMap<String, String[]>();
        userName = request.getParameter("j_username");
        password = request.getParameter("j_password");
        String originPwd = password;

        if ("".equals(userName) || userName == null) {
            userName = request.getParameter("input_j_username");
            password = request.getParameter("input_j_password");
            extraParams.put("j_username", new String[] { userName });
            extraParams.put("j_password", new String[] { password });
            HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
            request = wrappedRequest;
        }
        if (password == null)
            password = "";

        password = onPasswordEncrypted(userName, password);
        extraParams.put("j_username", new String[] { userName });
        extraParams.put("j_password", new String[] { password });
        HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
        request = wrappedRequest;
        this.request = request;
        this.response = response;
        Authentication auth = authenticate(this.request,this.response);
        if(auth == null){
            return;
        }else{
            this.authentication = auth;
            UserAccount currentUser = userAccountRepository.getByLoginName(userName);
            if(!currentUser.getIsActive()){
                //RequestContext.getCurrentInstance().openDialog("ActiveDlg2");
                WebUtil.redirectTo("/authorization2.xhtml");
                //WebUtil.redirectTo("/login.xhtml");
                return;
            }
            afterActive(userName);
        }
    }*/

/*    public void afterActive(String userName){
        WebApplicationContext contextForWeb = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        MicroServiceUtil msUtil = contextForWeb.getBean(MicroServiceUtil.class);
        saveToContext(this.request,this.response,this.authentication);
        afterLogin();
        updateLoginStatus(userName, true);
        String accessToken = msUtil.getAuthenticateByUserPassword(userName,plainPassword);
        request.getSession().setAttribute("ACCESS_TOKEN", accessToken);
    }*/

/*    @Transactional
    public void confirmActivation(){
       UserAccount loginUser = UserContext.getCurrentLoginUser();//UserContextService.getCurrentUserAccount();
       UserAccountRepository dao = WebUtil.getBean(UserAccountRepository.class);
       TenantInfoRepository tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        if(loginUser.getIsActive()){
            System.out.println("already active !");
            WebUtil.addSuccessMessage("激活成功！");
            return;
        }
        loginUser.setIsActive(true);
        loginUser.setActiveTime(new Date());
        dao.save(loginUser);
        List<UserRole> roles = loginUser.getUserRoleList();
        roles = roles.stream().filter(role -> role.getRoleId() == ACTIVATION_ROLE).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(roles)){
            TenantInfo tenant = tenantInfoRepository.findByuid(loginUser.getTenantUID());
            tenant.setIsEnabled(true);
            tenant.setActiveTime(new Date());
            tenantInfoRepository.save(tenant);
            dao.updateMembersStatus(loginUser.getTenantUID());
*//*            if(tenant.getIsEnabled() == null || (tenant.getIsEnabled() != null && !tenant.getIsEnabled())){

            }*//*
        }

        afterActive(loginUser.getLoginName());
        WebUtil.addSuccessMessage("激活成功！");
    }*/


	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

    public Boolean getAgree() {
        return agree;
    }

    public void setAgree(Boolean agree) {
        this.agree = agree;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
    

    private String username;
    private String password;
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void resetPassword(){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        String tempPassword = UserContextService.decryptData(password);
        String tempNewPassword = UserContextService.decryptData(newPassword);

        user = userAccountDao.getByLoginName(username);
        if(user==null){
            WebUtil.addErrorMessage("用户名或密码错误!");
            return;
        }

        if(!checkPassword(tempPassword))
            WebUtil.addErrorMessage("用户名或密码错误!");
        else if(PasswordUtil.DEFAULT_PWD.equals(tempNewPassword))
            WebUtil.addErrorMessage("请不要使用系统默认密码作为个人密码!");
        else{
            String checkResult = PasswordUtil.checkPwd(tempNewPassword);
            if(!StringUtils.isEmpty(checkResult))
                WebUtil.addErrorMessage(checkResult);
            else{
                user.setPassword(this.onPasswordEncrypted(username, tempNewPassword));
                user.setLastModifiedDate(TimeUtil.now());
                user.setLastModifiedBy(user.getLoginName());
                userAccountDao.save(user);
                user.setPlainPassword(tempNewPassword);
                initialLogin = 4;
                doSpringSecuritylogin(request, response, user);
            }
        }
    }
    public void doVerificationCode(){
        //"校验验证码,进行登录
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

//        logger.info("phone login: telephone={},mdmCode={}",telephone,verificationCode);
        if(org.springframework.util.StringUtils.isEmpty(telephone)){
            WebUtil.addErrorMessage("手机号不能为空");
            return;
        }
        if(org.springframework.util.StringUtils.isEmpty(verificationCode)){
            WebUtil.addErrorMessage("验证码不能为空");
            return;
        }
        Map<String,String> map = msUtil.getMsMCode(telephone);
        if(map == null){
            WebUtil.addErrorMessage("验证码已经过期,请重新发送");
            return;
        }
        String phone = map.get("phone");
        String code = map.get("code");
        if(phone.equals(telephone) && verificationCode.equals(code)){
            skipTelephoneValidlogin();
            // called after user logined.
            String str = WebUtil.getBean(UserContextService.class).getUserDefaultHomePage();
            WebUtil.redirectTo(str);
        }else {
            WebUtil.addErrorMessage("验证码错误");
        }

    }
}
