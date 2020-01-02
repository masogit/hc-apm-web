package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbEngineer;
import com.ge.apm.domain.ZkbEngineerTrackRecord;
import com.ge.apm.service.zkb.ZkbEngineerService;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
 * @author 212605082 on 2019/12/18.
 */
@ManagedBean
@ViewScoped
public class ZkbEngineerController extends GenericCRUDUUIDController<ZkbEngineer> {

    private ZkbEngineerRepository dao;
    private ZkbEngineerService zkbEngineerService;

    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String selectionTenantInfo;
    private String actionName;

    private List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList;
    private ZkbEngineerTrackRecord zkbEngineerTrackRecordSelected;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbEngineerRepository.class);
        zkbEngineerService = WebUtil.getBean(ZkbEngineerService.class);

        currentUser = UserContextService.getCurrentUserAccount();
        tenantInfoList = zkbEngineerService.getSourceTenantInfoList();
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

        return dao.findBySearchFilter(searchFilters, pageRequest);
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
}
