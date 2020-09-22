package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/2
 */
public class OrderHistoryInfoRow {
    public static final String DELETE_MARK = "#";

    private OrderHistoryVO orderHistoryVO;
    private Map<String, Object> rowMap;

    public OrderHistoryInfoRow(OrderHistoryVO orderHistoryVO, Map<String, Object> rowMap) {
        this.orderHistoryVO = orderHistoryVO;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            this.rowMap.put(fieldName, fieldValueOfString);
            Method method = OrderHistoryVO.class.getMethod(methodName, String.class);
            if (orderHistoryVO.getId()==null) {
                method.invoke(orderHistoryVO, fieldValueOfString);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    String emptyString = null;
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        method.invoke(orderHistoryVO, emptyString);
                    } else {
                        method.invoke(orderHistoryVO, fieldValueOfString);
                    }
                }

            }
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
                this.rowMap.put(fieldName, fieldValueOfInteger);
            }
            Method method = OrderHistoryVO.class.getMethod(methodName, Integer.class);
            if (orderHistoryVO.getId()==null) {
                method.invoke(orderHistoryVO, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(orderHistoryVO, nullInteger);
                    } else {
                        method.invoke(orderHistoryVO, fieldValueOfInteger);
                    }
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
            Double fieldValueOfDouble = null;
            if (!DELETE_MARK.equals(fieldValueOfString)) {
                fieldValueOfDouble = ExcelDocument.getCellDoubleValue(rowMap.get(fieldName));
                this.rowMap.put(fieldName, fieldValueOfDouble);
            }

            Method method = OrderHistoryVO.class.getMethod(methodName, Double.class);

            if (StringUtils.isEmpty(orderHistoryVO.getId())) {
                method.invoke(orderHistoryVO, fieldValueOfDouble);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Double empty = null;
                        method.invoke(orderHistoryVO, empty);
                    } else {
                        method.invoke(orderHistoryVO, fieldValueOfDouble);
                    }
                }

            }
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
                this.rowMap.put(fieldName, fieldValueOfDate);
            }
            if (fieldValueOfDate == null) {
                validateDate(fieldName, fieldValueOfString);
            }
            Method method =  OrderHistoryVO.class.getMethod(methodName, Date.class);
            if (StringUtils.isEmpty(orderHistoryVO.getId())) {
                method.invoke(orderHistoryVO, fieldValueOfDate);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Date nullDate = null;
                        method.invoke(orderHistoryVO, nullDate);
                    } else {
                        method.invoke(orderHistoryVO, fieldValueOfDate);
                    }
                }

            }
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

    public static void addErrMessage(Map<String, Object> item, String comments) {
        if (item.containsKey("errmsg")) {
            ((List<String>) item.get("errmsg")).add(comments);
        } else {
            List<String> c = new ArrayList();
            c.add(comments);
            item.put("errmsg", c);
        }
    }

    public static void clearErrMessage(Map<String, Object> item) {
        if (item.containsKey("errmsg")) {
            List<String> errMsgs = ((List<String>) item.get("errmsg"));
            errMsgs.clear();
        }
    }

}
