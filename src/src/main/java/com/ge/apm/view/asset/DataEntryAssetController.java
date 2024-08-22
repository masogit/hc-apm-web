package com.ge.apm.view.asset;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class DataEntryAssetController extends DataEntryController{
    @Override
    protected void init() {
        super.init();
        type = 2;
    }


}
