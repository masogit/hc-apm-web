package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.ConfigItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 503310146
 */
public interface ConfigItemMapper extends BaseMapper<ConfigItem> {

    /**
     * 根据用户 key和 siteUid 查询配置项
     *
     * @param key     唯一 key
     * @param siteUid 用户 siteUid
     * @return ConfigItem
     */
    @Select("SELECT t1.* FROM config_item t1 LEFT JOIN config_item_value t2 ON t1.key = t2.key " +
            " WHERE t1.key = #{key} AND(t2.tenant_uid = #{siteUid} OR t2.institution_uid = #{siteUid} OR t2.hospital_uid = #{siteUid} OR t2.site_uid = #{siteUid}) ")
    ConfigItem findByKey(@Param("key") String key, @Param("siteUid") String siteUid);
}
