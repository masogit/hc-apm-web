package com.ge.apm.service.surgeryAnesthesia;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaDataSource;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * 手术麻醉源数据Excel行映射工具类
 * 通过反射将Excel单元格值映射到 SurgeryAnesthesiaDataSource 实体
 */
public class SurgeryAnesthesiaDataSourceRow {

    public static final String DELETE_MARK = "#";

    private SurgeryAnesthesiaDataSource dataSource;
    private Map rowMap;

    public SurgeryAnesthesiaDataSourceRow(SurgeryAnesthesiaDataSource dataSource, Map rowMap) {
        this.dataSource = dataSource;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method = SurgeryAnesthesiaDataSource.class.getMethod(methodName, String.class);
            method.invoke(dataSource, fieldValueOfString);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void setFieldDateValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Date fieldValueOfDate = null;
            if (!DELETE_MARK.equals(fieldValueOfString)) {
                fieldValueOfDate = ExcelDocument.getCellDateValue(rowMap.get(fieldName));
            }

            if (fieldValueOfDate == null) {
                validateDate(fieldName, fieldValueOfString);
            }

            Method method = SurgeryAnesthesiaDataSource.class.getMethod(methodName, Date.class);
            method.invoke(dataSource, fieldValueOfDate);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void validateDate(String fieldName, String fieldValue) throws Exception {
        if (StringUtils.isEmpty(fieldValue)) {
            return;
        }
        if (DELETE_MARK.equals(fieldValue)) {
            return;
        }
        throw new Exception(fieldName + "[" + fieldValue + "]无效的日期格式");
    }
}
