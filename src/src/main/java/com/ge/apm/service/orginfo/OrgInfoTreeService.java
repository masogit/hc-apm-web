package com.ge.apm.service.orginfo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrgInfoTreeService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    public TreeNode buildHierachyClinicalDept(String siteUid, List<OrgInfo> orgInfoList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = new DefaultTreeNode("root", null, null);
        rootNode.setSelectable(false);

        for(OrgInfo org: orgInfoList){
            TreeNode currentNode = treeMap.get(org.getUid());

            if (org.getParentUID().equals(siteUid)) {

                rootNode.getChildren().add(currentNode);
            } else {
                TreeNode parentNode = treeMap.get(org.getParentUID());
                parentNode.getChildren().add(currentNode);
            }

        }

        return rootNode;
    }

    public Set<String> collectChildClinicalDept(String parentUid) {
        Set<String> orgUidSet = new HashSet<>();
        collectChildClinicalDept(parentUid, orgUidSet);
        return orgUidSet;
    }

    public void collectChildClinicalDept(String parentUid, Set<String> clinicalDeptSet) {

        clinicalDeptSet.add(parentUid);

        List<OrgInfo> orgInfos = orgDao.findByParentUID(parentUid);

        for (OrgInfo orgInfo: orgInfos) {
            collectChildClinicalDept(orgInfo.getUid(), clinicalDeptSet);
        }

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
