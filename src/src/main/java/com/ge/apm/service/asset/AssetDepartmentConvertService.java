package com.ge.apm.service.asset;

import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AssetDepartmentConvertService extends MicroServiceInvoker {

    protected static final Logger logger = LoggerFactory.getLogger(MicroServiceInvoker.class);

    public void apply(Map<String ,Object> param) {
        String token = UserContextService.getAccessToken();
        HttpEntity<Map<String ,Object>> requestEntity = new HttpEntity<>(param, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.ASSET_DEPARTMENT_COVERT_APPLY);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
        if (isOkay(response)) {
            WebUtil.addSuccessMessage("申请成功");
            logger.info("AssetDepartmentConvertService apply assetUids:{},result:{}", param.get("assetUids") , responseMap.get("data"));
        }else {
            WebUtil.addErrorMessage((String)responseMap.get("message"));
            logger.error("AssetDepartmentConvertService apply error assetUids:{},result:{}", param.get("assetUids") , responseMap.get("data"));
        }
    }
}
