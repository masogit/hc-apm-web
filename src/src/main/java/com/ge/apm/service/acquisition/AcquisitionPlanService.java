package com.ge.apm.service.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.AcquisitionPlanStepRepository;
import com.ge.apm.dao.AcquisitionSpecificationRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PlanFilingPeriodRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.AcquisitionPlanStep;
import com.ge.apm.domain.acquisition.AcquisitionSpecification;
import com.ge.apm.domain.acquisition.PlanFilingPeriod;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.acquisition.AcquisitionPlanEditController;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Service
public class AcquisitionPlanService {

    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private PlanFilingPeriodRepository planFilingPeriodRepository;
    @Autowired
    private BlobFileService blobFileService;
    @Autowired
    private AcquisitionPlanRepository acquisitionPlanRepository;
    @Autowired
    private AcquisitionPlanStepRepository acquisitionPlanStepRepository;
    @Autowired
    private AcquisitionSpecificationRepository acquisitionSpecificationRepository;

    @Autowired
    private UaaService uaaService;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");


    public void initCreate(AcquisitionPlan acquisitionPlan, UserAccount userAccount){
        acquisitionPlan.setApplicationDate(new Date());
        acquisitionPlan.setApplicationStep(0);
        acquisitionPlan.setApplicationUserUid(userAccount.getUid());
        acquisitionPlan.setApplicationUserName(userAccount.getName());
        acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.TempSave.name());

        acquisitionPlan.setApplicationHospitalUid(userAccount.getHospitalUID());
        if(StrUtil.isNotEmpty(userAccount.getHospitalUID())){
            OrgInfo orgInfo = orgDao.findByUid(userAccount.getHospitalUID());
            if(orgInfo != null){
                acquisitionPlan.setApplicationHospitalName(orgInfo.getName());
            }
        }
        acquisitionPlan.setCreatedBy(userAccount.getLoginName());
        acquisitionPlan.setCreatedDate(new Date());
        acquisitionPlan.setLastModifiedBy(userAccount.getLoginName());
        acquisitionPlan.setLastModifiedDate(new Date());

        OrgInfo orgInfo = orgDao.findByUid(userAccount.getOrgInfoUID());
        if(orgInfo.getOrgType()==4){
            acquisitionPlan.setApplicationDeptUid(userAccount.getClinicalDeptUID());
            acquisitionPlan.setApplicationDeptName(userAccount.getDepartName());
        }

