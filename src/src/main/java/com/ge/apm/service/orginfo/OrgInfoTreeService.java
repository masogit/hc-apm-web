package com.ge.apm.service.orginfo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.mapper.OrgInfoMapper;
import com.ge.apm.domain.OrgInfo;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrgInfoTreeService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private OrgInfoMapper orgInfoMapper;

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    public Set<String> collectChildClinicalDept(String parentUid) {
        List<OrgInfo> orgInfos = orgInfoMapper.listOrgHierarchyUnderCurrentOrg(parentUid);

        Set<String> orgUids = orgInfos.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        return orgUids;
    }

    public Set<OrgInfo> collectChildClinicalDeptInExstingList(List<OrgInfo> orgInfosMatchingByName, List<OrgInfo> clinicalDeptList) {

        Set<OrgInfo> clinicalDeptSet = new HashSet<>();

        for (OrgInfo orgInfo: orgInfosMatchingByName) {
            collectChildClinicalDeptInExstingList(orgInfo, clinicalDeptList, clinicalDeptSet);
        }

        return clinicalDeptSet;
    }

    public void collectChildClinicalDeptInExstingList(OrgInfo parentOrg, List<OrgInfo> clinicalDeptList, Set<OrgInfo> clinicalDeptSetOutput) {

        clinicalDeptSetOutput.add(parentOrg);

        for (OrgInfo orgInfo: clinicalDeptList) {
            if (!orgInfo.getParentUID().equals(parentOrg.getUid())) continue;
            collectChildClinicalDeptInExstingList(orgInfo, clinicalDeptList, clinicalDeptSetOutput);

        }


    }


    public List<OrgInfo> collectChildClinicalDeptByParent(TreeNode parent) {

        List<OrgInfo> clinicalDeptList = new ArrayList<>();

        collectChildClinicalDeptByParent(parent, clinicalDeptList);

        return clinicalDeptList;
    }

    public void collectChildClinicalDeptByParent(TreeNode parent, List<OrgInfo> clinicalDeptList) {

        OrgInfo parentOrg = (OrgInfo)parent.getData();
        clinicalDeptList.add(parentOrg);

        for (TreeNode child: parent.getChildren()) {
            collectChildClinicalDeptByParent(child, clinicalDeptList);
        }
    }


}
