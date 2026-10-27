package com.ge.apm.view.acquisition;

import com.ge.apm.dao.AcquisitionPlanAssetRepository;
import com.ge.apm.dao.AcquisitionPlanRepository;
import com.ge.apm.dao.HisEquipmentDataResultRepository;
import com.ge.apm.dao.mapper.AcquisitionPlanAssetMapper;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.acquisition.AcquisitionPlan;
import com.ge.apm.domain.acquisition.AcquisitionPlanAsset;
import com.ge.apm.domain.acquisition.HospitalAreaClassification;
import com.ge.apm.service.acquisition.AcquisitionPlanService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
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
    private AcquisitionPlanAssetMapper planAssetMapper;
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

    private AcquisitionPlanService acquisitionPlanService;
    private UaaService uaaService;
    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(AcquisitionPlanAssetRepository.class);
        planAssetMapper = WebUtil.getBean(AcquisitionPlanAssetMapper.class);
        planDao = WebUtil.getBean(AcquisitionPlanRepository.class);
        hisDataResultDao = WebUtil.getBean(HisEquipmentDataResultRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        acquisitionPlanService = WebUtil.getBean(AcquisitionPlanService.class);
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
    }

    @Override
    protected Page<AcquisitionPlanAsset> loadData(PageRequest pageRequest) {
        selected = null;
        performanceDataAssetUids = Collections.emptySet();
        try {
            if (this.searchFilters == null) {
                searchFilters = new ArrayList();
            }

            Map<String, Object> param = new HashMap<>();
            param.put("tenantUid", loginUser.getTenantUID());
            param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
            param.put("pageSize", pageRequest.getPageSize());

            // 绩效过滤
            if ("hasData".equals(searchPerformance) || "noData".equals(searchPerformance)) {
                param.put("searchPerformance", searchPerformance);
            }

            // 权限过滤
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID(), "1*"));
            Set<String> functionList = UserContextService.getCurrentUserFunction();
            if (!functionList.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_DRUG_CENTER.name())
                    && !functionList.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_WORK_GROUP.name())
                    && !functionList.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_BUREAU_PARTY.name())
                    && !functionList.contains(ApmFunction.ACQUISITION_PLAN_MANAGEMENT_AUDIT_VIEW.name())) {
                if (!this.hasFilterOnField("hospitalUID")) {
                    HospitalAreaClassification hospitalAreaClassification = acquisitionPlanService.getHospitalAreaClassification(loginUser);
                    if (hospitalAreaClassification != null) {
                        List<String> hospitalUidList = acquisitionPlanService.getHospitals(hospitalAreaClassification, loginUser.getHospitalUID());
                        if (CollectionUtils.isNotEmpty(hospitalUidList)) {
                            param.put("hospitalUidList", hospitalUidList);
                        } else {
                            param.put("hospitalUid", loginUser.getHospitalUID());
                        }
                    } else {
                        List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                        param.put("institutionUidList", orgUidList);
                        param.put("hospitalUid", loginUser.getHospitalUID());
                        param.put("siteUid", loginUser.getSiteUID());
                    }
                }
            }

            // 列过滤条件转换
            covQueryCondition(this.searchFilters, param);

            List<AcquisitionPlanAsset> list = new ArrayList<>(planAssetMapper.selectList(param));
            Long count = planAssetMapper.selectListCount(param);

            Page<AcquisitionPlanAsset> page = new PageImpl<>(list, pageRequest, count);
            loadCurrentPagePerformanceUids(page);
            return page;
        } catch (Exception e) {
            log.error("loadData error: " + e.getMessage(), e);
            WebUtil.addErrorMessage("数据加载失败: " + e.getMessage());
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }
    }

    /**
     * 将 DataTable 列过滤的 SearchFilter 转换为 MyBatis 参数
     */
    private void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        if (searchFilters == null) return;
        for (SearchFilter sf : searchFilters) {
            String fieldName = sf.fieldName;
            switch (fieldName) {
                case "tenantUID":
                    // 已在 param 中设置，跳过
                    break;
                case "hospitalUID":
                    if (sf.operator == SearchFilter.Operator.IN && sf.value instanceof List) {
                        param.put("hospitalUidList", sf.value);
                    } else {
                        param.put("hospitalUid", sf.value);
                    }
                    break;
                case "institutionUID":
                    if (sf.value instanceof List) {
                        param.put("institutionUidList", sf.value);
                    }
                    break;
                case "siteUID":
                    param.put("siteUid", sf.value);
                    break;
                case "acquisitionPlan.applicationYear":
                    param.put("applicationYear", sf.value);
                    break;
                case "acquisitionPlan.applicationNo":
                    param.put("applicationNo", sf.value);
                    break;
                case "acquisitionPlan.applicationType":
                    param.put("applicationType", sf.value);
                    break;
                case "assetInfo.name":
                    param.put("assetName", sf.value);
                    break;
                case "assetInfo.functionType":
                    param.put("functionType", sf.value);
                    break;
                case "assetInfo.hospital.name":
                    param.put("hospitalName", sf.value);
                    break;
                case "assetInfo.clinicalDeptName":
                    param.put("clinicalDeptName", sf.value);
                    break;
                case "assetInfo.serialNum":
                    param.put("serialNum", sf.value);
                    break;
                case "assetInfo.qrCode":
                    param.put("qrCode", sf.value);
                    break;
                case "assetInfo.installDate":
                    if (sf.operator == SearchFilter.Operator.GTE) {
                        param.put("installDateGTE", sf.value);
                    } else if (sf.operator == SearchFilter.Operator.LTE) {
                        param.put("installDateLTE", sf.value);
                    }
                    break;
                default:
                    break;
            }
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

    public void search() {
        // searchFilters will be converted by covQueryCondition() in loadData()
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
