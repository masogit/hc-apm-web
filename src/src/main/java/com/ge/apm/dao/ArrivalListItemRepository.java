/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.ArrivalListItem;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author 212579464
 */
public interface ArrivalListItemRepository extends GenericRepositoryUUID<ArrivalListItem>{

    public List<ArrivalListItem> findByArrivalListId(String arrivalListId);

    List<ArrivalListItem> findByArrivalListIdOrderByFinancingNumAscItemNumAsc(String arrivalListId);

    List<ArrivalListItem> findByIdIn(Collection<String> ids);

    List<ArrivalListItem> findByInStockNo(String inStockNo);

    List<ArrivalListItem> findByOutStockNo(String inStockNo);

    List<ArrivalListItem> findByArrivalListIdIn(Collection<String> arrivalListIds);

    @Query(value = "select listItem.* from arrival_list list,arrival_list_item listItem where list.id=listItem.arrival_list_id and list.contract_num=?1 and listItem.clinical_dept_uid=?2 and listItem.install_status=?3 order by listItem.function_type",nativeQuery=true)
    List<ArrivalListItem> getArrivalListItem(String contractNum, String clinicalDeptUID, String installStatus);

    @Query(value = "select distinct(list.contract_num) from arrival_list list,arrival_list_item item where list.id=item.arrival_list_id and (list.site_uid in(?1) or list.tenant_uid in(?1) or list.institution_uid in(?1) or list.hospital_uid in(?1) or item.current_person_uid=?2 ) and list.process_type='Normal' and list.supplier_status =?3 and list.contract_num !='' and item.install_status='new'",nativeQuery=true)
    List<String> getArrivalListNotInstalledContractNum(List<String> orgUidList, String userUid, String supplierStatus);

    @Modifying
    @Transactional
    @Query(value = "UPDATE arrival_list_item SET comments=?1 WHERE arrival_list_id=?2", nativeQuery = true)
    public void updateComments(String comments, String arrivalListId);

    @Query(value = "select asset_uid from arrival_list_item where arrival_list_id=?1",nativeQuery=true)
    List<String> getAssetUid(String arrivalListId);

    @Query(value = "select * from arrival_list_item t1 inner join arrival_list t2 on t1.arrival_list_id = t2.id where t2.asset_name = ?1 and t2.function_type = ?2 and t2.tenant_uid = ?3 and t1.arrival_list_id != ?4 order by t1.created_date desc limit 1",nativeQuery=true)
    ArrivalListItem getLastItemByNameAndModel(String assetName, String functionType, String tenantUid, String arrivalListId);



    @Query(value = "select listItem.* from  arrival_list_item listItem where asset_uid=?1 ",nativeQuery=true)
    ArrivalListItem getArrivalListItemByAssetUid(String assetUid);

    ArrivalListItem findByQrCode(String qrCode);

    @Query(value = "select RIGHT(max(in_stock_no), 3) from arrival_list_item ali, arrival_list al where al.tenant_uid = ?1 and ali.arrival_list_id = al.id and ali.in_stock_no like ?2",nativeQuery = true)
    String queryInStockNo(String tenantUid, String inStockNo);

    @Query(value = "select RIGHT(max(out_stock_no),3) from arrival_list_item ali, arrival_list al where al.tenant_uid = ?1 and ali.arrival_list_id = al.id and ali.out_stock_no like ?2",nativeQuery = true)
    String queryOutStockNo(String tenantUid, String outStockNo);
    List<ArrivalListItem> findBySiteUidAndOutStockNoAndOutStockNoNotNull(String siteUid, String outStockNo);
}
