package com.ge.apm.domain.his;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "his_equipment_data_source") //抓取的数据源
@Data
public class HisEquipmentDataSource implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //联机机构代码
    @Column(name = "org_code")
    private String orgCode;

    @Column(name = "org_name")
    private String orgName;
    //联机设备唯一编号（设备序列号）Eq_id,his为空
    @Column(name = "device_code")
    private String deviceCode;

    //病人住院号或门诊号
    @Column(name = "patient_registration_num")
    private String patientRegistrationNum;

    @Column(name = "prescription_num")//医嘱号、处方号、申请单号
    private String prescriptionNum;

    @Column(name = "device_dept_code")//收费项目代码, TODO 唯一吗？
    private String deviceDeptCode;

    @Column(name = "receipt_num") //收费数量(退费传负数)
    private Integer receiptNum;

    @Column(name = "unit") //单位
    private String unit;

    @Column(name = "unit_price") // 单价
    private BigDecimal unitPrice;

    @Column(name = "total_amount") //总金额，实付金额
    private BigDecimal totalAmount;

    @Column(name = "exec_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date execTime; //执行时间

    @Column(name = "exec_dept")
    private String execDept;//执行科室

    @Column(name = "exec_dept_code")
    private String execDeptCode;//执行科室code

    @Column(name = "exam_body_part")
    private String examBodyPart;//检查部位(LIS为检验项目或收费名称

    @Column(name = "patient_type")
    private String patientType;//来源

    @Column(name = "source_code")
    private String sourceCode;//系统来源

    @Column(name = "serial_num")
    private String serialNum;// 流水号 --新加

    @Column(name = "category")
    private String category; //类别

    @Column(name = "project_name")
    private String projectName; //项目名称

    @Column(name = "standards")
    private String standards; //规格

    @Column(name = "pay_amount") //应付金额
    private BigDecimal payAmount;

    @Column(name = "billing_department")
    private String billingDepartment; //开单科室

    @Column(name = "is_matched")//是否匹配过,默认是false
    private Boolean isMatched;


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name = "record_count")
    private Integer recordCount;  //记录条数

    @CreatedDate
    @Column(name = "create_date")
    private Date createDate;

    @Transient
    private String sfxmName;
    @Transient
    private String sfxmLb;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumns({
//            @JoinColumn(name = "device_dept_code", referencedColumnName = "sfxm_id", insertable = false,updatable = false),
//            @JoinColumn(name = "org_code", referencedColumnName = "hos_id", insertable = false,updatable = false),
//            @JoinColumn(name = "tenant_uid", referencedColumnName = "tenant_uid", insertable = false,updatable = false)
//    })
//    private HisEquipmentSfxm hisEquipmentSfxm;

    //excel行数
    @Transient
    private int lineNum;

    public Boolean getMatched() {
        return isMatched;
    }

    public void setMatched(Boolean matched) {
        isMatched = matched;
    }

}
