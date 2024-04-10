package com.ge.apm.service.asset;


import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.PermitInfoRepository;
import com.ge.apm.dao.SpecialAssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.PermitInfo;
import com.ge.apm.domain.SpecialAssetInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class SpecialAssetInfoService {

    @Autowired
    private SpecialAssetInfoRepository specialAssetInfoRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private AssetAnnualInspectionReportService assetAnnualInspectionReportService;

    @Autowired
    private PermitInfoRepository permitInfoRepository;


    @Autowired
    private  SpecialAssetPermitInfoService specialAssetPermitInfoService;

    public void initSpecialAsset(final AssetInfo assetInfo) {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        SpecialAssetInfo specialAssetInfo = new SpecialAssetInfo();
        specialAssetInfo.setAssetId(assetInfo.getId());
        specialAssetInfo.setAssetUid(assetInfo.getUid());
        specialAssetInfo.setHospitalUID(assetInfo.getHospitalUID());
        specialAssetInfo.setTenantUID(assetInfo.getTenantUID());
        specialAssetInfo.setInstitutionUID(assetInfo.getInstitutionUID());
        specialAssetInfo.setSiteUID(assetInfo.getSiteUID());
        specialAssetInfo.setCreatedDate(new Date());
        specialAssetInfo.setCreatedByUid(userAccount.getUid());
        specialAssetInfo.setLastModifiedByUid(userAccount.getUid());
        specialAssetInfo.setLastModifiedDate(new Date());
        specialAssetInfoRepository.save(specialAssetInfo);

    }

    @Transactional
    public void deleteSpecialAssetInfo(SpecialAssetInfo specialAssetInfo) {
        specialAssetInfoRepository.delete(specialAssetInfo.getId());
        assetInfoRepository.updateSpecialAsset(specialAssetInfo.getAssetId(),false);
    }

    public SpecialAssetInfo findByAssetId(Integer id) {

        SpecialAssetInfo specialAssetInfo= specialAssetInfoRepository.findByAssetId(id);
        if(specialAssetInfo!=null){
           List<PermitInfo>  permitInfos= permitInfoRepository.findBySpecialAssetId(specialAssetInfo.getAssetUid());
           specialAssetInfo.setPermitInfos(permitInfos);
        }
        return specialAssetInfo;
    }

    public List<SpecialAssetInfo> findByAssessmentNoAndTenantUID(String assessmentNo, String tenantUID) {

       return specialAssetInfoRepository.findByAssessmentNoAndTenantUID(assessmentNo,tenantUID);
    }

    @Transactional
    public void saveSpecialAsset(SpecialAssetInfo specialAssetInfo) {
        specialAssetPermitInfoService.savePermitInfo(specialAssetInfo.getPermitInfos(),specialAssetInfo.getAssetUid());
        specialAssetInfoRepository.save(specialAssetInfo);
        specialAssetPermitInfoService.saveSpecialAssetPermitInfo(specialAssetInfo.getPermitInfos(),specialAssetInfo.getAssetUid());
        assetAnnualInspectionReportService.saveAssetReport(specialAssetInfo.getAssetAnnualInspectionReports(),specialAssetInfo.getId());

    }
}
