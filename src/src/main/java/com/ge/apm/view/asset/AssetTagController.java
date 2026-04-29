package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.asset.CustomConfigService;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.lang.reflect.Field;
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

    CustomConfigService ccService;

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

    private String assetName;

    private List<I18nMessage> spareList;

    private String spareField;

    private String spareFieldValue;

    private CustomConfigObject customConfigObject;

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
        ccService = WebUtil.getBean(CustomConfigService.class);

        currentUser = UserContextService.getCurrentUserAccount();

        this.siteIdFilter = currentUser.getSiteId();

        targetBiomedGroup = new ArrayList<>();
        queryBiomedGroup = new BiomedGroup();

        targetAssets = new ArrayList<>();
        queryAsset = new AssetInfo();

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
        initSpareList();

    }

    @Override
    protected AssetTagRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetTag> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

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

        return dao.findBySearchFilter(searchFilters, pageRequest);
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

    public void getQueryBiomedGroupList() {
        sourceBiomedGroup = assetTagService.getBiomedGroupList(queryBiomedGroup);

        sourceBiomedGroup = new ArrayList<>(sourceBiomedGroup);
        sourceBiomedGroup.removeAll(targetBiomedGroup);

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
        getSpareFieldString();
        if (selectedOrg != null && selectedOrg.getOrgLevel() != null && (spareFieldValue != null || filterDeptOrg != null || queryAsset.getAssetGroup() != null || queryAsset.getFunctionType() != null || this.assetName != null)) {

            if(filterDeptOrg != null){
                queryAsset.setClinicalDeptId(filterDeptOrg.getId());
            }else {
                queryAsset.setClinicalDeptId(null);
            }
            if(this.assetName != null){
                queryAsset.setName(this.assetName);
            }else{
                queryAsset.setName(null);
            }
            sourceAssets = assetTagService.getAssetList(queryAsset,this::add);

            sourceAssets = new ArrayList(sourceAssets);
            sourceAssets.removeAll(targetAssets);

        } else {
            sourceAssets = new ArrayList<>();
        }
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }
    public void add(List<SearchFilter> searchFilters){
            if(!StringUtils.isEmpty(spareFieldValue)){
                searchFilters.add(new SearchFilter(spareField, SearchFilter.Operator.EQ,spareFieldValue));
            }
        spareFieldValue = null;
    }
    public void getSpareFieldString(){
        try {
        if(StringUtils.isEmpty(spareField) ) return;
        Class<? extends AssetInfo> aClass = AssetInfo.class;
        Field declaredField = aClass.getDeclaredField(spareField);
        declaredField.setAccessible(true);
        Object o = declaredField.get(queryAsset);
        spareFieldValue =(String) o;
        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
            spareFieldValue = null;
            logger.error(e.getMessage(),e);
        }
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
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.initSearchData();
        //this.getQueryBiomedGroupList();
        //this.getQueryAssetsList();

        targetBiomedGroup = new ArrayList<>();
        sourceBiomedGroup = assetTagService.getBiomedGroupList(queryBiomedGroup);
        targetAssets = new ArrayList<>();
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
        availableBiomedGroup = new DualListModel<>(sourceBiomedGroup, targetBiomedGroup);

    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.initSearchData();

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
            }else if(selectedOrg.getOrgLevel() == 2){
                queryAsset.setHospitalUID(selectedOrg.getHospitalUID());
            }else if(selectedOrg.getOrgLevel() == 3){
                queryAsset.setSiteUID(selectedOrg.getSiteUID());
            }
        }

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
    private void initSpareList(){
        String[] spareKey = {"spare01","spare02","spare03","spare04","spare05","spare06","spare07","spare08","spare09","spare10"};
        List<String> spareKeyList = Arrays.asList(spareKey);
        String siteUID = UserContextService.getCurrentUserAccount().getSiteUID();
        CustomConfig customConfig = ccService.getCustomConfigBySite(siteUID);
        if (null == customConfig) {
            customConfigObject = ccService.getCurrentCustomConfig(siteUID);
        } else {
            customConfigObject = JSONObject.parseObject(customConfig.getConfig(), CustomConfigObject.class);
        }
        Map<String, CustomConfigAssetField> assetFields = customConfigObject.getAssetFields();
        spareKeyList= spareKeyList.stream().filter(item -> assetFields.containsKey(item)).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(spareKeyList)){
            spareList = spareKeyList.stream().map(item -> {
                String message = WebUtil.getMessage(item);
                I18nMessage i18nMessage = new I18nMessage();
                i18nMessage.setMsgKey(item);
                i18nMessage.setValueZh(message);
                return i18nMessage;
            }).collect(Collectors.toList());
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

    private void initSearchData(){
        selectedOrg = null;
        clinicalDeptList = null;
        filterDeptOrg = null;
        assetName = null;

        queryAsset.setClinicalDeptId(null);
        queryAsset.setAssetGroup(null);
        queryAsset.setFunctionType(null);
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

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public List<I18nMessage> getSpareList() {
        return spareList;
    }

    public void setSpareList(List<I18nMessage> spareList) {
        this.spareList = spareList;
    }

    public String getSpareField() {
        return spareField;
    }

    public void setSpareField(String spareField) {
        this.spareField = spareField;
    }
}
