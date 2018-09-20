package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.AssetInfo;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SharedAssetBatchLeaseController extends MyCRUDController<AssetInfo> {

    @Override
    protected BaseMapper getDAO() {
        return null;
    }

}
