package com.ge.apm.view.asset;

import com.ge.apm.dao.ContractInfoRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.stream.Collectors;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

@ManagedBean
@ViewScoped
public class ContractInfoController extends GenericCRUDUUIDController<ContractInfo> {

    UaaService uaaService;
    ContractInfoRepository dao = null;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ContractInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
    }

    @Override
    protected ContractInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ContractInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }

        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }
    
    public String createAction() {
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Create");
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Edit&selectedid=".concat(selected.getId()));
    }

}
