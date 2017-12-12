package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.InvocationTargetException;
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

    public void setAliasName() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("设备别名"));
        if (isNew) {
            assetInfo.setAliasName(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setAliasName(null);
                } else {
                    assetInfo.setAliasName(fieldValueOfString);
                }
            }

        }
    }

    public void setFieldValue(String fieldName, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
        Method method = AssetInfo.class.getMethod(methodName, String.class);
        if (isNew) {
            method.invoke(assetInfo, fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    method.invoke(assetInfo, null);
                } else {
                    method.invoke(assetInfo, fieldValueOfString);
                }
            }

        }
    }

    public void setFieldIntegerValue(String fieldName, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
        Integer fieldValueOfInteger = ExcelDocument.getCellIntegerValue(rowMap.get(fieldName));
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
    }

    public void setFieldDoubleValue(String fieldName, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
        Double fieldValueOfDouble = ExcelDocument.getCellDoubleValue(rowMap.get(fieldName));
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
    }

    public void setFieldDateValue(String fieldName, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
        Date fieldValueOfDate = ExcelDocument.getCellDateValue(rowMap.get(fieldName));
        Method method = AssetInfo.class.getMethod(methodName, Date.class);
        if (isNew) {
            method.invoke(assetInfo, fieldValueOfDate);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    method.invoke(assetInfo, null);
                } else {
                    method.invoke(assetInfo, fieldValueOfDate);
                }
            }

        }
    }

    public void setFieldDropdownValue(String fieldName, String methodName, List<I18nMessage> msgList, Map map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
        Method method = AssetInfo.class.getMethod(methodName, Integer.class);

        Integer selectedOption = null;
        if (!StringUtils.isEmpty(fieldValueOfString) && !DELETE_MARK.equals(fieldValueOfString)) {
            selectedOption = getI18nMsgKey(msgList, fieldValueOfString, map, fieldName);
        }

        Integer nullInteger = null;

        if (isNew) {
            method.invoke(assetInfo, selectedOption);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    method.invoke(assetInfo, nullInteger);
                } else {
                    method.invoke(assetInfo, selectedOption);
                }
            }

        }
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

    private void addErrMessage(Map<String, Object> item, String comments) {
        if (item.containsKey("errmsg")) {
            ((List<String>) item.get("errmsg")).add(comments);
        } else {
            List<String> c = new ArrayList();
            c.add(comments);
            item.put("errmsg", c);
        }
    }

    public void setAssetOwnerName() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("维修负责人"));
        if (isNew) {
            assetInfo.setAssetOwnerName(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setAssetOwnerName(null);
                } else {
                    assetInfo.setAssetOwnerName(fieldValueOfString);
                }
            }

        }
    }

    public void setAssetOwnerName2() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("维修负责人2"));
        if (isNew) {
            assetInfo.setAssetOwnerName2(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setAssetOwnerName2(null);
                } else {
                    assetInfo.setAssetOwnerName2(fieldValueOfString);
                }
            }

        }
    }

    public void setAssetOwnerName3() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("维修负责人3"));
        if (isNew) {
            assetInfo.setAssetOwnerName3(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setAssetOwnerName3(null);
                } else {
                    assetInfo.setAssetOwnerName3(fieldValueOfString);
                }
            }

        }
    }

    public void setClinicalDeptName() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("使用科室"));
        if (isNew) {
            assetInfo.setClinicalDeptName(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setClinicalDeptName(null);
                } else {
                    assetInfo.setClinicalDeptName(fieldValueOfString);
                }
            }

        }
    }

    public void setManufacture() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("制造商"));
        if (isNew) {
            assetInfo.setManufacture(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setManufacture(null);
                } else {
                    assetInfo.setManufacture(fieldValueOfString);
                }
            }

        }
    }

    public void setMakerShortName() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("品牌/制造商简称"));
        if (isNew) {
            assetInfo.setMakerShortName(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setMakerShortName(null);
                } else {
                    assetInfo.setMakerShortName(fieldValueOfString);
                }
            }

        }
    }

    public void setDeviceName() {
        String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get("设备标准名称"));
        if (isNew) {
            assetInfo.setDeviceName(fieldValueOfString);
        } else {
            if (!StringUtils.isEmpty(fieldValueOfString)) {
                if (DELETE_MARK.equals(fieldValueOfString)) {
                    assetInfo.setDeviceName(null);
                } else {
                    assetInfo.setDeviceName(fieldValueOfString);
                }
            }

        }
    }


}
