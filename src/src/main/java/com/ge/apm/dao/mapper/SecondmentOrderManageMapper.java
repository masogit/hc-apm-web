package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.dto.SecondmentOrderDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SecondmentOrderManageMapper extends BaseMapper<SecondmentOrderDto> {

    List<SecondmentOrderDto> loadSecondmentOrder(Pagination page, @Param("ew") Wrapper<SecondmentOrderDto> wrapper);

    int rewriteSelectCount(@Param("ew") Wrapper<SecondmentOrderDto> wrapper);

}
