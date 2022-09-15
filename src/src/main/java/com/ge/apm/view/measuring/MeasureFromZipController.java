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
import java.util.List;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
public class MeasureFromZipController {

    private MeasuringService measuringService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<MeasuringCertificateDetail> noFoundList;
    private UserAuth customRoleAuth;

    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        measuringService = WebUtil.getBean(MeasuringService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
    }

    public void handleZipFileUpload(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        noFoundList = measuringService.handleZip(event.getFile(), currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<MeasuringCertificateDetail> getNoFoundList() {
        return noFoundList;
    }

    public void setNoFoundList(List<MeasuringCertificateDetail> noFoundList) {
        this.noFoundList = noFoundList;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public void setCustomRoleAuth(UserAuth customRoleAuth) {
        this.customRoleAuth = customRoleAuth;
    }
}
