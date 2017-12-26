/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;
import com.ge.apm.domain.V2_WorkOrderCoworker;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 *
 * @author 212579464
 */
public interface V2_WorkOrderCoworkerRepository extends GenericRepositoryUUID<V2_WorkOrderCoworker>{

    public List<V2_WorkOrderCoworker> findByWoId(String woId);
    
}
