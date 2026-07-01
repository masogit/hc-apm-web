package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.OrgInfoComparator;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
@ManagedBean
@ViewScoped
public class RelRuleAssetController extends GenericCRUDUUIDController<RelRuleAsset> {
    private String installDateFormatTime = null;
    private Date varInstallDateTo = null;
    private Date installDateFrom;
    private Date installDateTo;
    private Date startInstallDate;
    private Date endInstallDate;
    private static final long serialVersionUID = -1L;
    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private UserAccount user;
    private AssetInfoRepository assetInfoRepository;
    private QueryAsset queryAsset;
    private RelRuleAssetRepository relRuleAssetRepository;
    private String pmOrderRuleUID;
    private Integer level;
    private PmOrderRuleRepository pmOrderRuleRepository;
    private OrgInfoRepository orgInfoRepository;
    private String siteUid;
    private BrandRepository brandRepository;
    private Boolean isInsulate;
    private Integer loginUserOrgType;
    private UserCommonService userCommonService;
    private PmOrderRule pmOrderRule;
    public static List<String> assetInfoUids = null;
    private List<ZkbFileDirectory> subZkbFileDirectoryList=null;
    @Getter @Setter
    private PmOrderRule pmOrderRuleTwoLevel;
    @Getter @Setter
    private PmOrderRule pmOrderRuleThreeLevel;
    private String ruleUid;
    //private SupplierRepository supplierDao;
    private SupplierService supplierService;
    private ConfigService configService;
    private TenantInfo selectedTenant;
    private ZkbFileDirectoryService zkbFileDirectoryService;
    private List<OrgInfo> selectedUserOrg;
    private OrgInfo selectedOrg;
    private TreeNode orgTree;
    private TreeNode[] selectedNode;
    private TenantInfoRepository tenantInfoRepository;
    private TableDefinitionRepository tableDao;
    private ZkbDirectoryTableDefineRepository zkbDirectoryRepository;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;
    private OrgInfo clinicalOrgInfo;
    private OrgInfo assetDeptOrgInfo;
    private boolean isYmyg = false;
    private OrgInfo userOrgInfo;
    private String zkbBatch;
    private List<AssetTypeGroup> assetTypeGroupList;

