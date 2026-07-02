package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.SpecialAssetInfoService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.pm.RelRuleAssetController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;

import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class SpecialAssetController extends GenericCRUDUUIDController<SpecialAssetInfo> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(SpecialAssetController.class);

    private SpecialAssetInfoRepository dao = null;;
    private AssetInfoRepository assetInfoRepository ;

    private SpecialAssetInfoService   specialAssetInfoService ;

    private UaaService uaaService;
    //搜索字串
    private String oneSearchStr;

//    public List<SearchFilter> searchFilters;
    private UserContextService userContextService;


    private AssetTypeGroupService assetTypeGroupService;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private OrgInfoRepository orgDao;
    private I18nMessageRepository i18nDao;
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
    private String filterAssetDeptOrg;
    private List<OrgInfo> clinicalDeptList;

    private OrgInfoTreeService orgInfoTreeService;
    private Set<String> clinicalDeptOrgUidSet;
    private Set<String> assetDeptOrgUidSet;


    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap;

    private List<SpecialAssetInfo> currentPageAssets;
    @Override
    protected void init() {
        dao = WebUtil.getBean(SpecialAssetInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        specialAssetInfoService = WebUtil.getBean(SpecialAssetInfoService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        this.selectedMap = new HashMap();
        assetTypeGroupList = user.getAssetTypeGroupList();

        loadClinicalDeptList();
    }

    @Override
    protected Page<SpecialAssetInfo> loadData(PageRequest pageRequest) {
        long l = System.currentTimeMillis();
        logger.info("SpecialAssetController.loadData start time:{}",l);


        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));

        if (!UserContextService.isMVSRole()) {
            if (!this.hasFilterOnField("siteUID")) {
                List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }


        if (assetTypeGroupEncodedSet != null && !assetTypeGroupEncodedSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, new ArrayList<>(assetTypeGroupEncodedSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetInfo.assetType".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (user.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, user.getAssetTypeGroupMap().keySet(), "1*"));
        }

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetInfo.clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (assetDeptOrgUidSet != null && !assetDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, new ArrayList<>(assetDeptOrgUidSet), "1*2+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetInfo.assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*2+")).collect(Collectors.toList()));
        }

        Page<SpecialAssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets = page.getContent();
        addMisingClinicalDeptOrg(currentPageAssets);

        Set<String> encodes = currentPageAssets.stream().map(SpecialAssetInfo::getAssetInfo).map(AssetInfo::getAssetType).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<AssetTypeGroup> currentPageAssetTypeGroupList = assetTypeGroupRepository.findByEncodedInAndTenantUid(encodes, user.getTenantUID());
        currentPageAssetTypeGroupMap = currentPageAssetTypeGroupList.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, ast -> ast));
        return page;
    }



    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
        if (StringUtils.isEmpty(oneSearchStr)) {
//            this.removeFilterOnField("name");
//            this.removeFilterOnField("namePyCode");
//            this.removeFilterOnField("departNum");
//            this.removeFilterOnField("financingNum");
//            this.removeFilterOnField("qrCode");
//            this.removeFilterOnField("serialNum");
//            this.removeFilterOnField("manufacture");
//            this.removeFilterOnField("clinicalDeptName");
//            this.removeFilterOnField("assetTags.tag");
        }
    }

    @Override
    protected SpecialAssetInfoRepository getDAO() {
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
        filterAssetDeptOrg = null;
        clinicalDeptOrgUidSet = null;
        assetDeptOrgUidSet = null;
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
        for (SpecialAssetInfo item : currentPageAssets) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void setSelectedAssetsUids() {
        List<String> selectedAssetUids = new ArrayList<>();
        selectedMap.keySet().stream().forEach(assetUid -> { if(selectedMap.get(assetUid)) selectedAssetUids.add(assetUid); });
        RelRuleAssetController.assetInfoUids = selectedAssetUids;
    }



    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }


    public void getAssetInfoBatchEdit() {
//        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
//        List<AssetInfo> resultList;
//        if (assetUids.isEmpty()) {
//            if(CollectionUtils.isEmpty(searchFilters)){
//                this.setUserSearchFilters();
//            }
//            resultList = dao.findBySearchFilter(searchFilters, lastPageSort);
//        } else {
//            resultList = dao.findByUidIn(assetUids, lastPageSort);
//        }
//        String verificationCode = "";
//        Random random = new Random();
//        for (int i = 0; i < 6; i++) {
//            int r = random.nextInt(10); //每次随机出一个数字（0-9）
//            verificationCode = verificationCode + r;  //把每次随机出的数字拼在一起
//        }
//        resultAssetMap = new HashMap<>();
//
//        resultAssetMap.put(verificationCode,resultList);
//        WebUtil.navigateTo("AssetInfoBatchEdit.xhtml?faces-redirect=true&uidList= "+ verificationCode);
    }

    public void deleteAsset(SpecialAssetInfo specialAssetInfo) {
        specialAssetInfoService.deleteSpecialAssetInfo(specialAssetInfo);
    }

    public String getEditPage(String uid) {
        SpecialAssetInfo specialAssetInfo = dao.findById(uid);


//        AssetInfo assetInfo = assetInfoRepository.findById(specialAssetInfo.getAssetId());
        String operation = "AssetCreate.xhtml?actionName=Edit&selectedid=" + specialAssetInfo.getAssetInfo().getUid()
                + "&asset_name=" + specialAssetInfo.getAssetInfo().getName()
                +"&type=SpecialAsset"
                + "&terminate=" + this.terminate;
        return UrlEncryptController.encodeUrlParam(operation);
    }


    private boolean terminate = false;

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }
    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getAssetInfo().getUid()
                + "&asset_name=" + selected.getAssetInfo().getName()
                + "&terminate=" + this.terminate;
        url = "AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
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

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (clinicalDeptList == null || clinicalDeptList.isEmpty()) {
            assetDeptOrgUidSet = Collections.emptySet();
            return;
        }

        if (!Strings.isNullOrEmpty(filterAssetDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterAssetDeptOrg.equals(item.getName()))) {
            filterAssetDeptOrg = null;
        }

        if (filterAssetDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterAssetDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            assetDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            assetDeptOrgUidSet = Collections.emptySet();
        }

    }



    private void addMisingClinicalDeptOrg(List<SpecialAssetInfo> currentPageAssets) {
        Set<String> orgUids = currentPageAssets.stream().filter(item -> item.getAssetInfo().getClinicalDeptUID() != null).map(SpecialAssetInfo::getAssetInfo).map(AssetInfo::getClinicalDeptUID).collect(Collectors.toSet());


        if (orgUids.isEmpty()) {
            return;
        }

        List<OrgInfo> orgInfoList = orgDao.findByUids(orgUids);

        Map<String, OrgInfo> orgInfoMap = orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> item));

        currentPageAssets.stream().forEach(item -> {
            if (item.getAssetInfo().getClinicalDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getAssetInfo().getClinicalDeptUID()));
                if (found != null) {
                    item.setClinicalDeptOrg(found);
                }
            }
        });

    }







    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public List<SearchFilter> getSearchFilters() {
        return searchFilters;
    }

    public void setSearchFilters(List<SearchFilter> searchFilters) {
        this.searchFilters = searchFilters;
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

    public String getFilterAssetDeptOrg() {
        return filterAssetDeptOrg;
    }

    public void setFilterAssetDeptOrg(String filterAssetDeptOrg) {
        this.filterAssetDeptOrg = filterAssetDeptOrg;
    }

    public Set<String> getAssetDeptOrgUidSet() {
        return assetDeptOrgUidSet;
    }

    public void setAssetDeptOrgUidSet(Set<String> assetDeptOrgUidSet) {
        this.assetDeptOrgUidSet = assetDeptOrgUidSet;
    }

    public OrgInfo getOrgInfoByUid(String uid) {
        return orgDao.getByUid(uid);
    }
}


