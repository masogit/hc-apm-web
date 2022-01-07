package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbContractLogRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContractLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/5/25
 * @description :
 */
@Service
public class ZkbContractLogService {

    @Autowired
    ZkbContractLogRepository zkbContractLogRepository;

    @Transactional(rollbackFor = Exception.class)
    public void addLog(String contractId,String contractNo, String logType,String courseData,String assetData, String reason, UserAccount currentUser,String catData){
        ZkbContractLog zkbContractLog = new ZkbContractLog();
        zkbContractLog.setContractId(contractId);
        zkbContractLog.setContractNo(contractNo);
        zkbContractLog.setLogType(logType);
        zkbContractLog.setCourseData(courseData);
        zkbContractLog.setAssetData(assetData);
        zkbContractLog.setLoginUserName(currentUser.getName());
        zkbContractLog.setCreatedDate(new Date());
        zkbContractLog.setReason(reason);
        zkbContractLog.setCatData(catData);
        zkbContractLogRepository.save(zkbContractLog);
    }

}
