/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisEquipmentSfxmMatch;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

/**
 *
 * @author
 */
public interface HisEquipmentSfxmMatchRepository extends GenericRepositoryUUID<HisEquipmentSfxmMatch> {
//    public interface HisEquipmentConnectRepository extends GenericRepository<HisEquipmentConnect> {

    @Query("select o from HisEquipmentSfxmMatch o ")
    public List<HisEquipmentSfxmMatch> findHisEquipmentSfxmMatchAll();

    @Query("select o from HisEquipmentSfxmMatch o where o.sfxmLb = ?1 and o.sfxmName like concat('%',?2,'%')")
    public List<HisEquipmentSfxmMatch> findHisEquipmentSfxmMatchByName(String departNum2, String name);

    @Query("select o from HisEquipmentSfxmMatch o  where o.id in ?1 ")
    public List<HisEquipmentSfxmMatch> findByIds(List<String> ids);

    @Query("select o from HisEquipmentSfxmMatch o  where o.sfxmId in ?1 ")
    public List<HisEquipmentSfxmMatch> findBySfxmId(String sfxmIds);

    @Query("select o from HisEquipmentSfxmMatch o  where o.sfxmId in ?1 ")
    public List<HisEquipmentSfxmMatch> findHisEquipmentSfxmMatchBySfxmIds(List<String> sfxmIds);

    @Query("select o.sfxmId from HisEquipmentSfxmMatch o  where o.clinicalDeptUid in ?1 ")
    public List<String> findByClinicalDeptUids(Set<String> clinicalDeptUids);

    @Query("select o.clinicalDeptUid from HisEquipmentSfxmMatch o  where o.sfxmId in ?1 ")
    public List<String> findBySfxmIds(Set<String> sfxmIds);

    @Query("select o from HisEquipmentSfxmMatch o  where o.hisGroupMatchUid = ?1 ")
    public List<HisEquipmentSfxmMatch> findByHisGroupMatchUid(String hisGroupMatchUid);

    @Query("select o from HisEquipmentSfxmMatch o  where o.sfxmId in ?1 and o.clinicalDeptUid in ?2 ")
    public List<HisEquipmentSfxmMatch> findBySfxmIdsAndClinicalDeptUids(Set<String> sfxmIds, Set<String> clinicalDeptUids);
}
