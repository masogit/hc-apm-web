package com.ge.apm.dao;

import com.ge.apm.domain.InferenceModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * 推理模型数据访问层
 */
public interface InferenceModelRepository extends GenericRepositoryUUID<InferenceModel> {

    /**
     * 覆写框架的findById，映射到uid字段（InferenceModel主键字段为uid）
     */
    @Override
    @Query(value = "SELECT * FROM inference_model WHERE uid = ?1", nativeQuery = true)
    InferenceModel findById(String id);

    /**
     * 根据UID查找模型
     */
    InferenceModel findByUid(String uid);

    /**
     * 根据租户UID查询模型列表
     */
    List<InferenceModel> findByTenantUid(String tenantUid);

    /**
     * 根据租户UID和模型类型查询
     */
    List<InferenceModel> findByTenantUidAndType(String tenantUid, String type);



    /**
     * 根据租户UID、模型类型和启用状态查询（用于校验同类型只能有一个启用）
     */
    @Query(value = "SELECT * FROM inference_model WHERE tenant_uid = ?1 AND type = ?2 AND enabled = true AND uid != ?3", nativeQuery = true)
    List<InferenceModel> findEnabledByTenantUidAndTypeExcludeUid(String tenantUid, String type, String excludeUid);

    /**
     * 根据租户UID、模型类型和版本号查询（用于校验版本唯一性，排除指定UID）
     */
    @Query(value = "SELECT * FROM inference_model WHERE tenant_uid = ?1 AND type = ?2 AND version = ?3 AND uid != ?4", nativeQuery = true)
    List<InferenceModel> findByTenantUidAndTypeAndVersionExcludeUid(String tenantUid, String type, String version, String excludeUid);

    /**
     * 根据UID删除
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM inference_model WHERE uid = ?1", nativeQuery = true)
    int deleteByUid(String uid);
}
