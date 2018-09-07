package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.orginfo.OrgInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2017/5/12.
 */
@ManagedBean
@ViewScoped
public class AssetTagController extends JpaCRUDController<AssetTag> {

    AssetTagRepository dao = null;
    I18nMessageRepository i18nDao = null;
    TenantInfoRepository tenantInfoDao = null;
    OrgInfoRepository orgInfoDao = null;

    private DualListModel<BiomedGroup> availableBiomedGroup;
    private List<BiomedGroup> sourceBiomedGroup;
    private List<BiomedGroup> targetBiomedGroup;
    private BiomedGroup queryBiomedGroup;

    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private AssetInfo queryAsset;

    private AssetTagService assetTagService;

    private Integer siteId;
    private Integer hospitalId;

    private Integer siteIdFilter;
    private Integer hospitalIdFilter;

    private UserAccount currentUser;

    private OrgInfoService orgInfoService;
    private AssetInfoService assetInfoService;

    private UaaService uaaService;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private List<OrgInfo> clinicalDeptList;
    private OrgInfo filterDeptOrg;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetTagRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetTagService = WebUtil.getBean(AssetTagService.class);
        orgInfoService = WebUtil.getBean(OrgInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        this.siteIdFilter = currentUser.getSiteId();

        targetBiomedGroup = new ArrayList<>();
        queryBiomedGroup = new BiomedGroup();

        targetAssets = new ArrayList();
        queryAsset = new AssetInfo();
        //clinicalDeptList = new ArrayList<OrgInfo>();

        queryBiomedGroup.setTenantUID(currentUser.getTenantUID());
        queryAsset.setTenantUID(currentUser.getTenantUID());

        if(currentUser.getOrgLevel() != 0) {
            if (currentUser.getOrgLevel() == 1) {
                queryBiomedGroup.setInstitutionUID(currentUser.getInstitutionUID());
                queryAsset.setInstitutionUID(currentUser.getInstitutionUID());
            } else if (currentUser.getOrgLevel() == 2) {
                queryBiomedGroup.setHospitalUID(currentUser.getHospitalUID());
                queryAsset.setHospitalUID(currentUser.getHospitalUID());
            } else {
                queryBiomedGroup.setSiteUID(currentUser.getSiteUID());
                queryAsset.setSiteUID(currentUser.getSiteUID());
            }
        }

        this.getQueryBiomedGroupList();
        this.getQueryAssetsList();

        initOrgTree();
        this.loadClinicalDept();

    }

