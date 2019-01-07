package webapp.framework.web.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeSsoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeSsoService.class);

    private final static String BODY_TEMPLATE = "TARGET=https%3A%2F%2Frsologin.ssogen2.corporate.ge.com%2FRSOZone%2Frsozone.do%3F" +
        "interimTARGET%3Dhttps%3A%2F%2Fsmloginmap.ssogen2.corporate.ge.com%2Fsmzone%2Fsessionmap.jsp%3F" +
        "OrigTARGET%3D&smauthreason=&clientfp=&smtryno=0&";

    public static final String URL = "https://ssologin.ssogen2.corporate.ge.com/SSOLogin/verify.fcc";

    private String getRequestBody(String username, String password) {
        String REQUEST_CREDENTIALS = "username=%s&password=%s&SMAGENTNAME=null&POSTPRESERVATIONDATA=";
        return BODY_TEMPLATE + String.format(REQUEST_CREDENTIALS, username, password);
    }


    public boolean authenticate(String username, String password) {
        LOGGER.info("User {} is trying SSO login.", username);

        try {
            HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
            HttpPost request = new HttpPost(URL);

            StringEntity se = new StringEntity(getRequestBody(username, password));
            request.setEntity(se);

            HttpResponse response = client.execute(request);

            Header[] headers = response.getHeaders("Set-Cookie");
            for (Header header : headers) {
                System.out.println("Key : " + header.getName() + " ,Value : " + header.getValue());
                if (StringUtils.startsWith(header.getValue(), "JSESSIONID=")) {
                    LOGGER.info("User {} passed SSO login.", username);
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Fail to authenticate {}", username, e);
        }
        LOGGER.info("User {} failed SSO login.", username);
        return false;
    }
}
