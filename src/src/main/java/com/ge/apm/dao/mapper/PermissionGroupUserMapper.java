package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.PermissionGroupUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 503310146
 */
public interface PermissionGroupUserMapper extends BaseMapper<PermissionGroupUser> {

    @Select("SELECT pg.* FROM permission_group pg LEFT JOIN permission_group_user pgu ON pg.uid = pgu.permission_group_uid WHERE pgu.user_uid = #{uid}")
    List<String> getPermissionGroupsByUserUid(String uid);
}
