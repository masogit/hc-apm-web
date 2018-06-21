package com.ge.apm.view.wo;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.sysutil.UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
public class WorkOrderExportTemplateController extends JpaCRUDController<WorkflowConfig> {


    protected static final Logger logger = Logger.getLogger(WorkOrderExportTemplate.class);

    private static final long serialVersionUID = 1L;
    private WorkflowConfigRepository dao = null;
    private UserAccountRepository userDao;
    private UserAccount user;
    private UserAccount owner;
    private List<UserAccount> dispatchUserList = new ArrayList<UserAccount>();
    private List<UserInfo> userList;
    private List<V2_DeviceType> deviceTypeList;
    private List<OrgInfo> institutionList;
    private List<OrgInfo> hospitalList;
    private List<OrgInfo> siteList;
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

    @Override
    protected void init() {
        user = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(WorkflowConfigRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        isInsulate = UserContextService.isOnlyLocalITAdmin();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
        setTenantUid(user.getTenantUID());
        blobService = WebUtil.getBean(BlobFileService.class);
    }


    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        try {
            this.file = event.getFile();
            ExcelDocument.ExcelType type = ExcelDocument.parseTypeByFileName(file.getFileName());
            if (type == null) {
                throw new IllegalStateException("Invalid file type: " + file.getFileName());
            }
            doc = new ExcelDocument(file.getInputstream(), type);
            uploadedFileObject = blobService.uploadFile(file);
            logger.info("File is uploaded as " + uploadedFileObject.getObjectStorageId());
        } catch (IOException ex) {
            logger.error(ex);
            WebUtil.addErrorMessage("File can not be read!");
        } catch (Exception e) {
            logger.error(e);
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--" + e.getMessage());
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
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

    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }

    @Override
    protected WorkflowConfigRepository getDAO() {
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
    protected Page<WorkflowConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = userCommonService.getLoginUserFilters(isInsulate, loginUserOrgType);
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public void onOwnerChange() {
        if (null != owner) {
            selected.setDispatchUserId(owner.getId());
            selected.setDispatchUserName(owner.getName());
        }
    }

    public String getTypeName(Integer deviceType) {
        for (V2_DeviceType dt : deviceTypeList) {
            if (dt.getId().equals(deviceType)) {
                return dt.getName();
            }
        }
        return null;
    }

    @Override
    public List<WorkflowConfig> getItemList() {
        return dao.find();
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public List<UserAccount> getDispatchUserList() {
        return dispatchUserList;
    }

    public void setDispatchUserList(List<UserAccount> dispatchUserList) {
        this.dispatchUserList = dispatchUserList;
    }

    @Transactional
    @Override
    public void onBeforeSave(WorkflowConfig config) {
        selected.setTenantUID(selectedUserOrg.getTenantUID());
        selected.setSiteId(user.getSiteId());
        selected.setHospitalId(user.getHospitalId());
        selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
        selected.setHospitalUID(selectedUserOrg.getHospitalUID());
        selected.setSiteUID(selectedUserOrg.getSiteUID());
        int orgType = selectedUserOrg.getOrgType();
        if (orgType == 0) {
            selected.setAssetSeriaNumLevel(4);//租户级别
        } else if (orgType == 1) {
            selected.setAssetSeriaNumLevel(3);//集团级别
        } else if (orgType == 2) {
            selected.setAssetSeriaNumLevel(2);//医院级别
        } else if (orgType == 3) {
            selected.setAssetSeriaNumLevel(1);//科室级别
        }

        UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
        if (selected.getDispatchUserId() != null) {
            UserAccount dispatcher1 = uaDao.getById(config.getDispatchUserId());
            if (dispatcher1 != null) {
                selected.setDispatchUserName(dispatcher1.getName());
            }
        } else {
            selected.setDispatchUserName(null);
        }
        if (selected.getDispatchUserId2() != null) {
            UserAccount dispatcher2 = uaDao.getById(config.getDispatchUserId2());
            if (dispatcher2 != null) {
                selected.setDispatchUserName2(dispatcher2.getName());
            }
        } else {
            selected.setDispatchUserName2(null);
        }

        //pm
        if (selected.getPmDispatchUserId() != null) {
            UserAccount pmDispatcher1 = uaDao.getById(config.getPmDispatchUserId());
            if (pmDispatcher1 != null) {
                selected.setPmDispatchUserName(pmDispatcher1.getName());
            }
        } else {
            selected.setPmDispatchUserName(null);
        }
        if (selected.getPmDispatchUserId2() != null) {
            UserAccount pmDispatcher2 = uaDao.getById(config.getPmDispatchUserId2());
            if (pmDispatcher2 != null) {
                selected.setPmDispatchUserName2(pmDispatcher2.getName());
            }
        } else {
            selected.setPmDispatchUserName2(null);
        }

    }

    @Transactional
    public void applyChange() {
        if (selectedUserOrg == null) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("selectedOrg"));
            return;
        }
        Boolean isManual = selected.getIsManualEnabled();
        Boolean isTakeOrder = selected.getIsTakeOrderEnabled();
        Boolean isPmManual = selected.getIsPmManualEnabled();
        Boolean isPmTakeOrder = selected.getIsPmTakeOrderEnabled();

        if (isManual != null && isManual) {
            if (selected.getDispatchUserId() == null && selected.getDispatchUserId2() == null) {
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage(WebUtil.getMessage("WoNeedDispather"));
                return;
            }
        }
        if (isPmManual != null && isPmManual) {
            if (selected.getPmDispatchUserId() == null && selected.getPmDispatchUserId2() == null) {
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage(WebUtil.getMessage("PmNeedDispather"));
                return;
            }
        }


        if (isManual != null && isTakeOrder != null && !isManual && !isTakeOrder) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("WoManualAndGrapMsg"));
            return;
        }

