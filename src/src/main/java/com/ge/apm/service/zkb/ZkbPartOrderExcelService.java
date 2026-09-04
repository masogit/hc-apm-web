package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbPartOrderItemRepository;
import com.ge.apm.dao.ZkbPartOrderReturnRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.ZkbPartOrder;
import com.ge.apm.domain.ZkbPartOrderItem;
import com.ge.apm.domain.ZkbPartOrderReturn;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 503189362
 * @date : 2020/11/25
 * @description :
 */
@Service
public class ZkbPartOrderExcelService {

    public static final String SHEET_ZKB_PART_ORDER = "维修工单";
    public static final String SHEET_ZKB_PART_ORDER_ITEM = "备件明细";
    public static final String SHEET_ZKB_PART_ORDER_CAT = "探头明细";
    public static final String SHEET_ZKB_PART_ORDER_RETURN = "归还明细";
    public static final String SHEET_ZKB_PART_ORDER_REPORT = "维修工单报表";

    @Autowired
    ZkbPartOrderItemRepository zkbPartOrderItemRepository;

    @Autowired
    ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;

    @Autowired
    TenantInfoRepository tenantInfoRepository;


    public HashMap<String,Object> createZkbPartOrderData(List<ZkbPartOrder> zkbPartOrderList){
        List<Map<String, Object>> zkbPartOrderDataList = new ArrayList<>();
        List<Map<String,Object>> zkbPartOrderCatDataList = new ArrayList<>();

        HashMap<String, String> partOrderNoMap = new HashMap<>();
        List<String> ids = new ArrayList<>();
        for(ZkbPartOrder p : zkbPartOrderList){
            Map<String, Object> zkbPartOrderDataMap = getPartOrderMap(p);
            zkbPartOrderDataList.add(zkbPartOrderDataMap);
            if(ZkbPartOrder.OrderType.Probe.toString().equals(p.getOrderType())){
                Map<String, Object> zkbPartOrderCatDataMap = getPartOrderCatMap(p);
                zkbPartOrderCatDataList.add(zkbPartOrderCatDataMap);
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(p.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(p.getOrderType())){
                ids.add(p.getId());
                partOrderNoMap.put(p.getId(),p.getPartOrderNo());
            }
        }
        List<Map<String, Object>> zkbPartOrderItemDataList = createZkbPartOrderItemData(ids,partOrderNoMap);
        List<Map<String, Object>> zkbPartOrderReturnDataList = createZkbPartOrderReturnData(ids,partOrderNoMap);
        List<Map<String, Object>> zkbPartOrderReportDataList = createZkbPartOrderReportData(zkbPartOrderList);
        HashMap<String,Object> map = new HashMap<>(6);
        map.put("zkbPartOrderDataList",zkbPartOrderDataList);
        map.put("zkbPartOrderItemDataList",zkbPartOrderItemDataList);
        map.put("zkbPartOrderCatDataList",zkbPartOrderCatDataList);
        map.put("zkbPartOrderReturnDataList",zkbPartOrderReturnDataList);
        map.put("zkbPartOrderReportDataList",zkbPartOrderReportDataList);
        return map;
    }

    public List<Map<String, Object>> createZkbPartOrderItemData(List<String> ids,HashMap<String, String> partOrderNoMap){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("partOrderId", SearchFilter.Operator.IN, ids));
        List<ZkbPartOrderItem> zkbPartOrderItems = zkbPartOrderItemRepository.findBySearchFilter(searchFilters);
        List<Map<String,Object>> zkbPartOrderItemDataList = new ArrayList<>();
        for(ZkbPartOrderItem i : zkbPartOrderItems){
            Map<String, Object> zkbPartOrderItemDataMap = getPartOrderItemMap(i,partOrderNoMap);
            zkbPartOrderItemDataList.add(zkbPartOrderItemDataMap);
        }
        return zkbPartOrderItemDataList;
    }

    public List<Map<String, Object>> createZkbPartOrderReturnData(List<String> ids,HashMap<String, String> partOrderNoMap){
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("partOrderId", SearchFilter.Operator.IN, ids));

