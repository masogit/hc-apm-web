package com.ge.apm.service.his;

import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.service.utils.ExcelDocument;

import java.lang.reflect.Method;
import java.util.Map;

public class HisClinicalRow {

    public static final String DELETE_MARK = "#";

    private HisAdministrativeOffices hisAdministrativeOffices;
    private Map rowMap;


    public HisClinicalRow(HisAdministrativeOffices hisAdministrativeOffices, Map rowMap) {
        this.hisAdministrativeOffices = hisAdministrativeOffices;
        this.rowMap = rowMap;
    }

    public void setFieldStringValue(String fieldName, String methodName) {
        try {
            String fieldValueOfString = ExcelDocument.getCellStringValue(rowMap.get(fieldName));
            Method method = HisAdministrativeOffices.class.getMethod(methodName, String.class);

                //only new record allow # for its content

//                if (DELETE_MARK.equals(fieldValueOfString)) {
//                    fieldValueOfString = null;
//                }
                method.invoke(hisAdministrativeOffices, fieldValueOfString);

        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

}
