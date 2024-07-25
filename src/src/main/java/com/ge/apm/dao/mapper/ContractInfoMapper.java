package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ContractInfoMapper {

    @Select({
        "<script>" +
                "select ci.id, cii.asset_uid assetUid, ci.start_date startDate,ci.end_date endDate from contract_info ci join contract_info_item cii on cii.contract_id = ci.id where cii.item_type = 'ExistAsset' and ci.start_date is not null and ci.end_date is not null and cii.asset_uid in" +
                "<foreach item = 'item' index = 'index' collection = 'assetUidList' open='(' separator=',' close=')'>" +
                "#{item}" +
                "</foreach>"+
                "</script>"})
    List<Map<String, Object>> findDateByAssetUidList(@Param("assetUidList") Collection<String> assetUidList);
}
