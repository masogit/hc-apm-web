package com.ge.apm.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "asset_achievements")
public class AssetAchievements implements Serializable{
	private static final long serialVersionUID = -1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;
    
    @Column(name = "asset_group")
	private Integer assetGroup;//设备类型
    
    @Column(name = "manufacture")
	private String manufacture;//品牌
    
    @Column(name = "function_type")
	private String functionType;//设备型号

	@Column(name = "record_date")
	private String recordDate;//填写日期
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

	@Column(name="institution_uid",columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name="hospital_uid",columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name="site_uid",columnDefinition = "CHAR(32)")
	private String siteUID;

	@Column(name="user_uid",columnDefinition = "CHAR(32)")
	private String userUid;

	@Basic(optional = false)
	@NotNull
	@Column(name = "site_id")
	private Integer siteId;
    
    @Column(name="uid",columnDefinition = "CHAR(32)")
    private String uid;

	@Column(name="asset_uid")
	private String assetUid;
    
    @Column(name = "achievements_report")
	private String achievementsReport;//绩效报告
    
	@Column(name = "report_storage_id")
	private String reportStorageId; // 报告模版

	@Column(name = "report_template_name")
	private String reportTemplateName; // 报告模版名称
    
    @Column(name = "remark")
    private String remark;//备注

    @Column(name = "asset_name")
    private String assetName;//设备名称

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Column(name = "created_date", nullable = true)
	private Date createdDate;

	@PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
		createdDate = new Date();
    }
}
