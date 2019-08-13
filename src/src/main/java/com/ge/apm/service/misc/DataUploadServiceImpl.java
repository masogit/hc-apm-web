package com.ge.apm.service.misc;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;

import java.util.List;

import org.springframework.beans.BeanUtils;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

public class DataUploadServiceImpl implements DataUploadService {

    public EdgeServerInfo getEdgeServerInfo(String edgeServerKey, AssetClinicalRecord assetClinicalRecord) throws Exception {
        Integer siteId = assetClinicalRecord.getSiteId();
        return getEdgeServerInfo(edgeServerKey);
    }

    public EdgeServerInfo getEdgeServerInfo(String edgeServerKey) throws Exception {
        EdgeServerInfoRepository dao = WebUtil.getBean(EdgeServerInfoRepository.class);
        EdgeServerInfo edgeServerInfo = dao.getByEdgeServerKey(edgeServerKey);
        if (edgeServerInfo == null) throw new Exception("invalid edgeServerKey");

        return edgeServerInfo;
    }

    public Integer getAssetIdByModalityId(AssetClinicalRecord assetClinicalRecord) throws Exception {
        if (assetClinicalRecord == null || assetClinicalRecord.getModalityId() == null || "".equals(assetClinicalRecord.getModalityId()))
            throw new Exception("invalid modalityId");

        AssetInfoRepository dao = WebUtil.getBean(AssetInfoRepository.class);
        List<AssetInfo> assets = dao.getBySiteIdAndModalityId(assetClinicalRecord.getSiteId(), assetClinicalRecord.getModalityId());

        if (assets.size() > 0)
            return assets.get(0).getId();
        else
            throw new Exception("invalid modalityId");
    }

    public void mapProcedureName(int siteId, int hospitalId, AssetClinicalRecord assetClinicalRecord) throws Exception {
        assetClinicalRecord.setOriginalProcedureStepId(assetClinicalRecord.getProcedureId().toString());
        assetClinicalRecord.setOriginalProcedureStepName(assetClinicalRecord.getProcedureName());

        ProcedureNameMapingRepository mapDao = WebUtil.getBean(ProcedureNameMapingRepository.class);
        List<ProcedureNameMaping> nameList = mapDao.getBySiteIdAndHospitalIdAndRisProcedureName(siteId, hospitalId, assetClinicalRecord.getOriginalProcedureStepName());
        if (nameList.size() > 0) {
            assetClinicalRecord.setProcedureId(nameList.get(0).getApmProcedureId());
            assetClinicalRecord.setProcedureName(nameList.get(0).getApmProcedureName());
        } else {
            //throw new Exception("procedure name not mapped.");
            // mapped to "Else"
            assetClinicalRecord.setProcedureId(5);
            assetClinicalRecord.setProcedureName("其他");
        }
    }


    @Override
    public String uploadAssetClinicalRecord(ClinicalDataUploadRequest dataUploadRequest) throws Exception {
        String edgeServerKey = dataUploadRequest.getEdgeServerKey();
        AssetClinicalRecord assetClinicalRecord = dataUploadRequest.getClinicalRecord();
        try {
            EdgeServerInfo edgeServerInfo = getEdgeServerInfo(edgeServerKey, assetClinicalRecord);

            assetClinicalRecord.setSiteId(edgeServerInfo.getSiteId());
            assetClinicalRecord.setHospitalId(edgeServerInfo.getHospitalId());
            assetClinicalRecord.setAssetId(getAssetIdByModalityId(assetClinicalRecord));
            assetClinicalRecord.setId(null);

            assetClinicalRecord.setOriginalExamDuration(assetClinicalRecord.getExamDuration());
            if (assetClinicalRecord.getExamDuration() > 7200) {
                assetClinicalRecord.setExamDuration(7200);  // exam duration should be no more than 2 hours.
            }

            mapProcedureName(edgeServerInfo.getSiteId(), edgeServerInfo.getHospitalId(), assetClinicalRecord);

            AssetClinicalRecordRepository dao = WebUtil.getBean(AssetClinicalRecordRepository.class);
            dao.save(assetClinicalRecord);
            return "dataUploadedOK";
        } catch (Exception ex) {
            String errMsg = ex.getMessage();
            if (errMsg == null) errMsg = ex.getClass().getSimpleName();

            logToAssetClinicalRecordErrLog(edgeServerKey, assetClinicalRecord, errMsg);

            if (errMsg.contains("ConstraintViolationException"))
                return updateAssetClinicalRecord(assetClinicalRecord);

            throw ex;
        }
    }

    @Override
    public String uploadAssetPerformanceRecord(AssetPerformanceUploadRequest request) throws Exception {
        String edgeServerKey = request.getEdgeServerKey();
        SmallAssetPerformance record = request.getSmallAssetPerformance();
        try {
            EdgeServerInfo edgeServerInfo = getEdgeServerInfo(edgeServerKey);
            OrgInfoRepository orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
            OrgInfo site = orgInfoRepository.findById(edgeServerInfo.getSiteId());
            record.setSiteUID(site.getUid());
            OrgInfo hospital = orgInfoRepository.findById(edgeServerInfo.getHospitalId());
            record.setHospitalUID(hospital.getUid());
            record.setTenantUID(site.getTenantUID());
            record.setId(null);
            SmallAssetPerformanceRepository dao = WebUtil.getBean(SmallAssetPerformanceRepository.class);
            dao.save(record);
        } catch (Exception e) {

        }
        return null;
    }

    public void logToAssetClinicalRecordErrLog(String edgeServerKey, AssetClinicalRecord assetClinicalRecord, String errMsg) {
        AssetClinicalRecordErrlog assetClinicalRecordErrlog = new AssetClinicalRecordErrlog();
        BeanUtils.copyProperties(assetClinicalRecord, assetClinicalRecordErrlog);

        assetClinicalRecordErrlog.setEdgeServerKey(edgeServerKey);
        assetClinicalRecordErrlog.setUploadTime(TimeUtil.now());

        int len = errMsg.length();
        if (len > 255) len = 255;
        assetClinicalRecordErrlog.setErrMsg(errMsg.substring(0, len));

        AssetClinicalRecordErrlogRepository dao = WebUtil.getBean(AssetClinicalRecordErrlogRepository.class);
        dao.save(assetClinicalRecordErrlog);
    }

    public String updateAssetClinicalRecord(AssetClinicalRecord assetClinicalRecord) throws Exception {
        AssetClinicalRecordRepository dao = WebUtil.getBean(AssetClinicalRecordRepository.class);
        List<AssetClinicalRecord> result = dao.getBySiteIdAndHospitalIdAndModalityIdAndSourceRecordIdOrderByExamDateDesc(assetClinicalRecord.getSiteId(), assetClinicalRecord.getHospitalId(), assetClinicalRecord.getModalityId(), assetClinicalRecord.getSourceRecordId());
        if (result.size() < 1) return "dataUploadedOK";

        AssetClinicalRecord existingRecord = result.get(0);
        int id = existingRecord.getId();
        BeanUtils.copyProperties(assetClinicalRecord, existingRecord);
        existingRecord.setId(id);

        dao.save(existingRecord);

        return "dataUploadedOK";
    }

}
