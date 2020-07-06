package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/30
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderEditController  extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderRepository dao;
    private UserContextService userContextService;
    private UserAccount currentUser;
    private ZkbPartOrderService zkbPartOrderService;
    private String actionName;
    private String actionItemName;
    private String selectionTenantInfo;
    private AssetInfo queryAsset;
    private V2_WorkOrder queryWorkFlow;
    private V2_WorkOrder SelectedWorkFlow;
    private List<HashMap<String, String>> v2_listHashMap;
    private List<ZkbContract> contractList;
    private ZkbPartOrderItem zkbPartOrderItem;
    private String orderType;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        selectionTenantInfo = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectionTenantInfo");
        queryWorkFlow = new V2_WorkOrder();
        queryWorkFlow.setAssetInfo(new AssetInfo());
        queryWorkFlow.setServiceRequest(new V2_ServiceRequest());
        if("Create".equals(actionName)){
            this.selected = new ZkbPartOrder();
        }else{
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            this.selected = m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            if(StringUtils.isNotEmpty(this.selected.getOrderType())){
                orderType = this.selected.getOrderType();
            }
            ZkbContract c = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            if(c != null){
                contractList = new ArrayList<>();
                contractList.add(c);
            }
            HashMap<String, String> mm = m.get("v2") == null ? null : (HashMap<String, String>)m.get("v2");
            v2_listHashMap = new ArrayList<>();
            if(mm != null){
                v2_listHashMap.add(mm);
            }
        }
;    }

    public void getQueryWorkFlowList(){
        if(queryWorkFlow == null){
            WebUtil.addErrorMessage("查询有误，请输入条件","");
            return;
        }
        if((queryWorkFlow.getAssetInfo() == null || StringUtils.isEmpty(queryWorkFlow.getAssetInfo().getSystemId())) &&
           (queryWorkFlow.getServiceRequest() == null || StringUtils.isEmpty(queryWorkFlow.getServiceRequest().getSrNum()))) {
            WebUtil.addErrorMessage("查询有误，请输入条件","");
            return;
        }
        v2_listHashMap = zkbPartOrderService.getQueryWorkFlowList(queryWorkFlow);
        if(v2_listHashMap != null && v2_listHashMap.size() >0){
            contractList = zkbPartOrderService.queryContractByAsset(selectionTenantInfo,String.valueOf(v2_listHashMap.get(0).get("assetId")));
            if(contractList.size() == 0){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "未查询出合同数据！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            if(contractList.size() > 1){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "合同超过两条数据，请检查！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }else{
            contractList = new ArrayList<>();
        }
    }

    public void prepareItemCreate(){
        this.actionItemName = "Create";
        this.zkbPartOrderItem = new ZkbPartOrderItem();
    }

    public void prepareItemEdit(){
        this.actionItemName = "Edit";
    }

    public void deleteItem(){
        this.selected.getZkbPartOrderItems().remove(this.zkbPartOrderItem);
        this.zkbPartOrderItem = null;
        reSortItem();
    }

    public void saveItem(){
        if("Create".equals(this.actionItemName)){
            if(this.selected == null){
                this.selected = new ZkbPartOrder();
            }
            if(this.selected.getZkbPartOrderItems() ==null){
                this.selected.setZkbPartOrderItems(new ArrayList<>());
            }
            int i = this.selected.getZkbPartOrderItems().size();
            this.zkbPartOrderItem.setIndex(i);
            this.selected.getZkbPartOrderItems().add(zkbPartOrderItem);
        }else{
            this.selected.getZkbPartOrderItems().set(this.zkbPartOrderItem.getIndex(),this.zkbPartOrderItem);
        }
        this.zkbPartOrderItem = null;
        this.selected.setQty(calcOrderQty());
    }

    public int calcOrderQty(){
        int rs = 0;
        for(ZkbPartOrderItem pi : this.selected.getZkbPartOrderItems()){
            rs += pi.getPartQty();
        }
        return rs;
    }

    public void reSortItem(){
        int i =0;
        for(ZkbPartOrderItem pi : this.selected.getZkbPartOrderItems()){
            pi.setIndex(i);
            i++;
        }
    }

    /**
     * 保存方法
     */
    @Override
    public void save(){
        boolean flag = true;
        String errorLog = "";
        if(StringUtils.isEmpty(orderType)){
            flag=false;
            errorLog += "订单类型不能为空！";
        }
        if(v2_listHashMap.size() == 0 ){
            flag=false;
            errorLog += "工单数据不能为空！";
        }
        if(contractList.size() == 0){
            flag=false;
            errorLog += "合同数据不能为空！";
        }
        if(contractList.size() > 1){
            flag=false;
            errorLog += "合同数据大于一条！";
        }
        if(this.selected.getZkbPartOrderItems().size() == 0){
            flag=false;
            errorLog += "额度申请明细不呢为空！";
        }
        if(!flag){
            WebUtil.addErrorMessage(errorLog);
            return;
        }
        this.selected.setOrderType(orderType);
        HashMap m = v2_listHashMap.get(0);
        ZkbContract zkbContract = contractList.get(0);
        zkbPartOrderService.create(m.get("id").toString(),m.get("assetId").toString(),zkbContract,this.selected,currentUser,selectionTenantInfo,m.get("srNum").toString());
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

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public V2_WorkOrder getQueryWorkFlow() {
        return queryWorkFlow;
    }

    public void setQueryWorkFlow(V2_WorkOrder queryWorkFlow) {
        this.queryWorkFlow = queryWorkFlow;
    }

    public V2_WorkOrder getSelectedWorkFlow() {
        return SelectedWorkFlow;
    }

    public void setSelectedWorkFlow(V2_WorkOrder selectedWorkFlow) {
        SelectedWorkFlow = selectedWorkFlow;
    }

    public List<HashMap<String, String>> getV2_listHashMap() {
        return v2_listHashMap;
    }

    public void setV2_listHashMap(List<HashMap<String, String>> v2_listHashMap) {
        this.v2_listHashMap = v2_listHashMap;
    }

    public List<ZkbContract> getContractList() {
        return contractList;
    }

    public void setContractList(List<ZkbContract> contractList) {
        this.contractList = contractList;
    }

    public ZkbPartOrderItem getZkbPartOrderItem() {
        return zkbPartOrderItem;
    }

    public void setZkbPartOrderItem(ZkbPartOrderItem zkbPartOrderItem) {
        this.zkbPartOrderItem = zkbPartOrderItem;
    }

    public String getActionItemName() {
        return actionItemName;
    }

    public void setActionItemName(String actionItemName) {
        this.actionItemName = actionItemName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }
}
