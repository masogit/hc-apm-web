package com.ge.apm.service.orginfo;

import com.ge.apm.dao.BuildManagementRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.BuildManagement;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.dto.BuildManagementExportDto;
import com.ge.apm.service.EasyExcelExportUtil;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BuildManagementService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private BuildManagementRepository buildManagementRepository;

    public ExcelDocument exportOrg() throws IOException {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<Map<String, Object>> resultList = new ArrayList<>();

        FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);

        InputStream stream = fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.BUILD_MANAGEMENT.getCode(), SystemTemplateEnum.BUILD_MANAGEMENT.getFileType());
//        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/orgInfo/APM_Building_Template.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        String[] columnKeys = doc.getKeyRow(0, 1);
        int startRow = 2;
        if (Objects.equals(user.getSiteId(), 46)) {
            //1.获取对应住户下的所有集团
            List<OrgInfo> orginfoInstitutionlist = orgDao.getHospitalByTenantUIDAndType(user.getTenantUID());
            OrgInfo parent = orgDao.getOrgInfoByUid(user.getTenantUID());
            getPagingData1(orginfoInstitutionlist, 5, parent, columnKeys, startRow, doc);
        } else {
            List<OrgInfo> orgInfoList = orgDao.getByTenantUIDAndOrgTypeNotEq(user.getTenantUID(), 4);

            Map<String, OrgInfo> orgInfoMap = new HashMap<>();

            orgInfoList.stream().forEach(orgInfo -> {
                orgInfoMap.put(orgInfo.getUid(), orgInfo);
            });

            List<BuildManagement> buildManagementList = buildManagementRepository.findByTenantUID(user.getTenantUID());
            Map<Integer, BuildManagement> buildManagementMap = new HashMap<>(buildManagementList.size());
            buildManagementList.stream().forEach(buildManagement -> {
                buildManagementMap.put(buildManagement.getId(), buildManagement);
            });

            exportOrgByParent(user.getTenantUID(), orgInfoList, orgInfoMap, resultList, buildManagementList, buildManagementMap);

            //remove fake data row in template
            doc.clearRow(startRow);
            for (Map<String, Object> map : resultList) {
                doc.writeRowData(0, columnKeys, startRow, map);
                ++startRow;
            }
        }
        return doc;


    }

    private void exportOrgByParent(String parentUid, final List<OrgInfo> orgInfoList, final Map<String, OrgInfo> orgInfoMap, final List<Map<String, Object>> resultList, final List<BuildManagement> buildManagementList, final Map<Integer, BuildManagement> buildManagementMap) {

        List<OrgInfo> children = findOrgByParent(parentUid, orgInfoList);

        for (OrgInfo orgInfo : children) {

            List<OrgInfo> list = findOrgByParent(orgInfo.getUid(), orgInfoList);
            if (list.size() > 0) {
                exportOrgByParent(orgInfo.getUid(), orgInfoList, orgInfoMap, resultList, buildManagementList, buildManagementMap);
            }
            exportBuildManagement(orgInfo, resultList, orgInfoMap, buildManagementList, buildManagementMap);
            exportOrg(orgInfo, resultList, orgInfoMap, null, null);
        }
    }

    private void exportBuildManagement(OrgInfo orgInfo, List<Map<String, Object>> resultList, Map<String, OrgInfo> orgInfoMap, List<BuildManagement> buildManagementList, Map<Integer, BuildManagement> buildManagementMap) {
//        if(orgInfo.getOrgType()!=4){
//            exportOrg(orgInfo, resultList, orgInfoMap,null,null);
//        }
        List<BuildManagement> children = findBuildManagementBySiteUid(orgInfo.getSiteUID(), buildManagementList);
//        if(CollectionUtils.isEmpty(children)){
//            exportOrg(orgInfo, resultList, orgInfoMap,null,null);
//        }
        for (BuildManagement child : children) {
            aa(orgInfo, resultList, orgInfoMap, child, buildManagementList, buildManagementMap);
        }

    }

    private void aa(OrgInfo orgInfo, List<Map<String, Object>> resultList, final Map<String, OrgInfo> orgInfoMap, BuildManagement child, List<BuildManagement> buildManagementList, Map<Integer, BuildManagement> buildManagementMap) {
        List<BuildManagement> buildManagementByParent = findBuildManagementByParent(child.getId(), buildManagementList);
        if (buildManagementByParent.size() > 0) {
            for (BuildManagement buildManagement : buildManagementByParent) {
                aa(orgInfo, resultList, orgInfoMap, buildManagement, buildManagementList, buildManagementMap);
            }
        }
        exportOrg(orgInfo, resultList, orgInfoMap, buildManagementMap, child);


    }

    private List<BuildManagement> findBuildManagementBySiteUid(String siteUid, final List<BuildManagement> buildManagementList) {

        List<BuildManagement> children = buildManagementList.stream().filter(buildManagement -> siteUid.equals(buildManagement.getSiteUID())).collect(Collectors.toList());

        return children;
    }

    private List<BuildManagement> findBuildManagementByParent(Integer parentId, final List<BuildManagement> buildManagementList) {

        List<BuildManagement> children = buildManagementList.stream().filter(buildManagement -> parentId == buildManagement.getParentID()).collect(Collectors.toList());

        return children;

    }

    private List<OrgInfo> findOrgByParent(String parentUid, final List<OrgInfo> orgInfoList) {

        List<OrgInfo> children = orgInfoList.stream().filter(orgInfo -> parentUid.equals(orgInfo.getParentUID())).collect(Collectors.toList());

        return children;

    }

    private void exportOrg(OrgInfo orgInfo, List<Map<String, Object>> resultList, final Map<String, OrgInfo> orgInfoMap, Map<Integer, BuildManagement> buildManagementMap, BuildManagement buildManagement) {
        Integer orgType = orgInfo.getOrgType();
        if (buildManagement != null) {
            orgType = buildManagement.getBuildType();
        }
        Map<String, Object> map = new HashMap<>();
        if (Objects.equals(orgType, 1)) {
            map.put("集团", orgInfo.getName());
        } else if (Objects.equals(orgType, 2)) {
            OrgInfo institutionLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
            map.put("集团", institutionLevelOrg.getName());
            map.put("医院", orgInfo.getName());
        } else if (Objects.equals(orgType, 3)) {
            OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
            OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
            map.put("集团", institutionLevelOrg.getName());
            map.put("医院", hospitalLevelOrg.getName());
            map.put("院区", orgInfo.getName());
        } else if (Objects.equals(orgType, 4)) {
            if (Objects.equals(orgInfo.getOrgLevel(), 0)) {

            } else if (Objects.equals(orgInfo.getOrgLevel(), 1)) {
                OrgInfo institutionLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                map.put("集团", institutionLevelOrg.getName());
            } else if (Objects.equals(orgInfo.getOrgLevel(), 2)) {
                OrgInfo hospitalLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
            } else {
                OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
                map.put("院区", orgInfo.getName());
            }
            map.put("楼栋名称", buildManagement.getName());
        } else if (Objects.equals(orgType, 5)) {
            if (Objects.equals(orgInfo.getOrgLevel(), 0)) {

            } else if (Objects.equals(orgInfo.getOrgLevel(), 1)) {
                OrgInfo institutionLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                map.put("集团", institutionLevelOrg.getName());
            } else if (Objects.equals(orgInfo.getOrgLevel(), 2)) {
                OrgInfo hospitalLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
            } else {
                OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
                map.put("院区", orgInfo.getName());
            }
            map.put("楼栋名称", buildManagementMap.get(buildManagement.getParentID()).getName());
            map.put("楼层名称", buildManagement.getName());
        } else if (Objects.equals(orgType, 6)) {
            if (Objects.equals(orgInfo.getOrgLevel(), 0)) {

            } else if (Objects.equals(orgInfo.getOrgLevel(), 1)) {
                OrgInfo institutionLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                map.put("集团", institutionLevelOrg.getName());
            } else if (Objects.equals(orgInfo.getOrgLevel(), 2)) {
                OrgInfo hospitalLevelOrg = getParentHospitalForDepartment(orgInfo, orgInfoMap);
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
            } else {
                OrgInfo hospitalLevelOrg = orgInfoMap.get(orgInfo.getParentUID());
                OrgInfo institutionLevelOrg = orgInfoMap.get(hospitalLevelOrg.getParentUID());
                map.put("集团", institutionLevelOrg.getName());
                map.put("医院", hospitalLevelOrg.getName());
                map.put("院区", orgInfo.getName());
            }
            BuildManagement buildManagement1 = buildManagementMap.get(buildManagement.getParentID());
            map.put("楼栋名称", buildManagementMap.get(buildManagement1.getParentID()).getName());
            map.put("楼层名称", buildManagement1.getName());
            map.put("房间名称", buildManagement.getName());

        }

        if (buildManagement != null) {
            map.put("UUID", buildManagement.getUid());
            map.put("UCMID", buildManagement.getHospitalUcmid());
        }


        resultList.add(map);

    }


    //为科室寻找商机医院(院区 or 医院 or 集团), 考虑多级科室的情形，需要向上寻找到院区
    private OrgInfo getParentHospitalForDepartment(OrgInfo dept, final Map<String, OrgInfo> orgInfoMap) {
        OrgInfo parentOrg = orgInfoMap.get(dept.getParentUID());
        while (parentOrg != null) { //如果是多级科室，需要向上寻找到院区
            if (parentOrg.getOrgType() <= 3) break;
            parentOrg = orgInfoMap.get(parentOrg.getParentUID());
        }
        if (parentOrg == null)
            parentOrg = dept;  //如果没有找到，则返回本身。

        return parentOrg;
    }


    private void getPagingData1(List<OrgInfo> orginfoInstitutionlist, int pageSize, OrgInfo parent, String[] columnKeys, int startRow, ExcelDocument doc) {
        List<String> uids = new ArrayList();

        for (int i = 0; i < orginfoInstitutionlist.size(); i++) {
            uids.add(orginfoInstitutionlist.get(i).getUid());//每次循环收集要查询的uid,到与pageSize一样大小的时候统一执行。
            if (uids.size() % pageSize == 0) {//如果到了分页的次数就统一执行一次
                List<Map<String, Object>> resultList2 = new ArrayList<>();
                //2.通过集团信息向下递归出所有的子集 orginfoInstitutionlist.uid
                List<OrgInfo> orgHospitalChildrenlist = orgDao.getHospitalChildrenByUids(uids);
                //查询出顶级目录为了能让集团变为顶级的儿子
                orgHospitalChildrenlist.add(parent);
                Map<String, OrgInfo> orgInfoMap2 = new HashMap<>();
                orgHospitalChildrenlist.stream().forEach(orgInfo -> {
                    orgInfoMap2.put(orgInfo.getUid(), orgInfo);
                });
                exportOrgByParent(parent.getUid(), orgHospitalChildrenlist, orgInfoMap2, resultList2, null, null);
                uids = new ArrayList();//为下次查询清空uids
                for (Map<String, Object> map : resultList2) {
                    doc.writeRowData(0, columnKeys, startRow, map);
                    ++startRow;
                }
            }
            if ((i + 1) == orginfoInstitutionlist.size() && orginfoInstitutionlist.size() % pageSize != 0) {//如果遍历到了最后一次则把剩下的执行完毕。
                List<Map<String, Object>> resultList2 = new ArrayList<>();
                //2.通过集团信息向下递归出所有的子集 orginfoInstitutionlist.uid
                List<OrgInfo> orgHospitalChildrenlist = orgDao.getHospitalChildrenByUids(uids);
                //查询出顶级目录为了能让集团变为顶级的儿子
                orgHospitalChildrenlist.add(parent);
                Map<String, OrgInfo> orgInfoMap2 = new HashMap<>();
                orgHospitalChildrenlist.stream().forEach(orgInfo -> {
                    orgInfoMap2.put(orgInfo.getUid(), orgInfo);
                });
                exportOrgByParent(parent.getUid(), orgHospitalChildrenlist, orgInfoMap2, resultList2, null, null);
                uids = new ArrayList();//为下次查询清空uids
                for (Map<String, Object> map : resultList2) {
                    doc.writeRowData(0, columnKeys, startRow, map);
                    ++startRow;
                }
            }
        }
    }

