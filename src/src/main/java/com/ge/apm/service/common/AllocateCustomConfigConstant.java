package com.ge.apm.service.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public enum  AllocateCustomConfigConstant  {

    ASSET_ALLOCATE{
        private   LinkedHashMap<String,String> allAllocateFields = new LinkedHashMap<>();
        @Override
        public  LinkedHashMap<String,String> getAllAllocateFields(){
            if(allAllocateFields.isEmpty()) {
                allAllocateFields.put("reclinicalDept", "借用科室");
                allAllocateFields.put("arrivalListAssetName", "设备名称");
                allAllocateFields.put("departNum", "设备编号");
                allAllocateFields.put("swapName", "调剂名称");
                allAllocateFields.put("functionType", "型号");
                allAllocateFields.put("brand", "品牌");
                allAllocateFields.put("chargingBeginTime", "计费开始时间");
                allAllocateFields.put("chargingEndTime", "计费结束时间");
                allAllocateFields.put("unitPrice", "单价");
                allAllocateFields.put("chargingType", "计价方式");
                allAllocateFields.put("rentalTime", "使用时长");
                allAllocateFields.put("totalAmount", "租赁费用");
                allAllocateFields.put("compensationAmount", "赔偿费用");
                allAllocateFields.put("gratisDuration", "免除计费时长");
                allAllocateFields.put("gratisFee", "免除费用");
                allAllocateFields.put("discountPeriod", "优惠时段");
                allAllocateFields.put("discount", "优惠折扣");
                allAllocateFields.put("discountTime", "优惠时长");
                allAllocateFields.put("discountFee", "优惠费用");
                allAllocateFields.put("cost", "总计费用");
                allAllocateFields.put("status", "状态");
                allAllocateFields.put("swapIndex", "调剂编码");
                allAllocateFields.put("precautions", "注意事项");
                allAllocateFields.put("borrowDealName", "借用受理人");
            }
            return allAllocateFields;
        }
        @Override
        public  List<String> getAssetAllocateFields() {
            return Arrays.asList("reclinicalDept","arrivalListAssetName","departNum","swapName","functionType",
                    "brand","chargingBeginTime","chargingEndTime","unitPrice","chargingType","rentalTime","totalAmount",
                    "compensationAmount","gratisDuration","gratisFee","discountPeriod","discount","discountTime","discountFee","cost",
                    "status","swapIndex","precautions","borrowDealName","signatory","signatureTime","signatureId");
        }
    };
    public  LinkedHashMap<String,String> getAllAllocateFields(){return new LinkedHashMap<>();}
    public  List<String> getAssetAllocateFields() {return new ArrayList<>();}

}
