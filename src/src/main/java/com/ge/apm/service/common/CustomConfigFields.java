package com.ge.apm.service.common;

import lombok.Getter;

import static com.ge.apm.service.common.CustomConfigFields.MsgType.*;
import static com.ge.apm.service.common.CustomConfigFields.DataType.*;


public enum CustomConfigFields {


    serialNum(fieldName,"serialNum", Text), //制造商序列号
    departNum(fieldName, "departNum",Text), //设备编号（院方）
    name(fieldName, "EquipmentName",Text),  //设备名称
    aliasName(fieldName, "aliasName",Text),  //设备别名
    assetGroup(fieldName, "assetGroup" ,Select),        //设备分类
    assetGroupSecondary(fieldName, "assetGroupSecondary" ,Select), //设备分类2级
    assetOwnerName(fieldName, "assetOwnerName" ,Text),  //维修负责人
    assetOwnerTel(fieldName, "assetOwnerTel" ,Text),   //维修负责人电话
    assetOwnerName2(fieldName, "assetOwnerName2" ,Text),  //维修负责人2
    assetOwnerTel2(fieldName, "assetOwnerTel2" ,Text),  //维修负责人2电话
    assetOwnerName3(fieldName, "assetOwnerName3" ,Text), //维修负责人3
    assetOwnerTel3(fieldName, "assetOwnerTel3" ,Text),  //维修负责人3电话
    siteUID(fieldName, "hospitalId" ,Text),   //所属院区
    clinicalDeptName(fieldName, "clinicalDeptName" ,Text),  //所属科室
    assetDeptName(fieldName, "assetDeptName" ,Text),    //使用科室
    accountingDeptName(fieldName, "accountingDeptName" ,Text),  //账户科室
    modalityType(fieldName, "modalityType" ,Text),   //设备类型
    monitorRate(fieldName, "monitorRate" ,Text),  //主机功率
    otherPower(fieldName, "otherPower" ,Text),  //其他功率
    unitPriceElectricity(fieldName, "unitPriceElectricity" ,Text),  //电费单价
    clinicalOwnerName(fieldName, "clinicalOwnerName" ,Text),  //使用责任人
    clinicalOwnerTel(fieldName, "clinicalOwnerTel" ,Text),   //使用责任人电话
    manufacture(fieldName, "manufacture" ,Text),   //制造商
    manufactureTel(fieldName, "manufactureTel" ,Text),   //制造商电话
    makerShortName(label, "makerShortName" ,Text),   //品牌/制造商简称
    vendor(fieldName, "vendor" ,Text),    //供应商
    vendorTel(fieldName, "vendorTel" ,Text),   //供应商电话
    maitanance(fieldName, "maitanance" ,Text),   //维修商
    maitananceTel(fieldName, "maitananceTel" ,Text),  //维修商电话
    nationalStandardCode(fieldName, "nationalStandardCode" ,Text),  //设备国标码
    parentAssetId(fieldName, "parentAsset" ,Text),   //父设备资产id
    deviceName(label, "deviceName" ,Text),    //设备标准名称
    systemId(fieldName, "systemId" ,Text),    //SystemID/厂商报修ID
    financingNum(fieldName, "financingNum" ,Text),  //资产编号
    barcode(fieldName, "barcode" ,Text),  //条码
    registrationNo(fieldName, "registrationNo" ,Text),   //注册证号
    modalityId(fieldName, "modalityId" ,Text),         //院内IT系统编号
    qrCode(fieldName, "qrCode" ,Text),   //二维码
    functionType(fieldName, "functionType" ,Text),  //设备型号
    functionGroup(fieldName, "functionGroup" ,Text),  //医疗器械分类(旧)
    assetCode68(fieldName, "assetCode68" ,Text),   //医疗器械子分类(旧)
    medicalApparatusClassification(label, "medicalApparatusClassification" ,Text),  //医疗器械分类
    functionGrade(fieldName, "functionGrade" ,Text),  //功能等级
    repairForm(fieldName, "repairForm" ,Text),   //维修类型
    locationCode(fieldName, "locationCode" ,Text),      //安装位置编码
    locationName(fieldName, "locationName" ,Text),     //安装位置
    warrantyStatus(fieldName, "warrantyStatus" ,Text),     //维保状态
    status(fieldName, "status" ,Text),   //当前状态
    isValid(fieldName, "isValid" ,Text),       //是否在用
    deviceStatus(label, "deviceStatus" ,Text), //资产状态
    comments(fieldName, "comments" ,Text),   //备注
    locationNotes(fieldName, "locationNotes" ,Text), //位置备注1(科室)
    assetUsage(fieldName, "assetUsage" ,Text),      //用途分类
    capitalSource(fieldName, "capitalSource" ,Text),  //资金来源
    capitalSource2(fieldName, "capitalSource2" ,Text),  //资金来源2
    measureGrade(fieldName, "measureGrade" ,Text),  //计量等级
    riskGrade(fieldName, "riskGrade" ,Text),   //风险等级
    emergencyEquipment(fieldName, "emergencyEquipment" ,Text),  //是否急救设备
    importEquipment(fieldName, "importEquipment" ,Text),    //是否进口设备
    measuringItemCategory(fieldName, "measuringItemCategory" ,Text),  //计量项目类别
    accountingCategory(fieldName, "accountingCategory" ,Text),   //核算类别
    manufactDate(fieldName, "manufactDate" ,Text),   //出厂日期
    purchaseDate(fieldName, "purchaseDate" ,Text),   //采购日期
    arriveDate(fieldName, "arriveDate" ,Text),    //到货日期
    installDate(fieldName, "installDate" ,Text),   //启用日期
    startUsingDate(fieldName,"startUsingDate" ,Text), //
    factoryWarrantyDate(fieldName, "factoryWarrantyDate" ,Text),  //质保截止日期
    warrantyDate(fieldName, "warrantyDate" ,Text),    //维保截止日期
    terminateDate(fieldName, "terminateDate" ,Text),  //报废日期
    purchasePrice(fieldName, "purchasePrice" ,Text),  //采购价格(元)
    salvageValue(fieldName, "salvageValue" ,Text),   //最终残值(元)
    lifecycle(fieldName, "lifecycle" ,Text),               //使用年限
    depreciationMethod(fieldName, "depreciationMethod" ,Text), //折旧算法
    isPm(fieldName, "isAssetPMed" ,Text),   //是否是保养
    isFixed(fieldName, "isFixed" ,Text),  //是否固定资产
    isChecked(fieldName, "isAssetChecked" ,Text),  //是否选勾
    lastPmDate(fieldName, "lastPmDate" ,Text),    //上次保养日期
    lastQaDate(fieldName, "lastQaDate" ,Text),   //上次巡检日期
    lastMeteringDate(fieldName, "lastMeteringDate" ,Text),   //上次计量日期
    nextMeteringDate(fieldName, "nextMeteringDate" ,Text),   //下次计量日期

