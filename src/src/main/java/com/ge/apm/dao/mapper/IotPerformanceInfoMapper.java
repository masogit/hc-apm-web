package com.ge.apm.dao.mapper;

import com.ge.apm.domain.dto.AssetCostDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IotPerformanceInfoMapper {


    @Select({
            "<script>" +
                    "select  sum(check_number_people) as checkNumberPeople  ,asset_uid as assetUid  from  iot_performance_info   where date between #{startDate} and #{endDate} and asset_uid  in " +
                    "<foreach item = 'item' index = 'index' collection = 'assetUids' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>"+
                    "group by asset_uid"+
                    "</script>"})
    List<AssetCostDTO> findByAssetUidAndDateInSunAmount(@Param("assetUids")  List<String> assetUids, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Select({
            "<script>" +
                    "select  sum(check_number_people) as checkNumberPeople  ,qr_code as qrCode  from  iot_performance_info   where date between #{startDate} and #{endDate} and qr_code  in " +
                    "<foreach item = 'item' index = 'index' collection = 'qrCodes' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>"+
                    "group by qr_code"+
                    "</script>"})
    List<AssetCostDTO> findByQrCodeAndDateInSunAmount(@Param("qrCodes")  List<String> qrCodes, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
