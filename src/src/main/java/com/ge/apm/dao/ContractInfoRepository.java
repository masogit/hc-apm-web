/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.V2_WorkOrder;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 *
 * @author 212579464
 */
public interface ContractInfoRepository extends GenericRepositoryUUID<ContractInfo> {

    @Query("select o from ContractInfo o where o.tenantUID = ?1 and o.contractType = 'Equipment'")
    public List<ContractInfo> findEquipmentContractByTenant(String tenantUID);

    public ContractInfo findByIdAndContractTypeAndTenantUID(String id, String contractType, String tenantUID);
    
    public List<ContractInfo> findByContractNumAndTenantUID(String id, String tenantUID);
    
    @Query("select o from ContractInfo o where o.contractNum in ?1 and o.tenantUID = ?2 and o.contractType = 'Parts'")
    public List<ContractInfo> findByContractNumIn(Collection<String> numList,String tenantUID);
    
}
