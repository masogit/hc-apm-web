package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbTrainCourse;
import com.ge.apm.domain.ZkbTrainPublish;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ZkbTrainPublishService {


    @Autowired
    private ZkbTrainPublishRepository zkbTrainPublishRepository;



    public void save(ZkbTrainPublish zkbTrainPublish, UserAccount userAccount, ZkbTrainCourse zkbTrainCourse){
        if(StrUtil.isEmpty(zkbTrainPublish.getId())){
            zkbTrainPublish.setCreatedBy(userAccount.getUid());
            zkbTrainPublish.setCreatedDate(new Date());
            zkbTrainPublish.setCourseId(zkbTrainCourse.getId());
        }else{
            zkbTrainPublish.setLastModifiedBy(userAccount.getUid());
            zkbTrainPublish.setLastModifiedDate(new Date());
        }
        zkbTrainPublishRepository.save(zkbTrainPublish);
    }
}
