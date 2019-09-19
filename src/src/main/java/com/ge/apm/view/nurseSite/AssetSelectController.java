package com.ge.apm.view.nurseSite;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.AssetSelectService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private Set<AssetInfo> selectedAssets5;

    private Map<String, Boolean> selectedAssetUids;
    
    private Map<String, Boolean> selectedAssetUids5;

    private ServerEventInterface callingController;

    private AssetSelectService assetSelectService;
    
    private List<OrgInfo> listCheckOrgInfo;

    @Override
    protected GenericRepositoryUUID<AssetInfo> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        assetSelectService = WebUtil.getBean(AssetSelectService.class);
        searchFilters = new ArrayList<>(8);
        selectedAssetUids = new HashMap<>(32);
        selectedAssets = new HashSet<>();
        selectedAssetUids5 = new HashMap<>(32);
        selectedAssets5 = new HashSet<>();
        currentUser = UserContextService.getCurrentUserAccount();
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
        this.selectedAssetUids5.clear();
        this.selectedAssets5.clear();
        this.assets4Maintain = null;
        this.assets5Maintain=new ArrayList<>();
        this.assetGroup = null;
        this.assetName = null;
        this.listCheckOrgInfo=new ArrayList<OrgInfo>();
        this.is4Edit = Boolean.FALSE;
    }

    public void loadData4Edit(NurseSiteMaintainConfig config) {
        this.is4Edit = Boolean.TRUE;
        this.orgTree = assetSelectService.loadOrgTree();
        this.selectedOrg = assetSelectService.loadSelectedOrg();

        this.selectedAssets = assetSelectService.loadSelectedAssetByConfig(config.getId()).stream().collect(Collectors.toSet());
        this.selectedAssets.stream().forEach(t -> selectedAssetUids.put(t.getUid(), Boolean.TRUE));
        OrgInfo selectedDept = assetSelectService.getOrgInfoByUid(config.getClinicalDeptUID());
        this.selectedOrg = assetSelectService.getOrgInfoByUid(selectedDept.getParentUID());
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
    }

    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
        if (selectedOrg != null) {
            log.info("selectedOrg: {}", selectedOrg.getShortName());
            this.deptList = assetSelectService.loadDeptListByOrg(selectedOrg);
        }
    }

    public void findAssets2Maintain() {
        //clear selected asset for each search
        this.selectedAssetUids.clear();
        this.selectedAssets.clear();
        this.buildBaseSearchFilters();
        int filterCount=this.buildAdvancedSearchFilters();
        if(filterCount==0){
        	WebUtil.addErrorMessage("缺少筛选条件！");
        }
        else{
        	assets4Maintain = assetSelectService.loadAssetsBySearchFilter(this.searchFilters);
        }
        this.searchFilters.clear();
    }
    
    public void addAssetInfo(){
    	for(AssetInfo assetInfo: selectedAssets){
    		if(!assets5Maintain.contains(assetInfo)){
    			assets5Maintain.add(assetInfo);
    		}
    	}
    	this.tiggerEvent();
    }
    
    public void removeAssetInfo(){
    	assets5Maintain.removeAll(selectedAssets5);
    	this.tiggerEvent();
    }
    
    public void clearAssetInfo(){
    	assets5Maintain.clear();
    }

    public void addAssetsOfCurrentPage(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected: {}", this.isAllSelected);
        if(isAllSelected) {
            assets4Maintain.stream().forEach(t -> {
                selectedAssetUids.put(t.getUid(), Boolean.TRUE);
                selectedAssets.add(t);
            });
        } else {
            assets4Maintain.stream().forEach(t -> {
                selectedAssetUids.put(t.getUid(), Boolean.FALSE);
                selectedAssets.remove(t);
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

    public void updateSelctedAssetList(AjaxBehaviorEvent event) {
        log.debug("updateSelctedAssetList");
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssets.add(asset);
        } else {
            selectedAssets.remove(asset);
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
        List<AssetInfo> assetInfos = assetSelectService.loadAssetsBySearchFilter(this.searchFilters);
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
            
            if(listCheckOrgInfo!=null && listCheckOrgInfo.size()>0){
            	Set<String> setOrgInfoUid=new HashSet<String>();
            	for(int i=0;i<listCheckOrgInfo.size();++i){
            		setOrgInfoUid.add(listCheckOrgInfo.get(i).getUid());
            	}
            	if(setOrgInfoUid.size()>0){
            		searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, setOrgInfoUid, "1*3+"));
                    searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, setOrgInfoUid, "1*3+"));
                    ++count;
            	}
            }
//            if(StringUtils.isNotBlank(this.getSelectedDeptName())) {
//                String selectedDeptUid = deptList.stream().filter(dept -> this.getSelectedDeptName().equals(dept.getShortName())).map(dept -> dept.getUid()).findFirst().get();
//            }
        }
        return count;
    }

}
