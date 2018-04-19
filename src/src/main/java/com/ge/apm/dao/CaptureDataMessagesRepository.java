package com.ge.apm.dao;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.CaptureDataMessages;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface CaptureDataMessagesRepository extends GenericRepositoryUUID<CaptureDataMessages> {

	@Query("select o from CaptureDataMessages o where o.systemId=?1")
	public CaptureDataMessages findBySystemId(String systemId);
	
	@Query("select o.content from CaptureDataMessages o where o.id=?1")
	public String getContentById(String id);
}
