package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.ZkbContractMapper;
import com.ge.apm.dao.mapper.ZkbKeyBehaviorIndexMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoRow;
import com.ge.apm.service.asset.KeyBehaviorIndexRow;
import com.ge.apm.service.asset.TenantInfoRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.asset.ZkbKeyBehaviorIndexController;
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
import webapp.framework.util.DateUtil;
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
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbKeyBehaviorIndexRepository zkbKeyBehaviorIndexRepository;

    /**
     *配件归还子表dao
     */
    @Autowired
    private ZkbContractRepository zkbContractRepository;

    /**
     *合同
     */
    @Autowired
    private ZkbContractMapper zkbContractMapper;

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
     * 申请单
     */
    @Autowired
    private V2_WorkOrderRepository v2_WorkOrderRepository;

    /**
     * 获取二维啊粘贴状态
     */
    @Autowired
    private ZkbAssetQrCodeInfoRepository zkbAssetQrCodeInfoRepository;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    /**
     * 保养申请单
     */
    @Autowired
    private V2_PmOrderRepository v2PmOrderRepository;

    @Autowired
    private AssetInfoMapper assetInfoMapper;

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
    public static final String monthDateFormat_two = "yyyy-MM";

    /**
     * 日期格式化
     */
    private static final String monthDateFormat = "yyyy年MM月";

    /**
     * 权重最高分
     */
    public static final int  totalFullScore = 100;

    /**
     * yes
     */
    public static final String yes ="Y";

    /**
     * no
     */
    public static final String no ="N";

    /**
     * 获取配置KPI生成配置权重
     */
    @Autowired
    private ZkbKeyBehaviorIndexConfigRepository zkbKeyBehaviorIndexConfigRepository;


    /**
     * 导出excel路径
     */
    public static final String excelTenant = "/portal/zkb/keyHehaviorIndex/Generate_KPI_DATA.xlsx";

    /**
     * 批量生成sheet名字
     */
    public static final String excelSheetNameOfTenant = "Tenant";

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public List<TenantInfo> getSourceTenantInfoList(String loginName,Date kpiDate){
        if(StringUtils.isNotBlank(loginName)&&loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
            return this.tenantInfoDao.findByType(3);
        //return this.tenantInfoDao.findByTypeAndNameNotLike(3,"ZKB%");
        //结束时间不能小于startdate 开始时间不能大于结束时间
        return this.tenantInfoDao.findByAnyContractDate(getFirstDay(kpiDate),getLastDay(kpiDate),3);
    }

    public List<TenantInfo> getSourceTenantInfoListForExcel(String loginName){
        if(StringUtils.isNotBlank(loginName)&&loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
            return this.tenantInfoDao.findByType(3);
        return this.tenantInfoDao.findByTypeAndNameNotLike(3,"ZKB%");
    }
    public Date getFirstDay(final Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        String lastDay = format.format(cal.getTime());
        lastDay = lastDay+"000000";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(lastDay);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return date1;
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
     * @param rankMap 排名
     * @param isZkbadmin 是否管理员
     * @return kpi 数据列表
     */
    public List<ZkbKeyBehaviorIndex> loadZkbKeyBehaviorIndex(String tenantUID,
                                                             Date beginningTime,
                                                             Date endingTime,
                                                             Map<String,Map<String,Integer>> rankMap,String isZkbadmin)
    {
        List<SearchFilter> searchFilters = this.generageSearchFilter(tenantUID,beginningTime,endingTime,isZkbadmin);
        List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList = this.zkbKeyBehaviorIndexMapper.loadZkbKeyBehaviorIndex(searchFilters);
        return this.rankZkbKeyBehaviorIndexList(zkbKeyBehaviorIndexList,rankMap);
    }

    /**
     * 按分数排名
     * @param zkbKeyBehaviorIndexList KPI LIST
     * @param rankMap 名次MAP
     * @return
     */
    public List<ZkbKeyBehaviorIndex> rankZkbKeyBehaviorIndexList(List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList, Map<String,Map<String,Integer>> rankMap)
    {
        if(rankMap==null)
        {
            return zkbKeyBehaviorIndexList;
        }
        if(CollectionUtils.isNotEmpty(zkbKeyBehaviorIndexList))
        {
            for(ZkbKeyBehaviorIndex item:zkbKeyBehaviorIndexList)
            {
                Map<String,Integer> tenantRankMap = rankMap.get(DateFormatUtils.format(item.getKbiDate(),monthDateFormat_two));
                if(tenantRankMap==null) continue;
                item.setRank(tenantRankMap.get(item.getTenantUID()));
            }
        }
        return zkbKeyBehaviorIndexList;
    }

    /**
     * 初始化排名
     * @return
     */
    public Map<String,Map<String,Integer>> initRankingMap() {
        List<String> dateStrList = this.zkbKeyBehaviorIndexRepository.findKbiDate();
        Map<String, Map<String, Integer>> rankMap = new HashMap<>();
        if (CollectionUtils.isEmpty(dateStrList)) {
            return rankMap;
        }
        for (String item : dateStrList) {
            if (StringUtils.isEmpty(item)) continue;
            List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList = this.zkbKeyBehaviorIndexMapper.initRanking(item, monthDateFormat_two);
            if (CollectionUtils.isEmpty(zkbKeyBehaviorIndexList)) continue;
            Map<String,Integer> tenantRankMap = new HashMap<>();
            for(ZkbKeyBehaviorIndex item1:zkbKeyBehaviorIndexList)
            {
                if(tenantRankMap.containsKey(item1.getTenantUID())) continue;
                tenantRankMap.put(item1.getTenantUID(),item1.getRank());
            }
            rankMap.put(item,tenantRankMap);
        }
        return rankMap;
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
                                                    Date endingTime,
                                                    String isZkbAdmin)
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
        if(!ZkbKeyBehaviorIndexService.yes.equals(isZkbAdmin))
        {
            searchFilters.add(new SearchFilter("effect", SearchFilter.Operator.EQ, yes));
        }
        return searchFilters;
    }
    /**
     * 获取平均 kpi 数据列表
     * @param tenantUID 租户UID
     * @param beginningTime 开始时间
     * @param endingTime 结束时间
     * @param isZkbAdmin 智康宝admin
     * @return kpi 数据列表
     */
    public List<ZkbKeyBehaviorIndex> loadAverageZkbKeyBehaviorIndex(String tenantUID,
                                                             Date beginningTime,
                                                             Date endingTime,
                                                             String isZkbAdmin)
    {
        List<SearchFilter> searchFilters = this.generageSearchFilter(tenantUID,beginningTime,endingTime,isZkbAdmin);
        return this.zkbKeyBehaviorIndexMapper.loadAverageZkbKeyBehaviorIndex(searchFilters);
    }

    /**
     * 生成excel
     * @param doc 文件
     */
    public void exportTenantData(ExcelDocument doc,List<TenantInfo> tenantInfoList) {
        List<Map<String, Object>> tenantInfoMapList = this.createTenantInfoData(tenantInfoList);
        Integer keyRow = 1;
        Integer dataRow = 2;
        if(CollectionUtils.isNotEmpty(tenantInfoMapList))
        {
            for (Map<String, Object> item : tenantInfoMapList) {
                doc.writeRowData(excelSheetNameOfTenant, keyRow, dataRow++, item);
            }
        }
    }

    /**
     * 租户信息
     * @param tenantInfoList 租户列表
     * @return
     */
    public List<Map<String, Object>> createTenantInfoData(List<TenantInfo> tenantInfoList)
    {
        List<Map<String, Object>> tenantInfoMapList = new ArrayList<>();
        if(CollectionUtils.isEmpty(tenantInfoList))
            return tenantInfoMapList;
        for(TenantInfo tenantInfo:tenantInfoList)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("ID",tenantInfo.getUid());
            map.put("代理商名称",tenantInfo.getName());
            tenantInfoMapList.add(map);
        }
        return tenantInfoMapList;
    }

    /**
     * 根据excel文件 读取文件中的数据
     * @param doc 文件
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getTenantMapFromTemplate(ExcelDocument doc) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_KBI_DATA_ROW ; i <= doc.getWorkBook().getSheet(excelSheetNameOfTenant).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(excelSheetNameOfTenant, EXCEL_KBI_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            TenantInfoVO tenantInfoVO = new TenantInfoVO();
            tenantInfoVO.setLineNum(i);
            TenantInfoRow tenantInfoRow = new TenantInfoRow(tenantInfoVO,rowMap);
            tenantInfoRow.setFieldStringValue("ID", "setUid");
            tenantInfoRow.setFieldStringValue("代理商名称","setName");
            tenantInfoRow.setFieldStringValue("年", "setYear");
            tenantInfoRow.setFieldStringValue("月", "setMonth");
            if(tenantInfoVO.isNull()){
                continue;
            }
            map.put("tenantInfoVO", tenantInfoVO);
            map.put("rowData", rowMap);
            map.put("hashCode", tenantInfoVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getKbiMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }

    /**
     * 组装报表数据
     * @param tenantUID 租户ID
     * @param beginningTime 开始时间
     * @param endingTime 结束时间
     */
    public LineChartModel generateCharts(String tenantUID,Date beginningTime,Date endingTime,String isZkbadmin)
    {
        LineChartModel areaModel = new LineChartModel();
        List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList = this.loadZkbKeyBehaviorIndex(tenantUID,beginningTime,endingTime,null,isZkbadmin);
        List<ZkbKeyBehaviorIndex> zkbAverageKeyBehaviorIndexList = this.loadAverageZkbKeyBehaviorIndex(null,beginningTime,endingTime,isZkbadmin);
        LineChartSeries series1 = new LineChartSeries();
        boolean showNonData = true;
        if(CollectionUtils.isNotEmpty(zkbKeyBehaviorIndexList))
        {
            series1.setLabel(zkbKeyBehaviorIndexList.get(0).getTenantName());
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
       String beginningTimeString =  this.getDateFormat(beginningTime,ZkbKeyBehaviorIndexService.monthDateFormat);
       String endingTimeString =  this.getDateFormat(endingTime,ZkbKeyBehaviorIndexService.monthDateFormat);
       return "KPI"+"("+beginningTimeString+"至"+endingTimeString+")";
    }

    /**
     * 日期格式化
     * @param date 时间
     * @param dateFormat 时间格式
     * @return
     */
    private String getDateFormat(Date date,String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }
    /**
     * 根据KPI数据组装导出excel需要的map
     * @param zkbKeyBehaviorIndexList KPI数据
     * @return 导出excel需要的kpi数据
     */
    public List<Map<String, Object>> createExportKeyBehaviorIndexData(List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList){
        List<Map<String, Object>> keyBehaviorIndexMapList = new ArrayList<>();
        for (ZkbKeyBehaviorIndex keyBehaviorIndex : zkbKeyBehaviorIndexList) {
            Map<String, Object> orderHistoryMap = new HashMap<>();
            List<ZkbContract> zkbContractList = zkbContractRepository.findByTenantUID(keyBehaviorIndex.getTenantUID());
            if(CollectionUtils.isNotEmpty(zkbContractList))
            {
                orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_region"),zkbContractList.get(0).getRegion());
            }
            orderHistoryMap.put(WebUtil.getMessage("zkbHistory_systemId"),keyBehaviorIndex.getId());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_tenant"),keyBehaviorIndex.getTenantName());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_kbiDate"),keyBehaviorIndex.getKbiDateFormat());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_rank"),keyBehaviorIndex.getRank());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_totalScore"),keyBehaviorIndex.getTotalScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_partReturnFullScore"),keyBehaviorIndex.getPartReturnFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_partReturnScore"),keyBehaviorIndex.getPartReturnScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeFullScore"),keyBehaviorIndex.getSatisfactoryDegreeFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_satisfactoryDegreeScore"),keyBehaviorIndex.getSatisfactoryDegreeScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_apmBillingRateFullScore"),keyBehaviorIndex.getApmBillingRateFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_apmBillingRateScore"),keyBehaviorIndex.getApmBillingRateScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_pasteQuickResponseRateFullScore"),keyBehaviorIndex.getPasteQuickResponseRateFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_pasteQuickResponseRateScore"),keyBehaviorIndex.getPasteQuickResponseRateScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceFullScore"),keyBehaviorIndex.getComplianceFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_complianceScore"),keyBehaviorIndex.getComplianceScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerFullScore"),keyBehaviorIndex.getEngineerFullScore());
            orderHistoryMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_engineerScore"),keyBehaviorIndex.getEngineerScore());
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
     * 导入代理商数据
     * @param tenantMapList 代理商列表
     */
    public void importTenantData(List<Map<String, Object>> tenantMapList) {
        int row = 0;
        boolean ok = true;
        try {
            for (Map<String, Object> item : tenantMapList) {
                ++row;
                if(!checkTenantData(item)) {
                    ok=false;
                }
            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        if(!ok)
            return;
        for(Map<String, Object> item : tenantMapList)
        {
            TenantInfoVO tenantInfoVO = (TenantInfoVO)item.get("tenantInfoVO");
            Date kpiDate = DateUtil.getDateByFormatStr(tenantInfoVO.getYear()+"-"+tenantInfoVO.getMonth(),"yyyy-MM");
            TenantInfo tenantInfo = this.tenantInfoDao.findByUid(tenantInfoVO.getUid());
            this.generateSingleKpiData(tenantInfo,kpiDate);
        }
    }
    public Date getLastDay(final Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = format.format(cal.getTime());
        lastDay = lastDay+"235959";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexController.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(lastDay);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return date1;
    }

    public Date getMonthOfFirstDay(final Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        String lastDay = format.format(cal.getTime());
        lastDay = lastDay+"000000";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexController.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(lastDay);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return date1;
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
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, errorMessage.toString());
            isValidateData = false;
            return isValidateData;
        }
        return isValidateData;
    }

    /**
     * 组装单个KPI
     * @param tenantUID
     * @return
     */
    public ZkbKeyBehaviorIndex prepareGenerateSingleKpiData(String tenantUID,Date kpiDate)
    {
        ZkbKeyBehaviorIndexConfig zkbKeyBehaviorIndexConfig = this.zkbKeyBehaviorIndexConfigRepository.getDefaultConfig();
        List<ZkbKeyBehaviorIndexConfig>  list = this.zkbKeyBehaviorIndexConfigRepository.findByTenantUID(tenantUID);
        if(CollectionUtils.isNotEmpty(list))
        {
            zkbKeyBehaviorIndexConfig=list.get(0);
        }
        ZkbKeyBehaviorIndex zkbKeyBehaviorIndex = new ZkbKeyBehaviorIndex();
        zkbKeyBehaviorIndex.setPartReturnFullScore(zkbKeyBehaviorIndexConfig.getPartReturnFullScore());
        zkbKeyBehaviorIndex.setPasteQuickResponseRateFullScore(zkbKeyBehaviorIndexConfig.getPasteQuickResponseRateFullScore());
        zkbKeyBehaviorIndex.setApmBillingRateFullScore(zkbKeyBehaviorIndexConfig.getApmBillingRateFullScore());
        zkbKeyBehaviorIndex.setSatisfactoryDegreeFullScore(zkbKeyBehaviorIndexConfig.getSatisfactoryDegreeFullScore());
        zkbKeyBehaviorIndex.setComplianceFullScore(zkbKeyBehaviorIndexConfig.getComplianceFullScore());
        zkbKeyBehaviorIndex.setEngineerFullScore(zkbKeyBehaviorIndexConfig.getEngineerFullScore());
        zkbKeyBehaviorIndex.setTenantUID(tenantUID);
        zkbKeyBehaviorIndex.setSatisfactoryDegreeScore(zkbKeyBehaviorIndexConfig.getSatisfactoryDegreeFullScore());
        zkbKeyBehaviorIndex.setComplianceScore(zkbKeyBehaviorIndexConfig.getComplianceFullScore());
        zkbKeyBehaviorIndex.setEngineerScore(zkbKeyBehaviorIndexConfig.getEngineerFullScore());
        zkbKeyBehaviorIndex.setKbiDate(kpiDate);
        return zkbKeyBehaviorIndex;
    }

    public void generateSingleKpiData(TenantInfo item,Date kpiDate)
    {
        Date lastDay = this.getLastDay(kpiDate);//最后一体天
        Date monthOfFirstDay = this.getMonthOfFirstDay(kpiDate);
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexService.monthDateFormat_two);
        String dateStry = formatter.format(kpiDate);
        ZkbKeyBehaviorIndex zkbKeyBehaviorIndex = this.prepareGenerateSingleKpiData(item.getUid(),kpiDate);
        this.calculateScore(zkbKeyBehaviorIndex,item.getUid(),lastDay,monthOfFirstDay);
        List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList = this.zkbKeyBehaviorIndexRepository.findByTenantUIDAndKbiDate(item.getUid(),ZkbKeyBehaviorIndexService.monthDateFormat_two,dateStry);
        if(CollectionUtils.isNotEmpty(zkbKeyBehaviorIndexList))
        {
            ZkbKeyBehaviorIndex oldZkbKeyBehaviorIndex =zkbKeyBehaviorIndexList.get(0);
            zkbKeyBehaviorIndex.setId(oldZkbKeyBehaviorIndex.getId());
        }
        this.zkbKeyBehaviorIndexRepository.save(zkbKeyBehaviorIndex);
    }

    public ZkbKeyBehaviorIndex generateSingleKpiDataOfCCSB(final ZkbKeyBehaviorIndex zkbKeyBehaviorIndexFinal){
          ZkbKeyBehaviorIndex zkbKeyBehaviorIndex = this.prepareGenerateSingleKpiData(zkbKeyBehaviorIndexFinal.getTenantUID(),zkbKeyBehaviorIndexFinal.getKbiDate());
        this.calculateScoreOfCCSB(zkbKeyBehaviorIndex,zkbKeyBehaviorIndexFinal.getTenantUID(),
                this.getLastDay(zkbKeyBehaviorIndexFinal.getKbiDate()),this.getMonthOfFirstDay(zkbKeyBehaviorIndexFinal.getKbiDate()));
         return zkbKeyBehaviorIndex;
    }

    /**
     * 检查数据是否合法
     * 1.数据类型字段必填
     * @param item
     * @return
     */
    public boolean checkTenantData(Map<String, Object> item) {
        TenantInfoVO tenantInfoVO = (TenantInfoVO)item.get("tenantInfoVO");
        boolean isValidateData = true;
        AssetInfoRow.clearErrMessage(item);
        item.put("status", ZkbOrderHistoryService.ImportStatus.New);
        if(StringUtils.isBlank(tenantInfoVO.getUid())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbKeyBehaviorIndex_idCannotBeNull"));
            isValidateData = false;
        }
        if(StringUtils.isBlank(tenantInfoVO.getYear())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbKeyBehaviorIndex_yearCannotBeNull"));
            isValidateData = false;
        }
        if(StringUtils.isBlank(tenantInfoVO.getMonth())){
            item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbKeyBehaviorIndex_monthCannotBeNull"));
            isValidateData = false;
        }
        if(StringUtils.isNotBlank(tenantInfoVO.getUid()))
        {
            TenantInfo tenantInfo = tenantInfoDao.findByUid(tenantInfoVO.getUid());
            if(tenantInfo==null)
            {
                item.put("status", ZkbOrderHistoryService.ImportStatus.Failure);
                AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbKeyBehaviorIndex_canNotFindTenantByUid"));
                isValidateData = false;
            }

        }
        return isValidateData;
    }

    /**
     * 计算分数
     * @param zkbKeyBehaviorIndex kbi实体
     * @param tenantUID 租户
     * @param kpiDate 对应KPI时间
     * @param firstDay 第一时间
     */
    public void calculateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Date kpiDate,Date firstDay)
    {
        StringBuffer remark = new StringBuffer();
        this.calculatePartReturnScore(zkbKeyBehaviorIndex,tenantUID,kpiDate,firstDay,remark);
        Integer totalIbNumber = this.calculatePasteQuickResponseRateScore(zkbKeyBehaviorIndex,tenantUID,kpiDate,firstDay,remark);
        this.calculateApmBillingRateScore(zkbKeyBehaviorIndex,tenantUID,totalIbNumber,kpiDate,firstDay,remark);
        zkbKeyBehaviorIndex.setRemark(remark.toString());
    }

    /**
     * ccsb重新计算分数用于生成积分
     * @param zkbKeyBehaviorIndex kbi实体
     * @param tenantUID 租户
     * @param kpiDate 对应KPI时间
     * @param firstDay 第一时间
     */
    public void calculateScoreOfCCSB(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Date kpiDate,Date firstDay){
        StringBuilder remark = new StringBuilder();
        this.calculatePartReturnScoreOfCCSB(zkbKeyBehaviorIndex,tenantUID,kpiDate,firstDay,remark);
        Integer totalIbNumber = this.calculatePasteQuickResponseRateScoreOfCCSB(zkbKeyBehaviorIndex,tenantUID,kpiDate,firstDay,remark);
        this.calculateApmBillingRateScoreOfCCSB(zkbKeyBehaviorIndex,tenantUID,totalIbNumber,kpiDate,firstDay,remark);
        zkbKeyBehaviorIndex.setRemarkCcsb(remark.toString());
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
    public Date lastDayOnMonth(LocalDateTime date)
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
     * @param tenantUID 租户tenantUID
     * @param kpiDate kpi时间
     * @param remark 生成备注
     */
    private void calculatePartReturnScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Date kpiDate,Date firstDate,StringBuffer remark)
    {
        remark=remark.append("calculatePartReturnScore--");
        Calendar cd = Calendar.getInstance();
        cd.setTime(kpiDate);
        cd.add(Calendar.DATE,-14);
        Date nowBefore = cd.getTime();
        Integer unReturnCount  = this.zkbPartOrderRepository.fetchUnReturnCount(nowBefore,ZkbPartOrder.OrderType.Probe.toString(),tenantUID);
        if(unReturnCount==null)
        {
            unReturnCount=0;
        }
        remark=remark.append("unReturnCount:").append(unReturnCount).append(";");
        Integer applyPartsCount = this.zkbPartOrderRepository.fetchApplyPartsQty(nowBefore,ZkbPartOrder.OrderType.Parts.toString(),tenantUID);
        if(applyPartsCount==null)
        {
            applyPartsCount=0;
        }
        remark=remark.append("applyPartsCount:").append(applyPartsCount).append(";");
        Integer returnQty = this.zkbPartOrderReturnRepository.fetchQty(nowBefore,ZkbPartOrder.OrderType.Parts.toString(),tenantUID);
        if(returnQty==null)
        {
            returnQty = new Integer(0);
        }
        remark=remark.append("returnQty:").append(returnQty).append(";");
        Integer unReturnApplyPartsCount = applyPartsCount-returnQty;
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


    /**
     * ccsb类型合同重新计算得分用于生成积分
     * @param zkbKeyBehaviorIndex KPI
     * @param tenantUID 租户tenantUID
     * @param kpiDate kpi时间
     */
    private void calculatePartReturnScoreOfCCSB(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Date kpiDate,Date firstDate,StringBuilder remark){
        remark=remark.append("ScoreOfCCSB--");
        Calendar cd = Calendar.getInstance();
        cd.setTime(kpiDate);
        cd.add(Calendar.DATE,-18);
        Date nowBefore = cd.getTime();
        Integer unReturnCount  = this.zkbPartOrderRepository.fetchUnReturnCountOfCCSB(nowBefore,ZkbPartOrder.OrderType.Probe.toString(),tenantUID);
        remark=remark.append("ReturnScore:").append(unReturnCount).append(";");
        Integer applyPartsCount = this.zkbPartOrderRepository.fetchApplyPartsQtyOfCCSB(nowBefore,ZkbPartOrder.OrderType.Parts.toString(),tenantUID);
        remark=remark.append(":").append(applyPartsCount).append(";");
        Integer returnQty = this.zkbPartOrderReturnRepository.fetchQtyOfCCSB(nowBefore,ZkbPartOrder.OrderType.Parts.toString(),tenantUID);
        remark=remark.append(":").append(returnQty).append(";");
        Integer unReturnApplyPartsCount = applyPartsCount-returnQty;
        BigDecimal result = new BigDecimal(14).divide(new BigDecimal(5),2,BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(unReturnApplyPartsCount+unReturnCount)).setScale(0,BigDecimal.ROUND_HALF_DOWN);
        remark=remark.append(":").append(result).append(";");
        BigDecimal partReturnFullScore = new BigDecimal(zkbKeyBehaviorIndex.getPartReturnFullScore());
        if(result.compareTo(partReturnFullScore)>0){
            zkbKeyBehaviorIndex.setPartReturnScore(0);
        }else{
            zkbKeyBehaviorIndex.setPartReturnScore(partReturnFullScore.subtract(result).intValue());
        }
        remark=remark.append(":").append(zkbKeyBehaviorIndex.getPartReturnScore()).append(";");


    }

    /**
     * 计算二维码粘贴率得分
     * @param zkbKeyBehaviorIndex 当前用户得分
     * @param tenantUID 租户 tenantUID
     * @param kpiDate 某月KPI
     * @param remark 备注
     */
    private Integer calculatePasteQuickResponseRateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Date kpiDate,Date firstDate,StringBuffer remark)
    {
        remark=remark.append("calculatePasteQuickResponseRateScore--");
        //Integer totalIbNumber = this.zkbContractRepository.fetchIbNumber(tenantUID,firstDate,kpiDate);
        Integer totalIbNumber = this.zkbContractRepository.fetchIbNumberNew(tenantUID,firstDate,kpiDate);

       /* if(totalIbNumber==null)
        {
            totalIbNumber=new Integer(0);
        }*/
        remark=remark.append("totalIbNumber:").append(totalIbNumber).append(";");
        if(totalIbNumber==0)
        {
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(0);
            return totalIbNumber;
        }
        List<ZkbContractAsset> zkbContractAssetList = this.zkbContractAssetRepository.findZkbContractAssetByTenantUID(tenantUID,firstDate,kpiDate);

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
        /*Integer count =  this.zkbAssetQrCodeInfoRepository.findCount(assetId,tenantUID,yes);
        Integer countAssetWithQRcode = this.assetInfoMapper.countAssetWithQRcode(assetId,tenantUID);
        if(countAssetWithQRcode==null)
        {
            countAssetWithQRcode=0;
        }
        count=count==null?0:count;
        countAssetWithQRcode=countAssetWithQRcode+count;*/
        //二维码是否已实施 or 是否设备无法粘贴二维码    拆机是  否
        //新逻辑    22.6.9
        Integer qrCodePasteCount = this.zkbAssetQrCodeInfoRepository.qrCodePasteCount(assetId,tenantUID);//已经贴码设备
        Integer qrCodePasteScrappedCount = this.zkbAssetQrCodeInfoRepository.qrCodePasteScrappedCount(assetId,tenantUID);//贴码状态的拆机设备
        Integer scrappedCount = this.zkbAssetQrCodeInfoRepository.scrappedCount(assetId,tenantUID);//拆机设备
        logger.info("qrCodePasteCount", qrCodePasteCount+";qrCodePasteScrappedCount"+qrCodePasteScrappedCount+";scrappedCount"+scrappedCount);
        remark=remark.append("RateScore:").append(qrCodePasteCount).append(";").append(qrCodePasteScrappedCount).append(";").append(scrappedCount).append(";");
        Integer fullScore = zkbKeyBehaviorIndex.getPasteQuickResponseRateFullScore();
        Integer pasteQuickResponseRateScore = new BigDecimal(qrCodePasteCount-qrCodePasteScrappedCount).
                divide(new BigDecimal(totalIbNumber-scrappedCount),2,BigDecimal.ROUND_HALF_DOWN).
                multiply(new BigDecimal(fullScore)).setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
        logger.info("pasteQuickResponseRateScore，fullScore:", pasteQuickResponseRateScore+","+fullScore);
        if(pasteQuickResponseRateScore>fullScore)
        {
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(fullScore);
        }else{
                zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(pasteQuickResponseRateScore);
        }
        return totalIbNumber;
    }



    /**
     * 计算二维码粘贴率得分
     * @param zkbKeyBehaviorIndex 当前用户得分
     * @param tenantUID 租户 tenantUID
     * @param kpiDate 某月KPI
     */

    private Integer calculatePasteQuickResponseRateScoreOfCCSB(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Date kpiDate,Date firstDate,StringBuilder remark) {
        remark=remark.append("RateScoreOfCCSB--");
        Integer totalIbNumber = this.zkbContractRepository.fetchIbNumberOfNewCCSB(tenantUID,firstDate,kpiDate);
        remark=remark.append(":").append(totalIbNumber).append(";");

        if(totalIbNumber==0){
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(0);
            return totalIbNumber;
        }
        List<ZkbContractAsset> zkbContractAssetList = this.zkbContractAssetRepository.findZkbContractAssetByTenantUIDOfCCSB(tenantUID,firstDate,kpiDate);
        if(CollectionUtils.isEmpty(zkbContractAssetList)){
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(0);
            return totalIbNumber;
        }
        List<String> assetId = new ArrayList<>();
        for(ZkbContractAsset item:zkbContractAssetList){
            assetId.add(item.getAssetId());
        }
        Integer qrCodePasteCount = this.zkbAssetQrCodeInfoRepository.qrCodePasteCountOfCCSB(assetId,tenantUID);//已经贴码设备
        Integer qrCodePasteScrappedCount = this.zkbAssetQrCodeInfoRepository.qrCodePasteScrappedCountOfCCSB(assetId,tenantUID);//贴码状态的拆机设备
        Integer scrappedCount = this.zkbAssetQrCodeInfoRepository.scrappedCountOfCCSB(assetId,tenantUID);//拆机设备
        Integer fullScore = zkbKeyBehaviorIndex.getPasteQuickResponseRateFullScore();
        Integer pasteQuickResponseRateScore = new BigDecimal(qrCodePasteCount-qrCodePasteScrappedCount).
                divide(new BigDecimal(totalIbNumber-scrappedCount),2,BigDecimal.ROUND_HALF_DOWN).
                multiply(new BigDecimal(fullScore)).setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
        remark=remark.append(":").append(qrCodePasteCount).append(";").append(qrCodePasteScrappedCount).append(";").
                append(scrappedCount).append(";").append(fullScore).append(";").append(pasteQuickResponseRateScore).append(";");
        if(pasteQuickResponseRateScore>fullScore){
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(fullScore);
        }else{
            zkbKeyBehaviorIndex.setPasteQuickResponseRateScore(pasteQuickResponseRateScore);
        }
        remark=remark.append(":").append(zkbKeyBehaviorIndex.getPasteQuickResponseRateScore()).append(";");
        return totalIbNumber;
    }
    /**
     * 计算APM开单率得分
     * @param zkbKeyBehaviorIndex
     * @param tenantUID 合同列表
     * @param totalIbNumber ib 总量
     * @param remark 备注
     */
    private void calculateApmBillingRateScore(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Integer totalIbNumber,Date kpiDate,Date firstDate,StringBuffer remark)
    {
        remark=remark.append("calculateApmBillingRateScore--");
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexService.monthDateFormat_two);
        String dateStry = formatter.format(kpiDate);
        if(totalIbNumber==null||totalIbNumber==0)
        {
            zkbKeyBehaviorIndex.setApmBillingRateScore(0);
            return;
        }
        Integer countWorkOrder = this.v2_WorkOrderRepository.fetchCount(ZkbKeyBehaviorIndexService.monthDateFormat_two,dateStry,tenantUID);
        Integer countPmOrder = this.v2PmOrderRepository.fetchCount(monthDateFormat_two,dateStry,tenantUID);
        Integer highMachineCount = this.zkbContractRepository.fetchHighMachineCount(tenantUID,firstDate,kpiDate);
        if(highMachineCount==null)
        {
            highMachineCount=0;
        }
        if(countWorkOrder==null)
        {
            countWorkOrder=new Integer(0);
        }
        if(countPmOrder==null)
        {
            countPmOrder=new Integer(0);
        }
        remark=remark.append("countWorkOrder:").append(countWorkOrder).append(";");
        remark=remark.append("countPmOrder:").append(countPmOrder).append(";");
        remark=remark.append("totalIbNumber:").append(totalIbNumber).append(";");
        remark=remark.append("highMachineCount:").append(highMachineCount).append(";");
        Integer divide =totalIbNumber-highMachineCount;
        if(divide<=0)
        {
            zkbKeyBehaviorIndex.setApmBillingRateScore(0);
            return;
        }
        Integer apmBillingRateScore = new BigDecimal(countWorkOrder+countPmOrder).
                divide(new BigDecimal(divide),2,BigDecimal.ROUND_HALF_DOWN).
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
     * 计算APM开单率得分
     * @param zkbKeyBehaviorIndex
     * @param tenantUID 合同列表
     * @param totalIbNumber ib 总量
     */
    private void calculateApmBillingRateScoreOfCCSB(ZkbKeyBehaviorIndex zkbKeyBehaviorIndex,String tenantUID,Integer totalIbNumber,Date kpiDate,Date firstDate,StringBuilder remark) {
        remark=remark.append("ApmScoreOfCCSB--");


        SimpleDateFormat formatter = new SimpleDateFormat(ZkbKeyBehaviorIndexService.monthDateFormat_two);
        String dateStry = formatter.format(kpiDate);
        if(totalIbNumber==null||totalIbNumber==0)
        {
            zkbKeyBehaviorIndex.setApmBillingRateScore(0);
            return;
        }
        Integer countWorkOrder = this.v2_WorkOrderRepository.fetchCountOfCCSB(ZkbKeyBehaviorIndexService.monthDateFormat_two,dateStry,tenantUID);
        Integer countPmOrder = this.v2PmOrderRepository.fetchCountOfCCSB(monthDateFormat_two,dateStry,tenantUID);
        Integer highMachineCount = this.zkbContractRepository.fetchHighMachineCountOfCCSB(tenantUID,firstDate,kpiDate);
        remark=remark.append(":").append(countWorkOrder).append(";");
        remark=remark.append(":").append(countPmOrder).append(";");
        remark=remark.append(":").append(totalIbNumber).append(";");
        remark=remark.append(":").append(highMachineCount).append(";");
        Integer divide =totalIbNumber-highMachineCount;
        if(divide<=0) {
            zkbKeyBehaviorIndex.setApmBillingRateScore(0);
            return;
        }
        Integer apmBillingRateScore = new BigDecimal(countWorkOrder+countPmOrder).
                divide(new BigDecimal(divide),2,BigDecimal.ROUND_HALF_DOWN).
                multiply(new BigDecimal(100)).
                setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
        remark=remark.append(":").append(apmBillingRateScore).append(";");
        if(apmBillingRateScore>zkbKeyBehaviorIndex.getApmBillingRateFullScore()){
            remark=remark.append(":").append(zkbKeyBehaviorIndex.getApmBillingRateScore()).append(";");
            zkbKeyBehaviorIndex.setApmBillingRateScore(zkbKeyBehaviorIndex.getApmBillingRateFullScore());
            return;
        }
        zkbKeyBehaviorIndex.setApmBillingRateScore(apmBillingRateScore);
        remark=remark.append(":").append(zkbKeyBehaviorIndex.getApmBillingRateScore()).append(";");
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
        Map tenantUIDMap = new HashMap();
        List<String> tenantUIDList = new ArrayList<>();
        for(ZkbKeyBehaviorIndex item:zkbKeyBehaviorIndexList)
        {
            if(tenantUIDMap.containsKey(item.getTenantUID())) continue;
            tenantUIDList.add(item.getTenantUID());
            tenantUIDMap.put(item.getTenantUID(),item.getTenantUID());
        }

        List<ZkbContract> zkbContractList = this.zkbContractMapper.getContractByTenantUIDList(tenantUIDList);
        for (ZkbContract zkbContract : zkbContractList) {
            Map<String, Object> zkbContractMap = new HashMap<>();
            zkbContractMap.put(WebUtil.getMessage("zkbHistory_systemId"),zkbContract.getId());
            zkbContractMap.put(WebUtil.getMessage("zkbKeyBehaviorIndex_tenant"),zkbContract.getTenantName());
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

    /**
     * 生效
     * @param beginningTime 生效时间
     * @param tenantUID 哪些租户
     * @param currentUser 当前操作用户
     * @param currentZkbKeyBehaviorIndexList 当前页面列表
     */
    public void effect(Date beginningTime,String tenantUID,UserAccount currentUser,List<ZkbKeyBehaviorIndex> currentZkbKeyBehaviorIndexList)
    {
        Date date = null;
        if(beginningTime!=null)
        {
            date=beginningTime;
        }else
            {
                date=getPreviousMonth();
            }
        String dateStr = this.getDateFormat(date,monthDateFormat_two);
        List<ZkbKeyBehaviorIndex> zkbKeyBehaviorIndexList = this.zkbKeyBehaviorIndexMapper.findByDateAndTenantUIDOfCCSB(dateStr,monthDateFormat_two,tenantUID);
        this.zkbKeyBehaviorIndexMapper.batchUpdate(dateStr,monthDateFormat_two,tenantUID);
        if(CollectionUtils.isEmpty(zkbKeyBehaviorIndexList))
        {
            return;
        }
        for(ZkbKeyBehaviorIndex item:zkbKeyBehaviorIndexList)
        {
            String recordType = "";
            if(no.equals(item.getEffect()))
            {
                recordType= ZkbKeyBehaviorIndexController.kpi_add;
            }else
            {
                recordType=ZkbKeyBehaviorIndexController.kpi_modify;
            }
            this.changeScore(item,recordType,currentUser);
            if(CollectionUtils.isEmpty(currentZkbKeyBehaviorIndexList))
            {
                continue;
            }
            for(ZkbKeyBehaviorIndex item1:currentZkbKeyBehaviorIndexList)
            {
                if(item1.getId().equals(item.getId()))
                {
                    item1.setEffect(yes);
                }
            }
        }
    }

    /**
     * 改变积分
     * @param item kpi数据
     * @param recordType kpi单挑记录
     * @param currentUser 当前用户
     */
    public void changeScore(ZkbKeyBehaviorIndex item,String recordType,UserAccount currentUser) {
        Integer totalScore = item.getTotalScore();
        if(item.isRecalculate()){
            ZkbKeyBehaviorIndex handItem = generateSingleKpiDataOfCCSB(item);
            totalScore = handItem.getTotalScore();
            zkbKeyBehaviorIndexRepository.updateRemarkCcsb(handItem.getRemarkCcsb()+handItem.getTotalScore()+"kpi总分;",item.getId());
        }
        zkbIntegralService.kpiScoreHandler(item.getTenantUID(),totalScore,recordType,currentUser,this.getDateFormat(item.getKbiDate(),monthDateFormat_two));
    }

    public Date getPreviousMonth()
    {
        Date now = new Date();
        Calendar cd = Calendar.getInstance();
        cd.setTime(now);
        cd.add(Calendar.MONTH,-1);
        return cd.getTime();
    }

}
