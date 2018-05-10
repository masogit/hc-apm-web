package com.ge.apm.view.uaa;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UserAccountService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class UserAccountImportController extends GenericCRUDController<UserAccount> implements Serializable, ServerEventInterface {

    private UserAccountRepository dao;
    private UserAccount currentUser;
    private UserAccountService service;
    private UploadedFile file;
    private List<UserAccount> importFailedDataList;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(UserAccountRepository.class);
        service = WebUtil.getBean(UserAccountService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }
    
    @Override
    protected GenericRepository<UserAccount> getDAO() {
    	// TODO Auto-generated method stub
    	return dao;
    }

    @Override
    protected Page<UserAccount> loadData(PageRequest pageRequest) {
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
    public void onBeforeNewObject(UserAccount object) {
        super.onBeforeNewObject(object);
    }
    
    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        this.file = event.getFile();
        importFailedDataList=service.handleExcel(file, currentUser);
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public List<UserAccount> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<UserAccount> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}
    
}
