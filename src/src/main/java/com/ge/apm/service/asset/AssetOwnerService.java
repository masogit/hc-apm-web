/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.UserAccount;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class AssetOwnerService {

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private UserAccountRepository userDao;

    public List<AssetInfo> getAssetList(AssetInfo queryAsset) {

        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        if (null != queryAsset.getAssetGroup()) {
            OrgInfoFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (null != queryAsset.getClinicalDeptId()) {
            OrgInfoFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
        }
        if (null != queryAsset.getAssetOwnerId()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId()));
        }
        if (null != queryAsset.getAssetOwnerId2()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId2", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId2()));
        }

        return assetDao.findBySearchFilter(OrgInfoFilters);
    }

    public List<AssetInfo> getAssetListEx(AssetInfo queryAsset) {

        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
        OrgInfoFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, queryAsset.getHospitalId()));
        if (null != queryAsset.getAssetGroup()) {
            OrgInfoFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
        }
        if (null != queryAsset.getClinicalDeptUID()) {
            OrgInfoFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptUID()));
        }
        if (null != queryAsset.getAssetOwnerId()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId()));
        }
        if (null != queryAsset.getAssetOwnerId2()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId2", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId2()));
        }
        if (null != queryAsset.getAssetOwnerId3()) {
            OrgInfoFilters.add(new SearchFilter("assetOwnerId3", SearchFilter.Operator.EQ, queryAsset.getAssetOwnerId3()));
        }

        return assetDao.findBySearchFilter(OrgInfoFilters);
    }

    public UserAccount getOwnerAccount(Integer userId) {
        return userDao.getById(userId);
    }

    @Transactional
    public void updateAssetOwner(List<AssetInfo> targetAssets,
                                 Boolean chanegeOwner, Integer ownerId,
                                 Boolean chanegeOwner2, Integer ownerId2,
                                 Boolean chanegeOwner3, Integer ownerId3) {

        UserAccount owner = null;
        UserAccount owner2 = null;
        UserAccount owner3 = null;
        if (null != ownerId && chanegeOwner) {
            owner = getOwnerAccount(ownerId);
        }
        if (null != ownerId2 && chanegeOwner2) {
            owner2 = getOwnerAccount(ownerId2);
        }
        if (null != ownerId3 && chanegeOwner3) {
            owner3 = getOwnerAccount(ownerId3);
        }
        for (AssetInfo item : targetAssets) {
            if (chanegeOwner) {
                item.setAssetOwnerId(null == owner ? null : owner.getId());
                item.setAssetOwnerName(null == owner ? null : owner.getName());
                item.setAssetOwnerTel(null == owner ? null : owner.getTelephone());
            }
            if (chanegeOwner2) {
                item.setAssetOwnerId2(null == owner2 ? null : owner2.getId());
                item.setAssetOwnerName2(null == owner2 ? null : owner2.getName());
                item.setAssetOwnerTel2(null == owner2 ? null : owner2.getTelephone());
            }
            if (chanegeOwner3) {
                item.setAssetOwnerId3(null == owner3 ? null : owner3.getId());
                item.setAssetOwnerName3(null == owner3 ? null : owner3.getName());
                item.setAssetOwnerTel3(null == owner3 ? null : owner3.getTelephone());
            }
        }

        assetDao.save(targetAssets);
    }

}
