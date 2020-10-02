package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.pm.PmOrderService;
import com.ge.apm.service.pm.V2_PmOrderService;
import org.apache.commons.lang.StringUtils;
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
    private V2_PmOrderService pmOrderService;

    @Autowired
    private V2_PmOrderRepository pmOrderDao;

    @Transactional
    public void commitBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset) {

        commitCorrectiveMaintenanceBulkEdit(assets, targetAsset);

        commitPreventativeMaintenanceBulkEdit(assets, targetAsset);

        commitAssetsBulkEdit(assets, targetAsset);
    }

    private void commitAssetsBulkEdit(List<AssetInfo> assets, AssetInfo targetAsset) {


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

                    updateWorkOrderDetails(details, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                    updateWorkOrderCoworks(coworkers, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                }

            }

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

                updateWorkOrderDetails(details, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
                updateWorkOrderCoworks(coworkers, targetAsset.getSiteOrg(), targetAsset.getClinicalDeptOrg());
            }
        }

    }

    private void updateWorkOrderDetails(List<V2_WorkOrder_Detail> details, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        for (V2_WorkOrder_Detail detail: details) {

        }
    }

    private void updateWorkOrderCoworks(List<V2_WorkOrderCoworker> coworkers, OrgInfo targetSite, OrgInfo targetClinicalDept) {
        for (V2_WorkOrderCoworker detail: coworkers) {

        }
    }

}
