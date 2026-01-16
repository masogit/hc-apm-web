package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZknContractCourseScheduleMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 212605082 on 2019/12/18.
 */
@Component
public class ZkbEngineerService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbEngineerService.class);

    @Autowired
    BlobObjectRepository blobObjectRepository;

    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private ZkbEngineerRepository zkbEngineerDao;
    @Autowired
    private ZkbEngineerTrackRecordRepository zkbEngineerTrackRecordDao;

    @Autowired
    private ZkbEngineerRepository zkbEngineerRepository;

    @Autowired
    private ZknContractCourseScheduleMapper zknContractCourseScheduleMapper;

    @Autowired
    private ZkbTrainAttendanceRecordRepository zkbTrainAttendanceRecordRepository;

    public List<TenantInfo> getSourceTenantInfoList() {

        List<SearchFilter> searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));

        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID) {
        return tenantInfoDao.findByuid(tenantUID);
    }

    public List<ZkbEngineerTrackRecord> getZkbEngineerTrackRecordList(String zkbEngineerId) {

        List<SearchFilter> searchFilters = new ArrayList<>();

        searchFilters.add(new SearchFilter("zkbEngineerId", SearchFilter.Operator.EQ, zkbEngineerId));

        return zkbEngineerTrackRecordDao.findBySearchFilter(searchFilters);

    }

    /**
     * 根据工程师ID获取培训记录
     *
     * @param eid 工程师主键
     * @return
     */
    public List<ZkbContractCourseSchedule> getZkbEngineerTrainRecordList(String eid) {
        return this.zknContractCourseScheduleMapper.fetchByEid(eid);
    }

    @Transactional
    public void delete(ZkbEngineer zkbEngineer, List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList) {
        zkbEngineerDao.delete(zkbEngineer);
        zkbEngineerTrackRecordDao.delete(zkbEngineerTrackRecordList);
    }

    public ZkbEngineerTrackRecord saveZkbEngineerTrackRecord(ZkbEngineerTrackRecord zkbEngineerTrackRecord) {
        return zkbEngineerTrackRecordDao.save(zkbEngineerTrackRecord);
    }

    public void deleteZkbEngineerTrackRecord(ZkbEngineerTrackRecord zkbEngineerTrackRecord) {
        zkbEngineerTrackRecordDao.delete(zkbEngineerTrackRecord);
    }

    public List<ZkbEngineerTrackRecord> saveZkbEngineerTrackRecordList(List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList, ZkbEngineerTrackRecord currentZkbEngineerTrackRecord) {
        List<ZkbEngineerTrackRecord> tempZkbEngineerTrackRecordList = new ArrayList<>();
        zkbEngineerTrackRecordList.forEach((item) -> {
            if (!item.getId().equals(currentZkbEngineerTrackRecord.getId())) {
                tempZkbEngineerTrackRecordList.add(item);
            }
        });
        tempZkbEngineerTrackRecordList.add(currentZkbEngineerTrackRecord);

        return tempZkbEngineerTrackRecordList;
    }

    public List<ZkbEngineerTrackRecord> deleteZkbEngineerTrackRecordList(List<ZkbEngineerTrackRecord> zkbEngineerTrackRecordList, ZkbEngineerTrackRecord currentZkbEngineerTrackRecord) {
        List<ZkbEngineerTrackRecord> tempZkbEngineerTrackRecordList = new ArrayList<>();
        zkbEngineerTrackRecordList.forEach((item) -> {
            if (!item.getId().equals(currentZkbEngineerTrackRecord.getId())) {
                tempZkbEngineerTrackRecordList.add(item);
            }
        });

        return tempZkbEngineerTrackRecordList;
    }

    /**
     * 保存附件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUploadFile(List<V2_BlobObject> v2_blobObjects, String id) {
        if (CollectionUtils.isNotEmpty(v2_blobObjects)) {
            for (V2_BlobObject v : v2_blobObjects) {
                v.setBoUuid(id);
                blobObjectRepository.save(v);
            }
        }
    }

    /**
     * 移除附件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeFile(List<V2_BlobObject> v2_blobObjects) {
        if (CollectionUtils.isNotEmpty(v2_blobObjects)) {
            for (V2_BlobObject v : v2_blobObjects) {
                blobObjectRepository.delete(v.getId());
            }
        }
    }

    /**
     * 生成SSO索引
     *
     * @param zkbEngineer 当前工程师
     * @return
     */
    public ZkbEngineer fullSso(ZkbEngineer zkbEngineer, String orderPrefix) {
        String yearStr = DateUtil.getFormatStrByDate(new Date(), "yyyy");
        String endIndex = yearStr.substring(2);
        Object o = zkbEngineerRepository.getMaxIndex(orderPrefix + endIndex + "%");
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String sso = orderPrefix + endIndex + String.format("%04d", index);
        zkbEngineer.setSso(sso);
        zkbEngineer.setSsoIndex(index);
        return zkbEngineer;
    }

    @Transactional
    public void save(ZkbEngineer engineer) {
        zkbEngineerRepository.save(engineer);
    }

    public List<ZkbEngineer> handleExcel(UploadedFile file, UserAccount currentUser) {
        List<ZkbEngineer> importFailedDataList = new ArrayList<>();
        Workbook workbook = null;

        try {
            String fileName = file.getFileName().toLowerCase();
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputstream());
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputstream());
            } else {
                ZkbEngineer errorRecord = new ZkbEngineer();
                errorRecord.setErrorMsg("不支持的文件格式，请上传 .xls 或 .xlsx 文件；");
                importFailedDataList.add(errorRecord);
                return importFailedDataList;
            }

            // ===== 第一步：解析工程师基本信息（Sheet 0）=====
            Sheet engineerSheet = workbook.getSheetAt(0);
            if (engineerSheet == null) {
                ZkbEngineer errorRecord = new ZkbEngineer();
                errorRecord.setErrorMsg("未找到工程师信息表；");
                importFailedDataList.add(errorRecord);
                return importFailedDataList;
            }

            List<String> headers = Arrays.asList("租户", "姓名", "电话", "邮箱", "身份证号", "学历", "毕业院校", "专业", "SSO");
            Map<String, Integer> headerMap = ExcelParser.findHeader(engineerSheet, headers);
            if (headerMap.isEmpty()) {
                ZkbEngineer errorRecord = new ZkbEngineer();
                errorRecord.setErrorMsg("未找到有效表头，请检查 Excel 文件；");
                importFailedDataList.add(errorRecord);
                return importFailedDataList;
            }

            int tenantIdx = headerMap.get("租户");
            int nameIdx = headerMap.get("姓名");
            int phoneIdx = headerMap.get("电话");
            int emailIdx = headerMap.get("邮箱");
            int idCardNoIdx = headerMap.get("身份证号");
            int educationalBackgroundIdx = headerMap.get("学历");
            int graduateSchoolIdx = headerMap.get("毕业院校");
            int specialtyIdx = headerMap.get("专业");
            int ssoIdx = headerMap.get("SSO");

            int headerRow = headerMap.get("HEADER_ROW");
            int lastRowNum = engineerSheet.getLastRowNum();

            Map<String, ZkbEngineer> engineerMap = new HashMap<>();
            boolean hasSuccessfulImport = false; // 👈 关键标志：是否有至少一条成功

            for (int i = headerRow + 1; i <= lastRowNum; i++) {
                Row row = engineerSheet.getRow(i);
                if (row == null) continue;

                String name = StringUtils.trim(ExcelParser.getCellStringValue(row, nameIdx));
                String phone = StringUtils.trim(ExcelParser.getCellStringValue(row, phoneIdx));
                String email = StringUtils.trim(ExcelParser.getCellStringValue(row, emailIdx));
                String identityNumber = StringUtils.trim(ExcelParser.getCellStringValue(row, idCardNoIdx));

                ZkbEngineer engineer = new ZkbEngineer();
                engineer.setExcelIdex(i + 1);
                engineer.setName(name);
                engineer.setPhone(phone);
                engineer.setEmail(email);
                engineer.setIdentityNumber(identityNumber);

                boolean valid = true;

                // 必填校验
                if (StringUtils.isEmpty(name)) {
                    engineer.setErrorMsg("姓名不能为空；");
                    valid = false;
                }
                if (StringUtils.isEmpty(phone)) {
                    engineer.setErrorMsg("电话不能为空；");
                    valid = false;
                }
                if (StringUtils.isEmpty(email)) {
                    engineer.setErrorMsg("邮箱不能为空；");
                    valid = false;
                }
                if (StringUtils.isEmpty(identityNumber)) {
                    engineer.setErrorMsg("身份证号不能为空；");
                    valid = false;
                }

                String tenantName = StringUtils.trim(ExcelParser.getCellStringValue(row, tenantIdx));
                if (StringUtils.isEmpty(tenantName)) {
                    engineer.setErrorMsg("租户不能为空；");
                    valid = false;
                }

                if (!valid) {
                    importFailedDataList.add(engineer);
                    continue;
                }

                // 检查重复
                ZkbEngineer existingByCard = zkbEngineerDao.findByIdentityNumber(identityNumber);
                ZkbEngineer existingByNamePhoneEmail = zkbEngineerDao.findByNameAndPhoneAndEmail(name, phone, email);

                if (existingByCard != null || existingByNamePhoneEmail != null) {
                    engineer.setErrorMsg(
                            "该工程师已存在（身份证号或姓名+电话+邮箱重复）");
                    importFailedDataList.add(engineer);
                    continue;
                }

                // 租户校验
                List<TenantInfo> tenantInfoList = tenantInfoDao.findByNameAndType(tenantName, 3);
                if (tenantInfoList == null || tenantInfoList.isEmpty()) {
                    engineer.setErrorMsg(WebUtil.getMessage("该代理商名称不存在！"));
                    importFailedDataList.add(engineer);
                    continue;
                } else if (tenantInfoList.size() > 1) {
                    engineer.setErrorMsg(WebUtil.getMessage("该代理商名称存在多条数据！"));
                    importFailedDataList.add(engineer);
                    continue;
                }

                // 设置字段
                engineer.setIdentityNumber(identityNumber);
                engineer.setEducationalBackground(StringUtils.trim(ExcelParser.getCellStringValue(row, educationalBackgroundIdx)));
                engineer.setGraduateSchool(StringUtils.trim(ExcelParser.getCellStringValue(row, graduateSchoolIdx)));
                engineer.setSpecialty(StringUtils.trim(ExcelParser.getCellStringValue(row, specialtyIdx)));
                if (!StringUtils.isEmpty(StringUtils.trim(ExcelParser.getCellStringValue(row, ssoIdx)))) {
                    engineer.setSso(StringUtils.trim(ExcelParser.getCellStringValue(row, ssoIdx)));
                }
                engineer.setTenantUID(tenantInfoList.get(0).getUid());
                engineer.setCreatedBy(currentUser.getLoginName());
                engineer.setCreatedDate(new Date());
                engineer.setLastModifiedDate(new Date());
                engineer.setLastModifiedBy(currentUser.getLoginName());

                try {
                    zkbEngineerDao.save(engineer);
                    String key = name + "_" + phone + "_" + email;
                    engineerMap.put(key, engineer);
                    hasSuccessfulImport = true; // 👈 标记有成功
                } catch (Exception e) {
                    logger.error("保存工程师失败，行号: {}", i + 1, e);
                    engineer.setErrorMsg("系统保存失败：" + e.getMessage());
                    importFailedDataList.add(engineer);
                }
            }

            // ✅ 关键判断：如果第一页一条都没成功，直接返回，不处理第二页
            if (!hasSuccessfulImport) {
                return importFailedDataList;
            }

            // ===== 第二步：只有当第一页有成功，才处理工作经历（Sheet 1）=====
            if (workbook.getNumberOfSheets() >= 2) {
                Sheet trackSheet = workbook.getSheetAt(1);
                if (trackSheet != null) {
                    List<String> trackHeaders = Arrays.asList("姓名", "电话", "邮箱", "公司名称", "职务", "入职时间", "工作年限");
                    Map<String, Integer> trackHeaderMap = ExcelParser.findHeader(trackSheet, trackHeaders);
                    if (!trackHeaderMap.isEmpty()) {
                        int trackNameIdx = trackHeaderMap.get("姓名");
                        int trackPhoneIdx = trackHeaderMap.get("电话");
                        int trackEmailIdx = trackHeaderMap.get("邮箱");
                        int companyNameIdx = trackHeaderMap.get("公司名称");
                        int positionIdx = trackHeaderMap.get("职务");
                        int hireDateIdx = trackHeaderMap.get("入职时间");
                        int workSeniorityIdx = trackHeaderMap.get("工作年限");

                        int trackHeaderRow = trackHeaderMap.get("HEADER_ROW");
                        int trackLastRowNum = trackSheet.getLastRowNum();

                        for (int i = trackHeaderRow + 1; i <= trackLastRowNum; i++) {
                            Row row = trackSheet.getRow(i);
                            if (row == null) continue;

                            try {
                                String name = StringUtils.trim(ExcelParser.getCellStringValue(row, trackNameIdx));
                                String phone = StringUtils.trim(ExcelParser.getCellStringValue(row, trackPhoneIdx));
                                String email = StringUtils.trim(ExcelParser.getCellStringValue(row, trackEmailIdx));

                                if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(email)) {
                                    continue;
                                }

                                String key = name + "_" + phone + "_" + email;
                                ZkbEngineer engineer = engineerMap.get(key);

                                if (engineer == null) {
                                    // 只有在第一页有成功的情况下才会走到这里
                                    ZkbEngineer errorRecord = new ZkbEngineer();
                                    errorRecord.setExcelIdex(i + 1);
                                    errorRecord.setName(name);
                                    errorRecord.setPhone(phone);
                                    errorRecord.setEmail(email);
                                    errorRecord.setErrorMsg("无法找到对应工程师");
                                    importFailedDataList.add(errorRecord);
                                    continue;
                                }

                                String companyName = StringUtils.trim(ExcelParser.getCellStringValue(row, companyNameIdx));
                                String position = StringUtils.trim(ExcelParser.getCellStringValue(row, positionIdx));
                                Date hireDate = ExcelParser.getCellDateValue(row, hireDateIdx, new SimpleDateFormat("M/d/yyyy"));
                                String workSeniorityStr = StringUtils.trim(ExcelParser.getCellStringValue(row, workSeniorityIdx));

                                // 防重：避免重复插入相同工作经历
                                List<ZkbEngineerTrackRecord> existingRecords = zkbEngineerTrackRecordDao.findByZkbEngineerIdAndCompanyNameAndPositionAndHireDate(
                                        engineer.getId(), companyName, position, hireDate
                                );
                                if (!existingRecords.isEmpty()) {
                                    continue;
                                }

                                ZkbEngineerTrackRecord record = new ZkbEngineerTrackRecord();
                                record.setZkbEngineerId(engineer.getId());
                                record.setCompanyName(companyName);
                                record.setPosition(position);
                                record.setHireDate(hireDate);

                                if (StringUtils.isNotBlank(workSeniorityStr)) {
                                    try {
                                        record.setWorkSeniority(Integer.parseInt(workSeniorityStr));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }

                                zkbEngineerTrackRecordDao.save(record);

                            } catch (Exception e) {
                                logger.error("解析工作经历第 {} 行异常", i + 1, e);
                                ZkbEngineer errorRecord = new ZkbEngineer();
                                errorRecord.setExcelIdex(i + 1);
                                errorRecord.setErrorMsg("工作经历解析失败：" + e.getMessage());
                                importFailedDataList.add(errorRecord);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("读取 Excel 文件失败", e);
            ZkbEngineer engineer = new ZkbEngineer();
            engineer.setErrorMsg("文件读取失败：" + e.getMessage());
            importFailedDataList.add(engineer);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    logger.warn("关闭 workbook 失败", e);
                }
            }
        }

        return importFailedDataList;
    }

    public List<ZkbTrainAttendanceRecord> getZkbTrainAttendanceRecordList(String id) {
        return zkbTrainAttendanceRecordRepository.findAllByEngineerId(id);

    }
}
