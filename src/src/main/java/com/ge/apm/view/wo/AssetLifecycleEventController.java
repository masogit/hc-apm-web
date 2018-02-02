/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEventController extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable {

    private AssetLifecycleEventRepository dao;

    private UaaService uaaService;

    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(AssetLifecycleEventRepository.class);
        this.filterBySite = false;
        this.filterByHospital = false;

    }

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {

        this.setSearchFilter();
        this.selected = null;
        searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "3"));//3外部维修
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
       /* if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
}*/
    }

    public void onSelectAssetLifecycleEvent() {
        System.out.println("kdkdkd");
    }
    public String getMsStatus(AssetLifecycleEvent alse){
        String strStatus=null;
        JSONObject parse = (JSONObject)JSON.parse(alse.getDescription());
        JSONArray status = (JSONArray)parse.get("messages");
        if(status!=null)
         strStatus= StringUtils.removeEnd(status.get(0).toString(),"状态: ");
        return strStatus;
    }

    public String getEvStatus(int status) {
        if (status == 1) {
            return "open";
        }
        if (status == 2) {
            return "close";
        }

        return "cancel";

    }

}