package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 212605082 on 2018/9/20.
 */
@Service
public class ContractExportService {

    private static final String SHEET_WORKORDER_CONTRACT = "Contract List";
    private static final String SHEET_WORKORDER_CONTRACT_ITEM = "ContractItem List";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @Autowired
    private ContractInfoRepository contractInfoDao;
    @Autowired
    private ContractInfoItemRepository contractInfoItemDao;
    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private OrgInfoRepository orgInfoDao;
    @Autowired
    private ContractInfoService contractService;
    @Autowired
    private ArrivalListRepository arrivalListRepository;
    @Autowired
    private ArrivalListItemRepository arrivalListItemRepository;
    @Autowired
    private ContractInfoItemRepository contractInfoItemRepository;


    public List<Map<String, Object>> createContractData(OrgInfo selectedOrg, String contractType, Date startDateFilter, Date endDateFilter){

        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if (selectedOrg != null && Objects.equals(selectedOrg.getOrgLevel(), 0)) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if (selectedOrg != null && Objects.equals(selectedOrg.getOrgLevel(), 1)) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if (selectedOrg != null && Objects.equals(selectedOrg.getOrgLevel(), 2)) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if (selectedOrg != null && Objects.equals(selectedOrg.getOrgLevel(), 3)) {
            SearchFilter siteFilter = new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if (contractType != null) {
            searchFilters.add(new SearchFilter("contractType", SearchFilter.Operator.EQ, contractType));
        }

        ZoneId zone = ZoneId.systemDefault();
        if (startDateFilter != null) {
            searchFilters.add(new SearchFilter("startDate", SearchFilter.Operator.GTE, startDateFilter));
        }
        if (endDateFilter != null) {
            LocalDate endDate = LocalDateTime.ofInstant(endDateFilter.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("endDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }

        List<ContractInfo> contractInfoList = contractInfoDao.findBySearchFilter(searchFilters);

        List<Map<String, Object>> contractResultList = new ArrayList<>();

        for (ContractInfo contractInfo : contractInfoList) {
            String supplierName = "";
            if(contractInfo.getSupplierUid() != null){
                Supplier supplier = supplierDao.findByUid(contractInfo.getSupplierUid());
                if(supplier != null){
                    supplierName = supplier.getName();
                }
            }
            calcPercentage(contractInfo);
            getCompletion(contractInfo);
            Map<String, Object> contractInfoMap = new HashedMap();
            contractInfoMap.put("合同UID", contractInfo.getId());
            contractInfoMap.put("名称", contractInfo.getName());
            contractInfoMap.put("合同编号", contractInfo.getContractNum());
            contractInfoMap.put("报告号", contractInfo.getReportNum());
            contractInfoMap.put("外贸合同号", contractInfo.getForeignTradeNum());
            contractInfoMap.put("发票号", contractInfo.getInvoiceNum());
            contractInfoMap.put("合同类型", contractInfo.getContractType() != null ? FieldValueMessageController.fieldValue("contractInfoType", contractInfo.getContractType()) : "");
            contractInfoMap.put("所属院区", orgInfoDao.getByUid(contractInfo.getSiteUID()).getName());
            contractInfoMap.put("供应商", supplierName);
            contractInfoMap.put("合同总额", contractInfo.getAmount());
            contractInfoMap.put("开始时间", formatDate(contractInfo.getStartDate()));
            contractInfoMap.put("结束时间", formatDate(contractInfo.getEndDate()));
            contractInfoMap.put("合同时间(月)", contractInfo.getEndMonths());
            contractInfoMap.put("endMonths", contractInfo.getEndMonths());
            contractInfoMap.put("应到货时间", formatDate(contractInfo.getExpectArrivalDate()));
            contractInfoMap.put("是否完成验收","COMPLETE".equals(contractInfo.getContractStatus())?"是":"否" );
            contractInfoMap.put("付款进度", contractInfo.getPaymentProgress());
            contractInfoMap.put("完成情况", contractInfo.getCompletion());

            contractResultList.add(contractInfoMap);
        }

        return contractResultList;

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
    }
    public void calcPercentage(ContractInfo contractInfo){

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



    public List<Map<String, Object>> createContractItemData(List<Map<String, Object>> contractResultList){

        List<Map<String, Object>> contractItemResultList = new ArrayList<>();

        for (Map contractInfoMap : contractResultList) {
            List<ContractInfoItem> contractInfoItemList = contractInfoItemDao.findByContractId(contractInfoMap.get("合同UID").toString());

            for(ContractInfoItem contractInfoItem : contractInfoItemList){
                Map<String, Object> contractInfoItemMap = new HashedMap();
                if(contractInfoItem.getAssetInfo() != null){
                    contractInfoItemMap.put("资产名称", contractInfoItem.getAssetInfo().getName());
                    contractInfoItemMap.put("制造商序列号", contractInfoItem.getAssetInfo().getSerialNum());
                    contractInfoItemMap.put("所属科室", contractInfoItem.getAssetInfo().getClinicalDeptName());
                }else{
                    contractInfoItemMap.put("资产名称", contractInfoItem.getContractAssetName());
                    contractInfoItemMap.put("制造商序列号", "");
                    contractInfoItemMap.put("所属科室", "");
                }

                contractInfoItemMap.put("合同名称", contractInfoMap.get("名称").toString());
                contractInfoItemMap.put("合同UID", contractInfoMap.get("合同UID").toString());
                contractInfoItemMap.put("资产UID", contractInfoItem.getAssetUid());

                contractItemResultList.add(contractInfoItemMap);
            }

        }

        return contractItemResultList;

    }

    public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> contractData, List<Map<String, Object>> contractItemData) {

        Integer keyRow_contract = 1;
        Integer dataRow_contract = 2;
        for (Map<String, Object> item : contractData) {
            doc.writeRowData(SHEET_WORKORDER_CONTRACT, keyRow_contract, dataRow_contract++, item);
        }

        Integer keyRow_contractItem = 1;
        Integer dataRow_contractItem = 2;
        for (Map<String, Object> item : contractItemData) {
            doc.writeRowData(SHEET_WORKORDER_CONTRACT_ITEM, keyRow_contractItem, dataRow_contractItem++, item);
        }

        return Boolean.TRUE;
    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

}
