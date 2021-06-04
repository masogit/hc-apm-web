package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/5/25
 * @description :
 */

@Entity
@Table(name = "zkb_contract_log")
public class ZkbContractLog {

    private static final long serialVersionUID = 1L;

    public enum LogType {
        contractUpdate,contractDelete,courseUpdate,CourseDelete,assetAdd,assetDelete,zkbPartOrderDelete,catUpdate,catDelete
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 合同号
     */
    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "loginUser_user_name")
    private String loginUserName;

    @Column(name = "created_date")
    private Date createdDate;

    /**
     * contractUpdate,contractDelete,courseUpdate,CourseDelete,assetAdd,assetDelete,zkbPartOrderDelete
     */
    @Column(name = "log_type")
    private String logType;

    @Column(name = "course_data")
    private String courseData;

    @Column(name = "asset_data")
    private String assetData;

    @Column(name = "reason")
    private String reason;

    @Column(name= "json_data",length = 4000)
    private String jsonData;

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getCourseData() {
        return courseData;
    }

    public void setCourseData(String courseData) {
        this.courseData = courseData;
    }

    public String getAssetData() {
        return assetData;
    }

    public void setAssetData(String assetData) {
        this.assetData = assetData;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
