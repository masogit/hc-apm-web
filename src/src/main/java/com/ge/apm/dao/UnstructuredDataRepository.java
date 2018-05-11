/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.domain.UnstructuredDataCollection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;


public interface UnstructuredDataRepository extends GenericRepositoryUUID<UnstructuredData> {


	@Transactional
	@Modifying
	@Query(value = "update unstructured_data  set value = ?2 where id = ?1",nativeQuery =true)
	public void updateUnData(String id,String value);
        
        public List<UnstructuredData> findByCollectionId(String collectionId);
}
