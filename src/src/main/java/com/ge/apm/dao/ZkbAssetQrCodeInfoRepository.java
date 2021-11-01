package com.ge.apm.dao;

import com.ge.apm.domain.ZkbAssetQrCodeInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author @author 503202755 2021/3/4
 */
public interface ZkbAssetQrCodeInfoRepository extends GenericRepositoryUUID<ZkbAssetQrCodeInfo> {

    /**
     * 根据systemId获取无法粘贴二维码
     * @param systemId 设备systemId
     * @return
     */
    List<ZkbAssetQrCodeInfo> findBySystemId(String systemId);

    /**
     * 设备粘贴状态
     * @param tenantUID 供应商tenant UID
     * @param canNotPastQrCode 是否可以粘贴二维码
     * @return
     */
    List<ZkbAssetQrCodeInfo> findByTenantUIDAndCanNotPastQrCode(String tenantUID,String canNotPastQrCode);

    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id where ai.uid IN ?1 and zkba.tenant_uid= ?2 and zkba.can_not_past_qr_code=?3 ", nativeQuery = true)
    Integer findCount(List<String> uidList,String tenantUID,String canNotPastQrCode);

    @Query(value = "SELECT COUNT(bo_uuid) AS qr_code_count FROM v2_blob_object WHERE bo_sub_type = '1' AND bo_uuid =?1 GROUP BY bo_uuid", nativeQuery = true)
    Integer findAttachedCount(String assetUID);

    @Transactional
    @Modifying
    @Query(value = "delete from zkb_asset_qr_code_info qr where qr.system_id not in(select ai.system_id from asset_info ai,zkb_contract_asset zca,zkb_contract ac where qr.system_id=ai.system_id and ai.uid= zca.asset_id and zca.contract_id=ac.id AND ai.is_deleted=false AND ai.is_valid=true AND ac.start_date<=?1 and ac.end_date>=?1)", nativeQuery = true)
    void deleteZkbAssetQrCodeInfo(Date currentDate);
}
