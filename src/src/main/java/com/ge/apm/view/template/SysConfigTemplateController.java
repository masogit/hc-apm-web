package com.ge.apm.view.template;


import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SystemTemplateRepository;
import com.ge.apm.dao.SystemTemplateValueRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.SystemTemplateMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

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

    private SystemTemplateService systemTemplateService;
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
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);

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
        return SystemTemplateEnum.getName(code);
    }

    public String getBusinessName(String code){
        return SystemTemplateEnum.SystemTemplateBusinessEnum.getName(code);
    }

    public List<SystemTemplateEnum.SystemTemplateBusinessEnum> getBusinessTypes(){
        return Arrays.asList(SystemTemplateEnum.SystemTemplateBusinessEnum.values());
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


    @Transactional
    public void addTemplate() {
        try {
            saveSubTemplate();
        } catch (Exception e) {
            logger.error("systemTemplate saveTemplate  error:", e);
            WebUtil.addErrorMessage("保存失败");
        }
        file = null;
        uploadedFileObject = null;
    }

    private void saveSubTemplate() {

        if (uploadedFileObject == null) {
            logger.info("systemTemplate saveTemplate uploadedFileObject is null ");
            WebUtil.addErrorMessage("上传文件为空！请上传文件");
            return;
        }
        handlerSubSystemTemplate();
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
//        handlerSubOrgInfoSystemTemplate();
        WebUtil.addSuccessMessage("保存成功");
    }


    //处理当前组织添加的子模版 需要查看当前组织下的子组织是否自己添加过子模版 如果添加过需要吧这个模板添加到子组织的模板里
    private void handlerSubOrgInfoSystemTemplate() {
        if(selectedOrg.getOrgLevel()>2){
            return;
        }
        List<SystemTemplateValue> systemTemplateValues = systemTemplateService.getOrgInfoSubSystemTemplate(selectedOrg.getTenantUID(), selected.getTemplateType());
        List<SystemTemplateValue> collect = systemTemplateValues.stream().filter(item -> !item.getSiteUid().equals(selectedOrg.getTenantUID())).collect(Collectors.toList());
        if(Objects.equals(selectedOrg.getOrgLevel(), 2)){
            collect = systemTemplateValues.stream().filter(item -> item.getHospitalUid().equals(selectedOrg.getHospitalUID()) &&  !item.getSiteUid().equals(item.getHospitalUid() ) ).collect(Collectors.toList());
        }
        if(Objects.equals(selectedOrg.getOrgLevel(), 1)){
            collect = systemTemplateValues.stream().filter(item -> item.getInstitutionUid().equals(selectedOrg.getInstitutionUID()) &&  !item.getSiteUid().equals(item.getInstitutionUid() ) ).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(collect)) {
            List<SystemTemplateValue> distinctSystemTemplateValue = collect.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    SystemTemplateValue::getSiteUid,
                                    SystemTemplateValue -> SystemTemplateValue,
                                    (existing, replacement) -> existing
                            ),
                            map -> new ArrayList<>(map.values())
                    ));
            List<SystemTemplateValue> templateValues = new ArrayList<>();
            for (SystemTemplateValue templateValue : distinctSystemTemplateValue) {
                SystemTemplateValue systemTemplateValue1 = new SystemTemplateValue();
                systemTemplateValue1.setSiteUid(templateValue.getSiteUid());
                systemTemplateValue1.setHospitalUid(templateValue.getHospitalUid());
                systemTemplateValue1.setInstitutionUid(templateValue.getInstitutionUid());
                systemTemplateValue1.setTenantUid(templateValue.getTenantUid());
                systemTemplateValue1.setTemplateType(templateValue.getTemplateType());
                systemTemplateValue1.setTemplateTypeName(systemTemplateValue.getTemplateTypeName());
                systemTemplateValue1.setStorageId(systemTemplateValue.getStorageId());
                systemTemplateValue1.setFileName(systemTemplateValue.getFileName());
                systemTemplateValue1.setCreateBy(systemTemplateValue.getCreateBy());
                systemTemplateValue1.setCreateTime(new Date());
                templateValues.add(systemTemplateValue1);
            }
            systemTemplateValueRepository.save(templateValues);
        }
    }


    //处理当前组织如果是第一次添加子模板需要把他上级组织的模板添加到他的组织下面
    public void handlerSubSystemTemplate(){
        if(!SystemTemplateEnum.ARRIVAL_ACCEPTANCE_REPORT.getCode().equals(selected.getTemplateType())){
            return;
        }
        List<SystemTemplate> byKeyAndSiteForUseParent = systemTemplateService.findByKeyAndSiteForUseParent(selected.getTemplateType(), selectedOrg.getSiteUID(),selectedOrg.getHospitalUID(),selectedOrg.getInstitutionUID(),selectedOrg.getTenantUID());
        List<SystemTemplate> filterSite = byKeyAndSiteForUseParent.stream().filter(item -> !selectedOrg.getSiteUID().equals(item.getSiteUid())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(filterSite)){
            List<SystemTemplateValue> systemTemplateValues = new ArrayList<>();
            for (SystemTemplate systemTemplate : filterSite) {
                SystemTemplateValue systemTemplateValue1 = new SystemTemplateValue();
                systemTemplateValue1.setTemplateTypeName(systemTemplate.getTemplateTypeNameValue()!=null?systemTemplate.getTemplateTypeNameValue():systemTemplate.getTemplateTypeName());
                systemTemplateValue1.setTemplateType(systemTemplate.getTemplateType());
                systemTemplateValue1.setCreateTime(new Date());
                systemTemplateValue1.setCreateBy(user.getName());
                systemTemplateValue1.setFileName(systemTemplate.getFileNameValue()!=null?systemTemplate.getFileNameValue():systemTemplate.getFileName());
                systemTemplateValue1.setStorageId(systemTemplate.getValueStorageId()!=null?systemTemplate.getValueStorageId():systemTemplate.getStorageId());
                systemTemplateValue1.setTenantUid(selectedOrg.getTenantUID());
                systemTemplateValue1.setInstitutionUid(selectedOrg.getInstitutionUID());
                systemTemplateValue1.setHospitalUid(selectedOrg.getHospitalUID());
                systemTemplateValue1.setSiteUid(selectedOrg.getSiteUID());
                systemTemplateValues.add(systemTemplateValue1);
            }
            systemTemplateValueRepository.save(systemTemplateValues);
        }
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
