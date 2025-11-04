package com.ge.apm.service.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationExecutor {

    private static final Logger logger = LoggerFactory.getLogger(NotificationExecutor.class);

    /**
     * 条件执行通知动作：若 sendValue 非空，则执行 sendAction；否则记录日志。
     * @param sendType   联系方式类型
     * @param sendValue  联系方式值
     * @param courseId      课程ID
     * @param courseName    课程名称
     * @param loginName        用户名
     * @param sendAction    发送动作的回调
     */
    public static void executeIfNotBlank(String sendType, String sendValue, String courseId, String courseName, String loginName,
            Runnable sendAction) {

        if (StringUtils.isNotBlank(sendValue)) {
            sendAction.run();
        } else {
            logger.info("课程ID: {}, 课程名称: {}, 用户名: {} 的{}为空，跳过通知",
                    courseId, courseName, loginName, sendType);
        }
    }
}
