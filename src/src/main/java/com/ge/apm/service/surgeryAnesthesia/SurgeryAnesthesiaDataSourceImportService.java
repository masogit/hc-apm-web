package com.ge.apm.service.surgeryAnesthesia;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SurgeryAnesthesiaDataSourceRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaDataSource;
import com.ge.apm.service.asset.ContractRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SurgeryAnesthesiaDataSourceImportService {
    private static final Logger logger = LoggerFactory.getLogger(SurgeryAnesthesiaDataSourceImportService.class);

    public static final String SHEET_NAME = "OrDataSource List";
    public static final int EXCEL_HEADER_ROW = 1;
    public static final int EXCEL_DATA_ROW = 2;

    @Autowired
    private SurgeryAnesthesiaDataSourceRepository dataSourceRepository;

    @Autowired
    private OrgInfoRepository orgInfoDao;

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_DATA_ROW; i <= doc.getWorkBook().getSheet(SHEET_NAME).getLastRowNum() + 1; i++) {
            Map<String, Object> rowMap = doc.getDataRowMap(SHEET_NAME, EXCEL_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap<>();
            SurgeryAnesthesiaDataSource dataSource = new SurgeryAnesthesiaDataSource();
            dataSource.setLineNum(i);
            SurgeryAnesthesiaDataSourceRow row = new SurgeryAnesthesiaDataSourceRow(dataSource, rowMap);
            row.setFieldStringValue("机构代码", "setOrgCode");
            row.setFieldStringValue("设备代码", "setDeviceCode");
            row.setFieldDateValue("手术日期", "setSurgeryDate");
            row.setFieldDateValue("计划开始时间", "setScheduledStartTime");
            row.setFieldDateValue("手术开始时间", "setSurgeryStartTime");
            row.setFieldDateValue("手术结束时间", "setSurgeryEndTime");
            row.setFieldDateValue("离开手术室时间", "setExitFromOrTime");
            row.setFieldStringValue("手术室名称", "setOperatingRoomName");
            row.setFieldStringValue("手术室代码", "setOperatingRoomCode");
            row.setFieldStringValue("住院号", "setInpNo");
            row.setFieldStringValue("系统来源", "setSourceCode");
            map.put("surgeryAnesthesiaDataSource", dataSource);
            map.put("rowData", rowMap);
            map.put("status", ImportStatus.New.name());
            result.add(map);
        }
        return result;
    }

    public void importData(List<Map<String, Object>> importMapList) {
        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        orgInfoFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.IsNotNull, null));
        List<OrgInfo> orgInfoList = orgInfoDao.findBySearchFilter(orgInfoFilters);
        List<String> orgCodeList = orgInfoList.stream().map(OrgInfo::getOrgCode).collect(Collectors.toList());
        Map<String, OrgInfo> orgInfoMap = orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getOrgCode, each -> each, (v1, v2) -> v1));
        List<SurgeryAnesthesiaDataSource> existingData = dataSourceRepository.findByTenantUid(UserContextService.getCurrentUserAccount().getTenantUID());
        Map<String, List<SurgeryAnesthesiaDataSource>> existingByOrgCode = existingData.stream()
                .filter(d -> d.getOrgCode() != null && d.getSurgeryDate() != null)
                .collect(Collectors.groupingBy(SurgeryAnesthesiaDataSource::getOrgCode));
        int row = 0;
        try {
            for (Map<String, Object> item : importMapList) {
                ++row;
                checkData(item, orgCodeList, existingByOrgCode, orgInfoMap);
            }
        } catch (Exception e) {
            logger.error("error occurred when importing the {} row", row, e);
            throw e;
        }
    }

    private boolean checkData(Map<String, Object> item, List<String> orgCodeList,
                              Map<String, List<SurgeryAnesthesiaDataSource>> existingByOrgCode,
                              Map<String, OrgInfo> orgInfoMap) {
        boolean isValid = true;
        SurgeryAnesthesiaDataSource ds = (SurgeryAnesthesiaDataSource) item.get("surgeryAnesthesiaDataSource");
        ContractRow.clearErrMessage(item);
        if (ds.getOrgCode() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "机构代码不能为空"); isValid = false; }
        if (ds.getDeviceCode() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "设备代码不能为空"); isValid = false; }
        if (ds.getSurgeryDate() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "手术日期不能为空"); isValid = false; }
        if (ds.getScheduledStartTime() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "计划开始时间不能为空"); isValid = false; }
        if (ds.getSurgeryStartTime() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "手术开始时间不能为空"); isValid = false; }
        if (ds.getSurgeryEndTime() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "手术结束时间不能为空"); isValid = false; }
        if (ds.getExitFromOrTime() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "离开手术室时间不能为空"); isValid = false; }
        if (ds.getOperatingRoomName() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "手术室名称不能为空"); isValid = false; }
        if (ds.getOperatingRoomCode() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "手术室代码不能为空"); isValid = false; }
        if (ds.getInpNo() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "住院号不能为空"); isValid = false; }
        if (ds.getSourceCode() == null) { item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "系统来源不能为空"); isValid = false; }
        if (ds.getSurgeryStartTime() != null && ds.getSurgeryEndTime() != null && ds.getSurgeryEndTime().before(ds.getSurgeryStartTime())) {
            item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "手术结束时间不能早于手术开始时间"); isValid = false;
        }
        if (StringUtils.isNotBlank(ds.getOrgCode())) {
            if (!orgCodeList.contains(ds.getOrgCode())) {
                item.put("status", ImportStatus.Failure.name()); ContractRow.addErrMessage(item, "租户下面不存在该机构代码"); isValid = false;
            } else {
                OrgInfo orgInfo = orgInfoMap.get(ds.getOrgCode());
                ds.setSiteUid(orgInfo.getSiteUID()); ds.setTenantUid(orgInfo.getTenantUID());
            }
        }
        return isValid;
    }

    public void saveToDB(List<Map<String, Object>> importMapList) {
        List<SurgeryAnesthesiaDataSource> list = importMapList.stream()
                .filter(map -> { String s = map.get("status") == null ? null : map.get("status").toString(); return s == null || (!ImportStatus.Failure.name().equals(s) && !ImportStatus.CheckFail.name().equals(s)); })
                .map(map -> (SurgeryAnesthesiaDataSource) map.get("surgeryAnesthesiaDataSource"))
                .collect(Collectors.toCollection(ArrayList::new));
        if (CollectionUtils.isEmpty(list)) return;
        list.forEach(item -> { item.setIsMatched(Boolean.FALSE); item.setCreateDate(new Date()); });
        dataSourceRepository.save(list);
        logger.info("成功导入 {} 条手术麻醉源数据", list.size());
    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : importMapList) {
            String importStatus = map.get("status") == null ? null : map.get("status").toString();
            if (importStatus == null) continue;
            if (ImportStatus.CheckFail.name().equals(importStatus) || ImportStatus.Failure.name().equals(importStatus)) result.add(map);
        }
        return result;
    }

    public enum ImportStatus { New, Exist, Created, Failure, CheckFail }
}
