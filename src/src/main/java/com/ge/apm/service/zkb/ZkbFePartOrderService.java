package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.ZkbFePartOrderMapper;
import com.ge.apm.dao.mapper.ZkbPartOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInfoRow;
import com.ge.apm.service.asset.ZkbFePartOrderRow;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.zkb.fePartOrder.ZkbFePartOrderProbeController;
import com.ge.apm.view.zkb.fePartOrder.ZkbFePartOrderSparePartDetailController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 503202755 2020/12/8
 */
@Component
public class ZkbFePartOrderService extends MicroServiceInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFePartOrderService.class);

    /**
     * 模板数据起始行数
     */
    public static final int EXCEL_ORDER_HISTORY_DATA_ROW = 2;

    @Autowired
    BlobObjectRepository blobObjectRepository;

    /**
     * 设备
     */
    @Autowired
    private AssetInfoRepository assetInfoRepository;
    /**
     * 归还数量
     */
    @Autowired
    ZkbFePartOrderReturnRepository zkbFePartOrderReturnRepository;

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

    @Autowired
    private AssetInfoMapper assetInfoMapper;

    /**
     * 根据system_id和用户tenant_uid 查询设备信息
     */
    @Autowired
    private ZkbFePartOrderMapper zkbFePartOrderMapper;

    @Autowired
    private ZkbContractQuotaLogService zkbContractQuotaLogService;

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
     * 查询i18 repository
     */
    @Autowired
    private I18nMessageRepository i18nMessageRepository;

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;

    @Autowired
    ZkbFePartOrderModifyLogRepository zkbFePartOrderModifyLogRepository;

    @Autowired
    ZkbFePartOrderReturnLogRepository zkbFePartOrderReturnLogRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 人工申请单号前缀
     */
    private static final String ORDER_PREFIX = "FE";

    /**
     * 备件申请单号前缀
     */
    private static final String SPARE_PARTS_ORDER_PREFIX = "FEPO";

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
     * 探头申请sheet名字
     */
    public static final String excelSheetNameOfProbeReturn = "探头归还";

    /**
     * 备件申请sheet名字
     */
    public static final String excelSheetNameOfSparePartReturn = "备件归还";

    /**
     * 机器状态i18值
     */
    public static final String machineStatus = "zkbFePartOrder_machineStatus";

    /**
     * 紧急类型
     */
    public static final String urgencyStatus = "zkbFePartOrder_casePriority";

    /**
     * 是否扣除名额
     */
    public static final String subtractTotalStatus = "zkbFePartOrder_isSubtractTotalStatus";

    /**
     * 流程状态
     */
    public static final String status = "zkbFePartOrder_status";

    /**
     * 机型
     */
    public static final String artificialType = "zkbFePartOrder_artificialType";

    /**
     * 订单标注
     */
    public static final String sparePartsOrderAnnotation = "zkbFePartOrder_sparePartsOrderAnnotation";

    /**
     * 设备类型（DI）
     */
    public static final String assetTypeDi = "zkbFePartOrder_assetTypeDi";

    /**
     * 订单紧急的原因
     */
    public static final String zkbUrgentReasonType = "zkbUrgentReasonType";

    /**
     * 归还类型
     */
    public static final String zkbPoReturnType = "zkbFePartOrder_zkbPoReturnType";

    /**
     * 故障描述
     */
    public static final String partQualityIssue = "zkbFePartOrder_partQualityIssue";

    /**
     * 是否需要探头说明函
     */
    public static final String needLetterFlag = "zkbFePartOrder_needLetterFlag";

    /**
     * 是还是否
     */
    public static final String yesOrNo = "zkbFePartOrder_yesOrNo";

    /**
     * 是否紧急
     */
    public static final String emergency = "zkbFePartOrder_emergency";

    public static final String zkbPoValueType = "zkbPoValueType";

    public static final String gefeOrderType = "zkbFePartOrder_orderType";

    /**
     * 日期格式
     */
    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public static final String dateFormat_month = "yyyy-MM-dd";

    /**
     * 合同超声高端机
     */
    public static final String assetTypeHighMachineValue = "highMachine";

    public static final String testLoginNameStartWith = "test.";

    /**
     * 是测试数据值
     */
    public static final String testStatusTrueValue = "t";

    /**
     * 是测试数据值
     */
    public static final String testStatusFalseValue = "f";

    /**
     * 租户信息
     */
    @Autowired
    private TenantInfoRepository tenantInfoRepository;

    /**
     * 导入模板头部行数
     */
    public static final int EXCEL_FE_PART_ORDER_HEADER_ROW = 1;

    /**
     * 最大导入行数
     */
    public static final int EXCEL_FE_PART_ORDER_IMPORT_MAX_ROW = 10000;


    /**
     * @autor by lyj 503277717
     * @date 2022/1/7
    */
    public String checkAsset(String sysId) {
        List<AssetInfo> assetInfos = assetInfoRepository.getBySystemIdNoConditionGefe(sysId);
        if (CollectionUtils.isEmpty(assetInfos)) {
            return "设备不存在！";
        }
        if(assetInfos.size()>1){
            return "该SystemId存在多条设备!";
        }
        /*if(!assetInfo.getIsValid()){
            return "设备已报废，不能继续申请！！";
        }*/
        return "";
    }


    /**
     * 给申请单添加单号和索引
     *
     * @param zkbFePartOrder
     * @return
     */
    private ZkbFePartOrder fullOrderNo(ZkbFePartOrder zkbFePartOrder, String orderPrefix) {
        Object o = zkbFePartOrderRepository.getMaxIndex(orderPrefix + simpleDateFormat.format(new Date()) + "%");
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String partOrderNo = orderPrefix + simpleDateFormat.format(new Date()) + String.format("%04d", index);
        zkbFePartOrder.setPartOrderNo(partOrderNo);
        zkbFePartOrder.setPartOrderIndex(index);
        return zkbFePartOrder;
    }

    /**
     * 获取AssetTypeDi list 转map
     *
     * @return
     */
    public Map<String, String> getI18MessageMap(String type) {
        List<I18nMessage> messageList = this.i18nMessageRepository.getByTypeAndSiteId(type, -1);
        Map<String, String> messageMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(messageList)) {
            Iterator<I18nMessage> i18nMessageIterator = messageList.iterator();
            while (i18nMessageIterator.hasNext()) {
                I18nMessage item = i18nMessageIterator.next();
                messageMap.put(item.getMsgKey(), item.getValueZh());
            }
        }
        return messageMap;
    }

    /**
     * 添加新申请单初始化数据
     *
     * @param zkbFePartOrder
     * @return
     */
    public ZkbFePartOrder initNewOrder(ZkbFePartOrder zkbFePartOrder) {
        Date date = new Date();
        zkbFePartOrder.setReportDate(date);
        this.fullOrderNo(zkbFePartOrder, ZkbFePartOrderService.ORDER_PREFIX);
        return zkbFePartOrder;
    }

    public ZkbFePartOrder initTestStatus(String loginName, ZkbFePartOrder zkbFePartOrder) {
        zkbFePartOrder.setTestStatus(testStatusFalseValue);
        if (StringUtils.isBlank(loginName)) return zkbFePartOrder;
        if (loginName.startsWith(testLoginNameStartWith))
            zkbFePartOrder.setTestStatus(testStatusTrueValue);
        return zkbFePartOrder;
    }

    /**
     * 添加新申请单初始化数据
     *
     * @param zkbFePartOrder
     * @return
     */
    public ZkbFePartOrder initNewSparePartsOrder(ZkbFePartOrder zkbFePartOrder) {
        Date date = new Date();
        zkbFePartOrder.setReportDate(date);
        this.fullOrderNo(zkbFePartOrder, ZkbFePartOrderService.SPARE_PARTS_ORDER_PREFIX);
        return zkbFePartOrder;
    }

    /**
     * 根据SystemId获取相关信息
     *
     * @param zkbFePartOrder
     * @param systemId       设备system id
     * @return
     */
    public List<AssetInfo> fullInfoBySystemId(ZkbFePartOrder zkbFePartOrder, String systemId) {
        zkbFePartOrder.setSystemId(systemId);
        List<AssetInfo> listAssetInfo = this.zkbFePartOrderMapper.loadAssetInfoBySysId(systemId);
        return CollectionUtils.isNotEmpty(listAssetInfo)?listAssetInfo:new ArrayList<AssetInfo>();
    }

    /**
     * 根据UID获取相关信息
     *
     * @param zkbFePartOrder
     * @param zkbFePartOrder
     * @return
     */
    public List<AssetInfo> initAssetInfo(ZkbFePartOrder zkbFePartOrder) {
        List<AssetInfo> listAssetInfo = new ArrayList<AssetInfo>();
        if(StringUtils.isNotBlank(zkbFePartOrder.getAssetInfoId())){
            listAssetInfo = this.zkbFePartOrderMapper.loadAssetInfoByUid(zkbFePartOrder.getAssetInfoId());
        }else{
            listAssetInfo = this.zkbFePartOrderMapper.loadAssetInfoForHisData(zkbFePartOrder.getSystemId(),zkbFePartOrder.getContractId(),zkbFePartOrder.getTenantUid());

        }
        return CollectionUtils.isNotEmpty(listAssetInfo)?listAssetInfo:new ArrayList<AssetInfo>();
    }

    /**
     * 点击sysid 后的方法
     * @param zkbFePartOrder
     * @param assetInfo
     * @return
     */
    public List<ZkbFePartOrder> fullInfoBySystemIdBuildData(ZkbFePartOrder zkbFePartOrder,AssetInfo assetInfo) {
        List<ZkbFePartOrder> list = this.zkbFePartOrderMapper.loadAssetInfoBuildByUid(assetInfo.getUid());
        if (CollectionUtils.isEmpty(list)||list.size()>1) {
            zkbFePartOrder.setContractId(null);
            zkbFePartOrder.setFunctionType(null);
            zkbFePartOrder.setContractId(null);
            zkbFePartOrder.setHospitalName(null);
            zkbFePartOrder.setTenantUid(null);
            zkbFePartOrder.setSubtractTotalStatus(null);
            zkbFePartOrder.setAssetType(null);
            return list;
        }
        ZkbFePartOrder item = list.get(0);
        zkbFePartOrder.setContractId(item.getContractId());
        zkbFePartOrder.setFunctionType(item.getFunctionType());
        zkbFePartOrder.setHospitalName(item.getHospitalName());
        zkbFePartOrder.setTenantUid(item.getTenantUid());
        zkbFePartOrder.setAssetType(item.getAssetType());
        zkbFePartOrder.setAssetInfoId(assetInfo.getUid());
        if (assetTypeHighMachineValue.equals(zkbFePartOrder.getAssetType())) {
            zkbFePartOrder.setSubtractTotalStatus("false");
        }
        return list;
    }
    /*public ZkbFePartOrder fullInfoBySystemId(ZkbFePartOrder zkbFePartOrder, String systemId) {
        zkbFePartOrder.setSystemId(systemId);
        List<ZkbFePartOrder> list = this.zkbFePartOrderMapper.loadAssetInfo(systemId);
        List<AssetInfo> listAssetInfo = this.zkbFePartOrderMapper.loadAssetInfoBySysId(systemId);

        if (CollectionUtils.isEmpty(list)) {
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
        if (assetTypeHighMachineValue.equals(zkbFePartOrder.getAssetType())) {
            zkbFePartOrder.setSubtractTotalStatus("false");
        }
        return zkbFePartOrder;
    }*/

    /**
     * 检查合同和systemId是否一致
     *
     * @param zkbFePartOrder 人工申请主表信息
     * @return
     */
    /*public Boolean checkSystemIdAndContract(ZkbFePartOrder zkbFePartOrder) {
        List<ZkbFePartOrder> list = this.zkbFePartOrderMapper.loadAssetInfo(zkbFePartOrder.getSystemId());
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        ZkbFePartOrder item = list.get(0);
        if (!item.getContractId().equals(zkbFePartOrder.getContractId())) {
            return false;
        }
        return true;
    }*/

    /**
     * 根据ID发送消息
     *
     * @param id 主键ID
     */
    public void sendMessage(String id, GatewayURL gatewayURL) {
        String token = UserContextService.getAccessToken();
        HttpHeaders header = createHttpHeaders(token);
        HttpEntity<Map> requestEntity = new HttpEntity<>(header);
        //运用多线程，防止服务不可用，不影响主流程
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //停顿5秒，防止数据未入库
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.info(e.getMessage());
                }
                ResponseEntity<LinkedHashMap> response = template.exchange(getGatewayURL(gatewayURL) + "?id=" + id, HttpMethod.POST, requestEntity, LinkedHashMap.class);
            }
        });
        t.start();
    }

    /**
     * 根据登录账户名获取当前账户名流程配置信息
     *
     * @return
     */
    public Map<String, String> getMapConfigByLoginName(String loginName) {
        Map<String, String> configTypeMap = new HashMap<>();
        List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(loginName);
        if (CollectionUtils.isEmpty(list)) {
            return configTypeMap;
        }
        for (ZkbFePartOrderUserConfig item : list) {
            if (item == null) continue;
            if (configTypeMap.containsKey(item.getConfigType())) continue;
            configTypeMap.put(item.getConfigType(), item.getConfigType());
        }
        return configTypeMap;
    }

    /**
     * 判断流程是否在第一节点
     *
     * @param currentStep
     * @return
     */
    public boolean workFlowInFistStep(String currentStep) {
        if (ZkbFePartOrder.workFlowSteps.Requested.toString().equals(currentStep))
            return true;
        return false;
    }

    /**
     * 回退列表
     *
     * @param queryStatus 查询状态
     * @return
     */
    public String backList(String queryStatus) {
        return "List.xhtml?faces-redirect=true&queryStatus=" + queryStatus;
    }

    /**
     * @param zkbFePartOrder 当前数据
     * @param userUid        用户UID
     * @param userName       用户user name
     * @param tenantUID      用户 tenant UID
     * @param queryStatus    查询状态
     * @param status         流程状态
     * @param currentStep    流程节点
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param gatewayURL     消息发送地址
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
                                     GatewayURL gatewayURL) {
        if (ZkbFePartOrder.workFlowSteps.Requested.toString().equals(zkbFePartOrder.getCurrentStep())) {
            List<ZkbFePartOrder> listZkbFePartOrder = this.zkbFePartOrderRepository.findByPartOrderNo(zkbFePartOrder.getPartOrderNo());
            if (CollectionUtils.isNotEmpty(listZkbFePartOrder)) {
                String orderPrefix = "";
                if (zkbFePartOrder.getOrderType().equals(ZkbFePartOrder.OrderType.ManMade)) {
                    orderPrefix = ORDER_PREFIX;
                } else {
                    orderPrefix = SPARE_PARTS_ORDER_PREFIX;
                }
                this.fullOrderNo(zkbFePartOrder, orderPrefix);
            }
        }
        zkbFePartOrder.setStatus(status);
        zkbFePartOrder.setCurrentStep(currentStep);
        this.zkbFePartOrderRepository.save(zkbFePartOrder);
        this.zkbFePartOrderStepService.updateLatestStepEndTime(zkbFePartOrder.getId(), startTime);
        this.zkbFePartOrderStepService.addNewItem(
                zkbFePartOrder.getId(),
                currentStep,
                userUid, userName,
                startTime,
                endTime,
                status,
                tenantUID);
        if (gatewayURL != null) {
            this.sendMessage(zkbFePartOrder.getId(), gatewayURL);
        }

        return this.backList(queryStatus);
    }

    /**
     * 修改高值探头信息
     *
     * @param userAccount 修改用户
     * @param isEnabled   是否可用
     * @param id          主键
     */
    public void updateZkbContractCat(UserAccount userAccount, Boolean isEnabled, String id) {
        ZkbContractCat zkbContractCat = this.zkbContractCatRepository.findById(id);
        if (zkbContractCat == null) {
            return;
        }
        zkbContractCat.setLastModifiedDate(new Date());
        zkbContractCat.setEnabled(isEnabled);
        zkbContractCat.setLastModifiedBy(userAccount.getName());
        this.zkbContractCatRepository.save(zkbContractCat);
    }

    /**
     * 加载合同信息
     */
    public List<ZkbContract> loadZkbContractInfo(String contractId) {
        List<ZkbContract> list = new ArrayList<>();
        if (StringUtils.isBlank(contractId)) {
            return list;
        }
        ZkbContract item = this.zkbContractRepository.findById(contractId);
        list.add(item);
        return list;
    }

    /**
     * 加载合同信息
     */
    public List<ZkbContract> loadZkbContractInfoWithCheck(String contractId,String systemId) {
        List<ZkbContract> list = new ArrayList<>();
        if (StringUtils.isBlank(contractId)) {
            return list;
        }
        ZkbContract item = this.zkbContractRepository.findById(contractId);
        int i =  this.zkbFePartOrderMapper.checkAssetInfoInContract(contractId,systemId);
        if(i==1)
            list.add(item);
        return list;
    }
    /**
     * @param zkbContract    合同信息
     * @param type           1.把剩余名额移入申请名额 2.把申请名额移入已使用名额 3.把申请名额移入剩余名额
     * @param zkbFePartOrder 流程申请数据
     */
    public void moveRemianTimeOfContract(ZkbContract zkbContract, Integer type, ZkbFePartOrder zkbFePartOrder, UserAccount userAccount, String step) {

        if (!"true".equals(zkbFePartOrder.getSubtractTotalStatus())) return;
        String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderDeduct.toString();
        String orderType = null;
        if ("DI".equals(zkbContract.getContractType())
                || "PCB".equals(zkbContract.getContractType())
                || "PCB MVS".equals(zkbContract.getContractType())) {
            orderType = ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString();
            switch (type) {
                case 1:
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail() - 1);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply() + 1);
                    break;
                case 2:
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply() - 1);//备件/人工/保养申请名额
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse() + 1);//备件/人工/保养已使用名额
                    break;
                case 3:
                case 4:
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail() + 1);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply() - 1);
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    break;
            }
        } else if ("CCS".equals(zkbContract.getContractType())
                || "Biomed".equals(zkbContract.getContractType())) {
            orderType = ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString();
            switch (type) {
                case 1:
                    zkbContract.setGeArtificialSupportTimes(zkbContract.getGeArtificialSupportTimes() - 1);
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply() + 1);
                    break;
                case 2:
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply() - 1);//GE人工支持申请次数
                    zkbContract.setGeArtificialSupportTimesUse(zkbContract.getGeArtificialSupportTimesUse() + 1);//GE人工支持使用次数
                    break;
                case 3:
                case 4:
                    zkbContract.setGeArtificialSupportTimes(zkbContract.getGeArtificialSupportTimes() + 1);
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply() - 1);
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    break;
            }
        }
        this.zkbContractRepository.save(zkbContract);
        this.zkbContractQuotaLogService.saveData(zkbContract, null, zkbFePartOrder, opType, orderType, "zkbFePartOrder", userAccount, step);
    }
    /**
     * 移动备件名额
     *
     * @param type        移动类型
     * @param zkbContract 合同
     * @param qty         数量
     */
    public void moveSpareQuota(Integer type, ZkbContract zkbContract, Integer qty, UserAccount userAccount, ZkbFePartOrder zkbFePartOrder, String step) {
        String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderDeduct.toString();
        String orderType = null;
        if ("DI".equals(zkbContract.getContractType())
                || "PCB".equals(zkbContract.getContractType())
                || "PCB MVS".equals(zkbContract.getContractType())) {
            orderType = ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString();
            switch (type) {
                case 1:
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail() - qty);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply() + qty);
                    break;
                case 2:
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply() - qty);
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse() + qty);
                    break;
                case 3:
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse() - qty);
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail() + qty);
                    break;
                case 4:
                case 6:
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail() + qty);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply() - qty);
                    break;
                case 5://好还变坏还，已使用名额+ qty，剩余名额- qty，坏变好走第3步骤
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse() + qty);
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail() - qty);
                    break;
            }
        } else if ("CCS".equals(zkbContract.getContractType())
                || "Biomed".equals(zkbContract.getContractType())) {
            orderType = ZkbContractQuotaLog.OrderTypeEnum.Spare.toString();
            switch (type) {
                case 1:
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota() - qty);
                    zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaApply() + qty);
                    break;
                case 2:
                    zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaApply() - qty);
                    zkbContract.setSpareQuotaUse(zkbContract.getSpareQuotaUse() + qty);
                    break;
                case 3:
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    zkbContract.setSpareQuotaUse(zkbContract.getSpareQuotaUse() - qty);
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota() + qty);
                    break;
                case 4://代理商拒绝
                case 6://400团队拒绝
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota() + qty);//拒绝备件剩余名额加回去
                    zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaApply() - qty);
                    break;
                case 5://好还变坏还，已使用名额+ qty，剩余名额- qty，坏变好走第3步骤
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    zkbContract.setSpareQuotaUse(zkbContract.getSpareQuotaUse() + qty);
                    zkbContract.setSpareQuota(zkbContract.getSpareQuota() - qty);
                    break;
            }
        }
        this.zkbContractRepository.save(zkbContract);
        this.zkbContractQuotaLogService.saveData(zkbContract, null, zkbFePartOrder, opType, orderType, "zkbFePartOrder", userAccount, step);
    }
    /**
     * 移动探头名额
     *
     * @param type                    移动类型
     * @param zkbContract             合同
     * @param qty                     数量
     * @param showZkbPoProbeQuotaType 是否显示按钮
     * @param zkbPoProbeQuotaType     按钮选择
     */
    public void moveProbe(Integer type, ZkbContract zkbContract, Integer qty, Boolean showZkbPoProbeQuotaType, String zkbPoProbeQuotaType, UserAccount userAccount, ZkbFePartOrder zkbFePartOrder, Boolean record, String step) {
        Integer probeQuota = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
        Integer probeQuotaTotal = zkbContract.getProbeQuotaTotal() == null ? 0 : zkbContract.getProbeQuotaTotal();
        Integer probeQuotaUse = zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse();
        Integer probeQuotaApply = zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply();
        Integer probeQuotaNormal = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
        Integer probeQuotaNormalTotal = zkbContract.getProbeQuotaNormalTotal() == null ? 0 : zkbContract.getProbeQuotaNormalTotal();
        Integer probeQuotaNormalUse = zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse();
        Integer probeQuotaNormalApply = zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply();
        Integer probeQuotaSpecial = zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial();
        Integer probeQuotaSpecialTotal = zkbContract.getProbeQuotaSpecialTotal() == null ? 0 : zkbContract.getProbeQuotaSpecialTotal();
        Integer probeQuotaSpecialUse = zkbContract.getProbeQuotaSpecialUse() == null ? 0 : zkbContract.getProbeQuotaSpecialUse();
        Integer probeQuotaSpecialApply = zkbContract.getProbeQuotaSpecialApply() == null ? 0 : zkbContract.getProbeQuotaSpecialApply();
        Integer probeQuotaHigh = zkbContract.getProbeQuotaHigh() == null ? 0 : zkbContract.getProbeQuotaHigh();
        Integer probeQuotaHighUse = zkbContract.getProbeQuotaHighUse() == null ? 0 : zkbContract.getProbeQuotaHighUse();
        Integer probeQuotaHighApply = zkbContract.getProbeQuotaHighApply() == null ? 0 : zkbContract.getProbeQuotaHighApply();
        Integer probeQuotaHighTotal = zkbContract.getProbeQuotaHighTotal() == null ? 0 : zkbContract.getProbeQuotaHighTotal();
        String opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderDeduct.toString();
        String orderType = null;
        if (showZkbPoProbeQuotaType) {

            switch (type) {
                case 1:
                    //探头剩余名额-1，探头申请名额+1
                    if (ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString();
                        zkbContract.setProbeQuota(probeQuota - qty);
                        zkbContract.setProbeQuotaApply(probeQuotaApply + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal - qty);
                        zkbContract.setProbeQuotaNormalApply(probeQuotaNormalApply + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString();
                        zkbContract.setProbeQuotaSpecial(probeQuotaSpecial - qty);
                        zkbContract.setProbeQuotaSpecialApply(probeQuotaSpecialApply + qty);
                    } else {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal - qty);
                        zkbContract.setProbeQuotaNormalApply(probeQuotaNormalApply + qty);
                    }
                    break;
                case 2:
                    if (ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString();
                        //探头申请名额-1，探头已使用名额+1
                        zkbContract.setProbeQuotaApply(probeQuotaApply - qty);
                        zkbContract.setProbeQuotaUse(probeQuotaUse + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormalApply(probeQuotaNormalApply - qty);
                        zkbContract.setProbeQuotaNormalUse(probeQuotaNormalUse + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString();
                        zkbContract.setProbeQuotaSpecialApply(probeQuotaSpecialApply - qty);
                        zkbContract.setProbeQuotaSpecialUse(probeQuotaSpecialUse + qty);
                    } else {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormalApply(probeQuotaNormalApply - qty);
                        zkbContract.setProbeQuotaNormalUse(probeQuotaNormalUse + qty);
                    }
                    break;
                case 3:
                    //探头剩余名额-1 探头已使用名额+1
                    if (ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbPoProbeQuotaType)) {
                        //探头申请名额-1，探头已使用名额+1
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString();
                        zkbContract.setProbeQuota(probeQuota - qty);
                        zkbContract.setProbeQuotaUse(probeQuotaUse + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbPoProbeQuotaType)) {
                        //探头申请名额-1，探头已使用名额+1
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal - qty);
                        zkbContract.setProbeQuotaNormalUse(probeQuotaNormalUse + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbPoProbeQuotaType)) {
                        //探头申请名额-1，探头已使用名额+1
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString();
                        zkbContract.setProbeQuotaSpecial(probeQuotaSpecial - qty);
                        zkbContract.setProbeQuotaSpecialUse(probeQuotaSpecialUse + qty);
                    } else {
                        //探头申请名额-1，探头已使用名额+1
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal - qty);
                        zkbContract.setProbeQuotaNormalUse(probeQuotaNormalUse + qty);
                    }
                    break;
                case 4:
                case 6://400拒绝
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    if (ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString();
                        //探头申请名额-1，探头已使用名额+1
                        zkbContract.setProbeQuota(probeQuota + qty);
                        zkbContract.setProbeQuotaApply(probeQuotaApply - qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal + qty);
                        zkbContract.setProbeQuotaNormalApply(probeQuotaNormalApply - qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString();
                        zkbContract.setProbeQuotaSpecial(probeQuotaSpecial + qty);
                        zkbContract.setProbeQuotaSpecialApply(probeQuotaSpecialApply - qty);
                    } else {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal + qty);
                        zkbContract.setProbeQuotaNormalApply(probeQuotaNormalApply - qty);
                    }
                    break;
                case 5:
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    if (ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString();
                        //探头已使用名额-1，探头剩余名额+1
                        zkbContract.setProbeQuotaUse(probeQuotaUse - qty);
                        zkbContract.setProbeQuota(probeQuota + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        //探头已使用名额-1，探头剩余名额+1
                        zkbContract.setProbeQuotaNormalUse(probeQuotaNormalUse - qty);
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal + qty);
                    } else if (ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(zkbPoProbeQuotaType)) {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString();
                        //探头已使用名额-1，探头剩余名额+1
                        zkbContract.setProbeQuotaSpecialUse(probeQuotaSpecialUse - qty);
                        zkbContract.setProbeQuotaSpecial(probeQuotaSpecial + qty);
                    } else {
                        orderType = ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString();
                        //探头已使用名额-1，探头剩余名额+1
                        zkbContract.setProbeQuotaNormalUse(probeQuotaNormalUse - qty);
                        zkbContract.setProbeQuotaNormal(probeQuotaNormal + qty);
                    }
                    break;
            }
        } else {
            orderType = ZkbContractQuotaLog.OrderTypeEnum.Probe.toString();
            switch (type) {
                case 1:
                    //探头剩余名额-1，探头申请名额+1
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota() - qty);
                    zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaApply() + qty);
                    break;
                case 2:
                    //探头申请名额-1，探头已使用名额+1
                    zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaApply() - qty);
                    zkbContract.setProbeQuotaUse(zkbContract.getProbeQuotaUse() + qty);
                    break;
                case 3:
                    //探头剩余名额-1 探头已使用名额+1
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota() - qty);
                    zkbContract.setProbeQuotaUse(zkbContract.getProbeQuotaUse() + qty);
                    break;
                case 4:
                case 6://400拒绝
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    //探头剩余名额+1，探头申请名额-1
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota() + qty);
                    zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaApply() - qty);
                    break;
                case 5:
                    opType = ZkbContractQuotaLog.OpTypeEnum.ZkbFePartOrderReturn.toString();
                    //探头已使用名额-1，探头剩余名额+1
                    zkbContract.setProbeQuotaUse(zkbContract.getProbeQuotaUse() - qty);
                    zkbContract.setProbeQuota(zkbContract.getProbeQuota() + qty);
                    break;
            }
        }
        this.zkbContractRepository.save(zkbContract);
        if (record && StringUtils.isNotBlank(orderType)) {
            this.zkbContractQuotaLogService.saveData(zkbContract, null, zkbFePartOrder, opType, orderType, "zkbFePartOrder", userAccount, step);
        }

    }

    public void sendWeChatRemindLetter(ZkbFePartOrder zkbFePartOrder) {
        this.sendMessage(zkbFePartOrder.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART_REMIND_LETTER);
    }

    /**
     * 根据List获取map
     *
     * @param assetInfoList 申请设备列表
     * @return
     */
    public Map<String, Integer> getAssetInfoMap(List<ZkbFePartOrderAssetInfo> assetInfoList) {
        Map<String, Integer> assetInfoMap = new HashMap<>();
        for (ZkbFePartOrderAssetInfo item : assetInfoList) {
            String key = item.getAssetNo() + item.getOrderNo();
            if (assetInfoMap.containsKey(key)) {
                Integer qty = assetInfoMap.get(key);
                assetInfoMap.put(key, qty + item.getAssetQty());
                continue;
            }
            assetInfoMap.put(key, item.getAssetQty());
        }
        return assetInfoMap;
    }

    /**
     * 计算扣除数量
     *
     * @param zkbFePartOrder
     * @param zkbContract
     * @return
     */
    public ZkbFePartOrder calDeductQty(ZkbFePartOrder zkbFePartOrder, ZkbContract zkbContract) {
        if (ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())) {
            return this.calDeductQtyByNew(zkbFePartOrder);
        }
        return this.calDeductQtyByOld(zkbFePartOrder);
    }

    /**
     * 新逻辑扣除数量计算
     *
     * @param zkbFePartOrder
     * @return
     */
    public ZkbFePartOrder calDeductQtyByNew(ZkbFePartOrder zkbFePartOrder) {
        zkbFePartOrder.setQty(1);
        if ("High".equals(zkbFePartOrder.getZkbPoProbeQuotaType())) {
            if (ZkbFePartOrderProbeController.FOUR_IN_ONE.equalsIgnoreCase(zkbFePartOrder.getContractCatSnNo())) {
                zkbFePartOrder.setQty(4);
            } else {
                zkbFePartOrder.setQty(0);
            }
        }
        return zkbFePartOrder;
    }

    /**
     * 旧逻辑扣除数量计算
     *
     * @param zkbFePartOrder
     * @return
     */
    public ZkbFePartOrder calDeductQtyByOld(ZkbFePartOrder zkbFePartOrder) {
        zkbFePartOrder.setQty(0);
        if (ZkbFePartOrderService.testStatusTrueValue.equals(zkbFePartOrder.getHighCatFlag())) {
            if (ZkbFePartOrderProbeController.FOUR_IN_ONE.equalsIgnoreCase(zkbFePartOrder.getContractCatSnNo())) {
                zkbFePartOrder.setQty(4);
            }
        } else {
            if (ZkbPartOrderCat.ValueType.NormalValue.toString().equals(zkbFePartOrder.getZkbPartOrderCat().getValueType())) {
                zkbFePartOrder.setQty(1);
            } else if (ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(zkbFePartOrder.getZkbPartOrderCat().getValueType())) {
                zkbFePartOrder.setQty(2);
            }
        }
        return zkbFePartOrder;
    }

    /**
     * 根据List获取map
     *
     * @param zkbFePartOrderReturnList 返回设备列表
     * @param zkbFePartOrder           流程主表数据
     * @return
     */
    public Map<String, Integer> getZkbFePartOrderReturnMap(List<ZkbFePartOrderReturn> zkbFePartOrderReturnList, ZkbFePartOrder zkbFePartOrder) {
        Map<String, Integer> zkbFePartOrderReturnMap = new HashMap<>();
        for (ZkbFePartOrderReturn item : zkbFePartOrderReturnList) {
            String key = item.getAssetNo() + item.getOrderNo();
            if (zkbFePartOrderReturnMap.containsKey(key)) {
                Integer qty = zkbFePartOrderReturnMap.get(key);
                zkbFePartOrderReturnMap.put(key, qty + item.getQty());
                continue;
            }
            zkbFePartOrderReturnMap.put(key, item.getQty());
            item.setPartOrderId(zkbFePartOrder.getId());
        }
        return zkbFePartOrderReturnMap;
    }

    /**
     * 判断是否隐藏还是显示
     *
     * @param fieldsetType
     * @return
     */
    public Boolean checkRendered(String fieldsetType, ZkbFePartOrder zkbFePartOrder, Map<String, String> configTypeMap, String[] stepAndRoles) {
        if (StringUtils.isBlank(fieldsetType)) {
            return false;
        }
        Map<String, Integer> stepMap = this.stepAndRoleMap(stepAndRoles);
        String currentStep = zkbFePartOrder.getCurrentStep();
        Integer fieldsetTypeIndex = stepMap.get(fieldsetType);
        Integer currentStepIndex = stepMap.get(currentStep);
        if (fieldsetTypeIndex < currentStepIndex) {
            List<ZkbFePartOrderStep> zkbFePartOrderStep = this.zkbFePartOrderStepService.findByPartOrderIdAndStepKey(zkbFePartOrder.getId(), fieldsetType);
            if (CollectionUtils.isNotEmpty(zkbFePartOrderStep)) {
                return true;
            }

        }
        if (fieldsetTypeIndex == currentStepIndex) {
            String[] stepAndRole = stepAndRoles[fieldsetTypeIndex].split(",");
            if (configTypeMap.containsKey(stepAndRole[1])) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Integer> stepAndRoleMap(String[] stepAndRoles) {
        Map<String, Integer> stepMap = new HashMap<>();
        for (int i = 0; i < stepAndRoles.length; i++) {
            String[] stepAndRole = stepAndRoles[i].split(",");
            stepMap.put(stepAndRole[0], i);
        }
        return stepMap;
    }

    /**
     * 判断是否隐藏还是显示
     *
     * @param fieldsetType
     * @return
     */
    public Boolean hasEditRight(String fieldsetType, ZkbFePartOrder zkbFePartOrder, Map<String, String> configTypeMap, String[] stepAndRoles) {
        String currentStep = zkbFePartOrder.getCurrentStep();
        if (!currentStep.equals(fieldsetType)) {
            return false;
        }
        Map<String, Integer> stepMap = this.stepAndRoleMap(stepAndRoles);
        String[] stepAndRole = stepAndRoles[stepMap.get(fieldsetType)].split(",");
        if (configTypeMap.containsKey(stepAndRole[1])) {
            return true;
        }
        return false;
    }

    /**
     * 根据角色判断是否有查看探头说明函条件
     *
     * @param configTypeMap 角色MAP
     * @return
     */
    public boolean hasLetterViewRight(Map<String, String> configTypeMap) {
        boolean right = false;
        if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString())) {
            right = true;
        } else if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())) {
            right = true;
        }else if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Reviewer.toString())) {
            right = true;
        }
        return right;
    }

    /**
     * 根据角色判断是否有查看探头说明函条件
     *
     * @param configTypeMap 角色MAP
     * @return
     */
    public boolean hasAdminRight(Map<String, String> configTypeMap) {
        boolean right = false;
        if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())) {
            right = true;
        }
        return right;
    }

    /**
     * 根据角色判断是否是供应商权限
     *
     * @param configTypeMap 角色MAP
     * @return
     */
    public boolean hasSupplierRight(Map<String, String> configTypeMap) {
        boolean right = false;
        if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString())) {
            right = true;
        }
        return right;
    }

    public boolean hasOrderModifyRight(String orderType,String currentStatus,Map<String, String> configTypeMap) {
        boolean right = false;
        if(orderType.equals(ZkbFePartOrder.OrderType.ManMade.toString())){
            return right;
        }
        if(currentStatus.equals(ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString())){
            if(hasAdminRight(configTypeMap)){
                right = true;
            }
            if(orderType.equals(ZkbFePartOrder.OrderType.SpareParts.toString())){
                if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString())) {
                    right = true;
                }
            }else if(orderType.equals(ZkbFePartOrder.OrderType.Probe.toString())){
                if (configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString())) {
                    right = true;
                }
            }
        }
        return right;
    }

    public boolean hasOrderReturnModifyRight(String orderType,String currentStatus,Map<String, String> configTypeMap) {
        boolean right = false;
        if(currentStatus.equals(ZkbFePartOrder.workFlowStatus.GEAdminAccept.toString())){
            if(orderType.equals(ZkbFePartOrder.OrderType.SpareParts.toString()) || orderType.equals(ZkbFePartOrder.OrderType.Probe.toString())){
                if(hasAdminRight(configTypeMap)){
                    right = true;
                }
            }
        }
        return right;
    }

    /**
     * 根据systemId和 报修单号检索
     *
     * @param systemId    systemId
     * @param sr          报修单号检索
     * @param partOrderNo 订单编号
     * @param assetGroup  设备类型
     */
    public List<ZkbFePartOrder> getSparePartsZkbFePartOrder(String systemId, String sr, String partOrderNo, Integer assetGroup, String orderType) {
        List<ZkbFePartOrder> sparePartsZkbFePartOrderList = null;
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ, ZkbFePartOrder.OrderType.ManMade.toString()));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString()));
        if (StringUtils.isBlank(systemId) && StringUtils.isBlank(sr) && StringUtils.isBlank(partOrderNo)) {
            return sparePartsZkbFePartOrderList;
        }
        if (StringUtils.isNotBlank(systemId)) {
            searchFilters.add(new SearchFilter("systemId", SearchFilter.Operator.LIKE, systemId));
        }
        if (StringUtils.isNotBlank(sr)) {
            searchFilters.add(new SearchFilter("sr", SearchFilter.Operator.LIKE, sr));
        }
        if (StringUtils.isNotBlank(partOrderNo)) {
            searchFilters.add(new SearchFilter("partOrderNo", SearchFilter.Operator.EQ, partOrderNo));
        }
        if (assetGroup != null) {
            searchFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetGroup));
        }
        sparePartsZkbFePartOrderList = this.zkbFePartOrderMapper.queryZkbFePart(searchFilters);
        List<ZkbPartOrder> zkbPartOrderList = null;
        zkbPartOrderList = this.zkbPartOrderMapper.queryBySystemIdAndSr(systemId, sr, partOrderNo, assetGroup);
        if (CollectionUtils.isNotEmpty(zkbPartOrderList)) {
            List<ZkbFePartOrder> list = new ArrayList<>();
            for (ZkbPartOrder item : zkbPartOrderList) {
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
                zkbFePartOrder.setHospitalName(item.getHospitalName());
                zkbFePartOrder.setHospitalClientName(item.getHospitalRepairer());
                zkbFePartOrder.setHospitalClientPhone(item.getHospitalRepairerPhone());
                zkbFePartOrder.setUrgencyStatus(item.getUrgentOrder());
                if ("t".equals(item.getUrgentOrder())) {
                    zkbFePartOrder.setUrgencyStatus("1");
                }

                list.add(zkbFePartOrder);
            }
            if (CollectionUtils.isNotEmpty(sparePartsZkbFePartOrderList)) {
                sparePartsZkbFePartOrderList.addAll(list);
            } else {
                sparePartsZkbFePartOrderList = list;
            }
        }
        if (CollectionUtils.isEmpty(sparePartsZkbFePartOrderList)) {
            if (ZkbFePartOrder.OrderType.Probe.toString().equals(orderType)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("错误：暂未检索到超声类对应报修单"));

            } else {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_searchError"));
            }

        }
        return sparePartsZkbFePartOrderList;
    }


    /**
     * 选择单据
     *
     * @param selected             当前流程数据
     * @param selectZkbFePartOrder 选择的流程
     * @return
     */
    public ZkbFePartOrder selectedWorkFlow(ZkbFePartOrder selected, ZkbFePartOrder selectZkbFePartOrder) {
        selected.setFatherPartOrderNo(selectZkbFePartOrder.getPartOrderNo());
        selected.setSystemId(selectZkbFePartOrder.getSystemId());
        selected.setContractId(selectZkbFePartOrder.getContractId());
        selected.setPhenomenon(selectZkbFePartOrder.getPhenomenon());
        selected.setFunctionType(selectZkbFePartOrder.getFunctionType());
        selected.setMachineStatus(selectZkbFePartOrder.getMachineStatus());
        selected.setUrgencyStatus(selectZkbFePartOrder.getUrgencyStatus());
        selected.setSubtractTotalStatus(selectZkbFePartOrder.getSubtractTotalStatus());
        selected.setHospitalName(selectZkbFePartOrder.getHospitalName());
        selected.setHospitalClientName(selectZkbFePartOrder.getHospitalClientName());
        selected.setHospitalClientPhone(selectZkbFePartOrder.getHospitalClientPhone());
        ZkbContract zkbContract = this.zkbContractRepository.findById(selectZkbFePartOrder.getContractId());
        selected.setTenantUid(zkbContract.getTenantUID());
        List<AssetInfo> assetInfoList = this.assetInfoMapper.fetchByAssetInfoBySystemId(selectZkbFePartOrder.getSystemId(), selectZkbFePartOrder.getContractId());
        if (CollectionUtils.isNotEmpty(assetInfoList)) {
            AssetInfo assetInfo = assetInfoList.get(0);
            selected.setArtificialType("");
            if (assetInfo.getAssetGroup() != null && "2".equals(assetInfo.getAssetGroup().toString())) {
                selected.setArtificialType(highArtificialTypeValue);
            }
            selected.setAssetGroup(assetInfo.getAssetGroup().toString());
            selected.setProductNumber(assetInfo.getFunctionType());
            selected.setAssetType(assetInfo.getAssetType());
            if (assetTypeHighMachineValue.equals(assetInfo.getAssetType())) {
                selected.setArtificialType(highArtificialTypeValue);
            }
        }
        return selected;
    }

    /**
     * 生成excel
     *
     * @param doc     文件
     * @param dataMap 要导出的map
     */
    public void exportData(ExcelDocument doc, HashMap<String, Object> dataMap) {
        Integer keyRow = 1;
        Integer dataRow = 2;

        List<Map<String, Object>> zkbManMadeFePartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbManMadeFePartOrderDataList");
        List<Map<String, Object>> zkbProbeFePartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbProbeFePartOrderDataList");
        List<Map<String, Object>> zkbSparePartFePartOrderDataList = (List<Map<String, Object>>) dataMap.get("zkbSparePartFePartOrderDataList");
        if (CollectionUtils.isNotEmpty(zkbManMadeFePartOrderDataList)) {
            for (Map<String, Object> item : zkbManMadeFePartOrderDataList) {
                doc.writeRowData(excelSheetNameOfManMade, keyRow, dataRow++, item);
            }
        }
        dataRow = 2;
        if (CollectionUtils.isNotEmpty(zkbProbeFePartOrderDataList)) {
            for (Map<String, Object> item : zkbProbeFePartOrderDataList) {
                doc.writeRowData(excelSheetNameOfProbe, keyRow, dataRow++, item);
            }
        }
        dataRow = 2;
        if (CollectionUtils.isNotEmpty(zkbSparePartFePartOrderDataList)) {
            for (Map<String, Object> item : zkbSparePartFePartOrderDataList) {
                doc.writeRowData(excelSheetNameOfSparePart, keyRow, dataRow++, item);
            }
        }
    }

    /**
     * 批量获取合同信息
     *
     * @param zkbFePartOrderList 流程列表
     * @return
     */
    public Map<String, ZkbContract> getContractMap(List<ZkbFePartOrder> zkbFePartOrderList) {
        Map<String, ZkbContract> contractMap = new HashMap<>();
        if (CollectionUtils.isEmpty(zkbFePartOrderList))
            return contractMap;
        Set<String> contractIdSet = new HashSet<>();
        for (ZkbFePartOrder item : zkbFePartOrderList) {
            if (StringUtils.isBlank(item.getContractId()))
                continue;
            contractIdSet.add(item.getContractId());
        }
        if (CollectionUtils.isEmpty(contractIdSet))
            return contractMap;
        List<ZkbContract> zkbContractList = this.zkbContractRepository.findByIdIn(contractIdSet);
        if (CollectionUtils.isEmpty(zkbContractList))
            return contractMap;
        for (ZkbContract item : zkbContractList)
            contractMap.put(item.getId(), item);
        return contractMap;
    }

    /**
     * 批量获取租户信息
     *
     * @param zkbFePartOrderList
     * @return
     */
    public Map<String, TenantInfo> getTenantInfoMap(List<ZkbFePartOrder> zkbFePartOrderList) {
        Map<String, TenantInfo> tenantInfoMap = new HashMap<>();
        if (CollectionUtils.isEmpty(zkbFePartOrderList))
            return tenantInfoMap;
        Set<String> tenantUidSet = new HashSet<>();
        for (ZkbFePartOrder item : zkbFePartOrderList) {
            if (StringUtils.isBlank(item.getTenantUid()))
                continue;
            tenantUidSet.add(item.getTenantUid());
        }
        if (CollectionUtils.isEmpty(tenantUidSet))
            return tenantInfoMap;
        List<TenantInfo> tenantInfoList = this.tenantInfoRepository.findByUidIn(tenantUidSet);
        if (CollectionUtils.isEmpty(tenantInfoList))
            return tenantInfoMap;
        for (TenantInfo item : tenantInfoList)
            tenantInfoMap.put(item.getUid(), item);
        return tenantInfoMap;
    }

    /**
     * 批量获取合同信息
     *
     * @param zkbFePartOrderList 流程列表
     * @return
     */
    public Map<String, ZkbContract> getContractMapForZkbPartOrder(List<ZkbPartOrder> zkbFePartOrderList) {
        Map<String, ZkbContract> contractMap = new HashMap<>();
        if (CollectionUtils.isEmpty(zkbFePartOrderList))
            return contractMap;
        Set<String> contractIdSet = new HashSet<>();
        for (ZkbPartOrder item : zkbFePartOrderList) {
            if (StringUtils.isBlank(item.getContractId()))
                continue;
            contractIdSet.add(item.getContractId());
        }
        if (CollectionUtils.isEmpty(contractIdSet))
            return contractMap;
        List<ZkbContract> zkbContractList = this.zkbContractRepository.findByIdIn(contractIdSet);
        if (CollectionUtils.isEmpty(zkbContractList))
            return contractMap;
        for (ZkbContract item : zkbContractList)
            contractMap.put(item.getId(), item);
        return contractMap;
    }

    /**
     * 批量获取租户信息
     *
     * @param zkbFePartOrderList
     * @return
     */
    public Map<String, TenantInfo> getTenantInfoMapForZkbPartOrder(List<ZkbPartOrder> zkbFePartOrderList) {
        Map<String, TenantInfo> tenantInfoMap = new HashMap<>();
        if (CollectionUtils.isEmpty(zkbFePartOrderList))
            return tenantInfoMap;
        Set<String> tenantUidSet = new HashSet<>();
        for (ZkbPartOrder item : zkbFePartOrderList) {
            if (StringUtils.isBlank(item.getTenantUID()))
                continue;
            tenantUidSet.add(item.getTenantUID());
        }
        if (CollectionUtils.isEmpty(tenantUidSet))
            return tenantInfoMap;
        List<TenantInfo> tenantInfoList = this.tenantInfoRepository.findByUidIn(tenantUidSet);
        if (CollectionUtils.isEmpty(tenantInfoList))
            return tenantInfoMap;
        for (TenantInfo item : tenantInfoList)
            tenantInfoMap.put(item.getUid(), item);
        return tenantInfoMap;
    }


    /**
     * 根据订单列表生成MAP
     *
     * @param zkbFePartOrderList 订单列表
     * @return
     */
    public HashMap<String, Object> createZkbFePartOrderData(List<ZkbFePartOrder> zkbFePartOrderList) {
        List<Map<String, Object>> zkbManMadeFePartOrderDataList = new ArrayList<>();
        List<Map<String, Object>> zkbProbeFePartOrderDataList = new ArrayList<>();
        List<Map<String, Object>> zkbSparePartFePartOrderDataList = new ArrayList<>();
        Map<String, Map<String, String>> i18Map = batchI18MapForPartOrder();
        Map<String, ZkbContract> contractMap = this.getContractMap(zkbFePartOrderList);
        Map<String, TenantInfo> tenantInfoMap = this.getTenantInfoMap(zkbFePartOrderList);
        if (CollectionUtils.isNotEmpty(zkbFePartOrderList)) {
            for (ZkbFePartOrder item : zkbFePartOrderList) {
                if (ZkbFePartOrder.OrderType.ManMade.toString().equals(item.getOrderType())) {
                    HashMap<String, Object> map = this.getManMadeHashMap(item, i18Map, contractMap, tenantInfoMap);
                    zkbManMadeFePartOrderDataList.add(map);
                } else if (ZkbFePartOrder.OrderType.Probe.toString().equals(item.getOrderType())) {
                    HashMap<String, Object> map = this.getProbeHashMap(item, i18Map, contractMap, tenantInfoMap);
                    zkbProbeFePartOrderDataList.add(map);
                } else if (ZkbFePartOrder.OrderType.SpareParts.toString().equals(item.getOrderType())) {
                    List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = this.zkbFePartOrderAssetInfoRepository.findByFePartOrderId(item.getId());
                    if (CollectionUtils.isNotEmpty(zkbFePartOrderAssetInfoList)) {
                        for (ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfo : zkbFePartOrderAssetInfoList) {
                            List<HashMap<String, Object>> map = this.getSparePartHashMap(item, i18Map, zkbFePartOrderAssetInfo, contractMap, tenantInfoMap);
                            zkbSparePartFePartOrderDataList.addAll(map);
                        }
                    } else {
                        List<HashMap<String, Object>> map = this.getSparePartHashMap(item, i18Map, null, contractMap, tenantInfoMap);
                        zkbSparePartFePartOrderDataList.addAll(map);
                    }
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("zkbManMadeFePartOrderDataList", zkbManMadeFePartOrderDataList);
        map.put("zkbProbeFePartOrderDataList", zkbProbeFePartOrderDataList);
        map.put("zkbSparePartFePartOrderDataList", zkbSparePartFePartOrderDataList);
        return map;
    }

    public Map<String, Map<String, String>> batchI18MapForPartOrder() {
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
        typeList.add(gefeOrderType);
        Map<String, Map<String, String>> i18Map = this.getI18ToMap(typeList);
        return i18Map;
    }

    /**
     * @param strList
     * @return
     */
    public Map<String, Map<String, String>> getI18ToMap(List<String> strList) {
        Map<String, Map<String, String>> i18Map = new HashMap<>();
        if (CollectionUtils.isEmpty(strList)) {
            return i18Map;
        }
        for (String item : strList) {
            List<I18nMessage> i18nMessageList = this.i18nMessageRepository.getByTypeAndSiteId(item, -1);
            if (CollectionUtils.isEmpty(i18nMessageList)) continue;
            Map<String, String> map = new HashMap();
            for (I18nMessage i18nMessage : i18nMessageList) {
                if (map.containsKey(i18nMessage.getMsgKey())) continue;
                map.put(i18nMessage.getMsgKey(), i18nMessage.getValueZh());
            }
            if (i18Map.containsKey(item)) continue;
            i18Map.put(item, map);
        }
        return i18Map;
    }

    /**
     * 把I18组成map
     *
     * @param type
     * @return
     */
    public Map<String, String> getI18ToMap(String type) {
        Map<String, String> i18Map = new HashMap<>();
        List<I18nMessage> i18nMessageList = this.i18nMessageRepository.getByTypeAndSiteId(type, -1);
        for (I18nMessage i18nMessage : i18nMessageList) {
            if (i18Map.containsKey(i18nMessage.getValueZh())) continue;
            i18Map.put(i18nMessage.getValueZh(), i18nMessage.getMsgKey());
        }
        return i18Map;
    }

    /**
     * 把I18组成map
     *
     * @param type
     * @return
     */
    public Map<String, String> getI18ToMapEngKey(String type) {
        Map<String, String> i18Map = new HashMap<>();
        List<I18nMessage> i18nMessageList = this.i18nMessageRepository.getByTypeAndSiteId(type, -1);
        for (I18nMessage i18nMessage : i18nMessageList) {
            if (i18Map.containsKey(i18nMessage.getValueZh())) continue;
            i18Map.put(i18nMessage.getMsgKey(), i18nMessage.getValueZh());
        }
        return i18Map;
    }

    /**
     * 根据人工申请生成HashMap
     *
     * @param zkbFePartOrder 人工申请List
     * @param i18Map         i18 Map集合
     * @return
     */
    public HashMap<String, Object> getManMadeHashMap(ZkbFePartOrder zkbFePartOrder, Map<String, Map<String, String>> i18Map, Map<String, ZkbContract> contractMap, Map<String, TenantInfo> tenantInfoMap) {
        HashMap<String, Object> manMadeMap = new HashMap();
        if (zkbFePartOrder == null) {
            return manMadeMap;
        }
        this.fullContractAndTenantInfo(contractMap, tenantInfoMap, manMadeMap, zkbFePartOrder.getContractId(), zkbFePartOrder.getTenantUid());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"), zkbFePartOrder.getSystemId());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_orderNo"), zkbFePartOrder.getPartOrderNo());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_reportUser"), zkbFePartOrder.getReportUser());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_reportDate"), this.dateFormat(zkbFePartOrder.getReportDate(), dateFormat));
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_hospitalName"), zkbFePartOrder.getHospitalName());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_hospitalClientName"), zkbFePartOrder.getHospitalClientName());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_hospitalClientPhone"), zkbFePartOrder.getHospitalClientPhone());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_functionType"), zkbFePartOrder.getFunctionType());
        if (i18Map.containsKey(machineStatus) && i18Map.get(machineStatus).containsKey(zkbFePartOrder.getMachineStatus())) {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_machineStatus"), i18Map.get(machineStatus).get(zkbFePartOrder.getMachineStatus()));
        }
        if (i18Map.containsKey(urgencyStatus) && i18Map.get(urgencyStatus).containsKey(zkbFePartOrder.getUrgencyStatus())) {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_urgencyStatus"), i18Map.get(urgencyStatus).get(zkbFePartOrder.getUrgencyStatus()));
        }
        if (i18Map.containsKey(subtractTotalStatus) && i18Map.get(subtractTotalStatus).containsKey(zkbFePartOrder.getSubtractTotalStatus())) {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_subtractTotalStatus"), i18Map.get(subtractTotalStatus).get(zkbFePartOrder.getSubtractTotalStatus()));
        }
        if (i18Map.containsKey(status) && i18Map.get(status).containsKey(zkbFePartOrder.getStatus())) {
            manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"), i18Map.get(status).get(zkbFePartOrder.getStatus()));
        }
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"), zkbFePartOrder.getPhenomenon());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"), zkbFePartOrder.getTenantAssignUser());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"), this.dateFormat(zkbFePartOrder.getTenantAssignDate(), dateFormat));
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_comment"), zkbFePartOrder.getComment());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_sr"), zkbFePartOrder.getSr());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_act"), zkbFePartOrder.getAct());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_geEngineerName"), zkbFePartOrder.getGeEngineerName());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_geEngineerPhone"), zkbFePartOrder.getGeEngineerPhone());
        manMadeMap.put(WebUtil.getMessage("zkbFePartOrder_fourHundredComment"), zkbFePartOrder.getRemark());
        return manMadeMap;
    }

    /**
     * 生成需要归还的配件信息
     *
     * @param zkbFePartOrder 配件信息
     * @return
     */
    public HashMap<String, Object> getReturnSparePartHashMap(ZkbFePartOrder zkbFePartOrder) {
        HashMap<String, Object> sparePartMap = new HashMap();
        if (zkbFePartOrder == null) {
            return sparePartMap;
        }
        sparePartMap.put("id", zkbFePartOrder.getId());
        sparePartMap.put(WebUtil.getMessage("zkbContact_tenant"), zkbFePartOrder.getTenantName());
        sparePartMap.put(WebUtil.getMessage("zkbContract_contractNo"), zkbFePartOrder.getContractNo());
        sparePartMap.put(WebUtil.getMessage("zkbContract_region"), zkbFePartOrder.getRegion());
        sparePartMap.put(WebUtil.getMessage("zkbContract_contractType"), zkbFePartOrder.getContractType());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"), zkbFePartOrder.getSystemId());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNo"), zkbFePartOrder.getPartOrderNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetNo"), zkbFePartOrder.getAssetNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetDescription"), zkbFePartOrder.getAssetDescription());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"), zkbFePartOrder.getCatNum());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"), zkbFePartOrder.getOrderNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"), zkbFePartOrder.getPrice());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_remark"), zkbFePartOrder.getComment());
        return sparePartMap;
    }

    /**
     * 生成需要归还的探头信息
     *
     * @param zkbFePartOrder 配件信息
     * @return
     */
    public HashMap<String, Object> getReturnProbeHashMap(ZkbFePartOrder zkbFePartOrder, Map<String, Map<String, String>> i18Map) {
        HashMap<String, Object> sparePartMap = new HashMap();
        if (zkbFePartOrder == null) {
            return sparePartMap;
        }
        sparePartMap.put(WebUtil.getMessage("zkbContact_tenant"), zkbFePartOrder.getTenantName());
        sparePartMap.put(WebUtil.getMessage("zkbContract_contractNo"), zkbFePartOrder.getContractNo());
        sparePartMap.put(WebUtil.getMessage("zkbContract_region"), zkbFePartOrder.getRegion());
        sparePartMap.put(WebUtil.getMessage("zkbContract_contractType"), zkbFePartOrder.getContractType());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"), zkbFePartOrder.getSystemId());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNo"), zkbFePartOrder.getPartOrderNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_catNo"), zkbFePartOrder.getCatNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_zkbPartOrderCat"), zkbFePartOrder.getCatModel());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_valueType"), i18Map.get(zkbPoValueType).get(zkbFePartOrder.getValueType()));
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_oldCatSn"), zkbFePartOrder.getOldCatSn());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"), "1");
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"), zkbFePartOrder.getOrderNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"), zkbFePartOrder.getPrice());
        return sparePartMap;
    }

    public List<Map<String, Object>> getZkbFePartOrderSparePartMapFromTemplate(ExcelDocument doc, Map<String, String> i18Map) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_ORDER_HISTORY_DATA_ROW; i <= doc.getWorkBook().getSheet(excelSheetNameOfSparePartReturn).getLastRowNum() + 1; i++) {
            Map<String, Object> rowMap = doc.getDataRowMap(excelSheetNameOfSparePartReturn, EXCEL_FE_PART_ORDER_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbFePartOrderVO zkbFePartOrderVO = new ZkbFePartOrderVO();
            zkbFePartOrderVO.setLineNum(i);
            zkbFePartOrderVO.setSheetName("备件归还");
            ZkbFePartOrderRow zkbFePartOrderRow = new ZkbFePartOrderRow(zkbFePartOrderVO, rowMap);
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_orderNo"), "setPartOrderNo");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_assetNo"), "setAssetNo");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_orderNoa"), "setOrderNo");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_returnType"), "setReturnType");
            zkbFePartOrderRow.setFieldIntegerValue("归还数量", "setQty");
            zkbFePartOrderRow.setFieldStringValue("归还备注", "setGeAdminComment");
            if (zkbFePartOrderVO.isSparePartNull()) {
                break;
            }
            if (!i18Map.containsKey(zkbFePartOrderVO.getReturnType())) {
                zkbFePartOrderVO.setReturnType(null);
            } else {
                zkbFePartOrderVO.setReturnType(i18Map.get(zkbFePartOrderVO.getReturnType()));
            }

            map.put("zkbFePartOrderVO", zkbFePartOrderVO);
            map.put("rowData", rowMap);
            map.put("status", ZkbOrderHistoryService.ImportStatus.New.name());
            map.put("hashCode", zkbFePartOrderVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getOrderHistoryMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }

    public List<Map<String, Object>> getZkbFePartOrderProbeMapFromTemplate(ExcelDocument doc, Map<String, String> i18Map) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_ORDER_HISTORY_DATA_ROW; i <= doc.getWorkBook().getSheet(excelSheetNameOfProbeReturn).getLastRowNum() + 1; i++) {
            Map<String, Object> rowMap = doc.getDataRowMap(excelSheetNameOfProbeReturn, EXCEL_FE_PART_ORDER_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbFePartOrderVO zkbFePartOrderVO = new ZkbFePartOrderVO();
            zkbFePartOrderVO.setLineNum(i);
            zkbFePartOrderVO.setSheetName("探头归还");
            ZkbFePartOrderRow zkbFePartOrderRow = new ZkbFePartOrderRow(zkbFePartOrderVO, rowMap);
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_orderNo"), "setPartOrderNo");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_returnType"), "setReturnType");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_partSerialNumber"), "setPartSerialNumber");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_partQualityIssue"), "setPartQualityIssue");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_airWayBillNo"), "setWaybillNo");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_newCatSn"), "setNewCatSn");
            zkbFePartOrderRow.setFieldStringValue(WebUtil.getMessage("zkbFePartOrder_remark"), "setGeAdminComment");
            zkbFePartOrderRow.setFieldDateValue(WebUtil.getMessage("zkbFePartOrder_returnDate1"), "setReturnDate");
            if (zkbFePartOrderVO.isProbeNull()) {
                break;
            }
            if (!i18Map.containsKey(zkbFePartOrderVO.getReturnType())) {
                zkbFePartOrderVO.setReturnType(null);
            } else {
                zkbFePartOrderVO.setReturnType(i18Map.get(zkbFePartOrderVO.getReturnType()));
            }
            map.put("zkbFePartOrderVO", zkbFePartOrderVO);
            map.put("rowData", rowMap);
            map.put("status", ZkbOrderHistoryService.ImportStatus.New.name());
            map.put("hashCode", zkbFePartOrderVO.hashCode());
            result.add(map);
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getOrderHistoryMapFromTemplate: {} seconds", duration.getSeconds());
        return result;
    }


    /**
     * 批量归还配件探头信息
     *
     * @param importSparePartList 批量配件信息
     * @param importProbeList     批量探头信息
     */
    public void importData(List<Map<String, Object>> importSparePartList, List<Map<String, Object>> importProbeList, String userId, String userName, String tenantUid, UserAccount userAccount, Map<String, String> i18Map) {
        Boolean validate = true;
        LocalDateTime startTime = LocalDateTime.now();
        List<ZkbFePartOrder> zkbFePartOrderSparePartList = new ArrayList<>();
        List<ZkbFePartOrder> zkbFePartOrderProbeList = new ArrayList<>();
        int row = 0;
        try {
            for (Map<String, Object> item : importSparePartList) {
                ++row;
                if (checkSparePartData(item)) {
                    ZkbFePartOrderVO zkbFePartOrderVO = (ZkbFePartOrderVO) item.get("zkbFePartOrderVO");
                    ZkbFePartOrder zkbFePartOrder = new ZkbFePartOrder();
                    BeanUtils.copyProperties(zkbFePartOrderVO, zkbFePartOrder);
                    zkbFePartOrderSparePartList.add(zkbFePartOrder);
                } else {
                    validate = false;
                }
            }
            row = 0;
            for (Map<String, Object> item : importProbeList) {
                ++row;
                if (checkProbeData(item)) {
                    ZkbFePartOrderVO zkbFePartOrderVO = (ZkbFePartOrderVO) item.get("zkbFePartOrderVO");
                    ZkbFePartOrder zkbFePartOrder = new ZkbFePartOrder();
                    BeanUtils.copyProperties(zkbFePartOrderVO, zkbFePartOrder);
                    zkbFePartOrderProbeList.add(zkbFePartOrder);
                } else {
                    validate = false;
                }
            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }
        if (validate) {
            if (CollectionUtils.isNotEmpty(zkbFePartOrderProbeList)) {
                for (ZkbFePartOrder item : zkbFePartOrderProbeList) {
                    ZkbFePartOrder zkbFePartOrder = this.zkbFePartOrderRepository.findByPartOrderNo(item.getPartOrderNo()).get(0);
                    zkbFePartOrder.setReturnType(item.getReturnType());
                    if ("DOA".equals(zkbFePartOrder.getReturnType())) {
                        zkbFePartOrder.setPartSerialNumber(item.getPartSerialNumber());
                        zkbFePartOrder.setPartQualityIssue(item.getPartQualityIssue());
                    }
                    zkbFePartOrder.setWaybillNo(item.getWaybillNo());
                    zkbFePartOrder.setReturnDate(item.getReturnDate());
                    zkbFePartOrder.setNewCatSn(item.getNewCatSn());
                    zkbFePartOrder.setGeAdminComment(item.getGeAdminComment());
                    ZkbContract zkbContract = this.zkbContractRepository.findById(zkbFePartOrder.getContractId());
                    boolean showZkbPoProbeQuotaType = false;
                    if (ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())) {
                        showZkbPoProbeQuotaType = true;
                    }
                    this.geAdminReturn(zkbFePartOrder, showZkbPoProbeQuotaType, userId, userName, tenantUid, "", userAccount, i18Map);
                }
            }

            if (CollectionUtils.isNotEmpty(zkbFePartOrderSparePartList)) {
                for (ZkbFePartOrder item : zkbFePartOrderSparePartList) {
                    ZkbFePartOrder item1 = this.zkbFePartOrderRepository.findByPartOrderNo(item.getPartOrderNo()).get(0);
                    ZkbFePartOrderReturn zkbFePartOrderReturn = new ZkbFePartOrderReturn();
                    zkbFePartOrderReturn.setAssetNo(item.getAssetNo());
                    zkbFePartOrderReturn.setOrderNo(item.getOrderNo());
                    zkbFePartOrderReturn.setPartOrderId(item1.getId());
                    zkbFePartOrderReturn.setQty(item.getQty());
                    zkbFePartOrderReturn.setReturnDate(new Date());
                    zkbFePartOrderReturn.setReturnType(item.getReturnType());
                    zkbFePartOrderReturn.setComment(item.getGeAdminComment());
                    this.zkbFePartOrderReturnRepository.save(zkbFePartOrderReturn);
                    String status = this.changeStatus(item1.getId(), userId, userName, tenantUid);
                    if (ZkbFePartOrderService.highArtificialTypeValue.equals(item1.getArtificialType()) || ZkbFePartOrderSparePartDetailController.badReturnValue.equals(item.getReturnType())) {
                        continue;
                    }
                    ZkbContract zkbContract = this.zkbContractRepository.findById(item1.getContractId());
                    this.moveSpareQuota(3, zkbContract, item.getQty(), userAccount, item1, i18Map.get(status));
                }
            }
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    /**
     * 修改状态
     *
     * @param id
     * @param userId
     * @param userName
     * @param tenantUid
     * @return
     */
    public String changeStatus(String id, String userId, String userName, String tenantUid) {
        ZkbFePartOrder zkbFePartOrder = this.zkbFePartOrderRepository.findById(id);
        Integer returnCount = zkbFePartOrderReturnRepository.fetchQty(id); //归还数量
        Integer applyCount = this.zkbFePartOrderAssetInfoRepository.fetchQty(id); //申请数量
        String workFlowStatus = "";
        String workFlowSteps = "";
        Date startDate = null;
        Date endDate = null;
        GatewayURL gatewayURL = null;
        if (returnCount >= applyCount) {
            workFlowStatus = ZkbFePartOrder.workFlowStatus.GEAdminAccept.toString();
            workFlowSteps = ZkbFePartOrder.workFlowSteps.Complete.toString();
            startDate = new Date();
            endDate = new Date();
            gatewayURL = GatewayURL.FE_PART_ORDER_SEND_WECHART;
        } else if (returnCount > 0) {
            workFlowStatus = ZkbFePartOrder.workFlowStatus.GEAdminAcceptPart.toString();
            workFlowSteps = ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString();
            startDate = new Date();
            endDate = null;
            gatewayURL = GatewayURL.FE_PART_ORDER_SEND_WECHART;
        }
        this.wolkFlowToComplete(
                zkbFePartOrder,
                userId,
                userName,
                tenantUid,
                "",
                workFlowStatus,
                workFlowSteps, startDate, endDate, gatewayURL);
        return workFlowStatus;
    }

    public Date getDateFormat(String format, String formatDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param zkbFePartOrder
     * @param showZkbPoProbeQuotaType
     * @param userUid
     * @param userName
     * @param tenantUID
     * @param queryStatus
     * @param userAccount
     * @param i18Map
     * @return
     */
    public String geAdminReturn(ZkbFePartOrder zkbFePartOrder,
                                boolean showZkbPoProbeQuotaType,
                                String userUid,
                                String userName,
                                String tenantUID,
                                String queryStatus,
                                UserAccount userAccount,
                                Map<String, String> i18Map) {
        ZkbContract zkbContract = this.zkbContractRepository.findById(zkbFePartOrder.getContractId());
        if (!ZkbFePartOrderSparePartDetailController.badReturnValue.equals(zkbFePartOrder.getReturnType())) {
            if (zkbFePartOrder.getQty() != null && zkbFePartOrder.getQty() > 0) {
                this.moveProbe(5, zkbContract, zkbFePartOrder.getQty(), showZkbPoProbeQuotaType, zkbFePartOrder.getZkbPoProbeQuotaType(), userAccount, zkbFePartOrder, true, i18Map.get("GEAdminAccept"));
            }
            if (StringUtils.isNotBlank(zkbFePartOrder.getContractCatId())) {
                this.updateZkbContractCat(userAccount, true, zkbFePartOrder.getContractCatId());
            }
        }
        Date date = new Date();
        return this.wolkFlowToComplete(
                zkbFePartOrder,
                userUid,
                userName,
                tenantUID,
                queryStatus,
                ZkbFePartOrder.workFlowStatus.GEAdminAccept.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), date, date, GatewayURL.FE_PART_ORDER_SEND_WECHART);
    }


    /**
     * 整理错误信息
     *
     * @param importSparePartList 备件错误信息
     * @param importProbeList     探头错误信息
     * @return
     */
    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importSparePartList, List<Map<String, Object>> importProbeList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : importSparePartList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbCourseScheduleService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbCourseScheduleService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        for (Map<String, Object> map : importProbeList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }
            if (ZkbCourseScheduleService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbCourseScheduleService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }


    /**
     * 检查批量配件归还数据是否合法
     *
     * @param item 批量配件
     * @return
     */
    public boolean checkSparePartData(Map<String, Object> item) {

        boolean isValidateData = true;
        ZkbFePartOrderVO zkbFePartOrderVO = (ZkbFePartOrderVO) item.get("zkbFePartOrderVO");
        AssetInfoRow.clearErrMessage(item);
        String errorMessage = "不能为空";
        if (StringUtils.isBlank(zkbFePartOrderVO.getPartOrderNo())) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbFePartOrder_orderNo") + errorMessage);
            isValidateData = false;
        }
        if (StringUtils.isBlank(zkbFePartOrderVO.getAssetNo())) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbFePartOrder_assetNo") + errorMessage);
            isValidateData = false;
        }
        if (StringUtils.isBlank(zkbFePartOrderVO.getOrderNo())) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbFePartOrder_orderNoa") + errorMessage);
            isValidateData = false;
        }
        if (StringUtils.isBlank(zkbFePartOrderVO.getReturnType())) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbFePartOrder_returnType") + errorMessage);
            isValidateData = false;
        }
        if (zkbFePartOrderVO.getQty() == null) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, "归还数量" + errorMessage);
            isValidateData = false;
        }
        if (StringUtils.isNotBlank(zkbFePartOrderVO.getPartOrderNo())) {
            List<ZkbFePartOrder> zkbFePartOrderList = this.zkbFePartOrderRepository.findByPartOrderNoAndOrderType(
                    zkbFePartOrderVO.getPartOrderNo(),
                    ZkbFePartOrder.OrderType.SpareParts.toString());
            boolean continueValid = true;
            if (CollectionUtils.isEmpty(zkbFePartOrderList)) {
                item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
                AssetInfoRow.addErrMessage(item, "未找到订单编号《" + zkbFePartOrderVO.getPartOrderNo() + "》备件申请流程");
                isValidateData = false;
                continueValid = false;
            }
            if (continueValid) {
                ZkbFePartOrder zkbFePartOrder = zkbFePartOrderList.get(0);
                if (!ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString().equals(zkbFePartOrder.getCurrentStep())) {
                    item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
                    AssetInfoRow.addErrMessage(item, "订单编号《" + zkbFePartOrderVO.getPartOrderNo() + "》备件申请流程暂无需归还配件");
                    isValidateData = false;
                    continueValid = false;
                }
            }
            if (continueValid
                    && StringUtils.isNotBlank(zkbFePartOrderVO.getOrderNo())
                    && StringUtils.isNotBlank(zkbFePartOrderVO.getAssetNo())) {
                List<ZkbFePartOrder> zkbFePartOrderList1 = this.zkbFePartOrderRepository.findNeedReturnPartOrder(zkbFePartOrderVO.getPartOrderNo(), zkbFePartOrderVO.getOrderNo(), zkbFePartOrderVO.getAssetNo());
                if (CollectionUtils.isEmpty(zkbFePartOrderList1)) {
                    item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
                    AssetInfoRow.addErrMessage(item,
                            "在订单编号《" + zkbFePartOrderVO.getPartOrderNo() +
                                    "》备件申请流程中未找到" + WebUtil.getMessage("zkbFePartOrder_assetNo") +
                                    "《" + zkbFePartOrderVO.getAssetNo() +
                                    "》，" + WebUtil.getMessage("zkbFePartOrder_orderNoa") + "《" + zkbFePartOrderVO.getOrderNo() + "》配件");
                    isValidateData = false;
                    continueValid = false;
                }
            }
            if (continueValid
                    && StringUtils.isNotBlank(zkbFePartOrderVO.getOrderNo())
                    && StringUtils.isNotBlank(zkbFePartOrderVO.getAssetNo())
                    && zkbFePartOrderVO.getQty() != null) {
                ZkbFePartOrder zkbFePartOrder = zkbFePartOrderList.get(0);
                Integer qty = this.zkbFePartOrderAssetInfoRepository.fetchQty(zkbFePartOrder.getId(), zkbFePartOrderVO.getAssetNo(), zkbFePartOrderVO.getOrderNo());
                Integer returnQty = this.zkbFePartOrderReturnRepository.allReturnQty(zkbFePartOrderVO.getAssetNo(), zkbFePartOrderVO.getOrderNo(), zkbFePartOrder.getId());
                qty = qty == null ? 0 : qty;
                returnQty = returnQty == null ? 0 : returnQty;
                Integer currentReturnQty = zkbFePartOrderVO.getQty();
                if (currentReturnQty > qty - returnQty) {
                    item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
                    AssetInfoRow.addErrMessage(item, "归还数量不能大于未归还数量");
                    isValidateData = false;
                }
            }
        }
        return isValidateData;
    }

    /**
     * 检查批量配件归还数据是否合法
     *
     * @param item 批量配件
     * @return
     */
    public boolean checkProbeData(Map<String, Object> item) {

        boolean isValidateData = true;
        ZkbFePartOrderVO zkbFePartOrderVO = (ZkbFePartOrderVO) item.get("zkbFePartOrderVO");
        AssetInfoRow.clearErrMessage(item);
        String errorMessage = "不能为空";
        if (StringUtils.isBlank(zkbFePartOrderVO.getPartOrderNo())) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbFePartOrder_orderNo") + errorMessage);
            isValidateData = false;
        }
        if (StringUtils.isBlank(zkbFePartOrderVO.getReturnType())) {
            item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
            AssetInfoRow.addErrMessage(item, WebUtil.getMessage("zkbFePartOrder_returnType") + errorMessage);
            isValidateData = false;
        }
        if (StringUtils.isNotBlank(zkbFePartOrderVO.getPartOrderNo())) {
            List<ZkbFePartOrder> zkbFePartOrderList = this.zkbFePartOrderRepository.findByPartOrderNoAndOrderType(
                    zkbFePartOrderVO.getPartOrderNo(),
                    ZkbFePartOrder.OrderType.Probe.toString());
            boolean continueValid = true;
            if (CollectionUtils.isEmpty(zkbFePartOrderList)) {
                item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
                AssetInfoRow.addErrMessage(item, "未找到订单编号《" + zkbFePartOrderVO.getPartOrderNo() + "》探头申请流程");
                isValidateData = false;
                continueValid = false;
            }
            if (continueValid) {
                ZkbFePartOrder zkbFePartOrder = zkbFePartOrderList.get(0);
                if (!ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString().equals(zkbFePartOrder.getCurrentStep())) {
                    item.put("status", ZkbCourseScheduleService.ImportStatus.Failure);
                    AssetInfoRow.addErrMessage(item, "订单编号《" + zkbFePartOrderVO.getPartOrderNo() + "》探头申请流程暂无需归还探头");
                    isValidateData = false;
                    continueValid = false;
                }
            }
        }
        return isValidateData;
    }


    /**
     * 组装租户信息和合同信息
     *
     * @param contractMap   合同集合
     * @param tenantInfoMap 租户集合
     * @param map           导出数据集合
     * @param contractId    合同ID
     * @param tenantUid     租户tenant uid
     */
    public void fullContractAndTenantInfo(Map<String, ZkbContract> contractMap, Map<String, TenantInfo> tenantInfoMap, Map<String, Object> map, String contractId, String tenantUid) {
        ZkbContract zkbContract = contractMap.get(contractId);
        if (zkbContract != null) {
            map.put(WebUtil.getMessage("zkbContract_contractNo"), zkbContract.getContractNo());
            map.put(WebUtil.getMessage("zkbContract_region"), zkbContract.getRegion());
            map.put(WebUtil.getMessage("zkbContract_contractType"), zkbContract.getContractType());
        }
        TenantInfo tenantInfo = tenantInfoMap.get(tenantUid);
        if (tenantInfo != null) {
            map.put(WebUtil.getMessage("zkbContact_tenant"), tenantInfo.getName());
        }
    }

    /**
     * 根据探头申请生成HashMap
     *
     * @param zkbFePartOrder 人工申请List
     * @return
     */
    public HashMap<String, Object> getProbeHashMap(ZkbFePartOrder zkbFePartOrder, Map<String, Map<String, String>> i18Map, Map<String, ZkbContract> contractMap, Map<String, TenantInfo> tenantInfoMap) {
        HashMap probeMap = new HashMap();
        if (zkbFePartOrder == null) {
            return probeMap;
        }
        this.fullContractAndTenantInfo(contractMap, tenantInfoMap, probeMap, zkbFePartOrder.getContractId(), zkbFePartOrder.getTenantUid());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"), zkbFePartOrder.getSystemId());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_probeOrderNo"), zkbFePartOrder.getPartOrderNo());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_reportUser"), zkbFePartOrder.getReportUser());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_probeReportDate"), this.dateFormat(zkbFePartOrder.getReportDate(), dateFormat));
        if (i18Map.containsKey(assetTypeDi) && i18Map.get(assetTypeDi).containsKey(zkbFePartOrder.getAssetTypeDi())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_assetTypeDi"), i18Map.get(assetTypeDi).get(zkbFePartOrder.getAssetTypeDi()));
        }
        if (i18Map.containsKey(sparePartsOrderAnnotation) && i18Map.get(sparePartsOrderAnnotation).containsKey(zkbFePartOrder.getOrderAnnotation())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderAnnotation"), i18Map.get(sparePartsOrderAnnotation).get(zkbFePartOrder.getOrderAnnotation()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_problemOrderNumber"), zkbFePartOrder.getProblemOrderNumber());
        if (i18Map.containsKey(emergency) && i18Map.get(emergency).containsKey(zkbFePartOrder.getUrgentOrder())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_urgentOrder"), i18Map.get(emergency).get(zkbFePartOrder.getUrgentOrder()));
        }
        if (i18Map.containsKey(zkbUrgentReasonType) && i18Map.get(zkbUrgentReasonType).containsKey(zkbFePartOrder.getUrgencyReasons())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_urgencyReasons"), i18Map.get(zkbUrgentReasonType).get(zkbFePartOrder.getUrgencyReasons()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"), zkbFePartOrder.getPhenomenon());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_productNumber"), zkbFePartOrder.getProductNumber());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_remark"), zkbFePartOrder.getRemark());
        if (i18Map.containsKey(status) && i18Map.get(status).containsKey(zkbFePartOrder.getStatus())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"), i18Map.get(status).get(zkbFePartOrder.getStatus()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_catNo"), zkbFePartOrder.getCatNo());

        if (zkbFePartOrder.getZkbPartOrderCat() != null) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_zkbPartOrderCat"), zkbFePartOrder.getZkbPartOrderCat().getCatModel());
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_valueType"), zkbFePartOrder.getZkbPartOrderCat().getValueType());
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_oldCatSn"), zkbFePartOrder.getOldCatSn());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"), zkbFePartOrder.getCatNum());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"), zkbFePartOrder.getTenantAssignUser());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"), this.dateFormat(zkbFePartOrder.getTenantAssignDate(), dateFormat));
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_comment"), zkbFePartOrder.getComment());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"), zkbFePartOrder.getOrderNo());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"), zkbFePartOrder.getPrice());
        if (i18Map.containsKey(yesOrNo) && i18Map.get(yesOrNo).containsKey(zkbFePartOrder.getNewCatFlag())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_newProbe"), i18Map.get(yesOrNo).get(zkbFePartOrder.getNewCatFlag()));
        }
        if (i18Map.containsKey(needLetterFlag) && i18Map.get(needLetterFlag).containsKey(zkbFePartOrder.getNeedLetterFlag())) {
            if (ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString().equals(zkbFePartOrder.getStatus())
                    || ZkbFePartOrder.workFlowStatus.GEAdminAccept.toString().equals(zkbFePartOrder.getStatus())) {
                probeMap.put(WebUtil.getMessage("zkbFePartOrder_needProbeLetter"), i18Map.get(needLetterFlag).get(zkbFePartOrder.getNeedLetterFlag()));
            }
        }
        probeMap.put("400下单" + WebUtil.getMessage("zkbFePartOrder_remark"), zkbFePartOrder.getApmComment());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderedPerson"), zkbFePartOrder.getFullApmInfoUser());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_orderedDate"), this.dateFormat(zkbFePartOrder.getFullApmInfoDate(), dateFormat));
        if (i18Map.containsKey(zkbPoReturnType) && i18Map.get(zkbPoReturnType).containsKey(zkbFePartOrder.getReturnType())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_returnType"), i18Map.get(zkbPoReturnType).get(zkbFePartOrder.getReturnType()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_partSerialNumber"), zkbFePartOrder.getPartSerialNumber());
        if (i18Map.containsKey(partQualityIssue) && i18Map.get(partQualityIssue).containsKey(zkbFePartOrder.getPartQualityIssue())) {
            probeMap.put(WebUtil.getMessage("zkbFePartOrder_partQualityIssue"), i18Map.get(partQualityIssue).get(zkbFePartOrder.getPartQualityIssue()));
        }
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_airWayBillNo"), zkbFePartOrder.getWaybillNo());
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_returnDate1"), this.dateFormat(zkbFePartOrder.getReturnDate(), dateFormat_month));
        probeMap.put(WebUtil.getMessage("zkbFePartOrder_newCatSn"), zkbFePartOrder.getNewCatSn());
        probeMap.put("FE归还信息" + WebUtil.getMessage("zkbFePartOrder_remark"), zkbFePartOrder.getGeAdminComment());
        return probeMap;
    }

    /**
     * 根据备件申请生成HashMap
     *
     * @param zkbFePartOrder          人工申请List
     * @param i18Map                  i18信息
     * @param zkbFePartOrderAssetInfo 申请信息
     * @return
     */
    public List<HashMap<String, Object>> getSparePartHashMap(ZkbFePartOrder zkbFePartOrder, Map<String, Map<String, String>> i18Map, ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfo, Map<String, ZkbContract> contractMap, Map<String, TenantInfo> tenantInfoMap) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        HashMap sparePartMap = new HashMap();
        if (zkbFePartOrder == null) {
            list.add(sparePartMap);
            return list;
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_systemId"), zkbFePartOrder.getSystemId());
        this.fullContractAndTenantInfo(contractMap, tenantInfoMap, sparePartMap, zkbFePartOrder.getContractId(), zkbFePartOrder.getTenantUid());
        if (i18Map.containsKey(artificialType) && i18Map.get(artificialType).containsKey(zkbFePartOrder.getArtificialType())) {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_artificialType"), i18Map.get(artificialType).get(zkbFePartOrder.getArtificialType()));
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_reportUser"), zkbFePartOrder.getReportUser());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_probeReportDate"), this.dateFormat(zkbFePartOrder.getReportDate(), dateFormat));
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNo"), zkbFePartOrder.getPartOrderNo());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_phenomenon"), zkbFePartOrder.getPhenomenon());
        if (i18Map.containsKey(sparePartsOrderAnnotation) && i18Map.get(sparePartsOrderAnnotation).containsKey(zkbFePartOrder.getOrderAnnotation())) {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderAnnotation"), i18Map.get(sparePartsOrderAnnotation).get(zkbFePartOrder.getOrderAnnotation()));
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_problemOrderNumber"), zkbFePartOrder.getProblemOrderNumber());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_productNumber"), zkbFePartOrder.getProductNumber());
        if (i18Map.containsKey(status) && i18Map.get(status).containsKey(zkbFePartOrder.getStatus())) {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_currentStatus"), i18Map.get(status).get(zkbFePartOrder.getStatus()));
        }
        if (i18Map.containsKey(assetTypeDi) && i18Map.get(assetTypeDi).containsKey(zkbFePartOrder.getAssetTypeDi())) {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetTypeDi"), i18Map.get(assetTypeDi).get(zkbFePartOrder.getAssetTypeDi()));
        }
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignUser"), zkbFePartOrder.getTenantAssignUser());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_tenantAssignDate"), this.dateFormat(zkbFePartOrder.getTenantAssignDate(), dateFormat));
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_comment"), zkbFePartOrder.getComment());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderedPerson"), zkbFePartOrder.getFullApmInfoUser());
        sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderedDate"), this.dateFormat(zkbFePartOrder.getFullApmInfoDate(), dateFormat));
        if (zkbFePartOrderAssetInfo != null) {
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetNo"), zkbFePartOrderAssetInfo.getAssetNo());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetDescription"), zkbFePartOrderAssetInfo.getAssetDescription());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetQty"), zkbFePartOrderAssetInfo.getAssetQty());
            sparePartMap.put("修改数量备注", zkbFePartOrderAssetInfo.getUpdateComment());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_orderNoa"), zkbFePartOrderAssetInfo.getOrderNo());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_assetPrice"), zkbFePartOrderAssetInfo.getPrice());
            sparePartMap.put(WebUtil.getMessage("zkbFePartOrder_spareParts"), zkbFePartOrderAssetInfo.getSparePart());
            if (sparePartMap.get(WebUtil.getMessage("zkbFePartOrder_assetNo")) == null
                    || sparePartMap.get(WebUtil.getMessage("zkbFePartOrder_orderNoa")) == null) {
                list.add(sparePartMap);
                return list;
            }
            List<ZkbFePartOrderReturn> zkbFePartOrderReturnList = this.zkbFePartOrderReturnRepository.findByPartOrderIdAndAssetNoAndOrderNo(
                    zkbFePartOrder.getId(),
                    sparePartMap.get(WebUtil.getMessage("zkbFePartOrder_assetNo")).toString(),
                    sparePartMap.get(WebUtil.getMessage("zkbFePartOrder_orderNoa")).toString());
            if (CollectionUtils.isEmpty(zkbFePartOrderReturnList)) {
                list.add(sparePartMap);
                return list;
            }
            for (ZkbFePartOrderReturn zkbFePartOrderReturn : zkbFePartOrderReturnList) {
                HashMap<String, Object> newMap = sparePartMap;
                if (i18Map.containsKey(zkbPoReturnType) && i18Map.get(zkbPoReturnType).containsKey(zkbFePartOrderReturn.getReturnType())) {
                    newMap.put(WebUtil.getMessage("zkbFePartOrder_returnType"), i18Map.get(zkbPoReturnType).get(zkbFePartOrderReturn.getReturnType()));
                }
                newMap.put(WebUtil.getMessage("zkbFePartOrder_returnDate"), this.dateFormat(zkbFePartOrderReturn.getReturnDate(), ZkbFePartOrderService.dateFormat));
                newMap.put("归还" + WebUtil.getMessage("zkbFePartOrder_remark"), zkbFePartOrderReturn.getComment());
                newMap.put("归还" + WebUtil.getMessage("zkbFePartOrder_assetQty"), zkbFePartOrderReturn.getQty());
                list.add(newMap);
            }
        }
        return list;
    }

    /**
     * 日期格式化
     *
     * @param date       时间
     * @param dateFormat 日期格式
     * @return
     */
    public String dateFormat(Date date, String dateFormat) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }


    /**
     * 生成需要归还的配件探头结果集
     *
     * @return
     */
    public HashMap<String, Object> createZkbFePartOrderNeedReturnData(String testLogin) {
        List<ZkbFePartOrder> zkbFePartOrderSparePartList = this.zkbFePartOrderMapper.buildFilter_queryNeedReturnSparePart(testLogin);
        List<ZkbFePartOrder> zkbFePartOrderProbeList = this.zkbFePartOrderMapper.buildFilter_queryNeedReturnProbe(testLogin);
        List<Map<String, Object>> zkbSparePartFePartOrderDataMapList = new ArrayList<>();
        List<Map<String, Object>> zkbProbeFePartOrderDataMapList = new ArrayList<>();
        Map<String, Map<String, Object>> zkbSparePartFePartOrderDataMap = new HashMap<>();
        List<String> typeList = new ArrayList<>();
        typeList.add(zkbPoValueType);
        Map<String, Map<String, String>> i18Map = this.getI18ToMap(typeList);
        if (CollectionUtils.isNotEmpty(zkbFePartOrderSparePartList)) {
            for (ZkbFePartOrder item : zkbFePartOrderSparePartList) {
                if (item == null) continue;
                String key = item.getPartOrderNo() + item.getAssetNo() + item.getOrderNo();
                if (zkbSparePartFePartOrderDataMap.containsKey(key)) {
                    Integer num = (Integer) zkbSparePartFePartOrderDataMap.get(key).get(WebUtil.getMessage("zkbFePartOrder_assetQty"));
                    zkbSparePartFePartOrderDataMap.get(key).put("数量", num + item.getCatNum());
                    continue;
                }
                HashMap<String, Object> spareProbeMap = this.getReturnSparePartHashMap(item);
                zkbSparePartFePartOrderDataMap.put(key, spareProbeMap);
            }
        }
        if (CollectionUtils.isNotEmpty(zkbFePartOrderProbeList)) {
            for (ZkbFePartOrder item : zkbFePartOrderProbeList) {
                if (item == null) continue;
                HashMap<String, Object> spareProbeMap = this.getReturnProbeHashMap(item, i18Map);
                zkbProbeFePartOrderDataMapList.add(spareProbeMap);
            }
        }
        if (zkbSparePartFePartOrderDataMap != null && !zkbSparePartFePartOrderDataMap.isEmpty()) {
            for (String key : zkbSparePartFePartOrderDataMap.keySet()) {
                Map<String, Object> objectMap = zkbSparePartFePartOrderDataMap.get(key);
                String id = objectMap.get("id").toString();
                String assetNo = objectMap.get(WebUtil.getMessage("zkbFePartOrder_assetNo")).toString();
                String orderNo = objectMap.get(WebUtil.getMessage("zkbFePartOrder_orderNoa")).toString();
                Integer allReturn = this.zkbFePartOrderReturnRepository.allReturnQty(assetNo, orderNo, id);
                allReturn = allReturn == null ? 0 : allReturn;
                objectMap.put("已经归还数量", allReturn);
                if (allReturn >= (Integer) objectMap.get("数量")) {
                    continue;
                }
                zkbSparePartFePartOrderDataMapList.add(objectMap);
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("zkbSparePartFePartOrderDataMapList", zkbSparePartFePartOrderDataMapList);
        map.put("zkbProbeFePartOrderDataMapList", zkbProbeFePartOrderDataMapList);
        return map;
    }

    /**
     * 生成excel
     *
     * @param doc 文件
     */
    public void exportReturnData(ExcelDocument doc, String testLogin) {
        HashMap<String, Object> returnDataMap = this.createZkbFePartOrderNeedReturnData(testLogin);
        Integer keyRow = 1;
        Integer dataRow = 2;
        List<Map<String, Object>> zkbSparePartFePartOrderDataMapList = (List<Map<String, Object>>) returnDataMap.get("zkbSparePartFePartOrderDataMapList");
        List<Map<String, Object>> zkbProbeFePartOrderDataMapList = (List<Map<String, Object>>) returnDataMap.get("zkbProbeFePartOrderDataMapList");
        if (CollectionUtils.isNotEmpty(zkbSparePartFePartOrderDataMapList)) {
            for (Map<String, Object> item : zkbSparePartFePartOrderDataMapList) {
                doc.writeRowData(excelSheetNameOfSparePartReturn, keyRow, dataRow++, item);
            }
        }
        dataRow = 2;
        if (CollectionUtils.isNotEmpty(zkbProbeFePartOrderDataMapList)) {
            for (Map<String, Object> item : zkbProbeFePartOrderDataMapList) {
                doc.writeRowData(excelSheetNameOfProbeReturn, keyRow, dataRow++, item);
            }
        }
    }

    /**
     * 映射附件与业务关系
     *
     * @param v2_blobObjectList 附件
     * @param id                主键
     */
    public void saveUploadFile(List<V2_BlobObject> v2_blobObjectList, String id) {
        if (CollectionUtils.isEmpty(v2_blobObjectList)) {
            return;
        }
        for (V2_BlobObject item : v2_blobObjectList) {
            item.setBoUuid(id);
            this.blobObjectRepository.save(item);
        }
    }

    public boolean checkContractDate(List<ZkbContract> contractList) {
        if (CollectionUtils.isEmpty(contractList)) return false;
        Date startDate = contractList.get(0).getStartDate();
        Date endDate = contractList.get(0).getEndDate();
        String format = "yyyy-MM-dd";
        String dateStr = DateUtil.getFormatStrByDate(new Date(), format);
        Date date = DateUtil.getDateByFormatStr(dateStr, format);
        if (date.compareTo(startDate) >= 0 && endDate.compareTo(date) >= 0)
            return false;
        return true;
    }

    /**
     * 下单信息修改
     * @param zkbPartOrder
     * @param userAccount
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderModify(ZkbFePartOrder zkbPartOrder,UserAccount userAccount){
        ZkbFePartOrder oldZkbPartOrder = zkbFePartOrderRepository.findById(zkbPartOrder.getId());
        if (ZkbFePartOrder.OrderType.SpareParts.toString().equals(zkbPartOrder.getOrderType())){
            List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = zkbFePartOrderAssetInfoRepository.findByFePartOrderId(zkbPartOrder.getId());
            for(ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfoOld : zkbFePartOrderAssetInfoList){
                ZkbFePartOrderModifyLog zkbPartOrderModifyLog = new ZkbFePartOrderModifyLog();
//                zkbPartOrderModifyLog.setSrNo(oldZkbPartOrder.getSr());
//                zkbPartOrderModifyLog.setActivityNo(oldZkbPartOrder.getAct());
                zkbPartOrderModifyLog.setPartOrderId(oldZkbPartOrder.getId());
                zkbPartOrderModifyLog.setPartOrderItemId(zkbFePartOrderAssetInfoOld.getId());
                zkbPartOrderModifyLog.setOrderNo(zkbFePartOrderAssetInfoOld.getOrderNo());
                zkbPartOrderModifyLog.setActualPartNo(zkbFePartOrderAssetInfoOld.getAssetNo());
                zkbPartOrderModifyLog.setPartName(zkbFePartOrderAssetInfoOld.getAssetDescription());
                zkbPartOrderModifyLog.setPrice(zkbFePartOrderAssetInfoOld.getPrice());
                zkbPartOrderModifyLog.setCreateUserDate(new Date());
                zkbPartOrderModifyLog.setCreateUserUid(userAccount.getUid());
                zkbPartOrderModifyLog.setCreateUserName(userAccount.getName());
                zkbFePartOrderModifyLogRepository.save(zkbPartOrderModifyLog);
            }
            for(ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfoNew : zkbPartOrder.getZkbFePartOrderAssetInfoList()){
                zkbFePartOrderAssetInfoRepository.save(zkbFePartOrderAssetInfoNew);
            }
        }else if(ZkbFePartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            ZkbFePartOrderModifyLog zkbPartOrderModifyLog = new ZkbFePartOrderModifyLog();
//            zkbPartOrderModifyLog.setSrNo(oldZkbPartOrder.getSr());
//            zkbPartOrderModifyLog.setActivityNo(oldZkbPartOrder.getAct());
            zkbPartOrderModifyLog.setOrderNo(oldZkbPartOrder.getOrderNo());
            zkbPartOrderModifyLog.setPrice(oldZkbPartOrder.getPrice());
            zkbPartOrderModifyLog.setPartOrderId(oldZkbPartOrder.getId());
            zkbPartOrderModifyLog.setCreateUserDate(new Date());
            zkbPartOrderModifyLog.setCreateUserUid(userAccount.getUid());
            zkbPartOrderModifyLog.setCreateUserName(userAccount.getName());
            zkbFePartOrderModifyLogRepository.save(zkbPartOrderModifyLog);
        }else{
            return ;
        }
        zkbFePartOrderRepository.save(zkbPartOrder);
    }

    /**
     * 已归还已完成工单信息修改
     * @param zkbPartOrder
     * @param userAccount
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveReturnModify(ZkbFePartOrder zkbPartOrder,UserAccount userAccount){
        if (ZkbFePartOrder.OrderType.SpareParts.toString().equals(zkbPartOrder.getOrderType())){
            List<ZkbFePartOrderReturn> ZkbFePartOrderReturnList = zkbPartOrder.getZkbFePartOrderReturnList();
            List<ZkbFePartOrderReturn> oldZkbFePartOrderReturnList = this.zkbFePartOrderReturnRepository.findByPartOrderId(zkbPartOrder.getId());
            for(ZkbFePartOrderReturn zkbFePartOrderReturn : ZkbFePartOrderReturnList){
                String id = zkbFePartOrderReturn.getId();
                ZkbFePartOrderReturn oldZkbFePartOrderReturn = null;
                if(id != null){
                    oldZkbFePartOrderReturn = zkbFePartOrderReturnRepository.findById(zkbFePartOrderReturn.getId());
                }
                if(oldZkbFePartOrderReturn == null){
                    oldZkbFePartOrderReturn = zkbFePartOrderReturn;
                }
                saveZkbFePartOrderReturnLog(oldZkbFePartOrderReturn,userAccount);
                if(zkbFePartOrderReturn.getPartOrderId() == null){
                    zkbFePartOrderReturn.setPartOrderId(zkbPartOrder.getId());
                }
                zkbFePartOrderReturnRepository.save(zkbFePartOrderReturn);
            }
            for(ZkbFePartOrderReturn oldZkbFePartOrderReturn : oldZkbFePartOrderReturnList){
                if(!ZkbFePartOrderReturnList.contains(oldZkbFePartOrderReturn)){
                    saveZkbFePartOrderReturnLog(oldZkbFePartOrderReturn,userAccount);
                    zkbFePartOrderReturnRepository.delete(oldZkbFePartOrderReturn);
                }
            }
        }else if(ZkbFePartOrder.OrderType.Probe.toString().equals(zkbPartOrder.getOrderType())){
            ZkbFePartOrder oldZkbPartOrder = zkbFePartOrderRepository.findById(zkbPartOrder.getId());
            ZkbFePartOrderReturnLog zkbFePartOrderReturnLog = new ZkbFePartOrderReturnLog();
            zkbFePartOrderReturnLog.setReturnType(oldZkbPartOrder.getReturnType());
            zkbFePartOrderReturnLog.setPartSerialNumber(oldZkbPartOrder.getPartSerialNumber());
            zkbFePartOrderReturnLog.setPartQualityIssue(oldZkbPartOrder.getPartQualityIssue());
            zkbFePartOrderReturnLog.setWaybillNo(oldZkbPartOrder.getWaybillNo());
            zkbFePartOrderReturnLog.setReturnDate(oldZkbPartOrder.getReturnDate());
            zkbFePartOrderReturnLog.setNewCatSn(oldZkbPartOrder.getNewCatSn());
            zkbFePartOrderReturnLog.setPartOrderId(oldZkbPartOrder.getId());
            zkbFePartOrderReturnLog.setComment(oldZkbPartOrder.getGeAdminComment());
            zkbFePartOrderReturnLog.setCreateDate(new Date());
            zkbFePartOrderReturnLog.setCreateUserId(userAccount.getUid());
            zkbFePartOrderReturnLog.setCreateUserName(userAccount.getName());
            zkbFePartOrderReturnLogRepository.save(zkbFePartOrderReturnLog);
            zkbFePartOrderRepository.save(zkbPartOrder);
        }else{
            return ;
        }
    }

    public void  saveZkbFePartOrderReturnLog(ZkbFePartOrderReturn oldZkbFePartOrderReturn,UserAccount userAccount){
        ZkbFePartOrderReturnLog zkbFePartOrderReturnLog = new ZkbFePartOrderReturnLog();
        zkbFePartOrderReturnLog.setAssetNo(oldZkbFePartOrderReturn.getAssetNo());
        zkbFePartOrderReturnLog.setQty(oldZkbFePartOrderReturn.getQty());
        zkbFePartOrderReturnLog.setPartOrderId(oldZkbFePartOrderReturn.getPartOrderId());
        zkbFePartOrderReturnLog.setOrderNo(oldZkbFePartOrderReturn.getOrderNo());
        zkbFePartOrderReturnLog.setReturnType(oldZkbFePartOrderReturn.getReturnType());
        zkbFePartOrderReturnLog.setComment(oldZkbFePartOrderReturn.getComment());
        zkbFePartOrderReturnLog.setCreateDate(new Date());
        zkbFePartOrderReturnLog.setCreateUserId(userAccount.getUid());
        zkbFePartOrderReturnLog.setCreateUserName(userAccount.getName());
        zkbFePartOrderReturnLogRepository.save(zkbFePartOrderReturnLog);
    }
}
