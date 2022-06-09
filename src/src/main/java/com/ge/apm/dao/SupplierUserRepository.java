package com.ge.apm.dao;

import com.ge.apm.domain.SupplierUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SupplierUserRepository extends GenericRepositoryUUID<SupplierUser> {

    List<SupplierUser> findBySupplierIDAndStatus(Integer supplierID, String status);

    @Query(value ="SELECT user_uid FROM supplier_user WHERE supplier_uid = ?1" ,nativeQuery = true)
    List<String> findSupplierUsersBySupplierUID(String supplierUid);

    List<SupplierUser> findBySupplierIDAndUserIDAndStatus(Integer supplierID, Integer userID, String status);

    List<SupplierUser> findByStatus(String status);

    void deleteBySupplierIDAndStatus(Integer supplierID, String status);

    void deleteBySupplierIDAndStatusAndUserIDIn(Integer supplierID, String status, List<Integer> userIDList);

    @Modifying
    @Query(nativeQuery = true, value = "delete from user_wx_app where user_id = ?1 and app_name='hc-apm-supplier'")
    void deleteUserWxApp(Integer userId);

}
