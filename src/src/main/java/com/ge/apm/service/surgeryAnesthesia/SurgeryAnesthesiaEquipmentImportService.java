package com.ge.apm.service.surgeryAnesthesia;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SurgeryAnesthesiaEquipmentConnectRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaEquipmentConnect;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 手麻系统设备导入Service
 * 解析Excel并导入手术麻醉设备数据
 */
@Service
public class SurgeryAnesthesiaEquipmentImportService {
    private static final Logger logger = LoggerFactory.getLogger(SurgeryAnesthesiaEquipmentImportService.class);

    public static final String SHEET_NAME = "SurgeryEquipment List";
    public static final int EXCEL_HEADER_ROW = 1;
    public static final int EXCEL_DATA_ROW = 2;

    @Autowired
    private SurgeryAnesthesiaEquipmentConnectRepository connectRepository;

    @Autowired
    private OrgInfoRepository orgInfoDao;

    /**
     * 从Excel模板中解析数据
     */
    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        int keyRow = EXCEL_HEADER_ROW;
        int dataRow = EXCEL_DATA_ROW;
        for (Map<String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String deviceCode = ExcelDocument.getCellStringValue(rowMap.get("device_code"));
            String deviceName = ExcelDocument.getCellStringValue(rowMap.get("device_name"));

            if (null == deviceCode && null == deviceName) {
                break;
            }

            SurgeryAnesthesiaEquipmentConnect connect = new SurgeryAnesthesiaEquipmentConnect();
            connect.setDeviceCode(deviceCode);
            connect.setDeviceName(ExcelDocument.getCellStringValue(rowMap.get("device_name")));
            connect.setOrgCode(ExcelDocument.getCellStringValue(rowMap.get("org_code")));
            connect.setIsMatched(false);
            connect.setCreatedDate(new Date());
            connect.setLineNum(dataRow);

            // 设置租户信息
            String tenantUid = UserContextService.getCurrentUserAccount().getTenantUID();
            connect.setTenantUid(tenantUid);

            // 根据机构代码查找siteUid
            if (StringUtils.isNotBlank(connect.getOrgCode())) {
                List<SearchFilter> orgFilters = new ArrayList<>();
                orgFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.EQ, connect.getOrgCode()));
                orgFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUid));
                List<OrgInfo> orgInfos = orgInfoDao.findBySearchFilter(orgFilters);
                if (!orgInfos.isEmpty()) {
                    connect.setSiteUid(orgInfos.get(0).getSiteUID());
                }
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("connect", connect);
            resultMap.put("status", "success");
            resultList.add(resultMap);
        }

        return resultList;
    }

    /**
     * 导入数据到数据库
     */
    public void importData(List<Map<String, Object>> mapList) {
        List<SurgeryAnesthesiaEquipmentConnect> toSave = new ArrayList<>();

        for (Map<String, Object> map : mapList) {
            SurgeryAnesthesiaEquipmentConnect connect = (SurgeryAnesthesiaEquipmentConnect) map.get("connect");
            try {
                // 校验必填字段
                if (StringUtils.isBlank(connect.getDeviceCode())) {
                    map.put("status", "error");
                    map.put("errmsg", "设备识别码不能为空");
                    continue;
                }
                if (StringUtils.isBlank(connect.getDeviceName())) {
                    map.put("status", "error");
                    map.put("errmsg", "设备名称不能为空");
                    continue;
                }

                // 检查是否已存在
                SurgeryAnesthesiaEquipmentConnect existing = null;
                if (StringUtils.isNotBlank(connect.getOrgCode())) {
                    existing = connectRepository.findByDeviceCodeAndOrgCode(connect.getDeviceCode(), connect.getOrgCode());
                } else {
                    existing = connectRepository.findByDeviceCode(connect.getDeviceCode());
                }

                if (existing != null) {
                    map.put("status", "error");
                    map.put("errmsg", "设备识别码已存在，请勿重复导入");
                    continue;
                }

                toSave.add(connect);
            } catch (Exception e) {
                map.put("status", "error");
                map.put("errmsg", "数据校验失败: " + e.getMessage());
                logger.error("导入手麻系统设备数据异常, 行号: " + connect.getLineNum(), e);
            }
        }

        if (!toSave.isEmpty()) {
            connectRepository.save(toSave);
            logger.info("成功导入 {} 条手麻系统设备数据", toSave.size());
        }
    }

    /**
     * 过滤出错误记录
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> mapList) {
        return mapList.stream()
                .filter(map -> "error".equals(map.get("status")))
                .collect(Collectors.toList());
    }
}