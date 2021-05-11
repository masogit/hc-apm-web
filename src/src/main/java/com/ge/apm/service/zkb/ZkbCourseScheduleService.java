package com.ge.apm.service.zkb;


import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.dao.ZkbCourseScheduleRepository;
import com.ge.apm.domain.ZkbContractCourseSchedule;
import com.ge.apm.domain.ZkbContractCourseScheduleVO;
import com.ge.apm.domain.ZkbCourse;
import com.ge.apm.service.asset.AssetInfoRow;
import com.ge.apm.service.asset.ZkbContractCourseScheduleRow;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.zkb.ZkbCourseScheduleController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 课程计划表service
 */
@Component
public class ZkbCourseScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbOrderHistoryService.class);

    @Autowired
    private ZkbCourseScheduleRepository zkbCourseScheduleRepository;

    /**
     * 根据excel文件 读取文件中的数据
     * @param doc 文件
     * @return
     * @throws Exception
     */

    /**
     * 模板数据起始行数
     */
    public static final int EXCEL_ORDER_HISTORY_DATA_ROW = 2;

    /**
     * excel sheet 名称
     */
    public static final String SHEET_SERVICE_COURSE_SCHEDULE = "课程计划";

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_COURSE_SCHEDULE_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_COURSE_SCHEDULE_IMPORT_MAX_ROW = 10000;

    /**
     * 目录地址
     */
    public static final String COURSE_SCHEDULE_PATH="/portal/zkb/courseSchedule/";

    @Autowired
    private ZkbCourseRepository zkbCourseRepository;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    @Autowired
    private ZkbFePartOrderService zkbFePartOrderService;

    public List<Map<String, Object>> getZkbCourseScheduleMapFromTemplate(ExcelDocument doc) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_ORDER_HISTORY_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_COURSE_SCHEDULE).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_COURSE_SCHEDULE, EXCEL_COURSE_SCHEDULE_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbContractCourseScheduleVO zkbContractCourseScheduleVO = new ZkbContractCourseScheduleVO();
            zkbContractCourseScheduleVO.setLineNum(i);
            ZkbContractCourseScheduleRow zkbContractCourseScheduleRow = new ZkbContractCourseScheduleRow(zkbContractCourseScheduleVO,rowMap);
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_cid"), "setCid");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_cname"), "setCname");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_modality"), "setModality");
            zkbContractCourseScheduleRow.setFieldDateValue(WebUtil.getMessage("zkbContractCourseSchedule_startDate"),"setStartDate");
            zkbContractCourseScheduleRow.setFieldDateValue(WebUtil.getMessage("zkbContractCourseSchedule_endDate"),"setEndDate");
            zkbContractCourseScheduleRow.setFieldIntegerValue(WebUtil.getMessage("zkbContractCourseSchedule_trainingDays"), "setTrainingDays");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_instructor"), "setInstructor");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_audience"), "setAudience");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_location"), "setLocation");
            zkbContractCourseScheduleRow.setFieldIntegerValue(WebUtil.getMessage("zkbContractCourseSchedule_studentClass"), "setStudentClass");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_batchNo"), "setBatchNo");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_valid"), "setValid");
            zkbContractCourseScheduleRow.setFieldDoubleValue(WebUtil.getMessage("zkbContractCourseSchedule_deductCount"), "setDeductCount");
            zkbContractCourseScheduleRow.setFieldStringValue(WebUtil.getMessage("zkbContractCourseSchedule_type"), "setType");
            if("是".equals(zkbContractCourseScheduleVO.getValid()))zkbContractCourseScheduleVO.setValid("t");
            if("否".equals(zkbContractCourseScheduleVO.getValid()))zkbContractCourseScheduleVO.setValid("f");
            if("线下".equals(zkbContractCourseScheduleVO.getType()))zkbContractCourseScheduleVO.setType("offline");
            if("线上".equals(zkbContractCourseScheduleVO.getType()))zkbContractCourseScheduleVO.setType("online");

            if(zkbContractCourseScheduleVO.isNull()){
                break;
            }
            map.put("zkbContractCourseScheduleVO", zkbContractCourseScheduleVO);
            map.put("rowData", rowMap);
            map.put("status", zkbContractCourseScheduleVO.getId()==null ? ZkbOrderHistoryService.ImportStatus.New.name() : ZkbOrderHistoryService.ImportStatus.Exist.name());
            map.put("hashCode", zkbContractCourseScheduleVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getOrderHistoryMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }

    /**
     * 导出
     * @param zkbContractCourseScheduleList
     * @return
     */
    public List<Map<String, Object>> createExportCourseScheduleData(List<ZkbContractCourseSchedule> zkbContractCourseScheduleList){
        List<Map<String, Object>> zkbContractCourseScheduleMapList = new ArrayList<>();
        if(CollectionUtils.isEmpty(zkbContractCourseScheduleList))
        {
            return zkbContractCourseScheduleMapList;
        }
        for (ZkbContractCourseSchedule zkbContractCourseSchedule : zkbContractCourseScheduleList) {
            Map<String, Object> zkbContractCourseScheduleMap = new HashMap<>();
            String valid = "";
            if("t".equals(zkbContractCourseSchedule.getValid()))
            {
                valid="是";
            }else if("f".equals(zkbContractCourseSchedule.getValid()))
            {
                valid="否";
            }
            String type = "";
            if("offline".equals(zkbContractCourseSchedule.getType()))
            {
                type="线下";
            }else if("online".equals(zkbContractCourseSchedule.getType()))
            {
                type="线上";
            }
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_cid"),zkbContractCourseSchedule.getCid());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_cname"),zkbContractCourseSchedule.getCname());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_modality"),zkbContractCourseSchedule.getModality());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_startDate"),zkbContractCourseSchedule.getStartDate());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_endDate"),zkbContractCourseSchedule.getEndDate());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_trainingDays"),zkbContractCourseSchedule.getTrainingDays());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_instructor"),zkbContractCourseSchedule.getInstructor());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_audience"),zkbContractCourseSchedule.getAudience());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_location"),zkbContractCourseSchedule.getLocation());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_studentClass"),zkbContractCourseSchedule.getStudentClass());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_batchNo"),zkbContractCourseSchedule.getBatchNo());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_valid"),valid);
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_deductCount"),zkbContractCourseSchedule.getDeductCount());
            zkbContractCourseScheduleMap.put(WebUtil.getMessage("zkbContractCourseSchedule_type"),type);
            zkbContractCourseScheduleMapList.add(zkbContractCourseScheduleMap);
        }
        return zkbContractCourseScheduleMapList;
    }

    /**
     * 导入数据
     * @param importCourseScheduleMapList 课程计划信息
     */
    public void importData(List<Map<String, Object>> importCourseScheduleMapList) {
        List<ZkbCourse> allZkbCourseList =  this.zkbCourseRepository.find();
        Map<String,ZkbCourse> allZkbCourseMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(allZkbCourseList))
        {
            for(ZkbCourse item:allZkbCourseList){
                if(item==null) continue;
                if(allZkbCourseMap.containsKey(item.getCid())) continue;
                allZkbCourseMap.put(item.getCid(),item);
            }
        }
        LocalDateTime startTime = LocalDateTime.now();
        StringBuffer ids = new StringBuffer();
        int row = 0;
        try {
            for (Map<String, Object> item : importCourseScheduleMapList) {
                ++row;
                if(checkData(item,allZkbCourseMap)) {
                    ZkbContractCourseScheduleVO zkbContractCourseScheduleVO = (ZkbContractCourseScheduleVO)item.get("zkbContractCourseScheduleVO");
                    ZkbContractCourseSchedule zkbContractCourseSchedule = new ZkbContractCourseSchedule();
                    if(allZkbCourseMap.containsKey(zkbContractCourseScheduleVO.getCid()))
                    {
                        ZkbCourse zkbCourse = allZkbCourseMap.get(zkbContractCourseScheduleVO.getCid());
                        zkbContractCourseScheduleVO.setCname(zkbCourse.getCname());
                        zkbContractCourseScheduleVO.setModality(zkbCourse.getModality());
                    }
                    BeanUtils.copyProperties(zkbContractCourseScheduleVO, zkbContractCourseSchedule);
                    this.zkbCourseScheduleRepository.save(zkbContractCourseSchedule);
                    if(ZkbCourseScheduleController.t.equals(zkbContractCourseSchedule.getValid()))
                    {
                        ids=ids.append(zkbContractCourseSchedule.getId()).append(",");
                    }
                }
            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
        String idsStr = ids.toString();
        if(StringUtils.isNotBlank(idsStr))
        {
            idsStr = idsStr.substring(0,idsStr.length()-1);
            this.zkbFePartOrderService.sendMessage(idsStr, GatewayURL.CONTRACT_COURSE_SCHEDULE_MESSAGE);
        }
    }

    /**
     * 检查数据是否合法
     * 1.数据类型字段必填
     * @param item
     * @param allZkbCourseMap 所有课程MAP
     * @return
     */
    public boolean checkData(Map<String, Object> item,Map<String,ZkbCourse> allZkbCourseMap) {
        boolean isValidateData = true;
        ZkbContractCourseScheduleVO zkbContractCourseScheduleVO = (ZkbContractCourseScheduleVO)item.get("zkbContractCourseScheduleVO");
        AssetInfoRow.clearErrMessage(item);
        String errorMessage = "不能为空";
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getCid())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_cid")+errorMessage);
            isValidateData = false;
        }
        if(zkbContractCourseScheduleVO.getStartDate()==null){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_startDate")+errorMessage);
            isValidateData = false;
        }
        if(zkbContractCourseScheduleVO.getEndDate()==null){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_endDate")+errorMessage);
            isValidateData = false;
        }
        if(zkbContractCourseScheduleVO.getTrainingDays()==null){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_trainingDays")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getInstructor())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_instructor")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getAudience())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_audience")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getLocation())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_location")+errorMessage);
            isValidateData = false;
        }
        if(zkbContractCourseScheduleVO.getStudentClass()==null){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_studentClass")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getBatchNo())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_batchNo")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getValid())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_valid")+errorMessage);
            isValidateData = false;
        }
        if(zkbContractCourseScheduleVO.getDeductCount()==null){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_deductCount")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getType())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_type")+errorMessage);
            isValidateData = false;
        }
        if(StringUtils.isBlank(zkbContractCourseScheduleVO.getType())){
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_type")+errorMessage);
            isValidateData = false;
        }
        if(!allZkbCourseMap.containsKey(zkbContractCourseScheduleVO.getCid()))
        {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbContractCourseSchedule_cid")+"不存在");
            isValidateData = false;
        }
        return isValidateData;
    }

    /**
     * @param course
     * @return
     */
    public boolean isCourseUsed(ZkbCourse course) {
        List<ZkbContractCourseSchedule> list = zkbCourseScheduleRepository.findByZkbCourseId(course.getId());
        if(CollectionUtils.isNotEmpty(list)){
            return true;
        }
        return false;
    }

    /**
     * 整理错误信息
     * @param importCourseScheduleMapList
     * @return
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importCourseScheduleMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importCourseScheduleMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbCourseScheduleService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbCourseScheduleService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> courseScheduleMapList) {
        Integer dataRow = 2;
        for (Map<String, Object> item : courseScheduleMapList) {
            doc.writeRowData(SHEET_SERVICE_COURSE_SCHEDULE, 1, dataRow++, item);
        }
    }

}
