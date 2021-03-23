package com.ge.apm.dao;

import com.ge.apm.domain.ZkbAssetQrCodeInfo;
import webapp.framework.dao.GenericRepositoryUUID;

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

}
