package com.ge.apm.service.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTypeGroupMapper;
import com.ge.apm.dao.simpleparts.SimplePartsRequestRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.PurchaseOrderInfo;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.AssetInfoToBeTerminatedService;
import com.ge.apm.service.asset.AssetLifecyleService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.budget.PurchaseBudgetConfigService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.WechatSendMsgService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.constant.PmConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.service.simpleparts.SimplePartsConfigService;
import com.ge.apm.service.simpleparts.SimplePartsRequestService;
import com.ge.apm.service.simpleparts.SimplePartsRequestWorkOrderService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.budget.BudgetConstant;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.core.message.MSGEvent;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class MultiLevelApprovalService extends MicroServiceInvoker {

    protected static final Logger logger = Logger.getLogger(MultiLevelApprovalService.class);
    @Autowired
    private PurchaseBudgetConfigService purchaseBudgetConfigService;
    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private WechatSendMsgService wechatSendMsgService;
    @Autowired
    private SimplePartsRequestService partsRequestService;
    @Autowired
    private SimplePartsRequestRepository partsRequestRepository;
    @Autowired
    private MultiLevelApprovalConfigRepository multiApprovalConfigDao;
    @Autowired
    private V2_WorkOrderRepository v2_WorkOrderRepository;
    @Autowired
    private V2_PmOrderRepository v2_PmOrderRepository;
    @Autowired
    private BudgetApplicationRepository budgetApplicationRepository;
    @Autowired
    private PurchaseOrderInfoRepository purchaseOrderInfoRepository;
    @Autowired
    private V2_WorkOrderDetailRepository v2_WorkOrderDetailRepository;
    @Autowired
    private SimplePartsRequestWorkOrderService simplePartsRequestWorkOrderService;
    @Autowired
    private V2_PmOrderService pmOrderService;
    @Autowired
    private SimplePartsConfigService partsConfigService;
    @Autowired
    private BudgetApplicationAssetRepository budgetApplicationAssetRepository;
    @Autowired
    private AssetScrapRepository assetScrapDao;

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private UserRoleRepository userRoleDao;

    @Autowired
    private SysRoleRepository roleDao;

    @Autowired
    private UserOrgInfoRepository userOrgDao;
    @Autowired
    private AssetDepartmentConvertRepository assetDepartmentConvertRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private AssetInfoToBeTerminatedService assetInfoToBeTerminatedService;

    @Autowired
    private WxMessageService wxMessageService;

    @Autowired
    private AssetTypeGroupMapper assetTypeGroupMapper;
    @Autowired
    private AssetTypeGroupRepository assetTypeGroupRepository;
    @Autowired
    private BlobFileService blobService;
    @Autowired
    private SharedAssetRequestRepository sharedAssetRequestRepository;
    @Autowired
    private SharedAssetService sharedAssetService;
    public MultiLevelApprovalConfig getFirstApprovalList(ApprovableTarget approvableTarget, boolean approvalFlag) {
        // 需要调用服务端提供的接口
        logger.info("approvableTarget" + approvableTarget);
        BigDecimal totalPrice = approvableTarget.getTotalPriceToBigDecimal();
        MultiLevelApprovalConfig multiLevelApprovalConfig =
                this.getApproveConfig(switchBusyTypeToString(approvableTarget.getBusinessType()), totalPrice, approvalFlag,approvableTarget);
        return multiLevelApprovalConfig;
    }

    /**
     * 转换BusinessType
     *
     * @return
     */
    String switchBusyTypeToString(int type) {
        switch (type) {
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT:
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT:
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT:
                return ApprovalConstant.APPROVAL_PARTS_STOCK;
            case ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT:
                return ApprovalConstant.APPROVAL_ASSET_INFO_MODIFY;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT:
                return ApprovalConstant.APPROVAL_ASSET_OVERHAUL;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW:
                return ApprovalConstant.APPROVAL_ASSET_BORROW;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                return ApprovalConstant.APPROVAL_ASSET_REPAIR;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                return ApprovalConstant.APPROVAL_ASSET_MAINTAIN;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_BORROW_RETURN;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE_RETURN;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER:
                return ApprovalConstant.APPROVAL_ASSET_TRANSFER;
            case ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS:
                return ApprovalConstant.APPROVAL_ADVERSE_EVENTS;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                return ApprovalConstant.APPROVAL_BUDGET_MANAGEMENT;
            case ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT:
                return ApprovalConstant.APPROVAL_PURCHASE_MANAGEMENT;
            case ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT:
                return ApprovalConstant.APPROVAL_ASSET_EQUIPMENT_ADJUSTMENT;
            default:
                return null;
        }
    }

    /**
     * 获取指定审核人列表
     * @return
     */
    public MultiLevelApprovalConfig getApproveWorkflowConfig(String approvalUid) {
        MultiLevelApprovalConfig approveWorkflowConfig = multiApprovalConfigDao.findById(approvalUid);
        return approveWorkflowConfig;
    }

    /**
     * 保存并提交到审批人
     */
    @Transactional(rollbackFor = Exception.class)
    public MultiLevelApproval getMultiLevelApprovalPersonAndSubmit(ApprovableTarget approvableTarget, Integer level, boolean isApproval) {
        if (level == null) {
            level = 1;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget, false);
        if (multiLevelApprovalConfig != null) {
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                MultiLevelApproval multiLevelApproval = new MultiLevelApproval();
                List<String> sendUserUids = ListUtil.newArrayList();
                List<com.ge.apm.domain.ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<com.ge.apm.domain.ApprovalNode>>() {
                });
                if (approvalNodes != null && !approvalNodes.isEmpty()) {
                    com.ge.apm.domain.ApprovalNode approvalNode = approvalNodes.get(0);
                    if (approvalNode != null) {
                        boolean usingRole = approvalNode.getUsingRole();
                        if (usingRole) {//角色审批
                            List<Integer> nodeRoleUids = approvalNode.getRoleUids();
                            if (nodeRoleUids != null) {
                                List<UserAccount> userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                List<String> uids = userAccounts.stream().filter(item -> StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
                                sendUserUids.addAll(uids);
                                //保存角色到审批流中
                                multiLevelApproval.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                Object[] objects = nodeRoleUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                multiLevelApproval.setApprovalUid(str);
                            }
                        } else {//用户审批
                            List<String> userUids = approvalNode.getUserUids();
                            if(CollectionUtils.isEmpty(userUids)){
                                userUids=new ArrayList<>();
                            }
                            List<String> uids=  getRoleApproveCondition(approvalNode,approvableTarget.getBusinessUid(),approvableTarget.getBusinessType());
                            if(CollectionUtils.isNotEmpty(uids)){
                                userUids.addAll(uids);
                                userUids = userUids.stream().distinct().collect(Collectors.toList());
                            }
                             List<String> strings = generationAdjustment(approvalNodes, approvalNode,approvableTarget.getBusinessUid(),approvableTarget.getBusinessType());
                            if(CollectionUtils.isNotEmpty(strings)){
                                userUids = strings;
                            }
                            sendUserUids.addAll(userUids);
                            multiLevelApproval.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                            Object[] objects = userUids.toArray();
                            String str = StringUtils.join(objects, ",");
                            multiLevelApproval.setApprovalUid(str);
                        }

                        multiLevelApproval.setBusinessNumber(approvableTarget.getBusinessNumber());
                        multiLevelApproval.setBusinessType(approvableTarget.getBusinessType());
                        multiLevelApproval.setBusinessUid(approvableTarget.getBusinessUid());
                        multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                        multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                        multiLevelApproval.setApprovalLevel(level);
                        multiLevelApproval.setApprovalNumber(autoGenerateFinacingNum());
                        multiLevelApproval.setApprovalIng(false);
                        multiLevelApproval.setApprovalLevelNow(level);
                        multiLevelApproval.setWorkflowConfigApproveUid(multiLevelApprovalConfig.getId());
                        multiLevelApproval.setInitiateApproverUid(userAccount.getUid());
                        multiLevelApproval.setCreatedDate(TimeUtils.localDateTimeConverterToDate(LocalDateTime.now()));
                        multiLevelApproval.setCreatedBy(userAccount.getName());
                        multiLevelApproval.setTenantUID(userAccount.getTenantUID());
                        multiLevelApproval.setHierOrgUid(userAccount.getSiteUID());
                        multiLevelApproval.setDigest(approvableTarget.getBusinessDigest());
                        MultiLevelApproval multiLevel = multiLevelApprovalRepository.save(multiLevelApproval);
                        if (multiLevel != null) {
                            // 发送短信
                            String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
                            String linkUrl="/wx/approvals/" + multiLevel.getId()+"?showType=0";
                            this.sendWxMsg(sendUserUids,message,"待审批",linkUrl,MSGEvent.MULTI_LEVEL_APPROVAL_TO_BE_APPROVED,multiLevelApproval);
                        }
                        if(multiLevelApproval.getBusinessType()==ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT){
                            mqUtils.sendApprovalMessage(userAccount,multiLevelApproval.getBusinessType(),multiLevelApproval.getBusinessUid(),multiLevelApproval.getApprovalStatus());
                        }
                        return multiLevel;
                    }
                }
            }
        }
        return null;
    }

    private List<String> generationAdjustment(List<ApprovalNode> approvalNodes,ApprovalNode approvalNode,String businessUid,int businessType) {
        List<String> uids = new ArrayList<>();
        if(!Arrays.asList(ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT,ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP).contains(businessType)) return uids;
        if(StringUtils.isNotEmpty(approvalNode.getApprovalStrategy())){
            List<String> orgUidByRoleId =getUserListByBusinessType(approvalNode,businessUid,businessType);
            if(CollectionUtils.isNotEmpty(orgUidByRoleId)){
                uids.addAll(orgUidByRoleId);
            }else{
                int nextIndex = approvalNodes.indexOf(approvalNode) + 1;
                if(nextIndex < approvalNodes.size()){
                    ApprovalNode nextApprovalNode = approvalNodes.get(nextIndex);
                   return generationAdjustment(approvalNodes,nextApprovalNode,businessUid,businessType);
                }
            }
        }else{
            uids.addAll(approvalNode.getUserUids());
        }
        return uids;
    }
    private List<String> getUserListByBusinessType(ApprovalNode approvalNode,String businessUid,int businessType){
        List<String> uids = new ArrayList<>();
        String clinicalDeptUid = null;
        switch (businessType){
            case ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT:
                SharedAssetRequest request = sharedAssetRequestRepository.findById(businessUid);
                clinicalDeptUid = request.getClinicalDeptUid();
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                AssetScrap assetScrap = assetScrapDao.findById(businessUid);
                clinicalDeptUid = assetScrap.getClinicalDeptUid();
                break;
            default:
                return uids;
        }
        int roleId = roleDao.getIdByName(ApmSystemRole.DeptHead.name());
        uids = userRoleDao.findOrgUidByRoleId(clinicalDeptUid, roleId);
        return uids;
    }

    private List<String> getRoleApproveCondition(ApprovalNode approvalNode,String businessUid,int businessType) {
        RoleApproveCondition roleApproveCondition = approvalNode.getRoleApproveCondition();
        List<String> uids=null;
        if(roleApproveCondition!=null && roleApproveCondition.getCondition()!=null && org.apache.commons.lang3.StringUtils.isNotBlank(roleApproveCondition.getApproveRole())){
            String clinicalDeptUID=null;
            switch (businessType) {
                case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                    if(roleApproveCondition.getCondition()==1) {
                        V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(businessUid);
                        if(v2WorkOrder==null){
                            return Collections.emptyList();
                        }
                        clinicalDeptUID=v2WorkOrder.getAssetInfo().getClinicalDeptUID();
                    }
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(clinicalDeptUID)) {
                        int roleId = roleDao.getIdByName(roleApproveCondition.getApproveRole());
                        uids = userRoleDao.findOrgUidByRoleId(clinicalDeptUID, roleId);
                    }
                    break;
                default:
                    break;
            }

        }
        return uids;
    }

    /**
     * 供备件审批使用--审批不同意
     *
     * @param multiLevelApproval
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalPersonAndSubmitByDisagree(MultiLevelApproval multiLevelApproval, String approvalOpinion) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
        multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
        multiLevelApproval.setActualApproverName(userAccount.getName());
        multiLevelApproval.setActualApproverDate(new Date());
        multiLevelApproval.setActualApproverUid(userAccount.getUid());
        multiLevelApproval.setApprovalOpinion(approvalOpinion);
        multiLevelApproval.setApprovalIng(true);
        //更新审批数据
        multiLevelApprovalRepository.save(multiLevelApproval);
        saveBlobFile(multiLevelApproval);
        //更新审批状态为不通过 业务状态为审批不通过
        List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
        //发起人UID
        String initiateApproverUid = null;
        String businessType = this.getBusinessType(multiLevelApproval.getBusinessType());
        String businessNumber = multiLevelApproval.getBusinessNumber();
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                MultiLevelApproval multi = list.get(i);
                if (null != multi.getInitiateApproverUid() && !"".equals(multi.getInitiateApproverUid())) {
                    initiateApproverUid = multi.getInitiateApproverUid();
                }
                multi.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_DIS_AGREE);
                multi.setApprovalLevelNow(ApprovalConstant.APPROVAL_NOW_DISAGREE);
                multiLevelApprovalRepository.save(multi);
            }
        }
        switch (multiLevelApproval.getBusinessType()) {
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                v2WorkOrder.setApprovalStatus(WorkOrderConstant.APPROVAL_STATUS_DISAGREE);//审批不同意
                v2_WorkOrderRepository.save(v2WorkOrder);
                this.doWorkDetailByIsAgreeType(v2WorkOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_DISAGREE, null, null);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                v2PmOrder.setApprovalStatus(PmConstant.PM_APPROVAL_STATUS_DISAGREE);
                v2_PmOrderRepository.save(v2PmOrder);
                this.doWorkDetailByIsAgreeType(v2PmOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_DISAGREE, null, null);
                break;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                // 预算管理 审批不通过
                updateBudget(multiLevelApproval, BudgetConstant.BUDGET_APPLICATION_STATUS_NOT_APPROVED,BudgetApplication.APPLICATIONSTEP.ToSubmitted.toString());
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                //
                updateAssetScrap(multiLevelApproval, ApprovalConstant.SCRAP_STATUS_REVIEW_REFUSE, userAccount, approvalOpinion);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                AssetDepartmentConvert byId = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
                byId.setCurrentStatus(3);
                byId.setLastModifiedDate(new Date());
                byId.setLastModifiedBy(userAccount.getLoginName());
                byId.setApprovalReason(approvalOpinion);
                assetDepartmentConvertRepository.save(byId);
                break;
             case ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT:
                SharedAssetRequest sharedAssetRequest = sharedAssetRequestRepository.findById(multiLevelApproval.getBusinessUid());
                 sharedAssetRequest.setApprovalStatus(2);
                 sharedAssetRequest.setState(0);
                 sharedAssetRequestRepository.save(sharedAssetRequest);
                 sharedAssetService.pushWxMsg(sharedAssetRequest.getId());
                 break;
            default:
                throw new RuntimeException("New Business type not handled yet");
        }

        //审批不同意时 给发起人发送微信
        UserAccount user = userAccountRepository.getByUid(initiateApproverUid);
        List<String> sendUserUids = new ArrayList<>(1);
        if(user!=null && StringUtils.isNotBlank(user.getUid())){
            sendUserUids.add(user.getUid());
        }
        ArrayList<UserAccount> userAccounts = new ArrayList<>();
        userAccounts.add(user);
        switch (multiLevelApproval.getBusinessType()) {
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                AssetScrap assetScrap = assetScrapDao.findById(multiLevelApproval.getBusinessUid());
                AssetInfo assetInfo = assetInfoDao.findByUid(assetScrap.getAssetUid());
                if(!assetScrap.getRequestUserUid().equals(assetScrap.getApprovalUid())){
                    UserAccount applyUser = userAccountRepository.getByUid(assetScrap.getRequestUserUid());
                    userAccounts.add(applyUser);
                }
                wxMessageService.sendMessage(assetInfo,"审批失败",assetScrap,userAccounts,"",userAccount,assetScrap.getApplyReason(),MSGEvent.ASSET_SCRAP_MULTI_LEVEL_APPROVAL_REFUSED);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                AssetDepartmentConvert assetDepartmentConvert = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
                assetInfo = assetInfoDao.findByUid(assetDepartmentConvert.getAssetUid());
                UserAccount applyUser = userAccountRepository.getByUid(assetDepartmentConvert.getTransferOutUid());
                userAccounts.add(applyUser);
                wxMessageService.sendMessage(assetInfo,"审批失败",assetDepartmentConvert,userAccounts,"",userAccount,assetDepartmentConvert.getApplyReason(),MSGEvent.ASSET_DEPARTMENT_CONVERT_MULTI_LEVEL_APPROVAL_REFUSED);
                break;
            default:
                //审批同意时 给审批人发送微信
                String message = ApprovalConstant.WX_MESSAGE_DISAGREE_1+businessType+ApprovalConstant.WX_MESSAGE_DISAGREE_2+businessNumber+ApprovalConstant.WX_MESSAGE_DISAGREE_3;
                this.sendWxMsg(sendUserUids,message,"审批失败","",MSGEvent.MULTI_LEVEL_APPROVAL_REFUSED,multiLevelApproval);
                break;
        }


    }

    public void doWorkDetailByIsAgreeType(String id, int isAgreeType, List<V2_WorkOrder_Detail> accessorySparePartsBuyList, List<V2_WorkOrder_Detail> accessorySparePartsRequestList) {
        List<V2_WorkOrder_Detail> approvedDetailsNew = getWorkOrderDetail(id);
        if (approvedDetailsNew.isEmpty()) {
            return;
        }
        if (isAgreeType == ApprovalConstant.WORK_DETAIL_STATUS_AGREE) {
            //最后一个审批通过的时候 生成相应单子
            for (int i = 0; i < approvedDetailsNew.size(); i++) {
                V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                if (v2_workOrder_detail.getApproved() != null && v2_workOrder_detail.getApproved()) {
                    continue;
                }
                v2_workOrder_detail.setApproved(Boolean.TRUE);
                v2_WorkOrderDetailRepository.save(v2_workOrder_detail);
                if (ApmConstant.TREASURY_USE.equals(v2_workOrder_detail.getPartsType())) {//库房领用
                    accessorySparePartsRequestList.add(v2_workOrder_detail);
                } else if (ApmConstant.MEDICAL_WORKERS_PURCHASING.equals(v2_workOrder_detail.getPartsType())) {//医工采购
                    accessorySparePartsBuyList.add(v2_workOrder_detail);
                }
            }
        } else {
            for (int i = 0; i < approvedDetailsNew.size(); i++) {
                V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                if (v2_workOrder_detail.getApproved() != null && v2_workOrder_detail.getApproved()) {
                    continue;
                }
                v2_workOrder_detail.setApproved(Boolean.FALSE);
                v2_WorkOrderDetailRepository.save(v2_workOrder_detail);
            }
        }
    }

    /**
     * @param busiType
     * @param amount
     * @return MultiLevelApprovalConfig
     * @date 2020/7/30 16:00
     * 根据金额查询审批配置信息
     *
     */
    public MultiLevelApprovalConfig getApproveConfig(String busiType, BigDecimal amount, boolean approvalFlag,ApprovableTarget approvableTarget) {
        logger.info("-----校验开启多级审核请求参数 busiType : " + " , amount : " + amount);
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = new ArrayList<>(Arrays.asList(user.getSiteUID()));
        for (String orgUid : orgUidList) {
            MultiLevelApprovalConfig approveConfig = null;
            if (approvalFlag) {
                approveConfig = getApproveConfigByOrgUid(orgUid, busiType, amount,approvableTarget);
            } else {
                approveConfig = getApproveConfigByOrgUidApprovalFlag(orgUid, busiType, amount,approvableTarget);
            }
            if (null != approveConfig) {
                return approveConfig;
            }
        }
        return null;
    }

    MultiLevelApprovalConfig getApproveConfigByOrgUid(String orgUid, String busiType, BigDecimal amount,ApprovableTarget approvableTarget) {
        if (StringUtils.isEmpty(busiType)) {
            return null;
        }
        List<MultiLevelApprovalConfig> configList = multiApprovalConfigDao.getConfigByCondition(orgUid, busiType);
        UserAccount user = UserContextService.getCurrentUserAccount();

        boolean enableAssetType= user.isEnableAssetTypeGroup();

        if(approvableTarget.getApprovalAssetInfo()!=null){
            configList = filterConfigs(configList, approvableTarget, enableAssetType);
        }
        if (null != configList && configList.size() > 0) {
            logger.info("多级审核  configList : " + configList.toString());
            if (ApprovalConstant.APPROVAL_PARTS_STOCK.equals(busiType) || ApprovalConstant.APPROVAL_BUDGET_MANAGEMENT.equals(busiType)) {
                return getPartsSteam(configList, amount);
            } else {
                return configList.get(0);
            }
        }
        return null;
    }

    MultiLevelApprovalConfig getApproveConfigByOrgUidApprovalFlag(String orgUid, String busiType, BigDecimal amount,ApprovableTarget approvableTarget) {
        if (StringUtils.isEmpty(busiType)) {
            return null;
        }
        List<MultiLevelApprovalConfig> configList = multiApprovalConfigDao.getConfigByCondition(orgUid, busiType);
        UserAccount user = UserContextService.getCurrentUserAccount();
        boolean enableAssetType= user.isEnableAssetTypeGroup();
        if(approvableTarget.getApprovalAssetInfo()!=null){
            configList = filterConfigs(configList, approvableTarget, enableAssetType);
        }
        if (null != configList && configList.size() > 0) {
            if (ApprovalConstant.APPROVAL_PARTS_STOCK.equals(busiType) || ApprovalConstant.APPROVAL_ASSET_REPAIR.equals(busiType)
                    || ApprovalConstant.APPROVAL_ASSET_MAINTAIN.equals(busiType) ) {
                return getPartsSteam(configList, amount);
            } else {
                return configList.get(0);
            }
        }
        return null;
    }
    public List<MultiLevelApprovalConfig> filterConfigs(List<MultiLevelApprovalConfig> configList, ApprovableTarget approvableTarget, boolean enableAssetType) {
        List<MultiLevelApprovalConfig> collect = configList.stream()
                .filter(config -> config.getConfigration() != null)
                .map(config -> {
                    Map<String, Object> map = JSON.parseObject(config.getConfigration(), Map.class);
                    List<String> assetType = (List<String>) map.get("selectAssetTypes");
                    List<String> selectAssetGroups = (List<String>) map.get("selectAssetGroups");
                    List<String> selectAssetClinicalDeptUids = (List<String>) map.get("selectAssetClinicalDeptUids");
                    if(CollectionUtils.isNotEmpty(assetType)){
                        List<AssetTypeGroup> assetTypeByUids = assetTypeGroupMapper.getAssetTypeByUids(assetType);
                        assetType=assetTypeByUids.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
                    }
                    int matchCount = getMatchCount(approvableTarget, assetType, selectAssetGroups, selectAssetClinicalDeptUids, enableAssetType);

                    // 返回包含原始对象和匹配数量的SimpleEntry
                    return new AbstractMap.SimpleEntry<>(config, matchCount);
                })
                .filter(entry -> entry.getValue()!=  0) // 过滤掉不满足条件的项
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) // 按照匹配数量从大到小排序（升序比较时交换两个参数的位置）
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return collect;
    }

    private int getMatchCount(ApprovableTarget approvableTarget, List<String> assetType,
                              List<String> selectAssetGroups, List<String> selectAssetClinicalDeptUids, boolean enableAssetType) {
        // 如果至少有一个集合非空并且不包含对应的值，则返回0，表示不匹配
        if ((CollectionUtils.isNotEmpty(selectAssetClinicalDeptUids) && !selectAssetClinicalDeptUids.contains(approvableTarget.getApprovalAssetInfo().getClinicalDeptUID()))
                || (CollectionUtils.isNotEmpty(selectAssetGroups) && !selectAssetGroups.contains(String.valueOf(approvableTarget.getApprovalAssetInfo().getAssetGroup())))
                || (enableAssetType && CollectionUtils.isNotEmpty(assetType) && !assetType.contains(approvableTarget.getApprovalAssetInfo().getAssetType()))) {
            return 0;
        }

        // 否则计算匹配的数量
        int count = 0;
        if (enableAssetType) {
            count += CollectionUtils.isNotEmpty(selectAssetClinicalDeptUids) && selectAssetClinicalDeptUids.contains(approvableTarget.getApprovalAssetInfo().getClinicalDeptUID()) ? 1 : 0;
            count += CollectionUtils.isNotEmpty(selectAssetGroups) && selectAssetGroups.contains(String.valueOf(approvableTarget.getApprovalAssetInfo().getAssetGroup())) ? 1 : 0;
            count += CollectionUtils.isNotEmpty(assetType) && assetType.contains(approvableTarget.getApprovalAssetInfo().getAssetType()) ? 1 : 0;
        } else {
            count += CollectionUtils.isNotEmpty(selectAssetClinicalDeptUids) && selectAssetClinicalDeptUids.contains(approvableTarget.getApprovalAssetInfo().getClinicalDeptUID()) ? 1 : 0;
            count += CollectionUtils.isNotEmpty(selectAssetGroups) && selectAssetGroups.contains(String.valueOf(approvableTarget.getApprovalAssetInfo().getAssetGroup())) ? 1 : 0;
        }
        return count;
    }

    //获取备件库存审批流
    MultiLevelApprovalConfig getPartsSteam(List<MultiLevelApprovalConfig> configList, BigDecimal amount) {
        //先遍历所有 有配置金额信息的
        for (MultiLevelApprovalConfig config : configList) {
            if (null != config.getConfigration()) {
                Map configMap = JSON.parseObject(config.getConfigration(), Map.class);
                Integer minAmount = ((Integer) configMap.get("minAmount"));
                Integer maxAmount = ((Integer) configMap.get("maxAmount"));
                if (minAmount < amount.doubleValue() && amount.doubleValue() <= maxAmount) {
                    return config;
                }
                if (minAmount > 0 && maxAmount == 0) {
                    if (minAmount < amount.doubleValue()) {
                        return config;
                    }
                }
            }
        }
        //若审批流配置金额条件都不满足，则取无金额配置信息的
        for (MultiLevelApprovalConfig config : configList) {
            if (null != config.getConfigration()) {
                Map configMap = JSON.parseObject(config.getConfigration(), Map.class);
                if (0 == (int) configMap.get("minAmount") && 0 == (int) configMap.get("maxAmount")) {
                    return config;
                }
            }
        }
        return null;
    }

    public List<MultiLevelApproval> getByInitiateApproverUid(String userUid) {
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.getByInitiateApproverUid(userUid);
        return multiLevelApprovals;
    }

    public List<MultiLevelApproval> getMultiLevelApprovalList(String actualApproverUid, Integer approvalNodeStatus) {
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.getMultiLevelApprovalList(actualApproverUid, approvalNodeStatus);
        return multiLevelApprovals;
    }

    public List<MultiLevelApproval> getMultiLevelApproval(MultiLevelApproval multiLevelApproval) {
        String approvalNumber = multiLevelApproval.getApprovalNumber();
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.getByApprovalNumberList(approvalNumber);
        if(CollectionUtils.isNotEmpty(multiLevelApprovals)){
            Set<String> collect = multiLevelApprovals.stream().map(MultiLevelApproval::getId).collect(Collectors.toSet());
            List<V2_BlobObject> blobObjectsByBoUids = blobService.findBlobObjectsByBoUids(collect);
            multiLevelApprovals.forEach(item->{
                List<V2_BlobObject> blobList = blobObjectsByBoUids.stream().filter(blob -> blob.getBoUuid().equals(item.getId())).collect(Collectors.toList());
                item.setAttachments(blobList);
            });
        }
        return multiLevelApprovals;
    }

    //生成审批单号
    public String autoGenerateFinacingNum() {
        DateTime now = new DateTime();
        String prefix = ApprovalConstant.APPROVAL_NUM;
        String year = now.toString("yyyyMM");
        int max = 999999;

        String maxFinacingNum = null;//当前最大值的str
        synchronized (this) {
            maxFinacingNum = multiLevelApprovalRepository.selectMaxApprovalNum(prefix + year);
            if (maxFinacingNum == null) {
                return prefix + year + "000001";
            }
            int finacingNum = 0;//当前最大数值
            if (finacingNum > max) {
                return "error:无法生成资产编号，已达到上限。";
            }
            finacingNum = Integer.parseInt(maxFinacingNum.substring(8));
            String num = String.format("%06d", ++finacingNum);
            return prefix + year + num;
        }
    }

    /**
     * 供备件审批使用--审批同意
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalPersonByAgree(MultiLevelApproval multiLevelApprovalNew, String approvalOpinion) throws Exception {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalNew;
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getApproveWorkflowConfig(multiLevelApproval.getWorkflowConfigApproveUid());
        List<UserAccount> userAccounts=new ArrayList<>();

        if (multiLevelApprovalConfig != null) {
            multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
            multiLevelApproval.setActualApproverName(userAccount.getName());
            multiLevelApproval.setActualApproverDate(new Date());
            multiLevelApproval.setActualApproverUid(userAccount.getUid());
            multiLevelApproval.setApprovalOpinion(approvalOpinion);
            multiLevelApproval.setApprovalIng(true);
            int approvalLevel = multiLevelApproval.getApprovalLevel();
            int level = approvalLevel + 1;
            multiLevelApproval.setApprovalLevelNow(level);
            //更新审批数据
            multiLevelApprovalRepository.save(multiLevelApproval);
            saveBlobFile(multiLevelApproval);
            //更新审批进度
            if (level > 2) {
                List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
                if (null != list) {
                    for (int i = 0; i < list.size(); i++) {
                        MultiLevelApproval multi = list.get(i);
                        if (multi != null && multi.getApprovalLevelNow() != level) {
                            multi.setApprovalLevelNow(level);
                            multiLevelApprovalRepository.save(multi);
                        }
                    }
                }
            }
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                List<String> sendUserUids = ListUtil.newArrayList();

                List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {
                });
                if (approvalNodes != null) {
                    MultiLevelApproval date = multiLevelApprovalNew;
                    int maxNodeId = approvalNodes.size();

                    List<MultiLevelApproval> multiLevelApprovalList=null;

                    if (level > maxNodeId) {//最后一个人审批通过 更新审批状态为通过 业务状态为审批通过
                        multiLevelApprovalList = this.getMultiLevelApproval(multiLevelApproval);
                        //发起人UID
                        String initiateApproverUid = null;
                        String businessType = this.getBusinessType(multiLevelApproval.getBusinessType());
                        UserAccount user=null;
                        if (null != multiLevelApprovalList) {

                            for (int i = 0; i < multiLevelApprovalList.size(); i++) {
                                MultiLevelApproval multi = multiLevelApprovalList.get(i);
                                if (null != multi.getInitiateApproverUid() && !"".equals(multi.getInitiateApproverUid())) {
                                    initiateApproverUid = multi.getInitiateApproverUid();
                                }
                                multi.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                                multi.setApprovalLevelNow(ApprovalConstant.APPROVAL_NOW_AGREE);
                                multiLevelApprovalRepository.save(multi);
                            }
                            //微信通知发起人审批通过
                             user = userAccountRepository.getByUid(initiateApproverUid);
                            if(ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getUid())){
                                sendUserUids.add(user.getUid());
                            }
                            String message = ApprovalConstant.WX_MESSAGE_AGREE_1 + businessType + ApprovalConstant.WX_MESSAGE_AGREE_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_AGREE_3;
                            if(multiLevelApproval.getBusinessType()!=ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP && multiLevelApproval.getBusinessType()!=ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT) {
                                this.sendWxMsg(sendUserUids, message,"审批通过","",MSGEvent.MULTI_LEVEL_APPROVAL_PASS,multiLevelApproval);
                            }
                        }
                        switch (multiLevelApproval.getBusinessType()) {
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT://维修
                                V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                                v2WorkOrder.setApprovalStatus(WorkOrderConstant.APPROVAL_STATUS_AGREE);//审批同意
                                v2_WorkOrderRepository.save(v2WorkOrder);
                                List<V2_WorkOrder_Detail> accessorySparePartsBuyList = new ArrayList<>();//申购
                                List<V2_WorkOrder_Detail> accessorySparePartsRequestList = new ArrayList<>();//申领
                                this.doWorkDetailByIsAgreeType(v2WorkOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_AGREE, accessorySparePartsBuyList, accessorySparePartsRequestList);
                                //审批校验,生成申领申购单
                                if (partsConfigService.isEnableIntegrationWithWorkOrder()) {
                                    generateSparePartsBuyList(SimplePartsConstant.WORK_ORDER, accessorySparePartsBuyList, accessorySparePartsRequestList, userAccountRepository.findById(v2WorkOrder.getCurrentPersonId()));
                                }

//                                paramList.clear();
                                //推送消息到临床(该工单的报修人)
//                                String initiateApproverUid1 = multiLevelApproval.getInitiateApproverUid();
//                                Integer requestId = v2WorkOrder.getServiceRequest().getRequestorId();
//                                UserAccount requestUser = userAccountRepository.getByUid(initiateApproverUid1);
//                                paramList.add(new WechatParam(user.getWeChatId(), user.getAppAlias()));
                                //设备科长(可见范围内包含该设备的所有医学工程处主任)
//                                int roleId = roleDao.getIdByName("AssetHead");
                                String clinicalDeptUID = v2WorkOrder.getAssetInfo().getClinicalDeptUID();
                                userAccounts=userAccountRepository.findUserByClinicalDeptUID(clinicalDeptUID,"AssetHead");
                                userAccounts.add(user);
                                sendUserUids=userAccounts.stream().distinct().filter(item->StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
                                String message = ApprovalConstant.WX_MESSAGE_AGREE_1 + businessType + ApprovalConstant.WX_MESSAGE_AGREE_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_AGREE_3;
                                this.sendWxMsg(sendUserUids, message,"审批通过","",MSGEvent.MULTI_LEVEL_APPROVAL_PASS,multiLevelApproval);
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT://保养
                                V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                                v2PmOrder.setApprovalStatus(PmConstant.PM_APPROVAL_STATUS_AGREE);
                                v2_PmOrderRepository.save(v2PmOrder);
                                List<V2_WorkOrder_Detail> pmAccessorySparePartsBuyList = new ArrayList<>();//申购
                                List<V2_WorkOrder_Detail> pmAccessorySparePartsRequestList = new ArrayList<>();//申领
                                this.doWorkDetailByIsAgreeType(v2PmOrder.getId(), ApprovalConstant.WORK_DETAIL_STATUS_AGREE, pmAccessorySparePartsBuyList, pmAccessorySparePartsRequestList);
                                //审批校验,生成申领申购单
                                if (partsConfigService.isEnableIntegrationWithWorkOrder()) {
                                    generateSparePartsBuyList(SimplePartsConstant.PM_ORDER, pmAccessorySparePartsBuyList, pmAccessorySparePartsRequestList, userAccountRepository.getByUid(v2PmOrder.getCurrentPersonId()));
                                }
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                                // 预算管理
                                updateBudget(multiLevelApproval, BudgetConstant.BUDGET_APPLICATION_STATUS_APPROVED,BudgetApplication.APPLICATIONSTEP.PendingComplete.toString());
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                                updateAssetScrap(multiLevelApproval, ApprovalConstant.SCRAP_STATUS_SUCCESS, userAccount, approvalOpinion);
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                                AssetDepartmentConvert byId = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
                                byId.setCurrentStatus(3);
                                byId.setLastModifiedBy(userAccount.getName());
                                byId.setLastModifiedDate(new Date());
                                byId.setTransferInName(byId.getApprovalBy());
                                byId.setTransferInAt(new Date());
                                byId.setTransferInUid(byId.getApprovalUid());
                                assetDepartmentConvertRepository.save(byId);
                                AssetInfo assetInfoByUid = assetInfoDao.getByUid(byId.getAssetUid());
                                OrgInfo orgInfo = orgInfoRepository.getByUid(byId.getInDeptUid());
                                assetInfoByUid.setClinicalDeptUID(byId.getInDeptUid());
                                assetInfoByUid.setClinicalDeptId(orgInfo.getId());
                                assetInfoByUid.setClinicalDeptName(byId.getInDeptName());
                                assetInfoByUid.setLastModifiedDate(new Date());
                                assetInfoByUid.setLastModifiedBy(userAccount.getName());
                                assetInfoDao.save(assetInfoByUid);

                                MqContext mqContext = new MqContext();
                                mqContext.setClassName("MultiLevelApprovalService");
                                mqContext.setServiceName("hc-apm-web");
                                mqContext.setMethodName("getMultiLevelApprovalPersonByAgree");
                                mqContext.setDescribe("资产转科审核同意");
                                UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
                                mqUtils.sendAssetToMQ(assetInfoByUid,currentUserAccount,mqContext);


                                Set<String> approverUids = multiLevelApprovalList.stream().map(MultiLevelApproval::getActualApproverUid).distinct().collect(Collectors.toSet());
                                approverUids.add(byId.getTransferOutUid());
                                List<UserAccount> userAccounts1=userAccountRepository.getByUids(approverUids);
                                int roleId = roleDao.getIdByName("DeptHead");
                                List<UserAccount> inUsers = userAccountRepository.findOrgUidByRoleId(byId.getInDeptUid(), roleId);
                                List<UserAccount> outUsers = userAccountRepository.findOrgUidByRoleId(byId.getOutDeptUid(), roleId);
                                if(CollectionUtils.isNotEmpty(inUsers)){
                                    userAccounts1.addAll(inUsers);
                                }
                                if(CollectionUtils.isNotEmpty(outUsers)){
                                    userAccounts1.addAll(outUsers);
                                }
                                userAccounts1= userAccounts1.stream().distinct().collect(Collectors.toList());
                                wxMessageService.sendMessage(assetInfoByUid,"审批通过",byId,userAccounts1,"",userAccount,byId.getApplyReason(),MSGEvent.ASSET_DEPARTMENT_CONVERT_MULTI_LEVEL_APPROVAL_PASS);
                                break;
                            case ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT:
                                SharedAssetRequest sharedAssetRequest = sharedAssetRequestRepository.findById(multiLevelApproval.getBusinessUid());
                                sharedAssetRequest.setApprovalStatus(1); // 多级审批状态 0 审批中 1 审批通过，2 审批不通过
                                sharedAssetRequestRepository.save(sharedAssetRequest);
                                sharedAssetService.pushWxMsg(sharedAssetRequest.getId());
                                break;
                            default:
                                throw new RuntimeException("New Business type not handled yet");
                        }
                        mqUtils.sendApprovalMessage(userAccount,multiLevelApproval.getBusinessType(),multiLevelApproval.getBusinessUid(),ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                    } else {
                        com.ge.apm.domain.ApprovalNode node = approvalNodes.get(approvalLevel);
                        if (node != null) {
                            boolean usingRole = node.getUsingRole();
                            if (usingRole) {//角色审批，根据角色拉取用户进行审批
                                List<Integer> nodeRoleUids = node.getRoleUids();
                                if (nodeRoleUids != null) {
                                    userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                    List<String> uids = userAccounts.stream().filter(item -> StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
                                    sendUserUids.addAll(uids);

                                    //保存角色到审批流中
                                    date.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                    Object[] objects = nodeRoleUids.toArray();
                                    String str = StringUtils.join(objects, ",");
                                    date.setApprovalUid(str);
                                }
                            } else {//用户审批
                                List<String> userUids = node.getUserUids();
                                if(CollectionUtils.isEmpty(userUids)){
                                    userUids=new ArrayList<>();
                                }
                                List<String> uids=  getRoleApproveCondition(node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                                if(CollectionUtils.isNotEmpty(uids)){
                                    userUids.addAll(uids);
                                    userUids = userUids.stream().distinct().collect(Collectors.toList());
                                }
                                List<String> strings = generationAdjustment(approvalNodes, node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                                if(CollectionUtils.isNotEmpty(strings)){
                                    userUids = strings;
                                }
                                userAccounts = userAccountRepository.findByUidIn(userUids);
                                sendUserUids.addAll(userUids);
                                date.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                                Object[] objects = userUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                date.setApprovalUid(str);
                            }
                            date.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                            date.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                            date.setApprovalLevel(level);
                            date.setCreatedBy(userAccount.getLoginName());
                            date.setApprovalIng(false);
                            date.setApprovalLevelNow(level);
                            date.setInitiateApproverUid(null);
                            date.setCreatedDate(new Date());
                            date.setId(null);
                            date.setActualApproverName(null);
                            date.setActualApproverDate(null);
                            date.setApprovalOpinion(null);
                            //生成下条待审批数据
                            MultiLevelApproval multiLevel = multiLevelApprovalRepository.save(date);
                            String linkUrl="/wx/approvals/" + multiLevel.getId()+"?showType=0";
                            extracted(multiLevelApproval, userAccount, userAccounts, sendUserUids, date, node, linkUrl);

                        }
                    }
                }
            }
        }
    }
    private void saveBlobFile(MultiLevelApproval multiLevelApproval){
        List<V2_BlobObject> blobObjects=null;
        if(multiLevelApproval != null && !CollectionUtils.isEmpty(blobObjects = multiLevelApproval.getAttachments())){
            blobService.saveBlobObject(blobObjects);
        }
    }

    /**
     * 采购使用--审批同意
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalPurchaseByAgree(MultiLevelApproval multiLevelApprovalNew,
                                                     String approvalOpinion,PurchaseOrderInfo purchaseOrderInfo,
                                                     VenderInfo venderConfirm,VenderInfo companyConfirm) throws Exception {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalNew;
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getApproveWorkflowConfig(multiLevelApproval.getWorkflowConfigApproveUid());
        List<UserAccount> userAccounts=new ArrayList<>();

        if (multiLevelApprovalConfig != null) {
            multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
            multiLevelApproval.setActualApproverName(userAccount.getName());
            multiLevelApproval.setActualApproverDate(new Date());
            multiLevelApproval.setActualApproverUid(userAccount.getUid());
            multiLevelApproval.setApprovalOpinion(approvalOpinion);
            multiLevelApproval.setApprovalIng(true);
            int approvalLevel = multiLevelApproval.getApprovalLevel();
            int level = approvalLevel + 1;
            multiLevelApproval.setApprovalLevelNow(level);
            //更新审批数据
            multiLevelApprovalRepository.save(multiLevelApproval);
            //更新审批进度
            if (level > 2) {
                List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
                if (null != list) {
                    for (int i = 0; i < list.size(); i++) {
                        MultiLevelApproval multi = list.get(i);
                        if (multi != null && multi.getApprovalLevelNow() != level) {
                            multi.setApprovalLevelNow(level);
                            multiLevelApprovalRepository.save(multi);
                        }
                    }
                }
            }
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                List<String> sendUserUids = ListUtil.newArrayList();
                List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {
                });
                if (approvalNodes != null) {
                    MultiLevelApproval date = multiLevelApprovalNew;
                    int maxNodeId = approvalNodes.size();

                    List<MultiLevelApproval> multiLevelApprovalList=new ArrayList<>();

                    if (level > maxNodeId) {//最后一个人审批通过 更新审批状态为通过 业务状态为审批通过
                        multiLevelApprovalList = this.getMultiLevelApproval(multiLevelApproval);
                        //发起人UID
                        String initiateApproverUid = null;
                        String businessType = this.getBusinessType(multiLevelApproval.getBusinessType());
                        UserAccount user=null;
                        if (null != multiLevelApprovalList) {

                            for (int i = 0; i < multiLevelApprovalList.size(); i++) {
                                MultiLevelApproval multi = multiLevelApprovalList.get(i);
                                if (null != multi.getInitiateApproverUid() && !"".equals(multi.getInitiateApproverUid())) {
                                    initiateApproverUid = multi.getInitiateApproverUid();
                                }
                                multi.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
                                multi.setApprovalLevelNow(ApprovalConstant.APPROVAL_NOW_AGREE);
                                multiLevelApprovalRepository.save(multi);
                            }
                            //微信通知发起人审批通过
                            user = userAccountRepository.getByUid(initiateApproverUid);
                            if(ObjectUtil.isNotEmpty(user) && !ObjectUtil.isAllEmpty(user.getUid())){
                                sendUserUids.add(user.getUid());
                            }
                            String message = ApprovalConstant.WX_MESSAGE_AGREE_1 + businessType + ApprovalConstant.WX_MESSAGE_AGREE_2 + multiLevelApproval.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_AGREE_3;
                            if(multiLevelApproval.getBusinessType()!=ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP && multiLevelApproval.getBusinessType()!=ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT) {
                                this.sendWxMsg(sendUserUids, message,"审批通过","",MSGEvent.MULTI_LEVEL_APPROVAL_PASS,multiLevelApproval);
                            }
                            try {
                                purchaseBudgetConfigService.sendWxMsgPurchase(purchaseOrderInfo, UserContextService.getAccessToken(),"RESULT");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        switch (multiLevelApproval.getBusinessType()) {

                            case ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT:
                                // 采购管理管理
                                updatePurchase(purchaseOrderInfo, PurchaseOrderInfo.PURCHASESTEP.Complete.toString()
                                        ,PurchaseOrderInfo.PURCHASESTEPSTATUS.Complete.toString(),venderConfirm,companyConfirm);
                                break;
                            default:
                                throw new RuntimeException("New Business type not handled yet");
                        }
                    } else {
                        com.ge.apm.domain.ApprovalNode node = approvalNodes.get(approvalLevel);
                        if (node != null) {
                            boolean usingRole = node.getUsingRole();
                            if (usingRole) {//角色审批，根据角色拉取用户进行审批
                                List<Integer> nodeRoleUids = node.getRoleUids();
                                if (nodeRoleUids != null) {
                                    userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                    //保存角色到审批流中
                                    date.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                    Object[] objects = nodeRoleUids.toArray();
                                    String str = StringUtils.join(objects, ",");
                                    date.setApprovalUid(str);
                                }
                            } else {//用户审批
                                List<String> userUids = node.getUserUids();
                                if(CollectionUtils.isEmpty(userUids)){
                                    userUids=new ArrayList<>();
                                }
                                List<String> uids=  getRoleApproveCondition(node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                                if(CollectionUtils.isNotEmpty(uids)){
                                    userUids.addAll(uids);
                                    userUids = userUids.stream().distinct().collect(Collectors.toList());
                                }
                                userAccounts = userAccountRepository.findByUidIn(userUids);

                                sendUserUids.addAll(userUids);
                                date.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                                Object[] objects = userUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                date.setApprovalUid(str);
                            }
                            date.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                            date.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                            date.setApprovalLevel(level);
                            date.setCreatedBy(userAccount.getLoginName());
                            date.setApprovalIng(false);
                            date.setApprovalLevelNow(level);
                            date.setInitiateApproverUid(null);
                            date.setCreatedDate(new Date());
                            date.setId(null);
                            date.setActualApproverName(null);
                            date.setActualApproverDate(null);
                            date.setApprovalOpinion(null);
                            //生成下条待审批数据
                            MultiLevelApproval multiLevel = multiLevelApprovalRepository.save(date);
                            String linkUrl="/wx/approvals/" + multiLevel.getId()+"?showType=0";
                            AssetInfo assetInfo=null;
                            switch (multiLevelApproval.getBusinessType()) {
                                case ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT:
                                    // 采购管理管理
                                    if(approvalLevel==1){//第一级临床确认
                                        updatePurchase(purchaseOrderInfo,
                                                PurchaseOrderInfo.PURCHASESTEP.ToConfirmedInfoMeet.toString(),
                                                PurchaseOrderInfo.PURCHASESTEPSTATUS.ConfirmedInfo.toString(),null,null);
                                        try {
                                            purchaseBudgetConfigService.sendWxMsgPurchase(purchaseOrderInfo, UserContextService.getAccessToken(),null);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }else if(approvalLevel==2){//第二级  过会信息 填写设备科
                                        updatePurchase(purchaseOrderInfo,
                                                PurchaseOrderInfo.PURCHASESTEP.ToConfirmedInfoTendered.toString(),
                                                PurchaseOrderInfo.PURCHASESTEPSTATUS.ConfirmedInfoMeet.toString(),null,null);
                                    }else if(approvalLevel==3){//第二级  过会信息 填写设备科
                                        updatePurchase(purchaseOrderInfo,
                                                PurchaseOrderInfo.PURCHASESTEP.Complete.toString(),
                                                PurchaseOrderInfo.PURCHASESTEPSTATUS.Complete.toString(),null,null);
                                    }
                                    String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2 + date.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
                                    try {
                                        this.sendWxMsg(sendUserUids, message,"待审批","",MSGEvent.MULTI_LEVEL_APPROVAL_TO_BE_APPROVED,multiLevelApproval);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;

                            }
                        }
                    }
                }
            }
        }
    }


    private void updateAssetScrap(MultiLevelApproval multiLevelApproval, Integer status, UserAccount userAccount, String approvalOpinion) {
        AssetScrap assetScrap = assetScrapDao.findById(multiLevelApproval.getBusinessUid());
        assetScrap.setLastModifiedBy(userAccount.getLoginName());
        assetScrap.setCurrentStatus(status);
        assetScrap.setLastModifiedDate(new Date());
        assetScrap.setApprovalReason(approvalOpinion);
        assetScrapDao.save(assetScrap);
        AssetInfo assetInfo = assetInfoDao.getById(assetScrap.getAssetId());

        if (ApprovalConstant.SCRAP_STATUS_SUCCESS.equals(status)   ) {


            UserAccount applyUser = userAccountRepository.getByUid(assetScrap.getRequestUserUid());
            //报废
            assetInfoToBeTerminatedService.terminateAsset(assetInfo,applyUser);

            //设备科长(可见范围内包含该设备的所有医学工程处主任)
            List<UserAccount> userAccounts=userAccountRepository.findUserByClinicalDeptUID(assetInfo.getClinicalDeptUID(),"AssetHead");
            if (userAccount.isEnableAssetTypeGroup()) {
                // 已开启资产分类，需要过滤掉没有资产分类权限的人
                if(StringUtils.isNotBlank(assetInfo.getAssetType())){
                    List<String> userUidList = assetTypeGroupRepository.getUserUidListByEncoded(assetInfo.getAssetType());
                    userAccounts = userAccounts.stream().filter(user -> userUidList.contains(user.getUid())).collect(Collectors.toList());
                }else{
                    userAccounts=new ArrayList<>();
                }

            }
            //推送消息到临床(该工单的报修人)
            UserAccount user = userAccountRepository.getByUid(assetScrap.getRequestUserUid());
            userAccounts.add(user);
            if(!assetScrap.getRequestUserUid().equals(assetScrap.getApprovalUid())){
                //责任工程师(提交审核时，该工单的当前owner)
                UserAccount approvalUser = userAccountRepository.getByUid(assetScrap.getApprovalUid());
                userAccounts.add(approvalUser);
            }
            wxMessageService.sendMessage(assetInfo,"审批通过",assetScrap,userAccounts,null,userAccount,assetScrap.getApplyReason(),MSGEvent.ASSET_SCRAP_MULTI_LEVEL_APPROVAL_PASS);
        }else{
            if(assetScrap.getAssetStatus()!=null){
                assetInfo.setStatus(assetScrap.getAssetStatus());
            }else{
                assetInfo.setStatus(1);
            }
            assetInfoDao.save(assetInfo);
        }

    }

    @Autowired
    AssetLifecyleService assetLifecyleService;

    @Autowired
    private MqUtils mqUtils;

    public void saveAssetHistoryIfUpdated(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setClassName("MultiLevelApprovalService");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setMethodName("saveAssetHistoryIfUpdated");
        mqContext.setDescribe("资产报废多级审批流程");
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(newAsset,currentUserAccount,mqContext);
    }
    public void sendWxMsg(List<String> userUids, String message, String remark,String linkUrl,MSGEvent msgEvent,MultiLevelApproval multiLevelApproval){
        Map<String, String> map = new HashMap<>();
        map.put("message", ApprovalConstant.WX_MESSAGE_APPROVAL + TimeUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss") + message);
        map.put("status", remark);
        map.put("businessNumber", multiLevelApproval.getBusinessNumber());
        map.put("businessTypeName",multiLevelApproval.getBusinessTypeName() );
        map.put("businessType",String.valueOf(multiLevelApproval.getBusinessType()) );
        map.put("date", TimeUtils.getStrDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        wxMessageService.sendWxMsgToMq(map,linkUrl, msgEvent,userUids,null);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    /**
     * @param type        0 维修工单, 1 保养工单
     *
     */
    private void generateSparePartsBuyList(int type, List<V2_WorkOrder_Detail> accessorySparePartsBuyList, List<V2_WorkOrder_Detail> accessorySparePartsRequestList, UserAccount userAccount) {
        if (accessorySparePartsRequestList != null && !accessorySparePartsRequestList.isEmpty()) {
            simplePartsRequestWorkOrderService.savePartsRequest(accessorySparePartsRequestList, type, userAccount);//生成申领单
        }
        if (accessorySparePartsBuyList != null && !accessorySparePartsBuyList.isEmpty()) {
            simplePartsRequestWorkOrderService.savePartsPurchase(accessorySparePartsBuyList, type, userAccount);//生成申购单
        }
    }

    public String getBusinessType(Integer businessType) {
        if (null == businessType) {
            return null;
        }
        switch (businessType) {
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST;
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE;
            case ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT:
                return ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                return ApprovalConstant.APPROVAL_ASSET_REPAIR_CHi;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                return ApprovalConstant.APPROVAL_ASSET_MAINTAIN_CHi;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT:
                return ApprovalConstant.APPROVAL_ASSET_BIG_REPAIR;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW:
                return ApprovalConstant.APPROVAL_ASSET_BORROW_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_BORROW_RETURN_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN:
                return ApprovalConstant.APPROVAL_ASSET_RECEIVE_RETURN_CHI;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER:
                return ApprovalConstant.APPROVAL_ASSET_TRANSFER_CHI;
            case ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT:
                return ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN;
            case ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS:
                return ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS_NAME;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                return ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT_NAME;
            case ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT:
                return ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT_NAME;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                return ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP_NAME;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                return ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT_NAME;
            case ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT:
                return ApprovalConstant.BUSINESS_TYPE_ASSET_EQUIPMENT_ADJUSTMENT_NAME;
            default:
                break;
        }
        return null;
    }

    /*
     * 判断有没有配置多级审核
     */
    public MultiLevelApprovalConfig checkMultiLevelApprovalPersonAndSubmit(ApprovableTarget approvableTarget) {
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getFirstApprovalList(approvableTarget, true);
        return multiLevelApprovalConfig;
    }

    public AdverseEvents getAdverseEvents(String token, String uid) {
        HttpEntity<String> requestEntity = new HttpEntity<>(uid, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.GET_ADVERSE_EVENTS_INFO);
        String url = gatewayURL.replace("{uid}", uid);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            String result = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            AdverseEvents adverseEvents = JSONObject.toJavaObject(data, AdverseEvents.class);
            return adverseEvents;
        }
        return null;
    }

    public void approveAdverseEvents(String token, AdverseEvents adverseEvents, MultiLevelApproval multiLevelApproval) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        HttpEntity<AdverseEvents> requestEntity = new HttpEntity<>(adverseEvents, createHttpHeaders(token));
        String gatewayURL = getGatewayURL(GatewayURL.APPROVE_ADVERSE_EVENTS);
        ResponseEntity<LinkedHashMap> response = template.postForEntity(gatewayURL, requestEntity, LinkedHashMap.class);
        if (!isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody();
            String message = responseMap.get("message").toString();
            throw new RuntimeException(message);
        }else{
            saveBlobFile(multiLevelApproval);
        }
    }

    // 预算管理
    private void updateBudget(MultiLevelApproval multiLevelApproval, Integer approvalStatus,String applicationStep) {
        BudgetApplication budget = budgetApplicationRepository.findById(multiLevelApproval.getBusinessUid());
        if(CollectionUtils.isNotEmpty(budget.getBudgetAssets())){
            budget.getBudgetAssets().iterator().next().setSignatureNurse(multiLevelApproval.getSignatureNurse());
        }
        budget.setApplicationStatus(approvalStatus);
        budget.setApplicationStep(applicationStep);
        //budget.setApprovalProgress(budget.getApprovalProgress() + 1);
        budget.setApproverId(UserContextService.getCurrentUserAccount().getId());
        budget.setApprovalDate(new Date());
        budgetApplicationRepository.save(budget);
    }

    // 采购管理
    private void updatePurchase(PurchaseOrderInfo purchaseInfo, String orderStep,String stepStatus
    , VenderInfo venderConfirm,VenderInfo companyConfirm) {
        purchaseInfo.setOrderStep(orderStep);
        purchaseInfo.setStepStatus(stepStatus);
        purchaseInfo.setApproverId(UserContextService.getCurrentUserAccount().getId());
        if(PurchaseOrderInfo.PURCHASESTEP.Complete.toString().equals(orderStep)){
            if(venderConfirm != null){
                purchaseInfo.setVenderId(venderConfirm.getId());
                purchaseInfo.setVenderName(venderConfirm.getVenderName());
            }
            if(companyConfirm != null){
                purchaseInfo.setCompanyId(companyConfirm.getId());
                purchaseInfo.setCompanyName(companyConfirm.getCompanyName());
            }
        }
        purchaseOrderInfoRepository.save(purchaseInfo);
    }

    // 预算管理
    @Transactional(rollbackFor = Exception.class)
    public void cancelApplication(MultiLevelApproval multiLevelApproval, Integer approvalStatus,String applicationStep) {
        BudgetApplication budget = budgetApplicationRepository.findById(multiLevelApproval.getBusinessUid());
        if(CollectionUtils.isNotEmpty(budget.getBudgetAssets())){
            budget.getBudgetAssets().iterator().next().setSignatureNurse(multiLevelApproval.getSignatureNurse());
        }
        budget.setApplicationStatus(approvalStatus);
        budget.setApplicationStep(applicationStep);
        budgetApplicationRepository.save(budget);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(budget.getBudgetApprovals())){
            multiLevelApprovalRepository.delete(budget.getBudgetApprovals());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalReturn(MultiLevelApproval multiLevelApprovalNew, String approvalOpinion,int returnLevel) {

        MultiLevelApproval multiLevelApproval = multiLevelApprovalNew;
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<UserAccount> userAccounts=new ArrayList<>();

        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getApproveWorkflowConfig(multiLevelApproval.getWorkflowConfigApproveUid());
        if (multiLevelApprovalConfig != null) {
            multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_RETURN);
            multiLevelApproval.setActualApproverName(userAccount.getName());
            multiLevelApproval.setActualApproverDate(new Date());
            multiLevelApproval.setActualApproverUid(userAccount.getUid());
            multiLevelApproval.setApprovalOpinion(approvalOpinion);
            multiLevelApproval.setApprovalIng(true);
            int level = returnLevel;
            if(level==0){
                return ;
            }
            multiLevelApproval.setApprovalLevelNow(returnLevel);
            //更新审批数据
            multiLevelApprovalRepository.save(multiLevelApproval);
            saveBlobFile(multiLevelApproval);
            //更新审批进度
            if (level > 0) {
                List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
                if (null != list) {
                    for (int i = 0; i < list.size(); i++) {
                        MultiLevelApproval multi = list.get(i);
                        if (multi != null ) {
                            multi.setApprovalLevelNow(returnLevel);
                            multiLevelApprovalRepository.save(multi);
                        }
                    }
                }
            }
            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                List<String> sendUserUids = ListUtil.newArrayList();
                List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {
                });
                if (approvalNodes != null) {
                    MultiLevelApproval date = multiLevelApprovalNew;
                    if (level == 1) {//退回第一节点

                        //没有特殊处理
                    }
                    com.ge.apm.domain.ApprovalNode node = approvalNodes.get(level-1);
                    if (node != null) {
                        boolean usingRole = node.getUsingRole();
                        if (usingRole) {//角色审批，根据角色拉取用户进行审批
                            List<Integer> nodeRoleUids = node.getRoleUids();
                            if (nodeRoleUids != null) {
                                userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                List<String> uids = userAccounts.stream().filter(item -> StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());
                                sendUserUids.addAll(uids);
                                //保存角色到审批流中
                                date.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                Object[] objects = nodeRoleUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                date.setApprovalUid(str);
                            }
                        } else {//用户审批
                            List<String> userUids = node.getUserUids();
                            if(CollectionUtils.isEmpty(userUids)){
                                userUids=new ArrayList<>();
                            }
                            List<String> uids=  getRoleApproveCondition(node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                            if(CollectionUtils.isNotEmpty(uids)){
                                userUids.addAll(uids);
                                userUids = userUids.stream().distinct().collect(Collectors.toList());
                            }
                            List<String> strings = generationAdjustment(approvalNodes, node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                            if(CollectionUtils.isNotEmpty(strings)){
                                userUids = strings;
                            }
                            userAccounts = userAccountRepository.findByUidIn(userUids);
                            sendUserUids.addAll(userUids);
                            date.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                            Object[] objects = userUids.toArray();
                            String str = StringUtils.join(objects, ",");
                            date.setApprovalUid(str);
                        }
                        date.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                        date.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                        date.setApprovalLevel(level);
                        date.setCreatedBy(userAccount.getLoginName());
                        date.setApprovalIng(false);
                        date.setApprovalLevelNow(level);
                        date.setInitiateApproverUid(null);
                        date.setCreatedDate(new Date());
                        date.setId(null);
                        date.setActualApproverName(null);
                        date.setActualApproverDate(null);
                        date.setApprovalOpinion(null);
                        //生成下条待审批数据
                        MultiLevelApproval multiLevel = multiLevelApprovalRepository.save(date);
                        String  linkUrl="/wx/approvals/" + multiLevel.getId()+"?showType=0";
                        extracted(multiLevelApproval, userAccount, userAccounts, sendUserUids, date, node, linkUrl);

                    }

                }
            }
        }

    }

    private void extracted(MultiLevelApproval multiLevelApproval, UserAccount userAccount, List<UserAccount> userAccounts, List<String> sendUserUids, MultiLevelApproval date, ApprovalNode node, String linkUrl) {
        AssetInfo assetInfo;
        switch (multiLevelApproval.getBusinessType()) {
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                AssetScrap assetScrap = assetScrapDao.findById(multiLevelApproval.getBusinessUid());
                assetInfo = assetInfoDao.findByUid(assetScrap.getAssetUid());
                wxMessageService.sendMessage(assetInfo,node.getNodeName(),assetScrap,userAccounts,linkUrl,userAccount,assetScrap.getApplyReason(),MSGEvent.ASSET_SCRAP_MULTI_LEVEL_APPROVAL_TO_APPROVAL);
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                AssetDepartmentConvert assetDepartmentConvert = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
                assetInfo = assetInfoDao.findByUid(assetDepartmentConvert.getAssetUid());
                wxMessageService.sendMessage(assetInfo,node.getNodeName(),assetDepartmentConvert,userAccounts,linkUrl,userAccount,assetDepartmentConvert.getApplyReason(),MSGEvent.ASSET_DEPARTMENT_CONVERT_MULTI_LEVEL_APPROVAL_TO_APPROVAL);
                break;
            default:
                //审批同意时 给审批人发送微信
                String message = ApprovalConstant.WX_MESSAGE_APPROVAL_1 + ApprovalConstant.WX_MESSAGE_APPROVAL_2 + date.getBusinessNumber() + ApprovalConstant.WX_MESSAGE_APPROVAL_3;
                this.sendWxMsg(sendUserUids, message, "待审批",linkUrl,MSGEvent.MULTI_LEVEL_APPROVAL_TO_BE_APPROVED,multiLevelApproval);

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void getMultiLevelApprovalDirectDelivery(MultiLevelApproval multiLevelApprovalNew, String approvalOpinion) {

        MultiLevelApproval multiLevelApproval = multiLevelApprovalNew;
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        MultiLevelApprovalConfig multiLevelApprovalConfig = this.getApproveWorkflowConfig(multiLevelApproval.getWorkflowConfigApproveUid());

        List<MultiLevelApproval> list = this.getMultiLevelApproval(multiLevelApproval);
        MultiLevelApproval multiLevelApprovalReturn = list.get(list.size() - 2);

        if(multiLevelApprovalReturn.getApprovalNodeStatus()!=ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_RETURN){
           return;
        }
        List<UserAccount> userAccounts=new ArrayList<>();

        if (multiLevelApprovalConfig != null) {
            multiLevelApproval.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            multiLevelApproval.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_AGREE);
            multiLevelApproval.setActualApproverName(userAccount.getName());
            multiLevelApproval.setActualApproverDate(new Date());
            multiLevelApproval.setActualApproverUid(userAccount.getUid());
            multiLevelApproval.setApprovalOpinion(approvalOpinion);
            multiLevelApproval.setApprovalIng(true);
            int level = multiLevelApprovalReturn.getApprovalLevel();

            multiLevelApproval.setApprovalLevelNow(level);
            //更新审批数据
            multiLevelApprovalRepository.save(multiLevelApproval);
            saveBlobFile(multiLevelApproval);
            //更新审批进度

                if (null != list) {
                    for (int i = 0; i < list.size(); i++) {
                        MultiLevelApproval multi = list.get(i);
                        if (multi != null) {
                            multi.setApprovalLevelNow(level);
                            multiLevelApprovalRepository.save(multi);
                        }
                    }
                }

            String approvalItem = multiLevelApprovalConfig.getApprovalItem();
            if (null != approvalItem) {
                List<String> sendUserUids = ListUtil.newArrayList();
                List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {
                });
                if (approvalNodes != null) {
                    MultiLevelApproval date = multiLevelApprovalNew;
                    if (level == 1) {//退回第一节点

                        //没有特殊处理
                    }
                    com.ge.apm.domain.ApprovalNode node = approvalNodes.get(level-1);
                    if (node != null) {
                        boolean usingRole = node.getUsingRole();
                        if (usingRole) {//角色审批，根据角色拉取用户进行审批
                            List<Integer> nodeRoleUids = node.getRoleUids();
                            if (nodeRoleUids != null) {
                                userAccounts = userAccountRepository.getApprovalPersonByRoleId(nodeRoleUids);
                                List<String> uids = userAccounts.stream().filter(item -> StringUtils.isNotBlank(item.getUid())).map(UserAccount::getUid).collect(Collectors.toList());

                                sendUserUids.addAll(uids);
                                //保存角色到审批流中
                                date.setApprovalType(ApprovalConstant.ROLE_APPROVAL_TYPE);
                                Object[] objects = nodeRoleUids.toArray();
                                String str = StringUtils.join(objects, ",");
                                date.setApprovalUid(str);
                            }
                        } else {//用户审批
                            List<String> userUids = node.getUserUids();
                            if(CollectionUtils.isEmpty(userUids)){
                                userUids=new ArrayList<>();
                            }
                            List<String> uids=  getRoleApproveCondition(node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                            if(CollectionUtils.isNotEmpty(uids)){
                                userUids.addAll(uids);
                                userUids = userUids.stream().distinct().collect(Collectors.toList());
                            }
                            List<String> strings = generationAdjustment(approvalNodes, node,multiLevelApproval.getBusinessUid(),multiLevelApproval.getBusinessType());
                            if(CollectionUtils.isNotEmpty(strings)){
                                userUids = strings;
                            }
                            userAccounts = userAccountRepository.findByUidIn(userUids);
                            sendUserUids.addAll(userUids);
                            date.setApprovalType(ApprovalConstant.USER_APPROVAL_TYPE);
                            Object[] objects = userUids.toArray();
                            String str = StringUtils.join(objects, ",");
                            date.setApprovalUid(str);
                        }
                        date.setApprovalStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
                        date.setApprovalNodeStatus(ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
                        date.setApprovalLevel(level);
                        date.setCreatedBy(userAccount.getLoginName());
                        date.setApprovalIng(false);
                        date.setApprovalLevelNow(level);
                        date.setInitiateApproverUid(null);
                        date.setCreatedDate(new Date());
                        date.setId(null);
                        date.setActualApproverName(null);
                        date.setActualApproverDate(null);
                        date.setApprovalOpinion(null);
                        //生成下条待审批数据
                        MultiLevelApproval multiLevel = multiLevelApprovalRepository.save(date);

                        String linkUrl="/wx/approvals/" + multiLevel.getId()+"?showType=0";
                        extracted(multiLevelApproval, userAccount, userAccounts, sendUserUids, date, node, linkUrl);

                    }

                }
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveConfirmInfoBudgetApp(PurchaseOrderInfo purchaseOrderInfoV,String budgetAssetId,BudgetApplicationAsset budgetApplicationAssetConfirm) {
        BudgetApplicationAsset budgetApplicationAsset = budgetApplicationAssetRepository.findById(budgetAssetId);
        budgetApplicationAsset.setConfirmApplicationDeptId(budgetApplicationAssetConfirm.getConfirmApplicationDeptId());
        budgetApplicationAsset.setConfirmEquipmentCategory(budgetApplicationAssetConfirm.getConfirmEquipmentCategory());
        budgetApplicationAsset.setConfirmQuantity(budgetApplicationAssetConfirm.getConfirmQuantity());
        budgetApplicationAsset.setConfirmPrice(budgetApplicationAssetConfirm.getConfirmPrice());
        budgetApplicationAsset.setConfirmNationalFinance(budgetApplicationAssetConfirm.getConfirmNationalFinance());
        budgetApplicationAsset.setConfirmProvincialFinance(budgetApplicationAssetConfirm.getConfirmProvincialFinance());
        budgetApplicationAsset.setConfirmNotice(budgetApplicationAssetConfirm.getConfirmNotice());
        budgetApplicationAsset.setConfirmImport(budgetApplicationAssetConfirm.getConfirmImport());
        budgetApplicationAsset.setConfirmMajorFunction(budgetApplicationAssetConfirm.getConfirmMajorFunction());
        budgetApplicationAsset.setConfirmParameters(budgetApplicationAssetConfirm.getConfirmParameters());
        budgetApplicationAsset.setConfirmBrand(budgetApplicationAssetConfirm.getConfirmBrand());
        budgetApplicationAsset.setConfirmMaximumPrice(budgetApplicationAssetConfirm.getConfirmMaximumPrice());
        budgetApplicationAsset.setConfirmRemark(budgetApplicationAssetConfirm.getConfirmRemark());
        budgetApplicationAssetRepository.save(budgetApplicationAsset);

        //处理采购数量和采购金额
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ,purchaseOrderInfoV.getId()));
        List<BudgetApplicationAsset> budgetApplicationAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets);
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        int applicationQuantitySumResult = 0;
        for(BudgetApplicationAsset curApplicationBudget:budgetApplicationAssets){
            if(curApplicationBudget.getConfirmAmount()==null && curApplicationBudget.getConfirmQuantity()==null){
                totalAmount.set(totalAmount.get().add(curApplicationBudget.getOfflineApplicationAmount()==null?
                        new BigDecimal(0):curApplicationBudget.getOfflineApplicationAmount()));
                applicationQuantitySumResult += curApplicationBudget.getOfflineApplicationQuantity()==null?
                        0:curApplicationBudget.getOfflineApplicationQuantity();
            }else{
                totalAmount.set(totalAmount.get().add(curApplicationBudget.getConfirmAmount()==null?
                        new BigDecimal(0):curApplicationBudget.getConfirmAmount()));
                applicationQuantitySumResult += curApplicationBudget.getConfirmQuantity()==null?
                        0:curApplicationBudget.getConfirmQuantity();
            }
        }
        purchaseOrderInfoV.setTotalPrice(totalAmount.get());
        purchaseOrderInfoV.setPurchaseQuantity(applicationQuantitySumResult);
        purchaseOrderInfoRepository.save(purchaseOrderInfoV);

    }
}
