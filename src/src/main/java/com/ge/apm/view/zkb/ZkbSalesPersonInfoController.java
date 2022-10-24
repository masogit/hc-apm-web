package com.ge.apm.view.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbSalesPersonInfoRepository;
import com.ge.apm.dao.ZkbSalesTenantAssociateRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbSalesPersonInfo;
import com.ge.apm.domain.ZkbSalesTenantAssociate;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbSalesPersonInfoService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ZkbSalesPersonInfoController extends GenericCRUDUUIDController<ZkbSalesPersonInfo> {

    /**
     *
     */
    private static final long serialVersionUID = -1L;

    private ZkbSalesPersonInfoService zkbSalesPersonInfoService;

    private ZkbSalesPersonInfoRepository dao;

    private TenantInfoRepository tenantInfoDao;

    private ZkbSalesTenantAssociateRepository zkbSalesTenantAssociateRepository;

    private List<TenantInfo> tenantInfoList;

    private List<String> tenantInfoUids = new ArrayList<>();

    private String tenantInfoUid;

    private UserAccount currentUser;

    private StreamedContent exportFile;

    private UploadedFile uploadedFile;

    private List<HashMap<String,String>> errorImportList= new ArrayList<>();

    private List<TenantInfo> tenantInfosByUids ;


    @Override
    protected GenericRepositoryUUID<ZkbSalesPersonInfo> getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbSalesPersonInfoRepository.class);
        zkbSalesTenantAssociateRepository =  WebUtil.getBean(ZkbSalesTenantAssociateRepository.class);
        zkbSalesPersonInfoService = WebUtil.getBean(ZkbSalesPersonInfoService.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        tenantInfoList = zkbSalesPersonInfoService.getSourceTenantInfoList();
        tenantInfosByUids = new ArrayList<>();
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        if(StringUtils.isNotEmpty(encodeStr)){
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            this.selected = dao.findById(selectedId);
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.tenantInfoUids = new ArrayList<String>();
        this.selected.setSparePartsSelection(true);
        this.selected.setProbeSelection(true);
        this.selected.setCreatedBy(currentUser.getLoginName());
        this.selected.setCreatedTime(new Date());
        this.selected.setModifiedBy(currentUser.getLoginName());
        this.selected.setModifiedTime(new Date());
    }

    @Override
    public void prepareEdit(){
        super.prepareEdit();
        tenantInfoUids = new ArrayList<String>();
        if (selected == null) {
            return;
        }
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList = this.selected.getZkbSalesTenantAssociateList();// zkbSalesTenantAssociateRepository.getBySalesPersonId(selected.getId());
        zkbSalesTenantAssociateList.stream().forEach(a ->{
            tenantInfoUids.add(a.getTenantUid());
        });
        this.selected.setModifiedBy(currentUser.getLoginName());
        this.selected.setModifiedTime(new Date());
    }
    public void prepareAssociateTenant() {
        tenantInfoUids = new ArrayList<String>();
        if (selected == null) {
            return;
        }
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList = this.selected.getZkbSalesTenantAssociateList();// zkbSalesTenantAssociateRepository.getBySalesPersonId(selected.getId());
        zkbSalesTenantAssociateList.stream().forEach(a ->{
            tenantInfoUids.add(a.getTenantUid());
        });
    }

    public void saveSalesTenantAssociate() {
        if (selected == null) {
            return;
        }
        zkbSalesTenantAssociateRepository.deleteBySalesPersonId(this.selected.getId());
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociatesList = new ArrayList<ZkbSalesTenantAssociate>();
        tenantInfosByUids.stream().forEach(tenantInfo -> {
            ZkbSalesTenantAssociate zkbSalesTenantAssociates = new ZkbSalesTenantAssociate();
            zkbSalesTenantAssociates.setTenantUid(tenantInfo.getUid());
            zkbSalesTenantAssociates.setZkbSalesPersonInfo(this.selected);
            zkbSalesTenantAssociates.setCreatedTime(new Date());
            zkbSalesTenantAssociates.setSalesPersonId(this.selected.getId());
            zkbSalesTenantAssociates.setCreatedBy(currentUser.getLoginName());
            zkbSalesTenantAssociatesList.add(zkbSalesTenantAssociates);
         });
        zkbSalesTenantAssociateRepository.save(zkbSalesTenantAssociatesList);

    }
    @Override
    public void onAfterDeleteObject(ZkbSalesPersonInfo zkbSalesPersonInfo, boolean isOK) {
        /*if(isOK){
             List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList =  new ArrayList<>(); //zkbSalesTenantAssociateRepository.getBySalesPersonId(zkbSalesPersonInfo.getId());
            zkbSalesTenantAssociateRepository.delete(zkbSalesTenantAssociateList);
        }*/
    }

    public String getAssociateTenant(String salesPersonId){
        StringBuilder sb = new StringBuilder();
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList =  zkbSalesTenantAssociateRepository.findBySalesPersonId(salesPersonId); //zkbSalesTenantAssociateRepository.getBySalesPersonId(salesPersonId);
        Set<String> uidSet = new HashSet<>();
        zkbSalesTenantAssociateList.stream().forEach(a ->{
            uidSet.add(a.getTenantUid());
        });
        List<TenantInfo> tenantInfoList = tenantInfoDao.findByUidIn(uidSet);
        tenantInfoList.stream().forEach(t ->{
            sb.append(t.getName()).append(" ");
        });
        return sb.toString();
    }

    @Override
    @Transactional
    public void save() {
        if(!this.selected.getSparePartsSelection()&&!this.selected.getProbeSelection()){
            WebUtil.addErrorMessage(WebUtil.getMessage("请至少勾选一种订单类型！"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
       ZkbSalesPersonInfo zkbSalesPersonInfo = dao.save(this.selected);;
       this.saveSalesTenantAssociate();
    }

    public void exportSalesTenantAssociate(){
        List<ZkbSalesTenantAssociate> zkbSalesTenantAssociateList = zkbSalesTenantAssociateRepository.findBySalesPersonId(selected.getId());
        List<HashMap<String, Object>> dataMap = zkbSalesPersonInfoService.createSalesTenantAssociateData(zkbSalesTenantAssociateList);

        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/salesPerson/excel/APM_ZKB_SalePerson_Exp_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbSalesPersonInfoService.exportData(doc, dataMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbSalesTenantAssociateExcel_Export.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String importAction(){
        return UrlEncryptController.encodeUrlParam("Import?selectedid=".concat(selected.getId()));
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"导入完成", event.getFile().getFileName() + " 处理完成！");
        this.uploadedFile = event.getFile();
        errorImportList = zkbSalesPersonInfoService.handleExcel(uploadedFile, currentUser,selected);
        //有错误数据 warning 没有错误数据  info
        if (CollectionUtils.isNotEmpty(errorImportList)){
            message = new FacesMessage(FacesMessage.SEVERITY_WARN,"导入完成", event.getFile().getFileName() + " 处理完成，但有错误数据！");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void exportAllTenant(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        List<TenantInfo> tenantInfos = tenantInfoDao.findBySearchFilter(searchFilters);
        List<HashMap<String, Object>> dataMap = zkbSalesPersonInfoService.createTenantInfoData(tenantInfos);

        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/salesPerson/excel/APM_ZKB_SalePerson_Exp_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbSalesPersonInfoService.exportData(doc, dataMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbTenantInfoExcel_Export.xlsx");
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

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public List<String> getTenantInfoUids() {
        return tenantInfoUids;
    }

    public String getTenantInfoUid() {
        return tenantInfoUid;
    }

    public void setTenantInfoUid(String tenantInfoUid) {
        this.tenantInfoUid = tenantInfoUid;
    }

    public void setTenantInfoUids(List<String> tenantInfoUids) {
        this.tenantInfoUids = tenantInfoUids;
    }

    public List<HashMap<String, String>> getErrorImportList() {
        return errorImportList;
    }

    public void setErrorImportList(List<HashMap<String, String>> errorImportList) {
        this.errorImportList = errorImportList;
    }

    public List<TenantInfo> getTenantInfosByUids() {
        tenantInfosByUids = new ArrayList<TenantInfo>();
        if(CollectionUtils.isNotEmpty(this.tenantInfoUids)){
            Set<String> tenantInfoUidOfSet = tenantInfoUids.stream().map(t ->t).collect(Collectors.toSet());
            tenantInfosByUids = this.tenantInfoDao.findByUidIn(tenantInfoUidOfSet);
        }
        return tenantInfosByUids;
    }

    public void setTenantInfosByUids(List<TenantInfo> tenantInfosByUids) {
        this.tenantInfosByUids = tenantInfosByUids;
    }
}
