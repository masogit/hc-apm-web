package com.ge.apm.dao.mapper;

import com.ge.apm.domain.his.HisEquipmentDataSource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface HisEquipmentDataSourceMapper {



    @Select("select org_code,max(exec_time)  exec_time from his_equipment_data_source group by org_code")
    List<HisEquipmentDataSource> selectByOrgCodeAndExecTime();


    @Select("select org_code,max(exec_time)  exec_time from his_equipment_data_source where tenant_uid=#{tenantUid} group by org_code")
    List<HisEquipmentDataSource> selectByTenantUidGroup(@Param("tenantUid") String tenantUid);


    List<HisEquipmentDataSource> queryList(Map<String, Object> map);

    Long selectListCount(Map<String, Object> param);

}
