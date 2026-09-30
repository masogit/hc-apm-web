package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetFileImportCacheService {

    @Autowired
    private OrgInfoRepository orgInfoDao;

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private AssetInfoMapper assetInfoMapper;


    public static final String TENANT_ORG_KEY = "TENANT_ORG";


    public Map<String, OrgInfo> flatternLocalOrgNamesCache(String tenantUid) {

        Map<String, OrgInfo> result = new HashMap();

        List<Object[]> orgInfosArr = orgInfoDao.getOrgHierarchyForTenant(tenantUid);
        List<OrgInfo> orgInfos = convertOrgInfo(orgInfosArr);
        orgInfos.forEach(item -> {
            if (Objects.equals(item.getOrgType(), 0)) {
                result.put(TENANT_ORG_KEY, item);
            } else {
                result.put(item.getOrgUri(), item);
            }
        });

        return result;
    }


    public Map<String, OrgInfo> allCilinicalOfSiteUid(String siteUid){

        Map<String, OrgInfo> result =  new HashMap<>();
        List<OrgInfo> clinicals = orgInfoDao.findByParentUIDAndOrgType(siteUid, 4);
        clinicals.stream().forEach(item -> result.put(item.getName(), item));
        return  result;
    }

    public Map<String, AssetInfo> findDepartNumInAssetInfo(List<String> departsNums){

        Map<String, AssetInfo> result = new HashMap();
        List<AssetInfo> clinicals = assetInfoDao.findAllByDepartNumIn(departsNums);
        clinicals.stream().forEach(item -> result.put(item.getDepartNum(), item));
        return  result;
    }

    public Multimap<String, UserAccount> findAllAssetOwnerName(List<String> nameLists, String tenantUid) {

        Multimap<String, UserAccount> result = HashMultimap.create();
        List<UserAccount> userAccounts = userAccountMapper.getUserAccountByNameAndTenantUid(nameLists, tenantUid);
        userAccounts.stream().forEach(item -> result.put(item.getName(), item));
        if(result.isEmpty()){
            return result;
        }
        return result;

    }

    public Multimap<String, UserAccount> findAllAssetOwnerName2(List<String> nameLists, String tenantUid) {

        Multimap<String, UserAccount> result = ArrayListMultimap.create();
        List<UserAccount> userAccounts = userAccountMapper.getUserAccountByNameAndTenantUid(nameLists, tenantUid);
        userAccounts.stream().forEach(item -> result.put(item.getName(), item));
        if(result.isEmpty()){
            return result;
        }
        return result;

    }

    public Multimap<String, UserAccount> findAllAssetOwnerName3(List<String> nameLists, String tenantUid) {

        Multimap<String, UserAccount> result = HashMultimap.create();
        List<UserAccount> userAccounts = userAccountMapper.getUserAccountByNameAndTenantUid(nameLists, tenantUid);
        userAccounts.stream().forEach(item -> result.put(item.getName(), item));
        if(result.isEmpty()){
            return result;
        }
        return result;

    }

    public Multimap<String, UserAccount> findAllClinicalOwnerName(List<String> nameLists, String tenantUid) {

        Multimap<String, UserAccount> result = HashMultimap.create();
        List<UserAccount> userAccounts = userAccountMapper.getUserAccountByNameAndTenantUid(nameLists, tenantUid);
        userAccounts.stream().forEach(item -> result.put(item.getName(), item));
        if(result.isEmpty()){
            return result;
        }
        return result;

    }

    public Multimap<String, UserAccount> findAllPmOwnerName(List<String> nameLists, String tenantUid) {

        Multimap<String, UserAccount> result = HashMultimap.create();
        List<UserAccount> userAccounts = userAccountMapper.getUserAccountByNameAndTenantUid(nameLists, tenantUid);
        userAccounts.stream().forEach(item -> result.put(item.getName(), item));
        if(result.isEmpty()){
            return result;
        }
        return result;

    }

    public Map<String, Supplier> findAllSupplier(List<String> assetSuppliers, String tenantUID) {
        Map<String, Supplier> result = new HashMap();
        List<Supplier> supplier = assetInfoMapper.getSupliersBySuplierNamesAndTenantUID(assetSuppliers, tenantUID);
        supplier.stream().forEach(item -> result.put(item.getName(), item));
        if(result.isEmpty()){
            return result;
        }
        return result;
    }


    private List<OrgInfo> convertOrgInfo(List<Object[]> orgInfosArr) {
        List<OrgInfo> orgInfos = new ArrayList<>();
        for (Object[] objArr: orgInfosArr) {
            int index = 0;
            OrgInfo orgInfo = new OrgInfo();

            orgInfo.setId((Integer)objArr[index++]);
            orgInfo.setUid((String)objArr[index++]);

            orgInfo.setShortName((String)objArr[index++]);
            orgInfo.setName((String)objArr[index++]);

            orgInfo.setParentID((Integer)objArr[index++]);
            orgInfo.setParentUID((String)objArr[index++]);

            orgInfo.setOrgLevel((Integer)objArr[index++]);
            orgInfo.setOrgType((Integer)objArr[index++]);

            orgInfo.setSiteUID((String)objArr[index++]);
            orgInfo.setHospitalUID((String)objArr[index++]);
            orgInfo.setInstitutionUID((String)objArr[index++]);
            orgInfo.setTenantUID((String)objArr[index++]);

            orgInfo.setSiteId((Integer)objArr[index++]);

            orgInfo.setOrgUri((String)objArr[index++]);

            orgInfos.add(orgInfo);
        }

        return orgInfos;
    }
}
