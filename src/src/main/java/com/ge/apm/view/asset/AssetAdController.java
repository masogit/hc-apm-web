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

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.AssetAdRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AssetAd;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetAdService;
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
public class AssetAdController extends GenericCRUDUUIDController<AssetAd> implements Serializable, ServerEventInterface {

	private static final long serialVersionUID = 1L;
	private AssetAdRepository dao;
    private AssetAdService service;
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<AssetAd> importFailedDataList;
    private String content;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetAdRepository.class);
        service = WebUtil.getBean(AssetAdService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<AssetAd> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetAd> loadData(PageRequest pageRequest) {
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
    public void onBeforeNewObject(AssetAd object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    public void save() {
    	String systemId=this.selected.getSystemId();
    	if(StringUtils.isNotEmpty(systemId) && service.checkSystemIdExists(systemId)){
    		super.save();
    		this.selected=null;
    	}
    	else{
    		FacesMessage message = new FacesMessage("SystemID输入有误");
            FacesContext.getCurrentInstance().addMessage(null, message);
    	}
    }
    
    @Override
    public void delete() {
    	// TODO Auto-generated method stub
    	super.delete();
    	this.selected=null;
    }
    
    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public List<AssetAd> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<AssetAd> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}
	
	public void setContent(String id){
		content=dao.getContentById(id);
	}
	
	public String getContent(){
		if(StringUtils.isNotEmpty(content)){
			return content;
		}
		return "";
	}
	
}
