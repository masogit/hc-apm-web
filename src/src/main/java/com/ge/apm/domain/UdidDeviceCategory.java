package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName udid_device_category
 */
@Data
@TableName(value = "udid_device_category")
public class UdidDeviceCategory implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Integer id;

    /**
     * 序号
     */
    private Integer seqNo;

    /**
     * 一级分类代码
     */
    private Integer cat1Code;

    /**
     * 二级分类代码
     */
    private Integer cat2Code;

    /**
     * 三级分类代码
     */
    private Integer cat3Code;

    /**
     * 分类代码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String catalogName;

    /**
     * 一级分类
     */
    private String primaryCategory;

    /**
     * 二级分类
     */
    private String secondaryCategory;

    /**
     * 产品描述
     */
    private String productDescription;

    /**
     * 设备用途
     */
    private String intendedUse;

    /**
     * 产品实例
     */
    private String productExamples;

    /**
     * 管理类别
     */
    private String managementClass;

    /**
     * 获取医疗器械分类显示文本
     * <p>处理逻辑:
     * <ul>
     *   <li>去除 categoryCode 中的连字符(-)</li>
     *   <li>查找 secondaryCategory 中首个空格（兼容普通空格和NBSP \u00A0），取位置较前者</li>
     *   <li>提取首个空格后的内容（跳过分类编号）</li>
     *   <li>拼接为: categoryCode + 空格 + 分类名称</li>
     * </ul>
     * <p>示例:
     * <pre>
     *   categoryCode="05-02-01", secondaryCategory="01.1 伽玛射束远距离治疗机"  -> "050201 伽玛射束远距离治疗机"
     *   categoryCode="06-03-01", secondaryCategory="01\u00A0X 射线高压发生器"   -> "060301 X 射线高压发生器"
     *   categoryCode="03-02-01", secondaryCategory="01.2 医用X射线设备"        -> "030201 医用X射线设备"
     *   categoryCode=null                                                   -> null
     * </pre>
     *
     * @return 医疗器械分类显示文本，categoryCode或secondaryCategory为null时返回categoryCode原值
     */
    public String getDisplayCategory() {
        if (categoryCode == null || secondaryCategory == null) {
            return categoryCode;
        }
        String code = categoryCode.replace("-", "");
        String secondary = secondaryCategory.trim();
        int spaceIdx = secondary.indexOf('\u00A0');
        int normalSpaceIdx = secondary.indexOf(' ');
        if (spaceIdx < 0 || (normalSpaceIdx >= 0 && normalSpaceIdx < spaceIdx)) {
            spaceIdx = normalSpaceIdx;
        }
        if (spaceIdx >= 0 && spaceIdx < secondary.length() - 1) {
            return code + "  " + secondary.substring(spaceIdx + 1);
        }
        return code + "  " + secondary;
    }
}