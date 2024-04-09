package com.ge.apm.service.asset;


import com.ge.apm.dao.AssetAnnualInspectionReportRepository;
import com.ge.apm.domain.AssetAnnualInspectionReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetAnnualInspectionReportService {

    @Autowired
    private AssetAnnualInspectionReportRepository repository;

    public void saveAssetReport(List<AssetAnnualInspectionReport> assetAnnualInspectionReports, String id) {
        if(id!=null){
            repository.deleteBySpecialAssetUid(id);
        }
        assetAnnualInspectionReports.stream().forEach(item->{
            item.setSpecialAssetUid(id);
            item.setUid(null);
        });
        repository.save(assetAnnualInspectionReports);

    }




}
