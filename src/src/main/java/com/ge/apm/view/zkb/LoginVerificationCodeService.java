package com.ge.apm.view.zkb;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LoginVerificationCodeService extends MicroServiceInvoker {

@Autowired
private UserAccountRepository userAccountDao;

    public ResponseEntity<LinkedHashMap> sendTheVerificationCode(String telephone,String email ) {
        String url = getGatewayURL(GatewayURL.SMS_VERIFICATION_CODE);
        HttpHeaders header = createHttpHeaders(null);
        HttpEntity<Map> requestEntity = new HttpEntity<>(new HashMap(), header);
        requestEntity.getBody().put("telephone", telephone);
        requestEntity.getBody().put("mailboxNumber", email);
        return template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
    }
    public ResponseEntity<LinkedHashMap> sendMail(String verificationCode,String telephone) {
        String url = getGatewayURL(GatewayURL.SMS_SEND_MAIL);
        HttpHeaders header = createHttpHeaders(null);
        HttpEntity<Map> requestEntity = new HttpEntity<>(new HashMap(), header);
        requestEntity.getBody().put("verificationCode", verificationCode);
        requestEntity.getBody().put("telephone", telephone);
        UserAccountService userAccountService = WebUtil.getBean(UserAccountService.class);
        UserAccount user = userAccountService.getUserByTelephone(telephone);
        user.setPlainPassword(PasswordUtil.DEFAULT_PWD);
        try {
            user.entryptPassword();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        requestEntity.getBody().put("password", PasswordUtil.DEFAULT_PWD);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        if(response.getBody() != null && "OK".equals(response.getBody().get("bizStatusCode"))){
            user.setIsLocked(false);
            userAccountDao.save(user);
        }
        return response;
    }
}
