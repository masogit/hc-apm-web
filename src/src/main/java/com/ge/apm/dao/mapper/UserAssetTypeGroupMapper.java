package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserAssetTypeGroupMapper {
	
	@Select("select asset_group_uid from user_asset_group where user_uid= #{userUid}")
	List<String> findAssetGroupByUser(String userUid);

	@Select("select sag.encoded\n" +
			"from user_asset_group uag\n" +
			"join asset_type_group sag on uag.auth_group_uid = sag.uid\n" +
			"where uag.user_uid = #{userUid}")
	List<String> findAuthGroupEncodedByUser(@Param("userUid")String userUid);

	@Delete("delete from user_asset_group where user_uid = #{userUid} and auth_group = #{authGroup}")
	int removeAuthGroup(@Param("userUid") String userUid, @Param("authGroup") String authGroup);

	@Delete("delete from user_asset_group where user_uid = #{userUid}")
	int removeAuthGroupByUser(@Param("userUid") String userUid);
	
}
