package com.ge.apm.service.orginfo;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class OrgInfoService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    public List<OrgInfo> flattenOrgTree(List<OrgInfo> orgInfoList) {

        Map<String, TreeNode> treeMap = new LinkedHashMap<>();

        for(OrgInfo org: orgInfoList){
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setRowKey("org_"+org.getId());
            treeMap.put(org.getUid(), node);
        }

        TreeNode rootNode = null;

        for(OrgInfo org: orgInfoList){
            TreeNode node = treeMap.get(org.getUid());

            OrgInfo current = org;
            while(current.getParentUID()!=null){
                TreeNode currentNode = treeMap.get(current.getUid());
                TreeNode parentNode = treeMap.get(current.getParentUID());
                if (parentNode == null) break;

                parentNode.getChildren().add(currentNode);
                current = (OrgInfo)parentNode.getData();
            }

            if (rootNode == null) {
                if (current.getParentUID() == null) {
                    rootNode = treeMap.get(current.getUid());
                } else if (treeMap.get(current.getParentUID()) == null){
                    rootNode = treeMap.get(current.getUid());
                }
            }
        }

        List<OrgInfo> resultList = new ArrayList<>();

        traverseWithOrgInfo(rootNode, resultList);
        return resultList;
    }

    private void traverseWithOrgInfo(TreeNode node, List<OrgInfo> itemList) {

        OrgInfo orgInfo = (OrgInfo) node.getData();

        itemList.add(orgInfo);

        if (!node.isLeaf()) {
            for (TreeNode nodeItem : node.getChildren()) {
                traverseWithOrgInfo(nodeItem, itemList);
            }
        }

    }

    @Transactional
    public List<Map> importOrg(UploadedFile file, UserAccount user) {

        List<Map> resultList = new ArrayList<>();

        try {

            TenantInfo tenantInfo = tenantInfoDao.findByuid(user.getTenantUID());

            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            int keyRow = 1;
            int dataRow = 2;

            for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {

                Map<String, String> result = new HashMap<>();

                try {
//                    ClinicalDepartment depart = new ClinicalDepartment();

                    String institutionName = ExcelDocument.getCellStringValue(rowMap.get("集团"));
                    String hospitalName = ExcelDocument.getCellStringValue(rowMap.get("医院"));
                    String siteName = ExcelDocument.getCellStringValue(rowMap.get("院区"));
                    String departName = ExcelDocument.getCellStringValue(rowMap.get("科室"));

                    if (StringUtils.isEmpty(institutionName)) break;

                    result.put("institutionName", institutionName);
                    result.put("hospitalName", hospitalName);
                    result.put("siteName", siteName);
                    result.put("departName", departName);

                    OrgInfo institution = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 1, institutionName);

                    if (institution == null) {
                        institution = OrgInfoBuilder.buildOrgInstitution(tenantInfo, institutionName);
                        orgDao.save(institution);
                        institution = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 1, institutionName);
                    }

                    OrgInfo hospital = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 2, hospitalName);

                    if (hospital == null) {
                        hospital = OrgInfoBuilder.buildOrgHospital(tenantInfo, hospitalName, institution);
                        orgDao.save(hospital);
                        hospital = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 2, hospitalName);
                    }

                    OrgInfo site = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 3, siteName);

                    if (site == null) {
                        site = OrgInfoBuilder.buildOrgSite(tenantInfo, siteName, hospital);
                        orgDao.save(site);
                        site = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 3, siteName);
                    }

                    OrgInfo depart = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 4, departName);

                    if (depart == null) {
                        depart = OrgInfoBuilder.buildOrgClinicalDept(tenantInfo, departName, site);
                        orgDao.save(depart);
                        depart = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 4, departName);
                    }


                } catch(Exception e){
                    result.put("reason", e.getMessage());
                    throw new Exception("第" + dataRow + "行数据错误" + e.getMessage());
                } finally {
                    resultList.add(result);
                }

            }//for

        } catch (Exception e) {

        }



        return resultList;
    }

}
