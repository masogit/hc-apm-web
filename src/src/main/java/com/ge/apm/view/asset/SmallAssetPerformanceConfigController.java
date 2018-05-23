package com.ge.apm.view.asset;

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

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.SmallAssetPerformanceConfigRepository;
import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.SmallAssetPerformanceConfigService;
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
public class SmallAssetPerformanceConfigController extends GenericCRUDUUIDController<SmallAssetPerformanceConfig> implements Serializable, ServerEventInterface {

    private SmallAssetPerformanceConfigRepository dao;
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private SmallAssetPerformanceConfigService service;
    private UploadedFile file;
    private List<SmallAssetPerformanceConfig> importFailedDataList;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(SmallAssetPerformanceConfigRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        service = WebUtil.getBean(SmallAssetPerformanceConfigService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<SmallAssetPerformanceConfig> getDAO() {
        return dao;
    }

    @Override
    protected Page<SmallAssetPerformanceConfig> loadData(PageRequest pageRequest) {
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
    public void onBeforeNewObject(SmallAssetPerformanceConfig object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    public void save() {
    	if(this.selected.hashCode()==0){
    		if(service.checkConfigExists(this.selected)){
    			FacesMessage message = new FacesMessage("错误", "项目名称和设备名称唯一，请勿重复添加！");
    			this.selected=null;
    			return;
    		}
    	}
    	service.checkData(this.selected,this.selected.getId(),false);
    	super.save();
        this.selected=null;
    }
    
    @Override
    public void delete() {
    	// TODO Auto-generated method stub
    	service.checkListConfig(this.selected, this.selected.getId(), true);
    	super.delete();
    	this.selected=null;
    }
    
    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        importFailedDataList=service.handleExcel(file, currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public List<SmallAssetPerformanceConfig> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<SmallAssetPerformanceConfig> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}
	
	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return "设备绩效配置"+" "+WebUtil.getMessage(actionName);
	}
    
}
