package com.ge.apm.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.ElectricSafetyData;

import webapp.framework.dao.GenericRepositoryUUID;

public interface ElectricSafetyDataRepository extends GenericRepositoryUUID<ElectricSafetyData> {
	
	@Query("select o.id from ElectricSafetyData o where o.financingNum =?1 and o.checkDate =?2")
	public String findIdByFinancingNumAndCheckDate(String financingNum,Date checkDate);
	
	@Query("select o from ElectricSafetyData o where o.financingNum =?1 and o.checkDate =?2")
	public ElectricSafetyData findByFinancingNumAndCheckDate(String financingNum,Date checkDate);
	
	@Query("select 1 from ElectricSafetyData o where o.financingNum =?1 and o.checkDate =?2")
	public Integer findExistsByFinancingNumAndCheckDate(String financingNum,Date checkDate);
}
