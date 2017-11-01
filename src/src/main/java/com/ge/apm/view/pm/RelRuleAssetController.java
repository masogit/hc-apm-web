package com.ge.apm.view.pm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.RelRuleAssetRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.QueryAsset;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class RelRuleAssetController extends JpaCRUDController<RelRuleAsset>{

	private static final long serialVersionUID = -1L;
    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
	private UserAccount user;
    private AssetInfoRepository assetInfoRepository;
	private QueryAsset queryAsset;
	private RelRuleAssetRepository relRuleAssetRepository;
	private String pmOrderRuleUID;
	private PmOrderRuleRepository pmOrderRuleRepository;
	
	@Override
    protected void init() {
		this.filterByHospital = false;
		this.filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        targetAssets = new ArrayList<AssetInfo>();
        queryAsset = new QueryAsset();
        queryAsset.setTenantUID(user.getTenantUID());
        getAssetsListByTenantUid();
    }
	
    
    public void getAssetsListByTenantUid() {
        sourceAssets = assetInfoRepository.findByTenantUID(user.getTenantUID());
        targetAssets.forEach((item)->{
            if(sourceAssets.contains(item)){
                sourceAssets.remove(item);
            }
        });
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }
    
    public List<AssetInfo> getAssetList() {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, queryAsset.getTenantUID()));
        if(null != queryAsset.getFunctionType()){
        	assetFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, queryAsset.getFunctionType()));
        }
        if (null != queryAsset.getName()) {
            assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, queryAsset.getName()));
        }
        if (null != queryAsset.getManufacture()) {
            assetFilters.add(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, queryAsset.getManufacture()));
        }
        if (null != queryAsset.getAssetGroup()) {
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        return assetInfoRepository.findBySearchFilter(assetFilters);
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
	protected GenericRepository<RelRuleAsset> getDAO() {
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
    		rels.add(rel);
    		deleteIds.add(ai.getUid());
		}
    	relRuleAssetRepository.deleteByAssetUIDIn(deleteIds);
    	relRuleAssetRepository.save(rels);
    	WebUtil.addSuccessMessage("配置成功！");
    	targetAssets.clear();
    }
    
    public List<PmOrderRule> getPmOrderRuleList(){
    	return pmOrderRuleRepository.findByTenantUIDAndStatus(user.getTenantUID(),1);
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
	
	
	
}
