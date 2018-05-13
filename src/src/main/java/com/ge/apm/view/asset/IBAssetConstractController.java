/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetContract;
import java.util.Arrays;
import webapp.framework.web.mvc.JpaCRUDController;

/**
 *
 * @author 212579464
 */
public class IBAssetConstractController extends AssetContractController{
    
    
    @Override
    protected void init() {
        super.init();
        this.contractType =Arrays.asList(11,12,13);
    }

    @Override
    protected Class<AssetContract> getEntityClass() {
        return AssetContract.class;
//        return super.getEntityClass(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Class<AssetContract> getBeanClass() {
        return AssetContract.class;
    }
    
    
}
