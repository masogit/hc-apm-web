package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import com.ge.apm.domain.SystemTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SystemTemplateMapper extends BaseMapper<SystemTemplate> {

    List<SystemTemplate> searchSystemTemplate(Map<String, Object> param);


    Long searchSystemTemplateCount(Map<String, Object> param);

    List<SystemTemplate> findByKeyAndSiteForUseParentList(@Param("templateType") String templateType,@Param("siteUid") String siteUid);
}
