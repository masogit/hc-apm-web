package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetCostAmountInfoRepository;
import com.ge.apm.dao.DataEntryRepository;
import com.ge.apm.domain.AssetCostAmountInfo;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.DataEntry;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DataEntryService {

    @Autowired
    private DataEntryRepository dataEntryRepository;

    @Autowired
    private AssetCostAmountInfoRepository assetCostAmountInfoRepository;

    @Transactional
    public void save(DataEntry dataEntry, UserAccount user, List<HisAssetGroupClinicalMatch> selectAssetInfos) {
        if(dataEntry.getUid()!=null){
            assetCostAmountInfoRepository.deleteByDataEntryUid(dataEntry.getUid());
        }
        dataEntryRepository.save(dataEntry);
        LocalDate startDate = dataEntry.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = dataEntry.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<AssetCostAmountInfo> assetCostAmountInfos = new ArrayList<>();
        for (HisAssetGroupClinicalMatch selectAssetInfo : selectAssetInfos) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                AssetCostAmountInfo assetCostAmountInfo = new AssetCostAmountInfo();
                assetCostAmountInfo.setCostAmount(selectAssetInfo.getAmount());
                assetCostAmountInfo.setAssetUid(selectAssetInfo.getAssetUid());
                assetCostAmountInfo.setDataEntryUid(dataEntry.getUid());
                assetCostAmountInfo.setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                assetCostAmountInfos.add(assetCostAmountInfo);
            }
        }
        assetCostAmountInfoRepository.save(assetCostAmountInfos);
    }

    @Transactional
    public void saveAssetEntry(List<AssetInfo> selectAssets, List<DataEntry> dataEntries, UserAccount user) {
        for (AssetInfo selectAsset : selectAssets) {
            for (DataEntry dataEntry : dataEntries) {
                buildDataEntry(dataEntry,selectAsset,user,2);
                dataEntryRepository.save(dataEntry);
                LocalDate startDate = dataEntry.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = dataEntry.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                List<AssetCostAmountInfo> assetCostAmountInfos = new ArrayList<>();
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    AssetCostAmountInfo assetCostAmountInfo = new AssetCostAmountInfo();
                    assetCostAmountInfo.setCostAmount(dataEntry.getEntryAmount());
                    assetCostAmountInfo.setAssetUid(dataEntry.getAssetUid());
                    assetCostAmountInfo.setDataEntryUid(dataEntry.getUid());
                    assetCostAmountInfo.setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    assetCostAmountInfos.add(assetCostAmountInfo);
                }
                assetCostAmountInfoRepository.save(assetCostAmountInfos);
            }
        }
    }

    private void buildDataEntry(DataEntry dataEntry, AssetInfo selectAsset, UserAccount user,int type) {
        dataEntry.setTenantUid(selectAsset.getTenantUID());
        dataEntry.setInstitutionUid(selectAsset.getInstitutionUID());
        dataEntry.setHospitalUid(selectAsset.getHospitalUID());
        dataEntry.setSiteUid(selectAsset.getSiteUID());
        dataEntry.setAssetUid(selectAsset.getUid());
        dataEntry.setOperationUserUid(user.getUid());
        dataEntry.setCreatedDate(new Date());
        dataEntry.setLastModifiedDate(new Date());
        dataEntry.setGroupOrSingle(type);
    }

    @Transactional
    public void deleteDataEntry(DataEntry dataEntry) {
        dataEntryRepository.delete(dataEntry);
        assetCostAmountInfoRepository.deleteByDataEntryUid(dataEntry.getUid());
    }
}
