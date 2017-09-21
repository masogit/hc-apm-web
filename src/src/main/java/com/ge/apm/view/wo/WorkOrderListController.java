/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderListController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {
    private Integer manHours;
    private Double otherExpense;
    private Date esTime;
    private Date confirmedUpDate;
    private V2_WorkOrderRepository dao;
    private ServiceRequestRepository srDao;
    private V2_WorkOrderService woService;
    private List<I18nMessage>   msgModeList;
    private String equipmentTaker;
    private String takeTime;
    //status：0-待派工/1-待接单/2-维修中/3-已完成/4-已派工/10-已取消  待验收-5, 待关单-6, 已关单-7
    private Integer queryIndex;
    private Integer category=100;
    private boolean addOrUpdate;
    private V2_ServiceRequest selectedServiceRequest;
    private V2_WorkOrder selectedWorkOrder;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetInfoRepository;
    private UserAccount ua;
    private List<V2_WorkOrder_Detail> workOrderDetailsList;
    private List<V2_WorkOrder_Detail> accessList;
    private List<V2_WorkOrderCoworker> coWorkerList;
    private WorkOrderCoworkerRepository coWorkerDao;
    private WorkOrderDetailRepository woDetailDao;

    private V2_WorkOrder_Detail itemDetail ;
    private V2_WorkOrderCoworker coWorkerDetail ;
    private UserAccountRepository userAccountRepository;
    private String testMethod;
    private String solution;
    private String problemDes;
    private String comments;
    String woUid=null;
    String woAccUid=null;
    String woCoworkUid=null;
    protected void init() {
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        srDao = WebUtil.getBean(ServiceRequestRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        this.filterBySite = true;
        queryIndex = 3;
        category=0;
        ua = UserContextService.getCurrentUserAccount();
        assetFaultTypeService=WebUtil.getBean(AssetFaultTypeService.class);
        assetInfoRepository=WebUtil.getBean(AssetInfoRepository.class);
        userAccountRepository=WebUtil.getBean(UserAccountRepository.class);
        coWorkerDao=WebUtil.getBean(WorkOrderCoworkerRepository.class);
        woDetailDao=WebUtil.getBean(WorkOrderDetailRepository.class);
        initWorkOrderDetailList();
    }
    public void initWorkOrderDetailList(){

        workOrderDetailsList = new ArrayList<V2_WorkOrder_Detail>();
        accessList = new ArrayList<V2_WorkOrder_Detail>();
        coWorkerList = new ArrayList<V2_WorkOrderCoworker>();
        woUid=UUID.randomUUID().toString().replace("-", "");
        confirmedUpDate=null;
        manHours=null;
        otherExpense=null;
        //accessory list init
        woAccUid=UUID.randomUUID().toString().replace("-", "");
        itemDetail = new V2_WorkOrder_Detail();
        itemDetail.setId(woAccUid);
        //  itemDetail.setManHours(0);
        accessList.add(itemDetail);
        //co woker list init
        coWorkerDetail = new V2_WorkOrderCoworker();
        woCoworkUid=UUID.randomUUID().toString().replace("-", "");
        coWorkerDetail.setId(woCoworkUid);
        coWorkerDetail.setManHours(0);
        coWorkerList.add(coWorkerDetail);

        // workOrderDetailsList.add(itemDetail);

    }


  /*  public boolean isManHours(){
        if(itemDetail!=null)
            return  itemDetail.getManHours()==null?false:true;
        else
            return false;
    }*/

    public void saveDetail(){
        /*String username=null;
        removeWoUid(workOrderDetailsList);
        V2_WorkOrder_Detail v2 = new V2_WorkOrder_Detail();
       // v2.setManHours(itemDetail.getManHours());
        v2.setId(UUID.randomUUID().toString().replace("-", ""));
        v2.setParts(itemDetail.getParts());
        v2.setPartsPrice(itemDetail.getPartsPrice());
        v2.setApartsQuantity(itemDetail.getApartsQuantity());
        v2.setOtherExpense(itemDetail.getOtherExpense());
        v2.setCowokerUserId(itemDetail.getCowokerUserId());
        if(itemDetail!=null)
         username=userAccountRepository.findById(itemDetail.getCowokerUserId()).getName();
        v2.setCowokerUserName(username);
        workOrderDetailsList.add(v2);*/
    }

    public void addAcessItem(){
        removeAcessListFirstItem(accessList);
        V2_WorkOrder_Detail v2 = new V2_WorkOrder_Detail();
        v2.setId(UUID.randomUUID().toString().replace("-", ""));
        v2.setParts(itemDetail.getParts());
        v2.setPartsPrice(itemDetail.getPartsPrice());
        v2.setApartsQuantity(itemDetail.getApartsQuantity());
      /*  v2.setManHours(manHours);
        v2.setOtherExpense(otherExpense);*/
        accessList.add(v2);
    }
    public void addCoWorkerItem(){
        String username=null;
        removeCoWorkerListFirstItem(coWorkerList);
        V2_WorkOrderCoworker v2 = new V2_WorkOrderCoworker();
        v2.setId(UUID.randomUUID().toString().replace("-", ""));
        v2.setCowokerUserId(coWorkerDetail.getCowokerUserId());
        if(coWorkerDetail!=null)
            username=userAccountRepository.findById(coWorkerDetail.getCowokerUserId()).getName();
        v2.setManHours(coWorkerDetail.getManHours());
        v2.setCowokerUserName(username);
        //  v2.setOtherExpense(otherExpense);
        coWorkerList.add(v2);
    }
    private void removeWoUid(List<V2_WorkOrder_Detail> arrayList) {
        if(woUid!=null){
            Iterator<V2_WorkOrder_Detail> sListIterator = arrayList.iterator();
            while(sListIterator.hasNext()){
                V2_WorkOrder_Detail workOrderDetail = sListIterator.next();
                if(workOrderDetail.getId().equals(woUid)){
                    sListIterator.remove();
                }
            }
            woUid=null;
        }
    }
    private void removeAcessListFirstItem(List<V2_WorkOrder_Detail> arrayList) {
        if(woAccUid!=null){
            Iterator<V2_WorkOrder_Detail> sListIterator = arrayList.iterator();
            while(sListIterator.hasNext()){
                V2_WorkOrder_Detail workOrderDetail = sListIterator.next();
                if(workOrderDetail.getId().equals(woAccUid)){
                    sListIterator.remove();
                }
            }
            woAccUid=null;
        }
    }
    private void removeCoWorkerListFirstItem(List<V2_WorkOrderCoworker> arrayList) {
        if(woCoworkUid!=null){
            Iterator<V2_WorkOrderCoworker> sListIterator = arrayList.iterator();
            while(sListIterator.hasNext()){
                V2_WorkOrderCoworker coWorkerList = sListIterator.next();
                if(coWorkerList.getId().equals(woCoworkUid)){
                    sListIterator.remove();
                }
            }
            woCoworkUid=null;
        }
    }
    public void takeEquip(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        takeTime = sdf.format(new Date());
        woService.takeEquip(selectedWorkOrder,equipmentTaker,takeTime);
    }
    public Double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(Double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public void createWorkOrder(){
        selectedWorkOrder.setComment(comments);
        V2_ServiceRequest sr = srDao.findById(selectedWorkOrder.getSrId());
        sr.setRequestReason(comments);
        woService.reCreateWorkOrder(selectedWorkOrder,selectedWorkOrder.getCurrentPersonId(),2,comments,selectedWorkOrder.getIntExtType());
    }

    public void newOrderCancel(){
        comments =null;
    }

    public void repairWorkOrder(){
        if(woUid!=null){ //没有添加任何detail，将初始化时默认的detail去掉
            workOrderDetailsList=null;
        }
        if(woAccUid!=null){
            accessList=null;
        }
        if(woCoworkUid!=null){
            coWorkerList=null;
        }
        //gl:1表示是正常
        woService.repairWorkOrder(selectedWorkOrder,confirmedUpDate,1,accessList,coWorkerList,manHours,otherExpense);

    }
    private String checkInDate;
    public void checkIn(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        checkInDate = sdf.format(new Date());
        if(this.selectedWorkOrder!=null)
            woService.checkIn(this.selectedWorkOrder);

    }
    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public boolean isWoListEmpty(){
        return  (workOrderDetailsList.size()==0);
    }


    public boolean isAccessListEmpty(){
        return  (accessList.size()==0);
    }

    public void prepareEdit(V2_WorkOrder_Detail workOrderDetail){
        this.prepareEdit();
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public void prepareDispatch(String woid){
        selectedWorkOrder = dao.findById(woid);
        initWorkOrderDetailList();

    }
    //V2_WorkOrderRepository

    public void clickCloseOrder(String woid){
        selectedWorkOrder = dao.findById(woid);

        coWorkerList = coWorkerDao.getByWoIdOrderByCreatedDateAsc(woid);
        accessList = woDetailDao.getByWoIdOrderByCreatedDateAsc(woid);



        manHours=selectedWorkOrder.getManHours();
        otherExpense=selectedWorkOrder.getOtherExpense();
        System.out.println();

    }
    public void prepareEditDetail(V2_WorkOrder_Detail workOrderDetail){
        itemDetail =workOrderDetail;
        setAddOrUpdate(false);

    }
    public void prepareEditDetail(V2_WorkOrderCoworker coWorkerDetail){
        this.coWorkerDetail =coWorkerDetail;
        setAddOrUpdate(false);

    }
    public void accEditDetail(V2_WorkOrder_Detail workOrderDetail){
        itemDetail =workOrderDetail;
        setAddOrUpdate(false);

    }
    public void coworkerEditDetail(V2_WorkOrderCoworker v2co){
        this.coWorkerDetail =v2co;
        setAddOrUpdate(false);

    }

    public void updateEditDetail(){
        System.out.println();
        /*if(itemDetail!=null) {
            itemDetail.setCowokerUserName(userAccountRepository.getById(itemDetail.getCowokerUserId()).getName());
        }
        setAddOrUpdate(false);*/
        //   coWorkerDetail.setManHours();

    }
    public void coWorkerEditDetailConfirm(){
        //   System.out.println(coWorkerDetail);
        if(coWorkerDetail!=null){
            coWorkerDetail.setCowokerUserName(userAccountRepository.getById(coWorkerDetail.getCowokerUserId()).getName());
        }
    }

    public void updateAccessDetail(){

        setAddOrUpdate(false);
    }
    public void prepareCreateDetail(){
        itemDetail = new V2_WorkOrder_Detail();
        setAddOrUpdate(true);
    }
    public void prepareCreateCoWorkerDetail(){
        this.manHours=null;
        coWorkerDetail = new V2_WorkOrderCoworker();
        setAddOrUpdate(true);
    }
    public void newAccess(){
        itemDetail = new V2_WorkOrder_Detail();
        setAddOrUpdate(true);
    }
    public void deleteWoDetail(String itemId){
        if(workOrderDetailsList.size()==1){
            V2_WorkOrder_Detail v = new V2_WorkOrder_Detail();
            workOrderDetailsList = new ArrayList<V2_WorkOrder_Detail>();
            woUid=UUID.randomUUID().toString().replace("-", "");

            v.setId(woUid);
            //  v.setManHours(0);
            workOrderDetailsList.add(v);
        }
        else {
            Iterator<V2_WorkOrder_Detail> sListIterator = workOrderDetailsList.iterator();
            while (sListIterator.hasNext()) {
                V2_WorkOrder_Detail workOrderDetail = sListIterator.next();
                if (workOrderDetail.getId().equals(itemId)) {
                    sListIterator.remove();
                }
            }
        }
    }

    public void deleteItem(String itemId,int type){
        if(type ==1){ //删除附件表的记录
            if(accessList.size()==1){
                V2_WorkOrder_Detail v = new V2_WorkOrder_Detail();
                accessList = new ArrayList<V2_WorkOrder_Detail>();
                woAccUid=UUID.randomUUID().toString().replace("-", "");
                v.setId(woAccUid);
                accessList.add(v);
            }
            else {
                Iterator<V2_WorkOrder_Detail> sListIterator = accessList.iterator();
                while (sListIterator.hasNext()) {
                    V2_WorkOrder_Detail workOrderDetail = sListIterator.next();
                    if (workOrderDetail.getId().equals(itemId)) {
                        sListIterator.remove();
                    }
                }
            }
        }
        if(type ==2){ //删除协作者表的记录
            if(coWorkerList.size()==1){
                V2_WorkOrderCoworker v = new V2_WorkOrderCoworker();
                coWorkerList = new ArrayList<V2_WorkOrderCoworker>();
                woCoworkUid=UUID.randomUUID().toString().replace("-", "");
                v.setId(woCoworkUid);
                v.setManHours(0);
                coWorkerList.add(v);
            }
            else {
                Iterator<V2_WorkOrderCoworker> sListIterator = coWorkerList.iterator();
                while (sListIterator.hasNext()) {
                    V2_WorkOrderCoworker workOrderDetail = sListIterator.next();
                    if (workOrderDetail.getId().equals(itemId)) {
                        sListIterator.remove();
                    }
                }
            }
        }
    }

    public List<V2_WorkOrder_Detail> getAccessList() {
        return accessList;
    }

    public void setAccessList(List<V2_WorkOrder_Detail> accessList) {
        this.accessList = accessList;
    }

    public List<V2_WorkOrderCoworker> getCoWorkerList() {
        return coWorkerList;
    }

    public void setCoWorkerList(List<V2_WorkOrderCoworker> coWorkerList) {
        this.coWorkerList = coWorkerList;
    }


    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getProblemDes() {
        return problemDes;
    }

    public void setProblemDes(String problemDes) {
        this.problemDes = problemDes;
    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        Page<V2_WorkOrder> pages = null;
        this.setSiteFilter();
        removeFilterOnField("status");
        //7->已关单 3->待接单 4->维修中 6->待关单 5-->待验收
        if (queryIndex == 5 ||queryIndex == 3||queryIndex == 4||queryIndex == 6) {
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, ua.getId()));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            removeFilterOnField("currentStepId");
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, queryIndex));
        }
        if ( queryIndex ==  7) {//已关单
           // searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, ua.getId()));
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
            removeFilterOnField("currentStepId");
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, queryIndex));
        }
        if(category!=100 && queryIndex == 3 ){//表示在待接单 并且catetory不为100表示属于 我的 或 同组 或 同院中的一个
            pages =woService.getWorkOrderListToPickup(category,pageRequest,ua);
            return pages;
        }

        this.selected = null;
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public List<V2_WorkOrder_Detail> getWorkOrderDetailsList() {
        return workOrderDetailsList;
    }

    public void setWorkOrderDetailsList(List<V2_WorkOrder_Detail> workOrderDetailsList) {
        this.workOrderDetailsList = workOrderDetailsList;
    }

    public List<UserAccount> findRepairPerson(){
        List<UserAccount> assetResponser=new ArrayList<UserAccount>();
        if(this.selectedWorkOrder!=null) {
            assetResponser = woService.getAssetResponser(this.selectedWorkOrder.getAssetId());
           if(!CollectionUtils.isEmpty(assetResponser)){
                return assetResponser;
           }else{
               return  woService.getAssetResponser2(this.selectedWorkOrder.getAssetId());
           }
        }
        return assetResponser;
    }

    public Date getEsTime() {
        return esTime;
    }

    public void setEsTime(Date esTime) {
        this.esTime = esTime;
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        cancel();
    }

    public void defineCate(int cate){
        category =cate;
    }
    public void cancel() {
        selected=null;
        selectedServiceRequest = null;
        comments=null;
        esTime=null;
        selectedWorkOrder=null;
    }
    public void cancelNewWorkdetail(){
        itemDetail=null;
    }
    public void cancelNewCoWoker(){
        coWorkerDetail=null;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public V2_ServiceRequest getServiceRequest(String srId){
        return srDao.findById(srId);
    }

    public void onSelectWorkOrder() {
        selectedServiceRequest = srDao.findById(selected.getSrId());
    }

    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }

    public List<V2_WorkOrder_Detail> getWOrkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }

