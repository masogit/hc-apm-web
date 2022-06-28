package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.mapper.simpleparts.SimplePartsReportMapper;
import com.ge.apm.domain.simpleparts.SimplePartsReportVo;
import com.ge.apm.service.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author by lyj 503277717
 * @date 2022/5/19.
 */

@Service
public class SimplePartsReportService {

    @Autowired
    private SimplePartsReportMapper simplePartsReportMapper;




    public List<SimplePartsReportVo> querySimplePartsReportList(Date startTime, Date endTime,String tenantUID) {
        String startDate = null;
        String endDate = null;
        if(startTime != null){
            startDate = TimeUtils.getStrDate(startTime, null) + " 00:00:00";
        }
        if(endTime != null){
            endDate = TimeUtils.getStrDate(endTime, null) + " 23:59:59";
        }
        return simplePartsReportMapper.querySimplePartsReportList(startDate,endDate,tenantUID);
    }
}
