package com.ge.apm.view.wo;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.WorkOrderExportTemplateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.WorkOrderExportTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
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
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author 212688574
 */
@ManagedBean
@ViewScoped
public class WorkOrderExportTemplateController extends GenericCRUDUUIDController<WorkOrderExportTemplate> {

    protected static final Logger logger = Logger.getLogger(WorkOrderExportTemplate.class);

    private static final long serialVersionUID = 1L;
    private WorkOrderExportTemplateRepository dao = null;
    private UserAccount user;
    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;
    private Boolean isInsulate;
    private Integer loginUserOrgType;
    private UserCommonService userCommonService;
    private OrgInfo selectedUserOrg;
    private TenantInfo selectedTenant;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private String tenantUid;
    private UaaService uaaService;
    private OrgInfo selectedOrg;

    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;
    private BlobFileService blobService;
    private WorkOrderExportTemplateService workOrderExportTemplateService;

    @Override
    protected void init() {
        user = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(WorkOrderExportTemplateRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        isInsulate = UserContextService.isOnlyLocalITAdmin();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
        setTenantUid(user.getTenantUID());
        blobService = WebUtil.getBean(BlobFileService.class);
        workOrderExportTemplateService = WebUtil.getBean(WorkOrderExportTemplateService.class);
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

    public String getOrgFullName(OrgInfo org) {
        if (null == org || org.getId() == -1) {
            return null;
        }
        StringBuilder name = new StringBuilder(org.getName());
        while (org.getParentOrg() != null) {
            org = org.getParentOrg();
            name.insert(0, org.getName() + "/");
        }

        return name.toString();
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedUserOrg = (OrgInfo) node.getData();
        selected.setSiteUID(selectedUserOrg.getSiteUID());
    }

    public StreamedContent getActiveTemplate() {
        try {
            byte[] activeTemplateBytes = workOrderExportTemplateService.getActiveTemplateBytes(user.getSiteUID());
            return new DefaultStreamedContent((new ByteArrayInputStream(activeTemplateBytes)), "application/msword", "template.docx");
        } catch (IOException e) {
            WebUtil.addErrorMessage("Fail to download work order template");
        }
        return null;
    }

    public boolean existsSiteTemplate(){
        return workOrderExportTemplateService.existsSiteTemplate(user.getSiteUID());
    }

    public void setTenantUid(String tenantUid) {
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
        if (UserContextService.isOnlyLocalITAdmin()) {
            selectedTenant = tempTenantInfo;
            orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
        } else {
            selectedTenant = tenantInfoRepository.findByuid(tenantUid);
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setId(-1);
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
            orgTree = new DefaultTreeNode("root", null, null);
            TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
            rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenantWithoutClinical(tenantUid).getChildren());
        }

        TreeNode selectedUserOrg1 = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg1) {
            selectedUserOrg1.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg1);
        }
    }

    private boolean isNullOrBelongs(String uid, Set<String> uids) {
        return uid == null || uids.contains(uid);
    }

    public List<OrgInfo> getIncludeOrgInfo(String uid, String parentUID, Integer orgType) {
        return userCommonService.getIncludeOrgInfo(isInsulate, loginUserOrgType, uid, parentUID, orgType);
    }

    @Override
    protected WorkOrderExportTemplateRepository getDAO() {
        return dao;
    }

    public String getOrgNameById(Integer orgId) {
        OrgInfo oi = orgInfoRepository.findById(orgId);
        if (oi == null) {
            return null;
        }
        return oi.getName();
    }

    public String getInsNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (oi.getOrgLevel() == 1) {
            return oi.getName();
        }
        return null;
    }

    public String getHospitalNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (oi.getOrgLevel() == 2) {
            return oi.getName();
        }
        return null;
    }

    public String getSiteNameByUId(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (oi.getOrgLevel() == 3) {
            return oi.getName();
        }
        return null;
    }

    public String getTenantName(String tenantUID) {
        return tenantInfoRepository.findByuid(tenantUID).getName();
    }

    @Override
    protected Page<WorkOrderExportTemplate> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = userCommonService.getLoginUserFilters(isInsulate, loginUserOrgType);
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public List<WorkOrderExportTemplate> getItemList() {
        return dao.find();
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    @Transactional
    @Override
    public void onBeforeSave(WorkOrderExportTemplate config) {
        selected.setTenantUID(selectedUserOrg.getTenantUID());
        selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
        selected.setHospitalUID(selectedUserOrg.getHospitalUID());
        selected.setSiteUID(selectedUserOrg.getSiteUID());
        selected.setCreateTime(new Date());
        selected.setCreatorId(user.getUid());
        selected.setCreatorName(user.getName());
        selected.setFileName(file.getFileName());
        selected.setStorageId(uploadedFileObject.getObjectStorageId());

        uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_WORKORDER_EXPORT_TEMPLATE);
        uploadedFileObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_DOCS);
        uploadedFileObject.setBoUuid(selected.getId());
        blobService.saveBlobObject(uploadedFileObject);
    }

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
        selectedUserOrg = null;
        file = null;
        uploadedFileObject = null;
    }

    @Override
    public void delete() {
        super.delete();
        selectedUserOrg = null;
        file = null;
        uploadedFileObject = null;
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        selectedUserOrg = orgInfoRepository.getByUid(selected.getSiteUID());
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

    public String getTenantUid() {
        return tenantUid;
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