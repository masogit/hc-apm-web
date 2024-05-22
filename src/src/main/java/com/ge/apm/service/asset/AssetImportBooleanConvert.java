package com.ge.apm.service.asset;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class AssetImportBooleanConvert implements ApmEasyExcelConvert, Converter<Boolean> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Boolean convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {


        return Boolean.TRUE;
    }

    @Override
    public Boolean convertToJavaData(ReadConverterContext<?> context) {
        String value = context.getReadCellData().getStringValue();
        if ("是".equals(value) || "Yes".equals(value)) {
            return Boolean.TRUE;
        } else if ("否".equals(value) || "No".equals(value)) {
            return Boolean.FALSE;
        } else if ("#".equals(value)) {
            deleteMarkHandel(context);
        } else {
            dataFormatHandel(context, "请选择是否");
        }
        return null;
    }
}
