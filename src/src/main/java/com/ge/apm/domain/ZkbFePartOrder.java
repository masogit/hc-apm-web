package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2020/12/3
 * @description :
 */
public class ZkbFePartOrder implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 额度申请唯一编号（系统自动生成）
     */
    @Column(name = "part_order_no")
    private String partOrderNo;

    /**
     * 唯一编号自增索引
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_order_index")
    private Integer partOrderIndex;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private String orderType;

    /**
     * 当前步骤
     */
    @Column(name = "current_step")
    private String currentStep;

    /**
     * 当前状态
     */
    @Column(name = "status")
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartOrderNo() {
        return partOrderNo;
    }

    public void setPartOrderNo(String partOrderNo) {
        this.partOrderNo = partOrderNo;
    }

    public Integer getPartOrderIndex() {
        return partOrderIndex;
    }

    public void setPartOrderIndex(Integer partOrderIndex) {
        this.partOrderIndex = partOrderIndex;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
