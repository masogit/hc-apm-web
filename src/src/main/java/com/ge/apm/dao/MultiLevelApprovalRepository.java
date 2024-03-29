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

    @Query("select m from MultiLevelApproval m  where m.businessUid = ?1 Order By m.createdDate ASC")
    public List<MultiLevelApproval> getByBusinessUidList(String businessUid);

    @Query("select m from MultiLevelApproval m  where m.approvalNumber = ?1 Order By m.createdDate ASC")
    public List<MultiLevelApproval> getByApprovalNumberList(String approvalNumber);

    @Query("select m from MultiLevelApproval m  where m.actualApproverUid = ?1 and m.approvalNodeStatus != ?2 Order By m.createdDate DESC ")
    public List<MultiLevelApproval> getMultiLevelApprovalList(String actualApproverUid, Integer approvalNodeStatus);

    @Query(value=
            "SELECT MAX (A .approva_number) FROM ( SELECT MAX ( to_number( B.approva_number, '99999999999999' )) approva_number FROM multi_level_approval B WHERE B.approva_number IN ( SELECT C .approva_number FROM multi_level_approval C WHERE C .approva_number LIKE ?1% )) A",nativeQuery = true)
    String selectMaxApprovalNum(String format);

    public List<MultiLevelApproval> findByBusinessUid(String businessUid);

    public List<MultiLevelApproval> findByBusinessUidIn(List<String> woId);

    @Query(value = "select count(m.id) from multi_level_approval m  where m.workflow_config_approve_uid = ?1 and ( m.approval_status = 0 or m.approval_status = 1   ) ",nativeQuery = true)
    int getIncompleteApprovalConfig(String id);


    @Query(value = "select   count(ma.*) from multi_level_approval ma left join asset_scrap  scrap on ma.business_number=scrap.business_number \n" +
            "left join asset_department_convert adc   on ma.business_number=adc.business_number \n" +
            "left join v2_service_request sr on ma.business_number=sr.sr_num \n" +
            "left join v2_pm_order po on ma.business_uid=po.id\n" +
            "where ma.tenant_uid=?1 and ma.initiate_approver_uid is not null and  (scrap.asset_uid=?2 or  adc.asset_uid=?2 or sr.asset_uid=?2  or po.asset_uid= ?2  )\n",nativeQuery = true)
    int getAssetProcessRecord(String tenantUID, String uid);


    @Query(value = "select   ma.* from multi_level_approval ma left join asset_scrap  scrap on ma.business_number=scrap.business_number \n" +
            "left join asset_department_convert adc   on ma.business_number=adc.business_number \n" +
            "left join v2_service_request sr on ma.business_number=sr.sr_num \n" +
            "left join v2_pm_order po on ma.business_uid=po.id\n" +
            "where ma.tenant_uid=?1 and ma.initiate_approver_uid is not null and  (scrap.asset_uid=?2 or  adc.asset_uid=?2 or sr.asset_uid=?2  or po.asset_uid= ?2 ) limit ?3 offset ?4   \n",nativeQuery = true)
    List<MultiLevelApproval> getAssetProcessRecordPage(String tenantUID, String uid, Integer pageSize, Integer pageNum);
}
