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

    private Integer pushDateUnit;

    private Integer pushDateValue;

    private String content;

    private Date startDate;

    private Date endDate;

    private String reminderDate;

    private String noticeType;

    private String noticeUser;

    private Integer pushType;

    private Integer pushCycle;

    private Integer pushNum;

    private List<Integer> assetInfoIds;

    private Map<Integer, List<MessageSubscriber>> usersMap;

}
