package com.ge.apm.service.wo;

import com.ge.apm.dao.WorkOrderDetailRepository;
import com.ge.apm.dao.mapper.WorkOrderReportMapper;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrder_Detail;
import com.ge.apm.domain.pure.WorkOrderDetailPojo;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by 212605082 on 2019/3/18.
 */
@Component
public class WorkOrderDetailExportService {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderDetailExportService.class);
    private static final String SHEET_WORKORDERDETAIL = "WorkOrderDetail List";
    private static final String STATEMENT_STATUS_OPENED = "Opened";
    private static final String STATEMENT_STATUS_CLOSED = "Closed";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OrgInfoTreeService orgInfoTreeService;
    @Autowired
    private WorkOrderReportMapper worDao;
    @Autowired
    private WorkOrderDetailRepository workOrderDetailDao;

    public List<Map<String, Object>> createWorkOrderDetailData(Map<String, Object> filterMap){

        List<Map<String, Object>> resultList = new ArrayList<>();
        List<SearchFilter> searchFilters = new ArrayList<>();

        if(filterMap.containsKey("selectedOrg")){
            OrgInfo selectedOrg = (OrgInfo)filterMap.get("selectedOrg");
            SearchFilter siteFilter = new SearchFilter("orgUID", SearchFilter.Operator.IN, selectedOrg.getUid());
            searchFilters.add(siteFilter);
        }

        if(filterMap.containsKey("filterDeptOrg") && filterMap.containsKey("clinicalDeptList")){
            OrgInfo clinicalDept = (OrgInfo)filterMap.get("filterDeptOrg");
            List<OrgInfo> clinicalDeptList = (List<OrgInfo>)filterMap.get("clinicalDeptList");

            List<OrgInfo> orgSearchList = new ArrayList<>();
            orgSearchList.add(clinicalDept);

            Set<OrgInfo> tempOrgSearchSet = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgSearchList, clinicalDeptList);
            List<String> clinicalDeptUIDList = new ArrayList<>();
            for (OrgInfo oi : tempOrgSearchSet) {
                clinicalDeptUIDList.add(oi.getUid());
            }
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalDeptUIDList));
        }

        if(filterMap.containsKey("filterCurrentOwner")) {
            UserAccount currentOwner = (UserAccount)filterMap.get("filterCurrentOwner");
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, currentOwner.getId()));
        }

        ZoneId zone = ZoneId.systemDefault();
        if (filterMap.containsKey("startTime")) {
            Date startTime = (Date)filterMap.get("startTime");
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.GTE, startTime));
        }
        if (filterMap.containsKey("endTime")) {
            Date endTime = (Date)filterMap.get("endTime");
            LocalDate endDate = LocalDateTime.ofInstant(endTime.toInstant(), zone).toLocalDate();
            searchFilters.add(new SearchFilter("createdDate", SearchFilter.Operator.LT, Date.from(endDate.plus(1, ChronoUnit.DAYS).atStartOfDay().atZone(zone).toInstant())));
        }
        if(filterMap.containsKey("filterMinPrice")){
            Double minPrice = (Double)filterMap.get("filterMinPrice");
            searchFilters.add(new SearchFilter("totalPrice", SearchFilter.Operator.GTE, minPrice));
        }
        if(filterMap.containsKey("filterMaxPrice")){
            Double maxPrice = (Double)filterMap.get("filterMaxPrice");
            searchFilters.add(new SearchFilter("totalPrice", SearchFilter.Operator.LTE, maxPrice));
        }
        if(filterMap.containsKey("filterStatementStatus")){
            String statementStatus = (String) filterMap.get("filterStatementStatus");
            if(STATEMENT_STATUS_OPENED.equals(statementStatus)){
                searchFilters.add(new SearchFilter("statementStatus", SearchFilter.Operator.EQ, Boolean.FALSE));
            }else if(STATEMENT_STATUS_CLOSED.equals(statementStatus)){
                searchFilters.add(new SearchFilter("statementStatus", SearchFilter.Operator.EQ, Boolean.TRUE));
            }
        }

        if(filterMap.containsKey("searchStr")) {
            String searchStr = (String)filterMap.get("searchStr");
            searchFilters.add(new SearchFilter("searchStr", SearchFilter.Operator.EQ, searchStr));
        }
        if (filterMap.containsKey("assetTypeGroupEncodedSet")) {
            Set<String> assetTypeGroupEncodedSet = (Set<String>) filterMap.get("assetTypeGroupEncodedSet");
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, assetTypeGroupEncodedSet));
        }

        searchFilters.add(new SearchFilter("approvedStatus", SearchFilter.Operator.IN, filterMap.get("approvedStatus")));
        List<WorkOrderDetailPojo> workOrderDetailList =  worDao.workOrderDetailExport(searchFilters);

        // 查询MVS工单备件数据
        try {
            searchFilters.add(new SearchFilter("userUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            List<WorkOrderDetailPojo> mvsWorkOrderDetailList = worDao.mvsWorkOrderDetailExport(searchFilters);
            log.info("MVS workOrderDetail query returned {} records, local query returned {} records", mvsWorkOrderDetailList.size(), workOrderDetailList.size());
            workOrderDetailList.addAll(mvsWorkOrderDetailList);
        } catch (Exception e) {
            log.error("Failed to query MVS workOrderDetail data", e);
        }

        for(WorkOrderDetailPojo workOrderDetailPojo : workOrderDetailList){
            Map<String, Object> workOrderDetailMap = new HashedMap();

            workOrderDetailMap.put("wodID", workOrderDetailPojo.getId());
            workOrderDetailMap.put(WebUtil.getMessage("Department"), workOrderDetailPojo.getAssetClinicalDeptName());
            workOrderDetailMap.put(WebUtil.getMessage("EquipmentID_xlsx"), workOrderDetailPojo.getAssetDepartNum());
            workOrderDetailMap.put(WebUtil.getMessage("AssetName_xlsx"), workOrderDetailPojo.getAssetName());
            workOrderDetailMap.put(WebUtil.getMessage("Vendor"), workOrderDetailPojo.getSupplierName());
            workOrderDetailMap.put(WebUtil.getMessage("PartName_xlsx2"), workOrderDetailPojo.getPartsName());
            workOrderDetailMap.put(WebUtil.getMessage("Quantity"), workOrderDetailPojo.getPartsQuantity());
            workOrderDetailMap.put(WebUtil.getMessage("ClosedPrice_xlsx"), workOrderDetailPojo.getClosedPrice());
            workOrderDetailMap.put(WebUtil.getMessage("InvoiceNumber_xlsx"), workOrderDetailPojo.getInvoiceNum());
            workOrderDetailMap.put(WebUtil.getMessage("InvoiceTime_xlsx"), formatDate(workOrderDetailPojo.getInvoiceTime()));
            workOrderDetailMap.put(WebUtil.getMessage("approveUserId"), workOrderDetailPojo.getApproverName());
            workOrderDetailMap.put(WebUtil.getMessage("SerialNumber_xlsx"), workOrderDetailPojo.getSerialNum());
            workOrderDetailMap.put(WebUtil.getMessage("ContractNumber_xlsx"), workOrderDetailPojo.getContractNum());
            workOrderDetailMap.put(WebUtil.getMessage("Remark"), workOrderDetailPojo.getComment());
            workOrderDetailMap.put(WebUtil.getMessage("PartType"), workOrderDetailPojo.getPartsType() != null ? FieldValueMessageController.fieldValue("partsType", workOrderDetailPojo.getPartsType()) : "");
            workOrderDetailMap.put(WebUtil.getMessage("ApprovalStatus"), workOrderDetailPojo.getApproved() == null ? FieldValueMessageController.fieldValue("approveStatus", "1")
                    : Boolean.TRUE == workOrderDetailPojo.getApproved() ? FieldValueMessageController.fieldValue("approveStatus", "2") : FieldValueMessageController.fieldValue("approveStatus", "3"));
            if(workOrderDetailPojo.getStatementStatus() != null){
                workOrderDetailMap.put(WebUtil.getMessage("CloseStatus_xlsx"), workOrderDetailPojo.getStatementStatus() == Boolean.TRUE ? WebUtil.getMessage("ClosedFinish") : WebUtil.getMessage("Opened"));
            }else{
                workOrderDetailMap.put(WebUtil.getMessage("CloseStatus_xlsx"), WebUtil.getMessage("Opened"));
            }


            resultList.add(workOrderDetailMap);
        }

        return resultList;
    }

    public Boolean closeStatement(List<Map<String, Object>> workOrderDetailDataList){

        for (Map<String, Object> workOrderDetailData : workOrderDetailDataList) {
            V2_WorkOrder_Detail workOrderDetail = workOrderDetailDao.findById(workOrderDetailData.get("wodID").toString());
            workOrderDetail.setClosed(Boolean.TRUE);
            workOrderDetailDao.save(workOrderDetail);
        }

        return Boolean.TRUE;

    }

    public Boolean openStatement(List<Map<String, Object>> workOrderDetailDataList){
        for (Map<String, Object> workOrderDetailData : workOrderDetailDataList) {
            V2_WorkOrder_Detail workOrderDetail = workOrderDetailDao.findById(workOrderDetailData.get("wodID").toString());
            workOrderDetail.setClosed(Boolean.FALSE);
            workOrderDetailDao.save(workOrderDetail);
        }

        return Boolean.TRUE;
    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    public Boolean exportOrderDetailData(ExcelDocument doc, List<Map<String, Object>> workOrderDetailData) {

        Integer keyRow_workOrderDetail = 1;
        Integer dataRow_workOrderDetail = 2;

        for (Map<String, Object> item : workOrderDetailData) {
            doc.writeRowData(SHEET_WORKORDERDETAIL, keyRow_workOrderDetail, dataRow_workOrderDetail++, item);
        }

        return Boolean.TRUE;
    }

}
