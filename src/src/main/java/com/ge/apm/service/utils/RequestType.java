package com.ge.apm.service.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * 定义借用请求类型
 * @author nick
 */
public enum RequestType {

    /*
     * 借用请求为维修，不计算租赁费用
     */
    REQ_FOR_REPAIR("维修", "1"),
    /*
     * 借用请求为补登记录，正常计算租赁费用
     */
    REQ_FOR_RECORD("补登记", "2");

    @Getter
    @Setter
    private String desc;

    @Getter
    @Setter
    private String index;

    RequestType(String desc, String index) {
        this.desc = desc;
        this.index = index;
    }

    public static RequestType getRequestTypeByIndex(String index) {
        // 请求类型缺省为补登记
        RequestType rt = RequestType.REQ_FOR_RECORD;
        for(RequestType requestType : RequestType.values()) {
            if(requestType.index.equals(index)) {
                rt = requestType;
            }
        }
        return rt;
    }

}
