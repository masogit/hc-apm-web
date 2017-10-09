/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
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
import java.util.ArrayList;
import java.util.List;

import static webapp.framework.dao.SearchFilter.Operator.EQ;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderFromReportController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable, ServerEventInterface  {

    private static final long serialVersionUID = 1L;
    private UserAccount currentUser;
    private V2_WorkOrderRepository dao;
    private List<V2_WorkOrder> workOrders;
    private List<String> clinicalDeptNameList;
    private UaaService uaaService;
    private V2_WorkOrderService woService;
    private List<UserAccount> usersInHospital;

    @Override
    protected void init() {
        super.init();
        this.currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
    }


    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        String woId = WebUtil.getRequestParameter("woId");
        this.selected = Strings.isNullOrEmpty(woId) ? null : dao.findById(woId);
        this.loadSrDetails();
        this.usersInHospital = uaaService.getUserList(this.currentUser.getHospitalId());
        this.clinicalDeptNameList = dao.getClinicalDeptNameList(this.currentUser.getHospitalId(), this.currentUser.getSiteId());
        this.setSearchFilter();
        /*this.searchFilters.add(new SearchFilter("status", EQ, 1));
        this.searchFilters.add(new SearchFilter("hospitalId", EQ, this.currentUser.getHospitalId()));
        this.searchFilters.add(new SearchFilter("siteId", EQ, this.currentUser.getSiteId()));*/
        this.searchFilters.add(new SearchFilter("id", EQ, woId));
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    public List<String> getClinicalDeptNameList() {
        return clinicalDeptNameList;
    }

    public void setClinicalDeptNameList(List<String> clinicalDeptNameList) {
        this.clinicalDeptNameList = clinicalDeptNameList;
    }

    public void onSelectServiceRequest() {
        this.loadSrDetails();
    }

    public void loadSrDetails() {
        this.workOrders = new ArrayList<>();
        //this.workOrders.add(dao.findById(selected.getId()));
        if(selected!=null) this.workOrders.add(selected);
    }

    public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }

    public List<V2_WorkOrder>   getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<V2_WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    public List<UserAccount> getUsersInHospital() {
        return usersInHospital;
    }

    public void setUsersInHospital(List<UserAccount> usersInHospital) {
        this.usersInHospital = usersInHospital;
    }
}
