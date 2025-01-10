package com.ge.apm.view.template;


import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SystemTemplateRepository;
import com.ge.apm.dao.SystemTemplateValueRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.SystemTemplateMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean
@ViewScoped
public class SysConfigTemplateController extends GenericCRUDUUIDController<SystemTemplate> {
    private static final Logger logger = LoggerFactory.getLogger(SystemTemplateController.class);
    private UserAccount user;
    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;
    private UploadedFile file;
    private V2_BlobObject uploadedFileObject;
    private BlobFileService blobService;

    private SystemTemplateRepository systemTemplateRepository;

    private SystemTemplateValueRepository systemTemplateValueRepository;

    @Getter
    @Setter
    private OrgInfo selectedOrg;
    @Getter@Setter
    private String businessType;

    @Getter
    @Setter
    private TreeNode orgTree;
    private UaaService uaaService ;

    @Getter
    @Setter
    private SystemTemplateValue systemTemplateValue;

    private SystemTemplateMapper systemTemplateMapper;
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
        systemTemplateMapper = WebUtil.getBean(SystemTemplateMapper.class);
        uaaService = WebUtil.getBean(UaaService.class);

        selectedOrg = orgInfoRepository.getByUid(UserContextService.getCurrentUserAccount().getSiteUID());

    }





    @Override
    protected Page<SystemTemplate> loadData(PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(this.searchFilters)) {
            this.searchFilters = new ArrayList<>();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("page", pageRequest.getPageSize());
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        param.put("siteUid", selectedOrg.getSiteUID());
        param.put("hospitalUid", selectedOrg.getHospitalUID());
        param.put("institutionUID", selectedOrg.getInstitutionUID());
        param.put("tenantUid", selectedOrg.getTenantUID());
        covQueryCondition(this.searchFilters,param);

        List<SystemTemplate> list = systemTemplateMapper.searchSystemTemplate(param);

        for (SystemTemplate systemTemplate : list) {
            if(systemTemplate.getTemplateTypeNameValue()!=null){
                systemTemplate.setTemplateTypeName(systemTemplate.getTemplateTypeNameValue());
            }
            systemTemplate.setOnlyId(systemTemplate.getValueId()==null?systemTemplate.getId():systemTemplate.getValueId());
            if(selectedOrg.getSiteUID().equals(systemTemplate.getSiteUid())){
                systemTemplate.setEdit(true);
            }
        }
        Long count = systemTemplateMapper.searchSystemTemplateCount(param);
        return new PageImpl<>(list, pageRequest, count);
    }
    public void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        for (SearchFilter searchFilter : searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "businessType":
                    param.put("businessType", searchFilter.value);
                    break;
                case "templateType":
                    param.put("templateType", searchFilter.value);
                    break;
                default:
                    break;
            }
        }

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
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.systemTemplateValue = new SystemTemplateValue();
    }

    @Override
    public void prepareEdit()   {
        systemTemplateValue= systemTemplateValueRepository.findById(selected.getValueId());
    }


    public void addTemplate() {
        if (uploadedFileObject == null) {
            logger.info("systemTemplate saveTemplate uploadedFileObject is null ");
            WebUtil.addErrorMessage("上传文件为空！请上传文件");
            return;
        }
        try {
            systemTemplateValue.setTemplateType(this.selected.getTemplateType());
            systemTemplateValue.setTenantUid(selectedOrg.getTenantUID());
            systemTemplateValue.setInstitutionUid(selectedOrg.getInstitutionUID());
            systemTemplateValue.setHospitalUid(selectedOrg.getHospitalUID());
            systemTemplateValue.setSiteUid(selectedOrg.getSiteUID());
            systemTemplateValue.setFileName(uploadedFileObject.getObjectName());
            systemTemplateValue.setStorageId(uploadedFileObject.getObjectStorageId());
            systemTemplateValue.setCreateBy(user.getUid());
            systemTemplateValue.setCreateTime(new Date());
            systemTemplateValueRepository.save(systemTemplateValue);
            uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_SYSTEM_TEMPLATE);
            uploadedFileObject.setBoUuid(systemTemplateValue.getId());
            blobService.saveBlobObject(uploadedFileObject);

            WebUtil.addSuccessMessage("保存成功");
        } catch (Exception e) {
            logger.error("systemTemplate saveTemplate  error:", e);
            WebUtil.addErrorMessage("保存失败");
        }
        file = null;
        uploadedFileObject = null;

    }






    public void   editTemplate(){
        if(!selectedOrg.getSiteUID().equals(selected.getSiteUid())){
            WebUtil.addErrorMessage("模板不是组织机构下面的不能编辑");
            return;
        }
        try {
            if(uploadedFileObject!=null){
                systemTemplateValue.setStorageId(uploadedFileObject.getObjectStorageId());
                systemTemplateValue.setFileName(uploadedFileObject.getObjectName());

            }
            systemTemplateValueRepository.save(systemTemplateValue);
            if(uploadedFileObject!=null){
                uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_SYSTEM_TEMPLATE);
                uploadedFileObject.setBoUuid(systemTemplateValue.getId());
                blobService.saveBlobObject(uploadedFileObject);
            }
            WebUtil.addSuccessMessage("编辑成功");
        }catch (Exception e){
            logger.error("systemTemplate editTemplate  error:", e);
            WebUtil.addErrorMessage("编辑失败");
        }
        file=null;
        uploadedFileObject=null;
    }


    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void resetSystemTemplate(String valueId) {
        if(!selectedOrg.getSiteUID().equals(selected.getSiteUid())){
            WebUtil.addErrorMessage("模板不是组织机构下面的不能删除");
            return;
        }
        systemTemplateValueRepository.deleteById(valueId);
        WebUtil.addSuccessMessage("子系统模板已删除");
    }
}
