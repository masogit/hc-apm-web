package com.ge.apm.view.asset;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class AssetOutStockedController extends AssetOutStockController{

    @Override
    protected void init() {
        super.init();
        tabType = TAB_TYPE_OUT_STOCKED;
    }
}
