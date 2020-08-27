package com.ge.apm.view.zkb;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.*;
import java.util.stream.Collectors;

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
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private AssetInfoRepository assetInfoRepository;
    private String actionName;
    private String actionItemName;
    private String selectionTenantInfo;
    private AssetInfo queryAsset;
    private V2_WorkOrder queryWorkFlow;
    private HashMap<String, String> selectedWorkFlow;
    private List<HashMap<String, String>> v2_listHashMap;
    private List<ZkbContract> contractList;
    private ZkbPartOrderItem zkbPartOrderItem;
    private String orderType;
    private String artificialType;
    private List<ZkbPartOrderCat> zkbPartOrderCatList ;
    private List<V2_BlobObject> v2_blobObjectList;
    private BlobFileService blobService;
    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList =new ArrayList<>();
    private AssetInfo assetInfo;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        selectionTenantInfo = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectionTenantInfo");
        queryWorkFlow = new V2_WorkOrder();
        queryWorkFlow.setAssetInfo(new AssetInfo());
        queryWorkFlow.setServiceRequest(new V2_ServiceRequest());
        if("Create".equals(actionName)){
            this.selected = new ZkbPartOrder();
            selected.setRequestDate(new Date());
            v2_blobObjectList = new ArrayList<>();
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
            v2_blobObjectList = m.get("v2_blobObjectList") == null ? new ArrayList<>() : (List<V2_BlobObject>)m.get("v2_blobObjectList");
        }
        initZknPartCat();
    }

    public void getQueryWorkFlowList(){
        if(StringUtils.isEmpty(orderType)){
            WebUtil.addErrorMessage("请输入订单类型","");
            return;
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(StringUtils.isEmpty(artificialType)){
                WebUtil.addErrorMessage("请输入机型","");
                return;
            }
        }
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
        contractList = new ArrayList<>();
    }

    public void workFlowSelected(SelectEvent event){

        if(v2_listHashMap ==null || v2_listHashMap.get(0) ==null || event.getObject() == null ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "未查询出合同数据！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        selectedWorkFlow = (HashMap<String, String>) event.getObject();
        assetInfo = assetInfoRepository.findByUid(String.valueOf(selectedWorkFlow.get("assetId")));
        selected.setProductNumber(assetInfo.getFunctionType());

        if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            if(assetInfo.getAssetGroup() == null || assetInfo.getAssetGroup() != 2){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "探头申请只能选择超声类设备！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            if(assetInfo.getAssetGroup() != null && (assetInfo.getAssetGroup() == 2 || assetInfo.getAssetGroup() == 1)){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "备件申请只能选择超声类或者放射类设备！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }

        contractList = zkbPartOrderService.queryContractByAsset(selectionTenantInfo,String.valueOf(selectedWorkFlow.get("assetId")));
        if(contractList.size() == 0){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "未查询出合同数据！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }else if(contractList.size() > 1){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同超过两条数据，请检查！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }else if(contractList.size() == 1){
            ZkbContract zkbContract = contractList.get(0);
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
                if(zkbContract.getSpareQuota() == null || zkbContract.getSpareQuota() <= 1){
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同备件数量小于等于1，请检查！", "");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            } else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                if(zkbContract.getSpareQuota() == null || zkbContract.getSpareQuota() <= 1){
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同探头数量小于等于1，请检查！", "");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            } else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
                if("1".equals(artificialType)){
                    if(zkbContract.getSpareArtificail() == null || zkbContract.getSpareArtificail() <= 1){
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "备件+人工+保养剩余名额小于等于1，请检查！", "");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                    }
                }else if("3".equals(artificialType)){
                    if(ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                        if(zkbContract.getSpareArtificail() == null || zkbContract.getSpareArtificail() <= 1){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "备件+人工+保养剩余名额小于等于1，请检查！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        }
                    }else if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())){
                        if(zkbContract.getGeArtificialSupportTimes() == null || zkbContract.getGeArtificialSupportTimes() <= 1){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "人工支持次数小于等于1，请检查！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        }
                    }
                }
            }

            if(selectedWorkFlow.get("customerName") != null){
                selected.setHospitalRepairer(selectedWorkFlow.get("customerName").toString());
            }else {
                selected.setHospitalRepairer("");
            }
            if(selectedWorkFlow.get("customerTel") != null){
                selected.setHospitalRepairerPhone(selectedWorkFlow.get("customerTel").toString());
            }else {
                selected.setHospitalRepairerPhone("");
            }
            if(selectedWorkFlow.get("hospitalName") != null){
                selected.setHospitalName(selectedWorkFlow.get("hospitalName").toString());
            }else {
                selected.setHospitalName("");
            }
            if(selectedWorkFlow.get("faultDescription") != null){
                selected.setFaultDescription(selectedWorkFlow.get("faultDescription").toString());
            }else {
                selected.setFaultDescription("");
            }
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetType("TechnicalPending","CS");
            }
        }
        calcRemainQty();

    }

    public void contractRowSelected(SelectEvent event){

    }

    private boolean showProblemOrderNumber = false;
    public void orderAnnotationChange(){
        if("NormalOrder".equals(selected.getOrderAnnotation())){
            showProblemOrderNumber = true;
        }else{
            showProblemOrderNumber = false;
        }
    }

    public void prepareItemCreate(){
        this.actionItemName = "Create";
        this.zkbPartOrderItem = new ZkbPartOrderItem();
    }

    public void prepareItemEdit(){
        this.actionItemName = "Edit";
    }

    public boolean isItemDisabled(){
        if(this.zkbPartOrderItem == null){
            return true;
        }else{
            return false;
        }
    }

    public void deleteItem(){
        this.selected.getZkbPartOrderItems().remove(this.zkbPartOrderItem);
        this.zkbPartOrderItem = null;
        reSortItem();
        this.selected.setQty(calcOrderQty());
        calcRemainQty();
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
        calcRemainQty();
    }

    public int calcOrderQty(){
        int rs = 0;
        for(ZkbPartOrderItem pi : this.selected.getZkbPartOrderItems()){
            rs += pi.getPartQty();
        }
        return rs;
    }

    public void calcRemainQty(){
        if(contractList != null && contractList.size() > 0){
            ZkbContract zkbContract = contractList.get(0);
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
                Integer contractCount = zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota();
                this.selected.setRemainQty(contractCount - this.selected.getQty());
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                this.selected.setRemainQty(contractCount - this.selected.getQty());
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
                if("1".equals(artificialType)){
                    Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }else if("3".equals(artificialType)){
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())){
                        Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                        this.selected.setRemainQty(contractCount - this.selected.getQty());
                    }else if(ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                        Integer contractCount = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                        this.selected.setRemainQty(contractCount - this.selected.getQty());
                    }
                }else {
                    Integer contractCount = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }
            }
        }
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
    public String saveEntity(){
        boolean flag = true;
        String errorLog = "";
        if(StringUtils.isEmpty(this.orderType)){
            flag=false;
            errorLog += "订单类型不能为空！";
        }
        if(v2_listHashMap == null || v2_listHashMap.size() == 0 ){
            flag=false;
            errorLog += "工单数据不能为空！";
        }
        if(contractList == null || contractList.size() == 0){
            flag=false;
            errorLog += "合同数据不能为空！";
        }else if(contractList.size() > 1){
            flag=false;
            errorLog += "合同数据大于一条！";
        }
        if((this.selected.getZkbPartOrderItems() ==null || this.selected.getZkbPartOrderItems().size() == 0) && "Parts".equals(orderType)){
            flag=false;
            errorLog += "额度申请明细不能为空！";
        }
        if(!ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup() == 2 && selected.getOnlineEngine() == null){
                flag=false;
                errorLog += "在线工程师不能为空！";
            }
        }
        if(!ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(this.v2_blobObjectList == null || this.v2_blobObjectList.size() == 0){
                //flag=false;
                errorLog += "附件不能为空！";
            }
        }
        if(!flag){
            WebUtil.addErrorMessage(errorLog);
            return "";
        }
        this.selected.setOrderType(orderType);
        if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            this.selected.setCatId(this.selected.getZkbPartOrderCat().getId());
            if(this.selected.getOnlineEngine() != null){
                this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getId());
            }
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            this.selected.setArtificialType(artificialType);
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
            if(this.selected.getOnlineEngine() != null){
                this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getId());
            }
        }
        HashMap m = v2_listHashMap.get(0);
        ZkbContract zkbContract = contractList.get(0);
        selected = zkbPartOrderService.create(m.get("id").toString(),m.get("assetId").toString(),zkbContract,this.selected,currentUser,selectionTenantInfo,m.get("srNum").toString());
        zkbPartOrderService.saveUploadFile(v2_blobObjectList,this.selected.getId());
        zkbPartOrderService.sendEmail(selected);
        zkbPartOrderService.sendMessage(selected);
        return "List?faces-redirect=true";
    }

    public void orderTypeChange(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if("2".equals(artificialType)){
                this.selected.setQty(0);
            }else {
                this.selected.setQty(1);
            }
        }else{
            this.selected.setQty(0);
            if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                this.selected.setCatNum(1);
            }
        }
        calcRemainQty();
    }

    public void zkbPartOrderCatSelect(){
        if(this.selected.getZkbPartOrderCat() != null){
            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                this.selected.setQty(1);
            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                this.selected.setQty(2);
            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                this.selected.setQty(2);
            }
        }
        calcRemainQty();
    }

    public void initZknPartCat(){
        zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat();
    }
    public List<ZkbPartOrderCat> autoCompleteCat(String query){
        List<ZkbPartOrderCat> res = zkbPartOrderCatList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getCatModel().contains(query)).collect(Collectors.toList());

        return res;
    }

    public List<ZkbPartOrderUserConfig> autoCompleteUserConfig(String query){
        List<ZkbPartOrderUserConfig> res = zkbPartOrderUserConfigList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
        return res;
    }

    public void removeFile(V2_BlobObject v2_blobObject) {
        v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());

    }

    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            //blobObject.setBoId(selected.getId());
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoUuid(selected.getId());
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public boolean isShowPartOrder(){
        if(StringUtils.isNotEmpty(orderType) && (contractList != null && contractList.size() == 1)){
            return true;
        }else{
            return false;
        }
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

    public HashMap<String, String> getSelectedWorkFlow() {
        return selectedWorkFlow;
    }

    public void setSelectedWorkFlow(HashMap<String, String> selectedWorkFlow) {
        this.selectedWorkFlow = selectedWorkFlow;
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

    public List<ZkbPartOrderCat> getZkbPartOrderCatList() {
        return zkbPartOrderCatList;
    }

    public void setZkbPartOrderCatList(List<ZkbPartOrderCat> zkbPartOrderCatList) {
        this.zkbPartOrderCatList = zkbPartOrderCatList;
    }

    public List<V2_BlobObject> getV2_blobObjectList() {
        return v2_blobObjectList;
    }

    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
        this.v2_blobObjectList = v2_blobObjectList;
    }

    public String getArtificialType() {
        return artificialType;
    }

    public void setArtificialType(String artificialType) {
        this.artificialType = artificialType;
    }

    public boolean isShowProblemOrderNumber() {
        return showProblemOrderNumber;
    }

    public void setShowProblemOrderNumber(boolean showProblemOrderNumber) {
        this.showProblemOrderNumber = showProblemOrderNumber;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public List<ZkbPartOrderUserConfig> getZkbPartOrderUserConfigList() {
        return zkbPartOrderUserConfigList;
    }

    public void setZkbPartOrderUserConfigList(List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList) {
        this.zkbPartOrderUserConfigList = zkbPartOrderUserConfigList;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }
}
