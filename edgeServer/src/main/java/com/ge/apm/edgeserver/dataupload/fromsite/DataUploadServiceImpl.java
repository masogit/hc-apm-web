package com.ge.apm.edgeserver.dataupload.fromsite;

import com.ge.apm.edgeserver.dataupload.dao.AssetPerformanceRecordDeltaRepository;
import com.ge.apm.edgeserver.dataupload.entity.AssetPerformanceRecordDelta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ge.apm.edgeserver.dataupload.dao.AssetClinicalDeltaRecordRepository;
import com.ge.apm.edgeserver.dataupload.entity.AssetClinicalRecordDelta;
import com.ge.apm.edgeserver.sysutil.JsonMapper;

import java.util.Map;

import org.apache.camel.Headers;
import org.apache.log4j.Logger;

@Component
public class DataUploadServiceImpl {

    private static final Logger logger = Logger.getLogger(DataUploadServiceImpl.class);

    private static String serverKey = null;

    public void putServerKey(@Headers Map<String, Object> headers) {
        if (serverKey == null) serverKey = ServerKeyUtil.getServerKey();

        headers.put("edgeServerKey", serverKey);
    }

    @Autowired
    private AssetClinicalDeltaRecordRepository dao;

    @Autowired
    private AssetPerformanceRecordDeltaRepository assetPerformanceRecordDeltaRepository;

    public SoapResult uploadAssetClinicalRecord(org.apache.cxf.message.MessageContentsList msgContentList) {
        String strAssetClinicalRecordDelta = msgContentList.get(0).toString();
        JsonMapper mapper = JsonMapper.nonEmptyMapper();
        AssetClinicalRecordDelta assetClinicalRecordDelta = mapper.fromJson(strAssetClinicalRecordDelta, AssetClinicalRecordDelta.class);

        SoapResult result = new SoapResult();
        result.setResultCode("1");
        result.setResultMessage("OK");

        try {
            dao.save(assetClinicalRecordDelta);
        } catch (Exception ex) {
            result.setResultCode("0");
            result.setResultMessage("Error:" + ex.getClass().getSimpleName() + ";" + ex.getMessage());
        }

        return result;
    }

    public SoapResult uploadAssetPerformanceRecord(org.apache.cxf.message.MessageContentsList msgContentList) {
        String strAssetClinicalRecordDelta = msgContentList.get(0).toString();
        JsonMapper mapper = JsonMapper.nonEmptyMapper();
        AssetPerformanceRecordDelta assetPerformanceRecordDelta = mapper.fromJson(strAssetClinicalRecordDelta, AssetPerformanceRecordDelta.class);

        SoapResult result = new SoapResult();
        result.setResultCode("1");
        result.setResultMessage("OK");

        try {
            assetPerformanceRecordDeltaRepository.save(assetPerformanceRecordDelta);
        } catch (Exception ex) {
            result.setResultCode("0");
            result.setResultMessage("Error:" + ex.getClass().getSimpleName() + ";" + ex.getMessage());
        }

        return result;
    }

}
