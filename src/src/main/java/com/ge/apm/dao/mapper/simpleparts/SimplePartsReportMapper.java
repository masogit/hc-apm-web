package com.ge.apm.dao.mapper.simpleparts;

import com.ge.apm.dao.mapper.provider.SimplePartsReportProvider;
import com.ge.apm.domain.simpleparts.SimplePartsReportVo;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author by lyj 503277717
 * @date 2022/5/19.
 */
public interface SimplePartsReportMapper {


    @SelectProvider(type = SimplePartsReportProvider.class,method="querySimplePartsReportList")
    List<SimplePartsReportVo> querySimplePartsReportList(String startDate, String endDate);


}
