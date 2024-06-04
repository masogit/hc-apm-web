/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.ContractInfoItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;
/**
 *
 * @author 212579464
 */
public interface ContractInfoItemRepository extends GenericRepositoryUUID<ContractInfoItem>{

   List<ContractInfoItem>  findByContractId(String id);

   List<ContractInfoItem>  findByContractIdAndItemType(String id, String itemType);

   List<ContractInfoItem>  findByAssetUid(String id);
   
   ContractInfoItem getByArrivalItemId(String id);

   @Query("select cii from ContractInfoItem cii, ContractInfo ci where cii.contractId = ci.id and cii.assetUid = ?1 and ci.contractType not in ('ZZB','ZYB','ZXB','Parts')")
   List<ContractInfoItem> getByAssetUidAndContractType(String assetUid);

   List<ContractInfoItem> findByContractIdAndAssetUid(String contractId, String assetUid);

   @Query("select cii from ContractInfoItem cii where cii.contractId = ?1 and cii.id <> ?2 and cii.itemType = 'Purchase' and cii.contractAssetQuantity = 1 and cii.parentId is null")
   List<ContractInfoItem> findParentByPurchaseAndContractId(String contractId, String id);

   List<ContractInfoItem>  findByParentIdAndContractId(String parentId, String contractId);

   List<ContractInfoItem> findByPurchaseItemIdAndContractId(String purchaseItemId, String contractId);
   @Modifying
   @Transactional
   @Query(value = "delete from contract_info_item where id in ?1",nativeQuery = true)
   Integer deleteById(List<String> collect);
}
