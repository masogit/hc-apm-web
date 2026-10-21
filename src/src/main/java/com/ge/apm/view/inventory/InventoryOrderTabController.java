package com.ge.apm.view.inventory;

import com.ge.apm.view.sysutil.UrlEncryptController;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class InventoryOrderTabController {

    Logger logger = Logger.getLogger(InventoryOrderTabController.class.getName());

    private String queryTab;
    private int activeIndex;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        queryTab = request.getParameter("queryTab");
        if (StringUtils.isNotEmpty(queryTab)) {
            try {
                this.activeIndex = Integer.parseInt(queryTab);
            } catch (NumberFormatException e) {
                logger.info("queryTab is not a number");
                this.activeIndex = 0;
            }
        } else {
            this.activeIndex = 0;
        }
    }

    public String getQueryTab() {
        return queryTab;
    }

    public void setQueryTab(String queryTab) {
        this.queryTab = queryTab;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }
}
