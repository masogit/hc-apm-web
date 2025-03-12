package com.ge.apm.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AttachmentDTO {
    @JsonProperty("boId")
    private String businessObjectId;

    @JsonProperty("boName")
    private String originalFileName;

    @JsonProperty("boType")
    private FileType fileType;

    @JsonProperty("createdBy")
    private String creatorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
    @JsonProperty("createdDate")
    private Date creationTimestamp;

    @JsonProperty("id")
    private String fileId;

    @JsonProperty("objectId")
    private String relatedObjectId;

    @JsonProperty("type")
    private AttachmentType attachmentType;

    public enum FileType {
        @JsonProperty(".docx") DOCX,
        @JsonProperty("pdf") PDF,
        @JsonProperty("jpg") JPG
    }
    public enum AttachmentType {
        @JsonProperty("workOrderFormReport") WORK_ORDER_REPORT,
        @JsonProperty("workOrderFormReport-pdf") WORK_ORDER_PDF,
        @JsonProperty("customerSignature") CUSTOMER_SIGNATURE
    }

}