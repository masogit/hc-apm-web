package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 503266962
 */

@Entity
@Table(name = "key_asset_report")
@Data
public class KeyAssetReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 标题
     */
    @Column(name = "form_title")
    private String formTitle;

    /**
     * 填表人
     */
    @Column(name = "form_filler")
    private String formFiller;

    /**
     * 填表日期
     */
    @Column(name = "form_fill_time")
    private Date formFillTime;

    /**
     * 设备名称
     */
    @Column(name = "asset_name")
    private String assetName;

    /**
     * 使用科室
     */
    @Column(name = "dept_id")
    private Integer deptId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", insertable = false, updatable = false)
    private OrgInfo clinicalDept;


    /**
     * 使用科室
     */
    @Column(name = "asset_dept_name")
    private String assetDeptName;//使用科室


    /**
     * 报表年份
     */
    @Column(name = "report_year")
    private Integer reportYear;

    /**
     * 报表类型
     * 1-月度分析 2-年度分析
     */
    @Column(name = "report_type")
    private Integer reportType;

    /**
     * 设备明细 json 型号、品牌、数量、价格、启用日期、折旧、维修支出、维保支出、耗材支出、历史收益
     */
    @Column(name = "asset_detail",columnDefinition = "text")
    private String assetDetail;

    /**
     * 设备运营情况 json 月份、检查人数、收入总和
     */
    @Column(name = "asset_operation",columnDefinition = "text")
    private String assetOperation;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "creator", columnDefinition = "VARCHAR(60)")
    private String creator;

    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
    @Column(name="summary", columnDefinition = "VARCHAR(2500)")
    private String summary;
}