//    public List<UserAccount> getWorkerList(){
//        return woService.getWorkerList();
//    }

    public List<AssetFaultType> getAssetFaultType(){
        AssetInfo assetinfo = assetInfoRepository.findById(this.selectedWorkOrder.getAssetId());
        return assetFaultTypeService.getFaultTypeList(assetinfo.getAssetGroup());
    }

    public void closeWorkOrder(){
        woService.closeWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void acceptWorkOrder(){
        String repairType =selectedWorkOrder.getRepairType().toString();
        woService.acceptWorkOrder(selectedWorkOrder,esTime,repairType,comments);
        cancel();
    }
    public void reassignWorkOrder(int step){
        woService.reassignWorkOrder(step,selectedWorkOrder, selectedWorkOrder.getCurrentPersonId(), comments);
        cancel();
    }


    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public V2_ServiceRequest getSelectedServiceRequest() {
        return selectedServiceRequest;
    }

    public void setSelectedServiceRequest(V2_ServiceRequest selectedServiceRequest) {
        this.selectedServiceRequest = selectedServiceRequest;
    }

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
    }


    public Integer getManHours() {
        return manHours;
    }

    public void setManHours(Integer manHours) {
        this.manHours = manHours;
    }

    public Date getConfirmedUpDate() {
        return confirmedUpDate;
    }

    public void setConfirmedUpDate(Date confirmedUpDate) {
        this.confirmedUpDate = confirmedUpDate;
    }

    public V2_WorkOrderRepository getDao() {
        return dao;
    }

    public void setDao(V2_WorkOrderRepository dao) {
        this.dao = dao;
    }

    public boolean renderAddUpdate(){
        return addOrUpdate;
    }
    public boolean isAddOrUpdate() {
        return addOrUpdate;
    }

    public void setAddOrUpdate(boolean addOrUpdate) {
        this.addOrUpdate = addOrUpdate;
    }

    public V2_WorkOrderCoworker getCoWorkerDetail() {
        return coWorkerDetail;
    }

    public void setCoWorkerDetail(V2_WorkOrderCoworker coWorkerDetail) {
        this.coWorkerDetail = coWorkerDetail;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }
}
