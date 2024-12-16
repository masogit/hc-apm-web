package com.ge.apm.service.budget;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.mq.MqUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



import java.io.Serializable;
import java.util.*;



@Service
public class AccessRecordsService  implements Serializable {

    @Autowired
    private MqUtils mqUtils;
    private static Logger logger = LoggerFactory.getLogger(AccessRecordsService.class);


    @Async
    public void  accessRecords(String featureName, String businessType, UserAccount userAccount) {
        mqUtils.sendUserAccessRecords(userAccount,businessType,featureName);
    }
}
