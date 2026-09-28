package com.ge.apm.view.measuring;

import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.domain.MeasuringCertificateDetail;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
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
import java.util.Objects;

/**
 * Created by 212605082 on 2018/1/8.
 */
@ManagedBean
@ViewScoped
public class MeasuringResultListController extends GenericCRUDUUIDController<MeasuringCertificateDetail> implements Serializable, ServerEventInterface {

    private MeasuringCertificateDetailRepository dao;
    private UserAccount currentUser;

    @Override
    protected void init() {
        dao = WebUtil.getBean(MeasuringCertificateDetailRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<MeasuringCertificateDetail> getDAO() {
        return dao;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    protected Page<MeasuringCertificateDetail> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));

        if(!Objects.equals(currentUser.getOrgLevel(), 0)){
            if(Objects.equals(currentUser.getOrgLevel(), 1)){
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
            }else if(Objects.equals(currentUser.getOrgLevel(), 2)){
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
            }else{
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            }
        }

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

}
