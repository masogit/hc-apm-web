package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface ZkbPartOrderRepository extends GenericRepositoryUUID<ZkbPartOrder> {

    @Query("select max(p.partOrderIndex) from ZkbPartOrder p where p.partOrderNo like ?1 ")
    Object getMaxIndex(String dateCondition);

    /**
     * 根据多个合同ID，获取总申请数
     * @param contractIdList
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_part_order where contract_id in ?1", nativeQuery = true)
    Integer fetchQty(List<String> contractIdList);

    @Query(value = "SELECT count(*) from zkb_part_order where current_step = ?1", nativeQuery = true)
    Integer fetchQtyByStep(String currentStep);

    @Query(value = "SELECT count(*) from zkb_part_order where current_step = ?1 and tenant_uid = ?2 and data_type is null", nativeQuery = true)
    Integer fetchQtyByStep(String currentStep,String tenantUid);
    @Query(value = "SELECT count(*) from zkb_part_order where current_step = ?1 and tenant_uid = ?2 and data_type = 'test.'", nativeQuery = true)
    Integer fetchQtyByStepForTest(String currentStep,String tenantUid);

    @Query(value = "SELECT count(*) from zkb_part_order where current_step in ?1 and tenant_uid = ?2 and data_type is null", nativeQuery = true)
    Integer fetchQtyBySteps(List<String> currentSteps,String tenantUid);
    @Query(value = "SELECT count(*) from zkb_part_order where current_step in ?1 and tenant_uid = ?2 and data_type = 'test.'", nativeQuery = true)
    Integer fetchQtyByStepsForTest(List<String> currentSteps,String tenantUid);


    @Query(value = "SELECT sum(qty) from zkb_part_order where contract_id = ?1", nativeQuery = true)
    Integer fetchQty(String contractId);

    /**
     * 根据多个合同ID，获取总申请数
     * @param contractIdList
     * @return
     */
    @Query(value = "SELECT sum(return_qty) from zkb_part_order where contract_id in ?1 and return_type='goodReturn'", nativeQuery = true)
    Integer fetchReturnGoodQty(List<String> contractIdList);

    /**
     * 设备ID
     * @param contractIdList 设备ID
     * @param firstday 开始时间
     * @param lastday 结束时间
     * @return
     */
    @Query(value = "SELECT count(id)  from zkb_part_order  where contract_id in ?1 and ordered_date between ?2 and ?3", nativeQuery = true)
    Integer fetchCount(List<String> contractIdList, Date firstday, Date lastday);



    @Query(value = "select count(1) as totalTimes, ai.asset_owner_id,ai.asset_owner_id2,ai.asset_owner_id3 from asset_info ai where ai.asset_owner_id=:assetOwnerId and ai.site_uid=:siteUID "
            + "group by ai.asset_owner_id2,ai.asset_owner_id3,ai.asset_owner_id order by totalTimes desc limit 1",nativeQuery = true)
    public List<Object[]> queryZkbHistoryData(@Param("assetOwnerId") Integer assetOwnerId, @Param("siteUID") String siteUID);


    /**
     * 获取探头未归还
     * @param orderedDate 时间
     * @param orderType 订单类型
     * @param tenantUID 租户tenantUID
     * 探头多了400开单拒绝  技术审批拒绝  经理拒绝  400下单拒绝  commercial拒绝  400取消派工这个是人工的状态 总监审批拒绝
     * @return
     */
    @Query(value = "SELECT count(zkbp.id) from zkb_part_order zkbp INNER JOIN zkb_contract zkbc on zkbp.contract_id = zkbc.id where  zkbp.order_type = ?2 and zkbp.ordered_date< ?1 " +
            "AND (zkbp.status!='Closed' and zkbp.status != 'UnOrderedReject' and zkbp.status != 'TechnicalReject' and zkbp.status != 'ManageReject' " +
            "and zkbp.status != 'OrderedReject' and zkbp.status != 'CommercialReject' and zkbp.status != 'FourHundredCancel' " +
            "and zkbp.status != 'DirectorReject' and zkbp.status != 'ReturnCompleted' and zkbp.status != 'CloseCommited' and zkbp.status != 'CloseApproved' " +
            "and zkbp.status != 'CloseFailed' and zkbp.status != 'PartReturned') and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd') and zkbc.end_date>=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd')", nativeQuery = true)
    Integer fetchUnReturnCount(Date orderedDate,String orderType,String tenantUID);

    /**
     * CCSB类型合同不计算在内获取探头未归还
     * @param orderedDate 时间
     * @param orderType 订单类型
     * @param tenantUID 租户tenantUID
     * 探头多了400开单拒绝  技术审批拒绝  经理拒绝  400下单拒绝  commercial拒绝  400取消派工这个是人工的状态 总监审批拒绝
     * @return
     */
    @Query(value = "SELECT coalesce(count(zkbp.id),0) from zkb_part_order zkbp INNER JOIN zkb_contract zkbc on zkbp.contract_id = zkbc.id where zkbc.contract_type <> 'CCSB' and zkbp.order_type = ?2 and zkbp.ordered_date< ?1 " +
            "AND (zkbp.status!='PartReturned' and zkbp.status!='Returned' and zkbp.status!='Closed' and zkbp.status != 'UnOrderedReject' and zkbp.status != 'TechnicalReject' and zkbp.status != 'ManageReject' and zkbp.status != 'OrderedReject' and zkbp.status != 'CommercialReject' and zkbp.status != 'FourHundredCancel' and zkbp.status != 'DirectorReject') and zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd') and zkbc.end_date>=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd')", nativeQuery = true)
    Integer fetchUnReturnCountOfCCSB(Date orderedDate, String orderType, String tenantUID);



    /**
     * 获取备件申请数量
     * @param orderedDate 下单时间
     * @param orderType 订单类型
     * @param tenantUID 租户tenant uid
     * @return
     */
    @Query(value = "SELECT sum(qty) from zkb_part_order zkbp INNER JOIN zkb_contract zkbc on zkbp.contract_id = zkbc.id where zkbp.order_type = ?2 and zkbp.ordered_date< ?1 and " +
            "zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd')  and zkbc.end_date>=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd') " +
            " and zkbp.status != 'TechnicalReject' and zkbp.status != 'ManageReject' and zkbp.status != 'OrderedReject' and zkbp.status != 'CommercialReject' and zkbp.status != 'FourHundredCancel' and zkbp.status != 'DirectorReject'", nativeQuery = true)
    Integer fetchApplyPartsQty(Date orderedDate, String orderType, String tenantUID);

    /**
     * ccsb类型的合同数据在生成积分的时候需要剔除 获取备件申请数量
     * @param orderedDate 下单时间
     * @param orderType 订单类型
     * @param tenantUID 租户tenant uid
     * @return
     */
    @Query(value = "SELECT coalesce(sum(qty),0) from zkb_part_order zkbp INNER JOIN zkb_contract zkbc on zkbp.contract_id = zkbc.id where zkbc.contract_type <> 'CCSB' and zkbp.order_type = ?2 and zkbp.ordered_date< ?1 and " +
            "zkbc.tenant_uid = ?3 and zkbc.start_date<=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd')  and zkbc.end_date>=to_date(to_char(zkbp.ordered_date, 'yyyy-MM-dd'),'yyyy-MM-dd') " +
            " and zkbp.status != 'TechnicalReject' and zkbp.status != 'ManageReject' and zkbp.status != 'OrderedReject' and zkbp.status != 'CommercialReject' and zkbp.status != 'FourHundredCancel' and zkbp.status != 'DirectorReject'", nativeQuery = true)
    Integer fetchApplyPartsQtyOfCCSB(Date orderedDate, String orderType, String tenantUID);

    @Query(value = "SELECT * from zkb_part_order where order_type = ?3 and ordered_date< ?1  and contract_id in ?2 and (status = 'PartReturned' or status = 'Returned' or status = 'Closed')", nativeQuery = true)
    List<ZkbPartOrder> findReturnId(Date orderedDate, List<String> contractIdList, String orderType);

    @Query(value = "SELECT * from zkb_part_order where online_engineer = ?1", nativeQuery = true)
    List<ZkbPartOrder> findByOnline(String zkbPartOrderUserConfigId);

    /**
     * 根据租户uid 查找
     * @param tenantUid
     * @return
     */
    @Query(value = "SELECT * from zkb_part_order where tenant_uid = ?1  order by request_date desc ", nativeQuery = true)
    List<ZkbPartOrder> findByTenantUIDOrderByRequestDateDesc(String tenantUid);

    @Query(value = "SELECT * from zkb_part_order order by request_date desc ", nativeQuery = true)
    List<ZkbPartOrder> findAllPartOrder();

    @Query(value = "SELECT * from zkb_part_order where asset_id = ?1 and id != ?2 order by created_date desc LIMIT 1 ", nativeQuery = true)
    ZkbPartOrder findByAssetIdTopOne(String assetId, String id);

    @Query(value = "select  t.name from user_account t inner join tenant_info tt ON t.tenant_uid=tt.uid where tt.type='3' and t.login_name like 'test.%' ", nativeQuery = true)
    List<String> getZkbTestName();

    @Query(value = "SELECT * from zkb_part_order where sr_no = ?1 and current_step in ('CompletePending', 'CompleteAuditPending') and id != ?2 ", nativeQuery = true)
    List<ZkbPartOrder> queryBySrNoExceptStatus(String srNo, String partOrderId);


    @Query(value = "SELECT * from zkb_part_order where sr_no = ?1 and id != ?2 ", nativeQuery = true)
    List<ZkbPartOrder> queryBySrNo(String srNo, String partOrderId);
}

