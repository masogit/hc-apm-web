package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/8
 * @descript 收据Repository
 * @package com.ge.apm.dao
 */
public interface SimplePartsRequestRepository extends GenericRepositoryUUID<SimplePartsRequest>, JpaRepository<SimplePartsRequest, String> {

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequest>
     * @params [type, status]
     * @author csh
     * @createTime 2020/6/15 14:53
     * @description 根据单据类型和状态查询
     */
    @Query(value = "select * from simple_parts_request where type = ?1 and status = ?2", nativeQuery = true)
    List<SimplePartsRequest> findByTypeAndStatus(Integer type, Integer status);

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequest>
     * @params [requestCode]
     * @author csh
     * @createTime 2020/6/17 17:51
     * @description 根据订单号查询
     */
    @Query(value = "select * from simple_parts_request where request_code = ?1", nativeQuery = true)
    List<SimplePartsRequest> findByRequestCode(String requestCode);

    @Query(value=
            "SELECT MAX (A .request_code) FROM ( SELECT MAX ( to_number( B.request_code, '99999999999999' )) request_code FROM simple_parts_request B WHERE B.request_code IN ( SELECT C .request_code FROM simple_parts_request C WHERE C .request_code LIKE ?1% AND C .type = ?2)) A",nativeQuery = true)
    String fetchMaxAssetFinacingNum(String format,Integer type);

    @Query(value = "select * from simple_parts_request where maintenance_work_order_number LIKE ?1%", nativeQuery = true)
    List<SimplePartsRequest> findByMaintenanceWorkOrderNumberLike(String maintenanceWorkOrderNumber);
}
