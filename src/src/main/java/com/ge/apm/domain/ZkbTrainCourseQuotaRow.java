package com.ge.apm.domain;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by 212605082 on 2018/8/21.
 */
public class ZkbTrainCourseQuotaRow {
    public static final String DELETE_MARK = "#";

    private ZkbTrainCourseQuotaImportVO zkbTrainCourseQuotaImportVO;
    private Map<String, Object> rowMap;

    public ZkbTrainCourseQuotaRow(ZkbTrainCourseQuotaImportVO zkbTrainCourseQuotaImportVO, Map<String, Object> rowMap) {
        this.zkbTrainCourseQuotaImportVO = zkbTrainCourseQuotaImportVO;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            this.rowMap.put(fieldName, fieldValueOfString);
            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, String.class);

            if (!StringUtils.isEmpty(fieldValueOfString)) {
                String emptyString = null;
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    method.invoke(zkbTrainCourseQuotaImportVO, emptyString);
                } else {
                    method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfString);
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
            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, Integer.class);

            if (StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId())) {
                method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbTrainCourseQuotaImportVO, nullInteger);
                    } else {
                        method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfInteger);
                    }
                }

            }
        } catch (Exception e) {
            throw new IllegalStateException(fieldName + "[" + fieldValueOfString + "]" + e.getMessage());
        }
    }

    public void setFieldNumValue(String fieldName, String methodName){
        String fieldValueOfString = "";
        try {
            fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Integer fieldValueOfInteger = null;
            if(fieldValueOfString ==null || fieldValueOfString.isEmpty()){
                fieldValueOfInteger = 0;
            }else if("N/A".equals(fieldValueOfString)){
                fieldValueOfInteger = 0;
            }else if(!isNumeric(fieldValueOfString)){
                fieldValueOfInteger = 0;
            }else if (!DELETE_MARK.equals(fieldValueOfString)) {
                fieldValueOfInteger = ExcelDocument.getCellIntegerValue(rowMap.get(fieldName));
                this.rowMap.put(fieldName, fieldValueOfInteger);
            }
            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, Integer.class);

            if (StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId())) {
                method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbTrainCourseQuotaImportVO, nullInteger);
                    } else {
                        method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfInteger);
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

            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, Double.class);

            if (StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId())) {
                method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfDouble);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Double empty = null;
                        method.invoke(zkbTrainCourseQuotaImportVO, empty);
                    } else {
                        method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfDouble);
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

            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, Date.class);

            if (StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId())) {
                method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfDate);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Date nullDate = null;
                        method.invoke(zkbTrainCourseQuotaImportVO, nullDate);
                    } else {
                        method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfDate);
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
            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, Integer.class);

            Integer selectedOption = null;
            if (!StringUtils.isEmpty(fieldValueOfString) && !DELETE_MARK.equals(fieldValueOfString)) {
                selectedOption = getI18nMsgKey(msgList, fieldValueOfString, map, fieldName);
                this.rowMap.put(fieldName, selectedOption);
            }

            if (selectedOption == null) {
                selectedOption = defaultValue;
            }

            if (StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId())) {
                method.invoke(zkbTrainCourseQuotaImportVO, selectedOption);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(zkbTrainCourseQuotaImportVO, nullInteger);
                    } else {
                        method.invoke(zkbTrainCourseQuotaImportVO, selectedOption);
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

            Method method = ZkbTrainCourseQuotaImportVO.class.getMethod(methodName, Boolean.class);

            if (StringUtils.isEmpty(zkbTrainCourseQuotaImportVO.getId())) {
                method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfBoolean);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Boolean empty = null;
                        method.invoke(zkbTrainCourseQuotaImportVO, empty);
                    } else {
                        method.invoke(zkbTrainCourseQuotaImportVO, fieldValueOfBoolean);
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


    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        return pattern.matcher(str).matches();
    }
}
