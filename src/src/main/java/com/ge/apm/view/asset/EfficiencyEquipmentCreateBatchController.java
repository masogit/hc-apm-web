package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.EfficiencyEquipmentInfoRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.EfficiencyEquipmentInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.EfficiencyEquipmentInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.UserSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class EfficiencyEquipmentCreateBatchController extends GenericCRUDUUIDController<EfficiencyEquipmentInfo> implements  ServerEventInterface {

    private static final Logger log = LoggerFactory.getLogger(EfficiencyEquipmentCreateBatchController.class);

    @Getter
    @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private OrgInfo filterDeptOrg;


    //采购原值(元)
    @Getter @Setter
    private Double purchasePrice;

    @Getter @Setter
    private Integer assetGroup;//设备分类

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



    @Getter
    @Setter
    private Integer filterAssetGroup;


    private UserSiteOrgTreeComponentController userSiteOrgTreeComponentController;

    private UaaService uaaService;

    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private TreeNode selectedNode;


    @Override
    protected void init() {
        efficiencyEquipmentInfoRepository = WebUtil.getBean(EfficiencyEquipmentInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
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
    protected GenericRepositoryUUID<EfficiencyEquipmentInfo> getDAO() {
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
        if(selectedOrg != null){
            selectAssetInfos = getAssetListByDept(selectedOrg, filterDeptOrg);
        }else{
            selectAssetInfos = new ArrayList<>();
        }

    }


    public List<AssetInfo> getAssetListByDept(OrgInfo siteOrg, OrgInfo clinicalDept) {
        if (null == clinicalDept) {
            return new ArrayList<>();
        }
        List<String> siteOrgUids = orgInfoDao.getSiteOrgUidsByParentUid(siteOrg.getUid());
        List<AssetInfo> assetInfoIsNotEfficiencyEquipment = assetInfoMapper.getAssetInfoIsNotEfficiencyEquipment(siteOrg.getTenantUID(), siteOrgUids, clinicalDept.getUid(),assetGroup,purchasePrice);
        return assetInfoIsNotEfficiencyEquipment;
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
}
