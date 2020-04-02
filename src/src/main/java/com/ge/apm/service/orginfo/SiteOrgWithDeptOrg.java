package com.ge.apm.service.orginfo;

import com.ge.apm.domain.OrgInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class SiteOrgWithDeptOrg {

    private OrgInfo siteOrg;

    private Optional<OrgInfo> clinicalDeptOrg;

    private Optional<OrgInfo> assetDeptOrg;
}
