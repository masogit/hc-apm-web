package com.ge.apm.dao;

import com.ge.apm.domain.acquisition.AcquisitionPlanStep;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AcquisitionPlanStepRepository extends GenericRepositoryUUID<AcquisitionPlanStep> {

    AcquisitionPlanStep findFirstByPidOrderByCreatedDateDesc(String pid);


    List<AcquisitionPlanStep> findByPid(String pid);


    List<AcquisitionPlanStep> findByPidAndAuditStatus(String pid, String status);

}
