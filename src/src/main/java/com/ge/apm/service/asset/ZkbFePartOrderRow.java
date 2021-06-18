package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.ZkbFePartOrderVO;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/2
 */
public class ZkbFePartOrderRow {
    public static final String DELETE_MARK = "#";

    private ZkbFePartOrderVO zkbFePartOrderVO;
    private Map<String, Object> rowMap;

    public ZkbFePartOrderRow(ZkbFePartOrderVO zkbFePartOrderVO, Map<String, Object> rowMap) {
        this.zkbFePartOrderVO = zkbFePartOrderVO;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            this.rowMap.put(fieldName, fieldValueOfString);
            Method method = ZkbFePartOrderVO.class.getMethod(methodName, String.class);
            if (zkbFePartOrderVO.getId()==null) {
                method.invoke(zkbFePartOrderVO, fieldValueOfString);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    String emptyString = null;
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        method.invoke(zkbFePartOrderVO, emptyString);
                    } else {
                        method.invoke(zkbFePartOrderVO, fieldValueOfString);
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
            Method method = ZkbFePartOrderVO.class.getMethod(methodName, Integer.class);
            if (zkbFePartOrderVO.getId()==null) {
                method.invoke(zkbFePartOrderVO, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbFePartOrderVO, nullInteger);
                    } else {
                        method.invoke(zkbFePartOrderVO, fieldValueOfInteger);
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

            Method method = ZkbFePartOrderVO.class.getMethod(methodName, Double.class);

            if (StringUtils.isEmpty(zkbFePartOrderVO.getId())) {
                method.invoke(zkbFePartOrderVO, fieldValueOfDouble);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Double empty = null;
                        method.invoke(zkbFePartOrderVO, empty);
                    } else {
                        method.invoke(zkbFePartOrderVO, fieldValueOfDouble);
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
            Method method =  ZkbFePartOrderVO.class.getMethod(methodName, Date.class);
            if (StringUtils.isEmpty(zkbFePartOrderVO.getId())) {
                method.invoke(zkbFePartOrderVO, fieldValueOfDate);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Date nullDate = null;
                        method.invoke(zkbFePartOrderVO, nullDate);
                    } else {
                        method.invoke(zkbFePartOrderVO, fieldValueOfDate);
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
