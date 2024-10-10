package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.EasyExcel;
import com.ge.apm.domain.AdverseEvents;
import com.ge.apm.domain.dto.AdverseEventsExportDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdverseEventsService {


    public InputStream exportExcel(List<AdverseEvents> adverseEventsList){
        List<AdverseEventsExportDto> adverseEventsExportDtos = new ArrayList<>();
        adverseEventsList.forEach( adverseEvent ->{
            AdverseEventsExportDto adverseEventsExportDto = new AdverseEventsExportDto();
            BeanUtils.copyProperties(adverseEvent, adverseEventsExportDto);
            adverseEventsExportDtos.add(adverseEventsExportDto);
        });
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, AdverseEventsExportDto.class)
                .sheet("Sheet1")
                .registerWriteHandler(new AdverseEventCellWriteWidthConfig())
                .doWrite(adverseEventsExportDtos);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
