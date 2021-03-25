package com.ge.apm.domain;

import org.apache.logging.log4j.core.tools.Generate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaokai
 * @create 2021-02-02-10:28
 */
@Entity
@Table(name = "sql_export_queue")
public class SqlExportQueue implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "creater" )
    private int creater;

    @Column(name = "creater_name" )
    private String createrName;

    @Column(name = "creation_time")
    private Date creationTime;//创建时间

    @Column(name = "execute_statement",columnDefinition = "text")
    private String executeStatement;//执行语句

    @Column(name = "task_status")
    private int taskStatus;//任务状态

    @Column(name = "update_time")
    private Date updateTime;//更新时间

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCreater() {
        return creater;
    }

    public void setCreater(int creater) {
        this.creater = creater;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getExecuteStatement() {
        return executeStatement;
    }

    public void setExecuteStatement(String executeStatement) {
        this.executeStatement = executeStatement;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
