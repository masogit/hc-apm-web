package com.ge.apm.view.zkb;

import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
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
import java.text.ParseException;
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
    private ZkbIntegralService zkbIntegralService;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private AssetInfoRepository assetInfoRepository;
    private String actionName;
    private String actionItemName;
    private String selectionTenantInfo;
    private AssetInfo queryAsset;
    private V2_WorkOrder queryWorkFlow;
    private HashMap<String, Object> selectedWorkFlow;
    private List<HashMap<String, Object>> v2_listHashMap;
    private List<ZkbContract> contractList;
    private ZkbContract selectedContract;
    private ZkbPartOrderItem zkbPartOrderItem;
    private String orderType;
    private String artificialType;
    private List<ZkbPartOrderCat> zkbPartOrderCatList ;
    private List<V2_BlobObject> v2_blobObjectList;
    private List<V2_BlobObject> v2_probe_blobObjectList;
    private BlobFileService blobService;
    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList =new ArrayList<>();
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
    private ZkbContractAssetService zkbContractAssetService;
    private String maintainFlag;
    private String supplementFlag = CopyByInvok.F;;
    private List<I18nMessage> exchangeTypeList = new ArrayList<>();
    private I18nMessageRepository i18nMessageRepository;
    private ZkbHighPartInfoRepository zkbHighPartInfoRepository;
    private String notGeAsset;
    private String listNg;
    String encodeStr;
    private boolean disableNotGeAsset = false;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbIntegralService = WebUtil.getBean(ZkbIntegralService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        zkbContractAssetRepository = WebUtil.getBean(ZkbContractAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        zkbHighPartInfoRepository = WebUtil.getBean(ZkbHighPartInfoRepository.class);
        zkbContractAssetService = WebUtil.getBean(ZkbContractAssetService.class);
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
            selected.setRequestDate(new Date());
            selected.setRequestUser(currentUser.getName());
            selected.setRequestPhone(currentUser.getTelephone());
            v2_blobObjectList = new ArrayList<>();
            v2_probe_blobObjectList = new ArrayList<>();
            Integer c = zkbPartOrderService.getZkbContractRepository().fetchContractCount(selectionTenantInfo);
            if(c == null || c == 0){
                notGeAsset = CopyByInvok.F;
                disableNotGeAsset = true;
            }
        } else if("Copy".equals(actionName)){
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            selected =  m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            copyZkbPartOrder();
            if(StringUtils.isNotEmpty(this.selected.getOrderType())){
                orderType = this.selected.getOrderType();
            }
            artificialType = this.selected.getArtificialType();
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
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetType("TechnicalPending",ZkbPartOrder.CONFIG_US);
            }
            calcRemainQty();
            orderAnnotationChange();
            if(zkbPartOrderService.checkContractLastDay(selectedContract,orderType)){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "请电话联络400先开出工单，避免由于次日审批结束无法订购备件！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                FacesMessage message2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "电话：400-812-8188。", "");
                FacesContext.getCurrentInstance().addMessage(null, message2);
            }
            Integer c = zkbPartOrderService.getZkbContractRepository().fetchContractCount(selectionTenantInfo);
            if(c == null || c == 0){
                notGeAsset = CopyByInvok.F;
                disableNotGeAsset = true;
            }
        }else{
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            this.selected = m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            if(StringUtils.isNotEmpty(this.selected.getOrderType())){
                orderType = this.selected.getOrderType();
            }
            selectedContract = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            if(selectedContract != null){
                contractList = new ArrayList<>();
                contractList.add(selectedContract);
                initZknPartCat(selectedContract.getStartDate());
            }
            HashMap<String, Object> mm = m.get("v2") == null ? null : (HashMap<String, Object>)m.get("v2");
            v2_listHashMap = new ArrayList<>();
            if(mm != null){
                v2_listHashMap.add(mm);
            }
            selectedWorkFlow = mm;
            v2_blobObjectList = m.get("v2_blobObjectList") == null ? new ArrayList<>() : (List<V2_BlobObject>)m.get("v2_blobObjectList");
        }
    }

    public void copyZkbPartOrder(){
        selected.setId(null);
        selected.setStatus(null);
        selected.setCurrentStep(null);
        selected.setRequestDate(new Date());
        selected.setCreatedBy(currentUser.getLoginName());
        selected.setCreatedDate(new Date());
        selected.setRequestUser(currentUser.getName());
        selected.setPartOrderNo(null);
        selected.setPartOrderIndex(null);
        selected.setTechAuditer(null);
        selected.setTechAuditerUid(null);
        selected.setTechRemark(null);
        selected.setTechAuditDate(null);
        selected.setManageAuditerUid(null);
        selected.setManageAuditer(null);
        selected.setManageAuditDate(null);
        selected.setManageRemark(null);
        selected.setSrNo(null);
        selected.setActivityNo(null);
        selected.setOrderNo(null);
        selected.setPrice(null);
        selected.setOrderedRemark(null);
        selected.setUnOrderRemark(null);
        selected.setOrderedDate(null);
        selected.setUnOrderDate(null);
        selected.setUnOrderAuditer(null);
        selected.setUnOrderAuditerUid(null);
        selected.setOrderedAuditerUid(null);
        selected.setReturnAuditerUid(null);
        selected.setReturnAuditer(null);
        selected.setReturnAuditDate(null);
        selected.setReturnQty(null);
        selected.setNewCatSn(null);
        selected.setReturnDate(null);
        selected.setReturnRemark(null);
        selected.setReturnType(null);
        selected.setWaybillNo(null);
        selected.setWhetherDeduct(null);
        selected.setPartSerialNumber(null);
        selected.setPartQualityIssue(null);
        selected.setClosedAuditerUid(null);
        selected.setClosedAuditer(null);
        selected.setClosedDate(null);
        selected.setClosedRemark(null);
        selected.setZkbPartOrderStepSet(null);
        selected.setWhetherDeduct(null);
        selected.setHaveTrainCourseFlag(null);
        selected.setNewCatFlag(null);
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
        if(StringUtils.isEmpty(orderType)){
            errMsg += "请输入订单类型!\n";
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(StringUtils.isEmpty(artificialType)){
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

        if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            if(assetInfo.getAssetGroup() == null || assetInfo.getAssetGroup() != 2){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "探头申请只能选择超声类设备！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
            if(assetInfo.getAssetGroup() != null && !(assetInfo.getAssetGroup() == 2 || assetInfo.getAssetGroup() == 1)){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "备件申请只能选择超声类或者放射类设备！", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }

        contractList = zkbPartOrderService.queryContractByAsset(selectionTenantInfo,String.valueOf(selectedWorkFlow.get("assetId")),(Date)selectedWorkFlow.get("requestTime"));
        if(contractList.size() == 0){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "未查询出合同数据！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }else if(contractList.size() > 1){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同超过两条数据，请检查！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            contractList = new ArrayList<>();
            return;
        }else if(contractList.size() == 1){
            selectedContract = contractList.get(0);
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
            if(CopyByInvok.T.equals(notGeAsset)){
                if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "非GE设备申请只能申请低端机！", "");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    contractList = new ArrayList<>();
                    return;
                }
            }
            //判断合同是否是CCSB
            if(ZkbContract.ContractType.CCSB.toString().equals(selectedContract.getContractType())){
                if(!ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CCSB类型合同只能进行人工申请！", "");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    contractList = new ArrayList<>();
                    return;
                }
            }

            //强制Zkb Fe不能申请高端机
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                if(zkbContractAsset != null && StringUtils.isNotEmpty(zkbContractAsset.getAssetType())){
                    if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "设备为高端机，请检查！", "");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        contractList = new ArrayList<>();
                        return;
                    }
                }
            }


            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
                if(ZkbContract.YOrNEnum.Y.toString().equals(selectedContract.getFiveNineNineFlag()) && ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                    if(CopyByInvok.T.equals(selectedContract.getFirstYearFlag()) && (zkbContractAsset.getApplyNum() == null || zkbContractAsset.getApplyNum() == 0)){
                        Boolean r = zkbContractAssetService.checkHalfAsset(selectedContract.getId(),assetInfo.getUid());
                        if(r == null){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同中没有高端机设备！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            contractList = new ArrayList<>();
                            return;
                        }else if(!r){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "当前备件申请的设备已经超过50%的IB入保数额，此IB无法申请备件！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            contractList = new ArrayList<>();
                        }
                    }
                }else{
                    if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) ||
                        ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                        if(selectedContract.getSpareQuota() == null || selectedContract.getSpareQuota() < 0){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同备件数量小于0，请检查！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            contractList = new ArrayList<>();
                            return;
                        }
                    }else{
                        if(selectedContract.getSpareArtificail() == null || selectedContract.getSpareArtificail() < 0){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同备件+人工+保养剩余名额小于0，请检查！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            contractList = new ArrayList<>();
                            return;
                        }
                    }
                }

            } else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
                    if(selectedContract.getProbeQuota() == null || selectedContract.getProbeQuota() <= 0){
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同探头数量小于等于0，请检查！", "");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        contractList = new ArrayList<>();
                        return;
                    }
                }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){

                }else if(ZkbContract.QuotaDeductTypeEnum.Null.toString().equals(selectedContract.getQuotaDeductType())){
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "探头申请中名额扣除类型不能为N/A！", "");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    contractList = new ArrayList<>();
                    return;
                }
                initHighList(selectedContract.getId());
            } else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
                selected.setSsaKeyFlag(CopyByInvok.F);
                /*if(!"2".equals(artificialType)){
                    if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                        if(selectedContract.getGeArtificialSupportTimes() == null || selectedContract.getGeArtificialSupportTimes() <= 0){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "人工支持次数小于等于0，请检查！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            contractList = new ArrayList<>();
                            return;
                        }
                    }else{
                        if(selectedContract.getSpareArtificail() == null || selectedContract.getSpareArtificail() <= 0){
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "备件+人工+保养剩余名额小于等于0，请检查！", "");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            contractList = new ArrayList<>();
                            return;
                        }
                    }
                }*/
            }else if(ZkbPartOrder.OrderType.Tool.toString().equals(orderType)){
                if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                    if(selectedContract.getSpareQuota() == null || selectedContract.getSpareQuota() < 0){
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同备件数量小于0，请检查！", "");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        contractList = new ArrayList<>();
                        return;
                    }
                }else{
                    if(selectedContract.getSpareArtificail() == null || selectedContract.getSpareArtificail() < 0){
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "合同备件+人工+保养剩余名额小于0，请检查！", "");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        contractList = new ArrayList<>();
                        return;
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
                if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) >-1){
                     if(CopyByInvok.T.equals(notGeAsset)){
                         zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameContains(ZkbPartOrderStep.PartOrderStep.TsePending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                     }else{
                         zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameContains(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                     }
                }else{
                    if(CopyByInvok.T.equals(notGeAsset)){
                        zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameIsNotContaining(ZkbPartOrderStep.PartOrderStep.TsePending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                    }else{
                        zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameIsNotContaining(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                    }
                }
            }
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
                if(StringUtils.isEmpty(zkbContractAsset.getAssetType())){
                    selected.setArtificialType(CopyByInvok.NORMAL_THREE);
                }else if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                    selected.setArtificialType(CopyByInvok.NORMAL_TWO);
                }else if(ZkbContractAsset.AssetTypeEnum.lowMachine.toString().equals(zkbContractAsset.getAssetType())){
                    selected.setArtificialType(CopyByInvok.NORMAL_THREE);
                }else if(ZkbContractAsset.AssetTypeEnum.depot.toString().equals(zkbContractAsset.getAssetType())){
                    selected.setArtificialType(CopyByInvok.NORMAL_DEPOT);
                }else if(ZkbContractAsset.AssetTypeEnum.empty.toString().equals(zkbContractAsset.getAssetType())){
                    selected.setArtificialType(CopyByInvok.NORMAL_THREE);
                }

                //判断是否是放射类以及合同类型是否是DI或者PCB
                if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())){
                    if(assetInfo.getAssetGroup().equals(1)){
                        radiationQuotaTypeFlag = true;
                        Integer q = selectedContract.getPartHighQuota() == null ? 0 : selectedContract.getPartHighQuota();
                        if(q<= 0){
                            selected.setRadiationQuotaType(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString());
                            spareArtificialFlag = true;
                        }
                    }
                }
            }
            selected.setRegion(selectedContract.getRegion());
            initZknPartCat(selectedContract.getStartDate());
            if(zkbPartOrderService.checkContractLastDay(selectedContract,orderType)){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "请电话联络400先开出工单，避免由于次日审批结束无法订购备件!", "");
                FacesContext.getCurrentInstance().addMessage(null, message);
                FacesMessage message2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "电话：400-812-8188。", "");
                FacesContext.getCurrentInstance().addMessage(null, message2);
            }
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) && assetInfo.getAssetGroup() == 2){
                //如果是备件机型默认超神低端机
                //selected.setArtificialType("3");
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
                editCurrentScoreFlag();
                handleQtyByOrderType();
            }
        }
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = new ArrayList<ZkbPartOrderUserConfig>();
        if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndRegionAndLoginNameContains(ZkbPartOrderStep.PartOrderStep.ManagePending.toString(), selected.getRegion(),ZkbPartOrder.DATA_TYPE_TEST);
        }else{
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndRegionAndLoginNameIsNotContaining(ZkbPartOrderStep.PartOrderStep.ManagePending.toString(), selected.getRegion(),ZkbPartOrder.DATA_TYPE_TEST);
        }
        Optional<ZkbPartOrderUserConfig>  zkbPartOrderUserConfig = zkbPartOrderUserConfigs.stream().findFirst();
        if(zkbPartOrderUserConfig.isPresent()){
            selected.setRegionalManagerEmail(zkbPartOrderUserConfig.get().getEmail());
            selected.setRegionalManagerPhone(zkbPartOrderUserConfig.get().getPhone());
            selected.setRegionalManagerName(zkbPartOrderUserConfig.get().getName());
        }
        calcRemainQty();
    }

    public void handleQtyByOrderType(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            this.selected.setQty(1);
        }else{
            this.selected.setQty(0);
            if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
                this.selected.setCatNum(1);
                if(CopyByInvok.T.equals(notGeAsset)){
                    this.selected.setQty(1);
                }
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
                if(assetInfo == null || StringUtils.isEmpty(assetInfo.getUid()) || selectedContract == null){
                    this.selected.setArtificialType(CopyByInvok.NORMAL_THREE);
                }else{
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
                    if(ZkbContract.YOrNEnum.Y.toString().equals(selectedContract.getFiveNineNineFlag()) && ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType()) ){
                        this.selected.setArtificialType(CopyByInvok.NORMAL_TWO);
                    } else{
                        if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                            this.selected.setArtificialType(CopyByInvok.NORMAL_THREE);
                        }else if(ZkbContractAsset.AssetTypeEnum.depot.toString().equals(zkbContractAsset.getAssetType())){
                            this.selected.setArtificialType(CopyByInvok.NORMAL_DEPOT);
                        }

                    }
                }
            }
        }
    }

    public void editCurrentScoreFlag(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
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
        }else if("f".equals(isVacation)){
            return "正常上班中";
        }else if("t".equals(isVacation)){
            return "休假中";
        }
        return "";
    }

    public boolean scoreFlagRender(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(CopyByInvok.T.equals(maintainFlag)){
                return true;
            }else{
                if(!CopyByInvok.NORMAL_TWO.equals(artificialType)){
                    return true;
                }
                if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) && "t".equals(selectedContract.getFirstYearFlag())){

                }else{
                    return true;
                }
            }
        }
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

    public boolean isItemCreateDisabled(){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())) {
                if (assetInfo.getAssetGroup().equals(1)) {
                    if(StrUtil.isEmpty(selected.getRadiationQuotaType())){
                        return true;
                    }
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
        if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)) {
            if (ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())) {
                if (assetInfo.getAssetGroup().equals(1)) {
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    if (ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())) {
                        ZkbHighPartInfo z = zkbHighPartInfoRepository.findFirstByPartNo(zkbPartOrderItem.getPartNo());
                        if (z != null) {
                            String el = "";
                            if (ZkbHighPartInfo.PartTypeEnum.highPart.toString().equals(z.getPartType())) {
                                el += zkbPartOrderItem.getPartNo() + "属于高值备件，不可订购！";
                            } else if (ZkbHighPartInfo.PartTypeEnum.bulbTube.toString().equals(z.getPartType())) {
                                el += zkbPartOrderItem.getPartNo() + "属于球管，不可订购！";
                            }
                            if (StrUtil.isNotEmpty(el)) {
                                itemSaveTips = el;
                                return;
                            } else {
                                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
                            }
                        } else {
                            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
                        }
                    } else if (ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())) {
                        List<ZkbHighPartInfo> zkbHighPartInfos = zkbHighPartInfoRepository.findBulbTube(zkbPartOrderItem.getPartNo());
                        if (zkbHighPartInfos != null && zkbHighPartInfos.size() > 0) {
                            itemSaveTips = zkbPartOrderItem.getPartNo() + "属于球管，不可订购！";
                            return;
                        } else {
                            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
                        }
                    }

                }
            }
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
            if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Tool.toString().equals(orderType)){
                if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) &&
                        (ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())) &&
                        assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                        Integer contractCount = selectedContract.getPartHighQuota() == null ? 0 : selectedContract.getPartHighQuota();
                        this.selected.setRemainQty(contractCount - this.selected.getQty());
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                        Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                        this.selected.setRemainQty(contractCount - this.selected.getQty());
                    }
                }else if(!CopyByInvok.NORMAL_TWO.equals(selected.getArtificialType())) {
                    if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                        Integer contractCount = selectedContract.getSpareQuota() == null ? 0 : selectedContract.getSpareQuota();
                        this.selected.setRemainQty(contractCount - this.selected.getQty());
                    }else{
                        Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                        this.selected.setRemainQty(contractCount - this.selected.getQty());
                    }
                }else {
                    if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                        Integer contractCount = selectedContract.getSpareQuota() == null ? 0 : selectedContract.getSpareQuota();
                        this.selected.setRemainQty(contractCount);
                    }else{
                        Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                        this.selected.setRemainQty(contractCount);
                    }
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
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
                            if(CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())){
                                Integer contractCount = selectedContract.getProbeQuotaNormal() == null ? 0 : selectedContract.getProbeQuotaNormal();
                                this.selected.setRemainQty(contractCount - this.selected.getQty());
                            }else {
                                this.selected.setRemainQty(0);
                            }
                        }
                    }else if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
                        if(StringUtils.isEmpty(selected.getHighCatFlag()) || CopyByInvok.F.equals(selected.getHighCatFlag())){
                            Integer contractCount = selectedContract.getProbeQuota() == null ? 0 : selectedContract.getProbeQuota();
                            this.selected.setRemainQty(contractCount - this.selected.getQty());
                        }else{
                            if(CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())){
                                Integer contractCount = selectedContract.getProbeQuota() == null ? 0 : selectedContract.getProbeQuota();
                                this.selected.setRemainQty(contractCount - this.selected.getQty());
                            }else{
                                this.selected.setRemainQty(0);
                            }
                        }
                    }
                }
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
                if(ZkbContract.ContractType.CCSB.toString().equals(selectedContract.getContractType())){
                    if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(selected.getCcsbApplyType())){
                        Integer remindQty = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                        if(remindQty >= 1){
                            this.selected.setRemainQty(remindQty - 1);
                        }else{
                            this.selected.setRemainQty(0);
                        }
                    }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(selected.getCcsbApplyType())){
                        Integer remindQty = selectedContract.getCcsbMaintenanceQuota() == null ? 0 : selectedContract.getCcsbMaintenanceQuota();
                        if(remindQty >= 1){
                            this.selected.setRemainQty(remindQty - 1);
                        }else{
                            this.selected.setRemainQty(0);
                        }
                    }else{
                        this.selected.setRemainQty(0);
                    }
                }else if(CopyByInvok.T.equals(maintainFlag)){
                    if(CopyByInvok.T.equals(selected.getScoreFlag())){
                        this.selected.setRemainQty(0);
                    }else {
                        Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                        this.selected.setRemainQty(contractCount);
                    }
                }else{
                    if(CopyByInvok.NORMAL_TWO.equals(selected.getArtificialType())) {
                        if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                            Integer contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                            this.selected.setRemainQty(contractCount);
                        }else{
                            Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                            this.selected.setRemainQty(contractCount);
                        }
                    }else{
                        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
                        if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) && CopyByInvok.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                            Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                            this.selected.setRemainQty(contractCount);

                        } else if(CopyByInvok.T.equals(selected.getScoreFlag())){
                            this.selected.setRemainQty(0);
                        } else{
                            if(CopyByInvok.NORMAL_ONE.equals(selected.getArtificialType()) && CopyByInvok.T.equals(selected.getSsaKeyFlag())){
                                if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                                    Integer contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                                    this.selected.setRemainQty(contractCount);
                                }else{
                                    Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                                    this.selected.setRemainQty(contractCount);
                                }
                            }  else {
                                if(ZkbContract.ContractType.CCS.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(selectedContract.getContractType())){
                                    Integer contractCount = selectedContract.getGeArtificialSupportTimes() == null ? 0 : selectedContract.getGeArtificialSupportTimes();
                                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                                }else{
                                    Integer contractCount = selectedContract.getSpareArtificail() == null ? 0 : selectedContract.getSpareArtificail();
                                    this.selected.setRemainQty(contractCount - this.selected.getQty());
                                }
                            }
                        }
                    }
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
    public void preSave() throws ParseException {
        if(assetInfo == null){
            return;
        }
        selected.setOrderType(orderType);
        selected.setSupplementFlag(supplementFlag);
        if(StrUtil.isNotEmpty(notGeAsset)){
            if(CopyByInvok.T.equals(notGeAsset)){
                selected.setNotGeAsset(CopyByInvok.T);
            }else{
                selected.setNotGeAsset(null);
            }
        }
        if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            boolean onlineEngineFlag= false;
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType()) && !ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                onlineEngineFlag = true;
            }
            if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType()) && CopyByInvok.F.equals(selected.getHighCatFlag())){
                onlineEngineFlag = true;
            }
            if(onlineEngineFlag){
                this.selected.setCatId(this.selected.getZkbPartOrderCat().getId());
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
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(ZkbContract.ContractType.CCSB.toString().equals(selectedContract.getContractType())){
                maintainFlag = CopyByInvok.F;
            }
            this.selected.setArtificialType(artificialType);
            this.selected.setMaintainFlag(maintainFlag);
            if(CopyByInvok.T.equals(maintainFlag)){
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
                if("t".equals(zkbContractAsset.getSupportMaintainFlag())){
                    boolean b = zkbPartOrderService.checkDeductMaintain(zkbContractAsset);
                    if(b){
                        this.selected.setMaintainFirstFlag(CopyByInvok.F);
                    }else{
                        this.selected.setMaintainFirstFlag(CopyByInvok.T);
                    }
                }

            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
            if(this.selected.getOnlineEngine() != null){
                if(CopyByInvok.T.equals(this.selected.getOnlineEngine().getIsVacation())){
                    ZkbPartOrderUserConfig backUp = zkbPartOrderUserConfigRepository.findById(this.selected.getOnlineEngine().getUsBackUpId());
                    this.selected.setOnlineEngine(backUp);
                    this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getUsBackUpId());
                }else{
                    this.selected.setOnlineEngineer(this.selected.getOnlineEngine().getId());
                }
            }
        }
        quotaTips = zkbPartOrderService.preSave(selected,selectedContract,assetInfo);

    }


    /**
     * 保存方法
     */
    public String saveData(){
        boolean flag = true;
        String errorLog = "";
        if(StringUtils.isEmpty(this.orderType)){
            flag=false;
            errorLog += "订单类型不能为空！";
        }
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(this.orderType)){
            if(StringUtils.isEmpty(this.maintainFlag)){
                flag=false;
                errorLog += "保养申请不能为空！";
            }
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
        if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Tool.toString().equals(orderType)){
            if((this.selected.getZkbPartOrderItems() ==null || this.selected.getZkbPartOrderItems().size() == 0) ){
                flag=false;
                errorLog += "额度申请明细不能为空！";
            }
        }

        if(!ZkbPartOrder.OrderType.Artificial.toString().equals(orderType) && !ZkbPartOrder.OrderType.Tool.toString().equals(orderType)){
            if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup() == 2 && selected.getOnlineEngine() == null){
                flag=false;
                errorLog += "在线工程师不能为空！";
            }
        }
        if(!ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(this.v2_blobObjectList == null || this.v2_blobObjectList.size() == 0){
                flag=false;
                errorLog += "附件不能为空！";
            }
        }else{
            if("t".equals(selected.getScoreFlag())){
                boolean r = zkbIntegralService.checkScoreFlag(selectionTenantInfo,selected.getQty());
                if(!r){
                    flag=false;
                    errorLog += "积分不够！";
                }
            }
        }
        if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType()) && CopyByInvok.T.equals(selected.getTenantNewCatFlag())){
                if(this.v2_probe_blobObjectList == null || this.v2_probe_blobObjectList.size() == 0){
                    flag=false;
                    errorLog += "院方合同证明不能为空！";
                }else if(this.v2_probe_blobObjectList.size() > 1){
                    flag=false;
                    errorLog += "院方合同证明只能上传单个！";
                }
            }
        }
        if(selected.getRemainQty() == null){
            flag=false;
            errorLog += "剩余数量不能为空！";
        }else if(selected.getRemainQty() < 0){
            flag=false;
            errorLog += "剩余数量不足！";
        }

        if(!flag){
            WebUtil.addErrorMessage(errorLog);
            return "";
        }
        //region 实时获取合同进行校验数量
        ZkbContract zkbContract = zkbPartOrderService.getZkbContractRepository().findById(selectedContract.getId());
        String reLog = "";
        if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
            if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                if(CopyByInvok.T.equals(selectedContract.getFirstYearFlag()) && (zkbContractAsset.getApplyNum() == null || zkbContractAsset.getApplyNum() == 0)){
                    Boolean r = zkbContractAssetService.checkHalfAsset(zkbContract.getId(),assetInfo.getUid());
                    if(r == null){
                        reLog += "合同中没有高端机设备！";
                    }else if(!r){
                        reLog += "当前备件申请的设备已经超过50%的IB入保数额，此IB无法申请备件！";
                    }
                }
            }else{
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    Integer contractCount = zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota();
                    if(contractCount - selected.getQty() < 0){
                        reLog += "合同备件剩余名额不足，请检查！";
                    }
                }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())) {
                    if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                        if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                            Integer contractCount = zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota();
                            if(contractCount - selected.getQty() < 0){
                                reLog += "备件高值剩余名额不足，请检查！";
                            }
                        }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                            Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                            if(contractCount - selected.getQty() < 0){
                                reLog += "合同备件+人工+保养剩余名额不足，请检查！";
                            }
                        }
                    }else{
                        Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                        if(contractCount - selected.getQty() < 0){
                            reLog += "合同备件+人工+保养剩余名额不足，请检查！";
                        }
                    }
                }else{
                    Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                    if(contractCount - selected.getQty() < 0){
                        reLog += "合同备件+人工+保养剩余名额不足，请检查！";
                    }
                }
            }
            if(ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())){
                if(assetInfo.getAssetGroup().equals(1)){
                    String el = "";
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                        for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                            ZkbHighPartInfo z = zkbHighPartInfoRepository.findFirstByPartNo(i.getPartNo());
                            if(z != null ){
                                if(ZkbHighPartInfo.PartTypeEnum.highPart.toString().equals(z.getPartType())){
                                    el += "备件号："+i.getPartNo()+"属于高值备件，不可订购！";
                                }else if(ZkbHighPartInfo.PartTypeEnum.bulbTube.toString().equals(z.getPartType())){
                                    el += "备件号："+i.getPartNo()+"属于球管，不可订购！";
                                }
                            }
                        }
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                        for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                            List<ZkbHighPartInfo> zkbHighPartInfos = zkbHighPartInfoRepository.findBulbTube(i.getPartNo());
                            if(zkbHighPartInfos != null && zkbHighPartInfos.size() > 0){
                                el += "备件号："+i.getPartNo()+"属于球管，不可订购！";
                            }
                        }
                    }
                    reLog += el;
                }
            }
        } else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            String probeType = "";
            if(StringUtils.isEmpty(zkbContract.getQuotaDeductType())){
                reLog += "合同探头扣除类型为空，请检查！";
            }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Any.toString();
                    Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                    if(contractCount - selected.getQty() < 0){
                        reLog += "合同探头任意剩余名额不足，请检查！";
                    }
                    if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog += "高值探头不能选用任意名额！";
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString();
                    Integer contractCount = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
                    if(contractCount - selected.getQty() < 0){
                        reLog += "合同探头普通剩余名额不足，请检查！";
                    }
                    if(!ZkbPartOrderCat.ValueType.NormalValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog += "探头普通剩余名额只能选择普通探头！";
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Special.toString();
                    Integer contractCount = zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial();
                    if(contractCount - selected.getQty() < 0){
                        reLog += "合同探头特殊剩余名额不足，请检查！";
                    }
                    if(!ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog += "探头特殊剩余名额只能选择特殊探头！";
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString();
                    ZkbContractCat zkbContractCat = zkbPartOrderService.getZkbContractCatRepository().findById(this.selected.getContractCatId());
                    if(zkbContractCat != null && CopyByInvok.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){                        probeType = ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString();
                        Integer contractCount = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
                        if(contractCount - selected.getQty() < 0){
                            reLog += "合同探头普通剩余名额不足，请检查！";
                        }
                    }
                }
            }else if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())){
                if(CopyByInvok.F.equals(selected.getHighCatFlag())){
                    if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog += "不能选择高值探头！";
                    }
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString();
                    Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                    if(contractCount - selected.getQty() < 0){
                        reLog += "合同探头剩余名额不足，请检查！";
                    }
                }else if(CopyByInvok.T.equals(selected.getHighCatFlag())){
                    ZkbContractCat zkbContractCat = zkbPartOrderService.getZkbContractCatRepository().findById(this.selected.getContractCatId());
                    if(zkbContractCat != null && CopyByInvok.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                        probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString();
                        Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                        if(contractCount - selected.getQty() < 0){
                            reLog += "合同探头剩余名额不足，请检查！";
                        }
                    }
                }
            }
            if(CopyByInvok.T.equals(zkbContract.getAssetQuotaType()) && StrUtil.isNotEmpty(probeType)){
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
                boolean r = zkbPartOrderService.checkQuotaHistory("apply", zkbContract, selected,zkbContractAsset, probeType);
                if(!r){
                    if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                        reLog += "该合同中的高端机名额不足,请检查！";
                    }else{
                        reLog += "该合同中的低端机名额不足,请检查！";
                    }
                }
            }

        } else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){

            }else if(CopyByInvok.T.equals(maintainFlag)){
                if(CopyByInvok.T.equals(selected.getScoreFlag())){
                }else{
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
                    if(CopyByInvok.F.equals(selected.getMaintainFirstFlag()) || CopyByInvok.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                        Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                        if(contractCount - selected.getQty() < 0){
                            reLog += "备件+人工+保养剩余名额不足，请检查！";
                        }
                    }
                }
            }else{
                if(!CopyByInvok.NORMAL_TWO.equals(artificialType)){
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
                    if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) && CopyByInvok.T.equals(zkbContractAsset.getSupportMaintainFlag())){

                    } else if(CopyByInvok.NORMAL_ONE.equals(artificialType) && CopyByInvok.T.equals(selected.getSsaKeyFlag())){

                    } else if(CopyByInvok.T.equals(selected.getScoreFlag())){

                    } else{
                        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                            Integer contractCount = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                            if(contractCount - selected.getQty() < 0){
                                reLog += "人工剩余名额不足，请检查！";
                            }
                        }else{
                            Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                            if(contractCount - selected.getQty() < 0){
                                reLog += "备件+人工+保养剩余名额不足，请检查！";
                            }
                        }
                    }
                }
            }
        } else if(ZkbPartOrder.OrderType.Tool.toString().equals(orderType)){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                Integer contractCount = zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota();
                if(contractCount - selected.getQty() < 0){
                    reLog += "合同备件剩余名额不足，请检查！";
                }
            }else{
                Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                if(contractCount - selected.getQty() < 0){
                    reLog += "合同备件+人工+保养剩余名额不足，请检查！";
                }
            }
        }
        if(StringUtils.isNotEmpty(reLog)){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, reLog, "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            contractList = new ArrayList<>();
            contractList.add(zkbContract);
            return "";
        }

        //selected.setOrderType(orderType);
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
        if(zkbPartOrderService.checkContractLastDay(zkbContract,orderType)){
            zkbPartOrderService.sendEmail(selected, CopyByInvok.EMAIL_URL_TYPE_LASTDAY,UserContextService.getAccessToken());
        }
        return "List?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(listNg) ? "" : listNg);
    }

    public void notGeAssetChange(ValueChangeEvent event){
        String v = event.getNewValue() == null ? "" : (String)event.getNewValue();
        String url = "Edit.xhtml?str="+ encodeStr+"&ng="+v;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void orderTypeChange(){
        v2_listHashMap = new ArrayList<>();
        contractList = new ArrayList<>();
        assetInfo  = new AssetInfo();
        selectedContract = new ZkbContract();
        selected = new ZkbPartOrder();
        selected.setRequestDate(new Date());
        selected.setRequestUser(currentUser.getName());
        selected.setRequestPhone(currentUser.getTelephone());
        queryWorkFlow = new V2_WorkOrder();
        queryWorkFlow.setAssetInfo(new AssetInfo());
        queryWorkFlow.setServiceRequest(new V2_ServiceRequest());
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            selected.setScoreFlag(CopyByInvok.F);
            if(StringUtils.isEmpty(maintainFlag)){
                maintainFlag = CopyByInvok.F;
            }

            if(StringUtils.isNotEmpty(artificialType)){
                if(queryWorkFlow.getAssetInfo() != null && (StringUtils.isNotEmpty(queryWorkFlow.getAssetInfo().getSystemId()) || StringUtils.isNotEmpty(queryWorkFlow.getServiceRequest().getSrNum()))) {
                    getQueryWorkFlowList();
                }
            }

        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            this.selected.setCatNum(1);
        }
    }

    public void supplementFlagChange(){
        if(CopyByInvok.F.equals(supplementFlag)){
            selected.setSrNo("");
        }
    }

    public void scoreFlagChange(){
        if(CopyByInvok.T.equals(selected.getScoreFlag())){
            selected.setRemainQty(0);
            exchangeTypeList = i18nMessageRepository.findI18nByType("zkbExchangeType");
            if(CopyByInvok.T.equals(maintainFlag)){
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

    public void ssaKeyFlagChange(){
        if("t".equals(selected.getSsaKeyFlag())){
            currentScoreFlag = true;
            currentScoreText = "";
            selected.setScoreFlag(CopyByInvok.F);
            return;
        }
    }

    public void tenantNewCatFlagChange(){
        if(this.selected.getQty() == null){
            return;
        }
        if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())){
            if(CopyByInvok.T.equals(selected.getTenantNewCatFlag())){
                this.selected.setQty(selected.getQty() * 2);
            }else if(CopyByInvok.F.equals(selected.getTenantNewCatFlag())){
                this.selected.setQty(selected.getQty() / 2);
            }
        }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){
            //不做任何操作
        }else{
            //扣除逻辑为空将数量设置为null
            this.selected.setQty(null);
        }
        calcRemainQty();
    }

    public void zkbPartOrderCatSelect(){
        if(CopyByInvok.T.equals(notGeAsset)){
            this.selected.setQty(1);
            valueTypeTips = "";
        }else{
            if(StringUtils.isNotEmpty(selectedContract.getQuotaDeductType())){
                if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){
                    if(!ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                        if(this.selected.getZkbPartOrderCat() != null){
                            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                                this.selected.setQty(1);
                                valueTypeTips = "";
                            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                                this.selected.setQty(null);
                            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                                this.selected.setQty(1);
                                valueTypeTips = "";
                            }
                        }
                    }else {
                        if(StringUtils.isNotEmpty(this.selected.getContractCatId())){
                            ZkbContractCat zkbContractCat = zkbPartOrderService.getZkbContractCatRepository().findById(this.selected.getContractCatId());
                            if(zkbContractCat != null && CopyByInvok.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                this.selected.setQty(4);
                            }else{
                                this.selected.setQty(0);
                            }
                        }
                    }
                }else if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())) {
                    if(StringUtils.isEmpty(selected.getHighCatFlag()) || CopyByInvok.F.equals(selected.getHighCatFlag())){
                        if(this.selected.getZkbPartOrderCat() != null){
                            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                                this.selected.setQty(1);
                                valueTypeTips = "";
                            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                                this.selected.setQty(0);
                                valueTypeTips = "您的选择为高值探头，请确认合同中是否有此权益！";
                            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                                this.selected.setQty(2);
                                valueTypeTips = "";
                            }
                        }
                        if(CopyByInvok.T.equals(selected.getTenantNewCatFlag())){
                            this.selected.setQty(selected.getQty() * 2);
                        }
                    }else{
                        if(StringUtils.isNotEmpty(this.selected.getContractCatId())){
                            ZkbContractCat zkbContractCat = zkbPartOrderService.getZkbContractCatRepository().findById(this.selected.getContractCatId());
                            if(zkbContractCat != null && CopyByInvok.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                if(CopyByInvok.T.equals(selected.getTenantNewCatFlag())){
                                    this.selected.setQty(8);
                                }else{
                                    this.selected.setQty(4);
                                }
                            }else{
                                this.selected.setQty(0);
                            }
                        }
                    }
                }
            }else {
                this.selected.setQty(null);
            }
        }

        calcRemainQty();
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
        if(StringUtils.isNotEmpty(orderType) && (contractList != null && contractList.size() == 1)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isShowRegionalManager(){
        boolean isShowRegionalManager = true;
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)) {
            if("2".equals(artificialType)){
                isShowRegionalManager = false;
            }
        }
        return isShowRegionalManager;
    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(listNg) ? "" : listNg);
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

    public String getMaintainFlag() {
        return maintainFlag;
    }

    public void setMaintainFlag(String maintainFlag) {
        this.maintainFlag = maintainFlag;
    }

    public List<I18nMessage> getExchangeTypeList() {
        return exchangeTypeList;
    }

    public void setExchangeTypeList(List<I18nMessage> exchangeTypeList) {
        this.exchangeTypeList = exchangeTypeList;
    }

    public String getSupplementFlag() {
        return supplementFlag;
    }

    public void setSupplementFlag(String supplementFlag) {
        this.supplementFlag = supplementFlag;
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

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }
}