        if (selected.getGenerateMethod() != null && selected.getGenerateMethod() == 1) {
            if (StringUtils.isEmpty(selected.getPrefix())) {
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage(WebUtil.getMessage("prefixIsNull"));
                return;
            } else {
                List<WorkflowConfig> prefixList = dao.findAllPrefixByTenantUID(user.getTenantUID());
                if (CollectionUtils.isNotEmpty(prefixList)) {
                    for (WorkflowConfig temp : prefixList) {
                        if ((!temp.equals(selected)) && temp.getPrefix().equals(selected.getPrefix())) {
                            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                            WebUtil.addErrorMessage(WebUtil.getMessage("prefixExistsAlready"));
                            return;
                        }
                    }
                }
            }

        }

        onBeforeSave(selected);

        dao.deleteBySiteUID(selected.getSiteUID());
        super.save();
        selectedUserOrg = null;
    }

    @Override
    public void delete() {
        super.delete();
        selectedUserOrg = null;
        dispatchUserList.clear();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        selectedUserOrg = orgInfoRepository.getByUid(selected.getSiteUID());
    }

    @Override
    public void prepareCreate() {
        selectedUserOrg = null;
        try {
            super.prepareCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSelect() {
        if (selected != null && selected.getId() != null) {
            selected = dao.findById(selected.getId());
        }
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public List<V2_DeviceType> getDeviceTypeList() {
        return deviceTypeList;
    }

    public void setDeviceTypeList(List<V2_DeviceType> deviceTypeList) {
        this.deviceTypeList = deviceTypeList;
    }

    public List<OrgInfo> getInstitutionList() {
        return institutionList;
    }

    public void setInstitutionList(List<OrgInfo> institutionList) {
        this.institutionList = institutionList;
    }

    public List<OrgInfo> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<OrgInfo> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public List<OrgInfo> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<OrgInfo> siteList) {
        this.siteList = siteList;
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