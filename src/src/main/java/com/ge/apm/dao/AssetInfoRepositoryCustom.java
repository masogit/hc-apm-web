package com.ge.apm.dao;

import com.ge.apm.service.analysis.FieldFillRateDTO;

public interface AssetInfoRepositoryCustom {

    FieldFillRateDTO calculateFieldFillRate(String fieldName,
                                            String tenantUid,
                                            Integer level,
                                            String levelUid,
                                            String selectSite,
                                            String clinicalDeptUid,
                                            boolean ignoreEmptyString);
}
