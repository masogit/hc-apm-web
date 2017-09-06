package com.ge.apm.dao;

import com.ge.apm.domain.WorkflowConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;
import java.util.List;

public interface WorkflowConfigRepository extends GenericRepository<WorkflowConfig> {
    @Deprecated
    public WorkflowConfig getBySiteIdAndHospitalId(Integer siteId,Integer hospitalId);
    public WorkflowConfig getByHospitalId(int hospitalId);
    
    @Query(value="select w from WorkflowConfig w where w.siteUID in ?1")
    public List<WorkflowConfig>  getBySiteUIDList(List<String> uidList);
}
