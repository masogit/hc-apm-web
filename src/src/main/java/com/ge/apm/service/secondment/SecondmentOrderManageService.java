package com.ge.apm.service.secondment;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.SecondmentOrderManageMapper;
import com.ge.apm.domain.dto.SecondmentOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecondmentOrderManageService {

    @Autowired
    private SecondmentOrderManageMapper manageMapper;
    public Page<SecondmentOrderDto> loadSecondmentOrderList(PageRequest pageRequest, Wrapper<SecondmentOrderDto> wrapper){
        Pagination page = new Pagination(pageRequest.getPageNumber(),pageRequest.getPageSize());
        Integer total= manageMapper.rewriteSelectCount(wrapper);
        List<SecondmentOrderDto> secondmentOrderDtos = manageMapper.loadSecondmentOrder(page, wrapper);
        return new PageImpl<SecondmentOrderDto>(secondmentOrderDtos,pageRequest,total);
    }

}
