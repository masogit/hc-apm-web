package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.Data;


@Data
public class AdverseEventApprovalResultConverter implements Converter<Integer> {

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(value == null){
            return new WriteCellData<>("");
        }
        if(value == 1){
            return new WriteCellData<>("同意上报");
        }else if(value == 2){
            return new WriteCellData<>("不同意上报");
        }
        return new WriteCellData<>("");
    }

}
