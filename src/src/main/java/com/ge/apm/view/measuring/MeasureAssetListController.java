package com.ge.apm.view.measuring;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.ConfigItemMapper;
import com.ge.apm.dao.mapper.MeasureAssetMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.EasyExcelExportUtil;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.utils.WrapperSearchFilterUtils;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.preview.PreViewerBean;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.wo.WorkOrderMgrController;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 212605082
 * @date 2018/6/4
 */
@ManagedBean
@ViewScoped
@Data
@EqualsAndHashCode(callSuper = true)
public class MeasureAssetListController extends JpaCRUDController<AssetInfo> {

    private int currentMonthCount = 0;
    private int nextMonthCount = 0;
    private String queryAssetUID;
    private Boolean scrapped = false;
    private Boolean flag = true;
    private Boolean measurementRepairVisible;
    private Map<String, Boolean> selectedMap;
    private OrgInfo filterOrg;
    @Getter @Setter
    private OrgInfo filterAssetDeptOrg;
    private UserAccount currentUser;
    private List<AssetInfo> currentPageAssets;
    private List<V2_BlobObject> resultAttachList;
    private List<MeasuringCertificateDetail> measuringCertificateList;

    private AssetInfoRepository dao;
    private UaaService uaaService;
    private TreeNode selectedNode;
    private UserAuth customRoleAuth;
    private StreamedContent exportFile;
    private ConfigCacheService ccService;
    private FileDownloadService fileDownloadService;
    private I18nMessageRepository i18nMessageDao;
    private ConfigItemMapper configItemMapper;
    private BlobFileService blobService;
    private BlobObjectRepository blobDao;
    private OrgInfoTreeService orgInfoTreeService;
    private AssetInfoMapper assetInfoMapper;
    private WorkflowService workflowService;
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;
    private TenantInfo currentTenantInfo;
    private PreViewerBean preViewerBean;
    private MeasuringService measuringService;
    private SystemEventAuditAopAspect systemEventAuditAopAspect;

    @Getter
    private int matchType=9;

    @Getter
    @Setter
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();

    @Getter  @Setter
    private List<AssetInfo> assetInfoList ;


    @Getter  @Setter
    private TreeNode orgTreeSearch;
    @Getter  @Setter
    private TreeNode orgTree;

    @Getter  @Setter
    private OrgInfo org;
    private OrgInfoRepository orgDao;
    @Getter @Setter
    private AssetInfo selectAsset;
    private I18nMessageService i18nMessageService;
    private MeasuringCertificateDetail selectedMeasuringCertificateDetail;

    private MeasureAssetMapper measureAssetMapper;

    private SystemTemplateService systemTemplateService;

    private UserAccountRepository userAccountDao;

    private StreamedContent excelFile;

    @Getter
    private Boolean measuringDateWarningEnabled = false;
    @Getter
    private Boolean displayMeasureCycle = false;
    @Getter @Setter
    private Integer validityMonths;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        measuringCertificateDetailDao = WebUtil.getBean(MeasuringCertificateDetailRepository.class);
        i18nMessageDao = WebUtil.getBean(I18nMessageRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        ccService = WebUtil.getBean(ConfigCacheService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        configItemMapper = WebUtil.getBean(ConfigItemMapper.class);
        preViewerBean = WebUtil.getBean(PreViewerBean.class);
        measuringService = WebUtil.getBean(MeasuringService.class);
        systemEventAuditAopAspect = WebUtil.getBean(SystemEventAuditAopAspect.class);
        measureAssetMapper = WebUtil.getBean(MeasureAssetMapper.class);
        WorkflowConfig woc = workflowService.getWorkflowByOrg(currentUser.getSiteUID());
        measurementRepairVisible = woc != null && woc.getMeasurementRepair() !=null && woc.getMeasurementRepair();

        // 获取计量结果日期预警配置项
        ConfigItemValue configItemValue = configItemMapper.findByKey("measuringDateWarning", currentUser.getSiteUID());
        if (configItemValue != null && "true".equalsIgnoreCase(configItemValue.getValue())) {
            measuringDateWarningEnabled = true;
        }
        ConfigItemValue configItemValue1 = configItemMapper.findByKey("displayMeasureCycle", currentUser.getSiteUID());
        if (configItemValue1 != null && "true".equalsIgnoreCase(configItemValue1.getValue())) {
            displayMeasureCycle = true;
        }
        String enCodeParameters = WebUtil.getRequestParameter("str");
        queryAssetUID = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
        this.selectedMap = MapUtil.newHashMap();
        this.searchFilters = new ArrayList<>();
        totalMeasureExpireMonths();
        currentTenantInfo = ccService.getTenantByUid(UserContextService.getCurrentUserAccount().getTenantUID());
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        userAccountDao = WebUtil.getBean(UserAccountRepository.class);

        org = orgDao.findById(currentUser.getOrgInfoId());

        TreeNode treeNodeSearch = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(currentUser.getSiteUID(), null);
        orgTreeSearch = initOrgTree(treeNodeSearch);
        String encodeStr = WebUtil.getRequestParameter("str");
        if(StringUtils.hasText(encodeStr)){
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            String matchTypeStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "matchType");
            String boUuid = (String) UrlEncryptController.getValueFromMap(encodeStr, "boUuid");
            if(StringUtils.isEmpty(matchTypeStr)) return;
            matchType = Integer.valueOf(matchTypeStr);
            this.selected = dao.findByUid(selectedId);
            if(this.selected == null){
                this.selected = new AssetInfo();
                this.selected.setBoUuid(boUuid);
            }
            if(Integer.valueOf(1).equals(matchType) || Integer.valueOf(2).equals(matchType)){
                measuringCertificateList = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
            }else{

                MeasuringCertificateDetail byId = measuringCertificateDetailDao.findById(boUuid);
                if(byId!=null){
                    measuringCertificateList=new ArrayList<>();
                    measuringCertificateList.add(byId);
                }
            }
        }

    }

