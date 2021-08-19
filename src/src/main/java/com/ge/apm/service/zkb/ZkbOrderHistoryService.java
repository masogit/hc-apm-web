package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbOrderHistoryRepository;
import com.ge.apm.dao.mapper.ZkbOrderHistoryMapper;
import com.ge.apm.domain.OrderHistoryVO;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.ZkbOrderHistory;
import com.ge.apm.service.asset.AssetInfoRow;
import com.ge.apm.service.asset.OrderHistoryInfoRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
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
     * 租户信息repository
     */
    @Autowired
    private TenantInfoRepository tenantInfoRepository;

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_ORDER_HISTORY_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_ORDER_HISTORY_IMPORT_MAX_ROW = 10000;

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
    public static final String EXCEL_TEMPLATE_NAME="Order_History_Service_Export.xlsx";

    /**
     * excel模板英文名称
     */
    public static final String EXCEL_TEMPLATE_EN_NAME="Order_History_Service_Export_EN.xlsx";

    /**
     * 目录地址
     */
    public static final String DIRECOTORY_PATH="/portal/zkb/orderHistory/";

    /**
     *智康宝租户type
     */
    public static final Integer zkbTenantType = 3;

    /**
     * 历史数据查询mapper
     */
    @Autowired
    private ZkbOrderHistoryMapper zkbOrderHistoryMapper;


//    private UserContextService userContextService  = WebUtil.getBean(UserContextService.class);;

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
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_systemId"), "setId");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_dataType"), "setDataType");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_region"), "setRegion");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_agent"), "setAgent");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_contractNo"), "setContractNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_contractType"), "setContractType");
            orderHistoryInfoRow.setFieldDateValue(WebUtil.getMessage("zkbHistory_contractBeginningTime"), "setContractBeginningTime");
            orderHistoryInfoRow.setFieldDateValue(WebUtil.getMessage("zkbHistory_contractEndingTime"), "setContractEndingTime");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_quantityIb"), "setQuantityIb");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_labourQuantity"), "setLabourQuantity");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_partsQuantity"), "setPartsQuantity");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_probeQuantity"), "setProbeQuantity");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_countQuantity"), "setCountQuantity");
            orderHistoryInfoRow.setFieldDateValue(WebUtil.getMessage("zkbHistory_eventDate"), "setEventDate");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_type"), "setType");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_systemNo"), "setSystemNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_faultDescription"), "setFaultDescription");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_hospitalName"), "setHospitalName");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_machineType"), "setMachineType");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_sparePartsNo"), "setSparePartsNo");
            orderHistoryInfoRow.setFieldDoubleValue(WebUtil.getMessage("zkbHistory_sparePartsPrice"), "setSparePartsPrice");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_description"), "setDescription");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_orderNo"), "setOrderNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_RSESup"), "setRSESup");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_quantity"), "setQuantity");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_totalContractProbeCount"), "setTotalContractProbeCount");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_applyLimitCount"), "setApplyLimitCount");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_normalLimitCount"), "setNormalLimitCount");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_specialLimitCount"), "setSpecialLimitCount");
            orderHistoryInfoRow.setFieldIntegerValue(WebUtil.getMessage("zkbHistory_highValueLimitCount"), "setHighValueLimitCount");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_sr"), "setSr");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_act"), "setAct");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_sparePartsReturn"), "setSparePartsReturn");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_comment"), "setComment");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_insuranceDescription"), "setInsuranceDescription");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_probeType"), "setProbeType");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_useStatus"), "setUseStatus");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_callNo"), "setCallNo");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_rowner"), "setRowner");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_ownerName"), "setOwnerName");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_status"), "setStatus");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("Delete"),"setDelete");
            orderHistoryInfoRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_remark"),"setRemark");
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

        if(StringUtils.isBlank(orderHistoryVO.getRegion())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorRegion"));
            isValidateData = false;
        }
        if(StringUtils.isBlank(orderHistoryVO.getContractNo())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorContractNo"));
            isValidateData = false;
        }
        if(StringUtils.isBlank(orderHistoryVO.getContractType())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorContractType"));
            isValidateData = false;
        }
        if(StringUtils.isBlank(orderHistoryVO.getSystemNo())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorSystemNo"));
            isValidateData = false;
        }

        if(StringUtils.isBlank(orderHistoryVO.getDataType())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorDataTypeIsEmpty"));
            isValidateData = false;
        }
        if(StringUtils.isNotBlank(orderHistoryVO.getId())){
              ZkbOrderHistory zkbOrderHistory = this.zkbOrderHistoryRepository.findById(orderHistoryVO.getId());
              if(zkbOrderHistory==null)
              {
                item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
                AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorSystemId"));
                isValidateData = false;
              }
        }
        //代理商字段是否为空
