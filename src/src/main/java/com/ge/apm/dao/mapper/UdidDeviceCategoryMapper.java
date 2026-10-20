package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.UdidDeviceCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 550021593
 * @description 针对表【udid_device_category】的数据库操作Mapper
 * @createDate 2026-10-19 11:35:21
 * @Entity com.ge.apm.domain.UdidDeviceCategory
 */
public interface UdidDeviceCategoryMapper extends BaseMapper<UdidDeviceCategory> {

    List<UdidDeviceCategory> fuzzySearch(@Param("registryNum") String registryNum,
                                         @Param("category") String category);
}
