package com.ge.apm.domain.his;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "his_web_service_inbound_request")
@Data
@NoArgsConstructor
public class HisWebServiceInboundRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uuid;

    @Column(name = "message_code")
    private String messageCode;

    private String sourceSystem;

    private String messageId;

    @Column(name = "input_stream")
    @Type(type = "text")
    private String inputStream;
}
