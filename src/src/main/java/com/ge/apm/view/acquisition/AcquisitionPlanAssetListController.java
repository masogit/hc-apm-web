package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanAssetRepository;
import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.HisEquipmentDataResultRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.AcquisitionPlanAsset;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AcquisitionPlanAssetListController extends GenericCRUDUUIDController<AcquisitionPlanAsset> {

    private static final Logger log = LoggerFactory.getLogger(AcquisitionPlanAssetListController.class);

    private AcquisitionPlanAssetRepository dao;
    private AcquisitionPlanRepository planDao;
    private HisEquipmentDataResultRepository hisDataResultDao;
    private UserAccount loginUser;
    private Set<String> performanceDataAssetUids = Collections.emptySet();

    @Getter @Setter
    private Map<String, Boolean> selectedMap = new HashMap<>();

    @Getter @Setter
    private String searchPerformance;

    @Getter
    private String benefitAssessIframeUrl;

    @Getter
    private List<String> yearList;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanAssetRepository.class);
        planDao = WebUtil.getBean(AcquisitionPlanRepository.class);
        hisDataResultDao = WebUtil.getBean(HisEquipmentDataResultRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        loadYearList();
    }

    public boolean hasPerformanceData(AcquisitionPlanAsset item) {
        return item != null && item.getAssetUid() != null
                && performanceDataAssetUids.contains(item.getAssetUid());
    }

    private void loadYearList() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        List<AcquisitionPlan> plans = planDao.findBySearchFilter(filters);
        if (plans == null) {
            plans = new ArrayList<>();
        }
        yearList = plans.stream()
                .map(AcquisitionPlan::getApplicationYear)
                .filter(y -> y != null && !y.isEmpty())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    protected boolean isDateTimeField(String fieldName) {
        if ("assetInfo.installDate".equals(fieldName)) {
            return true;
        }
        return super.isDateTimeField(fieldName);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        // 始终添加租户隔离过滤
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        // 绩效过滤
        if ("hasData".equals(searchPerformance) && !performanceDataAssetUids.isEmpty()) {
            this.searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN, new ArrayList<>(performanceDataAssetUids)));
        } else if ("noData".equals(searchPerformance) && !performanceDataAssetUids.isEmpty()) {
            this.searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.NotIN, new ArrayList<>(performanceDataAssetUids)));
        }
    }

    @Override
    protected Page<AcquisitionPlanAsset> loadData(PageRequest pageRequest) {
        selected = null;
        performanceDataAssetUids = Collections.emptySet();
        try {
            Page<AcquisitionPlanAsset> page;
            if ("hasData".equals(searchPerformance)) {
                page = dao.findWithPerformanceData(loginUser.getTenantUID(), pageRequest);
            } else if ("noData".equals(searchPerformance)) {
                page = dao.findWithoutPerformanceData(loginUser.getTenantUID(), pageRequest);
            } else {
                this.setSearchFilter();
                page = dao.findBySearchFilter(this.searchFilters, pageRequest);
            }
            // 加载当前页的绩效数据UID，用于按钮置灰判断
            loadCurrentPagePerformanceUids(page);
            return page;
        } catch (Exception e) {
            log.error("loadData error: " + e.getMessage(), e);
            WebUtil.addErrorMessage("数据加载失败: " + e.getMessage());
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }
    }

    private void loadCurrentPagePerformanceUids(Page<AcquisitionPlanAsset> page) {
        if (page == null || page.getContent().isEmpty()) {
            performanceDataAssetUids = Collections.emptySet();
            return;
        }
        List<String> assetUids = page.getContent().stream()
                .map(AcquisitionPlanAsset::getAssetUid)
                .filter(uid -> uid != null)
                .distinct()
                .collect(Collectors.toList());
        if (assetUids.isEmpty()) {
            performanceDataAssetUids = Collections.emptySet();
            return;
        }
        try {
            performanceDataAssetUids = hisDataResultDao.findAssetUidsWithPerformanceData(
                    assetUids, loginUser.getTenantUID());
        } catch (Exception e) {
            log.error("loadCurrentPagePerformanceUids error: " + e.getMessage(), e);
            performanceDataAssetUids = Collections.emptySet();
        }
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }
        // 不重复添加 tenantUID（onFilter 可能已经设置过了）
        boolean hasTenantFilter = false;
        for (SearchFilter sf : this.searchFilters) {
            if ("tenantUID".equals(sf.fieldName)) {
                hasTenantFilter = true;
                break;
            }
        }
        if (!hasTenantFilter) {
            this.searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        }
    }

    public void search() {
        // searchFilters will be built by setSearchFilter() called from loadData()
    }

    public void resetSearch() {
        this.searchPerformance = null;
        this.searchFilters = new ArrayList<>();
    }

    public void deleteItem(AcquisitionPlanAsset item) {
        try {
            dao.delete(item);
            WebUtil.addSuccessMessage("删除成功！");
        } catch (Exception e) {
            log.error("deleteItem error: " + e.getMessage(), e);
            WebUtil.addErrorMessage("删除失败: " + e.getMessage());
        }
    }

    public void removeSelected() {
        if (selectedMap == null || selectedMap.isEmpty()) {
            WebUtil.addErrorMessage("请先选择要删除的记录！");
            return;
        }
        List<String> idsToRemove = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : selectedMap.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                idsToRemove.add(entry.getKey());
            }
        }
        if (idsToRemove.isEmpty()) {
            WebUtil.addErrorMessage("请先选择要删除的记录！");
            return;
        }
        int removedCount = 0;
        for (String id : idsToRemove) {
            AcquisitionPlanAsset item = dao.findById(id);
            if (item != null) {
                dao.delete(item);
                removedCount++;
            }
        }
        selectedMap.clear();
        if (removedCount > 0) {
            WebUtil.addSuccessMessage("成功删除 " + removedCount + " 条关联记录！");
        }
    }

    public void benefitAssess(AcquisitionPlanAsset item) {
        if (item == null || item.getAssetUid() == null) {
            WebUtil.addErrorMessage("设备信息不完整，无法查看效益评估");
            return;
        }
        this.benefitAssessIframeUrl = "/reports/appraisal/benefit/" + item.getAssetUid();
    }

    public String goToAdd() {
        return "Add?faces-redirect=true";
    }

    @Override
    protected GenericRepositoryUUID<AcquisitionPlanAsset> getDAO() {
        return dao;
    }
}
