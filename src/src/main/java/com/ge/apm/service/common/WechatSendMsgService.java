/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import com.ge.apm.domain.remote.GatewayURL;
import com.ge.apm.service.utils.*;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;

/**
 *
 * @author 212579464
 */
@Component
public class WechatSendMsgService extends MicroServiceInvoker {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Value("#{systemEnvironment['GATEWAY_URL'] ?: urlProperties.defaultGateway}")
    private String baseURL;

    @Value("#{wxProperties.asset_build_template_id}")
    private String AssetBuildTemplateId;
    @Value("#{wxProperties.web_context_url}")
    private String webContextUrl;
    
    public Boolean sendCreateAssetRejectMsg(Map<String, Object> map){
        WechatPushModel model = new WechatPushModel();
        map.put("_templateId", AssetBuildTemplateId);
        map.put("linkUrl", webContextUrl.concat((String)map.get("linkUrl")));
        model.setMessage(JSON.toJSONString(map));
        HttpEntity<WechatPushModel> requestEntity = new HttpEntity<WechatPushModel>(model,createHttpHeaders(null));

        String gatewayURL = GatewayURL.SEND_WECHAT_MSG.toGatewayURL(baseURL);
        ResponseEntity<String> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, String.class);
        return response.getStatusCode().equals(HttpStatus.OK);
    } 

}

class WechatPushModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1L;
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}