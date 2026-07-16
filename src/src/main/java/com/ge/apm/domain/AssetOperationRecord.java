package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Cache;
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
@Cache(usage = CacheConcurrencyStrategy.NONE)
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

	//二维码
	@Size(max = 64)
	@Column(name = "qr_code")
	private String assetInfoqrCode;

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

	@Column(name = "description",columnDefinition = "text")
	private String description;

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AssetOperationRecord)) {
			return false;
		}
		AssetOperationRecord other = (AssetOperationRecord) object;
		return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
	}

	@Override
	public String toString() {
		return "AssetOperationRecord [ id=" + id + " ]";
	}

}
