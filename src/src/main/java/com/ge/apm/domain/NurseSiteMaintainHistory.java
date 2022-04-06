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
@Table(name = "nurse_site_maintain_history")
public class NurseSiteMaintainHistory{

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

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "create_by")
    private String createByUid;

    @Column(name = "modify_by")
    private String modifyByUid;

    @Column(name="name")
    private String name;

    @Transient
    private UserAccount createBy;

    @Transient
    private UserAccount modifyBy;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nurseSiteMaintainHistory", fetch = FetchType.LAZY)
    private List<NurseSiteMaintainItemHistory> listNurseSiteMaintainItem;

    //0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
    @Column(name = "status")
    private Integer status=0;

    @Column(name="config_id")
    private String configId;

    @Column(name="signature_id")
    private String signatureId;

    //NULL:no signature,0:from web(file text),1:from weixin(file stream)
    @Column(name="signature_source_type")
    private Integer signatureSourceType=0;

    @Column(name="sequence")
    private Integer sequence;

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//使用科室


    public String getShowTitle() {
        //0：未检查,1:已检查，2:通过，3有问题
        String strStatus="未检查";
        if(status==null || status==0){
        }
        else if(status==1){
            strStatus="已检查";
        }
        else if(status==2|| status==5|| status==6|| status==7){
            strStatus="通过";
        }
        else if(status==3 || status==8){
            strStatus="有问题";
        }
        else if(status==4){
            strStatus="进行中";
        }

        String str=getName();
        if(getSequence()==null||getSequence()<=0){
            str+="-"+strStatus;
        }
        else{
            str+="-"+getSequence()+"-"+strStatus;
        }
        return str;
    }

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
        if (!(object instanceof NurseSiteMaintainHistory)) {
            return false;
        }
        NurseSiteMaintainHistory other = (NurseSiteMaintainHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NurseSiteMaintainHistory [ id=" + id + " ]";
    }

}
