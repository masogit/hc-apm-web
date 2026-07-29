/*
 */
package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author 212547631
 */
@Data
@Entity
@Table(name = "asset_out_stock_record")
public class AssetOutStockRecord implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "site_uid")
    private String siteUid;

    @Column(name = "hospital_uid")
    private String hospitalUid;

    @Column(name = "institution_uid")
    private String institutionUid;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    @JoinColumn(name = "asset_uid",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    //出库单号
    @Column(name = "out_stock_no")
    private String outStockNo;

    //出库业务日期-出库时间
    @Column(name = "business_date")
    private Date businessDate;

    //退库时间
    @Column(name = "returned_date")
    private Date returnedDate;

    //退库单号
    @Column(name = "returned_stock_no")
    private String returnedStockNo;

    //供应商
    @Size(max = 64)
    @Column(name = "vendor")
    private String vendor;

    //出库单制单人
    @Column(name = "out_stock_user")
    private String outStockUser;

    //资产财产编号
    @Size(max = 64)
    @Column(name = "financing_num")
    private String financingNum;

    //设备名称
    @Basic(optional = false)
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;

    //合同号
    @Size(max = 64)
    @Column(name = "contract_num")
    private String contractNum;

    //启用日期
    @Column(name = "install_date")
    @Temporal(TemporalType.DATE)
    private Date installDate;

    //设备型号
    @Column(name = "function_type")
    private String functionType;

    //备注
    @Column(name = "comments")
    @Size(max = 256)
    private String comments;

    //采购价格(元)
    @Column(name = "purchase_price")
    private Double purchasePrice;

    //发票号
    @Size(max = 64)
    @Column(name = "invoice_num")
    private String invoiceNum;

    //发票日期
    @Column(name = "invoice_date")
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;

    //到货清单id
    @Column(name = "arrival_list_id", columnDefinition = "CHAR(32)")
    private String arrivalListId;

    @JoinColumn(name = "arrival_list_id",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ArrivalList arrivalList;

    //到货单条目
    @Column(name = "arrival_item_id", columnDefinition = "CHAR(32)")
    private String arrivalItemId;

    //领用科室
    @Column(name = "recruite_dept_uid")
    private String recruiteDeptUid;

    //状态 4、已出库 5、已退库
    @Column(name = "stock_status")
    private Integer stockStatus;

    //库存来源 1、台账入库 2、转科入库 3、到货入库 4、退库入库
    @Column(name = "stock_source")
    private Integer stockSource;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetOutStockRecord)) {
            return false;
        }
        AssetOutStockRecord other = (AssetOutStockRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
