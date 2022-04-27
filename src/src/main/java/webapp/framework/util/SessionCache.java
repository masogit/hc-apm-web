package webapp.framework.util;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yangfan, Liu
 */
public class SessionCache {
    private static Map<String, HttpSession> sessionCache = new HashMap<>();

    public static void putSession(HttpSession session) {
        UserAccount user = ((UserContextService) session.getAttribute("userContextService")).getLoginUser();
        sessionCache.put(user.getUid(), session);
        System.out.println("session count: " + sessionCache.size());
    }

    public static void moveSession(HttpSession session) {
        UserAccount user = ((UserContextService) session.getAttribute("userContextService")).getLoginUser();
        sessionCache.remove(user.getUid());
        System.out.println("session count: " + sessionCache.size());
    }

    public static void destroyedSession(UserAccount user) {
        sessionCache.get(user.getUid()).invalidate();
    }
}
