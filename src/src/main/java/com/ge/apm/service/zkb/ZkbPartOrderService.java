package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@Service
public class ZkbPartOrderService {

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    @Autowired
    private ZkbPartOrderItemRepository zkbPartOrderItemRepository;

    @Autowired
    ZkbContractRepository zkbContractRepository;

    @Autowired
    private V2_WorkOrderRepository v2_workOrderRepository;

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
        zkbPartOrderRepository.delete(zkbPartOrder);
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
            v2_listHashMap.add(m);
        }
        return v2_listHashMap;
    }

    public List<ZkbContract> queryContractByAsset(String tenUId,String assetId){
        //assetId="8842631547e94e8688122da85ae19ce8";
        List<ZkbContract> zkbContracts = zkbContractRepository.findByAsset(tenUId,new Date(),assetId);
        System.out.println(zkbContracts);
        return zkbContracts;
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(String workOrderId, String assetId,ZkbContract zkbContract,ZkbPartOrder zkbPartOrder,UserAccount userAccount,String tenantUid,String srNum){
        zkbPartOrder.setWorkOrderId(workOrderId);
        zkbPartOrder.setAssetId(assetId);
        zkbPartOrder.setContractId(zkbContract.getId());
        zkbPartOrder.setContractNo(zkbContract.getContractNo());
        zkbPartOrder.setTenantUID(tenantUid);
        zkbPartOrder.setStatus("Pending");
        zkbPartOrder.setCurrentStep("TechnicalPending");
        zkbPartOrder.setCreatedBy(userAccount.getLoginName());
        zkbPartOrder.setCreatedDate(new Date());
        zkbPartOrder.setSrNum(srNum);
         Object o= zkbPartOrderRepository.getMaxIndex() ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String partOrderNo = "PO"+simpleDateFormat.format(new Date())+String.format("%06d",index);
        zkbPartOrder.setPartOrderNo(partOrderNo);
        zkbPartOrder.setPartOrderIndex(index);

        ZkbPartOrderStep zkbPartOrderStep = new ZkbPartOrderStep();
        zkbPartOrderStep.setStepKey("TechnicalPending");
        zkbPartOrderStep.setStepName("待技术支持审批");
        zkbPartOrderStep.setStartTime(new Date());
        zkbPartOrderStep.setOwnerId(userAccount.getUid());
        zkbPartOrderStep.setOwnerName(userAccount.getName());
        zkbPartOrderStep.setTenantUID(zkbPartOrder.getTenantUID());
        Set<ZkbPartOrderStep> zkbPartOrderStepSet = new HashSet<>();
        zkbPartOrderStepSet.add(zkbPartOrderStep);
        zkbPartOrder.setZkbPartOrderStepSet(zkbPartOrderStepSet);
        zkbPartOrderRepository.save(zkbPartOrder);
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
                m.put("id",v.getId());
                m.put("assetId",v.getAssetInfo().getUid());
                map.put("v2", m);
            }
        }

        map.put("zkbPartOrder",zkbPartOrder);
        return map;
    }
    public ZkbPartOrderRepository getZkbPartOrderRepository() {
        return zkbPartOrderRepository;
    }

    public void setZkbPartOrderRepository(ZkbPartOrderRepository zkbPartOrderRepository) {
        this.zkbPartOrderRepository = zkbPartOrderRepository;
    }
}
