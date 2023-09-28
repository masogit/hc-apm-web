package com.ge.apm.view.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.ZkbFileDirectory;
import com.ge.apm.service.asset.ZkbFileAttachmentService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author 212605082 on 2019/12/2.
 */
@ManagedBean
@ViewScoped
public class ZkbFileAttachmentController extends GenericCRUDUUIDController<V2_BlobObject>{

    private BlobObjectRepository dao;
    private TenantInfoRepository tenantInfoDao;
    private ZkbFileAttachmentService zkbFileAttachmentService;
    private List<ZkbFileDirectory> subZkbFileDirectoryList=null;

    /**
     * 目录关系service
     */
    private ZkbFileDirectoryService zkbFileDirectoryService;
    private List<V2_BlobObject> attachements;
    private String actionName;
    private UserAccount currentUser;
    //复选的附件数据
    private Map<String, Boolean> selectedMap;
    private List<V2_BlobObject> currentPageBlobObject;

    private List<TenantInfo> tenantInfoList;
    private List<TenantInfo> selectedTenantInfoList;

    private Map<String, List<String>> zkbSecondDirectoryData;

    private List<Map<String, Object>> errorDirectoryList;
    private String vlink;
    private String vlinkTitle;
    /**
     * 主目录
     */
    private String mainDirectory;

    /**
     * 导入文件
     */
    private UploadedFile file;

