package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.provider.AssetInfoProvider;
import com.ge.apm.domain.AssetCostStatistics;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.Supplier;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Date;
import java.util.List;

public interface AssetInfoMapper extends BaseMapper<AssetInfo> {

	@SelectProvider(type = AssetInfoProvider.class,method="fetchAssetInfo")
	public List<AssetCostStatistics> fetchAssetInfo();

	@SelectProvider(type = AssetInfoProvider.class,method="fetchAssetCostStatisticsByAssetId")
	public AssetCostStatistics fetchAssetCostStatisticsByAssetId(@Param("assetId") Integer assetId, @Param("day") Date day);

	@SelectProvider(type = AssetInfoProvider.class,method="fetchAssetInfoByDay")
	public List<AssetCostStatistics> fetchAssetInfoByDay(Date day);

	@Select("select * from asset_info where id= #{id}")
	public AssetInfo fetchAssetInfoById(Integer id);

	@Select({
			"<script>" +
				"select  asset.id,asset.depart_num,asset.qr_code,asset.name,asset.uid from  asset_info asset  where asset.id in " +
				"<foreach item = 'item' index = 'index' collection = 'parendAssetIds' open='(' separator=',' close=')'>" +
				"#{item}" +
				"</foreach>"+
			"</script>"})
	public List<AssetInfo> getAssetMap(@Param("parendAssetIds")List<Integer> parendAssetIds);

	@Select({
			"<script>" +
					"select  id,tel  from  supplier spl  where spl.id in " +
					"<foreach item = 'item' index = 'index' collection = 'suplierIdList' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"</script>"})
	public List<Supplier> getSupliersBySuplierIds(@Param("suplierIdList")List<Integer> suplierIdList);

	@Select({
			"<script>" +
					"select  *  from  supplier spl  where spl.tenant_uid = #{tenantUid} and spl.name in " +
					"<foreach item = 'item' index = 'index' collection = 'assetSuppliers' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"</script>"})
	public List<Supplier> getSupliersBySuplierNamesAndTenantUID(@Param("assetSuppliers")List<String> assetSuppliers, @Param("tenantUid")String tenantUid);


	/**
	 * 根据合同ID和SYSTEM ID获取信息
	 * @param systemId system ID
	 * @param contractId 合同ID
	 * @return
	 */
	@SelectProvider(type = AssetInfoProvider.class,method="fetchByAssetInfoBySystemId")
	List<AssetInfo> fetchByAssetInfoBySystemId(@Param("systemId")String systemId, @Param("contractId")String contractId);


	/**
	 * 根据合同ID和SYSTEM ID获取信息
	 * @param orgList orgList ID
	 * @return
	 */
	@SelectProvider(type = AssetInfoProvider.class,method="getMvsWorkOrders")
	List<String> getMvsWorkOrders(@Param("orgList")List<String> orgList,@Param("tenantUid")String tenantUid);

	/**
	 * 根据合同ID和SYSTEM ID获取信息
	 * @param orgList orgList ID
	 * @return
	 */
	@SelectProvider(type = AssetInfoProvider.class,method="getMvsPmOrders")
	List<String> getMvsPmOrders(@Param("orgList")List<Integer> orgList,@Param("tenantUid")String tenantUid);

	@SelectProvider(type = AssetInfoProvider.class,method="countAssetWithQRcode")
	Integer countAssetWithQRcode(@Param("uidList")List<String> uidList,@Param("tenantUid")String tenantUid);

	List<AssetInfo> selectMeasureResult(Pagination pagination, @Param("ew") Wrapper<AssetInfo> wrapper);

	List<AssetInfo> selectMeasureResult(@Param("ew") Wrapper<AssetInfo> wrapper);

    List<AssetInfo> findAssetByMaintainConfigParentId(@Param("ew") Wrapper<AssetInfo> wrapper);

	List<AssetInfo> findAssetByMaintainConfigJNParentId(@Param("ew") Wrapper<AssetInfo> wrapper);
}
