package com.ge.apm.view.wo;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
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
    private UserContextService userContextService;
    private AssetLifecycleEventRepository assetLifecycleEventRepository;

    @PostConstruct
    public void init() {
    	userContextService = WebUtil.getBean(UserContextService.class);
        assetLifecycleEventRepository = WebUtil.getBean(AssetLifecycleEventRepository.class);
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
            String queryOrderId = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryOrderId");
            if (StringUtils.isNotEmpty(queryOrderId)) {
                AssetLifecycleEvent assetLifecycleEvent = assetLifecycleEventRepository.findById(queryOrderId);
                if(assetLifecycleEvent != null){
                    if(assetLifecycleEvent.getEventType() == 3){
                        this.queryTab = userContextService.isYiMaiYangGuangTenant() ? 2 : 1;
                    }else if(assetLifecycleEvent.getEventType() == 6) {
                        this.queryTab = userContextService.isYiMaiYangGuangTenant() ? 3 : 2;
                    }
                }
            }
        }
        String tabIndex =  WebUtil.getRequestParameter("tabIndex");
        loadTab(tabIndex);

    }

    public void loadTab(String tabIndex){
        if(StrUtil.isNotEmpty(tabIndex)){
            try {
                this.queryTab = Integer.parseInt(tabIndex);
            } catch (NumberFormatException e) {
                setTabIndexByTabId(tabIndex);
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
        int count = 0;
        if (showInOrder && !userContextService.isZtbTenant()) {
            //内部工单显示了
            innerWorkOrder = true;
            if("inWorkOrder".equals(tabId)){
                this.queryTab = count;
            }else {
                count++;
            }
        }
        if(userContextService.isYiMaiYangGuangTenant()){
            //MVS工单显示了
            if ("mvsWorkOrder".equals(tabId)) {
                this.queryTab = count;
            }else{
                count++;
            }
        }
        if(!userContextService.isZkbAccount()){
            if((userContextService.enableDisplayExternalWorkOrder() && userContextService.accessContainsFunctionMenu("REPAIR_EXORDER_VIEW"))){
                if ("outWorkOrder".equals(tabId)) {
                    this.queryTab = count;
                }else{
                    count++;
                }
            }
            if(userContextService.enableDisplayRemoteWorkOrder() &&  userContextService.accessContainsFunctionMenu("REPAIR_REMOTE_ORDER_VIEW")){
                if ("outRemoteWorkOrder".equals(tabId)) {
                    this.queryTab = count;
                }else{
                    count++;
                }
            }
        }else{
            if("outZkbWorkOrder".equals(tabId)){
                this.queryTab = count;
            }
            count++;
            if(!userContextService.isAccountEndUser() && userContextService.isLocale() || userContextService.isZtbTenant() || userContextService.isHaveIbDeviceUser() || userContextService.accessContainsFunctionMenu("REPAIR_REMOTE_ORDER_VIEW")){
                if("outRemoteWorkOrder".equals(tabId)){
                    this.queryTab = count;
                }
            }
        }
       /* if ("outWorkOrder".equals(tabId)) {
            //现场保养可能是第一页或者第二页，要看内部保养是不是显示
            if (innerWorkOrder) {
                this.queryTab = 1;
            } else {
                this.queryTab = 0;
            }

        }*/
    }
}
