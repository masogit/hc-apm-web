/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.ContractInfo;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 *
 * @author 212579464
 */
public interface ContractInfoRepository extends GenericRepositoryUUID<ContractInfo> {

    @Query(value = "select ci.* from contract_info_item cii inner join contract_info ci on ci.id = cii.contract_id where cii.asset_uid = ?1 and ci.start_date <= now() and ci.end_date >= now() and ci.contract_type in ('Full','Artificial')", nativeQuery = true)
    List<ContractInfo> findByAssetUidAndRequestTime(String assetUid);

    @Query("select o from ContractInfo o where o.tenantUID = ?1 and o.contractType = 'Equipment'")
    public List<ContractInfo> findEquipmentContractByTenant(String tenantUID);
    
    @Query("select o from ContractInfo o where o.siteUID = ?1 and o.contractType = 'Equipment'")
    public List<ContractInfo> findEquipmentContractBySite(String siteUID);
    
    @Query("select o from ContractInfo o where o.hospitalUID = ?1 and o.contractType = 'Equipment'")
    public List<ContractInfo> findEquipmentContractByHospital(String hospitalUID);
    
    @Query("select o from ContractInfo o where o.institutionUID = ?1 and o.contractType = 'Equipment'")
    public List<ContractInfo> findEquipmentContractByInstitution(String institutionUID);

    public ContractInfo findByIdAndContractTypeAndTenantUID(String id, String contractType, String tenantUID);
    
    public List<ContractInfo> findByContractNumAndTenantUID(String id, String tenantUID);
    
    @Query("select o from ContractInfo o where o.contractNum in ?1 and o.tenantUID = ?2 and o.contractType = 'Parts'")
    public List<ContractInfo> findByContractNumIn(Collection<String> numList,String tenantUID);

    @Query("select o from ContractInfo o where o.contractNum = ?1 and o.tenantUID = ?2 and o.contractType = 'Equipment'")
    public ContractInfo findEquipmentByContractNum(String contractNum, String tenantUID);

    @Modifying
    @Query("update ContractInfo o set o.contractStatus = ?2 where o.id = ?1")
    public int updateContractStatus(String id, String status);

}
