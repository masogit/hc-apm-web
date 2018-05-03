package com.ge.apm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.CaptureDataMessages;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface CaptureDataMessagesRepository extends GenericRepositoryUUID<CaptureDataMessages> {

	@Query(value="select o.* from capture_data_messages o where o.system_id=?1 and o.type=0 order by o.index desc fetch first 1 rows only",nativeQuery=true)
	public CaptureDataMessages findBySystemId(String systemId);
	
	@Query(value="select o.* from capture_data_messages o where o.ge_device_type=?1 and o.type=1 order by o.index desc fetch first 1 rows only",nativeQuery=true)
	public CaptureDataMessages findByGeDeviceType(Integer geDeviceType);
	
	@Query("select o.content from CaptureDataMessages o where o.id=?1")
	public String getContentById(String id);
}
