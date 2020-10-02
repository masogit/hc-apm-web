package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.KeyLabel;
import com.ge.apm.service.asset.AssetInfoBulkEditService;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoBulkEditController extends JpaCRUDController<AssetInfo>  {

    static PageRequest pageRequest = new PageRequest(0, 100);

    private AssetInfoRepository dao;
    private AssetInfoService assetInfoService;
    private UaaService uaaService;
    private AssetInfoBulkEditService assetInfoBulkEditService;

    //******* filter start
    @Getter @Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>(null, new ArrayList<>());

    private TreeNode orgTree;

    @Setter
    private OrgInfo filterOrgInfo;

    @Getter @Setter
    private AssetInfo filter = new AssetInfo();
    //******* filter end


    //****** new value start
    private List<OrgInfo> clinicalDeptList = Collections.emptyList();
    //****** new value end

    @Getter
    private List<KeyLabel> candidateKeyLabels = new ArrayList<>();

    @Getter @Setter
    private List<String> selectedKeyLabels = new ArrayList<>();

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoBulkEditService = WebUtil.getBean(AssetInfoBulkEditService.class);

        selected = new AssetInfo();

        initCandidateKeyLabels();
    }

    private void initCandidateKeyLabels() {
        KeyLabel keyLabel;

        keyLabel = new KeyLabel("hospitalId", WebUtil.getMessage("hospitalId"));
        candidateKeyLabels.add(keyLabel);

        keyLabel = new KeyLabel("clinicalDeptName", WebUtil.getMessage("clinicalDeptName"));
        candidateKeyLabels.add(keyLabel);

        keyLabel = new KeyLabel("assetGroup", WebUtil.getMessage("assetGroup"));
        candidateKeyLabels.add(keyLabel);

    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return null;
    }

    public void initOrgTree() {

        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNodeAsFilter(NodeSelectEvent event) {
        if (null == event || null == event.getTreeNode() || null == event.getTreeNode().getData()) {
            filterOrgInfo = null;
        } else {
            filterOrgInfo = (OrgInfo) event.getTreeNode().getData();
        }
    }

    public DualListModel<AssetInfo> getAssets() {

        prepareFilters();

        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInfo> source = new ArrayList<>(page.getContent());
        source.removeAll(assets.getTarget());
        assets.setSource(source);

        return assets;
    }

    private void prepareFilters() {
        searchFilters = new ArrayList<>();
        SearchFilter searchFilter;

        searchFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID());
        this.searchFilters.add(searchFilter);

        if (null != filterOrgInfo) {

        }


        if (null != this.filter.getAssetGroup()) {
            searchFilter = new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.filter.getAssetGroup());
            this.searchFilters.add(searchFilter);
        }
    }

    public void onTransfer(TransferEvent event) {
//        for (Object item : event.getItems()) {
//            AssetInfo assetInfo = (AssetInfo)item;
//            if (event.isAdd()) {
//                if (!assets.getTarget().contains(assetInfo)) {
//                    assets.getTarget().add(assetInfo);
//                }
//
//            } else {
//                assets.getTarget().remove(assetInfo);
//            }
//        }
    }

    public void onSelectTreeNodeAsNewValue(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo siteOrg = (OrgInfo) node.getData();
        selected.setSiteOrg(siteOrg);

        if (null == siteOrg) {
            clinicalDeptList = Collections.emptyList();
        } else {
            clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(selected.getSiteOrg().getSiteUID());
        }

    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onClinicalDeptChange() {
        logger.info(selected.getClinicalDeptOrg().getName());


    }

    public void confirmAssetsSelection() {

    }

    public void updateSelectedPropertiesToChange() {

//        for (KeyLabel keyLabel: selectedKeyLabels) {
//
//
//        }
    }

    public boolean displayControl(String name) {
        for (String key: selectedKeyLabels) {
            if (key.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void commitBulkEdit() {

        List<AssetInfo> selectedAssets = assets.getTarget();

        assetInfoBulkEditService.commitBulkEdit(selectedAssets, selected);

    }

    public boolean noAssetSelected() {

//        List<AssetInfo> targetAssets = assets.getTarget();
//        return targetAssets.size() == 0;

        return false;
    }
}
