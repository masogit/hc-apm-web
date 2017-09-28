/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Component
public class V2_WorkOrderService {

    @Autowired
    private BiomedGroupRepository groupDao;

    @Autowired
    private V2_WorkOrderRepository woDao;

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private ServiceRequestRepository srDao;

    @Autowired
    private V2_WorkOrderStepRepository stepDao;

    @Autowired
    private WorkOrderDetailRepository detailDao;

    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private ServiceRequestApiService srApi;
    @Autowired
    OrgInfoRepository orgInfoRepository;
    @Autowired
    AssetInfoRepository assetInfoRepository;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    private WorkOrderCoworkerRepository workOrderCoworkerRepository;
    public V2_WorkOrder getWorkOrderById(String woId) {
        return woDao.findById(woId);
    }

    public List<V2_WorkOrder> getWorkOrdersBySR(String serviceRequest) {
        return woDao.findBySrId(serviceRequest);
    }

    public List<V2_ServiceRequest> getServiceRequestByAssetId(Integer id) {
        return srDao.findByAssetId(id);
    }

    public List<V2_WorkOrder_Step> getWorkOrderSteps(String woId) {
        return stepDao.findByWorkOrder(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetails(String woId) {
        return detailDao.findByWoId(woId);
    }
    
    public List<String> getUnfinishedSRList() {
        List<SearchFilter> srSearchFilters = new ArrayList();
        srSearchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        srSearchFilters.add(new SearchFilter("requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        srSearchFilters.add(new SearchFilter("fromDeptUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getOrgInfoUID(), "1*2+"));
        return srDao.findBySearchFilter(srSearchFilters).stream().map(item -> item.getId()).collect(Collectors.toList());
    }

    public void createServiceRequest(V2_ServiceRequest newServiceRequest, AssetInfo asset) {
        String token = UserContextService.getAccessToken();
        Map<String, Object> data = srApi.createServiceRequest(token, newServiceRequest, asset);
    }

    public void cancelWorkOrder(String workOrderId, String reason) {
        V2_WorkOrder wo = woDao.findById(workOrderId);
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setDesc(reason);
        formData.setCurrentStepId(wo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token,formData,"cancel",workOrderId);
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Cancel"));
        }
    }

    public List<UserAccount> getWorkerList(Integer assetId) {
        Map<String, Object> res = srApi.getReponserByAsset(UserContextService.getAccessToken(), assetId);
        if(null!=res){
            return (List<UserAccount>)res.get("data");
        }
        return null;
    }
    
    public AssetInfo getAssetInfo(Integer assetId){
        return assetDao.findById(assetId);
    }

    public void dispatchWorkOrder(V2_WorkOrder selectedWorkOrder) {
        V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setAssigneeId(selectedWorkOrder.getCurrentPersonId().toString());
        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token,formData,"assign",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("dispatchWorkOrder"));
        }
    }

    public void ackWorkOrder(V2_WorkOrder selectedWorkOrder) {
        V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token,formData,"ack",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Acknowledge"));
        }
    }
    
    public void updateDownTime(V2_WorkOrder wo,Date confirmedDowntime) {
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(wo.getCurrentStepId());
        formData.setAssetStatus("2");
        formData.setConfirmedDownTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedDowntime));
        String res = srApi.invokeWorkOrderAction(token,formData,"updateTime",wo.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Acknowledge"));
        }
    }

    public void rejectWorkOrder(V2_WorkOrder selectedWorkOrder,String reason, Integer status, Date confirmedDowntime) {
        V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        formData.setDesc(reason);
        formData.setAssetStatus(String.valueOf(status));
        String res = srApi.invokeWorkOrderAction(token,formData,"ackFailed",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("Acknowledge"));
        }
    }

    public void rateWorkOrder(V2_WorkOrder selectedWorkOrder, Integer rating, String comments) {
        V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        formData.setDesc(comments);
        formData.setFeedbackRating(String.valueOf(rating));
        String res = srApi.invokeWorkOrderAction(token,formData,"rate",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("feedbackComment"));
        }
    }
    public void checkIn(V2_WorkOrder selectedWorkOrder){
        V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        String res = srApi.invokeWorkOrderAction(token,formData,"checkIn",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("feedbackComment"));
        }
    }
    public void takeEquip(V2_WorkOrder selectedWorkOrder,String taker, String takeTime){
        V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(tmpwo.getCurrentStepId());
        formData.setEquipmentTaker(taker);
        formData.setTakeTime(takeTime);
        String res = srApi.invokeWorkOrderAction(token,formData,"take",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),WebUtil.getMessage("feedbackComment"));
        }
    }

    public void acceptWorkOrder(V2_WorkOrder selectedWorkOrder, Date estimatedCloseTime, String extType,String comments) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        selectedWorkOrder.setCurrentStepId(3);
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(3);
        formData.setEstimatedCloseTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(estimatedCloseTime));
        formData.setIntExtType(extType);
        formData.setDesc(comments);
        String res = srApi.invokeWorkOrderAction(token,formData,"accept",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"接单");
        }
        selectedWorkOrder.setCurrentStepId(4);
    }

    public void takeWorkOrder(V2_WorkOrder selectedWorkOrder, Date takeTime, String taker) {
        //V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        selectedWorkOrder.setCurrentStepId(3);
        selectedWorkOrder.setCurrentStepName("待接单");
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(selectedWorkOrder.getCurrentStepId());
        formData.setTakeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(takeTime));
        formData.setEquipmentTaker(taker);
        String res = srApi.invokeWorkOrderAction(token,formData,"take",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"接单");
        }
    }

    public void reassignWorkOrder(int step, V2_WorkOrder selectedWorkOrder, Integer assigneeId,String comments) {
       // V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        selectedWorkOrder.setCurrentStepId(step);
        if(step==3){
            selectedWorkOrder.setCurrentStepName("待接单");
        }else{
            selectedWorkOrder.setCurrentStepName("维修中");
        }
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(step);
        formData.setAssigneeId(String.valueOf(assigneeId));
        formData.setDesc(comments);
        String res = srApi.invokeWorkOrderAction(token,formData,"reassign",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"转单");
        }
    }

    public void repairWorkOrder(V2_WorkOrder selectedWorkOrder, Date confirmedUpTime, Integer assetStatus, List<V2_WorkOrder_Detail> accDetails, List<V2_WorkOrderCoworker> coDetails, Integer myhours, Double otherExpense, String comments) {

        // V2_WorkOrder tmpwo=woDao.findById(selectedWorkOrder.getId());
        selectedWorkOrder.setCurrentStepId(4);
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        formData.setCurrentStepId(4);
        selectedWorkOrder.setCurrentStepName("维修中");
//        formData.setDesc(selectedWorkOrder.getComment());
        formData.setDesc(comments);
        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setPatActions(selectedWorkOrder.getPatActions());
        formData.setPatProblems(selectedWorkOrder.getPatProblems());
        formData.setPatTests(selectedWorkOrder.getPatTests());
        formData.setConfirmedUpTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(confirmedUpTime));
        formData.setCaseType(selectedWorkOrder.getCaseType().toString());
        formData.setAssetStatus(assetStatus.toString());
        formData.setStepDetail(accDetails);
        formData.setStepCoworker(coDetails);
        formData.setManHours(myhours);
        formData.setOtherExpense(otherExpense);
        String res = srApi.invokeWorkOrderAction(token,formData,"repair",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"完成");
        }
    }


    public void closeWorkOrder(V2_WorkOrder selectedWorkOrder) {
        String token = UserContextService.getAccessToken();
        WorkOrderActionForm formData = new WorkOrderActionForm();
        selectedWorkOrder.setCurrentStepId(6);
        selectedWorkOrder.setCurrentStepName("待关单");
        formData.setCurrentStepId(6);
        formData.setDesc(selectedWorkOrder.getComment());
        formData.setIntExtType(selectedWorkOrder.getIntExtType().toString());
        formData.setPatActions(selectedWorkOrder.getPatActions());
        formData.setPatProblems(selectedWorkOrder.getPatProblems());
        formData.setPatTests(selectedWorkOrder.getPatTests());
        formData.setCaseType(selectedWorkOrder.getCaseType().toString());
        String res = srApi.invokeWorkOrderAction(token,formData,"close",selectedWorkOrder.getId());
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"关单");
        }
    }

    public void reCreateWorkOrder(V2_WorkOrder selectedWorkOrder,Integer assigneeId, Integer assetSuatus,String reason,Integer extType) {
        String token = UserContextService.getAccessToken();
        WorkOrderForm formData = new WorkOrderForm();
        formData.setServiceRequestId(selectedWorkOrder.getSrId());
        formData.setAssigneeId(assigneeId.toString());
        formData.setAssetId(selectedWorkOrder.getAssetId());
        formData.setAssetStatus(assetSuatus.toString());
        formData.setRequestReason(reason);
        formData.setIntExtType(extType);
        String res = srApi.createWorkOrder(token,formData);
        if (res==null || !res.contains("success")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("OperationFail"),"创建");
        }
    }
    public List<UserAccount> getAssetResponser2(int assetId) {

        AssetInfo assetInfo = assetInfoRepository.getById(assetId);

        //获取关注过该资产标签的用户, 设备标签<->(设备+工程师组[员1，2..])
        List<UserAccount> userListTag =  userAccountRepository.getResponserByAssetId(assetId);
        //获取和资产绑定的owner
        List<UserAccount> defaultUserList=new ArrayList<UserAccount>();

        // Get responser with deeper down agaisnt on siteuid ->hospitalUid -> InstitutionUID-->tenanent
        if(CollectionUtils.isEmpty(userListTag)){

            if(!StringUtils.isEmpty(assetInfo.getSiteUID()) )
                return userAccountRepository.getBySiteUID(assetInfo.getSiteUID());
            if(!StringUtils.isEmpty(assetInfo.getHospitalUID()) )
                return userAccountRepository.getByHospitalUID(assetInfo.getHospitalUID());
            if(!StringUtils.isEmpty(assetInfo.getInstitutionUID()) )
                return userAccountRepository.getByInstitutionUID(assetInfo.getInstitutionUID());
            if(!StringUtils.isEmpty(assetInfo.getTenantUID()) )
                return userAccountRepository.getByTenantUID(assetInfo.getInstitutionUID());

            return defaultUserList;
        }else
            return userListTag;
    }
    public List<UserAccount> getAssetResponser(int assetId) {
        List<UserAccount> userListTag = null;
        AssetInfo assetInfo = assetInfoRepository.getById(assetId);

        //获取关注过该资产标签的用户, 设备标签<->(设备+工程师组[员1，2..])
        userListTag = userAccountRepository.getResponserByAssetId(assetId);
        //获取和资产绑定的owner
        List<UserAccount> assetOwnerList=  userAccountRepository.getResponserWithAsset(assetId);
        List<UserAccount> defaultUserList=new ArrayList<UserAccount>();

        // Get responser with deeper down agaisnt on siteuid ->hospitalUid -> InstitutionUID
        if(CollectionUtils.isEmpty(userListTag) &  CollectionUtils.isEmpty(assetOwnerList)){
            OrgInfo orgInfo = orgInfoRepository.getByUid( assetInfo.getClinicalDeptUID());
            if(orgInfo!=null) {
                if(orgInfo.getAssetOwnerId()!=null) {
                    UserAccount user1 = userAccountRepository.findById(orgInfo.getAssetOwnerId());
                    defaultUserList.add(user1);
                }
                if(orgInfo.getAssetOwnerId2()!=null) {
                    UserAccount user2 = userAccountRepository.findById(orgInfo.getAssetOwnerId2());
                    defaultUserList.add(user2);
                }
            }
            return defaultUserList;

        }
        if(CollectionUtils.isNotEmpty(userListTag) & CollectionUtils.isNotEmpty(assetOwnerList)){
            userListTag.addAll(assetOwnerList);
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            return userListTag;
        }else if(CollectionUtils.isEmpty(assetOwnerList)){
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            return userListTag;
        }else if(CollectionUtils.isEmpty(userListTag)){
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(assetOwnerList));
            defaultUserList=  userAccountRepository.getDefaultUsers(assetId);
            userListTag.addAll(defaultUserList);
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            return userListTag;
        }

        return userListTag;
    }


