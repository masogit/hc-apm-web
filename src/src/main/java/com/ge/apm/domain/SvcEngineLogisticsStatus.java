package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2021/6/28
 * @description :
 */
@Entity
@Table(name = "svc_engine_logistics_status")
public class SvcEngineLogisticsStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 订单状态（ 18 中状态，后续待确认哪些是：待发货、已发货、已送达）
     */
    @Column(name = "decoded_status")
    public String decodedStatus;

    @Column(name = "local_status_code")
    public String localStatusCode;

    @Column(name = "local_status_name")
    public String localStatusName;

    public String getDecodedStatus() {
        return decodedStatus;
    }

    public void setDecodedStatus(String decodedStatus) {
        this.decodedStatus = decodedStatus;
    }

    public String getLocalStatusCode() {
        return localStatusCode;
    }

    public void setLocalStatusCode(String localStatusCode) {
        this.localStatusCode = localStatusCode;
    }

    public String getLocalStatusName() {
        return localStatusName;
    }

    public void setLocalStatusName(String localStatusName) {
        this.localStatusName = localStatusName;
    }
}
