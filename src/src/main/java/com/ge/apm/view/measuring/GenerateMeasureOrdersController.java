package com.ge.apm.view.measuring;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.AssetRuleModel;
import com.ge.apm.domain.pure.AssetRuleModelList;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.OrgInfoComparator;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class GenerateMeasureOrdersController extends GenericCRUDUUIDController<RelMeasureRule> {
	public static final Logger LOGGER = LoggerFactory.getLogger(GenerateMeasureOrdersController.class);
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
	private RelMeasureRuleRepository relMeasureRuleRepository;
	private Integer level;
	private MeasureOrderRuleRepository measureOrderRuleRepository;
	private OrgInfoRepository orgInfoRepository;
	private PmOrderRequestApiService pmOrderRequestApiService;
	private V2_PmOrderRepository pmOrderRepository;
	private String siteUid;
	private Date planTime;
	private Integer pmType;//此处为measureType
	private Boolean isInsulate;
	private Integer loginUserOrgType;
	private UserCommonService userCommonService;
	//private SupplierRepository supplierDao;
	private SupplierService supplierService;

    private TenantInfo selectedTenant;
    private OrgInfo selectedUserOrg;
    private OrgInfo selectedOrg;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private TenantInfoRepository tenantInfoRepository;
    private UaaService uaaService;
    private OrgInfoTreeService orgInfoTreeService;
    private OrgInfo clinicalOrgInfo;
    private List<AssetInfo> assetInfoList = new ArrayList<>();
    private Integer total = 0;
    private V2_MeasureRepository measureRepository;


    @Override
	protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
		relMeasureRuleRepository = WebUtil.getBean(RelMeasureRuleRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		measureOrderRuleRepository = WebUtil.getBean(MeasureOrderRuleRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		pmOrderRequestApiService = WebUtil.getBean(PmOrderRequestApiService.class);
		pmOrderRepository =  WebUtil.getBean(V2_PmOrderRepository.class);
		supplierService = WebUtil.getBean(SupplierService.class);
		targetAssets = new ArrayList<AssetInfo>();
		queryAsset = new QueryAsset();
		queryAsset.setTenantUID(user.getTenantUID());
		queryAsset.setRuleStatus(1);// 默认只显示已配置过规则的资产
		siteUid = user.getSiteUID();
		//initAssets();
        userCommonService = WebUtil.getBean(UserCommonService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
		measureRepository = WebUtil.getBean(V2_MeasureRepository.class);
		isInsulate = UserContextService.isInsulate();
		loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        selectedUserOrg = orgInfoRepository.getByUid(user.getSiteUID());
        //initTree(user.getTenantUID());
		initOrgTree();
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
        selectedUserOrg = (OrgInfo) node.getData();
        //addFilter();
    }

    private void initTree(String tenantUid) {
        TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
		selectedTenant = tempTenantInfo;
		orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
/*        if(UserContextService.isInsulate()){
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

    public String getOrgFullName(OrgInfo org) {
        if(null == org || org.getId() == -1) {
            return null;
        }
        String name = org.getName();
        return name;
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

	public List<AssetInfo> getAssetList() {
		List<SearchFilter> assetFilters = new ArrayList<>();
		if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
			assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
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
/*		if(level != null) {
			List<String> uids = null;
			if(level == 1) {
				uids = relRuleAssetRepository.findRuleUid1ByLevelAndTenantUID(user.getTenantUID());
				uids = uids.stream().filter(uid -> uid != null).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}else if(level == 2) {
				uids = relRuleAssetRepository.findRuleUid2ByLevelAndTenantUID(user.getTenantUID());
				uids = uids.stream().filter(uid -> uid != null).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}else if(level == 3) {
				uids = relRuleAssetRepository.findRuleUid3ByLevelAndTenantUID(user.getTenantUID());
				uids = uids.stream().filter(uid -> uid != null).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
			}
		}*/
		if (!assetFilters.isEmpty()) {
			List<SearchFilter> userFilter = getAssetFilter();
			assetFilters.addAll(userFilter);
			return assetInfoRepository.findBySearchFilter(assetFilters);
		}
		return new ArrayList<>();
	}

	public List<SearchFilter> getAssetFilter() {
        List<SearchFilter> filter = new ArrayList<>();
        if(selectedUserOrg != null){
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedUserOrg.getSiteUID(),"1*"));
        }else{
            filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID(),"1*"));
        }
/*        if(selectedUserOrg != null){
            if(selectedUserOrg.getOrgType() == 0){
                filter.addAll(userCommonService.getSelectedOrgFilters(selectedUserOrg));
            }
        }else{
            filter.addAll(userCommonService.getUserLogicFilters(loginUserOrgType, user));
        }*/
		filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
		return filter;
	}

