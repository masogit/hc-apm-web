/*
 */
package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "asset_stocked")
public class AssetStocked implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    //库存管理业务时间-设备安装时间
    @Column(name = "business_date")
    private Date businessDate;

    //领用人
    @Column(name = "recruiter")
    private String recruiter;

    //领用科室
    @Column(name = "recruite_dept_uid")
    private String recruiteDeptUid;

    //入库单号
    @Column(name = "in_stock_no")
    private String inStockNo;

    //出库单号
    @Column(name = "out_stock_no")
    private String outStockNo;

    //库存状态 1、待审核 2、已审核 3、已入库 4、已出库 5、已退库
    @Column(name = "stock_status")
    private Integer stockStatus;

    //入库单据时间
    @Column(name = "in_stock_documents_date")
    private Date inStockDocumentsDate;

    //出库单据时间
    @Column(name = "out_stock_documents_date")
    private Date outStockDocumentsDate;

    //入库单制单人
    @Column(name = "in_stock_user")
    private String inStockUser;

    //出库单制单人
    @Column(name = "out_stock_user")
    private String outStockUser;

    //入库业务日期-审核入库时间
    @Column(name = "in_stock_business_date")
    private Date inStockBusinessDate;

    //出库业务日期-首次生成入库单时间
    @Column(name = "out_stock_business_date")
    private Date outStockBusinessDate;

    //备注
    @Column(name = "comments")
    @Size(max = 256)
    private String comments;

    //到货清单id
    @Column(name = "arrival_list_id", columnDefinition = "CHAR(32)")
    private String arrivalListId;

    @JoinColumn(name = "arrival_list_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ArrivalList arrivalList;

    //到货单条目
    @Column(name = "arrival_item_id", columnDefinition = "CHAR(32)")
    private String arrivalItemId;

    //库存来源 1、台账入库 2、转科入库 3、到货入库 4、退库入库
    @Column(name = "stock_source")
    private Integer stockSource;

    @Transient
    private String assetName;
    @Transient
    private String functionType;
    @Transient
    private String invoiceNum;
    @Transient
    private String contractNum;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetStocked)) {
            return false;
        }
        AssetStocked other = (AssetStocked) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
