/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAdministrativeOffices;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author
 */
public interface HisAdministrativeOfficesRepository extends GenericRepositoryUUID<HisAdministrativeOffices>{

    @Query("select o from HisAdministrativeOffices o where isMatched = false")
    public List<HisAdministrativeOffices> findHisAdministrativeOfficesAllFalse();

    @Transactional
    @Modifying
    @Query(value="delete from his_administrative_offices o where o.id = ?1 ",nativeQuery = true)
    public void deleteById(String id);

}
