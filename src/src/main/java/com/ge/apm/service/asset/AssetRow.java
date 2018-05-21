package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AssetRow {
    public static final String DELETE_MARK = "#";

    private AssetInfo assetInfo;
    private boolean isNew;
    private Map<String, Object> rowMap;


    public AssetRow(AssetInfo assetInfo, boolean isNew, Map<String, Object> rowMap) {
        this.assetInfo = assetInfo;
        this.isNew = isNew;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method = AssetInfo.class.getMethod(methodName, String.class);
            if (isNew) {
                method.invoke(assetInfo, fieldValueOfString);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    String emptyString = null;
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        method.invoke(assetInfo, emptyString);
                    } else {
                        method.invoke(assetInfo, fieldValueOfString);
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
            }
            Method method = AssetInfo.class.getMethod(methodName, Integer.class);
            if (isNew) {
                method.invoke(assetInfo, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(assetInfo, nullInteger);
                    } else {
                        method.invoke(assetInfo, fieldValueOfInteger);
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
            }

            Method method = AssetInfo.class.getMethod(methodName, Double.class);
            if (isNew) {
                method.invoke(assetInfo, fieldValueOfDouble);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Double empty = null;
                        method.invoke(assetInfo, empty);
                    } else {
                        method.invoke(assetInfo, fieldValueOfDouble);
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
            }

            if (fieldValueOfDate == null) {
                validateDate(fieldName, fieldValueOfString);
            }

            Method method = AssetInfo.class.getMethod(methodName, Date.class);
            if (isNew) {
                method.invoke(assetInfo, fieldValueOfDate);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Date nullDate = null;
                        method.invoke(assetInfo, nullDate);
                    } else {
                        method.invoke(assetInfo, fieldValueOfDate);
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
            Method method = AssetInfo.class.getMethod(methodName, Integer.class);

            Integer selectedOption = null;
            if (!StringUtils.isEmpty(fieldValueOfString) && !DELETE_MARK.equals(fieldValueOfString)) {
                selectedOption = getI18nMsgKey(msgList, fieldValueOfString, map, fieldName);
            }

            if (selectedOption == null) {
                selectedOption = defaultValue;
            }

            if (isNew) {
                method.invoke(assetInfo, selectedOption);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(assetInfo, nullInteger);
                    } else {
                        method.invoke(assetInfo, selectedOption);
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
            }

            if (fieldValueOfBoolean == null) {
                fieldValueOfBoolean = defaultValue;
            }

            Method method = AssetInfo.class.getMethod(methodName, Boolean.class);
            if (isNew) {
                method.invoke(assetInfo, fieldValueOfBoolean);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Boolean empty = null;
                        method.invoke(assetInfo, empty);
                    } else {
                        method.invoke(assetInfo, fieldValueOfBoolean);
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


}
