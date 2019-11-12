/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.common;

import java.util.*;

import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
public class QrcodeConfigConstant {

    public static final String DEFAULT_TEMPLATE_JSON = "{\"Height\":360,\"Elements\":[{\"ColorStr\":\"000000\",\"PositionY\":40,\"FontSize\":40,\"TextValue\":\"\",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"hospital\"},{\"ColorStr\":\"000000\",\"PositionY\":240,\"FontSize\":32,\"TextValue\":\"Equipment Name   \",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"name\"},{\"ColorStr\":\"000000\",\"PositionY\":120,\"FontSize\":32,\"TextValue\":\"Clinical Department   \",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"clinicalDept\"},{\"ColorStr\":\"000000\",\"PositionY\":180,\"FontSize\":32,\"TextValue\":\"Asset ID   \",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"financingNum\"}],\"Width\":900,\"QrCodeElements\":{\"Type\":\"QrCode\",\"PositionY\":20,\"FontSize\":24,\"Size\":300,\"FontName\":\"Microsoft YaHei\",\"FontPositionX\":14,\"PositionX\":10}}";

    private static Map<String, String> FieldsMap;
    
    private static Map<String, String> FontNameMap;

    private static Map<String, String> qrCodeEncodingMethods;

    static {
        qrCodeEncodingMethods = new LinkedHashMap<>();
        qrCodeEncodingMethods.put("redirect2Wechat", WebUtil.getMessage("redirect2Wechat"));
        qrCodeEncodingMethods.put("redirect2FuncPage", WebUtil.getMessage("redirect2FuncPage"));
    }
    
    public static Map<String, String> getFieldsMap() {
        if (null == FieldsMap) {
            FieldsMap = new LinkedHashMap();
            FieldsMap.put("blank", "无");                                        //空白
            FieldsMap.put("name", WebUtil.getMessage("name"));                   //设备名称  AssetInfo.name
            FieldsMap.put("assetGroup", WebUtil.getMessage("assetGroup"));       //设备类型  AssetInfo.assetGroup
            FieldsMap.put("hospital", WebUtil.getMessage("hospitalId"));         //所属院区  AssetInfo.siteUID -> OrgInfo.name
            FieldsMap.put("functionType", WebUtil.getMessage("functionType"));   //设备型号  AssetInfo.functionType
            FieldsMap.put("clinicalDept", WebUtil.getMessage("clinicalDeptId")); //使用科室  AssetInfo.clinicalDeptUID -> OrgInfo.name
            FieldsMap.put("serialNum", WebUtil.getMessage("serialNum"));         //序列号(厂商SN)  AssetInfo.serialNum
            FieldsMap.put("financingNum", WebUtil.getMessage("financingNum"));   //资产编号  AssetInfo.financingNum
            FieldsMap.put("departNum", WebUtil.getMessage("departNum"));         //设备编号（院方）  AssetInfo.departNum
            FieldsMap.put("modalityId", WebUtil.getMessage("modalityId"));       //院内IT系统编号  AssetInfo.modalityId
            FieldsMap.put("makerShortName", WebUtil.getMessage("makerShortName"));       //品牌/制造商简称  AssetInfo.makerShortName
            FieldsMap.put("systemId", WebUtil.getMessage("systemId"));           //SystemID/厂商报修ID  AssetInfo.systemId
            FieldsMap.put("installDate", WebUtil.getMessage("installDate"));           //启用日期  AssetInfo.installDate
            FieldsMap.put("qrCode", WebUtil.getMessage("QRCode"));           //启用日期  AssetInfo.installDate

        }
        return FieldsMap;
    }

    public static Map<String, String> getFontNameMap() {
        if (FontNameMap == null) {
            FontNameMap = new LinkedHashMap();
            FontNameMap.put("TimesRoman", "TimesRoman");
            FontNameMap.put("Courier", "Courier");
            FontNameMap.put("Arial", "Arial");
            FontNameMap.put("Microsoft YaHei", "Microsoft YaHei");
            FontNameMap.put("SimHei", "SimHei");
            FontNameMap.put("SimSun", "SimSun");
        }
        return FontNameMap;
    }

    public static Map<String, String> getQrCodeEncodingMethod() {
        return qrCodeEncodingMethods;
    }
    
}
