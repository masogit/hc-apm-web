/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
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
 *v
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2PmOrderExController extends GenericCRUDUUIDController<PmScheduleData> implements Serializable, ServerEventInterface {

    V2_PmScheduleRepository dao;
    private String dptName;
    private OrgInfoRepository orgDao;
    private V2_PmOrderService pmOrderService;
    private String assetName;
    private Integer ruleLevel;

    private Date startTime;
    private Date endTime;

    private UaaService uaaService;

    private Integer queryIndex;
    private AssetInfo selectedAsset;

    private Integer currentStatus = 1;

    private Boolean needConfirmTime = false;

    private String cancelReason;
    private String stepFileter;

    //for details form
  /*  private List<V2_WorkOrder_Detail> accessoryList;
    private List<V2_WorkOrderCoworker> coWorkerList;*/

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
    private String approvaler;



    //create service request

    private String pmOrderId;
    private String filterPlanTime;

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return orgDao.getByUid(orgUid);
    }

    protected void init() {
        dao = WebUtil.getBean(V2_PmScheduleRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        pmOrderService = WebUtil.getBean(V2_PmOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        queryIndex = -1;
        this.filterBySite = false;
        this.filterByHospital = false;

    }

    @Override
    protected GenericRepositoryUUID<PmScheduleData> getDAO() {
        return dao;
    }

    @Override
    protected Page<PmScheduleData> loadData(PageRequest pageRequest) {
        this.setSearchFilter();

        searchFilters.add(new SearchFilter("srType", SearchFilter.Operator.EQ, "PM"));
        this.selected = null;
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        List<V2_WorkOrder_Detail> list = pmOrderService.getWorkOrderDetails(woId);
        return pmOrderService.getWorkOrderDetails(woId);
    }
    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return pmOrderService.getWorkOrderCoworker(woId);
    }


    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }
    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
    }


    public void onSelectPmOrder(){
        System.out.println(selected);
      /*  accessoryList = getWorkOrderDetail(selected.getId());
        coWorkerList = getWorkOrderCoworker(selected.getId());*/

        // 获取picList
        //  picList =  blobDao.findByBoUuid(selected.getId());
    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        assetName=asset.getName();
        if(Strings.isNullOrEmpty(asset.getClinicalDeptName()) )  {
            dptName=null;
        }
        else{
            dptName=asset.getClinicalDeptName();
        }

        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (Objects.equals(currentStatus, 1)) {
            currentStatus = 3;
        }
        needConfirmTime = false;
    }



    private V2_WorkOrderCoworkerRepository coWorkerDao;



    public void setPlanTimeFilter() {
        /*if (filterPlanTime == null || !"1".equals(filterPlanTime)) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        //1周内所有未完成
        Date startTime = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        c.add(Calendar.DAY_OF_WEEK, 1);
        varPlanTimeTo = c.getTime();
        searchFilters.add(new SearchFilter("planTime", SearchFilter.Operator.LTE, varPlanTimeTo));
        searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, false));
        filterPlanTime = null;
        filterIsFinished = "false";*/

        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if(pmOrderId != null){
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, pmOrderId));
        }
    }

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
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

   /* public V2_PmOrder getSelectedPmOrder() {
        return selectedPmOrder;
    }

    public void setSelectedPmOrder(V2_PmOrder selectedPmOrder) {
        this.selectedPmOrder = selectedPmOrder;
    }*/

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

  /*  public void setCoWorkerList(List<V2_WorkOrderCoworker> coWorkerList) {
        this.coWorkerList = coWorkerList;
    }

    public V2_WorkOrderCoworkerRepository getCoWorkerDao() {
        return coWorkerDao;
    }

    public void setCoWorkerDao(V2_WorkOrderCoworkerRepository coWorkerDao) {
        this.coWorkerDao = coWorkerDao;
    }*/

    public String getApprovaler() {
        return approvaler;
    }

    public void setApprovaler(String approvaler) {
        this.approvaler = approvaler;
    }

   /* public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }*/

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getDptName() {
        return dptName;
    }

    public void setDptName(String dptName) {
        this.dptName = dptName;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public String getFilterPlanTime() {
        return filterPlanTime;
    }

    public void setFilterPlanTime(String filterPlanTime) {
        this.filterPlanTime = filterPlanTime;
    }

    public String getPmOrderId() {
        return pmOrderId;
    }

    public void setPmOrderId(String pmOrderId) {
        this.pmOrderId = pmOrderId;
    }
}
