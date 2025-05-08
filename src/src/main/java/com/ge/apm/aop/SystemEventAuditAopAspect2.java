package com.ge.apm.aop;


import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.audit.AuditHandler;
import com.ge.apm.domain.SystemEventAudit;
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
public class SystemEventAuditAopAspect2 {

    private Logger logger = LoggerFactory.getLogger(SystemEventAuditAopAspect2.class);
    @Autowired
    private MqUtils mqUtils;




    @Autowired(required = false)
    private Map<String, AuditHandler> handlerMap = new HashMap<>();

    @Around("execution(* com.ge.apm.dao.ContractInfoRepository.save(..))   || execution(* com.ge.apm.dao.ContractInfoRepository.delete(..))")
    public Object contractInfoSystemEventAopPointcutAround(ProceedingJoinPoint pjp  ) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"contract_info");
    }



    @Around("execution(* com.ge.apm.dao.BlobObjectRepository.delete(..)) || execution(* com.ge.apm.dao.BlobObjectRepository.save(..))")
    public Object blobObjectDeleteSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"v2_blob_object");
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
        try {
            args= pjp.getArgs();

            AuditHandler auditHandler = handlerMap.getOrDefault(boType, handlerMap.get("defaultAuditHandler"));
            systemEventAuditEnum= auditHandler.determineEventEnum(args, boType, operation);
            eventData = auditHandler.generateEventData(args );
            result = pjp.proceed();
            boUid = auditHandler.extractBoUid(args );
            isSaveAudit = auditHandler.isSave(args, boType, operation);
        } catch (Throwable e) {
            isSuccess = false;
            exceptionMessage = e.getMessage();
            logger.error("repositorySystemEventAopPointcutAround 目标方法执行失败 boType:{},e: ", boType, e);
            throw e;
        } finally {
            try {
                 if(isSaveAudit && systemEventAuditEnum!=null){
                    UserAccount userAccount = getUserAccount();
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
