package com.ge.apm.view.zkb;

import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
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
    private UserAccount currentUser;
    private ZkbPartOrderService zkbPartOrderService;
    private ZkbPartOrderEditService zkbPartOrderEditService;
    private ZkbIntegralService zkbIntegralService;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private AssetInfoRepository assetInfoRepository;

    @Getter@Setter
    private String actionName;

    @Getter@Setter
    private String actionItemName;
    private String selectionTenantInfo;
    private AssetInfo queryAsset;
    private V2_WorkOrder queryWorkFlow;
    private HashMap<String, Object> selectedWorkFlow;
    private List<HashMap<String, Object>> v2_listHashMap;
    private List<ZkbContract> contractList;
    private ZkbContract selectedContract;
    private ZkbPartOrderItem zkbPartOrderItem;
    private List<ZkbPartOrderCat> zkbPartOrderCatList ;
    private List<V2_BlobObject> v2_blobObjectList;
    private List<V2_BlobObject> v2_probe_blobObjectList;
    private BlobFileService blobService;
    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();
    private AssetInfo assetInfo;
    private String valueTypeTips = "";
    private String saveTips = "";
    private String queryStatus;
    private Integer currentScore = 0;
    private String currentScoreText;
    private boolean currentScoreFlag =false;
    private boolean radiationQuotaTypeFlag = false;
    private boolean spareArtificialFlag = false;
    private List<HashMap<String, String>> highCatList = new ArrayList<>();
    private ZkbContractAssetRepository zkbContractAssetRepository;
    private String faultUnClosedSr;
    private List<I18nMessage> exchangeTypeList = new ArrayList<>();
    private List<I18nMessage> zkbPoProbeQuotaTypeList = new ArrayList<>();

    @Getter@Setter
    private List<I18nMessage> zkbPoPartQuotaTypeList = new ArrayList<>();
    private I18nMessageRepository i18nMessageRepository;
    private String notGeAsset;
    private String listNg;
    String encodeStr;
    private boolean disableNotGeAsset = false;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        zkbPartOrderEditService = WebUtil.getBean(ZkbPartOrderEditService.class);
        zkbIntegralService = WebUtil.getBean(ZkbIntegralService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        zkbContractAssetRepository = WebUtil.getBean(ZkbContractAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        selectionTenantInfo = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectionTenantInfo");
        queryStatus = UrlEncryptController.getValueFromMap(encodeStr, "queryStatus") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        listNg = UrlEncryptController.getValueFromMap(encodeStr, "notGeAsset") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "notGeAsset");
        notGeAsset = WebUtil.getRequestParameter("ng");
        queryWorkFlow = new V2_WorkOrder();
        queryWorkFlow.setAssetInfo(new AssetInfo());
        queryWorkFlow.setServiceRequest(new V2_ServiceRequest());
        if("Create".equals(actionName)){
            this.selected = new ZkbPartOrder();
            selected.setScoreFlag(null);
            selected.setRequestDate(new Date());
            selected.setRequestUser(currentUser.getName());
            selected.setRequestPhone(currentUser.getTelephone());
            selected.setSupplementFlag(ZkbConstantUtil.F);
            v2_blobObjectList = new ArrayList<>();
            v2_probe_blobObjectList = new ArrayList<>();
            Integer c = zkbPartOrderService.getZkbContractRepository().fetchContractCount(selectionTenantInfo);
            if(c == null || c == 0){
                notGeAsset = ZkbConstantUtil.F;
                disableNotGeAsset = true;
            }
        } else if("Copy".equals(actionName)){
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            selected =  m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            copyZkbPartOrder();
            selectedContract = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            if(selectedContract != null){
                contractList = new ArrayList<>();
                contractList.add(selectedContract);
            }
            HashMap<String, Object> mm = m.get("v2") == null ? null : (HashMap<String, Object>)m.get("v2");
            v2_listHashMap = new ArrayList<>();
            if(mm != null){
                v2_listHashMap.add(mm);
                selectedWorkFlow = mm;
                //assetInfo = assetInfoRepository.findByUid(String.valueOf(selectedWorkFlow.get("assetId")));
                assetInfo = new AssetInfo();
            }
            v2_blobObjectList = m.get("v2_blobObjectList") == null ? new ArrayList<>() : (List<V2_BlobObject>)m.get("v2_blobObjectList");
            v2_probe_blobObjectList = m.get("v2_probe_blobObjectList") == null ? new ArrayList<>() : (List<V2_BlobObject>)m.get("v2_probe_blobObjectList");
            if (selectedContract != null) {
                initZknPartCat(selectedContract.getStartDate());
            }
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndDeletedFlagIsNull("TechnicalPending",ZkbPartOrder.CONFIG_US);
            }
            calcRemainQty();
            orderAnnotationChange();
            if(zkbPartOrderService.checkContractLastDay(selectedContract,selected.getOrderType())){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "请电话联络400先开出工单，避免由于次日审批结束无法订购备件！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                FacesMessage message2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "电话：400-812-8188。", "");
                FacesContext.getCurrentInstance().addMessage(null, message2);
            }
            Integer c = zkbPartOrderService.getZkbContractRepository().fetchContractCount(selectionTenantInfo);
            if(c == null || c == 0){
                notGeAsset = ZkbConstantUtil.F;
                disableNotGeAsset = true;
            }
        }
    }

    public void copyZkbPartOrder(){
       zkbPartOrderEditService.copyZkbPartOrder(selected, currentUser);
    }

    public void initHighList(String contractId){
        highCatList = zkbPartOrderService.queryHighCatByContract(contractId,false);
    }

    public void getQueryWorkFlowList(){
        v2_listHashMap = new ArrayList<>();
        String errMsg = "";
        if(StrUtil.isEmpty(notGeAsset)){
            errMsg += "请输入GE设备或非GE设备!\n";
        }
        if(StringUtils.isEmpty(selected.getOrderType())){
            errMsg += "请输入订单类型!\n";
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(StringUtils.isEmpty(selected.getArtificialType())){
                errMsg += "请输入机型!";
            }
        }

        if(StrUtil.isNotEmpty(errMsg)){
            WebUtil.addErrorMessage(errMsg,"");
            return;
        }
        if(queryWorkFlow == null){
            WebUtil.addErrorMessage("查询有误，请输入条件","");
            return;
        }
        if((queryWorkFlow.getAssetInfo() == null || StringUtils.isEmpty(queryWorkFlow.getAssetInfo().getSystemId())) &&
            (queryWorkFlow.getAssetInfo() == null || StringUtils.isEmpty(queryWorkFlow.getAssetInfo().getDepartNum())) &&
           (queryWorkFlow.getServiceRequest() == null || StringUtils.isEmpty(queryWorkFlow.getServiceRequest().getSrNum()))) {
            WebUtil.addErrorMessage("查询有误，请输入条件","");
            return;
        }
        v2_listHashMap = zkbPartOrderService.getQueryWorkFlowList(queryWorkFlow,selectionTenantInfo);
        contractList = new ArrayList<>();
    }

    public void workFlowSelected(SelectEvent event){
        if(v2_listHashMap ==null || v2_listHashMap.size() == 0 || v2_listHashMap.get(0) ==null || event.getObject() == null ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "未查询出合同数据！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        selectedWorkFlow = (HashMap<String, Object>) event.getObject();
        assetInfo = assetInfoRepository.findByUid(String.valueOf(selectedWorkFlow.get("assetId")));
        selected.setProductNumber(assetInfo.getFunctionType());
        //验证设备状态
        if (StringUtils.isNotBlank(zkbPartOrderService.checkAsset(assetInfo))) {
            WebUtil.addErrorMessage(zkbPartOrderService.checkAsset(assetInfo),"");
            return;
        }

        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(assetInfo.getAssetGroup() == null || assetInfo.getAssetGroup() != 2){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "探头申请只能选择超声类设备！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(assetInfo.getAssetGroup() != null && !(assetInfo.getAssetGroup() == 2 || assetInfo.getAssetGroup() == 1)){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "备件申请只能选择超声类或者放射类设备！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }

        contractList = zkbPartOrderService.queryContractByAsset(selectionTenantInfo,String.valueOf(selectedWorkFlow.get("assetId")),(Date)selectedWorkFlow.get("requestTime"));
        String resultString  = zkbPartOrderEditService.workFlowCheck(selected, contractList, assetInfo,notGeAsset, selected.getOrderType());
        if(StrUtil.isNotEmpty(resultString)){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resultString, "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            contractList = new ArrayList<>();
            return;
        }
        selectedContract = contractList.get(0);
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(), assetInfo.getUid());
        zkbPartOrderUserConfigList = zkbPartOrderEditService.queryUserConfig(selected.getOrderType(), currentUser,notGeAsset);
        zkbPartOrderEditService.setZkbPartOrderByWorkFlowSelected(selected, selectedWorkFlow, selected.getOrderType(), assetInfo, zkbContractAsset, notGeAsset, selectedContract, currentUser);

        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            initHighList(selectedContract.getId());
            zkbPoProbeQuotaTypeList = i18nMessageRepository.findI18nByType("ZkbPoProbeQuotaType");
            if(ZkbConstantUtil.T.equals(notGeAsset)){
                if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
                    zkbPartOrderCatSelect();//非GE设备是否高值将变为不可编辑，所以需要手动触发一次选择事件获取订单名额
                }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){
                    for(I18nMessage i : zkbPoProbeQuotaTypeList){
                        if(ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(i.getMsgKey())){
                            zkbPoProbeQuotaTypeList.remove(i);
                            break;
                        }
                    }
                }
            }
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            //editCurrentScoreFlag();
            handleQtyByOrderType();
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            initRadiationQuotaTypeList();
        }

        initZknPartCat(selectedContract.getStartDate());
        if(zkbPartOrderService.checkContractLastDay(selectedContract,selected.getOrderType())){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "请电话联络400先开出工单，避免由于次日审批结束无法订购备件!", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            FacesMessage message2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "电话：400-812-8188。", "");
            FacesContext.getCurrentInstance().addMessage(null, message2);
        }
        calcRemainQty();
    }

    public void initRadiationQuotaTypeList() {
        //重新初始化
        //radiationQuotaTypeFlag = true;
        spareArtificialFlag = false;
        selected.setRadiationQuotaType(null);
        List<I18nMessage> removeList = new ArrayList<>();
        zkbPoPartQuotaTypeList = i18nMessageRepository.findI18nByType("ZkbPoPartQuotaType");
        if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
            spareArtificialFlag = true;
            selected.setRadiationQuotaType(ZkbPartOrder.RadiationQuotaTypeEnum.Part.toString());
        }else if("PCB MVS".equals(selectedContract.getContractType())){
            spareArtificialFlag = true;
            selected.setRadiationQuotaType(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString());
        }else if(ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())){
            if(assetInfo.getAssetGroup().equals(1)){
                if(zkbPartOrderEditService.checkPcbMvsTotal(selectedContract)){
                    selected.setRadiationQuotaType(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString());
                    spareArtificialFlag = true;
                }else{
                    for(I18nMessage i18nMessage : zkbPoPartQuotaTypeList){
                        if(i18nMessage.getMsgKey().equals("Part") || i18nMessage.getMsgKey().equals("Consumable")){
                            removeList.add(i18nMessage);
                        }
                    }
                    int partHighQuota = selectedContract.getPartHighQuota() == null ? 0 : selectedContract.getPartHighQuota();
                    if(partHighQuota<= 0){
                        selected.setRadiationQuotaType(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString());
                        spareArtificialFlag = true;
                    }
                }
            }else{
                spareArtificialFlag = true;
                selected.setRadiationQuotaType(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString());
            }
        }else if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selectedContract.getContractType())){
            int partHighQuota = selectedContract.getPartHighQuota() == null ? 0 : selectedContract.getPartHighQuota();
            int consumableQuota = selectedContract.getConsumableQuota() == null ? 0 : selectedContract.getConsumableQuota();

            for(I18nMessage i18nMessage : zkbPoPartQuotaTypeList){
                if(i18nMessage.getMsgKey().equals("Part")){
                    removeList.add(i18nMessage);
                }
                if(consumableQuota <= 0){
                    if(i18nMessage.getMsgKey().equals("Consumable")){
                        removeList.add(i18nMessage);
                    }
                }
                if(!assetInfo.getAssetGroup().equals(1)){
                    if(i18nMessage.getMsgKey().equals(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString())){
                        removeList.add(i18nMessage);
                    }
                }
            }
        }

        if(CollectionUtils.isNotEmpty(removeList)){
            zkbPoPartQuotaTypeList.removeAll(removeList);
        }
        radiationQuotaTypeFlag = zkbPoPartQuotaTypeList.stream().map(I18nMessage::getMsgKey).filter(t -> t.equals(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString())).findAny().isPresent();

    }

    public boolean highCatFlagDisabledCheck(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbConstantUtil.T.equals(notGeAsset)){
                if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
                    return true;
                }
            }
        }
        return false;
    }

    public void handleQtyByOrderType(){
        zkbPartOrderEditService.handleQtyByOrderType(selected, selectedContract, notGeAsset, assetInfo);
    }

    public void editCurrentScoreFlag(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.CCSB.toString().equals(selectedContract.getContractType())){
                currentScoreFlag = true;
                return;
            }
        }
        //积分不够不能申请
        currentScore = zkbIntegralService.queryScoreFlag(selectionTenantInfo);
        currentScoreText = "当前积分："+ currentScore;
        if(currentScore < ZkbIntegralService.unitScore){
            currentScoreFlag = true;
        }else{
            currentScoreFlag = false;
        }

        //校验
        if(!currentScoreFlag){
            boolean r = zkbPartOrderService.checkScoreFlagValid(selected,selectedContract);
            currentScoreFlag = !r;
        }

        if(zkbPartOrderEditService.checkPcbMvsTotal(selectedContract)){
            if(!currentScoreFlag){
                selected.setScoreFlag(ZkbConstantUtil.T);
                scoreFlagChange();
                currentScoreText += "；该合同只支持积分申请,请检查！";
            }else{
                currentScoreText += "；该合同只支持积分申请,请检查！";
            }
        }
    }

    private boolean showProblemOrderNumber = false;
    public void orderAnnotationChange(){
        if("NormalOrder".equals(selected.getOrderAnnotation())){
            showProblemOrderNumber = true;
        }else{
            showProblemOrderNumber = false;
        }
    }

    public String vacationRender(String isVacation){
        if(StringUtils.isEmpty(isVacation)){
            return "正常上班中";
        }else if(ZkbConstantUtil.F.equals(isVacation)){
            return "正常上班中";
        }else if(ZkbConstantUtil.T.equals(isVacation)){
            return "休假中";
        }
        return "";
    }

    //工作站条件：人工申请且机型为放射类设备时才显示
    public boolean workStationRender(){
        /*if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(CopyByInvok.NORMAL_ONE.equals(selected.getArtificialType())){
                return true;
            }
        }*/
        return false;
    }

    public boolean scoreFlagRender(){
        /*if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(CopyByInvok.T.equals(selected.getMaintainFlag())){
                return true;
            }else{
                if(!CopyByInvok.NORMAL_TWO.equals(selected.getArtificialType())){
                    return true;
                }
                if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) && "t".equals(selectedContract.getFirstYearFlag())){

                }else{
                    return true;
                }
            }
        }*/
        return false;
    }

    public void prepareItemCreate(){
        this.actionItemName = "Create";
        this.zkbPartOrderItem = new ZkbPartOrderItem();
        this.itemSaveTips = "";
    }

    public void prepareItemEdit(){
        this.actionItemName = "Edit";
        this.itemSaveTips = "";
    }

    public boolean isItemDisabled(){
        if(this.zkbPartOrderItem == null){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkPcbMvsTotal() {
        return zkbPartOrderEditService.checkPcbMvsTotal(selectedContract);
    }

    public boolean isItemCreateDisabled(){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())) {
                if (assetInfo.getAssetGroup().equals(1)) {
                    return StrUtil.isEmpty(selected.getRadiationQuotaType());
                }
            }
        }

        return false;
    }

    public void deleteItem(){
        this.selected.getZkbPartOrderItems().remove(this.zkbPartOrderItem);
        this.zkbPartOrderItem = null;
        reSortItem();
        this.selected.setQty(calcOrderQty());
        calcRemainQty();
    }

    private String itemSaveTips = "";
    public void saveItem(){
        zkbPartOrderItem.setPartNo(zkbPartOrderItem.getPartNo().trim());

        String rsLog = zkbPartOrderEditService.checkSaveItem(selected, selectedContract,assetInfo, zkbPartOrderItem);
        if(StrUtil.isNotEmpty(rsLog)){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            itemSaveTips = rsLog;
            return;
        }else{
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }
        if("Create".equals(this.actionItemName)){
            if(this.selected == null){
                this.selected = new ZkbPartOrder();
                selected.setRequestDate(new Date());
                selected.setRequestUser(currentUser.getName());
                selected.setRequestPhone(currentUser.getTelephone());
            }
            if(this.selected.getZkbPartOrderItems() ==null){
                this.selected.setZkbPartOrderItems(new ArrayList<>());
            }
            int i = this.selected.getZkbPartOrderItems().size();
            this.zkbPartOrderItem.setIndex(i);
            this.zkbPartOrderItem.setPartQty(zkbPartOrderItem.getPrePartQty());
            this.selected.getZkbPartOrderItems().add(zkbPartOrderItem);
        }else{
            this.selected.getZkbPartOrderItems().set(this.zkbPartOrderItem.getIndex(),this.zkbPartOrderItem);
            this.zkbPartOrderItem.setPartQty(zkbPartOrderItem.getPrePartQty());
        }
        this.zkbPartOrderItem = null;
        this.itemSaveTips = "";
        this.selected.setQty(calcOrderQty());
        calcRemainQty();
        saveTips = "备件申请信息已添加，提交申请后流程才会进入审批阶段，请在添加完需要审批的备件信息后提交申请，以免影响您的申请进度，感谢您的配合！";
    }

    public int calcOrderQty(){
        int rs = 0;
        for(ZkbPartOrderItem pi : this.selected.getZkbPartOrderItems()){
            rs += pi.getPartQty();
        }
        return rs;
    }

    public void calcRemainQty(){
        if(selectedContract != null){
            if(this.selected.getQty() == null){
                this.selected.setRemainQty(null);
                return;
            }
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                    Integer contractCount = selectedContract.getPartHighQuota() == null ? 0 : selectedContract.getPartHighQuota();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                    Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Part.toString().equals(selected.getRadiationQuotaType())){
                    Integer contractCount = selectedContract.getSpareQuota() == null ? 0 : selectedContract.getSpareQuota();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(selected.getRadiationQuotaType())){
                    Integer contractCount = selectedContract.getConsumableQuota() == null ? 0 : selectedContract.getConsumableQuota();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                if(StringUtils.isNotEmpty(selectedContract.getQuotaDeductType())){
                    if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){
                        if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                            Integer contractCount = selectedContract.getProbeQuota() == null ? 0 : selectedContract.getProbeQuota();
                            this.selected.setRemainQty(contractCount - this.selected.getQty());
                        }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                            Integer contractCount = selectedContract.getProbeQuotaNormal() == null ? 0 : selectedContract.getProbeQuotaNormal();
                            this.selected.setRemainQty(contractCount - this.selected.getQty());
                        }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                            Integer contractCount = selectedContract.getProbeQuotaSpecial() == null ? 0 : selectedContract.getProbeQuotaSpecial();
                            this.selected.setRemainQty(contractCount - this.selected.getQty());
                        }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                            if(ZkbConstantUtil.FOUR_TO_ONE.equals(selected.getContractCatId())){
                                Integer contractCount = selectedContract.getProbeQuotaNormal() == null ? 0 : selectedContract.getProbeQuotaNormal();
                                this.selected.setRemainQty(contractCount - this.selected.getQty());
                            }else {
                                this.selected.setRemainQty(0);
                            }
                        }
                    }else if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
                        if(StringUtils.isEmpty(selected.getHighCatFlag()) || ZkbConstantUtil.F.equals(selected.getHighCatFlag())){
                            Integer contractCount = selectedContract.getProbeQuota() == null ? 0 : selectedContract.getProbeQuota();
                            this.selected.setRemainQty(contractCount - this.selected.getQty());
                        }else{
                            if(ZkbConstantUtil.FOUR_TO_ONE.equals(selected.getContractCatId())){
                                Integer contractCount = selectedContract.getProbeQuota() == null ? 0 : selectedContract.getProbeQuota();
                                this.selected.setRemainQty(contractCount - this.selected.getQty());
                            }else{
                                this.selected.setRemainQty(0);
                            }
                        }
                    }
                }
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
                if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())) {
                    if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){
                        this.selected.setRemainQty(0);
                    }else {
                        Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(), assetInfo.getUid());
                        if(ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                            if(ZkbConstantUtil.F.equals(selected.getMaintainFirstFlag())){
                                contractCount -= selected.getQty();
                            }
                        }else {
                            contractCount -= selected.getQty();
                        }
                        this.selected.setRemainQty(contractCount);
                    }
                }else {
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
                    if(ZkbConstantUtil.NORMAL_TWO.equals(selected.getArtificialType())) {
                        if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                            Integer contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                            this.selected.setRemainQty(contractCount - 1);
                        }else{
                            Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                            this.selected.setRemainQty(contractCount);
                        }
                    }else if(ZkbContract.ContractType.CCSB.toString().equals(selectedContract.getContractType())){
                        if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(selected.getCcsbApplyType())){
                            Integer remindQty = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                            if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){
                                if(remindQty >= 1){
                                    this.selected.setRemainQty(remindQty - 1);
                                }else{
                                    this.selected.setRemainQty(0);
                                }
                            }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                                this.selected.setRemainQty(remindQty);
                            }
                        }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(selected.getCcsbApplyType())){
                            int remindQty = selectedContract.getCcsbMaintenanceQuota() == null ? 0 : selectedContract.getCcsbMaintenanceQuota();
                            if(remindQty >= 1){
                                this.selected.setRemainQty(remindQty - 1);
                            }else{
                                this.selected.setRemainQty(0);
                            }
                        }else{
                            this.selected.setRemainQty(0);
                        }
                    }else if((ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selectedContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                        Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                        this.selected.setRemainQty(contractCount);
                    }else if(ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){
                        if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                            Integer contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                            this.selected.setRemainQty(contractCount);
                        }else{
                            Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                            this.selected.setRemainQty(contractCount);
                        }
                    }else if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){
                        this.selected.setRemainQty(0);
                    }else {
                        Integer contractCount = 0;
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){
                            if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                                contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                                contractCount = contractCount - this.selected.getQty();
                            }else {
                                contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                                contractCount = contractCount - this.selected.getQty();
                            }
                        }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                            if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())) {
                                contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                            }else{
                                contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                                contractCount = contractCount - this.selected.getQty();
                            }
                        }
                        this.selected.setRemainQty(contractCount);
                    }
                }
            }else if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                    Integer contractCount = selectedContract.getSpareQuota() == null ? 0 : selectedContract.getSpareQuota();
                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                }else{
                    Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
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

    public String quotaTips ="";
    public void preSave() {
        if(assetInfo == null){
            return;
        }
        selected.setFaultUnClosedSr(faultUnClosedSr);
        if(StrUtil.isNotEmpty(notGeAsset)){
            if(ZkbConstantUtil.T.equals(notGeAsset)){
                selected.setNotGeAsset(ZkbConstantUtil.T);
            }else{
                selected.setNotGeAsset(null);
            }
        }
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbConstantUtil.F.equals(notGeAsset)){
                boolean onlineEngineFlag= false;
                if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType()) && !ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                    onlineEngineFlag = true;
                }
                if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType()) && ZkbConstantUtil.F.equals(selected.getHighCatFlag())){
                    onlineEngineFlag = true;
                }
                if(onlineEngineFlag){
                    this.selected.setCatId(this.selected.getZkbPartOrderCat().getId());
                }
            }
            if(this.selected.getOnlineEngine() != null){
                if("t".equals(this.selected.getOnlineEngine().getIsVacation())){
                    if(StrUtil.isNotEmpty(this.selected.getOnlineEngine().getUsBackUpId())){
                        ZkbPartOrderUserConfig backUp = zkbPartOrderUserConfigRepository.findById(this.selected.getOnlineEngine().getUsBackUpId());
                        this.selected.setOnlineEngine(backUp);
                        this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getUsBackUpId());
                    }else{
                        this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getId());
                    }
                }else{
                    this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getId());
                }
            }
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
                if("t".equals(zkbContractAsset.getSupportMaintainFlag())){
                    boolean b = zkbPartOrderService.checkDeductMaintain(zkbContractAsset,(Date)selectedWorkFlow.get("requestTime"));
                    if(b){
                        this.selected.setMaintainFirstFlag(ZkbConstantUtil.F);
                    }else{
                        this.selected.setMaintainFirstFlag(ZkbConstantUtil.T);
                    }
                }

            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(this.selected.getOnlineEngine() != null){
                if(ZkbConstantUtil.T.equals(this.selected.getOnlineEngine().getIsVacation())){
                    ZkbPartOrderUserConfig backUp = zkbPartOrderUserConfigRepository.findById(this.selected.getOnlineEngine().getUsBackUpId());
                    this.selected.setOnlineEngine(backUp);
                    this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getUsBackUpId());
                }else{
                    this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getId());
                }
            }
        }

        String errorLog = zkbPartOrderEditService.saveCheck(selected, selectedContract, faultUnClosedSr, v2_listHashMap, contractList, assetInfo, v2_blobObjectList, v2_probe_blobObjectList, selectionTenantInfo);
        if(StrUtil.isNotEmpty(errorLog)){
            WebUtil.addErrorMessage(errorLog);
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return ;
        }

        //region 实时获取合同进行校验数量
        ZkbContract zkbContract = zkbPartOrderService.getZkbContractRepository().findById(selectedContract.getId());
        String reLog = zkbPartOrderEditService.checkCurrentContractQuota(selected, zkbContract, assetInfo, notGeAsset);
        if(StringUtils.isNotEmpty(reLog.toString())){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, reLog.toString(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            contractList = new ArrayList<>();
            contractList.add(zkbContract);
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return ;
        }
        //endregion
        quotaTips = zkbPartOrderEditService.preSave(selected,selectedContract,assetInfo);

    }


    /**
     * 保存方法
     */
    public String saveData(){
        ZkbContract zkbContract = zkbPartOrderService.getZkbContractRepository().findById(selectedContract.getId());
        if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
            selected.setDataType(ZkbPartOrder.DATA_TYPE_TEST);
        }
        selected.setDataFlag(0);

        HashMap m = selectedWorkFlow;
        selected = zkbPartOrderService.create(m.get("id").toString(),m.get("assetId").toString(),zkbContract,this.selected,currentUser,selectionTenantInfo,m.get("srNum").toString());
        zkbPartOrderService.saveUploadFile(v2_blobObjectList,this.selected.getId());
        if(v2_probe_blobObjectList != null && v2_probe_blobObjectList.size() > 0){
            zkbPartOrderService.saveUploadFile(v2_probe_blobObjectList,this.selected.getId());
        }
        zkbPartOrderService.sendEmail(selected,"",UserContextService.getAccessToken());
        zkbPartOrderService.sendMessage(selected,UserContextService.getAccessToken());
        if(zkbPartOrderService.checkContractLastDay(zkbContract,selected.getOrderType())){
            zkbPartOrderService.sendEmail(selected, ZkbConstantUtil.EMAIL_URL_TYPE_LASTDAY,UserContextService.getAccessToken());
        }
        return "List?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(listNg) ? "" : listNg);
    }

    public void notGeAssetChange(ValueChangeEvent event){
        String v = event.getNewValue() == null ? "" : (String)event.getNewValue();
        String url = "Edit.xhtml?str="+ encodeStr+"&ng="+v;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void orderTypeChange(ValueChangeEvent event){
        v2_listHashMap = new ArrayList<>();
        contractList = new ArrayList<>();
        assetInfo  = new AssetInfo();
        selectedContract = new ZkbContract();

        String orderType = StrUtil.isEmptyIfStr(event.getNewValue()) ? "" : event.getNewValue().toString();
        zkbPartOrderEditService.orderTypeChange(selected, orderType,currentUser);
        queryWorkFlow = new V2_WorkOrder();
        queryWorkFlow.setAssetInfo(new AssetInfo());
        queryWorkFlow.setServiceRequest(new V2_ServiceRequest());
    }

    public void supplementFlagChange(){
        if(ZkbConstantUtil.F.equals(selected.getSupplementFlag())){
            selected.setSrNo("");
        }
    }

    public void scoreFlagChange(){
        if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){
            selected.setRemainQty(0);
            exchangeTypeList = i18nMessageRepository.findI18nByType("zkbExchangeType");
            if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                for(I18nMessage i : exchangeTypeList){
                    if("artificial".equals(i.getMsgKey())){
                        exchangeTypeList.remove(i);
                        break;
                    }
                }
            }
        }else{
            exchangeTypeList = new ArrayList<>();
            if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                Integer contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                this.selected.setRemainQty(contractCount - this.selected.getQty());
            }else{
                Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                this.selected.setRemainQty(contractCount - this.selected.getQty());
            }
        }
    }

    public void maintainFlagChange(){
       /* if(ZkbContract.ContractType.CCS.name().equals(selectedContract.getContractType()) ||
                ZkbContract.ContractType.Biomed.name().equals(selectedContract.getContractType())){
            if(CopyByInvok.T.equals(selected.getMaintainFlag())){
                selected.setScoreFlag(CopyByInvok.T);
                currentScoreFlag = true;
            }else {
                currentScoreFlag = false;
            }
            scoreFlagChange();
        }*/
    }

    public void ssaKeyFlagChange(){
        calcRemainQty();
       /* if(CopyByInvok.T.equals(selected.getSsaKeyFlag())){
            currentScoreFlag = true;
            currentScoreText = "";
            selected.setScoreFlag(CopyByInvok.F);
            selected.setExchangeType(null);
            return;
        }else{
            currentScoreFlag = false;
            editCurrentScoreFlag();
        }*/
    }

    public void tenantNewCatFlagChange(){
        if(this.selected.getQty() == null){
            return;
        }
        if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
            if(ZkbConstantUtil.T.equals(selected.getTenantNewCatFlag())){
                this.selected.setQty(selected.getQty() * 2);
            }else if(ZkbConstantUtil.F.equals(selected.getTenantNewCatFlag())){
                this.selected.setQty(selected.getQty() / 2);
            }
        }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){
            //新逻辑中如果为全新探头，且是否可以全新合同选择否时名额翻倍
            if(ZkbConstantUtil.T.equals(selected.getTenantNewCatFlag())){
                if(StrUtil.isEmpty(selected.getProvideNewContract())){
                    this.selected.setProvideNewContract(ZkbConstantUtil.T);
                }else if(ZkbConstantUtil.F.equals(selected.getProvideNewContract())){
                    this.selected.setQty(selected.getQty() * 2);
                }else if(ZkbConstantUtil.T.equals(selected.getProvideNewContract())){
                    this.selected.setQty(selected.getQty() / 2);
                }
            }else if(ZkbConstantUtil.F.equals(selected.getTenantNewCatFlag())){
                //如果全新探头由是-->否，清除【是否可以提供全新合同】值，如果【是否可以提供全新合同】为否即名额翻倍还需要将名额去除翻倍
                if(ZkbConstantUtil.F.equals(selected.getProvideNewContract())){
                    this.selected.setQty(selected.getQty() / 2);
                }
                this.selected.setProvideNewContract(null);
            }
        }else{
            //扣除逻辑为空将数量设置为null
            this.selected.setQty(null);
        }
        calcRemainQty();
    }

    public void provideNewContractChange(){
        if(this.selected.getQty() == null){
            return;
        }
        zkbPartOrderEditService.provideNewContractChange(selected);
        calcRemainQty();
    }

    public void zkbPartOrderCatSelect(){
        valueTypeTips = zkbPartOrderEditService.catSelected(selected, notGeAsset, selectedContract);
        calcRemainQty();
    }

    public void workStationFlagSelect(){
        if(ZkbConstantUtil.T.equals(selected.getWorkStationFlag())){
            selected.setProductNumber("AW");
        }else {
            selected.setProductNumber(assetInfo.getFunctionType());
        }
    }

    public void ccsbApplyTypeSelect(){
        calcRemainQty();
    }
    public void initZknPartCat(Date contractDate){
        zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(contractDate);
    }
    public List<ZkbPartOrderCat> autoCompleteCat(String query){
        List<ZkbPartOrderCat> res = zkbPartOrderCatList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getCatModel().contains(query)).collect(Collectors.toList());

        return res;
    }

    public List<ZkbPartOrderUserConfig> autoCompleteUserConfig(String query){
        List<ZkbPartOrderUserConfig> res = zkbPartOrderUserConfigList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
        return res;
    }

    public void removeFile(V2_BlobObject v2_blobObject,String type) {
        if(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER.equals(type)){
            v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
        }else if(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_PROVE.equals(type)){
            v2_probe_blobObjectList = v2_probe_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
        }
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
    public void handleProveFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_PROVE);
            v2_blobObject.setBoUuid(selected.getId());
            v2_probe_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public boolean isShowPartOrder(){
        if(StringUtils.isNotEmpty(selected.getOrderType()) && (contractList != null && contractList.size() == 1)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isShowRegionalManager(){
        boolean isShowRegionalManager = true;
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())) {
            if("2".equals(selected.getArtificialType())){
                isShowRegionalManager = false;
            }
        }
        return isShowRegionalManager;
    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(listNg) ? "" : listNg);
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

    public HashMap<String, Object> getSelectedWorkFlow() {
        return selectedWorkFlow;
    }

    public void setSelectedWorkFlow(HashMap<String, Object> selectedWorkFlow) {
        this.selectedWorkFlow = selectedWorkFlow;
    }

    public List<HashMap<String, Object>> getV2_listHashMap() {
        return v2_listHashMap;
    }

    public void setV2_listHashMap(List<HashMap<String, Object>> v2_listHashMap) {
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

    public String getValueTypeTips() {
        return valueTypeTips;
    }

    public void setValueTypeTips(String valueTypeTips) {
        this.valueTypeTips = valueTypeTips;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public String getCurrentScoreText() {
        return currentScoreText;
    }

    public void setCurrentScoreText(String currentScoreText) {
        this.currentScoreText = currentScoreText;
    }

    public boolean isCurrentScoreFlag() {
        return currentScoreFlag;
    }

    public void setCurrentScoreFlag(boolean currentScoreFlag) {
        this.currentScoreFlag = currentScoreFlag;
    }

    public String getSaveTips() {
        return saveTips;
    }

    public void setSaveTips(String saveTips) {
        this.saveTips = saveTips;
    }

    public List<HashMap<String, String>> getHighCatList() {
        return highCatList;
    }

    public void setHighCatList(List<HashMap<String, String>> highCatList) {
        this.highCatList = highCatList;
    }

    public List<V2_BlobObject> getV2_probe_blobObjectList() {
        return v2_probe_blobObjectList;
    }

    public void setV2_probe_blobObjectList(List<V2_BlobObject> v2_probe_blobObjectList) {
        this.v2_probe_blobObjectList = v2_probe_blobObjectList;
    }

    public ZkbContract getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(ZkbContract selectedContract) {
        this.selectedContract = selectedContract;
    }

    public String getQuotaTips() {
        return quotaTips;
    }

    public void setQuotaTips(String quotaTips) {
        this.quotaTips = quotaTips;
    }

    public List<I18nMessage> getExchangeTypeList() {
        return exchangeTypeList;
    }

    public void setExchangeTypeList(List<I18nMessage> exchangeTypeList) {
        this.exchangeTypeList = exchangeTypeList;
    }

    public boolean isRadiationQuotaTypeFlag() {
        return radiationQuotaTypeFlag;
    }

    public void setRadiationQuotaTypeFlag(boolean radiationQuotaTypeFlag) {
        this.radiationQuotaTypeFlag = radiationQuotaTypeFlag;
    }

    public boolean isSpareArtificialFlag() {
        return spareArtificialFlag;
    }

    public void setSpareArtificialFlag(boolean spareArtificialFlag) {
        this.spareArtificialFlag = spareArtificialFlag;
    }

    public String getItemSaveTips() {
        return itemSaveTips;
    }

    public void setItemSaveTips(String itemSaveTips) {
        this.itemSaveTips = itemSaveTips;
    }

    public String getNotGeAsset() {
        return notGeAsset;
    }

    public void setNotGeAsset(String notGeAsset) {
        this.notGeAsset = notGeAsset;
    }

    public boolean isDisableNotGeAsset() {
        return disableNotGeAsset;
    }

    public void setDisableNotGeAsset(boolean disableNotGeAsset) {
        this.disableNotGeAsset = disableNotGeAsset;
    }

    public List<I18nMessage> getZkbPoProbeQuotaTypeList() {
        return zkbPoProbeQuotaTypeList;
    }

    public void setZkbPoProbeQuotaTypeList(List<I18nMessage> zkbPoProbeQuotaTypeList) {
        this.zkbPoProbeQuotaTypeList = zkbPoProbeQuotaTypeList;
    }

    public String getFaultUnClosedSr() {
        return faultUnClosedSr;
    }

    public void setFaultUnClosedSr(String faultUnClosedSr) {
        this.faultUnClosedSr = faultUnClosedSr;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }
}
