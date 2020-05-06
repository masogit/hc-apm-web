package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserTagsMapper {

	@Select("select distinct tags.tag from user_tags tags where tags.tenant_uid = #{tenantUid}")
	public List<String> getTagsByTenant(String tenantUid);

	@Select("select distinct tags.tag from user_tags tags where tags.user_uid = #{userUid}")
	public List<String> getTagsByUser(String userUid);

	@Delete("delete from user_tags tags where tags.user_uid = #{userUid} and tags.tag = #{tag}")
	public int removeTag(@Param("userUid") String userUid, @Param("tag") String tag);
}
