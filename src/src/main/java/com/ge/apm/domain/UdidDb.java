package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName udid_db
 */
@TableName(value = "udid_db")
@Data
public class UdidDb implements Serializable {
    /**
     * 数据库ID
     */
    private String id;

    /**
     * 数据库名
     */
    private String name;

    /**
     * 是否为注册信息
     * true为注册信息，false为备案信息
     */
    private boolean isRegistration;

    /**
     * 是否为境内注册或境内备案
     * true为境内注册或境内备案，false为进口注册或进口备案
     */
    private boolean isDomestic;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}