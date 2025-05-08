package com.ge.apm.aop.audit;


import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;

import java.lang.reflect.InvocationTargetException;


public interface AuditHandler {

    String getBoType();

    default SystemEventAuditEnum determineEventEnum(Object[] args, String boType, String operation) {
        SystemEventAuditEnum systemEventAuditEnum = null;

        String uid = extractBoUid(args);
        switch (boType) {
            case "contract_info":
                if ("delete".equals(operation)) {
                    systemEventAuditEnum = SystemEventAuditEnum.CONTRACT_DELETE;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.CONTRACT_UPDATE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.CONTRACT_CREATE;
                    }
                }
                break;
            case "supplier":
                if ("delete".equals(operation)) {
                    systemEventAuditEnum = SystemEventAuditEnum.SUPPLIER_DELETE;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.SUPPLIER_UPDATE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.SUPPLIER_CREATE;
                    }
                }
                break;
            default:

                break;


        }
        return systemEventAuditEnum;
    }

    default String generateEventData(Object[] args) {
//        Object entity = args[0];
//        return entity != null ? JSON.toJSONString(entity) : null;
        return null;
    }

    default String extractBoUid(Object[] args) {
        Object entity = args[0];
        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getId");
            Object idValue = method.invoke(entity);
            if (idValue != null) {
                return idValue.toString();
            }
            return null;
        } catch (Exception e) {

        }

        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getUid");
            Object uidValue = method.invoke(entity);
            if (uidValue != null) {
                return uidValue.toString();
            }
            return null;
        } catch (Exception e) {
        }

        return null;
    }

    default boolean isSave(Object[] args, String boType, String operation) {
        return true;
    }
}
