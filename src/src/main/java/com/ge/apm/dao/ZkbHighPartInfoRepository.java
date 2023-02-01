package com.ge.apm.dao;


import com.ge.apm.domain.ZkbHighPartInfo;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/16.
 */
public interface ZkbHighPartInfoRepository extends GenericRepositoryUUID<ZkbHighPartInfo> {



    List<ZkbHighPartInfo> findByPartNo(String partNo);
    ZkbHighPartInfo findFirstByPartNo(String partNo);
}
