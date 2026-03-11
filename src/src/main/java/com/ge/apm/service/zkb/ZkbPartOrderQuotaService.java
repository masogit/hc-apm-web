package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class ZkbPartOrderQuotaService {

    @Autowired
    private ZkbContractRepository zkbContractRepository;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    @Autowired
    private ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    private ZkbContractQuotaLogService zkbContractQuotaLogService;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    ZkbContractCatQuotaHistoryService zkbContractCatQuotaHistoryService;

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;

    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;
    
    public enum HandQuotaType{
        apply, use, returned
    }

    //region 申请名额处理
    /**
     * 申请名额处理
     */
    public void subApplyQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, int qty, UserAccount userAccount, boolean acceptFlag){
        String opType = "";
        if(qty > 0){
            opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderDeduct.toString();
        }else {
            opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString();
        }
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            subApplyQtyForPart(zkbContract,zkbPartOrder,qty,userAccount, acceptFlag,opType);
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            subApplyQtyForProbe(zkbContract,zkbPartOrder,qty,userAccount, acceptFlag,opType);
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            subApplyQtyForArtificial(zkbContract,zkbPartOrder,qty,userAccount, acceptFlag,opType);
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            subApplyQtyForTool(zkbContract,zkbPartOrder,qty,userAccount, acceptFlag,opType);
        }
    }

    /**
     *备件名额处理
     */
    public void subApplyQtyForPart(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, int qty, UserAccount userAccount, boolean acceptFlag, String opType){
        //region 备件名额处理
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
        if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){
            Integer a = zkbContractAsset.getApplyNum() == null ? 0: zkbContractAsset.getApplyNum();
            if(acceptFlag){
                a++;
            }else {
                a--;
            }
            zkbContractAsset.setApplyNum(a);
            zkbContractAssetRepository.save(zkbContractAsset);
        }else{
            /*if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
            }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                    }else{
                        //兼容老数据
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                    }
                }else{
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }
            }else{
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
            }*/
            if(StrUtil.isEmpty(zkbPartOrder.getRadiationQuotaType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }else{
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }
            }else{
                if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Part.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Consumable.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }
            }
        }
        //endregion
    }

    public void subApplyQtyForProbe(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, int qty, UserAccount userAccount, boolean acceptFlag, String opType){
        //region 探头名额处理
        if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType()) || StringUtils.isEmpty(zkbContract.getQuotaDeductType())){
            if(StringUtils.isEmpty(zkbPartOrder.getHighCatFlag())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString());
            }else if(ZkbConstantUtil.F.equals(zkbPartOrder.getHighCatFlag())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString());
            }else if(ZkbConstantUtil.T.equals(zkbPartOrder.getHighCatFlag())){
                ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                if(zkbContractCat != null) {
                    if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                        if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                            handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString());
                        }
                    }else{
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                        handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString());
                    }
                    if(acceptFlag){
                        zkbContractCat.setEnabled(false);
                    }else {
                        zkbContractCat.setEnabled(true);
                    }
                    zkbContractCat.setLastModifiedDate(new Date());
                    zkbContractCat.setLastModifiedBy(userAccount.getName());
                    zkbContractCatRepository.save(zkbContractCat);
                }
            }
        }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
            if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.Any.toString());
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString());
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.Special.toString());
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                if(zkbContractCat != null){
                    if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                        if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                            handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString());
                        }
                    }else{
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                        handleQuotaHistory(zkbContract,zkbPartOrder,qty,userAccount, ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString());
                    }
                    if(acceptFlag){
                        zkbContractCat.setEnabled(false);
                    }else {
                        zkbContractCat.setEnabled(true);
                    }
                    zkbContractCat.setLastModifiedDate(new Date());
                    zkbContractCat.setLastModifiedBy(userAccount.getName());
                    zkbContractCatRepository.save(zkbContractCat);
                }
            }
        }
        //endregion
    }

    public void subApplyQtyForArtificial(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, int qty, UserAccount userAccount, boolean acceptFlag, String opType){
        //region  人工
        if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFlag())){
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                if(qty < 0){
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
                }else{
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaDeduct.toString());
                }
            }else{
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
                if(ZkbConstantUtil.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                }else {
                    if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFirstFlag())){
                        if(qty > 0){
                            V2_WorkOrder v = v2_workOrderRepository.findById(zkbPartOrder.getWorkOrderId());
                            if(v != null && v.getServiceRequest() != null){
                                saveContractAsset(zkbContract,zkbContractAsset,v.getServiceRequest().getRequestTime());
                            }
                        }else{
                            zkbContractAsset.setMaintainStartDate(null);
                            zkbContractAsset.setMaintainEndDate(null);
                            zkbContractAssetRepository.save(zkbContractAsset);
                        }
                    }else {
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                        if(qty > 0){
                            V2_WorkOrder v = v2_workOrderRepository.findById(zkbPartOrder.getWorkOrderId());
                            if(v != null && v.getServiceRequest() != null){
                                saveContractAsset(zkbContract,zkbContractAsset,v.getServiceRequest().getRequestTime());
                            }
                        }
                    }
                }
            }
        }else{
            if(ZkbConstantUtil.NORMAL_TWO.equals(zkbPartOrder.getArtificialType())){
                if(!ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) && !ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    return;
                }
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    Integer remindQty = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                    if(qty < 0){
                        if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){
                            //如果是产品经理拒绝或者产品经理审批过说明申请时名额不足，并未扣除过名额
                        }else{
                            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                            if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Artificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                            }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2) { //外修不扣名额
                            }
                        }
                    }else{
                        if(remindQty >= 1){
                            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                            if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Artificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                            }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2) { //外修不扣名额
                            }
                        }
                    }
                }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    Integer remindQty = zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota();
                    if(qty < 0){
                        if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){
                        }else{
                            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                            if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                            }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2) { //外修不扣名额
                            }
                        }
                    }else{
                        if(remindQty >= 1){
                            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                            if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                            }
                        }
                    }
                }
                return;
            }
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
            if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                return;
            }
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getSsaKeyFlag())){
                return;
            }
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                if(qty < 0){
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
                }else{
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaDeduct.toString());
                }
            }else{
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){ //内修
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Artificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                    }else{
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                    }
                }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){ //外修
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    }else{
                        handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
                    }
                }
            }
        }
        //endregion
    }

    public void subApplyQtyForTool(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, int qty, UserAccount userAccount, boolean acceptFlag, String opType){
        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) ||
                ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
        }else{
            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.apply.name(), qty, zkbContract, zkbPartOrder,userAccount, opType);
        }
    }

    public void handleQuotaByType(String type, String businessType,int qty, ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,UserAccount userAccount, String opType){
        if(ZkbContractQuotaLog.OrderTypeEnum.Spare.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) - qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) + qty);
            }else if(HandQuotaType.use.name().equals(businessType)){
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) - qty);
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
            }else if(HandQuotaType.use.name().equals(businessType)){
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.name().equals(type) || ZkbContractQuotaLog.OrderTypeEnum.Probe.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qty);
                zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) + qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) - qty);
                zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) + qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setProbeQuotaNormal((zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal()) - qty);
                zkbContract.setProbeQuotaNormalApply((zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply()) + qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setProbeQuotaNormalUse((zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse()) - qty);
                zkbContract.setProbeQuotaNormal((zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal()) + qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setProbeQuotaSpecial((zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial()) - qty);
                zkbContract.setProbeQuotaSpecialApply((zkbContract.getProbeQuotaSpecialApply() == null ? 0 : zkbContract.getProbeQuotaSpecialApply()) + qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setProbeQuotaSpecialUse((zkbContract.getProbeQuotaSpecialUse() == null ? 0 : zkbContract.getProbeQuotaSpecialUse()) - qty);
                zkbContract.setProbeQuotaSpecial((zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial()) + qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.Artificial.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) + qty);
            }else if(HandQuotaType.use.name().equals(businessType)){
                zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) - qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setPartHighQuota((zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota()) - qty);
                zkbContract.setPartHighQuotaApply((zkbContract.getPartHighQuotaApply() == null ? 0 : zkbContract.getPartHighQuotaApply()) + qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setPartHighQuotaUse((zkbContract.getPartHighQuotaUse() == null ? 0 : zkbContract.getPartHighQuotaUse()) - qty);
                zkbContract.setPartHighQuota((zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota()) + qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setCcsbMaintenanceQuota((zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota()) - qty);
                zkbContract.setCcsbMaintenanceQuotaApply((zkbContract.getCcsbMaintenanceQuotaApply() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaApply()) + qty);
            }else if(HandQuotaType.use.name().equals(businessType)){
                zkbContract.setCcsbMaintenanceQuotaUse((zkbContract.getCcsbMaintenanceQuotaUse() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaUse()) + qty);
                zkbContract.setCcsbMaintenanceQuotaApply((zkbContract.getCcsbMaintenanceQuotaApply() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaApply()) - qty);
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.Consumable.name().equals(type)){
            if(HandQuotaType.apply.name().equals(businessType)){
                zkbContract.setConsumableQuota((zkbContract.getConsumableQuota() == null ? 0 : zkbContract.getConsumableQuota()) - qty);
                zkbContract.setConsumableQuotaApply((zkbContract.getConsumableQuotaApply() == null ? 0 : zkbContract.getConsumableQuotaApply()) + qty);
            }else if(HandQuotaType.use.name().equals(businessType)){
                zkbContract.setConsumableQuotaApply((zkbContract.getConsumableQuotaApply() == null ? 0 : zkbContract.getConsumableQuotaApply()) - qty);
                zkbContract.setConsumableQuotaUse((zkbContract.getConsumableQuotaUse() == null ? 0 : zkbContract.getConsumableQuotaUse()) + qty);
            }else if(HandQuotaType.returned.name().equals(businessType)){
                zkbContract.setConsumableQuota((zkbContract.getConsumableQuota() == null ? 0 : zkbContract.getConsumableQuota()) + qty);
                zkbContract.setConsumableQuotaUse((zkbContract.getConsumableQuotaUse() == null ? 0 : zkbContract.getConsumableQuotaUse()) - qty);
            }
        }
        zkbContractRepository.save(zkbContract);
        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,type,ZkbPartOrder.class.getSimpleName(),userAccount, WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
    }
    //endregion



    //region 已使用名额变化
    public void subUseQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount) {
        String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderDeduct.toString();
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            subUseQtyForPart(zkbContract, zkbPartOrder, qty, userAccount, opType);
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            subUseQtyForProbe(zkbContract, zkbPartOrder, qty, userAccount, opType);
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            subUseQtyForArtificial(zkbContract, zkbPartOrder, qty, userAccount, opType);
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            subUseQtyForTool(zkbContract, zkbPartOrder, qty, userAccount, opType);
        }
    }

    public void subUseQtyForPart(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount,String opType){
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
        if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){

        }else{
            Integer returnQty = 0;
            for (ZkbPartOrderItem i : zkbPartOrder.getZkbPartOrderItems()) {
                returnQty += i.getPrePartQty() - i.getPartQty();
            }
            if(StrUtil.isEmpty(zkbPartOrder.getRadiationQuotaType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                    zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                    zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty - returnQty);
                    zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }else {
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty - returnQty);
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }else{
                if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    zkbContract.setPartHighQuotaUse((zkbContract.getPartHighQuotaUse() == null ? 0 : zkbContract.getPartHighQuotaUse()) + qty);
                    zkbContract.setPartHighQuotaApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getPartHighQuotaApply()) - qty - returnQty);
                    zkbContract.setPartHighQuota((zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty - returnQty);
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Part.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                    zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty - returnQty);
                    zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    zkbContract.setConsumableQuotaUse((zkbContract.getConsumableQuotaUse() == null ? 0 : zkbContract.getConsumableQuotaUse()) + qty);
                    zkbContract.setConsumableQuotaApply((zkbContract.getConsumableQuotaApply() == null ? 0 : zkbContract.getConsumableQuotaApply()) - qty - returnQty);
                    zkbContract.setConsumableQuota((zkbContract.getConsumableQuota() == null ? 0 : zkbContract.getConsumableQuota()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Consumable.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }
            /*if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty - returnQty);
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + returnQty);
                zkbContractRepository.save(zkbContract);
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        zkbContract.setPartHighQuotaUse((zkbContract.getPartHighQuotaUse() == null ? 0 : zkbContract.getPartHighQuotaUse()) + qty);
                        zkbContract.setPartHighQuotaApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getPartHighQuotaApply()) - qty - returnQty);
                        zkbContract.setPartHighQuota((zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota()) + returnQty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                        zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty - returnQty);
                        zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + returnQty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else {
                        //兼容老数据
                        zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                        zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty - returnQty);
                        zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + returnQty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }
                }else{
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty - returnQty);
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + returnQty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty - returnQty);
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + returnQty);
                zkbContractRepository.save(zkbContract);
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }*/
        }
    }

    public void subUseQtyForProbe(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount,String opType){
        ZkbContractCat zkbContractCat = new ZkbContractCat();
        if(StringUtils.isNotEmpty(zkbPartOrder.getContractCatId())){
            zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
        }
        if(StringUtils.isEmpty(zkbContract.getQuotaDeductType()) || ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())) {
            if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                if(StringUtils.isEmpty(zkbPartOrder.getHighCatFlag()) || ZkbConstantUtil.F.equals(zkbPartOrder.getHighCatFlag()) || ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                    if(zkbPartOrder.getNewCatFlag().equals(zkbPartOrder.getTenantNewCatFlag())){
                        zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + qty);
                    }else{
                        if (ZkbConstantUtil.T.equals(zkbPartOrder.getNewCatFlag())) {
                            zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + (qty * 2));
                            zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qty);
                            zkbContractCatQuotaHistoryService.updateZkbFeHistoryQtyByOldQuotaType(zkbPartOrder.getId(), qty * 2);
                        }else{
                            zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) + (qty/2));
                            zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + (qty/2));
                            zkbContractCatQuotaHistoryService.updateZkbFeHistoryQtyByOldQuotaType(zkbPartOrder.getId(), qty/2);
                        }
                    }
                    zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) - qty);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Probe.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }else{
                zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) - qty);
                zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + qty);
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Probe.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }
        }else {
            //将qty与zkbPartOrder.getQty()进行比较，如果一致，名额没有发生变化，如果不一致那么则发生了变化
            //剩余名额要-qtyChange，申请名额-qty，已使用名额增加 zkbPartOrder.getQty()
            int qtyChange = zkbPartOrder.getQty() - qty;
            if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + zkbPartOrder.getQty());
                zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) - qty);
                if(qtyChange != 0){
                    zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qtyChange);
                }
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                zkbContract.setProbeQuotaNormalUse((zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse()) + zkbPartOrder.getQty());
                zkbContract.setProbeQuotaNormalApply((zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply()) - qty);
                if(qtyChange != 0){
                    zkbContract.setProbeQuotaNormal((zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal()) - qtyChange);
                }
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                zkbContract.setProbeQuotaSpecialUse((zkbContract.getProbeQuotaSpecialUse() == null ? 0 : zkbContract.getProbeQuotaSpecialUse()) + zkbPartOrder.getQty());
                zkbContract.setProbeQuotaSpecialApply((zkbContract.getProbeQuotaSpecialApply() == null ? 0 : zkbContract.getProbeQuotaSpecialApply()) - qty);
                if(qtyChange != 0){
                    zkbContract.setProbeQuotaSpecial((zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial()) - qtyChange);
                }
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                    if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                        zkbContract.setProbeQuotaNormalUse((zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse()) + zkbPartOrder.getQty());
                        zkbContract.setProbeQuotaNormalApply((zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply()) - qty);
                        if(qtyChange != 0){
                            zkbContract.setProbeQuotaNormal((zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal()) - qtyChange);
                        }
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }
                }else{
                    zkbContract.setProbeQuotaNormalUse((zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse()) + zkbPartOrder.getQty());
                    zkbContract.setProbeQuotaNormalApply((zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply()) - qty);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }
        }
        zkbContractRepository.save(zkbContract);
    }

    public void subUseQtyForArtificial(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount,String opType){
        if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFlag())){
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){

            }else{
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
                if(ZkbConstantUtil.F.equals(zkbPartOrder.getMaintainFirstFlag()) || ZkbConstantUtil.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }
        }else{
            if(ZkbConstantUtil.NORMAL_TWO.equals(zkbPartOrder.getArtificialType())){
                if(!ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) && !ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    return;
                }
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){
                    }else{
                        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                            zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                            zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){

                    }else{
                        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                            zkbContract.setCcsbMaintenanceQuotaUse((zkbContract.getCcsbMaintenanceQuotaUse() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaUse()) + qty);
                            zkbContract.setCcsbMaintenanceQuotaApply((zkbContract.getCcsbMaintenanceQuotaApply() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaApply()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }
                return;
            }
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
            if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                return;
            }
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getSsaKeyFlag())){
                return;
            }
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                //zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
            }else{
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){ //内修
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                        zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                        zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) - qty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else{
                        //if(StringUtils.isEmpty(zkbPartOrder.getHaveTrainCourseFlag()) || "t".equals(zkbPartOrder.getHaveTrainCourseFlag())) {
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        //}
                    }
                }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){

                    }else{
                        //if(StringUtils.isEmpty(zkbPartOrder.getHaveTrainCourseFlag()) || "t".equals(zkbPartOrder.getHaveTrainCourseFlag())) {
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        //}
                    }
                }
            }
        }
    }

    public void subUseQtyForTool(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount,String opType){
        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
            zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
            zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty);
            zkbContractRepository.save(zkbContract);
            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
        }else{
            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
            zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
            zkbContractRepository.save(zkbContract);
            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
        }
    }
    //endregion

    //region 归还名额变化
    /**
     * 归还一般都是正数，但是特殊情况：当GE Admin修改归还数据的时候会出现归还数量为负数的情况
     * 补充：四合一申请归还时需要将合同四合一状态变更同时数量需要发生变化，具体状态变化根据qty的正负进行判断，正为不可用，负数变为可用
     */
    public void subReturnQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount) {
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            subReturnQtyForPart(zkbContract, zkbPartOrder, qty, userAccount);
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            subReturnQtyForProbe(zkbContract, zkbPartOrder, qty, userAccount);
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            subReturnQtyForTool(zkbContract, zkbPartOrder, qty, userAccount);
        }
    }

    public void subReturnQtyForPart(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount) {
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
        if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){

        }else {
            if(StrUtil.isEmpty(zkbPartOrder.getRadiationQuotaType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                }else {
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                }
            }else{
                if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Part.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                    handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Consumable.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                }
            }
            /*if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())
                    || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) - qty);
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + qty);
                zkbContractRepository.save(zkbContract);
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString(),ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        zkbContract.setPartHighQuotaUse((zkbContract.getPartHighQuotaUse() == null ? 0 : zkbContract.getPartHighQuotaUse()) - qty);
                        zkbContract.setPartHighQuota((zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota()) + qty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString(),ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                        zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString(),ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else{
                        zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                        zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString(),ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }
                }else{
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString(),ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                zkbContractRepository.save(zkbContract);
                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString(),ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
            }*/
        }
    }

    public void subReturnQtyForProbe(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount){
        if(StringUtils.isEmpty(zkbContract.getQuotaDeductType()) || ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())) {
            if(StringUtils.isEmpty(zkbPartOrder.getHighCatFlag()) || ZkbConstantUtil.F.equals(zkbPartOrder.getHighCatFlag())){
                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(),qty);
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
            }else{
                if(StringUtils.isNotEmpty(zkbPartOrder.getContractCatId())){
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                    if(zkbContractCat != null){
                        if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                            if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(),qty);
                                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                            }
                        }else{
                            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Probe.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                            zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(),qty);
                        }

                        if(qty >0){
                            zkbContractCat.setEnabled(true);
                        }else if(qty < 0){
                            zkbContractCat.setEnabled(false);
                        }
                        zkbContractCat.setLastModifiedDate(new Date());
                        zkbContractCat.setLastModifiedBy(userAccount.getName());
                        zkbContractCatRepository.save(zkbContractCat);
                    }
                }
            }
        }else{
            if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(),qty);
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(),qty);
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(),qty);
            }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                if(StringUtils.isNotEmpty(zkbPartOrder.getContractCatId())) {
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                    if (zkbContractCat != null) {
                        if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())) {
                            if (ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())) {
                                handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(), qty);
                            }
                        }else{
                            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
                            zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(), qty);
                        }
                        if(qty >0){
                            zkbContractCat.setEnabled(true);
                        }else if(qty < 0){
                            zkbContractCat.setEnabled(false);
                        }
                        zkbContractCat.setLastModifiedDate(new Date());
                        zkbContractCat.setLastModifiedBy(userAccount.getName());
                        zkbContractCatRepository.save(zkbContractCat);
                    }
                }
            }
        }
    }

    public void subReturnQtyForTool(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount){
        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.Spare.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
        }else{
            handleQuotaByType(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(), HandQuotaType.returned.name(), qty, zkbContract, zkbPartOrder,userAccount, ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString());
        }
    }

    public void reDeductArtificialQuota(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, Integer qty, UserAccount userAccount){
        String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderDeduct.toString();
        if(qty > 0){
            opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderDeduct.toString();
        }else if(qty < 0){
            opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString();
        }else {
            return;
        }

        if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFlag())){
            if(ZkbConstantUtil.F.equals(zkbPartOrder.getScoreFlag())){
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
                if(ZkbConstantUtil.F.equals(zkbPartOrder.getMaintainFirstFlag()) || ZkbConstantUtil.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }
        }else{
            if(ZkbConstantUtil.NORMAL_TWO.equals(zkbPartOrder.getArtificialType())){
                if(!ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) && !ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    return;
                }
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){
                    }else{
                        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                            zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                            zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){

                    }else{
                        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                            zkbContract.setCcsbMaintenanceQuotaUse((zkbContract.getCcsbMaintenanceQuotaUse() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaUse()) + qty);
                            zkbContract.setCcsbMaintenanceQuota((zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }
                return;
            }
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
            if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                return;
            }
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getSsaKeyFlag())){
                return;
            }
            if(StrUtil.isEmpty(zkbPartOrder.getScoreFlag()) || ZkbConstantUtil.F.equals(zkbPartOrder.getScoreFlag())){
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){ //内修
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                        zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                        zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else{
                        if(StringUtils.isEmpty(zkbPartOrder.getHaveTrainCourseFlag()) || "t".equals(zkbPartOrder.getHaveTrainCourseFlag())) {
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                    if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){

                    }else{
                        if(StringUtils.isEmpty(zkbPartOrder.getHaveTrainCourseFlag()) || "t".equals(zkbPartOrder.getHaveTrainCourseFlag())) {
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }
            }
        }
    }
    //endregion

    public void handleQuotaHistory(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, Integer qty, UserAccount userAccount,String probeType){
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
        if(ZkbConstantUtil.T.equals(zkbContract.getAssetQuotaType())){
            if(qty > 0){
                zkbContractCatQuotaHistoryService.saveZkbFeQuotaHistory(zkbContract,zkbContractAsset, zkbPartOrder.getId(),zkbPartOrder.getPartOrderNo(), probeType, userAccount, qty);
            }else{
                zkbContractCatQuotaHistoryService.deleteZkbFeHistory(zkbPartOrder.getId(), Math.abs(qty));
            }
        }
    }

    public void saveContractAsset(ZkbContract zkbContract, ZkbContractAsset zkbContractAsset,Date now){
        if(zkbContractAsset.getMaintainStartDate() != null && zkbContractAsset.getMaintainEndDate() != null){
            if(now.getTime() >= zkbContractAsset.getMaintainStartDate().getTime() && now.getTime() <= zkbContractAsset.getMaintainEndDate().getTime()){
                return;
            }else{
                Date startDate = zkbContractAsset.getMaintainStartDate();
                Date endDate =zkbContractAsset.getMaintainEndDate();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                Date tempDate = calendar.getTime();

                if(tempDate.getTime() > zkbContract.getEndDate().getTime()){
                    return;
                }
                if(now.getTime() >= startDate.getTime() && now.getTime() <= tempDate.getTime()){
                    zkbContractAsset.setMaintainStartDate(startDate);
                    zkbContractAsset.setMaintainEndDate(tempDate);
                    zkbContractAssetRepository.save(zkbContractAsset);
                    return;
                }
                while (true){
                    Calendar specialDate = Calendar.getInstance();
                    specialDate.setTime(startDate);
                    specialDate.add(Calendar.YEAR, 1);
                    startDate = specialDate.getTime();

                    specialDate.add(Calendar.YEAR, 1);
                    specialDate.add(Calendar.DAY_OF_YEAR, -1);
                    endDate = specialDate.getTime();

                    if(startDate.getTime() > zkbContract.getEndDate().getTime()){
                        break;
                    }
                    if(endDate.getTime() > zkbContract.getEndDate().getTime()){
                        endDate = zkbContract.getEndDate();
                    }
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String nowStr = format.format(now);
                    try {
                        now = format.parse(nowStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(now.getTime() >= startDate.getTime() && now.getTime() <= endDate.getTime()){
                        zkbContractAsset.setMaintainStartDate(startDate);
                        zkbContractAsset.setMaintainEndDate(endDate);
                        zkbContractAssetRepository.save(zkbContractAsset);
                        break;
                    }
                    if(startDate.getTime() >= endDate.getTime()){
                        break;
                    }
                }
            }
        }else{
            Date startDate = zkbContract.getStartDate();
            while (true){
                Calendar specialDate = Calendar.getInstance();
                specialDate.setTime(startDate);
                specialDate.add(Calendar.YEAR, 1);
                Date newStartDate = specialDate.getTime();
                specialDate.add(Calendar.DAY_OF_YEAR, -1);
                Date endDate = specialDate.getTime();
                if(endDate.getTime() > zkbContract.getEndDate().getTime()){
                    endDate = zkbContract.getEndDate();
                }
                //Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String nowStr = format.format(now);
                try {
                    now = format.parse(nowStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(now.getTime() >= startDate.getTime() && now.getTime() <= endDate.getTime()){
                    zkbContractAsset.setMaintainStartDate(startDate);
                    zkbContractAsset.setMaintainEndDate(endDate);
                    zkbContractAssetRepository.save(zkbContractAsset);
                    break;
                }
                startDate = newStartDate;

                if(newStartDate.getTime() >= zkbContract.getEndDate().getTime()){
                    break;
                }
            }
        }
    }
}
