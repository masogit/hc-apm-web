package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.AssetCostAmountInfoRepository;
import com.ge.apm.dao.DataEntryAssetDetailRepository;
import com.ge.apm.dao.DataEntryRepository;
import com.ge.apm.dao.HisGroupDataEntryHistoryRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataEntryService {
    private static final Logger log = LoggerFactory.getLogger(DataEntryService.class);

    @Autowired
    private DataEntryRepository dataEntryRepository;

    @Autowired
    private AssetCostAmountInfoRepository assetCostAmountInfoRepository;

    @Autowired
    private DataEntryAssetDetailRepository dataEntryAssetDetailRepository;

    @Autowired
    private HisGroupDataEntryHistoryRepository hisGroupDataEntryHistoryRepository;

    @Transactional
    public void save(DataEntry dataEntry, UserAccount user, List<HisAssetGroupClinicalMatch> selectAssetInfos) {
        if(dataEntry.getUid()!=null){
            DataEntry oldDataEntry = dataEntryRepository.findById(dataEntry.getUid());
            hisGroupDataEntryHistoryRepository.deleteByDataEntryUid(dataEntry.getUid());
            this.dataEntryUpdate(oldDataEntry);
            dataEntry.setLastModifiedBy(user.getUid());
            dataEntry.setLastModifiedDate(new Date());
        }
        dataEntryRepository.save(dataEntry);
        LocalDate startDate = dataEntry.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = dataEntry.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<AssetCostAmountInfo> assetCostAmountInfos = new ArrayList<>();
        List<DataEntryAssetDetail> dataEntryAssetDetails= new ArrayList<>();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate)+1;
        ArrayList<HisGroupDataEntryHistory.AssetGroupDetail> assetGroupDetails = new ArrayList<>();
        for (HisAssetGroupClinicalMatch selectAssetInfo : selectAssetInfos) {
            BigDecimal divide=BigDecimal.ZERO;
            BigDecimal subtract=BigDecimal.ZERO;
            if(selectAssetInfo.getAmount()!=null){
                 divide = selectAssetInfo.getAmount().divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.DOWN);
                 subtract= selectAssetInfo.getAmount().subtract((divide.multiply(BigDecimal.valueOf(daysBetween))));
                boolean flag=true;
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    AssetCostAmountInfo assetCostAmountInfo = new AssetCostAmountInfo();
                    DataEntryAssetDetail dataEntryAssetDetail = new DataEntryAssetDetail();
                    assetCostAmountInfo.setTenantUid(user.getTenantUID());
                    if(selectAssetInfo.getAmount()!=null){
                        assetCostAmountInfo.setCostAmount(divide);
                        dataEntryAssetDetail.setCostAmount(divide);
                        if(flag && subtract.compareTo(BigDecimal.ZERO)!=0){
                            assetCostAmountInfo.setCostAmount(divide.add(subtract));
                            dataEntryAssetDetail.setCostAmount(divide.add(subtract));
                            flag=false;
                        }
                    }else{
                        assetCostAmountInfo.setCostAmount(BigDecimal.ZERO);
                        dataEntryAssetDetail.setCostAmount(BigDecimal.ZERO);
                    }
                    assetCostAmountInfo.setAssetUid(selectAssetInfo.getAssetUid());
                    assetCostAmountInfo.setStatisticalDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    assetCostAmountInfos.add(assetCostAmountInfo);
                    dataEntryAssetDetail.setDataEntryUid(dataEntry.getUid());
                    dataEntryAssetDetail.setAssetUid(selectAssetInfo.getAssetUid());
                    dataEntryAssetDetail.setEntryAmount(dataEntry.getEntryAmount());
                    dataEntryAssetDetail.setCreatedDate(new Date());
                    dataEntryAssetDetail.setEntryType(dataEntry.getEntryType());
                    dataEntryAssetDetail.setAssetGroupUid(dataEntry.getAssetGroupUid());
                    dataEntryAssetDetail.setAssetGroupOrgInfoUid(dataEntry.getAssetGroupOrgInfoUid());
                    dataEntryAssetDetail.setTenantUid(dataEntry.getTenantUid());
                    dataEntryAssetDetail.setStatisticalDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    dataEntryAssetDetails.add(dataEntryAssetDetail);

                }
            }

            HisGroupDataEntryHistory.AssetGroupDetail assetGroupDetail = new HisGroupDataEntryHistory.AssetGroupDetail();
            assetGroupDetail.setAssetUid(selectAssetInfo.getAssetUid());
            assetGroupDetail.setScaleAssessment(selectAssetInfo.getScaleAssessment());
            assetGroupDetail.setAmount(selectAssetInfo.getAmount());
            assetGroupDetails.add(assetGroupDetail);
        }
        List<String> assetUids = selectAssetInfos.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toList());

        this.saveOrUpdate(assetCostAmountInfos,assetUids,dataEntry.getStartDate(),dataEntry.getEndDate());
        dataEntryAssetDetailRepository.save(dataEntryAssetDetails);
        HisGroupDataEntryHistory hisGroupDataEntryHistory = new HisGroupDataEntryHistory();

        hisGroupDataEntryHistory.setAssetGroupUid(dataEntry.getAssetGroupUid());

        hisGroupDataEntryHistory.setDataStartDate(dataEntry.getStartDate());
        hisGroupDataEntryHistory.setDataEndDate(dataEntry.getEndDate());
        hisGroupDataEntryHistory.setDataEntryUid(dataEntry.getUid());
        hisGroupDataEntryHistory.setCreatedDate(dataEntry.getCreatedDate());
        hisGroupDataEntryHistory.setLastModifiedDate(dataEntry.getLastModifiedDate());
        hisGroupDataEntryHistory.setJsonString(JSON.toJSONString(assetGroupDetails));
        hisGroupDataEntryHistoryRepository.save(hisGroupDataEntryHistory);
    }

    private void dataEntryUpdate( DataEntry oldDataEntry) {
        List<DataEntryAssetDetail> byDataEntryUid = dataEntryAssetDetailRepository.findByDataEntryUid(oldDataEntry.getUid());
        Map<String, DataEntryAssetDetail> dataEntryAssetDetailMap = byDataEntryUid.stream()
                .collect(Collectors.toMap(
                        data -> data.getAssetUid() + ":" +DateUtil.formatDate(data.getStatisticalDate()),
                        data -> data
                ));
        dataEntryAssetDetailRepository.deleteByDataEntryUid(oldDataEntry.getUid());
//             Date startDate = oldDataEntry.getStartDate();
//             Date endDate = oldDataEntry.getEndDate();

        LocalDate startDate =new Date(oldDataEntry.getStartDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = new Date(oldDataEntry.getEndDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<String> assetUids = byDataEntryUid.stream().map(DataEntryAssetDetail::getAssetUid).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(assetUids)){
            List<AssetCostAmountInfo> byAssetUidInAndDateIn = assetCostAmountInfoRepository.getByAssetUidInAndDateIn(assetUids, oldDataEntry.getStartDate(), oldDataEntry.getEndDate());
            for (AssetCostAmountInfo assetCostAmountInfo : byAssetUidInAndDateIn) {
                String key = assetCostAmountInfo.getAssetUid() +":"+ DateUtil.formatDate(assetCostAmountInfo.getStatisticalDate());
                DataEntryAssetDetail dataEntryAssetDetail = dataEntryAssetDetailMap.get(key);
                if(dataEntryAssetDetail!=null){
                    if(dataEntryAssetDetail.getCostAmount()!=null){
                        assetCostAmountInfo.setCostAmount(assetCostAmountInfo.getCostAmount().subtract(dataEntryAssetDetail.getCostAmount()));
                    }
                }
            }
            assetCostAmountInfoRepository.save(byAssetUidInAndDateIn);
        }

    }

    private void saveOrUpdate(List<AssetCostAmountInfo> assetCostAmountInfos, List<String> assetUids , Date startDate, Date endDate) {
        List<AssetCostAmountInfo> byAssetUidInAndDateIn = assetCostAmountInfoRepository.getByAssetUidInAndDateIn(assetUids, startDate, endDate);

        List<AssetCostAmountInfo> toUpdate = new ArrayList<>();
        List<AssetCostAmountInfo> toInsert = new ArrayList<>();
        Map<String, AssetCostAmountInfo> existingData = byAssetUidInAndDateIn.stream()
                .collect(Collectors.toMap(
                        data -> data.getAssetUid() + ":" +DateUtil.formatDate(data.getStatisticalDate()),
                        data -> data
                ));
        for (AssetCostAmountInfo assetCostAmountInfo : assetCostAmountInfos) {
            String key = assetCostAmountInfo.getAssetUid() +":"+ DateUtil.formatDate(assetCostAmountInfo.getStatisticalDate());
            if (existingData.containsKey(key)) {
                toUpdate.add(assetCostAmountInfo);
                AssetCostAmountInfo assetCostAmountInfo1 = existingData.get(key);
                assetCostAmountInfo1.setCostAmount(assetCostAmountInfo1.getCostAmount().add(assetCostAmountInfo.getCostAmount()));
                toInsert.add(assetCostAmountInfo1);

            } else {
                toInsert.add(assetCostAmountInfo);
            }
        }

        assetCostAmountInfoRepository.save(toInsert);
    }





    @Transactional
    public void saveAssetEntry(AssetInfo selectAsset, List<DataEntry> dataEntries, UserAccount user ) {
//        for (AssetInfo selectAsset : selectAssets) {
            for (DataEntry dataEntry : dataEntries) {
                if(dataEntry.getUid()!=null){
                    DataEntry byId = dataEntryRepository.findById(dataEntry.getUid());
                    this.dataEntryUpdate(byId);
                    dataEntry.setLastModifiedBy(user.getUid());
                    dataEntry.setLastModifiedDate(new Date());
                }else{
                    buildDataEntry(dataEntry,selectAsset,user,2);
                }
                dataEntryRepository.save(dataEntry);
                LocalDate startDate = dataEntry.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = dataEntry.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate)+1;

                List<AssetCostAmountInfo> assetCostAmountInfos = new ArrayList<>();

                List<DataEntryAssetDetail> dataEntryAssetDetails= new ArrayList<>();
                BigDecimal divide = dataEntry.getEntryAmount().divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.DOWN);
                BigDecimal subtract = dataEntry.getEntryAmount().subtract((divide.multiply(BigDecimal.valueOf(daysBetween))));

                boolean flag=true;
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    AssetCostAmountInfo assetCostAmountInfo = new AssetCostAmountInfo();
                    DataEntryAssetDetail dataEntryAssetDetail = new DataEntryAssetDetail();
                    assetCostAmountInfo.setTenantUid(user.getTenantUID());
                    assetCostAmountInfo.setCostAmount(divide);
                    dataEntryAssetDetail.setCostAmount(divide);
                    if(flag && subtract.compareTo(BigDecimal.ZERO)!=0){
                        assetCostAmountInfo.setCostAmount(divide.add(subtract));
                        dataEntryAssetDetail.setCostAmount(divide.add(subtract));
                        flag=false;
                    }
                    assetCostAmountInfo.setAssetUid(dataEntry.getAssetUid());
                    assetCostAmountInfo.setStatisticalDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    assetCostAmountInfos.add(assetCostAmountInfo);
                    dataEntryAssetDetail.setDataEntryUid(dataEntry.getUid());
                    dataEntryAssetDetail.setEntryType(dataEntry.getEntryType());
                    dataEntryAssetDetail.setEntryAmount(dataEntry.getEntryAmount());
                    dataEntryAssetDetail.setAssetUid(selectAsset.getUid());
                    dataEntryAssetDetail.setCreatedDate(new Date());
                    dataEntryAssetDetail.setAssetGroupOrgInfoUid(dataEntry.getAssetGroupOrgInfoUid());
                    dataEntryAssetDetail.setTenantUid(dataEntry.getTenantUid());
                    dataEntryAssetDetail.setStatisticalDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    dataEntryAssetDetails.add(dataEntryAssetDetail);

                }
                List<String> uids = new ArrayList<>();
                uids.add(selectAsset.getUid());
                this.saveOrUpdate(assetCostAmountInfos,uids,dataEntry.getStartDate(),dataEntry.getEndDate());
                dataEntryAssetDetailRepository.save(dataEntryAssetDetails);
            }
