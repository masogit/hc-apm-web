package com.ge.apm.dao;

import com.ge.apm.domain.ConfigItemVO;
import com.ge.apm.domain.ConfigItemValue;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * Created by 503266962
 */
public interface ConfigItemValueRepository extends GenericRepositoryUUID<ConfigItemValue> {

    ConfigItemValue findFirstByKeyAndSiteUid(String key, String siteUid);

    @Query(value = "select u.* from config_item_value u where u.key=?1", nativeQuery = true)
    List<ConfigItemValue> findByKey(String key);

    @Query(value = "SELECT " +
            " coalesce(t5.uid,t4.uid,t3.uid,t2.uid) AS uid, " +
            " coalesce(t5.key,t4.key,t3.key,t2.key) AS key, " +
            " coalesce(t5.value,t4.value,t3.value,t2.value,t1.default_value) AS value, " +
            " coalesce(t5.tenant_uid,t4.tenant_uid,t3.tenant_uid,t2.tenant_uid) AS tenant_uid, " +
            " coalesce(t5.institution_uid,t4.institution_uid,t3.institution_uid,t2.institution_uid) AS institution_uid, " +
            " coalesce(t5.hospital_uid,t4.hospital_uid,t3.hospital_uid,t2.hospital_uid) AS hospital_uid, " +
            " coalesce(t5.site_uid,t4.site_uid,t3.site_uid,t2.site_uid) AS site_uid, " +
            " coalesce(t5.custom_value,t4.custom_value,t3.custom_value,t2.custom_value) AS custom_value " +
            " FROM config_item t1 " +
            " LEFT JOIN config_item_value t2 ON t1.KEY = t2.KEY AND t2.site_uid = ?4 " +
            " LEFT JOIN config_item_value t3 ON t1.KEY = t3.KEY AND t3.site_uid = ?3 " +
            " LEFT JOIN config_item_value t4 ON t1.KEY = t4.KEY AND t4.site_uid = ?2 " +
            " LEFT JOIN config_item_value t5 ON t1.KEY = t5.KEY AND t5.site_uid = ?1 ",nativeQuery = true)
    List<ConfigItemValue> selectConfigItemAll(String siteUid, String hospitalUID, String institutionUID, String tenantUID);

    @Query(value = "SELECT  coalesce(t5.value,t4.value,t3.value,t2.value,t1.default_value) as actualValue " +
            " FROM config_item t1 LEFT JOIN config_item_value t2 ON t1.KEY = t2.KEY AND t2.site_uid = ?4" +
            " LEFT JOIN config_item_value t3 ON t1.KEY = t3.KEY AND t3.site_uid = ?3 " +
            " LEFT JOIN config_item_value t4 ON t1.KEY = t4.KEY AND t4.site_uid = ?2 " +
            " LEFT JOIN config_item_value t5 ON t1.KEY = t5.KEY AND t5.site_uid = ?1" +
            " where t1.key = ?5 limit 1",nativeQuery = true)
    String findByKeyAndSiteForUseParent(String siteUid, String hospitalUID, String institutionUID, String tenantUID, String key);


    @Query(value = "SELECT  coalesce(t3.value,t2.value,t1.default_value) as actualValue " +
            " FROM config_item t1 LEFT JOIN config_item_value t2 ON t1.KEY = t2.KEY AND t2.site_uid = ?2" +
            " LEFT JOIN config_item_value t3 ON t1.KEY = t3.KEY AND t3.site_uid = ?1 " +
            " where t1.key = ?3 limit 1",nativeQuery = true)
    String findByKeyAndSiteForUseParent( String institutionUID, String tenantUID, String key);
}
