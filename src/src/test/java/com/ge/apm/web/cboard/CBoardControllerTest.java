package com.ge.apm.web.cboard;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CBoardControllerTest {

    @Test
    public void redirect() {
    }

    @Test
    @Ignore
    public void getCommonUrl() throws IOException {

        CBoardController controller = new CBoardController();
        String url = controller.getCommonUrl("http://apm-dev.hcdigital.com.cn/geapm/web/bi");

        assertEquals("http://apm-dev.hcdigital.com.cn", url);

//        String cboardUrl = url + "/bi/login.html";
        String cboardUrl = url + "/hc-apm-cboard/login.html";
        HttpGet httpOptions = new HttpGet(cboardUrl);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(httpOptions);
        assertEquals(200, response.getStatusLine().getStatusCode());

    }

    @Test
    public void getCommonUrlForHttps() throws IOException {

        CBoardController controller = new CBoardController();
        String url = controller.getCommonUrl("https://apm-dev.hcdigital.com.cn/geapm/web/bi");

        assertEquals("https://apm-dev.hcdigital.com.cn", url);

    }

    @Test
    @Ignore
    public void getCboardUrlForDev() throws IOException {

        CBoardController controller = new CBoardController();
        String url = controller.getCBoardUrl("http://apm-dev.hcdigital.com.cn/geapm/web/bi");

        assertEquals("http://apm-dev.hcdigital.com.cn/hc-apm-cboard", url);

    }

    @Test
    @Ignore
    public void getCboardUrlForSIT() throws IOException {

        CBoardController controller = new CBoardController();
        String url = controller.getCBoardUrl("http://apm.hcdigital.com.cn/geapm/web/bi");

        assertEquals("http://apm.hcdigital.com.cn/bi", url);

    }

    @Test
    @Ignore
    public void getCboardUrlForStaging() throws IOException {

        CBoardController controller = new CBoardController();
        String url = controller.getCBoardUrl("http://apm-stg.gechs.cn/geapm/web/bi");

        assertEquals("http://apm-stg.gechs.cn/bi", url);

    }
}