package com.ge.apm.view.insp;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.MvsIbMappingMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.ServiceRequestProjection;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionServiceController {

    InspectionOrderRepository inspectionDao = WebUtil.getBean(InspectionOrderRepository.class);
    ServiceRequestRepository srDao = WebUtil.getBean(ServiceRequestRepository.class);
    InventoryOrderDetailAssetInfoRepository infoRepositoryDao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
    InventoryOrderRepository inventoryOrderRepository = WebUtil.getBean(InventoryOrderRepository.class);
    InventoryIssueRepository inventoryIssueRepository = WebUtil.getBean(InventoryIssueRepository.class);
    V2_PmOrderRepository pmDao = WebUtil.getBean(V2_PmOrderRepository.class);
    UaaService uuaService = WebUtil.getBean(UaaService.class);
    AssetInfoMapper assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
    MvsIbMappingMapper mvsIbMappingMapper =  WebUtil.getBean(MvsIbMappingMapper.class);
    ServiceRequestApiService serviceRequestApiService = WebUtil.getBean(ServiceRequestApiService.class);

    public List<InspectionOrder> getInspectionOrderList(Integer assetId,Integer orderType,Integer limit){
        List<InspectionOrder> res = inspectionDao.getRecentlyInspectionOrder(assetId,orderType);
        return res.subList(0, Math.min(limit, res.size()));
    }

    public List<AssetInfo> getMeasureAssetList(Integer assetId){
        Pagination pagination = new Pagination(0, 5000);
        Wrapper<AssetInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("ai.id", assetId);
        wrapper.orderBy("ai.id");
        wrapper.orderBy("mcd.check_date", false);
        return assetInfoMapper.selectMeasureResult(pagination, wrapper);
    }

    public List<ServiceRequestProjection> getWorkOrderList(Integer assetId){
        List<Integer> assetIds = mvsIbMappingMapper.findMvsAssetIds(assetId);
        assetIds.add(assetId);
        Sort sort = new Sort(Sort.Direction.DESC, "requestTime");
        PageRequest pr =  new PageRequest(0, 5000, sort);
        return srDao.findProjectionByAssetIdIn(assetIds, pr).getContent();
    }

    public List<ServiceRequestProjection> getWorkOrderList(Integer assetId, Integer limit){
        List<Integer> assetIds = mvsIbMappingMapper.findMvsAssetIds(assetId);
        assetIds.add(assetId);
        Sort sort = new Sort(Sort.Direction.DESC, "requestTime");
        PageRequest pr =  new PageRequest(0, limit, sort);
        return srDao.findProjectionByAssetIdIn(assetIds, pr).getContent();
    }

    public List<V2_PmOrder> getPmOrderList(Integer assetId){
        List<Integer> assetIds = mvsIbMappingMapper.findMvsAssetIds(assetId);
        assetIds.add(assetId);
        Sort sort = new Sort(Sort.Direction.DESC, "startTime");
        PageRequest pr =  new PageRequest(0, 5000, sort);
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, assetIds));
        return pmDao.findBySearchFilter(searchFilters, pr).getContent();
    }

    public List<V2_PmOrder> getPmOrderList(Integer assetId,Integer limit){
        List<Integer> assetIds = mvsIbMappingMapper.findMvsAssetIds(assetId);
        assetIds.add(assetId);
        Sort sort = new Sort(Sort.Direction.DESC, "startTime");
        PageRequest pr =  new PageRequest(0, limit, sort);
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, assetIds));
        return pmDao.findBySearchFilter(searchFilters, pr).getContent();
    }

    public List<InventoryOrderDetail> getInventoryOrderDetailList(Integer assetId){
        List<InventoryOrderDetail> inventoryOrderDetails = infoRepositoryDao.findProjectionByAssetId(assetId);
        if(inventoryOrderDetails != null && inventoryOrderDetails.size()>0){
            for(InventoryOrderDetail inventoryOrderDetail:inventoryOrderDetails){
                if(inventoryOrderDetail.getStatus() == 0){
                    inventoryOrderDetail.setLastModifiedDate(null);
                    inventoryOrderDetail.setLastModifiedBy(null);
                }
                String inventoryOrderId = inventoryOrderDetail.getInventoryOrderId();
                if(inventoryOrderId != null){
                    InventoryOrder inventoryOrder = inventoryOrderRepository.findById(inventoryOrderId);
                    inventoryOrderDetail.setPlanEndTime(inventoryOrder.getPlanEndTime());
                    inventoryOrderDetail.setPlanStartTime(inventoryOrder.getPlanStartTime());
                    if(inventoryOrderDetail.getStatus() == 1){
                        InventoryIssue inventoryIssue = inventoryIssueRepository.findProjectionByAssetIdAndInventoryOrderId(assetId, inventoryOrderId);
                        if(inventoryIssue != null){
                            inventoryOrderDetail.setStatus(3);
                        }
                    }
                }
            }
        }
        return inventoryOrderDetails;
    }

    public List<UserAccount> getOwnerList(){
        return getOwnerList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    public List<OrgInfo> getOwnerOrgList(){
        return getOwnerOrgList(UserContextService.getCurrentUserAccount().getHospitalId());
    }
    public List<UserAccount> getOwnerList(Integer hospitalId){
        return uuaService.getUserList(hospitalId);
    }
    public List<OrgInfo> getOwnerOrgList(Integer hospitalId){
        return uuaService.getOrgListByHospitalId(hospitalId);
    }
    
    
    
    
    
}
