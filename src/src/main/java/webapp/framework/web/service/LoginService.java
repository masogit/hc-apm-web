package webapp.framework.web.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.B64Code;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.MicroServiceUtil;

import webapp.framework.context.WrappedRequest;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

public class LoginService implements Serializable, ApplicationEventPublisherAware {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    public static final int PASSWORD_ERROR_TIME_LIMIT = 6;
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private SecurityContextRepository securityContextRepository;
    private MessageUtil messageUtil;
    protected UserAccountRepository userAccountDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private MicroServiceUtil msUtil;
    private GeSsoService geSsoService;
    
    protected UserAccount user;

    static {
        IdFuzzyResolver.registerResolver();
        // Global setting: change default timezone to UTC+0
        // TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

    @PostConstruct
    private void init() {
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);
        geSsoService = WebUtil.getBean(GeSsoService.class);
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        usernamePasswordAuthenticationFilter = ctx.getBean(UsernamePasswordAuthenticationFilter.class);
        sessionAuthenticationStrategy = ctx.getBean(SessionAuthenticationStrategy.class);
        securityContextRepository = ctx.getBean(SecurityContextRepository.class);
        messageUtil = ctx.getBean(MessageUtil.class);
        msUtil = ctx.getBean(MicroServiceUtil.class);
    }

    public void setSessionExpired(String value) {
        messageUtil.error("session_expired");
    }

    public String getSessionExpired() {
        return null;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void forgetPassword() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        String loginName = request.getParameter("j_username");

        FacesMessage msg = new FacesMessage();
        msg.setSummary("Password reset");
        msg.setDetail("Password reset");
        FacesContext.getCurrentInstance().addMessage("forgetPassword", msg);
    }

    public String onPasswordEncrypted(String loginName, String plainPassword) {
        return plainPassword;
    }

    protected void afterLogin(UserAccount originUser) {

    }

    protected void setCookieForUser(HttpServletResponse res, String userName, String password) throws Exception {
        String body = "{\"loginName\": \"" + userName + "\",\"password\": \"" + password + "\"}";
        Properties pro = new Properties();
        pro.load(PropertyUtils.class.getResourceAsStream("/url.properties"));
        String authenticateBasic = pro.getProperty("authenticateBasic").trim();

        String jsonStr = requestPost(authenticateBasic, body);
        JSONObject obj = new JSONObject(jsonStr);
        if (obj == null)
            return;
        JSONObject data = obj.getJSONObject("data");
        if (data == null)
            return;
        Cookie cookie = new Cookie("Authorization", data.getString("id_token"));
        res.addCookie(cookie);
    }

    public String requestPost(String url, String body) {
        CloseableHttpClient httpclient = null;
        HttpPost httppost = null;
        try {
            httpclient = HttpClientBuilder.create().build();
            httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(body));
            httppost.setHeader("Content-Type", "application/json");

            CloseableHttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            messageUtil.error("security_cookie_error");
        } finally {
            try {
                if (httppost != null) {
                    httppost.releaseConnection();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                messageUtil.error("security_cookie_error");
            }
        }
        return "";
    }

    public void addErrorMessage(HttpServletResponse response, int errorCode, String msgKey){
        response.setStatus(errorCode);
        messageUtil.error(msgKey);
    }
    
