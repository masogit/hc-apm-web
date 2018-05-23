package com.ge.apm.view;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SharedAssetRequestNewController extends GenericCRUDUUIDController<SharedAssetRequest> {

    SharedAssetRequestRepository dao = null;
    AssetInfoRepository assetInfoDao = null;
    SharedAssetReqDetailRepository detailDao = null;
    private String applyTimeFormatTime = null;
    OrgInfoRepository orgInfoDao = null;
    SharedAssetReqDetailRepository asdDao = null;
    private List<AssetInfo> selectInfo ;
    private SharedAssetReqDetail selectedSard;
    private UaaService uuaService;
    SharedAssetReqTypeRepository apdDao = null;
    AssetInfoRepository asiDao = null;
    SharedAssetTypeRepository swapDao = null;
    private String filterApplyTime = null;
    private Date applyTimeTo = null;
    private Date applyTimeFrom = null;
    public SharedAssetReqDetail getSelectedSard() {
        return selectedSard;
    }

    public void setSelectedSard(SharedAssetReqDetail selectedSard) {
        this.selectedSard = selectedSard;
    }
    private String Num;
    private boolean confirm;
    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        asdDao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        asiDao = WebUtil.getBean(AssetInfoRepository.class);
        dao = WebUtil.getBean(SharedAssetRequestRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        swapDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
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
    }
    @Override
    public void save(){
        this.selected.setState(0);
        super.save();
    }
    public void confirm(String type){
        if("借用".equals(type)){
        this.selected.setState(6);
        if(!detailDao.findBySharedId(this.selected.getId()).isEmpty()){
            List<SharedAssetReqDetail> detail = detailDao.findBySharedId(this.selected.getId());
            for(SharedAssetReqDetail details:detail){
            details.setState("0");
            detailDao.save(details);
        }
        }
        super.save();
        }
        else{
            this.selected.setState(12);
            super.save();
        }
    }
    public void cancel(){
        this.selected.setState(0);
        super.save();
    }

    @Override
    protected SharedAssetRequestRepository getDAO() {
        return dao;
    }
    public void onRowEdit() throws IOException {
        String url = "actionName=Edit&selectedid="
                + selected.getId();
        url = "Edit_1.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        //FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    @Override
    protected Page<SharedAssetRequest> loadData(PageRequest pageRequest) {
        setUserSearchFilters();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.IsNotNull,null));
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    private void setUserSearchFilters() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

//        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//        //申请日期过滤
//        if (applyTimeFormatTime != null ) {
//            String[] split = applyTimeFormatTime.split("~");
//            if (split.length == 1) {
//                try {
//                    Date dateFrom = sdf.parse(split[0] + " 00:00:00");
//                    searchFilters.add(new SearchFilter("applyTime", SearchFilter.Operator.GTE, dateFrom));
//                } catch (ParseException ex) {
//                    Logger.getLogger(SharedAssetRequestV2Controller.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } else {
//                if (split[0].equals("")) {
//                    try {
//                        Date dateTo = sdf.parse(split[1] + " 23:59:59");
//                        searchFilters.add(new SearchFilter("applyTime", SearchFilter.Operator.LTE, dateTo));
//                    } catch (ParseException ex) {
//                        Logger.getLogger(SharedAssetRequestV2Controller.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                } else {
//                    try {
//                        Date dateFrom = sdf.parse(split[0] + " 00:00:00");
//                        Date dateTo = sdf.parse(split[1] + " 23:59:59");
//                        searchFilters.add(new SearchFilter("applyTime", SearchFilter.Operator.GTE, dateFrom));
//                        searchFilters.add(new SearchFilter("applyTime", SearchFilter.Operator.LTE, dateTo));
//                    } catch (ParseException ex) {
//                        Logger.getLogger(SharedAssetRequestV2Controller.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//
//        }


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
    
    public void getAssetInfo(Date times,Date timesa,String accepter,String ClinicalUid) throws ParseException{//查询并插入借用设备信息
        List<SharedAssetReqDetail> sardList = selected.getSardList();
        Date endsTime = null;
        List<AssetInfo> assetInfoList = selected.getInfoList();
        String ClinicalName = orgInfoDao.findByUid(ClinicalUid).getName();
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
                
                String ClinicalDeptUID = infoVo.getClinicalDeptUID();
                if(null==infoVo.getSharedAssetTypeUid()|| infoVo.getSharedAssetTypeUid().equalsIgnoreCase("0")){
                    WebUtil.addErrorMessage("该设备不属于调剂设备类型！");
                    return;
                }else if(infoVo.getStatus()!= 1 && infoVo.getStatus()!= 3){
                    WebUtil.addErrorMessage("该设备故障，不可借用！");
                    return; 
                }
                //在asset_info中判断是否属于 调剂中心科室
                else if(!depts.contains(ClinicalDeptUID)){
                    WebUtil.addErrorMessage("该设备已借出！");
                    return; 
                }
                else if(!depts.contains(userOrg.getUid())){
                    WebUtil.addErrorMessage("当前角色非调剂中心人员，请核实后操作！");
                    return; 
                }
                else{
    //            }
    //            if("0".equals(infoVo.getSwapTypeState())){//判断是否调剂设备
                                        SharedAssetType swapType = swapDao.findById(infoVo.getSharedAssetTypeUid());
                                        SharedAssetReqDetail assetDetaiVo = new SharedAssetReqDetail();
                                        assetDetaiVo.setChargingBeginTime(times);
                                        assetDetaiVo.setBorrowId(Uid);
                                        assetDetaiVo.setReturnId(Uid);
                                        assetDetaiVo.setAssetInfoId(infoVo.getUid());
                                        assetDetaiVo.setAssetName(infoVo.getName());
                                        assetDetaiVo.setAssetNumber(infoVo.getDepartNum());
//                                        assetDetaiVo.setClinicalDeptUid(selected.getClinicalDeptUid());
//                                        assetDetaiVo.setClinicalDeptName(selected.getClinicalDeptName());
        //                                assetDetaiVo.setState("0");//0：待归还 1：已归还
                                        assetDetaiVo.setBorrowDealUid(UserContextService.getCurrentUserAccount().getUid());//受理人
                                        assetDetaiVo.setFinancingNum(infoVo.getFinancingNum());//资产编号
                                        String swaptypeid = infoList.get(0).getSharedAssetTypeUid();
                                        SharedAssetType swapTypeVo = swapDao.findById(swaptypeid);
                                        assetDetaiVo.setSharedAssetTypeId(swaptypeid);
                                        selected.setClinicalDeptName(ClinicalName);
                                        selected.setClinicalDeptUid(ClinicalUid);
                                        //权限字段
                                        assetDetaiVo.setHospitalUID(selected.getHospitalUID());
                                        assetDetaiVo.setInstitutionUID(selected.getInstitutionUID());
                                        assetDetaiVo.setSiteUID(selected.getSiteUID());
                                        assetDetaiVo.setTenantUID(selected.getTenantUID());
                                        
                                        //时间往后推
                                        assetDetaiVo.setClinicalDeptUid(ClinicalUid);
                                        assetDetaiVo.setClinicalDeptName(ClinicalName);

                                        if(timesa==null){
                                            infoVo.setClinicalDeptName(ClinicalName);
                                            infoVo.setClinicalDeptUID(ClinicalUid);
                                            if (!"4".equals(swapType.getChargingType())&&!"3".equals(swapType.getChargingType())){
                                                assetDetaiVo.setChargingBeginTime(this.setBeginTime(times));
                                            }
                                        }
                                        else{
//                                            assetDetaiVo.setChargingBeginTime(times);
                                            if (!"4".equals(swapType.getChargingType())&&!"3".equals(swapType.getChargingType())){
                                                assetDetaiVo.setChargingBeginTime(this.setBeginTime(times));
                                                assetDetaiVo.setChargingEndTime(this.setEndTime(timesa));
                                            }
                                            else{
                                                assetDetaiVo.setChargingEndTime(timesa);
                                            }
                                            assetDetaiVo.setClinicalDeptForReturnUid(ClinicalUid);
                                            assetDetaiVo.setClinicalDeptForReturnName(ClinicalName);
                                            endsTime = timesa;
//                                            if( "1".equals(this.selected.getType())){
//                                               assetDetaiVo.setChargingType("维修借用");
//                                             }
                                    
                                            if("1".equals(swapTypeVo.getChargingType())){
                                                assetDetaiVo.setCountQty(1);
                                                assetDetaiVo.setCost(swapTypeVo.getUnitPrice());
                                            }
                                            else if("2".equals(swapTypeVo.getChargingType())){
                                            long nd = 1000 * 24 * 60 * 60;
                                            long nh = 1000 * 60 * 60;
                                            long nm = 1000 * 60;
                                            long diff = (assetDetaiVo.getChargingEndTime()).getTime() - (assetDetaiVo.getChargingBeginTime()).getTime();
                                            Integer countQty = null;
                                            //long time = (diff % nd / nh)*2;
                                            // 计算差多少天
                                            long day = diff / nd;
                                            // 计算差多少小时
                                            long hour = diff % nd / nh;
                                            // 计算差多少分钟
                                            long min = diff % nd % nh / nm;
                                            System.out.println(day);
                                            System.out.println(hour);
                                            System.out.println(min);
                                            countQty = (int)(day*24*60/30+hour*2+min/30);
                                            assetDetaiVo.setCountQty(countQty);
                                            assetDetaiVo.setCost(countQty*swapTypeVo.getUnitPrice()/2);
                                        }
                                            else if("4".equals(swapTypeVo.getChargingType())){
                                                long nd = 1000 * 24 * 60 * 60;
                                                long nh = 1000 * 60 * 60;
                                                long nm = 1000 * 60;
                                                long diff = (assetDetaiVo.getChargingEndTime()).getTime() - (assetDetaiVo.getChargingBeginTime()).getTime();
                                                Integer countQty = null;
                                                //long time = (diff % nd / nh)*2;
                                                // 计算差多少天
                                                long day = diff / nd;
                                                // 计算差多少小时
                                                long hour = diff % nd / nh;
                                                // 计算差多少分钟
                                                long min = diff % nd % nh / nm;
                                                double number = day * 24 + hour + min/60.0;
                                                number = number < 0 ? 0 : number;
                                                assetDetaiVo.setCost(number*swapTypeVo.getUnitPrice());
                                            }
                                        else{
                                            Calendar cal1 = Calendar.getInstance();
                                            cal1.setTime(assetDetaiVo.getChargingBeginTime());
                                            Calendar cal2 = Calendar.getInstance();
                                            cal2.setTime((assetDetaiVo.getChargingEndTime()));
                                            int day1= cal1.get(Calendar.DAY_OF_YEAR);
                                            int day2 = cal2.get(Calendar.DAY_OF_YEAR);
                                            int year1 = cal1.get(Calendar.YEAR);
                                            int year2 = cal2.get(Calendar.YEAR); 
                                            Date date1 = assetDetaiVo.getChargingBeginTime();
                                            Date date2 = assetDetaiVo.getChargingEndTime();
                                            long  between = date2.getTime() - date1.getTime();
                                            if(year1 != year2)   //同一年
                                                {
                                                    int timeDistance = 0 ;
                                                    for(int i = year1 ; i < year2 ; i ++)
                                                    {
                                                        if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
                                                        {
                                                            timeDistance += 366;
                                                        }
                                                        else    //不是闰年
                                                        {
                                                            timeDistance += 365;
                                                        }
                                                    }
                                                    if(between >= (24* 3600000)){
                                                    assetDetaiVo.setCountQty(timeDistance + (day2-day1));
                                                    assetDetaiVo.setCost((timeDistance + (day2-day1))*swapTypeVo.getUnitPrice());
                                                    }
                                                    else{
                                                         assetDetaiVo.setCountQty(0);
                                                         assetDetaiVo.setCost(null);
                                                    }
                                                }
                                            else    //不同年
                                            {  if(between >= (24* 3600000)){
                                                    assetDetaiVo.setCountQty(day2-day1);
                                                    assetDetaiVo.setCost((day2-day1)*swapTypeVo.getUnitPrice());
                                               }
                                                else{
                                                    assetDetaiVo.setCountQty(0);
                                                    assetDetaiVo.setCost(null);
                                                }
                                            }
                                        }
                                        }
                                        if(!"".equals(accepter)){
                                            assetDetaiVo.setAccepter(accepter);
                                        }
                                        if( "1".equals(this.selected.getType())){
                                               assetDetaiVo.setChargingType("维修借用");
                                               assetDetaiVo.setCost(null);
                                        }
                                        
//                                        if(times==null&&"".equals(accepter)){
//                                            assetDetaiVo.setAccepter(acceptman);
//                                            Calendar calendar = Calendar.getInstance();
//                                            Date date = new Date();
//                                            calendar.setTime(date);
//                                            int hour  = calendar.get(Calendar.HOUR_OF_DAY);
//                                            int month = calendar.get(Calendar.MONTH) + 1;
//                                            String year =  format(date).substring(0, 4);
//                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
//                                            int minute = calendar.get(Calendar.MINUTE);
//                                            if(minute>=0&&minute<30){
//                                                String startTime = year+"-"+month+"-"+day+" "+hour+":"+"30:00";
//                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                Date beginTime = dateFormat.parse(startTime);
//                                                assetDetaiVo.setChargingBeginTime(beginTime);
//                                            }
//                                            else{
//                                                int hours = hour+1;
//                                                String startTime = year+"-"+month+"-"+day+" "+hours+":"+"00:00";
//                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                Date beginTime = dateFormat.parse(startTime);
//                                                assetDetaiVo.setChargingBeginTime(beginTime);
//                                            }
//                                        }
//                                        else if(times!=null&&!"".equals(accepter)){
//                                            assetDetaiVo.setChargingBeginTime(times);
//                                            assetDetaiVo.setAccepter(accepter);
//                                        }
//                                        else if(times==null&&!"".equals(accepter)){
//                                            assetDetaiVo.setAccepter(accepter);
//                                            Calendar calendar = Calendar.getInstance();
//                                            Date date = new Date();
//                                            calendar.setTime(date);
//                                            int hour  = calendar.get(Calendar.HOUR_OF_DAY);
//                                            int month = calendar.get(Calendar.MONTH) + 1;
//                                            String year =  format(date).substring(0, 4);
//                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
//                                            int minute = calendar.get(Calendar.MINUTE);
//                                            if(minute>=0&&minute<30){
//                                                String startTime = year+"-"+month+"-"+day+" "+hour+":"+"30:00";
//                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                Date beginTime = dateFormat.parse(startTime);
//                                                assetDetaiVo.setChargingBeginTime(beginTime);
//                                            }
//                                            else{
//                                                int hours = hour+1;
//                                                String startTime = year+"-"+month+"-"+day+" "+hours+":"+"00:00";
//                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                Date beginTime = dateFormat.parse(startTime);
//                                                assetDetaiVo.setChargingBeginTime(beginTime);
//                                            }
//                                        }
//                                        else{
//                                            assetDetaiVo.setChargingBeginTime(times);
//                                            assetDetaiVo.setAccepter(acceptman);
//                                        }
                                        
                                    //附件状态默认全选
                                    for(SharedAssetTypeAccesssory item:swapTypeVo.getAccessoryList()){
                                        item.setBorrowStatus(true); 
                                        item.setReturnStatus(true);
                                    }
                                    assetDetaiVo.setAccessoryList(swapTypeVo.getAccessoryList());
                                    if( !"1".equals(this.selected.getType())){
                                        assetDetaiVo.setChargingType(swapTypeVo.getChargingType());//计价方式
                                    }
                                    assetDetaiVo.setUnitPrice(swapTypeVo.getUnitPrice());//单价
                                    assetDetaiVo.setAssetInfoId(infoList.get(0).getUid());
                                    assetDetaiVo.setCreateDate(new Date());
                                    assetDetaiVo.setCreateBy(UserContextService.getCurrentUserAccount().getUid());
                                    //asdDao.save(assetDetaiVo);
                                    
                                    assetDetaiVo.setId(UUID.randomUUID().toString().replace("-", ""));
                                   
                                    if (sardList != null && sardList.size() > 0) {
                                        for (int i = 0; i < sardList.size(); i++) {
                                            SharedAssetReqDetail sard = sardList.get(i);
                                            if (assetDetaiVo.getAssetNumber().equals(sard.getAssetNumber())) {
                                                selectedSard = sard;
                                                WebUtil.addErrorMessage("该设备已录入，请勿重复录入！");
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
                                        
                                    
    //                                infoVo.setSwapTypeState("1");
                                    //更改使用科室
//                                    infoVo.setClinicalDeptName(this.selected.getClinicalDeptName());
//                                    infoVo.setClinicalDeptUID(this.selected.getClinicalDeptUid());
    //                                String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
    //                                OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
    //                                if(userOrg.getOrgType()==4){
    //                                    infoVo.setClinicalDeptName(userOrg.getName());
    //                                    infoVo.setClinicalDeptUID(userOrg.getUid());
    //                                }
    
                                    //asiDao.save(infoVo);
                                    
                                    assetInfoList.add(infoVo);

                                    //apdDao.save(applySwapType);
                                    WebUtil.addSuccessMessage("录入成功！");
                                    return;
                       
                }
            }else{
                WebUtil.addErrorMessage("录入信息不符，请核对后录入！");
                return;
            }
    }
    
    public Date setBeginTime(Date beginTime){
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginTime);
        cal.set(Calendar.SECOND,0);
        int minutes = cal.get(Calendar.MINUTE);
        if (minutes>0 && minutes <=30){
            cal.set(Calendar.MINUTE,30);
        }else if(minutes >30) {
            cal.set(Calendar.MINUTE,0);
            cal.add(Calendar.HOUR,1);
        }
        return cal.getTime();
    }
    
    public Date setEndTime(Date endTime){
        Calendar cal = Calendar.getInstance();
        cal.setTime(endTime);
        cal.set(Calendar.SECOND,0);
        int minutes = cal.get(Calendar.MINUTE);
        if (minutes>=0 && minutes <30){
            cal.set(Calendar.MINUTE,0);
        }else {
            cal.set(Calendar.MINUTE,30);
        }
        return cal.getTime();
    }
    
    public void deleteBy(String uid){
        List<SharedAssetReqDetail> sardList = selected.getSardList();
         for (SharedAssetReqDetail sharedAssetReqDetail : sardList) {
             if (sharedAssetReqDetail.getId().equals(uid)) {
                 //清理infoList
                 String infoId = sharedAssetReqDetail.getAssetInfoId();
                 List<AssetInfo> assetInfoList = selected.getInfoList();
                 for (AssetInfo assetInfo : assetInfoList) {
                     if (assetInfo.getUid().equals(infoId)) {
                         assetInfoList.remove(assetInfo);
                         break;
                     }
                 }
                 sardList.remove(sharedAssetReqDetail);
                 break;
             }
         }
    }
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
        if(this.selectedSard.getAccessoryList()!=null){
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
        
    }
    public void deliverR() {
           String msg = "";
           List<AssetInfo> infoList = selected.getInfoList();
           List<SharedAssetReqDetail> sardList = selected.getSardList();
           if (sardList == null || sardList.size() == 0) {
               String url = "norespond" ;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("url", url);
               WebUtil.addWarnMessage("请录入设备！");
               return;
           }
           this.selected.setCreateDate(new Date());
//           System.out.println(selected.getInfoList().get(0).getClinicalDeptUID());
           dao.save(selected);
           asiDao.save(selected.getInfoList());
           asdDao.save(selected.getSardList());
       
    }
    public void setCost(SharedAssetReqDetail sard){
        if (sard.getChargingEndTime() != null) {
            if ("1".equals(selected.getType())) {
                sard.setChargingType("维修借用");
                sard.setCost(null);
            }
            if ("1".equals(sard.getChargingType())) {
                sard.setCountQty(1);
                sard.setCost(sard.getUnitPrice());
            } else if ("2".equals(sard.getChargingType())) {
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
                countQty = (int) (day * 24 * 60 / 30 + hour * 2 + min / 30);
                sard.setCountQty(countQty);
                sard.setCost(countQty * sard.getUnitPrice() / 2);
            } else if("4".equals(sard.getChargingType())){
                long nd = 1000 * 24 * 60 * 60;
                long nh = 1000 * 60 * 60;
                long nm = 1000 * 60;
                long diff = (sard.getChargingEndTime()).getTime() - (sard.getChargingBeginTime()).getTime();
                Integer countQty = null;
                //long time = (diff % nd / nh)*2;
                // 计算差多少天
                long day = diff / nd;
                // 计算差多少小时
                long hour = diff % nd / nh;
                // 计算差多少分钟
                long min = diff % nd % nh / nm;
                double number = day * 24 + hour + min/60.0;
                number = number < 0 ? 0 : number;
                sard.setCost(number*sard.getUnitPrice());
            }
            else {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(sard.getChargingBeginTime());
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(sard.getChargingEndTime());
                int day1 = cal1.get(Calendar.DAY_OF_YEAR);
                int day2 = cal2.get(Calendar.DAY_OF_YEAR);
                int year1 = cal1.get(Calendar.YEAR);
                int year2 = cal2.get(Calendar.YEAR);
                Date date1 = sard.getChargingBeginTime();
                Date date2 = sard.getChargingEndTime();
                long between = date2.getTime() - date1.getTime();
                if (year1 != year2) {  //同一年
                    int timeDistance = 0;
                    for (int i = year1; i < year2; i++) {
                        if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                        {
                            timeDistance += 366;
                        } else    //不是闰年
                        {
                            timeDistance += 365;
                        }
                    }
                    if (between >= (24 * 3600000)) {
                        sard.setCountQty(timeDistance + (day2 - day1));
                        sard.setCost((timeDistance + (day2 - day1)) * sard.getUnitPrice());
                    } else {
                        sard.setCountQty(0);
                        sard.setCost(null);
                    }
                } else {  //不同年
                    if (between >= (24 * 3600000)) {
                        sard.setCountQty(day2 - day1);
                        sard.setCost((day2 - day1) * sard.getUnitPrice());
                    } else {
                        sard.setCountQty(0);
                        sard.setCost(null);
                    }
                }
            }
            if ("1".equals(selected.getType())) {
                sard.setChargingType("维修借用");
                sard.setCost(null);
            }
        }
    }
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
        
         if (selectedSard.getChargingEndTime() != null){
          String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
          OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
          
         List<AssetInfo> assetInfoList = selected.getInfoList();
         AssetInfo asset = asiDao.findByUid(selectedSard.getAssetInfoId());
                 for (AssetInfo assetInfo : assetInfoList) {
                     if (assetInfo.getUid().equals(selectedSard.getAssetInfoId())) {

                         assetInfo.setClinicalDeptUID(userOrg.getUid());
                         assetInfo.setClinicalDeptName(userOrg.getName());
                         System.out.println(assetInfoList.get(0).getClinicalDeptUID());
                         break;
                     }
                 }
                 if(asset !=null){
                 asset.setClinicalDeptUID(userOrg.getUid());
                 asset.setClinicalDeptName(userOrg.getName());
                 assetInfoList.add(asset);
                 }
         }
         selectedSard.setAccessoryList(accessoryList);
         this.setCost(selectedSard);
     }
    @Override
    public void onBeforeNewObject(SharedAssetRequest object) {
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setCreateDate(new Date());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setApplyTime(new Date());
      //  object.setState(UserContextService.getCurrentUserAccount().getSiteUID());
//        object.setApplicant(UserContextService.getCurrentUserAccount().getName());
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
//        object.setClinicalDeptName(userOrg.getName());
//        object.setClinicalDeptUid(userOrg.getUid());
    }
    

    @Override
    public List<SharedAssetRequest> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    

/*
    @Override
    public void onBeforeNewObject(SharedAssetRequest object) {
    }
    
    @Override
    public void onAfterNewObject(SharedAssetRequest object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(SharedAssetRequest object) {
    }
    
    @Override
    public void onAfterUpdateObject(SharedAssetRequest object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(SharedAssetRequest object) {
    }
    
    @Override
    public void onAfterDeleteObject(SharedAssetRequest object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(SharedAssetRequest object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/

    /**
     * @return the selectInfo
     */
    public List<AssetInfo> getSelectInfo() {
        return selectInfo;
    }

    /**
     * @param selectInfo the selectInfo to set
     */
    public void setSelectInfo(List<AssetInfo> selectInfo) {
        this.selectInfo = selectInfo;
    }

    /**
     * @return the Num
     */
    public String getNum() {
        return Num;
    }

    /**
     * @param Num the Num to set
     */
    public void setNum(String Num) {
        this.Num = Num;
    }

    /**
     * @return the confirm
     */
  
}