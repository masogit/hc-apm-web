package com.ge.apm.dao.mapper;

import com.ge.apm.domain.dto.AssetCostDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HisEquipmentDataResultMapper {




    @Select({
            "<script>" +
                    "select  sum(total_amount) as amount ,asset_uid as assetUid  from  his_equipment_data_result   where exec_time between #{startDate} and #{endDate} and asset_uid  in " +
                    "<foreach item = 'item' index = 'index' collection = 'assetUids' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>"+
                    "group by asset_uid"+
                    "</script>"})
    List<AssetCostDTO> findByAssetUidAndDateInSunAmount(@Param("assetUids")  List<String> assetUids, @Param("startDate") Date startDate, @Param("endDate")  Date endDate);




}
