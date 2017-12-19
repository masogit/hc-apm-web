package com.ge.apm.dao;

import java.util.List;
import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.ServiceRequestMsg;

public interface ServiceRequestMsgRepository extends GenericRepositoryUUID<ServiceRequestMsg> {
    
    List<ServiceRequestMsg> findBySrIdOrderBySendTime(String srId);
    
}
