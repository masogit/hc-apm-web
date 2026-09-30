package com.ge.apm.service.wo;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.UserContext;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 *
 * @author 212547631
 */
@Service
public class WorkOrderService {
    private static final Logger logger = Logger.getLogger(WorkOrderService.class);

    @Autowired
    private TenantInfoRepository siteDao;
    @Autowired
    private WorkOrderStepRepository stepDao;

    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    private WorkOrderStepRepository workOrderStepRepository;

    @Autowired
    private I18nMessageRepository i18nMessageRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private OrgInfoRepository orgDao;


    public List<WorkOrder> findWorkOrderByStatus(int status)throws Exception{
        List<WorkOrder> byStatus = workOrderRepository.findByStatus(status);
        return byStatus;
    }


    public Page<WorkOrder> findWorkOrderOfficeByStatus(HttpServletRequest request,Integer pageSize, Integer pageNum)throws Exception{
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageNum, pageSize, sort);
        Page<WorkOrder> byStatus = workOrderRepository.findOfficeWorkOrderList(ua.getId(),Integer.parseInt(request.getParameter("status")),pageRequest);
        return byStatus;
    }


    public  Page<WorkOrder>  findWorkOrderByCon(HttpServletRequest request, Integer pageSize, Integer pageNum){
        UserAccount ua = UserContext.getCurrentLoginUser(request);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageNum, pageSize, sort);
        return workOrderRepository.findByRequestorIdAndStatusOrderByIdDesc(ua.getId(), Integer.parseInt(request.getParameter("status")), pageRequest);

    }
    //public List<WorkOrder>

    public  List<WorkOrder>  findWorkOrderByContest(Integer requestorId){
        return workOrderRepository.findByRequestorId(requestorId);
    }
    
    public List<WorkOrder> findWorkOrderByAssetIdAndStatus(Integer assetId){
        return workOrderRepository.findByAssetIdAndStatusOrderByIdDesc(assetId, 1);
    }

    public WorkOrderStep initWorkOrderCurrentStep(WorkOrder wo){
        WorkOrderStep woStep = new WorkOrderStep();

        woStep.setWorkOrderId(wo.getId());
        woStep.setSiteId(wo.getSiteId());

        if(wo.getCurrentPersonId()!=null) woStep.setOwnerId(wo.getCurrentPersonId());
        if(wo.getCurrentPersonName()!=null) woStep.setOwnerName(wo.getCurrentPersonName());

        woStep.setStepId(wo.getCurrentStepId());
        woStep.setStepName(WebUtil.getMessage("woSteps"+"-"+wo.getCurrentStepId()));

        woStep.setStartTime(TimeUtil.now());

        return woStep;
    }

    public String findClosedWorkOrder(){
        return "success";
    }

    /**
     * depend on dispatch mode to create work_order
     * @param woc 
     * 1: 专人派工  via WorkOrderDispatcher role to find the users and the users can see the workOrder, workOrder.currentPersonId will be -1. The all users will get msg
     * 2: 抢单  via AssetStaff role to find users, and users will get msg, workOrder.currentPersonId will be -1, there will no step for assign
     * 3: 自动派工  will get the first user by the asset_owner_id of asset_info and there will no step for assign
     */
    private void dispatchMode(WorkflowConfig woc, WorkOrder wo, AssetInfo ai) {
        int dispatchMode = woc.getDispatchMode();
        int curPerId=-1, curStepId=3;
        String curPerName="无";
        switch(dispatchMode) {
            case 1: curStepId=2;
                if (null != woc.getDispatchUserId()) {
                    curPerId=woc.getDispatchUserId(); curPerName=woc.getDispatchUserName();
                } else if (null != woc.getDispatchUserId2()) {
                    curPerId=woc.getDispatchUserId2(); curPerName=woc.getDispatchUserName2();
                } 
                break;
            case 3: 
                if (null != ai.getAssetOwnerId()) {
                    curPerId=ai.getAssetOwnerId(); curPerName=ai.getAssetOwnerName();
                } else if (null != ai.getAssetOwnerId2()) {
                    curPerId=ai.getAssetOwnerId2(); curPerName=ai.getAssetOwnerName2();
                } 
        }
        wo.setCurrentPersonId(curPerId);
        wo.setCurrentPersonName(curPerName);
        wo.setCurrentStepId(curStepId);
        wo.setCurrentStepName(i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps",Integer.toString(curStepId)).getValueZh()) ;
    }


    @Transactional
    public void signWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws  Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        updateEndTime(wo, "签到");
        UserAccount currentUser = UserContext.getCurrentLoginUser(request);
        WorkOrderStep wds = initWorkOrderStep(currentUser, wo);
        workOrderStepRepository.save(wds);
//        sendWoMsgs(wo, );
    }
    @Transactional
    public void feedbackWorkOrder(HttpServletRequest request, WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        wo.setFeedbackComment(wopo.getDesc());
        wo.setFeedbackRating(Integer.parseInt(wopo.getFeedbackRating()));
        workOrderRepository.save(wo);
    }
    
    @Transactional
    private void workOrderUpdate(HttpServletRequest request, WorkOrder wo)throws  Exception{
        UserAccount currentUserAccount = UserContext.getCurrentLoginUser(request);
        wo.setCurrentPersonId(currentUserAccount.getId());
        wo.setCurrentPersonName(currentUserAccount.getName());
        int currentStepId = wo.getCurrentStepId();
        wo.setCurrentStepId(currentStepId+1);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId+1)).getValueZh();
        wo.setCurrentStepName(stepName);
        workOrderRepository.save(wo);
    }

    private WorkOrderStep initWorkOrderStep(UserAccount currentUserAccount, WorkOrder wo ) throws Exception{
        WorkOrderStep wds = new WorkOrderStep();
        wds.setOwnerId(currentUserAccount.getId());
        wds.setOwnerName(currentUserAccount.getName());
        wds.setStartTime(new Date());
        wds.setWorkOrderId(wo.getId());
        wds.setSiteId(wo.getSiteId());
        int currentStepId = wo.getCurrentStepId();
        wds.setStepId(currentStepId);
        String stepName = i18nMessageRepository.getByMsgTypeAndMsgKey("woSteps", String.valueOf(currentStepId)).getValueZh();
        wds.setStepName(stepName);
        return wds;
    }
    @Transactional
    private WorkOrderStep updateEndTime(WorkOrder wo, String desc)throws Exception{
        //2.1  update endtime in wos for last step workorder
        List<WorkOrderStep> wosList =workOrderStepRepository.getByWorkOrderIdAndStepId(wo.getId(),wo.getCurrentStepId());
        if(wosList.size()>0){
            WorkOrderStep wos = wosList.get(0);
            wos.setEndTime(new Date());
            wos.setDescription(desc);
            return workOrderStepRepository.save(wos);
        }else{
            throw new Exception("work order step missing");
        }

    }
    
    private WorkOrder initWorkOrder(WorkOrderPoJo wop,UserAccount usr,List<WorkOrder> reopenWorkOrder) throws Exception{

        WorkOrder neWorkOrder = new WorkOrder();
        AssetInfo ai = assetInfoRepository.getById(wop.getAssetId());
        if(ai == null){
            throw new Exception("该资产暂时还没有档案");
        }
        neWorkOrder.setSiteId(ai.getSiteId());
        neWorkOrder.setHospitalId(ai.getHospitalId());
        OrgInfo org = orgDao.findById(ai.getHospitalId());
        if (org != null) {
            neWorkOrder.setHospitalName(org.getName());
        }
        neWorkOrder.setAssetId(wop.getAssetId());
        neWorkOrder.setAssetName(ai.getName());
        // 科室
        neWorkOrder.setFromDeptId(usr.getOrgInfoId());
        OrgInfo dept = orgDao.findById(usr.getOrgInfoId());
        if (dept != null) {
            neWorkOrder.setFromDeptName(dept.getName());
        }
        neWorkOrder.setRequestorId(usr.getId());
        neWorkOrder.setRequestorName(usr.getName());
        neWorkOrder.setRequestTime(new Date());
        neWorkOrder.setRequestReason("".equals(wop.getReason())?"参见语音":wop.getReason());
        neWorkOrder.setCasePriority(Integer.parseInt(wop.getPriority()));
        if (wop.getConfirmedDownTime() != null && !"".equals(wop.getConfirmedDownTime())){
            Date confirmedDownTime = null;
            try{
                confirmedDownTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wop.getConfirmedDownTime());
            }catch(Exception e) {
                confirmedDownTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wop.getConfirmedDownTime()+":00");
            }
            neWorkOrder.setConfirmedDownTime(confirmedDownTime);
        }
        
        if(reopenWorkOrder != null && reopenWorkOrder.size()>0){
            neWorkOrder.setParentWoId(reopenWorkOrder.get(0).getId());
        }
        neWorkOrder.setTotalManHour(0);//gl:----?
        neWorkOrder.setTotalPrice(0.0);//gl:----?

        neWorkOrder.setStatus(1);  //gl: -- 1-在修 / 2-完成 / 3-取消
        neWorkOrder.setFeedbackRating(0);
        
        return neWorkOrder;
    }
    @Transactional
    public void saveWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep, WorkOrderStep nextWoStep) throws RuntimeException{
        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);
        if(wo!=null){
            try{
                wo.setCurrentStepName(WebUtil.getMessage("woSteps"+"-"+wo.getCurrentStepId()));
                woDao.save(wo);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save WorkOrder:"+ex.getMessage());
            }
        }
        if(currentWoStep!=null){
            if(wo!=null){
                currentWoStep.setWorkOrderId(wo.getId());
            }

            try{
                currentWoStep.removeEmptyStepDetailRecord();
                woStepDao.save(currentWoStep);
                //保存step后会连带保存detail，再计算order上面的工时和总价
                saveTotalTimeAndPrice(woDao, wo, currentWoStep);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save current WorkOrderStep:"+ex.getMessage());
            }
        }
        if(nextWoStep!=null){
            if(wo!=null){
                nextWoStep.setWorkOrderId(wo.getId());
            }

            try{
                nextWoStep.removeEmptyStepDetailRecord();
                woStepDao.save(nextWoStep);
            }
            catch(Exception ex){
                throw new RuntimeException("Failed to save next WorkOrderStep:"+ex.getMessage());
            }
        }
    }

    protected void checkWorkOrderPersonNames(WorkOrder wo){
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        UserAccount user;

        user = uaaService.getUserById(wo.getRequestorId());
        if(user!=null)
            wo.setRequestorName(user.getName());
        else
            wo.setRequestorName("N/A");

        user = uaaService.getUserById(wo.getCurrentPersonId());
        if(user!=null)
            wo.setCurrentPersonName(user.getName());
        else
            wo.setCurrentPersonName("N/A");
    }

    public void createWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        finishWorkOrderStep(wo, currentWoStep);
    }

    public void finishWorkOrderStep(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        // update current Work Order Step
        currentWoStep.setEndTime(TimeUtil.now());

        // initiate next Work Order Step
        wo.setCurrentStepId(currentWoStep.getStepId()+1);
        checkWorkOrderPersonNames(wo);

        WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);

        try{
            saveWorkOrderStep(wo, currentWoStep, nextWoStep);
        }
        catch(Exception ex){
            // rollback WorkOrder changes
            wo.setCurrentStepId(currentWoStep.getStepId());
            wo.setCurrentPersonId(currentWoStep.getOwnerId());
            wo.setCurrentPersonName(currentWoStep.getOwnerName());

            throw ex;
        }
        //do wechat push
