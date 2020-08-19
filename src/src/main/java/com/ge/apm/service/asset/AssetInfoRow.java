package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.AssetInfoVO;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 503202755 on 2020/8/11.
 */
public class AssetInfoRow {
    public static final String DELETE_MARK = "#";

    private AssetInfoVO assetInfoVO;
    private Map<String, Object> rowMap;

    public AssetInfoRow(AssetInfoVO assetInfoVO, Map<String, Object> rowMap) {
        this.assetInfoVO = assetInfoVO;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            this.rowMap.put(fieldName, fieldValueOfString);
            Method method = AssetInfoVO.class.getMethod(methodName, String.class);

            if (assetInfoVO.getId()==null) {
                method.invoke(assetInfoVO, fieldValueOfString);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    String emptyString = null;
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        method.invoke(assetInfoVO, emptyString);
                    } else {
                        method.invoke(assetInfoVO, fieldValueOfString);
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
            Method method = AssetInfoVO.class.getMethod(methodName, Integer.class);

            if (assetInfoVO.getId()==null) {
                method.invoke(assetInfoVO, fieldValueOfInteger);
            } else {
                if (!StringUtils.isEmpty(fieldValueOfString)) {
                    if (DELETE_MARK.equals(fieldValueOfString)) {
                        Integer nullInteger = null;
                        method.invoke(assetInfoVO, nullInteger);
                    } else {
                        method.invoke(assetInfoVO, fieldValueOfInteger);
                    }
                }

            }
        } catch (Exception e) {
            throw new IllegalStateException(fieldName + "[" + fieldValueOfString + "]" + e.getMessage());
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
