package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanAssetRepository;
import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.AcquisitionPlanAsset;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanAssetAddController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(AcquisitionPlanAssetAddController.class);

    private AcquisitionPlanRepository planDao;
    private AssetInfoRepository assetDao;
    private AcquisitionPlanAssetRepository planAssetDao;
    private OrgInfoRepository orgInfoDao;
    private UaaService uaaService;
    private UserAccount user;

    // ========== 采购信息搜索条件 ==========
    @Getter @Setter
    private String searchYear;
    @Getter @Setter
    private String searchAssetName;
    @Getter @Setter
    private String searchSpec;
    @Getter @Setter
    private String searchAppNo;

    @Getter
    private List<AcquisitionPlan> appNoOptions;

    @Getter
    private List<String> yearList;
    @Getter
    private List<AcquisitionPlan> planSearchResult;
    @Getter @Setter
    private AcquisitionPlan selectedPlan;
    @Getter @Setter
    private String dropdownPlanId;

    // ========== 设备搜索条件 ==========
    @Getter @Setter
    private OrgInfo selectedOrg;
    @Getter @Setter
    private OrgInfo filterDeptOrg;
    @Getter @Setter
    private TreeNode orgTree;
    @Getter @Setter
    private TreeNode selectedNode;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private String searchDeviceName;
    @Getter @Setter
    private String searchFunctionType;
    @Getter @Setter
    private String searchManufacture;
    @Getter @Setter
    private String searchVendor;

    @Getter
    private List<AssetInfo> deviceSearchResult;
    @Getter @Setter
    private Map<String, Boolean> deviceSelectedMap = new HashMap<>();

    // ========== 已选设备 ==========
    @Getter
    private List<AssetInfo> selectedDeviceList = new ArrayList<>();
    @Getter @Setter
    private Map<String, Boolean> chosenDeviceMap = new HashMap<>();

    Page<AssetInfo> devicePage;

    @PostConstruct
    public void init() {
        planDao = WebUtil.getBean(AcquisitionPlanRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        planAssetDao = WebUtil.getBean(AcquisitionPlanAssetRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        user = UserContextService.getCurrentUserAccount();
        loadYearList();
        initOrgTree();
    }

    private void loadYearList() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.EQ,
                AcquisitionPlan.ApplicationStatusEnum.Approved.name()));
        List<AcquisitionPlan> approvedPlans = planDao.findBySearchFilter(filters);
        if (approvedPlans == null) approvedPlans = new ArrayList<>();
        yearList = approvedPlans.stream()
                .map(AcquisitionPlan::getApplicationYear)
                .filter(y -> y != null && !y.isEmpty())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private void initOrgTree() {
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(user.getSiteUID(), user.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        if (treeNode != null) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = orgInfo;
        }
        loadClinicalDept();
    }

    private void loadClinicalDept() {
        if (selectedOrg == null) {
            clinicalDeptList = Collections.emptyList();
        } else {
            clinicalDeptList = orgInfoDao.getUserAccessibleClinicalDeptsBySelectedSite(
                    selectedOrg.getUid(), user.getUid());
        }
        filterDeptOrg = null;
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query)
                        || item.getName().contains(query)
                        || (item.getNamePyCode() != null && item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onAppNoSelect() {
        selectedPlan = null;
        if (dropdownPlanId != null && appNoOptions != null) {
            AcquisitionPlan plan = appNoOptions.stream()
                    .filter(p -> dropdownPlanId.equals(p.getId()))
                    .findFirst().orElse(null);
            if (plan != null) {
                planSearchResult = new ArrayList<>();
                planSearchResult.add(plan);
            } else {
                planSearchResult = null;
            }
        } else {
            planSearchResult = null;
        }
    }

    public void loadAppNoList() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.EQ,
                AcquisitionPlan.ApplicationStatusEnum.Approved.name()));
        if (StringUtils.isNotEmpty(searchYear)) {
            filters.add(new SearchFilter("applicationYear", SearchFilter.Operator.EQ, searchYear));
        }
        if (StringUtils.isNotEmpty(searchAssetName)) {
            filters.add(new SearchFilter("assetName", SearchFilter.Operator.LIKE, searchAssetName));
        }
        if (StringUtils.isNotEmpty(searchSpec)) {
            filters.add(new SearchFilter("specifications", SearchFilter.Operator.LIKE, searchSpec));
        }
        try {
            appNoOptions = planDao.findBySearchFilter(filters);
        } catch (Exception e) {
            appNoOptions = new ArrayList<>();
        }
        if (appNoOptions == null) appNoOptions = new ArrayList<>();
    }

    // ========== 采购单搜索 ==========
    public void searchPlans() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.EQ,
                AcquisitionPlan.ApplicationStatusEnum.Approved.name()));

        if (StringUtils.isNotEmpty(searchYear)) {
            filters.add(new SearchFilter("applicationYear", SearchFilter.Operator.EQ, searchYear));
        }
        if (StringUtils.isNotEmpty(searchAssetName)) {
            filters.add(new SearchFilter("assetName", SearchFilter.Operator.LIKE, searchAssetName));
        }
        if (StringUtils.isNotEmpty(searchSpec)) {
            filters.add(new SearchFilter("specifications", SearchFilter.Operator.LIKE, searchSpec));
        }
        if (StringUtils.isNotEmpty(searchAppNo)) {
            filters.add(new SearchFilter("applicationNo", SearchFilter.Operator.LIKE, searchAppNo));
        }
        try {
            planSearchResult = planDao.findBySearchFilter(filters);
        } catch (Exception e) {
            planSearchResult = new ArrayList<>();
        }
        if (planSearchResult == null) planSearchResult = new ArrayList<>();
    }

    public void resetPlanSearch() {
        searchYear = null;
        searchAssetName = null;
        searchSpec = null;
        searchAppNo = null;
        dropdownPlanId = null;
        planSearchResult = null;
        selectedPlan = null;
        appNoOptions = null;
    }

    /** 获取采购单的绑定情况，如 "2/5" */
    public String getBindingStatus(AcquisitionPlan plan) {
        if (plan == null || plan.getId() == null) return "0/0";
        List<AcquisitionPlanAsset> existing = planAssetDao.findByAcquisitionPlanId(plan.getId());
        int bound = existing != null ? existing.size() : 0;
        int total = plan.getApplicationPlanCount() != null ? plan.getApplicationPlanCount() : 0;
        return bound + "/" + total;
    }

    // ========== 设备搜索 ==========

    public void searchDevices() {
        if (selectedOrg == null) {
            WebUtil.addErrorMessage("所属院区必填");
            return;
        }
        if (filterDeptOrg == null) {
            WebUtil.addErrorMessage("所属科室必填");
            return;
        }


        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        filters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(user.getAssetTypeGroupList())) {
            log.info("设备搜索 - 用户无资产分类权限，返回空结果");
            deviceSearchResult = new ArrayList<>();
            return;
        }

        // 院区过滤 - 获取选中节点下所有院区UID
        List<String> accessibleSites = orgInfoDao.getSiteOrgUidsByParentUid(selectedOrg.getUid());
        filters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));

        // 科室过滤
        if (filterDeptOrg != null) {
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filterDeptOrg.getUid()));
        }
        // 资产名称
        if (StringUtils.isNotEmpty(searchDeviceName)) {
            filters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, searchDeviceName));
        }
        // 设备型号
        if (StringUtils.isNotEmpty(searchFunctionType)) {
            filters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, searchFunctionType));
        }
        // 制造商
        if (StringUtils.isNotEmpty(searchManufacture)) {
            filters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, searchManufacture));
        }
        // 品牌/制造商简称
        if (StringUtils.isNotEmpty(searchVendor)) {
            filters.add(new SearchFilter("vendor", SearchFilter.Operator.LIKE, searchVendor));
        }

        // 资产分类权限
        if (user.isEnableAssetTypeGroup()) {
            filters.add(new SearchFilter("assetType", SearchFilter.Operator.IN,
                    user.getAssetTypeGroupMap().keySet()));
        }

        try {
            deviceSearchResult = assetDao.findBySearchFilter(filters);
        } catch (Exception e) {
            log.error("设备搜索异常", e);
            deviceSearchResult = new ArrayList<>();
        }
        if (deviceSearchResult == null) deviceSearchResult = new ArrayList<>();
        deviceSelectedMap = new HashMap<>();
    }

    public void resetDeviceSearch() {
        selectedOrg = null;
        filterDeptOrg = null;
        clinicalDeptList = null;
        searchDeviceName = null;
        searchFunctionType = null;
        searchManufacture = null;
        searchVendor = null;
        deviceSearchResult = null;
        deviceSelectedMap = new HashMap<>();
    }

    // ========== 设备选择操作 ==========
    public void onSelectAllDevices() {
        if (deviceSearchResult == null) return;
        boolean allSelected = deviceSearchResult.stream()
                .allMatch(a -> Boolean.TRUE.equals(deviceSelectedMap.get(a.getUid())));
        for (AssetInfo a : deviceSearchResult) {
            deviceSelectedMap.put(a.getUid(), !allSelected);
        }
    }

    public void addCheckedDevices() {
        if (deviceSelectedMap == null || deviceSelectedMap.isEmpty()) {
            WebUtil.addErrorMessage("请先勾选设备！");
            return;
        }
        Set<String> existingUids = selectedDeviceList.stream()
                .map(AssetInfo::getUid).collect(Collectors.toSet());

        int addedCount = 0;
        for (Map.Entry<String, Boolean> entry : deviceSelectedMap.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue()) && !existingUids.contains(entry.getKey())) {
                AssetInfo asset = assetDao.findByUid(entry.getKey());
                if (asset != null) {
                    selectedDeviceList.add(asset);
                    addedCount++;
                }
            }
        }
        deviceSelectedMap.clear();
        if (addedCount > 0) {
            WebUtil.addSuccessMessage("已添加 " + addedCount + " 台设备到已选列表！");
        } else {
            WebUtil.addErrorMessage("未勾选任何设备或设备已在已选列表中！");
        }
    }

    public void removeCheckedDevices() {
        if (chosenDeviceMap == null || chosenDeviceMap.isEmpty()) {
            WebUtil.addErrorMessage("请先勾选要移除的设备！");
            return;
        }
        List<AssetInfo> toRemove = new ArrayList<>();
        for (AssetInfo a : selectedDeviceList) {
            if (Boolean.TRUE.equals(chosenDeviceMap.get(a.getUid()))) {
                toRemove.add(a);
            }
        }
        selectedDeviceList.removeAll(toRemove);
        chosenDeviceMap.clear();
        if (!toRemove.isEmpty()) {
            WebUtil.addSuccessMessage("已移除 " + toRemove.size() + " 台设备！");
        }
    }

    public void clearAllDevices() {
        selectedDeviceList.clear();
        chosenDeviceMap.clear();
    }

    public void onSelectAllChosen() {
        if (selectedDeviceList == null) return;
        boolean allSelected = selectedDeviceList.stream()
                .allMatch(a -> Boolean.TRUE.equals(chosenDeviceMap.get(a.getUid())));
        for (AssetInfo a : selectedDeviceList) {
            chosenDeviceMap.put(a.getUid(), !allSelected);
        }
    }

    // ========== 确认绑定 ==========
    public String confirmAdd() {
        if (selectedPlan == null) {
            WebUtil.addErrorMessage("请先选择一个采购单！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return null;
        }
        if (selectedDeviceList.isEmpty()) {
            WebUtil.addErrorMessage("请至少添加一台设备！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return null;
        }

        // 查询已有绑定
        List<AcquisitionPlanAsset> existing = planAssetDao.findByAcquisitionPlanId(selectedPlan.getId());
        Set<String> existingUids = existing.stream()
                .map(AcquisitionPlanAsset::getAssetUid).collect(Collectors.toSet());

        // 统计本次实际新增的设备数（排除已绑定的）
        int newDeviceCount = 0;
        for (AssetInfo asset : selectedDeviceList) {
            if (!existingUids.contains(asset.getUid())) {
                newDeviceCount++;
            }
        }

        // 校验总数量：已绑定数 + 本次新增数 <= 购置数量
        int planCount = selectedPlan.getApplicationPlanCount() != null ? selectedPlan.getApplicationPlanCount() : 0;
        if (existingUids.size() + newDeviceCount > planCount) {
            WebUtil.addErrorMessage("该采购单已绑定 " + existingUids.size() + " 台设备，购置数量为 "
                    + planCount + "，本次新增 " + newDeviceCount + " 台将超出限制！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return null;
        }

        // 校验设备是否已绑定其他购置单
        List<String> conflictNames = new ArrayList<>();
        for (AssetInfo asset : selectedDeviceList) {
            if (existingUids.contains(asset.getUid())) {
                continue; // 已绑定到当前采购单，跳过
            }
            List<AcquisitionPlanAsset> otherBindings = planAssetDao.findByAssetUid(asset.getUid());
            if (otherBindings != null && !otherBindings.isEmpty()) {
                // 检查是否绑定到了其他采购单
                boolean boundToOther = otherBindings.stream()
                        .anyMatch(b -> !selectedPlan.getId().equals(b.getAcquisitionPlanId()));
                if (boundToOther) {
                    conflictNames.add(asset.getName());
                }
            }
        }
        if (!conflictNames.isEmpty()) {
            String names = conflictNames.size() > 3
                    ? String.join("、", conflictNames.subList(0, 3)) + " 等" + conflictNames.size() + "台设备"
                    : String.join("、", conflictNames);
            WebUtil.addErrorMessage(names + " 已绑定其他购置单，请先解除绑定后再操作！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return null;
        }

        int addedCount = 0;
        for (AssetInfo asset : selectedDeviceList) {
            if (existingUids.contains(asset.getUid())) {
                continue;
            }
            AcquisitionPlanAsset relation = new AcquisitionPlanAsset();
            relation.setAcquisitionPlanId(selectedPlan.getId());
            relation.setAssetUid(asset.getUid());
            relation.setTenantUID(user.getTenantUID());
            relation.setInstitutionUID(user.getInstitutionUID());
            relation.setHospitalUID(user.getHospitalUID());
            relation.setSiteUID(user.getSiteUID());
            relation.setCreatedBy(user.getLoginName());
            relation.setCreatedDate(new Date());
            planAssetDao.save(relation);
            addedCount++;
        }

        if (addedCount > 0) {
            WebUtil.addSuccessMessage("成功绑定 " + addedCount + " 台设备！");
            return "List?faces-redirect=true";
        } else {
            WebUtil.addErrorMessage("所选设备已全部绑定！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return null;
        }
    }

    public String cancel() {
        return "List?faces-redirect=true";
    }
}
