package com.ge.apm.domain.pure;

/**
 * 高级查询区间选项配置
 */
public class RangeOption {
    private String label;     // 区间显示名称（如"0-1万"）
    private String minValue;  // 最小值（字符串存储，日期用yyyy-MM-dd格式，null表示无下限）
    private String maxValue;  // 最大值（null表示无上限）

    public RangeOption() {}

    public RangeOption(String label, String minValue, String maxValue) {
        this.label = label;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getMinValue() { return minValue; }
    public void setMinValue(String minValue) { this.minValue = minValue; }

    public String getMaxValue() { return maxValue; }
    public void setMaxValue(String maxValue) { this.maxValue = maxValue; }
}
