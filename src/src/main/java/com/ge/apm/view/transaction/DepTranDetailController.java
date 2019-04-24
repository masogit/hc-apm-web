package com.ge.apm.view.transaction;

import com.ge.apm.dao.DeptTransactionLogRepository;
import com.ge.apm.domain.DeptTransactionLog;
import com.ge.apm.service.transaction.TransactionService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.DepTranForTrans;
import com.ge.apm.web.TransactionForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
@ManagedBean
@ViewScoped
public class DepTranDetailController extends JpaCRUDController<DeptTransactionLog> implements ServerEventInterface{
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
    private TransactionService transactionService;
    private UserContextService userContextService;
    @Setter @Getter
    private DeptTransactionLog deptTransactionLog;
    private Integer authorId;

    //    private ServerEventInterface callingController;
    @Setter @Getter
    private List<DeptTransactionLog> deptTranLogList;

    @Getter @Setter
    TransactionStatis transactionStatis;
    @Override
    protected DeptTransactionLogRepository getDAO() {
        return deptDao;
    }
    DeptTransactionLogRepository deptDao;
    DepTranForTrans depTranForTrans;


    public void searchRanageSet(){
        depTranForTrans= new DepTranForTrans();
        depTranForTrans.setStartTime(TimeUtils.getInputStartDate(startTime));
        depTranForTrans.setEndTime(TimeUtils.getInputEndDate(endTime));
    }
    @Override
    protected void init(){
        depTranForTrans= new DepTranForTrans();
    transactionService = WebUtil.getBean(TransactionService.class);
    userContextService = WebUtil.getBean(UserContextService.class);
    authorId=userContextService.getLoginUser().getId();
        deptDao = WebUtil.getBean(DeptTransactionLogRepository.class);

}

    public void prepareEdit() {
        tmCategory=selected.getCategory();
        startTime=selected.getDate();
        hours= selected.getHours();
        remark=selected.getRemark();

    }
    boolean isSummarySelected=false;
    @Override
    protected Page<DeptTransactionLog> loadData(PageRequest pageRequest) {
        PageImpl page=null;

        depTranForTrans.setPageNumber(pageRequest.getPageNumber());
        depTranForTrans.setPageSize(pageRequest.getPageSize());

        if(isSummarySelected){
            depTranForTrans.setUserId(transactionStatis.getAuthorId());
            depTranForTrans.setStartTime(transactionStatis.getStartTime());
            depTranForTrans.setEndTime(transactionStatis.getEndTime());
            deptTranLogList=refreshLoadDataForSelectedItem(depTranForTrans);
            isSummarySelected=false;
        }else{
            depTranForTrans.setUserId(userContextService.getLoginUserId());
            deptTranLogList=refreshLoadData(depTranForTrans);
        }
        page = new PageImpl(deptTranLogList,pageRequest,totalSize);
        return page;
    }


    public void getDetailsDataBySearch(){
        depTranForTrans.setPageNumber(0);
        depTranForTrans.setPageSize(50);
        depTranForTrans.setStartTime(TimeUtils.getCurrentStartYYMM());
        depTranForTrans.setEndTime(TimeUtils.getCurrentEndYYMM());
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(depTranForTrans,UserContextService.getAccessToken());
        totalSize = (Integer)trListByPerson.get("rowCount");
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        isSummarySelected=true;
        transactionStatis = (TransactionStatis)eventObject;
//        List<DeptTransactionLog> deptTransactionLogs = refreshLoadData(transactionStatis.getAuthorId());
    }
    Integer totalSize;
    /*userId 有两种情况
       1 当前登陆的用户是非科室主任,那么userId就是当前登陆的用户
       2 或者创建科务的用户id(这种情况是当登陆用户以主任角登陆后选择某个科务汇总表数据时传过来的创建科务的那个userId)*/
    public List<DeptTransactionLog> refreshLoadData(DepTranForTrans dep){
        if(startTime==null){
            dep.setStartTime(TimeUtils.getCurrentStartYYMM());
        }
        if(endTime==null){
            dep.setEndTime(TimeUtils.getCurrentEndYYMM());
        }
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(dep,UserContextService.getAccessToken());
        totalSize = (Integer)trListByPerson.get("rowCount");
        deptTranLogList= decomposeTransactionLog(trListByPerson);
        return deptTranLogList;
    }


    public List<DeptTransactionLog> refreshLoadDataForSelectedItem(DepTranForTrans dep){
        HashMap<String, Object> trListByPerson = transactionService.getTransactionListByPerson(dep,UserContextService.getAccessToken());
        totalSize = (Integer)trListByPerson.get("rowCount");
        deptTranLogList= decomposeTransactionLog(trListByPerson);
        return deptTranLogList;
    }

    public void onselectDetailItem(){
        //    System.out.println(selected);
    }



    public void deleteKW(){
        deptDao.delete(selected);
        WebUtil.addSuccessMessage("科务已经成功删除");
    }




    private List<DeptTransactionLog>  decomposeTransactionLog(HashMap<String, Object> hmSO ){
        List<DeptTransactionLog> tranList = new ArrayList<DeptTransactionLog>();
        ArrayList al1 = (ArrayList) hmSO.get("data");
        for (int i=0;i<al1.size();i++){
            HashMap<String,Object> hm1 = (HashMap<String,Object>)al1.get(i);
            DeptTransactionLog ts = new DeptTransactionLog();
            ts.setId((Integer)hm1.get("id"));
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
    public void prepareCreate(){
        remark=null;
        startTime=null;
        hours=null;
        tmCategory=null;

    }
    public void locUpdateTransaction() {
        selected.setRemark(remark);
        selected.setDate(startTime);
        selected.setHours(hours);
        selected.setCategory(tmCategory);
        transactionService.localUpdateTransaction(selected);
        WebUtil.addSuccessMessage("更新成功");
    }

    public void createTransaction(){
        TransactionForm tf = new TransactionForm();
        tf.setCategory(tmCategory);
        tf.setRemark(remark);
        tf.setHours(hours);
        String date= TimeUtils.getStrYHMS(startTime,"YYYYMMDDHHMMSS");
        tf.setDate(date);
        transactionService.createTmByLink(tf, UserContextService.getAccessToken());
        WebUtil.addSuccessMessage("成功创建科务记录");
      // refreshLoadData(userContextService.getLoginUserId());
        cancel();
        deptTransactionLog= new DeptTransactionLog();
    }
    public void cancel(){
        this.remark=null;
        this.tmCategory=null;
        this.startTime=null;
        this.hours=null;
    }

/*
    public void m1(ServerEventInterface sei, TransactionStatis tr){

    }

    public void onSelectSummary(TransactionStatis transactionStatis){
        System.out.println(transactionStatis);
    }*/


}