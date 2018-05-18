/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset.components;

import java.util.ArrayList;
import java.util.List;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author 212579464
 */
public class CustomDataTable extends DataTable {

    List<String> customedColumn;

    @Override
    public List<UIColumn> getColumns() {
        List<UIColumn> resultList = new ArrayList();
        List<UIColumn> sourceList = super.getColumns();
        resultList.add(sourceList.stream().filter(columItem -> "checkBocColumn".equals(columItem.getClientId())).findAny().get());
        resultList.add(sourceList.stream().filter(columItem -> "operatorColumn".equals(columItem.getClientId())).findAny().get());
        customedColumn.forEach(item -> {
            UIColumn col = sourceList.stream().filter(columItem-> columItem instanceof CustomColumns).filter(sourceItem -> ((CustomColumns) sourceItem).getColumnKey().equalsIgnoreCase(item)).findAny().orElse(null);
            if (null != col) {
                resultList.add(col);
            }
        });
        resultList.add(sourceList.stream().filter(columItem -> "usageStatus".equals(columItem.getClientId())).findAny().get());
        
        return resultList;

    }

    public List<String> getCustomedColumn() {
        return customedColumn;
    }

    public void setCustomedColumn(List<String> customedColumn) {
        this.customedColumn = customedColumn;
    }
    
}
