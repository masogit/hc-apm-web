package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.analysis.FieldFillRateDTO;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class AssetImportantController extends JpaCRUDController<AssetInfo> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(AssetImportantController.class);
    private AssetInfoRepository dao = null;
    private UaaService uaaService;
    private UserContextService userContextService;
    private UserAccountRepository userAccountRepository;
    private CustomConfigService customConfigService;
    private I18nMessageRepository I18dao;
    private OrgInfoRepository orgDao;
    private ConfigCacheService configCacheService;
    @Getter
    private CustomConfigObject customConfig;
    //选定的院区
    private String selectSite;
    @Getter @Setter
    private List<OrgInfo> siteList;
    private UserAccount user;
    private String filterAssetStatus = null;
    private Integer assetClass;
    private Integer assetGroup;
    private String filterClinicalDeptOrg;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private Map<Integer, Boolean> selectedMap; //复选的设备
    private List<AssetInfo> currentPageAssets;
    @Getter @Setter
    private List<UserAccount> assetOwnerList;
    @Getter @Setter
    private Integer selectAssetOwner;
    @Getter @Setter
    private Integer batchModifyCount;
    @Getter @Setter
    private String importantIsNull;
    @Getter @Setter
    private String ownerSearch;
    @Getter @Setter
    private Map<String, FieldFillRateDTO>  fieldFillRate = new LinkedHashMap<>();

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        customConfigService = WebUtil.getBean(CustomConfigService.class);
        I18dao = WebUtil.getBean(I18nMessageRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        this.selectedMap = new HashMap();
        loadSList();
        loadAssetOwnerList();
        loadFieldFillRate();
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        long l = System.currentTimeMillis();
        logger.info("AssetImportantController.loadData start time:{}",l);
        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        if (StringUtils.hasText(importantIsNull)) {
            if (importantIsNull.equals("assetDeptName")) {
                importantIsNull = "assetDeptId";
            }
            if (importantIsNull.equals("clinicalDeptName")) {
                importantIsNull = "clinicalDeptUID";
            }
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*6+")).collect(Collectors.toList()));
            this.updateSearchFilter(new SearchFilter(importantIsNull, SearchFilter.Operator.IsNull, null, "1*6+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*6+")).collect(Collectors.toList()));
        }
        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite, "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite, "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite, "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite, "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        if (StringUtils.hasText(filterClinicalDeptOrg)) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filterClinicalDeptOrg, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (selectAssetOwner != null && selectAssetOwner != 0) {
            this.updateSearchFilter(new SearchFilter("assetOwnerId", SearchFilter.Operator.EQ, selectAssetOwner, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetOwnerId".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (pageRequest.getSort() == null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), new Sort(Sort.Direction.DESC, "lastModifiedDate"));
        }
        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = page.getContent();
        return page;
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    public String findDeptName(String deptUid) {
        if (StringUtils.hasText(deptUid)) {
            return Optional.ofNullable(configCacheService.findOrgByUid(deptUid)).orElse(new OrgInfo()).getName();
        }
        return null;
    }

    public void siteChangeEvent() {
        this.searchFilters.clear();
        loadClinicalDeptList();
        loadFieldFillRate();
        filterClinicalDeptOrg = null;
        filterAssetStatus = null;
    }

    public String findI18nByKey(String key) {
        return WebUtil.getMessage(key);
    }

    public String getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(String selectSite) {
        this.selectSite = selectSite;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void onSelectOfPage() {

    }
    public void changeDeptListener() {
        loadFieldFillRate();
    }

    public void changeListener() {
    }

    public void navigateTo(){
        String verificationCode = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10); //每次随机出一个数字（0-9）
            verificationCode = verificationCode + r;  //把每次随机出的数字拼在一起
        }
        Map<String, List<AssetInfo>> resultAssetMap = new HashMap<>();
        List<AssetInfo> assetInfos = dao.findByIdIn(selectedMap.entrySet().stream().filter(v -> v.getValue()).map(Map.Entry::getKey).collect(Collectors.toSet()));
        AssetInfoListController.resultAssetMap = resultAssetMap;

        resultAssetMap.put(verificationCode, assetInfos);

        WebUtil.navigateTo("AssetInfoBatchEdit.xhtml?faces-redirect=true&uidList= "+ verificationCode);
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public void batchEditUsageDateBefore() {
        batchModifyCount = selectedMap.values().stream().filter(Boolean::booleanValue).collect(Collectors.toList()).size();
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    private void loadClinicalDeptList() {
        if (StringUtils.hasText(selectSite)) {
            clinicalDeptList = uaaService.getClinicalDeptListBySiteUID(selectSite);
        } else {
            clinicalDeptList = new ArrayList<>();
        }
    }

    private void loadAssetOwnerList() {
        if (CollectionUtils.isEmpty(assetOwnerList)) {
            assetOwnerList = userAccountRepository.findByTenantUIDAndStaffRole(user.getTenantUID());
        }
    }

    private void loadSList() {
        siteList = uaaService.getUserAccessibleSiteList(user.getUid()).stream().filter(item -> item.getOrgType() == 3).collect(Collectors.toList());
    }

    private void loadFieldFillRate() {
        for (String field : customConfig.getAssetsImportantFields()) {
            FieldFillRateDTO dto = dao.calculateFieldFillRate(
                    field,
                    user.getTenantUID(),
                    user.getOrgLevel(),
                    user.getOrgInfoUID(),
                    selectSite,
                    filterClinicalDeptOrg,
                    true   // ignoreEmptyString: "" 视为未填
            );
            fieldFillRate.put(field, dto);
        }
    }

    public Map<Integer, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public Integer getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(Integer assetClass) {
        this.assetClass = assetClass;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public String getFilterAssetStatus() {
        return filterAssetStatus;
    }

    public void setFilterAssetStatus(String filterAssetStatus) {
        this.filterAssetStatus = filterAssetStatus;
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }
    public List<Map.Entry<String, FieldFillRateDTO>> getFieldFillRateList() {
        List<Map.Entry<String, FieldFillRateDTO>> result = new ArrayList<>();
        // 逐个按顺序取字段
        for (String key : CustomConfigConstant.getAssetsImportantFieldsAll()) {
            FieldFillRateDTO dto = fieldFillRate.get(key);
            if (dto != null) {
                // 按 Map.Entry 形式加入
                result.add(new AbstractMap.SimpleEntry<>(key, dto));
            }
        }
        return result;
    }

    public List<Integer> getSingleRow() {
        return java.util.Collections.singletonList(1);
    }
}


