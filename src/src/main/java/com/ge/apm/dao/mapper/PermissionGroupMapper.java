package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.PermissionGroup;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 503310146
 */
public interface PermissionGroupMapper extends BaseMapper<PermissionGroup> {

    @Select("SELECT * FROM permission_group WHERE tenant_uid = #{tenantUid}")
    List<PermissionGroup> getBySiteUid(String tenantUid);
}
