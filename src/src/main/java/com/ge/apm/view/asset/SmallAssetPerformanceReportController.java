package com.ge.apm.view.asset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.SmallAssetPerformanceReportRepository;
import com.ge.apm.dao.SmallAssetPerformanceReportRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.SmallAssetPerformanceReport;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class SmallAssetPerformanceReportController extends GenericCRUDUUIDController<SmallAssetPerformanceReport> implements Serializable, ServerEventInterface {

    private SmallAssetPerformanceReportRepository dao;
    private I18nMessageRepository i18nDao;

    private UserAccount currentUser;


    @Override
    protected void init() {
        dao = WebUtil.getBean(SmallAssetPerformanceReportRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<SmallAssetPerformanceReport> getDAO() {
        return dao;
    }

    @Override
    protected Page<SmallAssetPerformanceReport> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        if(currentUser.getOrgLevel() != 0){
            if(currentUser.getOrgLevel() == 1){
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
            }else if(currentUser.getOrgLevel() == 2){
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
            }else{
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            }
        }
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    public void onBeforeNewObject(SmallAssetPerformanceReport object) {
        super.onBeforeNewObject(object);

        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());

    }

    @Override
    public void save() {
        super.save();
        this.selected=null;
    }
}
