package com.ge.apm.dao;
  
import com.ge.apm.domain.MvsIbMapping;
import com.ge.apm.service.uaa.MvsIbMappingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;  
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MvsIbMappingRepository extends JpaRepository<MvsIbMapping, String> {
  
    @Query(value = "SELECT " +  
                   "ai.id AS ib_id, " +
                   "ai.system_id AS ib_system_id, " +
                   "mvs.system_id AS mvs_system_id, " +  
                   "ai.system_num1 AS ib_systemnum, " +
                   "mvs.qr_code AS mvs_qrcode, " +  
                   "mvs.id AS mvs_ib_id, " +
                   "ti.name AS ib_tenant_name ," +
                   "ti.uid AS ib_tenant_uid " +
            "FROM asset_info ai, asset_info mvs, tenant_info ti " +
                   "WHERE mvs.tenant_uid = :tenantUid AND ai.tenant_uid != :tenantUid " +  
                   "AND ai.tenant_uid = ti.uid " +  
                   "AND (mvs.qr_code = SUBSTRING(ai.system_id, 5) AND ai.system_id LIKE 'MVS-%' " +  
                   "OR mvs.qr_code = SUBSTRING(ai.system_num1, 5) AND ai.system_num1 LIKE 'MVS-%')",  
           nativeQuery = true)
    List<MvsIbMappingDto> findMvsIbMappingByTenantUid(@Param("tenantUid") String tenantUid);
}