package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;


@Data
public class AdverseEventDevicesClassConverter implements Converter<Integer> {

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(value == null){
            return new WriteCellData<>("");
        }
        if(value == 1){
            return new WriteCellData<>("设备");
        }else if(value == 2){
            return new WriteCellData<>("耗材");
        }
        return new WriteCellData<>("");
    }

}
