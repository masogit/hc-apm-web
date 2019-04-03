package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.NonAssetInfoRepository;
import com.ge.apm.domain.NonAssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class NonAssetInfoController extends GenericCRUDUUIDController<NonAssetInfo> implements Serializable, ServerEventInterface {

    private NonAssetInfoRepository dao;
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    @Override
    protected void init() {
        dao = WebUtil.getBean(NonAssetInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<NonAssetInfo> getDAO() {
        return dao;
    }

    @Override
    protected Page<NonAssetInfo> loadData(PageRequest pageRequest) {
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
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
    	// TODO Auto-generated method stub
    	super.prepareCreate();
    	selected.setCreatedBy(currentUser.getUid());
    	selected.setCreatedDate(new Date());
    }
    @Override
    public void prepareEdit() {
    	// TODO Auto-generated method stub
    	super.prepareEdit();
    	selected.setLastModifiedBy(currentUser.getUid());
    	selected.setLastModifiedDate(new Date());
    }

    @Override
    public void onBeforeNewObject(NonAssetInfo object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    public void delete() {
    	// TODO Auto-generated method stub
    	super.delete();
    	this.selected=null;
    }
	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return "资产"+" "+WebUtil.getMessage(actionName);
	}

	
}
