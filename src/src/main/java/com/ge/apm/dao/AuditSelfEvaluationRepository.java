package com.ge.apm.dao;

import com.ge.apm.domain.AuditSelfEvaluation;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 503266963
 */
public interface AuditSelfEvaluationRepository extends GenericRepository<AuditSelfEvaluation> {

    void deleteByCaseUid(String caseUid);

}
