/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetReqDetailForQueryController extends GenericCRUDUUIDController<SharedAssetReqDetail>{

    SharedAssetReqDetailRepository dao = null;
    private String iouUid;
   
    private Integer iouId = -1;
    private List<String> selectedBorrowAccessoryList = null;
    private List<String> selectedReturnAccessoryList = null;
    AssetInfoRepository asiDao = null;
    SharedAssetReqTypeRepository apdDao = null;
    SharedAssetRequestRepository sharedDao = null;
    OrgInfoRepository orgInfoDao = null;
    WorkflowConfigRepository wfCfigDao = null;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        asiDao = WebUtil.getBean(AssetInfoRepository.class);
        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        sharedDao = WebUtil.getBean(SharedAssetRequestRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        wfCfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        iouUid = (String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if ("Create".equalsIgnoreCase(actionName)) {
            iouId = -2 ;
        }  
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        
    }
    
    
    @Override
    protected SharedAssetReqDetailRepository getDAO() {
        return dao;
    }
    
    @Override
    protected Page<SharedAssetReqDetail> loadData(PageRequest pageRequest) {
        List<String> sharedList = new ArrayList<String>();
        
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
        List<WorkflowConfig> wfCfigList = wfCfigDao.findByDeptForSharedAsset(orgUid);
        if (wfCfigList == null || wfCfigList.size() == 0) {
            sharedList = sharedDao.findSharedIdByOrgUidforQuery(orgUid);
        } else {
            sharedList = sharedDao.findSharedIdforQuery();
        }
      
        searchFilters.add(new SearchFilter("borrowId", SearchFilter.Operator.IN,sharedList));
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    
    public String getclinicalDeptName(String Uid){
        if(null == Uid||Uid.equalsIgnoreCase("")){
            return "";
        }else{
            List<SharedAssetRequest> sharedList = sharedDao.findByDept(Uid);
            if(sharedList.size()==1){
                return sharedList.get(0).getClinicalDeptName();
            }else{
                return "";
            }
        }
    }
    
}
