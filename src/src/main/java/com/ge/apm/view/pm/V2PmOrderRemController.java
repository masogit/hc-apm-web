/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *v
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2PmOrderRemController extends GenericCRUDUUIDController<PmScheduleData> implements Serializable {

    V2_PmScheduleRepository dao;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;

    protected void init() {
        dao = WebUtil.getBean(V2_PmScheduleRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
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

        searchFilters.add(new SearchFilter("srType", SearchFilter.Operator.EQ, "ePM"));
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
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }
    public void searchList(Integer buttonIndex) {
        this.searchFilters = null;
    }


    public void onSelectPmOrder(){
        System.out.println(selected);
      /*  accessoryList = getWorkOrderDetail(selected.getId());
        coWorkerList = getWorkOrderCoworker(selected.getId());*/

        // 获取picList
        //  picList =  blobDao.findByBoUuid(selected.getId());
    }




}
