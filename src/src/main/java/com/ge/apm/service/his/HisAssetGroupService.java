package com.ge.apm.service.his;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.*;
import com.ge.apm.service.asset.DataEntryService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;

import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    private DataEntryService dataEntryService;

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

    public List<AssetInfo> getAssetListByDept(OrgInfo siteOrg, OrgInfo clinicalDept, boolean isSubEquipment,Integer assetGroup,Double purchasePrice) {
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

        if(assetGroup!=null){
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetGroup));
        }

        if(purchasePrice!=null){
            assetFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, purchasePrice));
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

//        HisAssetGroupAssetMatch hisAssetGroupAssetMatch =  hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getUid());
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch;
        if("Cost".equals(hisAssetGroup.getType())){
            hisAssetGroupAssetMatch = hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndOrgInfoUid(hisAssetGroup.getUuid(),selectOrg.getSiteUID());
        }else{
            hisAssetGroupAssetMatch =hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getUid());
        }

        if (null != hisAssetGroupAssetMatch) {

            if("Cost".equals(hisAssetGroup.getType())){

                if(needPromptInputDates(targetAssets,hisAssetGroup,selectOrg) && updateStartDate!=null && updateEndDate!=null){
                    dataEntryService.updateAssetGroup(hisAssetGroup,targetAssets,updateStartDate,updateEndDate,selectOrg);
                }
                hisAssetGroupClinicalMatchRepository.deleteAssetByHisAssetGroupAndOrgInfoUid(hisAssetGroup.getUuid(),selectOrg.getSiteUID());

            }else{
                String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();

                addNewJobIfBoundUpdated(targetAssets, hisAssetGroup, clinicalDept, updateStartDate, updateEndDate);
                hisAssetGroupClinicalMatchRepository.deleteAssetByHisAssetGroupClinicalId(hisAssetGroupClinicalMatchUid);
            }
            //修改前先移除该分组下所有资产
            insertAssetsToGroup(targetAssets, hisAssetGroup, clinicalDept,selectOrg);
        } else {
            saveHisAssetGroupAssetMatch(clinicalDept, hisAssetGroup, subEquipment,selectOrg);
            insertAssetsToGroup(targetAssets, hisAssetGroup, clinicalDept,selectOrg);
        }
    }

    public void saveHisAssetGroupAssetMatch(OrgInfo clinicalDept, HisAssetGroup hisAssetGroup, Boolean subEquipment,OrgInfo selectOrg){
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = new HisAssetGroupAssetMatch();
        hisAssetGroupAssetMatch.setOrgInfoUid(selectOrg.getSiteUID());
        if(hisAssetGroup.getType().equals("Income")){
            hisAssetGroupAssetMatch.setClinicalDeptUid(clinicalDept.getUid());
            hisAssetGroupAssetMatch.setClinicalDeptName(clinicalDept.getName());
        }else{
            hisAssetGroupAssetMatch.setClinicalDeptUid(null);
            hisAssetGroupAssetMatch.setClinicalDeptName(null);
        }

        hisAssetGroupAssetMatch.setAssetGroupUid(hisAssetGroup.getUuid());
        hisAssetGroupAssetMatch.setAssetGroupName(hisAssetGroup.getName());
        hisAssetGroupAssetMatch.setIsSubEquipment(subEquipment);
        hisAssetGroupAssetMatch.setSiteUid(selectOrg.getSiteUID());
        hisAssetGroupAssetMatch.setTenantUid(selectOrg.getTenantUID());
        hisAssetGroupAssetMatchRepository.save(hisAssetGroupAssetMatch);
    }

    private void insertAssetsToGroup(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept,OrgInfo selectOrg) {
        String hisAssetGroupOrgInfoUid = hisAssetGroup.getUuid() + selectOrg.getSiteUID();

        targetAssets.stream().forEach(item -> {
            HisAssetGroupClinicalMatch hisAssetGroupClinicalMatch = new HisAssetGroupClinicalMatch();
            if(hisAssetGroup.getType().equals("Income")){
                String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();

                hisAssetGroupClinicalMatch.setAssetGroupClinicalId(hisAssetGroupClinicalMatchUid);

            }
            hisAssetGroupClinicalMatch.setAssetUid(item.getUid());
            hisAssetGroupClinicalMatch.setDepartNum(item.getDepartNum());
            hisAssetGroupClinicalMatch.setScaleAssessment(item.getScaleAssessment());
            hisAssetGroupClinicalMatch.setHisAssetGroupUId(hisAssetGroup.getUuid());
            hisAssetGroupClinicalMatch.setClinicalDeptUid(item.getClinicalDeptUID());
            hisAssetGroupClinicalMatch.setSiteUid(selectOrg.getSiteUID());
            hisAssetGroupClinicalMatch.setTenantUid(selectOrg.getTenantUID());
            hisAssetGroupClinicalMatch.setOrgInfoUid(selectOrg.getSiteUID());
            hisAssetGroupClinicalMatch.setAssetGroupOrgInfoUid(hisAssetGroupOrgInfoUid);
            hisAssetGroupClinicalMatchRepository.save(hisAssetGroupClinicalMatch);
        });

    }

    private void addNewJobIfBoundUpdated(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept, Date updateStartDate, Date updateEndDate) {
        String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();
//        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getUid());

//        List<HisAssetGroupClinicalMatch> list = hisAssetGroupClinicalMatchRepository.findByAssetGroupClinicalMatchId(hisAssetGroupClinicalMatchUid);

        List<HisAssetGroupClinicalMatch> list = hisAssetGroupClinicalMatchRepository.findByHisAssetGroupUIdAndOrgInfoUid(hisAssetGroup.getUuid(), clinicalDept.getSiteUID());
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getSiteUID());
        Map<String, List<HisAssetGroupClinicalMatch>> clinicalMap = list.stream().collect(Collectors.groupingBy(HisAssetGroupClinicalMatch::getClinicalDeptUid));
        Map<String, List<AssetInfo>> selectAssetClinicalMap = targetAssets.stream().collect(Collectors.groupingBy(AssetInfo::getClinicalDeptUID));

        for (Map.Entry<String, List<HisAssetGroupClinicalMatch>> clinical : clinicalMap.entrySet()) {
            List<HisAssetGroupClinicalMatch> value = clinical.getValue();
            List<AssetInfo> assetInfos = selectAssetClinicalMap.get(clinical.getKey());
            if(CollectionUtils.isEmpty(assetInfos)){
              continue;
            }
            Set<String> uids1 = value.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toSet());
            Set<String> uids2 = assetInfos.stream().map(AssetInfo::getUid).collect(Collectors.toSet());

            Map<String, BigDecimal> listMap = value.stream().filter(match -> match.getScaleAssessment() != null).collect(Collectors.toMap(HisAssetGroupClinicalMatch::getAssetUid, HisAssetGroupClinicalMatch::getScaleAssessment));
            Map<String, BigDecimal> targetAssetsMap = assetInfos.stream().filter(match -> match.getScaleAssessment() != null).collect(Collectors.toMap(AssetInfo::getUid, AssetInfo::getScaleAssessment));
            boolean flag=false;
            flag = !uids1.equals(uids2);
            if(!flag){
                for (Map.Entry<String, BigDecimal> stringDoubleEntry : listMap.entrySet()) {
                    BigDecimal aDouble = targetAssetsMap.get(stringDoubleEntry.getKey());
                    if((stringDoubleEntry.getValue()==null && aDouble!=null)   ||  (stringDoubleEntry.getValue()!=null && aDouble==null)   || (stringDoubleEntry.getValue()!=null && aDouble!=null &&  !stringDoubleEntry.getValue().equals(aDouble )  )  ){
                        flag=true;
                        break;
                    }

                }
            }
            if (flag) {
                StringBuilder businessId = new StringBuilder();
//                List<HisEquipmentSfxmMatch> listSfxmMatch = sfxmMatchRepository.findByHisGroupMatchUid(hisAssetGroupAssetMatch.getUuid());
                List<HisEquipmentSfxmMatch> listSfxmMatch = sfxmMatchRepository.findByHisGroupUidAndclinicalDeptUid(hisAssetGroup.getUuid(), clinical.getKey());

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
                    assetInfos.stream().forEach(item -> {
                        HisAssetGroupClinicalMatch hisAssetGroupClinicalMatch = new HisAssetGroupClinicalMatch();
                        hisAssetGroupClinicalMatch.setAssetGroupClinicalId(hisAssetGroup.getUuid()+clinical.getKey());
                        hisAssetGroupClinicalMatch.setAssetUid(item.getUid());
                        hisAssetGroupClinicalMatch.setDepartNum(item.getDepartNum());
                        hisAssetGroupClinicalMatch.setHisAssetGroupUId(hisAssetGroup.getUuid());
                        hisAssetGroupClinicalMatch.setClinicalDeptUid(item.getClinicalDeptUID());
                        hisAssetGroupClinicalMatch.setOrgInfoUid(clinicalDept.getSiteUID());
                        hisAssetGroupClinicalMatch.setAssetGroupOrgInfoUid(hisAssetGroup.getUuid()+clinicalDept.getSiteUID());
                        hisAssetGroupClinicalMatch.setScaleAssessment(item.getScaleAssessment());
                        newList.add(hisAssetGroupClinicalMatch);
                    });

                    UserAccount loginUser = UserContextService.getCurrentUserAccount();
                    HisAdjustBusinessJob hisAdjustBusinessJob = new HisAdjustBusinessJob();
                    hisAdjustBusinessJob.setOrgCode(listSfxmMatch.get(0).getHosId());
                    hisAdjustBusinessJob.setTaskStatus(0);
                    if(hisAssetGroup.getType().equals("Income")){
                        hisAdjustBusinessJob.setTaskType(3);
                    }else{
                        hisAdjustBusinessJob.setTaskType(0);
                        hisAdjustBusinessJob.setTaskStatus(1);
                    }
                    hisAdjustBusinessJob.setBusinessId(businessId.toString());//收费项目ids
                    hisAdjustBusinessJob.setOldStrJson(value.isEmpty() ? null : JSON.toJSONString(value));
                    hisAdjustBusinessJob.setNewStrJson(newList.isEmpty() ? null : JSON.toJSONString(newList));
                    hisAdjustBusinessJob.setDataStartDate(updateStartDate);
                    hisAdjustBusinessJob.setDataEndDate(updateEndDate);
                    hisAdjustBusinessJob.setCreatedBy(loginUser.getLoginName());
                    hisAdjustBusinessJob.setCreatedDate(new Date());
                    hisAdjustBusinessJob.setTenantUid(listSfxmMatch.get(0).getTenantUid());
                    hisAdjustBusinessJob.setSiteUid(listSfxmMatch.get(0).getSiteUid());
                    hisAdjustBusinessJobRepository.save(hisAdjustBusinessJob);
                }


            }

        }



    }

    private void addNewJobIfBoundUpdated2(List<AssetInfo> targetAssets, HisAssetGroup hisAssetGroup, OrgInfo clinicalDept, Date updateStartDate, Date updateEndDate) {
        String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch;
        List<HisAssetGroupClinicalMatch> list;
        if(hisAssetGroup.getType().equals("Income")){
            hisAssetGroupAssetMatch= hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndClinicalDeptUid(hisAssetGroup.getUuid(), clinicalDept.getUid());
             list = hisAssetGroupClinicalMatchRepository.findByAssetGroupClinicalMatchId(hisAssetGroupClinicalMatchUid);

        }else{
            hisAssetGroupAssetMatch= hisAssetGroupAssetMatchRepository.findByAssetGroupUidAndOrgInfoUid(hisAssetGroup.getUuid(), clinicalDept.getSiteUID());
            list = hisAssetGroupClinicalMatchRepository.findByHisAssetGroupUIdAndOrgInfoUid( hisAssetGroup.getUuid(),clinicalDept.getSiteUID());

        }
        Set<String> uids1 = list.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toSet());
        Set<String> uids2 = targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toSet());

        boolean flag=false;


        Map<String, BigDecimal> listMap = list.stream().collect(Collectors.toMap(HisAssetGroupClinicalMatch::getAssetUid, HisAssetGroupClinicalMatch::getScaleAssessment));
        Map<String, BigDecimal> targetAssetsMap = targetAssets.stream().collect(Collectors.toMap(AssetInfo::getUid, AssetInfo::getScaleAssessment));

        for (Map.Entry<String, BigDecimal> stringDoubleEntry : listMap.entrySet()) {
            BigDecimal aDouble = targetAssetsMap.get(stringDoubleEntry.getKey());
            if((stringDoubleEntry.getValue()==null && aDouble!=null)   ||  (stringDoubleEntry.getValue()!=null && aDouble==null)   || (stringDoubleEntry.getValue()!=null && aDouble!=null &&  !stringDoubleEntry.getValue().equals(aDouble )  )  ){
                flag=true;
            }

        }

        if ((!uids1.equals(uids2)) ||  flag) {

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
                    if(hisAssetGroup.getType().equals("Income")){
                        hisAssetGroupClinicalMatch.setClinicalDeptUid(item.getClinicalDeptUID());
                    }else{
                        hisAssetGroupClinicalMatch.setClinicalDeptUid(clinicalDept.getUid());
                    }
                    hisAssetGroupClinicalMatch.setOrgInfoUid(clinicalDept.getSiteUID());
                    hisAssetGroupClinicalMatch.setAssetGroupOrgInfoUid(hisAssetGroup.getUuid()+clinicalDept.getSiteUID());
                    hisAssetGroupClinicalMatch.setScaleAssessment(item.getScaleAssessment());
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
        List<HisAssetGroupClinicalMatch> list;
        if("Cost".equals(hisAssetGroup.getType())){
            list = hisAssetGroupClinicalMatchRepository.findByHisAssetGroupUIdAndOrgInfoUid(hisAssetGroup.getUuid(), clinicalDept.getSiteUID());
        }else{
            String hisAssetGroupClinicalMatchUid = hisAssetGroup.getUuid() + clinicalDept.getUid();
            list = hisAssetGroupClinicalMatchRepository.findByAssetGroupClinicalMatchId(hisAssetGroupClinicalMatchUid);
        }
        Set<String> uids1 = list.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toSet());
        Set<String> uids2 = targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toSet());
        if (!uids1.equals(uids2)) {
            return Boolean.TRUE;
        }

        Map<String, BigDecimal> listMap = list.stream().filter(match -> match.getScaleAssessment() != null).collect(Collectors.toMap(HisAssetGroupClinicalMatch::getAssetUid, HisAssetGroupClinicalMatch::getScaleAssessment));
        Map<String, BigDecimal> targetAssetsMap = targetAssets.stream().filter(match -> match.getScaleAssessment() != null).collect(Collectors.toMap(AssetInfo::getUid, AssetInfo::getScaleAssessment));

        for (Map.Entry<String, BigDecimal> stringDoubleEntry : listMap.entrySet()) {
            BigDecimal aDouble = targetAssetsMap.get(stringDoubleEntry.getKey());
            if((stringDoubleEntry.getValue()==null && aDouble!=null)   ||  (stringDoubleEntry.getValue()!=null && aDouble==null)   || (stringDoubleEntry.getValue()!=null && aDouble!=null &&  !stringDoubleEntry.getValue().equals(aDouble )  )  ){
                return Boolean.TRUE;
            }

        }



        return false;
    }


}
