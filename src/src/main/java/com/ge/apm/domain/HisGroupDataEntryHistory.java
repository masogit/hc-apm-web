package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "his_group_data_entry_history")
@Data
public class HisGroupDataEntryHistory {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @Column(name = "data_entry_uid")
    private String  dataEntryUid;

    @Column(name = "asset_group_uid")
    private String  assetGroupUid;


    @Temporal(TemporalType.DATE)
    @Column(name = "data_start_date")
    private Date dataStartDate;


    @Temporal(TemporalType.DATE)
    @Column(name = "data_end_date")
    private Date dataEndDate;

    @Column(name = "json_string" ,columnDefinition = "TEXT")
    private String jsonString;


    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;


    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;


    @Transient
    private List<AssetGroupDetail> assetGroupDetailList;

    @Setter
    @Getter
    @Data
    public static class AssetGroupDetail {

        private String assetUid;

        private BigDecimal amount;

        private BigDecimal  scaleAssessment;

        private String groupName;

        private String assetName;

    }





}
