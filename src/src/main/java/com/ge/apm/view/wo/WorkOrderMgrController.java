/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.AssetFaultType;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrderCoworker;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderMgrController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable, ServerEventInterface {

    V2_WorkOrderRepository dao;

    private OrgInfoRepository orgDao;
    private V2_WorkOrderService woService;
    private UaaService uaaService;
    private UserContextService userContextService;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetDao;

    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;

    private V2_ServiceRequest newServiceRequest;

    private V2_WorkOrder selectedWorkOrder;
    private List<V2_WorkOrder> selectedWorkOrderList;

    private AssetInfo selectedAsset;

    private Integer currentStatus = 1;

    private Boolean needConfirmTime = false;

    private String cancelReason;
    private String stepFileter;

    //for details form
    private List<V2_WorkOrder_Detail> accessoryList;
    private List<V2_WorkOrderCoworker> coWorkerList;

    private V2_WorkOrder_Detail itemDetail;
    private V2_WorkOrderCoworker coWorkerDetail;

    private Date esTime;
    private String comments;
    private Date confirmedUpDate;
    private Date confirmedDownTime;
    private String checkInDate;
    private Integer rating;
    private String failReason;
    private String equipmentTaker;
    private String takeTime;
    
    
    private List<Integer> tagGroupAssets;

    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        assetFaultTypeService = WebUtil.getBean(AssetFaultTypeService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        queryIndex = 0;
        this.filterBySite = false;
        this.filterByHospital = false;

    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        removeFilterOnField("status");
        removeFilterOnField("currentStepId");
        removeFilterOnField("currentPersonId");
        removeFilterOnField("dispatchMode");
        removeFilterOnField("srId");
        removeFilterOnField("feedbackRating");
        removeFilterOnField("assetId");

        if (queryIndex == 1) {//已报修 可取消
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1)), "1*2+"));
//                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            } else {
//                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getUnfinishedSRList(),"1*2+"));
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            }
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.LTE, 3));
        } else if (queryIndex == 2) { //待派工
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
        } else if (queryIndex == 3) { //待接单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3),"1*3+4*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getId(),"1*3+4*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(),"1*3+"));
            if(!getTagGroupAssets().isEmpty()){
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, getTagGroupAssets()));
            }
            
        } else if (queryIndex == 4) { //维修中
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
        } else if (queryIndex == 5) { //待验收
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1)), "1*2+"));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            } else {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            }
        } else if (queryIndex == 6) { //待评价
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1,2)), "1*2+"));
            } 
            searchFilters.add(new SearchFilter("feedbackRating", SearchFilter.Operator.EQ, 0));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
        } else if (queryIndex == 7) { //待关单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
        } else if (queryIndex == 8) { //已关单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
        } else if (queryIndex == 9) { //已取消
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
        }else{//全部
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        }

        this.selected = null;
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = getVisiableOrgUidList();
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("serviceRequest.requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        }
    }

    private List<String> getVisiableOrgUidList() {
        if (userContextService.isDispatcher() || userContextService.hasRole("AssetHead")) {
            return uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).distinct().collect(Collectors.toList());
        } else {
            return uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        }
    }
    
    private List<Integer> getTagGroupAssets(){
        if(tagGroupAssets == null){
            tagGroupAssets = dao.getAssetsByTagGroup(UserContextService.getCurrentUserAccount().getId());
        }
        return tagGroupAssets;
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        cancel();
    }

    public void cancel() {
        selected = null;
        newServiceRequest = null;
        esTime = null;
        comments = null;
        selectedWorkOrder = null;
        failReason = null;
        rating = null;
        confirmedDownTime = null;
        selectedWorkOrderList = null;
        equipmentTaker = null;
        takeTime = null;
    }

