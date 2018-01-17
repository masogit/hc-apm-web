package com.ge.apm.view.asset;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.service.asset.QrCodeService;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@ViewScoped
public class QrCodeLibController extends JpaCRUDController<QrCodeLib> {

    QrCodeLibRepository dao = null;
    TenantInfoRepository tenantInfoDao = null;
    OrgInfoRepository orgInfoDao = null;
    QrCodeLibRepository qrCodeLibDao = null;

    QrCodeService qrCodeService;

    private AssetCreateService acService;

    private StreamedContent zipFile;

    private String tenantUID;
    private String siteUID;
    private Integer qrCodeNum;
    private String qrCode;
    private String qrCodeImageBase64;

    private String tenantUIDFilter;
    private OrgInfo siteUIDFilter;
    private Integer hospitalIdFilter;
    private UserContextService userContextService;

    private List<OrgInfo> hospitalFileterList = new ArrayList();

    OrgInfo selectedOrg;

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeLibRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        qrCodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);

        userContextService = WebUtil.getBean(UserContextService.class);

        if (userContextService.hasRole("SuperAdmin")) {
            this.filterByHospital = false;
            this.filterBySite = false;
        } else {
            tenantUIDFilter = UserContextService.getCurrentUserAccount().getTenantUID();
            this.filterBySite = true;
            hospitalFileterList = orgInfoDao.getByTenantUID(tenantUIDFilter).stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        }

    }

    @Override
    protected QrCodeLibRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeLib> loadData(PageRequest pageRequest) {

        this.setSiteFilter();

         this.removeFilterOnField("siteUID");
        if(siteUIDFilter!=null && siteUIDFilter.getId()>0){
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUIDFilter.getSiteUID()));
        }

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    @Override
    public List<QrCodeLib> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public String getHospitalName(String orgUid) {
        return orgInfoDao.getByUid(orgUid).getName();
    }

    public String getSiteName(Integer siteId) {
        return acService.getTenantName(siteId);
    }

    public List<TenantInfo> getTenantList() {

        List<SearchFilter> tenantFilter = new ArrayList();
        if (filterBySite) {
            tenantFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        }
        if (filterByHospital) {
            tenantFilter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getSiteUID()));
        }

        List<TenantInfo> tenantList = tenantInfoDao.findBySearchFilter(tenantFilter);

        return tenantList;
    }

    public void createQrCode() {
        if (userContextService.hasRole("ITAdmin")) {
            tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
            //siteUID = UserContextService.getCurrentUserAccount().getSiteUID();
        }
        qrCodeService.createManyQrCode(tenantUID, selectedOrg.getSiteUID(), qrCodeNum);
        this.selectedOrg=null;
        qrCodeNum = 0;

    }

    public void showQrCode() {
        qrCode = this.selected.getQrCode();
        qrCodeImageBase64 = qrCodeService.getQrCodeImageBase64(this.selected.getQrCode());
    }

    public void onSiteChange() {
        if (tenantUID != null) {
            hospitalFileterList = orgInfoDao.getByTenantUID(tenantUID).stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        }
    }

    public void onHospitalChange(NodeSelectEvent event) {
        if (null != event.getTreeNode().getData()) {
            selectedOrg = (OrgInfo)event.getTreeNode().getData();
        }
    }
    
    public void onSiteFilterChange(NodeSelectEvent event){
        if (null != event.getTreeNode().getData()) {
            siteUIDFilter = (OrgInfo)event.getTreeNode().getData();
        }else{
            this.removeFilterOnField("siteUID");
        }
    }

    public void onTenantFilterChange() {
        if (tenantUIDFilter == null) {
            hospitalFileterList.clear();
            siteUIDFilter = null;
        } else {
            hospitalFileterList = orgInfoDao.getByTenantUID(tenantUIDFilter).stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        }
    }

    public void exportQrCode() throws Exception {
        List<QrCodeLib> exportList = dao.findBySearchFilter(searchFilters);
        List<String> qrCodeList = exportList.stream().filter(item -> item.getQrCode() != null && !item.getQrCode().isEmpty()).map(QrCodeLib::getQrCode).collect(Collectors.toList());
        zipFile = qrCodeService.exportQrCodeList(qrCodeList);
    }

    public String getFullorgName(String uid) {
        OrgInfo tempOrgInfo = orgInfoDao.getByUid(uid);
        String tempInstitutionName = orgInfoDao.getByUid(tempOrgInfo.getInstitutionUID()).getName();
        String tempHospitalName = orgInfoDao.getByUid(tempOrgInfo.getHospitalUID()).getName();
        String tempSiteName = orgInfoDao.getByUid(tempOrgInfo.getSiteUID()).getName();

        switch (tempOrgInfo.getOrgLevel()) {

            case 0:
                return tempOrgInfo.getName();
            case 1: {
                return tempInstitutionName;
            }

            case 2: {
                return tempInstitutionName + "/" + tempHospitalName;
            }

            case 3: {
                return tempInstitutionName + "/" + tempHospitalName + "/" + tempSiteName;
            }

            default:
                return "";

        }
    }

    public Boolean isChooseTenant() {
        return this.filterBySite;
    }

    public Integer getQrCodeNum() {
        return qrCodeNum;
    }

    public Integer getHospitalIdFilter() {
        return hospitalIdFilter;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getQrCodeImageBase64() {
        return qrCodeImageBase64;
    }

    public void setQrCodeNum(Integer qrCodeNum) {
        this.qrCodeNum = qrCodeNum;
    }

    public void setHospitalIdFilter(Integer hospitalIdFilter) {
        this.hospitalIdFilter = hospitalIdFilter;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setQrCodeImageBase64(String qrCodeImageBase64) {
        this.qrCodeImageBase64 = qrCodeImageBase64;
    }

    public String getTenantUIDFilter() {
        return tenantUIDFilter;
    }

    public void setTenantUIDFilter(String tenantUIDFilter) {
        this.tenantUIDFilter = tenantUIDFilter;
    }

    public OrgInfo getSiteUIDFilter() {
        return siteUIDFilter;
    }

    public void setSiteUIDFilter(OrgInfo siteUIDFilter) {
        this.siteUIDFilter = siteUIDFilter;
    }

    public List<OrgInfo> getHospitalFileterList() {
        return hospitalFileterList;
    }

    public void setHospitalFileterList(List<OrgInfo> hospitalFileterList) {
        this.hospitalFileterList = hospitalFileterList;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public StreamedContent getZipFile() {
        return zipFile;
    }

    public void setZipFile(StreamedContent zipFile) {
        this.zipFile = zipFile;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

}
