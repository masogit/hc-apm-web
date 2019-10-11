package com.ge.apm.view.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AssetTagService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.VlinkParserService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends GenericCRUDUUIDController<V2_BlobObject> implements Serializable, ServerEventInterface {

    BlobObjectRepository dao;
    OrgInfoRepository orgInfoDao;

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
    private String boSubTypeBatch;

    //复选的附件数据
    private Map<String, Boolean> selectedMap;
    private List<V2_BlobObject> currentPageBlobObject;

    private String vlink;

    private String vlinkTitle;

    private VlinkParserService vlinkParserService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BlobObjectRepository.class);
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
        if (StringUtils.isEmpty(functionTypeStr)) {
            blobObjectPage = dao.loadDataForPageForFunctionType(orgUidList, boSubType, boName, assetName, functionType, pageRequest);
        } else {
            blobObjectPage = dao.loadDataForPage(orgUidList, boSubType, boName, assetName, functionType, pageRequest);
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
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().add(treeNode);
        //rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenant(loginUser.getTenantUID()).getChildren());
        rootNode.setExpanded(true);

    }

    public void onSelectTreeNode(NodeSelectEvent event) {

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
            if (selectedOrg.getOrgLevel() == 1) {
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

        this.getQueryAssetsList();

    }

    public void getQueryAssetsList() {
        if (selectedOrg != null && selectedOrg.getOrgLevel() != null && (queryAsset.getClinicalDeptId() != null || queryAsset.getAssetGroup() != null || queryAsset.getFunctionType() != null)) {
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

    public void handleFileUploadBatch(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            batchBlobObject = blobObject;
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void removeAttachmentBatch(Integer fileId) {

        blobService.deleteBlobFile(batchBlobObject);
        batchBlobObject.setId(null);
        batchBlobObject.setObjectName(null);
        batchBlobObject.setObjectStorageId(null);
        batchBlobObject.setObjectSize(null);
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

    public void batchSave() {
        if (batchBlobObject != null || "vlink".equals(boSubTypeBatch)) {
            String parsedLink = vlinkParserService.parseVlink(vlink);
            if ("vlink".equals(boSubTypeBatch) && StringUtils.isEmpty(parsedLink)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("link") + WebUtil.getMessage("source_not_supported"));
                return;
            }
            for (AssetInfo tempAssetInfo : targetAssets) {
                V2_BlobObject tempBlobObject = new V2_BlobObject();

                tempBlobObject.setBoId(tempAssetInfo.getId());
                tempBlobObject.setBoUuid(tempAssetInfo.getUid());
                tempBlobObject.setBoSubType(boSubTypeBatch);
                tempBlobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
                if ("vlink".equals(boSubTypeBatch)) {
                    tempBlobObject.setObjectName(vlinkTitle);
                    tempBlobObject.setExtLink(parsedLink);
                } else {
                    tempBlobObject.setObjectName(batchBlobObject.getObjectName());
                    tempBlobObject.setObjectStorageId(batchBlobObject.getObjectStorageId());
                    tempBlobObject.setObjectSize(batchBlobObject.getObjectSize());
                    tempBlobObject.setObjectType(batchBlobObject.getObjectType());
                }
                dao.save(tempBlobObject);
            }
        } else {
            WebUtil.addErrorMessage("附件不能为空");
            return;
        }

        this.initBatchData();
    }

    private void initBatchData() {
        selectedOrg = null;
        batchBlobObject = null;
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
}
