package webapp.framework.util;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Yangfan, Liu
 */
public class SessionCache {
    private static Map<String, HttpSession> sessionCache = new HashMap<>();

    public static void putSession(HttpSession session) {
        Optional.ofNullable(session.getAttribute("userContextService"))
                .flatMap(c -> Optional.ofNullable(((UserContextService) c).getLoginUser()))
                .ifPresent(u -> sessionCache.put(u.getUid(), session));
    }

    public static void moveSession(HttpSession session) {
        Optional.ofNullable(session.getAttribute("userContextService"))
                .flatMap(c -> Optional.ofNullable(((UserContextService) c).getLoginUser()))
                .ifPresent(u -> sessionCache.remove(u.getUid()));
    }

    public static void destroyedSession(UserAccount user) {
        Optional.ofNullable(sessionCache.get(user.getUid())).ifPresent(HttpSession::invalidate);
    }
}
