package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderCloseInfo;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author : 503189362
 * @date : 2023/10/24
 * @description :
 */
public interface ZkbPartOrderCloseInfoRepository extends GenericRepositoryUUID<ZkbPartOrderCloseInfo> {

    ZkbPartOrderCloseInfo findFirstBySrNo(String srNo);

}
