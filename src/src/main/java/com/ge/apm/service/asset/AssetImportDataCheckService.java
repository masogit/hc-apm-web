package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetImport;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.constant.UsageStatusConstant;
import com.ge.apm.service.orginfo.OrgInfoLocalCacheService;
import com.ge.apm.service.orginfo.SiteOrgWithDeptOrg;
import com.ge.apm.service.utils.MvsUtil;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class AssetImportDataCheckService {
    private static final Logger logger = LoggerFactory.getLogger(AssetImportDataCheckService.class);

    public static final String EXCEL_ASSET_TEMPLATE_VERSION = "v2.4";

    public static final String SHEET_ASSET = "Asset List";
    public static final String SHEET_ORG = "Org List";
    public static final String SHEET_TCM = "TCM List";
    public static final int EXCEL_ASSET_HEADER_ROW = 1;
    public static final int EXCEL_ASSET_DATA_ROW = 2;

    public static final int EXCEL_ORG_HEADER_ROW = 1;
    public static final int EXCEL_ORG_DATA_ROW = 2;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    protected AssetCreateService assetCreateService = WebUtil.getBean(AssetCreateService.class);

    protected   ConfigCacheService configCacheService = WebUtil.getBean(ConfigCacheService.class);

    protected Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    protected String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
    public Map<String, String> assetFunctionTypes = i18nMap(FieldValueMessageController.getFieldValueList("assetFunctionType", siteId));
    public Map<String, String> assetGroup = i18nMap(FieldValueMessageController.getFieldValueList("assetGroup", siteId));
    public Map<String, String> assetClassList = i18nMap(FieldValueMessageController.getFieldValueList("assetClass", siteId));
    public Map<String, String> spare01List = i18nMap(FieldValueMessageController.getFieldValueList("spare01", siteId));
    public Map<String, String> spare02List = i18nMap(FieldValueMessageController.getFieldValueList("spare02", siteId));
    public Map<String, String> spare03List = i18nMap(FieldValueMessageController.getFieldValueList("spare03", siteId));
    public Map<String, String> spare04List = i18nMap(FieldValueMessageController.getFieldValueList("spare04", siteId));
    public Map<String, String> spare05List = i18nMap(FieldValueMessageController.getFieldValueList("spare05", siteId));
    public Map<String, String> depreciationMethod = i18nMap(FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId));
    public Map<String, String> assetUsageList = i18nMap(FieldValueMessageController.getFieldValueList("assetUsage", siteId));
    public Map<String, String> capitalSourceList = i18nMap(FieldValueMessageController.getFieldValueList("capitalSource", siteId));
    public Map<String, String> measureGradeList = i18nMap(FieldValueMessageController.getFieldValueList("measureGrade", siteId));
    public Map<String, String> riskGradeList = i18nMap(FieldValueMessageController.getFieldValueList("riskGrade", siteId));
    public Map<String, String> emergencyEquipmentList = i18nMap(FieldValueMessageController.getFieldValueList("emergencyEquipment", siteId));
    public Map<String, String> importEquipmentList = i18nMap(FieldValueMessageController.getFieldValueList("importEquipment", siteId));
    public Map<String, String> accountingCategoryList = i18nMap(FieldValueMessageController.getFieldValueList("accountingCategory", siteId));
    public Map<String, String> measuringItemCategoryList = i18nMap(FieldValueMessageController.getFieldValueList("measuringItemCategory", siteId));
    public Map<String, String> repairFormList = i18nMap(FieldValueMessageController.getFieldValueList("repairForm", siteId));
    public Map<String, String> assetStatusList = i18nMap(assetCreateService.getAssetStatusList());
    public Map<String, String> assetValueCategoryList = i18nMap(FieldValueMessageController.getFieldValueList("assetValueCategory", siteId));
    public Map<String, String> capitalSource2List = i18nMap(FieldValueMessageController.getFieldValueList("capitalSource2", siteId));
    public Map<String, String> deviceStatusList = i18nMap(FieldValueMessageController.getFieldValueList("deviceStatus", siteId));
    public Map<String, String> networkingStatusList = i18nMap(FieldValueMessageController.getFieldValueList("networkingStatus", siteId));
    public Map<String, String> proprietaryClassList = i18nMap(FieldValueMessageController.getFieldValueList("proprietaryClass", siteId));
    public Map<String, String> assetQualityList = i18nMap(FieldValueMessageController.getFieldValueList("assetQuality", siteId));
    public Map<String, String> productSegmentList = i18nMap(FieldValueMessageController.getFieldValueList("productSegment", siteId));
    public Map<String, String> assetGroupSecondary = new HashMap<>();
    public Map<String, String> asset68 = new HashMap<>();

    @Autowired
    protected AssetInfoRepository assetDao;

    @Autowired
    protected SupplierRepository supplierDao;

    @Autowired
    protected OrgInfoRepository orgDao;

    @Autowired
    protected UserAccountRepository userDao;

    @Autowired
    protected UserAccountMapper userAccountMapper;

    @Autowired
    protected QrCodeLibRepository qrcodeDao;

    @Autowired
    protected QrCodeService qrCodeService;

    @Autowired
    protected SysAuthGroupRepository authGroupDao;

    @Autowired
    protected AssetTypeGroupRepository assetTypeGroupRepository;

    @Autowired
    protected AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected AssetFileImportSupportingService supportingService;

    @Autowired
    private ZkbFileDirectoryService zkbFileDirectoryService;

    @Autowired
    protected AssetDepreciationService assetDepreciationService;

    @Autowired
    protected I18nMessageRepository i18nDao;

    @Autowired
    public AssetTagsMapper assetTagsMapper;

    @Autowired
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;

    @Autowired
    private V2ServiceRequestRepository v2ServiceRequestRepository;

    @Autowired
    private OrgInfoLocalCacheService orgInfoLocalCacheService;

    @Autowired
    MvsUtil mvsUtil;

    @Autowired
    private MqUtils mqUtils;


    protected OrgInfo selectedOrgToImport;

    protected String assetCodeConfig;

    protected WorkflowConfig workflowConfig;

    protected boolean isNeedAudit;

    protected boolean crossSite;

    protected Map<String, AssetInfo> parentAssetsMap = new HashMap<>();

    protected List<AssetTypeGroup> assetTypeGroupList = new ArrayList<>();
    protected Map<String, AssetTypeGroup> assetTypeGroupMap = new HashMap<>();

    public Map checkData(List<AssetImport> assetImportList, List<Map<String, Object>> importOrgMap, List<AssetInfo> unCheckNurseSiteMaintainList, Progress progress) {
        assetTypeGroupList = assetTypeGroupRepository.listAssetTypeGroupInHierarchyByUser(UserContextService.getCurrentUserAccount().getUid());
        assetTypeGroupMap = assetTypeGroupList.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, t -> t));
        /*if (!crossSite) {
            //only selected site can import clinical dept, otherwise clinical dept excel sheet is meaningless.
            importOrgMap.forEach(item -> {
                OrgInfo newOrgInfo = (OrgInfo) item.get("org");

                //parent uid is not working for child clinical dept, so we use site id
                OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

                item.put("status", orgInfo == null ? AssetFileImportBaseService.ImportStatus.New : AssetFileImportBaseService.ImportStatus.Exist);
            });
        }*/

        Map<String, Boolean> result = new HashMap<>();
        result.put("canBeImport", Boolean.TRUE);
        result.put("unCheckedNurseSite", Boolean.FALSE);
        Map<String, OrgInfo> orgNamesUriCache = null;
        if (crossSite) {
            orgNamesUriCache = orgInfoLocalCacheService.flatternLocalOrgNamesCache(UserContextService.getCurrentUserAccount().getTenantUID());
        }
        for (AssetImport assetImport : assetImportList) {

            if (!StringUtils.isEmpty(assetImport.getUid())) {
                AssetInfo existAsset = assetDao.findByUid(assetImport.getUid());
                if(existAsset !=null && !StringUtils.isEmpty(existAsset.getAssetDeptUID())){
                    OrgInfo org = configCacheService.findOrgByUid(existAsset.getAssetDeptUID());
                    if(org!=null){
                        existAsset.setAssetDeptName(org.getName());
                    }

                }
                assetImport.setOldQrCode(existAsset.getQrCode());
                BeanUtils.copyProperties(assetImport, existAsset, beanNullProperty(assetImport));
                BeanUtils.copyProperties(existAsset, assetImport);
                //检查是否输入非法UUID
                if (!tenantUID.equals(assetImport.getTenantUID())) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    assetImport.addErrMsg(String.format("第%d行错误: 非法UUID！"));
                }
            }
            //对默认值和组织机构进行处理
            OrgInfo site;
            if (crossSite) {
                SiteOrgWithDeptOrg siteOrgInAsset = findSiteOrgInAssetExcelRow(assetImport, orgNamesUriCache);
                markDefautValue(assetImport, siteOrgInAsset.getSiteOrg(), false);
                site = siteOrgInAsset.getSiteOrg();
            } else {
                markDefautValue(assetImport, selectedOrgToImport, isNeedAudit);
                site = selectedOrgToImport;
            }
            if (ObjectUtil.isNotEmpty(assetImport.getUid()) && !assetImport.getSiteUID().equals(site.getUid())) {
                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误: 院区不支持修改");
            }
            if (CollectionUtils.isNotEmpty(assetImport.getErrMsg())) {
                result.put("canBeImport", Boolean.FALSE);
            }
            if (null == assetImport.getAssetGroup()) {
                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误:" + WebUtil.getMessage("assetGroup") + WebUtil.getMessage("ValidationRequire"));
            }

            if (StringUtils.isEmpty(assetImport.getDepartNum()) && StringUtils.isEmpty(assetImport.getUid())) {
                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误: 设备编号是必须的");
            }
            if (StringUtils.isEmpty(assetImport.getName())) {
                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误: 设备名称是必须的");

            }
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            boolean zkbAccountBoolean = userContextService.isZkbAccount();
            if (zkbAccountBoolean) {
                if (StringUtils.isEmpty(assetImport.getMainDirectory())) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    assetImport.addErrMsg("错误: 主目录名字不能为空");
                } else {
                    List<ZkbFileDirectory> mainDirectoryList = zkbFileDirectoryService.findByLevelAndName(zkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE, assetImport.getMainDirectory());
                    if (mainDirectoryList.isEmpty()) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 主目录名字没有找到! " + assetImport.getMainDirectory());
                    }
                }
                if (StringUtils.isEmpty(assetImport.getSecondDirectory())) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    assetImport.addErrMsg("错误: 子目录名字不能为空");
                } else {
                    List<ZkbFileDirectory> secondDirectoryList = zkbFileDirectoryService.findByLevelAndName(zkbFileDirectoryService.SUB_DIRECTORY_LEVEL_VALUE, assetImport.getSecondDirectory());
                    if (secondDirectoryList.isEmpty()) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 子目录名字找不到! " + assetImport.getSecondDirectory());
                    }
                }
            }
            //check clinical dept
            String clinicalDeptName = assetImport.getClinicalDeptName();
            List<AssetInfo> assets = new ArrayList<>();
            AssetInfo asset = null;
            if (!StringUtils.isEmpty(assetImport.getUid())) {
                asset = assetDao.findByUid(assetImport.getUid());
                assets.add(asset);
            }
            if (StringUtils.isEmpty(clinicalDeptName)) {
                //新增的时候不允许为空，更新的时候允许为空。
                if (StringUtils.isEmpty(assetImport.getUid())) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    assetImport.addErrMsg("错误: 所属科室为空");
                } else {
                    assetImport.setClinicalDeptId(null);
                    assetImport.setClinicalDeptUID(null);
                }
            } else {
                OrgInfo clinicalDept = null;
                // todu:add site
                //if child clinical dept not found, we don't create. we only create parent clinical dept in later steps.
                if (supportingService.isLongName(clinicalDeptName)) {
                    clinicalDept = orgDao.findFirstBySiteUIDAndNameAndOrgType(assetImport.getSiteUID(), clinicalDeptName, 4);
                    if (clinicalDept == null) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 所属科室没有找到! " + clinicalDeptName);
                    }

                } else {
                    if (crossSite) {
                        //single site mode, we will import org in later steps, so no need to check.
                        clinicalDept = orgDao.findFirstByParentUIDAndShortName(assetImport.getSiteUID(), clinicalDeptName);
                        if (clinicalDept == null) {
                            assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            assetImport.addErrMsg("错误: 所属科室没有找到! " + clinicalDeptName);
                        }
                    } else {
                        if (!supportingService.isClinicalDeptExists(clinicalDeptName, importOrgMap)) {
                            assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            assetImport.addErrMsg("错误: 所属科室没有找到! " + clinicalDeptName + ",请添加到科室列表页");
                        }
                    }

                }

            }

            //check asset dept
            String assetDeptName = assetImport.getAssetDeptName();
            if (!StringUtils.isEmpty(assetDeptName)) {
                OrgInfo assetDept;
                if (supportingService.isLongName(assetDeptName)) {
                    assetDept = orgDao.findFirstBySiteUIDAndNameAndOrgType(assetImport.getSiteUID(), assetDeptName, 4);
                    if (assetDept == null) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 使用科室没有找到! " + assetDeptName);
                    }
                } else {
                    if (crossSite) {
                        //single site mode, we will import org in later steps, so no need to check.
                        assetDept = orgDao.findFirstByParentUIDAndShortName(assetImport.getSiteUID(), assetDeptName);
                        if (assetDept == null) {
                            assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            assetImport.addErrMsg("错误: 使用科室没有找到! " + assetDeptName);
                        }
                    } else {
                        if (!supportingService.isClinicalDeptExists(assetDeptName, importOrgMap)) {
                            assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            assetImport.addErrMsg("错误: 使用科室没有找到! " + assetDeptName + ",请添加到科室列表页");
                        }
                    }
                }
            }
            if (asset != null) {
                OrgInfo orgInfo = orgDao.findByUid(assetImport.getAssetDeptUID());
                String assetDeptNameOld = null;
                if (orgInfo != null) {
                    assetDeptNameOld = orgInfo.getName();
                }

                if ((StringUtils.hasText(assetDeptNameOld) && !assetDeptNameOld.equals(assetDeptName)) ||
                        (StringUtils.hasText(asset.getClinicalDeptName()) && !asset.getClinicalDeptName().equals(clinicalDeptName))) {
                    if (asset.getSharedAssetTypeUid() != null) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 设备已绑定调剂名称,不能修改科室");
                    }
                    if (!assetInfoBulkEditCheckService.verifyForMaintain(assets)) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 设备有未完成的保养工单,不能修改科室");
                    }
                    if (!assetInfoBulkEditCheckService.verifyForInventory(assets)) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 设备有未盘点的工单,不能修改科室");
                    }
                    for (AssetInfo as : assets) {
                        boolean assetFlag = true;
                        List<V2_ServiceRequest> serviceRequestList = v2ServiceRequestRepository.findByAssetId(as.getId());
                        if (CollectionUtils.isNotEmpty(serviceRequestList)) {
                            for (V2_ServiceRequest service : serviceRequestList) {
                                if (service.getCloseTime() == null) {
                                    assetFlag = false;
                                }
                            }
                            if (assetFlag) {
                                continue;
                            } else {
                                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                                result.put("canBeImport", Boolean.FALSE);
                                assetImport.addErrMsg("错误: 设备有未完成的维修工单,不能修改科室");
                            }
                        }
                    }
                    //临床助手检查进行中
                    unCheckNurseSiteMaintainList.addAll(assetInfoBulkEditCheckService.verifyForNurseSiteByBatchEdit(assets));
                }
                if (assetDao.secondment(asset.getUid()) > 0) {
                    boolean flag = false;
                    if (!StringUtils.isEmpty(assetDeptNameOld) && !assetDeptNameOld.equals(assetDeptName)) {
                        flag = true;
                    } else if (!StringUtils.isEmpty(assetDeptName) && !assetDeptName.equals(assetDeptNameOld)) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 设备正在科室借调中,不能修改使用科室");
                    }
                    String clinicalDeptNameOld = asset.getClinicalDeptName();
                    String clinicalDeptNameNew = clinicalDeptName;
                    if (!StringUtils.isEmpty(clinicalDeptNameOld) && !clinicalDeptNameOld.equals(clinicalDeptNameNew)) {
                        flag = true;
                    } else if (!StringUtils.isEmpty(clinicalDeptNameNew) && !clinicalDeptNameNew.equals(clinicalDeptNameOld)) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 设备正在科室借调中,不能修改所属科室");
                    }
                }
            }

            //check depart num to avoid duplicate when update
            boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(assetImport.getSiteUID(), assetImport);
            if (isDuplicate) {
                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误: 设备编号重复");
            }

            if (!StringUtils.isEmpty(assetImport.getDepartNum())) {
                if (assetImport.getDepartNum().length() > 64) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    assetImport.addErrMsg("错误: 设备编号不能超过64位");
                }

                //配置了三码合一和设备编号不为空，则检查，Jira: YHAAO-1594
                if ((AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig) || AssetFileImportSupportingService.ASSET_CODE_MERGE_TWO.equals(assetCodeConfig))) {


                    if (!assetImport.getDepartNum().equals(assetImport.getFinancingNum()) && !StringUtils.isEmpty(assetImport.getFinancingNum())) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误: 资产编码与设备编码不相同不允许导入");
                    } else {
                        assetImport.setFinancingNum(assetImport.getDepartNum());

                        if (AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
                            assetImport.setQrCode(assetImport.getDepartNum());
                        }

                    }


                }
            }

            if (!StringUtils.isEmpty(assetImport.getFinancingNum()) && assetImport.getFinancingNum().length() > 64) {

                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误: 资产编码不能超过64位");
            }

            //check QrCode
            if (!StringUtils.isEmpty(assetImport.getQrCode())) {
                if (!Objects.equals(assetImport.getQrCode(), assetImport.getOldQrCode())) {
                    if (supportingService.isQrCodeNewOrChanged(assetImport)) {
                        if (AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
                            //三码合一情况下，可以创建新的二维码或者二维码已发行
                            QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(assetImport.getQrCode(), assetImport.getSiteUID());
                            if (qrCodeLib != null && qrCodeLib.getStatus() != 1) {
                                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                                result.put("canBeImport", Boolean.FALSE);
                                assetImport.addErrMsg("错误:" + WebUtil.getMessage("InvalidQRCode") + assetImport.getQrCode());
                            }
                        } else {
                            //非三码合一情况下，二维码需要已发行
                            boolean isAvailable = qrCodeService.isQrcodeAvailableInStatus(assetImport.getQrCode(), assetImport.getSiteUID(), 1);
                            if (!isAvailable) {
                                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                                result.put("canBeImport", Boolean.FALSE);
                                assetImport.addErrMsg("错误:" + WebUtil.getMessage("InvalidQRCode") + assetImport.getQrCode());
                            }
                        }
                    }
                }
            }


            if (StringUtils.hasLength(assetImport.getMedicalApparatusClassification())) {
                SysAuthGroup selectedAuthGroup = authGroupDao.findByClassificationCode(assetImport.getMedicalApparatusClassification());
                if (selectedAuthGroup == null) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    assetImport.addErrMsg("错误:" + WebUtil.getMessage("medicalApparatusClassification") + "编码不存在");
                }
            }
            // 资产分类校验
            if (UserContextService.getCurrentUserAccount().isEnableAssetTypeGroup()) {
                if (ObjectUtil.isEmpty(assetImport.getAssetType())) {
                    assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    assetImport.addErrMsg("错误: 资产分类编码为空！");
                } else {
                    AssetTypeGroup selectedAssetTypeGroup = assetTypeGroupMap.get(assetImport.getAssetType());
                    if (selectedAssetTypeGroup == null) {
                        assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        assetImport.addErrMsg("错误:" + WebUtil.getMessage("assetTypeGroup") + "编码不存在");
                    }
                }
            }
            // 县域租户添加设备性质字段，且必填
            if (assetImport.getAssetQuality() == null && userContextService.isCountyArea()) {
                assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                assetImport.addErrMsg("错误: 设备性质是必须的");
            }
            if (CollectionUtils.isNotEmpty(unCheckNurseSiteMaintainList)) {
                result.put("unCheckedNurseSite", Boolean.TRUE);
            }
            progress.setComplete();
        }

        return result;
    }

    public void setSelectedOrgToImport(OrgInfo selectedOrgToImport, boolean crossSite) {
        this.selectedOrgToImport = selectedOrgToImport;
        this.crossSite = crossSite;

        workflowConfig = configService.findConfigBySite(selectedOrgToImport.getSiteUID());

        if (workflowConfig != null) {
            assetCodeConfig = workflowConfig.getAssetCodeConfig();
        }
        isNeedAudit = configService.isAssetNeedAudit(selectedOrgToImport.getSiteUID());

    }

    /**
     * 对i18n类型的字段进行转换
     */
    public void i18nConvert(AssetImport assetImport) {
        if (!StringUtils.isEmpty(assetImport.getFunctionGroupStr())) {
            String functionGroup = assetFunctionTypes.get(assetImport.getFunctionGroupStr());
            if (Objects.isNull(functionGroup)) {
                setErrorMsg(assetImport, "medicalInstrumentClassification", assetImport.getFunctionGroupStr());
            } else {
                assetImport.setFunctionGroup(Integer.parseInt(functionGroup));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAssetGroupStr())) {
            String value = assetGroup.get(assetImport.getAssetGroupStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "assetGroup", assetImport.getAssetGroupStr());
            } else {
                assetImport.setAssetGroup(Integer.parseInt(value));
                if (!StringUtils.isEmpty(assetImport.getAssetGroupSecondaryStr())) {
                    String secondaryValue = assetGroupSecondary(Integer.parseInt(value), siteId, assetImport.getAssetGroupSecondaryStr());
                    if (Objects.nonNull(secondaryValue)) {
                        assetImport.setAssetGroupSecondary(Integer.parseInt(secondaryValue));
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAssetClassStr())) {
            String value = assetClassList.get(assetImport.getAssetClassStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "assetClass", assetImport.getAssetClassStr());
            } else {
                assetImport.setAssetClass(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getDepreciationMethodStr())) {
            String value = depreciationMethod.get(assetImport.getDepreciationMethodStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "DepreciationMethod", assetImport.getDepreciationMethodStr());
            } else {
                assetImport.setDepreciationMethod(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAssetUsageStr())) {
            String value = assetUsageList.get(assetImport.getAssetUsageStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "UsageClassification", assetImport.getAssetUsageStr());
            } else {
                assetImport.setAssetUsage(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getCapitalSourceStr())) {
            String value = capitalSourceList.get(assetImport.getCapitalSourceStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "CapitalSource", assetImport.getCapitalSourceStr());
            } else {
                assetImport.setCapitalSource(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getMeasureGradeStr())) {
            String value = measureGradeList.get(assetImport.getMeasureGradeStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "measureGrade", assetImport.getMeasureGradeStr());
            } else {
                assetImport.setMeasureGrade(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getRiskGradeStr())) {
            String value = riskGradeList.get(assetImport.getRiskGradeStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "riskGrade", assetImport.getRiskGradeStr());
            } else {
                assetImport.setRiskGrade(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getEmergencyEquipmentStr())) {
            String value = emergencyEquipmentList.get(assetImport.getEmergencyEquipmentStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "emergencyEquipment", assetImport.getEmergencyEquipmentStr());
            } else {
                assetImport.setEmergencyEquipment(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getImportEquipmentStr())) {
            String value = importEquipmentList.get(assetImport.getImportEquipmentStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "IsImportEquipment", assetImport.getImportEquipmentStr());
            } else {
                assetImport.setImportEquipment(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAccountingCategoryStr())) {
            String value = accountingCategoryList.get(assetImport.getAccountingCategoryStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "AccountingCategory", assetImport.getAccountingCategoryStr());
            } else {
                assetImport.setAccountingCategory(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getMeasuringItemCategoryStr())) {
            String value = measuringItemCategoryList.get(assetImport.getMeasuringItemCategoryStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "measuringItemCategory", assetImport.getMeasuringItemCategoryStr());
            } else {
                assetImport.setMeasuringItemCategory(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getRepairFormStr())) {
            String value = repairFormList.get(assetImport.getRepairFormStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "repairForm", assetImport.getRepairFormStr());
            } else {
                assetImport.setRepairForm(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getStatusStr())) {
            String value = assetStatusList.get(assetImport.getStatusStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "CurrentStatus", assetImport.getStatusStr());
            } else {
                assetImport.setStatus(Integer.parseInt(value));
            }
        } else {
            if (StringUtils.isEmpty(assetImport.getUid())) {
                assetImport.setStatus(1);
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAssetValueCategory())) {
            String value = assetValueCategoryList.get(assetImport.getAssetValueCategory());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "assetValueCategory", assetImport.getAssetValueCategory());
            } else {
                assetImport.setAssetValueCategory(value);
            }
        }
        if (!StringUtils.isEmpty(assetImport.getCapitalSource2Str())) {
            String value = capitalSource2List.get(assetImport.getCapitalSource2Str());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "capitalSource2", assetImport.getCapitalSource2Str());
            } else {
                assetImport.setCapitalSource2(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getDeviceStatusStr())) {
            String value = deviceStatusList.get(assetImport.getDeviceStatusStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "deviceStatus", assetImport.getDeviceStatusStr());
            } else {
                assetImport.setDeviceStatus(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getNetworkingStatusStr())) {
            String value = networkingStatusList.get(assetImport.getNetworkingStatusStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "networkingStatus", assetImport.getNetworkingStatusStr());
            } else {
                assetImport.setNetworkingStatus(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getProprietaryClass())) {
            String value = proprietaryClassList.get(assetImport.getProprietaryClass());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "ProprietaryClass", assetImport.getProprietaryClass());
            } else {
                assetImport.setProprietaryClass(value);
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAssetQualityStr())) {
            String value = assetQualityList.get(assetImport.getAssetQualityStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "assetQuality", assetImport.getAssetQualityStr());
            } else {
                assetImport.setAssetQuality(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getProductSegmentStr())) {
            String value = productSegmentList.get(assetImport.getProductSegmentStr());
            if (Objects.isNull(value)) {
                setErrorMsg(assetImport, "productSegment", assetImport.getProductSegmentStr());
            } else {
                assetImport.setProductSegment(Integer.parseInt(value));
            }
        }
        if (!StringUtils.isEmpty(assetImport.getAssetCode68Str())) {
            String value = asset68.get(assetImport.getAssetCode68Str());
            if (Objects.nonNull(value)) {
                assetImport.setAssetCode68(Integer.parseInt(value));
            } else {
                Integer functionGroup = assetImport.getFunctionGroup();
                Map<String, String> assetCode68ByFunctionGroup = i18nMap(FieldValueMessageController.getFieldValueList(String.valueOf(functionGroup), siteId));
                if (MapUtils.isNotEmpty(assetCode68ByFunctionGroup)) {
                    assetImport.setAssetCode68(Integer.parseInt(assetCode68ByFunctionGroup.get(assetImport.getAssetCode68Str())));
                    asset68.putAll(assetCode68ByFunctionGroup);
                } else {
                    setErrorMsg(assetImport, "assetCode68", assetImport.getAssetCode68Str());
                }
            }
        }
    }

    public void setErrorMsg(AssetImport assetImport, String messageKey, String value) {
        assetImport.addErrMsg("警告:无效值" + WebUtil.getMessage(messageKey) + "[" + value + "]");
    }

    /**
     * 将i18n集合转为中英文map
     *
     * @param i18nMessages
     * @return
     */
    public Map<String, String> i18nMap(List<I18nMessage> i18nMessages) {
        Map<String, String> i18nMap = new HashMap<>();
        i18nMessages.forEach(e -> {
            i18nMap.put(e.getValueZh(), e.getMsgKey());
            i18nMap.put(e.getValueEn(), e.getMsgKey());
        });
        return i18nMap;
    }

    public void markDefautValue(AssetImport asset, OrgInfo selectedOrgToImport, boolean isNeedAudit) {
        Date now = new Date();
        if (StringUtils.isEmpty(asset.getUid())) {
            // new
            asset.setSiteId(selectedOrgToImport.getSiteId());
            asset.setHospitalId(selectedOrgToImport.getId());

            asset.setTenantUID(selectedOrgToImport.getTenantUID());
            asset.setHospitalUID(selectedOrgToImport.getHospitalUID());
            asset.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            asset.setSiteUID(selectedOrgToImport.getSiteUID());

            if (Objects.isNull(asset.getUid())) {
                asset.setIsValid(true);
                asset.setIsDeleted(Boolean.FALSE);
                if (Objects.isNull(asset.getStatus())) {
                    asset.setStatus(1);
                }
                if (Objects.isNull(asset.getIsPm())) {
                    asset.setIsPm(true);
                }
                if (Objects.isNull(asset.getRepairFormStr())) {
                    asset.setRepairForm(1);
                }
                if (Objects.isNull(asset.getIsChecked())) {
                    asset.setIsChecked(true);
                }
                if (Objects.isNull(asset.getUseRegistration())) {
                    asset.setUseRegistration(true);
                }
                if (Objects.isNull(asset.getIsWarranty())) {
                    asset.setIsWarranty(true);
                }
                if (Objects.isNull(asset.getIsFixed())) {
                    asset.setIsFixed(true);
                }
                if (Objects.isNull(asset.getNetworkingStatus())) {
                    asset.setNetworkingStatus(0);
                }
                asset.setConnectToInsite(3);
                asset.setIbDeviceStatus(5);
                asset.setVisibleStatus(1);
            }
            asset.setCreatedBy(UserContextService.getCurrentUserAccount().getName());
            asset.setCreatedDate(now);
            asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            asset.setLastModifiedDate(now);

            if (asset.getDeviceStatus() == null) {
                asset.setDeviceStatus(0);
            }

            //only new asset need to set this
            if (isNeedAudit) {
                asset.setUsageStatus(UsageStatusConstant.UNDER_REVIEW); //待审核
            } else {
                asset.setUsageStatus(UsageStatusConstant.ARCHIVED); //已建档
            }

        } else {
            //update
            asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            asset.setLastModifiedDate(now);
            cleanRelatedFieldsWhenUpdating(asset);
        }


    }

    public void cleanRelatedFieldsWhenUpdating(AssetImport asset) {
        if (asset.getAssetOwnerName() == null) {
            asset.setAssetOwnerId(null);
            asset.setAssetOwnerTel(null);
        }
        if (asset.getAssetOwnerName2() == null) {
            asset.setAssetOwnerId2(null);
            asset.setAssetOwnerTel2(null);
        }
        if (asset.getAssetOwnerName3() == null) {
            asset.setAssetOwnerId3(null);
            asset.setAssetOwnerTel3(null);
        }

        if (asset.getClinicalOwnerName() == null) {
            asset.setClinicalOwnerId(null);
            asset.setClinicalOwnerTel(null);
        }
        if (asset.getClinicalDeptName() == null) {
            asset.setClinicalDeptId(null);
            asset.setClinicalDeptUID(null);
        }
        if (asset.getVendor() == null) {
            asset.setSupplierId(null);
        }

    }

    public SiteOrgWithDeptOrg findSiteOrgInAssetExcelRow(AssetImport asset, Map<String, OrgInfo> orgNamesUriCache) {
        String institution = asset.getInstitutionName();
        String hospital = asset.getHospitalName();
        String site = asset.getSiteName();
        String clinicalDept = asset.getClinicalDeptName();
        String assetDept = asset.getAssetDeptName();

        SiteOrgWithDeptOrg siteOrgWithDeptOrg = orgInfoLocalCacheService.findSiteOrgByFlatternedMap(orgNamesUriCache, institution, hospital, site, clinicalDept, assetDept);

        return siteOrgWithDeptOrg;
    }

    public static String[] beanNullProperty(Object assetImport) {
        final BeanWrapper src = new BeanWrapperImpl(assetImport);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullProperty = new HashSet<>();
        AssetImport assetImport1 = (AssetImport) assetImport;
        List<String> deletedFiled = assetImport1.getDeletedField();
        for (PropertyDescriptor item : pds) {
            Object value = src.getPropertyValue(item.getName());
            if (value == null && !deletedFiled.contains(item.getName())) {
                nullProperty.add(item.getName());
            }
        }
        String[] properties = new String[nullProperty.size()];
        return nullProperty.toArray(properties);
    }


    public void orgCheck(List<Map<String, Object>> importOrgMap) {
        if (!crossSite) {
            //only selected site can import clinical dept, otherwise clinical dept excel sheet is meaningless.
            importOrgMap.forEach(item -> {
                OrgInfo newOrgInfo = (OrgInfo) item.get("org");

                //parent uid is not working for child clinical dept, so we use site id
                OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

                item.put("status", orgInfo == null ? AssetFileImportBaseService.ImportStatus.New : AssetFileImportBaseService.ImportStatus.Exist);
            });
        }
    }

    public String assetGroupSecondary(Integer assetGroup, Integer siteId, String assetGroupSecondaryStr) {
        String msgType = "assetGroupSecondary_" + assetGroup;
        String mappingKey = assetGroup + "_" + assetGroupSecondaryStr;
        String assetGroupSecondaryValue = assetGroupSecondary.get(mappingKey);
        if (Objects.isNull(assetGroupSecondaryValue)) {
            List<I18nMessage> assetGroupSecondaryTmp = FieldValueMessageController.getFieldValueList(msgType, siteId);
            if (CollectionUtils.isNotEmpty(assetGroupSecondaryTmp)) {
                assetGroupSecondaryTmp.forEach(e -> {
                    assetGroupSecondary.put(assetGroup+"_"+e.getValueZh(), e.getMsgKey());
                    assetGroupSecondary.put(assetGroup+"_"+e.getValueEn(), e.getMsgKey());
                });
            }
            return assetGroupSecondary.get(mappingKey);

        }else{
            return assetGroupSecondaryValue;
        }
    }
}
