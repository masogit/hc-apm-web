package com.ge.apm.dao;

import com.ge.apm.domain.ConfigItemVO;
import com.ge.apm.domain.ConfigItemValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Map;

/**
 * Created by 503266962
 */
public interface ConfigItemValueRepository extends GenericRepositoryUUID<ConfigItemValue> {

    ConfigItemValue findFirstByKeyAndSiteUid(String key, String siteUid);

    @Query(value = "select u.* from config_item_value u where u.key=?1", nativeQuery = true)
    List<ConfigItemValue> findByKey(String key);





    @Query(value = "SELECT distinct t1.key, coalesce(t5.value,t4.value,t3.value,t2.value,t1.default_value) as actualValue " +
            " FROM config_item t1 LEFT JOIN config_item_value t2 ON t1.KEY = t2.KEY AND t2.site_uid = ?4" +
            " LEFT JOIN config_item_value t3 ON t1.KEY = t3.KEY AND t3.site_uid =?3 " +
            " LEFT JOIN config_item_value t4 ON t1.KEY = t4.KEY AND t4.site_uid = ?2" +
            " LEFT JOIN config_item_value t5 ON t1.KEY = t5.KEY AND t5.site_uid = ?1 " +
            " where t1.key is not null ",nativeQuery = true)
    List<ConfigItemVO> selectConfigItemAll(String siteUid, String hospitalUID, String institutionUID, String tenantUID);



    @Query(value = "SELECT  coalesce(t5.value,t4.value,t3.value,t2.value,t1.default_value) as actualValue " +
            " FROM config_item t1 LEFT JOIN config_item_value t2 ON t1.KEY = t2.KEY AND t2.site_uid = ?4" +
            " LEFT JOIN config_item_value t3 ON t1.KEY = t3.KEY AND t3.site_uid = ?3 " +
            " LEFT JOIN config_item_value t4 ON t1.KEY = t4.KEY AND t4.site_uid = ?2 " +
            " LEFT JOIN config_item_value t5 ON t1.KEY = t5.KEY AND t5.site_uid = ?1" +
            " where t1.key = ?5 limit 1",nativeQuery = true)
    String findByKeyAndSiteForUseParent(String siteUid, String hospitalUID, String institutionUID, String tenantUID, String key);
}
