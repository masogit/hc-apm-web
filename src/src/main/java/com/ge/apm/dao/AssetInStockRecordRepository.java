/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.AssetInStockRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetInStockRecordRepository extends GenericRepositoryUUID<AssetInStockRecord>{
    @Transactional
    @Modifying
    void deleteByArrivalItemIdIn(List<String> arrivalItemIds);
}
