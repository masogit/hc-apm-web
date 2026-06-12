package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 设备风险评估记录
 */
@Data
@Entity
@Table(name = "risk_assessment_record")
public class RiskAssessmentRecord {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /** 设备UID */
    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    /** 设备名称 */
    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    /** 评估创建时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_date")
    private Date createDate;

    /** 发生概率 */
    @Column(name = "probability")
    private Double probability;

    /** 评分 */
    @Column(name = "score")
    private Double score;

    /** 发生等级 */
    @Column(name = "occurrence_level")
    private String occurrenceLevel;

    /** 严重等级 */
    @Column(name = "severity_level")
    private String severityLevel;

    /** 综合风险等级 */
    @Column(name = "overall_risk")
    private String overallRisk;

    /** 综合风险等级编码 */
    @Column(name = "overall_risk_code")
    private Integer overallRiskCode;


    /** 设备风险评分 */
    @Column(name = "device_risk_score")
    private Double deviceRiskScore;

    /** 物理风险评分 */
    @Column(name = "physical_risk_score")
    private Double physicalRiskScore;

    /** 总严重程度评分 */
    @Column(name = "total_severity_score")
    private Double totalSeverityScore;

    /** 模型类型 */
    @Column(name = "model_type")
    private String modelType;

    /** 模型ID */
    @Column(name = "model_id", columnDefinition = "CHAR(32)")
    private String modelId;

    /** 模型版本 */
    @Column(name = "model_version", length = 50)
    private String modelVersion;
}
