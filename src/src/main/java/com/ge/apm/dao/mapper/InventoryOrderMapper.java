package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrder;
import com.ge.apm.domain.InventoryOrderDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface InventoryOrderMapper extends BaseMapper<InventoryOrder> {
    List<InventoryOrderDetail> findJoinAssetList(Map<String, Object> param);

    Long findJoinAssetCount(Map<String, Object> param);

    List<Map<String, Object>> findNoneLedgerOrder(Map<String, Object> param);

    List<Map<String, Object>> findIssueOrder(Map<String, Object> param);

    List<Map<String, Object>> findDetailOrder(Map<String, Object> param);

    List<AssetInfo> findNoneOrderList(Map<String, Object> param);

    Long findNoneOrderCount(Map<String, Object> param);


    @Select(" select d.*,io.plan_start_time ,io.plan_end_time " +
            " from inventory_order_detail d inner join inventory_order io on io.id = d.inventory_order_id\n" +
            " left join inventory_issue ii on d.inventory_order_id = ii.inventory_order_id and d.asset_id = ii.asset_id " +
            " where 1=1 ${ew.sqlSegment}")
    List<InventoryOrderDetail> selectInventoryDetailByCondition(Pagination pagination, @Param("ew") Wrapper wrapper);

}
