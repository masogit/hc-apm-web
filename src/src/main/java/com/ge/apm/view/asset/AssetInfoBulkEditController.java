package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.component.AssetTypeGroupTreeComponentController;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.pure.KeyLabel;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.orginfo.OrgInfoClinicalDeptService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.PinYinComparator;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.component.MedicalInstrumentsClassificationCompCtrl;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.*;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.GenerateOrderNumUtil;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoBulkEditController extends JpaCRUDController<AssetInfo>  {
    private static final Logger logger = LoggerFactory.getLogger(AssetInfoBulkEditController.class);
    private static final String TAB_SELECT_ASSETS = "SELECT_ASSETS";

    static PageRequest pageRequest = new PageRequest(0, 1000);

    private AssetInfoRepository dao;
    private AssetInfoService assetInfoService;
    private UaaService uaaService;
    private UserCommonService ucService;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgDao;
    private ConfigService configService;
    private OrgInfoClinicalDeptService clinicalDeptService;
    private UserAccountRepository userDao;
    private AssetInfoMgrService aimService;
    private AutoCompleteService autoCompleteService;
    private GbCodeRepository gbDao;
    private GbCodeService gbCodeService;
    private QrCodeService qrCodeService;
    private CustomConfigService ccService;
    private AssetInfoBulkEditService assetInfoBulkEditService;
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;
    private OrgInfoRepository orgInfoDao;
    private V2ServiceRequestRepository v2ServiceRequestRepository;
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;
    private AssetCreateService acService;
    private ZkbFileDirectoryService zkbFileDirectoryService;
    private BlobFileService blobService;
    private AssetHistoryService assetHistoryService;
    private SupplierController supplierController;
    private MedicalInstrumentsClassificationCompCtrl medicalInstrumentsClassificationCompCtrl;
    private AssetTypeGroupTreeComponentController assetTypeGroupTreeComponentController;
    private WorkflowService workflowService;
    private V2_WorkOrderService woService;
    private AssetDepartmentConvertService assetDepartmentConvertService;
    private MqUtils mqUtils;
    private UserContextService userContextService;
    private QrCodeLib createRequest;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private AssetScrapService assetScrapService;
    private GenerateOrderNumUtil generateOrderNumUtil;
    private AssetTagsMapper assetTagsMapper;
    private AssetTagsRepository assetTagsDao;
    //******* filter start

    @Getter @Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>(null, new ArrayList<>());
    @Getter @Setter
    private DualListModel<Map<String, Object>> assets1 = new DualListModel<>(null, new ArrayList<>());
    @Getter @Setter
    private TreeNode orgTree;

    @Getter @Setter
    private AssetInfo filter = new AssetInfo();

    private List<OrgInfo> clinicalDeptListAsFilter = Collections.emptyList();

    @Getter @Setter
    private String oneSearchStr;

    //******* filter end


    //****** new value start
    private List<OrgInfo> clinicalDeptList = Collections.emptyList();
    //****** new value end

    @Getter
    private List<KeyLabel> candidateColumns = new ArrayList<>();

    @Getter @Setter
    private List<String> selectedColumns = new ArrayList<>();
    @Getter @Setter
    private boolean assetGroupSecondarySupport = false;
    @Getter @Setter
    private Boolean unCheckedNurseSite = false;
    @Getter @Setter
    private Boolean toDeleteNurseMaintainSite = false;
    private List<AssetInfo> unCheckedNursiteMaintainList = new ArrayList<>();
    @Getter @Setter
    private List<I18nMessage> assetGroupSecondaryList;
    @Getter @Setter
    private List<UserAccount> ownerList;
    @Getter @Setter
    private List<AssetInfo> parentAssetList;
    @Getter@Setter
    private Map<String, Object> selectedObjectMap = new HashMap();
    private WorkflowConfig config;
    private List<UserAccount> clinicalOwnerList;
    @Getter
    @Setter
    private List<Supplier> manuList = Collections.emptyList();
    @Getter
    @Setter
    private List<Supplier> supplierList = Collections.emptyList();
    @Getter
    @Setter
    private List<Supplier> mantainList = Collections.emptyList();
    @Getter
    @Setter
    private Supplier newSupplier;
    private String createTpye = null;
    @Getter
    @Setter
    private String gbCodeName;
    @Getter
    @Setter
    private TreeNode gbTree;
    @Getter
    @Setter
    private GbCode selectedGb;
    @Getter
    @Setter
    private String qrCode;
    @Getter
    @Setter
    private Date purchaseDate;
    @Getter
    @Setter
    private List<I18nMessage> code68List;
    private List<String> readableFieldsList;
    private List<String> editableFieldsList;
    @Getter@Setter
    private CustomConfigObject customConfig;
    //设备状态
    @Getter
    List<I18nMessage> assetStatusList = Collections.emptyList();
    @Setter
    private List<ZkbFileDirectory> subZkbFileDirectoryList;
    private List<V2_BlobObject> attachements;
    @Getter
    @Setter
    private List<V2_BlobObject> pictureList;
    @Setter
    @Getter
    private String[] selectedPictures;
    @Getter
    private List<JSONObject> changeHistoryList = new ArrayList<>();
    private String uidList = null;
    @Getter
    private List<AssetInfo> assetInfoList = null;
    public Set<AssetInfo> assetInfos = null;
    private UserAccount loginUser;
    // 签名相关
    @Getter @Setter
    private String signatureContent;
    @Getter
    private StreamedContent signatureContentStream;
    @Getter
    private String signatureId;
    @Getter
    private boolean newSignature = true;
    @Setter
    @Getter
    private boolean displayErrorsOnly;

    private List<AssetInfo>  displayErrorAssets;

    private List<AssetInfo>  displayAssetsNew;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoBulkEditService = WebUtil.getBean(AssetInfoBulkEditService.class);
        assetInfoBulkEditCheckService = WebUtil.getBean(AssetInfoBulkEditCheckService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        ucService = WebUtil.getBean(UserCommonService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        clinicalDeptService = WebUtil.getBean(OrgInfoClinicalDeptService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        gbDao = WebUtil.getBean(GbCodeRepository.class);
        assetFinacingNumGenerateService = WebUtil.getBean(AssetFinacingNumGenerateService.class);
        gbCodeService = WebUtil.getBean(GbCodeService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        v2ServiceRequestRepository = WebUtil.getBean(V2ServiceRequestRepository.class);
        acService = WebUtil.getBean(AssetCreateService.class);
        zkbFileDirectoryService = WebUtil.getBean(ZkbFileDirectoryService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        medicalInstrumentsClassificationCompCtrl = WebUtil.getBean(MedicalInstrumentsClassificationCompCtrl.class);
        assetTypeGroupTreeComponentController = WebUtil.getBean(AssetTypeGroupTreeComponentController.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        woService= WebUtil.getBean(V2_WorkOrderService.class);
        nurseSiteMaintainService= WebUtil.getBean(NurseSiteMaintainService.class);
        assetScrapService= WebUtil.getBean(AssetScrapService.class);
        assetDepartmentConvertService= WebUtil.getBean(AssetDepartmentConvertService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        generateOrderNumUtil = WebUtil.getBean(GenerateOrderNumUtil.class);
        assetTagsMapper = WebUtil.getBean(AssetTagsMapper.class);
        assetTagsDao = WebUtil.getBean(AssetTagsRepository.class);
        medicalInstrumentsClassificationCompCtrl.registerController(this);
        assetTypeGroupTreeComponentController.registerController(this);

        selected = new AssetInfo();
        selected.clearDefaultValue();
        loginUser = UserContextService.getCurrentUserAccount();
        customConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        editableFieldsList = ccService.getEditAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        assetGroupSecondarySupport = i18nDao.countBySiteIdAndMsgTypeStartsWith(loginUser.getSiteId(), "assetGroupSecondary") > 0 ? true : false;
        if (assetGroupSecondarySupport) {
            onAssetGroupChange();
        }
        uidList = WebUtil.getRequestParameter("uidList");
        if(AssetInfoListController.resultAssetMap!=null){
            assetInfoList = AssetInfoListController.resultAssetMap.get(uidList);
        }
        ownerList = ucService.getWorkerList(selected.getId(), "");
        selectedObjectMap.put("selectedOrg", orgDao.findByUid(loginUser.getSiteUID()));
        changeAssetOrg((OrgInfo) selectedObjectMap.get("selectedOrg"));
        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
        initGbTree();
        qrCode = WebUtil.getRequestParameter("qrCode");
        updateAssetStatusList();
        mqUtils = WebUtil.getBean(MqUtils.class);
        getTenantOrgTreeOther();

    }
    public void saveNewSupplier() {
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
    public String getListPageLink() {
        return "AssetList.xhtml?faces-redirect=true";
    }


    public Set<AssetInfo> getAssetInfos() {
        return assetInfos;
    }
    public boolean getFlag() {
        if (assetInfos != null){
            return false;
        }
        return true;
    }
    public void todelete() {
        toDeleteNurseMaintainSite = true;
    }

    public String verifyIfRequiredBeforeApplyChange() throws Exception {
        if(selected.getClinicalDeptUID() !=null && !unCheckedNurseSite) {
            //日常检查设备进行中
            unCheckedNursiteMaintainList  = assetInfoBulkEditCheckService.verifyForNurseSiteByBatchEdit(assetInfoList);
            if (CollectionUtils.isNotEmpty(unCheckedNursiteMaintainList)) {
                unCheckedNurseSite = true;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("unCheckedNurseSite", unCheckedNurseSite);
                requestContext.addCallbackParam("toDeleteNurseMaintainSite", toDeleteNurseMaintainSite);
                return "";
            }
        }
        return applyChange();
    }

    public String applyChange() throws Exception {
        assetInfos = new HashSet<>();
        if (!isValidParentAsset()) {
            WebUtil.addErrorMessage("主设备不符合要求，请重新设置");
            return "";
        }
        if (null != selectedObjectMap.get("selectedAuthGroup")) {
            SysAuthGroup selectedAuthGroup = (SysAuthGroup) selectedObjectMap.get("selectedAuthGroup");
            selected.setMedicalApparatusClassification(selectedAuthGroup.getClassificationCode());
        }
        String isPmValue = selected.getIsPmValue();
        if(isPmValue!=null){
            if("2".equals(isPmValue)){
                selected.setIsPm(false);
            }else {
                selected.setIsPm(true);
            }
        }
        String isCheckedValue = selected.getIsCheckedValue();
        if(isCheckedValue!=null){
            if("2".equals(isCheckedValue)){
                selected.setIsChecked(false);
            }else {
                selected.setIsChecked(true);
            }
        }
        String warrantyValue = selected.getWarrantyValue();
        if(warrantyValue!=null){
            if("2".equals(warrantyValue)){
                selected.setWarranty(false);
            }else {
                selected.setWarranty(true);
            }
        }
        String clinicalDeptUID = selected.getClinicalDeptUID();
        if(clinicalDeptUID != null){
            List<String> adjustByassetInfoUid = assetInfoBulkEditCheckService.verifyByBatchEdit(assetInfoList);
            if(adjustByassetInfoUid != null && !adjustByassetInfoUid.isEmpty()){
                List<AssetInfo> assets = dao.findByUidIn(adjustByassetInfoUid);
                for ( AssetInfo a:assets) {
                    a.setErrorMessage("正在调剂中，不能修改所属科室!");
                }
                assetInfos.addAll(assets);
                assetInfoList.removeAll(assets);
            }
            List<String> maintainByassetInfoUid = assetInfoBulkEditCheckService.verifyForMaintainByBatchEdit(assetInfoList);
            if(maintainByassetInfoUid != null && !maintainByassetInfoUid.isEmpty()){
                List<AssetInfo> assets = dao.findByUidIn(maintainByassetInfoUid);
                for ( AssetInfo a:assets) {
                    a.setErrorMessage("正在保养中，不能修改所属科室!");
                }
                assetInfos.addAll(assets);
                assetInfoList.removeAll(assets);
            }
            List<Integer> assetIds = assetInfoBulkEditCheckService.verifyForInventoryByBatchEdit(assetInfoList);
            if(assetIds != null && !assetIds.isEmpty()){
                List<AssetInfo> assets = dao.findByIdIn(assetIds);
                for ( AssetInfo a:assets) {
                    a.setErrorMessage("正在盘点中，不能修改所属科室!");
                }
                assetInfos.addAll(assets);
                assetInfoList.removeAll(assets);
            }
            if(assetInfoList != null && !assetInfoList.isEmpty()){
                for(AssetInfo as:assetInfoList){
                    List<V2_ServiceRequest> serviceRequestList =v2ServiceRequestRepository.findByAssetId(as.getId());
                    if(CollectionUtils.isNotEmpty(serviceRequestList)){
                        List<Integer> repairByAssetIds = new ArrayList<>();
                        for(V2_ServiceRequest service: serviceRequestList){
                            if(service.getCloseTime()==null){
                                repairByAssetIds.add(service.getAssetId());
                            }
                        }
                        if(repairByAssetIds!= null && !repairByAssetIds.isEmpty()){
                            List<AssetInfo> assets = dao.findByIdIn(repairByAssetIds);
                            for ( AssetInfo a:assets) {
                                a.setErrorMessage("正在维修中，不能修改所属科室!");
                            }
                            assetInfos.addAll(assets);
                        }
                    }
                }
                assetInfoList.removeAll(assetInfos);
                if(assetInfoList != null && !assetInfoList.isEmpty()){
                    List<String> assetUids = assetInfoList.stream().map(assetInfo -> assetInfo.getUid()).collect(Collectors.toList());
                    List<AssetInfo> assets = dao.findBySharedAssetTypeUid(assetUids);
                    if (assets != null && !assets.isEmpty()){
                        for ( AssetInfo a:assets) {
                            a.setErrorMessage("调剂设备，不能修改所属科室!");
                        }
                        assetInfos.addAll(assets);
                        assetInfoList.removeAll(assets);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(assetInfoList)){
                List<AssetInfo> collect = assetInfoList.stream().filter(item -> dao.secondment(item.getUid()) > 0).map(item->{
                    item.setErrorMessage("设备借调中，不能修改所属科室!");
                    return item;
                }).collect(Collectors.toList());
                assetInfos.addAll(collect);
                assetInfoList.removeAll(collect);
            }
        }
        String assetDeptUID = selected.getAssetDeptUID();
        if(assetDeptUID != null){
            if(assetInfoList != null && !assetInfoList.isEmpty()){
                List<String> assetUids = assetInfoList.stream().map(assetInfo -> assetInfo.getUid()).collect(Collectors.toList());
                List<AssetInfo> assets = dao.findBySharedAssetTypeUid(assetUids);
                if (assets != null && !assets.isEmpty()){
                    for ( AssetInfo a:assets) {
                        a.setErrorMessage("调剂设备，不能修改使用科室!");
                    }
                    assetInfos.addAll(assets);
                    assetInfoList.removeAll(assets);
                }

            }
            if(CollectionUtils.isNotEmpty(assetInfoList)){
                List<AssetInfo> collect = assetInfoList.stream().filter(item -> dao.secondment(item.getUid()) > 0).map(item->{
                    item.setErrorMessage("设备借调中，不能修改使用科室!");
                    return item;
                }).collect(Collectors.toList());
                assetInfos.addAll(collect);
                assetInfoList.removeAll(collect);
            }
        }
        if(assetInfoList == null && assetInfoList.isEmpty()){
            return "";
        }
        //设置质保时间
        if (null == selected.getFactoryWarrantyDate()) {
            if (selected.getAcceptDate() != null && selected.getWarrantyMonth() != null) {
                selected.setFactoryWarrantyDate(TimeUtil.toJodaDate(selected.getAcceptDate()).plusMonths(selected.getWarrantyMonth()).toDate());
            }
        }

        // 资产分类
        if (null != selectedObjectMap.get("selectedAssetTypeGroup")) {
            AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup) selectedObjectMap.get("selectedAssetTypeGroup");
            selected.setAssetType(selectedAssetTypeGroup.getEncoded());
        }

        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return "";
            }

            selected.setNationalStandardCode(selectedGb.getCode());
        }
        if (UserContextService.isMVSRole() && this.selected.getMvsEquip() == null) {
            this.selected.setMvsEquip(1);
        }
        List<Integer> idList =new ArrayList<>();
        List<AssetInfo> newList = depCopy(assetInfoList);
        for (AssetInfo assetInfo : newList ) {
            idList.add(assetInfo.getId());
        }
        for (AssetInfo assetInfo: newList) {
            if(selected.getClinicalDeptId() != null && !selected.getClinicalDeptId().equals(assetInfo.getClinicalDeptId()) && selected.getParentAssetId() == null){
                List<AssetInfo> assetInfoByParentAssetId = dao.findByParentAssetId(assetInfo.getId());
                if(!assetInfoByParentAssetId.isEmpty()){
                    for(AssetInfo date:assetInfoByParentAssetId){
                        if(!idList.contains(date.getId())){
                            date.setParentAssetId(null);
                            dao.save(date);
                        }
                    }
                }else {
                    Integer parentAssetId = assetInfo.getParentAssetId();
                    if(!idList.contains(parentAssetId)){
                        assetInfo.setParentAssetId(null);
                        dao.save(assetInfo);
                    }
                }
            }
            if(selected.getParentAssetId() != null){
                List<AssetInfo> assetInfoByParentAssetId = dao.findByParentAssetId(assetInfo.getId());
                if(!assetInfoByParentAssetId.isEmpty()){
                    for(AssetInfo date:assetInfoByParentAssetId){
                        if(!idList.contains(date.getId())){
                            date.setParentAssetId(null);
                            dao.save(date);
                        }
                    }
                }
            }
        }
        Integer assetDeptId = selected.getAssetDeptId();
        for (AssetInfo assetInfo : newList ) {
            idList.add(assetInfo.getId());
            selected.setIsValid(assetInfo.getIsValid());
            selected.setIbDeviceStatus(assetInfo.getIbDeviceStatus());
            selected.setConnectToInsite(assetInfo.getConnectToInsite());
            if(assetDeptId == null || assetDeptId == 0){
                selected.setAssetDeptId(assetInfo.getAssetDeptId());
            }
            selected.setSiteId(assetInfo.getSiteId());
            selected.setSiteUID(assetInfo.getSiteUID());
            selected.setHospitalId(assetInfo.getHospitalId());
            selected.setTenantUID(assetInfo.getTenantUID());
            selected.setHospitalUID(assetInfo.getHospitalUID());
            selected.setInstitutionUID(assetInfo.getInstitutionUID());
            copyPropertiesExclude(selected, assetInfo);
        }

        Date now = DateUtil.now();
        for (AssetInfo assetInfo: newList) {
            assetInfo.setLastModifiedDate(now);
            dao.save(assetInfo);
            saveOrUpdateAssetTags(assetInfo);
            onAfterNewObject(assetInfo);
        }
        this.saveAssetHistory(assetInfoList,newList);
        AssetInfoListController.resultAssetMap.clear();
        if(assetInfos != null && !assetInfos.isEmpty()){
            return "";
        }
        if(CollectionUtils.isNotEmpty(unCheckedNursiteMaintainList) && toDeleteNurseMaintainSite) {
            for(AssetInfo assetInfo : unCheckedNursiteMaintainList) {
                assetInfoBulkEditCheckService.deleteNursiteMaintain(assetInfo);
            }
        }
        return "AssetList.xhtml?faces-redirect=true";
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

    private void saveOrUpdateAssetTags(AssetInfo assetInfo) {

        List<String> existingTags = assetTagsMapper.getTagsByAsset(assetInfo.getUid());
        List<String> currentTags = assetInfo.getTags();

        List<String> newTags = new ArrayList<>(currentTags);
        newTags.removeAll(existingTags);

        List<String> removeTags = new ArrayList<>(existingTags);
        removeTags.removeAll(currentTags);

        //add new tags
        newTags.stream().distinct().forEach(item -> {
            AssetTags assetTags = new AssetTags();
            assetTags.setAssetUid(assetInfo.getUid());
            assetTags.setTenantUID(loginUser.getTenantUID());
            assetTags.setTag(item);
            assetTagsDao.save(assetTags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> {
            assetTagsMapper.removeTag(assetInfo.getUid(), item);
        });

    }

    public void onAfterNewObject(AssetInfo object) {
        if(attachements !=null){
            attachements.forEach(item -> {
                if (item.getBoId() == null) {
                    item.setBoId(object.getId());
                    item.setBoUuid(object.getUid());
                    blobService.saveBlobObject(item);
                }
            });
        }

    }

    public static <T> List<T> depCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>) inStream.readObject();
            return destList;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制对象属性：只复制需要的属性值，目标对象中原来的值不变
     * 只适用于2个相同对象
     * @param from 要复制的对象
     * @param to 目标对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void copyPropertiesExclude(Object from, Object to) throws Exception {
        if(!from.getClass().equals(to.getClass())){
            throw new RuntimeException("对象不是同一类型，fromClass："+from.getClass()+",toClass："+to.getClass());
        }
        Method[] fromMethods = from.getClass().getDeclaredMethods();
        Method[] toMethods = to.getClass().getDeclaredMethods();
        Method fromMethod = null, toMethod = null;
        String fromMethodName = null, toMethodName = null;
        for (int i = 0; i < fromMethods.length; i++) {
            fromMethod = fromMethods[i];
            fromMethodName = fromMethod.getName();
            if (!fromMethodName.contains("get")) {
                continue;
            }
            toMethodName = "set" + fromMethodName.substring(3);
            toMethod = findMethodByName(toMethods, toMethodName);
            //如果方法为null，不处理
            if (toMethod == null) {
                continue;
            }
            Object value = fromMethod.invoke(from, new Object[0]);
            //如果值为空，不处理
            if(value == null) {
                continue;
            }
            //集合类判空处理
            if(value instanceof Collection) {
                Collection newValue = (Collection)value;
                if(newValue.size() <= 0)
                    continue;
            }
            toMethod.invoke(to, new Object[] {value});
        }
    }
    /**
     * 从方法数组中获取指定名称的方法
     *
     * @param methods
     * @param name
     * @return
     */
    public static Method findMethodByName(Method[] methods, String name) {
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(name))
                return methods[j];
        }
        return null;
    }

    private void saveAssetHistory(List<AssetInfo> oldAssetInfoList,List<AssetInfo> assetInfoList) {
        for (AssetInfo oldAssetInfo:oldAssetInfoList ) {
            for (int i = 0; i < assetInfoList.size(); i++) {
                int oldId = oldAssetInfo.getId();
                int newId =assetInfoList.get(i).getId();
                if( oldId == newId ){
//                    assetHistoryService.saveAssetHistoryIfUpdated(oldAssetInfo, assetInfoList.get(i));
                    this.sendAssetMq(assetInfoList.get(i));
                }
            }
        }

    }


    private void sendAssetMq(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setDescribe("批量修改设备");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("AssetInfoBulkEditController");
        mqContext.setMethodName("verifyIfRequiredBeforeApplyChange");
        UserAccount currentUserAccount = loginUser;
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(newAsset,currentUserAccount,mqContext);
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
    public boolean isValidParentAsset() {

        if (selected.getParentAssetId() == null) {
            return true;
        }

        AssetInfo assetInfo = dao.findById(selected.getParentAssetId());

        if (null == assetInfo) {
            return false;
        }

//        if (StringUtils.isEmpty(selected.getClinicalDeptUID())) {
//            return false;
//        }
//
//        //parent asset and child asset will be in the same dept
//        if (!selected.getClinicalDeptUID().equals(assetInfo.getClinicalDeptUID())) {
//            return false;
//        }

        return true;
    }

    public boolean needEdit(String fieldKey) {
        return editableFieldsList.contains(fieldKey);
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
    public boolean getIsSelect46Account() {
        if (loginUser.getSiteId()==46&&loginUser.getTenantUID().equals(loginUser.getSiteUID())) {
            return true;
        }
        return false;
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
    //判断待报废按钮是否禁用
    public boolean hasScrappedAuthority() {
        return acService.hasScrappedAuthority();
    }
    public String selectCreatedBy(String loginName){
        UserAccount user = userDao.getByLoginName(loginName);
        if(user != null){
            return user.getName();
        }
        return null;
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
    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(assetuid);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
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
    public int getAssetChangeHistoryCount() {
        return changeHistoryList.size();
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
                code68List = i18nDao.getByMsgTypeOrderByMsgKey(functionGroup.getMsgKey());
            }
        }
    }
    public void handleDateSelect(SelectEvent event) {
        Object object = event.getObject();
        purchaseDate= (Date) object;
    }
    public void renewQrcode() {
        QrCodeLib qrcodeLib = qrCodeService.createOneQrCode(selected.getTenantUID(), selected.getSiteUID(), 1);
        qrCode = qrcodeLib.getQrCode();
        onDepartNumChange("qrCode");
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

    //三码合一设置
    public String getAssetCodeConfig() {
        return config == null ? null : config.getAssetCodeConfig();
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

    public void  onParentAssetChange() {
        AssetInfo selectedAsset = (AssetInfo) selectedObjectMap.get("parentAsset");
        selected.setParentAssetId(null == selectedAsset ? null : selectedAsset.getId());
    }
    public List<AssetInfo> completeParentAssetIdList(String query) {
        Integer clinicalDeptId = assetInfoList.get(0).getClinicalDeptId();
        String clinicalDeptName = assetInfoList.get(0).getClinicalDeptName();
        String clinicalDeptUID = assetInfoList.get(0).getClinicalDeptUID();
        Integer clinicalDeptIdNew = selected.getClinicalDeptId();
        String clinicalDeptNameNew = selected.getClinicalDeptName();
        String clinicalDeptUIDNew = selected.getClinicalDeptUID();
        selected.setClinicalDeptId(clinicalDeptId);
        selected.setClinicalDeptName(clinicalDeptName);
        selected.setClinicalDeptUID(clinicalDeptUID);
        boolean b = true;
        List<Integer> assetInfoIdList = new ArrayList<>();
        for (AssetInfo assetInfo:assetInfoList) {
            assetInfoIdList.add(assetInfo.getId());
            if(clinicalDeptIdNew!=null){
                b = true;
                selected.setClinicalDeptId(clinicalDeptIdNew);
                selected.setClinicalDeptName(clinicalDeptNameNew);
                selected.setClinicalDeptUID(clinicalDeptUIDNew);
                break;
            }else {
                if(!clinicalDeptId.equals(assetInfo.getClinicalDeptId())){
                    b = false;
                    break;
                }
            }

        }
        if (b){
            parentAssetList = new ArrayList<>();
            List<AssetInfo> parentsAssetInfoList = aimService.getParentsAssetsList(this.selected);
            for(AssetInfo assetInfo :parentsAssetInfoList){
                if(!assetInfoIdList.contains(assetInfo.getId())){
                    parentAssetList.add(assetInfo);
                }
            }
        }else {
            parentAssetList = null;
        }
        selected.setClinicalDeptId(clinicalDeptIdNew);
        selected.setClinicalDeptName(clinicalDeptNameNew);
        selected.setClinicalDeptUID(clinicalDeptUIDNew);
        return autoCompleteService.completeAssetInfoList(query, parentAssetList);
    }

    public List<AssetInfo> completeParentAssetId(String query) {
        parentAssetList = aimService.getParentsAssetsList(this.selected);
        return autoCompleteService.completeAssetInfoList(query, parentAssetList);
    }
    private void updateSelectedOrgGb(GbCode gbCode) {
        if (gbCode == null || gbCode.getId() == -1) {
            selectedGb = null;
        } else {
            selectedGb = gbCode;
            gbCodeName = gbCode.getCode() + "/" + gbCode.getName();
        }
    }

    public void onSelectGbTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        GbCode gbCode = (GbCode) node.getData();
        updateSelectedOrgGb(gbCode);
    }

    public void initGbTree() {
        TreeNode treeNode = gbCodeService.buildGbTree();
        gbTree = new DefaultTreeNode("root", null, null);
        gbTree.getChildren().add(treeNode);
        treeNode.setExpanded(true);
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

    public List<Supplier> autoCompleteMaintainer(String query) {
        mantainList = aimService.getOnlyMaintainListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteMaintainer(query, mantainList);
    }
    public List<Supplier> autoCompleteSupplier(String query) {
        //重新获取一下供应商
        supplierList = aimService.getOnlySuplierListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public void prepareCreateSupplier(String type) {
        newSupplier = new Supplier();
        if ("manufacturer".equals(type)) {
            createTpye = type;
            newSupplier.setManufacturerT(true);
        }
        if ("supplier".equals(type)) {
            createTpye = type;
            newSupplier.setSupplierT(true);
        }
        if ("maintainer".equals(type)) {
            createTpye = type;
            newSupplier.setMaintainerT(true);
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
                selected.setManufactureTel(Optional.ofNullable(supplier).map(Supplier::getTel).orElse(null));
                break;
        }
    }
    public List<Supplier> autoCompleteManufacture(String query) {
        manuList = aimService.getOnlyManuListBySiteUid(selected.getTenantUID());
        return autoCompleteService.autoCompleteManufacture(query, manuList);
    }
    public List<UserAccount> completeClinicalOwnerList(String query) {
        if (clinicalOwnerList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();

        selectedObjectMap.put("selectedOrg", orgInfo);

        changeAssetOrg(orgInfo);
    }
    private void updateClinicalDeptOnSiteChange() {
        if(assetInfoList != null && !assetInfoList.isEmpty()){
            String siteUID = assetInfoList.get(0).getSiteUID();
            boolean isSiteUid = true;
            for(AssetInfo assetInfo :assetInfoList){
                if(!siteUID.equals(assetInfo.getSiteUID())){
                    clinicalDeptList = new ArrayList<>();
                    isSiteUid = false;
                }
            }
            if(isSiteUid){
                clinicalDeptList = orgDao.getDeptListBySiteUid(siteUID);
            }
        }else {
            clinicalDeptList = clinicalDeptService.getUserAccessibleClinicalDeptsBySelectedSite(selected.getSiteUID());
        }

    }
    public void getClinicalOwnerList() {
        if (null != selected.getClinicalDeptUID()) {
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(selected.getClinicalDeptUID());
        }else if(CollectionUtils.isNotEmpty(assetInfoList)){
            List<String> list = new ArrayList<>();
            for ( AssetInfo assetInfo:assetInfoList) {
                String clinicalDeptUID = assetInfo.getClinicalDeptUID();
                if(list.size()>0 && !list.contains(clinicalDeptUID)){
                    clinicalOwnerList = Collections.emptyList();
                    return;
                }
                list.add(clinicalDeptUID);
            }
            clinicalOwnerList = userDao.findUsersByOrgIdExceptTemporaryAccount(list.get(0));
        }else {
            clinicalOwnerList = Collections.emptyList();
        }
    }
    public void onAssetDeptChangeEdit(String idx) {
        OrgInfo clinicalDept = null;
        switch (idx) {
            case "clinicalDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("clinicalDept");
                selected.setClinicalDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setClinicalDeptName(clinicalDept == null ? null : clinicalDept.getName());
                selected.setClinicalDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                selectedObjectMap.put("clinicalOwner", null);
                onOwnerChange("clinicalOwner");
                getClinicalOwnerList();
                selected.setParentAssetId(null);
                selectedObjectMap.put("parentAsset",null);
                break;
            case "assetDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("assetDept");
                selected.setAssetDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setAssetDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                break;
        }

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
                getClinicalOwnerList();
                break;
            case "assetDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("assetDept");
                selected.setAssetDeptId(clinicalDept == null ? null : clinicalDept.getId());
                selected.setAssetDeptUID(clinicalDept == null ? null : clinicalDept.getUid());
                break;
        }

    }
    private void changeAssetOrg(OrgInfo orgInfo) {
        if(assetInfoList != null && !assetInfoList.isEmpty()){
            String siteUID = assetInfoList.get(0).getSiteUID();
            boolean isSiteUid = true;
            for(AssetInfo assetInfo :assetInfoList){
                if(!siteUID.equals(assetInfo.getSiteUID())){
                    isSiteUid = false;
                }
            }
            if(isSiteUid){
                selected.setSiteId(assetInfoList.get(0).getSiteId());
                selected.setSiteUID(siteUID);
                config = configService.findConfigBySite(siteUID);
            }
        }else{
            selected.setSiteId(orgInfo.getSiteId());
            selected.setSiteUID(orgInfo.getSiteUID());
            config = configService.findConfigBySite(selected.getSiteUID());
        }
        selected.setHospitalId(orgInfo.getId());

        selected.setTenantUID(orgInfo.getTenantUID());
        selected.setHospitalUID(orgInfo.getHospitalUID());
        selected.setInstitutionUID(orgInfo.getInstitutionUID());

        updateClinicalDeptOnSiteChange();

        selectedObjectMap.put("clinicalDept", null);
        onAssetDeptChange("clinicalDept");
        selectedObjectMap.put("assetDept", null);
        onAssetDeptChange("assetDept");
        if (parentAssetList != null) {
            parentAssetList.clear();
        }
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
    public List<UserAccount> completeOwnerList(String query) {
        if (ownerList == null) {
            return Collections.emptyList();
        }
        List<UserAccount> res = this.ownerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        return res;
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
    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return null;
    }

    public void initOrgTree() {
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

    private TreeNode tenantOrgTreeOtherPage;

    public TreeNode getTenantOrgTreeOther() {
        if (null == tenantOrgTreeOtherPage) {
            tenantOrgTreeOtherPage = createTenantOrgTreeOther();
        }
        return tenantOrgTreeOtherPage;
    }

    public TreeNode createTenantOrgTreeOther() {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);

        List<OrgInfo> orgInfoList;
        orgInfoList = orgDao.getOrgInfosByParentId(UserContextService.getCurrentUserAccount().getSiteUID());
        filter.setSiteOrg(orgInfoList.get(0));
        clinicalDeptListAsFilter = uaaService.getOrgDeptList(filter.getSiteOrg().getUid(), loginUser.getUid());
        TreeNode siteTree = buildTree(orgInfoList);
        expandedByOrgType(siteTree, 2);
        rootNode.getChildren().addAll(siteTree.getChildren());
        return rootNode;
    }

    private void expandedByOrgType(TreeNode tree, Integer type) {
        if (tree.getData() != null) {
            OrgInfo node = (OrgInfo) tree.getData();
            if (node.getOrgType() > type) {
                return;
            }
        }
        tree.setExpanded(true);
        for (TreeNode child : tree.getChildren()) {
            expandedByOrgType(child, type);
        }

    }

    private TreeNode buildTree(List<OrgInfo> orgList) {
        TreeNode rootNode = new DefaultTreeNode("org", null, null);
        Map<String, TreeNode> treeMap = orgList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> new DefaultTreeNode("org", item, null)));
        for (OrgInfo org : orgList) {
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

    public void onSelectTreeNodeAsFilter(NodeSelectEvent event) {
        OrgInfo filterSiteOrg = null;
        if (null == event || null == event.getTreeNode() || null == event.getTreeNode().getData()) {
            filterSiteOrg = null;
        } else {
            filterSiteOrg = (OrgInfo) event.getTreeNode().getData();
        }
        filter.setSiteOrg(filterSiteOrg);
        filter.setClinicalDeptOrg(null);

        if (null == filterSiteOrg) {
            clinicalDeptListAsFilter = Collections.emptyList();
        } else {
            clinicalDeptListAsFilter = uaaService.getOrgDeptList(filter.getSiteOrg().getUid(), loginUser.getUid());
        }
        logger.error("clinicalDeptListAsFilter size = {}", clinicalDeptListAsFilter.size());
    }

    public List<OrgInfo> autoCompleteDeptAsFilter(String query) {
        if (clinicalDeptListAsFilter.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptListAsFilter.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onClinicalDeptChangeAsFilter() {

    }

    public DualListModel<AssetInfo> getAssets() {
        if (assets.getTarget().size() >= 1000) {
            return assets;
        }

        prepareFilters();

        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInfo> source = new ArrayList<>(page.getContent());
        source.removeAll(assets.getTarget());
        assets.setSource(source);

        return assets;
    }

    public DualListModel<AssetInfo> getAssets1() {

//        prepareFilters();

        Map<String, Object> param = MapUtil.newHashMap();
        param.put("tenantUid", loginUser.getTenantUID());
        param.put("userUid", loginUser.getUid());
        if (!assets.getTarget().isEmpty()) {
            List<String> assetUids = assets.getTarget().stream().map(AssetInfo::getUid).collect(Collectors.toList());
            param.put("assetUids", assetUids);
        }
        if (null != filter.getSiteOrg() && null != filter.getSiteOrg().getUid()) {
            param.put("siteUid", filter.getSiteOrg().getUid());
        }
        if (null != filter.getClinicalDeptOrg() && null != filter.getClinicalDeptOrg().getUid()) {
            param.put("clinicalDeptUid", filter.getClinicalDeptOrg().getUid());
        }
        if (null != this.filter.getAssetGroup()) {
            param.put("assetGroup", filter.getAssetGroup());
        }
        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            param.put("oneSearchStr", oneSearchStr);
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
//        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInfo> source = new ArrayList<>(assetInfoBulkEditService.findBathUpdateAssetInfos(param));
        source.removeAll(assets.getTarget());
        assets.setSource(source);

        return assets;
    }

    private void prepareFilters() {
        searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, loginUser.getTenantUID()));

        /*if (assets.getTarget().size() > 0) {
            List<String> assetUids = assets.getTarget().stream().map(AssetInfo::getUid).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("uid", SearchFilter.Operator.NotIN, assetUids));
        }*/

        List<String> orgUidList;

        if (null != filter.getSiteOrg() && null != filter.getSiteOrg().getUid()) {
            orgUidList = orgInfoDao.getOrgInfoUidsByparentUid(filter.getSiteOrg().getUid());
        }else {
            UserAccount user = loginUser;
            orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        }

        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        if (null != filter.getClinicalDeptOrg()) {
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filter.getClinicalDeptOrg().getUid()));
        } else {
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptListAsFilter));
        }

        if (null != this.filter.getAssetGroup()) {
            searchFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.filter.getAssetGroup()));
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public void onTransfer(TransferEvent event) {
        unCheckedNurseSite = false;
        toDeleteNurseMaintainSite = false;
    }

    public void onTabChange(TabChangeEvent event) {
        if (TAB_SELECT_ASSETS.equals(event.getTab().getId())) {
            return;
        }

        setDefaultValueForSelectedAssets();
    }

    private void setDefaultValueForSelectedAssets() {
        List<AssetInfo> assetInfos = assets.getTarget();

        if (assetInfos.size() == 0) {
            return;
        }

        AssetInfo firstAsset = assetInfos.get(0);

        Object obj;
        obj = getSameValue(assets.getTarget(), "siteUID");
//        if (null != obj) {
//            OrgInfo foundOrgInfo = orgInfoDao.findByUid((String)obj);
//            selected.setSiteOrg(foundOrgInfo);
//        }

        obj = getSameValue(assets.getTarget(), "clinicalDeptUID");
        if (null != obj) {
            OrgInfo foundOrgInfo = orgInfoDao.findByUid((String)obj);
            selected.setClinicalDeptOrg(foundOrgInfo);
        }

        obj = getSameValue(assets.getTarget(), "assetGroup");
        if (null != obj) {
            selected.setAssetGroup((Integer)obj);
        }


    }

    private Object getSameValue(List<AssetInfo> assetInfos, String columnName) {
        try {
            Field field = AssetInfo.class.getDeclaredField(columnName);
            field.setAccessible(true);

            if (assetInfos.size() == 0) {
                return null;
            }

            if (assetInfos.size() == 1) {
                Object obj = field.get(assetInfos.get(0));
                return obj;
            }

            Object prevObj = field.get(assetInfos.get(0));

            for (int i = 1; i < assetInfos.size(); i++) {
                AssetInfo assetInfo = assetInfos.get(i);
                Object obj = field.get(assetInfo);

                if (!Objects.equals(obj, prevObj)) {
                    return null;
                }

                prevObj = obj;
            }

            return prevObj;

        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public void onSelectTreeNodeAsNewValue(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo siteOrg = (OrgInfo) node.getData();
        selected.setSiteOrg(siteOrg);

        selected.setClinicalDeptOrg(null);

        if (null == siteOrg) {
            clinicalDeptList = Collections.emptyList();
        } else {
            clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(selected.getSiteOrg().getSiteUID());
        }

    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public boolean displayControl(String name) {
        for (String key: selectedColumns) {
            if (key.equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Getter @Setter
    private String applyReason;

    public List<String> getSelectedUidList() {
        return assets.getTarget().stream().map(AssetInfo::getUid).collect(Collectors.toList());
    }

    public void  getSig() {
        if (StringUtils.isNotEmpty(selected.getSiteUID())) {
            WorkflowConfig workflowConfig = workflowService.getWorkflowByOrg(selected.getSiteUID());
            if (workflowConfig != null && Boolean.TRUE.equals(workflowConfig.getPartsNeedSign())) {
                // 如果勾选了‘备件审核需要签名’，校验是否已经签名 弹出签名窗口
                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
                response.setStatus(205);
            }
        }
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
        }else{
            this.newSignature = true;
        }
    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
    }

    public void checkedIfNessesaryBeforeCommitBulkEditSite() {
        try {
            unCheckedNursiteMaintainList  = assetInfoBulkEditCheckService.verifyForNurseSiteByBatchEdit(assets.getTarget());
            if(!unCheckedNurseSite && !toDeleteNurseMaintainSite && CollectionUtils.isNotEmpty(unCheckedNursiteMaintainList)) {
                unCheckedNurseSite = true;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("unCheckedNurseSite", unCheckedNurseSite);
                requestContext.addCallbackParam("toDeleteNurseMaintainSite", toDeleteNurseMaintainSite);
            } else {
                commitBulkEditSite();
            }
        } catch (Exception e) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("errFlag", true);
            logger.error("bath asset department convert checked err {} ", e.getMessage(), e);
        }
    }

    public void commitBulkEditSite() {

        boolean errFlag = false;
        displayAssetsNew = new ArrayList<>();
        displayErrorAssets = new ArrayList<>();
        if (!checkIsValidInput()) {
            errFlag = true;
        }

        //commit changes after validated
        List<AssetInfo> selectedAssets = assets.getTarget();
        displayAssetsNew = selectedAssets;
        List<AssetInfo> notPass =new ArrayList<>();
        //验证
        if(!assetInfoBulkEditCheckService.verify(selectedAssets,notPass)){
            List<String> uids= notPass.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            WebUtil.addErrorMessage("设备正在调剂中，不能更换院区!");
            displayAssetsNew.forEach(item->{
                if(uids.contains(item.getUid())){
                    String errorMessage = item.getErrorMessage();
                    errorMessage = StringUtils.isBlank(errorMessage)?"设备正在调剂中，不能更换院区!\n":errorMessage+"设备正在调剂中，不能更换院区!\n";
                    item.setErrorMessage(errorMessage);
                    displayErrorAssets.add(item);
                }
            });
            errFlag = true;
            notPass.clear();
        }

        if(!assetInfoBulkEditCheckService.verifyForMaintain(selectedAssets,notPass)){
            List<String> uids= notPass.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            WebUtil.addErrorMessage("设备正在保养中，不能更换院区！");
            errFlag = true;
            displayAssetsNew.forEach(item->{
                if(uids.contains(item.getUid())){
                    String errorMessage = item.getErrorMessage();
                    errorMessage = StringUtils.isBlank(errorMessage)?"设备正在保养中，不能更换院区!\n":errorMessage+"设备正在保养中，不能更换院区!\n";
                    item.setErrorMessage(errorMessage);
                    displayErrorAssets.add(item);
                }
            });
            notPass.clear();
        }

        if(!assetInfoBulkEditCheckService.verifyForInventory(selectedAssets,notPass)){
            List<String> uids= notPass.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            WebUtil.addErrorMessage("设备正在盘点中，不能更换院区!");
            errFlag = true;
            displayAssetsNew.forEach(item->{
                if(uids.contains(item.getUid())){
                    String errorMessage = item.getErrorMessage();
                    errorMessage = StringUtils.isBlank(errorMessage)?"设备正在盘点中，不能更换院区!\n":errorMessage+"设备正在盘点中，不能更换院区!\n";
                    item.setErrorMessage(errorMessage);
                    displayErrorAssets.add(item);
                }
            });
            notPass.clear();
        }

        for(AssetInfo as:selectedAssets){
            boolean assetFlag =true;
            List<V2_ServiceRequest> serviceRequestList =v2ServiceRequestRepository.findByAssetId(as.getId());
            if(CollectionUtils.isNotEmpty(serviceRequestList)){
                for(V2_ServiceRequest service: serviceRequestList){
                    if(service.getCloseTime()==null){
                        assetFlag=false;
                    }
                }
                if(assetFlag){
                    continue;
                }else{
                    notPass.add(as);
                    errFlag = true;
                }
            }
        }
        if(CollectionUtils.isNotEmpty(notPass)){
            List<String> uids= notPass.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            WebUtil.addErrorMessage("设备正在维修中，不能更换院区!");
            displayAssetsNew.forEach(item->{
                if(uids.contains(item.getUid())){
                    String errorMessage = item.getErrorMessage();
                    errorMessage = StringUtils.isBlank(errorMessage)?"设备正在维修，不能更换院区!\n":errorMessage+"设备正在维修，不能更换院区!\n";
                    item.setErrorMessage(errorMessage);
                    displayErrorAssets.add(item);
                }
            });
            notPass.clear();
        }

        if(!assetInfoBulkEditCheckService.verifySiteToSite(selectedAssets,notPass)){
            List<String> uids= notPass.stream().map(AssetInfo::getUid).collect(Collectors.toList());
            WebUtil.addErrorMessage("设备正在科室借调中，不能更换院区!");
            errFlag = true;
            displayAssetsNew.forEach(item->{
                if(uids.contains(item.getUid())){
                    String errorMessage = item.getErrorMessage();
                    errorMessage = StringUtils.isBlank(errorMessage)?"设备正在科室借调中，不能更换院区!\n":errorMessage+"设备正在科室借调中，不能更换院区!\n";
                    item.setErrorMessage(errorMessage);
                    displayErrorAssets.add(item);
                }
            });
            notPass.clear();
        }
        //临床助手检查进行中
        if(CollectionUtils.isNotEmpty(unCheckedNursiteMaintainList) && toDeleteNurseMaintainSite) {
            assetScrapService.processUnfinishedNurseSiteMaintain(UserContextService.getAccessToken(), unCheckedNursiteMaintainList.stream().map(AssetInfo::getUid).collect(Collectors.toList()), "transfer");
//            nurseSiteMaintainService.processUnfinishedNurseSiteMaintain(unCheckedNursiteMaintainList, "已转科");
            toDeleteNurseMaintainSite = false;
            unCheckedNurseSite = false;
        }

        if (errFlag) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("errFlag", errFlag);
            return;
        }

        try {
            String convertNo = generateOrderNumUtil.autoGenerateOutBoundOrderNum(selected.getClinicalDeptOrg().getUid());
            Map<String, Object> param = MapUtil.newHashMap();
            param.put("assetUids", selectedAssets.stream().map(AssetInfo::getUid).collect(Collectors.toList()));
            param.put("inDeptUid", selected.getClinicalDeptOrg().getUid());
            param.put("inDeptName", selected.getClinicalDeptOrg().getName());
            param.put("auditSignature", signatureId);
            param.put("applyReason", applyReason);
            param.put("convertNo", convertNo);
            param.put("flag", false);
            assetDepartmentConvertService.apply(param);
            assetInfoBulkEditService.commitBulkEditSite(selectedAssets, selected, selectedColumns);
            assets.setSource(new ArrayList<>());
            assets.setTarget(new ArrayList<>());
            selected = new AssetInfo();
        } catch (Exception e) {
            WebUtil.addErrorMessage(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    public void applyAssetApplication() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if(StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }
        if(this.newSignature && StringUtils.isNotEmpty(signatureContent)){
            try {
                String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decode = decoder.decodeBuffer(base64);

                V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                if (null != blobObject) {
                    this.signatureId = blobObject.getObjectStorageId();
                    woService.addUserSignature(this.signatureId);
                }else{
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
        try {
            List<AssetInfo> selectedAssets = assets.getTarget();
            Map<String, Object> param = MapUtil.newHashMap();
            param.put("assetUids", selectedAssets.stream().map(AssetInfo::getUid).collect(Collectors.toList()));
            param.put("inDeptUid", selected.getClinicalDeptOrg().getUid());
            param.put("inDeptName", selected.getClinicalDeptOrg().getName());
            param.put("auditSignature", signatureId);
            param.put("applyReason", applyReason);

            assets.setSource(new ArrayList<>());
            assets.setTarget(new ArrayList<>());
            selected = new AssetInfo();
            assetDepartmentConvertService.apply(param);
        } catch(Exception e) {
            WebUtil.addErrorMessage(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    public void commitBulkEdit() {

//        if (!checkIsValidInput()) {
//            return;
//        }

        //commit changes after validated
        List<AssetInfo> selectedAssets = assets.getTarget();
        if (null != gbCodeName) {
            GbCode gbCode = gbDao.getGbCode(gbCodeName.split("/")[0]);
            if (null == gbCode) {
                WebUtil.addErrorMessage(WebUtil.getMessage("无效的国标码"));
                return ;
            }
            selected.setNationalStandardCode(selectedGb.getCode());
        }
        selected.setQrCode(qrCode);
        assetInfoBulkEditService.commitBulkEdit(selectedAssets, selected, selectedColumns);

        assets.setSource(new ArrayList<>());
        assets.setTarget(new ArrayList<>());
        selected = new AssetInfo();
        WebUtil.addSuccessMessage("批量修改资产属性成功!");

    }

    private boolean checkIsValidInput() {
        if (assets.getTarget().size() == 0) {
            WebUtil.addErrorMessage("请选择相应资产!");
            return false;
        }

        if (null == this.selected.getSiteOrg()) {
            WebUtil.addErrorMessage("请选择院区!");
            return false;
        }

        if (null == this.selected.getClinicalDeptOrg()) {
            WebUtil.addErrorMessage("请选择科室!");
            return false;
        }

        return true;
    }

    public boolean disableBulkEditSiteButton() {
        if (assets.getTarget().size() == 0) {
            return true;
        }

        return false;
    }

    public boolean disableBulkEditButton() {
        if (assets.getTarget().size() == 0) {
            return true;
        }

        if (selectedColumns.size() == 0) {
            return true;
        }

        return false;
    }

    public String getTabIDAsSelectAssets() {
        return TAB_SELECT_ASSETS;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        switch (eventName) {
            case MedicalInstrumentsClassificationCompCtrl.MEDICALINSTRUMENTS_CLASSIFICATION_CHANGE:
                SysAuthGroup selectedAuthGroup = (SysAuthGroup) eventObject;
                selectedObjectMap.put("selectedAuthGroup", selectedAuthGroup);
                break;
            case AssetTypeGroupTreeComponentController.ASSET_TYPE_GROUP_CHANGE:
                AssetTypeGroup selectedAssetTypeGroup = (AssetTypeGroup) eventObject;
                selectedObjectMap.put("selectedAssetTypeGroup", selectedAssetTypeGroup);
                if (userContextService.enableAssetGroupAssetTypeGroupAssociation()) {
                    selected.setAssetGroup(selectedAssetTypeGroup.getAssetGroup());
                }
                break;
            default:
                break;
        }
    }


    public List<AssetInfo> getDisplayAssetsNew(){
        logger.info("asset import result display error:{}", isDisplayErrorsOnly());
        if (isDisplayErrorsOnly()) {
            return displayErrorAssets;
        }
        return displayAssetsNew;
    }
}
