package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MicroServiceInvoker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 503202755 2020/12/8
 */
@Component
public class ZkbExportOrderService extends MicroServiceInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ZkbExportOrderService.class);

    /**
     * 归还类型
     */
    private static final String zkbPoReturnType="zkbFePartOrder_zkbPoReturnType";

    /**
     * GEFE
     */
    private static final String GEFE = "GEFE";

    /**
     * FE
     */
    private static final String FE = "ZKB FE";

    /**
     * FE
     */
    private static final String orderHistory = "ORDER HISTORY LIST";

    /**
     * 归还数量
     */
    @Autowired
    ZkbFePartOrderReturnRepository zkbFePartOrderReturnRepository;

    /**
     * 归还信息
     */
    @Autowired
    ZkbPartOrderReturnRepository zkbPartOrderReturnRepository;


    @Autowired
    ZkbFePartOrderService zkbFePartOrderService;

    /**
     * 备件申请备件信息service
     */
    @Autowired
    private ZkbFePartOrderAssetInfoRepository zkbFePartOrderAssetInfoRepository;

    /**
     * 配件/工具申请明细
     */
    @Autowired
    private ZkbPartOrderItemRepository zkbPartOrderItemRepository;

    /**
     * 人工申请主表repository
     */
    @Autowired
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * FE订单
     */
    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;



    /**
     * 订单类型
     */
    private static final String zkbPartOrderType = "zkbPartOrderType";

    /**
     * 当前状态
     */
    private static final String zkbPoStatus = "zkbPoStatus";


    /**
     * 是 否
     */
    private static final String yesOrNo = "zkbFePartOrder_yesOrNo";

    /**
     * 紧急 不紧急
     */
    private static final String emergency = "zkbFePartOrder_emergency";

    /**
     * 订单标注
     */
    private static final String orderAnnotation ="zkbPoOrderAnnotationType";

    /**
     * 订单紧急的原因
     */
    private static final String zkbUrgentReasonType = "zkbUrgentReasonType";

    /**
     * 扣除名额类型
     */
    private static final String zkbPoProbeQuotaType = "ZkbPoProbeQuotaType";

    /**
     * 价值类型
     */
    private static final String zkbPoValueType = "zkbPoValueType";

    /**
     * 是否需要探头说明函
     */
    private static final String needLetterFlag = "zkbFePartOrder_needLetterFlag";

    /**
     * FE归还类型
     */
    private static final String zkbFePoReturnType = "zkbPoReturnType";

    /**
     * 审核备注
     */
    private static final String zkbPoReturnDataStatus = "zkbPoReturnDataStatus";

    /**
     * 导出fepartorder
     * @param zkbFePartOrder 需要导出的数据
     * @param i18Map 字典
     * @param contractMap 合同信息
     * @param tenantInfoMap 租户信息
     * @return
     */
    public Map<String,Object> getFePartOrderHashMap(ZkbFePartOrder zkbFePartOrder,
                                                    Map<String,Map<String,String>> i18Map,
                                                    Map<String, ZkbContract> contractMap,
                                                    Map<String, TenantInfo> tenantInfoMap)
    {
        Map<String,Object> map = new HashMap();
        if(zkbFePartOrder==null){
            return map;
        }
        this.zkbFePartOrderService.fullContractAndTenantInfo(contractMap,tenantInfoMap,map,zkbFePartOrder.getContractId(),zkbFePartOrder.getTenantUid());
        map.put(WebUtil.getMessage("zkbFePartOrder_systemId"),zkbFePartOrder.getSystemId());
        map.put(WebUtil.getMessage("zkbFePartOrder_orderNo"),zkbFePartOrder.getPartOrderNo());

        if(i18Map.containsKey(ZkbFePartOrderService.gefeOrderType)&&i18Map.get(ZkbFePartOrderService.gefeOrderType).containsKey(zkbFePartOrder.getOrderType()))
            map.put(WebUtil.getMessage("zkbFePartOrder_orderTypeName"),i18Map.get(ZkbFePartOrderService.gefeOrderType).get(zkbFePartOrder.getOrderType()));

        if(ZkbFePartOrder.OrderType.ManMade.toString().equals(zkbFePartOrder.getOrderType())
                &&ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString().equals(zkbFePartOrder.getStatus()))
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"),"400已开单");
        }else
            {
                if(i18Map.containsKey(ZkbFePartOrderService.status)&&i18Map.get(ZkbFePartOrderService.status).containsKey(zkbFePartOrder.getStatus()))
                    map.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"),i18Map.get(ZkbFePartOrderService.status).get(zkbFePartOrder.getStatus()));
            }
        map.put(WebUtil.getMessage("zkbFePartOrder_reportUser"),zkbFePartOrder.getReportUser());
        map.put("申请时间",this.zkbFePartOrderService.dateFormat(zkbFePartOrder.getReportDate(),ZkbFePartOrderService.dateFormat));
        map.put(WebUtil.getMessage("zkbFePartOrder_functionType"),zkbFePartOrder.getFunctionType());

        if(i18Map.containsKey(ZkbFePartOrderService.machineStatus)&&i18Map.get(ZkbFePartOrderService.machineStatus).containsKey(zkbFePartOrder.getMachineStatus()))
            map.put(WebUtil.getMessage("zkbFePartOrder_machineStatus"),i18Map.get(ZkbFePartOrderService.machineStatus).get(zkbFePartOrder.getMachineStatus()));
        if(i18Map.containsKey(ZkbFePartOrderService.urgencyStatus)&&i18Map.get(ZkbFePartOrderService.urgencyStatus ).containsKey(zkbFePartOrder.getUrgencyStatus()))
            map.put(WebUtil.getMessage("zkbFePartOrder_urgencyStatus"),i18Map.get(ZkbFePartOrderService.urgencyStatus).get(zkbFePartOrder.getUrgencyStatus()));
        if(i18Map.containsKey(ZkbFePartOrderService.subtractTotalStatus)&&i18Map.get(ZkbFePartOrderService.subtractTotalStatus ).containsKey(zkbFePartOrder.getSubtractTotalStatus()))
            map.put(WebUtil.getMessage("zkbFePartOrder_subtractTotalStatus"),i18Map.get(ZkbFePartOrderService.subtractTotalStatus).get(zkbFePartOrder.getSubtractTotalStatus()));
        map.put(WebUtil.getMessage("zkbFePartOrder_hospitalName"),zkbFePartOrder.getHospitalName());
        map.put(WebUtil.getMessage("zkbFePartOrder_hospitalClientName"),zkbFePartOrder.getHospitalClientName());
        map.put(WebUtil.getMessage("zkbFePartOrder_hospitalClientPhone"),zkbFePartOrder.getHospitalClientPhone());
        map.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"),zkbFePartOrder.getPhenomenon());
        map.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"),zkbFePartOrder.getTenantAssignUser());
        map.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"),this.zkbFePartOrderService.dateFormat(zkbFePartOrder.getTenantAssignDate(),ZkbFePartOrderService.dateFormat));
        map.put(WebUtil.getMessage("zkbFePartOrder_comment"),zkbFePartOrder.getComment());
        map.put(WebUtil.getMessage("zkbFePartOrder_sr"),zkbFePartOrder.getSr());
        map.put(WebUtil.getMessage("zkbFePartOrder_act"),zkbFePartOrder.getAct());
        map.put(WebUtil.getMessage("zkbFePartOrder_geEngineerName"),zkbFePartOrder.getGeEngineerName());
        map.put(WebUtil.getMessage("zkbFePartOrder_geEngineerPhone"),zkbFePartOrder.getGeEngineerPhone());
        map.put(WebUtil.getMessage("zkbFePartOrder_fourHundredComment"),zkbFePartOrder.getRemark());
        if(i18Map.containsKey(ZkbFePartOrderService.sparePartsOrderAnnotation)&&i18Map.get(ZkbFePartOrderService.sparePartsOrderAnnotation).containsKey(zkbFePartOrder.getOrderAnnotation()))
            map.put(WebUtil.getMessage("zkbFePartOrder_orderAnnotation"),i18Map.get(ZkbFePartOrderService.sparePartsOrderAnnotation).get(zkbFePartOrder.getOrderAnnotation()));
        map.put(WebUtil.getMessage("zkbFePartOrder_problemOrderNumber"),zkbFePartOrder.getProblemOrderNumber());
        if(i18Map.containsKey(ZkbFePartOrderService.zkbUrgentReasonType)&&i18Map.get(ZkbFePartOrderService.zkbUrgentReasonType).containsKey(zkbFePartOrder.getUrgencyReasons()))
            map.put(WebUtil.getMessage("zkbFePartOrder_urgencyReasons"),i18Map.get(ZkbFePartOrderService.zkbUrgentReasonType).get(zkbFePartOrder.getUrgencyReasons()));
        map.put(WebUtil.getMessage("zkbFePartOrder_catNo"),zkbFePartOrder.getCatNo());
        if(zkbFePartOrder.getZkbPartOrderCat()!=null)
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_zkbPartOrderCat"),zkbFePartOrder.getZkbPartOrderCat().getCatModel());
            map.put(WebUtil.getMessage("zkbFePartOrder_valueType"),zkbFePartOrder.getZkbPartOrderCat().getValueType());
        }
        map.put(WebUtil.getMessage("zkbFePartOrder_oldCatSn"),zkbFePartOrder.getOldCatSn());
        map.put(WebUtil.getMessage("zkbFePartOrder_assetQty"),zkbFePartOrder.getCatNum());
        map.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"),zkbFePartOrder.getOrderNo());
        map.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"),zkbFePartOrder.getPrice());
        if(i18Map.containsKey(ZkbFePartOrderService.yesOrNo)&&i18Map.get(ZkbFePartOrderService.yesOrNo).containsKey(zkbFePartOrder.getNewCatFlag()))
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_newProbe")+"(代理商)",i18Map.get(ZkbFePartOrderService.yesOrNo).get(zkbFePartOrder.getNewCatFlag()));
        }
        if(i18Map.containsKey(ZkbFePartOrderService.yesOrNo)&&i18Map.get(ZkbFePartOrderService.yesOrNo).containsKey(zkbFePartOrder.getSecondNewCatFlag()))
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_newProbe")+"(400)",i18Map.get(ZkbFePartOrderService.yesOrNo).get(zkbFePartOrder.getSecondNewCatFlag()));
        }
        if(ZkbFePartOrder.OrderType.Probe.toString().equals(zkbFePartOrder.getOrderType())
                &&i18Map.containsKey(ZkbFePartOrderService.needLetterFlag)
                &&i18Map.get(ZkbFePartOrderService.needLetterFlag).containsKey(zkbFePartOrder.getNeedLetterFlag()))
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_needProbeLetter"),i18Map.get(ZkbFePartOrderService.needLetterFlag).get(zkbFePartOrder.getNeedLetterFlag()));
        }
        map.put(WebUtil.getMessage("zkbFePartOrder_orderedPerson"),zkbFePartOrder.getFullApmInfoUser());
        map.put(WebUtil.getMessage("zkbFePartOrder_orderedDate"),this.zkbFePartOrderService.dateFormat(zkbFePartOrder.getFullApmInfoDate(),ZkbFePartOrderService.dateFormat));
        if(i18Map.containsKey(zkbPoReturnType)&&i18Map.get(zkbPoReturnType).containsKey(zkbFePartOrder.getReturnType()))
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_returnType"),i18Map.get(zkbPoReturnType).get(zkbFePartOrder.getReturnType()));
        }
        map.put(WebUtil.getMessage("zkbFePartOrder_partSerialNumber"),zkbFePartOrder.getPartSerialNumber());
        if(i18Map.containsKey(ZkbFePartOrderService.partQualityIssue)&&i18Map.get(ZkbFePartOrderService.partQualityIssue).containsKey(zkbFePartOrder.getPartQualityIssue()))
        {
            map.put(WebUtil.getMessage("zkbFePartOrder_partQualityIssue"),i18Map.get(ZkbFePartOrderService.partQualityIssue).get(zkbFePartOrder.getPartQualityIssue()));
        }
        map.put(WebUtil.getMessage("zkbFePartOrder_airWayBillNo"),zkbFePartOrder.getWaybillNo());
        map.put(WebUtil.getMessage("zkbFePartOrder_returnDate"),this.zkbFePartOrderService.dateFormat(zkbFePartOrder.getReturnDate(),ZkbFePartOrderService.dateFormat));
        map.put(WebUtil.getMessage("zkbFePartOrder_newCatSn"),zkbFePartOrder.getNewCatSn());
        map.put("归还"+WebUtil.getMessage("zkbFePartOrder_remark"),zkbFePartOrder.getGeAdminComment());
        return map;
    }

    /**
     * @param zkbPartOrder
     * @param i18Map
     * @param contractMap
     * @param tenantInfoMap
     * @return
     */
    public Map<String,Object> getPartOrderHashMap(ZkbPartOrder zkbPartOrder,
                                                  Map<String,Map<String,String>> i18Map,
                                                  Map<String, ZkbContract> contractMap,
                                                  Map<String, TenantInfo> tenantInfoMap)
    {
        Map<String,Object> map = new HashMap();
        if(zkbPartOrder==null){
            return map;
        }
        this.zkbFePartOrderService.fullContractAndTenantInfo(contractMap,tenantInfoMap,map,zkbPartOrder.getContractId(),zkbPartOrder.getTenantUID());
        if(i18Map.containsKey(zkbPartOrderType)&&i18Map.get(zkbPartOrderType).containsKey(zkbPartOrder.getOrderType()))
            map.put("订单类型",i18Map.get(zkbPartOrderType).get(zkbPartOrder.getOrderType()));
        map.put("申请号",zkbPartOrder.getPartOrderNo());
        if(i18Map.containsKey(zkbPoStatus)&&i18Map.get(zkbPoStatus).containsKey(zkbPartOrder.getStatus()))
            map.put("当前状态",i18Map.get(zkbPoStatus).get(zkbPartOrder.getStatus()));
        map.put("System ID",zkbPartOrder.getAssetInfo().getSystemId());
        map.put("报修单号",zkbPartOrder.getSrNum());
        map.put("申请人",zkbPartOrder.getRequestUser());
        map.put("申请人电话",zkbPartOrder.getRequestPhone());
        map.put("申请时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getRequestDate(),ZkbFePartOrderService.dateFormat));
        map.put("设备类型（DI）",zkbPartOrder.getAssetTypeDi());
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getScoreFlag()))
            map.put("是否使用积分抵扣",i18Map.get(yesOrNo).get(zkbPartOrder.getScoreFlag()));
        if("2".equals(zkbPartOrder.getArtificialType()))
            map.put("机型","超声高端机");
        else if("3".equals(zkbPartOrder.getArtificialType()))
            map.put("机型","超声低端机");
        if(i18Map.containsKey(zkbPoProbeQuotaType)&&i18Map.get(zkbPoProbeQuotaType).containsKey(zkbPartOrder.getProbeQuotaType()))
            map.put("扣除名额类型",i18Map.get(zkbPoProbeQuotaType).get(zkbPartOrder.getProbeQuotaType()));
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getHighCatFlag()))
            map.put("是否高值探头",i18Map.get(yesOrNo).get(zkbPartOrder.getHighCatFlag()));
        map.put("医院名称",zkbPartOrder.getHospitalName());
        map.put("医院报修人姓名",zkbPartOrder.getHospitalRepairer());
        map.put("医院报修人电话",zkbPartOrder.getHospitalRepairerPhone());
        if(i18Map.containsKey(orderAnnotation)&&i18Map.get(orderAnnotation).containsKey(zkbPartOrder.getOrderAnnotation()))
            map.put("订单标注",i18Map.get(orderAnnotation).get(zkbPartOrder.getOrderAnnotation()));
        map.put("发生问题的order number",zkbPartOrder.getProblemOrderNumber());
        if(i18Map.containsKey(emergency)&&i18Map.get(emergency).containsKey(zkbPartOrder.getUrgentOrder()))
            map.put("是否为紧急订单",i18Map.get(emergency).get(zkbPartOrder.getUrgentOrder()));
        if(i18Map.containsKey(zkbUrgentReasonType)&&i18Map.get(zkbUrgentReasonType).containsKey(zkbPartOrder.getUrgencyReasons()))
            map.put("订单紧急的原因",i18Map.get(zkbUrgentReasonType).get(zkbPartOrder.getUrgencyReasons()));
        map.put("订单数量/订单名额",zkbPartOrder.getQty());
        map.put("ZKB工程师姓名",zkbPartOrder.getZkbEngineerName());
        map.put("ZKB工程师电话",zkbPartOrder.getZkbEngineerPhone());
        map.put("收货人",zkbPartOrder.getReceiver());
        map.put("收货人电话",zkbPartOrder.getReceiverPhone());
        map.put("收货地址",zkbPartOrder.getReceiverAddress());
        map.put("备注",zkbPartOrder.getRemark());
        map.put("产品型号",zkbPartOrder.getProductNumber());
        map.put("软件版本",zkbPartOrder.getSoftwareVersion());
        map.put("故障描述",zkbPartOrder.getFaultDescription());
        map.put("探头号",zkbPartOrder.getCatNo());
        if(zkbPartOrder.getZkbPartOrderCat()!=null&&StringUtils.isNotBlank(zkbPartOrder.getZkbPartOrderCat().getCatText()))
        {
            map.put("探头型号/SN编号",zkbPartOrder.getZkbPartOrderCat().getCatText());
        }else if("fourToOne".equals(zkbPartOrder.getContractCatId()))
        {
            map.put("探头型号/SN编号","四合一");
        }
        else if(StringUtils.isNotBlank(zkbPartOrder.getContractCatId()) )
        {
            ZkbContractCat zkbContractCat = this.zkbContractCatRepository.findById(zkbPartOrder.getContractCatId());
            if(zkbContractCat!=null){
                map.put("探头型号/SN编号",zkbContractCat.getSnNo());
            }
        }
        if(i18Map.containsKey(zkbPoValueType)&&zkbPartOrder.getZkbPartOrderCat()!=null&&i18Map.get(zkbPoValueType).containsKey(zkbPartOrder.getZkbPartOrderCat().getValueType()))
            map.put("价值类型",i18Map.get(zkbPoValueType).get(zkbPartOrder.getZkbPartOrderCat().getValueType()));
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getTenantNewCatFlag()))
            map.put("是否全新探头",i18Map.get(yesOrNo).get(zkbPartOrder.getTenantNewCatFlag()));
        map.put("旧探头SN编号",zkbPartOrder.getOldCatSn());
        map.put("探头数量",zkbPartOrder.getCatNum());
        map.put("技术支持审批人",zkbPartOrder.getTechAuditer());
        map.put("技术支持审批时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getTechAuditDate(),ZkbFePartOrderService.dateFormat));
        map.put("技术支持审批备注",zkbPartOrder.getTechRemark());
        map.put("区域经理审批人",zkbPartOrder.getManageAuditer());
        map.put("区域经理审批时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getManageAuditDate(),ZkbFePartOrderService.dateFormat));
        if(i18Map.containsKey(zkbUrgentReasonType)&&i18Map.get(zkbUrgentReasonType).containsKey(zkbPartOrder.getUrgencyReasons()))
            map.put("订单紧急的原因",i18Map.get(zkbUrgentReasonType).get(zkbPartOrder.getUrgencyReasons()));
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getHaveTrainCourseFlag()))
            map.put("是否上过培训课",i18Map.get(yesOrNo).get(zkbPartOrder.getHaveTrainCourseFlag()));
        map.put("区域经理审批备注",zkbPartOrder.getManageRemark());
        map.put("销售总监审批人",zkbPartOrder.getDirectorAuditer());
        map.put("销售总监审批时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getDirectorAuditDate(),ZkbFePartOrderService.dateFormat));
        map.put("销售总监审批备注",zkbPartOrder.getDirectorRemark());
        map.put("SR#",zkbPartOrder.getSrNo());
        map.put("Act#",zkbPartOrder.getActivityNo());
        map.put("Order#",zkbPartOrder.getOrderNo());
        map.put("价格",zkbPartOrder.getPrice());
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getNewCatFlag()))
            map.put("是否全新探头",i18Map.get(yesOrNo).get(zkbPartOrder.getNewCatFlag()));
        if(i18Map.containsKey(needLetterFlag)&&i18Map.get(needLetterFlag).containsKey(zkbPartOrder.getNeedLetterFlag()))
            map.put("是否需要探头说明函",i18Map.get(needLetterFlag).get(zkbPartOrder.getNeedLetterFlag()));
        map.put("GE工程师姓名",zkbPartOrder.getGeEngineerName());
        map.put("GE工程师电话",zkbPartOrder.getGeEngineerPhone());
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getWhetherDeduct()))
            map.put("是否扣除名额",i18Map.get(yesOrNo).get(zkbPartOrder.getWhetherDeduct()));
        map.put("开单审批人",zkbPartOrder.getUnOrderAuditer());
        map.put("开单审批时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getUnOrderDate(),ZkbFePartOrderService.dateFormat));
        map.put("下单审批人",zkbPartOrder.getOrderedAuditer());
        map.put("下单审批时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getOrderedDate(),ZkbFePartOrderService.dateFormat));
        if(StringUtils.isNotBlank(zkbPartOrder.getOrderedRemark()))
            map.put("拒绝原因",zkbPartOrder.getOrderedRemark());
        else if(StringUtils.isNotBlank(zkbPartOrder.getUnOrderRemark()))
            map.put("拒绝原因",zkbPartOrder.getUnOrderRemark());
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbPartOrder.getWhetherDeduct()))
            map.put("是否扣除名额",i18Map.get(yesOrNo).get(zkbPartOrder.getWhetherDeduct()));
        if(i18Map.containsKey(zkbFePoReturnType)&&i18Map.get(zkbFePoReturnType).containsKey(zkbPartOrder.getReturnType()))
            map.put("归还类型",i18Map.get(zkbFePoReturnType).get(zkbPartOrder.getReturnType()));
        map.put("Part Serial Number",zkbPartOrder.getPartSerialNumber());
        map.put("归还故障描述",zkbPartOrder.getPartQualityIssue());
        map.put("归还数量",zkbPartOrder.getReturnQty());
        map.put("运单号",zkbPartOrder.getWaybillNo());
        map.put("归还日期",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getReturnDate(),ZkbFePartOrderService.dateFormat));
        map.put("新探头SN编号",zkbPartOrder.getNewCatSn());
        map.put("归还备注",zkbPartOrder.getReturnRemark());
        map.put("关单审批人",zkbPartOrder.getClosedAuditer());
        map.put("关单审批时间",this.zkbFePartOrderService.dateFormat(zkbPartOrder.getClosedDate(),ZkbFePartOrderService.dateFormat));
        map.put("关单审批备注",zkbPartOrder.getClosedRemark());
        return map;
    }


    /**
     *
     * @param zkbPartOrder 当前需要导出的工单信息
     * @param map
     * @return
     */
    public List<Map<String,Object>> getPartOrderHashMapBySparePartsAssetInfo(ZkbPartOrder zkbPartOrder, Map<String,Object> map, Map<String,Map<String,String>> i18Map)
    {
        List<Map<String,Object>> list = new ArrayList<>();
        if(!(ZkbPartOrder.OrderType.Parts.toString().equals(zkbPartOrder.getOrderType())
                ||ZkbPartOrder.OrderType.Tool.toString().equals(zkbPartOrder.getOrderType())))
            return list;
        List<ZkbPartOrderItem> zkbPartOrderItemList = this.zkbPartOrderItemRepository.findByPartOrderId(zkbPartOrder.getId());
        if(CollectionUtils.isEmpty(zkbPartOrderItemList))
            return list;
        for(ZkbPartOrderItem item:zkbPartOrderItemList)
        {
            Map<String,Object> newMap = map;
            newMap.put("配件号/工具号",item.getPartNo());
            newMap.put("配件名称",item.getPartName());
            newMap.put("配件数量",item.getPrePartQty());
            newMap.put("实际配件数量",item.getPartQty());
            newMap.put("配件SN(序列号)",item.getSerialNumber());
            newMap.put("Order#",item.getOrderNo());
            newMap.put("价格",item.getPrice());
            newMap.put("备注（说明是否DOA或者Short life）",item.getRemark());
            list.add(newMap);
        }
        if(CollectionUtils.isEmpty(list))
            return list;
        List<Map<String,Object>> returnPartlist = new ArrayList<>();
        for(Map<String,Object> item:list)
        {
            if(item.get("配件号/工具号")==null)
                continue;
            List<ZkbPartOrderReturn> zkbPartOrderReturnList = zkbPartOrderReturnRepository.findByPartOrderIdAndPartNo(zkbPartOrder.getId(),item.get("配件号/工具号").toString());
            if(CollectionUtils.isEmpty(zkbPartOrderReturnList))
                continue;
            for(ZkbPartOrderReturn zkbPartOrderReturn:zkbPartOrderReturnList)
            {
                Map<String,Object> newMap = map;
                newMap.put("运单号",zkbPartOrderReturn.getWaybillNo());
                newMap.put("归还日期",this.zkbFePartOrderService.dateFormat(zkbPartOrderReturn.getReturnDate(),ZkbFePartOrderService.dateFormat));
                newMap.put("归还类型",this.zkbFePartOrderService.dateFormat(zkbPartOrderReturn.getReturnDate(),ZkbFePartOrderService.dateFormat));
                if(i18Map.containsKey(zkbFePoReturnType)&&i18Map.get(zkbFePoReturnType).containsKey(zkbPartOrderReturn.getReturnType()))
                    map.put("归还类型",i18Map.get(zkbFePoReturnType).get(zkbPartOrderReturn.getReturnType()));
                if(i18Map.containsKey(zkbPoReturnDataStatus)&&i18Map.get(zkbPoReturnDataStatus).containsKey(zkbPartOrderReturn.getDataStatus()))
                    map.put("状态",i18Map.get(zkbPoReturnDataStatus).get(zkbPartOrderReturn.getDataStatus()));
                map.put("Part Serial Number",zkbPartOrderReturn.getPartSerialNumber());
                map.put("归还故障描述",zkbPartOrderReturn.getPartQualityIssue());
                map.put("归还数量",zkbPartOrderReturn.getQty());
                returnPartlist.add(newMap);
            }
        }
        return returnPartlist;
    }



    /**
     *
     * @param zkbFePartOrder 当前需要导出的工单信息
     * @param map
     * @return
     */
    public List<Map<String,Object>> getFePartOrderHashMapBySparePartsAssetInfo(ZkbFePartOrder zkbFePartOrder, Map<String,Object> map, Map<String,Map<String,String>> i18Map)
    {
        List<Map<String,Object>> list = new ArrayList<>();
        if(!ZkbFePartOrder.OrderType.SpareParts.toString().equals(zkbFePartOrder.getOrderType()))
            return list;
        List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = this.zkbFePartOrderAssetInfoRepository.findByFePartOrderId(zkbFePartOrder.getId());
        if(CollectionUtils.isEmpty(zkbFePartOrderAssetInfoList))
            return list;
        for(ZkbFePartOrderAssetInfo item:zkbFePartOrderAssetInfoList)
        {
            Map<String,Object> newMap = map;
            newMap.put(WebUtil.getMessage("zkbFePartOrder_assetNo"),item.getAssetNo());
            newMap.put(WebUtil.getMessage("zkbFePartOrder_assetDescription"),item.getAssetDescription());
            newMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"),item.getAssetQty());
            newMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"),item.getOrderNo());
            newMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"),item.getPrice());
            list.add(newMap);
        }
        if(CollectionUtils.isEmpty(list))
            return list;
        List<Map<String,Object>> returnPartlist = new ArrayList<>();
        for(Map<String,Object> item:list)
        {
            if(item.get(WebUtil.getMessage("zkbFePartOrder_assetNo"))==null
                    ||item.get(WebUtil.getMessage("zkbFePartOrder_orderNoa"))==null)
            {
                continue;
            }
            List<ZkbFePartOrderReturn> zkbFePartOrderReturnList = this.zkbFePartOrderReturnRepository.findByPartOrderIdAndAssetNoAndOrderNo(
                    zkbFePartOrder.getId(),
                    item.get(WebUtil.getMessage("zkbFePartOrder_assetNo")).toString(),
                    item.get(WebUtil.getMessage("zkbFePartOrder_orderNoa")).toString());
            if(CollectionUtils.isEmpty(zkbFePartOrderReturnList))
                continue;
            for(ZkbFePartOrderReturn zkbFePartOrderReturn:zkbFePartOrderReturnList)
            {
                Map<String,Object> newMap = map;
                if(i18Map.containsKey(zkbPoReturnType)&&i18Map.get(zkbPoReturnType).containsKey(zkbFePartOrderReturn.getReturnType()))
                {
                    newMap.put(WebUtil.getMessage("zkbFePartOrder_returnType"),i18Map.get(zkbPoReturnType).get(zkbFePartOrderReturn.getReturnType()));
                }
                newMap.put(WebUtil.getMessage("zkbFePartOrder_returnDate"),this.zkbFePartOrderService.dateFormat(zkbFePartOrderReturn.getReturnDate(),ZkbFePartOrderService.dateFormat_month));
                newMap.put("归还"+WebUtil.getMessage("zkbFePartOrder_remark"),zkbFePartOrderReturn.getComment());
                newMap.put("归还"+WebUtil.getMessage("zkbFePartOrder_assetQty"),zkbFePartOrderReturn.getQty());
                returnPartlist.add(newMap);
            }
        }
        return returnPartlist;
    }

    /**
     * 导出
     * @param doc
     * @param fePartOrderList
     */
    public void exportData(ExcelDocument doc, List<Map<String, Object>> fePartOrderList,List<Map<String, Object>> partOrderList,List<Map<String, Object>> orderHistoryMapList)
    {
        Integer keyRow = 1;
        Integer dataRow = 2;
        if(CollectionUtils.isNotEmpty(fePartOrderList))
        {
            for (Map<String, Object> item : fePartOrderList) {
                doc.writeRowData(GEFE, keyRow, dataRow++, item);
            }
        }
         keyRow = 1;
        dataRow = 2;
        if(CollectionUtils.isNotEmpty(partOrderList))
        {
            for (Map<String, Object> item : partOrderList) {
                doc.writeRowData(FE, keyRow, dataRow++, item);
            }
        }
        keyRow = 1;
        dataRow = 2;
        if(CollectionUtils.isNotEmpty(orderHistoryMapList))
        {
            for (Map<String, Object> item : orderHistoryMapList) {
                doc.writeRowData(orderHistory, keyRow, dataRow++, item);
            }
        }
    }

    public List<Map<String, Object>> getZkbFePartOrder(String tenantUid)
    {
        List<Map<String, Object>> fePartOrderList = new ArrayList<>();
        List<ZkbFePartOrder> list = null;
        if(StringUtils.isNotBlank(tenantUid))
            list=this.zkbFePartOrderRepository.findByTenantUidOrderByReportDateDesc(tenantUid);
        else
            list=this.zkbFePartOrderRepository.findAllFePartOrder();
        if(CollectionUtils.isEmpty(list))
            return fePartOrderList;
        Map<String,Map<String,String>> i18Map=this.zkbFePartOrderService.batchI18MapForPartOrder();
        Map<String,ZkbContract> contractMap=this.zkbFePartOrderService.getContractMap(list);
        Map<String,TenantInfo> tenantInfoMap=this.zkbFePartOrderService.getTenantInfoMap(list);
        for(ZkbFePartOrder zkbFePartOrder:list)
        {
            Map<String,Object> map = this.getFePartOrderHashMap(zkbFePartOrder,i18Map,contractMap,tenantInfoMap);
            List<Map<String,Object>> mapListForSingleOrder = this.getFePartOrderHashMapBySparePartsAssetInfo(zkbFePartOrder,map,i18Map);
            if(CollectionUtils.isNotEmpty(mapListForSingleOrder))
            {
                fePartOrderList.addAll(mapListForSingleOrder);
            }else
            {
                fePartOrderList.add(map);
            }
        }
        return fePartOrderList;
    }

    public List<Map<String, Object>> getZkbPartOrder(String tenantUid)
    {
        List<Map<String, Object>> partOrderList = new ArrayList<>();
        List<ZkbPartOrder> list = null;
        if(StringUtils.isNotBlank(tenantUid))
            list=this.zkbPartOrderRepository.findByTenantUIDOrderByRequestDateDesc(tenantUid);
        else
            list=this.zkbPartOrderRepository.findAllPartOrder();
        if(CollectionUtils.isEmpty(list))
            return partOrderList;
        Map<String,Map<String,String>> i18Map=this.batchI18MapForPartOrder();
        Map<String,ZkbContract> contractMap=this.zkbFePartOrderService.getContractMapForZkbPartOrder(list);
        Map<String,TenantInfo> tenantInfoMap=this.zkbFePartOrderService.getTenantInfoMapForZkbPartOrder(list);
        for(ZkbPartOrder zkbPartOrder:list)
        {
            Map<String,Object> map = this.getPartOrderHashMap(zkbPartOrder,i18Map,contractMap,tenantInfoMap);
            List<Map<String,Object>> mapListForSingleOrder = this.getPartOrderHashMapBySparePartsAssetInfo(zkbPartOrder,map,i18Map);
            if(CollectionUtils.isNotEmpty(mapListForSingleOrder))
            {
                partOrderList.addAll(mapListForSingleOrder);
            }else
            {
                partOrderList.add(map);
            }
        }
        return partOrderList;
    }

    public Map<String,Map<String,String>> batchI18MapForPartOrder()
    {
        List<String> typeList = new ArrayList<>();
        typeList.add(zkbPartOrderType);
        typeList.add(zkbPoStatus);
        typeList.add(yesOrNo);
        typeList.add(emergency);
        typeList.add(orderAnnotation);
        typeList.add(zkbUrgentReasonType);
        typeList.add(zkbPoProbeQuotaType);
        typeList.add(zkbPoValueType);
        typeList.add(needLetterFlag);
        typeList.add(zkbFePoReturnType);
        typeList.add(zkbPoReturnDataStatus);
        Map<String,Map<String,String>> i18Map = this.zkbFePartOrderService.getI18ToMap(typeList);
        return i18Map;
    }


}
