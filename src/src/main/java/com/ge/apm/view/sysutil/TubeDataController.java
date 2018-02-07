package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TubeConfigRepository;
import com.ge.apm.dao.TubeDataRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TubeConfig;
import com.ge.apm.domain.TubeData;
import com.ge.apm.domain.UserAccount;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class TubeDataController extends JpaCRUDController<TubeData> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TubeDataRepository dao = null;
	private UserAccount user;
//	private List<AssetInfo> assetInfos;
//	private List<TubeConfig> configs;
	private AssetInfoRepository assetInfoRepository;
	private TubeConfigRepository tubeConfigRepository;
	private OrgInfoRepository orgInfoRepository;
	private Map<String,AssetInfo> assetMap;
	private Map<String,TubeConfig> configMap;
	
    @Override
    protected void init() {
        dao = WebUtil.getBean(TubeDataRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tubeConfigRepository = WebUtil.getBean(TubeConfigRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        assetMap = new HashMap<String,AssetInfo>(200);
        configMap = new HashMap<String,TubeConfig>(200);
//        assetInfos = new ArrayList<AssetInfo>(200);
//        configs = new ArrayList<TubeConfig>(200);
    }

    @Override
    protected TubeDataRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<TubeData> loadData(PageRequest pageRequest) {
    	assetMap.clear();
    	configMap.clear();
    	String tenantUid = user.getTenantUID();
    	PageRequest pr = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize());
    	Page<TubeData> datas = dao.findByCondition(tenantUid,pr);
    	if(datas.getTotalElements() > 0) {
    		List<TubeData> elements = datas.getContent();
    		List<String> uids = new ArrayList<String>();
    		for (TubeData tubeData : elements) {
    			uids.add(tubeData.getAssetUid());
		}
    		if(CollectionUtils.isNotEmpty(uids)) {
    			List<AssetInfo> assetInfos = assetInfoRepository.findByTenantUidAndUidIn(tenantUid,uids);
        		for (AssetInfo assetInfo : assetInfos) {
        			assetMap.put(assetInfo.getUid(), assetInfo);
			}
        		List<TubeConfig> configs = tubeConfigRepository.findbyTenantUidAndAssetUidIn(tenantUid,uids);
        		for (TubeConfig tubeConfig : configs) {
        			configMap.put(tubeConfig.getAssetUid() + "_" + tubeConfig.getSeriaNum(), tubeConfig);
			}
    		}
    	}
    	return datas;
    }

    @Override
    public List<TubeData> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    
    public AssetInfo getAssetByUid(String uid) {
    		return assetMap.get(uid);
    }
    
    public TubeConfig getConfigByUid(String uid,String sn) {
    		return configMap.get(uid +"_"+sn);
    }
    
    public String getAssetName(String uid) {
    		AssetInfo ai = getAssetByUid(uid);
    		if(ai != null) {
    			return ai.getName();
    		}
    		return null;
    }
    
    public String getAssetSN(String uid) {
		AssetInfo ai = getAssetByUid(uid);
		if(ai != null) {
			return ai.getFinancingNum();
		}
		return null;
    }
    
    public String getAssetSystemId(String uid) {
		AssetInfo ai = getAssetByUid(uid);
		if(ai != null) {
			return ai.getSystemId();
		}
		return null;
    }
    
    public String getAssetSiteName(String uid) {
		AssetInfo ai = getAssetByUid(uid);
		if(ai != null) {
			OrgInfo org = orgInfoRepository.getByUid(ai.getSiteUID());
			if(org != null) {
				return org.getName();
			}
		}
		return null;
    }
    
	public String getClinicalDeptName(String assetUid) {
		AssetInfo ai = getAssetByUid(assetUid);
		if (ai != null) {
			return ai.getClinicalDeptName();
		}
		return null;
	}
	
	public String getPatientExamDueDate(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config != null && config.getPatientExamDueDate() != null) {
			return new DateTime(config.getPatientExamDueDate()).toString("yyyy-MM-dd");
		}
		return null;
	}
	
	public String getMinDate(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config == null) {
			return null;
		}
		Long l1 = config.getPatientExamDueDate() == null ? Long.MAX_VALUE : config.getPatientExamDueDate().getTime();
		Long l2 = config.getTubeMASDueDate() == null ? Long.MAX_VALUE : config.getTubeMASDueDate().getTime();
		Long l3 = config.getTubeScanDueDate() == null ? Long.MAX_VALUE : config.getTubeScanDueDate().getTime();
		if(l1 == Long.MAX_VALUE && l2 == Long.MAX_VALUE && l3 == Long.MAX_VALUE) {
			return null;
		}
		Long min = l1 <= l2 ? l1 : l2;
		min = min <= l3 ? min :l3;
		if(min == Long.MAX_VALUE) {
			return null;
		}
		DateTime dt = new DateTime(min);
		return dt.toString("yyyy-MM-dd");
	}

	
	public String getTubeMas(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config != null && config.getTubeMASDueDate() != null) {
			return new DateTime(config.getTubeMASDueDate()).toString("yyyy-MM-dd");
		}
		return null;
	}
	
	public String getTubeScan(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config != null && config.getTubeScanDueDate() != null) {
			return new DateTime(config.getTubeScanDueDate()).toString("yyyy-MM-dd");
		}
		return null;
	}
	
	public Integer getMaxPatientExam(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config != null ) {
			return config.getMaxTotalPatientExam();
		}
		return null;
	}
	
	public Integer getMaxTubeMas(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config != null ) {
			return config.getMaxTotalTubeMAS();
		}
		return null;
	}
	
	public Integer getMaxTubeScan(String assetUid,String sn) {
		TubeConfig config = getConfigByUid(assetUid,sn);
		if(config != null ) {
			return config.getMaxTotalTubeScan();
		}
		return null;
	}
/*
    @Override
    public void onBeforeNewObject(TubeData object) {
    }
    
    @Override
    public void onAfterNewObject(TubeData object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(TubeData object) {
    }
    
    @Override
    public void onAfterUpdateObject(TubeData object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(TubeData object) {
    }
    
    @Override
    public void onAfterDeleteObject(TubeData object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(TubeData object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/
}