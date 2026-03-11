package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ZkbIntegralOrderApplyService extends MicroServiceInvoker {

    @Autowired
    ZkbPartOrderStepRepository zkbPartOrderStepRepository;

    @Autowired
    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;

    @Autowired
    private ZkbContractRepository zkbContractRepository;

    @Autowired
    private ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;

    @Autowired
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;

    @Autowired
    private ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    Logger logger = LoggerFactory.getLogger(ZkbIntegralOrderApplyService.class);

    public String checkAsset(AssetInfo assetInfo) {
        if (assetInfo==null) {
            return "设备不存在！";
        }
        if(assetInfo.getIsDeleted() != null && assetInfo.getIsDeleted()){
            return "设备已删除，不能继续申请！";

        }
        if(!assetInfo.getIsValid()){
            return "设备已报废，不能继续申请！";
        }
        if(assetInfo.getRepairForm() ==null || (assetInfo.getRepairForm() != 1 && assetInfo.getRepairForm() != 2)){
            return "设备维修类型有异常，请检查！";
        }
        return "";
    }

    public List<HashMap<String, Object>> getQueryWorkFlowList(V2_WorkOrder v2_workOrder,String tenantUID){
        List<HashMap<String, Object>> v2_listHashMap = new ArrayList<>();
        List<SearchFilter> assetFilters = new ArrayList<>();
        //assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 1));
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));

        if(v2_workOrder.getServiceRequest() != null && StringUtils.isNotEmpty(v2_workOrder.getServiceRequest().getSrNum())){
            assetFilters.add(new SearchFilter("serviceRequest.srNum", SearchFilter.Operator.EQ, v2_workOrder.getServiceRequest().getSrNum()));
        }
        if(v2_workOrder.getAssetInfo() != null && StringUtils.isNotEmpty(v2_workOrder.getAssetInfo().getSystemId())){
            assetFilters.add(new SearchFilter("assetInfo.systemId", SearchFilter.Operator.EQ, v2_workOrder.getAssetInfo().getSystemId()));
        }
        if(v2_workOrder.getAssetInfo() != null && StringUtils.isNotEmpty(v2_workOrder.getAssetInfo().getDepartNum())){
            assetFilters.add(new SearchFilter("assetInfo.departNum", SearchFilter.Operator.EQ, v2_workOrder.getAssetInfo().getDepartNum()));
        }
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 2));
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 3));

        List<V2_WorkOrder> v2_workOrderList = v2_workOrderRepository.findBySearchFilter(assetFilters);
        for(V2_WorkOrder v : v2_workOrderList){
            if(v != null) {
                HashMap<String, Object> m = new HashMap<>(20);
                m.put("id", v.getId());
                Optional.ofNullable(v.getServiceRequest()).ifPresent(t -> {
                    m.put("srNum", v.getServiceRequest().getSrNum());
                    m.put("customerName", v.getServiceRequest().getCustomerName());
                    m.put("customerTel", v.getServiceRequest().getCustomerTel());
                    m.put("faultDescription", v.getServiceRequest().getRequestReason());
                    m.put("requestTime", v.getServiceRequest().getRequestTime());
                });
                Optional.ofNullable(v.getAssetInfo()).ifPresent(t -> {
                    m.put("name", v.getAssetInfo().getName());
                    m.put("systemId", v.getAssetInfo().getSystemId());
                    m.put("assetId", v.getAssetInfo().getUid());
                    m.put("hospitalName", v.getAssetInfo().getClinicalDeptName());
                    m.put("makerShortName", v.getAssetInfo().getManufacture());
                    m.put("oneQrCode", v.getAssetInfo().getQrCode());
                    m.put("departNum", v.getAssetInfo().getDepartNum());
                });
                v2_listHashMap.add(m);
            }
        }
        return v2_listHashMap;
    }

    public String workFlowCheck(ZkbIntegralOrder selected, List<ZkbContract> contractList, AssetInfo assetInfo, UserAccount userAccount, ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo){
        if(contractList.size() == 0){
            return "未查询出合同数据！";
        }else if(contractList.size() > 1){
            return "合同超过两条数据，请检查！";
        }else if(contractList.size() == 1) {
            ZkbContract ct = contractList.get(0);
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(ct.getId(), assetInfo.getUid());

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

            //超声的depot不能申请YHAAO-22468
            if(ZkbContractAsset.AssetTypeEnum.depot.toString().equals(zkbContractAsset.getAssetType())){
                return "ZKB FE不能申请deport类型设备，请联系管理人员！";
            }

            /*boolean isCheckCcs2 = true;
            if ("2".equals(selected.getArtificialType())) {
                if (ZkbContract.ContractType.CCS.toString().equals(ct.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(ct.getContractType())) {
                    if (assetInfo.getRepairForm() == null || assetInfo.getRepairForm() != 1) {
                        isCheckCcs2 = false;
                    }
                } else {
                    isCheckCcs2 = true;
                }
            }

            if (!isCheckCcs2) {
                return "设备为超声高端机，请检查是否满足申请条件！";
            }*/
            if(ZkbIntegralOrder.OrderTypeEnum.EngineerOnSiteMaintenance.name().equals(selected.getOrderType())){
                if (!ZkbContract.ContractType.CCS.toString().equals(ct.getContractType()) && !ZkbContract.ContractType.Biomed.toString().equals(ct.getContractType())) {
                    return "兑换GE工程师现场保养合同类型只能是CCS或Biomed！";
                }
            }
            String rs = checkScoreFlagValid(ct, selected.getOrderType(), userAccount);
            zkbIntegralOrderStatusVo.setScoreCanApply(StrUtil.isEmpty(rs));
            if(StrUtil.isNotEmpty(rs)){
                return"积分申请不符合条件：" + rs;
            }
        }
        return "";
    }

    public List<ZkbContract> queryContractByTenantUid(UserAccount userAccount){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        filters.add(new SearchFilter("startDate", SearchFilter.Operator.LTE, new Date()));
        filters.add(new SearchFilter("endDate", SearchFilter.Operator.GT, DateUtils.addDays(new Date(), -1)));
        List<ZkbContract> zkbContractList = zkbContractRepository.findBySearchFilter(filters);
        return zkbContractList;
    }

    public List<ZkbIntegralOrder> queryIntegralOrderByCreatedBy(UserAccount userAccount,String engineerNo){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbIntegralOrder.OrderTypeEnum.ToolBox.name()));
        filters.add(new SearchFilter("engineerNo", SearchFilter.Operator.EQ, engineerNo));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationReject.name()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbIntegralOrder.IntegralOrderStatusEnum.OrderedReject.name()));
        List<ZkbIntegralOrder> zkbIntegralOrders = zkbIntegralOrderRepository.findBySearchFilter(filters);
        return zkbIntegralOrders;
    }

    public List<ZkbIntegralOrder> queryIntegralOrderByTenantThreeYear(UserAccount userAccount){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbIntegralOrder.OrderTypeEnum.ToolBox.name()));
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,userAccount.getTenantUID()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbIntegralOrder.IntegralOrderStatusEnum.EngineerQualificationReject.name()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbIntegralOrder.IntegralOrderStatusEnum.OrderedReject.name()));
        Date dt = DateUtils.addYears(new Date(), -3);
        filters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, dt));

        List<ZkbIntegralOrder> zkbIntegralOrders = zkbIntegralOrderRepository.findBySearchFilter(filters);
        return zkbIntegralOrders;
    }

    public List<ZkbContract> queryContractByAsset(String tenUId, String assetId, Date requestTime){
        List<String> strings = zkbContractAssetRepository.findDistinctContractId(assetId);
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("id", SearchFilter.Operator.IN,strings));
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenUId));
        filters.add(new SearchFilter("startDate", SearchFilter.Operator.LTE, new Date()));
        filters.add(new SearchFilter("endDate", SearchFilter.Operator.GT, DateUtils.addDays(new Date(), -1)));
        List<ZkbContract> zkbContracts = zkbContractRepository.findBySearchFilter(filters);
        return zkbContracts;
    }

    public void workFlowSelectedHandle(ZkbIntegralOrder selected, HashMap<String, Object> selectedWorkFlow, String orderType, AssetInfo assetInfo,ZkbContract zkbContract, UserAccount userAccount) {
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
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(), assetInfo.getUid());
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
        Optional<ZkbPartOrderUserConfig> zkbPartOrderUserConfig = zkbPartOrderUserConfigs.stream().findFirst();
        if(zkbPartOrderUserConfig.isPresent()){
            selected.setRegionalManagerEmail(zkbPartOrderUserConfig.get().getEmail());
            selected.setRegionalManagerPhone(zkbPartOrderUserConfig.get().getPhone());
            selected.setRegionalManagerName(zkbPartOrderUserConfig.get().getName());
        }
    }

    public Integer queryScore(UserAccount userAccount){
        return zkbIntegralService.queryScoreFlag(userAccount.getTenantUID());
    }

    public String checkScoreFlagValid(ZkbContract zkbContract,String orderType,UserAccount userAccount){
        StringBuilder sb = new StringBuilder();
        List<SearchFilter> filters = new ArrayList<>();
        List<String> orderTypes = Arrays.asList("Parts","Probe","Tool");
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.IN, orderTypes));
        filters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ, zkbContract.getId()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Returned.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.PartReturned.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Closed.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.TechnicalReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.ManageReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.DirectorReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.CommercialReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.CloseApproved.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.CloseFailed.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.CloseCommited.toString()));

        int a = 0;
        int b = 0;
        b += zkbContract.getProbeQuotaTotal() == null ? 0 : zkbContract.getProbeQuotaTotal();
        b += zkbContract.getProbeQuotaNormalTotal() == null ? 0 : zkbContract.getProbeQuotaNormalTotal();
        b += zkbContract.getProbeQuotaSpecialTotal() == null ? 0 : zkbContract.getProbeQuotaSpecialTotal();
        b += zkbContract.getSpareQuotaTotal() == null ? 0 :  zkbContract.getSpareQuotaTotal();
        b += zkbContract.getSpareArtificailTotal() == null ? 0 :  zkbContract.getSpareArtificailTotal();
        b += zkbContract.getPartHighQuotaTotal() == null ? 0 :  zkbContract.getPartHighQuotaTotal();

        List<ZkbPartOrder> zkbPartOrders = zkbPartOrderRepository.findBySearchFilter(filters);
        int qty = 0;
        for(ZkbPartOrder o : zkbPartOrders){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(o.getOrderType())){
                if(o.getZkbPartOrderCat() != null && ZkbPartOrderCat.ValueType.HighValue.toString().equals(o.getZkbPartOrderCat().getValueType())){
                    continue;
                }
                if("t".equals(o.getHighCatFlag())){
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(o.getContractCatId());
                    if(zkbContractCat != null) {
                        if (!ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())) {
                            continue;
                        }
                    }
                }
                if("High".equals(o.getProbeQuotaType())){
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(o.getContractCatId());
                    if(zkbContractCat != null) {
                        if (!ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())) {
                            continue;
                        }
                    }
                }
                qty += o.getQty();
            }if(ZkbPartOrder.OrderType.Parts.toString().equals(o.getOrderType())){
                qty += o.getQty();
            }else if(ZkbPartOrder.OrderType.Tool.toString().equals(o.getOrderType())){
                qty += o.getQty();
            }
        }
        List<SearchFilter> partFilters = new ArrayList<>();
        partFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbPartOrder.OrderType.Parts.toString()));
        partFilters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ, zkbContract.getId()));
        partFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.PartReturned.toString(),"1*2+"));
        partFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.Returned.toString(),"1*2+"));
        List<ZkbPartOrder> partReturnZkbPartOrders =  zkbPartOrderRepository.findBySearchFilter(partFilters);
        List<String> partReturnIds = new ArrayList<>();
        int partQty = 0;
        for(ZkbPartOrder o : partReturnZkbPartOrders){
            partReturnIds.add(o.getId());
            partQty += o.getQty();
        }
        List<SearchFilter> returnFilters = new ArrayList<>();
        returnFilters.add(new SearchFilter("partOrderId", SearchFilter.Operator.IN, partReturnIds));
        returnFilters.add(new SearchFilter("dataStatus", SearchFilter.Operator.EQ, ZkbPartOrderReturn.DataStatusEnum.Accept.toString()));
        List<ZkbPartOrderReturn> zkbPartOrderReturns = zkbPartOrderReturnRepository.findBySearchFilter(returnFilters);
        int partReturnQty  = 0;
        for(ZkbPartOrderReturn r : zkbPartOrderReturns){
            partReturnQty += r.getQty();
        }
        a += qty + (partQty - partReturnQty);

        boolean flag = true;
        double d = b * 0.3 ;
        if((int)d != d){
            d= (int) d;
            d++;
        }
        if(a > d){
            return sb.append("该租户申请未归还数量超过30%，请及时归还！").toString();
        }

        if(flag){
            Date nowDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fDate = null;
            Date suxDate = null;
            try {
                fDate = simpleDateFormat.parse(simpleDateFormat.format(nowDate));
                suxDate = DateUtils.addMonths(fDate, -3);
                suxDate = DateUtils.addDays(suxDate, 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(suxDate != null){
                filters = new ArrayList<>();
                filters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbPartOrder.OrderType.Parts.toString()));
                filters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ, zkbContract.getId()));
                filters.add(new SearchFilter("orderedDate", SearchFilter.Operator.LT, suxDate));
                filters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.Ordered.toString()));
                List<ZkbPartOrder> threeMonthList = zkbPartOrderRepository.findBySearchFilter(filters);
                if(threeMonthList != null && threeMonthList.size() > 0){
                    return sb.append("该租户存在超过3个月未归还数据，请及时归还！").toString();
                }
            }
        }
        return "";
    }

    public void tempSave(ZkbIntegralOrder zkbIntegralOrder, UserAccount userAccount, ZkbContract zkbContract, List<V2_BlobObject> v2_blobObjectList){
        if(StrUtil.isEmpty(zkbIntegralOrder.getId())){
            tempSaveForCreate(zkbIntegralOrder, userAccount, zkbContract, v2_blobObjectList);
        }else{
            zkbIntegralOrderRepository.save(zkbIntegralOrder);
            if(CollectionUtils.isNotEmpty(v2_blobObjectList)){
                for(V2_BlobObject v2BlobObject : v2_blobObjectList){
                    if(StrUtil.isEmpty(v2BlobObject.getBoUuid())){
                        v2BlobObject.setBoUuid(zkbIntegralOrder.getId());
                        blobObjectRepository.save(v2BlobObject);
                    }
                }
            }
        }
    }

    public void tempSaveForCreate(ZkbIntegralOrder zkbIntegralOrder, UserAccount userAccount, ZkbContract zkbContract, List<V2_BlobObject> v2_blobObjectList){
        if(userAccount.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
            zkbIntegralOrder.setDataType(ZkbPartOrder.DATA_TYPE_TEST);
        }
        zkbIntegralOrder.setDataFlag(0);
        zkbIntegralOrder.setTenantUID(userAccount.getTenantUID());
        zkbIntegralOrder.setCreatedBy(userAccount.getLoginName());
        zkbIntegralOrder.setCreatedBy(userAccount.getUid());
        zkbIntegralOrder.setCreatedDate(new Date());
        Object o= zkbIntegralOrderRepository.getMaxIndex("IO"+simpleDateFormat.format(new Date())+"%") ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String orderNo = "IO"+simpleDateFormat.format(new Date())+String.format("%04d",index);
        zkbIntegralOrder.setIntegralOrderNo(orderNo);
        zkbIntegralOrder.setOrderIndex(index);
        zkbIntegralOrderRepository.save(zkbIntegralOrder);
        if(CollectionUtils.isNotEmpty(v2_blobObjectList)){
            for(V2_BlobObject v2BlobObject : v2_blobObjectList){
                if(StrUtil.isEmpty(v2BlobObject.getBoUuid())){
                    v2BlobObject.setBoUuid(zkbIntegralOrder.getId());
                    blobObjectRepository.save(v2BlobObject);
                }
            }
        }
    }

    public boolean checkSave(ZkbIntegralOrder zkbIntegralOrder, List<V2_BlobObject> v2_blobObjectList, ZkbContract zkbContract, HashMap<String, Object> selectedWorkFlow){
        if(zkbIntegralOrder == null){
            WebUtil.addErrorMessage("zkbIntegralOrder 不能为空！");
            return false;
        }
        if(zkbContract == null){
            WebUtil.addErrorMessage("合同 不能为空！");
            return false;
        }
        boolean rs = true;
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            if(StrUtil.isEmpty(zkbIntegralOrder.getCatId())){
                WebUtil.addErrorMessage("工具箱号 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getContractId())){
                WebUtil.addErrorMessage("合同号 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getZkbEngineerName())){
                WebUtil.addErrorMessage("工程师姓名 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getZkbEngineerPhone())){
                WebUtil.addErrorMessage("工程师电话 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getEngineerNo())){
                WebUtil.addErrorMessage("工程师学员号 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getReceiver())){
                WebUtil.addErrorMessage("收货人 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getReceiverPhone())){
                WebUtil.addErrorMessage("收货人电话 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getReceiverAddress())){
                WebUtil.addErrorMessage("收货人地址 不能为空！");
                rs = false;
            }

            if(ZkbContract.ContractType.CCS.name().equals(zkbContract.getContractType()) ||
                    ZkbContract.ContractType.Biomed.name().equals(zkbContract.getContractType())){
               /* if(CollectionUtils.isEmpty(v2_blobObjectList)){
                    WebUtil.addErrorMessage("合同附件 不能为空！");
                    rs = false;
                }*/
            }
        }else {
            if(StrUtil.isEmpty(zkbIntegralOrder.getArtificialType())){
                WebUtil.addErrorMessage("机型 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getUrgentOrder())){
                WebUtil.addErrorMessage("是否为紧急订单 不能为空！");
                rs = false;
            }else{
                if(ZkbConstantUtil.T.equals(zkbIntegralOrder.getUrgentOrder())){
                    if(StrUtil.isEmpty(zkbIntegralOrder.getUrgencyReasons())){
                        WebUtil.addErrorMessage("订单紧急的原因 不能为空！");
                        rs = false;
                    }
                }
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getZkbEngineerName())){
                WebUtil.addErrorMessage("ZKB工程师姓名 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getZkbEngineerPhone())){
                WebUtil.addErrorMessage("ZKB工程师电话 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getProductNumber())){
                WebUtil.addErrorMessage("产品型号 不能为空！");
                rs = false;
            }
            if(StrUtil.isEmpty(zkbIntegralOrder.getFaultDescription())){
                WebUtil.addErrorMessage("故障描述 不能为空！");
                rs = false;
            }
            if(selectedWorkFlow.get("id") == null || StrUtil.isEmpty(selectedWorkFlow.get("id").toString())){
                if(StrUtil.isEmpty(zkbIntegralOrder.getUrgentOrder())){
                    WebUtil.addErrorMessage("工单 不能为空！");
                    rs = false;
                }
            }
            if(selectedWorkFlow.get("srNum") == null || StrUtil.isEmpty(selectedWorkFlow.get("srNum").toString())){
                if(StrUtil.isEmpty(zkbIntegralOrder.getUrgentOrder())){
                    WebUtil.addErrorMessage("工单srNum 不能为空！");
                    rs = false;
                }
            }
            if(selectedWorkFlow.get("assetId") == null || StrUtil.isEmpty(selectedWorkFlow.get("assetId").toString())){
                if(StrUtil.isEmpty(zkbIntegralOrder.getUrgentOrder())){
                    WebUtil.addErrorMessage("设备 不能为空！");
                    rs = false;
                }
            }
        }
        return rs;
    }

    public boolean checkScore(UserAccount currentUser,ZkbIntegralOrder zkbIntegralOrder){
        boolean checkFlag = true;
        Integer score = queryScore(currentUser);
        Integer deductScore = 0;
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            deductScore = 900;
        }else {
            deductScore = 200;
        }
        if(score < deductScore){
            WebUtil.addErrorMessage("当前申请需要扣除" +deductScore+ "，积分不够！", "");
            checkFlag = false;
        }
        return checkFlag;
    }

    public boolean checkLogic(ZkbIntegralOrder zkbIntegralOrder, UserAccount currentUser, ZkbContract zkbContract){
        boolean checkFlag = true;
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            List<ZkbIntegralOrder> zkbIntegralOrders = queryIntegralOrderByCreatedBy(currentUser, zkbIntegralOrder.getEngineerNo());
            if(CollectionUtils.isNotEmpty(zkbIntegralOrders) && zkbIntegralOrders.size() >=1){
                WebUtil.addErrorMessage("超过可申请数量，申请失败！");
                checkFlag = false;
            }

            zkbIntegralOrders = queryIntegralOrderByTenantThreeYear(currentUser);
            if(zkbIntegralOrders.size() >= 2){
                WebUtil.addErrorMessage("超过可申请数量，申请失败！");
                checkFlag = false;
            }
        }
        String rs = checkScoreFlagValid(zkbContract, zkbIntegralOrder.getOrderType(), currentUser);
        if(StrUtil.isNotEmpty(rs)){
            WebUtil.addErrorMessage("积分申请不符合条件:" + rs);
            checkFlag = false;
        }
        return checkFlag;
    }

    public void preSave(ZkbIntegralOrder zkbIntegralOrder, ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo){
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            zkbIntegralOrderStatusVo.setQuotaTips("此次申请扣除900积分！");
        }else {
            zkbIntegralOrderStatusVo.setQuotaTips("此次申请扣除200积分！");
        }
    }

    public void create(ZkbIntegralOrder zkbIntegralOrder, UserAccount userAccount, ZkbContract zkbContract, List<V2_BlobObject> v2_blobObjectList){
        zkbIntegralOrder.setStatus(ZkbIntegralOrder.IntegralOrderStatusEnum.Requested.name());
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.name());
        }else{
            if("2".equals(zkbIntegralOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                }else{
                    zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                }
            }else{
                zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.name());
            }
        }
        tempSave(zkbIntegralOrder, userAccount, zkbContract, v2_blobObjectList);

        ZkbPartOrderStep zkbPartOrderStep = new ZkbPartOrderStep();
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            zkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.name());
            zkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbIntegralOrderSteps",ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.toString()));
        }else{
            if("2".equals(zkbIntegralOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                }else{
                    if("GT".equals(ZkbConstantUtil.compareDateByYyyyMMdd(zkbIntegralOrder.getRequestDate(),zkbContract.getEndDate()))){
                        zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                    }else{
                        zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                    }
                }
            }
            zkbIntegralOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.name());
        }
        zkbPartOrderStep.setStartTime(new Date());
        zkbPartOrderStep.setOwnerId(userAccount.getUid());
        zkbPartOrderStep.setOwnerName(userAccount.getName());
        zkbPartOrderStep.setTenantUID(zkbIntegralOrder.getTenantUID());
        zkbPartOrderStep.setPartOrderId(zkbIntegralOrder.getId());
        zkbPartOrderStepRepository.save(zkbPartOrderStep);
        applyIntegralHandle(zkbIntegralOrder, userAccount, ZkbIntegralRecord.RecordType.QuotaDeduct.name());

        List<String> strs = Arrays.asList(ZkbPartOrderStep.PartOrderStep.ManagePending.name(),ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.name(),ZkbPartOrderStep.PartOrderStep.CommercialPending.name());
        if(strs.contains(zkbIntegralOrder.getCurrentStep())){
            sendMessage(zkbIntegralOrder, UserContextService.getAccessToken(), WebUtil.getFieldValueMessage("zkbIntegralOrderType",zkbIntegralOrder.getOrderType()) + "申请");
            sendEmail(zkbIntegralOrder, UserContextService.getAccessToken(), getGatewayURL(GatewayURL.INTEGRAL_ORDER_SEND_EMAIL), WebUtil.getFieldValueMessage("zkbIntegralOrderType", zkbIntegralOrder.getOrderType()));
        }
    }

    /**
     * 申请积分扣减以及审核不通过返回积分
     */
    public void applyIntegralHandle(ZkbIntegralOrder zkbIntegralOrder, UserAccount userAccount, String handleType){
        int score = 0;
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(zkbIntegralOrder.getOrderType())){
            score = 900;
        }else {
            score = 200;
        }
        zkbIntegralService.deductScore(zkbIntegralOrder.getTenantUID(),userAccount, handleType, score);
    }

    public List<ZkbPartOrderCat> queryPartOrderCatByContract(UserAccount userAccount){
        List<ZkbPartOrderCat> list = new ArrayList<>();
        String date = DateUtil.format(new Date(), "yyyy-MM-dd");
        Date d = DateUtil.parse(date, "yyyy-MM-dd");
        List<ZkbContract> zkbContracts = zkbContractRepository.queryByTenantAndDate(userAccount.getTenantUID(), new Date(d.getTime()));
        if(CollectionUtils.isNotEmpty(zkbContracts)){
            List<String> contractTypes = zkbContracts.stream().map(t -> contractTypeToCatType(t.getContractType())).distinct().collect(Collectors.toList());
            if(contractTypes.size() == 1){
                String ty = contractTypes.get(0);
                if("all".equals(ty)){
                    list = zkbPartOrderCatRepository.findByBusinessType(ZkbPartOrderCat.BusinessTypeEnum.ToolBox.name());
                }else{
                    list = zkbPartOrderCatRepository.findByBusinessTypeAndCatType(ZkbPartOrderCat.BusinessTypeEnum.ToolBox.name(), ty);
                }
            }else {
                list = zkbPartOrderCatRepository.findByBusinessType(ZkbPartOrderCat.BusinessTypeEnum.ToolBox.name());
            }
        }
        return list;
    }

    public String contractTypeToCatType(String contractType){
        if(ZkbContract.ContractType.DI.name().equals(contractType) || ZkbContract.ContractType.ULS_OEC.name().equals(contractType)){
            return "Radiation";
        }else if(ZkbContract.ContractType.CCS.name().equals(contractType) || ZkbContract.ContractType.CCSB.name().equals(contractType) || ZkbContract.ContractType.Biomed.name().equals(contractType)){
            return "Ultrasonic";
        }else{
            return "all";
        }
    }

    @Async
    public void sendMessage(ZkbIntegralOrder zkbIntegralOrder,String token, String subject) {
        HashMap<String, Object> emailWrapperMap = null;
        try {
            emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbIntegralOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbIntegralOrder.getId());
            emailWrapperMap.put("url", "/zkbnewapply?ordernum=" + zkbIntegralOrder.getId());
            //emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbIntegralOrderType",zkbIntegralOrder.getOrderType()) + "申请");
            emailWrapperMap.put("subject", subject);
            String url = getGatewayURL(GatewayURL.INTEGRAL_ORDER_SEND_MESSAGE);
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbIntegralOrder.getIntegralOrderNo()+" sendMessage "+response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendEmail(ZkbIntegralOrder zkbIntegralOrder,String token, String url,String subject){
        try {
            HashMap<String, Object> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbIntegralOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbIntegralOrder.getId());
            emailWrapperMap.put("url", "/zkbnewapply?ordernum=" + zkbIntegralOrder.getId());
            //emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbIntegralOrderType", zkbIntegralOrder.getOrderType()));
            emailWrapperMap.put("subject", subject);

            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbIntegralOrder.getIntegralOrderNo()+" sendEmail " +response.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
