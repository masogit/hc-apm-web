package com.ge.apm.service.asset;

import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MvsContractDateInfoRepository;
import com.ge.apm.dao.MvsOrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.ExcelParser;
import com.ge.apm.view.sysutil.UserContextService;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author 503266962
 */
@Component
public class MvsFileImportService {

    private Logger logger = LoggerFactory.getLogger(MvsFileImportService.class);

    @Autowired
    private MvsContractDateInfoRepository mvsContractRepository;
    @Autowired
    private MvsOrgInfoRepository mvsOrgInfoRepository;
    @Autowired
    private TenantInfoRepository tenantInfoRepository;
    @Autowired
    private AssetInfoRepository assetInfoRepository;
    @Autowired
    private MqUtils mqUtils;

    private final String ORDER_CHANGED = "Order Changed";
    private final String IN_ORDER_CHANGE = "IN Order Change";
    private final String NONE = "-";

    public List<MvsContractDateInfo> handleContractExcel(UploadedFile file) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<MvsContractDateInfo> importDataList = new ArrayList<>();
        Workbook workbook = null;
        if (file.getFileName().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        List<String> headers = Stream.of("ContractNo","SVCOffering","PackageNo","SystemID","ProductName","CustomerName"
                ,"DealerName","Status","StartDate","EndDate","OC Status","OC sumbit date","OrderChangeDate-批准时间"
                ,"OCSvcstratdate","OCSvcenddate").collect(Collectors.toList());
        Map<String, Integer> header = ExcelParser.findFirstRowHeader(sheet);
        if (header.isEmpty()) {
            return null;
        }
        for (String title : headers) {
            if(null == header.get(title)){
                WebUtil.addErrorMessage("Excel格式错误，请检查！");
                return null;
            }
        }
        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            boolean pass = true;
            MvsContractDateInfo dateInfo = new MvsContractDateInfo();
            try {
                if (StringUtils.isEmpty(ExcelParser.getCellStringValue(row, header.get("ContractNo")))) {
                    dateInfo.setCheckResult("合同编号不能为空！");
                    pass = false;
                }
                dateInfo.setPass(pass);
                dateInfo.setContractNo(ExcelParser.getCellStringValue(row, header.get("ContractNo")));
                dateInfo.setSVCOffering(ExcelParser.getCellStringValue(row, header.get("SVCOffering")));
                dateInfo.setPackageNo(ExcelParser.getCellStringValue(row, header.get("PackageNo")));
                dateInfo.setSystemId(ExcelParser.getCellStringValue(row, header.get("SystemID")));
                dateInfo.setProductName(ExcelParser.getCellStringValue(row, header.get("ProductName")));
                dateInfo.setCustomerName(ExcelParser.getCellStringValue(row, header.get("CustomerName")));
                dateInfo.setDealerName(ExcelParser.getCellStringValue(row, header.get("DealerName")));
                dateInfo.setStatus(ExcelParser.getCellStringValue(row, header.get("Status")));
                dateInfo.setStartDate(ExcelParser.getCellDateValue(row, header.get("StartDate"), sdf));
                dateInfo.setEndDate(ExcelParser.getCellDateValue(row, header.get("EndDate"), sdf));

                String ocStatus = ExcelParser.getCellStringValue(row, header.get("OC Status"));
                if(StringUtils.isEmpty(ocStatus)){
                    dateInfo.setOCStatus(NONE);
                }else{
                    dateInfo.setOCStatus(ocStatus);
                }
                dateInfo.setOCSubmitDate(ExcelParser.getCellDateValue(row, header.get("OC sumbit date"), sdf));
                dateInfo.setOrderChangeDate(ExcelParser.getCellDateValue(row, header.get("OrderChangeDate-批准时间"), sdf));
                dateInfo.setOCSvcStartDate(ExcelParser.getCellDateValue(row, header.get("OCSvcstratdate"), sdf));
                dateInfo.setOCSvcEndDate(ExcelParser.getCellDateValue(row, header.get("OCSvcenddate"), sdf));

                importDataList.add(dateInfo);
            } catch (Exception e) {
                logger.error("mvs合同信息导入失败", e);
                throw new RuntimeException("mvs合同信息导入失败");
            }
        }
        return importDataList;
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveContractInfo(List<MvsContractDateInfo> list){
        mvsContractRepository.deleteAll();
        mvsContractRepository.save(list);

        Map<String, List<MvsContractDateInfo>> contractMap = new HashMap<>();

        for (MvsContractDateInfo dateInfo : list) {
            List<MvsContractDateInfo> infoList;
            if(contractMap.containsKey(dateInfo.getContractNo())){
                infoList = contractMap.get(dateInfo.getContractNo());
            }else{
                infoList = new ArrayList<>();
            }
            infoList.add(dateInfo);
            contractMap.put(dateInfo.getContractNo(), infoList);
        }

        Optional<TenantInfo> tenantInfo = tenantInfoRepository.findFirstByName(ApmConstant.MVS_TENANT_NAME);
        String tenantUid = tenantInfo.get().getUid();

        // 数据回写，将导入的日期回写到asset的质保截止日期和维保截止日期字段中，具体逻辑见 YHAAO-17712
        for (String contractNo : contractMap.keySet()) {
            List<AssetInfo> assetList = assetInfoRepository.findByFinancingNumAndTenantUID(contractNo, tenantUid);
            if(CollectionUtils.isEmpty(assetList)){
                continue;
            }
            boolean orderChanged = false;
            boolean inOrderChange = false;
            boolean none = false;
            List<MvsContractDateInfo> infoList = contractMap.get(contractNo);
            for (MvsContractDateInfo dateInfo : infoList) {
                if(ORDER_CHANGED.equals(dateInfo.getOCStatus())){
                    orderChanged = true;
                    break;
                }else if(IN_ORDER_CHANGE.equals(dateInfo.getOCStatus())){
                    inOrderChange = true;
                }else if(NONE.equals(dateInfo.getOCStatus())){
                    none = true;
                }
            }
            List<MvsContractDateInfo> dateInfoList = null;

            boolean hasOrderChangeDate = true;
            if(orderChanged){
                // 查询批准日期最新的一条order changed记录
                dateInfoList = mvsContractRepository.findNewestByStatusAndContractNo(ORDER_CHANGED ,contractNo);
                // 如果为空，表示没有状态为order changed 且批准日期不为空的记录，则查询全部order changed记录，取任意一条记录的start date和end date
                if(CollectionUtils.isEmpty(dateInfoList)){
                    hasOrderChangeDate = false;
                    dateInfoList = mvsContractRepository.findByStatusAndContractNo(ORDER_CHANGED ,contractNo);
                }
            }else if(inOrderChange){
                dateInfoList = mvsContractRepository.findByStatusAndContractNo(IN_ORDER_CHANGE ,contractNo);
            }else if(none){
                dateInfoList = mvsContractRepository.findByStatusAndContractNo(NONE ,contractNo);
            }

            if(CollectionUtils.isEmpty(dateInfoList)){
                continue;
            }

            for (AssetInfo assetInfo : assetList) {
                if(orderChanged){
                    if(hasOrderChangeDate){
                        assetInfo.setFactoryWarrantyDate(dateInfoList.get(0).getOCSvcStartDate());
                        assetInfo.setWarrantyDate(dateInfoList.get(0).getOCSvcEndDate());
                    }else{
                        if(null != assetInfo.getFactoryWarrantyDate() && null != assetInfo.getWarrantyDate()){
                            continue;
                        }
                        assetInfo.setFactoryWarrantyDate(dateInfoList.get(0).getStartDate());
                        assetInfo.setWarrantyDate(dateInfoList.get(0).getEndDate());
                    }

                }else if (null == assetInfo.getFactoryWarrantyDate() && null == assetInfo.getWarrantyDate()){
                    assetInfo.setFactoryWarrantyDate(dateInfoList.get(0).getStartDate());
                    assetInfo.setWarrantyDate(dateInfoList.get(0).getEndDate());
                }
                assetInfoRepository.save(assetInfo);
                saveAssetHistory(assetInfo);
            }
        }
    }

