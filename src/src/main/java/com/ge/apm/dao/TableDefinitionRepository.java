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
 * @author 212579464Un
 */
public interface TableDefinitionRepository extends GenericRepositoryUUID<TableDefinition>{
    
    
    @Query("select o from TableDefinition o where o.siteUID in ?1 and o.status='Released'")
    public List<TableDefinition> getBySiteUIDIn(Collection<String> orgUids);

    @Query("select o from TableDefinition o where o.tenantUID = ?1 and o.status='Released'")
	public List<TableDefinition> findByTenantUID(String tenantUID);

        @Query("select o from TableDefinition o where o.baseId = ?1 and o.status='Released'")
        public TableDefinition findByBaseId(String baseId);
    public TableDefinition findById(String ID);

    public List<TableDefinition> getByBaseId(String baseId);

	@Query(value = "select td.* from table_definition td where td.status='Released' and td.base_id in (\n" +
			"\tselect performance_report from pm_order_rule where uid=?1 union select safety_report from pm_order_rule where uid=?1)",nativeQuery = true)
	public List<TableDefinition> findTableName(String pmOrderRuleId);
	
	@Query(value = "select td.* from table_definition td where td.status='Released' and td.base_id in (select safety_report from pm_order_rule where uid=?1)",nativeQuery = true)
	public List<TableDefinition> findSafetyReportTable(String pmOrderRuleId);
}
