package com.ge.apm.view.pm;

import com.ge.apm.view.sysutil.UrlEncryptController;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PmOrderTabController {
    private int queryTab;

    @PostConstruct
    public void init() {
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            String queryTab = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryTab");
            if (StringUtils.isNotEmpty(queryTab)) {
                this.queryTab = Integer.parseInt(queryTab);
            } else {
                this.queryTab = 0;
            }

        }
    }

    public int getQueryTab() {
        return queryTab;
    }

    public void setQueryTab(int queryTab) {
        this.queryTab = queryTab;
    }


}
