package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class AdverseEventHarmDegreeConverter  implements Converter<Integer> {

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(value == null){
            return new WriteCellData<>("");
        }
        if(value == 1){
            return new WriteCellData<>("死亡");
        }else if(value == 2){
            return new WriteCellData<>("严重伤害");
        }else if(value == 3){
            return new WriteCellData<>("其他");
        }
        return new WriteCellData<>("");
    }

}
