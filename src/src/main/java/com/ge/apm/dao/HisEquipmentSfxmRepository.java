/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.his.HisEquipmentSfxm;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author
 */
public interface HisEquipmentSfxmRepository extends GenericRepositoryUUID<HisEquipmentSfxm> {
//    public interface HisEquipmentConnectRepository extends GenericRepository<HisEquipmentConnect> {

    @Query("select o from HisEquipmentSfxm o ")
    public List<HisEquipmentSfxm> findHisEquipmentConnectAll();

    @Query("select o from HisEquipmentSfxm o where o.sfxmLb = ?1 and o.sfxmName like concat('%',?2,'%')")
    public List<HisEquipmentSfxm> findHisEquipmentSfxmByName(String departNum2, String name);

    @Query("select o from HisEquipmentSfxm o  where o.id in ?1 ")
    public List<HisEquipmentSfxm> findByIds(List<String> ids);

    @Modifying
    @Transactional
    @Query("update HisEquipmentSfxm set isMatched = ?1 where id = ?2 ")
    public void updateIsMatched(boolean is_matched, String id);

    HisEquipmentSfxm findBySfxmIdAndHosId(String sfxmId, String hosId);
    HisEquipmentSfxm findBySfxmIdAndHosIdAndSiteUid(String sfxmId, String hosId,String siteUid);

    @Query(value = "select * from his_equipment_sfxm where tenant_uid=?1", nativeQuery = true)
    List<HisEquipmentSfxm> findByTenantUid(String tenantUid);

    @Query(value = "select * from his_equipment_sfxm where tenant_uid=?1 and site_uid=?2", nativeQuery = true)
    List<HisEquipmentSfxm> findByTenantUidAndSiteUid(String tenantUid, String siteUid);

//    @Modifying
//    @Transactional
//    @Query(value = "update AssetInfo set nationalStandardCode=?1 where uid=?2")
//    public void updateStandardCode(String name, String uid);

}
