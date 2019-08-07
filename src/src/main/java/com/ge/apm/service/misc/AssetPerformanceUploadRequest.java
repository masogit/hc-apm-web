package com.ge.apm.service.misc;

import com.ge.apm.domain.SmallAssetPerformance;

/**
 *
 * @author 212547631
 */
public class AssetPerformanceUploadRequest {
    private String edgeServerKey;
    private SmallAssetPerformance smallAssetPerformance;

    public String getEdgeServerKey() {
        return edgeServerKey;
    }

    public void setEdgeServerKey(String edgeServerKey) {
        this.edgeServerKey = edgeServerKey;
    }

    public SmallAssetPerformance getSmallAssetPerformance() {
        return smallAssetPerformance;
    }

    public void setSmallAssetPerformance(SmallAssetPerformance smallAssetPerformance) {
        this.smallAssetPerformance = smallAssetPerformance;
    }
}
