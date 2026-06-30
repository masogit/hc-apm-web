package com.ge.apm.web.wo;

import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.AssetWorkOrder;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.wo.AssetWorkOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2019/1/11.
 */
@ManagedBean
@ViewScoped
public class AssetWorkOrderController implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AssetWorkOrderController.class);

    private AssetWorkOrderService assetWorkOrderService;
    private List<AssetWorkOrder> assetWorkOrderList;

    private UaaService uaaService;
    private AssetInfoService assetInfoService;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;
    private UserAccount currentUser;

    private Integer filterAssetGroup;
    private OrgInfo filterDeptOrg;
    private OrgInfo filterFromDeptOrg;
    private List<OrgInfo> clinicalDeptList;
    private String searchStr;
    private Date startDate;
    private Date endDate;
    private String contextPath;

    @PostConstruct
    protected void init() {
        assetWorkOrderService = WebUtil.getBean(AssetWorkOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        currentUser = UserContextService.getCurrentUserAccount();

        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        currentAssetTypeGroupList = currentUser.getAssetTypeGroupList();
        enableAssetTypeGroup = currentUser.isEnableAssetTypeGroup();
        if (enableAssetTypeGroup) {
            assetTypeGroupEncodedSet = currentAssetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        contextPath = request.getContextPath();
        this.loadData();
        this.initOrgTree();
        this.loadClinicalDept();
    }

    public void loadData() {
        assetWorkOrderList = assetWorkOrderService.loadAssetWorkOrderData(selectedOrg, filterDeptOrg, filterAssetGroup, searchStr, clinicalDeptList, startDate, endDate, assetTypeGroupEncodedSet,filterFromDeptOrg);
        setDataLinkUrl();
    }
    private void setDataLinkUrl(){
        if(CollectionUtils.isEmpty(assetWorkOrderList)) return;
        String prefix="/portal/woMgr/workOrderList.xhtml?faces-redirect=true&query=12&queryAssetUID=";
        assetWorkOrderList= assetWorkOrderList.stream().map(item->{
             String linkUrl = UrlEncryptController.encodeUrlParam(prefix.concat(item.getUid()));
            linkUrl = linkUrl.replaceAll("&amp;","&");
            item.setLinkUrl(contextPath+linkUrl);
           return item;
        }).collect(Collectors.toList());
    }
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }
        loadClinicalDept();
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().add(treeNode);
        rootNode.setExpanded(true);
    }

    private void loadClinicalDept() {
        if(null == selectedOrg) {
            clinicalDeptList = Collections.EMPTY_LIST;
        } else {
            clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getSiteUID());
            List<OrgInfo> allAccessibleClinicalDeptList = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
            clinicalDeptList = clinicalDeptList.stream().filter(orgInfo -> allAccessibleClinicalDeptList.contains(orgInfo)).collect(Collectors.toList());
            if(null != filterDeptOrg && !filterDeptOrg.getSiteUID().equals(selectedOrg.getSiteUID()) ){
                filterDeptOrg = null;
            }
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    private AssetTypeGroupRepository assetTypeGroupRepository;
    private AssetTypeGroupService assetTypeGroupService;
    private Set<String> assetTypeGroupEncodedSet;
    private List<AssetTypeGroup> currentAssetTypeGroupList;
    @Getter
    @Setter
    private String filterAssetTypeName;
    @Getter @Setter
    private boolean enableAssetTypeGroup;

    public List<String> completeAssetTypeGroupList(String query) {
        if (currentAssetTypeGroupList == null) {
            return Collections.emptyList();
        }
        List<AssetTypeGroup> res = this.currentAssetTypeGroupList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .collect(Collectors.toList());
        return res.stream().map(AssetTypeGroup::getName).distinct().collect(Collectors.toList());
    }

    public void onAssetTypeGroupChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterAssetTypeName = (String) ((SelectEvent) event).getObject();
        }
        if (filterAssetTypeName != null) {
            List<AssetTypeGroup> assetTypeGroupsMatchingByName = this.currentAssetTypeGroupList.stream().filter(item -> item.getName().equals(filterAssetTypeName)).collect(Collectors.toList());
            Set<AssetTypeGroup> matchedList = assetTypeGroupService.collectChildAssetTypeGroupInExstingList(assetTypeGroupsMatchingByName, currentAssetTypeGroupList);
            assetTypeGroupEncodedSet = matchedList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
        } else {
            if (enableAssetTypeGroup) {
                assetTypeGroupEncodedSet = currentAssetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
            } else {
                assetTypeGroupEncodedSet = Collections.emptySet();
            }
        }
    }

    public List<AssetWorkOrder> getAssetWorkOrderList() {
        return assetWorkOrderList;
    }

    public void setAssetWorkOrderList(List<AssetWorkOrder> assetWorkOrderList) {
        this.assetWorkOrderList = assetWorkOrderList;
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

    public OrgInfo getFilterDeptOrg() {
        return filterDeptOrg;
    }

    public void setFilterDeptOrg(OrgInfo filterDeptOrg) {
        this.filterDeptOrg = filterDeptOrg;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public Integer getFilterAssetGroup() {
        return filterAssetGroup;
    }

    public void setFilterAssetGroup(Integer filterAssetGroup) {
        this.filterAssetGroup = filterAssetGroup;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public OrgInfo getFilterFromDeptOrg() {
        return filterFromDeptOrg;
    }

    public void setFilterFromDeptOrg(OrgInfo filterFromDeptOrg) {
        this.filterFromDeptOrg = filterFromDeptOrg;
    }
}
