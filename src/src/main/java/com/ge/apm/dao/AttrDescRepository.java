package com.ge.apm.dao;

import com.ge.apm.domain.AttrDesc;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AttrDescRepository extends GenericRepositoryUUID<AttrDesc> {

    List<AttrDesc> findByName(String name);

    List<AttrDesc> findByAttributeId(String attributeId);

    @Modifying
    @Transactional
    @Query("delete from AttrDesc where attributeId = :attributeId")
    void deleteAttrDescByAttributeId(@Param("attributeId") String attributeId);

}
