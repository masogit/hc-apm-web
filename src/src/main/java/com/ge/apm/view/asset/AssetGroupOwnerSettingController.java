package com.ge.apm.view.asset;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.OwnerConfigRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.OwnerConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.asset.OwnerConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2019/2/25.
 */
@ManagedBean
@ViewScoped
public class AssetGroupOwnerSettingController extends GenericCRUDUUIDController<OwnerConfig> {

    private OwnerConfigRepository dao;
    private UserCommonService userCommonService;
    private OwnerConfigService ownerConfigService;
    private OrgInfoRepository orgInfoDao;

    private List<UserAccount> userAccountList;

    private Integer assetGroupID;
    private Integer assetGroupSecondaryID;
    private List<I18nMessage> assetGroupSecondaryList;

    private UserAccount woOwner;
    private UserAccount woOwner2;
    private UserAccount woOwner3;
    private UserAccount pmOwner;

    private UserAccount currentUser;

    private UaaService uaaService;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private I18nMessageRepository i18nDao;

    private Integer assetGroupFilter;
    private Integer assetGroupSecondaryIDFilter;
    private List<I18nMessage> assetGroupSecondaryListFilter;

    @Override
    protected void init() {

        dao = WebUtil.getBean(OwnerConfigRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        ownerConfigService = WebUtil.getBean(OwnerConfigService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);

        currentUser = UserContextService.getCurrentUserAccount();
        userAccountList = userCommonService.getWorkerList(null,null);

        this.selectedOrg = orgInfoDao.getByUid(currentUser.getSiteUID());
        this.initOrgTree();
    }

    @Override
    protected OwnerConfigRepository getDAO(){
        return dao;
    }

    protected Page<OwnerConfig> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }

        this.removeFilterOnField("tenantUID");
        this.removeFilterOnField("institutionUID");
        this.removeFilterOnField("hospitalUID");
        this.removeFilterOnField("siteUID");

