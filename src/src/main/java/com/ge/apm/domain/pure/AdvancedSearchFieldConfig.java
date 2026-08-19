package com.ge.apm.domain.pure;

import java.util.ArrayList;
import java.util.List;

/**
 * 高级查询字段配置
 */
public class AdvancedSearchFieldConfig {
    private String fieldKey;              // 字段key
    private boolean defaultCondition;     // 是否默认搜索条件
    private List<RangeOption> ranges;     // 区间选项列表（数字/日期类型字段使用）

    public AdvancedSearchFieldConfig() {
        this.ranges = new ArrayList<>();
    }

    public AdvancedSearchFieldConfig(String fieldKey) {
        this.fieldKey = fieldKey;
        this.defaultCondition = false;
        this.ranges = new ArrayList<>();
    }

    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }

    public boolean isDefaultCondition() { return defaultCondition; }
    public void setDefaultCondition(boolean defaultCondition) { this.defaultCondition = defaultCondition; }

    public List<RangeOption> getRanges() {
        if (ranges == null) {
            ranges = new ArrayList<>();
        }
        return ranges;
    }
    public void setRanges(List<RangeOption> ranges) { this.ranges = ranges; }
}
