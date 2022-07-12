package com.ge.apm.dao;

import com.ge.apm.domain.InspectionOrder;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface InspectionOrderRepository extends GenericRepository<InspectionOrder> {

//     @Query("select  distinct o from InspectionOrder o,InspectionOrderDetail d where o.id=d.orderId and d.assetId=?1 and o.orderType=?2 and o.startTime>current_date group by o.id order by o.startTime ")
//     @Query("select  distinct o from InspectionOrder o,InspectionOrderDetail d where o.id=d.orderId and d.assetId=?1 and o.orderType=?2 and o.isFinished=false  group by o.id order by o.startTime ")
    @Query(value = "select * from inspection_order o where exists (select * from inspection_order_detail d where d.order_id=o.id and d.asset_id=?1) and o.order_type =?2 and !o.is_finished order by o.start_time ", nativeQuery = true)
     public List<InspectionOrder> getRecentlyInspectionOrder(Integer assetId,Integer orderType);




}