//        sendTemplateMsg(wo, currentWoStep, nextWoStep);

        //judge whether continue, if auto_stepX is true then continue
        int currentStep = wo.getCurrentStepId();
        if ( wo.getSiteId() == null) return;
        TenantInfo site = siteDao.findById(wo.getSiteId());
        if (site == null) return;
        List<WorkOrderStep> list = stepDao.getByWorkOrderIdAndStepId(wo.getId(), currentStep);
        if (list.size() == 0) return;
        for(WorkOrderStep step : list) {
            if (step.getEndTime() == null)
                currentWoStep = step;
        }
        switch (currentStep){
            case 2:if(site.getWfAutoStep2()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 3:if(site.getWfAutoStep3()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 4:if(site.getWfAutoStep4()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 5:if(site.getWfAutoStep5()){finishWorkOrderStep(wo, currentWoStep);}break;
            case 6:if(site.getWfAutoStep6()){closeWorkOrder(wo, currentWoStep);}break;
            default:;
        }
    }

    public void closeWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        wo.setCurrentStepId(6);
        wo.setStatus(2);
        currentWoStep.setEndTime(TimeUtil.now());

        WorkOrderRepository woDao = WebUtil.getBean(WorkOrderRepository.class);
        WorkOrderStepRepository woStepDao = WebUtil.getBean(WorkOrderStepRepository.class);

        try{
            if (Objects.equals(currentWoStep.getStepId(), 6)){
                saveWorkOrderStep(wo, currentWoStep, null);
            } else {
                WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);
                nextWoStep.setEndTime(TimeUtil.now());
                saveWorkOrderStep(wo, currentWoStep, nextWoStep);
            }
        }
        catch(Exception ex){
            throw ex;
        }
    }

    @Transactional
    public void transferWorkOrder(WorkOrder wo, WorkOrderStep currentWoStep) throws Exception{
        checkWorkOrderPersonNames(wo);
        WorkOrderStep nextWoStep = initWorkOrderCurrentStep(wo);

        try{
            saveWorkOrderStep(wo, currentWoStep, nextWoStep);
        }
        catch(Exception ex){
            throw ex;
        }
        //do wechat push
//        sendTemplateMsg(wo, currentWoStep, null);
    }

    private void saveTotalTimeAndPrice(WorkOrderRepository woDao, WorkOrder wo, WorkOrderStep wos) {
        int totalManHour = wo.getTotalManHour();
        double totalPrice = wo.getTotalPrice();
        if ( wo.getSiteId() == null) return;
        TenantInfo site = siteDao.findById(wo.getSiteId());
        if (site == null) return;
        double hourPrice = site.getManhourPrice()==null?0.0:site.getManhourPrice();
        List<WorkOrderStepDetail> details = wos.getStepDetails();
        if (details == null || details.isEmpty()) return;
        for (WorkOrderStepDetail detail : details) {
            totalManHour += detail.getManHours()==null?0:detail.getManHours();
            totalPrice = totalPrice + (detail.getManHours()==null?0:detail.getManHours())*hourPrice
                    + (detail.getAccessoryQuantity()==null?0:detail.getAccessoryQuantity())*(detail.getAccessoryPrice()==null?0.0:detail.getAccessoryPrice())
                    + (detail.getOtherExpense()==null?0.0:detail.getOtherExpense());
        }
        wo.setTotalManHour(totalManHour);
        wo.setTotalPrice(totalPrice);
        woDao.save(wo);
    }


    

    @Transactional
    public void estimatedTime(WorkOrderPoJo wopo)throws Exception{
        Integer woId= Integer.valueOf(wopo.getWoId());
        WorkOrder wo = workOrderRepository.getById(woId);
        Date estimatedCloseTime = null;
        try{
            estimatedCloseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wopo.getEstimatedCloseTime());
        }catch(Exception e) {
            estimatedCloseTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(wopo.getEstimatedCloseTime()+":00");
        }
        wo.setEstimatedCloseTime(estimatedCloseTime);
        workOrderRepository.save(wo);
    }

}
