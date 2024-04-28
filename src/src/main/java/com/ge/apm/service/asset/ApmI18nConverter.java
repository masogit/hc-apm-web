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

    private Integer institutionId;

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
            Map<String, I18nMessage> cache = DbMessageSource.getMessageCache(institutionId);
            if (Objects.nonNull(cache)) {
                I18nMessage i18nMessage = cache.get(i18nKey);
            }

        }

        return 0;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public ApmI18nConverter(Integer institutionId) {
        this.institutionId = institutionId;
    }
}
