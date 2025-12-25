/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAdministrativeOfficesMatch;
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
public interface HisAdministrativeOfficesMatchRepository extends GenericRepositoryUUID<HisAdministrativeOfficesMatch>{

    @Query("select o from HisAdministrativeOfficesMatch o ")
    public List<HisEquipmentMatch> findHisEquipmentSfxmMatchAll();

    @Transactional
    @Modifying
    @Query(value="delete from his_administrative_offices_match o where o.id = ?1 ",nativeQuery = true)
    public void deleteById(String id);


    @Query("select o from HisAdministrativeOfficesMatch o where o.hisAdmId = ?1 and o.orgInfoUid =?2 ")
    public HisAdministrativeOfficesMatch findByHisAdmIdAndOrgInfoUid(String hisAdmId, String orgInfoUid);
}
