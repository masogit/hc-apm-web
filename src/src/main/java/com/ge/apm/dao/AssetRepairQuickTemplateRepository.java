package com.ge.apm.dao;

import com.ge.apm.domain.AssetRepairQuickTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface AssetRepairQuickTemplateRepository extends GenericRepositoryUUID<AssetRepairQuickTemplate>, JpaRepository<AssetRepairQuickTemplate, String> {

    @Query(value = "select * from asset_repair_quick_template where template_type = ?1 and asset_group = ?2 and tenant_uid = ?3 and (created_by =?4 or hier_org_uid in ?5)", nativeQuery = true)
    List<AssetRepairQuickTemplate> queryListByTypeAndAssetUid2(Integer templateType, Integer assetGroup, String tenantUid, String loginName, List<String> orgUids);

    @Query(value = "select * from asset_repair_quick_template where template_type = ?1 and asset_group = ?2 and tenant_uid = ?3 and (created_by =?4)", nativeQuery = true)
    List<AssetRepairQuickTemplate> queryListByTypeAndAssetUid(Integer templateType, Integer assetGroup, String tenantUid, String loginName);

    @Query(value = "select * from asset_repair_quick_template where asset_group = ?1 and template_title = ?2 and tenant_uid = ?3", nativeQuery = true)
    List<AssetRepairQuickTemplate> findByTemplateTitleAndAssetGroupAndTenantUID(Integer assetGroup, String templateTitle, String tenantUID);
}
