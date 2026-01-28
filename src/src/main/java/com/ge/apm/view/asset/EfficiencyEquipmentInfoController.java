package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetCostDTO;
import com.ge.apm.domain.dto.DepreciationResultData;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.service.EasyExcelExportUtil;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.BiFunction;
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


    @Getter @Setter
    private String filterClinicalDeptOrg;
    @Getter @Setter
    private Set<String> clinicalDeptOrgUidSet;

    private OrgInfoTreeService orgInfoTreeService;
    private AssetDepreciationMapper assetDepreciationMapper;

    private I18nMessageRepository i18nMessageRepository;


    private Date startDate=DateUtil.currentMonthFirstDay();

    private Date endDate=new Date();


    private HisEquipmentDataResultMapper hisEquipmentDataResultMapper;
    private ConfigItemValueRepository configItemValueRepository;
    private AssetCostAmountInfoMapper assetCostAmountInfoMapper;
    private IotPerformanceInfoMapper iotPerformanceInfoMapper;

    private StreamedContent excelFile;

    private I18nMessageService i18nMessageService;


    @Setter
    private String url;

    @Getter
    @Setter
    private BigDecimal positiveRate;

    @Getter
    @Setter
    private String displayName;

    private SystemTemplateService systemTemplateService;
    @Getter
    @Setter
    private Map<String,Object> filterObject;

    private Sort lastPageSort;

    @Getter@Setter
    private List<FieldColumnDto> fieldColumnDtoList = new ArrayList<>();

    @Override
    protected GenericRepositoryUUID<EfficiencyEquipmentInfo> getDAO() {
        return efficiencyEquipmentInfoRepository;
    }

    @Override
    protected void init() {
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);

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
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);

        this.selectedMap = new HashMap<>();
        filterObject=new HashMap<>();
        loadClinicalDeptList();
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID(), "EfficiencyEquipmentColumn");
        List<FieldColumnDto> list =  JSON.parseArray(value, FieldColumnDto.class);
        if(!org.springframework.util.CollectionUtils.isEmpty(list)){
            fieldColumnDtoList = list.stream().filter(item -> item.getRendered()).collect(Collectors.toList());
        }
    }

    public Boolean showFilterRender(String field){
        if(CollectionUtils.isEmpty(fieldColumnDtoList)){
            return Boolean.FALSE;
        }
        return fieldColumnDtoList.stream().filter(item -> item.getField().equals(field)).findFirst().isPresent();
    }

    @Override
    protected Page<EfficiencyEquipmentInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("page", pageRequest.getPageSize());

        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(orgUidList)){
            param.put("orgUidList",orgUidList);
        }
        if (null != selectSite) {
            param.put("selectedOrgUid",selectSite.getUid());
        }

        if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
            param.put("clinicalDeptUids", clinicalDeptOrgUidSet);
        }
        param.put("startDate",startDate);
        param.put("endDate",endDate);
        param.put("tenantUid",user.getTenantUID());
        covQueryCondition(searchFilters,param);
        lastPageSort = pageRequest.getSort();
        StringBuilder orderBy = new StringBuilder();
        for (Sort.Order order : lastPageSort) {
            String property = order.getProperty();
            boolean ascending = order.isAscending();
            switch (property){
                case "positiveRate":
                case "purchasePrice":
                    orderBy.append("r."+(property)).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                default:
                    orderBy.append("r."+(property)).append(ascending ? " ASC" : " DESC").append(",");
                    break;
            }
        }
        param.put("orderBy", orderBy.substring(0, orderBy.length() - 1));
        List<EfficiencyEquipmentInfo> efficiencyEquipmentInfos = efficiencyEquipmentMapper.selectList(param);
        Long count = efficiencyEquipmentMapper.selectListCount(param);
        currentPageAssets=efficiencyEquipmentInfos;
        return  new PageImpl<>(efficiencyEquipmentInfos, pageRequest, count);
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

