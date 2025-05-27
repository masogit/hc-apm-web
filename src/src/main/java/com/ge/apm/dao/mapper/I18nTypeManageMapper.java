package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.I18nTypeManage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface I18nTypeManageMapper extends BaseMapper<I18nTypeManage> {


    List<I18nTypeManage> loadSharedAssets(Pagination page, @Param("ew") Wrapper<I18nTypeManage> wrapper,@Param("tenantUid")String tenantUid);
    int getTotalRowCount(@Param("ew") Wrapper<I18nTypeManage> wrapper,@Param("tenantUid")String tenantUid);
}
