package com.ge.apm.view.approval;

import com.ge.apm.dao.BudgetApplicationRepository;
import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.AdverseEvents;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.V2_WorkOrder;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

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
        multiLevelApprovalService.getMultiLevelApprovalPersonByAgree(multiLevelApproval,approvalOpinion);
        selected = null;
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
        multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmitByDisagree(multiLevelApproval,approvalOpinion);
        selected = null;
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
}
