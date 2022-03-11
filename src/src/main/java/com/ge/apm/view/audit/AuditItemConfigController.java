package com.ge.apm.view.audit;

import com.ge.apm.dao.AuditConfigItemRepository;
import com.ge.apm.dao.AuditEntryItemRelRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AuditConfigItem;
import com.ge.apm.domain.AuditEntryItemRel;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.audit.AuditItemConfigService;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class AuditItemConfigController extends GenericCRUDUUIDController<AuditConfigItem> {

    private AuditConfigItemRepository dao;
    private AuditEntryItemRelRepository relDao;
    private I18nMessageRepository i18nMessageRepository;
    private List<I18nMessage> itemContentTypeList;
    private List<V2_BlobObject> attachements;

    private String searchContentType = "1";
    private String selectType = "1";

    private BlobFileService blobService;
    private AuditItemConfigService auditItemConfigService;

    @Override
    protected void init() {
        this.filterBySite = false;
        dao = WebUtil.getBean(AuditConfigItemRepository.class);
        relDao = WebUtil.getBean(AuditEntryItemRelRepository.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        auditItemConfigService = WebUtil.getBean(AuditItemConfigService.class);
        itemContentTypeList = i18nMessageRepository.getByMsgTypeOrderByMsgKey("auditItemType");

        String contentType = WebUtil.getRequestParameter("contentType");
        if(StringUtils.isNotBlank(contentType)){
            this.searchContentType = contentType;
        }

    }

    @Override
    protected GenericRepositoryUUID<AuditConfigItem> getDAO() {
        return dao;
    }

    @Override
    protected Page<AuditConfigItem> loadData(PageRequest pageRequest) {

        if(CollectionUtils.isEmpty(this.searchFilters)){
            this.searchFilters = new ArrayList<>();
            this.searchFilters.add(new SearchFilter("contentType", SearchFilter.Operator.EQ, this.searchContentType));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest page = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        List<SearchFilter> searchFilters = this.searchFilters;
        Page<AuditConfigItem> list = dao.findBySearchFilter(searchFilters, page);
        return list;
    }

    public void searchByType(String contentType) {
        this.searchFilters = new ArrayList<>();
        this.searchFilters.add(new SearchFilter("contentType", SearchFilter.Operator.EQ, contentType));
        this.searchContentType = contentType;
    }

    public void changeType(){
        selectType = "1".equals(selectType) ? "2" : "1";
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        attachements = null;
        selectType = "1";
        super.prepareCreate();
        this.selected.setContentType(Integer.parseInt(searchContentType));
    }

    public void setSelected(String uid) {
        attachements = null;
        AuditConfigItem auditConfigItem = dao.findById(uid);
        super.setSelected(auditConfigItem);
        selectType = this.selected.getType().toString();
    }

    public String saveAuditConfigItem(){

        if(1 ==this.selected.getType() && StringUtils.isEmpty(this.selected.getContent())){
            WebUtil.addErrorMessage("请填写内容！");
            return "";
        }
        if(2 ==this.selected.getType() && CollectionUtils.isEmpty(this.attachements)){
            WebUtil.addErrorMessage("请上传图片！");
            return "";
        }

        // 同类型下，mark唯一
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("mark", SearchFilter.Operator.EQ, this.selected.getMark()));
        searchFilterList.add(new SearchFilter("contentType", SearchFilter.Operator.EQ, this.selected.getContentType()));

        if(StringUtils.isNotEmpty(this.selected.getUid())){
            searchFilterList.add(new SearchFilter("uid", SearchFilter.Operator.NE, this.selected.getUid()));
        }

        List<AuditConfigItem> auditConfigItemList = dao.findBySearchFilter(searchFilterList);

        if(CollectionUtils.isNotEmpty(auditConfigItemList)){
            WebUtil.addErrorMessage("同类型下，编号重复，无法提交！");
            return "";
        }

        if(StringUtils.isBlank(this.selected.getUid())){
            this.selected.setCreator(UserContextService.getCurrentUserAccount().getName());
            this.selected.setCreateTime(new Date());
        }

        auditItemConfigService.saveAuditConfigItem(this.selected, attachements);
        this.selected=null;
        WebUtil.addSuccessMessage("保存成功！");
        return "auditItemConfigList?faces-redirect=true&contentType=" + this.searchContentType;
    }

    public void deleteItem(AuditConfigItem configItem){
        List<AuditEntryItemRel> list = relDao.findByItemUid(configItem.getUid());

        if(CollectionUtils.isNotEmpty(list)){
            WebUtil.addErrorMessage("内容被其他配置引用，无法删除！");
            return;
        }

        dao.delete(configItem);
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AUDIT_CONFIG_ITEM);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public List<V2_BlobObject> getAttachList(String itemUid, String type) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(itemUid);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
        return collect;
    }

    public List<I18nMessage> getItemContentTypeList() {
        return itemContentTypeList;
    }

    public void setItemContentTypeList(List<I18nMessage> itemContentTypeList) {
        this.itemContentTypeList = itemContentTypeList;
    }

    public String getSearchContentType() {
        return searchContentType;
    }

    public void setSearchContentType(String searchContentType) {
        this.searchContentType = searchContentType;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }
}
