package com.ge.apm.service.uaa;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.TenantOptionConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import webapp.framework.web.WebUtil;

import java.util.List;
import java.util.Optional;

@Component
public class UpdateTenantFeatureService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	UserAccountMapper userAccountMapper;
	
	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	AssetInfoRepository assetInfoRepository;
	
	@Autowired
	TenantInfoRepository tenantInfoRepository;
	
	@Autowired
	OrgInfoRepository orgInfoRepository;

	@Autowired
	UserOrgInfoRepository userOrgDao;
	
	@Autowired
	UserOrgInfoRepository userOrgInfoRepository;
	
	@Autowired
	SysRoleRepository sysRoleRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;

	@Autowired
	CustomRoleRepository customRoleRepository;
	
	@Autowired
	NurseSiteMaintainRepository nurseSiteMaintainRepository;
	
	@Autowired
	NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
	
	@Autowired
	NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
	
	@Autowired
	NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
	
	@Autowired
	AssetLifecycleEventRepository assetLifecycleEventRepository;
	
	@Autowired
	NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;
	
	@Autowired
	NurseSiteMaintainService nurseSiteMaintainService;
	
	@Autowired
	I18nMessageRepository i18nRepository;
	
	@Autowired
	BatchJobRepository batchJobRepository;
	
	@Autowired
    RestTemplate template;
	
	@Autowired
	BlobFileService blobFileService;
	
	@Autowired
	BlobObjectRepository blobObjectRepository;

	@Autowired
	private AssetHistoryRepository assetHistoryRepository;

	@Autowired
	private WorkflowConfigRepository workflowConfigRepository;

	@Autowired
	private ConfigItemRepository configItemRepository;

	@Autowired
	private ConfigItemValueRepository configItemValueRepository;

	@Autowired
	private TenantOptionConfigRepository tenantOptionConfigRepository;

	@Autowired
	private ConfigService configService;

	private static final String MSG_TYPE_VERSION = "tenantVersionMenu";

	public boolean enableAssetPreview(TenantInfo tenantInfo) {
		List<WorkflowConfig> bySiteUID = workflowConfigRepository.findBySiteUID(tenantInfo.getUid());
		if (bySiteUID == null || bySiteUID.isEmpty()) {
			return false;
		}
		WorkflowConfig workflowConfig = bySiteUID.get(0);
		return workflowConfig.getEnableAssetPreview() != null && workflowConfig.getEnableAssetPreview();
	}

	public boolean isAllowAssetBatchUpdate(TenantInfo tenantInfo) {
		Optional<TenantOptionConfig> tenantOptionConfigOptional = tenantOptionConfigRepository.findByTenantUid(tenantInfo.getUid());
		if (!tenantOptionConfigOptional.isPresent()) {
			return false;
		}
		TenantOptionConfig tenantOptionConfig = tenantOptionConfigOptional.get();
		if (null == tenantOptionConfig.getAllowAssetBatchUpdate()) {
			return false;
		}
		return tenantOptionConfig.getAllowAssetBatchUpdate();
	}

	public boolean needScrappedControl(TenantInfo tenantInfo){
		WorkflowConfig workflowConfig = Optional.ofNullable(workflowConfigRepository.findFirstBySiteUID(tenantInfo.getUid())).orElse(new WorkflowConfig());
		return BooleanUtils.isTrue(workflowConfig.getEnableScrappedControl());
	}

	public Boolean showSupplierViewAndEditFieldsTable(TenantInfo tenantInfo) {
		Optional<TenantOptionConfig> tenantOptionConfigOptional = tenantOptionConfigRepository.findByTenantUid(tenantInfo.getUid());
		if (!tenantOptionConfigOptional.isPresent()) return false;
		TenantOptionConfig tenantOptionConfig = tenantOptionConfigOptional.get();
		return BooleanUtils.isTrue(tenantOptionConfig.getActiveSupplier());
	}

	public Boolean hasCurrentTenantName(TenantInfo tenantInfo) {
		if("云南省阜外心血管病医院".equals(tenantInfo.getName().trim())){
			return Boolean.TRUE;
		}
		return false;
	}


}
