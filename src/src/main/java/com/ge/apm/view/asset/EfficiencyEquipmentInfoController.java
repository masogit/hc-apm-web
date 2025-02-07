package com.ge.apm.view.asset;

import com.ge.apm.dao.*;

import com.ge.apm.dao.mapper.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetCostDTO;
import com.ge.apm.domain.dto.DepreciationResultData;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class EfficiencyEquipmentInfoController extends GenericCRUDUUIDController<EfficiencyEquipmentInfo> implements Serializable, ServerEventInterface {



    private EfficiencyEquipmentInfoRepository efficiencyEquipmentInfoRepository;

    private UserAccount user;
    private UaaService uaaService;

    private UserContextService userContextService;

    private AssetInfoRepository assetInfoRepository;



    private EfficiencyEquipmentMapper efficiencyEquipmentMapper;

    //选定的院区
    private OrgInfo selectSite;

    private List<EfficiencyEquipmentInfo> currentPageAssets;
    @Getter
    @Setter
    private Integer filterAssetGroup;
    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap;

    @Getter @Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>();


    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private OrgInfo filterDeptOrg;


    //采购原值(元)
    @Getter @Setter
    private Double purchasePrice;

    @Getter @Setter
    private Integer assetGroup;//设备分类

    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;

    private AssetDepreciationMapper assetDepreciationMapper;

    private I18nMessageRepository i18nMessageRepository;


    private Date startDate=DateUtil.currentMonthFirstDay();

    private Date endDate=new Date();


    private HisEquipmentDataResultMapper hisEquipmentDataResultMapper;


    private AssetCostAmountInfoMapper assetCostAmountInfoMapper;

    private IotPerformanceInfoMapper iotPerformanceInfoMapper;

    private StreamedContent excelFile;

    private I18nMessageService i18nMessageService;


    @Setter
    private String url;

    @Getter
    @Setter
    private BigDecimal positiveRate;

    private FileDownloadService fileDownloadService;

    @Override
    protected GenericRepositoryUUID<EfficiencyEquipmentInfo> getDAO() {
        return efficiencyEquipmentInfoRepository;
    }

    @Override
    protected void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);

        efficiencyEquipmentInfoRepository = WebUtil.getBean(EfficiencyEquipmentInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);

        hisEquipmentDataResultMapper = WebUtil.getBean(HisEquipmentDataResultMapper.class);
        assetCostAmountInfoMapper = WebUtil.getBean(AssetCostAmountInfoMapper.class);
        iotPerformanceInfoMapper = WebUtil.getBean(IotPerformanceInfoMapper.class);
        efficiencyEquipmentMapper = WebUtil.getBean(EfficiencyEquipmentMapper.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        assetDepreciationMapper = WebUtil.getBean(AssetDepreciationMapper.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);

        this.selectedMap = new HashMap<>();
    }



    @Override
    protected Page<EfficiencyEquipmentInfo> loadData(PageRequest pageRequest) {

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));

        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }
        Page<EfficiencyEquipmentInfo> page = efficiencyEquipmentInfoRepository.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets=page.getContent();
        if(!CollectionUtils.isEmpty(currentPageAssets)){

            calculationData(currentPageAssets,startDate,endDate);
        }
        return page;
    }

    private void calculationData(List<EfficiencyEquipmentInfo> currentPageAssets,Date startDate,Date endDate) {
        List<String> assetUids = currentPageAssets.stream().map(EfficiencyEquipmentInfo::getAssetUid).collect(Collectors.toList());
        List<AssetCostDTO> hisResult= hisEquipmentDataResultMapper.findByAssetUidAndDateInSunAmount(assetUids,startDate,endDate);
        Map<String, AssetCostDTO> hisResultMap = hisResult.stream().collect(Collectors.toMap(data -> data.getAssetUid(), data -> data));
        List<AssetCostDTO> assetCost=  assetCostAmountInfoMapper.findByAssetUidAndDateInSunAmount(assetUids,startDate,endDate);
        Map<String, AssetCostDTO> assetCostMap = assetCost.stream().collect(Collectors.toMap(data -> data.getAssetUid(), data -> data));
        List<String> qrCodes = currentPageAssets.stream().map(EfficiencyEquipmentInfo::getAssetInfo).map(AssetInfo::getQrCode).filter(Objects::nonNull).collect(Collectors.toList());

        List<Integer> assetIds = currentPageAssets.stream().map(EfficiencyEquipmentInfo::getAssetInfo).map(AssetInfo::getId).filter(Objects::nonNull).collect(Collectors.toList());

//        List<AssetCostDTO>  iotList= iotPerformanceInfoMapper.findByQrCodeAndDateInSunAmount(qrCodes,startDate,endDate);
//        Map<String, AssetCostDTO> iotMap = iotList.stream().collect(Collectors.toMap(AssetCostDTO::getQrCode, data -> data));

        Map<Integer, BigDecimal> assetDepreciationAmount = getAssetDepreciationAmount(startDate, endDate, assetIds);
        for (EfficiencyEquipmentInfo currentPageAsset : currentPageAssets) {
            AssetCostDTO hisResultDto= hisResultMap.get(currentPageAsset.getAssetUid());
//            AssetCostDTO iot = currentPageAsset.getAssetInfo().getQrCode()!=null?iotMap.get(currentPageAsset.getAssetInfo().getQrCode()):null;
            AssetCostDTO assetCostDTO = assetCostMap.get(currentPageAsset.getAssetUid());
            BigDecimal income=hisResultDto!=null?hisResultDto.getAmount()!=null?hisResultDto.getAmount():BigDecimal.ZERO:BigDecimal.ZERO;
            BigDecimal cost=assetCostDTO!=null?assetCostDTO.getAmount()!=null?assetCostDTO.getAmount():BigDecimal.ZERO:BigDecimal.ZERO;
//            BigDecimal checkNumberPeople=iot!=null?iot.getCheckNumberPeople()!=null?iot.getCheckNumberPeople():BigDecimal.ZERO:BigDecimal.ZERO;
            Integer id = currentPageAsset.getAssetInfo().getId();
            BigDecimal bigDecimal =assetDepreciationAmount!=null? assetDepreciationAmount.get(id)!=null? assetDepreciationAmount.get(id):BigDecimal.ZERO:BigDecimal.ZERO;

            currentPageAsset.setIncome(income);
            currentPageAsset.setCost(cost.add(bigDecimal));
//            currentPageAsset.setCheckNumberPeople(checkNumberPeople);
            BigDecimal profits = income.subtract(cost).subtract(bigDecimal);
            currentPageAsset.setProfits(profits);
            if(!(income.compareTo(BigDecimal.ZERO)==0)){
                currentPageAsset.setProfitsRate(profits.multiply(BigDecimal.valueOf(100)).divide(income,2, RoundingMode.HALF_UP));
            }else{
                currentPageAsset.setProfitsRate(BigDecimal.ZERO);
            }
        }

    }




    public void onSelectSiteTreeNode(NodeSelectEvent event) {

        this.searchFilters.clear();

        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }

        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }

    }

    private Map<Integer,BigDecimal> getAssetDepreciationAmount(Date startDate, Date endDate, List<Integer> assetIds) {

        LocalDate localDateStart = startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate localDateEnd = endDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        List<DepreciationResultData> list = assetDepreciationList(localDateStart, localDateEnd, assetIds);
        if(org.springframework.util.CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream()
                .collect(Collectors.groupingBy(
                        DepreciationResultData::getAssetId,
                        Collectors.mapping(DepreciationResultData::getDepreciationEquipment,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )));

    }
    private  List<DepreciationResultData>  assetDepreciationList(LocalDate startDate, LocalDate endDate, List<Integer> assetIds) {
        //折旧
        LocalDate localDate = startDate.withDayOfMonth(1);//开始日期月份第一天
        LocalDate lastDateEnd = startDate.with(TemporalAdjusters.lastDayOfMonth());  //开始日期月底

        int daysMonth = startDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth(); //月天数
        long daysDifferenceStart = ChronoUnit.DAYS.between(startDate, lastDateEnd)+1;  //相差天数


        LocalDate localEndDate = endDate.withDayOfMonth(1);
        long daysDifferenceEnd = ChronoUnit.DAYS.between(localEndDate, endDate)+1;
        int daysMonthEnd = localEndDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();


        List<DepreciationResultData> assetDepreciationList = assetDepreciationMapper.findAssetDepreciationGroupMonthByAssetIds(assetIds, localDate, endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String startString = startDate.format(formatter);
        String endString = endDate.format(formatter);

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        for (DepreciationResultData performanceResultData : assetDepreciationList) {
            if(!startString.equals(endString)){
                if(startString.equals(performanceResultData.getDate())){
                    BigDecimal depreciationEquipment = performanceResultData.getDepreciationEquipment()!=null?performanceResultData.getDepreciationEquipment():BigDecimal.ZERO;
                    BigDecimal divide = depreciationEquipment.multiply(BigDecimal.valueOf(daysDifferenceStart)).divide(BigDecimal.valueOf(daysMonth), 2, RoundingMode.HALF_UP);
                    performanceResultData.setDepreciationEquipment(divide);
                }
                if(endString.equals(performanceResultData.getDate())){
                    BigDecimal depreciationEquipment = performanceResultData.getDepreciationEquipment()!=null?performanceResultData.getDepreciationEquipment():BigDecimal.ZERO;
                    BigDecimal divide = depreciationEquipment.multiply(BigDecimal.valueOf(daysDifferenceEnd)).divide(BigDecimal.valueOf(daysMonthEnd), 2, RoundingMode.HALF_UP);
                    performanceResultData.setDepreciationEquipment(divide);
                }
            }else{
                BigDecimal depreciationEquipment = performanceResultData.getDepreciationEquipment()!=null?performanceResultData.getDepreciationEquipment():BigDecimal.ZERO;
                BigDecimal divide = depreciationEquipment.multiply(BigDecimal.valueOf(days)).divide(BigDecimal.valueOf(daysMonth), 2, RoundingMode.HALF_UP);
                performanceResultData.setDepreciationEquipment(divide);
            }

        }


        return assetDepreciationList;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getAssetUid()) || !selectedMap.get(item.getAssetUid()));
        for (EfficiencyEquipmentInfo item : currentPageAssets) {
            selectedMap.put(item.getAssetUid(), !isSelectedAll);
        }
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }


    public void deleteAsset(EfficiencyEquipmentInfo efficiencyEquipmentInfo) {
        efficiencyEquipmentInfoRepository.delete(efficiencyEquipmentInfo);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        selectedOrg=null;
        filterDeptOrg=null;
        assetGroup=null;
        purchasePrice=null;
        assets= new DualListModel<>();
        super.prepareCreate();
    }



    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

       String url= "assetUid=" + selected.getAssetUid();
         url = "EfficiencyEquipmentAssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }




    private List<String> fields = ListUtil.newArrayList("设备名称", "设备型号", "品牌", "制造商序列号", "二维码", "所属科室", "设备分类", "采购金额", "收入", "成本", "利润", "利润率");

    public void exportExcel()  {
        try {
            Set<String> assetUids = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
            List<String> orgUidList=null;

            List<String> qrCodes=null;

            String orgUid=null;
            if(selectSite!=null){
                orgUid = selectSite.getUid();
            }else{
                orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
            }
            if(!assetUids.isEmpty()){

                List<AssetInfo> byUids = assetInfoRepository.getByUids(assetUids);
                qrCodes=  byUids.stream().map(AssetInfo::getQrCode).filter(Objects::nonNull).collect(Collectors.toList());
            }


            List<EfficiencyEquipmentInfo> list = efficiencyEquipmentMapper.getEfficiencyEquipmentInfo(startDate, endDate, user.getTenantUID(),assetUids,orgUid,orgUidList,qrCodes);
            List<I18nMessage> assetGroup = i18nMessageService.getByMsgType("assetGroup");
            Map<String, String> assetGroupMap  = assetGroup.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, I18nMessage::getValue, (value1, value2) -> value1));
            List<Integer> assetIds = list.stream().map(EfficiencyEquipmentInfo::getAssetId).collect(Collectors.toList());
            Map<Integer, BigDecimal> assetDepreciationAmount = getAssetDepreciationAmount(startDate, endDate, assetIds);


            for (EfficiencyEquipmentInfo efficiencyEquipmentInfo : list) {
                if (efficiencyEquipmentInfo.getAssetGroup() != null) {
                    efficiencyEquipmentInfo.setAssetGroupName(assetGroupMap.get(efficiencyEquipmentInfo.getAssetGroup().toString()));
                }
                BigDecimal income = efficiencyEquipmentInfo.getIncome() != null ? efficiencyEquipmentInfo.getIncome() : BigDecimal.ZERO;
                BigDecimal cost = efficiencyEquipmentInfo.getCost() != null ? efficiencyEquipmentInfo.getCost() : BigDecimal.ZERO;
                Integer id = efficiencyEquipmentInfo.getAssetId();
                BigDecimal bigDecimal =assetDepreciationAmount!=null? assetDepreciationAmount.get(id)!=null? assetDepreciationAmount.get(id):BigDecimal.ZERO:BigDecimal.ZERO;
                efficiencyEquipmentInfo.setIncome(income);
                efficiencyEquipmentInfo.setCost(cost.add(bigDecimal));
                BigDecimal profits = income.subtract(cost).subtract(bigDecimal);
                efficiencyEquipmentInfo.setProfits(profits);
                if(!(income.compareTo(BigDecimal.ZERO)==0)){
                    efficiencyEquipmentInfo.setProfitsRate(profits.multiply(BigDecimal.valueOf(100)).divide(income,2, RoundingMode.HALF_UP));
                }else{
                    efficiencyEquipmentInfo.setProfitsRate(BigDecimal.ZERO);
                }
            }
            downloadFile(parseListToMap(list), fields);
        }catch (Exception e){
            logger.error("导出效益设备异常 e:",e);
        }

    }

    private void downloadFile(List<Map<String, Object>> data, List<String> fields) {
        try {
//            InputStream stream = EfficiencyEquipmentInfoController.class.getClassLoader().getResourceAsStream("/template/APM_EfficiencyEquipment_export_Template.xlsx");
            InputStream stream =fileDownloadService.getTemplateFileInputStream(SystemTemplate.SystemTemplateEnum.BENEFIT_ANALYSIS.getCode(), SystemTemplate.SystemTemplateEnum.BENEFIT_ANALYSIS.getFileType());
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = exportData(doc, data, fields);
            if (Boolean.TRUE.equals(exportFlag)) {
                String start = com.ge.healthcare.cn.common.utils.date.DateUtil.format(startDate, "yyyy-MM-dd");
                String end = com.ge.healthcare.cn.common.utils.date.DateUtil.format(endDate, "yyyy-MM-dd");
                String fileName=start+"~"+end+"效益设备数据.xlsx";
                excelFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", fileName);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }



    private Boolean exportData(ExcelDocument doc, List<Map<String, Object>> dataList, List<String> fields) {
        int dataRow = 2;
        for (Map<String, Object> data : dataList) {
            for (int index = 0; index < fields.size(); index++) {
                String columnTitle = fields.get(index);
                if (columnTitle == null || columnTitle.isEmpty()) {
                    continue;
                }
                Object value = data.get(columnTitle);
                if (value == null) continue;

                doc.writeCellData("datalist", dataRow, index + 1, value);
            }
            dataRow++;
        }
        return Boolean.TRUE;
    }


    public List<Map<String, Object>> parseListToMap(List<EfficiencyEquipmentInfo> list) {
        List<Map<String, Object>> listMapResult = new ArrayList<>();
        for (EfficiencyEquipmentInfo equipmentInfo : list) {
            Map<String, Object> tempMap = MapUtil.newHashMap();
            tempMap.put("设备名称", equipmentInfo.getAssetName());
            tempMap.put("设备型号", equipmentInfo.getFunctionType());
            tempMap.put("品牌", equipmentInfo.getMakerShortName());
            tempMap.put("制造商序列号", equipmentInfo.getSerialNum());
            tempMap.put("二维码", equipmentInfo.getQrCode());
            tempMap.put("所属科室", equipmentInfo.getClinicalDeptName());
            tempMap.put("设备分类", equipmentInfo.getAssetGroupName());
            tempMap.put("采购金额", equipmentInfo.getPurchasePrice());
//            tempMap.put("检查人次", equipmentInfo.getCheckNumberPeople());
            tempMap.put("收入", equipmentInfo.getIncome());
            tempMap.put("成本", equipmentInfo.getCost());
            tempMap.put("利润", equipmentInfo.getProfits());
            tempMap.put("利润率",equipmentInfo.getProfitsRate());
            listMapResult.add(tempMap);
        }
        return listMapResult;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }

    public String getUrl() {
        if(selected!=null){
            String url= "/reports/apm-nurse-home?assetUid="+selected.getAssetUid();
            return url;
        }

        return null;
    }
    public void  editBatch1(){
        this.positiveRate=null;
    }
    public void  editBatch(){
        if(positiveRate.compareTo(BigDecimal.valueOf(100))>0){
            this.positiveRate=null;
            WebUtil.addErrorMessage("阳性率不能大于100");
            return;
        }
        Set<String> assetUids = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
        int count=efficiencyEquipmentInfoRepository.updateBatchPositiveRate(assetUids,positiveRate);
        if(count>0){
            WebUtil.addErrorMessage("修改阳性率成功");
        }else{
            WebUtil.addErrorMessage("修改阳性率失败");
        }
        this.selectedMap = new HashMap<>();
        this.positiveRate=null;
    }

}
