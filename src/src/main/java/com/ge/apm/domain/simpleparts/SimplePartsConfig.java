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

    //配置备件是从外部系统同步过来整个租户公用的情况(适用于维修中的增加备件时选择“库房领用”的场景)
    @Column(name = "enable_tenant_local")
    private Boolean enableTenantLocal;

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

    public boolean isEnableTenantLocal() {
        return BooleanUtils.getFalseIfNull(enableTenantLocal);
    }

    public void setEnableTenantLocal(boolean enableTenantLocal) {
        this.enableTenantLocal = enableTenantLocal;
    }
}
