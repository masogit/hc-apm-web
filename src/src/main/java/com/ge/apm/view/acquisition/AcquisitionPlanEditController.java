package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.AcquisitionPlanStep;
import com.ge.apm.domain.acquisition.AcquisitionSpecification;
import com.ge.apm.service.acquisition.AcquisitionPlanService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ManagedBean
@ViewScoped
public class AcquisitionPlanEditController extends GenericCRUDUUIDController<AcquisitionPlan> {


    public final static String CREATE_ACTION = "Create";
    public final static String EDIT_ACTION = "Edit";
    private UserAccount userAccount;

    private AcquisitionPlanRepository dao;
    private AcquisitionPlanService acquisitionPlanService;
    private BlobFileService blobService;
    private UaaService uaaService;
    private String selectedId;
    @Getter @Setter
    private List<V2_BlobObject> attachList;
    @Getter @Setter
    private List<String> removeFileBlobId = new ArrayList<>();

    @Getter @Setter
    private boolean canEdit = true;
    @Getter @Setter
    private List<AcquisitionPlanStep> acquisitionPlanStepList;
    @Getter @Setter
    private String searchDeptName;
    @Getter @Setter
    private String searchSpecifications;
    @Getter @Setter
    private TreeNode orgTreeEdit;
    @Getter @Setter
    private OrgInfo selectedDept;
    @Getter @Setter
    private OrgInfo org;
    @Getter @Setter
    private TreeNode orgTreeSpecifications;
    @Getter @Setter
    private AcquisitionSpecification selectedSpecifications;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanRepository.class);
        acquisitionPlanService = WebUtil.getBean(AcquisitionPlanService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userAccount = UserContextService.getCurrentUserAccount();
        String action = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        selectedId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "id");
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(userAccount.getOrgInfoUID(), userAccount.getUid(), 4);
        orgTreeEdit = initOrgTree(treeNodeEdit);  //
        orgTreeSpecifications = acquisitionPlanService.getSpecificationTree("");

        if (AcquisitionPlanEditController.CREATE_ACTION.equals(action)) {
            prepareCreate();
        } else if (AcquisitionPlanEditController.EDIT_ACTION.equals(action)) {
            prepareEdit();
            if(StrUtil.isNotEmpty(selected.getApplicationStatus())){
                canEdit = false;
            }
            if(StrUtil.isNotEmpty(selected.getApplicationStatus())){
                acquisitionPlanStepList = acquisitionPlanService.queryApplicationStep(selected.getId());
            }
            attachList = blobService.findBlobObjectsByBoUid(selected.getId());
        }
    }



    public void prepareCreate(){
        selected = new AcquisitionPlan();
        acquisitionPlanService.initCreate(selected, userAccount);
        attachList = new ArrayList<>();
    }
    public void prepareEdit(){
        selected = dao.findOne(selectedId);
    }

    public void calcAmount(){
        if(selected.getBudgetUnitPrice() != null && selected.getApplicationPlanCount() != null){
            Integer applicationPlanCount = selected.getApplicationPlanCount();
            BigDecimal budgetUnitPrice = selected.getBudgetUnitPrice();
            BigDecimal amount = new BigDecimal(applicationPlanCount).multiply(budgetUnitPrice);
            selected.setBudgetTotalAmount(amount);
        }
        calcDepreciationCost();
    }


    //计算折旧成本
    public void calcDepreciationCost(){
        if(selected.getBudgetUnitPrice() != null && selected.getEstimatedServiceYears() != null){
            BigDecimal budgetTotalPrice = selected.getBudgetTotalAmount();
            Integer estimatedServiceYears = selected.getEstimatedServiceYears();
            BigDecimal depreciationCost = budgetTotalPrice.divide(
                    new BigDecimal(estimatedServiceYears),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
            selected.setDepreciationCost(depreciationCost);
        }
    }

    public void calcEfficiencyWeight(){
        if(selected.getAnnualServiceIncome() == null ){
           selected.setAnnualOperationEfficiencyWeight(new BigDecimal(0));
           return;
        }
        BigDecimal annualServiceIncome = selected.getAnnualServiceIncome();
        BigDecimal siteRenovationCost = selected.getSiteRenovationCost() == null ? new BigDecimal(0) : selected.getSiteRenovationCost();
        Integer  newStaffAllocation = selected.getNewStaffAllocation() == null ? 0 : selected.getNewStaffAllocation();
        BigDecimal depreciationCost = selected.getDepreciationCost() == null ? new BigDecimal(0) : selected.getDepreciationCost();
        BigDecimal maintenanceCost = selected.getMaintenanceCost() == null ? new BigDecimal(0) : selected.getMaintenanceCost();
        BigDecimal consumablesCost = selected.getConsumablesCost() == null ? new BigDecimal(0) : selected.getConsumablesCost();

        BigDecimal totalCost = siteRenovationCost
                .add(new BigDecimal(newStaffAllocation).multiply(new BigDecimal("15")))
                .add(depreciationCost)
                .add(maintenanceCost)
                .add(consumablesCost);

        if(totalCost.compareTo(BigDecimal.ZERO) > 0){
            BigDecimal efficiencyWeight = annualServiceIncome.divide(
                    totalCost,
                    2,
                    java.math.RoundingMode.HALF_UP
            );
            selected.setAnnualOperationEfficiencyWeight(efficiencyWeight);
        } else {
            selected.setAnnualOperationEfficiencyWeight(BigDecimal.ZERO);
        }
    }


    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject ) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ACQUISITION);
           /* if (Optional.ofNullable(selected.getId()).isPresent()) { // 编辑
                blobObject.setBoUuid(selected.getBudgetAssets().iterator().next().getId());
            }*/
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
    public void removeAttachmentById(String blobId) {
        this.removeFileBlobId.add(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }


    public List<Integer> getYears(){
        List<Integer> list = IntStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());
        return list;
    }

    public String backList() {
        return "List.xhtml?faces-redirect=true";
    }

    public String saveApplication(){
        acquisitionPlanService.saveApplication(selected, userAccount, attachList, removeFileBlobId);
        return "List.xhtml?faces-redirect=true";
    }

    public String submitApplication() {
        acquisitionPlanService.submitApplication(selected, userAccount, attachList, removeFileBlobId);
        return "List.xhtml?faces-redirect=true";
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedDept = (OrgInfo) node.getData();
        this.selected.setApplicationDeptName(selectedDept.getName());
        this.selected.setApplicationDeptUid(selectedDept.getUid());
    }
    public void onSelectSpecificationsTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedSpecifications = (AcquisitionSpecification) node.getData();
        this.selected.setSpecifications(selectedSpecifications.getSecondLevelCategory());
        this.selected.setApplicationDeptUid(selectedSpecifications.getId());
    }

    public void treeSearchSpecificationsCondition(){
        orgTreeSpecifications = acquisitionPlanService.getSpecificationTree(searchSpecifications);
    }

    public void treeSearchCondition(){
        TreeNode treeNodeEdit = null;
        if(StrUtil.isEmpty(searchDeptName)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(userAccount.getOrgInfoUID(), userAccount.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(userAccount.getOrgInfoUID(), userAccount.getUid(), 4, searchDeptName);
        }
        orgTreeEdit = initOrgTree(treeNodeEdit);
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

    @Setter @Getter
    List<AssetInfo> selectedAssets = new ArrayList();

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selectedAssets = (List<AssetInfo>) eventObject;
        if (CollectionUtils.isEmpty(selectedAssets)) {
            return;
        }
        String assetIDs = selectedAssets.stream().map(item -> item.getUid()).collect(Collectors.joining(","));
        String names = selectedAssets.stream().map(item -> item.getName()).collect(Collectors.joining(","));

        selected.setUpdatedAssetUid(assetIDs);
        selected.setUpdatedAssetName(names);
    }

    @Override
    protected GenericRepositoryUUID<AcquisitionPlan> getDAO() {
        return dao;
    }
}
