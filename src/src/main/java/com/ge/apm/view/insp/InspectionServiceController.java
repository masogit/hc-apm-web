package com.ge.apm.view.insp;

import com.ge.apm.dao.InspectionOrderRepository;
import com.ge.apm.dao.ServiceRequestRepository;
import com.ge.apm.dao.V2_PmOrderRepository;
import com.ge.apm.dao.WorkOrderRepository;
import com.ge.apm.domain.InspectionOrder;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_ServiceRequest;
import com.ge.apm.domain.pure.ServiceRequestProjection;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class InspectionServiceController {

    InspectionOrderRepository inspectionDao = WebUtil.getBean(InspectionOrderRepository.class);
    WorkOrderRepository workOrderDao = WebUtil.getBean(WorkOrderRepository.class);
    ServiceRequestRepository srDao = WebUtil.getBean(ServiceRequestRepository.class);
    V2_PmOrderRepository pmDao = WebUtil.getBean(V2_PmOrderRepository.class);
    UaaService uuaService = WebUtil.getBean(UaaService.class);
    
    public List<InspectionOrder> getInspectionOrderList(Integer assetId,Integer orderType,Integer limit){
        List<InspectionOrder> res = inspectionDao.getRecentlyInspectionOrder(assetId,orderType);
        return res.subList(0, Math.min(limit, res.size()));
    }
    
    public List<ServiceRequestProjection> getWorkOrderList(Integer assetId, Integer limit){
        Sort sort = new Sort(Sort.Direction.DESC, "requestTime");
        PageRequest pr =  new PageRequest(0, limit, sort);
        return srDao.findProjectionByAssetId(assetId, pr).getContent();
    }
    
    public List<V2_PmOrder> getPmOrderList(String assetUid,Integer limit){
        Sort sort = new Sort(Sort.Direction.DESC, "startTime");
        PageRequest pr =  new PageRequest(0, limit, sort);
        List<SearchFilter> searchFilters = new ArrayList();
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.EQ, assetUid));
        return pmDao.findBySearchFilter(searchFilters, pr).getContent();
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
