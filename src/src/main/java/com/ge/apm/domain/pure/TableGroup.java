/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 212579464
 */
public class TableGroup {
    private String title;
    private List<TableItem> items =new ArrayList();;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TableItem> getItems() {
        return items;
    }

    public void setItems(List<TableItem> items) {
        this.items = items;
    }

}