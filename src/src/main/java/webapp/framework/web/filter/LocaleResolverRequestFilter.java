package webapp.framework.web.filter;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import webapp.framework.context.LocaleHolder;
import webapp.framework.web.WebUtil;

/**
 * Set the current {@link Locale} in the {@link LocaleHolder}
 */
public class LocaleResolverRequestFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(LocaleResolverRequestFilter.class);

    public static final String LOCALE_PARAMETER = "locale";

    private LocaleResolver localeResolver;

    @Override
    protected void initFilterBean() throws ServletException {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        localeResolver = context.getBean(LocaleResolver.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        forceLocaleFromParameter(request, response);
        try {
            LocaleHolder.setLocale(localeResolver.resolveLocale(request));
            filterChain.doFilter(request, response);
        } finally {
            LocaleHolder.resetLocaleContext();
        }
    }

    private void forceLocaleFromParameter(HttpServletRequest request, HttpServletResponse response) {
        //如果用户在浏览器端设置了locale,以用户设置为主，否则，以租户上的默认语言为主
        Cookie[] cookies = request.getCookies();
        boolean carryCookie = false;
        if(cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(LOCALE_PARAMETER)){
                    carryCookie = true;
                }
            }
        }
        String localeParameter = null;
        try {
            localeParameter = request.getParameter(LOCALE_PARAMETER);
            if (isNotBlank(localeParameter)) {
                Locale locale = LocaleUtils.toLocale(localeParameter);
                log.debug("forcing locale to {}", locale.getLanguage());
                localeResolver.setLocale(request, response, locale);
            }else if(request.getUserPrincipal() != null && !carryCookie ){
                //优先获取用户设置的默认语言，如果没有设置，就用租户上的
                WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
                UserAccountService userAccountService = context.getBean(UserAccountService.class);
                String loginName = request.getUserPrincipal().getName();
                String defaultLanguage = userAccountService.getDefaultLanguage(loginName);
                if(StringUtils.isEmpty(defaultLanguage)){
                    defaultLanguage = userAccountService.getUserLanguageByLoginName(loginName);
                }
                if(isNotBlank(defaultLanguage)){
                    Locale locale = LocaleUtils.toLocale(defaultLanguage);
                    log.debug("forcing locale to tenant {}", locale.getLanguage());
                    localeResolver.setLocale(request, response, locale);
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("Locale " + localeParameter + " is not valid");
        }
    }
}
