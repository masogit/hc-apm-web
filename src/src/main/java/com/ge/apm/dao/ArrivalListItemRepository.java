/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.ArrivalListItem;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author 212579464
 */
public interface ArrivalListItemRepository extends GenericRepositoryUUID<ArrivalListItem>{

    public List<ArrivalListItem> findByArrivalListId(String arrivalListId);

    @Query(value = "select listItem.* from arrival_list list,arrival_list_item listItem where list.id=listItem.arrival_list_id and list.contract_num=?1 and listItem.clinical_dept_uid=?2 and listItem.install_status=?3",nativeQuery=true)
    List<ArrivalListItem> getArrivalListItem(String contractNum,String clinicalDeptUID,String installStatus);
}
