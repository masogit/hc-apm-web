
package com.ge.apm.view;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.sharedAsset.SharedAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.AllocationCenterUtils;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;

import javaslang.Tuple3;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetRequestV2Controller extends GenericCRUDUUIDController<SharedAssetRequest>{
    private Double total;
    private UaaService uuaService;
    private SharedAssetService sharedAssetService;
    private Integer queryIndex=-1;
    UserOrgInfoRepository userOrgDao = null;
    OrgInfoRepository orgInfoDao = null;
    SharedAssetRequestRepository dao = null;
    SharedAssetReqDetailRepository asdDao = null;
    SharedAssetReqTypeRepository apdDao = null;
    AssetInfoRepository asiDao = null;
    SharedAssetTypeRepository swapDao = null;
    WorkflowConfigRepository wfCfigDao = null;
    SharedAssetConfigRepository sharedAssetConfigDao = null;
    String p=null;
    private String operation;
    
    private Date applyTimeTo = null;
    private Date applyTimeFrom = null;
    private String applyTimeFormatTime = null;
    private String filterApplyTime = null;
    
    private UserAccount selectedAssignee;

     public String getFilterApplyTime() {
        return filterApplyTime;
    }

    public void setFilterApplyTime(String filterApplyTime) {
        this.filterApplyTime = filterApplyTime;
    }

    public Date getApplyTimeTo() {
        return applyTimeTo;
    }
    
    public void setApplyTimeTo(Date applyTimeTo) {
        this.applyTimeTo = applyTimeTo;
    }

    public Date getApplyTimeFrom() {
        return applyTimeFrom;
    }

    public void setApplyTimeFrom(Date applyTimeFrom) {
        this.applyTimeFrom = applyTimeFrom;
    }
    
    public String getApplyTimeFormatTime() {

            return applyTimeFormatTime;
    }

    public void setApplyTimeFormatTime(String applyTimeFormatTime) {
        this.applyTimeFormatTime = applyTimeFormatTime;
    }
    
    private SharedAssetReqType selectedSart;//借用申请优化机制

    public SharedAssetReqType getSelectedSart() {
        return selectedSart;
    }

    public void setSelectedSart(SharedAssetReqType selectedSart) {
        this.selectedSart = selectedSart;
    }
    
    private SharedAssetReqDetail selectedSard;//录入设备优化

    public SharedAssetReqDetail getSelectedSard() {
        return selectedSard;
    }

    public void setSelectedSard(SharedAssetReqDetail selectedSard) {
        this.selectedSard = selectedSard;
    }
    
    public UserAccount getSelectedAssignee() {
    	return this.selectedAssignee;
    }
    
    public void setSelectedAssignee(UserAccount ua) {
    	this.selectedAssignee = ua;
    }
    
    public void prepareCreateSart(){
        selectedSart = new SharedAssetReqType();
    }
    
    public void prepareEditSart(){
        crudActionName = "Edit";
    }
    
    public void deleteSart(){
        if(selectedSart!=null)
            selected.getSartList().remove(selectedSart);
        selectedSart = null;
    }
    
    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
        dao = WebUtil.getBean(SharedAssetRequestRepository.class);
        asdDao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        asiDao = WebUtil.getBean(AssetInfoRepository.class);
        swapDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        wfCfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        sharedAssetConfigDao = WebUtil.getBean(SharedAssetConfigRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        p=request.getServletPath();
        String encodeStr = request.getParameter("str");
        String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
        else if ("View".equalsIgnoreCase(actionName)) {
            prepareView();
        }
        else if ("Edit".equalsIgnoreCase(actionName)) {
            setSelected(dao.findById((String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            prepareEdit();
        }
        else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        } 
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }

    @Override
    protected SharedAssetRequestRepository getDAO() {
        return dao;
    }

    public String getViewPage(String id, String actionName) {
        operation = "Edit.xhtml?actionName=Edit&selectedid=" + "1";
        return UrlEncryptController.encodeUrlParam(operation);
    }
    
    @Override
    protected Page<SharedAssetRequest> loadData(PageRequest pageRequest) {
//       setSearchFilter();
       
       setUserSearchFilters();

       this.updateSearchFilter(new SearchFilter("type", SearchFilter.Operator.IsNull,null));//过滤借还维护的单子

        if (queryIndex == -2) {
            List<String> stateList = new ArrayList<String>();
            stateList.add("0");
            stateList.add("1");
            stateList.add("2");
            stateList.add("3");
            stateList.add("4");
            stateList.add("5");
            stateList.add("6");
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
        }else if (queryIndex == -3) {
            List<String> stateList = new ArrayList<String>();
            stateList.add("7");
            stateList.add("8");
            stateList.add("9");
            stateList.add("10");
            stateList.add("11");
            stateList.add("12");
            stateList.add("13");
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
        }else if(queryIndex == -1){
            if(p.equalsIgnoreCase("/portal/BorrowApplyV2/List.xhtml")){//分配页面初始List
                List<String> stateList = new ArrayList<String>();
                stateList.add("0");
                stateList.add("1");
                stateList.add("2");
                stateList.add("3");
                stateList.add("4");
                stateList.add("5");
                stateList.add("6");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }else if(p.equalsIgnoreCase("/portal/ReturnApplyV2/List.xhtml")){//分配页面初始List
                List<String> stateList = new ArrayList<String>();
                stateList.add("7");
                stateList.add("8");
                stateList.add("9");
                stateList.add("10");
                stateList.add("11");
                stateList.add("12");
                stateList.add("13");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }else if(p.equalsIgnoreCase("/portal/BorrowAcceptanceV2/List.xhtml")){
                List<String> stateList = new ArrayList<String>();
                stateList.add("1");
                stateList.add("2");
                stateList.add("5");
//                stateList.add("6");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }else if(p.equalsIgnoreCase("/portal/ReturnAcceptanceV2/List.xhtml")){
                List<String> stateList = new ArrayList<String>();
                stateList.add("7");
                stateList.add("8");
                stateList.add("11");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }
        }else {
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.EQ,queryIndex.toString()));
        }
       
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public List<SharedAssetRequest> getItemList() {
        return dao.find();
    }
    
    @Override
    public void onBeforeSave(SharedAssetRequest object) {
        OrgInfo userOrg = orgInfoDao.getByUid(object.getClinicalDeptUid());
        object.setClinicalDeptName(userOrg.getName());
        object.setApplyAssetInfo(null);
    }
    
    @Override
    public void onBeforeNewObject(SharedAssetRequest object) {
     
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setState(1);
                
        if(p.equalsIgnoreCase("/portal/BorrowApplyV2/Edit.xhtml")){
            //借用待受理
            object.setState(1);
            object.setBehavior(1);
        }else if(p.equalsIgnoreCase("/portal/ReturnApplyV2/Edit.xhtml")){
            //归还待受理
            object.setState(7);
            object.setBehavior(2);
        }

        //object.setCreateDate(new Date());
        object.setApplyTime(new Date());
        object.setClinicalDeptUid(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setCreateBy(UserContextService.getCurrentUserAccount().getUid());
        
        object.setApplicant(UserContextService.getCurrentUserAccount().getLoginName());
        object.setApplicantPhone(UserContextService.getCurrentUserAccount().getTelephone());
        
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
        
        if(userOrg.getOrgType()==4){
            object.setClinicalDeptName(userOrg.getName());
            object.setClinicalDeptUid(userOrg.getUid());
        }
       
        //Date today = getNexDate(new Date());
//        today.setHours(0);
//        today.setMinutes(0);
//        today.setSeconds(0);
//        Calendar c = Calendar.getInstance();
//        c.setTime(today);
//        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
//        c.add(Calendar.);
//        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
//        ft.format(today);
//        object.setInvalidTime(today);
        
        
    }

    //根据登录人信息配置权限
    private void setUserSearchFilters() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        

       
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));

//        if(p.equalsIgnoreCase("/portal/BorrowApplyV2/List.xhtml")||p.equalsIgnoreCase("/portal/ReturnApplyV2/List.xhtml")){
//            searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        }

    }
    
    public Date getNexDate(Date date){
        int n = 1;
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");  
        SimpleDateFormat dd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
        return new Date(date.getTime() + n * 24 * 60 * 60 * 1000L); 
        
    }
    
    public void onSelectB() throws IOException {
        String pageName = "Edit_1";
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    
    public void onSelectD() throws IOException {
        
        String pageName = "Edit_1";
       
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    
    public void onSelectE() {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();

        String pageName = "";
        if(sta == 8){
            pageName = "Edit_1";
            selected.setMsgEndTime(new Date());
//            selected.setMsgEndTime(this.getTime("归还"));
            selected.setState(sta);
            this.save();

        }else if(sta == 7 || sta == 11){
            pageName = "Edit";
        }else if(sta == 12 || sta == 9 || sta == 10 || sta == 13){
            pageName = "Edit_2";
        }

        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        requestContext.addCallbackParam("pageName", pageName);
    }
    
    public void onSelectC() {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();

        String pageName = "";
        selected.getSartList();
        if(sta == 2){
            pageName = "Edit_1";
//            selected.setMsgAccepter(this.selected.getApplicant());
            selected.setMsgAccepter(this.getNameByLoginName(selected.getApplicant()));
//            selected.setMsgBeginTime(this.getTime("借用"));
            selected.setMsgBeginTime(new Date());
            selected.setState(sta);
            this.save();

        }else if(sta == 1||sta == 5){
            pageName = "Edit";
        }else if(sta == 6 || sta == 3 || sta == 4 || sta == 0){
            pageName = "Edit_2";
        }

        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        requestContext.addCallbackParam("pageName", pageName);

    }
    
    public String queryBorrowInfo(String assetId,String clinicalDeptUID){

        List<String> borrowIds = asdDao.getBorrowDetail(assetId, clinicalDeptUID);
        if(borrowIds.size()!=1){
            WebUtil.addErrorMessage("找不到借用详情！");
            return "";
        }else{
            operation = "EditForQuery.xhtml?actionName=Edit&selectedid=" + borrowIds.get(0);
            //ArrivalList o=new ArrivalList();
            return UrlEncryptController.encodeUrlParam(operation);
        }

    }


     public void getAssetInfoR(Date times) throws ParseException{//查询并插入归还设备信息
         String uid = selected.getId();
         SharedAssetRequest realSAR = dao.findById(uid);
         int sta = realSAR.getState();
         if (sta != 8){
             WebUtil.addErrorMessage("申请已取消或已完成交付，不能录入设备，请返回并刷新！");
             return;
         }

          List<SharedAssetReqDetail> sardList = selected.getSardList();
          List<AssetInfo> assetInfoList = selected.getInfoList();
          List<SharedAssetReqType> sartList = selected.getSartList();
         
            String applyAssetInfo = selected.getApplyAssetInfo();
            String Uid = selected.getId();
            List<SharedAssetReqType> applySwapTypeList  = apdDao.findSwapTypeId(Uid);
            String departNum = applyAssetInfo;//设备编号
            String financingNum = applyAssetInfo;//资产编号
            String qrCode = applyAssetInfo;//二维码
            AssetInfo infoVo = new AssetInfo();
            List<AssetInfo> infoList = asiDao.findSwapTypeInfo(departNum,financingNum,qrCode,this.selected.getSiteUID());
            List<String> depts = dao.findDeptForSharedAsset(this.selected.getSiteUID());
            String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
            OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
            if(infoList.size()==1){
                infoVo = infoList.get(0);
                String assetDeptUID = infoVo.getAssetDeptUID();
                String ClinicalDeptName = infoVo.getClinicalDeptName();
                //获得计费开始时间、附件字段

                if(null==infoVo.getSharedAssetTypeUid()|| infoVo.getSharedAssetTypeUid().equalsIgnoreCase("0")){
                    WebUtil.addWarnMessage("该设备不属于调剂设备类型！");
                    return;
                }//在asset_info中判断是否属于 调剂中心科室
                else if(depts.contains(assetDeptUID)){
                    WebUtil.addWarnMessage("该设备已归还！");
                    return; 
                }
                else if(!depts.contains(userOrg.getUid())){
                    WebUtil.addErrorMessage("当前角色非调剂中心人员，请核实后操作！");
                    return; 
                }
               
                else{
                    if(sartList.size()>0){
                        for(SharedAssetReqType applySwapType :sartList){
                            if(infoVo.getSharedAssetTypeUid().equals(applySwapType.getSharedAssetTypeId())){
                                selectedSart = applySwapType;//明细表联动选中
                                
                                if(applySwapType.getActualQty() < applySwapType.getApplyQty()){
                                    SharedAssetReqDetail assetDetaiVo = new SharedAssetReqDetail();
                                    List<SharedAssetReqDetail> detail_list = asdDao.getBorrowInfo(infoVo.getUid());
                                    if(detail_list.size()==1){
                                        if(!assetDeptUID.equals(detail_list.get(0).getClinicalDeptUid())){
                                            WebUtil.addErrorMessage("该设备当前使用科室与申请科室不一致，使用科室是:"+ClinicalDeptName);
                                            return;
                                        }
                                    }else{
                                        WebUtil.addErrorMessage("获取该设备借用信息有误！");
                                        return; 
                                    }
                                    SharedAssetReqDetail sard = detail_list.get(0);
                                    sard.setReturnId(Uid);
                                    sard.setAssetInfoId(infoVo.getUid());
                                    sard.setAssetName(infoVo.getName());
                                    sard.setAssetNumber(infoVo.getDepartNum());
                                    sard.setBorrowDealUid(UserContextService.getCurrentUserAccount().getUid());
                                    sard.setFinancingNum(infoVo.getFinancingNum());
                                    sard.setClinicalDeptForReturnUid(this.selected.getClinicalDeptUid());
                                    sard.setClinicalDeptForReturnName(this.selected.getClinicalDeptName());
                                    String swaptypeid = applySwapType.getSharedAssetTypeId();
                                    SharedAssetType swapTypeVo = swapDao.findById(swaptypeid);

                                    //时间往前推
                                    if ("2".equals(swapTypeVo.getChargingType())){
                                        sard.setChargingEndTime(this.setEndTime(times));
                                    }else {
                                        sard.setChargingEndTime(times);

                                    }
                                    if(times==null){
                                        sard.setChargingEndTime(this.setEndTime(new Date()));
                                    }

                                    sard.setChargingType(swapTypeVo.getChargingType());

                                    if(sard.getUnitPrice() == null) {
                                        sard.setUnitPrice(swapTypeVo.getUnitPrice());
                                    }
                                    sard.setUnit(swapTypeVo.getUnit());

//                                    this.setCost(sard);
                                    //JIRA#3043
                                    Double totalAmount = this.getTotalAmount(sard);
                                    sard.setCost(totalAmount);

                                    //附件状态默认全选
                                    for(SharedAssetTypeAccesssory item:sard.getAccessoryList()){
                                        if (item.getBorrowStatus()) {
                                            item.setReturnStatus(true);
                                        }
                                    }
                                    sard.setAccessoryList(sard.getAccessoryList());

                                    //asdDao.save(detail_list.get(0));
                                    if (sardList != null && sardList.size() > 0) {
                                        for (int i = 0; i < sardList.size(); i++) {
                                            SharedAssetReqDetail sard2 = sardList.get(i);
                                            if (sard.getAssetNumber().equals(sard2.getAssetNumber())) {
                                                selectedSard = sard2;
                                                WebUtil.addWarnMessage("该设备已录入，请勿重复录入！");
                                                return;
                                            }
                                            if (i == sardList.size()-1) {
                                                sardList.add(sard);
                                                selectedSard = sard;
                                                break;
                                            }
                                        }
                                    }else if(sardList.size() == 0){
                                        sardList.add(sard);
                                        selectedSard = sard;
                                    }
                                     
                                    //更改使用科室

                                    if(userOrg.getOrgType()==4){
                                        infoVo.setClinicalDeptName(userOrg.getName());
                                        infoVo.setAssetDeptUID(userOrg.getUid());
                                    }

                                    //asiDao.save(infoVo);
                                    
                                    assetInfoList.add(infoVo);

                                    //applySwapType.setActualQty(applySwapType.getActualQty()+1);//借用数+1
                                    //apdDao.save(applySwapType);
                                    
                                    applySwapType.setActualQty(applySwapType.getActualQty() + 1);
                                    WebUtil.addSuccessMessage("录入成功！");
                                    return;
                                }else{
                                    WebUtil.addWarnMessage("该设备类型 ‘"+applySwapType.getSwapName()+"’ 申请数量已满！");
                                    return;
                                }
                            }
                        }
                        WebUtil.addWarnMessage("该设备不属于归还申请的设备类型！");
                        return;
                    }
                }
            }else{

                WebUtil.addWarnMessage("录入信息不符，请核对后录入！");
 
                return;
            }
    }
    
    public void setCost(SharedAssetReqDetail sard){
        if (sard.getChargingEndTime() != null) {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long diff = sard.getChargingEndTime().getTime() - (sard.getChargingBeginTime()).getTime();
            Integer countQty = null;
            // 计算差多少天
            long day = diff / nd;
            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分钟
            long min = diff % nd % nh / nm;

            double number = day * 24 + hour + min/60.0;
            number = number < 0 ? 0 : number;

            if ("1".equals(sard.getChargingType())) {
                number = 1;
            } else if ("3".equals(sard.getChargingType())) {
                number = day;
            }
            sard.setCost(number * sard.getUnitPrice());
        }
    }
    
    public void onSelectR() throws IOException {
        
        String pageName = "Edit_2";
        
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public Date setBeginTime(Date date){
        Calendar cal = Calendar.getInstance();
//        Date date = new Date();
        cal.setTime(date);
        cal.set(Calendar.SECOND,0);
        int minutes = cal.get(Calendar.MINUTE);
        if (minutes>0 && minutes <=30){
            cal.set(Calendar.MINUTE,30);
        }else if (minutes > 30){
            cal.set(Calendar.MINUTE,0);
            cal.add(Calendar.HOUR,1);
        }
        return cal.getTime();
    }

    public Date setEndTime(Date date){
        Calendar cal = Calendar.getInstance();
//        Date date = new Date();
        cal.setTime(date);
        cal.set(Calendar.SECOND,0);
        int minutes = cal.get(Calendar.MINUTE);
        if (minutes>=0 && minutes <30){
            cal.set(Calendar.MINUTE,0);
        }else {
            cal.set(Calendar.MINUTE,30);
        }
        return cal.getTime();
    }

     public Date getTime(String type) throws ParseException{
            if("借用".equals(type)){
                return setBeginTime(new Date());
            }
            else{
                return setEndTime(new Date());
            }
            
     }
    
    public void getAssetInfo(Date times,String accepter) throws ParseException{//查询并插入借用设备信息
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
        if (sta != 2){
            WebUtil.addErrorMessage("申请已取消或已完成交付，不能录入设备，请返回并刷新！");
            return;
        }

        List<SharedAssetReqDetail> sardList = selected.getSardList();
        List<AssetInfo> assetInfoList = selected.getInfoList();
        List<SharedAssetReqType> sartList = selected.getSartList();
        
            String applyAssetInfo = selected.getApplyAssetInfo();
            String Uid = selected.getId();
            String acceptman = selected.getApplicant();
            List<SharedAssetReqType> applySwapTypeList  = apdDao.findSwapTypeId(Uid);
            String departNum = applyAssetInfo;//设备编号
            String financingNum = applyAssetInfo;//资产编号
            String qrCode = applyAssetInfo;//二维码
            AssetInfo infoVo = new AssetInfo();
            List<AssetInfo> infoList = asiDao.findSwapTypeInfo(departNum,financingNum,qrCode,this.selected.getSiteUID());
            List<String> depts = dao.findDeptForSharedAsset(this.selected.getSiteUID());
            String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
            OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
            if(infoList.size()==1){
                infoVo = infoList.get(0);
                String ClinicalDeptUID = infoVo.getAssetDeptUID();
                if(null==infoVo.getSharedAssetTypeUid()|| infoVo.getSharedAssetTypeUid().equalsIgnoreCase("0")){
                    WebUtil.addWarnMessage("该设备不属于调剂设备类型！");
                    return;
                }else if(infoVo.getStatus()!= 1 && infoVo.getStatus()!= 3){
                    WebUtil.addWarnMessage("该设备故障，不可借用！");
                    return; 
                }
                //在asset_info中判断是否属于 调剂中心科室
                else if(!depts.contains(ClinicalDeptUID)){
                    WebUtil.addWarnMessage("该设备已借出！");
                    return; 
                }
                else if(!depts.contains(userOrg.getUid())){
                    WebUtil.addErrorMessage("当前角色非调剂中心人员，请核实后操作！");
                    return; 
                }
                else{
                    if(sartList.size()>0){
                        for(SharedAssetReqType applySwapType :sartList){

                            if(applySwapType.getSharedAssetTypeId().equals(infoVo.getSharedAssetTypeUid())){
                                selectedSart = applySwapType;//明细表联动选中
                                
                                if(applySwapType.getActualQty() < applySwapType.getExceptQty()){
                                    SharedAssetReqDetail assetDetaiVo = new SharedAssetReqDetail();
                                    assetDetaiVo.setBorrowId(Uid);
                                    assetDetaiVo.setSharedId(Uid);

                                    assetDetaiVo.setAssetInfoId(infoVo.getUid());
                                    assetDetaiVo.setAssetName(infoVo.getName());
                                    assetDetaiVo.setAssetNumber(infoVo.getDepartNum());
                                    assetDetaiVo.setClinicalDeptUid(selected.getClinicalDeptUid());
                                    assetDetaiVo.setClinicalDeptName(selected.getClinicalDeptName());
                                    //权限字段
                                    assetDetaiVo.setHospitalUID(selected.getHospitalUID());
                                    assetDetaiVo.setInstitutionUID(selected.getInstitutionUID());
                                    assetDetaiVo.setSiteUID(selected.getSiteUID());
                                    assetDetaiVo.setTenantUID(selected.getTenantUID());

    //                                assetDetaiVo.setState("0");//0：待归还 1：已归还
                                    assetDetaiVo.setBorrowDealUid(UserContextService.getCurrentUserAccount().getUid());//受理人
                                    assetDetaiVo.setFinancingNum(infoVo.getFinancingNum());//资产编号
                                    String swaptypeid = applySwapType.getSharedAssetTypeId();
                                    SharedAssetType swapTypeVo = swapDao.findById(swaptypeid);
                                    assetDetaiVo.setSharedAssetTypeId(swaptypeid);

                                    assetDetaiVo.setAccepter(acceptman);
                                    if (accepter != null && !"".equals(accepter)){
                                        assetDetaiVo.setAccepter(accepter);
                                    }
                                    //时间往后推
                                    if ("2".equals(swapTypeVo.getChargingType())){
                                        assetDetaiVo.setChargingBeginTime(this.setBeginTime(times));
                                    }else {
                                        assetDetaiVo.setChargingBeginTime(times);

                                    }
                                    if(times==null){
                                        assetDetaiVo.setChargingBeginTime(this.setBeginTime(new Date()));
                                    }

                                    //附件状态默认全选
                                    for(SharedAssetTypeAccesssory item:swapTypeVo.getAccessoryList()){
                                        item.setBorrowStatus(true); 
                                    }
                                    assetDetaiVo.setAccessoryList(swapTypeVo.getAccessoryList());
                                    assetDetaiVo.setChargingType(swapTypeVo.getChargingType());//计价方式
                                    assetDetaiVo.setUnitPrice(swapTypeVo.getUnitPrice());//单价
                                    assetDetaiVo.setCreateDate(new Date());
                                    assetDetaiVo.setCreateBy(UserContextService.getCurrentUserAccount().getUid());
                                    //asdDao.save(assetDetaiVo);

                                    logger.info("Asset Uid: " + infoVo.getUid() + ", start duration: " + swapTypeVo.getStartingDuration() + ", start price: " + swapTypeVo.getStartingPrice());
                                    if(swapTypeVo.getStartingDuration() != null && swapTypeVo.getStartingPrice() != null) {
                                        assetDetaiVo.setStartingDuration(swapTypeVo.getStartingDuration());
                                        assetDetaiVo.setStartingPrice(swapTypeVo.getStartingPrice());
                                    }
                                    
                                    assetDetaiVo.setId(UUID.randomUUID().toString().replace("-", ""));
                                    
                                    if (sardList != null && sardList.size() > 0) {
                                        for (int i = 0; i < sardList.size(); i++) {
                                            SharedAssetReqDetail sard = sardList.get(i);
                                            if (assetDetaiVo.getAssetNumber().equals(sard.getAssetNumber())) {
                                                selectedSard = sard;
                                                WebUtil.addWarnMessage("该设备已录入，请勿重复录入！");
                                                return;
                                            } 
                                            if (i == sardList.size()-1) {
                                                sardList.add(assetDetaiVo);
                                                selectedSard = assetDetaiVo;
                                                break;
                                            }
                                        }
                                    }else if(sardList.size() == 0){
                                        sardList.add(assetDetaiVo);
                                        selectedSard = assetDetaiVo;
                                    }
                                        
                                    //更改使用科室
                                    
                                    OrgInfo findByUid = orgInfoDao.findByUid(this.selected.getClinicalDeptUid());
                                    infoVo.setAssetDeptId(findByUid.getId());
//                                    infoVo.setClinicalDeptName(this.selected.getClinicalDeptName());
                                    infoVo.setAssetDeptUID(this.selected.getClinicalDeptUid());

                                    //asiDao.save(infoVo);
                                    
                                    assetInfoList.add(infoVo);

                                    //apdDao.save(applySwapType);
                                    applySwapType.setActualQty(applySwapType.getActualQty() + 1);
                                    
                                    WebUtil.addSuccessMessage("录入成功！");
                                    return;
                                }else{
                                    WebUtil.addWarnMessage("该设备类型 ‘"+applySwapType.getSwapName()+"’ 预计借用数量已满！");
                                    return;
                                }
                            }
                        }
                        WebUtil.addWarnMessage("该设备不属于借用申请的设备类型！");
                        return;
                    }
                }
            }else{
                WebUtil.addWarnMessage("录入信息不符，请核对后录入！");
                return;
            }
    }
    /**
     录入设备的删除
     */
     public void deleteBy(String uid){
        List<SharedAssetReqDetail> sardList = selected.getSardList();
        List<AssetInfo> assetInfoList = selected.getInfoList();
        List<SharedAssetReqType> sartList = selected.getSartList();
         for (SharedAssetReqDetail sharedAssetReqDetail : sardList) {
             if (sharedAssetReqDetail.getId().equals(uid)) {
                 //清理infoList
                 String infoId = sharedAssetReqDetail.getAssetInfoId();
                 for (AssetInfo assetInfo : assetInfoList) {
                     if (assetInfo.getUid().equals(infoId)) {
                         //处理sartList
                         for (SharedAssetReqType sharedAssetReqType : sartList) {
                             if (sharedAssetReqType.getSharedAssetTypeId().equals(assetInfo.getSharedAssetTypeUid())) {
                                 sharedAssetReqType.setActualQty(sharedAssetReqType.getActualQty()-1);
                                 selectedSart = sharedAssetReqType;//明细表联动选中
                                 break;
                             }
                         }
                         assetInfoList.remove(assetInfo);
                         break;
                     }
                 }
                 //清理sardList
                 sardList.remove(sharedAssetReqDetail);
                 break;
             }
         }
    }
    //录入设备修改前
    private List<String> selectedBorrowAccessoryList = null;
    private List<String> selectedReturnAccessoryList = null;

    public List<String> getSelectedBorrowAccessoryList() {
        return selectedBorrowAccessoryList;
    }

    public void setSelectedBorrowAccessoryList(List<String> selectedBorrowAccessoryList) {
        this.selectedBorrowAccessoryList = selectedBorrowAccessoryList;
    }

    public List<String> getSelectedReturnAccessoryList() {
        return selectedReturnAccessoryList;
    }

    public void setSelectedReturnAccessoryList(List<String> selectedReturnAccessoryList) {
        this.selectedReturnAccessoryList = selectedReturnAccessoryList;
    }
    
    public void prepareEditSard(){
        selectedBorrowAccessoryList = new ArrayList<String>();
        for(SharedAssetTypeAccesssory item: this.selectedSard.getAccessoryList()){
            if(item.getBorrowStatus() !=null&&item.getBorrowStatus()){
                selectedBorrowAccessoryList.add(item.getName());
            }
        }
        selectedReturnAccessoryList = new ArrayList<String>();
        for(SharedAssetTypeAccesssory item: this.selectedSard.getAccessoryList()){
            if(selectedReturnAccessoryList!=null){
                if(item.getReturnStatus() !=null&&item.getReturnStatus()){
                    selectedReturnAccessoryList.add(item.getName());
                }
            }
        }

    }
    //录入设备修改
    public void saveSard(String type){
        List<SharedAssetReqDetail> sardList = selected.getSardList();
        List<SharedAssetTypeAccesssory> accessoryList = selectedSard.getAccessoryList();
         
        for (int j = 0; j < accessoryList.size(); j++) {
            SharedAssetTypeAccesssory sharedAssetTypeAccesssory = accessoryList.get(j);
            boolean flag = true;
            if (selectedBorrowAccessoryList != null && selectedBorrowAccessoryList.size() > 0) {
                 for (int i = 0; i < selectedBorrowAccessoryList.size(); i++) {
                    String accessoryName = selectedBorrowAccessoryList.get(i);
                    if (sharedAssetTypeAccesssory.getName().equals(accessoryName)) {
                        flag = false;
                    }
                }
                 if (flag) {
                     sharedAssetTypeAccesssory.setBorrowStatus(false);
                 }
            }else{
                sharedAssetTypeAccesssory.setBorrowStatus(false);
            }
        }
        
        for (int j = 0; j < accessoryList.size(); j++) {
            SharedAssetTypeAccesssory sharedAssetTypeAccesssory = accessoryList.get(j);
            boolean flag = true;
            if (selectedReturnAccessoryList != null && selectedReturnAccessoryList.size() > 0) {
                 for (int i = 0; i < selectedReturnAccessoryList.size(); i++) {
                    String accessoryName = selectedReturnAccessoryList.get(i);
                    if (sharedAssetTypeAccesssory.getName().equals(accessoryName)) {
                        flag = false;
                    }
                }
                 if (flag) {
                     sharedAssetTypeAccesssory.setReturnStatus(false);
                 }
            }else{
                sharedAssetTypeAccesssory.setReturnStatus(false);
            }
 
        }
     
        selectedSard.setAccessoryList(accessoryList);
        this.setCost(selectedSard);
    }

    
    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        cancel();
    }

    public void cancel() {
        this.selected = null;
    }
    
    public void submmit0() {
        if (selected.getSartList().size() == 0) {
                String url = "norespond" ;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("url", url);
                
               WebUtil.addWarnMessage("请先添加申请明细，再进行提交！");
               return;
       } else {
            String name = orgInfoDao.getByUid(selected.getClinicalDeptUid()).getName();
            selected.setClinicalDeptName(name);
            selected.setApplyTime(new Date());
            selected.setCreateDate(new Date());

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            selected.setInvalidTime(cal.getTime());

            dao.save(selected);
            apdDao.save(selected.getSartList());

            //调用微服务
            try {
                sharedAssetService.pushWxMsg(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            String url = "List.xhtml?faces-redirect=true";
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", url);
            return;
       }
       
    }
    
   public void submmitR0() throws Exception {
       this.save();
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 0;
       
       if(sta == 7){
           state = 8;
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else{
           WebUtil.addErrorMessage("当前状态不是可提交状态！");
           return;
       }
       
    }
    
    public void refers() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 4;//4为取消借用
       if(sta == 0||sta == 1||sta ==2|| sta == 5){
           dao.updateState(uid, state);

           String iouId = uid;
                
            List<SharedAssetReqDetail> ListAll = asdDao.selectBySharedId(iouId);
            int x = 0;
            if(ListAll.size()>0){

                for(SharedAssetReqDetail dtl : ListAll){
                    
                    String assetInfoId = dtl.getAssetInfoId();
                    AssetInfo infoVo = new AssetInfo();
                    infoVo = asiDao.findByUid(assetInfoId);
                    infoVo.setSwapTypeState("0");
                    asiDao.save(infoVo);//将AssetInfo的借用状态还原
                }
            }
             asdDao.delete(ListAll);//删除借用设备信息

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可取消借用！");
           return;
       }
       
    }
    public void submmitR() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 12;//12归还待确认—确认—>6借用完成
       
       if(sta == 9){//借用待确认状态
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可确认归还！");
           return;
       }
       
    }
    
    public void Accept() throws Exception {
       String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 2;//2借用待交付状态

       List<SharedAssetReqType> sartList = selected.getSartList();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(sta == 1) {
        	dao.updateStateAndOwner(uid, state, UserContextService.getCurrentUserAccount().getLoginName());
        } 
        if(sta == 5){
        	dao.updateState(uid, state);
        }
        if(sta ==1|| sta == 5){//借用待受理状态/借用搁置状态
            apdDao.save(sartList);

            //调用微服务
            try {
                sharedAssetService.pushWxMsg(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                sharedAssetService.remoteAcceptRent(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            String url = "List.xhtml?faces-redirect=true";
            requestContext.addCallbackParam("url", url);
            return;
        }else {
            WebUtil.addErrorMessage("申请已取消或已受理，请返回并刷新！");
            requestContext.addCallbackParam("url","norespond");
            return;
        }
    }
    
    public void AcceptR() throws Exception {
       String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 8;//2归还待交付状态
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(sta == 7) {
        	dao.updateStateAndOwner(uid, state, UserContextService.getCurrentUserAccount().getLoginName());
        }
        if(sta == 11) {
        	dao.updateState(uid, state);
        }
       if(sta  == 7 || sta == 11){//归还待受理状态
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
           String url = "List.xhtml?faces-redirect=true";
           requestContext.addCallbackParam("url", url);
           return;
       }else {
           WebUtil.addErrorMessage("申请已取消或已受理，请返回并刷新！");
           requestContext.addCallbackParam("url","norespond");
           return;
       }
    }
    
    public void Shelve() throws Exception {
       String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 5;//5为借用搁置状态
        RequestContext requestContext = RequestContext.getCurrentInstance();
       if(sta == 1){//借用待受理状态
           dao.updateState(uid, state);

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
           String url = "List.xhtml?faces-redirect=true";
           requestContext.addCallbackParam("url", url);
           return;
       }else {
           WebUtil.addErrorMessage("申请已取消或已搁置受理，请返回并刷新！");
           requestContext.addCallbackParam("url","norespond");
           return;
       }
    }

    public void ShelveR() throws Exception {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
        int state = 11;//归还搁置状态
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(sta == 7){//待受理状态
            dao.updateState(uid, state);

            //调用微服务
            try {
                sharedAssetService.pushWxMsg(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            String url = "List.xhtml?faces-redirect=true";
            requestContext.addCallbackParam("url", url);
            return;
        }else {
            WebUtil.addErrorMessage("当前状态不可搁置！");
            requestContext.addCallbackParam("url","norespond");
            return;
        }
    }
    
    public void deliver() throws Exception {

        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
        int state = 3;//完成交付进入借用待确认状态
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if(sta == 2){
           //处理多用户申请
           String msg = "";
           List<AssetInfo> infoList = selected.getInfoList();
           List<SharedAssetReqDetail> sardList = selected.getSardList();
           List<SharedAssetReqType> sartList = selected.getSartList();
           if (sardList == null || sardList.size() == 0) {
               String url = "norespond" ;
                requestContext.addCallbackParam("url", url);
               WebUtil.addWarnMessage("请录入设备！");
               return;
           }
           for (int i = 0; i < infoList.size(); i++) {
               AssetInfo info = infoList.get(i);
               AssetInfo realInfo = asiDao.findByUid(info.getUid());
               List<WorkflowConfig> wfcList = wfCfigDao.findByDeptForSharedAsset(realInfo.getClinicalDeptUID());
               if (wfcList == null || wfcList.size() == 0) {
                   msg = msg + "设备 ‘" + info.getDepartNum() +"’ 刚已被科室 ‘" + realInfo.getClinicalDeptName() + "’ 借走，设备已移除。 ";
                   for (int j = 0; j < sardList.size(); j++) {
                       SharedAssetReqDetail sard = sardList.get(j);
                       if (sard.getAssetNumber().equals(info.getDepartNum())) {
                           sardList.remove(j);
                           break;
                       }
                   }
                    //处理sartList
                    for (SharedAssetReqType sharedAssetReqType : sartList) {
                        if (sharedAssetReqType.getSharedAssetTypeId().equals(info.getSharedAssetTypeUid())) {
                            sharedAssetReqType.setActualQty(sharedAssetReqType.getActualQty()-1);
                            selectedSart = sharedAssetReqType;//明细表联动选中
                            break;
                        }
                    }
                   infoList.remove(i);
                   i--;
               }
           }
           if (!msg.equals("")) {
               requestContext.addCallbackParam("url", "norespond");
               WebUtil.addWarnMessage(msg);
               return;
           } else {
                selected.setState(state);
                dao.save(selected);
                asiDao.save(selected.getInfoList());

                apdDao.save(selected.getSartList());

                asdDao.save(selected.getSardList());

               //调用微服务
               try {
                   sharedAssetService.pushWxMsg(selected.getId());
               }catch (Exception e){
                   e.printStackTrace();
               }
               String url = "List.xhtml?faces-redirect=true";
                requestContext.addCallbackParam("url", url);
                return;
           }
          
       }else {
           WebUtil.addErrorMessage("当前不是可交付状态，请返回！");
           requestContext.addCallbackParam("url", "norespond");
           return;
       }
    }
    
    public void deliverR() throws Exception {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 9;//完成交付
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if(sta == 8){//借用待交付
          //处理多用户申请
           String msg = "";
           List<AssetInfo> infoList = selected.getInfoList();
           List<SharedAssetReqDetail> sardList = selected.getSardList();
           List<SharedAssetReqType> sartList = selected.getSartList();
           if (sardList == null || sardList.size() == 0) {
               String url = "norespond" ;
                requestContext.addCallbackParam("url", url);
               WebUtil.addWarnMessage("请录入设备！");
               return;
           }
           for (int i = 0; i < infoList.size(); i++) {
               AssetInfo info = infoList.get(i);
               AssetInfo realInfo = asiDao.findByUid(info.getUid());
               List<WorkflowConfig> wfcList = wfCfigDao.findByDeptForSharedAsset(realInfo.getAssetDeptUID());
               if (wfcList != null && wfcList.size() > 0) {
                   msg = msg + "设备 ‘" + info.getDepartNum() +"’ 刚刚已被归还，请重新录入！ ";
                   for (int j = 0; j < sardList.size(); j++) {
                       SharedAssetReqDetail sard = sardList.get(j);
                       if (sard.getAssetNumber().equals(info.getDepartNum())) {
                           sardList.remove(j);
                           break;
                       }
                   }
                   //处理sartList
                    for (SharedAssetReqType sharedAssetReqType : sartList) {
                        if (sharedAssetReqType.getSharedAssetTypeId().equals(info.getSharedAssetTypeUid())) {
                            sharedAssetReqType.setActualQty(sharedAssetReqType.getActualQty()-1);
                            selectedSart = sharedAssetReqType;//明细表联动选中
                            break;
                        }
                    }
                   infoList.remove(i);
                   i--;
               }
           }
           if (!msg.equals("")) {
                String url = "norespond" ;
                requestContext.addCallbackParam("url", url);
                
               WebUtil.addWarnMessage(msg);
               return;
           } else {
                selected.setState(state);
                dao.save(selected);
                asiDao.save(selected.getInfoList());

               apdDao.save(selected.getSartList());

                asdDao.save(selected.getSardList());

               //调用微服务
               try {
                   sharedAssetService.pushWxMsg(selected.getId());
               }catch (Exception e){
                   e.printStackTrace();
               }
               String url = "List.xhtml?faces-redirect=true";
                requestContext.addCallbackParam("url", url);
                return;
           }
       }else {
           WebUtil.addErrorMessage("当前不是可交付状态，请返回！");
            requestContext.addCallbackParam("url", "norespond");
           return;
       }
    }

    public int getSwapQty2(String Uid,String clinicalDeptUID){
        if(null == Uid||Uid.equalsIgnoreCase("")){
            return 0;
        }else{
            List<AssetInfo> objectList = new ArrayList<AssetInfo>();
            objectList = swapDao.findbyBorrowQty(clinicalDeptUID,Uid);
          
            return objectList.size();
        }
    }
    public int getSwapQty(){
        String Uid = this.selectedSart.getSharedAssetTypeId();
        if(null == Uid||Uid.equalsIgnoreCase("")){
            return 0;
        }else{
            List<AssetInfo> objectList = new ArrayList<AssetInfo>();
            UserAccount user = UserContextService.getCurrentUserAccount();
            objectList = swapDao.findbyBorrowQty(user.getOrgInfoUID(),Uid);

            return objectList.size();
        }
    }
    
    //
    private String filterState = null;

    public String getFilterState() {
        return filterState;
    }

    public void setFilterState(String filterState) {
        this.filterState = filterState;
    }

    public void setStatusFilter() {
        if (filterState == null && filterApplyTime == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterState != null) {
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.EQ, filterState));
//            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
        }
        /*if (assetId != null) {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, assetId));
        }*/
        if (filterApplyTime != null && "1".equals(filterApplyTime)) {
            Date applyTime = new Date();
            //2个月内，则是保修到期时间在2个月后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(applyTime);
            c.add(Calendar.MONTH, 2);
            applyTimeTo = c.getTime();
            searchFilters.add(new SearchFilter("applyTime", SearchFilter.Operator.LTE, applyTimeTo));
//            searchFilters.add(new SearchFilter("isWarranty", SearchFilter.Operator.EQ, true));
//            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getSiteId()));
            searchFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
            //filterApplyTime = null;
        }
    }
    
    public void saveSart(){
        if (selectedSart.getApplyQty() == 0){
            String message = "wrong" ;
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("message", message);

            WebUtil.addWarnMessage("申请数必须大于0！");
            return;
        }
        selectedSart.setId(UUID.randomUUID().toString().replace("-", ""));
        String swapName = swapDao.findById(selectedSart.getSharedAssetTypeId()).getSwapName();
        selectedSart.setSwapName(swapName);
        selectedSart.setSharedId(selected.getId());
        List<SharedAssetReqType> sartList = selected.getSartList();
         if (sartList != null && sartList.size()!=0 ) {
             for (int i = 0; i < sartList.size(); i++) {
                SharedAssetReqType sharedAssetReqType = sartList.get(i);
                
                if (sharedAssetReqType.getSwapName().equals(swapName)) {
                    sartList.remove(i);
                    i--;
                  
                }
               
             }
             
         }
         sartList.add(selectedSart);
    }
    
    
    
    /**
     * 以下是老版本的一些方法暂做保留
     */
    
    public void refersR() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
              int state = 10;//11为取消归还
       if(sta == 7 ||sta == 8 || sta == 11){

           dao.updateState(uid, state);
            String iouId = uid;
            List<SharedAssetReqDetail> ListAll = asdDao.selectByIouId(iouId);
            int x = 0;
            if(ListAll.size()>0){

                for(SharedAssetReqDetail dtl : ListAll){
                    
                    String assetInfoId = dtl.getAssetInfoId();
                    AssetInfo infoVo = new AssetInfo();
                    infoVo = asiDao.findByUid(assetInfoId);
                    infoVo.setSwapTypeState("1");
                    asiDao.save(infoVo);//将AssetInfo的借用状态还原

                }
            }
            asdDao.delete(ListAll);

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可取消归还！");
           return;
       }
       
    }
    public void onRowEditSart(RowEditEvent event){
        SharedAssetReqType sart = (SharedAssetReqType) event.getObject();
        List<SharedAssetReqType> sartList = selected.getSartList();
        for (SharedAssetReqType sharedAssetReqType : sartList) {
            if (sharedAssetReqType.getId() == sart.getId()){
                sharedAssetReqType.setExceptQty(sart.getExceptQty());
            }
        }

    }
    
    public void submmit() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 6;//3借用待确认—确认—>6借用完成
       
       if(sta == 3){//借用待确认状态
           dao.updateState(uid, state);

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可确认借用！");
           return;
       }
       
    }
    
    public void submmit1() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 2;//
     
       if(sta == 1){//借用待确认状态
           dao.updateState(uid, state);

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可受理！");
           return;
}

    }
        
    public void submmit2() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 0;
       
       if(1==0){//判断是否部分归还(还未做，暂用1==0代替判断条件)
           state = 5;//5为部分归还状态
       }
       
       if(sta == 7){
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可确认归还！");
           return;
       }
       
    }
        
    public void submmit5() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 6;//
     
       if(sta == 5){//借用待确认状态
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可受理！");
           return;
        
       }

    }
    
    
    public void refers2() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 0;
       if(1==0){//判断是否有部分已经归还(还未做，暂用1==0代替判断条件)
           state = 4;//4为待归还状态
       }else{
           state = 5;//5为部分归还状态

       }
           if(sta == 6||sta == 7){
           dao.updateState(uid, state);
               //调用微服务
               try {
                   sharedAssetService.pushWxMsg(selected.getId());
               }catch (Exception e){
                   e.printStackTrace();
               }
       }else {
           WebUtil.addErrorMessage("当前状态不可取消归还！");
           return;
       }
    }
    
    
    public void upLoad() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 6;
       if(sta == 4||sta == 5){
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可申请归还！");
           return;
       }
    }

    public String getNameByLoginName(String loginName){
        List<String> nameList = dao.findNameByLoginName(loginName);
        if (nameList != null && nameList.size() > 0)
            return nameList.get(0);
        return loginName;
    }

    public String getNameByLoginName2(){
        return this.getNameByLoginName(selected.getApplicant());
    }




    /**
     * @return the total
     */
    public Double getTotal() {
        
        List<SharedAssetReqDetail> detailList  = asdDao.findByReturnId(this.selected.getId());
        Double sum;
        sum = 0.0;
        if(detailList.size()>0){
            for(SharedAssetReqDetail detail :detailList){
                sum = sum + detail.getCost();
            }
            return sum;
        }
        else{
            return 0.0;
        }
    }

    /**
     * @param total the total to set
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }
    
    public String workOrderRouter() {
    	String retrunUrl = "";
    	RequestContext requestContext = RequestContext.getCurrentInstance();
    	requestContext.addCallbackParam("showNotification", true);
    	
    	boolean onDuty = true;
    	try {
    		onDuty = sharedAssetService.onDuty();
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	if(onDuty) {
    		requestContext.addCallbackParam("showNotification", false);
            requestContext.addCallbackParam("url", UrlEncryptController.encodeUrlParam("Edit?actionName=Create"));
            retrunUrl = UrlEncryptController.encodeUrlParam("Edit?actionName=Create");
    	}
    	
    	return retrunUrl;
    }
    
    public String getOnCallNumber() {
    	List<SharedAssetConfig> sharedAssetConfigList = sharedAssetConfigDao.findBySiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
    	String onCallNumber = "";
    	if(sharedAssetConfigList != null && sharedAssetConfigList.size() > 0) {
    		onCallNumber = Optional.ofNullable(sharedAssetConfigList.get(0)).map(item -> item.getTel()).orElse("");
    	}
    	return onCallNumber;
    }
    
    public void prepareTransferRequest() {
    	selectedAssignee = new UserAccount();
    }

    public List getAvailableAssignees() {
    	List<LinkedHashMap> rst = null;
        try {
        	ResponseEntity<LinkedHashMap> response = sharedAssetService.getAssigneeList(selected.getId());
            LinkedHashMap respBody = response.getBody();
            if("OK".equals(respBody.get("bizStatusCode"))) {
            	List<LinkedHashMap> assignees = (List) respBody.get("data");
            	UserAccount user = UserContextService.getCurrentUserAccount();
            	rst = assignees.stream()
            						.filter(item -> !user.getUid().equals(item.get("uid")))
            						.collect(Collectors.toList());
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return rst;
    }
    
    public void transferRequest() {
    	String url = "List.xhtml?faces-redirect=true";
    	RequestContext requestContext = RequestContext.getCurrentInstance();
    	try {
    		sharedAssetService.remoteAssignRequest(selected.getId(), selectedAssignee.getUid());
    	} catch(Exception ex) {
    		url = "norespond";
    		WebUtil.addErrorMessage("转单失败，请返回重新申请！");
    		ex.printStackTrace();
    	}
    	requestContext.addCallbackParam("url", url);
    }

    public boolean validateAllExpectedQty() {
        List<SharedAssetReqType> sartList = selected.getSartList();
        boolean expectedQtyGTZero = false;
        for (SharedAssetReqType sart : sartList) {
            if (sart.getExceptQty() > 0){
                expectedQtyGTZero = true;
                break;
            }
        }
        logger.debug("validateTotalExpectedQty result: " + expectedQtyGTZero);
        return !expectedQtyGTZero;
    }

    public String getOrgLocation() {
        StringBuilder orgLoc = new StringBuilder();
        if(this.selected != null) {
            String orgUid = this.selected.getClinicalDeptUid();
            OrgInfo orgInfo = orgInfoDao.findByUid(orgUid);
            if(StringUtils.isNotBlank(orgInfo.getBuildingNum())) {
                orgLoc.append(orgInfo.getBuildingNum());
                orgLoc.append("楼");
            }
            if(StringUtils.isNotBlank(orgInfo.getFloorNum())) {
                orgLoc.append(orgInfo.getFloorNum());
                orgLoc.append("层");
            }
            if(StringUtils.isNotBlank(orgInfo.getOther())) {
                orgLoc.append(orgInfo.getOther());
            }
        }
        return orgLoc.toString();
    }

    public Double getTotalAmount(SharedAssetRequest request) {
        double totalAmount = 0d;
        SharedAssetReqDetail reqDetail = null;
        List<SharedAssetReqDetail> reqDetailList = null;
        if("1".equals(request.getBehavior())) {//借用请求
            reqDetailList = asdDao.findByBorrowId(request.getId());
        } else {
            reqDetailList = asdDao.findByReturnId(request.getId());
        }
        reqDetail = reqDetailList.stream().findFirst().orElse(null);
        if(reqDetail != null) {
            AssetInfo assetInfo = asiDao.findByUid(reqDetail.getAssetInfoId());
            SharedAssetType sharedAssetType = swapDao.findById(assetInfo.getSharedAssetTypeUid());
            Tuple3<Long, String, BigDecimal> billingDetail = AllocationCenterUtils.getBillingDetail(sharedAssetType, reqDetail);
            totalAmount = billingDetail._3.doubleValue();
        }
        return totalAmount;
    }

    public Double getTotalAmount(SharedAssetReqDetail reqDetail) {
        double totalAmount = 0d;
        if(reqDetail != null) {
            AssetInfo assetInfo = asiDao.findByUid(reqDetail.getAssetInfoId());
            SharedAssetType sharedAssetType = swapDao.findById(assetInfo.getSharedAssetTypeUid());
            Tuple3<Long, String, BigDecimal> billingDetail = AllocationCenterUtils.getBillingDetail(sharedAssetType, reqDetail);
            totalAmount = billingDetail._3.doubleValue();
        }
        return totalAmount;
    }

}
    
    
    