package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AssetTagsMapper {

	@Select("select distinct tags.tag from asset_tags tags where tags.tenant_uid = #{tenantUid}")
	public List<String> getTagsByTenant(String tenantUid);

	@Select("select distinct tags.tag from asset_tags tags where tags.asset_uid = #{assetUid}")
	public List<String> getTagsByAsset(String assetUid);

	@Delete("delete from asset_tags tags where tags.asset_uid = #{assetUid} and tags.tag = #{tag}")
	public int removeTag(@Param("assetUid")String assetUid, @Param("tag")String tag);
}
