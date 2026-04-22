package com.ge.apm.service.his.load;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.HisEquipmentConnectRepository;
import com.ge.apm.dao.HisEquipmentDataSourceRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.HisEquipmentDataSourceMapper;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentDataSource;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.ContractRow;
import com.ge.apm.service.his.HisEquipmentDataSourceRow;
import com.ge.apm.service.his.HisEquipmentRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.mq.RabbitConfig;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HisEquipmentDataSourceImportService {
    private static final Logger logger = LoggerFactory.getLogger(HisEquipmentDataSourceImportService.class);


    public static final String SHEET_SERVICE_CONTRACT = "HisEquipmentDataSource List";
    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACT_DATA_ROW = 2;


    @Autowired
    HisEquipmentDataSourceRepository hisEquipmentDataSourceRepository;

    @Autowired
    private HisEquipmentDataSourceMapper hisEquipmentDataSourceMapper;
    @Autowired
    private OrgInfoRepository orgInfoDao;

    @Autowired
    private MqUtils mqUtils;
    

    public List<HisEquipmentDataSource> saveAll(List<HisEquipmentDataSource> data){
        Iterable<HisEquipmentDataSource> iterable = hisEquipmentDataSourceRepository.save(data);

        return Lists.newArrayList(iterable);
    }

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {

        List<Map<String, Object>> result = new ArrayList<>();

        //for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, dataRow); rowMap.size() > 0 ; rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, ++dataRow)) {
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            HisEquipmentDataSource hisEquipmentDataSource = new HisEquipmentDataSource();


            hisEquipmentDataSource.setLineNum(i);

            HisEquipmentDataSourceRow hisEquipmentDataSourceRow = new HisEquipmentDataSourceRow(hisEquipmentDataSource, rowMap);

            hisEquipmentDataSourceRow.setFieldStringValue(WebUtil.getMessage("EqId"), "setDeviceCode");
            hisEquipmentDataSourceRow.setFieldStringValue(WebUtil.getMessage("HospitalCode"), "setOrgCode");
            hisEquipmentDataSourceRow.setFieldStringValue("病人住院号", "setPatientRegistrationNum");
            hisEquipmentDataSourceRow.setFieldStringValue("医嘱号", "setPrescriptionNum");
            hisEquipmentDataSourceRow.setFieldStringValue("收费项目代码", "setDeviceDeptCode");
            hisEquipmentDataSourceRow.setFieldIntegerValue("收费数量", "setReceiptNum");
            hisEquipmentDataSourceRow.setFieldStringValue("单位", "setUnit");
            hisEquipmentDataSourceRow.setFieldDoubleValue("单价", "setUnitPrice");
            hisEquipmentDataSourceRow.setFieldDoubleValue("总金额", "setTotalAmount");
            hisEquipmentDataSourceRow.setFieldDateValue("执行时间", "setExecTime");
            hisEquipmentDataSourceRow.setFieldStringValue("执行科室", "setExecDept");
            hisEquipmentDataSourceRow.setFieldStringValue("执行科室代码", "setExecDeptCode");
            hisEquipmentDataSourceRow.setFieldStringValue("检查部位", "setExamBodyPart");
            hisEquipmentDataSourceRow.setFieldStringValue("来源", "setPatientType");
            hisEquipmentDataSourceRow.setFieldStringValue("系统来源", "setSourceCode");
            map.put("hisEquipmentDataSource", hisEquipmentDataSource);
            map.put("rowData", rowMap);
            map.put("status",  ImportStatus.New.name());

            result.add(map);
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importMapList) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map: importMapList) {

            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (ImportStatus.CheckFail.name().equals(importStatus) || ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }

        }

        return result;
    }

    public void importData(List<Map<String, Object>> importMapList) {

        List<SearchFilter> orgInfoFilters = new ArrayList<>();
        orgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        orgInfoFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.IsNotNull,null));
        List<OrgInfo> orgInfoList = orgInfoDao.findBySearchFilter(orgInfoFilters);
        List<String> orgCodeList = orgInfoList.stream().map(OrgInfo::getOrgCode).collect(Collectors.toList());
        Map<String,OrgInfo> map =  orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getOrgCode,each->each,(value1, value2) -> value1));
        List<HisEquipmentDataSource>  hisEquipmentDataSource=hisEquipmentDataSourceMapper.selectByTenantUidGroup(UserContextService.getCurrentUserAccount().getTenantUID());
        Map<String, List<HisEquipmentDataSource>> collect = hisEquipmentDataSource.stream().collect(Collectors.groupingBy(HisEquipmentDataSource::getOrgCode));
        int row = 0;
        try {
            for (Map<String, Object> item : importMapList) {
                ++row;
                if(checkData(item, importMapList,orgCodeList,collect,map)) {
//                    importOneHisEquipment(item);
                }

            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    private boolean checkData(Map<String, Object> item, List<Map<String, Object>> importMapList,List<String> orgCodeList , Map<String, List<HisEquipmentDataSource>> collect, Map<String,OrgInfo> map) {


        boolean isValidateData = true;
        HisEquipmentDataSource hisEquipmentDataSource = (HisEquipmentDataSource)item.get("hisEquipmentDataSource");
        ContractRow.clearErrMessage(item);

        if(hisEquipmentDataSource.getOrgCode() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "医院机构代码不能为空");
            isValidateData = false;
        }
//        if(hisEquipmentDataSource.getPatientRegistrationNum() == null){
//            item.put("status", ImportStatus.Failure);
//            ContractRow.addErrMessage(item, "病人住院号或门诊号不能为空");
//            isValidateData = false;
//        }
//        if(hisEquipmentDataSource.getPrescriptionNum() == null){
//            item.put("status", ImportStatus.Failure);
//            ContractRow.addErrMessage(item, "医嘱号、处方号、申请单号不能为空");
//            isValidateData = false;
//        }
        if(hisEquipmentDataSource.getDeviceDeptCode() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "收费项目代码不能为空");
            isValidateData = false;
        }

        if(hisEquipmentDataSource.getReceiptNum() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "收费数量不能为空");
            isValidateData = false;
        }
