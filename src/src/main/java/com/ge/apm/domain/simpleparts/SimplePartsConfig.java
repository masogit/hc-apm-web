package com.ge.apm.domain.simpleparts;

import com.ge.apm.service.utils.BooleanUtils;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "simple_parts_config")
@Data
public class SimplePartsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "enable_warehouse_selection_in_request")
    private Boolean enableWarehouseSelectionInRequest;

    @Column(name = "enable_warehouse_locator")
    private Boolean enableWarehouseLocator;

    @Column(name = "enable_integration_with_work_order")
    private Boolean enableIntegrationWithWorkOrder;

    @Column(name = "enable_integration_with_multi_level_auditing")
    private Boolean enableIntegrationWithMultiLevelAuditing;

    @Column(name = "tenant_uid")
    private String tenantUid;

    public boolean isEnableWarehouseLocator() {
        return enableWarehouseLocator;
    }

    public void setEnableWarehouseLocator(boolean enableWarehouseLocator) {
        this.enableWarehouseLocator = enableWarehouseLocator;
    }

    public Boolean isEnableWarehouseSelectionInRequest() {
        return enableWarehouseSelectionInRequest;
    }

    public void setEnableWarehouseSelectionInRequest(Boolean enableWarehouseSelectionInRequest) {
        this.enableWarehouseSelectionInRequest = enableWarehouseSelectionInRequest;
    }

    public boolean isEnableIntegrationWithWorkOrder() {
        return BooleanUtils.getFalseIfNull(enableIntegrationWithWorkOrder);
    }

    public void setEnableIntegrationWithWorkOrder(boolean enableIntegrationWithWorkOrder) {
        this.enableIntegrationWithWorkOrder = enableIntegrationWithWorkOrder;
    }

    public boolean isEnableIntegrationWithMultiLevelAuditing() {
        return BooleanUtils.getFalseIfNull(enableIntegrationWithMultiLevelAuditing);
    }

    public void setEnableIntegrationWithMultiLevelAuditing(boolean enableIntegrationWithMultiLevelAuditing) {
        this.enableIntegrationWithMultiLevelAuditing = enableIntegrationWithMultiLevelAuditing;
    }
}
