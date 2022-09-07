package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.MultiLevelApprovalConfig;
import org.apache.ibatis.annotations.Select;

/**
 * @author 503310146
 */
public interface MultiLevelApprovalConfigMapper extends BaseMapper<MultiLevelApprovalConfig> {

    @Select("select count(t.userUuids) from (select json_array_elements(approval_item::json)::json ->> 'userUids' as userUuids from multi_level_approval_config) t where position(#{uid} in t.userUuids) > 0")
    Integer countApprovalItemIncludeUserUid(String uid);

}
