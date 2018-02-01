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
public class V2PmOrderExController extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable {

    AssetLifecycleEventRepository dao;
    private UaaService uaaService;

    protected void init() {
        dao = WebUtil.getBean(AssetLifecycleEventRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
    }

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "4"));//4外部保养
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
