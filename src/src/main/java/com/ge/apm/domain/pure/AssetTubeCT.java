package com.ge.apm.domain.pure;

import java.util.Date;

import javax.persistence.Column;

public class AssetTubeCT {
	private String assetUid;
	
	private String seriaNum;
	
    private Integer patientExam;//病人扫描值
	
    private Integer tubeMAS;//管电流值
	
    private Integer tubeScan;//曝光量值
	
    private Date inputDate;//用户输入时间
    
    public AssetTubeCT(String assetUid, String seriaNum, Integer patientExam, Integer tubeMAS, Integer tubeScan,
			Date inputDate) {
		this.assetUid = assetUid;
		this.seriaNum = seriaNum;
		this.patientExam = patientExam;
		this.tubeMAS = tubeMAS;
		this.tubeScan = tubeScan;
		this.inputDate = inputDate;
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
    
}
