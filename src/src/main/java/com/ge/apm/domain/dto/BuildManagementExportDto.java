package com.ge.apm.domain.dto;

import lombok.Data;

@Data
public class BuildManagementExportDto {
    private String institutionName;
    private String hospitalName;

    private String siteName;

    private String buildingName;
    private String buildingNameEn;
    private String floorName;
    private String floorNameEn;
    private String roomName;
    private String roomNameEn;

    private String UUID;
    private String UCMID;


}
