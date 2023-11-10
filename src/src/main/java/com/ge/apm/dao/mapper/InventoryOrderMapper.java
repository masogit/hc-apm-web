package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.InventoryOrder;

import java.util.List;
import java.util.Map;

public interface InventoryOrderMapper extends BaseMapper<InventoryOrder> {
    List<Map<String, Object>> findJoinAssetList(Map<String, Object> param);

    Long findJoinAssetCount(Map<String, Object> param);

    List<Map<String, Object>> findNoneLedgerOrder(Map<String, Object> param);

    List<Map<String, Object>> findIssueOrder(Map<String, Object> param);

    List<Map<String, Object>> findDetailOrder(Map<String, Object> param);

    List<AssetInfo> findNoneOrderList(Map<String, Object> param);

    Long findNoneOrderCount(Map<String, Object> param);

}
