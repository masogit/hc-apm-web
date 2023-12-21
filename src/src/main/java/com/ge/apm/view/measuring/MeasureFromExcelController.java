package com.ge.apm.view.measuring;

import com.ge.apm.domain.*;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.view.component.FilterSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MeasureFromExcelController implements ServerEventInterface {

    private MeasuringService measuringService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<MeasuringCertificateDetail> noFoundList;
    private UserAuth customRoleAuth;
    @Getter @Setter
    private OrgInfo selectedOrg;
    private FilterSiteOrgTreeComponentController filterSiteOrgTreeComponentController;
    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        measuringService = WebUtil.getBean(MeasuringService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        filterSiteOrgTreeComponentController = WebUtil.getBean(FilterSiteOrgTreeComponentController.class);
        filterSiteOrgTreeComponentController.registerController(this);
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        if (ObjectUtil.isEmpty(selectedOrg)) {
            WebUtil.addErrorMessage("所属院区必填！");
            return;
        }
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        noFoundList = measuringService.handleExcel(file, selectedOrg);
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

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selectedOrg = (OrgInfo) eventObject;
    }
}
