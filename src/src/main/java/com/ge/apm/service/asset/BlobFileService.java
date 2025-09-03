/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import com.ge.apm.mq.MqUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.asset.AssetFileAttachmentController;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.tools.FileObject;

/**
 *
 * @author 212579464
 */
@Component
public class BlobFileService {
    private final org.slf4j.Logger log = LoggerFactory.getLogger(AssetFaultTypeService.class);

    @Autowired
    BlobObjectRepository blobDao;

    @Autowired
    MicroServiceUtil microService;

    @Autowired
    FileScanService fsService;

    @Autowired
    private MqUtils mqUtils;
    
    public V2_BlobObject uploadFile(FileUploadEvent event) {
        return uploadFile(event.getFile());
    }
    
    public V2_BlobObject uploadFile(UploadedFile file) {
    
        String path = "";
        try {
            path = fsService.saveUploadedFile(file);
        } catch (Exception ex) {
//            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
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
            String objectName = (String) fileObject.get("objectName");
            blobObject.setObjectName((String) fileObject.get("objectName"));
//            try {
//                String name = new String(objectName.getBytes("ISO-8859-1"), StandardCharsets.UTF_8);
//                log.info("objectName:{}",name);
//                blobObject.setObjectName(name);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            return blobDao.save(blobObject);
        } else {
            return null;
        }

    }

    public V2_BlobObject uploadFile(V2_BlobObject tempBlobObject, UploadedFile file) {

        String path = "";
        try {
            path = fsService.saveUploadedFile(file);
        } catch (Exception ex) {
//            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
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

        if (null != fileObject && tempBlobObject != null) {
            V2_BlobObject blobObject = tempBlobObject;
            blobObject.setObjectName((String) fileObject.get("objectName"));
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            return blobDao.save(blobObject);
        } else {
            return null;
        }

    }

    public V2_BlobObject uploadFile(InputStream input,String fileName) {
        
        String path = "";
        try {
            path = fsService.saveUploadedFile(input,fileName);
        } catch (Exception ex) {
//            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
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
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            return blobDao.save(blobObject);
        } else {
            return null;
        }

    }
    
    public V2_BlobObject uploadFileString(String str) {
        String path = "";
        try {
            path = fsService.saveFileString(str);
        } catch (Exception ex) {
//            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
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
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
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
    
    public void deleteBlobFile(List<V2_BlobObject> listBlobObject){
    	if(listBlobObject!=null && listBlobObject.size()>0){
    		for(int i=0;i<listBlobObject.size();++i){
    			deleteBlobFile(listBlobObject.get(i));
    		}
    	}
    }


    public List<V2_BlobObject> getBlobObjectsByStorageId(String storagedId) {
        return blobDao.findAllByObjectStorageId(storagedId);
    }
    
    public List<V2_BlobObject> getBlobObjectsByStorageId(Set<String> storagedId) {
        return blobDao.findAllByObjectStorageId(storagedId);
    }

    public List<V2_BlobObject> findBlobObjectsByBoUid(String uid){
        return blobDao.findByBoUuid(uid);
    }
    public List<V2_BlobObject> findBlobObjectsByBoUids(Set<String> uid){
        List<SearchFilter> searchFilters=new ArrayList<>();
        searchFilters.add(new SearchFilter("boUuid", SearchFilter.Operator.IN,uid));
        return blobDao.findBySearchFilter(searchFilters);
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
            blobDao.delete(blobObject);
            return true;
        }
    }
    
    public Boolean deleteBlobFileByBoUuid(String boUuid){
        List<V2_BlobObject> listBlobObject = blobDao.findByBoUuid(boUuid);
        if(listBlobObject==null){
            return null;
        }
        if(listBlobObject!=null && listBlobObject.size()>0){
            for(int i=0;i<listBlobObject.size();++i){
            	deleteBlobFile(listBlobObject.get(i));
            }
        }
        return true;
    }
    
    public Boolean deleteBlobFileByBoUuidAndType(String boUuid,String boType,String subType){
        List<V2_BlobObject> listBlobObject = blobDao.fetchBlobObjectByUuidAndType(boUuid,boType,subType);
        if(listBlobObject==null){
            return null;
        }
        if(listBlobObject!=null && listBlobObject.size()>0){
            for(int i=0;i<listBlobObject.size();++i){
            	deleteBlobFile(listBlobObject.get(i));
            }
        }
        return true;
    }
    
    public List<V2_BlobObject> fetchBlobObject(String boUuid,String boType,String subType){
    	return blobDao.fetchBlobObjectByUuidAndType(boUuid, boType, subType);
    }
    
    public List<V2_BlobObject> fetchBlobObject(String boUuid,String boType){
    	return blobDao.fetchBlobObjectByUuidAndType(boUuid, boType);
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
    
    public Boolean deleteBlobFilesByStorageId(Set<String> setObjectIds) {
        List<V2_BlobObject> objects = getBlobObjectsByStorageId(setObjectIds);
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
    
    public void saveBlobObject(List<V2_BlobObject> blobObjects){
        blobDao.save(blobObjects);
    }

    public void updateBlobLock(String blobId, Boolean lock) {
        blobDao.updateBlobLock(blobId, lock);
    }


    public StreamedContent getBlobObjectByStorageId(String storageId){
        return microService.downloadSingleFile(storageId);
    }


    public V2_BlobObject compressByObjectId( String objectStorageId ) {
        Map<String, Object> fileObject = microService.compressByObjectId(objectStorageId);
        if (null != fileObject) {
            V2_BlobObject blobObject = new V2_BlobObject();
            blobObject.setObjectName((String) fileObject.get("objectName"));
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            return blobObject;
        } else {
            return null;
        }
    }

    public V2_BlobObject uploadCompressFile(UploadedFile file) {

        String path = "";
        try {
            path = fsService.saveUploadedFile(file);
        } catch (Exception ex) {
//            WebUtil.addErrorMessage("Upload file exception" + ex.getMessage());
            Logger.getLogger(AssetFileAttachmentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        String token = UserContextService.getAccessToken();
        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have premission");
            return null;
        }

        Map<String, Object> fileObject = microService.uploadSingleCompressFile(token, new File(path));
        fsService.deleteUploadedFile(path);

        if (null != fileObject) {
            V2_BlobObject blobObject = new V2_BlobObject();
            blobObject.setObjectName((String) fileObject.get("objectName"));
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            return blobDao.save(blobObject);
        } else {
            return null;
        }

    }

    public void sendImageCompressMQ(V2_BlobObject blobObject){
        if(null == blobObject){
            return;
        }
        MqContext mqContext = new MqContext();
        mqContext.setTime(new Date());
        mqContext.setType("imageCompress");
        mqContext.setRoutingKey(RabbitConfig.IMAGE_COMPRESS_ROUTING_KEY);
        mqContext.setExChange(RabbitConfig.IMAGE_COMPRESS_CHANGE);

        List<V2_BlobObject> list = new ArrayList<>();
        list.add(blobObject);
        JSONObject jsonObject = new JSONObject();
        String str = JSON.toJSONString(list, SerializerFeature.WriteDateUseDateFormat);
        jsonObject.put("blobObject", str);
        mqContext.setJsonObject(jsonObject);

        mqUtils.sendToMq(mqContext);
    }
}
