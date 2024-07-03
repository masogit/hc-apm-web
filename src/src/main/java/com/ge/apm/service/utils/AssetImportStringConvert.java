package com.ge.apm.service.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.ge.apm.service.asset.ApmEasyExcelConvert;

public class AssetImportStringConvert implements ApmEasyExcelConvert, Converter<String> {


    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {


        return null;
    }

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        String stringValue = context.getReadCellData().getStringValue();
        if ("#".equals(stringValue)) {
            String fieldName = context.getContentProperty().getField().getName();
            if (fieldName.endsWith("Str")) {
                deleteMarkHandel(context, fieldName.substring(0, fieldName.length() - 3));
            }else if(fieldName.equals("assetOwnerName")){
                deleteMarkHandel(context, "assetOwnerName");
                deleteMarkHandel(context, "assetOwnerId");
                deleteMarkHandel(context, "assetOwnerTel");
            }else if(fieldName.equals("assetOwnerName2")){
                deleteMarkHandel(context, "assetOwnerName2");
                deleteMarkHandel(context, "assetOwnerId2");
                deleteMarkHandel(context, "assetOwnerTel2");
            }else if(fieldName.equals("assetOwnerName3")){
                deleteMarkHandel(context, "assetOwnerName3");
                deleteMarkHandel(context, "assetOwnerId3");
                deleteMarkHandel(context, "assetOwnerTel3");
            }
            else if(fieldName.equals("clinicalOwnerName")){
                deleteMarkHandel(context, "clinicalOwnerName");
                deleteMarkHandel(context, "clinicalOwnerId");
                deleteMarkHandel(context, "clinicalOwnerTel");
            } else if(fieldName.equals("pmOwnerName")){
                deleteMarkHandel(context, "pmOwnerName");
                deleteMarkHandel(context, "pmOwnerId");
                deleteMarkHandel(context, "pmOwnerTel");
            }else if(fieldName.equals("patrolInspectionOwnerName")){
                deleteMarkHandel(context, "patrolInspectionOwnerName");
                deleteMarkHandel(context, "patrolInspectionOwnerUID");
                deleteMarkHandel(context, "patrolInspectionOwnerTel");
            }
            else {
                deleteMarkHandel(context);
            }
            return null;
        } else {
            return stringValue;
        }
    }
}
