/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderMgrOutController extends GenericCRUDUUIDController<ExternalWorkOrder> implements Serializable, ServerEventInterface {

    private ExWorkOrderRepository dao;

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
    private String assetStatus;

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
        dao = WebUtil.getBean(ExWorkOrderRepository.class);
        queryIndex = 0;
        this.filterBySite = false;
        this.filterByHospital = false;

        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            requestAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "assetUID");
        }

    }

    @Override
    protected GenericRepositoryUUID<ExternalWorkOrder> getDAO() {
        return dao;
    }

    private ExWorkOrderRepository exWoDao;
  /*  public List<ExternalWorkOrder> getExWordOrder(){
        List<ExternalWorkOrder> exWorkOrderList = exWoDao.getExWorkOrderList(this.selected.getUid());
        return exWorkOrderList;

    }*/
    @Override
    protected Page<ExternalWorkOrder> loadData(PageRequest pageRequest) {

        this.setSearchFilter();
        this.selected = null;
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
       // uaaService.get
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
}
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



    public void acceptWorkOrder() {
        Integer repairType = selectedWorkOrder.getRepairType();
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

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
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
        woService.submitWorkOrder(selectedWorkOrder, accessoryList, this.comments);
        cancel();
    }

    public List<V2_BlobObject> getPicListByBoType(String boType) {
        if(picList !=null){
        return picList.stream().filter(item -> boType.equals(item.getBoType())).collect(Collectors.toList());
        }
        else
            return null;
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

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }
}
