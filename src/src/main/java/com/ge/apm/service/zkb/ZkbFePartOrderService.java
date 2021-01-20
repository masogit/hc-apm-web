package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import com.ge.apm.dao.mapper.ZkbFePartOrderMapper;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
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

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 503202755 2020/12/8
 */
@Component
public class ZkbFePartOrderService extends MicroServiceInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ZkbFePartOrderService.class);

    /**
     * 人工申请主表repository
     */
    @Autowired
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

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
        this.sendMessage(zkbFePartOrder.getId(),gatewayURL);
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


}
