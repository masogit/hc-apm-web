package com.ge.apm.aop.audit;


import com.ge.apm.domain.SharedAssetRequest;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import org.apache.ibatis.annotations.Param;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface AuditHandler {

    String getBoType();

    default SystemEventAuditEnum determineEventEnum(Object[] args, String boType, String operation) {
        SystemEventAuditEnum systemEventAuditEnum = null;

        String uid = extractBoUid(null,args);
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
            case "supplier_contact_person":
                if ("delete".equals(operation)) {
                    systemEventAuditEnum = SystemEventAuditEnum.SUPPLIER_DELETE_CONTACT_PERSON;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.SUPPLIER_UPDATE_CONTACT_PERSON;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.SUPPLIER_CREATE_CONTACT_PERSON;
                    }
                }
                break;
            case "org_info":
                if ("delete".equals(operation)) {
                    systemEventAuditEnum = SystemEventAuditEnum.ORG_DELETE;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.ORG_UPDATE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.ORG_CREATE;
                    }
                }
                break;
            case "tenant_info":
                if ("delete".equals(operation)) {
                    systemEventAuditEnum = SystemEventAuditEnum.TENANT_DELETE;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.TENANT_UPDATE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.TENANT_CREATE;
                    }
                }
                break;
            case "admin_feature_active":
                if ("save".equals(operation)) {
                    systemEventAuditEnum = SystemEventAuditEnum.TENANT_ACTIVE;
                }
                break;
            case "nurse_site_maintain_config":
                if (operation.contains("delete")) {
                    systemEventAuditEnum = SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_DELETE;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_UPDATE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_CREATE;
                    }
                }
                break;
            case "nurse_site_maintain_config_item":
                if (operation.contains("delete")) {
                    systemEventAuditEnum = SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM_DELETE;
                } else {
                    if (uid != null) {
                        systemEventAuditEnum = SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM_UPDATE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.NURSE_SITE_MAINTAIN_CONFIG_ITEM_CREATE;
                    }
                }
                break;
            case "shared_asset_request":
                SharedAssetRequest sharedAssetRequest = (SharedAssetRequest) args[0];
                switch (sharedAssetRequest.getState()){
                    case 1:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_REQUESTED;
                        break;
                    case 2:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_ACCEPTED;
                        break;
                    case 3:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_DELIVERED;
                        break;
                    case 4:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_CANCELED;
                        break;
                    case 5:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_SUSPENDED;
                        break;
                    case 6:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_CONFIRMED;
                        break;
                    case 7:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_REQUESTED;
                        break;
                    case 8:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_ACCEPTED;
                        break;
                    case 9:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_DELIVERED;
                        break;
                    case 10:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_CANCELED;
                        break;
                    case 11:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_SUSPENDED;
                        break;
                    case 12:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_CONFIRMED;
                        break;
                    case 13:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_EXPIRED;
                        break;
                    default:
                        break;
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

    default String extractBoUid(MethodSignature signature, Object[] args) {
        Object entity = args[0];

        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getUid");
            Object uidValue = method.invoke(entity);
            if (uidValue != null) {
                return uidValue.toString();
            }
            return null;
        } catch (Exception e) {
        }

        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getId");
            Object idValue = method.invoke(entity);
            if (idValue != null) {
                return idValue.toString();
            }
            return null;
        } catch (Exception e) {

        }
        try{
            Method method = signature.getMethod();
            Parameter[] parameters = method.getParameters();
            for (int i=0;i<parameters.length;i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(Param.class)) {
                    Param annotation = parameter.getAnnotation(Param.class);
                    String value = annotation.value();
                    if("uid".equals(value) || "id".equals(value)) {
                        return  args[i].toString();
                    }
                }
            }
        }catch (Exception e){

        }
        return null;
    }

    default boolean isSave(Object[] args, String boType, String operation) {
        return true;
    }
}