        this.updateSearchFilter(new SearchFilter("ownerConfigType", SearchFilter.Operator.EQ, OwnerConfig.OwnerConfigType.AssetGroup.name()));
        if(this.selectedOrg.getOrgLevel() == 0){
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }
        if(this.selectedOrg.getOrgLevel() == 1){
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }
        if(this.selectedOrg.getOrgLevel() == 2){
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }
        if(this.selectedOrg.getOrgLevel() == 3){
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid()));
        }

        this.loadAssetGroupSecondary();

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    //得到所选记录数据信息
    public void onSelectOwnerConfig(){
        this.assetGroupID = this.selected.getAssetGroupID();
        this.assetGroupSecondaryID = this.selected.getAssetGroupSecondaryID();
        this.woOwner = this.selected.getWoOwner();
        this.woOwner2 = this.selected.getWoOwner2();
        this.woOwner3 = this.selected.getWoOwner3();
        this.pmOwner = this.selected.getPmOwner();

        if(this.assetGroupID != null){
            assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + this.assetGroupID, currentUser.getSiteId());
        }
    }

    //维修工程师自动填写列表
    public List<UserAccount> autoCompleteOwner(String query) {

        if (userAccountList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = userAccountList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        return res;
    }

    //新增修改时选择设备类型或设备类型2时得到责任人数据
    public void loadOwnerConfig(){

        this.resetOwnerConfig();

        if(assetGroupID != null){
            List<OwnerConfig> ownerConfigList = ownerConfigService.loadAssetGroupOwnerConfig(assetGroupID, assetGroupSecondaryID, selectedOrg.getSiteUID());
            for(OwnerConfig ownerConfig : ownerConfigList){
                this.woOwner = ownerConfig.getWoOwner();
                this.woOwner2 = ownerConfig.getWoOwner2();
                this.woOwner3 = ownerConfig.getWoOwner3();
                this.pmOwner = ownerConfig.getPmOwner();
            }
        }
    }

    private void resetOwnerConfig(){
        this.woOwner = null;
        this.woOwner2 = null;
        this.woOwner3 = null;
        this.pmOwner = null;
    }

    //保存按钮动作
    public void saveChange() {

        if(woOwner == null && woOwner2 == null && woOwner3 == null && pmOwner == null){
            WebUtil.addErrorMessage("请设置维修负责人或保养负责人");
            return;
        }

        List<OwnerConfig> ownerConfigList = ownerConfigService.loadAssetGroupOwnerConfig(assetGroupID, assetGroupSecondaryID, selectedOrg.getSiteUID());

        OwnerConfig ownerConfig = new OwnerConfig();

        for (OwnerConfig tempOwnerConfig : ownerConfigList) {
            ownerConfig.setId(tempOwnerConfig.getId());
        }
        ownerConfig.setTenantUID(selectedOrg.getTenantUID());
        ownerConfig.setInstitutionUID(selectedOrg.getInstitutionUID());
        ownerConfig.setHospitalUID(selectedOrg.getHospitalUID());
        ownerConfig.setSiteUID(selectedOrg.getSiteUID());

        ownerConfig.setWoOwnerUID(woOwner != null ? woOwner.getUid() : null);
        ownerConfig.setWoOwnerUID2(woOwner2 != null ? woOwner2.getUid() : null);
        ownerConfig.setWoOwnerUID3(woOwner3 != null ? woOwner3.getUid() : null);
        ownerConfig.setPmOwnerUID(pmOwner != null ? pmOwner.getUid() : null);

        ownerConfig.setOwnerConfigType(OwnerConfig.OwnerConfigType.AssetGroup.name());
        ownerConfig.setAssetGroupID(assetGroupID);
        ownerConfig.setAssetGroupSecondaryID(assetGroupSecondaryID);

        ownerConfig.setCreatedBy(currentUser.getLoginName());
        ownerConfig.setCreatedDate(new Date());
        ownerConfig.setLastModifiedBy(currentUser.getLoginName());
        ownerConfig.setLastModifiedDate(new Date());

        dao.save(ownerConfig);

        this.resetOwnerConfig();
        this.assetGroupID = null;
        this.assetGroupSecondaryID = null;
        WebUtil.addErrorMessage("保存成功");
    }

    //选择院区树后的动作
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        this.resetOwnerConfig();
        this.assetGroupID = null;
        this.assetGroupSecondaryID = null;
    }

    //院区选择树数据初始
    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        orgTree = new DefaultTreeNode("root", null, null);
        orgTree.getChildren().add(treeNode);
        orgTree.setExpanded(true);

        TreeNode selectedTempOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedTempOrg) {
            selectedTempOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedTempOrg);
        }
    }

    //新增按钮动作
    public void createOwerConfig(){
        this.resetOwnerConfig();
        this.assetGroupID = null;
        this.assetGroupSecondaryID = null;
        this.selected = new OwnerConfig();
    }

    //删除按钮动作
    public void deleteOwerConfig(){
        dao.delete(this.selected);
        this.selected = null;
        WebUtil.addErrorMessage("删除成功");
    }

    //新增修改时选择设备分类时的动作（得到责任人信息和设备分类2的数据）
    public void loadAssetGroupSecondary() {
        this.assetGroupSecondaryID = null;
        this.loadOwnerConfig();
        if(this.assetGroupID != null){
            assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + this.assetGroupID, currentUser.getSiteId());
        }
    }

    //过滤时选择选择设备分类时的动作（得到设备分类2过滤列表数据）
    public void loadAssetGroupSecondaryFilter() {
        this.assetGroupSecondaryIDFilter = null;
        assetGroupSecondaryListFilter = Collections.emptyList();

        if(this.hasFilterOnField("assetGroupID")){
            for (SearchFilter sf : this.searchFilters) {
                //判断是否有计量等级的过滤条件
                if(sf.fieldName.equals("assetGroupID")){
                    if(sf.value != null){
                        assetGroupSecondaryListFilter = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + sf.value.toString(), currentUser.getSiteId());
                    }
                }
            }
        }
    }

    public void onCheckChange(){
    }

    public void onOwnerChange(SelectEvent event){
    }

    public Integer getAssetGroupID() {
        return assetGroupID;
    }

    public void setAssetGroupID(Integer assetGroupID) {
        this.assetGroupID = assetGroupID;
    }

    public UserAccount getWoOwner() {
        return woOwner;
    }

    public void setWoOwner(UserAccount woOwner) {
        this.woOwner = woOwner;
    }

    public UserAccount getWoOwner2() {
        return woOwner2;
    }

    public void setWoOwner2(UserAccount woOwner2) {
        this.woOwner2 = woOwner2;
    }

    public UserAccount getWoOwner3() {
        return woOwner3;
    }

    public void setWoOwner3(UserAccount woOwner3) {
        this.woOwner3 = woOwner3;
    }

    public UserAccount getPmOwner() {
        return pmOwner;
    }

    public void setPmOwner(UserAccount pmOwner) {
        this.pmOwner = pmOwner;
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

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public Integer getAssetGroupSecondaryID() {
        return assetGroupSecondaryID;
    }

    public void setAssetGroupSecondaryID(Integer assetGroupSecondaryID) {
        this.assetGroupSecondaryID = assetGroupSecondaryID;
    }

    public List<I18nMessage> getAssetGroupSecondaryList() {
        return assetGroupSecondaryList;
    }

    public void setAssetGroupSecondaryList(List<I18nMessage> assetGroupSecondaryList) {
        this.assetGroupSecondaryList = assetGroupSecondaryList;
    }

    public Integer getAssetGroupFilter() {
        return assetGroupFilter;
    }

    public void setAssetGroupFilter(Integer assetGroupFilter) {
        this.assetGroupFilter = assetGroupFilter;
    }

    public Integer getAssetGroupSecondaryIDFilter() {
        return assetGroupSecondaryIDFilter;
    }

    public void setAssetGroupSecondaryIDFilter(Integer assetGroupSecondaryIDFilter) {
        this.assetGroupSecondaryIDFilter = assetGroupSecondaryIDFilter;
    }

    public List<I18nMessage> getAssetGroupSecondaryListFilter() {
        return assetGroupSecondaryListFilter;
    }

    public void setAssetGroupSecondaryListFilter(List<I18nMessage> assetGroupSecondaryListFilter) {
        this.assetGroupSecondaryListFilter = assetGroupSecondaryListFilter;
    }
}
