package com.ge.apm.view.his;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.HisAdministrativeOfficesRepository;
import com.ge.apm.dao.HisEquipmentDataSourceRepository;
import com.ge.apm.dao.HisEquipmentSfxmRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.HisRequestParamsDto;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.domain.his.HisEquipmentDataSource;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.primefaces.component.datatable.DataTable;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * HIS收费明细源数据ES查询控制器
 * 从Elasticsearch查询收费结果数据，表结构和字段与DB一致
 */
@ManagedBean
@ViewScoped
public class HisDataSourceEsController extends GenericCRUDController<HisEquipmentDataSource> implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(HisDataSourceEsController.class);

    /** ES索引名称，与数据库表his_equipment_data_source对应 */
    private static final String ES_INDEX = "his_equipment_data_source";

    private ElasticService elasticService;
    private HisEquipmentDataSourceRepository sourceDao;
    private OrgInfoRepository orgInfoRepository;
    private HisAdministrativeOfficesRepository hisAdministrativeOfficesRepository;
    private HisEquipmentSfxmRepository hisEquipmentSfxmRepository;

    private UserAccount loginUser;
    private OrgInfo selectedOrg;

    @Getter @Setter
    private HisRequestParamsDto hisRequestParamsDto;

    @Getter @Setter
    private List<OrgInfo> execDeptList;

    @Getter @Setter
    private String filterExecDept;

    @Getter @Setter
    private Set<String> execDeptOrgUidSet;

    @Getter @Setter
    private String filterProjectName;

    @Getter @Setter
    private String filterSfxmLb;

    @Getter @Setter
    private Set<String> filterDeviceDeptCodeSet;

    @Getter @Setter
    private Set<String> filterDeviceDeptCodeInSet;

    private List<HisEquipmentSfxm> sfxmList;

    @Getter @Setter
    private Long datalistSize = 0L;

    private HisOrgComponentController orgComponentController;

    @Override
    protected void init() {
        elasticService = WebUtil.getBean(ElasticService.class);
        sourceDao = WebUtil.getBean(HisEquipmentDataSourceRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        hisAdministrativeOfficesRepository = WebUtil.getBean(HisAdministrativeOfficesRepository.class);
        hisEquipmentSfxmRepository = WebUtil.getBean(HisEquipmentSfxmRepository.class);

        loginUser = UserContextService.getCurrentUserAccount();
        selectedOrg = orgInfoRepository.getByUid(loginUser.getOrgInfoUID());

        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgComponentController.registerController(this);

        hisRequestParamsDto = new HisRequestParamsDto();
        hisRequestParamsDto.setEndDate(new Date());
        hisRequestParamsDto.setStartDate(DateUtil.addMonth(new Date(), -3));

        filterDeviceDeptCodeSet = new HashSet<>();
        filterDeviceDeptCodeInSet = new HashSet<>();

        loadExecDeptList();
        loadSfxmList();
    }

    @Override
    protected GenericRepository<HisEquipmentDataSource> getDAO() {
        return null;
    }

    /** Java实体字段名 → ES字段名 映射（用于PrimeFaces filterBy） */
    private static final Map<String, String> ES_FIELD_MAP = new HashMap<>();
    static {
        ES_FIELD_MAP.put("deviceCode", "device_code");
        ES_FIELD_MAP.put("patientRegistrationNum", "patient_registration_num");
        ES_FIELD_MAP.put("prescriptionNum", "prescription_num");
        ES_FIELD_MAP.put("deviceDeptCode", "device_dept_code");
        ES_FIELD_MAP.put("execDeptCode", "exec_dept_code");
        ES_FIELD_MAP.put("sourceCode", "source_code");
        ES_FIELD_MAP.put("isMatched", "is_matched");
        ES_FIELD_MAP.put("receiptNum", "receipt_num");
        ES_FIELD_MAP.put("totalAmount", "total_amount");
        ES_FIELD_MAP.put("execTime", "exec_time");
    }

    /**
     * 从ES加载收费明细源数据
     */
    @Override
    protected Page<HisEquipmentDataSource> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }

        searchFilters.add(new SearchFilter("tenant_uid", SearchFilter.Operator.EQ, loginUser.getTenantUID()));

        if (selectedOrg != null) {
            searchFilters.add(new SearchFilter("org_code", SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
            searchFilters.add(new SearchFilter("site_uid", SearchFilter.Operator.EQ, selectedOrg.getSiteUID()));
        }

        if (hisRequestParamsDto.getStartDate() != null) {
            searchFilters.add(new SearchFilter("exec_time", SearchFilter.Operator.GTE, hisRequestParamsDto.getStartDate()));
        }
        if (hisRequestParamsDto.getEndDate() != null) {
            searchFilters.add(new SearchFilter("exec_time", SearchFilter.Operator.LT, DateUtil.getDateAddOneDay(hisRequestParamsDto.getEndDate())));
        }

        if (execDeptOrgUidSet != null && !execDeptOrgUidSet.isEmpty()) {
            searchFilters.add(new SearchFilter("exec_dept_code", SearchFilter.Operator.IN, new ArrayList<>(execDeptOrgUidSet)));
        }

        // 收费项目名称/分类 下拉筛选（转换为收费项目代码）
        if (filterDeviceDeptCodeSet != null && !filterDeviceDeptCodeSet.isEmpty()) {
            if (filterDeviceDeptCodeInSet != null && !filterDeviceDeptCodeInSet.isEmpty()) {
                Set<String> intersection = new HashSet<>(filterDeviceDeptCodeSet);
                intersection.retainAll(filterDeviceDeptCodeInSet);
                if (intersection.size() == 1) {
                    searchFilters.add(new SearchFilter("device_dept_code", SearchFilter.Operator.EQ, intersection.iterator().next()));
                } else if (intersection.size() > 1) {
                    searchFilters.add(new SearchFilter("device_dept_code", SearchFilter.Operator.IN, new ArrayList<>(intersection)));
                } else {
                    searchFilters.add(new SearchFilter("device_dept_code", SearchFilter.Operator.EQ, "__NO_MATCH__"));
                }
            } else {
                if (filterDeviceDeptCodeSet.size() == 1) {
                    searchFilters.add(new SearchFilter("device_dept_code", SearchFilter.Operator.EQ, filterDeviceDeptCodeSet.iterator().next()));
                } else {
                    searchFilters.add(new SearchFilter("device_dept_code", SearchFilter.Operator.IN, new ArrayList<>(filterDeviceDeptCodeSet)));
                }
            }
        } else if (filterDeviceDeptCodeInSet != null && !filterDeviceDeptCodeInSet.isEmpty()) {
            searchFilters.add(new SearchFilter("device_dept_code", SearchFilter.Operator.IN, new ArrayList<>(filterDeviceDeptCodeInSet)));
        }

        // 从 DataTable 组件直接提取表头文本过滤值，转换字段名后加入 searchFilters
        extractFiltersFromDataTable();

        String sortField = "exec_time";
        String sortDirection = "DESC";
        if (pageRequest.getSort() != null) {
            Iterator<Sort.Order> orders = pageRequest.getSort().iterator();
            if (orders.hasNext()) {
                Sort.Order order = orders.next();
                sortField = mapSortField(order.getProperty());
                sortDirection = order.isAscending() ? "ASC" : "DESC";
            }
        }

        Page<Object> esPage = elasticService.searchHistory(
                searchFilters,
                UserContextService.getAccessToken(),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                ES_INDEX,
                sortField,
                sortDirection
        );

        List<HisEquipmentDataSource> list = new ArrayList<>();
        if (esPage != null && esPage.getContent() != null) {
            for (Object obj : esPage.getContent()) {
                try {
                    HisEquipmentDataSource item = JSON.parseObject(obj.toString(), HisEquipmentDataSource.class);
                    list.add(item);
                } catch (Exception e) {
                    logger.warn("ES data parse error: {}", e.getMessage());
                }
            }
            datalistSize = esPage.getTotalElements();
        } else {
            datalistSize = 0L;
        }

        // 查询结果后关联查询收费项目名称、收费项目分类、执行科室
        enrichWithLookupData(list);

        return new PageImpl<>(list, pageRequest, datalistSize);
    }

    /**
     * 从关联表中查询并填充：收费项目名称(projectName)、收费项目分类(sfxmLb)、执行科室(execDept)
     * 关联逻辑与DB版本Mapper SQL一致：
     *   his_equipment_sfxm: device_dept_code = sfxm_id AND org_code = hos_id AND tenant_uid = tenant_uid
     *   his_administrative_offices: exec_dept_code = his_ksid AND org_code = hos_id AND tenant_uid = tenant_uid
     */
    private void enrichWithLookupData(List<HisEquipmentDataSource> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        String tenantUid = loginUser.getTenantUID();

        // 构建收费项目(sfxm)查找Map: key = "sfxmId|hosId"
        List<HisEquipmentSfxm> sfxmList = hisEquipmentSfxmRepository.findByTenantUid(tenantUid);
        Map<String, HisEquipmentSfxm> sfxmMap = new HashMap<>();
        if (sfxmList != null) {
            for (HisEquipmentSfxm sfxm : sfxmList) {
                String key = sfxm.getSfxmId() + "|" + sfxm.getHosId();
                sfxmMap.put(key, sfxm);
            }
        }

        // 构建科室(hao)查找Map: key = "hisKsid|hosId"
        List<HisAdministrativeOffices> haoList = hisAdministrativeOfficesRepository.findByTenantUid(tenantUid);
        Map<String, HisAdministrativeOffices> haoMap = new HashMap<>();
        if (haoList != null) {
            for (HisAdministrativeOffices hao : haoList) {
                String key = hao.getHisKsid() + "|" + hao.getHosId();
                haoMap.put(key, hao);
            }
        }

        // 为每条记录填充关联字段
        for (HisEquipmentDataSource item : list) {
            // 收费项目名称 & 收费项目分类 (from his_equipment_sfxm)
            if (!Strings.isNullOrEmpty(item.getDeviceDeptCode()) && !Strings.isNullOrEmpty(item.getOrgCode())) {
                String sfxmKey = item.getDeviceDeptCode() + "|" + item.getOrgCode();
                HisEquipmentSfxm sfxm = sfxmMap.get(sfxmKey);
                if (sfxm != null) {
                    item.setProjectName(sfxm.getSfxmName());
                    item.setSfxmLb(sfxm.getSfxmLb());
                    item.setSfxmName(sfxm.getSfxmName());
                }
            }

            // 执行科室 (from his_administrative_offices)
            if (!Strings.isNullOrEmpty(item.getExecDeptCode()) && !Strings.isNullOrEmpty(item.getOrgCode())) {
                String haoKey = item.getExecDeptCode() + "|" + item.getOrgCode();
                HisAdministrativeOffices hao = haoMap.get(haoKey);
                if (hao != null) {
                    item.setExecDept(hao.getHisKsname());
                    item.setKsName(hao.getHisKsname());
                }
            }
        }
    }

    /**
     * 直接从 PrimeFaces DataTable 组件提取表头过滤值，
     * 将 Java 实体字段名转换为 ES 字段名后加入 searchFilters。
     * 不依赖父类 onFilter(FilterEvent)，避免 MatchMode 兼容问题。
     */
    @SuppressWarnings("unchecked")
    private void extractFiltersFromDataTable() {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc == null || fc.getViewRoot() == null) return;
            DataTable table = (DataTable) fc.getViewRoot().findComponent("HisDataSourceTab:IouListForm:datalist");
            if (table == null) return;
            Map<String, Object> filters = table.getFilters();
            if (filters == null) return;

            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String javaFieldName = entry.getKey();
                Object filterValue = entry.getValue();
                if (filterValue == null) continue;
                String strValue = filterValue.toString().trim();
                if (strValue.isEmpty()) continue;

                String esFieldName = ES_FIELD_MAP.get(javaFieldName);
                if (esFieldName == null) continue; // 非映射字段跳过（如下拉筛选已自行处理）

                // 根据 ES 字段类型选择匹配模式
                if ("is_matched".equals(esFieldName)) {
                    searchFilters.add(new SearchFilter(esFieldName, SearchFilter.Operator.EQ, strValue));
                } else if ("receipt_num".equals(esFieldName) || "total_amount".equals(esFieldName)) {
                    searchFilters.add(new SearchFilter(esFieldName, SearchFilter.Operator.GTE, strValue));
                } else {
                    searchFilters.add(new SearchFilter(esFieldName, SearchFilter.Operator.LIKE, strValue));
                }
            }
        } catch (Exception e) {
            logger.warn("Extract DataTable filters error: {}", e.getMessage());
        }
    }

    private String mapSortField(String property) {
        if (property == null) {
            return "exec_time";
        }
        switch (property) {
            case "id": return "id";
            case "execTime": return "exec_time";
            case "totalAmount": return "total_amount";
            case "receiptNum": return "receipt_num";
            case "deviceCode": return "device_code";
            case "deviceDeptCode": return "device_dept_code";
            case "execDeptCode": return "exec_dept_code";
            case "createDate": return "create_date";
            default: return "exec_time";
        }
    }

    @Override
    public List<HisEquipmentDataSource> getItemList() {
        return sourceDao.find();
    }

    /**
     * 拦截 PrimeFaces filter 事件，阻止父类 onFilter(FilterEvent) 执行。
     * 过滤值由 extractFiltersFromDataTable() 在 loadData() 中直接从 DataTable 提取。
     */
    public void onFilter(AjaxBehaviorEvent event) {
        // 不执行任何操作，父类 onFilter(FilterEvent) 不会被调用
    }

    public void getEndDateChange() {
        if (hisRequestParamsDto.getStartDate() != null) {
            if (hisRequestParamsDto.getEndDate() != null
                    && !hisRequestParamsDto.getEndDate().before(DateUtil.addMonth(hisRequestParamsDto.getStartDate(), 12))) {
                hisRequestParamsDto.setEndDate(DateUtil.addMonth(hisRequestParamsDto.getStartDate(), 12));
            }
        }
    }

    public Date getMaxDate() {
        if (hisRequestParamsDto.getStartDate() != null) {
            return DateUtil.addMonth(hisRequestParamsDto.getStartDate(), 12);
        }
        return null;
    }

    private void loadExecDeptList() {
        List<HisAdministrativeOffices> lists = hisAdministrativeOfficesRepository.findByTenantUid(loginUser.getTenantUID());
        execDeptList = new ArrayList<>();
        for (HisAdministrativeOffices item : lists) {
            OrgInfo orgInfo = new OrgInfo();
            orgInfo.setName(item.getHisKsname());
            orgInfo.setUid(item.getHisKsid());
            orgInfo.setSiteUID(item.getSiteUid());
            execDeptList.add(orgInfo);
        }
        if (selectedOrg != null) {
            execDeptList = execDeptList.stream()
                    .filter(item -> item.getSiteUID().equals(selectedOrg.getSiteUID()))
                    .collect(Collectors.toList());
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        if (execDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.execDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(OrgInfo::getName).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof org.primefaces.event.SelectEvent) {
            filterExecDept = (String) ((org.primefaces.event.SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterExecDept)
                && !execDeptList.stream().anyMatch(item -> filterExecDept.equals(item.getName()))) {
            filterExecDept = null;
        }
        if (filterExecDept != null) {
            List<OrgInfo> matchedOrgs = this.execDeptList.stream()
                    .filter(item -> item.getName().equals(filterExecDept))
                    .collect(Collectors.toList());
            execDeptOrgUidSet = matchedOrgs.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            execDeptOrgUidSet = Collections.emptySet();
        }
    }

    private void loadSfxmList() {
        sfxmList = hisEquipmentSfxmRepository.findByTenantUid(loginUser.getTenantUID());
        if (sfxmList == null) {
            sfxmList = new ArrayList<>();
        }
        if (selectedOrg != null) {
            final String siteUid = selectedOrg.getSiteUID();
            sfxmList = sfxmList.stream()
                    .filter(item -> siteUid.equals(item.getSiteUid()))
                    .collect(Collectors.toList());
        }
    }

    public List<String> autoCompleteProjectName(String query) {
        if (sfxmList == null) {
            return Collections.emptyList();
        }
        return sfxmList.stream()
                .filter(item -> !Strings.isNullOrEmpty(item.getSfxmName())
                        && (Strings.isNullOrEmpty(query) || item.getSfxmName().contains(query)))
                .map(HisEquipmentSfxm::getSfxmName)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> autoCompleteSfxmLb(String query) {
        if (sfxmList == null) {
            return Collections.emptyList();
        }
        return sfxmList.stream()
                .filter(item -> !Strings.isNullOrEmpty(item.getSfxmLb())
                        && (Strings.isNullOrEmpty(query) || item.getSfxmLb().contains(query)))
                .map(HisEquipmentSfxm::getSfxmLb)
                .distinct()
                .collect(Collectors.toList());
    }

    public void onProjectNameFilterChange(AjaxBehaviorEvent event) {
        filterDeviceDeptCodeSet = new HashSet<>();
        if (!Strings.isNullOrEmpty(filterProjectName) && sfxmList != null) {
            for (HisEquipmentSfxm sfxm : sfxmList) {
                if (filterProjectName.equals(sfxm.getSfxmName())) {
                    filterDeviceDeptCodeSet.add(sfxm.getSfxmId());
                }
            }
        }
    }

    public void onSfxmLbFilterChange(AjaxBehaviorEvent event) {
        filterDeviceDeptCodeInSet = new HashSet<>();
        if (!Strings.isNullOrEmpty(filterSfxmLb) && sfxmList != null) {
            for (HisEquipmentSfxm sfxm : sfxmList) {
                if (filterSfxmLb.equals(sfxm.getSfxmLb())) {
                    filterDeviceDeptCodeInSet.add(sfxm.getSfxmId());
                }
            }
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        if (HisOrgComponentController.ORG_CHANGE.equals(eventName)) {
            selectedOrg = (OrgInfo) eventObject;
            loadExecDeptList();
            loadSfxmList();
        }
    }
}