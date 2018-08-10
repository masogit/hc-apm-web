package com.ge.apm.view.pm;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderReportTemplateRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrderReportTemplate;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@ManagedBean
@ViewScoped
public class PmOrderReportTemplateController extends GenericCRUDUUIDController<PmOrderReportTemplate> {

    private static final Logger logger = Logger.getLogger(PmOrderReportTemplateController.class);
    private static final long serialVersionUID = 1L;

    private PmOrderReportTemplateService pmOrderReportTemplateService;
    private PmOrderReportTemplateRepository dao = null;

    private BlobFileService blobFileService;

    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;

    private UserAccount user;
    private Boolean isInsulate;
    private OrgInfo selectedUserOrg;

    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;

    protected void init() {
        pmOrderReportTemplateService = WebUtil.getBean(PmOrderReportTemplateService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);

        dao = WebUtil.getBean(PmOrderReportTemplateRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);

        user = UserContextService.getCurrentUserAccount();
        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
    }

    public boolean existsSiteTemplate() {
        return pmOrderReportTemplateService.existsSiteTemplate(user.getSiteUID());
    }

    @Override
    protected GenericRepositoryUUID<PmOrderReportTemplate> getDAO() {
        return dao;
    }

    /**
     * callback method while saving.
     *
     * @param object {@link PmOrderReportTemplate} object.
     */
    @Transactional
    @Override
    public void onBeforeSave(PmOrderReportTemplate object) {
        selected.setTenantUID(selectedUserOrg.getTenantUID());
        selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
        selected.setHospitalUID(selectedUserOrg.getHospitalUID());
        selected.setSiteUID(selectedUserOrg.getSiteUID());
        selected.setCreateTime(new Date());
        selected.setCreatorId(user.getUid());
        selected.setCreatorName(user.getName());
        selected.setFileName(file.getFileName());
        selected.setStorageId(uploadedFileObject.getObjectStorageId());

        uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_PMORDER_REPORT_TEMPLATE);
        uploadedFileObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_DOCS);
        uploadedFileObject.setBoUuid(selected.getId());
        blobFileService.saveBlobObject(uploadedFileObject);
    }

    /**
     * Getter for tenant name.
     *
     * @param tenantUID tenant UID
     * @return name
     */
    public String getTenantName(String tenantUID) {
        return tenantInfoRepository.findByuid(tenantUID).getName();
    }

    /**
     * Getter for institution name.
     *
     * @param uid institution UID
     * @return institution name
     */
    public String getInsNameByUID(String uid) {
        return getOrgNameByUIDAndLevel(uid, 1);
    }

    /**
     * Getter for hospital name.
     *
     * @param uid hospital UID
     * @return hospital name
     */
    public String getHospitalNameByUID(String uid) {
        return getOrgNameByUIDAndLevel(uid, 2);
    }

    /**
     * Getter for site name.
     *
     * @param uid site UID
     * @return site name
     */
    public String getSiteNameByUID(String uid) {
        return getOrgNameByUIDAndLevel(uid, 3);
    }

    /**
     * Getter for org name.
     *
     * @param uid   org UID
     * @param level level
     * @return org name
     */
    private String getOrgNameByUIDAndLevel(String uid, int level) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (oi.getOrgLevel() == level) {
            return oi.getName();
        }
        return null;

    }

    /**
     * called when download template.
     *
     * @return {@link StreamedContent} object
     */
    public StreamedContent getActiveTemplate() {
        try {
            byte[] activeTemplateBytes = pmOrderReportTemplateService.getActiveTemplateBytes(isInsulate, user);
            return new DefaultStreamedContent((new ByteArrayInputStream(activeTemplateBytes)), "application/msword", "template.docx");
        } catch (IOException e) {
            WebUtil.addErrorMessage("Fail to download template");
            logger.warn("fail to get active template.", e);
        }
        return null;
    }

    /**
     * Called on create.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        clearContext();
        super.prepareCreate();
    }

    /**
     * Called at save template dialog.
     */
    @Transactional
    public void applyChange() {
        if (selectedUserOrg == null) {
            selectedUserOrg = orgInfoRepository.getByUid(user.getSiteUID());
        }
        if (file == null || uploadedFileObject == null) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("File has not been uploaded");
            return;
        }
        super.save();
        clearContext();
    }

    /**
     * Called at delete item.
     */
    @Override
    public void delete() {
        super.delete();
        this.clearContext();
    }

    /**
     * common method that clear context.
     */
    private void clearContext() {
        selectedUserOrg = null;
        file = null;
        uploadedFileObject = null;
    }

    /**
     * Called on upload data file.
     *
     * @param event {@link FileUploadEvent} object.
     */
    public void uploadDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        String filename = file.getFileName();
        if (!StringUtils.endsWithIgnoreCase(filename, ".docx")) {
            throw new IllegalStateException("Invalid file type: " + file.getFileName());
        }
        uploadedFileObject = blobFileService.uploadFile(file);
    }

    /**
     * Called while an item is selected.
     */
    public void onSelect() {
        if (selected != null && selected.getId() != null) {
            selected = dao.findById(selected.getId());
        }
    }

    /**
     * Getter for uploaded file.
     *
     * @return {@link UploadedFile} object.
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * Setter for uploaded file.
     *
     * @param file {@link UploadedFile} object.
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
