package com.ge.apm.view.measuring;

import com.ge.apm.dao.MeasuringTypeConfigRepository;
import com.ge.apm.domain.MeasuringTypeConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MeasureFromExcelController extends GenericCRUDUUIDController<MeasuringTypeConfig> implements Serializable, ServerEventInterface {

    private MeasuringTypeConfigRepository dao;
    private MeasuringService measuringService;
    private UserAccount currentUser;
    private UploadedFile file;

    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(MeasuringTypeConfigRepository.class);
        measuringService = WebUtil.getBean(MeasuringService.class);
        currentUser = UserContextService.getCurrentUserAccount();
    }

    @Override
    protected GenericRepositoryUUID<MeasuringTypeConfig> getDAO() {
        return dao;
    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " 上传成功！");
        measuringService.handleExcel(file, currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
