package com.ge.apm.dao;

import com.ge.apm.domain.SvcEngineLogistics;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface SvcEngineLogisticsRepository extends GenericRepositoryUUID<SvcEngineLogistics> {

    @Query(nativeQuery = true, value = "select distinct s.awb,s.hyper_link,s.carrier,s.carrier_Chinese from svc_engine_logistics s inner join (" +
            "SELECT awb,max(update_time) update_time from svc_engine_logistics where line_ID = ?1 and job = ?2 and awb is not null and awb != '' and awb != 'null' and awb != 'NULL' group by awb ) t " +
            " on s.awb  = t.awb and s.update_time = t.update_time inner join svc_engine_logistics_status st on st.decoded_status = s.decoded_status " +
            " where s.line_ID = ?1 and s.job = ?2 and (s.awb is not null and s.awb  != '' and s.awb  != 'null' and s.awb  != 'NULL') and st.local_status_code is not null ")
    List<Object[]> queryDetailByLineIdFromSql(String lineId,String job);

}