    private ConfigItemValueRepository configItemValueRepository;
    @Override
    protected void init() {
        this.filterByHospital = false;
        this.filterBySite = false;
        user = UserContextService.getCurrentUserAccount();
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tableDao = WebUtil.getBean(TableDefinitionRepository.class);
        zkbDirectoryRepository = WebUtil.getBean(ZkbDirectoryTableDefineRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        zkbFileDirectoryService = WebUtil.getBean(ZkbFileDirectoryService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        brandRepository = WebUtil.getBean(BrandRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        targetAssets = new ArrayList<>();
        queryAsset = new QueryAsset();
        queryAsset.setTenantUID(user.getTenantUID());
        siteUid = user.getSiteUID();
        initAssets();
        this.zkbBatch = WebUtil.getRequestParameter("zkbBatch");
        userCommonService = WebUtil.getBean(UserCommonService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        isInsulate = UserContextService.isInsulate();
        loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = new ArrayList<>();
        userOrgInfo = orgInfoRepository.getByUid(user.getSiteUID());
        selectedUserOrg.add(userOrgInfo);
        isYmyg = UserContextService.isYiMaiYangGuangTenantForServer();
        assetTypeGroupList=user.getAssetTypeGroupList();
        //initTree(user.getTenantUID());
        initOrgTree();
        if (isYmyg) {
            getAssetList();
        }
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }
    public boolean isMultiplePMRuleWithAsset(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmFourthRuleLevelForAsset();
    }
    /**
     * 根据主目录值获取子目录信息
     * @return
     */
    public List<ZkbFileDirectory> getSubZkbFileDirectoryList() {
        if(StringUtils.isBlank(this.queryAsset.getMainDirectory())){
            return null;
        }
        List<ZkbFileDirectory> mainZkbFileDirectory = this.zkbFileDirectoryService.findByLevelAndName(
                ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE,
                this.queryAsset.getMainDirectory());
        if(CollectionUtils.isEmpty(mainZkbFileDirectory)){
            return null;
        }
        subZkbFileDirectoryList=this.zkbFileDirectoryService.findByZkbFileDirectory(mainZkbFileDirectory.get(0));
        return subZkbFileDirectoryList;
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
//        orgTree = new DefaultTreeNode("root",null,null);
//        orgTree.getChildren().add(treeNode);
//        orgTree.setExpanded(true);

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);

    }

    private void initTree(String tenantUid) {
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
        selectedTenant = tempTenantInfo;
        orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
 /*       if(UserContextService.isInsulate()){
            //隔离租户
            selectedTenant = tempTenantInfo;
            orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
        }else{
            //非隔离租户
            selectedTenant = tenantInfoRepository.findByuid(tenantUid);
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setId(-1);
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
            orgTree = new DefaultTreeNode("root", null, null);
            TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
            rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenantWithoutClinical(tenantUid).getChildren());
        }*/

        TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
        if (null != selectedUserOrg) {
            selectedUserOrg.setSelected(true);
            TreeNodeUtil.expandParentsNodes(selectedUserOrg);
        }
    }

    public void onSelectOrgForUser(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        if (!isYmyg) {
            selectedUserOrg.clear();
            //selectedUserOrg.add(userOrgInfo);
        }  else {
            setTreeNodes(node, true);
        }
        selectedUserOrg.add((OrgInfo) node.getData());
        addFilter();
    }

    public void onUnSelectOrgForUser(NodeUnselectEvent event) {
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        selectedUserOrg.remove((OrgInfo)node.getData());
        addFilter();
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        if (root == null) {
            return;
        }
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            if (isSelected && !ObjectUtils.isEmpty(item)) {
                selectedUserOrg.add((OrgInfo)item.getData());
            } else {
                selectedUserOrg.remove((OrgInfo)item.getData());
            }
            setTreeNodes(item, isSelected);
        }
    }

    private void updateParentNode(TreeNode parentNode) {
        if (parentNode == null) {
            return;
        }
        boolean result = false;
        for (TreeNode item : parentNode.getChildren()) {
            result = item.isSelected();
            if (result) {
                break;
            }
        }
        parentNode.setSelected(result);
    }

    public void initAssets() {
        sourceAssets = new ArrayList<>();
        targetAssets.forEach((item) -> {
            if (sourceAssets.contains(item)) {
                sourceAssets.remove(item);
            }
        });
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public String getOrgName(String uid) {
        OrgInfo orgInfo = orgInfoRepository.getByUid(uid);
        if (orgInfo == null) {
            return null;
        }
        return orgInfo.getName();
    }

    public String getOrgFullName(List<OrgInfo> org) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(org)) {
            org.forEach(val -> sb.append(val.getName() + ","));
            String name = sb.toString();
            return name.substring(0, name.length()-1);
        }
        return null;
//        if(null == org || org.getId() == -1) {
//            return null;
//        }
//        String name = org.getName();
///*        while (org.getParentOrg() != null) {
//            org = org.getParentOrg();
//            name = org.getName() + "/" + name;
//        }*/
//        return name;
    }
    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
    }
    @Override
    protected Page<RelRuleAsset> loadData(PageRequest pageRequest) {

        return null;
    }

