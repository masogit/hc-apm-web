package com.ge.apm.domain;

import com.ge.apm.domain.dto.FieldColumnDto;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 503266962
 */

@Entity
@Table(name = "config_item", uniqueConstraints = {@UniqueConstraint(columnNames="key")})
@Data
public class ConfigItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @Column(name = "name")
    private String name;

    /**
     * 类别 配置归属于哪个业务模块
     * 1-设备档案 2-设备调剂 3-到货安装 4-维修流程 5-预防维护 6-预算管理 7-计量管理 8-临床助手 9-知识文档 10-绩效分析 11-质控评审 12-盘点工单 13-系统配置
     */
    @Column(name = "category")
    private Integer category;

    /**
     * 唯一性，有含义的值
     */
    @Column(name = "key")
    private String key;

    /**
     * 值类型 1-text 2-number 3-boolean 4-date 5-time 6-datetime 7-enum 8-multi-enum 9-字段配置
     * 类型为text时，如果需要做格式校验，需要在 validation 字段中定义正则表达式
     * 类型为enum时，需要在 validation 字段中定义key-value array
     */
    @Column(name = "value_type")
    private Integer valueType;

    /**
     * 合法性验证-正则表达式
     * 如果是enum类型，则存放enum的array
     * eg:[{"key":"人员1","value":"person1"},{"key":"人员2","value":"person2"},{"key":"人员3","value":"person3"}]
     */
    @Column(name = "validation")
    private String validation;

    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 是否超管修改
     * 是-该配置仅在APM平台管理下的配置项管理菜单下修改
     * 否-该配置仅在系统配置下的配置项管理菜单下修改
     */
    @Column(name = "super_admin_edit")
    private Boolean superAdminEdit;

    /**
     * 默认值
     */
    @Column(name = "default_value")
    @Type(type = "text")
    private String defaultValue;

    /**
     * 使用上级配置
     */
    @Column(name = "user_parent_config")
    private Boolean userParentConfig;

    @Transient
    private ConfigItemValue configItemValue;

    @Transient
    private String showValue;

    @Transient
    private List<Map<String, String>> enumList;

    @Transient
    private FieldColumnDto fieldColumnDto;

    @Transient
    private List<FieldColumnDto> fieldColumnDtoList;
}
