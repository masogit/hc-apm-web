package com.ge.apm.view.zkb;

import cn.hutool.core.util.StrUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.CopyByInvok;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.springframework.retry.backoff.Sleeper;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

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
    private List<ZkbContract> contractList;
    private String orderType;
    private String artificialType;
    private List<V2_BlobObject> v2_blobObjectList;
    private List<V2_BlobObject> v2_probe_blobObjectList;
    private boolean showTech=false;
    private boolean showProduct = false;
    private boolean showManage=false;
    private boolean showDirector = false;
    private boolean showCommercial = false;
    private boolean showUnOrder = false;
    private boolean showOrder=false;
    private boolean showReturn=false;
    private boolean showRejectOrder = false;
    private boolean showClosed=false;
    private boolean canEditFlag=false;
    private boolean isChoseAdmin = false;
    private boolean isOrder = false;
    private boolean isUnOrder = false;
    private boolean whetherDeductEdit = false;
    private boolean showBtnFourHundredCancel = false;

    private String showBtn = "";
    private ZkbPartOrderReturn zkbPartOrderReturn;
    private List<ZkbPartOrderReturn> zkbPartOrderReturnList;
    private String actionReturnName;
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private ZkbContractAssetRepository zkbContractAssetRepository;
    private ZkbPartOrderRepository zkbPartOrderRepository;
    private I18nMessageRepository i18nDao;
    private List<HashMap<String, Object>> v2_listHashMap;
    private TenantInfo tenantInfo;
    private TenantInfoRepository tenantInfoRepository;

    private List<HashMap<String, String>> partNoList = new ArrayList<>();
    private List<HashMap<String, String>> orderNoList = new ArrayList<>();
    private List<I18nMessage> returnTypeList= new ArrayList<>();
    private String valueTypeTips = "";
    private String showOrderedFieldsetText = "";
    private String trainTips="";
    private String queryStatus;
    private List<ZkbPartOrderItemVo> zkbPartOrderItemVoList = new ArrayList<>();
    private HashMap<String,String> conditionMap = new HashMap<>();
    private List<ZkbPoHistoryQueryVo> zkbHistoryDataList;
    private List<ZkbPoHistoryQueryVo> geHistoryDataList;
    private String remindLetterFlag = "0";
    private ZkbContractRepository zkbContractRepository;
    private List<ZkbPartOrderReturn> zkbPartOrderReturnModifyList;
    private String saveTips;
    private String letterTips;
    private List<HashMap<String, String>> highCatList = new ArrayList<>();
    private String showCatListFlag = "";//1显示探头 2:显示sn列表
    private List<SvcEngineLogisticsLineVO> svcEngineLogisticsLineVOList = new ArrayList<>();

    private String modifyType="";
    private String modifyTitle="";
    private String str = "";
    private String modifySsaKey;

    @Override
    protected void init() {
        super.init();
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbPartOrderReturnRepository = WebUtil.getBean(ZkbPartOrderReturnRepository.class);
        zkbPartOrderRepository = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        zkbContractAssetRepository = WebUtil.getBean(ZkbContractAssetRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();

        str = WebUtil.getRequestParameter("str");
        System.out.println(str);
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        queryStatus = UrlEncryptController.getValueFromMap(encodeStr, "queryStatus") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");

        if(StringUtils.isNotEmpty(selectedId)){
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            this.selected = m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            Optional.ofNullable(selected.getTenantUID()).ifPresent(t -> tenantInfo = tenantInfoRepository.findByuid(selected.getTenantUID()));
            Optional.ofNullable(this.selected.getOrderType()).ifPresent(t -> orderType = this.selected.getOrderType());
            Optional.ofNullable(this.selected.getArtificialType()).ifPresent(t -> artificialType = this.selected.getArtificialType());

            zkbContract = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            if(zkbContract == null){
                zkbContract = new ZkbContract();
                return;
            }else{
                contractList = new ArrayList<>();
                contractList.add(zkbContract);
            }
            selectedWorkFlow = m.get("V2_WorkOrder") == null ? null : (V2_WorkOrder)m.get("V2_WorkOrder");
            HashMap<String, Object> mm = m.get("v2") == null ? null : (HashMap<String, Object>)m.get("v2");
            v2_listHashMap = new ArrayList<>();
            if(mm != null){
                v2_listHashMap.add(mm);
            }
            v2_blobObjectList = m.get("v2_blobObjectList") == new ArrayList<>() ? null : (List<V2_BlobObject>)m.get("v2_blobObjectList");
            v2_probe_blobObjectList = m.get("v2_probe_blobObjectList") == null ? new ArrayList<>() : (List<V2_BlobObject>)m.get("v2_probe_blobObjectList");
            fieldRendered();
            zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selectedId,isChoseAdmin);

            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    selected.setRemainQty(zkbContract.getSpareQuota());
                }else{
                    selected.setRemainQty(zkbContract.getSpareArtificail());
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                if(StringUtils.isNotEmpty(selected.getProbeQuotaType())){
                    if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                        this.selected.setRemainQty(zkbContract.getProbeQuota());
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                        this.selected.setRemainQty(zkbContract.getProbeQuotaNormal());
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                        this.selected.setRemainQty(zkbContract.getProbeQuotaSpecial());
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                        if(CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())){
                            this.selected.setRemainQty(zkbContract.getProbeQuotaNormal());
                        }
                    }
                }else{
                    if(StringUtils.isEmpty(selected.getHighCatFlag()) || CopyByInvok.F.equals(selected.getHighCatFlag())){
                        this.selected.setRemainQty(zkbContract.getProbeQuota());
                    }else{
                        if(CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())){
                            this.selected.setRemainQty(zkbContract.getProbeQuota());
                        }
                    }

                }
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
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
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                this.selected.setNeedLetterFlag(CopyByInvok.F);
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
            initHighList(zkbContract.getId());

            if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
                showOrderedFieldsetText = "派工信息";
            }else{
                showOrderedFieldsetText = "订货信息";
            }
            trainCourseRender();
            if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Parts.toString()) && selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString())){
                List<String> ss = new ArrayList<>();
                for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                    ss.add(i.getPartNo());
                }
                zkbPartOrderItemVoList = zkbPartOrderService.getZkbPartOrderItemRepository().queryHistoryPart(selected.getSrNum(),selected.getId(),selected.getAssetTypeDi());
            }

            if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Parts.toString()) || this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Probe.toString())){
                if(canEditFlag && !zkbPartOrderService.checkThreeValid(selected)){
                    WebUtil.addErrorMessage("此订单下已订购三个及以上备件或探头，建议联络技术支持或区经理寻求指导！","");
                }
                if(canEditFlag){
                    String validString = zkbPartOrderService.checkContractWeekValid(selected,zkbContract);
                    if(StringUtils.isNotBlank(validString)){
                        WebUtil.addErrorMessage(validString,"");
                    }
                    if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(selected.getCurrentStep())){
                        if(StringUtils.isNotEmpty(selected.getCheckApplyNumFlag()) && CopyByInvok.F.equals(selected.getCheckApplyNumFlag())) {
                            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                                if(!ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                    boolean r = zkbPartOrderService.checkApplyNumValid(selected,zkbContract);
                                    if(r){
                                        WebUtil.addErrorMessage("代理商备件已完成归还，目前未超限，可直接批复。","");
                                    }else{
                                        WebUtil.addErrorMessage("未归还备件/探头数量已超过限额，请先提交备件/探头归还，若场地有其他特殊情况请邮件申请。","");
                                    }
                                }
                            }else{
                                boolean r = zkbPartOrderService.checkApplyNumValid(selected,zkbContract);
                                if(r){
                                    WebUtil.addErrorMessage("代理商备件已完成归还，目前未超限，可直接批复。","");
                                }else{
                                    WebUtil.addErrorMessage("未归还备件/探头数量已超过限额，请先提交备件/探头归还，若场地有其他特殊情况请邮件申请。","");
                                }
                            }
                        }
                    }else if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(selected.getCurrentStep()) || ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                            boolean f = false;
                            if(selected.getZkbPartOrderCat() != null && !ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                f = true;
                            }else if("t".equals(selected.getHighCatFlag())){
                                f = true;
                            }
                            if(f){
                                boolean r = zkbPartOrderService.checkApplyNumValid(selected,zkbContract);
                                if(!r){
                                    WebUtil.addErrorMessage("未归还备件/探头数量已超过限额，请先提交备件/探头归还，若场地有其他特殊情况请邮件申请。","");
                                }
                            }
                        }else{
                            boolean r = zkbPartOrderService.checkApplyNumValid(selected,zkbContract);
                            if(!r){
                                WebUtil.addErrorMessage("未归还备件/探头数量已超过限额，请先提交备件/探头归还，若场地有其他特殊情况请邮件申请。","");
                            }
                        }
                    }
                }
                if(userContextService.isZkbUser()) {
                    if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Probe.toString())
                            && CopyByInvok.T.equals(selected.getNeedLetterFlag())
                            && (CopyByInvok.F.equals(selected.getUploadLetterFlag()) || StringUtils.isEmpty(selected.getUploadLetterFlag()))){
                        //WebUtil.addErrorMessage("请尽快邮件提交探头说明函致400彭海萍haiping.peng@ge.com。","");
                        letterTips = "请尽快邮件提交探头说明函致400唐扬Yang.Tang@ge.com。";
                    }
                }
            }else if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Artificial.toString())){
                if(canEditFlag){
                    if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                            if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                                whetherDeductEdit = true;
                                selected.setWhetherDeduct(CopyByInvok.F);
                            }else{
                                if(selected.getAssetInfo().getRepairForm() != null && selected.getAssetInfo().getRepairForm() == 2){
                                    whetherDeductEdit = true;
                                    selected.setWhetherDeduct(CopyByInvok.F);
                                }
                            }
                        }else if(CopyByInvok.T.equals(selected.getMaintainFlag())){
                            whetherDeductEdit = true;
                            ZkbContractAsset zkbContractAsset = zkbPartOrderService.getZkbContractAssetRepository().findFirstByContractIdAndAssetId(selected.getContractId(),selected.getAssetId());
                            if(CopyByInvok.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                                selected.setWhetherDeduct(CopyByInvok.T);
                            }else{
                                if(CopyByInvok.T.equals(selected.getMaintainFirstFlag())){
                                    selected.setWhetherDeduct(CopyByInvok.F);
                                }else{
                                    selected.setWhetherDeduct(CopyByInvok.T);
                                }
                            }
                        }else{
                            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), selected.getAssetInfo().getUid());
                            if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) && CopyByInvok.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                                whetherDeductEdit = true;
                                selected.setWhetherDeduct(CopyByInvok.F);
                            }else if(selected.getSsaKeyFlag() != null && CopyByInvok.T.equals(selected.getSsaKeyFlag())){
                                whetherDeductEdit = true;
                                selected.setWhetherDeduct(CopyByInvok.F);
                            }else if(CopyByInvok.T.equals(selected.getScoreFlag())){
                                whetherDeductEdit = true;
                                selected.setWhetherDeduct(CopyByInvok.T);
                            }else{
                                if(selected.getAssetInfo().getRepairForm() != null && selected.getAssetInfo().getRepairForm() == 2){
                                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                                        whetherDeductEdit = true;
                                        selected.setWhetherDeduct(CopyByInvok.F);
                                    }else{
                                        selected.setWhetherDeduct(CopyByInvok.T);
                                    }
                                }else{
                                    selected.setWhetherDeduct(CopyByInvok.T);
                                }
                            }
                        }
                    }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        if(CopyByInvok.NORMAL_ONE.equals(selected.getArtificialType())){
                            if(CopyByInvok.T.equals(selected.getSsaKeyFlag())){
                                WebUtil.addErrorMessage("代理商申请SSA Key维修派工。","");
                            }
                        }else{
                            if(CopyByInvok.T.equals(selected.getSsaKeyFlag())){
                                WebUtil.addErrorMessage("代理商申请Dongle维修派工。","");
                            }
                        }
                    }
                }
                if(isChoseAdmin || isOrder){
                    ZkbContractAsset zkbContractAsset = zkbPartOrderService.getZkbContractAssetRepository().findFirstByContractIdAndAssetId(selected.getContractId(),selected.getAssetId());
                    if(CopyByInvok.T.equals(selected.getMaintainFlag())){
                        if(CopyByInvok.T.equals(selected.getMaintainFirstFlag())){
                            trainTips = "该设备享有每年一次免费保养，此次保养为第一次保养，不需要扣除名额。";
                        }
                    }else{
                        if(zkbContractAsset != null && CopyByInvok.T.equals(zkbContractAsset.getTrainReasonFlag())){
                            trainTips = "由于代理商个人原因未在规定时间内完成培训，无免费人工";
                        }
                    }
                }
            }
            if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Probe.toString())){
                initRemindLetterFlag();
                if(StringUtils.isNotEmpty(selected.getProbeQuotaType()) && ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                    showCatListFlag = "2";
                }else if(StringUtils.isNotEmpty(selected.getHighCatFlag()) && CopyByInvok.T.equals(selected.getHighCatFlag())){
                    showCatListFlag = "2";
                }else{
                    showCatListFlag = "1";
                }
            }
            if(canEditFlag && CopyByInvok.T.equals(selected.getSupplementFlag())){
                boolean f = false;
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                    f = true;
                }
                if(!ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                    f = true;
                }
                if(f){
                    WebUtil.addErrorMessage("该申请为补单申请，请勿重复下单。","");
                }
            }
        }
    }

    public void fieldRendered(){
        if(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep())){
            if(currentUser.getTenantUID().equals(selected.getTenantUID())){
                canEditFlag = true;
            }else{
                if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                    List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigType(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                    if(zkbPartOrderUserConfigs.size() > 0){
                        for (ZkbPartOrderUserConfig c : zkbPartOrderUserConfigs){
                            if(currentUser.getLoginName().equals(c.getLoginName())){
                                canEditFlag = true;
                            }
                        }
                    }
                }
            }
        }else{
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = new ArrayList<>();
            if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndRegion(selected.getCurrentStep(),selected.getRegion());
            }else if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(selected.getCurrentStep())){
                if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo() != null && selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup() == 2){
                        ZkbPartOrderUserConfig zkbPartOrderUserConfig = selected.getOnlineEngine();
                        if(zkbPartOrderUserConfig != null){
                            zkbPartOrderUserConfigs.add(zkbPartOrderUserConfig);
                        }
                    }else if(selected.getAssetInfo() != null && selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup() == 1){
                        zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetType(selected.getCurrentStep(),selected.getAssetTypeDi());
                    }
                }
            }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndOrderType(selected.getCurrentStep(),selected.getOrderType());
                }else{
                    zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndOrderTypeNull(selected.getCurrentStep());
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
                    if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        showCommercial = true;
                        showBtn = "0";
                    }else{
                        showOrder = true;
                        showBtn = "1";
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                    showCommercial = true;
                    if(canEditFlag){
                        showOrder = true;
                        showBtn = "1";
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                    showCommercial = true;
                    showOrder = false;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        showCommercial = true;
                    }
                    showOrder = true;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        showCommercial = true;
                    }
                    showRejectOrder = true;
                    showBtn = "";
                }
            }else{
                if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus()) && canEditFlag){
                    if(ZkbPartOrderStep.PartOrderStep.ProductPending.toString().equals(selected.getCurrentStep())){
                        showProduct = true;
                    }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        showManage = true;
                    }
                    showBtn = "0";
                }else if(ZkbPartOrder.PartOrderStatus.ProductAccept.toString().equals(selected.getStatus())){
                    showProduct = true;
                    showManage = true;
                    showBtn = "0";
                }else if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(selected.getStatus())){
                    showProduct = true;
                }else if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    if(canEditFlag){
                        if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                            showCommercial = true;
                            showBtn = "0";
                        }else{
                            showOrder = true;
                            showBtn = "1";
                        }
                    }
                }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    showCommercial = true;
                    if(canEditFlag){
                        showOrder = true;
                        showBtn = "1";
                    }
                }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    showCommercial = true;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        showCommercial = true;
                    }
                    showOrder = true;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.FourHundredCancel.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        showCommercial = true;
                    }
                    showOrder = true;
                    showBtn = "";
                }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                    if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                        showProduct = true;
                    }
                    showManage = true;
                    if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                        showCommercial = true;
                    }
                    showRejectOrder = true;
                    showBtn = "";
                }
            }
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
                    if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(selected.getCurrentStep())){
                        showDirector = true;
                        showBtn = "0";
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        showUnOrder = true;
                        showBtn = "1";
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        showOrder = true;
                        showBtn = "1";
                    }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        showCommercial = true;
                        showBtn = "0";
                    }
                }
            } else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
            } else if(ZkbPartOrder.PartOrderStatus.DirectorAccept.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                showDirector = true;
                if(canEditFlag){
                    if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        showUnOrder = true;
                        showBtn = "1";
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        showOrder = true;
                        showBtn = "1";
                    }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(selected.getCurrentStep())){
                        showCommercial = true;
                        showBtn = "0";
                    }
                }
            } else if(ZkbPartOrder.PartOrderStatus.DirectorReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                showDirector = true;
            } else if(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                showCommercial = true;
                if(canEditFlag){
                    if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(selected.getCurrentStep())){
                        showUnOrder = true;
                        showBtn = "1";
                    }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        showOrder = true;
                        showBtn = "1";
                    }
                }
            }else if(ZkbPartOrder.PartOrderStatus.CommercialReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                showCommercial = true;
                showBtn = "";
            } else if(ZkbPartOrder.PartOrderStatus.UnOrdered.toString().equals(selected.getStatus())) {
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                showUnOrder = true;
                if(canEditFlag){
                    showOrder = true;
                    showBtn = "1";
                }
            }else if(ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString().equals(selected.getStatus())) {
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                showRejectOrder = true;
                showBtn="";
            }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                showOrder = true;
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    showUnOrder = true;
                }
                if(canEditFlag){
                    showReturn = true;
                    showBtn = "1";
                }
            } else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                showRejectOrder = true;
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    showUnOrder = true;
                }
            }else if(ZkbPartOrder.PartOrderStatus.PartReturned.toString().equals(selected.getStatus())){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                //if("GT".equals(CopyByInvok.compareDateByYyyyMMdd(selected.getRequestDate(),zkbContract.getEndDate()))){
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    showUnOrder = true;
                }
                showOrder = true;
                if(canEditFlag){
                    showReturn = true;
                    showBtn = "1";
                }else {
                    List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(), ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                    if(zkbPartOrderUserConfigs.size() > 0){
                        showReturn = true;
                    }
                }
            }else if(ZkbPartOrder.PartOrderStatus.Returned.toString().equals(selected.getStatus()) ){
                if(!ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
                    if(selected.getAssetInfo().getFunctionGroup() != null && selected.getAssetInfo().getFunctionGroup() == 1){
                    }else {
                        showTech= true;
                    }
                }
                showManage = true;
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                //if("GT".equals(CopyByInvok.compareDateByYyyyMMdd(selected.getRequestDate(),zkbContract.getEndDate()))){
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    showUnOrder = true;
                }
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
                if(CopyByInvok.F.equals(selected.getCheckApplyNumFlag())){
                    showDirector = true;
                }
                //if("GT".equals(CopyByInvok.compareDateByYyyyMMdd(selected.getRequestDate(),zkbContract.getEndDate()))){
                if(StringUtils.isNotEmpty(selected.getCommercialAuditerUid())){
                    showCommercial = true;
                }
                if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                    showUnOrder = true;
                }
                showOrder = true;
                showReturn = true;
                showClosed = true;
                showBtn = "";
            }
        }
        List<ZkbPartOrderUserConfig> closeUserConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(), ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
        if(closeUserConfigs.size() > 0){
            isChoseAdmin = true;
        }//只有人工申请允许该申请被取消


        List<ZkbPartOrderUserConfig> orderConfigs = zkbPartOrderUserConfigRepository.findByLoginName(currentUser.getLoginName());
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())&& CollectionUtils.isNotEmpty(orderConfigs)){
            String oldStep = selected.getCurrentStep();
            if(orderConfigs.stream().filter(m -> ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(m.getConfigType())).findAny().isPresent() &&
                    oldStep.equals(ZkbPartOrderStep.PartOrderStep.Complete.toString()) &&
                    !ZkbPartOrder.PartOrderStatus.FourHundredCancel.toString().equals(selected.getStatus()) ){
                showBtnFourHundredCancel = true;
            }
        }
        for(ZkbPartOrderUserConfig c : orderConfigs){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(c.getConfigType()) && ZkbPartOrder.OrderType.Probe.toString().equals(c.getOrderType())){
                    isOrder = true;
                    break;
                }
            }else{
                if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(c.getConfigType()) && StringUtils.isEmpty(c.getOrderType())){
                    isOrder = true;
                    break;
                }
            }
        }

        List<ZkbPartOrderUserConfig> unOrderConfigs = zkbPartOrderUserConfigRepository.findByLoginNameAndConfigType(currentUser.getLoginName(), ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
        if(unOrderConfigs.size() > 0){
            isUnOrder = true;
        }
    }

    public boolean scoreFlagRender(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(CopyByInvok.T.equals(selected.getMaintainFlag())){
                return true;
            }else{
                if(!CopyByInvok.NORMAL_TWO.equals(selected.getArtificialType())){
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean isCanModifyUnOrder(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus()) || ZkbPartOrder.PartOrderStatus.UnOrdered.toString().equals(selected.getStatus())){
                if(isUnOrder || isChoseAdmin){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isCanModifyOrder(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                if(isOrder || isChoseAdmin){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isCanModifyClose(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.Closed.toString().equals(selected.getStatus())){
                if(isChoseAdmin){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public void initHighList(String contractId){
        highCatList = zkbPartOrderService.queryHighCatByContract(contractId,true);
    }

    public String closeFieldTitle(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(CopyByInvok.T.equals(selected.getNeedLetterFlag()) ){
                return "探头说明函";
            }
        }
        return "关单信息";
    }

    public void initRemindLetterFlag(){
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByLoginName(currentUser.getLoginName());
        if(zkbPartOrderUserConfigs != null && zkbPartOrderUserConfigs.size() > 0){
            for (ZkbPartOrderUserConfig c : zkbPartOrderUserConfigs){
                if(c.getConfigType().equals(ZkbPartOrderStep.PartOrderStep.ClosePending.toString())){
                    remindLetterFlag = "1";
                    break;
                }
            }
        }
    }

    public void initReturnList(){
        returnTypeList = i18nDao.getByMsgType("zkbPoReturnType");
        if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            List<I18nMessage> removeI18 = new ArrayList<>();
            for(I18nMessage i18nMessage : returnTypeList){
                if(!"DOA".equals(i18nMessage.getMsgKey()) &&
                    !"goodReturn".equals(i18nMessage.getMsgKey()) &&
                    !"cancel".equals(i18nMessage.getMsgKey()) &&
                    !"damagedShipment".equals(i18nMessage.getMsgKey())
                ){
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
        zkbPartOrderService.sendMessage(zkbPartOrder,UserContextService.getAccessToken());
        zkbPartOrderService.sendEmail(zkbPartOrder,"",UserContextService.getAccessToken());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String rejectNext(){
        zkbPartOrderService.auditNext(this.selected,false,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendMessage(zkbPartOrder,UserContextService.getAccessToken());
        zkbPartOrderService.sendEmail(zkbPartOrder,"",UserContextService.getAccessToken());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String closedBack(){
        zkbPartOrderService.closedBack(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatBackMessages(zkbPartOrder);
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String normalCheck(){
        StringBuilder errorLog = new StringBuilder();
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())){
                if(StrUtil.isEmpty(selected.getSrNo())){
                    errorLog.append("SR#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getActivityNo())){
                    errorLog.append("Act#不能为空，\r\n");
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                if(StrUtil.isEmpty(selected.getOrderNo())){
                    errorLog.append("Order#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getPrice())){
                    errorLog.append("价格不能为空，\r\n");
                }
                if(selected.getZkbPartOrderCat() != null && !ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType()) && StrUtil.isEmpty(selected.getNewCatFlag())){
                    errorLog.append("是否全新探头不能为空，\r\n");
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){
                if(StrUtil.isEmpty(selected.getReturnType())){
                    errorLog.append("归还类型不能为空，\r\n");
                }
                if("DOA".equals(selected.getReturnType())){
                    if(StrUtil.isEmpty(selected.getPartSerialNumber())){
                        errorLog.append("Part Serial Number不能为空，\r\n");
                    }
                    if(StrUtil.isEmpty(selected.getPartQualityIssue())){
                        errorLog.append("故障描述不能为空，\r\n");
                    }
                }

                if(StrUtil.isEmpty(selected.getWaybillNo())){
                    errorLog.append("运单号不能为空，\r\n");
                }
                if(selected.getReturnDate() == null){
                    errorLog.append("归还日期不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getNewCatSn())){
                    errorLog.append("新探头SN编号不能为空，\r\n");
                }
            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                if(StrUtil.isEmpty(selected.getSrNo())){
                    errorLog.append("SR#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getActivityNo())){
                    errorLog.append("Act#不能为空，\r\n");
                }
            }
        }
        return errorLog.toString();
    }

    public String normalNext(){
        String checkResult = normalCheck();
        if(StrUtil.isNotEmpty(checkResult)){
            WebUtil.addErrorMessage(checkResult,"");
            return "";
        }

        ZkbContract c = zkbContractRepository.findById(selected.getContractId());
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){
                if(selected.getReturnQty() > selected.getQty()){
                    WebUtil.addErrorMessage("归还数量不能大于申请数量","");
                    return "";
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                if(StringUtils.isEmpty(c.getQuotaDeductType()) || ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(c.getQuotaDeductType())) {
                    if(!selected.getNewCatFlag().equals(selected.getTenantNewCatFlag())){
                        if (CopyByInvok.T.equals(selected.getNewCatFlag())) {
                            if(CopyByInvok.T.equals(selected.getNewCatFlag()) && StringUtils.isEmpty(selected.getProbeQuotaType())){
                                if(c.getProbeQuota() < selected.getQty()){
                                    WebUtil.addErrorMessage("探头剩余名额不足，请联系供应商！","");
                                    return "";
                                }
                            }
                        }
                        if(CopyByInvok.T.equals(zkbContract.getAssetQuotaType())){
                            if (CopyByInvok.T.equals(selected.getNewCatFlag())) {
                                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),selected.getAssetId());
                                boolean r = zkbPartOrderService.checkQuotaHistory("order",zkbContract, selected, zkbContractAsset, ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString());
                                if(!r){
                                    if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                                        WebUtil.addErrorMessage("该合同中的高端机名额不足,请检查！","");

                                    }else{
                                        WebUtil.addErrorMessage("该合同中的低端机名额不足,请检查！","");
                                    }
                                    return "";
                                }
                            }
                        }
                    }
                    if(c.getProbeQuotaApply() < selected.getQty()){
                        WebUtil.addErrorMessage("探头申请名额不足，请联系供应商！","");
                        return "";
                    }
                }else{
                    if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                        if(c.getProbeQuotaApply() < selected.getQty()){
                            WebUtil.addErrorMessage("探头任意申请名额不足，请联系供应商！","");
                            return "";
                        }
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                        if(c.getProbeQuotaNormalApply() < selected.getQty()){
                            WebUtil.addErrorMessage("探头普通申请名额不足，请联系供应商！","");
                            return "";
                        }
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                        if(c.getProbeQuotaSpecialApply() < selected.getQty()){
                            WebUtil.addErrorMessage("探头特殊申请名额不足，请联系供应商！","");
                            return "";
                        }
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())) {
                        if (CopyByInvok.FOUR_TO_ONE.equals(selected.getContractCatId())) {
                            if(c.getProbeQuotaNormalApply() < selected.getQty()){
                                WebUtil.addErrorMessage("探头高值名额不足，请联系供应商！","");
                                return "";
                            }
                        }
                    }
                }
            }
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())){

            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                boolean flag = true;
                String errorLog = "";
                Integer t= 0;
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
                    if(StringUtils.isEmpty(zkbPartOrderItem.getOrderNo())){
                        flag = false;
                        errorLog += "Order#不能为空,";
                    }
                    if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) && StringUtils.isEmpty(zkbPartOrderItem.getActualPartNo())){
                        flag = false;
                        errorLog += "实际配件号不能为空,";
                    }
                    if(zkbPartOrderItem.getPrePartQty() > zkbPartOrderItem.getPartQty()){
                        if(StringUtils.isEmpty(zkbPartOrderItem.getRemark())){
                            flag = false;
                            errorLog += "配件号为"+zkbPartOrderItem.getPartNo()+"的实际配件数量修改后备注不能为空,";
                        }
                    }
                    t += zkbPartOrderItem.getPartQty();
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
                if(t == 0){
                    WebUtil.addErrorMessage("实际配件数量不能全部为0","");
                    return "";
                }
                ZkbContractAsset zkbContractAsset = zkbPartOrderService.getZkbContractAssetRepository().findFirstByContractIdAndAssetId(zkbContract.getId(),selected.getAssetId());
                if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){

                }else {
                    if (ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                        if(c.getSpareQuotaApply() < selected.getQty()){
                            WebUtil.addErrorMessage("备件申请名额不足，请联系供应商！","");
                            return "";
                        }
                    }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                        if(selected.getAssetInfo().getAssetGroup() != null && selected.getAssetInfo().getAssetGroup().equals(1)){
                            if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                                if(c.getPartHighQuotaApply() < selected.getQty()){
                                    WebUtil.addErrorMessage("高值备件申请名额不足，请联系供应商！","");
                                    return "";
                                }
                            }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                                if(c.getSpareArtificailApply() < selected.getQty()){
                                    WebUtil.addErrorMessage("联合申请名额不足，请联系供应商！","");
                                    return "";
                                }
                            }else{
                                if(c.getSpareArtificailApply() < selected.getQty()){
                                    WebUtil.addErrorMessage("联合申请名额不足，请联系供应商！","");
                                    return "";
                                }
                            }
                        }else{
                            if(c.getSpareArtificailApply() < selected.getQty()){
                                WebUtil.addErrorMessage("联合申请名额不足，请联系供应商！","");
                                return "";
                            }
                        }
                    }else{
                        if(c.getSpareArtificailApply() < selected.getQty()){
                            WebUtil.addErrorMessage("联合申请名额不足，请联系供应商！","");
                            return "";
                        }
                    }
                }
                selected.setQty(t);
            }
        }
        if((ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType()))
                && ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep()) && userContextService.isZkbUser()){
            List<ZkbPartOrderReturn> currentZkbPartOrderReturns = zkbPartOrderReturnRepository.findByPartOrderIdAndModifyFlag(selected.getId(),ZkbPartOrderReturn.ModifyFlagEnum.Y.toString());
            if(currentZkbPartOrderReturns ==null || currentZkbPartOrderReturns.size() ==0){
                WebUtil.addErrorMessage("请填写归还数据","");
                return "";
            }
            zkbPartOrderService.batchesReturnNext(selected,currentZkbPartOrderReturns,currentUser);
        } else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) && isChoseAdmin){
            if(ZkbPartOrder.PartOrderStatus.PartReturned.toString().equals(selected.getStatus()) || ZkbPartOrder.PartOrderStatus.Returned.toString().equals(selected.getStatus())){
                List<ZkbPartOrderReturn> auditReturnList = new ArrayList<>();

                for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
                    if(ZkbPartOrderReturn.ModifyFlagEnum.Y.toString().equals(r.getModifyFlag())){
                        auditReturnList.add(r);
                    }
                }
                if(auditReturnList.size() == 0){
                    WebUtil.addErrorMessage("请填写审批数据","");
                    return "";
                }
                zkbPartOrderService.returnAudit(selected,auditReturnList,currentUser,zkbPartOrderReturnList);

                selected = zkbPartOrderRepository.findById(selected.getId());
            }
        } else {
            zkbPartOrderService.normalNext(this.selected,currentUser);
        }
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(zkbPartOrder.getCurrentStep())){
            //归还时发送邮件
            zkbPartOrderService.sendEmail(zkbPartOrder,"",UserContextService.getAccessToken());
        }
        if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrderService.sendEmail(zkbPartOrder,"",UserContextService.getAccessToken());
        }
        if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType()) && ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrderService.sendEmail(zkbPartOrder, CopyByInvok.EMAIL_URL_TYPE_UNORDER,UserContextService.getAccessToken());
        }
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String orderCancelNext(){
        zkbPartOrderService.orderCancelNext(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String fourHundredCancel(){
        zkbPartOrderService.fourHundredCancelNext(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }



    public void preModifyApply(){
        modifySsaKey = selected.getSsaKeyFlag();
    }

    public void saveModifyApply(){
        selected.setSsaKeyFlag(modifySsaKey);
        zkbPartOrderService.saveModifyApply(selected,currentUser);
        selected = zkbPartOrderRepository.findById(selected.getId());
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), selected.getAssetInfo().getUid());
        if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) && CopyByInvok.T.equals(zkbContractAsset.getSupportMaintainFlag())){
            whetherDeductEdit = true;
            selected.setWhetherDeduct(CopyByInvok.F);
        }else if(selected.getSsaKeyFlag() != null && CopyByInvok.T.equals(selected.getSsaKeyFlag())){
            whetherDeductEdit = true;
            selected.setWhetherDeduct(CopyByInvok.F);
        }else {
            whetherDeductEdit = false;
        }
    }

    public String dialogComplete(){
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public void prepareReturnCreate(){
        this.actionReturnName = "Create";
        this.zkbPartOrderReturn = new ZkbPartOrderReturn();
        zkbPartOrderReturn.setReturnDate(new Date());
        zkbPartOrderReturn.setPartOrderId(selected.getId());
        initPartList();
    }

    public void prepareReturnEdit(){
        this.actionReturnName = "Edit";
        initPartList();
    }

    public void prepareReturnModifyCreate(){
        this.actionReturnName = "ModifyCreate";
        zkbPartOrderReturn = new ZkbPartOrderReturn();
        zkbPartOrderReturn.setReturnDate(new Date());
        zkbPartOrderReturn.setPartOrderId(selected.getId());
        initPartList();
        System.out.println(partNoList);
    }

    public void prepareReturnModifyEdit(){
        this.actionReturnName = "ModifyEdit";
        initPartList();
    }

    public void deleteReturnModify(){
        zkbPartOrderReturnModifyList.remove(zkbPartOrderReturn);
        zkbPartOrderReturn = null;
    }

    public void initPartList(){
        partNoList = new ArrayList<>();
        orderNoList = new ArrayList<>();
        List<ZkbPartOrderItem> zkbPartOrderItemList = zkbPartOrderService.queryItemBuPartOrderId(selected.getId());
        List<String> orderContains = new ArrayList<>();
        for(ZkbPartOrderItem i :zkbPartOrderItemList){
            HashMap<String, String> m = new HashMap<>();
            m.put("text",i.getPartName());
            m.put("value",i.getPartNo());
            partNoList.add(m);
            if(StringUtils.isNotEmpty(i.getOrderNo())){
                if(!orderContains.contains(i.getOrderNo())){
                    HashMap<String, String> mm = new HashMap<>();
                    mm.put("text",i.getOrderNo());
                    mm.put("value",i.getOrderNo());
                    orderNoList.add(mm);
                    orderContains.add(i.getOrderNo());
                }
            }
        }
    }

    public void deleteReturn(){
        zkbPartOrderReturnRepository.delete(zkbPartOrderReturn);
        zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId(),false);
    }

    public void saveReturn(){
        if(!"DOA".equals(zkbPartOrderReturn.getReturnType())){
            zkbPartOrderReturn.setPartSerialNumber(null);
            zkbPartOrderReturn.setPartQualityIssue(null);
        }
        if("Create".equals(actionReturnName) || "Edit".equals(actionReturnName)){
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
                zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId(),isChoseAdmin);
                return;
            }
            if(StringUtils.isEmpty(zkbPartOrderReturn.getId())){
                zkbPartOrderReturn.setCreatedDate(new Date());
            }else{
                zkbPartOrderReturn.setUpdatedDate(new Date());
            }
            zkbPartOrderReturn.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.Y.toString());
            zkbPartOrderReturn.setDataStatus(null);
            zkbPartOrderReturnRepository.save(zkbPartOrderReturn);

            saveTips = "备件归还信息已添加，提交申请后流程才会进入Admin确认阶段，请在添加完需要已归还的备件信息后提交申请，以免影响您后续的进度，感谢您的配合！";
        }else if("ModifyCreate".equals(actionReturnName)){
            zkbPartOrderReturn.setId(UUID.randomUUID().toString());
            zkbPartOrderReturnModifyList.add(zkbPartOrderReturn);
        }
        zkbPartOrderReturn = null;
        zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId(),isChoseAdmin);
    }

    private boolean haveTrainCourseFlag = false;
    public void trainCourseRender(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType) && "1".equals(selected.getArtificialType())){
            if(zkbContract != null ){
                if(ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType())){
                    haveTrainCourseFlag = true;
                }
            }
        }
    }

    public void preHistoryData(){
        conditionMap = new HashMap<>();
        zkbHistoryDataList = new ArrayList<>();
        geHistoryDataList = new ArrayList<>();
    }

    public void queryHistoryData(){
        List<SearchFilter> filters = new ArrayList<>();
        for (Map.Entry<String, String> entry : conditionMap.entrySet()) {
            if(StringUtils.isEmpty(entry.getValue())){
                continue;
            }
            SearchFilter filter = new SearchFilter(entry.getKey(), SearchFilter.Operator.LIKE, entry.getValue());
            filters.add(filter);
        }
        zkbHistoryDataList = zkbPartOrderService.queryZkbHistoryByCondition(filters,selected.getTenantUID());
        geHistoryDataList = zkbPartOrderService.queryGeHistoryByCondition(filters,selected.getTenantUID());
    }

    public void handleFileUpload(FileUploadEvent event) {
        boolean b = zkbPartOrderService.uploadFile(event,selected);
        if(b){
            selected = zkbPartOrderRepository.findById(selected.getId());
            zkbPartOrderService.sendEmailForProbeLetter(selected);
            WebUtil.addSuccessMessage(selected.getUploadLetterFileName() + WebUtil.getMessage("SuccessUploaded"));
        }else{
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUpload2(){
        selected.setUploadLetterFlag("t");
        zkbPartOrderRepository.save(selected);
    }

    public void remindLetter(){
        boolean r = zkbPartOrderService.sendWeChatRemindLetter(selected);
        if(r){
            remindLetterFlag = "2";
            WebUtil.addSuccessMessage("催函成功！");
        }else{
            WebUtil.addErrorMessage("催函失败！");
        }
    }

    //region 关单修改归还数据
    public void preReturnModifyEdit(){
        actionReturnName = "ModifyEdit";
        zkbPartOrderReturn = null;

        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            zkbPartOrderReturnModifyList = zkbPartOrderService.getZkbPartOrderReturnRepository().findByPartOrderId(selected.getId());
            partNoList = new ArrayList<>();
            orderNoList = new ArrayList<>();
            List<ZkbPartOrderItem> zkbPartOrderItemList = zkbPartOrderService.queryItemBuPartOrderId(selected.getId());
            List<String> orderContains = new ArrayList<>();
            for(ZkbPartOrderItem i :zkbPartOrderItemList){
                HashMap<String, String> m = new HashMap<>();
                m.put("text",i.getPartName());
                m.put("value",i.getPartNo());
                partNoList.add(m);
                if(StringUtils.isNotEmpty(i.getOrderNo())){
                    if(!orderContains.contains(i.getOrderNo())){
                        HashMap<String, String> mm = new HashMap<>();
                        mm.put("text",i.getOrderNo());
                        mm.put("value",i.getOrderNo());
                        orderNoList.add(mm);
                        orderContains.add(i.getOrderNo());
                    }
                }
            }
        }

    }

    public void returnModifyCancel(){
        zkbPartOrderReturn = null;
    }

    public void saveReturnModify(){
        if("ModifyCreate".equals(actionReturnName)){
            zkbPartOrderReturnModifyList.add(zkbPartOrderReturn);
        }else{
           /* for(ZkbPartOrderReturn r : zkbPartOrderReturnModifyList){
                if (r.getId().equals(zkbPartOrderReturnModify.getId())){
                    Collections.replaceAll(zkbPartOrderReturnModifyList,r,zkbPartOrderReturnModify);
                    break;
                }
            }*/
        }
        zkbPartOrderReturn = null;
    }

    public void submitCloseReturn(){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
            for(ZkbPartOrderReturn r : zkbPartOrderReturnModifyList){
                if(returnMap.containsKey(r.getPartNo())){
                    Integer vv = returnMap.get(r.getPartNo());
                    vv += r.getQty();
                    returnMap.put(r.getPartNo(),vv);
                }else{
                    returnMap.put(r.getPartNo(),r.getQty());
                }
            }
            boolean flag = true;
            for(ZkbPartOrderItem zkbPartOrderItem : selected.getZkbPartOrderItems()){
                Integer returnQty = returnMap.get(zkbPartOrderItem.getPartNo());
                if(returnQty == null){
                    flag = false;
                }else if(returnQty != zkbPartOrderItem.getPartQty()){
                    flag = false;
                }
            }
            if(!flag){
                WebUtil.addErrorMessage("归还数量与申请不一致，请检查！");
                return;
            }
            boolean arg = zkbPartOrderService.submitCloseReturn(zkbPartOrderReturnModifyList,selected,currentUser);
            if(arg){
                WebUtil.addErrorMessage("归还类型已修改，剩余名额不足，需400手动处理！","");
             }
            zkbPartOrderReturnList = zkbPartOrderService.getZkbPartOrderReturnRepository().findByPartOrderId(selected.getId());
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){

            boolean arg = zkbPartOrderService.probeModifyReturn(selected,currentUser);
            if(arg){
                WebUtil.addErrorMessage("归还类型已修改，剩余名额不足，需400手动处理！","");
             }
        }

    }
    //endregion

    //region  关单审批归还
    public void onRowEditReturn(RowEditEvent event){
        ZkbPartOrderReturn r = (ZkbPartOrderReturn)event.getObject();
        if(r.getDataStatus().equals(ZkbPartOrderReturn.DataStatusEnum.Pending)){
            r.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.N.toString());
        }else{
            r.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.Y.toString());
        }
    }


    //endregion

    public void preLogistics(String id){
        ZkbPartOrderItem se = null;
        for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
            if(id.equals(i.getId())){
                se = i;
                break;
            }
        }
        if(se == null){
            return;
        }
        if(StringUtils.isEmpty(se.getOrderNo())){
            WebUtil.addErrorMessage("Order#不能为空！");
            return;
        }
        if(StringUtils.isEmpty(se.getPartNo())){
            WebUtil.addErrorMessage("配件号不能为空！");
            return;
        }
        svcEngineLogisticsLineVOList = zkbPartOrderService.querySvcLogistics(selected.getSrNo(),se.getOrderNo(),se.getPartNo());
        if(svcEngineLogisticsLineVOList == null || svcEngineLogisticsLineVOList.size() == 0){
            WebUtil.addErrorMessage("查询结果为空！");
        }
    }

    public void prepareModify(String m){
        modifyType = m;
        if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(m)){
            modifyTitle = "下单数据修改";

        }else if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(m)){
            modifyTitle = "开单数据修改";
        }
    }

    public String saveOrderModify(){
        boolean flag = true;
        String errorLog = "";
        Integer t= 0;
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
            if(StringUtils.isEmpty(zkbPartOrderItem.getOrderNo())){
                flag = false;
                errorLog += "Order#不能为空,";
            }
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) && StringUtils.isEmpty(zkbPartOrderItem.getActualPartNo())){
                flag = false;
                errorLog += "实际配件号不能为空,";
            }
            if(zkbPartOrderItem.getPrePartQty() > zkbPartOrderItem.getPartQty()){
                if(StringUtils.isEmpty(zkbPartOrderItem.getRemark())){
                    flag = false;
                    errorLog += "配件号为"+zkbPartOrderItem.getPartNo()+"的实际配件数量修改后备注不能为空,";
                }
            }
            t += zkbPartOrderItem.getPartQty();
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
        zkbPartOrderService.saveOrderModify(selected,currentUser,modifyType);
        selected = zkbPartOrderRepository.findById(selected.getId());
        return "AuditStep?faces-redirect=true&str="+str;
    }

    public String closeOrderModify(){
        return "AuditStep?faces-redirect=true&str="+str;
    }

    public String probeCloseReject() throws InterruptedException {
        zkbPartOrderService.probeCloseReject(selected, currentUser);
        WebUtil.addErrorMessage("操作成功！","操作成功w2");
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    //region get、set
    public boolean isHaveTrainCourseFlag() {
        return haveTrainCourseFlag;
    }

    public void setHaveTrainCourseFlag(boolean haveTrainCourseFlag) {
        this.haveTrainCourseFlag = haveTrainCourseFlag;
    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String getModifyType() {
        return modifyType;
    }

    public void setModifyType(String modifyType) {
        this.modifyType = modifyType;
    }

    public String getModifyTitle() {
        return modifyTitle;
    }

    public void setModifyTitle(String modifyTitle) {
        this.modifyTitle = modifyTitle;
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

    public String getShowOrderedFieldsetText() {
        return showOrderedFieldsetText;
    }

    public void setShowOrderedFieldsetText(String showOrderedFieldsetText) {
        this.showOrderedFieldsetText = showOrderedFieldsetText;
    }

    public boolean isShowUnOrder() {
        return showUnOrder;
    }

    public void setShowUnOrder(boolean showUnOrder) {
        this.showUnOrder = showUnOrder;
    }

    public boolean isCanEditFlag() {
        return canEditFlag;
    }

    public void setCanEditFlag(boolean canEditFlag) {
        this.canEditFlag = canEditFlag;
    }

    public boolean isShowRejectOrder() {
        return showRejectOrder;
    }

    public void setShowRejectOrder(boolean showRejectOrder) {
        this.showRejectOrder = showRejectOrder;
    }

    public List<ZkbPartOrderItemVo> getZkbPartOrderItemVoList() {
        return zkbPartOrderItemVoList;
    }

    public void setZkbPartOrderItemVoList(List<ZkbPartOrderItemVo> zkbPartOrderItemVoList) {
        this.zkbPartOrderItemVoList = zkbPartOrderItemVoList;
    }

    public List<HashMap<String, Object>> getV2_listHashMap() {
        return v2_listHashMap;
    }

    public void setV2_listHashMap(List<HashMap<String, Object>> v2_listHashMap) {
        this.v2_listHashMap = v2_listHashMap;
    }

    public List<ZkbPoHistoryQueryVo> getZkbHistoryDataList() {
        return zkbHistoryDataList;
    }

    public void setZkbHistoryDataList(List<ZkbPoHistoryQueryVo> zkbHistoryDataList) {
        this.zkbHistoryDataList = zkbHistoryDataList;
    }

    public List<ZkbPoHistoryQueryVo> getGeHistoryDataList() {
        return geHistoryDataList;
    }

    public void setGeHistoryDataList(List<ZkbPoHistoryQueryVo> geHistoryDataList) {
        this.geHistoryDataList = geHistoryDataList;
    }

    public HashMap<String, String> getConditionMap() {
        return conditionMap;
    }

    public void setConditionMap(HashMap<String, String> conditionMap) {
        this.conditionMap = conditionMap;
    }

    public List<HashMap<String, String>> getOrderNoList() {
        return orderNoList;
    }

    public void setOrderNoList(List<HashMap<String, String>> orderNoList) {
        this.orderNoList = orderNoList;
    }

    public String getRemindLetterFlag() {
        return remindLetterFlag;
    }

    public void setRemindLetterFlag(String remindLetterFlag) {
        this.remindLetterFlag = remindLetterFlag;
    }

    public TenantInfo getTenantInfo() {
        return tenantInfo;
    }

    public void setTenantInfo(TenantInfo tenantInfo) {
        this.tenantInfo = tenantInfo;
    }

    public boolean isShowDirector() {
        return showDirector;
    }

    public void setShowDirector(boolean showDirector) {
        this.showDirector = showDirector;
    }

    public List<ZkbPartOrderReturn> getZkbPartOrderReturnModifyList() {
        return zkbPartOrderReturnModifyList;
    }

    public void setZkbPartOrderReturnModifyList(List<ZkbPartOrderReturn> zkbPartOrderReturnModifyList) {
        this.zkbPartOrderReturnModifyList = zkbPartOrderReturnModifyList;
    }

    public boolean isChoseAdmin() {
        return isChoseAdmin;
    }

    public void setChoseAdmin(boolean choseAdmin) {
        isChoseAdmin = choseAdmin;
    }

    public String getSaveTips() {
        return saveTips;
    }

    public void setSaveTips(String saveTips) {
        this.saveTips = saveTips;
    }

    public String getLetterTips() {
        return letterTips;
    }

    public void setLetterTips(String letterTips) {
        this.letterTips = letterTips;
    }

    public List<HashMap<String, String>> getHighCatList() {
        return highCatList;
    }

    public void setHighCatList(List<HashMap<String, String>> highCatList) {
        this.highCatList = highCatList;
    }
    public String getShowCatListFlag() {
        return showCatListFlag;
    }

    public void setShowCatListFlag(String showCatListFlag) {
        this.showCatListFlag = showCatListFlag;
    }

    public List<SvcEngineLogisticsLineVO> getSvcEngineLogisticsLineVOList() {
        return svcEngineLogisticsLineVOList;
    }

    public void setSvcEngineLogisticsLineVOList(List<SvcEngineLogisticsLineVO> svcEngineLogisticsLineVOList) {
        this.svcEngineLogisticsLineVOList = svcEngineLogisticsLineVOList;
    }

    public List<V2_BlobObject> getV2_probe_blobObjectList() {
        return v2_probe_blobObjectList;
    }

    public void setV2_probe_blobObjectList(List<V2_BlobObject> v2_probe_blobObjectList) {
        this.v2_probe_blobObjectList = v2_probe_blobObjectList;
    }

    public List<ZkbContract> getContractList() {
        return contractList;
    }

    public void setContractList(List<ZkbContract> contractList) {
        this.contractList = contractList;
    }

    public boolean isShowCommercial() {
        return showCommercial;
    }

    public void setShowCommercial(boolean showCommercial) {
        this.showCommercial = showCommercial;
    }

    public boolean isOrder() {
        return isOrder;
    }

    public void setOrder(boolean order) {
        isOrder = order;
    }

    public boolean isUnOrder() {
        return isUnOrder;
    }

    public void setUnOrder(boolean unOrder) {
        isUnOrder = unOrder;
    }

    //endregion

    public boolean isWhetherDeductEdit() {
        return whetherDeductEdit;
    }

    public void setWhetherDeductEdit(boolean whetherDeductEdit) {
        this.whetherDeductEdit = whetherDeductEdit;
    }

    public String getTrainTips() {
        return trainTips;
    }

    public void setTrainTips(String trainTips) {
        this.trainTips = trainTips;
    }

    public String getModifySsaKey() {
        return modifySsaKey;
    }

    public void setModifySsaKey(String modifySsaKey) {
        this.modifySsaKey = modifySsaKey;
    }

    public boolean isShowProduct() {
        return showProduct;
    }

    public void setShowProduct(boolean showProduct) {
        this.showProduct = showProduct;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return zkbPartOrderService.getZkbPartOrderRepository();
    }

    public boolean isShowBtnFourHundredCancel() {
        return showBtnFourHundredCancel;
    }

    public void setShowBtnFourHundredCancel(boolean showBtnFourHundredCancel) {
        this.showBtnFourHundredCancel = showBtnFourHundredCancel;
    }
}
