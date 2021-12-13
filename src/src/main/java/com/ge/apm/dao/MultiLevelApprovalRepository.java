package com.ge.apm.dao;

import com.ge.apm.domain.MultiLevelApproval;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 503189828
 */
public interface MultiLevelApprovalRepository extends GenericRepositoryUUID<MultiLevelApproval> {

    @Query("select m from MultiLevelApproval m  where m.initiateApproverUid = ?1 Order By m.createdDate DESC")
    public List<MultiLevelApproval> getByInitiateApproverUid(String initiateApproverUid);

    @Query("select m from MultiLevelApproval m  where m.businessNumber = ?1 Order By m.createdDate ASC")
    public List<MultiLevelApproval> getByBusinessNumberList(String businessNumber);

    @Query("select m from MultiLevelApproval m  where m.approvalNumber = ?1 Order By m.createdDate ASC")
    public List<MultiLevelApproval> getByApprovalNumberList(String approvalNumber);

    @Query("select m from MultiLevelApproval m  where m.actualApproverUid = ?1 and m.approvalNodeStatus != ?2 Order By m.createdDate DESC ")
    public List<MultiLevelApproval> getMultiLevelApprovalList(String actualApproverUid, Integer approvalNodeStatus);

    @Query(value=
            "SELECT MAX (A .approva_number) FROM ( SELECT MAX ( to_number( B.approva_number, '99999999999999' )) approva_number FROM multi_level_approval B WHERE B.approva_number IN ( SELECT C .approva_number FROM multi_level_approval C WHERE C .approva_number LIKE ?1% )) A",nativeQuery = true)
    String selectMaxApprovalNum(String format);
}
