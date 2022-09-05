package com.ge.apm.view.measuring;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.WrapperSearchFilterUtils;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.wo.WorkOrderMgrController;
import com.google.common.collect.Lists;
import lombok.Data;
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
 * Created by 212605082 on 2018/6/4.
 */
@ManagedBean
@ViewScoped
@Data
public class MeasureAssetListController extends JpaCRUDController<AssetInfo> {

    private UserAccount currentUser;

    private AssetInfoRepository dao;
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;
    private WorkflowService workflowService;
    private I18nMessageRepository i18nMessageDao;
    private AssetInfoMapper assetInfoMapper;
    private FileDownloadService fileDownloadService;

    private List<MeasuringCertificateDetail> measuringCertificateList;

    private BlobFileService blobService;
    private BlobObjectRepository blobDao;
    private TenantInfoRepository tenantInfoDao;

    private UaaService uaaService;

    private List<V2_BlobObject> attachements;

    private OrgInfo fileterOrg;

    private TreeNode selectedNode;

    private OrgInfoTreeService orgInfoTreeService;
    private Integer verificationConclusion =null;
    private int currentMonthCount = 0;
    private int nextMonthCount = 0;
    public List<AssetInfo> currentPageAssets;
    //复选的设备
    private Map<String, Boolean> selectedMap;
    private Boolean flag=true;
    private UserAuth customRoleAuth;
    private Boolean measurementRepairVisible;
    private List<V2_BlobObject> resultAttachList;
    private StreamedContent exportFile;
    @Override
    protected void init() {

        dao = WebUtil.getBean(AssetInfoRepository.class);
        measuringCertificateDetailDao = WebUtil.getBean(MeasuringCertificateDetailRepository.class);
        i18nMessageDao = WebUtil.getBean(I18nMessageRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        WorkflowConfig woc = workflowService.getWorkflowByOrg(currentUser.getSiteUID());
        measurementRepairVisible = woc != null && woc.getMeasurementRepair() !=null && woc.getMeasurementRepair();

        attachements = new ArrayList<>();
        this.selectedMap = new HashMap();
        totalMeasureExpireMonths();
    }

    @Override
    protected AssetInfoRepository getDAO(){
        return dao;
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber(), pageRequest.getPageSize());
        Wrapper<AssetInfo> wrapper = convert();
        Sort sort = pageRequest.getSort();
        for (Sort.Order order : sort) {
            Sort.Direction direction = order.getDirection();
            String property = ZipUtil.camelToUnderline(order.getProperty());
            String prefix = "check_date".equals(property) ? "mcd." : "ai.";
            wrapper.orderBy(prefix + property, "ASC".equals(direction.name()));
        }
        currentPageAssets = assetInfoMapper.selectMeasureResultPage(pagination, wrapper);
        return new PageImpl<>(currentPageAssets, pageRequest, pagination.getTotal());
    }