        List<PlanFilingPeriod> planFilingPeriods = planFilingPeriodRepository.queryEffectConfig(userAccount.getTenantUID(), new Date());
        if(CollectionUtils.isNotEmpty(planFilingPeriods)){
            acquisitionPlan.setApplicationYear(planFilingPeriods.get(0).getYear());
        }
    }

    @Transactional
    public void saveApplication(AcquisitionPlan acquisitionPlan, UserAccount account, List<V2_BlobObject> attachList, List<String> removeFileBlobIds){
        if(acquisitionPlan.getId()==null){
            acquisitionPlan.setTenantUID(account.getTenantUID());
            acquisitionPlan.setInstitutionUID(account.getInstitutionUID());
            acquisitionPlan.setHospitalUID(account.getHospitalUID());
            acquisitionPlan.setSiteUID(account.getSiteUID());
            this.genApplicationNum(acquisitionPlan,"GZ");
        }

        acquisitionPlan = acquisitionPlanRepository.save(acquisitionPlan);
        if(CollectionUtils.isNotEmpty(attachList)){
            for(V2_BlobObject blobObject : attachList){
                if(StrUtil.isEmpty(blobObject.getBoUuid())){
                    blobObject.setBoUuid(acquisitionPlan.getId());
                    blobFileService.saveBlobObject(blobObject);
                }
            }
        }

        //去掉删除的数据
        if(CollectionUtils.isNotEmpty(removeFileBlobIds)){
            removeFileBlobIds.stream().forEach(blobId ->{
                blobFileService.deleteBlobFileById(blobId);
            });
        }
    }

    private void genApplicationNum(AcquisitionPlan acquisitionPlan, String orderType) {
        Object o = acquisitionPlanRepository.getMaxIndex();
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String applicationNum = orderType + simpleDateFormat.format(new Date()) + String.format("%04d", index);
        AcquisitionPlan exist = acquisitionPlanRepository.findByApplicationNo(applicationNum);
        if(exist != null){
            applicationNum = applicationNum+"_1";
        }
        acquisitionPlan.setApplicationNo(applicationNum);
        acquisitionPlan.setApplicationNoIndex(index);
    }

    @Transactional
    public void submitApplication(AcquisitionPlan acquisitionPlan, UserAccount account, List<V2_BlobObject> attachList, List<String> removeFileBlobIds){
        acquisitionPlan.setApplicationStep(1);
        acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name());
        String names = "";
        String uids = "";
        List<UserAccount> users = uaaService.getUsersByApmFunction(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_DRUG_CENTER, account.getTenantUID());
        if(CollectionUtils.isNotEmpty(users)){
            names = users.stream()
                    .map(UserAccount::getName)
                    .collect(Collectors.joining(","));
            uids = users.stream()
                    .map(UserAccount::getUid)
                    .collect(Collectors.joining(","));
        }
        saveApplication(acquisitionPlan, account, attachList, removeFileBlobIds);

        AcquisitionPlanStep acquisitionPlanStep = new AcquisitionPlanStep();
        acquisitionPlanStep.setStep(1);
        acquisitionPlanStep.setPid(acquisitionPlan.getId());
        acquisitionPlanStep.setAuditStatus(acquisitionPlan.getApplicationStatus());
        acquisitionPlanStep.setNodeStatus(AcquisitionPlan.NodeStatusEnum.Pending.name());
        acquisitionPlanStep.setAuditComment("");
        acquisitionPlanStep.setCreatedBy(account.getName());
        acquisitionPlanStep.setCreatedDate(new Date());
        acquisitionPlanStep.setPendingAuditUserUid(uids);
        acquisitionPlanStep.setPendingAuditUserName(names);

        acquisitionPlanStepRepository.save(acquisitionPlanStep);
    }

    @Transactional
    public void removeAttachment(List<V2_BlobObject> attachList){
        for(V2_BlobObject blobObject : attachList){
            blobFileService.deleteBlobFileById(blobObject.getId());
        }
    }

    public void auditApplication(AcquisitionPlan acquisitionPlan, UserAccount account,String result, String comment){
        Integer oldStep = acquisitionPlan.getApplicationStep();
        String oldStatus = acquisitionPlan.getApplicationStatus();
        String nodeStatus = AcquisitionPlan.NodeStatusEnum.Approved.name();
        String names = "";
        String uids = "";
        ApmFunction function = ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_WORK_GROUP;
        if(result.equals(AcquisitionPlan.ResultEnum.SelfReview.name()) || result.equals(AcquisitionPlan.ResultEnum.Approved.name())) {
            nodeStatus = AcquisitionPlan.NodeStatusEnum.Approved.name();
            if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name())){
                acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name());
                acquisitionPlan.setApplicationStep(oldStep +1);
                acquisitionPlan.setDrugCenterUid(account.getUid());
                function = ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_WORK_GROUP;
            }else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name())){
                acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name());
                acquisitionPlan.setApplicationStep(oldStep +1);
                acquisitionPlan.setWorkGroupUid(account.getUid());
                function = ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_DRUG_CENTER;
            } else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name())){
                acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Approved.name());
                acquisitionPlan.setBureauPartyUid(account.getUid());
                acquisitionPlan.setApplicationStep(9);
            }
            acquisitionPlanRepository.save(acquisitionPlan);
        }else if(result.equals(AcquisitionPlan.ResultEnum.Rejected.name())){
            acquisitionPlan.setApplicationStep(9);//完成
            acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Rejected.name());
            nodeStatus = AcquisitionPlan.NodeStatusEnum.Rejected.name();
            if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name())){
                acquisitionPlan.setDrugCenterUid(account.getUid());
            }else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name())){
                acquisitionPlan.setWorkGroupUid(account.getUid());
            }else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name())){
                acquisitionPlan.setBureauPartyUid(account.getUid());
            }
        }
        if(acquisitionPlan.getApplicationStep() != 9){
            List<UserAccount> users = uaaService.getUsersByApmFunction(function, account.getTenantUID());
            if(CollectionUtils.isNotEmpty(users)){
                names = users.stream()
                        .map(UserAccount::getName)
                        .collect(Collectors.joining(","));
                uids = users.stream()
                        .map(UserAccount::getUid)
                        .collect(Collectors.joining(","));
            }
        }

        acquisitionPlanRepository.save(acquisitionPlan);

        //获取上一个
        AcquisitionPlanStep oldPlanStep = acquisitionPlanStepRepository.findFirstByPidOrderByCreatedDateDesc(acquisitionPlan.getId());
        if(oldPlanStep != null){
            oldPlanStep.setNodeStatus(nodeStatus);
            oldPlanStep.setAuditResult(result);
            oldPlanStep.setAuditDate(new Date());
            oldPlanStep.setAuditComment(comment);
            oldPlanStep.setActualAuditUserUid(account.getUid());
            oldPlanStep.setActualAuditUserName(account.getName());
            oldPlanStep.setLastModifiedBy(account.getName());
            oldPlanStep.setLastModifiedDate(new Date());
            acquisitionPlanStepRepository.save(oldPlanStep);
        }

        if(acquisitionPlan.getApplicationStep() != 9){
            //创建下一个节点
            AcquisitionPlanStep acquisitionPlanStep = new AcquisitionPlanStep();
            acquisitionPlanStep.setPid(acquisitionPlan.getId());
            acquisitionPlanStep.setPid(acquisitionPlan.getId());
            acquisitionPlanStep.setStep(acquisitionPlan.getApplicationStep());
            acquisitionPlanStep.setActualAuditUserUid(null);
            acquisitionPlanStep.setActualAuditUserName(null);
            acquisitionPlanStep.setAuditStatus(acquisitionPlan.getApplicationStatus());
            acquisitionPlanStep.setNodeStatus(AcquisitionPlan.NodeStatusEnum.Pending.name());
            acquisitionPlanStep.setCreatedBy(account.getName());
            acquisitionPlanStep.setCreatedDate(new Date());
            acquisitionPlanStep.setPendingAuditUserUid(uids);
            acquisitionPlanStep.setPendingAuditUserName(names);

            acquisitionPlanStepRepository.save(acquisitionPlanStep);
        }

    }

    public List<AcquisitionPlanStep> queryApplicationStep(String pid){
        return acquisitionPlanStepRepository.findByPid(pid);
    }

    /**
     * 根据采购医疗设备分类（一级分类、二级分类）构建树形结构
     * 树结构：根节点 → 一级分类（不可选） → 二级分类（可选）
     *
     * @param searchKeyword 搜索关键字，在数据库层按一级分类或二级分类名称模糊匹配，为空时返回全部
     */
    public TreeNode getSpecificationTree(String searchKeyword) {
        List<AcquisitionSpecification> specList;
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            specList = acquisitionSpecificationRepository.findByCategoryKeyword(searchKeyword.trim());
        } else {
            specList = (List<AcquisitionSpecification>) acquisitionSpecificationRepository.findAll();
        }

        // 按一级分类分组，保持插入顺序
        Map<String, List<AcquisitionSpecification>> groupedMap = new LinkedHashMap<>();
        for (AcquisitionSpecification spec : specList) {
            String firstLevel = spec.getFirstLevelCategory();
            if (firstLevel != null) {
                groupedMap.computeIfAbsent(firstLevel, k -> new java.util.ArrayList<>()).add(spec);
            }
        }

        // 创建根节点
        TreeNode rootNode = new DefaultTreeNode("root", "设备分类", null);
        rootNode.setExpanded(true);

        // 构建一级分类节点和二级分类子节点
        for (Map.Entry<String, List<AcquisitionSpecification>> entry : groupedMap.entrySet()) {
            String firstLevelName = entry.getKey();
            List<AcquisitionSpecification> children = entry.getValue();

            TreeNode firstLevelNode = new DefaultTreeNode("firstLevel", firstLevelName, rootNode);
            firstLevelNode.setSelectable(false);
            firstLevelNode.setExpanded(true);

            for (AcquisitionSpecification spec : children) {
                if (spec.getSecondLevelCategory() != null && !spec.getSecondLevelCategory().trim().isEmpty()) {
                    TreeNode secondLevelNode = new DefaultTreeNode("secondLevel", spec, firstLevelNode);
                    secondLevelNode.setSelectable(true);
                }
            }
        }

        return rootNode;
    }
}
