package com.ge.apm.view.sysutil;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.utils.Digests;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpHandler;

import com.ge.apm.web.cboard.CBoardController;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import webapp.framework.context.WrappedRequest;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.LoginService;
import webapp.framework.web.service.MessageUtil;
import webapp.framework.web.service.UserContext;

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
    
    @Override
    protected void afterLogin(String userName,String password) {
        // called after user logined.
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        userContextService.processAfterLogin();
        isSuccess = true;
        UserAccount user = userContextService.getLoginUser();

        //in order to get token for deactive user
        saveSession(user,userName,password);

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
            String str = userContextService.getUserDefaultHomePage();
            //System.out.println(str);
            WebUtil.redirectTo(str);
        }
    }

    @Transactional
    public void saveSession(UserAccount user,String userName,String password){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        MicroServiceUtil msUtil = WebUtil.getBean(MicroServiceUtil.class);
        UserAccountRepository dao = WebUtil.getBean(UserAccountRepository.class);
        if(user.getIsActive()){
            try{
                String accessToken = msUtil.getAuthenticateByUserPassword(userName, password);
                request.getSession().setAttribute("ACCESS_TOKEN", accessToken);
            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            user.setIsActive(true);
            dao.save(user);
            try{
                String accessToken = msUtil.getAuthenticateByUserPassword(userName, password);
                request.getSession().setAttribute("ACCESS_TOKEN", accessToken);
            }catch(Exception e){
                e.printStackTrace();
            }

            user.setIsActive(false);
            dao.save(user);
        }

        request.getSession().setAttribute(CBoardController.LOGIN_NAME_KEY, userName);
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
}
