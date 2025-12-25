package com.ge.apm.view.uaa;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.uaa.AdminConsoleService;
import com.ge.apm.service.uaa.TenantInfoConfigService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import com.ge.healthcare.cn.common.cons.ApmFeatureVersion;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class TenantInfoController extends JpaCRUDController<TenantInfo> {
	private static final Logger log = LoggerFactory.getLogger(TenantInfoController.class);

	TenantInfoRepository dao;
	UserAccountRepository userAccountRepository;
	UserAccount loginUser;
	TenantInfoConfigService tenantConfigService;
	AssetInfoRepository assetInfoRepository;
	TableDefinitionRepository tbDao;
	OrgInfoRepository orgInfoDao;
	private ConfigCacheService configCacheService;

	boolean tempAlready = false;
	String type = "";

	@Getter@Setter
	List<UserAccount> activePersons;
	@Getter@Setter
	private List<ApmFeature> allFeatures;
	@Getter@Setter
	private List<ApmFeature> features;
	@Getter@Setter
	private Set<String> allSelectedMenuFeatures;
	@Getter@Setter
	private List<String> selectedMenuFeaturesAsset;
	@Getter@Setter
	private List<String> selectedMenuFeaturesApprove;
	@Getter@Setter
	private List<String> selectedMenuFeaturesInstall;
	@Getter@Setter
	private List<String> selectedMenuFeaturesSpecialAsset;
	@Getter@Setter
	private List<String> selectedMenuFeaturesParts;
	@Getter@Setter
	private List<String> selectedMenuFeaturesRepair;
	@Getter@Setter
	private List<String> selectedMenuFeaturesMaintain;
	@Getter@Setter
	private List<String> selectedMenuFeaturesMeasure;
	@Getter@Setter
	private List<String> selectedMenuFeaturesNurseSite;
	@Getter@Setter
	private List<String> selectedMenuFeaturesKnowledge;
	@Getter@Setter
	private List<String> selectedMenuFeaturesAdjust;
	@Getter@Setter
	private List<String> selectedMenuFeaturesAudit;
	@Getter@Setter
	private List<String> selectedMenuFeaturesPerformance;
	@Getter@Setter
	private List<String> selectedMenuFeaturesBudget;
	@Getter@Setter
	private List<String> selectedMenuFeaturesHisPerformance;
	@Getter@Setter
	private List<String> selectedMenuFeaturesReportCenter;
	@Getter@Setter
	private List<String> selectedMenuFeaturesAssetAccountDashboard;
	@Getter@Setter
	private List<String> selectedMenuFeaturesAuditConfig;
	@Getter@Setter
	private List<String> selectedMenuFeaturesMvsAsset;
	@Getter@Setter
	private List<String> selectedMenuFeaturesLogAudit;
	@Getter@Setter
	private List<String> selectedMenuFeaturesDeviceDataReview;
	@Getter@Setter
	private List<String> selectedMenuFeaturesSupplierTenant;
	@Getter@Setter
	private List<String> selectedMenuFeaturesConfig;

	@Getter@Setter
	private List<String> selectedMenuFeaturesConfigAssetGroup;
	@Getter@Setter
	private List<String> selectedMenuFeaturesPlatform;

	private boolean isTestTenant = false;
	private boolean isShowTestTenant = true;
	private static final Integer NORMAL_TENANT_STATUS = 0; //NORMALTenant
	private static final Integer TEST_TENANT_STATUS = -1; //TestTenant
	private static final Integer LX_USER_TYPE = -1; // lixiang user type

	@Getter@Setter
	private boolean disableCreateTemplateBt = false; // 如果编辑状态初是'通用版',添加模版按钮就设置为灰色

	public boolean createOrEdit() {
		if ("create".equalsIgnoreCase(type)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void init() {
		filterBySite = false;
		loginUser = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(TenantInfoRepository.class);
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		configCacheService = WebUtil.getBean(ConfigCacheService.class);
		tenantConfigService = WebUtil.getBean(TenantInfoConfigService.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		tbDao = WebUtil.getBean(TableDefinitionRepository.class);
		orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
		allFeatures = Arrays.asList(ApmFeature.values()).stream().collect(Collectors.toList());
		features = allFeatures;
		//判断是否是理想用户
		if(LX_USER_TYPE.equals(loginUser.getType())){
			this.isShowTestTenant = Boolean.FALSE;
		}else {
			this.isShowTestTenant = Boolean.TRUE;
		}
	}

	@Override
	protected TenantInfoRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<TenantInfo> loadData(PageRequest pageRequest) {
		selected = null;

		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
		if(LX_USER_TYPE.equals(loginUser.getType())){
			searchFilters.add(new SearchFilter("testStatus", SearchFilter.Operator.NE, TEST_TENANT_STATUS));
		}
		return dao.findBySearchFilter(searchFilters, pageRequest);
	}

	public static File[] getResourceFolderFiles(String folder) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(folder);
		String path = url.getPath();
		return new File(path).listFiles();
	}

	private HashMap<String, String> fhMap = new HashMap<String, String>();

	public void loadTemplateFile() {
		String rstr = "";
		fhMap = new HashMap<>();
		for (File f : getResourceFolderFiles("/templateFile/")) {
			try (FileInputStream in = new FileInputStream(f);
				 BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));){
				String str;
				while ((str = br.readLine()) != null) {
					rstr += str;
				}
				String fileName = f.getName();
				int num = fileName.substring(fileName.lastIndexOf(".")).length();
				fhMap.put(fileName.substring(0, fileName.length() - num), rstr);
				rstr = "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<TenantInfo> getItemList() {
		List<TenantInfo> tenantInfos;
		if (UserContextService.isSuperAdminWithPartnerAccount()) {
			tenantInfos = dao.findAllByTestStatusIsNullOrTestStatusNotOrderByName(-1);
		} else {
			tenantInfos = dao.getAllOrderByName();
		}

		return tenantInfos;

	}

	public boolean isIBTenant() {
		TenantInfo thisTenant = this.selected;
		return thisTenant.getId() == 46;
	}
	public void prepareCreate(String type) throws InstantiationException, IllegalAccessException {
		super.prepareCreate();
		selected.setHaveIbDevice(true);
		selected.setEnablesNewMenu(true);
		tenantConfigService.init();
		this.type = type;
        this.isTestTenant = Boolean.FALSE;
	}

	@Override
	public void prepareEdit() {
		super.prepareEdit();
		parseTenantConfig();
		this.type="edit";
	}

	public String editFeature(String tenantUid) {
		return UrlEncryptController.encodeUrlParam("FeatureSetting?tenantUid="+ tenantUid);
	}

	public void prepareEdit(String type) {
		super.prepareEdit();
		parseTenantConfig();
		this.type = type;
		if (tenantConfigService.getEdition().equals(TenantInfoConfigService.VERSION_COMMON)) {
			isGeneralVersion = true;
			tempAlready = true;
			disableCreateTemplateBt = false;
		} else if(tenantConfigService.getEdition().equals(TenantInfoConfigService.EXCELLENT_ENJOYMENT_A) ||
                tenantConfigService.getEdition().equals(TenantInfoConfigService.EXCELLENT_ENJOYMENT_B)){
            disableCreateTemplateBt = false;
            isGeneralVersion = false;
        }else {
			isGeneralVersion = false;
			disableCreateTemplateBt = true;
		}
		tempAlready = false;

		//判断是否是测试租户
		if(TEST_TENANT_STATUS.equals(selected.getTestStatus())){
			this.isTestTenant = Boolean.TRUE;
		}else{
			this.isTestTenant = Boolean.FALSE;
		}
		initActivePersons();
	}

	private void initActivePersons() {
		activePersons = null;
		if (StringUtils.isNotEmpty(selected.getActiveUsers())) {
			activePersons = new ArrayList<>();
			activePersons.addAll(userAccountRepository.getByIds(Arrays.stream(selected.getActiveUsers().split(","))
							.map(Integer::parseInt)
							.collect(Collectors.toSet())).stream().filter(u -> BooleanUtils.isFalse(u.getIsDeleted()) && BooleanUtils.isFalse(u.getIsDisable())).collect(Collectors.toList()));
			if (null != selected.getActiveUserId()) {
				Optional<UserAccount> userAccount = activePersons.stream()
						.filter(obj -> obj.getId().compareTo(selected.getActiveUserId()) == 0)
						.findFirst();
				if (!userAccount.isPresent()) {
					UserAccount account = userAccountRepository.findById(selected.getActiveUserId());
					if (BooleanUtils.isFalse(account.getIsDeleted()) && BooleanUtils.isFalse(account.getIsDisable())) {
						activePersons.add(account);
					}
				}
			}
		}
	}

	@Transactional
	@Override
	public void save() {
		if (CollectionUtils.isNotEmpty(activePersons)) {
			selected.setActiveUsers(activePersons.stream().map(obj -> String.valueOf(obj.getId()))
					.collect(Collectors.joining(",")));
		}
		if (type.equalsIgnoreCase("create")) {// 如果租户已经创建过则退出
			List<TenantInfo> byName = dao.getByName(this.selected.getName());
			if (byName.size() > 0) {
				WebUtil.addErrorMessage(byName.get(0).getName() + "租户已经创建过了");
				RequestContext.getCurrentInstance().addCallbackParam("tenantEstablished", true);
				return;
			}
			Optional<ApmFeatureVersion> features = Arrays.stream(ApmFeatureVersion.values()).filter(f -> f.name().equals(tenantConfigService.getEdition())).collect(Collectors.toSet()).stream().findFirst();
			selected.setFeatureList(JSON.toJSONString(features.get().getFeatureList()));
			selected.setEnablesNewMenu(true);
			selected.setEnablesMenuOrder(false);
		}
		this.selected.setTenantConfig(tenantConfigService.getJSONStr());
		log.info("save/updating... TenantInfo , tenantName:{},tenantConfig:{}", this.selected.getName(), this.selected.getTenantConfig());
		this.selected.setLastModifiedDate(new Date());
		super.save();
		if (!StringUtils.isEmpty(this.selected.getUid())) {
			OrgInfo tenantOrg = orgInfoDao.findFirstByTenantUIDAndOrgType(this.selected.getUid(), 0);
			if (tenantOrg != null) {
				tenantOrg.setName(this.selected.getName());
				tenantOrg.setShortName(this.selected.getName());
				orgInfoDao.save(tenantOrg);
			}
		}
		if (!isOpenNewMenuRoles() && !type.equalsIgnoreCase("create")) {
			analyseMenus(selected);
		}
		log.info("save/update TenantInfo successful, tenantName:{}", this.selected.getName());
	}

	@Transactional
	public void saveFeature() {
		selected.setLastModifiedDate(new Date());
		allSelectedMenuFeatures.clear();
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesAsset);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesApprove);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesInstall);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesSpecialAsset);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesParts);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesRepair);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesMaintain);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesMeasure);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesNurseSite);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesKnowledge);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesAdjust);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesAudit);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesPerformance);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesBudget);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesHisPerformance);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesReportCenter);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesAssetAccountDashboard);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesAuditConfig);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesMvsAsset);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesLogAudit);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesSupplierTenant);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesDeviceDataReview);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesConfig);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesConfigAssetGroup);
		allSelectedMenuFeatures.addAll(selectedMenuFeaturesPlatform);
		if (CollectionUtils.isNotEmpty(allSelectedMenuFeatures)) {
			selected.setFeatureList(JSON.toJSONString(allSelectedMenuFeatures));
		} else {
			WebUtil.addErrorMessage("租户功能模块不能为空已");
			return;
		}
		JSONObject jsonObject = JSON.parseObject(selected.getTenantConfig());
		jsonObject.remove("edition");
		jsonObject.put("edition", tenantConfigService.getEdition());
		selected.setTenantConfig(jsonObject.toJSONString());
		dao.save(selected);
		configCacheService.cleanTenantCacheByUid(selected.getUid());
	}

	List<TableDefinition> tbList = new ArrayList<TableDefinition>();

	public void addTemplateFile() {
		if (tempAlready) { // 该条件判断只能防止在修改租户对话框中反复'添加模版'，但是当'通用'改成'非通用'
							// 再改回'通用'这种情况下 添加模版还是会重复添加
			WebUtil.addSuccessMessage("该租户模版已经添加");
			RequestContext.getCurrentInstance().addCallbackParam("tmplateFilled", true);
			return;
		} else {
			if (tenantConfigService.getEdition().contains(TenantInfoConfigService.VERSION_COMMON) ||
					tenantConfigService.getEdition().contains(TenantInfoConfigService.EXCELLENT_ENJOYMENT_A) ||
					tenantConfigService.getEdition().contains(TenantInfoConfigService.EXCELLENT_ENJOYMENT_B)) {
				// 加载模版文件
				loadTemplateFile();
				// 将模版插入TableDefinition, 先从数据库里查有没有该模版，没有的话就将其加入tbList to be
				// saved
				tbList = new ArrayList<>();
				fhMap.entrySet().stream().forEach(item -> {
					// List<TableDefinition> td = tbDao.findTD(item.getKey(),
					// selected.getUid());
					// if (td.size() == 0) {
					TableDefinition tb = new TableDefinition();
					tb.setTenantUID(selected.getUid());
					tb.setInstitutionUID(selected.getUid());
					tb.setHospitalUID(selected.getUid());
					tb.setSiteUID(selected.getUid());
					tb.setStatus(TableDefinition.TableDefinitionStatus.Draft.toString());
					tb.setInstrumentKeyValues("{}");
					tb.setCreatedDate(new Date());
					tb.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
					tb.setName(item.getKey());
					tb.setSchema(item.getValue());
					tbList.add(tb);
					// }
				});
				if (tbList.size() > 0) {
					tbDao.save(tbList);
				}
				tbList.stream().forEach(item -> item.setBaseId(item.getId()));
				tbDao.save(tbList);
			}
			tempAlready = true;
			// 模版添加完后 将'添加模版'按钮变灰
			// disableCreateTemplateBt=true;
			WebUtil.addSuccessMessage("模版添加完成");
		}
	}

	@Override
	public void delete() {
		String uid = selected.getUid();
		boolean hasAssets = assetInfoRepository.findRecordByTenantUid(uid);
		if (hasAssets) {
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("hasAssets"));
			return;
		}
		boolean hasUsers = userAccountRepository.findRecordByTenantUid(uid);
		if (hasUsers) {
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("hasUsers"));
			return;
		}
		super.delete();
		configCacheService.cleanTenantCacheByUid(uid);
		log.info("delete tenantInfo successful, uid:{}", uid);
	}

	public void markDeleted() {
		selected.setIsDeleted(true);
		dao.save(selected);
		configCacheService.cleanTenantCacheByUid(selected.getUid());
	}

	@Override
	public String getErrorMessageForDuplicateKey(TenantInfo site) {
		return String.format(WebUtil.getMessage("DuplicateSiteName"), site.getName());
	}

	@Override
	public String getKeyFieldNameValue(TenantInfo site) {
		return WebUtil.getMessage("name") + "=" + site.getName();
	}

	public String getNameByUid(String uid)
	{
		if(StringUtils.isBlank(uid)) return "";
		TenantInfo tenantInfo = this.dao.findByUid(uid);
		return tenantInfo.getName();
	}

	@Override
	public void onAfterNewObject(TenantInfo tenant, boolean isOK) {
		if (!isOK) return;
		OrgInfo org = new OrgInfo();
		org.setSiteId(tenant.getId());

		// for new UUIDs
		org.setOrgLevel(0);
		org.setOrgType(0);
		org.setUid(tenant.getUid());
		org.setTenantUID(tenant.getUid());
		org.setInstitutionUID(tenant.getUid());
		org.setHospitalUID(tenant.getUid());
		org.setSiteUID(tenant.getUid());

		org.setShortName(tenant.getName());
		org.setName(tenant.getName());

		OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
		orgDao.save(org);

		String config = tenantConfigService.getJSONStr();

		//通用版本和优享A默认导入8条模板
		if (config.contains(TenantInfoConfigService.VERSION_COMMON) || config.contains(TenantInfoConfigService.EXCELLENT_ENJOYMENT_A)) {
			List<TableDefinition> tbList = new ArrayList<TableDefinition>();
			// 加载模版文件
			loadTemplateFile();
			// 将模版插入TableDefinition, 先从数据库里查有没有该模版，没有的话就将其加入tbList to be saved
			fhMap.entrySet().stream().forEach(item -> {
				List<TableDefinition> td = tbDao.findTD(item.getKey(), tenant.getUid());
				if (td.size() == 0) {
					TableDefinition tb = new TableDefinition();
					tb.setTenantUID(tenant.getUid());
					tb.setInstitutionUID(tenant.getUid());
					tb.setHospitalUID(tenant.getUid());
					tb.setSiteUID(tenant.getUid());
					tb.setStatus(TableDefinition.TableDefinitionStatus.Draft.toString());
					tb.setInstrumentKeyValues("{}");
					tb.setCreatedDate(new Date());
					tb.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
					tb.setName(item.getKey());
					tb.setSchema(item.getValue());
					tbList.add(tb);
				}
			});
			if (tbList.size() > 0) {
				tbDao.save(tbList);
			}
			tbList.stream().forEach(item -> item.setBaseId(item.getId()));
			tbDao.save(tbList);
		}

	}

	@Transactional
	public void onAfterUpdateObject(TenantInfo tenant, boolean isOK) {
		/* 如果由非通用版改成通用版，查找tabledef表(tenantUid,name)有没有存在模版，没有的话添加 */
		if (!isOK) return;
		if (selected.getIsEnabled() != null && selected.getIsEnabled()) {
			// 如果超级管理员激活某个租户，则激活该租户下的所有用户
			userAccountRepository.updateMembersStatus(selected.getUid());
		} else {
			// 如果超级管理员关闭某个租户，则关闭该租户下的所有用户
			userAccountRepository.deactiveTenantMembersStatus(selected.getUid());
		}
	}

	@Override
	public void onBeforeNewObject(TenantInfo tenant) {
		tenant.setLastModifiedDate(new Date());
		tenant.setCreatedDate(new Date());
		tenant.setTestStatus(NORMAL_TENANT_STATUS);
		tenant.setIsEnabled(false);
		super.onBeforeNewObject(tenant);
	}

	@Override
	protected boolean onPersistException(Exception ex, TenantInfo site) {
		if (ex.getClass().equals(DataIntegrityViolationException.class)) {
			DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
			if (exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")) {
				String errMessage = WebUtil.getMessage("ForeighKeyErrorWithSiteId");
				WebUtil.addErrorMessage(errMessage);

				return true;
			}
		}

		return false;
	}

	public void onBeforeSave(TenantInfo tenantInfo) {

		if (tenantInfo.getIsEnabled() && tenantInfo.getActiveTime() == null) {
			tenantInfo.setActiveTime(new Date());
			tenantInfo.setActiveType(0);
			tenantInfo.setActiveUserId(loginUser.getId());
		}
		if (!tenantInfo.getIsEnabled() && tenantInfo.getActiveTime() != null) {
			tenantInfo.setActiveTime(null);
		}

		if(this.isTestTenant){
			tenantInfo.setTestStatus(TEST_TENANT_STATUS);
		}else{
            tenantInfo.setTestStatus(NORMAL_TENANT_STATUS);
        }

	}

	public boolean isGeneralVersion;

	public void onVersionChange() {
		List<String> showTemplateList = Lists.newArrayList(TenantInfoConfigService.VERSION_COMMON,TenantInfoConfigService.ZKB_VERSION,
                TenantInfoConfigService.EXCELLENT_ENJOYMENT_A,TenantInfoConfigService.EXCELLENT_ENJOYMENT_B);
		if (showTemplateList.contains(tenantConfigService.getEdition())) {
			disableCreateTemplateBt = Boolean.FALSE;
		} else {
			disableCreateTemplateBt = Boolean.TRUE;
		}
		tenantConfigService.resetVersion();
	}

	private void parseTenantConfig() {
		String configJsonStr = this.selected.getTenantConfig();
		buildFeature(selected.getFeatureList());
		tenantConfigService.parseStr(configJsonStr);
	}

	private void buildFeature(String featureList) {
		if (StringUtils.isNotEmpty(featureList)) {
			allSelectedMenuFeatures = ((List<String>) JSON.parse(featureList)).stream().collect(Collectors.toSet());
			selectedMenuFeaturesAsset = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("ASSET")).collect(Collectors.toList());
			selectedMenuFeaturesApprove = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("MULTILEVEL")).collect(Collectors.toList());
			selectedMenuFeaturesInstall = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("INSTALL")).collect(Collectors.toList());
			selectedMenuFeaturesSpecialAsset = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("SPECIAL")).collect(Collectors.toList());
			selectedMenuFeaturesParts = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("PARTS")).collect(Collectors.toList());
			selectedMenuFeaturesRepair = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("REPAIR")).collect(Collectors.toList());
			selectedMenuFeaturesMaintain = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("MAINTAIN")).collect(Collectors.toList());
			selectedMenuFeaturesMeasure = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("MEASURE")).collect(Collectors.toList());
			selectedMenuFeaturesNurseSite = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("NURSESITE")).collect(Collectors.toList());
			selectedMenuFeaturesKnowledge = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("KNOWLEDGE")).collect(Collectors.toList());
			selectedMenuFeaturesAdjust = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("ADJUST")).collect(Collectors.toList());
			selectedMenuFeaturesAudit = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("AUDIT")).collect(Collectors.toList());
			selectedMenuFeaturesPerformance = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("PERFORMANCE")).collect(Collectors.toList());
			selectedMenuFeaturesBudget = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("BUDGET")).collect(Collectors.toList());
			selectedMenuFeaturesHisPerformance = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("HISPERFORMANCE")).collect(Collectors.toList());
			selectedMenuFeaturesReportCenter = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("REPORT_CENTER")).collect(Collectors.toList());
			selectedMenuFeaturesAssetAccountDashboard = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("ASSET_ACCOUNT_DASHBOARD")).collect(Collectors.toList());
			selectedMenuFeaturesAuditConfig = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("AUDIT_CONFIG")).collect(Collectors.toList());
			selectedMenuFeaturesMvsAsset = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("MVS_ASSET")).collect(Collectors.toList());
			selectedMenuFeaturesLogAudit = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("LOG_AUDIT")).collect(Collectors.toList());
			selectedMenuFeaturesSupplierTenant = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("SUPPLIER_TENANT")).collect(Collectors.toList());
			selectedMenuFeaturesDeviceDataReview = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("DEVICEDATAPREVIEW")).collect(Collectors.toList());
			selectedMenuFeaturesConfig = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("CONFIG") && !f.equals("CONFIG_ASSET_TYPE_GROUP") ).collect(Collectors.toList());
			selectedMenuFeaturesConfigAssetGroup = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("CONFIG_ASSET_TYPE_GROUP")).collect(Collectors.toList());
			selectedMenuFeaturesPlatform = allSelectedMenuFeatures.stream().filter(f -> f.startsWith("PLATFORM")).collect(Collectors.toList());
		}
	}

	public boolean isOpenNewMenuRoles() {
		if (selected == null) {
			return false;
		}
		return selected.getEnablesNewMenu() && StringUtils.isNotEmpty(selected.getFeatureList());
	}

	public void changeListener() {
		Optional<ApmFeatureVersion> features = Arrays.stream(ApmFeatureVersion.values()).filter(f -> f.name().equals(tenantConfigService.getEdition())).collect(Collectors.toSet()).stream().findFirst();
		buildFeature(JSON.toJSONString(features.get().getFeatureList()));
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TenantInfoConfigService getTenantConfigService() {
		return tenantConfigService;
	}

	public void setTenantConfigService(TenantInfoConfigService tenantConfigService) {
		this.tenantConfigService = tenantConfigService;
	}

	public boolean isTestTenant() {
		return isTestTenant;
	}

	public void setTestTenant(boolean testTenant) {
		isTestTenant = testTenant;
	}

	public boolean isShowTestTenant() {
		return isShowTestTenant;
	}

	public void setShowTestTenant(boolean showTestTenant) {
		isShowTestTenant = showTestTenant;
	}

	private void analyseMenus(TenantInfo tenantInfo) {
		WebUtil.getBean(AdminConsoleService.class).analyseMenu(tenantInfo);
	}

	public List<UserAccount> getUserAccounts() {
		List<UserAccount> userAccounts = userAccountRepository.findByTenantUID(selected.getUid());
		userAccounts = userAccounts.stream().filter(u -> BooleanUtils.isFalse(u.getIsDeleted()) && BooleanUtils.isFalse(u.getIsDisable())).collect(Collectors.toList());
		if (selected != null && selected.getActiveUserId() != null) {
			Optional<UserAccount> userAccount = userAccounts.stream()
					.filter(obj -> obj.getId().compareTo(selected.getActiveUserId()) == 0)
					.findFirst();
			if (!userAccount.isPresent()) {
				UserAccount account = userAccountRepository.findById(selected.getActiveUserId());
				if (BooleanUtils.isFalse(account.getIsDeleted()) && BooleanUtils.isFalse(account.getIsDisable())) {
					userAccounts.add(account);
				}
			}
		}
		return userAccounts;
	}

	public String getNameWithOrgName(UserAccount user) {
		OrgInfo org = orgInfoDao.getByUid(user.getOrgInfoUID());
		if(4 == org.getOrgType()){
			return user.getName() + "-" + org.getParentOrg().getName() + "-" + org.getName();
		}
		return user.getName() + "-" + org.getName();
	}

	public boolean newVersionTenant() {
		if (ObjectUtils.isEmpty(selected)) {
			return true;
		} else {
			return (selected.getType() != 0 && selected.getType() != 2);
		}
	}
}
