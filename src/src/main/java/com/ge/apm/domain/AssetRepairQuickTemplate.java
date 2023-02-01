package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "asset_repair_quick_template")
public class AssetRepairQuickTemplate implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 模板类别（维修 1 故障原因，2 解决方案，3 交付检测）
     */
    @Column(name = "template_type")
    private Integer templateType;

    /**
     * 设备分类
     */
    @Column(name = "asset_group")
    private Integer assetGroup;

    /**
     * 设备分类名称
     */
    @Column(name = "asset_group_name")
    private String assetGroupName;

    /**
     * 租户id
     */
    @Column(name = "tenant_uid")
    private String tenantUid;

    /**
     * 组织id
     */
    @Column(name = "hier_org_uid", columnDefinition = "CHAR(64)")
    private String hierOrgUid;

    /**
     * 模板标题
     */
    @Column(name = "template_title")
    private String templateTitle;

    /**
     * 模板内容
     */
    @Column(name = "template_content")
    private String templateContent;

    @Column(name = "created_by")
    private String createdBy;//创建人

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;//创建日期

    @Column(name = "modify_by")
    private String modifyBy;//修改人

    @Column(name = "modify_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;//修改日期

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AssetRepairQuickTemplate)) {
            return false;
        }
        AssetRepairQuickTemplate other = (AssetRepairQuickTemplate) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
