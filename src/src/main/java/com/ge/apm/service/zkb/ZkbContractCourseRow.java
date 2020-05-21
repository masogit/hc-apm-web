package com.ge.apm.service.zkb;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain. ZkbContractCourseVO;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2018/8/21.
 */
public class  ZkbContractCourseRow {
    public static final String DELETE_MARK = "#";

    private  ZkbContractCourseVO zkbContractCourse;
    private Map<String, Object> rowMap;

    public  ZkbContractCourseRow(ZkbContractCourseVO zkbContractCourse, Map<String, Object> rowMap) {
        this.zkbContractCourse = zkbContractCourse;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            this.rowMap.put(fieldName, fieldValueOfString);
            Method method =   ZkbContractCourseVO.class.getMethod(methodName, String.class);

            if (!StringUtils.isEmpty(fieldValueOfString)) {
                String emptyString = null;
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    method.invoke(zkbContractCourse, emptyString);
                } else {
                    method.invoke(zkbContractCourse, fieldValueOfString);
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
            Method method =  ZkbContractCourseVO.class.getMethod(methodName, Integer.class);

            if (StringUtils.isEmpty(zkbContractCourse.getId())) {
                method.invoke(zkbContractCourse, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbContractCourse, nullInteger);
                    } else {
                        method.invoke(zkbContractCourse, fieldValueOfInteger);
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

            Method method =  ZkbContractCourseVO.class.getMethod(methodName, Double.class);

            if (StringUtils.isEmpty(zkbContractCourse.getId())) {
                method.invoke(zkbContractCourse, fieldValueOfDouble);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Double empty = null;
                        method.invoke(zkbContractCourse, empty);
                    } else {
                        method.invoke(zkbContractCourse, fieldValueOfDouble);
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

            Method method =  ZkbContractCourseVO.class.getMethod(methodName, Date.class);

            if (StringUtils.isEmpty(zkbContractCourse.getId())) {
                method.invoke(zkbContractCourse, fieldValueOfDate);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Date nullDate = null;
                        method.invoke(zkbContractCourse, nullDate);
                    } else {
                        method.invoke(zkbContractCourse, fieldValueOfDate);
                    }
                }

            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void setFieldDropdownValue(String fieldName, String methodName, List<I18nMessage> msgList, Map map, Integer defaultValue) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method =  ZkbContractCourseVO.class.getMethod(methodName, Integer.class);

            Integer selectedOption = null;
            if (!StringUtils.isEmpty(fieldValueOfString) && !DELETE_MARK.equals(fieldValueOfString)) {
                selectedOption = getI18nMsgKey(msgList, fieldValueOfString, map, fieldName);
                this.rowMap.put(fieldName, selectedOption);
            }

            if (selectedOption == null) {
                selectedOption = defaultValue;
            }

            if (StringUtils.isEmpty(zkbContractCourse.getId())) {
                method.invoke(zkbContractCourse, selectedOption);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbContractCourse, nullInteger);
                    } else {
                        method.invoke(zkbContractCourse, selectedOption);
                    }
                }

            }
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void setFieldBooleanValue(String fieldName, String methodName, Boolean defaultValue) {
        String fieldValueOfString = null;
        try {
            fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Boolean fieldValueOfBoolean = null;
            if (!StringUtils.isEmpty(fieldValueOfString) && !DELETE_MARK.equals(fieldValueOfString)) {
                fieldValueOfBoolean = "否".equals(fieldValueOfString) ? false : true;
                this.rowMap.put(fieldName, fieldValueOfBoolean);
            }

            if (fieldValueOfBoolean == null) {
                fieldValueOfBoolean = defaultValue;
            }

            Method method =  ZkbContractCourseVO.class.getMethod(methodName, Boolean.class);

            if (StringUtils.isEmpty(zkbContractCourse.getId())) {
                method.invoke(zkbContractCourse, fieldValueOfBoolean);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Boolean empty = null;
                        method.invoke(zkbContractCourse, empty);
                    } else {
                        method.invoke(zkbContractCourse, fieldValueOfBoolean);
                    }
                }

            }
        } catch (Exception e) {
            throw new IllegalStateException(fieldName + "[" + fieldValueOfString + "]" + e.getMessage());
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

    private Integer getI18nMsgKey(List<I18nMessage> msgList, String value, Map map, String fieldName) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        I18nMessage msg = msgList.stream().filter(t -> t.getValueZh().equals(value)).findAny().orElse(null);
        if (msg == null) {
            addErrMessage(map, "警告:无效值" + fieldName + "[" + value + "]");
            return null;
        } else {
            return Integer.parseInt(msg.getMsgKey());
        }
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
