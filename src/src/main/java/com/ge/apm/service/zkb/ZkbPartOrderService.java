package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@Service
public class ZkbPartOrderService  extends MicroServiceInvoker {

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

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");


    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(ZkbPartOrder zkbPartOrder){
        zkbPartOrderItemRepository.deleteByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderStepRepository.deleteByPartOrderId(zkbPartOrder.getId());
        zkbPartOrderRepository.delete(zkbPartOrder);
        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
        subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty());
    }


    public List<HashMap<String, String>> getQueryWorkFlowList(V2_WorkOrder v2_workOrder){
        List<HashMap<String, String>> v2_listHashMap = new ArrayList<>();
        List<SearchFilter> assetFilters = new ArrayList<>();
        //assetFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 1));

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
            HashMap<String, String> m = new HashMap<>();
            m.put("name",v.getAssetInfo().getName());
            m.put("systemId",v.getAssetInfo().getSystemId());
            m.put("srNum",v.getServiceRequest().getSrNum());
            m.put("id",v.getId());
            m.put("assetId",v.getAssetInfo().getUid());
            m.put("customerName",v.getServiceRequest().getCustomerName());
            m.put("customerTel",v.getServiceRequest().getCustomerTel());
            m.put("faultDescription",v.getServiceRequest().getRequestReason());
            String siteUid = v.getAssetInfo().getSiteUID();
            if(StringUtils.isNotEmpty(siteUid)){
                OrgInfo orgInfo = orgInfoRepository.getByUid(siteUid);
                if(orgInfo != null){
                    m.put("hospitalName",orgInfo.getName());
                }
            }
            v2_listHashMap.add(m);
        }
        return v2_listHashMap;
    }

    @Autowired
    ZkbContractAssetRepository zkbContractAssetRepository;

    public List<ZkbContract> queryContractByAsset(String tenUId,String assetId){
        List<String> strings = zkbContractAssetRepository.findDistinctContractId(assetId);
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("id", SearchFilter.Operator.IN,strings));
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,tenUId));
        filters.add(new SearchFilter("startDate", SearchFilter.Operator.LTE, new Date()));
        filters.add(new SearchFilter("endDate", SearchFilter.Operator.GTE,new Date()));
       /* if(ZkbPartOrder.OrderType.Parts.toString().equals(orderType)){
            filters.add(new SearchFilter("spareQuota", SearchFilter.Operator.GT,1));
        } else if(ZkbPartOrder.OrderType.Probe.toString().equals(orderType)){
            filters.add(new SearchFilter("probeQuota", SearchFilter.Operator.GT,1));
        } else if(ZkbPartOrder.OrderType.Artificial.toString().equals(orderType)){
            filters.add(new SearchFilter("geArtificialSupportTimes", SearchFilter.Operator.GT,1,"1*2+"));
            filters.add(new SearchFilter("spareArtificail", SearchFilter.Operator.GT,1,"1*2+"));
        }*/
        Pageable pageable = new PageRequest(0, 5000);
        Page page = zkbContractRepository.findBySearchFilter(filters, pageable);
        List<ZkbContract> zkbContracts = new ArrayList(page.getContent());
        return zkbContracts;
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

        subApplyQty(zkbContract,zkbPartOrder,zkbPartOrder.getQty());

        return zkbPartOrder;
    }

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
                HashMap<String, String> m = new HashMap<>();
                m.put("name",v.getAssetInfo().getName());
                m.put("systemId",v.getAssetInfo().getSystemId());
                m.put("srNum",v.getServiceRequest().getSrNum());
                m.put("customerName",v.getServiceRequest().getCustomerName());
                m.put("customerTel",v.getServiceRequest().getCustomerTel());
                m.put("id",v.getId());
                m.put("assetId",v.getAssetInfo().getUid());
                m.put("faultDescription",v.getServiceRequest().getRequestReason());
                String siteUid = v.getAssetInfo().getSiteUID();
                if(StringUtils.isNotEmpty(siteUid)){
                    OrgInfo orgInfo = orgInfoRepository.getByUid(siteUid);
                    if(orgInfo != null){
                        m.put("hospitalName",orgInfo.getName());
                    }
                }
                map.put("v2", m);
            }
        }

        if(zkbPartOrder != null){
            List<V2_BlobObject> v2_blobObjectList = blobObjectRepository.findByBoUuid(zkbPartOrder.getId());
            map.put("v2_blobObjectList",v2_blobObjectList);
        }

        map.put("zkbPartOrder",zkbPartOrder);
        return map;
    }

    public List<ZkbPartOrderReturn> queryReturnListByPartOrderId(String id){
        List<ZkbPartOrderReturn> zkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderId(id);
        return zkbPartOrderReturnList;
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
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ManageAccept.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
            }else{
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.ManageReject.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
            }
        }
        zkbPartOrderRepository.save(zkbPartOrder);
        List<ZkbPartOrderStep> zkbPartOrderStepList = zkbPartOrderStepRepository.findByPartOrderId(zkbPartOrder.getId());
        for(ZkbPartOrderStep s : zkbPartOrderStepList){
            if(s.getStepKey().equals(oldStep)){
                s.setEndTime(new Date());
                zkbPartOrderStepRepository.save(s);
            }
        }
        ZkbPartOrderStep newZkbPartOrderStep = new ZkbPartOrderStep();

        if(nextFlag){
            if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(oldStep)) {
                newZkbPartOrderStep.setStepKey("ManagePending");
                newZkbPartOrderStep.setStepName("待经理审批");
            }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(oldStep)){
                newZkbPartOrderStep.setStepKey("OrderPending");
                newZkbPartOrderStep.setStepName("待开单");
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
            subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty());
        }
    }

    /**
     * 订货、归还、关单按钮操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void normalNext(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        String oldStep = zkbPartOrder.getCurrentStep();
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())){
            zkbPartOrder.setOrderedDate(new Date());
            zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
            zkbPartOrder.setOrderedAuditer(userAccount.getName());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
            zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.Complete.toString());
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
                zkbPartOrder.setUnOrderDate(new Date());
                zkbPartOrder.setUnOrderAuditer(userAccount.getName());
                zkbPartOrder.setUnOrderAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.UnOrdered.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())) {
                zkbPartOrder.setOrderedDate(new Date());
                zkbPartOrder.setOrderedAuditer(userAccount.getName());
                zkbPartOrder.setOrderedAuditerUid(userAccount.getUid());
                zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.Ordered.toString());
                zkbPartOrder.setCurrentStep(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
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
            if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(zkbPartOrder.getStatus())){
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
            if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending.toString())) {
                if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                    newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                }else{
                    newZkbPartOrderStep.setStepKey(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
                }
            }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())) {
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
                if("1".equals(zkbPartOrder.getArtificialType())){
                    if("t".equals(zkbPartOrder.getWhetherDeduct())){
                        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                        subUseQty(zkbContract, zkbPartOrder, zkbPartOrder.getQty());
                    }else {
                        //不扣除名额，那么申请中-1，剩余+1
                        ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                        subApplyQty(zkbContract,zkbPartOrder, (0-zkbPartOrder.getQty()));
                    }
                }else{
                    ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                    subUseQty(zkbContract, zkbPartOrder, zkbPartOrder.getQty());
                }
            }else if(!ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                subUseQty(zkbContract, zkbPartOrder, zkbPartOrder.getQty());
            }
            if (ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
                for (ZkbPartOrderItem i : zkbPartOrder.getZkbPartOrderItems()) {
                    zkbPartOrderItemRepository.save(i);
                }
            }
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString())) {
            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
                ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                subReturnQty(zkbContract,zkbPartOrder, zkbPartOrder.getReturnQty());
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
                List<ZkbPartOrderReturn> zkbPartOrderReturns = zkbPartOrderReturnRepository.findByPartOrderIdAndReturnTypeIsNot(zkbPartOrder.getId(),"badReturn");
                if(zkbPartOrderReturns != null && zkbPartOrderReturns.size() > 0){
                    int v = 0;
                    ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
                    for (ZkbPartOrderReturn r : zkbPartOrderReturns){
                        v += r.getQty();
                    }
                    subReturnQty(zkbContract,zkbPartOrder,v);
                }
            }
        }else if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString())){
            ZkbContract zkbContract = zkbContractRepository.findById(zkbPartOrder.getContractId());
            subUseQty(zkbContract, zkbPartOrder, zkbPartOrder.getQty());
        }
    }

    public void orderCancelNext(ZkbPartOrder zkbPartOrder,UserAccount userAccount){
        String oldStep = zkbPartOrder.getCurrentStep();
        if(oldStep.equals(ZkbPartOrderStep.PartOrderStep.OrderPending)){
            zkbPartOrder.setUnOrderDate(new Date());
            zkbPartOrder.setUnOrderAuditer(userAccount.getName());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString());
        }else{
            zkbPartOrder.setOrderedDate(new Date());
            zkbPartOrder.setOrderedAuditer(userAccount.getName());
            zkbPartOrder.setStatus(ZkbPartOrder.PartOrderStatus.OrderedReject.toString());
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
        subApplyQty(zkbContract,zkbPartOrder,0 - zkbPartOrder.getQty());
    }

    public void subApplyQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty){
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) - qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) - qty);
            zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) + qty);
            zkbContractRepository.save(zkbContract);
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
           /* if("1".equals(zkbPartOrder.getArtificialType())){
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }else if("3".equals(zkbPartOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())){
                    zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                }else if(ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                    zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                    zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) + qty);
                    zkbContractRepository.save(zkbContract);
                }
            }*/
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setGeArtificialSupportTimes((zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes()) - qty);
                zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) - qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) + qty);
                zkbContractRepository.save(zkbContract);
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
            zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) - qty);
            zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) + qty);
            zkbContractRepository.save(zkbContract);
        }
    }

    public void subUseQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty) {
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())) {
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
                zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) + qty);
            zkbContract.setProbeQuotaApply((zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply()) - qty);
            zkbContractRepository.save(zkbContract);
        }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(zkbPartOrder.getOrderType())) {
            /*if("1".equals(zkbPartOrder.getArtificialType())){
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }else if("3".equals(zkbPartOrder.getArtificialType())){
                if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())){
                    zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                    zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                    zkbContractRepository.save(zkbContract);
                }else if(ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                    zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                    zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) - qty);
                    zkbContractRepository.save(zkbContract);
                }
            }*/
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setGeArtificialSupportTimesUse((zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse()) + qty);
                zkbContract.setGeArtificialSupportTimesApply((zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) + qty);
                zkbContract.setSpareArtificailApply((zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply()) - qty);
                zkbContractRepository.save(zkbContract);
            }
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
            zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) + qty);
            zkbContract.setSpareQuotaApply((zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply()) - qty);
            zkbContractRepository.save(zkbContract);
        }
    }

    public void subReturnQty(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder,int qty) {
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())) {
            if(ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType())){
                zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) - qty);
                zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + qty);
                zkbContractRepository.save(zkbContract);
            }else{
                zkbContract.setSpareArtificailUse((zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse()) - qty);
                zkbContract.setSpareArtificail((zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail()) + qty);
                zkbContractRepository.save(zkbContract);
            }
        }else if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())) {
            zkbContract.setProbeQuotaUse((zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse()) - qty);
            zkbContract.setProbeQuota((zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota()) + qty);
            zkbContractRepository.save(zkbContract);
        }else if(ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())){
            zkbContract.setSpareQuotaUse((zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse()) - qty);
            zkbContract.setSpareQuota((zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota()) + qty);
            zkbContractRepository.save(zkbContract);
        }
    }


    /**
     * 发送邮件
     */
    public void sendEmail(ZkbPartOrder zkbPartOrder){
        try {
            HashMap<String, String> emailWrapperMap = new HashMap<>();
            emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
            emailWrapperMap.put("systemId", zkbPartOrder.getId());
            emailWrapperMap.put("url", "?id=" + zkbPartOrder.getId());
            emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType", zkbPartOrder.getOrderType()));

            String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_EMAIL);
            String token = UserContextService.getAccessToken();
            HttpHeaders header = createHttpHeaders(token);
            HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
            ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(ZkbPartOrder zkbPartOrder) {
        //如果是备件申请，并且在技术支持审批and放射类设备则不需要发短信
        if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())){
            if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrder.getCurrentStep())){
                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
                if(assetInfo != null && assetInfo.getAssetGroup() != null && assetInfo.getAssetGroup() == 1){
                    return;
                }
            }
        }
        if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrder.getCurrentStep()) || ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrder.getCurrentStep())) {
            HashMap<String, String> emailWrapperMap = null;
            try {
                emailWrapperMap = new HashMap<>();
                emailWrapperMap.put("configType", zkbPartOrder.getCurrentStep());
                emailWrapperMap.put("systemId", zkbPartOrder.getId());
                emailWrapperMap.put("url", "?id=" + zkbPartOrder.getId());
                emailWrapperMap.put("subject", WebUtil.getFieldValueMessage("zkbPartOrderType",zkbPartOrder.getOrderType()));
                String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_MESSAGE);
                String token = UserContextService.getAccessToken();
                HttpHeaders header = createHttpHeaders(token);
                HttpEntity<Map> requestEntity = new HttpEntity<>(emailWrapperMap, header);
                ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 发送微信消息
     */
    public void sendWeChatMessages(ZkbPartOrder zkbPartOrder){
        //if(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())) {
            if (ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(zkbPartOrder.getStatus()) || ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(zkbPartOrder.getStatus()) ||
                    ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(zkbPartOrder.getStatus()) || ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(zkbPartOrder.getStatus())) {
                try {
                    String url = getGatewayURL(GatewayURL.PART_ORDER_SEND_WECHAT_MESSAGE).concat("?id=").concat(zkbPartOrder.getId());
                    String token = UserContextService.getAccessToken();
                    HttpHeaders header = createHttpHeaders(token);
                    HttpEntity<Map> requestEntity = new HttpEntity<>(header);
                    ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.POST, requestEntity, LinkedHashMap.class);
                } catch (RestClientException e) {
                    e.printStackTrace();
                }
            }
        //}
    }

    public List<ZkbPartOrderItem> queryItemBuPartOrderId(String id){
        List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrderItemRepository.findByPartOrderId(id);
        return zkbPartOrderItems;
    }

    public List<ZkbPartOrderCat> queryPartOrderCat(){
        return zkbPartOrderCatRepository.find();
        //return zkbPartOrderCatRepository.findByValueTypeIsNot(ZkbPartOrderCat.ValueType.HighValue.toString());
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

