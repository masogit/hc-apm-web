package com.ge.apm.view.asset;

import lombok.Getter;
import lombok.Setter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@ManagedBean
@ViewScoped
public class AssetUnOutStockController extends AssetOutStockController{



    @Override
    protected void init() {
        super.init();
        tabType = TAB_TYPE_UN_OUT_STOCK;
    }



}
