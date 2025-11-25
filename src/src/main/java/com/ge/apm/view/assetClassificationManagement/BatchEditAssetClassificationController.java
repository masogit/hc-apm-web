package com.ge.apm.view.assetClassificationManagement;


import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.EfficiencyEquipmentInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.UserSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@Data
public class BatchEditAssetClassificationController extends GenericCRUDController<AssetInfo> implements Serializable, ServerEventInterface {
    private static final Logger log = LoggerFactory.getLogger(BatchEditAssetClassificationController.class);

    @Getter
    @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private OrgInfo filterDeptOrg;
    @Getter @Setter
    private List<OrgInfo> selectsDeptOrg=new ArrayList<>();

    @Getter @Setter
    private List<String> selectsDeptOrgUid=new ArrayList<>();

    //采购原值(元)
    @Getter @Setter
    private Double purchasePrice;

    @Getter @Setter
    private Integer assetGroup;//设备分类

    private String assetName;
    private String functionType;

    private String manufacture;//制造商

    private String makerShortName;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;


    @Getter @Setter
    private List<AssetInfo> selectAssetInfos;


    @Getter
    @Setter
    private Boolean allSelected1 = Boolean.FALSE;
    @Getter
    @Setter
    private Boolean allSelected2 = Boolean.FALSE;
    @Getter
    @Setter
    private Map<String, Boolean> selectedAssetUidTable1;
    @Getter
    @Setter
    private Map<String, Boolean> selectedAssetUidTable2;
    @Getter
    @Setter
    private Set<AssetInfo> selectedAssetsed1;
    @Getter
    @Setter
    private Set<AssetInfo> selectedAssetsed2;
    @Getter
    @Setter
    private Set<AssetInfo> selectAsset;

    private EfficiencyEquipmentInfoRepository efficiencyEquipmentInfoRepository;

    private UserAccount user;


    private OrgInfoRepository orgInfoDao;



    private AssetInfoMapper assetInfoMapper;

    private AssetInfoRepository  assetInfoRepository;



    @Getter
    @Setter
    private Integer filterAssetGroup;


    private UserSiteOrgTreeComponentController userSiteOrgTreeComponentController;

    private UaaService uaaService;

    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private TreeNode selectedNode;
    private MasterDataService masterDataService;

    @Override
    protected void init() {
        efficiencyEquipmentInfoRepository = WebUtil.getBean(EfficiencyEquipmentInfoRepository.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);
        user = UserContextService.getCurrentUserAccount();
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userSiteOrgTreeComponentController = WebUtil.getBean(UserSiteOrgTreeComponentController.class);
        userSiteOrgTreeComponentController.registerController(this);
        userSiteOrgTreeComponentController.setSelectedOrg(null);


        uaaService = WebUtil.getBean(UaaService.class);
        initOrgTree();
        selectedAssetUidTable1=new HashMap<>();
        selectedAssetUidTable2=new HashMap<>();
        selectedAssetsed1=new HashSet<>();
        selectedAssetsed2=new HashSet<>();
        selectAsset=new HashSet<>();

    }

