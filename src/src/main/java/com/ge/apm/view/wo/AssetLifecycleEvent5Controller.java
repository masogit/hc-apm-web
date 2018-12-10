package com.ge.apm.view.wo;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import webapp.framework.dao.SearchFilter;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEvent5Controller extends AssetLifecycleEventController {

	
    protected void addFilter(){
    	searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "5"));//3外部维修
    }
}