package com.ge.apm.view.sysutil;

import com.ge.apm.dao.LoginRecordRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.LoginRecord;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.web.cboard.CBoardController;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import webapp.framework.web.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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

        if ("3".equals(userTenantType)) {
            logoutSuccessUrl = String.format("/loginzkb.xhtml?uid=%s", request.getParameter("uid"));
        } else {
            logoutSuccessUrl = String.format("/login.xhtml?uid=%s", request.getParameter("uid"));
        }

        if (auth != null) {
            UserAccount user = userAccountDao.getById(Integer.parseInt(request.getParameter("uid")));
            request.getSession().invalidate();
            request.getSession().setAttribute(CBoardController.LOGIN_NAME_KEY, "");
            saveLogoutRecord(user);
            super.onLogoutSuccess(request, response, auth);
        } else
            request.getRequestDispatcher(logoutSuccessUrl).forward(request, response);
    }

    private void saveLogoutRecord(UserAccount user) {
        try {
            LoginRecordRepository loginRecordRepository = WebUtil.getBean(LoginRecordRepository.class);
            LoginRecord oldLoginRecord = new LoginRecord();
            List<LoginRecord> list = loginRecordRepository.findByLoginName(user.getLoginName());
            if (CollectionUtils.isNotEmpty(list)) {
                oldLoginRecord = list.get(0);
            }

            LoginRecord newLoginRecord = oldLoginRecord;
            //先删除旧的登录记录，再更新登出时间
            loginRecordRepository.delete(oldLoginRecord);
            newLoginRecord.setIsDeleted(true);
            newLoginRecord.setLogoutTime(new Date());
            loginRecordRepository.save(newLoginRecord);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected String determineTargetUrl(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) {
        return logoutSuccessUrl;
    }
}