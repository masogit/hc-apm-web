package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.dao.mapper.ZkbFePartOrderMapper;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
     * 根据system_id和用户tenant_uid 查询设备信息
     */
    @Autowired
    private ZkbFePartOrderMapper zkbFePartOrderMapper;


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 单号前缀
     */
    private static final String  ORDER_PREFIX = "FE";

    /**
     * 给申请单添加单号和索引
     * @param zkbFePartOrder
     * @return
     */
    private ZkbFePartOrder fullOrderNo(ZkbFePartOrder zkbFePartOrder)
    {
        Object o= zkbFePartOrderRepository.getMaxIndex(ORDER_PREFIX+simpleDateFormat.format(new Date())+"%") ;
        int index = o == null ? 0 : Integer.parseInt(o.toString());
        index++;
        String partOrderNo = ORDER_PREFIX+simpleDateFormat.format(new Date())+String.format("%04d",index);
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
        this.fullOrderNo(zkbFePartOrder);
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


}
