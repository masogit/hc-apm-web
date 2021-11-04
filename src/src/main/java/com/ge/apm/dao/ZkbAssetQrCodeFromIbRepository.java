package com.ge.apm.dao;


import com.ge.apm.domain.ZkbAssetQrCodeFromIb;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
public interface ZkbAssetQrCodeFromIbRepository extends GenericRepository<ZkbAssetQrCodeFromIb> {


    ZkbAssetQrCodeFromIb findFirstByAssetId(Integer assetId);
    List<ZkbAssetQrCodeFromIb> findByAssetId(Integer assetId);
}
