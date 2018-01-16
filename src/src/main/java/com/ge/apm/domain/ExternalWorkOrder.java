package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by lindai on 2017/12/11.
 */
@Entity
@Table(name="external_work_order")
public class ExternalWorkOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "id")
	private String id;
	@Column(name = "status")
	private String status;
	@Column(name = "sr_num")
	private String srNum;
	@Column(name = "hospital_Name")
	private String hospitalName;
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time")
	private Date updateTime;
	@Column(name = "asset_desc")
	private String assetDesc;
	@Column(name = "system_id")
	private String systemId;
	@Column(name = "asset_serial_no")
	private String assetSerialNo;


	@Column(name = "asset_uid")
	private String assetUid;

	@JoinColumn(name = "asset_uid", referencedColumnName = "id", insertable = false, updatable = false)
	@ManyToOne(fetch = FetchType.EAGER)
	private AssetInfo assetInfo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSrNum() {
		return srNum;
	}

	public void setSrNum(String srNum) {
		this.srNum = srNum;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAssetDesc() {
		return assetDesc;
	}

	public void setAssetDesc(String assetDesc) {
		this.assetDesc = assetDesc;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getAssetSerialNo() {
		return assetSerialNo;
	}

	public void setAssetSerialNo(String assetSerialNo) {
		this.assetSerialNo = assetSerialNo;
	}

	public String getAssetUid() {
		return assetUid;
	}

	public void setAssetUid(String assetUid) {
		this.assetUid = assetUid;
	}

	public AssetInfo getAssetInfo() {
		return assetInfo;
	}

	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
	}
}