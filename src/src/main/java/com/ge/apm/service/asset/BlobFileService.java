/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.asset.AssetFileAttachmentController;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class BlobFileService {

    @Autowired
    BlobObjectRepository blobDao;

    @Autowired
    MicroServiceUtil microService;

    @Autowired
    FileScanService fsService;
    
    public V2_BlobObject uploadFile(FileUploadEvent event) {
        return uploadFile(event.getFile());
    }
    
    public V2_BlobObject uploadFile(UploadedFile file) {
    
        String path = "";
        try {
            path = fsService.saveUploadedFile(file);
        } catch (Exception ex) {
            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
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
            V2_BlobObject blobObject = new V2_BlobObject();
            blobObject.setObjectName((String) fileObject.get("objectName"));
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            return blobDao.save(blobObject);
        } else {
            return null;
        }

    }

    public Boolean deleteBlobFile(V2_BlobObject blobObject) {
        
        if(null == blobObject){
            return false;
        }
        
        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return false;
        }

        return deleteBlobFile(token,blobObject);
    }


    public List<V2_BlobObject> getBlobObjectsByStorageId(String storagedId) {
        return blobDao.findAllByObjectStorageId(storagedId);
    }

    public List<V2_BlobObject> findBlobObjectsByBoUid(String uid){
        return blobDao.findByBoUuid(uid);
    }
    
    public Boolean deleteBlobFileById(String id){
        V2_BlobObject blobObject = blobDao.findById(id);
        if(blobObject==null){
            return null;
        }
        List<V2_BlobObject> blobList = getBlobObjectsByStorageId(blobObject.getObjectStorageId());
        if(blobList!=null && blobList.size()==1){
            return deleteBlobFile(blobObject);
        }else{
            blobDao.delete(id);
            return true;
        }
    }

    /**
     * Need to investigate why the same object storage id occurs more than once
     * @param objectId
     * @return
     */
    public Boolean deleteBlobFilesByStorageId(String objectId) {
        List<V2_BlobObject> objects = getBlobObjectsByStorageId(objectId);
        for (V2_BlobObject object: objects) {
            if(!deleteBlobFile(object)){
                return false;
            }
        }
        return true;
    }

    
    public Boolean deleteBlobFile(String token,V2_BlobObject blobObject) {
        if (microService.deleteFileObject(token, blobObject.getObjectStorageId())) {
            blobDao.delete(blobObject);
            return true;
        } else {
            WebUtil.addErrorMessage("Fail to delete file");
            return false;
        }
    }
    
    public void saveBlobObject(V2_BlobObject blobObject){
        blobDao.save(blobObject);
    }

    public StreamedContent getBlobObjectByStorageId(String storageId){
        return microService.downloadSingleFile(storageId);
    }
}
