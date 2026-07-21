package com.ge.apm.view.surgeryAnesthesia;


import com.ge.apm.view.sysutil.UrlEncryptController;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class SurgeryAnesthesiaEquipmentInfoDetailController implements Serializable {

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
