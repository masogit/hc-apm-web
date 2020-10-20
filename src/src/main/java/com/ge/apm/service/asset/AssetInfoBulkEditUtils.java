package com.ge.apm.service.asset;

import com.ge.apm.domain.*;

import java.util.List;

public class AssetInfoBulkEditUtils {

    public static void updateServiceRequests(List<V2_ServiceRequest> serviceRequests, OrgInfo targetSite) {
        for (V2_ServiceRequest item: serviceRequests) {

            item.setSiteUID(targetSite.getSiteUID());
            item.setHospitalUID(targetSite.getHospitalUID());
            item.setInstitutionUID(targetSite.getInstitutionUID());
            item.setTenantUID(targetSite.getTenantUID());
        }
    }

    public static void updateWorkOrder(V2_WorkOrder workOrder, OrgInfo targetSite) {
        workOrder.setSiteUID(targetSite.getSiteUID());
        workOrder.setHospitalUID(targetSite.getHospitalUID());
        workOrder.setInstitutionUID(targetSite.getInstitutionUID());
        workOrder.setTenantUID(targetSite.getTenantUID());
    }

    public static void updatePMOrder(V2_PmOrder pmOrder, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        pmOrder.setClinicalDeptId(targetClinicalDept.getUid());

        pmOrder.setSiteUID(targetSite.getSiteUID());
        pmOrder.setHospitalUID(targetSite.getHospitalUID());
        pmOrder.setInstitutionUID(targetSite.getInstitutionUID());
        pmOrder.setTenantUID(targetSite.getTenantUID());
    }

    public static void updateWorkOrderDetails(List<V2_WorkOrder_Detail> details, OrgInfo targetSite) {
        for (V2_WorkOrder_Detail item: details) {
            item.setSiteUID(targetSite.getSiteUID());
            item.setHospitalUID(targetSite.getHospitalUID());
            item.setInstitutionUID(targetSite.getInstitutionUID());
            item.setTenantUID(targetSite.getTenantUID());
        }
    }

    public static void updateWorkOrderCoworks(List<V2_WorkOrderCoworker> coworkers, OrgInfo targetSite) {
        for (V2_WorkOrderCoworker item: coworkers) {
            item.setSiteUID(targetSite.getSiteUID());
            item.setHospitalUID(targetSite.getHospitalUID());
            item.setInstitutionUID(targetSite.getInstitutionUID());
            item.setTenantUID(targetSite.getTenantUID());
        }
    }

}
