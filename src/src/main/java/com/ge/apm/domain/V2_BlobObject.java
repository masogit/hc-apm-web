package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "v2_blob_object")
public class V2_BlobObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ZkbBoSubType {
        ServiceManual, OperationManual, MaintenanceList, Others
    }

    public enum ZkbOperationType {
        A, C
    }
    public enum  AttachmentType{

        PM_REPORT("PmReport","保养报告"),
        MEASUREMENT_REPORT("MeReport","计量报告"),
        AUDIT_REPORT("AuReport","质控报告"),
        INSPECTION_REPORT ("PIReport","巡检报告"),
        OTHER ("Other","其他"),
        MULTI_LEVEL_APPROVAL("MultiLevelApproval","多级审批"),
//        PMBE("PMBE","保养前照片"),
//        PMAF("PMAF","保养后照片"),
        ;
        private String key;
        private String value;

        AttachmentType(String key, String value) {
            this.key=key;
            this.value=value;
        }


        public static List<String> getAttachmentTypeList(){
            ArrayList<String> attList = new ArrayList<>();

            for (AttachmentType attachmentType : values()) {
                attList.add(attachmentType.value);
            }
            return  attList;

        }
        public  static String  getKeyByValue(String value){
            for (AttachmentType attachmentType : values()) {
                if(attachmentType.value.equals(value)){
                    return attachmentType.key;
                }
            }
            return null;
        }
    }

    public static final String BO_TYPE_ServiceRequest = "ServiceRequest";
    public static final String BO_TYPE_SR_NO_ASSET = "ServiceRequestNoAsset";
    public static final String BO_TYPE_PCCReport = "PccReport";
    public static final String BO_TYPE_PmRequest = "PmRequest";
    public static final String BO_TYPE_MultiLevelApproval = "MultiLevelApproval"; // 多级审批
    public static final String BO_TYPE_PartAttachment="PartAttachment";
    public static final String BO_TYPE_PrePmOrder = "PrePmOrder"; //保养前照片
    public static final String BO_TYPE_PmOrder = "PmOrder"; //保养后照片
    public static final String BO_TYPE_PmReport = "PmReport"; //保养报告
    public static final String BO_TYPE_MeReport = "MeReport"; //计量报告
    public static final String BO_TYPE_AuReport = "AuReport"; //质控报告
    public static final String BO_TYPE_PIReport = "PIReport"; //巡检报告
    public static final String BO_TYPE_Other = "Other"; //其他

    public static final String BO_TYPE_PM_RULE = "PmRule";
    public static final String BO_TYPE_WorkOrder = "WorkOrder";
    public static final String BO_TYPE_WorkOrderStep = "WorkOrderStep";
    public static final String BO_TYPE_WorkOrderDetail = "WorkOrderDetail";
    public static final String BO_TYPE_WORKORDER_EXPORT_TEMPLATE = "WorkOrderExportTemplate";

    public static final String BO_TYPE_SYSTEM_TEMPLATE = "SystemTemplate";
    public static final String BO_TYPE_PMORDER_REPORT_TEMPLATE = "PmOrderReportTemplate";
    public static final String BO_TYPE_InspectionOrder = "InspectionOrder";
    public static final String BO_TYPE_AssetInfo = "AssetInfo";
    public static final String BO_TYPE_SpecialAssetPermitInfo = "SpecialAssetPermitInfo";
    public static final String BO_TYPE_SpecialAssetReportInfo = "SpecialAssetReportInfo";
    public static final String BO_TYPE_Supply = "supplier";
    public static final String BO_TYPE_AssetContract = "AssetContract";
    public static final String BO_TYPE_QRCodeLib = "QRCodeLib";
    public static final String BO_TYPE_ArrivalListItem = "ArrivalListItem";
    public static final String BO_TYPE_ArrivalList = "ArrivalList";
    public static final String BO_TYPE_TableDefinition = "TableDefinition";
    public static final String BO_TYPE_EXPORT_RECORD = "ExportRecord";
    public static final String BO_TYPE_ZKB = "ZKB";
    public static final String BO_TYPE_BUDGET = "budget";
    public static final String BO_TYPE_BUDGET_PURCHASE = "budgetPurchase";
    public static final String BO_SUB_TYPE_DOCS = "Docs";
    public static final String BO_SUB_TYPE_EHOME = "WorkOrderEHome";
    public static final String BO_SUB_TYPE_IMG = "Image";
    public static final String BO_SUB_TYPE_EXCEL = "Excel";
    public static final String BO_SUB_FILE = "File";
    public static final String BO_SUB_TYPE_AssetInfo_Measuring = "Measuring";
    public static final String BO_TYPE_ZKB_PART_ORDER = "ZkbPartOrder";
    public static final String BO_TYPE_ZKB_COURSE = "ZkbCourse";
    public static final String BO_TYPE_ZKB_COURSE_TRAIN = "ZkbCourseTrain";
    public static final String BO_TYPE_ZKB_FE_PART_ORDER = "ZkbFePartOrder";
    public static final String BO_TYPE_ZKB_PART_ORDER_LETTER = "ZkbPartOrderLetter";
    public static final String BO_TYPE_ZKB_PART_ORDER_RETURN_NEW_CAT = "ZkbPartOrderReturnNewCat";
    public static final String BO_TYPE_ZKB_PART_ORDER_RETURN_OLD_CAT = "ZkbPartOrderReturnOldCat";
    public static final String BO_TYPE_ZKB_PART_CLOSE_FILE = "ZkbPartOrderClose";
    public static final String BO_TYPE_ZKB_PART_ORDER_PROVE = "ZkbPartOrderProve";
    public static final String BO_TYPE_ZKB_PART_ORDER_RETURN = "ZkbPartOrderReturn";
    public static final String BO_TYPE_ZKB_INTEGRAL_ORDER = "ZkbIntegralOrder";
    public static final String BO_TYPE_COIL_OPPORTUNITY = "CoilOpportunity";
    public static final String BO_TYPE_ADJUST_EQUIPMENT = "AdjustEquipment";
    public static final String BO_TYPE_IBU_INFO = "IBUInfo";
    public static final String BO_TYPE_US_Option = "USOption";
    public static final String BO_TYPE_PROBE_RECOMMEND = "ProbeRecommend";
    public static final String BO_TYPE_AUDIT_CONFIG_ITEM = "auditConfigItem";
    public static final String BO_TYPE_AUDIT_TEMPLATE = "auditTemplate";
    public static final String BO_TYPE_PART_PIC = "sparePart";
    public static final String BO_TYPE_BUDGET_PURCHASE_TEMPLATE = "budgetPurchaseTemplate";
    /**
     * 工程师信息
     */
    public static final String BO_TYPE_ZKB_ENGINEER="ZkbEngineer";

    public static final String BO_TYPE_QRCODE_BACKGROUND = "qrCodeBackground";

    public static final String BO_TYPE_TRAIN_DETAIL = "TrainDetail";
    public static final String BO_TYPE_TRAIN_RESULT = "TrainDetailResult";

    public static final String BO_TYPE_MC_SUBSCRIBE = "MC_SUBSCRIBE";
    public static final String BO_TYPE_MC_SUBSCRIBE_APPLY = "MC_SUBSCRIBE_APPLY";
    public static final String BO_TYPE_TRAIN_REGISTER = "TRAIN_REGISTER";

    public static final String BO_SUB_TYPE_PHYSICAL_EXAM = "PHYSICAL_EXAM";       // 放射体检证明
    public static final String BO_SUB_TYPE_SAFETY_CERT = "SAFETY_CERT";           // 安全防护证书
    public static final String BO_SUB_TYPE_RECOMMENDATION = "RECOMMENDATION";     // 公司推荐函


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "bo_uuid")
    private String boUuid;

    @Column(name = "bo_id")
    private Integer boId;

    @Column(name = "bo_type")
    private String boType;

    @Column(name = "bo_sub_type")
    private String boSubType;

    @Column(name = "object_storage_id")
    private String objectStorageId;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_size")
    private Long objectSize;

    @Column(name = "object_source")
    private V2_BlobObject_Source objectSource = V2_BlobObject_Source.STORED_IN_DB;

    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Column(name = "ext_link")
    private String extLink;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "main_directory")
    private String mainDirectory;

    @Column(name = "second_directory")
    private String secondDirectory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "bo_uuid", referencedColumnName = "uid", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "bo_type = 'AssetInfo'")
    @JsonBackReference
    private AssetInfo assetInfo;

    @Column(name = "compress_object_id")
    private String compressObjectId;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "approve_status")
    private String approveStatus;

    @Transient
    private List<String> showPreviewTypeList = ListUtil.newArrayList("pdf", "png", "jpg", "jpeg");

    @Transient
    private List<String> imageTypeList = ListUtil.newArrayList("png", "jpg", "jpeg");

    @Transient
    private String boSubTypeValue;

    public String getBoSubTypeValue() {
        return boSubTypeValue;
    }

    public void setBoSubTypeValue(String boSubTypeValue) {
        this.boSubTypeValue = boSubTypeValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public Integer getBoId() {
        return boId;
    }

    public void setBoId(Integer boId) {
        this.boId = boId;
    }

    public String getObjectStorageId() {
        return objectStorageId;
    }

    public void setObjectStorageId(String objectStorageId) {
        this.objectStorageId = objectStorageId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(Long objectSize) {
        this.objectSize = objectSize;
    }

    public V2_BlobObject_Source getObjectSource() {
        return objectSource;
    }

    public void setObjectSource(V2_BlobObject_Source objectSource) {
        this.objectSource = objectSource;
    }

    public String getBoType() {
        return boType;
    }

    public void setBoType(String boType) {
        this.boType = boType;
    }

    public String getBoSubType() {
        return boSubType;
    }

    public void setBoSubType(String boSubType) {
        this.boSubType = boSubType;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getExtLink() {
        return extLink;
    }

    public void setExtLink(String extLink) {
        this.extLink = extLink;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    public String getSecondDirectory() {
        return secondDirectory;
    }

    public void setSecondDirectory(String secondDirectory) {
        this.secondDirectory = secondDirectory;
    }

    public String getCompressObjectId() {
        return compressObjectId;
    }

    public void setCompressObjectId(String compressObjectId) {
        this.compressObjectId = compressObjectId;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public boolean showPreview() {
        String type = objectName.substring(objectName.lastIndexOf(".") + 1);
        return showPreviewTypeList.contains(type.toLowerCase());
    }

    public boolean isImageType() {
        String type = objectName.substring(objectName.lastIndexOf(".") + 1);
        return imageTypeList.contains(type.toLowerCase());
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_BlobObject)) {
            return false;
        }
        V2_BlobObject other = (V2_BlobObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
