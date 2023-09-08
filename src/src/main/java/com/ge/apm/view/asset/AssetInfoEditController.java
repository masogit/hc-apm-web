package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.view.component.AssetTypeGroupTreeComponentController;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.constant.UsageStatusConstant;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.*;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
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

    private Boolean terminate = false;
    private CustomConfigObject customConfig;
    private List<AssetInfo> parentAssetList;
    private List<OrgInfo> clinicalDeptList;
    private List<UserAccount> ownerList;
    private List<I18nMessage> code68List;

    private AssetTagsMapper assetTagsMapper;
    private AssetTagsRepository assetTagsDao;
    private List<ZkbFileDirectory> subZkbFileDirectoryList=null;
    SysAuthGroupRepository authGroupDao;
    @Getter
    @Setter
    private List<Supplier> supplierList = Collections.emptyList();
    @Getter
    @Setter
    private List<Supplier> manuList = Collections.emptyList();
    @Getter
    @Setter
    private List<Supplier> mantainList = Collections.emptyList();
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


    private MqUtils mqUtils;

    //设备状态
    @Getter
    List<I18nMessage> assetStatusList = Collections.emptyList();

    @Getter
    private Boolean unCheckedNursesiteMaintainSite = false;
    @Getter
    private Boolean toDeleteNurseMaintainSite = false;
    private List<AssetInfo> unCheckNurseSiteMaintain = Collections.emptyList();

    public void toDelete () {
       toDeleteNurseMaintainSite = true;
    }

    @Getter @Setter
    private PmOrderRule pmOrderRuleTwoLevel;
    @Getter @Setter
    private PmOrderRule pmOrderRuleThreeLevel;
    @Getter @Setter
    private List<NurseSiteMaintainConfig> listSelectedNurseSiteMaintainConfig;
    @Getter @Setter
    private NurseSiteMaintainConfigJN selectedNurseSiteMaintainConfigJN;

    // TreeNodeName Filter
    private String treeNodeNameFilter;
    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }
    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

    @Override
    protected void init() {
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

        countyArea = userContextService.isCountyArea();
        assetTypeGroupTreeComponentController.registerController(this);

        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");

        qr_Code = WebUtil.getRequestParameter("qrCode");
        siteUid = WebUtil.getRequestParameter("siteUid");
        createRequest = acService.getCreateRequest(qr_Code, siteUid);
        if(createRequest != null){
            qrCode = qr_Code;
            isStatus = true;
            createAssetInfo();
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
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID()));
                changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
                assetLifecyleStatus = 0;
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            isStatusAssetHistory = true;
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(dao.findByUid(selectedId));
            String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
            terminate = Boolean.parseBoolean(terminateStr);
            prepareEdit();
            beforeEdit();
            assetLifecyleStatus = selected.getStatus();

            List<String> existingTags = assetTagsMapper.getTagsByAsset(this.selected.getUid());
            this.selected.setTags(existingTags);

