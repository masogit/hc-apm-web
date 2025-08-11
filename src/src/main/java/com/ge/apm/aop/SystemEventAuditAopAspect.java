package com.ge.apm.aop;


import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.audit.AuditHandler;
import com.ge.apm.domain.SystemEventAudit;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Aspect
@Component
public class SystemEventAuditAopAspect {

    private Logger logger = LoggerFactory.getLogger(SystemEventAuditAopAspect.class);
    @Autowired
    private MqUtils mqUtils;




    @Autowired(required = false)
    private Map<String, AuditHandler> handlerMap = new HashMap<>();

    @Around("execution(* com.ge.apm.dao.ContractInfoRepository.save(..))   || execution(* com.ge.apm.dao.ContractInfoRepository.delete(..))")
    public Object contractInfoSystemEventAopPointcutAround(ProceedingJoinPoint pjp  ) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"contract_info");
    }



    @Around("execution(* com.ge.apm.dao.BlobObjectRepository.delete(..)) || execution(* com.ge.apm.dao.BlobObjectRepository.save(..))")
    public Object blobObjectSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"v2_blob_object");
    }

    @Around("execution(* com.ge.apm.dao.SupplierRepository.delete(..)) || execution(* com.ge.apm.dao.SupplierRepository.save(..))")
    public Object supplierSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"supplier");
    }

    @Around("execution(* com.ge.apm.dao.TenantInfoRepository.save(..))   || execution(* com.ge.apm.dao.TenantInfoRepository.delete(..))")
    public Object tenantInfoSystemEventAopPointcutAround(ProceedingJoinPoint pjp  ) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"tenant_info");
    }

    @Around("execution(* com.ge.apm.dao.AdminFeatureActiveRepository.save(..))")
    public Object orgInfoSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp, "admin_feature_active");
    }

    @Around("execution(* com.ge.apm.dao.OrgInfoRepository.delete(..)) || execution(* com.ge.apm.dao.OrgInfoRepository.save(..))")
    public Object adminFeatureActiveSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp, "org_info");
    }

    @Around("execution(* com.ge.apm.dao.SupplierContactPersonRepository.delete(..)) || execution(* com.ge.apm.dao.SupplierContactPersonRepository.save(..))")
    public Object supplierContactPersonSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp, "supplier_contact_person");
    }


//    @Around("execution(* com.ge.apm.dao.SharedAssetRequestRepository.save(..))")
    public Object sharedAssetRequestSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp, "shared_asset_request");
    }



