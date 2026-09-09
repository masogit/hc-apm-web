package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ZkbPartOrderEditService {

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    ZkbContractAssetService zkbContractAssetService;

    @Autowired
    ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Autowired
    I18nMessageRepository i18nMessageRepository;

    @Autowired
    ZkbContractCatRepository zkbContractCatRepository;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    @Autowired
    private ZkbHighPartInfoRepository zkbHighPartInfoRepository;

    @Autowired
    private ZkbContractCatQuotaHistoryService zkbContractCatQuotaHistoryService;


    public String workFlowCheck(ZkbPartOrder selected, List<ZkbContract> contractList, AssetInfo assetInfo, String notGeAsset, String orderType){
        if(contractList.size() == 0){
            return "未查询出合同数据！";
        }else if(contractList.size() > 1){
            return "合同超过两条数据，请检查！";
        }else if(contractList.size() == 1) {
            ZkbContract ct = contractList.get(0);
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(ct.getId(), assetInfo.getUid());
            if (ZkbConstantUtil.T.equals(notGeAsset)) {
                if (ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                    return "非GE设备申请只能申请低端机！";
                }
            }

            if(zkbContractAsset != null){
                if(StrUtil.isEmpty(zkbContractAsset.getZone())){
                    return "设备Zone为空，请检查！";
                }else {
                   List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndZoneContains(ZkbPartOrderStep.PartOrderStep.ManagePending.toString(), zkbContractAsset.getZone());
                   if(zkbPartOrderUserConfigs.isEmpty()){
                       return "Zone未配置相应的区域经理，请检查！";
                   }
                }
            }

            //判断合同是否是CCSB
            if (ZkbContract.ContractType.CCSB.toString().equals(ct.getContractType())) {
                if (!ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)) {
                    return "CCSB类型合同只能进行人工申请！";
                }
            }
            //超声的depot不能申请YHAAO-22468
            if(ZkbContractAsset.AssetTypeEnum.depot.toString().equals(zkbContractAsset.getAssetType())){
                return "ZKB FE不能申请deport类型设备，请联系管理人员！";
            }

            //判断要不要进行超声高端机判断
            /*
             * 2023-12-26 YHAAO-20623 只有是CCS/Biomed合同且内修的时候才能申请
             */
            boolean isCheckCcs2 = true;
            if (ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Probe.toString().equals(orderType)) {
                if (zkbContractAsset != null && StringUtils.isNotEmpty(zkbContractAsset.getAssetType())) {
                    if (ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                        if (ZkbContract.ContractType.CCS.toString().equals(ct.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(ct.getContractType())) {
                            if (assetInfo.getRepairForm() == null || assetInfo.getRepairForm() != 1) {
                                isCheckCcs2 = false;
                            }
                        }else {
                            isCheckCcs2 = false;
                        }
                    }
                }
            } else if (ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)) {
                //人工申请只有在机型为超声高端机时才满足条件
                if ("2".equals(selected.getArtificialType())) {
                    if (ZkbContract.ContractType.CCS.toString().equals(ct.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(ct.getContractType())) {
                        if (assetInfo.getRepairForm() == null || assetInfo.getRepairForm() != 1) {
                            isCheckCcs2 = false;
                        }
                    } else {
                        isCheckCcs2 = true;
                    }
                }
            }

            if (!isCheckCcs2) {
                return "设备为超声高端机，请检查是否满足申请条件！";
            }

            if (ZkbPartOrder.OrderType.Parts.toString().equals(orderType)) {
                if (ZkbContract.YOrNEnum.Y.toString().equals(ct.getFiveNineNineFlag()) && ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                    if (ZkbConstantUtil.T.equals(ct.getFirstYearFlag()) && (zkbContractAsset.getApplyNum() == null || zkbContractAsset.getApplyNum() == 0)) {
                        Boolean r = zkbContractAssetService.checkHalfAsset(ct.getId(), assetInfo.getUid());
                        if (r == null) {
                            return "合同中没有高端机设备！";
                        } else if (!r) {
                            return "当前备件申请的设备已经超过50%的IB入保数额，此IB无法申请备件！";
                        }
                    }
                } else {
                    if (ZkbContract.ContractType.CCS.toString().equals(ct.getContractType()) ||
                        ZkbContract.ContractType.Biomed.toString().equals(ct.getContractType())) {
                        if (ct.getSpareQuota() == null || ct.getSpareQuota() < 0) {
                            return "合同备件数量小于0，请检查！";
                        }
                    } else {
                        if (ct.getSpareArtificail() == null || ct.getSpareArtificail() < 0) {
                            return "合同备件+人工+保养剩余名额小于0，请检查！";
                        }
                    }
                }
            } else if (ZkbPartOrder.OrderType.Probe.toString().equals(orderType)) {
                if (ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(ct.getQuotaDeductType())) {
                    if (ct.getProbeQuota() == null || ct.getProbeQuota() <= 0) {
                        return "合同探头数量小于等于0，请检查！";
                    }
                } else if (ZkbContract.QuotaDeductTypeEnum.Null.toString().equals(ct.getQuotaDeductType())) {
                    return "探头申请中名额扣除类型不能为N/A！";
                }
            } else if (ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)) {
                selected.setSsaKeyFlag(ZkbConstantUtil.F);
            } else if (ZkbPartOrder.OrderType.Tool.toString().equals(orderType)) {
                if (ZkbContract.ContractType.CCS.toString().equals(ct.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(ct.getContractType())) {
                    if (ct.getSpareQuota() == null || ct.getSpareQuota() < 0) {
                        return "合同备件数量小于0，请检查！";
                    }
                } else {
                    if (ct.getSpareArtificail() == null || ct.getSpareArtificail() < 0) {
                        return "合同备件+人工+保养剩余名额小于0，请检查！";
                    }
                }
            }
        }
        return "";
    }

    public void copyZkbPartOrder(ZkbPartOrder selected, UserAccount currentUser){
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

    public void handleQtyByOrderType(ZkbPartOrder selected, ZkbContract selectedContract, String notGeAsset, AssetInfo assetInfo){
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            selected.setQty(1);
        }else{
            selected.setQty(0);
            if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
                selected.setCatNum(1);
                if(ZkbConstantUtil.T.equals(notGeAsset)){
                    selected.setQty(1);
                }
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
                if(assetInfo == null || StringUtils.isEmpty(assetInfo.getUid()) || selectedContract == null){
                    selected.setArtificialType(ZkbConstantUtil.NORMAL_THREE);
                }else{
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(selectedContract.getId(),assetInfo.getUid());
                    if(ZkbContract.YOrNEnum.Y.toString().equals(selectedContract.getFiveNineNineFlag()) && ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType()) ){
                        selected.setArtificialType(ZkbConstantUtil.NORMAL_TWO);
                    } else{
                        if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                            selected.setArtificialType(ZkbConstantUtil.NORMAL_THREE);
                        }else if(ZkbContractAsset.AssetTypeEnum.depot.toString().equals(zkbContractAsset.getAssetType())){
                            selected.setArtificialType(ZkbConstantUtil.NORMAL_DEPOT);
                        }

                    }
                }
            }
        }
    }

    public List<ZkbPartOrderUserConfig> queryUserConfig(String orderType, UserAccount currentUser, String notGeAsset){
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();
        if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType) || ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) >-1){
                if(ZkbConstantUtil.T.equals(notGeAsset)){
                    zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameContainsAndDeletedFlagIsNull(ZkbPartOrderStep.PartOrderStep.TsePending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                }else{
                    zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameContainsAndDeletedFlagIsNull(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                }
            }else{
                if(ZkbConstantUtil.T.equals(notGeAsset)){
                    zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameIsNotContainingAndDeletedFlagIsNull(ZkbPartOrderStep.PartOrderStep.TsePending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                }else{
                    zkbPartOrderUserConfigList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameIsNotContainingAndDeletedFlagIsNull(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString(),ZkbPartOrder.CONFIG_US,ZkbPartOrder.DATA_TYPE_TEST);
                }
            }
        }
        return zkbPartOrderUserConfigList;
    }

    public void setZkbPartOrderByWorkFlowSelected(ZkbPartOrder selected, HashMap<String, Object> selectedWorkFlow, String orderType, AssetInfo assetInfo, ZkbContractAsset zkbContractAsset,String notGeAsset, ZkbContract zkbContract, UserAccount userAccount) {
        selected.setRegion(zkbContract.getRegion());
        if (selectedWorkFlow.get("customerName") != null) {
            selected.setHospitalRepairer(selectedWorkFlow.get("customerName").toString());
        } else {
            selected.setHospitalRepairer("");
        }
        if (selectedWorkFlow.get("customerTel") != null) {
            selected.setHospitalRepairerPhone(selectedWorkFlow.get("customerTel").toString());
        } else {
            selected.setHospitalRepairerPhone("");
        }
        if (selectedWorkFlow.get("hospitalName") != null) {
            selected.setHospitalName(selectedWorkFlow.get("hospitalName").toString());
        } else {
            selected.setHospitalName("");
        }
        if (selectedWorkFlow.get("faultDescription") != null) {
            selected.setFaultDescription(selectedWorkFlow.get("faultDescription").toString());
        } else {
            selected.setFaultDescription("");
        }
        if(zkbContractAsset != null){
            selected.setZone(zkbContractAsset.getZone());
        }

        //区域经理块显示
        List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = new ArrayList<ZkbPartOrderUserConfig>();
        if(userAccount.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndZoneContainsAndLoginNameContains(ZkbPartOrderStep.PartOrderStep.ManagePending.toString(), selected.getZone(),ZkbPartOrder.DATA_TYPE_TEST);
        }else{
            zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndZoneContainsAndLoginNameIsNotContaining(ZkbPartOrderStep.PartOrderStep.ManagePending.toString(), selected.getZone(),ZkbPartOrder.DATA_TYPE_TEST);
        }
        Optional<ZkbPartOrderUserConfig>  zkbPartOrderUserConfig = zkbPartOrderUserConfigs.stream().findFirst();
        if(zkbPartOrderUserConfig.isPresent()){
            selected.setRegionalManagerEmail(zkbPartOrderUserConfig.get().getEmail());
            selected.setRegionalManagerPhone(zkbPartOrderUserConfig.get().getPhone());
            selected.setRegionalManagerName(zkbPartOrderUserConfig.get().getName());
        }

        if (ZkbPartOrder.OrderType.Parts.toString().equals(orderType)) {
            if (assetInfo != null && assetInfo.getAssetGroup() != null) {
                if (assetInfo.getAssetGroup() == 2) {
                    if (StringUtils.isEmpty(zkbContractAsset.getAssetType())) {
                        selected.setArtificialType(ZkbConstantUtil.NORMAL_THREE);
                    } else if (ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                        selected.setArtificialType(ZkbConstantUtil.NORMAL_TWO);
                    } else if (ZkbContractAsset.AssetTypeEnum.lowMachine.toString().equals(zkbContractAsset.getAssetType())) {
                        selected.setArtificialType(ZkbConstantUtil.NORMAL_THREE);
                    } else if (ZkbContractAsset.AssetTypeEnum.depot.toString().equals(zkbContractAsset.getAssetType())) {
                        selected.setArtificialType(ZkbConstantUtil.NORMAL_DEPOT);
                    } else if (ZkbContractAsset.AssetTypeEnum.empty.toString().equals(zkbContractAsset.getAssetType())) {
                        selected.setArtificialType(ZkbConstantUtil.NORMAL_THREE);
                    }
                } else if (assetInfo.getAssetGroup() == 1 && StrUtil.isNotEmpty(assetInfo.getMainDirectory())) {
                    String[] xrs = {"WIX MAMMO", "WIX SURGEY", "WIX RF", "WIX DXR", "WIX BMD"};
                    List<String> xrList = Arrays.asList(xrs);;
                    if ("CT".equals(assetInfo.getMainDirectory())) {
                        selected.setAssetTypeDi("CT");
                    } else if (xrList.contains(assetInfo.getMainDirectory())) {
                        selected.setAssetTypeDi("XR");
                    } else {
                        selected.setAssetTypeDi("");
                    }
                }
            }

        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            if(ZkbConstantUtil.T.equals(notGeAsset)){
                if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())){
                    selected.setHighCatFlag(ZkbConstantUtil.F);
                }
                selected.setProvideNewContract(ZkbConstantUtil.T);
            }
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            //selected.setScoreFlag(CopyByInvok.F);
            selected.setMaintainFlag(ZkbConstantUtil.F);
        }
    }

    public void orderTypeChange(ZkbPartOrder selected, String orderType, UserAccount currentUser){
        if(selected == null){
            selected = new ZkbPartOrder();
        }
        selected.setOrderType(orderType);
        selected.setRequestDate(new Date());
        selected.setRequestUser(currentUser.getName());
        selected.setRequestPhone(currentUser.getTelephone());
        selected.setSupplementFlag(ZkbConstantUtil.F);
        selected.setSsaKeyFlag(null);
        selected.setMaintainFlag(null);
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            selected.setCatNum(1);
        }
    }

    public String catSelected(ZkbPartOrder selected, String notGeAsset, ZkbContract selectedContract){
        String valueTypeTips = "";
        if(ZkbConstantUtil.T.equals(notGeAsset)){
            selected.setQty(1);
            valueTypeTips = "";
        }else{
            if(StringUtils.isNotEmpty(selectedContract.getQuotaDeductType())){
                if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType())){
                    if(!ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                        if(selected.getZkbPartOrderCat() != null){
                            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                selected.setQty(1);
                                valueTypeTips = "";
                            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                selected.setQty(null);
                            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                selected.setQty(1);
                                valueTypeTips = "";
                            }
                        }
                    }else {
                        if(StringUtils.isNotEmpty(selected.getContractCatId())){
                            ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(selected.getContractCatId());
                            if(zkbContractCat != null && ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                selected.setQty(4);
                            }else{
                                selected.setQty(0);
                            }
                        }
                    }
                    if(ZkbConstantUtil.T.equals(selected.getTenantNewCatFlag()) && ZkbConstantUtil.F.equals(selected.getProvideNewContract())){
                        selected.setQty(selected.getQty() * 2);
                    }
                }else if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(selectedContract.getQuotaDeductType())) {
                    if(StringUtils.isEmpty(selected.getHighCatFlag()) || ZkbConstantUtil.F.equals(selected.getHighCatFlag())){
                        if(selected.getZkbPartOrderCat() != null){
                            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                selected.setQty(1);
                                valueTypeTips = "";
                            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                selected.setQty(0);
                                valueTypeTips = "您的选择为高值探头，请确认合同中是否有此权益！";
                            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                                selected.setQty(2);
                                valueTypeTips = "";
                            }
                        }
                        if(ZkbConstantUtil.T.equals(selected.getTenantNewCatFlag())){
                            selected.setQty(selected.getQty() * 2);
                        }
                    }else{
                        if(StringUtils.isNotEmpty(selected.getContractCatId())){
                            ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(selected.getContractCatId());
                            if(zkbContractCat != null && ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                if(ZkbConstantUtil.T.equals(selected.getTenantNewCatFlag())){
                                    selected.setQty(8);
                                }else{
                                    selected.setQty(4);
                                }
                            }else{
                                selected.setQty(0);
                            }
                        }
                    }
                }
            }else {
                selected.setQty(null);
            }
        }
        return valueTypeTips;
    }

    public void provideNewContractChange(ZkbPartOrder selected){
        if(selected.getQty() == null){
            return;
        }
        int basicQty = 1;
        if(ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
            ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(selected.getContractCatId());
            if(zkbContractCat != null && ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                basicQty = 4;
            }else{
                basicQty = 0;
            }
        }
        if(ZkbConstantUtil.F.equals(selected.getProvideNewContract())){
            selected.setQty(basicQty * 2);
        }else if(ZkbConstantUtil.T.equals(selected.getProvideNewContract())){
            selected.setQty(basicQty);
        }else {
            selected.setQty(basicQty);
        }
    }

    public boolean checkPcbMvsTotal(ZkbContract zkbContract){
        boolean r = false;
        if(zkbContract != null && (ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType()) ||
            "PCB MVS".equals(zkbContract.getContractType()))){
            int total = zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal();
            if(total >= 1000){
                r = true;
            }
        }
        return r;
    }

    /**
     * 申请保存前必填项Check，
     * @return
     */
    public String saveCheck(ZkbPartOrder selected,
                            ZkbContract selectedContract,
                            String faultUnClosedSr,
                            List<HashMap<String, Object>> v2_listHashMap,
                            List<ZkbContract> contractList,
                            AssetInfo assetInfo,
                            List<V2_BlobObject> v2_blobObjectList,
                            List<V2_BlobObject> v2_probe_blobObjectList,
                            String selectionTenantInfo){
        StringBuilder errorLog = new StringBuilder();
        if(StringUtils.isEmpty(selected.getOrderType())){
            errorLog.append("订单类型不能为空！");
        }
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            if(StringUtils.isEmpty(selected.getRadiationQuotaType())){
                errorLog.append("名额扣除类型不能为空！");
            }
        }
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(StringUtils.isEmpty(faultUnClosedSr)){
                errorLog.append("同一故障需继续订购且有未关闭的SR不能为空！");
            }
        }

        if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(StringUtils.isEmpty(selected.getMaintainFlag())){
                errorLog.append("保养申请不能为空！");
            }
        }
        if(v2_listHashMap == null || v2_listHashMap.size() == 0 ){
            errorLog.append("工单数据不能为空！");
        }
        if(contractList == null || contractList.size() == 0){
            errorLog.append("合同数据不能为空！");
        }else if(contractList.size() > 1){
            errorLog.append("合同数据大于一条！");
        }
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            if((selected.getZkbPartOrderItems() ==null || selected.getZkbPartOrderItems().size() == 0) ){
                errorLog.append("额度申请明细不能为空！");
            }
        }

        if(!ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType()) && !ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            //超声类add校验
            if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup() == 2){
                List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigs = zkbPartOrderUserConfigRepository.findByConfigTypeAndZoneContainsAndAssetTypeAndDeletedFlagIsNull(ZkbPartOrderStep.PartOrderStep.TechnicalPending.name(), selected.getZone(), "US");
                if(CollectionUtils.isEmpty(zkbPartOrderUserConfigs)){
                    errorLog.append("未匹配到相应的技术支持审批人员！");
                }
                //errorLog.append("在线工程师不能为空！");
            }
        }
        if(!ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(v2_blobObjectList == null || v2_blobObjectList.size() == 0){
                errorLog.append("附件不能为空！");
            }
        }else{
            if(ZkbConstantUtil.F.equals(selected.getSsaKeyFlag()) && checkPcbMvsTotal(selectedContract)){
                if(ZkbConstantUtil.F.equals(selected.getScoreFlag()) || StrUtil.isEmpty(selected.getScoreFlag())){
                    errorLog.append("该合同只支持积分申请！");
                }
            }
            if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                if(ZkbContract.ContractType.CCS.name().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.name().equals(selectedContract.getContractType())){
                    errorLog.append("CCS或Biomed合同保养申请时只能使用积分申请！");
                }
            }
            if("t".equals(selected.getScoreFlag())){
                boolean r = zkbIntegralService.checkScoreFlag(selectionTenantInfo,selected.getQty());
                if(!r){
                    errorLog.append("积分不够！");
                }
            }
        }
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(selectedContract.getQuotaDeductType()) && ZkbConstantUtil.T.equals(selected.getTenantNewCatFlag())){
                if(ZkbConstantUtil.T.equals(selected.getProvideNewContract())){
                    if(v2_probe_blobObjectList == null || v2_probe_blobObjectList.size() == 0){
                        errorLog.append("院方合同证明不能为空！");
                    }else if(v2_probe_blobObjectList.size() > 1){
                        errorLog.append("院方合同证明只能上传单个！");
                    }
                }
            }
        }
        if(selected.getRemainQty() == null){
            errorLog.append("剩余数量不能为空！");
        }else if(selected.getRemainQty() < 0){
            errorLog.append("剩余数量不足！");
        }
        return errorLog.toString();

    }

    /**
     * 申请保存期获取实时的合同校验名额是否足够，防止录入申请过程中名额被扣减后造成名额不足，导致名额出现负数的问题
     * @param selected
     * @param zkbContract
     * @param assetInfo
     * @param notGeAsset
     * @return
     */
    public String checkCurrentContractQuota(ZkbPartOrder selected, ZkbContract zkbContract, AssetInfo assetInfo,String notGeAsset){
        StringBuilder reLog = new StringBuilder();
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())){
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
            if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())){
                if(ZkbConstantUtil.T.equals(zkbContract.getFirstYearFlag()) && (zkbContractAsset.getApplyNum() == null || zkbContractAsset.getApplyNum() == 0)){
                    Boolean r = zkbContractAssetService.checkHalfAsset(zkbContract.getId(),assetInfo.getUid());
                    if(r == null){
                        reLog.append("合同中没有高端机设备！");
                    }else if(!r){
                        reLog.append("当前备件申请的设备已经超过50%的IB入保数额，此IB无法申请备件！");
                    }
                }
            }else{
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    Integer contractCount = zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota();
                    if(contractCount - selected.getQty() < 0){
                        reLog.append("合同备件剩余名额不足，请检查！");
                    }
                }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())) {
                    if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                        if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                            Integer contractCount = zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota();
                            if(contractCount - selected.getQty() < 0){
                                reLog.append("备件高值剩余名额不足，请检查！");
                            }
                        }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())){
                            Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                            if(contractCount - selected.getQty() < 0){
                                reLog.append("合同备件+人工+保养剩余名额不足，请检查！");
                            }
                        }
                    }else{
                        Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                        if(contractCount - selected.getQty() < 0){
                            reLog.append("合同备件+人工+保养剩余名额不足，请检查！");
                        }
                    }
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(selected.getRadiationQuotaType())){
                        Integer contractCount = zkbContract.getConsumableQuota() == null ? 0 : zkbContract.getConsumableQuota();
                        if(contractCount - selected.getQty() < 0){
                            reLog.append("耗材剩余名额不足，请检查！");
                        }
                    }
                }else{
                    Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                    if(contractCount - selected.getQty() < 0){
                        reLog.append("合同备件+人工+保养剩余名额不足，请检查！");
                    }
                }
            }
            if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
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
                                }else if(ZkbHighPartInfo.PartTypeEnum.consumable.toString().equals(z.getPartType())){
                                    el += "备件号："+i.getPartNo()+"属于耗材，不可订购！";
                                }else if(ZkbHighPartInfo.PartTypeEnum.tool.toString().equals(z.getPartType())){
                                    el += "备件号："+i.getPartNo()+"属于工具，不可订购！";
                                }
                            }
                        }
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())){
                        for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                            List<ZkbHighPartInfo> zkbHighPartInfos = zkbHighPartInfoRepository.findBulbTube(i.getPartNo());
                            if(zkbHighPartInfos != null && zkbHighPartInfos.size() > 0){
                                el += "备件号："+i.getPartNo()+"属于球管，不可订购！";
                            }
                            List<ZkbHighPartInfo> consumableInfos = zkbHighPartInfoRepository.findConsumable(i.getPartNo());
                            if(consumableInfos != null && consumableInfos.size() > 0){
                                el += "备件号："+i.getPartNo()+"属于耗材，不可订购！";
                            }
                            List<ZkbHighPartInfo> toolInfos = zkbHighPartInfoRepository.findTool(i.getPartNo());
                            if(toolInfos != null && toolInfos.size() > 0){
                                el += "备件号："+i.getPartNo()+"属于工具，不可订购！";
                            }
                        }
                    }
                    reLog.append(el);
                }
                if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(selected.getRadiationQuotaType())){
                    String el2 = "";
                    for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                        List<ZkbHighPartInfo> consumableInfos = zkbHighPartInfoRepository.findConsumable(i.getPartNo());
                        if(consumableInfos == null || consumableInfos.size() == 0){
                            el2 += "备件号："+i.getPartNo()+"不属于耗材，不可订购！";
                        }
                    }
                    reLog.append(el2);
                }
            }
        } else if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            String probeType = "";
            if(StringUtils.isEmpty(zkbContract.getQuotaDeductType())){
                reLog.append("合同探头扣除类型为空，请检查！");
            }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Any.toString();
                    Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                    if(contractCount - selected.getQty() < 0){
                        reLog.append("合同探头任意剩余名额不足，请检查！");
                    }
                    if(ZkbConstantUtil.F.equals(notGeAsset) && ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog.append("高值探头不能选用任意名额！");
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString();
                    Integer contractCount = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
                    if(contractCount - selected.getQty() < 0){
                        reLog.append("合同探头普通剩余名额不足，请检查！");
                    }
                    if(ZkbConstantUtil.F.equals(notGeAsset) && !ZkbPartOrderCat.ValueType.NormalValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog.append("探头普通剩余名额只能选择普通探头！");
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Special.toString();
                    Integer contractCount = zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial();
                    if(contractCount - selected.getQty() < 0){
                        reLog.append("合同探头特殊剩余名额不足，请检查！");
                    }
                    if(ZkbConstantUtil.F.equals(notGeAsset) && !ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog.append("探头特殊剩余名额只能选择特殊探头！");
                    }
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(selected.getProbeQuotaType())){
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.Normal.toString();
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(selected.getContractCatId());
                    if(zkbContractCat != null && ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                        probeType = ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString();
                        Integer contractCount = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
                        if(contractCount - selected.getQty() < 0){
                            reLog.append("合同探头普通剩余名额不足，请检查！");
                        }
                    }
                }
            }else if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())){
                if(ZkbConstantUtil.F.equals(selected.getHighCatFlag())){
                    if(ZkbConstantUtil.F.equals(notGeAsset) && ZkbPartOrderCat.ValueType.HighValue.toString().equals(selected.getZkbPartOrderCat().getValueType())){
                        reLog.append("不能选择高值探头！");
                    }
                    probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString();
                    Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                    if(contractCount - selected.getQty() < 0){
                        reLog.append("合同探头剩余名额不足，请检查！");
                    }
                }else if(ZkbConstantUtil.T.equals(selected.getHighCatFlag())){
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(selected.getContractCatId());
                    if(zkbContractCat != null && ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                        probeType = ZkbContractCatQuotaHistory.ProbeTypeStatus.OldProbe.toString();
                        Integer contractCount = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
                        if(contractCount - selected.getQty() < 0){
                            reLog.append("合同探头剩余名额不足，请检查！");
                        }
                    }
                }
            }
            if(ZkbConstantUtil.T.equals(zkbContract.getAssetQuotaType()) && StrUtil.isNotEmpty(probeType)){
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
                boolean r = zkbContractCatQuotaHistoryService.checkQuotaByHistory(zkbContract, zkbContractAsset, probeType, selected.getQty());
                if(!r){
                    if(ZkbContractAsset.AssetTypeEnum.highMachine.toString().equals(zkbContractAsset.getAssetType())) {
                        reLog.append("该合同中的高端机名额不足,请检查！");
                    }else{
                        reLog.append("该合同中的低端机名额不足,请检查！");
                    }
                }
            }
        } else if(ZkbPartOrder.OrderType.Artificial.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){

            }else if(ZkbConstantUtil.T.equals(selected.getMaintainFlag())){
                if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){
                }else{
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
                    if(ZkbConstantUtil.F.equals(selected.getMaintainFirstFlag()) || ZkbConstantUtil.F.equals(zkbContractAsset.getSupportMaintainFlag())){
                        Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                        if(contractCount - selected.getQty() < 0){
                            reLog.append("备件+人工+保养剩余名额不足，请检查！");
                        }
                    }
                }
            }else{
                if(!ZkbConstantUtil.NORMAL_TWO.equals(selected.getArtificialType())){
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),assetInfo.getUid());
                    if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){

                    } else if(ZkbConstantUtil.NORMAL_ONE.equals(selected.getArtificialType()) && ZkbConstantUtil.T.equals(selected.getSsaKeyFlag())){

                    } else if(ZkbConstantUtil.T.equals(selected.getScoreFlag())){

                    } else{
                        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                            Integer contractCount = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                            if(contractCount - selected.getQty() < 0){
                                reLog.append("人工剩余名额不足，请检查！");
                            }
                        }else{
                            Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                            if(contractCount - selected.getQty() < 0){
                                reLog.append("备件+人工+保养剩余名额不足，请检查！");
                            }
                        }
                    }
                }
            }
        } else if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                Integer contractCount = zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota();
                if(contractCount - selected.getQty() < 0){
                    reLog.append("合同备件剩余名额不足，请检查！");
                }
            }else{
                Integer contractCount = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
                if(contractCount - selected.getQty() < 0){
                    reLog.append("合同备件+人工+保养剩余名额不足，请检查！");
                }
            }
            String toolEl = "";
            for(ZkbPartOrderItem i : selected.getZkbPartOrderItems()){
                List<ZkbHighPartInfo> toolInfos = zkbHighPartInfoRepository.findTool(i.getPartNo());
                if(toolInfos == null || toolInfos.size() == 0){
                    toolEl += "备件号："+i.getPartNo()+"不属于工具，不可订购！";
                }
            }
            reLog.append(toolEl);
        }

        return reLog.toString();
    }

    /**
     * 备件/工具明细保存前check
     * @param selected
     * @param selectedContract
     * @param assetInfo
     * @param zkbPartOrderItem
     * @return
     */
    public String checkSaveItem(ZkbPartOrder selected, ZkbContract selectedContract, AssetInfo assetInfo, ZkbPartOrderItem zkbPartOrderItem){
        StringBuilder rsLog = new StringBuilder();
        if(ZkbPartOrder.OrderType.Parts.toString().equals(selected.getOrderType())) {
            if (ZkbContract.ContractType.DI.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(selectedContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(selectedContract.getContractType())) {
                if (assetInfo.getAssetGroup().equals(1)) {
                    if (ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(selected.getRadiationQuotaType())) {
                        ZkbHighPartInfo z = zkbHighPartInfoRepository.findFirstByPartNo(zkbPartOrderItem.getPartNo());
                        if (z != null) {
                            if (ZkbHighPartInfo.PartTypeEnum.highPart.toString().equals(z.getPartType())) {
                                rsLog.append(zkbPartOrderItem.getPartNo() + "属于高值备件，不可订购！");
                            } else if (ZkbHighPartInfo.PartTypeEnum.bulbTube.toString().equals(z.getPartType())) {
                                rsLog.append(zkbPartOrderItem.getPartNo() + "属于球管，不可订购！");
                            } else if (ZkbHighPartInfo.PartTypeEnum.consumable.toString().equals(z.getPartType())) {
                                rsLog.append(zkbPartOrderItem.getPartNo() + "属于耗材，不可订购！");
                            } else if (ZkbHighPartInfo.PartTypeEnum.tool.toString().equals(z.getPartType())) {
                                rsLog.append(zkbPartOrderItem.getPartNo() + "属于工具，不可订购！");
                            }
                        }
                    } else if (ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(selected.getRadiationQuotaType())) {
                        List<ZkbHighPartInfo> zkbHighPartInfos = zkbHighPartInfoRepository.findBulbTube(zkbPartOrderItem.getPartNo());
                        if (zkbHighPartInfos != null && zkbHighPartInfos.size() > 0) {
                            rsLog.append(zkbPartOrderItem.getPartNo() + "属于球管，不可订购！");
                        }
                        List<ZkbHighPartInfo> consumableInfos = zkbHighPartInfoRepository.findConsumable(zkbPartOrderItem.getPartNo());
                        if (consumableInfos != null && consumableInfos.size() > 0) {
                            rsLog.append(zkbPartOrderItem.getPartNo() + "属于耗材，不可订购！");
                        }
                        List<ZkbHighPartInfo> toolInfos = zkbHighPartInfoRepository.findTool(zkbPartOrderItem.getPartNo());
                        if (toolInfos != null && toolInfos.size() > 0) {
                            rsLog.append(zkbPartOrderItem.getPartNo() + "属于工具，不可订购！");
                        }
                    }
                }
                if (ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(selected.getRadiationQuotaType())) {
                    List<ZkbHighPartInfo> consumableInfos = zkbHighPartInfoRepository.findConsumable(zkbPartOrderItem.getPartNo());
                    if (consumableInfos == null || consumableInfos.size() == 0) {
                        rsLog.append(zkbPartOrderItem.getPartNo() + "不属于耗材，不可订购！");
                    }
                }
            }
        }
        if(ZkbPartOrder.OrderType.Tool.toString().equals(selected.getOrderType())) {
            List<ZkbHighPartInfo> toolInfos = zkbHighPartInfoRepository.findTool(zkbPartOrderItem.getPartNo());
            if (toolInfos == null || toolInfos.size() == 0) {
                rsLog.append(zkbPartOrderItem.getPartNo() + "不属于工具，不可订购！");
            }
        }
        return rsLog.toString();
    }


    /**
     * 保存前提示扣除名额，根据申请类型以及合同类型
     * @param zkbPartOrder
     * @param zkbContract
     * @param assetInfo
     * @return
     */
    public String preSave(ZkbPartOrder zkbPartOrder, ZkbContract zkbContract, AssetInfo assetInfo) {
        if (ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), assetInfo.getUid());
            if (ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())) {
                return "该申请不扣任何名额！";
            } else {
                if(StrUtil.isEmpty(zkbPartOrder.getRadiationQuotaType())){
                    if (ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                        return "当前合同剩余备件名额为"+zkbContract.getSpareQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个备件名额！";
                    }else{
                        return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                    }
                }else{
                    if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        return "当前合同剩余高值备件名额为"+zkbContract.getPartHighQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个高值备件名额！";
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Part.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        return "当前合同剩余备件名额为"+zkbContract.getSpareQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个备件名额！";
                    }else if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                        return "当前合同剩余耗材名额为"+zkbContract.getConsumableQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个耗材名额！";
                    }
                }
                /*if (ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                    return "当前合同剩余备件名额为"+zkbContract.getSpareQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个备件名额！";
                } else if (ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())) {
                    if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                        if(ZkbPartOrder.RadiationQuotaTypeEnum.PartsHigh.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                            return "当前合同剩余高值备件名额为"+zkbContract.getPartHighQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个高值备件名额！";
                        }else if(ZkbPartOrder.RadiationQuotaTypeEnum.SpareArtificial.toString().equals(zkbPartOrder.getRadiationQuotaType())){
                            return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                        }
                    }else{
                        return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                    }
                }else{
                    return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                }*/
            }
        } else if (ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            if (ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType()) || StringUtils.isEmpty(zkbContract.getQuotaDeductType())) {
                if (StringUtils.isEmpty(zkbPartOrder.getHighCatFlag())) {
                    return "当前合同剩余探头名额为"+zkbContract.getProbeQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头名额！";
                } else if (ZkbConstantUtil.F.equals(zkbPartOrder.getHighCatFlag())) {
                    return "当前合同剩余探头名额为"+zkbContract.getProbeQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头名额！";
                } else if (ZkbConstantUtil.T.equals(zkbPartOrder.getHighCatFlag())) {
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                    if (zkbContractCat != null) {
                        if (ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())) {
                            //扣除名额，同时将cat设置不可用
                            return "当前合同剩余探头名额为"+zkbContract.getProbeQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头名额以及扣除SN编号:"+zkbContractCat.getSnNo()+"！";
                        } else {
                            if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                                return "该申请扣除SN编号:"+zkbContractCat.getSnNo()+"！";
                            }else{
                                return "当前合同剩余探头名额为"+zkbContract.getProbeQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头名额以及扣除SN编号:"+zkbContractCat.getSnNo()+"！";
                            }
                        }
                    }
                }
            }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    return "当前合同剩余探头任意名额为"+zkbContract.getProbeQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头任意名额！";
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    return "当前合同剩余探头普通名额为"+zkbContract.getProbeQuotaNormal()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头普通名额！";
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    return "当前合同剩余探头特殊名额为"+zkbContract.getProbeQuotaSpecial()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头特殊名额！";
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                    if(zkbContractCat != null){
                        if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                            return "当前合同剩余探头普通名额为"+zkbContract.getProbeQuotaNormal()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个普通名额以及扣除SN编号:"+zkbContractCat.getSnNo()+"！";
                        }else {
                            if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                                return "该申请扣除SN编号:"+zkbContractCat.getSnNo()+"！";
                            }else{
                                return "当前合同剩余探头名额为"+zkbContract.getProbeQuotaNormal()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个探头名额以及扣除SN编号:"+zkbContractCat.getSnNo()+"！";
                            }
                        }
                    }
                }
            }
        } else if (ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            //region  人工
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFlag())){
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                    //扣除积分
                    return "该申请扣除积分:"+ zkbPartOrder.getQty() * 200;
                }else{
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), assetInfo.getUid());
                    if(ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                        if(ZkbConstantUtil.F.equals(zkbPartOrder.getMaintainFirstFlag())){
                            return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                        }else {
                            return "该申请不扣任何名额！";
                        }
                    }else{
                        return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                    }
                }
            }else{
                if(ZkbConstantUtil.NORMAL_TWO.equals(zkbPartOrder.getArtificialType())){
                    if(!ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) && !ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                        return "该申请不扣任何名额";
                    }
                }
                if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                    if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(zkbPartOrder.getCcsbApplyType())){
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){
                            Integer remindQty = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                            if(remindQty >= 1){
                                return "当前合同剩余人工名额为"+zkbContract.getGeArtificialSupportTimes()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个人工名额！";
                            }else{
                                return "该申请不扣任何名额！";
                            }
                        }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                            return "该申请不扣任何名额！";
                        }
                    }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(zkbPartOrder.getCcsbApplyType())){
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) {
                            Integer remindQty = zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota();
                            if (remindQty >= 1) {
                                return "当前合同剩余保养名额为" + zkbContract.getCcsbMaintenanceQuota() + "；此次申请扣除" + zkbPartOrder.getQty() + "个保养名额！";
                            } else {
                                return "该申请不扣任何名额！";
                            }
                        }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                            return "该申请不扣任何名额！";
                        }
                    }else{
                        return "该申请名额扣除有误";
                    }
                }
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), assetInfo.getUid());
                if((ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                    return "该申请不扣任何名额";
                }
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getSsaKeyFlag())){
                    return "该申请不扣任何名额";
                }
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                    //扣除积分
                    return "该申请扣除积分:"+ zkbPartOrder.getQty() * 200;
                }else{
                    if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){
                        //内修
                        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                            return "当前合同剩余人工名额为"+zkbContract.getGeArtificialSupportTimes()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个人工名额！";
                        }else{
                            return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                        }
                    }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){
                        //外修
                        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                            return "该申请不扣任何名额";
                        }else{
                            return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
                        }
                    }
                }
            }
            //endregion
        } else if (ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) ||
                    ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                //备件名额
                return "当前合同剩余备件名额为"+zkbContract.getSpareQuota()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个备件名额！";
            }else{
                //联合名额
                return "当前合同剩余联合名额为"+zkbContract.getSpareArtificail()+"；此次申请扣除"+ zkbPartOrder.getQty() +"个联合名额！";
            }
        }
        return "";
    }



}



