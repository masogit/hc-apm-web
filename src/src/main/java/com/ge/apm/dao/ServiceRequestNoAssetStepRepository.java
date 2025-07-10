package com.ge.apm.dao;

import com.ge.apm.domain.V2_ServiceRequestNoAssetStep;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface ServiceRequestNoAssetStepRepository extends GenericRepositoryUUID<V2_ServiceRequestNoAssetStep> {

    List<V2_ServiceRequestNoAssetStep> findBySrIdOrderByStartTimeDesc(String srId);


    @Query("select s from V2_ServiceRequestNoAssetStep s where s.srId=?1 and s.stepId=?2 and endTime is null")
    List<V2_ServiceRequestNoAssetStep> getBySrIdAndStepIdAndEndTimeIsNull(String srId, int stepId);


    List<V2_ServiceRequestNoAssetStep> findBySrIdOrderByStartTime(String srId);


    public V2_ServiceRequestNoAssetStep findFirstBySrIdAndStepIdOrderByStartTimeAsc(String srId, Integer stepId);

}
