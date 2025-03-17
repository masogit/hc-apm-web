package com.ge.apm.domain.budget;

import lombok.Getter;
import lombok.Setter;

public class MaintenanceContractApplyPurchaseStatusVo {

    @Getter
    @Setter
    private String actionName;
    @Getter@Setter
    private boolean canEdit = true;
    @Getter@Setter
    private boolean showTech = false;
    @Getter@Setter
    private boolean showMeetReasoning = false;
    @Getter@Setter
    private boolean showPurchaseConfirm = false;

}
