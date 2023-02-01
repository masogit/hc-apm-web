package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "his_manual_job")
@Data
public class HisManualJob implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    /**
     * 任务类型
     */
    @Column(name = "task_type")
    private String taskType;
    /**
     * 任务类型名称
     */
    @Column(name = "task_type_name")
    private String taskTypeName;
    /**
     * 任务状态
     */
    @Column(name = "task_status")
    private String taskStatus;

    /**
     * 任务结果状态
     */
    @Column(name = "task_result_status")
    private String taskResultStatus;

    /**
     * 任务开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "task_start_date")
    private Date taskStartDate;
    /**
     * 任务结束时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "task_end_date")
    private Date taskEndDate;

    /**
     * 数据起始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_start_date")
    private Date dataStartDate;
    /**
     * 数据结束时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_end_date")
    private Date dataEndDate;

    /**
     * 数据抓取时间 单位毫秒
     */
    @Column(name = "sql_execute_time")
    private Integer sqlExecuteTime;

    /**
     * 数据抓取条数
     */
    @Column(name = "query_quantity")
    private Integer queryQuantity;

    /**
     * 成功条数
     */
    @Column(name = "total_quantity")
    private Integer totalQuantity;

    /**
     * 执行日志信息
     */
    @Column(name = "comment", columnDefinition = "text")
    private String comment;
    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(60)")
    private String createdBy;

    /**
     * 所属医院的orgCode
     */
    @Column(name = "org_code")
    private String orgCode;
    /**
     * 所属医院名称
     */
    @Column(name = "org_name", columnDefinition = "text")
    private String orgName;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HisManualJob)) {
            return false;
        }
        HisManualJob other = (HisManualJob) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
