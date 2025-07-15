package com.ge.apm.service.zkb;

import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbPartOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@Service
public class ZkbPartOrderService  extends MicroServiceInvoker {

    Logger logger = LoggerFactory.getLogger(ZkbPartOrderService.class);

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Getter
    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    @Getter@Setter
    @Autowired
    private ZkbPartOrderItemRepository zkbPartOrderItemRepository;

    @Autowired
    private ZkbPartOrderStepRepository zkbPartOrderStepRepository;

    @Autowired
    private ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    @Getter
    @Autowired
    ZkbContractRepository zkbContractRepository;

    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Getter
    @Autowired
    private BlobObjectRepository blobObjectRepository;

    @Getter
    @Autowired
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;

    @Getter
    @Autowired
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Getter
    @Autowired
    private BlobFileService blobService;

    @Autowired
    private ZkbPartOrderReturnHistoryRepository zkbPartOrderReturnHistoryRepository;

    @Autowired
    private ZkbContractLogRepository zkbContractLogRepository;

    @Autowired
    private ZkbPartOrderQuotaService zkbPartOrderQuotaService;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;

    @Getter
    @Autowired
    private ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    SvcEngineLogisticsRepository svcEngineLogisticsRepository;

    @Autowired
    SvcEngineLogisticsStatusRepository svcEngineLogisticsStatusRepository;

    @Autowired
    ZkbPartOrderModifyLogRepository zkbPartOrderModifyLogRepository;

    @Autowired
    ZkbUnrealRepository zkbUnrealRepository;

    @Autowired
    private ZkbContractQuotaLogService zkbContractQuotaLogService;
    @Autowired
    ZkbPartOrderCloseInfoRepository zkbPartOrderCloseInfoRepository;

    @Autowired
    ZkbPartOrderCloseWorkTimeRepository zkbPartOrderCloseWorkTimeRepository;

    @Autowired
    ZkbContractCatQuotaHistoryService zkbContractCatQuotaHistoryService;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    public List<TenantInfo> getSourceTenantInfoList(){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderItemRepository.deleteByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.deleteByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderRepository.delete(zkbPartOrder);
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        if(zkbContract != null){
            //subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount,false);
        }

        zkbPartOrder.setZkbPartOrderItems(zkbPartOrderItems);
        zkbPartOrder.setAssetInfo(null);
        ZkbContractLog zkbContractLog = new ZkbContractLog();
        zkbContractLog.setContractId(zkbPartOrder.getId());
        zkbContractLog.setContractNo(zkbPartOrder.getPartOrderNo());
        zkbContractLog.setLogType(ZkbContractLog.LogType.zkbPartOrderDelete.toString());
        zkbContractLog.setJsonData(JSON.toJSONString(zkbPartOrder));
        zkbContractLog.setLoginUserName(userAccount.getName());
        zkbContractLog.setCreatedDate(new Date());
        zkbContractLogRepository.save(zkbContractLog);
    }

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

    public List<HashMap<String, String>> queryHighCatByContract(String contractId,boolean isAll){
        List<ZkbContractCat> zkbContractCats = new ArrayList<>();
        if(isAll){
            zkbContractCats = zkbContractCatRepository.queryAllByContractId(contractId);
        }else {
            zkbContractCats = zkbContractCatRepository.queryByContractId(contractId);
        }
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap m = new HashMap();
        for(ZkbContractCat c : zkbContractCats){
            HashMap<String, String> r = new HashMap<>();
            r.put("msgKey",c.getId());
            r.put("value",c.getSnNo());
            result.add(r);
        }
        return result;
    }

