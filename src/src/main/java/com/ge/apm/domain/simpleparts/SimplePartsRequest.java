package com.ge.apm.domain.simpleparts;

import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author csh
 * @params
 * @return
 * @throws
 * @createTime 2020/6/11 10:44
 * @description 申领申购单据
 */
@Data
@Entity
@Table(name = "simple_parts_request")
public class SimplePartsRequest implements ApprovableTarget, Serializable {

    private static final long serialVersionUID = -1;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    /**
     * @author csh
     * @createTime 2020/6/11 11:24
     * @description 申请单编号
     */
    @Column(name = "request_code", columnDefinition = "VARCHAR(60)")
    private String requestCode;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 申请类型： 1.申领单 2.申购单
     */
    @Column(name = "type")
    private int type;

    /**
     * @author csh
     * @createTime 2020/6/11 11:25
     * @description 申请备件数量
     */
    @Column(name = "request_qty")
    private int requestQty;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 备件总价
     */
    @Column(name = "total_prices")
    private BigDecimal totalPrice;

    /**
     * @author csh
     * @createTime 2020/6/11 11:34
     * @description 选中的默认仓库的uid
     */
    @Column(name = "parts_warehouse_id")
    private String partsWarehouseId;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 状态：申领单时（1.已出库 2.未出库 ） 申购单时(1.已入库,2未入库 )
     */
    @Column(name = "status")
    private int status;

    /**
     * @author csh
     * @createTime 2020/6/11 11:25
     * @description 申请人的id
     */
    @Column(name = "proposer_id")
    private int proposerId;

    /**
     * @author csh
     * @createTime 2020/6/11 11:26
     * @description 申请状态(0 、 待审批 ， 1 、 审批中 ， 2 、 审批通过 ， 3 、 审批不同意)
     */
    @Column(name = "proposer_status")
    private int proposerStatus;

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
     * 维修工作单号
     */
    @Column(name = "maintenance_work_order_number")
    private String maintenanceWorkOrderNumber;

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
    private String clinicalDeptUID;

    /**
     * 工单id 维修or保养的id
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 工单类型 0-维修 1-保养
     */
    @Column(name = "order_type")
    private Integer orderType;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SimplePartsRequest)) {
            return false;
        }
        SimplePartsRequest other = (SimplePartsRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return requestCode;
    }

    public BigDecimal getTotalPriceToBigDecimal() {
        return totalPrice;
    }

    @Override
    public String getBusinessNumber() {
        return getRequestCode();
    }

    @Override
    public String getBusinessUid() {
        return getId();
    }

    @Override
    public int getBusinessType() {
        return getType();
    }

    public static BigDecimal calculateTotalPrice(long quantity, BigDecimal unitPrice) {
        // 计算总价格
        if (null == unitPrice) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return totalPrice;
    }
}
