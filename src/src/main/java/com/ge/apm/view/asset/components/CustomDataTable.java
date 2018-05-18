/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset.components;

import java.util.ArrayList;
import java.util.List;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author 212579464
 */
public class CustomDataTable extends DataTable {

    List<String> customedColumn;

    private List<UIColumn> CustomColumnsList = new ArrayList();
    @Override
    public List<UIColumn> getColumns() {
        if(CustomColumnsList.isEmpty()){
//        List<UIColumn> resultList = new ArrayList();
        List<UIColumn> sourceList = super.getColumns();
        CustomColumnsList.add(sourceList.stream().filter(columItem -> "checkBocColumn".equals(((Column)columItem).getId())).findAny().get());
        CustomColumnsList.add(sourceList.stream().filter(columItem -> "operatorColumn".equals(((Column)columItem).getId())).findAny().get());
        customedColumn.forEach(item -> {
            UIColumn col = sourceList.stream().filter(columItem-> columItem instanceof CustomColumns).filter(sourceItem -> ((CustomColumns) sourceItem).getFieldKey().equalsIgnoreCase(item)).findAny().orElse(null);
            if (null != col) {
                CustomColumnsList.add(col);
            }
            
        });
        CustomColumnsList.add(sourceList.stream().filter(columItem -> "usageStatus".equals(((Column)columItem).getId())).findAny().get());
        }
        return CustomColumnsList;

    }

    public List<String> getCustomedColumn() {
        return customedColumn;
    }

    public void setCustomedColumn(List<String> customedColumn) {
        this.customedColumn = customedColumn;
    }
    
}
