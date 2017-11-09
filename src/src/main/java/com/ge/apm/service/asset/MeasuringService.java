package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.MeasuringCertificateDetail;
import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.framework.util.StringUtil;

/**
 * Created by 212605082 on 2017/11/7.
 */
public class MeasuringService {

    @Autowired
    private MeasuringCertificateDetailRepository measuringCertificateDetailDao;
    @Autowired
    private AssetInfoRepository assetInfoDao;

    public void operateMeasuringAndAssetInfo(MeasuringCertificateDetail measuringCertificateDetail){
        if(!StringHelper.isNullOrEmptyString(measuringCertificateDetail.getCertificateNo())){
            MeasuringCertificateDetail tempData = measuringCertificateDetailDao.findByCertificateNoAndTenantUID(measuringCertificateDetail.getCertificateNo(), measuringCertificateDetail.getTenantUID());

            if(tempData != null){
                measuringCertificateDetail.setId(tempData.getId());
            }

            measuringCertificateDetailDao.save(measuringCertificateDetail);

            AssetInfo tempAssetInfo = assetInfoDao.findByFinancingNumAndTenantUID(measuringCertificateDetail.getFinanceNo(), measuringCertificateDetail.getTenantUID());

            tempAssetInfo.setNextMeteringDate(measuringCertificateDetail.getValidDateTo());
            tempAssetInfo.setLastMeteringDate(measuringCertificateDetail.getCheckDate());
            tempAssetInfo.setAccountingCategory(measuringCertificateDetail.getStatus());
            if(measuringCertificateDetail.getStatus() == 2){//计量不合格
                tempAssetInfo.setUsageStatus(2);//停用
            }
        }
    }

}
