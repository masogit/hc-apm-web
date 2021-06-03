package com.ge.apm.view.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.VlinkParserService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends GenericCRUDUUIDController<V2_BlobObject> implements Serializable, ServerEventInterface {

    BlobObjectRepository dao;
    OrgInfoRepository orgInfoDao;
    TenantInfoRepository tenantInfoDao;

    BlobFileService blobFileService;

    UserAccount currentUser;

    AssetInfo selectedAsset;

    private AssetInfoService assetService;
    private BlobFileService blobService;

    private UaaService uuaService;
    private AssetTagService assetTagService;

    MicroServiceUtil microService = WebUtil.getBean(MicroServiceUtil.class);

    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets;
    private List<AssetInfo> targetAssets;
    private AssetInfo queryAsset;

    private List<String> orgUidList;

    private UaaService uaaService;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private List<OrgInfo> clinicalDeptList;
    private V2_BlobObject batchBlobObject;
    private List<V2_BlobObject> batchBlobObjectList = new ArrayList<>();
    private Set<V2_BlobObject> repeatBlobObjectSet;
    private Set<AssetInfo> repeatAssets;
    private String boSubTypeBatch;

    //复选的附件数据
    private Map<String, Boolean> selectedMap;
    private List<V2_BlobObject> currentPageBlobObject;

    private String vlink;

    private String vlinkTitle;

    private VlinkParserService vlinkParserService;

    private String assetName;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BlobObjectRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        microService = WebUtil.getBean(MicroServiceUtil.class);
        assetService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        assetTagService = WebUtil.getBean(AssetTagService.class);
        vlinkParserService = WebUtil.getBean(VlinkParserService.class);
        this.filterBySite = false;
        orgUidList = uuaService.getOrgListByUser(currentUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        if(currentUser.getOrgLevel() != null && currentUser.getOrgLevel() == 0){
            orgUidList.add(currentUser.getTenantUID());
        }

        this.selectedMap = new HashMap();
        this.initBatchData();
    }

    @Override
    protected BlobObjectRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_BlobObject> loadData(PageRequest pageRequest) {

        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        String boSubType = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("boSubType")).map(item -> (String) item.value).findAny().orElse(""));
        String boName = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("objectName")).map(item -> (String) item.value).findAny().orElse(""));
        String assetName = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("assetInfo.name")).map(item -> (String) item.value).findAny().orElse(""));

        String functionTypeStr = searchFilters.stream().filter(item -> item.fieldName.equals("assetInfo.functionType")).map(item -> (String) item.value).findAny().orElse("");
        String functionType = "%like%".replace("like", functionTypeStr);

        Page<V2_BlobObject> blobObjectPage;
        if(UserContextService.hasCurrentTenantName("")){
            if (StringUtils.isEmpty(functionTypeStr)) {
                blobObjectPage = dao.loadDataForPageForFunctionTypeYunNan(orgUidList, boSubType, boName, assetName, functionType, pageRequest);
            } else {
                blobObjectPage = dao.loadDataForPageYunNan(orgUidList, boSubType, boName, assetName, functionType, pageRequest);
            }
        }else{
            if (StringUtils.isEmpty(functionTypeStr)) {
                blobObjectPage = dao.loadDataForPageForFunctionType(orgUidList, boSubType, boName, assetName, functionType, pageRequest);
            } else {
                blobObjectPage = dao.loadDataForPage(orgUidList, boSubType, boName, assetName, functionType, pageRequest);
            }
        }

        currentPageBlobObject = blobObjectPage.getContent();
        return blobObjectPage;
    }

    @Override
    public List<V2_BlobObject> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(V2_BlobObject object) {
        this.selectedAsset = null;
    }

    @Override
    public void save() {
        if (null == selectedAsset) {
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
            selected.setBoId(selectedAsset.getId());
            selected.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            selected.setBoUuid(selectedAsset.getUid());
            selected.setObjectName(vlinkTitle);
            dao.save(selected);
            cancel();
            return;
        } else if (null == selected.getObjectName() || selected.getObjectName().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
        } else if (selected.getBoSubType().equals("1") && !selected.getObjectName().toLowerCase().matches("^.*?\\.(jpg|jpeg|bmp|gif|png)$")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("InvalidPictureFileType"));
        } else {
            selected.setBoId(selectedAsset.getId());
            selected.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            selected.setBoUuid(selectedAsset.getUid());
            dao.save(selected);
            cancel();
        }
    }

    public void cancel() {
        this.selected = null;
        this.vlink = null;
        this.vlinkTitle = null;
        this.selectedAsset = null;
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
            selected = blobObject;
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
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }

        if (this.selected == null) {
            return;
        }
        this.selected.setBoId(asset.getId());
        this.selected.setBoUuid(asset.getUid());
        this.selectedAsset = asset;
    }

    public void onSelectedChange() {
        selectedAsset = assetService.getAssetInfo(selected.getBoId());
        crudActionName = "Edit";
    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        if(currentUser.getOrgLevel() != null && currentUser.getOrgLevel() == 0){
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
            if(selectedOrg.getOrgLevel() == 0){
                queryAsset.setTenantUID(selectedOrg.getTenantUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getTenantUID(), 4);
            } else if (selectedOrg.getOrgLevel() == 1) {
                queryAsset.setInstitutionUID(selectedOrg.getInstitutionUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getInstitutionUID(), 4);
            } else if (selectedOrg.getOrgLevel() == 2) {
                queryAsset.setHospitalUID(selectedOrg.getHospitalUID());
                clinicalDeptList = orgInfoDao.findByParentUIDAndOrgTypeOrderByNameAsc(selectedOrg.getHospitalUID(), 4);
            } else if (selectedOrg.getOrgLevel() == 3) {
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

    public Set<AssetInfo> getAssetInfos() {
        return repeatAssets;
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
        repeatAssets = new HashSet<>();;
        for (AssetInfo tempAssetInfo : targetAssets) {
            List<V2_BlobObject> v2BlobObjectList = dao.findByBoUuid(tempAssetInfo.getUid());
            Set<String> objectNameSet = new HashSet<>();
            for(V2_BlobObject v2BlobObject : v2BlobObjectList){
                objectNameSet.add(v2BlobObject.getObjectName());
            }
            for(V2_BlobObject batchBlobObject : batchBlobObjectList){
                if(!objectNameSet.isEmpty() && objectNameSet.contains(batchBlobObject.getObjectName())){
                    repeatBlobObjectSet.add(batchBlobObject);
                    tempAssetInfo.setErrorMessage(batchBlobObject.getObjectName());
                    repeatAssets.add(tempAssetInfo);
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
            for(V2_BlobObject v2BlobObject : v2BlobObjectList){
                dao.delete(v2BlobObject.getId());
            }
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

    public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
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
