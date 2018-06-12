package com.ge.apm.view.measuring;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.ElectricSafetyDataRepository;
import com.ge.apm.domain.ElectricSafetyData;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.ElectricSafetyService;
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
public class MeasureElectricSafetyController extends GenericCRUDUUIDController<ElectricSafetyData> implements Serializable, ServerEventInterface{

    private ElectricSafetyService electricSafetyService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<ElectricSafetyData> dataList;
    private List<ElectricSafetyData> selectedItems;
    private ElectricSafetyDataRepository dao;

    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        electricSafetyService = WebUtil.getBean(ElectricSafetyService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        dao=WebUtil.getBean(ElectricSafetyDataRepository.class);
    }
    
    @Override
    protected Page<ElectricSafetyData> loadData(PageRequest pageRequest) {
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
        Page<ElectricSafetyData> result=dao.findBySearchFilter(searchFilters, pageRequest);
        return result;
    }
    
    @Override
    public void onBeforeNewObject(ElectricSafetyData object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage("解析完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        dataList=electricSafetyService.handlerPsFile(file, currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void importSelectedItem(){
    	if(selectedItems!=null&&selectedItems.size()>0){
    		electricSafetyService.insertUpdateData(selectedItems);
    	}
    	else{
    		electricSafetyService.insertUpdateData(dataList);
    	}
    	FacesMessage message = new FacesMessage("导入完成");
    	FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<ElectricSafetyData> getDataList() {
        return dataList;
    }

    public void setDataList(List<ElectricSafetyData> dataList) {
        this.dataList = dataList;
    }

	public List<ElectricSafetyData> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<ElectricSafetyData> selectedItems) {
		this.selectedItems = selectedItems;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected GenericRepositoryUUID<ElectricSafetyData> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}
    
    
}
