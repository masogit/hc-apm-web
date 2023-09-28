package com.ge.apm.dao.mapper;

import com.ge.apm.domain.his.HisEquipmentDataSource;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HisEquipmentDataSourceMapper {



    @Select("select org_code,max(exec_time)  exec_time from his_equipment_data_source group by org_code")
    List<HisEquipmentDataSource> selectByOrgCodeAndExecTime();
}
