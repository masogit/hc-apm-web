package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

/**
 * Created by 503035627 on 2017/11/6.
 */

@Entity
@Table(name = "electric_safety_data")
public class ElectricSafetyData implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
	
	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	@Size(max = 32)
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

	// 警告信息
	@Column(name = "warn_message")
	private String warnMessage;

	// 存储器日期
	@Column(name = "storage_date")
	private Date storageDate;

	// 检测仪日期
	@Column(name = "check_date")
	private Date checkDate;
//delete,oneGroupApp
//	@Column(name = "group_type")
//	private String groupType;
	//设备uid
	@Column(name = "asset_uid")
	private String assetUid;
	
	//对应asset_info 的id
	@Column(name = "asset_id")
	private Integer assetId;
	
	//对应v2_pm_order 的id
	@Column(name = "v2_pm_order_id")
	private String v2_PmOrderId;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "last_modified_by")
	private Integer lastModifiedBy;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "last_modified_date")
	private Date lastModifiedDate;
	
//	2,——》 检测类型
	@Column(name = "check_type")
	private String checkType;
	
//	2,4,——》 类别
	@Column(name = "type")
	private String type;
	
//	2,——》 名称
	@Column(name = "name")
	private String name;
	
//	2,——》 标准名称
	@Column(name = "standard_name")
	private String standardName;
	
//	2,——》应用组数
	@Column(name="app_group_count")
	private String appGroupCount;
	
//	5，——》测试仪日期（格式：日-月-年）
	@Column(name="origin_check_date")
	private String originCheckDate;
	
//	6，——》测试仪时间（格式：H:M:S）
	@Column(name="origin_check_time")
	private String originCheckTime;
	
//	7,——》保护接地阻抗（单位：Ω）
	@Column(name = "protect_ground_impedance")
	private String protectGroundImpedance;
//	8,——》保护接地阻抗参考值（<0.2Ω）
	@Column(name = "protect_ground_impedance_ref_val")
	private String protectGroundImpedanceRefVal;
	
//	21,——》等效设备漏电流（单位：μA）
	@Column(name="equipment_electric_leakage")
	private String equipmentElectricLeakage;
	
//	22,——》等效设备漏电流参考值（<1.0mA）
	@Column(name="equipment_electric_leakage_ref_val")
	private String equipmentElectricLeakageRefVal;
	
//	23,——》患者漏电流（应用部分加电）（单位：μA）
	@Column(name = "patient_electric_leakage_app")
	private String patientElectricLeakage_app;
//	24,——》患者漏电流（应用部分加电）参考值（<50.0μA）
	@Column(name = "patient_electric_leakage_ref_val_app")
	private String patientElectricLeakageRefVal_app;
//	25,——》对地漏电流（正常状态）（单位：mA）
	@Column(name = "ground_electric_leakage_normal")
	private String groundElectricLeakage_normal;
//	26,——》对地漏电流（正常状态）参考值（<0.5mA）
	@Column(name = "ground_electric_leakage_ref_val_normal")
	private String groundElectricLeakageRefVal_normal;
//	27,——》对地漏电流（单一故障状态）（单位：mA）
	@Column(name = "ground_electric_leakage_single_error")
	private String groundElectricLeakage_singleError;
//	28,——》对地漏电流（单一故障状态）参考值（<1.0mA）
	@Column(name = "ground_electric_leakage_ref_val_single_error")
	private String groundElectricLeakageRefVal_singleError;
//	29,——》外壳漏电流（正常状态）（单位：μA）
	@Column(name = "shell_electric_leakage_normal")
	private String shellElectricLeakage_normal;
//	30,——》外壳漏电流（正常状态）参考值（<100μA）
	@Column(name = "shell_electric_leakage_ref_val_normal")
	private String shellElectricLeakageRefVal_normal;
//	31,——》外壳漏电流（单一故障状态）（单位：μA）
	@Column(name = "shell_electric_leakage_single_error")
	private String shellElectricLeakage_singleError;
//	32,——》外壳漏电流（单一故障状态）参考值（<0.5mA）
	@Column(name = "shell_electric_leakage_ref_val_single_error")
	private String shellElectricLeakageRefVal_singleError;
//	33,——》患者漏电流AC（正常状态）（单位：μA）
	@Column(name = "patient_electric_leakage_ac_normal")
	private String patientElectricLeakageAC_normal;