//    @Override
//    public void onFilter(FilterEvent event) {
//        super.onFilter(event);
//        this.selectedMap.clear();
//    }
    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        onFilterCustoms(event, this::numberField, this::dateTimeField);
        this.selectedMap.clear();
    }

    public void onFilterCustoms(FilterEvent event, BiFunction<String, String, Boolean>... functions) {
        if (searchFilters == null) searchFilters = new ArrayList<>();
        searchFilters.clear();
        if (getFiltersFromDynaColumns()) return;
        getFilterMatchModes((DataTable)event.getComponent());
        for (Map.Entry<String, Object> filter: event.getFilters().entrySet()) {
            String fieldName = filter.getKey();
            Object filterValue = filter.getValue();
            if (filterValue == null) continue;
            String filterVal = filterValue.toString();
            if (StringUtils.isEmpty(filterVal)) continue;
            for(BiFunction<String, String, Boolean> function : functions){
                if (function.apply(fieldName, filterVal)) {
                    continue;
                }
            }
            String matchMode = filterMatchModes.get(fieldName);
            addFilter(fieldName, matchMode, filterValue);
        }
        setSiteFilter();
        setHospitalFilter();
    }

protected boolean numberField(String fieldName, String filterVal) {
    if (isNumberField(fieldName)) {
        processNumberFiled(filterVal, fieldName);
        return true;
    }
    return false;
}
    private Map<String, Boolean> numberFields = null;

    protected boolean isNumberField(String fieldName){
        if(numberFields==null){
            numberFields = new HashMap<String, Boolean>();
            Class a = getBeanClass();
            for(Field f: a.getDeclaredFields()){
                if(f.getType()==Double.class ||  f.getType()== BigDecimal.class  ||  f.getType()==Integer.class       )
                    numberFields.put(f.getName(), Boolean.TRUE);
            }
        }
        return numberFields.get(fieldName)!=null;
    }

    protected void processNumberFiled(String filterVal, String fieldName) {
        String[] values = filterVal.split("~");
        if (values.length > 0) {
            Optional.ofNullable((values[0])).ifPresent(var -> searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.GTE, var)));
        }
        if (values.length > 1) {
            Optional.ofNullable((values[1])).ifPresent(var -> searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.LTE, var)));
        }
    }


    public void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        for (SearchFilter searchFilter :searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "assetName":
                    param.put("assetName", searchFilter.value);
                    break;
                case "functionType":
                    param.put("functionType", searchFilter.value);
                    break;
                case "makerShortName":
                    param.put("makerShortName", searchFilter.value);
                    break;
                case "serialNum":
                    param.put("serialNum", searchFilter.value);
                    break;
                case "financingNum":
                    param.put("financingNum", searchFilter.value);
                    break;
                case "displayName":
                    param.put("displayName", searchFilter.value);
                    break;
                case "assetGroup":
                    try {
                        param.put("assetGroup", Integer.parseInt(searchFilter.value.toString()));
                    }catch (Exception e){
                        logger.error("his设备绩效 assetGroup 搜索数值转换异常 e:",e);
                    }
                    break;
                case "qrCode":
                    param.put("qrCode", searchFilter.value);
                    break;

                case "clinicalDeptName":
                    param.put("clinicalDeptName", searchFilter.value);
                    break;
                case "cost":
                case "income":
                case "profits":
                case "profitsRate":
                case "positiveRate":
                case "purchasePrice":
                    try {
                        double num = Double.parseDouble(searchFilter.value.toString());
                        String pararName;
                        if (searchFilter.operator.name().equals("GTE")) {
                            pararName= fieldName+"GTE";
                            param.put(pararName, num);
                        } else {
                            pararName=fieldName+"LTE";
                            param.put(pararName, num);
                        }
                    }catch (Exception e){
                        logger.error("his设备绩效数值区间搜索数值转换异常 e:",e);
                    }

                    break;
                case "purchaseDate":
                    try {
                        Date dv =  (Date)searchFilter.value;
                        String pararName;
                        if (searchFilter.operator.name().equals("GTE")) {
                            pararName= fieldName+"GTE";
                            param.put(pararName, dv);
                        } else {
                            pararName=fieldName+"LTE";
                            param.put(pararName, dv);
                        }
                    }catch (Exception e){
                        logger.error("his设备绩效时间区间搜索数值转换异常 e:",e);
                    }

                    break;
                default:
                    break;
            }
        }
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
            if (searchFilters == null) {
                searchFilters = new ArrayList<>();
            }
            Map<String, Object> param = new HashMap<>();
            param.put("page", 5000);

            param.put("offset", 0);
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(orgUidList)){
                param.put("orgUidList",orgUidList);
            }
            if (null != selectSite) {
                param.put("selectedOrgUid",selectSite.getUid());
            }
            param.put("startDate",startDate);
            param.put("endDate",endDate);
            param.put("tenantUid",user.getTenantUID());
            if(CollectionUtils.isNotEmpty(assetUids)){
                param.put("assetUids",assetUids);
            }
            covQueryCondition(searchFilters,param);


            StringBuilder orderBy = new StringBuilder();
            for (Sort.Order order : lastPageSort) {
                String property = order.getProperty();
                boolean ascending = order.isAscending();
                switch (property){
                    case "positiveRate":
                    case "purchasePrice":
                        orderBy.append("r."+(property)).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                        break;
                    default:
                        orderBy.append("r."+(property)).append(ascending ? " ASC" : " DESC").append(",");
                        break;
                }
            }
            param.put("orderBy", orderBy.substring(0, orderBy.length() - 1));

            List<EfficiencyEquipmentInfo> efficiencyEquipmentInfos = efficiencyEquipmentMapper.selectList(param);

            List<I18nMessage> assetGroup = i18nMessageService.getByMsgType("assetGroup");
            Map<String, String> assetGroupMap  = assetGroup.stream().collect(Collectors.toMap(I18nMessage::getMsgKey, I18nMessage::getValue, (value1, value2) -> value1));


            efficiencyEquipmentInfos.forEach(item->item.setAssetGroupName(assetGroupMap.get(item.getAssetGroup().toString())));


            easyExcelExport(efficiencyEquipmentInfos);
        }catch (Exception e){
            logger.error("导出效益设备异常 e:",e);
        }

    }

    public void easyExcelExport( List<EfficiencyEquipmentInfo> list ) throws IOException {
        byte[] stream =systemTemplateService.getTemplateFileByte(SystemTemplateEnum.BENEFIT_ANALYSIS,user);
        byte[] bytes = EasyExcelExportUtil.exportExcel(stream, list, new HashMap<>());
        String start = com.ge.healthcare.cn.common.utils.date.DateUtil.format(startDate, "yyyy-MM-dd");
        String end = com.ge.healthcare.cn.common.utils.date.DateUtil.format(endDate, "yyyy-MM-dd");
        String fileName=start+"~"+end+"效益设备数据.xlsx";
        excelFile = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/vnd.ms-excel", fileName);
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
        this.displayName=null;
    }
    public void  editBatch(){
        if(positiveRate!=null){
            if(positiveRate.compareTo(BigDecimal.valueOf(100))>0){
                this.positiveRate=null;
                WebUtil.addErrorMessage("阳性率不能大于100");
                return;
            }
        }
        Set<String> assetUids = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
        int count;
        if(positiveRate!=null){
             count=efficiencyEquipmentInfoRepository.updateBatch(assetUids,positiveRate,displayName);
        }else{
             count=efficiencyEquipmentInfoRepository.updateBatchDisplayName(assetUids,displayName);
        }

        if(count>0){
            WebUtil.addErrorMessage("批量修改成功");
        }else{
            WebUtil.addErrorMessage("批量修改失败");
        }
        this.selectedMap = new HashMap<>();
        this.positiveRate=null;
        this.displayName=null;
    }


    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }

        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }

    }
}