    public List<MvsOrgInfo> handleSiteExcel(UploadedFile file) throws IOException, ParseException {
        List<MvsOrgInfo> importDataList = new ArrayList<>();
        Map<String, String> importKeyMap = new HashMap<>();
        Workbook workbook = null;
        if (file.getFileName().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        List<String> headers = Stream.of("Site ID","Installed At Alternate Customer Name","UCM ID","Area","Province","Service Region"
                ,"Customer Chinese Address","P&L","City","HP Level","Sales SSO","Account Sales","Sales Mgr","Region Mgr","Region2"
                ,"Enterprise","Enterprise VIP"," Enterprise VIP Leader SSO"," Enterprise VIP Leader Name"
                ,"Commerial SVC Region","Account Segment-1st Level","Account Segment-2nd Level").collect(Collectors.toList());
        Map<String, Integer> header = ExcelParser.findFirstRowHeader(sheet);
        if (header.isEmpty()) {
            return null;
        }
        for (String title : headers) {
            if(null == header.get(title)){
                WebUtil.addErrorMessage("Excel格式错误，请检查！");
                return null;
            }
        }
        for (int j = 1; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            boolean pass = true;
            MvsOrgInfo orgInfo = new MvsOrgInfo();
            try {
                if (StringUtils.isEmpty(ExcelParser.getCellStringValue(row, header.get("Site ID")))) {
                    orgInfo.setCheckResult("Site ID不能为空！");
                    pass = false;
                }else if(importKeyMap.containsKey(ExcelParser.getCellStringValue(row, header.get("Site ID")))){
                    orgInfo.setCheckResult("Site ID重复，数据被丢弃！");
                    pass = false;
                }
                orgInfo.setPass(pass);
                orgInfo.setSiteId(ExcelParser.getCellStringValue(row, header.get("Site ID")));
                orgInfo.setLocationCode(ExcelParser.getCellStringValue(row, header.get("Installed At Alternate Customer Name")));
                orgInfo.setUCMId(ExcelParser.getCellStringValue(row, header.get("UCM ID")));
                orgInfo.setArea(ExcelParser.getCellStringValue(row, header.get("Area")));
                orgInfo.setProvince(ExcelParser.getCellStringValue(row, header.get("Province")));
                orgInfo.setServiceRegion(ExcelParser.getCellStringValue(row, header.get("Service Region")));
                orgInfo.setCustomerChineseAddress(ExcelParser.getCellStringValue(row, header.get("Customer Chinese Address")));
                orgInfo.setPL(ExcelParser.getCellStringValue(row, header.get("P&L")));
                orgInfo.setCity(ExcelParser.getCellStringValue(row, header.get("City")));
                orgInfo.setHPLevel(ExcelParser.getCellStringValue(row, header.get("HP Level")));
                orgInfo.setSalesSSO(ExcelParser.getCellStringValue(row, header.get("Sales SSO")));
                orgInfo.setAccountSales(ExcelParser.getCellStringValue(row, header.get("Account Sales")));
                orgInfo.setSalesMgr(ExcelParser.getCellStringValue(row, header.get("Sales Mgr")));
                orgInfo.setRegionMgr(ExcelParser.getCellStringValue(row, header.get("Region Mgr")));
                orgInfo.setRegion2(ExcelParser.getCellStringValue(row, header.get("Region2")));
                orgInfo.setEnterprise(ExcelParser.getCellStringValue(row, header.get("Enterprise")));
                orgInfo.setEnterpriseVIP(ExcelParser.getCellStringValue(row, header.get("Enterprise VIP")));
                orgInfo.setEnterpriseVIPLeaderSSO(ExcelParser.getCellStringValue(row, header.get(" Enterprise VIP Leader SSO")));
                orgInfo.setEnterpriseVIPLeaderName(ExcelParser.getCellStringValue(row, header.get(" Enterprise VIP Leader Name")));
                orgInfo.setCommerialSVCRegion(ExcelParser.getCellStringValue(row, header.get("Commerial SVC Region")));
                orgInfo.setAccountSegmentFirstLevel(ExcelParser.getCellStringValue(row, header.get("Account Segment-1st Level")));
                orgInfo.setAccountSegmentSecondLevel(ExcelParser.getCellStringValue(row, header.get("Account Segment-2nd Level")));

                importKeyMap.put(orgInfo.getSiteId(), orgInfo.getSiteId());
                importDataList.add(orgInfo);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("mvs合同信息导入失败", e);
                throw new RuntimeException("mvs合同信息导入失败");
            }
        }
        return importDataList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrgInfo(List<MvsOrgInfo> list){
        mvsOrgInfoRepository.deleteAll();
        mvsOrgInfoRepository.save(list);
    }

    private void saveAssetHistory(AssetInfo asset){
        MqContext mqContext = new MqContext();
        mqContext.setClassName("MvsFileImportService");
        mqContext.setMethodName("saveContractInfo");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setDescribe("MVS合同导入修改设备");
        mqContext.setModifySource(ModifySourceEnum.MVS_IMPORT.getCode());
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        mqUtils.sendAssetToMQ(asset,currentUserAccount,mqContext);

    }
}
