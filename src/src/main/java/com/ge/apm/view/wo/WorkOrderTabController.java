package com.ge.apm.view.wo;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tabview.TabView;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;

import lombok.Getter;
import lombok.Setter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class WorkOrderTabController {
    private int queryTab;
    @Getter @Setter
    private boolean showInOrder;
    @Autowired
    UserContextService userContextService;
    @PostConstruct
    public void init() {
    	userContextService = WebUtil.getBean(UserContextService.class);
    	if(userContextService.isIBTenant()){
    		if(userContextService.hasAnyRole("AssetStaff,AssetHead")){
    			showInOrder=true;
    		}
    		else{
    			showInOrder=false;
    		}
    	}
    	else{
    		showInOrder=true;
    	}
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            String queryTab = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryTab");
            if (StringUtils.isNotEmpty(queryTab)) {
                try {
                    this.queryTab = Integer.parseInt(queryTab);
                } catch (NumberFormatException e) {
                    setTabIndexByTabId(queryTab);
                }
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

    private void setTabIndexByTabId(String tabId) {

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        boolean innerWorkOrder = false;
        if (!userContextService.isIBorUltraSoundIBEndUser() && !userContextService.isIBTenant()) {
            //内部工单显示了
            innerWorkOrder = true;
        }


        if ("outWorkOrder".equals(tabId)) {
            //现场保养可能是第一页或者第二页，要看内部保养是不是显示
            if (innerWorkOrder) {
                this.queryTab = 1;
            } else {
                this.queryTab = 0;
            }

        }

    }
}
