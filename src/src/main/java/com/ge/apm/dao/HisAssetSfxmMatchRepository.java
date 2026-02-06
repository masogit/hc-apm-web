/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisAssetSfxmMatch;
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
public interface HisAssetSfxmMatchRepository extends GenericRepositoryUUID<HisAssetSfxmMatch>{


    @Transactional
    @Modifying
    @Query(value="delete from his_asset_sfxm_match o where o.id = ?1 ",nativeQuery = true)
    public void deleteById(String id);

    @Query(value = "select * from his_asset_sfxm_match  where asset_uid in ?1 ",nativeQuery = true)
    public List<HisAssetSfxmMatch> findByAssetUids(List<String> ids);
}