//        }
    }

    private void buildDataEntry(DataEntry dataEntry, AssetInfo selectAsset, UserAccount user,int type) {
        dataEntry.setTenantUid(selectAsset.getTenantUID());
        dataEntry.setInstitutionUid(selectAsset.getInstitutionUID());
        dataEntry.setHospitalUid(selectAsset.getHospitalUID());
        dataEntry.setSiteUid(selectAsset.getSiteUID());
        dataEntry.setAssetUid(selectAsset.getUid());
        dataEntry.setClinicalDeptUid(selectAsset.getClinicalDeptUID());
        dataEntry.setOperationUserUid(user.getUid());
        dataEntry.setCreatedBy(user.getUid());
        dataEntry.setCreatedName(user.getName());
        dataEntry.setCreatedDate(new Date());
        dataEntry.setLastModifiedBy(user.getUid());
        dataEntry.setLastModifiedDate(new Date());
        dataEntry.setGroupOrSingle(type);
    }

    @Transactional
    public void deleteDataEntry(DataEntry dataEntry) {
        dataEntryRepository.delete(dataEntry);
        this.dataEntryUpdate(dataEntry);
//        assetCostAmountInfoRepository.deleteByDataEntryUid(dataEntry.getUid());
    }

//         List<DataEntry> dataEntries = dataEntryRepository.findByAssetGroupUidAndAssetGroupOrgInfoUid(hisAssetGroup.getUuid(), selectOrg.getSiteUID(),updateStartDate,updateEndDate);

    @Transactional
    public void updateAssetGroup(HisAssetGroup hisAssetGroup, List<AssetInfo> targetAssets, Date updateStartDate, Date updateEndDate, OrgInfo selectOrg) {
        List<DataEntryAssetDetail> byAssetGroupUidAndOrgInfoUidAndDateIn = dataEntryAssetDetailRepository.getByAssetGroupUidAndOrgInfoUidAndDateIn(hisAssetGroup.getUuid(), selectOrg.getSiteUID(), updateStartDate, updateEndDate);
        Map<String, List<DataEntryAssetDetail>> dataEntryUidMap = byAssetGroupUidAndOrgInfoUidAndDateIn.stream().collect(Collectors.groupingBy(DataEntryAssetDetail::getDataEntryUid));
        Set<String> dataEntryUids = byAssetGroupUidAndOrgInfoUidAndDateIn.stream().map(item -> item.getDataEntryUid()).collect(Collectors.toSet());

        List<DataEntry> dataEntries=dataEntryRepository.findByUidIn(dataEntryUids);
        dataEntryAssetDetailRepository.delete(byAssetGroupUidAndOrgInfoUidAndDateIn);



        for (DataEntry dataEntry : dataEntries) {
            List<AssetCostAmountInfo> assetCostAmountInfos = new ArrayList<>();
            List<DataEntryAssetDetail> dataEntryAssetDetails= new ArrayList<>();
            LocalDate startDate =    new Date(dataEntry.getStartDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate =new Date(dataEntry.getEndDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            LocalDate startDate1 = updateStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate1= updateEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate newStartDate= !startDate.isAfter(startDate1)?startDate1:startDate;
            LocalDate newEndDate= !endDate.isAfter(endDate1)?  endDate:endDate1;
            Date starDate = Date.from(newStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date enDate = Date.from(newEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            long daysBetween = ChronoUnit.DAYS.between(newStartDate, newEndDate) + 1;

            List<DataEntryAssetDetail> dataEntryAssetDetails1 = dataEntryUidMap.get(dataEntry.getUid());

            Map<String, DataEntryAssetDetail> dataEntryAssetDetailMap = dataEntryAssetDetails1.stream()
                    .collect(Collectors.toMap(
                            data -> data.getAssetUid() + ":" +DateUtil.formatDate(data.getStatisticalDate()),
                            data -> data
                    ));
            Set<String> dataEntryAssetUids = dataEntryAssetDetails1.stream().map(item -> item.getAssetUid()).collect(Collectors.toSet());
            List<AssetCostAmountInfo> byAssetUidInAndDateIn = assetCostAmountInfoRepository.getByAssetUidInAndDateIn(new ArrayList<>(dataEntryAssetUids), starDate,enDate);
            for (AssetCostAmountInfo assetCostAmountInfo : byAssetUidInAndDateIn) {
                String key = assetCostAmountInfo.getAssetUid() +":"+ DateUtil.formatDate(assetCostAmountInfo.getStatisticalDate());
                DataEntryAssetDetail dataEntryAssetDetail = dataEntryAssetDetailMap.get(key);
                if(dataEntryAssetDetail!=null){
                    if(dataEntryAssetDetail.getCostAmount()!=null){
                        assetCostAmountInfo.setCostAmount(assetCostAmountInfo.getCostAmount().subtract(dataEntryAssetDetail.getCostAmount()));
                    }
                }
            }
            assetCostAmountInfoRepository.save(byAssetUidInAndDateIn);
            BigDecimal sum = dataEntryAssetDetails1.stream().filter(Objects::nonNull).map(item -> {BigDecimal costAmount = item.getCostAmount();if (costAmount == null) {return BigDecimal.ZERO;}return costAmount;}).reduce(BigDecimal.ZERO, BigDecimal::add);

            ArrayList<HisGroupDataEntryHistory.AssetGroupDetail> assetGroupDetails = new ArrayList<>();
            for (AssetInfo targetAsset : targetAssets) {
                boolean flag=true;
                BigDecimal assetAmount =null;
                BigDecimal dayAmount = BigDecimal.ZERO;
                BigDecimal subtract = BigDecimal.ZERO;

                if(targetAsset.getScaleAssessment()!=null){
                     assetAmount = sum.multiply(targetAsset.getScaleAssessment()).divide(BigDecimal.valueOf(100), 6, RoundingMode.DOWN);
                     dayAmount = assetAmount.divide(BigDecimal.valueOf(daysBetween), 2, RoundingMode.DOWN);
                     subtract = assetAmount.subtract((dayAmount.multiply(BigDecimal.valueOf(daysBetween))));
                    for (LocalDate date = newStartDate; !date.isAfter(newEndDate); date = date.plusDays(1)) {
                        AssetCostAmountInfo assetCostAmountInfo = new AssetCostAmountInfo();
                        assetCostAmountInfo.setTenantUid(dataEntry.getTenantUid());
                        DataEntryAssetDetail dataEntryAssetDetail = new DataEntryAssetDetail();
                        assetCostAmountInfo.setCostAmount(dayAmount);
                        dataEntryAssetDetail.setCostAmount(dayAmount);
                        if(assetAmount.compareTo(BigDecimal.ZERO)!=0 && subtract.compareTo(BigDecimal.ZERO)!=0){
                            if(flag && subtract.compareTo(BigDecimal.ZERO)!=0){
                                assetCostAmountInfo.setCostAmount(dayAmount.add(subtract));
                                dataEntryAssetDetail.setCostAmount(dayAmount.add(subtract));
                                flag=false;
                            }
                        }
                        assetCostAmountInfo.setAssetUid(targetAsset.getUid());
                        assetCostAmountInfo.setStatisticalDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        dataEntryAssetDetail.setDataEntryUid(dataEntry.getUid());
                        dataEntryAssetDetail.setAssetUid(targetAsset.getUid());
                        dataEntryAssetDetail.setEntryAmount(dataEntry.getEntryAmount());
                        dataEntryAssetDetail.setCreatedDate(new Date());
                        dataEntryAssetDetail.setEntryType(dataEntry.getEntryType());
                        dataEntryAssetDetail.setAssetGroupUid(dataEntry.getAssetGroupUid());
                        dataEntryAssetDetail.setAssetGroupOrgInfoUid(dataEntry.getAssetGroupOrgInfoUid());
                        dataEntryAssetDetail.setTenantUid(dataEntry.getTenantUid());
                        dataEntryAssetDetail.setStatisticalDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        assetCostAmountInfos.add(assetCostAmountInfo);
                        dataEntryAssetDetails.add(dataEntryAssetDetail);
                    }

                }
                HisGroupDataEntryHistory.AssetGroupDetail assetGroupDetail = new HisGroupDataEntryHistory.AssetGroupDetail();
                assetGroupDetail.setAssetUid(targetAsset.getUid());
                assetGroupDetail.setScaleAssessment(targetAsset.getScaleAssessment());
                assetGroupDetail.setAmount(assetAmount);
                assetGroupDetails.add(assetGroupDetail);
            }
            Set<String> assetUids = targetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toSet());
            ArrayList<String> uidsList = new ArrayList<>(assetUids);

            this.saveOrUpdate(assetCostAmountInfos,uidsList,starDate,enDate);
            dataEntryAssetDetailRepository.save(dataEntryAssetDetails);
            HisGroupDataEntryHistory hisGroupDataEntryHistory = new HisGroupDataEntryHistory();
            hisGroupDataEntryHistory.setAssetGroupUid(hisAssetGroup.getUuid());

            hisGroupDataEntryHistory.setDataStartDate(updateStartDate);
            hisGroupDataEntryHistory.setDataEndDate(updateEndDate);
            hisGroupDataEntryHistory.setDataEntryUid(dataEntry.getUid());
            hisGroupDataEntryHistory.setCreatedDate(new Date());
            hisGroupDataEntryHistory.setLastModifiedDate(new Date());
            hisGroupDataEntryHistory.setJsonString(JSON.toJSONString(assetGroupDetails));
            hisGroupDataEntryHistoryRepository.save(hisGroupDataEntryHistory);

        }

    }

    public DataEntry getByDataEntryUid(String uid){
        return dataEntryRepository.findById(uid);
    }

    @Transactional
    public void deleteByCostType(List<String> deleteEnumKey,String siteUid) {
        log.info("deleteByCostType 配置项删除自定义成本类型开始删除his数据录入 siteUid:{},deleteEnumKey:{}",siteUid,JSON.toJSONString(deleteEnumKey));
        List<DataEntry> dataEntrys = dataEntryRepository.findBySiteUidAndEntryTypeIn(siteUid, deleteEnumKey);
        for (DataEntry dataEntry : dataEntrys) {
            deleteDataEntry(dataEntry);

        }
    }
}
