package com.ge.apm.dao;

import com.ge.apm.domain.MvsAssetInfo;
import webapp.framework.dao.GenericRepository;

public interface MvsAssetInfoRepository extends GenericRepository<MvsAssetInfo> {

    public MvsAssetInfo findByAssetUid(String assetUid);

}
