/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.pm;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.google.common.base.Strings;
import org.apache.commons.collections4.map.HashedMap;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
/**
 *v
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2PmOrderController extends GenericCRUDUUIDController<V2_PmOrder> implements Serializable, ServerEventInterface {
    V2_PmOrderRepository dao;
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
    private Date planTimeForBatch;
    private String currentPersonIdBatch;
    private String qualityCheckingBatch;
	Boolean terminate;
    private UaaService uaaService;
    private UserContextService userContextService;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private WorkOrderDetailRepository detailDao;
    private List<V2_PmOrder> selectedPmOrderList;
    //private V2_WorkOrderCoworkerRepository coWorkerDao;
    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;
    private AssetInfo selectedAsset;
    private Integer currentStatus = 1;
    private Boolean needConfirmTime = false;
    private String cancelReason;
    private String stepFileter;
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
    private List<V2_BlobObject> audioList;
    private V2_WorkOrderService woService;
    private V2_ServiceRequest newServiceRequest;
    private BlobObjectRepository blobDao;
    private String pmOrderId;
    private String filterPlanTime;
    private Map<String, Boolean> selectedMap;
	private Boolean isSelectedCurrentPage = false;
	private List<V2_PmOrder> currentPagePmOrders;
	private Integer pmType;

	public boolean isChecked() {
		Map<String, Boolean> map = this.selectedMap;
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			if (entry.getValue()) {
				return true;
			}

		}
		return false;
	}

    //是否允许内修

    public Boolean canCreateSr(){
        if(selectedAsset!= null && selectedAsset.getIbStatus()!= null && selectedAsset.getIbStatus() !=1){
            if(selectedAsset.getRepairForm() != null && selectedAsset.getRepairForm()==2){
                if(null != selectedAsset.getGeDeviceType() && selectedAsset.getGeDeviceType() == 22){
                    return selectedAsset.getWarrantyDate() != null && selectedAsset.getWarrantyDate().before(new Date());
                }

                if(null != selectedAsset.getGeDeviceType() && selectedAsset.getGeDeviceType() == 3){
                    return selectedAsset.getWarrantyDate() != null && selectedAsset.getWarrantyDate().before(new Date());
                }
                return false;
            }
        }
        return true;
    }
    public void prepareAproval(String woid) {
        prepareAct(woid);
        approvedDetails = getWorkOrderDetail(selectedPmOrder.getId());
        approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).forEach(item -> selectedMap.put((item.getId()), false));

    }

    public void onApprovedDetailsSelected(String id) {
        if ("All".equals(id)) {
            approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).forEach(item -> selectedMap.put(item.getId(), selectedMap.get("All")));
        } else {
            selectedMap.put("All", !selectedMap.entrySet().stream().anyMatch(item -> !item.getKey().equals("All") && !item.getValue()));
        }
    }

    public void accessoryArrival(String itemId){
        accessoryList.stream().filter(item -> itemId.equals(item.getId())).forEach(item -> item.setArrived(true));
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }
    public List<UserAccount> findRepairPerson(){
        if(this.selectedPmOrder!=null) {
            return  woService.getWorkerList(this.selectedPmOrder.getAssetId());
        } else {
            return Collections.emptyList();
        }
    }
    public List<UserAccount> findRepairPersonBatch(){
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        currentPagePmOrders.stream().forEach(item->{
            if(selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true){
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        if(pmListTmp.size()>0){ //如果是批量操作
            return pmOrderService.getWorkerList(pmListTmp.get(0).getAssetId());
        }

        if(this.selectedPmOrder!=null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public void reassignWorkOrder(){
        if(isChecked()==false) {//非批量
            pmOrderService.reassignWorkOrder(selectedPmOrder, selectedPmOrder.getCurrentPersonId(), comments);
        }else{//批量派工
            pmOrderService.reassignWorkOrderBatch(currentPagePmOrders,selectedMap,currentPersonIdBatch,comments);
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
    public OrgInfo getOrgInfoByUid(String orgUid) {
        return orgDao.getByUid(orgUid);
    }
    public void savePM(){
          PmOrderActionForm formData = new PmOrderActionForm();
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能保养");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        }
        if(Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())){
            WebUtil.addErrorMessage("没有使用科室的资产不能创建保养工单");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        }else{
            if(null!=planTime){
                formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
            }
            formData.setRuleLevel(ruleLevel);
            formData.setAttachments(picList);
            formData.setPmType(pmType);
            pmOrderService.createPMrequestApi(selectedAsset.getUid(),formData);
            cancel();
        }
    }
    public List<PmOrderSteps> aquirePmOrderStep(){
        if(selected!=null)
        	return pmOrderService.getPmOrderStep(selected.getId());
        else
            return new ArrayList<PmOrderSteps>();
    }
    public void createPMRequest(){
        cancel();
    }
	public void onStatusChange() {
		if (null != selectedAsset) {//new sr
			needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
		} else {//update sr
			needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
		}
	}
    public void createServiceRequest(V2_PmOrder selectedPmOrder) {
        selected = null;
        newServiceRequest = new V2_ServiceRequest();
        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
		if(selectedPmOrder!=null){
			selectedAsset=selectedPmOrder.getAssetInfo();
			if(Strings.isNullOrEmpty(selectedAsset.getLocationName())){
				final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
				dptName = orgDao.getByUid(orgInfoUID).getName();
			}else {
				dptName = selectedAsset.getLocationName();
			}
		}
    }
    public void saveServiceRequest() {
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        }else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("所选设备没有使用科室,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            //1表示主动报修
            newServiceRequest.setSrRepairType("1");
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(dptName);
            woService.createServiceRequest(newServiceRequest, selectedAsset, picList);
            //报修完后直接关单
            pmOrderService.closePM(selectedPmOrder,picList);
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
    ///
    protected void init() {
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        pmOrderService = WebUtil.getBean(V2_PmOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetFaultTypeService = WebUtil.getBean(AssetFaultTypeService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        detailDao = WebUtil.getBean(WorkOrderDetailRepository.class);
        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
		terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
		this.selectedMap = new HashMap();
         //如果当前人员只是 临床科室主任或者 只是临床科室人员...
        if((userContextService.hasAnyRole("DeptHead") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) ||(userContextService.hasAnyRole("ClinicalStaff") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest") ) || (userContextService.hasAnyRole("DeptHead") && userContextService.hasAnyRole("ClinicalStaff") &&!userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest"))){
            queryIndex= 8;
        }else {
            queryIndex = -1;
        }
        this.filterBySite = false;
        this.filterByHospital = false;
    }
    public void submitAppvalPmOrder() {
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        pmOrderService.submitWorkOrder(selectedPmOrder, accessoryList,approvaler,comments);
        cancel();
    }
    public void rateWorkOrder() {
        pmOrderService.rateWorkOrder(selectedPmOrder, rating, comments);
        cancel();
    }
    public void rejectWorkOrder() {
        pmOrderService.rejectWorkOrder(selectedPmOrder, failReason, currentStatus, confirmedDownTime);
        cancel();
    }
   /* public void approveWorkOrder(Boolean isAgree) {
        pmOrderService.approveAction(selectedPmOrder, isAgree, comments);
        cancel();
    }*/
    public void dispatchWorkOrder() {
        if(isChecked()==false) {
            Date planTime = selectedPmOrder.getPlanTime();
            pmOrderService.dispatchWorkOrder(selectedPmOrder, planTime);
            cancel();
        }else{//批量派工
            pmOrderService. dispatchWorkOrderBatch(currentPagePmOrders,selectedMap,currentPersonIdBatch,planTimeForBatch);
            cancel();

        }
    }

    public List<UserAccount> getWorkerList() {
        if(this.selectedPmOrder!=null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }


    public List<UserAccount> getWorkerListBatch() {
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        currentPagePmOrders.stream().forEach(item->{
            if(selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true){
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
            if(pmListTmp.size()>0){ //如果是批量操作
            return pmOrderService.getWorkerList(pmListTmp.get(0).getAssetId());
        }

        if(this.selectedPmOrder!=null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }




    public List<UserAccount> getApproveList() {
        if(this.selectedPmOrder!=null)
            return pmOrderService.getApproveList(selectedPmOrder.getAssetUid());
        else
            return new ArrayList<UserAccount>();
    }
    private List<Integer> tagGroupAssets;
    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }
    public void acceptWorkOrder() {
        if(isChecked()==false) //如果是false说明不是批量的操作
        {
        Date planTime =selectedPmOrder.getPlanTime();
        pmOrderService.acceptWorkOrder(selectedPmOrder,planTime);
        }else{ //批量操作
            pmOrderService.acceptWorkOrderBatch(currentPagePmOrders,selectedMap,planTimeForBatch);
		}
		cancel();
    }

    private List<Integer> tagNoGroupAssets;
    private List<Integer> getNoTagGroupAssets() {
        if (tagNoGroupAssets == null) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            if(orgUidList ==null || orgUidList.isEmpty()){
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
    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        removeFilterOnField("currentStepId");
        removeFilterOnField("isFinished");
        removeFilterOnField("dispatchMode");
        removeFilterOnField("ownerId");
        removeFilterOnField("approvalStatus");
        if(queryIndex==-2){
            searchFilters.add(new SearchFilter("simplify", SearchFilter.Operator.EQ, true));
        }
        if (queryIndex == -1) {
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
			Page<V2_PmOrder> result =dao.findBySearchFilter(this.searchFilters, pageRequest);
			currentPagePmOrders = result.getContent();
			return result;
        }
         if (queryIndex == 2) { //待派工  Assign
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE,true ));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
             searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
             List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
             searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
             searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
             searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
             searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
             searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
         } else if (queryIndex == 3) {//待接单
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept"));
			 searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
			 //someone could only access siteA,but assigned with asset on SiteB, therefore "OR" is added saying that one also could see the asset that is assigned to him.
             searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(),"1*2+"));
        }else if(queryIndex == 31){//抢单 dispatch 2是人工派工或抢单模式 3 抢单模式 4是人工派工
			 //抢单tab中 ,已经派的单但是未被接单人接的单子也要显示出来 (已经派的单状态是Accept，所以也要加进来)
          /*  searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList("Assign","Accept")));*/
             searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.NE, 4));
          List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
             searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*6+"));
             searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*6+"));
             searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*6+"));
             searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*6+"));
             searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, sitelist, "1*6+"));
             searchFilters.add(new SearchFilter("currentStepId",SearchFilter.Operator.EQ ,"Assign","1*5+"));
             searchFilters.add(new SearchFilter("currentStepId",SearchFilter.Operator.EQ ,"Accept","1*5+6*"));
             searchFilters.add(new SearchFilter("currentPersonId",SearchFilter.Operator.NE,UserContextService.getCurrentUserAccount().getUid(),"1*5+6*"));
            //searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.EQ, "1*3+4*"));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2,3), "1*3+4*"));


            //searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2,4), "1*3+4*"));

            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+4*"));

            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        //用户必须对其可视范围内的设备都可见,包括打过标签的设备(如果该设备的标签和组绑定了，并且当前用户没有在该组内，那是看不到的)和没有打过设备的标签
                 //打过标签的设备(该标签都和组绑定过)
                 List<Integer> tagGroupAssets = getTagGroupAssets();
                 if(tagGroupAssets!=null && !tagGroupAssets.isEmpty()){
                     searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets,"1*3+4*5+"));
                 }else{
                     searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0),"1*3+4*5+"));
                 }
                 //没有打过标签的设备
                 tagGroupAssets = getNoTagGroupAssets();
                 if(tagGroupAssets!=null && !tagGroupAssets.isEmpty()){
                     searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets,"1*3+4*5+"));
                 }else{
                     searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0),"1*3+4*5+"));
                 }


            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE,true ));
        }
        else if (queryIndex == 6) { //待关单
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Close"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE,true ));
             searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }else if (queryIndex == 8) { //待审核
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
           //1表示该pm工单已被提交等待审核
            searchFilters.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE,true ));
             searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(),"1*2+"));
        }
        else if (queryIndex == 0) { //保养中
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
             searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(),"1*3+"));
             searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
             //   searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE,true ));
             searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
             searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }
         else if (queryIndex == 7) { //已关单
             if (!userContextService.hasRole("AssetHead") && !userContextService.isPmDispatcher()) {
                 searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
                 searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));

             }
             searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, true));
         }
        this.selected = null;

		Page<V2_PmOrder> result =dao.findBySearchFilter(this.searchFilters, pageRequest);

		currentPagePmOrders = result.getContent();
		this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));


		return result;
    }
    public void onSelectPmOrder(){
        accessoryList = getWorkOrderDetail(selected.getId());
        coWorkerList = getWorkOrderCoworker(selected.getId());
       // 获取picList
		picList =  blobDao.findByBoUuid(selected.getId());
    }
    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        List<V2_WorkOrder_Detail> list = pmOrderService.getWorkOrderDetails(woId);
        return pmOrderService.getWorkOrderDetails(woId);
    }
    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return pmOrderService.getWorkOrderCoworker(woId);
    }
