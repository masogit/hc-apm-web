/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.ArrivalList;
import java.util.Collection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;
/**
 *
 * @author 212579464
 */
public interface ArrivalListRepository extends GenericRepositoryUUID<ArrivalList>{
    
    
    List<ArrivalList> findByContractItemIdIn(Collection<String> itemId);

    List<ArrivalList> findBySupplierId(Integer supplierId);
    ArrivalList findByName(String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE arrival_list SET invoice_num=?1 WHERE id=?2", nativeQuery = true)
    public void updateInvoiceNum(String invoiceNum, String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE arrival_list SET vendor=?1,supplier_id=?2,supplier_user_id=?3 WHERE id=?4 AND status!=?5", nativeQuery = true)
    public void updateSupplierUser(String vendor, Integer supplierId, Integer SupplierUserId, String id,int status);

    @Query(value = "select list.id from arrival_list list where (list.tenant_uid in(?1) or list.site_uid in(?1) or list.institution_uid in(?1) or list.hospital_uid in(?1)) and list.status in(1,2) and list.id not in(select list.id from arrival_list_item item where list.id=item.arrival_list_id and list.status in(2) and item.install_status in('instorage','finished'))",nativeQuery = true)
    public List<String> getArrivalListNotFinishedIds(List<String> orgUidList);
    @Query(value = "select distinct(list.id) from arrival_list list,arrival_list_item item where list.id=item.arrival_list_id and (list.tenant_uid in(?1) or list.site_uid in(?1) or list.institution_uid in(?1) or list.hospital_uid in(?1)) and list.status=2 and item.install_status in('instorage','finished')",nativeQuery = true)
    public List<String> getArrivalListPartiallyFinishedIds(List<String> orgUidList);
    @Query(value = "select list.hospital_uid,list.institution_uid,list.site_uid,list.tenant_uid from arrival_list list where id = ?1}",nativeQuery = true)
    public List<String> getArrivalListHILT(String id);
    @Query(value = "select * from arrival_list list where id = ?1}",nativeQuery = true)
    public ArrivalList getById(String id);
}
