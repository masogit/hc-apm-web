package com.ge.apm.view.asset;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.service.uaa.UaaService;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class QrCodeLibCreateController extends JpaCRUDController<QrCodeLib> {

    private static final long serialVersionUID = -1;
    QrCodeLibRepository dao = null;
    TenantInfoRepository tenantdao = null;
    OrgInfoRepository orgDao = null;

    private AssetCreateService acService;
    private UaaService uaaService;
    UserContextService userContextService;

    private SearchFilter statusFilter = new SearchFilter("status", SearchFilter.Operator.EQ, 2);

    @Override
    protected void init() {
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        tenantdao = WebUtil.getBean(TenantInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        userContextService = WebUtil.getBean(UserContextService.class);

        this.filterBySite = false;
//        if (userContextService.hasRole("SuperAdmin")) {
//            this.filterByHospital = false;
//            this.filterBySite = false;
//        } else {
//            this.filterBySite = true;
//        }
//        searchFilters.add(statusFilter);

    }

    @Override
    protected QrCodeLibRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeLib> loadData(PageRequest pageRequest) {
        setSearchFilter();
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID") && !userContextService.hasRole("SuperAdmin")) {
            if (userContextService.hasRole("AssetHead")) {
                List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).distinct().collect(Collectors.toList());
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            } else {
                List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }
        if (!this.hasFilterOnField("status")) {
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
        }
    }

    @Override
    public List<QrCodeLib> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public List<TenantInfo> getSiteList() {

        List<TenantInfo> res = new ArrayList();
        if (userContextService.hasRole("SuperAdmin")) {
            res.addAll(tenantdao.find());
        } else {
            res.add(tenantdao.findById(UserContextService.getCurrentUserAccount().getSiteId()));
        }
        return res;
    }

    public String getHospitalName(Integer hospitalId) {
        return acService.getHospitalName(hospitalId);
    }

    public String getSiteName(Integer siteId) {
        return acService.getTenantName(siteId);
    }

    public String getOrgName(Integer orgid) {
        return acService.getOrgName(orgid);
    }

    public List<OrgInfo> getHospitalList() {
        List<OrgInfo> res = new ArrayList<OrgInfo>();
        if (userContextService.hasRole("SuperAdmin")) {
            res.addAll(orgDao.find());
        } else {
            res.add(orgDao.findById(UserContextService.getCurrentUserAccount().getHospitalId()));
        }
        return res;
    }

    public List<OrgInfo> getOrgList() {
        return acService.getClinicalDeptList(UserContextService.getCurrentUserAccount().getHospitalId());
    }

}
