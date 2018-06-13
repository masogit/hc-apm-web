package com.ge.apm.dao;

import com.ge.apm.domain.TenantInfo;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface TenantInfoRepository extends GenericRepository<TenantInfo> {
    
    public List<TenantInfo> getByName(String name);

    public TenantInfo findByuid(String uid);

    @Query("select t from TenantInfo t order by name")
    public List<TenantInfo> getAllOrderByName();
}
