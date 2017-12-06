package com.ge.apm.view.pm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.RelRuleAssetRepository;
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
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class RelRuleAssetController extends GenericCRUDUUIDController<RelRuleAsset>{

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
	
	@Override
    protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        targetAssets = new ArrayList<AssetInfo>();
        queryAsset = new QueryAsset();
        queryAsset.setTenantUID(user.getTenantUID());
        getAssetsListByTenantUid();
    }
	
    
    public void getAssetsListByTenantUid() {
        sourceAssets = new ArrayList<>();//assetInfoRepository.findBySearchFilter(getFilter(user.getSiteUID()));//findByTenantUID(user.getTenantUID());
        targetAssets.forEach((item)->{
            if(sourceAssets.contains(item)){
                sourceAssets.remove(item);
            }
        });
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }
    
    public String getOrgName(String uid){
    	OrgInfo orgInfo = orgInfoRepository.getByUid(uid);
    	if(orgInfo == null){
    		return null;
    	}
    	return orgInfo.getName();		
    }
    
    public List<AssetInfo> getAssetList() {
        List<SearchFilter> assetFilters =  new ArrayList<SearchFilter>();//getFilter(user.getSiteUID());
        List<String> uids = null;
        
        if(queryAsset.getRuleStatus() != null) {
        		uids = new ArrayList<String>();
            if(queryAsset.getRuleStatus() == 1){//已配置
	        		uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
	        		if(CollectionUtils.isNotEmpty(uids)) {
	        			assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, uids));
	        		}
	        }else if(queryAsset.getRuleStatus() == 2) {//未配置
	//        		uids = relRuleAssetRepository.findAssetUidsByTenantUID(user.getTenantUID());
	//        		assetFilters.add(new SearchFilter("uid", SearchFilter.Operator., uids));
	        }
        }

        if(StringUtils.isNotEmpty(queryAsset.getFunctionType())){
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
        if(!assetFilters.isEmpty()) {
        	 List<SearchFilter> userFilter = getFilter(user.getSiteUID());
        	 assetFilters.addAll(userFilter);
        	 return assetInfoRepository.findBySearchFilter(assetFilters); 
        }
       return new ArrayList<>();
    }
    
    public  List<SearchFilter> getFilter(String siteUID){
    	List<SearchFilter>filter = new ArrayList<SearchFilter>();
    	OrgInfo orgInfo = orgInfoRepository.getByUid(siteUID);
    	if(orgInfo.getOrgType() == 3){//院区
    		filter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
    	}else if(orgInfo.getOrgType() == 2){//医院
    		filter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
    	}else if(orgInfo.getOrgType() == 1){//集团
    		filter.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
    	}else{//租户
    		filter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, orgInfo.getUid()));
    	}
    	filter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
    	return filter;
    }
  
    
    public void addFilter(){
    	sourceAssets = getAssetList();
        targetAssets.forEach((item)->{
            if(sourceAssets.contains(item)){
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
        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetAssets.add((AssetInfo)item);
            }else{
                targetAssets.remove((AssetInfo)item);
            }
        }
    }
    
    @Transactional
    public void saveRel(){
    	List<RelRuleAsset> rels = new ArrayList<RelRuleAsset>(targetAssets.size());
    	List<String> deleteIds = new ArrayList<String>();
    	for (AssetInfo ai : targetAssets) {
    		RelRuleAsset rel = new RelRuleAsset();
    		rel.setAssetUID(ai.getUid());
    		rel.setRuleUID(pmOrderRuleUID);
    		rel.setCreatedDate(new Date());
    		rel.setTenantUID(ai.getTenantUID());
    		rels.add(rel);
    		deleteIds.add(ai.getUid());
		}
    	relRuleAssetRepository.deleteByAssetUIDIn(deleteIds);
    	relRuleAssetRepository.save(rels);
    	WebUtil.addSuccessMessage("配置成功！");
    	targetAssets.clear();
    }
    
    public void generatePmOrders() {
    	List<AssetRuleModel> models = new ArrayList<AssetRuleModel>();
    	for (AssetInfo ai : targetAssets) {
    		AssetRuleModel model = new AssetRuleModel();
    		model.setUid(ai.getUid());
    		models.add(model);
		}
    	//TODO invoke userCommonService
    	WebUtil.addSuccessMessage("配置成功！");
    	targetAssets.clear();
    }
    
    public List<PmOrderRule> getPmOrderRuleList(){
    	List<SearchFilter> assetFilters = new ArrayList<>();//getFilter(user.getSiteUID());
		assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, queryAsset.getTenantUID()));
		assetFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
		if(StringUtils.isNotEmpty(queryAsset.getFunctionType())){
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
		if(queryAsset.getLevel() != null) {
			if(queryAsset.getLevel() == 1) {//一级
				
			}else if(queryAsset.getLevel() == 2){//二级
				
			}else if(queryAsset.getLevel() == 3){//三级
				
			}
		}
		return pmOrderRuleRepository.findBySearchFilter(assetFilters);
//    	return pmOrderRuleRepository.findByTenantUIDAndStatus(user.getTenantUID(),1);
    }
    
    public String getStrDate(Date lastPmDate){
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
	
}
