package com.ge.apm.dao;

import com.ge.apm.domain.ZkbEngineer;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author 212605082 on 2019/12/18.
 */
public interface ZkbEngineerRepository extends GenericRepositoryUUID<ZkbEngineer> {


    /**
     * @param sso
     * @return
     */
    @Query("select max(p.ssoIndex) from ZkbEngineer p where p.sso like ?1 ")
    Object getMaxIndex(String sso);

    List<ZkbEngineer> findBySso(String sso);

    List<ZkbEngineer> findByTenantUID(String tenantUid);

    List<ZkbEngineer> findByPhone(String phone);

    ZkbEngineer findByIdentityNumber(String identityNumber);

    ZkbEngineer findByNameAndPhoneAndEmail(String name, String phone, String email);
}
