package com.ge.apm.service.asset;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class AssetImportDoubleConverter implements ApmEasyExcelConvert, Converter<Double> {

    /**
     * 删除标识
     */

    @Override
    public Class<?> supportJavaTypeKey() {
        return Double.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Double convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {

        return 0.0;
    }

    @Override
    public Double convertToJavaData(ReadConverterContext<?> context) throws Exception {
        BigDecimal numberValue = context.getReadCellData().getNumberValue();
        if (Objects.nonNull(numberValue)) {
            return numberValue.doubleValue();
        } else {
            String stringValue = context.getReadCellData().getStringValue();
            if ("#".equals(stringValue)) {
                deleteMarkHandel(context);
                return null;
            }
            try {
                return Double.parseDouble(stringValue);
            } catch (Exception exception) {
                dataFormatHandel(context, "应为数字类型");
            }
            return null;
        }
    }
}
