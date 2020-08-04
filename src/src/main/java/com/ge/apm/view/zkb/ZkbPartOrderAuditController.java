package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.dao.ZkbPartOrderReturnRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/7/6
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderAuditController extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderService zkbPartOrderService;
    private UserContextService userContextService;
    private UserAccount currentUser;
    private String actionName;
    private V2_WorkOrder selectedWorkFlow;
    private ZkbContract zkbContract;
    private String orderType;
    private String artificialType;
    private List<V2_BlobObject> v2_blobObjectList;
    private boolean showTech=false;
    private boolean showManage=false;
    private boolean showOrder=false;
    private boolean showReturn=false;
    private boolean showClosed=false;
    private String showBtn = "";
    private ZkbPartOrderReturn zkbPartOrderReturn;
    private List<ZkbPartOrderReturn> zkbPartOrderReturnList;
    private String actionReturnName;
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;
    private ZkbPartOrderRepository zkbPartOrderRepository;
    private List<HashMap<String, String>> partNoList = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbPartOrderReturnRepository = WebUtil.getBean(ZkbPartOrderReturnRepository.class);
        zkbPartOrderRepository = WebUtil.getBean(ZkbPartOrderRepository.class);


        currentUser = UserContextService.getCurrentUserAccount();

        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if(StringUtils.isNotEmpty(selectedId)){
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            this.selected = m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            if(StringUtils.isNotEmpty(this.selected.getOrderType())){
                orderType = this.selected.getOrderType();
            }
            if(StringUtils.isNotEmpty(this.selected.getArtificialType())){
                artificialType = this.selected.getArtificialType();
            }
            zkbContract = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            selectedWorkFlow = m.get("V2_WorkOrder") == null ? null : (V2_WorkOrder)m.get("V2_WorkOrder");
            v2_blobObjectList = m.get("v2_blobObjectList") == new ArrayList<>() ? null : (List<V2_BlobObject>)m.get("v2_blobObjectList");
            zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selectedId);

            isFieldRendered();
        }
    }

    public void isFieldRendered(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            showTech = false;
            if("2".equals(selected.getArtificialType())){
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus())){
                    showBtn = "1";
                    showOrder = true;
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    showOrder = true;
                    showBtn = "";
                }
            }else{
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus())){
                    showManage = true;
                    showOrder = false;
                    showBtn = "0";
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    showManage = true;
                    showOrder = true;
                    showBtn = "1";
                }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    showManage = true;
                    showOrder = false;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    showOrder = true;
                    showOrder = true;
                    showBtn = "";
                }
            }
            showReturn = false;
            showClosed = false;
        }else{
            if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus())){
                if(selected.getAssetInfo().getFunctionGroup() == 1){
                    showManage = true;
                }else{
                    showTech =true;
                }
                showBtn = "0";
            } else if(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString().equals(selected.getStatus())){
                showTech= true;
                showManage = true;
                showBtn = "0";
            } else if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(selected.getStatus())){
                showTech= true;
            } else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                if(selected.getAssetInfo().getFunctionGroup() == 1){
                    showTech= false;
                }else {
                    showTech= true;
                }
                showManage = true;
                showOrder = true;
                showBtn = "1";
            } else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                if(selected.getAssetInfo().getFunctionGroup() == 1){
                    showTech= false;
                }else {
                    showTech= true;
                }
                showManage = true;
            } else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                if(selected.getAssetInfo().getFunctionGroup() == 1){
                    showTech= false;
                }else {
                    showTech= true;
                }
                showManage = true;
                showOrder = true;
                showReturn = true;
                showBtn = "1";
            } else if(ZkbPartOrder.PartOrderStatus.Returned.toString().equals(selected.getStatus())){
                if(selected.getAssetInfo().getFunctionGroup() == 1){
                    showTech= false;
                }else {
                    showTech= true;
                }
                showManage = true;
                showOrder = true;
                showReturn = true;
                showClosed = true;
                showBtn = "1";
            } else if(ZkbPartOrder.PartOrderStatus.Closed.toString().equals(selected.getStatus())){
                if(selected.getAssetInfo().getFunctionGroup() == 1){
                    showTech= false;
                }else {
                    showTech= true;
                }
                showManage = true;
                showOrder = true;
                showReturn = true;
                showClosed = true;
                showBtn = "";
            }
        }
    }

    public String getShowBtn() {
        return showBtn;
    }

    public void setShowBtn(String showBtn) {
        this.showBtn = showBtn;
    }

    public String acceptNext(){
        zkbPartOrderService.auditNext(this.selected,true,currentUser);
        return "List?faces-redirect=true";
    }

    public String rejectNext(){
        zkbPartOrderService.auditNext(this.selected,false,currentUser);
        return "List?faces-redirect=true";
    }

    public String normalNext(){
        zkbPartOrderService.normalNext(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder);
        return "List?faces-redirect=true";
    }

    public String orderCancelNext(){
        zkbPartOrderService.orderCancelNext(this.selected,currentUser);
        return "List?faces-redirect=true";
    }

    public void prepareReturnCreate(){
        this.actionReturnName = "Create";
        this.zkbPartOrderReturn = new ZkbPartOrderReturn();
        zkbPartOrderReturn.setPartOrderId(selected.getId());
        initPartList();
    }

    public void prepareReturnEdit(){
        this.actionReturnName = "Edit";
        initPartList();
    }

    public void initPartList(){
        List<ZkbPartOrderItem> zkbPartOrderItemList = zkbPartOrderService.queryItemBuPartOrderId(selected.getId());
        for(ZkbPartOrderItem i :zkbPartOrderItemList){
            HashMap<String, String> m = new HashMap<>();
            m.put("text",i.getPartName());
            m.put("value",i.getPartNo());
            partNoList.add(m);
        }
    }

    public void deleteReturn(){
        zkbPartOrderReturnRepository.delete(zkbPartOrderReturn);
        zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId());
    }

    public void saveReturn(){
        boolean flag = true;
        for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
            if(zkbPartOrderReturn.getPartNo().equals(i.getPartNo())){
                if(i.getPartQty() < zkbPartOrderReturn.getQty()){
                    flag = false;
                }
            }
        }
        if(!flag){
            WebUtil.addErrorMessage("归还数量不能大于配件数量","");
            zkbPartOrderReturn = null;
            zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId());
            return;
        }

        zkbPartOrderReturnRepository.save(zkbPartOrderReturn);
        zkbPartOrderReturn = null;
        zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId());
    }

    public String getArtificialType() {
        return artificialType;
    }

    public void setArtificialType(String artificialType) {
        this.artificialType = artificialType;
    }

    public boolean isShowTech() {
        return showTech;
    }

    public void setShowTech(boolean showTech) {
        this.showTech = showTech;
    }

    public boolean isShowManage() {
        return showManage;
    }

    public void setShowManage(boolean showManage) {
        this.showManage = showManage;
    }

    public boolean isShowOrder() {
        return showOrder;
    }

    public void setShowOrder(boolean showOrder) {
        this.showOrder = showOrder;
    }

    public boolean isShowReturn() {
        return showReturn;
    }

    public void setShowReturn(boolean showReturn) {
        this.showReturn = showReturn;
    }

    public boolean isShowClosed() {
        return showClosed;
    }

    public void setShowClosed(boolean showClosed) {
        this.showClosed = showClosed;
    }

    public V2_WorkOrder getSelectedWorkFlow() {
        return selectedWorkFlow;
    }

    public void setSelectedWorkFlow(V2_WorkOrder s) {
        this.selectedWorkFlow = s;
    }

    public ZkbContract getZkbContract() {
        return zkbContract;
    }

    public void setZkbContract(ZkbContract zkbContract) {
        this.zkbContract = zkbContract;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<V2_BlobObject> getV2_blobObjectList() {
        return v2_blobObjectList;
    }

    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
        this.v2_blobObjectList = v2_blobObjectList;
    }

    public ZkbPartOrderReturn getZkbPartOrderReturn() {
        return zkbPartOrderReturn;
    }

    public void setZkbPartOrderReturn(ZkbPartOrderReturn zkbPartOrderReturn) {
        this.zkbPartOrderReturn = zkbPartOrderReturn;
    }

    public String getActionReturnName() {
        return actionReturnName;
    }

    public void setActionReturnName(String actionReturnName) {
        this.actionReturnName = actionReturnName;
    }

    public List<ZkbPartOrderReturn> getZkbPartOrderReturnList() {
        return zkbPartOrderReturnList;
    }

    public void setZkbPartOrderReturnList(List<ZkbPartOrderReturn> zkbPartOrderReturnList) {
        this.zkbPartOrderReturnList = zkbPartOrderReturnList;
    }

    public List<HashMap<String, String>> getPartNoList() {
        return partNoList;
    }

    public void setPartNoList(List<HashMap<String, String>> partNoList) {
        this.partNoList = partNoList;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return zkbPartOrderService.getZkbPartOrderRepository();
    }
}
