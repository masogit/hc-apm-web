package com.ge.apm.view.budget;

import com.ge.apm.dao.MaintenanceContractApplyPurchaseRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.budget.MaintenanceContractApplyPurchase;
import com.ge.apm.domain.budget.MaintenanceContractApplyPurchaseAsset;
import com.ge.apm.domain.budget.MaintenanceContractApplyPurchaseStatusVo;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.budget.MaintenanceContractApplyPurchaseService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ViewScoped
@ManagedBean(name = "mcApplyPurchaseEditController")
public class MaintenanceContractApplyPurchaseEditController extends GenericCRUDUUIDController<MaintenanceContractApplyPurchase> {

    private static Logger logger = LoggerFactory.getLogger(MaintenanceContractApplyPurchaseEditController.class);

    private MaintenanceContractApplyPurchaseService mcApplyPurchaseService;
    private MaintenanceContractApplyPurchaseRepository dao;
    private OrgInfoRepository orgDao;
    private UaaService uaaService;
    private BlobFileService blobService;
    @Getter @Setter
    private UserAccount account = UserContextService.getCurrentUserAccount();
    @Getter @Setter
    private OrgInfo selectedDept;
    @Getter @Setter
    private String searchDeptName;
    @Getter  @Setter
    private TreeNode orgTree;
    @Getter  @Setter
    private TreeNode orgTreeSearch;
    @Getter  @Setter
    private OrgInfo org;
    @Getter @Setter
    private MaintenanceContractApplyPurchaseAsset selectedApplyPurchaseAsset;
    @Getter @Setter
    private List<MaintenanceContractApplyPurchaseAsset> applyPurchaseAssetList = new ArrayList<>();
    @Getter @Setter
    private AssetInfo selectedAssetInfo;
    @Getter  @Setter
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();
    @Getter  @Setter
    private List<AssetInfo> assetInfoList ;
    @Getter @Setter
    private List<V2_BlobObject> v2_blobObjectList;
    @Getter @Setter
    private List<String> removeFileBlobId = new ArrayList<>();
    @Getter @Setter
    private String selectedId;

