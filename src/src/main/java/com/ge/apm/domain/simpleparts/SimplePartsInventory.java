package com.ge.apm.domain.simpleparts;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件库存
 * @package com.ge.apm.domain
 */
@Data
@Entity
@Table(name = "simple_parts_inventory")
public class SimplePartsInventory implements Serializable {

    private static final long serialVersionUID = -3219107586626148069L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "parts_item_id")
    private String partsItemId;

    @Column(name = "name")
    @Basic(optional = false)
    private String name;

    @Column(name = "parts_warehouse_id")
    private String partsWarehouseId;

    @Column(name = "parts_locator_id")
    private String partsLocatorId;

    @Column(name = "onhand_qty")
    private int onhandQty;

    @Transient
    private int physicalCount;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parts_item_id",referencedColumnName = "id", insertable = false,updatable = false)
    private SimplePartsItem simplePartsItem;

    @Transient
    private String warningStatus = null;

    public void setWarningStatus(){
        if(simplePartsItem==null || (simplePartsItem.getMaximumQuantity()==null ||simplePartsItem.getMaximumQuantity()==0 ) ||   (simplePartsItem.getMinimumQuantity()==null ||simplePartsItem.getMinimumQuantity()==0  )    ){
            return;
        }
        if(this.getOnhandQty()>= simplePartsItem.getMaximumQuantity()*0.9 ){
            warningStatus = "warrantyCeiling";
        }
        if(this.getOnhandQty()>= simplePartsItem.getMinimumQuantity()*1.2 ){
            warningStatus = "warrantyFloor";
        }
        if(this.getOnhandQty()== simplePartsItem.getMinimumQuantity()*1.2  && this.getOnhandQty()== simplePartsItem.getMaximumQuantity()*0.9){
            warningStatus = "warrantyFloor";
        }
    }
}
