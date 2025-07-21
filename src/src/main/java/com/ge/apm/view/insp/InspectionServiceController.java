package com.ge.apm.view.insp;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.WorkOrderEventRecordDto;
import com.ge.apm.service.asset.AssetHistoryService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.nurseSite.export.NurseSiteMaintainExport;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.asset.AssetInfoDetailController;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.TabChangeEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

@ManagedBean
@ViewScoped
public class InspectionServiceController extends GenericCRUDUUIDController<V2_WorkOrder> implements Serializable {

    InspectionOrderRepository inspectionDao = WebUtil.getBean(InspectionOrderRepository.class);
    WorkOrderMapper workOrderMapper = WebUtil.getBean(WorkOrderMapper.class);
    InventoryOrderDetailAssetInfoRepository infoRepositoryDao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
    InventoryOrderRepository inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
    InventoryIssueRepository inventoryIssueRepository = WebUtil.getBean(InventoryIssueRepository.class);
    PmOrderMapper pmOrderMapper = WebUtil.getBean(PmOrderMapper.class);
    UaaService uuaService = WebUtil.getBean(UaaService.class);
    AssetInfoMapper assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
    MvsIbMappingMapper mvsIbMappingMapper =  WebUtil.getBean(MvsIbMappingMapper.class);
    NurseSiteMaintainMapper nurseSiteMaintainMapper =  WebUtil.getBean(NurseSiteMaintainMapper.class);
    AssetOperationRecordMapper assetOperationRecordMapper =  WebUtil.getBean(AssetOperationRecordMapper.class);
    AssetOptRecordMapper assetOptRecordMapper =  WebUtil.getBean(AssetOptRecordMapper.class);
    AdverseEventsMapper adverseEventsMapper =  WebUtil.getBean(AdverseEventsMapper.class);
    MultiLevelApprovalMapper approvalMapper =  WebUtil.getBean(MultiLevelApprovalMapper.class);
    AssetHistoryService assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
    ConfigService configService = WebUtil.getBean(ConfigService.class);

    @Getter @Setter
    List<AssetInfo> detailMeasureAssetList;
    @Getter @Setter
    List<WorkOrderEventRecordDto> detailWorkOrderList;
    @Getter @Setter
    List<V2_PmOrder> detailPmOrderList;
    @Getter @Setter
    List<NurseSiteMaintainExport> detailMaintainDataList;
    @Getter @Setter
    List<AssetOperationRecord> detailAssetOperationRecordList;
    @Getter @Setter
    List<AssetOptRecord> detailAssetOptRecordList;
    @Getter @Setter
    List<InventoryOrderDetail> detailInventoryOrderDetailList;
    @Getter @Setter
    List<AdverseEvents> detailAdverseEventList;
    @Getter @Setter
    List<MultiLevelApproval> detailApprovalEventList;
    @Getter
    private List<JSONObject> detailAssetChangeHistoryList;
    @Getter @Setter
    private AssetInfo assetInfo;
    @Getter @Setter
    private Boolean needTwoSubStatus=false;
    @Getter @Setter
    private String stepFilter;
    @Getter @Setter
    private Date startTime;
    @Getter @Setter
    private Date endTime;
    @Getter @Setter
    private boolean dateQuery;
    private Map<String, Integer> woStatus = new HashMap<>();

    @Override
    protected void init() {
        needTwoSubStatus = isConfigNeedTwoSubStatusByUser(UserContextService.getCurrentUserAccount());
        FieldValueMessageController.getFieldValueListByMsgType("woSteps").forEach(item -> woStatus.put(item.getValueZh(), Integer.parseInt(item.getMsgKey())));
    }

    public List<InspectionOrder> getInspectionOrderList(Integer assetId,Integer orderType,Integer limit){
        List<InspectionOrder> res = inspectionDao.getRecentlyInspectionOrder(assetId,orderType);
        return res.subList(0, Math.min(limit, res.size()));
    }

