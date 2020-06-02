package com.ge.apm.dao;


import com.ge.apm.domain.ApmConfigInfo;
import webapp.framework.dao.GenericRepositoryUUID;

public interface ApmConfigInfoRepository extends GenericRepositoryUUID<ApmConfigInfo> {

    ApmConfigInfo findByItem(String item);

}
