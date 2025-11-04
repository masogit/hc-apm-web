/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisEquipmentDataSource;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 *
 * @author
 */
public interface HisEquipmentDataSourceRepository extends GenericRepositoryUUID<HisEquipmentDataSource>{

    @Query("select o from HisEquipmentDataSource o ")
    public List<HisEquipmentDataSource> findHisEquipmentDataSourceAll();

    @Transactional
    @Modifying
    @Query(value="delete from his_equipment_data_source o where o.id = ?1 ",nativeQuery = true)
    public void deleteById(String id);

    @Query("select o from HisEquipmentDataSource o  where o.id in ?1 ")
    public List<HisEquipmentDataSource> findByIds(List<String> ids);

    @Transactional
    @Modifying
    @Query(value="update his_equipment_data_source set is_matched = true where id in ?1 ",nativeQuery = true)
    public void updateHisEquipmentDataSourceByIds(List<String> ids);


    @Query(value = "select * from his_equipment_data_source order by exec_time desc limit 1",nativeQuery = true)
    HisEquipmentDataSource selectOneDateDesc();


    @Query(value = "select * from his_equipment_data_source  where org_code =?1  order by exec_time desc limit 1",nativeQuery = true)
    HisEquipmentDataSource selectByOrgCodeOneDateDesc(String orgCode);


    @Query(value = "select max(id) as id,org_code,max(exec_time) as exec_time from his_equipment_data_source group by org_code",nativeQuery = true)
    List<HisEquipmentDataSource> selectByOrgCodeAndExecTime();


    @Query(value = "select max(exec_time)  from his_equipment_data_source where tenant_uid=?1",nativeQuery = true)
    Date getTenantMaxExecDate(String tenantUID);


}
