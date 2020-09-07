package com.ge.apm.view.zkb;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.dao.ZkbPartOrderReturnRepository;
import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import net.sf.json.util.WebUtils;
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
    private boolean canEditFlag=false;
    private String showBtn = "";
    private ZkbPartOrderReturn zkbPartOrderReturn;
    private List<ZkbPartOrderReturn> zkbPartOrderReturnList;
    private String actionReturnName;
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private ZkbPartOrderRepository zkbPartOrderRepository;
    private I18nMessageRepository i18nDao;

    private List<HashMap<String, String>> partNoList = new ArrayList<>();
    private List<I18nMessage> returnTypeList= new ArrayList<>();
    private String valueTypeTips = "";

    @Override
    protected void init() {
        super.init();
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbPartOrderReturnRepository = WebUtil.getBean(ZkbPartOrderReturnRepository.class);
        zkbPartOrderRepository = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
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
            fieldRendered();
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    selected.setRemainQty(zkbContract.getSpareQuota());
                }else{
                    selected.setRemainQty(zkbContract.getSpareArtificail());
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                selected.setRemainQty(zkbContract.getProbeQuota());
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
                /*if("1".equals(selected.getArtificialType())){
                    selected.setRemainQty(zkbContract.getSpareArtificail());
                }else if("3".equals(selected.getArtificialType())){
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())){
                        selected.setRemainQty(zkbContract.getSpareArtificail());
                    }else if(ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                        selected.setRemainQty(zkbContract.getGeArtificialSupportTimes());
                    }
                }*/
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    selected.setRemainQty(zkbContract.getGeArtificialSupportTimes());
                }else{
                    selected.setRemainQty(zkbContract.getSpareArtificail());
                }
            }else if(ZkbPartOrder.OrderType.Tool.toString().equals(orderType)){
                selected.setRemainQty(zkbContract.getSpareQuota());
            }

            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep())){
                this.selected.setReturnQty(this.selected.getQty());
            }

            if(this.selected.getZkbPartOrderCat() != null){
                if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                    valueTypeTips = "";
                }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                    valueTypeTips = "您的选择为高值探头，请确认合同中是否有此权益！";
                }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                    valueTypeTips = "";
                }
            }
            initReturnList();
        }
    }

    public void fieldRendered(){
        if(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep())){
            if(currentUser.getTenantUID().equals(selected.getTenantUID())){
                canEditFlag = true;
            }
        }else{
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = new ArrayList<>();
            if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndRegion(selected.getCurrentStep(),selected.getRegion());
            }else if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(selected.getCurrentStep())){
                if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo() != null && selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup() == 2){
                        ZkbPartOrderUserConfig zkbPartOrderUserConfig = selected.getOnlineEngine();
                        zkbPartOrderUserConfigs.add(zkbPartOrderUserConfig);
                    }else if(selected.getAssetInfo() != null && selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup() == 1){
                        zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetType(selected.getCurrentStep(),selected.getAssetTypeDi());
                    }
                }
            }else {
                zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigType(selected.getCurrentStep());
            }
            if(zkbPartOrderUserConfigs != null && zkbPartOrderUserConfigs.size() > 0){
                for (ZkbPartOrderUserConfig c : zkbPartOrderUserConfigs){
                    if(currentUser.getLoginName().equals(c.getLoginName())){
                        canEditFlag = true;
                    }
                }
            }
        }

        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            //region 人工页面初始化
            showTech = false;
            //超声高端
            if("2".equals(selected.getArtificialType())){
                // 申请并有权限
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && canEditFlag){
                    showBtn = "1";
                    showOrder = true;
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    showOrder = true;
                    showBtn = "";
                }
            }else{
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && canEditFlag){
                    showManage = true;
                    showOrder = false;
                    showBtn = "0";
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    showManage = true;
                    if(canEditFlag){
                        showOrder = true;
                        showBtn = "1";
                    }
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
            //endregion
        }else{
            if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && canEditFlag){
                if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    showManage = true;
                }else{
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                        showManage = true;
                    }else{
                        showTech =true;
                    }
                }
                showBtn = "0";
            } else if(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString().equals(selected.getStatus())){
                showTech= true;
                if(canEditFlag){
                    showManage = true;
                    showBtn = "0";
                }
            } else if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(selected.getStatus())){
                showTech= true;
            } else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }

                showManage = true;
                if(canEditFlag){
                    showOrder = true;
                    showBtn = "1";
                }
            } else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
            } else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                showOrder = true;
                if(canEditFlag){
                    showReturn = true;
                    showBtn = "1";
                }
            } else if(ZkbPartOrder.PartOrderStatus.Returned.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                showOrder = true;
                showReturn = true;
                if(canEditFlag){
                    showClosed = true;
                    showBtn = "1";
                }
            } else if(ZkbPartOrder.PartOrderStatus.Closed.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                showOrder = true;
                showReturn = true;
                showClosed = true;
                showBtn = "";
            }
        }
    }

    public void initReturnList(){
        returnTypeList = i18nDao.getByMsgType("zkbPoReturnType");
        if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            List<I18nMessage> removeI18 = new ArrayList<>();
            for(I18nMessage i18nMessage : returnTypeList){
                if(!"DOA".equals(i18nMessage.getMsgKey()) && !"goodReturn".equals(i18nMessage.getMsgKey())){
                    removeI18.add(i18nMessage);
                }
            }
            if(removeI18.size() > 0){
                returnTypeList.removeAll(removeI18);
            }
        }
    }



    public String acceptNext(){
        zkbPartOrderService.auditNext(this.selected,true,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendMessage(zkbPartOrder);
        zkbPartOrderService.sendEmail(zkbPartOrder);
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder);
        return "List?faces-redirect=true";
    }

    public String rejectNext(){
        zkbPartOrderService.auditNext(this.selected,false,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder);
        return "List?faces-redirect=true";
    }

    public String normalNext(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){
                if(selected.getReturnQty() > selected.getQty()){
                    WebUtil.addErrorMessage("归还数量不能大于申请数量","");
                    return "";
                }
            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){
                HashMap<String, Integer> returnMap = new HashMap<>();
                for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
                    if(returnMap.containsKey(r.getPartNo())){
                        Integer v = returnMap.get(r.getPartNo());
                        v += r.getQty();
                        returnMap.put(r.getPartNo(),v);
                    }else{
                        returnMap.put(r.getPartNo(),r.getQty());
                    }
                }
                boolean flag = true;
                String errorLog = "";
                for(ZkbPartOrderItem zkbPartOrderItem : selected.getZkbPartOrderItems()){
                    Integer returnQty = returnMap.get(zkbPartOrderItem.getPartNo());
                    if(returnQty == null){
                        flag = false;
                        errorLog += zkbPartOrderItem.getPartNo() + ",";
                    }else if(returnQty != zkbPartOrderItem.getPartQty()){
                        flag = false;
                        errorLog += zkbPartOrderItem.getPartNo() + ",";
                    }
                }
                if(!flag){
                    errorLog = errorLog.substring(0,errorLog.length()-1);
                    WebUtil.addErrorMessage(errorLog+"归还数量不等于申请数量","");
                    return "";
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                boolean flag = true;
                String errorLog = "";
                for(ZkbPartOrderItem zkbPartOrderItem : selected.getZkbPartOrderItems()){
                    if(StringUtils.isEmpty(zkbPartOrderItem.getPartNo())){
                        flag = false;
                        errorLog += "配件号不能为空,";
                    }
                    if(StringUtils.isEmpty(zkbPartOrderItem.getPartName())){
                        flag = false;
                        errorLog += "配件名称不能为空,";
                    }
                    if(StringUtils.isEmpty(zkbPartOrderItem.getPrice())){
                        flag = false;
                        errorLog += "价格不能为空,";
                    }
                    ZkbPartOrderItem oldItem = zkbPartOrderService.getZkbPartOrderItemRepository().findById(zkbPartOrderItem.getId());
                    if(oldItem != null){
                        if(!oldItem.getPartNo().equals(zkbPartOrderItem.getPartNo()) || !oldItem.getPartName().equals(zkbPartOrderItem.getPartName())){
                            if(StringUtils.isEmpty(zkbPartOrderItem.getRemark())){
                                flag = false;
                                errorLog += "配件号或配件名称修改后备注不能为空,";
                            }
                        }
                    }
                }
                if(!flag){
                    errorLog = errorLog.substring(0,errorLog.length()-1);
                    WebUtil.addErrorMessage(errorLog,"");
                    return "";
                }
            }
        }
        zkbPartOrderService.normalNext(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder);
        if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(zkbPartOrder.getCurrentStep())){
            //归还时发送邮件
            zkbPartOrderService.sendEmail(zkbPartOrder);
        }
        return "List?faces-redirect=true";
    }

    public String orderCancelNext(){
        zkbPartOrderService.orderCancelNext(this.selected,currentUser);
        return "List?faces-redirect=true";
    }

    public String dialogComplete(){
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
        partNoList = new ArrayList<>();
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
        Integer total = 0;
        for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
            if(zkbPartOrderReturn.getPartNo().equals(i.getPartNo())){
                total = i.getPartQty();
                break;
            }
        }
        Integer returnQty = 0;
        List<ZkbPartOrderReturn> returns = new ArrayList<>();
        if(StringUtils.isEmpty(zkbPartOrderReturn.getId())){
            returns = zkbPartOrderReturnRepository.findByPartOrderIdAndPartNo(selected.getId(),zkbPartOrderReturn.getPartNo());
        }else {
            returns = zkbPartOrderReturnRepository.findByPartOrderIdAndPartNoAndIdIsNot(selected.getId(),zkbPartOrderReturn.getPartNo(),zkbPartOrderReturn.getId());
        }
        if(returns.size() > 0){
            for(ZkbPartOrderReturn r : returns){
                returnQty += r.getQty();
            }
        }
        if(total < returnQty + zkbPartOrderReturn.getQty()){
            flag = false;
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

    public void naFlagListener(){
        zkbPartOrderReturn.setPartSerialNumber("");
    }

    public boolean iscanEditFlag() {
        return canEditFlag;
    }

    public void setcanEditFlag(boolean canEditFlag) {
        this.canEditFlag = canEditFlag;
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

    public List<I18nMessage> getReturnTypeList() {
        return returnTypeList;
    }

    public void setReturnTypeList(List<I18nMessage> returnTypeList) {
        this.returnTypeList = returnTypeList;
    }

    public String getShowBtn() {
        return showBtn;
    }

    public void setShowBtn(String showBtn) {
        this.showBtn = showBtn;
    }

    public String getValueTypeTips() {
        return valueTypeTips;
    }

    public void setValueTypeTips(String valueTypeTips) {
        this.valueTypeTips = valueTypeTips;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return zkbPartOrderService.getZkbPartOrderRepository();
    }
}
