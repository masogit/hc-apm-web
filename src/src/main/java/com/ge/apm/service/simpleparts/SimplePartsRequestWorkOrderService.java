package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.*;
import com.ge.apm.dao.simpleparts.SimplePartsItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestLineItemRepository;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseUserRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.simpleparts.*;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/8
 * @descript 收据业务
 * @package com.ge.apm.service.spareParts
 */
@Service
public class SimplePartsRequestWorkOrderService {
    private static final Logger log = LoggerFactory.getLogger(SimplePartsRequestWorkOrderService.class);

    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;

    @Autowired
    private V2_WorkOrderRepository woDao;

    @Autowired
    private V2_PmOrderRepository v2PmOrderRepository;

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private ServiceRequestRepository srDao;

    @Autowired
    private SimplePartsRequestLineItemRepository partsRequestLineItemRepository;

    @Autowired
    private SimplePartsWarehouseService partsWarehouseService;

    @Autowired
    private SimplePartsRequestLineItemService partsRequestLineItemService;

    @Autowired
    private SimplePartsItemRepository partsItemRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private SimplePartsCodeGenerationService partsCodeGenerationService;

    @Autowired
    private WechatSendMsgService wechatSendMsgService;

    @Autowired
    private SimplePartsWarehouseUserRepository partsWarehouseUserRepository;

    @Autowired
    private SimplePartsConfigService partsConfigService;

    /**
     * 生成申购单
     * @param accessoryList
     */
    @Transactional
    public void savePartsPurchase(List<V2_WorkOrder_Detail> accessoryList,int type, UserAccount userAccount){
        if(userAccount == null ){
            userAccount = UserContextService.getCurrentUserAccount();
        }
        SimplePartsRequest partsRequest = new SimplePartsRequest();
        //check whether work order is valid parts request
        if (!checkAndAssignmentAccessoryList(accessoryList, partsRequest, type, userAccount)) {
            return;
        }

        partsRequest.setRequestCode(partsCodeGenerationService.getNextRequestCode(SimplePartsConstant.PARTS_PURCHASE_FORM));
        // 申购单类型
        partsRequest.setType(SimplePartsConstant.PARTS_PURCHASE_FORM);
        // 未入库
        partsRequest.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
        // 已审批
        partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
        partsRequestRepository.save(partsRequest);
        for (int i = 0; i <accessoryList.size() ; i++) {
            V2_WorkOrder_Detail item = accessoryList.get(i);
            //一个备件的数量
            Integer apartsQuantity = item.getApartsQuantity();
            //一个备件的总价
            BigDecimal oneTotalPrice = new BigDecimal(item.getPartsPrice()).multiply(new BigDecimal(apartsQuantity));
            SimplePartsRequestLineItem partsRequestLineItem = new SimplePartsRequestLineItem();
            partsRequestLineItem.setTotalPrice(oneTotalPrice);
            partsRequestLineItem.setRequestQty(apartsQuantity);
            if(isCountyArea()){//保存县域租户备件表拼凑的收货信息
                partsRequestLineItem.setAddressInfo(item.getComment());
                partsRequestLineItem.setPartsDescription(item.getPartsDescription());
            }
            assignmentPartsRequestLineItem(item, partsRequest, partsRequestLineItem, userAccount);
            // 未入库
            partsRequestLineItem.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
            partsRequestLineItemService.save(partsRequestLineItem);
        }
        //发送微信通知
        this.sendWxMsg(partsRequest);
    }

