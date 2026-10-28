package com.ge.apm.dao;

import com.ge.apm.domain.Supplier;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;

public interface SupplierRepository extends GenericRepository<Supplier> {
    public List<Supplier> getBySiteId(int siteId);
    
    public List<Supplier> getBySiteUID(String siteUid);
    public List<Supplier> getByTenantUID(String tenantUid);


    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true and (status is null or status ='Enable')" ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUIDAndStatus(String tenantUid);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true " ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUID(String tenantUid);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true  and  name like %?2%  order by  name_py_code  asc   limit ?3 " ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUIDAndName(String tenantUid,String name,int size);
    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true order by  name_py_code  asc  limit ?2 " ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUID(String tenantUid,int size);


    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true   and (status is null or status ='Enable')  and  name like %?2%  order by  name_py_code  asc  limit ?3 " ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUIDAndNameAndStatus(String tenantUid,String name,int size);
    @Query(value= "select su.* from supplier su where tenant_uid=?1 and supplier_t is true and (status is null or status ='Enable') order by  name_py_code  asc   limit ?2 " ,nativeQuery = true)
    List<Supplier> findSupplierByTenantUIDAndStatus(String tenantUid,int size);


    @Query(value= "select su.* from supplier su where tenant_uid=?1 and manufacturer_t is true " ,nativeQuery = true)
    List<Supplier> findManuByTenantUID(String tenantUid);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and manufacturer_t is true   and  name like %?2%   order by  name_py_code  asc  limit ?3     " ,nativeQuery = true)
    List<Supplier> findManuByTenantUID(String tenantUid,String name,int size);
    @Query(value= "select su.* from supplier su where tenant_uid=?1 and manufacturer_t is true  order by  name_py_code  asc  limit ?2 " ,nativeQuery = true)
    List<Supplier> findManuByTenantUID(String tenantUid,int size);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and maintainer_t is true " ,nativeQuery = true)
    List<Supplier> findMaintainByTenantUID(String tenantUid);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and maintainer_t is true   and  name like %?2%  order by  name_py_code  asc  limit ?3 " ,nativeQuery = true)
    List<Supplier> findMaintainByTenantUID(String tenantUid,String name,int size);

    @Query(value= "select su.* from supplier su where tenant_uid=?1 and maintainer_t is true order by  name_py_code  asc   limit ?2 " ,nativeQuery = true)
    List<Supplier> findMaintainByTenantUID(String tenantUid,int size);

    public Supplier getBySiteIdAndName(int siteId, String name);
    
    public Supplier findByUid(String uid);

    Supplier findBySiteIdAndName(Integer siteId, String name);
    
    @Query(value="select o.* from supplier o where o.uid=?1 limit 1",nativeQuery=true)
    public Supplier getByUid(String uid);

    @Query(value="select o.* from supplier o where o.id=?1 limit 1",nativeQuery=true)
    public Supplier getById(int id);

    public List<Supplier> findBySupplierTAndTenantUID(Boolean supplierT, String tenantUid);
    public List<Supplier> findByMaintainerTAndTenantUID(Boolean maintainerT, String tenantUid);
    public List<Supplier> findByManufacturerTAndTenantUID(Boolean manufacturerT, String tenantUid);

    public Supplier findFirstByTenantUIDAndName(String tenantUid, String name);

    List<Supplier> findByProductRegistrationCodeAndTenantUIDAndIdNot(String productRegistrationCode, String tenantUID, Integer id);

    List<Supplier> findBySupplierCodeAndTenantUIDAndIdNot(String supplierCode, String tenantUID, Integer id);

    Supplier findByTenantUIDAndName(String tenantUid, String name);
    List<Supplier> findByTenantUIDAndNameAndSupplierT(String tenantUid, String name,Boolean supplierT);
    List<Supplier> findByTenantUIDAndNameAndManufacturerT(String tenantUid, String name,Boolean manufacturerT);

    List<Supplier> findByIdIn(Collection<Integer> ids);

    List<Supplier> findByUidIn(Collection<String> uidList);

    List<Supplier> findByTenantUIDAndVenderCompanyIdIsNotNull(String tenantUid);

    Supplier findByTenantUIDAndVenderCompanyId(String tenantUid, String venderCompanyId);

    @Query(value = "select * from supplier where tenant_uid = ?1 and supplier_t is true and name = ?2 limit 1", nativeQuery = true)
    Supplier findBySupplierTAndTenantUIDAndName(String tenantUid, String name);

    @Query(value = "select * from supplier where tenant_uid = ?1 and maintainer_t is true and name = ?2 limit 1", nativeQuery = true)
    Supplier findByMaintainerTAndTenantUIDAndName(String tenantUid, String name);

    @Query(value = "select * from supplier where tenant_uid = ?1 and manufacturer_t is true and name = ?2 limit 1", nativeQuery = true)
    Supplier findByManufacturerTAndTenantUIDAndName(String tenantUid, String name);
}
