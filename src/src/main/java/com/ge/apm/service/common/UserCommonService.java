/*
 */
package com.ge.apm.service.common;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;
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
            List<LinkedHashMap> mapList = (List<LinkedHashMap>)res.get("data");
            List<UserAccount> userList = mapList.stream().map(item ->{
                UserAccount u = new UserAccount();
                u.setId(Integer.parseInt(item.get("id").toString()));
                u.setUid(item.get("uid").toString());
                u.setName(item.get("name").toString());
                u.setTelephone(Optional.ofNullable(item.get("telephone")).orElse("").toString());
                return u;
            }).collect(Collectors.toList());
            
            return userList;
        }
        return null;
    }
}
