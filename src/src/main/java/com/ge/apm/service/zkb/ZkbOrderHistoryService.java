package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbOrderHistoryRepository;
import com.ge.apm.dao.mapper.ZkbOrderHistoryMapper;
import com.ge.apm.domain.OrderHistoryVO;
import com.ge.apm.domain.ZkbOrderHistory;
import com.ge.apm.service.asset.AssetInfoRow;
import com.ge.apm.service.asset.OrderHistoryInfoRow;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 503202755 2020/9/1
 */
@Component
public class ZkbOrderHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbOrderHistoryService.class);

    @Autowired
    private ZkbOrderHistoryRepository zkbOrderHistoryRepository;

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_ORDER_HISTORY_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_ORDER_HISTORY_IMPORT_MAX_ROW = 5000;

    /**
     * 模板数据起始行数
     */
    public static final int EXCEL_ORDER_HISTORY_DATA_ROW = 2;

    /**
     * excel sheet 名称
     */
    public static final String SHEET_SERVICE_ORDER_HISTORY = "ORDER HISTORY LIST";

    /**
     * 删除标识
     */
    public static final String delete = "Y";

    /**
     * excel模板名称
     */
    public static final String EXCEL_TEMPLATE_NAME="Order_History_Service_Template.xlsx";

    /**
     * excel模板英文名称
     */
    public static final String EXCEL_TEMPLATE_EN_NAME="Order_History_Service_Template_EN.xlsx";

    /**
     * 目录地址
     */
    public static final String DIRECOTORY_PATH="/portal/zkb/orderHistory/";

    /**
     * 历史数据查询mapper
     */
    @Autowired
    private ZkbOrderHistoryMapper zkbOrderHistoryMapper;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    /**
     * 根据excel文件 读取文件中的数据
     * @param doc 文件
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrderHistoryMapFromTemplate(ExcelDocument doc) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_ORDER_HISTORY_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_ORDER_HISTORY).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_ORDER_HISTORY, EXCEL_ORDER_HISTORY_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            OrderHistoryVO orderHistoryVO = new OrderHistoryVO();
            orderHistoryVO.setLineNum(i);
            OrderHistoryInfoRow orderHistoryInfoRow = new OrderHistoryInfoRow(orderHistoryVO,rowMap);
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("systemId"), "setId");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("dataType"), "setDataType");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("region"), "setRegion");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("agent"), "setAgent");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("contractNo"), "setContractNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("contractType"), "setContractType");
            orderHistoryInfoRow.setFieldDateValue(WebUtil.getMessage("contractBeginningTime"), "setContractBeginningTime");
            orderHistoryInfoRow.setFieldDateValue(WebUtil.getMessage("contractEndingTime"), "setContractEndingTime");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("quantityIb"), "setQuantityIb");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("sparePartsQuantity"), "setSparePartsQuantity");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("fw"), "setFw");
            orderHistoryInfoRow.setFieldDateValue(WebUtil.getMessage("eventDate"), "setEventDate");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("eventQuarter"), "setEventQuarter");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("systemNo"), "setSystemNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("faultDescription"), "setFaultDescription");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("hospitalName"), "setHospitalName");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("machineType"), "setMachineType");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("sparePartsNo"), "setSparePartsNo");
            orderHistoryInfoRow.setFieldDoubleValue(WebUtil.getMessage("sparePartsPrice"), "setSparePartsPrice");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("description"), "setDescription");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("orderNo"), "setOrderNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("RSESup"), "setRSESup");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("quantity"), "setQuantity");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("sr"), "setSr");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("act"), "setAct");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("sparePartsLeft"), "setSparePartsLeft");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("sparePartsReturn"), "setSparePartsReturn");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("comment"), "setComment");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("insuranceDescription"), "setInsuranceDescription");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("arrangement"), "setArrangement");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("callNo"), "setCallNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("rowner"), "setRowner");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("status"), "setStatus");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("owner"), "setOwner");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zone"), "setZone");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("source"), "setSource");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("Delete"),"setDelete");
            if(orderHistoryVO.isNull()){
                break;
            }
            map.put("orderHistoryVO", orderHistoryVO);
            map.put("rowData", rowMap);
            map.put("status", orderHistoryVO.getId()==null ? ZkbOrderHistoryService.ImportStatus.New.name() : ZkbOrderHistoryService.ImportStatus.Exist.name());
            map.put("hashCode", orderHistoryVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getOrderHistoryMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }

    /**
     * 导入数据
     * @param importOrderHistoryMapList 目录信息
     */
    public void importData(List<Map<String, Object>> importOrderHistoryMapList) {
        LocalDateTime startTime = LocalDateTime.now();
        int row = 0;
        try {
            for (Map<String, Object> item : importOrderHistoryMapList) {
                ++row;
                if(checkData(item)) {
                    OrderHistoryVO orderHistoryVO = (OrderHistoryVO)item.get("orderHistoryVO");
                    ZkbOrderHistory zkbOrderHistory = new ZkbOrderHistory();
                    BeanUtils.copyProperties(orderHistoryVO, zkbOrderHistory);
                    //如果excel中删除字段为Y，并且ID不为空则做删除动作
                    if(ZkbOrderHistoryService.delete.equalsIgnoreCase(orderHistoryVO.getDelete())
                            &&StringUtils.isNotEmpty(orderHistoryVO.getId()))
                    {
                        try {
                            this.zkbOrderHistoryRepository.delete(orderHistoryVO.getId());
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                            logger.info("为找到ID："+orderHistoryVO.getId()+"的数据");
                        }
                        continue;
                    }
                    if(ZkbOrderHistoryService.delete.equalsIgnoreCase(orderHistoryVO.getDelete()))
                    {
                        continue;
                    }
                    this.zkbOrderHistoryRepository.save(zkbOrderHistory);
                }

            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    /**
     * 检查数据是否合法
     * 1.数据类型字段必填
     * @param item
     * @return
     */
    public boolean checkData(Map<String, Object> item) {
        boolean isValidateData = true;
        OrderHistoryVO orderHistoryVO = (OrderHistoryVO)item.get("orderHistoryVO");
        AssetInfoRow.clearErrMessage(item);
        if(orderHistoryVO.getDataType()==null){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorDataTypeIsEmpty"));
            isValidateData = false;
        }
        if(StringUtils.isNotEmpty(orderHistoryVO.getId())){
              ZkbOrderHistory zkbOrderHistory = this.zkbOrderHistoryRepository.findById(orderHistoryVO.getId());
              if(zkbOrderHistory==null)
              {
                item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
                AssetInfoRow.addErrMessage(item, WebUtil.getMessage("ErrorSystemId"));
                isValidateData = false;
              }
        }
        return isValidateData;
    }

    /**
     * 整理错误信息
     * @param importOrderHistoryMapList
     * @return
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importOrderHistoryMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importOrderHistoryMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbOrderHistoryService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbOrderHistoryService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 根据条件搜索
     * @param dataType 数据类型
     * @param contractBeginningTime 合同开始时间
     * @param contractEndingTime 合同结束时间
     * @param searchStr 搜索文本框
     * @param region 地区
     * @param agent 代理商
     * @param contractNo 合同号
     * @param contractType 合同类型
     * @return
     */
    public List<ZkbOrderHistory> loadZkbOrderHistoryData(String dataType,Date contractBeginningTime,Date contractEndingTime,String region,String agent,String contractNo,String contractType,String searchStr){
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if(dataType!=null)
        {
            SearchFilter dataTypeFilter = new SearchFilter("dataType", SearchFilter.Operator.EQ, dataType);
            searchFilters.add(dataTypeFilter);
        }
        if(contractBeginningTime!=null)
        {
            SearchFilter contractBeginningTimeFilter = new SearchFilter("contractBeginningTime", SearchFilter.Operator.GT, contractBeginningTime);
            searchFilters.add(contractBeginningTimeFilter);
        }
        if(contractEndingTime!=null)
        {
            SearchFilter contractEndingTimeFilter = new SearchFilter("contractEndingTime", SearchFilter.Operator.LT, contractEndingTime);
            searchFilters.add(contractEndingTimeFilter);
        }
        if(searchStr!=null)
        {
            SearchFilter searchStrFilter = new SearchFilter("searchStrFilter", SearchFilter.Operator.LIKE, searchStr);
            searchFilters.add(searchStrFilter);
        }
        if(region!=null)
        {
            SearchFilter searchStrFilter = new SearchFilter("region", SearchFilter.Operator.LIKE, region);
            searchFilters.add(searchStrFilter);
        }
        if(agent!=null)
        {
            SearchFilter searchStrFilter = new SearchFilter("agent", SearchFilter.Operator.LIKE, agent);
            searchFilters.add(searchStrFilter);
        }
        if(contractNo!=null)
        {
            SearchFilter searchStrFilter = new SearchFilter("contractNo", SearchFilter.Operator.LIKE, contractNo);
            searchFilters.add(searchStrFilter);
        }
        if(contractType!=null)
        {
            SearchFilter searchStrFilter = new SearchFilter("contractType", SearchFilter.Operator.LIKE, contractType);
            searchFilters.add(searchStrFilter);
        }
        return zkbOrderHistoryMapper.loadZkbOrderHistory(searchFilters);
    }


    public List<Map<String, Object>> createExportOrderHistoryData(List<ZkbOrderHistory> zkbOrderHistoryList){
        List<Map<String, Object>> orderHistoryMapList = new ArrayList<>();
        for (ZkbOrderHistory orderHistory : zkbOrderHistoryList) {
            Map<String, Object> orderHistoryMap = new HashMap<>();
            orderHistoryMap.put(WebUtil.getMessage("systemId"),orderHistory.getId());
            orderHistoryMap.put(WebUtil.getMessage("dataType"),orderHistory.getDataType());
            orderHistoryMap.put(WebUtil.getMessage("region"),orderHistory.getRegion());
            orderHistoryMap.put(WebUtil.getMessage("agent"),orderHistory.getAgent());
            orderHistoryMap.put(WebUtil.getMessage("contractNo"),orderHistory.getContractNo());
            orderHistoryMap.put(WebUtil.getMessage("contractType"),orderHistory.getContractType());
            orderHistoryMap.put(WebUtil.getMessage("contractBeginningTime"),orderHistory.getContractBeginningTime());
            orderHistoryMap.put(WebUtil.getMessage("contractEndingTime"),orderHistory.getContractEndingTime());
            orderHistoryMap.put(WebUtil.getMessage("quantityIb"),orderHistory.getQuantityIb());
            orderHistoryMap.put(WebUtil.getMessage("sparePartsQuantity"),orderHistory.getSparePartsQuantity());
            orderHistoryMap.put(WebUtil.getMessage("fw"),orderHistory.getFw());
            orderHistoryMap.put(WebUtil.getMessage("eventDate"),orderHistory.getEventDate());
            orderHistoryMap.put(WebUtil.getMessage("eventQuarter"),orderHistory.getEventQuarter());
            orderHistoryMap.put(WebUtil.getMessage("systemNo"),orderHistory.getSystemNo());
            orderHistoryMap.put(WebUtil.getMessage("faultDescription"),orderHistory.getFaultDescription());
            orderHistoryMap.put(WebUtil.getMessage("hospitalName"),orderHistory.getHospitalName());
            orderHistoryMap.put(WebUtil.getMessage("machineType"),orderHistory.getMachineType());
            orderHistoryMap.put(WebUtil.getMessage("sparePartsNo"),orderHistory.getSparePartsNo());
            orderHistoryMap.put(WebUtil.getMessage("sparePartsPrice"),orderHistory.getSparePartsPrice());
            orderHistoryMap.put(WebUtil.getMessage("description"),orderHistory.getDescription());
            orderHistoryMap.put(WebUtil.getMessage("orderNo"),orderHistory.getOrderNo());
            orderHistoryMap.put(WebUtil.getMessage("RSESup"),orderHistory.getRSESup());
            orderHistoryMap.put(WebUtil.getMessage("quantity"),orderHistory.getQuantity());
            orderHistoryMap.put(WebUtil.getMessage("sr"),orderHistory.getSr());
            orderHistoryMap.put(WebUtil.getMessage("act"),orderHistory.getAct());
            orderHistoryMap.put(WebUtil.getMessage("sparePartsLeft"),orderHistory.getSparePartsLeft());
            orderHistoryMap.put(WebUtil.getMessage("sparePartsReturn"),orderHistory.getSparePartsReturn());
            orderHistoryMap.put(WebUtil.getMessage("comment"),orderHistory.getComment());
            orderHistoryMap.put(WebUtil.getMessage("insuranceDescription"),orderHistory.getInsuranceDescription());
            orderHistoryMap.put(WebUtil.getMessage("arrangement"),orderHistory.getArrangement());
            orderHistoryMap.put(WebUtil.getMessage("callNo"),orderHistory.getCallNo());
            orderHistoryMap.put(WebUtil.getMessage("rowner"),orderHistory.getRowner());
            orderHistoryMap.put(WebUtil.getMessage("status"),orderHistory.getStatus());
            orderHistoryMap.put(WebUtil.getMessage("owner"),orderHistory.getOwner());
            orderHistoryMap.put(WebUtil.getMessage("zone"),orderHistory.getZone());
            orderHistoryMap.put(WebUtil.getMessage("source"),orderHistory.getSource());
            orderHistoryMapList.add(orderHistoryMap);
        }
        return orderHistoryMapList;
    }
    public void exportData(ExcelDocument doc, List<Map<String, Object>> orderHistoryMapList) {
        Integer dataRow = 2;
        for (Map<String, Object> item : orderHistoryMapList) {
            doc.writeRowData(SHEET_SERVICE_ORDER_HISTORY, 1, dataRow++, item);
        }
    }



}
