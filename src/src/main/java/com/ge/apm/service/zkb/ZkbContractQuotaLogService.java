package com.ge.apm.service.zkb;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.ZkbContractQuotaLogRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

/**
 * @author : 503189362
 * @date : 2021/6/8
 * @description :
 */
@Service
public class ZkbContractQuotaLogService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbContractQuotaLogService.class);

    @Autowired
    ZkbContractQuotaLogRepository zkbContractQuotaLogRepository;

    public List<ZkbContractQuotaLog> queryListByFilter(List<SearchFilter> searchFilter){
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        List<ZkbContractQuotaLog> list = zkbContractQuotaLogRepository.findBySearchFilter(searchFilter,sort);
        return list;
    }

    public List<Map<String, Object>> createZkbEngineerData(List<SearchFilter> searchFilter){
        List<ZkbContractQuotaLog> zkbContractQuotaLogList = queryListByFilter(searchFilter);
        List<Map<String, Object>> zkbEngineerDataList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ZkbContractQuotaLog q : zkbContractQuotaLogList) {
            Map<String, Object> zkbLogDataMap = new HashMap<>();

            zkbLogDataMap.put("合同号", q.getContractNo());
            zkbLogDataMap.put("申请编号", q.getZkbPartOrderNo());
            zkbLogDataMap.put("名额类型", WebUtil.getFieldValueMessage("zkbContractQuotaOrderType",q.getOrderType()));
            zkbLogDataMap.put("操作类型", WebUtil.getFieldValueMessage("zkbContractQuotaOpType",q.getOpType()));
            zkbLogDataMap.put("步骤", q.getStep());
            zkbLogDataMap.put("总名额", q.getQuotaTotal());
            zkbLogDataMap.put("剩余名额", q.getQuota());
            zkbLogDataMap.put("申请名额", q.getQuotaApply());
            zkbLogDataMap.put("已使用名额", q.getQuotaUse());
            zkbLogDataMap.put("操作人", q.getCreatedUserName());
            zkbLogDataMap.put("操作时间", format.format(q.getCreatedDate()));

            zkbEngineerDataList.add(zkbLogDataMap);
        }

        return zkbEngineerDataList;
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbEngineerData) {
        Integer keyRow_zkbEngineer = 1;
        Integer dataRow_zkbEngineer = 2;

        for (Map<String, Object> item : zkbEngineerData) {
            doc.writeRowData("日志", keyRow_zkbEngineer, dataRow_zkbEngineer++, item);
        }


    }


    @Transactional(rollbackFor = Exception.class)
    public void saveData(ZkbContract zkbContract, ZkbPartOrder zkbPartOrder, ZkbFePartOrder zkbFePartOrder, String opType, String orderType, String businessType, UserAccount userAccount,String step){
        ZkbContractQuotaLog l = new ZkbContractQuotaLog();
        l.setCreatedDate(new Date());
        l.setCreatedUserName(userAccount.getName());
        l.setCreatedUserUid(userAccount.getUid());
        if(zkbContract != null){
            l.setContractId(zkbContract.getId());
            l.setContractNo(zkbContract.getContractNo());
        }
        if(zkbPartOrder != null){
            l.setZkbPartOrderId(zkbPartOrder.getId());
            l.setZkbPartOrderNo(zkbPartOrder.getPartOrderNo());
        }
        if(zkbFePartOrder != null){
            l.setZkbPartOrderId(zkbFePartOrder.getId());
            l.setZkbPartOrderNo(zkbFePartOrder.getPartOrderNo());
        }
        l.setOpType(opType);
        l.setOrderType(orderType);
        l.setBusinessType(businessType);
        l.setStep(step);

        if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(orderType)){
            l.setQuota(zkbContract.getSpareQuota());
            l.setQuotaApply(zkbContract.getSpareQuotaApply());
            l.setQuotaTotal(zkbContract.getSpareQuotaTotal());
            l.setQuotaUse(zkbContract.getSpareQuotaUse());
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString().equals(orderType)){
            l.setQuota(zkbContract.getProbeQuota());
            l.setQuotaApply(zkbContract.getProbeQuotaApply());
            l.setQuotaTotal(zkbContract.getProbeQuotaTotal());
            l.setQuotaUse(zkbContract.getProbeQuotaUse());
        }else if(ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(orderType)){
            l.setQuota(zkbContract.getGeArtificialSupportTimes());
            l.setQuotaApply(zkbContract.getGeArtificialSupportTimesApply());
            l.setQuotaTotal(zkbContract.getGeArtificialSupportTimesTotal());
            l.setQuotaUse(zkbContract.getGeArtificialSupportTimesUse());
        }else if(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString().equals(orderType)){
            l.setQuota(zkbContract.getSpareArtificail());
            l.setQuotaApply(zkbContract.getSpareArtificailApply());
            l.setQuotaTotal(zkbContract.getSpareArtificailTotal());
            l.setQuotaUse(zkbContract.getSpareArtificailUse());
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString().equals(orderType)){
            l.setQuota(zkbContract.getProbeQuotaNormal());
            l.setQuotaApply(zkbContract.getProbeQuotaNormalApply());
            l.setQuotaTotal(zkbContract.getProbeQuotaNormalTotal());
            l.setQuotaUse(zkbContract.getProbeQuotaNormalUse());
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString().equals(orderType)){
            l.setQuota(zkbContract.getProbeQuotaSpecial());
            l.setQuotaApply(zkbContract.getProbeQuotaSpecialApply());
            l.setQuotaTotal(zkbContract.getProbeQuotaSpecialTotal());
            l.setQuotaUse(zkbContract.getProbeQuotaSpecialUse());
        }else if(ZkbContractQuotaLog.OrderTypeEnum.Probe.toString().equals(orderType)){
            l.setQuota(zkbContract.getProbeQuota());
            l.setQuotaApply(zkbContract.getProbeQuotaApply());
            l.setQuotaTotal(zkbContract.getProbeQuotaTotal());
            l.setQuotaUse(zkbContract.getProbeQuotaUse());
        }
        zkbContractQuotaLogRepository.save(l);
        logger.info("5 liyonjian ZKB - 2022：zkbContract" + zkbContract.getProbeQuota()+"ProbeQuotaSpecial:"+zkbContract.getProbeQuotaSpecial()+"ProbeQuotaNormal:"+zkbContract.getProbeQuotaNormal());
        logger.info("5 liyonjian ZKB - 2022：ZkbContractQuotaLog" + JSONObject.toJSONString(l, true) );

    }
}
