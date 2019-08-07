package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by 503035627 on 2018/3/7.
 */
@Entity
@Table(name = "batch_job")
@Cacheable(false)
@Cache(usage=CacheConcurrencyStrategy.NONE)
public class BatchJob implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchJob.class);
	private static final long serialVersionUID = 1L;
	public static final int STATUS_START=1;
	public static final int STATUS_SUCCESS=2;
	public static final int STATUS_FAILED=3;
	public static final String NAME_NURSESITE="NurseSiteMaintain";
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	// 创建日期
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	// scheduleDate
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "schedule_time")
	private Date scheduleTime;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

	@Column(name = "create_by")
	private String createByUid;
	
	@Column(name="name")
	private String name;
	
	@Column(name="sub_name")
	private String subName;

	@Transient
	private UserAccount createBy;
	
	//0：未启动，1:启动中,2:成功，3：失败
	@Column(name = "status")
	private Integer status;
	
	@Column(name="priority")
	private Integer priority=0;
	
	@Column(name="times")
	private Integer times=0;
	
	@Transient
	private boolean needGetItem=true;
	
	//开始处理时间
    @Column(name = "process_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date processStartTime;

    //结束处理时间
    @Column(name = "process_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date processEndTime;
	
	//日志
    @Column(name = "log")
    private String log;
    
    @Column(name="content")
    private String content;
	
	public BatchJob() {
		super();
		this.status=0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSiteUID() {
		return siteUID;
	}

	public void setSiteUID(String siteUID) {
		this.siteUID = siteUID;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public String getCreateByUid() {
		return createByUid;
	}

	public void setCreateByUid(String createByUid) {
		this.createByUid = createByUid;
	}

	public UserAccount getCreateBy() {
		return createBy;
	}

	public void setCreateBy(UserAccount createBy) {
		this.createBy = createBy;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedGetItem() {
		return needGetItem;
	}

	public void setNeedGetItem(boolean needGetItem) {
		this.needGetItem = needGetItem;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Date getProcessStartTime() {
		return processStartTime;
	}

	public void setProcessStartTime(Date processStartTime) {
		this.processStartTime = processStartTime;
	}

	public Date getProcessEndTime() {
		return processEndTime;
	}

	public void setProcessEndTime(Date processEndTime) {
		this.processEndTime = processEndTime;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
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
		if (!(object instanceof BatchJob)) {
			return false;
		}
		BatchJob other = (BatchJob) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BatchJob [ id=" + id + " ]";
	}
}