    @Override
    protected void init() {

        dao = WebUtil.getBean(BlobObjectRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        zkbFileAttachmentService = WebUtil.getBean(ZkbFileAttachmentService.class);
        zkbFileDirectoryService = WebUtil.getBean(ZkbFileDirectoryService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.selectedMap = new HashMap();
        this.attachements = new ArrayList<>();
        selectedTenantInfoList = new ArrayList<>();

        tenantInfoList = zkbFileAttachmentService.getSourceTenantInfoList();
        zkbSecondDirectoryData = zkbFileAttachmentService.initZkbSecondDirectory();
    }

    @Override
    protected BlobObjectRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_BlobObject> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("boType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB));
        Page<V2_BlobObject> blobObjectPage = dao.findBySearchFilter(searchFilters, pageRequest);

        currentPageBlobObject = blobObjectPage.getContent();

        return blobObjectPage;

    }

    public void onSelectChange(ValueChangeEvent event) {
        this.selected.setMainDirectory(null);
        this.selected.setSecondDirectory(null);
        if(event.getNewValue()!=null &&
                ("OperationVideo".equals(event.getNewValue().toString()) || "OperationManual".equals(event.getNewValue().toString()))){
            this.selected.setMainDirectory("ALL");
            this.selected.setSecondDirectory("ALL");
        }
    }
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";

        selectedTenantInfoList = new ArrayList<>();
        attachements = new ArrayList<>();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        selectedTenantInfoList = new ArrayList<>();

        String blobObjectID = null;
        for (Map.Entry<String, Boolean> tempData : selectedMap.entrySet()) {
            if(tempData.getValue()){
                blobObjectID = tempData.getKey();
            }
        }
        if(blobObjectID != null){
            this.selected = dao.findById(blobObjectID);
            this.setVlink(this.selected.getExtLink());
            this.setVlinkTitle(this.selected.getObjectName());
            this.selectedTenantInfoList = zkbFileAttachmentService.getTargetTenantInfo(this.selected);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        V2_BlobObject blobObject = zkbFileAttachmentService.fileUpload(file);

        if (null != blobObject) {
            attachements.add(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file" + file.getFileName());
        }
    }

    public void removeAttachmentById(String blobId) {
        zkbFileAttachmentService.removeUploadFile(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageBlobObject.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_BlobObject item : currentPageBlobObject) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void saveZkbFileAttachment(){
        if ("OperationVideo".equals(selected.getBoSubType())) {
            if (org.apache.commons.lang.StringUtils.isEmpty(vlinkTitle)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("name") + WebUtil.getMessage("ValidationRequire"));
                return;
            } else if (org.apache.commons.lang.StringUtils.isEmpty(vlink)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("link") + WebUtil.getMessage("ValidationRequire"));
                return;
            }
        }
        if(this.actionName.equals("Create")){
            if(attachements.isEmpty() && !"OperationVideo".equals(this.selected.getBoSubType())){
                WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
                return;
            }
            zkbFileAttachmentService.saveZkbFileAttachment(this.selected, this.attachements, this.selectedTenantInfoList,vlink,this.vlinkTitle);
        }else if(this.actionName.equals("Edit")){
            zkbFileAttachmentService.updateZkbFileAttachment(this.selected, this.selectedTenantInfoList,vlink,this.vlinkTitle);
        }
        this.cancel();
    }

    public void deleteZkbFileAttachment() {

        selectedMap.forEach((key, value) -> {
            if (value) {
                zkbFileAttachmentService.deleteZkbFileAttachment(key);
            }
        });

        this.selectedMap = new HashMap();
        this.cancel();

    }

    public List<String> getZkbSecondDirectoryList(){
        if(this.selected != null){
            return this.zkbSecondDirectoryData.get(this.selected.getMainDirectory());
        }
        return new ArrayList<>();
    }

    public void cancel() {
        this.selected = null;
        this.vlink = null;
        this.vlinkTitle = null;
        this.selectedTenantInfoList = new ArrayList<>();
        this.attachements = new ArrayList<>();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public boolean isCheckedOne() {
        long count = selectedMap.values().stream().filter(item -> Boolean.TRUE.equals(item)).count();
        if(count == 1){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public boolean isZkbAdmin(){
        return zkbFileAttachmentService.isZkbAdmin(currentUser);
    }

    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<V2_BlobObject> attachements) {
        this.attachements = attachements;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public List<TenantInfo> getSelectedTenantInfoList() {
        return selectedTenantInfoList;
    }

    public void setSelectedTenantInfoList(List<TenantInfo> selectedTenantInfoList) {
        this.selectedTenantInfoList = selectedTenantInfoList;
    }

    /**
     * 获取所有主目录信息
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory() {
        return zkbFileDirectoryService.findMainDirectory(null);
    }


    /**
     * 根据主目录值获取子目录信息
     * @return
     */
    public List<ZkbFileDirectory> getSubZkbFileDirectoryList() {
        if(this.selected==null||this.selected.getMainDirectory()==null)
        {
            return null;
        }
        List<ZkbFileDirectory> mainZkbFileDirectory = this.zkbFileDirectoryService.findByLevelAndName(
                ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE,
                this.selected.getMainDirectory());
        if(CollectionUtils.isEmpty(mainZkbFileDirectory))
        {
            return null;
        }
        ZkbFileDirectory zkbFileDirectory = new ZkbFileDirectory();
        zkbFileDirectory=mainZkbFileDirectory.get(0);
        subZkbFileDirectoryList=this.zkbFileDirectoryService.findByZkbFileDirectory(zkbFileDirectory);
        return subZkbFileDirectoryList;
    }

    /**
     * 根据主目录值获取子目录信息
     * @return
     */
    public List<ZkbFileDirectory> getSubZkbFileDirectoryListByMainDirectory() {
        if(StringUtils.isBlank(this.mainDirectory))
        {
            return null;
        }
        List<ZkbFileDirectory> mainZkbFileDirectory = this.zkbFileDirectoryService.findByLevelAndName(
                ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE,
                this.getMainDirectory());
        if(CollectionUtils.isEmpty(mainZkbFileDirectory))
        {
            return null;
        }
        ZkbFileDirectory zkbFileDirectory = new ZkbFileDirectory();
        zkbFileDirectory=mainZkbFileDirectory.get(0);
        return this.zkbFileDirectoryService.findByZkbFileDirectory(zkbFileDirectory);
    }

    public void setSubZkbFileDirectoryList(List<ZkbFileDirectory> subZkbFileDirectoryList) {

        this.subZkbFileDirectoryList = subZkbFileDirectoryList;
    }

    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "DirectoryInfoImport?faces-redirect=true";
    }

    /**
     * 文件下载
     * @return
     */
    public String getTemplateName(){
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        return ucs.isLocale() == true ? "Directory_Service_Template.xlsx":"Directory_Service_Template_en.xlsx";
    }

    /**
     * 文件上传
     * @param event  文件流
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbFileAttachmentService.EXCEL_DIRECTORY_HEADER_ROW + ZkbFileAttachmentService.EXCEL_DIRECTORY_IMPORT_MAX_ROW;
            if(doc.exceedMaxRowNum(ZkbFileAttachmentService.SHEET_SERVICE_DIRECTORY, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            List<Map<String, Object>> importDirectoryMapList = zkbFileAttachmentService.getDirectoryMapFromTemplate(doc);
            zkbFileAttachmentService.importData(importDirectoryMapList);
            errorDirectoryList = zkbFileAttachmentService.filterErrorRecords(importDirectoryMapList);
            if(!errorDirectoryList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorDirectoryList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }

        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }

    }

    public List<Map<String, Object>> getErrorDirectoryList() {
        return errorDirectoryList;
    }

    public void setErrorDirectoryList(List<Map<String, Object>> errorDirectoryList) {
        this.errorDirectoryList = errorDirectoryList;
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    public String getVlink() {
        return vlink;
    }

    public void setVlink(String vlink) {
        this.vlink = vlink;
    }

    public String getVlinkTitle() {
        return vlinkTitle;
    }

    public void setVlinkTitle(String vlinkTitle) {
        this.vlinkTitle = vlinkTitle;
    }
}
