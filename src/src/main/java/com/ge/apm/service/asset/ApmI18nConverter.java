package com.ge.apm.service.asset;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.service.utils.I18n;
import webapp.framework.web.service.DbMessageSource;

import java.util.Map;
import java.util.Objects;

public class ApmI18nConverter implements Converter<Integer> {


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
        I18n i18n = context.getContentProperty().getField().getAnnotation(I18n.class);
        if (Objects.nonNull(i18n)) {
            String i18nKey = i18n.value();



        }

        return 0;
    }



}
