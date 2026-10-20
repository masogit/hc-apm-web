package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.mapper.UdidDeviceCategoryMapper;
import com.ge.apm.dao.mapper.UdidRegistryNumInfoMapper;
import com.ge.apm.domain.UdidDeviceCategory;
import com.ge.apm.domain.UdidRegistryNumInfo;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
public class UdidSearchService {

    @Autowired
    private UdidDeviceCategoryMapper udidDeviceCategoryMapper;
    @Autowired
    private UdidRegistryNumInfoMapper udidRegistryNumInfoMapper;

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

        if (StringUtils.isBlank(registryNum) && StringUtils.isBlank(manufacture)
                && StringUtils.isBlank(assetName) && StringUtils.isBlank(functionType)) {
            return Lists.newArrayList();
        }
        return udidRegistryNumInfoMapper.fuzzySearch(registryNum, manufacture, assetName, functionType);
    }

    /**
     * 模糊查询设备型号
     *
     * @param category 器械分类代码或名称
     */
    public List<String> fuzzySearchCategory(String registryNum, String category) {

        List<UdidDeviceCategory> result = fuzzySearch(registryNum, category);

        return result.stream()
                .map(UdidDeviceCategory::getDisplayCategory)
                .filter(StringUtils::isNotBlank)
                .distinct()
                // 支持输入筛选
                .filter(s -> StringUtils.isBlank(category) || s.toLowerCase().contains(category.toLowerCase()))
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询
     *
     * @param category 器械分类代码或名称
     */
    private List<UdidDeviceCategory> fuzzySearch(String registryNum, String category) {

        if (StringUtils.isBlank(registryNum) && StringUtils.isBlank(category)) {
            return Lists.newArrayList();
        }
        return udidDeviceCategoryMapper.fuzzySearch(registryNum, category);
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
     * 解析JSON数组字符串并去除元素中的连字符
     * 例如: ["22-01-04"] -> ["220104"]
     *
     * @param jsonArray JSON数组字符串
     * @return 去除连字符后的字符串列表，输入无效时返回空列表
     */
    public static List<String> parseJsonArrayAndStripHyphens(String jsonArray) {
        return parseJsonArrayToStringList(jsonArray).stream()
                .filter(StringUtils::isNotBlank)
                .map(s -> s.replace("-", ""))
                .collect(Collectors.toList());
    }
}
