/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.AllocateCustomConfigConstant;
import com.ge.apm.service.sharedasset.SharedAssetReqDetailService;
import com.ge.apm.service.sharedasset.valuation.ValuationFactory;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
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
    AssetInfoService assetInfoService = null;
    WorkflowConfigRepository wfCfigDao = null;
    SharedAssetTypeRepository sharedAssetTypeRepository = null;
    SharedAssetSwapIndexRepository swapIndexRepository=null;
    private UaaService uuaService;
    private CustomConfigService ccService;
    @Autowired
    SharedAssetReqDetailService sharedAssetReqDetailService;
    private Date filterBeginTime;
    private Date filterEndTime;
    private String oneSearchStr;
    private Object clinicalDeptName;
    private List<String> assetAllocateFields;
    private Boolean whethertoenableunattended;
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
        sharedAssetReqDetailService = WebUtil.getBean(SharedAssetReqDetailService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        swapIndexRepository=WebUtil.getBean(SharedAssetSwapIndexRepository.class);
        ccService=WebUtil.getBean(CustomConfigService.class);
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
        initAssetAllocateFields();
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
        List<String> orgUidList =new ArrayList<>();
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = userContextService.getSelectSite();
        if(selectSite != null){
            List<OrgInfo> deptListBySiteUid = orgInfoDao.getDeptListBySiteUid(selectSite.getUid());
            orgUidList = deptListBySiteUid.stream().map(OrgInfo::getUid).collect(Collectors.toList());

        }else{
            UserAccount user = UserContextService.getCurrentUserAccount();
            orgUidList = uuaService.getUserAccessibleSiteListWithClinicalDept(user.getUid()).stream()
                    .filter(item -> item.getOrgType() == 4)
                    .map(item -> item.getUid())
                    .collect(Collectors.toList());

            orgUidList.add(getVirtuallyOrgInfoUid(user));
        }

        this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        this.updateSearchFilter(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));

    }
    private String getVirtuallyOrgInfoUid(UserAccount user){
        char[] chars = user.getSiteUID().toCharArray();
        chars[0]='$';
        return String.valueOf(chars);
    }
    @Override
    public void getFilterMatchModes(DataTable table) {
        super.getFilterMatchModes(table);
        clinicalDeptNameParamInit(table);
    }

    private void clinicalDeptNameParamInit(DataTable table) {
        Object clinicalDeptName = table.getFilters().remove("clinicalDeptName");
        this.clinicalDeptName = clinicalDeptName;
        }
    private void addClinicalDeptNameSearchFilters() {
        if(this.clinicalDeptName == null){
            return;
        }
        String name = clinicalDeptName.toString();
        if(name.length() == 0){
            return;
        }
        List<SelectItem> orgInfo = assetInfoService.getAllAccessibleOrgDeptListNestedByOrgLevel();
        List<Object> orgUidList = orgInfo.stream().filter(item -> item.getLabel().contains(name)).map(SelectItem::getValue).collect(Collectors.toList());
        this.searchFilters.add(0,new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList,"1*"));
    }

    @Override
    protected Page<SharedAssetReqDetail> loadData(PageRequest pageRequest) {

        setUserSearchFilters();

        addClinicalDeptNameSearchFilters();

        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        List<WorkflowConfig> wfCfigList = wfCfigDao.findByDeptForSharedAsset(orgUid);
        if (wfCfigList != null && wfCfigList.size() > 0) {
            this.updateSearchFilter(new SearchFilter("clinical_dept_uid", SearchFilter.Operator.NE,orgUid));
        }

        if (queryIndex == 0){
            this.removeFilterOnField("charging_begin_time");
        }


        if (queryIndex == 1){
            this.updateSearchFilter(new SearchFilter("charging_end_time", SearchFilter.Operator.IsNull,null));
        }
        if (filterBeginTime != null){
            this.updateSearchFilter(new SearchFilter("charging_begin_time", SearchFilter.Operator.GTE,filterBeginTime));
        }
        if (filterEndTime != null){
            this.updateSearchFilter(new SearchFilter("charging_end_time", SearchFilter.Operator.LTE,filterEndTime));
        }
        Wrapper wrapper = sharedAssetReqDetailService.buildDefaultFilters(searchFilters);
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
//            this.updateSearchFilter(new SearchFilter("asset_name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
//            this.updateSearchFilter(new SearchFilter("asset_number", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            wrapper.andNew().like("asset_name",oneSearchStr).or().like("asset_number",oneSearchStr);
        }
        int totalRowCount = sharedAssetReqDetailService.getTotalRowCount(wrapper);
        List<SharedAssetReqDetail> sharedAssets = sharedAssetReqDetailService.loadSharedAssets(pageRequest, wrapper);
        Page<SharedAssetReqDetail> result=null;
        if (sharedAssets== null) {
            result=new PageImpl(new ArrayList(),pageRequest,totalRowCount);
        } else {
            result= new PageImpl(sharedAssets,pageRequest,totalRowCount);
        }
        return result;
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

    public Double getTotalAmount(SharedAssetReqDetail reqDetail) {
        double totalAmount;
        String returnReqId = reqDetail.getReturnId();
        SharedAssetRequest request;
        if(StringUtils.isNotEmpty(returnReqId)) {
            //借出未归还
            request = sharedDao.findById(returnReqId);
        } else {
            //已归还
            if (reqDetail.getBorrowId()==null){
                request = sharedDao.findById("");
            }
            else {
                request = sharedDao.findById(reqDetail.getBorrowId());
            }

        }
        if(request!=null && "1".equals(request.getType())) {
            totalAmount = 0d;
        } else {
            totalAmount = reqDetail.getRentalAmount().doubleValue();
        }

        return totalAmount;
    }

    @Override
    public void updateSearchFilter(SearchFilter searchFilter) {
        List existedFilters = this.searchFilters.stream()
                .filter(item -> searchFilter.fieldName.equals(item.fieldName) && searchFilter.operator.compareTo(item.operator) == 0)
                .collect(Collectors.toList());
        this.searchFilters.removeAll(existedFilters);
        searchFilters.add(searchFilter);
    }

    public String  jsonFormatDate(int type){
        Date date=null;
        try {
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            switch (type){
                case 1:
                    date=selected.getLeaseRequest().getApplyTime();
                    break;
                case 2:
                    date=selected.getChargingBeginTime();
                    break;
                case 3:
                    date=selected.getChargingEndTime();;
                    break;
                case 4:
                    date=selected.getMaintainTime();
                    break;
            }
            if(date != null){
                return  formatter.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public boolean  openWhethertoenableunattended(){
        if(whethertoenableunattended == null){
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            String siteUID = currentUserAccount.getSiteUID();
            List<WorkflowConfig> bySiteUID = wfCfigDao.findBySiteUID(siteUID);
            if(CollectionUtils.isEmpty(bySiteUID)){
                return false;
            }
            whethertoenableunattended= bySiteUID.stream().allMatch(item -> item.getWhethertoenableunattended() != null && item.getWhethertoenableunattended());
        }

       return whethertoenableunattended;
    }

    public void  initAssetAllocateFields(){
        if(!openWhethertoenableunattended())  {
            assetAllocateFields= AllocateCustomConfigConstant.ASSET_ALLOCATE.getAssetAllocateFields();
            return ;
        }else {
            UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
            String siteUID = currentUserAccount.getSiteUID();
            CustomConfigObject currentCustomConfig = ccService.getCurrentCustomConfig(siteUID);
            assetAllocateFields = currentCustomConfig.getAssetAllocateFields();
        }

    }

    public List<String> getAssetAllocateFields() {
        return assetAllocateFields;
    }

    public void setAssetAllocateFields(List<String> assetAllocateFields) {
        this.assetAllocateFields = assetAllocateFields;
    }
}
