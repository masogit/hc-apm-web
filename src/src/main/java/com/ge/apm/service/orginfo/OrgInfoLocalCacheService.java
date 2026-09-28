package com.ge.apm.service.orginfo;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.util.*;

@Service
public class OrgInfoLocalCacheService {

    public static final String TENANT_ORG_KEY = "TENANT_ORG";

    public static final String FLATTER_ORG_ARROW = "->";

    @Autowired
    private OrgInfoRepository orgInfoDao;


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

    public SiteOrgWithDeptOrg findSiteOrgByFlatternedMap(Map orgNamesCache, String institutionName, String hospitalName, String siteName, String deptName, String assetDeptName) {
        OrgInfo siteOrg = findSiteOrgByFlatternedMap(orgNamesCache, institutionName, hospitalName, siteName);

        OrgInfo clinicalDept = null;
        if (null != deptName) {
            String clicnicalDeptUri = siteOrg.getOrgUri() + FLATTER_ORG_ARROW + deptName;
            clinicalDept = (OrgInfo)orgNamesCache.get(clicnicalDeptUri);
        }

        OrgInfo assetDept = null;
        if (null != assetDeptName) {
            String assetDeptUri = siteOrg.getOrgUri() + FLATTER_ORG_ARROW + assetDeptName;
            assetDept = (OrgInfo)orgNamesCache.get(assetDeptUri);
        }

        SiteOrgWithDeptOrg siteOrgWithDeptOrg = new SiteOrgWithDeptOrg(siteOrg, Optional.ofNullable(clinicalDept), Optional.ofNullable(assetDept));
        return siteOrgWithDeptOrg;

    }

    public OrgInfo findSiteOrgByFlatternedMap(Map orgNamesCache, String institutionName, String hospitalName, String siteName) {

        checkOrgHierachy(institutionName, hospitalName, siteName);

        if (null == institutionName) {
            //dept is directly under tenant, return them
            OrgInfo tenantOrg = (OrgInfo) orgNamesCache.get(TENANT_ORG_KEY);
            return tenantOrg;
        }

        OrgInfo institutionOrg = (OrgInfo)orgNamesCache.get(flatterOrgHierachyAsKey(institutionName));
        if (null == institutionOrg) {

            throw new IllegalArgumentException(WebUtil.getMessage("organizationNotFound") + "(" + institutionName + ")");
        }

        if (null == hospitalName) {
            //dept is under institution, return them
            return institutionOrg;
        }

        OrgInfo hospitalOrg = (OrgInfo) orgNamesCache.get(flatterOrgHierachyAsKey(institutionName, hospitalName));
        if (null == hospitalOrg) {
            throw new IllegalArgumentException(WebUtil.getMessage("organizationNotFound") + "(" + hospitalName + ")");
        }

        if (null == siteName) {
            //dept is under hospital, return them
            return hospitalOrg;
        }

        //dept is under site
        OrgInfo siteOrg = (OrgInfo) orgNamesCache.get(flatterOrgHierachyAsKey(institutionName, hospitalName, siteName));
        if (null == siteOrg) {
            throw new IllegalArgumentException(WebUtil.getMessage("organizationNotFound") + "(" + siteName + ")");
        }

        return siteOrg;

    }

    public void checkOrgHierachy(String institutionName, String hospitalName, String siteName) {
        int institution = StringUtils.isEmpty(institutionName) ? 0 : 1;
        int hospital = StringUtils.isEmpty(hospitalName) ? 0 : 1;
        int site = StringUtils.isEmpty(siteName) ? 0 : 1;

        if (institution < hospital || institution < site || hospital < site) {
            throw new IllegalArgumentException(WebUtil.getMessage("organizationHierarchyWrong")
                    + "(" + emptyStringIfNull(institutionName) + "," + emptyStringIfNull(hospitalName) + "," + emptyStringIfNull(siteName) + ")");
        }

    }

    private String emptyStringIfNull(String name) {
        if (null == name) {
            return "";
        }
        return  name;
    }

    private String flatterOrgHierachyAsKey(String ... names) {
        StringJoiner joiner = new StringJoiner(FLATTER_ORG_ARROW, FLATTER_ORG_ARROW, "");
        Arrays.stream(names).forEach(item -> joiner.add(item));
        return joiner.toString();
    }

    private Optional<OrgInfo> findDept(Map parentOrgNameCache, String deptName) {
        if (null == deptName) {
            return Optional.empty();
        }

        OrgInfo deptOrg = (OrgInfo) parentOrgNameCache.get(deptName);

        if (null == deptOrg) {
            throw new IllegalArgumentException(String.format("dept not found %s", deptName));
        }

        return Optional.of(deptOrg);
    }

}
