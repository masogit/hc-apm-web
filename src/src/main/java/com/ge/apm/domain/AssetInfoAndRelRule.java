package com.ge.apm.domain;

import lombok.Data;
import java.util.Date;

@Data
public class AssetInfoAndRelRule {
    private Date installDate;     //启用日期
    private Date purchaseDate;    //采购日期
    private String assetName;     //资产名称
    private String assetOwnerName;  //维修负责人
    private String pmOwnerName;   //保养负责人
    private String clinicalDeptName; //所属科室
    private String nationalStandardCode; //设备国际码
    private String departNum;     //设备编号（院方）
    private String makerShortName; //品牌/制造商简称
    private String manufacture;  //制造商
    private String qrCode;      //二维码
    private String assetGroupName; //设备分类

    private String levelTwoName;  //二级规则
    private String levelThreeName; //三级规则
    private String levelOneName;  //二级规则
    private String levelFourName; //三级规则
    private Date createdDate;     //配置日期

    private String serialNum;
    private String siteName;      //所属院区
}