    private void initOrgTree() {
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(user.getSiteUID(), user.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root",null,null);
        TreeNode rootNode = new DefaultTreeNode("org",rootOrg,orgTree);
        if(!StringUtils.isEmpty(treeNode)){
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNode2(NodeSelectEvent event) {

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }
        loadClinicalDept();

    }
    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return null;
    }


    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.emptyList();
        }else{
            clinicalDeptList = orgInfoDao.getUserAccessibleClinicalDeptsBySelectedSite(selectedOrg.getUid(), UserContextService.getCurrentUserAccount().getUid());
        }
    }

    @Override
    public void save() {
        try {
            if(CollectionUtils.isEmpty(selectAsset)){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("请选择添加效益设备");
                return;
            }
            List<EfficiencyEquipmentInfo> efficiencyEquipmentInfos = initializationEfficiencyEquipmentInfo(selectAsset, user);
            efficiencyEquipmentInfoRepository.save(efficiencyEquipmentInfos);
            clearSelectedInPickList();
            WebUtil.addErrorMessage("新增效益设备成功");
        } catch (Exception e) {
            logger.error("新增效益设备异常,e: ",e);
        }
    }

    public void  cancel(){
        clearSelectedInPickList();

    }

    private List<EfficiencyEquipmentInfo> initializationEfficiencyEquipmentInfo(Set<AssetInfo> assetInfos, UserAccount user) {

        if (CollectionUtils.isEmpty(assetInfos)) return ListUtil.empty();
        return  assetInfos.stream().map(item->{
            item.setSpecialAsset(Boolean.TRUE);
            EfficiencyEquipmentInfo efficiencyEquipmentInfo= new EfficiencyEquipmentInfo();
            efficiencyEquipmentInfo.setAssetUid(item.getUid());
            efficiencyEquipmentInfo.setCreatedByUid(user.getUid());
            efficiencyEquipmentInfo.setCreatedDate(new Date());
            efficiencyEquipmentInfo.setAssetName(item.getName());
            efficiencyEquipmentInfo.setQrCode(item.getQrCode());
            efficiencyEquipmentInfo.setSerialNum(item.getSerialNum());
            efficiencyEquipmentInfo.setFunctionType(item.getFunctionType());
            return efficiencyEquipmentInfo;
        }).collect(Collectors.toList());
    }


    private void clearSelectedInPickList() {

        selectedOrg=null;
        filterDeptOrg=null;
        selectAssetInfos.clear();
        selectedAssetUidTable1=new HashMap<>();
        selectedAssetUidTable2=new HashMap<>();
        selectedAssetsed1=new HashSet<>();
        selectedAssetsed2=new HashSet<>();
        selectAsset=new HashSet<>();
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? Boolean.FALSE : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onClinicalDeptChange() {

        getQueryAssetsList();
    }

    public void getQueryAssetsList() {
        if(CollectionUtils.isNotEmpty(selectsDeptOrg)){
            selectAssetInfos = getAssetListByDept(selectedOrg, selectsDeptOrg);
        }else{
            selectAssetInfos = new ArrayList<>();
        }

    }


    public List<AssetInfo> getAssetListByDept(OrgInfo siteOrg, List<OrgInfo> clinicalDepts) {
        if(CollectionUtils.isEmpty(clinicalDepts)){
            return new ArrayList<>();
        }
//        List<String> siteOrgUids = orgInfoDao.getSiteOrgUidsByParentUid(siteOrg.getUid());
//
//        HashMap<String, Object> params = new HashMap<>();
//        params.put("tenantUid",siteOrg.getTenantUID());
//        params.put("siteUid",siteOrg.getUid());
//        params.put("clinicalDeptUid",clinicalDept.getUid());
//        params.put("name",assetName);
//        params.put("functionType",functionType);
//        params.put("manufacture",manufacture);
//        params.put("makerShortName",makerShortName);

        List<String> collect = clinicalDepts.stream().map(OrgInfo::getUid).collect(Collectors.toList());
        this.searchFilters=new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, siteOrg.getTenantUID(), "1*"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteOrg.getSiteUID(), "1*"));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, collect, "1*"));
//        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, clinicalDept.getUid(), "1*"));
        if(org.apache.commons.lang3.StringUtils.isNotBlank(assetName)){
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE,assetName, "1*"));
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(functionType)){
            searchFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE,functionType, "1*"));
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(manufacture)){
            searchFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE,manufacture, "1*"));
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(makerShortName)){
            searchFilters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE,makerShortName, "1*"));
        }

        List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(this.searchFilters);


