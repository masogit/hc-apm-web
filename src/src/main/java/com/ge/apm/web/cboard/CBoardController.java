package com.ge.apm.web.cboard;

import com.ge.apm.service.security.TokenProvider;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class CBoardController {
    private static final Logger logger = LoggerFactory.getLogger(CBoardController.class);
    Pattern pattern = Pattern.compile("(https?://.*?)/.*?");

    @RequestMapping("/bi")
    public String redirect() throws UnsupportedEncodingException {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String url = request.getRequestURL().toString();
        logger.info("apm web url, {}", url);

        String commonUrl = getCommonUrl(url);

        String loginName = UserContextService.getCurrentUserAccount().getLoginName();

        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.encode(loginName);

        String resultUrl = commonUrl + "/hc-apm-cboard?token=" + token;

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
}
