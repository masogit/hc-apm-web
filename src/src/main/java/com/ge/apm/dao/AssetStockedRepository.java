/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.dao;

import com.ge.apm.domain.AssetStocked;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetStockedRepository extends GenericRepositoryUUID<AssetStocked> {
    List<AssetStocked> findByInStockNo(String inStockNo);

    List<AssetStocked> findByOutStockNo(String inStockNo);

    List<AssetStocked> findByArrivalListId(String arrivalListId);
}
