package com.ge.apm.dao;

import com.ge.apm.domain.V2_WorkOrderManHours;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.Date;
import java.util.List;

public interface V2_WorkOrderManHoursRepository extends GenericRepositoryUUID<V2_WorkOrderManHours> {

    V2_WorkOrderManHours findByManHoursOwnerAndManHoursDateAndWoId(String manHoursOwner, Date manHoursDate, String woId);

    @Query(value = "select * from v2_work_order_man_hours where wo_id=?1 order by man_hours_date desc", nativeQuery = true)
    List<V2_WorkOrderManHours> findByWoIdSort(String woId);
}