    @Override
    protected AssetInfoRepository getDAO(){
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());

        if(matchType==1 || matchType==2 || matchType == 9){
            Wrapper<AssetInfo> wrapper = convert();
            Sort sort = pageRequest.getSort();
            for (Sort.Order order : sort) {
                Sort.Direction direction = order.getDirection();
                String property = ZipUtil.camelToUnderline(order.getProperty());
                String prefix = "check_date".equals(property) || "validity_months".equals(property)? "mcd." : "ai.";
                wrapper.orderBy(prefix + property, "ASC".equals(direction.name()));
            }
            if (matchType == 9) {
                wrapper.and().in("mcd.certificate_type", Arrays.asList(1, 2));
            } else {
                wrapper.and().eq("mcd.certificate_type", matchType);
            }
            wrapper.orderBy("mcd.id");
            currentPageAssets = assetInfoMapper.selectMeasureResult(pagination, wrapper);
        }else{
            Wrapper<AssetInfo> wrapper1 = measureUnpairedResultConvert();
            currentPageAssets = assetInfoMapper.selectMeasureUnpairedResult(pagination, wrapper1);

            int i=1;
            for (AssetInfo currentPageAsset : currentPageAssets) {
                currentPageAsset.setId(i);
                i++;
            }
        }

        // 如果启用了计量日期预警，则对数据进行校验
        if (measuringDateWarningEnabled) {
            for (AssetInfo asset : currentPageAssets) {
                String warningMsg = asset.validateMeasuringDates();
                if (warningMsg != null) {
                    asset.setHasDateWarning(true);
                    asset.setDateWarningMessage(warningMsg);
                } else {
                    asset.setHasDateWarning(false);
                    asset.setDateWarningMessage("");
                }
            }
        }

