package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrderManHoursDetail;
import com.ge.apm.domain.V2_WorkOrder_Step;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface V2_WorkOrderManHoursDetailRepository extends GenericRepositoryUUID<V2_WorkOrderManHoursDetail> {

    public List<V2_WorkOrderManHoursDetail> findByWoIdOrderByStartTime(String woId);
}
