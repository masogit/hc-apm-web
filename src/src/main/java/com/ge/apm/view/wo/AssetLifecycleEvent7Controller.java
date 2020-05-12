package com.ge.apm.view.wo;

import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.dao.SearchFilter;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEvent7Controller extends AssetLifecycleEventController {

    protected void init() {
        super.init();
        tabStatus = 9;
        searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, tabStatus));
    }

}