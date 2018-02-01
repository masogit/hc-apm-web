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


@ManagedBean
@ViewScoped
public class V2PmOrderRemController extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable{

    private AssetLifecycleEventRepository dao;
    private UserAccountRepository userDao;
    private UaaService uaaService;
    protected void init() {
        dao = WebUtil.getBean(AssetLifecycleEventRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);

        this.filterBySite = false;
        this.filterByHospital = false;

    }
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "5"));//4外部保养
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
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
        }
    }
    public String getEvStatus(int status) {
        if (status == 1) {
            return "open";
        }
        if (status == 2) {
            return "close";
        }

        return "cancel";

    }


}
