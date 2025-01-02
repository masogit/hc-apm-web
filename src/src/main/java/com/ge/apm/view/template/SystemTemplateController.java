package com.ge.apm.view.template;

import com.ge.apm.dao.*;

import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.util.*;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class SystemTemplateController extends GenericCRUDUUIDController<SystemTemplate> {
    private static final Logger logger = LoggerFactory.getLogger(SystemTemplateController.class);
    private UserAccount user;
    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;
    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;
    private BlobFileService blobService;

    private SystemTemplateRepository systemTemplateRepository;

    private SystemTemplateValueRepository systemTemplateValueRepository;

//    @Getter@Setter
//    private OrgInfo selectSite;

    @Getter@Setter
    private OrgInfo    selectedOrg;
    @Getter@Setter
    private String businessType;

    @Getter@Setter
    private String tenantUid;
    @Getter@Setter
    private TenantInfo selectedTenant;

    @Getter
    @Setter
    private TreeNode orgTree;
    private  UaaService uaaService ;

    @Getter
    @Setter
    private List<OrgInfo> affiliationDepartmentGroup = new ArrayList<>();

    @Override
    protected SystemTemplateRepository getDAO() {
        return systemTemplateRepository;
    }

    @Override
    protected void init() {
        user = UserContextService.getCurrentUserAccount();
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        systemTemplateRepository = WebUtil.getBean(SystemTemplateRepository.class);
        systemTemplateValueRepository = WebUtil.getBean(SystemTemplateValueRepository.class);
//        selectSite = orgInfoRepository.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());
        uaaService = WebUtil.getBean(UaaService.class);
        selectedTenant = tenantInfoRepository.findByuid(user.getTenantUID());
        setITAdminOrgTree();
        selectedOrg = orgInfoRepository.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());

    }


    private void setITAdminOrgTree(){
        selectedOrg = null;
        selectedTenant = tenantInfoRepository.findByuid(user.getTenantUID());
//        String orgInfoUID = user.getOrgInfoUID();
//        affiliationDepartmentGroup= orgInfoRepository.getHospitalChildrenByUids(Arrays.asList(orgInfoUID));
//        orgTree=uaaService.getOrgTreeNodes(orgInfoUID,affiliationDepartmentGroup);

        tenantUid=user.getTenantUID();
        if (selectedTenant != null && selectedTenant.getId() != 46) {
            rebuildOrgTree();
            TreeNode selectedOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
            if (null != selectedOrgNode) {
                selectedOrgNode.setSelected(true);
                TreeNodeUtil.expandParentsNodes(selectedOrgNode);
            }
        } else {
            orgTree = null;
        }
    }

    @Override
    protected Page<SystemTemplate> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if(searchFilters == null){
            searchFilters = new ArrayList<>();
        }
        Page<SystemTemplate> page = systemTemplateRepository.findBySearchFilter(searchFilters, pageRequest);
        List<SystemTemplate> list = page.getContent();
        if(CollectionUtils.isNotEmpty(list)) {
            for (SystemTemplate systemTemplate : list) {
                SystemTemplateValue  value = systemTemplateValueRepository.findFirstByTemplateTypeAndSiteUid(systemTemplate.getTemplateType(), selectedOrg.getSiteUID());
                if(value == null){
                    value = systemTemplateValueRepository.findFirstByTemplateTypeAndSiteUid(systemTemplate.getTemplateType(),  selectedOrg.getHospitalUID());
                    if(value == null){
                        value = systemTemplateValueRepository.findFirstByTemplateTypeAndSiteUid(systemTemplate.getTemplateType(), selectedOrg.getInstitutionUID());
                        if(value == null){
                            value = systemTemplateValueRepository.findFirstByTemplateTypeAndSiteUid(systemTemplate.getTemplateType(), selectedOrg.getTenantUID());
                        }
                    }
                }

                if(value != null){
                    systemTemplate.setStorageId(value.getStorageId());
                    systemTemplate.setFileName(value.getFileName());
                }
            }
        }
        return page;
    }
    public void uploadDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        uploadedFileObject = blobService.uploadFile(file);
        logger.info("File is uploaded as " + uploadedFileObject.getObjectStorageId());
    }


    public String getTemplateName(String code){
        return SystemTemplate.SystemTemplateEnum.getName(code);
    }

    public String getBusinessName(String code){
        return SystemTemplate.SystemTemplateBusinessEnum.getName(code);
    }

    public List<SystemTemplate.SystemTemplateBusinessEnum> getBusinessTypes(){
       return Arrays.asList(SystemTemplate.SystemTemplateBusinessEnum.values());
    }

    public String getTenantName(String tenantUID) {
        return tenantInfoRepository.findByuid(tenantUID).getName();
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                OrgInfo site = (OrgInfo) event.getTreeNode().getData();
                if(StringUtils.isNotEmpty(site.getUid())){
                    this.selectedOrg = site;
                }
            } else {
                this.selectedOrg = orgInfoRepository.getByUid(user.getSiteUID());
            }
        }
    }


    public void   saveTemplate(){
        if(uploadedFileObject==null){
            return;
        }
        SystemTemplateValue   systemTemplateValue = systemTemplateValueRepository.findFirstByTemplateTypeAndSiteUid(this.selected.getTemplateType(), selectedOrg.getSiteUID());
        if(systemTemplateValue == null){
            systemTemplateValue = new SystemTemplateValue();
            systemTemplateValue.setTemplateType(this.selected.getTemplateType());
            systemTemplateValue.setTenantUid(selectedOrg.getTenantUID());
            systemTemplateValue.setInstitutionUid(selectedOrg.getInstitutionUID());
            systemTemplateValue.setHospitalUid(selectedOrg.getHospitalUID());
            systemTemplateValue.setSiteUid(selectedOrg.getSiteUID());
        }
        systemTemplateValue.setFileName(uploadedFileObject.getObjectName());
        systemTemplateValue.setStorageId(uploadedFileObject.getObjectStorageId());
        systemTemplateValue.setCreateBy(user.getUid());
        systemTemplateValue.setCreateTime(new Date());
        systemTemplateValueRepository.save(systemTemplateValue);
        uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_SYSTEM_TEMPLATE);
        uploadedFileObject.setBoUuid(systemTemplateValue.getId());
        blobService.saveBlobObject(uploadedFileObject);

        file=null;

    }


    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<TenantInfo> getTenantInfoList() {
        List<TenantInfo> tenantInfos;
        if (UserContextService.isSuperAdminWithPartnerAccount()) {
            tenantInfos = tenantInfoRepository.findAllByTestStatusIsNullOrTestStatusNotOrderByName(-1);
        } else {
            tenantInfos = tenantInfoRepository.getAllOrderByName();
        }

        return tenantInfos;

    }

    public void setTenantInfoValue(String tenantUid) {
        this.tenantUid = tenantUid;
        if (selectedTenant != null && selectedTenant.getUid().equals(tenantUid)) return;

        selectedOrg = null;
        selected = null;
        selectedTenant = tenantInfoRepository.findByuid(tenantUid);

        if (selectedTenant != null && selectedTenant.getId() != 46) {
            rebuildOrgTree();
            TreeNode selectedOrgNode = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
            if (null != selectedOrgNode) {
                selectedOrgNode.setSelected(true);
                TreeNodeUtil.expandParentsNodes(selectedOrgNode);
            }
        } else {
            orgTree = null;
        }
        selectedOrg= orgInfoRepository.getByUid(selectedTenant.getUid());

    }

    public void rebuildOrgTree() {
        //判断登陆用户是否是LocalITAdmin(本地后台运营管理)
        if (UserContextService.isOnlyLocalITAdmin()) {
            orgTree = getOrgTreeBySite(user.getSiteUID());
        } else {
            orgTree = getOrgTreeBySite(tenantUid);
        }
    }

    public TreeNode getOrgTreeBySite(String siteUID){
        List<OrgInfo> orgList = orgInfoRepository.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(siteUID).stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        return uaaService.getOrgTreeNodes(siteUID, orgList);
    }



}
