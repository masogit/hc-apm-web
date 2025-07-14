package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbPartOrderCatRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.ZkbIntegralOrder;
import com.ge.apm.domain.ZkbPartOrderCat;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZkbIntegralOrderExcelService {


    public static final String SHEET_TOOL_BOX = "工具箱";
    public static final String SHEET_UN_TOOL_BOX = "非工具箱";

    @Autowired
    TenantInfoRepository tenantInfoRepository;

    @Autowired
    ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    public HashMap<String,Object> createData(List<ZkbIntegralOrder> toolBoxLIst, List<ZkbIntegralOrder> unToolBoxLIst){
        HashMap<String, Object> dbMap = new HashMap<>();
        List<Map<String, Object>> tbMapList = new ArrayList<>();
        for(ZkbIntegralOrder i : toolBoxLIst){
            Map<String, Object> tbMap = getToolBoxMap(i);
            tbMapList.add(tbMap);
        }

        List<Map<String, Object>> utbMapList = new ArrayList<>();
        for(ZkbIntegralOrder i : unToolBoxLIst){
            Map<String, Object> tbMap = getUnToolBoxMap(i);
            utbMapList.add(tbMap);
        }
        dbMap.put("toolBoxList",tbMapList);
        dbMap.put("unToolBoxList",utbMapList);
        return dbMap;
    }

    public Map<String, Object> getToolBoxMap(ZkbIntegralOrder i){
        Map<String, Object> map = new HashMap<>(50);
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(i.getTenantUID());
        if(tenantInfo != null){
            map.put("代理商", tenantInfo.getName());
        }
        map.put("申请编号", i.getIntegralOrderNo());
        map.put("当前状态",  WebUtil.getFieldValueMessage("zkbIntegralOrderStatus", i.getStatus()));
        map.put("当前步骤",  WebUtil.getFieldValueMessage("zkbIntegralOrderSteps", i.getCurrentStep()));        map.put("申请人", i.getRequestUser());
        map.put("申请人电话", i.getRequestPhone());
        map.put("申请时间", DateUtil.format(i.getRequestDate(), "yyyy-MM-dd HH:mm:ss"));
        ZkbPartOrderCat zkbPartOrderCat = zkbPartOrderCatRepository.findById(i.getCatId());
        if(zkbPartOrderCat != null){
            map.put("工具箱号", zkbPartOrderCat.getCatNo());
        }
        map.put("合同号", i.getContractNo());
        map.put("数量", i.getQty());
        map.put("工程师姓名", i.getZkbEngineerName());
        map.put("工程师电话", i.getZkbEngineerPhone());
        map.put("工程师学员号", i.getEngineerNo());
        map.put("收货人", i.getReceiver());
        map.put("收货人电话", i.getReceiverPhone());
        map.put("收货地址", i.getReceiverAddress());
        map.put("备注", i.getRemark());
        map.put("工程师是否有资质", WebUtil.getFieldValueMessage("zkbCommonYOrN", i.getEngineerQualificationsFlag()));
        map.put("工程师资质审批人", i.getEngineerAuditer());
        map.put("工程师资质审批时间", DateUtil.format(i.getEngineerAuditDate(), "yyyy-MM-dd HH:mm:ss"));
        map.put("工程师资质审批备注", i.getEngineerRemark());
        map.put("Order#", i.getOrderNo());
        map.put("下单审批人", i.getOrderedAuditer());
        map.put("下单审批时间", DateUtil.format(i.getOrderedDate(), "yyyy-MM-dd HH:mm:ss"));
        map.put("下单审批备注", i.getOrderedRemark());

        return map;
    }

    public Map<String, Object> getUnToolBoxMap(ZkbIntegralOrder i) {
        Map<String, Object> map = new HashMap<>(50);
        TenantInfo tenantInfo = tenantInfoRepository.findByuid(i.getTenantUID());
        if (tenantInfo != null) {
            map.put("代理商", tenantInfo.getName());
        }
        map.put("工单号", i.getSrNum());
        map.put("申请编号", i.getIntegralOrderNo());
        map.put("当前状态",  WebUtil.getFieldValueMessage("zkbIntegralOrderStatus", i.getStatus()));
        map.put("当前步骤",  WebUtil.getFieldValueMessage("zkbIntegralOrderSteps", i.getCurrentStep()));
        if(i.getAssetInfo() != null){
            map.put("Sys ID", i.getAssetInfo().getSystemId());
            map.put("设备名称", i.getAssetInfo().getName());
        }
        map.put("订单类型", WebUtil.getFieldValueMessage("zkbIntegralOrderType", i.getOrderType()));
        map.put("申请人", i.getRequestUser());
        map.put("申请人电话", i.getRequestPhone());
        map.put("申请时间", DateUtil.format(i.getRequestDate(), "yyyy-MM-dd HH:mm:ss"));
        map.put("医院名称", i.getHospitalName());
        map.put("医院报修人姓名", i.getHospitalRepairer());
        map.put("医院报修人电话", i.getHospitalRepairerPhone());
        map.put("是否为紧急订单", WebUtil.getFieldValueMessage("zkbCommonYOrN", i.getUrgentOrder()));
        if(ZkbConstantUtil.T.equals(i.getUrgentOrder())){
            map.put("紧急原因", WebUtil.getFieldValueMessage("zkbUrgentReasonType", i.getUrgencyReasons()));
        }
        map.put("订单数量", i.getQty());
        map.put("ZKB工程师姓名", i.getZkbEngineerName());
        map.put("ZKB工程师电话", i.getZkbEngineerPhone());
        map.put("产品型号", i.getProductNumber());
        map.put("故障描述", i.getFaultDescription());
        map.put("备注", i.getRemark());
        map.put("区域经理审批人", i.getManageAuditer());
        map.put("区域经理审批时间", i.getManageAuditDate());
        map.put("区域经理审批备注", i.getManageRemark());
        map.put("SR#", i.getSrNo());
        map.put("Act#", i.getActivityNo());
        map.put("GE工程师姓名", i.getGeEngineerName());
        map.put("GE工程师电话", i.getGeEngineerPhone());
        map.put("是否扣除积分", i.getWhetherDeduct());
        map.put("下单审批人", i.getOrderedAuditer());
        map.put("下单审批时间", i.getOrderedDate());
        map.put("下单审批备注", i.getOrderedRemark());
        return map;
    }

    public void exportData(ExcelDocument doc, HashMap<String, Object> dataMap) {
        Integer keyRow = 1;
        Integer dataRow = 2;

        List<Map<String, Object>> toolBoxLIst = (List<Map<String, Object>>)dataMap.get("toolBoxList");
        List<Map<String, Object>> unToolBoxList = (List<Map<String, Object>>)dataMap.get("unToolBoxList");

        for (Map<String, Object> item : toolBoxLIst) {
            doc.writeRowData(SHEET_TOOL_BOX, keyRow, dataRow++, item);
        }

        dataRow = 2;
        for (Map<String, Object> item : unToolBoxList) {
            doc.writeRowData(SHEET_UN_TOOL_BOX, keyRow, dataRow++, item);
        }
    }

}