//            changeHistoryList = assetHistoryService.getAssetChangeHistoryByJSON(selected.getUid());
            uploadFlag = getAttachList(selected.getUid(), "1").isEmpty();
            selected.setAssetUsedLifecycle(AssetInfoService.getAssetUsedLifecycle(selected.getInstallDate(), selected.getLifecycle()));
            assetTypeGroupTreeComponentController.onServerEvent(AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE, this.selected.getAssetType());
        }
        if(selected ==null){
            try {
                prepareCreate();
                selectedObjectMap.put("selectedOrg", orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID()));
                changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ownerList = ucService.getWorkerList(selected.getId(), "");
        config = configService.findConfigBySite(selected.getSiteUID());
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        editableFieldsList = ccService.getEditAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        //新增时不显示创建人
        if ("Create".equalsIgnoreCase(actionName)) {
            if (readableFieldsList.contains("createdBy")) {
                readableFieldsList.remove("createdBy");
            }
        }
        if(selected.getUnitPriceElectricity()==null){
            WorkflowConfig workflowConfig = configService.findConfigBySite(selected.getSiteUID());
            selected.setUnitPriceElectricity(workflowConfig != null ? workflowConfig.getUnitPriceElectricity() : null);
        }
        initGbTree();
        initOrgTree();

        updateClinicalDeptOnSiteChange();
        updateAssetStatusList();

        UserAccount loginUser = UserContextService.getCurrentUserAccount();

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
        assetStatusList.stream().forEach(status ->{
            if(status.getMsgKey().equals("4")){
                status.setEditable(hasScrappedAuthority());
            }else{
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
        if (selected == null){
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

        if(null != createRequest.getManufactureId()){
            Supplier supplier = supplierDao.getById(Integer.valueOf(createRequest.getManufactureId()));
            if (null != supplier){
                selected.setManufactureId(supplier.getId().toString());
                selected.setManufacture(supplier.getName());
                selectedObjectMap.put("manufacturer", supplier.getUid() == null ? null : supplierDao.findByUid(supplier.getUid()));
            }
        }
        if (null != createRequest.getOrgId()) {
            OrgInfo org = acService.getOrgInfo(createRequest.getOrgId());
            if (org != null){
                selected.setClinicalDeptId(org.getId());
                selected.setClinicalDeptUID(org.getUid());
                selectedObjectMap.put("clinicalDept", org.getUid() == null ? null : orgDao.findByUid(org.getUid()));
            }
        }
        OrgInfo siteOrg = orgDao.findByUid(createRequest.getSiteUID());
        if(siteOrg !=null){
            selectedObjectMap.put("selectedOrg", siteOrg.getSiteUID() == null ? null : orgDao.findByUid(siteOrg.getSiteUID()));//业务单元
            selected.setHospitalId(siteOrg.getId());
            selected.setSiteId(siteOrg.getSiteId());
        }
        selected.setParentAssetId(createRequest.getParentAssetId());
        selectedObjectMap.put("parentAsset", createRequest.getParentAssetId() == null ? null : dao.findById(createRequest.getParentAssetId()));//主设备
        if (null != createRequest.getUserId()) {
            UserAccount owner = acService.getUserInfo(createRequest.getUserId());
            if(owner != null){
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
                selected.setCreatedBy(submitter.getUid());
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
            AssetTypeGroup selectedAssetTypeGroup = assetTypeGroupRepository.findByEncoded(selected.getAssetType());
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
        if(getIsSelect46Account()){
            return;
        }
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
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
        if(parentAssetList != null){
            parentAssetList.clear();
        }
    }

    private void updateClinicalDeptOnSiteChange() {

        clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(selected.getSiteUID());
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
        }



    }

    public void onSupplierChange(String idx) {
        changeSelect(idx);
    }

    private void changeSelect(String idx){
        Supplier supplier = (Supplier) selectedObjectMap.get(idx);
        switch (idx) {
            case "supplier":
                selected.setSupplierId(supplier == null ? null : supplier.getId());
                selected.setVendor(supplier == null ? null : supplier.getName());
                selected.setVendorTel(supplier == null ? null : supplier.getTel());
                break;
            case "maintainer":
                selected.setMaitananceId(supplier == null ? null : supplier.getId().toString());
                selected.setMaitanance(supplier == null ? null : supplier.getName());
                selected.setMaitananceTel(supplier == null ? null : supplier.getTel());
                break;
            case "manufacturer":
                selected.setManufactureId(supplier == null ? null : supplier.getId().toString());
                selected.setManufacture(supplier == null ? null : supplier.getName());
                break;
        }
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
        if(attachements !=null){
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
        V2_BlobObject blobObject = blobService.uploadFile(event);
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
    }

    public void todelete() {
        toDeleteNurseMaintainSite = true;
    }

    public String checkIfRequireBeforeApplyChange() {
        if(!unCheckedNursesiteMaintainSite && selected.getUid()!=null) {
            AssetInfo old = dao.findByUid(selected.getUid());
            Integer oldClinicalId = old.getClinicalDeptId() == null ? 0 : old.getClinicalDeptId();
            Integer newClinicalId = selected.getClinicalDeptId() == null ? 0 : selected.getClinicalDeptId();
            if((!oldClinicalId.equals(newClinicalId)) || !StringUtils.equals(old.getAssetDeptUID(),selected.getAssetDeptUID())) {
                List<AssetInfo> assets = new ArrayList<>();
                assets.add(old);
                unCheckNurseSiteMaintain = assetInfoBulkEditCheckService.verifyForNurseSiteByBatchEdit(assets);
                if(CollectionUtils.isNotEmpty(unCheckNurseSiteMaintain)) {
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

        if(!checkDepartNumUnique()){
            WebUtil.addErrorMessage("设备编号已被占用");
            return "";
        }
        if(selected.getUnitPriceElectricity()==null){
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
        if(isStatus){
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

        // 资产分类
        if (null != selectedObjectMap.get("selectedAssetTypeGroup")) {
            AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup) selectedObjectMap.get("selectedAssetTypeGroup");
            selected.setAssetType(selectedAssetTypeGroup.getEncoded());
        }

        if (null != selectedObjectMap.get("selectedAuthGroup")) {
            SysAuthGroup selectedAuthGroup = (SysAuthGroup) selectedObjectMap.get("selectedAuthGroup");
            selected.setMedicalApparatusClassification(selectedAuthGroup.getClassificationCode());
        }

        //when mvs user create a new record, mark this asset as mvs asset
        if (UserContextService.isMVSRole() && this.selected.getMvsEquip() == null) {
            this.selected.setMvsEquip(1);
        }

        boolean isCreate=false;
        if(selected.getId()==null){
            isCreate=true;
        }
        selected.setOfflineUpdateDate(new Date());
        this.save();
        AssetInfo assetInfo = new AssetInfo();
        BeanUtils.copyProperties(selected,assetInfo);
        if(isCreate){
            assetInfo.setId(null);
        }
        this.saveAssetHistory(assetInfo);
        creteRule();
        if(CollectionUtils.isNotEmpty(unCheckNurseSiteMaintain) && toDeleteNurseMaintainSite) {
            assetInfoBulkEditCheckService.deleteNursiteMaintain(unCheckNurseSiteMaintain.get(0));
        }

        if(selected != null && selected.getId() != null && selectedPictures != null){
            acService.createAttachments(selected, selectedPictures);
        }
        if(qr_Code !=null && siteUid != null){
            acService.updateQrLib(qr_Code, 3, siteUid);
        }
        // do other stuff after save
        int status = selected.getStatus();
        if(status != assetLifecyleStatus){
            assetLifecyleService.saveLifecycle(assetLifecyleStatus,selected,UserContextService.getCurrentUserAccount());
        }
        if (this.selected.getStatus() == 4) {
            aimService.updateNurseSiteMaintain(this.selected.getUid());
        }
        assetDepreciationService.saveAssetDerpeciation(selected);

        mvsUtil.copyToMvsTableIfMvsEquip(selected, mvsAssetInfo);

        saveOrUpdateAssetTags();
        if(isStatus){
            return "/portal/asset/createAsset/RequestsList?faces-redirect=true";
        }
        if (resultStatus) {
            return getListPageLink();
        } else {
            return "";
        }
    }

    private void creteRule() {
        if(pmOrderRuleThreeLevel!=null || pmOrderRuleTwoLevel!=null) {
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
            if(pmOrderRuleTwoLevel!=null) { //二级
                rel.setRuleUID2(pmOrderRuleTwoLevel.getUid());
            }
            if(pmOrderRuleThreeLevel!=null) { //三级
                rel.setRuleUID(pmOrderRuleThreeLevel.getUid());
            }
            relRuleAssetRepository.save(rel);
        }
        if(CollectionUtils.isNotEmpty(listSelectedNurseSiteMaintainConfig)) {
            List<NurseSiteMaintainConfigItem> items = new ArrayList<>();
            for(NurseSiteMaintainConfig config : listSelectedNurseSiteMaintainConfig) {
                UserAccount userAccount = UserContextService.getCurrentUserAccount();
                config.setModifyBy(userAccount);
                config.setModifyByUid(userAccount.getUid());
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
        if(Objects.nonNull(selectedNurseSiteMaintainConfigJN)) {
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            selectedNurseSiteMaintainConfigJN.setModifyBy(userAccount);
            selectedNurseSiteMaintainConfigJN.setModifyByUid(userAccount.getUid());
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

        if(org.springframework.util.StringUtils.isEmpty(selected.getDepartNum())){
            return true;
        }

        return checkDepartNumUniqueAssetInfo();

    }

    private boolean checkDepartNumUniqueAssetInfo() {
        List<String> hisData = dao.findIdByDepartNumAndTenantUID(selected.getDepartNum(), selected.getTenantUID());
        if(hisData.isEmpty()){
            return true;
        }
        //兼容老数据
        for( String uid:hisData){
            if(uid.equals(selected.getUid())){
                return true;
            }
        }
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
            assetTags.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
            assetTags.setTag(item);
            assetTagsDao.save(assetTags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> {
            assetTagsMapper.removeTag(this.selected.getUid(), item);
        });

    }

    public String getListPageLink() {
        if(isStatus){
            return "/portal/asset/createAsset/RequestsList?faces-redirect=true";
        }
        if (!selected.getIsValid() || 4 == selected.getStatus()) {
            return "AssetTerminateEntryList?faces-redirect=true";
        }
        return "AssetList?faces-redirect=true";
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
            this.assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId(msgType, UserContextService.getCurrentUserAccount().getSiteId());
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
            object.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setCreatedDate(now);
            object.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setLastModifiedDate(now);
        } else {
            object.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            object.setLastModifiedDate(now);
        }
    }

    //转入待报废
    public String processToBeTerminated() {
        int originStatus = selected.getStatus();
        AssetInfo oldAssetInfo = assetHistoryService.depCopy(selected);
        this.selected.setStatus(4);
        update();
        assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo,selected);
        assetLifecyleService.saveLifecycle(originStatus,selected,UserContextService.getCurrentUserAccount());
        return "AssetList?faces-redirect=true";
    }

    //重新启用
    public String processToBeValid() {
        int originStatus = selected.getStatus();
        AssetInfo oldAssetInfo = assetHistoryService.depCopy(selected);
        this.selected.setStatus(1);
        this.selected.setIsValid(true);
        this.selected.setTerminateDate(null);
        update();
        assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo,selected);
        assetLifecyleService.saveLifecycle(originStatus,selected,UserContextService.getCurrentUserAccount());
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
        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public List<Supplier> autoCompleteManufacture(String query) {
        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteManufacture(query, manuList);
    }

    public List<Supplier> autoCompleteMaintainer(String query) {
        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteMaintainer(query, mantainList);
    }
    public String selectCreatedBy(String loginName){
        UserAccount user = userDao.getByLoginName(loginName);
        if(user != null){
            return user.getName();
        }
        return null;
    }
    public List<String> completeAssetName(String prefix) {
        String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
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
        }

    }

    public void getClinicalOwnerList() {
        if (null != selected.getClinicalDeptUID()) {
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptUID());
        } else {
            clinicalOwnerList = Collections.emptyList();
        }
    }

    public String submitApproval() {
        selected.setUsageStatus(UsageStatusConstant.UNDER_REVIEW);
        return applyChange();
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
        }

        if(selected.getSiteId()==46){
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


    public String createTpye=null;
    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        if("manufacturer".equals(type)){
            createTpye=type;
            newSupplier.setManufacturerT(true);
        }
        if("supplier".equals(type)){
            createTpye=type;
            newSupplier.setSupplierT(true);
        }
        if("maintainer".equals(type)){
            createTpye=type;
            newSupplier.setMaintainerT(true);
        }

    }



    public void saveNewSupplier() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        this.newSupplier.setSiteId(loginUser.getSiteId());
        this.newSupplier.setHospitalId(loginUser.getHospitalId());
        this.newSupplier.setTenantUID(loginUser.getTenantUID());
        this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
        this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
        this.newSupplier.setSiteUID(loginUser.getSiteUID());
        this.newSupplier.setCreatedDate(new Date());
        this.newSupplier.setCreatedBy(loginUser.getName());

        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();
        supplierList.add(newSupplier);
        if (StringUtils.isNotBlank(newSupplier.getUid()) && !supplierList.contains(newSupplier)) {
            supplierList.add(newSupplier);
        }
        changeSelect(createTpye);

    }

    public List<String> autoCompleteTags(String query) {

        List<String> tags = assetTagsMapper.getTagsByTenant(UserContextService.getCurrentUserAccount().getTenantUID());

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
     * @return
     */
    public List<ZkbFileDirectory> findMainDirectory() {
        return zkbFileDirectoryService.findMainDirectory(null);
    }

    /**
     * 根据主目录值获取子目录信息
     * @return
     */
    public List<ZkbFileDirectory> getSubZkbFileDirectoryList() {
        if(this.selected==null||this.selected.getMainDirectory()==null)
        {
            return null;
        }
        List<ZkbFileDirectory> mainZkbFileDirectory = this.zkbFileDirectoryService.findByLevelAndName(
                ZkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE,
                this.selected.getMainDirectory());
        if(CollectionUtils.isEmpty(mainZkbFileDirectory))
        {
            return null;
        }
        ZkbFileDirectory zkbFileDirectory = new ZkbFileDirectory();
        zkbFileDirectory=mainZkbFileDirectory.get(0);
        subZkbFileDirectoryList=this.zkbFileDirectoryService.findByZkbFileDirectory(zkbFileDirectory);
        return subZkbFileDirectoryList;
    }

    public void setSubZkbFileDirectoryList(List<ZkbFileDirectory> subZkbFileDirectoryList) {
        this.subZkbFileDirectoryList = subZkbFileDirectoryList;
    }


    private void saveAssetHistory(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("AssetInfoEditController");
        mqContext.setMethodName("applyChange");
        mqContext.setDescribe("设备详情单个修改/设备状态修改待审核");
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        mqUtils.sendAssetToMQ(newAsset,currentUserAccount,mqContext);
    }

    public int getAssetChangeHistoryCount() {
        return changeHistoryList.size();
    }

    public boolean getIsSelect46Account() {
        if (UserContextService.getCurrentUserAccount().getSiteId()==46&&UserContextService.getCurrentUserAccount().getTenantUID().equals(UserContextService.getCurrentUserAccount().getSiteUID())) {
            return true;
        }
        return false;
    }
    private TreeNode buildTree(List<OrgInfo> orgList,int siteId) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("org", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
            rootNode.setSelectable(true);
            rootNode.setRowKey("org_"+org.getId());
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
    public void filterTreeNode(){
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgList = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid(),treeNodeNameFilter,loginUser.getSiteId());
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
                break;
            default:
                break;
        }
    }
    public boolean secondment(){
        if(StringUtils.isBlank(selected.getUid())){
            return false;
        }
        return dao.secondment(selected.getUid())>0;
    }
}
