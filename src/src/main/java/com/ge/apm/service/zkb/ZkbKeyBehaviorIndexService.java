package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbKeyBehaviorIndexMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoRow;
import com.ge.apm.service.asset.KeyBehaviorIndexRow;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.primefaces.model.chart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @author 503202755 2020/9/23
 */
@Component
public class ZkbKeyBehaviorIndexService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbKeyBehaviorIndexService.class);

    @Autowired
    private ZkbKeyBehaviorIndexRepository zkbKeyBehaviorIndexRepository;

    /**
     *配件归还子表dao
     */
    @Autowired
    private ZkbContractRepository zkbContractRepository;

    /**
     * KBI数据查询mapper
     */
    @Autowired
    private ZkbKeyBehaviorIndexMapper zkbKeyBehaviorIndexMapper;

    /**
     *配件归还主表Dao
     */
    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    /**
     *配件归还子表dao
     */
    @Autowired
    private ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;

    /**
     * 合同设备serveice
     */
    @Autowired
    private ZkbContractAssetRepository zkbContractAssetRepository;

    /**
     * 设备信息
     */
    @Autowired
    private AssetInfoRepository assetInfoRepository;

    /**
     * 申请单
     */
    @Autowired
    private V2_WorkOrderRepository v2_WorkOrderRepository;

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_KBI_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_KBI_IMPORT_MAX_ROW = 5000;

    /**
     * 模板数据起始行数
     */
    public static final int EXCEL_KBI_DATA_ROW = 2;

    /**
     * excel模板名称
     */
    public static final String EXCEL_TEMPLATE_NAME="Key_Behavior_Index_Template.xlsx";

    /**
     * excel模板英文名称
     */
    public static final String EXCEL_TEMPLATE_EN_NAME="Key_Behavior_Index_Template_EN.xlsx";

    /**
     * 目录地址
     */
    public static final String DIRECOTORY_PATH="/portal/zkb/keyHehaviorIndex/";

    /**
     * excel sheet  KPI项
     */
    public static final String SHEET_SERVICE_KEY_BEHAVIOR_INDEX = "Key_Behavior_Index_List";

    /**
     * excel sheet  KPI项
     */
    public static final String SHEET_SERVICE_CONTRACT_LIST = "Contract_List";

    /**
     * 日期格式化
     */
    private static final String dateFormat = "yyyyMMddHHmmss";

    /**
     * 日期格式化
     */
    private static final String monthDateFormat = "yyyy年MM月";

    /**
     * 权重最高分
     */
    public static final int  totalFullScore = 100;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    /**
     * 根据租户UID获取所有合同列表
     * @param tenantUID 租户UID
     * @return 合同列表
     */
    public List<ZkbContract> getZkbContractListByTenantUID(String tenantUID)
    {
        if(StringUtils.isBlank(tenantUID))
        {
            return null;
        }
        return this.zkbContractRepository.findByTenantUID(tenantUID);
    }

    /**
     * 获取 kpi 数据列表
     * @param tenantUID 租户UID
     * @param beginningTime 开始时间
     * @param endingTime 结束时间
     * @return kpi 数据列表
     */
    public List<ZkbKeyBehaviorIndex> loadZkbKeyBehaviorIndex(String tenantUID,
                                                             Date beginningTime,
                                                             Date endingTime)
    {
        List<SearchFilter> searchFilters = this.generageSearchFilter(tenantUID,beginningTime,endingTime);
        return this.zkbKeyBehaviorIndexMapper.loadZkbKeyBehaviorIndex(searchFilters);
    }

    /**
     * 生成过滤条件
     * @param tenantUID 租户UID
     * @param beginningTime 开始时间
     * @param endingTime 结束时间
     * @return
     */
    private List<SearchFilter> generageSearchFilter(String tenantUID,
                                                    Date beginningTime,
                                                    Date endingTime)
    {
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if(StringUtils.isNotBlank(tenantUID))
        {
            SearchFilter tenantUIDFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID);
            searchFilters.add(tenantUIDFilter);
        }
        if(beginningTime!=null)
        {
            LocalDateTime localDateTime = beginningTime.toInstant()
                    .atZone( ZoneId.systemDefault() )
                    .toLocalDateTime();
            Date firstday = this.firstDayOnMonth(localDateTime);
            SearchFilter beginningTimeFilter = new SearchFilter("beginningTime", SearchFilter.Operator.GT, firstday);
            searchFilters.add(beginningTimeFilter);
        }
        if(endingTime!=null)
        {
            LocalDateTime localDateTime = endingTime.toInstant()
                    .atZone( ZoneId.systemDefault() )
                    .toLocalDateTime();
            Date lastday = this.lastDayOnMonth(localDateTime);
            SearchFilter endingTimeFilter = new SearchFilter("endingTime", SearchFilter.Operator.LT, lastday);
            searchFilters.add(endingTimeFilter);
        }
        return searchFilters;
    }
    /**
     * 获取平均 kpi 数据列表
     * @param tenantUID 租户UID
     * @param beginningTime 开始时间
     * @param endingTime 结束时间
     * @return kpi 数据列表
     */
    public List<ZkbKeyBehaviorIndex> loadAverageZkbKeyBehaviorIndex(String tenantUID,
                                                             Date beginningTime,
                                                             Date endingTime)
    {
        List<SearchFilter> searchFilters = this.generageSearchFilter(tenantUID,beginningTime,endingTime);
        return this.zkbKeyBehaviorIndexMapper.loadAverageZkbKeyBehaviorIndex(searchFilters);
    }

    /**
     * 组装报表数据
     * @param tenantUID 租户ID
     * @param beginningTime 开始时间
     * @param endingTime 结束时间
     */
    public LineChartModel generateCharts(String tenantUID,Date beginningTime,Date endingTime)
    {
        LineChartModel areaModel = new LineChartModel();
        List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList = this.loadZkbKeyBehaviorIndex(tenantUID,beginningTime,endingTime);
        List<ZkbKeyBehaviorIndex> zkbAverageKeyBehaviorIndexList = this.loadAverageZkbKeyBehaviorIndex(null,beginningTime,endingTime);
        LineChartSeries series1 = new LineChartSeries();
        boolean showNonData = true;
        if(CollectionUtils.isNotEmpty(zkbKeyBehaviorIndexList))
        {
            series1.setLabel(zkbKeyBehaviorIndexList.get(0).getTenantName());
//            zkbKeyBehaviorIndexList.forEach(item->{
//                series1.set(DateFormatUtils.format(item.getKbiDate(),"yyyy-MM"), item.getTotalScore());
//            });
            for(ZkbKeyBehaviorIndex item:zkbKeyBehaviorIndexList)
            {
                series1.set(DateFormatUtils.format(item.getKbiDate(),"yyyy-MM"), item.getTotalScore());
            }
            showNonData=false;
        }else
            {
                series1.setLabel("该租户没有相关数据");
                series1.set(DateFormatUtils.format(beginningTime,"yyyy-MM"), 0);

            }
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("平均分数");
        if(CollectionUtils.isNotEmpty(zkbAverageKeyBehaviorIndexList))
        {
//            zkbAverageKeyBehaviorIndexList.forEach(item->{
//                series2.set(DateFormatUtils.format(item.getKbiDate(),"yyyy-MM"), item.getTotalScore());
//            });
            for(ZkbKeyBehaviorIndex item:zkbAverageKeyBehaviorIndexList)
            {
                series2.set(DateFormatUtils.format(item.getKbiDate(),"yyyy-MM"), item.getTotalScore());
            }
            showNonData=false;
        }else
        {
            series2.set(DateFormatUtils.format(beginningTime,"yyyy-MM"), 0);
        }
        String title = this.getTitle(beginningTime,endingTime);
        areaModel.addSeries(series1);
        areaModel.addSeries(series2);
        areaModel.setTitle(title);
        areaModel.setLegendPosition("e");
        areaModel.setShowPointLabels(true);
        String axisTitle = "Months";
        if(showNonData)
        {
            axisTitle = "没有相关数据";
        }
        Axis xAxis = new CategoryAxis(axisTitle);
        areaModel.getAxes().put(AxisType.X, xAxis);
        Axis yAxis = areaModel.getAxis(AxisType.Y);
        yAxis.setLabel("得分");
        yAxis.setMin(0);
        yAxis.setMax(100);
        return areaModel;
    }

    /**
     * 获取title
     * @param beginningTime 开始时间
     * @param endingTime 结束事假
     * @return 标题
     */
    private String getTitle(Date beginningTime,Date endingTime)
    {
       String beginningTimeString =  this.getDateFormat(beginningTime);
       String endingTimeString =  this.getDateFormat(endingTime);
       return "KPI"+"("+beginningTimeString+"至"+endingTimeString+")";
    }
    private String getDateFormat(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexService.monthDateFormat);
        return formatter.format(date);
    }
    /**
     * 根据KPI数据组装导出excel需要的map
     * @param zkbKeyBehaviorIndexList KPI数据
     * @return 导出excel需要的kpi数据
     */
    public List<Map<String, Object>> createExportKeyBehaviorIndexData(List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList){
        List<Map<String, Object>> keyBehaviorIndexMapList = new ArrayList<>();
        for (ZkbKeyBehaviorIndex zeyBehaviorIndex : zkbKeyBehaviorIndexList) {
            Map<String, Object> orderHistoryMap = new HashMap<>();
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_systemId"),zeyBehaviorIndex.getId());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_tenant"),zeyBehaviorIndex.getTenantName());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_kbiDate"),zeyBehaviorIndex.getKbiDateFormat());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_totalScore"),zeyBehaviorIndex.getTotalScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_partReturnFullScore"),zeyBehaviorIndex.getPartReturnFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_partReturnScore"),zeyBehaviorIndex.getPartReturnScore());
//            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_intactPartReturnRateFullScore"),zeyBehaviorIndex.getIntactPartReturnRateFullScore());
//            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_intactPartReturnRateScore"),zeyBehaviorIndex.getIntactPartReturnRateScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeFullScore"),zeyBehaviorIndex.getSatisfactoryDegreeFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeScore"),zeyBehaviorIndex.getSatisfactoryDegreeScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_apmBillingRateFullScore"),zeyBehaviorIndex.getApmBillingRateFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_apmBillingRateScore"),zeyBehaviorIndex.getApmBillingRateScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_pasteQuickResponseRateFullScore"),zeyBehaviorIndex.getPasteQuickResponseRateFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_pasteQuickResponseRateScore"),zeyBehaviorIndex.getPasteQuickResponseRateScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceFullScore"),zeyBehaviorIndex.getComplianceFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceScore"),zeyBehaviorIndex.getComplianceScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerFullScore"),zeyBehaviorIndex.getEngineerFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerScore"),zeyBehaviorIndex.getEngineerScore());
            keyBehaviorIndexMapList.add(orderHistoryMap);
        }
        return keyBehaviorIndexMapList;
    }


    /**
     * 根据excel文件 读取文件中的数据
     * @param doc 文件
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getKbiMapFromTemplate(ExcelDocument doc) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_KBI_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_KEY_BEHAVIOR_INDEX).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_KEY_BEHAVIOR_INDEX, EXCEL_KBI_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            KeyBehaviorIndexVO keyBehaviorIndexVO = new KeyBehaviorIndexVO();
            keyBehaviorIndexVO.setLineNum(i);
            KeyBehaviorIndexRow keyBehaviorIndexRow = new KeyBehaviorIndexRow(keyBehaviorIndexVO,rowMap);
            keyBehaviorIndexRow.setFieldStringValue(WebUtil.getMessage("zkbHistory_systemId"), "setId");
            keyBehaviorIndexRow.setFieldIntegerValue(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeScore"), "setSatisfactoryDegreeScore");
            keyBehaviorIndexRow.setFieldIntegerValue(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceScore"), "setComplianceScore");
            keyBehaviorIndexRow.setFieldIntegerValue(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerScore"), "setEngineerScore");
            if(keyBehaviorIndexVO.isNull()){
                continue;
            }
            map.put("keyBehaviorIndexVO", keyBehaviorIndexVO);
            map.put("rowData", rowMap);
            map.put("status", keyBehaviorIndexVO.getId()==null ? ZkbKeyBehaviorIndexService.ImportStatus.New.name() : ZkbKeyBehaviorIndexService.ImportStatus.Exist.name());
            map.put("hashCode", keyBehaviorIndexVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getKbiMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }
    /**
     * 导入数据
     * @param importKbiMapList KBI列表信息
     */
    public void importData(List<Map<String, Object>> importKbiMapList) {
        LocalDateTime startTime = LocalDateTime.now();
        int row = 0;
        try {
            for (Map<String, Object> item : importKbiMapList) {
                ++row;
                if(checkData(item)) {
                    KeyBehaviorIndexVO keyBehaviorIndexVO = (KeyBehaviorIndexVO)item.get("keyBehaviorIndexVO");
                    ZkbKeyBehaviorIndex zkbKeyBehaviorIndex = new ZkbKeyBehaviorIndex();
                    BeanUtils.copyProperties(keyBehaviorIndexVO, zkbKeyBehaviorIndex);
                    this.zkbKeyBehaviorIndexRepository.save(zkbKeyBehaviorIndex);
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
        KeyBehaviorIndexVO keyBehaviorIndexVO = (KeyBehaviorIndexVO)item.get("keyBehaviorIndexVO");
        AssetInfoRow.clearErrMessage(item);
        if(StringUtils.isBlank(keyBehaviorIndexVO.getId())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbKeyBehaviorIndex_systemIdError"));
           return isValidateData = false;
        }
        ZkbKeyBehaviorIndex zkbKeyBehaviorIndex = this.zkbKeyBehaviorIndexRepository.findById(keyBehaviorIndexVO.getId());
        if(zkbKeyBehaviorIndex==null)
        {
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbHistory_ErrorSystemId"));
            isValidateData = false;
            return isValidateData;
        }
        KeyBehaviorIndexVO keyBehaviorIndexVO1 = new KeyBehaviorIndexVO();
        BeanUtils.copyProperties(zkbKeyBehaviorIndex, keyBehaviorIndexVO1);
        keyBehaviorIndexVO1.setSatisfactoryDegreeScore(keyBehaviorIndexVO.getSatisfactoryDegreeScore());
        keyBehaviorIndexVO1.setComplianceScore(keyBehaviorIndexVO.getComplianceScore());
        keyBehaviorIndexVO1.setEngineerScore(keyBehaviorIndexVO.getEngineerScore());
        keyBehaviorIndexVO1.setLineNum(keyBehaviorIndexVO.getLineNum());
        item.put("keyBehaviorIndexVO",keyBehaviorIndexVO1);
        if(keyBehaviorIndexVO1.getSatisfactoryDegreeScore()>keyBehaviorIndexVO1.getSatisfactoryDegreeFullScore())
        {
            StringBuffer errorMessage = new StringBuffer(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeScore"));
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_fieldThanAnotherFieldErro"));
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeFullScore"));
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, errorMessage.toString());
            isValidateData = false;
        }
        if(keyBehaviorIndexVO1.getComplianceScore()>keyBehaviorIndexVO1.getComplianceFullScore())
        {
            StringBuffer errorMessage = new StringBuffer(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceScore"));
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_fieldThanAnotherFieldErro"));
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceFullScore"));
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, errorMessage.toString());
            isValidateData = false;
        }
        if(keyBehaviorIndexVO1.getEngineerScore()>keyBehaviorIndexVO1.getEngineerFullScore())
        {
            StringBuffer errorMessage = new StringBuffer(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerScore"));
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_fieldThanAnotherFieldErro"));
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerFullScore"));
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, errorMessage.toString());
            isValidateData = false;
        }
        if(keyBehaviorIndexVO1.getTotalScore()>ZkbKeyBehaviorIndexService.totalFullScore)
        {
            StringBuffer errorMessage = new StringBuffer();
            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_totalScoreError"));
//            errorMessage=errorMessage.append(WebUtil.getMessage("zkbKeyBehaviorIndex_totalScoreError"));
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, errorMessage.toString());
            isValidateData = false;
            return isValidateData;
        }
        return isValidateData;
    }

    /**
     * 计算分数
     * @param zkbKeyBehaviorIndex kbi实体
     * @param zkbContractList 合同列表
     * @param tenantUid
     */
    public void calculateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,List<ZkbContract> zkbContractList,String tenantUid)
    {
        //取合同ID
//        List<String> contractIdList = zkbContractList.stream().map(p -> p.getId()).collect(Collectors.toList());
        List<String> contractIdList = new ArrayList<>();
        for(ZkbContract item:zkbContractList)
        {
            contractIdList.add(item.getId());
        }
        LocalDateTime localDateTime = zkbKeyBehaviorIndex.getKbiDate().toInstant()
                .atZone( ZoneId.systemDefault() )
                .toLocalDateTime();
        Date firstday = this.firstDayOnMonth(localDateTime);
        Date lastday = this.lastDayOnMonth(localDateTime);
        this.calculatePartReturnScore(zkbKeyBehaviorIndex,contractIdList,lastday);
//        this.calculateIntactPartReturnRateScore(zkbKeyBehaviorIndex,contractIdList);
        Integer totalIbNumber = this.calculatePasteQuickResponseRateScore(zkbKeyBehaviorIndex,contractIdList);
        this.calculateApmBillingRateScore(zkbKeyBehaviorIndex,tenantUid,totalIbNumber,firstday,lastday);
    }


    /**
     * 获取某月第一天
     * @return 返回某月第一天时间
     */
    private Date firstDayOnMonth(LocalDateTime date)
    {

        LocalDateTime firstday = date.with(TemporalAdjusters.firstDayOfMonth());
        String dateStr = firstday.format(DateTimeFormatter.ofPattern("yyyyMMdd"))+"000000";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexService.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(dateStr);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return date1;
    }

    /**
     * 获取某月最后一天
     * @return 返回某月最后一天时间
     */
    private Date lastDayOnMonth(LocalDateTime date)
    {

        LocalDateTime lasttday = date.with(TemporalAdjusters.lastDayOfMonth());
        String dateStr = lasttday.format(DateTimeFormatter.ofPattern("yyyyMMdd"))+"235959";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexService.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(dateStr);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return date1;
    }

    /**
     * 计算配件配件归还得分
     * @param zkbKeyBehaviorIndex KPI
     * @param contractIdList 合同ID列表
     */
    private void calculatePartReturnScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,List<String> contractIdList,Date now)
    {
        Calendar cd = Calendar.getInstance();
        cd.setTime(now);
        cd.add(Calendar.DATE,-14);
        Date nowBefore = cd.getTime();
        Integer unReturnCount  = this.zkbPartOrderRepository.fetchUnReturnCount(contractIdList,nowBefore,ZkbPartOrder.OrderType.Probe.toString());
        if(unReturnCount==null)
        {
            unReturnCount=0;
        }
        List<ZkbPartOrder> zkbPartOrdersList = this.zkbPartOrderRepository.findReturnId(nowBefore,contractIdList,ZkbPartOrder.OrderType.Parts.toString());
        Integer applyPartsCount = this.zkbPartOrderRepository.fetchApplyPartsQty(contractIdList,nowBefore,ZkbPartOrder.OrderType.Parts.toString());
        if(applyPartsCount==null)
        {
            applyPartsCount=0;
        }
        List<String> partOrderIdList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(zkbPartOrdersList))
        {
            for(ZkbPartOrder item:zkbPartOrdersList)
            {
                partOrderIdList.add(item.getId());
            }
        }
        Integer returnQty1 = new Integer(0);
        if(CollectionUtils.isNotEmpty(partOrderIdList))
        {
            returnQty1 = this.zkbPartOrderReturnRepository.fetchQty(partOrderIdList);
        }
        if(returnQty1==null)
        {
            returnQty1 = new Integer(0);
        }
        Integer unReturnApplyPartsCount = applyPartsCount-returnQty1;
        BigDecimal result = new BigDecimal(14).divide(new BigDecimal(5),2,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(unReturnApplyPartsCount+unReturnCount)).setScale(0,BigDecimal.ROUND_HALF_DOWN);
        BigDecimal partReturnFullScore = new BigDecimal(zkbKeyBehaviorIndex.getPartReturnFullScore());
        if(result.compareTo(partReturnFullScore)>0)
        {
            zkbKeyBehaviorIndex.setPartReturnScore(0);
        }else
            {
                zkbKeyBehaviorIndex.setPartReturnScore(partReturnFullScore.subtract(result).intValue());
            }
        return;
    }

