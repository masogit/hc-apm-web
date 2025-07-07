package com.ge.apm.dao;

import com.ge.apm.domain.EquipmentExpiryManagement;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface EquipmentExpiryManagementRepository extends GenericRepository<EquipmentExpiryManagement> {

    List<EquipmentExpiryManagement> findByRegisterNo(String registerNo);

    List<EquipmentExpiryManagement> findByManufactureAndAndFunctionType(String manufacture, String functionType);

}
