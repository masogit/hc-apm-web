package com.ge.apm.dao;

import com.ge.apm.domain.EquipmentExpiryManagement;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface EquipmentExpiryManagementRepository extends GenericRepository<EquipmentExpiryManagement> {
    @Query(value = "SELECT distinct on (e.register_no,e.manufact_date) e.* FROM equipment_expiry_information e  where e.register_no = ?1 order by e.manufact_date", nativeQuery = true)
    List<EquipmentExpiryManagement> fetchByRegister(String registerNo);

    @Query(value = "SELECT * " +
            "FROM ( " +
            "    SELECT  " +
            "        distinct on(e.function_type,e.manufacture,e.manufact_date)" +
            "        e.uid,e.manufacture,e.manufacture_name,e.function_type,e.register_no,e.lifecycle,e.manufact_date,e.asset_name,e.version_number,e.version_time,e.version_status,e.keyword,e.keyword1,e.keyword2, " +
            "        ( " +
            "            CASE  " +
            "                WHEN REGEXP_REPLACE(LOWER(TRIM(function_type)), '\\s+', '', 'g') = ?2 THEN 1.0 " +
            "                WHEN REGEXP_REPLACE(TRIM(function_type), '\\s+', '', 'g') ~* ('^' || ?2 || '$') THEN 0.9 " +
            "                WHEN REGEXP_REPLACE(LOWER(TRIM(function_type)), '\\s+', '', 'g') ~ ?2 THEN 0.7 " +
            "                WHEN REGEXP_REPLACE(LOWER(TRIM(function_type)), '\\s+', '', 'g') ~ ('^' || ?2 || '.*') THEN 0.5 " +
            "                ELSE 0.0 " +
            "            END " +
            "        ) AS match_factor " +
            "    FROM equipment_expiry_information e" +
            "    WHERE TRIM(keyword) ~* ?1 " +
            ") AS subquery " +
            "WHERE match_factor > 0.5 " +
            "ORDER BY  " +
            "    match_factor DESC; ", nativeQuery = true)
    List<EquipmentExpiryManagement> matchByManufactureAndFunctionType(String manufacture, String functionType);

}
