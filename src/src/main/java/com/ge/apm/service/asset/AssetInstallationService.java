/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.common.UserCommonService;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
public class AssetInstallationService {
    
    @Autowired
    AssetInstallationApiService installApiService;
    
    @Autowired
    BlobObjectRepository blobDao;
    
    @Autowired
    OrgInfoRepository orgDao;
    
    @Autowired
    UserAccountRepository userDao;
    
    @Autowired
    private UserCommonService ucService;
    
    
    public Boolean submitArrivalList(String srrivalListId){
        String token = UserContextService.getAccessToken();
        String res = installApiService.submitArrivalList(token,srrivalListId);
        return res.contains("successfully");
    }
    
    public Boolean submitInstallItem(String itemId,Boolean isFinish) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.submitArrivalListItem(token,itemId,isFinish);
        return res.contains("successfully");
    }
    
    public Boolean reAssignWorkOrder(ArrivalListItem selectedItem) {
        String token = UserContextService.getAccessToken();
        String res = installApiService.reAssignOrderOwner(token,selectedItem.getId(),selectedItem.getCurrentPersonUID());
        return res.contains("successfully");
    }
    
    public List<V2_BlobObject> getPicList(String itemId){
        return blobDao.findByBoUuid(itemId);
    }

    public OrgInfo getOrgInfoByUid(String siteUID) {
        return orgDao.getByUid(siteUID);
    }

    public UserAccount getUserAccountByUid(String userUid){
        return userDao.getByUid(userUid);
    }

    public List<UserAccount> getWorkerList() {
        return ucService.getWorkerList(0);
    }

    public List<UserAccount> getClinicalOwnerList(OrgInfo clinicalDept) {
        if (clinicalDept == null){
            return null;
        }
        List<UserAccount> userlist = ucService.getClinicalDeptOwnerList(clinicalDept);
        return userlist.stream().filter(item -> item.getUserRoleList()!=null && !item.getUserRoleList().stream()
                        .anyMatch(roleItem -> roleItem.getRole().getName().equals("Guest"))).collect(Collectors.toList());
    }

}
