package com.ge.apm.service.his;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetInfoVO;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentDataSource;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class HisEquipmentDataSourceRow {

    public static final String DELETE_MARK = "#";

    private HisEquipmentDataSource hisEquipmentDataSource;
    private Map rowMap;


    public HisEquipmentDataSourceRow(HisEquipmentDataSource hisEquipmentDataSource, Map rowMap) {
        this.hisEquipmentDataSource = hisEquipmentDataSource;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method = HisEquipmentDataSource.class.getMethod(methodName, String.class);

            //only new record allow # for its content

//                if (DELETE_MARK.equals(fieldValueOfString)) {
//                    fieldValueOfString = null;
//                }
            method.invoke(hisEquipmentDataSource, fieldValueOfString);

        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void setFieldIntegerValue(String fieldName, String methodName) {
        String fieldValueOfString = null;
        try {
            fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Integer fieldValueOfInteger = null;
            if (!DELETE_MARK.equals(fieldValueOfString)) {
                fieldValueOfInteger = ExcelDocument.getCellIntegerValue(rowMap.get(fieldName));
            }
            Method method = HisEquipmentDataSource.class.getMethod(methodName, Integer.class);

            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    Integer nullInteger = null;
                    method.invoke(hisEquipmentDataSource, nullInteger);
                } else {
                    method.invoke(hisEquipmentDataSource, fieldValueOfInteger);
                }
            }


        } catch (Exception e) {
            throw new IllegalStateException(fieldName + "[" + fieldValueOfString + "]" + e.getMessage());
        }
    }


    public void setFieldDoubleValue(String fieldName, String methodName) {
        String fieldValueOfString = null;
        try {
            fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            BigDecimal fieldValueOfDouble = null;
            if (!DELETE_MARK.equals(fieldValueOfString)) {
                Double cellDoubleValue = ExcelDocument.getCellDoubleValue(rowMap.get(fieldName));
                if(cellDoubleValue!=null){
                    fieldValueOfDouble = BigDecimal.valueOf(ExcelDocument.getCellDoubleValue(rowMap.get(fieldName)));
                }
            }
            Method method = HisEquipmentDataSource.class.getMethod(methodName, BigDecimal.class);
            method.invoke(hisEquipmentDataSource, fieldValueOfDouble);

        } catch (Exception e) {
            throw new IllegalStateException(fieldName + "[" + fieldValueOfString + "]" + e.getMessage());
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

            Method method = HisEquipmentDataSource.class.getMethod(methodName, Date.class);

            method.invoke(hisEquipmentDataSource, fieldValueOfDate);

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