    public List<ZkbContract> queryContractByAsset(String tenUId,String assetId,Date requestTime){
        List<String> strings = zkbContractAssetRepository.findDistinctContractId(assetId);
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("id", SearchFilter.Operator.IN,strings));
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenUId));
        filters.add(new SearchFilter("startDate", SearchFilter.Operator.LTE, requestTime));
        filters.add(new SearchFilter("endDate", SearchFilter.Operator.GT, DateUtils.addDays(requestTime, -1)));
        List<ZkbContract> zkbContracts = zkbContractRepository.findBySearchFilter(filters);
        return zkbContracts;
    }

    public Boolean checkThreeValid(ZkbPartOrder zkbPartOrder){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("srNum", SearchFilter.Operator.EQ,zkbPartOrder.getSrNum()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.TechnicalReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.ManageReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.DirectorReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.CommercialReject.toString()));

        filters.add(new SearchFilter("requestDate", SearchFilter.Operator.LT,zkbPartOrder.getRequestDate()));
        List<ZkbPartOrder> historyList = zkbPartOrderRepository.findBySearchFilter(filters);
        Integer historyQty = 0;
        for(ZkbPartOrder o : historyList){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                historyQty++;
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                historyQty += o.getQty();
            }
        }
        if(historyQty >=3 ){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public String checkContractWeekValid(ZkbPartOrder zkbPartOrder,ZkbContract zkbContract){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ,zkbPartOrder.getContractId()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.TechnicalReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.ManageReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.DirectorReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.CommercialReject.toString()));
        filters.add(new SearchFilter("requestDate", SearchFilter.Operator.GTE,DateUtils.addDays(zkbContract.getEndDate(),-6)));
        filters.add(new SearchFilter("requestDate", SearchFilter.Operator.LT,DateUtils.addDays(zkbContract.getEndDate(),1)));
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.NE,ZkbPartOrder.OrderType.Tool.toString()));
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.NE,ZkbPartOrder.OrderType.Artificial.toString()));
        List<ZkbPartOrder> historyList = zkbPartOrderRepository.findBySearchFilter(filters);
        Integer historyQty = 0;
        for(ZkbPartOrder o : historyList){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                historyQty++;
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                historyQty += o.getQty();
            }
        }

        Integer days = ZkbConstantUtil.getBetweenDay(zkbPartOrder.getRequestDate(),zkbContract.getEndDate()) + 1;
        if(days > 7 || days < 1){
            return "";
        }else{
            return "合同还有【"+String.valueOf(days)+"】天到期，合同临结束7天内已订购【"+String.valueOf(historyQty)+"】个备件/探头！";
        }
    }

    public boolean checkScoreFlagValid(ZkbPartOrder zkbPartOrder,ZkbContract zkbContract){
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
        partFilters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ,zkbContract.getId()));
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
            flag = false;
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
                    flag = false;
                }
            }
        }
        return flag;
    }

    public String normalCheck(ZkbPartOrder selected){
        StringBuilder errorLog = new StringBuilder();
        if(ZkbPartOrder.OrderType.Probe.toString().equals(selected.getOrderType())){
            if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())){
                if(StrUtil.isEmpty(selected.getSrNo())){
                    errorLog.append("服务序号SR#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getActivityNo())){
                    errorLog.append("服务编号Activity#不能为空，\r\n");
                }
            }else if(selected.getCurrentStep().equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                if(StrUtil.isEmpty(selected.getOrderNo())){
                    errorLog.append("Order#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getNotGeAsset()) && StrUtil.isEmpty(selected.getPrice())){
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
                    errorLog.append("服务序号SR#不能为空，\r\n");
                }
                if(StrUtil.isEmpty(selected.getActivityNo())){
                    errorLog.append("服务编号Activity#不能为空，\r\n");
                }
            }
        }
        return errorLog.toString();
    }

    /**
     * 按照申请单中的申请数量与名额总数进行比较，不超过30%；如果总是小于10，则申请数量不能超过3个
     * @param zkbPartOrder
     * @param zkbContract
     * @return
     */
    public boolean checkApplyNumValid(ZkbPartOrder zkbPartOrder,ZkbContract zkbContract){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ,zkbPartOrder.getOrderType()));
        filters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ,zkbContract.getId()));
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Returned.toString()));
            filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.PartReturned.toString()));
        }else{
            filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Returned.toString()));
        }
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Closed.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.CloseApproved.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.CloseFailed.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.CloseCommited.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.TechnicalReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.ManageReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.DirectorReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
        int a = 0;
        int b = 0;
        if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            List<ZkbPartOrder> zkbPartOrders = zkbPartOrderRepository.findBySearchFilter(filters);
            int qty = 0;
            for(ZkbPartOrder o : zkbPartOrders){
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
                if("High".equals(zkbPartOrder.getProbeQuotaType())){
                    ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(o.getContractCatId());
                    if(zkbContractCat != null) {
                        if (!ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())) {
                            continue;
                        }
                    }
                }
                //qty++;
                qty += o.getQty();
            }
            a = qty ;
            b = zkbContract.getProbeQuotaTotal() + (zkbContract.getProbeQuotaNormalTotal() == null ? 0 : zkbContract.getProbeQuotaNormalTotal())+(zkbContract.getProbeQuotaSpecialTotal() == null ? 0 : zkbContract.getProbeQuotaSpecialTotal());
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){

            List<ZkbPartOrder> zkbPartOrders = zkbPartOrderRepository.findBySearchFilter(filters);
            Integer unReturnQty = 0;
            for(ZkbPartOrder o : zkbPartOrders){
                unReturnQty += o.getQty();
            }

            List<SearchFilter> partFilters = new ArrayList<>();
            partFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ,zkbPartOrder.getOrderType()));
            partFilters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ,zkbContract.getId()));
            partFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.PartReturned.toString(),"1*2+"));
            partFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbPartOrder.PartOrderStatus.Returned.toString(),"1*2+"));
            List<ZkbPartOrder> partReturnZkbPartOrders =  zkbPartOrderRepository.findBySearchFilter(partFilters);
            List<String> partReturnIds = new ArrayList<>();
            Integer partQty = 0;
            for(ZkbPartOrder o : partReturnZkbPartOrders){
                partReturnIds.add(o.getId());
                partQty += o.getQty();
            }
            List<SearchFilter> returnFilters = new ArrayList<>();
            returnFilters.add(new SearchFilter("partOrderId", SearchFilter.Operator.IN, partReturnIds));
            returnFilters.add(new SearchFilter("dataStatus", SearchFilter.Operator.EQ, ZkbPartOrderReturn.DataStatusEnum.Accept.toString()));
            List<ZkbPartOrderReturn> zkbPartOrderReturns = zkbPartOrderReturnRepository.findBySearchFilter(returnFilters);
            Integer partReturnQty  = 0;
            for(ZkbPartOrderReturn r : zkbPartOrderReturns){
                partReturnQty += r.getQty();
            }
            unReturnQty += (partQty - partReturnQty);
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                a = unReturnQty;
                b = zkbContract.getSpareQuotaTotal();
            }else if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                a = unReturnQty;
                b = (zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal()) + (zkbContract.getPartHighQuotaTotal() == null ? 0 : zkbContract.getPartHighQuotaTotal());
            }else {
                a = unReturnQty;
                b = zkbContract.getSpareArtificailTotal();
            }
        }
        if(b <= 0){
            return true;
        }
        if(b < 10){
            if(a > 3){
                return false;
            }
        }else{
            double d = b * 0.3 ;
            if((int)d != d){
                d= (int) d;
                d++;
            }
            if(a > d){
                return false;
            }
        }
        return true;
    }

    public boolean checkContractLastDay(ZkbContract zkbContract,String orderType){
        if(ZkbConstantUtil.getBetweenDay(zkbContract.getEndDate(),new Date()) == 0){
            return true;
        }
        return false;
    }

    /**
     * 申请保存方法：
 *      根据不同的申请类型，步骤、状态也不相同
     */
    @Transactional(rollbackFor = Exception.class)
    public ZkbPartOrder create(String workOrderId, String assetId,ZkbContract zkbContract,ZkbPartOrder zkbPartOrder,UserAccount userAccount,String tenantUid,String srNum){
        zkbPartOrder.setWorkOrderId(workOrderId);
        zkbPartOrder.setAssetId(assetId);
        zkbPartOrder.setContractId(zkbContract.getId());
        zkbPartOrder.setContractNo(zkbContract.getContractNo());
        zkbPartOrder.setTenantUID(tenantUid);
        zkbPartOrder.setUploadLetterFlag(null);
        zkbPartOrder.setNeedLetterFlag(null);
        if(StringUtils.isNotEmpty(zkbPartOrder.getTenantNewCatFlag())){
            zkbPartOrder.setNewCatFlag(zkbPartOrder.getTenantNewCatFlag());
        }
        if(zkbPartOrder.getOnlineEngine() != null){
            zkbPartOrder.setOnlineEngineer(zkbPartOrder.getOnlineEngine().getId());
        }
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
            if(StrUtil.isNotEmpty(zkbPartOrder.getNotGeAsset()) && ZkbConstantUtil.T.equals(zkbPartOrder.getNotGeAsset())){
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.TsePending.toString());
            }else{
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
            if(StrUtil.isNotEmpty(zkbPartOrder.getNotGeAsset()) && ZkbConstantUtil.T.equals(zkbPartOrder.getNotGeAsset())){
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.TsePending.toString());
            }else{
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
            }
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            //如果为超声高端则不需要进行区域经理审批
            if("2".equals(zkbPartOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                }else{
                    if("GT".equals(ZkbConstantUtil.compareDateByYyyyMMdd(zkbPartOrder.getRequestDate(),zkbContract.getEndDate()))){
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                    }else{
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                    }
                }
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
            }else if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                Integer quota = 0;
                if(ZkbPartOrder.CcsbQuotaTypeEnum.Repair.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    quota = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
                }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(zkbPartOrder.getCcsbApplyType())){
                    quota = zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota();
                }
                if(quota >= 1){
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                }else{
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ProductPending.toString());
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
            }
            zkbPartOrder.setScoreFlag(null);
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
            zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
        }
        zkbPartOrder.setCreatedBy(userAccount.getLoginName());
        zkbPartOrder.setCreatedDate(new Date());
        zkbPartOrder.setSrNum(srNum);
         Object o= zkbPartOrderRepository.getMaxIndex("PO"+simpleDateFormat.format(new Date())+"%") ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String partOrderNo = "PO"+simpleDateFormat.format(new Date())+String.format("%04d",index);
        zkbPartOrder.setPartOrderNo(partOrderNo);
        zkbPartOrder.setPartOrderIndex(index);

        List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrder.getZkbPartOrderItems();

        zkbPartOrder = zkbPartOrderRepository.save(zkbPartOrder);

        for(ZkbPartOrderItem zkbPartOrderItem : zkbPartOrderItems){
            zkbPartOrderItem.setPartOrderId(zkbPartOrder.getId());
            zkbPartOrderItemRepository.save(zkbPartOrderItem);
        }

        ZkbPartOrderStep zkbPartOrderStep = new ZkbPartOrderStep();
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
            zkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString()));
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
            zkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString()));
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrderStep.setStepKey(zkbPartOrder.getCurrentStep());
            zkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",zkbPartOrder.getCurrentStep()));
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
            zkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString()));
        }
        zkbPartOrderStep.setStartTime(new Date());
        zkbPartOrderStep.setOwnerId(userAccount.getUid());
        zkbPartOrderStep.setOwnerName(userAccount.getName());
        zkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        zkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(zkbPartOrderStep);

        zkbPartOrderQuotaService.subApplyQty(zkbContract,zkbPartOrder,zkbPartOrder.getQty(),userAccount,true);
        return zkbPartOrder;
    }

    @Autowired
    ZkbPartOrderMapper zkbPartOrderMapper;

    public List<ZkbPoHistoryQueryVo> queryZkbHistoryByCondition(List<SearchFilter> filters,String tenantUID){
        return zkbPartOrderMapper.queryZkbHistory(filters,tenantUID);
    }

    public List<ZkbPoHistoryQueryVo> queryGeHistoryByCondition(List<SearchFilter> filters,String tenantUID){
        return zkbPartOrderMapper.queryGeHistory(filters,tenantUID);
    }

    /**
     * 保存附件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUploadFile(List<V2_BlobObject> v2_blobObjects,String uid){
        if(v2_blobObjects != null){
            for (V2_BlobObject v : v2_blobObjects){
                v.setBoUuid(uid);
                blobObjectRepository.save(v);
            }
        }
    }

    public HashMap<String,Object> queryEditInfo(String id){
        HashMap<String,Object> map = new HashMap<>();
        ZkbPartOrder zkbPartOrder = zkbPartOrderRepository.findById(id);
        if(zkbPartOrder != null){
            List<ZkbPartOrderItem> zkbPartOrderItems = new ArrayList<>();
            zkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(id);
            zkbPartOrder.setZkbPartOrderItems(zkbPartOrderItems);
        }
        if(zkbPartOrder != null && StringUtils.isNotEmpty(zkbPartOrder.getContractId())){
            ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
            map.put("zkbContract",zkbContract);
        }
        if(zkbPartOrder != null && StringUtils.isNotEmpty(zkbPartOrder.getWorkOrderId())){
            V2_WorkOrder v = v2_workOrderRepository.findById(zkbPartOrder.getWorkOrderId());
            map.put("V2_WorkOrder",v);
            if(v != null){
                HashMap<String, Object> m = new HashMap<>();
                m.put("id",v.getId());
                Optional.ofNullable(v.getServiceRequest()).ifPresent(t -> {
                    m.put("srNum",v.getServiceRequest().getSrNum());
                    m.put("customerName",v.getServiceRequest().getCustomerName());
                    m.put("customerTel",v.getServiceRequest().getCustomerTel());
                    m.put("faultDescription",v.getServiceRequest().getRequestReason());
                    m.put("requestTime",v.getServiceRequest().getRequestTime());
                });
                Optional.ofNullable(v.getAssetInfo()).ifPresent(t ->{
                    m.put("name",v.getAssetInfo().getName());
                    m.put("systemId",v.getAssetInfo().getSystemId());
                    m.put("assetId",v.getAssetInfo().getUid());
                    m.put("hospitalName", v.getAssetInfo().getClinicalDeptName());
                    m.put("oneQrCode",v.getAssetInfo().getQrCode());
                    m.put("makerShortName", v.getAssetInfo().getManufacture());
                    m.put("departNum", v.getAssetInfo().getDepartNum());
                });
                map.put("v2", m);
            }
        }

        if(zkbPartOrder != null){
            //List<V2_BlobObject> v2_blobObjectList = blobObjectRepository.fetchBlobObjectByUuidAndType(zkbPartOrder.getId(),V2_BlobObject.BO_TYPE_ZKB_PART_ORDER,V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();
            List<SearchFilter> sfs = new ArrayList();
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, zkbPartOrder.getId()));
            sfs.add(new SearchFilter("boType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_PART_ORDER));
            sfs.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_PART_ORDER));
            v2_blobObjectList = blobObjectRepository.findBySearchFilter(sfs);
            map.put("v2_blobObjectList",v2_blobObjectList);
        }
        if(zkbPartOrder != null && ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            List<V2_BlobObject> v2_probe_blobObjectList = new ArrayList<>();
            List<SearchFilter> sfs = new ArrayList();
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, zkbPartOrder.getId()));
            sfs.add(new SearchFilter("boType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_PART_ORDER));
            sfs.add(new SearchFilter("boSubType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_PROVE));
            v2_probe_blobObjectList = blobObjectRepository.findBySearchFilter(sfs);
            map.put("v2_probe_blobObjectList",v2_probe_blobObjectList);
        }
        map.put("zkbPartOrder",zkbPartOrder);
        return map;
    }

    public List<ZkbPartOrderReturn> queryReturnListByPartOrderId(String id,boolean isCloseAdmin){
        if(isCloseAdmin){
            List<ZkbPartOrderReturn> zkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderIdAndDataStatusNotNull(id);
            return zkbPartOrderReturnList;
        }else{
            List<ZkbPartOrderReturn> zkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderId(id);
            return zkbPartOrderReturnList;
        }
    }

    public boolean  checkPartConsumable(ZkbPartOrder zkbPartOrder){
        if(ZkbPartOrder.OrderType.Parts.name().equals(zkbPartOrder.getOrderType())){
            if(ZkbPartOrder.RadiationQuotaTypeEnum.Consumable.name().equals(zkbPartOrder.getRadiationQuotaType())){
                return false;
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditNext(ZkbPartOrder zkbPartOrder,boolean nextFlag,UserAccount userAccount, ZkbPartOrderCloseInfo zkbPartOrderCloseInfo){
        String oldStep = zkbPartOrder.getCurrentStep();
        ZkbContract c = zkbContractRepository.findById(zkbPartOrder.getContractId());
        if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setTechAuditDate(new Date());
            zkbPartOrder.setTechAuditerUid(userAccount.getUid());
            zkbPartOrder.setTechAuditer(userAccount.getName());
            if(nextFlag){
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString());
                boolean isNewPartChange = false;
                if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                    if(ZkbContract.ContractType.DI.toString().equals(c.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(c.getContractType()) || "PCB MVS".equals(c.getContractType())){
                        AssetInfo a = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                        if(a != null && a.getAssetGroup() != null && a.getAssetGroup().equals(1)){
                            isNewPartChange = true;
                        }
                    }
                }
                if(isNewPartChange){
                    if(checkPartSeventyPercent(c,zkbPartOrder) && checkPartConsumable(zkbPartOrder)){
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                    }else{
                        if(!checkPartConsumable(zkbPartOrder)){
                            zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                        }else{
                            boolean r = checkApplyNumValid(zkbPartOrder,c);
                            if(!r){
                                zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.F);
                            }else{
                                zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.T);
                            }
                            if(ZkbConstantUtil.F.equals(zkbPartOrder.getCheckApplyNumFlag())){
                                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                            }else if("GT".equals(ZkbConstantUtil.compareDateByYyyyMMdd(zkbPartOrder.getRequestDate(),c.getEndDate()))){
                                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                            }else{
                                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                            }
                        }
                    }
                }else{
                    if(checkPartConsumable(zkbPartOrder)){
                        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString());
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                    }else{
                        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString());
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                    }
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setManageAuditDate(new Date());
            zkbPartOrder.setManageAuditerUid(userAccount.getUid());
            zkbPartOrder.setManageAuditer(userAccount.getName());
            if(nextFlag){
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    boolean flag  =false;
                    if(zkbPartOrder.getZkbPartOrderCat() != null && !ZkbPartOrderCat.ValueType.HighValue.toString().equals(zkbPartOrder.getZkbPartOrderCat().getValueType())){
                        flag = true;
                    }else if(StringUtils.isNotEmpty(zkbPartOrder.getHighCatFlag()) && "t".equals(zkbPartOrder.getHighCatFlag())){
                        flag = true;
                    }
                    if(flag){
                        boolean r = checkApplyNumValid(zkbPartOrder,c);
                        if(!r){
                            zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.F);
                        }else{
                            zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.T);
                        }
                    }else{
                        zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.T);
                    }
                }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                    boolean r = checkApplyNumValid(zkbPartOrder,c);
                    if(!r){
                        zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.F);
                    }else{
                        zkbPartOrder.setCheckApplyNumFlag(ZkbConstantUtil.T);
                    }
                }

                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ManageAccept.toString());
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getNotGeAsset()) && ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                } else if(ZkbConstantUtil.F.equals(zkbPartOrder.getCheckApplyNumFlag())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                } else if("GT".equals(ZkbConstantUtil.compareDateByYyyyMMdd(zkbPartOrder.getRequestDate(),c.getEndDate()))){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                } else{
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                    }else{
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                    }
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ManageReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setDirectorAuditDate(new Date());
            zkbPartOrder.setDirectorAuditerUid(userAccount.getUid());
            zkbPartOrder.setDirectorAuditer(userAccount.getName());
            if(nextFlag){
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.DirectorAccept.toString());
                if("GT".equals(ZkbConstantUtil.compareDateByYyyyMMdd(zkbPartOrder.getRequestDate(),c.getEndDate()))){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                }else{
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                    }else{
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                    }
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.DirectorReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(zkbPartOrder.getCurrentStep())) {
            zkbPartOrder.setCommercialAuditDate(new Date());
            zkbPartOrder.setCommercialAuditerUid(userAccount.getUid());
            zkbPartOrder.setCommercialAuditer(userAccount.getName());
            if(nextFlag){
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CommercialAccept.toString());
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                }else{
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CommercialReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.ProductPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setProductAuditDate(new Date());
            zkbPartOrder.setProductAuditerUid(userAccount.getUid());
            zkbPartOrder.setProductAuditer(userAccount.getName());
            if(nextFlag){
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ProductAccept.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ProductReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setTseAuditDate(new Date());
            zkbPartOrder.setTseAuditerUid(userAccount.getUid());
            zkbPartOrder.setTseAuditer(userAccount.getName());
            if(nextFlag) {
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TseAccept.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TseReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setOsscAuditDate(new Date());
            zkbPartOrder.setOsscAuditerUid(userAccount.getUid());
            zkbPartOrder.setOsscAuditer(userAccount.getName());
            if(nextFlag) {
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.OsscAccept.toString());
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                }else{
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.OsscReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrderCloseInfo.setCompleteAuditDate(new Date());
            zkbPartOrderCloseInfo.setCompleteAuditer(userAccount.getName());
            zkbPartOrderCloseInfo.setCompleteAuditerUid(userAccount.getUid());
            zkbPartOrderCloseInfoRepository.save(zkbPartOrderCloseInfo);
            if(nextFlag) {
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CloseApproved.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CloseFailed.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
            }
        }

        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrderRepository.save(zkbPartOrder);
        List<ZkbPartOrderStep> zkbPartOrderStepList = zkbPartOrderStepRepository.findByPartOrderIdAndStepKey(zkbPartOrder.getId(), oldStep);
        for(ZkbPartOrderStep s : zkbPartOrderStepList){
            s.setEndTime(new Date());
            zkbPartOrderStepRepository.save(s);
        }
        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(zkbPartOrder.getCurrentStep());
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);

        if(!nextFlag && !(oldStep.equals(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString()))){
            ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
            zkbPartOrderQuotaService.subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount,false);
        }
    }

    /**
     * 根据srNo查询其他处于已归还审核但是为完成的单子，直接完成
     * @param zkbPartOrder
     */
    public void updateCompleteAuditBySrNum(ZkbPartOrder zkbPartOrder){
        List<ZkbPartOrder> zkbPartOrderList = zkbPartOrderRepository.queryBySrNoExceptStatus(zkbPartOrder.getSrNo(), zkbPartOrder.getId());
        if(zkbPartOrderList != null && zkbPartOrderList.size() > 0){
            for(ZkbPartOrder p : zkbPartOrderList){
                Integer dataFlag = p.getDataFlag() == null ? 0 : p.getDataFlag();
                dataFlag++;
                p.setDataFlag(dataFlag);
                p.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                p.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
            zkbPartOrderRepository.save(zkbPartOrderList);
        }
    }

    /**
     * 根据srNo查询出是否已经存在close的信息
     * @param srNo
     * @return
     */
    public boolean checkCloseInfoBySrNum(String srNo, ZkbPartOrderCloseInfo existingCloseInfo){
        ZkbPartOrderCloseInfo zkbPartOrderCloseInfo = zkbPartOrderCloseInfoRepository.findFirstBySrNo(srNo);
        if(zkbPartOrderCloseInfo != null){
            //排除本身
            if(existingCloseInfo != null){
                if(zkbPartOrderCloseInfo.getId().equals(existingCloseInfo.getId())){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 获取备件申请不真实信息
     * @param zkbPartOrder
     */
    public void getUnrealData(ZkbPartOrder zkbPartOrder){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for(ZkbPartOrderItem i : zkbPartOrder.getZkbPartOrderItems()){
            List<ZkbUnreal> zkbUnrealList = zkbUnrealRepository.findBySrNumAndItemNo(zkbPartOrder.getSrNum(),i.getPartNo());
            if(zkbUnrealList != null && zkbUnrealList.size() > 0){
                StringBuilder sb = new StringBuilder();
                for(ZkbUnreal u : zkbUnrealList){
                    sb.append(format.format(u.getCreatedDate()) + "判定为不真实；");
                }
                i.setUnrealStatus(sb.toString());
            }
        }
    }

    /**
     * 获取探头申请不真实信息
     * @param zkbPartOrder
     */
    public String getProbeUnrealData(ZkbPartOrder zkbPartOrder, String showCatListFlag){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<ZkbUnreal> zkbUnrealList = zkbUnrealRepository.findBySrNumAndItemNo(zkbPartOrder.getSrNum(),zkbPartOrder.getCatNo());
        StringBuilder sb = new StringBuilder();
        for(ZkbUnreal u : zkbUnrealList){
            sb.append(format.format(u.getCreatedDate()) + "判定为不真实；");
        }
        return sb.toString();
    }

    public List<ZkbUnrealVo> getUnrealCheckBoxList(List<ZkbPartOrderItem> zkbPartOrderItems,String partOrderId,ZkbPartOrder zkbPartOrder){
        List<ZkbUnrealVo> zkbUnrealVos = new ArrayList<>();

        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            for(ZkbPartOrderItem i : zkbPartOrderItems){
                List<ZkbUnreal> zkbUnrealList = zkbUnrealRepository.findByPartOrderIdAndItemNo(partOrderId, i.getPartNo());
                ZkbUnrealVo v = new ZkbUnrealVo();
                v.setItemNo(i.getPartNo());
                if(zkbUnrealList != null && zkbUnrealList.size() > 0){
                    ZkbUnreal u = zkbUnrealList.get(0);
                    v.setUnrealPartName(i.getPartNo()+ "(" + i.getPartName() + ")");
                    v.setCbStatus(ZkbConstantUtil.F);
                }else{
                    v.setUnrealPartName(i.getPartNo()+ "(" + i.getPartName() + ")");
                    v.setCbStatus(ZkbConstantUtil.T);
                }
                zkbUnrealVos.add(v);
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            ZkbUnrealVo u = new ZkbUnrealVo();
            u.setItemNo(zkbPartOrder.getCatNo());
            List<ZkbUnreal> zkbUnrealList = zkbUnrealRepository.findByPartOrderIdAndItemNo(partOrderId, zkbPartOrder.getCatNo());
            if(zkbUnrealList != null && zkbUnrealList.size() > 0){
                u.setUnrealPartName(zkbPartOrder.getCatNo());
                u.setCbStatus(ZkbConstantUtil.F);
            }else{
                u.setUnrealPartName(zkbPartOrder.getCatNo());
                u.setCbStatus(ZkbConstantUtil.T);
            }
            zkbUnrealVos.add(u);
        }
        return zkbUnrealVos;
    }

    @Transactional(rollbackFor = Exception.class)
    public void unrealNext(ZkbPartOrder zkbPartOrder,String[] selectedOptions,UserAccount userAccount,String reason){
        List<ZkbUnreal> zkbUnrealList = new ArrayList<>();
        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
        for (int i =0; i < selectedOptions.length;i++){
            //判断数据库是否存在如果存在进行更新操作，不存在则进行insert
            List<ZkbUnreal> existList = zkbUnrealRepository.findByPartOrderIdAndItemNo(zkbPartOrder.getId(), selectedOptions[i]);
            if(existList!= null && existList.size() > 0){
                ZkbUnreal zkbUnreal = existList.get(0);
                zkbUnreal.setCreatedDate(new Date());
                zkbUnreal.setCreatedBy(userAccount.getName());
                zkbUnreal.setReason(reason);
                zkbUnrealList.add(zkbUnreal);
            }else{
                ZkbUnreal zkbUnreal = new ZkbUnreal();
                zkbUnreal.setOrderType(zkbPartOrder.getOrderType());
                zkbUnreal.setPartOrderNo(zkbPartOrder.getPartOrderNo());
                zkbUnreal.setPartOrderId(zkbPartOrder.getId());
                zkbUnreal.setSrNum(zkbPartOrder.getSrNum());
                zkbUnreal.setSysId(assetInfo.getSystemId());
                zkbUnreal.setItemNo(selectedOptions[i]);
                zkbUnreal.setCreatedBy(userAccount.getName());
                zkbUnreal.setCreatedDate(new Date());
                zkbUnreal.setReason(reason);
                zkbUnrealList.add(zkbUnreal);
            }
        }
        zkbUnrealRepository.save(zkbUnrealList);
    }


    public boolean probeUnrealCheck(ZkbPartOrder zkbPartOrder, String showCatListFlag){
        List<ZkbUnreal> existList = zkbUnrealRepository.findByPartOrderIdAndItemNo(zkbPartOrder.getId(), zkbPartOrder.getCatNo());
        if(existList!= null && existList.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void closedBack(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.PartReturned.toString());
        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrderRepository.save(zkbPartOrder);

        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
        newZkbPartOrderStep.setStepName("退回归还");
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);
    }

    /**
     * 订货、归还、关单按钮操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void normalNext(ZkbPartOrder zkbPartOrder,UserAccount userAccount, ZkbPartOrderCloseInfo zkbPartOrderCloseInfo){
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        String oldStep = zkbPartOrder.getCurrentStep();
        Integer qty = zkbPartOrder.getQty();
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
            if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
                zkbPartOrder.setOrderedDate(new Date());
                zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
                zkbPartOrder.setOrderedAuditer(userAccount.getName());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
                zkbPartOrder.setOrderedDate(new Date());
                zkbPartOrder.setOrderedAuditer(userAccount.getName());
                zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
                if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
                    zkbPartOrder.setUploadLetterFlag(ZkbConstantUtil.F);
                    probeOrderNewCatChangeCalcQty(zkbPartOrder, zkbContract);
                }else {
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString());
                }
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())) {
                zkbPartOrder.setUnOrderDate(new Date());
                zkbPartOrder.setUnOrderAuditer(userAccount.getName());
                zkbPartOrder.setUnOrderAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.UnOrdered.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())) {
                zkbPartOrder.setReturnAuditDate(new Date());
                zkbPartOrder.setReturnAuditer(userAccount.getName());
                zkbPartOrder.setReturnAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Returned.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ClosePending.toString())) {
                zkbPartOrder.setClosedDate(new Date());
                zkbPartOrder.setClosedAuditer(userAccount.getName());
                zkbPartOrder.setClosedAuditerUid(userAccount.getUid());
                //如果srNo之前存在了完成信息，那么直接结束，否则进行完成信息的填写
                ZkbPartOrderCloseInfo ci = zkbPartOrderCloseInfoRepository.findFirstBySrNo(zkbPartOrder.getSrNo());
                if(ci == null || StringUtils.isEmpty(ci.getId())){
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
                }else{
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
                }
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString())) {
                zkbPartOrder.setReturnAuditDate(new Date());
                zkbPartOrder.setReturnAuditer(userAccount.getName());
                zkbPartOrder.setReturnAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.CompletePending.toString())) {
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CloseCommited.toString());
                if(StringUtils.isEmpty(zkbPartOrderCloseInfo.getPartOrderId())){
                    zkbPartOrderCloseInfo.setPartOrderId(zkbPartOrder.getId());
                    zkbPartOrderCloseInfo.setPartOrderNo(zkbPartOrder.getPartOrderNo());
                    zkbPartOrderCloseInfo.setSrNo(zkbPartOrder.getSrNo());
                }
                zkbPartOrderCloseInfo.setCompleteAuditDate(new Date());
                zkbPartOrderCloseInfo.setCompleteAuditer(userAccount.getName());
                zkbPartOrderCloseInfo.setCompleteAuditerUid(userAccount.getUid());
                zkbPartOrderCloseInfoRepository.save(zkbPartOrderCloseInfo);
            }
        }else{
            if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(zkbPartOrder.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.OsscAccept.toString().equals(zkbPartOrder.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.DirectorAccept.toString().equals(zkbPartOrder.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString().equals(zkbPartOrder.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.CommercialAccept.toString().equals(zkbPartOrder.getStatus())){
                zkbPartOrder.setOrderedDate(new Date());
                zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
                zkbPartOrder.setOrderedAuditer(userAccount.getName());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
            }else if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(zkbPartOrder.getStatus())){
                zkbPartOrder.setReturnAuditDate(new Date());
                zkbPartOrder.setReturnAuditer(userAccount.getName());
                zkbPartOrder.setReturnAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Returned.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            }else if(ZkbPartOrder.PartOrderStatus.Returned.toString().equals(zkbPartOrder.getStatus())){
                if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
                    //如果srNo之前存在了完成信息，那么直接结束，否则进行完成信息的填写
                    ZkbPartOrderCloseInfo ci = zkbPartOrderCloseInfoRepository.findFirstBySrNo(zkbPartOrder.getSrNo());
                    if(ci == null || StringUtils.isEmpty(ci.getId())){
                        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString());
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
                    }else{
                        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
                    }
                }else{
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
                }
                zkbPartOrder.setClosedDate(new Date());
                zkbPartOrder.setClosedAuditer(userAccount.getName());
                zkbPartOrder.setClosedAuditerUid(userAccount.getUid());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.CompletePending.toString())) {
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CloseCommited.toString());

                zkbPartOrderCloseInfo.setCompleteAuditDate(new Date());
                zkbPartOrderCloseInfo.setCompleteAuditer(userAccount.getName());
                zkbPartOrderCloseInfo.setCompleteAuditerUid(userAccount.getUid());
                if(StringUtils.isEmpty(zkbPartOrderCloseInfo.getPartOrderId())){
                    zkbPartOrderCloseInfo.setPartOrderId(zkbPartOrder.getId());
                    zkbPartOrderCloseInfo.setPartOrderNo(zkbPartOrder.getPartOrderNo());
                    zkbPartOrderCloseInfo.setSrNo(zkbPartOrder.getSrNo());
                }
                zkbPartOrderCloseInfoRepository.save(zkbPartOrderCloseInfo);
            }
        }
        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrder = zkbPartOrderRepository.save(zkbPartOrder);

        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(zkbPartOrder.getCurrentStep());
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);

        if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
            if (ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getWhetherDeduct())){
                    zkbPartOrderQuotaService.subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
                }else {
                    //不扣除名额，那么申请中-1，剩余+1
                    if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFlag()) && ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFirstFlag())){

                    }else{
                        zkbPartOrderQuotaService.subApplyQty(zkbContract,zkbPartOrder, -qty,userAccount,false);
                    }
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
                zkbPartOrderQuotaService.subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
            }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
                zkbPartOrderQuotaService.subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
                zkbPartOrderQuotaService.subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
            }
            if (ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
                zkbPartOrderItemRepository.save(zkbPartOrder.getZkbPartOrderItems());
            }
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ClosePending.toString())){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                if(StrUtil.isEmpty(zkbPartOrder.getNotGeAsset())){
                    if(!ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(zkbPartOrder.getReturnType()) && !ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(zkbPartOrder.getReturnType())) {
                        if(zkbPartOrder.getReturnQty() != 0){
                            zkbPartOrderQuotaService.subReturnQty(zkbContract,zkbPartOrder, zkbPartOrder.getReturnQty(),userAccount);
                        }
                    }
                }
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
                List<ZkbPartOrderReturn> zkbPartOrderReturns = zkbPartOrderReturnRepository.findByPartOrderId(zkbPartOrder.getId());
                if(zkbPartOrderReturns != null && zkbPartOrderReturns.size() > 0){
                    int v = 0;
                    for (ZkbPartOrderReturn r : zkbPartOrderReturns){
                        if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(r.getReturnType())){
                            continue;
                        }else if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(r.getReturnType())){
                            continue;
                        }else{
                            v += r.getQty();
                        }
                    }
                    if(v > 0){
                        zkbPartOrderQuotaService.subReturnQty(zkbContract,zkbPartOrder,v,userAccount);
                    }
                }
            }
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString())){
            if(ZkbConstantUtil.F.equals(zkbPartOrder.getTakeFlag())){
                zkbPartOrderQuotaService.subReturnQty(zkbContract,zkbPartOrder, zkbPartOrder.getQty(),userAccount);
            }
        }
    }

    public void saveBlob(List<V2_BlobObject> v2BlobObjectList, String uuid){
        for(V2_BlobObject v :v2BlobObjectList){
            v.setBoUuid(uuid);
        }
        blobObjectRepository.save(v2BlobObjectList);
    }

    public boolean probeOrderNewCatChangeCheck(ZkbPartOrder selected, ZkbContract zkbContract){
        boolean result = false;
        if(selected.getQty() == 0){
            return result;
        }
        if(selected.getNewCatFlag().equals(selected.getTenantNewCatFlag())){
            if(ZkbConstantUtil.T.equals(selected.getNewCatFlag())){
                if(selected.getProvideNewContractSecond().equals(selected.getProvideNewContract())){
                    //名额不发生变化
                }else if(ZkbConstantUtil.T.equals(selected.getProvideNewContractSecond()) && StrUtil.isEmpty(selected.getProvideNewContract())){
                    //名额不发生变化
                }else{
                    if(ZkbConstantUtil.T.equals(selected.getProvideNewContractSecond())){
                        //名额需要2 -->  1
                    }else if(ZkbConstantUtil.F.equals(selected.getProvideNewContractSecond())){
                        //名额需要1 -->  2
                        result = true;
                    }
                }
            }else{
                //名额不发生变化
            }
        }else {
            if(ZkbConstantUtil.T.equals(selected.getNewCatFlag())){
                if(ZkbConstantUtil.F.equals(selected.getProvideNewContractSecond())){
                    //名额需要1 -->  2
                    result = true;
                }else {
                    //名额不发生变化
                }
            }else{
                if(ZkbConstantUtil.F.equals(selected.getProvideNewContract())){
                    //名额需要2 -->  1
                }
            }
        }
        return result;
    }

    public void probeOrderNewCatChangeCalcQty(ZkbPartOrder zkbPartOrder, ZkbContract zkbContract){
        if(StrUtil.isEmpty(zkbContract.getQuotaDeductType()) || ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())){
            if(!zkbPartOrder.getNewCatFlag().equals(zkbPartOrder.getTenantNewCatFlag())){
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getNewCatFlag())){
                    zkbPartOrder.setQty(zkbPartOrder.getQty() * 2);
                }else {
                    zkbPartOrder.setQty(zkbPartOrder.getQty() / 2);
                }
            }
        }else if("New".equals(zkbContract.getQuotaDeductType())){
            if(zkbPartOrder.getNewCatFlag().equals(zkbPartOrder.getTenantNewCatFlag())){
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getNewCatFlag())){
                    if(zkbPartOrder.getProvideNewContractSecond().equals(zkbPartOrder.getProvideNewContract())){
                        //名额不发生变化
                    }else if(ZkbConstantUtil.T.equals(zkbPartOrder.getProvideNewContractSecond()) && StrUtil.isEmpty(zkbPartOrder.getProvideNewContract())){
                        //名额不发生变化
                    }else{
                        if(ZkbConstantUtil.T.equals(zkbPartOrder.getProvideNewContractSecond())){
                            //名额需要2 -->  1
                            zkbPartOrder.setQty(zkbPartOrder.getQty() / 2);
                        }else if(ZkbConstantUtil.F.equals(zkbPartOrder.getProvideNewContractSecond())){
                            //名额需要1 -->  2
                            zkbPartOrder.setQty(zkbPartOrder.getQty() * 2);
                        }
                    }
                }else{
                    //名额不发生变化
                }
            }else {
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getNewCatFlag())){
                    if(ZkbConstantUtil.F.equals(zkbPartOrder.getProvideNewContractSecond())){
                        //名额需要1 -->  2
                        zkbPartOrder.setQty(zkbPartOrder.getQty() * 2);
                    }else {
                        //名额不发生变化
                    }
                }else{
                    if(ZkbConstantUtil.F.equals(zkbPartOrder.getProvideNewContract())){
                        //名额需要2 -->  1
                        zkbPartOrder.setQty(zkbPartOrder.getQty() / 2);
                    }
                }
            }
        }
    }

    public void tempSaveComplete(ZkbPartOrderCloseInfo zkbPartOrderCloseInfo, ZkbPartOrder zkbPartOrder, List<ZkbPartOrderCloseWorkTime> zkbPartOrderCloseWorkTimeList, List<V2_BlobObject> v2BlobObjectList, UserAccount userAccount){
        if(StringUtils.isEmpty(zkbPartOrderCloseInfo.getPartOrderId())){
            zkbPartOrderCloseInfo.setPartOrderNo(zkbPartOrder.getPartOrderNo());
            zkbPartOrderCloseInfo.setPartOrderId(zkbPartOrder.getId());
            zkbPartOrderCloseInfo.setSrNo(zkbPartOrder.getSrNo());
        }
        zkbPartOrderCloseInfo = zkbPartOrderCloseInfoRepository.save(zkbPartOrderCloseInfo);
        for(ZkbPartOrderCloseWorkTime w : zkbPartOrderCloseWorkTimeList){
            if(StrUtil.isEmpty(w.getCloseId())){
                w.setSrNo(zkbPartOrder.getSrNo());
                w.setPartOrderId(zkbPartOrder.getId());
                w.setPartOrderNo(zkbPartOrder.getPartOrderNo());
                w.setCloseId(zkbPartOrderCloseInfo.getId());
            }
        }
        zkbPartOrderCloseWorkTimeRepository.save(zkbPartOrderCloseWorkTimeList);

        for(V2_BlobObject v :v2BlobObjectList){
            if(StrUtil.isEmpty(v.getBoUuid())){
                v.setBoUuid(zkbPartOrderCloseInfo.getId());
            }
        }
        blobObjectRepository.save(v2BlobObjectList);
        //將其他srNo的數據設置為已完成。
        updateCompleteAuditBySrNum(zkbPartOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void normalNextComplete(ZkbPartOrderCloseInfo zkbPartOrderCloseInfo, ZkbPartOrder zkbPartOrder, List<ZkbPartOrderCloseWorkTime> zkbPartOrderCloseWorkTimeList, List<V2_BlobObject> v2BlobObjectList, UserAccount userAccount){

        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString());
        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CloseCommited.toString());
        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrder = zkbPartOrderRepository.save(zkbPartOrder);

        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(zkbPartOrder.getCurrentStep());
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);


        if(StringUtils.isEmpty(zkbPartOrderCloseInfo.getPartOrderId())){
            zkbPartOrderCloseInfo.setPartOrderNo(zkbPartOrder.getPartOrderNo());
            zkbPartOrderCloseInfo.setPartOrderId(zkbPartOrder.getId());
            zkbPartOrderCloseInfo.setSrNo(zkbPartOrder.getSrNo());
        }
        //zkbPartOrderCloseInfo.setCompleteAuditRemark(null);
        zkbPartOrderCloseInfo = zkbPartOrderCloseInfoRepository.save(zkbPartOrderCloseInfo);
        for(ZkbPartOrderCloseWorkTime w : zkbPartOrderCloseWorkTimeList){
            w.setSrNo(zkbPartOrder.getSrNo());
            w.setPartOrderId(zkbPartOrder.getId());
            w.setPartOrderNo(zkbPartOrder.getPartOrderNo());
            w.setCloseId(zkbPartOrderCloseInfo.getId());
        }
        zkbPartOrderCloseWorkTimeRepository.save(zkbPartOrderCloseWorkTimeList);
        saveBlob(v2BlobObjectList, zkbPartOrderCloseInfo.getId());
        //將其他srNo的數據設置為已完成。
        updateCompleteAuditBySrNum(zkbPartOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchesReturnNext(ZkbPartOrder zkbPartOrder,List<ZkbPartOrderReturn> currentZkbPartOrderReturns,UserAccount userAccount){
        //第二步判断是否归还所有的配件，
        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
        List<ZkbPartOrderReturn> zkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderId(zkbPartOrder.getId());
        for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
            if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                if(ZkbPartOrderReturn.DataStatusEnum.Reject.toString().equals(r.getDataStatus())){
                    continue;
                }
            }
            if(returnMap.containsKey(r.getItemId())){
                Integer vv = returnMap.get(r.getItemId());
                vv += r.getQty();
                returnMap.put(r.getItemId(),vv);
            }else{
                returnMap.put(r.getItemId(),r.getQty());
            }
        }
        boolean flag = true;
        for(ZkbPartOrderItem zkbPartOrderItem : zkbPartOrder.getZkbPartOrderItems()){
            if(zkbPartOrderItem.getPartQty() == 0){
                continue;
            }
            Integer returnQty = returnMap.get(zkbPartOrderItem.getId());
            if(returnQty == null){
                flag = false;
            }else if(returnQty != zkbPartOrderItem.getPartQty()){
                flag = false;
            }
        }

        if(flag){
            //归还完毕
            zkbPartOrder.setReturnAuditDate(new Date());
            zkbPartOrder.setReturnAuditer(userAccount.getName());
            zkbPartOrder.setReturnAuditerUid(userAccount.getUid());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Returned.toString());
            zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
            dataFlag++;
            zkbPartOrder.setDataFlag(dataFlag);
            zkbPartOrderRepository.save(zkbPartOrder);

            ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
            newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
            newZkbPartOrderStep.setStartTime(new Date());
            newZkbPartOrderStep.setOwnerId(userAccount.getUid());
            newZkbPartOrderStep.setOwnerName(userAccount.getName());
            newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
            newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
            zkbPartOrderStepRepository.save(newZkbPartOrderStep);

            for(ZkbPartOrderReturn r : currentZkbPartOrderReturns){
                r.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.N.toString());
                r.setDataStatus(ZkbPartOrderReturn.DataStatusEnum.Pending.toString());
                zkbPartOrderReturnRepository.save(r);
            }
        }else {
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.PartReturned.toString());
            Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
            dataFlag++;
            zkbPartOrder.setDataFlag(dataFlag);
            zkbPartOrderRepository.save(zkbPartOrder);

            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType", zkbPartOrder.getOrderType()));

            try {
                String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_RETURN_EMAIL);
                String token = UserContextService.getAccessToken();
                HttpHeaders header = createHttpHeaders(token);
                HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
                ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            for(ZkbPartOrderReturn r : currentZkbPartOrderReturns){
                r.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.N.toString());
                r.setDataStatus(ZkbPartOrderReturn.DataStatusEnum.Pending.toString());
                zkbPartOrderReturnRepository.save(r);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveModifyApply(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        ZkbPartOrder oldZkbPartOrder = zkbPartOrderRepository.findById(zkbPartOrder.getId());
        Integer qty = null;
        if(!oldZkbPartOrder.getSsaKeyFlag().equals(zkbPartOrder.getSsaKeyFlag())){
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getSsaKeyFlag())){
                qty = 0 - zkbPartOrder.getQty();
            }else{
                qty = zkbPartOrder.getQty();
            }
        }

        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrderRepository.save(zkbPartOrder);
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType())){
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
            if(ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                return;
            }
        }
        if(qty != null && !ZkbConstantUtil.NORMAL_TWO.equals(zkbPartOrder.getArtificialType())){
            String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderDeduct.toString();
            if(qty > 0){
                opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderDeduct.toString();
            }else {
                opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString();
            }
            V2_WorkOrder v2_workOrder = v2_workOrderRepository.findById(zkbPartOrder.getWorkOrderId());
            if(v2_workOrder.getIntExtType() != null && v2_workOrder.getIntExtType() == 1){
                //内修
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                    zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }else{
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }else if(v2_workOrder.getIntExtType() != null && v2_workOrder.getIntExtType() == 2){
                //外修
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                }else{
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                    zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void orderCancelNext(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        String oldStep = zkbPartOrder.getCurrentStep();
        if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())){
            zkbPartOrder.setOrderedDate(new Date());
            zkbPartOrder.setOrderedAuditer(userAccount.getName());
            zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.OrderedReject.toString());
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())){
            zkbPartOrder.setUnOrderDate(new Date());
            zkbPartOrder.setUnOrderAuditer(userAccount.getName());
            zkbPartOrder.setUnOrderAuditerUid(userAccount.getUid());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString());
        }else{
            return;
        }
        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrderRepository.save(zkbPartOrder);

        for(ZkbPartOrderStep s :zkbPartOrder.getZkbPartOrderStepSet()){
            if(s.getStepKey().equals(oldStep)){
                s.setEndTime(new Date());
                zkbPartOrderStepRepository.save(s);
            }
        }
        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey("Complete");
        newZkbPartOrderStep.setStepName("完成");
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);

        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());

        zkbPartOrderQuotaService.subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount,false);

    }
    @Transactional(rollbackFor = Exception.class)
    public void fourHundredCancelNext(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        String oldStep = zkbPartOrder.getCurrentStep();

        zkbPartOrder.setUnOrderDate(new Date());
        zkbPartOrder.setUnOrderAuditer(userAccount.getName());
        zkbPartOrder.setUnOrderAuditerUid(userAccount.getUid());
        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.FourHundredCancel.toString());
        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrderRepository.save(zkbPartOrder);

        List<ZkbPartOrderStep> zkbPartOrderStepList = zkbPartOrderStepRepository.findByPartOrderIdAndStepKey(zkbPartOrder.getId(),ZkbPartOrderStep.PartOrderStep.Complete.toString());
        for(ZkbPartOrderStep s : zkbPartOrderStepList){
            if(s.getStepKey().equals(oldStep)){
                s.setEndTime(new Date());
                zkbPartOrderStepRepository.save(s);
            }
        }
        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.FourHundredCancel.toString());
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        if(ZkbConstantUtil.T.equals(zkbPartOrder.getWhetherDeduct())){
            fourHundredCancelSubApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount,false);
        }
    }

    /**
     * 400拒绝归还名额
     * 此方法主要用于人工400拒绝
     */
    public void fourHundredCancelSubApplyQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount,boolean acceptFlag){
        String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbPartOrderReturn.toString();
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            //region  人工
            if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFlag())){//是否保养
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
                }else{
                    ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
                    if(ZkbConstantUtil.F.equals(zkbContractAsset.getSupportMaintainFlag())){//不是ge 第一次免费保养名额
                        zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                        zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                        zkbContractRepository.save(zkbContract);
                        zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                    }else {
                        //这段逻辑   1. 如果该设备享有GE每年一次的免费保养名额，则合同内每一年第一次申请时不扣除名额，当第二次申请后开始扣除名额
                        if(ZkbConstantUtil.T.equals(zkbPartOrder.getMaintainFirstFlag())){
                            zkbContractAsset.setMaintainStartDate(null);
                            zkbContractAsset.setMaintainEndDate(null);
                            zkbContractAssetRepository.save(zkbContractAsset);
                        }else {
                            zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
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
                        if(ZkbPartOrder.PartOrderStatus.ProductReject.toString().equals(zkbPartOrder.getStatus()) || StrUtil.isNotEmpty(zkbPartOrder.getProductAuditerUid())){
                            //如果是产品经理拒绝或者产品经理审批过说明申请时名额不足，并未扣除过名额
                        }else{
                            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                            if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                                zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                                zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                                zkbContractRepository.save(zkbContract);
                                zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                            }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2) { //外修不扣名额
                            }
                        }
                    }else if(ZkbPartOrder.CcsbQuotaTypeEnum.Maintain.toString().equals(zkbPartOrder.getCcsbApplyType())){
                        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                        if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1) { //内修
                            zkbContract.setCcsbMaintenanceQuota((zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota()) - qty);
                            zkbContract.setCcsbMaintenanceQuotaUse((zkbContract.getCcsbMaintenanceQuotaUse() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaUse()) + qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2) { //外修不扣名额
                        }
                    }
                    return;
                }
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getSsaKeyFlag())){
                    return;
                }
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
                if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) && ZkbConstantUtil.T.equals(zkbContractAsset.getSupportMaintainFlag())){
                    return;
                }
                if(ZkbConstantUtil.T.equals(zkbPartOrder.getScoreFlag())){
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
                }else{
                    AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                    if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 1){ //内修
                        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                            zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                            zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }else{
                            zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }else if(assetInfo.getRepairForm() != null && assetInfo.getRepairForm() == 2){ //外修
                        if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                        }else{
                            zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                            zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                            zkbContractRepository.save(zkbContract);
                            zkbContractQuotaLogService.saveData(zkbContract,zkbPartOrder,null,opType,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbPartOrder.class.getSimpleName(),userAccount,WebUtil.getFieldValueMessage("zkbPoStatus",zkbPartOrder.getStatus()));
                        }
                    }
                }
            }
        }
    }

    public boolean checkPartSeventyPercent(ZkbContract zkbContract,ZkbPartOrder zkbPartOrder){
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
        if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){
            return false;
        }
        if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
            if(assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup().equals(1)){
                int partHighQuotaTotal = zkbContract.getPartHighQuotaTotal() == null ? 0 : zkbContract.getPartHighQuotaTotal();
                int spareArtificailTotal = zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal();
                int total = partHighQuotaTotal + spareArtificailTotal;
                double l = total * 0.7;
                Integer molecule = ( zkbContract.getPartHighQuotaApply() == null ? 0 : zkbContract.getPartHighQuotaApply()) + (zkbContract.getPartHighQuotaUse() == null ? 0 : zkbContract.getPartHighQuotaUse());
                molecule += ( zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + (zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse());
                if(molecule > l){
                    return true;
                }
            }else {
                int total = zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal();
                double l = total * 0.7;
                int molecule = (zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + (zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse());
                if(molecule > l){
                    return true;
                }
            }
        }else if("PCB MVS".equals(zkbContract.getContractType())){
            int total = zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal();
            double l = total * 0.7;
            int molecule = (zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + (zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse());
            if(molecule > l){
                return true;
            }
        }
        return false;
    }

    /**
     * ZKB FE 当备件/探头/工具归还时需要选择为好还还是坏还，当代理商选择了好还关单后，GEAdmin发现归还的备件/探头/工具属于坏还并将信息由好还变为坏还
     * 。系统目前是自动扣除名额的。如果代理商的剩余名额已经为0时，系统不需要继续扣除名额变成负值，系统只需要提示400手动处理即可。
     * 小于0 返回  true 否则 返回 false
     */
    public boolean checkRemaining(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount) {
        boolean arg = false;
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(zkbContract.getId(),zkbPartOrder.getAssetId());
            if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag()) && "highMachine".equals(zkbContractAsset.getAssetType())){
            }else {
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())
                        || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    arg = (zkbContract.getSpareQuota() + qty)<0? true:arg;
                 }else{
                    arg = (zkbContract.getSpareArtificail() + qty)<0? true:arg;
                }
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            if(StringUtils.isEmpty(zkbContract.getQuotaDeductType()) || ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())) {
                if(StringUtils.isEmpty(zkbPartOrder.getHighCatFlag()) || ZkbConstantUtil.F.equals(zkbPartOrder.getHighCatFlag())){
                    arg = (zkbContract.getProbeQuota() + qty)<0? true:arg;
                }else{
                    if(StringUtils.isNotEmpty(zkbPartOrder.getContractCatId())){
                        ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                        if(zkbContractCat != null){
                            if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                arg = (zkbContract.getProbeQuota() + qty)<0? true:arg;
                            }
                        }
                    }
                }
            }else{
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    arg = (zkbContract.getProbeQuota() + qty)<0? true:arg;
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    arg = (zkbContract.getProbeQuotaNormal() + qty)<0? true:arg;
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    arg = (zkbContract.getProbeQuotaSpecial() + qty)<0? true:arg;
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    if(StringUtils.isNotEmpty(zkbPartOrder.getContractCatId())) {
                        ZkbContractCat zkbContractCat = zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
                        if (zkbContractCat != null) {
                            if(ZkbConstantUtil.FOUR_TO_ONE.equals(zkbContractCat.getSnNo())){
                                arg = (zkbContract.getProbeQuotaNormal() + qty)<0? true:arg;
                            }
                        }
                    }
                }
            }
        }
        return  arg;
    }

    /**
     * 发送邮件
     */
    public void sendEmail(ZkbPartOrder zkbPartOrder,String urlType,String token){
        try {
            HashMap<String, Object> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            emailWrapperMap.put("url", "/zkb?id=" + zkbPartOrder.getId());
            emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType", zkbPartOrder.getOrderType()));
            if(zkbPartOrder.getAssetInfo() != null){
                zkbPartOrder.getAssetInfo().setAssetTags(null);
            }
            emailWrapperMap.put("zkbPartOrder", zkbPartOrder);
            String url = "";
            if(StringUtils.isEmpty(urlType)){
                url = getGatewayURL(GatewayURL.PART_ORDER_SEND_EMAIL);
            }else if(ZkbConstantUtil.EMAIL_URL_TYPE_LASTDAY.equals(urlType)){
                url = getGatewayURL(GatewayURL.PART_ORDER_SEND_LAST_DAY_EMAIL);
            }else if(ZkbConstantUtil.EMAIL_URL_TYPE_UNORDER.equals(urlType)){
                url = getGatewayURL(GatewayURL.PART_ORDER_SEND_UN_ORDER);
            }
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" sendEmail " +response.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(ZkbPartOrder zkbPartOrder,String token) {
        HashMap<String, Object> emailWrapperMap = null;
        try {
            emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            emailWrapperMap.put("url", "/zkb?id=" + zkbPartOrder.getId());
            emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType",zkbPartOrder.getOrderType()));
            if(zkbPartOrder.getAssetInfo() != null){
                zkbPartOrder.getAssetInfo().setAssetTags(null);
            }
            emailWrapperMap.put("zkbPartOrder", zkbPartOrder);

            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_MESSAGE);
            //String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" sendMessage "+response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessageAgain(ZkbPartOrder zkbPartOrder,String token){
        sendMessage(zkbPartOrder, token);
    }

    /**
     * 发送微信消息
     */
    public void sendWeChatMessages(ZkbPartOrder zkbPartOrder,String token){
        try {
            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_WECHAT_MESSAGE).concat("?id=").concat(zkbPartOrder.getId());
            //String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" sendWeChatMessages "+response.toString());
        } catch (RestClientException e) {
            e.printStackTrace();
        }

    }

    public void sendWeChatBackMessages(ZkbPartOrder zkbPartOrder){
        try {
            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_WECHAT_BACK_MESSAGE).concat("?id=").concat(zkbPartOrder.getId());
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" sendWeChatBackMessages "+response.toString());
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailForProbeLetter(ZkbPartOrder zkbPartOrder){
        try {
            HashMap<String, Object> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            emailWrapperMap.put("zkbPartOrder", zkbPartOrder);

            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_PROBE_LETTER_EMAIL);
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" 发送说明函邮件 "+response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendWeChatRemindLetter(ZkbPartOrder zkbPartOrder){
        try {
            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_WECHAT_REMIND_LETTER).concat("?id=").concat(zkbPartOrder.getId());
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            if(response.getStatusCode() == HttpStatus.OK){
                return true;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean sendWeChatReturnAudit(ZkbPartOrder zkbPartOrder){
        try {
            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_WECHAT_RETURN_AUDIT).concat("?id=").concat(zkbPartOrder.getId());
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            if(response.getStatusCode() == HttpStatus.OK){
                return true;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean sendEmailToEngineer(ZkbPartOrder zkbPartOrder, String token){
        try {
            HashMap<String, Object> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_EMAIL_ENGINEER);

            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" sendEmailToEngineer " +response.toString());
            if(response.getStatusCode() == HttpStatus.OK){
                return true;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean checkSrNumExist(ZkbPartOrder zkbPartOrder){

        return false;
    }

    public boolean uploadFile(FileUploadEvent event, ZkbPartOrder zkbPartOrder){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        return Optional.ofNullable(v2_blobObject).map(t ->{
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_LETTER);
            v2_blobObject.setBoUuid(zkbPartOrder.getId());
            blobObjectRepository.save(v2_blobObject);

            zkbPartOrder.setUploadLetterFileName(v2_blobObject.getObjectName());
            zkbPartOrder.setUploadLetterFlag("t");
            zkbPartOrder.setUploadLetterFileId(v2_blobObject.getObjectStorageId());
            zkbPartOrderRepository.save(zkbPartOrder);
            return true;
        }).isPresent();

    }

    public V2_BlobObject uploadCloseFile(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if(v2_blobObject != null){
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_PART_CLOSE_FILE);
            //v2_blobObject.setBoUuid(zkbPartOrder.getId());
            blobObjectRepository.save(v2_blobObject);
        }
        return v2_blobObject;
    }

    public V2_BlobObject uploadFile(FileUploadEvent event, String type){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if(v2_blobObject != null){
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(type);
            blobObjectRepository.save(v2_blobObject);
        }
        return v2_blobObject;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean submitCloseReturn(List<ZkbPartOrderReturn> zkbPartOrderReturnModifyList,ZkbPartOrder zkbPartOrder,UserAccount currentUser){
        boolean arg = false;
        List<ZkbPartOrderReturn> oldZkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderId(zkbPartOrder.getId());
        List<ZkbPartOrderReturnHistory> zkbPartOrderReturnHistoryList = new ArrayList<>();

        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        int oldBackTotal = 0;
        for(ZkbPartOrderReturn r : oldZkbPartOrderReturnList){
            ZkbPartOrderReturnHistory history = new ZkbPartOrderReturnHistory();
            history.setPartNo(r.getPartNo());
            history.setOrderNo(r.getOrderNo());
            history.setPartOrderId(r.getPartOrderId());
            history.setReturnType(r.getReturnType());
            history.setReturnDate(r.getReturnDate());
            history.setQty(r.getQty());
            history.setPartSerialNumber(r.getPartSerialNumber());
            history.setPartQualityIssue(r.getPartQualityIssue());
            history.setRemark(r.getRemark());
            history.setWaybillNo(r.getWaybillNo());
            history.setDataStatus(r.getDataStatus());
            history.setReturnCreatedDate(r.getCreatedDate());
            history.setReturnUpdatedDate(r.getUpdatedDate());
            history.setCreateDate(new Date());
            history.setCreateUserId(currentUser.getUid());
            history.setCreateUserName(currentUser.getName());
            zkbPartOrderReturnHistoryList.add(history);
            if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(r.getReturnType())){
                continue;
            }else if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(r.getReturnType())){
                continue;
            }
            oldBackTotal += r.getQty();
        }
        int nowBackTotal = 0;
        for(ZkbPartOrderReturn r : zkbPartOrderReturnModifyList){
            if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(r.getReturnType())){
                continue;
            }else if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(r.getReturnType())){
                continue;
            }
            nowBackTotal += r.getQty();
        }
        int ce =  nowBackTotal - oldBackTotal;
        //1.保存历史数据
        zkbPartOrderReturnHistoryRepository.save(zkbPartOrderReturnHistoryList);

        zkbPartOrderReturnRepository.deleteByPartOrderId(zkbPartOrder.getId());

        //2.保存修改
        for(ZkbPartOrderReturn zkbPartOrderReturn : zkbPartOrderReturnModifyList){
            zkbPartOrderReturn.setDataStatus(ZkbPartOrderReturn.DataStatusEnum.Accept.toString());
            zkbPartOrderReturn.setModifyFlag(ZkbPartOrderReturn.ModifyFlagEnum.N.toString());
        }
        zkbPartOrderReturnRepository.save(zkbPartOrderReturnModifyList);
        //3.名额修改
        if(ce != 0 ){
            //ZKB FE 当备件/探头/工具归还时需要选择为好还还是坏还，当代理商选择了好还关单后，GEAdmin发现归还的备件/探头/工具属于坏还并将信息由好还变为坏还
            // 。系统目前是自动扣除名额的。如果代理商的剩余名额已经为0时，系统不需要继续扣除名额变成负值，系统只需要提示400手动处理即可。
            if(checkRemaining(zkbContract,zkbPartOrder,ce,currentUser)){
                arg = true;
            }else{
                zkbPartOrderQuotaService.subReturnQty(zkbContract,zkbPartOrder,ce,currentUser);
            }
        }
        return arg;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean probeModifyReturn(ZkbPartOrder zkbPartOrder,UserAccount account, List<V2_BlobObject> v2_blobObjectList){
        boolean arg = false;
        ZkbPartOrder oldZkbPartOrder = zkbPartOrderRepository.findById(zkbPartOrder.getId());
        int oldBackTotal = 0;
        if(!ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(oldZkbPartOrder.getReturnType()) && !ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(oldZkbPartOrder.getReturnType())){
            oldBackTotal = zkbPartOrder.getQty();
        }
        int nowBackTotal = 0;
        if(!ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(zkbPartOrder.getReturnType()) && !ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(zkbPartOrder.getReturnType())){
            nowBackTotal = zkbPartOrder.getQty();
        }
        int ce =  nowBackTotal - oldBackTotal;
        zkbPartOrderRepository.save(zkbPartOrder);
        //3.名额修改
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        if(ce != 0 ){
            //ZKB FE 当备件/探头/工具归还时需要选择为好还还是坏还，当代理商选择了好还关单后，GEAdmin发现归还的备件/探头/工具属于坏还并将信息由好还变为坏还
            // 。系统目前是自动扣除名额的。如果代理商的剩余名额已经为0时，系统不需要继续扣除名额变成负值，系统只需要提示400手动处理即可。
            if (checkRemaining(zkbContract,zkbPartOrder,ce,account)){
                arg = true;
            }else{
                zkbPartOrderQuotaService.subReturnQty(zkbContract,zkbPartOrder,ce,account);
            }
        }
        if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.name().equals(zkbPartOrder.getReturnType())){
            if(zkbPartOrder.getOrderNo().toUpperCase().startsWith("FE")){
                List<V2_BlobObject> newList = v2_blobObjectList.stream().filter(t-> StrUtil.isEmpty(t.getBoUuid())).collect(Collectors.toList());
                saveBlob(newList, zkbPartOrder.getId());
            }
        }
        return arg;
    }

    @Transactional(rollbackFor = Exception.class)
    public void returnAudit(ZkbPartOrder zkbPartOrder,List<ZkbPartOrderReturn> auditReturnList,UserAccount userAccount,List<ZkbPartOrderReturn> zkbPartOrderReturnList){
        for(ZkbPartOrderReturn r  : auditReturnList){
            r.setModifyFlag("N");
            r.setClosedDate(new Date());
            r.setClosedAuditerUid(userAccount.getUid());
            r.setClosedAuditer(userAccount.getName());
        }
        zkbPartOrderReturnRepository.save(auditReturnList);
        sendWeChatReturnAudit(zkbPartOrder);

        boolean isAllYes = true;
        for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
            if(ZkbPartOrderReturn.DataStatusEnum.Reject.toString().equals(r.getDataStatus())){
                isAllYes = false;
            }
        }
        if(ZkbPartOrder.PartOrderStatus.Returned.toString().equals(zkbPartOrder.getStatus()) && !isAllYes){
            closedBack(zkbPartOrder,userAccount);
        }else{
            partsClosed(zkbPartOrder,zkbPartOrderReturnList,userAccount);
        }

        //如果审批通过后则进行核算名额
        int v = 0;
        for (ZkbPartOrderReturn r : auditReturnList){
            if(!ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(r.getReturnType()) && !ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(r.getReturnType())){
                if(ZkbPartOrderReturn.DataStatusEnum.Accept.toString().equals(r.getDataStatus())){
                    if(StrUtil.isNotEmpty(zkbPartOrder.getNotGeAsset()) && ZkbConstantUtil.T.equals(zkbPartOrder.getNotGeAsset()) && "goodReturn".equals(r.getReturnType())){
                        if(ZkbConstantUtil.T.equals(r.getSevenReturnedFlag())){
                            v += (r.getQty() - 1);
                        }else{
                            v += r.getQty();
                        }
                    }else{
                        v += r.getQty();
                    }

                }
            }
        }
        if(v > 0){
            ZkbContract contract = zkbContractRepository.findById(zkbPartOrder.getContractId());
            zkbPartOrderQuotaService.subReturnQty(contract,zkbPartOrder,v,userAccount);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void partsClosed(ZkbPartOrder zkbPartOrder,List<ZkbPartOrderReturn> zkbPartOrderReturnAcceptList,UserAccount userAccount){
        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
        //List<ZkbPartOrderReturn> zkbPartOrderReturnAcceptList = zkbPartOrderReturnRepository.findByPartOrderIdAndDataStatus(zkbPartOrder.getId(),ZkbPartOrderReturn.DataStatusEnum.Accept.toString());
        for(ZkbPartOrderReturn r : zkbPartOrderReturnAcceptList){
            if(!ZkbPartOrderReturn.DataStatusEnum.Accept.toString().equals(r.getDataStatus())){
                continue;
            }
            if(returnMap.containsKey(r.getItemId())){
                Integer vv = returnMap.get(r.getItemId());
                vv += r.getQty();
                returnMap.put(r.getItemId(),vv);
            }else{
                returnMap.put(r.getItemId(),r.getQty());
            }
        }
        boolean flag = true;
        for(ZkbPartOrderItem zkbPartOrderItem : zkbPartOrder.getZkbPartOrderItems()){
            if(zkbPartOrderItem.getPartQty() == 0 ){
                continue;
            }
            Integer returnQty = returnMap.get(zkbPartOrderItem.getId());
            if(returnQty == null){
                flag = false;
            }else if(returnQty != zkbPartOrderItem.getPartQty()){
                flag = false;
            }
        }

        if(flag){
            //审批完毕关单
            zkbPartOrder.setClosedDate(new Date());
            zkbPartOrder.setClosedAuditer(userAccount.getName());
            zkbPartOrder.setClosedAuditerUid(userAccount.getUid());
            if(StringUtils.isEmpty(zkbPartOrder.getNotGeAsset())){
                ZkbPartOrderCloseInfo zkbPartOrderCloseInfo = zkbPartOrderCloseInfoRepository.findFirstBySrNo(zkbPartOrder.getSrNo());
                if(zkbPartOrderCloseInfo == null){
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ReturnCompleted.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
                }else{
                    zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }

            Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
            dataFlag++;
            zkbPartOrder.setDataFlag(dataFlag);
            zkbPartOrderRepository.save(zkbPartOrder);

            ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
            newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
            newZkbPartOrderStep.setStartTime(new Date());
            newZkbPartOrderStep.setOwnerId(userAccount.getUid());
            newZkbPartOrderStep.setOwnerName(userAccount.getName());
            newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
            newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
            zkbPartOrderStepRepository.save(newZkbPartOrderStep);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ZkbPartOrder> usVacationHandle(ZkbPartOrderUserConfig zkbPartOrderUserConfig,String isVacation){
        if(StrUtil.isEmpty(zkbPartOrderUserConfig.getUsBackUpId())){
            return null;
        }
        ZkbPartOrderUserConfig backUp = zkbPartOrderUserConfigRepository.findById(zkbPartOrderUserConfig.getUsBackUpId());
        List<ZkbPartOrder> zkbPartOrderList = new ArrayList<>();
        if("t".equals(isVacation)){
            List<SearchFilter> searchFilters = new ArrayList<>();
            searchFilters.add(new SearchFilter("onlineEngineer", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getId()));
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getConfigType()));
            zkbPartOrderList = zkbPartOrderRepository.findBySearchFilter(searchFilters);
            for(ZkbPartOrder p : zkbPartOrderList){
                p.setOnlineEngineer(backUp.getId());
                p.setOnlineEngine(backUp);
                p.setModifyFlag("t");
            }
            zkbPartOrderRepository.save(zkbPartOrderList);
        }else{
            List<SearchFilter> searchFilters = new ArrayList<>();
            searchFilters.add(new SearchFilter("onlineEngineer", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getUsBackUpId()));
            searchFilters.add(new SearchFilter("modifyFlag", SearchFilter.Operator.EQ, "t"));
            searchFilters.add(new SearchFilter("currentStep", SearchFilter.Operator.EQ, zkbPartOrderUserConfig.getConfigType()));
            zkbPartOrderList = zkbPartOrderRepository.findBySearchFilter(searchFilters);
            for(ZkbPartOrder p : zkbPartOrderList){
                p.setOnlineEngineer(zkbPartOrderUserConfig.getId());
                p.setOnlineEngine(zkbPartOrderUserConfig);
            }
            zkbPartOrderRepository.save(zkbPartOrderList);
        }
        return zkbPartOrderList;
    }

    public List<SvcEngineLogisticsLineVO> querySvcLogistics(String job,String orderNo,String partNo){
        List<SearchFilter> searchFilters = new ArrayList<>();
        if(StringUtils.isNotEmpty(job)){
            searchFilters.add(new SearchFilter("job", SearchFilter.Operator.EQ, job));
        }
        if(StringUtils.isNotEmpty(partNo)){
            searchFilters.add(new SearchFilter("item", SearchFilter.Operator.EQ, partNo));
        }
        if(StringUtils.isNotEmpty(orderNo)){
            searchFilters.add(new SearchFilter("order", SearchFilter.Operator.EQ, orderNo));
        }
        Sort sort=new Sort(Sort.Direction.DESC,"lineID","updateTime");
        List<SvcEngineLogistics> svcEngineLogisticsList = svcEngineLogisticsRepository.findBySearchFilter(searchFilters,sort);
        if(svcEngineLogisticsList == null || svcEngineLogisticsList.size() == 0){
            return new ArrayList<>();
        }

        List<SvcEngineLogisticsStatus> svcEngineLogisticsStatusList = svcEngineLogisticsStatusRepository.findAll();
        HashMap<String, SvcEngineLogisticsStatus> statusHashMap = new HashMap<>();
        for(SvcEngineLogisticsStatus st : svcEngineLogisticsStatusList){
            statusHashMap.put(st.getDecodedStatus().toLowerCase(),st);
        }
        HashMap<String,List<SvcEngineLogistics>> lineLogisticsMap = new HashMap<>();
        List<SvcEngineLogistics> lineLogisticsList = new ArrayList<>();
        String oldLineId = svcEngineLogisticsList.get(0).getLineID();
        for(SvcEngineLogistics s : svcEngineLogisticsList){
            if(!s.getLineID().equals(oldLineId)){
                lineLogisticsMap.put(oldLineId,lineLogisticsList);
                oldLineId = s.getLineID();
                lineLogisticsList = new ArrayList<>();
            }
            //转换成本地状态
            Optional.ofNullable(s.getDecodedStatus())
                    .map(ss -> ss = ss.toLowerCase())
                    .map(statusHashMap :: get)
                    .ifPresent(t ->{
                        s.setLocalStatusCode(t.getLocalStatusCode());
                        s.setLocalStatusName(t.getLocalStatusName());
                    });
            lineLogisticsList.add(s);
        }
        lineLogisticsMap.put(oldLineId,lineLogisticsList);

        List<SvcEngineLogisticsLineVO> results = new ArrayList<>();
        for(Map.Entry<String,  List<SvcEngineLogistics>> entry: lineLogisticsMap.entrySet()) {
            List<SvcEngineLogistics> engineLogistics = entry.getValue();
            if(engineLogistics != null && engineLogistics.size() > 0){
                SvcEngineLogistics setSvc =  null;
                for(SvcEngineLogistics s : engineLogistics){
                    if(s != null && StringUtils.isNotEmpty(s.getLocalStatusCode())){
                        setSvc = s;
                        break;
                    }
                }
                if(setSvc == null){
                    break;
                }
                SvcEngineLogisticsLineVO svcEngineLogisticsLineVO = new SvcEngineLogisticsLineVO();
                svcEngineLogisticsLineVO.setLineID(setSvc.getLineID());
                svcEngineLogisticsLineVO.setItem(setSvc.getItem());
                svcEngineLogisticsLineVO.setQty(setSvc.getQty());
                svcEngineLogisticsLineVO.setDescription(setSvc.getDescription());
                svcEngineLogisticsLineVO.setDescriptionChinese(setSvc.getDescriptionChinese());
                svcEngineLogisticsLineVO.setLocalStatusCode(setSvc.getLocalStatusCode());
                svcEngineLogisticsLineVO.setLocalStatusName(setSvc.getLocalStatusName());
                List<SvcEngineLogisticsOrderDetailVO> svcEngineLogisticsOrderDetailVOS = getDetailVOs(setSvc.getLineID(),setSvc.getJob());
                svcEngineLogisticsLineVO.setSvcEngineLogisticsOrderDetails(svcEngineLogisticsOrderDetailVOS);
                List<SvcEngineLogistics> details = new ArrayList<>();
                String localStatusCode = setSvc.getLocalStatusCode();

                for(SvcEngineLogistics s : svcEngineLogisticsList) {
                    if (StringUtils.isEmpty(localStatusCode) || "null".equals(localStatusCode)) {
                        continue;
                    }
                    if(!localStatusCode.equals(s.getLocalStatusCode())){
                        details.add(setSvc);
                    }
                    localStatusCode = s.getLocalStatusCode();
                    setSvc = s;
                }
                if(StringUtils.isNotEmpty(localStatusCode)) {
                    details.add(setSvc);
                }
                svcEngineLogisticsLineVO.setSvcEngineLogisticsList(details);
                results.add(svcEngineLogisticsLineVO);
            }
        }
        return results;
    }

    public List<SvcEngineLogisticsOrderDetailVO> getDetailVOs(String lineId,String job){
        List<SvcEngineLogisticsOrderDetailVO> svcEngineLogisticsOrderDetailVOS = new ArrayList<>();
        List<Object[]> queryList = svcEngineLogisticsRepository.queryDetailByLineIdFromSql(lineId,job);
        if (queryList.isEmpty()) {
            return new ArrayList<>();
        }
        for(int i=0;i< queryList.size();i++){
            Object[] split = queryList.get(i);
            String awb = split[0] == null ? "" : (String)split[0];
            String hyperlink = split[0] == null ? "" : (String)split[1];
            if(StringUtils.isNotEmpty(hyperlink)){
                if(hyperlink.indexOf("http") > -1){

                }else{
                    hyperlink = "";
                }
            }
            String carrier = split[0] == null ? "" : (String)split[2];
            String carrierChinese = split[0] == null ? "" : (String)split[3];
            SvcEngineLogisticsOrderDetailVO s = new SvcEngineLogisticsOrderDetailVO(awb,hyperlink,carrier,carrierChinese);
            svcEngineLogisticsOrderDetailVOS.add(s);
        }

        return svcEngineLogisticsOrderDetailVOS;
    }

    //region  下单修改
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderModify(ZkbPartOrder zkbPartOrder,UserAccount userAccount,String modifyType){
        ZkbPartOrder oldZkbPartOrder = zkbPartOrderRepository.findById(zkbPartOrder.getId());
        String oldWhetherDeduct = oldZkbPartOrder.getWhetherDeduct();

        if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(modifyType)){
            ZkbPartOrderModifyLog zkbPartOrderModifyLog = new ZkbPartOrderModifyLog();
            zkbPartOrderModifyLog.setSrNo(oldZkbPartOrder.getSrNo());
            zkbPartOrderModifyLog.setActivityNo(oldZkbPartOrder.getActivityNo());
            zkbPartOrderModifyLog.setPartOrderId(oldZkbPartOrder.getId());
            zkbPartOrderModifyLog.setCreateUserDate(new Date());
            zkbPartOrderModifyLog.setCreateUserUid(userAccount.getUid());
            zkbPartOrderModifyLog.setCreateUserName(userAccount.getName());
            zkbPartOrderModifyLogRepository.save(zkbPartOrderModifyLog);
        }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(modifyType)){
            if (ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(zkbPartOrder.getId());
                for(ZkbPartOrderItem zkbPartOrderItem : zkbPartOrderItems){
                    ZkbPartOrderModifyLog zkbPartOrderModifyLog = new ZkbPartOrderModifyLog();
                    zkbPartOrderModifyLog.setSrNo(oldZkbPartOrder.getSrNo());
                    zkbPartOrderModifyLog.setActivityNo(oldZkbPartOrder.getActivityNo());
                    zkbPartOrderModifyLog.setPartOrderId(oldZkbPartOrder.getId());
                    zkbPartOrderModifyLog.setPartOrderItemId(zkbPartOrderItem.getId());
                    zkbPartOrderModifyLog.setOrderNo(zkbPartOrderItem.getOrderNo());
                    zkbPartOrderModifyLog.setActualPartNo(zkbPartOrderItem.getActualPartNo());
                    zkbPartOrderModifyLog.setPartName(zkbPartOrderItem.getPartName());
                    zkbPartOrderModifyLog.setPrice(zkbPartOrderItem.getPrice());
                    zkbPartOrderModifyLog.setCreateUserDate(new Date());
                    zkbPartOrderModifyLog.setCreateUserUid(userAccount.getUid());
                    zkbPartOrderModifyLog.setCreateUserName(userAccount.getName());
                    zkbPartOrderModifyLogRepository.save(zkbPartOrderModifyLog);
                }
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                ZkbPartOrderModifyLog zkbPartOrderModifyLog = new ZkbPartOrderModifyLog();
                zkbPartOrderModifyLog.setNeedLetterFlag(oldZkbPartOrder.getNeedLetterFlag());
                zkbPartOrderModifyLog.setOrderNo(oldZkbPartOrder.getOrderNo());
                zkbPartOrderModifyLog.setPrice(oldZkbPartOrder.getPrice());
                zkbPartOrderModifyLog.setPartOrderId(oldZkbPartOrder.getId());
                zkbPartOrderModifyLog.setCreateUserDate(new Date());
                zkbPartOrderModifyLog.setCreateUserUid(userAccount.getUid());
                zkbPartOrderModifyLog.setCreateUserName(userAccount.getName());
                zkbPartOrderModifyLogRepository.save(zkbPartOrderModifyLog);
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
                ZkbPartOrderModifyLog zkbPartOrderModifyLog = new ZkbPartOrderModifyLog();
                zkbPartOrderModifyLog.setSrNo(oldZkbPartOrder.getSrNo());
                zkbPartOrderModifyLog.setActivityNo(oldZkbPartOrder.getActivityNo());
                zkbPartOrderModifyLog.setPartOrderId(oldZkbPartOrder.getId());
                zkbPartOrderModifyLog.setGeEngineerName(oldZkbPartOrder.getGeEngineerName());
                zkbPartOrderModifyLog.setGeEngineerPhone(oldZkbPartOrder.getGeEngineerPhone());
                zkbPartOrderModifyLog.setCreateUserDate(new Date());
                zkbPartOrderModifyLog.setCreateUserUid(userAccount.getUid());
                zkbPartOrderModifyLog.setCreateUserName(userAccount.getName());
                zkbPartOrderModifyLogRepository.save(zkbPartOrderModifyLog);
            }
        }

        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrderRepository.save(zkbPartOrder);

        if (ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) && ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(modifyType)) {
            for (ZkbPartOrderItem i : zkbPartOrder.getZkbPartOrderItems() ) {
                zkbPartOrderItemRepository.save(i);
            }
        }

        if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
            if(!oldWhetherDeduct.equals(zkbPartOrder.getWhetherDeduct())){
                Integer q = zkbPartOrder.getQty();
                if(ZkbConstantUtil.F.equals(zkbPartOrder.getWhetherDeduct())){
                    q =  -q;
                }
                ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                zkbPartOrderQuotaService.reDeductArtificialQuota(zkbContract, zkbPartOrder, q, userAccount);
            }
        }
    }


    //endregion

    //region 探头关单拒绝
    @Transactional(rollbackFor = Exception.class)
    public void probeCloseReject(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
        zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.CloseReject.toString());
        zkbPartOrderRepository.save(zkbPartOrder);

        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);
    }
    //endregion

    public List<V2_BlobObject> queryBlobBySubType(String id){
        List<V2_BlobObject> list = new ArrayList<>();
        List<V2_BlobObject> newList = blobObjectRepository.findByBoUuidAndBoSubType(id, V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN_NEW_CAT);
        list.addAll(newList);
        List<V2_BlobObject> oldList = blobObjectRepository.findByBoUuidAndBoSubType(id, V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_RETURN_OLD_CAT);
        list.addAll(oldList);
        return list;
    }

    public boolean checkDeductMaintain(ZkbContractAsset zkbContractAsset,Date requestTime){
        if(zkbContractAsset.getMaintainStartDate() == null && zkbContractAsset.getMaintainEndDate() == null){
            return false;
        }else{
            boolean flag = true;
            //Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String nowStr = format.format(requestTime);
            try {
                requestTime = format.parse(nowStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(requestTime.getTime() >= zkbContractAsset.getMaintainStartDate().getTime() && requestTime.getTime() <= zkbContractAsset.getMaintainEndDate().getTime()){
                flag = true;
            }else{
                flag = false;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(zkbContractAsset.getMaintainStartDate());
            calendar.add(Calendar.YEAR, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Date tempDate = calendar.getTime();
            if(requestTime.getTime() >= zkbContractAsset.getMaintainStartDate().getTime() && requestTime.getTime() <= tempDate.getTime()){
                flag = true;
            }
            return flag;
        }
    }


    public boolean checkQuotaHistory(String type, ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, ZkbContractAsset zkbContractAsset, String probeType){
        if("apply".equals(type)){
            return zkbContractCatQuotaHistoryService.checkQuotaByHistory(zkbContract, zkbContractAsset, probeType, zkbPartOrder.getQty());
        }else if("order".equals(type)){
            return zkbContractCatQuotaHistoryService.checkQuotaByHistoryWithOutPartOrderId(zkbContract, zkbContractAsset, probeType, zkbPartOrder.getQty(),zkbPartOrder.getId());
        }
        return true;
    }
    public List<ZkbPartOrderItem> queryItemBuPartOrderId(String id){
        List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(id);
        return zkbPartOrderItems;
    }

    public List<String> getZkbTestName(){
        List<String> names = zkbPartOrderRepository.getZkbTestName();
        return CollectionUtils.isNotEmpty(names)?names:new ArrayList<String>();
    }

    public List<ZkbPartOrderCat> queryPartOrderCat(Date contractDate){
        return zkbPartOrderCatRepository.queryByIsVaild("N");
    }

    public List<ZkbPartOrderCat> queryPartOrderCat(List<String> typeList){
        return zkbPartOrderCatRepository.getByValueTypeInAndIsVaildIsNot(typeList,"N");
    }

}

