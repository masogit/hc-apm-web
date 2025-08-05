package com.ge.apm.view.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.budget.PurchaseBudgetConfigService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.budget.BudgetConstant;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class MultiLevelApprovalController extends GenericCRUDUUIDController<MultiLevelApproval> implements ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(MultiLevelApprovalController.class);

    private MultiLevelApprovalRepository multiLevelApprovalRepository= null;
    private MultiLevelApprovalService multiLevelApprovalService;
    private V2_WorkOrderRepository v2_WorkOrderRepository;
    private V2_PmOrderRepository v2_PmOrderRepository;
    private V2_WorkOrderService woService;
    private BudgetApplicationRepository budgetDao;
    private BudgetApplicationAssetRepository budgetApplicationAssetRepository;
    private PurchaseOrderInfoRepository purchaseOrderInfoRepository;
    private OrgInfoRepository orgDao;
    private VenderInfoRepository venderInfoRepository;
    private UaaService uaaService;
    private SharedAssetRequestRepository sharedAssetRequestRepository;
    private MaintenanceContractApplyPurchaseRepository maintenanceContractApplyPurchaseRepository;
    private MaintenanceContractApplyPurchaseAssetRepository maintenanceContractApplyPurchaseAssetRepository;

    private UserAccountRepository userAccountRepository;
    private WorkflowService workflowService;
    private BlobFileService blobService;

    private boolean signatureNurseRendered = false;
    private String signatureNurse;
    @Setter @Getter
    private boolean budgetConfirmArg = false;
    public String getSignatureNurse() {
        return signatureNurse;
    }

    public void setSignatureNurse(String signatureNurse) {
        this.signatureNurse = signatureNurse;
    }

    private BlobObjectRepository blobObjectRepository;
    private AssetScrapRepository assetScrapDao;
    private AssetDepartmentConvertRepository assetDepartmentConvertRepository;

    private AssetInfoRepository assetInfoRepository;

    private I18nMessageService i18nMessageService;


    @Setter
    @Getter
    private List<MultiLevelApproval> multiLevelApprovals;
    private UserAccount account = UserContextService.getCurrentUserAccount();
    @Getter
    @Setter
    private Integer businessTypeFlag;
    @Getter
    @Setter
    private String approvalOpinion;

    @Getter
    @Setter
    private Integer approvalResult;// 不良事件审核结果 1-同意上报 2-同意不上报

    @Getter
    @Setter
    private List<V2_BlobObject> attachList;

    @Getter
    @Setter
    private List<V2_BlobObject> mcApplyPurchaseBlobList;

    @Getter
    @Setter
    private List<V2_BlobObject> fixedAttachList;
    @Getter
    @Setter
    private List<Map<String, List<V2_BlobObject>>> workParts;

    @Getter
    private Object primaryApprovalData;

    @Getter
    private Object secondaryApprovalData;

    // 签名相关
    @Getter
    @Setter
    private String signatureContent;
    @Getter
    private StreamedContent signatureContentStream;
    @Getter
    private String signatureId;
    @Getter
    private boolean newSignature = true;
    @Getter @Setter
    private BudgetApplicationAsset confirmBudget;
    @Getter @Setter
    private PurchaseOrderInfo purchaseOrderInfo;
    @Getter @Setter
    private MaintenanceContractApplyPurchaseVo applyPurchaseVo;
    @Getter  @Setter
    private OrgInfo org;
    @Getter  @Setter
    private TreeNode orgTree;
    @Getter  @Setter
    private String budgetAssetId;

    @Getter  @Setter
    private VenderInfo venderInfoConfirm;
    @Getter  @Setter
    private VenderInfo companyInfoConfirm;
    @Getter  @Setter
    private List<VenderInfo> venderInfos = new ArrayList<>();
    @Getter  @Setter
    private List<VenderInfo> companyInfos = new ArrayList<>();

    private boolean isAgree = true;
    private boolean needSignature = false;

    private boolean isReturnApproval=false;

    private boolean isDirectDelivery=false;
    private MultiLevelApprovalConfigRepository multiLevelApprovalConfigRepository;
    @Override
    protected GenericRepositoryUUID<MultiLevelApproval> getDAO() {
        return multiLevelApprovalRepository;
    }

    @Override
    protected void init() {
        v2_WorkOrderRepository= WebUtil.getBean(V2_WorkOrderRepository.class);
        woService= WebUtil.getBean(V2_WorkOrderService.class);
        v2_PmOrderRepository= WebUtil.getBean(V2_PmOrderRepository.class);
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        multiLevelApprovalService = WebUtil.getBean(MultiLevelApprovalService.class);
        budgetDao = WebUtil.getBean(BudgetApplicationRepository.class);
        budgetApplicationAssetRepository = WebUtil.getBean(BudgetApplicationAssetRepository.class);
        purchaseOrderInfoRepository = WebUtil.getBean(PurchaseOrderInfoRepository.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetScrapDao = WebUtil.getBean(AssetScrapRepository.class);
        assetDepartmentConvertRepository = WebUtil.getBean(AssetDepartmentConvertRepository.class);
        venderInfoRepository = WebUtil.getBean(VenderInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        sharedAssetRequestRepository = WebUtil.getBean(SharedAssetRequestRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        maintenanceContractApplyPurchaseRepository = WebUtil.getBean(MaintenanceContractApplyPurchaseRepository.class);
        maintenanceContractApplyPurchaseAssetRepository = WebUtil.getBean(MaintenanceContractApplyPurchaseAssetRepository.class);
        multiLevelApprovalConfigRepository = WebUtil.getBean(MultiLevelApprovalConfigRepository.class);

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(account.getHospitalUID(), null);
        org = orgDao.findById(account.getOrgInfoId());
        orgTree = initOrgTree(treeNode);
        this.confirmBudget = new BudgetApplicationAsset();
        this.purchaseOrderInfo = new PurchaseOrderInfo();
        applyPurchaseVo = new MaintenanceContractApplyPurchaseVo();
    }
    private Map<String, List<V2_BlobObject>> initBlob(){
        Map<String, List<V2_BlobObject>> objectObjectHashMap = new HashMap<>();
        List<V2_BlobObject> blobObjects = new ArrayList<>();
        Page<V2_BlobObject> all = blobObjectRepository.findAll(new PageRequest(1, 10));
        blobObjects.addAll(all.getContent());
        String key = "123";
        objectObjectHashMap.put(key, blobObjects);
        return objectObjectHashMap;
    }

    public void getApprovalContentByCurrentApproval(MultiLevelApproval multiLevelApproval) {

        primaryApprovalData = null;
        secondaryApprovalData = null;
        multiLevelApprovals = multiLevelApprovalService.getMultiLevelApproval(multiLevelApproval);
//        if(true){
//            return;
//        }
        if (null == multiLevelApproval) {
            multiLevelApprovals = null;
            return;
        }

        //multi level approval status
        multiLevelApprovals = multiLevelApprovalService.getMultiLevelApproval(multiLevelApproval);
        businessTypeFlag = multiLevelApproval.getBusinessType();
        setHandlerPerson(multiLevelApprovals);
        //multi level approval content
        switch (businessTypeFlag){
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                V2_WorkOrder work = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                List<V2_WorkOrder> workOrderLists = new ArrayList<V2_WorkOrder>();
                workOrderLists.add(work);
                fixedAttachList= blobObjectRepository.findByBoUuid(work.getId());
                primaryApprovalData = workOrderLists;
                List<V2_WorkOrder_Detail> workOrderDetails = woService.getWorkOrderDetails(work.getId());
                workParts = workOrderDetails.stream().filter(item -> !CollectionUtils.isEmpty(item.getAttachments()))
                        .map(item->{
                            Map<String, List<V2_BlobObject>> objectObjectHashMap = new HashMap<>();
                            objectObjectHashMap.put(item.getParts(), item.getAttachments());
                            return objectObjectHashMap;
                        })
                        .collect(Collectors.toList());

                List<MultiLevelApproval> multiLevelApprovalsALl   = multiLevelApprovalRepository.getByBusinessUidList(multiLevelApproval.getBusinessUid());
                List<String> workflowConfigApproveUids = multiLevelApprovalsALl.stream().map(MultiLevelApproval::getWorkflowConfigApproveUid).collect(Collectors.toList());
                Map<String, List<MultiLevelApproval>> listMap = multiLevelApprovalsALl.stream()
                        .filter(item -> item.getApprovalLevelNow() != -1 && item.getApprovalLevelNow() != 0)
                        .collect(Collectors.groupingBy(MultiLevelApproval::getWorkflowConfigApproveUid));
                HashMap<String, List<MultiLevelApproval>> stringListHashMap = new HashMap<>();

                List<MultiLevelApprovalConfig> multiLevelApprovalConfigs = multiLevelApprovalConfigRepository.getByIds(workflowConfigApproveUids);
                for (MultiLevelApprovalConfig multiLevelApprovalConfig : multiLevelApprovalConfigs) {
                    Map currentConfig = JSON.parseObject(multiLevelApprovalConfig.getConfigration(), Map.class);
                    List<String> currentConfigPartsType = (List<String>) currentConfig.get("selectAssetPartsTypes");
                    List<MultiLevelApproval> multiLevelApprovals = listMap.get(multiLevelApprovalConfig.getId());
                    for (String s : currentConfigPartsType) {
                        stringListHashMap.put(s,multiLevelApprovals);
                    }
                }

                workOrderDetails.forEach(item->{
                    if(item.getApproved()!=null){
                        item.setApprovalStatusName((item.getApproved()?"审批通过":"审批不通过"));
                    }else{
                       if("1".equals(work.getApprovalStatus())){
                           item.setApprovalStatusName("未审批");
                       }else if("5".equals(work.getApprovalStatus())){
                           List<MultiLevelApproval> multiLevelApprovalsType = stringListHashMap.get(item.getPartsType());


                           if(!CollectionUtils.isEmpty(multiLevelApprovalsType) && multiLevelApprovalsType.size()==1){
                               item.setApprovalStatusName("未审批");
                           }else{
                               item.setApprovalStatusName("审批中");
                           }
                       }else{
                           item.setApprovalStatusName("未提交");
                       }
                    }

                });
                secondaryApprovalData = workOrderDetails;
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT:
                V2_PmOrder byId = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
                ArrayList<V2_PmOrder> pmOrderLists = new ArrayList<>();
                pmOrderLists.add(byId);
                primaryApprovalData = pmOrderLists;
                secondaryApprovalData = woService.getWorkOrderDetails(byId.getId());;
                break;
            case ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS:
                String token = UserContextService.getAccessToken();
                AdverseEvents adverseEvents = multiLevelApprovalService.getAdverseEvents(token, multiLevelApproval.getBusinessUid());
                List<AdverseEvents> list = new ArrayList<>();
                list.add(adverseEvents);
                primaryApprovalData = list;
                secondaryApprovalData = adverseEvents;
                break;
            case ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT:
                BudgetApplication budgetApplication = budgetDao.findById(multiLevelApproval.getBusinessUid());
                BudgetApplicationAsset budgetApplicationAsset = new BudgetApplicationAsset();
                if(!CollectionUtils.isEmpty(budgetApplication.getBudgetAssets())){
                     budgetApplicationAsset = budgetApplication.getBudgetAssets().iterator().next();
                }
                budgetApplicationAsset.setApplicantName(budgetApplication.getApplicant().getName());//申请人
                budgetApplicationAsset.setApplicationDeptName(budgetApplication.getApplicationDept().getName());//申请科室
                budgetApplicationAsset.setApplicationDate(budgetApplication.getApplicationDate());//申请日期
                attachList = blobObjectRepository.findByBoUuid(budgetApplicationAsset.getId());
                primaryApprovalData = budgetApplication;
                secondaryApprovalData = budgetApplication.getBudgetAssets();
                signatureNurseRendered = false;
                if("T".equals(budgetApplicationAsset.getUsedOperatingRoom())){
                    signatureNurseRendered = true;
                }
                break;
            case ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT:
                this.venderInfos = new ArrayList<>();
                this.companyInfos = new ArrayList<>();
                PurchaseOrderInfo purchaseOrderInfo = purchaseOrderInfoRepository.findById(multiLevelApproval.getBusinessUid());
                List<VenderInfo> venderInfoList = venderInfoRepository.queryByPurchaseId(purchaseOrderInfo.getId());
                if(StringUtils.isNotBlank(purchaseOrderInfo.getVenderId())){
                    this.venderInfoConfirm = venderInfoRepository.findById(purchaseOrderInfo.getVenderId());
                }
                if(StringUtils.isNotBlank(purchaseOrderInfo.getCompanyId())){
                    this.companyInfoConfirm = venderInfoRepository.findById(purchaseOrderInfo.getCompanyId());
                }
                if(!CollectionUtils.isEmpty(venderInfoList)){
                    this.venderInfos = venderInfoList.stream().filter(vi -> VenderInfo.BidType.Vender.toString().equals(vi.getBidType())).collect(Collectors.toList());
                    this.companyInfos = venderInfoList.stream().filter(vi -> VenderInfo.BidType.Company.toString().equals(vi.getBidType())).collect(Collectors.toList());
                }
                this.purchaseOrderInfo = purchaseOrderInfo;


                List<SearchFilter> searchFilterAssets = new ArrayList<>();
                searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                        SearchFilter.Operator.EQ,purchaseOrderInfo.getId()));
                List<BudgetApplicationAsset> budgetApplicationAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets);
                if(!CollectionUtils.isEmpty(budgetApplicationAssets) &&
                        PurchaseOrderInfo.PURCHASESTEP.ToConfirmedInfoMeet.toString().equals(purchaseOrderInfo.getOrderStep())){
                    budgetApplicationAssets.stream().filter(baa -> baa.getConfirmApplicationDeptId()!=null).forEach(baa ->{
                        if(StringUtils.isBlank(baa.getBudgetApplication().getConfirmStatus()) ||
                                "UnConfirm".equals(baa.getBudgetApplication().getConfirmStatus())){
                            budgetConfirmArg = true;
                        }
                    });
                }
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                AssetScrap assetScrap = assetScrapDao.findById(multiLevelApproval.getBusinessUid());
                UserAccount user = userAccountRepository.findByUid(assetScrap.getRequestUserUid());
                if(StringUtils.isNotBlank(assetScrap.getEquipmentDisposalMethod())){
                    I18nMessage equipmentDisposalMethod = i18nMessageService.getByMsgTypeAndMsgKey("equipmentDisposalMethod", assetScrap.getEquipmentDisposalMethod());
                    if(equipmentDisposalMethod!=null){
                        if(equipmentDisposalMethod.getMsgKey().equals("0")){
                            String method;
                            if(assetScrap.getCustomDisposalMethod()!=null){
                                method =equipmentDisposalMethod.getValueZh()+"("+assetScrap.getCustomDisposalMethod()+")";
                            }else{
                                method =equipmentDisposalMethod.getValueZh();
                            }
                            assetScrap.setCustomDisposalMethod(method);
                        }else{
                            assetScrap.setCustomDisposalMethod(equipmentDisposalMethod.getValueZh());
                        }
                    }
                }
                assetScrap.setCreatedBy(user.getName());
                AssetInfo asset = assetInfoRepository.findByUid(assetScrap.getAssetUid());
                fixedAttachList= blobObjectRepository.findByBoUuid(assetScrap.getId());
                JSONObject jsonObject = (JSONObject) JSON.toJSON(assetScrap);
                jsonObject.put("assetInfo",asset);
                jsonObject.put("lifecycleInYear",asset.getLifecycleInYear());
                List<Object> objects = new ArrayList<>();
                objects.add(jsonObject);
                primaryApprovalData = objects;
                secondaryApprovalData =objects;
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                AssetDepartmentConvert assetDepartmentConvert = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
                primaryApprovalData = assetDepartmentConvert;
                secondaryApprovalData = assetDepartmentConvert;
                break;
            case ApprovalConstant.BUSINESS_TYPE_BATH_ASSET_DEPARTMENT_CONVERT:
                List<AssetDepartmentConvert> assetDepartmentConverts = assetDepartmentConvertRepository.findByTenantAndBusinessNumber(account.getTenantUID(), multiLevelApproval.getBusinessNumber());
                primaryApprovalData = assetDepartmentConverts;
                secondaryApprovalData = assetDepartmentConverts;
                break;
            case ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT:
                  SharedAssetRequest sharedAssetRequest = sharedAssetRequestRepository.findById(multiLevelApproval.getBusinessUid());
                primaryApprovalData = sharedAssetRequest;
                secondaryApprovalData =sharedAssetRequest;
                break;
            case ApprovalConstant.BUSINESS_TYPE_MAINTENANCE_CONTRACT_APPLY_PURCHASE:
                MaintenanceContractApplyPurchase maintenanceContractApplyPurchase = maintenanceContractApplyPurchaseRepository.findById(multiLevelApproval.getBusinessUid());
                primaryApprovalData = maintenanceContractApplyPurchase;
                secondaryApprovalData =maintenanceContractApplyPurchaseAssetRepository.findByApplicationId(maintenanceContractApplyPurchase.getId());
                mcApplyPurchaseBlobList = blobObjectRepository.findByBoUuid(maintenanceContractApplyPurchase.getId());
                break;
            default:
                throw new RuntimeException("New Business type not handled yet");
        }
    }

    private void setHandlerPerson(List<MultiLevelApproval> multiLevelApprovals) {
        for (MultiLevelApproval levelApproval : multiLevelApprovals) {
            if ( levelApproval.getApprovalNodeStatus() == 0) {
                String handlerPerson = Optional.ofNullable(levelApproval.getApprovalUid())
                        .map(uidStr -> Arrays.stream(uidStr.split(","))
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList()))
                        .filter(uids -> !uids.isEmpty())
                        .map(userAccountRepository::findByUidIn)
                        .map(userAccounts -> userAccounts.stream()
                                .map(UserAccount::getName)
                                .collect(Collectors.toList()))
                        .map(names -> String.join(",", names))
                        .orElse("");
                levelApproval.setHandlerPerson(handlerPerson);
            }else{
                levelApproval.setHandlerPerson(levelApproval.getActualApproverName());

            }
        }
    }

    /**
     * 供备件审批使用--审批同意
     * @return
     */
    public void getMultiLevelApprovalPersonByAgree() throws Exception {
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());
        multiLevelApproval.setAttachments(attachList);
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批，勿重复操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApproval.setSignatureNurse(this.signatureNurse);
        multiLevelApprovalService.getMultiLevelApprovalPersonByAgree(multiLevelApproval,approvalOpinion);
        selected = null;
        this.needSignature = false;
        this.resetSignature();
    }

    /**
     * 供备件审批使用--审批同意
     * @return
     */
    public void getBudgetMultiLevelApprovalPersonByAgree() throws Exception {
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(signatureNurseRendered && "F".equals(this.signatureNurse)){
            WebUtil.addWarnMessage("在手术室使用的设备，手术室护士长签名为否时，不允许通过审核！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批，勿重复操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApproval.setSignatureNurse(this.signatureNurse);
        multiLevelApproval.setAttachments(attachList);
        if(businessTypeFlag == 20){
            multiLevelApprovalService.maintenanceContractApplyPurchaseAgree(multiLevelApproval, approvalOpinion, applyPurchaseVo);
            approvalOpinion = "";
            applyPurchaseVo = new MaintenanceContractApplyPurchaseVo();
        }else{
            multiLevelApprovalService.getMultiLevelApprovalPersonByAgree(multiLevelApproval,approvalOpinion);
        }
        selected = null;
        this.needSignature = false;
        this.resetSignature();
    }

    /**
     * 供采购使用
     * @return
     */
    public void purchaseInfoSave() throws Exception {
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批，勿重复操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        purchaseOrderInfoRepository.save(this.purchaseOrderInfo);
        WebUtil.addSuccessMessage("采购过会信息保存成功！");
    }

    /**
     * 供采购使用
     * @return
     */
    public void purchaseInfoSubmit() throws Exception {
        if(this.selected.getApprovalLevel()!=null && this.selected.getApprovalLevel() == 2){
            if(!handValidateData(this.purchaseOrderInfo)){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            };
        };
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批，勿重复操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        //  1节点校验所有数据全部确认完成
        PurchaseOrderInfo purchaseOrderInfo = purchaseOrderInfoRepository.findById(this.selected.getBusinessUid());
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ,purchaseOrderInfo.getId()));
        List<BudgetApplicationAsset> budgetApplicationAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets);
        if (!CollectionUtils.isEmpty(budgetApplicationAssets)){
            try {
                budgetApplicationAssets.stream().forEach(baa -> {
                    if(StringUtils.isBlank(baa.getConfirmNotice()) || StringUtils.isBlank(baa.getConfirmEquipmentCategory())){
                        throw new RuntimeException();
                    }
                    if(this.selected.getApprovalLevel()!=null && this.selected.getApprovalLevel() == 2) {
                        if(baa.getConfirmPrice() == null || baa.getConfirmQuantity() == null ){
                            throw new RuntimeException();
                        }
                    }
                });
            } catch(Exception e) {
                if(this.selected.getApprovalLevel()!=null && this.selected.getApprovalLevel() == 2) {
                    WebUtil.addWarnMessage("申购单数量/价格确认未完成，请检查后重试！");
                }else {
                    WebUtil.addWarnMessage("申购单信息确认未完成，请检查后重试！");
                }
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
        }
        multiLevelApprovalService.getMultiLevelApprovalPurchaseByAgree(multiLevelApproval,approvalOpinion,
                this.purchaseOrderInfo,this.venderInfoConfirm,this.companyInfoConfirm);
        selected = null;
        this.needSignature = false;
        this.resetSignature();
    }

    public boolean handValidateData(PurchaseOrderInfo purchaseOrderInfo){
        boolean validateArg = true;
       /* if(StringUtils.isBlank(purchaseOrderInfo.getMeetingEngineerOpinion())){
            WebUtil.addWarnMessage("专业工程师意见不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }*/
        if(StringUtils.isBlank(purchaseOrderInfo.getMeetingExistingBrands())){
            WebUtil.addWarnMessage("医院现有品牌不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
       /* if(StringUtils.isBlank(purchaseOrderInfo.getMeetingFeeItems())){
            WebUtil.addWarnMessage("收费项目不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
        if(StringUtils.isBlank(purchaseOrderInfo.getMeetingConsumableDescription())){
            WebUtil.addWarnMessage("耗材或试剂说明不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }*/
        if(StringUtils.isBlank(purchaseOrderInfo.getMeetingEquipmentDeptOpin())){
            WebUtil.addWarnMessage("器械科审核意见不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
        if(purchaseOrderInfo.getMeetingApproveDate()==null){
            WebUtil.addWarnMessage("审核时间不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
        if(StringUtils.isBlank(purchaseOrderInfo.getMeetingPurchasingImports())){
            WebUtil.addWarnMessage("是否采购进口产品不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
        if(StringUtils.isBlank(purchaseOrderInfo.getMeetingRemarks())){
            WebUtil.addWarnMessage("备注不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
        if(purchaseOrderInfo.getMeetingDate()==null){
            WebUtil.addWarnMessage("过会日期不可为空！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            validateArg = false;
        }
        return validateArg;
    }

    /**
     * 供备件审批使用--审批不同意
     * @return
     */
    public void getMultiLevelApprovalPersionAndSubmitByDisagree() {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        multiLevelApproval.setAttachments(attachList);
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批,勿重复操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
         }
        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApproval.setSignatureNurse(this.signatureNurse);
        multiLevelApproval.setAttachments(attachList);
        if(businessTypeFlag == 20){
            multiLevelApprovalService.maintenanceContractApplyPurchaseByDisagree(multiLevelApproval, approvalOpinion, applyPurchaseVo);
        }else{
            multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmitByDisagree(multiLevelApproval,approvalOpinion);
        }
        selected = null;
        this.needSignature = false;
        this.resetSignature();
    }

    /**
     * 预算审批取消
     * @return
     */
    public void cancelApplicationMultiLevelApproval() {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批,勿重复操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApproval.setSignatureNurse(this.signatureNurse);
        multiLevelApprovalService.cancelApplication(multiLevelApproval,BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING,BudgetApplication.APPLICATIONSTEP.ToSubmitted.toString());
        selected = null;
        this.needSignature = false;
        this.resetSignature();
    }

    /**
     * 不良事件--审批同意
     * @return
     */
    public void adverseEventsAgree() throws Exception {
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批，勿重复操作！");
            return;
        }
        multiLevelApproval.setAttachments(attachList);
        String token = UserContextService.getAccessToken();
        AdverseEvents events = new AdverseEvents();
        events.setUid(multiLevelApproval.getBusinessUid());
        events.setStatus(6);
        events.setApprovalOpinion(approvalOpinion);
        events.setApprovalResult(approvalResult);
        multiLevelApprovalService.approveAdverseEvents(token, events,multiLevelApproval);
        selected = null;
    }

    /**
     * 不良事件--审批不同意
     * @return
     */
    public void adverseEventsDisagree() {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批,勿重复操作！");
            return;
        }
        multiLevelApproval.setAttachments(attachList);
        String token = UserContextService.getAccessToken();
        AdverseEvents events = new AdverseEvents();
        events.setUid(multiLevelApproval.getBusinessUid());
        events.setStatus(4);
        events.setApprovalOpinion(approvalOpinion);
        events.setApprovalResult(approvalResult);
        multiLevelApprovalService.approveAdverseEvents(token, events,multiLevelApproval);
        selected = null;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selected = (MultiLevelApproval) eventObject;

        if (null == selected) {
            return;
        }

        businessTypeFlag = selected.getBusinessType();

        getApprovalContentByCurrentApproval(selected);
    }

    public void preApproveCheck(Integer agree) throws Exception {
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());

        String siteUid = null;

        if(this.businessTypeFlag == 7){
            V2_WorkOrder v2WorkOrder = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
            siteUid = v2WorkOrder.getSiteUID();
        }else if(this.businessTypeFlag == 8){
            V2_PmOrder v2PmOrder = v2_PmOrderRepository.findById(multiLevelApproval.getBusinessUid());
            siteUid = v2PmOrder.getSiteUID();
        }else if(this.businessTypeFlag == 15){
            AssetScrap assetScrap = assetScrapDao.findById(multiLevelApproval.getBusinessUid());
            siteUid = assetScrap.getSiteUid();
        }else if(this.businessTypeFlag == 16 || this.businessTypeFlag == 19){
            AssetDepartmentConvert assetDepartmentConvert = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
            AssetInfo byUid = assetInfoRepository.findByUid(assetDepartmentConvert.getAssetUid());
            siteUid=byUid.getSiteUID();
        }else if(this.businessTypeFlag == 18){
            SharedAssetRequest sharedAssetRequest = sharedAssetRequestRepository.findById(multiLevelApproval.getBusinessUid());
            siteUid=sharedAssetRequest.getSiteUID();
        }

        if(StringUtils.isNotEmpty(siteUid)){
            WorkflowConfig workflowConfig = workflowService.getWorkflowByOrg(siteUid);
            if(workflowConfig != null && Boolean.TRUE.equals(workflowConfig.getPartsNeedSign())){
                if(agree == 1){
                    this.isAgree = true;
                }else if(agree == 2){
                    this.isAgree = false;
                }else if(agree==3){
                    this.isReturnApproval=true;
                }else if(agree==4){
                    this.isDirectDelivery=true;
                }
                this.needSignature = true;
                // 如果勾选了‘备件审核需要签名’，校验是否已经签名 弹出签名窗口
                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
                response.setStatus(205);
                return;
            }
        }

        if(agree == 1){
            this.isAgree = true;
            this.getMultiLevelApprovalPersonByAgree();
        }else if(agree == 2){
            this.isAgree = false;
            this.getMultiLevelApprovalPersionAndSubmitByDisagree();
        }else if(agree == 3){
            this.returnApprove();
        }else if(agree==4){
            this.directDelivery();
        }

    }



    public void returnApprove() throws Exception {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        multiLevelApproval.setAttachments(attachList);
        int level = 0;
        for (int i=1;i<=nodes.size();i++){
            if(nodes.get(i-1).getNodeName().equals(nodeName)){
                level=i;
            }
        }
        if(level==0 || level>multiLevelApproval.getApprovalLevelNow() || multiLevelApproval.getApprovalLevelNow()==1 || multiLevelApproval.getApprovalLevelNow()==level){
            return;
        }

        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批,勿重复操作！");
            return;
        }
        this.isAgree = false;

        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApprovalService.getMultiLevelApprovalReturn(multiLevelApproval,approvalOpinion,level);
        selected = null;
        this.needSignature = false;
        this.isReturnApproval=false;
        this.nodes=null;
        this.nodeName=null;
        this.resetSignature();
    }
    public void directDelivery() throws Exception {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        multiLevelApproval.setAttachments(attachList);
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批,勿重复操作！");
            return;
        }
        this.isAgree = false;

        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApprovalService.getMultiLevelApprovalDirectDelivery(multiLevelApproval,approvalOpinion);
        selected = null;
        this.needSignature = false;
        this.isDirectDelivery=false;
        this.resetSignature();
    }


    public void getSignature() {
        this.signatureContent = null;
        this.signatureContentStream = null;

        List<Map<String, String>> signatureList = woService.getUserSignature();

        if (!CollectionUtils.isEmpty(signatureList)) {
            String lastSignatureId = signatureList.get(signatureList.size() - 1).get("signatureUid");
            this.signatureContentStream = blobService.getBlobObjectByStorageId(lastSignatureId);
            this.signatureId = lastSignatureId;
            this.newSignature = false;
        }else{
            this.newSignature = true;
        }
    }

    public void saveSignatureAndSubmitApprove() throws Exception {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if(StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }

        if(this.newSignature && StringUtils.isNotEmpty(signatureContent)){
            try {
                String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decode = decoder.decodeBuffer(base64);

                V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                if (null != blobObject) {
                    this.signatureId = blobObject.getObjectStorageId();
                    woService.addUserSignature(this.signatureId);
                }else{
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                WebUtil.addErrorMessage("签名保存失败！");
                response.setStatus(206);
                return;
            }
        }

        if(this.isReturnApproval){
            this.returnApprove();
            return;
        }
        if(this.isDirectDelivery){
            this.directDelivery();
            return;
        }
        if(this.isAgree){
            this.getMultiLevelApprovalPersonByAgree();
        }else{
            this.getMultiLevelApprovalPersionAndSubmitByDisagree();
        }
    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
        this.attachList = null;
    }


    @Getter@Setter
    private  List<ApprovalNode> nodes;
    @Getter@Setter
    private String nodeName;

    public List<ApprovalNode> getApprovalNode(){
        MultiLevelApprovalConfig multiLevelApprovalConfig = multiLevelApprovalService.getApproveWorkflowConfig(selected.getWorkflowConfigApproveUid());
        String approvalItem = multiLevelApprovalConfig.getApprovalItem();
        if (null != approvalItem) {
            List<ApprovalNode> approvalNodes = JSON.parseObject(approvalItem, new TypeReference<List<ApprovalNode>>() {
            });
            Integer approvalLevelNow = selected.getApprovalLevelNow();
            if(approvalLevelNow!=0){
                approvalNodes= approvalNodes.stream().limit(approvalLevelNow-1).collect(Collectors.toList());
            }
            nodes= approvalNodes;
            return nodes;
        }
        return new ArrayList<>();
    }


    public List<ApprovalNode> getNodes(){
        return nodes;
    }

    public boolean  getReturn(){
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalService.getMultiLevelApproval(multiLevelApproval);
        if(multiLevelApprovals.size()>2){
            MultiLevelApproval multiLevelApprovalReturn = multiLevelApprovals.get(multiLevelApprovals.size() - 2);
            if(multiLevelApprovalReturn.getApprovalNodeStatus()==ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_RETURN){
                return  true;
            }
        }else{
            return false;
        }
        return false;
    }

    public boolean isSignatureNurseRendered() {
        return signatureNurseRendered;
    }

    public void setSignatureNurseRendered(boolean signatureNurseRendered) {
        this.signatureNurseRendered = signatureNurseRendered;
    }

    public TreeNode initOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(org.getOrgType() == 4);
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }
    /**
     * 编辑区域部门选择
     * @param event
     */
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        this.confirmBudget.setConfirmApplicationDeptName(orgInfo.getName());
        this.confirmBudget.setConfirmApplicationDeptId(orgInfo.getId());
    }


    public void prepareConfirm(String budgetAssetId) {
        this.budgetAssetId = budgetAssetId;
        this.confirmBudget = new BudgetApplicationAsset();
        BudgetApplicationAsset budgetApplicationAsset = budgetApplicationAssetRepository.findById(budgetAssetId);
        if(budgetApplicationAsset!=null){
            this.confirmBudget.setConfirmApplicationDeptId(budgetApplicationAsset.getConfirmApplicationDeptId());
            if(budgetApplicationAsset.getConfirmApplicationDeptId() != null){
                OrgInfo confirmOrg = orgDao.findById(budgetApplicationAsset.getConfirmApplicationDeptId());
                this.confirmBudget.setConfirmApplicationDeptName(confirmOrg!=null?confirmOrg.getName():null);
                this.confirmBudget.setConfirmApplicationDeptId(confirmOrg!=null?confirmOrg.getId():null);
            }else{
                OrgInfo applicationOrg = orgDao.findById(budgetApplicationAsset.getApplicationDeptId());
                this.confirmBudget.setConfirmApplicationDeptName(applicationOrg!=null?applicationOrg.getName():null);
                this.confirmBudget.setConfirmApplicationDeptId(applicationOrg!=null?applicationOrg.getId():null);

            }
            this.confirmBudget.setConfirmQuantity(budgetApplicationAsset.getConfirmQuantity());
            this.confirmBudget.setConfirmPrice(budgetApplicationAsset.getConfirmPrice());
            this.confirmBudget.setConfirmNationalFinance(budgetApplicationAsset.getConfirmNationalFinance());
            this.confirmBudget.setConfirmProvincialFinance(budgetApplicationAsset.getConfirmProvincialFinance());
            this.confirmBudget.setConfirmNotice(budgetApplicationAsset.getConfirmNotice());
            this.confirmBudget.setConfirmImport(budgetApplicationAsset.getConfirmImport());
            this.confirmBudget.setConfirmMajorFunction(budgetApplicationAsset.getConfirmMajorFunction());
            this.confirmBudget.setConfirmEquipmentCategory(budgetApplicationAsset.getConfirmEquipmentCategory());
            this.confirmBudget.setConfirmParameters(budgetApplicationAsset.getConfirmParameters());
            this.confirmBudget.setConfirmBrand(budgetApplicationAsset.getConfirmBrand());
            this.confirmBudget.setConfirmMaximumPrice(budgetApplicationAsset.getConfirmMaximumPrice());
            this.confirmBudget.setConfirmRemark(budgetApplicationAsset.getConfirmRemark());
        }
    }


    public void saveConfirmInfoBudgetApp() {
        multiLevelApprovalService.saveConfirmInfoBudgetApp(this.purchaseOrderInfo,budgetAssetId,this.confirmBudget);
    }
    /**
     * 申购单信息
     * @return
     */
    public List<BudgetApplicationAsset> applicationDetailConfirm(){
        PurchaseOrderInfo purchaseOrderInfo = purchaseOrderInfoRepository.findById(this.selected.getBusinessUid());
        List<SearchFilter> searchFilterAssets = new ArrayList<>();
        searchFilterAssets.add(new SearchFilter("budgetApplication.purchaseId",
                SearchFilter.Operator.EQ,purchaseOrderInfo.getId()));
        List<BudgetApplicationAsset> budgetApplicationAssets = budgetApplicationAssetRepository.findBySearchFilter(searchFilterAssets,new Sort(Sort.Direction.DESC, "createdDate"));
        if(!CollectionUtils.isEmpty(budgetApplicationAssets)){
            budgetApplicationAssets.stream().filter(baa -> baa.getConfirmApplicationDeptId()!=null).forEach(baa ->{
                OrgInfo confirmOrg = orgDao.findById(baa.getConfirmApplicationDeptId());
                baa.setConfirmApplicationDeptName(confirmOrg!=null?confirmOrg.getName():null);
            });
        }
        return CollectionUtils.isEmpty(budgetApplicationAssets)?null:budgetApplicationAssets;
    }

    public Integer approvaLevelPurchase() {
        if(this.selected == null){
            return null;
        }
        return this.selected.getApprovalLevel();
    }

    public void handleFileUpload(FileUploadEvent event) {
        if(CollectionUtils.isEmpty(attachList)){
            attachList= new ArrayList<>();
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getFile().getContentType().startsWith("image")?"Image":"File";
            blobObject.setBoType(V2_BlobObject.AttachmentType.getKeyByValue("多级审批"));
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getId());
            blobObject.setTenantUid(selected.getTenantUID());
//            blobService.saveBlobObject(blobObject);
            attachList.add(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public List<V2_BlobObject> getFileByBoSubType(String type){
        if(CollectionUtils.isEmpty(attachList)){
            attachList= new ArrayList<>();
        }
        return attachList.stream().filter(item->type.equals(item.getBoSubType())).collect(Collectors.toList());
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList = attachList.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public boolean approvedCompleted(){
       return multiLevelApprovals.stream().anyMatch(item -> item.getApprovalStatus() == 0 || item.getApprovalStatus() == 1);
    }

}
