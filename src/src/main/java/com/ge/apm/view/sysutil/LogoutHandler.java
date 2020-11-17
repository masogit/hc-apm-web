package com.ge.apm.view.sysutil;

import com.ge.apm.dao.LoginRecordRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.LoginRecord;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.web.cboard.CBoardController;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
  private static final Logger log = LoggerFactory.getLogger(LogoutHandler.class);
  private static String logoutSuccessUrl = "/login.xhtml";
  @Autowired
  protected UserAccountRepository userAccountDao;
  public static void setLogoutSuccessUrl(String _logoutSuccessUrl) {
    logoutSuccessUrl = _logoutSuccessUrl;
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
    throws IOException, ServletException {
    String userTenantType = request.getParameter("userTenantType");

    if("3".equals(userTenantType)){
      logoutSuccessUrl = String.format("/loginzkb.xhtml?uid=%s", request.getParameter("uid"));
    }else {
      logoutSuccessUrl = String.format("/login.xhtml?uid=%s", request.getParameter("uid"));
    }

    if (auth != null) {
      //UserAccount user = userAccountDao.getById(Integer.parseInt(request.getParameter("uid")));
      request.getSession().invalidate();
      request.getSession().setAttribute(CBoardController.LOGIN_NAME_KEY, "");
      //saveLogoutRecord(request,user);
      super.onLogoutSuccess(request, response, auth);
    } else
      request.getRequestDispatcher(logoutSuccessUrl).forward(request, response);
  }

  private void saveLogoutRecord(HttpServletRequest request,UserAccount user) {
    try {
     String addr = request.getRemoteAddr();
      LoginRecordRepository loginRecordRepository = WebUtil.getBean(LoginRecordRepository.class);

      LoginRecord loginRecord =new LoginRecord();

      List<LoginRecord> list = loginRecordRepository.findListByLoginName
              (user.getLoginName());
      if(CollectionUtils.isNotEmpty(list)){
        loginRecord= list.get(0);
      }
      loginRecord.setLoginName(user.getLoginName());
      loginRecord.setLoginUrl("");
      //loginRecord.setLoginTime(new Date());
      loginRecord.setLoginIp(addr);
      loginRecord.setIsDeleted(true);
      loginRecordRepository.save(loginRecord);
    }catch (Exception ex) {
      java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  @Override
  protected String determineTargetUrl(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
    return logoutSuccessUrl;
  }
}