package com.ge.apm.service.wo;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.OpsSrReportMapper;
import com.ge.apm.domain.OpsSrReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpsSrReportService {
    @Autowired
    private OpsSrReportMapper mapper;

    public List<OpsSrReport> loadData(Pagination pageRequest, Wrapper<OpsSrReport> wrapper) {
        return mapper.findSRWithoutReports(pageRequest, wrapper);
    }

    public Integer selectCount(Wrapper<OpsSrReport> wrapper) {
        return mapper.count(wrapper);
    }

}
