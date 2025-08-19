package com.ge.apm.aop.audit;

import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

@Service("defaultAuditHandler")
public class DefaultAuditHandler implements AuditHandler{
    @Override
    public String getBoType() {
        return "defaultAuditHandler";
    }

    @Override
    public SystemEventAuditEnum determineEventEnum(Object[] args, String boType, String operation) {
        return AuditHandler.super.determineEventEnum(args, boType, operation);
    }

    @Override
    public String generateEventData(Object[] args) {
        return AuditHandler.super.generateEventData(args);
    }

    @Override
    public String extractBoUid(MethodSignature signature,Object[] args) {
        return AuditHandler.super.extractBoUid(signature,args);
    }

    @Override
    public boolean isSave(Object[] args, String boType, String operation) {
        return AuditHandler.super.isSave(args, boType, operation);
    }
}
