package com.ge.apm.service.uaa;
  
import lombok.Data;

@Data
public class MvsIbMappingDto {  
    private Integer ibId;
    private String ibSystemId;
    private String mvsSystemId;  
    private String ibSystemNum;
    private String mvsQrcode;  
    private Integer mvsIbId;
    private String ibTenantName;
    private String ibTenantUid;
    private String createdBy;
}