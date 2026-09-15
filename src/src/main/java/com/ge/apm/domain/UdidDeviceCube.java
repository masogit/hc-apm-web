package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName udid_device_cube
 */
@Data
@TableName(value = "udid_device_cube")
public class UdidDeviceCube implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 注册号
     */
    private String registryNum;

    /**
     * 注册人
     */
    private String registrantName;

    /**
     * 设备名
     */
    private String deviceName;

    /**
     * 设备型号
     */
    private String devicePattern;

    /**
     * 设备类别
     */
    private String deviceCategory;
}