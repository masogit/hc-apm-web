package com.ge.apm.service.adverseEvent;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ge.healthcare.cn.common.utils.util.StrUtil;

public class AdverseEventEventCauseAnalyzeConverter implements Converter<String> {


    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String result = "";
        if(!StrUtil.isEmpty(value)){
            if(value.indexOf(",") > -1){
                String[] vls = value.split(",");
                for(int i =0; i< vls.length;i++){
                    result += valueToText(vls[i])+ ",";
                }
                result = result.substring(0, result.length() -1);
            }else{
                result = valueToText(value);
            }
        }
        return new WriteCellData<>(result);
    }

    public String valueToText(String value){
        switch (value){
            case "1":
                return "产品原因（包括说明书等）";
            case "2":
                return "操作原因";
            case "3":
                return "患者自身原因";
            case "4":
                return "无法确定";
            default:
                return "";
        }
    }

}