/*	public void addFilter() {
		sourceAssets = getAssetList();
		targetAssets.forEach((item) -> {
			if (sourceAssets.contains(item)) {
				sourceAssets.remove(item);
			}
		});
		availableAssets = new DualListModel<>(sourceAssets, targetAssets);
	}*/

	public void addFilter(){
		assetInfoList.clear();
		assetInfoList = getAssetList();
		total = assetInfoList.size();
	}

    public List<OrgInfo> getClinicalDeptList(String name){
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4,"1*"));
		if(selectedUserOrg != null){
			filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedUserOrg.getSiteUID(),"1*"));
		}else{
			filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID(),"1*"));
		}
        //filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID(),"1*"));
        if(StringUtils.isNotEmpty(name)){
            filter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, name,"1*2+"));
            filter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, name.toLowerCase(),"1*2+"));
        }
        List<OrgInfo> orgInfos = orgInfoRepository.findBySearchFilter(filter);
        orgInfos.sort(new OrgInfoComparator());
        return orgInfos;
    }

	@Override
	protected RelMeasureRuleRepository getDAO() {
		return relMeasureRuleRepository;
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

	public void generatePmOrders() {
		if (CollectionUtils.isEmpty(assetInfoList)) {
			WebUtil.addErrorMessage(WebUtil.getMessage("AssetsIsBlank"));
			return;
		}
		List<AssetRuleModel> models = new ArrayList<>();
		List<AssetInfo> targetAssets = assetInfoList.stream().distinct().collect(Collectors.toList());
		if(targetAssets.size() == 1) {
			AssetInfo ai = targetAssets.get(0);
			if(ai.getClinicalDeptId() == null) {
				WebUtil.addErrorMessage("没有使用科室，无法生成计量工单！");
				return;
			}
			MeasureOrderRule rule = measureOrderRuleRepository.findMeasureOrderRuleByAssetUid(ai.getUid());
/*			if (level == RULE_STATUS_LEVLE_1) {
				rule = pmOrderRuleRepository.findPmOrderRuleLevel1(ai.getUid());
			} else if (level == RULE_STATUS_LEVLE_2) {
				rule = pmOrderRuleRepository.findPmOrderRuleLevel2(ai.getUid());
			} else if (level == RULE_STATUS_LEVLE_3) {
				rule = pmOrderRuleRepository.findPmOrderRuleLevel3(ai.getUid());
			}*/
			if (rule == null) {
				WebUtil.addErrorMessage("找不到该设备的计量规则！");
				return;
			}
			Integer period = rule.getPeriod();
			if(period == null) {
				period = 1;
			}
			//boolean isExists = pmOrderRepository.findOpenPm(ai.getId(),level,pmType);
			boolean isExists = measureRepository.findOpenPm(ai.getId(),pmType);
			if(isExists) {
				WebUtil.addErrorMessage("计量工单已存在！");
				return;
			}
			AssetRuleModel model = new AssetRuleModel();
			if(pmType == 1){
				model.setPeriod(period);
			}
			model.setMeasuringType(pmType);
			model.setUid(ai.getUid());
			model.setLevel(level);
			model.setPlanTime(planTime);
			models.add(model);
		}else {
			for (AssetInfo ai : targetAssets) {
				if(ai.getClinicalDeptId() == null) {
					continue;
				}
				MeasureOrderRule rule = measureOrderRuleRepository.findMeasureOrderRuleByAssetUid(ai.getUid());
				/*if (level == RULE_STATUS_LEVLE_1) {
					rule = pmOrderRuleRepository.findPmOrderRuleLevel1(ai.getUid());
				} else if (level == RULE_STATUS_LEVLE_2) {
					rule = pmOrderRuleRepository.findPmOrderRuleLevel2(ai.getUid());
				} else if (level == RULE_STATUS_LEVLE_3) {
					rule = pmOrderRuleRepository.findPmOrderRuleLevel3(ai.getUid());
				}*/
				if (rule == null) {
					LOGGER.error("not find measureOrderRule by assetUid : {},level : {}", ai.getUid(), level);
					continue;
				}
				boolean isExists = measureRepository.findOpenPm(ai.getId(),pmType);
				if(isExists) {
					LOGGER.warn("open measure order exists !");
					continue;
				}
				int period = rule.getPeriod();
				AssetRuleModel model = new AssetRuleModel();
				if(pmType == 1){
					model.setPeriod(period);
				}
				model.setMeasuringType(pmType);
				model.setUid(ai.getUid());
				model.setLevel(level);
				model.setPlanTime(planTime);
				models.add(model);
			}
			if (CollectionUtils.isEmpty(models)) {
				WebUtil.addErrorMessage("已经存在同级别同类型的计量工单！");
				LOGGER.warn("no assets need to generate measure orders !");
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
			pmOrderRequestApiService.createMeasureOrdersByBatch(token, request);
			long end = System.currentTimeMillis();
			LOGGER.info("批量生成计量工单结束，耗时 : {} 秒 !",(end - start)/1000);
			WebUtil.addSuccessMessage("提交完成，工单生成中，请稍后在工作台中查看！");
			//WebUtil.addSuccessMessage("工单生成成功！");
		} catch(Exception e){
			//WebUtil.addErrorMessage("工单生成失败！");
			e.printStackTrace();
			LOGGER.error("工单生成失败，msg is {}",e.getMessage());
		}

	}

	public void removeItem(String uid){
		assetInfoList = assetInfoList.stream().filter( assetInfo -> !assetInfo.getUid().equals(uid)).collect(Collectors.toList());
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

    public OrgInfo getSelectedUserOrg() {
        return selectedUserOrg;
    }

    public void setSelectedUserOrg(OrgInfo selectedUserOrg) {
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

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
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
}
