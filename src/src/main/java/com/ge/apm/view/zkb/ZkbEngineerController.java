package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbEngineer;
import com.ge.apm.domain.ZkbEngineerTrackRecord;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbEngineerExportService;
import com.ge.apm.service.zkb.ZkbEngineerService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbEngineerRepository.class);
        zkbEngineerService = WebUtil.getBean(ZkbEngineerService.class);
        zkbEngineerExportService = WebUtil.getBean(ZkbEngineerExportService.class);
        userContextService = WebUtil.getBean(UserContextService.class);

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
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        this.selected.setLastModifiedBy(this.currentUser.getLoginName());
        this.selected.setLastModifiedDate(new Date());

        zkbEngineerTrackRecordList = zkbEngineerService.getZkbEngineerTrackRecordList(this.selected.getId());
    }

    @Override
    public void save() {
        super.save();
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

    public void tenantInfoSelect(){
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
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
}
