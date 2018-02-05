package com.ge.apm.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "tube_config")
public class TubeConfig {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "asset_uid")
    private String assetUid;
    
    @Column(name = "seria_num")
    private String seriaNum;
    
    @Column(name = "max_total_patient_exam")
    private Integer maxTotalPatientExam;//最大病人扫描量
    
    @Column(name = "max_total_tube_mas")
    private Integer maxTotalTubeMAS;//最大管电流
    
    @Column(name = "max_total_tube_scan")
    private Integer maxTotalTubeScan;//最大曝光量
    
    @Column(name = "patient_exam_due_date")
    private Date patientExamDueDate;//检查数告警时间
    
    @Column(name = "tube_mas_due_date")
    private Date tubeMASDueDate;//管电流告警时间
    
    @Column(name = "tube_scan_due_date")
    private Date tubeScanDueDate;//曝光量告警时间‘yyyy-MM-dd’
    
    @Column(name = "in_use")
    private Integer inUse;//是否在用 0、否 1、是
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;

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

	public Integer getMaxTotalPatientExam() {
		return maxTotalPatientExam;
	}

	public void setMaxTotalPatientExam(Integer maxTotalPatientExam) {
		this.maxTotalPatientExam = maxTotalPatientExam;
	}

	public Integer getMaxTotalTubeMAS() {
		return maxTotalTubeMAS;
	}

	public void setMaxTotalTubeMAS(Integer maxTotalTubeMAS) {
		this.maxTotalTubeMAS = maxTotalTubeMAS;
	}

	public Integer getMaxTotalTubeScan() {
		return maxTotalTubeScan;
	}

	public void setMaxTotalTubeScan(Integer maxTotalTubeScan) {
		this.maxTotalTubeScan = maxTotalTubeScan;
	}

	public Date getPatientExamDueDate() {
		return patientExamDueDate;
	}

	public void setPatientExamDueDate(Date patientExamDueDate) {
		this.patientExamDueDate = patientExamDueDate;
	}

	public Date getTubeMASDueDate() {
		return tubeMASDueDate;
	}

	public void setTubeMASDueDate(Date tubeMASDueDate) {
		this.tubeMASDueDate = tubeMASDueDate;
	}

	public Date getTubeScanDueDate() {
		return tubeScanDueDate;
	}

	public void setTubeScanDueDate(Date tubeScanDueDate) {
		this.tubeScanDueDate = tubeScanDueDate;
	}

	public Integer getInUse() {
		return inUse;
	}

	public void setInUse(Integer inUse) {
		this.inUse = inUse;
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
