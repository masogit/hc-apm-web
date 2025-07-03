package com.ge.apm.service.wo;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequestNoAsset;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import lombok.Data;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

}


@Data
class ServiceRequestNoAssetForm {

    Integer casePriority;
    String assetName;

    String deptUid;
    String deptName;
    String requestReason;
    List<V2_BlobObject> attachments;
    String comment;
    String requestorPhone;
}
