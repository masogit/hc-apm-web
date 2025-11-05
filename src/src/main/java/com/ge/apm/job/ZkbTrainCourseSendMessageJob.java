package com.ge.apm.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.zkb.ZkbTrainPublishService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateTime;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import javaslang.Tuple2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by 503035627
 */
@Component
@EnableScheduling
public class ZkbTrainCourseSendMessageJob {

    public static final Logger logger = LoggerFactory.getLogger(ZkbTrainCourseSendMessageJob.class);

    @Autowired
    ZkbTrainPublishService zkbTrainPublishService;

    @Autowired
    ZkbTrainPublishRepository zkbTrainPublishRepository;

    @Scheduled(cron = "0 0 11 * * ?")
    public void execute() {
        logger.info("ZkbTrainCourseSendMessageJob start...");
        try {
            doJob();
        } catch (Exception e) {
           logger.error("ZkbTrainCourseSendMessageJob error", e);
        }
        logger.info("ZkbTrainCourseSendMessageJob end...");
    }

    private void doJob() {
        try {
            // 查询课程开始时间刚好是15天后的课程列表
            List<ZkbTrainPublishInfo> courses = zkbTrainPublishRepository.findByCourseStartDate();
            
            if (CollectionUtils.isEmpty(courses)) {
                logger.info("未找到符合条件的课程");
                return;
            }
            
            for (ZkbTrainPublishInfo zkbTrainPublishInfo : courses) {
                zkbTrainPublishService.sendMessageByPublishInfo(zkbTrainPublishInfo);
                logger.info("课程Id : {}, 推送短信邮件完成", zkbTrainPublishInfo.getCourseId());
            }
        } catch (Exception e) {
            logger.error("推送课程完成通知时发生异常", e);
        }
    }


}
