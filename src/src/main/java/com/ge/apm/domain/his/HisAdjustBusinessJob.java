package com.ge.apm.domain.his;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "his_adjust_business_job")
@Data
public class HisAdjustBusinessJob implements Serializable {

    private static final long serialVersionUID = 9L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    /**
     * 所属医院org_code
     */
    @Column(name = "org_code")
    private String orgCode;
    /**
     * 任务类型(1 取消收费代码匹配 2 取消联机匹配 3 设备标签修改设备)  0分组成本 匹配修改  4取消手麻系统设备匹配
     */
    @Column(name = "task_type")
    private Integer taskType;

    /**
     * 业务id 多个时用 , 隔开
     * 收费项目ids / 联机设备识别码
     */
    @Column(name = "business_id", length = 1200)
    private String businessId;

    /**
     * 任务数据开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_start_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataStartDate;

    /**
     * 任务数据结束时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_end_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataEndDate;

    /**
     *
     * oldStrJson(历史数据json存储)
     */
    @Column(name = "old_str_json", columnDefinition = "text")
    private String oldStrJson;

    /**
     *
     * newStrJson(新数据json存储)
     */
    @Column(name = "new_str_json", columnDefinition = "text")
    private String newStrJson;

    /**
     * 任务状态(0 待执行, 1 已执行, 2 执行失败)
     */
    @Column(name = "task_status")
    private Integer taskStatus;

    /**
     * 任务执行时间 单位毫秒
     */
    @Column(name = "task_execute_time")
    private Integer taskExecuteTime;

    /**
     * 执行日志信息
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 创建日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    /**
     * 更新日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    /**
     * 联机匹配连接表id
     */
    @Transient
    @JsonSerialize
    @JsonDeserialize
    private String equipmentMatchId;

    /**
     * 收费项目id
     */
    @Transient
    @JsonSerialize
    @JsonDeserialize
    private String sfxmId;

    /**
     * 创建人
     */
    @Column(name = "created_by", columnDefinition = "VARCHAR(60)")
    private String createdBy;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HisAdjustBusinessJob)) {
            return false;
        }
        HisAdjustBusinessJob other = (HisAdjustBusinessJob) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
