package com.ge.apm.view.measuring;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.ConfigItemMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.WrapperSearchFilterUtils;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.preview.PreViewerBean;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.wo.WorkOrderMgrController;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
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
        WorkflowConfig woc = workflowService.getWorkflowByOrg(currentUser.getSiteUID());
        measurementRepairVisible = woc != null && woc.getMeasurementRepair() !=null && woc.getMeasurementRepair();
        String enCodeParameters = WebUtil.getRequestParameter("str");
        queryAssetUID = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
        this.selectedMap = MapUtil.newHashMap();
        this.searchFilters = new ArrayList<>();
        totalMeasureExpireMonths();
        currentTenantInfo = ccService.getTenantByUid(UserContextService.getCurrentUserAccount().getTenantUID());
    }

    @Override
    protected AssetInfoRepository getDAO(){
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        Wrapper<AssetInfo> wrapper = convert();
        Sort sort = pageRequest.getSort();
        for (Sort.Order order : sort) {
            Sort.Direction direction = order.getDirection();
            String property = ZipUtil.camelToUnderline(order.getProperty());
            String prefix = "check_date".equals(property) ? "mcd." : "ai.";
            wrapper.orderBy(prefix + property, "ASC".equals(direction.name()));
        }
        wrapper.orderBy("mcd.check_date", false);
        currentPageAssets = assetInfoMapper.selectMeasureResult(pagination, wrapper);
        return new PageImpl<>(currentPageAssets, pageRequest, pagination.getTotal());
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
        if(this.hasFilterOnField("verificationConclusion")){
            Integer verificationConclusion = Integer.valueOf(searchFilters.stream().filter(sf -> "verificationConclusion".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("verificationConclusion", SearchFilter.Operator.EQ, 0)).value.toString());
            searchFilters.removeIf(e -> "verificationConclusion".equals(e.fieldName));
            wrapper.eq("mcd.status", verificationConclusion);
        }
        if(this.hasFilterOnField("measuringItemCategory")){
            Integer measuringItemCategory = Integer.valueOf(searchFilters.stream().filter(sf -> "measuringItemCategory".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("measuringItemCategory", SearchFilter.Operator.EQ, 0)).value.toString());
            searchFilters.removeIf(e -> "measuringItemCategory".equals(e.fieldName));
            wrapper.eq("ai.measuring_item_category", measuringItemCategory);
        }
        if(this.hasFilterOnField("assetGroup")){
            Integer assetGroup = Integer.valueOf(searchFilters.stream().filter(sf -> "assetGroup".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, 0)).value.toString());
            searchFilters.removeIf(e -> "assetGroup".equals(e.fieldName));
            wrapper.eq("ai.asset_group", assetGroup);
        }
        if(this.hasFilterOnField("checkDate")){
            List<SearchFilter> checkDateFilters = searchFilters.stream().filter(sf -> "checkDate".equals(sf.fieldName)).collect(Collectors.toList());
            searchFilters.removeIf(e -> "checkDate".equals(e.fieldName));
            WrapperSearchFilterUtils.convert(checkDateFilters, wrapper, "mcd");
        }
        if(this.hasFilterOnField("nextMeteringDate")){
            List<SearchFilter> nextMeteringDateFilters = searchFilters.stream().filter(sf -> "nextMeteringDate".equals(sf.fieldName)).collect(Collectors.toList());
            List<SearchFilter> validDateToFilters = ListUtil.newArrayList();
            for (SearchFilter nextMeteringDateFilter : nextMeteringDateFilters) {
                validDateToFilters.add(new SearchFilter("validDateTo", nextMeteringDateFilter.operator, nextMeteringDateFilter.value));
            }
            searchFilters.removeIf(e -> "nextMeteringDate".equals(e.fieldName));
            WrapperSearchFilterUtils.convert(validDateToFilters, wrapper, "mcd");
        }
        if(this.hasFilterOnField("meteringNO")){
            String meteringNO = searchFilters.stream().filter(sf -> "meteringNO".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("meteringNO", SearchFilter.Operator.EQ, 0)).value.toString();
            searchFilters.removeIf(e -> "meteringNO".equals(e.fieldName));
            wrapper.like("mcd.certificate_no", meteringNO);
        }

        WrapperSearchFilterUtils.convert(searchFilters, wrapper, "ai");
        return wrapper;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = currentPageAssets.stream().noneMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        currentPageAssets.forEach(item -> selectedMap.put(item.getUid(), !isSelectedAll));
    }

    public void setSelectedAssetsUuids() {
        flag=true;
        List<String> selectedAssetUuids = selectedMap.keySet().stream().filter(assetUid -> Boolean.TRUE.equals(selectedMap.get(assetUid))).collect(Collectors.toList());
        currentPageAssets = selectedAssetUuids.isEmpty() && currentPageAssets==null ? dao.findBySearchFilter(searchFilters) : dao.findByUidIn(selectedAssetUuids);
        if (currentPageAssets.isEmpty()) {
            WebUtil.addErrorMessage("请选择设备,暂不支持报修");
            flag = false;
            return;
        }
        for (AssetInfo assetInfo: currentPageAssets) {
            List<MeasuringCertificateDetail>  certificateDetails = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(assetInfo.getUid());
            if (ObjectUtil.isEmpty(certificateDetails) || 2 == certificateDetails.get(0).getStatus()) {
                WebUtil.addErrorMessage("有设备检定结论,不符合要求,暂不支持报修");
                flag = false;
                return;
            }
        }
        WorkOrderMgrController.assetInfos = currentPageAssets;
    }

    public List<MeasuringCertificateDetail> getMeasuringResult(){
        return measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
    }

    public void onSelectMeasureAsset(){
        measuringCertificateList = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
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
        OrgInfo deptOrg = (OrgInfo) selectedNode.getData();
        if (deptOrg.getOrgType() == 4) {
            filterOrg = deptOrg;
        } else {
            filterOrg = null;
        }
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
        String uid = this.selected.getUid();
        String boUuid = this.selected.getBoUuid();
        if(Objects.isNull(boUuid)) return;
        measuringCertificateDetailDao.delete(boUuid);
        blobService.deleteBlobFileByBoUuid(boUuid);
        WebUtil.addSuccessMessage("删除成功");
        AssetInfo assetInfo = dao.findByUid(uid);
        measuringService.updateMeasuringDate(assetInfo);
        this.selected = null;
        SystemEventAudit systemEventAudit = systemEventAuditAopAspect.buildEventAudit(currentUser, SystemEventAuditEnum.MEASURE_DELETE, boUuid, Boolean.TRUE, "","measuring", "");
        systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);

    }

}
