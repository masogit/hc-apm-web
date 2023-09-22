package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbAssetQrCodeInfoRepository;
import com.ge.apm.dao.mapper.ZkbAssetStatisticMapper;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbAssetQrCodeInfo;
import com.ge.apm.domain.ZkbAssetStatistic;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
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
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 503202755 2020/10/23
 */
@Component
public class ZkbAssetStatisticService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbAssetStatisticService.class);

    /**
     * 历史数据查询mapper
     */
    @Autowired
    private ZkbAssetStatisticMapper zkbAssetStatisticMapper;

    /**
     * excel模板名称
     */
    public static final String EXCEL_TEMPLATE_NAME="Asset_Statistic_Template.xlsx";

    /**
     * excel模板英文名称
     */
    public static final String EXCEL_TEMPLATE_EN_NAME="Asset_Statistic_Template_EN.xlsx";

    /**
     * 目录地址
     */
    public static final String DIRECOTORY_PATH="/portal/zkb/assetStatistic/";

    /**
     * excel sheet 名称
     */
    public static final String SHEET_ASSET_STATISTIC_LIST = "ASSET STATISTIC LIST";

    private final String[] HEADER_FIELD_CN_NAME = {"代理商","systemID","是否设备无法粘贴二维码","无法粘贴二维码原因","无法贴码原因归类"};//header名
    private final String[] HEADER_FIELD = {"tenantName","systemId","canNotPastQrCode","reason","reasonCategorize"};  //字段名
    private final String[] HEADER_FIELD_REQUIRED = {"tenantName","systemId","canNotPastQrCode"};  //必填字段名

    private final String SCRAPPED = "已拆机或已报废";
    private final String NOTALLOWED = "与医院无保修，医院不允许贴码";
    private final String NOTFIND = "设备找不到";
    private final String OTHER = "其他";

    @Autowired
    private ZkbAssetQrCodeInfoRepository zkbAssetQrCodeInfoRepository;

    /**
     * 根据添加统计设备信息
     * @param tenantUid 租户UID
     * @param systemId SystemID/厂商报修ID
     * @param qrCodeCount 二维码个数
     * @param qrCodeCountState 二维码状态
     * @param contractNo 合同编号
     * @param hospitalName 医院名称
     * @param functionType 设备型号
     * @param canNotPastQrCode 无法粘贴二维码
     * @return
     */
    public List<ZkbAssetStatistic> loadAllAsset(String tenantUid,String systemId,String qrCodeCount,
                                                String qrCodeCountState,String contractNo,String hospitalName,String functionType,String canNotPastQrCode){
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if(tenantUid!=null)
        {
            SearchFilter dataTypeFilter = new SearchFilter("tenantUid", SearchFilter.Operator.EQ, tenantUid);
            searchFilters.add(dataTypeFilter);
        }
        if(systemId!=null)
        {
            SearchFilter systemIdFilter = new SearchFilter("systemId", SearchFilter.Operator.EQ, systemId);
            searchFilters.add(systemIdFilter);
        }
        if(contractNo!=null)
        {
            SearchFilter systemIdFilter = new SearchFilter("contractNo", SearchFilter.Operator.EQ, contractNo);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(hospitalName))
        {
            SearchFilter systemIdFilter = new SearchFilter("hospitalName", SearchFilter.Operator.EQ, hospitalName);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(functionType))
        {
            SearchFilter systemIdFilter = new SearchFilter("functionType", SearchFilter.Operator.EQ, functionType);
            searchFilters.add(systemIdFilter);
        }
        if(StringUtils.isNotBlank(canNotPastQrCode))
        {
            SearchFilter systemIdFilter = new SearchFilter("canNotPastQrCode", SearchFilter.Operator.EQ, canNotPastQrCode);
            searchFilters.add(systemIdFilter);
        }
        searchFilters.add(new SearchFilter("installStatus", SearchFilter.Operator.NE, 2));
        List<ZkbAssetStatistic> assetList = zkbAssetStatisticMapper.loadAllAsset(searchFilters);
        if(CollectionUtils.isEmpty(assetList))
        {
            return null;
        }
        List<String> assetUIDList =assetList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getAssetUid()))),
                        ArrayList::new)).stream().map(ZkbAssetStatistic::getAssetUid).collect(Collectors.toList());
        List<ZkbAssetStatistic> assetList1 = zkbAssetStatisticMapper.loadQrCount(assetUIDList);
        if(CollectionUtils.isNotEmpty(assetList1))
        {
            Map<String, ZkbAssetStatistic> map = assetList1.stream().collect(Collectors.toMap(ZkbAssetStatistic::getBoUuid, Function.identity()));
            for(ZkbAssetStatistic zkbAssetStatistic:assetList)
            {
                ZkbAssetStatistic item=map.get(zkbAssetStatistic.getAssetUid());
                if(item==null) continue;
                zkbAssetStatistic.setQrCodeCount(item.getQrCodeCount());
            }
        }
        if(StringUtils.isNotBlank(qrCodeCount))
        {
            assetList=assetList.stream().filter(s->qrCodeCount.equals(s.getQrCodeCount())).collect(Collectors.toList());
        }
        if(StringUtils.isNotBlank(qrCodeCountState))
        {
            assetList=assetList.stream().filter(s->qrCodeCountState.equals(s.getQrCodeCountState())).collect(Collectors.toList());
        }
        if(StringUtils.isNotBlank(canNotPastQrCode))
        {
            assetList=assetList.stream().filter(s->canNotPastQrCode.equals(s.getCanNotPastQrCode())).collect(Collectors.toList());
        }
        return assetList;
    }

    /**
     * 整理需要导出的数据
     * @param zkbAssetStatisticList
     * @return
     */
    public List<Map<String, Object>> createZkbAssetStatisticData(List<ZkbAssetStatistic> zkbAssetStatisticList){
        List<Map<String, Object>> zkbAssetStatisticMapList = new ArrayList<>();
        if(CollectionUtils.isEmpty(zkbAssetStatisticList))
        {
            return zkbAssetStatisticMapList;
        }
        for (ZkbAssetStatistic zkbAssetStatistic : zkbAssetStatisticList) {
            Map<String, Object> zkbAssetStatisticMap = new HashMap<>();
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbHistory_agent"),zkbAssetStatistic.getName());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_hospitalName"),zkbAssetStatistic.getHospitalName());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_functionType"),zkbAssetStatistic.getFunctionType());
            zkbAssetStatisticMap.put(WebUtil.getMessage("systemId"),zkbAssetStatistic.getSystemId());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_countOfImage"),zkbAssetStatistic.getQrCodeCount());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_contractNo"),zkbAssetStatistic.getContractNo());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_qrCodeOfPaste"),zkbAssetStatistic.getQrCodeCountState());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_canNotPast"),zkbAssetStatistic.getCanNotPastQrCode());
            zkbAssetStatisticMap.put(WebUtil.getMessage("zkbAssetStatistic_canNotPastReason"),zkbAssetStatistic.getReason());
            zkbAssetStatisticMapList.add(zkbAssetStatisticMap);
        }
        return zkbAssetStatisticMapList;
    }
    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbAssetStatisticMapList) {
        Integer dataRow = 2;
        for (Map<String, Object> item : zkbAssetStatisticMapList) {
            doc.writeRowData(SHEET_ASSET_STATISTIC_LIST, 1, dataRow++, item);
        }
    }

    public void regenerateZkbAssetQrCodeInfo(List<SearchFilter> searchFilter){
        //先清除已过期的合同中关联的设备、已修改system_id和已失效的设备数据
        zkbAssetQrCodeInfoRepository.deleteZkbAssetQrCodeInfo(getDateStr());
        // TODO 查出所有合同有效内关联设备的贴码数据，兼顾历史数据的更新。只限于WEB端，Schedule微服务那边只查询为"NULL"、"N"的数据
        List<ZkbAssetQrCodeInfo> zkbAssetQrCodeInfoList = zkbAssetStatisticMapper.getZkbAssetQrCodeInfoList(null);
        if(zkbAssetQrCodeInfoList == null || zkbAssetQrCodeInfoList.size() == 0){
            WebUtil.addErrorMessage("没有新的二维码记录需要生成");
            return ;
        }
        for (ZkbAssetQrCodeInfo zkbAssetQrCodeInfo : zkbAssetQrCodeInfoList){
            handleQRCodeInfo(zkbAssetQrCodeInfo);
        }
    }

    private String getDateStr()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(new Date());
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleQRCodeInfo(ZkbAssetQrCodeInfo zkbAssetQrCodeInfo){
        if(zkbAssetQrCodeInfo == null){
            return;
        }
        String qrCodeCountState = zkbAssetQrCodeInfo.getQrCodeCountState();
        Date date = new Date();
        if(qrCodeCountState == null || qrCodeCountState.equals("N")){
            if("Y".equals(zkbAssetQrCodeInfo.getIbQrCodeState())){
                zkbAssetQrCodeInfo.setQrCodeDate(date);//为Y 时需要记录时间
                zkbAssetQrCodeInfo.setQrCodeCountState("Y");//二维码是否已实施
                zkbAssetQrCodeInfo.setQrCodeState(null);//是否已贴旧二维码
            }else{
                zkbAssetQrCodeInfo.setQrCodeState(null);
                Integer qrCodeCount = zkbAssetQrCodeInfoRepository.findAttachedCount(zkbAssetQrCodeInfo.getAssetUid());
                if(qrCodeCount != null && qrCodeCount == 3){
                    zkbAssetQrCodeInfo.setQrCodeState("Y");
                    zkbAssetQrCodeInfo.setQrCodeDate(date);
                    zkbAssetQrCodeInfo.setQrCodeCountState("Y");
                }
            }
            if(zkbAssetQrCodeInfo.getId() == null){//ID == null 则新增记录
                if(StringUtils.isBlank(zkbAssetQrCodeInfo.getQrCodeCountState())){
                    zkbAssetQrCodeInfo.setQrCodeCountState("N");
                }
                zkbAssetQrCodeInfo.setCreatedDate(date);
            }
        }
        if("Y".equals(zkbAssetQrCodeInfo.getIbQrCodeState())){
            zkbAssetQrCodeInfo.setQrCodeState(null);
        }else{
            zkbAssetQrCodeInfo.setGeFlag(null);
        }
        if("Y".equals(zkbAssetQrCodeInfo.getQrCodeState())){
            zkbAssetQrCodeInfo.setIbQrCodeState(null);
            zkbAssetQrCodeInfo.setGeFlag(null);
        }
        zkbAssetQrCodeInfoRepository.save(zkbAssetQrCodeInfo);
    }


    public List<ZkbAssetQrCodeInfo> handleExcel(UploadedFile file, UserAccount user) throws IOException, ParseException {
        List<ZkbAssetQrCodeInfo> tempQrCodeInfoList = new ArrayList<>();
        Workbook workbook = this.createExcel(file);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        for(int j = 0 ; j < sheet.getLastRowNum(); j ++){
            Row row = sheet.getRow(j+1);
            String errorInfo = "";
            ZkbAssetQrCodeInfo qssetQrCodeInfo = new ZkbAssetQrCodeInfo();
            for(int i = 0 ; i< HEADER_FIELD.length ; i++){
                String mtName = "set"+HEADER_FIELD[i].substring(0, 1).toUpperCase() + HEADER_FIELD[i].substring(1);
                if(Optional.ofNullable(row.getCell(i)).isPresent() && StringUtils.isNotBlank(CellType.NUMERIC.equals(row.getCell(i).getCellTypeEnum()) ?
                        new DecimalFormat("##############.#####").format(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue())){
                    String opt = CellType.NUMERIC.equals(row.getCell(i).getCellTypeEnum()) ? new DecimalFormat("##############.#####").format(row.getCell(i).getNumericCellValue()) : row.getCell(i).getStringCellValue();
                    try {
                        Method method = ZkbAssetQrCodeInfo.class.getMethod(mtName,String.class);
                        method.invoke(qssetQrCodeInfo, opt);
                    } catch (Exception e) {
                        logger.debug("handleExcel error"+e.getMessage());
                    }
                }else if(Arrays.asList(HEADER_FIELD_REQUIRED).contains(HEADER_FIELD[i])){
                    errorInfo += "[请输入"+HEADER_FIELD_CN_NAME[i]+"]";
                }
            }
            qssetQrCodeInfo.setExcelIdex(j + 1);
            if (StringUtils.isNotBlank(errorInfo)) {
                qssetQrCodeInfo.setErrorInfo(errorInfo);
                tempQrCodeInfoList.add(qssetQrCodeInfo);
                continue;
            }
            List<ZkbAssetQrCodeInfo> qssetQrCodeInfos = zkbAssetQrCodeInfoRepository.findByTenantNameAndSystemId(qssetQrCodeInfo.getTenantName(),qssetQrCodeInfo.getSystemId());
            if(CollectionUtils.isEmpty(qssetQrCodeInfos)){
                qssetQrCodeInfo.setErrorInfo("[数据不存在，请检查后重试！]");
                tempQrCodeInfoList.add(qssetQrCodeInfo);
                continue;
            }else{
                if("Y".equals(qssetQrCodeInfo.getCanNotPastQrCode())){
                    if(StringUtils.isBlank(qssetQrCodeInfo.getReason())||StringUtils.isBlank(qssetQrCodeInfo.getReasonCategorize())){
                        qssetQrCodeInfo.setErrorInfo("[无法粘贴二维码为Y时，无法贴码原因和原因归类为必填！]");
                        tempQrCodeInfoList.add(qssetQrCodeInfo);
                        continue;
                    }
                }else if("N".equals(qssetQrCodeInfo.getCanNotPastQrCode())){
                    qssetQrCodeInfo.setReason(null);
                    qssetQrCodeInfo.setReasonCategorize(null);
                }
                qssetQrCodeInfos.get(0).setCanNotPastQrCode(qssetQrCodeInfo.getCanNotPastQrCode());
                qssetQrCodeInfos.get(0).setReason(qssetQrCodeInfo.getReason());
                qssetQrCodeInfos.get(0).setReasonCategorize(qssetQrCodeInfo.getReasonCategorize());
                zkbAssetQrCodeInfoRepository.save(this.convertDownBox(qssetQrCodeInfos.get(0)));
            }
        }
        return tempQrCodeInfoList;
    }

    public Workbook createExcel(UploadedFile file) throws IOException {
        Workbook workbook = null;
        if (ExcelDocument.ExcelType.XLS.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (ExcelDocument.ExcelType.XLSX.equals(ExcelDocument.parseTypeByFileName(file.getFileName()))) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        return workbook;
    }
    public ZkbAssetQrCodeInfo convertDownBox(ZkbAssetQrCodeInfo qrCodeInfo){
        if(SCRAPPED.equals(qrCodeInfo.getReasonCategorize())){
            qrCodeInfo.setReasonCategorize("scrapped");
        }else if (NOTALLOWED.equals(qrCodeInfo.getReasonCategorize())){
            qrCodeInfo.setReasonCategorize("notAllowed");
        }else if (NOTFIND.equals(qrCodeInfo.getReasonCategorize())){
            qrCodeInfo.setReasonCategorize("notFind");
        }else if (OTHER.equals(qrCodeInfo.getReasonCategorize())){
            qrCodeInfo.setReasonCategorize("other");
        }
        return qrCodeInfo;
    }

}