//Type：0-我的/1-同一group的/2-同院区的/3-其它院区的
public Page<V2_WorkOrder> getWorkOrderListToPickup(int type,PageRequest pr, UserAccount ua) {
    Page<V2_WorkOrder> pages = null;

    if (type == 0 || type == 1) {
        switch(type) {
            case 0: pages = woDao.fetchAvailableWorkOrderByUser(ua.getId(), pr);break;
            case 1: pages = woDao.fetchAvailableSameGroupWorkOrderByUser(ua.getId(),pr);
        }
        return pages;
    }
    Integer orgLevel = ua.getOrgLevel();
    if (orgLevel == null || orgLevel == 0)
        orgLevel = 3;
    List<SearchFilter> searchFilters = new ArrayList<>();
    switch (orgLevel) {
        case 1: searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, ua.getInstitutionUID()));break;
        case 2: searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, ua.getHospitalUID()));break;
        case 3: searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, ua.getSiteUID()));
    }

    searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
    searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3));

    return woDao.findBySearchFilter(searchFilters, pr);
}
    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return workOrderCoworkerRepository.findByWoId(woId);
    }
   /* public Page<V2_WorkOrder> getWorkOrderListToPickup_backup(int type,PageRequest pr, UserAccount ua) {
        Page<V2_WorkOrder> pages = null;
        //base on the orgLevel
        Integer orgLevel = ua.getOrgLevel();
        if (orgLevel == null || orgLevel == 0)
            orgLevel = 3;
        Integer groupCount = 0;
        List<SearchFilter> searchFilters = new ArrayList<>();
        switch (orgLevel) {
            case 1: searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, ua.getInstitutionUID()));
                groupCount = groupDao.getCountByUserOrgLevel1(ua.getInstitutionUID());break;
            case 2: searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, ua.getHospitalUID()));
                groupCount = groupDao.getCountByUserOrgLevel2(ua.getHospitalUID());break;
            case 3: searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, ua.getSiteUID()));
                groupCount = groupDao.getCountByUserOrgLevel3(ua.getSiteUID());
        }
        if (groupCount > 0 && (type == 0 || type == 1)) {
            switch(type) {
                case 0: pages = woDao.fetchAvailableWorkOrderByUser(ua.getId(), pr);break;
                case 1: pages = woDao.fetchAvailableSameGroupWorkOrderByUser(ua.getId(),pr);
            }
            return pages;
        }

        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3));
        if(type == 0) {
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.IN, Arrays.asList(-1,ua.getId())));
        }

        return woDao.findBySearchFilter(searchFilters, pr);
    }*/

    public List<UserAccount> findMaintenanceStaff(int assetId) {
        List<UserAccount> userListTag = null;
        AssetInfo assetInfo = assetInfoRepository.getById(assetId);

        //获取关注过该资产标签的用户, 设备标签<->(设备+工程师组[员1，2..])
        userListTag = userAccountRepository.getResponserByAssetId(assetId);

        //获取和资产绑定的owner
        List<UserAccount> assetOwnerList=  userAccountRepository.getResponserWithAsset(assetId);
        List<UserAccount> defaultUserList=new ArrayList<UserAccount>();

        // Get responser with deeper down agaisnt on siteuid ->hospitalUid -> InstitutionUID
        if(CollectionUtils.isEmpty(userListTag) &  CollectionUtils.isEmpty(assetOwnerList)){
            OrgInfo orgInfo = orgInfoRepository.getByUid( assetInfo.getClinicalDeptUID());
            if(orgInfo!=null) {
                if(orgInfo.getAssetOwnerId()!=null) {
                    UserAccount user1 = userAccountRepository.findById(orgInfo.getAssetOwnerId());
                    defaultUserList.add(user1);
                }
                if(orgInfo.getAssetOwnerId2()!=null) {
                    UserAccount user2 = userAccountRepository.findById(orgInfo.getAssetOwnerId2());
                    defaultUserList.add(user2);
                }
            }
            return defaultUserList;

        }
        if(CollectionUtils.isNotEmpty(userListTag) & CollectionUtils.isNotEmpty(assetOwnerList)){
            userListTag.addAll(assetOwnerList);
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            return userListTag;
        }else if(CollectionUtils.isEmpty(assetOwnerList)){
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            return userListTag;
        }else if(CollectionUtils.isEmpty(userListTag)){
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(assetOwnerList));
            defaultUserList=  userAccountRepository.getDefaultUsers(assetId);
            userListTag.addAll(defaultUserList);
            userListTag = new ArrayList<UserAccount>(new HashSet<UserAccount>(userListTag));
            userListTag = userListTag.stream().filter(id -> id != null).collect(Collectors.toList());
            return userListTag;
        }

        return userListTag;
    }

}


