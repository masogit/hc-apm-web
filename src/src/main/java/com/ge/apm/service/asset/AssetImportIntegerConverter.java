package com.ge.apm.service.asset;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class AssetImportIntegerConverter implements Converter<Integer> {


    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {


        return 0;
    }

    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) throws Exception {
        BigDecimal numberValue = context.getReadCellData().getNumberValue();
        if (Objects.nonNull(numberValue)) {
            return numberValue.intValue();
        } else {
            String stringValue = context.getReadCellData().getStringValue();
            return Integer.parseInt(stringValue);
        }
    }
}
