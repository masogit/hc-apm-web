package webapp.framework.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

@WebFilter(filterName = "SessionTimeoutFilter", urlPatterns = "/*")
public class SessionTimeoutFilter implements Filter {

    private static final String loginPage = "login.xhtml";
    private static final String reloginPage = "login.xhtml";
    private static final String phoneValidPage = "portal/login/phoneValid.xhtml";
    private static final String redirectURL ="%s/" + loginPage + "?session_expired=1";
    private static final String redirectXML = "<?xml version='1.0' encoding='UTF-8'?>" 
                + "<partial-response>" 
                + "<redirect url=\"%s/" + loginPage + "?session_expired=1\"/>" 
                + "</partial-response>";

    private static final String redirectPhoneValidURL ="%s/" + phoneValidPage + "?session_expired=1";
    private static final String redirectPhoneValidXML = "<?xml version='1.0' encoding='UTF-8'?>"
            + "<partial-response>"
            + "<redirect url=\"%s/" + phoneValidPage + "?session_expired=1\"/>"
            + "</partial-response>";
    private static final String resources = "javax.faces.resource";
    private static final String sso_login = "api/a1sso";

    private PassLoginUtil passLoginUtil;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        passLoginUtil = WebUtil.getBean(PassLoginUtil.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //System.out.println("doFilter request URI = " + httpServletRequest.getRequestURI());
        if (isSessionControlRequired(httpServletRequest)) {
            if(isAJAXRequest(httpServletRequest)){
                boolean notLogin = isNotLogin(httpServletRequest);
                if(notLogin){
                    httpServletResponse.getWriter().print(String.format(redirectXML, httpServletRequest.getContextPath()));
                    httpServletResponse.flushBuffer();
                    return;
                }
                boolean notPhoneLogin = isNotPhoneLogin(httpServletRequest);
                if(notPhoneLogin){
                    httpServletResponse.getWriter().print(String.format(redirectPhoneValidXML, httpServletRequest.getContextPath()));
                    httpServletResponse.flushBuffer();
                    return;
                }
            }
            else{
                // Non-Ajax Request
                if(isRequestedSessionInvalid(httpServletRequest)){
                    httpServletResponse.sendRedirect(String.format(redirectURL, httpServletRequest.getContextPath()));
                    return;
                }
                boolean notLogin = isNotLogin(httpServletRequest);
                String token = request.getParameter("token");
                String mobile = request.getParameter("mobile");
                if(notLogin && !StringUtils.isEmpty(token) && !StringUtils.isEmpty(mobile)){
                    passLoginUtil.handler(httpServletRequest,httpServletResponse);
                    httpServletRequest.getSession().setAttribute("PHONE_SMSCODE_VALID",true);
                    String requestURI = httpServletRequest.getRequestURI();
                    requestURI = requestURI.replaceAll("%23","#");
                    httpServletResponse.sendRedirect(requestURI);
                    return;
                }
                boolean notPhoneLogin = isNotPhoneLogin(httpServletRequest);
                String requestPath = httpServletRequest.getRequestURI();
                if(!notLogin && notPhoneLogin && !requestPath.contains(phoneValidPage)){
                    httpServletResponse.sendRedirect(String.format("%s/"+phoneValidPage, httpServletRequest.getContextPath()));
                    return;
                }

            }
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isRootPath(String requestPath) {
        return (StringUtils.countOccurrencesOf(requestPath, "/") <= 2) && (StringUtils.countOccurrencesOf(requestPath, "/home")<1);
    }
    
    private boolean isSessionControlRequired(HttpServletRequest httpServletRequest) {
        String requestPath = httpServletRequest.getRequestURI();
        return !requestPath.contains(loginPage) && !requestPath.contains(resources) 
                && !requestPath.contains(sso_login) && !isRootPath(requestPath);
    }

    private boolean isNotLogin(HttpServletRequest httpServletRequest) {
        if(httpServletRequest.getSession()==null) return true;

        return (httpServletRequest.getSession().getAttribute("SPRING_SECURITY_CONTEXT")==null);
    }
    private boolean isNotPhoneLogin(HttpServletRequest httpServletRequest) {
        if(httpServletRequest.getSession()==null) return true;

        return (httpServletRequest.getSession().getAttribute("PHONE_SMSCODE_VALID")==null);
    }


    private boolean isRequestedSessionInvalid(HttpServletRequest httpServletRequest) {
        String reqID = httpServletRequest.getRequestedSessionId();
        if (reqID == null) return false;

        HttpSession session = httpServletRequest.getSession(false);
        if (session==null) return false;
        try {
          session.getCreationTime();
        } catch (IllegalStateException ex) {
          return false;
        }
        //if(isNotLogin(httpServletRequest)) return true;
        return false;
        //return !httpServletRequest.isRequestedSessionIdValid();
    }

    private boolean isAJAXRequest(HttpServletRequest request) {
        return  "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @Override
    public void destroy() {
    }

}
