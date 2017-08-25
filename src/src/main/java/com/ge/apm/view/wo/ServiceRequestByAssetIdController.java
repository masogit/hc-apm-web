/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class ServiceRequestByAssetIdController extends ServiceRequestController implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isClosed;
    private Integer assetId;

    @Override
    protected void init() {
        super.init();
        try{
            assetId=Integer.valueOf(WebUtil.getRequestParameter("assetId"));
        }
        catch(Exception ex){
            assetId = -1;
        }
    }

    @Override
    protected Page<V2_ServiceRequest> loadData(PageRequest pageRequest) {
        this.setSiteFilter();

        if(assetId!=-1){
            searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.EQ, assetId));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1)); //query Closed service request
        }
        
        this.selected = null;
        this.workOrders = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

}
