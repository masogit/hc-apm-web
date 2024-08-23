package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class AdverseEventDateConverter implements Converter<Date> {

    // 需要的日期格式(我们需要的日期样式)
    private static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";

    @Override
    public Class<?> supportJavaTypeKey() {
        return Converter.super.supportJavaTypeKey();
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return Converter.super.supportExcelTypeKey();
    }

    /**
     * @param value               库中的数据，即目标转换的字段
     * @param contentProperty
     * @param globalConfiguration
     * @return
     * @throws Exception
     */
    @Override
    public WriteCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_YYYY_MM_DD);
        String dateValue = sdf.format(value);
        return new WriteCellData<>(dateValue);
    }
}
