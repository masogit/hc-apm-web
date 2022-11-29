/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisEquipmentDataResult;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author
 */
public interface HisEquipmentDataResultRepository extends GenericRepositoryUUID<HisEquipmentDataResult>{

    @Query("select o from HisEquipmentDataResult o ")
    public List<HisEquipmentDataResult> findHisEquipmentDataResultAll();

    @Transactional
    @Modifying
    @Query(value="delete from his_equipment_data_source o where o.id = ?1 ",nativeQuery = true)
    public void deleteById(String id);

    @Query("select o from HisEquipmentDataResult o  where o.id in ?1 ")
    public List<HisEquipmentDataResult> findByIds(List<String> ids);
}
