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
    private String boType;

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
    private String type;



}