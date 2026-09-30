package com.ge.apm.view.asset;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import org.primefaces.event.SelectEvent;
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
import com.google.common.base.Strings;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import webapp.framework.web.mvc.GenericCRUDController;

@ManagedBean
@ViewScoped
public class QrCodeLibCreateController extends GenericCRUDController<QrCodeLib> {

    private static final long serialVersionUID = -1;
    QrCodeLibRepository dao = null;
    TenantInfoRepository tenantdao = null;
    OrgInfoRepository orgDao = null;

    private AssetCreateService acService;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;
    UserContextService userContextService;

    String selectedSiteUid;

    private OrgInfo fileterDeptOrg;
    private OrgInfo fileterSiteOrg;
    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;
    private List<OrgInfo> clinicalDeptList;
    private Set<Integer> clinicalDeptOrgUidSet;
    private Set<Integer> assetDeptOrgIdSet;
    @Override
    protected void init() {
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        tenantdao = WebUtil.getBean(TenantInfoRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);

        this.filterBySite = false;
        loadClinicalDeptList();
    }
    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
        if (null != fileterSiteOrg) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(fileterSiteOrg.getSiteUID()) || item.getHospitalUID().equals(fileterSiteOrg.getSiteUID())
                    || item.getInstitutionUID().equals(fileterSiteOrg.getSiteUID()) || item.getTenantUID().equals(fileterSiteOrg.getSiteUID())).collect(Collectors.toList());
        }
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

        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList;
            if (userContextService.hasRole("AssetHead")) {
                orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).distinct().collect(Collectors.toList());
            } else {
                orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            }
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (null != fileterSiteOrg) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, fileterSiteOrg.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, fileterSiteOrg.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, fileterSiteOrg.getUid(), "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, fileterSiteOrg.getUid(), "1*2+"));
        }
        
        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("orgId", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "orgId".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (assetDeptOrgIdSet != null && !assetDeptOrgIdSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetDeptId", SearchFilter.Operator.IN, new ArrayList<>(assetDeptOrgIdSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptId".equals(item.fieldName) && "1*".equals(item.conditionPath)).collect(Collectors.toList()));
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

    
    public void showMsg(){
        String pageMsg = WebUtil.getGrowlMsg();
        if(!Strings.isNullOrEmpty(pageMsg)){
            WebUtil.addErrorMessage(pageMsg);
        }
    }
//    public List<TenantInfo> getSiteList() {
//
//        List<TenantInfo> res = new ArrayList();
//        if (userContextService.hasRole("SuperAdmin")) {
//            res.addAll(tenantdao.find());
//        } else {
//            res.add(tenantdao.findById(UserContextService.getCurrentUserAccount().getSiteId()));
//        }
//        return res;
//    }
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            fileterSiteOrg = (OrgInfo) event.getTreeNode().getData();
        } else {
            fileterSiteOrg = null;
        }
        onClinicalDeptFilter(null);
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            OrgInfo deptOrg = (OrgInfo) event.getTreeNode().getData();
            if (Objects.equals(deptOrg.getOrgType(), 4)) {
                fileterDeptOrg =deptOrg;
                return;
            }
        }
        fileterDeptOrg = null;

    }
    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }

        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getId).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }

    }

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterAssetDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterAssetDeptOrg.equals(item.getName()))) {
            filterAssetDeptOrg = null;
        }

        if (filterAssetDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterAssetDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            assetDeptOrgIdSet = matchedList.stream().map(OrgInfo::getId).collect(Collectors.toSet());
        } else {
            assetDeptOrgIdSet = Collections.emptySet();
        }

    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }
    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public String getFilterAssetDeptOrg() {
        return filterAssetDeptOrg;
    }

    public void setFilterAssetDeptOrg(String filterAssetDeptOrg) {
        this.filterAssetDeptOrg = filterAssetDeptOrg;
    }

    public String getHospitalName(String siteUid) {
        return acService.getSiteName(siteUid);
    }

//    public String getSiteName(Integer siteId) {
//        return acService.getTenantName(siteId);
//    }
    public String getOrgName(Integer orgid) {
        return acService.getOrgName(orgid);
    }

    public void onSiteChange() {

    }

    public List<OrgInfo> getHospitalList() {
        return uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid());
    }

    public List<OrgInfo> getOrgList() {
        return Strings.isNullOrEmpty(selectedSiteUid) ? null : acService.getClinicalDeptList(selectedSiteUid);
    }

    public String getSelectedSiteUid() {
        return selectedSiteUid;
    }

    public void setSelectedSiteUid(String selectedSiteUid) {
        this.selectedSiteUid = selectedSiteUid;
    }

    public OrgInfo getFileterDeptOrg() {
        return fileterDeptOrg;
    }

    public void setFileterDeptOrg(OrgInfo fileterDeptOrg) {
        this.fileterDeptOrg = fileterDeptOrg;
    }

    public OrgInfo getFileterSiteOrg() {
        return fileterSiteOrg;
    }

    public void setFileterSiteOrg(OrgInfo fileterSiteOrg) {
        this.fileterSiteOrg = fileterSiteOrg;
    }

}
