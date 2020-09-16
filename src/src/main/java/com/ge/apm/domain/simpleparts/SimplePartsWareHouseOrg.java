package com.ge.apm.domain.simpleparts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: kwg
 * @date: 2020/7/13 11:10
 * @version: 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "simple_parts_warehouse_org")
public class SimplePartsWareHouseOrg {

    /**
     * 仓库和机构关系表主键
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * @date 2020/7/13 10:56
     * 仓库id
     */
    @Column(name = "parts_warehouse_id")
    private String partsWarehouseId;

    /**
     * 仓库名称
     */
    @Transient
    private String partsWarehouseName;

    /**
     * 机构uid
     */
    @Column(name = "org_uid")
    private String orgUid;

    /**
     * 机构名称
     */
    @Transient
    private String orgName;

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

}
