package com.ge.apm.service.orginfo;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class OrgInfoClinicalDeptService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;

    public List<OrgInfo> collectChildClinicalDept(OrgInfo parentOrg) {
        List<OrgInfo> orgInfoSet = new ArrayList<>();
        collectChildClinicalDept(parentOrg, orgInfoSet);
        return orgInfoSet;
    }

    public void collectChildClinicalDept(OrgInfo parentOrg, List<OrgInfo> orgInfoSet) {

        orgInfoSet.add(parentOrg);

        List<OrgInfo> orgInfos = orgDao.findByParentUID(parentOrg.getUid());

        for (OrgInfo orgInfo: orgInfos) {
            collectChildClinicalDept(orgInfo, orgInfoSet);
        }

    }



}
