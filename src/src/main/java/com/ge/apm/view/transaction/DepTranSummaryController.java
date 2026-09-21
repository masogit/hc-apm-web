package com.ge.apm.view.transaction;

import com.alibaba.excel.util.DateUtils;
import com.ge.apm.domain.DeptTransactionLog;
import com.ge.apm.service.transaction.TransactionService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.DepTranForTrans;
import com.ge.apm.web.TimeForm;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.component.tabview.TabView;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class DepTranSummaryController  implements Serializable{
    private ServerEventInterface callingController;
    @Getter @Setter
    DeptTransactionLog deptTransactionLog;
    @Getter @Setter
    Double hours;
    @Getter @Setter
    private String remark;
    @Getter @Setter
    private Integer tmCategory;
    @Getter @Setter
    private Date startTime;

    @Getter @Setter
    private Date endTime;

    @Getter
    @Setter
    private TabView tabView;
    @Getter
    @Setter
    private int queryTab;
    @Setter
    @Getter
    TransactionStatis transactionStatis = null;
    @Setter
    @Getter
    List<TransactionStatis> summaryTraList = new ArrayList<TransactionStatis>();
    @Setter
    @Getter
    List<DeptTransactionLog> deptTranLogList = new ArrayList<DeptTransactionLog>();

    private TransactionService transactionService;
    private UserContextService userContextService;
    private Integer authorId;
    public void prepareCreate(){
        remark=null;
        startTime=null;
        hours=null;
        tmCategory=null;

    }

public Integer getCurrentMonth(){
    return  TimeUtils.getMonthNumber(startTime);
}
public String getDate(){
        String date = "";
        if(startTime == null){
            date = DateUtils.format(new Date(),"yyyy/MM");
        }else{
            date = DateUtils.format(startTime,"yyyy/MM");
        }
    return date;
}

   /* public List<DeptTransactionLog> refreshLoadData(){
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(TimeUtils.getCurrentStartYYMM(),TimeUtils.getCurrentEndYYMM(),authorId, UserContextService.getAccessToken());
        deptTranLogList= decomposeTransactionLog(trListByPerson);
        return deptTranLogList;
    }*/
   DepTranForTrans depTranForTrans;
    @PostConstruct
    public void init() {
        deptTransactionLog= new DeptTransactionLog();
        transactionService = WebUtil.getBean(TransactionService.class);

        userContextService = WebUtil.getBean(UserContextService.class);
        authorId=userContextService.getLoginUser().getId();
        initLoad();
    }
    public void initLoad(){
        TimeForm tf = new TimeForm();
        //是临床科室主任
        if(userContextService.hasRole("AssetHead")){
            TimeUtils.timeDeal(tf,startTime,endTime);
            HashMap<String, Object> viewByAssetHead=transactionService.getSummary(tf.getFrom(),tf.getTo(), UserContextService.getAccessToken());
            summaryTraList= decomposeSummary(tf,viewByAssetHead);
        }
    }

    public void searchTheMonth(){
        depTranForTrans= new DepTranForTrans();
        TimeForm tf = new TimeForm();
        HashMap<String, Object> viewByAssetHead=null;
        if(startTime!=null) {
            viewByAssetHead=transactionService.getSummary(TimeUtils.getInputStartDate(startTime),TimeUtils.getInputEndDate(startTime), UserContextService.getAccessToken());
            summaryTraList= decomposeSummary(tf,viewByAssetHead);
        }
    }


    public void searchRange(){
        depTranForTrans= new DepTranForTrans();
        if(startTime!=null)
         depTranForTrans.setStartTime(TimeUtils.getInputStartDate(startTime));
        if(endTime!=null)
            depTranForTrans.setEndTime(TimeUtils.getInputEndDate(endTime));
        TimeForm tf = new TimeForm();
        HashMap<String, Object> viewByAssetHead=transactionService.getSummary(TimeUtils.getInputStartDate(startTime),TimeUtils.getInputEndDate(endTime), UserContextService.getAccessToken());
        summaryTraList= decomposeSummary(tf,viewByAssetHead);
    }
    public void cancel(){
        this.remark=null;
        this.tmCategory=null;
        this.startTime=null;
        this.hours=null;
    }

    private List<TransactionStatis>  decomposeSummary(TimeForm tf, HashMap<String, Object> hmSO ){
        List<TransactionStatis> tranList = new ArrayList<TransactionStatis>();
        ArrayList al1 = (ArrayList) hmSO.get("data");
        String strDate;
        for (int i=0;i<al1.size();i++){
            HashMap<String,Object> hm1 = (HashMap<String,Object>)al1.get(i);
            TransactionStatis ts = new TransactionStatis();
            ts.setAuthorName(hm1.get("authorName").toString());
            ts.setAuthorId((Integer)hm1.get("authorId"));
            if(hm1.get("count")!=null)
                ts.setHours(Double.valueOf(hm1.get("count").toString()));
            else
                ts.setHours(0.0);
            if(hm1.get("clinicalDeptName")!=null)
                ts.setClinicalDepName(hm1.get("clinicalDeptName").toString());
            else
                ts.setClinicalDepName("无科室");
            ts.setAuthorId(Integer.valueOf(hm1.get("authorId").toString()));

            if(hm1.get("date")!=null) {
                strDate = hm1.get("date").toString();
                ts.setStartYear( Integer.valueOf(strDate.substring(0,4)));
                ts.setStartMonth(Integer.valueOf(strDate.substring(5,7)));

            }
            tranList.add(ts);
        }
        return tranList;
    }

    private List<DeptTransactionLog>  decomposeTransactionLog(HashMap<String, Object> hmSO ){
        List<DeptTransactionLog> tranList = new ArrayList<DeptTransactionLog>();
        ArrayList al1 = (ArrayList) hmSO.get("data");
        for (int i=0;i<al1.size();i++){
            HashMap<String,Object> hm1 = (HashMap<String,Object>)al1.get(i);
            DeptTransactionLog ts = new DeptTransactionLog();
            ts.setId((Integer)hm1.get("id"));
            if(hm1.get("uid") != null) {
                ts.setUid(hm1.get("uid").toString());
            }
            if(hm1.get("authorName")!=null) {
                ts.setAuthorName(hm1.get("authorName").toString());
            }
            if(hm1.get("category")!=null) {
                ts.setCategory(Integer.valueOf(hm1.get("category").toString()));
            }
            if(hm1.get("clinicalDeptName")!=null) {
                ts.setClinicalDeptName(hm1.get("clinicalDeptName").toString());
            }
            if(hm1.get("hours")!=null) {
                ts.setHours(Double.valueOf(hm1.get("hours").toString()));
            }
            if(hm1.get("date")!=null) {
                String strDate=hm1.get("date").toString();
                Date date= TimeUtils.convertStrToDate(strDate);
                ts.setDate(date);
            }
            if(hm1.get("remark")!=null) {
                ts.setRemark(hm1.get("remark").toString());
            }

            ts.setAuthorId(Integer.valueOf(hm1.get("authorId").toString()));
            tranList.add(ts);
        }
        return tranList;
    }

    public void prepareEdit(){
        this.hours=deptTransactionLog.getHours();
        this.remark=deptTransactionLog.getRemark();
        this.tmCategory=deptTransactionLog.getCategory();
        this.startTime=deptTransactionLog.getDate();
    }

    public void onSelectSummary(ServerEventInterface callingController){
        this.callingController=callingController;
        if(startTime!=null){
            transactionStatis.setStartTime(TimeUtils.getInputStartDate(startTime));
            transactionStatis.setEndTime(TimeUtils.getInputEndDate(startTime));
        }else{
            Integer month = getCurrentMonth();
            transactionStatis.setStartTime(TimeUtils.getStartDateOnMonth(month));
            transactionStatis.setEndTime(TimeUtils.getEndDateOnMonth(month));
        }

        callingController.onServerEvent("callingFromDepTranSummaryController", this.transactionStatis);

    }
    public String titleNameSummary(){
        if(userContextService.isOnlyAssetStaff()){
            return "科务记录";
        }else{
            return "科务详情";
        }
    }

}