    @Getter @Setter
    private MaintenanceContractApplyPurchaseStatusVo applyPurchaseStatusVo;
    @Getter  @Setter
    private Integer tabIndex = 0;
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();;


    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(MaintenanceContractApplyPurchaseRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        mcApplyPurchaseService = WebUtil.getBean(MaintenanceContractApplyPurchaseService.class, "mcApplyPurchaseService");
        blobService = WebUtil.getBean(BlobFileService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        org = orgDao.findById(account.getOrgInfoId());
        applyPurchaseStatusVo = new MaintenanceContractApplyPurchaseStatusVo();
        String actionName = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        applyPurchaseStatusVo.setActionName(actionName);
        this.selectedId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "selectedId");
        String tabIndexStr = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "tabIndex");
        if(StringUtils.isNotBlank(tabIndexStr)){
            this.tabIndex = Integer.parseInt(tabIndexStr);
        }
        if (MaintenanceContractApplyPurchaseService.CREATE_ACTION.equals(applyPurchaseStatusVo.getActionName())) {
            selectedDept = null;
            selected = new MaintenanceContractApplyPurchase();
            mcApplyPurchaseService.initCreateObject(selected, account);
            v2_blobObjectList = new ArrayList<>();
        } else if (MaintenanceContractApplyPurchaseService.EDIT_ACTION.equals(applyPurchaseStatusVo.getActionName())) {
            selected = dao.findById(selectedId);
            v2_blobObjectList = blobService.findBlobObjectsByBoUid(selected.getId());
            applyPurchaseAssetList = mcApplyPurchaseService.queryPurchaseAssetById(selected.getId());
            if(!MaintenanceContractApplyPurchase.ApplicationStepEnum.SubmitPending.name().equals(selected.getApplicationStep())){
                applyPurchaseStatusVo.setCanEdit(false);
            }
            if(!account.getUid().equals(selected.getCreatedByUid())){
                applyPurchaseStatusVo.setCanEdit(false);
            }
            mcApplyPurchaseService.fieldRender(applyPurchaseStatusVo, selected);
        }
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        orgTree = initOrgTree(treeNodeEdit);
        TreeNode treeNodeSearch = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(account.getSiteUID(), null);
        orgTreeSearch = initOrgTree(treeNodeSearch);
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
            node.setSelectable(Objects.equals(org.getOrgType(), 4));
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        selectedDept = orgInfo;
        this.selected.setApplicationDeptName(selectedDept.getName());
        this.selected.setApplicationDept(selectedDept);
        this.selected.setApplicationDeptId(selectedDept.getId());
    }

    public void treeSeachCondition(){
        TreeNode treeNodeEdit = null;
        if(StrUtil.isEmpty(searchDeptName)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4, searchDeptName);
        }
        orgTree = initOrgTree(treeNodeEdit);
    }

    public void prepareAssetAdd(){
        if(selected.getApplicationDept() != null){
            assetSearchForm.setApplicationDeptId(selected.getApplicationDept().getUid());
            assetSearchForm.setDept(selected.getApplicationDept());
            assetSearchForm.setDeptName(selected.getApplicationDept().getName());
        }
    }

    public void deleteApplyPurchaseAsset(){
        if(selectedApplyPurchaseAsset != null){
            applyPurchaseAssetList.remove(selectedApplyPurchaseAsset);
        }
    }

    public void searchAsset(){
        if(StrUtil.isEmpty(assetSearchForm.getApplicationDeptId())){
            WebUtil.addErrorMessage("清输入申请科室！");
            return;
        }
        assetInfoList = mcApplyPurchaseService.queryAssetByCondition(account, assetSearchForm);
    }

    public void resetAssetSearch(){
        assetSearchForm = new BudgetAssetSearchForm();
    }

    public void saveSelectedAsset(){
        if(selectedMap.isEmpty()){
            WebUtil.addErrorMessage("请选择设备！");
            return;
        }

        mcApplyPurchaseService.saveSelectedAsset(selectedMap, applyPurchaseAssetList);
        System.out.println(applyPurchaseAssetList.size());
    }

    public String saveApplication(){
        mcApplyPurchaseService.saveData(selected,applyPurchaseAssetList,v2_blobObjectList,removeFileBlobId, account,applyPurchaseStatusVo.getActionName());
        return "List.xhtml?faces-redirect=true";
    }

    public String submitAudit(){
        String resultMultiLevelApproval = mcApplyPurchaseService.checkMultiLevelApproval(selected,account);
        if(StrUtil.isNotEmpty(resultMultiLevelApproval)){
            WebUtil.addSuccessMessage(resultMultiLevelApproval);
            return "";
        }
        if(!mcApplyPurchaseService.checkRequiredField(selected, applyPurchaseAssetList)){
            return "";
        }
        mcApplyPurchaseService.submitAudit(selected, applyPurchaseAssetList, v2_blobObjectList, removeFileBlobId, account, applyPurchaseStatusVo.getActionName());
        return "List.xhtml?faces-redirect=true";
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject ) {
            v2_blobObjectList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_MC_SUBSCRIBE);
            blobObject.setBoSubType(V2_BlobObject.BO_TYPE_MC_SUBSCRIBE_APPLY);

            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeAttachmentById(String blobId){
        this.removeFileBlobId.add(blobId);
        v2_blobObjectList.removeAll(v2_blobObjectList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        assetSearchForm.setDept(orgInfo);
        assetSearchForm.setApplicationDeptId(orgInfo.getUid());
        assetSearchForm.setDeptName(orgInfo.getName());
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !assetInfoList.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : assetInfoList) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public String backList(){
        return "List.xhtml?faces-redirect=true&tabIndex="+tabIndex;
    }

    @Override
    protected GenericRepositoryUUID<MaintenanceContractApplyPurchase> getDAO() {
        return dao;
    }
}
