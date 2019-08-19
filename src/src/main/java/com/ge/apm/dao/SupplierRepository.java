package com.ge.apm.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.Supplier;

import webapp.framework.dao.GenericRepository;

public interface SupplierRepository extends GenericRepository<Supplier> {
    public List<Supplier> getBySiteId(int siteId);
    
    public List<Supplier> getBySiteUID(String siteUid);
    public List<Supplier> getByTenantUID(String tenantUid);



    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true " ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUID(String tenantUid);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and manufacturer_t is true " ,nativeQuery = true)
    List<Supplier> findManuByTenantUID(String tenantUid);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and maintainer_t is true " ,nativeQuery = true)
    List<Supplier> findMaintainByTenantUID(String tenantUid);

    public Supplier getBySiteIdAndName(int siteId, String name);
    
    public Supplier findByUid(String uid);
    
    @Query(value="select o.* from supplier o where o.uid=?1 limit 1",nativeQuery=true)
    public Supplier getByUid(String uid);

    public List<Supplier> findBySupplierTAndTenantUID(Boolean supplierT, String tenantUid);
    public List<Supplier> findByMaintainerTAndTenantUID(Boolean maintainerT, String tenantUid);
    public List<Supplier> findByManufacturerTAndTenantUID(Boolean manufacturerT, String tenantUid);

    public Supplier findFirstByTenantUIDAndName(String tenantUid, String name);
}
