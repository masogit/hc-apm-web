package com.ge.apm.web.cboard;

import com.ge.apm.service.security.TokenProvider;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class CBoardController {
    private static final Logger logger = LoggerFactory.getLogger(CBoardController.class);
    Pattern pattern = Pattern.compile("(https?://.*?)/.*?");

    public static final String LOGIN_NAME_KEY = "login_name";

    @RequestMapping("/bi")
    public String redirect() throws UnsupportedEncodingException {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String url = request.getRequestURL().toString();
        logger.info("apm web url, {}", url);

        String cBoardUrl = getCBoardUrl(url);

        String loginName = (String) request.getSession().getAttribute(LOGIN_NAME_KEY);

        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.encode(loginName);

        String resultUrl = cBoardUrl + "?Token=" + token;

        //response.sendRedirect(redirectUrl);

        return "redirect:" + resultUrl;
    }

    public String getCommonUrl(String url) {
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;

    }

    public String getCBoardUrl(String url) {

        String commonUrl = getCommonUrl(url);

        String cboardUrl = commonUrl + "/bi/login.html";

        //SIT, Prod use /bi link
        if (isUrlOk(cboardUrl)) {
            return commonUrl + "/bi";
        }

        //DEV use /hc-apm-cboard link
        cboardUrl = commonUrl + "/hc-apm-cboard/login.html";

        if (isUrlOk(cboardUrl)) {
            return commonUrl + "/hc-apm-cboard";
        }

        return commonUrl + "/bi";

    }

    public boolean isUrlOk(String url) {
        HttpGet httpGet = new HttpGet(url);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

            return false;
        }

        int statusCode = response.getStatusLine().getStatusCode();

        if ( statusCode >= 200 && statusCode < 300) {
            return true;
        }

        return false;
    }
}
