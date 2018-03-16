package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212605082 on 2017/5/15.
 */
@Component
public class AssetTagService {

    @Autowired
    private BiomedGroupRepository biomedGroupDao;
    @Autowired
    private AssetTagBiomedGroupRepository assetTagBiomedGroupDao;
    @Autowired
    private AssetTagRuleRepository assetTagRuleDao;
    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private AssetTagRepository assetTagDao;

    public List<BiomedGroup> getBiomedGroupList(BiomedGroup queryBiomedGroup){
        List<SearchFilter> biomedGroupFilters = new ArrayList<>();
        if(queryBiomedGroup.getTenantUID() != null){
            biomedGroupFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, queryBiomedGroup.getTenantUID()));
        }
        if(queryBiomedGroup.getInstitutionUID() != null){
            biomedGroupFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, queryBiomedGroup.getInstitutionUID()));
        }
        if(queryBiomedGroup.getHospitalUID() != null){
            biomedGroupFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, queryBiomedGroup.getHospitalUID()));
        }
        if(queryBiomedGroup.getSiteUID() != null){
            biomedGroupFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, queryBiomedGroup.getSiteUID()));
        }

        return biomedGroupDao.findBySearchFilter(biomedGroupFilters);
    }

    public List<BiomedGroup> getTargetBiomedGroup(AssetTag tempAssetTag){
        List<BiomedGroup> tempTargetBiomedGroup = new ArrayList<>();

        if(tempAssetTag.getId() != null){
            for(AssetTagBiomedGroup tempAssetTagBiomedGroup : assetTagBiomedGroupDao.findByTagId(tempAssetTag.getId())){
                tempTargetBiomedGroup.add(tempAssetTagBiomedGroup.getBiomedGroup());
            }
        }

        return tempTargetBiomedGroup;
    }

    public List<AssetInfo> getAssetList(AssetInfo queryAsset) {

        if(queryAsset.getTenantUID() != null) {
            List<SearchFilter> assetFilters = new ArrayList<>();

            //assetFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, queryAsset.getSiteId()));
            assetFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE));
            assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE));

            if (queryAsset.getTenantUID() != null) {
                assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, queryAsset.getTenantUID()));
            }
            if (queryAsset.getInstitutionUID() != null) {
                assetFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, queryAsset.getInstitutionUID()));
            }
            if (queryAsset.getHospitalUID() != null) {
                assetFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, queryAsset.getHospitalUID()));
            }
            if (queryAsset.getSiteUID() != null) {
                assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, queryAsset.getSiteUID()));
            }
            if (null != queryAsset.getClinicalDeptId()) {
                assetFilters.add(new SearchFilter("clinicalDeptId", SearchFilter.Operator.EQ, queryAsset.getClinicalDeptId()));
            }
            if (null != queryAsset.getAssetGroup()) {
                assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, queryAsset.getAssetGroup()));
            }

            //if there are 10,000 records displayed in pick list, the dropdown filter will not work due to large data set.
            Pageable pageable = new PageRequest(0, 5000);
            Page page = assetDao.findBySearchFilter(assetFilters, pageable);
            return page.getContent();
        }else{
            return new ArrayList<AssetInfo>();
        }

    }

    public List<AssetInfo> getTargetAssets(AssetTag tempAssetTag){
        List<AssetInfo> tempTargetAssets = new ArrayList<>();

        if(tempAssetTag.getId() != null){
            for(AssetTagRule tempAssetTagRule : assetTagRuleDao.findByTagId(tempAssetTag.getId())){
                tempTargetAssets.add(tempAssetTagRule.getAssetInfo());
            }
        }

        return tempTargetAssets;
    }

    @Transactional
    public void saveAssetTag(AssetTag tempAssetTag, List<BiomedGroup> targetBiomedGroup, List<AssetInfo> targetAssets){

        if(tempAssetTag.getId() != null){
            assetTagBiomedGroupDao.deleteByTagId(tempAssetTag.getId());
            assetTagRuleDao.deleteByTagId(tempAssetTag.getId());
        }

        for(BiomedGroup tempBiomedGroup : targetBiomedGroup){
            AssetTagBiomedGroup tempAssetTagBiomedGroup = new AssetTagBiomedGroup();
            tempAssetTagBiomedGroup.setBiomedGroupId(tempBiomedGroup.getId());
            tempAssetTagBiomedGroup.setTagId(tempAssetTag.getId());
            assetTagBiomedGroupDao.save(tempAssetTagBiomedGroup);
        }

        for(AssetInfo tempAssetInfo : targetAssets){
            AssetTagRule tempAssetTagRule = new AssetTagRule();
            tempAssetTagRule.setAssetId(tempAssetInfo.getId());
            tempAssetTagRule.setTagId(tempAssetTag.getId());
            assetTagRuleDao.save(tempAssetTagRule);
        }
    }

    @Transactional
    public void delete(AssetTag item){
        assetTagRuleDao.deleteByTagId(item.getId());
        AssetTag assetTag = assetTagDao.findById(item.getId());
        assetTagDao.delete(assetTag);
    }
}
