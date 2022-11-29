package com.ge.apm.domain.his;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "his_web_service_inbound_request_mes0068")
@Data
@NoArgsConstructor
public class HisWebServiceInboundRequestMes0068 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uuid;

    private String messageCode;
    private String sourceSystem;
    private String messageId;


    private String businessFieldCode;
    private String chargeChildCode;
    private String code;
    private String codeDesc;
    private String cateCode;
    private String cateDesc;
    private String price;
    private String codeSystemCode;
    private String remarks;
    private String status;
    private String updateUserCode;
    private String updateDate;
    private String updateTime;

//    private LocalDateTime createdDate;

}
