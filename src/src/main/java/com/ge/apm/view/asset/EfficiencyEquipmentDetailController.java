package com.ge.apm.view.asset;


import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AeMapper;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.domain.MvsAssetInfo;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.utils.MvsUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class EfficiencyEquipmentDetailController  implements Serializable {

    @Getter
    @Setter
    private String assetUid;

    @PostConstruct
    protected void init() {

        String encodeStr = WebUtil.getRequestParameter("str");
        String assetUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetUid");
        this.assetUid=assetUid;

    }
}