    public List<AssetInfo> getAssetList() {
        if (user.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new ArrayList<>();
        }
        List<SearchFilter> assetFilters = new ArrayList<SearchFilter>();
        List<String> uids = null;
        if (queryAsset.getPurchaseDateStart() != null) {
            assetFilters.add(new SearchFilter("purchaseDate", SearchFilter.Operator.GTE, queryAsset.getPurchaseDateStart()));
        }
        if (queryAsset.getPurchaseDateEnd()!=null) {
            assetFilters.add(new SearchFilter("purchaseDate", SearchFilter.Operator.LTE
                    , queryAsset.getPurchaseDateEnd()));
        }

        if (queryAsset.getRiskGrade()!=null) {
            assetFilters.add(new SearchFilter("riskGrade", SearchFilter.Operator.EQ, queryAsset.getRiskGrade()));
        }

        if (queryAsset.getStartInstallDate() != null) {

            assetFilters.add(new SearchFilter("installDate", SearchFilter.Operator.GTE, queryAsset.getStartInstallDate()));
        }
        if (queryAsset.getEndInstallDate()!=null) {
            assetFilters.add(new SearchFilter("installDate", SearchFilter.Operator.LTE
                    , queryAsset.getEndInstallDate()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
            assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getName())) {
            assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
        }
        if (null != queryAsset.getPurchasePriceStart()) {
            assetFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, queryAsset.getPurchasePriceStart()));
        }
        if (null != queryAsset.getPurchasePriceEnd()) {
            assetFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.LTE, queryAsset.getPurchasePriceEnd()));
        }
		if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
			assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
		}
        if (StringUtils.isNotEmpty(queryAsset.getMainDirectory())) {
            assetFilters.add(new SearchFilter("mainDirectory", SearchFilter.Operator.EQ, queryAsset.getMainDirectory()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getSecondDirectory())) {
            assetFilters.add(new SearchFilter("secondDirectory", SearchFilter.Operator.EQ, queryAsset.getSecondDirectory()));
        }
        if(this.pmOrderRule != null && "1".equals(this.zkbBatch)){
            Set<String> mainDirectorySets = new HashSet<String>();
            Set<String> secondDirectorySets = new HashSet<String>();
            PmOrderRule pmOrderRule = this.pmOrderRuleRepository.findByUid(this.getPmOrderRule().getUid());
            if(StringUtils.isBlank(pmOrderRule.getPerformanceReport())){
                return new ArrayList<>();
            }
            TableDefinition tableDefinition = tableDao.findById(pmOrderRule.getPerformanceReport());
            List<SearchFilter> filter = new ArrayList<>();
            filter.add(new SearchFilter("name", SearchFilter.Operator.EQ, tableDefinition.getName()));
            filter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "0"));
            List<ZkbDirectoryTableDefine> zkbDirectoryTableDefineList = zkbDirectoryRepository.findBySearchFilter(filter);
            zkbDirectoryTableDefineList.stream().forEach(zkbDirectory -> {
                if(StringUtils.isNotBlank(zkbDirectory.getMainDirectory())&&StringUtils.isNotBlank(zkbDirectory.getSecondDirectory())){
                    mainDirectorySets.add(zkbDirectory.getMainDirectory());
                    secondDirectorySets.add(zkbDirectory.getSecondDirectory());
                }
            });
            assetFilters.add(new SearchFilter("mainDirectory", SearchFilter.Operator.IN,mainDirectorySets));
            assetFilters.add(new SearchFilter("secondDirectory", SearchFilter.Operator.IN,secondDirectorySets));
        }
/*        if (null != queryAsset.getManufactureId()) {
            assetFilters.add(new SearchFilter("manufactureId", SearchFilter.Operator.EQ, queryAsset.getManufactureId().toString()));
        }*/
        if (null != queryAsset.getAssetGroup()) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (queryAsset.getEmergencyEquipment()!=null) {
            assetFilters.add(new SearchFilter("emergencyEquipment", SearchFilter.Operator.EQ, queryAsset.getEmergencyEquipment()));
        }
        if (queryAsset.getWarrantyDate() != null) {

            assetFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.EQ, queryAsset.getWarrantyDate()));
        }

        if (clinicalOrgInfo != null){
            Set<String> clinicalUids = orgInfoTreeService.collectChildClinicalDept(clinicalOrgInfo.getUid());
            List<String> results = new ArrayList<>(clinicalUids);
            if(CollectionUtils.isNotEmpty(results)){
                assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,results));
            }
        }

        if (assetDeptOrgInfo != null){
            Set<String> assetDeptUids = orgInfoTreeService.collectChildClinicalDept(assetDeptOrgInfo.getUid());
            List<String> results = new ArrayList<>(assetDeptUids);
            if(CollectionUtils.isNotEmpty(results)){
                assetFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN,results));
            }
        }