    public List<AssetInfo> getMeasureAssetList(Integer assetId){
        Pagination pagination = new Pagination(0, 1000);
        Wrapper wrapper = getWrapper("ai.id", null, null, ListUtil.newArrayList(assetId), "mcd.check_date");
        return assetInfoMapper.selectMeasureResult(pagination, wrapper);
    }

    public List<WorkOrderEventRecordDto> getWorkOrderList(String assetUid){
        return getWorkOrderList(assetUid, 1000);
    }

    public List<WorkOrderEventRecordDto> getWorkOrderList(String assetUid, Integer limit){
        List<String> assetUIds = mvsIbMappingMapper.findMvsAssetUIds(assetUid);
        assetUIds.add(assetUid);
        Pagination pagination = new Pagination(0, limit);
        Wrapper wrapper = getWrapper("wo.asset_uid", null, assetUIds, null, "sr.request_time");
        if (ObjectUtil.isNotEmpty(stepFilter)) {
            Integer filterStepId = woStatus.get(stepFilter);
            List<Integer> steps;
            if (filterStepId > 6) {
                if (UserContextService.hasCurrentTenantName("")) {
                    steps = Arrays.asList(7, 9);
                } else if (filterStepId == 10 || filterStepId == 11){
                    steps = Collections.singletonList(filterStepId);
                }else {
                    steps = Collections.singletonList(7);
                }
            } else {
                if (filterStepId == 3 && UserContextService.hasCurrentTenantName("")) {
                    steps = Arrays.asList(2, 3);
                } else {
                    steps = Collections.singletonList(filterStepId);
                }
            }
            wrapper.in("wo.currentStepId", steps);
        }
        return workOrderMapper.getEventRecord(pagination, wrapper, assetUIds.stream()
                .map(id -> "'" + id.replace("'", "''") + "'")
                .collect(Collectors.joining(",")));
    }

    public List<V2_PmOrder> getPmOrderList(String assetUid){
        return getPmOrderList(assetUid, 1000);
    }

    public List<V2_PmOrder> getPmOrderList(String assetUid,Integer limit){
        List<String> assetUIds = mvsIbMappingMapper.findMvsAssetUIds(assetUid);
        assetUIds.add(assetUid);
        Pagination pagination = new Pagination(0, limit);
        Wrapper wrapper = getWrapper("asset_uid", null, assetUIds, null, "end_time");
        return pmOrderMapper.getEventRecord(pagination, wrapper);
    }

    public List<NurseSiteMaintainExport> getMaintainDataList(){
        Pagination pagination = new Pagination(0, 1000);
        Wrapper wrapper = getWrapper("nsmi.asset_uid", null, ListUtil.newArrayList(this.assetInfo.getUid()), null, "nsmi.schedule_date");
        List<NurseSiteMaintainExport> eventRecords = nurseSiteMaintainMapper.getEventRecord(pagination, wrapper);
        eventRecords.forEach(item -> item.setStatus(setStatus(Integer.parseInt(item.getStatus()))));
        return eventRecords;
    }

    public List<AssetOperationRecord> getAssetOperationRecordList(){
        Pagination pagination = new Pagination(0, 1000);
        Wrapper wrapper = getWrapper("asset_info_uid", null, ListUtil.newArrayList(this.assetInfo.getUid()), null, "create_date");
        return assetOperationRecordMapper.getEventRecord(pagination, wrapper);
    }

    public List<AssetOptRecord> getAssetOptRecordList(){
        Pagination pagination = new Pagination(0, 1000);
        Wrapper wrapper = getWrapper("aor.asset_uid", null, ListUtil.newArrayList(this.assetInfo.getUid()), null, "aor.start_date");
        List<AssetOptRecord> eventRecords = assetOptRecordMapper.getEventRecord(pagination, wrapper);
        eventRecords.forEach(item -> item.setCostTime(DateUtil.generateCostTime(item.getStartDate()==null?new Date():item.getStartDate(), item.getEndDate()==null?new Date():item.getEndDate())));
        return eventRecords;
    }

