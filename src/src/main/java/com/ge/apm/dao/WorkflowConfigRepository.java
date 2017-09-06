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
    
    @Query(value="select * from workflow_config w where w.site_UID=:uid order by w.id desc limit 1",nativeQuery = true)
    public WorkflowConfig getBySiteUID(@Param("uid")String site_uid);
    
    @Query(value="select w from WorkflowConfig w where w.siteUID in ?1")
    public List<WorkflowConfig>  getBySiteUIDList(List<String> uidList);
}
