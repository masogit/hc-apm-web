package com.ge.apm.domain.budget;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * lyj
 */
@Data
public class BudgetApplicationAssetVo  {

    //申请科室
    @NotNull(message = "申请科室不能为空！")
    private String applicationDeptName;

    //病区代码
    @NotNull(message = "病区代码不能为空！")
    private String wardCode;

    /**
     * *设备名称
     */
    @NotNull(message = "设备名称不能为空！")
    private String applicationAssetName;


    /**
     * *设备品类
     */
    @NotNull(message = "设备品类不能为空！")
    private String applicationAssetCategory;

    /**
     * *推荐品牌
     */
    @NotNull(message = "推荐品牌不能为空！")
    private String recommendedBrand;

    /**
     * *规格
     */
    @NotNull(message = "规格不能为空！")
    private String standards;

    /**
     * *型号
     */
    @NotNull(message = "型号不能为空！")
    private String model;

    // 申请数量
    @NotNull(message = "申请数量不能为空！")
    private Integer applicationQuantity;

    // 单位
    @NotNull(message = "单位不能为空！")
    private String unit;

    // 预算单价
    @NotNull(message = "预算单价不能为空！")
    private BigDecimal applicationPrice;

    // 预算总价
    @NotNull(message = "预算总价不能为空！")
    private BigDecimal applicationAmount;

    /**
     * *经费来源
     */
    @NotNull(message = "经费来源不能为空！")
    private String sourceFunding;


    // 科室现有同类设备数量
    @NotNull(message = "科室现有同类设备数量不能为空！")
    private Integer similarDevicesNumber;

    //科室现有同类设备利用情况
    @NotNull(message = "科室现有同类设备利用情况不能为空！")
    private String similarDevicesUse;

    //是否采购进口产品
    @NotNull(message = "是否采购进口产品不能为空！")
    private String importedOr;

    //申购仪器设备的主要功能
    @NotNull(message = "申购仪器设备的主要功能不能为空！")
    private String majorFunction;

    //申购仪器设备的配置
    @NotNull(message = "申购仪器设备的配置不能为空！")
    private String deviceConfiguration;

    //申购仪器设备的主要技术参数及要求
    @NotNull(message = "申购仪器设备的主要技术参数及要求不能为空！")
    private String technicalParameter;

    //申购仪器设备在本单位医疗、教学、科研工作中的利用
    @NotNull(message = "申购仪器设备在本单位医疗、教学、科研工作中的利用不能为空！")
    private String equipmentUtilization;

    //社会、经济效益分析
    @NotNull(message = "社会、经济效益分析不能为空！")
    private String benefitAnalysis;

    //同类产品比较
    @NotNull(message = "同类产品比较不能为空！")
    private String productComparison;

    //使用年限
    @NotNull(message = "使用年限不能为空！")
    private String serviceLife;

    //收费标准
    @NotNull(message = "收费标准不能为空！")
    private BigDecimal chargingStandards;

    //年预估收入
    @NotNull(message = "年预估收入不能为空！")
    private BigDecimal yearIncome;

    //所需耗材及使用量
    @NotNull(message = "所需耗材及使用量不能为空！")
    private String usage;

    //是否在手术室使用
    @NotNull(message = "是否在手术室使用不能为空！")
    private String usedOperatingRoom;

    //设备是否有放置场地
    @NotNull(message = "设备是否有放置场地不能为空！")
    private String placeSite;

    // 系统对接
    @NotNull(message = "是否需要同院内信息系统对接不能为空！")
    private String systemDocking;

    //连接外网
    @NotNull(message = "是否需要连接外网不能为空！")
    private String connectNetwork;

    @NotNull(message = "情况说明不能为空！")
    private String description;
}
