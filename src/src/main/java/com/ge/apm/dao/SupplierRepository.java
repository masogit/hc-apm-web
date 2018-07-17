package com.ge.apm.dao;

import com.ge.apm.domain.Supplier;
import java.util.List;
import webapp.framework.dao.GenericRepository;

public interface SupplierRepository extends GenericRepository<Supplier> {
    public List<Supplier> getBySiteId(int siteId);
    
    public List<Supplier> getBySiteUID(String siteUid);
    public List<Supplier> getByTenantUID(String tenantUid);

    public Supplier getBySiteIdAndName(int siteId, String name);
    
    public Supplier findByUid(String uid);

    public List<Supplier> findBySupplierTAndTenantUID(Boolean supplierT, String tenantUid);
    public List<Supplier> findByMaintainerTAndTenantUID(Boolean maintainerT, String tenantUid);
    public List<Supplier> findByManufacturerTAndTenantUID(Boolean manufacturerT, String tenantUid);

    public Supplier findFirstByTenantUIDAndName(String tenantUid, String name);
}
