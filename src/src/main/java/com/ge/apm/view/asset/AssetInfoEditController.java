package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.domain.pure.CustomConfigAssetField;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.approval.WxMessageService;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.constant.UsageStatusConstant;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.*;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.component.AssetTypeGroupTreeComponentController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.DataSourceEnum;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.healthcare.cn.common.llm.EquipmentNameplate;
import com.ge.healthcare.cn.common.onnx.RiskLevel;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@Slf4j
public class AssetInfoEditController extends JpaCRUDController<AssetInfo> {

    private CustomConfigService ccService;
    private AssetInfoRepository dao;
    private MvsAssetInfoRepository mvsAssetInfoDao;
    private UserCommonService ucService;
    private BlobFileService blobService;
    private QrCodeService qrCodeService;
    private AssetInfoMgrService aimService;
    private AssetCreateService acService;
    private GbCodeService gbCodeService;
    private UaaService uaaService;
    private AssetDepreciationService assetDepreciationService;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgDao;
    private GbCodeRepository gbDao;
    private AssetInfoService assetInfoService;
    private SupplierController supplierController;
    private UserAccountRepository userDao;
    private SupplierRepository supplierDao;
    private AutoCompleteService autoCompleteService;
    private OrgInfoTreeService orgInfoTreeService;
    private AssetHistoryService assetHistoryService;
    private MvsUtil mvsUtil;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private MedicalInstrumentsClassificationCompCtrl medicalInstrumentsClassificationCompCtrl;
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;
    private RelRuleAssetRepository relRuleAssetRepository;
    private NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;
    private NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;
    private NurseSiteMaintainConfigJNRepository nurseSiteMaintainConfigJNRepository;
    private NurseSiteMaintainConfigJNItemRepository nurseSiteMaintainConfigJNItemRepository;
    private AssetTypeGroupTreeComponentController assetTypeGroupTreeComponentController;
    private AssetTypeGroupRepository assetTypeGroupRepository;

    private BuildManagementRepository buildManagementRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private AssetScrapRepository assetScrapRepository;
    private SpecialAssetInfoService specialAssetInfoService;

    private Boolean terminate = false;
    private CustomConfigObject customConfig;
    private List<AssetInfo> parentAssetList;
    private List<OrgInfo> clinicalDeptList;

    private V2_WorkOrderService woService;

    private WorkflowService workflowService;

    private V2_WorkOrderRepository v2WorkOrderRepository;
    private V2_PmOrderRepository v2PmOrderRepository;

    private NurseSiteMaintainRepository nurseSiteMaintainRepository;
    private NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

    private NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

    private SysRoleRepository sysRoleRepository;

    private MasterDataService masterDataService;
    private AssetFileImportSupportingService supportingService;

    @Getter
    @Setter
    private Map<String, Boolean> assetInfoRequireField = new HashMap<>();

    @Getter
    @Setter
    private EquipmentNameplate equipmentNameplate = new EquipmentNameplate();

    @Getter
    @Setter
    private Map<String, String> result;

    private RestTemplate restTemplate;

    private MicroServiceUtil microServiceUtil;

    public List<BuildManagement> getManagementList() {
        return managementList;
    }

    @Getter
    @Setter
    private Map<String, Boolean> useValue = new HashMap<>();

    private FileDownloadService fileDownloadService;

    public List<String> managementListString(String query) {
        if (CollectionUtils.isNotEmpty(managementList)) {
            List<String> list = managementList.stream().map(BuildManagement::getName).filter(item -> item.contains(query)).collect(Collectors.toList());
            Map<String, List<BuildManagement>> collect = managementList.stream().collect(Collectors.groupingBy(BuildManagement::getName));
            managementMap = collect;
            return list;
        }
        return new ArrayList<>();

    }

    public void setManagementList(List<BuildManagement> managementList) {
        this.managementList = managementList;
    }

    private List<BuildManagement> managementList = new ArrayList<>();
    private Map<String, List<BuildManagement>> managementMap = new HashMap<>();
    private List<UserAccount> ownerList;
    private List<I18nMessage> code68List;

    private AssetTagsMapper assetTagsMapper;
    private AssetTagsRepository assetTagsDao;
    private List<ZkbFileDirectory> subZkbFileDirectoryList = null;
    SysAuthGroupRepository authGroupDao;
    //    @Getter
//    @Setter
//    private List<Supplier> supplierList = Collections.emptyList();
//    @Getter
//    @Setter
//    private List<Supplier> manuList = Collections.emptyList();
//    @Getter
//    @Setter
//    private List<Supplier> mantainList = Collections.emptyList();
    private List<UserAccount> clinicalOwnerList;
    private List<V2_BlobObject> attachements;
    private ConfigService configService;
    private boolean resultStatus;
    private WorkflowConfig config;
    private String qrCode;
    private String gbCodeName;
    private GbCode selectedGb;
    private TreeNode gbTree;
    private TreeNode orgTree;
    private QrCodeLib createRequest;


    private boolean assetGroupSecondarySupport = false;
    private List<I18nMessage> assetGroupSecondaryList;

    private Map<String, Object> selectedObjectMap = new HashMap();
    private List<String> readableFieldsList;
    private List<String> editableFieldsList;

    private MvsAssetInfo mvsAssetInfo = new MvsAssetInfo();


    @Getter
    @Setter
    private Supplier newSupplier;

    @Getter
    @Setter
    private int assetLifecyleStatus;// 0、初始状态，其他对应资产status
    @Setter
    @Getter
    private List<String> rejectTextHistory;
    @Getter
    @Setter
    private String rejectText;
    @Setter
    @Getter
    private boolean isStatus;
    private boolean isStatusAssetHistory;
    @Setter
    @Getter
    private boolean countyArea = false;

    @Getter
    @Setter
    private List<V2_BlobObject> pictureList;
    @Setter
    @Getter
    private List<V2_BlobObject> audioList;
    @Setter
    @Getter
    private List<String> commentsList;
    @Setter
    @Getter
    private String[] selectedPictures;

    AssetLifecyleService assetLifecyleService;
    private String qr_Code = null;
    private String siteUid = null;
    //如果无图片 就显示请上传图片
    private boolean uploadFlag;
    @Getter
    private String uploadUrl = "/portal/uploadPic.png";
    @Getter
    private List<JSONObject> changeHistoryList = new ArrayList<>();

    /**
     * 目录关系service
     */
    private ZkbFileDirectoryService zkbFileDirectoryService;

    private UserContextService userContextService;

    private AssetScrapService assetScrapService;
    private AssetDepartmentConvertService assetDepartmentConvertService;

    private WxMessageService wxMessageService;

    private MqUtils mqUtils;

    //设备状态
    @Getter
    List<I18nMessage> assetStatusList = Collections.emptyList();

    @Getter
    private Boolean unCheckedNursesiteMaintainSite = false;
    @Getter
    private Boolean toDeleteNurseMaintainSite = false;
    private List<AssetInfo> unCheckNurseSiteMaintain = Collections.emptyList();

    public void toDelete() {
        toDeleteNurseMaintainSite = true;
    }

    @Getter
    @Setter
    private PmOrderRule pmOrderRuleTwoLevel;
    @Getter
    @Setter
    private PmOrderRule pmOrderRuleThreeLevel;
    @Getter
    @Setter
    private List<NurseSiteMaintainConfig> listSelectedNurseSiteMaintainConfig;
    @Getter
    @Setter
    private NurseSiteMaintainConfigJN selectedNurseSiteMaintainConfigJN;

    // 签名相关
    @Getter
    @Setter
    private String signatureContent;
    @Getter
    private StreamedContent signatureContentStream;
    @Getter
    private ConfigItemService configItemService;
    @Getter
    private String signatureId;
    @Getter
    private boolean newSignature = true;

