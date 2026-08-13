package com.ge.apm.domain.reminder.rule;

import com.ge.apm.domain.MessageSubscriber;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 503310146
 */
@Data
public class RuleParam {

    private String field;

    private Boolean afterReminder;

    private Integer reminderDay = 0;

    private Integer pushDateValue = 0;

    private String content;

    private Date startDate;

    private Date endDate;

    private String reminderDate;

    private String noticeType;

    private List<String> noticeUser;

    private List<String> customRole;

    private String messageType;

    private String scheduledFrequency;

    private Integer scheduledDay;

    private Date scheduledTime;

    private Integer pushType = 1;

    private Integer pushCycle = 0;

    private Integer pushNum = 0;

    private Integer emergencyEquipment = 0;

    private List<Integer> assetInfoIds;

    private Map<Integer, List<MessageSubscriber>> usersMap;

}
