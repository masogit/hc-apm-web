package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.AssetRuleModel;
import com.ge.apm.domain.pure.AssetRuleModelList;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.OrgInfoComparator;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class GeneratePmOrdersController extends GenericCRUDUUIDController<RelRuleAsset> {
	public static final Logger LOGGER = LoggerFactory.getLogger(GeneratePmOrdersController.class);
	private static final long serialVersionUID = -1L;
	public static final int RULE_STATUS_CONFIGURED = 1;
	public static final int RULE_STATUS_NOT_CONFIGURED = 2;
	public static final int RULE_STATUS_LEVLE_1 = 1;
	public static final int RULE_STATUS_LEVLE_2 = 2;
	public static final int RULE_STATUS_LEVLE_3 = 3;
	private DualListModel<AssetInfo> availableAssets;
	private List<AssetInfo> sourceAssets;
	private List<AssetInfo> targetAssets;
	private UserAccount user;
	private AssetInfoRepository assetInfoRepository;
	private QueryAsset queryAsset;
	private RelRuleAssetRepository relRuleAssetRepository;
    private ConfigService configService;
	private Integer level;
	private PmOrderRuleRepository pmOrderRuleRepository;
	private OrgInfoRepository orgInfoRepository;
	private PmOrderRequestApiService pmOrderRequestApiService;
	private V2_PmOrderRepository pmOrderRepository;
	private String siteUid;
	private Date planTime;
	private Integer pmType;
	@Getter @Setter
	private PmOrderRule pmOrderRule;
	@Getter @Setter
	private Boolean isAllRuleSelected = Boolean.TRUE;
	@Getter @Setter
	private Boolean isAllAssetSelected = Boolean.FALSE;
	//复选的设备
	@Getter @Setter
	private Map<String, Boolean> selectedMap;
	@Getter @Setter
	private Map<String, Boolean> selectedRuleMap;
	private SupplierService supplierService;

    private TenantInfo selectedTenant;
    private List<OrgInfo> selectedUserOrg;
    private OrgInfo selectedOrg;
    private TreeNode orgTree;
    private TreeNode[] selectedNode;
    private TenantInfoRepository tenantInfoRepository;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;
    private OrgInfo clinicalOrgInfo;
    private List<AssetInfo> assetInfoList = new ArrayList<>();
    private Integer total = 0;
    private Integer assetRuleModelTotal = 0;
	private List<AssetTypeGroup> assetTypeGroupList;
	private boolean isYmyg = false;
	private OrgInfo userOrgInfo;

	private ConfigItemValueRepository configItemValueRepository;

	private List<AssetRuleModel> onlyOneModels = new ArrayList<>();//只存在唯一一个质控绑定规则的记录默认批量创建
	private List<AssetRuleModel> notOnlyOneModels = new ArrayList<>();//存在多个绑定规则的列表（需用户选择是否批量创建）
	private List<AssetRuleModel> selectedNotOnlyOneModels = new ArrayList<>();//弹出框存在多个质控规则中选定的记录
	private List<AssetRuleModel> batchCreateModels = new ArrayList<>();//最终批量创建列表

    @Override
	protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
		relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		pmOrderRequestApiService = WebUtil.getBean(PmOrderRequestApiService.class);
		pmOrderRepository =  WebUtil.getBean(V2_PmOrderRepository.class);
		supplierService = WebUtil.getBean(SupplierService.class);
        configService = WebUtil.getBean(ConfigService.class);
		configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);targetAssets = new ArrayList<>();
		queryAsset = new QueryAsset();
		queryAsset.setTenantUID(user.getTenantUID());
		queryAsset.setRuleStatus(1);// 默认只显示已配置过规则的资产
		siteUid = user.getSiteUID();
		selectedMap = new HashMap<>();
		selectedRuleMap = new HashMap<>();
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
		selectedUserOrg = new ArrayList<>();
		userOrgInfo = orgInfoRepository.getByUid(user.getSiteUID());
		selectedUserOrg.add(userOrgInfo);
		isYmyg = UserContextService.isYiMaiYangGuangTenantForServer();
		initOrgTree();
		assetTypeGroupList = user.getAssetTypeGroupList();
	}

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }

	public void initOrgTree() {
		UserAccount loginUser = UserContextService.getCurrentUserAccount();
		TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
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

	public void onSelectOrgForUser(NodeSelectEvent event) {
		TreeNode node = event.getTreeNode();
		if (!isYmyg) {
			selectedUserOrg.clear();
		} else {
			setTreeNodes(node, true);
		}
		selectedUserOrg.add((OrgInfo) node.getData());
	}

	public void onUnSelectOrgForUser(NodeUnselectEvent event) {
		TreeNode node = event.getTreeNode();
		setTreeNodes(node, false);
		selectedUserOrg.remove((OrgInfo)node.getData());
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

    public String getOrgFullName(List<OrgInfo> org) {
		StringBuilder sb = new StringBuilder();
		if (CollectionUtils.isNotEmpty(org)) {
			org.forEach(val -> sb.append(val.getName() + ","));
			String name = sb.toString();
			return name.substring(0, name.length()-1);
		}
		return null;
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

	public void onSelectAllOfPage() {
		boolean isSelectedAll = !assetInfoList.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
		for (AssetInfo item : assetInfoList) {
			selectedMap.put(item.getUid(), !isSelectedAll);
		}
        prepareAssetRuleList();
	}
	public void onSelectRuleAllOfPage() {
        boolean b = notOnlyOneModels.stream().anyMatch(e -> !selectedRuleMap.get(e.getUniqueUid()));
			notOnlyOneModels.stream().forEach(e -> {
                selectedRuleMap.put(e.getUniqueUid(), b);
            });
			if(b){
			    isAllRuleSelected = true;
            }else {
			    isAllRuleSelected = false;
            }
    }

	public void setRuleId() {}

	public List<PmOrderRule> getRuleList(String name) {
		List<SearchFilter> filter = new ArrayList<>();
		filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
		if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
			filter.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
		}
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
			filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name));
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

	public List<AssetInfo> getAssetList() {
		List<SearchFilter> assetFilters = new ArrayList<>();
		if (user.isEnableAssetTypeGroup()) {
			if(ObjectUtil.isEmpty(assetTypeGroupList)){
				return new ArrayList<>();
			}else{
				List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
				assetFilters.add(new SearchFilter("assetType", SearchFilter.Operator.IN, new ArrayList<>(encodedList)));
			}
		}
		if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
			assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
		}
		if (null != queryAsset.getRiskGrade()) {
			assetFilters.add(new SearchFilter("riskGrade", SearchFilter.Operator.EQ, queryAsset.getRiskGrade()));
		}
		if (StringUtils.isNotEmpty(queryAsset.getName())) {
			assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
		}
		if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
			assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
		}
		if (null != queryAsset.getAssetGroup()) {
			assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
		}
        if (clinicalOrgInfo != null){
            Set<String> clinicalUids = orgInfoTreeService.collectChildClinicalDept(clinicalOrgInfo.getUid());
            List<String> results = new ArrayList<>(clinicalUids);
            if(CollectionUtils.isNotEmpty(results)){
                assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN,results));
            }
        }
		if (StringUtils.isNotEmpty(queryAsset.getBrand())) {
			assetFilters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, queryAsset.getBrand()));
		}
		if (queryAsset.getWarrantyDate() != null) {

			assetFilters.add(new SearchFilter("warrantyDate", SearchFilter.Operator.EQ, queryAsset.getWarrantyDate()));
		}
		if(level != null) {
			List<String> uids = null;
			if(level == 1) {
				uids = pmOrderRule==null ? relRuleAssetRepository.findRuleUid1ByLevelAndTenantUID(user.getTenantUID()) : relRuleAssetRepository.findRuleUid1ByLevelAndTenantUID(user.getTenantUID(),pmOrderRule.getUid());
				uids = uids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}else if(level == 2) {
				uids = pmOrderRule==null ? relRuleAssetRepository.findRuleUid2ByLevelAndTenantUID(user.getTenantUID()) : relRuleAssetRepository.findRuleUid2ByLevelAndTenantUID(user.getTenantUID(),pmOrderRule.getUid());
				uids = uids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}else if(level == 3) {
				uids = pmOrderRule==null ? relRuleAssetRepository.findRuleUid3ByLevelAndTenantUID(user.getTenantUID()) : relRuleAssetRepository.findRuleUid3ByLevelAndTenantUID(user.getTenantUID(),pmOrderRule.getUid());
				uids = uids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}else if(level == 4){
				List<SearchFilter> searchFilters=new ArrayList<>();
				searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,user.getTenantUID()));
				if(pmOrderRule==null) {
					searchFilters.add(new SearchFilter("ruleUID4", SearchFilter.Operator.IsNotNull,null));
				} else {
					searchFilters.add(new SearchFilter("ruleUID4", SearchFilter.Operator.LIKE,pmOrderRule.getUid()));
				}
				List<RelRuleAsset> bySearchFilter = relRuleAssetRepository.findBySearchFilter(searchFilters);
				uids = bySearchFilter.stream().map(RelRuleAsset::getAssetUID).collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}
		}else{
			List<String> uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
			uids = uids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
			assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
		}
		if (!assetFilters.isEmpty()) {
			List<SearchFilter> userFilter = getAssetFilter();
			assetFilters.addAll(userFilter);
			return assetInfoRepository.findBySearchFilter(assetFilters);
		}
		return new ArrayList<>();
	}

	public List<SearchFilter> getAssetFilter() {
        List<SearchFilter> filter = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(selectedUserOrg)){
			if (!isYmyg) {
				filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList()),"1*2+"));
				filter.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList()),"1*2+"));
				filter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList()),"1*2+"));
				filter.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList()),"1*2+"));
			} else {
				filter.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, selectedUserOrg.stream().map(OrgInfo::getSiteUID).collect(Collectors.toList()),"1*"));
			}
		}else{
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID(),"1*"));
        }
		filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
		List<AssetTypeGroup> assetTypeGroupList = null;
		if(user != null && user.isEnableAssetTypeGroup() && !CollectionUtils.isEmpty(assetTypeGroupList=user.getAssetTypeGroupList())) {
			List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
			filter.add(new SearchFilter("assetType", SearchFilter.Operator.IN, new ArrayList<>(encodedList), "1*"));
		}
		return filter;
	}

	public void addFilter(){
		isAllAssetSelected = Boolean.TRUE;
		assetInfoList.clear();
		assetInfoList = getAssetList();
		selectedMap = new HashMap<>();
		for(AssetInfo assetInfo : assetInfoList) {
			selectedMap.put(assetInfo.getUid(),true);
		}
		filterAssetInfoList();
		total = assetInfoList.size();
		prepareAssetRuleList();
	}
	private void filterAssetInfoList(){
//		TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
//		if(tempTenantInfo != null && "3".equals(tempTenantInfo.getCustomTenant())){
			boolean generate = queryAsset.getGenerate();  //true:已生成过/false:为生成过
			assetInfoList=assetInfoList.stream()
					.filter(ai->{
						Long countByAssetId;
						if(level != null){
							countByAssetId= pmOrderRepository.findCountByAssetId(ai.getId(), level, pmType);
						}else{
							countByAssetId= pmOrderRepository.findCountByAssetIdAndPmType(ai.getId(), pmType);
						}
						return generate? countByAssetId > 0:countByAssetId == 0;
					})
					.collect(Collectors.toList());
//		}
	}
	public String findCustomTenant(){
		String customTenant="";
		TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
		if(tempTenantInfo != null){
			customTenant=tempTenantInfo.getCustomTenant();
		}
		return customTenant;
	}
	public void checkGenerate(){
		// 页面 Generate 属性，清空数据列表
	  if(assetInfoList.size() > 0) {
		  assetInfoList.clear();
		  this.total=0;
	  }
	}
	public void addSelected(){

	}

    public List<OrgInfo> getClinicalDeptList(String name){
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

	public void generatePmOrdersForMuliPMRule4() {
		List<AssetRuleModel> models;
		selectedNotOnlyOneModels = notOnlyOneModels.stream().filter(e->selectedRuleMap.get(e.getUniqueUid())).collect(Collectors.toList());
		batchCreateModels.addAll(selectedNotOnlyOneModels);
		batchCreateModels.addAll(onlyOneModels);
		models = batchCreateModels;
		models.forEach(m -> {
			if (m.getPlanTime() == null) {
				m.setPlanTime(new Date());
			}
		});
		AssetRuleModelList model = new AssetRuleModelList();
		model.setModels(models);
		String token = UserContextService.getAccessToken();
		String request = JSON.toJSONString(model);
		LOGGER.info("token is {}",token);
		LOGGER.info("list is {}",request);
		long start = System.currentTimeMillis();
		try{
			pmOrderRequestApiService.createPmOrdersByBatch(token, request);
			long end = System.currentTimeMillis();
			LOGGER.info("批量生成保养工单结束，耗时 : {} 秒 !",(end - start)/1000);
			WebUtil.addSuccessMessage("提交完成，工单生成中，请稍后在工作台中查看！");
		} catch(Exception e){
			e.printStackTrace();
			LOGGER.error("工单生成失败，msg is {}",e.getMessage());
		}

	}

	public boolean prepareGenPmOrders(){
		UserAccount userAccount = UserContextService.getCurrentUserAccount();
		WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
		if(workflowConfig != null && workflowConfig.getPmRuleLevelFour()&& workflowConfig.getPmFourthRuleLevelForAsset() && level != null && level == 4) {
			return true;
		}else {
			return false;
		}
	}

	public void prepareAssetRuleModelList(RelRuleAsset e,Integer level,Map<String, V2_PmOrder> pmOrderMap){
		String ruleUID = null;
			if(level == 1){
				 ruleUID = e.getRuleUID1();
			}else if(level == 2){
				ruleUID = e.getRuleUID2();
			}else if(level == 3){
				ruleUID = e.getRuleUID();
			}else if(level == 4){
				ruleUID = e.getRuleUID4();
			}
		String[] split = StringUtils.split(ruleUID, "|");
		List<String> ruleUids = Arrays.asList(split);
		List<PmOrderRule> orderRules = pmOrderRuleRepository.findByUidIn(ruleUids);
		Map<String, PmOrderRule> ruleNameMap = orderRules.stream().collect(Collectors.toMap(PmOrderRule::getUid, v->v,(o1,o2)->o2));
		if(!StringUtils.isEmpty(ruleUID)&&ruleUID.contains("|")){
				ruleUids.stream().forEach(item->{
				    if(pmOrderMap.get(e.getAssetUID()+level+pmType+item) == null) {
                        AssetRuleModel model = new AssetRuleModel();
                        model.setPmType(pmType);
                        model.setUid(e.getAssetUID());
                        model.setLevel(level);
                        model.setPlanTime(planTime);
                        model.setPmRuleUid(item);
						PmOrderRule rule = ruleNameMap.get(item);
						if (ObjectUtil.isNotEmpty(rule)) {
							model.setAssetName(e.getAssetName());
							model.setUniqueUid(e.getAssetUID() + item);
							notOnlyOneModels.add(model);
						}
                    }
					});
				}else{
			if(pmOrderMap.get(e.getAssetUID()+level+pmType+ruleUID) == null) {
				AssetRuleModel model = new AssetRuleModel();
				model.setPmType(pmType);
				model.setUid(e.getAssetUID());
				model.setLevel(level);
				model.setPlanTime(planTime);
				model.setPmRuleUid(ruleUID);
				model.setPmRuleName(ruleNameMap.get(ruleUID).getName());
				onlyOneModels.add(model);
				}
			}
	}

	public String mulitPmOrderList(){
		if(prepareGenPmOrders()){
			return "visibility:visible";
		}else {
			return "visibility:hidden";
		}
	}

	public void prepareAssetRuleList(){
        clearModels();//清除缓存
        if(prepareGenPmOrders()){
            Set selectedAssetUid = selectedMap.keySet();
            List<AssetInfo> targetAssets = assetInfoList.stream().filter(assetInfo -> selectedAssetUid.contains(assetInfo.getUid()) && selectedMap.get(assetInfo.getUid())).distinct().collect(Collectors.toList());
            Map<String, AssetInfo> assetInfoMap = targetAssets.stream().collect(Collectors.toMap(AssetInfo::getUid, v -> v, (v1, v2) -> v2));
            if(CollectionUtils.isNotEmpty(assetInfoMap.entrySet())){

                List<RelRuleAsset> ruleAssets = relRuleAssetRepository.findByAssetUids1(targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toSet()));
                List<String> assetUids = ruleAssets.stream().map(RelRuleAsset::getAssetUID).collect(Collectors.toList());
                List<V2_PmOrder> pmOrders = pmOrderRepository.findByAssets(assetUids);
                Map<String, V2_PmOrder> pmOrderMap = pmOrders.stream().collect(Collectors.toMap(e -> e.getAssetUid() + e.getRuleLevel() +e.getPmType()+ e.getPmRuleUid(), v -> v, (v1, v2) -> v2));
                if(CollectionUtils.isNotEmpty(ruleAssets)) {
                    ruleAssets.stream().forEach(
                            e -> {
                                e.setAssetName(assetInfoMap.get(e.getAssetUID()).getName());
                                prepareAssetRuleModelList(e, level,pmOrderMap);
                            }
                    );
                }else {
                    clearModels();
                }
            }else {
                clearModels();
            }
            if(!CollectionUtils.isEmpty(notOnlyOneModels)){

                notOnlyOneModels.stream().forEach(e->{
                    selectedRuleMap.put(e.getUniqueUid(),true);
                });
                isAllRuleSelected = true;
            }else {
                isAllRuleSelected = false;
            }
        }
        assetRuleModelTotal = notOnlyOneModels.size();
    }

	public void generatePmOrders() {
		if(prepareGenPmOrders()){
            generatePmOrdersForMuliPMRule4();
		}else{
			generatePmOrders1();
		}
	}
	public void generatePmOrders1() {
		if (level == null) {
			WebUtil.addErrorMessage("请选择保养等级再生成保养工单！");
			return;
		}
		if (CollectionUtils.isEmpty(assetInfoList)) {
			WebUtil.addErrorMessage(WebUtil.getMessage("AssetsIsBlank"));
			return;
		}
		Set selectedAssetUid = selectedMap.keySet();
		if(CollectionUtils.isEmpty(selectedAssetUid)) {
			WebUtil.addErrorMessage("请勾选设备！");
		}
		List<AssetInfo> targetAssets = assetInfoList.stream().filter(assetInfo -> selectedAssetUid.contains(assetInfo.getUid()) && selectedMap.get(assetInfo.getUid())).distinct().collect(Collectors.toList());
		List<AssetRuleModel> models = new ArrayList<>();
		if(targetAssets.size() == 1) {
			AssetInfo ai = targetAssets.get(0);
			if(ai.getClinicalDeptId() == null) {
				WebUtil.addErrorMessage("没有使用科室，无法生成保养工单！");
				return;
			}
			PmOrderRule rule = null;
			if (level == RULE_STATUS_LEVLE_1) {
				rule = pmOrderRuleRepository.findPmOrderRuleLevel1(ai.getUid());
			} else if (level == RULE_STATUS_LEVLE_2) {
				rule = pmOrderRuleRepository.findPmOrderRuleLevel2(ai.getUid());
			} else if (level == RULE_STATUS_LEVLE_3) {
				rule = pmOrderRuleRepository.findPmOrderRuleLevel3(ai.getUid());
			}else if(level == 4){
				rule = pmOrderRuleRepository.findPmOrderRuleLevel4(ai.getUid());
			}
			if (rule == null) {
				WebUtil.addErrorMessage("找不到该设备该级别的保养规则！");
				return;
			}
			Integer period = rule.getPeriod();
			if(period == null) {
				period = 1;
			}
			boolean isExists = pmOrderRepository.findOpenPm(ai.getId(),level,pmType);
			if(isExists) {
				WebUtil.addErrorMessage("保养工单已存在！");
				return;
			}
			AssetRuleModel model = new AssetRuleModel();
			if(pmType == 1){
				model.setPeriod(period);
			}
			model.setPmType(pmType);
			model.setUid(ai.getUid());
			model.setLevel(level);
			model.setPlanTime(planTime == null ? new Date():planTime);
			model.setPmRuleUid(rule.getUid());
			model.setPmRuleName(rule.getName());
			model.setPeriod(rule.getPeriod());
			model.setPeriodUnit(rule.getPeriodUnit());
            model.setRuleScore(rule.getRuleScore());
            models.add(model);
		}else {
			for (AssetInfo ai : targetAssets) {
				if(ai.getClinicalDeptId() == null) {
					continue;
				}
				PmOrderRule rule = null;
				if (level == RULE_STATUS_LEVLE_1) {
					rule = pmOrderRuleRepository.findPmOrderRuleLevel1(ai.getUid());
				} else if (level == RULE_STATUS_LEVLE_2) {
					rule = pmOrderRuleRepository.findPmOrderRuleLevel2(ai.getUid());
				} else if (level == RULE_STATUS_LEVLE_3) {
					rule = pmOrderRuleRepository.findPmOrderRuleLevel3(ai.getUid());
				}else if(level == 4){
					rule = pmOrderRuleRepository.findPmOrderRuleLevel4(ai.getUid());
				}
				if (rule == null) {
					LOGGER.error("not find pmOrderRule by assetUid : {},level : {}", ai.getUid(), level);
					continue;
				}
				boolean isExists = pmOrderRepository.findOpenPm(ai.getId(),level,pmType);
				if(isExists) {
					LOGGER.warn("open pm exists !");
					continue;
				}
				int period = rule.getPeriod();
				AssetRuleModel model = new AssetRuleModel();
				if(pmType == 1){
					model.setPeriod(period);
				}
				model.setPmType(pmType);
				model.setUid(ai.getUid());
				model.setLevel(level);
				model.setPlanTime(planTime == null ? new Date():planTime);
				model.setPmRuleUid(rule.getUid());
                model.setRuleScore(rule.getRuleScore());
				models.add(model);
			}
			if (CollectionUtils.isEmpty(models)) {
				WebUtil.addErrorMessage("已经存在同级别同类型的保养工单！");
				LOGGER.warn("no assets need to generate pm orders !");
				return;
			}
		}
		AssetRuleModelList model = new AssetRuleModelList();
		model.setModels(models);
		String token = UserContextService.getAccessToken();
		String request = JSON.toJSONString(model);
		LOGGER.info("token is {}",token);
		LOGGER.info("list is {}",request);
		long start = System.currentTimeMillis();
		try{
			pmOrderRequestApiService.createPmOrdersByBatch(token, request);
			long end = System.currentTimeMillis();
			LOGGER.info("批量生成保养工单结束，耗时 : {} 秒 !",(end - start)/1000);
			WebUtil.addSuccessMessage("提交完成，工单生成中，请稍后在工作台中查看！");
			//WebUtil.addSuccessMessage("工单生成成功！");
		} catch(Exception e){
			//WebUtil.addErrorMessage("工单生成失败！");
			e.printStackTrace();
			LOGGER.error("工单生成失败，msg is {}",e.getMessage());
		}

	}

	public void updateCheckBox(){
        isAllRuleSelected = selectedRuleMap.values().stream().allMatch(e -> e.equals(true));
    }


	public void clearModels(){
		onlyOneModels.clear();
		notOnlyOneModels.clear();
		batchCreateModels.clear();
		selectedNotOnlyOneModels.clear();
	}

	public void updatePMPlanTime(){
	    notOnlyOneModels.stream().forEach(e->{
	        e.setPlanTime(planTime);
        });
	    onlyOneModels.stream().forEach(e->{
	        e.setPlanTime(planTime);
        });

    }
	public void updatePMType(){
	    notOnlyOneModels.stream().forEach(e->{
	        e.setPmType(pmType);
        });
	    onlyOneModels.stream().forEach(e->{
            e.setPmType(pmType);
        });

    }

	public boolean isEnableWarrantyDateSearch(){
		String value = configItemValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),"enableWarrantyDateSearch");
		return Boolean.FALSE.toString().equalsIgnoreCase(value)?false:true;
	}

	public String getName(AssetInfo assetInfo){
		return StringUtils.isEmpty(assetInfo.getName()) ? "  " : assetInfo.getName();
	}

	public String getDepartNum(AssetInfo assetInfo){
		return StringUtils.isEmpty(assetInfo.getDepartNum()) ? "  " : assetInfo.getDepartNum();
	}

	public String getClinicalDeptName(AssetInfo assetInfo){
		return StringUtils.isEmpty(assetInfo.getClinicalDeptName()) ? "  " : assetInfo.getClinicalDeptName();
	}

	public String getFunctionType(AssetInfo assetInfo) {
		return StringUtils.isEmpty(assetInfo.getFunctionType()) ? "  " : assetInfo.getFunctionType();
	}

	public String getSeriaNum(AssetInfo assetInfo) {
		return StringUtils.isEmpty(assetInfo.getSerialNum()) ? "  " : assetInfo.getSerialNum();
	}

	public String getStrDate(Date lastPmDate) {
		return TimeUtils.getStrDate(lastPmDate, "yyyy-MM-dd");
	}

	public QueryAsset getQueryAsset() {
		return queryAsset;
	}

	public List<String> getSupplier(String name) {
		return supplierService.getSupplier(name);
	}

	public OrgInfo getClinicalOrgInfo() {
		return clinicalOrgInfo;
	}

	public void setClinicalOrgInfo(OrgInfo clinicalOrgInfo) {
		this.clinicalOrgInfo = clinicalOrgInfo;
	}

	public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(TenantInfo selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public List<OrgInfo> getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void setSelectedUserOrg(List<OrgInfo> selectedUserOrg) {
        this.selectedUserOrg = selectedUserOrg;
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

    public TreeNode[] getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode[] selectedNode) {
        this.selectedNode = selectedNode;
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

	public Date getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}

	public Integer getPmType() {
		return pmType;
	}

	public void setPmType(Integer pmType) {
		this.pmType = pmType;
	}

	public List<AssetInfo> getAssetInfoList() {
		return assetInfoList;
	}

	public void setAssetInfoList(List<AssetInfo> assetInfoList) {
		this.assetInfoList = assetInfoList;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<AssetRuleModel> getOnlyOneModels() {
		return onlyOneModels;
	}

	public void setOnlyOneModels(List<AssetRuleModel> onlyOneModels) {
		this.onlyOneModels = onlyOneModels;
	}

	public List<AssetRuleModel> getNotOnlyOneModels() {
		return notOnlyOneModels;
	}

	public void setNotOnlyOneModels(List<AssetRuleModel> notOnlyOneModels) {
		this.notOnlyOneModels = notOnlyOneModels;
	}

	public List<AssetRuleModel> getBatchCreateModels() {
		return batchCreateModels;
	}

	public void setBatchCreateModels(List<AssetRuleModel> batchCreateModels) {
		this.batchCreateModels = batchCreateModels;
	}

    public Integer getAssetRuleModelTotal() {
        return assetRuleModelTotal;
    }

    public void setAssetRuleModelTotal(Integer assetRuleModelTotal) {
        this.assetRuleModelTotal = assetRuleModelTotal;
    }
}
