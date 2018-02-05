package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import com.ge.apm.domain.TubeData;
import webapp.framework.dao.GenericRepository;

public interface TubeDataRepository extends GenericRepository<TubeData> {

//	@Query(value = 
//			"select asset_uid,seria_num,max(input_date) input_date," + 
//			"max(patient_exam) patient_exam,max(tube_mas) tube_mas,max(tube_scan) tube_scan" + 
//			" from tube_data where ge_device_type = 22 and tenant_uid =?1" + 
//			" group by asset_uid,seria_num order by input_date desc \\n-- #pageable#\\n" , nativeQuery = true)
	
	
	@Query("select new com.ge.apm.domain.TubeData(max(td.id) as id,"
			+ "td.assetUid,td.seriaNum,max(td.patientExam),max(td.tubeMAS),max(td.tubeScan),max(td.inputDate) as inputDate) "
			+ "from TubeData td where td.tenantUID = ?1 group by td.assetUid,td.seriaNum order by inputDate desc")
	Page<TubeData> findByCondition(String tenantUID, Pageable pageable);

}
