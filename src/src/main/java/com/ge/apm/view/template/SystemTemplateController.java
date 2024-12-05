package com.ge.apm.view.template;

import com.ge.apm.dao.*;

import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
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



@ManagedBean
@ViewScoped
public class SystemTemplateController extends GenericCRUDUUIDController<SystemTemplate> {
    private static final Logger logger = LoggerFactory.getLogger(SystemTemplateController.class);
    private UserAccount user;
    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;

    private OrgInfo selectedUserOrg;

    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;
    private BlobFileService blobService;

    private SystemTemplateRepository systemTemplateRepository;

    private SystemTemplateValueRepository systemTemplateValueRepository;



    @Override
    protected SystemTemplateRepository getDAO() {
        return systemTemplateRepository;
    }

    @Override
    protected void init() {
        user = UserContextService.getCurrentUserAccount();
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);

        selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
        blobService = WebUtil.getBean(BlobFileService.class);
        systemTemplateRepository = WebUtil.getBean(SystemTemplateRepository.class);
        systemTemplateValueRepository = WebUtil.getBean(SystemTemplateValueRepository.class);

    }


    @Override
    protected Page<SystemTemplate> loadData(PageRequest pageRequest) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<SearchFilter> searchFilters = this.searchFilters;

        if(searchFilters == null){
            searchFilters = new ArrayList<>();
        }
        Page<SystemTemplate> page = systemTemplateRepository.findBySearchFilter(searchFilters, pageRequest);
        List<SystemTemplate> list = page.getContent();
        if(CollectionUtils.isNotEmpty(list)) {
            for (SystemTemplate systemTemplate : list) {
                SystemTemplateValue  value = systemTemplateValueRepository.findFirstBySystemTemplateIdAndSiteUid(systemTemplate.getId(), user.getTenantUID());
                if(value!=null){
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



    public String getOrgName(String uid) {
        OrgInfo oi = orgInfoRepository.getByUid(uid);
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



    public void   saveTemplate(){
        if(uploadedFileObject==null){
            return;
        }
        SystemTemplateValue   systemTemplateValue = systemTemplateValueRepository.findFirstBySystemTemplateIdAndSiteUid(this.selected.getId(), user.getTenantUID());
        if(systemTemplateValue == null){
            systemTemplateValue = new SystemTemplateValue();
            systemTemplateValue.setSystemTemplateId(this.selected.getId());
        }
        systemTemplateValue.setTenantUid(user.getTenantUID());
        systemTemplateValue.setInstitutionUid(user.getTenantUID());
        systemTemplateValue.setHospitalUid(user.getTenantUID());
        systemTemplateValue.setSiteUid(user.getTenantUID());
        systemTemplateValue.setFileName(uploadedFileObject.getObjectName());
        systemTemplateValue.setStorageId(uploadedFileObject.getObjectStorageId());
        systemTemplateValue.setCreateBy(user.getUid());
        systemTemplateValue.setCreateTime(new Date());
        systemTemplateValueRepository.save(systemTemplateValue);
        uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_SYSTEM_TEMPLATE);
        uploadedFileObject.setBoUuid(systemTemplateValue.getId());
        blobService.saveBlobObject(uploadedFileObject);
    }


    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
