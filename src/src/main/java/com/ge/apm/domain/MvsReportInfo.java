package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503266962
 */
@Data
@Entity
@Table(name = "mvs_report_info")
public class MvsReportInfo implements Serializable{

    private static final long serialVersionUID = -6466710321726902730L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "depart_num")
    private String departNum;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "alias_name")
    private String aliasName;

    @Column(name = "asset_owner_name")
    private String assetOwnerName;

    @Column(name = "asset_owner_tel")
    private String assetOwnerTel;

    @Column(name = "asset_owner_name2")
    private String assetOwnerName2;

    @Column(name = "asset_owner_tel2")
    private String assetOwnerTel2;

    @Column(name = "asset_owner_name3")
    private String assetOwnerName3;

    @Column(name = "asset_owner_tel3")
    private String assetOwnerTel3;

    @Column(name = "pm_owner_name")
    private String pmOwnerName;

    @Column(name = "pm_owner_tel")
    private String pmOwnerTel;

    @Column(name = "measuring_owner_name")
    private String measuringOwnerMame;

    @Column(name = "clinical_site")
    private String clinicalSite;

    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;

    @Column(name = "clinical_owner_name")
    private String clinicalOwnerName;

    @Column(name = "clinical_owner_tel")
    private String clinicalOwnerTel;

    @Column(name = "asset_use_dept")
    private String assetUseDept;

    @Column(name = "manufacture")
    private String manufacture;

    @Column(name = "make_short_name")
    private String makeShortName;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "vendor_tel")
    private String vendorTel;

    @Column(name = "function_group")
    private String functionGroup;

    @Column(name = "asset_code68")
    private String assetCode68;
    
    @Column(name = "asset_group")
    private String assetGroup;
    
    @Column(name = "main_asset")
    private String mainAsset;
    
    @Column(name = "function_grade")
    private String functionGrade;
    
    @Column(name = "repair_form")
    private String repairForm;
    
    @Column(name = "function_type")
    private String functionType;

    @Column(name = "serial_num")
    private String serialNum;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "national_standard_code")
    private String nationalStandardCode;

    @Column(name = "financing_num")
    private String financingNum;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "modality_id")
    private String modalityId;

    @Column(name = "purchase_price")
    private Double purchasePrice;

    @Column(name = "salvage_value")
    private Double salvageValue;

    @Column(name = "lifecycle")
    private Integer lifecycle;

    @Column(name = "depreciation_method")
    private String depreciationMethod;

    @Column(name = "manufact_date")
    @Temporal(TemporalType.DATE)
    private Date manufactDate;

    @Column(name = "purchase_date")
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;

    @Column(name = "arrive_date")
    @Temporal(TemporalType.DATE)
    private Date arriveDate;

    @Column(name = "install_date")
    @Temporal(TemporalType.DATE)
    private Date installDate;

    @Column(name = "factory_warranty_date")
    @Temporal(TemporalType.DATE)
    private Date factoryWarrantyDate;

    @Column(name = "warranty_date")
    @Temporal(TemporalType.DATE)
    private Date warrantyDate;

    @Column(name = "terminate_date")
    @Temporal(TemporalType.DATE)
    private Date terminateDate;

    @Column(name = "last_pm_date")
    @Temporal(TemporalType.DATE)
    private Date lastPmDate;

    @Column(name = "last_qa_date")
    @Temporal(TemporalType.DATE)
    private Date lastQaDate;

    @Column(name = "last_metering_date")
    @Temporal(TemporalType.DATE)
    private Date lastMeteringDate;

    @Column(name = "maitanance")
    private String maitanance;

    @Column(name = "maitanance_tel")
   private String maitananceTel;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "location_code")
    private String locationCode;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "comments")
    private String comments;

    @Column(name = "asset_usage")
    private String assetUsage;

    @Column(name = "capital_source")
    private String capitalSource;

    @Column(name = "capital_source2")
    private String capitalSource2;

    @Column(name = "measure_grade")
    private String measureGrade;

    @Column(name = "risk_grade")
    private String riskGrade;

    @Column(name = "emergency_equipment")
    private String emergencyEquipment;

    @Column(name = "import_equipment")
    private String importEquipment;

    @Column(name = "measuring_item_category")
    private String measuringItemCategory;

    @Column(name = "accounting_category")
    private String accountingCategory;

    @Column(name = "status")
    private String status;

    @Column(name = "is_pm")
    private String isPm;

    @Column(name = "is_checked")
    private String isChecked;

    @Column(name = "asset_value_category")
    private String assetValueCategory;

    @Column(name = "device_status")
    private String deviceStatus;

    @Column(name = "is_warranty")
    private String isWarranty;

    @Column(name = "ucm_id")
    private String ucmId;

    @Column(name = "asset_uid")
    private String assetUid;

    @Column(name = "parent_asset_id")
    private String parentAssetId;

    @Column(name = "ib_type")
    private String ibType;

    @Column(name = "area")
    private String area;

    @Column(name = "province")
    private String province;

    @Column(name = "service_region")
    private String serviceRegion;

    @Column(name = "parent_system_id")
    private String parentSystemId;

    @Column(name = "parent_serial")
    private String parentSerial;

    @Column(name = "customer_chinese_address")
    private String customerChineseAddress;

    @Column(name = "pl")
    private String pl;

    @Column(name = "city")
    private String city;

    @Column(name = "product_family")
    private String productFamily;

    @Column(name = "product_segment")
    private String productSegment;

    @Column(name = "asset_status")
    private String assetStatus;

    @Column(name = "svc_offering")
    private String svcOffering;

    @Column(name = "mvs_contract_start_date")
    @Temporal(TemporalType.DATE)
    private Date mvsContractStartDate;

    @Column(name = "mvs_contract_end_date")
    @Temporal(TemporalType.DATE)
    private Date mvsContractEndDate;

    @Column(name = "dealer_name")
    private String dealerName;

    @Column(name = "expert_sso")
    private String expertSso;

    @Column(name = "intermediate_sso")
    private String intermediateSso;

    @Column(name = "hp_level")
    private String hpLevel;

    @Column(name = "sales_sso")
    private String salesSso;

    @Column(name = "account_sales")
    private String accountSales;

    @Column(name = "sales_mgr")
    private String salesMgr;

    @Column(name = "region_mgr")
    private String regionMgr;

    @Column(name = "region2")
    private String region2;

    @Column(name = "area_leader_sso")
    private String areaLeaderSso;

    @Column(name = "enterprise")
    private String enterprise;

    @Column(name = "enterprise_vip")
    private String enterpriseVip;

    @Column(name = "enterprise_vip_leader_sso")
    private String enterpriseVipLeaderSso;

    @Column(name = "enterprise_vip_leader_name")
    private String enterpriseVipLeaderName;

    @Column(name = "commerial_svc_region")
    private String commerialSvcRegion;

    @Column(name = "account_segment_first_level")
    private String accountSegmentFirstLevel;

    @Column(name = "account_segment_second_level")
    private String accountSegmentSecondLevel;

    @Column(name = "contract_type2")
    private String contractType2;

    @Column(name = "report_date")
    @Temporal(TemporalType.DATE)
    private Date reportDate;

}
