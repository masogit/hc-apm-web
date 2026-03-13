package com.ge.apm.domain.reminder;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author 503310146
 */
@Data
public class WechatParam {

    @JSONField(name = "_openId")
    private String openId;

    @JSONField(name = "_templateId")
    private String templateId;

    private String first;

    private String remark;

    private String linkUrl;

    private String keyword1;

    private String keyword2;

    private String keyword3;

    private String keyword4;

    private String keyword5;

    public WechatParam() {

    }

    public WechatParam(String openId, String templateId) {
        this.openId = openId;
        this.templateId = templateId;
    }

    public WechatParam(String openId, String templateId, String first) {
        this.openId = openId;
        this.templateId = templateId;
        this.first = first;
    }

    public WechatParam(String openId, String templateId, String first, String remark) {
        this.openId = openId;
        this.templateId = templateId;
        this.first = first;
        this.remark = remark;
    }

    public WechatParam(String openId, String templateId, String first, String remark, String linkUrl) {
        this.openId = openId;
        this.templateId = templateId;
        this.first = first;
        this.remark = remark;
        this.linkUrl = linkUrl;
    }

}
