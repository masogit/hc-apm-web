/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisEquipmentConnect;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author
 */
public interface HisEquipmentConnectRepository extends GenericRepositoryUUID<HisEquipmentConnect> {
//    public interface HisEquipmentConnectRepository extends GenericRepository<HisEquipmentConnect> {

//    @Query("select o from HisEquipmentConnect o where o.isMatched = false ")
    @Query(value= " select u.* from his_equipment_connect u where u.is_matched = false LIMIT 100 ",nativeQuery=true)
    public List<HisEquipmentConnect> findHisEquipmentConnectLimit();

    @Query("select o from HisEquipmentConnect o where o.eqLb = ?1 and o.eqName like concat('%',?2,'%')")
    public List<HisEquipmentConnect> findHisEquipmentConnectByName(String departNum2,String name);

    @Transactional
    @Modifying
    @Query(value="update his_equipment_connect set is_matched = true where id in ?1 ",nativeQuery = true)
    public void updateHisEquipmentConnectByIds(List<String> ids);

    @Transactional
    @Modifying
    @Query(value="update his_equipment_connect set is_matched = false where id = ?1 ",nativeQuery = true)
    public void updateHisEquipmentConnectById(String id);

    @Query(value="select o from HisEquipmentConnect o where o.isMatched = true and o.id in ?1 ")
    public List<HisEquipmentConnect> findHisEquipmentConnectByIds(List<String> ids);

    HisEquipmentConnect findByEqId(String eqId);
}
