package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import java.util.*;

@Data
public class AssetFileImportCache {


    private AssetFileImportCacheService assetFileImportCacheService;

    private List<Map<String, Object>> importAssetMap;

    private OrgInfo selectedOrg;

    private OrgInfoRepository orgInfoDao;

    private AssetInfoRepository assetInfoDao;

    private UserAccountMapper userAccountMapper;

    private AssetInfoMapper assetInfoMapper;

    public static final String TENANT_ORG_KEY = "TENANT_ORG";


    Map<String, OrgInfo> allCilinicalOfSiteUid;
    Map<String, OrgInfo> orgNamesUriCache;
    Map<String, AssetInfo> departNumInAssetInfoCache;
    //Map<String, UserAccount> orgClinicalCache;
    Multimap<String, UserAccount> userAccountOwnerNameInfoCahce;
    Multimap<String, UserAccount> userAccountOwnerName2InfoCahce;
    Multimap<String, UserAccount> userAccountOwnerName3InfoCahce;
    Multimap<String, UserAccount> userAccountClinicalOwnerInfoCache;
    Multimap<String, UserAccount> userAccountPmOwnerInfoCache;
    Map<String, OrgInfo> flatternLocalOrgNamesCache;
    Map<String, Supplier> supplierCache;
    Map<String, Supplier> manufacturerCache;
    Map<String, Supplier> maintainancerCache;


    public AssetFileImportCache(List<Map<String, Object>> importAssetMap, OrgInfo selectedOrg, AssetFileImportCacheService assetFileImportCacheService) {
        this.assetFileImportCacheService = assetFileImportCacheService;
        this.importAssetMap = importAssetMap;
        if(selectedOrg != null){
            this.selectedOrg = selectedOrg;
        }
        allCilinicalOfSiteUid = getAllCilinicalOfSiteUid();
        departNumInAssetInfoCache = getDepartNumInAssetInfo();
        //orgClinicalCache = getClinicalOwnerNames();
        userAccountOwnerNameInfoCahce = getAssetOwnerNames();
        userAccountOwnerName2InfoCahce = getAssetOwnerNames2();
        userAccountOwnerName3InfoCahce = getAssetOwnerNames3();
        userAccountClinicalOwnerInfoCache = getClinicalOwnerNames();
        userAccountPmOwnerInfoCache = getPmOwnerNames();
        supplierCache = getSupplier();
        manufacturerCache = getManufacturerCache();
        maintainancerCache = getMaintainancerCache();
    }



    public Map<String, OrgInfo> getAllCilinicalOfSiteUid(){
        if(null == selectedOrg){
            return Collections.EMPTY_MAP;
        }
        Map<String, OrgInfo> cilinicalOfSiteUids = assetFileImportCacheService.allCilinicalOfSiteUid(selectedOrg.getSiteUID());
        return cilinicalOfSiteUids;
    }

