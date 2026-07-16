package com.ge.apm.view.surgeryAnesthesia;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 手术室绩效管理 - 批量添加绩效设备Controller
 */
@ManagedBean
@ViewScoped
public class SurgeryAnesthesiaEquipmentCreateBatchController {

    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;
    private UaaService uaaService;
    private UserAccount user;

    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private TreeNode selectedNode;

    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private List<AssetInfo> selectAssetInfos = new ArrayList<>();

    @Getter @Setter
    private List<AssetInfo> selectAsset = new ArrayList<>();

    @Getter @Setter
    private Map<String, Boolean> selectedAssetUidTable1 = new HashMap<>();

    @Getter @Setter
    private Map<String, Boolean> selectedAssetUidTable2 = new HashMap<>();

    @Getter @Setter
    private Boolean allSelected1 = false;

    @Getter @Setter
    private Boolean allSelected2 = false;

    @Getter @Setter
    private Double purchasePrice = 0d;

    @Getter @Setter
    private String filterName;

    @Getter @Setter
    private String filterDepartNum;

    @Getter @Setter
    private String filterSerialNum;

    @Getter @Setter
    private String filterQrCode;

    @Getter @Setter
    private OrgInfo filterDeptOrg;

    @Getter @Setter
    private Integer assetGroup;

    private List<OrgInfo> clinicalDeptList;

    private SurgeryAnesthesiaEquipmentInfoController parentController;

    @PostConstruct
    public void init() {
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        user = UserContextService.getCurrentUserAccount();
        parentController = WebUtil.getBean(SurgeryAnesthesiaEquipmentInfoController.class);
        initOrgTree();
    }

    /**
     * 按用户权限范围构建组织树，同 HIS 效益设备
     */
    private void initOrgTree() {
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(user.getSiteUID(), user.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName("全部院区");
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        if (treeNode != null) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    /**
     * 选中院区节点后加载该院区下的科室列表
     */
    public void onSelectTreeNode(org.primefaces.event.NodeSelectEvent event) {
        if (event.getTreeNode() != null && event.getTreeNode().getData() != null) {
            OrgInfo orgInfo = (OrgInfo) event.getTreeNode().getData();
            if (orgInfo.getId() != null && orgInfo.getId() == -1) {
                selectedOrg = null;
            } else {
                selectedOrg = orgInfo;
            }
        }
        loadClinicalDeptList();
    }

    /**
     * 根据所选院区加载可见科室
     */
    private void loadClinicalDeptList() {
        if (selectedOrg == null) {
            clinicalDeptList = Collections.emptyList();
        } else {
            clinicalDeptList = orgInfoRepository.getUserAccessibleClinicalDeptsBySelectedSite(
                    selectedOrg.getUid(), user.getUid());
        }
        filterDeptOrg = null;
    }

    /**
     * 科室自动补全
     */
    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = clinicalDeptList.stream()
                .filter(item -> StringUtils.isBlank(query)
                        || (item.getName() != null && item.getName().contains(query))
                        || (item.getNamePyCode() != null && item.getNamePyCode().toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    /**
     * 科室变更时重新查询
     */
    public void onClinicalDeptChange() {
        getQueryAssetsList();
    }

    /**
     * 查询可选设备列表
     */
    public void getQueryAssetsList() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        filters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        if (selectedOrg != null) {
            List<String> accessibleSites = orgInfoRepository.getSiteOrgUidsByParentUid(selectedOrg.getUid());
            filters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
        }

        if (purchasePrice != null && purchasePrice > 0) {
            filters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, purchasePrice));
        }
        if (StringUtils.isNotBlank(filterName)) {
            filters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, filterName.trim()));
        }
        if (StringUtils.isNotBlank(filterDepartNum)) {
            filters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, filterDepartNum.trim()));
        }
        if (StringUtils.isNotBlank(filterSerialNum)) {
            filters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, filterSerialNum.trim()));
        }
        if (StringUtils.isNotBlank(filterQrCode)) {
            filters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, filterQrCode.trim()));
        }
        if (filterDeptOrg != null) {
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filterDeptOrg.getUid()));
        }
        if (assetGroup != null) {
            filters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetGroup));
        }

        PageRequest pageRequest = new PageRequest(0, 500);
        selectAssetInfos = new ArrayList<>(assetInfoRepository.findBySearchFilter(filters, pageRequest).getContent());
        selectedAssetUidTable1.clear();
    }

    /**
     * 添加已勾选设备到右侧列表
     */
    public void addCheckedDevice() {
        List<String> selectedUids = selectedAssetUidTable1.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (AssetInfo asset : selectAssetInfos) {
            if (selectedUids.contains(asset.getUid())) {
                // 检查是否已在右侧
                boolean exists = selectAsset.stream().anyMatch(a -> a.getUid().equals(asset.getUid()));
                if (!exists) {
                    selectAsset.add(asset);
                }
            }
        }
        selectedAssetUidTable1.clear();
    }

    /**
     * 删除右侧已勾选设备
     */
    public void deleteCheckedDevice() {
        List<String> selectedUids = selectedAssetUidTable2.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        selectAsset = selectAsset.stream()
                .filter(a -> !selectedUids.contains(a.getUid()))
                .collect(Collectors.toList());
        selectedAssetUidTable2.clear();
    }

    /**
     * 全部清空
     */
    public void clearAll() {
        selectAsset.clear();
        selectedAssetUidTable1.clear();
        selectedAssetUidTable2.clear();
    }

    /**
     * 保存
     */
    public void save() {
        if (parentController == null) {
            parentController = WebUtil.getBean(SurgeryAnesthesiaEquipmentInfoController.class);
        }
        parentController.addBatch(selectAsset);
        clearAll();
    }

    /**
     * 更新左侧选中列表
     */
    public void updateSelctedAssetList1() {
        // no-op
    }

    /**
     * 更新右侧选中列表
     */
    public void updateSelctedAssetList2() {
        // no-op
    }

    /**
     * 左侧全选当前页
     */
    public void addAssetsOfCurrentPage1() {
        if (selectAssetInfos == null) return;
        for (AssetInfo item : selectAssetInfos) {
            selectedAssetUidTable1.put(item.getUid(), allSelected1);
        }
    }

    /**
     * 右侧全选当前页
     */
    public void addAssetsOfCurrentPage2() {
        if (selectAsset == null) return;
        for (AssetInfo item : selectAsset) {
            selectedAssetUidTable2.put(item.getUid(), allSelected2);
        }
    }
}