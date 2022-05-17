package com.ge.apm.web;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceUtil;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import webapp.framework.context.WrappedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
@Validated
public class AuditLoginController {
    private static final Logger log = LoggerFactory.getLogger(AuditLoginController.class);
    @Autowired
    private SecurityContextRepository securityContextRepository;
    @Autowired
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
    @Autowired
    private MicroServiceUtil microServiceUtil;
    @Autowired
    private UserAccountRepository userAccountDao;

    @Autowired
    private RestTemplate template;

//    @CrossOrigin(allowCredentials="true",
//            allowedHeaders="*",
//            methods= {
//                    RequestMethod.GET,
//                    RequestMethod.POST,
//                    RequestMethod.DELETE,
//                    RequestMethod.OPTIONS,
//                    RequestMethod.HEAD,
//                    RequestMethod.PUT,
//                    RequestMethod.PATCH},
//            origins="*")
    @RequestMapping(method = RequestMethod.POST,value = "/login")
    @ResponseBody
    public  ResponseEntity<LinkedHashMap>  doLogin(HttpServletRequest request, HttpServletResponse response,@RequestBody UserAccount reqUser){
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);
        Map<String, String[]> extraParams = new TreeMap<>();
        String userAgent = request.getHeader("User-Agent");
        try {
            String loginName = reqUser.getLoginName();
            String password = atob(reqUser.getPassword());
            UserAccount  user = userAccountDao.getByLoginName(loginName);
            if(user == null){
                return setResultFailed("用户名或密码有误");
            }
            extraParams.put("j_username", new String[] { user.getLoginName()});
            extraParams.put("j_password", new String[] { user.getPassword()});
            HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
            request = wrappedRequest;
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                return setResultFailed("Authentication  FAILED");
            }
            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            long l = System.currentTimeMillis();
            LinkedHashMap<String, Object> responseEntity = doGetAuthenticateByUserPassword(loginName, password, false, userAgent, request);
            log.info("doLogin end :{}",responseEntity);
            return ResponseEntity.ok(responseEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return setResultFailed(e.getMessage());
        }finally {
            securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        }
    }
    private ResponseEntity<LinkedHashMap> setResultFailed(String message){
        LinkedHashMap<String, Object> result=new LinkedHashMap<>();
        result.put("bizStatusCode","FAILED");
        result.put("message",message);
        result.put("referencesURLs","");
        result.put("data","");
        result.put("rowCount","");
        return ResponseEntity.ok(result);
    }
    private  LinkedHashMap<String, Object> doGetAuthenticateByUserPassword(String userName, String password, boolean isPasswordEncrypted, String userAgent, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
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
    protected Boolean isOkay(ResponseEntity res) {

        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) res.getBody();

        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }
    private static String base64hash = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static String atob(String inStr) {
        if (inStr == null)
            return null;
        inStr = inStr.replaceAll("\\s|=", "");
        StringBuilder result = new StringBuilder();
        int cur;
        int prev = -1;
        int mod;
        int i = 0;
        while (i < inStr.length()) {
            cur = base64hash.indexOf(inStr.charAt(i));
            mod = i % 4;
            switch (mod) {
                case 0:
                    break;
                case 1:
                    result.append(String.valueOf((char) (prev << 2 | cur >> 4)));
                    break;
                case 2:

                    result.append(String.valueOf((char) ((prev & 0x0f) << 4 | cur >> 2)));
                    break;
                case 3:

                    result.append(String.valueOf((char) ((prev & 3) << 6 | cur)));
                    break;
            }
            prev = cur;
            i++;
        }
        return result.toString();
    }
}