    @Override
    protected AssetTagRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetTag> loadData(PageRequest pageRequest) {

        selected = null;

        List<SearchFilter> searchFilters = this.searchFilters;
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));

        if(currentUser.getOrgLevel() != 0){
            if(currentUser.getOrgLevel() == 1){
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
            }else if(currentUser.getOrgLevel() == 2){
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
            }else{
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            }
        }

        if(searchFilters.size() <= 0){
            return dao.findAll(pageRequest);
        }else{
            return dao.findBySearchFilter(searchFilters, pageRequest);
        }
    }

    @Override
    public List<AssetTag> getItemList() {
        return dao.find();
    }

    public String getTenantName(String tenantUid) {
        return tenantInfoDao.findByuid(tenantUid).getName();
    }
    public String getSiteName(String siteUid) {
        return orgInfoDao.getByUid(siteUid).getName();
    }

    public List<TenantInfo> getSiteList() {

        List<TenantInfo> siteList = tenantInfoDao.find();

        return siteList;
    }

    public List<OrgInfo> getHospitalList() {

        List<OrgInfo> hospitalList = null;
        if (siteId != null) {

            hospitalList = orgInfoDao.getHospitalBySiteId(Integer.valueOf(siteId));
        }

        return hospitalList;
    }

    public List<OrgInfo> getHospitalListFilter() {

        List<OrgInfo> hospitalList = null;
        if (siteIdFilter != null) {

            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            hospitalList = orgInfoDao.getOrgInfosByParentId(userAccount.getSiteUID());
            hospitalList = orgInfoService.flattenOrgTree(hospitalList);
        }

        return hospitalList;
    }

    public void onSiteChange(){
        if(siteId == null){
            hospitalId = null;
        }
    }

    public void onSiteFilterChange(){
        if(siteIdFilter == null){
            hospitalIdFilter = null;
        }
    }

    public void getQueryBiomedGroupList() {
        sourceBiomedGroup = assetTagService.getBiomedGroupList(queryBiomedGroup);
        targetBiomedGroup.forEach((item)->{
            if(sourceBiomedGroup.contains(item)){
                sourceBiomedGroup.remove(item);
            }
        });

        availableBiomedGroup = new DualListModel<>(sourceBiomedGroup, targetBiomedGroup);
    }

    public void onBiomedGroupTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetBiomedGroup.add((BiomedGroup)item);
            }else{
                targetBiomedGroup.remove((BiomedGroup)item);
            }
        }
    }

    public void getQueryAssetsList() {
        if(selectedOrg != null && selectedOrg.getOrgLevel() != null){
            sourceAssets = assetTagService.getAssetListByDept(queryAsset, filterDeptOrg, clinicalDeptList);

            sourceAssets = new ArrayList(sourceAssets);
            sourceAssets.removeAll(targetAssets);
            /*
            targetAssets.forEach((item)->{
                if(sourceAssets.contains(item)){
                    sourceAssets.remove(item);
                }
            });*/
        }else{
            sourceAssets = new ArrayList<AssetInfo>();
        }
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public void onAssetsTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetAssets.add((AssetInfo)item);
            }else{
                targetAssets.remove((AssetInfo)item);
            }
        }
    }

    public void save(){
        /*if(selectedOrg != null){
            this.selected.setSiteId(selectedOrg.getSiteId());
            this.selected.setHospitalId(selectedOrg.getHospitalId());
            this.selected.setTenantUID(selectedOrg.getTenantUID());
            this.selected.setInstitutionUID(selectedOrg.getInstitutionUID());
            this.selected.setHospitalUID(selectedOrg.getHospitalUID());
            this.selected.setSiteUID(selectedOrg.getSiteUID());
        }*/
        super.save();
        assetTagService.saveAssetTag(this.getSelected(), targetBiomedGroup, targetAssets);
    }

    @Override
    public void onBeforeNewObject(AssetTag object) {
        object.setSiteId(currentUser.getSiteId());
        object.setHospitalId(currentUser.getHospitalId());
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());

        selectedOrg = null;
        clinicalDeptList = null;
        filterDeptOrg = null;

        targetBiomedGroup = assetTagService.getTargetBiomedGroup(this.getSelected());
        this.getQueryBiomedGroupList();

        targetAssets = assetTagService.getTargetAssets(this.getSelected());
        this.getQueryAssetsList();


    }

    @Override
    public void prepareEdit() {
        queryAsset.setInstitutionUID(null);
        queryAsset.setHospitalUID(null);
        queryAsset.setSiteUID(null);
        queryAsset.setClinicalDeptId(null);
        queryAsset.setAssetGroup(null);

        selectedOrg = null;
        filterDeptOrg = null;

        super.prepareEdit();

        targetBiomedGroup = assetTagService.getTargetBiomedGroup(this.getSelected());
        this.getQueryBiomedGroupList();

        targetAssets = assetTagService.getTargetAssets(this.getSelected());
        this.getQueryAssetsList();

    }

    public void delete() {
        AssetTag assetTag = this.selected;
        assetTagService.delete(assetTag);
        this.selected = null;

    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        queryAsset.setInstitutionUID(null);
        queryAsset.setHospitalUID(null);
        queryAsset.setSiteUID(null);

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        if(selectedOrg != null && selectedOrg.getOrgLevel() != null){
            if(selectedOrg.getOrgLevel() == 1){
                queryAsset.setInstitutionUID(selectedOrg.getInstitutionUID());
                //clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getInstitutionUID(), 4);
            }else if(selectedOrg.getOrgLevel() == 2){
                queryAsset.setHospitalUID(selectedOrg.getHospitalUID());
                ///clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getHospitalUID(), 4);
            }else if(selectedOrg.getOrgLevel() == 3){
                queryAsset.setSiteUID(selectedOrg.getSiteUID());
                //clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getSiteUID(), 4);
            }
        }

        this.getQueryAssetsList();
        this.loadClinicalDept();

    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root",null,null);
        TreeNode rootNode = new DefaultTreeNode("org",rootOrg,orgTree);
        rootNode.getChildren().add(treeNode);
        //rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(loginUser.getTenantUID()).getChildren());
        rootNode.setExpanded(true);

    }

    public String getOrgName(String siteUID){
        OrgInfo orgInfo = orgInfoDao.getByUid(siteUID);
        if(orgInfo != null){
            return orgInfo.getName();
        }else{
            return "";
        }
    }

    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.EMPTY_LIST;
        }else{
            clinicalDeptList = assetInfoService.getClinicalDeptList(selectedOrg.getSiteUID());
            if(null != filterDeptOrg && !filterDeptOrg.getSiteUID().equals(selectedOrg.getSiteUID()) ){
                filterDeptOrg = null;
            }
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getSiteIdFilter() {
        return siteIdFilter;
    }

    public void setSiteIdFilter(Integer siteIdFilter) {
        this.siteIdFilter = siteIdFilter;
    }

    public Integer getHospitalIdFilter() {
        return hospitalIdFilter;
    }

    public void setHospitalIdFilter(Integer hospitalIdFilter) {
        this.hospitalIdFilter = hospitalIdFilter;
    }

    public DualListModel<BiomedGroup> getAvailableBiomedGroup() {
        return availableBiomedGroup;
    }

    public void setAvailableBiomedGroup(DualListModel<BiomedGroup> availableBiomedGroup) {
        this.availableBiomedGroup = availableBiomedGroup;
    }

    public List<BiomedGroup> getSourceBiomedGroup() {
        return sourceBiomedGroup;
    }

    public void setSourceBiomedGroup(List<BiomedGroup> sourceBiomedGroup) {
        this.sourceBiomedGroup = sourceBiomedGroup;
    }

    public List<BiomedGroup> getTargetBiomedGroup() {
        return targetBiomedGroup;
    }

    public void setTargetBiomedGroup(List<BiomedGroup> targetBiomedGroup) {
        this.targetBiomedGroup = targetBiomedGroup;
    }

    public DualListModel<AssetInfo> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<AssetInfo> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<AssetInfo> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<AssetInfo> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<AssetInfo> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public BiomedGroup getQueryBiomedGroup() {
        return queryBiomedGroup;
    }

    public void setQueryBiomedGroup(BiomedGroup queryBiomedGroup) {
        this.queryBiomedGroup = queryBiomedGroup;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public OrgInfo getFilterDeptOrg() {
        return filterDeptOrg;
    }

    public void setFilterDeptOrg(OrgInfo filterDeptOrg) {
        this.filterDeptOrg = filterDeptOrg;
    }
}
