package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.SysAuthGroupRepository;
import com.ge.apm.dao.mapper.UdidRegistryNumInfoMapper;
import com.ge.apm.domain.SysAuthGroup;
import com.ge.apm.domain.UdidRegistryNumInfo;
import com.ge.healthcare.cn.common.cons.DataSourceEnum;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
public class UdidInfoService {

    @Autowired
    private UdidRegistryNumInfoMapper udidRegistryNumInfoMapper;
    @Autowired
    private SysAuthGroupRepository authGroupDao;

    /**
     * 模糊查询注册证号
     *
     * @param registryNum 注册证号
     */
    public List<String> fuzzySearchRegistryNum(String registryNum) {

        List<UdidRegistryNumInfo> result = fuzzySearch(registryNum, "", "", "");

        return result.stream()
                .map(UdidRegistryNumInfo::getRegistryNum)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询制造商
     *
     * @param registryNum 注册证号
     * @param manufacture 制造商
     */
    public List<String> fuzzySearchManufacture(String registryNum, String manufacture) {

        List<UdidRegistryNumInfo> result = fuzzySearch(registryNum, manufacture, "", "");

        return result.stream()
                .map(UdidRegistryNumInfo::getRegistrantName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询设备名称
     *
     * @param registryNum 注册证号
     * @param assetName   设备名称
     */
    public List<String> fuzzySearchAssetName(String registryNum, String assetName) {

        List<UdidRegistryNumInfo> result = fuzzySearch(registryNum, "", assetName, "");

        return result.stream()
                .map(UdidRegistryNumInfo::getProductName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询设备型号
     *
     * @param registryNum  注册证号
     * @param functionType 设备型号
     */
    public List<String> fuzzySearchFunctionType(String registryNum, String functionType) {

        List<UdidRegistryNumInfo> result = fuzzySearch(registryNum, "", "", functionType);

        return result.stream()
                .map(UdidRegistryNumInfo::getGgxh)
                .filter(StringUtils::isNotBlank)
                .flatMap(s -> parseJsonArrayToStringList(s).stream())
                .filter(StringUtils::isNotBlank)
                .distinct()
                // 支持输入筛选
                .filter(s -> StringUtils.isBlank(functionType) || s.toLowerCase().contains(functionType.toLowerCase()))
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询
     *
     * @param registryNum  注册证号
     * @param manufacture  制造商
     * @param assetName    设备名称
     * @param functionType 设备型号
     */
    private List<UdidRegistryNumInfo> fuzzySearch(String registryNum, String manufacture,
                                                  String assetName, String functionType) {

        return udidRegistryNumInfoMapper.fuzzySearch(registryNum, manufacture, assetName, functionType);
    }

    /**
     * 模糊查询
     *
     * @param registryNum 注册证号
     */
    private List<SysAuthGroup> fuzzySearch(String registryNum) {

        if (StringUtils.isBlank(registryNum)) {
            return Lists.newArrayList();
        }

        UdidRegistryNumInfo udidRegistryNumInfo = selectByRegistryNum(registryNum);

        if (Objects.isNull(udidRegistryNumInfo)) {
            return Lists.newArrayList();
        }

        List<String> categoryList = parseJsonArrayAndStripHyphens(udidRegistryNumInfo.getFlbm()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(categoryList)) {
            return Lists.newArrayList();
        }

        return authGroupDao.listSysAuthGroupForUdid(categoryList);
    }

    /**
     * 查询注册证详情
     *
     * @param registryNum 注册证号
     */
    public UdidRegistryNumInfo selectByRegistryNum(String registryNum) {

        if (StringUtils.isBlank(registryNum)) {
            return null;
        }
        return udidRegistryNumInfoMapper.selectByRegistryNum(registryNum);
    }

    public TreeNode buildMedicalClassTree(String registryNum) {
        TreeNode rootNode = new DefaultTreeNode("medicalClass", null, null);

        TreeNode udidTree = buildTreeNode(fuzzySearch(registryNum), DataSourceEnum.UDID.getSourceName());
        udidTree.setSelectable(false);
        TreeNode apmTree = buildTreeNode(authGroupDao.listSysAuthGroup(), DataSourceEnum.APM.getSourceName());
        apmTree.setSelectable(false);

        List<TreeNode> children = rootNode.getChildren();
        children.add(udidTree);
        children.add(apmTree);
        rootNode.setExpanded(true);

        return rootNode;
    }

    public TreeNode buildTreeNode(List<SysAuthGroup> medicalClassList, String rootNodeName) {
        TreeNode tempNode = new DefaultTreeNode("medicalClass", null, null);
        Map<String, TreeNode> treeMap = medicalClassList.stream().collect(Collectors.toMap(SysAuthGroup::getUid, item -> new DefaultTreeNode("medicalClass", item, null)));
        for (SysAuthGroup entry : medicalClassList) {
            treeMap.getOrDefault(entry.getParentUid(), tempNode).getChildren().add(treeMap.get(entry.getUid()));
        }
        AssetInfoService.sortSysAuthGroupNaturally(tempNode);
        TreeNode rootNode = new DefaultTreeNode("medicalClass", new SysAuthGroup(rootNodeName), null);
        List<TreeNode> children = rootNode.getChildren();
        children.addAll(tempNode.getChildren());
        rootNode.setExpanded(true);
        return rootNode;
    }

    /**
     * 解析JSON数组字符串为字符串List
     * 例如: ["TDP-L1", "TDP-L2", "TDP-L3"] -> List<String>
     *
     * @param jsonArray JSON数组字符串
     * @return 字符串列表，输入无效时返回空列表
     */
    public static List<String> parseJsonArrayToStringList(String jsonArray) {
        if (StringUtils.isBlank(jsonArray)) {
            return Lists.newArrayList();
        }
        try {
            List<String> list = JSON.parseArray(jsonArray, String.class);
            // 移除可能的null元素（如输入"[null]"）
            list.removeIf(Objects::isNull);
            return list;
        } catch (Exception e) {
            log.warn("Failed to parse JSON array: {}", jsonArray, e);
            return Lists.newArrayList();
        }
    }

    /**
     * 解析JSON数组字符串并去除尾部的"-00"及剩余连字符
     * <pre>
     *   ["22-01-04"] -> ["220104"]
     *   ["22-00-00"] -> ["22"]
     *   ["22-01-00"] -> ["2201"]
     * </pre>
     *
     * @param jsonArray JSON数组字符串
     * @return 处理后的字符串列表，输入无效时返回空列表
     */
    public static List<String> parseJsonArrayAndStripHyphens(String jsonArray) {
        return parseJsonArrayToStringList(jsonArray).stream()
                .filter(StringUtils::isNotBlank)
                .map(s -> s.replace("-00", "").replace("-", ""))
                .collect(Collectors.toList());
    }
}
