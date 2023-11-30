package com.ge.apm.dao;

import com.ge.apm.domain.MultiLevelApprovalConfig;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 503189824
 */
public interface MultiLevelApprovalConfigRepository extends GenericRepositoryUUID<MultiLevelApprovalConfig> {

    @Query(value="select m.* from multi_level_approval_config m where m.hier_org_uid=?1 and m.business_type =?2 and m.enable = true", nativeQuery = true)
    public List<MultiLevelApprovalConfig> getConfigByCondition(String hierOrgUid, String busiType);

    public List<MultiLevelApprovalConfig>  findByHierOrgUidAndBusinessTypeAndEnable(String hierOrgUid,String businessType,Boolean enable);

}