    public List<AdverseEvents> getAdverseEventList() {
        if (ObjectUtil.isEmpty(this.assetInfo.getDepartNum())) {
            return ListUtil.empty();
        }
        Pagination pagination = new Pagination(0, 1000);
        Wrapper wrapper = getWrapper("device_num", this.assetInfo.getDepartNum(), null, null, "report_date");
        return adverseEventsMapper.getEventRecord(pagination, wrapper);
    }

    public List<MultiLevelApproval> getApprovalEventList() {
        Pagination pagination = new Pagination(0, 1000);
        Wrapper wrapper = getWrapper("ma.tenant_uid", this.assetInfo.getTenantUID(), null, null, "ma.actual_approver_date");
        wrapper.isNotNull("ma.initiate_approver_uid");
        wrapper.andNew().eq("scrap.asset_uid", this.assetInfo.getUid()).or().eq("adc.asset_uid", this.assetInfo.getUid()).or().eq("sr.asset_uid", this.assetInfo.getUid()).or().eq("po.asset_uid", this.assetInfo.getUid());
        return approvalMapper.getEventRecord(pagination, wrapper);
    }

    public List<JSONObject> getAssetChangeList() {
        return assetHistoryService.getAssetChangeHistoryByJSON(this.assetInfo.getUid(), this.startTime, this.endTime, 0, 1000);
    }

    public List<InventoryOrderDetail> getInventoryOrderDetailList(Integer assetId){
        List<InventoryOrderDetail> inventoryOrderDetails = infoRepositoryDao.findProjectionByAssetId(assetId);
        if(inventoryOrderDetails != null && inventoryOrderDetails.size()>0){
            for(InventoryOrderDetail inventoryOrderDetail:inventoryOrderDetails){
                if(inventoryOrderDetail.getStatus() == 0){
                    inventoryOrderDetail.setLastModifiedDate(null);
                    inventoryOrderDetail.setLastModifiedBy(null);
                }
                String inventoryOrderId = inventoryOrderDetail.getInventoryOrderId();
                if(inventoryOrderId != null){
                    InventoryOrder inventoryOrder = inventoryOrderRepository.findById(inventoryOrderId);
                    inventoryOrderDetail.setPlanEndTime(inventoryOrder.getPlanEndTime());
                    inventoryOrderDetail.setPlanStartTime(inventoryOrder.getPlanStartTime());
                    if(inventoryOrderDetail.getStatus() == 1){
                        InventoryIssue inventoryIssue = inventoryIssueRepository.findProjectionByAssetIdAndInventoryOrderId(assetId, inventoryOrderId);
                        if(inventoryIssue != null){
                            inventoryOrderDetail.setStatus(3);
                        }
                    }
                }
            }
        }
        return inventoryOrderDetails;
    }

    public List<String> getStepsInTableFilter() {
        List<String> steps = new ArrayList<>();
        steps.add("待派工");
        steps.add("待接单");
        steps.add("已计划");
        steps.add("待验收");
        return steps;
    }

    private Wrapper getWrapper(String queryField, String queryValue, List<String> assetUids, List<Integer> assetIds, String dateField) {
        Wrapper wrapper = new EntityWrapper();
        if (ObjectUtil.isNotEmpty(queryValue)) {
            wrapper.eq(queryField, queryValue);
        }
        if (ObjectUtil.isNotEmpty(assetUids)) {
            wrapper.in(queryField, assetUids);
        }
        if (ObjectUtil.isNotEmpty(assetIds)) {
            wrapper.in(queryField, assetIds);
        }
        if (ObjectUtil.isNotEmpty(startTime)) {
            wrapper.ge(dateField, startTime);
        }
        if (ObjectUtil.isNotEmpty(endTime)) {
            wrapper.le(dateField, endTime);
        }
        wrapper.orderBy(dateField, false);
        return wrapper;
    }

