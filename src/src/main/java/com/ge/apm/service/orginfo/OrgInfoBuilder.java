package com.ge.apm.service.orginfo;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrgInfoBuilder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static OrgInfo buildOrgInstitution(TenantInfo tenantInfo, String name) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(1);
        orgInfo.setOrgType(1);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setHospitalId(null);

        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(orgInfo.getUid());
        orgInfo.setHospitalUID(orgInfo.getUid());
        orgInfo.setSiteUID(orgInfo.getUid());

        orgInfo.setParentUID(tenantInfo.getUid());

        return orgInfo;
    }

    public static OrgInfo buildOrgHospital(TenantInfo tenantInfo, String name, OrgInfo parentOrg) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(2);
        orgInfo.setOrgType(2);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setHospitalId(parentOrg.getId());

        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(parentOrg.getUid());
        orgInfo.setHospitalUID(orgInfo.getUid());
        orgInfo.setSiteUID(orgInfo.getUid());

        orgInfo.setParentUID(parentOrg.getUid());

        return orgInfo;
    }

    public static OrgInfo buildOrgSite(TenantInfo tenantInfo, String name, OrgInfo parentOrg) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(3);
        orgInfo.setOrgType(3);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setHospitalId(parentOrg.getId());

        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(parentOrg.getUid());
        orgInfo.setHospitalUID(parentOrg.getUid());
        orgInfo.setSiteUID(orgInfo.getUid());

        orgInfo.setParentUID(parentOrg.getUid());

        return orgInfo;
    }

    public static OrgInfo buildOrgClinicalDept(TenantInfo tenantInfo, String name, OrgInfo parentOrg) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(4);
        orgInfo.setOrgType(4);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setHospitalId(parentOrg.getId());

        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(parentOrg.getUid());
        orgInfo.setHospitalUID(parentOrg.getUid());
        orgInfo.setSiteUID(parentOrg.getUid());

        orgInfo.setParentUID(parentOrg.getUid());

        return orgInfo;
    }

}