//	34,——》患者漏电流AC（正常状态）参考值（<10.0μA）
	@Column(name = "patient_electric_leakage_ref_val_ac_normal")
	private String patientElectricLeakageRefValAC_normal;
//	35,——》患者漏电流AC（单一故障状态）（单位：μA）
	@Column(name = "shell_electric_leakage_ac_single_error")
	private String shellElectricLeakageAC_singleError;
//	36,——》患者漏电流AC（单一故障状态）参考值（<50.0μA）
	@Column(name = "shell_electric_leakage_ref_val_ac_single_error")
	private String shellElectricLeakageRefValAC_singleError;
//	37,——》患者漏电流DC（正常状态）（单位：μA）
	@Column(name = "patient_electric_leakage_dc_normal")
	private String patientElectricLeakageDC_normal;
//	38,——》患者漏电流DC（正常状态）参考值（<10.0μA）
	@Column(name = "patient_electric_leakage_ref_val_dc_normal")
	private String patientElectricLeakageRefValDC_normal;
//	39,——》患者漏电流DC（单一故障状态）（单位：μA）
	@Column(name = "shell_electric_leakage_dc_single_error")
	private String shellElectricLeakageDC_singleError;
//	40,——》患者漏电流DC（单一故障状态）参考值（<50.0μA）
	@Column(name = "shell_electric_leakage_ref_val_dc_single_error")
	private String shellElectricLeakageRefValDC_singleError;
//	41,——》患者辅助电流AC（正常状态）（单位：μA）
	@Column(name = "patient_assis_electric_ac_normal")
	private String patientAssisElectricAC_normal;
//	42,——》患者辅助电流AC（正常状态）参考值（<10.0μA）
	@Column(name = "patient_assis_electric_ref_val_ac_normal")
	private String patientAssisElectricRefValAC_normal;
//	43,——》患者辅助电流AC（单一故障状态）（单位：μA）
	@Column(name = "patient_assis_electric_ac_single_error")
	private String patientAssisElectricAC_singleError;
//	44,——》患者辅助电流AC（单一故障状态）参考值（<50.0μA）
	@Column(name = "patient_assis_electric_ref_val_ac_single_error")
	private String patientAssisElectricRefValAC_singleError;
//	45,——》患者辅助电流DC（正常状态）（单位：μA）
	@Column(name = "patient_assis_electric_dc_normal")
	private String patientAssisElectricDC_normal;
//	46,——》患者辅助电流DC（正常状态）参考值（<10.0μA）
	@Column(name = "patient_assis_electric_ref_val_dc_normal")
	private String patientAssisElectricRefValDC_normal;
//	47,——》患者辅助电流DC（单一故障状态）（单位：μA）
	@Column(name = "patient_assis_electric_dc_single_error")
	private String patientAssisElectricDC_singleError;
//	48,——》患者辅助电流DC（单一故障状态）参考值（<50.0μA）
	@Column(name = "patient_assis_electric_ref_val_dc_single_error")
	private String patientAssisElectricRefValDC_singleError;
//	51,——》市电电压实际值
	@Column(name = "city_voltage")
	private String cityVoltage;
//	52,——》市电电压参考值（230V）
	@Column(name = "city_voltage_ref_val")
	private String cityVoltageRefVal;
	
//	55，——》存储器日期（格式：日-月-年）
	@Column(name = "origin_storage_date")
	private String originStorageDate;
	
//	56，——》存储器时间（格式：H:M）
	@Column(name = "origin_storage_time")
	private String originStorageTime;
	
//	60，——》 资产编号
	@Column(name = "financing_num")
	private String financingNum;
	
