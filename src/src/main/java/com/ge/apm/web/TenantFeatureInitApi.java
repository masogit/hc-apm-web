package com.ge.apm.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ge.apm.dao.AdminFeatureActiveRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.AdminFeatureActive;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.web.difyChat.DiFyChatRequest;
import com.ge.healthcare.cn.common.cons.ApmFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apmTenant")
public class TenantFeatureInitApi {
  private static final Logger log = LoggerFactory.getLogger(TenantFeatureInitApi.class);
  @Autowired
  private TenantInfoRepository tenantInfoRepository;
  @Autowired
  private AdminFeatureActiveRepository adminFeatureActiveRepository;

  @RequestMapping(value = "init_quality_control", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> initQualityFeature() {
    Map<String, String> tenantLog = new HashMap<>();
    Iterable<TenantInfo> allTenant = tenantInfoRepository.findAll();
    allTenant.forEach(tenantInfo -> {
      boolean contains = tenantInfo.getFeatureList().contains("QUALITY_CONTROL_MANAGE");
      if (contains) {
        // add
        JSONArray array = JSON.parseArray(tenantInfo.getFeatureList());
        String calendar = ApmFeature.QUALITY_CONTROL_CALENDAR.name();
        String radBoard = ApmFeature.QUALITY_CONTROL_RAD_BOARD.name();
        if (!array.contains(calendar)) {
          array.add(calendar);
        }
        if (!array.contains(radBoard)) {
          array.add(radBoard);
        }
        tenantInfo.setFeatureList(array.toJSONString());
        tenantInfoRepository.save(tenantInfo);
        tenantLog.put(tenantInfo.getUid(), tenantInfo.getName());
      }
    });
    // 查询 admin_feature_active 表中 featureKey 为 QUALITY_CONTROL_MANAGE 的记录，全部替换为 QUALITY_CONTROL_RAD_BOARD
    List<AdminFeatureActive> adminFeatureActiveList = adminFeatureActiveRepository.findByFeatureKey("QUALITY_CONTROL_MANAGE");
    adminFeatureActiveList.forEach(adminFeatureActive -> {
      adminFeatureActive.setFeatureKey("QUALITY_CONTROL_RAD_BOARD");
      adminFeatureActiveRepository.save(adminFeatureActive);
    });
    return ResponseEntity.ok(JSON.toJSONString(tenantLog));
  }

}
