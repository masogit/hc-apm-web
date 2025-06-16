/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.AssetOutStockRecord;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetOutStockRecordRepository extends GenericRepositoryUUID<AssetOutStockRecord>{
    void deleteByArrivalItemIdIn(List<String> arrivalItemIds);
}
