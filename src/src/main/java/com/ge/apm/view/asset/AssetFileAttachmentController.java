package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.V2_BlobObjectMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.VlinkParserService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends GenericCRUDUUIDController<V2_BlobObject> implements Serializable, ServerEventInterface {

    BlobObjectRepository dao;
    V2_BlobObjectMapper blobObjectMapper;
    OrgInfoRepository orgInfoDao;
    TenantInfoRepository tenantInfoDao;

    BlobFileService blobFileService;

    UserAccount currentUser;

    //AssetInfo selectedAsset;

    private AssetInfoService assetService;
    private BlobFileService blobService;

    private UaaService uuaService;
    private AssetTagService assetTagService;

    MicroServiceUtil microService = WebUtil.getBean(MicroServiceUtil.class);

    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets = new ArrayList<>();
    private AssetInfo queryAsset;

    private List<String> orgUidList;

    private UaaService uaaService;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private List<OrgInfo> clinicalDeptList;
    private V2_BlobObject batchBlobObject;
    @Getter@Setter
    private List<V2_BlobObject> batchBlobObjectList = new ArrayList<>();

    List<V2_BlobObject> v2BlobObjects = new ArrayList<>();
    private Set<V2_BlobObject> repeatBlobObjectSet;
    private Set<AssetInfo> repeatAssets;
    private List<AssetInfo> assetInfoList;
    private String boSubTypeBatch;
    private ConfigService configService;
    @Getter @Setter
    private List<I18nMessage> i18nMessages = new ArrayList<>();
    //复选的附件数据
    private Map<String, Boolean> selectedMap;
    private List<V2_BlobObject> currentPageBlobObject;

    private String vlink;

    private String vlinkTitle;

    private VlinkParserService vlinkParserService;

    private String assetName;

    protected FileDownloadService fileDownloadService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BlobObjectRepository.class);
        blobObjectMapper = WebUtil.getBean(V2_BlobObjectMapper.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        microService = WebUtil.getBean(MicroServiceUtil.class);
        assetService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        assetTagService = WebUtil.getBean(AssetTagService.class);
        vlinkParserService = WebUtil.getBean(VlinkParserService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        configService = WebUtil.getBean(ConfigService.class);
        getAllAttachmentCategories();
        this.filterBySite = false;
        orgUidList = uuaService.getOrgListByUser(currentUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        if(currentUser.getOrgLevel() != null && Objects.equals(currentUser.getOrgLevel(), 0)){
            orgUidList.add(currentUser.getTenantUID());
        }

        this.selectedMap = new HashMap();
        this.fileDownloadService=WebUtil.getBean(FileDownloadService.class);
        this.initBatchData();
    }

    @Override
    protected BlobObjectRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_BlobObject> loadData(PageRequest pageRequest) {
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        Wrapper<V2_BlobObject> wrapper = new EntityWrapper<>();
        if (hasFilterOnField("boSubType")) {
            wrapper.eq("o.bo_sub_type", searchFilters.stream().filter(item -> item.fieldName.equals("boSubType")).map(item -> (String) item.value).findAny().orElse(""));
        }
        if (hasFilterOnField("objectName")) {
            wrapper.like("o.object_name", searchFilters.stream().filter(item -> item.fieldName.equals("objectName")).map(item -> (String) item.value).findAny().orElse(""));
        }
        if (hasFilterOnField("assetName")) {
            wrapper.like("a.name", searchFilters.stream().filter(item -> item.fieldName.equals("assetName")).map(item -> (String) item.value).findAny().orElse(""));
        }
        if (hasFilterOnField("serialNum")) {
            wrapper.like("a.serial_num", searchFilters.stream().filter(item -> item.fieldName.equals("serialNum")).map(item -> (String) item.value).findAny().orElse(""));
        }
        if (hasFilterOnField("qrCode")) {
            wrapper.like("a.qr_code", searchFilters.stream().filter(item -> item.fieldName.equals("qrCode")).map(item -> (String) item.value).findAny().orElse(""));
        }
        if (hasFilterOnField("functionType")) {
            wrapper.like("a.function_type", searchFilters.stream().filter(item -> item.fieldName.equals("functionType")).map(item -> (String) item.value).findAny().orElse(""));
        }
        if (hasFilterOnField("createdBy")) {
            String createdBy = searchFilters.stream().filter(item -> item.fieldName.equals("createdBy")).map(item -> (String) item.value).findAny().orElse("");
            List<UserAccount> list = uaaService.findByNameLike(createdBy);
            if (list != null && list.size() > 0) {
                wrapper.in("o.created_by", list.stream().map(item -> item.getUid()).collect(Collectors.toList()));
            }else {
                wrapper.eq("1", "0"); // 1!=1 的条件，确保查询不到任何结果
            }
        }
        if (hasFilterOnField("createdDate")) {
            // 查找大于等于操作符的开始日期条件
            searchFilters.stream()
                    .filter(item -> item.fieldName.equals("createdDate") && item.operator == SearchFilter.Operator.GTE)
                    .findFirst()
                    .ifPresent(filter -> wrapper.ge("o.created_date", (Date) filter.value));

            // 查找小于等于操作符的结束日期条件
            searchFilters.stream()
                    .filter(item -> item.fieldName.equals("createdDate") && item.operator == SearchFilter.Operator.LTE)
                    .findFirst()
                    .ifPresent(filter -> wrapper.le("o.created_date", (Date) filter.value));
        }

        if(UserContextService.hasCurrentTenantName("")){
            wrapper.ne("o.bo_sub_type", "1");
        }
        wrapper.andNew().in("a.site_uid", orgUidList).or().in("a.clinical_dept_uid", orgUidList).or().in("a.hospital_uid", orgUidList).or().in("a.institution_uid", orgUidList).or().in("a.tenant_uid", orgUidList);
        List<String> assetFiled = ListUtil.newArrayList("serial_num", "qr_code", "function_type");
        Sort sort = pageRequest.getSort();
        for (Sort.Order order : sort) {
            Sort.Direction direction = order.getDirection();
            String property = ZipUtil.camelToUnderline(order.getProperty());
            String column;
            if ("asset_name".equals(property)) {
                column = "a.name";
            } else {
                column = (assetFiled.contains(property) ? "a." : "o.") + property;
            }
            wrapper.orderBy(column, "ASC".equals(direction.name()));
        }
        currentPageBlobObject = blobObjectMapper.selectAssetBlob(pagination, wrapper);
        return new PageImpl<>(currentPageBlobObject, pageRequest, pagination.getTotal());
        /*String boSubType = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("boSubType")).map(item -> (String) item.value).findAny().orElse(""));
        boSubType = boSubType.length() > 2  ? boSubType.replace("%","") : boSubType;
        String boName = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("objectName")).map(item -> (String) item.value).findAny().orElse(""));
        String assetName = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("assetInfo.name")).map(item -> (String) item.value).findAny().orElse(""));
        String serialNum = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("assetInfo.serialNum")).map(item -> (String) item.value).findAny().orElse(""));
        String qrCode = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("assetInfo.qrCode")).map(item -> (String) item.value).findAny().orElse(""));
        String functionTypeStr = searchFilters.stream().filter(item -> item.fieldName.equals("assetInfo.functionType")).map(item -> (String) item.value).findAny().orElse("");
        String functionType = "%like%".replace("like", functionTypeStr);

        Page<V2_BlobObject> blobObjectPage;
        if(UserContextService.hasCurrentTenantName("")){
                blobObjectPage = dao.loadDataForPageYunNan(orgUidList, boSubType, boName, assetName, serialNum,qrCode, functionType,"%%", pageRequest);
        }else{
                blobObjectPage = dao.loadDataForPage(orgUidList, boSubType, boName ,assetName,serialNum,qrCode,functionType,"%%", pageRequest);
        }

        currentPageBlobObject = blobObjectPage.getContent();
        return blobObjectPage;*/
    }

    @Override
    public List<V2_BlobObject> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(V2_BlobObject object) {
        this.targetAssets = new ArrayList<>();
    }

    @Override
    public void save() {
        if (targetAssets.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("assetName") + WebUtil.getMessage("ValidationRequire"));
        } else if (null == selected.getBoSubType() || selected.getBoSubType().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("fileType") + WebUtil.getMessage("ValidationRequire"));
        } else if ("vlink".equals(selected.getBoSubType())) {
            if (StringUtils.isEmpty(vlinkTitle)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("name") + WebUtil.getMessage("ValidationRequire"));
                return;
            } else if (StringUtils.isEmpty(vlink)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("link") + WebUtil.getMessage("ValidationRequire"));
                return;
            }
            String parsedLink = vlinkParserService.parseVlink(vlink);
            if (StringUtils.isNotEmpty(parsedLink)) {
                selected.setExtLink(parsedLink);
            } else {
                WebUtil.addErrorMessage(WebUtil.getMessage("link") + WebUtil.getMessage("source_not_supported"));
                return;
            }

            for(AssetInfo a : targetAssets){
                V2_BlobObject object = new V2_BlobObject();
                object.setBoSubType(selected.getBoSubType());
                object.setBoId(a.getId());
                object.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                object.setBoUuid(a.getUid());
                object.setObjectName(vlinkTitle);
                object.setExtLink(parsedLink);
                object.setCreatedBy(currentUser.getUid());
                object.setCreatedDate(new Date());
                dao.save(object);
            }
            cancel();
        } else if (batchBlobObjectList.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
        } else {
            if (selected.getBoSubType().equals("1")) {
                boolean r = false;
                for(V2_BlobObject b : batchBlobObjectList){
                    if(b.getObjectName().toLowerCase().matches("^.*?\\.(jpg|jpeg|bmp|gif|png)$")){
                        r = true;
                        break;
                    }
                }
                if(!r){
                    WebUtil.addErrorMessage(WebUtil.getMessage("InvalidPictureFileType"));
                    return;
                }
            }

            for(V2_BlobObject blobObject : batchBlobObjectList) {
                for(AssetInfo a : targetAssets){
                    V2_BlobObject object = new V2_BlobObject();
                    object.setBoSubType(selected.getBoSubType());
                    object.setBoId(a.getId());
                    object.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                    object.setBoUuid(a.getUid());
                    object.setObjectStorageId(blobObject.getObjectStorageId());
                    object.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                    object.setObjectName(blobObject.getObjectName());
                    object.setObjectStorageId(blobObject.getObjectStorageId());
                    object.setObjectSize(blobObject.getObjectSize());
                    object.setObjectType(blobObject.getObjectType());
                    object.setCreatedBy(blobObject.getCreatedBy());
                    object.setCreatedDate(blobObject.getCreatedDate());
                    dao.save(object);
                }
            }

            cancel();
        }
    }

    public void cancel() {
        this.selected = null;
        this.vlink = null;
        this.vlinkTitle = null;
        this.targetAssets = new ArrayList<>();
        this.batchBlobObjectList = new ArrayList<>();
    }

    private void getAllAttachmentCategories() {
        List<WorkflowConfig.AttachmentCategory> attachmentCategoriesList = configService.getAttachmentCategoriesByTenantUid(currentUser.getTenantUID());
        i18nMessages = FieldValueMessageController.getFieldValueListByMsgType("attachmentType");
        for(WorkflowConfig.AttachmentCategory attachmentCategory : attachmentCategoriesList) {
            I18nMessage i18nMessage = new I18nMessage();
            i18nMessage.setMsgKey(attachmentCategory.getBoType());
            i18nMessage.setValueZh(attachmentCategory.getLabel());
            i18nMessages.add(i18nMessage);
        }
    }

    public String getAttachmentType(String msyKey) {
        for(I18nMessage i18nMessage : i18nMessages) {
            if(i18nMessage.getMsgKey()!=null && msyKey!=null && i18nMessage.getMsgKey().equals(msyKey)) return i18nMessage.getValue();
        }
        return "未匹配类型";
    }

    @Override
    public void delete() {
        List<V2_BlobObject> blobObjList = dao.findAllByObjectStorageId(selected.getObjectStorageId());

        if (!ObjectUtils.isEmpty(blobObjList) && blobObjList.size() == 1) {
            blobService.deleteBlobFile(selected);
        }

        super.delete();
        cancel();
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            batchBlobObjectList.add(blobObject);
            v2BlobObjects.add(blobObject);
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void removeAttachment(Integer fileId) {
        blobService.deleteBlobFile(selected);
        selected.setId(null);
        selected.setObjectName(null);
        selected.setObjectStorageId(null);
        selected.setObjectSize(null);
    }

    public void onAssetSelected(SelectEvent event) {
        onServerEvent("assetSelected", event.getObject());
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        List<AssetInfo> asset = (List<AssetInfo>) eventObject;
        if (asset == null) {
            return;
        }

        if (this.selected == null) {
            return;
        }

        List<AssetInfo> addAsset = asset.stream().filter(t -> {
            boolean r = true;
            for (AssetInfo a : targetAssets){
                if(a.getUid().equals(t.getUid())){
                    r = false;
                    break;
                }
            }
            return r;
        }).collect(Collectors.toList());
        this.targetAssets.addAll(addAsset);
    }

    public void onSelectedChange() {
        //selectedAsset = assetService.getAssetInfo(selected.getBoId());
        crudActionName = "Edit";
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        if(currentUser.getOrgLevel() != null && Objects.equals(currentUser.getOrgLevel(), 0)){
            OrgInfo tempOrgInfo = orgInfoDao.getByUid(loginUser.getTenantUID());
            rootOrg = tempOrgInfo;
        }else {
            rootOrg.setId(-1);
            rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        }
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().add(treeNode);
        //rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(loginUser.getTenantUID()).getChildren());
        rootNode.setExpanded(true);

    }

    public void onSelectTreeNode(NodeSelectEvent event) {

        queryAsset.setTenantUID(null);
        queryAsset.setInstitutionUID(null);
        queryAsset.setHospitalUID(null);
        queryAsset.setSiteUID(null);

        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

        if (selectedOrg != null && selectedOrg.getOrgLevel() != null) {
            if(Objects.equals(selectedOrg.getOrgLevel(), 0)){
                queryAsset.setTenantUID(selectedOrg.getTenantUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getTenantUID(), 4);
            } else if (Objects.equals(selectedOrg.getOrgLevel(), 1)) {
                queryAsset.setInstitutionUID(selectedOrg.getInstitutionUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getInstitutionUID(), 4);
            } else if (Objects.equals(selectedOrg.getOrgLevel(), 2)) {
                queryAsset.setHospitalUID(selectedOrg.getHospitalUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getHospitalUID(), 4);
            } else if (Objects.equals(selectedOrg.getOrgLevel(), 3)) {
                queryAsset.setSiteUID(selectedOrg.getSiteUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getSiteUID(), 4);
            }
        }

        //this.getQueryAssetsList();

    }

    public void getQueryAssetsList() {
        if (selectedOrg != null && selectedOrg.getOrgLevel() != null && (queryAsset.getClinicalDeptId() != null || queryAsset.getAssetGroup() != null || queryAsset.getFunctionType() != null || this.assetName != null)) {

            if(this.assetName != null){
                queryAsset.setName(this.assetName);
            }else{
                queryAsset.setName(null);
            }
            sourceAssets = assetTagService.getAssetList(queryAsset);

            sourceAssets = new ArrayList(sourceAssets);
            sourceAssets.removeAll(targetAssets);

            /*targetAssets.forEach((item)->{
                if(sourceAssets.contains(item)){
                    sourceAssets.remove(item);
                }
            });*/
        } else {
            sourceAssets = new ArrayList<AssetInfo>();
        }
        availableAssets = new DualListModel<>(sourceAssets, targetAssets);
    }

    public void onAssetsTransfer(TransferEvent event) {

        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                targetAssets.add((AssetInfo) item);
            } else {
                targetAssets.remove((AssetInfo) item);
            }
        }
    }

    public String getOrgName(String siteUID) {
        OrgInfo orgInfo = orgInfoDao.getByUid(siteUID);
        if (orgInfo != null) {
            return orgInfo.getName();
        } else {
            return "";
        }
    }



    public void handleFileUploadBatch(FileUploadEvent event,String type) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            blobObject.setBoSubType(type);
            batchBlobObjectList.add(blobObject);
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadBatchImage(FileUploadEvent event) {
        handleFileUploadBatch(event,"1");
    }

    public void handleFileUploadBatchUserManual(FileUploadEvent event) {
        handleFileUploadBatch(event,"2");
    }

    public void handleFileUploadBatchTrainingMaterial(FileUploadEvent event) {
        handleFileUploadBatch(event,"3");
    }

    public void handleFileUploadBatchMeasurementDocument(FileUploadEvent event) {
        handleFileUploadBatch(event,"Measuring");
    }

    public void handleFileUploadBatchVerification(FileUploadEvent event) {
        handleFileUploadBatch(event,"4");
    }

    public void handleFileUploadBatchVideoLink(FileUploadEvent event) {
        handleFileUploadBatch(event,"vlink");
    }

    public void removeAttachmentBatch(String objectId) {
        Iterator<V2_BlobObject> iterator = batchBlobObjectList.iterator();
        while (iterator.hasNext()){
            V2_BlobObject object = iterator.next();
            if(object.getId().equals(objectId)){
                blobService.deleteBlobFile(object);
                iterator.remove();
                return;
            }
        }
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageBlobObject.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_BlobObject item : currentPageBlobObject) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void deleteBatch() {
        selectedMap.forEach((key, value) -> {
            if (value) {
                blobService.deleteBlobFileById(key);
            }
        });
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public String batchSave() {
        if(targetAssets == null || targetAssets.isEmpty()){
            WebUtil.addErrorMessage(WebUtil.getMessage("assetName") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return "List.xhtml?faces-redirect=true";
        }

        List<V2_BlobObject> batchSaveObject =new ArrayList<>();

        //保存视频链接
        if(!batchSaveVlinkAdd(batchSaveObject)){
            return "List.xhtml?faces-redirect=true";
        }
        //保存上传对象
        batchSaveObjectAdd(batchSaveObject);

        this.initBatchData();
        if(!batchSaveObject.isEmpty()){
            dao.save(batchSaveObject);
        }
        if(repeatAssets!= null && !repeatAssets.isEmpty()){
            return "";
        }
        return "List.xhtml?faces-redirect=true";
    }

    public String repeatSave() {
        if(repeatAssets == null || repeatAssets.isEmpty()){
            WebUtil.addErrorMessage(WebUtil.getMessage("assetName") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return "List.xhtml?faces-redirect=true";
        }
        List<V2_BlobObject> batchSaveObject =new ArrayList<>();

        //保存上传对象
        repeatSaveObjectAdd(batchSaveObject);
        this.initBatchData();
        dao.save(batchSaveObject);
        return "List.xhtml?faces-redirect=true";
    }
    public void cancelData(){
        repeatBlobObjectSet = new HashSet<>();
        repeatAssets = new HashSet<>();
        assetInfoList = new ArrayList<>();
        getWorkflowList();
    }
    public List<AssetInfo> getAssetInfos() {
        return assetInfoList;
    }
    public boolean getFlag() {
        if (repeatAssets != null && !repeatAssets.isEmpty()){
            return false;
        }
        return true;
    }
    private void batchSaveObjectAdd(List<V2_BlobObject> batchSaveObject) {
        if(batchBlobObjectList.isEmpty()){
            return ;
        }
        repeatBlobObjectSet = new HashSet<>();
        repeatAssets = new HashSet<>();
        assetInfoList = new ArrayList();
        for (AssetInfo tempAssetInfo : targetAssets) {
            List<V2_BlobObject> v2BlobObjectList = dao.findByBoUuid(tempAssetInfo.getUid());
            Set<String> objectNameSet = new HashSet<>();
            for(V2_BlobObject v2BlobObject : v2BlobObjectList){
                objectNameSet.add(v2BlobObject.getObjectName());
            }
            for(V2_BlobObject batchBlobObject : batchBlobObjectList){
                if(!objectNameSet.isEmpty() && objectNameSet.contains(batchBlobObject.getObjectName())){
                    repeatBlobObjectSet.add(batchBlobObject);
                    AssetInfo assetInfo = new AssetInfo();
                    BeanUtils.copyProperties(tempAssetInfo,assetInfo);
                    assetInfo.setErrorMessage(batchBlobObject.getObjectName());
                    repeatAssets.add(tempAssetInfo);
                    assetInfoList.add(assetInfo);
                }else {
                    V2_BlobObject tempBlobObject = new V2_BlobObject();
                    tempBlobObject.setBoId(tempAssetInfo.getId());
                    tempBlobObject.setBoUuid(tempAssetInfo.getUid());
                    tempBlobObject.setBoSubType(batchBlobObject.getBoSubType());
                    tempBlobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                    tempBlobObject.setObjectName(batchBlobObject.getObjectName());
                    tempBlobObject.setObjectStorageId(batchBlobObject.getObjectStorageId());
                    tempBlobObject.setObjectSize(batchBlobObject.getObjectSize());
                    tempBlobObject.setObjectType(batchBlobObject.getObjectType());
                    tempBlobObject.setCreatedBy(batchBlobObject.getCreatedBy());
                    tempBlobObject.setCreatedDate(batchBlobObject.getCreatedDate());
                    batchSaveObject.add(tempBlobObject);
                }
            }
        }
    }
    private void repeatSaveObjectAdd(List<V2_BlobObject> batchSaveObject) {
        if(repeatBlobObjectSet.isEmpty()){
            return ;
        }

        for (AssetInfo tempAssetInfo : repeatAssets) {
            List<V2_BlobObject> v2BlobObjectList = dao.findByBoUuid(tempAssetInfo.getUid());
            /*for(V2_BlobObject v2BlobObject : v2BlobObjectList){
                dao.delete(v2BlobObject.getId());
            }*/
            for(V2_BlobObject batchBlobObject : repeatBlobObjectSet){
                V2_BlobObject tempBlobObject = new V2_BlobObject();
                tempBlobObject.setBoId(tempAssetInfo.getId());
                tempBlobObject.setBoUuid(tempAssetInfo.getUid());
                tempBlobObject.setBoSubType(batchBlobObject.getBoSubType());
                tempBlobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                tempBlobObject.setObjectName(batchBlobObject.getObjectName());
                tempBlobObject.setObjectStorageId(batchBlobObject.getObjectStorageId());
                tempBlobObject.setObjectSize(batchBlobObject.getObjectSize());
                tempBlobObject.setObjectType(batchBlobObject.getObjectType());
                tempBlobObject.setCreatedDate(new Date());
                tempBlobObject.setCreatedBy(currentUser.getUid());
                batchSaveObject.add(tempBlobObject);
            }
        }
    }
    private boolean batchSaveVlinkAdd(List<V2_BlobObject> batchSaveObject) {
        if(StringUtils.isEmpty(vlink)){
            return true;
        }
        String parsedLink = vlinkParserService.parseVlink(vlink);
        if (StringUtils.isEmpty(parsedLink)) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("link") + WebUtil.getMessage("source_not_supported"));
            return false;
        }
        for (AssetInfo tempAssetInfo : targetAssets) {
            V2_BlobObject tempBlobObject = new V2_BlobObject();
            tempBlobObject.setBoId(tempAssetInfo.getId());
            tempBlobObject.setBoUuid(tempAssetInfo.getUid());
            tempBlobObject.setBoSubType("vlink");
            tempBlobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            tempBlobObject.setObjectName(vlinkTitle);
            tempBlobObject.setExtLink(parsedLink);
            batchSaveObject.add(tempBlobObject);
        }
        return true;
    }

    private void initBatchData() {
        selectedOrg = null;
        batchBlobObject = null;
        batchBlobObjectList.clear();
        boSubTypeBatch = null;

        targetAssets = new ArrayList();
        queryAsset = new AssetInfo();
        clinicalDeptList = new ArrayList<OrgInfo>();

        queryAsset.setTenantUID(currentUser.getTenantUID());

        this.getQueryAssetsList();
        this.initOrgTree();
    }


    public void downloadDocument(String reportStorageId) {
        try {
            StreamedContent file = fileDownloadService.getFile(reportStorageId);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

            StreamUtils.copy(file.getStream(), response.getOutputStream());

            facesContext.responseComplete();
            facesContext.renderResponse();
        } catch (Exception e) {
            WebUtil.addErrorMessage("Fail to download report.");
        }
    }

    public String getUserName(String uid){
        if (StrUtil.isEmpty(uid)) {
            return "";
        }
        UserAccount userAccount = uaaService.getUserByUid(uid);
        if(userAccount != null){
            return userAccount.getName();
        }
        return uid;
    }
    public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }

    @Getter@Setter
    List<WorkflowConfig.AttachmentCategory> attachmentCategories = new ArrayList<>();
    public void getWorkflowList() {
        WorkflowConfig workflowConfig = configService.findConfigBySite(currentUser.getSiteUID());
        if(workflowConfig != null){
            String categories = workflowConfig.getCustomizedAssetAttachmentCategory();
            if (null != categories) {
                attachmentCategories = JSON.parseObject(categories, new TypeReference<List<WorkflowConfig.AttachmentCategory>>() {
                });
            }
        }

    }

    public void handleFileUploadWorkFlowConfig(FileUploadEvent event) {
        handleFileUploadBatch(event,event.getComponent().getId());
    }


    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public DualListModel<AssetInfo> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<AssetInfo> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<AssetInfo> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<AssetInfo> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<AssetInfo> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public V2_BlobObject getBatchBlobObject() {
        return batchBlobObject;
    }

    public void setBatchBlobObject(V2_BlobObject batchBlobObject) {
        this.batchBlobObject = batchBlobObject;
    }

    public String getBoSubTypeBatch() {
        return boSubTypeBatch;
    }

    public void setBoSubTypeBatch(String boSubTypeBatch) {
        this.boSubTypeBatch = boSubTypeBatch;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public String getVlink() {
        return vlink;
    }

    public void setVlink(String vlink) {
        this.vlink = vlink;
    }

    public String getVlinkTitle() {
        return vlinkTitle;
    }

    public void setVlinkTitle(String vlinkTitle) {
        this.vlinkTitle = vlinkTitle;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public List<V2_BlobObject> getBatchBlobObjectList() {
        return batchBlobObjectList;
    }

    public List<V2_BlobObject> getBatchBlobObjectListByType(String type) {
        List<V2_BlobObject> collect = batchBlobObjectList.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
        return collect;
    }

    public void setBatchBlobObjectList(List<V2_BlobObject> batchBlobObjectList) {
        this.batchBlobObjectList = batchBlobObjectList;
    }
}
