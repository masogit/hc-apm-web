/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.SharedAssetInfoUtils;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import javaslang.Tuple2;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    SharedAssetTypeRepository sharedAssetTypeRepository = null;
    private UaaService uuaService;

    private Date filterBeginTime;
    private Date filterEndTime;
    private String oneSearchStr;

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (oneSearchStr.isEmpty()) {
            this.removeFilterOnField("assetName");
            this.removeFilterOnField("assetNumber");
        }
    }
    private long queryIndex = 0;
    public long getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(long queryIndex) {
        this.queryIndex = queryIndex;
    }

    public Date getFilterBeginTime() {
        return filterBeginTime;
    }

    public void setFilterBeginTime(Date filterBeginTime) {
        this.filterBeginTime = filterBeginTime;
    }

    public Date getFilterEndTime() {
        return filterEndTime;
    }

    public void setFilterEndTime(Date filterEndTime) {
        this.filterEndTime = filterEndTime;
    }

    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        asiDao = WebUtil.getBean(AssetInfoRepository.class);
        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        sharedDao = WebUtil.getBean(SharedAssetRequestRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        wfCfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        sharedAssetTypeRepository = WebUtil.getBean(SharedAssetTypeRepository.class);
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

    //根据登录人信息配置权限
    private void setUserSearchFilters() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getUserAccessibleSiteListWithClinicalDept(user.getUid()).stream()
																					                .filter(item -> item.getOrgType() == 4)  
																					                .map(item -> item.getUid())  
																					                .collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));

    }
    
    @Override
    protected Page<SharedAssetReqDetail> loadData(PageRequest pageRequest) {

        setUserSearchFilters();

        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        List<WorkflowConfig> wfCfigList = wfCfigDao.findByDeptForSharedAsset(orgUid);
        if (wfCfigList != null && wfCfigList.size() > 0) {
            searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.NE,orgUid));
        }

        if (queryIndex == 0){
            this.removeFilterOnField("chargingEndTime");
        }
        if (filterBeginTime != null){
            searchFilters.add(new SearchFilter("chargingBeginTime", SearchFilter.Operator.GTE,filterBeginTime));
        }
        if (filterEndTime != null){
            searchFilters.add(new SearchFilter("chargingEndTime", SearchFilter.Operator.LTE,filterEndTime));
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetName", SearchFilter.Operator.LIKE, oneSearchStr, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetNumber", SearchFilter.Operator.LIKE, oneSearchStr, "1*2+"));
        }
        if (queryIndex == 1){
            this.updateSearchFilter(new SearchFilter("chargingEndTime", SearchFilter.Operator.IsNull,null));
        }

//        searchFilters.add(new SearchFilter("borrowId", SearchFilter.Operator.IN,sharedList));
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public void searchList(Long queryIndex){
        this.queryIndex = queryIndex;
        this.searchFilters.clear();
    }

    public String getUseTime(Date chargingBeginTime,Date chargingEndTime,String chargingType){

        return (String) getUseTimeOrCost(chargingBeginTime,chargingEndTime,chargingType,null);
    }

    public Double getUseCost(Date chargingBeginTime,Date chargingEndTime,String chargingType,Double unitPrice){

        return (Double) getUseTimeOrCost(chargingBeginTime,chargingEndTime,chargingType,unitPrice == null ? 0.0 : unitPrice);
    }

    public Object getUseTimeOrCost(Date chargingBeginTime,Date chargingEndTime,String chargingType,Double unitPrice){
        if (chargingEndTime == null){
            Calendar cal = Calendar.getInstance();
            Date date = new Date();
            cal.setTime(date);
            if ("2".equals(chargingType)){
                cal.set(Calendar.SECOND,0);
                int minutes = cal.get(Calendar.MINUTE);
                if (minutes>=0 && minutes <30){
                    cal.set(Calendar.MINUTE,0);
                }else {
                    cal.set(Calendar.MINUTE,30);
                }
            }
            chargingEndTime = cal.getTime();
        }
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long diff = chargingEndTime.getTime() - chargingBeginTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        double number = day * 24 + hour + min/60.0;
        number = number < 0 ? 0 : number;

        String useTime = "";
        useTime = day+"天"+hour+"时"+min+"分";
        if ("1".equals(chargingType)){
            useTime = "1次";
            number = 1;
        }else if ("3".equals(chargingType)){
            useTime = day+"天";
            number = day;
        }
        if (unitPrice == null){
            return useTime;
        }
        if ("维修借用".equals(chargingType)){
            return 0.0;
        }
        return number * unitPrice;

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

    public SharedAssetReqDetailRepository getDao() {
        return dao;
    }

    public void setDao(SharedAssetReqDetailRepository dao) {
        this.dao = dao;
    }

    public String getRentalTime(SharedAssetReqDetail reqDetail) {
        Tuple2<BigDecimal, String> chargingUnitAndTotalTime = SharedAssetInfoUtils.getChargingUnitAndTotalTime(reqDetail);
        return chargingUnitAndTotalTime._2;
    }

    public Double getTotalAmount(SharedAssetReqDetail reqDetail) {
        SharedAssetRequest request = (StringUtils.isEmpty(reqDetail.getReturnId())) ? reqDetail.getLeaseRequest() : reqDetail.getReturnRequest();

        BigDecimal unit = SharedAssetInfoUtils.getChargingUnitAndTotalTime(reqDetail)._1;
        if ("1".equals(request.getType())) {//no charging for asset maintain
            unit = new BigDecimal(0);
        }
        AssetInfo asset = asiDao.findByUid(reqDetail.getAssetInfoId());
        SharedAssetType sharedAssetType = sharedAssetTypeRepository.findById(asset.getSharedAssetTypeUid());
        BigDecimal totalAmount = SharedAssetInfoUtils.getTotalAmount(unit, sharedAssetType, reqDetail);
        return totalAmount.doubleValue();
    }

}
