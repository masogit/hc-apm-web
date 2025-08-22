package com.ge.apm.view.nurseSite;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.BuildManagementRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.nurseSite.AssetSelectService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nick.liu
 * @create 2019/3/21 16:56
 */
@Data
@ManagedBean
@ViewScoped
public class AssetSelectController extends GenericCRUDUUIDController<AssetInfo> implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(AssetSelectController.class);

    private OrgInfo selectedOrg;

    private String assetName;

    private String assetGroup;

    private String functionType;

    private String departNum;

    private String emergencyEquipment;

    private Boolean isAllSelected = Boolean.FALSE;
    
    private Boolean isAllSelected5 = Boolean.FALSE;

    private Boolean is4Edit = Boolean.FALSE;

    private List<OrgInfo> deptList;

    private TreeNode orgTree;


    private UserAccount currentUser;

    private List<SearchFilter> searchFilters;

    private List<AssetInfo> assets4Maintain;
    
    private List<AssetInfo> assets5Maintain;

    private Set<AssetInfo> selectedAssets;

    private Set<AssetInfo> selectedAssetsed;

    private Set<AssetInfo> selectedAssets5;

    private Map<String, Boolean> selectedAssetUids;
    
    private Map<String, Boolean> selectedAssetUids5;

    private ServerEventInterface callingController;

    private AssetSelectService assetSelectService;
    
    private List<OrgInfo> listCheckOrgInfo;

    private String locationName;
    private String locationCode;
    private BuildManagementRepository buildManagementRepository;

    private List<BuildManagement>  managementList=new ArrayList<>();

    private List<AssetInfo> assets4Permit;

    private List<AssetInfo> assets5Permit;



    private Map<String,List<BuildManagement>>  managementMap=new HashMap<>();

    @Override
    protected GenericRepositoryUUID<AssetInfo> getDAO() {
        return null;
    }

    private Boolean isAllSelectedAssetGroup = Boolean.FALSE;

    private List<I18nMessage> i18nMessageList;

    private List<I18nMessage> listCheckI18nMessage;
    @Override
    protected void init() {
        assetSelectService = WebUtil.getBean(AssetSelectService.class);
        searchFilters = new ArrayList<>(8);
        selectedAssetUids = new HashMap<>(32);
        selectedAssets = new HashSet<>();
        selectedAssetsed = new HashSet<>();
        selectedAssetUids5 = new HashMap<>(32);
        selectedAssets5 = new HashSet<>();
        currentUser = UserContextService.getCurrentUserAccount();
        buildManagementRepository = WebUtil.getBean(BuildManagementRepository.class);
        this.isSelectAll = Boolean.FALSE;
        i18nMessageList = FieldValueMessageController.getFieldValueList("assetGroup");
    }

    public void loadData4Create() {
        this.orgTree = assetSelectService.loadOrgTree();
        this.selectedOrg = assetSelectService.loadSelectedOrg();
        this.deptList = assetSelectService.loadValidDeptList(selectedOrg);

        // ugly way to reset below arguments, it seems this controller won't be release without submit
        // need to investigate this issue later
        //TODO
        this.selectedAssetUids.clear();
        this.selectedAssets.clear();
        this.selectedAssetsed.clear();
        this.selectedAssetUids5.clear();
        this.selectedAssets5.clear();
        this.assets4Maintain = null;
        this.assets5Maintain=new ArrayList<>();
        this.assetGroup = null;
        this.assetName = null;
        this.locationName = null;
        this.emergencyEquipment = null;
        this.listCheckOrgInfo=new ArrayList<OrgInfo>();
        this.is4Edit = Boolean.FALSE;
        this.isSelectAll = Boolean.FALSE;

        this.assets4Permit = null;
        this.assets5Permit=new ArrayList<>();
        this.listCheckI18nMessage = new ArrayList<>();
        this.isAllSelectedAssetGroup = false;
    }

    public void loadData4Edit(NurseSiteMaintainConfig config) {
        this.is4Edit = Boolean.TRUE;
        this.orgTree = assetSelectService.loadOrgTree();
        this.selectedOrg = assetSelectService.loadSelectedOrg();
        // 页面 资产查询列表的复选框用
        this.selectedAssetUids.clear();
        // 编辑时，之前编辑的时候已经勾选了的资产
        this.selectedAssets.clear();
        // 本次编辑时勾选的资产
        this.selectedAssetsed.clear();

        Wrapper<AssetInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("nsmci.parent_id", config.getId());
        if (currentUser.isEnableAssetTypeGroup()) {
            wrapper.in("ai.asset_type", currentUser.getAssetTypeGroupMap().keySet());
        }
        this.selectedAssets = new HashSet<>(assetSelectService.loadSelectedAssetByConfig(wrapper));
        this.selectedAssets.forEach(t -> selectedAssetUids.put(t.getUid(), Boolean.TRUE));
        OrgInfo selectedDept = assetSelectService.getOrgInfoByUid(config.getClinicalDeptUID());
        this.selectedOrg = assetSelectService.getOrgInfoByUid(selectedDept.getSiteUID());
		if (this.listCheckOrgInfo == null) {
			this.listCheckOrgInfo = new ArrayList<OrgInfo>();
		}
		else{
			this.listCheckOrgInfo.clear();
		}
		listCheckOrgInfo.add(selectedDept);
        this.deptList = assetSelectService.loadValidDeptList(selectedOrg);

        //sort assets by selected for edit view
        List<AssetInfo> assetsWithoutSelected = this.loadAssets4EditView().stream()
                .filter(t -> !this.selectedAssetUids.getOrDefault(t.getUid(), Boolean.FALSE))
                .collect(Collectors.toList());
        this.assets4Maintain = new ArrayList<>();
        this.assets4Maintain.addAll(assetsWithoutSelected);
        this.assets5Maintain = new ArrayList<>(this.selectedAssets);
        this.isAllSelectedAssetGroup = Boolean.FALSE;
        this.listCheckI18nMessage = new ArrayList<>();
    }


    public void loadData4Edit(List<AssetInfo> assetInfos,String  siteUid) {
        this.is4Edit = Boolean.TRUE;
        this.orgTree = assetSelectService.loadOrgTree();
        this.selectedOrg = assetSelectService.loadSelectedOrg();
        // 页面 资产查询列表的复选框用
        this.selectedAssetUids.clear();
        // 编辑时，之前编辑的时候已经勾选了的资产
        this.selectedAssets.clear();
        // 本次编辑时勾选的资产
        this.selectedAssetsed.clear();


        this.selectedAssets = new HashSet<>(assetInfos);
        this.selectedAssets.forEach(t -> selectedAssetUids.put(t.getUid(), Boolean.TRUE));
//        OrgInfo selectedDept = assetSelectService.getOrgInfoByUid(config.getClinicalDeptUID());
        this.selectedOrg = assetSelectService.getOrgInfoByUid(siteUid);
        if (this.listCheckOrgInfo == null) {
            this.listCheckOrgInfo = new ArrayList<OrgInfo>();
        }
        else{
            this.listCheckOrgInfo.clear();
        }
//        listCheckOrgInfo.add(selectedDept);
        if(!CollectionUtils.isEmpty(assetInfos)  && !CollectionUtils.isEmpty(assetInfos.stream().filter(ite->StringUtils.isNotBlank(ite.getClinicalDeptUID()) ).collect(Collectors.toList())) ){
            List<OrgInfo> orgInfos= assetSelectService.getOrgInfoByUids(assetInfos.stream().filter(ite->StringUtils.isNotBlank(ite.getClinicalDeptUID())).map(item->item.getClinicalDeptUID()).collect(Collectors.toList()));
            listCheckOrgInfo.addAll(orgInfos);
        }

        this.deptList = assetSelectService.loadValidDeptList(selectedOrg);

        //sort assets by selected for edit view
        searchFilters.add(new SearchFilter("isSpecialAsset", SearchFilter.Operator.EQ, true, "1*"));

        List<AssetInfo> assetsWithoutSelected = this.loadAssets4EditView().stream()
                .filter(t -> !this.selectedAssetUids.getOrDefault(t.getUid(), Boolean.FALSE))
                .collect(Collectors.toList());
        this.assets4Permit = new ArrayList<>();
        this.assets4Permit.addAll(assetsWithoutSelected);
        this.assets5Permit = new ArrayList<>(this.selectedAssets);
    }



    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
        if (selectedOrg != null) {
            log.info("selectedOrg: {}", selectedOrg.getShortName());
            this.deptList = assetSelectService.loadDeptListByOrg(selectedOrg);
        }
        this.callingController.onServerEvent("changeSite",selectedOrg);
        isSelectAll = false;
    }

    public void findAssets2Permit() {
        //clear selected asset for each search
        this.selectedAssetUids.clear();
        this.selectedAssets.clear();
        this.buildBaseSearchFilters();
        searchFilters.add(new SearchFilter("isSpecialAsset", SearchFilter.Operator.EQ, true, "1*"));

        int filterCount=this.buildAdvancedSearchFilters();
        if(filterCount==0){
        	WebUtil.addErrorMessage("缺少筛选条件！");
        } else{
            if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
                assets4Permit = ListUtil.empty();
            } else {
                if (currentUser.isEnableAssetTypeGroup()) {
                    searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet(), "1*"));
                }
                assets4Permit = assetSelectService.loadAssetsBySearchFilter(this.searchFilters);
            }
        }
        this.searchFilters.clear();
    }


    public void findAssets2Maintain() {
        //clear selected asset for each search
        this.selectedAssetUids.clear();
        this.selectedAssets.clear();
        this.buildBaseSearchFilters();
        int filterCount=this.buildAdvancedSearchFilters();
        if(filterCount==0){
            WebUtil.addErrorMessage("缺少筛选条件！");
        } else{
            if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
                assets4Maintain = ListUtil.empty();
            } else {
                if (currentUser.isEnableAssetTypeGroup()) {
                    searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet(), "1*"));
                }
                assets4Maintain = assetSelectService.loadAssetsBySearchFilter(this.searchFilters);
            }
        }
        this.searchFilters.clear();
    }



    private void  assetInfosFilter(){
        if(CollectionUtils.isEmpty(assets4Maintain) || CollectionUtils.isEmpty(listCheckOrgInfo) )return;
        List<String> collect = listCheckOrgInfo.stream().map(OrgInfo::getUid).collect(Collectors.toList());
        assets4Maintain=assets4Maintain.stream().filter(item->{
            String assetDeptUID = item.getAssetDeptUID();
            if(StringUtils.isBlank(assetDeptUID)) return true;
                return collect.contains(assetDeptUID);
        }).collect(Collectors.toList());
    }
    public void addAssetInfo(){
        for(AssetInfo assetInfo: selectedAssetsed){
            if(!selectedAssets.contains(assetInfo)){
                selectedAssets.add(assetInfo);
            }
        }
    	for(AssetInfo assetInfo: selectedAssets){
    		if(!assets5Maintain.contains(assetInfo)){
    			assets5Maintain.add(assetInfo);
    		}
    	}
    	this.tiggerEvent();
    }

    public void addAssetInfoPermit(){
        for(AssetInfo assetInfo: selectedAssetsed){
            if(!selectedAssets.contains(assetInfo)){
                selectedAssets.add(assetInfo);
            }
        }
        for(AssetInfo assetInfo: selectedAssets){
            if(!assets5Permit.contains(assetInfo)){
                assets5Permit.add(assetInfo);
            }
        }
    }
    
    public void removeAssetInfo(){
        selectedAssets.removeAll(selectedAssets5);
        assets5Maintain.removeAll(selectedAssets5);
        selectedAssets5.clear();
        selectedAssetUids5.clear();
        this.tiggerEvent();
    }
    
    public void clearAssetInfo(){
        this.selectedAssets.clear();
        assets5Maintain.clear();
        selectedAssets5.clear();
        selectedAssetUids5.clear();
        this.tiggerEvent();
    }

    public void removePermitAssetInfo(){
        selectedAssets.removeAll(selectedAssets5);
        assets5Permit.removeAll(selectedAssets5);
        selectedAssets5.clear();
        selectedAssetUids5.clear();
//        this.tiggerEvent();
    }

    public void clearPermitAssetInfo(){
        this.selectedAssets.clear();
        assets5Permit.clear();
        selectedAssets5.clear();
        selectedAssetUids5.clear();
    }


    public void addAssetsOfCurrentPage(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected: {}", this.isAllSelected);
        if(isAllSelected) {
            assets4Maintain.stream().forEach(t -> {
                selectedAssetUids.put(t.getUid(), Boolean.TRUE);
                selectedAssetsed.add(t);
            });
        } else {
            assets4Maintain.stream().forEach(t -> {
                selectedAssetUids.put(t.getUid(), Boolean.FALSE);
                selectedAssetsed.remove(t);
            });
        }
        isAllSelected = !isAllSelected;
        log.debug("size of selectedAssetUids: {}", selectedAssetUids.size());
    }


    public void addAssetsPermitOfCurrentPage(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected: {}", this.isAllSelected);
        if(isAllSelected) {
            assets4Permit.stream().forEach(t -> {
                selectedAssetUids.put(t.getUid(), Boolean.TRUE);
                selectedAssetsed.add(t);
            });
        } else {
            assets4Permit.stream().forEach(t -> {
                selectedAssetUids.put(t.getUid(), Boolean.FALSE);
                selectedAssetsed.remove(t);
            });
        }
        isAllSelected = !isAllSelected;
        log.debug("size of selectedAssetUids: {}", selectedAssetUids.size());
    }

    public void addAssetsOfCurrentPage5(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected5: {}", this.isAllSelected5);
        if(isAllSelected5) {
            assets5Maintain.stream().forEach(t -> {
                selectedAssetUids5.put(t.getUid(), Boolean.TRUE);
                selectedAssets5.add(t);
            });
        } else {
            assets5Maintain.stream().forEach(t -> {
                selectedAssetUids5.put(t.getUid(), Boolean.FALSE);
                selectedAssets5.remove(t);
            });
        }
        isAllSelected5 = !isAllSelected5;
        log.debug("size of selectedAssetUids5: {}", selectedAssetUids.size());
    }

    public void addAssetsPermitOfCurrentPage5(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected5: {}", this.isAllSelected5);
        if(isAllSelected5) {
            assets5Permit.stream().forEach(t -> {
                selectedAssetUids5.put(t.getUid(), Boolean.TRUE);
                selectedAssets5.add(t);
            });
        } else {
            assets5Permit.stream().forEach(t -> {
                selectedAssetUids5.put(t.getUid(), Boolean.FALSE);
                selectedAssets5.remove(t);
            });
        }
        isAllSelected5 = !isAllSelected5;
        log.debug("size of selectedAssetUids5: {}", selectedAssetUids.size());
    }

    public void updateSelctedAssetList(AjaxBehaviorEvent event) {
        log.debug("updateSelctedAssetList");
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssetsed.add(asset);
        } else {
            selectedAssetsed.remove(asset);
        }
    }
    
    public void updateSelctedAssetList5(AjaxBehaviorEvent event) {
        log.debug("updateSelctedAssetList5");
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssets5.add(asset);
        } else {
            selectedAssets5.remove(asset);
        }
    }

