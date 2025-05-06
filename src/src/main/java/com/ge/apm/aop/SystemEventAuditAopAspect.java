package com.ge.apm.aop;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.SystemEventAudit;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
public class SystemEventAuditAopAspect {

    private Logger logger = LoggerFactory.getLogger(SystemEventAuditAopAspect.class);
    @Autowired
    private MqUtils mqUtils;

    @Pointcut("execution(* com.ge.apm.dao.ContractInfoRepository.save(..))")
    public void contractInfoSystemEventAuditAopPointcut() {}



    @Around("contractInfoSystemEventAuditAopPointcut()")
    public Object contractInfoSystemEventAopPointcutAround(ProceedingJoinPoint pjp  ) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"contract_info","save");
    }


    @Around("execution(* com.ge.apm.dao.ContractInfoRepository.delete(..))")
    public Object contractInfoDeleteSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"contract_info","delete");
    }

    @Around("execution(* com.ge.apm.dao.BlobObjectRepository.delete(..))")
    public Object blobObjectDeleteSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"v2_blob_object","delete");
    }
    @Around("execution(* com.ge.apm.dao.BlobObjectRepository.save(..))")
    public Object blobObjectSaveSystemEventAopPointcutAround(ProceedingJoinPoint pjp) throws Throwable {
        return repositorySystemEventAopPointcutAround(pjp,"v2_blob_object","save");
    }

    public Object repositorySystemEventAopPointcutAround(ProceedingJoinPoint pjp,String boType ,String operation) throws Throwable {
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
            switch (boType){
                case  "contract_info":
                    ContractInfo  contractInfo = (ContractInfo)args[0];
                    if("delete".equals(operation)){
                        systemEventAuditEnum=SystemEventAuditEnum.CONTRACT_DELETE;
                    }else{
                        if(contractInfo.getId()!=null){
                            systemEventAuditEnum=SystemEventAuditEnum.CONTRACT_UPDATE;
                        }else{
                            systemEventAuditEnum=SystemEventAuditEnum.CONTRACT_CREATE;
                        }
                    }
                    break;
                case "v2_blob_object":
                    List<V2_BlobObject> blobList = new ArrayList<>();
                    if (args.length > 0 && args[0] instanceof Iterable) {
                        Iterable<V2_BlobObject> iterable = (Iterable<V2_BlobObject>) args[0];
                        iterable.forEach(blobList::add);
                    }else{
                        V2_BlobObject v2BlobObject=(V2_BlobObject)   args[0];
                        blobList.add(v2BlobObject);
                    }
                    blobList = blobList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());

                    if(CollectionUtils.isEmpty(blobList)){
                        isSaveAudit=false;
                    }else{
                        V2_BlobObject v2BlobObject = blobList.get(0);
                        boUid=v2BlobObject.getBoUuid();
                        switch (v2BlobObject.getBoType()){
                            case V2_BlobObject.BO_TYPE_AssetContract:
                                if("delete".equals(operation)){
                                    systemEventAuditEnum=SystemEventAuditEnum.CONTRACT_ATTACHMENT_DELETE;
                                }else{
                                    systemEventAuditEnum=SystemEventAuditEnum.CONTRACT_ATTACHMENT_ADD;
                                }
                                break;
                            case V2_BlobObject.BO_TYPE_ArrivalList:
                                if("delete".equals(operation)){
                                    systemEventAuditEnum=SystemEventAuditEnum.ARRIVAL_LIST_ATTACHMENT_DELETE;
                                }else{
                                    systemEventAuditEnum=SystemEventAuditEnum.ARRIVAL_LIST_ATTACHMENT_ADD;
                                }
                                break;
                            case V2_BlobObject.BO_TYPE_ArrivalListItem:
                                if("delete".equals(operation)){
                                    systemEventAuditEnum=SystemEventAuditEnum.ARRIVAL_ITEM_ATTACHMENT_DELETE;
                                }else{
                                    systemEventAuditEnum=SystemEventAuditEnum.ARRIVAL_ITEM_ATTACHMENT_ADD;
                                }
                                break;
                            default:
                                isSaveAudit=false;
                                break;
                        }

                        Map<String, List<V2_BlobObject>> blobMap = blobList.stream().collect(Collectors.groupingBy(blob -> blob.getBoSubType() == null ? "unknown" : blob.getBoSubType()));
                        HashMap<Object, Object> eventMap = new HashMap<>();
                        for (Map.Entry<String, List<V2_BlobObject>> stringListEntry : blobMap.entrySet()) {
                            List<V2_BlobObject> value = stringListEntry.getValue();
                            List<String> objectStorageIds = value.stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toList());
                            eventMap.put(stringListEntry.getKey(),objectStorageIds);
                        }
                        eventData= JSON.toJSONString(eventMap);
                    }
                    break;
                default:
                    break;
            }
            result = pjp.proceed();

            if("contract_info".equals(boType)){
                ContractInfo  contractInfo = (ContractInfo)args[0];
                boUid=contractInfo.getId();
            }
        } catch (Throwable e) {
            isSuccess = false;
            exceptionMessage = e.getMessage();
            logger.error("repositorySystemEventAopPointcutAround 目标方法执行失败 boType:{},e: ", boType, e);
            throw e;
        } finally {
            try {
                if(isSaveAudit){
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