//        List<AssetInfo> assetInfos = assetInfoMapper.queryAssetInfo(params);
        return assetInfos;
    }

    public void updateSelctedAssetList1(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssetsed1.add(asset);
        } else {
            selectedAssetsed1.remove(asset);
        }
        allSelected1=selectAssetInfos.stream().allMatch(item-> Optional.ofNullable(selectedAssetUidTable1.get(item.getUid())).orElse(Boolean.FALSE));
    }
    public void updateSelctedAssetList2(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssetsed2.add(asset);
        } else {
            selectedAssetsed2.remove(asset);
        }
        allSelected2=selectAssetInfos.stream().allMatch(item->Optional.ofNullable(selectedAssetUidTable2.get(item.getUid())).orElse(Boolean.FALSE));
    }


    public void addAssetsOfCurrentPage1(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected: {}", this.allSelected1);
        if(allSelected1) {
            selectAssetInfos.stream().forEach(t -> {
                selectedAssetUidTable1.put(t.getUid(), Boolean.TRUE);
                selectedAssetsed1.add(t);
            });
        } else {
            selectAssetInfos.stream().forEach(t -> {
                selectedAssetUidTable1.put(t.getUid(), Boolean.FALSE);
                selectedAssetsed1.remove(t);
            });
        }
        log.debug("size of selectedAssetUids: {}", selectedAssetUidTable1.size());
    }
    public void addAssetsOfCurrentPage2(AjaxBehaviorEvent event) {
        log.debug("value of allSelected2: {}", this.allSelected2);
        if(allSelected2) {
            selectAsset.stream().forEach(t -> {
                selectedAssetUidTable2.put(t.getUid(), Boolean.TRUE);
                selectedAssetsed2.add(t);
            });
        } else {
            selectAsset.stream().forEach(t -> {
                selectedAssetUidTable2.put(t.getUid(), Boolean.FALSE);
                selectedAssetsed2.remove(t);
            });
        }
        log.debug("size of selectedAssetsed2: {}", selectedAssetUidTable2.size());
    }

    public void addCheckedDevice(){
        for (AssetInfo assetInfo : selectedAssetsed1) {
            boolean b = selectAsset.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
            if(!b){
                selectAsset.add(assetInfo);
                assetInfo.setCategoryRadio(assetInfo.getAssetClassification32());
                if(org.springframework.util.CollectionUtils.isEmpty(assetInfo.getAvailableCategories())) {
                    AssetClassificationModel assetClassificationModel = new AssetClassificationModel();
                    assetClassificationModel.setBrand(assetInfo.getMakerShortName());
                    assetClassificationModel.setPattern(assetInfo.getFunctionType());
                    assetClassificationModel.setSourceName(assetInfo.getName());
                    assetClassificationModel.setFactory(assetInfo.getManufacture());
                    Map<String, Object> resMapByAsset = masterDataService.getResMapByAsset(assetClassificationModel);
                    if (resMapByAsset != null) {
                        assetInfo.setCategory1(resMapByAsset.get("category") != null ? (String) resMapByAsset.get("category") : null);
                        assetInfo.setCategory2(resMapByAsset.get("category2") != null ? (String) resMapByAsset.get("category2") : null);
                        assetInfo.setCategory3(resMapByAsset.get("category3") != null ? (String) resMapByAsset.get("category3") : null);

                        List<String> objects = new ArrayList<>();
                        if (org.apache.commons.lang3.StringUtils.isNotBlank((String) resMapByAsset.get("category"))) {
                            objects.add((String) resMapByAsset.get("category"));
                        }
                        if (org.apache.commons.lang3.StringUtils.isNotBlank((String) resMapByAsset.get("category2"))) {
                            objects.add((String) resMapByAsset.get("category2"));
                        }
                        if (org.apache.commons.lang3.StringUtils.isNotBlank((String) resMapByAsset.get("category3"))) {
                            objects.add((String) resMapByAsset.get("category3"));
                        }
                        assetInfo.setAvailableCategories(objects);
                    }
                }
            }
        }
        allSelected1 = Boolean.FALSE;
        selectedAssetsed1.clear();
        selectedAssetUidTable1.clear();
    }


    public void deleteCheckedDevice(){
        for (AssetInfo assetInfo : selectedAssetsed2) {
            boolean b = selectAsset.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
            if(b){
                selectAsset.remove(assetInfo);
            }
        }
        allSelected2 = Boolean.FALSE;
        selectedAssetsed2.clear();
        selectedAssetUidTable2.clear();
    }
    public void clearAll(){
        selectAsset.clear();
        allSelected1 = Boolean.FALSE;
        selectedAssetsed1.clear();
        selectedAssetUidTable1.clear();
        allSelected2 = Boolean.FALSE;
        selectedAssetsed2.clear();
        selectedAssetUidTable2.clear();

    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    public void batchEdit() {

        if(CollectionUtils.isEmpty(selectAsset)){
            WebUtil.addErrorMessage("请选择添加设备");
            return;
        }
        assetInfoRepository.save(selectAsset);
        clearAll();
        WebUtil.addErrorMessage("批量设置成功");

    }



    public void selectCategory(AssetInfo item, String selectedCategory) {
        item.setAssetClassification32(selectedCategory);
    }

    public void assetClassification32Change(AssetInfo asset) {
        asset.setCategoryRadio(asset.getAssetClassification32());
    }

    public void categoryRadioChange(AssetInfo asset) {
        asset.setAssetClassification32(asset.getCategoryRadio());
    }

    public List<OrgInfo> getSelectsDeptOrg() {
        return selectsDeptOrg;
    }

    public void setSelectsDeptOrg(List<OrgInfo> selectsDeptOrg) {
        this.selectsDeptOrg = selectsDeptOrg;
    }

//    public void selectCategory(AssetInfo iteme) {
//        item.setAssetClassification32(selectedCategory);
//    }


    public List<String> getSelectsDeptOrgUid() {
        return selectsDeptOrgUid;
    }

    public void setSelectsDeptOrgUid(List<String> selectsDeptOrgUid) {
        this.selectsDeptOrgUid = selectsDeptOrgUid;
    }
}
