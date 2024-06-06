package com.ge.apm.service.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ge.apm.service.asset.ApmEasyExcelConvert;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AssetImportDateConvert implements ApmEasyExcelConvert, Converter<Date> {

    private String[] dateFormatSet = {"yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "MM/dd/yy", "dd/MM/yy"};

    @Override
    public Class<?> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return Converter.super.convertToJavaData(cellData, contentProperty, globalConfiguration);
    }

    @Override
    public Date convertToJavaData(ReadConverterContext<?> context) throws Exception {
        String dateStr = context.getReadCellData().getStringValue();
        if ("#".equals(dateStr)) {
            deleteMarkHandel(context);
            return null;

        } else if (StringUtils.isNotEmpty(dateStr)) {
            Date date = stringDateConvert(dateStr);
            if (Objects.isNull(date)) {
                dataFormatHandel(context, "请填入正确的日期");
            }
            return date;
        }
        BigDecimal dateNum = context.getReadCellData().getNumberValue();
        if (Objects.nonNull(dateNum)) {
            Date date = numberDataConvert(dateNum);
            if (Objects.isNull(date)) {
                dataFormatHandel(context, "请填入正确的日期");
            }
            return date;
        }
        return null;

    }

    public static Date numberDataConvert(BigDecimal daysDuration) {

        int days = daysDuration.intValue();//天数
        int mills = (int) Math.round(daysDuration.subtract(new BigDecimal(days)).doubleValue() * 24 * 3600);

        //获取时间
        Calendar c = Calendar.getInstance();
        c.set(1900, 0, 1);
        c.add(Calendar.DATE, days - 2);
        int hour = mills / 3600;
        int minute = (mills - hour * 3600) / 60;
        int second = mills - hour * 3600 - minute * 60;
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

    private Date stringDateConvert(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr)) {
            Date date = null;
            for (int i = 0; i < dateFormatSet.length; i++) {
                date = TimeUtils.getDateFromStr(dateStr, dateFormatSet[i]);
                if (Objects.nonNull(date)) break;
            }
            return date;
        }
        return null;
    }
}
