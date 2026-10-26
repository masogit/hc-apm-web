/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AssetOwnerService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.google.common.base.Strings;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetOwnerSettingController {

    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets = Collections.emptyList();
    private List<AssetInfo> targetAssets;
    private AssetCreateService acServie;
    private AssetInfoService assetInfoService;

    private AssetOwnerService aoService;
    private UserCommonService userCommonService;
    private CustomConfigService customConfigService;

    private AssetInfo queryAsset;

    List<UserAccount> userAccountList;
    private UserAccount ownerSearch;
    private UserAccount ownerSearch2;
    private UserAccount ownerSearch3;
    private Integer ownerId;
    private UserAccount owner;
    private Integer ownerId2;
    private UserAccount owner2;
    private Integer ownerId3;
    private UserAccount owner3;
    private Boolean isChanegeOwner;
    private Boolean isChanegeOwner2;
    private Boolean isChanegeOwner3;
    private Boolean hasAssetGroupSecondary;

    private UserAccount currentUser;

    private List<OrgInfo> clinicalDeptList;
    private OrgInfo deptOrg;
    private List<String> clinicalDeptUids;

    //
    private OrgInfo selectedOrg;
    private UploadedFile file;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private UaaService uaaService;



    @PostConstruct
    protected void init() {

        acServie = WebUtil.getBean(AssetCreateService.class);
        aoService = WebUtil.getBean(AssetOwnerService.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        customConfigService = WebUtil.getBean(CustomConfigService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        queryAsset = new AssetInfo();
        queryAsset.setSiteId(currentUser.getSiteId());
        queryAsset.setHospitalId(currentUser.getHospitalId());

        targetAssets = new ArrayList();

        availableAssets = new DualListModel<>(Collections.emptyList(), targetAssets);

        CustomConfigObject customConfigObject = customConfigService.getCurrentCustomConfig(currentUser.getSiteUID());
        if(customConfigObject.getAssetFields() != null && customConfigObject.getAssetFields().containsKey("assetGroupSecondary")){
            hasAssetGroupSecondary = Boolean.TRUE;
        }else {
            hasAssetGroupSecondary = Boolean.FALSE;
        }

        userAccountList = userCommonService.getWorkerList(null,null);
        clinicalDeptList = getClinicalDeptList();
        initOrgTree();
        //not display data initially
//        getQueryAssetsList();

    }
//新增 ------------------------------------
    public String getSelectedOrgName() {
        if (selectedOrg == null) {
            return "";
        } else {
            return selectedOrg.getName();
        }
    }
    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }
    public TreeNode getOrgTree() {

        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public boolean getIsSelect46Account() {
        if (Objects.equals(UserContextService.getCurrentUserAccount().getSiteId(),46)&&UserContextService.getCurrentUserAccount().getTenantUID().equals(UserContextService.getCurrentUserAccount().getSiteUID())) {
            return true;
        }
        return false;
    }

    public void initOrgTree() {
        if(getIsSelect46Account()){
            return;
        }
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);

    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
    
        if (orgInfo == null || orgInfo.getId() == -1) {
            selectedOrg = null;
            // 选择"所有医院"时，重新加载全部用户
            userAccountList = userCommonService.getWorkerList(null, null);
        } else {
            selectedOrg = orgInfo;
            // 选择医院后，按选中的组织UID过滤用户列表
            userAccountList = userCommonService.getWorkerList(null, selectedOrg.getUid());
            if(Objects.equals(selectedOrg.getOrgLevel(),0) && !Objects.equals(selectedOrg.getOrgType(),4)){
                queryAsset.setTenantUID(selectedOrg.getUid());
                clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getUid(),0);
            }
            if(Objects.equals(selectedOrg.getOrgLevel(),1) && !Objects.equals(selectedOrg.getOrgType(),4)){
                queryAsset.setInstitutionUID(selectedOrg.getUid());
                clinicalDeptList = assetInfoService. getClinicalDeptList(selectedOrg.getUid(),1);
            }
            if(Objects.equals(selectedOrg.getOrgLevel(),2) && !Objects.equals(selectedOrg.getOrgType(),4)){
                queryAsset.setHospitalUID(selectedOrg.getUid());
                clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getUid(),2);
            }
            if(Objects.equals(selectedOrg.getOrgLevel(),3) && !Objects.equals(selectedOrg.getOrgType(),4)){
                queryAsset.setSiteUID(selectedOrg.getUid());
                clinicalDeptList =  assetInfoService.getClinicalDeptList(selectedOrg.getUid(),3);
            }
            if(Objects.equals(selectedOrg.getOrgType(),4)){
                queryAsset.setClinicalDeptUID(selectedOrg.getUid());
                clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getUid(),4);
            }
        }
        // 切换医院时清空已选的负责人，避免数据不一致
        ownerSearch = null;
        ownerSearch2 = null;
        ownerSearch3 = null;
        queryAsset.setAssetOwnerId(null);
        queryAsset.setAssetOwnerId2(null);
        queryAsset.setAssetOwnerId3(null);
        getQueryAssetsList();
    }

    public void getQueryAssetsList() {
        if(queryAsset.getTenantUID()==null&&queryAsset.getInstitutionUID()==null&&queryAsset.getHospitalUID()==null&&queryAsset.getClinicalDeptUID()==null&&queryAsset.getSiteUID()==null&&queryAsset.getAssetGroup() == null && queryAsset.getClinicalDeptUID() == null && queryAsset.getAssetOwnerId() == null && queryAsset.getAssetOwnerId2() == null && queryAsset.getAssetOwnerId3() == null){
            availableAssets = new DualListModel<>(Collections.emptyList(), targetAssets);
            return;
        }
        Page page = aoService.getAssetListEx(queryAsset,clinicalDeptUids);
        sourceAssets = new ArrayList<AssetInfo>(page.getContent());
        targetAssets.forEach((item)->{
            if(sourceAssets.contains(item)){
                sourceAssets.remove(item);
            }
        });

        availableAssets = new DualListModel<>(sourceAssets, targetAssets);

        boolean needFilterFurther = page.getTotalElements() > page.getSize();
        if (needFilterFurther) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("needFilterFurthur"));
        }

        this.queryAsset.setAssetGroupSecondary(null);
    }



    public void onAssetsTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetAssets.add((AssetInfo)item);
            }else{
                targetAssets.remove((AssetInfo)item);
            }
        }
    }

    public List<OrgInfo> getClinicalDeptList() {
        //return acServie.getClinicalDeptList(queryAsset.getSiteUID());
        if(Objects.equals(currentUser.getOrgLevel(),0)){
            return assetInfoService.getClinicalDeptList(currentUser.getTenantUID(),0);
        }
        if(Objects.equals(currentUser.getOrgLevel(),1)){
            return assetInfoService.getClinicalDeptList(currentUser.getInstitutionUID(),1);
        }
        if(Objects.equals(currentUser.getOrgLevel(),2)){
            return assetInfoService.getClinicalDeptList(currentUser.getHospitalUID(),2);
        }
        if(Objects.equals(currentUser.getOrgLevel(),3)){
            return assetInfoService.getClinicalDeptList(currentUser.getSiteUID(),3);
        }
        if(Objects.equals(currentUser.getOrgType(),4)){
            return assetInfoService.getClinicalDeptList(currentUser.getClinicalDeptUID(),4);
        }
        return null;
    }

    public List<UserAccount> getOwnerList() {
//        List<UserAccount> res = acServie.getAssetOnwers(queryAsset.getSiteId(), queryAsset.getHospitalId());
        List<UserAccount> res = userCommonService.getWorkerList(null,"");
        return res;
    }

    public List<UserAccount> autoCompleteOwner(String query) {

        if (userAccountList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = userAccountList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        //res.sort(new PinYinComparatorByName());
        return res;
    }

    public void handleSelectOwener(AjaxBehaviorEvent event) {
        Object filterOwner;
        Integer filterOwnerId = null;
        if (event instanceof SelectEvent) {
            filterOwner = ((SelectEvent) event).getObject();
            filterOwnerId = ((UserAccount) filterOwner).getId();
        }

        if (ownerSearch != null && !userAccountList.stream().anyMatch(item -> ownerSearch.getId().equals(item.getId()))) {
            ownerSearch = null;
        }

        queryAsset.setAssetOwnerId(filterOwnerId);
        this.getQueryAssetsList();
    }
    public void handleSelectOwener2(AjaxBehaviorEvent event) {
        Object filterOwner;
        Integer filterOwnerId = null;
        if (event instanceof SelectEvent) {
            filterOwner = ((SelectEvent) event).getObject();
            filterOwnerId = ((UserAccount) filterOwner).getId();
        }

        if (ownerSearch2 != null && !userAccountList.stream().anyMatch(item -> ownerSearch2.getId().equals(item.getId()))) {
            ownerSearch2 = null;
        }

        queryAsset.setAssetOwnerId2(filterOwnerId);
        this.getQueryAssetsList();
    }
    public void handleSelectOwener3(AjaxBehaviorEvent event) {
        Object filterOwner;
        Integer filterOwnerId = null;
        if (event instanceof SelectEvent) {
            filterOwner = ((SelectEvent) event).getObject();
            filterOwnerId = ((UserAccount) filterOwner).getId();
        }

        if (ownerSearch3 != null && !userAccountList.stream().anyMatch(item -> ownerSearch3.getId().equals(item.getId()))) {
            ownerSearch3 = null;
        }

        queryAsset.setAssetOwnerId3(filterOwnerId);
        this.getQueryAssetsList();
    }

    public void onOwnerChange(SelectEvent event){

    }

    public void saveChange() {
        if(queryAsset.getAssetGroup() == null && queryAsset.getAssetGroupSecondary() == null && deptOrg == null && ownerSearch == null && ownerSearch2 == null && ownerSearch3 == null ){
            WebUtil.addErrorMessage(WebUtil.getMessage("相关设备查询条件([所属院区]为必选条件，其它查询条件最少要填写或选择一个)"));
        }
        if (targetAssets.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
        } else if (!(isChanegeOwner || isChanegeOwner2)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssetsOwner"));
        } else {
            //aoService.updateAssetOwner(targetAssets, isChanegeOwner, ownerId, isChanegeOwner2, ownerId2, isChanegeOwner3, ownerId3);
            Integer ownerId = owner != null ? owner.getId() : null;
            Integer ownerId2 = owner2 != null ? owner2.getId() : null;
            Integer ownerId3 = owner3 != null ? owner3.getId() : null;

            aoService.updateAssetOwner(targetAssets, isChanegeOwner, ownerId, isChanegeOwner2, ownerId2, isChanegeOwner3, ownerId3);
            availableAssets.setTarget(targetAssets);
        }
    }

    public List<I18nMessage> loadAssetGroupSecondaryList(){
        List<I18nMessage> assetGroupSecondaryList = new ArrayList<>();

        if(queryAsset.getAssetGroup() != null){
            assetGroupSecondaryList = FieldValueMessageController.doGetFieldValueList("assetGroupSecondary_" + queryAsset.getAssetGroup(), currentUser.getSiteId());
        }

        return assetGroupSecondaryList;
    }

    public void onCheckChange(){
    }

    //新增修改所属科室控件自动填写列表
    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void handleSelectDept(AjaxBehaviorEvent event){
        this.queryAsset.setClinicalDeptUID(null);
        if(this.deptOrg != null){
            clinicalDeptUids = uaaService.getOrgInfoUidsByparentUid(deptOrg.getUid());
            this.getQueryAssetsList();
        }
    }
    
    //getter and setter
    public DualListModel getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getOwnerId2() {
        return ownerId2;
    }

    public void setOwnerId2(Integer ownerId2) {
        this.ownerId2 = ownerId2;
    }

    public Integer getOwnerId3() {
        return ownerId3;
    }

    public void setOwnerId3(Integer ownerId3) {
        this.ownerId3 = ownerId3;
    }

    public Boolean getIsChanegeOwner() {
        return isChanegeOwner;
    }

    public void setIsChanegeOwner(Boolean isChanegeOwner) {
        this.isChanegeOwner = isChanegeOwner;
    }

    public Boolean getIsChanegeOwner2() {
        return isChanegeOwner2;
    }

    public void setIsChanegeOwner2(Boolean isChanegeOwner2) {
        this.isChanegeOwner2 = isChanegeOwner2;
    }

    public Boolean getIsChanegeOwner3() {
        return isChanegeOwner3;
    }

    public void setIsChanegeOwner3(Boolean chanegeOwner3) {
        isChanegeOwner3 = chanegeOwner3;
    }

    public Boolean getHasAssetGroupSecondary() {
        return hasAssetGroupSecondary;
    }

    public void setHasAssetGroupSecondary(Boolean hasAssetGroupSecondary) {
        this.hasAssetGroupSecondary = hasAssetGroupSecondary;
    }

    public UserAccount getOwnerSearch() {
        return ownerSearch;
    }

    public void setOwnerSearch(UserAccount ownerSearch) {
        this.ownerSearch = ownerSearch;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    public UserAccount getOwner2() {
        return owner2;
    }

    public void setOwner2(UserAccount owner2) {
        this.owner2 = owner2;
    }

    public UserAccount getOwner3() {
        return owner3;
    }

    public void setOwner3(UserAccount owner3) {
        this.owner3 = owner3;
    }

    public UserAccount getOwnerSearch2() {
        return ownerSearch2;
    }

    public void setOwnerSearch2(UserAccount ownerSearch2) {
        this.ownerSearch2 = ownerSearch2;
    }

    public UserAccount getOwnerSearch3() {
        return ownerSearch3;
    }

    public void setOwnerSearch3(UserAccount ownerSearch3) {
        this.ownerSearch3 = ownerSearch3;
    }

    public OrgInfo getDeptOrg() {
        return deptOrg;
    }

    public void setDeptOrg(OrgInfo deptOrg) {
        this.deptOrg = deptOrg;
    }
}
