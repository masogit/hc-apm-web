package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName udid_registry_num_info
 */
@TableName(value = "udid_registry_num_info")
@Data
public class UdidRegistryNumInfo implements Serializable {
    /**
     * 注册证号
     */
    private String registryNo;

    /**
     * 器械类别
     */
    private String qxlb;

    /**
     * 产品类别
     */
    private String cplb;

    /**
     * 器械名称
     */
    private String cpmctymc;

    /**
     * 规格
     */
    private String ggxh;

    /**
     * 分类编号
     */
    private String flbm;

    /**
     * ID
     */
    private String id;

    /**
     * 数据库ID
     */
    private String dbId;

    /**
     * 注册证号
     */
    private String registryNum;

    /**
     * 注册人名称
     */
    private String registrantName;

    /**
     * 注册人地址
     */
    private String registrantAddress;

    /**
     * 生产地址
     */
    private String productionAddress;

    /**
     * 代理人
     */
    private String agentName;

    /**
     * 代理人地址
     */
    private String agentAddress;

    /**
     * 代理人注册地址
     */
    private String agentRegAddress;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 管理类别
     */
    private String managementCategory;

    /**
     * 型号规格
     */
    private String modelSpec;

    /**
     * 结构组成
     */
    private String structureComposition;

    /**
     * 适用范围
     */
    private String scopeOfUse;

    /**
     * 存储条件
     */
    private String storageConditions;

    /**
     * 附件
     */
    private String attachments;

    /**
     * 其他内容
     */
    private String otherContent;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 批准部门
     */
    private String approvalDept;

    /**
     * 批准日期
     */
    private String approvalDate;

    /**
     * 生效日期
     */
    private String effectiveDate;

    /**
     * 有效期
     */
    private String validPeriod;

    /**
     * 变更记录
     */
    private String changes;

    /**
     * 是否为注册信息
     * true为注册信息，false为备案信息
     */
    @TableField(exist = false)
    private boolean isRegistration;

    /**
     * 是否为境内注册或境内备案
     * true为境内注册或境内备案，false为进口注册或进口备案
     */
    @TableField(exist = false)
    private boolean isDomestic;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}