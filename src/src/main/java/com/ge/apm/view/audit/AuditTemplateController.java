package com.ge.apm.view.audit;

import com.ge.apm.dao.AuditEntryTemplateRelRepository;
import com.ge.apm.dao.AuditTemplateRepository;
import com.ge.apm.domain.AuditEntryTemplateRel;
import com.ge.apm.domain.AuditTemplate;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditTemplateService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class AuditTemplateController extends GenericCRUDUUIDController<AuditTemplate> {


    private AuditTemplateRepository dao;
    private AuditEntryTemplateRelRepository relDao;
    private V2_BlobObject fileObj;

    private BlobFileService blobService;
    private AuditTemplateService auditTemplateService;

    @Override
    protected void init() {
        this.filterBySite = false;
        dao = WebUtil.getBean(AuditTemplateRepository.class);
        relDao = WebUtil.getBean(AuditEntryTemplateRelRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        auditTemplateService = WebUtil.getBean(AuditTemplateService.class);
    }

    @Override
    protected GenericRepositoryUUID<AuditTemplate> getDAO() {
        return dao;
    }

    @Override
    protected Page<AuditTemplate> loadData(PageRequest pageRequest) {
        Sort sort = new Sort(Sort.Direction.DESC, "uploadTime");
        PageRequest page = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        if(CollectionUtils.isEmpty(this.searchFilters)){
            this.searchFilters = new ArrayList<>();
            // 支持下系统模板，不查询用户上传的模板
            removeFilterOnField("type");
            this.searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, "1"));
        }

        List<SearchFilter> searchFilters = this.searchFilters;
        Page<AuditTemplate> list = dao.findBySearchFilter(searchFilters, page);
        return list;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        fileObj = null;
        super.prepareCreate();
    }

    public void setSelected(String uid) {
        fileObj = null;
        AuditTemplate auditTemplate = dao.findById(uid);
        super.setSelected(auditTemplate);
    }

    public String saveAuditTemplate(){

        if(fileObj == null){
            WebUtil.addErrorMessage("请上传模板文件！");
            return "";
        }

        // 编号唯一性校验
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("mark", SearchFilter.Operator.EQ, this.selected.getMark()));

        if(StringUtils.isNotEmpty(this.selected.getUid())){
            searchFilterList.add(new SearchFilter("uid", SearchFilter.Operator.NE, this.selected.getUid()));
        }

        List<AuditTemplate> auditTemplateList = dao.findBySearchFilter(searchFilterList);

        if(CollectionUtils.isNotEmpty(auditTemplateList)){
            WebUtil.addErrorMessage("编号重复，无法提交！");
            return "";
        }

        if(StringUtils.isBlank(this.selected.getUid())){
            this.selected.setUploader(UserContextService.getCurrentUserAccount().getName());
            this.selected.setUploadTime(new Date());
            this.selected.setType(1);
        }
        auditTemplateService.saveAuditTemplate(this.selected, this.fileObj);
        this.selected=null;
        WebUtil.addSuccessMessage("保存成功！");
        return "auditTemplateList?faces-redirect=true";
    }

    public void deleteItem(AuditTemplate auditTemplate){
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("templateUid", SearchFilter.Operator.EQ, auditTemplate.getUid()));

        List<AuditEntryTemplateRel> list = relDao.findBySearchFilter(searchFilterList);

        if(CollectionUtils.isNotEmpty(list)){
            WebUtil.addErrorMessage("该模板被其他配置引用，无法删除！");
            return;
        }

        dao.delete(auditTemplate);
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AUDIT_TEMPLATE);
            blobObject.setBoSubType(V2_BlobObject.BO_TYPE_AUDIT_TEMPLATE);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            fileObj = blobObject;

            this.selected.setFileName(fileObj.getObjectName());
            this.selected.setFileSize(fileObj.getObjectSize());
            this.selected.setObjectUId(fileObj.getId());
            this.selected.setObjectStorageId(fileObj.getObjectStorageId());
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        this.selected.setFileName(null);
        this.selected.setFileSize(null);
        this.selected.setObjectUId(null);
    }

    /*public String fileSizeTransfer(Long fileSize){
        if(fileSize / 1024 > 1024){
            return (fileSize.intValue() / 1024 / 1024) + " MB";
        }
        return (fileSize.intValue() / 1024) + " KB";
    }*/

    public V2_BlobObject getFileObj() {
        return fileObj;
    }

    public void setFileObj(V2_BlobObject fileObj) {
        this.fileObj = fileObj;
    }
}
