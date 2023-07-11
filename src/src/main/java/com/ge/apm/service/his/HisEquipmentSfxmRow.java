package com.ge.apm.service.his;

import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.Map;

public class HisEquipmentSfxmRow {

    public static final String DELETE_MARK = "#";

    private HisEquipmentSfxm hisEquipmentSfxm;
    private Map rowMap;


    public HisEquipmentSfxmRow(HisEquipmentSfxm hisEquipmentSfxm, Map rowMap) {
        this.hisEquipmentSfxm = hisEquipmentSfxm;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method = HisEquipmentSfxm.class.getMethod(methodName, String.class);

                //only new record allow # for its content

//                if (DELETE_MARK.equals(fieldValueOfString)) {
//                    fieldValueOfString = null;
//                }
                method.invoke(hisEquipmentSfxm, fieldValueOfString);

        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

}
