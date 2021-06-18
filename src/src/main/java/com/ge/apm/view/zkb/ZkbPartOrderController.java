package com.ge.apm.view.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderController  extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderRepository dao;
    private UserContextService userContextService;
    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String actionName;
    private String selectionTenantInfo;
    private Page<ZkbPartOrder> zkbContractPage;
    private ZkbPartOrderService zkbPartOrderService;
    private ZkbPartOrderExcelService zkbPartOrderExcelService;
    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();
    private String queryStatus = "";
    private boolean showStatusBtn = true;
    private boolean vacationBtn = false;
    private String isVacation;
    private boolean canDelete= false;
    private boolean operateDelete = false;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        zkbPartOrderExcelService = WebUtil.getBean(ZkbPartOrderExcelService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        queryStatus =WebUtil.getRequestParameter("queryStatus");
        selectionTenantInfo =WebUtil.getRequestParameter("tenantUID");
        if(StringUtils.isEmpty(queryStatus)){
            queryStatus = "";
        }
        if(StringUtils.isEmpty(selectionTenantInfo)){
            selectionTenantInfo = "";
        }
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
        }else {
            zkbPartOrderUserConfigList = zkbPartOrderService.getZkbPartOrderUserConfigRepository().findByLoginName(currentUser.getLoginName());
            if(zkbPartOrderUserConfigList != null && zkbPartOrderUserConfigList.size() >0){
                ZkbPartOrderUserConfig zkbPartOrderUserConfig = zkbPartOrderUserConfigList.get(0);
                if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    showStatusBtn =false;
                }
                if(zkbPartOrderUserConfigList.size() == 2){
                    isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? CopyByInvok.F : zkbPartOrderUserConfig.getIsVacation();
                    vacationBtn = true;
                }else if(zkbPartOrderUserConfigList.size() == 1){
                    if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) && ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrderUserConfig.getOrderType())){
                        isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? CopyByInvok.F : zkbPartOrderUserConfig.getIsVacation();
                        vacationBtn = true;
                    }
                    if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) && "US".equals(zkbPartOrderUserConfig.getAssetType())){
                        isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? CopyByInvok.F : zkbPartOrderUserConfig.getIsVacation();
                        vacationBtn = true;
                    }
                }
                for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
                    if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(c.getConfigType())){
                        canDelete = true;
                        break;
                    }
                }
            }
        }

        tenantInfoList = zkbPartOrderService.getSourceTenantInfoList();
    }


    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<ZkbPartOrder> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = getSearchFilter(this.searchFilters);
        zkbContractPage = dao.findBySearchFilter(searchFilters, pageRequest);
        //searchFilters.clear();
        return zkbContractPage;
    }

    public List<SearchFilter> getSearchFilter(List<SearchFilter> csearchFilters){
        List<SearchFilter> searchFilters = csearchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
            searchFilters.add(new SearchFilter("dataType", SearchFilter.Operator.EQ, ZkbPartOrder.DATA_TYPE_TEST));
        }else {
            searchFilters.add(new SearchFilter("dataType", SearchFilter.Operator.IsNull, ""));
        }
        if("letter".equals(queryStatus)){
            if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
            }
            searchFilters.add(new SearchFilter("needLetterFlag", SearchFilter.Operator.EQ, "t"));
            searchFilters.add(new SearchFilter("uploadLetterFlag", SearchFilter.Operator.EQ, "f"));
        }else if(userContextService.isZkbUser()) {
            if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
            }
            if(StringUtils.isEmpty(queryStatus)){
                searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ReturnPending.toString()));
            }else if(!"all".equals(queryStatus)){
                searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, queryStatus));
            }
        }else{
            if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectionTenantInfo));
            }

            if(zkbPartOrderUserConfigList == null || zkbPartOrderUserConfigList.size() == 0){
                //return null;
            }else if(zkbPartOrderUserConfigList.size() == 1){
                //region 用户只有一种角色
                ZkbPartOrderUserConfig zkbPartOrderUserConfig = zkbPartOrderUserConfigList.get(0);
                if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if("US".equals(zkbPartOrderUserConfig.getAssetType())){
                        if(StringUtils.isEmpty(queryStatus)){
                            searchFilters.add(new SearchFilter("onlineEngineer", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getId()));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString()));
                        }else if("all".equals(queryStatus)){
                            searchFilters.add(new SearchFilter("onlineEngineer", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getId()));
                        }
                    }else {
                        if(StringUtils.isEmpty(queryStatus)){
                            searchFilters.add(new SearchFilter("assetTypeDi", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getAssetType()));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString()));
                        }else if("all".equals(queryStatus)){
                            /*if(searchFilters.size() > 0) {
                                searchFilters.add(new SearchFilter("assetTypeDi", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getAssetType(),"1*2+3*"));
                                searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString(),"1*2+3*"));
                                searchFilters.add(new SearchFilter("techAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(),"1*2+"));
                            }else{
                                searchFilters.add(new SearchFilter("assetTypeDi", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getAssetType(),"1+2*"));
                                searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString(),"1+2*"));
                                searchFilters.add(new SearchFilter("techAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(),"1+"));
                            }*/
                            searchFilters.add(new SearchFilter("assetTypeDi", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getAssetType(),"1*2+3*"));
                        }
                    }

                }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(StringUtils.isEmpty(queryStatus)){
                        searchFilters.add(new SearchFilter("region", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getRegion()));
                        searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ManagePending.toString()));
                    }else if("all".equals(queryStatus)){
                        if(searchFilters.size() > 0) {
                            searchFilters.add(new SearchFilter("region", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getRegion(),"1*2+3*"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ManagePending.toString(),"1*2+3*"));
                            searchFilters.add(new SearchFilter("manageAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(),"1*2+"));
                        }else {
                            searchFilters.add(new SearchFilter("region", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getRegion(), "1+2*"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ManagePending.toString(), "1+2*"));
                            searchFilters.add(new SearchFilter("manageAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(), "1+"));
                        }
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(StringUtils.isEmpty(queryStatus)){
                        searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.DirectorPending.toString()));
                    }else if("all".equals(queryStatus)){
                        if(searchFilters.size() > 0) {
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.DirectorPending.toString(),"1*2+"));
                            searchFilters.add(new SearchFilter("directorAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(),"1*2+"));
                        }else {
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.DirectorPending.toString(), "1+"));
                            searchFilters.add(new SearchFilter("directorAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(), "1+"));
                        }
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrderUserConfig.getOrderType())){
                        if(StringUtils.isEmpty(queryStatus)){
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderPending.toString()));
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbPartOrder.OrderType.Probe.toString()));
                        }else if(!"all".equals(queryStatus)){
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, queryStatus));
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbPartOrder.OrderType.Probe.toString()));
                        }else{
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbPartOrder.OrderType.Probe.toString()));
                        }
                    }else{
                        if(StringUtils.isEmpty(queryStatus)){
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderPending.toString()));
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.NE, ZkbPartOrder.OrderType.Probe.toString()));
                        }else if(!"all".equals(queryStatus)){
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, queryStatus));
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.NE, ZkbPartOrder.OrderType.Probe.toString()));
                        }
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(StringUtils.isEmpty(queryStatus)){
                        searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString()));
                    }else if(!"all".equals(queryStatus)){
                        searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, queryStatus));
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(StringUtils.isEmpty(queryStatus)){
                        if(searchFilters.size() > 0) {
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ClosePending.toString(),"1*2+"));
                            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.PartReturned.toString(),"1*2+"));
                        }else {
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ClosePending.toString(),"1+"));
                            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.PartReturned.toString(),"1+"));
                        }
                    }else if("all".equals(queryStatus)){
                        /*if(searchFilters.size() > 0 ){
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ClosePending.toString(),"1*2+"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.Complete.toString(),"1*2+"));
                        }else{
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.ClosePending.toString(),"1+"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.Complete.toString(),"1+"));
                        }*/

                        /*searchFilters.add(new SearchFilter("closedAuditerUid", SearchFilter.Operator.EQ, currentUser.getUid(),"1+"));*/
                    }else{
                        searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, queryStatus));

                    }
                }
                //endregion
            }else if(zkbPartOrderUserConfigList.size() == 2){
                //判断是否为肖雨的角色
                boolean is4ooRole = true;
                for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
                    if(StringUtils.isEmpty(c.getConfigType())){
                        is4ooRole = false;
                    }
                    if(!c.getConfigType().contains("Order")){
                        is4ooRole = false;
                    }
                }
                if(is4ooRole) {
                    if (StringUtils.isEmpty(queryStatus)) {
                        if(searchFilters.size() > 0){
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.NE, ZkbPartOrder.OrderType.Probe.toString(),"1*2+3*"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderPending.toString(),"1*2+3*"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString(),"1*2+"));
                        }else{
                            searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.NE, ZkbPartOrder.OrderType.Probe.toString(),"1+2*"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderPending.toString(),"1+2*"));
                            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString(),"1+"));
                        }
                    }else if(!"all".equals(queryStatus)){
                        searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, queryStatus));
                    }
                }
            }
        }
        return searchFilters;
    }

    public String techAuditField(ZkbPartOrder zkbPartOrder){
        if(StringUtils.isNotEmpty(zkbPartOrder.getTechAuditer())){
            return zkbPartOrder.getTechAuditer();
        }else if(StringUtils.isNotEmpty(zkbPartOrder.getOnlineEngineer())){
            return zkbPartOrder.getOnlineEngine().getName();
        }else{
            return zkbPartOrder.getAssetTypeDi();
        }
    }

    public String scoreFlagField(String scoreFlag){
        if(StringUtils.isEmpty(scoreFlag)){
            return "名额申请";
        }else if(CopyByInvok.T.equals(scoreFlag)){
            return "积分申请";
        }else if(CopyByInvok.F.equals(scoreFlag)){
            return "名额申请";
        }else{
            return "名额申请";
        }
    }

    public String queryListByStatus(String status){
        queryStatus = status;
        return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
    }

    public void doubleClick(SelectEvent event){
        selected = (ZkbPartOrder) event.getObject();
        String str = "action=Edit&selectedid=".concat(selected.getId())+"&queryStatus="+queryStatus;
        String url = "AuditStep.xhtml?str="+UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public boolean isContainOrder(){
        boolean flag = false;
        for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
            if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(c.getConfigType())){
                flag = true;
                break;
            }
        }
        return  flag;
    }

    public boolean isContainManage(){
        boolean flag = false;
        for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
            if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(c.getConfigType())){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbPartOrderService.getTenantInfo(tenantInfoUID);
    }

    /**
     * @author : 503189362
     * @description : 租户选择变化时，那么重新进行编辑页面
     * @date : 2020/4/21 15:06
     */
    public void tenantInfoSelect(){
        this.cancel();
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        if(StringUtils.isEmpty(selected.getId())){
            operateDelete = Boolean.TRUE;
        }else if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.DirectorReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString().equals(selected.getStatus())
        ){
            operateDelete = Boolean.FALSE;
        }else{
            operateDelete = Boolean.TRUE;
        }
    }


    @Override
    public void delete() {
        //if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus())){
            zkbPartOrderService.delete(this.selected,currentUser);
            this.selected= null;
        //}else {
           /* WebUtil.addErrorMessage("数据已审批不能删除！","");
            return;*/
        //}
    }

    public void saveVacation(){
        boolean flag = false;
        ZkbPartOrderUserConfig zkbPartOrderUserConfig = new ZkbPartOrderUserConfig();
        for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
            c.setIsVacation(isVacation);
            zkbPartOrderService.getZkbPartOrderUserConfigRepository().save(c);
            if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(c.getConfigType()) && "US".equals(c.getAssetType())){
                flag = true;
                zkbPartOrderUserConfig = c;
            }
        }
        if(flag){
            List<ZkbPartOrder> zkbPartOrders = zkbPartOrderService.usVacationHandle(zkbPartOrderUserConfig,isVacation);
            for(ZkbPartOrder o : zkbPartOrders){
                zkbPartOrderService.sendEmail(o,"",UserContextService.getAccessToken());
                zkbPartOrderService.sendMessage(o,UserContextService.getAccessToken());
            }
        }
    }


    public String createAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Create&selectionTenantInfo=".concat(selectionTenantInfo)+"&queryStatus="+queryStatus);
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("AuditStep?action=Edit&selectedid=".concat(selected.getId())+"&queryStatus="+queryStatus);
    }
    private StreamedContent exportFile;

    public void exportAll(){
        List<SearchFilter> searchFilters = getSearchFilter(this.searchFilters);
        Sort sort=new Sort(Sort.Direction.DESC,"createdDate");
        List<ZkbPartOrder> zkbPartOrders =  dao.findBySearchFilter(searchFilters,sort);
        HashMap<String, Object> dataMap = zkbPartOrderExcelService.createZkbPartOrderData(zkbPartOrders);

        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/partOrder/excel/APM_ZKB_PartOrder_Exp_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbPartOrderExcelService.exportData(doc, dataMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbPartOrderExcel_Export.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String copyAction(){
        /*try {
            ZkbPartOrder newPartOrder = zkbPartOrderService.copyZkbPartOrder(selected,currentUser);
            zkbPartOrderService.sendEmail(newPartOrder);
            zkbPartOrderService.sendMessage(newPartOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return UrlEncryptController.encodeUrlParam("Edit?action=Copy&selectionTenantInfo=".concat(selectionTenantInfo)+"&selectedid=".concat(selected.getId())+"&queryStatus="+queryStatus);

    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
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

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public Page<ZkbPartOrder> getZkbContractPage() {
        return zkbContractPage;
    }

    public void setZkbContractPage(Page<ZkbPartOrder> zkbContractPage) {
        this.zkbContractPage = zkbContractPage;
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    public boolean isShowStatusBtn() {
        return showStatusBtn;
    }

    public void setShowStatusBtn(boolean showStatusBtn) {
        this.showStatusBtn = showStatusBtn;
    }

    public String getIsVacation() {
        return isVacation;
    }

    public void setIsVacation(String isVacation) {
        this.isVacation = isVacation;
    }

    public boolean isVacationBtn() {
        return vacationBtn;
    }

    public void setVacationBtn(boolean vacationBtn) {
        this.vacationBtn = vacationBtn;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isOperateDelete() {
        return operateDelete;
    }

    public void setOperateDelete(boolean operateDelete) {
        this.operateDelete = operateDelete;
    }
}
