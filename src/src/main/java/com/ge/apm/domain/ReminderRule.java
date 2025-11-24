package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.apm.domain.reminder.WechatParam;
import com.ge.apm.domain.reminder.rule.RuleParam;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503310146
 */
@Data
@Entity
@Table(name = "reminder_rule")
public class ReminderRule implements Serializable {

    private static final long serialVersionUID = 8492409962179388085L;

    public static final String ASSET_LIFECYCLE_MSG = "{'first':'即将达到使用年限的设备提醒','keyword1':'设备名称:{name},设备编号:{departNum},制造商序列号:{serialNum}'}";
    public static final String ASSET_WARRANTY_DATE_MSG = "{'first':'即将到达维保截止日期设备提醒','keyword1':'{name},{departNum}'}";
    public static final String ASSET_PROBATION_EXPIRATION_MSG = "{'first':'设备试用到期提醒','keyword1':'{name},{functionType},{assetDeptName}'}";
    public static final String CONTRACT_OVERDUE_MSG = "{'first':'合同应到货日期逾期提醒','keyword1':'{name}合同已逾期，请尽快处理！'}";
    public static final String CONTRACT_ACK_MSG = "{'first':'合同验收提醒','keyword1':'合同号：{contractNum}，状态：{completion}，请尽快验收完成确认！'}";
    public static final String NURSE_SITE_MSG = "{'first':'临床完成日常检查情况','keyword1':'{deptName}，当日应检查设备{count}台，未检查设备{unfinished}台'}";
    public static final String UNATTENDED_OVERDUE_MSG = "{'first':'无人值守借用超期提醒','keyword1':'检测到{clinicalDeptName}已借用设备{assetName}({assetNumber})已超过{chargingDays}天，请尽快归还！'}";
    public static final String SUPPLIER_REVIEW_MSG = "{'first':'供应商关联审核提醒','keyword1':'{name}({type})'}";
    public static final String UNATTENDED_APPROVAL_MSG = "{'first':'无人值守审批提醒','keyword1':'{swapIndex}','keyword2':'{clinicalDeptName}申请借用{assetName}审批,请及时处理！','keyword3':'{applyTime}','linkUrl':'/wx/myReservation'}";
    public static final String ADJUSTMENT_CENTER_RETURN_MSG = "{'first':'设备借用超期','keyword1':'检测到{clinicalDeptName}已借用设备{assetName}({assetNumber})已超过{chargingDays}天，请尽快归还！'}";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name = "name")
    private String name;

    @Column(name = "state")
    private Integer state;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "reminder_type")
    private String reminderType;

    @Column(name = "parent_uid")
    private String parentUid;

    @Column(name = "parameters", columnDefinition = "text")
    private String parameters;

    @Transient
    @TableField(exist = false)
    private RuleParam ruleParam = new RuleParam();

    @Transient
    @TableField(exist = false)
    private WechatParam wechatParam = new WechatParam();

    @Column(name = "push_date_unit")
    private Integer pushDateUnit;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReminderRule)) {
            return false;
        }
        ReminderRule other = (ReminderRule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
