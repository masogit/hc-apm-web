package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.AssetAnnualInspectionReportRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.SpecialAssetInfoRepository;
import com.ge.apm.dao.mapper.AssetAnnualInspectionReportMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetAnnualInspectionReportService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.component.FilterSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import org.primefaces.context.RequestContext;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.springframework.util.StringUtils;
@ManagedBean
@ViewScoped
public class AssetAnnualInspectionReportController extends GenericCRUDUUIDController<AssetAnnualInspectionReport> {

    private AssetAnnualInspectionReportRepository dao;
    private SpecialAssetInfoRepository specialAssetInfoRepository;
    private AssetAnnualInspectionReportMapper assetAnnualInspectionReportMapper;
    private AssetAnnualInspectionReportService assetAnnualInspectionReportService;
    private FilterSiteOrgTreeComponentController filterSiteOrgTreeComponentController;
    private UserAuth customRoleAuth;
    private Map<String, Boolean> selectedMap;
    private UserAccount currentUser;
    private OrgInfo selectedOrg;
    private UploadedFile file;
    private BlobFileService blobService;
    private BlobObjectRepository blobDao;

    /** 校验后的全部数据列表 */
    private List<AssetAnnualInspectionReport> displayList;
    /** 校验失败的数据列表 */
    private List<AssetAnnualInspectionReport> displayErrorList;
    /** 是否可以导入（全部校验通过时为true） */
    private Boolean canbeImported = false;
    /** 是否已执行导入 */
    private Boolean isImported = false;
    /** 是否仅显示错误记录 */
    private boolean displayErrorsOnly = false;
    /** 详情页设备信息 */
    private AssetInfo detailAssetInfo;
    /** 详情页年检报告列表 */
    private List<AssetAnnualInspectionReport> detailReportList;
    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetAnnualInspectionReportRepository.class);
        specialAssetInfoRepository = WebUtil.getBean(SpecialAssetInfoRepository.class);
        assetAnnualInspectionReportMapper = WebUtil.getBean(AssetAnnualInspectionReportMapper.class);
        assetAnnualInspectionReportService = WebUtil.getBean(AssetAnnualInspectionReportService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        filterSiteOrgTreeComponentController = WebUtil.getBean(FilterSiteOrgTreeComponentController.class);
        filterSiteOrgTreeComponentController.registerController(this);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth = UserContextService.getCustomRoleAuth(currentUser.getId(), "SPECIALASSET_CHECK");
        selectedMap = new HashMap<>();
        this.searchFilters = new ArrayList<>();
    }

    @Override
    protected GenericRepositoryUUID<AssetAnnualInspectionReport> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetAnnualInspectionReport> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        Wrapper<AssetAnnualInspectionReport> wrapper = buildWrapper();
        List<AssetAnnualInspectionReport> list = assetAnnualInspectionReportMapper.selectAnnualInspectionReportList(pagination, wrapper);
        return new PageImpl<>(list, pageRequest, pagination.getTotal());
    }

    private Wrapper<AssetAnnualInspectionReport> buildWrapper() {
        Wrapper<AssetAnnualInspectionReport> wrapper = new EntityWrapper<>();
        wrapper.eq("ai.tenant_uid", currentUser.getTenantUID());
        if (!UserContextService.isMVSRole()) {
            List<String> orgUidList = Collections.emptyList();
            try {
                orgUidList = WebUtil.getBean(UaaService.class)
                        .getOrgListByUser(currentUser.getUid()).stream()
                        .map(OrgInfo::getUid)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
            }
            wrapper.andNew();
            wrapper.in("ai.institution_uid", orgUidList);
            wrapper.or().in("ai.hospital_uid", orgUidList);
            wrapper.or().in("ai.site_uid", orgUidList);
            wrapper.or().in("ai.clinical_dept_uid", orgUidList);
        }
        if (searchFilters != null) {
            for (SearchFilter filter : searchFilters) {
                switch (filter.fieldName){
                    case "assetName":
                        wrapper.like("ai.name", filter.value.toString());
                        break;
                    case "assetSerialNum":
                        wrapper.like("ai.serial_num", filter.value.toString());
                        break;
                    case "assetQrCode":
                        wrapper.like("ai.qr_code", filter.value.toString());
                        break;
                    case "clinicalDeptName":
                        wrapper.like("ai.clinical_dept_name", filter.value.toString());
                        break;
                    case "name":
                        wrapper.like("aair.name", filter.value.toString());
                        break;
                    case "reportType":
                        wrapper.like("aair.report_type", filter.value.toString());
                        break;
                    case "testingUnit":
                        wrapper.like("aair.testing_unit", filter.value.toString());
                        break;
                    case "testResults":
                        wrapper.like("aair.test_results", filter.value.toString());
                        break;
                    case "certificateIssuingUnit":
                        wrapper.like("aair.certificate_issuing_unit", filter.value.toString());
                        break;
                    case "effectiveTime":
                        SearchFilter.Operator operator = filter.operator;
                        if (SearchFilter.Operator.GTE.equals(operator)) {
                            wrapper.ge("aair.effective_time", filter.value);
                        }else{
                            wrapper.le("aair.effective_time", filter.value);
                        }
                        break;
                    case "expiryDate":
                        SearchFilter.Operator operator1 = filter.operator;
                        if (SearchFilter.Operator.GTE.equals(operator1)) {
                            wrapper.ge("aair.expiry_date", filter.value);
                        }else{
                            wrapper.le("aair.expiry_date", filter.value);
                        }
                        break;
                    case "createdByName":
                        wrapper.like("aair.created_by_name", filter.value.toString());
                        break;
                    case "isUpdate":
                        String string = filter.value.toString();
                        if(Boolean.TRUE.toString().equals(string)){
                            wrapper.isNotNull("bo.id");
                        }else{
                            wrapper.isNull("bo.id");
                        }
                        break;
                }

            }
        }
        wrapper.orderBy("aair.effective_time", false);
        return wrapper;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public SpecialAssetInfo getSpecialAssetInfo(String specialAssetUid) {
        if (specialAssetUid == null) return null;
        return specialAssetInfoRepository.findById(specialAssetUid);
    }

    public AssetInfo getAssetInfo(String specialAssetUid) {
        SpecialAssetInfo specialAssetInfo = getSpecialAssetInfo(specialAssetUid);
        if (specialAssetInfo != null) {
            return specialAssetInfo.getAssetInfo();
        }
        return null;
    }

    public String getAssetInfoName(String specialAssetUid) {
        AssetInfo assetInfo = getAssetInfo(specialAssetUid);
        return assetInfo != null ? assetInfo.getName() : "";
    }

    public String getAssetInfoSerialNum(String specialAssetUid) {
        AssetInfo assetInfo = getAssetInfo(specialAssetUid);
        return assetInfo != null ? assetInfo.getSerialNum() : "";
    }

    public String getAssetInfoQrCode(String specialAssetUid) {
        AssetInfo assetInfo = getAssetInfo(specialAssetUid);
        return assetInfo != null ? assetInfo.getQrCode() : "";
    }

    public String getAssetInfoClinicalDeptName(String specialAssetUid) {
        AssetInfo assetInfo = getAssetInfo(specialAssetUid);
        return assetInfo != null ? assetInfo.getClinicalDeptName() : "";
    }

    public void onSelectAllOfPage() {
        if (selectedMap == null || lazyModel == null) return;
        java.util.List<AssetAnnualInspectionReport> currentPageItems = lazyModel.getWrappedData();
        if (currentPageItems == null) return;
        boolean isSelectedAll = currentPageItems.stream().noneMatch(i -> !selectedMap.containsKey(i.getUid()) || !selectedMap.get(i.getUid()));
        for (AssetAnnualInspectionReport i : currentPageItems) {
            selectedMap.put(i.getUid(), !isSelectedAll);
        }
    }

    public void deleteSelected() {
        if (selectedMap == null || selectedMap.isEmpty()) {
            WebUtil.addErrorMessage("请先选择要删除的记录");
            return;
        }
        java.util.List<String> idsToDelete = new java.util.ArrayList<>();
        for (Map.Entry<String, Boolean> entry : selectedMap.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                idsToDelete.add(entry.getKey());
            }
        }
        if (idsToDelete.isEmpty()) {
            WebUtil.addErrorMessage("请先选择要删除的记录");
            return;
        }
        for (String uid : idsToDelete) {
            AssetAnnualInspectionReport report = dao.findById(uid);
            if (report != null) {
                dao.delete(report);
            }
        }
        selectedMap.clear();
        WebUtil.addSuccessMessage("删除成功");
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (ObjectUtil.isEmpty(selectedOrg)) {
            WebUtil.addErrorMessage("所属院区必填！");
            return;
        }
        this.file = event.getFile();
        this.isImported = false;
        this.canbeImported = false;
        this.displayErrorsOnly = false;
        try {
            displayList = assetAnnualInspectionReportService.validateExcel(file, selectedOrg);
        } catch (Exception e) {
            displayList = new ArrayList<>();
            logger.error("文件解析失败：" + e.getMessage(), e);
            WebUtil.addErrorMessage("文件解析失败：" + e.getMessage());
            return;
        }
        if (CollectionUtils.isEmpty(displayList)) {
            WebUtil.addErrorMessage("Excel中没有有效数据行");
            return;
        }
        filterErrorRecords();
        boolean hasError = false;
        for (AssetAnnualInspectionReport report : displayList) {
            if (AssetAnnualInspectionReportService.IMPORT_STATUS_CHECK_FAIL.equals(report.getImportStatus())) {
                hasError = true;
                break;
            }
        }
        canbeImported = !hasError;
        if (canbeImported) {
            WebUtil.addSuccessMessage("校验完成", "共 " + displayList.size() + " 条数据全部校验通过，请点击【确认导入】按钮完成导入。");
        } else {
            int failCount = displayErrorList != null ? displayErrorList.size() : 0;
            WebUtil.addWarnMessage("校验完成", "共 " + displayList.size() + " 条数据，其中 " + failCount + " 条校验失败，请修正后重新上传。");
        }
    }

    /**
     * 确认导入 - 实际保存数据库
     */
    public void importData() {
        if (!canbeImported || CollectionUtils.isEmpty(displayList)) {
            WebUtil.addErrorMessage("数据校验未通过，无法导入");
            return;
        }
        try {
            List<AssetAnnualInspectionReport> failedList = assetAnnualInspectionReportService.importValidatedData(displayList, currentUser.getUid());
            isImported = true;
            canbeImported = false;
            filterErrorRecords();
            if (CollectionUtils.isEmpty(failedList)) {
                WebUtil.addSuccessMessage("导入成功", "共成功导入 " + displayList.size() + " 条年检报告数据。");
            } else {
                int successCount = displayList.size() - failedList.size();
                WebUtil.addWarnMessage("导入完成", "成功 " + successCount + " 条，失败 " + failedList.size() + " 条。");
            }
        } catch (Exception e) {
            isImported = true;
            canbeImported = false;
            WebUtil.addErrorMessage("导入失败：" + e.getMessage());
        }
    }

    /**
     * 重置导入状态，清空所有校验数据和文件
     */
    public void resetImport() {
        this.file = null;
        this.displayList = null;
        this.displayErrorList = null;
        this.canbeImported = false;
        this.isImported = false;
        this.displayErrorsOnly = false;
    }

    /**
     * 切换显示全部/仅错误
     */
    public void switchDisplayMode() {
        filterErrorRecords();
    }

    private void filterErrorRecords() {
        if (CollectionUtils.isEmpty(displayList)) {
            displayErrorList = new ArrayList<>();
            return;
        }
        List<AssetAnnualInspectionReport> errors = new ArrayList<>();
        for (AssetAnnualInspectionReport report : displayList) {
            String st = report.getImportStatus();
            if (AssetAnnualInspectionReportService.IMPORT_STATUS_CHECK_FAIL.equals(st) || AssetAnnualInspectionReportService.IMPORT_STATUS_FAILURE.equals(st)) {
                errors.add(report);
            }
        }
        displayErrorList = errors;
    }

    public void handleZipFileUpload(FileUploadEvent event) throws IOException {
        displayList = new ArrayList<>();
        displayList = assetAnnualInspectionReportService.handleZip(event.getFile(), currentUser);
        WebUtil.addSuccessMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<AssetAnnualInspectionReport> getDisplayList() {
        if (displayErrorsOnly) {
            return displayErrorList;
        }
        return displayList;
    }

    public void setDisplayList(List<AssetAnnualInspectionReport> displayList) {
        this.displayList = displayList;
    }

    public Boolean getCanbeImported() {
        return canbeImported;
    }

    public void setCanbeImported(Boolean canbeImported) {
        this.canbeImported = canbeImported;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public void setIsImported(Boolean isImported) {
        this.isImported = isImported;
    }

    public boolean isDisplayErrorsOnly() {
        return displayErrorsOnly;
    }

    public void setDisplayErrorsOnly(boolean displayErrorsOnly) {
        this.displayErrorsOnly = displayErrorsOnly;
    }

    public boolean isHasData() {
        return displayList != null && !displayList.isEmpty();
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public AssetInfo getDetailAssetInfo() {
        return detailAssetInfo;
    }

    public void setDetailAssetInfo(AssetInfo detailAssetInfo) {
        this.detailAssetInfo = detailAssetInfo;
    }

    public List<AssetAnnualInspectionReport> getDetailReportList() {
        return detailReportList;
    }

    public void setDetailReportList(List<AssetAnnualInspectionReport> detailReportList) {
        this.detailReportList = detailReportList;
    }

    public void onSelectRow(org.primefaces.event.SelectEvent event) {
        AssetAnnualInspectionReport selectedReport = (AssetAnnualInspectionReport) event.getObject();
        if (selectedReport == null) {
            return;
        }
        String specialAssetUid = selectedReport.getSpecialAssetUid();
        if (specialAssetUid == null) {
            return;
        }
        String url = "specialAssetUid=" + specialAssetUid;
        url = "specialAssetReportDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void initDetailData() {
        String encodeStr = WebUtil.getRequestParameter("str");
        if (!StringUtils.hasText(encodeStr)) {
            return;
        }
        String specialAssetUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "specialAssetUid");
        if (specialAssetUid == null) {
            return;
        }

        SpecialAssetInfo specialAssetInfo = specialAssetInfoRepository.findById(specialAssetUid);
        if (specialAssetInfo != null) {
            detailAssetInfo = specialAssetInfo.getAssetInfo();
        }

        Wrapper<AssetAnnualInspectionReport> wrapper = new EntityWrapper<>();
        wrapper.eq("sai.id", specialAssetUid);
        detailReportList = assetAnnualInspectionReportMapper.selectAnnualInspectionReportList(new Pagination(1, Integer.MAX_VALUE), wrapper);
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        extracted();
    }

    private void extracted() {
        AssetAnnualInspectionReport assetAnnualInspectionReport = detailReportList.get(0);
        String specialAssetUid = assetAnnualInspectionReport.getSpecialAssetUid();
        Wrapper<AssetAnnualInspectionReport> wrapper = new EntityWrapper<>();
        wrapper.eq("sai.id", specialAssetUid);
        detailReportList = assetAnnualInspectionReportMapper.selectAnnualInspectionReportList(new Pagination(1, Integer.MAX_VALUE), wrapper);
    }

    public void uploadAnnualInspectionFile(FileUploadEvent event) {
        String uid = event.getComponent().getAttributes().get("value").toString();
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            blobObject.setBoType(V2_BlobObject.BO_SUB_FILE);
            blobObject.setBoUuid(uid);
            List<V2_BlobObject> hasList = blobDao.findByBoUuidAndObjectName(uid, blobObject.getObjectName());
            if (ObjectUtil.isNotEmpty(hasList)) {
                blobDao.delete(hasList);
            }
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
        extracted();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selectedOrg = (OrgInfo) eventObject;
    }
}
