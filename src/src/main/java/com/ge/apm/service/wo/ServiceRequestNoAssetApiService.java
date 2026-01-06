package com.ge.apm.service.wo;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequestNoAsset;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.healthcare.cn.common.cons.RepairSourceEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceRequestNoAssetApiService  extends MicroServiceInvoker {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());



    public Map<String, Object> createServiceRequest(String token, V2_ServiceRequestNoAsset newServiceRequest, List<V2_BlobObject> picList) {
        ServiceRequestNoAssetForm formData = new ServiceRequestNoAssetForm();
        formData.setCasePriority(newServiceRequest.getCasePriority());
        formData.setRequestReason(newServiceRequest.getRequestReason());
        formData.setAttachments(picList);
        formData.setDeptUid(newServiceRequest.getDeptUid());
        formData.setDeptName(newServiceRequest.getDeptName());
        formData.setAssetName(newServiceRequest.getAssetName());
        formData.setComment(newServiceRequest.getComment());
        formData.setRequestorPhone(newServiceRequest.getRequestorPhone());
        formData.setAssetStatus(newServiceRequest.getAssetStatus());
        formData.setRepairSource(RepairSourceEnum.ServiceRequestNoAsset.name());
        formData.setFinancingNum(newServiceRequest.getFinancingNum());
        if (null != newServiceRequest.getConfirmedDownTime()) {
            formData.setConfirmedDownTime(TimeUtils.getStrDate(newServiceRequest.getConfirmedDownTime(), "yyyy-MM-dd HH:mm:ss"));
        }

        HttpEntity<ServiceRequestNoAssetForm> requestEntity = new HttpEntity<>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SERVICE_NO_ASSET_REQUEST_CREATE);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap.get("data");
        } else {
            return null;
        }
    }


    public Map<String, Object> getAssigneeUsersBySite(String token,String orgUid) {
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(header);

        String gatewayURL = getGatewayURL(GatewayURL.SERVICE_NO_ASSET_ASSIGNEE_USERS);
        if(StringUtils.isNotEmpty(orgUid)){
            gatewayURL = gatewayURL + "?orgUid=" +orgUid;
        }
        ResponseEntity<LinkedHashMap> response = template.exchange(gatewayURL, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            return (LinkedHashMap<String, Object>) responseMap;
        } else {
            return null;
        }
    }

    public String invokeAction(String token, ServiceRequestNoAssetActionForm formData, String actionName, String woId){
        HttpEntity<ServiceRequestNoAssetActionForm> requestEntity = new HttpEntity<ServiceRequestNoAssetActionForm>(formData, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.SERVICE_NO_ASSET_ACTION);
        String url = gatewayURL.replace("{id}", woId).concat("?type=").concat(actionName);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(url, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            logger.info("Invoke ServiceRequestAction " + actionName + " has finished " + responseMap.get("data"));
            return (String) responseMap.get("data");
        } else {
            return this.getResponseFailMessage(response);
        }
    }


}




@Data
class ServiceRequestNoAssetForm {

    Integer casePriority;
    String assetName;

    String deptUid;
    String deptName;
    String financingNum;
    String requestReason;
    List<V2_BlobObject> attachments;
    String comment;
    String requestorPhone;

    String confirmedDownTime;

    Integer assetStatus;

    String repairSource;
}

@Data
class ServiceRequestNoAssetActionForm{
    String assigneeId;
    Integer currentStepId;
    String estimatedCloseTime;

    String desc;

    String assetStatus;
    String cancelReason;
    Integer repairType;
    String assetUid;


    private String financingNum;//资产编号


    private String functionType;//设备型号



    private String serialNum;//制造商序列号


    private String makerShortName;
}
