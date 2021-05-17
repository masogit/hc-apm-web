package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbPartOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    @Autowired
    private ZkbPartOrderItemRepository zkbPartOrderItemRepository;

    @Autowired
    private ZkbPartOrderStepRepository zkbPartOrderStepRepository;

    @Autowired
    private ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    @Autowired
    ZkbContractRepository zkbContractRepository;

    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    BlobObjectRepository blobObjectRepository;

    @Autowired
    ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;

    @Autowired
    OrgInfoRepository orgInfoRepository;

    @Autowired
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    @Autowired
    private BlobFileService blobService;

    @Autowired
    private ZkbPartOrderReturnHistoryRepository zkbPartOrderReturnHistoryRepository;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    public static String NEW_CAT_END_DATE= "2021-03-01";


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
        zkbPartOrderItemRepository.deleteByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.deleteByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderRepository.delete(zkbPartOrder);
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        if(zkbContract != null){
            subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount);
        }
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
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 2));
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 3));

        Pageable pageable = new PageRequest(0, 5000);
        Page page = v2_workOrderRepository.findBySearchFilter(assetFilters, pageable);
        List<V2_WorkOrder> v2_workOrderList = page.getContent();
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
                });
                v2_listHashMap.add(m);
            }
        }
        return v2_listHashMap;
    }

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;

    public List<ZkbContract> queryContractByAsset(String tenUId,String assetId,Date requestTime){
        List<String> strings = zkbContractAssetRepository.findDistinctContractId(assetId);
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("id", SearchFilter.Operator.IN,strings));
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenUId));
        filters.add(new SearchFilter("startDate", SearchFilter.Operator.LTE, requestTime));
        filters.add(new SearchFilter("endDate", SearchFilter.Operator.GT, DateUtils.addDays(requestTime, -1)));
        Pageable pageable = new PageRequest(0, 5000);
        Page page = zkbContractRepository.findBySearchFilter(filters, pageable);
        List<ZkbContract> zkbContracts = new ArrayList(page.getContent());
        return zkbContracts;
    }

    public Boolean checkThreeValid(ZkbPartOrder zkbPartOrder){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("srNum", SearchFilter.Operator.EQ,zkbPartOrder.getSrNum()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.TechnicalReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.ManageReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
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
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
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

        Integer days = getBetweenDay(zkbPartOrder.getRequestDate(),zkbContract.getEndDate()) + 1;
        if(days > 7 || days < 1){
            return "";
        }else{
            return "合同还有【"+String.valueOf(days)+"】天到期，合同临结束7天内已订购【"+String.valueOf(historyQty)+"】个备件/探头！";
        }
    }

    public static int getBetweenDay(Date date1, Date date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String sd1 = format.format(date1);
        String sd2 = format.format(date2);
        try {
            date1 = format.parse(sd1);
            date2 = format.parse(sd2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long days = (date2.getTime() - date1.getTime()) / (24*3600*1000);
        if((date2.getTime() - date1.getTime())% (24*3600*1000) != 0){
            days++;
        }
        return (int)days;
    }

    public int getBetweenDay(String sd1, String sd2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = format.parse(sd1);
            Date date2 = format.parse(sd2);
            long days = (date2.getTime() - date1.getTime()) / (24*3600*1000);
            if((date2.getTime() - date1.getTime())% (24*3600*1000) != 0){
                days++;
            }
            return (int)days;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean checkApplyNumValid(ZkbPartOrder zkbPartOrder,ZkbContract zkbContract){
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ,zkbPartOrder.getOrderType()));
        filters.add(new SearchFilter("contractId", SearchFilter.Operator.EQ,zkbContract.getId()));
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Returned.toString()));
            filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.PartReturned.toString()));
        }
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.Closed.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.TechnicalReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.ManageReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.OrderedReject.toString()));
        filters.add(new SearchFilter("status", SearchFilter.Operator.NE,ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString()));
        int a = 0;
        int b = 0;
        if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            List<ZkbPartOrder> zkbPartOrders = zkbPartOrderRepository.findBySearchFilter(filters);
            Integer qty = 0;
            for(ZkbPartOrder o : zkbPartOrders){
                if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(o.getZkbPartOrderCat().getValueType())){
                    continue;
                }
                qty++;
            }
            a = qty ;
            b = zkbContract.getProbeQuotaTotal() + (zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal())+(zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial());
        }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            filters.add(new SearchFilter("status", SearchFilter.Operator.NE, ZkbPartOrder.PartOrderStatus.PartReturned.toString()));
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
            /*double d = new BigDecimal((float)a/b).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (d > 0.3){
                return false;
            }*/
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
        if(getBetweenDay(zkbContract.getEndDate(),new Date()) == 0){
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
        if(zkbPartOrder.getOnlineEngine() != null){
            zkbPartOrder.setOnlineEngineer(zkbPartOrder.getOnlineEngine().getId());
        }
        AssetInfo assetInfo = assetInfoRepository.findByUid(assetId);
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            zkbPartOrder.setStatus("Requested");
            zkbPartOrder.setCurrentStep("TechnicalPending");
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrder.setStatus("Requested");
            zkbPartOrder.setCurrentStep("TechnicalPending");
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            //如果为超声高端则不需要进行区域经理审批
            if("2".equals(zkbPartOrder.getArtificialType())){
                zkbPartOrder.setStatus("Requested");
                zkbPartOrder.setCurrentStep("OrderPending");
            }else{
                zkbPartOrder.setStatus("Requested");
                zkbPartOrder.setCurrentStep("ManagePending");
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            zkbPartOrder.setStatus("Requested");
            zkbPartOrder.setCurrentStep("ManagePending");
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
            zkbPartOrderStep.setStepKey("TechnicalPending");
            zkbPartOrderStep.setStepName("待技术支持审批");
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrderStep.setStepKey("TechnicalPending");
            zkbPartOrderStep.setStepName("待技术支持审批");
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            if("2".equals(zkbPartOrder.getArtificialType())){
                zkbPartOrderStep.setStepKey("OrderPending");
                zkbPartOrderStep.setStepName("待开单");
            }else{
                zkbPartOrderStep.setStepKey("ManagePending");
                zkbPartOrderStep.setStepName("待经理审批");
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
            zkbPartOrderStep.setStepKey("TechnicalPending");
            zkbPartOrderStep.setStepName("待技术支持审批");
        }
        zkbPartOrderStep.setStartTime(new Date());
        zkbPartOrderStep.setOwnerId(userAccount.getUid());
        zkbPartOrderStep.setOwnerName(userAccount.getName());
        zkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        zkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(zkbPartOrderStep);

        subApplyQty(zkbContract,zkbPartOrder,zkbPartOrder.getQty(),userAccount);
        return zkbPartOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    public ZkbPartOrder copyZkbPartOrder(ZkbPartOrder oldZkbPartOrder,UserAccount userAccount) throws Exception {
        List<ZkbPartOrderItem> oldZkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(oldZkbPartOrder.getId());
        List<ZkbPartOrderReturn> oldZkbPartOrderReturns = zkbPartOrderReturnRepository.findByPartOrderId(oldZkbPartOrder.getId());
        List<V2_BlobObject> v2_blobObjects = blobObjectRepository.findByBoUuid(oldZkbPartOrder.getId());
        ZkbPartOrder newZkbPartOrder = oldZkbPartOrder;
        newZkbPartOrder.setId(null);
        newZkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Requested.toString());
        newZkbPartOrder.setRequestDate(new Date());
        newZkbPartOrder.setRequestUser(userAccount.getName());
        if(ZkbPartOrder.OrderType.Parts.toString().equals(newZkbPartOrder.getOrderType())){
            newZkbPartOrder.setCurrentStep("TechnicalPending");
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(newZkbPartOrder.getOrderType())) {
            newZkbPartOrder.setCurrentStep("TechnicalPending");
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(newZkbPartOrder.getOrderType())) {
            //如果为超声高端则不需要进行区域经理审批
            if("2".equals(newZkbPartOrder.getArtificialType())){
                newZkbPartOrder.setCurrentStep("OrderPending");
            }else{
                newZkbPartOrder.setCurrentStep("ManagePending");
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(newZkbPartOrder.getOrderType())){
            newZkbPartOrder.setCurrentStep("ManagePending");
        }
        newZkbPartOrder.setCreatedBy(userAccount.getLoginName());
        newZkbPartOrder.setCreatedDate(new Date());
        Object o= zkbPartOrderRepository.getMaxIndex("PO"+simpleDateFormat.format(new Date())+"%") ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String partOrderNo = "PO"+simpleDateFormat.format(new Date())+String.format("%04d",index);
        newZkbPartOrder.setPartOrderNo(partOrderNo);
        newZkbPartOrder.setPartOrderIndex(index);
        newZkbPartOrder = zkbPartOrderRepository.save(newZkbPartOrder);

        for(ZkbPartOrderItem zkbPartOrderItem : oldZkbPartOrderItems){
            ZkbPartOrderItem newItem = CopyByInvok.translateField(zkbPartOrderItem,ZkbPartOrderItem.class);
            newItem.setId(null);
            newItem.setPartOrderId(newZkbPartOrder.getId());
            zkbPartOrderItemRepository.save(newItem);
        }

        for(V2_BlobObject v : v2_blobObjects){
            V2_BlobObject nv =  CopyByInvok.translateField(v,V2_BlobObject.class);
            nv.setBoUuid(newZkbPartOrder.getId());
        }

        ZkbContract zkbContract = zkbContractRepository.findById(newZkbPartOrder.getContractId());
        subApplyQty(zkbContract,newZkbPartOrder,newZkbPartOrder.getQty(),userAccount);
        return newZkbPartOrder;
    }

    @Autowired
    ZkbPartOrderMapper zkbPartOrderMapper;

    public List<ZkbPoHistoryQueryVo> queryZkbHistoryByCondition(List<SearchFilter> filters,String tenantUID){
        List<ZkbPoHistoryQueryVo> zkbPoHistoryQueryVos = zkbPartOrderMapper.queryZkbHistory(filters,tenantUID);
        return zkbPoHistoryQueryVos;
    }

    public List<ZkbPoHistoryQueryVo> queryGeHistoryByCondition(List<SearchFilter> filters,String tenantUID){
        List<ZkbPoHistoryQueryVo> zkbPoHistoryQueryVos = zkbPartOrderMapper.queryGeHistory(filters,tenantUID);
        return zkbPoHistoryQueryVos;
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

    @Transactional(rollbackFor = Exception.class)
    public void auditNext(ZkbPartOrder zkbPartOrder,boolean nextFlag,UserAccount userAccount){
        String oldStep = zkbPartOrder.getCurrentStep();
        if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setTechAuditDate(new Date());
            zkbPartOrder.setTechAuditerUid(userAccount.getUid());
            zkbPartOrder.setTechAuditer(userAccount.getName());
            if(nextFlag){
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrder.getCurrentStep())){
            zkbPartOrder.setManageAuditDate(new Date());
            zkbPartOrder.setManageAuditerUid(userAccount.getUid());
            zkbPartOrder.setManageAuditer(userAccount.getName());
            if(nextFlag){
                ZkbContract c = zkbContractRepository.findById(zkbPartOrder.getContractId());
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    if(!ZkbPartOrderCat.ValueType.HighValue.toString().equals(zkbPartOrder.getZkbPartOrderCat().getValueType())){
                        boolean r = checkApplyNumValid(zkbPartOrder,c);
                        if(!r){
                            zkbPartOrder.setCheckApplyNumFlag(CopyByInvok.F);
                        }else{
                            zkbPartOrder.setCheckApplyNumFlag(CopyByInvok.T);
                        }
                    }else{
                        zkbPartOrder.setCheckApplyNumFlag(CopyByInvok.T);
                    }
                }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
                    boolean r = checkApplyNumValid(zkbPartOrder,c);
                    if(!r){
                        zkbPartOrder.setCheckApplyNumFlag(CopyByInvok.F);
                    }else{
                        zkbPartOrder.setCheckApplyNumFlag(CopyByInvok.T);
                    }
                }

                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ManageAccept.toString());
                if(CopyByInvok.F.equals(zkbPartOrder.getCheckApplyNumFlag())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                }else{
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
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                }else{
                    zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                }
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.DirectorReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
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
        if(nextFlag){
            if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(oldStep)) {
                newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                newZkbPartOrderStep.setStepName("待经理审批");
            }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(oldStep)){
                if(CopyByInvok.F.equals(zkbPartOrder.getCheckApplyNumFlag())){
                    newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                    newZkbPartOrderStep.setStepName("待总监审批");
                }else {
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                        newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                        newZkbPartOrderStep.setStepName("待开单");
                    }else{
                        newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                        newZkbPartOrderStep.setStepName("待下单");
                    }
                }
            }else if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(oldStep)){
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                    newZkbPartOrderStep.setStepName("待开单");
                }else{
                    newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                    newZkbPartOrderStep.setStepName("待下单");
                }
            }
        }else{
            newZkbPartOrderStep.setStepKey("Complete");
            newZkbPartOrderStep.setStepName("完成");
        }

        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);

        if(!nextFlag){
            ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
            subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount);
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
    public void normalNext(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        String oldStep = zkbPartOrder.getCurrentStep();
        Integer qty = zkbPartOrder.getQty();
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
            zkbPartOrder.setOrderedDate(new Date());
            zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
            zkbPartOrder.setOrderedAuditer(userAccount.getName());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
            zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
                zkbPartOrder.setOrderedDate(new Date());
                zkbPartOrder.setOrderedAuditer(userAccount.getName());
                zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
                if(CopyByInvok.T.equals(zkbPartOrder.getNewCatFlag())){
                    ZkbContract c = zkbContractRepository.findById(zkbPartOrder.getContractId());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = null;
                    try {
                        d = format.parse(NEW_CAT_END_DATE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(d == null){
                        d = new Date();
                    }
                    if(getBetweenDay(d,c.getStartDate()) < 0){
                        zkbPartOrder.setQty(zkbPartOrder.getQty() * 2);
                    }
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
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }else{
            if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(zkbPartOrder.getStatus()) || ZkbPartOrder.PartOrderStatus.DirectorAccept.toString().equals(zkbPartOrder.getStatus())){
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
                zkbPartOrder.setClosedDate(new Date());
                zkbPartOrder.setClosedAuditer(userAccount.getName());
                zkbPartOrder.setClosedAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }
        Integer dataFlag = zkbPartOrder.getDataFlag() == null ? 0 : zkbPartOrder.getDataFlag();
        dataFlag++;
        zkbPartOrder.setDataFlag(dataFlag);
        zkbPartOrder = zkbPartOrderRepository.save(zkbPartOrder);

        List<ZkbPartOrderStep> zkbPartOrderStepList = zkbPartOrderStepRepository.findByPartOrderId(zkbPartOrder.getId());
        for(ZkbPartOrderStep s : zkbPartOrderStepList){
            if(s.getStepKey().equals(oldStep)){
                s.setEndTime(new Date());
                zkbPartOrderStepRepository.save(s);
            }
        }
        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
            newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.Complete.toString());
        }else{
            if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())) {
                newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
                newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())) {
                newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ClosePending.toString())) {
                newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }
        newZkbPartOrderStep.setStepName(WebUtil.getFieldValueMessage("zkbPoSteps",newZkbPartOrderStep.getStepKey()));
        newZkbPartOrderStep.setStartTime(new Date());
        newZkbPartOrderStep.setOwnerId(userAccount.getUid());
        newZkbPartOrderStep.setOwnerName(userAccount.getName());
        newZkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        newZkbPartOrderStep.setPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.save(newZkbPartOrderStep);

        if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
            if (ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
                //if("1".equals(zkbPartOrder.getArtificialType())){
                    if("t".equals(zkbPartOrder.getWhetherDeduct())){
                        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                        subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
                    }else {
                        //不扣除名额，那么申请中-1，剩余+1
                        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                        subApplyQty(zkbContract,zkbPartOrder, (0- qty),userAccount);
                    }
                /*}else{
                    ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                    subUseQty(zkbContract, zkbPartOrder, zkbPartOrder.getQty());
                }*/
            }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
                ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
            }else {
                ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                subUseQty(zkbContract, zkbPartOrder, qty,userAccount);
            }
            if (ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
                for (ZkbPartOrderItem i : zkbPartOrder.getZkbPartOrderItems()) {
                    zkbPartOrderItemRepository.save(i);
                }
            }
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())) {
            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                /*ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                subReturnQty(zkbContract,zkbPartOrder, zkbPartOrder.getReturnQty());*/
            }
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ClosePending.toString())){
            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                subReturnQty(zkbContract,zkbPartOrder, zkbPartOrder.getReturnQty());
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
                List<ZkbPartOrderReturn> zkbPartOrderReturns = zkbPartOrderReturnRepository.findByPartOrderId(zkbPartOrder.getId());
                if(zkbPartOrderReturns != null && zkbPartOrderReturns.size() > 0){
                    int v = 0;
                    ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                    for (ZkbPartOrderReturn r : zkbPartOrderReturns){
                        if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(r.getReturnType())){
                            continue;
                        }else if(ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(r.getReturnType())){
                            continue;
                        }else{
                            v += r.getQty();
                        }
                    }
                    subReturnQty(zkbContract,zkbPartOrder,v);
                }
            }
        }
    }

    public void batchesReturnNext(ZkbPartOrder zkbPartOrder,List<ZkbPartOrderReturn> currentZkbPartOrderReturns,UserAccount userAccount){
        //第一步，先归还非坏还的数量
       /* int v = 0;
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        for (ZkbPartOrderReturn r : currentZkbPartOrderReturns){
            if(!"badReturn".equals(r.getReturnType())){
                v += r.getQty();
            }
        }
        subReturnQty(zkbContract,zkbPartOrder,v);*/

        //第二步判断是否归还所有的配件，
        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
        List<ZkbPartOrderReturn> zkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderId(zkbPartOrder.getId());
        for(ZkbPartOrderReturn r : zkbPartOrderReturnList){
            if(returnMap.containsKey(r.getPartNo())){
                Integer vv = returnMap.get(r.getPartNo());
                vv += r.getQty();
                returnMap.put(r.getPartNo(),vv);
            }else{
                returnMap.put(r.getPartNo(),r.getQty());
            }
        }
        boolean flag = true;
        for(ZkbPartOrderItem zkbPartOrderItem : zkbPartOrder.getZkbPartOrderItems()){
            Integer returnQty = returnMap.get(zkbPartOrderItem.getPartNo());
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
        subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty(),userAccount);
    }


    public void subApplyQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            if(!"2".equals(zkbPartOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) - qty);
                    zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                }else{
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                }
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            if(StringUtils.isEmpty(zkbPartOrder.getProbeQuotaType())){
                zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qty);
                zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) + qty);
            }else{
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qty);
                    zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) + qty);
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaNormal((zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal()) - qty);
                    zkbContract.setProbeQuotaNormalApply((zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply()) + qty);
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaSpecial((zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial()) - qty);
                    zkbContract.setProbeQuotaSpecialApply((zkbContract.getProbeQuotaSpecialApply() == null ? 0 : zkbContract.getProbeQuotaSpecialApply()) + qty);
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(zkbPartOrder.getStatus()) ||
                    ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(zkbPartOrder.getStatus()) ||
                    ZkbPartOrder.PartOrderStatus.DirectorReject.toString().equals(zkbPartOrder.getStatus())){
                        zkbContract.setProbeQuotaHigh((zkbContract.getProbeQuotaHigh() == null ? 0 : zkbContract.getProbeQuotaHigh()) + 1);
                        zkbContract.setProbeQuotaHighApply((zkbContract.getProbeQuotaHighApply() == null ? 0 : zkbContract.getProbeQuotaHighApply()) - 1);
                    }else{
                        zkbContract.setProbeQuotaHigh((zkbContract.getProbeQuotaHigh() == null ? 0 : zkbContract.getProbeQuotaHigh()) - 1);
                        zkbContract.setProbeQuotaHighApply((zkbContract.getProbeQuotaHighApply() == null ? 0 : zkbContract.getProbeQuotaHighApply()) + 1);
                    }
                }
            }
            zkbContractRepository.save(zkbContract);
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            if("2".equals(zkbPartOrder.getArtificialType())){
                return;
            }
            if(CopyByInvok.T.equals(zkbPartOrder.getScoreFlag())){
                if(qty < 0){
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
                }else{
                    zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaDeduct.toString());
                }
            }else{
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                    zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                }else{
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                }
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) ||
                ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) - qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }
        }
    }

    public void subUseQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty,UserAccount userAccount) {
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            if(!"2".equals(zkbPartOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                    zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                    zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty);
                    zkbContractRepository.save(zkbContract);
                }else{
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                    zkbContractRepository.save(zkbContract);
                }
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            if(StringUtils.isEmpty(zkbPartOrder.getProbeQuotaType())) {
                if (CopyByInvok.T.equals(zkbPartOrder.getNewCatFlag())) {
                    zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + (qty * 2));
                    zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qty);
                }else{
                    zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + qty);
                }
                zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) - qty);
            }else {
                if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + qty);
                    zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) - qty);
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaNormalUse((zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse()) + qty);
                    zkbContract.setProbeQuotaNormalApply((zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply()) - qty);
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaSpecialUse((zkbContract.getProbeQuotaSpecialUse() == null ? 0 : zkbContract.getProbeQuotaSpecialUse()) + qty);
                    zkbContract.setProbeQuotaSpecialApply((zkbContract.getProbeQuotaSpecialApply() == null ? 0 : zkbContract.getProbeQuotaSpecialApply()) - qty);
                }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaHighUse((zkbContract.getProbeQuotaHighUse() == null ? 0 : zkbContract.getProbeQuotaHighUse()) + 1);
                    zkbContract.setProbeQuotaHighApply((zkbContract.getProbeQuotaHighApply() == null ? 0 : zkbContract.getProbeQuotaHighApply()) - 1);
                }
            }
            zkbContractRepository.save(zkbContract);
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            if("2".equals(zkbPartOrder.getArtificialType())){
                return;
            }
            if(CopyByInvok.T.equals(zkbPartOrder.getScoreFlag())){
                //zkbIntegralService.deductScore(zkbPartOrder.getTenantUID(),zkbPartOrder.getQty(),userAccount,ZkbIntegralRecord.RecordType.QuotaReturn.toString());
            }else{
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                    zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) - qty);
                    zkbContractRepository.save(zkbContract);
                }else{
                    if(StringUtils.isEmpty(zkbPartOrder.getHaveTrainCourseFlag()) || "t".equals(zkbPartOrder.getHaveTrainCourseFlag())) {
                        zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                        zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                        zkbContractRepository.save(zkbContract);
                    }
                }
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }
        }
    }

    public void subReturnQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty) {
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            if(!"2".equals(zkbPartOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())
                    || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                    zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) - qty);
                    zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + qty);
                    zkbContractRepository.save(zkbContract);
                }else{
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                    zkbContractRepository.save(zkbContract);
                }
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            if(!ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(zkbPartOrder.getReturnType())
                && !ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(zkbPartOrder.getReturnType())) {
                if(StringUtils.isEmpty(zkbPartOrder.getProbeQuotaType())){
                    zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) - qty);
                    zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) + qty);
                }else{
                    if(ZkbPartOrder.ProbeQuotaTypeEnum.Any.toString().equals(zkbPartOrder.getProbeQuotaType())){
                        zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) - qty);
                        zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) + qty);
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Normal.toString().equals(zkbPartOrder.getProbeQuotaType())){
                        zkbContract.setProbeQuotaNormalUse((zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse()) - qty);
                        zkbContract.setProbeQuotaNormal((zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal()) + qty);
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.Special.toString().equals(zkbPartOrder.getProbeQuotaType())){
                        zkbContract.setProbeQuotaSpecialUse((zkbContract.getProbeQuotaSpecialUse() == null ? 0 : zkbContract.getProbeQuotaSpecialUse()) - qty);
                        zkbContract.setProbeQuotaSpecial((zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial()) + qty);
                    }else if (ZkbPartOrder.ProbeQuotaTypeEnum.High.toString().equals(zkbPartOrder.getProbeQuotaType())){
                        zkbContract.setProbeQuotaHighUse((zkbContract.getProbeQuotaHighUse() == null ? 0 : zkbContract.getProbeQuotaHighUse()) - 1);
                        zkbContract.setProbeQuotaHigh((zkbContract.getProbeQuotaHigh() == null ? 0 : zkbContract.getProbeQuotaHigh()) + 1);
                    }
                }
                zkbContractRepository.save(zkbContract);
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())
                || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) - qty);
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                zkbContractRepository.save(zkbContract);
            }
        }
    }

    /**
     * 发送邮件
     */
    public void sendEmail(ZkbPartOrder zkbPartOrder,String urlType,String token){
        try {
            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            emailWrapperMap.put("url", "?id=" + zkbPartOrder.getId());
            emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType", zkbPartOrder.getOrderType()));

            String url = "";
            if(StringUtils.isEmpty(urlType)){
                url = getGatewayURL(GatewayURL.PART_ORDER_SEND_EMAIL);
            }else if(CopyByInvok.EMAIL_URL_TYPE_LASTDAY.equals(urlType)){
                url = getGatewayURL(GatewayURL.PART_ORDER_SEND_LAST_DAY_EMAIL);
            }else if(CopyByInvok.EMAIL_URL_TYPE_UNORDER.equals(urlType)){
                url = getGatewayURL(GatewayURL.PART_ORDER_SEND_UN_ORDER);
            }
            //String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            logger.info(zkbPartOrder.getPartOrderNo()+" "+ url + " " +response.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(ZkbPartOrder zkbPartOrder,String token) {
        //如果是备件申请，并且在技术支持审批and放射类设备则不需要发短信
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrder.getCurrentStep())){
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo != null && assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup() == 1){
                    //return;
                }
            }
        }
        if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrder.getCurrentStep()) || ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrder.getCurrentStep()) ||
                ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrder.getCurrentStep())) {
            HashMap<String, String> emailWrapperMap = null;
            try {
                emailWrapperMap = new HashMap<>();
                emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
                emailWrapperMap.put("systemId", zkbPartOrder.getId());
                emailWrapperMap.put("url", "?id=" + zkbPartOrder.getId());
                emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType",zkbPartOrder.getOrderType()));
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
    }

    /**
     * 发送微信消息
     */
    public void sendWeChatMessages(ZkbPartOrder zkbPartOrder,String token){
        //if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
        List<String> statusList = new ArrayList<>();
        statusList.add(ZkbPartOrder.PartOrderStatus.ManageAccept.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.ManageReject.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.Ordered.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.OrderedReject.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.DirectorReject.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.DirectorAccept.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString());
        statusList.add(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString());
        if (statusList.contains(zkbPartOrder.getStatus())) {
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
            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
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
        System.out.println("---sendWeChatReturnAudit----");
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
        /*if (null != v2_blobObject) {
            //blobObject.setBoId(selected.getId());
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_PART_ORDER_LETTER);
            v2_blobObject.setBoUuid(zkbPartOrder.getId());
            blobObjectRepository.save(v2_blobObject);

            zkbPartOrder.setUploadLetterFileName(v2_blobObject.getObjectName());
            zkbPartOrder.setUploadLetterFlag("t");
            zkbPartOrder.setUploadLetterFileId(v2_blobObject.getId());
            zkbPartOrderRepository.save(zkbPartOrder);
            return true;
        }
        return false;*/
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitCloseReturn(List<ZkbPartOrderReturn> zkbPartOrderReturnModifyList,ZkbPartOrder zkbPartOrder,UserAccount currentUser){

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

        //2.保存修改
        zkbPartOrderReturnRepository.save(zkbPartOrderReturnModifyList);

        //3.名额修改
        subReturnQty(zkbContract,zkbPartOrder,ce);
    }

    @Transactional(rollbackFor = Exception.class)
    public void returnAudit(ZkbPartOrder zkbPartOrder,List<ZkbPartOrderReturn> zkbPartOrderReturnList,UserAccount userAccount){
        for(ZkbPartOrderReturn r  : zkbPartOrderReturnList){
            r.setModifyFlag("N");
            r.setClosedDate(new Date());
            r.setClosedAuditerUid(userAccount.getUid());
            r.setClosedAuditer(userAccount.getName());
        }
        zkbPartOrderReturnRepository.save(zkbPartOrderReturnList);
        sendWeChatReturnAudit(zkbPartOrder);

        //如果审批通过后则进行核算名额
        int v = 0;
        for (ZkbPartOrderReturn r : zkbPartOrderReturnList){
            if(!ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.useNoReturn.toString().equals(r.getReturnType()) && !ZkbPartOrderReturn.ReturnTypeNoQuotaEnum.badReturn.toString().equals(r.getReturnType())){
                if(ZkbPartOrderReturn.DataStatusEnum.Accept.toString().equals(r.getDataStatus())){
                    v += r.getQty();
                }
            }
        }
        if(v > 0){
            ZkbContract contract = zkbContractRepository.findById(zkbPartOrder.getContractId());
            subReturnQty(contract,zkbPartOrder,v);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void partsClosed(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        HashMap<String, Integer> returnMap = new HashMap<String, Integer>();
        List<ZkbPartOrderReturn> zkbPartOrderReturnAcceptList = zkbPartOrderReturnRepository.findByPartOrderIdAndDataStatus(zkbPartOrder.getId(),ZkbPartOrderReturn.DataStatusEnum.Accept.toString());
        for(ZkbPartOrderReturn r : zkbPartOrderReturnAcceptList){
            if(returnMap.containsKey(r.getPartNo())){
                Integer vv = returnMap.get(r.getPartNo());
                vv += r.getQty();
                returnMap.put(r.getPartNo(),vv);
            }else{
                returnMap.put(r.getPartNo(),r.getQty());
            }
        }
        boolean flag = true;
        for(ZkbPartOrderItem zkbPartOrderItem : zkbPartOrder.getZkbPartOrderItems()){
            Integer returnQty = returnMap.get(zkbPartOrderItem.getPartNo());
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
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Closed.toString());
            zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
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


    public List<ZkbPartOrderItem> queryItemBuPartOrderId(String id){
        List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(id);
        return zkbPartOrderItems;
    }

    public List<ZkbPartOrderCat> queryPartOrderCat(Date contractDate){
        return zkbPartOrderCatRepository.queryByDate(contractDate,contractDate);
        //return zkbPartOrderCatRepository.findByValueTypeIsNot(ZkbPartOrderCat.ValueType.HighValue.toString());
    }

    public List<ZkbPartOrderCat> queryPartOrderCat(Date contractDate,List<String> typeList){
        return zkbPartOrderCatRepository.getByStartDateLessThanEqualAndEndDateGreaterThanEqualAndValueTypeIn(contractDate,contractDate,typeList);
    }

    public ZkbPartOrderRepository getZkbPartOrderRepository() {
        return zkbPartOrderRepository;
    }

    public void setZkbPartOrderRepository(ZkbPartOrderRepository zkbPartOrderRepository) {
        this.zkbPartOrderRepository = zkbPartOrderRepository;
    }

    public ZkbPartOrderItemRepository getZkbPartOrderItemRepository() {
        return zkbPartOrderItemRepository;
    }

    public void setZkbPartOrderItemRepository(ZkbPartOrderItemRepository zkbPartOrderItemRepository) {
        this.zkbPartOrderItemRepository = zkbPartOrderItemRepository;
    }

    public ZkbPartOrderStepRepository getZkbPartOrderStepRepository() {
        return zkbPartOrderStepRepository;
    }

    public void setZkbPartOrderStepRepository(ZkbPartOrderStepRepository zkbPartOrderStepRepository) {
        this.zkbPartOrderStepRepository = zkbPartOrderStepRepository;
    }

    public ZkbPartOrderCatRepository getZkbPartOrderCatRepository() {
        return zkbPartOrderCatRepository;
    }

    public void setZkbPartOrderCatRepository(ZkbPartOrderCatRepository zkbPartOrderCatRepository) {
        this.zkbPartOrderCatRepository = zkbPartOrderCatRepository;
    }

    public ZkbContractRepository getZkbContractRepository() {
        return zkbContractRepository;
    }

    public void setZkbContractRepository(ZkbContractRepository zkbContractRepository) {
        this.zkbContractRepository = zkbContractRepository;
    }

    public V2_WorkOrderRepository getV2_workOrderRepository() {
        return v2_workOrderRepository;
    }

    public void setV2_workOrderRepository(V2_WorkOrderRepository v2_workOrderRepository) {
        this.v2_workOrderRepository = v2_workOrderRepository;
    }

    public AssetInfoRepository getAssetInfoRepository() {
        return assetInfoRepository;
    }

    public void setAssetInfoRepository(AssetInfoRepository assetInfoRepository) {
        this.assetInfoRepository = assetInfoRepository;
    }

    public ZkbPartOrderUserConfigRepository getZkbPartOrderUserConfigRepository() {
        return zkbPartOrderUserConfigRepository;
    }

    public void setZkbPartOrderUserConfigRepository(ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository) {
        this.zkbPartOrderUserConfigRepository = zkbPartOrderUserConfigRepository;
    }

    public ZkbPartOrderReturnRepository getZkbPartOrderReturnRepository() {
        return zkbPartOrderReturnRepository;
    }

    public void setZkbPartOrderReturnRepository(ZkbPartOrderReturnRepository zkbPartOrderReturnRepository) {
        this.zkbPartOrderReturnRepository = zkbPartOrderReturnRepository;
    }
}

