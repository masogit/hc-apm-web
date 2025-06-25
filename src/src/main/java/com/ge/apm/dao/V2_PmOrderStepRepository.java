package com.ge.apm.dao;

import com.ge.apm.domain.V2_PmOrderStep;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface V2_PmOrderStepRepository extends GenericRepositoryUUID<V2_PmOrderStep> {


    List<V2_PmOrderStep> findByPmId(String pmId);
}
