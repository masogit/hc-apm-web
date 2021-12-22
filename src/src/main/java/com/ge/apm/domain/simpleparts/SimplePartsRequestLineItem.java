package com.ge.apm.domain.simpleparts;

import com.ge.apm.domain.pure.SimplePartsItemValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/15
 * @descript 备件明细
 * @package com.ge.apm.domain
 */
@Data
@EqualsAndHashCode
@Entity
@Table(name = "simple_parts_request_line_item")
public class SimplePartsRequestLineItem implements Serializable {

    private static final long serialVersionUID = 7757822777093142302L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "parts_item_id")
    private String partsItemId;

    /**
     * @author csh
     * @createTime 2020/6/11 11:24
     * @description 备件名称
     */
    @Column(name = "name")
    private String name;

    /**
     * @author csh
     * @createTime 2020/6/11 11:25
     * @description 申请备件数量
     */
    @Column(name = "request_qty")
    private int requestQty;

    /**
     * @author csh
     * @createTime 2020/6/15 18:07
     * @description 当前交易数量，可能是出库数量，也可能是入库的部分数量 (当前实际出入库的备件数量)
     */
    @Column(name = "transaction_qty")
    private int transactionQty;

    /**
     * @author kwg
     * @createTime 2020/9/1 18:07
     * @description 当前未交易数量， (当前还未出入库的备件数量)
     */
    @Transient
    private Integer notTransactionQty;

    // 归还入库数量
    @Column(name = "return_qty")
    private Integer returnQty;

    // 未归还入库数量
    @Transient
    private Integer noReturnQty;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 状态：申领单时（1.已出库 2.未出库 ） 申购单时(1.已入库,2未入库 )
     */
    @Column(name = "status")
    private int status;

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
     * @description 备件品牌
     */
    @Column(name = "parts_brand")
    private String partsBrand;

    /**
     * @author csh
     * @createTime 2020/6/11 11:24
     * @description 备件型号
     */
    @Column(name = "parts_model")
    private String partsModel;

    /**
     * @author csh
     * @createTime 2020/6/11 11:24
     * @description 备件供应商
     */
    @Column(name = "parts_supplier")
    private String partsSupplier;

    /**
     * @author csh
     * @createTime 2020/6/11 11:24
     * @description 申请单编号
     */
    @Column(name = "request_code", columnDefinition = "VARCHAR(60)")
    private String requestCode;

    @Column(name = "parts_request_id")
    private String partsRequestId;

    /**
     * @author csh
     * @createTime 2020/6/11 11:27
     * @description 申请日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "proposer_date")
    private Date proposerDate;

    /**
     * @author csh
     * @createTime 2020/6/11 11:27
     * @description 创建日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * @author csh
     * @createTime 2020/6/11 11:27
     * @description 创建人
     */
    @Column(name = "created_by", columnDefinition = "VARCHAR(60)")
    private String createdBy;

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

    @Column(name = "address_info")
    private String addressInfo;

    //used by stock in or stock out
    @Transient
    private String partsWarehouseId;

    //used by stock in or stock out
    @Transient
    private String partsLocatorId;

    @Transient
    private SimplePartsItem partsItem;

    @Transient
    private SimplePartsItemValue partsItemValue;

    public Integer getRequestQtyAsObject() {
        if (null == this.getId()) {
            return null;
        }

        return this.getRequestQty();
    }

}
