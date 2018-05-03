/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.CustomConfig;
import java.util.List;
import webapp.framework.dao.GenericRepositoryUUID;
/**
 *
 * @author 212579464
 */
public interface CustomConfigRepository extends GenericRepositoryUUID<CustomConfig>{
    
    CustomConfig findBySiteUID(String siteUID);
    
    List<CustomConfig>  findBySiteUIDIn(List<String> orgUid);
}
