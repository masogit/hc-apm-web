/*
 */
package com.ge.apm.service.common;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.Arrays;
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
    
    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private PmOrderRequestApiService pmApi;
    public List<UserAccount> getWorkerList(Integer assetId) {
        Map<String, Object> res = srApi.getReponserByAsset(UserContextService.getAccessToken(), assetId == null ? 0 : assetId);
        if (null != res) {
            List<LinkedHashMap> mapList = (List<LinkedHashMap>) res.get("data");
            List<UserAccount> userList = mapList.stream().map(item -> {
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
public List<PmOrderSteps> getPmOrderStepsList(String pmOrderId){
    Map<String, Object> res = pmApi.getPmOrderSteps(UserContextService.getAccessToken(), pmOrderId == null ? "" : pmOrderId);
    if (null != res) {
        List<LinkedHashMap> mapList = (List<LinkedHashMap>) res.get("data");
        List<PmOrderSteps> pmOrderStepsList = mapList.stream().map(item -> {
            PmOrderSteps ps = new PmOrderSteps();
            ps.setComments(Optional.ofNullable(item.get("comment")).orElse("").toString());
            ps.setEndTime(Optional.ofNullable(item.get("endTime")).orElse("").toString());
            ps.setStartTime(Optional.ofNullable(item.get("startTime")).orElse("").toString());
			ps.setOwnerName(Optional.ofNullable(item.get("ownerName")).orElse("").toString());
            ps.setStepName(Optional.ofNullable(item.get("stepName")).orElse("").toString());
            return ps;
        }).collect(Collectors.toList());

        return pmOrderStepsList;
    }
    return null;
}

    public List<UserAccount> getApproveList(String assetUid) {
        Map<String, Object> res = pmApi.getApproveByAsset(UserContextService.getAccessToken(), assetUid == null ? "" : assetUid);
        if (null != res) {
            List<LinkedHashMap> mapList = (List<LinkedHashMap>) res.get("data");
            List<UserAccount> userList = mapList.stream().map(item -> {
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
    public List<UserAccount> getClinicalDeptOwnerList(OrgInfo clinicalDept){
        List<String> strlist = Arrays.asList(clinicalDept.getTenantUID(),clinicalDept.getInstitutionUID(),clinicalDept.getHospitalUID(),clinicalDept.getSiteUID(),clinicalDept.getUid());
        return userDao.getByClinicalDept(strlist);
    }


    
}