//    @Around("execution(* com.ge.apm.dao.SharedAssetRequestRepository.updateState(..)) || execution(* com.ge.apm.dao.SharedAssetRequestRepository.updateStateAndOwner(..))")
    public Object sharedAssetRequestUpdateStateAround(ProceedingJoinPoint pjp) throws Throwable {
        boolean isSuccess = true;
        String exceptionMessage = null;
        Object result ;
        Object[] args = null;
        String boUid = null;
        boolean isSaveAudit=true;
        String eventData=null;
        SystemEventAuditEnum systemEventAuditEnum=null;
        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            isSuccess = false;
            exceptionMessage = e.getMessage();
            logger.error("sharedAssetRequestUpdateStateAround 目标方法执行失败 ,e: ", e);
            throw e;
        } finally {
            try {
//                MethodSignature signature = (MethodSignature) pjp.getSignature();
//                String[] parameterNames = signature.getParameterNames();
//
//                HashMap<Object, Object> param = new HashMap<>();
//                for (int i = 0; i < parameterNames.length; i++) {
//                    param.put(parameterNames[i], args[i]);
//                }
                args= pjp.getArgs();
                int  state = (int)args[1];
                systemEventAuditEnum= null;
                switch (state){
//                    case 1:
//                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_REQUESTED;
//                        break;
                    case 2:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_ACCEPTED;
                        break;
//                    case 3:
//                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_DELIVERED;
//                        break;
                    case 4:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_CANCELED;
                        break;
//                    case 5:
//                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_SUSPENDED;
//                        break;
                    case 6:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RENT_CONFIRMED;
                        break;

                    case 8:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_ACCEPTED;
                        break;
                    case 12:
                        systemEventAuditEnum = SystemEventAuditEnum.SHARED_RETURN_CONFIRMED;
                        break;
                    default:
                        break;
                }

                boUid =  (String) args[0];;

                if(isSaveAudit && systemEventAuditEnum!=null){
                    UserAccount userAccount = getUserAccount();
                    SystemEventAudit systemEventAudit = buildEventAudit(userAccount,systemEventAuditEnum, boUid, isSuccess, exceptionMessage,"shared_asset_request",eventData);
                    sendSystemAuditMessage(systemEventAudit);
                }
            } catch (Exception e) {
                logger.error("sharedAssetRequestUpdateStateAround , error:", e);
            }
        }

        return result;

    }

    public Object repositorySystemEventAopPointcutAround(ProceedingJoinPoint pjp,String boType  ) throws Throwable {
        String operation = pjp.getSignature().getName();
        boolean isSuccess = true;
        String exceptionMessage = null;
        Object result ;
        Object[] args = null;
        String boUid = null;
        boolean isSaveAudit=true;
        String eventData=null;

        SystemEventAuditEnum systemEventAuditEnum=null;
        args= pjp.getArgs();
        AuditHandler auditHandler = handlerMap.getOrDefault(boType, handlerMap.get("defaultAuditHandler"));
        try {
            systemEventAuditEnum= auditHandler.determineEventEnum(args, boType, operation);
            eventData = auditHandler.generateEventData(args );
        } catch (Throwable e) {
            logger.error("repositorySystemEventAopPointcutAround boType:{},operation:{}, 获取参数异常,e: ",boType,operation,e);
        }
        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            isSuccess = false;
            exceptionMessage = e.getMessage();
            logger.error("repositorySystemEventAopPointcutAround 目标方法执行失败 boType:{},,operation:{},e: ", boType,operation, e);
            throw e;
        } finally {
            try {
                boUid = auditHandler.extractBoUid(args );
                isSaveAudit = auditHandler.isSave(args, boType, operation);
                 if(isSaveAudit && systemEventAuditEnum!=null){
                    UserAccount userAccount = getUserAccount();
                    if("tenant_info".equals(boType)){
                        String uid = userAccount.getUid();
                        userAccount=new UserAccount();
                        userAccount.setUid(uid);
                        TenantInfo  tenantInfo = (TenantInfo) args[0];
                        userAccount.setTenantUID(tenantInfo.getUid());
                        userAccount.setInstitutionUID(tenantInfo.getUid());
                        userAccount.setHospitalUID(tenantInfo.getUid());
                        userAccount.setSiteUID(tenantInfo.getUid());
                    }
                    SystemEventAudit systemEventAudit = buildEventAudit(userAccount,systemEventAuditEnum, boUid, isSuccess, exceptionMessage,boType,eventData);
                    sendSystemAuditMessage(systemEventAudit);
                }
            } catch (Exception e) {
                logger.error("repositorySystemEventAopPointcutAround boType:{}, error:",boType, e);
            }
        }

        return result;
    }

    private UserAccount getUserAccount() {
        return UserContextService.getCurrentUserAccount();
    }


    public SystemEventAudit buildEventAudit(UserAccount user, SystemEventAuditEnum systemEventAuditEnum, String boUid, boolean isSuccess, String errorLog, String boType,String eventData){
        SystemEventAudit systemEventAudit = new SystemEventAudit();
        systemEventAudit.setErrorLog(errorLog);
        systemEventAudit.setResult(isSuccess);
        systemEventAudit.setCreatedDate(new Date());
        systemEventAudit.setTenantUid(user.getTenantUID());
        systemEventAudit.setBoUid(boUid);
        systemEventAudit.setOperatorUid(user.getUid());
        systemEventAudit.setInstitutionUid(user.getInstitutionUID());
        systemEventAudit.setSiteUid(user.getSiteUID());
        systemEventAudit.setHospitalUid(user.getHospitalUID());
        systemEventAudit.setBoType(boType);
        systemEventAudit.setSystemEventKey(systemEventAuditEnum.getFeatureCode());
        systemEventAudit.setEventData(eventData);
        return systemEventAudit;
    }

    public void sendSystemAuditMessage(SystemEventAudit systemEventAudit){
        String routingKey = RabbitConfig.SYSTEM_EVENT_AUDIT_KEY_PREFIX
            + systemEventAudit.getBoType().replace("_", ".").toLowerCase();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventAudit",systemEventAudit);
//        MqContext eventAuditMq = MqContext.builder().exChange(RabbitConfig.EVENT_NOTIFICATION_CHANGE).routingKey(routingKey).jsonObject(jsonObject).time(new Date()).type("eventAudit").build();
        MqContext mqContext = new MqContext();
        mqContext.setExChange(RabbitConfig.EVENT_NOTIFICATION_CHANGE);
        mqContext.setJsonObject(jsonObject);
        mqContext.setRoutingKey(routingKey);
        mqContext.setType("eventAudit");
        mqContext.setTime(new Date());
        mqContext.setRetryCount(0);
        mqUtils.sendToMq(mqContext);

    }





}
