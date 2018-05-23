package com.ge.apm.view;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetTypeRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Calendar;
import java.util.List;

@ManagedBean
@ViewScoped
public class SharedAssetReqDetailController extends GenericCRUDUUIDController<SharedAssetReqDetail> {

    SharedAssetReqDetailRepository dao = null;
    private List<AssetInfo> selectInfo ;
    private String shareUid;
    AssetInfoRepository assetInfoDao = null;
    SharedAssetTypeRepository sharedTypeDao = null;
    private String Num;
    @Override
    protected void init() {
        dao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        sharedTypeDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        SharedAssetRequestNewController sharedAssetRequestController = WebUtil.getBean(SharedAssetRequestNewController.class);
        shareUid = sharedAssetRequestController.getSelected().getId();
    }

    @Override
    protected SharedAssetReqDetailRepository getDAO() {
        return dao;
    }
     public void search(String Num){
         //System.out.print(Num);
         String siteUid = UserContextService.getCurrentUserAccount().getSiteUID();
         if(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).size()>1||assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).size()>1||assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).size()>1){
              WebUtil.addErrorMessage("此设备有重复");
         }
         else{
         if(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).size()!=0){
             if(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()!=null&&"0".equals(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeState())){
             System.out.print(12);
             this.setSelectInfo(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid));
             SharedAssetReqDetail add =new SharedAssetReqDetail();
             add.setAssetNumber(this.selectInfo.get(0).getDepartNum());
             add.setAssetInfoId(this.selectInfo.get(0).getUid());
             add.setSharedId(shareUid);
             add.setUnit(sharedTypeDao.findById(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getUnit());
             add.setChargingType(sharedTypeDao.findById(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getChargingType());
             add.setUnitPrice(sharedTypeDao.findById(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getUnitPrice());
             add.setSharedAssetTypeId(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid());
             dao.save(add);
             //assetInfoDao.findByDepartNum(Num).get(0).setSwapTypeState("1");
             assetInfoDao.updateAssetInfoSwapTypeState("1",assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getId());
             
             }
             else if(assetInfoDao.findByDepartNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()==null){
                  WebUtil.addErrorMessage("此设备不是调剂设备");
             }
             else{
                  WebUtil.addErrorMessage("此设备已被借走");
             }
         }
         else if(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).size()!=0){
             if(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()!=null&&"0".equals(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeState())){
             System.out.print(2);
             this.setSelectInfo(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid));
             SharedAssetReqDetail add =new SharedAssetReqDetail();
             add.setAssetNumber(this.selectInfo.get(0).getDepartNum());
             add.setAssetInfoId(this.selectInfo.get(0).getUid());
             add.setSharedId(shareUid);
             add.setUnit(sharedTypeDao.findById(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getUnit());
             add.setChargingType(sharedTypeDao.findById(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getChargingType());
             add.setUnitPrice(sharedTypeDao.findById(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getUnitPrice());
             add.setSharedAssetTypeId(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid());
             dao.save(add);
             assetInfoDao.updateAssetInfoSwapTypeState("1",assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getId());
             }
             else if(assetInfoDao.findByFinancingNumAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()==null){
                  WebUtil.addErrorMessage("此设备不是调剂设备");
             }
             else{
                  WebUtil.addErrorMessage("此设备已被借走");
             }
         }
             else if(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).size()!=0){
                 if(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()!=null&&"0".equals(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeState())){
                 System.out.print(3);
                 this.setSelectInfo(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid));
                 SharedAssetReqDetail add =new SharedAssetReqDetail();
                 add.setAssetNumber(this.selectInfo.get(0).getDepartNum());
                 add.setAssetInfoId(this.selectInfo.get(0).getUid());
                 add.setSharedId(shareUid);
                 add.setUnit(sharedTypeDao.findById(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getUnit());
                 add.setChargingType(sharedTypeDao.findById(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getChargingType());
                 add.setUnitPrice(sharedTypeDao.findById(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()).getUnitPrice());
                 add.setSharedAssetTypeId(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeUid());
                 dao.save(add);
                 //assetInfoDao.findByQrCode(Num).get(0).setSwapTypeState("1");
                 assetInfoDao.updateAssetInfoSwapTypeState("1",assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getId());
                 }
                  else if(assetInfoDao.findByQrCodeAndSiteUID(Num,siteUid).get(0).getSwapTypeUid()==null){
                  WebUtil.addErrorMessage("此设备不是调剂设备");
             }
             else{
                  WebUtil.addErrorMessage("此设备已被借走");
             }
         }
             else{
                 WebUtil.addErrorMessage("没找到该设备");
             }
         }
    }
    public String getSwapName(String id){
        String siteUid = UserContextService.getCurrentUserAccount().getSiteUID();
        return sharedTypeDao.findById(id).getSwapName();
    }

    @Override
    protected Page<SharedAssetReqDetail> loadData(PageRequest pageRequest) {
        return dao.findByReturnId(pageRequest, shareUid);
    }
    public List<AssetInfo> getSelectInfo() {
        return selectInfo;
    }
    public void setSelectInfo(List<AssetInfo> selectInfo) {
        this.selectInfo = selectInfo;
    }

    @Override
    public List<SharedAssetReqDetail> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    public String getNum() {
        return Num;
    }
    public void setNum(String Num) {
        this.Num = Num;
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
/*
    @Override
    public void onBeforeNewObject(SharedAssetReqDetail object) {
    }
    
    @Override
    public void onAfterNewObject(SharedAssetReqDetail object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(SharedAssetReqDetail object) {
    }
    
    @Override
    public void onAfterUpdateObject(SharedAssetReqDetail object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(SharedAssetReqDetail object) {
    }
    
    @Override
    public void onAfterDeleteObject(SharedAssetReqDetail object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(SharedAssetReqDetail object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}