package com.ge.apm.view.measuring;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import com.ge.apm.domain.ElectricSafetyData;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.ElectricSafetyService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.web.WebUtil;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MeasureElectricSafetyController {

    private ElectricSafetyService electricSafetyService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<ElectricSafetyData> dataList;
    private List<ElectricSafetyData> selectedItems;

    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        electricSafetyService = WebUtil.getBean(ElectricSafetyService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        dataList=electricSafetyService.handlerPsFile(file, currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void importSelectedItem(){
    	electricSafetyService.insertUpdateData(selectedItems);
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
    
    
}
