package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.UdidDeviceCube;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 550021593
 * @description 针对表【udid_device_cube】的数据库操作Mapper
 * @createDate 2026-09-01 17:36:14
 * @Entity com.ge.apm.domain.UdidDeviceCube
 */
public interface UdidDeviceCubeMapper extends BaseMapper<UdidDeviceCube> {

    List<UdidDeviceCube> fuzzySearch(@Param("registryNum") String registryNum,
                                     @Param("registrantName") String registrantName,
                                     @Param("deviceName") String deviceName,
                                     @Param("devicePattern") String devicePattern);
}
