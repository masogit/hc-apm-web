package com.ge.apm.service.his;

import com.ge.apm.dao.HisManualJobRepository;
import com.ge.apm.domain.his.HisManualJob;
import com.ge.apm.service.constant.HisConstant;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;

@Service
public class HisManualJobService {
    @Autowired
    private HisManualJobRepository hisManualJobRepository;


    public void saveManualData(HisManualJob hisManualJob){
        LinkedHashMap<String, String> linkedHashMap = HisConstant.getTaskTypeMap();
        hisManualJob.setTaskTypeName(hisManualJob.getTaskTypeName());
        hisManualJob.setTaskType(linkedHashMap.get(hisManualJob.getTaskTypeName()));
        hisManualJob.setTaskStatus(HisConstant.STATUS_INIT);
        hisManualJob.setCreatedBy(UserContextService.getCurrentUserAccount().getName());
        hisManualJob.setCreatedDate(new Date());
        hisManualJobRepository.save(hisManualJob);
    }
}
