package com.ge.apm.view.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeTemplateElementRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.service.orginfo.OrgInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.asset.QrCodeTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

@ManagedBean
@ViewScoped
public class QrCodeTemplateController extends GenericCRUDUUIDController<QrCodeTemplate> {

    QrCodeTemplateRepository dao = null;
    QrCodeTemplateService templateService;
    QrCodeService qrCodeService;
    QrCodeTemplateElementRepository elementDao;
    OrgInfoRepository orgDao;
    UaaService uaaService;
    TenantInfoRepository tenantInfoDao;
    UserContextService userContextService;
    OrgInfoService orgInfoService;

    String selectedTenantUid;
    List<String> accessibleOrgInfoList;

    @Override
    protected void init() {
        dao = WebUtil.getBean(QrCodeTemplateRepository.class);
        templateService = WebUtil.getBean(QrCodeTemplateService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        elementDao = WebUtil.getBean(QrCodeTemplateElementRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoService = WebUtil.getBean(OrgInfoService.class);
        accessibleOrgInfoList = uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
    }

    @Override
    protected QrCodeTemplateRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<QrCodeTemplate> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<SearchFilter>();
        }
        if (selectedTenantUid != null) {
            this.removeFilterOnField("tenantUID");
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedTenantUid));
        }
        if (!userContextService.hasRole("SuperAdmin")) {
            if (!hasFilterOnField("siteUID")) {
                List<String> orgList = getHospitalFileterList().stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgList));
            }
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public String getTenantName(String tenantUid) {
        return Optional.ofNullable(tenantInfoDao.findByuid(tenantUid)).orElse(new TenantInfo()).getName();
    }

    public String getEditLink() {
        return "CreateTemplate.xhtml?faces-redirect=true&action=Edit&templateId=" + selected.getId();
    }

    public String getPreViewBase64() {
        return selected == null ? "" : qrCodeService.getPreViewBase64(selected.getId());
    }

    @Transactional
    public void deleteTemplate() {
        if (selected.isIsUsing()) {
            WebUtil.addErrorMessage("不能删除当前使用模版");
        } else {
            List<QrCodeTemplateElement> elements = templateService.getElementsByTemplate(selected.getId());
            elementDao.delete(elements);
            dao.delete(selected);
        }
    }

    @Transactional
    public void setToUsing(String templateId, Boolean value) {
        QrCodeTemplate usingTemplate = dao.findById(templateId);
        if (value) {
            List<SearchFilter> templateFilters = new ArrayList();
            templateFilters.add(new SearchFilter("isUsing", SearchFilter.Operator.EQ, true));
            templateFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, usingTemplate.getSiteUID()));
            List<QrCodeTemplate> usingList = dao.findBySearchFilter(templateFilters);
            usingList.forEach(item -> item.setIsUsing(false));
            dao.save(usingList);
            usingTemplate.setIsUsing(true);
            usingTemplate.setUsingDate(new Date());
            dao.save(usingTemplate);
        } else {
            usingTemplate.setIsUsing(false);
            dao.save(usingTemplate);
        }

    }

    public void onTenantChange() {

    }

    public List<OrgInfo> getHospitalFileterList() {
        List<OrgInfo> orgInfoList = null;
        if (null != selectedTenantUid) {
            orgInfoList = orgDao.getByTenantUID(selectedTenantUid).stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        } else {
            UserAccount user = UserContextService.getCurrentUserAccount();
            orgInfoList = uaaService.getUserAccessibleSiteList(user.getUid());
            orgInfoList.add(orgDao.getByUid(user.getHospitalUID()));
            orgInfoList.add(orgDao.getByUid(user.getInstitutionUID()));
            orgInfoList.add(orgDao.getByUid(user.getTenantUID()));
            orgInfoList = orgInfoList.stream().distinct().collect(Collectors.toList());
        }
        if (orgInfoList == null) {
            return Collections.emptyList();
        } else {
            orgInfoList = orgInfoService.flattenOrgTree(orgInfoList);
            return orgInfoList;
        }

    }

    public boolean canEdit(String siteUID) {
        if(!Strings.isNullOrEmpty(siteUID)){
            return accessibleOrgInfoList.contains(siteUID);
        }
        
        if (selected == null) {
            return false;
        }else{
            siteUID = selected.getSiteUID();
        }
        return accessibleOrgInfoList.contains(siteUID);
    }

    public String getHospitalName(String orgUid) {
        return Optional.ofNullable(orgDao.getByUid(orgUid)).orElse(new OrgInfo()).getName();
    }

    public List<TenantInfo> getTenantList() {
        List<TenantInfo> tenantList = tenantInfoDao.find();
        return tenantList;
    }

    public String getSelectedTenantUid() {
        return selectedTenantUid;
    }

    public void setSelectedTenantUid(String selectedTenantUid) {
        this.selectedTenantUid = selectedTenantUid;
    }

}
