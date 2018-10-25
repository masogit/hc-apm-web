package com.ge.apm.dao;

import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

import webapp.framework.dao.GenericRepositoryUUID;

public interface V2_WorkOrderDetailRepository extends GenericRepositoryUUID<V2_WorkOrder_Detail> {

//    @Query("select wod from V2_WorkOrder_Detail wod  where wod.contractNum=?1 and wod.v2WorkOrder.tenantUID=?2")
//    List<V2_WorkOrder_Detail> findByContractNum(String contractNum, String tenant_uid);
//    
//    @Query(value ="select wod.* from V2_WorkOrder_Detail wod ,V2_Work_Order wo  where wod.wo_id=wo.id and wo.asset_Id =?1 and  wod.contract_num=?3 and wo.tenant_UID=?4"
//            + "union all select wod.* from V2_WorkOrder_Detail wod ,v2_pm_order po  where wod.wo_id=po.id and po.asset_uid =?2 and  wod.contract_num=?3 and po.tenant_UID=?4",nativeQuery = true)
//    List<V2_WorkOrder_Detail> findByAssetUidAndContractNum(Integer assetId,String assetUid, String contractNum, String tenant_uid);
//    
    @Query("select wo from V2_WorkOrder_Detail wod , V2_WorkOrder wo where wo.id=wod.woId and wod.contractNum=?1 and wo.tenantUID=?2")
    List<V2_WorkOrder> findWorkOrderByContractNum(String contractNum, String tenant_uid);
    
    @Query("select po from V2_WorkOrder_Detail wod , V2_PmOrder po where po.id=wod.woId and wod.contractNum=?1 and po.tenantUID=?2")
    List<V2_PmOrder> findPmOrderByContractNum(String contractNum, String tenant_uid);
    
}
