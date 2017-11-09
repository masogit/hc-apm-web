package com.ge.apm.dao;

import com.ge.apm.domain.MeasuringCertificateDetail;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * Created by 212605082 on 2017/11/7.
 */
public interface MeasuringCertificateDetailRepository extends GenericRepositoryUUID<MeasuringCertificateDetail> {

    public MeasuringCertificateDetail findByCertificateNoAndTenantUID(String certificateNo, String tenantUID);

}
