package com.ge.apm.view.pm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.RelRuleAssetRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.Brand;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

@ManagedBean
@ViewScoped
public class RelRuleAssetManagerController extends GenericCRUDUUIDController<RelRuleAsset> {

	private static final long serialVersionUID = -1L;
	private RelRuleAssetRepository dao = null;
	private UserAccount user;
	private Map<String, AssetInfo> assetMap;
	private Map<String, PmOrderRule> ruleMap;
	private PmOrderRuleRepository pmOrderRuleRepository;
	private AssetInfoRepository assetInfoRepository;

	@Override
	protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		dao = WebUtil.getBean(RelRuleAssetRepository.class);
		user = UserContextService.getCurrentUserAccount();
		pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		assetMap = new HashMap<String, AssetInfo>();
		ruleMap = new HashMap<String, PmOrderRule>();
	}

	@Override
	protected RelRuleAssetRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<RelRuleAsset> loadData(PageRequest pageRequest) {
		assetMap.clear();
		ruleMap.clear();
		if (searchFilters == null) {
			searchFilters = new ArrayList<SearchFilter>();
		}
		if (!this.hasFilterOnField("tenantUID")) {
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		}
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
		Page<RelRuleAsset> result = dao.findBySearchFilter(searchFilters, pageRequest);
		initMap(result);
		return result;
		// return dao.findBySearchFilter(searchFilters, pageRequest);
	}

	public void initMap(Page<RelRuleAsset> result) {
		if (result.hasContent()) {
			List<RelRuleAsset> rels = result.getContent();
			List<String> assetUids = new ArrayList<String>();
			List<String> ruleUids = new ArrayList<String>();
			for (RelRuleAsset relRuleAsset : rels) {
				assetUids.add(relRuleAsset.getAssetUID());
				ruleUids.add(relRuleAsset.getRuleUID());
			}
			if(CollectionUtils.isNotEmpty(assetUids)){
				List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(assetUids);
				if(CollectionUtils.isNotEmpty(assetInfos)){
					for (AssetInfo assetInfo : assetInfos) {
						assetMap.put(assetInfo.getUid(), assetInfo);
					}
				}
			}
			if(CollectionUtils.isNotEmpty(ruleUids)){
				List<PmOrderRule> rules = pmOrderRuleRepository.findByUidIn(ruleUids);
				if(CollectionUtils.isNotEmpty(rules)){
					for (PmOrderRule pmOrderRule : rules) {
						ruleMap.put(pmOrderRule.getUid(), pmOrderRule);
					}
				}
			}
		}
	}

	@Override
	public List<RelRuleAsset> getItemList() {
		return dao.find();
	}

	public String getRuleName(String ruleUID) {
		PmOrderRule rule = pmOrderRuleRepository.findByUid(ruleUID);
		if (rule != null) {
			return rule.getName();
		}
		return null;
	}

	public AssetInfo getAssetInfo(String assetUid) {
		return assetMap.get(assetUid);
		//return assetInfoRepository.findByUid(assetUid);
	}

	public String getAssetName(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getName();
		}
		return null;
	}

	public String getDepartNum(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getDepartNum();
		}
		return null;
	}

	public Integer getAssetGroup(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getAssetGroup();
		}
		return null;
	}

	public String getClinicalDeptName(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getClinicalDeptName();
		}
		return null;
	}

	public String getNationalStandardCode(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getNationalStandardCode();
		}
		return null;
	}

	public String getBrand(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			Brand brand = ai.getBrand();
			if (brand != null) {
				return brand.getName();
			}
		}
		return null;
	}

	public String getManufacture(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getManufacture();
		}
		return null;
	}

	public String getQrCode(String assetUid) {
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getQrCode();
		}
		return null;
	}

	/*
	 * @Override public void onBeforeNewObject(RelRuleAsset object) { }
	 * 
	 * @Override public void onAfterNewObject(RelRuleAsset object, boolean isOK)
	 * { }
	 * 
	 * @Override public void onBeforeUpdateObject(RelRuleAsset object) { }
	 * 
	 * @Override public void onAfterUpdateObject(RelRuleAsset object, boolean
	 * isOK) { }
	 * 
	 * @Override public void onBeforeDeleteObject(RelRuleAsset object) { }
	 * 
	 * @Override public void onAfterDeleteObject(RelRuleAsset object, boolean
	 * isOK) { }
	 * 
	 * @Override public void onBeforeSave(RelRuleAsset object) { }
	 * 
	 * @Override public void onAfterDataChanged(){ };
	 */
}