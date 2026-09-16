package com.ge.apm.service.asset;

import com.ge.apm.dao.mapper.UdidDeviceCubeMapper;
import com.ge.apm.dao.mapper.UdidRegistryNumInfoMapper;
import com.ge.apm.domain.UdidDeviceCube;
import com.ge.apm.domain.UdidRegistryNumInfo;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@Service
public class UdidSearchService {

    @Autowired
    private UdidDeviceCubeMapper udidDeviceCubeMapper;
    @Autowired
    private UdidRegistryNumInfoMapper udidRegistryNumInfoMapper;

    /**
     * 模糊查询注册证号
     */
    public List<String> fuzzySearchRegistryNum(String registryNum, String registrantName,
                                               String deviceName, String devicePattern) {

        List<UdidDeviceCube> result = fuzzySearch(registryNum, registrantName, deviceName, devicePattern);
        return result.stream()
                .map(UdidDeviceCube::getRegistryNum)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询制造商
     */
    public List<String> fuzzySearchRegistrantName(String registryNum, String registrantName,
                                                  String deviceName, String devicePattern) {

        List<UdidDeviceCube> result = fuzzySearch(registryNum, registrantName, deviceName, devicePattern);
        return result.stream()
                .map(UdidDeviceCube::getRegistrantName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询设备名称
     */
    public List<String> fuzzySearchDeviceName(String registryNum, String registrantName,
                                              String deviceName, String devicePattern) {

        List<UdidDeviceCube> result = fuzzySearch(registryNum, registrantName, deviceName, devicePattern);
        return result.stream()
                .map(UdidDeviceCube::getDeviceName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询设备型号
     */
    public List<String> fuzzySearchDevicePattern(String registryNum, String registrantName,
                                                 String deviceName, String devicePattern) {

        List<UdidDeviceCube> result = fuzzySearch(registryNum, registrantName, deviceName, devicePattern);
        return result.stream()
                .map(UdidDeviceCube::getDevicePattern)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
    }

    /**
     * 模糊查询
     */
    private List<UdidDeviceCube> fuzzySearch(String registryNum, String registrantName,
                                             String deviceName, String devicePattern) {

        if (StringUtils.isBlank(registryNum) && StringUtils.isBlank(registrantName)
                && StringUtils.isBlank(deviceName) && StringUtils.isBlank(devicePattern)) {
            return Lists.newArrayList();
        }
        return udidDeviceCubeMapper.fuzzySearch(registryNum, registrantName, deviceName, devicePattern);
    }

    /**
     * 查询注册证详情
     */
    public UdidRegistryNumInfo selectByRegistryNum(String registryNum) {

        if (StringUtils.isBlank(registryNum)) {
            return null;
        }
        return udidRegistryNumInfoMapper.selectByRegistryNum(registryNum);
    }
}
