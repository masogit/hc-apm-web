package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SupplierEmpowermentController extends JpaCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(SupplierEmpowermentController.class);

    private static final long serialVersionUID = -1L;
    private AssetInfoRepository dao;
    private AssetInfoMgrService aimService;
    private AutoCompleteService autoCompleteService;
    private SupplierUserRepository supplierUserRepository;
    private AssetEmpowermentRepostory assetEmpowermentRepostory;
    private SupplierRepository supplierRepository;
    private UserAccountRepository userAccountRepository;
    private OrgInfoRepository orgInfoRepository;
    private OrgInfoTreeService orgInfoTreeService;
    private UserContextService userContextService;
    private UaaService uaaService;
    private I18nMessageRepository i18nDao;

    @Getter @Setter
    private String selectedSupplierUid; //由于Supplier实体不能直接接收
    @Getter @Setter
    private String selectedUserUid;
    @Getter @Setter
    private List<AssetInfo> allAssets;
    @Getter @Setter
    private Map<String, Boolean> selectedMap;
    @Getter @Setter
    private Integer empowermentSearch;
    @Getter @Setter
    private OrgInfo selectSite;
    @Getter @Setter
    private Supplier selectSupplier;
    @Getter @Setter
    private List<Supplier> supplierList;
    @Getter @Setter
    private Map<String, Supplier> supplierMap;

    private Boolean showSupplierUser = Boolean.TRUE;
    //选定科室过滤
    @Getter @Setter
    private String filterClinicalDeptOrg;
    @Getter @Setter
    private String filterAssetDeptOrg;

    @Getter @Setter
    private String manufactDateFormatTime = null;
    @Getter @Setter
    private String installDateFormatTime = null;
    @Getter @Setter
    private String arriveDateFormatTime = null;

    @Getter @Setter
    private Integer assetGroup;
    @Getter @Setter
    private Integer assetClass;
    @Getter @Setter
    private List<I18nMessage> assetGroupSecondayList;

    private Set<String> clinicalDeptOrgUidSet;
    private Set<String> assetDeptOrgUidSet;

    private List<OrgInfo> clinicalDeptList;

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        supplierUserRepository = WebUtil.getBean(SupplierUserRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        supplierUserRepository = WebUtil.getBean(SupplierUserRepository.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        assetEmpowermentRepostory = WebUtil.getBean(AssetEmpowermentRepostory.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        selectedMap = new HashMap<>();
    }

    public List<Supplier> autoCompleteSupplier(String query) {
        supplierList = autoCompleteService.autoCompleteVendor(query, aimService.getOnlySuplierListBySiteUid(UserContextService.getCurrentUserAccount().getTenantUID()));
        supplierMap = supplierList.stream().collect(Collectors.toMap(Supplier::getUid, s -> s));
        return supplierList;
    }

    public List<UserAccount> supplierUsers(String query) {
        if(selectedSupplierUid !=null) {
            return userAccountRepository.findByUidIn(supplierUserRepository.findSupplierUsersBySupplierUID(selectedSupplierUid));
        }
        return null;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !allAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : allAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public void doEmpowerment() {
        if (selectedSupplierUid == null) {
            WebUtil.addErrorMessage("请选择供应商！"); return;
        }
        Set selectassetUid = selectedMap.keySet();
        if(selectassetUid.isEmpty()) {
            WebUtil.addErrorMessage("请勾选设备！"); return;
        }
        Supplier supplier = supplierMap.get(selectedSupplierUid);
        if(selectedUserUid == null && supplier.getVenderCompanyId() == null) {
            WebUtil.addErrorMessage("请选择负责人！"); return;
        }
        allAssets.stream().forEach(assetInfo -> {
            if(selectassetUid.contains(assetInfo.getUid())) {
                if(assetInfo.getEmpowermentStatus()==null || assetInfo.getEmpowermentStatus()==0) { //未赋权
                    AssetEmpowerment assetEmpowerment = new AssetEmpowerment();
                    assetEmpowerment.setAssetUid(assetInfo.getUid());
                    assetEmpowerment.setSupplierUid(selectedSupplierUid);
                    assetEmpowerment.setUserUid(selectedUserUid);
                    assetEmpowermentRepostory.save(assetEmpowerment);
                    assetInfo.setEmpowermentStatus(1);
                    dao.save(assetInfo);
                }
            }
        });
        selectedMap.clear();
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) { searchFilters = new ArrayList<SearchFilter>();}
        if(selectedSupplierUid!=null) {
/*            List assetUids =  supplierFEAssetJoinRepository.findAssetUidsByUserUid(selectedUserUid);
            this.updateSearchFilter(new SearchFilter("uid", SearchFilter.Operator.IN, assetUids));*/
            this.removeFilterOnField("uid"); //把首次进入页面的空列表查询条件移除
            this.updateSearchFilter(new SearchFilter("supplierId", SearchFilter.Operator.EQ,supplierRepository.getByUid(selectedSupplierUid).getId()));
            if(empowermentSearch!=null && empowermentSearch==1) {
                this.updateSearchFilter(new SearchFilter("empowermentStatus", SearchFilter.Operator.EQ, empowermentSearch));
            } else if(empowermentSearch!=null && empowermentSearch==0) {
                this.updateSearchFilter(new SearchFilter("empowermentStatus", SearchFilter.Operator.IsNull, null));
            }
            if(selectSite!=null) {
                this.updateSearchFilter(new SearchFilter("siteUID",SearchFilter.Operator.EQ,selectSite.getUid()));
            }
            if (assetDeptOrgUidSet != null && !assetDeptOrgUidSet.isEmpty()) {
                this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, new ArrayList<>(assetDeptOrgUidSet)));
            } else {
                searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName)).collect(Collectors.toList()));
            }
            if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet)));
            } else {
                searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName)).collect(Collectors.toList()));
            }
        } else {
            this.updateSearchFilter(new SearchFilter("uid", SearchFilter.Operator.EQ, null));
        }
        Page result = dao.findBySearchFilter(this.searchFilters,pageRequest);
        allAssets = result.getContent();
        return result;
    }


    public void assetGroupChangeListener() {
        if (assetGroup == null) {
            assetGroupSecondayList = Collections.emptyList();
        } else {
            assetGroupSecondayList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }
    }

    public void assetClassChangeListener() {

        if (assetClass == null) {
            assetGroupSecondayList = Collections.emptyList();
        } else {
            assetGroupSecondayList = i18nDao.getByMsgTypeAndSiteId("assetClassSecondary_" + String.valueOf(assetGroup.intValue()), userContextService.getLoginUser().getSiteId());
        }

    }

    public String getEmpowermentStatus(AssetInfo assetInfo) {
        if (assetInfo!=null && assetInfo.getEmpowermentStatus()!=null){
            return assetInfo.getEmpowermentStatus() == 1 ? "已授权" : "未授权";
        } else {
            return "未授权";
        }
    }

    public List<I18nMessage> getListForEmpowerment() {
        List<I18nMessage> result = new ArrayList<>();
        result.add(new I18nMessage(null,"0","未授权"));
        result.add(new I18nMessage(null,"1","已授权"));
        return result;
    }
    public String getSiteOrgName(String siteUID) {
        OrgInfo org = orgInfoRepository.getByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
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

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetDeptOrg = (String) ((SelectEvent) event).getObject();
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

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
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
        assetDeptOrgUidSet = null;
        clinicalDeptOrgUidSet = null;
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    public void onSupplierChange() {
        selectSupplier = supplierMap.get(selectedSupplierUid);
        showSupplierUser = selectSupplier.getVenderCompanyId() == null;
    }
    public void onSupplierUserChange() { }

    public Boolean getShowSupplierUser() {
        return showSupplierUser;
    }

    public void setShowSupplierUser(Boolean showSupplierUser) {
        this.showSupplierUser = showSupplierUser;
    }
}
