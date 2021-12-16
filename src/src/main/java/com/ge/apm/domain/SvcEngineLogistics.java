package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/6/28
 * @description :
 */
@Entity
@Table(name = "svc_engine_logistics")
public class SvcEngineLogistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * ID ，唯一标识，与运单号 +part NO 唯一对应
     */
    @Column(name = "line_id")
    private String lineID;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String order;

    /**
     * 设备编号
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 工程师姓名
     */
    @Column(name = "fe_name")
    private String feName;

    /**
     * 工程师 SSO 编号
     */
    @Column(name = "fe_sso")
    private String feSSO;

    /**
     * 备件编号
     */
    @Column(name = "item")
    private String item;

    /**
     * 数量
     */
    @Column(name = "qty")
    private Integer qty;

    /**
     * 医院名称
     */
    @Column(name = "hospital")
    private String hospital;

    /**
     * 订单状态（ 18 中状态，后续待确认哪些是：待发货、已发货、已送达）
     */
    @Column(name = "Decoded_Status")
    private String decodedStatus;

    /**
     * 物流公司缩写
     */
    @Column(name = "carrier")
    private String carrier;

    /**
     * 物流公司中文名字
     */
    @Column(name = "carrier_chinese")
    private String carrierChinese;

    /**
     * 物流公司跳转链接
     */
    @Column(name = "hyper_link")
    private String hyperlink;

    /**
     * 运单号
     */
    @Column(name = "awb")
    private String awb;

    /**
     * 订单创建时间
     */
    @Column(name = "creation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationDate;

    /**
     * 当前发货国家
     */
    @Column(name = "org_country")
    private String orgCountry;

    /**
     * 当前发货城市
     */
    @Column(name = "org_city")
    private String orgCity;

    /**
     * 国际发货国家
     */
    @Column(name = "isoale_org_country")
    private String isoaleOrgCountry;

    /**
     * 国际发货城市
     */
    @Column(name = "isoale_org_city")
    private String isoaleOrgCity;

    /**
     * 备件描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 备件描述中文翻译
     */
    @Column(name = "description_chinese")
    private String descriptionChinese;

    /**
     * 工单编号 SR Number
     */
    @Column(name = "job")
    private String job;

    /**
     * 更新时间， SDT 导入数据更新记录的时间
     */
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Column(name = "interface_created_date")
    private Date interfaceCreatedDate;

    @Transient
    @JsonProperty
    private String localStatusCode;

    @Transient
    @JsonProperty
    private String localStatusName;

    public String getLocalStatusCode() {
        return localStatusCode;
    }

    public void setLocalStatusCode(String localStatusCode) {
        this.localStatusCode = localStatusCode;
    }

    public String getLocalStatusName() {
        return localStatusName;
    }

    public void setLocalStatusName(String localStatusName) {
        this.localStatusName = localStatusName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getFeName() {
        return feName;
    }

    public void setFeName(String feName) {
        this.feName = feName;
    }

    public String getFeSSO() {
        return feSSO;
    }

    public void setFeSSO(String feSSO) {
        this.feSSO = feSSO;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDecodedStatus() {
        return decodedStatus;
    }

    public void setDecodedStatus(String decodedStatus) {
        this.decodedStatus = decodedStatus;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCarrierChinese() {
        return carrierChinese;
    }

    public void setCarrierChinese(String carrierChinese) {
        this.carrierChinese = carrierChinese;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOrgCountry() {
        return orgCountry;
    }

    public void setOrgCountry(String orgCountry) {
        this.orgCountry = orgCountry;
    }

    public String getOrgCity() {
        return orgCity;
    }

    public void setOrgCity(String orgCity) {
        this.orgCity = orgCity;
    }

    public String getIsoaleOrgCountry() {
        return isoaleOrgCountry;
    }

    public void setIsoaleOrgCountry(String isoaleOrgCountry) {
        this.isoaleOrgCountry = isoaleOrgCountry;
    }

    public String getIsoaleOrgCity() {
        return isoaleOrgCity;
    }

    public void setIsoaleOrgCity(String isoaleOrgCity) {
        this.isoaleOrgCity = isoaleOrgCity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionChinese() {
        return descriptionChinese;
    }

    public void setDescriptionChinese(String descriptionChinese) {
        this.descriptionChinese = descriptionChinese;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getInterfaceCreatedDate() {
        return interfaceCreatedDate;
    }

    public void setInterfaceCreatedDate(Date interfaceCreatedDate) {
        this.interfaceCreatedDate = interfaceCreatedDate;
    }
}
