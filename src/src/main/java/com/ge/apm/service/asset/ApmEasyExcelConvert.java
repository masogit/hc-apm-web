package com.ge.apm.service.asset;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import webapp.framework.web.WebUtil;

import java.util.Map;
import java.util.Objects;

public interface ApmEasyExcelConvert {

    Integer ERROR_MSG_INDEX = 999;

    Integer DELETE_FIELD_INDEX = 998;

    /**
     * 数据格式错误处理
     *
     * @param context
     * @param waringMsg
     */
    default void dataFormatHandel(ReadConverterContext<?> context, String waringMsg) {
        Map<Integer, Cell> cellMap = context.getAnalysisContext().readRowHolder().getCellMap();
        Cell errorCell = cellMap.get(ERROR_MSG_INDEX);
        ExcelProperty annotation = context.getContentProperty().getField().getAnnotation(ExcelProperty.class);
        String errorMsg;
        if (Objects.nonNull(annotation)) {
            errorMsg = WebUtil.getMessage(annotation.value()[0]) + waringMsg + ";";
        } else {
            errorMsg = context.getContentProperty().getField().getName() + waringMsg + ";";
        }
        if (Objects.isNull(errorCell)) {
            ReadCellData<String> newErrorCell = new ReadCellData<>();
            newErrorCell.setColumnIndex(ERROR_MSG_INDEX);
            newErrorCell.setStringValue(errorMsg);
            cellMap.put(ERROR_MSG_INDEX, newErrorCell);
            context.getAnalysisContext().readRowHolder().setCellMap(cellMap);
        } else {
            if (errorCell instanceof ReadCellData) {
                ReadCellData readCellData = (ReadCellData) errorCell;
                String existErrorMsg = readCellData.getStringValue();
                readCellData.setStringValue(existErrorMsg + errorMsg);
            }
        }
    }

    /**
     * 通过#清空字段值
     *
     * @param context
     * @param waringMsg
     */
    default void deleteMarkHandel(ReadConverterContext<?> context,String... specialFieldName) {
        Map<Integer, Cell> cellMap = context.getAnalysisContext().readRowHolder().getCellMap();
        Cell deletedCell = cellMap.get(DELETE_FIELD_INDEX);
        String fieldName;
        if(Objects.nonNull(specialFieldName)&&specialFieldName.length>0){
            fieldName=specialFieldName[0];
        }else{
            fieldName= context.getContentProperty().getField().getName();
        }
        if (Objects.isNull(deletedCell)) {
            ReadCellData<String> newErrorCell = new ReadCellData<>();
            newErrorCell.setColumnIndex(DELETE_FIELD_INDEX);
            newErrorCell.setStringValue(fieldName + ";");
            cellMap.put(DELETE_FIELD_INDEX, newErrorCell);
        } else {
            if (deletedCell instanceof ReadCellData) {
                ReadCellData readCellData = (ReadCellData) deletedCell;
                String existErrorMsg = readCellData.getStringValue();
                readCellData.setStringValue(existErrorMsg + fieldName + ";");
            }
        }
    }
}
