package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class SupplierApiService extends MicroServiceInvoker {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public byte[] getWxaCodeImage (String token, JSONObject payload){
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(payload, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SUPPLIER_WX_CODE_IMAGE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, byte[].class);
        if (response.getBody() != null) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public byte[] getWeChatQrCodeImage (String token){
        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SUPPLIER_WX_QRCODE_IMAGE);
        ResponseEntity<byte[]> response = template.exchange(gatewayURL, HttpMethod.POST, requestEntity, byte[].class);
        if (response.getBody() != null) {
            return response.getBody();
        } else {
            return null;
        }
    }

}
