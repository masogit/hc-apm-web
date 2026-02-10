/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisEquipmentMatch;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author
 */
public interface HisEquipmentMatchRepository extends GenericRepositoryUUID<HisEquipmentMatch>{

    @Query("select o from HisEquipmentMatch o ")
    public List<HisEquipmentMatch> findHisEquipmentMatchAll();

    @Transactional
    @Modifying
    @Query(value="delete from his_equipment_match o where o.id = ?1 ",nativeQuery = true)
    public void deleteById(String id);

    @Query("select o from HisEquipmentMatch o  where o.id in ?1 ")
    public List<HisEquipmentMatch> findByIds(List<String> ids);



    @Query(value="select * from his_equipment_match  where asset_uid = ?1 ",nativeQuery = true)
    public List<HisEquipmentMatch> findByAssetUid(String id);
}
