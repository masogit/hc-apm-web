/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderDispatchController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {

    V2_WorkOrderRepository dao;

    private ServiceRequestRepository srDao;
    private V2_WorkOrderService woService;
    private UaaService uaaService;

    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;

    private V2_ServiceRequest selectedServiceRequest;

    private V2_WorkOrder selectedWorkOrder;

    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        srDao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        queryIndex = 2;
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
        removeFilterOnField("dispatchMode");
        if (queryIndex == 5) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
        }else if(queryIndex == 2){
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2,4)));
        }else if(queryIndex == 3){
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GT, 2));
        }else if(queryIndex == 4){
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ,2 ));
        }
        
        this.selected = null;
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }
    
    @Override
    public void setSearchFilter() {
         if(this.searchFilters==null){
            searchFilters = new ArrayList();
        }
        if(!this.hasFilterOnField("siteUID")){
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).distinct().collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList,"1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList,"1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList,"1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList,"1*2+"));
        }
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        cancel();
    }

    public void cancel() {
        selected=null;
        selectedServiceRequest = null;
    }

    public V2_ServiceRequest getServiceRequest(String srId){
        return srDao.findById(srId);
    }

    public void onSelectWorkOrder() {
        selectedServiceRequest = srDao.findById(selected.getSrId());
    }

    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }

    public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }

    public void prepareDispatch(String woid){
        selectedWorkOrder = dao.findById(woid);
    }


    public List<UserAccount> getWorkerList(){
        return woService.getWorkerList(selectedWorkOrder.getAssetId());
    }

    public void dispatchWorkOrder(){
        woService.dispatchWorkOrder(selectedWorkOrder);
        cancel();
    }


    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public V2_ServiceRequest getSelectedServiceRequest() {
        return selectedServiceRequest;
    }

    public void setSelectedServiceRequest(V2_ServiceRequest selectedServiceRequest) {
        this.selectedServiceRequest = selectedServiceRequest;
    }

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
    }



}