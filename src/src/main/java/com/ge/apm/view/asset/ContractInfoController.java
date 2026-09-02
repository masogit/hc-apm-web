package com.ge.apm.view.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ContractInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.ContractInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import javaslang.Tuple2;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ContractInfoController extends GenericCRUDUUIDController<ContractInfo> {

    private UaaService uaaService;
    private ContractInfoService contractService;
    private ContractInfoRepository dao = null;
    private ArrivalListRepository arrivalListRepository;
    private ArrivalListItemRepository arrivalListItemRepository;
    private ContractInfoItemRepository contractInfoItemRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private ContractInfoMapper contractInfoMapper;
    private String assetUid;
    private UserAccount currentUser;
    @Getter @Setter
    Integer contractExpirationReminderDays = 0;

    @Getter
    @Setter
    public Map<String, Object> filterObject;

    private Sort lastPageSort;
    @Override
    protected void init() {
        dao = WebUtil.getBean(ContractInfoRepository.class);
        contractService = WebUtil.getBean(ContractInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        arrivalListRepository = WebUtil.getBean(ArrivalListRepository.class);
        arrivalListItemRepository = WebUtil.getBean(ArrivalListItemRepository.class);
        contractInfoItemRepository = WebUtil.getBean(ContractInfoItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        contractInfoMapper = WebUtil.getBean(ContractInfoMapper.class);
        currentUser = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        assetUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetUid");
        ConfigItemValue configItemValue = configItemValueRepository.findFirstByKeyAndSiteUid("contractExpirationReminderDays", currentUser.getSiteUID());
        if (ObjectUtil.isNotEmpty(configItemValue)) {
            contractExpirationReminderDays = Integer.parseInt(configItemValue.getValue());
        }
        filterObject=new HashMap<>();
    }

    @Override
    protected ContractInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ContractInfo> loadData(PageRequest pageRequest) {

        if (!StringUtils.isEmpty(assetUid)) {
            List<ContractInfo> contractInfos = contractService.getContractListByAssetUid(assetUid);
            PageImpl page = new PageImpl(contractInfos);
            List<ContractInfo> list = page.getContent();
            calcPercentage(list);
            return page;
        }

        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("page", pageRequest.getPageSize());

        paramMap.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
//        if(!this.hasFilterOnField("contractType")){
//            List<String> contractTypeList = Arrays.asList(ContractInfo.ContractType.values()).stream().map(item -> item.name()).collect(Collectors.toList());
//            searchFilters.add(new SearchFilter("contractType", SearchFilter.Operator.IN, contractTypeList, "1*"));
//        }

        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
//            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            if (ObjectUtil.isNotEmpty(orgUidList)) {
                paramMap.put("orgUidList", orgUidList);
            } else {
                paramMap.put("orgUidList", Collections.singletonList(currentUser.getOrgInfoUID()));
            }
        }

//        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.IsNull, null, "1*3+"));
//        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2, "1*3+"));
//        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 4, "1*3+"));




        covQueryCondition(searchFilters,paramMap);


        lastPageSort = pageRequest.getSort();
        StringBuilder orderBy = new StringBuilder();
        for (Sort.Order order : lastPageSort) {
            String property = order.getProperty();
            boolean ascending = order.isAscending();
            switch (property){
                case "supplierName":
                    orderBy.append("supplier.name").append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "assetContractAmount":
                    orderBy.append("assetContractAmount").append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "paymentProgress":
                    orderBy.append("paymentProgress").append(ascending ? " ASC" : " DESC").append(",");
                    break;
                default:
                    orderBy.append("c."+ ZipUtil.camelToUnderline(property)).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
            }
        }
        paramMap.put("orderBy", orderBy.substring(0, orderBy.length() - 1));


        List<ContractInfo> list= contractInfoMapper.pageList(paramMap);
        Long count= contractInfoMapper.pageListCount(paramMap);
        list.forEach(item->item.setPaymentProgress(item.getPaymentProgress()+"%"));

//        Page<ContractInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
//        List<ContractInfo> list = page.getContent();
        list.forEach(this::getCompletion);
//        calcPercentage(list);
        return new PageImpl<>(list, pageRequest, count);
    }

    @Override
    public void onFilter(FilterEvent event) {
        onFilterCustom(event, this::numberField);
    }
    protected boolean numberField(String fieldName, String filterVal) {
        if (isNumberField(fieldName)) {
            processNumberFiled(filterVal, fieldName);
            return true;
        }
        if(isDateTimeField(fieldName)){
            processTimeFiled(filterVal,fieldName);
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
                case "name":
                    param.put("name", searchFilter.value);
                    break;
                case "contractNum":
                    param.put("contractNum", searchFilter.value);
                    break;
                case "reportNum":
                    param.put("reportNum", searchFilter.value);
                    break;
                case "contractType":
                    param.put("contractType", searchFilter.value);
                    break;
                case "supplierName":
                    param.put("supplierName", searchFilter.value);
                    break;
                case "assetContractAmount":
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
                        logger.error("合同价格区间搜索数值转换异常 e:",e);
                    }
                    break;

                case "expectArrivalDate":
                case "startDate":
                case "endDate":
                    try {
                        Date date = (Date) searchFilter.value;
                        String pararName;
                        if (searchFilter.operator.name().equals("GTE")) {
                            pararName= fieldName+"GTE";
                            param.put(pararName, date);
                        } else {
                            pararName=fieldName+"LTE";
                            param.put(pararName, date);
                        }
                    }catch (Exception e){
                        logger.error("合同时间区间搜索数值转换异常 e:",e);
                    }
                    break;

                case "contractStatus":
                    param.put("contractStatus", searchFilter.value);
                    break;
                default:
                    break;
            }
        }
    }













    public void calcPercentage(List<ContractInfo> list){
        for (ContractInfo contractInfo : list) {
            double totalPaymentPrice = 0.0;
            List<ContractInfoItem> itemList = contractService.getContractItems(contractInfo.getId());
            if(CollectionUtils.isNotEmpty(itemList)){
                for (ContractInfoItem contractInfoItem : itemList) {
                    if(contractInfoItem.getPaymentPrice() != null){
                        totalPaymentPrice += contractInfoItem.getPaymentPrice();
                    }
                }
                if(totalPaymentPrice > 0){
                    if(contractInfo.getAmount() != null){
                        totalPaymentPrice = totalPaymentPrice / contractInfo.getAmount() * 100;
                        String percentage = new BigDecimal(totalPaymentPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%";
                        contractInfo.setPaymentProgress(percentage);
                    }else{
                        contractInfo.setPaymentProgress("0%");
                    }
                }else{
                    contractInfo.setPaymentProgress("0%");

                }
            }else{
                contractInfo.setPaymentProgress("0%");
            }
        }
    }

    protected long countValidContracts(ContractInfo contractInfo) {

        List<SearchFilter> localSearchFilters = new ArrayList<>();
        List<String> contractTypeList = Arrays.asList(ContractInfo.ContractType.values()).stream().map(item -> item.name()).collect(Collectors.toList());
        localSearchFilters.add(new SearchFilter("contractType", SearchFilter.Operator.IN, contractTypeList, "1*"));

        List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
        localSearchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        localSearchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        localSearchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        localSearchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        localSearchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, contractInfo.getId(), "1*"));

        Page<ContractInfo> contractInfoPage = dao.findBySearchFilter(localSearchFilters, new PageRequest(0, 1));

        return contractInfoPage.getTotalElements();
    }

    public boolean isEditable(ContractInfo contractInfo) {
        if (contractInfo == null || StringUtils.isEmpty(assetUid)) {
            return true;
        }

        long result = countValidContracts(contractInfo);

        if (result > 0) {
            return true;
        }

        return false;

    }

    public void completeContract(ContractInfo contractInfo) {
        if(contractInfo != null) {
            contractService.updateContractStatus(contractInfo.getId(), ContractInfo.ContractStatus.COMPLETE.toString());
        }
    }

    public void doubleClick(SelectEvent event){
        selected = (ContractInfo) event.getObject();
        String str = "action=Edit&selectedid=".concat(selected.getId());
        String url = "ContractView.xhtml?str="+ UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }


    public void deleteContract(){
        List<ContractInfoItem> contractItems = contractService.getContractItems(selected.getId());
        if(contractService.deleteContract(selected)){
            for (ContractInfoItem item : contractItems) {
                if (ContractInfoItem.ContractInfoItemType.ExistAsset.toString().equals(item.getItemType())) {
                    contractService.sendItemMq(item, SystemEventAuditEnum.CONTRACT_ASSET_INFO_DELETE);
                }
            }
            WebUtil.addSuccessMessage(WebUtil.getMessage("Deleted"));
        }else{
            WebUtil.addErrorMessage(WebUtil.getMessage("CannotBeDeleted"));
        }
    }

    public void getCompletion(ContractInfo contractInfo) {
        String completion = "";
        if (ContractInfo.ContractType.Equipment.toString().equals(contractInfo.getContractType())) {
            List<ContractInfoItem> purchases = contractInfoItemRepository.findByContractIdAndItemType(contractInfo.getId(), "Purchase");
            List<ArrivalList> arrivalLists = arrivalListRepository.findByContractItemIdIn(purchases.stream().map(ContractInfoItem::getId).collect(Collectors.toList()));
            if (ObjectUtil.isNotEmpty(arrivalLists)) {
                Set<Integer> arrivalStatus = arrivalLists.stream().map(ArrivalList::getStatus).collect(Collectors.toSet());
                List<ArrivalListItem> arrivalListItems = Optional.ofNullable(arrivalListItemRepository.findByArrivalListIdIn(arrivalLists.stream().map(ArrivalList::getId).collect(Collectors.toList()))).orElse(new ArrayList<>());
                Set<String> arrivalItemStatus = arrivalListItems.stream().map(ArrivalListItem::getInstallStatus).collect(Collectors.toSet());
                Map<String, Integer> contractItemMap = purchases.stream().collect(Collectors.toMap(ContractInfoItem::getId, ContractInfoItem::getContractAssetQuantity));
                Map<String, List<ArrivalList>> arrivalMap = arrivalLists.stream().collect(Collectors.groupingBy(ArrivalList::getContractItemId));
                int isFinished = 0;
                for (Map.Entry<String, Integer> item : contractItemMap.entrySet()) {
                    Integer quantity = item.getValue();
                    List<ArrivalList> lists = arrivalMap.get(item.getKey());
                    if (ObjectUtil.isNotEmpty(lists)) {
                        int sum = lists.stream().mapToInt(ArrivalList::getQuantity).sum();
                        if (sum >= quantity) {
                            isFinished++;
                        }
                    }
                }
                boolean arrival = arrivalItemStatus.isEmpty() || (arrivalItemStatus.size() == 1 && arrivalItemStatus.contains("new"));
                if (isFinished == contractItemMap.size()) {
                    // 到货完成
                    if (arrival) {
                        // 未安装
                        completion = "到货完成 未安装";
                    } else {
                        if (arrivalStatus.size() == 1 && arrivalStatus.contains(3)) {
                            completion = "到货完成 验收完成";
                        } else {
                            completion = "到货完成 验收中";
                        }
                    }
                } else {
                    if (arrival) {
                        completion =  "到货中 未安装";
                    } else {
                        completion =  "到货中 验收中";
                    }
                }
            } else {
                completion = "未到货";
            }
        }
        contractInfo.setCompletion(completion);
        if (ObjectUtil.isNotEmpty(completion) && contractExpirationReminderDays > 0) {
            contractInfo.setContractAlertStatus(contractExpirationReminderDays);
        }
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Create");
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Edit&selectedid=".concat(selected.getId()));
    }

    public String renewalAction(){
        ContractInfo c = copyContract();
        return UrlEncryptController.encodeUrlParam("ContractEdit?action=Edit&selectedid=".concat(c.getId()));
    }

    public ContractInfo copyContract(){
        ContractInfo contractInfo = new ContractInfo();
        BeanUtils.copyProperties(this.selected, contractInfo);
        contractInfo.setContractNum(null);
        contractInfo.setStartDate(null);
        contractInfo.setEndDate(null);
        contractInfo.setAmount(null);
        contractInfo.setEndMonths(null);
        contractInfo.setId(null);
        contractInfo.setLastModifiedBy(null);
        contractInfo.setLastModifiedDate(null);
        contractInfo.setCreatedBy(UserContextService.getCurrentUserAccount().getName());
        contractInfo.setCreatedDate(new Date());
        //contractInfo.set
        dao.save(contractInfo);

        List<ContractInfoItem> oldItem = contractService.getContractItems(selected.getId());
        List<ContractInfoItem> newContractItem = new ArrayList<>();
        for(ContractInfoItem i : oldItem){
            if(ContractInfoItem.ContractInfoItemType.ExistAsset.toString().equals(i.getItemType())){
                ContractInfoItem newItem = new ContractInfoItem();
                BeanUtils.copyProperties(i, newItem);
                newItem.setId(null);
                newItem.setContractId(contractInfo.getId());
                newContractItem.add(newItem);
            }
        }
        if(newContractItem.size() > 0 ){
            contractInfoItemRepository.save(newContractItem);
        }

        return contractInfo;
    }

    public String importAction() {
        return "contractImport/ContractImport?faces-redirect=true";
    }
    public String importItemAction() {
        return "contractImport/ContractItemImport?faces-redirect=true";
    }
    public String exportAction() {
        return "contractExport/ContractExport?faces-redirect=true";
    }

}
