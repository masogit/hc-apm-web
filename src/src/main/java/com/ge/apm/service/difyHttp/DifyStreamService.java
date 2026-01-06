package com.ge.apm.service.difyHttp;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.web.difyChat.DiFyChatRequest;
import com.ge.apm.web.difyChat.DiFyChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DifyStreamService {

    private static final Logger log = LoggerFactory.getLogger(DifyStreamService.class);

    public DiFyChatResponse processDifyStream(DiFyChatRequest chatRequest) throws Exception {
        log.info("start post dify...");
        RestTemplate restTemplate = new RestTemplate();//SSLUtils.getIgnoreSSLRestTemplate();
        log.info("restTemplate get success...");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer app-OJccYvgBi32jaw75uPygpHeA");
        JSONObject requestBody = new JSONObject();
//        JSONObject inputs = new JSONObject();
//        inputs.put("knowledge", chatRequest.getSelectStorage());
        requestBody.put("inputs", new ArrayList<>());
        requestBody.put("query", chatRequest.getParamStr());
        requestBody.put("response_mode", "streaming");
        requestBody.put("conversation_id", chatRequest.getConversationId());
        requestBody.put("user", chatRequest.getUserName());
        StringBuffer result = new StringBuffer();
        log.info("start restTemplate post...");
        AtomicReference<String> conversationId = new AtomicReference<>();
        // 使用RestTemplate执行流式请求
        restTemplate.execute(
                "http://dify-ib/v1/chat-messages",
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
                                JSONObject jsonData = JSONObject.parseObject(data);
                                processStreamData(jsonData, result);
                                if (!StringUtils.hasText(conversationId.get())) {
                                    conversationId.set(jsonData.getString("conversation_id"));
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("请求Dify异常!原因:{}", e.getMessage());
                    }
                    return null;
                }
        );
        DiFyChatResponse response = new DiFyChatResponse();
        response.setResult(result.toString());
        response.setConversationId(conversationId.get());
        return response;
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
