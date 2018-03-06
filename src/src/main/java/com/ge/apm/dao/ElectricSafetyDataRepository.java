package com.ge.apm.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.ElectricSafetyDetail;

import webapp.framework.dao.GenericRepositoryUUID;

public interface ElectricSafetyDataRepository extends GenericRepositoryUUID<ElectricSafetyDetail> {
	
	@Query("select o.id from ElectricSafetyDetail o where o.financingNum =?1 and o.checkDate =?2")
	public String findIdByFinancingNumAndCheckDate(String financingNum,Date checkDate);
	
	@Query("select o from ElectricSafetyDetail o where o.financingNum =?1 and o.checkDate =?2")
	public ElectricSafetyDetail findByFinancingNumAndCheckDate(String financingNum,Date checkDate);
	
	@Query("select 1 from ElectricSafetyDetail o where o.financingNum =?1 and o.checkDate =?2")
	public Integer findExistsByFinancingNumAndCheckDate(String financingNum,Date checkDate);
}
