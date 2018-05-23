package com.ge.apm.view.pm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.RelRuleAssetRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.QueryAsset;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.pure.AssetRuleModel;
import com.ge.apm.domain.pure.AssetRuleModelList;
import com.ge.apm.service.pm.PmOrderRequestApiService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

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
	private Integer level;
	private PmOrderRuleRepository pmOrderRuleRepository;
	private OrgInfoRepository orgInfoRepository;
	private PmOrderRequestApiService pmOrderRequestApiService;
	private V2_PmOrderRepository pmOrderRepository;
	private String siteUid;
	private Date planTime;

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
		targetAssets = new ArrayList<AssetInfo>();
		queryAsset = new QueryAsset();
		queryAsset.setTenantUID(user.getTenantUID());
		queryAsset.setRuleStatus(1);// 默认只显示已配置过规则的资产
		siteUid = user.getSiteUID();
		getAssetsListByTenantUid();
	}

	public void getAssetsListByTenantUid() {
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
		List<SearchFilter> assetFilters = new ArrayList<SearchFilter>();// getFilter(user.getSiteUID());
		/*			List<String> uids = null;
	if (queryAsset.getRuleStatus() != null) {
			uids = new ArrayList<String>();
			if (queryAsset.getRuleStatus() == RULE_STATUS_CONFIGURED) {// 已配置
				uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
				if (CollectionUtils.isNotEmpty(uids)) {
					assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
				}
			} else if (queryAsset.getRuleStatus() == RULE_STATUS_NOT_CONFIGURED) {// 未配置
				//TODO need wujb
				
				// uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
				// assetFilters.add(new SearchFilter("uid", SearchFilter.Operator., uids));
			}
		}*/

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
        if (null != queryAsset.getClinicalDeptId()) {
            assetFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
        }
		if (StringUtils.isNotEmpty(queryAsset.getBrand())) {
			assetFilters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, queryAsset.getBrand()));
		}
		if(level != null) {
			List<String> uids = null;
			if(level == 1) {
				uids = relRuleAssetRepository.findRuleUid1ByLevelAndTenantUID(user.getTenantUID());
				uids = uids.stream().filter(uid -> uid != null).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
/*				if (CollectionUtils.isNotEmpty(uids)) {
					assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
				}*/
			}else if(level == 2) {
				uids = relRuleAssetRepository.findRuleUid2ByLevelAndTenantUID(user.getTenantUID());
				uids = uids.stream().filter(uid -> uid != null).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
/*				if (CollectionUtils.isNotEmpty(uids)) {
					assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
				}*/
			}else if(level == 3) {
				uids = relRuleAssetRepository.findRuleUid3ByLevelAndTenantUID(user.getTenantUID());
				uids = uids.stream().filter(uid -> uid != null).distinct().collect(Collectors.toList());
				assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
/*				if (CollectionUtils.isNotEmpty(uids)) {
					assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
				}*/
			}
		}
		if (!assetFilters.isEmpty()) {
			List<SearchFilter> userFilter = getFilter(user.getSiteUID());
			assetFilters.addAll(userFilter);
			return assetInfoRepository.findBySearchFilter(assetFilters);
		}
		return new ArrayList<>();
	}

	public List<SearchFilter> getFilter(String siteUID) {
		List<SearchFilter> filter = new ArrayList<SearchFilter>();
		OrgInfo orgInfo = orgInfoRepository.getByUid(siteUID);
		if (orgInfo.getOrgType() == 3) {// 院区
			filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
		} else if (orgInfo.getOrgType() == 2) {// 医院
			filter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
		} else if (orgInfo.getOrgType() == 1) {// 集团
			filter.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
		} else {// 租户
			filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
		}
		filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
		return filter;
	}

	public void addFilter() {
		sourceAssets = getAssetList();
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

	/*
	 * @Transactional public void saveRel(){ List<RelRuleAsset> rels = new
	 * ArrayList<RelRuleAsset>(targetAssets.size()); List<String> deleteIds = new
	 * ArrayList<String>(); for (AssetInfo ai : targetAssets) { RelRuleAsset rel =
	 * new RelRuleAsset(); rel.setAssetUID(ai.getUid());
	 * rel.setRuleUID(pmOrderRuleUID); rel.setCreatedDate(new Date());
	 * rel.setTenantUID(ai.getTenantUID()); rels.add(rel);
	 * deleteIds.add(ai.getUid()); }
	 * relRuleAssetRepository.deleteByAssetUIDIn(deleteIds);
	 * relRuleAssetRepository.save(rels); WebUtil.addSuccessMessage("配置成功！");
	 * targetAssets.clear(); }
	 */

	public void generatePmOrders() {
		if (targetAssets.isEmpty()) {
			WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
			return;
		}
		List<AssetRuleModel> models = new ArrayList<AssetRuleModel>();
		targetAssets = targetAssets.stream().distinct().collect(Collectors.toList());
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
			}
			if (rule == null) {
				WebUtil.addErrorMessage("找不到该设备该级别的保养规则！");
				return;
			}
			Integer period = rule.getPeriod();
/*			if (ai.getLastPmDate() != null && TimeUtils.getDays(ai.getLastPmDate()) < period) {
				LOGGER.warn("during period !");
				continue;
			}*/
			if(period == null) {
				period = 1;
//				WebUtil.addErrorMessage("该设备该级别的保养规则周期不能为空！");
//				return;
			}
			boolean isExists = pmOrderRepository.findOpenPm(ai.getId(),level);
			if(isExists) {
				WebUtil.addErrorMessage("保养工单已存在！");
				return;
			}
			AssetRuleModel model = new AssetRuleModel();
			model.setUid(ai.getUid());
			model.setLevel(level);
			model.setPeriod(period);
			model.setPlanTime(planTime);
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
				}
				if (rule == null) {
					LOGGER.error("not find pmOrderRule by assetUid : {},level : {}", ai.getUid(), level);
					continue;
				}
				int period = rule.getPeriod();
				boolean isExists = pmOrderRepository.findOpenPm(ai.getId(),level);
				if(isExists) {
					LOGGER.warn("open pm exists !");
					continue;
				}
				AssetRuleModel model = new AssetRuleModel();
				model.setUid(ai.getUid());
				model.setLevel(level);
				model.setPeriod(period);
				model.setPlanTime(planTime);
				models.add(model);
			}
			if (CollectionUtils.isEmpty(models)) {
				WebUtil.addErrorMessage("无可生成保养工单的资产！");
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
		pmOrderRequestApiService.createPmOrdersByBatch(token, request);
		//WebUtil.addSuccessMessage("保养工单生成成功！");
		WebUtil.addSuccessMessage("正在生成保养工单，请稍候！");
		//targetAssets.clear();
	}

	/*
	 * public List<PmOrderRule> getPmOrderRuleList(){ List<SearchFilter>
	 * assetFilters = new ArrayList<>();//getFilter(user.getSiteUID());
	 * assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,
	 * queryAsset.getTenantUID())); assetFilters.add(new SearchFilter("status",
	 * SearchFilter.Operator.EQ, 1));
	 * if(StringUtils.isNotEmpty(queryAsset.getFunctionType())){
	 * assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE,
	 * queryAsset.getFunctionType())); } if
	 * (StringUtils.isNotEmpty(queryAsset.getName())) { assetFilters.add(new
	 * SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName())); } if
	 * (StringUtils.isNotEmpty(queryAsset.getManufacture())) { assetFilters.add(new
	 * SearchFilter("manufacture", SearchFilter.Operator.LIKE,
	 * queryAsset.getManufacture())); } if (null != queryAsset.getAssetGroup()) {
	 * assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,
	 * queryAsset.getAssetGroup())); } return
	 * pmOrderRuleRepository.findBySearchFilter(assetFilters); // return
	 * pmOrderRuleRepository.findByTenantUIDAndStatus(user.getTenantUID(),1); }
	 */

	public String getStrDate(Date lastPmDate) {
		return TimeUtils.getStrDate(lastPmDate, "yyyy-MM-dd");
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

	public Date getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}
	
	

}