//    public V2_ServiceRequest getServiceRequest(String srId) {
//        return srDao.findById(srId);
//    }

    public void onSelectWorkOrder() {
        newServiceRequest = null;
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        currentStatus = selected.getAssetInfo().getStatus();
        needConfirmTime = false;
        selectedWorkOrderList = dao.findBySrId(selected.getServiceRequest().getId());
    }

    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        List<V2_WorkOrder_Detail> list = woService.getWorkOrderDetails(woId);
        return woService.getWorkOrderDetails(woId);
    }


    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return woService.getWorkOrderCoworker(woId);
    }

    public void prepareAct(String woid) {
        selectedWorkOrder = dao.findById(woid);
    }

    public void prepareCloseOrder(String woid) {
        selectedWorkOrder = dao.findById(woid);
        equipmentTaker = selectedWorkOrder.getServiceRequest().getEquipmentTaker();
    }

    public void prepareCompleteOrder(String woid) {
        selectedWorkOrder = dao.findById(woid);
        selectedWorkOrder.setManHours(null);
        selectedWorkOrder.setOtherExpense(null);
        accessoryList = new ArrayList<V2_WorkOrder_Detail>();
        coWorkerList = new ArrayList<V2_WorkOrderCoworker>();
    }

    public List<UserAccount> getWorkerList() {
        return woService.getWorkerList(selectedWorkOrder.getAssetId());
    }

    public void ackWorkOrder() {
        woService.ackWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void updateDownTime() {
        woService.updateDownTime(selected, confirmedDownTime);
        needConfirmTime = false;
        this.selected.getServiceRequest().setConfirmedDownTime(confirmedDownTime);
    }

    public void rejectWorkOrder() {
        woService.rejectWorkOrder(selectedWorkOrder, failReason, currentStatus, confirmedDownTime);
        cancel();
    }

    public void rateWorkOrder() {
        woService.rateWorkOrder(selectedWorkOrder, rating, comments);
        cancel();
    }

    public void dispatchWorkOrder() {
        woService.dispatchWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void createWorkOrder() {
        selectedWorkOrder.setComment(comments);
        woService.reCreateWorkOrder(selectedWorkOrder, selectedWorkOrder.getCurrentPersonId(), 2, comments, selectedWorkOrder.getIntExtType());
    }

    public void checkIn(String woid) {
        checkInDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        V2_WorkOrder checkInWorkOrder = dao.findById(woid);
        woService.checkIn(checkInWorkOrder);
    }

    public void repairWorkOrder() {
        //gl:1表示是正常
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        woService.repairWorkOrder(selectedWorkOrder, confirmedUpDate, 1, accessoryList, coWorkerList, selectedWorkOrder.getManHours(), selectedWorkOrder.getOtherExpense(), comments);
        cancel();
    }

    public void cancelWorkOrder() {
        woService.cancelWorkOrder(selectedWorkOrder.getId(), cancelReason);
        cancelReason = null;
        cancel();
    }

    public void takeEquip() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        takeTime = sdf.format(new Date());
        woService.takeEquip(selectedWorkOrder, equipmentTaker, takeTime);
    }

    public void closeWorkOrder() {
        woService.closeWorkOrder(selectedWorkOrder, comments);
        cancel();
    }

    public void createServiceRequest() {
        cancel();
        selected = null;
        newServiceRequest = new V2_ServiceRequest();
        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
    }

    public void saveServiceRequest() {
        selectedAsset.setStatus(currentStatus);
        woService.createServiceRequest(newServiceRequest, selectedAsset);
        cancel();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }

        if (this.newServiceRequest == null) {
            return;
        }

        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;
    }

    public void onStatusChange() {
        if(selectedAsset==null && selected==null){
            return;
        }
        if(null!=selectedAsset){//new sr
            needConfirmTime =  currentStatus == 2 && selectedAsset.getStatus() != 2;
        }else{//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }
    }

    public void acceptWorkOrder() {
        String repairType = selectedWorkOrder.getRepairType().toString();
        woService.acceptWorkOrder(selectedWorkOrder, esTime, repairType, comments);
        cancel();
    }
    
    public void reassignWorkOrder(){
        woService.reassignWorkOrder(selectedWorkOrder, selectedWorkOrder.getCurrentPersonId(), comments);
        cancel();
    }

    public void prepareEditAccDetail(String itemId) {
        if ("new".equals(itemId)) {
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            itemDetail.setWoId(selectedWorkOrder.getId());

        } else {
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void prepareEditCoWorkerDetail(String itemId) {
        if ("new".equals(itemId)) {
            coWorkerDetail = new V2_WorkOrderCoworker();
            coWorkerDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            coWorkerDetail.setWoId(selectedWorkOrder.getId());

        } else {
            coWorkerDetail = coWorkerList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void addAcessItem() {
        if (!accessoryList.contains(itemDetail)) {
            accessoryList.add(itemDetail);
        }
        cancelWorkdetail();
    }

    public void addCoWokerItem() {
        if (!coWorkerList.contains(coWorkerDetail)) {
            coWorkerList.add(coWorkerDetail);
        }
        cancelWorkdetail();
    }

    public void deleteWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
    }

    public void cancelWorkdetail() {
        itemDetail = null;
        coWorkerDetail = null;
    }

    public List<AssetFaultType> getAssetFaultType() {
        AssetInfo assetinfo = assetDao.findById(this.selectedWorkOrder.getAssetId());
        return assetFaultTypeService.getFaultTypeList(assetinfo.getAssetGroup());
    }
    
    public String getFaultTypeName(Integer id){
        return assetFaultTypeService.getFaultName(id);
    }
    
    public OrgInfo getOrgInfoByUid(String orgUid){
        return orgDao.getByUid(orgUid);
    }
    
    public String getRequestReasonInShort(String reason) {
        if (reason == null) {
            return null;
        }
        if (reason.length() <= 20) {
            return reason;
        } else {
            return reason.substring(0, 19) + "...";
        }
    }

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getNeedConfirmTime() {
        return needConfirmTime;
    }

    public void setNeedConfirmTime(Boolean needConfirmTime) {
        this.needConfirmTime = needConfirmTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
    }

    public Date getEsTime() {
        return esTime;
    }

    public void setEsTime(Date esTime) {
        this.esTime = esTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getConfirmedUpDate() {
        return confirmedUpDate;
    }

    public void setConfirmedUpDate(Date confirmedUpDate) {
        this.confirmedUpDate = confirmedUpDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public List<V2_WorkOrder_Detail> getAccessoryList() {
        if (accessoryList == null) {
            return null;
        }
        V2_WorkOrder_Detail emptyDetail = new V2_WorkOrder_Detail();
        emptyDetail.setId("000");
        if (!accessoryList.contains(emptyDetail)) {
            accessoryList.add(emptyDetail);
        } else {
            accessoryList.remove(emptyDetail);
            accessoryList.add(emptyDetail);
        }
        return accessoryList;
    }

    public List<V2_WorkOrderCoworker> getCoWorkerList() {
        if (coWorkerList == null) {
            return null;
        }
        V2_WorkOrderCoworker emptyDetail = new V2_WorkOrderCoworker();
        emptyDetail.setId("000");
        if (!coWorkerList.contains(emptyDetail)) {
            coWorkerList.add(emptyDetail);
        } else {
            coWorkerList.remove(emptyDetail);
            coWorkerList.add(emptyDetail);
        }
        return coWorkerList;
    }

    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public V2_WorkOrderCoworker getCoWorkerDetail() {
        return coWorkerDetail;
    }

    public void setCoWorkerDetail(V2_WorkOrderCoworker coWorkerDetail) {
        this.coWorkerDetail = coWorkerDetail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_WorkOrder> getSelectedWorkOrderList() {
        return selectedWorkOrderList;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getStepFileter() {
        return stepFileter;
    }

    public void setStepFileter(String stepFileter) {
        this.stepFileter = stepFileter;
    }

}
