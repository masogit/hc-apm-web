package com.ge.apm.service.wo;

import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.AssetWorkOrder;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    UaaService uaaService;

    public List<AssetWorkOrder> loadAssetWorkOrderData(OrgInfo selectedOrg, OrgInfo filterDeptOrg, Integer filterAssetGroup, String searchStr, List<OrgInfo> clinicalDeptList, Date starDate, Date endDate, Set<String> assetTypeGroupEncodedSet,  OrgInfo filterFromDeptOrg ){

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        ZoneId zone = ZoneId.systemDefault();

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
        } else {
            List<String> allAccessibleClinicalDeptUids = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> item.getOrgType() == 4).map(orgInfo -> orgInfo.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, allAccessibleClinicalDeptUids));
        }

        if(filterFromDeptOrg !=null){
            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(filterFromDeptOrg);

            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, clinicalDeptList);
            List<String> clinicalDeptUIDList = new ArrayList<>();
            for (OrgInfo oi : tempOrgSearchSet) {
                clinicalDeptUIDList.add(oi.getUid());
            }
            searchFilters.add(new SearchFilter("fromDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }

        if(filterAssetGroup != null){
            SearchFilter assetGroupFilter = new SearchFilter("assetGroup", SearchFilter.Operator.EQ, filterAssetGroup);
            searchFilters.add(assetGroupFilter);
        }

        if(searchStr != null && searchStr.trim().length() > 0){
            SearchFilter searchStrFilter = new SearchFilter("searchStr", SearchFilter.Operator.IN, searchStr);
            searchFilters.add(searchStrFilter);
        }

        if(starDate != null){
            SearchFilter searchStrFilter = new SearchFilter("createdDateStart", SearchFilter.Operator.GTE, starDate);
            searchFilters.add(searchStrFilter);
        }

        if(endDate != null){
            LocalDate endLocalDate = LocalDateTime.ofInstant(endDate.toInstant(), zone).toLocalDate();
            SearchFilter searchStrFilter = new SearchFilter("createdDateEnd", SearchFilter.Operator.LT, Date.from(endLocalDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant()));
            searchFilters.add(searchStrFilter);
        }

        if (ObjectUtil.isNotEmpty(assetTypeGroupEncodedSet)) {
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, assetTypeGroupEncodedSet));
        }


        return workOrderReportMapper.statisticsAssetWorkOrder(searchFilters);
    }



}
