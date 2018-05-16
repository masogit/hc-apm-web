/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;
import com.ge.apm.domain.UnstructuredDataCollection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 *
 * @author 212579464
 */
public interface UnstructuredDataCollectionRepository extends GenericRepositoryUUID<UnstructuredDataCollection>{
    
    @Query(value = "select count(*) from UnstructuredDataCollection u where u.schemaId=?1")
    public Integer getCountByTemplate(String templateId);
    
    public List<UnstructuredDataCollection> findByBoUuid(String boUuid);

    @Query(value = "select * from unstructured_data_collection where bo_uuid=?1 and bo_type=?2",nativeQuery = true)
    public List<UnstructuredDataCollection> findCKByBoUuid(String boUuid,String boType);

    @Query(value = "select u from UnstructuredDataCollection u where u.id=?1")
    public UnstructuredDataCollection findByUid(String id);


    @Query(value = "select * from unstructured_data_collection where bo_uuid=?1 and bo_type != 'pm-checklist-report' ",nativeQuery = true)
    public List<UnstructuredDataCollection> findByBoUuidNoPMCheckList(String boUuid);



}
