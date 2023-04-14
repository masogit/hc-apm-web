package com.ge.apm.domain;

/**
 * @author 503310146
 */
public enum ReminderType {

    /**
     * 提醒规则类型
     */
    ASSET("AssetLifecycle", "un_take_task_template_id"),
    MEASURING("Measuring", "un_take_task_template_id"),
    CONTRACT("ContractOverdue", "un_take_task_template_id"),
    ;

    private final String type;
    private final String templateId;

    ReminderType(String type, String templateId) {
        this.type = type;
        this.templateId = templateId;
    }

    public static String getWechatTemplateId(String type) {
        String wechatTemplateId = "";
        for (ReminderType reminderType : ReminderType.values()) {
            if (reminderType.type.equals(type)) {
                wechatTemplateId = reminderType.getTemplateId();
            }
        }
        return wechatTemplateId;
    }

    public String getType() {
        return type;
    }

    public String getTemplateId() {
        return templateId;
    }

}
