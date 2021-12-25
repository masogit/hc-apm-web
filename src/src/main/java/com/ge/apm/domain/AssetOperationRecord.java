package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
    设备操作记录项
 */
@Data
@Entity
@Cacheable(false)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONE)
@Table(name = "asset_operation_record")
public class AssetOperationRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(columnDefinition = "CHAR(32)")
	private String id;

	// 登记时间
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_date")
	private Date createDate;

	//登记项名称
	@Column(name = "operationName")
	private String operationName;

	//登记人uid
	@Column(name = "operation_by")
	private String operationBy;

	//登记人姓名
	@Column(name = "operation_by_name")
	private String operationByName;

	//设备Uid
	@Column(name = "asset_info_uid")
	private String assetInfoUid;

	//设备名称
	@Column(name = "asset_info_name")
	private String assetInfoName;

	//设备型号
	@Column(name = "asset_info_function_type")
	private String assetInfofunctionType;

	//设备编号
	@Column(name = "asset_info_depart_num")
	private String assetInfodepartNum;

	//所属科室UID
	@Column(name = "asset_info_clinical")
	private String assetInfoclinical;

	//所属科室名称
	@Column(name = "asset_info_clinical_dept_name")
	private String assetInfoclinicalDeptName;

	//住院号
	@Column(name = "hospitalNumber")
	private String hospitalNumber;

	//制造商序列号
	@Size(max = 64)
	@Column(name = "serial_num")
	private String serialNum;

	//从开机到关机之中所有的检查项作为一次记录，如果有关机操作，则关机之前的所有记录改为  false,默认为true
	@Column(name = "is_end_closed_time")
	private boolean isEndByClosedTime;

	//操作类型 0：开机  1：其它检查项  2：关机
	@Column(name = "operation_type")
	private Integer operationType;

	//一次开关机里面所有的操作记录
	@Transient
	private List<AssetOperationRecord> assetOperationRecords;

	//一次开关机下所有的检查项
	@Transient
	private List<NurseSiteMaintainConfigCheckItem> nurseSiteMaintainConfigCheckItemList;

	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

	public String getOperationName() {
		if(operationType.equals(0)){
			setOperationName("开机");
		}else if(operationType.equals(2)){
			setOperationName("关机");
		}
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public Integer getOperationType() {
		return operationType;
	}

	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}

	public String getId() {
		return id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getOperationBy() {
		return operationBy;
	}

	public String getOperationByName() {
		return operationByName;
	}

	public String getAssetInfoUid() {
		return assetInfoUid;
	}

	public String getAssetInfoName() {
		return assetInfoName;
	}

	public String getAssetInfofunctionType() {
		return assetInfofunctionType;
	}

	public String getAssetInfodepartNum() {
		return assetInfodepartNum;
	}

	public String getAssetInfoclinical() {
		return assetInfoclinical;
	}

	public String getAssetInfoclinicalDeptName() {
		return assetInfoclinicalDeptName;
	}

	public String getHospitalNumber() {
		return hospitalNumber;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public boolean isEndByClosedTime() {
		return isEndByClosedTime;
	}

	public List<AssetOperationRecord> getAssetOperationRecords() {
		return assetOperationRecords;
	}

	public List<NurseSiteMaintainConfigCheckItem> getNurseSiteMaintainConfigCheckItemList() {
		return nurseSiteMaintainConfigCheckItemList;
	}

	public String getTenantUID() {
		return tenantUID;
	}

	public String getInstitutionUID() {
		return institutionUID;
	}

	public String getHospitalUID() {
		return hospitalUID;
	}

	public String getSiteUID() {
		return siteUID;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setOperationBy(String operationBy) {
		this.operationBy = operationBy;
	}

	public void setOperationByName(String operationByName) {
		this.operationByName = operationByName;
	}

	public void setAssetInfoUid(String assetInfoUid) {
		this.assetInfoUid = assetInfoUid;
	}

	public void setAssetInfoName(String assetInfoName) {
		this.assetInfoName = assetInfoName;
	}

	public void setAssetInfofunctionType(String assetInfofunctionType) {
		this.assetInfofunctionType = assetInfofunctionType;
	}

	public void setAssetInfodepartNum(String assetInfodepartNum) {
		this.assetInfodepartNum = assetInfodepartNum;
	}

	public void setAssetInfoclinical(String assetInfoclinical) {
		this.assetInfoclinical = assetInfoclinical;
	}

	public void setAssetInfoclinicalDeptName(String assetInfoclinicalDeptName) {
		this.assetInfoclinicalDeptName = assetInfoclinicalDeptName;
	}

	public void setHospitalNumber(String hospitalNumber) {
		this.hospitalNumber = hospitalNumber;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public void setEndByClosedTime(boolean endByClosedTime) {
		isEndByClosedTime = endByClosedTime;
	}

	public void setAssetOperationRecords(List<AssetOperationRecord> assetOperationRecords) {
		this.assetOperationRecords = assetOperationRecords;
	}

	public void setNurseSiteMaintainConfigCheckItemList(List<NurseSiteMaintainConfigCheckItem> nurseSiteMaintainConfigCheckItemList) {
		this.nurseSiteMaintainConfigCheckItemList = nurseSiteMaintainConfigCheckItemList;
	}

	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}

	public void setInstitutionUID(String institutionUID) {
		this.institutionUID = institutionUID;
	}

	public void setHospitalUID(String hospitalUID) {
		this.hospitalUID = hospitalUID;
	}

	public void setSiteUID(String siteUID) {
		this.siteUID = siteUID;
	}
}
