package com.ge.apm.dao;

import com.ge.apm.domain.Supplier;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface SupplierRepository extends GenericRepository<Supplier> {
    public List<Supplier> getBySiteId(int siteId);
    
    public List<Supplier> getBySiteUID(String siteUid);
    public List<Supplier> getByTenantUID(String tenantUid);

    public Supplier getBySiteIdAndName(int siteId, String name);
}
