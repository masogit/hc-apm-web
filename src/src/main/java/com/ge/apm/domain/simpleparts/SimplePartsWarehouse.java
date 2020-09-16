package com.ge.apm.domain.simpleparts;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: csh
 * @date: 2020/7/13 11:10
 * @version: 1.0
 */
@Entity
@Table(name = "simple_parts_warehouse")
@Data
public class SimplePartsWarehouse {

    /**
     * @date 2020/7/13 10:56
     * 仓库主键
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    /**
     * @date 2020/7/13 10:56
     * 仓库名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 仓库状态 0 禁用, 1启用
     */
    @Column(name = "status")
    private Integer status;

    /**
     * @date 2020/7/13 15:37
     * 创建日期
     */
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * @date 2020/7/13 15:38
     * 创建人
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * @date 2020/7/13 10:57
     * 院区UID
     */
    @Column(name = "site_uid")
    private String siteUid;

    /**
     * @date 2020/7/13 10:57
     * 医院UID
     */
    @Column(name = "hospital_uid")
    private String hospitalUid;

    /**
     * @date 2020/7/13 10:57
     * 集团UID
     */
    @Column(name = "institution_uid")
    private String institutionUid;

    /**
     * @date 2020/7/13 10:58
     * 租户UID
     */
    @Column(name = "tenant_uid")
    private String tenantUid;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SimplePartsWarehouse)) {
            return false;
        }
        SimplePartsWarehouse other = (SimplePartsWarehouse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
