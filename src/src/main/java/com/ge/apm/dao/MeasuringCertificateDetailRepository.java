package com.ge.apm.dao;

import com.ge.apm.domain.MeasuringCertificateDetail;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;

/**
 * Created by 212605082 on 2017/11/7.
 */
public interface MeasuringCertificateDetailRepository extends GenericRepositoryUUID<MeasuringCertificateDetail> {

    public MeasuringCertificateDetail findByCertificateNoAndTenantUID(String certificateNo, String tenantUID);

    public MeasuringCertificateDetail findByAssetSNAndCheckDateAndTenantUID(String assetSN, Date checkDate, String tenantUID);

    public MeasuringCertificateDetail findByFinanceNoAndCheckDateAndTenantUID(String financeNo, Date checkDate, String tenantUID);

    public MeasuringCertificateDetail findByAssetUIDAndCheckDateAndTenantUID(String assetUID, Date checkDate, String tenantUID);
}
