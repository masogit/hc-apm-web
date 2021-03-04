package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbIntegralRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbIntegralService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbIntegralController extends GenericCRUDUUIDController<ZkbIntegral> {

    private ZkbIntegralRepository zkbIntegralRepository;
    private ZkbIntegralService zkbIntegralService;
    private String selectionTenantInfo;
    private String actionName;
    private List<TenantInfo> tenantInfoList;
    private UserAccount currentUser;
    private List<ZkbIntegralRecord> zkbIntegralRecordList;
    private UserContextService userContextService;

    @Override
    protected void init() {
        super.init();
        zkbIntegralRepository = WebUtil.getBean(ZkbIntegralRepository.class);
        zkbIntegralService = WebUtil.getBean(ZkbIntegralService.class);
        tenantInfoList = zkbIntegralService.getSourceTenantInfoList();
        currentUser = UserContextService.getCurrentUserAccount();
        userContextService = WebUtil.getBean(UserContextService.class);
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbIntegral> getDAO() {
        return zkbIntegralRepository;
    }

    @Override
    protected Page<ZkbIntegral> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }
        return zkbIntegralRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";
        selected = new ZkbIntegral();
        selected.setCoefficient(ZkbIntegralService.initCoefficient);
        selected.setIntegralTotal(ZkbIntegralService.initialScore);
    }

    @Override
    public void prepareEdit(){
        super.prepareEdit();
        this.actionName = "Edit";
    }

    @Override
    public void save(){
        if(StringUtils.isEmpty(selected.getId())){
            ZkbIntegral exist = zkbIntegralRepository.findFirstByTenantUID(selected.getTenantUID());
            if(exist != null){
                WebUtil.addErrorMessage("租户已存在积分数据","");
                return;
            }
        }
        zkbIntegralService.save(selected,currentUser);
        this.selected = null;
        this.actionName = "";
    }

    public void cancel(){
        this.selected = null;
        this.actionName = "";
    }

    public String getRecordTypeName(String ty){
        return zkbIntegralService.getRecordTypeName(ty);
    }

    private StreamedContent exportFile;

    public void exportAll(){
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<ZkbIntegral> zkbPartOrders =  zkbIntegralRepository.findBySearchFilter(searchFilters,sort);
        HashMap<String, Object> dataMap = zkbIntegralService.createZkbPartOrderData(zkbPartOrders);

        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/integral/APM_ZKB_Integral_Exp_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbIntegralService.exportData(doc, dataMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbPartOrderExcel_Export.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public void viewRecord(String id){
        zkbIntegralRecordList = zkbIntegralService.queryRecordByIntegralId(id);
    }

    public List<ZkbIntegralRecord> getZkbIntegralRecordList() {
        return zkbIntegralRecordList;
    }

    public void setZkbIntegralRecordList(List<ZkbIntegralRecord> zkbIntegralRecordList) {
        this.zkbIntegralRecordList = zkbIntegralRecordList;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbIntegralService.getTenantInfo(tenantInfoUID);
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

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }
}
