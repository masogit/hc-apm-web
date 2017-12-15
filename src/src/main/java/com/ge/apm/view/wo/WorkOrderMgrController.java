/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkOrderCoworkerRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.domain.AssetFaultType;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrderCoworker;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
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
    private UserAccountRepository userDao;
    private V2_WorkOrderService woService;
    private UaaService uaaService;
    private UserContextService userContextService;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private WorkOrderDetailRepository detailDao;
    private V2_WorkOrderCoworkerRepository coWorkerDao;
    private String locationName;
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
    private Date takeTime;

    private List<V2_BlobObject> picList;
    private List<V2_BlobObject> audioList;

    private List<Integer> tagGroupAssets;
    private List<Integer> tagNoGroupAssets;

    private String requestAssetUid;

    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetFaultTypeService = WebUtil.getBean(AssetFaultTypeService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        detailDao = WebUtil.getBean(WorkOrderDetailRepository.class);
        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        queryIndex = 0;
        this.filterBySite = false;
        this.filterByHospital = false;

        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            requestAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "assetUID");
        }

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
        removeFilterOnField("assetInfo.institutionUID");
        removeFilterOnField("assetInfo.siteUID");
        removeFilterOnField("assetInfo.hosputalUID");
        removeFilterOnField("assetInfo.tenantUID");
        removeFilterOnField("approvalStatus");
        removeFilterOnField("assetInfo.assetOwnerId");
        removeFilterOnField("assetInfo.assetOwnerId2");
        removeFilterOnField("assetInfo.assetOwnerId3");

        if (queryIndex == 1) {//已报修 可取消
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1))));
            }
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.LTE, 3));
        } else if (queryIndex == 2) { //待派工
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).distinct().collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*2+"));

        } else if (queryIndex == 3) { //待接单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3), "1*3+4*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getId(), "1*3+4*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*3+4*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*3+4*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*3+4*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*3+4*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*3+4*6+owner3*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*3+4*6+owner3*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
            if (!getTagGroupAssets().isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, getTagGroupAssets(), "1*3+4*5+"));
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, getNoTagGroupAssets(), "1*3+4*5+"));
            }

        } else if (queryIndex == 4) { //维修中
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        } else if (queryIndex == 5) { //待验收
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1))));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            } else {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            }
        } else if (queryIndex == 6) { //待评价
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1, 2))));
            }
            searchFilters.add(new SearchFilter("feedbackRating", SearchFilter.Operator.EQ, 0));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
        } else if (queryIndex == 7) { //待关单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
        } else if (queryIndex == 8) { //已关单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
        } else if (queryIndex == 9) { //已取消
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
        } else if (queryIndex == 10) { //审批
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
            searchFilters.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
            searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1))));
        } else {//全部
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
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("serviceRequest.requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
            searchFilters.add(new SearchFilter("serviceRequest.fromDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    private List<Integer> getNoTagGroupAssets() {
        if (tagNoGroupAssets == null) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            tagNoGroupAssets = dao.getAssetsByNoTagGroup(orgUidList);
        }
        return tagNoGroupAssets;
    }

    private List<Integer> getTagGroupAssets() {
        if (tagGroupAssets == null) {
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
        picList = null;
        selectedAsset = null;
        requestAssetUid = null;
        locationName = null;
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
        audioList = getServiceRequestPicList(selected.getSrId(), 2);
        picList = getServiceRequestPicList(selected.getSrId(), 1);
    }

  /*  public void locateAsset() {
        System.out.println();
        if (selectedAsset.getLocationName().isEmpty()) {
            final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
            String lName = orgDao.getByUid(orgInfoUID).getName();
            selectedAsset.setLocationName(lName);
            locationName = lName;
        }
    }*/

    private List<V2_BlobObject> getServiceRequestPicList(String srId, Integer type) { //type 1:picture 2:audio
        return woService.getServiceRequestPicList(srId).stream().filter(item -> type == 1 ^ item.getObjectType().contains("audio")).collect(Collectors.toList());
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
        currentStatus = selectedWorkOrder.getAssetInfo().getStatus();
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
    }

    public void prepareCloseOrder(String woid) {
        selectedWorkOrder = dao.findById(woid);
        picList = new ArrayList<V2_BlobObject>();
        equipmentTaker = selectedWorkOrder.getServiceRequest().getEquipmentTaker();
        takeTime = selectedWorkOrder.getServiceRequest().getTakeTime();
    }

    public void prepareCompleteOrder(String woid) {
        selectedWorkOrder = dao.findById(woid);
        confirmedUpDate = selectedWorkOrder.getServiceRequest().getConfirmedUpTime();
        currentStatus = 1;//selectedWorkOrder.getAssetInfo().getStatus();
        accessoryList = getWorkOrderDetail(selectedWorkOrder.getId());
        coWorkerList = getWorkOrderCoworker(selectedWorkOrder.getId());
        picList = new ArrayList<V2_BlobObject>();
    }
    
    public void saveRepairWorkOrder(){
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        woService.saveRepairWorkOrder(selectedWorkOrder, confirmedUpDate, currentStatus, accessoryList, coWorkerList, comments, picList);
        cancel();
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
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        woService.repairWorkOrder(selectedWorkOrder, confirmedUpDate, currentStatus, accessoryList, coWorkerList, comments, picList);
        cancel();
    }

    public void cancelWorkOrder() {
        woService.cancelWorkOrder(selectedWorkOrder.getId(), cancelReason);
        cancelReason = null;
        cancel();
    }

    public void takeEquip() {
        if (Strings.isNullOrEmpty(equipmentTaker)) {
            WebUtil.addErrorMessage("取物人" + WebUtil.getMessage("ValidationRequire"));
        } else if (equipmentTaker.trim().isEmpty()) {
            WebUtil.addErrorMessage("取物人" + WebUtil.getMessage("ValidationRequire"));
        } else if (takeTime != null && takeTime.after(new Date())) {
            WebUtil.addErrorMessage("取物时间大于当前时间");
            takeTime = null;
        } else {
            if (null == takeTime) {
                takeTime = new Date();
            }
            woService.takeEquip(selectedWorkOrder, equipmentTaker, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(takeTime));
        }
    }

    public void closeWorkOrder() {
        woService.closeWorkOrder(selectedWorkOrder, comments, picList);
        cancel();
    }

    public void createServiceRequest() {
        cancel();
        if (!Strings.isNullOrEmpty(requestAssetUid)) {
            selectedAsset = assetDao.findByUid(requestAssetUid);
            currentStatus = selectedAsset.getStatus();
            if (currentStatus == 1) {
                currentStatus = 3;
            }
        }
        selected = null;
        newServiceRequest = new V2_ServiceRequest();
        picList = new ArrayList();
        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
    }

    public void saveServiceRequest() {
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        }else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("所选设备没有使用科室,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(locationName);
            woService.createServiceRequest(newServiceRequest, selectedAsset, picList);
            cancel();
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        if (Strings.isNullOrEmpty(asset.getLocationName())) {
            final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
            String lName = orgDao.getByUid(orgInfoUID).getName();
            asset.setLocationName(lName);
            locationName = lName;
        } else {
            locationName = asset.getLocationName();
        }
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;

        if (this.newServiceRequest == null) {
            return;
        }


    }

    public void onStatusChange() {
        if (selectedWorkOrder != null) {
            selectedAsset = selectedWorkOrder.getAssetInfo();
        }

        if (selectedAsset == null && selected == null) {
            return;
        }
        if (null != selectedAsset) {//new sr
            needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
        } else {//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }
    }

    public void acceptWorkOrder() {
        String repairType = selectedWorkOrder.getRepairType().toString();
        woService.acceptWorkOrder(selectedWorkOrder, esTime, repairType, comments);
        cancel();
    }

    public void reassignWorkOrder() {
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
//        detailDao.save(itemDetail);
        cancelWorkdetail();
    }

    public void addCoWokerItem() {
        if (!coWorkerList.contains(coWorkerDetail)) {
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
            coWorkerList.add(coWorkerDetail);
        }
//        coWorkerDao.save(coWorkerDetail);
        cancelWorkdetail();
    }

    public void deleteWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
            detailDao.delete(itemId);
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
            coWorkerDao.delete(itemId);
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


    public String getFaultTypeName(Integer id) {
        return assetFaultTypeService.getFaultName(id);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
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

    public void removeAttachment(String storageId) {
        blobService.deleteBlobFileByStorageId(storageId);
        picList.removeAll(picList.stream().filter(item -> item.getObjectStorageId().equals(storageId)).collect(Collectors.toList()));
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ServiceRequest);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void submitWorkOrder() {
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        woService.submitWorkOrder(selectedWorkOrder, accessoryList);
        cancel();
    }

    public void approveWorkOrder(Boolean isAgree) {
        woService.approveAction(selectedWorkOrder, isAgree, comments);
        cancel();
    }
    
    public List<V2_BlobObject> getPicListByBoType(String boType){
        return picList.stream().filter(item -> boType.equals(item.getBoType())).collect(Collectors.toList());
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public Date getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Date takeTime) {
        this.takeTime = takeTime;
    }

    public String getStepFileter() {
        return stepFileter;
    }

    public void setStepFileter(String stepFileter) {
        this.stepFileter = stepFileter;
    }

    public List<V2_BlobObject> getPicList() {
        return picList;
    }

    public void setPicList(List<V2_BlobObject> picList) {
        this.picList = picList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<V2_BlobObject> audioList) {
        this.audioList = audioList;
    }

    public String getRequestAssetUid() {
        return requestAssetUid;
    }

    public void setRequestAssetUid(String requestAssetUid) {
        this.requestAssetUid = requestAssetUid;
    }

}
