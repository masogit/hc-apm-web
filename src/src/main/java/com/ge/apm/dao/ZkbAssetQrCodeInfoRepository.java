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


    List<ZkbAssetQrCodeInfo> findByTenantNameAndSystemId(String TenantName,String SystemId);

    /**
     * 二维码是否已实施 or 是否设备无法粘贴二维码
     * 已经贴码设备
     * @param uidList
     * @param tenantUID
     * @return
     */
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id WHERE zkba.qr_code_count_state = 'Y' and ai.uid IN ?1 and zkba.tenant_uid= ?2 ", nativeQuery = true)
    Integer qrCodePasteCount(List<String> uidList,String tenantUID);

    /**
     * 二维码是否已实施 or 是否设备无法粘贴二维码
     * 已经贴码设备
     * @param uidList
     * @param tenantUID
     * @return
     */
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id  WHERE not exists(select 1 from zkb_contract zctccsb INNER JOIN zkb_contract_asset zcaccsb on zcaccsb.contract_id = zctccsb.id where zctccsb.contract_type = 'CCSB' and zcaccsb.asset_id=zkba.asset_uid ) and zkba.qr_code_count_state = 'Y' and ai.uid IN ?1 and zkba.tenant_uid= ?2 ", nativeQuery = true)
    Integer qrCodePasteCountOfCCSB(List<String> uidList,String tenantUID);

    /**
     * 贴码状态的拆机设备数量
     * @param uidList
     * @param tenantUID
     * @return
     */
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id WHERE zkba.qr_code_count_state = 'Y'  and exists(select 1 from zkb_asset_qr_code_info zkbat where zkba.id = zkbat.id and (zkbat.install_status = '2' or zkbat.reason_categorize = 'scrapped')) and ai.uid IN ?1 and zkba.tenant_uid= ?2 ", nativeQuery = true)
    Integer qrCodePasteScrappedCount(List<String> uidList,String tenantUID);
    /**
     * 贴码状态的拆机设备数量
     * @param uidList
     * @param tenantUID
     * @return
     */
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id WHERE not exists(select 1 from zkb_contract zctccsb INNER JOIN zkb_contract_asset zcaccsb on zcaccsb.contract_id = zctccsb.id where zctccsb.contract_type = 'CCSB' and zcaccsb.asset_id=zkba.asset_uid ) and zkba.qr_code_count_state = 'Y'  and exists(select 1 from zkb_asset_qr_code_info zkbat where zkba.id = zkbat.id and (zkbat.install_status = '2' or zkbat.reason_categorize = 'scrapped')) and ai.uid IN ?1 and zkba.tenant_uid= ?2 ", nativeQuery = true)
    Integer qrCodePasteScrappedCountOfCCSB(List<String> uidList,String tenantUID);

    /**
     * 所有的拆机设备数量
     * @param uidList
     * @param tenantUID
     * @return
     */
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id WHERE  exists(select 1 from zkb_asset_qr_code_info zkbat where zkba.id = zkbat.id and (zkbat.install_status = '2' or zkbat.reason_categorize = 'scrapped')) and ai.uid IN ?1 and zkba.tenant_uid= ?2 ", nativeQuery = true)
    Integer scrappedCount(List<String> uidList,String tenantUID);

    /**
     * 所有的拆机设备数量
     * @param uidList
     * @param tenantUID
     * @return
     */
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id WHERE not exists(select 1 from zkb_contract zctccsb INNER JOIN zkb_contract_asset zcaccsb on zcaccsb.contract_id = zctccsb.id where zctccsb.contract_type = 'CCSB' and zcaccsb.asset_id=zkba.asset_uid ) and  exists(select 1 from zkb_asset_qr_code_info zkbat where zkba.id = zkbat.id and (zkbat.install_status = '2' or zkbat.reason_categorize = 'scrapped')) and ai.uid IN ?1 and zkba.tenant_uid= ?2 ", nativeQuery = true)
    Integer scrappedCountOfCCSB(List<String> uidList,String tenantUID);
    @Query(value = "SELECT count(zkba.id) from zkb_asset_qr_code_info zkba INNER JOIN asset_info ai on zkba.system_id = ai.system_id where ai.uid IN ?1 and zkba.tenant_uid= ?2 and zkba.can_not_past_qr_code=?3 ", nativeQuery = true)
    Integer findCount(List<String> uidList,String tenantUID,String canNotPastQrCode);

    @Query(value = "SELECT COUNT(bo_uuid) AS qr_code_count FROM v2_blob_object WHERE bo_sub_type = '1' AND bo_uuid =?1 GROUP BY bo_uuid", nativeQuery = true)
    Integer findAttachedCount(String assetUID);

    @Transactional
    @Modifying
    @Query(value = "delete from zkb_asset_qr_code_info qr where not exists(select 'Y' from asset_info ai,zkb_contract_asset zca,zkb_contract ac where ai.uid= zca.asset_id and zca.contract_id=ac.id AND ai.uid=qr.asset_uid AND ai.is_deleted=false AND ai.is_valid=true AND ac.start_date<= to_date(?1,'yyyyMMdd') and ac.end_date>=to_date(?1,'yyyyMMdd'))", nativeQuery = true)
    void deleteZkbAssetQrCodeInfo(String currentDate);
}
