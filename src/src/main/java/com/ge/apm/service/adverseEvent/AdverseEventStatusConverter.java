package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class AdverseEventStatusConverter implements Converter<Integer> {

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(value == null){
            return new WriteCellData<>("");
        }
        if(value == 1){
            return new WriteCellData<>("暂存");
        }else if(value == 2){
            return new WriteCellData<>("待审核");
        }else if(value == 3){
            return new WriteCellData<>("审核中");
        }else if(value == 4){
            return new WriteCellData<>("已退回");
        }else if(value == 5){
            return new WriteCellData<>("已作废");
        }else if(value == 6){
            return new WriteCellData<>("审核完成");
        }else if(value == 7){
            return new WriteCellData<>("同意上报");
        }else if(value == 8){
            return new WriteCellData<>("上报完成");
        }else if(value == 9){
            return new WriteCellData<>("买方填写");
        }
        return new WriteCellData<>("");
    }
}
