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
            return (String) method.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getUid");
            return (String) method.invoke(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }

        return null;
    }

    default boolean isSave(Object[] args, String boType, String operation) {
        return true;
    }
}
