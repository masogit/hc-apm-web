package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbEngineerRepository;
import com.ge.apm.dao.ZkbTrainAttendanceRecordRepository;
import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelParser;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class ZkbTrainAttendanceRecordService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbTrainAttendanceRecordService.class);

    @Autowired
    private ZkbTrainAttendanceRecordRepository dao;

    @Autowired
    private ZkbContractRepository zkbContractRepository;

    @Autowired
    private ZkbEngineerRepository zkbEngineerRepository;

    @Autowired
    private ZkbTrainPublishRepository zkbTrainPublishRepository;

    @Autowired
    private ZkbTrainCourseQuotaService zkbTrainCourseQuotaService;

    private final List<String> statusList = Arrays.asList("Attended", "Unattended");

    public List<ZkbTrainAttendanceRecord> handleExcel(UploadedFile file, UserAccount currentUser) {
        List<ZkbTrainAttendanceRecord> importFailedDataList = new ArrayList<>();
        Workbook workbook = null;

        try {
            String fileName = file.getFileName().toLowerCase();
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputstream());
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputstream());
            } else {
                // 不支持的格式
                ZkbTrainAttendanceRecord errorRecord = new ZkbTrainAttendanceRecord();
                errorRecord.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "不支持的文件格式，请上传 .xls 或 .xlsx 文件；");
                importFailedDataList.add(errorRecord);
                return importFailedDataList;
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return importFailedDataList;
            }

            // 定义导入 Excel 表头
            List<String> headers = Arrays.asList(
                    "Status", "原因", "学员类型", "开始时间", "结束时间", "课程名称", "Modality",
                    "培训老师", "培训形式", "培训地点", "公司", "合同号", "合同唯一号/Unic ID",
                    "姓名", "SSO", "电话", "邮箱", "身份证号", "商备邮件",
                    "Online", "Lab+笔试", "Final", "Degree", "是否通过"
            );

            Map<String, Integer> headerMap = ExcelParser.findHeader(sheet, headers);
            if (headerMap.isEmpty()) {
                ZkbTrainAttendanceRecord errorRecord = new ZkbTrainAttendanceRecord();
                errorRecord.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "未找到有效表头，请检查 Excel 文件；");
                importFailedDataList.add(errorRecord);
                return importFailedDataList;
            }

            // 获取各列索引
            int statusIdx = headerMap.get("Status");
            int reasonIdx = headerMap.get("原因");
            int traineeTypeIdx = headerMap.get("学员类型");
            int startDateIdx = headerMap.get("开始时间");
            int endDateIdx = headerMap.get("结束时间");
            int courseNameIdx = headerMap.get("课程名称");
            int modalityIdx = headerMap.get("Modality");
            int trainerNameIdx = headerMap.get("培训老师");
            int trainFormatIdx = headerMap.get("培训形式");
            int locationIdx = headerMap.get("培训地点");
            int companyIdx = headerMap.get("公司");
            int contractNoIdx = headerMap.get("合同号");
            int contractUniqueIdIdx = headerMap.get("合同唯一号/Unic ID");
            int traineeNameIdx = headerMap.get("姓名");
            int ssoIdx = headerMap.get("SSO");
            int phoneIdx = headerMap.get("电话");
            int emailIdx = headerMap.get("邮箱");
            int idCardNoIdx = headerMap.get("身份证号");
            int businessEmailIdx = headerMap.get("商备邮件");
            int onlineScoreIdx = headerMap.get("Online");
            int labWrittenScoreIdx = headerMap.get("Lab+笔试");
            int finalScoreIdx = headerMap.get("Final");
            int degreeIdx = headerMap.get("Degree");
            int isPassedIdx = headerMap.get("是否通过");

            int headerRow = headerMap.get("HEADER_ROW");
            int lastRowNum = sheet.getLastRowNum();

            for (int i = headerRow + 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ZkbTrainAttendanceRecord record = new ZkbTrainAttendanceRecord();
                record.setExcelIdex(i + 1);
                boolean valid = true;

                try {
                    // 读取并 trim 字符串
                    String status = StringUtils.trim(ExcelParser.getCellStringValue(row, statusIdx));
                    String reason = StringUtils.trim(ExcelParser.getCellStringValue(row, reasonIdx));
                    String traineeType = StringUtils.trim(ExcelParser.getCellStringValue(row, traineeTypeIdx));
                    String courseName = StringUtils.trim(ExcelParser.getCellStringValue(row, courseNameIdx));
                    String modality = StringUtils.trim(ExcelParser.getCellStringValue(row, modalityIdx));
                    String trainerName = StringUtils.trim(ExcelParser.getCellStringValue(row, trainerNameIdx));
                    String trainFormat = StringUtils.trim(ExcelParser.getCellStringValue(row, trainFormatIdx));
                    String location = StringUtils.trim(ExcelParser.getCellStringValue(row, locationIdx));
                    String companyName = StringUtils.trim(ExcelParser.getCellStringValue(row, companyIdx));
                    String contractNo = StringUtils.trim(ExcelParser.getCellStringValue(row, contractNoIdx));
                    String contractUniqueId = StringUtils.trim(ExcelParser.getCellStringValue(row, contractUniqueIdIdx));
                    String traineeName = StringUtils.trim(ExcelParser.getCellStringValue(row, traineeNameIdx));
                    String sso = StringUtils.trim(ExcelParser.getCellStringValue(row, ssoIdx));
                    String phone = StringUtils.trim(ExcelParser.getCellStringValue(row, phoneIdx));
                    String email = StringUtils.trim(ExcelParser.getCellStringValue(row, emailIdx));
                    String idCardNo = StringUtils.trim(ExcelParser.getCellStringValue(row, idCardNoIdx));
                    String businessEmail = StringUtils.trim(ExcelParser.getCellStringValue(row, businessEmailIdx));
                    String onlineScore = StringUtils.trim(ExcelParser.getCellStringValue(row, onlineScoreIdx));
                    String labWrittenScore = StringUtils.trim(ExcelParser.getCellStringValue(row, labWrittenScoreIdx));
                    String finalScoreStr = StringUtils.trim(ExcelParser.getCellStringValue(row, finalScoreIdx));
                    String degree = StringUtils.trim(ExcelParser.getCellStringValue(row, degreeIdx));
                    String isPassed = StringUtils.trim(ExcelParser.getCellStringValue(row, isPassedIdx));

                    // 日期解析
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Date startDate = ExcelParser.getCellDateValue(row, startDateIdx, sdf);
                    Date endDate = ExcelParser.getCellDateValue(row, endDateIdx, sdf);

                    // 必填校验
                    if (StringUtils.isEmpty(status)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "Status 不能为空；");
                        valid = false;
                    }
                    if (StringUtils.isEmpty(courseName)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程名称不能为空；");
                        valid = false;
                    }
                    if (StringUtils.isEmpty(phone)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "电话不能为空；");
                        valid = false;
                    }
                    if (StringUtils.isEmpty(sso)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "sso不能为空；");
                        valid = false;
                    }
                    if (StringUtils.isEmpty(traineeName)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "学员姓名不能为空；");
                        valid = false;
                    }
                    if (StringUtils.isEmpty(contractNo)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "合同号不能为空；");
                        valid = false;
                    }
                    if (startDate == null) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "培训开始时间不能为空；");
                        valid = false;
                    }
                    if (endDate == null) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "培训结束时间不能为空；");
                        valid = false;
                    }

                    // Final 成绩数字校验
                    Double finalScore = null;
                    if (StringUtils.isNotEmpty(finalScoreStr)) {
                        try {
                            finalScore = Double.parseDouble(finalScoreStr.trim());
                        } catch (NumberFormatException e) {
                            record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "Final 成绩必须为数值型；");
                            valid = false;
                        }
                    }

                    // 状态校验
                    if (!statusList.contains(status)) {
                        record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "状态只能是：Attended 或 Unattended；");
                        valid = false;
                    }

                    // 设置字段
                    record.setStatus(status);
                    record.setReason(reason);
                    record.setTraineeType(traineeType);
                    record.setTrainStartDate(startDate);
                    record.setTrainEndDate(endDate);
                    record.setCourseName(courseName);
                    record.setModality(modality);
                    record.setTrainerName(trainerName);
                    record.setTrainFormat(trainFormat);
                    record.setLocation(location);
                    record.setCompanyName(companyName);
                    record.setContractNo(contractNo);
                    record.setContractUniqueId(contractUniqueId);
                    record.setTraineeName(traineeName);
                    record.setSso(sso);
                    record.setPhone(phone);
                    record.setEmail(email);
                    record.setIdCardNo(idCardNo);
                    record.setBusinessEmail(businessEmail);
                    record.setOnlineScore(onlineScore);
                    record.setLabWrittenScore(labWrittenScore);
                    record.setFinalScore(finalScore);
                    record.setDegree(degree);
                    record.setIsPassed(isPassed);

                    record.setCreatedBy(currentUser.getLoginName());
                    record.setCreatedDate(new Date());
                    record.setLastModifiedDate(new Date());
                    record.setLastModifiedBy(currentUser.getLoginName());

                    if (!valid) {
                        importFailedDataList.add(record);
                    } else {
                        // 设置代理商租户Id
                        ZkbContract zkbContract = zkbContractRepository.findFirstByContractNo(contractNo);
                        if (zkbContract != null) {
                            record.setTenantUid(zkbContract.getTenantUID());
                        }
                        // 设置课程发布Id
                        ZkbTrainPublishInfo zkbTrainPublishInfo = zkbTrainPublishRepository.findBySelectedInfo(courseName, startDate, endDate);
                        if (zkbTrainPublishInfo != null) {
                            record.setPublishId(zkbTrainPublishInfo.getId());
                        }
                        // 保存成功记录
                        dao.save(record);
                        // 更新工程师SSO
                        this.writeSsoToEngineer(record.getPhone(), record.getSso());
                    }

                } catch (Exception e) {
                    logger.error("解析第 {} 行数据异常: {}", i + 1, e.getMessage(), e);
                    record.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "系统异常：" + e.getMessage());
                    importFailedDataList.add(record);
                }
            }

        } catch (IOException e) {
            logger.error("读取 Excel 文件失败", e);
            ZkbTrainAttendanceRecord errorRecord = new ZkbTrainAttendanceRecord();
            errorRecord.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "文件读取失败：" + e.getMessage());
            importFailedDataList.add(errorRecord);
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

    public List<HashMap<String, String>> queryNormalRecords() {
        // 实现查询逻辑
        return null;
    }

    public void saveRecord(ZkbTrainAttendanceRecord selected, UserAccount currentUser) {
        if (selected.getId() == null) {
            selected.setCreatedDate(new Date());
            selected.setCreatedBy(currentUser.getLoginName());
        }
        // 设置代理商租户Id
        ZkbContract zkbContract = zkbContractRepository.findFirstByContractNo(selected.getContractNo());
        if (zkbContract != null) {
            selected.setTenantUid(zkbContract.getTenantUID());
        }

        // 设置课程发布Id
        ZkbTrainPublishInfo zkbTrainPublishInfo = zkbTrainPublishRepository.findBySelectedInfo(selected.getCourseName(), selected.getTrainStartDate(), selected.getTrainEndDate());
        if (zkbTrainPublishInfo != null) {
            selected.setPublishId(zkbTrainPublishInfo.getId());
        }
        selected.setLastModifiedDate(new Date());
        selected.setLastModifiedBy(currentUser.getLoginName());

        dao.save(selected);

        // 更新工程师SSO
        this.writeSsoToEngineer(selected.getPhone(), selected.getSso());

        // 关键修改：处理未参加状态
        if ("Unattended".equals(selected.getStatus())) {
            processUnattendedRecord(selected, zkbTrainPublishInfo, currentUser);
        }
    }

    private void processUnattendedRecord(ZkbTrainAttendanceRecord selected, ZkbTrainPublishInfo zkbTrainPublishInfo, UserAccount currentUser) {

        // 验证必要条件
        if (StringUtils.isEmpty(selected.getTenantUid()) || zkbTrainPublishInfo == null) {
            logger.warn("TenantUid or course info missing for unattended record: {}", selected.getId());
            return;
        }

        List<ZkbEngineer> engineerList = zkbEngineerRepository.findByPhone(selected.getPhone());
        if (CollectionUtils.isEmpty(engineerList)) {
            logger.warn("No engineer found for phone: {}", selected.getPhone());
            return;
        }

        String engineerId = engineerList.get(0).getId();

        // 3. 精确返还对应工程师的配额
        zkbTrainCourseQuotaService.adjustQuota(selected.getTenantUid(), zkbTrainPublishInfo.getCourseId(), 1, Collections.singletonList(engineerId), currentUser);
        logger.info("Returned quota for engineer: {}, course: {}", engineerId, zkbTrainPublishInfo.getCourseId());
    }

    private void writeSsoToEngineer(String phone, String sso) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(sso)) {
            return;
        }
        List<ZkbEngineer> engineerList = zkbEngineerRepository.findByPhone(phone);
        if (engineerList == null || engineerList.isEmpty()) {
            return; // 无匹配工程师，直接返回
        }

        boolean updated = false;
        for (ZkbEngineer engineer : engineerList) {
            if (StringUtils.isEmpty(engineer.getSso())) {
                engineer.setSso(sso);
                updated = true;
            }
        }

        // 只有在有更新的情况下才保存
        if (updated) {
            zkbEngineerRepository.save(engineerList);
        }
    }
}