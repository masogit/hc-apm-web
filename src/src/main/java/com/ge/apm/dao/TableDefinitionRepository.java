/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.TableDefinition;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
/**
 *
 * @author 212579464
 */
public interface TableDefinitionRepository extends GenericRepositoryUUID<TableDefinition>{
    
//    @Query("select o from TableDefinition o where o.baseId = ?1 and versionNum=(select max(versionNum) from TableDefinition o2 where o2.baseId=?1)")
//    public TableDefinition getLatestTemplateByBaseId(String baseId);
    
    @Query("select o from TableDefinition o where o.siteUID in ?1 and o.status='Released'")
    public List<TableDefinition> getBySiteUIDIn(Collection<String> orgUids);
}