    // TreeNodeName Filter
    private String treeNodeNameFilter;

    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }

    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

    private boolean approval = false;
    private String applyReason;
    private String approvalReason;
    private String equipmentDisposalMethod;
    private String customDisposalMethod;
    private String assetUploadFileFormats;
    @Getter
    @Setter
    private String inDeptUid;
    @Getter
    @Setter
    private String inDeptName;

    private boolean showCustomDisposalMethod;

    private String locationName;
    private UserAccount loginUser;

    private SpecialAssetInfo specialAssetInfo;


    private String type = null;
    @Getter
    @Setter
    private UdidInfoForm udidInfo;
    private UdidInfoService udidInfoService;
    private SupplierService supplierService;
    @Getter
    @Setter
    private Supplier newUdidSupplier;

    @Override
    protected void init() {
        sysRoleRepository = WebUtil.getBean(SysRoleRepository.class);

        isStatus = false;
        dao = WebUtil.getBean(AssetInfoRepository.class);
        mvsAssetInfoDao = WebUtil.getBean(MvsAssetInfoRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        configService = WebUtil.getBean(ConfigService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetDepreciationService = WebUtil.getBean(AssetDepreciationService.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        mvsUtil = WebUtil.getBean(MvsUtil.class);
        assetLifecyleService = WebUtil.getBean(AssetLifecyleService.class);
        assetTagsMapper = WebUtil.getBean(AssetTagsMapper.class);
        assetTagsDao = WebUtil.getBean(AssetTagsRepository.class);
        zkbFileDirectoryService = WebUtil.getBean(ZkbFileDirectoryService.class);
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        supportingService = WebUtil.getBean(AssetFileImportSupportingService.class);
        authGroupDao = WebUtil.getBean(SysAuthGroupRepository.class);
        medicalInstrumentsClassificationCompCtrl = WebUtil.getBean(MedicalInstrumentsClassificationCompCtrl.class);
        medicalInstrumentsClassificationCompCtrl.registerController(this);
        userContextService = WebUtil.getBean(UserContextService.class);
        mqUtils = WebUtil.getBean(MqUtils.class);
        assetInfoBulkEditCheckService = WebUtil.getBean(AssetInfoBulkEditCheckService.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        nurseSiteMaintainConfigRepository = WebUtil.getBean(NurseSiteMaintainConfigRepository.class);
        nurseSiteMaintainConfigItemRepository = WebUtil.getBean(NurseSiteMaintainConfigItemRepository.class);
        nurseSiteMaintainConfigJNRepository = WebUtil.getBean(NurseSiteMaintainConfigJNRepository.class);
        nurseSiteMaintainConfigJNItemRepository = WebUtil.getBean(NurseSiteMaintainConfigJNItemRepository.class);
        assetTypeGroupTreeComponentController = WebUtil.getBean(AssetTypeGroupTreeComponentController.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        assetScrapService = WebUtil.getBean(AssetScrapService.class);
        assetDepartmentConvertService = WebUtil.getBean(AssetDepartmentConvertService.class);
        buildManagementRepository = WebUtil.getBean(BuildManagementRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        assetScrapRepository = WebUtil.getBean(AssetScrapRepository.class);
        wxMessageService = WebUtil.getBean(WxMessageService.class);
        specialAssetInfoService = WebUtil.getBean(SpecialAssetInfoService.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        v2PmOrderRepository = WebUtil.getBean(V2_PmOrderRepository.class);
        v2WorkOrderRepository = WebUtil.getBean(V2_WorkOrderRepository.class);
        nurseSiteMaintainItemRepository = WebUtil.getBean(NurseSiteMaintainItemRepository.class);
        nurseSiteMaintainDataRepository = WebUtil.getBean(NurseSiteMaintainDataRepository.class);
        nurseSiteMaintainRepository = WebUtil.getBean(NurseSiteMaintainRepository.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        restTemplate = WebUtil.getBean(RestTemplate.class);
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);
        countyArea = userContextService.isCountyArea();
        assetTypeGroupTreeComponentController.registerController(this);
        configItemService = WebUtil.getBean(ConfigItemService.class);
        udidInfoService = WebUtil.getBean(UdidInfoService.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        approval = Boolean.parseBoolean((String) UrlEncryptController.getValueFromMap(encodeStr, "approval"));
        qr_Code = WebUtil.getRequestParameter("qrCode");
        siteUid = WebUtil.getRequestParameter("siteUid");
        createRequest = acService.getCreateRequest(qr_Code, siteUid);
        loginUser = UserContextService.getCurrentUserAccount();
        if (createRequest != null) {
            qrCode = qr_Code;
            isStatus = true;
            createAssetInfo();
            if (selected.getAssetClassification32() != null) {
                selectItemDto = new SelectItemDto(selected.getAssetClassification32(), null);
            }
            assetTypeGroupTreeComponentController.onServerEvent(AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE, this.selected.getAssetType() + "#" + this.selected.getTenantUID());
            rejectTextHistory = new ArrayList();
            if (null != createRequest.getFeedback()) {
                Collections.addAll(rejectTextHistory, createRequest.getFeedback().split("//"));
            }
            commentsList = new ArrayList();
            if (null != createRequest.getComment()) {
                Collections.addAll(commentsList, createRequest.getComment().split("\\|"));
            }
            pictureList = acService.getQrCodePictureList(createRequest.getUid());
            audioList = acService.getQrCodeAudioList(createRequest.getUid());
            //默认图片全选
            selectedPictures = new String[pictureList.size()];
            for (int i = 0; i < pictureList.size(); i++) {
                selectedPictures[i] = pictureList.get(i).getObjectStorageId();
            }

        }

        if ("Create".equalsIgnoreCase(actionName)) {
            isStatusAssetHistory = false;
            try {
                prepareCreate();
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(loginUser.getSiteUID()));
                changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
                assetLifecyleStatus = 0;
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
            specialAssetInfo = new SpecialAssetInfo();
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            isStatusAssetHistory = true;
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(dao.findByUid(selectedId));
            if (selected.getAssetClassification32() != null) {
                selectItemDto = new SelectItemDto(selected.getAssetClassification32(), null);
            }
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            terminate = Boolean.parseBoolean(terminateStr);
            prepareEdit();
            beforeEdit();
            assetLifecyleStatus = selected.getStatus();

            List<String> existingTags = assetTagsMapper.getTagsByAsset(this.selected.getUid());
            this.selected.setTags(existingTags);

            changeHistoryList = assetHistoryService.getAssetChangeHistoryByJSON(selected.getUid());
            uploadFlag = getAttachList(selected.getUid(), "1").isEmpty();
            selected.setAssetUsedLifecycle(AssetInfoService.getAssetUsedLifecycle(selected.getInstallDate(), selected.getLifecycle()));
            assetTypeGroupTreeComponentController.onServerEvent(AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE, this.selected.getAssetType() + "#" + this.selected.getTenantUID());
            assetInfoService.setSupplier(ListUtil.newArrayList(selected));
            if (Boolean.TRUE.equals(selected.isSpecialAsset())) {
                specialAssetInfo = specialAssetInfoService.findByAssetId(selected.getId());
            }
        } else if ("Copy".equalsIgnoreCase(actionName)) {
            isStatusAssetHistory = false;
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            AssetInfo assetInfo = dao.findByUid(selectedId);

            try {
                prepareCreate();
                String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
                terminate = Boolean.parseBoolean(terminateStr);
                assetInfoCopySelected(assetInfo, selected);
                OrgInfo orgInfo = orgDao.findByUid(assetInfo.getSiteUID());
                selectedObjectMap.put("selectedOrg", orgInfo);
//                changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
                selected.setSiteId(orgInfo.getSiteId());
                selected.setHospitalId(orgInfo.getId());

                selected.setTenantUID(orgInfo.getTenantUID());
                selected.setHospitalUID(orgInfo.getHospitalUID());
                selected.setInstitutionUID(orgInfo.getInstitutionUID());
                selected.setSiteUID(orgInfo.getSiteUID());
                config = configService.findConfigBySite(selected.getSiteUID());
                beforeEdit();
            } catch (Exception e) {
                logger.error("AssetInfoEditController copy error: " + e);
            }

        }
        type = (String) UrlEncryptController.getValueFromMap(encodeStr, "type");

        if (StringUtils.isNotBlank(type) && "SpecialAsset".equalsIgnoreCase(type)) {
            if (specialAssetInfo == null) {
                specialAssetInfo = specialAssetInfoService.findByAssetId(selected.getId());
            }
            selected.setSpecialAsset(true);
            specialAssetInfo.setAssetId(selected.getId());
            specialAssetInfo.setAssetUid(selected.getUid());
            specialAssetInfo.setHospitalUID(selected.getHospitalUID());
            specialAssetInfo.setTenantUID(selected.getTenantUID());
            specialAssetInfo.setInstitutionUID(selected.getInstitutionUID());
            specialAssetInfo.setSiteUID(selected.getSiteUID());
            specialAssetInfo.setCreatedDate(new Date());
            specialAssetInfo.setCreatedByUid(loginUser.getUid());
            specialAssetInfo.setLastModifiedByUid(loginUser.getUid());
            specialAssetInfo.setLastModifiedDate(new Date());
        }
        if (selected == null) {
            try {
                prepareCreate();
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(loginUser.getSiteUID()));
                changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ownerList = new ArrayList<>();
        ownerList = ucService.getWorkerList(selected.getId(), "");
        config = configService.findConfigBySite(selected.getSiteUID());
        ccService.cleanCurrentCustomConfigCache(loginUser.getSiteUID());
        customConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        editableFieldsList = ccService.getEditAssetFieldsByRoles(customConfig, UserContextService.getRoles());
//        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
//        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
//        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        //新增时不显示创建人
        if ("Create".equalsIgnoreCase(actionName)) {
            if (readableFieldsList.contains("createdBy")) {
                readableFieldsList.remove("createdBy");
            }
            readableFieldsList.remove("assetUsedLifecycle");
            readableFieldsList.remove("assetLifecycleDeadline");
        }
        if (selected.getUnitPriceElectricity() == null) {
            WorkflowConfig workflowConfig = configService.findConfigBySite(selected.getSiteUID());
            selected.setUnitPriceElectricity(workflowConfig != null ? workflowConfig.getUnitPriceElectricity() : null);
        }
        initGbTree();
        initOrgTree();

        updateClinicalDeptOnSiteChange();
        updateManagementOnSiteChange();
        updateAssetStatusList();

        assetGroupSecondarySupport = i18nDao.countBySiteIdAndMsgTypeStartsWith(loginUser.getSiteId(), "assetGroupSecondary") > 0 ? true : false;

        if (assetGroupSecondarySupport) {
            onAssetGroupChange();
        }
        //ge mvc data
        if (selected.getMvsEquip() != null && selected.getMvsEquip() == 1) {
            mvsUtil.copyToMvsTableIfMvsEquip(selected, mvsAssetInfo);
            MvsAssetInfo found = mvsAssetInfoDao.findById(selected.getUid());
            if (found != null) {
                mvsAssetInfo = found;
            }
        }
        //基础信息编辑时必填字段根据资产分类调整
        if (loginUser.isEnableAssetTypeGroup()) {
            String assetType = this.selected.getAssetType();
            if (StringUtils.isNotEmpty(assetType)) {
                customConfig.getAssetFields().forEach((k, v) -> {
                    requiredInput(assetType, v);
                });
            }
        }
    }

    public String getAssetUploadFileFormats() {
        return configItemService.getConfigItemByKeyToString("assetUploadFileFormats", loginUser);
    }

    private void assetInfoCopySelected(AssetInfo assetInfo, AssetInfo selected) {
        BeanUtils.copyProperties(assetInfo, selected);
        selected.setSerialNum(null);
        selected.setFinancingNum(null);
        selected.setDepartNum(null);
        selected.setQrCode(null);
        selected.setQrCode(null);
        selected.setId(null);
        selected.setUid(null);
        selected.setAssetDeptId(null);
        selected.setAssetDeptUID(null);
        selected.setAssetDeptOrg(null);
        selected.setAssetDeptName(null);
        selected.setCleaned(true);
        selected.setSharedAssetTypeUid(null);
        selected.setSharedAssetReqDetail(null);
        selected.setSharedAssetReqDetailList(null);
        selected.setSharedAssetSwapIndex(null);
        selected.setSharedAssetSwapIndexId(null);
    }

    public List<WorkflowConfig.AttachmentCategory> getAttachmentCategories() {
        return configService.getAttachmentCategories(config);
    }

    //判断待报废按钮是否禁用
    public boolean hasScrappedAuthority() {
        return acService.hasScrappedAuthority();
    }

    //重新获取资产状态枚举
    public void updateAssetStatusList() {
        assetStatusList = i18nDao.getByMsgTypeAndSiteId("assetStatus", ApmConstant.DEFAULT_SITE_ID);
        assetStatusList.stream().forEach(status -> {
            if (status.getMsgKey().equals("4")) {
                status.setEditable(hasScrappedAuthority());
            } else {
                status.setEditable(true);
            }
        });
    }

    public void returnBack() {
        acService.rejectReturn(createRequest, rejectText);
        this.rejectTextHistory.add(TimeUtils.getStrDate(new Date(), "[yyyy-MM-dd HH:mm:ss]").concat(rejectText));
        rejectText = "";
        WebUtil.addSuccessMessage(WebUtil.getMessage("MessageSent"));
    }

    private AssetInfo createAssetInfo() {
        if (selected == null) {
            selected = new AssetInfo();
        }
        selected.setQrCode(createRequest.getQrCode());
        selected.setSerialNum(createRequest.getSerialNum());//制造商序列号
        selected.setFunctionType(createRequest.getModelNum());//设备型号
        selected.setManufactDate(createRequest.getManufactureDate());//出厂日期
        selected.setDepartNum(createRequest.getDepartNum());//设备编号(院方)
        selected.setMakerShortName(createRequest.getMakerShortName());//品牌/制造商简称
        selected.setRegistrationNo(createRequest.getRegistrationNo());//注册证号
        selected.setAssetClass(createRequest.getAssetClass());
        selected.setIsValid(true);
        selected.setStatus(1);
        selected.setDeviceStatus(0);
        selected.setIsDeleted(Boolean.FALSE);
        selected.setName(createRequest.getAssetName());//名称
        selected.setAssetGroup(createRequest.getAssetGroup());//设备分类
        selected.setLocationName(createRequest.getLocationName());//安装位置
        selected.setComments(createRequest.getComments());//备注
        selected.setAssetType(createRequest.getAssetType());// 资产分类
        selected.setEmergencyEquipment(createRequest.getEmergencyEquipment());// 是否急救设备
        selected.setLifecycleInYear(createRequest.getLifecycleInYear());// 使用年限
        selected.setAssetClassification32(createRequest.getAssetClassification32());// 使用年限

        if (null != createRequest.getManufactureId()) {
            Supplier supplier = supplierDao.getById(Integer.valueOf(createRequest.getManufactureId()));
            if (null != supplier) {
                selected.setManufactureId(supplier.getId().toString());
                selected.setManufacture(supplier.getName());
                selected.setManufactureTel(supplier.getTel());
                selectedObjectMap.put("manufacturer", supplier.getUid() == null ? null : supplierDao.findByUid(supplier.getUid()));
            }
        }
        if (null != createRequest.getOrgId()) {
            OrgInfo org = acService.getOrgInfo(createRequest.getOrgId());
            if (org != null) {
                selected.setClinicalDeptId(org.getId());
                selected.setClinicalDeptUID(org.getUid());
                selectedObjectMap.put("clinicalDept", org.getUid() == null ? null : orgDao.findByUid(org.getUid()));
            }
        }
        OrgInfo siteOrg = orgDao.findByUid(createRequest.getSiteUID());
        if (siteOrg != null) {
            selectedObjectMap.put("selectedOrg", siteOrg.getSiteUID() == null ? null : orgDao.findByUid(siteOrg.getSiteUID()));//业务单元
            selected.setHospitalId(siteOrg.getId());
            selected.setSiteId(siteOrg.getSiteId());
        }
        selected.setParentAssetId(createRequest.getParentAssetId());
        selectedObjectMap.put("parentAsset", createRequest.getParentAssetId() == null ? null : dao.findById(createRequest.getParentAssetId()));//主设备
        if (null != createRequest.getUserId()) {
            UserAccount owner = acService.getUserInfo(createRequest.getUserId());
            if (owner != null) {
                selected.setAssetOwnerId(owner.getId());
                selected.setAssetOwnerName(owner.getName());
                selected.setAssetOwnerTel(owner.getTelephone());
            }
            selectedObjectMap.put("assetOwner", selected.getAssetOwnerId() == null ? null : userDao.findById(selected.getAssetOwnerId()));//维修负责人
        }
        selected.setTenantUID(createRequest.getTenantUID());
        selected.setHospitalUID(createRequest.getHospitalUID());
        selected.setInstitutionUID(createRequest.getInstitutionUID());
        selected.setSiteUID(createRequest.getSiteUID());
        selected.setUsageStatus(acService.needAssetApproval(createRequest.getSiteUID()) ? UsageStatusConstant.UNDER_REVIEW : UsageStatusConstant.ARCHIVED);

        if (!Strings.isNullOrEmpty(createRequest.getSubmitWechatId())) {
            UserAccount submitter = acService.getUserByWechatId(createRequest.getSubmitWechatId());
            if (null != submitter) {
                selected.setCreatedBy(submitter.getName());
            }
        }
        selected.setCreatedDate(createRequest.getSubmitDate());
        return selected;
    }

    @Override
    protected AssetInfoRepository getDAO() {
        return dao;
    }

    @Override
    public void onBeforeNewObject(AssetInfo object) {
        object.setIsValid(true);
        object.setStatus(1);
        object.setDeviceStatus(0);
        object.setIsDeleted(false);
    }

    private void beforeEdit() {
        if (!StringUtils.isEmpty(selected.getNationalStandardCode())) {
            selectedGb = gbDao.getGbByCode(selected.getNationalStandardCode());
            if (null != selectedGb) {
                gbCodeName = selectedGb.getCode() + "/" + selectedGb.getName();
            }
        }
        selectedObjectMap.put("assetOwner", selected.getAssetOwnerId() == null ? null : userDao.findById(selected.getAssetOwnerId()));
        selectedObjectMap.put("assetOwner2", selected.getAssetOwnerId2() == null ? null : userDao.findById(selected.getAssetOwnerId2()));
        selectedObjectMap.put("assetOwner3", selected.getAssetOwnerId3() == null ? null : userDao.findById(selected.getAssetOwnerId3()));
        selectedObjectMap.put("pmOwner", selected.getPmOwnerId() == null ? null : userDao.findById(selected.getPmOwnerId()));

        selectedObjectMap.put("measuringOwner", selected.getMeasuringOwnerId() == null ? null : userDao.findById(selected.getMeasuringOwnerId()));
        selectedObjectMap.put("patrolInspectionOwnerName", selected.getPatrolInspectionOwnerUID() == null ? null : userDao.findByUid(selected.getPatrolInspectionOwnerUID()));


        selectedObjectMap.put("clinicalOwner", selected.getClinicalOwnerId() == null ? null : userDao.findById(selected.getClinicalOwnerId()));
        selectedObjectMap.put("clinicalDept", selected.getClinicalDeptUID() == null ? null : orgDao.findByUid(selected.getClinicalDeptUID()));
        selectedObjectMap.put("assetDept", selected.getAssetDeptUID() == null ? null : orgDao.findByUid(selected.getAssetDeptUID()));
        selectedObjectMap.put("accountingDept", selected.getAccountingDeptUID() == null ? null : orgDao.findByUid(selected.getAccountingDeptUID()));
        selectedObjectMap.put("supplier", selected.getSupplierId() == null ? null : supplierDao.findById(selected.getSupplierId()));
        selectedObjectMap.put("maintainer", selected.getMaitananceId() == null ? null : supplierDao.findById(Integer.valueOf(selected.getMaitananceId())));
        selectedObjectMap.put("manufacturer", selected.getManufactureId() == null ? null : supplierDao.findById(Integer.valueOf(selected.getManufactureId())));
        selectedObjectMap.put("parentAsset", selected.getParentAssetId() == null ? null : dao.findById(selected.getParentAssetId()));
        selectedObjectMap.put("selectedOrg", selected.getSiteUID() == null ? null : orgDao.findByUid(selected.getSiteUID()));

        if (null != selected.getAssetType()) {
            AssetTypeGroup selectedAssetTypeGroup = assetTypeGroupRepository.findByEncodedAndTenantUid(selected.getAssetType(), selected.getTenantUID());
            if (null != selectedAssetTypeGroup) {
                selectedObjectMap.put("selectedAssetTypeGroup", selectedAssetTypeGroup);
            }
        }

        qrCode = selected.getQrCode();

        medicalInstrumentsClassificationCompCtrl.onServerEvent(MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, this.selected.getMedicalApparatusClassification());

        getClinicalOwnerList();

        if (selected.getSupplierId() != null) {
            Supplier supplier = supplierDao.findById(selected.getSupplierId());
            if (supplier != null) {
                selected.setVendorTel(supplier.getTel());
            }
        }

    }

    public void initOrgTree() {
        if (getIsSelect46Account()) {
            return;
        }
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();

        selectedObjectMap.put("selectedOrg", orgInfo);

        changeAssetOrg(orgInfo);
        updateManagementOnSiteChange();
    }

    public List<UserAccount> completeOwnerList(String query) {

        if (ownerList == null) {
            return Collections.emptyList();
        }

        List<UserAccount> res = this.ownerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
//        res.sort(new PinYinComparatorByName());
        return res;
    }

    private void changeAssetOrg(OrgInfo orgInfo) {
        selected.setSiteId(orgInfo.getSiteId());
        selected.setHospitalId(orgInfo.getId());

        selected.setTenantUID(orgInfo.getTenantUID());
        selected.setHospitalUID(orgInfo.getHospitalUID());
        selected.setInstitutionUID(orgInfo.getInstitutionUID());
        selected.setSiteUID(orgInfo.getSiteUID());
        config = configService.findConfigBySite(selected.getSiteUID());

        updateClinicalDeptOnSiteChange();

        selectedObjectMap.put("clinicalDept", null);
        onAssetDeptChange("clinicalDept");
        selectedObjectMap.put("assetDept", null);
        onAssetDeptChange("assetDept");
        if (parentAssetList != null) {
            parentAssetList.clear();
        }
    }

    private void updateClinicalDeptOnSiteChange() {

        clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(selected.getSiteUID());
    }

    private void updateManagementOnSiteChange() {
        managementList.clear();
        List<BuildManagement> managementList = buildManagementRepository.findBySiteUID(selected.getSiteUID());
        if (CollectionUtils.isEmpty(managementList)) {
            return;
        }
        TreeNode root = new DefaultTreeNode("root", null, null);
        if (!org.apache.commons.collections4.CollectionUtils.isEmpty(managementList)) {
            managementList.forEach(val -> root.getChildren().add(new DefaultTreeNode("org", val, null)));
            componentFloor(root);
        }
    }


    private void componentFloor(TreeNode orgTree) {
        orgTree.getChildren().forEach(val -> {
            BuildManagement buildManagement = (BuildManagement) val.getData();
            List<BuildManagement> buildFloorList = Optional.ofNullable(buildManagementRepository.findByParent(buildManagement.getId())).orElse(new ArrayList<>());
            buildFloorList.forEach(item -> val.getChildren().add(new DefaultTreeNode("org", item, null)));
            componentRoom(val);
        });
    }

    private void componentRoom(TreeNode orgTree) {
        orgTree.getChildren().forEach(val -> {
            BuildManagement buildManagement = (BuildManagement) val.getData();
            List<BuildManagement> buildFloorList = Optional.ofNullable(buildManagementRepository.findByParent(buildManagement.getId())).orElse(new ArrayList<>());
            buildFloorList.forEach(item -> val.getChildren().add(new DefaultTreeNode("org", item, null)));
            managementList.addAll(buildFloorList);
        });
    }


    public void onParentAssetChange() {
        AssetInfo selectedAsset = (AssetInfo) selectedObjectMap.get("parentAsset");
        selected.setParentAssetId(null == selectedAsset ? null : selectedAsset.getId());
    }

    public void onOwnerChange(String idx) {
        UserAccount selectedUser = (UserAccount) selectedObjectMap.get(idx);
        switch (idx) {
            case "assetOwner":
                selected.setAssetOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "assetOwner2":
                selected.setAssetOwnerId2(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName2(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel2(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "assetOwner3":
                selected.setAssetOwnerId3(null == selectedUser ? null : selectedUser.getId());
                selected.setAssetOwnerName3(null == selectedUser ? null : selectedUser.getName());
                selected.setAssetOwnerTel3(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "clinicalOwner":
                selected.setClinicalOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setClinicalOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setClinicalOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "measuringOwner":
                selected.setMeasuringOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setMeasuringOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setMeasuringOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "pmOwner":
                selected.setPmOwnerId(null == selectedUser ? null : selectedUser.getId());
                selected.setPmOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setPmOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
            case "patrolInspectionOwnerName":
                selected.setPatrolInspectionOwnerUID(null == selectedUser ? null : selectedUser.getUid());
                selected.setPatrolInspectionOwnerName(null == selectedUser ? null : selectedUser.getName());
                selected.setPatrolInspectionOwnerTel(null == selectedUser ? null : selectedUser.getTelephone());
                break;
        }


    }

    public void onSupplierChange(String idx) {
        changeSelect(idx);
    }

    private void changeSelect(String idx) {
        Supplier supplier = (Supplier) selectedObjectMap.get(idx);
        switch (idx) {
            case "supplier":
                selected.setSupplierId(supplier == null ? null : supplier.getId());
                selected.setVendor(supplier == null ? null : supplier.getName());
                selected.setVendorTel(Optional.ofNullable(supplier).map(Supplier::getTel).orElse(null));
                break;
            case "maintainer":
                selected.setMaitananceId(supplier == null ? null : supplier.getId().toString());
                selected.setMaitanance(supplier == null ? null : supplier.getName());
                selected.setMaitananceTel(Optional.ofNullable(supplier).map(Supplier::getTel).orElse(null));
                break;
            case "manufacturer":
                selected.setManufactureId(supplier == null ? null : supplier.getId().toString());
                selected.setManufacture(supplier == null ? null : supplier.getName());
                selected.setManufactureTel(Optional.ofNullable(supplier).map(Supplier::getTel).orElse(null));
                break;
            default:
                break;
        }
    }


    public void locationNamechangeSelect(SelectEvent event) {
        selected.setLocationCode(null);
        selected.setLocationName(locationName);
        if (event == null) {
            return;
        }
        String code = (String) event.getObject();
        if (StringUtils.isNotBlank(code)) {
            List<BuildManagement> list = managementMap.get(code);
            if (CollectionUtils.isNotEmpty(list)) {
                BuildManagement buildManagement = list.get(0);
                selected.setLocationCode(buildManagement.getNamePyCode());
                selected.setLocationName(buildManagement.getName());
            }
        }
    }
//        Map<Integer, List<BuildManagement>> collect = managementList.stream().collect(Collectors.groupingBy(BuildManagement::getId));
//        List<BuildManagement> list = collect.get(idx);
//        if(CollectionUtils.isNotEmpty(list)){
//            BuildManagement buildManagement = list.get(0);
//            selected.setLocationCode(buildManagement.getNamePyCode());
//            selected.setLocationName(buildManagement.getName());
//        }


    public void locationNamechange() {
        locationNamechangeSelect(null);

    }

    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(assetuid);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
        uploadFlag = collect.size() == 0;
        return collect;
    }

    @Override
    public void onAfterNewObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
        if (attachements != null) {
            if (isOK) {
                attachements.forEach(item -> {
                    if (item.getBoId() == null) {
                        item.setBoId(object.getId());
                        item.setBoUuid(object.getUid());
                        blobService.saveBlobObject(item);
                    }
                });
            }
        }

    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            String fileName = event.getFile().getFileName();
            List<String> videoExtensions = ListUtil.newArrayList("mp4", "MP4");
            if (videoExtensions.contains(FilenameUtils.getExtension(fileName))) {
                List<V2_BlobObject> type3 = getAttachList(selected.getUid(), "3");
                long count = type3.stream().filter(b -> ListUtil.newArrayList("mp4", "MP4").contains(FilenameUtils.getExtension(b.getObjectName()))).count();
                if (count >= 3) {
                    WebUtil.addErrorMessage("视频上传数量已达上限");
                    return;
                }
            }
            logger.info("AssetInfoEditController handleFileUpload fileName start:" + fileName);
            V2_BlobObject blobObject = blobService.uploadFile(event);
            logger.info("AssetInfoEditController handleFileUpload fileName end:" + fileName);
            if (null != blobObject) {
                String type = event.getComponent().getId();
                type = type.substring(type.indexOf("type") + 4);
                attachements.add(blobObject);
                blobObject.setBoId(selected.getId());
                blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                blobObject.setBoSubType(type);
                blobObject.setBoUuid(selected.getUid());
                blobService.saveBlobObject(blobObject);
                WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
            } else {
                WebUtil.addErrorMessage("fail to upload file");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AssetInfoEditController handleFileUpload error fileName:" + event.getFile().getFileName() + "error:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void todelete() {
        toDeleteNurseMaintainSite = true;
    }

    public String checkIfRequireBeforeApplyChange() {
        if (!unCheckedNursesiteMaintainSite && selected.getUid() != null) {
            AssetInfo old = dao.findByUid(selected.getUid());
            Integer oldClinicalId = old.getClinicalDeptId() == null ? 0 : old.getClinicalDeptId();
            Integer newClinicalId = selected.getClinicalDeptId() == null ? 0 : selected.getClinicalDeptId();
            if ((!oldClinicalId.equals(newClinicalId)) || !StringUtils.equals(old.getAssetDeptUID(), selected.getAssetDeptUID())) {
                List<AssetInfo> assets = new ArrayList<>();
                assets.add(old);
                unCheckNurseSiteMaintain = assetInfoBulkEditCheckService.verifyForNurseSiteByBatchEdit(assets);
                if (CollectionUtils.isNotEmpty(unCheckNurseSiteMaintain)) {
                    unCheckedNursesiteMaintainSite = true;
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("unCheckedNursesiteMaintainSite", unCheckedNursesiteMaintainSite);
                    requestContext.addCallbackParam("toDeleteNurseMaintainSite", toDeleteNurseMaintainSite);
                    return "";
                }
            }
        }
        return applyChange();
    }

    @Transactional
    public String applyChange() {

        //check whether parent asset is valid when editing
        if (!isValidParentAsset()) {
            WebUtil.addErrorMessage("主设备不符合要求，请重新设置");
            return "";
        }

        if (!checkDepartNumUnique()) {
            WebUtil.addErrorMessage("设备编号已被占用");
            return "";
        }
        // 序列号重复校验（新增时）
        if (selected.getId() == null && StringUtils.isNotEmpty(selected.getSerialNum())) {
            if (checkSerialNumUnique()) {
                WebUtil.addErrorMessage("制造商序列号已存在！");
            }
        }
        if (!checkAssetScrap()) {
            WebUtil.addErrorMessage("设备在报废流程中,不允许修改状态");
            return "";
        }

        // 资产分类
        if (null != selectedObjectMap.get("selectedAssetTypeGroup")) {
            AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup) selectedObjectMap.get("selectedAssetTypeGroup");
            selected.setAssetType(selectedAssetTypeGroup.getEncoded());
        }

        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(selected.getAssetType())) {
            WebUtil.addErrorMessage("当前租户已开启资产分类,必填");
            return "";
        }

        if (selected.getUnitPriceElectricity() == null) {
            WorkflowConfig workflowConfig = configService.findConfigBySite(selected.getSiteUID());
            selected.setUnitPriceElectricity(workflowConfig != null ? workflowConfig.getUnitPriceElectricity() : null);
        }
        //设置质保时间
        if (null == selected.getFactoryWarrantyDate()) {
            if (selected.getAcceptDate() != null && selected.getWarrantyMonth() != null) {
                selected.setFactoryWarrantyDate(TimeUtil.toJodaDate(selected.getAcceptDate()).plusMonths(selected.getWarrantyMonth()).toDate());
            }
        }

        if (selected.getUsageStatus() == null) {
            selected.setUsageStatus(UsageStatusConstant.ARCHIVED);
        }
        if (Strings.isNullOrEmpty(qrCode) || "".equals(qrCode.trim())) {
            if ("DFQ".equals(getAssetCodeConfig())) {
                //删除资产编号时候会清除二维码
                if (StringUtils.isNotEmpty(selected.getQrCode())) {
                    qrCodeService.forceRemoveQrcode(selected.getQrCode(), selected.getSiteUID());
                }
            }
            selected.setQrCode(null);
        } else {
            if (!qrCode.equals(selected.getQrCode())) {
                if ("DFQ".equals(getAssetCodeConfig())) {
                    QrCodeLib qrCodeLib = qrCodeService.findByQrCode(qrCode, selected.getSiteUID());
                    if (qrCodeLib == null) {
                        if (!qrCodeService.createSpecialQrCode(qrCode, selected.getSiteUID())) {
                            WebUtil.addErrorMessage("二维码不正确");
                            return "";
                        }
                        ;
                    }
                    qrCodeService.forceRemoveQrcode(selected.getQrCode(), selected.getSiteUID());
                    selected.setQrCode(null);
                }
                if (!updateQrCode()) {
                    qrCode = "";
                    return "";
                }
            }
        }
        //新增资产建档时
        if (isStatus) {
            selected.setQrCode(qr_Code);
        }
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return "";
            }

            selected.setNationalStandardCode(selectedGb.getCode());
        }

        if (null != selectedObjectMap.get("selectedAuthGroup")) {
            SysAuthGroup selectedAuthGroup = (SysAuthGroup) selectedObjectMap.get("selectedAuthGroup");
            selected.setMedicalApparatusClassification(selectedAuthGroup.getClassificationCode());
        }

        //when mvs user create a new record, mark this asset as mvs asset
        if (UserContextService.isMVSRole() && this.selected.getMvsEquip() == null) {
            this.selected.setMvsEquip(1);
        }

        boolean isCreate = false;
        if (selected.getId() == null) {
            isCreate = true;
        }
        selected.setOfflineUpdateDate(new Date());
        this.save();
        if (selected.isSpecialAsset()) {
            specialAssetInfo.setAssetId(selected.getId());
            specialAssetInfo.setAssetUid(selected.getUid());
            List<AssetAnnualInspectionReport> assetAnnualInspectionReports = specialAssetInfo.getAssetAnnualInspectionReports().stream().filter(item -> item.getName() != null).collect(Collectors.toList());
            assetAnnualInspectionReports.forEach(item -> item.setSpecialAssetUid(specialAssetInfo.getId()));
            List<PermitInfo> permitInfos = specialAssetInfo.getPermitInfos().stream().filter(item -> item.getCertificateName() != null).collect(Collectors.toList());
            specialAssetInfo.setAssetAnnualInspectionReports(assetAnnualInspectionReports);
            specialAssetInfo.setPermitInfos(permitInfos);
            specialAssetInfoService.saveSpecialAsset(specialAssetInfo);
            onAfterNewObjectPermit();

        }
        AssetInfo assetInfo = new AssetInfo();
        BeanUtils.copyProperties(selected, assetInfo);
        if (isCreate) {
//            newAsset.setId(null);
            assetInfo.setCreateType("Add");
        } else {
            assetInfo.setCreateType("Edit");
        }
        assetHistoryService.saveAssetHistory(assetInfo);
        creteRule();
        if (CollectionUtils.isNotEmpty(unCheckNurseSiteMaintain) && toDeleteNurseMaintainSite) {
            assetInfoBulkEditCheckService.deleteNursiteMaintain(unCheckNurseSiteMaintain.get(0));
        }

        if (selected != null && selected.getId() != null && selectedPictures != null) {
            acService.createAttachments(selected, selectedPictures);
        }
        if (qr_Code != null && siteUid != null) {
            acService.updateQrLib(qr_Code, 3, siteUid);
        }
        // do other stuff after save
        int status = selected.getStatus();
//        if(status != assetLifecyleStatus){
//            assetLifecyleService.saveLifecycle(assetLifecyleStatus,selected,loginUser);
//        }
        if (Objects.equals(this.selected.getStatus(), 4)) {
            aimService.updateNurseSiteMaintain(this.selected.getUid());
        }
        assetDepreciationService.saveAssetDerpeciation(selected);

        mvsUtil.copyToMvsTableIfMvsEquip(selected, mvsAssetInfo);

        saveOrUpdateAssetTags();
        if (StringUtils.isNotBlank(type) && "SpecialAsset".equalsIgnoreCase(type)) {
            return "specialAssetList?faces-redirect=true";
        }
        if (isStatus) {
            return "/portal/asset/createAsset/RequestsList?faces-redirect=true";
        }
        if (resultStatus) {
            return getListPageLink();
        } else {
            return "";
        }
    }

    private boolean checkAssetScrap() {
        if (selected.getId() != null) {
            AssetInfo assetInfo = dao.getById(selected.getId());
            if (assetInfo.getStatus() != selected.getStatus()) {
                AssetScrap assetScrap = assetScrapRepository.findByAssetId(selected.getId());
                if (assetScrap == null) {
                    return true;
                } else {
                    if (assetScrap.getCurrentStatus() == 2) {
                        return false;
                    }
                }
            }
        }
        return true;

    }

    private void creteRule() {
        if (pmOrderRuleThreeLevel != null || pmOrderRuleTwoLevel != null) {
            RelRuleAsset rel = new RelRuleAsset();
            rel.setAssetUID(selected.getUid());
            rel.setCreatedDate(new Date());
            rel.setTenantUID(selected.getTenantUID());
            rel.setInstitutionUID(selected.getInstitutionUID());
            rel.setHospitalUID(selected.getHospitalUID());
            rel.setSiteUID(selected.getSiteUID());
            /* if(pmOrderRule!=null) {  //一级 暂时用不到
                rel.setRuleUID1(pmOrderRule.getUid());
            }*/
            if (pmOrderRuleTwoLevel != null) { //二级
                rel.setRuleUID2(pmOrderRuleTwoLevel.getUid());
            }
            if (pmOrderRuleThreeLevel != null) { //三级
                rel.setRuleUID(pmOrderRuleThreeLevel.getUid());
            }
            relRuleAssetRepository.save(rel);
        }
        if (CollectionUtils.isNotEmpty(listSelectedNurseSiteMaintainConfig)) {
            List<NurseSiteMaintainConfigItem> items = new ArrayList<>();
            for (NurseSiteMaintainConfig config : listSelectedNurseSiteMaintainConfig) {
                config.setModifyBy(loginUser);
                config.setModifyByUid(loginUser.getUid());
                config.setModifyDate(new Date());
                NurseSiteMaintainConfigItem item = new NurseSiteMaintainConfigItem();
                item.setAssetUid(selected.getUid());
                item.setCreateBy(config.getCreateBy());
                item.setCreateByUid(config.getCreateByUid());
                item.setCreateDate(config.getCreateDate());
                item.setHospitalUID(config.getHospitalUID());
                item.setInstitutionUID(config.getInstitutionUID());
                item.setClinicalDeptName(config.getClinicalDeptName());
                item.setClinicalDeptUID(config.getClinicalDeptUID());
                item.setModifyBy(config.getModifyBy());
                item.setModifyByUid(config.getModifyByUid());
                item.setModifyDate(config.getModifyDate());
                item.setParentId(config.getId());
                item.setSiteUID(config.getSiteUID());
                item.setTenantUID(config.getTenantUID());
                item.setType(config.getType());
                item.setTypeValue(config.getTypeValue());
                items.add(item);
            }
            nurseSiteMaintainConfigRepository.save(listSelectedNurseSiteMaintainConfig);
            nurseSiteMaintainConfigItemRepository.save(items);
        }
        if (Objects.nonNull(selectedNurseSiteMaintainConfigJN)) {
            selectedNurseSiteMaintainConfigJN.setModifyBy(loginUser);
            selectedNurseSiteMaintainConfigJN.setModifyByUid(loginUser.getUid());
            selectedNurseSiteMaintainConfigJN.setModifyDate(new Date());
            NurseSiteMaintainConfigJNItem item = new NurseSiteMaintainConfigJNItem();
            item.setAssetUid(selected.getUid());
            item.setCreateBy(selectedNurseSiteMaintainConfigJN.getCreateBy());
            item.setCreateByUid(selectedNurseSiteMaintainConfigJN.getCreateByUid());
            item.setCreateDate(selectedNurseSiteMaintainConfigJN.getCreateDate());
            item.setHospitalUID(selectedNurseSiteMaintainConfigJN.getHospitalUID());
            item.setInstitutionUID(selectedNurseSiteMaintainConfigJN.getInstitutionUID());
            item.setClinicalDeptName(selectedNurseSiteMaintainConfigJN.getClinicalDeptName());
            item.setClinicalDeptUID(selectedNurseSiteMaintainConfigJN.getClinicalDeptUID());
            item.setModifyBy(selectedNurseSiteMaintainConfigJN.getModifyBy());
            item.setModifyByUid(selectedNurseSiteMaintainConfigJN.getModifyByUid());
            item.setModifyDate(selectedNurseSiteMaintainConfigJN.getModifyDate());
            item.setParentId(selectedNurseSiteMaintainConfigJN.getId());
            item.setSiteUID(selectedNurseSiteMaintainConfigJN.getSiteUID());
            item.setTenantUID(selectedNurseSiteMaintainConfigJN.getTenantUID());
            item.setType(selectedNurseSiteMaintainConfigJN.getType());
            item.setTypeValue(selectedNurseSiteMaintainConfigJN.getTypeValue());
            nurseSiteMaintainConfigJNRepository.save(selectedNurseSiteMaintainConfigJN);
            nurseSiteMaintainConfigJNItemRepository.save(item);
        }
    }

    public boolean checkDepartNumUnique() {

        if (org.springframework.util.StringUtils.isEmpty(selected.getDepartNum())) {
            return true;
        }

        return checkDepartNumUniqueAssetInfo();

    }

    private boolean checkDepartNumUniqueAssetInfo() {
        if (selected.getId() != null) {
            List<String> hisData = dao.findIdByDepartNumAndTenantUIDExcludeId(
                    selected.getDepartNum(), selected.getTenantUID(), selected.getId());
            if (!hisData.isEmpty()) {
                log.info("设备编号冲突: departNum={}, currentUid={}, conflictUids={}",
                        selected.getDepartNum(), selected.getUid(), hisData);
            }
            return hisData.isEmpty();
        }
        List<String> hisData = dao.findIdByDepartNumAndTenantUID(selected.getDepartNum(), selected.getTenantUID());
        if (hisData.isEmpty()) {
            return true;
        }
        //兼容老数据
        for (String uid : hisData) {
            if (uid.equals(selected.getUid())) {
                return true;
            }
        }
        log.info("==设备编号冲突: departNum={}, currentUid={}, conflictUids={}",
                selected.getDepartNum(), selected.getUid(), hisData);
        return false;
    }

    private void saveOrUpdateAssetTags() {

        List<String> existingTags = assetTagsMapper.getTagsByAsset(this.selected.getUid());
        List<String> currentTags = this.selected.getTags();

        List<String> newTags = new ArrayList<>(currentTags);
        newTags.removeAll(existingTags);

        List<String> removeTags = new ArrayList<>(existingTags);
        removeTags.removeAll(currentTags);

        //add new tags
        newTags.stream().distinct().forEach(item -> {
            AssetTags assetTags = new AssetTags();
            assetTags.setAssetUid(this.selected.getUid());
            assetTags.setTenantUID(loginUser.getTenantUID());
            assetTags.setTag(item);
            assetTagsDao.save(assetTags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> {
            assetTagsMapper.removeTag(this.selected.getUid(), item);
        });

    }

    public String getListPageLink() {
        if (isStatus) {
            return "/portal/asset/createAsset/RequestsList?faces-redirect=true";
        }
        if (!selected.getIsValid() || 4 == selected.getStatus()) {
            return "AssetTerminateEntryList?faces-redirect=true";
        }
        return "AssetList?faces-redirect=true";
    }

    public String getSpecialListPageLink() {

        return "specialAssetList?faces-redirect=true";
    }

    //三码合一设置
    public String getAssetCodeConfig() {
        return config == null ? null : config.getAssetCodeConfig();
    }

    private boolean updateQrCode() {
        QrCodeLib qrCodeLib = qrCodeService.findByQrCode(qrCode, selected.getSiteUID());
        if (qrCodeLib == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("InvalidQRCode"));
            return false;
        }
        if (!qrCodeLib.getSiteUID().equals(selected.getSiteUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            WebUtil.addErrorMessage(WebUtil.getMessage("AlreadyUsingQrCode"));
            logger.error("update qrCode err, qrCode is : " + qrCode + " asset qrCode is : " + selected.getQrCode());
            return false;
        }

        QrCodeLib oldQRCodeLib = qrCodeService.findByQrCode(selected.getQrCode(), selected.getSiteUID());
        if (null != oldQRCodeLib) {
            qrCodeService.releaseQrCode(oldQRCodeLib.getQrCode(), selected.getSiteUID());
        }
        qrCodeService.updateQrCodeStatus(qrCode, selected.getSiteUID(), 3);
        selected.setQrCode(qrCode);
        return true;
    }

    public void onAssetGroupChange() {

        if (!assetGroupSecondarySupport) return;

        if (this.selected.getAssetGroup() == null) {
            this.assetGroupSecondaryList = Collections.emptyList();
        } else {
            String msgType = "assetGroupSecondary_" + this.selected.getAssetGroup();
            this.assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId(msgType, loginUser.getSiteId());
        }

    }

    public void onFunctionGroupChange() {
        code68List = new ArrayList<>();
        this.selected.setAssetCode68(null);
        this.changeFunctionGroup();
    }

    public void changeFunctionGroup() {
        Integer functionGroupId = this.selected.getFunctionGroup();
        if (functionGroupId != null) {
            I18nMessage functionGroup = i18nDao.getByMsgTypeAndMsgKey("assetFunctionType", functionGroupId.toString());
            if (functionGroup != null) {
                code68List = i18nDao.getByMsgType(functionGroup.getMsgKey());
            }
        }
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        changeFunctionGroup();
    }

    @Override
    public void onBeforeSave(AssetInfo object) {
        if (null != object.getClinicalDeptId()) {
            OrgInfo org = orgDao.findById(object.getClinicalDeptId());
            if (org != null) {
                object.setClinicalDeptName(org.getName());
                object.setClinicalDeptUID(org.getUid());
            }

        } else {
            //clear uid and its name
            object.setClinicalDeptUID(null);
            object.setClinicalDeptName(null);
        }
        DateTime temp;
        if (null != object.getManufactDate()) {
            temp = new DateTime(object.getManufactDate());
            object.setManufactDate(temp.plusHours(12).toDate());
        }
        if (null != object.getPurchaseDate()) {
            temp = new DateTime(object.getPurchaseDate());
            object.setPurchaseDate(temp.plusHours(12).toDate());
        }
        if (null != object.getArriveDate()) {
            temp = new DateTime(object.getArriveDate());
            object.setArriveDate(temp.plusHours(12).toDate());
        }
        if (null != object.getInstallDate()) {
            temp = new DateTime(object.getInstallDate());
            object.setInstallDate(temp.plusHours(12).toDate());
        }
        if (null != object.getWarrantyDate()) {
            temp = new DateTime(object.getWarrantyDate());
            object.setWarrantyDate(temp.plusHours(12).toDate());
        }
        if (null != object.getTerminateDate()) {
            temp = new DateTime(object.getTerminateDate());
            object.setTerminateDate(temp.plusHours(12).toDate());
        }
        Date now = new Date();
        if (object.getId() == null) {
            object.setCreatedBy(loginUser.getName());
            object.setCreatedDate(now);
            object.setLastModifiedBy(loginUser.getName());
            object.setLastModifiedDate(now);
        } else {
            object.setLastModifiedBy(loginUser.getName());
            object.setLastModifiedDate(now);
        }
    }

    //转入待报废
    public String processToBeTerminated() {
        int originStatus = selected.getStatus();
        AssetInfo oldAssetInfo = assetHistoryService.depCopy(selected);
        this.selected.setStatus(4);
        update();
        assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo, selected, ModifySourceEnum.ASSET_SCRAP.getCode());
//        assetLifecyleService.saveLifecycle(originStatus,selected,loginUser);
//        logger.info("AssetInfoEditController processToBeTerminated apply assetId:{}"+ selected.getId());
//        assetScrapService.apply(selected.getId(),"请求报废");
        return "AssetList?faces-redirect=true";
    }

    //转入特种设备
    public String transforSpecialAsset() {
        selected.setSpecialAsset(true);
        specialAssetInfoService.initSpecialAsset(selected);
        dao.save(selected);
        WebUtil.addSuccessMessage("特种设备转入成功");

        return "AssetList?faces-redirect=true";
    }

    public void applyAssetApplication(String action) {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if (StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))) {
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }
        if (this.newSignature && StringUtils.isNotEmpty(signatureContent)) {
            try {
                String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decode = decoder.decodeBuffer(base64);

                V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                if (null != blobObject) {
                    this.signatureId = blobObject.getObjectStorageId();
                    woService.addUserSignature(this.signatureId);
                } else {
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                WebUtil.addErrorMessage("签名保存失败！");
                response.setStatus(206);
                return;
            }
        }
        if ("scrap".equals(action)) {
            List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals("Scrap")).collect(Collectors.toList());
            boolean checkOrderWhenScrap = BooleanUtils.isTrue(configItemService.getConfigItemByKey("checkOrderWhenScrap", loginUser));
            assetScrapService.apply(selected.getId(), applyReason, approvalReason, equipmentDisposalMethod, customDisposalMethod, signatureId, collect, checkOrderWhenScrap);
        } else if ("departmentCovert".equals(action)) {
            Map<String, Object> param = MapUtil.newHashMap();
            param.put("assetUids", bathSelectedList.stream().map(AssetInfo::getUid).collect(Collectors.toList()));
            param.put("outDeptUid", selected.getClinicalDeptUID());
            param.put("inDeptUid", inDeptUid);
            param.put("inDeptName", inDeptName);
            param.put("auditSignature", signatureId);
            param.put("applyReason", applyReason);
            param.put("requestSource", 1);

            assetDepartmentConvertService.apply(param);
        }
    }

    public List<String> getSelectedUidList(String uid) {
        return Collections.singletonList(uid);
    }

    @Getter
    @Setter
    private boolean nurseSiteMaintainIsComplete = false;
    @Getter
    @Setter
    private List<String> workOrderList = new ArrayList<>();
    @Getter
    @Setter
    private List<String> pmOrderList = new ArrayList<>();
    @Getter
    @Setter
    private List<AssetInfo> bathSelectedList = new ArrayList<>();

    public boolean checkAssetOrderIsComplete(List<String> uidList, boolean isScrap) {
        List<String> uids;
        if (uidList.size() == 1) {
            selected = dao.findByUid(uidList.get(0));
            bathSelectedList = Collections.singletonList(selected);
            uids = Collections.singletonList(selected.getUid());
        } else {
            bathSelectedList = dao.findByUidIn(uidList);
            uids = bathSelectedList.stream().map(AssetInfo::getUid).collect(Collectors.toList());
        }

        boolean checkOrderWhenScrap = BooleanUtils.isTrue(configItemService.getConfigItemByKey("checkOrderWhenScrap", loginUser));
        // checkOrderWhenScrap为false时申请报废，只检查mvs工单
        if (isScrap && !checkOrderWhenScrap) {
            List<V2_WorkOrder> workOrders = v2WorkOrderRepository.findUnFinishedMvsOrderByAsset(uids);
            List<V2_PmOrder> pmOrders = v2PmOrderRepository.findUnFinishedMvsOrderByAsset(uids);
            if (CollectionUtils.isNotEmpty(workOrders) || CollectionUtils.isNotEmpty(pmOrders)) {
                logger.error("Assets have outstanding work orders");
                nurseSiteMaintainIsComplete = false;
                workOrderList = workOrders.stream().map(V2_WorkOrder::getBusinessNumber).collect(Collectors.toList());
                pmOrderList = pmOrders.stream().map(V2_PmOrder::getBusinessNumber).collect(Collectors.toList());
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("asetOrderIsComplete", true);
                return true;
            }
        } else {
            List<V2_WorkOrder> workOrders = v2WorkOrderRepository.getByAssetUidInAndStatusOrderByCloseTimeDesc(uids, 1);
            List<V2_PmOrder> pmOrders = v2PmOrderRepository.findByAssetUidInAndIsFinished(uids, false);
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList = nurseSiteMaintainItemRepository.findNoCheckedByAssetUidIn(uids);
            if (CollectionUtils.isNotEmpty(workOrders) || CollectionUtils.isNotEmpty(pmOrders) || CollectionUtils.isNotEmpty(nurseSiteMaintainItemList)) {
                logger.error("Assets have outstanding work orders");
                nurseSiteMaintainIsComplete = CollectionUtils.isNotEmpty(nurseSiteMaintainItemList);
                workOrderList = workOrders.stream().map(V2_WorkOrder::getBusinessNumber).collect(Collectors.toList());
                pmOrderList = pmOrders.stream().map(V2_PmOrder::getBusinessNumber).collect(Collectors.toList());
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("asetOrderIsComplete", true);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void processUnfinishedNurseSiteMaintain(String operationType) {
        if (assetScrapService.processUnfinishedNurseSiteMaintain(UserContextService.getAccessToken(), bathSelectedList.stream().map(AssetInfo::getUid).collect(Collectors.toList()), operationType)) {
            WebUtil.addSuccessMessage("已自动处理未完成临床助手工单");
        } else {
            WebUtil.addErrorMessage("没有未完成的临床助手工单");
        }
    }

    //重新启用
    public String processToBeValid() {
        int originStatus = selected.getStatus();
        AssetInfo oldAssetInfo = assetHistoryService.depCopy(selected);
        this.selected.setStatus(1);
        this.selected.setIsValid(true);
        this.selected.setTerminateDate(null);
        update();
        assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo, selected, ModifySourceEnum.LEDGER_EDIT.getCode());
//        assetLifecyleService.saveLifecycle(originStatus,selected,loginUser);
        return "AssetTerminateEntryList?faces-redirect=true";
    }

    public void updateNurseSiteMaintain(String assetUid) {
        nurseSiteMaintainService.deleteAssetUid(assetUid);
    }

    public Boolean needAssetApproval(String siteUid) {
//        if (null != config && config.getNeedInstorageCheck() != null && config.getNeedInstorageCheck()) {
//            return false;
//        }
        return acService.needAssetApproval(siteUid);
    }

    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(selected.getTenantUID(), selected.getSiteUID(), 1);
        qrCode = qrcodeLib.getQrCode();
        onDepartNumChange("qrCode");
    }

    public void onDepartNumChange(String fieldName) {
        if (!Strings.isNullOrEmpty(getAssetCodeConfig())) {
            String num = null;
            switch (getAssetCodeConfig()) {
                case "DFQ":
                    switch (fieldName) {
                        case "financingNum":
                            num = selected.getFinancingNum();
                            break;
                        case "departNum":
                            num = selected.getDepartNum();
                            break;
                        case "qrCode":
                            num = qrCode;
                            break;
                        default:
                    }
                    if (!Strings.isNullOrEmpty(num) && num.length() > 64) {
                        qrCode = num.substring(0, 64);
                        selected.setFinancingNum(qrCode);
                        selected.setDepartNum(qrCode);
                        WebUtil.addErrorMessage("二维码超长,最大64位");
                    } else {
                        qrCode = num;
                        selected.setFinancingNum(num);
                        selected.setDepartNum(num);
                    }
                    break;
                case "DF":
                    switch (fieldName) {
                        case "financingNum":
                            num = selected.getFinancingNum();
                            selected.setFinancingNum(num);
                            selected.setDepartNum(num);
                            break;
                        case "departNum":
                            num = selected.getDepartNum();
                            selected.setFinancingNum(num);
                            selected.setDepartNum(num);
                            break;
                    }
                    break;
                default:
            }
        }
    }

    public void generateFinancialNum() {
        String code = this.selectedGb == null ? null : this.selectedGb.getCode();
        String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                this.selected.getId(),
                code,
                this.selected.getSiteUID(),
                this.selected.getParentAssetId(),
                this.selected.getFinancingNum()
        );
        if (financingNum != null) {
            if (financingNum.startsWith("error:")) {
                WebUtil.addErrorMessage(financingNum.replace("error:", ""));
            } else {
                this.selected.setFinancingNum(financingNum);
                onDepartNumChange("financingNum");
            }
        }
    }

    public void updateFinancingNumByNationalStandardCode() {
        if (null != selected && null != selectedGb) {
            String financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    this.selected.getId(),
                    this.selectedGb.getCode(),
                    this.selected.getSiteUID(),
                    null,
                    null
            );
            if (financingNum != null && StringUtils.isEmpty(this.selected.getFinancingNum())) {
                this.selected.setFinancingNum(financingNum);
            }
        }
    }

    public List<String> completeGbName(String prefix) {
        List<String> names = null;
        if (!StringUtils.isEmpty(prefix)) {
            names = gbDao.findGbCodeByPrefix(prefix);
        }
        return names;
    }

    private void updateSelectedOrgGb(GbCode gbCode) {
        if (gbCode == null || gbCode.getId() == -1) {
            selectedGb = null;
        } else {
            selectedGb = gbCode;
            gbCodeName = gbCode.getCode() + "/" + gbCode.getName();
        }
    }

    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public List<AssetInfo> completeParentAssetId(String query) {
        parentAssetList = aimService.getParentsAssetsList(this.selected);
        return autoCompleteService.completeAssetInfoList(query, parentAssetList);
    }

    public List<Supplier> autoCompleteSupplier(String query) {
        //重新获取一下供应商
//        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
//        return autoCompleteService.autoCompleteVendor(query, supplierList);

        return aimService.getOnlySuplierListBySiteUid(selected.getTenantUID(), query, 1000);
    }

    public List<Supplier> autoCompleteManufacture(String query) {
//        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
//        return autoCompleteService.autoCompleteManufacture(query, manuList);

        return aimService.getOnlyManuListBySiteUid(selected.getTenantUID(), query, 1000);
    }

    public List<Supplier> autoCompleteMaintainer(String query) {
//        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
//        return autoCompleteService.autoCompleteMaintainer(query, mantainList);

        return aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID(), query, 1000);
    }

    public String selectCreatedBy(String loginName) {
        UserAccount user = userDao.getByLoginName(loginName);
        if (user != null) {
            return user.getName();
        }
        return null;
    }

    public List<String> completeAssetName(String prefix) {
        String tenantUID = loginUser.getTenantUID();
        List<String> names = null;
        if (StringUtils.isEmpty(prefix)) {
            names = dao.findDistinctAssetNames(tenantUID);
        } else {
            names = dao.findDistinctAssetNamesByPrefix(tenantUID, prefix);
        }
        names.sort(new PinYinComparator());
        return names;
    }

    public List<UserAccount> completeClinicalOwnerList(String query) {
        if (clinicalOwnerList == null) {
            return Collections.emptyList();
        }
        ;
        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onAssetDeptChange(String idx) {
        OrgInfo clinicalDept = null;
        switch (idx) {
            case "clinicalDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("clinicalDept");
                selected.setClinicalDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setClinicalDeptName(clinicalDept == null ? null : clinicalDept.getName());
                selected.setClinicalDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                selectedObjectMap.put("clinicalOwner", null);
                onOwnerChange("clinicalOwner");
                //parentAssetList = aimService.getParentsAssetsList(this.selected);
                getClinicalOwnerList();
                break;
            case "assetDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("assetDept");
                selected.setAssetDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setAssetDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                break;
            case "departmentCovert":
                clinicalDept = (OrgInfo) selectedObjectMap.get("clinicalDept");
                inDeptUid = clinicalDept.getUid();
                inDeptName = clinicalDept.getName();
            case "accountingDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("accountingDept");
                selected.setAccountingDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                break;
        }

    }

    public void getClinicalOwnerList() {
        if (null != selected.getClinicalDeptUID()) {
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptUID());
        } else {
            clinicalOwnerList = Collections.emptyList();
        }
    }

    public String submitApproval(Integer usageStatusconstant) {
        Integer usageStatus = selected.getUsageStatus();
        //UsageStatusConstant.INITIAL,UNDER_REVIEW,ARCHIVED
        selected.setUsageStatus(usageStatusconstant);
        String redirectUrl = applyChange();
        if (StringUtils.isNotBlank(redirectUrl) && usageStatusconstant == 2) {
            String token = UserContextService.getAccessToken();
            wxMessageService.submitAssetApprovalSendMessage(selected, "待审核");
        } else {
            selected.setUsageStatus(usageStatus);
        }
        return redirectUrl;
    }

    public String submitApprovalExtend(Integer usageStatusconstant) {
        //UsageStatusConstant.INITIAL,UNDER_REVIEW,ARCHIVED
        String redirectUrl = submitApproval(usageStatusconstant);
        if (StringUtils.isNotBlank(redirectUrl)) {
            redirectUrl = "ApprovalList?faces-redirect=true";
        }
        return redirectUrl;
    }

    @Override
    public void onAfterUpdateObject(AssetInfo object, boolean isOK) {
        resultStatus = isOK;
    }

    public boolean needShow(String itemKey, String fieldKey) {
        return itemKey.equals(fieldKey) && readableFieldsList.contains(fieldKey);
    }

    public boolean needShow(String fieldKey) {

        if (customConfig.getAssetsEditpageBasicFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageCatalogFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageCodesFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageDateFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageFinicialFields().contains(fieldKey)) return true;
        if (customConfig.getAssetsEditpageStatusFields().contains(fieldKey)) return true;

        return false;
    }

    public boolean isValidParentAsset() {

        if (selected.getParentAssetId() == null) {
            return true;
        }

        AssetInfo assetInfo = dao.findById(selected.getParentAssetId());

        if (null == assetInfo) {
            return false;
        }

        if (StringUtils.isEmpty(selected.getClinicalDeptUID())) {
            return false;
        }

        //parent asset and child asset will be in the same dept
        if (!selected.getClinicalDeptUID().equals(assetInfo.getClinicalDeptUID())) {
            return false;
        } else {
            selected.setParentAssetUid(assetInfo.getUid());
        }

        if (Objects.equals(selected.getSiteId(), 46)) {
            selected.setVisibleStatus(1);
        }
        return true;
    }

    public boolean needEdit(String fieldKey) {
        return editableFieldsList.contains(fieldKey);
    }

    //getter and setter
    public Boolean getTerminate() {
        return terminate;
    }

    public CustomConfigObject getCustomConfig() {
        return customConfig;
    }

    public List<I18nMessage> getCode68List() {
        return code68List;
    }

    public List<AssetInfo> getParentAssetList() {
        return parentAssetList;
    }

    public void setParentAssetList(List<AssetInfo> parentAssetList) {
        this.parentAssetList = parentAssetList;
    }

    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<V2_BlobObject> attachements) {
        this.attachements = attachements;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getGbCodeName() {
        return gbCodeName;
    }

    public void setGbCodeName(String gbCodeName) {
        this.gbCodeName = gbCodeName;
    }

    public GbCode getSelectedGb() {
        return selectedGb;
    }

    public void setSelectedGb(GbCode selectedGb) {
        this.selectedGb = selectedGb;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getGbTree() {
        return gbTree;
    }

    public void setGbTree(TreeNode gbTree) {
        this.gbTree = gbTree;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public boolean isAssetGroupSecondarySupport() {
        return assetGroupSecondarySupport;
    }

    public void setAssetGroupSecondarySupport(boolean assetGroupSecondarySupport) {
        this.assetGroupSecondarySupport = assetGroupSecondarySupport;
    }

    public List<I18nMessage> getAssetGroupSecondaryList() {
        return assetGroupSecondaryList;
    }

    public void setAssetGroupSecondaryList(List<I18nMessage> assetGroupSecondaryList) {
        this.assetGroupSecondaryList = assetGroupSecondaryList;
    }

    public MvsAssetInfo getMvsAssetInfo() {
        return mvsAssetInfo;
    }

    public void setMvsAssetInfo(MvsAssetInfo mvsAssetInfo) {
        this.mvsAssetInfo = mvsAssetInfo;
    }


    public String createTpye = null;
    @Getter
    @Setter
    public String createTpyeName = null;

    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        if ("manufacturer".equals(type)) {
            createTpye = type;
            newSupplier.setManufacturerT(true);
            createTpyeName = "制造商信息";
        }
        if ("supplier".equals(type)) {
            createTpye = type;
            newSupplier.setSupplierT(true);
            createTpyeName = "供应商信息";
        }
        if ("maintainer".equals(type)) {
            createTpye = type;
            newSupplier.setMaintainerT(true);
            createTpyeName = "维修商信息";

        }

    }

    public void onBlur() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        Supplier supplier = supplierDao.getBySiteIdAndName(loginUser.getSiteId(), this.newSupplier.getName());
        if (!ObjectUtils.isEmpty(supplier)) {
            this.newSupplier = supplier;
        } else {
            String name = newSupplier.getName();
            boolean ManufacturerT = newSupplier.getManufacturerT();
            boolean SupplierT = newSupplier.getSupplierT();
            boolean MaintainerT = newSupplier.getMaintainerT();
            prepareCreateSupplier("manufacturer");
            newSupplier.setName(name);
            newSupplier.setSupplierT(SupplierT);
            newSupplier.setMaintainerT(MaintainerT);
            newSupplier.setManufacturerT(ManufacturerT);
        }
    }

    public void saveNewSupplier() {
        if (null == newSupplier.getId()) {
            this.newSupplier.setSiteId(loginUser.getSiteId());
            this.newSupplier.setHospitalId(loginUser.getHospitalId());
            this.newSupplier.setTenantUID(loginUser.getTenantUID());
            this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
            this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
            this.newSupplier.setSiteUID(loginUser.getSiteUID());
            this.newSupplier.setCreatedDate(new Date());
            this.newSupplier.setCreatedBy(loginUser.getName());
        }
        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();
//        if (newSupplier.getManufacturerT()) {
//            if (manuList.stream().filter(v -> v.getName().equals(newSupplier.getName())).collect(Collectors.toList()).size() < 1) {
//                manuList.add(newSupplier);
//            }
//        }
//        if (newSupplier.getSupplierT()) {
//            if (supplierList.stream().filter(v -> v.getName().equals(newSupplier.getName())).collect(Collectors.toList()).size() < 1) {
//                supplierList.add(newSupplier);
//            }
//        }
//        if (newSupplier.getMaintainerT()) {
//            if (mantainList.stream().filter(v -> v.getName().equals(newSupplier.getName())).collect(Collectors.toList()).size() < 1) {
//                mantainList.add(newSupplier);
//            }
//        }
        changeSelect(createTpye);
    }

    public List<String> autoCompleteTags(String query) {

        List<String> tags = assetTagsMapper.getTagsByTenant(loginUser.getTenantUID());

        if (org.springframework.util.StringUtils.isEmpty(query)) {
            return tags;
        }

        tags = tags.stream().filter(tag -> tag.contains(query)).collect(Collectors.toList());

        if (tags.isEmpty()) {
            tags.add(query);
        }

        return tags;
    }

    /**
     * 获取所有主目录信息
     *
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory() {
        return zkbFileDirectoryService.findMainDirectory(null);
    }

    /**
     * 根据主目录值获取子目录信息
     *
     * @return
     */
    public List<ZkbFileDirectory> getSubZkbFileDirectoryList() {
        if (this.selected == null || this.selected.getMainDirectory() == null) {
            return null;
        }
        List<ZkbFileDirectory> mainZkbFileDirectory = this.zkbFileDirectoryService.findByLevelAndName(
                ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE,
                this.selected.getMainDirectory());
        if (CollectionUtils.isEmpty(mainZkbFileDirectory)) {
            return null;
        }
        ZkbFileDirectory zkbFileDirectory = new ZkbFileDirectory();
        zkbFileDirectory = mainZkbFileDirectory.get(0);
        subZkbFileDirectoryList = this.zkbFileDirectoryService.findByZkbFileDirectory(zkbFileDirectory);
        return subZkbFileDirectoryList;
    }

    public void setSubZkbFileDirectoryList(List<ZkbFileDirectory> subZkbFileDirectoryList) {
        this.subZkbFileDirectoryList = subZkbFileDirectoryList;
    }

    public int getAssetChangeHistoryCount() {
        return changeHistoryList.size();
    }

    public boolean getIsSelect46Account() {
        return Objects.equals(loginUser.getSiteId(), 46) && loginUser.getTenantUID().equals(loginUser.getSiteUID());
    }

    private TreeNode buildTree(List<OrgInfo> orgList, int siteId) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("org", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            rootNode.setSelectable(true);
            rootNode.setRowKey("org_" + org.getId());
            if (treeMap.containsKey(org.getParentUID())) {
                treeMap.get(org.getParentUID()).getChildren().add(treeMap.get(org.getUid()));
            } else {
                rootNode.getChildren().add(treeMap.get(org.getUid()));
            }
        }
        TreeNodeUtil.sortTreeNodeByName(rootNode);
        rootNode.setExpanded(true);
        return rootNode;
    }

    public void filterTreeNode() {
        List<OrgInfo> orgList = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid(), treeNodeNameFilter, loginUser.getSiteId());
    }

    public boolean getAssetScrapEnable() {
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(), loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "assetScrap");
        return StringUtils.isNotEmpty(value) ? value.equals("true") : false;

    }

    public boolean getAssetScrapFunction() {
        Set<String> functionListByFeatureAndRoles = userContextService.getFunctionListByFeatureAndRoles();
        return functionListByFeatureAndRoles.contains(ApmFunction.ASSET_WEB_ASSET_SCRAP_ASSET_BUTTON.name()) || functionListByFeatureAndRoles.contains(ApmFunction.ASSET_WEB_ASSET_SCRAP_CLINICAL_BUTTON.name());

    }

    public boolean getIsAssetStaff() {
//        List<SysRole> userRoles = sysRoleRepository.getUserRolesByUserId(loginUser.getId());
//        List<String> strings = Arrays.asList(ApmSystemRole.ClinicalStaff.name(), ApmSystemRole.DeptHead.name(), ApmSystemRole.NursingStaff.name());
//        List<SysRole> roleList = userRoles.stream().filter(item->!strings.contains(item.getName()) ).collect(Collectors.toList());
        Set<String> functionListByFeatureAndRoles = userContextService.getFunctionListByFeatureAndRoles();
        if (!functionListByFeatureAndRoles.contains(ApmFunction.ASSET_WEB_ASSET_SCRAP_ASSET_BUTTON.name())) {
            return false;
        } else {
            return true;
        }

    }

    public boolean getUploadFlag() {
        return uploadFlag;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        switch (eventName) {
            case MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE:
                SysAuthGroup selectedAuthGroup = (SysAuthGroup) eventObject;
                selectedObjectMap.put("selectedAuthGroup", selectedAuthGroup);
                break;
            case AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE:
                AssetTypeGroup assetTypeGroup = (AssetTypeGroup) eventObject;
                selectedObjectMap.put("selectedAssetTypeGroup", assetTypeGroup);
                if (userContextService.enableAssetGroupAssetTypeGroupAssociation()) {
                    selected.setAssetGroup(assetTypeGroup.getAssetGroup());
                }
                customConfig.getAssetFields().forEach((k, v) -> {
                    requiredInput(assetTypeGroup.getEncoded(), v);
                });
                break;
            default:
                break;
        }
    }

    public boolean secondment() {
        if (StringUtils.isBlank(selected.getUid())) {
            return false;
        }
        return dao.secondment(selected.getUid()) > 0;
    }

    public void checkSerialNumExists() {
        if (supportingService.isAssetFieldDuplicate("serialNum", selected.getSerialNum(), selected.getSiteUID(), selected.getUid())) {
            WebUtil.addStickyErrorMessage("制造商序列号已存在！");
        }
    }

    private boolean checkSerialNumUnique() {
        if (StringUtils.isEmpty(selected.getSerialNum())) {
            return true;
        }
        return supportingService.isAssetFieldDuplicate("serialNum", selected.getSerialNum(), selected.getSiteUID(), selected.getUid());
    }

    public void requiredInput(String encoded, CustomConfigAssetField customConfigAssetField) {
        if (customConfigAssetField.isRequired() && BooleanUtils.isTrue(assetInfoRequireField.get(customConfigAssetField.getKey()))) {
            return;
        }
        if (StringUtils.isEmpty(encoded) || MapUtils.isEmpty(customConfigAssetField.getAssetTypeRequired())) {
            return;
        }
        if (assetInfoRequireField.get(customConfigAssetField.getKey()) == null) {
            assetInfoRequireField.put(customConfigAssetField.getKey(), customConfigAssetField.isRequired());
            if (customConfigAssetField.isRequired()) {
                return;
            }
        }
        customConfigAssetField.setRequired(BooleanUtils.isTrue(customConfigAssetField.getAssetTypeRequired().get(encoded)));
    }

    public boolean isApproval() {
        return approval;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }


    public String getEquipmentDisposalMethod() {
        return equipmentDisposalMethod;
    }

    public void setEquipmentDisposalMethod(String equipmentDisposalMethod) {
        this.equipmentDisposalMethod = equipmentDisposalMethod;
    }

    public String getCustomDisposalMethod() {
        return customDisposalMethod;
    }

    public void setCustomDisposalMethod(String customDisposalMethod) {
        this.customDisposalMethod = customDisposalMethod;
    }

    public boolean getShowCustomDisposalMethod() {
        return showCustomDisposalMethod;
    }

    public void setShowCustomDisposalMethod(boolean customDisposalMethod) {
        this.showCustomDisposalMethod = customDisposalMethod;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public SpecialAssetInfo getSpecialAssetInfo() {
        return specialAssetInfo;
    }

    public void setSpecialAssetInfo(SpecialAssetInfo specialAssetInfo) {
        this.specialAssetInfo = specialAssetInfo;
    }

    public void checkAssessmentNoExists() {
        List<SpecialAssetInfo> list = specialAssetInfoService.findByAssessmentNoAndTenantUID(specialAssetInfo.getAndAssessmentNo(), selected.getTenantUID());
        if (CollectionUtils.isNotEmpty(list)) {
            WebUtil.addWarnMessage("预评批号已存在！");
        }
    }

    private AssetAnnualInspectionReport assetAnnualInspectionReport;
    private PermitInfo permitInfo;

    public void onAddReport() {
        assetAnnualInspectionReport = new AssetAnnualInspectionReport();
    }

    public void onAddPermitInfo() {
        permitInfo = new PermitInfo();
    }

    public void onEditReport(String uid, Integer nodId, String name) {
        for (AssetAnnualInspectionReport report : specialAssetInfo.getAssetAnnualInspectionReports()) {
            if (StringUtils.isNotBlank(uid) && uid.equals(report.getUid())) {
                assetAnnualInspectionReport = report;
                return;
            }
            if (StringUtils.isNotBlank(name) && name.equals(report.getName())) {
                assetAnnualInspectionReport = report;
                return;
            }
            if (null != nodId && nodId == report.getNodeId()) {
                assetAnnualInspectionReport = report;
                return;
            }

        }
    }

    public void onEditPermitInfo(String uid, Integer nodId, String name) {
        for (PermitInfo permit : specialAssetInfo.getPermitInfos()) {
            if (StringUtils.isNotBlank(uid) && uid.equals(permit.getUid())) {
                permitInfo = permit;
                return;
            }
            if (StringUtils.isNotBlank(name) && name.equals(permit.getCertificateName())) {
                permitInfo = permit;
                return;
            }
            if (null != nodId && nodId == permit.getNodeId()) {
                permitInfo = permit;
                return;
            }
        }
    }


    public void delReport(String name) {
        List<AssetAnnualInspectionReport> collect = specialAssetInfo.getAssetAnnualInspectionReports().stream().filter(item -> !name.equals(item.getName())).collect(Collectors.toList());
        specialAssetInfo.setAssetAnnualInspectionReports(collect);

    }


    public void delPermitInfo(String name) {
        List<PermitInfo> collect = specialAssetInfo.getPermitInfos().stream().filter(item -> !name.equals(item.getCertificateName())).collect(Collectors.toList());
        specialAssetInfo.setPermitInfos(collect);

    }

    public void saveReport() {

        if (assetAnnualInspectionReport != null && (assetAnnualInspectionReport.getUid() != null || assetAnnualInspectionReport.getNodeId() != null)) {
            return;
        }
        List<AssetAnnualInspectionReport> assetAnnualInspectionReports = specialAssetInfo.getAssetAnnualInspectionReports();
        if (assetAnnualInspectionReports == null) {
            assetAnnualInspectionReports = new ArrayList<>();
        }
        assetAnnualInspectionReport.setCreatedByName(loginUser.getName());
        assetAnnualInspectionReport.setCreatedByUid(loginUser.getUid());
        assetAnnualInspectionReport.setNodeId(assetAnnualInspectionReports.size());
        assetAnnualInspectionReports.add(assetAnnualInspectionReport);
    }

    public void savePermitInfo() {
        if (permitInfo != null && (permitInfo.getUid() != null || permitInfo.getNodeId() != null)) {
            return;
        }
        List<PermitInfo> permitInfos = specialAssetInfo.getPermitInfos();
        if (permitInfos == null) {
            permitInfos = new ArrayList<>();
        }
        permitInfo.setTenantUID(loginUser.getTenantUID());
        permitInfo.setSiteUID(loginUser.getSiteUID());
        permitInfo.setHospitalUID(loginUser.getHospitalUID());
        permitInfo.setInstitutionUID(loginUser.getInstitutionUID());
        permitInfo.setCreatedByName(loginUser.getName());
        permitInfo.setCreatedByUid(loginUser.getUid());
        permitInfo.setNodeId(permitInfos.size());

        permitInfos.add(permitInfo);
    }

    public AssetAnnualInspectionReport getAssetAnnualInspectionReport() {
        return assetAnnualInspectionReport;
    }

    public void setAssetAnnualInspectionReport(AssetAnnualInspectionReport assetAnnualInspectionReport) {
        this.assetAnnualInspectionReport = assetAnnualInspectionReport;
    }

    public PermitInfo getPermitInfo() {
        return permitInfo;
    }

    public void setPermitInfo(PermitInfo permitInfo) {
        this.permitInfo = permitInfo;
    }


    public List<AssetAnnualInspectionReport> getAssetAnnualInspectionReports() {
        AssetAnnualInspectionReport assetAnnualInspectionReport = new AssetAnnualInspectionReport();
        assetAnnualInspectionReport.setNodeId(0);
        List<AssetAnnualInspectionReport> assetAnnualInspectionReports = null;
        if (specialAssetInfo.getAssetAnnualInspectionReports() == null) {
            assetAnnualInspectionReports = new ArrayList<>();
        } else {
            List<AssetAnnualInspectionReport> collect = specialAssetInfo.getAssetAnnualInspectionReports().stream().filter(item -> item.getNodeId() == null || item.getNodeId() != 0).collect(Collectors.toList());
            for (int i = 0; i < collect.size(); i++) {
                AssetAnnualInspectionReport assetAnnualInspectionReport1 = collect.get(i);
                assetAnnualInspectionReport1.setNodeId(i + 1);
            }
            assetAnnualInspectionReports = collect;

        }
        specialAssetInfo.setAssetAnnualInspectionReports(assetAnnualInspectionReports);
        assetAnnualInspectionReports.add(assetAnnualInspectionReport);
        return assetAnnualInspectionReports;
    }

    private Set<PermitInfo> permitInfos;

    public List<PermitInfo> getPermitInfos() {
        PermitInfo permitInfo = new PermitInfo();
        permitInfo.setNodeId(0);
        List<PermitInfo> permitInfos = null;
        if (CollectionUtils.isEmpty(specialAssetInfo.getPermitInfos())) {
            permitInfos = new ArrayList<>();

        } else {
            List<PermitInfo> collect = specialAssetInfo.getPermitInfos().stream().filter(item -> item.getNodeId() == null || item.getNodeId() != 0).collect(Collectors.toList());
            for (int i = 0; i < collect.size(); i++) {
                PermitInfo permitInfo1 = collect.get(i);
                permitInfo1.setNodeId(i + 1);
            }
            permitInfos = collect;

        }
        specialAssetInfo.setPermitInfos(permitInfos);
        permitInfos.add(permitInfo);

        return permitInfos;

    }

    public void processInputChange() {
        System.out.println("processInputChange");
    }


    public void removePermitAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        Set<V2_BlobObject> blobObjects = permitInfo.getBlobObjects();
        blobObjects = blobObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toSet());
        permitInfo.setBlobObjects(blobObjects);
    }

    public void removeReportAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        Set<V2_BlobObject> blobObjects = assetAnnualInspectionReport.getBlobObjects();
        blobObjects = blobObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toSet());
        permitInfo.setBlobObjects(blobObjects);
    }

    public void removeReportAttachmentById(String blobId, Integer nodeId) {
        blobService.deleteBlobFileById(blobId);

        List<AssetAnnualInspectionReport> collect = specialAssetInfo.getAssetAnnualInspectionReports().stream().filter(item -> item.getNodeId() == nodeId).collect(Collectors.toList());
        Set<V2_BlobObject> blobObjects = collect.get(0).getBlobObjects();
        blobObjects = blobObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toSet());
        for (AssetAnnualInspectionReport annualInspectionReport : specialAssetInfo.getAssetAnnualInspectionReports()) {
            if (annualInspectionReport.getNodeId() == nodeId) {
                annualInspectionReport.setBlobObjects(blobObjects);
            }
        }
    }

    public void removePermitAttachmentById(String blobId, Integer nodeId) {
        blobService.deleteBlobFileById(blobId);

        List<PermitInfo> collect = specialAssetInfo.getPermitInfos().stream().filter(item -> item.getNodeId() == nodeId).collect(Collectors.toList());
        Set<V2_BlobObject> blobObjects = collect.get(0).getBlobObjects();
        blobObjects = blobObjects.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toSet());
        for (PermitInfo permitInfo : specialAssetInfo.getPermitInfos()) {
            if (permitInfo.getNodeId() == nodeId) {
                permitInfo.setBlobObjects(blobObjects);
            }
        }
    }


    public void handleFileUploadPermit(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
            if (permitInfo.getBlobObjects() == null) {
                Set<V2_BlobObject> objects = new HashSet<>();
                permitInfo.setBlobObjects(objects);
            }
            permitInfo.getBlobObjects().add(blobObject);
//            blobObject.setBoId(permitInfo.getUid());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_SpecialAssetPermitInfo);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(permitInfo.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadReport(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
            if (assetAnnualInspectionReport.getBlobObjects() == null) {
                Set<V2_BlobObject> objects = new HashSet<>();
                assetAnnualInspectionReport.setBlobObjects(objects);
            }
            assetAnnualInspectionReport.getBlobObjects().add(blobObject);
//            blobObject.setBoId(permitInfo.getUid());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_SpecialAssetReportInfo);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(assetAnnualInspectionReport.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void onAfterNewObjectPermit() {

        if (specialAssetInfo.getPermitInfos() != null) {
            specialAssetInfo.getPermitInfos().forEach(item -> {
                Set<V2_BlobObject> blobObjects = item.getBlobObjects();
                if (blobObjects != null) {
                    blobObjects.forEach(ite -> {
                        if (ite.getBoUuid() == null) {
//                                ite.setBoId(object.getId());
                            ite.setBoUuid(item.getUid());
                            blobService.saveBlobObject(ite);
                        }
                    });
                }

            });
        }

        if (specialAssetInfo.getAssetAnnualInspectionReports() != null) {
            specialAssetInfo.getAssetAnnualInspectionReports().forEach(item -> {
                Set<V2_BlobObject> blobObjects = item.getBlobObjects();
                if (blobObjects != null) {
                    blobObjects.forEach(ite -> {
//                                ite.setBoId(object.getId());
                        ite.setBoUuid(item.getUid());
                        blobService.saveBlobObject(ite);

                    });
                }

            });
        }

    }

    public boolean verifyFileSuffixName(String fileName, String fileSuffixName) {

        if (fileName.contains(fileSuffixName)) {
            return true;
        }
        return false;
    }


    public void getSignature() {
        this.signatureContent = null;
        this.signatureContentStream = null;

        List<Map<String, String>> signatureList = woService.getUserSignature();

        if (!org.springframework.util.CollectionUtils.isEmpty(signatureList)) {
            String lastSignatureId = signatureList.get(signatureList.size() - 1).get("signatureUid");
            this.signatureContentStream = blobService.getBlobObjectByStorageId(lastSignatureId);
            this.signatureId = lastSignatureId;
            this.newSignature = false;
        } else {
            this.newSignature = true;
        }
    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
    }


    public void getSig() {

        if (StringUtils.isNotEmpty(selected.getSiteUID())) {
            WorkflowConfig workflowConfig = workflowService.getWorkflowByOrg(selected.getSiteUID());
            if (workflowConfig != null && Boolean.TRUE.equals(workflowConfig.getPartsNeedSign())) {
                // 如果勾选了‘备件审核需要签名’，校验是否已经签名 弹出签名窗口
                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
                response.setStatus(205);
                return;
            }
        }
    }

    private SelectItemDto selectItemDto;

    public List<SelectItemDto> autoAssetClassification32() {
        AssetClassificationModel assetClassificationModel = new AssetClassificationModel();
        assetClassificationModel.setId(selected.getUid());
        assetClassificationModel.setBrand(selected.getMakerShortName());
        assetClassificationModel.setPattern(selected.getFunctionType());
        assetClassificationModel.setSourceName(selected.getName());
        if (selected.getManufacture() != null) {
            assetClassificationModel.setFactory(selected.getManufacture());
        } else if (selected.getManufactureId() != null) {
            Supplier supplier = supplierDao.findByUid((selected.getManufactureId()));
            assetClassificationModel.setFactory(supplier != null ? supplier.getName() : null);
        }
        // ONNX FULL 模型扩展字段
        assetClassificationModel.setDepartment(selected.getClinicalDeptName());
        String riskVal = selected.getRiskGrade() != null
                ? FieldValueMessageController.fieldValue("riskGrade", String.valueOf(selected.getRiskGrade())) : null;
        assetClassificationModel.setRiskLevel(StringUtils.isNotBlank(riskVal) ? riskVal : null);
        String meterVal = selected.getMeasureGrade() != null
                ? FieldValueMessageController.fieldValue("measureGrade", String.valueOf(selected.getMeasureGrade())) : null;
        assetClassificationModel.setMeterLevel(StringUtils.isNotBlank(meterVal) ? meterVal : null);
        assetClassificationModel.setRegistrationNo(selected.getRegistrationNo());
        assetClassificationModel.setIsEmergencyDevice(
                selected.getEmergencyEquipment() != null ? FieldValueMessageController.fieldValue("emergencyEquipment", String.valueOf(selected.getEmergencyEquipment())) : null);
        assetClassificationModel.setPurchasePrice(
                selected.getPurchasePrice() != null ? String.valueOf(selected.getPurchasePrice()) : null);
        return masterDataService.autoAssetClassification32(assetClassificationModel);

    }

    public void assetClassification32Change() {
        if (selectItemDto != null) {
            selected.setAssetClassification32(selectItemDto.getLabel());
        }
    }

    public SelectItemDto getSelectItemDto() {
        return selectItemDto;
    }

    public void setSelectItemDto(SelectItemDto selectItemDto) {
        this.selectItemDto = selectItemDto;
    }

    @Setter
    @Getter
    private String selectedFileItemId;

    public void nameplatePhotoPreview(String itemId) {
        selectedFileItemId = itemId;
    }

    public void plateRecognition(String objectId) {
        try {
            String token = UserContextService.getAccessToken();
            String gatewayURL = microServiceUtil.getGatewayURL(GatewayURL.PLATE_RECOGNITION);
            gatewayURL = gatewayURL.replace("{objectId}", objectId).concat("?assetUid=").concat(selected == null ? "" : selected.getUid() == null ? "" : selected.getUid());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token);
            HttpEntity<Map> requestEntity = new HttpEntity<Map>(headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(gatewayURL, HttpMethod.GET, requestEntity, JSONObject.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject body = response.getBody();
                JSONObject data = body.getJSONObject("data");
                equipmentNameplate = data.toJavaObject(EquipmentNameplate.class);
                WebUtil.addSuccessMessage(WebUtil.getMessage("nameplateRecognition") + "完成");
            }
        } catch (Exception e) {
            // 识别失败，请重新上传图片之类的
            equipmentNameplate = new EquipmentNameplate();
            logger.info(e.getLocalizedMessage(), e);
            WebUtil.addErrorMessage(WebUtil.getMessage("nameplateRecognition") + "识别失败，请重新上传图片");
        }
    }

    public void plateRecognitionImage(String objectId) {
        try {
            String token = UserContextService.getAccessToken();
            String gatewayURL = microServiceUtil.getGatewayURL(GatewayURL.IMAGE_CONTENT_EXTRACTION);
            gatewayURL = gatewayURL.replace("{objectId}", objectId).concat("?assetUid=").concat(selected == null ? "" : selected.getUid() == null ? "" : selected.getUid());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token);
            HttpEntity<Map> requestEntity = new HttpEntity<Map>(headers);
            ResponseEntity<JSONObject> response = restTemplate.exchange(gatewayURL, HttpMethod.GET, requestEntity, JSONObject.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject body = response.getBody();
                JSONObject data = body.getJSONObject("data");
                result = data.toJavaObject(Map.class);
                WebUtil.addSuccessMessage(WebUtil.getMessage("nameplateRecognition") + "完成");
            }
        } catch (Exception e) {
            // 识别失败，请重新上传图片之类的
            result = null;
            logger.info(e.getLocalizedMessage(), e);
            WebUtil.addErrorMessage(WebUtil.getMessage("nameplateRecognition") + "识别失败，请重新上传图片");
        }
    }

    public void applyResult() {
        String deviceName = equipmentNameplate.getDevice_name();
        if (StringUtils.isNotEmpty(deviceName) && useValue.get("device_name")) {
            this.selected.setName(deviceName);
        }

        String financingNum = equipmentNameplate.getPattern();
        if (StringUtils.isNotEmpty(financingNum) && useValue.get("pattern")) {
            this.selected.setFunctionType(financingNum);
        }

        String serialNum = equipmentNameplate.getSerial_num();
        if (StringUtils.isNotEmpty(serialNum) && useValue.get("serial_num")) {
            this.selected.setSerialNum(serialNum);
            if (checkSerialNumUnique()) {
                WebUtil.addStickyErrorMessage("制造商序列号已存在！");
            }
        }

        String manufactory = equipmentNameplate.getManufactory();
        if (StringUtils.isNotEmpty(manufactory) && useValue.get("manufactory")) {
//            List<Supplier> suppliers = autoCompleteManufacture("");
//            Optional<Supplier> first = suppliers.stream().filter(item -> manufactory.equalsIgnoreCase(item.getName())).findFirst();

            List<Supplier> byTenantUIDAndNameAndManufacturerT = supplierDao.findByTenantUIDAndNameAndManufacturerT(selected.getTenantUID(), manufactory, Boolean.TRUE);


            Supplier supplier = null;
            if (CollectionUtils.isNotEmpty(byTenantUIDAndNameAndManufacturerT)) {
                supplier = byTenantUIDAndNameAndManufacturerT.get(0);
            } else {
                newSupplier = new Supplier();
                newSupplier.setName(manufactory);
                newSupplier.setManufacturerT(Boolean.TRUE);
                createTpye = "manufacturer";
                saveNewSupplier();
                supplier = newSupplier;
                WebUtil.addSuccessMessage("制造商创建成功");
            }
            selectedObjectMap.put("manufacturer", supplier);
            this.selected.setManufacture(supplier.getName());
            this.selected.setManufactureId(supplier.getId().toString());
            createTpye = null;
        }

        String registryNum = equipmentNameplate.getRegistry_num();
        if (StringUtils.isNotEmpty(registryNum) && useValue.get("registry_num")) {
            this.selected.setRegistrationNo(registryNum);
        }

        String usingYear = equipmentNameplate.getUsing_year();
        if (StringUtils.isNotEmpty(usingYear) && useValue.get("using_year")) {
            try {
                Pattern pattern = Pattern.compile("(?:\\d+\\.?\\d*|\\.\\d+)");
                Matcher matcher = pattern.matcher(usingYear);
                StringBuffer buffer = new StringBuffer();
                while (matcher.find()) {
                    buffer.append(matcher.group());
                }
                this.selected.setLifecycleInYear(Double.valueOf(buffer.toString()));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        Date factoryDate = equipmentNameplate.getFactory_date();
        if (factoryDate != null && useValue.get("factory_date")) {
            this.selected.setManufactDate(factoryDate);
        }
        equipmentNameplate = null;

    }

    public boolean isLoginUser(String userUid) {
        return loginUser.getUid().equals(userUid);
    }

    public RiskLevel[] getRiskLevels() {
        return RiskLevel.values();
    }

    /**
     * 打开“注册证-知识库参考”对话框，用当前资产数据初始化表单
     */
    public void showUdidInfo() {
        udidInfo = new UdidInfoForm();
        udidInfo.fillValue(selected.getRegistrationNo(), new AutoCompleteVo(selected.getName(), selected.getName()), selected.getFunctionType(),
                supplierService.findSupplierById(selected.getManufactureId()), getApmMedicalClass(selected.getMedicalApparatusClassification()));

        udidInfo.setMedicalClassTree(buildMedicalClassTree(selected.getRegistrationNo(), medicalInstrumentsClassificationCompCtrl.getSysAuthGroupTreeForUdid()));
        udidInfo.setChecked(false);
    }

    /**
     * 将对话框中的表单值应用到资产对象，逐字段校验必填项
     */
    public void applyUidInfo() {
        if (needEdit("registrationNo")) {
            if (StringUtils.isBlank(udidInfo.getRegistryNum())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("registrationNo") + WebUtil.getMessage("ValidationRequire"));
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
            selected.setRegistrationNo(udidInfo.getRegistryNum());
        }
        if (needEdit("name")) {
            if (Objects.isNull(udidInfo.getAssetName()) || StringUtils.isBlank(udidInfo.getAssetName().getValue())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("name") + WebUtil.getMessage("ValidationRequire"));
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
            selected.setName(udidInfo.getAssetName().getValue());
        }
        if (needEdit("functionType")) {
            if (customConfig.getAssetFields().get("functionType").isRequired() && StringUtils.isBlank(udidInfo.getFunctionType())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("functionType") + WebUtil.getMessage("ValidationRequire"));
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
            selected.setFunctionType(udidInfo.getFunctionType());
        }
        if (needEdit("manufacture")) {
            if (customConfig.getAssetFields().get("manufacture").isRequired() && Objects.isNull(udidInfo.getManufacture())) {
                WebUtil.addErrorMessage(WebUtil.getMessage("manufacture") + WebUtil.getMessage("ValidationRequire"));
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
            if (Objects.nonNull(udidInfo.getManufacture())) {
                if (Objects.equals(-1, udidInfo.getManufacture().getId())) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("manufactureNotExists"));
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("validationFailed", true);
                    return;
                }
                selected.setManufacture(udidInfo.getManufacture().getName());
                selected.setManufactureId(udidInfo.getManufacture().getId().toString());
                selected.setManufactureTel(udidInfo.getManufacture().getTel());
                selectedObjectMap.put("manufacturer", udidInfo.getManufacture());
            }
        }
        if (needEdit("medicalApparatusClassification")) {
            if (customConfig.getAssetFields().get("medicalApparatusClassification").isRequired()
                    && (Objects.isNull(udidInfo.getMedicalClass()) || StringUtils.isBlank(udidInfo.getMedicalClass().getClassificationCode()))) {
                WebUtil.addErrorMessage(WebUtil.getMessage("medicalApparatusClassification") + WebUtil.getMessage("ValidationRequire"));
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                return;
            }
            if (Objects.nonNull(udidInfo.getMedicalClass()) && StringUtils.isNotBlank(udidInfo.getMedicalClass().getClassificationCode())) {
                SysAuthGroup sysAuthGroup = authGroupDao.findByClassificationCode(udidInfo.getMedicalClass().getClassificationCode());
                if (Objects.isNull(sysAuthGroup)) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("medicalApparatusClassificationNotExists"));
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("validationFailed", true);
                    return;
                }
                selected.setMedicalApparatusClassification(udidInfo.getMedicalClass().getClassificationCode());
                medicalInstrumentsClassificationCompCtrl.onServerEvent(MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE, selected.getMedicalApparatusClassification());
            }
        }
    }

    /**
     * 知识库校验：将表单各字段与注册证知识库比对，标记一致/不一致状态
     */
    public void checkUdid() {
        int count = 0;
        StringBuilder warnColumn = new StringBuilder();

        if (StringUtils.isBlank(udidInfo.getRegistryNum())) {
            udidInfo.clearFlag();
            udidInfo.setRegistryNumFromUdid(false);
            udidInfo.setChecked(false);
            udidInfo.setMessage("注册证号为空");
            return;
        }

        UdidRegistryNumInfo udidRegistryNumInfo = udidInfoService.selectByRegistryNum(udidInfo.getRegistryNum());

        if (Objects.isNull(udidRegistryNumInfo)) {
            udidInfo.clearFlag();
            udidInfo.setRegistryNumFromUdid(false);
            udidInfo.setChecked(false);
            udidInfo.setMessage("该注册证号在知识库中不存在");
        } else {
            udidInfo.setRegistryNumFromUdid(true);
            if (needEdit("name") && (Objects.isNull(udidInfo.getAssetName()) || !Objects.equals(udidRegistryNumInfo.getProductName(), udidInfo.getAssetName().getValue()))) {
                count++;
                warnColumn.append("名称");
                udidInfo.setAssetNameFromUdid(false);
            } else {
                udidInfo.setAssetNameFromUdid(true);
            }
            if (needEdit("functionType") && !UdidInfoService.parseJsonArrayToStringList(udidRegistryNumInfo.getGgxh()).contains(udidInfo.getFunctionType())) {
                if (count > 0) {
                    warnColumn.append("、");
                }
                count++;
                warnColumn.append("设备型号");
                udidInfo.setFunctionTypeFromUdid(false);
            } else {
                udidInfo.setFunctionTypeFromUdid(true);
            }
            if (needEdit("manufacture") && (Objects.isNull(udidInfo.getManufacture()) || !Objects.equals(udidRegistryNumInfo.getRegistrantName(), udidInfo.getManufacture().getName()))) {
                if (count > 0) {
                    warnColumn.append("、");
                }
                count++;
                warnColumn.append("制造商");
                udidInfo.setManufactureFromUdid(false);
            } else {
                udidInfo.setManufactureFromUdid(true);
            }
            List<String> medicalClassList = UdidInfoService.parseJsonArrayAndStripHyphens(udidRegistryNumInfo.getFlbm());
            if (needEdit("medicalApparatusClassification") && CollectionUtils.isNotEmpty(medicalClassList)
                    && (Objects.isNull(udidInfo.getMedicalClass()) || !medicalClassList.contains(udidInfo.getMedicalClass().getClassificationCode()))) {
                if (count > 0) {
                    warnColumn.append("、");
                }
                count++;
                warnColumn.append("医疗器械分类");
                udidInfo.setMedicalClassFromUdid(false);
            } else {
                udidInfo.setMedicalClassFromUdid(true);
            }
            if (count > 0) {
                udidInfo.setChecked(false);
                udidInfo.setMessage("检测到" + count + "个字段与知识库不一致（" + warnColumn + "）");
            } else {
                udidInfo.setChecked(true);
                udidInfo.setMessage("");
            }
        }
    }

    /**
     * 重置表单为原始资产数据（编辑模式从数据库重新读取，创建模式清空）
     */
    public void resetUdid() {
        AssetInfo currentAsset = null;
        if (isEditAction()) {
            currentAsset = assetInfoService.getAssetInfo(selected.getId());
        }
        if (Objects.nonNull(currentAsset)) {
            udidInfo.fillValue(currentAsset.getRegistrationNo(), new AutoCompleteVo(currentAsset.getName(), currentAsset.getName()), currentAsset.getFunctionType(),
                    supplierService.findSupplierById(currentAsset.getManufactureId()), getApmMedicalClass(currentAsset.getMedicalApparatusClassification()));
        } else {
            udidInfo.clearValue();
        }
        udidInfo.clearFlag();
        udidInfo.clearMessage();
    }

    /**
     * 根据分类代码拼接医疗器械分类的显示文本，格式："代码  名称"
     */
    private SysAuthGroup getApmMedicalClass(String medicalClassCode) {
        if (StringUtils.isBlank(medicalClassCode)) {
            return null;
        }
        List<SysAuthGroup> sysAuthGroups = authGroupDao.listSysAuthGroupForUdid(Collections.singletonList(medicalClassCode));
        return CollectionUtils.isEmpty(sysAuthGroups) ? null : sysAuthGroups.get(0);
    }

    public String udidGroup(Object o) {
        return DataSourceEnum.UDID.getSourceName();
    }

    public String groupBySource(Supplier supplier) {
        return supplier.isFromUdid() ? DataSourceEnum.UDID.getSourceName() : DataSourceEnum.APM.getSourceName();
    }

    /**
     * 注册证号模糊搜索（返回纯字符串列表）
     */
    public List<String> completeUdidRegistryNum(String prefix) {
        return udidInfoService.fuzzySearchRegistryNum(prefix);
    }

    /**
     * 名称模糊搜索，合并 UDI 知识库 + APM 已有资产名称，按来源分组
     */
    public List<AutoCompleteVo> completeUdidAssetName(String prefix) {
        List<AutoCompleteVo> udidAssetList = udidInfoService.fuzzySearchAssetName(udidInfo.getRegistryNum(), prefix)
                .stream()
                .map(s -> new AutoCompleteVo(s, s, DataSourceEnum.UDID.getSourceName()))
                .collect(Collectors.toList());

        List<AutoCompleteVo> apmAssetList = completeAssetName(prefix)
                .stream()
                .map(s -> new AutoCompleteVo(s, s, true, DataSourceEnum.APM.getSourceName()))
                .collect(Collectors.toList());

        udidAssetList.addAll(apmAssetList);
        return udidAssetList;
    }

    /**
     * 设备型号模糊搜索（返回纯字符串列表）
     */
    public List<String> completeUdidFunctionType(String prefix) {
        return udidInfoService.fuzzySearchFunctionType(udidInfo.getRegistryNum(), prefix);
    }

    /**
     * 制造商模糊搜索，合并 UDI 知识库 + APM 供应商列表
     */
    public List<Supplier> completeUdidManufacture(String query) {
        // apm供应商
        List<Supplier> apmSupplierList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID(), query, 20);

        // udid供应商
        List<String> udidSupplierNameList = udidInfoService.fuzzySearchManufacture(udidInfo.getRegistryNum(), query);

        // 构造NMPA制造商列表
        List<Supplier> udidSupplierList = udidSupplierNameList.stream()
                .map(name -> {
                    Supplier supplier = new Supplier();
                    Supplier apmSupplier = supplierDao.findByManufacturerTAndTenantUIDAndName(loginUser.getTenantUID(), name);
                    supplier.setId(Objects.isNull(apmSupplier) ? -1 : apmSupplier.getId());
                    supplier.setFromUdid(true);
                    supplier.setName(name);
                    return supplier;
                }).collect(Collectors.toList());

        // 合并两个列表
        udidSupplierList.addAll(apmSupplierList);
        return udidSupplierList;
    }

    /**
     * 注册证号变更（下拉选中 / 手动输入blur）
     */
    public void onLookupRegNum() {
        String udidRegistryNum = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("udidRegistryNum");
        log.info("[onLookupRegNum] udidRegistryNum={}", udidRegistryNum);

        // 强制覆盖 bean 的值，不依赖 value 绑定回写
        udidInfo.setRegistryNum(udidRegistryNum);

        if (!Objects.equals(selected.getRegistrationNo(), udidInfo.getRegistryNum())) {
            udidInfo.setChecked(false);
            udidInfo.setMedicalClassTree(buildMedicalClassTree(udidInfo.getRegistryNum(), medicalInstrumentsClassificationCompCtrl.getSysAuthGroupTreeForUdid()));
        }
    }

    /**
     * 名称变更
     */
    public void onUdidAssetNameChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            udidInfo.setAssetName((AutoCompleteVo) ((SelectEvent) event).getObject());
        }

        if (Objects.isNull(udidInfo.getAssetName()) || !Objects.equals(selected.getName(), udidInfo.getAssetName().getValue())) {
            udidInfo.setChecked(false);
        }
    }

    /**
     * 设备型号变更
     */
    public void onUdidFunctionTypeChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            udidInfo.setFunctionType((String) ((SelectEvent) event).getObject());
        }

        if (!Objects.equals(selected.getFunctionType(), udidInfo.getFunctionType())) {
            udidInfo.setChecked(false);
        }
    }

    /**
     * 制造商变更
     */
    public void onUdidManufactureChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            udidInfo.setManufacture((Supplier) ((SelectEvent) event).getObject());
        }

        if (!Objects.equals(selected.getManufactureId(), udidInfo.getManufacture().getId().toString())) {
            udidInfo.setChecked(false);
        }
    }

    public void onUdidMedicalClassSelect(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        udidInfo.setMedicalClass((SysAuthGroup) node.getData());
    }

    public TreeNode buildMedicalClassTree(String registryNum, TreeNode apmTree) {
        TreeNode rootNode = new DefaultTreeNode("medicalClass", null, null);

        TreeNode tempUdidTree = assetInfoService.tempTreeSysAuthGroup(udidInfoService.selectSysAuthGroup(registryNum), "medicalClass");
        TreeNode udidTree = assetInfoService.finalTreeSysAuthGroupForUdid(tempUdidTree, "medicalClass", DataSourceEnum.UDID.getSourceName());
        udidTree.setSelectable(false);

        List<TreeNode> children = rootNode.getChildren();
        children.add(udidTree);
        children.add(apmTree);
        rootNode.setExpanded(true);

        return rootNode;
    }

    /**
     * 初始化新建制造商表单，从下拉面板按钮传入的 supplierName 参数预填名称
     */
    public void prepareCreateUdidSupplier() {
        String supplierName = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("supplierName");

        newUdidSupplier = new Supplier();
        newUdidSupplier.setName(supplierName);
        newUdidSupplier.setManufacturerT(true);
    }

    /**
     * 制造商名称变更时校验：若该名称已存在则回填完整信息，否则保留用户输入的类型标记并重置表单
     */
    public void onUdidSupplierChange() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        Supplier supplier = supplierDao.getBySiteIdAndName(loginUser.getSiteId(), this.newUdidSupplier.getName());
        if (!ObjectUtils.isEmpty(supplier)) {
            // 已存在：直接使用数据库中的完整供应商信息
            this.newUdidSupplier = supplier;
        } else {
            // 不存在：保留用户输入的名称和类型标记，重新初始化表单
            String name = newUdidSupplier.getName();
            boolean ManufacturerT = newUdidSupplier.getManufacturerT();
            boolean SupplierT = newUdidSupplier.getSupplierT();
            boolean MaintainerT = newUdidSupplier.getMaintainerT();
            prepareCreateUdidSupplier();
            newUdidSupplier.setName(name);
            newUdidSupplier.setSupplierT(SupplierT);
            newUdidSupplier.setMaintainerT(MaintainerT);
            newUdidSupplier.setManufacturerT(ManufacturerT);
        }
    }

    /**
     * 保存新建制造商：填充租户/机构等上下文信息，保存后回填到 UDI 表单的制造商字段
     */
    public void saveNewUdidSupplier() {
        if (null == newUdidSupplier.getId()) {
            this.newUdidSupplier.setSiteId(loginUser.getSiteId());
            this.newUdidSupplier.setHospitalId(loginUser.getHospitalId());
            this.newUdidSupplier.setTenantUID(loginUser.getTenantUID());
            this.newUdidSupplier.setInstitutionUID(loginUser.getInstitutionUID());
            this.newUdidSupplier.setHospitalUID(loginUser.getHospitalUID());
            this.newUdidSupplier.setSiteUID(loginUser.getSiteUID());
            this.newUdidSupplier.setCreatedDate(new Date());
            this.newUdidSupplier.setCreatedBy(loginUser.getName());
        }
        supplierController.setSelected(newUdidSupplier);
        supplierController.saveSubmit();

        newUdidSupplier = supplierDao.findByManufacturerTAndTenantUIDAndName(loginUser.getTenantUID(), newUdidSupplier.getName());

        udidInfo.setManufacture(newUdidSupplier);
    }
}
