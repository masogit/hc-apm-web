package com.ge.apm.service.difyHttp;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.difyChat.DiFyChatRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import webapp.framework.web.WebUtil;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Log4j2
public class DifyStreamService {

    public String processDifyStream(DiFyChatRequest chatRequest) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = SSLUtils.getIgnoreSSLRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer app-k9zaJQ2wAqEV5oI7vie2M5wU");
        JSONObject requestBody = new JSONObject();
        requestBody.put("inputs", new ArrayList<>());
        requestBody.put("query", chatRequest.getParamStr());
        requestBody.put("response_mode", "streaming");
        requestBody.put("conversation_id", "");
        requestBody.put("user", chatRequest.getUserName());
        StringBuffer result = new StringBuffer();
        // 使用RestTemplate执行流式请求
        restTemplate.execute(
                "https://apm-dify-stg.gehealthcloud.cn/v1/chat-messages",
                HttpMethod.POST,
                request -> {
                    request.getHeaders().putAll(headers);
                    String bodyStr = requestBody.toJSONString();
                    request.getBody().write(bodyStr.getBytes(StandardCharsets.UTF_8));
                },
                response -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                String data = line.substring(6);
                                if (data.equals("[DONE]")) {
                                    break;
                                }
                                processStreamData(JSONObject.parseObject(data), result);
                            }
                        }
                    } catch (Exception e) {
                        log.error("请求Dify异常!原因:{}", e.getMessage());
                    }
                    return null;
                }
        );
        return result.toString();
    }

    private void processStreamData(JSONObject jsonData, StringBuffer result) {
        try {
            // 处理不同类型的流数据
            if (jsonData.containsKey("answer")) {
                String answer = jsonData.getString("answer");
                result.append(answer);
            } else if (jsonData.containsKey("event")) {
                String event = jsonData.getString("event");
                if ("message_end".equals(event)) {
                    log.info("--- 回答结束 ---");
                }
            }
        } catch (Exception e) {
            log.error("解析流数据失败!原因:{}", e.getMessage());
        }
    }
}
