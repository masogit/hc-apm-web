package com.ge.apm.service.his;

import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HisAssetGroupService {

    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchRepository;
    @Autowired
    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchRepository;
    @Autowired
    private HisAdjustBusinessJobRepository hisAdjustBusinessJobRepository;
    @Autowired
    private HisEquipmentSfxmMatchRepository sfxmMatchRepository;
    @Autowired
    private OrgInfoRepository orgInfoRepository;

    public boolean isAssetGroupAlreadyBoundInOtherGroup(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup) {
        List<String> assetUids = targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toList());
        int count = hisAssetGroupClinicalMatchRepository.countAssetsInOtherGroups(assetUids, hisAssetGroup.getName());

        return count > 0;
    }

    public void saveHisAssetGroupAssetMatch(OrgInfo selectedOrg, OrgInfo filterDeptOrg, HisAssetGroup hisAssetGroup, Boolean subEquipment){
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = new HisAssetGroupAssetMatch();
        hisAssetGroupAssetMatch.setOrgInfoUid(selectedOrg.getUid());
        hisAssetGroupAssetMatch.setClinicalDeptUid(filterDeptOrg.getUid());
        hisAssetGroupAssetMatch.setClinicalDeptName(filterDeptOrg.getName());
        hisAssetGroupAssetMatch.setAssetGroupUid(hisAssetGroup.getUuid());
        hisAssetGroupAssetMatch.setAssetGroupName(hisAssetGroup.getName());
        hisAssetGroupAssetMatch.setIsSubEquipment(subEquipment);
        hisAssetGroupAssetMatchRepository.save(hisAssetGroupAssetMatch);
    }

    public List<AssetInfo> getAssetListByDept(OrgInfo siteOrg, OrgInfo clinicalDept, boolean isSubEquipment) {
        List<String> siteOrgUids = orgInfoRepository.getSiteOrgUidsByParentUid(siteOrg.getUid());

        List<SearchFilter> assetFilters = new ArrayList<>();

        assetFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE));
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, siteOrg.getTenantUID()));

        assetFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
        assetFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));

        if (null != clinicalDept) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, clinicalDept.getUid()));
        }

        if(isSubEquipment){
            assetFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNotNull, null));
        } else {
            assetFilters.add(new SearchFilter("parentAssetId", SearchFilter.Operator.IsNull, null));
        }

        Pageable pageable =  new PageRequest(0, 5000); //PageRequest.of(0, 5000);
        return assetDao.findBySearchFilter(assetFilters, pageable).getContent();
    }

    @Transactional
    public void bindAssetsToGroup(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept, boolean subEquipment, Date updateStartDate, Date updateEndDate,OrgInfo selectOrg){
        String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch =  hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getUid());

        if (null != hisAssetGroupAssetMatch) {

            addNewJobIfBoundUpdated(targetAssets, hisAssetGroup, clinicalDept, updateStartDate, updateEndDate);

            //修改前先移除该分组下所有资产
            hisAssetGroupClinicalMatchRepository.deleteAssetByHisAssetGroupClinicalId(hisAssetGroupClinicalMatchUid);
            insertAssetsToGroup(targetAssets, hisAssetGroup, clinicalDept,selectOrg);
        } else {
            saveHisAssetGroupAssetMatch(clinicalDept, hisAssetGroup, subEquipment,selectOrg);
            insertAssetsToGroup(targetAssets, hisAssetGroup, clinicalDept,selectOrg);
        }
    }

    public void saveHisAssetGroupAssetMatch(OrgInfo clinicalDept, HisAssetGroup hisAssetGroup, Boolean subEquipment,OrgInfo selectOrg){
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = new HisAssetGroupAssetMatch();
        hisAssetGroupAssetMatch.setOrgInfoUid(clinicalDept.getSiteUID());
        hisAssetGroupAssetMatch.setClinicalDeptUid(clinicalDept.getUid());
        hisAssetGroupAssetMatch.setClinicalDeptName(clinicalDept.getName());
        hisAssetGroupAssetMatch.setAssetGroupUid(hisAssetGroup.getUuid());
        hisAssetGroupAssetMatch.setAssetGroupName(hisAssetGroup.getName());
        hisAssetGroupAssetMatch.setIsSubEquipment(subEquipment);
        hisAssetGroupAssetMatch.setSiteUid(selectOrg.getSiteUID());
        hisAssetGroupAssetMatch.setTenantUid(selectOrg.getTenantUID());
        hisAssetGroupAssetMatchRepository.save(hisAssetGroupAssetMatch);
    }

    private void insertAssetsToGroup(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept,OrgInfo selectOrg) {
        String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();

        targetAssets.stream().forEach(item -> {
            HisAssetGroupClinicalMatch hisAssetGroupClinicalMatch = new HisAssetGroupClinicalMatch();
            hisAssetGroupClinicalMatch.setAssetGroupClinicalId(hisAssetGroupClinicalMatchUid);
            hisAssetGroupClinicalMatch.setAssetUid(item.getUid());
            hisAssetGroupClinicalMatch.setDepartNum(item.getDepartNum());
            hisAssetGroupClinicalMatch.setScaleAssessment(item.getScaleAssessment());
            hisAssetGroupClinicalMatch.setHisAssetGroupUId(hisAssetGroup.getUuid());
            hisAssetGroupClinicalMatch.setClinicalDeptUid(clinicalDept.getUid());
            hisAssetGroupClinicalMatch.setSiteUid(selectOrg.getSiteUID());
            hisAssetGroupClinicalMatch.setTenantUid(selectOrg.getTenantUID());
            hisAssetGroupClinicalMatchRepository.save(hisAssetGroupClinicalMatch);
        });

    }

    private void addNewJobIfBoundUpdated(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept, Date updateStartDate, Date updateEndDate) {
        String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getUid());

        List<HisAssetGroupClinicalMatch> list = hisAssetGroupClinicalMatchRepository.findByAssetGroupClinicalMatchId(hisAssetGroupClinicalMatchUid);
        Set<String> uids1 = list.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toSet());
        Set<String> uids2 = targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toSet());
        if (!uids1.equals(uids2)) {

            StringBuilder businessId = new StringBuilder();
            List<HisEquipmentSfxmMatch> listSfxmMatch = sfxmMatchRepository.findByHisGroupMatchUid(hisAssetGroupAssetMatch.getUuid());
            if (!listSfxmMatch.isEmpty()) {
                for (HisEquipmentSfxmMatch hisEquipmentSfxmMatch : listSfxmMatch) {
                    if (StringUtils.isNotEmpty(hisEquipmentSfxmMatch.getSfxmId())) {
                        if (businessId.length() > 0) {
                            businessId.append(",");
                        }
                        businessId.append(hisEquipmentSfxmMatch.getSfxmId());
                    }
                }

                List<HisAssetGroupClinicalMatch> newList = Lists.newArrayList();
                targetAssets.stream().forEach(item -> {
                    HisAssetGroupClinicalMatch hisAssetGroupClinicalMatch = new HisAssetGroupClinicalMatch();
                    hisAssetGroupClinicalMatch.setAssetGroupClinicalId(hisAssetGroupClinicalMatchUid);
                    hisAssetGroupClinicalMatch.setAssetUid(item.getUid());
                    hisAssetGroupClinicalMatch.setDepartNum(item.getDepartNum());
                    hisAssetGroupClinicalMatch.setHisAssetGroupUId(hisAssetGroup.getUuid());
                    hisAssetGroupClinicalMatch.setClinicalDeptUid(clinicalDept.getUid());
                    newList.add(hisAssetGroupClinicalMatch);
                });

                UserAccount loginUser = UserContextService.getCurrentUserAccount();
                HisAdjustBusinessJob hisAdjustBusinessJob = new HisAdjustBusinessJob();
                hisAdjustBusinessJob.setOrgCode(listSfxmMatch.get(0).getHosId());
                hisAdjustBusinessJob.setTaskType(3);
                hisAdjustBusinessJob.setBusinessId(businessId.toString());//收费项目ids
                hisAdjustBusinessJob.setOldStrJson(list.isEmpty() ? null : JSONArray.fromObject(list).toString());
                hisAdjustBusinessJob.setNewStrJson(newList.isEmpty() ? null : JSONArray.fromObject(newList).toString());
                hisAdjustBusinessJob.setDataStartDate(updateStartDate);
                hisAdjustBusinessJob.setDataEndDate(updateEndDate);
                hisAdjustBusinessJob.setTaskStatus(0);
                hisAdjustBusinessJob.setCreatedBy(loginUser.getLoginName());
                hisAdjustBusinessJob.setCreatedDate(new Date());
                hisAdjustBusinessJob.setTenantUid(listSfxmMatch.get(0).getTenantUid());
                hisAdjustBusinessJob.setSiteUid(listSfxmMatch.get(0).getSiteUid());
                hisAdjustBusinessJobRepository.save(hisAdjustBusinessJob);
            }


        }
    }

    public boolean needPromptInputDates(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept)
    {
        String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();

        List<HisAssetGroupClinicalMatch> list = hisAssetGroupClinicalMatchRepository.findByAssetGroupClinicalMatchId(hisAssetGroupClinicalMatchUid);
        Set<String> uids1 = list.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toSet());
        Set<String> uids2 = targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toSet());
        if (!uids1.equals(uids2)) {
            return Boolean.TRUE;
        }

        return false;
    }


}