//        if(hisEquipmentDataSource.getUnit() == null){
//            item.put("status", ImportStatus.Failure);
//            ContractRow.addErrMessage(item, "单位不能为空");
//            isValidateData = false;
//        }
//        if(hisEquipmentDataSource.getUnitPrice() == null){
//            item.put("status", ImportStatus.Failure);
//            ContractRow.addErrMessage(item, "单价不能为空");
//            isValidateData = false;
//        }
        if(hisEquipmentDataSource.getTotalAmount() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "总金额不能为空");
            isValidateData = false;
        }
        if(hisEquipmentDataSource.getExecTime() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "执行时间不能为空");
            isValidateData = false;
        }
//        if(hisEquipmentDataSource.getExecDept() == null){
//            item.put("status", ImportStatus.Failure);
//            ContractRow.addErrMessage(item, "执行科室不能为空");
//            isValidateData = false;
//        }
        if(hisEquipmentDataSource.getSourceCode() != null && hisEquipmentDataSource.getExamBodyPart() == null &&  !"HIS".equals(hisEquipmentDataSource.getSourceCode())  ){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "检查部位不能为空");
            isValidateData = false;
        }
        if(hisEquipmentDataSource.getSourceCode() == null){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "系统来源不能为空");
            isValidateData = false;
        }
        if(hisEquipmentDataSource.getSourceCode() != null &&  "HIS".equals(hisEquipmentDataSource.getSourceCode()) && hisEquipmentDataSource.getExecDeptCode()==null ){
            item.put("status", ImportStatus.Failure);
            ContractRow.addErrMessage(item, "HIS数据执行科室代码不能为空");
            isValidateData = false;
        }
//        if(!CollectionUtils.isEmpty(collect.get(hisEquipmentDataSource.getOrgCode())) &&  hisEquipmentDataSource.getExecTime() != null  ){
//            List<HisEquipmentDataSource> hisEquipmentDataSources = collect.get(hisEquipmentDataSource.getOrgCode());
//            if( hisEquipmentDataSource.getExecTime().before(hisEquipmentDataSources.get(0).getExecTime())){
//                item.put("status", ImportStatus.Failure);
//                ContractRow.addErrMessage(item, "已经导入此时间段数据");
//                isValidateData = false;
//            }
//        }
        if(StringUtils.isNotBlank(hisEquipmentDataSource.getOrgCode())){
            if(!orgCodeList.contains(hisEquipmentDataSource.getOrgCode())){
                item.put("status", HisEquipmentImportService.ImportStatus.Failure);
                ContractRow.addErrMessage(item, "租户下面不存在该医院机构代码");
                isValidateData = false;
            }else{
                OrgInfo orgInfo = map.get(hisEquipmentDataSource.getOrgCode());
                hisEquipmentDataSource.setSiteUid(orgInfo.getSiteUID());
                hisEquipmentDataSource.setTenantUid(orgInfo.getTenantUID());
            }
        }
        return isValidateData;

    }

    private void importOneHisEquipment(Map<String, Object> item) {

        HisEquipmentDataSource hisEquipmentDataSource = (HisEquipmentDataSource)item.get("hisEquipmentDataSource");
        //ContractRow.clearErrMessage(item);
        //item.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.Created.name() : ImportStatus.Exist.name());
//        hisEquipmentDataSource.setCreatedDate(new Date());
        hisEquipmentDataSource.setIsMatched(Boolean.FALSE);
        hisEquipmentDataSource.setRecordCount(0);
        hisEquipmentDataSource.setCreateDate(new Date());
        hisEquipmentDataSourceRepository.save(hisEquipmentDataSource);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDBAndToEs(List<Map<String, Object>> importMapList) {
        List<HisEquipmentDataSource> list = importMapList.stream()
                .filter(map ->    (map.get("status")==null ) ||            (   !ImportStatus.CheckFail.name().equals(map.get("status").toString()) && !ImportStatus.Failure.name().equals(map.get("status").toString())  ))
                .map(map -> (HisEquipmentDataSource) map.get("hisEquipmentDataSource"))
                .collect(Collectors.toCollection(ArrayList::new));
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.stream().forEach(item->{
            item.setIsMatched(Boolean.FALSE);
            item.setRecordCount(0);
            item.setCreateDate(new Date());
        });
        hisEquipmentDataSourceRepository.save(list);

        int batchSize = 1000;
        List<List<HisEquipmentDataSource>> splitLists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            splitLists.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        for (List<HisEquipmentDataSource> splitList : splitLists) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data",splitList);
            MqContext mqContext = MqContext.builder().serviceName("hc-apm-web").exChange(RabbitConfig.DATA_UPLOAD_EXCHANGE).routingKey(RabbitConfig.HIS_DATA_SOURCE_ROUTING_KEY).time(new Date()).jsonObject(jsonObject).build();
            mqUtils.sendToMq(mqContext);
        }
    }

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }


}
