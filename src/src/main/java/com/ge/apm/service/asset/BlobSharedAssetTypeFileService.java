package com.ge.apm.service.asset;

import com.ge.apm.dao.BlobObjectShareAssetTypeRepository;
import com.ge.apm.domain.V2_BlobObjectSharedAssetType;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.asset.AssetFileAttachmentController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class BlobSharedAssetTypeFileService {
    @Autowired
    private BlobObjectShareAssetTypeRepository blobObjectShareAssetTypeRepository;
    @Autowired
    private FileScanService fsService;
    @Autowired
    private MicroServiceUtil microService;

    public V2_BlobObjectSharedAssetType uploadFileShareAssetType(FileUploadEvent event) {
        return uploadFileShareAssetType(event.getFile());
    }

    public V2_BlobObjectSharedAssetType uploadFileShareAssetType (UploadedFile file) {

        String path = "";
        try {
            path = fsService.saveUploadedFile(file);
        } catch (Exception ex) {
            Logger.getLogger(AssetFileAttachmentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return null;
        }

        Map<String, Object> fileObject = microService.uploadSingleFile(token, new File(path));
        fsService.deleteUploadedFile(path);

        if (null != fileObject) {
            V2_BlobObjectSharedAssetType v2_blobObjectSharedAssetType = new V2_BlobObjectSharedAssetType();
            v2_blobObjectSharedAssetType.setObjectName((String) fileObject.get("objectName"));
            v2_blobObjectSharedAssetType.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            v2_blobObjectSharedAssetType.setObjectStorageId((String) fileObject.get("objectId"));
            v2_blobObjectSharedAssetType.setObjectType((String) fileObject.get("objectType"));
            v2_blobObjectSharedAssetType.setCreatedDate(new Date());
            v2_blobObjectSharedAssetType.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            return blobObjectShareAssetTypeRepository.save(v2_blobObjectSharedAssetType);
        } else {
            return null;
        }
    }

    public Boolean deleteBlobObjectShareAssetType(V2_BlobObjectSharedAssetType blobObject) {

        if(null == blobObject){
            return false;
        }

        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return false;
        }

        return deleteBlobObjectShareAssetType(token,blobObject);
    }

    public List<V2_BlobObjectSharedAssetType> findAllByObjectStorageId(String storagedId) {
        return blobObjectShareAssetTypeRepository.findAllByObjectStorageId(storagedId);
    }

    public List<V2_BlobObjectSharedAssetType> findBlobObjectShareAssetTypesByBoUid(String uid){
        return blobObjectShareAssetTypeRepository.findByBoUuid(uid);
    }

    public Boolean deleteBlobObjectShareAssetTypById(String id){
        V2_BlobObjectSharedAssetType blobObject = blobObjectShareAssetTypeRepository.findById(id);
        if(blobObject==null){
            return null;
        }
        List<V2_BlobObjectSharedAssetType> blobList = findAllByObjectStorageId(blobObject.getObjectStorageId());
        if(blobList!=null && blobList.size()==1){
            return deleteBlobObjectShareAssetType(blobObject);
        }else{
            blobObjectShareAssetTypeRepository.delete(id);
            return true;
        }
    }

    public Boolean deleteBlobObjectShareAssetType(String token, V2_BlobObjectSharedAssetType blobObject) {
        if (microService.deleteFileObject(token, blobObject.getObjectStorageId())) {
            blobObjectShareAssetTypeRepository.delete(blobObject);
            return true;
        } else {
            WebUtil.addErrorMessage("Fail to delete file");
            return false;
        }
    }
}
