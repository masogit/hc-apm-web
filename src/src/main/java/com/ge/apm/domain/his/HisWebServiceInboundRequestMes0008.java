package com.ge.apm.domain.his;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "his_web_service_inbound_request_mes0008")
@Data
@NoArgsConstructor
public class HisWebServiceInboundRequestMes0008 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uuid;

    private String messageCode;

    private String sourceSystem;
    private String messageId;

    private String patPatientId;

    private String paAdmVisitNumber;
    private String paAdmEnCounterTypeCode;

    private String orderItemId;
    private String billStatusCode;
    private String billStatusDesc;
    private String ordSubCatCode;
    private String ordSubCatDesc;
    private String ordCatCode;
    private String ordCatDesc;

    private String acceptDeptCode;
    private String appDeptCode;
    private String orderItemIdBill;
    private String arcItmMastCode;
    private String arcItmMastDesc;
    private String orderQty;
    private String price;
    private String doseUnit;
    private String cost;




}
