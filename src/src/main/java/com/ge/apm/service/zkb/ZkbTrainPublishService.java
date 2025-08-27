package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbTrainPublishRegisterDetailRepository;
import com.ge.apm.dao.ZkbTrainPublishRegisterRepository;
import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ZkbTrainPublishService {


    @Autowired
    private ZkbTrainPublishRepository zkbTrainPublishRepository;

    @Autowired
    private ZkbTrainPublishRegisterRepository zkbTrainPublishRegisterRepository;

    @Autowired
    private ZkbTrainPublishRegisterDetailRepository zkbTrainPublishRegisterDetailRepository;



    public void save(ZkbTrainPublishInfo zkbTrainPublishInfo, UserAccount userAccount, ZkbTrainCourse zkbTrainCourse){
        if(StrUtil.isEmpty(zkbTrainPublishInfo.getId())){
            zkbTrainPublishInfo.setCreatedBy(userAccount.getUid());
            zkbTrainPublishInfo.setCreatedDate(new Date());
            zkbTrainPublishInfo.setCourseId(zkbTrainCourse.getId());
        }else{
            zkbTrainPublishInfo.setLastModifiedBy(userAccount.getUid());
            zkbTrainPublishInfo.setLastModifiedDate(new Date());
        }
        zkbTrainPublishRepository.save(zkbTrainPublishInfo);
    }




    public void exportZipFile(ZkbTrainPublishInfo zkbTrainPublishInfo){
        List<ZkbTrainPublishRegister> registerList = null;
    }
}