//    /**
//     * 计算配件好还率得分
//     * @param zkbKeyBehaviorIndex
//     * @param contractIdList 合同ID list
//     */
//    private void calculateIntactPartReturnRateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,
//                                                    List<String> contractIdList)
//    {
//        Integer requireQty = this.zkbPartOrderRepository.fetchQty(contractIdList);
//        if(requireQty==null)
//        {
//            zkbKeyBehaviorIndex.setIntactPartReturnRateScore(0);
//            return;
//        }
//        Integer goodReturnQty = this.zkbPartOrderRepository.fetchReturnGoodQty(contractIdList);
//        Integer goodReturnQty1 = this.zkbPartOrderReturnRepository.fetchReturnGoodQty(contractIdList);
//        if(goodReturnQty==null) goodReturnQty = new Integer(0);
//        if(goodReturnQty1==null) goodReturnQty1 = new Integer(0);
//        BigDecimal goodReturnRare = new BigDecimal(goodReturnQty+goodReturnQty1).divide(new BigDecimal(requireQty),2,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(100));
//        goodReturnRare=goodReturnRare.subtract(new BigDecimal(30)).multiply(new BigDecimal(0.5)).setScale(0,BigDecimal.ROUND_HALF_DOWN);
//        BigDecimal intactPartReturnRateFullScoreBigDecimal = new BigDecimal(zkbKeyBehaviorIndex.getIntactPartReturnRateFullScore());
//        BigDecimal result =intactPartReturnRateFullScoreBigDecimal.subtract(goodReturnRare);
//        if(result.compareTo(intactPartReturnRateFullScoreBigDecimal)==1)
//        {
//            zkbKeyBehaviorIndex.setIntactPartReturnRateScore(zkbKeyBehaviorIndex.getIntactPartReturnRateFullScore());
//            return;
//        }else if(result.compareTo(intactPartReturnRateFullScoreBigDecimal)==-1)
//        {
//            zkbKeyBehaviorIndex.setIntactPartReturnRateScore(0);
//            return;
//        }else
//        {
//            zkbKeyBehaviorIndex.setIntactPartReturnRateScore(result.intValue());
//            return;
//        }
//    }

    /**
     * 计算二维码粘贴率得分
     * @param zkbKeyBehaviorIndex 当前用户得分
     * @param contractIdList 合同ID list
     */
    private Integer calculatePasteQuickResponseRateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,List<String> contractIdList)
    {
        Integer totalIbNumber = this.zkbContractRepository.fetchIbNumber(contractIdList);
        if(totalIbNumber==null)
        {
            totalIbNumber=new Integer(0);
        }
        List<ZkbContractAsset> zkbContractAssetList = this.zkbContractAssetRepository.findByContractIdIn(contractIdList);
        if(CollectionUtils.isEmpty(zkbContractAssetList))
        {
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(0);
            return totalIbNumber;
        }
        List<String> assetId = new ArrayList<>();
        for(ZkbContractAsset item:zkbContractAssetList)
        {
            assetId.add(item.getAssetId());
        }
//        List<String> assetId = zkbContractAssetList.stream().map(p -> p.getAssetId()).collect(Collectors.toList());
        Integer countAssetWithQRcode = this.assetInfoRepository.countAssetWithQRcode(assetId);
        Integer pasteQuickResponseRateScore = new BigDecimal(countAssetWithQRcode).
                divide(new BigDecimal(totalIbNumber),2,BigDecimal.ROUND_HALF_DOWN).
                multiply(new BigDecimal(zkbKeyBehaviorIndex.getPasteQuickResponseRateFullScore())).setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
        zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(pasteQuickResponseRateScore);
        return totalIbNumber;
    }

    /**
     * 计算APM开单率得分
     * @param zkbKeyBehaviorIndex
     * @param tenantUid 合同列表
     * @param totalIbNumber ib 总量
     */
    private void calculateApmBillingRateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUid,Integer totalIbNumber,Date firstday,Date lastday)
    {
        if(totalIbNumber==null||totalIbNumber==0)
        {
            zkbKeyBehaviorIndex.setApmBillingRateScore(0);
            return;
        }
        Integer countWorkOrder = this.v2_WorkOrderRepository.fetchCount(tenantUid,firstday,lastday);
        //Integer countWorkOrder = this.zkbPartOrderRepository.fetchCount(contractIdList,firstday,lastday);
        if(countWorkOrder==null)
        {
            countWorkOrder=new Integer(0);
        }
        Integer apmBillingRateScore = new BigDecimal(countWorkOrder).
                divide(new BigDecimal(totalIbNumber),2,BigDecimal.ROUND_HALF_DOWN).
                multiply(new BigDecimal(100)).
                setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
        if(apmBillingRateScore>zkbKeyBehaviorIndex.getApmBillingRateFullScore())
        {
             zkbKeyBehaviorIndex.setApmBillingRateScore(zkbKeyBehaviorIndex.getApmBillingRateFullScore());
            return;
        }
        zkbKeyBehaviorIndex.setApmBillingRateScore(apmBillingRateScore);
    }

    /**
     * 整理错误信息
     * @param importKeyBehaviorIndexMapList
     * @return
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importKeyBehaviorIndexMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importKeyBehaviorIndexMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbKeyBehaviorIndexService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbKeyBehaviorIndexService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 根据合同数据组装导出excel需要的map
     * @param zkbKeyBehaviorIndexList 合同数据
     * @return 导出excel需要的合同数据
     */
    public List<Map<String, Object>> createExportContractData(List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList){
        List<Map<String, Object>> zkbContractMapList = new ArrayList<>();
        if(CollectionUtils.isEmpty(zkbKeyBehaviorIndexList))
        {
            return zkbContractMapList;
        }
        //根据tenantUid去重zkbKeyBehaviorIndexList,并且把tenantUid组装到string list集合中
//        List<String> tenantUIDList =zkbKeyBehaviorIndexList.stream().collect(
//                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTenantUID()))),
//                        ArrayList::new)).stream().map(ZkbKeyBehaviorIndex::getTenantUID).collect(Collectors.toList());
        Map tenantUIDMap = new HashMap();
        List<String> tenantUIDList = new ArrayList<>();
        for(ZkbKeyBehaviorIndex item:zkbKeyBehaviorIndexList)
        {
            if(tenantUIDMap.containsKey(item.getTenantUID())) continue;
            tenantUIDList.add(item.getTenantUID());
            tenantUIDMap.put(item.getTenantUID(),item.getTenantUID());
        }

        List<ZkbContract> zkbContractList = this.zkbContractRepository.findByTenantUIDIn(tenantUIDList);
        for (ZkbContract zkbContract : zkbContractList) {
            Map<String, Object> zkbContractMap = new HashMap<>();
            zkbContractMap.put(WebUtil.getMessage("zkbHistory_systemId"),zkbContract.getId());
            zkbContractMap.put(WebUtil.getMessage("zkbContract_contractNo"),zkbContract.getContractNo());
            zkbContractMap.put(WebUtil.getMessage("zkbContract_contractType"),zkbContract.getContractType());
            zkbContractMap.put(WebUtil.getMessage("zkbContract_ibNumber"),zkbContract.getIbNumber());
            zkbContractMap.put(WebUtil.getMessage("zkbContract_startDate"),zkbContract.getStartDate());
            zkbContractMap.put(WebUtil.getMessage("zkbContract_endDate"),zkbContract.getEndDate());
            zkbContractMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_remainDaysContract"),zkbContract.getRemainDaysContract());
            zkbContractMapList.add(zkbContractMap);
        }
        return zkbContractMapList;
    }

    /**
     * 给EXCEL填入合同和KPI数据
     * @param doc
     * @param keyBehaviorIndexMapList
     * @param zkbContractMapList
     */
    public void exportData(ExcelDocument doc,List<Map<String, Object>> keyBehaviorIndexMapList,List<Map<String, Object>> zkbContractMapList) {
        Integer dataRow = 2;
        for (Map<String, Object> item : keyBehaviorIndexMapList) {
            doc.writeRowData(SHEET_SERVICE_KEY_BEHAVIOR_INDEX, 1, dataRow++, item);
        }
        dataRow=2;
        for (Map<String, Object> item : zkbContractMapList) {
            doc.writeRowData(SHEET_SERVICE_CONTRACT_LIST, 1, dataRow++, item);
        }
    }

}
