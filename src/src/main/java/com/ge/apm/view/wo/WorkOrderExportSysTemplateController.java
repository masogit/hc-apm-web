package com.ge.apm.view.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.systemTempate.SystemTemplateService;

import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 212688574
 */
@ManagedBean
@ViewScoped
public class WorkOrderExportSysTemplateController extends GenericCRUDUUIDController<SystemTemplateValue> {

    protected static final Logger logger = Logger.getLogger(WorkOrderExportSysTemplateController.class);

    private static final long serialVersionUID = 1L;
    private SystemTemplateValueRepository dao = null;
    private UserAccount user;
    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;
    private Boolean isInsulate;
    private OrgInfo selectedUserOrg;
    private TenantInfo selectedTenant;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;
    private BlobFileService blobService;

    private SystemTemplateService systemTemplateService;

    @Override
    protected void init() {
        user = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(SystemTemplateValueRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);

        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
        selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
        blobService = WebUtil.getBean(BlobFileService.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
    }


    @Override
    protected Page<SystemTemplateValue> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, user.getSiteUID()));
        searchFilters.add(new SearchFilter("templateType", SearchFilter.Operator.EQ, SystemTemplateEnum.WORK_ORDER_REPORT.getCode()));
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }


    public void uploadDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        String fileName = file.getFileName();
        if (!StringUtils.endsWithIgnoreCase(fileName, ".docx")) {
            throw new IllegalStateException("Invalid file type: " + file.getFileName());
        }
        uploadedFileObject = blobService.uploadFile(file);
        logger.info("File is uploaded as " + uploadedFileObject.getObjectStorageId());
    }

    public StreamedContent getActiveTemplate() {
        try {
            byte[] activeTemplateBytes = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.WORK_ORDER_REPORT, user);
            return new DefaultStreamedContent((new ByteArrayInputStream(activeTemplateBytes)), "application/msword", "template.docx");
        } catch (IOException e) {
            WebUtil.addErrorMessage("Fail to download work order template");
        }
        return null;
    }

    public boolean existsSiteTemplate() {
      return   systemTemplateService.existsSiteTemplate(SystemTemplateEnum.WORK_ORDER_REPORT.getCode(),user.getSiteUID());
    }


    @Override
    protected SystemTemplateValueRepository getDAO() {
        return dao;
    }


    public String getInsNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 1)) {
            return oi.getName();
        }
        return null;
    }

    public String getHospitalNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 2)) {
            return oi.getName();
        }
        return null;
    }

    public String getSiteNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 3)) {
            return oi.getName();
        }
        return null;
    }

    public String getTenantName(String tenantUID) {
        return tenantInfoRepository.findByuid(tenantUID).getName();
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }


    @Transactional
    public void applyChange() {
        if (selectedUserOrg == null) {
            selectedUserOrg = orgInfoRepository.getByUid(user.getSiteUID());
        }
        if (file == null || uploadedFileObject == null) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("文件未上传");
            return;
        }
        onBeforeTemplateSave();
        WebUtil.addSuccessMessage("维修工单模板保存成功");
        selectedUserOrg = null;
        file = null;
        uploadedFileObject = null;
    }


    public void onBeforeTemplateSave( ) {
        selected.setTenantUid(selectedUserOrg.getTenantUID());
        selected.setInstitutionUid(selectedUserOrg.getInstitutionUID());
        selected.setHospitalUid(selectedUserOrg.getHospitalUID());
        selected.setSiteUid(selectedUserOrg.getSiteUID());
        selected.setCreateTime(new Date());
        selected.setCreateBy(user.getUid());
        selected.setFileName(file.getFileName());
        selected.setStorageId(uploadedFileObject.getObjectStorageId());
        selected.setTemplateType(SystemTemplateEnum.WORK_ORDER_REPORT.getCode());
        dao.save(selected);
        uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_SYSTEM_TEMPLATE);
        uploadedFileObject.setBoSubType(SystemTemplateEnum.WORK_ORDER_REPORT.getCode());
        uploadedFileObject.setBoUuid(selected.getId());
        blobService.saveBlobObject(uploadedFileObject);
    }

    @Override
    public void delete() {
        dao.delete(selected);
        WebUtil.addSuccessMessage("维修工单模板已删除");
        selectedUserOrg = null;
        file = null;
        uploadedFileObject = null;
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        selectedUserOrg = orgInfoRepository.getByUid(selected.getSiteUid());
    }

    @Override
    public void prepareCreate() throws IllegalAccessException, InstantiationException {
        selectedUserOrg = null;
        file = null;
        uploadedFileObject = null;
        super.prepareCreate();
    }

    public void onSelect() {
        if (selected != null && selected.getId() != null) {
            selected = dao.findById(selected.getId());
        }
    }

    public OrgInfo getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void setSelectedUserOrg(OrgInfo selectedUserOrg) {
        this.selectedUserOrg = selectedUserOrg;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}