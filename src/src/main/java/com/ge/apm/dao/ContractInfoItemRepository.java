/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.ContractInfoItem;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;
/**
 *
 * @author 212579464
 */
public interface ContractInfoItemRepository extends GenericRepositoryUUID<ContractInfoItem>{

   List<ContractInfoItem>  findByContractId(String id);
   
   List<ContractInfoItem>  findByAssetUid(String id);
   
   ContractInfoItem getByArrivalItemId(String id);

   @Query("select cii from ContractInfoItem cii, ContractInfo ci where cii.contractId = ci.id and cii.assetUid = ?1 and ci.contractType not in ('ZZB','ZYB','ZXB','Parts')")
   List<ContractInfoItem> getByAssetUidAndContractType(String assetUid);

   List<ContractInfoItem> findByContractIdAndAssetUid(String contractId, String assetUid);
}
