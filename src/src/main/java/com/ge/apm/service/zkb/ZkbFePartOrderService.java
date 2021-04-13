package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.ZkbFePartOrderMapper;
import com.ge.apm.dao.mapper.ZkbPartOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 503202755 2020/12/8
 */
@Component
public class ZkbFePartOrderService extends MicroServiceInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFePartOrderService.class);

    @Autowired
    BlobObjectRepository blobObjectRepository;

    /**
     * 人工申请主表repository
     */
    @Autowired
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 获取FE订单
     */
    @Autowired
    private ZkbPartOrderMapper zkbPartOrderMapper;

    /**
     * 获取审批角色配置信息
     */
    @Autowired
    private ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository;

    /**
     * 合同信息
     */
    @Autowired
    private ZkbContractRepository zkbContractRepository;

    /**
     * 获取资产信息
     */
    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private AssetInfoMapper assetInfoMapper;

    /**
     * 根据system_id和用户tenant_uid 查询设备信息
     */
    @Autowired
    private ZkbFePartOrderMapper zkbFePartOrderMapper;

    /**
     * 智康宝人工申请STEP service
     */
    @Autowired
    private ZkbFePartOrderStepService zkbFePartOrderStepService;

    /**
     * 备件申请备件信息service
     */
    @Autowired
    private ZkbFePartOrderAssetInfoRepository zkbFePartOrderAssetInfoRepository;

    /**
     *查询i18 repository
     */
    @Autowired
    private I18nMessageRepository i18nMessageRepository;


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 人工申请单号前缀
     */
    private static final String  ORDER_PREFIX = "FE";

    /**
     * 备件申请单号前缀
     */
    private static final String  SPARE_PARTS_ORDER_PREFIX = "FEPO";

    /**
     * 超声高端机
     */
    public static String highArtificialTypeValue = "High";

    /**
     * 超声类
     */
    public static final Integer assetGroup = 2;

    /**
     * 正常订单
     */
    public static String normalOrderValue = "NormalOrder";

    /**
     * 不紧急值
     */
    public static String urgentOrderFalse = "f";

    /**
     * 人工申请sheet名字
     */
    private static final String excelSheetNameOfManMade = "人工申请";

    /**
     * 探头申请sheet名字
     */
    private static final String excelSheetNameOfProbe = "探头申请";

    /**
     * 备件申请sheet名字
     */
    private static final String excelSheetNameOfSparePart = "备件申请";

    /**
     * 机器状态i18值
     */
    private static final String machineStatus = "zkbFePartOrder_machineStatus";

    /**
     * 紧急类型
     */
    private static final String urgencyStatus = "zkbFePartOrder_casePriority";

    /**
     * 是否扣除名额
     */
    private static final String subtractTotalStatus = "zkbFePartOrder_isSubtractTotalStatus";

    /**
     * 流程状态
     */
    private static final String status = "zkbFePartOrder_status";

    /**
     * 机型
     */
    private static final String artificialType = "zkbFePartOrder_artificialType";

    /**
     * 订单标注
     */
    private static final String sparePartsOrderAnnotation = "zkbFePartOrder_sparePartsOrderAnnotation";

    /**
     * 设备类型（DI）
     */
    private static final String assetTypeDi = "zkbFePartOrder_assetTypeDi";

    /**
     * 订单紧急的原因
     */
    private static final String zkbUrgentReasonType = "zkbUrgentReasonType";

    /**
     * 归还类型
     */
    private static final String zkbPoReturnType="zkbFePartOrder_zkbPoReturnType";

    /**
     * 故障描述
     */
    private static final String partQualityIssue="zkbFePartOrder_partQualityIssue";

    /**
     * 是否需要探头说明函
     */
    private static final String needLetterFlag="zkbFePartOrder_needLetterFlag";

    /**
     * 是还是否
     */
    private static final String yesOrNo="zkbFePartOrder_yesOrNo";

    /**
     * 是否紧急
     */
    private static final String emergency="zkbFePartOrder_emergency";

    /**
     * 日期格式
     */
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 合同超声高端机
     */
    private static final String assetTypeHighMachineValue = "highMachine";

    /**
     * 给申请单添加单号和索引
     * @param zkbFePartOrder
     * @return
     */
    private ZkbFePartOrder fullOrderNo(ZkbFePartOrder zkbFePartOrder,String orderPrefix)
    {
        Object o= zkbFePartOrderRepository.getMaxIndex(orderPrefix+simpleDateFormat.format(new Date())+"%") ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String partOrderNo = orderPrefix+simpleDateFormat.format(new Date())+String.format("%04d",index);
        zkbFePartOrder.setPartOrderNo(partOrderNo);
        zkbFePartOrder.setPartOrderIndex(index);
        return zkbFePartOrder;
    }

    /**
     * 添加新申请单初始化数据
     * @param zkbFePartOrder
     * @return
     */
    public ZkbFePartOrder initNewOrder(ZkbFePartOrder zkbFePartOrder)
    {
        Date date = new Date();
        zkbFePartOrder.setReportDate(date);
        this.fullOrderNo(zkbFePartOrder,ZkbFePartOrderService.ORDER_PREFIX);
        return zkbFePartOrder;
    }

    /**
     * 添加新申请单初始化数据
     * @param zkbFePartOrder
     * @return
     */
    public ZkbFePartOrder initNewSparePartsOrder(ZkbFePartOrder zkbFePartOrder)
    {
        Date date = new Date();
        zkbFePartOrder.setReportDate(date);
        this.fullOrderNo(zkbFePartOrder,ZkbFePartOrderService.SPARE_PARTS_ORDER_PREFIX);
        return zkbFePartOrder;
    }

    /**
     * 根据SystemId获取相关信息
     * @param zkbFePartOrder
     * @param systemId 设备system id
     * @return
     */
    public ZkbFePartOrder fullInfoBySystemId(ZkbFePartOrder zkbFePartOrder,String systemId)
    {
        zkbFePartOrder.setSystemId(systemId);
        List<ZkbFePartOrder> list = this.zkbFePartOrderMapper.loadAssetInfo(systemId);
        if(CollectionUtils.isEmpty(list)) {
            zkbFePartOrder.setContractId(null);
            zkbFePartOrder.setFunctionType(null);
            zkbFePartOrder.setContractId(null);
            zkbFePartOrder.setHospitalName(null);
            zkbFePartOrder.setTenantUid(null);
            zkbFePartOrder.setSubtractTotalStatus(null);
            zkbFePartOrder.setAssetType(null);
            return zkbFePartOrder;
        }
        ZkbFePartOrder item = list.get(0);
        zkbFePartOrder.setContractId(item.getContractId());
        zkbFePartOrder.setFunctionType(item.getFunctionType());
        zkbFePartOrder.setContractId(item.getContractId());
        zkbFePartOrder.setHospitalName(item.getHospitalName());
        zkbFePartOrder.setTenantUid(item.getTenantUid());
        zkbFePartOrder.setAssetType(item.getAssetType());
        if(assetTypeHighMachineValue.equals(zkbFePartOrder.getAssetType()))
        {
            zkbFePartOrder.setSubtractTotalStatus("false");
        }
        return zkbFePartOrder;
    }

    /**
     * 检查合同和systemId是否一致
     * @param zkbFePartOrder 人工申请主表信息
     * @return
     */
    public Boolean checkSystemIdAndContract(ZkbFePartOrder zkbFePartOrder)
    {
        List<ZkbFePartOrder> list =this.zkbFePartOrderMapper.loadAssetInfo(zkbFePartOrder.getSystemId());
        if(CollectionUtils.isEmpty(list)) {
            return false;
        }
        ZkbFePartOrder item = list.get(0);
        if(!item.getContractId().equals(zkbFePartOrder.getContractId()))
        {
            return false;
        }
        return true;
    }

    /**
     * 根据ID发送消息
     * @param id 主键ID
     */
    public void sendMessage(String id,GatewayURL gatewayURL)
    {
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<Map> requestEntity = new HttpEntity<>(header);
        //运用多线程，防止服务不可用，不影响主流程
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //停顿5秒，防止数据未入库
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.info(e.getMessage());
                }
                ResponseEntity<LinkedHashMap> response = template.exchange(  getGatewayURL(gatewayURL)+"?id="+id, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            }
        });
        t.start();
    }

    /**
     * 根据登录账户名获取当前账户名流程配置信息
     * @return
     */
    public Map<String, String> getMapConfigByLoginName(String loginName)
    {
        Map<String, String> configTypeMap = new HashMap<>();
        List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(loginName);
        if(CollectionUtils.isEmpty(list))
        {
            return configTypeMap;
        }
        for(ZkbFePartOrderUserConfig item:list)
        {
            if(item==null) continue;
            if(configTypeMap.containsKey(item.getConfigType())) continue;
            configTypeMap.put(item.getConfigType(),item.getConfigType());
        }
        return configTypeMap;
    }

    /**
     * 判断流程是否在第一节点
     * @param currentStep
     * @return
     */
    public boolean workFlowInFistStep(String currentStep)
    {
        if(ZkbFePartOrder.workFlowSteps.Requested.toString().equals(currentStep))
            return true;
        return false;
    }

    /**
     * 回退列表
     * @param queryStatus 查询状态
     * @return
     */
    public String backList(String queryStatus){
        return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
    }
    /**
     *
     * @param zkbFePartOrder 当前数据
     * @param userUid 用户UID
     * @param userName 用户user name
     * @param tenantUID 用户 tenant UID
     * @param queryStatus 查询状态
     * @param status 流程状态
     * @param currentStep 流程节点
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param gatewayURL 消息发送地址
     * @return
     */
    public String wolkFlowToComplete(ZkbFePartOrder zkbFePartOrder,
                                     String userUid,
                                     String userName,
                                     String tenantUID,
                                     String queryStatus,
                                     String status,
                                     String currentStep,
                                     Date startTime,
                                     Date endTime,
                                     GatewayURL gatewayURL)
    {
        if(ZkbFePartOrder.workFlowSteps.Requested.toString().equals(zkbFePartOrder.getCurrentStep()))
        {
            List<ZkbFePartOrder> listZkbFePartOrder = this.zkbFePartOrderRepository.findByPartOrderNo(zkbFePartOrder.getPartOrderNo());
            if( CollectionUtils.isNotEmpty(listZkbFePartOrder))
            {
                String orderPrefix = "";
                if(zkbFePartOrder.getOrderType().equals(ZkbFePartOrder.OrderType.ManMade))
                {
                    orderPrefix=ORDER_PREFIX;
                }else
                {
                    orderPrefix=SPARE_PARTS_ORDER_PREFIX;
                }
                this.fullOrderNo(zkbFePartOrder,orderPrefix);
            }
        }
        zkbFePartOrder.setStatus(status);
        zkbFePartOrder.setCurrentStep(currentStep);
        this.zkbFePartOrderRepository.save(zkbFePartOrder);
        this.zkbFePartOrderStepService.updateLatestStepEndTime(zkbFePartOrder.getId(),startTime);
        this.zkbFePartOrderStepService.addNewItem(
                zkbFePartOrder.getId(),
                currentStep,
                userUid,userName,
                startTime,
                endTime,
                status,
                tenantUID);
        if(gatewayURL!=null)
        {
            this.sendMessage(zkbFePartOrder.getId(),gatewayURL);
        }

        return this.backList(queryStatus);
    }

    /**
     * 加载合同信息
     */
    public List<ZkbContract> loadZkbContractInfo(String contractId)
    {
        List<ZkbContract> list = new ArrayList<>();
        if(StringUtils.isBlank(contractId)) {
            return list;
        }
        ZkbContract item = this.zkbContractRepository.findById(contractId);
        list.add(item);
        return list;
    }
    /**
     * @param zkbContract 合同信息
     * @param type 1.把剩余名额移入申请名额 2.把申请名额移入已使用名额 3.把申请名额移入剩余名额
     * @param zkbFePartOrder 流程申请数据
     */
    public void moveRemianTimeOfContract(ZkbContract zkbContract,Integer type,ZkbFePartOrder zkbFePartOrder)
    {

        if(!"true".equals(zkbFePartOrder.getSubtractTotalStatus())) return;
        if("DI".equals(zkbContract.getContractType())
                ||"PCB".equals(zkbContract.getContractType()))
        {
            switch(type){
                case 1 :
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()-1);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()+1);
                    break;
                case 2 :
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()-1);
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse()+1);
                    break;
                case 3:
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()+1);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()-1);
                    break;
            }
        }else if("CCS".equals(zkbContract.getContractType())
                ||"Biomed".equals(zkbContract.getContractType()))
        {
            switch(type){
                case 1 :
                    zkbContract.setGeArtificialSupportTimes(zkbContract.getGeArtificialSupportTimes()-1);
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply()+1);
                    break;
                case 2 :
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply()-1);
                    zkbContract.setGeArtificialSupportTimesUse(zkbContract.getGeArtificialSupportTimesUse()+1);
                    break;
                case 3:
                    zkbContract.setGeArtificialSupportTimes(zkbContract.getGeArtificialSupportTimes()+1);
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply()-1);
                    break;
            }
        }
        this.zkbContractRepository.save(zkbContract);
    }

    /**
     * 移动备件名额
     * @param type 移动类型
     * @param zkbContract 合同
     * @param qty 数量
     */
    public void moveSpareQuota(Integer type,ZkbContract zkbContract,Integer qty)
    {
        if("DI".equals(zkbContract.getContractType())
                ||"PCB".equals(zkbContract.getContractType()))
        {
            switch(type){
                case 1 :
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()-qty);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()+qty);
                    break;
                case 2 :
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()-qty);
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse()+qty);
                    break;
                case 3:
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse()-qty);
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()+qty);
                    break;
                case 4 :
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()+qty);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()-qty);
                    break;
            }
        }else if("CCS".equals(zkbContract.getContractType())
                ||"Biomed".equals(zkbContract.getContractType()))
        {
            switch(type){
                case 1 :
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota()-qty);
                    zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaApply()+qty);
                    break;
                case 2 :
                    zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaApply()-qty);
                    zkbContract.setSpareQuotaUse(zkbContract.getSpareQuotaUse()+qty);
                    break;
                case 3:
                    zkbContract.setSpareQuotaUse(zkbContract.getSpareQuotaUse()-qty);
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota()+qty);
                    break;
                case 4 :
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota()+qty);
                    zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaApply()-qty);
                    break;
            }
        }
        this.zkbContractRepository.save(zkbContract);
    }

    /**
     * 移动备件名额
     * @param type 移动类型
     * @param zkbContract 合同
     * @param qty 数量
     */
    public void moveProbe(Integer type,ZkbContract zkbContract,Integer qty)
    {

            switch(type){
                case 1 :
                    //探头剩余名额-1，探头申请名额+1
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota()-qty);
                    zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaApply()+qty);
                    break;
                case 2 :
                    //探头申请名额-1，探头已使用名额+1
                    zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaApply()-qty);
                    zkbContract.setProbeQuotaUse(zkbContract.getProbeQuotaUse()+qty);
                    break;
                case 3 :
                    //探头剩余名额-1 探头已使用名额+1
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota()-qty);
                    zkbContract.setProbeQuotaUse(zkbContract.getProbeQuotaUse()+qty);
                    break;
                case 4 :
                    //探头剩余名额+1，探头申请名额-1
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota()+qty);
                    zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaApply()-qty);
                    break;
                case 5 :
                    //探头已使用名额-1，探头剩余名额+1
                    zkbContract.setProbeQuotaUse(zkbContract.getProbeQuotaUse()-qty);
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota()+qty);
                    break;
            }
            this.zkbContractRepository.save(zkbContract);
    }

    public void sendWeChatRemindLetter(ZkbFePartOrder zkbFePartOrder){
        this.sendMessage(zkbFePartOrder.getId(),GatewayURL.FE_PART_ORDER_SEND_WECHART_REMIND_LETTER);
    }

    /**
     * 根据List获取map
     * @param assetInfoList 申请设备列表
     * @return
     */
    public Map<String,Integer> getAssetInfoMap(List<ZkbFePartOrderAssetInfo> assetInfoList)
    {
        Map<String,Integer> assetInfoMap = new HashMap<>();
        for(ZkbFePartOrderAssetInfo item:assetInfoList)
        {
            String key = item.getAssetNo()+item.getOrderNo();
            if(assetInfoMap.containsKey(key))
            {
                Integer qty = assetInfoMap.get(key);
                assetInfoMap.put(key,qty+item.getAssetQty());
                continue;
            }
            assetInfoMap.put(key,item.getAssetQty());
        }
        return assetInfoMap;
    }

    /**
     * 根据List获取map
     * @param zkbFePartOrderReturnList 返回设备列表
     * @param zkbFePartOrder 流程主表数据
     * @return
     */
    public Map<String,Integer> getZkbFePartOrderReturnMap(List<ZkbFePartOrderReturn> zkbFePartOrderReturnList,ZkbFePartOrder zkbFePartOrder)
    {
        Map<String,Integer> zkbFePartOrderReturnMap = new HashMap<>();
        for(ZkbFePartOrderReturn item:zkbFePartOrderReturnList)
        {
            String key = item.getAssetNo()+item.getOrderNo();
            if(zkbFePartOrderReturnMap.containsKey(key))
            {
                Integer qty = zkbFePartOrderReturnMap.get(key);
                zkbFePartOrderReturnMap.put(key,qty+item.getQty());
                continue;
            }
            zkbFePartOrderReturnMap.put(key,item.getQty());
            item.setPartOrderId(zkbFePartOrder.getId());
        }
        return zkbFePartOrderReturnMap;
    }
    /**
     * 判断是否隐藏还是显示
     * @param fieldsetType
     * @return
     */
    public Boolean checkRendered(String fieldsetType,ZkbFePartOrder zkbFePartOrder,Map<String, String> configTypeMap,String[] stepAndRoles)
    {
        if(StringUtils.isBlank(fieldsetType))
        {
            return false;
        }
        Map<String,Integer> stepMap = this.stepAndRoleMap(stepAndRoles);
        String currentStep = zkbFePartOrder.getCurrentStep();
        Integer fieldsetTypeIndex = stepMap.get(fieldsetType);
        Integer currentStepIndex = stepMap.get(currentStep);
        if(fieldsetTypeIndex<currentStepIndex)
        {
            List<ZkbFePartOrderStep> zkbFePartOrderStep = this.zkbFePartOrderStepService.findByPartOrderIdAndStepKey(zkbFePartOrder.getId(),fieldsetType);
            if(CollectionUtils.isNotEmpty(zkbFePartOrderStep))
            {
                return true;
            }

        }
        if(fieldsetTypeIndex==currentStepIndex)
        {
            String [] stepAndRole = stepAndRoles[fieldsetTypeIndex].split(",");
            if(configTypeMap.containsKey(stepAndRole[1]))
            {
                return true;
            }
        }
        return false;
    }

    private Map<String,Integer> stepAndRoleMap(String[] stepAndRoles)
    {
        Map<String,Integer> stepMap = new HashMap<>();
        for(int i =0;i<stepAndRoles.length;i++)
        {
            String [] stepAndRole =stepAndRoles[i].split(",");
            stepMap.put(stepAndRole[0],i);
        }
        return stepMap;
    }

    /**
     * 判断是否隐藏还是显示
     * @param fieldsetType
     * @return
     */
    public Boolean hasEditRight(String fieldsetType,ZkbFePartOrder zkbFePartOrder,Map<String, String> configTypeMap,String[] stepAndRoles)
    {
        String currentStep = zkbFePartOrder.getCurrentStep();
        if(!currentStep.equals(fieldsetType))
        {
            return false;
        }
        Map<String,Integer> stepMap = this.stepAndRoleMap(stepAndRoles);
        String [] stepAndRole =stepAndRoles[stepMap.get(fieldsetType)].split(",");
        if(configTypeMap.containsKey(stepAndRole[1]))
        {
            return true;
        }
        return false;
    }

    /**
     * 根据角色判断是否有查看探头说明函条件
     * @param configTypeMap 角色MAP
     * @return
     */
    public boolean hasLetterViewRight(Map<String, String> configTypeMap)
    {
        boolean right = false;
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            right=true;
        }else if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString()))
        {
            right=true;
        }
        return right;
    }

    /**
     * 根据角色判断是否有查看探头说明函条件
     * @param configTypeMap 角色MAP
     * @return
     */
    public boolean hasAdminRight(Map<String, String> configTypeMap)
    {
        boolean right = false;
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString()))
        {
            right=true;
        }
        return right;
    }

    /**
     * 根据角色判断是否是供应商权限
     * @param configTypeMap 角色MAP
     * @return
     */
    public boolean hasSupplierRight(Map<String, String> configTypeMap)
    {
        boolean right = false;
        if(configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            right=true;
        }
        return right;
    }

    /**
     * 根据systemId和 报修单号检索
     * @param systemId systemId
     * @param sr 报修单号检索
     * @param partOrderNo 订单编号
     * @param assetGroup 设备类型
     */
    public List<ZkbFePartOrder> getSparePartsZkbFePartOrder(String systemId,String sr,String partOrderNo,Integer assetGroup,String orderType)
    {
        List<ZkbFePartOrder> sparePartsZkbFePartOrderList = null;
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ,ZkbFePartOrder.OrderType.ManMade.toString()));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString()));
        if(StringUtils.isBlank(systemId)&&StringUtils.isBlank(sr)&&StringUtils.isBlank(partOrderNo))
        {
            return sparePartsZkbFePartOrderList;
        }
        if(StringUtils.isNotBlank(systemId))
        {
            searchFilters.add(new SearchFilter("systemId", SearchFilter.Operator.LIKE,systemId));
        }
        if(StringUtils.isNotBlank(sr))
        {
            searchFilters.add(new SearchFilter("sr", SearchFilter.Operator.LIKE,sr));
        }
        if(StringUtils.isNotBlank(partOrderNo))
        {
            searchFilters.add(new SearchFilter("partOrderNo", SearchFilter.Operator.EQ,partOrderNo));
        }
        if(assetGroup!=null)
        {
            searchFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,assetGroup));
        }
        sparePartsZkbFePartOrderList=this.zkbFePartOrderMapper.queryZkbFePart(searchFilters);
        List<ZkbPartOrder> zkbPartOrderList =null;
        zkbPartOrderList = this.zkbPartOrderMapper.queryBySystemIdAndSr(systemId,sr,partOrderNo,assetGroup);
        if(CollectionUtils.isNotEmpty(zkbPartOrderList))
        {
            List<ZkbFePartOrder> list = new ArrayList<>();
            for(ZkbPartOrder item:zkbPartOrderList)
            {
                ZkbFePartOrder zkbFePartOrder = new ZkbFePartOrder();
                zkbFePartOrder.setPartOrderNo(item.getPartOrderNo());
                zkbFePartOrder.setSr(item.getSrNo());
                zkbFePartOrder.setFunctionType(item.getProductNumber());
                zkbFePartOrder.setPhenomenon(item.getFaultDescription());
                zkbFePartOrder.setReportUser(item.getRequestUser());
                zkbFePartOrder.setHospitalName(item.getHospitalName());
                zkbFePartOrder.setGeEngineerName(item.getGeEngineerName());
                zkbFePartOrder.setGeEngineerPhone(item.getGeEngineerPhone());
                zkbFePartOrder.setContractId(item.getContractId());
                zkbFePartOrder.setSystemId(item.getSystemId());
                zkbFePartOrder.setOrderType(item.getOrderType());
                zkbFePartOrder.setId(item.getId());
                zkbFePartOrder.setReportDate(item.getRequestDate());
                list.add(zkbFePartOrder);
            }
            if(CollectionUtils.isNotEmpty(sparePartsZkbFePartOrderList))
            {
                sparePartsZkbFePartOrderList.addAll(list);
            }else
                {
                    sparePartsZkbFePartOrderList=list;
                }
        }
        if(CollectionUtils.isEmpty(sparePartsZkbFePartOrderList))
        {
            if(ZkbFePartOrder.OrderType.Probe.toString().equals(orderType))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("错误：暂未检索到超声类对应报修单"));

            }else
                {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_searchError"));
                }

        }
        return sparePartsZkbFePartOrderList;
    }


    /**
     * 选择单据
     * @param selected 当前流程数据
     * @param selectZkbFePartOrder 选择的流程
     * @return
     */
    public ZkbFePartOrder selectedWorkFlow(ZkbFePartOrder selected,ZkbFePartOrder selectZkbFePartOrder)
    {
        selected.setFatherPartOrderNo(selectZkbFePartOrder.getPartOrderNo());
        selected.setSystemId(selectZkbFePartOrder.getSystemId());
        selected.setContractId(selectZkbFePartOrder.getContractId());
        selected.setPhenomenon(selectZkbFePartOrder.getPhenomenon());
        ZkbContract zkbContract = this.zkbContractRepository.findById(selectZkbFePartOrder.getContractId());
        selected.setTenantUid(zkbContract.getTenantUID());
        List<AssetInfo> assetInfoList = this.assetInfoMapper.fetchByAssetInfoBySystemId(selectZkbFePartOrder.getSystemId(),selectZkbFePartOrder.getContractId());
        if(CollectionUtils.isNotEmpty(assetInfoList))
        {
            AssetInfo assetInfo = assetInfoList.get(0);
            selected.setArtificialType("");
            if(assetInfo.getAssetGroup()!=null&&"2".equals(assetInfo.getAssetGroup().toString()))
            {
                selected.setArtificialType(highArtificialTypeValue);
            }
            selected.setAssetGroup(assetInfo.getAssetGroup().toString());
            selected.setProductNumber(assetInfo.getFunctionType());
            selected.setAssetType(assetInfo.getAssetType());
            if(assetTypeHighMachineValue.equals(assetInfo.getAssetType()))
            {
                selected.setArtificialType(highArtificialTypeValue);
            }
        }
        return selected;
    }

