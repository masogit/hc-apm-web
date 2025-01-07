package com.ge.apm.dao;

import com.ge.apm.domain.ConfigItemValue;
import com.ge.apm.domain.SystemTemplate;
import com.ge.apm.domain.SystemTemplateValue;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;


public interface SystemTemplateValueRepository extends GenericRepositoryUUID<SystemTemplateValue> {




    @Query(value = "SELECT  coalesce(t5.storage_id,t4.storage_id,t3.storage_id,t2.storage_id,t1.storage_id) as actualValue " +
            " FROM system_template t1 LEFT JOIN system_template_value t2 ON t1.template_type = t2.template_type AND t2.site_uid = ?4" +
            " LEFT JOIN system_template_value t3 ON t1.template_type = t3.template_type AND t3.site_uid = ?3 " +
            " LEFT JOIN system_template_value t4 ON t1.template_type = t4.template_type AND t4.site_uid = ?2 " +
            " LEFT JOIN system_template_value t5 ON t1.template_type = t5.template_type AND t5.site_uid = ?1" +
            " where t1.template_type = ?5 and t1.file_type=?6  limit 1",nativeQuery = true)
    String findByKeyAndSiteForUseParent(String siteUid, String hospitalUID, String institutionUID, String tenantUID, String templateType,String fileType);


    SystemTemplateValue findFirstByTemplateTypeAndSiteUid(String type, String siteUid);


    @Modifying
    @Transactional
    @Query(value = "delete from system_template_value where site_uid =?1  ", nativeQuery = true)
    int deleteBySiteUid(String siteUID);



    @Modifying
    @Transactional
    @Query(value = "delete from system_template_value where id =?1  ", nativeQuery = true)
    int deleteById(String valueId);

}
