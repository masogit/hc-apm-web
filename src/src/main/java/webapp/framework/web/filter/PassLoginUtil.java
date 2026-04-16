package webapp.framework.web.filter;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.cboard.CBoardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import webapp.framework.context.WrappedRequest;
import webapp.framework.dao.SearchFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class PassLoginUtil {
    private static final Logger log = LoggerFactory.getLogger(PassLoginUtil.class);

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private MicroServiceUtil microServiceUtil;

    @Autowired
    private RestTemplate template;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

    public void handler(HttpServletRequest request,HttpServletResponse response){
        String token = request.getParameter("token");
        String mobile = request.getParameter("mobile");
        log.info("token:{},mobile:{}",token,mobile);
        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(mobile)) return;
        UserAccount systemClientInfo = getUserOrSysClient(token);
        UserAccount userAccount = getUserAccount(systemClientInfo, mobile);
        if(userAccount == null) {
            log.info("为查询到用户信息");
            return;
        }
        loginWeb(request,response,userAccount);

    }


    /**
     *  获取 system_client_info
     * @param token
     * @return
     */
    public UserAccount getUserOrSysClient(String token){
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Authorization","Bearer "+token);
        String gatewayURL = microServiceUtil.getGatewayURL(GatewayURL.GET_APMUSER_OR_SYSCLIENT);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, entity, LinkedHashMap.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return JSON.parseObject(JSON.toJSONString(responseMap.get("data")), UserAccount.class);
        }
        return null;
    }

    /**
     *  获取登录用户
     * @param systemClientInfo
     * @param tel
     * @return
     */
    public UserAccount getUserAccount(UserAccount systemClientInfo,String tel){
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,systemClientInfo.getTenantUID()));
        searchFilterList.add(new SearchFilter("telephone", SearchFilter.Operator.EQ,tel));
        List<UserAccount> userAccounts = userAccountRepository.findBySearchFilter(searchFilterList);
        if(!CollectionUtils.isEmpty(userAccounts)){
          return   userAccounts.get(0);
        }
        return null;
    }

    public void  loginWeb(HttpServletRequest request,HttpServletResponse response,UserAccount reqUser){
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);
        Map<String, String[]> extraParams = new TreeMap<>();
        String userAgent = request.getHeader("User-Agent");
        try {
            String loginName = reqUser.getLoginName();
            extraParams.put("j_username", new String[] { loginName});
            extraParams.put("j_password", new String[] { reqUser.getPassword()});
            HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
            request = wrappedRequest;
            usernamePasswordAuthenticationFilter.setPostOnly(false);
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                // Authentication  FAILED
                return ;
            }
            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            long l = System.currentTimeMillis();
            LinkedHashMap<String, Object> responseEntity = doGetAuthenticateByUserPassword(loginName, reqUser.getPassword(), false, userAgent, request);
            UserContextService.setLoginUser(reqUser);
            request.getSession().setAttribute(CBoardController.LOGIN_NAME_KEY, loginName);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        }
    }
    private  LinkedHashMap<String, Object> doGetAuthenticateByUserPassword(String userName, String password, boolean isPasswordEncrypted, String userAgent, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        headers.setContentType(type);
        headers.add("User-Agent", userAgent);
        headers.add("request-source", "legacy");
        Map<String, String> postParameters = new HashMap<String, String>();
        postParameters.put("loginName", userName);
        postParameters.put("password", password);

        HttpEntity<Map> requestEntity = new HttpEntity<Map>(
                postParameters, headers);

        String gatewayURL;
        if (isPasswordEncrypted) {
            gatewayURL =microServiceUtil.getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_USER_PASSWORD_ENCRYPTED);
        }else {
            gatewayURL = microServiceUtil.getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_USER_PASSWORD);
        }
        log.info("url:{}",gatewayURL);
        LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        log.info("response:{}",response);
        if (isOkay(response)) {
            responseMap  = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            if (null != data && data.containsKey("jwtToken")) {
                LinkedHashMap<String, Object> jwtToken = (LinkedHashMap<String, Object>) data.get("jwtToken");
                if (null != jwtToken && jwtToken.containsKey("id_token")) {
                    request.getSession().setAttribute("ACCESS_TOKEN", (String) jwtToken.get("id_token"));
                }
            }
        }
        log.info("doGetAuthenticateByUserPassword end :{}",responseMap);
        return responseMap;

    }
    private Boolean isOkay(ResponseEntity res) {

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();

        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }
}
