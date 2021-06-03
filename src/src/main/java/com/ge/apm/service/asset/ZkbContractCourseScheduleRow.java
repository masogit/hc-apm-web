package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.ZkbContractCourseScheduleVO;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/9/2
 */
public class ZkbContractCourseScheduleRow {
    public static final String DELETE_MARK = "#";

    private ZkbContractCourseScheduleVO zkbContractCourseScheduleVO;
    private Map<String, Object> rowMap;

    public ZkbContractCourseScheduleRow(ZkbContractCourseScheduleVO zkbContractCourseScheduleVO, Map<String, Object> rowMap) {
        this.zkbContractCourseScheduleVO = zkbContractCourseScheduleVO;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            this.rowMap.put(fieldName, fieldValueOfString);
            Method method = ZkbContractCourseScheduleVO.class.getMethod(methodName, String.class);
            if (zkbContractCourseScheduleVO.getId()==null) {
                method.invoke(zkbContractCourseScheduleVO, fieldValueOfString);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    String emptyString = null;
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        method.invoke(zkbContractCourseScheduleVO, emptyString);
                    } else {
                        method.invoke(zkbContractCourseScheduleVO, fieldValueOfString);
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
            Method method = ZkbContractCourseScheduleVO.class.getMethod(methodName, Integer.class);
            if (zkbContractCourseScheduleVO.getId()==null) {
                method.invoke(zkbContractCourseScheduleVO, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbContractCourseScheduleVO, nullInteger);
                    } else {
                        method.invoke(zkbContractCourseScheduleVO, fieldValueOfInteger);
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

            Method method = ZkbContractCourseScheduleVO.class.getMethod(methodName, Double.class);

            if (StringUtils.isEmpty(zkbContractCourseScheduleVO.getId())) {
                method.invoke(zkbContractCourseScheduleVO, fieldValueOfDouble);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Double empty = null;
                        method.invoke(zkbContractCourseScheduleVO, empty);
                    } else {
                        method.invoke(zkbContractCourseScheduleVO, fieldValueOfDouble);
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
            Method method =  ZkbContractCourseScheduleVO.class.getMethod(methodName, Date.class);
            if (StringUtils.isEmpty(zkbContractCourseScheduleVO.getId())) {
                method.invoke(zkbContractCourseScheduleVO, fieldValueOfDate);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Date nullDate = null;
                        method.invoke(zkbContractCourseScheduleVO, nullDate);
                    } else {
                        method.invoke(zkbContractCourseScheduleVO, fieldValueOfDate);
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
