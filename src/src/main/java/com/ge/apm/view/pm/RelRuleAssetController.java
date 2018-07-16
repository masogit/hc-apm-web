package com.ge.apm.view.pm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.*;
import com.ge.apm.service.common.UserCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.QueryAsset;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.AssetRuleModel;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class RelRuleAssetController extends GenericCRUDUUIDController<RelRuleAsset> {

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

	@Override
	protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
		relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		brandRepository = WebUtil.getBean(BrandRepository.class);
		targetAssets = new ArrayList<>();
		queryAsset = new QueryAsset();
		queryAsset.setTenantUID(user.getTenantUID());
		siteUid = user.getSiteUID();
		initAssets();

		userCommonService = WebUtil.getBean(UserCommonService.class);
		isInsulate = UserContextService.isInsulate();
		loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
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
		List<SearchFilter> assetFilters = new ArrayList<SearchFilter>();
		List<String> uids = null;
		if (StringUtils.isNotEmpty(queryAsset.getFunctionType())) {
			assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
		}
		if (StringUtils.isNotEmpty(queryAsset.getName())) {
			assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
		}
/*		if (StringUtils.isNotEmpty(queryAsset.getManufacture())) {
			assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
		}*/
		if(null != queryAsset.getManufactureId()){
			assetFilters.add(new SearchFilter("manufactureId", SearchFilter.Operator.EQ, queryAsset.getManufactureId()));
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
		if (!assetFilters.isEmpty()) {
			if (queryAsset.getRuleStatus() != null) {
				uids = new ArrayList<>();
				if (queryAsset.getRuleStatus() == 1) {// 已配置
					uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
					if (CollectionUtils.isNotEmpty(uids)) {
						assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
					}
				} else if (queryAsset.getRuleStatus() == 2) {// 未配置
					uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
					assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN, uids));
				}
			}
			List<SearchFilter> userFilter = getAssetFilter();
			assetFilters.addAll(userFilter);
			return assetInfoRepository.findBySearchFilter(assetFilters);
		}
		return new ArrayList<>();
	}

	public List<SearchFilter> getAssetFilter() {
		List<SearchFilter> filter = userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType);
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

	@Transactional
	public void saveRel() {
		if (targetAssets.isEmpty()) {
			WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
		} else {
			List<RelRuleAsset> rels = new ArrayList<>(targetAssets.size());
			targetAssets = targetAssets.stream().distinct().collect(Collectors.toList());
			for (AssetInfo ai : targetAssets) {
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
						rel.setRuleUID1(pmOrderRuleUID);
					} else if (level == 2) {
						rel.setRuleUID2(pmOrderRuleUID);
					} else if (level == 3) {
						rel.setRuleUID(pmOrderRuleUID);
					}
					rels.add(rel);
				}
				relRuleAssetRepository.save(rels);
			}
			WebUtil.addSuccessMessage("配置成功!");
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
		if (queryAsset.getAssetGroup() != null) {
			assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
		}
		if (level != null) {
			assetFilters.add(new SearchFilter("level", SearchFilter.Operator.EQ, level));
		}
		return pmOrderRuleRepository.findBySearchFilter(assetFilters);
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

}