    lastInventoryDate(fieldName, "lastInventoryDate" ,Text),   //上次盘点日期
    warranty(fieldName, "isWarrantyNotice" ,Text),        //是否对维保到期提醒
    assetValueCategory(fieldName, "assetValueCategory" ,Text),    //资产价值分类
    pmOwnerName(fieldName, "pmOwnerName" ,Text),   //保养负责人
    pmOwnerTel(fieldName, "pmOwnerTel" ,Text),   //保养负责人电话
    patrolInspectionOwnerName(fieldName,"patrolInspectionOwnerName",Text), // 保养巡检负责人
    patrolInspectionOwnerTel(fieldName,"patrolInspectionOwnerTel",Text), // 保养巡检负责人电话
    acceptDate(fieldName, "acceptDate" ,Text),  //验收日期
    warrantyMonth(fieldName, "warrantyMonth" ,Text),   //质保时间（月）
    subjectProject(label, "subjectProject" ,Text),  //所属课题项目
    createdDate(label, "createdDate" ,Text),   //创建日期
    proprietaryClass(fieldName, "proprietaryClass" ,Text),    //专有分类
    measuringOwner(fieldName, "measuringOwner" ,Text),   //计量负责人
    orgPath(label, "orgPath" ,Text),  //所属单位
    assetLocation(label, "assetLocation" ,Text),  //当前位置
    UCMID(fieldName, "UCMID" ,Text),
    tags(label, "tags" ,TextArray),  //标签
    createdBy(fieldName, "createdBy" ,Text),  //创建人
    connectToInsite(label, "connectToInsite" ,Text),  //连通状态
    assetClass(customI18n, "assetClass" ,Text),  //管理分类
    oneQrCode(label, "oneQrCode" ,Text),   //一机一码
    installStatus(fieldName, "installStatus" ,Text),   //装机状态
    geibTable_installedTime(fieldName, "geibTable_installedTime" ,Text),  //装机时间
    powerOnThreshold(fieldName, "powerOnThreshold" ,Text),  //开机电流阈值
    rfId(fieldName, "rfId" ,Text),           //射频码
    rfGroup(fieldName, "rfGroup" ,Text),   //射频码分组
    currentId(fieldName, "currentId" ,Text), //电流ID
    assetQuality(fieldName, "assetQuality" ,Text),  //设备性质
    assetDisposalResults(fieldName, "assetDisposalResults" ,Text),  //资产处置结果
    isUseRegistration(fieldName, "isUseRegistration" ,Text),   //是否使用登记
    networkingStatus(fieldName, "networkingStatus" ,Text),  //联网情况
    systemNum1(fieldName, "systemNum1" ,Text),   //系统编号1
    systemNum2(fieldName, "systemNum2" ,Text),   //系统编号2
    systemNum3(fieldName, "systemNum3" ,Text),   //系统编号3
    systemNum4(fieldName, "systemNum4" ,Text),   //系统编号4
    systemNum5(fieldName, "systemNum5" ,Text),   //系统编号5
    assetUsedLifecycle(fieldName, "assetUsedLifecycle" ,Text),  //设备已使用年限
    assetLifecycleDeadline(fieldName, "assetLifecycleDeadline" ,Text),  //设备使用年限截止日期
    assetQuantity(label, "assetQuantity" ,Text),   //数量
    productFamily(label, "productFamily" ,Text),  //产品系列
    productSegment(label, "productSegment" ,Text),  //产品细分
    mvsContractStartDate(fieldName, "MVSContractStartDate" ,Text),
    mvsContractEndDate(fieldName, "MVSContractEndDate" ,Text),
    assetTypeGroup(label, "assetTypeGroup" ,Text),  //资产分类
    spare01(fieldName, "spare01" ,Text),  //备用字段01
    spare02(fieldName, "spare02" ,Text),  //备用字段02
    spare03(fieldName, "spare03" ,Text),  //备用字段03
    spare04(fieldName, "spare04" ,Text),  //备用字段04
    spare05(fieldName, "spare05" ,Text),  //备用字段05
    spare06(fieldName, "spare06" ,Text),  //备用字段06
    spare07(fieldName, "spare07" ,Text),  //备用字段07
    spare08(fieldName, "spare08" ,Text),  //备用字段08
    spare09(fieldName, "spare09" ,Text),  //备用字段09
    spare10(fieldName, "spare10" ,Text),  //备用字段10
    exceptionReason(fieldName, "exceptionReason" ,Text),  //备用字段10
    assetClassification32(fieldName, "assetClassification32" ,Text),  //设备32分类
    usageCount(fieldName, "usageCount" ,Text),  //已使用次数
    ratingUsageCount(fieldName, "ratingUsageCount" ,Text),  //额定使用次数
    epc(fieldName, "epc" ,Text),  //epc码
    estimatedRiskLevel(fieldName, "estimatedRiskLevel" ,Select),  //风险等级(预估)

    ;

    @Getter
    private MsgType msgType;
    @Getter
    private String msgKey;
    @Getter
    private DataType dataType;


    CustomConfigFields(MsgType msgType, String msgKey, DataType dataType) {
        this.msgType = msgType;
        this.msgKey = msgKey;
        this.dataType = dataType;
    }


    public enum MsgType{
        fieldName("field_name"),
        label("label"),
        customI18n("customI18n"),
        ;

        @Getter
        String msgTypeValue;

        MsgType(String msgTypeValue){
            this.msgTypeValue = msgTypeValue;
        }
    }


    public enum DataType{
        Select,
        Text,
        TextArray,
        Integer,
        Double,
        Boolean,
        Other
        ;

    }


}

