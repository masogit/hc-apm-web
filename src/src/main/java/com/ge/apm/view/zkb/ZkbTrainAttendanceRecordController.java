package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainAttendanceRecordRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbTrainAttendanceRecord;
import com.ge.apm.service.zkb.ZkbTrainAttendanceRecordService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbTrainAttendanceRecordController extends GenericCRUDUUIDController<ZkbTrainAttendanceRecord> {

    private ZkbTrainAttendanceRecordService zkbTrainAttendanceRecordService;

    private UserContextService userContextService;

    private ZkbTrainAttendanceRecordRepository dao;

    private UserAccount currentUser;

    private String selectionTenantInfo;

    @Getter @Setter
    private UploadedFile file;

    @Getter @Setter
    private List<ZkbTrainAttendanceRecord> importFailedDataList;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbTrainAttendanceRecordRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        zkbTrainAttendanceRecordService = WebUtil.getBean(ZkbTrainAttendanceRecordService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
    }

    @Override
    protected Page<ZkbTrainAttendanceRecord> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("zkbEngineer.tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        this.file = event.getFile();
        importFailedDataList = zkbTrainAttendanceRecordService.handleExcel(file, currentUser);
        if (CollectionUtils.isEmpty(importFailedDataList)) {
            WebUtil.addSuccessMessage("导入完成！");
        } else {
            WebUtil.addErrorMessage("导入失败！请修改错误数据");
        }
    }

    public List<HashMap<String, String>> queryNormalRecords() {
        return zkbTrainAttendanceRecordService.queryNormalRecords();
    }

    @Override
    protected GenericRepositoryUUID<ZkbTrainAttendanceRecord> getDAO() {
        return dao;
    }

    @Override
    public void prepareCreate() {
        selected = new ZkbTrainAttendanceRecord();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
    }

    public void delete() {
        super.delete();
    }

    public void saveRecord() {
        zkbTrainAttendanceRecordService.saveRecord(selected, currentUser);
        selected = null;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }
}