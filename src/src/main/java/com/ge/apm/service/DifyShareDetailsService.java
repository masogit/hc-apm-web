package com.ge.apm.service;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DifyShareDetailsService extends MicroServiceInvoker {

    /**
     * 	"data": [
     * 		        {
     * 			"createdDate": "2026-01-19T03:17:28.000+00:00",
     * 			"sessionDetailId": "47c970f7-9591-478a-88ef-1d94506a3217",
     * 			"sessionId": "875ebca0-6653-4240-96e0-ffb0198a31fe",
     * 			"conversation": [
     *                {
     * 					"type": "0",
     * 					"message": "123"
     *                },
     *                {
     * 					"messageState": null,
     * 					"type": "1",
     * 					"message": "您的问题“123”未涉及医疗设备相关内容，因此我无法提供专业解答。如有医疗设备方面的疑问，欢迎随时提问！"
     *                }
     * 			]
     *        }
     * 	],
     *
     * 	{
     * 	"message": "OK",
     * 	"referencesURLs": null,
     * 	"data": {
     * 		"id": 2,
     * 		"userId": 18336,
     * 		"updateDate": "2026-01-19 11:21:05",
     * 		"createdDate": "2026-01-19 11:21:05",
     * 		"sessionDetailIds": "47c970f7-9591-478a-88ef-1d94506a3217"
     *    },
     * 	"bizStatusCode": "OK",
     * 	"rowCount": 1
     * }
     * @param string
     * @return
     */
    public String createShareMessage(String string){
        String[] split = string.split(",");
        String url = this.getGatewayURL(GatewayURL.DIFY_CREATE_SHARE_MESSAGE);
        url+="?sessionDetailIds="+split;
        HttpHeaders header = createHttpHeaders(null);
        HttpEntity<Map> requestEntity = new HttpEntity<>(new HashMap(), header);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if(isOkay(response)){
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
            String data =(String) body.get("data");
            JSONObject objects = JSONObject.parseObject(data);
            Integer shareId = objects.getInteger("id");
            String shareUrl ="/portal/share/shareDetails.xhtml?str="+shareId;
            return shareUrl;
        }
       return null;
    }
/**
 * 	"data": [
 * 		        {
 * 			"createdDate": "2026-01-19T03:17:28.000+00:00",
 * 			"sessionDetailId": "47c970f7-9591-478a-88ef-1d94506a3217",
 * 			"sessionId": "875ebca0-6653-4240-96e0-ffb0198a31fe",
 * 			"conversation": [
 *                {
 * 					"type": "0",
 * 					"message": "123"
 *                },
 *                {
 * 					"messageState": null,
 * 					"type": "1",
 * 					"message": "您的问题“123”未涉及医疗设备相关内容，因此我无法提供专业解答。如有医疗设备方面的疑问，欢迎随时提问！"
 *                }
 * 			]
 *        }
 * 	],
 */
    public List<ApmDropDownItem> getShareMessage(Integer shareId) {
        List<ApmDropDownItem> result = new ArrayList<>();
        if(shareId == null) return result;
        String url = this.getGatewayURL(GatewayURL.DIFY_GET_SHARE_MESSAGE);
        url+="?userShareId="+shareId;
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<Map> requestEntity = new HttpEntity<>(new HashMap(), header);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if(isOkay(response)){
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
            ArrayList<LinkedHashMap<String,Object>> data =(ArrayList<LinkedHashMap<String,Object>>) body.get("data");
            for (int i = 0; i < data.size(); i++) {
                LinkedHashMap<String,Object> content = data.get(i);
                if(!content.containsKey("conversation")){
                    continue;
                }
                ArrayList<LinkedHashMap<String,String>> conversation =(ArrayList<LinkedHashMap<String,String>>) content.get("conversation");
                for (int i1 = 0; i1 < conversation.size(); i1++) {
                    LinkedHashMap<String,String> o = conversation.get(i1);
                    ApmDropDownItem item = new ApmDropDownItem();
                    item.setKey(o.get("type"));
                    item.setLabel(o.get("message"));
                    result.add(item);
                }
            }


        }
        return result;
    }
}
