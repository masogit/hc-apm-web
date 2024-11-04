package com.ge.apm.dao;

import com.ge.apm.domain.TenantOptionConfig;
import com.ge.apm.domain.WorkflowConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Optional;

public interface TenantOptionConfigRepository extends GenericRepositoryUUID<TenantOptionConfig> {

    Optional<TenantOptionConfig> findByTenantUid(String tenantUid);

    @Query("select w from TenantOptionConfig w where w.activeSupplier = true ")
    public List<TenantOptionConfig> findIsActiveSupplier();

    @Query("select w from TenantOptionConfig w where w.orderByCreateTimeByDefault = true and w.tenantUid is not null ")
    public List<TenantOptionConfig> findAssetListSort();
}
