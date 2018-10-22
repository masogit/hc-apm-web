package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrder_Detail;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

import webapp.framework.dao.GenericRepositoryUUID;

public interface V2_WorkOrderDetailRepository extends GenericRepositoryUUID<V2_WorkOrder_Detail> {

    @Query("select wod from V2_WorkOrder_Detail wod  where wod.contractNum=?1 and wod.v2WorkOrder.tenantUID=?2")
    List<V2_WorkOrder_Detail> findByContractNum(String contractNum, String tenant_uid);
   
}
