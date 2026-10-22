package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanAssetRepository;
import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanAssetSelectController extends JpaCRUDController<AssetInfo> {

    private AssetInfoRepository dao;
    private AcquisitionPlanRepository planDao;
    private AcquisitionPlanAssetRepository planAssetDao;
    private UaaService uaaService;
    private UserContextService userContextService;
    private UserAccount user;

    @Getter @Setter
    private String oneSearchStr;

    @Getter @Setter
    private boolean showDialog = false;

    @Getter @Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();

    @Getter @Setter
    private AcquisitionPlan selectedPlan;

    @Getter
    private List<AcquisitionPlan> planList;

    Page<AssetInfo> assetInfoPage;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        planDao = WebUtil.getBean(AcquisitionPlanRepository.class);
        planAssetDao = WebUtil.getBean(AcquisitionPlanAssetRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        user = UserContextService.getCurrentUserAccount();
        selectedMap = new HashMap<>();
        loadPlanList();
    }

    private void loadPlanList() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        planList = planDao.findBySearchFilter(filters);
        if (planList == null) {
            planList = new ArrayList<>();
        }
    }

    public String getPlanDisplayName(AcquisitionPlan plan) {
        if (plan == null) return "";
        StringBuilder sb = new StringBuilder();
        if (plan.getApplicationNo() != null) {
            sb.append(plan.getApplicationNo());
        }
        if (plan.getAssetName() != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(plan.getAssetName());
        }
        return sb.toString();
    }

    public void openDialog() {
        showDialog = true;
        selectedPlan = null;
        selectedMap = new HashMap<>();
        selected = null;
        oneSearchStr = null;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        this.selected = null;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));

        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(user.getAssetTypeGroupList())) {
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }

        if (!this.hasFilterOnField("siteUID")) {
            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream()
                    .map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }

        if (user.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN,
                    user.getAssetTypeGroupMap().keySet(), "1*"));
        }

        assetInfoPage = dao.findBySearchFilter(searchFilters, pageRequest);
        return assetInfoPage;
    }

    @Override
    public void setSearchFilter() {
        super.setSearchFilter();
        if (StringUtils.isNotEmpty(oneSearchStr)) {
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public void resetSearch() {
        oneSearchStr = null;
        if (searchFilters != null) {
            searchFilters.removeIf(f -> "departNum".equals(f.fieldName) ||
                    "financingNum".equals(f.fieldName) ||
                    "qrCode".equals(f.fieldName) ||
                    "serialNum".equals(f.fieldName));
        }
    }

    public void onSelectAllOfPage() {
        if (assetInfoPage == null) return;
        List<AssetInfo> currentPageAssets = assetInfoPage.getContent();
        boolean isSelectedAll = !currentPageAssets.stream()
                .anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public void onSelectRow() {
        // single row select handler - checkbox handles multi-select independently
    }

    public void confirmSelection() {
        if (selectedPlan == null) {
            WebUtil.addErrorMessage("请先选择购置计划！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        List<AssetInfo> selectedAssetList = new ArrayList<>();
        if (selectedMap != null && !selectedMap.isEmpty()) {
            for (Map.Entry<String, Boolean> entry : selectedMap.entrySet()) {
                if (Boolean.TRUE.equals(entry.getValue())) {
                    AssetInfo a = dao.findByUid(entry.getKey());
                    if (a != null) {
                        selectedAssetList.add(a);
                    }
                }
            }
        }

        if (selectedAssetList.isEmpty() && this.selected != null) {
            selectedAssetList.add(this.selected);
        }

        if (selectedAssetList.isEmpty()) {
            WebUtil.addErrorMessage("未选择任何设备！");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        // Create associations
        List<com.ge.apm.domain.acquisition.AcquisitionPlanAsset> existingList =
                planAssetDao.findByAcquisitionPlanId(selectedPlan.getId());
        Set<String> existingAssetUids = existingList.stream()
                .map(com.ge.apm.domain.acquisition.AcquisitionPlanAsset::getAssetUid)
                .collect(Collectors.toSet());

        int addedCount = 0;
        for (AssetInfo asset : selectedAssetList) {
            if (existingAssetUids.contains(asset.getUid())) {
                continue;
            }
            com.ge.apm.domain.acquisition.AcquisitionPlanAsset relation =
                    new com.ge.apm.domain.acquisition.AcquisitionPlanAsset();
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
            WebUtil.addSuccessMessage("成功关联 " + addedCount + " 台设备！");
        } else {
            WebUtil.addErrorMessage("所选设备已全部关联！");
        }

        showDialog = false;
        selectedMap.clear();
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }
}
