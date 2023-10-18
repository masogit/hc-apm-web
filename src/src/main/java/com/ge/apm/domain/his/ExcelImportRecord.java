package com.ge.apm.domain.his;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "excel_import_record")
@Data
public class ExcelImportRecord {

    private static final long serialVersionUID = 9L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 导入人
     */
    @Column(name = "import_name")
    private String importName;


    /**
     * 文件名称
     */
    @Column(name = "file_name")
    private String fileName;


    /**
     * 模块
     */
    @Column(name = "module")
    private String module;

    /**
     * 租户id
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUid;

}
