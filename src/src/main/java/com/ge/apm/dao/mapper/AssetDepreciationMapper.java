package com.ge.apm.dao.mapper;

import com.ge.apm.domain.dto.DepreciationResultData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface AssetDepreciationMapper {
	@Select("select sum(deprecate_amount) from asset_depreciation where asset_id=#{assetId} "
			+ "and deprecate_date=date(date_trunc('month',now())) group by deprecate_date")
	public Double fetchAssetDepreciationByAssetId(Integer assetId);



	@Select("select  COALESCE(sum(ad.deprecate_amount),0) as depreciationEquipment ,to_char(ad.deprecate_date,'YYYY-MM') as  date ,asset_id,assetUid from  asset_depreciation ad  where  ad.deprecate_amount >0 and ad.deprecate_amount !='NaN' and ad.deprecate_date between #{startTime} and #{endTime} and ad.asset_id =#{assetUid}  group by to_char(ad.deprecate_date,'YYYY-MM')  ,asset_uid   ")
	List<DepreciationResultData> findAssetDepreciationGroupMonth(@Param("assetUid")Integer assetUid, @Param("startTime") LocalDate startTime, @Param("endTime")LocalDate endTime);



	@Select({
			"<script>" +
					" select  COALESCE(sum(ad.deprecate_amount),0) as depreciationEquipment ,to_char(ad.deprecate_date,'YYYY-MM') as  date ,ad.asset_id as assetId from  asset_depreciation ad  where  ad.deprecate_amount >0 and ad.deprecate_amount !='NaN' and ad.deprecate_date between #{startTime} and #{endTime} " +
					"and ad.asset_id in " +
					"<foreach item = 'item' index = 'index' collection = 'assetIds' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"group by to_char(ad.deprecate_date,'YYYY-MM')  ,ad.asset_id "+
					"</script>"})
	List<DepreciationResultData> findAssetDepreciationGroupMonthByAssetIds(@Param("assetIds") List<Integer> assetIds, @Param("startTime") LocalDate startTime, @Param("endTime")LocalDate endTime);
}