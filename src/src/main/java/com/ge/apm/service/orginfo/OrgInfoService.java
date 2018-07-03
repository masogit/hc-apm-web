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

import java.io.IOException;
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
    public List<Map> importOrg(UploadedFile file, UserAccount user) throws IOException {

        List<Map> resultList = new ArrayList<>();

//        try {

            TenantInfo tenantInfo = tenantInfoDao.findByuid(user.getTenantUID());

            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            int keyRow = 1;
            int dataRow = 2;

            for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {

                Map<String, String> result = null;

                try {
//                    ClinicalDepartment depart = new ClinicalDepartment();

                    String institutionName = ExcelDocument.getCellStringValue(rowMap.get("集团"));
                    String hospitalName = ExcelDocument.getCellStringValue(rowMap.get("医院"));
                    String siteName = ExcelDocument.getCellStringValue(rowMap.get("院区"));
                    String departName = ExcelDocument.getCellStringValue(rowMap.get("科室"));

                    if (StringUtils.isEmpty(institutionName)) break;

                    result = new HashMap<>();
                    result.put("institutionName", institutionName);
                    result.put("hospitalName", hospitalName);
                    result.put("siteName", siteName);
                    result.put("departName", departName);
                    result.put("excelIdex", String.valueOf(dataRow));

                    checkRowData(institutionName, hospitalName, siteName, departName);



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


                    result.put("status", "导入成功");

                } catch(Exception e){
                    result.put("status", e.getMessage());
                } finally {
                    if (result != null) {
                        resultList.add(result);
                    }

                }

            }//for

//        } catch (Exception e) {
//
//        }



        return resultList;
    }

    public void checkRowData(String institutionName, String hospitalName, String siteName, String deptName) throws Exception {

        if (StringUtils.isEmpty(institutionName)) {
            throw new Exception("集团不能为空!");
        }
        if (institutionName.length() > 64) {
            throw new Exception("集团名称不能超过64个字符");
        }

        if (StringUtils.isEmpty(hospitalName)) {
            throw new Exception("医院不能为空!");
        }
        if (hospitalName.length() > 64) {
            throw new Exception("医院名称不能超过64个字符");
        }

        if (StringUtils.isEmpty(siteName)) {
            throw new Exception("院区不能为空!");
        }
        if (siteName.length() > 64) {
            throw new Exception("院区名称不能超过64个字符");
        }

        if (StringUtils.isEmpty(deptName)) {
            throw new Exception("科室不能为空!");
        }
        if (deptName.length() > 64) {
            throw new Exception("科室名称不能超过64个字符");
        }

    }

}