    private Wrapper<AssetInfo> convert() {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        Wrapper<AssetInfo> wrapper = new EntityWrapper<>();
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
        if(!this.hasFilterOnField("status")){
            wrapper.ne("ai.status", 4);
        }
        if(!this.hasFilterOnField("isValid")){
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
        if(null != fileterOrg){
            if (selectedNode.isLeaf()) {
                clinicalUidList = Lists.newArrayList(fileterOrg.getUid());
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
            Integer verificationConclusion = Integer.valueOf(searchFilters.stream().filter(sf -> "measuringItemCategory".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("measuringItemCategory", SearchFilter.Operator.EQ, 0)).value.toString());
            searchFilters.removeIf(e -> "measuringItemCategory".equals(e.fieldName));
            wrapper.eq("ai.measuring_item_category", verificationConclusion);
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

        WrapperSearchFilterUtils.convert(searchFilters, wrapper, "ai");
        return wrapper;
    }

    private List<AssetInfo> pageNum(List<AssetInfo> assetInfoList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = assetInfoList.size();
        if(size > numEnd){
            assetInfoList = assetInfoList.subList(numStart ,numEnd);
        }else {
            assetInfoList = assetInfoList.subList(numStart ,size);
        }
        return assetInfoList;
    }
    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public void setSelectedAssetsUids() {
        flag=true;
        List<String> selectedAssetUids = new ArrayList<>();
        selectedMap.keySet().stream().forEach(assetUid -> { if(selectedMap.get(assetUid)) selectedAssetUids.add(assetUid); });
        if (selectedAssetUids.isEmpty()) {
            if (currentPageAssets==null){
                currentPageAssets = dao.findBySearchFilter(searchFilters);
            }
        } else {
            currentPageAssets = dao.findByUidIn(selectedAssetUids);
        }
        if (currentPageAssets!=null && !currentPageAssets.isEmpty()){
            for (AssetInfo assetInfo: currentPageAssets){
                List<MeasuringCertificateDetail>  measuringCertificateDetails = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(assetInfo.getUid());
                if (measuringCertificateDetails!=null && !measuringCertificateDetails.isEmpty() && 2==measuringCertificateDetails.get(0).getStatus()) {
                }else {
                    WebUtil.addErrorMessage("有设备检定结论,不符合要求,暂不支持报修");
                    flag=false;
                    return;
                }
            }
        }else {
            WebUtil.addErrorMessage("请选择设备,暂不支持报修");
            flag=false;
            return;
        }
        WorkOrderMgrController.assetInfos = currentPageAssets;

    }

    public List<MeasuringCertificateDetail> getMeasuringResult(){
        return measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
//        List<String> ids = measuringCertificateList.stream().map(MeasuringCertificateDetail::getId).collect(Collectors.toList());
//        List<V2_BlobObject> measuringAttachList = blobDao.fetchListByBoTypeAndBoUuIds(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring, ids);
//        Map<String, List<V2_BlobObject>> measuringAttachMap = measuringAttachList.stream().collect(Collectors.groupingBy(V2_BlobObject::getBoUuid));
//        measuringCertificateList.forEach(detail -> detail.setMeasuringAttachList(ObjectUtil.isNotEmpty(measuringAttachMap.get(detail.getId())) ? measuringAttachMap.get(detail.getId()) : Lists.newArrayList()));
    }

    public void onSelectMeasureAsset(){
        measuringCertificateList = measuringCertificateDetailDao.findByAssetUIDOrderByCheckDateDesc(this.selected.getUid());
//        List<String> ids = measuringCertificateList.stream().map(MeasuringCertificateDetail::getId).collect(Collectors.toList());
//        List<V2_BlobObject> measuringAttachList = blobDao.fetchListByBoTypeAndBoUuIds(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring, ids);
//        Map<String, List<V2_BlobObject>> measuringAttachMap = measuringAttachList.stream().collect(Collectors.groupingBy(V2_BlobObject::getBoUuid));
//        measuringCertificateList.forEach(detail -> detail.setMeasuringAttachList(ObjectUtil.isNotEmpty(measuringAttachMap.get(detail.getId())) ? measuringAttachMap.get(detail.getId()) : Lists.newArrayList()));
    }
    public boolean judgeMeasurementRepair(){
        WorkflowConfig woc = workflowService.getWorkflowByOrg(currentUser.getSiteUID());
        return woc != null && woc.getMeasurementRepair() != null && woc.getMeasurementRepair();
    }

    public void uploadMeasuringFile(FileUploadEvent event) {
        String id = event.getComponent().getAttributes().get("value").toString();
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
            blobObject.setBoUuid(id);
            blobDao.save(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
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

//    public void getMeasuringAttachList(String id) {
//        resultAttachList = blobDao.fetchBlobObjectByUuidAndType(id, V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
//    }

    public List<V2_BlobObject> getAttachList() {
        List<V2_BlobObject> tempAttachList = new ArrayList<>();
        if(this.selected != null){
            tempAttachList = blobDao.findByBoUuidAndBoSubType(this.selected.getUid(), V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring);
        }

        return tempAttachList;
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {

        selectedNode = event.getTreeNode();
        OrgInfo deptOrg = (OrgInfo) selectedNode.getData();
        if (deptOrg.getOrgType() == 4) {
            fileterOrg = deptOrg;
        } else {
            fileterOrg = null;
        }
    }

    public void onTabChange(TabChangeEvent event){
        String tabId = event.getTab().getId();

        if("measuringAssetList".equals(tabId)){
            //this.searchFilters = new ArrayList<>();
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", "measuringResultMain.xhtml");
        }
    }

    public List<I18nMessage> getMeasureGradeList(){
//        List<I18nMessage> msgList = i18nMessageDao.getByMsgType("measureGrade");
        List<I18nMessage> msgList = FieldValueMessageController.getFieldValueListByMsgType("measureGrade");

        I18nMessage tempI18m = null;
        for (I18nMessage i18m : msgList) {
            if(i18m.getMsgKey().equals("4")){
                tempI18m = i18m;
            }
        }

        msgList.remove(tempI18m);

        return msgList;
    }

    public List<MeasuringCertificateDetail> getMeasuringCertificateList() {
        return measuringCertificateList;
    }

    public void setMeasuringCertificateList(List<MeasuringCertificateDetail> measuringCertificateList) {
        this.measuringCertificateList = measuringCertificateList;
    }

    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<V2_BlobObject> attachements) {
        this.attachements = attachements;
    }

    public OrgInfo getFileterOrg() {
        return fileterOrg;
    }

    public void setFileterOrg(OrgInfo fileterOrg) {
        this.fileterOrg = fileterOrg;
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    public void totalMeasureExpireMonths() {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
            this.searchFilters = searchFilters;
        }

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if(!this.hasFilterOnField("status")){
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 4, "1*"));
        }

        if(!this.hasFilterOnField("isValid")){
            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE, "1*"));
        }

        if(!this.hasFilterOnField("isDeleted")){
            searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE, "1*"));
        }

