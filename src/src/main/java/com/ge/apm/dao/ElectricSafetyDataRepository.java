package com.ge.apm.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.ElectricSafetyData;

import webapp.framework.dao.GenericRepositoryUUID;

public interface ElectricSafetyDataRepository extends GenericRepositoryUUID<ElectricSafetyData> {
	
	@Query("select o.id from ElectricSafetyData o where o.financingNum =?1 and o.checkDate =?2 and o.siteUID=?3")
	public String findIdByFinancingNumAndCheckDate(String financingNum,Date checkDate,String siteUID);
	
	@Query("select o from ElectricSafetyData o where o.financingNum =?1 and o.checkDate =?2 and o.siteUID=?3")
	public ElectricSafetyData findByFinancingNumAndCheckDate(String financingNum,Date checkDate,String siteUID);
	
	@Query("select 1 from ElectricSafetyData o where o.financingNum =?1 and o.checkDate =?2 and o.siteUID=?3")
	public Integer findExistsByFinancingNumAndCheckDate(String financingNum,Date checkDate,String siteUID);
	
	@Query(value="select o.* from electric_safety_data o where o.v2_pm_order_id =?1 order by o.created_date desc limit 1",nativeQuery=true)
	public ElectricSafetyData findByPmOrderId(String pmOrderId);
	
	@Query(value="select o.* from electric_safety_data o where o.asset_uid in (select b.asset_uid from electric_safety_data b where b.v2_pm_order_id = ?1) order by o.last_modified_date desc limit 1",nativeQuery=true)
	public ElectricSafetyData findByPmOrderIdAssetUid(String pmOrderId);
	
	@Query(value="select o.* from electric_safety_data o where o.asset_uid =?1 order by o.last_modified_date desc limit 1",nativeQuery=true)
	public ElectricSafetyData findByAssetUid(String assetUid);
}
