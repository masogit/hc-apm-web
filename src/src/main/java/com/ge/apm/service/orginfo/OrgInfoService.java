package com.ge.apm.service.orginfo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrgInfoService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private AssetInfoRepository assetInfoDao;

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
            OrgInfo tenantOrgInfo = orgDao.findByUid(tenantInfo.getUid());

            ExcelDocument doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            int keyRow = 1;
            int dataRow = 2;

            for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {

                //make tenant as default parent of clinical dept
                OrgInfo clinicalParent = tenantOrgInfo;
                int clinicalOrgLevel = 0;
                String message = "已经存在";

                Map<String, String> result = null;

                try {

                    String institutionName = ExcelDocument.getCellStringValue(rowMap.get("集团"));
                    String hospitalName = ExcelDocument.getCellStringValue(rowMap.get("医院"));
                    String siteName = ExcelDocument.getCellStringValue(rowMap.get("院区"));
                    String departName = ExcelDocument.getCellStringValue(rowMap.get("科室"));
                    String uuid = ExcelDocument.getCellStringValue(rowMap.get("UUID"));

                    if (getLastRow(institutionName, hospitalName, siteName, departName)) break;

                    result = new HashMap<>();
                    result.put("institutionName", institutionName);
                    result.put("hospitalName", hospitalName);
                    result.put("siteName", siteName);
                    result.put("departName", departName);
                    result.put("uuid", uuid);
                    result.put("excelIndex", String.valueOf(dataRow));

                    checkRowData(institutionName, hospitalName, siteName, departName);

                    if (!StringUtils.isEmpty(uuid)) {
                        //update by existing uuid
                        OrgInfo found = orgDao.findByUid(uuid);
                        if (found == null) {
                            throw new Exception("UUID不存在！");
                        }

                        //update name if depart name, site name, hospital name, institution name changed
                        if ((found.getOrgType() == 4 && !found.getName().equals(departName))
                                || (found.getOrgType() == 3 && !found.getName().equals(siteName))
                                || (found.getOrgType() == 2 && !found.getName().equals(hospitalName))
                                || (found.getOrgType() == 1 && !found.getName().equals(institutionName))
                                ) {
                            if (found.getOrgType() == 4) {
                                found.setName(departName);
                            } else if (found.getOrgType() == 3) {
                                found.setName(siteName);
                            } else if (found.getOrgType() == 2) {
                                found.setName(hospitalName);
                            } else if (found.getOrgType() == 1) {
                                found.setName(institutionName);
                            }

                            orgDao.save(found);
                            if (found.getOrgType() == 4) {
                                assetInfoDao.updateClinicalDeptName(found.getName(), found.getUid(), found.getTenantUID());
                            }
                            message = "更新成功";

                        }


                    } else {

                        //insert or update based on the institution name, hospital name, site name, clinical dept name
                        OrgInfo institution = null;
                        if (!StringUtils.isEmpty(institutionName)) {
                            institution = orgDao.findFirstByParentUIDAndName(user.getTenantUID(),institutionName);
                            if (institution == null) {
                                institution = OrgInfoBuilder.buildOrgInstitution(tenantInfo, institutionName);
                                orgDao.save(institution);
                                institution = orgDao.findFirstByParentUIDAndName(user.getTenantUID(),institutionName);
                                message = "导入成功";
                            }

                            clinicalParent = institution;
                            clinicalOrgLevel = 1;

                        }

                        OrgInfo hospital = null;
                        if (!StringUtils.isEmpty(hospitalName)) {
                            hospital = orgDao.findFirstByParentUIDAndName(institution.getUid(), hospitalName);

                            if (hospital == null) {
                                hospital = OrgInfoBuilder.buildOrgHospital(tenantInfo, hospitalName, institution);
                                orgDao.save(hospital);
                                hospital = orgDao.findFirstByParentUIDAndName(institution.getUid(), hospitalName);
                                message = "导入成功";
                            }

                            clinicalParent = hospital;
                            clinicalOrgLevel = 2;

                        }

                        OrgInfo site = null;
                        if (!StringUtils.isEmpty(siteName)) {
                            site = orgDao.findFirstByParentUIDAndName(hospital.getUid(), siteName);

                            if (site == null) {
                                site = OrgInfoBuilder.buildOrgSite(tenantInfo, siteName, hospital);
                                orgDao.save(site);
                                site = orgDao.findFirstByParentUIDAndName(hospital.getUid(), siteName);
                                message = "导入成功";
                            }

                            clinicalParent = site;
                            clinicalOrgLevel = 3;
                        }

                        if (!StringUtils.isEmpty(departName)) {
                            OrgInfo depart = orgDao.findFirstByParentUIDAndName(clinicalParent.getUid(),departName);

                            if (depart == null) {
                                depart = OrgInfoBuilder.buildOrgClinicalDept(tenantInfo, departName, clinicalParent, clinicalOrgLevel);
                                orgDao.save(depart);
                                message = "导入成功";
//                            depart = orgDao.findFirstByTenantUIDAndOrgTypeAndName(user.getTenantUID(), 4, departName);
                            }

                        }

                    }



                    result.put("status", message);

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

        if (StringUtils.isEmpty(institutionName) && !StringUtils.isEmpty(hospitalName)) {
            throw new Exception("集团不能为空!");
        }
        if (!StringUtils.isEmpty(institutionName) && institutionName.length() > 64) {
            throw new Exception("集团名称不能超过64个字符");
        }

        if (StringUtils.isEmpty(hospitalName) && !StringUtils.isEmpty(siteName)) {
            throw new Exception("医院不能为空!");
        }
        if (!StringUtils.isEmpty(hospitalName) && hospitalName.length() > 64) {
            throw new Exception("医院名称不能超过64个字符");
        }

//        if (StringUtils.isEmpty(siteName)) {
//            throw new Exception("院区不能为空!");
//        }
        if (!StringUtils.isEmpty(siteName) && siteName.length() > 64) {
            throw new Exception("院区名称不能超过64个字符");
        }

//        if (StringUtils.isEmpty(deptName)) {
//            throw new Exception("科室不能为空!");
//        }
        if (!StringUtils.isEmpty(deptName) && deptName.length() > 64) {
            throw new Exception("科室名称不能超过64个字符");
        }

    }

    public int getOrgLevelForClinicalDept(String institutionName, String hospitalName, String siteName) throws Exception {


        if (StringUtils.isEmpty(institutionName) && StringUtils.isEmpty(hospitalName) && StringUtils.isEmpty(siteName)) {
            return 0;
        }
        if (StringUtils.isEmpty(hospitalName) && StringUtils.isEmpty(siteName)) {
            return 1;
        }
        if (StringUtils.isEmpty(siteName)) {
            return 2;
        }

        return 3;

    }

    public boolean getLastRow(String institutionName, String hospitalName, String siteName, String clinicalDeptName) throws Exception {


        if (StringUtils.isEmpty(institutionName) && StringUtils.isEmpty(hospitalName)
                && StringUtils.isEmpty(siteName) && StringUtils.isEmpty(clinicalDeptName)) {
            return true;
        }

        return false;

    }

    public ExcelDocument exportOrg() throws IOException {
        UserAccount user = UserContextService.getCurrentUserAccount();

        List<OrgInfo> orgInfoList = orgDao.getByTenantUID(user.getTenantUID());

        Map<String, OrgInfo> orgInfoMap = new HashMap<>();

        orgInfoList.stream().forEach(orgInfo -> { orgInfoMap.put(orgInfo.getUid(), orgInfo); });


        List<Map<String, Object>> resultList = new ArrayList<>();


        exportOrgByParent(user.getTenantUID(), orgInfoList, orgInfoMap, resultList);

        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/orgInfo/APM_Org_Template.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        String[] columnKeys = doc.getKeyRow(0, 1);
        int startRow = 2;

        for (Map<String, Object> map: resultList) {

            doc.writeRowData(0, columnKeys, startRow, map);

            ++startRow;

        }

        return doc;

    }

    private void exportOrgByParent(String parentUid, final List<OrgInfo> orgInfoList, final Map<String, OrgInfo> orgInfoMap, final List<Map<String, Object>> resultList ) {

        List<OrgInfo> children = findOrgByParent(parentUid, orgInfoList);

        for ( OrgInfo orgInfo: children) {

            List<OrgInfo> list = findOrgByParent(orgInfo.getUid(), orgInfoList);
            if (list.size() > 0) {
                exportOrgByParent(orgInfo.getUid(), orgInfoList, orgInfoMap, resultList);

            } else {
                exportOrg(orgInfo, resultList, orgInfoMap);

            }

        }
    }

    private void exportOrg(OrgInfo orgInfo, List<Map<String, Object>> resultList, final Map<String, OrgInfo> orgInfoMap) {
        Map<String, Object> map = new HashMap<>();
        if (orgInfo.getOrgType() == 1) {
            map.put("集团", orgInfo.getName());
        } else if (orgInfo.getOrgType() == 2) {
            OrgInfo institutionLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
            map.put("集团", institutionLevelOrg.getName());
            map.put("医院", orgInfo.getName());
        } else if (orgInfo.getOrgType() == 3) {
            OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
            OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
            map.put("集团", institutionLevelOrg.getName());
            map.put("医院", hospitalLevelOrg.getName());
            map.put("院区", orgInfo.getName());
        } else if (orgInfo.getOrgType() == 4) {
            if (orgInfo.getOrgLevel() == 0) {

            } else if (orgInfo.getOrgLevel() == 1) {
                OrgInfo institutionLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
            } else if (orgInfo.getOrgLevel() == 2) {
                OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
            } else {
                OrgInfo siteLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
                OrgInfo hospitalLevelOrg = orgInfoMap.get(siteLevelOrg.getParentUID());
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
                map.put("院区", siteLevelOrg.getName());
            }

            map.put("科室", orgInfo.getName());

        }

        map.put("UUID", orgInfo.getUid());

        resultList.add(map);

    }

    private List<OrgInfo> findOrgByParent(String parentUid, final List<OrgInfo> orgInfoList) {

        List<OrgInfo> children = orgInfoList.stream().filter(orgInfo -> parentUid.equals(orgInfo.getParentUID())).collect(Collectors.toList());

        return children;

    }

}
