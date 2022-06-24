package com.ge.apm.domain.simpleparts;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件详细库存表，该表中的数据会汇总到SpartPartsInventory表中
 * @package com.ge.apm.domain
 */
@Data
@Entity
@Table(name = "simple_parts_transaction")
public class SimplePartsTransaction implements Serializable {

    private static final long serialVersionUID = -698082103714118314L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "parts_item_id")
    private String partsItemId;

    /**
     * @author csh
     * @createTime 2020/6/11 11:29
     * @description 备件名称
     */
    @Column(name = "name")
    @Basic(optional = false)
    private String name;

    @Column(name = "parts_request_id")
    private String partsRequestId;

    @Column(name = "request_code", columnDefinition = "VARCHAR(60)")
    private String requestCode;

    @Column(name = "parts_request_line_item_id")
    private String partsRequestLineItemId;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 申请类型： 1.申领单 2.申购单
     */
    @Column(name = "request_type")
    private int requestType;

    /**
     * 0 入库，1 出库
     */
    @Column(name = "transaction_type")
    private int transactionType;

    /**
     * @author csh
     * @createTime 2020/6/15 18:07
     * @description 当前交易数量，可能是出库数量，也可能是入库的部分数量
     */
    @Column(name = "transaction_qty")
    private int transactionQty;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 备件单价
     */
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 备件总价
     */
    @Column(name = "total_prices")
    private BigDecimal totalPrice;

    /**
     * @author csh
     * @createTime 2020/6/11 11:24
     * @description 货源供应商
     */
    @Column(name = "sourc_supplier")
    private String sourcSupplier;

    /**
     * @author csh
     * @createTime 2020/6/11 11:33
     * @description 入库时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * @author csh
     * @createTime 2020/6/11 11:34
     * @description 入库人
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * @author csh
     * @createTime 2020/6/11 11:34
     * @description 对应仓库的uid
     */
    @Column(name = "parts_warehouse_id")
    private String partsWarehouseId;

    @Column(name = "parts_locator_id")
    private String partsLocatorId;

    /**
     * @author csh
     * @createTime 2020/6/16 13:41
     * @description 租户
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    /**
     * @author csh
     * @createTime 2020/6/16 13:41
     * @description 集团
     */
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    /**
     * @author csh
     * @createTime 2020/6/16 13:41
     * @description 医院
     */
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    /**
     * @author csh
     * @createTime 2020/6/16 13:41
     * @description 院区
     */
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    /**
     * @author csh
     * @createTime 2020/6/16 13:41
     * @description 科室
     */
    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUid;

    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * 收货信息
     */
    @Column(name = "address_info")
    private String addressInfo;
    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * 备注
     */
    @Column(name = "comments")
    @Size(max = 2000)
    private String comments;
    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * 入库类型
     * @description 新货入库newgoods   虚拟入库virtual   好还入库Okay
     */
    @Column(name = "storage_type")
    private String storageType;
    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * 运单号
     */
    @Column(name = "transport_no")
    private String transportNo;

    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * 运输公司
     */

    @Column(name = "transport_company")
    private String transportCompany;

    //备件描述
    @Column(name = "parts_description")
    private String partsDescription;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SimplePartsTransaction)) {
            return false;
        }
        SimplePartsTransaction other = (SimplePartsTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
