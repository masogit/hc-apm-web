package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Nick Liu
 * @since 2018 10.1 release
 */

public interface SharedAssetMapper extends BaseMapper<AssetInfo> {

    /**
     *
     * @param wrapper
     * @return
     */
    @Select("getTotalRowCount")
    Integer getTotalRowCount(@Param("ew") Wrapper<AssetInfo> wrapper);

    /**
     *
     * @param page
     * @param wrapper
     * @return
     */
    @Select("loadSharedAssets")
    List<AssetInfo> loadSharedAssets(Pagination page, @Param("ew") Wrapper<AssetInfo> wrapper);

    /**
     *
     * @param wrapper
     * @return
     */
    @Select("SELECT ai.id,ai.uid,ai.depart_num,ai.name,ai.asset_dept_uid,ai.clinical_dept_uid," +
            "sat.swap_name as swap_name,sat.unit_price as unit_price,sat.charging_type as charging_type " +
            "FROM asset_info ai INNER JOIN shared_asset_type sat ON ai.shared_asset_type=sat.id " +
            "WHERE 1 = 1 ${ew.sqlSegment}")
    @Results({
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "uid", property = "uid"),
        @Result(column = "name", property = "name"),
        @Result(column = "depart_num", property = "departNum"),
        @Result(column = "asset_dept_uid", property = "assetDeptUID"),
        @Result(column = "swap_name", property = "sharedAssetType.swapName"),
        @Result(column = "unit_price", property = "sharedAssetType.unitPrice"),
        @Result(column = "charging_type", property = "sharedAssetType.chargingType")
    })
    List<AssetInfo> findSharedAsset(@Param("ew") Wrapper<AssetInfo> wrapper);

    /**
     *
     * @param wrapper
     * @return
     */
    @Select("SELECT sard.voucher_num,sard.charging_begin_time, sard.charging_end_time," +
            "sard.unit_price,sard.charging_type,sard.starting_duration,sard.starting_price,sard.will_be_delivered," +
            "sar.type " +
            "FROM shared_asset_req_detail sard INNER JOIN shared_asset_request sar on sard.borrow_id=sar.id " +
            "WHERE (sard.charging_end_time is null or sard.charging_end_time > now()) ${ew.sqlSegment}")
    @Results({
        @Result(column = "voucher_num", property = "voucherNum"),
        @Result(column = "charging_begin_time", property = "chargingBeginTime"),
        @Result(column = "charging_end_time", property = "chargingEndTime"),
        @Result(column = "unit_price", property = "unitPrice"),
        @Result(column = "charging_type", property = "chargingType"),
        @Result(column = "starting_duration", property = "startingDuration"),
        @Result(column = "starting_price", property = "startingPrice"),
        @Result(column = "will_be_delivered", property = "willBeDelivered"),
        @Result(column = "type", property = "leaseRequest.type")
    })
    List<SharedAssetReqDetail> findReqDetailByAsset(@Param("ew") Wrapper<AssetInfo> wrapper);

}
