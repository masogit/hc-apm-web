package com.ge.apm.service.misc;

import com.ge.apm.domain.SmallAssetPerformance;

/**
 *
 * @author 212547631
 */
public class AssetPerformanceUploadRequest {
    private String edgeServerKey;
    private SmallAssetPerformance performanceRecord;

    public String getEdgeServerKey() {
        return edgeServerKey;
    }

    public void setEdgeServerKey(String edgeServerKey) {
        this.edgeServerKey = edgeServerKey;
    }

    public SmallAssetPerformance getPerformanceRecord() {
        return performanceRecord;
    }

    public void setPerformanceRecord(SmallAssetPerformance performanceRecord) {
        this.performanceRecord = performanceRecord;
    }
}
