package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetInfoParentAssetService {

    @Autowired
    private AssetInfoRepository assetInfoDao;

    public String getParentAssetNameByAssetId(int assetId) {
        final String DEFAULT_PARENT_ASSET_NAME = "";

        AssetInfo ai = assetInfoDao.findById(assetId);
        if (null == ai || null == ai.getParentAssetId()) {
            return DEFAULT_PARENT_ASSET_NAME;
        }

        AssetInfo parentAsset = assetInfoDao.getById(ai.getParentAssetId());

        if (null == parentAsset) {
            return DEFAULT_PARENT_ASSET_NAME;
        }

        if (StringUtils.isEmpty(parentAsset.getFunctionType())) {
            return parentAsset.getName();
        } else {
            return parentAsset.getName() + "(" + parentAsset.getFunctionType() + ")";
        }

    }
}