    public Authentication authenticate(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("j_username");
        if (userName == null) {
            addErrorMessage(response, 401, "security_username_not_found");
            logger.error("userName can't be null !");
            return null;
        }
        try {
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                updateLoginStatus(userName, false);
                messageUtil.error("security_error");
            }
            return authResult;
        } catch (UsernameNotFoundException e) {
            addErrorMessage(response, 401, "security_username_not_found");
        } catch (DisabledException e) {
            addErrorMessage(response, 401, "security_account_disabled");
        } catch (LockedException e) {
            addErrorMessage(response, 403, "AccountLocked");
        } catch (AccountExpiredException e) {
            addErrorMessage(response, 401, "security_account_expired");
        } catch (CredentialsExpiredException e) {
            addErrorMessage(response, 401, "security_account_credentials_expired");
        } catch (AccountStatusException e) {
            addErrorMessage(response, 401, "security_account_status_invalid");
        } catch (BadCredentialsException e) {
            addErrorMessage(response, 401, "security_account_status_invalid");
            updateLoginStatus(userName, false);
        } catch (AuthenticationException e) {
            addErrorMessage(response, 401, "login_error");
            updateLoginStatus(userName, false);
        }
        return null;
    }

    public void saveToContext(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContextRepository securityContextRepository = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance()).getBean(SecurityContextRepository.class);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);
        String userName = request.getParameter("j_username");
        String originPwd = request.getParameter("j_password");
        try {
            sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        securityContext.setAuthentication(authResult);
        securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
        }
    }

    protected boolean beforeLogin(String password) {
        return true;
    }
    
    public void doLogin() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        String userName = request.getParameter("j_username");
        //String passwordPlain = request.getParameter("j_password");
        String tempPasswordPlain = request.getParameter("j_password");
        String passwordPlain = B64Code.decode(tempPasswordPlain, "utf-8");

        if ("".equals(userName) || userName == null) {
            userName = request.getParameter("input_j_username");
            passwordPlain = request.getParameter("input_j_password");
        }
        if (passwordPlain == null){
            addErrorMessage(response, 401, "login_error");
            return;
        }

        user = userAccountDao.getByLoginName(userName);
        if(user==null){
            String geLoginName = "ge" + userName;
            user = userAccountDao.getByLoginName(geLoginName);
            if (user != null) {
                try {
                	user.setGeSSO(geSsoService.authenticate(userName, passwordPlain));
                	user.setPlainPassword(passwordPlain);
                } catch (Exception e) {
                    logger.warn("Fail to valid by ge sso " + e.getMessage());
                }
            }
        }
        else{
        	user.setPlainPassword(passwordPlain);
        }
        if(user==null){
            addErrorMessage(response, 401, "login_error");
            return;
        }

        if(user.getIsLocked()!=null && user.getIsLocked()==true){
            if(TimeUtil.timeNow().minusHours(24).isBefore(TimeUtil.toJodaDate(user.getLockTime()))){
                addErrorMessage(response, 403, "AccountLocked");
                return;
            }
            user.setIsLocked(false);
            user.setPasswordErrorCount(0);
        }
        String passwordEncrypted = null;
        if(!user.isGeSSO()){
        	passwordEncrypted = onPasswordEncrypted(userName, passwordPlain);
        	if(!user.getPassword().equals(passwordEncrypted)){
                updateLoginStatus(userName, false);
                addErrorMessage(response, 401, "login_error");
                return;
            }
        	if(!beforeLogin(passwordPlain)){
        		return;
        	}
        }
        doSpringSecuritylogin(request, response, user);
    }

    public void doSpringSecuritylogin(HttpServletRequest request, HttpServletResponse response, UserAccount user) {
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);
        Map<String, String[]> extraParams = new TreeMap<>();

        try {
            extraParams.put("j_username", new String[] { user.getLoginName() });
            extraParams.put("j_password", new String[] { user.getPassword() });

            HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
            request = wrappedRequest;

            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                    updateLoginStatus(user.getLoginName(), false);
                    messageUtil.error("security_error");
                    return;
            }
            try{
                    sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
            }
            catch(Exception ex){
            }

            // below : do the same thing as in
            // AbstractAuthenticationProcessingFilter.successfulAuthentication(),
            // except for the redirection to the login success URL that is
            // managed by JSF
            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
            }
//			try {
//				setCookieForUser(response, userName, originPwd);
//			} catch (Exception e) {
//				messageUtil.error("security_cookie_error");
//			}
            afterLogin(user);
            updateLoginStatus(user.getLoginName(), true);
			/*String accessToken = msUtil.getAuthenticateByUserPassword(userName, originPwd);
			request.getSession().setAttribute("ACCESS_TOKEN", accessToken);*/
            // redirects to the home page
            /*
             * FacesContext context = FacesContext.getCurrentInstance();
             * ConfigurableNavigationHandler handler =
             * (ConfigurableNavigationHandler)
             * context.getApplication().getNavigationHandler();
             * handler.performNavigation("/home.xhtml?faces-redirect=true");
             */
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            addErrorMessage(response, 401, "login_error");
        } catch (DisabledException e) {
            addErrorMessage(response, 401, "security_account_disabled");
        } catch (LockedException e) {
            addErrorMessage(response, 403, "AccountLocked");
        } catch (AccountExpiredException | CredentialsExpiredException e) {
            addErrorMessage(response, 401, "security_account_expired");
        } catch (AccountStatusException e) {
            addErrorMessage(response, 401, "AccountLocked");
        } catch (AuthenticationException e) {
            addErrorMessage(response, 401, "login_error");
            updateLoginStatus(user.getLoginName(), false);
        } finally {
            securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        }
    }

    public void updateLoginStatus(String userName, boolean loginStatus) {
        if (userName == null || StringUtils.isEmpty(userName)) {
            logger.error("loginName:{} invalid !", userName);
            messageUtil.error("security_bad_credentials");
            return;
        }
        if (user == null) user = userAccountDao.getByLoginName(userName);

        if (user == null) {
            messageUtil.error("security_bad_credentials");
            logger.error("can't find user by loginName:{}", userName);
            return;
        }
/*		if(!user.getIsActive()){
			messageUtil.error("AccountNotActivated");
			return;
		}*/
        if (user.getIsLocked() != null && user.getIsLocked()) {
            messageUtil.error("AccountLocked");
            return;
        }
        if (loginStatus) {
            user.setLastLoginTime(new Date());
            user.setPasswordErrorCount(0);
            user.setIsLocked(false);
            userAccountDao.save(user);
        } else {
            Integer errorTimes = user.getPasswordErrorCount();
            if (errorTimes == null) {
                errorTimes = 0;
            }
            AtomicInteger ai = new AtomicInteger(errorTimes);
            ai.getAndAdd(1);
            errorTimes = ai.get();
            if (errorTimes >= PASSWORD_ERROR_TIME_LIMIT) {
                user.setIsLocked(true);
                user.setLockTime(new Date());
                userAccountDao.save(user);
                logger.error("user:{} is locked because of password error times reach the limit !", userName);
                messageUtil.error("AccountLocked");
                return;
            }
            user.setPasswordErrorCount(errorTimes);
            userAccountDao.save(user);
            messageUtil.error("security_bad_credentials");
        }
    }
}
