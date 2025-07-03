package com.ge.apm.service.wo;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.V2_ServiceRequestNoAsset;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceRequestNoAssetService {

    @Autowired
    ServiceRequestNoAssetApiService serviceRequestNoAssetApiService;


    public void saveServiceRequest(V2_ServiceRequestNoAsset v2ServiceRequestNoAsset,  List<V2_BlobObject> blobObjectList){
        String token = UserContextService.getAccessToken();
        serviceRequestNoAssetApiService.createServiceRequest(token, v2ServiceRequestNoAsset, blobObjectList);
    }
}
