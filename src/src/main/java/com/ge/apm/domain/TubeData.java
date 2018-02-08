package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tube_data")
public class TubeData implements Serializable{
	
	private static final long serialVersionUID = -5214143837964509457L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "asset_uid")
	private String assetUid;
	
	@Column(name = "seria_num")
	private String seriaNum;
	
	@Column(name = "patient_exam")
    private Integer patientExam;//病人扫描值
	
	@Column(name = "tube_mas")
    private Integer tubeMAS;//管电流值
	
	@Column(name = "tube_scan")
    private Integer tubeScan;//曝光量值
	
	@Column(name = "input_date")
    private Date inputDate = new Date();//用户输入时间
	
	@Column(name = "ge_device_type")
	private Integer geDeviceType;//2，21，23 api拉取的数据 22用户手动输入的数据
	
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;
    
	public TubeData() {
		super();
	}

	public TubeData(Integer id,String assetUid, String seriaNum, Integer patientExam, Integer tubeMAS, Integer tubeScan,
			Date inputDate) {
		super();
		this.id = id;
		this.assetUid = assetUid;
		this.seriaNum = seriaNum;
		this.patientExam = patientExam;
		this.tubeMAS = tubeMAS;
		this.tubeScan = tubeScan;
		this.inputDate = inputDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAssetUid() {
		return assetUid;
	}

	public void setAssetUid(String assetUid) {
		this.assetUid = assetUid;
	}

	public String getSeriaNum() {
		return seriaNum;
	}

	public void setSeriaNum(String seriaNum) {
		this.seriaNum = seriaNum;
	}

	public Integer getPatientExam() {
		return patientExam;
	}

	public void setPatientExam(Integer patientExam) {
		this.patientExam = patientExam;
	}

	public Integer getTubeMAS() {
		return tubeMAS;
	}

	public void setTubeMAS(Integer tubeMAS) {
		this.tubeMAS = tubeMAS;
	}

	public Integer getTubeScan() {
		return tubeScan;
	}

	public void setTubeScan(Integer tubeScan) {
		this.tubeScan = tubeScan;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}

	public Integer getGeDeviceType() {
		return geDeviceType;
	}

	public void setGeDeviceType(Integer geDeviceType) {
		this.geDeviceType = geDeviceType;
	}

	public String getTenantUID() {
		return tenantUID;
	}

	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}

	public String getInstitutionUID() {
		return institutionUID;
	}

	public void setInstitutionUID(String institutionUID) {
		this.institutionUID = institutionUID;
	}

	public String getHospitalUID() {
		return hospitalUID;
	}

	public void setHospitalUID(String hospitalUID) {
		this.hospitalUID = hospitalUID;
	}

	public String getSiteUID() {
		return siteUID;
	}

	public void setSiteUID(String siteUID) {
		this.siteUID = siteUID;
	}
    
    
}