/* if (values.length > 1) {
                    filterValue = TimeUtil.fromString(values[1]);
                    if (filterValue != null) {
                        //结束时间是一天的最后一秒  YHAAO-4035
                        Date toDate = TimeUtils.getDateFromStr(TimeUtils.getStrDate(((DateTime) filterValue).toDate(), "yyyyMMdd").concat("235959"), "yyyyMMddHHmmss");
                        searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, toDate));
                    }
                }*/



        if (StringUtils.isNotEmpty(queryAsset.getBrand())) {
            assetFilters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, queryAsset.getBrand()));
        }
        if (!assetFilters.isEmpty() || isYmyg) {
            if (queryAsset.getRuleStatus() != null) {
                uids = new ArrayList<>();
                if (queryAsset.getRuleStatus() == 1) {// 已配置
                    if(StringUtils.isNotEmpty(queryAsset.getQueryLevel())){
                        uids = getAssetUids(user.getTenantUID(),queryAsset.getQueryLevel());
                    }else{
                        uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
                    }
                    assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
                } else if (queryAsset.getRuleStatus() == 2) {// 未配置
                    if(StringUtils.isNotEmpty(queryAsset.getQueryLevel())){
                        uids = getAssetUids(user.getTenantUID(),queryAsset.getQueryLevel());
                    }else{
                        uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
                    }
                    assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN, uids));
                }
            }
            List<SearchFilter> userFilter = getAssetFilter();
            assetFilters.addAll(userFilter);
            addFilterAssetTypeGroup(assetFilters);
            return assetInfoRepository.findBySearchFilter(assetFilters);
        }
        return new ArrayList<>();
    }
    private void addFilterAssetTypeGroup(List<SearchFilter> assetFilters){
        if(user != null && user.isEnableAssetTypeGroup() && CollectionUtils.isNotEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            assetFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, new ArrayList<>(encodedList), "1*"));
        }
    }
    /**
     * 获取所有主目录信息
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory() {
        return zkbFileDirectoryService.findMainDirectory(null);
    }

    public List<String> getAssetUids(String tenantUid,String pmOrderRuleLevel){
        List<String> uids = null;
        if("2".equals(pmOrderRuleLevel)){//二级保养规则
            uids = relRuleAssetRepository.findRuleUid2ByLevelAndTenantUID(tenantUid);
        }else if("3".equals(pmOrderRuleLevel)){
            uids = relRuleAssetRepository.findRuleUid3ByLevelAndTenantUID(tenantUid);
        }else{
            uids = relRuleAssetRepository.findAssetUidsByTenantUID(tenantUid);
        }
        return uids;
    }

    public List<SearchFilter> getAssetFilter() {
        List<SearchFilter> filter = new ArrayList<>();
        if("1".equals(this.zkbBatch)) {
            List<OrgInfo> zkbBatchUserOrgInfo = orgInfoRepository.getSitesByTenantUID(user.getTenantUID());
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, zkbBatchUserOrgInfo.stream().map(OrgInfo::getSiteUID).collect(Collectors.toSet())));
        }else{
            if(CollectionUtils.isNotEmpty(selectedUserOrg)){
                filter.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList())));
            }else{
                filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
                //filter.addAll(userCommonService.getUserLogicFilters(loginUserOrgType, user));
            }
        }
        filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
        return filter;
    }

    public void x1(){
        System.out.println();
    }
    public void x2(){
        System.out.println();
    }
    public void addFilter() {
        List<AssetInfo> assetInfos = getAssetList();

        sourceAssets = assetInfos.stream().map(a -> {
            a.setRemark(a.getName()+","+a.getDepartNum());
            return a;
        }).collect(Collectors.toList());
        targetAssets.forEach((item) -> {
            if (sourceAssets.contains(item)) {
                sourceAssets.remove(item);
            }
        });
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    @Override
    protected RelRuleAssetRepository getDAO() {
        return relRuleAssetRepository;
    }

    public void onAssetsTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                targetAssets.add((AssetInfo) item);
            } else {
                targetAssets.remove((AssetInfo) item);
            }
        }
    }

    public void saveRelFromAssetList() {
        List<RelRuleAsset> rels = new ArrayList<>();
        boolean hasNoPm = false;
        if(assetInfoUids!=null) {
            int levelCount = 0;
            List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(assetInfoUids);
            for (AssetInfo ai : assetInfos) {
                if(ai.getIsPm()==null || ai.getIsPm()==false) {  //不需要配置的pass掉
                    hasNoPm = true;
                    break;
                }
                RelRuleAsset rel = relRuleAssetRepository.findByAssetUID(ai.getUid());
                if (rel == null) rel = new RelRuleAsset();
                rel.setAssetUID(ai.getUid());
                rel.setCreatedDate(new Date());
                rel.setTenantUID(ai.getTenantUID());
                rel.setInstitutionUID(ai.getInstitutionUID());
                rel.setHospitalUID(ai.getHospitalUID());
                rel.setSiteUID(ai.getSiteUID());
                if(pmOrderRule!=null) {  //一级
                    rel.setRuleUID1(pmOrderRule.getUid());
                    levelCount++;
                }
                if(pmOrderRuleTwoLevel!=null) { //二级
                    rel.setRuleUID2(pmOrderRuleTwoLevel.getUid());
                    levelCount++;
                }
                if(pmOrderRuleThreeLevel!=null) {
                    rel.setRuleUID(pmOrderRuleThreeLevel.getUid());
                    levelCount++;
                }
                if(levelCount==0) {
                    WebUtil.addErrorMessage("请至少选择一种保养规则！");
                    return;
                }
                rels.add(rel);
            }
            pmOrderRule = pmOrderRuleTwoLevel = pmOrderRuleThreeLevel = null;
            relRuleAssetRepository.save(rels);
            assetInfoUids = null;
            if(hasNoPm) {
                WebUtil.addSuccessMessage("批量设置成功,不需要保养的设备已经被过滤掉！");
            } else {
                WebUtil.addSuccessMessage("批量设置成功！");
            }

        }
    }

    @Transactional
    public void saveRel() {
        try {
            if (targetAssets.isEmpty()) {
                WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
            } else {
               /* //只有智康保的走这段逻辑
                UserContextService userContextService = WebUtil.getBean(UserContextService.class);
                boolean zkbAccountBoolean = userContextService.isZkbAccount();
                Set<String> directorySets = new HashSet<String>();*/
                /*if (zkbAccountBoolean){
                    PmOrderRule pmOrderRule = this.pmOrderRuleRepository.findByUid(this.getPmOrderRule().getUid());
                    TableDefinition tableDefinition = tableDao.findById(pmOrderRule.getPerformanceReport());
                    List<SearchFilter> filter = new ArrayList<>();
                    filter.add(new SearchFilter("name", SearchFilter.Operator.EQ, tableDefinition.getName()));
                    filter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "0"));
                    List<ZkbDirectoryTableDefine> zkbDirectoryTableDefineList = zkbDirectoryRepository.findBySearchFilter(filter);
                    zkbDirectoryTableDefineList.stream().forEach(zkbDirectory -> {
                        if(StringUtils.isNotBlank(zkbDirectory.getMainDirectory())&&StringUtils.isNotBlank(zkbDirectory.getSecondDirectory())){
                            directorySets.add(zkbDirectory.getMainDirectory()+zkbDirectory.getSecondDirectory());
                        }
                    });
                }*/
                List<RelRuleAsset> rels = new ArrayList<>(targetAssets.size());
                targetAssets = targetAssets.stream().distinct().collect(Collectors.toList());
                for (AssetInfo ai : targetAssets) {
                    //只有智康保的走这段逻辑
                    /*if(zkbAccountBoolean){
                        if(!directorySets.contains(ai.getMainDirectory()+ai.getSecondDirectory())){
                            WebUtil.addErrorMessage(WebUtil.getMessage("设备"+ai.getName()+"不适用当前规则，请检查设备[modality，product]是否匹配"));
                            return;
                        };
                    }*/

                    RelRuleAsset rel = relRuleAssetRepository.findByAssetUID(ai.getUid());
                    if (rel == null) {
                        rel = new RelRuleAsset();
                        rel.setAssetUID(ai.getUid());
                        rel.setCreatedDate(new Date());
                        rel.setTenantUID(ai.getTenantUID());
                        rel.setInstitutionUID(ai.getInstitutionUID());
                        rel.setHospitalUID(ai.getHospitalUID());
                        rel.setSiteUID(ai.getSiteUID());
                    }
                    if (level != null) {
                        if (level == 1) {
                            rel.setRuleUIDInspection(pmOrderRule.getUid());
                            rel.setRuleUID1(pmOrderRule.getUid());
                        } else if (level == 2) {
                            rel.setRuleUID2(pmOrderRule.getUid());
                        } else if (level == 3) {
                            rel.setRuleUID(pmOrderRule.getUid());
                        }else if (level == 4) {
                            String ruleUid4 = rel.getRuleUID4();
                            if(isMultiplePMRuleWithAsset() && !StringUtils.isEmpty(ruleUid4)){
                                if(!ruleUid4.contains(pmOrderRule.getUid())){
                                    rel.setRuleUID4(ruleUid4+"|"+pmOrderRule.getUid());
                                }
                            }else {
                                rel.setRuleUID4(pmOrderRule.getUid());
                            }
                        }
                        rels.add(rel);
                    }
                }
                relRuleAssetRepository.save(rels);
                this.level = null;
                this.ruleUid = null;
                this.pmOrderRule = null;
                WebUtil.addSuccessMessage(WebUtil.getMessage("ConfigurationSuccess"));
            }
        } catch (Exception e) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("FAILED"));
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public List<PmOrderRule> getPmOrderRuleList() {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, queryAsset.getTenantUID()));
        assetFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
            assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
        if (StringUtils.isNotEmpty(queryAsset.getName())) {
            assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
        }
		if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
			assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
		}
