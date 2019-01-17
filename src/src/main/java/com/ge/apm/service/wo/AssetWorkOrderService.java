package com.ge.apm.service.wo;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.mapper.AssetWorkOrderMapper;
import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.AssetWorkOrder;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by 212605082 on 2019/1/11.
 */
@Service
public class AssetWorkOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AssetWorkOrderService.class);

    @Autowired
    private WorkOrderReportMapper workOrderReportMapper;

    @Autowired
    private OrgInfoTreeService orgInfoTreeService;

    public List<AssetWorkOrder> loadAssetWorkOrderData(OrgInfo selectedOrg, OrgInfo filterDeptOrg, Integer filterAssetGroup, String searchStr, List<OrgInfo> clinicalDeptList){

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if(selectedOrg != null){
            SearchFilter orgFilter = new SearchFilter("orgUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(orgFilter);
        }else {
            String siteUID = UserContextService.getCurrentUserAccount().getSiteUID();
            SearchFilter orgFilter = new SearchFilter("orgUID", SearchFilter.Operator.EQ, siteUID);
            searchFilters.add(orgFilter);
        }

        if(filterDeptOrg !=null){
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(filterDeptOrg);

            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, clinicalDeptList);
            List<String> clinicalDeptUIDList = new ArrayList<>();
            for (OrgInfo oi : tempOrgSearchSet) {
                clinicalDeptUIDList.add(oi.getUid());
            }
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }

        if(filterAssetGroup != null){
            SearchFilter assetGroupFilter = new SearchFilter("assetGroup", SearchFilter.Operator.EQ, filterAssetGroup);
            searchFilters.add(assetGroupFilter);
        }

        if(searchStr != null && searchStr.trim().length() > 0){
            SearchFilter searchStrFilter = new SearchFilter("searchStr", SearchFilter.Operator.IN, searchStr);
            searchFilters.add(searchStrFilter);
        }


        return workOrderReportMapper.statisticsAssetWorkOrder(searchFilters);
    }



}
