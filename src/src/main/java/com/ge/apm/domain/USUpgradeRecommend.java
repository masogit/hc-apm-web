package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 503211108
 */
@Entity
@Table(name = "us_upgrade_recommend")
public class USUpgradeRecommend implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 设备编号
     */
    @Column(name = "system_id", nullable = false)
    private String systemId;

    /**
     * 设备编号没有0
     */
    @Column(name = "system_id_no_zero")
    private String systemIdNoZero;

    /**
     * ibstatus
     */
    @Column(name = "ib_status")
    private String ibStatus;

    /**
     * assetnumber
     */
    @Column(name = "asset_number")
    private String assetNumber;

    /**
     * subregion
     */
    @Column(name = "sub_region")
    private String subRegion;

    /**
     * business
     */
    @Column(name = "business")
    private String business;

    /**
     * Serviceable_IB
     */
    @Column(name = "serviceable_ib")
    private String serviceableIb;

    /**
     * Age in Years
     */
    @Column(name = "age_in_years")
    private String ageInYears;

    /**
     * eosl_sf10_flg
     */
    @Column(name = "eosl_sf10_flg")
    private String eoslSf10Flg;

    /**
     * eosl_sf10_date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "eosl_sf10_date")
    private Date eoslSf10Date;


    /**
     * wib_p
     */
    @Column(name = "wib_p")
    private String wibP;


    /**
     * systemcoveragelevelwarrantyc
     */
    @Column(name = "system_coverage_level")
    private String systemCoverageLevel;


    /**
     * ibiccount
     */
    @Column(name = "ibiccount")
    private String ibiccount;

    /**
     * ibowcount
     */
    @Column(name = "ibowcount")
    private String ibowcount;

    /**
     * ibiwcount
     */
    @Column(name = "ibiwcount")
    private String ibiwcount;

    /**
     * productidentifier
     */
    @Column(name = "product_identifier")
    private String productIdentifier;


    /**
     * c_modality
     */
    @Column(name = "c_modality")
    private String cModality;

    /**
     * a_level_modality
     */
    @Column(name = "a_level_modality")
    private String aLevelModality;


    /**
     * family_name
     */
    @Column(name = "family_name")
    private String familyName;

    /**
     * psi_description
     */
    @Column(name = "psi_description")
    private String psiDescription;

    /**
     * lifecycle
     */
    @Column(name = "life_cycle")
    private String lifeCycle;

    /**
     * equipment
     */
    @Column(name = "equipment")
    private String equipment;

    /**
     * warrantystartdate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "warranty_start_date")
    private Date warrantyStartDate;

    /**
     * warrantyenddate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "warranty_end_date")
    private Date warrantyEndDate;

    /**
     * Convert Month
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @Column(name = "convert_month")
    private Date convertMonth;

    /**
     * Month for Convert Opp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @Column(name = "convert_opp_month")
    private Date convertOppMonth;

    /**
     * contractstartdate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "contract_start_date")
    private Date contractStartDate;


    /**
     * contractenddate
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "contract_end_date")
    private Date contractEndDate;


    /**
     * Renew Month
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @Column(name = "renew_month")
    private Date renewMonth;


    /**
     * OC +90天 Month for Renew Opp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @Column(name = "renew_opp_month")
    private Date renewOppMonth;

    /**
     * PA Flag
     */
    @Column(name = "pa_flag")
    private String paFlag;

    /**
     * CQ end new OC
     */
    @Column(name = "cq_end_new_oc")
    private String cqEndNewOc;

    /**
     * CQ end new OW
     */
    @Column(name = "cq_end_new_ow")
    private String cqEndNewOw;

    /**
     * SS-Present Status
     */
    @Column(name = "ss_present_status")
    private String ssPresentStatus;

    /**
     * SS-Q1 end Status
     */
    @Column(name = "ss_q1_end_status")
    private String ssQ1EndStatus;

    /**
     * SS-Q2 end Status
     */
    @Column(name = "ss_q2_end_status")
    private String ssQ2EndStatus;


    /**
     * SS-Q3 end Status
     */
    @Column(name = "ss_q3_end_status")
    private String ssQ3EndStatus;


    /**
     * SS-Q4 end Status
     */
    @Column(name = "ss_q4_end_status")
    private String ssQ4EndStatus;


    /**
     * Installed Non-EOL Main system
     */
    @Column(name = "installed_non_eol_main_system")
    private String installedNonEolMainSystem;

    /**
     * IB Type
     */
    @Column(name = "ib_type")
    private String ibType;

    /**
     * Sales Order
     */
    @Column(name = "sales_order")
    private String salesOrder;

    /**
     * 主机SN
     */
    @Column(name = "main_engine_sn")
    private String mainEngineSn;

    /**
     * Parent Account Facility Type
     */
    @Column(name = "parent_account_facility_type")
    private String parentAccountFacilityType;

    /**
     * Installed At Alternate Customer Name
     */
    @Column(name = "installed_alternate_customer_name")
    private String installedAlternateCustomerName;

    /**
     * Site ID
     */
    @Column(name = "site_id")
    private String siteId;

    /**
     * Customer Chinese Address
     */
    @Column(name = "customer_chinese_address", columnDefinition = "text")
    private String customerChineseAddress;

    /**
     * HP Level
     */
    @Column(name = "hp_level")
    private String hpLevel;

    /**
     * Grade
     */
    @Column(name = "grade")
    private String grade;

    /**
     * UCM ID
     */
    @Column(name = "ucm_id")
    private String ucmId;


    /**
     * SVC Region
     */
    @Column(name = "svc_region")
    private String svcRegion;

    /**
     * P&L
     */
    @Column(name = "pl")
    private String pl;

    /**
     * Account Segment-1st Level（WB拆出）
     */
    @Column(name = "account_segment_1st_level_wb")
    private String accountSegment1stLevelWB;

    /**
     * Account Segment-1st Level
     */
    @Column(name = "account_segment_1st_level")
    private String accountSegment1stLevel;

    /**
     * Account Segment-2nd Level
     */
    @Column(name = "account_segment_2nd_level")
    private String accountSegment2ndLevel;


    /**
     * Enterprise 1st Level
     */
    @Column(name = "enterprise_1st_level")
    private String enterprise1stLevel;


    /**
     * Enterprise 2nd Level
     */
    @Column(name = "enterprise_2nd_level")
    private String enterprise2ndLevel;

    /**
     * 2021 VIP Enterpriese
     */
    @Column(name = "vip_enterpriese")
    private String vipEnterpriese;


    /**
     * 2021 Other Enterpriese
     */
    @Column(name = "other_enterpriese")
    private String otherEnterpriese;


    /**
     * Region
     */
    @Column(name = "region")
    private String region;

    /**
     * Province
     */
    @Column(name = "province")
    private String province;

    /**
     * City
     */
    @Column(name = "city")
    private String city;


    /**
     * County
     */
    @Column(name = "county")
    private String county;


    /**
     * 机构大类
     */
    @Column(name = "organ_main_category")
    private String organMainCategory;


    /**
     * Region Mgr
     */
    @Column(name = "region_mgr")
    private String regionMgr;

    /**
     * zone mgr @ 19 new
     */
    @Column(name = "zone_mgr")
    private String zoneMgr;

    /**
     * SVC Sales name
     */
    @Column(name = "svc_sales_name")
    private String svcSalesName;


    /**
     * Sales SSO
     */
    @Column(name = "sales_sso")
    private String salesSso;


    /**
     * Winback
     */
    @Column(name = "win_back")
    private String winBack;


    /**
     * Modality-1
     */
    @Column(name = "modality_one")
    private String modalityOne;


    /**
     * Modality-2
     */
    @Column(name = "modality_two")
    private String modalityTwo;


    /**
     * Series_1
     */
    @Column(name = "series_one")
    private String seriesOne;


    /**
     * Series_2
     */
    @Column(name = "series_two")
    private String seriesTwo;

    /**
     * Key Product
     */
    @Column(name = "key_product")
    private String keyProduct;


    /**
     * 3 Segment
     */
    @Column(name = "segment")
    private String segment;


    /**
     * 3 Segment
     */
    @Column(name = "eq_segment")
    private String eqSegment;


    /**
     * EOL
     */
    @Column(name = "eol")
    private String eol;


    /**
     * EOL Date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "eol_date")
    private Date eolDate;


    /**
     * Tube M
     */
    @Column(name = "tube_m")
    private String tubeM;


    /**
     * Part Number Description
     */
    @Column(name = "part_number_description")
    private String partNumberDescription;


    /**
     * ZKB product
     */
    @Column(name = "zkb_product")
    private String zkbProduct;


    /**
     * Installed Date and Time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "installed_date")
    private Date installedDate;


    /**
     * Warranty Start Date Override (Local)
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "warranty_start_date_one")
    private Date warrantyStartDateOne;


    /**
     * Warranty End Date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "warranty_end_date_one")
    private Date warrantyEndDateOne;

    /**
     * Contract Name
     */
    @Column(name = "contract_name")
    private String contractName;


    /**
     * CUST_PO
     */
    @Column(name = "cust_po")
    private String custPo;


    /**
     * Contract Type1
     */
    @Column(name = "contract_type1")
    private String contractType1;


    /**
     * Contract Type2
     */
    @Column(name = "contract_type2")
    private String contractType2;


    /**
     * C-Start
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "c_start")
    private Date cStart;


    /**
     * C-End
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "c_end")
    private Date cEnd;


    /**
     * TUI Contract Name
     */
    @Column(name = "tui_contract_name")
    private String tuiContractName;


    /**
     * TUI C-Start
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "tui_c_start")
    private Date tuiCStart;


    /**
     * TUI C-end
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "tui_c_end")
    private Date tuiCEnd;


    /**
     * TUI IC or not
     */
    @Column(name = "tui_c_or_not")
    private String tuiCOrNot;


    /**
     * IB Status present
     */
    @Column(name = "ib_status_present")
    private String ibStatusPresent;


    /**
     * IB Status-Q1 end
     */
    @Column(name = "ib_status_q1_end")
    private String ibStatusQ1End;


    /**
     * IB Status-Q2 end
     */
    @Column(name = "ib_status_q2_end")
    private String ibStatusQ2End;


    /**
     * IB Status-Q3 end
     */
    @Column(name = "ib_status_q3_end")
    private String ibStatusQ3End;


    /**
     * IB Status-Q4 end
     */
    @Column(name = "ib_status_q4_end")
    private String ibStatusQ4End;


    /**
     * 5Q convt/renew最新值
     */
    @Column(name = "convt_new")
    private String convtNew;


    /**
     * 5Q convt/renew初始值
     */
    @Column(name = "convt_initial")
    private String convtInitial;


    /**
     * EQT Sales Dealer
     */
    @Column(name = "eqt_sales_dealer")
    private String eqtSalesDealer;


    /**
     * ULS Dealer
     */
    @Column(name = "uls_dealer")
    private String ulsDealer;


    /**
     * Dealer Type
     */
    @Column(name = "dealer_type")
    private String dealerType;


    /**
     * ULS PCB Dealer
     */
    @Column(name = "uls_pcb_dealer")
    private String ulsPcbDealer;


    /**
     * ZKB dealer
     */
    @Column(name = "zkb_dealer")
    private String zkbDealer;


    /**
     * ZKB IC as of now
     */
    @Column(name = "zkb_ic_now")
    private String zkbIcNow;


    /**
     * ZKB IC Remaining days of Qend
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "zkb_ic_remaining_day")
    private Date zkbIcRemainingDay;


    /**
     * ZKB Entitlement
     */
    @Column(name = "zkb_entitlement")
    private String zkbEntitlement;


    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "modify_time")
    private Date modifyTime;

    /**
     * 导入文件id
     */
    @Column(name = "import_file_id", nullable = false)
    private String importFileId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemIdNoZero() {
        return systemIdNoZero;
    }

    public void setSystemIdNoZero(String systemIdNoZero) {
        this.systemIdNoZero = systemIdNoZero;
    }

    public String getIbStatus() {
        return ibStatus;
    }

    public void setIbStatus(String ibStatus) {
        this.ibStatus = ibStatus;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getServiceableIb() {
        return serviceableIb;
    }

    public void setServiceableIb(String serviceableIb) {
        this.serviceableIb = serviceableIb;
    }

    public String getAgeInYears() {
        return ageInYears;
    }

    public void setAgeInYears(String ageInYears) {
        this.ageInYears = ageInYears;
    }

    public String getEoslSf10Flg() {
        return eoslSf10Flg;
    }

    public void setEoslSf10Flg(String eoslSf10Flg) {
        this.eoslSf10Flg = eoslSf10Flg;
    }

    public Date getEoslSf10Date() {
        return eoslSf10Date;
    }

    public void setEoslSf10Date(Date eoslSf10Date) {
        this.eoslSf10Date = eoslSf10Date;
    }

    public String getWibP() {
        return wibP;
    }

    public void setWibP(String wibP) {
        this.wibP = wibP;
    }

    public String getSystemCoverageLevel() {
        return systemCoverageLevel;
    }

    public void setSystemCoverageLevel(String systemCoverageLevel) {
        this.systemCoverageLevel = systemCoverageLevel;
    }

    public String getIbiccount() {
        return ibiccount;
    }

    public void setIbiccount(String ibiccount) {
        this.ibiccount = ibiccount;
    }

    public String getIbowcount() {
        return ibowcount;
    }

    public void setIbowcount(String ibowcount) {
        this.ibowcount = ibowcount;
    }

    public String getIbiwcount() {
        return ibiwcount;
    }

    public void setIbiwcount(String ibiwcount) {
        this.ibiwcount = ibiwcount;
    }

    public String getProductIdentifier() {
        return productIdentifier;
    }

    public void setProductIdentifier(String productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    public String getcModality() {
        return cModality;
    }

    public void setcModality(String cModality) {
        this.cModality = cModality;
    }

    public String getaLevelModality() {
        return aLevelModality;
    }

    public void setaLevelModality(String aLevelModality) {
        this.aLevelModality = aLevelModality;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPsiDescription() {
        return psiDescription;
    }

    public void setPsiDescription(String psiDescription) {
        this.psiDescription = psiDescription;
    }

    public String getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Date getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyStartDate(Date warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public Date getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(Date warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }

    public Date getConvertMonth() {
        return convertMonth;
    }

    public void setConvertMonth(Date convertMonth) {
        this.convertMonth = convertMonth;
    }

    public Date getConvertOppMonth() {
        return convertOppMonth;
    }

    public void setConvertOppMonth(Date convertOppMonth) {
        this.convertOppMonth = convertOppMonth;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public Date getRenewMonth() {
        return renewMonth;
    }

    public void setRenewMonth(Date renewMonth) {
        this.renewMonth = renewMonth;
    }

    public Date getRenewOppMonth() {
        return renewOppMonth;
    }

    public void setRenewOppMonth(Date renewOppMonth) {
        this.renewOppMonth = renewOppMonth;
    }

    public String getPaFlag() {
        return paFlag;
    }

    public void setPaFlag(String paFlag) {
        this.paFlag = paFlag;
    }

    public String getCqEndNewOc() {
        return cqEndNewOc;
    }

    public void setCqEndNewOc(String cqEndNewOc) {
        this.cqEndNewOc = cqEndNewOc;
    }

    public String getCqEndNewOw() {
        return cqEndNewOw;
    }

    public void setCqEndNewOw(String cqEndNewOw) {
        this.cqEndNewOw = cqEndNewOw;
    }

    public String getSsPresentStatus() {
        return ssPresentStatus;
    }

    public void setSsPresentStatus(String ssPresentStatus) {
        this.ssPresentStatus = ssPresentStatus;
    }

    public String getSsQ1EndStatus() {
        return ssQ1EndStatus;
    }

    public void setSsQ1EndStatus(String ssQ1EndStatus) {
        this.ssQ1EndStatus = ssQ1EndStatus;
    }

    public String getSsQ2EndStatus() {
        return ssQ2EndStatus;
    }

    public void setSsQ2EndStatus(String ssQ2EndStatus) {
        this.ssQ2EndStatus = ssQ2EndStatus;
    }

    public String getSsQ3EndStatus() {
        return ssQ3EndStatus;
    }

    public void setSsQ3EndStatus(String ssQ3EndStatus) {
        this.ssQ3EndStatus = ssQ3EndStatus;
    }

    public String getSsQ4EndStatus() {
        return ssQ4EndStatus;
    }

    public void setSsQ4EndStatus(String ssQ4EndStatus) {
        this.ssQ4EndStatus = ssQ4EndStatus;
    }

    public String getInstalledNonEolMainSystem() {
        return installedNonEolMainSystem;
    }

    public void setInstalledNonEolMainSystem(String installedNonEolMainSystem) {
        this.installedNonEolMainSystem = installedNonEolMainSystem;
    }

    public String getIbType() {
        return ibType;
    }

    public void setIbType(String ibType) {
        this.ibType = ibType;
    }

    public String getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(String salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getMainEngineSn() {
        return mainEngineSn;
    }

    public void setMainEngineSn(String mainEngineSn) {
        this.mainEngineSn = mainEngineSn;
    }

    public String getParentAccountFacilityType() {
        return parentAccountFacilityType;
    }

    public void setParentAccountFacilityType(String parentAccountFacilityType) {
        this.parentAccountFacilityType = parentAccountFacilityType;
    }

    public String getInstalledAlternateCustomerName() {
        return installedAlternateCustomerName;
    }

    public void setInstalledAlternateCustomerName(String installedAlternateCustomerName) {
        this.installedAlternateCustomerName = installedAlternateCustomerName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getCustomerChineseAddress() {
        return customerChineseAddress;
    }

    public void setCustomerChineseAddress(String customerChineseAddress) {
        this.customerChineseAddress = customerChineseAddress;
    }

    public String getHpLevel() {
        return hpLevel;
    }

    public void setHpLevel(String hpLevel) {
        this.hpLevel = hpLevel;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUcmId() {
        return ucmId;
    }

    public void setUcmId(String ucmId) {
        this.ucmId = ucmId;
    }

    public String getSvcRegion() {
        return svcRegion;
    }

    public void setSvcRegion(String svcRegion) {
        this.svcRegion = svcRegion;
    }

    public String getPl() {
        return pl;
    }

    public void setPl(String pl) {
        this.pl = pl;
    }

    public String getAccountSegment1stLevelWB() {
        return accountSegment1stLevelWB;
    }

    public void setAccountSegment1stLevelWB(String accountSegment1stLevelWB) {
        this.accountSegment1stLevelWB = accountSegment1stLevelWB;
    }

    public String getAccountSegment1stLevel() {
        return accountSegment1stLevel;
    }

    public void setAccountSegment1stLevel(String accountSegment1stLevel) {
        this.accountSegment1stLevel = accountSegment1stLevel;
    }

    public String getAccountSegment2ndLevel() {
        return accountSegment2ndLevel;
    }

    public void setAccountSegment2ndLevel(String accountSegment2ndLevel) {
        this.accountSegment2ndLevel = accountSegment2ndLevel;
    }

    public String getEnterprise1stLevel() {
        return enterprise1stLevel;
    }

    public void setEnterprise1stLevel(String enterprise1stLevel) {
        this.enterprise1stLevel = enterprise1stLevel;
    }

    public String getEnterprise2ndLevel() {
        return enterprise2ndLevel;
    }

    public void setEnterprise2ndLevel(String enterprise2ndLevel) {
        this.enterprise2ndLevel = enterprise2ndLevel;
    }

    public String getVipEnterpriese() {
        return vipEnterpriese;
    }

    public void setVipEnterpriese(String vipEnterpriese) {
        this.vipEnterpriese = vipEnterpriese;
    }

    public String getOtherEnterpriese() {
        return otherEnterpriese;
    }

    public void setOtherEnterpriese(String otherEnterpriese) {
        this.otherEnterpriese = otherEnterpriese;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getOrganMainCategory() {
        return organMainCategory;
    }

    public void setOrganMainCategory(String organMainCategory) {
        this.organMainCategory = organMainCategory;
    }

    public String getRegionMgr() {
        return regionMgr;
    }

    public void setRegionMgr(String regionMgr) {
        this.regionMgr = regionMgr;
    }

    public String getZoneMgr() {
        return zoneMgr;
    }

    public void setZoneMgr(String zoneMgr) {
        this.zoneMgr = zoneMgr;
    }

    public String getSvcSalesName() {
        return svcSalesName;
    }

    public void setSvcSalesName(String svcSalesName) {
        this.svcSalesName = svcSalesName;
    }

    public String getSalesSso() {
        return salesSso;
    }

    public void setSalesSso(String salesSso) {
        this.salesSso = salesSso;
    }

    public String getWinBack() {
        return winBack;
    }

    public void setWinBack(String winBack) {
        this.winBack = winBack;
    }

    public String getModalityOne() {
        return modalityOne;
    }

    public void setModalityOne(String modalityOne) {
        this.modalityOne = modalityOne;
    }

    public String getModalityTwo() {
        return modalityTwo;
    }

    public void setModalityTwo(String modalityTwo) {
        this.modalityTwo = modalityTwo;
    }

    public String getSeriesOne() {
        return seriesOne;
    }

    public void setSeriesOne(String seriesOne) {
        this.seriesOne = seriesOne;
    }

    public String getSeriesTwo() {
        return seriesTwo;
    }

    public void setSeriesTwo(String seriesTwo) {
        this.seriesTwo = seriesTwo;
    }

    public String getKeyProduct() {
        return keyProduct;
    }

    public void setKeyProduct(String keyProduct) {
        this.keyProduct = keyProduct;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getEqSegment() {
        return eqSegment;
    }

    public void setEqSegment(String eqSegment) {
        this.eqSegment = eqSegment;
    }

    public String getEol() {
        return eol;
    }

    public void setEol(String eol) {
        this.eol = eol;
    }

    public Date getEolDate() {
        return eolDate;
    }

    public void setEolDate(Date eolDate) {
        this.eolDate = eolDate;
    }

    public String getTubeM() {
        return tubeM;
    }

    public void setTubeM(String tubeM) {
        this.tubeM = tubeM;
    }

    public String getPartNumberDescription() {
        return partNumberDescription;
    }

    public void setPartNumberDescription(String partNumberDescription) {
        this.partNumberDescription = partNumberDescription;
    }

    public String getZkbProduct() {
        return zkbProduct;
    }

    public void setZkbProduct(String zkbProduct) {
        this.zkbProduct = zkbProduct;
    }

    public Date getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(Date installedDate) {
        this.installedDate = installedDate;
    }

    public Date getWarrantyStartDateOne() {
        return warrantyStartDateOne;
    }

    public void setWarrantyStartDateOne(Date warrantyStartDateOne) {
        this.warrantyStartDateOne = warrantyStartDateOne;
    }

    public Date getWarrantyEndDateOne() {
        return warrantyEndDateOne;
    }

    public void setWarrantyEndDateOne(Date warrantyEndDateOne) {
        this.warrantyEndDateOne = warrantyEndDateOne;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getCustPo() {
        return custPo;
    }

    public void setCustPo(String custPo) {
        this.custPo = custPo;
    }

    public String getContractType1() {
        return contractType1;
    }

    public void setContractType1(String contractType1) {
        this.contractType1 = contractType1;
    }

    public String getContractType2() {
        return contractType2;
    }

    public void setContractType2(String contractType2) {
        this.contractType2 = contractType2;
    }

    public Date getcStart() {
        return cStart;
    }

    public void setcStart(Date cStart) {
        this.cStart = cStart;
    }

    public Date getcEnd() {
        return cEnd;
    }

    public void setcEnd(Date cEnd) {
        this.cEnd = cEnd;
    }

    public String getTuiContractName() {
        return tuiContractName;
    }

    public void setTuiContractName(String tuiContractName) {
        this.tuiContractName = tuiContractName;
    }

    public Date getTuiCStart() {
        return tuiCStart;
    }

    public void setTuiCStart(Date tuiCStart) {
        this.tuiCStart = tuiCStart;
    }

    public Date getTuiCEnd() {
        return tuiCEnd;
    }

    public void setTuiCEnd(Date tuiCEnd) {
        this.tuiCEnd = tuiCEnd;
    }

    public String getTuiCOrNot() {
        return tuiCOrNot;
    }

    public void setTuiCOrNot(String tuiCOrNot) {
        this.tuiCOrNot = tuiCOrNot;
    }

    public String getIbStatusPresent() {
        return ibStatusPresent;
    }

    public void setIbStatusPresent(String ibStatusPresent) {
        this.ibStatusPresent = ibStatusPresent;
    }

    public String getIbStatusQ1End() {
        return ibStatusQ1End;
    }

    public void setIbStatusQ1End(String ibStatusQ1End) {
        this.ibStatusQ1End = ibStatusQ1End;
    }

    public String getIbStatusQ2End() {
        return ibStatusQ2End;
    }

    public void setIbStatusQ2End(String ibStatusQ2End) {
        this.ibStatusQ2End = ibStatusQ2End;
    }

    public String getIbStatusQ3End() {
        return ibStatusQ3End;
    }

    public void setIbStatusQ3End(String ibStatusQ3End) {
        this.ibStatusQ3End = ibStatusQ3End;
    }

    public String getIbStatusQ4End() {
        return ibStatusQ4End;
    }

    public void setIbStatusQ4End(String ibStatusQ4End) {
        this.ibStatusQ4End = ibStatusQ4End;
    }

    public String getConvtNew() {
        return convtNew;
    }

    public void setConvtNew(String convtNew) {
        this.convtNew = convtNew;
    }

    public String getConvtInitial() {
        return convtInitial;
    }

    public void setConvtInitial(String convtInitial) {
        this.convtInitial = convtInitial;
    }

    public String getEqtSalesDealer() {
        return eqtSalesDealer;
    }

    public void setEqtSalesDealer(String eqtSalesDealer) {
        this.eqtSalesDealer = eqtSalesDealer;
    }

    public String getUlsDealer() {
        return ulsDealer;
    }

    public void setUlsDealer(String ulsDealer) {
        this.ulsDealer = ulsDealer;
    }

    public String getDealerType() {
        return dealerType;
    }

    public void setDealerType(String dealerType) {
        this.dealerType = dealerType;
    }

    public String getUlsPcbDealer() {
        return ulsPcbDealer;
    }

    public void setUlsPcbDealer(String ulsPcbDealer) {
        this.ulsPcbDealer = ulsPcbDealer;
    }

    public String getZkbDealer() {
        return zkbDealer;
    }

    public void setZkbDealer(String zkbDealer) {
        this.zkbDealer = zkbDealer;
    }

    public String getZkbIcNow() {
        return zkbIcNow;
    }

    public void setZkbIcNow(String zkbIcNow) {
        this.zkbIcNow = zkbIcNow;
    }

    public Date getZkbIcRemainingDay() {
        return zkbIcRemainingDay;
    }

    public void setZkbIcRemainingDay(Date zkbIcRemainingDay) {
        this.zkbIcRemainingDay = zkbIcRemainingDay;
    }

    public String getZkbEntitlement() {
        return zkbEntitlement;
    }

    public void setZkbEntitlement(String zkbEntitlement) {
        this.zkbEntitlement = zkbEntitlement;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getImportFileId() {
        return importFileId;
    }

    public void setImportFileId(String importFileId) {
        this.importFileId = importFileId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
