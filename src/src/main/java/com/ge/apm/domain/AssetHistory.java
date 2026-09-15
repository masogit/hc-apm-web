/*
 */
package com.ge.apm.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author 212601104
 */
@Entity
@Table(name = "asset_history")
@Data
public class AssetHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String uid;

    @Column(name = "asset_uid", nullable = false)
    private String assetUid;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "financing_num")
    private String financingNum;

    /**
     * [
     * {columnName: clinicalDeptUid, columnValue: '内科', label: 所属科室},
     * {columnName: position, columnValue: 'xxxx', label: 使用位置},
     * {columnName: ownerUid, columnValue: '张三', label: 使用人},
     * ]
     */
    @Column(name = "json_data", nullable = false, columnDefinition = "VARCHAR(2048)")
    private String jsonData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "clinical_dept_uid",columnDefinition = "CHAR(32)")
    private String clinicalDeptUid;   //所属科室

    @Column(name = "asset_json", nullable = true, columnDefinition = "VARCHAR(3072)")
    private String assetJson;

    @Column(name = "source")
    private String source;

    @Column(name = "modify_source")
    private String modifySource;

    public static class AssetHistoryInfo {

        @Getter
        @Setter
        private String columnName;

        @Getter
        @Setter
        private String oldColumnValue;

        @Getter
        @Setter
        private String newColumnValue;

        @Getter
        @Setter
        private String label;
    }
}
