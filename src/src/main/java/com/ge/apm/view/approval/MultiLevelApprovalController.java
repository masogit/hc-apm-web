package com.ge.apm.view.approval;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.core.WechatParam;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private WorkflowService workflowService;
    private BlobFileService blobService;


    private AssetScrapRepository assetScrapDao;
    private AssetDepartmentConvertRepository assetDepartmentConvertRepository;


    @Setter
    @Getter
    private List<MultiLevelApproval> multiLevelApprovals;

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
    private boolean isAgree = true;
    private boolean needSignature = false;

    private boolean isReturnApproval=false;

    private boolean isDirectDelivery=false;

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
        workflowService = WebUtil.getBean(WorkflowService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetScrapDao = WebUtil.getBean(AssetScrapRepository.class);
        assetDepartmentConvertRepository = WebUtil.getBean(AssetDepartmentConvertRepository.class);
    }

    public void getApprovalContentByCurrentApproval(MultiLevelApproval multiLevelApproval) {

        primaryApprovalData = null;
        secondaryApprovalData = null;

        if (null == multiLevelApproval) {
            multiLevelApprovals = null;
            return;
        }

        //multi level approval status
        multiLevelApprovals = multiLevelApprovalService.getMultiLevelApproval(multiLevelApproval);
        businessTypeFlag = multiLevelApproval.getBusinessType();

        //multi level approval content
        switch (businessTypeFlag){
            case ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT:
                V2_WorkOrder work = v2_WorkOrderRepository.findById(multiLevelApproval.getBusinessUid());
                List<V2_WorkOrder> workOrderLists = new ArrayList<V2_WorkOrder>();
                workOrderLists.add(work);
                primaryApprovalData = workOrderLists;
                secondaryApprovalData = woService.getWorkOrderDetails(work.getId());
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
                primaryApprovalData = budgetApplication;
                secondaryApprovalData = budgetApplication.getBudgetAssets();
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP:
                AssetScrap assetScrap = assetScrapDao.findById(multiLevelApproval.getBusinessUid());
                List<AssetScrap> assetScraps = new ArrayList<>();
                assetScraps.add(assetScrap);
                primaryApprovalData = assetScrap;
                secondaryApprovalData =assetScraps;
                break;
            case ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT:
                AssetDepartmentConvert assetDepartmentConvert = assetDepartmentConvertRepository.findById(multiLevelApproval.getBusinessUid());
                List<AssetDepartmentConvert> assetDepartmentConverts = new ArrayList<>();
                assetDepartmentConverts.add(assetDepartmentConvert);
                primaryApprovalData = assetDepartmentConvert;
                secondaryApprovalData =assetDepartmentConvert;
                break;
            default:
                throw new RuntimeException("New Business type not handled yet");
        }
    }

    /**
     * 供备件审批使用--审批同意
     * @return
     */
    public void getMultiLevelApprovalPersonByAgree() throws Exception {
        MultiLevelApproval multiLevelApproval= multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批，勿重复操作！");
            return;
        }
        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApprovalService.getMultiLevelApprovalPersonByAgree(multiLevelApproval,approvalOpinion);
        selected = null;
        this.needSignature = false;
        this.resetSignature();
    }

    /**
     * 供备件审批使用--审批不同意
     * @return
     */
    public void getMultiLevelApprovalPersionAndSubmitByDisagree() {
        MultiLevelApproval multiLevelApproval = multiLevelApprovalRepository.findById(selected.getId());
        boolean approvalIng = multiLevelApproval.getApprovalIng();
        if(approvalIng){
            WebUtil.addWarnMessage("审批流已经被审批,勿重复操作！");
            return;
         }
        if(this.needSignature){
            multiLevelApproval.setAuditSignature(this.signatureId);
        }
        multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmitByDisagree(multiLevelApproval,approvalOpinion);
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
        String token = UserContextService.getAccessToken();
        AdverseEvents events = new AdverseEvents();
        events.setUid(multiLevelApproval.getBusinessUid());
        events.setStatus(6);
        events.setApprovalOpinion(approvalOpinion);
        events.setApprovalResult(approvalResult);
        multiLevelApprovalService.approveAdverseEvents(token, events);
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
        String token = UserContextService.getAccessToken();
        AdverseEvents events = new AdverseEvents();
        events.setUid(multiLevelApproval.getBusinessUid());
        events.setStatus(4);
        events.setApprovalOpinion(approvalOpinion);
        events.setApprovalResult(approvalResult);
        multiLevelApprovalService.approveAdverseEvents(token, events);
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

        int level = 0;
        for (int i=1;i<nodes.size();i++){
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
            nodes= approvalNodes;
            Integer approvalLevelNow = selected.getApprovalLevelNow();
            if(approvalLevelNow!=0){
                approvalNodes= approvalNodes.stream().limit(approvalLevelNow-1).collect(Collectors.toList());

            }

            return approvalNodes;
        }
        return null;
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

}
