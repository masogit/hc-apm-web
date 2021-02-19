package com.ge.apm.service.api;

import com.ge.apm.domain.UserAccount;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DeviceStatusMonitorService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ResponseEntity<Map<String, Object>> createResponseEntityForAssetMetering(UserAccount user, Integer offset, Integer limit, Integer category, String display, String ref, String href) {
        String sql = "select a.id, a.name, a.next_metering_date as date from asset_info a where now() between a.next_metering_date and a.next_metering_date - :remindDays " +
                " and (tenant_uid IN (:site_uid) OR institution_uid IN (:site_uid) OR hospital_uid IN (:site_uid) OR site_uid IN (:site_uid)) order by next_metering_date desc limit :limit offset :offset";
        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql,
                new MapSqlParameterSource().addValue("site_uid", user.getSiteUID())
                        .addValue("remindDays", getMeteringRemindDays(user))
                        .addValue("offset", offset)
                        .addValue("limit", limit));

        Integer count = countAssetsToMeter(user);
        Map<String, Object> link = createLink(ref, href);
        Map<String, Object> pages = createPages(count);
        Map<String, Object> root = createRoot(category, display, count, link);

        items.forEach(item -> item.put("link", link));

        return ResponseEntity.ok()
                .body(new ImmutableMap.Builder<String, Object>()
                        .put("pages", pages)
                        .put("items", items)
                        .put("root", root)
                        .build());
    }

    public ResponseEntity<Map<String, Object>> createResponseEntityForPmLv2(UserAccount user, Integer offset, Integer limit, Integer category, String display, String ref, String href) {
        String sql = " SELECT count(*) FROM v2_pm_order po WHERE po.rule_level = 2 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                + " AND (po.tenant_uid IN (:site_uid) OR po.institution_uid IN (:site_uid) OR po.hospital_uid IN (:site_uid) OR po.site_uid IN (:site_uid)) order by plan_time desc limit :limit offset :offset";
        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql,
                new MapSqlParameterSource().addValue("site_uid", user.getSiteUID())
                        .addValue("offset", offset)
                        .addValue("limit", limit));

        Integer count = countPmLv2(user);
        Map<String, Object> link = createLink(ref, href);
        Map<String, Object> pages = createPages(count);
        Map<String, Object> root = createRoot(category, display, count, link);

        items.forEach(item -> item.put("link", link));

        return ResponseEntity.ok()
                .body(new ImmutableMap.Builder<String, Object>()
                        .put("pages", pages)
                        .put("items", items)
                        .put("root", root)
                        .build());
    }

    private Map<String, Object> createPages(Integer count) {
        return new ImmutableMap.Builder<String, Object>().put("total", count).build();
    }

    private Map<String, Object> createRoot(Integer category, String display, Integer count, Map<String, Object> link) {
        return new ImmutableMap.Builder<String, Object>()
                .put("category", category)
                .put("display", display)
                .put("count", count)
                .put("unit", "台")
                .put("link", link)
                .build();
    }

    private Map<String, Object> createLink(String ref, String href) {
        return new ImmutableMap.Builder<String, Object>()
                .put("ref", ref)
                .put("href", href)
                .build();
    }

    public Integer countAssetsToMeter(UserAccount user) {
        return jdbcTemplate.queryForObject("select count(*) from asset_info a where now() between a.next_metering_date and a.next_metering_date - :remindDays " +
                        " and (tenant_uid IN (:site_uid) OR institution_uid IN (:site_uid) OR hospital_uid IN (:site_uid) OR site_uid IN (:site_uid)) ",
                Integer.class, new MapSqlParameterSource().addValue("site_uid", user.getSiteUID()).addValue("remindDays", getMeteringRemindDays(user)));
    }

    public Integer countOpenIssues(UserAccount user) {
        String sql = " SELECT count(*) FROM v2_work_order WHERE v2_work_order.status = 1 " +
                " AND (v2_work_order.tenant_uid IN (:site_uid) OR v2_work_order.institution_uid IN (:site_uid) " +
                " OR v2_work_order.hospital_uid IN (:site_uid) OR v2_work_order.site_uid IN (:site_uid)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, new MapSqlParameterSource().addValue("site_uid", user.getSiteUID()));
    }

    public Integer countOutOfWarranty(UserAccount user) {
        String sql = " SELECT count(*) FROM asset_info WHERE is_valid = true AND is_warranty = true AND warranty_date <= (NOW() + interval '2 months') " +
                " AND (tenant_uid IN (:site_uid) OR institution_uid IN (:site_uid) OR hospital_uid IN (:site_uid) OR site_uid IN (:site_uid)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, new MapSqlParameterSource().addValue("site_uid", user.getSiteUID()));
    }

    public Integer countPmLv2(UserAccount user) {
        String sql = " SELECT count(*) FROM v2_pm_order po WHERE po.rule_level = 2 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                + " AND (po.tenant_uid IN (:site_uid) OR po.institution_uid IN (:site_uid) OR po.hospital_uid IN (:site_uid) OR po.site_uid IN (:site_uid)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, new MapSqlParameterSource().addValue("site_uid", user.getSiteUID()));
    }

    public Integer countPmLv3(UserAccount user) {
        String sql = " SELECT count(*) FROM v2_pm_order po WHERE po.rule_level = 3 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                + " AND (po.tenant_uid IN (:site_uid) OR po.institution_uid IN (:site_uid) OR po.hospital_uid IN (:site_uid) OR po.site_uid IN (:site_uid)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, new MapSqlParameterSource().addValue("site_uid", user.getSiteUID()));
    }

    private Integer getMeteringRemindDays(UserAccount user) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select measuring_remind_days from workflow_config w where w.site_id = :siteId and w.hospital_id = :hospitalId limit 1",
                Integer.class, new MapSqlParameterSource().addValue("siteId", user.getSiteId()).addValue("hospitalId", user.getHospitalId())))
                .orElse(30);
    }
}