/*        if (null != queryAsset.getManufactureId()) {
            assetFilters.add(new SearchFilter("manufactureId", SearchFilter.Operator.EQ, queryAsset.getManufactureId().toString()));
        }*/
        if (queryAsset.getAssetGroup() != null) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (level != null) {
            assetFilters.add(new SearchFilter("level", SearchFilter.Operator.EQ, level));
        }
        return pmOrderRuleRepository.findBySearchFilter(assetFilters);
    }

    public List<PmOrderRule> geLevelTwoRuleList(String name) {
        queryAsset = new QueryAsset();
        level = 2;
        return getRuleList(name);
    }

    public List<PmOrderRule> getLevelThreeRuleList(String name) {
        queryAsset = new QueryAsset();
        level = 3;
        return getRuleList(name);
    }

    public List<PmOrderRule> getRuleList(String name) {
		/*		filter.add(new SearchFilter("level", SearchFilter.Operator.EQ, this.level));
		filter.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));*/
        //List<SearchFilter> filter = userCommonService.getUserLogicFilters(loginUserOrgType,user);
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
        if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
            filter.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
/*		if (StringUtils.isNotEmpty(queryAsset.getName())) {
			filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
		}*/
        if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
            filter.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
        }
        if (queryAsset.getAssetGroup() != null) {
            filter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (level != null) {
            filter.add(new SearchFilter("level", SearchFilter.Operator.EQ, level));
        }
        if (StringUtils.isNotEmpty(name)) {
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name,"1*2+"));
            filter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, name,"1*2+"));
        }
        List<PmOrderRule> rules = pmOrderRuleRepository.findBySearchFilter(filter);
        rules.sort(new Comparator<PmOrderRule>() {
            @Override
            public int compare(PmOrderRule o1, PmOrderRule o2) {
                try {
                    String name1 = PinyinHelper.convertToPinyinString(o1.getName(), "", PinyinFormat.WITHOUT_TONE);
                    String name2 = PinyinHelper.convertToPinyinString(o2.getName(), "", PinyinFormat.WITHOUT_TONE);
                    return name1.compareTo(name2);
                } catch (PinyinException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return rules;
    }

    public void clearPmOrderRule(){
        this.pmOrderRule = null;
        this.ruleUid = null;
    }

    public List<String> getSupplier(String name) {
        return supplierService.getSupplier(name);
    }

    public List<OrgInfo> getClinicalDeptList(String name){
        //UserAccount user = UserContextService.getCurrentUserAccount();
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4,"1*"));
        if(CollectionUtils.isNotEmpty(selectedUserOrg)){
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList()),"1*"));
        }else{
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID(),"1*"));
        }
        if(StringUtils.isNotEmpty(name)){
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name,"1*2+"));
            filter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, name.toLowerCase(),"1*2+"));
        }
        List<OrgInfo> orgInfos = orgInfoRepository.findBySearchFilter(filter);
        orgInfos.sort(new OrgInfoComparator());
        return orgInfos;
    }
    
    public boolean isEnableWarrantyDateSearch(){
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),"enableWarrantyDateSearch");
        return Boolean.FALSE.toString().equalsIgnoreCase(value)?false:true;
    }

    public OrgInfo getClinicalOrgInfo() {
        return clinicalOrgInfo;
    }

    public void setClinicalOrgInfo(OrgInfo clinicalOrgInfo) {
        this.clinicalOrgInfo = clinicalOrgInfo;
    }

    public OrgInfo getAssetDeptOrgInfo() {
        return assetDeptOrgInfo;
    }

    public void setAssetDeptOrgInfo(OrgInfo assetDeptOrgInfo) {
        this.assetDeptOrgInfo = assetDeptOrgInfo;
    }

    public TreeNode[] getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode[] selectedNode) {
        this.selectedNode = selectedNode;
    }

    public List<OrgInfo> getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void setSelectedUserOrg(List<OrgInfo> selectedUserOrg) {
        this.selectedUserOrg = selectedUserOrg;
    }

    public void setRuleId() {

    }

    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public String getRuleUid() {
        return ruleUid;
    }

    public void setRuleUid(String ruleUid) {
        this.ruleUid = ruleUid;
    }

    public PmOrderRule getPmOrderRule() {
        return pmOrderRule;
    }

    public void setPmOrderRule(PmOrderRule pmOrderRule) {
        this.pmOrderRule = pmOrderRule;
    }

    public String getStrDate(Date lastPmDate) {
        return TimeUtils.getStrDate(lastPmDate, "yyyy-MM-dd");
    }

    public String getPmOrderRuleUID() {
        return pmOrderRuleUID;
    }

    public void setPmOrderRuleUID(String pmOrderRuleUID) {
        this.pmOrderRuleUID = pmOrderRuleUID;
    }

    public QueryAsset getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(QueryAsset queryAsset) {
        this.queryAsset = queryAsset;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getSiteUid() {
        return siteUid;
    }

    public void setSiteUid(String siteUid) {
        this.siteUid = siteUid;
    }


    public String getInstallDateFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varInstallDateTo == null) {
            return installDateFormatTime;
        } else {
            return "~" + sdf.format(varInstallDateTo);
        }
    }

    public void setInstallDateFormatTime(String installDateFormatTime) {
        this.installDateFormatTime = installDateFormatTime;
    }

    public Date getVarInstallDateTo() {
        return varInstallDateTo;
    }

    public Date getInstallDateFrom() {
        return installDateFrom;
    }

    public void setInstallDateFrom(Date installDateFrom) {
        this.installDateFrom = installDateFrom;
    }

    public Date getInstallDateTo() {
        return installDateTo;
    }

    public void setInstallDateTo(Date installDateTo) {
        this.installDateTo = installDateTo;
    }

    public Date getStartInstallDate() {
        return startInstallDate;
    }

    public void setStartInstallDate(Date startInstallDate) {
        this.startInstallDate = startInstallDate;
    }

    public Date getEndInstallDate() {
        return endInstallDate;
    }

    public void setEndInstallDate(Date endInstallDate) {
        this.endInstallDate = endInstallDate;
    }

    public String getZkbBatch() {
        return zkbBatch;
    }

    public void setZkbBatch(String zkbBatch) {
        this.zkbBatch = zkbBatch;
    }
}