//        boolean isEmptyOfAgent = false;
        if(StringUtils.isBlank(orderHistoryVO.getAgent()))
        {
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorDataAgentIsEmpty"));
            isValidateData = false;
            //代理商字段为空
//            isEmptyOfAgent = true;
        }
//        if(!isEmptyOfAgent)
//        {
//            List<TenantInfo> tenantInfoList = this.tenantInfoRepository.findByNameAndType(orderHistoryVO.getAgent(),ZkbOrderHistoryService.zkbTenantType);
//            if(CollectionUtils.isNotEmpty(tenantInfoList))
//            {
//                String tenantUid = tenantInfoList.get(0).getUid();
//                orderHistoryVO.setTenantUid(tenantUid);
//            }else
//                {
//                    item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
//                    AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorAgent"));
//                    isValidateData = false;
//                }
//        }
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
    public List<ZkbOrderHistory> loadZkbOrderHistoryData(String dataType,
                                                         Date contractBeginningTime,
                                                         Date contractEndingTime,
                                                         String region,
                                                         String agent,
                                                         String contractNo,
                                                         String contractType,
                                                         String searchStr,
                                                         String tenantUid){
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
        if(tenantUid!=null)
        {
            SearchFilter searchStrFilter = new SearchFilter("tenantUid", SearchFilter.Operator.EQ, tenantUid);
            searchFilters.add(searchStrFilter);
        }
        return zkbOrderHistoryMapper.loadZkbOrderHistory(searchFilters);
    }


    public List<Map<String, Object>> createExportOrderHistoryData(List<ZkbOrderHistory> zkbOrderHistoryList,UserContextService userContextService){
        List<Map<String, Object>> orderHistoryMapList = new ArrayList<>();
        for (ZkbOrderHistory orderHistory : zkbOrderHistoryList) {
            Map<String, Object> orderHistoryMap = new HashMap<>();
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_systemId"),orderHistory.getId());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_dataType"),orderHistory.getDataType());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_region"),orderHistory.getRegion());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_agent"),orderHistory.getAgent());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_contractNo"),orderHistory.getContractNo());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_contractType"),orderHistory.getContractType());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_contractBeginningTime"),orderHistory.getContractBeginningTime());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_contractEndingTime"),orderHistory.getContractEndingTime());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_quantityIb"),orderHistory.getQuantityIb());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_labourQuantity"),orderHistory.getLabourQuantity());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_partsQuantity"),orderHistory.getPartsQuantity());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_probeQuantity"),orderHistory.getProbeQuantity());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_countQuantity"),orderHistory.getCountQuantity());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_eventDate"),orderHistory.getEventDate());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_type"),orderHistory.getType());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_systemNo"),orderHistory.getSystemNo());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_faultDescription"),orderHistory.getFaultDescription());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_hospitalName"),orderHistory.getHospitalName());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_machineType"),orderHistory.getMachineType());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_sparePartsNo"),orderHistory.getSparePartsNo());
            if(userContextService.isZkbAdmin())
            {
                orderHistoryMap.put(WebUtil.getMessage("zkbHistory_sparePartsPrice"),orderHistory.getSparePartsPrice());
            }else
                {
                    orderHistoryMap.put(WebUtil.getMessage("zkbHistory_sparePartsPrice"),"-");
                }
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_description"),orderHistory.getDescription());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_orderNo"),orderHistory.getOrderNo());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_RSESup"),orderHistory.getRSESup());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_quantity"),orderHistory.getQuantity());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_totalContractProbeCount"),orderHistory.getTotalContractProbeCount());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_applyLimitCount"),orderHistory.getApplyLimitCount());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_normalLimitCount"),orderHistory.getNormalLimitCount());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_specialLimitCount"),orderHistory.getSpecialLimitCount());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_highValueLimitCount"),orderHistory.getHighValueLimitCount());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_sr"),orderHistory.getSr());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_act"),orderHistory.getAct());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_sparePartsReturn"),orderHistory.getSparePartsReturn());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_comment"),orderHistory.getComment());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_insuranceDescription"),orderHistory.getInsuranceDescription());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_probeType"),orderHistory.getProbeType());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_useStatus"),orderHistory.getUseStatus());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_callNo"),orderHistory.getCallNo());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_rowner"),orderHistory.getRowner());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_ownerName"),orderHistory.getOwnerName());
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_status"),orderHistory.getStatus());
            orderHistoryMap.put(WebUtil.getMessage("zkbFePartOrder_remark"),orderHistory.getRemark());
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
