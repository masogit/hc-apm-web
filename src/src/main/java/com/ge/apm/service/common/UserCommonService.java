/*
 */
package com.ge.apm.service.common;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class UserCommonService {
    
    @Autowired
    private ServiceRequestApiService srApi;
    
     public List<UserAccount> getWorkerList(Integer assetId) {
        Map<String, Object> res = srApi.getReponserByAsset(UserContextService.getAccessToken(), assetId==null?0:assetId);
        if(null!=res){
            return (List<UserAccount>)res.get("data");
        }
        return null;
    }
}
