package com.ge.apm.view.zkb;

import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.ZkbPartOrderStatusService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbConstantUtil;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2020/7/6
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderAuditController extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderService zkbPartOrderService;
    private ZkbPartOrderStatusService zkbPartOrderStatusService;
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
    @Getter@Setter
    private ZkbPartOrderStatusVo zkbPartOrderStatusVo;
    private List<ZkbPartOrderCat> zkbPartOrderCatList ;
    private ZkbPartOrderReturn zkbPartOrderReturn = new ZkbPartOrderReturn();
    private List<ZkbPartOrderReturn> zkbPartOrderReturnList;
    private String actionReturnName;
    private String returnFormFlag = "edit"; //edit  audit
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;
    private ZkbContractAssetRepository zkbContractAssetRepository;
    private ZkbPartOrderCloseInfoRepository zkbPartOrderCloseInfoRepository;
    private ZkbPartOrderRepository zkbPartOrderRepository;
    private List<HashMap<String, Object>> v2_listHashMap;
    private TenantInfo tenantInfo;
    private TenantInfoRepository tenantInfoRepository;
    private BlobFileService blobService;

    private List<HashMap<String, String>> partNoList = new ArrayList<>();
    private List<HashMap<String, String>> orderNoList = new ArrayList<>();
    private List<I18nMessage> returnTypeList= new ArrayList<>();
    private String queryStatus;
    private String nt = "";
    private List<ZkbPartOrderItemVo> zkbPartOrderItemVoList = new ArrayList<>();
    private HashMap<String,String> conditionMap = new HashMap<>();
    private List<ZkbPoHistoryQueryVo> zkbHistoryDataList;
    private List<ZkbPoHistoryQueryVo> geHistoryDataList;
    private String remindLetterFlag = "0";
    private List<ZkbPartOrderReturn> zkbPartOrderReturnModifyList;
    private List<HashMap<String, String>> highCatList = new ArrayList<>();
    private List<SvcEngineLogisticsLineVO> svcEngineLogisticsLineVOList = new ArrayList<>();

    @Getter@Setter
    private List<ZkbPartOrderCloseWorkTime> zkbPartOrderCloseWorkTimeList = new ArrayList<>();;
    @Getter@Setter
    ZkbPartOrderCloseWorkTime selectZkbPartOrderCloseWorkTime;
    @Getter@Setter
    private List<V2_BlobObject> closeBlobObjectList = new ArrayList<>();
    @Getter@Setter
    private List<V2_BlobObject> universalBlobObjectList = new ArrayList<>();

    private String modifyType="";
    private String modifyTitle="";
    private String str = "";
    private String modifySsaKey;

    private List<V2_BlobObject> goodReturnBlobObjectList = new ArrayList<>();

    @Getter@Setter
    private ZkbPartOrderCloseInfo zkbPartOrderCloseInfo;

    @Override
    protected void init() {
        super.init();
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        zkbPartOrderStatusService = WebUtil.getBean(ZkbPartOrderStatusService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbPartOrderReturnRepository = WebUtil.getBean(ZkbPartOrderReturnRepository.class);
        zkbPartOrderRepository = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        zkbContractAssetRepository = WebUtil.getBean(ZkbContractAssetRepository.class);
        zkbPartOrderCloseInfoRepository = WebUtil.getBean(ZkbPartOrderCloseInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        blobService = WebUtil.getBean(BlobFileService.class);
        str = WebUtil.getRequestParameter("str");
        String encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        queryStatus = UrlEncryptController.getValueFromMap(encodeStr, "queryStatus") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        nt = UrlEncryptController.getValueFromMap(encodeStr, "notGeAsset") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "notGeAsset");
        zkbPartOrderStatusVo = new ZkbPartOrderStatusVo();
        if(StringUtils.isNotEmpty(selectedId)){
            HashMap m = zkbPartOrderService.queryEditInfo(selectedId);
            this.selected = m.get("zkbPartOrder") ==null ? new ZkbPartOrder() : (ZkbPartOrder)m.get("zkbPartOrder");
            Optional.ofNullable(selected.getTenantUID()).ifPresent(t -> tenantInfo = tenantInfoRepository.findByuid(selected.getTenantUID()));
            Optional.ofNullable(this.selected.getOrderType()).ifPresent(t -> orderType = this.selected.getOrderType());
            Optional.ofNullable(this.selected.getArtificialType()).ifPresent(t -> artificialType = this.selected.getArtificialType());
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) && this.getUnrealStatusRender()){
                zkbPartOrderService.getUnrealData(selected);
            }

            zkbContract = m.get("zkbContract") ==null ? null : (ZkbContract)m.get("zkbContract");
            if(zkbContract == null){
                zkbContract = new ZkbContract();
                return;
            }else{
                contractList = new ArrayList<>();
                contractList.add(zkbContract);
            }
            initZknPartCat(zkbContract.getStartDate());
            selectedWorkFlow = m.get("V2_WorkOrder") == null ? null : (V2_WorkOrder)m.get("V2_WorkOrder");
            HashMap<String, Object> mm = m.get("v2") == null ? null : (HashMap<String, Object>)m.get("v2");
            v2_listHashMap = new ArrayList<>();
            if(mm != null){
                v2_listHashMap.add(mm);
            }
            v2_blobObjectList = m.get("v2_blobObjectList") == new ArrayList<>() ? null : (List<V2_BlobObject>)m.get("v2_blobObjectList");
            v2_probe_blobObjectList = m.get("v2_probe_blobObjectList") == null ? new ArrayList<>() : (List<V2_BlobObject>)m.get("v2_probe_blobObjectList");

            List<String> sts = Arrays.asList(ZkbPartOrderStep.PartOrderStep.CompletePending.toString(), ZkbPartOrderStep.PartOrderStep.ClosePending.toString(),ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString(), ZkbPartOrderStep.PartOrderStep.Complete.toString());
            if(sts.contains(selected.getCurrentStep())) {
                zkbPartOrderCloseInfo = zkbPartOrderCloseInfoRepository.findFirstBySrNo(selected.getSrNo());
                if (zkbPartOrderCloseInfo == null) {
                    zkbPartOrderCloseInfo = new ZkbPartOrderCloseInfo();
                } else {
                    closeBlobObjectList = blobService.findBlobObjectsByBoUid(zkbPartOrderCloseInfo.getId());
                    zkbPartOrderCloseWorkTimeList = zkbPartOrderStatusService.getZkbPartOrderCloseWorkTimeList(zkbPartOrderCloseInfo.getId());
                }
            }

            if(this.selected.getZkbPartOrderCat() != null){
                if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                    zkbPartOrderStatusVo.setValueTypeTips("");
                }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                    zkbPartOrderStatusVo.setValueTypeTips("您的选择为高值探头，请确认合同中是否有此权益！");
                }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                    zkbPartOrderStatusVo.setValueTypeTips("");
                }
            }
            if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
                zkbPartOrderStatusVo.setShowOrderedFieldsetText("派工信息");
            }else{
                zkbPartOrderStatusVo.setShowOrderedFieldsetText("订货信息");
            }
            zkbPartOrderStatusService.fieldRendered(currentUser,zkbPartOrderStatusVo,selected,zkbPartOrderCloseInfo);
            zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selectedId,zkbPartOrderStatusVo.isChoseAdmin());

            zkbPartOrderStatusService.initRemainQty(selected, zkbContract);
            zkbPartOrderStatusService.initValues(selected, zkbPartOrderStatusVo);
            returnTypeList = zkbPartOrderStatusService.initReturnList(selected);
            highCatList = zkbPartOrderStatusService.initHighList(zkbContract.getId());

            trainCourseRender();
            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep())){
                this.selected.setReturnQty(this.selected.getQty());
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    this.selected.setNeedLetterFlag(ZkbConstantUtil.F);
                    this.selected.setNewCatFlag(this.selected.getTenantNewCatFlag());
                    this.selected.setProvideNewContractSecond(this.selected.getProvideNewContract());
                }
            }
            if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Parts.toString()) && selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString())){
                List<String> ss = new ArrayList<>();
                for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                    ss.add(i.getPartNo());
                }
                zkbPartOrderItemVoList = zkbPartOrderService.getZkbPartOrderItemRepository().queryHistoryPart(selected.getSrNum(),selected.getId(),selected.getAssetTypeDi());
            }

            if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Parts.toString()) || this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Probe.toString())){
                if(zkbPartOrderStatusVo.isCanEditFlag() && !zkbPartOrderService.checkThreeValid(selected)){
                    WebUtil.addErrorMessage("此订单下已订购三个及以上备件或探头，建议联络技术支持或区经理寻求指导！","");
                }
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    String validString = zkbPartOrderService.checkContractWeekValid(selected,zkbContract);
                    if(StringUtils.isNotBlank(validString)){
                        WebUtil.addErrorMessage(validString,"");
                    }
                    if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(selected.getCurrentStep())){
                        if(StringUtils.isNotEmpty(selected.getCheckApplyNumFlag()) && ZkbConstantUtil.F.equals(selected.getCheckApplyNumFlag())) {
                            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                                //只有非高值探头的时候才会给销售总监进行提示，
                                boolean f = true;
                                if(StrUtil.isEmpty(selected.getProbeQuotaType()) && ZkbConstantUtil.T.equals(selected.getHighCatFlag())){
                                    f =false;
                                }
                                if(StrUtil.isNotEmpty(selected.getProbeQuotaType()) && ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                                    f =false;
                                }
                                if(f){
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
                            }else if(ZkbConstantUtil.F.equals(selected.getHighCatFlag())){
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
                            && ZkbConstantUtil.T.equals(selected.getNeedLetterFlag())
                            && (ZkbConstantUtil.F.equals(selected.getUploadLetterFlag()) || StringUtils.isEmpty(selected.getUploadLetterFlag()))){
                        zkbPartOrderStatusVo.setLetterTips("请尽快邮件提交探头说明函和旧探头序列号照片致400唐扬Yang.Tang@gehealthcare.com。");
                    }
                }
            }else if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Artificial.toString())){
                if(zkbPartOrderStatusVo.isCanEditFlag()){
                    if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(selected.getCurrentStep())){
                        if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                            if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                                selected.setWhetherDeduct(ZkbConstantUtil.F);
                            }else{
                                if(selected.getAssetInfo().getRepairForm() != null && selected.getAssetInfo().getRepairForm() == 2){
                                    zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                                    selected.setWhetherDeduct(ZkbConstantUtil.F);
                                }
                            }
                        }else if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                            zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                            ZkbContractAsset zkbContractAsset = zkbPartOrderService.getZkbContractAssetRepository().findFirstByContractIdAndAssetId(selected.getContractId(),selected.getAssetId());
                            if(ZkbConstantUtil.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                                selected.setWhetherDeduct(ZkbConstantUtil.T);
                            }else{
                                if(ZkbConstantUtil.T.equals(selected.getMaintainFirstFlag())){
                                    selected.setWhetherDeduct(ZkbConstantUtil.F);
                                }else{
                                    selected.setWhetherDeduct(ZkbConstantUtil.T);
                                }
                            }
                            if(ZkbContract.ContractType.CCS.name().equals(zkbContract.getContractType()) ||
                                ZkbContract.ContractType.Biomed.name().equals(zkbContract.getContractType()) ){
                                Date date1 = DateUtil.parse("2024/11/22");
                                if(date1.after(zkbContract.getCreatedDate())){
                                    zkbPartOrderStatusVo.setMaintainTips("该合同需要联系OTR添加 PM active entitlement；");
                                }
                            }
                        }else{
                            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), selected.getAssetInfo().getUid());
                            if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                                selected.setWhetherDeduct(ZkbConstantUtil.F);
                            }else if(selected.getSsaKeyFlag() != null && ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){
                                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                                selected.setWhetherDeduct(ZkbConstantUtil.F);
                            }else if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){
                                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                                selected.setWhetherDeduct(ZkbConstantUtil.T);
                            }else{
                                if(selected.getAssetInfo().getRepairForm() != null && selected.getAssetInfo().getRepairForm() == 2){
                                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                                        zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                                        selected.setWhetherDeduct(ZkbConstantUtil.F);
                                    }else{
                                        selected.setWhetherDeduct(ZkbConstantUtil.T);
                                    }
                                }else{
                                    selected.setWhetherDeduct(ZkbConstantUtil.T);
                                }
                            }
                        }
                    }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
                        if(ZkbConstantUtil.NORMAL_ONE.equals(selected.getArtificialType())){
                            if(ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){
                                WebUtil.addErrorMessage("代理商申请SSA Key维修派工。","");
                            }
                        }else{
                            if(ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){
                                WebUtil.addErrorMessage("代理商申请Dongle维修派工。","");
                            }
                        }
                    }
                }
                if(zkbPartOrderStatusVo.isChoseAdmin() || zkbPartOrderStatusVo.isOrder()){
                    ZkbContractAsset zkbContractAsset = zkbPartOrderService.getZkbContractAssetRepository().findFirstByContractIdAndAssetId(selected.getContractId(),selected.getAssetId());
                    if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                        if(ZkbConstantUtil.T.equals(selected.getMaintainFirstFlag())){
                            zkbPartOrderStatusVo.setTrainTips("该设备享有每年一次免费保养，此次保养为第一次保养，不需要扣除名额。");
                        }
                    }else{
                        if(zkbContractAsset != null && ZkbConstantUtil.T.equals(zkbContractAsset.getTrainReasonFlag())){
                            zkbPartOrderStatusVo.setTrainTips("由于代理商个人原因未在规定时间内完成培训，无免费人工。");
                        }
                    }
                }
            }
            if(this.selected.getOrderType().equals(ZkbPartOrder.OrderType.Probe.toString())){
                initRemindLetterFlag();
                if(StringUtils.isNotEmpty(selected.getProbeQuotaType()) && ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                    zkbPartOrderStatusVo.setShowCatListFlag(ZkbConstantUtil.NORMAL_TWO);
                }else if(StringUtils.isNotEmpty(selected.getHighCatFlag()) && ZkbConstantUtil.T.equals(selected.getHighCatFlag())){
                    zkbPartOrderStatusVo.setShowCatListFlag(ZkbConstantUtil.NORMAL_TWO);
                }else{
                    zkbPartOrderStatusVo.setShowCatListFlag(ZkbConstantUtil.NORMAL_ONE);
                }
                if(getUnrealStatusRender()){
                    zkbPartOrderStatusVo.setUnrealTips(zkbPartOrderService.getProbeUnrealData(selected, zkbPartOrderStatusVo.getShowCatListFlag()));
                }
                if(StrUtil.isNotEmpty(selected.getReturnType()) && selected.getReturnType().equals(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.name())){
                    if(StrUtil.isNotEmpty(selected.getOrderNo()) && selected.getOrderNo().toUpperCase().startsWith("FE")){
                        universalBlobObjectList = zkbPartOrderService.queryBlobBySubType(selectedId);
                    }
                }
            }
            if(zkbPartOrderStatusVo.isCanEditFlag() && ZkbConstantUtil.T.equals(selected.getSupplementFlag())){
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
            if(!currentUser.getTenantUID().equals(selected.getTenantUID())){
                if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType()) && selected.getArtificialType().equals(1)){
                    if(ZkbConstantUtil.T.equals(selected.getWorkStationFlag())){
                        WebUtil.addErrorMessage("当前申请是工作站。","");
                    }
                }
            }

        }
    }

    public boolean whetherDeductRender(){
        boolean result = true;
        if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
            result = false;
        }else if(ZkbConstantUtil.NORMAL_TWO.equals(selected.getArtificialType())){
            if(!ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) && !ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                result = false;
            }
        }

        return result;
    }

    public boolean scoreFlagRender(){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                if(StringUtils.isNotEmpty(selected.getScoreFlag())){
                    return true;
                }
            }else{
                if(!ZkbConstantUtil.NORMAL_TWO.equals(selected.getArtificialType())){
                    if(StringUtils.isNotEmpty(selected.getScoreFlag())){
                        return true;
                    }
                }
                if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && "t".equals(zkbContract.getFirstYearFlag())){
                    return false;
                }else{
                    if(StringUtils.isNotEmpty(selected.getScoreFlag())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkPcbMvsTotal(){
        boolean result = false;
        if(zkbContract != null && (ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType()) ||
                "PCB MVS".equals(zkbContract.getContractType()))){
            int total = zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal();
            if(total >= 1000){
                result = true;
            }
        }
        return result;
    }


    public boolean getUnrealStatusRender(){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(userContextService.isZkbUser()){
                return false;
            }else {
                List<String> configs = new ArrayList<>();
                configs.add(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.ProductPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                configs.add(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.queryForUnrealRoles(currentUser.getLoginName(), configs);
                if(zkbPartOrderUserConfigs != null && zkbPartOrderUserConfigs.size() > 0){
                    return true;
                }else {
                    return false;
                }
            }
        }else{
            return false;
        }

    }

    public String techFieldsetName(){
        if(StrUtil.isNotEmpty(selected.getNotGeAsset()) && ZkbConstantUtil.T.equals(selected.getNotGeAsset())){
            return "TSE审批";
        }else{
            return "技术支持审批";
        }
    }

    public void newCatFlagChange(){
        if(ZkbConstantUtil.T.equals(selected.getNewCatFlag())){
            if(StrUtil.isEmpty(selected.getProvideNewContractSecond())){
                selected.setProvideNewContractSecond(ZkbConstantUtil.T);
            }
        }else {
            selected.setProvideNewContractSecond(null);
        }
    }

    public Boolean isCanModifyUnOrder(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus()) || ZkbPartOrder.PartOrderStatus.UnOrdered.toString().equals(selected.getStatus())){
                if(zkbPartOrderStatusVo.isUnOrder() || zkbPartOrderStatusVo.isChoseAdmin()){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isCanModifyOrder(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                if(zkbPartOrderStatusVo.isOrder() || zkbPartOrderStatusVo.isChoseAdmin()){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isCanModifyClose(){
        //if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ){
            if(ZkbPartOrder.PartOrderStatus.Closed.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.CloseCommited.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.CloseApproved.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.CloseReject.toString().equals(selected.getStatus())
            ){
                if(zkbPartOrderStatusVo.isChoseAdmin()){
                    return Boolean.TRUE;
                }
            }
        //}
        return Boolean.FALSE;
    }



    public String closeFieldTitle(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbConstantUtil.T.equals(selected.getNeedLetterFlag()) ){
                return "探头说明函";
            }
        }
        return "归还审核信息";
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



    private String[] selectedOptions;
    private String unrealReason;
    List<ZkbUnrealVo> unrealItem = new ArrayList<>();
    public void preUnreal(){
        unrealItem = zkbPartOrderService.getUnrealCheckBoxList(selected.getZkbPartOrderItems(),selected.getId(),selected);
    }

    public String saveUnreal(){
        if(selectedOptions == null || selectedOptions.length == 0){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                WebUtil.addErrorMessage("请选择不真实的备件号!");
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                WebUtil.addErrorMessage("请选择不真实的探头号!");
            }
            return "";
        }else{
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }

        zkbPartOrderService.unrealNext(selected, selectedOptions, currentUser,unrealReason);
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            zkbPartOrderService.getUnrealData(selected);
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            zkbPartOrderStatusVo.setUnrealTips(zkbPartOrderService.getProbeUnrealData(selected, zkbPartOrderStatusVo.getShowCatListFlag()));
        }

        selectedOptions = new String[]{};
        unrealReason = "";
        return "";
    }

    public String acceptNext(){
        ZkbPartOrder newZkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        Integer newDataFlag = newZkbPartOrder.getDataFlag() == null ? 0 : newZkbPartOrder.getDataFlag();
        Integer oldDataFlag = selected.getDataFlag() == null ? 0 : selected.getDataFlag();
        if(!Objects.equals(newDataFlag, oldDataFlag)){
            WebUtil.addErrorMessage("数据已被审批！");
            return "";
        }

        if(ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(selected.getCurrentStep())){
            if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                if(StrUtil.isEmpty(selected.getOsscPartNo())){
                    WebUtil.addErrorMessage("备件号不能为空！");
                    return "";
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                if(StrUtil.isEmpty(selected.getRepairFlag())){
                    WebUtil.addErrorMessage("是否能维修不能为空！");
                    return "";
                }else {
                    if(ZkbConstantUtil.T.equals(selected.getRepairFlag())){
                        if(StrUtil.isEmpty(selected.getOsscPartNo())){
                            WebUtil.addErrorMessage("探头号不能为空！");
                            return "";
                        }
                    }
                }
            }

        }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(selected.getCurrentStep())){
            if(zkbPartOrderStatusVo.isHaveTrainCourseFlag()){
                if(StrUtil.isEmpty(selected.getHaveTrainCourseFlag())){
                    WebUtil.addErrorMessage("是否上过培训课不能为空！");
                    return "";
                }
            }
        }
        boolean f = true;
        if(ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(selected.getCurrentStep()) && ZkbPartOrder.OrderType.Probe.equals(selected.getOrderType())){
            if(ZkbConstantUtil.F.equals(selected.getRepairFlag())){
                f = false;
            }
        }
        zkbPartOrderService.auditNext(this.selected,f,currentUser,zkbPartOrderCloseInfo);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendMessage(zkbPartOrder,UserContextService.getAccessToken());
        zkbPartOrderService.sendEmail(zkbPartOrder,"",UserContextService.getAccessToken());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public String rejectNext(){
        ZkbPartOrder newZkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        Integer newDataFlag = newZkbPartOrder.getDataFlag() == null ? 0 : newZkbPartOrder.getDataFlag();
        Integer oldDataFlag = selected.getDataFlag() == null ? 0 : selected.getDataFlag();
        if(!Objects.equals(newDataFlag, oldDataFlag)){
            WebUtil.addErrorMessage("数据已被审批！");
            return "";
        }
        zkbPartOrderService.auditNext(this.selected,false,currentUser, zkbPartOrderCloseInfo);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendMessage(zkbPartOrder,UserContextService.getAccessToken());
        zkbPartOrderService.sendEmail(zkbPartOrder,"",UserContextService.getAccessToken());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public String closedBack(){
        zkbPartOrderService.closedBack(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatBackMessages(zkbPartOrder);
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    /**
     * 关单暂存
     */
    public void tempSaveComplete(){
        zkbPartOrderService.tempSaveComplete(zkbPartOrderCloseInfo, selected, zkbPartOrderCloseWorkTimeList, closeBlobObjectList, currentUser);
    }

    public String normalNextText(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(selected.getCurrentStep()) && zkbPartOrderStatusVo.isCanEditFlag()){
                return "审批通过";
            }
        }
        return "提交";
    }

    public String normalNext(){
        ZkbPartOrder newZkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        Integer newDataFlag = newZkbPartOrder.getDataFlag() == null ? 0 : newZkbPartOrder.getDataFlag();
        Integer oldDataFlag = selected.getDataFlag() == null ? 0 : selected.getDataFlag();
        if(!Objects.equals(newDataFlag, oldDataFlag)){
            WebUtil.addErrorMessage("数据已被操作，不需要重复进行！");
            return "";
        }
        String checkResult = zkbPartOrderService.normalCheck(selected);
        if(StrUtil.isNotEmpty(checkResult)){
            WebUtil.addErrorMessage(checkResult,"");
            return "";
        }

        checkResult = zkbPartOrderStatusService.normalCheckQuota(selected);
        if(StrUtil.isNotEmpty(checkResult)){
            WebUtil.addErrorMessage(checkResult,"");
            return "";
        }
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                Integer t = 0;
                for(ZkbPartOrderItem zkbPartOrderItem : selected.getZkbPartOrderItems()){
                    t += zkbPartOrderItem.getPartQty();
                }
                selected.setQty(t);
            }
        }
        if((ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType()))
                && ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(selected.getCurrentStep()) && userContextService.isZkbUser()){
            List<ZkbPartOrderReturn> currentZkbPartOrderReturns = zkbPartOrderReturnRepository.findByPartOrderIdAndModifyFlag(selected.getId(),ZkbPartOrderReturn.ModifyFlagEnum.Y.toString());
            if(currentZkbPartOrderReturns ==null || currentZkbPartOrderReturns.isEmpty()){
                WebUtil.addErrorMessage("请填写归还数据","");
                return "";
            }
            boolean r = true;
            for(ZkbPartOrderReturn zr : currentZkbPartOrderReturns){
                if(StrUtil.isEmpty(zr.getItemId())){
                    r = false;
                    break;
                }
            }
            if(!r){
                WebUtil.addErrorMessage("归还数据填写有误，请检查！","");
                return "";
            }
            zkbPartOrderService.batchesReturnNext(selected,currentZkbPartOrderReturns,currentUser);
        } else if((ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())) && zkbPartOrderStatusVo.isChoseAdmin()){
            if(ZkbPartOrder.PartOrderStatus.PartReturned.toString().equals(selected.getStatus()) || ZkbPartOrder.PartOrderStatus.Returned.toString().equals(selected.getStatus())){
                List<ZkbPartOrderReturn> auditReturnList = zkbPartOrderReturnList.stream().filter(t -> ZkbPartOrderReturn.ModifyFlagEnum.Y.toString().equals(t.getModifyFlag())).collect(Collectors.toList());
               /* for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
                    if(ZkbPartOrderReturn.ModifyFlagEnum.Y.toString().equals(r.getModifyFlag())){
                        auditReturnList.add(r);
                    }
                }*/
                if(auditReturnList.size() == 0){
                    WebUtil.addErrorMessage("请填写审批数据","");
                    return "";
                }
                zkbPartOrderService.returnAudit(selected,auditReturnList,currentUser,zkbPartOrderReturnList);
                selected = zkbPartOrderRepository.findById(selected.getId());
            }
        } else if(ZkbPartOrderStep.PartOrderStep.CompletePending.toString().equals(selected.getCurrentStep())){
            if(!zkbPartOrderStatusService.checkComplete(selected, zkbPartOrderCloseWorkTimeList, closeBlobObjectList, zkbPartOrderCloseInfo)){
                return "";
            }
            zkbPartOrderService.normalNextComplete(zkbPartOrderCloseInfo,selected,zkbPartOrderCloseWorkTimeList, closeBlobObjectList,currentUser);
            //发送邮件给关单审核人
            zkbPartOrderService.sendEmail(selected, "", UserContextService.getAccessToken());
        } else if(ZkbPartOrderStep.PartOrderStep.ReturnPending.name().equals(selected.getCurrentStep()) && ZkbPartOrder.OrderType.Probe.name().equals(selected.getOrderType())){
            if(selected.getOrderNo().toUpperCase().startsWith("FE") && ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.name().equals(selected.getReturnType())){
                List<V2_BlobObject> newBlobList = queryBlobByType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN_NEW_CAT);
                if(CollectionUtils.isEmpty(newBlobList)){
                    WebUtil.addErrorMessage("新探头序列号照片未上传！","");
                    return "";
                }
                List<V2_BlobObject> oldBlobList = queryBlobByType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN_OLD_CAT);
                if(CollectionUtils.isEmpty(oldBlobList)){
                    WebUtil.addErrorMessage("旧探头序列号照片未上传！","");
                    return "";
                }
            }
            zkbPartOrderService.normalNext(this.selected,currentUser,zkbPartOrderCloseInfo);
            if(CollectionUtils.isNotEmpty(universalBlobObjectList)){
                zkbPartOrderService.saveBlob(universalBlobObjectList, selected.getId());
            }
        }else {
            zkbPartOrderService.normalNext(this.selected,currentUser,zkbPartOrderCloseInfo);
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
        if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType()) &&
            (ZkbPartOrderStep.PartOrderStep.ReturnPending.toString().equals(zkbPartOrder.getCurrentStep()) || ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString().equals(zkbPartOrder.getCurrentStep()))){
            zkbPartOrderService.sendEmail(zkbPartOrder, ZkbConstantUtil.EMAIL_URL_TYPE_UNORDER,UserContextService.getAccessToken());
        }
        //推送工程师、系统激活专用角色微信
        if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(selected.getStatus())){
                List<ZkbPartOrder> zkbPartOrders = zkbPartOrderRepository.queryBySrNo(zkbPartOrder.getSrNo(), zkbPartOrder.getId());
                if(CollectionUtils.isEmpty(zkbPartOrders)){
                    zkbPartOrderService.sendEmailToEngineer(zkbPartOrder,UserContextService.getAccessToken());
                }
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbPartOrder.PartOrderStatus.UnOrdered.toString().equals(selected.getStatus())){
                List<ZkbPartOrder> zkbPartOrders = zkbPartOrderRepository.queryBySrNo(zkbPartOrder.getSrNo(), zkbPartOrder.getId());
                if(CollectionUtils.isEmpty(zkbPartOrders)){
                    zkbPartOrderService.sendEmailToEngineer(zkbPartOrder,UserContextService.getAccessToken());
                }
            }
        }
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public String orderCancelNext(){
        ZkbPartOrder newZkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        Integer newDataFlag = newZkbPartOrder.getDataFlag() == null ? 0 : newZkbPartOrder.getDataFlag();
        Integer oldDataFlag = selected.getDataFlag() == null ? 0 : selected.getDataFlag();
        if(!Objects.equals(newDataFlag, oldDataFlag)){
            WebUtil.addErrorMessage("数据已被审批！");
            return "";
        }
        zkbPartOrderService.orderCancelNext(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public String fourHundredCancel(){
        ZkbPartOrder newZkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        Integer newDataFlag = newZkbPartOrder.getDataFlag() == null ? 0 : newZkbPartOrder.getDataFlag();
        Integer oldDataFlag = selected.getDataFlag() == null ? 0 : selected.getDataFlag();
        if(!Objects.equals(newDataFlag, oldDataFlag)){
            WebUtil.addErrorMessage("数据已被审批！");
            return "";
        }
        zkbPartOrderService.fourHundredCancelNext(this.selected,currentUser);
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(selected.getId());
        zkbPartOrderService.sendWeChatMessages(zkbPartOrder,UserContextService.getAccessToken());
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public void preModifyApply(){
        modifySsaKey = selected.getSsaKeyFlag();
    }

    public void saveModifyApply(){
        selected.setSsaKeyFlag(modifySsaKey);
        zkbPartOrderService.saveModifyApply(selected,currentUser);
        selected = zkbPartOrderRepository.findById(selected.getId());
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), selected.getAssetInfo().getUid());
        if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
            zkbPartOrderStatusVo.setWhetherDeductEdit(true);
            selected.setWhetherDeduct(ZkbConstantUtil.F);
        }else if(selected.getSsaKeyFlag() != null && ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){
            zkbPartOrderStatusVo.setWhetherDeductEdit(true);
            selected.setWhetherDeduct(ZkbConstantUtil.F);
        }else {
            zkbPartOrderStatusVo.setWhetherDeductEdit(false);
        }
    }

    public void prepareReturnCreate(){
        this.actionReturnName = "Create";
        this.returnFormFlag = "edit";
        this.zkbPartOrderReturn = new ZkbPartOrderReturn();
        zkbPartOrderReturn.setReturnDate(new Date());
        zkbPartOrderReturn.setPartOrderId(selected.getId());
        goodReturnBlobObjectList = new ArrayList<>();
        initPartList();
    }

    public void prepareReturnEdit(){
        this.actionReturnName = "Edit";
        this.returnFormFlag = "edit";
        goodReturnBlobObjectList = zkbPartOrderService.getBlobObjectRepository().findByBoUuid(zkbPartOrderReturn.getId());
        initPartList();
    }

    public void prepareReturnAudit(ZkbPartOrderReturn r){
        this.actionReturnName = "Audit";
        if(zkbPartOrderStatusVo.isCanEditFlag()){
            if(ZkbPartOrderReturn.DataStatusEnum.Accept.toString().equals(r.getDataStatus()) || ZkbPartOrderReturn.DataStatusEnum.Reject.toString().equals(r.getDataStatus())){
                if(ZkbPartOrderReturn.ModifyFlagEnum.N.toString().equals(r.getModifyFlag())){
                    this.returnFormFlag = "readOnly";
                }else{
                    this.returnFormFlag = "audit";
                }
            }else{
                this.returnFormFlag = "audit";
            }
        }else{
            this.returnFormFlag = "readOnly";
        }

        this.zkbPartOrderReturn = r;
        goodReturnBlobObjectList = zkbPartOrderService.getBlobObjectRepository().findByBoUuid(zkbPartOrderReturn.getId());
        initPartList();
    }

    public void prepareReturnModifyCreate(){
        this.actionReturnName = "ModifyCreate";
        this.returnFormFlag = "edit";
        zkbPartOrderReturn = new ZkbPartOrderReturn();
        zkbPartOrderReturn.setReturnDate(new Date());
        zkbPartOrderReturn.setPartOrderId(selected.getId());
        goodReturnBlobObjectList = new ArrayList<>();
        initPartList();
    }

    public void prepareReturnModifyEdit(){
        this.actionReturnName = "ModifyEdit";
        this.returnFormFlag = "edit";
        goodReturnBlobObjectList = zkbPartOrderService.getBlobObjectRepository().findByBoUuid(zkbPartOrderReturn.getId());
        initPartList();
    }

    public void deleteReturnModify(){
        zkbPartOrderReturnModifyList.remove(zkbPartOrderReturn);
        goodReturnBlobObjectList = new ArrayList<>();
        zkbPartOrderReturn = new ZkbPartOrderReturn();
    }

    public boolean resizeReturnHeight(){
        if(ZkbConstantUtil.T.equals(selected.getNotGeAsset()) && "DOA".equals(zkbPartOrderReturn.getReturnType())){
            return true;
        }
        return false;
    }

    public void initPartList(){
        partNoList = new ArrayList<>();
        orderNoList = new ArrayList<>();
        List<ZkbPartOrderItem> zkbPartOrderItemList = zkbPartOrderService.queryItemBuPartOrderId(selected.getId());
        List<String> orderContains = new ArrayList<>();
        for(ZkbPartOrderItem i :zkbPartOrderItemList){
            HashMap<String, String> m = new HashMap<>();
            m.put("text",i.getPartNo()+ "("+i.getPartName() +")");
            m.put("value",i.getId());
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
        if(!"DOA".equals(zkbPartOrderReturn.getReturnType()) && !"damagedShipment".equals(zkbPartOrderReturn.getReturnType())){
            zkbPartOrderReturn.setPartSerialNumber(null);
            zkbPartOrderReturn.setPartQualityIssue(null);
        }
        if("Create".equals(actionReturnName) || "Edit".equals(actionReturnName)){
            if(StrUtil.isNotEmpty(selected.getNotGeAsset()) && ZkbConstantUtil.T.equals(selected.getNotGeAsset())){
                if("goodReturn".equals(zkbPartOrderReturn.getReturnType())){
                    if(goodReturnBlobObjectList == null || goodReturnBlobObjectList.isEmpty()){
                        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                        WebUtil.addErrorMessage("请提供好还的备件照片和寄件时的外包装照片","");
                        return;
                    }else if(goodReturnBlobObjectList.size() < 2){
                        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                        WebUtil.addErrorMessage("上传图片数量不能低于2张","");
                        return;
                    }else {
                        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
                    }
                }
            }
            boolean flag = true;
            Integer total = 0;
            for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                if(zkbPartOrderReturn.getItemId().equals(i.getId())){
                    total = i.getPartQty();
                    break;
                }
            }
            Integer returnQty = 0;
            List<ZkbPartOrderReturn> returns = new ArrayList<>();
            if(StringUtils.isEmpty(zkbPartOrderReturn.getId())){
                returns = zkbPartOrderReturnRepository.findByPartOrderIdAndItemId(selected.getId(),zkbPartOrderReturn.getItemId());
            }else {
                returns = zkbPartOrderReturnRepository.findByPartOrderIdAndItemIdAndIdIsNot(selected.getId(),zkbPartOrderReturn.getItemId(),zkbPartOrderReturn.getId());
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
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("归还数量不能大于配件数量","");
                return;
            }else{
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
            }
            if(StringUtils.isEmpty(zkbPartOrderReturn.getId())){
                zkbPartOrderReturn.setCreatedDate(new Date());
            }else{
                zkbPartOrderReturn.setUpdatedDate(new Date());
            }
            zkbPartOrderReturn.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.Y.toString());
            zkbPartOrderReturn.setDataStatus(null);
            zkbPartOrderReturn = zkbPartOrderReturnRepository.save(zkbPartOrderReturn);

            for(V2_BlobObject v : goodReturnBlobObjectList){
                if(StrUtil.isEmpty(v.getBoUuid())){
                    v.setBoUuid(zkbPartOrderReturn.getId());
                    blobService.saveBlobObject(v);
                }
            }

            zkbPartOrderStatusVo.setSaveTips("备件归还信息已添加，提交申请后流程才会进入Admin确认阶段，请在添加完需要已归还的备件信息后提交申请，以免影响您后续的进度，感谢您的配合！");
        }else if("ModifyCreate".equals(actionReturnName)){
            zkbPartOrderReturn.setId(UUID.randomUUID().toString());
            zkbPartOrderReturnModifyList.add(zkbPartOrderReturn);
        }
        zkbPartOrderReturn = null;
        zkbPartOrderReturnList = zkbPartOrderService.queryReturnListByPartOrderId(selected.getId(),zkbPartOrderStatusVo.isChoseAdmin());
    }

    public void trainCourseRender(){
        if(StrUtil.isEmpty(selected.getNotGeAsset()) && ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            if(zkbContract != null ){
                if(!ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus())){
                    if(!ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                        zkbPartOrderStatusVo.setHaveTrainCourseFlag(true);
                    }
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

    public String getPartSerialNumberTips(){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if("DOA".equals(zkbPartOrderReturn.getReturnType())){
                return "发生DOA的序列号，若没有序列号，填“无”";
            }else if("damagedShipment".equals(zkbPartOrderReturn.getReturnType())){
                return " 发生Damaged in shipment的序列号，若没有序列号，填“无”";
            }else{
                return "";
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if("DOA".equals(selected.getReturnType())){
                return "发生DOA的序列号，若没有序列号，填“无”";
            }else if("damagedShipment".equals(selected.getReturnType())){
                return " 发生Damaged in shipment的序列号，若没有序列号，填“无”";
            }else{
                return "";
            }
        }else{
            return "";
        }

    }

    //region 关单修改归还数据
    public void preReturnModifyEdit(){
        actionReturnName = "ModifyEdit";
        zkbPartOrderReturn = null;

        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
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
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
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
            if(selected.getOrderNo().toUpperCase().startsWith("FE") && ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.name().equals(selected.getReturnType())){
                List<V2_BlobObject> newBlobList = queryBlobByType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN_NEW_CAT);
                if(CollectionUtils.isEmpty(newBlobList)){
                    WebUtil.addErrorMessage("新探头序列号照片未上传！","");
                    return;
                }
                List<V2_BlobObject> oldBlobList = queryBlobByType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN_OLD_CAT);
                if(CollectionUtils.isEmpty(oldBlobList)){
                    WebUtil.addErrorMessage("旧探头序列号照片未上传！","");
                    return;
                }
            }

            boolean arg = zkbPartOrderService.probeModifyReturn(selected,currentUser, universalBlobObjectList);
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


    public void returnAudit(){
        if("goodReturn".equals(zkbPartOrderReturn.getReturnType()) && ZkbConstantUtil.T.equals(selected.getNotGeAsset())){
            if(StrUtil.isEmpty(zkbPartOrderReturn.getSevenReturnedFlag()) && ZkbPartOrderReturn.DataStatusEnum.Accept.toString().equals(zkbPartOrderReturn.getDataStatus())){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("是否7天未归还不能为空！");
                return;
            }else{
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
            }
        }

        zkbPartOrderReturn.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.Y.toString());
        for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
            if(r.getId().equals(zkbPartOrderReturn.getId())){
                r = zkbPartOrderReturn;
            }
        }
    }
    //endregion

    public boolean completeRequiredCheck(String column){
        if("Yes".equals(zkbPartOrderCloseInfo.getPatientUse()) || "Potential".equals(zkbPartOrderCloseInfo.getSafetyConcern()) || "Actual".equals(zkbPartOrderCloseInfo.getSafetyConcern())){
            return true;
        }else if("NotReported".equals(zkbPartOrderCloseInfo.getPatientUse()) && "No".equals(zkbPartOrderCloseInfo.getSafetyConcern()) ){
            if("qualityContact".equals(column)){
                return true;
            }
        }

        return false;
    }

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

    public void preProbeLogistics(){
        if(StringUtils.isEmpty(selected.getOrderNo())){
            WebUtil.addErrorMessage("Order#不能为空！");
            return;
        }
        if(StringUtils.isEmpty(selected.getCatNo())){
            WebUtil.addErrorMessage("配件号不能为空！");
            return;
        }
        svcEngineLogisticsLineVOList = zkbPartOrderService.querySvcLogistics(selected.getSrNo(),selected.getOrderNo(),selected.getCatNo());
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
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            reCalcWhetherDeduct();
        }
    }

    public void reCalcWhetherDeduct(){
        if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
            if(StrUtil.isNotEmpty(selected.getProductAuditerUid())){
                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
            }else{
                if(selected.getAssetInfo().getRepairForm() != null && selected.getAssetInfo().getRepairForm() == 2){
                    zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                }
            }
        }else if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
            zkbPartOrderStatusVo.setWhetherDeductEdit(true);
        }else{
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), selected.getAssetInfo().getUid());
            if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
            }else if(selected.getSsaKeyFlag() != null && ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){
                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
            }else if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){
                zkbPartOrderStatusVo.setWhetherDeductEdit(true);
            }else{
                if(selected.getAssetInfo().getRepairForm() != null && selected.getAssetInfo().getRepairForm() == 2){
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                        zkbPartOrderStatusVo.setWhetherDeductEdit(true);
                    }
                }
            }
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
            if(StrUtil.isEmpty(selected.getNotGeAsset()) && StringUtils.isEmpty(zkbPartOrderItem.getPrice())){
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
        WebUtil.addErrorMessage("操作成功！","操作成功");
        return "List?faces-redirect=true&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public void handleReturnFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN);
            v2_blobObject.setBoUuid(null);
            goodReturnBlobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeReturnFile(V2_BlobObject v2_blobObject){
        goodReturnBlobObjectList = goodReturnBlobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
        blobService.deleteBlobFile(v2_blobObject);
    }

    public void initZknPartCat(Date contractDate){
        zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(contractDate);
    }
    public List<ZkbPartOrderCat> autoCompleteCat(String query){
        List<ZkbPartOrderCat> res = zkbPartOrderCatList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getCatModel().contains(query)).collect(Collectors.toList());

        return res;
    }

    public void getAllegedPatientConsequence(){
        /*if(CopyByInvok.NORMAL_NO.equals(zkbPartOrderCloseInfo.getSafetyConcern()) && CopyByInvok.NORMAL_NO.equals(zkbPartOrderCloseInfo.getPatientUse())){
            zkbPartOrderCloseInfo.setAllegedPatientConsequence("非患者扫描时出现问题，不涉及患者");
            zkbPartOrderStatusVo.setAllegedPatientConsequenceFlag(true);
        }else if("非患者扫描时出现问题，不涉及患者".equals(zkbPartOrderCloseInfo.getAllegedPatientConsequence())){
            if(zkbPartOrderStatusVo.isAllegedPatientConsequenceFlag()){
                zkbPartOrderCloseInfo.setAllegedPatientConsequence("");
            }
            zkbPartOrderStatusVo.setAllegedPatientConsequenceFlag(false);
        }*/
        if(ZkbConstantUtil.NORMAL_NO.equals(zkbPartOrderCloseInfo.getSafetyConcern()) && ZkbConstantUtil.NORMAL_NO.equals(zkbPartOrderCloseInfo.getPatientUse())){
            zkbPartOrderCloseInfo.setPatientImpactOutcome("NoPatientInvolved");
        }
    }

    public void prepareWorkTimeCreate(){
        selectZkbPartOrderCloseWorkTime = new ZkbPartOrderCloseWorkTime();
        selectZkbPartOrderCloseWorkTime.setPartOrderId(selected.getId());
        selectZkbPartOrderCloseWorkTime.setPartOrderNo(selected.getPartOrderNo());
        selectZkbPartOrderCloseWorkTime.setSrNo(selected.getSrNo());
        selectZkbPartOrderCloseWorkTime.setCreatedDate(new Date());

    }

    public void prepareWorkTimeEdit(){
    }

    public void deleteWorkTime(){
        zkbPartOrderCloseWorkTimeList.remove(selectZkbPartOrderCloseWorkTime);
    }

    public void saveWorkTime(){
        if(selectZkbPartOrderCloseWorkTime.getIndex() == null){
            selectZkbPartOrderCloseWorkTime.setIndex(zkbPartOrderCloseWorkTimeList.size());
            zkbPartOrderCloseWorkTimeList.add(selectZkbPartOrderCloseWorkTime);
            selectZkbPartOrderCloseWorkTime = new ZkbPartOrderCloseWorkTime();
        }else{
            zkbPartOrderCloseWorkTimeList.set(this.selectZkbPartOrderCloseWorkTime.getIndex(),this.selectZkbPartOrderCloseWorkTime);
        }
    }

    public void handleCloseFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = zkbPartOrderService.uploadCloseFile(event);
        closeBlobObjectList.add(v2_blobObject);
        WebUtil.addSuccessMessage( WebUtil.getMessage("SuccessUploaded"));
    }

    public void removeFile(V2_BlobObject v2_blobObject){
        closeBlobObjectList.remove(v2_blobObject);
        blobService.deleteBlobFile(v2_blobObject);
    }

    public void workOrderView(String id){
        String url = "actionName=View&selectedid=" + id + "&queryIndex=0";
        url = "../../woMgr/workOrderList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(nt) ? "" : nt);
    }

    public void partNoChange(ValueChangeEvent event){
        ZkbPartOrderItem zkbPartOrderItem = selected.getZkbPartOrderItems().stream().filter(t -> t.getId().equals(event.getNewValue())).findFirst().get();
        zkbPartOrderReturn.setPartNo(zkbPartOrderItem.getPartNo());
    }

    public List<V2_BlobObject> queryBlobByType(String type){
        return universalBlobObjectList.stream().filter(t -> t.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public void handleUniversalFileUpload(FileUploadEvent event){
        String type = event.getComponent().getId();
        V2_BlobObject v2_blobObject = zkbPartOrderService.uploadFile(event, type);
        universalBlobObjectList.add(v2_blobObject);
        WebUtil.addSuccessMessage( WebUtil.getMessage("SuccessUploaded"));
    }

    public void removeUniversalFile(V2_BlobObject v2_blobObject){
        universalBlobObjectList.remove(v2_blobObject);
        blobService.deleteBlobFile(v2_blobObject);
    }

    public boolean showCatUploadFlag(){
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(StrUtil.isNotEmpty(selected.getOrderNo()) && selected.getOrderNo().toUpperCase().startsWith("FE")){
                if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.name().equals(selected.getReturnType())){
                    return true;
                }
            }
        }
        return false;
    }

    public void sendMessageAgain(){
        try{
            zkbPartOrderService.sendMessageAgain(selected, UserContextService.getAccessToken());
            WebUtil.addSuccessMessage("发送成功！");
        }catch (Exception ex){
            ex.printStackTrace();
            WebUtil.addSuccessMessage("发送失败！");
        }
    }

    //region get、set
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


    public List<ZkbPartOrderReturn> getZkbPartOrderReturnModifyList() {
        return zkbPartOrderReturnModifyList;
    }

    public void setZkbPartOrderReturnModifyList(List<ZkbPartOrderReturn> zkbPartOrderReturnModifyList) {
        this.zkbPartOrderReturnModifyList = zkbPartOrderReturnModifyList;
    }

    public List<HashMap<String, String>> getHighCatList() {
        return highCatList;
    }

    public void setHighCatList(List<HashMap<String, String>> highCatList) {
        this.highCatList = highCatList;
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

    //endregion

    public String getModifySsaKey() {
        return modifySsaKey;
    }

    public void setModifySsaKey(String modifySsaKey) {
        this.modifySsaKey = modifySsaKey;
    }

    public List<V2_BlobObject> getGoodReturnBlobObjectList() {
        return goodReturnBlobObjectList;
    }

    public void setGoodReturnBlobObjectList(List<V2_BlobObject> goodReturnBlobObjectList) {
        this.goodReturnBlobObjectList = goodReturnBlobObjectList;
    }

    public String getReturnFormFlag() {
        return returnFormFlag;
    }

    public void setReturnFormFlag(String returnFormFlag) {
        this.returnFormFlag = returnFormFlag;
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return zkbPartOrderService.getZkbPartOrderRepository();
    }

    public String[] getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(String[] selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public List<ZkbUnrealVo> getUnrealItem() {
        return unrealItem;
    }

    public void setUnrealItem(List<ZkbUnrealVo> unrealItem) {
        this.unrealItem = unrealItem;
    }

    public String getUnrealReason() {
        return unrealReason;
    }

    public void setUnrealReason(String unrealReason) {
        this.unrealReason = unrealReason;
    }


}
