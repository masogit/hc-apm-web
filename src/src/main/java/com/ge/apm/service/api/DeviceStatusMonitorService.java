package com.ge.apm.service.api;

import com.ge.apm.domain.UserAccount;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeviceStatusMonitorService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ResponseEntity<Map<String, Object>> createResponseEntityForAssetMetering(UserAccount user, Integer offset, Integer limit, Integer category, String display, String ref, String href) {
        String sql = "select a.id, a.name, a.next_metering_date as date from asset_info a " +
                " where (a.tenant_uid = ? OR a.institution_uid = ? OR a.hospital_uid = ? OR a.site_uid = ?) " +
                " and a.next_metering_date between now() and date_trunc('month', now() + interval '2 months') " +
                (user.isEnableAssetTypeGroup() ? " and a.asset_type in ( '" + String.join("','", user.getAssetTypeGroupMap().keySet()) + "' ) " : "" ) +
                " limit ? offset ?";

        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), limit, offset);
        Integer count = countAssetsToMeter(user);
        return createResponseEntity(category, display, ref, href, items, count);
    }

    public ResponseEntity<Map<String, Object>> createResponseEntityForPmLv2(UserAccount user, Integer offset, Integer limit, Integer category, String display, String ref, String href) {
        String sql;
        if (user.isEnableAssetTypeGroup()) {
            sql = " SELECT po.id as id, po.asset_name as name, to_char(po.plan_time, 'YYYY-MM-DD') as date FROM v2_pm_order po left join asset_info a on po.asset_uid = a.uid " +
                    " WHERE po.rule_level = 2 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                    + " AND (po.tenant_uid IN (?) OR po.institution_uid IN (?) OR po.hospital_uid IN (?) OR po.site_uid IN (?))" +
                    (user.isEnableAssetTypeGroup() ? " and a.asset_type in ( '" + String.join("','", user.getAssetTypeGroupMap().keySet()) + "' ) " : "" ) +
                    " order by plan_time desc limit ? offset ?";
        } else {
            sql = " SELECT po.id as id, po.asset_name as name, to_char(po.plan_time, 'YYYY-MM-DD') as date FROM v2_pm_order po " +
                    " WHERE po.rule_level = 2 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                    + " AND (po.tenant_uid IN (?) OR po.institution_uid IN (?) OR po.hospital_uid IN (?) OR po.site_uid IN (?)) order by plan_time desc limit ? offset ?";
        }
        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), limit, offset);

        Integer count = countPmLv2(user);
        return createResponseEntity(category, display, ref, href, items, count);
    }

    public Integer countAssetsToMeter(UserAccount user) {
        return jdbcTemplate.queryForObject("select count(*) from asset_info a " +
                        " where (a.tenant_uid = ? OR a.institution_uid = ? OR a.hospital_uid = ? OR a.site_uid = ?) " +
                        " and a.next_metering_date between now() and date_trunc('month', now() + interval '2 months')",
                Integer.class, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID());
    }

    public Integer countOpenIssues(UserAccount user) {
        String sql = " SELECT count(*) FROM v2_work_order WHERE v2_work_order.status = 1 " +
                " AND (v2_work_order.tenant_uid IN (?) OR v2_work_order.institution_uid IN (?) " +
                " OR v2_work_order.hospital_uid IN (?) OR v2_work_order.site_uid IN (?)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID());
    }

    public Integer countOutOfWarranty(UserAccount user) {
        String sql = " SELECT count(*) FROM asset_info WHERE is_valid = true AND is_warranty = true AND warranty_date <= (NOW() + interval '2 months') " +
                " AND (tenant_uid IN (?) OR institution_uid IN (?) OR hospital_uid IN (?) OR site_uid IN (?)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID());
    }

    public Integer countPmLv2(UserAccount user) {
        String sql = " SELECT count(*) FROM v2_pm_order po WHERE po.rule_level = 2 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                + " AND (po.tenant_uid IN (?) OR po.institution_uid IN (?) OR po.hospital_uid IN (?) OR po.site_uid IN (?)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID());
    }

    public Integer countPmLv3(UserAccount user) {
        String sql = " SELECT count(*) FROM v2_pm_order po WHERE po.rule_level = 3 and po.is_finished = false AND po.plan_time < (NOW() + interval '1 weeks') "
                + " AND (po.tenant_uid IN (?) OR po.institution_uid IN (?) OR po.hospital_uid IN (?) OR po.site_uid IN (?)) ";
        return jdbcTemplate.queryForObject(sql, Integer.class, user.getSiteUID(), user.getSiteUID(), user.getSiteUID(), user.getSiteUID());
    }

    private ResponseEntity<Map<String, Object>> createResponseEntity(Integer category, String display, String ref, String href, List<Map<String, Object>> items, Integer count) {
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
}
