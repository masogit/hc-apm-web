/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * v
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2PmOrderController extends GenericCRUDUUIDController<V2_PmOrder> implements Serializable, ServerEventInterface {
    @Setter @Getter
    private Boolean isBatchOp=true;
    private SupplierController supplierController;
    @Setter @Getter
    private Supplier newSupplier;
    public CustomConfigService customConfigService;
    //选定科室过滤
    @Setter @Getter
    private String filterClinicalDeptOrg;

    @Setter @Getter
    private Boolean periodShow;

    private String filterAssetDeptOrg;
    @Setter @Getter
    List<UnstructuredDataCollection> reportList;
    //搜索字串
    private String oneSearchStr;
    //选定的院区
    private OrgInfo selectSite;
    private List<OrgInfo> clinicalDeptList;
    private OrgInfoTreeService orgInfoTreeService;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSet;
    @Setter @Getter
    List<PmOrderRuleCheckList> pmOrderRuleCheckList;
    private ServiceRequestApiService serviceRequestApiService;
    private PmOrderRuleRepository pmOrderRuleRepository;
    private String token;
    private List<UnstructuredDataCollection> unDataCollection;
    private UnstructuredDataCollection unCollection;
    private PmOrderRuleCheckList pmCheckItem;
    private UnstructuredDataCollectionRepository reportDao;
    private List<PmOrderRuleCheckList> pmCheckItemList;// 每条检查项记录
    private CustomConfigObject currentCustomConfig;
    @Setter @Getter
    private Boolean nochange = false;
    private static final String SIGNATURE_HOLDER = "审核人签名：";
    private static final String EMPTY_SIGNATURE_HOLDER = "";
    @Getter @Setter
    Boolean switchTmp= false;
    @Getter @Setter
    private String fileterOrgName;
    @Getter @Setter
    private Integer casePriority;
    private  String cancelInt;
    private String supplierName;
    private AutoCompleteService autoCompleteService;
    private AssetInfoMgrService aimService;
    private List<Supplier> supplierList = new ArrayList<Supplier>();
    private V2_PmOrderRepository dao;
    private Double totalManHours;
    @Getter @Setter
    private String dptName;
    private OrgInfoRepository orgDao;
    private UserAccountRepository userDao;
    private V2_PmOrderService pmOrderService;
    private V2_PmOrder selectedPmOrder;
    private String assetName;
    private Integer ruleLevel;
    private Date startTime;
    private Date endTime;
    private Date planTime;
    private Date applyTime;
    private Date planTimeForBatch;
    private String currentPersonIdBatch;
    private String qualityCheckingBatch;
    private Boolean terminate;
    private UaaService uaaService;
    private UserContextService userContextService;
    private BlobFileService blobService;
    private List<V2_PmOrder> selectedPmOrderList;
    //private V2_WorkOrderCoworkerRepository coWorkerDao;
    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;
    private AssetInfo selectedAsset;
    private Integer currentStatus = 1;
    private Boolean needConfirmTime = false;
    private String cancelReason;
    private String stepFileter;
    private String repairType;
    private Integer repairTypeInt;
    //for details form
    private List<V2_WorkOrder_Detail> approvedDetails;
    private List<V2_WorkOrder_Detail> accessoryList;
    private List<V2_WorkOrderCoworker> coWorkerList;
    private V2_WorkOrder_Detail itemDetail;
    private V2_WorkOrderCoworker coWorkerDetail;
    private Date esTime;
    private String comments;
    private Date confirmedUpDate;
    private Date confirmedDownTime;
    private String checkInDate;
    private Integer rating;
    private String failReason;
    private String equipmentTaker;
    private Date takeTime;
    private String approvaler;
    private List<V2_BlobObject> picList;
    private List<V2_BlobObject> picListForRP;
    private List<V2_BlobObject> audioList;
    private V2_WorkOrderService woService;
    private V2_ServiceRequest newServiceRequest;
    private BlobObjectRepository blobDao;
    private String pmOrderId;
    private String filterPlanTime;
    private Map<String, Boolean> selectedMap;
    private TableDefinitionRepository tableDao;
    private UnstructuredDataRepository unstructuredDataDao;
    private PmOrderRuleCheckListRepository pmckLstDao;
    HashMap<String, UnstructuredData> hmCheck;
    private Boolean isSelectedCurrentPage = false;
    private List<V2_PmOrder> currentPagePmOrders;
    private Integer pmType;
    private I18nMessageRepository i18nMessageRepository;
    private String head;
    // cache for maintenance report
    private StreamedContent maintenanceReport;
    private UserAccount user;
    private TenantInfoRepository tenantInfoRepository;
    Boolean isInsulate;
    private String reportUid; //selected table base uid
    private Map<String, Object> selectedObjectMap = new HashMap();
    private PmReportService pmReportService;

    private String queryAssetUid;
    private ConfigService configService;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }


    public void preBatchCancel(){
        cancelReason=null;
        List<String> pmIdList = new ArrayList<String>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);
        });
  List<V2_PmOrder> pmList = dao.getBatchPmOrderList(pmIdList);
  //周期性工单的pmtype是1
        Optional<V2_PmOrder> any = pmList.stream().filter(item -> item.getPmType() == 1).findAny();
        if(any.isPresent()){
            cancelInt="2";
            periodShow=true;
        }else{
            cancelInt="1";
            periodShow=false;
        }

    }

    public boolean batchCancelShow(){
         if(queryIndex==0 || queryIndex==-1){
             if(!userContextService.hasRole("AssetHead"))
             {
                 return true;
             }
             else{
                 return false;
             }
         }else{
             return false;
         }
    }
    public void prepareEditAccDetailonPrice(String itemId) {
        accessoryList = woService.getWorkOrderDetails(selected.getId());//?如果一条woId仅仅对应一条workOrderDetail 这条代码就不需要
        if (!accessoryList.isEmpty())
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        supplierList = aimService.getSuplierListBySiteUid(selected.getTenantUID());
        supplierName = itemDetail.getSupplier();
        nochange = true;

    }
    public void prepareCreateSupplier() {
        newSupplier = new Supplier();
    }
    public void saveItem(){
        if (!accessoryList.contains(itemDetail)) {
            //合同价格"默认等于"预估单价
            if(itemDetail.getClosedPrice()==null) {
                itemDetail.setClosedPrice(itemDetail.getPartsPrice());
            }
            itemDetail.setArrivedGood(false);
            itemDetail.setArrivedGoodReturn(false);
            accessoryList.add(itemDetail);
            //调用微服务来存附件
            serviceRequestApiService.addAccess(token,selectedPmOrder.getAssetInfo().getUid(),itemDetail);
        }
    }

    public void saveAndadd(){
        //照旧原来的添加一个备件
        saveItem();
        V2_WorkOrder_Detail vwd= new V2_WorkOrder_Detail();
        try {
            BeanUtils.copyProperties(vwd,itemDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vwd.setId(UUID.randomUUID().toString().replace("-", ""));
        // clear 备件名称 备件数量  预估价格
        vwd.setParts(null);
        vwd.setApartsQuantity(null);
        vwd.setPartsPrice(null);
        vwd.setClosedPrice(null);
        itemDetail= vwd;
        accessoryList = getWorkOrderDetail(selectedPmOrder.getId());

    }
    public void prepareCancel(String itemId) {
        cancelReason = null;
        selected = dao.findById(itemId);
        int type = selected.getPmType();
        if (type == 1) {
            cancelInt = "2";
        }
        if (type == 2 || type ==3 ) {
            cancelInt = "1";
        }
    }

    public void cancelPMorder() {
        pmOrderService.invokePmOerCancel(cancelReason, selected, cancelInt);
    }


    public void cancelBatchPMorder() {
        pmOrderService.invokeBatchPmOrderCancel(cancelReason, selectedMap, cancelInt);
    }



    public boolean allowChanged() {
        //is closed
        if (selected == null) {
            return true;
        }
        if (selected.isFinished()) {//关单后
            if (userContextService.hasAnyRole("AssetHead")) {
                return true;
            } else {
                return false;
            }
        } else {
            if (userContextService.hasAnyRole("AssetHead") || userContextService.hasAnyRole("AssetStaff")) {
                return true;
            }
        }
        return true;
    }

    public boolean allowChangedForDisable() {
        if (selected.isFinished()) {//关单后
            if (userContextService.hasAnyRole("AssetHead")) {
                return false;
            } else {
                return true;
            }
        } else {
            if (userContextService.hasAnyRole("AssetHead") || userContextService.hasAnyRole("AssetStaff")) {
                return false;
            } else {
                return true;
            }
        }

    }

    public void partsPriceUpdate() {
        if (itemDetail.getClosedPrice() == null || itemDetail.getClosedPrice() == 0.0) {
            itemDetail.setClosedPrice(itemDetail.getPartsPrice());
        }
    }

    public void closePrcieUpdate() {
        itemDetail.setClosedPrice(itemDetail.getClosedPrice());
    }

    public void orderSet() {
        if (itemDetail == null) return;
        if (itemDetail.getClosed() == true)
            itemDetail.setClosedTime(new Date());
        else {
            itemDetail.setClosedTime(null);
        }
    }
    public void arrivalTimeSet() {
        if (itemDetail == null) return;
        if (itemDetail.getArrived() == true)
            itemDetail.setArrivedTime(new Date());
        else {
            itemDetail.setArrivedTime(null);
        }
    }

    public void handleToggle(ToggleEvent event) {
        //折叠保养清单后向前台传递的消息
       /* FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "折叠", "Visibility:" + event.getVisibility());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
    }

    public boolean sameType(){
        ArrayList<String> orderNumList = new ArrayList<String>();
        List<Map.Entry<String, Boolean>> collect =
                sameOpHashmap.entrySet().stream().filter(item -> item.getValue()).collect(Collectors.toList());
        collect.stream().forEach(item->{
            orderNumList.add(item.getKey());
        });
        boolean result=false;
         if(orderNumList.size()>0) {
             result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getApprovalStatus().equalsIgnoreCase("1"));
             if(result){
                 WebUtil.addErrorMessage("审核中的工单,无法操作");
                 RequestContext.getCurrentInstance().addCallbackParam("batchForbidForApproval", true);
                 return result;
             }

             result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getRuleLevel() == 3);
             if(result){
                 WebUtil.addErrorMessage("3级保养工单不能批量完成");
                 RequestContext.getCurrentInstance().addCallbackParam("batchForbidForMaintanLevel", true);
                 return result;
             }
         }
         return result;
    }

    public void forbidTransfer(){
        ArrayList<String> orderNumList = new ArrayList<String>();
        List<Map.Entry<String, Boolean>> collect =
                sameOpHashmap.entrySet().stream().filter(item -> item.getValue()).collect(Collectors.toList());
        collect.stream().forEach(item->{
            orderNumList.add(item.getKey());
        });
        boolean result=false;
        if(orderNumList.size()>0) {
            result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getApprovalStatus().equalsIgnoreCase("1"));
            if(result){
                WebUtil.addErrorMessage("审核中的工单,无法批量转单");
                RequestContext.getCurrentInstance().addCallbackParam("batchForbidForTransfer", true);

            }
        }
    }
    public void addReporter(String boUid) {
        UnstructuredDataCollection newReport = new UnstructuredDataCollection();
        newReport.setBoUuid(boUid);
        TableDefinition tbd = tableDao.findByBaseId(reportUid);
        newReport.setSchemaId(tbd.getId());
        PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(selectedPmOrder);
        if (tbd.getBaseId().equalsIgnoreCase(pmOrderRule.getPerformanceReport()))
            newReport.setBoType("pm-performance-report");
        if (tbd.getBaseId().equalsIgnoreCase(pmOrderRule.getSafetyReport()))
            newReport.setBoType("pm-safety-report");
        pmOrderService.saveReport(newReport);
    }

    public List<ServiceRequestMsg> getServiceRequestMessageList(String pmOrderId) {
        return selected == null ? null : pmOrderService.getServiceRequestMessageList(pmOrderId);
    }

    public List<TableDefinition> getReportList(String siteUID) {
        OrgInfo site = orgDao.getByUid(siteUID);
        List<String> orgUids = Arrays.asList(site.getSiteUID(), site.getHospitalUID(), site.getInstitutionUID(), site.getTenantUID());
        return tableDao.getBySiteUIDIn(orgUids);
    }



    public boolean hasRepairReport() {
        List<TableDefinition> tdList;
        List<TableDefinition> tdListTotal = new ArrayList<TableDefinition>();
        reportList = pmOrderService.getReportList(selectedPmOrder.getId());
        if (reportList != null) {
            for (int i = 0; i < reportList.size(); i++) {
                tdList = tableDao.getByBaseId(tableDao.findById(reportList.get(i).getSchemaId()).getBaseId());
                tdListTotal.addAll(tdList);
            }
        }
        return tdListTotal.stream().anyMatch(item -> item.getId().equalsIgnoreCase(reportUid));//reportUId: tableDefinition-id
    }

    /**
     * Check whether the target is selected.
     *
     * @return true if the mantenance report item has been selected, false otherwise.
     */
    public boolean isTargetSelected() {
        return !selectedMap.isEmpty() && selectedMap.values().contains(true);
    }

    /**
     * Generate maintenance report.
     */
    public void doExportMaintenanceReport() {
        maintenanceReport = null;
        try {
            maintenanceReport = pmReportService.generate(selectedMap, isInsulate, user);
        } catch (BusinessException e) {
            WebUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            Logger.getLogger(V2PmOrderController.class.getName()).log(Level.WARNING, "Error happens while generate maintenance report", e);
            WebUtil.addErrorMessage("保养报告生成失败");
        }
    }

    /**
     * Getter method for maintenance report.
     *
     * @return {@link StreamedContent} object that contains maintenance report.
     */
    public StreamedContent getMaintenanceReport() {
        return maintenanceReport;
    }

    /**
     * Setter for maintenance report.
     *
     * @param maintenanceReport {@link StreamedContent} object.
     */
    public void setMaintenanceReport(StreamedContent maintenanceReport) {
        this.maintenanceReport = maintenanceReport;
    }

    //是否全是批量完成
    public boolean isBatchComplete() {
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true)
                    pmList.add(item);
            });
        });
        //pmList存放的是所选中pm工单
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> item.getSimplify().equals(true));
        return mark;
    }

    public boolean isBatchComplete_backup() {
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true)
                    pmList.add(item);
            });
        });
        //pmList存放的是所选中pm工单
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> item.getSimplify().equals(true));
        return mark;
    }
    public boolean isBatchRob() {
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true)
                    pmList.add(item);
            });
        });
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> !item.getSimplify().equals(true));
        return mark;
    }


    public boolean isBatchTakePm() {
        // System.out.println(result);
        boolean mark = true;
        List<V2_PmOrder> pmOrderList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true){
                    pmOrderList.add(item);
                }
            });
        });
        //x3存放的是所选中pm工单
        if (pmOrderList.size() > 0)
            mark = pmOrderList.stream().anyMatch(item -> item.getSimplify().equals(true));
        return mark;//==false?true:false;
    }

    public Map<String,Boolean> sameOpHashmap = new HashMap<String,Boolean>();
    public boolean isChecked() {
        Map<String, Boolean> map = this.selectedMap;
        sameOpHashmap = map;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public String getTableDefName(String templateId) {
        TableDefinition tb = tableDao.findById(templateId);
        if (tb != null) {
            return tb.getName();
        } else {
            return "未知报告: unstrurct data collection的 记录并未能在table defintion中找到 schema_Id is " + templateId;
        }
    }

    UnstructuredData untest;

    public void hmCheckSet(String key) throws Exception {
        //临时用hmCheck1  因为hmCheck的value 是String ?
        untest = (UnstructuredData) hmCheck.get(key);
    }

    private String qaStatus = "1";

    public void onQaStatusChange() {
    }

    public Boolean canCreateSr() {
        if (selectedAsset != null && selectedAsset.getIbStatus() != null && selectedAsset.getIbStatus() == 1) {
            if (selectedAsset.getRepairForm() != null && selectedAsset.getRepairForm() == 2) {
                if (null != selectedAsset.getGeDeviceType() && (selectedAsset.getGeDeviceType() == 22 || selectedAsset.getGeDeviceType() == 3)) {
                    return selectedAsset.getWarrantyDate() == null || selectedAsset.getWarrantyDate().before(new Date());
                }
                return false;
            }
        }
        return true;
    }
    //此方法暂时没用到
    public void saveCheckItemDataBatch(List<String> pmOrderNumList){
        List<V2_PmOrder> batchPmOrderList = dao.getBatchPmOrderList(pmOrderNumList);
        //循环处理每一条工单，将其保养规则对应的保养检查项目存到undatastructCollection和undatastruct中

        for (int i = 0; i < batchPmOrderList.size(); i++) {
            HashMap<String, UnstructuredData> hmCheckBatch = new HashMap<String,UnstructuredData>();
            V2_PmOrder pmOrder=batchPmOrderList.get(i);
            pmCheckItemList = acPmOrderUleCheckList(pmOrder);
            if (pmCheckItemList != null){
                pmCheckItemList.stream().filter(item-> !hmCheckBatch.containsKey(item.getUid()))
                        .forEach(item -> {
                            UnstructuredData unData = new UnstructuredData();
                            unData.setValue("pass");
                            unData.setKey(item.getUid());
                            hmCheckBatch.put(item.getUid(), unData);
                        });

            }
            Iterator<Map.Entry<String, UnstructuredData>> it = hmCheckBatch.entrySet().iterator();
            unDataCollection = reportDao.findCKByBoUuid(pmOrder.getId(),"pm-checklist-report");
            UnstructuredDataCollection unColl = new UnstructuredDataCollection();
            ArrayList<UnstructuredData> unD = new ArrayList<UnstructuredData>();
            if(unDataCollection.isEmpty()){
                unColl.setBoUuid(pmOrder.getId());
                unColl.setBoType("pm-checklist-report");
                PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(pmOrder);
                if (pmOrderRule != null) {
                    unColl.setSchemaId(pmOrderRule.getPerformanceReport());
                }
                while (it.hasNext()) {
                    Map.Entry<String, UnstructuredData> entry = it.next();
                    untest = entry.getValue();
                    if (untest.getCollectionId() == null) {
                        unD.add(untest);
                    }
                }
                unColl.setData(unD);
                pmOrderService.saveReport(unColl);
            }
        }
    }
    public void saveCheckItemData() {
        UnstructuredDataCollection unColl = new UnstructuredDataCollection();
        ArrayList<UnstructuredData> unD = new ArrayList<UnstructuredData>();
        Iterator<Map.Entry<String, UnstructuredData>> it = hmCheck.entrySet().iterator();
        unDataCollection = reportDao.findCKByBoUuid(selectedPmOrder.getId(), "pm-checklist-report");
        try {
            if (unDataCollection.isEmpty()) {//检查项没有对应的unData即通过和不通过设置，保存新设置
                unColl.setBoUuid(selectedPmOrder.getId());
                unColl.setBoType("pm-checklist-report");
                PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(selectedPmOrder);
                if (pmOrderRule != null) {
                    unColl.setSchemaId(pmOrderRule.getPerformanceReport());
                }
                //将检查项的数据保存到UnstructuredDataCollection中.
                while (it.hasNext()) {
                    Map.Entry<String, UnstructuredData> entry = it.next();
                    untest = entry.getValue();
                    if (untest.getCollectionId() == null) {
                        unD.add(untest);
                    }
                }
                unColl.setData(unD);
                pmOrderService.saveReport(unColl);//insert into unstructured_data_collection
            } else { //更新已经有的检查项
                //清空原来的unstructuredDataList
                unDataCollection.get(0).setData(new ArrayList<UnstructuredData>());
                while (it.hasNext()) {
                    Map.Entry<String, UnstructuredData> entry = it.next();
                    untest = entry.getValue();
                    unDataCollection.get(0).getData().add(untest);
                }
                pmOrderService.saveReport(unDataCollection);
            }
            //  WebUtil.addSuccessMessage("保存检查项操作成功");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<UnstructuredDataCollection> findByBoUuidNoPMCheckList(String boUid) {
        return reportDao.findByBoUuidNoPMCheckList(boUid);
    }


    public void prepareAproval(String woid) {
        prepareAct(woid);
        selectedMap = new HashMap<>();
        approvedDetails = getWorkOrderDetail(selectedPmOrder.getId());
        approvedDetails.stream().forEach(item -> {
            if (item.getApproved() == null) {
                item.setApproved(false);
            }
            selectedMap.put(item.getId(), item.getApproved());
        });
    }


    public void onApprovedDetailsSelected(String id) {
        if ("All".equals(id)) {
            getNeedApprovalDetails().stream().filter(item -> !item.getApproved()).forEach(item -> selectedMap.put(item.getId(), selectedMap.get("All")));
        } else {
            selectedMap.put("All", !selectedMap.entrySet().stream().anyMatch(item -> !item.getKey().equals("All") && !item.getValue()));
//            approvedDetails.stream().filter(item->item.getId().equals(id)).findFirst().get().setApproved(selectedMap.get(id));
        }
    }


    public void removeAttachmentByIdForRP(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picListForRP.removeAll(picListForRP.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void accessoryArrival(String itemId) {
        accessoryList.stream().filter(item -> itemId.equals(item.getId())).forEach(item -> item.setArrived(true));
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public List<UserAccount> findRepairPerson() {
        if (this.selectedPmOrder != null) {
            return woService.getWorkerList(this.selectedPmOrder.getAssetId());
        } else {
            return Collections.emptyList();
        }
    }

    public List<UserAccount> findRepairPersonBatch() {
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        currentPagePmOrders.stream().forEach(item -> {
            if (selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true) {
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        if (pmListTmp.size() > 0) { //如果是批量操作
            return pmOrderService.getWorkerList(pmListTmp.get(0).getAssetId());
        }
        if (this.selectedPmOrder != null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public void reassignWorkOrder() {
        if (isChecked() == false) {//非批量
            pmOrderService.reassignWorkOrder(selectedPmOrder, selectedPmOrder.getCurrentPersonId(), comments);
        } else {//批量派工
            pmOrderService.reassignWorkOrderBatch(currentPagePmOrders, selectedMap, currentPersonIdBatch, comments);
        }
        cancel();
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PmRequest);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadForRP(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picListForRP.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PmRequest);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return orgDao.getByUid(orgUid);
    }

    public void savePM() {
        PmOrderActionForm formData = new PmOrderActionForm();
        if (selectedAsset.getStatus() == 5) {
            WebUtil.addErrorMessage("所选设备停用,不能保养");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能保养");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("没有使用科室的资产不能创建保养工单");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            if (null != planTime) {
                formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
            }
            formData.setRuleLevel(ruleLevel);
            formData.setAttachments(picList);
            formData.setPmType(pmType);
            Map<String, Object> res = pmOrderService.createPMrequestApi(selectedAsset.getUid(), formData);
            if (res != null && res.get("bizStatusCode") != null) {
                if (res.get("bizStatusCode").equals("INVALID_PARAM")) {
                    WebUtil.addErrorMessage(res.get("message").toString());
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("invalidParam", true);
                }
            }
           // cancel();
        }
    }

    public List<PmOrderSteps> aquirePmOrderStep() {
        if (selected != null)
            return pmOrderService.getPmOrderStep(selected.getId());
        else
            return new ArrayList<PmOrderSteps>();
    }

    public void createPMRequest() {
        cancel();
    }

    public void onStatusChange() {
        if (null != selectedAsset) {//new sr
            needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
        } else {//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }
    }

    public boolean requiredCheck() {
        if (selectedPmOrder.getManHours() == null && endTime == null) {
            WebUtil.addErrorMessage("工时必填写");
            RequestContext.getCurrentInstance().addCallbackParam("needManhours", true);
            WebUtil.addErrorMessage("结束时间必须填写");
            RequestContext.getCurrentInstance().addCallbackParam("needendtime", true);
            return false;
        } else if (endTime == null) {
            WebUtil.addErrorMessage("结束时间必须填写");
            RequestContext.getCurrentInstance().addCallbackParam("needendtime", true);
            return false;
        } else if (selectedPmOrder.getManHours() == null) {
            WebUtil.addErrorMessage("工时必填写");
            RequestContext.getCurrentInstance().addCallbackParam("needManhours", true);
            return false;
        }
        if (selectedPmOrder.getPlanTime() == null) {
            WebUtil.addErrorMessage("计划完成时间必须填写");
            RequestContext.getCurrentInstance().addCallbackParam("needplanTime", true);
            return false;
        }
        return true;
    }

    public void createServiceRequest(V2_PmOrder selectedPmOrder, int from) {
        //如果from 是1 表示是从保养对话框来的报修命令，就要校验一些必填项
        if (from == 1) {
            if (!requiredCheck()) {
                return;
            }
        }
        //去保修前把单子关了,把备件信息和协作人都传给后台
        if (accessoryList != null) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
            accessoryList.forEach(item -> item.setCreatedDate(null));
        }
        if (coWorkerList != null) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
            coWorkerList.forEach(item -> item.setCreatedDate(null));
        }
        selectedPmOrder.setQualityChecking(qaStatus);
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList);
        //保存保养清单中的数据
        saveCheckItemData();
        selected = null;
        newServiceRequest = new V2_ServiceRequest();
       // String requestReason=null;

        String strPriority = JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].priority.defaultValue[0]").toString();
        String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
        Object requestReason = JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.defaultValue[0]");
        if(requestReason==null){
            newServiceRequest.setRequestReason("设备故障");
        }else{
            newServiceRequest.setRequestReason(requestReason.toString());
        }
        currentStatus= Integer.valueOf(strAssetStatus);
        newServiceRequest.setCasePriority(Integer.valueOf(strPriority));//紧急程度默认是设置为一般
        newServiceRequest.setStatus(currentStatus);

        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
        if (selectedPmOrder != null) {
            selectedAsset = selectedPmOrder.getAssetInfo();
            currentStatus=3;
            final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
            dptName = orgDao.getByUid(orgInfoUID).getName();
            casePriority =3;//一般
        }

    }

    public void saveServiceRequest() {

        if(currentStatus!=2 && currentStatus!=3){
            WebUtil.addErrorMessage("必须选择当前状态");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("status", true);
        }
        if(newServiceRequest.getRequestReason()==null){
            newServiceRequest.setRequestReason("设备故障");
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("所选设备没有使用科室,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            //1表示主动报修
            newServiceRequest.setSrRepairType("1");
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(dptName);

            if(casePriority ==null) {
                newServiceRequest.setCasePriority(1);
            }else{
                newServiceRequest.setCasePriority(casePriority);
            }

            woService.createServiceRequest(newServiceRequest, selectedAsset, picListForRP);
            //报修完后直接关单
            //pmOrderService.closePM(selectedPmOrder,picList);
            cancel();
        }
    }

    public void cancelsr() {
        selected = null;
        newServiceRequest = null;
        esTime = null;
        comments = null;
        failReason = null;
        rating = null;
        confirmedDownTime = null;
        equipmentTaker = null;
        takeTime = null;
        picList = null;
    }
    public void onSupplierChange() {
        Supplier supplier = (Supplier) selectedObjectMap.get("supplier");
        if (supplier == null)
            itemDetail.setSupplier(supplierName);
        else
            itemDetail.setSupplier(supplier.getName());
    }
    private PmOrderRuleCheckListRepository pmOrderRuleCheckListRepository;
    protected void init() {

        supplierController = WebUtil.getBean(SupplierController.class);
        customConfigService=WebUtil.getBean(CustomConfigService.class);
        currentCustomConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());

        token = UserContextService.getAccessToken();
        pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        serviceRequestApiService = WebUtil.getBean(ServiceRequestApiService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        tableDao = WebUtil.getBean(TableDefinitionRepository.class);
        unstructuredDataDao = WebUtil.getBean(UnstructuredDataRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        pmckLstDao = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        reportDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        pmOrderService = WebUtil.getBean(V2_PmOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configService = WebUtil.getBean(ConfigService.class);
        this.selectedMap = new HashMap();
        pmReportService = WebUtil.getBean(PmReportService.class);
        //如果是院长等管理层登陆进来queryIndex默认是8即当前就显示待审核
        String query = WebUtil.getRequestParameter("query");
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            queryAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
            if (null != UrlEncryptController.getValueFromMap(enCodeParameters, "query")) {
                query = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "query");
            }
        }
        if(!userContextService.hasAnyRole("AssetHead,AssetStaff")){
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = 8;
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        }else {
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = -1;
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        }
        /*if ((userContextService.hasAnyRole("DeptHead") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) || (userContextService.hasAnyRole("ClinicalStaff") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) || (userContextService.hasAnyRole("DeptHead") && userContextService.hasAnyRole("ClinicalStaff") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) || userContextService.hasAnyRole("HospitalHead") && !userContextService.hasAnyRole("AssetHead,AssetStaff,ITAdmin,Guest") || userContextService.hasAnyRole("ITAdmin") && (userContextService.hasAnyRole("HospitalHead") || userContextService.hasAnyRole("ClinicalStaff") || userContextService.hasAnyRole("DeptHead"))) {
            String query = WebUtil.getRequestParameter("query");
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = 8;
            } else { userContextService.()
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        } else {
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = -1; //代办
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        }*/
        this.filterBySite = false;
        this.filterByHospital = false;
        hmCheck = new HashMap<String, UnstructuredData>();
        user = UserContextService.getCurrentUserAccount();
        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
        WorkflowConfig wfc = configService.findConfigBySite(user.getSiteUID());
        if(wfc !=null)
            isBatchOp=wfc.getPmIsSupportBatchFinish();
        loadClinicalDeptList();
    }
    public void saveNewSupplier() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        this.newSupplier.setSiteId(loginUser.getSiteId());
        if (newSupplier.getMaintainerT()) {
            supplierController.setMaintainerT(true);
        } else {
            supplierController.setMaintainerT(false);
        }
        if (newSupplier.getManufacturerT()) {
            supplierController.setManufacturerT(true);
        } else {
            supplierController.setManufacturerT(false);
        }
        if (newSupplier.getSupplierT()) {
            supplierController.setSupplierT(true);
        } else {
            supplierController.setSupplierT(false);
        }
        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();
        supplierList.add(newSupplier);
        if (org.apache.commons.lang.StringUtils.isNotBlank(newSupplier.getUid()) && !supplierList.contains(newSupplier)) {
            supplierList.add(newSupplier);
        }
    }
    public void submitAppvalPmOrder() {
        if(StringUtils.isEmpty(comments)){
            comments="报价审核";
        }
        //先保存检查项
        saveCheckItemData();
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        accessoryList.forEach(item -> {
            item.setCreatedDate(null);
            if (null != item.getApproved() && !item.getApproved()) {
                item.setApproved(null);
            }
        });
        pmOrderService.submitWorkOrder(selectedPmOrder, accessoryList, approvaler, comments);
        cancel();
    }

    public void dispatchWorkOrder() {
        if (isChecked() == false) {
            Date planTime = selectedPmOrder.getPlanTime();
            pmOrderService.dispatchWorkOrder(selectedPmOrder, planTime);
            cancel();
        } else {//批量派工
            pmOrderService.dispatchWorkOrderBatch(currentPagePmOrders, selectedMap, currentPersonIdBatch, planTimeForBatch);
            cancel();
        }
    }

    public List<UserAccount> getWorkerList() {
        if (this.selectedPmOrder != null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public List<UserAccount> getWorkerListBatch() {
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        currentPagePmOrders.stream().forEach(item -> {
            if (selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true) {
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        if (pmListTmp.size() > 0) { //如果是批量操作
            return pmOrderService.getWorkerList(pmListTmp.get(0).getAssetId());
        }
        if (this.selectedPmOrder != null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public List<UserAccount> getApproveList(String name) {
        if (this.selectedPmOrder != null)
            return pmOrderService.getApproveList(selectedPmOrder.getAssetUid(),name);
        else
            return new ArrayList<UserAccount>();
    }

    private List<Integer> tagGroupAssets;

    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }
        //type =1 是接单 ,type =2 是抢单
    public void acceptWorkOrder(int type) {
        if (isChecked() == false) //如果是false说明不是批量的操作
        {
            Date planTime = selectedPmOrder.getPlanTime();
            pmOrderService.acceptWorkOrder(selectedPmOrder, planTime);
        } else { //批量操作
            //先判定 批量的里面会不会包含 待接单Tab中 二级简化中的"完成"保养单
            List<String> assetIdList = new ArrayList<String>();
            selectedMap.forEach((k, v) -> {
                if (v == true)
                    assetIdList.add(k);
            });
            List<V2_PmOrder> pmList = dao.checkSimplify(assetIdList);
            if (pmList.size() > 0) {
                WebUtil.addErrorMessage("您的批量中不能包含待接单或待抢单的保养工单");
                return;
            }
            pmOrderService.acceptWorkOrderBatch(currentPagePmOrders, selectedMap, planTimeForBatch,type);
        }
        cancel();
    }

    private List<Integer> tagNoGroupAssets;

    private List<Integer> getNoTagGroupAssets() {
        if (tagNoGroupAssets == null) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            if (orgUidList == null || orgUidList.isEmpty()) {
                orgUidList = Arrays.asList("");
            }
            tagNoGroupAssets = dao.getAssetsByNoTagGroup(orgUidList);
        }
        return tagNoGroupAssets;
    }

    //获取当前登陆人可看到的设备, 这个人需要在标签组里面
    private List<Integer> getTagGroupAssets() {
        if (tagGroupAssets == null) {
            tagGroupAssets = dao.getAssetsByTagGroup(UserContextService.getCurrentUserAccount().getId());
        }
        return tagGroupAssets;
    }

    /*DISPATER(2,"待派工", "Assign"),ACCEPT(3,"待接单", "Accept"),
			CONFIRM(5,"待验收", "Ack"),CLOSE(6,"待关单", "Close"),EVALUATE(7,"待评价", "Rate"),APPROVAL(8, "待审核", "Approval"), MAINTAIN(0, "保养中", "Repair");*/
    Page<V2_PmOrder> result;

    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        removeFilterOnField("currentStepId");
        removeFilterOnField("currentPersonId");
        removeFilterOnField("isFinished");
        removeFilterOnField("dispatchMode");
        removeFilterOnField("ownerId");
        removeFilterOnField("approvalStatus");
        removeFilterOnField("assetInfo.assetOwnerId");
        removeFilterOnField("assetInfo.assetOwnerId2");
        removeFilterOnField("assetInfo.assetOwnerId3");

        if(queryIndex == 20){
            searchFilters.add(new SearchFilter("currentStepName", SearchFilter.Operator.EQ, "已取消"));
        }

        if(queryIndex==100){
            //from asset detail page
            if (!Strings.isNullOrEmpty(queryAssetUid)) {
                searchFilters.add(new SearchFilter("assetInfo.uid", SearchFilter.Operator.EQ, queryAssetUid, "1*"));
            }
        }
        if(queryIndex==10){

            List<Sort.Order> orders = new ArrayList<Sort.Order>();
            orders.add(new Sort.Order(Sort.Direction.DESC,"planTime"));
            orders.add(new Sort.Order(Sort.Direction.DESC,"pmNum"));
            Sort.Order orderFor = pageRequest.getSort().getOrderFor("planTime");
            //默认是计划完成时间按降序排,如果选择升续操作 就会加入下面的column
            if(orderFor!=null) {
                if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                    orders = new ArrayList<Sort.Order>();
                    orders.add(new Sort.Order(Sort.Direction.ASC, "planTime"));
                    orders.add(new Sort.Order(Sort.Direction.ASC, "pmNum"));
                }
            }else{
                //如果选择除计划完成时间的其它列 进行排序
                orders =  acqListSortOrder(pageRequest);
            }
            Sort sort = new Sort(orders);
            pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);

            Page<V2_PmOrder> result =dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPagePmOrders = result.getContent();
            return result;
        }
        if(queryIndex==-2){
            searchFilters.add(new SearchFilter("simplify", SearchFilter.Operator.EQ, true));
        }
        if (queryIndex == -1) {//待办
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, userContextService.getLoginUser().getUid(),"1*2+"));
            List<Sort.Order> orders = new ArrayList<Sort.Order>();
            orders.add(new Sort.Order(Sort.Direction.DESC,"planTime"));
            orders.add(new   Sort.Order(Sort.Direction.DESC,"pmNum"));
            Sort.Order orderFor = pageRequest.getSort().getOrderFor("planTime");
            //默认是计划完成时间按降序排,如果选择升续操作 就会加入下面的column
            if(orderFor!=null) {
                if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                    orders = new ArrayList<Sort.Order>();
                    orders.add(new Sort.Order(Sort.Direction.ASC, "planTime"));
                    orders.add(new Sort.Order(Sort.Direction.ASC, "pmNum"));
                }
            }else{
                //如果选择除计划完成时间的其它列 进行排序
                orders =  acqListSortOrder(pageRequest);
            }

            Sort sort = new Sort(orders);
            pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);
            Page<V2_PmOrder> result = dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPagePmOrders = result.getContent();
            return result;
        }
        if (queryIndex == 2) { //待派工  Assign
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, sitelist, "1*3+"));

        } else if (queryIndex == 3) {//待接单
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            //someone could only access siteA,but assigned with asset on SiteB, therefore "OR" is added saying that one also could see the asset that is assigned to him.
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 31) {//待抢单 dispatch 2表示人工派工或抢单模式 3表示抢单模式 4表示人工派工
            //抢单tab中 ,已经派的单但是未被接单人接的单子也要显示出来 (已经派的单状态是Accept，所以也要加进来)
            /*  searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList("Assign","Accept")));*/
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.NE, 4));
            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign", "1*3+"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept", "1*3+7*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+7*"));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            /*searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+4*"));*/
            //用户必须对其可视范围内的设备都可见,包括打过标签的设备(如果该设备的标签和组绑定了，并且当前用户没有在该组内，那是看不到的)和没有打过标签的设备
            //打过标签的设备(该标签都和组绑定过)
            List<Integer> tagGroupAssets = getTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*4+"));
            } else {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*4+"));
            }
            //没有打过标签的设备
            tagGroupAssets = getNoTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*4+owner*"));
            } else {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*4+owner*"));
            }
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            //维修责任人数量小于等于1的条件
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner3*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner3*"));

            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
        } else if (queryIndex == 6) { //待关单
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Close"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 8) { //待审核
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
            //1表示该pm工单已被提交等待审核
            searchFilters.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 0) { //已计划
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            //   searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 7) { //已关单
            if (!userContextService.hasRole("AssetHead") && !userContextService.isPmDispatcher()) {
                searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            }
            searchFilters.add(new SearchFilter("currentStepName", SearchFilter.Operator.EQ, "已关单"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, true));
        }
        this.selected = null;

        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.ASC,"planTime"));
        orders.add(new Sort.Order(Sort.Direction.ASC,"pmNum"));
        Sort.Order orderFor = pageRequest.getSort().getOrderFor("planTime");
        //默认是计划完成时间按降序排,如果选择升续操作 就会加入下面的column
       if(orderFor!=null) {
           if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
               orders = new ArrayList<Sort.Order>();
               orders.add(new Sort.Order(Sort.Direction.ASC, "planTime"));
               orders.add(new Sort.Order(Sort.Direction.ASC, "pmNum"));
           }
       }else{
      //如果选择除计划完成时间的其它列 进行排序
        orders =  acqListSortOrder(pageRequest);
       }
        Sort sort = new Sort(orders);
        pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);

        result = dao.findBySearchFilter(this.searchFilters, pageRequest);

        currentPagePmOrders = result.getContent();
        this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
       // isSelectedCurrentPage=false;
        return result;
    }

    private  List<Sort.Order> acqListSortOrder(PageRequest pageRequest){
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        Sort sort1 = pageRequest.getSort();
        String filterName = sort1.toString().substring(0,sort1.toString().indexOf(":"));
        Sort.Order orderFor =  pageRequest.getSort().getOrderFor(filterName);
        if(orderFor!=null) {
            if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.ASC, filterName));
            }else{
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.DESC, filterName));
            }
        }
     return orders;
    }

    public void onSelectPmOrder() {
        accessoryList = getWorkOrderDetail(selected.getId());
        coWorkerList = getWorkOrderCoworker(selected.getId());
        // 获取picList
        picList = blobDao.findByBoUuid(selected.getId());
        reportList = pmOrderService.getReportList(selected.getId());

        loadHmCheck(selected);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return pmOrderService.getWorkOrderCoworker(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetailON() {
 if(selected!=null)
        return pmOrderService.getWorkOrderDetails(selected.getId());
 else
     return null;
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworkerON(String woId) {
        if(selected!=null)
            return pmOrderService.getWorkOrderCoworker(selected.getId());
        else
            return null;
    }


    public void closePM(V2_PmOrder selectedPmOrder) {
        if (isChecked() == false) {
            pmOrderService.closePM(selectedPmOrder, picList);
        } else {
            picList.stream().forEach(item -> {
                item.setId(null);
            });
            currentPagePmOrders.stream().forEach(item -> {
                item.setQualityChecking("1");
            });
            pmOrderService.closePMBatch(currentPagePmOrders, selectedMap, picList);
        }
        cancel();
    }

    //审批
    public boolean approveNotes() {
        //如果发现有一个是true的  就返回false，否则 全是false 返回true
        return !selectedMap.entrySet().stream().anyMatch(item -> item.getValue());
    }

    public void approvePMorder() {
       if (selectedPmOrder != null) {
            if (approvedDetails.stream().anyMatch(item -> !selectedMap.containsKey
                    (item.getId()) || !selectedMap.get(item.getId())) && Strings.isNullOrEmpty(comments)) {
                WebUtil.addErrorMessage("审核不通过原因必须填写");
                RequestContext.getCurrentInstance().addCallbackParam("needComents", true);
                return;
            }
            approvedDetails.stream().filter(item -> Optional.ofNullable(selectedMap.get(item.getId())).orElse(false)).forEach(item -> item.setApproved(true));
            approvedDetails.forEach(item -> {
                item.setCreatedDate(null);
                if (null == item.getApproved()) {
                    item.setApproved(false);
                }
            });
         pmOrderService.approvePMorder(selectedPmOrder, approvedDetails, this.comments);
            this.comments = null;
            approvedDetails = null;
        }
    }

    public void prepareCompleteOrder(String woid) throws Exception {
        accessoryList = getWorkOrderDetail(woid);
        selectedPmOrder = dao.findById(woid);
        if (selectedPmOrder.getManHours() == null) {
            selectedPmOrder.setManHours(0.1);
        }
        confirmedUpDate = null;
        endTime = selectedPmOrder.getEndTime();
        planTime = selectedPmOrder.getPlanTime();
        accessoryList = getWorkOrderDetail(woid);
        coWorkerList = getWorkOrderCoworker(woid);
        picList = new ArrayList<V2_BlobObject>();
        loadHmCheck(selectedPmOrder);
        if (pmCheckItemList != null){
        pmCheckItemList.stream().filter(item-> !hmCheck.containsKey(item.getUid()))
                .forEach(item -> {
                    UnstructuredData unData = new UnstructuredData();
                    unData.setValue("pass");
                    unData.setKey(item.getUid());
                    hmCheck.put(item.getUid(), unData);
                });

        }

//        if (pmCheckItemList != null) {
//            if (hmCheck.isEmpty()) {
//                for (PmOrderRuleCheckList pmList : pmCheckItemList) {
//                    UnstructuredData unData = new UnstructuredData();
//                    unData.setValue("pass");
//                    unData.setKey(pmList.getUid());
//                    hmCheck.put(pmList.getUid(), unData);
//                }
//            }
//        }
        picList = new ArrayList<V2_BlobObject>();
        picListForRP = new ArrayList<V2_BlobObject>();
    }

    //
    /*public void onSelectAllOfPage_bak() {
        for (V2_PmOrder item : currentPagePmOrders) {
            selectedMap.put(item.getId(), this.isSelectedCurrentPage);
        }
        isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        isSelectedCurrentPage=false;
    }*/
    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_PmOrder item : currentPagePmOrders) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void onItemSelected() {
        isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        selectedPmOrder = null;//
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

            //先获取 filterOrgName-手术科的 uid
          //  List<String> orgUidList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
           // List<String> filterOrg = orgDao.getOrgBySite(orgUidList, fileterOrgName);

            if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
                this.updateSearchFilter(new SearchFilter("clinicalDeptId", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
            } else {
                searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptId".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }

        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));//所属科室
            searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));//使用科室

        }
    }

    public void searchList(Integer buttonIndex) {
        this.selectedMap = new HashedMap<>();
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        cancel();
    }

    public void cancel() {
        this.selectedAsset = null;
        planTime = null;
        planTimeForBatch = null;
        selectedPmOrder = null;
        assetName = null;
        endTime = null;
        ruleLevel = null;
        dptName = null;
        picList = null;
        planTimeForBatch = null;
        comments = null;
    }

    public void prepareActTakeGrap(String woid, String head) {
        this.setHead(head);
        if (woid != null && !woid.isEmpty()) {
            selectedPmOrder = dao.findById(woid);
            //关单时 默认设置为合格
            selectedPmOrder.setQualityChecking("1");
        }
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        picList = new ArrayList<V2_BlobObject>();
    }

    public void prepareAct(String woid) {
        if (woid != null && !woid.isEmpty()) {
            selectedPmOrder = dao.findById(woid);
            //关单时 默认设置为合格
            selectedPmOrder.setQualityChecking("1");
        }
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        picList = new ArrayList<V2_BlobObject>();
    }


    //已计划-->批量保养完成
    public void repairPmOrderBatch() {
//        保存检查项
       // saveCheckItemDataBatch(orderNumList);
        pmOrderService.repairWorkOrderBatch(endTime, totalManHours, selectedMap);
        selectedMap = new HashMap<String, Boolean>();
    }


    //已计划-->保养完成
    public void repairPmOrder() {
        if (!requiredCheck()) {
            return;
        }
        //保存检查项
        saveCheckItemData();
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        selectedPmOrder.setQualityChecking(qaStatus);
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList);
        //保养合格的话 去关单
        //cancel();
    }

    public void stagingPmOrder() {
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        pmOrderService.stagingPmrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList);
        //保存检查项
        saveCheckItemData();
        cancel();
    }

    private void loadClinicalDeptList() {
       /* clinicalDeptList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid())
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());*/
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }



    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgName) && !clinicalDeptList.stream().anyMatch(item -> fileterOrgName.equals(item.getName()))) {
            fileterOrgName = null;
        }

        if (fileterOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(fileterOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }
    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        assetName = asset.getName();
        //String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
        dptName = asset.getClinicalDeptName();//orgDao.getByUid(orgInfoUID).getName();
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;
    }


    public void prepareEditAccDetail(String itemId) throws Exception{
        Object eval;
        if ("new".equals(itemId)) {
            switchTmp=true;
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            itemDetail.setWoId(selectedPmOrder.getId());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            itemDetail.setApplyTime(sdf.parse(sdf.format(new Date())));
            selectedObjectMap  = new HashMap();
            supplierName = null;
            currentCustomConfig = customConfigService.getCurrentCustomConfig(selectedPmOrder.getSiteUID());
            eval = JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType");
            itemDetail.setPartsType(String.valueOf(JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType")));
        } else {
            switchTmp=false;
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
            supplierName = itemDetail.getSupplier();
        }
        supplierList = aimService.getSuplierListBySiteUid(selectedPmOrder.getTenantUID());
        nochange=false;
    }

    public void removeWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
        //调用微服务删除备件
        serviceRequestApiService.delAccess(token,itemId);
    }

    public void prepareBatchComplete() {
        this.endTime = null;
        this.totalManHours = null;
     //   this.selectedMap = new HashMap();
    }

    public void prepareEditCoWorkerDetail(String itemId) {
        if ("new".equals(itemId)) {
            coWorkerDetail = new V2_WorkOrderCoworker();
            coWorkerDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            coWorkerDetail.setWoId(selectedPmOrder.getId());
        } else {
            coWorkerDetail = coWorkerList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void addAcessItem() {
        saveItem();
    }

    public List<TableDefinition> getTablesName(V2_PmOrder v2) {
        List<TableDefinition> tDefinitions = pmOrderService.getTablesName(v2);
        return tDefinitions;
    }
    public  List<PmOrderRuleCheckList> getPmRuleCLclosed(V2_PmOrder v2PmOrder,String ruleUid){
        /*   unsDC:bo_uuid(工单)-->unsDC:id====>unsDC:id/unsD:collecitoId-->key
        * key就是检查项uid*/
        List<PmOrderRuleCheckList> pmcl =new ArrayList<PmOrderRuleCheckList>();
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2PmOrder.getId());
        if(unsD==null){
            return pmcl;
        }
        List<UnstructuredData> byCollectionId = unstructuredDataDao.findByCollectionId(unsD.getId());
        List<String> strList=new ArrayList<String>();
        byCollectionId.stream().forEach(item->strList.add(item.getKey()));
        if(strList.isEmpty()){
            strList.add("");
        }
        pmcl = pmckLstDao.findPMCL(strList,ruleUid);
        return pmcl;
    }

    public List<PmOrderRuleCheckList> acPmOrderUleCheckList(V2_PmOrder v2PmOrder) {
        if (v2PmOrder == null)
            return null;
        PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(v2PmOrder);
        if (pmOrderRule == null) {
            return null;
        }
       // return pmckLstDao.findAllByRuleUID_full(pmOrderRule.getUid());

        if(v2PmOrder.isFinished()) {
         return getPmRuleCLclosed(v2PmOrder,pmOrderRule.getUid());
        }
        else {
            return pmckLstDao.findAllByRuleUID(pmOrderRule.getUid());
        }
    }



    //hmCheck 的key是每一个检查项的uid, value是unstructrued_data的key
    public void loadHmCheck(V2_PmOrder v2PmOrder) {
        hmCheck = new   HashMap<String, UnstructuredData>();
        //pmCheckItemList:检查项
        pmCheckItemList = acPmOrderUleCheckList(v2PmOrder);
        unDataCollection = reportDao.findCKByBoUuid(v2PmOrder.getId(), "pm-checklist-report");//为0说明该工单的规则还没有绑定 通过和不通过
        if (pmCheckItemList == null) return ;
        //The below guys seems to exist no reason.
        for (int i = 0; i < pmCheckItemList.size(); i++) {
            pmCheckItem = (PmOrderRuleCheckList) pmCheckItemList.get(i);
            if (unDataCollection != null && !unDataCollection.isEmpty()) { //
                unCollection = (UnstructuredDataCollection) unDataCollection.get(0);
                if (unCollection != null) {
                    List<UnstructuredData> dataList = (List<UnstructuredData>) unCollection.getData();
                    for (int k = 0; k < dataList.size(); k++) {
                        UnstructuredData unsData = (UnstructuredData) dataList.get(k);
                        if (unsData.getKey().equalsIgnoreCase(pmCheckItem.getUid())) {
                            hmCheck.put(pmCheckItem.getUid(), unsData);
                        }
                    }
                }
            }
        }
    }



    //map : item.uid(pmorderrulecheck.uid) value: unstrundata
    public List<V2_WorkOrder_Detail> getNeedApprovalDetails() {
        return approvedDetails == null ? null : approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).collect(Collectors.toList());
    }

    private V2_WorkOrderCoworkerRepository coWorkerDao;

    public void addCoWokerItem() {
        if (!coWorkerList.contains(coWorkerDetail)) {
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
            coWorkerList.add(coWorkerDetail);
        } else {
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
        }
        //   cancelWorkdetail();
    }
    public void updateAccessPrice(){
        pmOrderService.updateAccessPrice(itemDetail);
    }
    public void cancelWorkdetail() {
        itemDetail = null;
        coWorkerDetail = null;
    }

    public boolean accessoryListEmpty() {
        if (accessoryList == null) {
            return true;
        }
        if (accessoryList.size() > 1) {
            return false;
        } else {
            return true;
        }
    }
    public List<String> autoCompleteSupplier(String query) {
        supplierList = aimService.getSuplierListBySiteUid(selectedPmOrder.getTenantUID());
        List<Supplier> suList = autoCompleteService.autoCompleteVendor(query, supplierList);
        List<String> sList = new ArrayList<String>();
        suList.forEach(item -> sList.add(item.getName()));
        return sList;
    }
    public List<V2_WorkOrder_Detail> getAccessoryList() {
        if (accessoryList == null) {
            return null;
        }
        V2_WorkOrder_Detail emptyDetail = new V2_WorkOrder_Detail();
        emptyDetail.setId("000");
        if (!accessoryList.contains(emptyDetail)) {
            accessoryList.add(emptyDetail);
        } else {
            accessoryList.remove(emptyDetail);
            accessoryList.add(emptyDetail);
        }
        return accessoryList;
    }

    public List<V2_WorkOrderCoworker> getCoWorkerList() {
        if (coWorkerList == null) {
            return null;
        }
        V2_WorkOrderCoworker emptyDetail = new V2_WorkOrderCoworker();
        emptyDetail.setId("000");
        if (!coWorkerList.contains(emptyDetail)) {
            coWorkerList.add(emptyDetail);
        } else {
            coWorkerList.remove(emptyDetail);
            coWorkerList.add(emptyDetail);
        }
        return coWorkerList;
    }

    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public V2_WorkOrderCoworker getCoWorkerDetail() {
        return coWorkerDetail;
    }

    public void setCoWorkerDetail(V2_WorkOrderCoworker coWorkerDetail) {
        this.coWorkerDetail = coWorkerDetail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_PmOrder> getSelectedPmOrderList() {
        return selectedPmOrderList;
    }

    public void setSelectedPmOrderList(List<V2_PmOrder> selectedPmOrderList) {
        this.selectedPmOrderList = selectedPmOrderList;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public Date getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Date takeTime) {
        this.takeTime = takeTime;
    }

    public String getStepFileter() {
        return stepFileter;
    }

    public void setStepFileter(String stepFileter) {
        this.stepFileter = stepFileter;
    }

    public List<V2_BlobObject> getPicList() {
        return picList;
    }

    public void setPicList(List<V2_BlobObject> picList) {
        this.picList = picList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<V2_BlobObject> audioList) {
        this.audioList = audioList;
    }

    public V2_PmOrder getSelectedPmOrder() {
        return selectedPmOrder;
    }

    public void setSelectedPmOrder(V2_PmOrder selectedPmOrder) {
        this.selectedPmOrder = selectedPmOrder;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setCoWorkerList(List<V2_WorkOrderCoworker> coWorkerList) {
        this.coWorkerList = coWorkerList;
    }

    public V2_WorkOrderCoworkerRepository getCoWorkerDao() {
        return coWorkerDao;
    }

    public void setCoWorkerDao(V2_WorkOrderCoworkerRepository coWorkerDao) {
        this.coWorkerDao = coWorkerDao;
    }

    public String getApprovaler() {
        return approvaler;
    }
    public void setNothing(){}

    public void setApprovaler(String approvaler) {
        this.approvaler = approvaler;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public String getFilterPlanTime() {
        return filterPlanTime;
    }

    public void setFilterPlanTime(String filterPlanTime) {
        this.filterPlanTime = filterPlanTime;
    }

    public String getPmOrderId() {
        return pmOrderId;
    }

    public void setPmOrderId(String pmOrderId) {
        this.pmOrderId = pmOrderId;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void setAccessoryList(List<V2_WorkOrder_Detail> accessoryList) {
        this.accessoryList = accessoryList;
    }

    public List<V2_WorkOrder_Detail> getApprovedDetails() {
        return approvedDetails;
    }

    public void setApprovedDetails(List<V2_WorkOrder_Detail> approvedDetails) {
        this.approvedDetails = approvedDetails;
    }

    public Boolean getIsSelectedCurrentPage() {
        return isSelectedCurrentPage;
    }

    public void setIsSelectedCurrentPage(Boolean selectedCurrentPage) {
        isSelectedCurrentPage = selectedCurrentPage;
    }

    public Boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    public Date getPlanTimeForBatch() {
        return planTimeForBatch;
    }

    public void setPlanTimeForBatch(Date planTimeForBatch) {
        this.planTimeForBatch = planTimeForBatch;
    }

    public String getCurrentPersonIdBatch() {
        return currentPersonIdBatch;
    }

    public void setCurrentPersonIdBatch(String currentPersonIdBatch) {
        this.currentPersonIdBatch = currentPersonIdBatch;
    }

    public Integer getPmType() {
        return pmType;
    }

    public void setPmType(Integer pmType) {
        this.pmType = pmType;
    }

    public String getQualityCheckingBatch() {
        return qualityCheckingBatch;
    }

    public void setQualityCheckingBatch(String qualityCheckingBatch) {
        this.qualityCheckingBatch = qualityCheckingBatch;
    }

    public String getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(String qaStatus) {
        this.qaStatus = qaStatus;
    }

    public Double getTotalManHours() {
        return totalManHours;
    }

    public void setTotalManHours(Double totalManHours) {
        this.totalManHours = totalManHours;
    }

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getNeedConfirmTime() {
        return needConfirmTime;
    }

    public void setNeedConfirmTime(Boolean needConfirmTime) {
        this.needConfirmTime = needConfirmTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
    }

    public Date getEsTime() {
        return esTime;
    }

    public void setEsTime(Date esTime) {
        this.esTime = esTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getConfirmedUpDate() {
        return confirmedUpDate;
    }

    public void setConfirmedUpDate(Date confirmedUpDate) {
        this.confirmedUpDate = confirmedUpDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setPmCheckItemList(List<PmOrderRuleCheckList> pmCheckItemList) {
        this.pmCheckItemList = pmCheckItemList;
    }

    public List<PmOrderRuleCheckList> getPmCheckItemList() {
        return pmCheckItemList;
    }

    public List<V2_BlobObject> getPicListForRP() {
        return picListForRP;
    }

    public void setPicListForRP(List<V2_BlobObject> picListForRP) {
        this.picListForRP = picListForRP;
    }

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public HashMap<String, UnstructuredData> getHmCheck() {
        return hmCheck;
    }

    public void setHmCheck(HashMap<String, UnstructuredData> hmCheck) {
        this.hmCheck = hmCheck;
    }

    public Integer getRepairTypeInt() {
        return repairTypeInt;
    }

    public void setRepairTypeInt(Integer repairTypeInt) {
        this.repairTypeInt = repairTypeInt;
    }

    public String getReportUid() {
        return reportUid;
    }

    public void setReportUid(String reportUid) {
        this.reportUid = reportUid;
    }




    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public List<V2_PmOrder> getCurrentPagePmOrders() {
        return currentPagePmOrders;
    }

    public void setCurrentPagePmOrders(List<V2_PmOrder> currentPagePmOrders) {
        this.currentPagePmOrders = currentPagePmOrders;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public AutoCompleteService getAutoCompleteService() {
        return autoCompleteService;
    }

    public void setAutoCompleteService(AutoCompleteService autoCompleteService) {
        this.autoCompleteService = autoCompleteService;
    }

    public AssetInfoMgrService getAimService() {
        return aimService;
    }

    public void setAimService(AssetInfoMgrService aimService) {
        this.aimService = aimService;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public OrgInfoTreeService getOrgInfoTreeService() {
        return orgInfoTreeService;
    }

    public void setOrgInfoTreeService(OrgInfoTreeService orgInfoTreeService) {
        this.orgInfoTreeService = orgInfoTreeService;
    }

    public String getCancelInt() {
        return cancelInt;
    }

    public void setCancelInt(String cancelInt) {
        this.cancelInt = cancelInt;
    }

}
