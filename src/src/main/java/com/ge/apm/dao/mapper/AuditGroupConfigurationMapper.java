package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.AuditGroupConfiguration;
import org.apache.ibatis.annotations.Select;

/**
 * @author 503310146
 */
public interface AuditGroupConfigurationMapper extends BaseMapper<AuditGroupConfiguration> {

    @Select("select count(t.userUuids) from (select json_array_elements(group_member_item::json)::json ->> 'userUids' as userUuids from audit_group_configuration) t where position(#{uid} in t.userUuids) > 0")
    Integer countGroupMemberItemIncludeUserUid(String uid);

}