//	75，——》序号
	@Column(name = "sequence_num")
	private String sequenceNum;
	
	@JoinColumn(name = "asset_id", referencedColumnName = "id",updatable=false,insertable=false)
    @ManyToOne(fetch = FetchType.EAGER,optional=true)
    private AssetInfo assetInfo;
	
	@JoinColumn(name = "v2_pm_order_id", referencedColumnName = "id",updatable=false,insertable=false)
    @ManyToOne(fetch = FetchType.EAGER,optional=true)
    private V2_PmOrder v2PmOrder;
	
	@Transient
	private List<V2_PmOrder> listV2PmOrder;

	// 文件名
	@Transient
	private String fileName;

	// 所在文件行数
	@Transient
	private int lineIndex;

	// 原始值
	@Transient
	private Map<String, Object> values;
	
	@Transient
	private static Map<String,String> mapLabelAttributes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWarnMessage() {
		return warnMessage;
	}

	public void setWarnMessage(String warnMessage) {
		this.warnMessage = warnMessage;
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public Date getStorageDate() {
		return storageDate;
	}

	public void setStorageDate(Date storageDate) {
		this.storageDate = storageDate;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		if (this.checkType == null)
			this.checkType = checkType;
		else if (!this.checkType.equals(checkType)) {
			this.warnMessage = "duplicate check Type:" + this.checkType + " AND " + checkType;
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		if(this.type==null){
			this.type = type;
		}
		else if (!this.type.equals(type)) {
//			this.warnMessage = "duplicate Type:" + this.type + " AND " + type;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandardName() {
		return standardName;
	}

	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(Integer lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getFinancingNum() {
		return financingNum;
	}

	public void setFinancingNum(String financingNum) {
		this.financingNum = financingNum;
	}
	
	public String getAssetUid() {
		return assetUid;
	}

	public void setAssetUid(String assetUid) {
		this.assetUid = assetUid;
	}

	public V2_PmOrder getV2PmOrder() {
		return v2PmOrder;
	}

	public void setV2PmOrder(V2_PmOrder v2PmOrder) {
		this.v2PmOrder = v2PmOrder;
	}

	public List<V2_PmOrder> getListV2PmOrder() {
		return listV2PmOrder;
	}

	public void setListV2PmOrder(List<V2_PmOrder> listV2PmOrder) {
		this.listV2PmOrder = listV2PmOrder;
	}

	public String getV2_PmOrderId() {
		return v2_PmOrderId;
	}

	public void setV2_PmOrderId(String v2_PmOrderId) {
		this.v2_PmOrderId = v2_PmOrderId;
	}

	public String getProtectGroundImpedance() {
		return protectGroundImpedance;
	}

	public void setProtectGroundImpedance(String protectGroundImpedance) {
		this.protectGroundImpedance = protectGroundImpedance;
	}
	

	public String getPatientElectricLeakage_app() {
		return patientElectricLeakage_app;
	}

	public void setPatientElectricLeakage_app(String patientElectricLeakage_app) {
		this.patientElectricLeakage_app = patientElectricLeakage_app;
	}
	

	public String getGroundElectricLeakage_normal() {
		return groundElectricLeakage_normal;
	}

	public void setGroundElectricLeakage_normal(String groundElectricLeakage_normal) {
		this.groundElectricLeakage_normal = groundElectricLeakage_normal;
	}
	

	public String getShellElectricLeakage_normal() {
		return shellElectricLeakage_normal;
	}

	public void setShellElectricLeakage_normal(String shellElectricLeakage_normal) {
		this.shellElectricLeakage_normal = shellElectricLeakage_normal;
	}
	
	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
	}
	
	
	public AssetInfo getAssetInfo() {
		return assetInfo;
	}
	
	public void setAssetInfo(AssetInfo assetInfo) {
		this.assetInfo = assetInfo;
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
	
	public String getEquipmentElectricLeakage() {
		return equipmentElectricLeakage;
	}

	public void setEquipmentElectricLeakage(String equipmentElectricLeakage) {
		this.equipmentElectricLeakage = equipmentElectricLeakage;
	}

	public String getAppGroupCount() {
		return appGroupCount;
	}

	public void setAppGroupCount(String appGroupCount) {
		this.appGroupCount = appGroupCount;
	}

	public String getProtectGroundImpedanceRefVal() {
		return protectGroundImpedanceRefVal;
	}

	public void setProtectGroundImpedanceRefVal(String protectGroundImpedanceRefVal) {
		this.protectGroundImpedanceRefVal = protectGroundImpedanceRefVal;
	}

	public String getPatientElectricLeakageRefVal_app() {
		return patientElectricLeakageRefVal_app;
	}

	public void setPatientElectricLeakageRefVal_app(String patientElectricLeakageRefVal_app) {
		this.patientElectricLeakageRefVal_app = patientElectricLeakageRefVal_app;
	}

	public String getGroundElectricLeakageRefVal_normal() {
		return groundElectricLeakageRefVal_normal;
	}

	public void setGroundElectricLeakageRefVal_normal(String groundElectricLeakageRefVal_normal) {
		this.groundElectricLeakageRefVal_normal = groundElectricLeakageRefVal_normal;
	}

	public String getGroundElectricLeakage_singleError() {
		return groundElectricLeakage_singleError;
	}

	public void setGroundElectricLeakage_singleError(String groundElectricLeakage_singleError) {
		this.groundElectricLeakage_singleError = groundElectricLeakage_singleError;
	}

	public String getGroundElectricLeakageRefVal_singleError() {
		return groundElectricLeakageRefVal_singleError;
	}

	public void setGroundElectricLeakageRefVal_singleError(String groundElectricLeakageRefVal_singleError) {
		this.groundElectricLeakageRefVal_singleError = groundElectricLeakageRefVal_singleError;
	}

	public String getShellElectricLeakageRefVal_normal() {
		return shellElectricLeakageRefVal_normal;
	}

	public void setShellElectricLeakageRefVal_normal(String shellElectricLeakageRefVal_normal) {
		this.shellElectricLeakageRefVal_normal = shellElectricLeakageRefVal_normal;
	}

	public String getShellElectricLeakage_singleError() {
		return shellElectricLeakage_singleError;
	}

	public void setShellElectricLeakage_singleError(String shellElectricLeakage_singleError) {
		this.shellElectricLeakage_singleError = shellElectricLeakage_singleError;
	}

	public String getShellElectricLeakageRefVal_singleError() {
		return shellElectricLeakageRefVal_singleError;
	}

	public void setShellElectricLeakageRefVal_singleError(String shellElectricLeakageRefVal_singleError) {
		this.shellElectricLeakageRefVal_singleError = shellElectricLeakageRefVal_singleError;
	}

	public String getPatientElectricLeakageAC_normal() {
		return patientElectricLeakageAC_normal;
	}

	public void setPatientElectricLeakageAC_normal(String patientElectricLeakageAC_normal) {
		this.patientElectricLeakageAC_normal = patientElectricLeakageAC_normal;
	}

	public String getPatientElectricLeakageRefValAC_normal() {
		return patientElectricLeakageRefValAC_normal;
	}

	public void setPatientElectricLeakageRefValAC_normal(String patientElectricLeakageRefValAC_normal) {
		this.patientElectricLeakageRefValAC_normal = patientElectricLeakageRefValAC_normal;
	}

	public String getShellElectricLeakageAC_singleError() {
		return shellElectricLeakageAC_singleError;
	}

	public void setShellElectricLeakageAC_singleError(String shellElectricLeakageAC_singleError) {
		this.shellElectricLeakageAC_singleError = shellElectricLeakageAC_singleError;
	}

	public String getShellElectricLeakageRefValAC_singleError() {
		return shellElectricLeakageRefValAC_singleError;
	}

	public void setShellElectricLeakageRefValAC_singleError(String shellElectricLeakageRefValAC_singleError) {
		this.shellElectricLeakageRefValAC_singleError = shellElectricLeakageRefValAC_singleError;
	}

	public String getPatientAssisElectricAC_normal() {
		return patientAssisElectricAC_normal;
	}

	public void setPatientAssisElectricAC_normal(String patientAssisElectricAC_normal) {
		this.patientAssisElectricAC_normal = patientAssisElectricAC_normal;
	}

	public String getPatientAssisElectricRefValAC_normal() {
		return patientAssisElectricRefValAC_normal;
	}

	public void setPatientAssisElectricRefValAC_normal(String patientAssisElectricRefValAC_normal) {
		this.patientAssisElectricRefValAC_normal = patientAssisElectricRefValAC_normal;
	}

	public String getPatientAssisElectricAC_singleError() {
		return patientAssisElectricAC_singleError;
	}

	public void setPatientAssisElectricAC_singleError(String patientAssisElectricAC_singleError) {
		this.patientAssisElectricAC_singleError = patientAssisElectricAC_singleError;
	}

	public String getPatientAssisElectricRefValAC_singleError() {
		return patientAssisElectricRefValAC_singleError;
	}

	public void setPatientAssisElectricRefValAC_singleError(String patientAssisElectricRefValAC_singleError) {
		this.patientAssisElectricRefValAC_singleError = patientAssisElectricRefValAC_singleError;
	}

	public String getEquipmentElectricLeakageRefVal() {
		return equipmentElectricLeakageRefVal;
	}

	public void setEquipmentElectricLeakageRefVal(String equipmentElectricLeakageRefVal) {
		this.equipmentElectricLeakageRefVal = equipmentElectricLeakageRefVal;
	}

	public String getOriginCheckDate() {
		return originCheckDate;
	}

	public void setOriginCheckDate(String originCheckDate) {
		this.originCheckDate = originCheckDate;
	}

	public String getOriginCheckTime() {
		return originCheckTime;
	}

	public void setOriginCheckTime(String originCheckTime) {
		this.originCheckTime = originCheckTime;
	}

	public String getPatientElectricLeakageDC_normal() {
		return patientElectricLeakageDC_normal;
	}

	public void setPatientElectricLeakageDC_normal(String patientElectricLeakageDC_normal) {
		this.patientElectricLeakageDC_normal = patientElectricLeakageDC_normal;
	}

	public String getPatientElectricLeakageRefValDC_normal() {
		return patientElectricLeakageRefValDC_normal;
	}

	public void setPatientElectricLeakageRefValDC_normal(String patientElectricLeakageRefValDC_normal) {
		this.patientElectricLeakageRefValDC_normal = patientElectricLeakageRefValDC_normal;
	}

	public String getShellElectricLeakageDC_singleError() {
		return shellElectricLeakageDC_singleError;
	}

	public void setShellElectricLeakageDC_singleError(String shellElectricLeakageDC_singleError) {
		this.shellElectricLeakageDC_singleError = shellElectricLeakageDC_singleError;
	}

	public String getShellElectricLeakageRefValDC_singleError() {
		return shellElectricLeakageRefValDC_singleError;
	}

	public void setShellElectricLeakageRefValDC_singleError(String shellElectricLeakageRefValDC_singleError) {
		this.shellElectricLeakageRefValDC_singleError = shellElectricLeakageRefValDC_singleError;
	}

	public String getPatientAssisElectricDC_normal() {
		return patientAssisElectricDC_normal;
	}

	public void setPatientAssisElectricDC_normal(String patientAssisElectricDC_normal) {
		this.patientAssisElectricDC_normal = patientAssisElectricDC_normal;
	}

	public String getPatientAssisElectricRefValDC_normal() {
		return patientAssisElectricRefValDC_normal;
	}

	public void setPatientAssisElectricRefValDC_normal(String patientAssisElectricRefValDC_normal) {
		this.patientAssisElectricRefValDC_normal = patientAssisElectricRefValDC_normal;
	}

	public String getPatientAssisElectricDC_singleError() {
		return patientAssisElectricDC_singleError;
	}

	public void setPatientAssisElectricDC_singleError(String patientAssisElectricDC_singleError) {
		this.patientAssisElectricDC_singleError = patientAssisElectricDC_singleError;
	}

	public String getPatientAssisElectricRefValDC_singleError() {
		return patientAssisElectricRefValDC_singleError;
	}

	public void setPatientAssisElectricRefValDC_singleError(String patientAssisElectricRefValDC_singleError) {
		this.patientAssisElectricRefValDC_singleError = patientAssisElectricRefValDC_singleError;
	}

	public String getCityVoltage() {
		return cityVoltage;
	}

	public void setCityVoltage(String cityVoltage) {
		this.cityVoltage = cityVoltage;
	}

	public String getCityVoltageRefVal() {
		return cityVoltageRefVal;
	}

	public void setCityVoltageRefVal(String cityVoltageRefVal) {
		this.cityVoltageRefVal = cityVoltageRefVal;
	}

	public String getOriginStorageDate() {
		return originStorageDate;
	}

	public void setOriginStorageDate(String originStorageDate) {
		this.originStorageDate = originStorageDate;
	}

	public String getOriginStorageTime() {
		return originStorageTime;
	}

	public void setOriginStorageTime(String originStorageTime) {
		this.originStorageTime = originStorageTime;
	}

	public String getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(String sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public static Map<String,String> getLabelAttributeMapping(){
		if(mapLabelAttributes==null){
			mapLabelAttributes=new HashMap<String,String>();
		}
		//
//		mapLabelAttributes.put("protectGroundImpedance", "保护接地阻抗");
//		mapLabelAttributes.put("patientElectricLeakage_app", "患者漏电流");
//		mapLabelAttributes.put("groundElectricLeakage_normal", "对地漏电流");
//		mapLabelAttributes.put("shellElectricLeakage_normal", "外壳漏电流");
//		mapLabelAttributes.put("equipmentElectricLeakage", "设备漏电流");
//		mapLabelAttributes.put("originCheckDate", "测试仪日期");
//		mapLabelAttributes.put("originCheckTime", "测试仪时间");
		//
//		mapLabelAttributes.put("checkType", "检测类型");
//		mapLabelAttributes.put("type", "类别");
//		mapLabelAttributes.put("name", "名称");
//		mapLabelAttributes.put("standardName", "标准名称");
//		mapLabelAttributes.put("appGroupCount", "应用组数");
		mapLabelAttributes.put("protectGroundImpedance", "保护接地阻抗");
//		mapLabelAttributes.put("protectGroundImpedanceRefVal", "保护接地阻抗参考值");
		mapLabelAttributes.put("equipmentElectricLeakage", "等效设备漏电流");
//		mapLabelAttributes.put("equipmentElectricLeakageRefVal", "等效设备漏电流参考值");
		mapLabelAttributes.put("patientElectricLeakage_app", "患者漏电流(应用部分加电)");
//		mapLabelAttributes.put("patientElectricLeakageRefVal_app", "患者漏电流(应用部分加电)参考值");
		mapLabelAttributes.put("groundElectricLeakage_normal", "对地漏电流(正常状态)");
//		mapLabelAttributes.put("groundElectricLeakageRefVal_normal", "对地漏电流(正常状态)参考值");
		mapLabelAttributes.put("groundElectricLeakage_singleError", "对地漏电流(单一故障状态)");
//		mapLabelAttributes.put("groundElectricLeakageRefVal_singleError", "对地漏电流(单一故障状态)参考值");
		mapLabelAttributes.put("shellElectricLeakage_normal", "外壳漏电流(正常状态)");
//		mapLabelAttributes.put("shellElectricLeakageRefVal_normal", "外壳漏电流(正常状态)参考值");
		mapLabelAttributes.put("shellElectricLeakage_singleError", "外壳漏电流(单一故障状态)");
//		mapLabelAttributes.put("shellElectricLeakageRefVal_singleError", "外壳漏电流(单一故障状态)参考值");
		mapLabelAttributes.put("patientElectricLeakageAC_normal", "患者漏电流AC(正常状态)");
//		mapLabelAttributes.put("patientElectricLeakageRefValAC_normal", "患者漏电流AC(正常状态)参考值");
		mapLabelAttributes.put("shellElectricLeakageAC_singleError", "患者漏电流AC(单一故障状态)");
//		mapLabelAttributes.put("shellElectricLeakageRefValAC_singleError", "患者漏电流AC(单一故障状态)参考值");
		mapLabelAttributes.put("patientElectricLeakageDC_normal", "患者漏电流DC(正常状态)");
//		mapLabelAttributes.put("patientElectricLeakageRefValDC_normal", "患者漏电流DC(正常状态)参考值");
		mapLabelAttributes.put("shellElectricLeakageDC_singleError", "患者漏电流DC(单一故障状态)");
//		mapLabelAttributes.put("shellElectricLeakageRefValDC_singleError", "患者漏电流DC(单一故障状态)参考值");
		mapLabelAttributes.put("patientAssisElectricAC_normal", "患者辅助电流AC(正常状态)");
//		mapLabelAttributes.put("patientAssisElectricRefValAC_normal", "患者辅助电流AC(正常状态)参考值");
		mapLabelAttributes.put("patientAssisElectricAC_singleError", "患者辅助电流AC(单一故障状态)");
//		mapLabelAttributes.put("patientAssisElectricRefValAC_singleError", "患者辅助电流AC(单一故障状态)参考值");
		mapLabelAttributes.put("patientAssisElectricDC_normal", "患者辅助电流DC(正常状态)");
//		mapLabelAttributes.put("patientAssisElectricRefValDC_normal", "患者辅助电流DC(正常状态)参考值");
		mapLabelAttributes.put("patientAssisElectricDC_singleError", "患者辅助电流DC(单一故障状态)");
//		mapLabelAttributes.put("patientAssisElectricRefValDC_singleError", "患者辅助电流DC(单一故障状态)参考值");
		mapLabelAttributes.put("cityVoltage", "市电电压实际值");
//		mapLabelAttributes.put("cityVoltageRefVal", "市电电压参考值");
//		mapLabelAttributes.put("originStorageDate", "存储器日期");
//		mapLabelAttributes.put("originStorageTime", "存储器时间");
//		mapLabelAttributes.put("sequenceNum", "序号");
		return mapLabelAttributes;
	}
	
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ElectricSafetyData)) {
            return false;
        }
        ElectricSafetyData other = (ElectricSafetyData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ElectricSafetyData[ id=" + id + " ]";
    }
}
