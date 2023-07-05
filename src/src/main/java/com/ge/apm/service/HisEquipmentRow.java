package com.ge.apm.service;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HisEquipmentRow {

    public static final String DELETE_MARK = "#";

    private HisEquipmentConnect hisEquipmentConnect;
    private Map rowMap;


    public HisEquipmentRow(HisEquipmentConnect hisEquipment, Map rowMap) {
        this.hisEquipmentConnect = hisEquipment;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method = HisEquipmentConnect.class.getMethod(methodName, String.class);

                //only new record allow # for its content

//                if (DELETE_MARK.equals(fieldValueOfString)) {
//                    fieldValueOfString = null;
//                }
                method.invoke(hisEquipmentConnect, fieldValueOfString);

        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

}
