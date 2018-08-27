/*
 */
package com.ge.apm.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class TenantInfoConfig  {
	private static final String ArrivalInstallation="安装验收";
	private static final String AssetMDM="设备档案";
	private static final String WorkOrderMgmtWithoutWorkSchedule="维修管理(不含排班管理)";
	private static final String WorkOrderMgmt="维修排班管理";
	private static final String PreventiveMaintainceMgmt="预防维护";
	private static final String DeviceOperationKPIAnalysis="绩效分析";
	private static final String SysAdmin="系统配置";
	private static final String AssetKnowledgeBaseMgmt="知识文档";
	private static final String NurseSite="护士站";
	private static final String MeasuringMgmt="计量管理";
	public static final String VERSION_FUNCTION="业务版";
	public static final String VERSION_COMMON="通用版";
	public static final String VERSION_ULTRASOUND="超声科室版";
	private static List<String> listVersions;
	
	//到货安装,安装验收
	private Boolean blnArrivalInstallation=false;
	//设备档案,设备档案
	private Boolean blnAssetMDM=false;
	//维修流程,维修管理(不含排班管理)
	private Boolean blnWorkOrderMgmtWithoutWorkSchedule=false;
	//维修流程,维修排班管理
	private Boolean blnWorkOrderMgmt=false;
	//预防维护,预防维护
	private Boolean blnPreventiveMaintainceMgmt=false;
	//绩效分析,绩效分析
	private Boolean blnDeviceOperationKPIAnalysis=false;
	//系统配置,系统配置
	private Boolean blnSysAdmin=false;
	//知识文档，知识文档
	private Boolean blnAssetKnowledgeBaseMgmt=false;
	//护士站，护士站
	private Boolean blnNurseSite=false;
	//计量管理，计量管理
	private Boolean blnMeasuringMgmt=false;
	public Boolean getBlnArrivalInstallation() {
		return blnArrivalInstallation;
	}
	public void setBlnArrivalInstallation(Boolean blnArrivalInstallation) {
		this.blnArrivalInstallation = blnArrivalInstallation;
	}
	public Boolean getBlnAssetMDM() {
		return blnAssetMDM;
	}
	public void setBlnAssetMDM(Boolean blnAssetMDM) {
		this.blnAssetMDM = blnAssetMDM;
	}
	public Boolean getBlnWorkOrderMgmtWithoutWorkSchedule() {
		return blnWorkOrderMgmtWithoutWorkSchedule;
	}
	public void setBlnWorkOrderMgmtWithoutWorkSchedule(Boolean blnWorkOrderMgmtWithoutWorkSchedule) {
		this.blnWorkOrderMgmtWithoutWorkSchedule = blnWorkOrderMgmtWithoutWorkSchedule;
	}
	public Boolean getBlnWorkOrderMgmt() {
		return blnWorkOrderMgmt;
	}
	public void setBlnWorkOrderMgmt(Boolean blnWorkOrderMgmt) {
		this.blnWorkOrderMgmt = blnWorkOrderMgmt;
	}
	public Boolean getBlnPreventiveMaintainceMgmt() {
		return blnPreventiveMaintainceMgmt;
	}
	public void setBlnPreventiveMaintainceMgmt(Boolean blnPreventiveMaintainceMgmt) {
		this.blnPreventiveMaintainceMgmt = blnPreventiveMaintainceMgmt;
	}
	public Boolean getBlnDeviceOperationKPIAnalysis() {
		return blnDeviceOperationKPIAnalysis;
	}
	public void setBlnDeviceOperationKPIAnalysis(Boolean blnDeviceOperationKPIAnalysis) {
		this.blnDeviceOperationKPIAnalysis = blnDeviceOperationKPIAnalysis;
	}
	public Boolean getBlnSysAdmin() {
		return blnSysAdmin;
	}
	public void setBlnSysAdmin(Boolean blnSysAdmin) {
		this.blnSysAdmin = blnSysAdmin;
	}
	public Boolean getBlnAssetKnowledgeBaseMgmt() {
		return blnAssetKnowledgeBaseMgmt;
	}
	public void setBlnAssetKnowledgeBaseMgmt(Boolean blnAssetKnowledgeBaseMgmt) {
		this.blnAssetKnowledgeBaseMgmt = blnAssetKnowledgeBaseMgmt;
	}
	public Boolean getBlnNurseSite() {
		return blnNurseSite;
	}
	public void setBlnNurseSite(Boolean blnNurseSite) {
		this.blnNurseSite = blnNurseSite;
	}
	public Boolean getBlnMeasuringMgmt() {
		return blnMeasuringMgmt;
	}
	public void setBlnMeasuringMgmt(Boolean blnMeasuringMgmt) {
		this.blnMeasuringMgmt = blnMeasuringMgmt;
	}
	
	public static List<String> getVersions(){
		if(listVersions==null){
    		listVersions=new ArrayList<String>();
    		listVersions.add(VERSION_FUNCTION);
    		listVersions.add(VERSION_COMMON);
    		listVersions.add(VERSION_ULTRASOUND);
    	}
		return listVersions;
	}
	
	public void putStr(String str) {
		if(StringUtils.isEmpty(str)){
			return;
		}
		if (str.equals(ArrivalInstallation)) {
			this.blnArrivalInstallation=true;
		} else if (str.equals(AssetMDM)) {
			this.blnAssetMDM=true;
		} else if (str.equals(WorkOrderMgmtWithoutWorkSchedule)) {
			this.blnWorkOrderMgmtWithoutWorkSchedule=true;
		} else if (str.equals(WorkOrderMgmt)) {
			this.blnWorkOrderMgmt=true;
		} else if (str.equals(PreventiveMaintainceMgmt)) {
			this.blnPreventiveMaintainceMgmt=true;
		} else if (str.equals(DeviceOperationKPIAnalysis)) {
			this.blnDeviceOperationKPIAnalysis=true;
		} else if (str.equals(SysAdmin)) {
			this.blnSysAdmin=true;
		} else if (str.equals(AssetKnowledgeBaseMgmt)) {
			this.blnAssetKnowledgeBaseMgmt=true;
		} else if (str.equals(NurseSite)) {
			this.blnNurseSite=true;
		} else if (str.equals(MeasuringMgmt)) {
			this.blnMeasuringMgmt=true;
		}
	}
	
	public List<Object> getListStr(){
		List<Object> result=new ArrayList<Object>();
		if(this.blnArrivalInstallation){
			result.add(ArrivalInstallation);
		}
		if(this.blnAssetMDM){
			result.add(AssetMDM);
		}
		if(this.blnWorkOrderMgmtWithoutWorkSchedule){
			result.add(WorkOrderMgmtWithoutWorkSchedule);
		}
		if(this.blnWorkOrderMgmt){
			result.add(WorkOrderMgmt);
		}
		if(this.blnPreventiveMaintainceMgmt){
			result.add(PreventiveMaintainceMgmt);
		}
		if(this.blnDeviceOperationKPIAnalysis){
			result.add(DeviceOperationKPIAnalysis);
		}
		if(this.blnSysAdmin){
			result.add(SysAdmin);
		}
		if(this.blnAssetKnowledgeBaseMgmt){
			result.add(AssetKnowledgeBaseMgmt);
		}
		if(this.blnNurseSite){
			result.add(NurseSite);
		}
		if(this.blnMeasuringMgmt){
			result.add(MeasuringMgmt);
		}
		return result;
	}
	
	public String getStr(){
		StringBuilder sb=new StringBuilder();
		if(this.blnArrivalInstallation){
			sb.append(ArrivalInstallation+",");
		}
		if(this.blnAssetMDM){
			sb.append(AssetMDM+",");
		}
		if(this.blnWorkOrderMgmtWithoutWorkSchedule){
			sb.append(WorkOrderMgmtWithoutWorkSchedule+",");
		}
		if(this.blnWorkOrderMgmt){
			sb.append(WorkOrderMgmt+",");
		}
		if(this.blnPreventiveMaintainceMgmt){
			sb.append(PreventiveMaintainceMgmt+",");
		}
		if(this.blnDeviceOperationKPIAnalysis){
			sb.append(DeviceOperationKPIAnalysis+",");
		}
		if(this.blnSysAdmin){
			sb.append(SysAdmin+",");
		}
		if(this.blnAssetKnowledgeBaseMgmt){
			sb.append(AssetKnowledgeBaseMgmt+",");
		}
		if(this.blnNurseSite){
			sb.append(NurseSite+",");
		}
		if(this.blnMeasuringMgmt){
			sb.append(MeasuringMgmt+",");
		}
		return sb.toString();
	}
	
	public void setValueWithVersion(String str){
		this.blnWorkOrderMgmt=true;
		this.blnNurseSite=true;
		this.blnMeasuringMgmt=true;
		
		this.blnArrivalInstallation=true;
		this.blnAssetMDM=true;
		this.blnWorkOrderMgmtWithoutWorkSchedule=true;
		this.blnPreventiveMaintainceMgmt=true;
		this.blnDeviceOperationKPIAnalysis=true;
		this.blnSysAdmin=true;
		this.blnAssetKnowledgeBaseMgmt=true;
		if(str.equals(VERSION_COMMON)){
			
		}
		else if(str.equals(VERSION_ULTRASOUND)){
			this.blnWorkOrderMgmt=false;
			this.blnNurseSite=false;
			this.blnMeasuringMgmt=false;
		}
		else if(str.equals(VERSION_FUNCTION)){
			this.blnWorkOrderMgmt=false;
			this.blnNurseSite=false;
			this.blnMeasuringMgmt=false;
		}
		else{
			
		}
	}
}
