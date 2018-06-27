/*
 */
package com.ge.apm.service.common;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.UserAgentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

/**
 *
 * @author 212579464
 */
@Component
public class UserCommonService {

    @Autowired
    private ServiceRequestApiService srApi;
    
    @Autowired
    private OrgInfoRepository orgInfoDao;

    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private PmOrderRequestApiService pmApi;

    @Autowired
    private TenantInfoRepository tenantInfoRepository;

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

    
    public UserAccount getUserByUid(String userUid){
        if(Strings.isNullOrEmpty(userUid)){
            return new UserAccount();
        }
        return userDao.getByUid(userUid);
    }

    public List<UserAccount> getAssetCreateApproverlList(String tenantUid){
        List<UserAccount> assetHeaderList = userDao.getAssetHeadList(tenantUid);
        assetHeaderList.addAll(userDao.findByTenantUIDAndStaffRole(tenantUid));
        return assetHeaderList.stream().distinct().collect(Collectors.toList());
    }

    //判断用户是否有操作数据权限
    public boolean isCanOperate(UserAccount cua, String siteUID){
        boolean flag = false;

        if(!UserContextService.isOnlyLocalITAdmin()){
            return true;
        }
        OrgInfo tempOrgInfo = orgInfoDao.getByUid(siteUID);

        if(cua.getOrgLevel() < tempOrgInfo.getOrgLevel()){
            flag = true;
        }

        if(cua.getOrgLevel() == tempOrgInfo.getOrgLevel() && cua.getSiteUID().equals(siteUID)){
            flag = true;
        }

        if(!flag){
            WebUtil.addErrorMessage("没有操作权限");
        }

        return flag;
    }

    //判断用户是否有操作数据权限
    public boolean isCanOperate(UserAccount cua, OrgInfo oi){
        boolean flag = false;

        if(!UserContextService.isOnlyLocalITAdmin()){
            return true;
        }

        if(cua.getOrgLevel() < oi.getOrgLevel()){
            flag = true;
        }

        if(cua.getOrgLevel() == oi.getOrgLevel() && cua.getSiteUID().equals(oi.getSiteUID())){
            flag = true;
        }

        if(!flag){
            WebUtil.addErrorMessage("没有操作权限");
        }

        return flag;
    }

    public List<SearchFilter> getLoginUserFilters(Boolean isInsulate,Integer loginUserOrgType){
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if(isInsulate){
            if(loginUserOrgType == 1){
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
            }else if(loginUserOrgType == 2){
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
            }else if(loginUserOrgType == 3 || loginUserOrgType == 4){
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
            }
/*            else if(loginUserOrgType == 4){//挂在科室下
                searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.EQ, user.getOrgInfoUID()));
            }*/
        }else{
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        }
        return searchFilters;
    }

    public List<OrgInfo> getIncludeOrgInfo(Boolean isInsulate,Integer loginUserOrgType,String uid,String parentUID,Integer orgType){
        UserAccount user = UserContextService.getCurrentUserAccount();
        if(isInsulate){
            List<OrgInfo> orgInfos = Lists.newArrayList();
            if(loginUserOrgType >= orgType){
                if(StringUtils.isEmpty(uid)){//新增时
                    if(orgType == 1){
                        uid = user.getInstitutionUID();
                    }else if(orgType == 2){
                        uid = user.getHospitalUID();
                    }else if(orgType == 3){
                        uid = user.getSiteUID();
                    }
                }
                OrgInfo orgInfo = orgInfoDao.findByUid(uid);
                orgInfos.add(orgInfo);
                return orgInfos;
            }else{
                return orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(parentUID,orgType);
            }
        }else{
            return orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(parentUID,orgType);
        }
    }
}
