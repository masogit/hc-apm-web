package com.ge.apm.service.simpleparts;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.simpleparts.SimplePartsWarehouseOrgRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.simpleparts.SimplePartsWareHouseOrg;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.Sets;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: kwg
 * @date: 2020/7/13 13:51
 * @version: 1.0
 */
@Service
public class SimplePartsWarehouseOrgService {

    @Autowired
    private SimplePartsWarehouseOrgRepository partsWarehouseOrgRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Transactional(readOnly = true)
    public List<SimplePartsWareHouseOrg> findAllByPartsWarehouseId(String id) {
        return partsWarehouseOrgRepository.findAllByPartsWarehouseId(id);
    }

    @Transactional
    public void deleteByPartsWarehouseId(String id) {
        partsWarehouseOrgRepository.deleteByPartsWarehouseId(id);
    }


    @Transactional(readOnly = true)
    public TreeNode preparePartsWarehouseOrgAdd() {
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        String siteUID = userAccount.getSiteUID();
        List<OrgInfo> orgList = orgInfoRepository.getBySiteUIDOrHospitalUIDOrInstitutionUIDOrTanantUID(siteUID);
        if (orgList.size() == 0) {
            return null;
        }
        TreeNode root = new DefaultTreeNode("root", null, null);
        Map<String, TreeNode> treeMap = new LinkedHashMap<>();
        Set<String> uidSets = Sets.newHashSet();
        for (OrgInfo org : orgList) {
            if (org.getOrgType() < 4) {
                TreeNode node = new DefaultTreeNode("org", org, null);
                node.setRowKey("org_" + org.getId());
                treeMap.put(org.getUid(), node);
            }
        }
        for (Map.Entry<String, TreeNode> nodeItem : treeMap.entrySet()) {
            TreeNode node = nodeItem.getValue();
            OrgInfo org = (OrgInfo) node.getData();
            if (org.getParentUID() != null) {
                TreeNode parentNode = treeMap.get(org.getParentUID());
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }
        if (treeMap.get(siteUID) != null) {
            root.getChildren().add(treeMap.get(siteUID));
        }
        return root;
    }

}
