package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.mapper.SupplierEvaluationRepository;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean
@ViewScoped
public class SupplierEvaluationController extends JpaCRUDController<SupplierEvaluation> {

    /*接口类*/
    private SupplierEvaluationRepository dao;
    private AssetInfoRepository assetInfoDao;

    /*属性类*/
    private UserAccount currentUser;
    @Getter@Setter
    private Integer assetGroup;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierEvaluationRepository.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = false;
    }

    @Override
    protected SupplierEvaluationRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<SupplierEvaluation> loadData(PageRequest pageRequest) {
        if(this.searchFilters ==null){
            searchFilters =  new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,UserContextService.getCurrentUserAccount().getTenantUID()));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    @Override
    public void prepareEdit(){
        super.prepareEdit();
    }

    public void save(){
        selected.setLastModifiedDate(new Date());
        selected.setLastModifiedBy(currentUser.getLoginName());
        selected.setInstitutionUID(currentUser.getInstitutionUID());
        selected.setHospitalUID(currentUser.getHospitalUID());
        selected.setSiteUID(currentUser.getSiteUID());
        selected.setSiteId(currentUser.getSiteId());
        selected.setTenantUID(currentUser.getTenantUID());
        try {
            dao.save(selected);
            WebUtil.addSuccessMessage(WebUtil.getMessage("SaveSuccess"));
        } catch (Exception e) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("Failed"));
        }
    }

    public void delete() {
        try {
            dao.delete(selected);
            WebUtil.addSuccessMessage(WebUtil.getMessage("DeleteSuccess"));
        } catch (Exception e) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("Failed"));
        }
    }
}