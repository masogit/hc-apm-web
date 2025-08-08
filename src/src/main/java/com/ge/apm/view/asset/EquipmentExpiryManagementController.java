package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class EquipmentExpiryManagementController extends JpaCRUDController<AssetInfo> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentExpiryManagementController.class);
    private AssetInfoRepository dao = null;
    private UaaService uaaService;
    private String oneSearchStr;//搜索字串
    private UserContextService userContextService;
    private AssetTypeGroupService assetTypeGroupService;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private OrgInfoRepository orgDao;
    private I18nMessageRepository i18nDao;
    private EquipmentExpiryManagementRepository equipmentExpiryManagementRepository;
    //选定的院区
    private OrgInfo selectSite;
    private UserAccount user;
    private String filterAssetStatus = null;
    private Integer assetClass;
    private List<I18nMessage> assetGroupSecondayList;
    private Integer assetGroup;
    private List<AssetTypeGroup> assetTypeGroupList;
    private String filterAssetTypeGroup;
    private Set<String> assetTypeGroupEncodedSet;
    private Map<String, AssetTypeGroup> currentPageAssetTypeGroupMap;
    private String filterClinicalDeptOrg;
    private List<OrgInfo> clinicalDeptList;
    private OrgInfoTreeService orgInfoTreeService;
    private Set<String> clinicalDeptOrgUidSet;
    @Getter @Setter
    private Map<Integer, Boolean> selectedMap; //复选的设备
    private List<AssetInfo> currentPageAssets;
    @Getter @Setter
    private Integer lifecycle;
    @Getter @Setter
    private Integer batchModifyCount;
    @Getter @Setter
    private List<EquipmentExpiryManagement> equipmentExpiryManagements;
    @Getter @Setter
    private Boolean matchByFunctionType;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        equipmentExpiryManagementRepository = WebUtil.getBean(EquipmentExpiryManagementRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        this.selectedMap = new HashMap();
        assetTypeGroupList = user.getAssetTypeGroupList();

        loadClinicalDeptList();
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        long l = System.currentTimeMillis();
        logger.info("EquipmentExpiryManagementController.loadData start time:{}",l);
        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
//            this.updateSearchFilter(new SearchFilter("assetTags.tag", SearchFilter.Operator.LIKE, oneSearchStr, "1*8+"));
        }
        if (user.isEnableAssetTypeGroup()) {
            if (assetTypeGroupEncodedSet != null && !assetTypeGroupEncodedSet.isEmpty()) {
                this.updateSearchFilter(new SearchFilter("assetType", SearchFilter.Operator.IN, assetTypeGroupEncodedSet, "1*"));
            } else {
                searchFilters.removeAll(searchFilters.stream().filter(item -> "assetType".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
                this.updateSearchFilter(new SearchFilter("assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
            }
        }
        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptOrgUidSet, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (pageRequest.getSort() == null) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), new Sort(Sort.Direction.DESC, "lastModifiedDate"));
        }
        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = page.getContent();
        addMisingClinicalDeptOrg(currentPageAssets);
        Set<String> encodes = currentPageAssets.stream().map(AssetInfo::getAssetType).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<AssetTypeGroup> currentPageAssetTypeGroupList = assetTypeGroupRepository.findByEncodedInAndTenantUid(encodes, user.getTenantUID());
        currentPageAssetTypeGroupMap = currentPageAssetTypeGroupList.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, ast -> ast));
        return page;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (StringUtils.isEmpty(oneSearchStr)) {
            this.removeFilterOnField("name");
            this.removeFilterOnField("namePyCode");
            this.removeFilterOnField("departNum");
            this.removeFilterOnField("financingNum");
            this.removeFilterOnField("qrCode");
            this.removeFilterOnField("serialNum");
            this.removeFilterOnField("manufacture");
            this.removeFilterOnField("clinicalDeptName");
            this.removeFilterOnField("assetTags.tag");
        }
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();

        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }
        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }
        loadClinicalDeptList();
        filterClinicalDeptOrg = null;
        clinicalDeptOrgUidSet = null;
        assetGroup = null;
        filterAssetStatus = null;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public void assetClassChangeListener() {
        if (assetClass == null) {
            assetGroupSecondayList = Collections.emptyList();
        } else {
            assetGroupSecondayList = i18nDao.getByMsgTypeAndSiteId("assetClassSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
        // 校验已选中的注册号是否一致否则不支持批量操作
        checkRegisterNo();
    }

    public void onSelectOfPage() {
        checkRegisterNo();
    }

    public void checkRegisterNo() {
        // 校验已选中的注册号是否一致否则不支持批量操作
        Set<Integer> ids = selectedMap.entrySet().stream().filter(v -> v.getValue()).map(Map.Entry::getKey).collect(Collectors.toSet());
        if (ids.size() > 1) {
            List<AssetInfo> assetInfos = dao.findByIdIn(ids);
            Set<String> registerNo = assetInfos.stream().map(AssetInfo::getRegistrationNo).filter(Objects::nonNull).collect(Collectors.toSet());
            if (assetInfos.stream().anyMatch(v -> StringUtils.isEmpty(v.getRegistrationNo())) || CollectionUtils.isEmpty(registerNo) || registerNo.size() != 1) {
                selectedMap.clear();
                WebUtil.addErrorMessage(WebUtil.getMessage("batchModifyRule"));
            }
        }
    }

    public void onAssetTypeGroupNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetTypeGroup = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterAssetTypeGroup) && !assetTypeGroupList.stream().anyMatch(item -> filterAssetTypeGroup.equals(item.getName()))) {
            filterAssetTypeGroup = null;
        }

        if (filterAssetTypeGroup != null) {
            List<AssetTypeGroup> assetTypeGroupsMatchingByName = this.assetTypeGroupList.stream().filter(item -> item.getName().equals(filterAssetTypeGroup)).collect(Collectors.toList());
            Set<AssetTypeGroup> matchedList = assetTypeGroupService.collectChildAssetTypeGroupInExstingList(assetTypeGroupsMatchingByName, assetTypeGroupList);
            assetTypeGroupEncodedSet = matchedList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toSet());
        } else {
            assetTypeGroupEncodedSet = Collections.emptySet();
        }
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public void batchEditUsageDateBefore() {
        batchModifyCount = selectedMap.values().stream().filter(Boolean::booleanValue).collect(Collectors.toList()).size();
    }

    public void prepareEditUsageDate(AssetInfo assetInfo) {
        matchByFunctionType = false;
        equipmentExpiryManagements = new ArrayList<>();
        AtomicBoolean batch = new AtomicBoolean(false);
        if (assetInfo != null) {
            selected = assetInfo;
            this.lifecycle = selected.getLifecycle()!=null?selected.getLifecycle() / 12:null;
        } else {
            selectedMap.entrySet().stream().forEach(k -> {
                if (k.getValue()) {
                    selected = dao.getById(k.getKey());
                    batch.set(true);
                    return;
                }
            });
            // 检查如果多选框只有一台批量操作切换为单台处理
            if (selectedMap.values().stream().filter(Boolean::booleanValue).collect(Collectors.toList()).size() == 1) {
                this.lifecycle = selected.getLifecycle()!=null?selected.getLifecycle() / 12:null;
                batch.set(false);
            } else {
                this.lifecycle = null;
            }
        }
        // 匹配效期信息
        if (StringUtils.isNotEmpty(selected.getRegistrationNo())) {
            equipmentExpiryManagements = equipmentExpiryManagementRepository.fetchByRegister(selected.getRegistrationNo());
        }
        if (batch.get()) {
            selected = null;
            return;
        }
        if (CollectionUtils.isEmpty(equipmentExpiryManagements)) {
            if ((StringUtils.isNotEmpty(selected.getManufacture()) || StringUtils.isNotEmpty(selected.getMakerShortName())) && StringUtils.isNotEmpty(selected.getFunctionType())) {
                // 继续通过制造商和设备型号获取
                equipmentExpiryManagements = equipmentExpiryManagementRepository.matchByManufactureAndFunctionType(StringUtils.isEmpty(selected.getMakerShortName())?selected.getManufacture():selected.getMakerShortName().replace(" ", "").toLowerCase(), selected.getFunctionType().replace(" ", "").toLowerCase());;
                matchByFunctionType = true;
            }
        }
    }

    public void applyChange() {
        try {
            Assert.isTrue((lifecycle != null && lifecycle > 0), WebUtil.getMessage("saveUsageYearError"));
            // 年份转月份
            Integer usageMonth = lifecycle * 12;
            if (selected != null) {
                selected.setLifecycle(usageMonth);
                selected.setLastModifiedDate(new Date());
                dao.save(selected);
            } else {
                Set<Integer> assetIds = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toSet());
                if (!assetIds.isEmpty()) {
                    dao.updateLifecycleByIds(usageMonth, assetIds);
                }
            }
            WebUtil.addSuccessMessage(WebUtil.getMessage("SaveSuccess"));
        } catch (Exception e) {
            WebUtil.addErrorMessage(e.getMessage());
        }
        selectedMap.clear();
        selected = null;
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public List<String> autoCompleteAssetTypeGroupName(String query) {
        if (assetTypeGroupList == null) {
            return Collections.emptyList();
        }
        List<AssetTypeGroup> res = this.assetTypeGroupList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query))
                .collect(Collectors.toList());
        return res.stream().map(AssetTypeGroup::getName).distinct().collect(Collectors.toList());
    }


    public String convertAssetTypeGroupEncodedToName(String encoded) {
        if (null == encoded) {
            return null;
        }

        AssetTypeGroup assetTypeGroup = currentPageAssetTypeGroupMap.get(encoded);
        if (null == assetTypeGroup) {
            return null;
        }

        return assetTypeGroup.getDisplayName();
    }
    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }


    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }


    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }
        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());
            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }

    }

    private void addMisingClinicalDeptOrg(List<AssetInfo> currentPageAssets) {
        Set<String> orgUids = currentPageAssets.stream().filter(item -> item.getClinicalDeptUID() != null).map(AssetInfo::getClinicalDeptUID).collect(Collectors.toSet());
        if (orgUids.isEmpty()) {
            return;
        }
        List<OrgInfo> orgInfoList = orgDao.findByUids(orgUids);
        Map<String, OrgInfo> orgInfoMap = orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> item));
        currentPageAssets.stream().forEach(item -> {
            if (item.getClinicalDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getClinicalDeptUID()));
                if (found != null) {
                    item.setClinicalDeptOrg(found);
                }
            }
        });
    }

    public Map<Integer, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public Integer getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(Integer assetClass) {
        this.assetClass = assetClass;
    }

    public List<I18nMessage> getAssetGroupSecondayList() {
        return assetGroupSecondayList;
    }

    public void setAssetGroupSecondayList(List<I18nMessage> assetGroupSecondayList) {
        this.assetGroupSecondayList = assetGroupSecondayList;
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

    public String getFilterAssetTypeGroup() {
        return filterAssetTypeGroup;
    }

    public void setFilterAssetTypeGroup(String filterAssetTypeGroup) {
        this.filterAssetTypeGroup = filterAssetTypeGroup;
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }
}


