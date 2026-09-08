
package com.ge.apm.view.his;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisEquipmentDataResultRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisEquipmentDataResult;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class JobController extends GenericCRUDUUIDController<HisEquipmentDataResult> {
    private OrgInfoRepository orgInfoDao;
    private HisEquipmentDataResultRepository resultDao;
    private HisAssetGroupAssetMatchRepository groupDao;
    private UserAccount selectedAssignee;
    private UaaService uaaService;
    private AssetInfoRepository assetInfoRepository;
    private DualListModel<HisEquipmentSfxm> availableAssets;
    private List<HisEquipmentSfxm> sourceAssets;
    private List<HisEquipmentSfxm> targetAssets;
    private OrgInfo selectedOrg;//selected
    private UserAccount loginUser;
    private String treeNodeNameFilter;
    private TreeNode selectedNode;
    private TreeNode orgTree;
    private TreeNode enableOrgTree;

    private String name; //设备名称
    private String departNum;//设备编号

    private String sfxmId;  //过滤His收费代码
    private String sfxmName;//过滤His收费名称

    List<HisAssetGroupAssetMatch> startList = new ArrayList<>();
    //复选的设备
    private Map<String, Boolean> selectedMap = new HashMap<>();
    List<String> pmIdList = new ArrayList<>(); //选中的
    private HisOrgComponentController orgComponentController;
    private Boolean flag = false;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    protected void init() {
        resultDao =  WebUtil.getBean(HisEquipmentDataResultRepository.class);
        groupDao = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        selectedOrg = orgInfoDao.getByUid(loginUser.getOrgInfoUID());
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgComponentController.registerController(this);
        rebuildOrgTree(loginUser.getTenantUID());
    }
    public void changAssetList(Boolean flg){
        flag = flg;
    }

    public void rebuildOrgTree(String tenantUid) {
        orgTree = uaaService.buildOrgTree(tenantUid, "", -1, -1);
        enableOrgTree = uaaService.buildEnableOrgTree(loginUser.getUid(), loginUser.getOrgInfoUID(), selectedOrg);
    }

    @Override
    protected Page<HisEquipmentDataResult> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        removeFilterOnField("orgCode");
        removeFilterOnField("siteUid");

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        removeFilterOnField("tenantUid");
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        if (selectedOrg != null) {
            searchFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, selectedOrg.getSiteUID()));
        }

        Page<HisEquipmentDataResult> page;
        if (this.searchFilters.isEmpty()) {
            page = resultDao.findAll(pageRequest);
        } else {
            page = resultDao.findBySearchFilter(searchFilters,pageRequest);
        }
        List<HisEquipmentDataResult> content = page.getContent();
        return page;
    }

    @Override
    public List<HisEquipmentDataResult> getItemList() {
        return resultDao.find();
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        selected = null;
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
    }


    public void setStatusFilter() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterState != null) {
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.EQ, filterState));
        }
    }


    public String workOrderRouter() {
        String retrunUrl = "";
        RequestContext requestContext = RequestContext.getCurrentInstance();
        Boolean willShowNotification = Boolean.FALSE;
        requestContext.addCallbackParam("showNotification", willShowNotification);
        if (true) {
            requestContext.addCallbackParam("url", UrlEncryptController.encodeUrlParam("transferAsset?actionName=Create"));
            retrunUrl = UrlEncryptController.encodeUrlParam("transferAsset?actionName=Create");
        }

        return retrunUrl;
    }

   // 4 * (2C、4G) ---->  4 * (2C * 8G)

    public void filterTreeNode() {
        uaaService.filterTreeNode(orgTree, treeNodeNameFilter, -1, -1);
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgInfos = uaaService.getOrgDeptList(siteUid, user.getUid());
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);
        return orgInfos;
    }


    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    //选中确认
    public void confirm(){
        if(CollectionUtils.isNotEmpty(pmIdList)) {
            startList = new ArrayList<>();
            List<HisAssetGroupAssetMatch> results = groupDao.findByIds(pmIdList);
            startList.addAll(results);
        }
    }
    //初始化
    public List<HisAssetGroupAssetMatch> getSartList() {
        return startList;
    }

    public void cancel() {
        this.selected = null;
    }

    private String filterState = null;

    public String getFilterState() {
        return filterState;
    }
    public void setFilterState(String filterState) {
        this.filterState = filterState;
    }

    public UserAccount getSelectedAssignee() {
        return this.selectedAssignee;
    }

    public void setSelectedAssignee(UserAccount ua) {
        this.selectedAssignee = ua;
    }

    public DualListModel<HisEquipmentSfxm> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<HisEquipmentSfxm> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<HisEquipmentSfxm> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<HisEquipmentSfxm> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<HisEquipmentSfxm> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<HisEquipmentSfxm> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public UserAccount getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(UserAccount loginUser) {
        this.loginUser = loginUser;
    }

    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

    public TreeNode getEnableOrgTree() {
        return enableOrgTree;
    }

    public void setEnableOrgTree(TreeNode enableOrgTree) {
        this.enableOrgTree = enableOrgTree;
    }

    public String getSfxmName() {
        return sfxmName;
    }

    public void setSfxmName(String sfxmName) {
        this.sfxmName = sfxmName;
    }

    public String getSfxmId() {
        return sfxmId;
    }

    public void setSfxmId(String sfxmId) {
        this.sfxmId = sfxmId;
    }
    public void prepareCreateSart() {
        crudActionName = "Create";
    }

    @Override
    protected HisEquipmentDataResultRepository getDAO() {
        return resultDao;
    }

    public void prepareEditSart() {
        crudActionName = "Edit";
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }
}


