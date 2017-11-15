/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import webapp.framework.dao.GenericRepositoryUUID;
import com.ge.apm.domain.ArrivalListItem;
import java.util.List;
/**
 *
 * @author 212579464
 */
public interface ArrivalListItemRepository extends GenericRepositoryUUID<ArrivalListItem>{

    public List<ArrivalListItem> findByArrivalListId(String arrivalListId);
    
}
