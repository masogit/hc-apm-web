package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbEngineerExportService;
import com.ge.apm.service.zkb.ZkbEngineerService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author 212605082 on 2019/12/18.
 */
@ManagedBean
@ViewScoped
public class ZkbEngineerController extends GenericCRUDUUIDController<ZkbEngineer> {

    private ZkbEngineerRepository dao;
    private ZkbEngineerService zkbEngineerService;
    private ZkbEngineerExportService zkbEngineerExportService;
    private UserContextService userContextService;

    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String selectionTenantInfo;
    private String actionName;
    private StreamedContent exportFile;
    private Page<ZkbEngineer> zkbEngineerPage;

    private List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList;

    private ZkbEngineerTrackRecord zkbEngineerTrackRecordSelected;

    /**
     * 获取附件处理service
     */
    private BlobFileService blobService;

    /**
     * 附件信息
     */
    private List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();

    /**
     * 需要移除的附件
     */
    private List<V2_BlobObject> removev2_blobObjectList = new ArrayList<>();

    private static final String ssoStartWith = "ZKB";

    @Getter
    @Setter
    private UploadedFile file;
    @Getter
    @Setter
    private List<ZkbEngineer> importFailedDataList;

    @Getter
    @Setter
    private List<ZkbTrainAttendanceRecord> zkbTrainAttendanceRecords;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbEngineerRepository.class);
        zkbEngineerService = WebUtil.getBean(ZkbEngineerService.class);
        zkbEngineerExportService = WebUtil.getBean(ZkbEngineerExportService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);

        currentUser = UserContextService.getCurrentUserAccount();
        tenantInfoList = zkbEngineerService.getSourceTenantInfoList();

        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbEngineer> getDAO() {
        return dao;
    }

    @Override
    protected Page<ZkbEngineer> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }

        zkbEngineerPage = dao.findBySearchFilter(searchFilters, pageRequest);

        return zkbEngineerPage;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        this.selected.setTenantUID(this.selectionTenantInfo);
        this.selected.setCreatedBy(this.currentUser.getLoginName());
        this.selected.setCreatedDate(new Date());
        this.zkbEngineerService.fullSso(this.selected,ssoStartWith);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        this.selected.setLastModifiedBy(this.currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());

        zkbEngineerTrackRecordList = zkbEngineerService.getZkbEngineerTrackRecordList(this.selected.getId());
        zkbTrainAttendanceRecords = zkbEngineerService.getZkbTrainAttendanceRecordList(this.selected.getId());

        this.initBlobObjectList(this.selected.getId());
    }

    @Override
    public void save() {
        if (selected.hashCode() == 0) {
            List<ZkbEngineer> zkbEngineerList = this.dao.findBySso(selected.getSso());
            if (CollectionUtils.isNotEmpty(zkbEngineerList)) {
                this.zkbEngineerService.fullSso(this.selected, ssoStartWith);
            }
        }
        ZkbEngineer existedEngineer = this.dao.findByIdentityNumber(this.selected.getIdentityNumber());
        if (existedEngineer != null && !Objects.equals(existedEngineer.getId(), this.selected.getId())) {
            WebUtil.addErrorMessage("保存失败！当前身份证号已存在！");
        } else {
            super.save();
            this.zkbEngineerService.saveUploadFile(this.v2_blobObjectList, this.selected.getId());
            this.zkbEngineerService.removeFile(this.removev2_blobObjectList);
        }
        this.cancel();
    }

    @Override
    public void delete() {
        zkbEngineerService.delete(this.selected, zkbEngineerTrackRecordList);
        this.cancel();
    }

    public void prepareZkbEngineerTrackRecordCreate(){
        this.zkbEngineerTrackRecordSelected = new ZkbEngineerTrackRecord();
        this.zkbEngineerTrackRecordSelected.setZkbEngineerId(this.selected.getId());

    }

    public void prepareZkbEngineerTrackRecordEdit(){
    }

    public void saveZkbEngineerTrackRecord(){
        zkbEngineerService.saveZkbEngineerTrackRecord(this.zkbEngineerTrackRecordSelected);
        zkbEngineerTrackRecordList = zkbEngineerService.saveZkbEngineerTrackRecordList(zkbEngineerTrackRecordList, this.zkbEngineerTrackRecordSelected);
        this.zkbEngineerTrackRecordSelected = null;
    }

    public void deleteZkbEngineerTrackRecord(){
        zkbEngineerService.deleteZkbEngineerTrackRecord(this.zkbEngineerTrackRecordSelected);
        zkbEngineerTrackRecordList = zkbEngineerService.deleteZkbEngineerTrackRecordList(zkbEngineerTrackRecordList, this.zkbEngineerTrackRecordSelected);
        this.zkbEngineerTrackRecordSelected = null;
    }

    public void exportAll(){
        List<Map<String, Object>> zkbEngineerDataList = zkbEngineerExportService.createZkbEngineerData(zkbEngineerPage.getContent());
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/engineer/APM_ZKB_Engineer_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbEngineerExportService.exportData(doc, zkbEngineerDataList, zkbEngineerExportService.createZkbEngineerTrackRecordData(zkbEngineerDataList));
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZKBEngineerData.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbEngineerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

        /**
     * 上传附件
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_ENGINEER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_ENGINEER);
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    /**
     * 移除附件
     * @param v2_blobObject
     */
    public void removeFile(V2_BlobObject v2_blobObject) {
        this.v2_blobObjectList = this.v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
        if (StringUtils.isNotBlank(v2_blobObject.getId())) {
            this.removev2_blobObjectList.add(v2_blobObject);
        }

    }

    /**
     * 初始化附件列表
     *
     * @param id 流程主键
     */
    public void initBlobObjectList(String id) {
        this.v2_blobObjectList = this.blobService.findBlobObjectsByBoUid(id);
    }

    public void tenantInfoSelect() {
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID) {
        return zkbEngineerService.getTenantInfo(tenantInfoUID);
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<ZkbEngineerTrackRecord> getZkbEngineerTrackRecordList() {
        return zkbEngineerTrackRecordList;
    }

    public void setZkbEngineerTrackRecordList(List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList) {
        this.zkbEngineerTrackRecordList = zkbEngineerTrackRecordList;
    }

    public ZkbEngineerTrackRecord getZkbEngineerTrackRecordSelected() {
        return zkbEngineerTrackRecordSelected;
    }

    public void setZkbEngineerTrackRecordSelected(ZkbEngineerTrackRecord zkbEngineerTrackRecordSelected) {
        this.zkbEngineerTrackRecordSelected = zkbEngineerTrackRecordSelected;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<V2_BlobObject> getV2_blobObjectList() {
        return v2_blobObjectList;
    }

    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
        this.v2_blobObjectList = v2_blobObjectList;
    }


    public void handleExcelUpload(FileUploadEvent event) {
        this.file = event.getFile();
        importFailedDataList = zkbEngineerService.handleExcel(file, currentUser);
        if (CollectionUtils.isEmpty(importFailedDataList)) {
            WebUtil.addSuccessMessage("导入完成！");
        } else {
            WebUtil.addErrorMessage("导入失败！请修改错误数据");
        }
    }

}