        if(!this.hasFilterOnField("measureGrade")){
            searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.NE, 4, "1*3+"));
            searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.IsNull, null, "1*3+"));
        }else{
            boolean hasNullMeasureGrade = false;
            Integer measureGradeValue = null;
            for (SearchFilter sf : this.searchFilters) {
                //判断是否有计量等级的过滤条件
                if("measureGrade".equals(sf.fieldName)){
                    if(sf.value == null){
                        hasNullMeasureGrade = true;
                    }else {
                        measureGradeValue = Integer.valueOf(sf.value.toString());
                    }
                }
            }

            //判断计量等级过滤条件是否为空
            if(hasNullMeasureGrade){
                this.removeFilterOnField("measureGrade");
                searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.NE, 4, "1*3+"));
                searchFilters.add(new SearchFilter("measureGrade", SearchFilter.Operator.IsNull, null, "1*3+"));
            }else {
                this.updateSearchFilter(new SearchFilter("measureGrade", SearchFilter.Operator.EQ, measureGradeValue, "1*"));
            }
        }

        if(null != fileterOrg){
            if (selectedNode.isLeaf()) {
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterOrg.getUid(), "1*"));
            } else {
                List<OrgInfo> allClinicals = orgInfoTreeService.collectChildClinicalDeptByParent(selectedNode);
                List<String> clinicalUids = allClinicals.stream().map(OrgInfo::getUid).collect(Collectors.toList());
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalUids, "1*"));
            }

        }else{
            if(this.hasFilterOnField("clinicalDeptUID")){
                searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
        }

        searchFilters.add(new SearchFilter("nextMeteringDate", SearchFilter.Operator.IsNotNull, null, "1*"));

        List<AssetInfo> assetInfoList = dao.findBySearchFilter(searchFilters);
        if(assetInfoList == null || assetInfoList.isEmpty()){
            return;
        }

        Date currentDate = new Date();
        Map<String,Integer> totalMonthMap = new HashMap<>();
        //统计计量到期本月、下月总台数
        for(AssetInfo assetInfoTemp:assetInfoList){
            Date nextMeteringDate = assetInfoTemp.getNextMeteringDate();
            if(DateUtil.isSameMonth(currentDate,nextMeteringDate)){
                currentMonthCount = currentMonthCount + 1;//统计本月总台数
            }else if(DateUtil.isSameMonth(DateUtil.addMonth(currentDate,1),nextMeteringDate)){
                nextMonthCount = nextMonthCount + 1;//统计下个月总台数
            }
        }

        this.searchFilters.removeAll(searchFilters);
    }
    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public Boolean isYunNanFuWai(){
        TenantInfo tenantInfo = tenantInfoDao.findByuid(UserContextService.getCurrentUserAccount().getTenantUID());
        if("云南省阜外心血管病医院".equals(tenantInfo.getName().trim())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void downloadSelectedFile() {
        List<String> boUuids = currentPageAssets.stream().map(AssetInfo::getBoUuid).collect(Collectors.toList());
        List<V2_BlobObject> blobObjects = blobDao.fetchListByBoTypeAndBoUuIds(V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring, boUuids);
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zipOut = new ZipOutputStream(bout)) {
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
            zipOut.close();
            exportFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "CertificateFiles.zip");
        } catch (Exception e) {
            WebUtil.addErrorMessage("Fail to download report:" + e.getMessage());
        }
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public int getCurrentMonthCount() {
        return currentMonthCount;
    }

    public void setCurrentMonthCount(int currentMonthCount) {
        this.currentMonthCount = currentMonthCount;
    }

    public int getNextMonthCount() {
        return nextMonthCount;
    }

    public void setNextMonthCount(int nextMonthCount) {
        this.nextMonthCount = nextMonthCount;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public List<AssetInfo> getCurrentPageAssets() {
        return currentPageAssets;
    }

    public void setCurrentPageAssets(List<AssetInfo> currentPageAssets) {
        this.currentPageAssets = currentPageAssets;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public void setCustomRoleAuth(UserAuth customRoleAuth) {
        this.customRoleAuth = customRoleAuth;
    }
}
