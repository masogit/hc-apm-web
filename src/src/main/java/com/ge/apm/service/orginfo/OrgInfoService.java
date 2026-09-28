package com.ge.apm.service.orginfo;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.OrgInfoMapper;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SystemTemplate;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

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
    private OrgInfoMapper orgInfoMapper;

    @Autowired
    private AssetInfoRepository assetInfoDao;

    @Autowired
    private ConfigCacheService configCacheService;

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
        TenantInfo tenantInfo = configCacheService.getTenantByUid(user.getTenantUID());
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
                String institutionEn = ExcelDocument.getCellStringValue(rowMap.get("集团名称En"));
                String hospitalName = ExcelDocument.getCellStringValue(rowMap.get("医院"));
                String hospitalEn = ExcelDocument.getCellStringValue(rowMap.get("医院名称En"));
                String siteName = ExcelDocument.getCellStringValue(rowMap.get("院区"));
                String siteEn = ExcelDocument.getCellStringValue(rowMap.get("院区名称En"));
                String departName = ExcelDocument.getCellStringValue(rowMap.get("科室"));
                String departEn = ExcelDocument.getCellStringValue(rowMap.get("科室名称En"));
                String uuid = ExcelDocument.getCellStringValue(rowMap.get("UUID"));
                String ucmid = ExcelDocument.getCellStringValue(rowMap.get("UCMID"));
                String buildingNum = ExcelDocument.getCellStringValue(rowMap.get("楼号"));
                String floorNum = ExcelDocument.getCellStringValue(rowMap.get("层数"));

                if (getLastRow(institutionName, hospitalName, siteName, departName, uuid)) break;

                result = new HashMap<>();
                result.put("institutionName", institutionName);
                result.put("institutionEn", institutionEn);
                result.put("hospitalName", hospitalName);
                result.put("hospitalEn", hospitalEn);
                result.put("siteName", siteName);
                result.put("siteEn", siteEn);
                result.put("departName", departName);
                result.put("departEn", departEn);
                result.put("uuid", uuid);
                result.put("ucmid", ucmid);
                result.put("buildingNum", buildingNum);
                result.put("floorNum", floorNum);
                result.put("excelIndex", String.valueOf(dataRow));

                checkRowData(institutionName, hospitalName, siteName, departName);

                if (!StringUtils.isEmpty(uuid)) {
                    //update by existing uuid
                    OrgInfo found = orgDao.findByUid(uuid);
                    if (found == null) {
                        throw new Exception("该组织机构不存在！");
                    }

                    //update name if depart name, site name, hospital name, institution name changed
                    String name = null;
                    String nameEn = null;
                    if (Objects.equals(found.getOrgType(), 4)) {
                        if (StringUtils.isEmpty(departName)) {
                            throw new Exception("科室名称不能为空！");
                        }
                        name = departName;
                        nameEn = departEn;
                    } else if (Objects.equals(found.getOrgType(), 3)) {
                        if (StringUtils.isEmpty(siteName)) {
                            throw new Exception("院区名称不能为空！");
                        }
                        name = siteName;
                        nameEn = siteEn;
                    } else if (Objects.equals(found.getOrgType(), 2)) {
                        if (StringUtils.isEmpty(hospitalName)) {
                            throw new Exception("医院名称不能为空！");
                        }
                        name = hospitalName;
                        nameEn = hospitalEn;
                    } else if (Objects.equals(found.getOrgType(), 1)) {
                        if (StringUtils.isEmpty(institutionName)) {
                            throw new Exception("集团名称不能为空！");
                        }
                        name = institutionName;
                        nameEn = institutionEn;
                    }

                    OrgInfo sameNameOrg = orgDao.findFirstByParentUIDAndNameAndUidIsNot(found.getParentUID(), name, found.getUid());
                    if(sameNameOrg != null){
                        throw new Exception(name + "已经存在!");
                    }
                    boolean isSame = true;
                    if(!found.getName().equals(name)){
                        isSame = false;
                    }else if(!Objects.equals(found.getNameEn(), nameEn)) {
                        isSame = false;
                    }else if(Objects.equals(found.getOrgType(), 2)){
                        if(!Objects.equals(found.getHospitalUcmid(), ucmid)) {
                            isSame = false;
                        }
                    }else if(Objects.equals(found.getOrgType(), 4)){
                        if(!Objects.equals(found.getBuildingNum(), buildingNum)) {
                            isSame = false;
                        } else if(!Objects.equals(found.getFloorNum(), floorNum)){
                            isSame = false;
                        }
                    }
                    if (!isSame){
                        found.setShortName(name);
                        found.setName(name);
                        found.setNameEn(nameEn);
                        if(Objects.equals(found.getOrgType(), 4 )){
                            found.setBuildingNum(buildingNum);
                            found.setFloorNum(floorNum);
                        }

                        if (!StringUtils.isEmpty(ucmid)) {
                            OrgInfo sameUcmidOrg = orgDao.findByHospitalUcmid(ucmid);
                            if (sameUcmidOrg != null && !sameUcmidOrg.getUid().equals(found.getUid())) {
                                throw new Exception(ucmid + "重复");
                            }
                        }
                        found.setHospitalUcmid(ucmid);

                        orgDao.save(found);
                        configCacheService.cleanOrgInfoCacheByUid(found.getUid());
                        if (Objects.equals(found.getOrgType(), 4)) {
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
                            institution = OrgInfoBuilder.buildOrgInstitution(tenantInfo, tenantOrgInfo, institutionName);
                            institution.setNameEn(institutionEn);
                            orgDao.save(institution);
                            configCacheService.cleanOrgInfoCacheByUid(institution.getUid());
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
                            hospital.setNameEn(hospitalEn);
                            if (!StringUtils.isEmpty(ucmid)) {
                                OrgInfo sameUcmidOrg = orgDao.findByHospitalUcmid(ucmid);
                                if (sameUcmidOrg != null && sameUcmidOrg.getName()!= hospitalName) {
                                    throw new Exception(ucmid + "重复");
                                }
                            }
                            hospital.setHospitalUcmid(ucmid);
                            orgDao.save(hospital);
                            configCacheService.cleanOrgInfoCacheByUid(hospital.getUid());
                            hospital = orgDao.findFirstByParentUIDAndName(institution.getUid(), hospitalName);
                            message = "导入成功";
                        }

                        clinicalParent = hospital;
                        clinicalOrgLevel = 2;
                    }

                    OrgInfo site = null;
                    if (!StringUtils.isEmpty(siteName)) {
                        site = orgDao.findFirstByParentUIDAndName(hospital.getUid(), siteName);
                        site.setNameEn(siteEn);
                        if (site == null) {
                            site = OrgInfoBuilder.buildOrgSite(tenantInfo, siteName, hospital);
                            orgDao.save(site);
                            configCacheService.cleanOrgInfoCacheByUid(site.getUid());
                            site = orgDao.findFirstByParentUIDAndName(hospital.getUid(), siteName);
                            message = "导入成功";
                        }

                        clinicalParent = site;
                        clinicalOrgLevel = 3;
                    }

                    if (!StringUtils.isEmpty(departName)) {
                        if (departName.contains("-")) {
                            String[] departNames = departName.split("-");
                            for (String childDepartName: departNames) {
                                //if there is child clinical dept name, then we need to use short name, because normal name used with concatination of all clinical depts
                                OrgInfo depart = orgDao.findFirstByParentUIDAndShortName(clinicalParent.getUid(),childDepartName);
                                if (depart == null) {
                                    depart = OrgInfoBuilder.buildOrgClinicalDept(tenantInfo, childDepartName, clinicalParent, clinicalOrgLevel);
                                    depart.setBuildingNum(buildingNum);
                                    depart.setFloorNum(floorNum);
                                    depart.setNameEn(departEn);
                                    orgDao.save(depart);
                                    configCacheService.cleanOrgInfoCacheByUid(depart.getUid());
                                    message = "导入成功";
                                }
                                clinicalParent = depart;
                            }
                        } else {
                            OrgInfo depart = orgDao.findFirstByParentUIDAndName(clinicalParent.getUid(),departName);
                            if (depart == null) {
                                depart = OrgInfoBuilder.buildOrgClinicalDept(tenantInfo, departName, clinicalParent, clinicalOrgLevel);
                                depart.setBuildingNum(buildingNum);
                                depart.setFloorNum(floorNum);
                                depart.setNameEn(departEn);
                                orgDao.save(depart);
                                configCacheService.cleanOrgInfoCacheByUid(depart.getUid());
                                message = "导入成功";
                            }
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
        }
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

    private boolean getLastRow(String institutionName, String hospitalName, String siteName, String clinicalDeptName, String uuid) throws Exception {


        if (StringUtils.isEmpty(institutionName) && StringUtils.isEmpty(hospitalName)
                && StringUtils.isEmpty(siteName) && StringUtils.isEmpty(clinicalDeptName)
                && StringUtils.isEmpty(uuid)) {
            return true;
        }

        return false;

    }

    public ExcelDocument exportOrg() throws IOException {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<Map<String, Object>> resultList = new ArrayList<>();
//        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/orgInfo/APM_Org_Template.xlsx");
        FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        InputStream  stream=fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.ORG_INFO.getCode(), SystemTemplateEnum.ORG_INFO.getFileType());
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        String[] columnKeys = doc.getKeyRow(0, 1);
        int startRow = 2;
        if(Objects.equals(user.getSiteId(), 46)){
            //1.获取对应住户下的所有集团
            List<OrgInfo> orginfoInstitutionlist = orgDao.getHospitalByTenantUIDAndType(user.getTenantUID());
            OrgInfo parent = orgDao.getOrgInfoByUid(user.getTenantUID());
            getPagingData1(orginfoInstitutionlist,5, parent,columnKeys,startRow,doc);
        }else{
            List<OrgInfo> orgInfoList = orgDao.getByTenantUID(user.getTenantUID());

            Map<String, OrgInfo> orgInfoMap = new HashMap<>();

            orgInfoList.stream().forEach(orgInfo -> { orgInfoMap.put(orgInfo.getUid(), orgInfo); });

            exportOrgByParent(user.getTenantUID(), orgInfoList, orgInfoMap, resultList);

            //remove fake data row in template
            doc.clearRow(startRow);
            for (Map<String, Object> map: resultList) {
                doc.writeRowData(0, columnKeys, startRow, map);
                ++startRow;
            }
        }
        return doc;


    }

    private void getPagingData1( List<OrgInfo> orginfoInstitutionlist,int pageSize,OrgInfo parent,String[] columnKeys,int startRow,ExcelDocument doc) {
        List<String> uids = new ArrayList();

        for (int i = 0; i <orginfoInstitutionlist.size() ; i++) {
            uids.add(orginfoInstitutionlist.get(i).getUid());//每次循环收集要查询的uid,到与pageSize一样大小的时候统一执行。
            if(uids.size()%pageSize==0){//如果到了分页的次数就统一执行一次
                List<Map<String, Object>> resultList2 = new ArrayList<>();
                //2.通过集团信息向下递归出所有的子集 orginfoInstitutionlist.uid
                List<OrgInfo> orgHospitalChildrenlist = orgDao.getHospitalChildrenByUids(uids);
                //查询出顶级目录为了能让集团变为顶级的儿子
                orgHospitalChildrenlist.add(parent);
                Map<String, OrgInfo> orgInfoMap2 = new HashMap<>();
                orgHospitalChildrenlist.stream().forEach(orgInfo -> {
                    orgInfoMap2.put(orgInfo.getUid(), orgInfo);
                });
                exportOrgByParent(parent.getUid(), orgHospitalChildrenlist, orgInfoMap2, resultList2);
                uids = new ArrayList();//为下次查询清空uids
                for (Map<String, Object> map: resultList2) {
                    doc.writeRowData(0, columnKeys, startRow, map);
                    ++startRow;
                }
            }
            if((i+1)==orginfoInstitutionlist.size() && orginfoInstitutionlist.size()%pageSize!=0){//如果遍历到了最后一次则把剩下的执行完毕。
                List<Map<String, Object>> resultList2 = new ArrayList<>();
                //2.通过集团信息向下递归出所有的子集 orginfoInstitutionlist.uid
                List<OrgInfo> orgHospitalChildrenlist = orgDao.getHospitalChildrenByUids(uids);
                //查询出顶级目录为了能让集团变为顶级的儿子
                orgHospitalChildrenlist.add(parent);
                Map<String, OrgInfo> orgInfoMap2 = new HashMap<>();
                orgHospitalChildrenlist.stream().forEach(orgInfo -> {
                    orgInfoMap2.put(orgInfo.getUid(), orgInfo);
                });
                exportOrgByParent(parent.getUid(), orgHospitalChildrenlist, orgInfoMap2, resultList2);
                uids = new ArrayList();//为下次查询清空uids
                for (Map<String, Object> map: resultList2) {
                    doc.writeRowData(0, columnKeys, startRow, map);
                    ++startRow;
                }
            }
        }
    }

    private List<String> getPagingData(List<Map<String, Object>> resultList, List<OrgInfo> orginfoInstitutionlist, List<String> uids, int i,OrgInfo parent) {
        List<Map<String, Object>> resultList2 = new ArrayList<>();
        //2.通过集团信息向下递归出所有的子集 orginfoInstitutionlist.uid
        List<OrgInfo> orgHospitalChildrenlist = orgDao.getHospitalChildrenByUids(uids);
        //查询出顶级目录为了能让集团变为顶级的儿子
        orgHospitalChildrenlist.add(parent);
        Map<String, OrgInfo> orgInfoMap2 = new HashMap<>();
        orgHospitalChildrenlist.stream().forEach(orgInfo -> {
            orgInfoMap2.put(orgInfo.getUid(), orgInfo);
        });
        exportOrgByParent(parent.getUid(), orgHospitalChildrenlist, orgInfoMap2, resultList2);
        resultList.addAll(resultList2);
        uids = new ArrayList();//为下次查询清空uids
        return uids;
    }

    private void exportOrgByParent(String parentUid, final List<OrgInfo> orgInfoList, final Map<String, OrgInfo> orgInfoMap, final List<Map<String, Object>> resultList ) {

        List<OrgInfo> children = findOrgByParent(parentUid, orgInfoList);

        for ( OrgInfo orgInfo: children) {

            List<OrgInfo> list = findOrgByParent(orgInfo.getUid(), orgInfoList);
            if (list.size() > 0) {
                exportOrgByParent(orgInfo.getUid(), orgInfoList, orgInfoMap, resultList);
            }
            exportOrg(orgInfo, resultList, orgInfoMap);
        }
    }
    
    //为科室寻找商机医院(院区 or 医院 or 集团), 考虑多级科室的情形，需要向上寻找到院区
    private OrgInfo getParentHospitalForDepartment(OrgInfo dept, final Map<String, OrgInfo> orgInfoMap) {
        OrgInfo parentOrg = orgInfoMap.get(dept.getParentUID());
        while(parentOrg!=null){ //如果是多级科室，需要向上寻找到院区
            if(parentOrg.getOrgType()<=3) break;
            parentOrg = orgInfoMap.get(parentOrg.getParentUID()); 
        }
        if(parentOrg==null) 
            parentOrg = dept;  //如果没有找到，则返回本身。
        
        return parentOrg;
    }

    private void exportOrg(OrgInfo orgInfo, List<Map<String, Object>> resultList, final Map<String, OrgInfo> orgInfoMap) {
        Map<String, Object> map = new HashMap<>();
        if (Objects.equals(orgInfo.getOrgType(), 1)) {
            map.put("集团", orgInfo.getName());
            map.put("集团名称En", orgInfo.getNameEn());
        } else if (Objects.equals(orgInfo.getOrgType(), 2)) {
            OrgInfo institutionLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
            map.put("集团", institutionLevelOrg.getName());
            map.put("集团名称En", institutionLevelOrg.getNameEn());
            map.put("医院", orgInfo.getName());
            map.put("医院名称En", orgInfo.getNameEn());
        } else if (Objects.equals(orgInfo.getOrgType(), 3)) {
            OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
            OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
            map.put("集团", institutionLevelOrg.getName());
            map.put("集团名称En", institutionLevelOrg.getNameEn());
            map.put("医院", hospitalLevelOrg.getName());
            map.put("医院名称En", hospitalLevelOrg.getNameEn());
            map.put("院区", orgInfo.getName());
            map.put("院区名称En", orgInfo.getNameEn());
        } else if (Objects.equals(orgInfo.getOrgType(), 4)) {
            if (Objects.equals(orgInfo.getOrgLevel(), 0)) {

            } else if (Objects.equals(orgInfo.getOrgLevel(), 1)) {
                OrgInfo institutionLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                map.put("集团", institutionLevelOrg.getName());
                map.put("集团名称En", institutionLevelOrg.getNameEn());
            } else if (Objects.equals(orgInfo.getOrgLevel(), 2)) {
                OrgInfo hospitalLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("集团名称En", institutionLevelOrg.getNameEn());
                map.put("医院", hospitalLevelOrg.getName());
                map.put("医院名称En", hospitalLevelOrg.getNameEn());
            } else {
                OrgInfo siteLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                OrgInfo hospitalLevelOrg = orgInfoMap.get(siteLevelOrg.getParentUID());
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("集团名称En", institutionLevelOrg.getNameEn());
                map.put("医院", hospitalLevelOrg.getName());
                map.put("医院名称En", hospitalLevelOrg.getNameEn());
                map.put("院区", siteLevelOrg.getName());
                map.put("院区名称En", siteLevelOrg.getNameEn());
            }

            map.put("科室", orgInfo.getName());
            map.put("科室名称En", orgInfo.getNameEn());
            map.put("楼号", orgInfo.getBuildingNum());
            map.put("层数", orgInfo.getFloorNum());
        }

        map.put("UUID", orgInfo.getUid());

        map.put("UCMID", orgInfo.getHospitalUcmid());

        resultList.add(map);

    }

    private List<OrgInfo> findOrgByParent(String parentUid, final List<OrgInfo> orgInfoList) {

        List<OrgInfo> children = orgInfoList.stream().filter(orgInfo -> parentUid.equals(orgInfo.getParentUID())).collect(Collectors.toList());

        return children;

    }

    public String buildFullPathOrgName(OrgInfo currentOrg) {
        if (currentOrg.getOrgType() < 4)  {
            return currentOrg.getShortName();
        }

        //the current org maybe is a new org, new persisted in the database, so use its parent org in db.
        List<OrgInfo> orgInfos = orgInfoMapper.listParentClinicalDeptsWithSelectedClinicalDept(currentOrg.getParentUID());

        //the most far away ancestor is the last record, reverse to make it the first
        Collections.reverse(orgInfos);

        //add the current org, it is the lowest level
        orgInfos.add(currentOrg);

        return orgInfos.stream().map(OrgInfo::getShortName).collect(Collectors.joining("-"));
    }

    public int getClinicalLevel(OrgInfo currentOrg) {
        if (!Objects.equals(currentOrg.getOrgType(), 4)) {
            return 0;
        }

        List<OrgInfo> orgInfos = orgInfoMapper.listParentClinicalDeptsWithSelectedClinicalDept(currentOrg.getUid());

        return orgInfos.size();
    }

    public List<OrgInfo> getHospitalList(String orgLevel) {
        List<OrgInfo> result = null;
        UserAccount user = UserContextService.getCurrentUserAccount();
        boolean insulate = UserContextService.isInsulate();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(orgLevel)) {
            int level = Integer.parseInt(orgLevel);
            Integer userLevel = user.getOrgLevel() == null ? 0 : user.getOrgLevel();
            List<SearchFilter> searchFilters = new ArrayList<>(5);
            if (!insulate) {
                if (0 == level) {
                    searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                    searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
                } else if (1 == level) {
                    searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                    searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
                } else if (2 == level) {
                    searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                    searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
                } else if (3 == level) {
                    searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                    searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
                }
            } else {
                switch (userLevel) {
                    case 0:
                        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                        if (0 == level) {
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
                        } else if (1 == level) {
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
                        } else if (2 == level) {
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
                        } else if (3 == level) {
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
                        }
                        break;
                    case 1:
                        if (0 == level) {
                            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
                        } else if (1 == level) {
                            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
                        } else if (2 == level) {
                            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
                        } else if (3 == level) {
                            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
                        }
                        break;
                    case 2:
                        if (0 == level) {
                            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
                        } else if (1 == level) {
                            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
                        } else if (2 == level) {
                            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
                        } else if (3 == level) {
                            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
                        }
                        break;
                    default:
                        if (0 == level) {
                            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
                        } else if (1 == level) {
                            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
                        } else if (2 == level) {
                            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
                        } else if (3 == level) {
                            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
                            searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
                        }
                        break;
                }
            }
            result = orgDao.findBySearchFilter(searchFilters);
        }
        return result;
    }

    public String getOrgNameById(Integer orgId) {
        OrgInfo oi = orgDao.findById(orgId);
        if (oi == null) {
            return null;
        }
        return oi.getName();
    }

    public String getInsNameByUId(String uid) {
        OrgInfo oi = orgDao.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 1)) {
            return oi.getName();
        }
        return null;
    }

    public String getHospitalNameByUId(String uid) {
        OrgInfo oi = orgDao.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 2)) {
            return oi.getName();
        }
        return null;
    }

    public String getSiteNameByUId(String uid) {
        OrgInfo oi = orgDao.getByUid(uid);
        if (oi == null) {
            return null;
        }
        if (Objects.equals(oi.getOrgLevel(), 3)) {
            return oi.getName();
        }
        return null;
    }

    public String getTenantName(String tenantUID) {
        return configCacheService.getTenantByUid(tenantUID).getName();
    }

    public static void main(String[] args) {
        System.out.println(Math.ceil(Double.valueOf(2000)/Double.valueOf(500)));
    }

}
