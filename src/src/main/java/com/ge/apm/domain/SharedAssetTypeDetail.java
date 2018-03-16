/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

/**
 *
 * @author caoyong
 */
//@Entity
//@Table(name = "swap_type_attach")
public class SharedAssetTypeDetail{
//    private static final long serialVersionUID = 1L;
//    @Id
//    @Column(name = "uid",columnDefinition = "CHAR(32)")
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid")
//    private String id;//主键
//    
//    @Column(name = "swap_type_id", columnDefinition = "CHAR(32)")
//    private String swapTypeId;//调剂设备id
//    
//    @Column(name = "name" )
    private Integer id;

    public Integer getId() {
        if(null==name){
           return 0;
        }else{
           return name.hashCode();
        }   
    }

    private String name;//附件名称

//    @Column(name = "remark" )
    private Boolean borrow_state;//

    private Boolean return_state;//
    
    private String borrow_changdu;//其他选项
    
    private String return_changdu;//其他选项
    
////    @Column(name = "create_by")
//    private String createBy;//创建人
//    
////    @Column(name = "modify_by")
//    private String modifyBy;//修改人
//    
////    @Column(name = "create_date")
////    @Temporal(TemporalType.DATE)
//    private Date createDate;//创建日期
//    
//    @Column(name = "modify_date")
//    @Temporal(TemporalType.DATE)
//    private Date modifyDate;//修改日期



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Boolean getBorrow_state() {
        return borrow_state;
    }

    public Boolean getReturn_state() {
        return return_state;
    }

    public String getBorrow_changdu() {
        return borrow_changdu;
    }

    public String getReturn_changdu() {
        return return_changdu;
    }
    
    public void setBorrow_state(Boolean borrow_state) {
        this.borrow_state = borrow_state;
    }

    public void setReturn_state(Boolean return_state) {
        this.return_state = return_state;
    }

    public void setBorrow_changdu(String borrow_changdu) {
        this.borrow_changdu = borrow_changdu;
    }

    public void setReturn_changdu(String return_changdu) {
        this.return_changdu = return_changdu;
    }
    
//
//    public String getCreateBy() {
//        return createBy;
//    }
//
//    public void setCreateBy(String createBy) {
//        this.createBy = createBy;
//    }
//
//    public String getModifyBy() {
//        return modifyBy;
//    }
//
//    public void setModifyBy(String modifyBy) {
//        this.modifyBy = modifyBy;
//    }
//
//    public Date getCreateDate() {
//        return createDate;
//    }
//
//    public void setCreateDate(Date createDate) {
//        this.createDate = createDate;
//    }
//
//    public Date getModifyDate() {
//        return modifyDate;
//    }
//
//    public void setModifyDate(Date modifyDate) {
//        this.modifyDate = modifyDate;
//    }
//    
  
}
