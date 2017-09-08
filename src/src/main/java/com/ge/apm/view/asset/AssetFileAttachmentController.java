package com.ge.apm.view.asset;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
//import com.ge.apm.dao.AssetFileAttachmentRepository;
import com.ge.apm.dao.BlobObjectRepository;
//import com.ge.apm.domain.AssetFileAttachment;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class AssetFileAttachmentController extends GenericCRUDUUIDController<V2_BlobObject> implements Serializable, ServerEventInterface{

    BlobObjectRepository dao;

    BlobFileService blobFileService;

    UserAccount currentUser;

    AssetInfo selectedAsset;

    private AssetInfoService assetService;
    private BlobFileService blobService;
    
    private UaaService uuaService;

    MicroServiceUtil microService = WebUtil.getBean(MicroServiceUtil.class);
    
    private List<String> orgUidList;

    @Override
    protected void init() {
        dao = WebUtil.getBean(BlobObjectRepository.class);
        microService = WebUtil.getBean(MicroServiceUtil.class);
        assetService = WebUtil.getBean(AssetInfoService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        uuaService = WebUtil.getBean(UaaService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = false;
        orgUidList = uuaService.getOrgListByUser(currentUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
    }

    @Override
    protected BlobObjectRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_BlobObject> loadData(PageRequest pageRequest) {
        
        if(searchFilters==null){
            searchFilters = new ArrayList();
        }
        String boSubType = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("boSubType")).map(item -> (String)item.value).findAny().orElse(""));
        String boName = "%like%".replace("like", searchFilters.stream().filter(item -> item.fieldName.equals("objectName")).map(item -> (String)item.value).findAny().orElse(""));
        
        return dao.loadDataForPage(orgUidList, boSubType, boName, pageRequest);
        
//        if (this.searchFilters == null) {
//            return dao.findAll(pageRequest);
//        } else {
//            return dao.findBySearchFilter(this.searchFilters, pageRequest);
//        }
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
        } else if (null == selected.getObjectName() || selected.getObjectName().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
        } else if (selected.getBoSubType().equals("1") && !selected.getBoSubType().matches("^.*?\\.(jpg|jpeg|bmp|gif|png)$")) {
            WebUtil.addErrorMessage(WebUtil.getMessage("InvalidPictureFileType"));
        } else {
            selected.setBoId(selectedAsset.getId());
            selected.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            selected.setBoId(selectedAsset.getId());
            selected.setBoUuid(selectedAsset.getUid());
            dao.save(selected);
            cancel();
        }
    }

    public void cancel() {
        this.selected = null;
        this.selectedAsset = null;
    }

    @Override
    public void delete() {
        blobService.deleteBlobFile(selected);
        super.delete();
        cancel();
    }

    public void handleFileUpload(FileUploadEvent event) {

        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            selected = blobObject;
//            selected.setName(blobObject.getObjectName());
//            selected.setFileUrl(blobObject.getObjectStorageId());
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
//        blobObject = blobService.getBlobObjectByStorageId(selected.getObjectStorageId());
        crudActionName = "Edit";
    }

    public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }
    
//     public List<AssetInfo> getAssetList() {
//        return assetList;
//    }
//
//    public void setAssetList(List<AssetInfo> assetList) {
//        this.assetList = assetList;
//    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }
}
