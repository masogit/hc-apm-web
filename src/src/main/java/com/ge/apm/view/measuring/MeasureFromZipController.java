package com.ge.apm.view.measuring;

import com.ge.apm.domain.*;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
public class MeasureFromZipController {

    private MeasuringService measuringService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<MeasuringCertificateDetail> resultList;
    private UserAuth customRoleAuth;
    private int progress;
    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        measuringService = WebUtil.getBean(MeasuringService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        measuringService.setProgress(0);
    }

    public void handleZipFileUpload(FileUploadEvent event) throws IOException {
        resultList = new ArrayList<>();
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        resultList = measuringService.handleZip(event.getFile(), currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    public void handleZipFileUploadOCR(FileUploadEvent event) throws IOException {
        resultList = new ArrayList<>();
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        resultList = measuringService.handleZipOCR(event.getFile(), currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<MeasuringCertificateDetail> getResultList() {
        return resultList;
    }

    public void setNoFoundList(List<MeasuringCertificateDetail> resultList) {
        this.resultList = resultList;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public void setCustomRoleAuth(UserAuth customRoleAuth) {
        this.customRoleAuth = customRoleAuth;
    }

    public void handleComplete() {
        //Add a faces message
    }
    public int getProgress() {

        return measuringService.getProgress();
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