    public List<UserAccount> getOwnerList(){
        return getOwnerList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    public List<OrgInfo> getOwnerOrgList(){
        return getOwnerOrgList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    public List<UserAccount> getOwnerList(Integer hospitalId){
        return uuaService.getUserList(hospitalId);
    }
    public List<OrgInfo> getOwnerOrgList(Integer hospitalId){
        return uuaService.getOrgListByHospitalId(hospitalId);
    }

    public void onTabChange(TabChangeEvent event) {
        if ("EventRecordTab".equals(event.getTab().getId())) {
            AssetInfoDetailController assetInfoDetailController =  WebUtil.getBean(AssetInfoDetailController.class);
            query(assetInfoDetailController.getSelected());
        }
    }

    public void query(AssetInfo assetInfo) {
        if (ObjectUtil.isNotEmpty(endTime)) {
            endTime = com.ge.healthcare.cn.common.utils.date.DateUtil.endOfDay(endTime);
        }
        this.assetInfo = assetInfo;
        detailMeasureAssetList = getMeasureAssetList(assetInfo.getId());
        detailWorkOrderList = getWorkOrderList(assetInfo.getUid());
        detailPmOrderList = getPmOrderList(assetInfo.getUid());
        detailMaintainDataList = getMaintainDataList();
        detailAssetOperationRecordList = getAssetOperationRecordList();
        detailAssetOptRecordList = getAssetOptRecordList();
        detailInventoryOrderDetailList = getInventoryOrderDetailList(assetInfo.getId());
        detailAdverseEventList = getAdverseEventList();
        detailApprovalEventList = getApprovalEventList();
        detailAssetChangeHistoryList = getAssetChangeList();
    }

    private String setStatus(Integer status){
        //0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
        String str=null;
        switch (status){
            case 0:
                str="未检查";
                break;
            case 2:
            case 5:
            case 6:
            case 7:
                str="已通过";
                break;
            case 3:
            case 8:
            case 9:
                str="有问题";
                break;
            case 4:
                str="进行中";
                break;
            default:
                str="";
        }
        return str;
    }

    public Boolean isConfigNeedTwoSubStatusByUser(UserAccount userAccount) {
        WorkflowConfig woc = configService.findConfigBySite(userAccount.getSiteUID());
        return woc != null&&woc.getEnableSendRepair()!=null&&woc.getEnableSendRepair();
    }

    public String getStepImg(WorkOrderEventRecordDto workOrderEventRecordDto){
        Integer stepId = workOrderEventRecordDto.getCurrentStepId();
        int mode = workOrderEventRecordDto.getDispatchMode() != null ? workOrderEventRecordDto.getDispatchMode().intValue() : 0;
        switch(stepId){
            case 7: //待评价
                return "icons/5.png";
            case 6: //
                return "icons/5.png";
            case 5: //待验收
                if( workOrderEventRecordDto.getDetailCount() >0 && workOrderEventRecordDto.getApprovalDate()==null){//有备件 没有提交审核 则显审核图标显示为灰色的
                    return "icons/6.png";
                }
                if(workOrderEventRecordDto.getDetailCount() == 0){
                    return "icons/6.png";
                }
                else{
                    return "icons/5.png";
                }
            case 8: //待审核
                return "icons/4.png";
            case 4: //维修中
            case 10: //待送修
            case 11: //待返还
                if(workOrderEventRecordDto.getApprovalDate()!=null){
                    return  "icons/4.png";
                }
                return "icons/3.png";
            case 3: //接单中
                return "icons/2.png";
            case 2:
                if(mode== 2 || mode ==3 ){//如果是抢单或抢单加手动模式
                    return "icons/2.png";
                }
            default:
                return "icons/1.png";
        }
    }

    public List<I18nMessage> getWoSteps(String stepIds){
        List<String> stepIdsList = Arrays.asList(stepIds.split(","));
        return FieldValueMessageController.doGetFieldValueList("woSteps", -1).stream()
                .filter(item -> stepIdsList.contains(item.getMsgKey())).collect(Collectors.toList());
    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return null;
    }
}
