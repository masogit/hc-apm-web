package com.ge.apm.view.pm;

import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tabview.TabView;
import org.springframework.beans.factory.annotation.Autowired;

import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean
@RequestScoped
public class PmOrderTabController {
    @Setter @Getter
    private Integer queryTab;
    @Getter @Setter
    private TabView tabView;
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
                }catch (NumberFormatException e) {
                    setTabIndexByTabId(queryTab);

                }
            } else {
                this.queryTab = 0;
            }

        }
    }

    private void setTabIndexByTabId(String tabId) {

        UserContextService userContextService = WebUtil.getBean(UserContextService.class);

        boolean innerPM = false;
        if (!userContextService.isIBorUltraSoundIBEndUser() &&  !userContextService.isIBTenant()) {
            //内部保养显示了
            innerPM = true;
        }

        boolean outterPM = false;
        boolean remotePM = false;
        if (!userContextService.isAccountEndUser()) {
            outterPM = true;
            remotePM = true;
        }


        if ("pmOrderListOut".equals(tabId)) {
            //现场保养可能是第一页或者第二页，要看内部保养是不是显示
            if (innerPM) {
                this.queryTab = 1;
            } else {
                this.queryTab = 0;
            }

        } else if ("pmOrderListRemote".equals(tabId)) {
            int remoteTab = 0;
            if (innerPM) {
                remoteTab++;
            }
            if (outterPM) {
                remoteTab++;
            }

            this.queryTab = remoteTab;
        }

    }
    
    
}