        return new PageImpl<>(currentPageAssets, pageRequest, pagination.getTotal());
    }

    private Wrapper<AssetInfo> measureUnpairedResultConvert() {
        Wrapper<AssetInfo> wrapper = new EntityWrapper<>();

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
            wrapper.andNew();
            wrapper.in("mcd.tenant_uid", orgUidList);
            wrapper.or().in("mcd.institution_uid", orgUidList);
            wrapper.or().in("mcd.hospital_uid", orgUidList);
            wrapper.or().in("mcd.site_uid", orgUidList);

        }
        wrapper.andNew();
        wrapper.isNull("mcd.asset_uid");


        if(this.hasFilterOnField("name")){
            String name = searchFilters.stream().filter(sf -> "name".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("name", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.asset_name", name);
        }

        if(this.hasFilterOnField("functionType")){
            String functionType = searchFilters.stream().filter(sf -> "functionType".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("functionType", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.asset_model", functionType);
        }
        if(this.hasFilterOnField("manufacture")){
            String manufacture = searchFilters.stream().filter(sf -> "manufacture".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("manufacture", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.asset_model", manufacture);
        }
        if(this.hasFilterOnField("serialNum")){
            String serialNum = searchFilters.stream().filter(sf -> "serialNum".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("serialNum", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.asset_sn", serialNum);
        }


        if(this.hasFilterOnField("verificationConclusion")){
            Integer verificationConclusion = Integer.valueOf(searchFilters.stream().filter(sf -> "verificationConclusion".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("verificationConclusion", SearchFilter.Operator.EQ, 0)).value.toString());
            wrapper.eq("mcd.status", verificationConclusion);
        }
        if(this.hasFilterOnField("measuringItemCategory")){
            Integer measuringItemCategory = Integer.valueOf(searchFilters.stream().filter(sf -> "measuringItemCategory".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("measuringItemCategory", SearchFilter.Operator.EQ, 0)).value.toString());
            this.updateSearchFilter(new SearchFilter("measuringItemCategory", SearchFilter.Operator.EQ, measuringItemCategory, "1*"));
        }

        if(this.hasFilterOnField("checkDate")){
            List<SearchFilter> checkDateFilters = searchFilters.stream().filter(sf -> "checkDate".equals(sf.fieldName)).collect(Collectors.toList());
            WrapperSearchFilterUtils.convert(checkDateFilters, wrapper, "mcd");
        }
        if(this.hasFilterOnField("nextMeteringDate")){
            List<SearchFilter> nextMeteringDateFilters = searchFilters.stream().filter(sf -> "nextMeteringDate".equals(sf.fieldName)).collect(Collectors.toList());
            List<SearchFilter> validDateToFilters = ListUtil.newArrayList();
            for (SearchFilter nextMeteringDateFilter : nextMeteringDateFilters) {
                validDateToFilters.add(new SearchFilter("validDateTo", nextMeteringDateFilter.operator, nextMeteringDateFilter.value));
            }
            WrapperSearchFilterUtils.convert(validDateToFilters, wrapper, "mcd");
        }
        if(this.hasFilterOnField("meteringNO")){
            String meteringNO = searchFilters.stream().filter(sf -> "meteringNO".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("meteringNO", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.certificate_no", meteringNO);
        }
        if(this.hasFilterOnField("checkPerson")){
            String checkPerson = searchFilters.stream().filter(sf -> "checkPerson".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("checkPerson", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.check_person", checkPerson);
        }
        if(this.hasFilterOnField("existFileFlag")){
            String checkPerson = searchFilters.stream().filter(sf -> "existFileFlag".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("existFileFlag", SearchFilter.Operator.EQ, 0)).value.toString();
            if("0".equals(checkPerson)){
                wrapper.isNotNull("o.cu");
            }else if("1".equals(checkPerson)){
                wrapper.isNull("o.cu");
            }
        }
        if(this.hasFilterOnField("certificateType")){
            String certificateType = searchFilters.stream().filter(sf -> "certificateType".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("certificateType", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.eq("mcd.certificate_type", Integer.valueOf(certificateType));
        }
        return wrapper;
    }

    public void initMatchResult(int matchType) {
        this.matchType=matchType;
        searchFilters=new ArrayList<>();
        this.selected=null;

    }
    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    private Wrapper<AssetInfo> convert() {
        Wrapper<AssetInfo> wrapper = new EntityWrapper<>();
        if (ObjectUtil.isNotEmpty(queryAssetUID)) {
            wrapper.eq("ai.uid", queryAssetUID);
            return wrapper;
        }
        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());

            wrapper.andNew();
            wrapper.in("ai.tenant_uid", orgUidList);
            wrapper.or().in("ai.institution_uid", orgUidList);
            wrapper.or().in("ai.hospital_uid", orgUidList);
            wrapper.or().in("ai.site_uid", orgUidList);
            wrapper.or().in("ai.clinical_dept_uid", orgUidList);
            wrapper.or().in("ai.asset_dept_uid", orgUidList);
        }

        wrapper.andNew();
        if (scrapped) {
            wrapper.eq("ai.status", 4);
//            wrapper.eq("ai.is_valid", false);
        } else {
            wrapper.ne("ai.status", 4);
            wrapper.eq("ai.is_valid", true);
        }
        if(!this.hasFilterOnField("isDeleted")){
            wrapper.eq("ai.is_deleted", false);
        }
        if(!this.hasFilterOnField("measureGrade")){
            wrapper.andNew();
            wrapper.ne("ai.measure_grade", 4);
            wrapper.or().isNull("ai.measure_grade");
        } else {
            Integer measureGradeValue = Integer.valueOf(searchFilters.stream().filter(sf -> "measureGrade".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("measureGrade", SearchFilter.Operator.EQ, 0)).value.toString());
            this.updateSearchFilter(new SearchFilter("measureGrade", SearchFilter.Operator.EQ, measureGradeValue, "1*"));
        }
        List<String> clinicalUidList = Lists.newArrayList();
        if(null != filterOrg){
            if (selectedNode.isLeaf()) {
                clinicalUidList = Lists.newArrayList(filterOrg.getUid());
            } else {
                List<OrgInfo> allClinicalList = orgInfoTreeService.collectChildClinicalDeptByParent(selectedNode);
                clinicalUidList = allClinicalList.stream().map(OrgInfo::getUid).collect(Collectors.toList());
            }
        }
        wrapper.andNew().in(ObjectUtil.isNotEmpty(clinicalUidList), "ai.clinical_dept_uid", clinicalUidList);
        List<String> assetDeptUidList = Lists.newArrayList();
        if(null != filterAssetDeptOrg){
            assetDeptUidList = Lists.newArrayList(filterAssetDeptOrg.getUid());
        }
        wrapper.in(ObjectUtil.isNotEmpty(assetDeptUidList), "ai.asset_dept_uid", assetDeptUidList);
        if(this.hasFilterOnField("verificationConclusion")){
            Integer verificationConclusion = Integer.valueOf(searchFilters.stream().filter(sf -> "verificationConclusion".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("verificationConclusion", SearchFilter.Operator.EQ, 0)).value.toString());
            wrapper.eq("mcd.status", verificationConclusion);
        }
        if(this.hasFilterOnField("measuringItemCategory")){
            Integer measuringItemCategory = Integer.valueOf(searchFilters.stream().filter(sf -> "measuringItemCategory".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("measuringItemCategory", SearchFilter.Operator.EQ, 0)).value.toString());
            this.updateSearchFilter(new SearchFilter("measuringItemCategory", SearchFilter.Operator.EQ, measuringItemCategory, "1*"));
        }
        if(this.hasFilterOnField("assetGroup")){
            Integer assetGroup = Integer.valueOf(searchFilters.stream().filter(sf -> "assetGroup".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, 0)).value.toString());
            this.updateSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetGroup, "1*"));
        }
        if(this.hasFilterOnField("checkDate")){
            List<SearchFilter> checkDateFilters = searchFilters.stream().filter(sf -> "checkDate".equals(sf.fieldName)).collect(Collectors.toList());
            WrapperSearchFilterUtils.convert(checkDateFilters, wrapper, "mcd");
        }
        if(this.hasFilterOnField("nextMeteringDate")){
            List<SearchFilter> nextMeteringDateFilters = searchFilters.stream().filter(sf -> "nextMeteringDate".equals(sf.fieldName)).collect(Collectors.toList());
            List<SearchFilter> validDateToFilters = ListUtil.newArrayList();
            for (SearchFilter nextMeteringDateFilter : nextMeteringDateFilters) {
                validDateToFilters.add(new SearchFilter("validDateTo", nextMeteringDateFilter.operator, nextMeteringDateFilter.value));
            }
            WrapperSearchFilterUtils.convert(validDateToFilters, wrapper, "mcd");
        }
        if(this.hasFilterOnField("meteringNO")){
            String meteringNO = searchFilters.stream().filter(sf -> "meteringNO".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("meteringNO", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.certificate_no", meteringNO);
        }
        if(this.hasFilterOnField("checkPerson")){
            String checkPerson = searchFilters.stream().filter(sf -> "checkPerson".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("checkPerson", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.like("mcd.check_person", checkPerson);
        }
        if(this.hasFilterOnField("existFileFlag")){
            String checkPerson = searchFilters.stream().filter(sf -> "existFileFlag".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("existFileFlag", SearchFilter.Operator.EQ, 0)).value.toString();
            if("0".equals(checkPerson)){
                wrapper.isNotNull("o.cu");
            }else if("1".equals(checkPerson)){
                wrapper.isNull("o.cu");
            }
        }
        if(this.hasFilterOnField("validityMonths")){
            Optional<SearchFilter> first = searchFilters.stream().filter(sf -> "validityMonths".equals(sf.fieldName)).findFirst();
            if (first.isPresent()) {
                wrapper.eq("mcd.validity_months", Integer.valueOf(first.get().value.toString()));
            }

        }
        if(this.hasFilterOnField("measureCycle")){
            String measureCycleVal = searchFilters.stream().filter(sf -> "measureCycle".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("measureCycle", SearchFilter.Operator.EQ, 0)).value.toString();
            wrapper.eq("ai.metering_cycle",Integer.valueOf(measureCycleVal));
        }
        if(this.hasFilterOnField("certificateType")){
            Optional<SearchFilter> first = searchFilters.stream().filter(sf -> "certificateType".equals(sf.fieldName)).findFirst();
            if (first.isPresent()) {
                wrapper.eq("mcd.certificate_type", Integer.valueOf(first.get().value.toString()));
            }

        }
        if(this.hasFilterOnField("measuringType")){
            Optional<SearchFilter> first = searchFilters.stream().filter(sf -> "measuringType".equals(sf.fieldName)).findFirst();
            if (first.isPresent()) {
                wrapper.eq("mcd.type", first.get().value.toString());
            }

        }
        List<String> removeFields = ListUtil.newArrayList("verificationConclusion", "checkDate", "nextMeteringDate", "meteringNO", "checkPerson","existFileFlag","validityMonths","certificateType","measuringType","measureCycle");
        List<SearchFilter> covertSearchFilters = searchFilters.stream().filter(sf -> !removeFields.contains(sf.fieldName)).collect(Collectors.toList());
        WrapperSearchFilterUtils.convert(covertSearchFilters, wrapper, "ai");
        return wrapper;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = currentPageAssets.stream().noneMatch(item -> !selectedMap.containsKey(item.getBoUuid()) || !selectedMap.get(item.getBoUuid()));
        currentPageAssets.forEach(item -> selectedMap.put(item.getBoUuid(), !isSelectedAll));
    }

    @Getter @Setter
    private List<AssetInfo> bathRepairAsset;

    public void setSelectedAssetsUuids() {
        flag=true;
        List<String> selectedBoUuids = selectedMap.keySet().stream().filter(assetUid -> Boolean.TRUE.equals(selectedMap.get(assetUid))).collect(Collectors.toList());
        List<String> assetUidList = currentPageAssets.stream().filter(asset -> selectedBoUuids.contains(asset.getBoUuid())).map(AssetInfo::getUid).collect(Collectors.toList());
        bathRepairAsset = selectedBoUuids.isEmpty() && currentPageAssets==null ? dao.findBySearchFilter(searchFilters) : dao.findByUidIn(assetUidList);
        if (bathRepairAsset.isEmpty()) {
            WebUtil.addErrorMessage("请选择设备,暂不支持报修");
            flag = false;
            return;
        }
        for (String selectedBoUuid : selectedBoUuids) {
            MeasuringCertificateDetail certificateDetail = measuringCertificateDetailDao.findById(selectedBoUuid);
            if (ObjectUtil.isEmpty(certificateDetail) || 2 != certificateDetail.getStatus()) {
                WebUtil.addErrorMessage("有设备检定结论,不符合要求,暂不支持报修");
                flag = false;
                return;
            }
        }
        WorkOrderMgrController.assetInfos = bathRepairAsset;
    }

    public void setSelectedAssetsAndMeasuring(){
        List<String> selectedBoUuids = selectedMap.keySet().stream().filter(assetUid -> Boolean.TRUE.equals(selectedMap.get(assetUid))).collect(Collectors.toList());
        bathRepairAsset = currentPageAssets.stream().filter(asset -> selectedBoUuids.contains(asset.getBoUuid())).collect(Collectors.toList());

    }

    public void updateValidityMonths() {
        List<SearchFilter> searchFilterList = new ArrayList<>();
        List<String> collect = bathRepairAsset.stream().map(AssetInfo::getBoUuid).collect(Collectors.toList());
        searchFilterList.add(new SearchFilter("id", SearchFilter.Operator.IN, collect));
        List<MeasuringCertificateDetail> measuringCertificateDetail = measuringCertificateDetailDao.findBySearchFilter(searchFilterList);
        measuringCertificateDetail.forEach(item->{
            item.setValidityMonths(validityMonths);
        });
        measuringCertificateDetailDao.save(measuringCertificateDetail);
        List<String> assetUids = bathRepairAsset.stream().map(AssetInfo::getUid).collect(Collectors.toList());
        List<AssetInfo> byUidIn = dao.findByUidIn(assetUids);
        for (AssetInfo assetInfo : bathRepairAsset) {
            Date checkDate = assetInfo.getCheckDate();
            Date result = DateUtil.addMonth(checkDate, validityMonths);
            Optional<AssetInfo> first = byUidIn.stream().filter(item -> assetInfo.getUid().equals(item.getUid())).findFirst();
            if (first.isPresent()) {
                first.get().setNextMeteringDate(result);
            }
        }
        dao.save(byUidIn);
        validityMonths = null;
    }

    public boolean judgeMeasurementRepair(){
        WorkflowConfig woc = workflowService.getWorkflowByOrg(currentUser.getSiteUID());
        if (woc != null && woc.getMeasurementRepair() !=null && woc.getMeasurementRepair()) {
            return true;
        }
        return false;
    }

    public List<MeasuringCertificateDetail> getMeasuringResult(){
        List<MeasuringCertificateDetail>  list=new ArrayList<>();
        if(matchType==1 || matchType==2){
            list= measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());

        }else{
            MeasuringCertificateDetail byId = measuringCertificateDetailDao.findById(this.selected.getBoUuid());
            if(byId!=null){
                list.add(byId);
            }
        }
        return list;
    }

    public void onSelectMeasureAsset(){
//        if(matchType==1){
//            measuringCertificateList = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
//        }else{
//            MeasuringCertificateDetail byId = measuringCertificateDetailDao.findById(this.selected.getBoUuid());
//            if(byId!=null){
//                measuringCertificateList=new ArrayList<>();
//                measuringCertificateList.add(byId);
//            }
//        }

        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getUid()
                +"&matchType="+matchType+"&boUuid="+selected.getBoUuid();
        url = "measuringResultList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void uploadMeasuringFile(FileUploadEvent event) {
        String id = event.getComponent().getAttributes().get("value").toString();
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            blobObject.setBoType(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
            blobObject.setBoUuid(id);
            List<V2_BlobObject> hasList = blobDao.findByBoUuidAndObjectName(id, blobObject.getObjectName());
            if (ObjectUtil.isNotEmpty(hasList)) {
                blobDao.delete(hasList);
            }
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
            blobObject.setBoUuid(selected.getUid());
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public List<V2_BlobObject> getMeasuringAttachList(String id) {
        return blobDao.fetchBlobObjectByUuidAndType(id, V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
    }

    public List<V2_BlobObject> getAttachList() {
        List<V2_BlobObject> tempAttachList = new ArrayList<>();
        if(this.selected != null){
            tempAttachList = blobDao.findByBoUuidAndBoSubType(this.selected.getUid(), V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
        }
        return tempAttachList;
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        filterOrg = (OrgInfo) selectedNode.getData();
//        if (deptOrg.getOrgType() == 4) {
//            filterOrg = deptOrg;
//        } else {
//            filterOrg = null;
//        }
    }

    public void onAssetDeptFilter(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        filterAssetDeptOrg = (OrgInfo) selectedNode.getData();
    }

    public void onTabChange(TabChangeEvent event){
        String tabId = event.getTab().getId();
        if ("measuringAssetList".equals(tabId)){
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", "measuringResultMain.xhtml");
        }
        if ("scrappedMeasuringAssetList".equals(tabId)) {
            scrapped = true;
            selected = null;
            measuringCertificateList = null;
            // 报废计量结果
            matchType = 9;
        }
        if ("resultImport".equals(tabId)) {
            MeasureFromExcelController importController = WebUtil.getBean(MeasureFromExcelController.class);
            importController.resetImport();
            RequestContext rc = RequestContext.getCurrentInstance();
            rc.update("measuringTab:importForm");
            rc.update("measuringTab:resultImportWrapper");
        }
    }

    public List<I18nMessage> getMeasureGradeList(){
        List<I18nMessage> msgList = FieldValueMessageController.getFieldValueListByMsgType("measureGrade");
        I18nMessage tempI18m = null;
        for (I18nMessage i18m : msgList) {
            if("4".equals(i18m.getMsgKey())){
                tempI18m = i18m;
            }
        }
        msgList.remove(tempI18m);
        return msgList;
    }

    public void totalMeasureExpireMonths() {
        Wrapper<AssetInfo> wrapper = convert();
        wrapper.isNotNull("ai.next_metering_date");
        wrapper.between("ai.next_metering_date", DateUtil.currentMonthFirstDay(), DateUtil.nextMonthLastDay());
        wrapper.isNotNull("mcd.valid_date_to");
        wrapper.between("mcd.valid_date_to", DateUtil.currentMonthFirstDay(), DateUtil.nextMonthLastDay());
        List<AssetInfo> assetInfoList = assetInfoMapper.selectMeasureResult(wrapper);
        if (ObjectUtil.isNotEmpty(assetInfoList)) {
            Date currentDate = new Date();
            //统计计量到期本月、下月总台数
            for (AssetInfo assetInfoTemp : assetInfoList){
                Date nextMeteringDate = assetInfoTemp.getNextMeteringDate();
                if (ObjectUtil.isEmpty(nextMeteringDate)) {
                    continue;
                }
                if(Boolean.TRUE.equals(DateUtil.isSameMonth(currentDate, nextMeteringDate))){
                    //统计本月总台数
                    currentMonthCount = currentMonthCount + 1;
                }else if(Boolean.TRUE.equals(DateUtil.isSameMonth(DateUtil.addMonth(currentDate,1), nextMeteringDate))){
                    //统计下个月总台数
                    nextMonthCount = nextMonthCount + 1;
                }
            }
            this.searchFilters = new ArrayList<>();
        }
    }

    public Boolean isYunNanFuWai(){
        return Objects.equals(currentTenantInfo.getName().trim(),"云南省阜外心血管病医院");
    }

    public void downloadSelectedFile() {
        List<String> boUuids = currentPageAssets.stream().map(AssetInfo::getBoUuid).collect(Collectors.toList());
        List<V2_BlobObject> blobObjects = blobDao.fetchListByBoTypeAndBoUuIds(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring, boUuids);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(bout)) {
            for (V2_BlobObject blobObject : blobObjects) {
                StreamedContent file = fileDownloadService.getFile(blobObject.getObjectStorageId());
                InputStream stream = file.getStream();
                zipOut.putNextEntry(new ZipEntry(blobObject.getObjectName()));
                byte[] bytes = IOUtils.toByteArray(stream);
                if (bytes != null) {
                    IOUtils.write(bytes, zipOut);
                }
                zipOut.closeEntry();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            WebUtil.addErrorMessage("Fail to download report:" + e.getMessage());
        }
        exportFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "CertificateFiles.zip");
    }

    public boolean canBatchUpload() {
        ConfigItemValue configItemValue = configItemMapper.findByKey("batch_upload", currentUser.getSiteUID());
        return ObjectUtil.isNotEmpty(configItemValue) && Boolean.TRUE.toString().equals(configItemValue.getValue());
    }

    public String getFileNewWindowUrl(String objectId) {
        return "/portal/preview/view.xhtml?faces-redirect=true&amp;objectId=" + objectId;
    }

    public void delete(){
        if(!selectedMap.isEmpty()) {
            List<String> mcdUidList = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(mcdUidList)){
                WebUtil.addErrorMessage("请先勾选设备");
                return;
            }
            mcdUidList.forEach(item->{
                delete(item);
            });
            WebUtil.addSuccessMessage("删除成功");
            selectedMap.clear();
        }else{
            WebUtil.addErrorMessage("请先勾选设备");
        }

    }
    public void delete(String mcdUid){
        if(Objects.isNull(mcdUid)) return;
        MeasuringCertificateDetail measuringCertificateDetail = measuringCertificateDetailDao.findById(mcdUid);
        String uid = measuringCertificateDetail.getAssetUID();
        measuringCertificateDetailDao.delete(mcdUid);
        blobService.deleteBlobFileByBoUuid(mcdUid);
        if(matchType==1){
            AssetInfo assetInfo = dao.findByUid(uid);
            measuringService.updateMeasuringDate(assetInfo);
        }
        this.selected = null;
        SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(currentUser, SystemEventAuditEnum.MEASURE_DELETE, mcdUid, Boolean.TRUE, "","measuring", "");
        systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);

    }



    public void searchAsset(){
        assetInfoList =queryAssetByCondition(assetSearchForm);
    }

    public void resetAssetSearch(){
        assetSearchForm = new BudgetAssetSearchForm();
    }

    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        assetSearchForm.setDept(orgInfo);
        assetSearchForm.setApplicationDeptId(orgInfo.getUid());
        assetSearchForm.setDeptName(orgInfo.getName());
    }


    public List<AssetInfo> queryAssetByCondition( BudgetAssetSearchForm assetSearchForm) {

        List<AssetInfo> assetInfos = new ArrayList<>();
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
//        filters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4));
        filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*"));
        if (StrUtil.isNotEmpty(assetSearchForm.getApplicationDeptId())) {
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, assetSearchForm.getApplicationDeptId()));
        }else{
            List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
            filters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetName())) {
            filters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetBrand())) {
            filters.add(new SearchFilter("makerShortName", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getAssetModel())) {
            filters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getSerialNum())) {
            filters.add(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, assetSearchForm.getSerialNum()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getFinancingNum())) {
            filters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, assetSearchForm.getFinancingNum()));
        }
        assetInfos = dao.findBySearchFilter(filters);
        return assetInfos;
    }



    public void treeSearchCondition(){
        TreeNode treeNodeEdit = null;
        if(StrUtil.isEmpty(assetSearchForm.getDeptName())){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4, assetSearchForm.getDeptName(),true);
        }
        orgTreeSearch = initOrgTree(treeNodeEdit);
    }
    public TreeNode initOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(org.getOrgType() == 4);
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }

    public void saveSelectedAsset(){
        if(selectAsset == null){
            WebUtil.addErrorMessage("请选择设备！");
            return;
        }
        if(selectedMeasuringCertificateDetail==null){
            WebUtil.addErrorMessage("计量数据不存在！");
            return;
        }
        selectedMeasuringCertificateDetail.setAssetUID(selectAsset.getUid());
        selectedMeasuringCertificateDetail.setQrCode(selectAsset.getQrCode());
        selectedMeasuringCertificateDetail.setAssetSN(selectAsset.getSerialNum());
        selectedMeasuringCertificateDetail.setDepartNum(selectAsset.getDepartNum());
        selectedMeasuringCertificateDetail.setFinanceNo(selectAsset.getFinancingNum());
        measuringCertificateDetailDao.save(selectedMeasuringCertificateDetail);
        measuringService.updateMeasuringDate(selectAsset);
    }


    public void matchAsset(String uid) {
        selectedMeasuringCertificateDetail = measuringCertificateDetailDao.findById(uid);
        assetSearchForm = new BudgetAssetSearchForm();
        assetInfoList=new ArrayList<>();

    }

    /**
     * 刷新匹配 - 根据SN号/资产编号/二维码自动匹配未关联台账的计量记录
     */
    public void refreshMatchBySN() {
        try {
            int[] result = measuringService.refreshMatchUnpairedRecords();
            int matched = result[0];
            int failed = result[1];
            if (matched > 0) {
                WebUtil.addSuccessMessage("匹配完成：成功匹配 " + matched + " 条" + (failed > 0 ? "，剩余 " + failed + " 条未匹配到" : ""));
            } else {
                WebUtil.addWarnMessage("匹配完成：没有匹配到任何台账记录");
            }
            // 刷新列表
            initMatchResult(matchType);
        } catch (Exception e) {
            logger.error("刷新匹配异常", e);
            WebUtil.addErrorMessage("刷新匹配失败：" + e.getMessage());
        }
    }


    public void exportExcel()  {
        try {
            Set<String> uids = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
            byte[] stream =systemTemplateService.getTemplateFileByte(SystemTemplateEnum.MEASURING_RESULT_EXPORT,currentUser);
            List<MeasuringCertificateDetail> list=new ArrayList<>();
            if(CollectionUtils.isEmpty(uids)){
                Wrapper<AssetInfo> wrapper = measureUnpairedResultConvert();
                list=  measureAssetMapper.selectMeasureUnpairedResult(wrapper);

            }else{
                List<SearchFilter> filters = new ArrayList<>();
                filters.add(new SearchFilter("id", SearchFilter.Operator.IN, uids));
                list= measuringCertificateDetailDao.findBySearchFilter(filters);
            }
            List<I18nMessage> messages = i18nMessageService.getByMsgType("measuringStatus");

            Map<String, String> measuringStatusMap  = messages.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, I18nMessage::getValueZh, (value1, value2) -> value1));

            // 设置检定结论名称和上传人姓名
            list.forEach(item->{
                item.setStatusName(measuringStatusMap.get(item.getStatus().toString()));
                // 根据 created_by 查询上传人姓名
                if (item.getCreatedBy() != null) {
                    UserAccount uploadUser = userAccountDao.findByLoginName(item.getCreatedBy());
                    if (uploadUser != null) {
                        item.setUploadUserName(uploadUser.getName());
                    }
                }
            });

            byte[] bytes = EasyExcelExportUtil.exportExcel(stream, list, new HashMap<>());

            excelFile = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/vnd.ms-excel", "无台账计量数据.xlsx");


        }catch (Exception e){
            logger.error("导出无台账计量数据异常 e:",e);
        }

    }

    public void postProcessorExcel(Object obj){
        SystemEventAuditEnum eventAuditEnum = SystemEventAuditEnum.TABLE_DOWNLOAD_MEASURE;
        String eventData = "";
        if(scrapped){
            eventData = "计量结果【报废】页面表格";
        }else {
            eventData = "计量结果【所有】页面表格";
        }
        measuringService.postProcessorExcel(eventAuditEnum, eventData);
    }


    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }

    public OrgInfo getOrgInfoByUid(String uid) {
        return orgDao.getByUid(uid);
    }

}
