package com.ge.apm.view.inventory;

import com.ge.apm.view.sysutil.UrlEncryptController;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class InventoryOrderTabController {

    Logger logger = Logger.getLogger(InventoryOrderTabController.class.getName());

    private int queryTab;

    @PostConstruct
    public void init() {
        String enCodeParameters = WebUtil.getRequestParameter("str");
        String queryTab = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryTab");
        if (StringUtils.isNotEmpty(queryTab)) {
            try {
                this.queryTab = Integer.parseInt(queryTab);
            } catch (NumberFormatException e) {
                logger.info("queryTab is not a number");
                this.queryTab = 0;
            }
        } else {
            this.queryTab = 0;
        }
    }


    public int getQueryTab() {
        return queryTab;
    }

    public void setQueryTab(int queryTab) {
        this.queryTab = queryTab;
    }
}
