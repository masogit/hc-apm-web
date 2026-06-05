package com.ge.apm.service.model;

import com.ge.apm.dao.InferenceModelRepository;
import com.ge.apm.domain.InferenceModel;
import com.ge.apm.service.asset.BlobFileService;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 推理模型业务逻辑服务层
 */
@Service
public class InferenceModelService {

    @Autowired
    private InferenceModelRepository inferenceModelRepository;

    @Autowired
    private BlobFileService blobFileService;

    /**
     * 根据UID查找模型
     */
    public InferenceModel findByUid(String uid) {
        return inferenceModelRepository.findByUid(uid);
    }

    /**
     * 根据租户UID查询所有模型
     */
    public List<InferenceModel> findByTenantUid(String tenantUid) {
        return inferenceModelRepository.findByTenantUid(tenantUid);
    }

    /**
     * 根据租户UID和模型类型查询
     */
    public List<InferenceModel> findByTenantUidAndType(String tenantUid, String type) {
        return inferenceModelRepository.findByTenantUidAndType(tenantUid, type);
    }

    /**
     * 保存模型
     */
    @Transactional
    public InferenceModel save(InferenceModel model) {
        return inferenceModelRepository.save(model);
    }

    /**
     * 删除模型（含关联Blob文件）
     */
    @Transactional
    public void deleteByUid(String uid) {
        InferenceModel model = inferenceModelRepository.findByUid(uid);
        if (model != null) {
            if (model.getStorageId() != null) {
                blobFileService.deleteBlobFilesByStorageId(model.getStorageId());
            }
            inferenceModelRepository.delete(model);
        }
    }

    /**
     * 获取模型文件流（用于下载）
     */
    public StreamedContent getModelFile(String storageId) {
        return blobFileService.getBlobObjectByStorageId(storageId);
    }

    /**
     * 获取模型文件字节数组
     */
    public byte[] getModelFileBytes(String storageId) throws IOException {
        StreamedContent content = blobFileService.getBlobObjectByStorageId(storageId);
        if (content != null && content.getStream() != null) {
            return IOUtils.toByteArray(content.getStream());
        }
        return null;
    }

    /**
     * 获取模型文件输入流
     */
    public InputStream getModelFileInputStream(String storageId) throws IOException {
        StreamedContent content = blobFileService.getBlobObjectByStorageId(storageId);
        if (content != null) {
            return content.getStream();
        }
        return null;
    }
}
