package com.ge.apm.service.acquisition;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.AcquisitionPlanStepRepository;
import com.ge.apm.dao.AcquisitionSpecificationRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PlanFilingPeriodRepository;
import com.ge.apm.domain.ConfigItemVO;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.acquisition.*;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.acquisition.AcquisitionPlanEditController;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.util.StringUtils;

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
    private ConfigItemService configItemService;

    @Autowired
    private UaaService uaaService;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public String checkRequire(AcquisitionPlan acquisitionPlan, List<V2_BlobObject> v2BlobObjects){
        StringBuilder sb = new StringBuilder();
        if(StrUtil.isEmpty(acquisitionPlan.getAssetName())){
            sb.append("设备名称 不能为空！\n");
        }
        if(StrUtil.isEmpty(acquisitionPlan.getSpecifications())){
            sb.append("规格 不能为空！\n");
        }
        if(StrUtil.isEmpty(acquisitionPlan.getApplicationType())){
            sb.append("申请类别 不能为空！\n");
        }else{
            if("updates".equals(acquisitionPlan.getApplicationType())){
                if(StrUtil.isEmpty(acquisitionPlan.getUpdatedAssetName())){
                    sb.append("被更新设备 不能为空！\n");
                }
                if(acquisitionPlan.getCurrentEquipmentServiceYears() == null){
                    sb.append("拟更新设备已使用年数（年） 不能为空！\n");
                }
            }
            if("necessities".equals(acquisitionPlan.getApplicationType()) || "general".equals(acquisitionPlan.getApplicationType())){
                if(acquisitionPlan.getNewStaffAllocation() == null){
                    sb.append("新增人员配置（人） 不能为空！\n");
                }
            }
            if("necessities".equals(acquisitionPlan.getApplicationType())){
                if(CollectionUtils.isEmpty(v2BlobObjects)){
                    sb.append("附件信息 不能为空！");
                }
            }
        }
        if(acquisitionPlan.getApplicationPlanCount() == null){
            sb.append("计划采购数量（台/套） 不能为空！\n");
        }
        if(acquisitionPlan.getBudgetUnitPrice() == null ){
            sb.append("预算单价（万元） 不能为空！\n");
        }
        if(acquisitionPlan.getEstimatedServiceYears() == null){
            sb.append("预计使用年限（年） 不能为空！\n");
        }
        if(acquisitionPlan.getSimilarEquipmentCount() == null){
            sb.append("同类设备数量（台/套） 不能为空！\n");
        }

        if(StrUtil.isEmpty(acquisitionPlan.getFundingSource())){
            sb.append("资金来源 不能为空！\n");
        }
        if(StrUtil.isEmpty(acquisitionPlan.getFundingImplemented())){
            sb.append("资金落实（是/否） 不能为空！\n");
        }
        if(StrUtil.isEmpty(acquisitionPlan.getMedicalEquipmentLicense())){
            sb.append("甲乙类大型医疗设备及配置许可证 不能为空！\n");
        }
        if(acquisitionPlan.getSiteRenovationCost() == null){
            sb.append("场地改造成本（万元） 不能为空！\n");
        }
        if(StrUtil.isEmpty(acquisitionPlan.getPurchaseReason())){
            sb.append("采购原因 不能为空！\n");
        }
        if(acquisitionPlan.getMaintenanceCost() == null){
            sb.append("维保费用（万元） 不能为空！\n");
        }
        if(acquisitionPlan.getConsumablesCost() == null){
            sb.append("配套不可单独收费耗材收费占比急采购成本（万元） 不能为空！\n");
        }
        if(acquisitionPlan.getAnnualServiceVolume() == null){
            sb.append("年均服务量（人/次） 不能为空！\n");
        }
        if(acquisitionPlan.getAnnualServiceIncome() == null){
            sb.append("年服务收入总金额（万元） 不能为空！\n");
        }

        return sb.toString();
    }

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

    public void cancelApplication(AcquisitionPlan acquisitionPlan, UserAccount account){
        acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Cancel.name());
        acquisitionPlan.setApplicationStep(9);
        acquisitionPlan.setLastModifiedBy(account.getLoginName());
        acquisitionPlan.setLastModifiedDate(new Date());
        acquisitionPlanRepository.save(acquisitionPlan);
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
        if(result.equals(AcquisitionPlan.ResultEnum.Approved.name())) {
            nodeStatus = AcquisitionPlan.NodeStatusEnum.Approved.name();
            if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name())){
                if(acquisitionPlan.getBudgetTotalAmount().compareTo(new BigDecimal(10)) >= 0){
                    acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name());
                    acquisitionPlan.setApplicationStep(oldStep +1);
                    acquisitionPlan.setDrugCenterUid(account.getUid());
                    function = ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_WORK_GROUP;
                }else {
                    acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Approved.name());
                    acquisitionPlan.setDrugCenterUid(account.getUid());
                    acquisitionPlan.setApplicationStep(9);
                }
            }else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name())){
                if(acquisitionPlan.getBudgetTotalAmount().compareTo(new BigDecimal(50)) >= 0) {
                    acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name());
                    acquisitionPlan.setApplicationStep(oldStep +1);
                    acquisitionPlan.setWorkGroupUid(account.getUid());
                    function = ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_DRUG_CENTER;
                }else{
                    acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Approved.name());
                    acquisitionPlan.setWorkGroupUid(account.getUid());
                    acquisitionPlan.setApplicationStep(9);
                }
            } else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name())){
                acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Approved.name());
                acquisitionPlan.setBureauPartyUid(account.getUid());
                acquisitionPlan.setApplicationStep(9);
            }
            acquisitionPlanRepository.save(acquisitionPlan);
        }else if(result.equals(AcquisitionPlan.ResultEnum.Rejected.name())){
            nodeStatus = AcquisitionPlan.NodeStatusEnum.Rejected.name();
            if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.DrugCenterPending.name())){
                acquisitionPlan.setDrugCenterUid(account.getUid());
            }else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.WorkGroupPending.name())){
                acquisitionPlan.setWorkGroupUid(account.getUid());
            }else if(acquisitionPlan.getApplicationStatus().equals(AcquisitionPlan.ApplicationStatusEnum.BureauPartyPending.name())){
                acquisitionPlan.setBureauPartyUid(account.getUid());
            }
            acquisitionPlan.setApplicationStep(9);//完成
            acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Rejected.name());
        }else if(result.equals(AcquisitionPlan.ResultEnum.SelfReview.name())){
            nodeStatus = AcquisitionPlan.NodeStatusEnum.Approved.name();
            acquisitionPlan.setApplicationStatus(AcquisitionPlan.ApplicationStatusEnum.Approved.name());
            acquisitionPlan.setDrugCenterUid(account.getUid());
            acquisitionPlan.setApplicationStep(9);
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


    public HospitalAreaClassification getHospitalAreaClassification(UserAccount user){
        HospitalAreaClassification  classification = new HospitalAreaClassification();
        ConfigItemForm form = new ConfigItemForm();
        form.setSiteUid(user.getSiteUID());
        form.setKey("HospitalAreaClassification");
        String actualValue = configItemService.getConfigByKeyAndSite(form);
        if(StringUtils.isEmpty(actualValue)){
            //throw  new CustomParameterizedException("'医院区域分类'未配置!");
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(actualValue);
        Set<String> keys = jsonObject.keySet();
        List<MedicalInstitution> result = new ArrayList<>();
        for (String key : keys) {
            List<MedicalInstitution> institutionList =jsonObject.getObject(key, new TypeReference<List<MedicalInstitution>>() {});
            result.addAll(institutionList);
        }
        if(org.springframework.util.CollectionUtils.isEmpty(result)){
            return classification;
        }
        // 单体医院
        List<MedicalInstitution> standaloneHospitals = result.stream().filter(item -> item.getUids() == null).collect(Collectors.toList());
        // 医共体
        List<MedicalInstitution> medicalConsortium = result.stream().filter(item -> !org.springframework.util.CollectionUtils.isEmpty(item.getUids() )).collect(Collectors.toList());
        classification.setStandaloneHospitals(standaloneHospitals);
        classification.setMedicalConsortium(medicalConsortium);
        return classification;
    }
    public List<String> getHospitals(HospitalAreaClassification classification,String hospitalUID){
        Set<String> result = new HashSet<>();

        // 处理 medicalConsortium：找到 uids 包含 hospitalUID 的 consortium，将其全部 uids 加入结果
        classification.getMedicalConsortium().stream()
                .filter(item -> !org.springframework.util.CollectionUtils.isEmpty(item.getUids()))
                .filter(item -> item.getUids().contains(hospitalUID))
                .forEach(item -> result.addAll(item.getUids()));

        // 处理 standaloneHospitals：找到 uid 与 hospitalUID 一致的加入结果
        classification.getStandaloneHospitals().stream()
                .filter(item -> StringUtils.hasText(item.getUid()) && java.util.Objects.equals(item.getUid(), hospitalUID))
                .map(MedicalInstitution::getUid)
                .forEach(result::add);

        return new ArrayList<>(result);
    }


}