//    public void onDeptNameChange(AjaxBehaviorEvent event) {
//        if (event instanceof SelectEvent) {
//            this.selectedDeptName = (String) ((SelectEvent) event).getObject();
//        } else {
//            if (!Strings.isNullOrEmpty(selectedDeptName) && !deptList.stream().anyMatch(item -> selectedDeptName.equals(item.getShortName()))) {
//                selectedDeptName = "";
//            }
//        }
//    }

    public List<String> autoCompleteDeptName(String query) {
        List<OrgInfo> res = deptList.stream().filter(item -> Strings.isNullOrEmpty(query)
                || item.getShortName().contains(query)
                || (item.getShortNamePyCode() == null ? false : item.getShortNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getShortName()).distinct().collect(Collectors.toList());
    }

    private List<AssetInfo> loadAssets4EditView() {
        log.debug("loadAssets4EditView");
        this.buildBaseSearchFilters();
        this.buildAdvancedSearchFilters();
        List<AssetInfo> assetInfos;
        if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
            assetInfos = ListUtil.empty();
        } else {
            if (currentUser.isEnableAssetTypeGroup()) {
                searchFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet(), "1*"));
            }
            assetInfos = assetSelectService.loadAssetsBySearchFilter(this.searchFilters);
        }
        log.debug("{} assets has been loaded", assetInfos.size());
        this.searchFilters.clear();
        return assetInfos;
    }

    private void tiggerEvent() {
        if(this.callingController != null) {
            this.callingController.onServerEvent("change", assets5Maintain.stream().collect(Collectors.toList()));
        }
    }

    /**
     * Build default search filter for access control and to ignore invalid assets
     */
    private void buildBaseSearchFilters() {
        // for access control
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getTenantUID(), "1*"));
        if(selectedOrg.getOrgLevel() == 1) {
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getInstitutionUID(), "1*"));
        }
        if(selectedOrg.getOrgLevel() == 2) {
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getInstitutionUID(), "1*"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getHospitalUID(), "1*"));
        }
        if(selectedOrg.getOrgLevel() == 3) {
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getInstitutionUID(), "1*"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getHospitalUID(), "1*"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getSiteUID(), "1*"));
        }
        // for valid asset
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, "4", "1*"));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));
        if(!CollectionUtils.isEmpty(listCheckI18nMessage)){
            List<String> collect = listCheckI18nMessage.stream().map(item -> item.getMsgKey()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.IN, collect, "1*"));
        }
    }

    /**
     * Build search filter based on form filled by user
     * In the form, depart_num has highest priority, it means if this field isn't blank, it will be
     * used as the only search filter, others will be ignored
     */
    private int buildAdvancedSearchFilters() {
    	int count=0;
        if(StringUtils.isNotBlank(this.getDepartNum())) {
            searchFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, this.getDepartNum(), "1*"));
            ++count;
        } else {
            if(StringUtils.isNotBlank(this.getAssetName())) {
                searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, this.getAssetName(), "1*2+"));
                searchFilters.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, this.getAssetName(), "1*2+"));
                ++count;
            }
            if(StringUtils.isNotBlank(this.getAssetGroup())) {
                searchFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.getAssetGroup(), "1*"));
                ++count;
            }
            if(StringUtils.isNotBlank(this.getFunctionType())) {
                searchFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, this.getFunctionType(), "1*"));
                ++count;
            }
            if(StringUtils.isNotBlank(this.getEmergencyEquipment())) {
                searchFilters.add(new SearchFilter("emergencyEquipment", SearchFilter.Operator.EQ, this.getEmergencyEquipment(), "1*"));
                ++count;
            }
            
            if(listCheckOrgInfo!=null && !listCheckOrgInfo.isEmpty()){
            	Set<String> setOrgInfoUid=new HashSet<>();
                for (OrgInfo orgInfo : listCheckOrgInfo) {
                    setOrgInfoUid.add(orgInfo.getUid());
                }
            	if(!setOrgInfoUid.isEmpty()){
            		searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, setOrgInfoUid, "1*3+"));
                    ++count;
            	}
            }
            if(StringUtils.isNotBlank(this.getLocationName())) {
                searchFilters.add(new SearchFilter("locationName", SearchFilter.Operator.LIKE, this.getLocationName(), "1*"));
                ++count;
            }
        }
        return count;
    }

    private Boolean isSelectAll = Boolean.FALSE;

    public void updateSelectAll() {
        if (Boolean.TRUE.equals(isSelectAll)) {
            listCheckOrgInfo = new ArrayList<>(deptList);
        } else {
            listCheckOrgInfo.clear();
        }
    }

    public void updateItem() {
        isSelectAll = deptList.size() == listCheckOrgInfo.size();
    }

    private void updateManagementOnSiteChange() {
        managementList=   buildManagementRepository.findBySiteUidTree(selectedOrg.getSiteUID());
    }

    public List<String> managementListString(String query) {
        updateManagementOnSiteChange();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(managementList)){
            List<String> list = managementList.stream().map(BuildManagement::getName).filter(item->item.contains(query)).collect(Collectors.toList());
            managementMap= managementList.stream().collect(Collectors.groupingBy(BuildManagement::getName));
            return list;
        }
        return new ArrayList<>();

    }

    public void locationNamechangeSelect(SelectEvent event){
        setLocationCode(null);
        setLocationName(locationName);
        if(event==null){
            return;
        }
        String code =(String) event.getObject();
        if(StringUtils.isNotBlank(code)){
            List<BuildManagement> list = managementMap.get(code);
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(list)){
                BuildManagement buildManagement = list.get(0);
                setLocationCode(buildManagement.getNamePyCode());
                setLocationName(buildManagement.getName());
            }
        }
    }

    public void updateSelectAllAssetGroup(){
        if (Boolean.TRUE.equals(isAllSelectedAssetGroup)) {
            listCheckI18nMessage = new ArrayList<>(i18nMessageList);
        } else {
            listCheckI18nMessage.clear();
        }
    }
    public void updateItemAssetGroup() {
        isAllSelectedAssetGroup = listCheckI18nMessage.size() == i18nMessageList.size();
    }
    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<OrgInfo> getListCheckOrgInfo() {
        return listCheckOrgInfo;
    }

    public void setListCheckOrgInfo(List<OrgInfo> listCheckOrgInfo) {
        this.listCheckOrgInfo = listCheckOrgInfo;
    }
}
