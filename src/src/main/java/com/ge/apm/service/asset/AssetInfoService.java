/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetContract;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInfoService {

    AssetInfoRepository assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
    AssetContractRepository contractDao = WebUtil.getBean(AssetContractRepository.class);
    OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);

    UaaService uaaService = WebUtil.getBean(UaaService.class);

    public String getAssetInfoName(Integer id) {
        if (null == id || id == 0) {
            return "";
        } else {
            return assetInfoDao.findById(id).getName();
        }
    }

    public AssetInfo getAssetInfo(int id) {
        return assetInfoDao.findById(id);
    }

    public List<AssetInfo> getAssetList() {
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        List<SearchFilter> assetInfoFilters = new ArrayList<>();
        assetInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, currentUser.getSiteId()));
        List<AssetInfo> assetList = assetInfoDao.findBySearchFilter(assetInfoFilters);
        return assetList;
    }

    public List<AssetContract> getContractList(Integer assetId) {
        return contractDao.findByAssetId(assetId);
    }

    public List<OrgInfo> getHospitalList() {
        UserAccount user = UserContextService.getCurrentUserAccount();

        List<OrgInfo> accessList = uaaService.getOrgListByUser(user.getUid());
        //for accessible sites
        List<String> orgUidSet1 = accessList.stream().filter(item -> item.getOrgType() != 4).map(item -> item.getUid()).collect(Collectors.toList());
        //for sites of accessible departments
        Set<String> orgUidSet2 = accessList.stream().filter(item -> item.getOrgType() == 4).map(item -> item.getSiteUID()).collect(Collectors.toSet());

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        if (!orgUidSet1.isEmpty()) {
        orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        orgInfoFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidSet1, "1*2+"));
        }
        if (!orgUidSet2.isEmpty()) {
            orgInfoFilters.add(new SearchFilter("uid", SearchFilter.Operator.IN, new ArrayList(orgUidSet2), "1*2+"));
        }
        orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.NE, 4));
        return orgDao.findBySearchFilter(orgInfoFilters);
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> accessList = uaaService.getOrgListByUser(user.getUid());
        Set<String> orgUidSet = accessList.stream().filter(item -> item.getOrgType() == 4).map(item -> item.getSiteUID()).collect(Collectors.toSet());
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        if (orgUidSet.contains(siteUid)) {
            return accessList.stream().filter(item -> item.getSiteUID().equals(siteUid) && item.getOrgType() == 4).collect(Collectors.toList());
        } else {
            orgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUid));
            orgInfoFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 4));
            return orgDao.findBySearchFilter(orgInfoFilters);
        }
    }
}