public void closePM(V2_PmOrder selectedPmOrder){
        if(isChecked()==false){
        pmOrderService.closePM(selectedPmOrder,picList);
        }else{
            picList.stream().forEach(item->{
            item.setId(null);
            });
            pmOrderService.closePMBatch(currentPagePmOrders,selectedMap,picList);
        }
    cancel();
}

public void batchDefineQualify(){
    List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
    currentPagePmOrders.stream().forEach(item->{
        if(selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true){
            //pmListTmp保存了所要提交的批量pmOrder
            item.setQualityChecking(qualityCheckingBatch);
            pmListTmp.add(item);
        }
    });
    System.out.println();
}

    public void approvePMorder(){
        if(selectedPmOrder!=null)
            approvedDetails.stream().filter(item -> Optional.ofNullable(selectedMap.get(item.getId())).orElse(false)).forEach(item -> item.setApproved(selectedMap.get(item.getId())));
            pmOrderService.approvePMorder(selectedPmOrder,approvedDetails,this.comments);
    }
    public void prepareCompleteOrder(String woid) {
       selectedPmOrder = dao.findById(woid);
       confirmedUpDate = null;
       endTime=selectedPmOrder.getEndTime();
       planTime=selectedPmOrder.getPlanTime();
       accessoryList = getWorkOrderDetail(woid);
        coWorkerList = getWorkOrderCoworker(woid);
       picList = new ArrayList<V2_BlobObject>();
   }

    //
	public void onSelectAllOfPage() {
		for (V2_PmOrder item : currentPagePmOrders) {
			selectedMap.put(item.getId(), this.isSelectedCurrentPage);
		}
    }
	public void onItemSelected() {
		isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        selectedPmOrder=null;//
	}
   /* @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }*/
    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
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
        planTime=null;
        planTimeForBatch =null;
        selectedPmOrder=null;
        assetName=null;
        endTime=null;
        ruleLevel=null;
        dptName=null;
        picList=null;
        planTimeForBatch=null;
        comments=null;
    }
    public void prepareAct(String woid) {
        if(woid!=null)
        selectedPmOrder = dao.findById(woid);
        //关单时 默认设置为合格
        selectedPmOrder.setQualityChecking("1");
        /*if(selectedPmOrder.getAssetInfo()!=null)
         currentStatus = selectedPmOrder.getAssetInfo().getStatus();*/
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        picList = new ArrayList<V2_BlobObject>();
    }
    public void repairPmOrder() {
        if(endTime==null){
            WebUtil.addErrorMessage("结束时间"+WebUtil.getMessage("ValidationRequire"));
        }
       /* if(planTime==null){
            WebUtil.addErrorMessage("计划完成时间"+WebUtil.getMessage("ValidationRequire"));
        }*/
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList,endTime,selectedPmOrder.getPlanTime(),picList);
        cancel();
    }
    public void stagingPmOrder(){
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        pmOrderService.stagingPmrder(selectedPmOrder, accessoryList, coWorkerList,endTime,selectedPmOrder.getPlanTime(),picList);
        cancel();
    }
    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        assetName=asset.getName();
        if(Strings.isNullOrEmpty(asset.getClinicalDeptName()) )  {
           dptName=null;
        }
        else{
            dptName=asset.getClinicalDeptName();
        }
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;
    }


    public void prepareEditAccDetail(String itemId) {
        if ("new".equals(itemId)) {
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            itemDetail.setWoId(selectedPmOrder.getId());
        } else {
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }
    public void removeWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
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
        if (!accessoryList.contains(itemDetail)) {
            accessoryList.add(itemDetail);
        }
      //  cancelWorkdetail();
    }
    private V2_WorkOrderCoworkerRepository coWorkerDao;
    public void addCoWokerItem() {
        if (!coWorkerList.contains(coWorkerDetail)) {
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
            coWorkerList.add(coWorkerDetail);
        }else{
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
        }
     //   cancelWorkdetail();
    }
    public void cancelWorkdetail() {
        itemDetail = null;
        coWorkerDetail = null;
    }
    public boolean accessoryListEmpty(){
        if(accessoryList==null){
            return true;
        }
        if(accessoryList.size()>1){
            return false;
        }else {
            return true;
        }
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
    public List<V2_WorkOrder_Detail> getAccessoryList() {
        if (accessoryList == null) {
           // accessoryList = new ArrayList<V2_WorkOrder_Detail>();
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
           // coWorkerList  = new ArrayList<V2_WorkOrderCoworker>();
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
    public String getDptName() {
        return dptName;
    }
    public void setDptName(String dptName) {
        this.dptName = dptName;
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
}