//    /**
//     * 保存附件
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void saveUploadFile(List<V2_BlobObject> v2_blobObjects,String id){
//        if(v2_blobObjects != null){
//            for (V2_BlobObject v : v2_blobObjects){
//                v.setBoUuid(id);
//                blobObjectRepository.save(v);
//            }
//        }
//    }

    /**
     * 生成excel
     * @param doc 文件
     * @param dataMap 要导出的map
     */
    public void exportData(ExcelDocument doc, HashMap<String, Object> dataMap) {
        Integer keyRow = 1;
        Integer dataRow = 2;

        List<Map<String, Object>> zkbManMadeFePartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbManMadeFePartOrderDataList");
        List<Map<String, Object>> zkbProbeFePartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbProbeFePartOrderDataList");
        List<Map<String, Object>> zkbSparePartFePartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbSparePartFePartOrderDataList");
        if(CollectionUtils.isNotEmpty(zkbManMadeFePartOrderDataList))
        {
            for (Map<String, Object> item : zkbManMadeFePartOrderDataList) {
                doc.writeRowData(excelSheetNameOfManMade, keyRow, dataRow++, item);
            }
        }
        dataRow = 2;
        if(CollectionUtils.isNotEmpty(zkbProbeFePartOrderDataList)) {
            for (Map<String, Object> item : zkbProbeFePartOrderDataList) {
                doc.writeRowData(excelSheetNameOfProbe, keyRow, dataRow++, item);
            }
        }
        dataRow = 2;
        if(CollectionUtils.isNotEmpty(zkbSparePartFePartOrderDataList)) {
        for (Map<String, Object> item : zkbSparePartFePartOrderDataList) {
            doc.writeRowData(excelSheetNameOfSparePart, keyRow, dataRow++, item);
        }
        }
    }


    /**
     * 根据订单列表生成MAP
     * @param zkbFePartOrderList 订单列表
     * @return
     */
    public HashMap<String,Object> createZkbFePartOrderData(List<ZkbFePartOrder> zkbFePartOrderList){
        List<Map<String, Object>> zkbManMadeFePartOrderDataList = new ArrayList<>();
        List<Map<String, Object>> zkbProbeFePartOrderDataList = new ArrayList<>();
        List<Map<String, Object>> zkbSparePartFePartOrderDataList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        typeList.add(machineStatus);
        typeList.add(urgencyStatus);
        typeList.add(subtractTotalStatus);
        typeList.add(status);
        typeList.add(artificialType);
        typeList.add(sparePartsOrderAnnotation);
        typeList.add(assetTypeDi);
        typeList.add(zkbUrgentReasonType);
        typeList.add(zkbPoReturnType);
        typeList.add(partQualityIssue);
        typeList.add(needLetterFlag);
        typeList.add(yesOrNo);
        typeList.add(emergency);
        Map<String,Map<String,String>> i18Map = this.getI18ToMap(typeList);
        if(CollectionUtils.isNotEmpty(zkbFePartOrderList))
        {
            for(ZkbFePartOrder item : zkbFePartOrderList){
                if(ZkbFePartOrder.OrderType.ManMade.toString().equals(item.getOrderType()))
                {
                    HashMap<String,Object> map = this.getManMadeHashMap(item,i18Map);
                    zkbManMadeFePartOrderDataList.add(map);
                }else if(ZkbFePartOrder.OrderType.Probe.toString().equals(item.getOrderType()))
                {
                    HashMap<String,Object> map = this.getProbeHashMap(item,i18Map);
                    zkbProbeFePartOrderDataList.add(map);
                }else if(ZkbFePartOrder.OrderType.SpareParts.toString().equals(item.getOrderType()))
                {
                    List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = this.zkbFePartOrderAssetInfoRepository.findByFePartOrderId(item.getId());
                    if(CollectionUtils.isNotEmpty(zkbFePartOrderAssetInfoList))
                    {
                        for(ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfo:zkbFePartOrderAssetInfoList)
                        {
                            HashMap<String,Object> map = this.getSparePartHashMap(item,i18Map,zkbFePartOrderAssetInfo);
                            zkbSparePartFePartOrderDataList.add(map);
                        }
                    }else
                        {
                            HashMap<String,Object> map = this.getSparePartHashMap(item,i18Map,null);
                            zkbSparePartFePartOrderDataList.add(map);
                        }
                }
            }
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("zkbManMadeFePartOrderDataList",zkbManMadeFePartOrderDataList);
        map.put("zkbProbeFePartOrderDataList",zkbProbeFePartOrderDataList);
        map.put("zkbSparePartFePartOrderDataList",zkbSparePartFePartOrderDataList);
        return map;
    }

    /**
     *
     * @param strList
     * @return
     */
    private Map<String,Map<String,String>> getI18ToMap(List<String> strList)
    {
        Map<String,Map<String,String>> i18Map = new HashMap<>();
        if(CollectionUtils.isEmpty(strList))
        {
            return i18Map;
        }
        for(String item:strList)
        {
            List<I18nMessage> i18nMessageList = this.i18nMessageRepository.getByTypeAndSiteId(item ,-1);
            if(CollectionUtils.isEmpty(i18nMessageList)) continue;
            Map<String,String> map = new HashMap();
            for(I18nMessage i18nMessage:i18nMessageList)
            {
                if(map.containsKey(i18nMessage.getMsgKey())) continue;
                map.put(i18nMessage.getMsgKey(),i18nMessage.getValueZh());
            }
            if(i18Map.containsKey(item)) continue;
            i18Map.put(item,map);
        }
        return i18Map;
    }

    /**
     * 根据人工申请生成HashMap
     * @param zkbFePartOrder 人工申请List
     * @param i18Map i18 Map集合
     * @return
     */
    public HashMap<String,Object> getManMadeHashMap(ZkbFePartOrder zkbFePartOrder,Map<String,Map<String,String>> i18Map)
    {
        HashMap<String,Object> manMadeMap = new HashMap();
        if(zkbFePartOrder==null){
            return manMadeMap;
        }
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"),zkbFePartOrder.getSystemId());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_orderNo"),zkbFePartOrder.getPartOrderNo());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_reportUser"),zkbFePartOrder.getReportUser());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_reportDate"),this.dateFormat(zkbFePartOrder.getReportDate(),dateFormat));
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_hospitalName"),zkbFePartOrder.getHospitalName());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_hospitalClientName"),zkbFePartOrder.getHospitalClientName());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_hospitalClientPhone"),zkbFePartOrder.getHospitalClientPhone());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_functionType"),zkbFePartOrder.getFunctionType());
        if(i18Map.containsKey(machineStatus)&&i18Map.get(machineStatus).containsKey(zkbFePartOrder.getMachineStatus()))
        {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_machineStatus"),i18Map.get(machineStatus).get(zkbFePartOrder.getMachineStatus()));
        }
        if(i18Map.containsKey(urgencyStatus)&&i18Map.get(urgencyStatus ).containsKey(zkbFePartOrder.getUrgencyStatus()))
        {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_urgencyStatus"),i18Map.get(urgencyStatus).get(zkbFePartOrder.getUrgencyStatus()));
        }
        if(i18Map.containsKey(subtractTotalStatus)&&i18Map.get(subtractTotalStatus ).containsKey(zkbFePartOrder.getSubtractTotalStatus()))
        {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_subtractTotalStatus"),i18Map.get(subtractTotalStatus).get(zkbFePartOrder.getSubtractTotalStatus()));
        }
        if(i18Map.containsKey(status)&&i18Map.get(status).containsKey(zkbFePartOrder.getStatus()))
        {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"),i18Map.get(status).get(zkbFePartOrder.getStatus()));
        }
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"),zkbFePartOrder.getPhenomenon());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"),zkbFePartOrder.getTenantAssignUser());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"),this.dateFormat(zkbFePartOrder.getTenantAssignDate(),dateFormat));
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_comment"),zkbFePartOrder.getComment());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_sr"),zkbFePartOrder.getSr());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_act"),zkbFePartOrder.getAct());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_geEngineerName"),zkbFePartOrder.getGeEngineerName());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_geEngineerPhone"),zkbFePartOrder.getGeEngineerPhone());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_fourHundredComment"),zkbFePartOrder.getRemark());
        return manMadeMap;
    }

    /**
     * 根据探头申请生成HashMap
     * @param zkbFePartOrder 人工申请List
     * @return
     */
    public HashMap<String,Object> getProbeHashMap(ZkbFePartOrder zkbFePartOrder,Map<String,Map<String,String>> i18Map)
    {
        HashMap probeMap = new HashMap();
        if(zkbFePartOrder==null){
            return probeMap;
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"),zkbFePartOrder.getSystemId());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_probeOrderNo"),zkbFePartOrder.getPartOrderNo());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_reportUser"),zkbFePartOrder.getReportUser());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_probeReportDate"),this.dateFormat(zkbFePartOrder.getReportDate(),dateFormat));
        if(i18Map.containsKey(assetTypeDi)&&i18Map.get(assetTypeDi).containsKey(zkbFePartOrder.getAssetTypeDi()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_assetTypeDi"),i18Map.get(assetTypeDi).get(zkbFePartOrder.getAssetTypeDi()));
        }
        if(i18Map.containsKey(sparePartsOrderAnnotation)&&i18Map.get(sparePartsOrderAnnotation).containsKey(zkbFePartOrder.getOrderAnnotation()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderAnnotation"),i18Map.get(sparePartsOrderAnnotation).get(zkbFePartOrder.getOrderAnnotation()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_problemOrderNumber"),zkbFePartOrder.getProblemOrderNumber());
        if(i18Map.containsKey(emergency)&&i18Map.get(emergency).containsKey(zkbFePartOrder.getUrgentOrder()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_urgentOrder"),i18Map.get(emergency).get(zkbFePartOrder.getUrgentOrder()));
        }
        if(i18Map.containsKey(zkbUrgentReasonType)&&i18Map.get(zkbUrgentReasonType).containsKey(zkbFePartOrder.getUrgencyReasons()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_urgencyReasons"),i18Map.get(zkbUrgentReasonType).get(zkbFePartOrder.getUrgencyReasons()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"),zkbFePartOrder.getPhenomenon());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_productNumber"),zkbFePartOrder.getProductNumber());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_remark"),zkbFePartOrder.getRemark());
        if(i18Map.containsKey(status)&&i18Map.get(status).containsKey(zkbFePartOrder.getStatus()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"),i18Map.get(status).get(zkbFePartOrder.getStatus()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_catNo"),zkbFePartOrder.getCatNo());

        if(zkbFePartOrder.getZkbPartOrderCat()!=null)
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_zkbPartOrderCat"),zkbFePartOrder.getZkbPartOrderCat().getCatModel());
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_valueType"),zkbFePartOrder.getZkbPartOrderCat().getValueType());
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_oldCatSn"),zkbFePartOrder.getOldCatSn());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"),zkbFePartOrder.getCatNum());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"),zkbFePartOrder.getTenantAssignUser());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"),this.dateFormat(zkbFePartOrder.getTenantAssignDate(),dateFormat));
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_comment"),zkbFePartOrder.getComment());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"),zkbFePartOrder.getOrderNo());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"),zkbFePartOrder.getPrice());
        if(i18Map.containsKey(yesOrNo)&&i18Map.get(yesOrNo).containsKey(zkbFePartOrder.getNewCatFlag()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_newProbe"),i18Map.get(yesOrNo).get(zkbFePartOrder.getNewCatFlag()));
        }
        if(i18Map.containsKey(needLetterFlag)&&i18Map.get(needLetterFlag).containsKey(zkbFePartOrder.getNeedLetterFlag()))
        {
            if(ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString().equals(zkbFePartOrder.getCurrentStep())
                    ||ZkbFePartOrder.workFlowSteps.Complete.toString().equals(zkbFePartOrder.getCurrentStep()))
            {
                probeMap.put(WebUtil.getMessage("zkbFePartOrder_needProbeLetter"),i18Map.get(needLetterFlag).get(zkbFePartOrder.getNeedLetterFlag()));
            }
        }
        probeMap.put("400下单"+WebUtil.getMessage("zkbFePartOrder_remark"),zkbFePartOrder.getApmComment());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderedPerson"),zkbFePartOrder.getFullApmInfoUser());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderedDate"),this.dateFormat(zkbFePartOrder.getFullApmInfoDate(),dateFormat));
        if(i18Map.containsKey(zkbPoReturnType)&&i18Map.get(zkbPoReturnType).containsKey(zkbFePartOrder.getReturnType()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_returnType"),i18Map.get(zkbPoReturnType).get(zkbFePartOrder.getReturnType()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_partSerialNumber"),zkbFePartOrder.getPartSerialNumber());
        if(i18Map.containsKey(partQualityIssue)&&i18Map.get(partQualityIssue).containsKey(zkbFePartOrder.getPartQualityIssue()))
        {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_partQualityIssue"),i18Map.get(partQualityIssue).get(zkbFePartOrder.getPartQualityIssue()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_airWayBillNo"),zkbFePartOrder.getWaybillNo());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_returnDate"),this.dateFormat(zkbFePartOrder.getReturnDate(),dateFormat));
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_newCatSn"),zkbFePartOrder.getNewCatSn());
        probeMap.put("FE归还信息"+WebUtil.getMessage("zkbFePartOrder_remark"),zkbFePartOrder.getGeAdminComment());
        return probeMap;
    }

    /**
     * 根据备件申请生成HashMap
     * @param zkbFePartOrder 人工申请List
     * @param i18Map i18信息
     * @param zkbFePartOrderAssetInfo 申请信息
     * @return
     */
    public HashMap<String,Object> getSparePartHashMap(ZkbFePartOrder zkbFePartOrder,Map<String,Map<String,String>> i18Map,ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfo)
    {
        HashMap sparePartMap = new HashMap();
        if(zkbFePartOrder==null){
            return sparePartMap;
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"),zkbFePartOrder.getSystemId());
        if(i18Map.containsKey(artificialType )&&i18Map.get(artificialType ).containsKey(zkbFePartOrder.getArtificialType()))
        {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_artificialType"),i18Map.get(artificialType).get(zkbFePartOrder.getArtificialType()));
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_reportUser"),zkbFePartOrder.getReportUser());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_probeReportDate"),this.dateFormat(zkbFePartOrder.getReportDate(),dateFormat));
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNo"),zkbFePartOrder.getPartOrderNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"),zkbFePartOrder.getPhenomenon());
        if(i18Map.containsKey(sparePartsOrderAnnotation  )&&i18Map.get(sparePartsOrderAnnotation  ).containsKey(zkbFePartOrder.getOrderAnnotation()))
        {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderAnnotation"),i18Map.get(sparePartsOrderAnnotation).get(zkbFePartOrder.getOrderAnnotation()));
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_problemOrderNumber"),zkbFePartOrder.getProblemOrderNumber());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_productNumber"),zkbFePartOrder.getProductNumber());
        if(i18Map.containsKey(status)&&i18Map.get(status).containsKey(zkbFePartOrder.getStatus()))
        {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"),i18Map.get(status).get(zkbFePartOrder.getStatus()));
        }
        if(i18Map.containsKey(assetTypeDi)&&i18Map.get(assetTypeDi).containsKey(zkbFePartOrder.getAssetTypeDi()))
        {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetTypeDi"),i18Map.get(assetTypeDi).get(zkbFePartOrder.getAssetTypeDi()));
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"),zkbFePartOrder.getTenantAssignUser());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"),this.dateFormat(zkbFePartOrder.getTenantAssignDate(),dateFormat));
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_comment"),zkbFePartOrder.getComment());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderedPerson"),zkbFePartOrder.getFullApmInfoUser());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderedDate"),this.dateFormat(zkbFePartOrder.getFullApmInfoDate(),dateFormat));
        if(zkbFePartOrderAssetInfo!=null)
        {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetNo"),zkbFePartOrderAssetInfo.getAssetNo());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetDescription"),zkbFePartOrderAssetInfo.getAssetDescription());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"),zkbFePartOrderAssetInfo.getAssetQty());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"),zkbFePartOrderAssetInfo.getOrderNo());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"),zkbFePartOrderAssetInfo.getPrice());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_spareParts"),zkbFePartOrderAssetInfo.getSparePart());
        }
        return sparePartMap;
    }

    /**
     * 日期格式化
     * @param date 时间
     * @param dateFormat 日期格式
     * @return
     */
    private String dateFormat(Date date,String dateFormat)
    {
        if(date==null)
        {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }


}
