package com.ge.apm.domain.pure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 高级查询配置（整体配置，包含多个字段）
 */
public class AdvancedSearchConfig {
    private List<AdvancedSearchFieldConfig> fields;

    public AdvancedSearchConfig() {
        this.fields = new ArrayList<>();
    }

    public List<AdvancedSearchFieldConfig> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<AdvancedSearchFieldConfig> fields) {
        this.fields = fields;
    }

    /**
     * 根据fieldKey获取配置
     */
    public AdvancedSearchFieldConfig getFieldConfig(String fieldKey) {
        if (fieldKey == null || fields == null) return null;
        return fields.stream()
                .filter(f -> fieldKey.equals(f.getFieldKey()))
                .findFirst().orElse(null);
    }

    /**
     * 获取默认条件字段列表
     */
    public List<AdvancedSearchFieldConfig> getDefaultFields() {
        if (fields == null) return new ArrayList<>();
        return fields.stream()
                .filter(AdvancedSearchFieldConfig::isDefaultCondition)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有启用的字段key列表
     */
    public List<String> getEnabledFieldKeys() {
        if (fields == null) return new ArrayList<>();
        return fields.stream()
                .map(AdvancedSearchFieldConfig::getFieldKey)
                .collect(Collectors.toList());
    }
}
