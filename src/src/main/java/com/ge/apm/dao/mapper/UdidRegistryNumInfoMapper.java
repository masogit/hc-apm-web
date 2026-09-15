package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.UdidRegistryNumInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author 550021593
 * @description 针对表【udid_registry_num_info】的数据库操作Mapper
 * @createDate 2026-09-15 14:17:51
 * @Entity com.ge.apm.domain.UdidRegistryNumInfo
 */
public interface UdidRegistryNumInfoMapper extends BaseMapper<UdidRegistryNumInfo> {

    UdidRegistryNumInfo selectByRegistryNum(@Param("registryNum") String registryNum);
}
