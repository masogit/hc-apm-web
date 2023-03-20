package com.ge.apm.view.pm;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.RelRuleAssetRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.RelRuleAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class RelRuleAssetManagerController extends GenericCRUDUUIDController<RelRuleAsset> {

	private static final long serialVersionUID = -1L;
    private PageUtil<RelRuleAsset> pageUtil = new PageUtil();

	private RelRuleAssetRepository dao = null;
	private UserAccount user;
	private Map<String, AssetInfo> assetMap;
	private Map<String, PmOrderRule> ruleMap;
	private PmOrderRuleRepository pmOrderRuleRepository;
	private AssetInfoRepository assetInfoRepository;
	private String queryCondition;
    private AssetInfoService assetInfoService;
    private OrgInfoTreeService orgInfoTreeService;
	private List<String> assetUids;
	private OrgInfoRepository orgInfoRepository;
	private RelRuleAssetService relRuleAssetService;
	private UaaService uaaService;
	private Boolean isInsulate;
	private Integer loginUserOrgType;
	private UserCommonService userCommonService;
	private boolean deleteRule2 = false;
	private boolean deleteRule3 = false;
	private Map<String, Boolean> selectedMap;
	private List<RelRuleAsset> currentPageAssets;
    private List<OrgInfo> clinicalDeptList;
    private  List<PmOrderRule> pmOrderRuleList;
    //选定科室过滤
    @Getter @Setter
    private String filterClinicalDeptOrg;
    //选定过滤规则
    private Integer filterRuleLevel=null;
    @Getter@Setter
    private String filterRuleUIDInspectionName;
    private Integer filterRuleUIDInspectionLevel=null;
    private String filterRuleUIDInspectionRuleUid=null;
    @Getter@Setter
    private String filterRuleUID2Name;
    private Integer filterRuleUID2Level=null;
    private String filterRuleUID2RuleUid=null;
    @Getter@Setter
    private String filterRuleUIDName;
    private Integer filterRuleUIDLevel=null;
    private String filterRuleUIDRuleUid=null;
    @Getter@Setter
    private String filterRuleUID4Name;
    private Integer filterRuleUID4Level=null;
    private String filterRuleUID4RuleUid=null;
    private Set<String> clinicalDeptOrgUidSet;
	@Getter @Setter
	private StreamedContent excelFile;
    private ConfigService configService;
    @Getter @Setter
    private String startUsingDateFormatTime = null;
    @Getter @Setter
    private String startPurchaseDateFormatTime = null;
    @Getter @Setter
    private String startConfigurationDateFormatTime = null;
	@Override
	protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		dao = WebUtil.getBean(RelRuleAssetRepository.class);
		user = UserContextService.getCurrentUserAccount();
		pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		userCommonService = WebUtil.getBean(UserCommonService.class);
		relRuleAssetService = WebUtil.getBean(RelRuleAssetService.class);
        configService = WebUtil.getBean(ConfigService.class);
		uaaService = WebUtil.getBean(UaaService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
		assetMap = new HashMap<>();
		ruleMap = new HashMap<>();
		this.selectedMap = new HashMap();
		this.currentPageAssets = new ArrayList<>();
		isInsulate = UserContextService.isInsulate();
		loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
        loadClinicalDeptList();
	}

	@Override
	protected RelRuleAssetRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<RelRuleAsset> loadData(PageRequest pageRequest) {
		assetMap.clear();
		ruleMap.clear();
        List<SearchFilter> searchFilterList = new ArrayList<>();
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterRuleUIDInspectionLevel!=null && StringUtils.isNotEmpty(filterRuleUIDInspectionRuleUid)) {
            searchFilterList.add(new SearchFilter("ruleUIDInspection", SearchFilter.Operator.EQ, filterRuleUIDInspectionRuleUid));
        }
        if (filterRuleUID2Level!=null && StringUtils.isNotEmpty(filterRuleUID2RuleUid)) {
            searchFilterList.add(new SearchFilter("ruleUID2", SearchFilter.Operator.EQ, filterRuleUID2RuleUid));
        }
        if (filterRuleUIDLevel!=null && StringUtils.isNotEmpty(filterRuleUIDRuleUid)) {
            searchFilterList.add(new SearchFilter("ruleUID", SearchFilter.Operator.EQ, filterRuleUIDRuleUid));
        }
        if (filterRuleUID4Level!=null && StringUtils.isNotEmpty(filterRuleUID4RuleUid)) {
            String[] ruleUid4s = StringUtils.split(filterRuleUID4RuleUid, "|");
            searchFilterList.add(new SearchFilter("ruleUID4", SearchFilter.Operator.CONTAINS, Arrays.asList(ruleUid4s)));
        }
        searchFilterList.addAll(searchFilters);
		if(StringUtils.isNotEmpty(queryCondition)){
            searchFilterList.add(new SearchFilter("assetUID", SearchFilter.Operator.IN, findAssetUidBySearchFilter(getAssetInfoByFilter())));
		}else {
            searchFilterList.addAll(getOrgFilter(user.getSiteUID()));
            searchFilterList.add(new SearchFilter("assetUID", SearchFilter.Operator.NotIN, getInvalidAssets(user.getTenantUID())));
		}
        String assetName=null;//资产名称
        String departNum=null;//设备编号
        Integer assetGroup=null;//设备分类
        String nationalStandardCode=null;//2010国家编码
        String makerShortName=null;//品牌/制造商简称
        String manufacture=null;//制造商
        String qrCode=null;//二维码
        String startUsingDate=null;
        String purchaseDate=null;
        if (CollectionUtils.isNotEmpty(searchFilters)){
            for (SearchFilter searchFilter :searchFilters) {
                if ("assetName".equals(searchFilter.fieldName)) assetName = (String)searchFilter.value;
                if ("departNum".equals(searchFilter.fieldName)) departNum = (String)searchFilter.value;
                if ("assetGroup".equals(searchFilter.fieldName)) assetGroup =Integer.valueOf((String)searchFilter.value);
                if ("nationalStandardCode".equals(searchFilter.fieldName)) nationalStandardCode = (String)searchFilter.value;
                if ("makerShortName".equals(searchFilter.fieldName)) makerShortName = (String)searchFilter.value;
                if ("manufacture".equals(searchFilter.fieldName)) manufacture = (String)searchFilter.value;
                if ("qrCode".equals(searchFilter.fieldName)) qrCode = (String)searchFilter.value;
                if ("startUsingDate".equals(searchFilter.fieldName)) {
                    startUsingDate = (String)searchFilter.value;
                }
                if ("purchaseDate".equals(searchFilter.fieldName)) {
                    purchaseDate = (String)searchFilter.value;
                }
            }
        }
        searchFilterList.removeIf(e -> e.fieldName.equals("assetName"));
        searchFilterList.removeIf(e -> e.fieldName.equals("departNum"));
        searchFilterList.removeIf(e -> e.fieldName.equals("assetGroup"));
        searchFilterList.removeIf(e -> e.fieldName.equals("clinicalDeptName"));
        searchFilterList.removeIf(e -> e.fieldName.equals("nationalStandardCode"));
        searchFilterList.removeIf(e -> e.fieldName.equals("makerShortName"));
        searchFilterList.removeIf(e -> e.fieldName.equals("manufacture"));
        searchFilterList.removeIf(e -> e.fieldName.equals("qrCode"));
        searchFilterList.removeIf(e -> e.fieldName.equals("startUsingDate"));
        searchFilterList.removeIf(e -> e.fieldName.equals("purchaseDate"));
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        List<RelRuleAsset> relRuleAssets = dao.findBySearchFilter(searchFilterList);
        List<RelRuleAsset> relRuleAssetList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(relRuleAssets) && (StringUtils.isNotEmpty(assetName)|| StringUtils.isNotEmpty(departNum)|| assetGroup!=null || CollectionUtils.isNotEmpty(clinicalDeptOrgUidSet)||
                StringUtils.isNotEmpty(nationalStandardCode)|| StringUtils.isNotEmpty(makerShortName)|| StringUtils.isNotEmpty(manufacture)|| StringUtils.isNotEmpty(qrCode)||
                StringUtils.isNotEmpty(startUsingDate) || StringUtils.isNotEmpty(purchaseDate))){
            for(RelRuleAsset relRuleAsset :relRuleAssets){
                String assetUID = relRuleAsset.getAssetUID();
                AssetInfo assetInfo = assetInfoService.selectAssetInfos(assetUID, assetName, departNum, assetGroup,  clinicalDeptOrgUidSet, nationalStandardCode, makerShortName, manufacture, qrCode,
                        startUsingDate,purchaseDate);
                if(assetInfo!=null){
                    relRuleAssetList.add(relRuleAsset);
                }
            }
             assetName=null;
             departNum=null;
             assetGroup=null;
             nationalStandardCode=null;
             makerShortName=null;
             manufacture=null;
             qrCode=null;
        }else {
            relRuleAssetList.addAll(relRuleAssets);
        }
		currentPageAssets = relRuleAssetList;
        Page<RelRuleAsset> assetInfoPage = pageUtil.pageNum(relRuleAssetList, pageRequest);
		initMap(relRuleAssetList);
		return assetInfoPage;
	}

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
    }
    public void getRuleList() {
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1, 2, 3)));
        pmOrderRuleList = pmOrderRuleRepository.findBySearchFilter(filter);
        pmOrderRuleList.sort(new Comparator<PmOrderRule>() {
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
    }
    public List<String> autoLevelRulePMName(Integer level) {
	    getRuleList();
        if(level!=null){
            filterRuleLevel=level;
            switch (level) {
                case 1:
                    filterRuleUIDInspectionLevel = level;
                    break;
                case 2:
                    filterRuleUID2Level= level;
                    break;
                case 3:
                    filterRuleUIDLevel = level;
                    break;
                case 4:
                    filterRuleUID4Level = level;
                    break;
            }
            pmOrderRuleList = pmOrderRuleList.stream().filter(s -> s.getLevel() ==level).collect(Collectors.toList());
        }
        return pmOrderRuleList.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }
    public void onLevelRulePMNameFilterChange(AjaxBehaviorEvent event) {
	    String filterLevelRulePMName;
        if (event instanceof SelectEvent) {
            filterLevelRulePMName=(String) ((SelectEvent) event).getObject();
            if(StringUtils.isNotEmpty(filterLevelRulePMName)){
                switch (filterRuleLevel) {
                    case 1:
                        filterRuleUIDInspectionName = filterLevelRulePMName;
                        filterRuleUIDInspectionRuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUIDInspectionLevel = filterRuleLevel;
                        break;
                    case 2:
                        filterRuleUID2Name = filterLevelRulePMName;
                        filterRuleUID2RuleUid= this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUID2Level = filterRuleLevel;
                        break;
                    case 3:
                        filterRuleUIDName = filterLevelRulePMName;
                        filterRuleUIDRuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUIDLevel = filterRuleLevel;
                        break;
                    case 4:
                        filterRuleUID4Name = filterLevelRulePMName;
                        filterRuleUID4RuleUid = this.pmOrderRuleList.stream().filter(item -> item.getName().equals(filterLevelRulePMName)).collect(Collectors.toList()).get(0).getUid();
                        filterRuleUID4Level = filterRuleLevel;
                        break;
                }
            }
        }else {
            switch (filterRuleLevel) {
                case 1:
                    filterRuleUIDInspectionName = null;
                    filterRuleUIDInspectionLevel = null;
                    break;
                case 2:
                    filterRuleUID2Name = null;
                    filterRuleUID2Level= null;

                    break;
                case 3:
                    filterRuleUIDName = null;
                    filterRuleUIDLevel = null;
                    break;
                case 4:
                    filterRuleUID4Name = null;
                    filterRuleUID4Level = null;
                    break;
            }
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }
    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }
        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }
	public void onSelectAllOfPage() {
		boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getAssetUID()) || !selectedMap.get(item.getAssetUID()));
		for (RelRuleAsset item : currentPageAssets) {
			selectedMap.put(item.getAssetUID(), !isSelectedAll);
		}
	}

	public void batchDelete(){
		Map<String, Boolean> map = this.selectedMap;
		List<String> deleteAssetUids = Lists.newArrayList();
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			if (!entry.getValue()) {
				continue;
			}
			deleteAssetUids.add(entry.getKey());
		}
		dao.deleteByAssetUids(deleteAssetUids);
		this.selectedMap.clear();
	}

	public boolean isChecked() {
		return selectedMap.containsValue(true);
	}

	private List<String> getInvalidAssets(String tenantUID) {
		List<String> invalidAssetUids = assetInfoRepository.getInvalidAssets(tenantUID,Lists.newArrayList(4,5,6));
		if(CollectionUtils.isEmpty(invalidAssetUids)){
			return new ArrayList<>();
		}
		return invalidAssetUids;
	}


	public List<String> findAssetUidBySearchFilter(List<SearchFilter> searchAssetFilters){
		List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(searchAssetFilters);
		List<String> uids = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(assetInfos)) {
			assetInfos.forEach((asset) -> uids.add(asset.getUid()));
		}
		return uids;
	}

	public void initMap(List<RelRuleAsset> rels) {
		if (CollectionUtils.isNotEmpty(rels)) {
			List<String> assetUids = new ArrayList<>();
			List<String> ruleUids = new ArrayList<>();
			for (RelRuleAsset relRuleAsset : rels) {
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
                if(relRuleAsset.getRuleUIDInspection() != null) {
                    ruleUids.add(relRuleAsset.getRuleUIDInspection());
                }
                if(relRuleAsset.getRuleUID4() != null) {
                    ruleUids.add(relRuleAsset.getRuleUID4());
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
        String[] split = StringUtils.split(ruleUID, '|');
        List<String> ruleUids = Arrays.asList(split);

        List<PmOrderRule> pmOrderRules = pmOrderRuleRepository.findByUidIn(ruleUids);
         String ruleName = "";
        if(CollectionUtils.isNotEmpty(pmOrderRules)){
             ruleName = pmOrderRules.stream().map(PmOrderRule::getName).collect(Collectors.joining("|"));
        }
        return ruleName;
	}
    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
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
	public Date getInstallDate(String assetUid){
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getInstallDate();
		}
		return null;
	}

	public Date getPurchaseDate(String assetUid){
		AssetInfo ai = getAssetInfo(assetUid);
		if (ai != null) {
			return ai.getPurchaseDate();
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

	public List<SearchFilter> getAssetInfoByFilter(){
		List<SearchFilter> searchAssetFilters = getFilter(user.getSiteUID());
		if(StringUtils.isNotEmpty(queryCondition)) {
			searchAssetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
			searchAssetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryCondition, "1*3+"));
		}
		return searchAssetFilters;
	}

	/**
	 * 导出已经配置了规则的设备
	 */
	public void exportRelRuleAssets() {
		try {
			List<RelRuleAsset> relRuleAssets = dao.findBySearchFilter(this.searchFilters);
			List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(relRuleAssets.stream().map(relRuleAsset -> {return relRuleAsset.getAssetUID();}).collect(Collectors.toList()));
			List<AssetInfoAndRelRule> assetInfoAndRelRules = relRuleAssetService.mergeAssetInfoAndRelRule(assetInfos,relRuleAssets);
			excelFile = relRuleAssetService.getExcelFile(assetInfoAndRelRules);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * 导出所有设备
	 */
	public void exportAllAssets() {
		try {
			List<AssetInfo> assetInfos = findAllAssets();
			List<RelRuleAsset> relRuleAssets = dao.findByAssetUids(assetInfos.stream().map(assetInfo -> {return assetInfo.getUid();}).collect(Collectors.toList()));
			List<AssetInfoAndRelRule> assetInfoAndRelRules = relRuleAssetService.mergeAssetInfoAndRelRule(assetInfos,relRuleAssets);
			excelFile = relRuleAssetService.getExcelFile(assetInfoAndRelRules);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}



	private List<AssetInfo> findAllAssets() {
		List<SearchFilter> searchFilters = new ArrayList<>();
		searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		searchFilters.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
		searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		if (!UserContextService.isMVSRole()) {
			UserAccount user = UserContextService.getCurrentUserAccount();
			List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
			searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
			searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
			searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
			searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
			searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		} else {
			searchFilters.add(new SearchFilter("mvsEquip", SearchFilter.Operator.EQ, 1));
		}
		List<AssetInfo> allAssets =  assetInfoRepository.findBySearchFilter(searchFilters);
		return allAssets;
	}

	public List<SearchFilter> getFilter(String siteUID) {
		List<SearchFilter> filter = getOrgFilter(siteUID);
		filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
		filter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("isPm", SearchFilter.Operator.EQ, true));
		filter.add(new SearchFilter("status", SearchFilter.Operator.IN, Lists.newArrayList(1,2,3)));
		return filter;
	}

	public List<SearchFilter> getOrgFilter(String siteUID){
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

	public Map<String, Boolean> getSelectedMap() {
		return selectedMap;
	}

	public void setSelectedMap(Map<String, Boolean> selectedMap) {
		this.selectedMap = selectedMap;
	}

	public List<RelRuleAsset> getCurrentPageAssets() {
		return currentPageAssets;
	}

	public void setCurrentPageAssets(List<RelRuleAsset> currentPageAssets) {
		this.currentPageAssets = currentPageAssets;
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