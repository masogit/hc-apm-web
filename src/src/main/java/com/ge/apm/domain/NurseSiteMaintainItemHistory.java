package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Cacheable(false)
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.NONE)
@Table(name = "nurse_site_maintain_item_history")
public class NurseSiteMaintainItemHistory {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    // 创建日期
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_date")
    private Date createDate;

    // 修改日期
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "modify_date")
    private Date modifyDate;

    // 检测日期
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "check_date")
    private Date checkDate;

    // scheduleDate
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "schedule_date")
    private Date scheduleDate;

    @Column(name = "create_by")
    private String createByUid;

    @Column(name = "modify_by")
    private String modifyByUid;

    @Transient
    private UserAccount createBy;

    @Transient
    private UserAccount modifyBy;

    @Column(name = "update_by")
    private String updateByUid;

    @Column(name="parent_id",insertable = false, updatable = false)
    private String parentId;

    @Column(name="asset_uid")
    private String assetUid;

    //0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
    @Column(name = "status")
    private Integer status;

    @Column(name = "is_finish")
    private Boolean isFinish = false; //是否完成，以所有检测项都已勾选为准

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//使用科室

    @Column(name="name")
    private String name;

    @Column(name="config_id")
    private String configId;

    @Column(name="signature_id")
    private String signatureId;

    //NULL:no signature,0:from web(file text),1:from weixin(file stream)
    @Column(name="signature_source_type")
    private Integer signatureSourceType=0;

    @Column(name="sequence")
    private Integer sequence;

    @Column(name="comment")
    private String comment="";

    @Transient
    private boolean needGetItem=true;

    public boolean isNeedGetItem() {
        return needGetItem;
    }

    public void setNeedGetItem(boolean needGetItem) {
        this.needGetItem = needGetItem;
    }

    public void setSequence(Integer sequence) {
        if(sequence==null){
            sequence=0;
        }
        this.sequence = sequence;
    }

    public String getSequenceName(){
        if(sequence==null||sequence<0){
            return "";
        }
        else{
            return sequence+"";
        }
    }

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item", fetch = FetchType.LAZY)
    private List<NurseSiteMaintainDataHistory> listData;

    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private NurseSiteMaintainHistory nurseSiteMaintainHistory;

    @Transient
    private AssetInfo assetInfo;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are
        // not set
        if (!(object instanceof NurseSiteMaintainItemHistory)) {
            return false;
        }
        NurseSiteMaintainItemHistory other = (NurseSiteMaintainItemHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))|| (this.id==null && other.id==null)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NurseSiteMaintainItemHistory [ id=" + id + " ]";
    }

    public Boolean getIsFinish() { return isFinish; }

    public void setIsFinish(Boolean finish) { isFinish = finish; }
}
