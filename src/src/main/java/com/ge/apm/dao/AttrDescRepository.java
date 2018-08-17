package com.ge.apm.dao;

import com.ge.apm.domain.AttrDesc;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AttrDescRepository extends GenericRepositoryUUID<AttrDesc> {

    List<AttrDesc> findByName(String name);

    List<AttrDesc> findByAttributeId(String attributeId);

}
