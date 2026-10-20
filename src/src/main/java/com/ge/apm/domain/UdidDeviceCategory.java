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
     * 显示分类代码
     */
    @TableField(exist = false)
    private String displayCategoryCode;

    /**
     * 显示分类名称
     */
    @TableField(exist = false)
    private String displayCategoryName;
}