package com.ge.apm.dao;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.analysis.FieldFillRateDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class AssetInfoRepositoryImpl implements AssetInfoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    public AssetInfoRepositoryImpl() {
    }

    @Override
    public FieldFillRateDTO calculateFieldFillRate(String fieldName,
                                                   String tenantUid,
                                                   Integer level,
                                                   String levelUid,
                                                   String selectSite,
                                                   String clinicalDeptUid,
                                                   boolean ignoreEmptyString) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<AssetInfo> root = cq.from(AssetInfo.class);

        // ===== 1. 固定条件 + 动态条件 =====
        List<Predicate> predicates = new ArrayList<>();

        // 固定条件
        predicates.add(cb.equal(root.get("isDeleted"), false));
        predicates.add(cb.equal(root.get("isValid"), true));
        predicates.add(cb.equal(root.get("tenantUID"), tenantUid));
        switch (level) {
            case 1:
                // 集团
                predicates.add(cb.equal(root.get("institutionUID"), levelUid));
                break;
            case 2:
                // 医院
                predicates.add(cb.equal(root.get("hospitalUID"), levelUid));
                break;
            case 3:
                // 院区
                predicates.add(cb.equal(root.get("siteUID"), levelUid));
                break;
            case 4:
                // 科室
                predicates.add(cb.equal(root.get("clinicalDeptUID"), levelUid));
                break;
        }

        // 动态条件：siteUID = selectSite（有值才加）
        if (selectSite != null && !selectSite.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("siteUID"), selectSite));
        }

        // 动态条件：clinicalDeptUID = clinicalDeptUid（有值才加）
        if (clinicalDeptUid != null && !clinicalDeptUid.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("clinicalDeptUID"), clinicalDeptUid));
        }

        // 2. 总数：count(*)
        Expression<Long> totalExpr = cb.count(root);
        // 字段非自然列变更
        if (fieldName.equals("assetDeptName")) {
            fieldName = "assetDeptId";
        }
        // 3. 填写数：根据字段是否“有值”
        Path<?> fieldPath = root.get(fieldName);
        Predicate notNull = cb.isNotNull(fieldPath);

        Class<?> javaType = fieldPath.getJavaType();

        Expression<Long> filledExpr;

        if (ignoreEmptyString && String.class.equals(javaType)) {
            // 只有 String 字段才去比 ""，否则会触发 NumberFormatException
            @SuppressWarnings("unchecked")
            Path<String> stringPath = (Path<String>) fieldPath;
            filledExpr = cb.sum(
                    cb.<Long>selectCase()
                            .when(cb.and(notNull), 1L)
                            .otherwise(0L)
            );
        } else {
            // 非字符串字段（数值/日期/布尔等），只判断 not null 即视为“有值”
            filledExpr = cb.sum(
                    cb.<Long>selectCase()
                            .when(notNull, 1L)
                            .otherwise(0L)
            );
        }

        cq.multiselect(
                totalExpr.alias("totalCount"),
                filledExpr.alias("filledCount")
        ).where(predicates.toArray(new Predicate[0]));

        Tuple tuple = em.createQuery(cq).getSingleResult();
        long total = tuple.get("totalCount", Long.class);
        long filled = 0;
        if (tuple.get("filledCount") != null) {
            filled = tuple.get("filledCount", Long.class);
        }
        double rate = (total == 0) ? 0.0 : (double) filled / total;
        return new FieldFillRateDTO(total, filled, rate);
    }
}
