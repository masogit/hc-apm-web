package com.ge.apm.view.iot;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.RccEtlDeviceRepository;
import com.ge.apm.dao.SystemClientInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.PasswordUtil;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class SystemClientController extends GenericCRUDUUIDController<SystemClientInfo> {


    private SystemClientInfoRepository dao;
    private OrgInfoRepository orgInfoRepository;
    private TenantInfoRepository tenantInfoRepository;
    private RccEtlDeviceRepository rccEtlDeviceRepository;

    private String assetUid;

    private TenantInfo selectTenant;
    private OrgInfo selectSite;
    private UserAccount user;

    private List<TenantInfo> tenantInfoList;
    private TreeNode tenantOrgTreePage;
    private TreeNode selectedOrgNode;

    private String siteUID;
    private String tenantUid="";
    private int siteId;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SystemClientInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        rccEtlDeviceRepository = WebUtil.getBean(RccEtlDeviceRepository.class);
        user = UserContextService.getCurrentUserAccount();

        //获取用户siteId用于区分用户是否是46号租户
        tenantUid = UserContextService.getCurrentUserAccount().getTenantUID();
        siteUID = UserContextService.getCurrentUserAccount().getSiteUID();
        siteId = UserContextService.getCurrentUserAccount().getSiteId();

        tenantInfoList = tenantInfoRepository.getAllOrderByName();
        selectTenant = new TenantInfo();
        selectTenant.setUid(null);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.selected = new SystemClientInfo();
        this.selectTenant = new TenantInfo();
        this.selectTenant.setUid(null);
        this.selectSite = null;
        this.tenantOrgTreePage = null;
        super.prepareCreate();
    }

    @Override
    protected GenericRepositoryUUID<SystemClientInfo> getDAO() {
        return dao;
    }

    @Override
    protected Page<SystemClientInfo> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;

        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    public List<AssetInfo> getAssetList() {
        if(this.selected == null || this.selected.getOrgInfo() == null){
            return new ArrayList<>();
        }
        return null;
    }

    public void saveClientInfo(){

        if(StringUtils.isNotEmpty(this.selected.getPlainPassword())){
            this.selected.setClientSecret(BCrypt.hashpw(this.selected.getPlainPassword(), BCrypt.gensalt()));
        }else{
            if (null == selected.getUid()) {
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("创建账户时，请输入密码！");
                return;
            }
        }
        this.selected.setOrgInfo(this.selectSite);
        dao.save(this.selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("SaveSuccess"));
    }

    @Override
    public void delete() {
        int count = rccEtlDeviceRepository.countByLoginName(this.selected.getClientName());

        if(count > 0){
            WebUtil.addErrorMessage("该账户下有设备数据，无法删除！");
            return;
        }

        super.delete();
    }

    public void setDefaultVal(){
        if(null != this.selected && null != this.selected.getOrgInfo()){
            this.selectSite = this.selected.getOrgInfo();
            this.selectTenant = tenantInfoRepository.findByuid(this.selectSite.getTenantUID());
            this.tenantOrgTreePage = null;
            getTenantOrgTree();
        }
    }

    public void resetOrgTree(){
        this.selectSite = null;
        this.tenantOrgTreePage = null;
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event) {
            if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
                OrgInfo site = (OrgInfo) event.getTreeNode().getData();
                if(StringUtils.isNotEmpty(site.getUid())){
                    this.selectSite = site;
                }
            } else {
                this.selectSite = orgInfoRepository.getByUid(user.getSiteUID());
            }
        }
    }

    public TreeNode getTenantOrgTree() {
        if(null == tenantOrgTreePage && null != selectTenant && StringUtils.isNotEmpty(selectTenant.getUid())){
            if(judgeTheUser()){
                tenantOrgTreePage = createTenantOrgTree46();
            }else{
                tenantOrgTreePage = createTenantOrgTree();
            }
        }
        return tenantOrgTreePage;
    }

    public TreeNode createTenantOrgTree46() {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        List<OrgInfo> siteList = orgInfoRepository.getOrgTreeBySiteAndparm(this.selectTenant.getUid(), "").stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());
        TreeNode siteTree = buildTree(siteList);
        expandedByOrgType(siteTree, 2);
        rootNode.getChildren().addAll(siteTree.getChildren());
        return rootNode;
    }

    public TreeNode createTenantOrgTree() {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        List<OrgInfo> siteList = orgInfoRepository.getByTenantUID(this.selectTenant.getUid())
                .stream().filter(item -> item.getOrgType() < 4).collect(Collectors.toList());

        TreeNode siteTree = buildTree(siteList);
        expandedByOrgType(siteTree, 2);
        rootNode.getChildren().addAll(siteTree.getChildren());
        return rootNode;
    }

    private void expandedByOrgType(TreeNode tree, Integer type) {
        if (tree.getData() != null) {
            OrgInfo node = (OrgInfo) tree.getData();
            if (node.getOrgType() > type) {
                return;
            }
        }
        tree.setExpanded(true);
        for (TreeNode child : tree.getChildren()) {
            expandedByOrgType(child, type);
        }

    }

    private TreeNode buildTree(List<OrgInfo> orgList) {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
        return rootNode;
    }

    public boolean judgeTheUser(){
        if(siteUID.equals(tenantUid) || 46==siteId){
            return true;
        }else{
            return false;
        }
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public TreeNode getSelectedOrgNode() {
        return selectedOrgNode;
    }

    public void setSelectedOrgNode(TreeNode selectedOrgNode) {
        this.selectedOrgNode = selectedOrgNode;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public TenantInfo getSelectTenant() {
        return selectTenant;
    }

    public void setSelectTenant(TenantInfo selectTenant) {
        this.selectTenant = selectTenant;
    }
}
