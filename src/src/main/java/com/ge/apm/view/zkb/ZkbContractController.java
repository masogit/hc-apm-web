package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.ZkbContractService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 503189362
 * @date : 2020/4/16
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbContractController extends GenericCRUDUUIDController<ZkbContract> {

    private ZkbContractRepository dao;
    private UserContextService userContextService;
    private ZkbContractService zkbContractService;
    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String actionName;
    private String actionDetailName;
    private String selectionTenantInfo;
    private Page<ZkbContract> zkbContractPage;
    private String reason;
    private boolean operateFlag = false;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Override
    protected void init() {
        super.init();
        reason="";
        dao = WebUtil.getBean(ZkbContractRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbContractService = WebUtil.getBean(ZkbContractService.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        tenantInfoList = zkbContractService.getSourceTenantInfoList();
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
        if(!userContextService.isZkbUser()){
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(),ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            if(zkbPartOrderUserConfigs !=null && zkbPartOrderUserConfigs.size() > 0){
                operateFlag = true;
            }
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbContract> getDAO() {
        return dao;
    }

    @Override
    protected Page<ZkbContract> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(this.selectionTenantInfo != null){
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
        }

        zkbContractPage = dao.findBySearchFilter(searchFilters, pageRequest);
        return zkbContractPage;
    }

    @Override
    public void delete() {
        if(!zkbContractService.checkForDelete(selected)){
            WebUtil.addSuccessMessage("合同已存在业务数据，不能删除！");
            this.selected= null;
            this.reason="";
            return;
        }
        zkbContractService.deleteContract(this.selected,this.reason,currentUser);
        this.selected= null;
        this.reason="";
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbContractService.getTenantInfo(tenantInfoUID);
    }
    /**
     * @author : 503189362
     * @description : 租户选择变化时，那么重新进行编辑页面
     * @date : 2020/4/21 15:06
     */
    public void tenantInfoSelect(){
        this.cancel();
    }

    public String getContractState(String value){
        if(value == null || value.isEmpty()) {
            return "";
        } else if(String.valueOf(ZkbContract.ContractState.effective).equals(value)){
            return  "有效";
        }else if(String.valueOf(ZkbContract.ContractState.noactive).equals(value)){
            return  "未生效";
        }else if(String.valueOf(ZkbContract.ContractState.expired).equals(value)){
            return  "已过期";
        }else{
            return "";
        }
    }

    public String qtyRender(String type, String contractType, String value){
        if(userContextService.isZkbUser()) {
            if(ZkbContract.ContractType.DI.toString().equals(contractType) || ZkbContract.ContractType.ULS_OEC.toString().equals(contractType)){
                if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.Probe.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(type)
                ){
                    return "-";
                }
            }else if(ZkbContract.ContractType.Biomed.toString().equals(contractType) || ZkbContract.ContractType.CCS.toString().equals(contractType)){
                if(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString().equals(type)){
                    return "-";
                }
            }else if(ZkbContract.ContractType.PCB.toString().equals(contractType) || "PCB MVS".equals(contractType)){
                if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(type) ||
                    ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(type)
                ){
                    return "-";
                }
            }
        }
        return value;
    }


    public String importAction(){
        return UrlEncryptController.encodeUrlParam("ContractImport?1=1");
    }


    public String createAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Create&selectionTenantInfo=".concat(selectionTenantInfo));
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Edit&selectedid=".concat(selected.getId()));
    }

    public void doubleClick(SelectEvent event){
        selected = (ZkbContract) event.getObject();
        String str = "action=Edit&selectedid=".concat(selected.getId());
        String url = "Edit.xhtml?str="+ UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
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

        List<ZkbContract> contractList = dao.findBySearchFilter(searchFilters);
        List<Map<String, Object>> zkbContractDataList = zkbContractService.createZkbContractData(contractList);

        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/contract/excel/APM_ZKB_Contract_Exp_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbContractService.exportData(doc, zkbContractDataList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZKBContractData.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbContractController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cancel() {
        this.selected = null;
    }

    public boolean isAddCanOperate(){

        if(StringUtils.isNotEmpty(selectionTenantInfo)){
            return !operateFlag;
        }
        return true;
    }

    public boolean isDeleteCanOperate(){
        if(this.selected == null){
            return false;
        }
        if(StringUtils.isEmpty(this.selected.getId())){
            return false;
        }else{
            if(operateFlag){
                return true;
            }else{
                return false;
            }
        }
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDetailName() {
        return actionDetailName;
    }

    public void setActionDetailName(String actionDetailName) {
        this.actionDetailName = actionDetailName;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isOperateFlag() {
        return operateFlag;
    }

    public void setOperateFlag(boolean operateFlag) {
        this.operateFlag = operateFlag;
    }
}
