package com.ge.apm.web.cboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CBoardController {
    private static final Logger logger = LoggerFactory.getLogger(CBoardController.class);

    @RequestMapping("/bi")
    public String redirect()
    {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String url = request.getRequestURL().toString();
        logger.info("cboard url, {}", url);

        //response.sendRedirect(redirectUrl);

//        return "redirect:" + request.getRequestURL().toString();
        return null;
    }
}