    @Transactional(readOnly = true)
    public void sendWxMsg(SimplePartsRequest partsRequest){
        try {
            log.info("工单申请备件发送微信通知,"+"工单号:"+partsRequest.getMaintenanceWorkOrderNumber()+",申请编号:"+partsRequest.getRequestCode());
            if(StringUtils.isEmpty(partsRequest.getPartsWarehouseId())) {
                log.info("工单申请备件,无默认仓库,"+"工单号:"+partsRequest.getMaintenanceWorkOrderNumber()+",申请编号:"+partsRequest.getRequestCode());
                return;
            }

            List<SimplePartsWareHouseUser> partsWareHouseUserList = partsWarehouseUserRepository.findAllByPartsWarehouseId(partsRequest.getPartsWarehouseId());
            Set<String> uidSets = partsWareHouseUserList.stream().filter(item -> StringUtils.isNotEmpty(item.getUserUid())).map(item -> item.getUserUid()).collect(Collectors.toSet());
            if(uidSets.size() == 0){
                log.info("工单申请备件,默认仓库未查询到仓库管理员,"+"工单号:"+partsRequest.getMaintenanceWorkOrderNumber()+",申请编号:"+partsRequest.getRequestCode()+",仓库id:"+partsRequest.getPartsWarehouseId());
                return;
            }

            List<UserAccount> userAccountList = userAccountRepository.getByUids(uidSets);
            if(userAccountList.size() == 0){
                log.info("工单申请备件,未查询到备件管理员用户信息,"+"工单号:"+partsRequest.getMaintenanceWorkOrderNumber()+",申请编号:"+partsRequest.getRequestCode()+",仓库id:"+partsRequest.getPartsWarehouseId());
                return;
            }

            List<String> weChatIdList = userAccountList.stream().filter(item -> StringUtils.isNotEmpty(item.getWeChatId())).map(item -> item.getWeChatId()).collect(Collectors.toList());
            if(weChatIdList.size() == 0){
                log.info("工单申请备件,未查询到备件管理员微信信息,"+"工单号:"+partsRequest.getMaintenanceWorkOrderNumber()+",申请编号:"+partsRequest.getRequestCode()+",仓库id:"+partsRequest.getPartsWarehouseId());
                return;
            }
            //如果有默认仓库,通知管理员
            String message = "您有工单号 : "+partsRequest.getMaintenanceWorkOrderNumber()+" , 备件出入库待处理";
            for (int i = 0; i < weChatIdList.size(); i++) {
                String str = weChatIdList.get(i);
                if(str != null){
                    Map<String,Object> map = new HashMap<>();
                    map.put("_openId",str);
                    map.put("_templateId","no_template");
                    map.put("message", SimplePartsConstant.WX_MESSAGE_PARTS_STOCK_IN_OR_OUT+ TimeUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss") + message);
                    wechatSendMsgService.sendWxMsg(map);
                }
            }
        }catch (Exception e){
            log.error("工单生成出入库备件,发送微信通知异常."+"申请信息"+partsRequest.toString(),e.getMessage());
        }
    }

    /**
     * 保存备件申领单据
     * @param accessoryList
     * type 工单类型
     */
    @Transactional
    public void savePartsRequest(List<V2_WorkOrder_Detail> accessoryList,int type, UserAccount userAccount){
        if(userAccount == null ){
            userAccount = UserContextService.getCurrentUserAccount();
        }
        SimplePartsRequest partsRequest = new SimplePartsRequest();
        //check whether work order is valid parts request
        if (!checkAndAssignmentAccessoryList(accessoryList, partsRequest, type, userAccount)) {
            return;
        }
        partsRequest.setRequestCode(partsCodeGenerationService.getNextRequestCode(SimplePartsConstant.PARTS_REQUEST_FORM));//生成备件申领单申购单编号
        partsRequest.setType(SimplePartsConstant.PARTS_REQUEST_FORM);        // 申领单类型
        partsRequest.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);       // 未出库
        partsRequest.setProposerStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE); // 已审批
        partsRequestRepository.save(partsRequest);
        //工单过来的,暂不做库存校验
        for (int i = 0; i <accessoryList.size() ; i++) {
            V2_WorkOrder_Detail item = accessoryList.get(i);
            //一种备件的数量
            Integer apartsQuantity = item.getApartsQuantity();
            //一个备件的总价
            BigDecimal oneTotalPrice = new BigDecimal(item.getPartsPrice()).multiply(new BigDecimal(apartsQuantity));
            SimplePartsRequestLineItem partsRequestLineItem = new SimplePartsRequestLineItem();
            partsRequestLineItem.setTotalPrice(oneTotalPrice);
            partsRequestLineItem.setRequestQty(apartsQuantity);
            if(isCountyArea()){//保存县域租户备件表拼凑的收货信息
                partsRequestLineItem.setAddressInfo(item.getComment());
                partsRequestLineItem.setPartsDescription(item.getPartsDescription());
            }
            assignmentPartsRequestLineItem(item, partsRequest, partsRequestLineItem, userAccount);
            // 未出库
            partsRequestLineItem.setStatus(SimplePartsConstant.PARTS_NOT_TRANSACTIONED);
            partsRequestLineItemService.save(partsRequestLineItem);
        }
        //发送微信通知
        this.sendWxMsg(partsRequest);
    }

    /**
     * @param accessoryList
     * @param partsRequest
     * @param type 0 维修工单, 1 保养工单
     * @param firstMultiLevelApproval 如果多级审核过来的申请单,需要从第一级审核中获取申请人信息
     * @return
     */
    private boolean checkAndAssignmentAccessoryList(List<V2_WorkOrder_Detail> accessoryList, SimplePartsRequest partsRequest, int type, UserAccount userAccount) {

        if(accessoryList == null || accessoryList.size() == 0){
            log.error("not find workOrder PartsRequest partsInfo, 没有找到报修备件信息.");
            return false;
        }
        V2_WorkOrder_Detail v2_WorkOrder_Detail = accessoryList.get(0);
        if(v2_WorkOrder_Detail == null || StringUtils.isBlank(v2_WorkOrder_Detail.getWoId())){
            log.error("not find workOrder PartsRequest partsInfo or woId, 没有找到报修备件信息或者维修id.");
            return false;
        }

        if(SimplePartsConstant.WORK_ORDER == type){//维修
            Optional<SimplePartsWarehouse> partsWarehouse = getPartsWarehouseByWorkOrderId(accessoryList.get(0).getWoId());
            if (partsWarehouse.isPresent()) {
                partsRequest.setPartsWarehouseId(partsWarehouse.get().getId());
            }
        }
        if(SimplePartsConstant.PM_ORDER == type){//保养
            Optional<SimplePartsWarehouse> partsWarehouse = getPartsWarehouseByPmOrderId(accessoryList.get(0).getWoId());
            if (partsWarehouse.isPresent()) {
                partsRequest.setPartsWarehouseId(partsWarehouse.get().getId());
            }
        }

        String woId = v2_WorkOrder_Detail.getWoId();
        // 保存维修保养工单id
        partsRequest.setOrderType(type);
        partsRequest.setOrderId(woId);
        if(SimplePartsConstant.WORK_ORDER == type){
            //添加维修工单号
            if(StringUtils.isNotEmpty(woId)){
                V2_WorkOrder v2_workOrder = woDao.findById(woId);
                if(v2_workOrder != null){
                    V2_ServiceRequest v2_serviceRequest = srDao.findById(v2_workOrder.getSrId());
                    if(v2_serviceRequest != null){
                        partsRequest.setMaintenanceWorkOrderNumber(v2_serviceRequest.getSrNum());
                    }else {
                        partsRequest.setMaintenanceWorkOrderNumber(v2_workOrder.getId());
                    }
                }
            }
        }

        if(SimplePartsConstant.PM_ORDER == type){
            //添加保养工单号
            if(StringUtils.isNotEmpty(woId)){
                V2_PmOrder v2_pmOrder = v2PmOrderRepository.findById(woId);
                if(v2_pmOrder != null){
                    partsRequest.setMaintenanceWorkOrderNumber(v2_pmOrder.getPmNum());
                }else {
                    partsRequest.setMaintenanceWorkOrderNumber(woId);
                }
            }
        }

        BigDecimal allTotalPrice = BigDecimal.ZERO;//所有备件总价
        Integer requestQuantityTotal = 0;//总数量
        for (V2_WorkOrder_Detail item  : accessoryList) {
            Integer apartsQuantity = item.getApartsQuantity();
            requestQuantityTotal = requestQuantityTotal + apartsQuantity;
            BigDecimal oneTotalPrice = new BigDecimal(item.getPartsPrice()).multiply(new BigDecimal(apartsQuantity));
            allTotalPrice = allTotalPrice.add(oneTotalPrice);
        }
        partsRequest.setTotalPrice(allTotalPrice);
        partsRequest.setRequestQty(requestQuantityTotal);
        partsRequest.setCreatedDate(TimeUtils.localDateTimeConverterToDate(LocalDateTime.now()));
        if(userAccount == null){
            userAccount = UserContextService.getCurrentUserAccount();
        }
        partsRequest.setCreatedBy(userAccount.getLoginName());
        partsRequest.setProposerId(userAccount.getId());
        partsRequest.setTenantUID(userAccount.getTenantUID());
        partsRequest.setInstitutionUID(userAccount.getInstitutionUID());
        partsRequest.setHospitalUID(userAccount.getHospitalUID());
        partsRequest.setSiteUID(userAccount.getSiteUID());
        return true;
    }

    /**
     * 申请单详情,赋值 , 当不为多级审核调用时 firstMultiLevelApproval = null
     * @param item
     * @param partsRequest
     * @param partsRequestLineItem
     * @param currentUserAccount
     * @param firstMultiLevelApproval
     */
    private void assignmentPartsRequestLineItem(V2_WorkOrder_Detail item, SimplePartsRequest partsRequest, SimplePartsRequestLineItem partsRequestLineItem, UserAccount currentUserAccount){
        String partsitemid=null;
        if (item.getPartsItemId()==null){
            partsitemid="";
        }
        else {
            partsitemid=item.getPartsItemId();
        }
        SimplePartsItem foundPartsItem = partsItemRepository.findById(partsitemid);
        if(foundPartsItem == null){
            log.error("备件申请单详情未查询到备件信息. PartsItemId : "+item.getPartsItemId());
            throw new RuntimeException("备件申请单详情未查询到备件信息. PartsItemId : "+item.getPartsItemId());
        }
        partsRequestLineItem.setPartsItemId(foundPartsItem.getId());
        partsRequestLineItem.setPartsSupplier(foundPartsItem.getSupplier());
        partsRequestLineItem.setName(foundPartsItem.getName());
        partsRequestLineItem.setPartsModel(foundPartsItem.getModel());
        partsRequestLineItem.setPartsBrand(foundPartsItem.getBrand());
        partsRequestLineItem.setUnitPrice(new BigDecimal(item.getPartsPrice()));
        partsRequestLineItem.setPartsRequestId(partsRequest.getId());
        partsRequestLineItem.setRequestCode(partsRequest.getRequestCode());
        partsRequestLineItem.setProposerDate(TimeUtils.localDateTimeConverterToDate(LocalDateTime.now()));
        partsRequestLineItem.setTransactionQty(0);
        partsRequestLineItem.setReturnQty(0);
        partsRequestLineItem.setCreatedDate(TimeUtils.localDateTimeConverterToDate(LocalDateTime.now()));
        partsRequestLineItem.setCreatedBy(currentUserAccount.getLoginName());
        partsRequestLineItem.setTenantUID(currentUserAccount.getTenantUID());
        partsRequestLineItem.setInstitutionUID(currentUserAccount.getInstitutionUID());
        partsRequestLineItem.setHospitalUID(currentUserAccount.getHospitalUID());
        partsRequestLineItem.setSiteUID(currentUserAccount.getSiteUID());
    }

    @Transactional(readOnly = true)
    public Optional<SimplePartsWarehouse> getPartsWarehouseByPmOrderId(String V2_PmOrderId){

        V2_PmOrder v2_pmOrder = v2PmOrderRepository.findById(V2_PmOrderId);
        if (null == v2_pmOrder) {
            return Optional.empty();
        }

        AssetInfo assetInfo = assetDao.findById(v2_pmOrder.getAssetId());
        if (null == assetInfo) {
            return Optional.empty();
        }
        boolean enableWarehouseSelectionInRequest = partsConfigService.isEnableWarehouseSelectionInRequest();
        if(enableWarehouseSelectionInRequest){
            Optional<SimplePartsWarehouse> partsWarehouseOptional = partsWarehouseService.findDefaultWarehouseByAssetInfoForCurrentUser(assetInfo);
            return partsWarehouseOptional;
        }
        return Optional.empty();
    }
    @Transactional(readOnly = true)
    public Optional<SimplePartsWarehouse> getPartsWarehouseByWorkOrderId(String workOrderId){

        V2_WorkOrder v2_workOrder = woDao.findById(workOrderId);
        if (null == v2_workOrder) {
            return Optional.empty();
        }

        AssetInfo assetInfo = assetDao.findById(v2_workOrder.getAssetId());
        if (null == assetInfo) {
            return Optional.empty();
        }
        boolean enableWarehouseSelectionInRequest = partsConfigService.isEnableWarehouseSelectionInRequest();
        if(enableWarehouseSelectionInRequest){
            Optional<SimplePartsWarehouse> partsWarehouseOptional = partsWarehouseService.findDefaultWarehouseByAssetInfoForCurrentUser(assetInfo);
            return partsWarehouseOptional;
        }
        return Optional.empty();
    }

    private boolean isWorkOrderValidPartsRequest(List<V2_WorkOrder_Detail> accessoryList) {

        //check whether work order is valid parts request
        boolean isPartsRequestValid = true;

        for (int i = 0; i <accessoryList.size() ; i++) {
            V2_WorkOrder_Detail item = accessoryList.get(i);
            SimplePartsItem partsItem = partsItemRepository.findById(item.getPartsItemId());
            if (partsItem == null) {
                isPartsRequestValid = false;
                break;
            }
        }

        return isPartsRequestValid;
    }

    public boolean isCountyArea(){
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        return userContextService.isCountyArea();
    }
}
