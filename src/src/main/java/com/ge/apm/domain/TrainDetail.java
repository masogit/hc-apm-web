package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "train_detail")
public class TrainDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status{
        Planned,Completed,Canceled
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 主表ID
     */
    @Column(name = "train_id")
    private String trainId;

    //培训课程子名称
    @Column(name = "name")
    private String name;

    //预计培训日期
    @Column(name = "plan_train_date")
    private Date planTrainDate;

    //预计培训开始时间
    @Column(name = "start_time")
    private Date startTime;

    //预计培训结束时间
    @Column(name = "end_time")
    private Date endTime;

    //内部授课人
    @Column(name = "internal_instructors")
    private String internalInstructors;

    //外部授课人
    @Column(name = "external_instructors")
    private String externalInstructors;

    @Column(name = "external_instructors_company")
    private String externalInstructorsCompany;

    //培训地点
    @Column(name = "train_location")
    private String trainLocation;

    @Column(name = "status")
    private String status;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;


    @Column(name = "qr_code_generate_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date qrCodeGenerateDate;

    @Column(name = "qr_code_str")
    private String qrCodeStr;

    @Transient
    List<V2_BlobObject> blobObjectList = new ArrayList<>();

    @Column(name = "actual_train_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualTrainDate;

    @Column(name = "complete_recorder")
    private String completeRecorder;



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrainDetail)) {
            return false;
        }
        TrainDetail other = (TrainDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
