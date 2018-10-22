package com.ge.apm.service.asset;

import com.ge.apm.dao.ContractInfoItemRepository;
import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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


    public List<Map<String, Object>> createContractData(OrgInfo selectedOrg, String contractType, Date startDateFilter, Date endDateFilter){

        List<Map<String, Object>> resultList = new ArrayList<>();

        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();

        if (selectedOrg != null && selectedOrg.getOrgLevel() == 0) {
            SearchFilter tenantFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(tenantFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 1) {
            SearchFilter institutionFilter = new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(institutionFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 2) {
            SearchFilter hospitalFilter = new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedOrg.getUid());
            searchFilters.add(hospitalFilter);
        }
        if (selectedOrg != null && selectedOrg.getOrgLevel() == 3) {
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

            Map<String, Object> contractInfoMap = new HashedMap();
            contractInfoMap.put("合同UID", contractInfo.getId());
            contractInfoMap.put("名称", contractInfo.getName());
            contractInfoMap.put("合同编号", contractInfo.getContractNum());
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

            contractResultList.add(contractInfoMap);
        }

        return contractResultList;

    }

    public List<Map<String, Object>> createContractItemData(List<Map<String, Object>> contractResultList){

        List<Map<String, Object>> contractItemResultList = new ArrayList<>();

        for (Map contractInfoMap : contractResultList) {
            List<ContractInfoItem> contractInfoItemList = contractInfoItemDao.findByContractId(contractInfoMap.get("合同UID").toString());

            for(ContractInfoItem contractInfoItem : contractInfoItemList){
                Map<String, Object> contractInfoItemMap = new HashedMap();
                contractInfoItemMap.put("资产名称", contractInfoItem.getAssetInfo().getName());
                contractInfoItemMap.put("制造商序列号", contractInfoItem.getAssetInfo().getName());
                contractInfoItemMap.put("所属科室", contractInfoItem.getAssetInfo().getClinicalDeptName());
                contractInfoItemMap.put("资产名称", contractInfoItem.getAssetInfo().getName());
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
