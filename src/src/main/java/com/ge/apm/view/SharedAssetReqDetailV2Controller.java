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
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetReqDetailV2Controller extends GenericCRUDUUIDController<SharedAssetReqDetail>{

    SharedAssetReqDetailRepository dao = null;
    private String iouUid;
   
    private Integer iouId = -1;
    private List<String> selectedBorrowAccessoryList = null;
    private List<String> selectedReturnAccessoryList = null;
    AssetInfoRepository asiDao = null;
    SharedAssetReqTypeRepository apdDao = null;
    SharedAssetRequestRepository iouDao = null;
    OrgInfoRepository orgInfoDao = null;
    Integer behavior = null;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        asiDao = WebUtil.getBean(AssetInfoRepository.class);
        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        iouDao = WebUtil.getBean(SharedAssetRequestRepository.class);
        SharedAssetRequestV2Controller sharedAssetRequestV2Controller = WebUtil.getBean(SharedAssetRequestV2Controller.class);
        behavior = sharedAssetRequestV2Controller.getSelected().getBehavior();
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        iouUid = (String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if ("Create".equalsIgnoreCase(actionName)) {
            iouId = -2 ;
        }  
        
        selectedAssets = new ArrayList<SharedAssetReqDetail>();
    }
    
    
    @Override
    protected SharedAssetReqDetailRepository getDAO() {
        return dao;
    }
    
    
    @Override
    protected Page<SharedAssetReqDetail> loadData(PageRequest pageRequest) {
        if(behavior==1){
            return dao.findByBorrowId(pageRequest, iouUid);
        }else{
            return dao.findByReturnId(pageRequest, iouUid);
        }
    }
    
    public void onIouCreate(){
       
        iouId =-2;
    };
    
    public void cancelDialog(){
        showDialog = false;
    }
    
    
    private boolean showDialog = true;
    public boolean isShowDialog() {
        return showDialog;
    }
    
    public void creates(){
        
       showDialog = true;
    }
    
    public void onChanged(){
        iouId =-1;
        
        SharedAssetRequestController iouController = WebUtil.getBean(SharedAssetRequestController.class);
        if(iouController.getSelected()!=null){
                    
        }
       
    }
    
    
    @Override
    public List<SharedAssetReqDetail> getItemList() {
        return dao.find();
    }
    
//    @Override
//    public void onBeforeSave(SharedAssetReqDetail object) {
//        SharedAssetRequestController iouController = WebUtil.getBean(SharedAssetRequestController.class);
//        object.setSharedId(iouUid);
//        iouController.save();
//        String createId = iouController.getSelected().getId();
//        if(iouId == -2||iouId == 0){
//            iouUid = createId;
//        }
//        object.setSharedId(iouUid);
//        
//        if("小时".equals(object.getUnit())){
//           if(object.getChargingEndTime()!=null && object.getChargingBeginTime()!=null){
//               object.setCountQty(getDatePoor(object.getChargingEndTime(),object.getChargingBeginTime()));
//               object.setCost(object.getUnitPrice()*object.getCountQty());
//           }else{
//               object.setCountQty(0);
//               object.setCost(0);
//           }
//               
//       }else if("天".equals(object.getUnit())){
//           if(object.getChargingEndTime()!=null && object.getChargingBeginTime()!=null){
//               try {
//                   object.setCountQty(daysBetween(object.getChargingBeginTime(),object.getChargingEndTime()));
//               } catch (ParseException ex) {
//                   Logger.getLogger(SharedAssetReqDetailController.class.getName()).log(Level.SEVERE, null, ex);
//               }
//               object.setCost(object.getUnitPrice()*object.getCountQty());
//           }else{
//               object.setCountQty(0);
//               object.setCost(0);
//           }
//       }else if("次".equals(object.getUnit())){
//           
//           object.setCountQty(1);
//           object.setCost(object.getUnitPrice());
//       }
//        
//        
//        
//    }
    
    private String qtyId;
    private Date chargingBeginTime;
    private String borrowAttachInfo;
    private Date charginEndTime;
    private String returnAttachInfo;
    
    
    public void onCellEditRegist(RowEditEvent event) throws ParseException {
       qtyId=((SharedAssetReqDetail) event.getObject()).getId();
       chargingBeginTime=((SharedAssetReqDetail) event.getObject()).getChargingBeginTime();
       charginEndTime = ((SharedAssetReqDetail) event.getObject()).getChargingEndTime();
       
//       returnAttachInfo = ((SharedAssetReqDetail) event.getObject()).getReturnAttachInfo();
//       
//       borrowAttachInfo = ((SharedAssetReqDetail) event.getObject()).getBorrowAttachInfo();
       System.out.println(borrowAttachInfo);
       
       SharedAssetReqDetail vo = new SharedAssetReqDetail();
       vo = dao.findById(qtyId);
       if(chargingBeginTime !=null){
           vo.setChargingBeginTime(chargingBeginTime);
       }
       
       if(charginEndTime!=null){
           vo.setChargingEndTime(charginEndTime);
       }
       
       
       if("小时".equals(vo.getUnit())){
           if(charginEndTime!=null && chargingBeginTime!=null){
               vo.setCountQty(getDatePoor(charginEndTime,chargingBeginTime));
               vo.setCost(vo.getUnitPrice()*vo.getCountQty());
           }else{
               vo.setCountQty(0);
               vo.setCost(null);
           }
           
           
               
       }else if("天".equals(vo.getUnit())){
           if(charginEndTime!=null && chargingBeginTime!=null){
               vo.setCountQty(daysBetween(chargingBeginTime,charginEndTime));
               vo.setCost(vo.getUnitPrice()*vo.getCountQty());
           }else{
               vo.setCountQty(0);
               vo.setCost(null);
           }
       }else if("次".equals(vo.getUnit())){
           
           vo.setCountQty(1);
           vo.setCost(vo.getUnitPrice());
       }
       
       dao.save(vo);
       
        
    }
    
    public void deleteT(String id) {
       SharedAssetReqDetail vo = new SharedAssetReqDetail();
       vo = dao.findById(id);
       String assetInfoId = vo.getAssetInfoId();
       AssetInfo infoVo = new AssetInfo();
       infoVo = asiDao.findByUid(assetInfoId);
       infoVo.setSwapTypeState("0");
       asiDao.save(infoVo);//将AssetInfo的借用状态还原
       dao.delete(id);//删除记录
       
       String swapTypeUID = infoVo.getSharedAssetTypeUid();
       String iouID = vo.getSharedId();
       
       SharedAssetReqType applyDetail = new SharedAssetReqType();
       applyDetail = apdDao.findByIdSharedIdandSharedAssetTypeId(iouID,swapTypeUID);
       
       applyDetail.setActualQty(applyDetail.getActualQty()-1);//借用数-1
       
       apdDao.save(applyDetail);
       
       
       
    }
    
    private List<SharedAssetReqDetail> selectedAssets;
    
    private SharedAssetReqDetail selectedAsset = null;
    
    
    public void setSelectedAssets(List<SharedAssetReqDetail> selectedAssets) {
        this.selectedAssets = selectedAssets;
    }
    
    public List<SharedAssetReqDetail> getSelectedAssets() {
        return selectedAssets;
    }
    
    private String updateViewIDs;
    public String getUpdateViewIDs() {
        return updateViewIDs;
        
    }
    
    
    public void confirmSelection(){
        
        if( (this.selectedAsset==null) && (selectedAssets.isEmpty()) ){
            WebUtil.addErrorMessageKey("noAssetSelected");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }else{
        
            for(SharedAssetReqDetail vo :selectedAssets){
                String assetInfoId = vo.getAssetInfoId();
                AssetInfo infoVo = new AssetInfo();
                infoVo = asiDao.findByUid(assetInfoId);
                infoVo.setSwapTypeState("0");
                asiDao.save(infoVo);//将AssetInfo的借用状态还原
                vo.setState("1");//将调剂设备状态设为已归还
                dao.save(vo);

                String swapTypeUID = infoVo.getSharedAssetTypeUid();
                String iouID = vo.getSharedId();

                SharedAssetReqType applyDetail = new SharedAssetReqType();
                applyDetail = apdDao.findByIdSharedIdandSharedAssetTypeId(iouID,swapTypeUID);

                applyDetail.setReturnQty(applyDetail.getReturnQty()+1);//归还数+1

                apdDao.save(applyDetail);
                
                
                String iouId = vo.getSharedId();
                
                List<SharedAssetReqDetail> ListAll = dao.selectBySharedId(iouId);
                int x = 0;
                if(ListAll.size()>0){
                
                    for(SharedAssetReqDetail dtl : ListAll){
                        
                        if("1".equals(dtl.getState())){
                            x++;
                        }
                    
                    }
                }
                
                SharedAssetRequest iouVo = new SharedAssetRequest();
                        
                iouVo = iouDao.findById(iouId);
                
                
                if(x==ListAll.size()){
                    iouVo.setState(8);//全部归还
                }else{
                    iouVo.setState(5);//部分归还
                }
                iouDao.save(iouVo);
                
            }
        }
    }
    
    
    public static int getDatePoor(Date endDate, Date beginDate) {
 
    long nd = 1000 * 24 * 60 * 60;
    long nh = 1000 * 60 * 60;
    long nm = 1000 * 60;
    // long ns = 1000;
    // 获得两个时间的毫秒时间差异
    long diff = endDate.getTime() - beginDate.getTime();
    // 计算差多少天
    long day = diff / nd;
    // 计算差多少小时
    long hour = diff % nd / nh;
    // 计算差多少分钟
    long min = diff % nd % nh / nm;
    // 计算差多少秒//输出结果
    // long sec = diff % nd % nh % nm / ns;
    
    String hourStr = String.valueOf(hour+1);
    return Integer.valueOf(hourStr);
}
    
    
    
    
    public static int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }   
    
    public void deleteBy(String uid){
    
        SharedAssetReqDetail vo = dao.findById(uid);
        String assetInfoId =vo.getAssetInfoId();
        String iouId = vo.getSharedId();
        AssetInfo info = new AssetInfo();
        info = asiDao.findByUid(assetInfoId);
        String swapTypeUid = info.getSharedAssetTypeUid();
        SharedAssetReqType applyVo = new SharedAssetReqType();
        applyVo = apdDao.findByIdSharedIdandSharedAssetTypeId(iouId, swapTypeUid);
        applyVo.setActualQty(applyVo.getActualQty()-1);
        apdDao.save(applyVo);
//        info.setSwapTypeState("0");
        //使用科室更改为调剂中心
        info.setClinicalDeptUID(UserContextService.getCurrentUserAccount().getSiteUID());
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
        if(userOrg.getOrgType()==4){
            info.setClinicalDeptName(userOrg.getName());
            info.setClinicalDeptUID(userOrg.getUid());
        }
        asiDao.save(info);
        dao.delete(uid);
    
    }
    
    public void deleteByR(String uid){
//        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        SharedAssetReqDetail vo = dao.findById(uid);
        String assetInfoId =vo.getAssetInfoId();
        String BorrowId;
        BorrowId = vo.getBorrowId();
        AssetInfo info = new AssetInfo();
        info = asiDao.findByUid(assetInfoId);
        String swapTypeUid = info.getSharedAssetTypeUid();
        SharedAssetReqType applyVo = new SharedAssetReqType();
        applyVo = apdDao.findByIdSharedIdandSharedAssetTypeId(BorrowId, swapTypeUid);
        applyVo.setActualQty(applyVo.getActualQty()-1);
        apdDao.save(applyVo);
        SharedAssetRequest shared = new SharedAssetRequest();
        List<SharedAssetRequest> sharedList = iouDao.findByDept(BorrowId);
        if(sharedList.size()==1){
            shared = sharedList.get(0);
        }
        String ClinicalDeptName = shared.getClinicalDeptName();
        String ClinicalDeptUID = shared.getClinicalDeptUid();
        info.setClinicalDeptName(ClinicalDeptName);
        info.setClinicalDeptUID(ClinicalDeptUID);
       
//        info.setSwapTypeState("1");
        asiDao.save(info);
        dao.delete(uid);
        
//        String states = "2";
//        SharedAssetReqDetail oldAsset = dao.findByAssetInfoAndState(assetInfoId,states);
//        oldAsset.setState("1");
//        dao.save(oldAsset);
    
    }
    
    
    private String operation;
    
    public String getReachEditPage(String assetId) {
         
        operation = "Detail_Edit.xhtml?actionName=Edit&selectedid=" + assetId;
        return UrlEncryptController.encodeUrlParam(operation);
    }
    
    public  SharedAssetReqDetail getAccessoriesById(String uid){
        
        SharedAssetReqDetail vo = dao.findById(uid);
        
        return vo;
    }
    public void saves(String type){
        if("借用".equals(type)){
        super.save();
        }
        else{
        if("次".equals(this.selected.getChargingType())){
            this.selected.setCountQty(1);
            this.selected.setCost(this.selected.getUnitPrice());
            super.save();
        }
        else if("天".equals(this.selected.getChargingType())){
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(this.selected.getChargingBeginTime());
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(this.selected.getChargingEndTime());
            int day1= cal1.get(Calendar.DAY_OF_YEAR);
            int day2 = cal2.get(Calendar.DAY_OF_YEAR);
            int year1 = cal1.get(Calendar.YEAR);
            int year2 = cal2.get(Calendar.YEAR); 
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

                    this.selected.setCountQty(timeDistance + (day2-day1));
                    this.selected.setCost((timeDistance + (day2-day1))*this.selected.getUnitPrice());
                    super.save();
                }
            else    //不同年
            {
               this.selected.setCountQty(day2-day1);
               this.selected.setCost((day2-day1)*this.selected.getUnitPrice());
               super.save();
            }
            
        }
        else{
             long nd = 1000 * 24 * 60 * 60;
             long nh = 1000 * 60 * 60;
             long nm = 1000 * 60;
             long diff = (this.selected.getChargingEndTime()).getTime() - (this.selected.getChargingBeginTime()).getTime();
             long time = (diff % nd / nh)*2;
             long minute = (diff % nd % nh / nm)/30;
             Integer countQty = (int)time+(int)minute;
             this.selected.setCountQty(countQty);
             this.selected.setCost(countQty*this.selected.getUnitPrice());
             super.save();
             
             
        }
        }
    }
    
    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.setSelectedAccessoryList(new ArrayList<String>());
        for(SharedAssetTypeAccesssory item: this.selected.getAccessoryList()){
            if(item.getBorrowStatus() !=null&&item.getBorrowStatus()){
                getSelectedAccessoryList().add(item.getName());
            }
        }
        this.setSelectedAccessoryListReturn(new ArrayList<String>());
        for(SharedAssetTypeAccesssory item: this.selected.getAccessoryList()){
            if(getSelectedAccessoryListReturn()!=null){
                if(item.getReturnStatus() !=null&&item.getReturnStatus()){
                    getSelectedAccessoryListReturn().add(item.getName());
                }
            }
        }
    }
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
//        selected.setAccessoryList(getAccessoriesById(selected.getSharedAssetTypeId()).getAccessoryList());
        this.setSelectedAccessoryList(new ArrayList<String>());
        for(SharedAssetTypeAccesssory item: this.selected.getAccessoryList()){
            item.setBorrowStatus(true);
            getSelectedAccessoryList().add(item.getName());
            
        }
        
    }
    @Override
    public void onBeforeSave(SharedAssetReqDetail object) {
        for(SharedAssetTypeAccesssory item: this.selected.getAccessoryList()){
            item.setBorrowStatus(false); 
            item.setReturnStatus(false);
            for(String selectedItem : selectedBorrowAccessoryList){
                if(selectedItem.equals(item.getName())){
                item.setBorrowStatus(true); 
                    if(object.getUnit()==null){
                        item.setReturnStatus(true);
                    }
                }
            }
        }
        if(object.getUnit()!=null){
        for(SharedAssetTypeAccesssory item: this.selected.getAccessoryList()){
            item.setReturnStatus(false);
            for(String selectedItem : selectedReturnAccessoryList){
                if(selectedItem.equals(item.getName()))
                item.setReturnStatus(true); 
            }
        }
        }
        this.selected.setAccessoryList(this.selected.getAccessoryList());
    }

    /**
     * @return the selectedBorrowAccessoryList
     */
    public List<String> getSelectedAccessoryList() {
        return getSelectedBorrowAccessoryList();
    }
    
    public List<String> getSelectedAccessoryListReturn() {
        return getSelectedReturnAccessoryList();
    }

    /**
     * @param selectedAccessoryList the selectedBorrowAccessoryList to set
     */
    public void setSelectedAccessoryList(List<String> selectedAccessoryList) {
        this.setSelectedBorrowAccessoryList(selectedAccessoryList);
    }
    
    public void setSelectedAccessoryListReturn(List<String> selectedAccessoryList) {
        this.setSelectedReturnAccessoryList(selectedAccessoryList);
    }

    /**
     * @return the selectedReturnAccessoryList
     */
    public List<String> getSelectedReturnAccessoryList() {
        return selectedReturnAccessoryList;
    }

    /**
     * @param selectedReturnAccessoryList the selectedReturnAccessoryList to set
     */
    public void setSelectedReturnAccessoryList(List<String> selectedReturnAccessoryList) {
        this.selectedReturnAccessoryList = selectedReturnAccessoryList;
    }

    /**
     * @return the selectedBorrowAccessoryList
     */
    public List<String> getSelectedBorrowAccessoryList() {
        return selectedBorrowAccessoryList;
    }

    /**
     * @param selectedBorrowAccessoryList the selectedBorrowAccessoryList to set
     */
    public void setSelectedBorrowAccessoryList(List<String> selectedBorrowAccessoryList) {
        this.selectedBorrowAccessoryList = selectedBorrowAccessoryList;
    }
    
    
}
