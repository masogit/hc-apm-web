package com.ge.apm.dao;

import com.ge.apm.domain.acquisition.AcquisitionPlan;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AcquisitionPlanRepository extends GenericRepositoryUUID<AcquisitionPlan> {

    @Query(value = "select max(t.application_no_index) from acquisition_plan t where to_char(t.created_date,'yyyyMMdd') = to_char(now(),'yyyyMMdd') ", nativeQuery = true)
    Object getMaxIndex();

    AcquisitionPlan findByApplicationNo(String applicationNo);


    List<AcquisitionPlan> findByApplicationYear(String applicationYear);
}
