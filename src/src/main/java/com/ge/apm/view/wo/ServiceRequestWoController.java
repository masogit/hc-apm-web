/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.wo.V2_WorkOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class ServiceRequestWoController extends ServiceRequestController implements Serializable, ServerEventInterface {

    private static final long serialVersionUID = 1L;
    private boolean isClosed;
    private Integer assetId;
    private String woId;
    ServiceRequestRepository dao;
    V2_WorkOrderRepository woDao;
    List<V2_WorkOrder> workOrders;
    private V2_WorkOrderService woService;
    Integer queryIndex;
    private V2_ServiceRequest newServiceRequest;
    private AssetInfo selectedAsset;
    private Integer currentStatus = 1;
    private Boolean needConfirmTime = false;
    private String cancelReason;

    protected void init() {
        dao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        woDao = WebUtil.getBean(V2_WorkOrderRepository.class);
        this.filterBySite = true;
        queryIndex = 1;
        assetId=Integer.valueOf(WebUtil.getRequestParameter("assetId"));
        woId=WebUtil.getRequestParameter("woId");
    }
    public void onSelectServiceRequest() {
        workOrders = woService.getWorkOrdersBySR(selected.getId());
    }

    @Override
    protected GenericRepositoryUUID<V2_ServiceRequest> getDAO() {
        return dao;
    }


    @Override
    protected Page<V2_ServiceRequest> loadData(PageRequest pageRequest) {
        this.setSiteFilter();
        V2_WorkOrder workOrder = woDao.findById(woId);
        String srId= workOrder.getSrId();
        removeFilterOnField("status");
        searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, srId));
        this.selected = null;
        this.workOrders = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getRequestReasonInShort(V2_ServiceRequest item) {
        if (item.getRequestReason() == null) {
            return null;
        }
        if (item.getRequestReason().length() <= 20) {
            return item.getRequestReason();
        } else {
            return item.getRequestReason().substring(1, 20) + "...";
        }
    }

    //getter and setter
    public List<V2_WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<V2_WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
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

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

}
