package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.AcquisitionPlanStep;
import com.ge.apm.service.acquisition.AcquisitionPlanService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class AcquisitionPlanAuditEditController  extends GenericCRUDUUIDController<AcquisitionPlan> {

    private AcquisitionPlanRepository dao;
    private AcquisitionPlanService acquisitionPlanService;
    private BlobFileService blobService;
    private UserAccount userAccount;
    private String selectedId;
    @Getter @Setter
    private String action;
    @Getter @Setter
    private List<AcquisitionPlanStep> acquisitionPlanStepList;
    @Getter @Setter
    private List<V2_BlobObject> attachList;
    @Getter @Setter
    private String auditResult;
    @Getter @Setter
    private String auditComment;



    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        acquisitionPlanService = WebUtil.getBean(AcquisitionPlanService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        userAccount = UserContextService.getCurrentUserAccount();
        selectedId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "id");
        action = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        selected = dao.findOne(selectedId);
        acquisitionPlanStepList = acquisitionPlanService.queryApplicationStep(selected.getId());
        attachList = blobService.findBlobObjectsByBoUid(selected.getId());

    }

    public String audit(){
        acquisitionPlanService.auditApplication(selected, userAccount, auditResult, auditComment);
        return "Main.xhtml?faces-redirect=true";
    }

    public String backList() {
        return "Main.xhtml?faces-redirect=true";
    }

    public Boolean showAudit(){
        Boolean result = true;
        return result;
    }


    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