    public Map<String, AssetInfo> getDepartNumInAssetInfo(){
        //find the same departNum in assetInfo
        List<String> departNums = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            departNums.add(asset.getDepartNum());
        }
        Map<String, AssetInfo> departNumInAssetInfo = assetFileImportCacheService.findDepartNumInAssetInfo(departNums);
        return departNumInAssetInfo;
    }

    public Multimap<String, UserAccount> getAssetOwnerNames(){
        if(null == selectedOrg){
            return HashMultimap.create();
        }
        List<String> assetOwnerNames = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getAssetOwnerName()){
                continue;
            }
            assetOwnerNames.add(asset.getAssetOwnerName());
        }
        if(assetOwnerNames.size() ==0){
            //return (Multimap<String, UserAccount>) Collections.EMPTY_MAP;
            return HashMultimap.create();
        }
        Multimap<String, UserAccount> assetOwners =  assetFileImportCacheService.findAllAssetOwnerName(assetOwnerNames, selectedOrg.getTenantUID());
        return assetOwners;
    }

    public Multimap<String, UserAccount> getAssetOwnerNames2(){
        if(null == selectedOrg){
            return HashMultimap.create();
        }
        List<String> assetOwnerNames2 = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getAssetOwnerName2()){
                continue;
            }
            assetOwnerNames2.add(asset.getAssetOwnerName2());
        }
        if(assetOwnerNames2.size() ==0){
            return HashMultimap.create();
        }
        Multimap<String, UserAccount> assetOwners2 =  assetFileImportCacheService.findAllAssetOwnerName2(assetOwnerNames2, selectedOrg.getTenantUID());
        return assetOwners2;
    }

    public Multimap<String, UserAccount> getAssetOwnerNames3(){
        if(null == selectedOrg){
            return HashMultimap.create();
        }
        List<String> assetOwnerNames3 = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getAssetOwnerName3()){
                continue;
            }
            assetOwnerNames3.add(asset.getAssetOwnerName3());
        }
        if(assetOwnerNames3.size() ==0){
            return HashMultimap.create();
        }
        Multimap<String, UserAccount> assetOwners3 =  assetFileImportCacheService.findAllAssetOwnerName3(assetOwnerNames3, selectedOrg.getTenantUID());
        return assetOwners3;
    }

    public Multimap<String, UserAccount> getClinicalOwnerNames(){
        if(null == selectedOrg){
            return HashMultimap.create();
        }
        List<String> clinicalOwnerNames = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getClinicalOwnerName()){
                continue;
            }
            clinicalOwnerNames.add(asset.getClinicalOwnerName());
        }
        if(clinicalOwnerNames.size() ==0){
            return HashMultimap.create();
        }
        Multimap<String, UserAccount> clinicalOwners =  assetFileImportCacheService.findAllClinicalOwnerName(clinicalOwnerNames, selectedOrg.getTenantUID());
        return clinicalOwners;
    }

    public Multimap<String, UserAccount> getPmOwnerNames(){
        if(null == selectedOrg){
            return HashMultimap.create();
        }
        List<String> assetPmOwnerNames = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getPmOwnerName()){
                continue;
            }
            assetPmOwnerNames.add(asset.getPmOwnerName());
        }
        if(assetPmOwnerNames.size() ==0){
            return HashMultimap.create();
        }
        Multimap<String, UserAccount> pmOwnerNames =  assetFileImportCacheService.findAllPmOwnerName(assetPmOwnerNames, selectedOrg.getTenantUID());
        return pmOwnerNames;
    }

    public Map<String, Supplier> getSupplier(){
        if(null == selectedOrg){
            return Collections.EMPTY_MAP;
        }
        List<String> assetSuppliers = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getVendor()){
                continue;
            }
            assetSuppliers.add(asset.getVendor());
        }
        if(assetSuppliers.size() ==0){
            return Collections.EMPTY_MAP;
        }
        Map<String, Supplier> supplierInfos =  assetFileImportCacheService.findAllSupplier(assetSuppliers, selectedOrg.getTenantUID());
        return supplierInfos;
    }

    public Map<String, Supplier> getManufacturerCache(){
        if(null == selectedOrg){
            return Collections.EMPTY_MAP;
        }
        List<String> assetManufacturers = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getManufacture()){
                continue;
            }
            assetManufacturers.add(asset.getManufacture());
        }
        if(assetManufacturers.size() ==0){
            return Collections.EMPTY_MAP;
        }
        Map<String, Supplier> manufacturerInfos =  assetFileImportCacheService.findAllSupplier(assetManufacturers, selectedOrg.getTenantUID());
        return manufacturerInfos;
    }

    public Map<String, Supplier> getMaintainancerCache(){
        if(null == selectedOrg){
            return Collections.EMPTY_MAP;
        }
        List<String> assetMaintainancers = new LinkedList<>();
        for (Map<String, Object> item: importAssetMap) {
            AssetInfo asset = (AssetInfo) item.get("asset");
            if(null == asset.getMaitanance()){
                continue;
            }
            assetMaintainancers.add(asset.getMaitanance());
        }
        if(assetMaintainancers.size() ==0){
            return Collections.EMPTY_MAP;
        }
        Map<String, Supplier> maintainancerInfos =  assetFileImportCacheService.findAllSupplier(assetMaintainancers, selectedOrg.getTenantUID());
        return maintainancerInfos;
    }

}