//    public ExcelDocument exportBuildManagement(TreeNode orgTree) throws IOException {
////        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/orgInfo/APM_Building_Template.xlsx");
//        FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);
//
//        InputStream stream = fileDownloadService.getTemplateFileInputStream(SystemTemplate.SystemTemplateEnum.BUILD_MANAGEMENT.getCode(), SystemTemplate.SystemTemplateEnum.BUILD_MANAGEMENT.getFileType());
//        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
//        String[] columnKeys = doc.getKeyRow(0, 1);
//        int startRow = 2;
//        List<Map<String, Object>> resultList = new ArrayList<>();
//
//        List<TreeNode> children = orgTree.getChildren();
//        for (TreeNode child : children) {
//            export(child, resultList);
//        }
//        doc.clearRow(startRow);
//        for (Map<String, Object> map : resultList) {
//            doc.writeRowData(0, columnKeys, startRow, map);
//            ++startRow;
//        }
//        return doc;
//    }


    public InputStream exportBuildManagement(TreeNode orgTree) throws IOException {
//        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/uaa/orgInfo/APM_Building_Template.xlsx");
        SystemTemplateService systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
        UserAccount user = UserContextService.getCurrentUserAccount();
        byte[] stream = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.BUILD_MANAGEMENT, user);
//        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
//        String[] columnKeys = doc.getKeyRow(0, 1);
//        int startRow = 2;
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<TreeNode> children = orgTree.getChildren();
        for (TreeNode child : children) {
            export(child, resultList);
        }

        List<BuildManagementExportDto> buildManagementExportDtoList = new ArrayList<>();
        for (Map<String, Object> map : resultList) {
            BuildManagementExportDto buildManagementExportDto = new BuildManagementExportDto();
            try {
                org.apache.commons.beanutils.BeanUtils.populate(buildManagementExportDto, map);
                buildManagementExportDtoList.add(buildManagementExportDto);
            } catch (Exception e) {
                logger.error("map to exportDto error: ",e);
                return  new ByteArrayInputStream(new byte[0]);
            }
        }

        byte[] bytes = EasyExcelExportUtil.exportExcel(stream, buildManagementExportDtoList, new HashMap<>());
        return new ByteArrayInputStream(bytes);
    }



    private void export(TreeNode child, List<Map<String, Object>> resultList) {
        List<TreeNode> children = child.getChildren();
        for (TreeNode treeNode : children) {
//            if(!"org".equalsIgnoreCase(treeNode.getType())){
//                continue;
//            }
            if (treeNode.getChildren().size() > 0) {
                export(treeNode, resultList);
            }
            exportBuildManagement(treeNode, resultList);
        }


    }

    private void exportBuildManagement(TreeNode child, List<Map<String, Object>> resultList) {
        Map<String, Object> map = buildMap(child);
        if(!CollectionUtils.isEmpty(map)){
            resultList.add(map);
        }
    }


    private Map<String, Object> buildMap(TreeNode child) {
        Map<String, Object> map = new HashMap<>();

        if (child.getData() instanceof OrgInfo) {
            OrgInfo orgInfo = (OrgInfo) child.getData();
            Integer orgType = orgInfo.getOrgType();
            if (Objects.equals(orgType, 1)) {
                map.put("institutionName", orgInfo.getName());
            } else if (Objects.equals(orgType, 2)) {
                TreeNode parent = child.getParent();
                OrgInfo orgInfo1 = (OrgInfo) parent.getData();
                map.put("institutionName", orgInfo1.getName());
                map.put("hospitalName", orgInfo.getName());
            } else if (Objects.equals(orgType, 3)) {
                TreeNode parent = child.getParent();
                OrgInfo orgInfo1 = (OrgInfo) parent.getData();
                map.put("hospitalName", orgInfo1.getName());
                TreeNode parent1 = parent.getParent();
                OrgInfo orgInfo2 = (OrgInfo) parent1.getData();
                map.put("institutionName", orgInfo2.getName());
                map.put("siteName", orgInfo.getName());
            }
        } else if (child.getData() instanceof BuildManagement) {

            TreeNode treeNode = null;

            BuildManagement buildManagement = (BuildManagement) child.getData();
            Integer orgType = buildManagement.getBuildType();
            if (Objects.equals(orgType, 4)) {
                map.put("buildingName", buildManagement.getName());
                map.put("buildingNameEn", buildManagement.getNameEn());
                treeNode = child;
            } else if (Objects.equals(orgType, 5)) {
                TreeNode parent = child.getParent();
                BuildManagement buildManagement1 = (BuildManagement) parent.getData();
                map.put("buildingName", buildManagement1.getName());
                map.put("buildingNameEn", buildManagement1.getNameEn());
                map.put("floorName", buildManagement.getName());
                map.put("floorNameEn", buildManagement.getNameEn());

                treeNode = parent;
            } else if (Objects.equals(orgType, 6)) {
                TreeNode parent = child.getParent();
                BuildManagement buildManagement1 = (BuildManagement) parent.getData();
                TreeNode parent1 = parent.getParent();
                BuildManagement buildManagement2 = (BuildManagement) parent1.getData();
                map.put("buildingName", buildManagement2.getName());
                map.put("buildingNameEn", buildManagement2.getNameEn());
                map.put("floorName", buildManagement1.getName());
                map.put("floorNameEn", buildManagement1.getNameEn());
                map.put("roomName", buildManagement.getName());
                map.put("roomNameEn", buildManagement.getNameEn());
                treeNode = parent1;
            }
            map.put("UUID", buildManagement.getUid());
            map.put("UCMID", buildManagement.getHospitalUcmid());
            Map<String, Object> resultMap = buildMap(treeNode.getParent());
            map.putAll(resultMap);
        }
        return map;
    }
}