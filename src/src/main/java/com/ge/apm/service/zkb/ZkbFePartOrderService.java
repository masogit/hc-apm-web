package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.ZkbFePartOrderMapper;
import com.ge.apm.dao.mapper.ZkbPartOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
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
import org.springframework.transaction.annotation.Transactional;
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


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 单号前缀
     */
    private static final String  ORDER_PREFIX = "FE";

    /**
     * 单号前缀
     */
    private static final String  SPARE_PARTS_ORDER_PREFIX = "FEPO";

    /**
     * 超声高端机
     */
    public static String highArtificialTypeValue = "High";

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
            return zkbFePartOrder;
        }
        ZkbFePartOrder item = list.get(0);
        zkbFePartOrder.setContractId(item.getContractId());
        zkbFePartOrder.setFunctionType(item.getFunctionType());
        zkbFePartOrder.setContractId(item.getContractId());
        zkbFePartOrder.setHospitalName(item.getHospitalName());
        zkbFePartOrder.setTenantUid(item.getTenantUid());
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
    public void moveSpareQuota(Integer type,ZkbContract zkbContract,Integer qty,ZkbFePartOrder zkbFePartOrder)
    {
        if(StringUtils.isNotBlank(zkbFePartOrder.getAssetTypeDi()))
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
        }else
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
    public Boolean checkRendered(String fieldsetType,ZkbFePartOrder zkbFePartOrder,Map<String, String> configTypeMap)
    {
        if(StringUtils.isBlank(fieldsetType))
        {
            return false;
        }
        Map<String,Integer> stepMap = this.stepAndRoleMap();
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
            String [] stepAndRole = ZkbFePartOrder.stepAndRoles[fieldsetTypeIndex].split(",");
            if(configTypeMap.containsKey(stepAndRole[1]))
            {
                return true;
            }
        }
        return false;
    }

    private Map<String,Integer> stepAndRoleMap()
    {
        Map<String,Integer> stepMap = new HashMap<>();
        for(int i =0;i<ZkbFePartOrder.stepAndRoles.length;i++)
        {
            String [] stepAndRole =ZkbFePartOrder.stepAndRoles[i].split(",");
            stepMap.put(stepAndRole[0],i);
        }
        return stepMap;
    }

    /**
     * 判断是否隐藏还是显示
     * @param fieldsetType
     * @return
     */
    public Boolean hasEditRight(String fieldsetType,ZkbFePartOrder zkbFePartOrder,Map<String, String> configTypeMap)
    {
        String currentStep = zkbFePartOrder.getCurrentStep();
        if(!currentStep.equals(fieldsetType))
        {
            return false;
        }
        Map<String,Integer> stepMap = this.stepAndRoleMap();
        String [] stepAndRole =ZkbFePartOrder.stepAndRoles[stepMap.get(fieldsetType)].split(",");
        if(configTypeMap.containsKey(stepAndRole[1]))
        {
            return true;
        }
        return false;
    }

    /**
     * 根据systemId和 报修单号检索
     * @param systemId systemId
     * @param sr 报修单号检索
     * @param partOrderNo 订单编号
     */
    public List<ZkbFePartOrder> getSparePartsZkbFePartOrder(String systemId,String sr,String partOrderNo)
    {
        List<ZkbFePartOrder> sparePartsZkbFePartOrderList = null;
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("orderType", SearchFilter.Operator.EQ,ZkbFePartOrder.OrderType.ManMade.toString()));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ,ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString()));
        if(StringUtils.isBlank(systemId)&&StringUtils.isBlank(sr)&&StringUtils.isBlank(partOrderNo))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdAndFatherPartOrderNoError"));
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
            searchFilters.add(new SearchFilter("partOrderNo", SearchFilter.Operator.LIKE,partOrderNo));
        }
        sparePartsZkbFePartOrderList = this.zkbFePartOrderRepository.findBySearchFilter(searchFilters);
        List<ZkbPartOrder> zkbPartOrderList =null;
        zkbPartOrderList = this.zkbPartOrderMapper.queryBySystemIdAndSr(systemId,sr,partOrderNo);
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
                zkbFePartOrder.setReportDate(item.getCreatedDate());
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
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_searchError"));
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
        AssetInfo assetInfo = this.assetInfoRepository.getBySystemId(selectZkbFePartOrder.getSystemId());
        if(assetInfo!=null)
        {
            selected.setArtificialType("");
            if(assetInfo.getAssetGroup()!=null&&"2".equals(assetInfo.getAssetGroup().toString()))
            {
                selected.setArtificialType(highArtificialTypeValue);
            }
            selected.setAssetGroup(assetInfo.getAssetGroup().toString());
            selected.setProductNumber(assetInfo.getFunctionType());
        }
        return selected;
    }
    /**
     * 保存附件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUploadFile(List<V2_BlobObject> v2_blobObjects,String id){
        if(v2_blobObjects != null){
            for (V2_BlobObject v : v2_blobObjects){
                v.setBoUuid(id);
                blobObjectRepository.save(v);
            }
        }
    }

}
