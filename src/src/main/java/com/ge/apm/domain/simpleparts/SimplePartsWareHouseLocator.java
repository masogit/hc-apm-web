package com.ge.apm.domain.simpleparts;

import com.ge.apm.service.constant.SimplePartsConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: kwg
 * @date: 2020/7/26 11:10
 * @version: 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "simple_parts_warehouse_locator")
public class SimplePartsWareHouseLocator {

    public static final SimplePartsWareHouseLocator VIRTUAL_LOCATOR = new SimplePartsWareHouseLocator(SimplePartsConstant.VIRTUAL_LOCATOR_ID, SimplePartsConstant.VIRTUAL_LOCATOR_NAME);

    public SimplePartsWareHouseLocator(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 仓位表主键
     */
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Id
    @Column(name = "id")
    private String id;

    /**
     * @date 2020/7/26 10:56
     * 仓库id
     */
    @Column(name = "parts_warehouse_id")
    private String partsWarehouseId;

    /**
     * 仓位名称
     */
    @Column(name = "name")
    private String name;
    /**
     * @date 2020/7/26 15:37
     * 创建日期
     */
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * @date 2020/7/26 15:38
     * 创建人
     */
    @Column(name = "created_by")
    private String createdBy;

}
