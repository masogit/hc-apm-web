package com.ge.apm.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.web.cboard.CBoardController;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import webapp.framework.context.WrappedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Controller
@RequestMapping("/dingTalk")
public class LoginDingTalkController {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

    @Autowired
    private MicroServiceUtil microServiceUtil;

    @Autowired
    protected RestTemplate restTemplate;

    @RequestMapping(method = RequestMethod.POST, value = "/ssoLogin")
    @ResponseBody
    public ResponseEntity<Map> doLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> param) {
        try {

            Map<String, Object> responseBody = doGetAuthenticateByDingTalkId(String.valueOf(param.get("userId")), request);
            Object obj = ((((Map) (((Map) responseBody.get("data"))).get("userProfile"))).get("userAccount"));
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            UserAccount userAccount = objectMapper.convertValue(obj, UserAccount.class);
            if (userAccount == null) {
                return ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
                        .put("message", "账户未绑定钉钉，请使用手机APP登录绑定")
                        .put("bizStatusCode", "FAILED")
                        .build());
            }
            UserAccount dbUser = userAccountRepository.findByLoginName(userAccount.getLoginName());
            HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
            SecurityContext securityContext = securityContextRepository.loadContext(holder);
            SecurityContextHolder.setContext(securityContext);
            Map<String, String[]> extraParams = new TreeMap<>();
            String loginName = dbUser.getLoginName();
            String password = dbUser.getPassword();
            extraParams.put("j_username", new String[]{loginName});
            extraParams.put("j_password", new String[]{password});
            HttpServletRequest wrappedRequest = new WrappedRequest(request, extraParams);
            request = wrappedRequest;
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                return ResponseEntity.ok().body(new ImmutableMap.Builder<String, Object>()
                        .put("message", "Authentication  FAILED")
                        .put("bizStatusCode", "FAILED")
                        .build());
            }
            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            request.getSession().setAttribute(CBoardController.LOGIN_NAME_KEY, userAccount.getLoginName());
            request.getSession().setAttribute("PHONE_SMSCODE_VALID", true);
            response.setHeader("JSESSIONID", request.getSession().getId());
            return ResponseEntity.ok().body(
                    responseBody
            );
        } catch (Exception ex) {
            log.info("ssoLogin err: {}" ,ex);
            return ResponseEntity.badRequest().body(new ImmutableMap.Builder<String, Object>()
                    .put("message", ex.getMessage())
                    .put("bizStatusCode", "ERROR")
                    .build());
        }
    }

    private Map<String, Object> doGetAuthenticateByDingTalkId(String userId, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("User-Agent", request.getHeader("User-Agent"));
        headers.add("request-source", "legacy");
        HttpEntity<Map> requestEntity = new HttpEntity<Map>(
                new ImmutableMap.Builder<String, Object>()
                        .put("userId", userId).build()
                , headers);

        String gatewayURL;
        gatewayURL = microServiceUtil.getGatewayURL(GatewayURL.GET_AUTHENTICATE_BY_DINGTALK_ID);
        LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
        ResponseEntity<LinkedHashMap> response = restTemplate.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            responseMap = (LinkedHashMap<String, Object>) response.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) responseMap.get("data");
            if (null != data && data.containsKey("jwtToken")) {
                LinkedHashMap<String, Object> jwtToken = (LinkedHashMap<String, Object>) data.get("jwtToken");
                if (null != jwtToken && jwtToken.containsKey("id_token")) {
                    request.getSession().setAttribute("ACCESS_TOKEN", (String) jwtToken.get("id_token"));
                }
            }
        }
        return responseMap;
    }

    protected Boolean isOkay(ResponseEntity res) {
        if (!res.getStatusCode().equals(HttpStatus.OK)) {
            return false;
        }
        Map<String, Object> body = (Map<String, Object>) res.getBody();
        return body.containsKey("bizStatusCode") && body.get("bizStatusCode").equals("OK");
    }
}
