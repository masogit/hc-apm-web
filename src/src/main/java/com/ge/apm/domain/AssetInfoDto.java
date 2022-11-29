package com.ge.apm.domain;

import lombok.Data;

/**
 * @author 503191723
 * kong.weigen
 * 页面请求参数封装资产信息实体类简易版,
 * 复用可以添加字段,不要删除字段,也不要添加字段非空校验,影响别人使用
 */
@Data
public class AssetInfoDto {

    private static final long serialVersionUID = 9L;

    //采购原值(元)
    private Double purchasePrice;

    //设备名称
    private String name;

    //设备编号
    private String departNum;

    //设备型号
    private String functionType;

}