        List<ZkbPartOrderReturn> zkbPartOrderReturns = zkbPartOrderReturnRepository.findBySearchFilter(searchFilters);
        List<Map<String, Object>> zkbPartOrderReturnDataList = new ArrayList<>();
        for (ZkbPartOrderReturn r: zkbPartOrderReturns){
            Map<String, Object> zkbPartOrderReturnDataMap = new HashMap<>();
            zkbPartOrderReturnDataMap.put("运单号", r.getWaybillNo());
            if(r.getReturnDate() != null){
                zkbPartOrderReturnDataMap.put("归还日期", DateFormatUtils.format(r.getReturnDate(),"yyyy-MM-dd  HH:mm:ss"));
            }
            zkbPartOrderReturnDataMap.put("配件号", r.getPartNo());
            if(StringUtils.isNotEmpty(r.getReturnType())){
                zkbPartOrderReturnDataMap.put("归还类型", WebUtil.getFieldValueMessage("zkbPoReturnType", r.getReturnType()));
            }
            zkbPartOrderReturnDataMap.put("Part Serial Number", r.getPartSerialNumber());
            zkbPartOrderReturnDataMap.put("故障描述", r.getPartQualityIssue());
            zkbPartOrderReturnDataMap.put("归还数量", r.getQty());
            zkbPartOrderReturnDataMap.put("备注", r.getRemark());
            zkbPartOrderReturnDataMap.put("申请编号", partOrderNoMap.get(r.getPartOrderId()));
            zkbPartOrderReturnDataList.add(zkbPartOrderReturnDataMap);
        }
        return zkbPartOrderReturnDataList;
    }

    public  List<Map<String, Object>> createZkbPartOrderReportData(List<ZkbPartOrder> zkbPartOrderList){
        List<Map<String, Object>> zkbPartOrderReportDataList = new ArrayList<>();
        for(ZkbPartOrder p : zkbPartOrderList){
            Map<String, Object> zkbPartOrderDataMap = new HashMap<>();
            if(ZkbPartOrder.OrderType.Probe.toString().equals(p.getOrderType())){
                zkbPartOrderDataMap = getPartOrderMap(p);
                zkbPartOrderDataMap.put("探头号", p.getCatNo());
                if(p.getZkbPartOrderCat() != null){
                    zkbPartOrderDataMap.put("探头型号", p.getZkbPartOrderCat().getCatModel());
                    zkbPartOrderDataMap.put("价值类型", p.getZkbPartOrderCat().getValueType());
                }
                zkbPartOrderDataMap.put("旧探头SN号输入框", p.getOldCatSn());
                zkbPartOrderDataMap.put("数量", p.getCatNum());
                zkbPartOrderReportDataList.add(zkbPartOrderDataMap);
            }else if(ZkbPartOrder.OrderType.Parts.toString().equals(p.getOrderType()) || ZkbPartOrder.OrderType.Tool.toString().equals(p.getOrderType())){
                List<ZkbPartOrderItem> zkbPartOrderItemList = zkbPartOrderItemRepository.findByPartOrderId(p.getId());
                for(ZkbPartOrderItem i : zkbPartOrderItemList){
                    zkbPartOrderDataMap = getPartOrderMap(p);
                    zkbPartOrderDataMap.put("配件号/工具号", i.getPartNo());
                    zkbPartOrderDataMap.put("配件名称", i.getPartName());
                    zkbPartOrderDataMap.put("配件数量", i.getPartQty());
                    zkbPartOrderDataMap.put("配件SN(序列号)", i.getSerialNumber());
                    zkbPartOrderDataMap.put("Order#", i.getOrderNo());
                    zkbPartOrderDataMap.put("价格", i.getPrice());
                    zkbPartOrderDataMap.put("备注（说明是否DOA或者Short life）", i.getRemark());
                    zkbPartOrderReportDataList.add(zkbPartOrderDataMap);
                }
            }else if(ZkbPartOrder.OrderType.Artificial.toString().equals(p.getOrderType())){
                zkbPartOrderDataMap = getPartOrderMap(p);
                zkbPartOrderReportDataList.add(zkbPartOrderDataMap);
            }
        }
        return zkbPartOrderReportDataList;
    }

    public Map<String, Object> getPartOrderMap(ZkbPartOrder p){
        Map<String, Object> zkbPartOrderDataMap = new HashMap<>(50);
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(p.getTenantUID());
        if(tenantInfo != null){
            zkbPartOrderDataMap.put("代理商", tenantInfo.getName());
        }
        zkbPartOrderDataMap.put("申请编号", p.getPartOrderNo());
        zkbPartOrderDataMap.put("工单号", p.getSrNum());
        zkbPartOrderDataMap.put("合同号", p.getContractNo());
        if(p.getAssetInfo() != null){
            zkbPartOrderDataMap.put("Sys ID", p.getAssetInfo().getSystemId());
            zkbPartOrderDataMap.put("设备名称", p.getAssetInfo().getName());
        }

        zkbPartOrderDataMap.put("订单类型", WebUtil.getFieldValueMessage("zkbPartOrderType", p.getOrderType()));
        zkbPartOrderDataMap.put("名额类型", WebUtil.getFieldValueMessage("ZkbPoPartQuotaType", p.getRadiationQuotaType()));
        zkbPartOrderDataMap.put("申请人", p.getRequestUser());
        zkbPartOrderDataMap.put("申请人电话", p.getRequestPhone());
        zkbPartOrderDataMap.put("申请时间", DateFormatUtils.format(p.getRequestDate(),"yyyy-MM-dd HH:mm:ss"));
        zkbPartOrderDataMap.put("设备类型（DI）", p.getAssetTypeDi());
        zkbPartOrderDataMap.put("区域", p.getRegion());
        zkbPartOrderDataMap.put("医院名称", p.getHospitalName());
        zkbPartOrderDataMap.put("医院报修人姓名", p.getHospitalRepairer());
        zkbPartOrderDataMap.put("医院报修人电话", p.getHospitalRepairerPhone());
        zkbPartOrderDataMap.put("订单标注", p.getOrderAnnotation());
        zkbPartOrderDataMap.put("发生问题的order number", p.getProblemOrderNumber());
        if(StringUtils.isNotEmpty(p.getUrgentOrder())){
            zkbPartOrderDataMap.put("是否为紧急订单", WebUtil.getFieldValueMessage("zkbUrgentType", p.getUrgentOrder()));
        }
        if(StringUtils.isNotEmpty(p.getUrgencyReasons())){
            zkbPartOrderDataMap.put("订单紧急的原因", WebUtil.getFieldValueMessage("zkbUrgentReasonType", p.getUrgencyReasons()));
        }
        zkbPartOrderDataMap.put("订单数量/订单名额", p.getQty());
        zkbPartOrderDataMap.put("收货人", p.getReceiver());
        zkbPartOrderDataMap.put("收货人电话", p.getReceiverPhone());
        zkbPartOrderDataMap.put("收货地址", p.getReceiverAddress());
        zkbPartOrderDataMap.put("ZKB工程师姓名", p.getZkbEngineerName());
        zkbPartOrderDataMap.put("ZKB工程师电话", p.getZkbEngineerPhone());
        zkbPartOrderDataMap.put("产品型号", p.getProductNumber());
        zkbPartOrderDataMap.put("软件版本", p.getSoftwareVersion());
        zkbPartOrderDataMap.put("故障描述", p.getFaultDescription());
        zkbPartOrderDataMap.put("SR#", p.getSrNo());
        zkbPartOrderDataMap.put("Act#", p.getActivityNo());
        zkbPartOrderDataMap.put("GE工程师姓名", p.getGeEngineerName());
        zkbPartOrderDataMap.put("GE工程师电话", p.getGeEngineerPhone());
        zkbPartOrderDataMap.put("是否扣除名额", getValueByKey("whetherDeduct",p.getWhetherDeduct()));
        zkbPartOrderDataMap.put("备注", p.getRemark());
        if(StringUtils.isNotEmpty(p.getTechAuditerUid())){
            if(ZkbPartOrder.PartOrderStatus.TechnicalAccept.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("技术支持审批状态", "技术支持审批通过");
            }else if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("技术支持审批状态", "技术支持审批不通过");
            }else{
                zkbPartOrderDataMap.put("技术支持审批状态", "技术支持审批通过");
            }
        }
        zkbPartOrderDataMap.put("技术支持审批人", p.getTechAuditer());
        if(p.getTechAuditDate() != null){
            zkbPartOrderDataMap.put("技术支持审批时间", DateFormatUtils.format(p.getTechAuditDate(),"yyyy-MM-dd HH:mm:ss"));
        }
        zkbPartOrderDataMap.put("技术支持审批备注", p.getTechRemark());
        if(StringUtils.isNotEmpty(p.getManageAuditerUid())){
            if(ZkbPartOrder.PartOrderStatus.ManageAccept.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("区域经理审批状态", "区域经理审批通过");
            }else if(ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("区域经理审批状态", "区域经理审批不通过");
            }else{
                zkbPartOrderDataMap.put("区域经理审批状态", "区域经理审批通过");
            }
        }
        zkbPartOrderDataMap.put("区域经理审批人", p.getManageAuditer());
        if(p.getManageAuditDate() != null){
            zkbPartOrderDataMap.put("区域经理审批时间", DateFormatUtils.format(p.getManageAuditDate(),"yyyy-MM-dd HH:mm:ss"));
        }
        zkbPartOrderDataMap.put("区域经理审批备注", p.getManageRemark());
        if(StringUtils.isNotEmpty(p.getDirectorAuditerUid())){
            if(ZkbPartOrder.PartOrderStatus.DirectorAccept.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("销售总监审批状态", "销售总监审批通过");
            }else if(ZkbPartOrder.PartOrderStatus.DirectorReject.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("销售总监审批状态", "销售总监审批不通过");
            }else{
                zkbPartOrderDataMap.put("销售总监审批状态", "销售总监审批通过");
            }
        }
        zkbPartOrderDataMap.put("销售总监审批人", p.getDirectorAuditer());
        if(p.getDirectorAuditDate() != null){
            zkbPartOrderDataMap.put("销售总监审批时间", DateFormatUtils.format(p.getDirectorAuditDate(),"yyyy-MM-dd HH:mm:ss"));
        }
        zkbPartOrderDataMap.put("销售总监审批备注", p.getDirectorRemark());
        zkbPartOrderDataMap.put("下单人", p.getOrderedAuditer());
        zkbPartOrderDataMap.put("下单备注", p.getOrderedRemark());
        if(StringUtils.isNotEmpty(p.getOrderedAuditerUid())){
            if(ZkbPartOrder.PartOrderStatus.Ordered.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("下单状态", "已下单");
            }else if(ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(p.getStatus())){
                zkbPartOrderDataMap.put("下单状态", "400下单拒绝");
            }else{
                zkbPartOrderDataMap.put("下单状态", "已下单");
            }
        }
        if(p.getOrderedDate() != null){
            zkbPartOrderDataMap.put("下单时间", DateFormatUtils.format(p.getOrderedDate(),"yyyy-MM-dd HH:mm:ss"));
        }
        if(ZkbPartOrder.OrderType.Artificial.toString().equals(p.getOrderType())){
            if("1".equals(p.getArtificialType())){
                zkbPartOrderDataMap.put("是否需要SSA Key", "t".equals(p.getSsaKeyFlag()) ? "是" : "否");
            }else {
                zkbPartOrderDataMap.put("是否需要Dongle", "t".equals(p.getSsaKeyFlag()) ? "是" : "否");
            }
        }
        return zkbPartOrderDataMap;
    }

    public Map<String, Object> getPartOrderCatMap(ZkbPartOrder p){
        Map<String, Object> zkbPartOrderCatDataMap = new HashMap<>(15);
        zkbPartOrderCatDataMap.put("申请编号", p.getPartOrderNo());
        zkbPartOrderCatDataMap.put("Order#", p.getOrderNo());
        zkbPartOrderCatDataMap.put("探头号", p.getCatNo());
        if(p.getZkbPartOrderCat() != null){
            zkbPartOrderCatDataMap.put("探头型号", p.getZkbPartOrderCat().getCatModel());
            zkbPartOrderCatDataMap.put("价值类型", p.getZkbPartOrderCat().getValueType());
        }

        zkbPartOrderCatDataMap.put("旧探头SN号输入框", p.getOldCatSn());
        zkbPartOrderCatDataMap.put("数量", p.getCatNum());
        if(StringUtils.isNotEmpty(p.getReturnType())){
            zkbPartOrderCatDataMap.put("归还类型", WebUtil.getFieldValueMessage("zkbPoReturnType", p.getReturnType()));
        }
        zkbPartOrderCatDataMap.put("Part Serial Number", p.getPartSerialNumber());
        zkbPartOrderCatDataMap.put("故障描述", p.getPartQualityIssue());
        zkbPartOrderCatDataMap.put("归还数量", p.getReturnQty());
        zkbPartOrderCatDataMap.put("运单号", p.getWaybillNo());
        if(p.getReturnDate() != null){
            zkbPartOrderCatDataMap.put("归还日期", DateFormatUtils.format(p.getReturnDate(),"yyyy-MM-dd HH:mm:ss"));
        }
        zkbPartOrderCatDataMap.put("新探头SN编号", p.getNewCatSn());
        zkbPartOrderCatDataMap.put("归还备注", p.getReturnRemark());
        return zkbPartOrderCatDataMap;
    }

    public Map<String, Object> getPartOrderItemMap(ZkbPartOrderItem i,HashMap<String, String> partOrderNoMap){
        Map<String, Object> zkbPartOrderItemDataMap = new HashMap<>(20);
        zkbPartOrderItemDataMap.put("配件号/工具号", i.getPartNo());
        zkbPartOrderItemDataMap.put("配件名称", i.getPartName());
        zkbPartOrderItemDataMap.put("配件数量", i.getPartQty());
        zkbPartOrderItemDataMap.put("配件SN(序列号)", i.getSerialNumber());
        zkbPartOrderItemDataMap.put("Order#", i.getOrderNo());
        zkbPartOrderItemDataMap.put("价格", i.getPrice());
        zkbPartOrderItemDataMap.put("备注（说明是否DOA或者Short life）", i.getRemark());
        zkbPartOrderItemDataMap.put("申请编号", partOrderNoMap.get(i.getPartOrderId()));
        return zkbPartOrderItemDataMap;
    }

    public String getValueByKey(String type,String key){
        if(StringUtils.isEmpty(key)){
            return "";
        }
        if("whetherDeduct".equals(type)){
            switch (key){
                case "t":
                    return "是";
                case "f":
                    return "否";
                default:
                    return "";
            }
        }
        return "";
    }


    public void exportData(ExcelDocument doc, HashMap<String, Object> dataMap) {
        Integer keyRow = 1;
        Integer dataRow = 2;

        List<Map<String, Object>> zkbPartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbPartOrderDataList");
        List<Map<String, Object>> zkbPartOrderItemDataList = (List<Map<String, Object>>) dataMap.get("zkbPartOrderItemDataList");
        List<Map<String, Object>> zkbPartOrderCatDataList = (List<Map<String, Object>>) dataMap.get("zkbPartOrderCatDataList");
        List<Map<String, Object>> zkbPartOrderReturnDataList = (List<Map<String, Object>>) dataMap.get("zkbPartOrderReturnDataList");
        List<Map<String, Object>> zkbPartOrderReportDataList = (List<Map<String, Object>>) dataMap.get("zkbPartOrderReportDataList");
        for (Map<String, Object> item : zkbPartOrderDataList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER, keyRow, dataRow++, item);
        }
        dataRow = 2;
        for (Map<String, Object> item : zkbPartOrderItemDataList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER_ITEM, keyRow, dataRow++, item);
        }
        dataRow = 2;
        for (Map<String, Object> item : zkbPartOrderCatDataList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER_CAT, keyRow, dataRow++, item);
        }
        dataRow = 2;
        for (Map<String, Object> item : zkbPartOrderReturnDataList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER_RETURN, keyRow, dataRow++, item);
        }
        dataRow = 2;
        for (Map<String, Object> item : zkbPartOrderReportDataList) {
            doc.writeRowData(SHEET_ZKB_PART_ORDER_REPORT, keyRow, dataRow++, item);
        }
    }

}
