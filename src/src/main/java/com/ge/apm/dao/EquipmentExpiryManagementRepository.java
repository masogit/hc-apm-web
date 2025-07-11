package com.ge.apm.dao;

import com.ge.apm.domain.EquipmentExpiryManagement;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface EquipmentExpiryManagementRepository extends GenericRepository<EquipmentExpiryManagement> {

    List<EquipmentExpiryManagement> findByRegisterNoOrderByManufactDateDesc(String registerNo);

    @Query(value = "SELECT * FROM equipment_expiry_information WHERE TRIM(keyword) ~* ?1 AND REGEXP_REPLACE(LOWER(TRIM(function_type)), '\\s+', '', 'g') = ?2 ORDER BY manufact_date DESC", nativeQuery = true)
    List<EquipmentExpiryManagement> matchByManufactureAndFunctionType(String manufacture, String functionType);

}
