package com.ge.apm.view.measuring;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class MeasureRuleConfigController extends GenericCRUDUUIDController<RelMeasureRule> {
	private static final long serialVersionUID = -1L;
	private RelMeasureRuleRepository dao = null;
	private UserAccount user;
	private Map<String, AssetInfo> assetMap;
	private Map<String, MeasureOrderRule> ruleMap;
	private MeasureOrderRuleRepository measureOrderRuleRepository;
	private AssetInfoRepository assetInfoRepository;
	private String queryCondition;
	private List<String> assetUids;
	private OrgInfoRepository orgInfoRepository;
	private Boolean isInsulate;
	private Integer loginUserOrgType;
	private UserCommonService userCommonService;
	private boolean deleteRule2 = false;
	private boolean deleteRule3 = false;

	@Override
	protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		dao = WebUtil.getBean(RelMeasureRuleRepository.class);
		user = UserContextService.getCurrentUserAccount();
		measureOrderRuleRepository = WebUtil.getBean(MeasureOrderRuleRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		userCommonService = WebUtil.getBean(UserCommonService.class);
		assetMap = new HashMap<>();
		ruleMap = new HashMap<>();
		isInsulate = UserContextService.isInsulate();
		loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
	}

	@Override
	protected RelMeasureRuleRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<RelMeasureRule> loadData(PageRequest pageRequest) {
		assetMap.clear();
		ruleMap.clear();
		if(searchFilters != null) {
			searchFilters.clear();
		}else{
			searchFilters = new ArrayList<>();
		}
		List<SearchFilter> userFilters = userCommonService.getUserLogicFilters(loginUserOrgType,user);
		searchFilters.addAll(userFilters);
		if(StringUtils.isNotEmpty(queryCondition)){
			searchFilters.add(new SearchFilter("assetUID", SearchFilter.Operator.IN, getAssetInfoByFilter()));
		}
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
		Page<RelMeasureRule> result = dao.findBySearchFilter(searchFilters, pageRequest);
		initMap(result);
		return result;
	}



	public void initMap(Page<RelMeasureRule> result) {
		if (result.hasContent()) {
			List<RelMeasureRule> rels = result.getContent();
			List<String> assetUids = new ArrayList<>();
			List<String> ruleUids = new ArrayList<>();
			for (RelMeasureRule relRuleAsset : rels) {
				if(relRuleAsset.getAssetUID() != null) {
					assetUids.add(relRuleAsset.getAssetUID());
				}
				if(relRuleAsset.getRuleUID1() != null) {
					ruleUids.add(relRuleAsset.getRuleUID1());
				}
				if(relRuleAsset.getRuleUID2() != null) {
					ruleUids.add(relRuleAsset.getRuleUID2());
				}
				if(relRuleAsset.getRuleUID() != null) {
					ruleUids.add(relRuleAsset.getRuleUID());
				}

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
				List<MeasureOrderRule> rules = measureOrderRuleRepository.findByUidIn(ruleUids);
				if(CollectionUtils.isNotEmpty(rules)){
					for (MeasureOrderRule measureOrderRule : rules) {
						ruleMap.put(measureOrderRule.getUid(), measureOrderRule);
					}
				}
			}
		}
	}

	@Override
	public List<RelMeasureRule> getItemList() {
		return dao.find();
	}

	public String getRuleName(String ruleUID) {
		MeasureOrderRule rule = ruleMap.get(ruleUID);
		if (rule != null) {
			return rule.getName();
		}
		return null;
	}

/*	public Integer getRuleLevel(String ruleUID) {
		PmOrderRule rule = ruleMap.get(ruleUID);
		if (rule != null) {
			return rule.getLevel();
		}
		return null;
	}*/

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
			return ai.getMakerShortName();
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

	public List<String> getAssetInfoByFilter() {
		if(StringUtils.isNotEmpty(queryCondition)) {
			List<SearchFilter> searchAssetFilters = getFilter(user.getSiteUID());
			searchAssetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(searchAssetFilters);
			List<String> uids = new ArrayList<String>();
			if(CollectionUtils.isNotEmpty(assetInfos)) {
				assetInfos.forEach((asset) -> uids.add(asset.getUid()));
			}
			return uids;
		}
		return null;
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
		filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1,2,3)));
		return filter;
	}

	public List<String> getAssetInfoUids(){
		List<SearchFilter> filters = getFilter(user.getSiteUID());
		List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(filters);
		List<String> uids = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(assetInfos)) {
			assetInfos.forEach((asset) -> uids.add(asset.getUid()));
		}
		return uids;
	}

	public void setOneSearchStr() {
		queryCondition = null;
	}

	public String getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}

	public List<String> getAssetUids() {
		return assetUids;
	}

	public void setAssetUids(List<String> assetUids) {
		this.assetUids = assetUids;
	}

	//@Transactional
	public void deleteRule(){
		if((!deleteRule2) && (!deleteRule3)){
			return;
		}
		String alert = null;
		if(deleteRule3){
			selected.setRuleUID(null);
			alert = "三级规则成功删除！";
		}
		if(deleteRule2){
			if(StringUtils.isEmpty(alert)){
				alert = "二级规则成功删除！";
			}else{
				alert = "二级规则和三级规则都已成功删除！";
			}
			selected.setRuleUID2(null);
		}
		if(selected.getRuleUID2() == null && selected.getRuleUID() == null){
			dao.delete(selected);
			WebUtil.addSuccessMessage(alert);
			deleteRule3 = false;
			deleteRule2 = false;
			return;
		}
		dao.save(selected);
		deleteRule3 = false;
		deleteRule2 = false;
		WebUtil.addSuccessMessage(alert);
	}

	public void deleteRule2(){
		this.selected.setRuleUID2(null);
		this.deleteRule2 = true;
	}

	public void deleteRule3(){
		this.selected.setRuleUID(null);
		this.deleteRule3 = true;
	}

	public boolean getDeleteRule2() {
		return deleteRule2;
	}

	public void setDeleteRule2(boolean deleteRule2) {
		this.deleteRule2 = deleteRule2;
	}

	public boolean getDeleteRule3() {
		return deleteRule3;
	}

	public void setDeleteRule3(boolean deleteRule3) {
		this.deleteRule3 = deleteRule3;
	}
	/*
	 * @Override public void onBeforeNewObject(RelMeasureRule object) { }
	 *
	 * @Override public void onAfterNewObject(RelMeasureRule object, boolean isOK)
	 * { }
	 *
	 * @Override public void onBeforeUpdateObject(RelMeasureRule object) { }
	 *
	 * @Override public void onAfterUpdateObject(RelMeasureRule object, boolean
	 * isOK) { }
	 *
	 * @Override public void onBeforeDeleteObject(RelMeasureRule object) { }
	 *
	 * @Override public void onAfterDeleteObject(RelMeasureRule object, boolean
	 * isOK) { }
	 *
	 * @Override public void onBeforeSave(RelMeasureRule object) { }
	 *
	 * @Override public void onAfterDataChanged(){ };
	 */

}