package com.ge.apm.dao;


import com.ge.apm.domain.InventoryIssue;

import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 212615494 on 8/11/2017.
 */
public interface InventoryIssueRepository extends GenericRepositoryUUID<InventoryIssue> {
//    @Modifying
//    @Query("update InventoryIssue i set i.isSolved = true, i.lastModifiedBy = ?2, i.lastModifiedDate = CURRENT_TIMESTAMP where i.id = ?1")
//    void resolveInventoryIssue(String id, String username);
	
	
}
