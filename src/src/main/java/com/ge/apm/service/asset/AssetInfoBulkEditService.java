package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.V2_WorkOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.pm.V2_PmOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssetInfoBulkEditService {

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private ServiceRequestRepository serviceRequestDao;

    @Autowired
    private V2_WorkOrderRepository workOrderDao;

    @Autowired
    private V2_PmOrderService pmOrderService;

    @Autowired
    private V2_PmOrderRepository pmOrderDao;

    @Transactional
    public void commitBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset, List<String> selectedColumns) {

        commitBulkEditForAssetRelated(assets, targetAsset, selectedColumns);

        commitBulkEditForAssetItself(assets, targetAsset, selectedColumns);
    }

    public void commitBulkEditForAssetRelated(List<AssetInfo> assets, AssetInfo targetAsset, List<String> selectedColumns) {

        if (selectedColumns.contains("hospitalId")) {
            commitCorrectiveMaintenanceBulkEdit(assets, targetAsset);
            commitPreventativeMaintenanceBulkEdit(assets, targetAsset);
        }
    }

    private void commitBulkEditForAssetItself(List<AssetInfo> assets, AssetInfo targetAsset, List<String> selectedColumns) {

        for (AssetInfo item: assets) {

            if (selectedColumns.contains("hospitalId")) {
                item.setSiteUID(targetAsset.getSiteOrg().getSiteUID());
                item.setHospitalUID(targetAsset.getSiteOrg().getHospitalUID());
                item.setInstitutionUID(targetAsset.getSiteOrg().getInstitutionUID());
                item.setTenantUID(targetAsset.getSiteOrg().getTenantUID());
            }
            if (selectedColumns.contains("clinicalDeptName")) {
                item.setClinicalDeptId(targetAsset.getClinicalDeptOrg().getId());
                item.setClinicalDeptUID(targetAsset.getClinicalDeptOrg().getUid());
                item.setClinicalDeptName(targetAsset.getClinicalDeptOrg().getName());
            }
            if (selectedColumns.contains("assetGroup")) {
                item.setAssetGroup(targetAsset.getAssetGroup());
            }
        }

        assetInfoDao.save(assets);

    }


    /**
     * 维修 (step 没有site uid)
     * correctvie maintenance
     */
    private void commitCorrectiveMaintenanceBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset) {

        for (AssetInfo assetInfo: assets) {
            List<V2_ServiceRequest> serviceRequests = serviceRequestDao.findByAssetId(assetInfo.getId());

            for (V2_ServiceRequest serviceRequest: serviceRequests) {
                List<V2_WorkOrder> workOrders = serviceRequest.getV2_workOrder_List();

                for (V2_WorkOrder workOrder: workOrders) {
                    List<V2_WorkOrder_Detail> details = workOrder.getV2_workOrder_detail_list();
                    List<V2_WorkOrderCoworker> coworkers = workOrder.getV2_workOrder_coworker_list();

                    updateWorkOrder(workOrder, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                    updateWorkOrderDetails(details, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                    updateWorkOrderCoworks(coworkers, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                }
                workOrderDao.save(workOrders);
            }

            updateServiceRequests(serviceRequests, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());

            serviceRequestDao.save(serviceRequests);
        }
    }

    /**
     * 保养 （附件没有site uid)
     * preventative maintenance
     */
    private void commitPreventativeMaintenanceBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset) {

        for (AssetInfo assetInfo: assets) {
            List<V2_PmOrder> pmOrders = pmOrderDao.getByAssetUid(assetInfo.getUid());

            for (V2_PmOrder pmOrder: pmOrders) {

                List<V2_WorkOrder_Detail> details = pmOrder.getDetails();
                List<V2_WorkOrderCoworker> coworkers = pmOrder.getCoworkers();

                updatePMOrder(pmOrder, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                updateWorkOrderDetails(details, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                updateWorkOrderCoworks(coworkers, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
            }

            pmOrderDao.save(pmOrders);
        }

    }

    private void updateServiceRequests(List<V2_ServiceRequest> serviceRequests, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        for (V2_ServiceRequest item: serviceRequests) {

            item.setSiteUID(targetSite.getSiteUID());
            item.setHospitalUID(targetSite.getHospitalUID());
            item.setInstitutionUID(targetSite.getInstitutionUID());
            item.setTenantUID(targetSite.getTenantUID());
        }
    }

    private void updateWorkOrder(V2_WorkOrder workOrder, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        workOrder.setSiteUID(targetSite.getSiteUID());
        workOrder.setHospitalUID(targetSite.getHospitalUID());
        workOrder.setInstitutionUID(targetSite.getInstitutionUID());
        workOrder.setTenantUID(targetSite.getTenantUID());
    }

    private void updatePMOrder(V2_PmOrder pmOrder, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        pmOrder.setClinicalDeptId(targetClinicalDept.getUid());

        pmOrder.setSiteUID(targetSite.getSiteUID());
        pmOrder.setHospitalUID(targetSite.getHospitalUID());
        pmOrder.setInstitutionUID(targetSite.getInstitutionUID());
        pmOrder.setTenantUID(targetSite.getTenantUID());
    }

    private void updateWorkOrderDetails(List<V2_WorkOrder_Detail> details, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        for (V2_WorkOrder_Detail item: details) {
            item.setSiteUID(targetSite.getSiteUID());
            item.setHospitalUID(targetSite.getHospitalUID());
            item.setInstitutionUID(targetSite.getInstitutionUID());
            item.setTenantUID(targetSite.getTenantUID());
        }
    }

    private void updateWorkOrderCoworks(List<V2_WorkOrderCoworker> coworkers, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        for (V2_WorkOrderCoworker item: coworkers) {
            item.setSiteUID(targetSite.getSiteUID());
            item.setHospitalUID(targetSite.getHospitalUID());
            item.setInstitutionUID(targetSite.getInstitutionUID());
            item.setTenantUID(targetSite.getTenantUID());
        }
    }

}
